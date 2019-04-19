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

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processAdvansePayments(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		FinanceMain fm = null;
		for (FinEODEvent finEODEvent : finEODEvents) {

			fm = finEODEvent.getFinanceMain();

			if (fm.getGrcAdvType() == null || fm.getAdvType() == null) {
				continue;
			}

			long accountingID = getAccountingID(fm, AccountEventConstants.ACCEVENT_REPAY);

			if (accountingID == Long.MIN_VALUE) {
				continue;
			}

			int idx = finEODEvent.getIdxDue();
			if (idx == -1) {
				continue;
			}

			FinanceScheduleDetail curSchd = finEODEvent.getFinanceScheduleDetails().get(idx);
			postInstallmentDues(finEODEvent, curSchd, custEODEvent, accountingID);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postInstallmentDues(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd, CustEODEvent custEODEvent,
			long accountingID) throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();
		Date valueDate = custEODEvent.getEodValueDate();
		List<FinExcessAmount> excessAmounts = finEODEvent.getFinExcessAmounts();

		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(profiDetails, finEODEvent.getFinanceScheduleDetails(),
				AccountEventConstants.ACCEVENT_REPAY, valueDate, curSchd.getSchDate());
		aeEvent.getAcSetIDList().add(accountingID);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		BigDecimal schdPriDue = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
		BigDecimal schdIntDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());

		AdvancePayment advancePayment = new AdvancePayment(fm.getGrcAdvType(), fm.getAdvType(),
				fm.getGrcPeriodEndDate());
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

		String adviceType = advancePayment.getAdvancePaymentType();
		BigDecimal amount = BigDecimal.ZERO;

		if (AdvanceRuleCode.getRule(adviceType) == AdvanceRuleCode.ADVINT) {
			amount = amountCodes.getIntDue();
		} else if (AdvanceRuleCode.getRule(adviceType) == AdvanceRuleCode.ADVEMI) {
			amount = amountCodes.getEmiDue();
		}

		createReceipt(fm.getFinReference(), fm.getFinBranch(), adviceType, amount, RepayConstants.RECEIPTTYPE_RECIPT);

		logger.debug(Literal.LEAVING);
	}

	//FIXME Back value

	public long excessAmountMovement(String finReference, String finBranch, String adviceType, BigDecimal reqAmount,
			String receiptType) {
		FinExcessAmount existingExcessAmt = null;

		FinExcessAmount excess = finExcessAmountDAO.getFinExcessAmount(finReference, adviceType);

		if (reqAmount == null) {
			reqAmount = BigDecimal.ZERO;
		}

		if (excess == null) {
			excess = new FinExcessAmount();
		}

		BigDecimal amount = excess.getAmount().add(reqAmount);
		BigDecimal utilisedAmt = excess.getUtilisedAmt();
		BigDecimal reservedAmt = excess.getReservedAmt();

		excess.setFinReference(finReference);
		excess.setAmountType(adviceType);
		excess.setAmount(amount);
		excess.setUtilisedAmt(utilisedAmt);
		excess.setReservedAmt(reservedAmt);
		excess.setBalanceAmt(amount.subtract(utilisedAmt).subtract(reservedAmt));

		if (excess.getExcessID() == Long.MIN_VALUE || excess.getExcessID() == 0) {
			finExcessAmountDAO.saveExcess(excess);
		} else {
			finExcessAmountDAO.updateExcess(existingExcessAmt);
		}

		long excessID = excess.getExcessID();
		FinExcessMovement movement = new FinExcessMovement();
		movement.setExcessID(excess.getExcessID());
		movement.setMovementType(receiptType);
		if (RepayConstants.RECEIPTTYPE_RECIPT.equals(movement.getMovementType())) {
			movement.setTranType(AccountConstants.TRANTYPE_CREDIT);
		} else {
			movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
		}

		movement.setAmount(excess.getAmount());
		finExcessAmountDAO.saveExcessMovement(movement);

		return excessID;
	}

	private void createReceipt(String finReference, String finBranch, String adviceType, BigDecimal amount,
			String receiptType) {

		long excessID = excessAmountMovement(finReference, finBranch, adviceType, amount, receiptType);

		// Receipt Header
		FinReceiptHeader rch = new FinReceiptHeader();
		rch.setReference(finReference);
		rch.setReceiptDate(DateUtility.getAppValueDate());
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		rch.setExcessAdjustTo(PennantConstants.List_Select);
		rch.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO); // FIXME Check with Pradeep
		rch.setReceiptAmount(amount);
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(RepayConstants.RECEIPTMODE_EMIINADV); // FIXME Check with Pradeep
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_APPROVED);
		rch.setLogSchInPresentment(false);
		rch.setPostBranch(finBranch);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);

		// Receipt Details
		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(RepayConstants.RECEIPTMODE_EMIINADV); // FIXME Check with Pradeep
		rcd.setPayAgainstID(excessID);
		rcd.setAmount(amount);
		rcd.setDueAmount(BigDecimal.ZERO);
		rcd.setValueDate(DateUtility.getAppValueDate());
		rcd.setReceivedDate(DateUtility.getAppValueDate());
		rcd.setPartnerBankAc(null);
		rcd.setPartnerBankAcType(null);

		finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);

		// Receipt Allocation
		List<ReceiptAllocationDetail> allocations = new ArrayList<>();
		ReceiptAllocationDetail allocation = new ReceiptAllocationDetail();
		allocation.setAllocationID(1);
		allocation.setAllocationType(RepayConstants.ALLOCATION_FUT_PFT);
		allocation.setDueAmount(BigDecimal.ZERO);
		allocation.setInProcess(BigDecimal.ZERO);
		allocation.setAllocationTo(0);
		allocation.setDueGST(BigDecimal.ZERO);
		allocation.setTotalDue(BigDecimal.ZERO);
		allocation.setEditable(false);
		allocation.setTotRecv(amount);
		allocation.setTypeDesc(Labels.getLabel("label_RecceiptDialog_AllocationType_PFT"));
		allocation.setBalance(allocation.getTotalDue());
		allocation.setTaxType("");
		allocations.add(allocation);

		receiptAllocationDetailDAO.saveAllocations(allocations, TableType.MAIN_TAB);

		FinRepayHeader rph = new FinRepayHeader();
		rph.setFinReference(rch.getReference());
		rph.setValueDate(rch.getValueDate());
		rph.setFinEvent(rch.getReceiptPurpose());
		rph.setRepayAmount(rcd.getAmount());
		rph.setExcessAmount(rcd.getAmount());
		rcd.setRepayHeader(rph);

		financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB.name());

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

}
