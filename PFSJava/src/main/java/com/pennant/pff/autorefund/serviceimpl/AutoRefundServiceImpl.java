package com.pennant.pff.autorefund.serviceimpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.FinOverDueService;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.AutoRefundDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
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
import com.pennant.backend.service.finance.impl.ManualAdviceUtil;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.autorefund.service.AutoRefundService;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.autorefund.RefundBeneficiary;

public class AutoRefundServiceImpl implements AutoRefundService {
	private static final Logger logger = LogManager.getLogger(AutoRefundServiceImpl.class);

	private AutoRefundDAO autoRefundDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private RuleDAO ruleDAO;
	private CustomerDAO customerDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinOverDueService finOverDueService;
	private PaymentHeaderService paymentHeaderService;
	private RefundBeneficiary refundBeneficiary;

	@Override
	public void loadAutoRefund(CustEODEvent cee) {
		logger.debug(Literal.ENTERING);
		EventProperties ep = cee.getEventProperties();

		Date appDate = cee.getEodDate();

		cee.getAutoRefundLoans().addAll(autoRefundDAO.getAutoRefunds(cee));

		for (AutoRefundLoan arl : cee.getAutoRefundLoans()) {
			long finID = arl.getFinID();

			int autoRefundDaysForActive = ep.getAutoRefundDaysForActive();
			int autoRefundDaysForClosed = ep.getAutoRefundDaysForClosed();

			arl.setAutoRefCheckDPD(ep.getAutoRefundCheckDPD());
			arl.setOverDueReq(ep.isAutoRefundOverdueCheck());
			arl.setActiveNDate(DateUtil.addDays(appDate, -autoRefundDaysForActive));
			arl.setClosedNDate(DateUtil.addDays(appDate, -autoRefundDaysForClosed));
			arl.setAlwRefundByCheque(ep.isAutoRefundByCheque());

			arl.setAppDate(appDate);

			Date maxValueDate = null;
			if (arl.isFinIsActive()) {
				maxValueDate = arl.getActiveNDate();
			} else {
				maxValueDate = arl.getClosedNDate();
			}

			/* Fetch Excess List against Reference and Before Requested Value Date */
			arl.getExcessList().addAll(finExcessAmountDAO.getExcessRcdList(finID, maxValueDate));

			/* Fetch Payable List against Reference and Before Requested Value Date */
			arl.getPayableList().addAll(manualAdviseDAO.getPayableAdviseList(finID, maxValueDate));

			arl.getReceivableList().addAll(manualAdviseDAO.getReceivableAdvises(finID));

			if (arl.getExcessList().isEmpty() && arl.getPayableList().isEmpty()) {
				continue;
			}

			arl.setPaymentInstruction(refundBeneficiary.getBeneficiary(finID, appDate, arl.isAlwRefundByCheque()));
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void executeRefund(CustEODEvent cee) {
		logger.debug(Literal.ENTERING);
		for (AutoRefundLoan arl : cee.getAutoRefundLoans()) {

			/* Overdue Amount calculation */
			if (arl.isOverDueReq()) {
				calculateOverDueAmount(arl, cee);
			}

			executeRefund(arl);
		}
		logger.debug(Literal.LEAVING);
	}

	private void executeRefund(AutoRefundLoan arl) {
		logger.debug(Literal.ENTERING);

		long finID = arl.getFinID();
		String finReference = arl.getFinReference();
		Date appDate = arl.getAppDate();

		/* Overdue Amount verification Check required or not */
		BigDecimal overDueAmount = arl.getOverDueAmount();

		BigDecimal reserveAmount = findReserveAmountForAutoRefund(finID, overDueAmount, appDate);

		if (reserveAmount.compareTo(BigDecimal.ZERO) > 0) {
			overDueAmount = overDueAmount.add(reserveAmount);
		}

		logger.debug("Auto Refund Process initiating for FinReference {}", finReference);

		BigDecimal excessBalance = getExcessBalance(arl.getExcessList(), arl.getPayableList());

		if (excessBalance.compareTo(BigDecimal.ZERO) == 0) {
			logger.debug("There is no excess amount for the FinReference {} to refund.", finReference);
			logger.debug(Literal.LEAVING);
			return;
		}

		if (excessBalance.compareTo(overDueAmount) <= 0) {
			setError(arl, "REFUND_006", CurrencyUtil.format(excessBalance), CurrencyUtil.format(overDueAmount));
			logger.debug(Literal.LEAVING);
			return;
		}

		List<PaymentDetail> list = arl.getPaymentDetails();

		for (FinExcessAmount excess : arl.getExcessList()) {
			BigDecimal balanceAmt = excess.getBalanceAmt();
			if (overDueAmount.compareTo(balanceAmt) > 0) {
				overDueAmount = overDueAmount.subtract(balanceAmt);
			} else {
				String amountType = excess.getAmountType();
				long excessID = excess.getExcessID();
				BigDecimal paymentAmt = balanceAmt.subtract(overDueAmount);

				if (paymentAmt.compareTo(BigDecimal.ZERO) > 0) {
					list.add(preparePD(amountType, excessID, null, null, paymentAmt));
				}

				overDueAmount = BigDecimal.ZERO;
			}
		}

		for (ManualAdvise adv : arl.getPayableList()) {
			ManualAdviceUtil.calculateBalanceAmt(adv);
			BigDecimal balanceAmt = adv.getBalanceAmt();

			if (overDueAmount.compareTo(balanceAmt) > 0) {
				overDueAmount = overDueAmount.subtract(balanceAmt);
			} else {
				BigDecimal paymentAmt = balanceAmt.subtract(overDueAmount);

				if (paymentAmt.compareTo(BigDecimal.ZERO) > 0) {
					list.add(preparePD(String.valueOf(AdviseType.PAYABLE.id()), adv.getAdviseID(), adv.getFeeTypeCode(), adv.getFeeTypeDesc(), paymentAmt));
				}

				overDueAmount = BigDecimal.ZERO;
			}

		}

		arl.setRefundAmt(excessBalance.subtract(arl.getOverDueAmount()));

		BigDecimal refundAmt = arl.getRefundAmt();
		BigDecimal maxRefundAmt = arl.getMaxRefundAmt();
		BigDecimal minRefundAmt = arl.getMinRefundAmt();

		ErrorDetail error = paymentHeaderService.validateRefund(arl, true);

		if (error != null) {
			setError(arl, error);
			logger.debug(Literal.LEAVING);
			return;
		}

		if (refundAmt.compareTo(maxRefundAmt) > 0) {
			setError(arl, "REFUND_004", CurrencyUtil.format(refundAmt), CurrencyUtil.format(maxRefundAmt));
			logger.debug(Literal.LEAVING);
			return;
		}

		if (refundAmt.compareTo(minRefundAmt) < 0) {
			setError(arl, "REFUND_005", CurrencyUtil.format(refundAmt), CurrencyUtil.format(minRefundAmt));
			logger.debug(Literal.LEAVING);
			return;
		}

		PaymentInstruction paymentInstruction = arl.getPaymentInstruction();

		if (paymentInstruction == null) {
			setError(arl, "REFUND_007");
			logger.debug(Literal.LEAVING);
			return;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateRefunds(CustEODEvent cee) {
		logger.debug(Literal.ENTERING);

		for (AutoRefundLoan arl : cee.getAutoRefundLoans()) {
			BigDecimal refundAmt = arl.getRefundAmt();

			if (arl.getError().getCode() == null && refundAmt.compareTo(BigDecimal.ZERO) > 0) {
				doProcessPayment(arl);
			}

			if (arl.getError().getCode() != null) {
				logRefund(arl);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	private void logRefund(AutoRefundLoan arl) {
		long id = autoRefundDAO.logRefund(arl);

		List<PaymentDetail> pdList = arl.getPaymentDetails();

		pdList.forEach(pd -> pd.setAutoRefundID(id));

		List<PaymentDetail> list = new ArrayList<>();

		pdList.forEach(pd -> {
			if (pd.getPaymentId() > 0) {
				list.add(pd);
			}
		});

		if (!list.isEmpty()) {
			autoRefundDAO.logPaymentDetails(list);
		}

	}

	private void doProcessPayment(AutoRefundLoan arl) {
		logger.debug(Literal.ENTERING);

		PaymentHeader paymentHeader = paymentHeaderService.prepareRefund(arl);
		AuditHeader auditHeader = getAuditHeader(paymentHeader, PennantConstants.TRAN_WF);

		try {
			paymentHeaderService.doApprove(auditHeader);
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}

		logger.debug(Literal.LEAVING);

		if (auditHeader.getErrorMessage() != null) {
			ErrorDetail errorDetail = auditHeader.getErrorMessage().get(0);
			arl.setError(errorDetail);

			logger.error(errorDetail.getCode());
		}

		ErrorDetail error = arl.getError();
		if (error != null && error.getMessage() != null) {
			setError(arl, error);
			logger.debug(Literal.LEAVING);
			return;
		}

		arl.setError(ErrorUtil.getErrorDetail(new ErrorDetail("REFUND_000")));
		arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
		arl.setStatus("S");

		logger.debug(Literal.LEAVING);
	}

	private void calculateOverDueAmount(AutoRefundLoan arl, CustEODEvent cee) {
		FinEODEvent finEodEvent = null;
		for (FinEODEvent item : cee.getFinEODEvents()) {
			if (item.getFinanceMain().getFinID() == arl.getFinID()) {
				finEodEvent = item;
			}
		}

		if (finEodEvent == null) {
			return;
		}

		BigDecimal overDueAmount = finOverDueService.getOveDueAmount(finEodEvent.getFinProfitDetail(),
				finEodEvent.getFinODDetails(), arl.getReceivableList());

		arl.setOverDueAmount(overDueAmount);

	}

	private PaymentDetail preparePD(String amountType, long refundAgainst, String feeTypeCode, String feeTypeDesc,
			BigDecimal amount) {
		logger.debug(Literal.ENTERING);

		PaymentDetail pd = new PaymentDetail();
		pd.setReferenceId(refundAgainst);
		pd.setAmount(amount);
		pd.setAmountType(amountType);

		if (StringUtils.isNotBlank(feeTypeCode)) {
			pd.setFeeTypeCode(feeTypeCode);
			pd.setFeeTypeDesc(feeTypeDesc);
		}

		pd.setFinSource(UploadConstants.FINSOURCE_ID_AUTOPROCESS);

		logger.debug(Literal.LEAVING);
		return pd;
	}

	private BigDecimal getExcessBalance(List<FinExcessAmount> excessList, List<ManualAdvise> advList) {
		BigDecimal balanceAmount = BigDecimal.ZERO;

		for (FinExcessAmount excess : excessList) {
			balanceAmount = balanceAmount.add(excess.getBalanceAmt());
		}

		for (ManualAdvise ma : advList) {
			ManualAdviceUtil.calculateBalanceAmt(ma);
			balanceAmount = balanceAmount.add(ma.getBalanceAmt());
		}

		return balanceAmount;
	}

	private void setError(AutoRefundLoan arl, String code) {
		arl.setError(ErrorUtil.getError(code));
		arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
		arl.setStatus("F");
	}

	private void setError(AutoRefundLoan arl, String code, String... args) {
		arl.setError(ErrorUtil.getError(code, args));
		arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
		arl.setStatus("F");
	}

	private void setError(AutoRefundLoan arl, ErrorDetail error) {
		arl.setError(error);
		arl.setExecutionTime(new Timestamp(System.currentTimeMillis()));
		arl.setStatus("F");
	}

	private BigDecimal findReserveAmountForAutoRefund(long finID, BigDecimal overDueAmt, Date appDate) {
		logger.debug(Literal.ENTERING);

		BigDecimal feeResult = BigDecimal.ZERO;

		if (overDueAmt == null) {
			overDueAmt = finOverDueService.getDueAgnistCustomer(finID);
		}

		if (overDueAmt == null) {
			overDueAmt = BigDecimal.ZERO;
		}

		List<Rule> rules = ruleDAO.getRuleByModuleAndEvent(RuleConstants.MODULE_AUTOREFUND,
				RuleConstants.EVENT_AUTOTREFUND, "");

		if (CollectionUtils.isNotEmpty(rules)) {
			FinanceProfitDetail fpd = profitDetailsDAO.getFinProfitDetailsById(finID);
			Customer customer = customerDAO.getCustomerForAutoRefund(fpd.getCustId());
			FinODDetails od = finODDetailsDAO.getFinODSummary(finID);

			if (od == null) {
				od = new FinODDetails();
			}

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

			BigDecimal nextEMIAmount = BigDecimal.ZERO;
			BigDecimal next2EMIAmount = BigDecimal.ZERO;

			FinanceScheduleDetail nextSchd = financeScheduleDetailDAO.getNextSchd(finID, appDate, true);

			if (nextSchd != null) {
				nextEMIAmount = nextSchd.getRepayAmount();

				FinanceScheduleDetail next2Schd = financeScheduleDetailDAO.getNextSchd(finID, nextSchd.getSchDate(),
						false);

				if (next2Schd != null) {
					next2EMIAmount = nextEMIAmount.add(next2Schd.getRepayAmount());
				}
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

	private AuditHeader getAuditHeader(PaymentHeader ph, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, ph.getBefImage(), ph);
		return new AuditHeader(String.valueOf(ph.getPaymentId()), String.valueOf(ph.getPaymentId()), null, null,
				auditDetail, ph.getUserDetails(), new HashMap<>());
	}

	@Autowired
	public void setAutoRefundDAO(AutoRefundDAO autoRefundDAO) {
		this.autoRefundDAO = autoRefundDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinOverDueService(FinOverDueService finOverDueService) {
		this.finOverDueService = finOverDueService;
	}

	@Autowired
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	@Autowired
	public void setRefundBeneficiary(RefundBeneficiary refundBeneficiary) {
		this.refundBeneficiary = refundBeneficiary;
	}

}
