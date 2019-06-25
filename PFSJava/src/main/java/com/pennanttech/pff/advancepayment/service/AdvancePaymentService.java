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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
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
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
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

		FinanceMain fm = null;
		for (FinEODEvent finEODEvent : finEODEvents) {

			fm = finEODEvent.getFinanceMain();
			int idx = finEODEvent.getIdxDue();
			if (idx == -1) {
				continue;
			}

			FinanceScheduleDetail curSchd = finEODEvent.getFinanceScheduleDetails().get(idx);

			AdvanceType advanceType = null;
			String amountType = "";

			boolean isBPi = false;
			if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
				if (!StringUtils.equals(FinanceConstants.BPI_DISBURSMENT, fm.getBpiTreatment())) {
					continue;
				} else if (!SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE)) {
					continue;
				}
				advanceType = AdvanceType.AF;
				isBPi = true;
			} else {

				if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {
					advanceType = AdvanceType.getType(fm.getGrcAdvType());
				} else {
					advanceType = AdvanceType.getType(fm.getAdvType());
				}
			}

			if (advanceType == null) {
				continue;
			}

			BigDecimal profitDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())
					.subtract(curSchd.getTDSAmount());

			boolean tdsInclOnPftPaid = false;
			if (isBPi && SysParamUtil.isAllowed(SMTParameterConstants.BPI_TDS_DEDUCT_ON_ORG)) {
				profitDue = curSchd.getProfitSchd();
				tdsInclOnPftPaid = true;
			}

			BigDecimal principalDue = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());

			//get excess
			String finReference = fm.getFinReference();
			BigDecimal dueAmt = BigDecimal.ZERO;

			if (advanceType == AdvanceType.AE) {
				if (AdvanceStage.getStage(fm.getAdvStage()) == AdvanceStage.FE) {
					continue;
				}
				amountType = RepayConstants.EXAMOUNTTYPE_ADVEMI;
				dueAmt = principalDue.add(profitDue);
			} else {
				amountType = RepayConstants.EXAMOUNTTYPE_ADVINT;
				dueAmt = profitDue;
			}

			//excess fetch
			FinExcessAmount excessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference, amountType);

			BigDecimal excessBal = BigDecimal.ZERO;
			if (excessAmount != null) {
				//we are considering reserved amount science it is the place where we are utilize the reserve amount.
				excessBal = excessAmount.getBalanceAmt().add(excessAmount.getReservedAmt());
				if (excessBal == null || excessBal.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
			}

			//Allocations
			BigDecimal adjustedAmount = BigDecimal.ZERO;

			if (dueAmt.compareTo(BigDecimal.ZERO) > 0) {
				if (excessBal.compareTo(BigDecimal.ZERO) > 0) {
					if (dueAmt.compareTo(excessBal) >= 0) {
						adjustedAmount = excessBal;
					} else {
						adjustedAmount = dueAmt;
					}
				}
			}

			/* Schedule Update */
			if (StringUtils.equals(amountType, RepayConstants.EXAMOUNTTYPE_ADVINT)) {

				curSchd.setSchdPftPaid(adjustedAmount);

			} else if (StringUtils.equals(amountType, RepayConstants.EXAMOUNTTYPE_ADVEMI)) {

				BigDecimal emiPayNow = adjustedAmount;

				if (emiPayNow.compareTo(profitDue) > 0) {

					curSchd.setSchdPftPaid(profitDue);

					emiPayNow = emiPayNow.subtract(profitDue);

					if (emiPayNow.compareTo(curSchd.getPrincipalSchd()) > 0) {
						curSchd.setSchdPriPaid(curSchd.getPrincipalSchd());
					} else {
						curSchd.setSchdPriPaid(emiPayNow);
					}

				} else {
					curSchd.setSchdPftPaid(emiPayNow);
				}
			}

			if (curSchd.getTDSAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (tdsInclOnPftPaid) {
					curSchd.setTDSPaid(curSchd.getTDSAmount());
				} else {
					if ((curSchd.getProfitSchd().subtract(curSchd.getTDSAmount()))
							.compareTo(curSchd.getSchdPftPaid()) == 0) {
						curSchd.setTDSPaid(curSchd.getTDSAmount());
						curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().add(curSchd.getTDSPaid()));
					} else {
						BigDecimal tds = receiptCalculator.getTDS(curSchd.getSchdPftPaid());
						curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().subtract(tds));
						curSchd.setTDSPaid(tds);
					}
				}
			}

			if (curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) >= 0) {
				curSchd.setSchPftPaid(true);
			}

			if (curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) >= 0) {
				curSchd.setSchPriPaid(true);
			}

			/* postings */

			long linkedTranId = postAdvancePayments(finEODEvent, curSchd, custEODEvent.getEodValueDate());

			/* Excess Movement */
			FinReceiptHeader rch = new FinReceiptHeader();
			rch.setReference(fm.getFinReference());
			long receiptID = finReceiptHeaderDAO.generatedReceiptID(rch);

			/* Update excess details */
			long excessID = advanceExcessMovement(excessAmount, adjustedAmount, receiptID, valueDate);

			/* Receipt creation */
			prepareReceipt(rch, fm, adjustedAmount, amountType, valueDate, excessID, curSchd, linkedTranId);
			FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();
			profitDetail.setFinReference(finReference);
			profitDetail.setTotalPriPaid(profitDetail.getTotalPriPaid().add(curSchd.getSchdPriPaid()));
			profitDetail.setTotalPftPaid(profitDetail.getTotalPftPaid().add(curSchd.getSchdPftPaid()));
			profitDetail.setTdTdsPaid(profitDetail.getTdTdsPaid().add(curSchd.getTDSPaid()));
			profitDetail.setTdTdsBal(profitDetail.getTdTdsAmount().subtract(profitDetail.getTdTdsPaid()));

			financeScheduleDetailDAO.updateSchPaid(curSchd);

			/* Update Summary */
			financeProfitDetailDAO.updateSchPaid(profitDetail);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param finEODEvent
	 * @param curSchd
	 * @param valueDate
	 * @return
	 */
	public long postAdvancePayments(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd, Date valueDate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();
		Date schDate = curSchd.getSchDate();

		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();

		String finEvent = AccountEventConstants.ACCEVENT_REPAY;
		long accountingID = getAccountingID(fm, finEvent);
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(profiDetails, schedules, finEvent, valueDate, schDate);
		aeEvent.getAcSetIDList().add(accountingID);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setPriAdjusted(curSchd.getSchdPriPaid());
		amountCodes.setIntAdjusted(curSchd.getSchdPftPaid());
		amountCodes.setIntTdsAdjusted(curSchd.getTDSPaid());
		if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
				&& StringUtils.equals(fm.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)) {
			if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_TDS_DEDUCT_ON_ORG)) {
				amountCodes.setIntTdsAdjusted(BigDecimal.ZERO);
			}
		}

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
		return aeEvent.getLinkedTranId();
	}

	/**
	 * @param excess
	 * @param adjAmount
	 * @param receiptID
	 * @param valueDate
	 * @return
	 */
	private long advanceExcessMovement(FinExcessAmount excess, BigDecimal adjAmount, Long receiptID, Date valueDate) {
		long excessID = Long.MIN_VALUE;

		if (excess != null) {
			excessID = excess.getExcessID();
			FinExcessMovement excessAmovement = finExcessAmountDAO.getFinExcessMovement(excessID,
					RepayConstants.PAYTYPE_PRESENTMENT, valueDate);

			if (excessAmovement != null) {
				//reserve exist
				excess.setReservedAmt(excess.getReservedAmt().subtract(excessAmovement.getAmount()));
			}

			excess.setUtilisedAmt(excess.getUtilisedAmt().add(adjAmount));
			excess.setBalanceAmt(excess.getAmount().subtract(excess.getReservedAmt()).subtract(excess.getUtilisedAmt()));
			finExcessAmountDAO.updateReserveUtilization(excess);

			//movement
			FinExcessMovement excessMovement = new FinExcessMovement();
			excessMovement.setExcessID(excessID);
			excessMovement.setAmount(adjAmount);
			excessMovement.setReceiptID(receiptID);
			excessMovement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
			excessMovement.setTranType(AccountConstants.TRANTYPE_DEBIT);
			finExcessAmountDAO.saveExcessMovement(excessMovement);
		}

		return excessID;
	}

	public List<ReturnDataSet> processBackDatedAdvansePayments(FinanceDetail financeDetail,
			FinanceProfitDetail profiDetails, Date appDate, String postBranch, boolean isApprove) {
		return new ArrayList<>();
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

		AdvancePayment advancePayment = new AdvancePayment(financeMain);

		Long feeTypeId;
		BigDecimal adviseAmount = BigDecimal.ZERO;
		if (previousAdvInt.compareTo(BigDecimal.ZERO) > 0) {
			feeTypeId = map.get(AdvanceRuleCode.ADVINT.name());
			advancePayment.setAdvancePaymentType(AdvanceRuleCode.ADVINT.name());

			if (advPayment.compareTo(previousAdvInt) < 0) {
				adviseAmount = previousAdvInt.subtract(advPayment);
				advancePayment.setRequestedAmt(adviseAmount);
				excessAmountMovement(advancePayment, null, AccountConstants.TRANTYPE_DEBIT);
				//createPayableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			} else if (advPayment.compareTo(previousAdvInt) > 0) {
				adviseAmount = advPayment.subtract(previousAdvInt);
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
				//createPayableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			} else if (advPayment.compareTo(previousAdvInt) > 0) {
				adviseAmount = previousAdvInt.subtract(advPayment);
				advancePayment.setRequestedAmt(adviseAmount);
				excessAmountMovement(advancePayment, null, AccountConstants.TRANTYPE_CREDIT);
				createReceivableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			}
		}
	}

	public long excessAmountMovement(AdvancePayment advancePayment, Long receiptID, String txnType) {
		String finReference = advancePayment.getFinanceMain().getFinReference();
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

	private FinReceiptHeader prepareReceipt(FinReceiptHeader rch, FinanceMain fm, BigDecimal receiptAmount,
			String receiptMode, Date valueDate, long excessID, FinanceScheduleDetail curSchd, long linkedTranId) {

		String finReference = fm.getFinReference();
		rch.setReference(finReference);
		rch.setReceiptDate(valueDate);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		rch.setExcessAdjustTo(PennantConstants.List_Select);
		rch.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		rch.setReceiptAmount(receiptAmount);
		//rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(receiptMode);
		rch.setSubReceiptMode(receiptMode);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		rch.setLogSchInPresentment(false);
		rch.setPostBranch(fm.getFinBranch());
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setRecordType("");
		rch.setVersion(1);
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		//rch.setLastMntBy(lastMntBy);
		rch.setRealizationDate(valueDate);
		rch.setCashierBranch(rch.getPostBranch());

		/* 2. Receipt Details */
		long receiptID = rch.getReceiptID();
		FinReceiptDetail rcd = getReceiptDetail(receiptAmount, valueDate, receiptID, excessID, receiptMode);

		/* 3. Receipt Allocation Details */
		List<ReceiptAllocationDetail> allocations = getAdvIntAllocations(finReference, receiptID, receiptAmount,
				curSchd.getTDSPaid(), BigDecimal.ZERO);

		/* 4. Repay Header */
		FinRepayHeader rph = getRepayHeader(finReference, valueDate, rch, rcd);

		/* 5. Repay Schedule Details */
		List<RepayScheduleDetail> list = getRepayScheduleDetail(curSchd, rph, valueDate, allocations);

		/* 8. Fin Repayments */
		FinanceRepayments financeRepayments = getFinanceRepayments(fm, curSchd, receiptID, linkedTranId, valueDate);

		finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);
		finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);
		receiptAllocationDetailDAO.saveAllocations(allocations, TableType.MAIN_TAB);
		rph.setReceiptSeqID(rcd.getReceiptSeqID());
		rph.setLinkedTranId(linkedTranId);
		financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

		for (RepayScheduleDetail repayScheduleDetail : list) {
			repayScheduleDetail.setRepayID(rph.getRepayID());
			repayScheduleDetail.setLinkedTranId(linkedTranId);
		}

		financeRepaymentsDAO.saveRpySchdList(list, TableType.MAIN_TAB);
		financeRepaymentsDAO.save(financeRepayments, TableType.MAIN_TAB.getSuffix());

		return rch;
	}

	private FinReceiptDetail getReceiptDetail(BigDecimal payNow, Date valueDate, long receiptID, long excessID,
			String paymentType) {
		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptID(receiptID);
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(paymentType);
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

	private List<ReceiptAllocationDetail> getAdvIntAllocations(String finReference, long receiptID, BigDecimal payNow,
			BigDecimal tdsPayNow, BigDecimal netPay) {

		FinReceiptData receiptData = new FinReceiptData();
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		receiptData.setFinanceDetail(financeDetail);

		List<ReceiptAllocationDetail> list = new ArrayList<>();
		if (payNow.compareTo(BigDecimal.ZERO) == 0) {
			return list;
		}

		int id = 1;
		ReceiptAllocationDetail allocation;
		String desc = Labels.getLabel("label_RecceiptDialog_AllocationType_PFT");
		allocation = receiptCalculator.setAllocRecord(receiptData, RepayConstants.ALLOCATION_PFT, id, payNow, desc, 0,
				"", false);
		allocation.setPaidNow(payNow);
		allocation.setPaidAmount(payNow);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_TDS");
		allocation = receiptCalculator.setAllocRecord(receiptData, RepayConstants.ALLOCATION_TDS, id, tdsPayNow, desc,
				0, "", false);
		allocation.setPaidNow(tdsPayNow);
		allocation.setPaidAmount(tdsPayNow);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_NPFT");
		allocation = receiptCalculator.setAllocRecord(receiptData, RepayConstants.ALLOCATION_NPFT, id, netPay, desc, 0,
				"", false);
		allocation.setPaidNow(netPay);
		allocation.setPaidNow(netPay);
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

	public FinanceRepayments getFinanceRepayments(FinanceMain fm, FinanceScheduleDetail curSchd, long receiptId,
			long linkedTranId, Date valueDate) {
		FinanceRepayments repayment = new FinanceRepayments();

		repayment.setReceiptId(receiptId);
		repayment.setFinReference(fm.getFinReference());
		repayment.setFinSchdDate(curSchd.getSchDate());
		repayment.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		repayment.setLinkedTranId(linkedTranId);

		repayment.setFinRpyAmount(curSchd.getSchdPftPaid());
		repayment.setFinPostDate(valueDate);
		repayment.setFinValueDate(valueDate);
		repayment.setFinBranch(fm.getFinBranch());
		repayment.setFinType(fm.getFinType());
		repayment.setFinCustID(fm.getCustID());
		repayment.setFinSchdPftPaid(curSchd.getSchdPftPaid());
		repayment.setFinSchdPriPaid(curSchd.getSchdPriPaid());
		repayment.setFinTotSchdPaid(curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid()));
		repayment.setFinSchdTdsPaid(curSchd.getTDSPaid());
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(BigDecimal.ZERO);
		repayment.setFinRefund(BigDecimal.ZERO);

		// Fee Details
		repayment.setSchdFeePaid(BigDecimal.ZERO);
		repayment.setSchdInsPaid(BigDecimal.ZERO);
		repayment.setSchdSuplRentPaid(BigDecimal.ZERO);
		repayment.setSchdIncrCostPaid(BigDecimal.ZERO);

		return repayment;
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

	/**
	 * Method for Creating Excess Amount record for the BPI processing Amount Movement.
	 * 
	 * @param finReference
	 * @param tdsApplicable
	 * @param pftSchd
	 * @param tdsAmount
	 */
	public void processBpiAmount(String finReference, boolean tdsApplicable, BigDecimal pftSchd, BigDecimal tdsAmount) {
		FinExcessAmount exAmount = this.finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
				RepayConstants.EXAMOUNTTYPE_ADVINT);

		BigDecimal bpiAmt = pftSchd;
		if (tdsApplicable && !SysParamUtil.isAllowed(SMTParameterConstants.BPI_TDS_DEDUCT_ON_ORG)) {
			bpiAmt = pftSchd.subtract(tdsAmount);
		}

		if (BigDecimal.ZERO.compareTo(bpiAmt) == 0) {
			return;
		}

		// Excess Record Creation
		if (exAmount == null) {
			exAmount = new FinExcessAmount();
			exAmount.setFinReference(finReference);
			exAmount.setAmountType(RepayConstants.EXAMOUNTTYPE_ADVINT);
			exAmount.setAmount(bpiAmt);
			exAmount.setBalanceAmt(bpiAmt);

			this.finExcessAmountDAO.saveExcess(exAmount);
		} else {
			this.finExcessAmountDAO.updateExcessBal(exAmount.getExcessID(), bpiAmt);
		}

		// Excess Movement Creation for Credit
		FinExcessMovement movement = new FinExcessMovement();
		movement.setExcessID(exAmount.getExcessID());
		movement.setReceiptID(null);
		movement.setMovementType(RepayConstants.RECEIPTTYPE_PAYABLE);
		movement.setTranType(AccountConstants.TRANTYPE_CREDIT);
		movement.setAmount(bpiAmt);
		this.finExcessAmountDAO.saveExcessMovement(movement);
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
