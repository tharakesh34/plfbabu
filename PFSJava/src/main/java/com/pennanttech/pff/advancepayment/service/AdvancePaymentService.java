package com.pennanttech.pff.advancepayment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.model.AdvancePayment;
import com.pennanttech.pff.core.TableType;

public class AdvancePaymentService extends ServiceHelper {
	private static final long serialVersionUID = 1442146139821584760L;
	private Logger logger = Logger.getLogger(AdvancePaymentService.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private ReceiptCalculator receiptCalculator;

	public void processAdvansePayments(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		FinanceMain fm = null;
		for (FinEODEvent finEODEvent : finEODEvents) {

			fm = finEODEvent.getFinanceMain();

			long accountingID = getAccountingID(fm, AccountEventConstants.ACCEVENT_REPAY);

			if (accountingID == Long.MIN_VALUE) {
				continue;
			}

			int idx = finEODEvent.getIdxDue();
			if (idx == -1) {
				continue;
			}

			FinanceScheduleDetail curSchd = finEODEvent.getFinanceScheduleDetails().get(idx);

			if (fm.getGrcAdvType() == null && fm.getAdvType() == null) {
				continue;
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {

				if (fm.getGrcAdvType() == null) {
					continue;
				}

				// ADVINST
				processAdvancePayments(finEODEvent, curSchd, custEODEvent, accountingID);
			}

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
				if (fm.getAdvType() == null) {
					continue;
				}
				//ADVEMI
				processAdvancePayments(finEODEvent, curSchd, custEODEvent, accountingID);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	public void processAdvancePayments(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd,
			CustEODEvent custEODEvent, long accountingID) throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();
		Date valueDate = custEODEvent.getEodValueDate();
		Date schDate = curSchd.getSchDate();

		List<FinExcessAmount> excessAmounts = finEODEvent.getFinExcessAmounts();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();

		String finEvent = AccountEventConstants.ACCEVENT_REPAY;
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

		AdvancePaymentUtil.calculateDue(advancePayment);

		amountCodes.setIntAdjusted(advancePayment.getIntAdjusted());
		amountCodes.setIntAdvAvailable(advancePayment.getIntAdvAvailable());
		amountCodes.setIntDue(advancePayment.getIntDue());
		amountCodes.setEmiAdjusted(advancePayment.getEmiAdjusted());
		amountCodes.setEmiAdvAvailable(advancePayment.getEmiAdvAvailable());
		amountCodes.setEmiDue(advancePayment.getEmiDue());

		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		aeEvent.setDataMap(dataMap);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setPostDate(custEODEvent.getCustomer().getCustAppDate());
		//Postings Process and save all postings related to finance for one time accounts update

		aeEvent = postAccountingEOD(aeEvent);

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		if (AdvanceRuleCode.getRule(advancePayment.getAdvancePaymentType()) == AdvanceRuleCode.ADVINT) {
			createAdvIntReceipt(advancePayment, curSchd, AccountConstants.TRANTYPE_DEBIT);
		} else if (AdvanceRuleCode.getRule(advancePayment.getAdvancePaymentType()) == AdvanceRuleCode.ADVEMI) {
			createAdvEMIReceipt(advancePayment, curSchd, AccountConstants.TRANTYPE_DEBIT);
		}

		logger.debug(Literal.LEAVING);
	}

	//FIXME Back value

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
			utilisedAmt = utilisedAmt.add(reqAmount);
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

	private void createAdvIntReceipt(AdvancePayment advancePayment, FinanceScheduleDetail curSchd, String txnType) {
		String finReference = advancePayment.getFinReference();
		String finBranch = advancePayment.getFinBranch();

		BigDecimal intAdjusted = advancePayment.getIntAdjusted();

		BigDecimal profitSchd = curSchd.getProfitSchd();
		BigDecimal schdPftPaid = curSchd.getSchdPftPaid();
		BigDecimal tDSPaid = curSchd.getTDSPaid();

		BigDecimal schdIntDue = profitSchd.subtract(schdPftPaid);

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

		long excessID;
		long receiptID = 0;

		FinReceiptHeader rch = getReceiptHeader(payNow, finReference, finBranch, AdvanceRuleCode.ADVINT);
		finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);
		advancePayment.setRequestedAmt(intAdjusted);
		receiptID = rch.getReceiptID();
		excessID = excessAmountMovement(advancePayment, receiptID, txnType);
		FinReceiptDetail rcd = getReceiptDetail(payNow, receiptID, excessID, AdvanceRuleCode.ADVINT);
		finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);
		List<ReceiptAllocationDetail> allocations = getAdvIntAllocations(receiptID, payNow, tdsPayNow, netPay);
		receiptAllocationDetailDAO.saveAllocations(allocations, TableType.MAIN_TAB);
		FinRepayHeader rph = getRepayHeader(finReference, rch, rcd);
		financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

		schdPftPaid = schdPftPaid.add(payNow);
		tDSPaid = tDSPaid.add(tdsPayNow);
		curSchd.setSchdPftPaid(schdPftPaid);
		curSchd.setTDSPaid(tDSPaid);

		if (schdPftPaid.equals(payNow)) {
			curSchd.setSchPftPaid(true);
		}

		financeScheduleDetailDAO.updateSchPftPaid(curSchd);
	}

	private void createAdvEMIReceipt(AdvancePayment advancePayment, FinanceScheduleDetail curSchd, String txnType) {
		String finReference = curSchd.getFinReference();
		String finBranch = advancePayment.getFinBranch();

		BigDecimal emiAdjusted = advancePayment.getEmiAdjusted();

		BigDecimal principalSchd = curSchd.getPrincipalSchd();
		BigDecimal schdPriPaid = curSchd.getSchdPriPaid();
		BigDecimal tDSPaid = curSchd.getTDSPaid();

		BigDecimal schdPriDue = principalSchd.subtract(schdPriPaid);

		BigDecimal payNow = BigDecimal.ZERO;
		BigDecimal tdsPayNow = BigDecimal.ZERO;
		BigDecimal netPay = BigDecimal.ZERO;

		if (curSchd.isTDSApplicable()) {
			tdsPayNow = receiptCalculator.getTDS(schdPriDue);
		}

		netPay = schdPriDue.subtract(tdsPayNow);
		if (emiAdjusted.compareTo(netPay) <= 0) {
			netPay = emiAdjusted;
		}

		if (curSchd.isTDSApplicable()) {
			tdsPayNow = receiptCalculator.getTDS(netPay);
		}
		payNow = netPay.add(tdsPayNow);

		long excessID;
		long receiptID = 0;

		FinReceiptHeader rch = getReceiptHeader(payNow, finReference, finBranch, AdvanceRuleCode.ADVEMI);
		finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);
		advancePayment.setRequestedAmt(emiAdjusted);
		receiptID = rch.getReceiptID();
		excessID = excessAmountMovement(advancePayment, receiptID, txnType);
		FinReceiptDetail rcd = getReceiptDetail(payNow, receiptID, excessID, AdvanceRuleCode.ADVEMI);
		finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);
		List<ReceiptAllocationDetail> allocations = getAdvIntAllocations(receiptID, payNow, tdsPayNow, netPay);
		receiptAllocationDetailDAO.saveAllocations(allocations, TableType.MAIN_TAB);
		FinRepayHeader rph = getRepayHeader(finReference, rch, rcd);
		financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

		schdPriPaid = schdPriPaid.add(payNow);
		tDSPaid = tDSPaid.add(tdsPayNow);
		curSchd.setSchdPriPaid(schdPriPaid);
		curSchd.setTDSPaid(tDSPaid);

		if (schdPriPaid.equals(payNow)) {
			curSchd.setSchPriPaid(true);
		}

		financeScheduleDetailDAO.updateSchPriPaid(curSchd);
	}

	private FinReceiptHeader getReceiptHeader(BigDecimal requestedAmt, String finReference, String finBranch,
			AdvanceRuleCode adviceType) {
		FinReceiptHeader rch = new FinReceiptHeader();
		rch.setReference(finReference);
		rch.setReceiptDate(DateUtility.getAppValueDate());
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		rch.setExcessAdjustTo(PennantConstants.List_Select);
		rch.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		rch.setReceiptAmount(requestedAmt);
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(adviceType.name());
		rch.setSubReceiptMode(adviceType.name());
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_APPROVED);
		rch.setLogSchInPresentment(false);
		rch.setPostBranch(finBranch);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		return rch;
	}

	private FinReceiptDetail getReceiptDetail(BigDecimal payNow, long receiptID, long excessID,
			AdvanceRuleCode adviceType) {
		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptID(receiptID);
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(adviceType.name());
		rcd.setPayAgainstID(excessID);
		rcd.setAmount(payNow);
		rcd.setDueAmount(payNow);
		rcd.setValueDate(DateUtility.getAppValueDate());
		rcd.setReceivedDate(DateUtility.getAppValueDate());
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
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_TDS");
		allocation = receiptCalculator.getAllocation(RepayConstants.ALLOCATION_TDS, id, tdsPayNow, desc, 0, "", false);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_NPFT");
		allocation = receiptCalculator.getAllocation(RepayConstants.ALLOCATION_NPFT, id, netPay, desc, 0, "", false);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		return list;
	}

	private FinRepayHeader getRepayHeader(String finReference, FinReceiptHeader rch, FinReceiptDetail rcd) {
		FinRepayHeader rph = new FinRepayHeader();
		rph.setReceiptSeqID(rcd.getReceiptSeqID());
		rph.setFinReference(finReference);
		rph.setValueDate(rch.getValueDate());
		rph.setFinEvent(rch.getReceiptPurpose());
		rph.setRepayAmount(rcd.getAmount());
		rph.setExcessAmount(rcd.getAmount());
		rph.setValueDate(DateUtility.getAppValueDate());
		return rph;
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
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}
}
