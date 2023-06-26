package com.pennanttech.pff.advancepayment.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.ServiceHelper;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.finance.AdvancePaymentDetailDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.AdvancePaymentDetail;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
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
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.advancepayment.model.AdvancePayment;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ExcessType;

public class AdvancePaymentService extends ServiceHelper {
	private Logger logger = LogManager.getLogger(AdvancePaymentService.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private ReceiptCalculator receiptCalculator;
	private AdvancePaymentDetailDAO advancePaymentDetailDAO;
	private FinFeeDetailDAO finFeeDetailDAO;

	public void processAdvansePayments(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();
		EventProperties eventProperties = custEODEvent.getEventProperties();

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
			if (FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())) {
				if (!FinanceConstants.BPI_DISBURSMENT.equals(fm.getBpiTreatment())) {
					continue;
				} else if (!eventProperties.isBpiPaidOnInstDate()) {
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

			boolean advTDSInczUpf = eventProperties.isAdvTdsIncsUpf();

			BigDecimal profitDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())
					.subtract(curSchd.getTDSAmount());

			boolean tdsInclOnPftPaid = false;
			if (isBPi && eventProperties.isBpiTdsDeductOnOrg()) {
				profitDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
				tdsInclOnPftPaid = true;
			} else if (advTDSInczUpf) {
				profitDue = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
			}

			BigDecimal principalDue = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());

			long finID = fm.getFinID();
			String finReference = fm.getFinReference();
			BigDecimal dueAmt = BigDecimal.ZERO;

			if (advanceType == AdvanceType.AE) {
				if (AdvanceStage.getStage(fm.getAdvStage()) == AdvanceStage.FE) {
					continue;
				}
				amountType = ExcessType.ADVEMI;
				dueAmt = principalDue.add(profitDue);
			} else {
				amountType = ExcessType.ADVINT;
				dueAmt = profitDue;
			}

			if (dueAmt.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			// excess fetch
			FinExcessAmount excessAmount = finExcessAmountDAO.getExcessAmountsByReceiptId(finID, amountType, 0);

			BigDecimal excessBal = BigDecimal.ZERO;
			if (excessAmount == null || excessAmount.getBalanceAmt() == null
					|| excessAmount.getBalanceAmt().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			excessBal = excessAmount.getBalanceAmt();

			// Allocations
			BigDecimal adjustedAmount = BigDecimal.ZERO;
			if (dueAmt.compareTo(excessBal) >= 0) {
				adjustedAmount = excessBal;
			} else {
				adjustedAmount = dueAmt;
			}

			/* Schedule Update */
			if (ExcessType.ADVINT.equals(amountType)) {

				curSchd.setSchdPftPaid(adjustedAmount);

			} else if (ExcessType.ADVEMI.equals(amountType)) {
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
						BigDecimal tds = receiptCalculator.getTDS(fm, curSchd.getSchdPftPaid());
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
			rch.setFinID(fm.getFinID());
			rch.setReference(fm.getFinReference());
			long receiptID = finReceiptHeaderDAO.generatedReceiptID(rch);

			/* Update excess details */
			long excessID = advanceExcessMovement(excessAmount, adjustedAmount, receiptID, valueDate);

			/* Receipt creation */
			prepareReceipt(rch, fm, adjustedAmount, amountType, valueDate, excessID, curSchd, linkedTranId);
			FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();

			profitDetail.setFinID(finID);
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

	public long postAdvancePayments(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd, Date valueDate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();
		Date schDate = curSchd.getSchDate();

		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		FinanceProfitDetail profiDetails = finEODEvent.getFinProfitDetail();

		String finEvent = AccountingEvent.REPAY;
		Long accountingID = getAccountingID(fm, finEvent);
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, profiDetails, schedules, finEvent, valueDate, schDate);
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

		dataMap.put("PB_ReceiptAmount", BigDecimal.ZERO);
		dataMap.put("EA_ReceiptAmount", BigDecimal.ZERO);
		dataMap.put("EX_ReceiptAmount", BigDecimal.ZERO);
		dataMap.put("bounceCharge", BigDecimal.ZERO);
		dataMap.put("bounceCharge_CGST", BigDecimal.ZERO);
		dataMap.put("bounceCharge_UGST", BigDecimal.ZERO);
		dataMap.put("bounceCharge_SGST", BigDecimal.ZERO);
		dataMap.put("bounceCharge_IGST", BigDecimal.ZERO);
		dataMap.put("bounceCharge_CESS", BigDecimal.ZERO);

		dataMap.put("fromState", "");
		dataMap.put("fromUnionTerritory", false);
		dataMap.put("fromStateGstExempted", false);

		dataMap.put("toState", "");
		dataMap.put("toUnionTerritory", false);
		dataMap.put("toStateGstExempted", false);

		aeEvent.setDataMap(dataMap);
		aeEvent.setPostDate(valueDate);
		aeEvent.setValueDate(valueDate);

		// Postings Process and save all postings related to finance for one time accounts update
		try {
			postAccountingEOD(aeEvent);
		} catch (Exception e) {
			throw new AppException();
		}

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		logger.debug(Literal.LEAVING);
		return aeEvent.getLinkedTranId();
	}

	private long advanceExcessMovement(FinExcessAmount excess, BigDecimal adjAmount, Long receiptID, Date valueDate) {
		long excessID = Long.MIN_VALUE;

		if (excess != null) {
			excessID = excess.getExcessID();
			FinExcessMovement excessAmovement = finExcessAmountDAO.getFinExcessMovement(excessID,
					RepayConstants.PAYTYPE_PRESENTMENT, valueDate);

			if (excessAmovement != null) {
				// reserve exist
				excess.setReservedAmt(excess.getReservedAmt().subtract(excessAmovement.getAmount()));
			}

			excess.setUtilisedAmt(excess.getUtilisedAmt().add(adjAmount));
			excess.setBalanceAmt(
					excess.getAmount().subtract(excess.getReservedAmt()).subtract(excess.getUtilisedAmt()));
			finExcessAmountDAO.updateReserveUtilization(excess);

			// movement
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

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		rch.setFinID(finID);
		rch.setReference(finReference);
		rch.setReceiptDate(valueDate);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(PennantConstants.List_Select);
		rch.setAllocationType(AllocationType.AUTO);
		rch.setReceiptAmount(receiptAmount);
		// rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(receiptMode);
		rch.setSubReceiptMode(receiptMode);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		rch.setLogSchInPresentment(false);
		rch.setPostBranch(fm.getFinBranch());
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setRecordType("");
		rch.setVersion(1);
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		// rch.setLastMntBy(lastMntBy);
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
		allocation = receiptCalculator.setAllocRecord(receiptData, Allocation.PFT, id, curSchd.getProfitSchd(), desc, 0,
				"", false, false);
		allocation.setPaidNow(curSchd.getSchdPftPaid());
		allocation.setPaidAmount(curSchd.getSchdPftPaid());
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_TDS");
		allocation = receiptCalculator.setAllocRecord(receiptData, Allocation.TDS, id, curSchd.getTDSAmount(), desc, 0,
				"", false, false);
		allocation.setPaidNow(curSchd.getTDSPaid());
		allocation.setPaidAmount(curSchd.getTDSPaid());
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_NPFT");
		BigDecimal npftDue = curSchd.getProfitSchd().subtract(curSchd.getTDSAmount());
		BigDecimal npftPaid = curSchd.getSchdPftPaid().subtract(curSchd.getTDSPaid());
		allocation = receiptCalculator.setAllocRecord(receiptData, Allocation.NPFT, id, npftDue, desc, 0, "", false,
				false);
		allocation.setPaidNow(npftPaid);
		allocation.setPaidAmount(npftPaid);
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_PRI");
		allocation = receiptCalculator.setAllocRecord(receiptData, Allocation.PRI, id, curSchd.getPrincipalSchd(), desc,
				0, "", false, false);
		allocation.setPaidNow(curSchd.getSchdPriPaid());
		allocation.setPaidAmount(curSchd.getSchdPriPaid());
		allocation.setReceiptID(receiptID);
		list.add(allocation);
		id = id + 1;

		BigDecimal emiDue = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd());
		BigDecimal emiPaid = curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid());
		desc = Labels.getLabel("label_RecceiptDialog_AllocationType_EMI");
		allocation = receiptCalculator.setAllocRecord(receiptData, Allocation.EMI, id, emiDue, desc, 0, "", false,
				false);
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

		rsd.setFinID(curSchd.getFinID());
		rsd.setFinReference(curSchd.getFinReference());
		rsd.setSchDate(curSchd.getSchDate());
		rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		rsd.setProfitSchdBal(curSchd.getProfitSchd());
		rsd.setPrincipalSchdBal(curSchd.getPrincipalSchd());
		rsd.setProfitSchdPayNow(curSchd.getSchdPftPaid());
		rsd.setPrincipalSchdPayNow(curSchd.getSchdPriPaid());

		int daysLate = DateUtil.getDaysBetween(curSchd.getSchDate(), valueDate);
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
			if (Allocation.PFT.equals(allocation.getAllocationType())) {
				rsd.setProfitSchdPayNow(rsd.getProfitSchdPayNow().add(allocation.getPaidNow()));
				rsd.setPftSchdWaivedNow(rsd.getPftSchdWaivedNow().add(allocation.getWaivedNow()));
			} else if (Allocation.TDS.equals(allocation.getAllocationType())) {
				rsd.setTdsSchdPayNow(allocation.getPaidNow().add(allocation.getWaivedNow()));
			} else if (Allocation.NPFT.equals(allocation.getAllocationType())) {
				// rsd.setTdsSchdPayNow(allocation.getPaidNow().add(allocation.getWaivedNow()));
			}
		}

		list.add(rsd);
		return list;
	}

	public FinanceRepayments getFinanceRepayments(FinanceMain fm, FinanceScheduleDetail curSchd, long receiptId,
			long linkedTranId, Date valueDate) {
		FinanceRepayments repayment = new FinanceRepayments();

		repayment.setReceiptId(receiptId);
		repayment.setFinID(fm.getFinID());
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

		long finID = financeMain.getFinID();
		String finReference = financeMain.getFinReference();
		boolean tdsApplicable = TDSCalculator.isTDSApplicable(financeMain);
		BigDecimal pftSchd = curSchd.getProfitSchd();
		BigDecimal tdsAmount = curSchd.getTDSAmount();
		String amountType = ExcessType.ADVINT;

		FinExcessAmount exAmount = this.finExcessAmountDAO.getExcessAmountsByReceiptId(finID, amountType, 0);

		BigDecimal bpiAmt = pftSchd;

		if (tdsApplicable && !SysParamUtil.isAllowed(SMTParameterConstants.BPI_TDS_DEDUCT_ON_ORG)) {
			bpiAmt = pftSchd.subtract(tdsAmount);
		}

		if (BigDecimal.ZERO.compareTo(bpiAmt) == 0) {
			return;
		}

		if (exAmount == null) {
			exAmount = new FinExcessAmount();
			exAmount.setFinID(finID);
			exAmount.setFinReference(finReference);
			exAmount.setAmountType(ExcessType.ADVINT);
			exAmount.setAmount(bpiAmt);
			exAmount.setBalanceAmt(bpiAmt);
			exAmount.setReceiptID(null);
			exAmount.setValueDate(SysParamUtil.getAppDate());
			exAmount.setPostDate(exAmount.getValueDate());

			this.finExcessAmountDAO.saveExcess(exAmount);
		} else {
			this.finExcessAmountDAO.updateExcessBal(exAmount.getExcessID(), bpiAmt);
		}

		FinExcessMovement movement = new FinExcessMovement();
		movement.setExcessID(exAmount.getExcessID());
		movement.setReceiptID(null);
		movement.setMovementType(RepayConstants.RECEIPTTYPE_PAYABLE);
		movement.setTranType(AccountConstants.TRANTYPE_CREDIT);
		movement.setAmount(bpiAmt);

		this.finExcessAmountDAO.saveExcessMovement(movement);
	}

	public void processAdvancePayment(AdvancePaymentDetail advPay, String moduleDefiner, long lastMntBy) {
		BigDecimal advInt = advPay.getAdvInt();
		BigDecimal advEMI = advPay.getAdvEMI();

		if (advInt.compareTo(BigDecimal.ZERO) == 0 && advEMI.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		// Advance Interest Amount Process
		if (advInt.compareTo(BigDecimal.ZERO) > 0 && ImplementationConstants.RCVADV_CREATE_ON_INTEMI) {
			updateExcess(advPay, ExcessType.ADVINT, moduleDefiner, lastMntBy);
		}

		// Advance EMI Amount Process
		if (advEMI.compareTo(BigDecimal.ZERO) > 0 && ImplementationConstants.RCVADV_CREATE_ON_INTEMI) {
			updateExcess(advPay, ExcessType.ADVEMI, moduleDefiner, lastMntBy);
		}

	}

	protected void updateExcess(AdvancePaymentDetail advPay, String excessType, String moduleDefiner, long lastMntBy) {
		FinExcessAmount excess = null;
		if (!StringUtils.equals(moduleDefiner, FinServiceEvent.ORG)) {
			excess = finExcessAmountDAO.getFinExcessAmount(advPay.getFinID(), ExcessType.ADVINT);
		}

		if (excess == null) {
			excess = new FinExcessAmount();
		}

		BigDecimal amount = BigDecimal.ZERO;
		if (StringUtils.equals(excessType, ExcessType.ADVINT)) {
			amount = advPay.getAdvInt();
			boolean advIntTDSInczUpf = SysParamUtil.isAllowed(SMTParameterConstants.ADVANCE_TDS_INCZ_UPF);
			if (advIntTDSInczUpf) {
				amount = amount.add(advPay.getAdvIntTds());
			}

		} else if (StringUtils.equals(excessType, ExcessType.ADVEMI)) {
			amount = advPay.getAdvEMI();
			boolean advEMITDSInczUpf = SysParamUtil.isAllowed(SMTParameterConstants.ADVANCE_TDS_INCZ_UPF);
			if (advEMITDSInczUpf) {
				amount = amount.add(advPay.getAdvEMITds());
			}
		}

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		FinExcessAmount existingExcessData = finExcessAmountDAO.getFinExcessAmount(advPay.getFinID(),
				ExcessType.ADVINT);

		if (existingExcessData != null) {
			excess.setExcessID(existingExcessData.getExcessID());
		}

		excess.setFinID(advPay.getFinID());
		excess.setFinReference(advPay.getFinReference());
		excess.setAmountType(excessType);
		excess.setAmount(excess.getAmount().add(amount));
		excess.setBalanceAmt(excess.getBalanceAmt().add(amount));

		if (excess.getExcessID() == Long.MIN_VALUE || excess.getExcessID() == 0) {
			excess.setReceiptID(null);
			excess.setValueDate(SysParamUtil.getAppDate());
			excess.setPostDate(excess.getValueDate());
			finExcessAmountDAO.saveExcess(excess);
		} else {
			finExcessAmountDAO.updateExcess(excess);
		}

		FinExcessMovement movement = new FinExcessMovement();
		movement.setExcessID(excess.getExcessID());
		movement.setReceiptID(advPay.getInstructionUID());
		movement.setMovementType(RepayConstants.RECEIPTTYPE_ADJUST);

		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
		} else {
			movement.setTranType(AccountConstants.TRANTYPE_CREDIT);
		}
		movement.setAmount(amount.abs());

		finExcessAmountDAO.saveExcessMovement(movement);

		// Create Advise based on Amount for the Excess Type
		if (StringUtils.equals(moduleDefiner, FinServiceEvent.ORG)) {
			return;
		}
		if (StringUtils.equals(moduleDefiner, FinServiceEvent.ADDDISB)) {
			if (!ImplementationConstants.RCVADV_CREATE_ON_INTEMI) {
				return;
			}
		}

		// Advise Creation
		createAdvise(advPay.getFinReference(), excessType, SysParamUtil.getAppDate(), amount, lastMntBy);

	}

	/**
	 * Creating a Receivable advise for insurance Cancel or surrender amount.
	 * 
	 * @param vASRecording
	 */
	private void createAdvise(String finReference, String amountType, Date valueDate, BigDecimal adviseAmount,
			long lastMntBy) {
		logger.debug(Literal.ENTERING);

		// Fetch Fee Type ID
		Long feeTypeID = feeTypeDAO.getFeeTypeId(amountType);

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		if (adviseAmount.compareTo(BigDecimal.ZERO) > 0) {
			manualAdvise.setAdviseType(AdviseType.RECEIVABLE.id());
		} else {
			manualAdvise.setAdviseType(AdviseType.PAYABLE.id());
		}
		manualAdvise.setFinReference(finReference);
		manualAdvise.setFeeTypeID(feeTypeID == null ? 0 : feeTypeID);
		manualAdvise.setAdviseAmount(adviseAmount.abs());
		manualAdvise.setRemarks("Advise for Advance Interest/EMI");
		manualAdvise.setValueDate(valueDate);
		manualAdvise.setPostDate(valueDate);
		manualAdvise.setBalanceAmt(adviseAmount.abs());

		manualAdvise.setVersion(0);
		manualAdvise.setLastMntBy(lastMntBy);
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setWorkflowId(0);

		manualAdviseDAO.save(manualAdvise, TableType.MAIN_TAB);

		logger.debug(Literal.LEAVING);
	}

	public AdvancePaymentDetail getAdvIntPayable(FinanceMain fm, FinScheduleData schdData) {
		if (!AdvanceType.hasAdvInterest(fm)) {
			return null;
		}

		AdvancePaymentDetail oldAdvPay = advancePaymentDetailDAO.getAdvancePaymentDetailBalByRef(fm.getFinID());
		AdvancePaymentDetail curAdvpay = AdvancePaymentUtil.getDiffOnAdvIntAndAdvEMI(schdData, oldAdvPay, "");

		if (curAdvpay == null) {
			return null;
		}

		BigDecimal amount = BigDecimal.ZERO;
		amount = curAdvpay.getAdvInt();
		boolean advIntTDSInczUpf = SysParamUtil.isAllowed(SMTParameterConstants.ADVANCE_TDS_INCZ_UPF);
		if (advIntTDSInczUpf) {
			amount = amount.add(curAdvpay.getAdvIntTds());
		}

		curAdvpay.setAdvInt(amount);

		return curAdvpay;
	}

	public void setAdvancePaymentDetails(FinanceMain fm, FinScheduleData finScheduleData) {
		AdvancePaymentDetail curAdvpay = getAdvIntPayable(fm, finScheduleData);

		if (curAdvpay == null) {
			return;
		}

		updateExcess(curAdvpay, ExcessType.ADVINT, fm.getModuleDefiner(), fm.getLastMntBy());
	}

	public void setAdvancePaymentDetails(FinanceDetail fd, AEAmountCodes amountCodes) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		AdvancePaymentDetail oldAdvPay = advancePaymentDetailDAO.getAdvancePaymentDetailBalByRef(fm.getFinID());
		AdvancePaymentDetail curAdvpay = AdvancePaymentUtil.getDiffOnAdvIntAndAdvEMI(schdData, oldAdvPay,
				fd.getModuleDefiner());
		if (curAdvpay != null) {
			amountCodes.setIntAdjusted(curAdvpay.getAdvInt());
			amountCodes.setEmiAdjusted(curAdvpay.getAdvEMI());
			if (SysParamUtil.isAllowed(SMTParameterConstants.ADVANCE_TDS_INCZ_UPF)) {
				amountCodes.setIntTdsAdjusted(curAdvpay.getAdvIntTds());
				amountCodes.setEmiTdsAdjusted(curAdvpay.getAdvEMITds());
			} else {
				amountCodes.setIntTdsAdjusted(BigDecimal.ZERO);
				amountCodes.setEmiTdsAdjusted(BigDecimal.ZERO);
			}
		}
		fd.setAdvancePaymentDetail(curAdvpay);
	}

	public void setIntAdvFlag(FinanceMain main, AEAmountCodes aeAmountCodes, boolean bpi) {
		// Set whether the advance interest available or not
		BigDecimal advIntBalance = getAdvIntBalance(main, bpi);
		if (advIntBalance.compareTo(BigDecimal.ZERO) > 0) {
			aeAmountCodes.setIntAdv(true);
		}
	}

	public BigDecimal getAdvIntBalance(FinanceMain fm, boolean bpi) {
		BigDecimal advIntBalance = BigDecimal.ZERO;
		if (AdvanceType.hasAdvInterest(fm) || bpi) {
			FinExcessAmount excessAmount = finExcessAmountDAO.getFinExcessAmount(fm.getFinID(),
					ExcessType.ADVINT);

			if (excessAmount != null) {
				advIntBalance = excessAmount.getBalanceAmt();
				if (advIntBalance.compareTo(BigDecimal.ZERO) == 0) {
					advIntBalance = excessAmount.getReservedAmt();
				}
			}
		}

		return advIntBalance;
	}

	public void save(AdvancePaymentDetail advancePaymentDetail) {
		advancePaymentDetailDAO.save(advancePaymentDetail);
	}

	public void excessAmountMovement(FinScheduleData finScheduleData) {
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		long finID = financeMain.getFinID();
		String finReference = financeMain.getFinReference();
		Date valueDate = SysParamUtil.getAppValueDate();
		long lastMntBy = financeMain.getLastMntBy();

		AdvanceType grcAdvanceType = AdvanceType.getType(financeMain.getGrcAdvType());
		AdvanceType repayAdvanceType = AdvanceType.getType(financeMain.getAdvType());

		List<FinFeeDetail> fees = finFeeDetailDAO.getPreviousAdvPayments(finID);
		List<ManualAdvise> manualAdvises = manualAdviseDAO.getPreviousAdvPayments(finID);

		BigDecimal previousAdvInt = BigDecimal.ZERO;
		BigDecimal previousAdvEmi = BigDecimal.ZERO;

		Map<String, Long> map = new HashMap<>();

		for (ManualAdvise manualAdvise : manualAdvises) {
			if (!AdvanceRuleCode.ADVINT.name().equals(manualAdvise.getFeeTypeCode())) {
				continue;
			}

			if (AdviseType.isReceivable(manualAdvise.getAdviseType())) {
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

			if (AdviseType.isReceivable(manualAdvise.getAdviseType())) {
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
				// createPayableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
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
				// createPayableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			} else if (advPayment.compareTo(previousAdvInt) > 0) {
				adviseAmount = previousAdvInt.subtract(advPayment);
				advancePayment.setRequestedAmt(adviseAmount);
				excessAmountMovement(advancePayment, null, AccountConstants.TRANTYPE_CREDIT);
				createReceivableAdvise(finReference, valueDate, feeTypeId, adviseAmount, lastMntBy);
			}
		}
	}

	private void createReceivableAdvise(String finReference, Date valueDate, long feeTypeID, BigDecimal adviseAmount,
			Long lastMntBy) {
		logger.debug(Literal.ENTERING);

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		manualAdvise.setAdviseType(AdviseType.RECEIVABLE.id());
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

	public long excessAmountMovement(AdvancePayment advancePayment, Long receiptID, String txnType) {
		FinanceMain fm = advancePayment.getFinanceMain();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		String adviceType = advancePayment.getAdvancePaymentType();
		AdvanceRuleCode advanceType = AdvanceRuleCode.getRule(adviceType);

		BigDecimal reqAmount = advancePayment.getRequestedAmt();
		FinExcessAmount excess = finExcessAmountDAO.getFinExcessAmount(finID, adviceType);

		if (reqAmount == null) {
			reqAmount = BigDecimal.ZERO;
		}

		if (excess == null) {
			excess = new FinExcessAmount();
		}

		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal utilisedAmt = excess.getUtilisedAmt();
		BigDecimal reservedAmt = excess.getReservedAmt();

		if (AccountConstants.TRANTYPE_CREDIT.equals(txnType)) {
			amount = excess.getAmount().add(reqAmount);
		} else {
			amount = excess.getAmount().subtract(reqAmount);
		}

		excess.setFinID(finID);
		excess.setFinReference(finReference);
		excess.setAmountType(adviceType);
		excess.setAmount(amount);
		excess.setReservedAmt(reservedAmt);
		excess.setUtilisedAmt(utilisedAmt);
		excess.setBalanceAmt(amount.subtract(utilisedAmt).subtract(reservedAmt));

		if (excess.getExcessID() == Long.MIN_VALUE || excess.getExcessID() == 0) {
			excess.setReceiptID(null);
			excess.setValueDate(SysParamUtil.getAppDate());
			excess.setPostDate(excess.getValueDate());
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

	public BigDecimal getBalAdvIntAmt(FinanceMain fm, Date schDate) {
		if (DateUtil.compare(schDate, fm.getMaturityDate()) <= 0) {
			return finExcessAmountDAO.getBalAdvIntAmt(fm.getFinReference());
		}
		return BigDecimal.ZERO;
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
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setAdvancePaymentDetailDAO(AdvancePaymentDetailDAO advancePaymentDetailDAO) {
		this.advancePaymentDetailDAO = advancePaymentDetailDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}
}
