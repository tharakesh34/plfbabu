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
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.AutoRefundDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.autorefund.service.AutoRefundService;
import com.pennanttech.pennapps.core.AppException;
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
	private CustomerDAO customerDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private PaymentHeaderService paymentHeaderService;

	@Override
	public List<AutoRefundLoan> getAutoRefunds() {
		logger.debug(Literal.ENTERING);
		List<AutoRefundLoan> finList = financeMainDAO.getAutoRefunds();
		logger.debug(Literal.LEAVING);
		return finList;
	}

	/**
	 * @param RefundLoan Validating loan DPD and In Process Receipts
	 * 
	 * @return list
	 */
	@Override
	public List<ErrorDetail> verifyRefundInitiation(AutoRefundLoan arl, boolean isEOD) {
		return paymentHeaderService.verifyRefundInitiation(arl, isEOD);
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
	public BigDecimal getOverDueAmount(long finID) {
		BigDecimal overDueAmount = BigDecimal.ZERO;

		overDueAmount = overDueAmount.add(profitDetailsDAO.getOverDueAmount(finID));
		overDueAmount = overDueAmount.add(finODDetailsDAO.getOverDueAmount(finID));
		overDueAmount = overDueAmount.add(manualAdviseDAO.getOverDueAmount(finID));

		return overDueAmount;
	}

	@Override
	public BigDecimal findReserveAmountForAutoRefund(long finID, BigDecimal overDueAmt, Date appDate) {
		logger.debug(Literal.ENTERING);

		BigDecimal feeResult = BigDecimal.ZERO;

		if (overDueAmt == null) {
			overDueAmt = getOverDueAmount(finID);
		}

		FinanceProfitDetail fpd = profitDetailsDAO.getFinProfitDetailsById(finID);
		FinODDetails od = finODDetailsDAO.getFinODSummary(finID);

		Customer customer = customerDAO.getCustomerForAutoRefund(fpd.getCustId());

		List<Rule> rules = ruleDAO.getRuleByModuleAndEvent(RuleConstants.MODULE_AUTOREFUND,
				RuleConstants.EVENT_AUTOTREFUND, "");

		if (CollectionUtils.isNotEmpty(rules)) {
			Map<String, Object> executionMap = new HashMap<>();

			executionMap.put("CustCtgCode", customer.getCustCtgCode());
			executionMap.put("CustTypeCode", customer.getCustTypeCode());
			executionMap.put("FinDivision", fpd.getFinBranch());
			executionMap.put("FinType", fpd.getFinType());
			executionMap.put("FinProduct", fpd.getProductCategory());
			executionMap.put("NumberOfTerms", fpd.getNOInst());
			executionMap.put("Finstatus", fpd.getFinIsActive());
			executionMap.put("NoofFutureTerms", fpd.getNOInst() - fpd.getNOPaidInst());
			executionMap.put("FutInstAmt", fpd.getTotalPriBal());
			executionMap.put("Fin_ODDays", fpd.getCurODDays());
			executionMap.put("Fin_CurODamt", overDueAmt);

			FinanceScheduleDetail nextSchd = financeScheduleDetailDAO.getNextSchd(finID, appDate, true);
			FinanceScheduleDetail next2Schd = financeScheduleDetailDAO.getNextSchd(finID, nextSchd.getSchDate(), false);

			BigDecimal nextEMIAmount = nextSchd.getRepayAmount();
			BigDecimal next2EMIAmount = BigDecimal.ZERO;

			if (next2Schd != null) {
				next2EMIAmount = nextEMIAmount.add(next2Schd.getRepayAmount());
			}

			executionMap.put("NextEMIAmount", nextEMIAmount);
			executionMap.put("Next2EMIAmount", next2EMIAmount);
			executionMap.put("UnpaidLPP", od.getTotPenaltyBal());
			executionMap.put("OverduePrincipal", fpd.getODPrincipal());
			executionMap.put("OverdueInterest", fpd.getODProfit());

			feeResult = RuleExecutionUtil.getRuleResult(rules.get(0).getsQLRule(), executionMap, fpd.getFinCcy());
		}

		logger.debug(Literal.LEAVING);
		return feeResult;
	}

	@Override
	public List<ErrorDetail> validateRefundAmt(BigDecimal refundAmt, AutoRefundLoan refundLoan) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<>();

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

	@Override
	public List<ErrorDetail> executeAutoRefund(AutoRefundLoan refundLoan, List<PaymentDetail> payDtlList,
			PaymentInstruction paymentInst) {
		logger.debug(Literal.ENTERING);

		PaymentHeader paymentHeader = paymentHeaderService.prepareRefund(refundLoan, payDtlList, paymentInst);
		AuditHeader auditHeader = getAuditHeader(paymentHeader, PennantConstants.TRAN_WF);

		try {
			paymentHeaderService.doApprove(auditHeader);
		} catch (Exception e) {
			throw new AppException(e.getMessage());
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
	public void save(List<AutoRefundLoan> finalRefundList) {
		autoRefundDAO.save(finalRefundList);
	}

	private AuditHeader getAuditHeader(PaymentHeader ph, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, ph.getBefImage(), ph);
		return new AuditHeader(String.valueOf(ph.getPaymentId()), String.valueOf(ph.getPaymentId()), null, null,
				auditDetail, ph.getUserDetails(), new HashMap<>());
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	@Autowired
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Autowired
	public void setAutoRefundDAO(AutoRefundDAO autoRefundDAO) {
		this.autoRefundDAO = autoRefundDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
