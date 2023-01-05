package com.pennant.pff.autorefund.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.AutoRefundDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.autorefund.service.AutoRefundService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoRefundServiceImpl implements AutoRefundService {
	private static final Logger logger = LogManager.getLogger(AutoRefundServiceImpl.class);
	private FinanceMainDAO financeMainDAO;
	private AutoRefundDAO autoRefundDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private RuleDAO ruleDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private CustomerDAO customerDAO;
	private MandateDAO mandateDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private PaymentHeaderService paymentHeaderService;

	/**
	 * @param Appdate Fetching Loans for the Auto Refund Process
	 * 
	 * @return ListofLoans
	 */
	@Override
	public List<AutoRefundLoan> autoRefundsLoanProcess(Date appDate) {
		logger.debug(Literal.ENTERING);
		List<AutoRefundLoan> finList = financeMainDAO.getAutoRefundsLoanList();
		logger.debug(Literal.LEAVING);
		return finList;
	}

	/**
	 * @param RefundLoan Validating loan DPD and In Process Receipts
	 * 
	 * @return list
	 */
	@Override
	public List<ErrorDetail> verifyRefundInitiation(AutoRefundLoan refundLoan, int autoRefCheckDPD) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();

		// DPD Days validation against System parameter Configuration
		if (refundLoan.getDpdDays() > autoRefCheckDPD) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND001", null)));
			logger.debug(Literal.LEAVING);
			return errors;
		}

		// Verification against Receipts , if any of the Cancelled Receipt in Process queue
		if (finReceiptHeaderDAO.isCancelReceiptInQueue(refundLoan.getFinID())) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND002", null)));
			logger.debug(Literal.LEAVING);
			return errors;
		}

		// Verification against Refunds , if any of the refund against loan in process
		if (paymentHeaderDAO.isRefundInQueue(refundLoan.getFinID())) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND003", null)));
			logger.debug(Literal.LEAVING);
			return errors;
		}

		// Verifying if the loan is write off or not
		if (FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(refundLoan.getClosingStatus())) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND010", null)));
			logger.debug(Literal.LEAVING);
			return errors;
		}
		if (FinanceConstants.FIN_HOLDSTATUS_HOLD.equals(refundLoan.getHoldStatus())) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND011", null)));
			logger.debug(Literal.LEAVING);
			return errors;
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	/**
	 * Fetching all the excess amount list on FinId
	 * 
	 * @param finID and Date
	 * @return ExcessList
	 */
	@Override
	public List<FinExcessAmount> getExcessRcdList(long finID, Date maxValueDate) {
		return finExcessAmountDAO.getExcessRcdList(finID, maxValueDate);
	}

	/**
	 * Fetching all the Payable list on FinId
	 * 
	 * @param finID and Date
	 * @return PayableList
	 */
	@Override
	public List<ManualAdvise> getPayableAdviseList(long finID, Date maxValueDate) {
		return manualAdviseDAO.getPayableAdviseList(finID, maxValueDate);
	}

	/**
	 * Fetching total overdue amount of the Loan on FinID
	 * 
	 * @param finID
	 * @return OverdueAmount
	 */
	@Override
	public BigDecimal getOverDueAmountByLoan(long finID) {
		return profitDetailsDAO.getOverDueAmountByLoan(finID);
	}

	/**
	 * Fetching rule amount of the Loan on FinID
	 * 
	 * @param finID
	 * @return Amount
	 */
	@Override
	public BigDecimal findReserveAmountForAutoRefund(long finID, BigDecimal overDueAmt) {
		logger.debug(Literal.ENTERING);
		BigDecimal feeResult = BigDecimal.ZERO;

		if (overDueAmt == null) {
			overDueAmt = getOverDueAmountByLoan(finID);
		}

		// FinPftDetails for rule mapping
		FinanceProfitDetail finPftDetails = profitDetailsDAO.getFinProfitDetailsById(finID);

		// customer details for rule mapping
		Customer customer = customerDAO.getCustomerForAutoRefund(finPftDetails.getCustId());

		// Fetch all the rules againt the Module Name and Event Code
		List<Rule> rules = ruleDAO.getRuleByModuleAndEvent(RuleConstants.MODULE_AUTOREFUND,
				RuleConstants.EVENT_AUTOTREFUND, "");
		if (CollectionUtils.isNotEmpty(rules)) {
			Map<String, Object> executionMap = new HashMap<String, Object>();
			executionMap.put("CustCtgCode", customer.getCustCtgCode());
			executionMap.put("CustTypeCode", customer.getCustTypeCode());
			executionMap.put("FinDivision", finPftDetails.getFinBranch());
			executionMap.put("FinType", finPftDetails.getFinType());
			executionMap.put("FinProduct", finPftDetails.getProductCategory());
			executionMap.put("FinProduct", finPftDetails.getProductCategory());
			executionMap.put("NumberOfTerms", finPftDetails.getNOInst());
			executionMap.put("Finstatus", finPftDetails.getFinIsActive());
			executionMap.put("NoofFutureTerms", finPftDetails.getNOInst() - finPftDetails.getNOPaidInst());
			executionMap.put("FutInstAmt", finPftDetails.getTotalPriBal());
			executionMap.put("Fin_ODDays", finPftDetails.getCurODDays());
			executionMap.put("Fin_CurODamt", overDueAmt);
			feeResult = RuleExecutionUtil.getRuleResult(rules.get(0).getsQLRule(), executionMap,
					finPftDetails.getFinCcy());
		}
		logger.debug(Literal.LEAVING);
		return feeResult;
	}

	/**
	 * Validating refund amount is less or grater on Loan type amounts
	 * 
	 * @param RefundAmt , Loan
	 * @return List
	 */

	@Override
	public List<ErrorDetail> validateRefundAmt(BigDecimal refundAmt, AutoRefundLoan refundLoan) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();

		if (refundAmt.compareTo(refundLoan.getMaxRefundAmt()) > 0) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND004", null)));
			logger.debug(Literal.LEAVING);
			return errors;
		} else if (refundAmt.compareTo(refundLoan.getMinRefundAmt()) < 0) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND005", null)));
			logger.debug(Literal.LEAVING);
			return errors;
		}
		logger.debug(Literal.LEAVING);
		return errors;
	}

	/**
	 * Prepare Payment details and Approve
	 * 
	 * @param refundLoan ,paymentInst,refundAmt,appDate
	 * @return OverdueAmount
	 */

	@Override
	public List<ErrorDetail> executeAutoRefund(AutoRefundLoan refundLoan, List<PaymentDetail> payDtlList,
			PaymentInstruction paymentInst, Date appDate) {
		logger.debug(Literal.ENTERING);
		PaymentHeader paymentHeader = paymentHeaderService.prepareRefund(refundLoan, payDtlList, paymentInst, appDate);
		AuditHeader auditHeader = getAuditHeader(paymentHeader, PennantConstants.TRAN_WF);
		try {
			paymentHeaderService.doApprove(auditHeader);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger.debug(Literal.LEAVING);
		if (auditHeader.getErrorMessage() != null) {
			logger.debug(auditHeader.getErrorMessage().get(0).getCode());
			logger.debug(Literal.LEAVING);
			return auditHeader.getErrorMessage();
		} else {
			logger.debug(Literal.LEAVING);
			return null;
		}
	}

	@Override
	public void saveRefundlist(List<AutoRefundLoan> finalRefundList) {
		autoRefundDAO.saveRefundlist(finalRefundList);
	}

	/**
	 * Method for preparation of Audit header for Approval process of Payment Header Details
	 * 
	 * @param paymentHeader
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(PaymentHeader paymentHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, paymentHeader.getBefImage(), paymentHeader);
		return new AuditHeader(String.valueOf(paymentHeader.getPaymentId()),
				String.valueOf(paymentHeader.getPaymentId()), null, null, auditDetail, paymentHeader.getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return presentmentDetailDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setAutoRefundDAO(AutoRefundDAO autoRefundDAO) {
		this.autoRefundDAO = autoRefundDAO;
	}

}
