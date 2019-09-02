package com.pennanttech.pff.advancepayment.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
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
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
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

			boolean advTDSInczUpf = SysParamUtil.isAllowed(SMTParameterConstants.ADVANCE_TDS_INCZ_UPF);

			BigDecimal profitDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())
					.subtract(curSchd.getTDSAmount());

			boolean tdsInclOnPftPaid = false;
			if (isBPi && SysParamUtil.isAllowed(SMTParameterConstants.BPI_TDS_DEDUCT_ON_ORG)) {
				profitDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
				tdsInclOnPftPaid = true;
			} else if (advTDSInczUpf) {
				profitDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
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

			if (dueAmt.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			//excess fetch
			FinExcessAmount excessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference, amountType);

			BigDecimal excessBal = BigDecimal.ZERO;
			if (excessAmount == null || excessAmount.getBalanceAmt() == null
					|| excessAmount.getBalanceAmt().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			excessBal = excessAmount.getBalanceAmt();

			//Allocations
			BigDecimal adjustedAmount = BigDecimal.ZERO;
			if (dueAmt.compareTo(excessBal) >= 0) {
				adjustedAmount = excessBal;
			} else {
				adjustedAmount = dueAmt;
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

					boolean pftSettled = false;
					if (advTDSInczUpf) {
						if (curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0) {
							pftSettled = true;
						}
					} else {
						if ((curSchd.getProfitSchd().subtract(curSchd.getTDSAmount()))
								.compareTo(curSchd.getSchdPftPaid()) == 0) {
							pftSettled = true;
						}
					}

					if (pftSettled) {
						curSchd.setTDSPaid(curSchd.getTDSAmount());
						if (!advTDSInczUpf) {
							curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().add(curSchd.getTDSPaid()));
						}
					} else {
						BigDecimal tds = receiptCalculator.getTDS(curSchd.getSchdPftPaid());
						curSchd.setTDSPaid(tds);
						if (!advTDSInczUpf) {
							curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().subtract(tds));
						}
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

			// Schedule Paid's Update When payment happened
			financeScheduleDetailDAO.updateSchPaid(curSchd);

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
		if (FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())
				&& FinanceConstants.BPI_DISBURSMENT.equals(fm.getBpiTreatment())) {
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
			excess.setBalanceAmt(
					excess.getAmount().subtract(excess.getReservedAmt()).subtract(excess.getUtilisedAmt()));
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
		List<ReceiptAllocationDetail> allocations = getAdvanceAllocations(finReference, receiptID, receiptAmount,
				curSchd);

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

	private List<ReceiptAllocationDetail> getAdvanceAllocations(String finReference, long receiptID, BigDecimal payNow,
			FinanceScheduleDetail curSchd) {

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
		allocation = receiptCalculator.setAllocRecord(receiptData, RepayConstants.ALLOCATION_PFT, id,
				curSchd.getProfitSchd(), desc, 0, "", false);
		allocation.setPaidNow(curSchd.getSchdPftPaid());
		allocation.setPaidAmount(curSchd.getSchdPftPaid());
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_TDS");
		allocation = receiptCalculator.setAllocRecord(receiptData, RepayConstants.ALLOCATION_TDS, id,
				curSchd.getTDSAmount(), desc, 0, "", false);
		allocation.setPaidNow(curSchd.getTDSPaid());
		allocation.setPaidAmount(curSchd.getTDSPaid());
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_NPFT");
		BigDecimal npftDue = curSchd.getProfitSchd().subtract(curSchd.getTDSAmount());
		BigDecimal npftPaid = curSchd.getSchdPftPaid().subtract(curSchd.getTDSPaid());
		allocation = receiptCalculator.setAllocRecord(receiptData, RepayConstants.ALLOCATION_NPFT, id, npftDue, desc, 0,
				"", false);
		allocation.setPaidNow(npftPaid);
		allocation.setPaidAmount(npftPaid);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_PRI");
		allocation = receiptCalculator.setAllocRecord(receiptData, RepayConstants.ALLOCATION_PRI, id,
				curSchd.getPrincipalSchd(), desc, 0, "", false);
		allocation.setPaidNow(curSchd.getSchdPriPaid());
		allocation.setPaidAmount(curSchd.getSchdPriPaid());
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		BigDecimal emiDue = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd());
		BigDecimal emiPaid = curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid());
		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_EMI");
		allocation = receiptCalculator.setAllocRecord(receiptData, RepayConstants.ALLOCATION_EMI, id, emiDue, desc, 0,
				"", false);
		allocation.setPaidNow(emiPaid);
		allocation.setPaidAmount(emiPaid);
		allocation.setReceiptID(receiptID);
		list.add(allocation);

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

	/**
	 * Method for Creating Excess Amount record for the BPI processing Amount Movement.
	 * 
	 * @param finScheduleData
	 * @param curSchd
	 */
	public void processBpiAmount(FinScheduleData finScheduleData, FinanceScheduleDetail curSchd) {
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		String finReference = financeMain.getFinReference();
		boolean tdsApplicable = financeMain.isTDSApplicable();
		BigDecimal pftSchd = curSchd.getProfitSchd();
		BigDecimal tdsAmount = curSchd.getTDSAmount();
		String amountType = RepayConstants.EXAMOUNTTYPE_ADVINT;

		FinExcessAmount exAmount = this.finExcessAmountDAO.getExcessAmountsByRefAndType(finReference, amountType);

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
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

}
