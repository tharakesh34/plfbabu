package com.pennanttech.pff.advancepayment.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.ServiceHelper;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.advancepayment.model.AdvancePayment;
import com.pennanttech.pff.core.TableType;

public class AdvancePaymentService extends ServiceHelper {
	private static final long serialVersionUID = 1442146139821584760L;
	private Logger logger = Logger.getLogger(AdvancePaymentService.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private ReceiptCalculator receiptCalculator;
	private FinFeeDetailDAO finFeeDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;

	public void processAdvansePayments(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		AdvancePayment advancePayment = null;

		FinanceMain fm = null;
		for (FinEODEvent finEODEvent : finEODEvents) {

			fm = finEODEvent.getFinanceMain();

			int idx = finEODEvent.getIdxDue();
			if (idx == -1) {
				continue;
			}

			FinanceScheduleDetail curSchd = finEODEvent.getFinanceScheduleDetails().get(idx);

			if (fm.getGrcAdvType() == null && fm.getAdvType() == null) {
				continue;
			}

			if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
				continue;
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {
				if (fm.getGrcAdvType() == null) {
					continue;
				}
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
				if (fm.getAdvType() == null) {
					continue;
				}
			}

			advancePayment = processAdvancePayments(finEODEvent, curSchd, custEODEvent.getEodValueDate());
			advancePayment.setProfitDetail(finEODEvent.getFinProfitDetail());
			createReceipt(advancePayment, fm, valueDate);
		}

		logger.debug(Literal.LEAVING);
	}

	public List<ReturnDataSet> processBackDatedAdvansePayments(FinanceDetail financeDetail,
			FinanceProfitDetail profiDetails, Date appDate, String postBranch, boolean isApprove) {
		logger.debug(Literal.ENTERING);
		AdvancePayment advancePayment = null;

		List<ReturnDataSet> datasets = new ArrayList<ReturnDataSet>();

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		CustEODEvent custEODEvent = new CustEODEvent();

		Customer customer = new Customer();
		customer.setCustAppDate(appDate);
		custEODEvent.setCustomer(customer);
		custEODEvent.setEodValueDate(appDate);

		FinEODEvent finEODEvent = new FinEODEvent();
		finEODEvent.setFinanceMain(fm);
		finEODEvent.setFinExcessAmounts(new ArrayList<>());
		finEODEvent.setFinProfitDetail(profiDetails);
		finEODEvent.setFinanceScheduleDetails(financeDetail.getFinScheduleData().getFinanceScheduleDetails());

		if (fm.getFinStartDate().compareTo(DateUtility.getAppDate()) >= 0) {
			return datasets;
		}

		int index = 0;
		for (FinanceScheduleDetail curSchd : finEODEvent.getFinanceScheduleDetails()) {
			if (curSchd.getDefSchdDate().compareTo(DateUtility.getAppDate()) > 0) {
				break;
			}

			index = index + 1;
			if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
				if (fm.isAlwBPI() && StringUtils.equals(FinanceConstants.BPI_DISBURSMENT, fm.getBpiTreatment())) {
					continue;
				}
			}

			if (fm.getGrcAdvType() == null && fm.getAdvType() == null) {
				continue;
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {
				if (fm.getGrcAdvType() == null) {
					continue;
				}
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
				if (fm.getAdvType() == null) {
					continue;
				}
			}

			advancePayment = processAdvancePayments(finEODEvent, curSchd, custEODEvent.getEodValueDate());
			if (isApprove) {
				advancePayment.setProfitDetail(finEODEvent.getFinProfitDetail());
				createReceipt(advancePayment, fm, appDate);
			}

		}

		logger.debug(Literal.LEAVING);
		return finEODEvent.getReturnDataSet();
	}

	public AdvancePayment processAdvancePayments(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd,
			Date valueDate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();
		Date schDate = curSchd.getSchDate();

		List<FinExcessAmount> excessAmounts = finEODEvent.getFinExcessAmounts();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();

		String finEvent = AccountEventConstants.ACCEVENT_REPAY;
		long accountingID = getAccountingID(fm, finEvent);
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(profiDetails, schedules, finEvent, valueDate, schDate);
		aeEvent.getAcSetIDList().add(accountingID);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		BigDecimal profitSchd = curSchd.getProfitSchd();
		BigDecimal schdPftPaid = curSchd.getSchdPftPaid();
		BigDecimal schdIntDue = profitSchd.subtract(schdPftPaid);

		BigDecimal principalSchd = curSchd.getPrincipalSchd();
		BigDecimal schdPriPaid = curSchd.getSchdPriPaid();
		BigDecimal schdPriDue = principalSchd.subtract(schdPriPaid);

		AdvancePayment advancePayment = new AdvancePayment(fm.getGrcAdvType(), fm.getAdvType(),
				fm.getGrcPeriodEndDate());

		advancePayment.setFinReference(fm.getFinReference());
		advancePayment.setFinBranch(fm.getFinBranch());
		advancePayment.setExcessAmounts(excessAmounts);
		advancePayment.setSchdPriDue(schdPriDue);
		advancePayment.setSchdIntDue(schdIntDue);
		advancePayment.setValueDate(valueDate);
		advancePayment.setCurSchd(curSchd);

		AdvancePaymentUtil.calculateDue(advancePayment);

		amountCodes.setIntAdjusted(advancePayment.getIntAdjusted());
		amountCodes.setIntAdvAvailable(advancePayment.getIntAdvAvailable());
		amountCodes.setIntDue(advancePayment.getIntDue());
		amountCodes.setEmiAdjusted(advancePayment.getEmiAdjusted());
		amountCodes.setEmiAdvAvailable(advancePayment.getEmiAdvAvailable());
		amountCodes.setEmiDue(advancePayment.getEmiDue());

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		aeEvent.setDataMap(dataMap);
		aeEvent.setPostDate(valueDate);
		aeEvent.setValueDate(valueDate);
		//Postings Process and save all postings related to finance for one time accounts update

		try {
			aeEvent = postAccountingEOD(aeEvent);
		} catch (Exception e) {
			throw new AppException();
		}

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		logger.debug(Literal.LEAVING);
		return advancePayment;
	}

	private void createReceipt(AdvancePayment advancePayment, FinanceMain fm, Date valueDate) {
		FinanceScheduleDetail curSchd = advancePayment.getCurSchd();

		if (AdvanceType.hasAdvInterest(fm.getGrcAdvType()) || AdvanceType.hasAdvInterest(fm.getAdvType())) {
			createAdvIntReceipt(advancePayment, valueDate, curSchd, AccountConstants.TRANTYPE_DEBIT);

		} else if (AdvanceType.hasAdvEMI(fm.getAdvType())) {
			createAdvEMIReceipt(advancePayment, valueDate, curSchd, AccountConstants.TRANTYPE_DEBIT);
		}
	}

	/**
	 * <p>
	 * Update Excess
	 * </p>
	 * <p>
	 * Insert Excess Movements
	 * </p>
	 * <p>
	 * Create Payable advise in case of profit change is negative else create receivable advise
	 * </p>
	 * 
	 * <p>
	 * The profit change will be compared with the previous collected advance interest/EMI
	 * </p>
	 * 
	 * @param finScheduleData
	 */
	public void excessAmountMovement(FinScheduleData finScheduleData) {
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String finReference = financeMain.getFinReference();
		Date valueDate = DateUtility.getAppValueDate();
		long lastMntBy = financeMain.getLastMntBy();

		AdvanceType grcAdvanceType = AdvanceType.getType(financeMain.getGrcAdvType());
		AdvanceType repayAdvanceType = AdvanceType.getType(financeMain.getAdvType());

		List<FinFeeDetail> fees = finFeeDetailDAO.getPreviousAdvPayments(finReference);
		List<ManualAdvise> manualAdvises = manualAdviseDAO.getPreviousAdvPayments(finReference);

		BigDecimal previousAdvInt = BigDecimal.ZERO;
		BigDecimal previousAdvEmi = BigDecimal.ZERO;

		Map<String, Long> map = new HashMap<>();

		for (ManualAdvise manualAdvise : manualAdvises) {
			if (!AdvanceRuleCode.ADVINT.name().equals(manualAdvise.getFeeTypeCode())) {
				continue;
			}

			if (FinanceConstants.MANUAL_ADVISE_RECEIVABLE == manualAdvise.getAdviseType()) {
				previousAdvInt = previousAdvInt.add(manualAdvise.getAdviseAmount());
			} else {
				previousAdvInt = previousAdvInt.subtract(manualAdvise.getAdviseAmount());
			}
		}

		for (FinFeeDetail fee : fees) {
			if (!AdvanceRuleCode.ADVINT.name().equals(fee.getFeeTypeCode())) {
				continue;
			}

			previousAdvInt = previousAdvInt.add(fee.getCalculatedAmount());
			map.put(fee.getFeeTypeCode(), fee.getFeeTypeID());
		}

		for (ManualAdvise manualAdvise : manualAdvises) {
			if (!AdvanceRuleCode.ADVEMI.name().equals(manualAdvise.getFeeTypeCode())) {
				continue;
			}

			if (FinanceConstants.MANUAL_ADVISE_RECEIVABLE == manualAdvise.getAdviseType()) {
				previousAdvEmi = previousAdvEmi.add(manualAdvise.getAdviseAmount());
			} else {
				previousAdvEmi = previousAdvEmi.subtract(manualAdvise.getAdviseAmount());
			}
		}

		for (FinFeeDetail fee : fees) {
			if (!AdvanceRuleCode.ADVEMI.name().equals(fee.getFeeTypeCode())) {
				continue;
			}

			previousAdvEmi = previousAdvEmi.add(fee.getCalculatedAmount());
			map.put(fee.getFeeTypeCode(), fee.getFeeTypeID());
		}

		BigDecimal advPayment = BigDecimal.ZERO;

		if (grcAdvanceType != null) {
			advPayment = AdvancePaymentUtil.calculateGrcAdvPayment(finScheduleData);
		} else if (repayAdvanceType != null) {
			advPayment = AdvancePaymentUtil.calculateRepayAdvPayment(finScheduleData);
		}

		AdvancePayment advancePayment = new AdvancePayment();
		advancePayment.setFinReference(finReference);

		Long feeTypeId;
		BigDecimal adviseAmount = BigDecimal.ZERO;
		if (previousAdvInt.compareTo(BigDecimal.ZERO) > 0) {
			feeTypeId = map.get(AdvanceRuleCode.ADVINT.name());
			advancePayment.setAdvancePaymentType(AdvanceRuleCode.ADVINT.name());

			if (advPayment.compareTo(previousAdvInt) < 0) {
				adviseAmount = previousAdvInt.subtract(advPayment);
				advancePayment.setRequestedAmt(adviseAmount);
				excessAmountMovement(advancePayment, null, AccountConstants.TRANTYPE_DEBIT);
				createPayableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			} else if (advPayment.compareTo(previousAdvInt) > 0) {
				adviseAmount = previousAdvInt.subtract(advPayment);
				advancePayment.setRequestedAmt(adviseAmount);
				excessAmountMovement(advancePayment, null, AccountConstants.TRANTYPE_CREDIT);
				createReceivableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			}
		} else if (previousAdvEmi.compareTo(BigDecimal.ZERO) > 0) {
			feeTypeId = map.get(AdvanceRuleCode.ADVEMI.name());
			advancePayment.setAdvancePaymentType(AdvanceRuleCode.ADVEMI.name());

			if (advPayment.compareTo(previousAdvInt) < 0) {
				adviseAmount = previousAdvInt.subtract(advPayment);
				advancePayment.setRequestedAmt(adviseAmount);
				excessAmountMovement(advancePayment, null, AccountConstants.TRANTYPE_DEBIT);
				createPayableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			} else if (advPayment.compareTo(previousAdvInt) > 0) {
				adviseAmount = previousAdvInt.subtract(advPayment);
				advancePayment.setRequestedAmt(adviseAmount);
				excessAmountMovement(advancePayment, null, AccountConstants.TRANTYPE_CREDIT);
				createReceivableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			}
		}
	}

	public long excessAmountMovement(AdvancePayment advancePayment, Long receiptID, String txnType) {
		String finReference = advancePayment.getFinReference();
		String adviceType = advancePayment.getAdvancePaymentType();
		AdvanceRuleCode advanceType = AdvanceRuleCode.getRule(adviceType);

		BigDecimal reqAmount = advancePayment.getRequestedAmt();
		FinExcessAmount excess = finExcessAmountDAO.getFinExcessAmount(finReference, adviceType);

		if (reqAmount == null) {
			reqAmount = BigDecimal.ZERO;
		}

		if (excess == null) {
			excess = new FinExcessAmount();
		}

		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal utilisedAmt = excess.getUtilisedAmt();
		BigDecimal reservedAmt = excess.getReservedAmt();

		excess.setFinReference(finReference);
		excess.setAmountType(adviceType);

		if (AccountConstants.TRANTYPE_CREDIT.equals(txnType)) {
			amount = excess.getAmount().add(reqAmount);
		} else {
			amount = excess.getAmount().subtract(reqAmount);
			//utilisedAmt = utilisedAmt.add(reqAmount);
		}

		excess.setFinReference(finReference);
		excess.setAmountType(adviceType);
		excess.setAmount(amount);
		excess.setReservedAmt(reservedAmt);
		excess.setUtilisedAmt(utilisedAmt);
		excess.setBalanceAmt(amount.subtract(utilisedAmt).subtract(reservedAmt));

		if (excess.getExcessID() == Long.MIN_VALUE || excess.getExcessID() == 0) {
			finExcessAmountDAO.saveExcess(excess);
		} else {
			finExcessAmountDAO.updateExcess(excess);
		}

		long excessID = excess.getExcessID();
		FinExcessMovement movement = new FinExcessMovement();
		movement.setExcessID(excess.getExcessID());
		movement.setReceiptID(receiptID);
		movement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
		movement.setTranType(txnType);
		movement.setAmount(reqAmount);
		finExcessAmountDAO.saveExcessMovement(movement);

		if (AccountConstants.TRANTYPE_DEBIT.equals(txnType)) {
			// Data setting to use in receipt creation.
			if (advanceType == AdvanceRuleCode.ADVINT) {
				advancePayment.setIntAdjusted(reqAmount);
			} else if (advanceType == AdvanceRuleCode.ADVEMI) {
				advancePayment.setEmiAdjusted(reqAmount);
			}
		}

		return excessID;
	}

	public long advanceExcessMovement(AdvancePayment advPayment, Long receiptID, String txnType) {
		String finRef = advPayment.getFinReference();
		String adviceType = advPayment.getAdvancePaymentType();
		long excessID = Long.MIN_VALUE;

		FinExcessAmount excess = finExcessAmountDAO.getFinExcessAmount(finRef, adviceType);
		if (excess != null) {
			excessID = excess.getExcessID();
			FinExcessMovement excessAmovement = finExcessAmountDAO.getFinExcessMovement(excessID,
					RepayConstants.PAYTYPE_PRESENTMENT, advPayment.getValueDate());

			if (excessAmovement != null) {
				//reserve exist
				excess.setReservedAmt(excess.getReservedAmt().subtract(excessAmovement.getAmount()));
			}

			BigDecimal intAdjusted = advPayment.getIntAdjusted();
			BigDecimal emiAdjusted = advPayment.getEmiAdjusted();
			//since both can't co-exists
			BigDecimal adjAmount = intAdjusted.add(emiAdjusted);
			excess.setUtilisedAmt(excess.getUtilisedAmt().add(adjAmount));
			finExcessAmountDAO.updateReserveUtilization(excess);

			//movement
			FinExcessMovement excessMovement = new FinExcessMovement();
			excessMovement.setExcessID(excessID);
			excessMovement.setAmount(adjAmount);
			excessMovement.setReceiptID(receiptID);
			excessMovement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
			excessMovement.setTranType(txnType);
			finExcessAmountDAO.saveExcessMovement(excessMovement);
		}

		return excessID;
	}

	private void createAdvIntReceipt(AdvancePayment advancePayment, Date valueDate, FinanceScheduleDetail curSchd,
			String txnType) {
		String finReference = advancePayment.getFinReference();
		String finBranch = advancePayment.getFinBranch();

		BigDecimal intAdjusted = advancePayment.getIntAdjusted();

		BigDecimal profitSchd = curSchd.getProfitSchd();
		BigDecimal schdPftPaid = curSchd.getSchdPftPaid();
		BigDecimal schdIntDue = profitSchd.subtract(schdPftPaid);
		BigDecimal tDSPaid = curSchd.getTDSPaid();

		BigDecimal payNow = BigDecimal.ZERO;
		BigDecimal tdsPayNow = BigDecimal.ZERO;
		BigDecimal netPay = BigDecimal.ZERO;

		if (curSchd.isTDSApplicable()) {
			tdsPayNow = receiptCalculator.getTDS(schdIntDue);
		}

		netPay = schdIntDue.subtract(tdsPayNow);
		if (intAdjusted.compareTo(netPay) <= 0) {
			netPay = intAdjusted;
		}

		if (curSchd.isTDSApplicable()) {
			tdsPayNow = receiptCalculator.getTDS(netPay);
		}
		payNow = netPay.add(tdsPayNow);

		if (payNow.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		long excessID;
		long receiptID = 0;

		// 1. Receipt Header
		FinReceiptHeader rch = getReceiptHeader(payNow, valueDate, finReference, finBranch, AdvanceRuleCode.ADVINT);
		finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);

		// 2. Receipt Details
		advancePayment.setRequestedAmt(intAdjusted);
		receiptID = rch.getReceiptID();
		excessID = advanceExcessMovement(advancePayment, receiptID, txnType);
		FinReceiptDetail rcd = getReceiptDetail(payNow, valueDate, receiptID, excessID, AdvanceRuleCode.ADVINT);
		finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);

		// 3. Receipt Allocation Details
		List<ReceiptAllocationDetail> allocations = getAdvIntAllocations(receiptID, payNow, tdsPayNow, netPay);
		receiptAllocationDetailDAO.saveAllocations(allocations, TableType.MAIN_TAB);

		// 4. Repay Header		
		FinRepayHeader rph = getRepayHeader(finReference, valueDate, rch, rcd);
		financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

		// 5. Repay Schedule Details 
		List<RepayScheduleDetail> list = getRepayScheduleDetail(curSchd, rph, valueDate, allocations);
		financeRepaymentsDAO.saveRpySchdList(list, TableType.MAIN_TAB);

		// 6. Schedule Details 
		schdPftPaid = schdPftPaid.add(payNow);
		tDSPaid = tDSPaid.add(tdsPayNow);
		curSchd.setSchdPftPaid(schdPftPaid);
		curSchd.setTDSPaid(tDSPaid);

		if (schdPftPaid.equals(payNow)) {
			curSchd.setSchPftPaid(true);
		}
		financeScheduleDetailDAO.updateSchPftPaid(curSchd);

		// 7. Profit Details
		FinanceProfitDetail profitDetail = advancePayment.getProfitDetail();
		profitDetail.setFinReference(finReference);

		BigDecimal totalPftPaid = profitDetail.getTotalPftPaid();
		totalPftPaid = totalPftPaid.add(schdPftPaid);
		profitDetail.setTotalPftPaid(totalPftPaid);

		BigDecimal tdTdsPaid = profitDetail.getTdTdsPaid();
		tdTdsPaid = tdTdsPaid.add(tDSPaid);
		profitDetail.setTdTdsPaid(tdTdsPaid);

		BigDecimal tdTdsBal = profitDetail.getTdTdsBal();
		tdTdsBal = tdTdsBal.add(tDSPaid); //FIXME
		profitDetail.setTdTdsBal(tdTdsBal);

		financeProfitDetailDAO.updateSchPftPaid(profitDetail);

	}

	private void createAdvEMIReceipt(AdvancePayment advancePayment, Date valueDate, FinanceScheduleDetail curSchd,
			String txnType) {
		String finReference = curSchd.getFinReference();
		String finBranch = advancePayment.getFinBranch();

		BigDecimal emiAdjusted = advancePayment.getEmiAdjusted();

		BigDecimal principalSchd = curSchd.getPrincipalSchd();
		BigDecimal schdPriPaid = curSchd.getSchdPriPaid();
		BigDecimal schdPriDue = principalSchd.subtract(schdPriPaid);

		BigDecimal profitSchd = curSchd.getProfitSchd();
		BigDecimal schdPftPaid = curSchd.getSchdPftPaid();
		BigDecimal schdIntDue = profitSchd.subtract(schdPftPaid);

		BigDecimal schdEmiDue = schdPriDue.add(schdPriPaid);

		BigDecimal tDSPaid = curSchd.getTDSPaid();

		BigDecimal payNow = BigDecimal.ZERO;
		BigDecimal tdsPayNow = BigDecimal.ZERO;
		BigDecimal netPay = BigDecimal.ZERO;

		if (curSchd.isTDSApplicable()) {
			tdsPayNow = receiptCalculator.getTDS(schdIntDue);
		}

		netPay = schdIntDue.subtract(tdsPayNow);
		if (emiAdjusted.subtract(schdIntDue).compareTo(netPay) <= 0) {
			netPay = emiAdjusted.subtract(schdIntDue);
		}

		if (curSchd.isTDSApplicable()) {
			tdsPayNow = receiptCalculator.getTDS(netPay);
		}
		payNow = netPay.add(tdsPayNow);

		payNow = payNow.add(schdEmiDue);

		if (payNow.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		long excessID;
		long receiptID = 0;

		// 1. Receipt Header
		FinReceiptHeader rch = getReceiptHeader(payNow, valueDate, finReference, finBranch, AdvanceRuleCode.ADVEMI);
		finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);

		// 2. Receipt Details
		advancePayment.setRequestedAmt(emiAdjusted);
		receiptID = rch.getReceiptID();
		excessID = advanceExcessMovement(advancePayment, receiptID, txnType);
		FinReceiptDetail rcd = getReceiptDetail(payNow, valueDate, receiptID, excessID, AdvanceRuleCode.ADVEMI);
		finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);

		// 3. Receipt Allocation Details
		List<ReceiptAllocationDetail> allocations = getAdvIntAllocations(receiptID, payNow, tdsPayNow, netPay);
		receiptAllocationDetailDAO.saveAllocations(allocations, TableType.MAIN_TAB);

		// 4. Repay Header
		FinRepayHeader rph = getRepayHeader(finReference, valueDate, rch, rcd);
		financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

		// 5. Repay Schedule Details 
		List<RepayScheduleDetail> list = getRepayScheduleDetail(curSchd, rph, valueDate, allocations);
		financeRepaymentsDAO.saveRpySchdList(list, TableType.MAIN_TAB);

		// 6. Schedule Details 
		schdPriPaid = schdPriPaid.add(payNow);
		curSchd.setSchdPriPaid(schdPriPaid);

		if (schdPriPaid.equals(payNow)) {
			curSchd.setSchPriPaid(true);
		}
		financeScheduleDetailDAO.updateSchPriPaid(curSchd);

		// 7. Profit Details
		FinanceProfitDetail profitDetail = advancePayment.getProfitDetail();
		profitDetail.setFinReference(finReference);

		BigDecimal totalPriPaid = profitDetail.getTotalPriPaid();
		totalPriPaid = totalPriPaid.add(schdPriPaid);
		profitDetail.setTotalPriPaid(schdPriPaid);

		BigDecimal tdTdsBal = profitDetail.getTdTdsBal();
		tdTdsBal = tdTdsBal.add(tDSPaid); //FIXME
		profitDetail.setTdTdsBal(tdTdsBal);

		financeProfitDetailDAO.updateSchPriPaid(profitDetail);

	}

	private FinReceiptHeader getReceiptHeader(BigDecimal requestedAmt, Date valueDate, String finReference,
			String finBranch, AdvanceRuleCode adviceType) {
		FinReceiptHeader rch = new FinReceiptHeader();
		rch.setReference(finReference);
		rch.setReceiptDate(valueDate);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		rch.setExcessAdjustTo(PennantConstants.List_Select);
		rch.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		rch.setReceiptAmount(requestedAmt);
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(adviceType.name());
		rch.setSubReceiptMode(adviceType.name());
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		rch.setLogSchInPresentment(false);
		rch.setPostBranch(finBranch);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		return rch;
	}

	private FinReceiptDetail getReceiptDetail(BigDecimal payNow, Date valueDate, long receiptID, long excessID,
			AdvanceRuleCode adviceType) {
		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptID(receiptID);
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(adviceType.name());
		rcd.setPayAgainstID(excessID);
		rcd.setAmount(payNow);
		rcd.setDueAmount(payNow);
		rcd.setValueDate(valueDate);
		rcd.setReceivedDate(valueDate);
		rcd.setPartnerBankAc(null);
		rcd.setPartnerBankAcType(null);
		rcd.setStatus(RepayConstants.PAYSTATUS_APPROVED);
		return rcd;
	}

	private List<ReceiptAllocationDetail> getAdvIntAllocations(long receiptID, BigDecimal payNow, BigDecimal tdsPayNow,
			BigDecimal netPay) {
		List<ReceiptAllocationDetail> list = new ArrayList<>();
		if (payNow.compareTo(BigDecimal.ZERO) == 0) {
			return list;
		}

		int id = 1;
		ReceiptAllocationDetail allocation;
		String desc = Labels.getLabel("label_RecceiptDialog_AllocationType_PFT");
		allocation = receiptCalculator.getAllocation(RepayConstants.ALLOCATION_PFT, id, payNow, desc, 0, "", false);
		allocation.setPaidAmount(payNow);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_TDS");
		allocation = receiptCalculator.getAllocation(RepayConstants.ALLOCATION_TDS, id, tdsPayNow, desc, 0, "", false);
		allocation.setPaidAmount(tdsPayNow);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_NPFT");
		allocation = receiptCalculator.getAllocation(RepayConstants.ALLOCATION_NPFT, id, netPay, desc, 0, "", false);
		allocation.setPaidAmount(netPay);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		return list;
	}

	private FinRepayHeader getRepayHeader(String finReference, Date valueDate, FinReceiptHeader rch,
			FinReceiptDetail rcd) {
		FinRepayHeader rph = new FinRepayHeader();
		rph.setReceiptSeqID(rcd.getReceiptSeqID());
		rph.setFinReference(finReference);
		rph.setValueDate(valueDate);
		rph.setFinEvent(rch.getReceiptPurpose());
		rph.setRepayAmount(rcd.getAmount());
		rph.setExcessAmount(rcd.getAmount());
		return rph;
	}

	private List<RepayScheduleDetail> getRepayScheduleDetail(FinanceScheduleDetail curSchd, FinRepayHeader rph,
			Date valueDate, List<ReceiptAllocationDetail> allocations) {
		List<RepayScheduleDetail> list = new ArrayList<>();

		RepayScheduleDetail rsd = new RepayScheduleDetail();

		rsd.setFinReference(curSchd.getFinReference());
		rsd.setSchDate(curSchd.getSchDate());
		rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		rsd.setProfitSchdBal(curSchd.getProfitSchd());
		rsd.setPrincipalSchdBal(curSchd.getPrincipalSchd());
		rsd.setProfitSchdPayNow(curSchd.getSchdPftPaid());
		rsd.setPrincipalSchdPayNow(curSchd.getSchdPriPaid());

		int daysLate = DateUtility.getDaysBetween(curSchd.getSchDate(), valueDate);
		rsd.setDaysLate(daysLate);

		rsd.setRepayBalance(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
		rsd.setProfitSchd(curSchd.getProfitSchd());
		rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());

		rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
		rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
		rsd.setPenaltyPayNow(BigDecimal.ZERO);

		rsd.setRepaySchID(1);
		rsd.setRepayID(rph.getRepayID());
		rsd.setLinkedTranId(rph.getLinkedTranId());

		for (ReceiptAllocationDetail allocation : allocations) {
			if (RepayConstants.ALLOCATION_PFT.equals(allocation.getAllocationType())) {
				rsd.setProfitSchdPayNow(rsd.getProfitSchdPayNow().add(allocation.getPaidNow()));
				rsd.setPftSchdWaivedNow(rsd.getPftSchdWaivedNow().add(allocation.getWaivedNow()));
			} else if (RepayConstants.ALLOCATION_TDS.equals(allocation.getAllocationType())) {
				rsd.setTdsSchdPayNow(allocation.getPaidNow().add(allocation.getWaivedNow()));
			} else if (RepayConstants.ALLOCATION_NPFT.equals(allocation.getAllocationType())) {
				//rsd.setTdsSchdPayNow(allocation.getPaidNow().add(allocation.getWaivedNow()));
			}
		}

		list.add(rsd);
		return list;
	}

	private void createPayableAdvise(String finReference, Date valueDate, long feeTypeID, BigDecimal adviseAmount,
			long lastMntBy) {
		logger.debug(Literal.ENTERING);

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_PAYABLE);
		manualAdvise.setFinReference(finReference);
		manualAdvise.setFeeTypeID(feeTypeID);
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(adviseAmount);
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setRemarks("Payble Advice for Advance Interest/EMI");
		manualAdvise.setBounceID(0);
		manualAdvise.setReceiptID(0);
		manualAdvise.setValueDate(valueDate);
		manualAdvise.setPostDate(valueDate);
		manualAdvise.setReservedAmt(BigDecimal.ZERO);
		manualAdvise.setBalanceAmt(adviseAmount);

		manualAdvise.setVersion(0);
		manualAdvise.setLastMntBy(lastMntBy);
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setRoleCode("");
		manualAdvise.setNextRoleCode("");
		manualAdvise.setTaskId("");
		manualAdvise.setNextTaskId("");
		manualAdvise.setRecordType("");
		manualAdvise.setWorkflowId(0);

		manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creating a Receivable advise for insurance Cancel or surrender amount.
	 * 
	 * @param vASRecording
	 */
	private void createReceivableAdvise(String finReference, Date valueDate, long feeTypeID, BigDecimal adviseAmount,
			Long lastMntBy) {
		logger.debug(Literal.ENTERING);

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
		manualAdvise.setFinReference(finReference);
		manualAdvise.setFeeTypeID(feeTypeID);
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(adviseAmount);
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setRemarks("Receivable Advice for Advance Interest/EMI");
		manualAdvise.setBounceID(0);
		manualAdvise.setReceiptID(0);
		manualAdvise.setValueDate(valueDate);
		manualAdvise.setPostDate(valueDate);
		manualAdvise.setReservedAmt(BigDecimal.ZERO);
		manualAdvise.setBalanceAmt(adviseAmount);

		manualAdvise.setVersion(0);
		if (lastMntBy != null) {
			manualAdvise.setLastMntBy(lastMntBy);
		}
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setRoleCode("");
		manualAdvise.setNextRoleCode("");
		manualAdvise.setTaskId("");
		manualAdvise.setNextTaskId("");
		manualAdvise.setRecordType("");
		manualAdvise.setWorkflowId(0);

		manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}
}
