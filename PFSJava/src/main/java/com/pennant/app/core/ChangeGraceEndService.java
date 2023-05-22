/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ChangeGraceEndService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 * *
 * Modified Date : 11-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;

public class ChangeGraceEndService extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(ChangeGraceEndService.class);

	private AccrualService accrualService;

	public void processChangeGraceEnd(CustEODEvent custEODEvent) {
		Date eodValueDate = custEODEvent.getEodValueDate();
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();
			if (!fm.isAllowGrcPeriod()) {
				continue;
			}

			if (!(fm.getFinAssetValue().compareTo(fm.getFinCurrAssetValue()) != 0)) {
				continue;
			}

			int thrldtoMaintainGrcPrd = finEODEvent.getFinType().getThrldtoMaintainGrcPrd();

			Date grcPeriodEndDate = fm.getGrcPeriodEndDate();

			if (thrldtoMaintainGrcPrd > 0) {
				int pendingGraceEMICount = 0;
				boolean duefound = false;
				boolean thrldfound = false;

				List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
				for (FinanceScheduleDetail schd : schedules) {
					Date schDate = schd.getSchDate();

					if (schDate.compareTo(eodValueDate) == 0) {
						duefound = true;
						pendingGraceEMICount += 1;
					}

					if (duefound) {
						if (schDate.compareTo(eodValueDate) > 0 && schDate.compareTo(grcPeriodEndDate) <= 0) {
							pendingGraceEMICount += 1;
						}
					} else if (!duefound && schDate.compareTo(grcPeriodEndDate) > 0) {
						break;
					}
					if (pendingGraceEMICount > thrldtoMaintainGrcPrd) {
						thrldfound = true;
						break;
					}
				}
				// Pending emi count is greater than threshold count
				if (thrldfound) {
					continue;
				}
				// if there is no pending emi's
				if (pendingGraceEMICount == 0) {
					continue;
				}
			} else {
				// If EOD value date is other than grace end date
				if (!(grcPeriodEndDate.compareTo(eodValueDate) == 0)) {
					continue;
				}
			}

			// Allow AutoIncGrcEndDate
			FinanceProfitDetail pfd = finEODEvent.getFinProfitDetail();
			FinanceType finType = getFinanceType(finEODEvent.getFinanceMain().getFinType());
			finEODEvent.setFinType(finType);

			if (!(fm.isAutoIncGrcEndDate() && pfd.getNOAutoIncGrcEnd() < finType.getMaxAutoIncrAllowed())) {

				continue;
			}

			AEEvent aeEvent = null;
			boolean isChangeGrcEndSuccess = true;
			FinScheduleData fsd = prepareFinScheduleData(finEODEvent);

			try {
				changeGraceEnd(fsd, false);

				if (fsd.getErrorDetails().isEmpty()) {
					aeEvent = getChangeGrcEndPostings(fsd);
				} else {
					isChangeGrcEndSuccess = false;
				}

			} catch (Exception e) {
				isChangeGrcEndSuccess = false;
			}

			if (isChangeGrcEndSuccess) {
				fsd.getFinanceMain().setScheduleChange(true);
				pfd.setNOAutoIncGrcEnd(pfd.getNOAutoIncGrcEnd() + 1);

				finEODEvent.setUpdFinMain(true);
				finEODEvent.setUpdRepayInstruct(true);
				finEODEvent.setUpdFinSchdForChangeGrcEnd(true);
				finEODEvent.setFinanceMain(fsd.getFinanceMain());
				finEODEvent.setRepayInstructions(fsd.getRepayInstructions());
				finEODEvent.setFinanceScheduleDetails(fsd.getFinanceScheduleDetails());

				// Prepare FinServiceInstruction
				List<FinServiceInstruction> finServInstList = getFinServiceInstruction(fsd);
				finEODEvent.setFinServiceInstructions(finServInstList);

				if (aeEvent != null) {
					finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
				}

				finEODEvent.setStepPolicyDetails(fsd.getStepPolicyDetails());
			}
		}
	}

	public void changeGraceEnd(FinScheduleData finScheduleData, boolean isFullDisb) {
		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		// Previous GrcPeriodEndDate
		Date newGrcEndDate = fm.getEventFromDate();
		Date prvGraceEnd = fm.getGrcPeriodEndDate();

		// Setting Details ---> 1. Grace Details

		int fddLockPeriod = financeType.getFddLockPeriod();
		if (fm.isAllowGrcPeriod() && !ImplementationConstants.APPLY_FDDLOCKPERIOD_AFTERGRACE) {
			fddLockPeriod = 0;
		}

		fm.setEventFromDate(formatDate(prvGraceEnd));

		// NextGrcPftDate
		Date nextGrcPftDate = FrequencyUtil.getNextDate(fm.getGrcPftFrq(), 1, fm.getFinStartDate(),
				HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate();

		nextGrcPftDate = formatDate(nextGrcPftDate);

		if (DateUtil.compare(nextGrcPftDate, newGrcEndDate) > 0) {
			nextGrcPftDate = newGrcEndDate;
		}

		fm.setNextGrcPftDate(nextGrcPftDate);

		boolean includeStartDate = false;

		if (DateUtil.compare(fm.getFinStartDate(), nextGrcPftDate) < 0) {
			includeStartDate = true;
		}

		// GraceEndDate AND GraceTerms
		if (isFullDisb) {

			if (fm.isEndGrcPeriodAftrFullDisb()) {
				fm.setGrcPeriodEndDate(formatDate(newGrcEndDate));
			}

			int prvGrcTerms = fm.getGraceTerms();
			fm.setGraceTerms(FrequencyUtil
					.getTerms(fm.getGrcPftFrq(), nextGrcPftDate, newGrcEndDate, includeStartDate, false).getTerms());
			changeStpDetails(finScheduleData, prvGrcTerms);
		} else {
			int prvGrcTerms = fm.getGraceTerms();
			int graceTerms = fm.getGraceTerms() + financeType.getGrcAutoIncrMonths();
			fm.setGraceTerms(graceTerms);

			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(fm.getGrcPftFrq(), graceTerms, nextGrcPftDate, HolidayHandlerTypes.MOVE_NONE, true, 0)
					.getScheduleList();

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);

				newGrcEndDate = formatDate(calendar.getTime());
				fm.setGrcPeriodEndDate(newGrcEndDate);
			}
			scheduleDateList = null;

			changeStpDetails(finScheduleData, prvGrcTerms);
		}

		// NextGrcPftRvwDate
		if (fm.isAllowGrcPftRvw()) {
			fm.setNextGrcPftRvwDate(FrequencyUtil.getNextDate(fm.getGrcPftRvwFrq(), 1, fm.getFinStartDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());

			fm.setNextGrcPftRvwDate(formatDate(fm.getNextGrcPftRvwDate()));

			if (fm.getNextGrcPftRvwDate().after(fm.getGrcPeriodEndDate())) {
				fm.setNextGrcPftRvwDate(fm.getGrcPeriodEndDate());
			}
		}

		// NextGrcCpzDate
		if (fm.isAllowGrcCpz()) {

			fm.setAllowGrcCpz(true);

			fm.setNextGrcCpzDate(FrequencyUtil.getNextDate(fm.getGrcCpzFrq(), 1, fm.getFinStartDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());

			fm.setNextGrcCpzDate(formatDate(fm.getNextGrcCpzDate()));

			if (fm.getNextGrcCpzDate().after(fm.getGrcPeriodEndDate())) {
				fm.setNextGrcCpzDate(fm.getGrcPeriodEndDate());
			}
		}

		// Setting Details ---> 2. Repay Details

		// NextRepayDate AND NextRepayPftDate
		if (fm.getRepayFrq() != null) {
			Date nextRepayDate = FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, fm.getGrcPeriodEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate();

			fm.setNextRepayDate(formatDate(nextRepayDate));
		}

		if (fm.getRepayPftFrq() != null) {
			fm.setNextRepayPftDate(formatDate(fm.getNextRepayDate()));
		}

		// RepayRvwFrq AND NextRepayRvwDate
		if (fm.isAllowRepayRvw()) {
			fm.setNextRepayRvwDate(FrequencyUtil.getNextDate(fm.getRepayRvwFrq(), 1, fm.getGrcPeriodEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());

			fm.setNextRepayRvwDate(formatDate(fm.getNextRepayRvwDate()));

		} else {
			fm.setRepayRvwFrq("");
		}

		// NextRepayCpzDate
		if (fm.isAllowRepayCpz()) {
			fm.setNextRepayCpzDate(FrequencyUtil.getNextDate(fm.getRepayCpzFrq(), 1, fm.getGrcPeriodEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());

			fm.setNextRepayCpzDate(formatDate(fm.getNextRepayCpzDate()));
		} else {
			fm.setRepayCpzFrq("");
		}

		// MaturityDate AND NumberOfTerms
		if (fm.getRepayFrq() != null && fm.getNextRepayDate() != null) {

			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(fm.getRepayFrq(), fm.getNumberOfTerms(),
					fm.getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, true, 0).getScheduleList();

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);

				fm.setMaturityDate(calendar.getTime());
				fm.setMaturityDate(formatDate(fm.getMaturityDate()));
			}
			scheduleDateList = null;

			fm.setNumberOfTerms(FrequencyUtil
					.getTerms(fm.getRepayFrq(), fm.getNextRepayDate(), fm.getMaturityDate(), true, true).getTerms());
		}

		fm.setDevFinCalReq(false);
		// financeMain.setChgDropLineSchd(true);

		// Schedule Re Calculation by Changing Grace End
		finScheduleData = ScheduleCalculator.changeGraceEnd(finScheduleData);

		if (finScheduleData.getErrorDetails() == null || finScheduleData.getErrorDetails().isEmpty()) {

			// Plan EMI Holidays Resetting after Change Grace Period End Date
			if (fm.isPlanEMIHAlw()) {
				if (!(fm.isStepFinance() && PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps()))) {
					fm.setEventFromDate(fm.getRecalFromDate());
					fm.setEventToDate(fm.getMaturityDate());
					fm.setRecalFromDate(fm.getRecalFromDate());
					fm.setRecalToDate(fm.getMaturityDate());
					fm.setRecalSchdMethod(fm.getScheduleMethod());
				}

				fm.setEqualRepay(true);
				fm.setCalculateRepay(true);

				if (StringUtils.equals(fm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					finScheduleData = ScheduleCalculator.getFrqEMIHoliday(finScheduleData);
				} else {
					finScheduleData = ScheduleCalculator.getAdhocEMIHoliday(finScheduleData);
				}
			}

			fm.setScheduleRegenerated(true);
			finScheduleData.setSchduleGenerated(true);
		}
	}

	// Modifying the Step policy details based on grace terms.
	private void changeStpDetails(FinScheduleData schdData, int prvGrcTerms) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();

		List<FinanceStepPolicyDetail> spdList = schdData.getStepPolicyDetails();

		if (fm.isStepFinance() && PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
				&& CollectionUtils.isNotEmpty(spdList)) {
			List<FinanceStepPolicyDetail> newSpdList = new ArrayList<>();
			int curGrcTerms = 0;
			boolean isGrcTenorIncr = false;
			List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();

			for (FinanceScheduleDetail fsd : fsdList) {
				if (fsd.isFrqDate() && DateUtil.compare(fsd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0
						&& !fsd.isDisbOnSchDate()) {
					curGrcTerms = curGrcTerms + 1;
				} else if (DateUtil.compare(fsd.getSchDate(), fm.getGrcPeriodEndDate()) > 0) {
					break;
				}
			}

			if (curGrcTerms != 0) {
				isGrcTenorIncr = isGrcTenorIncr(prvGrcTerms, fm, spdList, curGrcTerms);
			}

			if (!isGrcTenorIncr && curGrcTerms < prvGrcTerms) {
				int noOfGrcStps = 0;
				int redGrcStps = prvGrcTerms - curGrcTerms;
				spdList = spdList.stream()
						.sorted(Comparator.comparing(FinanceStepPolicyDetail::getStepSpecifier)
								.thenComparingInt(FinanceStepPolicyDetail::getStepNo).reversed())
						.collect(Collectors.toList());
				for (FinanceStepPolicyDetail spd : spdList) {

					if (!PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
						newSpdList.add(spd);
						continue;
					}

					if (curGrcTerms == 0) {
						continue;
					}

					BigDecimal tenorSplitPerc = BigDecimal.ZERO;

					if (redGrcStps > 0) {
						int remStps = spd.getInstallments() - redGrcStps;
						redGrcStps = redGrcStps - spd.getInstallments();
						if (remStps > 0) {
							spd.setInstallments(remStps);
							tenorSplitPerc = (new BigDecimal(spd.getInstallments()).multiply(new BigDecimal(100)))
									.divide(new BigDecimal(curGrcTerms), 2, RoundingMode.HALF_DOWN);
							spd.setTenorSplitPerc(tenorSplitPerc);
							newSpdList.add(spd);
							noOfGrcStps = noOfGrcStps + 1;
						}
					} else {
						tenorSplitPerc = (new BigDecimal(spd.getInstallments()).multiply(new BigDecimal(100)))
								.divide(new BigDecimal(curGrcTerms), 2, RoundingMode.HALF_DOWN);
						spd.setTenorSplitPerc(tenorSplitPerc);
						newSpdList.add(spd);
						noOfGrcStps = noOfGrcStps + 1;
					}
				}

				schdData.setStepPolicyDetails(newSpdList, true);
				schdData.getFinanceMain().setNoOfGrcSteps(noOfGrcStps);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean isGrcTenorIncr(int prvGrcTerms, FinanceMain fm, List<FinanceStepPolicyDetail> spdList,
			int curGrcTerms) {

		int stpGrcTerms = 0;
		boolean isGrcTenorIncr = false;

		for (FinanceStepPolicyDetail spd : spdList) {
			if (!PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
				continue;
			}

			fm.setGrcStps(true);
			BigDecimal tenorSplitPerc = (new BigDecimal(spd.getInstallments()).multiply(new BigDecimal(100)))
					.divide(new BigDecimal(curGrcTerms), 2, RoundingMode.HALF_DOWN);
			spd.setTenorSplitPerc(tenorSplitPerc);
			stpGrcTerms = stpGrcTerms + spd.getInstallments();

			if (curGrcTerms > prvGrcTerms && fm.getNoOfGrcSteps() == spd.getStepNo()) {
				isGrcTenorIncr = true;
				int remgrcTerms = curGrcTerms - prvGrcTerms;
				spd.setInstallments(spd.getInstallments() + remgrcTerms);
				tenorSplitPerc = (new BigDecimal(spd.getInstallments()).multiply(new BigDecimal(100)))
						.divide(new BigDecimal(curGrcTerms), 2, RoundingMode.HALF_DOWN);
				spd.setTenorSplitPerc(tenorSplitPerc);
				break;
			}

		}

		return isGrcTenorIncr;
	}

	public AEEvent getChangeGrcEndPostings(FinScheduleData fsd) {
		FinanceMain fm = fsd.getFinanceMain();

		long finID = fm.getFinID();

		EventProperties eventProperties = fm.getEventProperties();

		Date valueDate = null;
		if (eventProperties.isParameterLoaded()) {
			valueDate = eventProperties.getValueDate();
		} else {
			valueDate = SysParamUtil.getAppDate();
		}

		List<FinanceScheduleDetail> schedules = fsd.getFinanceScheduleDetails();

		// Get Profit Detail
		FinanceProfitDetail pd = financeProfitDetailDAO.getFinProfitDetailsById(finID);

		BigDecimal totalPftSchdOld = pd.getTotalPftSchd();
		BigDecimal totalPftCpzOld = pd.getTotalPftCpz();

		FinanceProfitDetail newProfitDetail = accrualService.calProfitDetails(fm, schedules, pd, valueDate);

		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		// Amount Codes Details Preparation
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, pd, schedules, AccountingEvent.SCDCHG, valueDate, valueDate);

		Map<String, Object> dataMap = aeEvent.getDataMap();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		Map<String, Object> map = fm.getGlSubHeadCodes();

		if (MapUtils.isEmpty(map)) {
			map = financeMainDAO.getGLSubHeadCodes(finID);
		}

		amountCodes.setBusinessvertical((String) map.get("BUSINESSVERTICAL"));
		amountCodes.setAlwflexi(fm.isAlwFlexi());
		amountCodes.setFinbranch(fm.getFinBranch());
		amountCodes.setEntitycode(fm.getEntityCode());

		if (amountCodes.getPftChg().compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}

		aeEvent.setPostDate(valueDate);
		aeEvent.setCustAppDate(valueDate);
		aeEvent.setFinType(fm.getFinType());

		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		Long accountingID = getAccountingID(fm, AccountingEvent.SCDCHG);
		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			return null;
		} else {
			aeEvent.getAcSetIDList().add(accountingID);
		}

		// Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);

		fsd.setPftChg(amountCodes.getPftChg());
		aeEvent.getReturnDataSet();

		return aeEvent;
	}

	private FinScheduleData prepareFinScheduleData(FinEODEvent finEODEvent) {
		FinScheduleData schdData = new FinScheduleData();
		FinEODEvent finEODEvt = finEODEvent.copyEntity();

		FinanceMain fm = finEODEvt.getFinanceMain();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);
		schdData.setFinanceType(finEODEvt.getFinType());
		schdData.setFinanceScheduleDetails(finEODEvt.getFinanceScheduleDetails());

		List<RepayInstruction> repayInstructions = repayInstructionDAO.getRepayInstrEOD(finID);

		List<RepayInstruction> repayIns = new ArrayList<>();
		for (RepayInstruction repay : repayIns) {
			repayIns.add(repay.copyEntity());
		}

		finEODEvent.setOrgRepayInsts(repayIns);
		schdData.setRepayInstructions(repayInstructions);

		if (fm.isStepFinance() && fm.getNoOfGrcSteps() > 0
				&& PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "", false));
		}

		List<FinanceDisbursement> fd = financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false);

		schdData.setDisbursementDetails(fd);
		finEODEvent.setFinanceDisbursements(fd);

		return schdData;
	}

	private List<FinServiceInstruction> getFinServiceInstruction(FinScheduleData schdData) {
		Date sysDate = DateUtil.getSysDate();

		FinServiceInstruction fsi = new FinServiceInstruction();
		List<FinServiceInstruction> fsiList = new ArrayList<>();

		FinanceMain fm = schdData.getFinanceMain();
		EventProperties eventProperties = fm.getEventProperties();

		Date appDate = null;
		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		fsi.setFinID(fm.getFinID());
		fsi.setFinReference(fm.getFinReference());
		fsi.setFinEvent(FinServiceEvent.CHGGRCEND);
		fsi.setGrcPeriodEndDate(fm.getGrcPeriodEndDate());
		fsi.setGrcTerms(fm.getGraceTerms());
		fsi.setGrcPftFrq(fm.getGrcPftFrq());
		fsi.setGrcCpzFrq(fm.getGrcCpzFrq());
		fsi.setGrcRvwFrq(fm.getGrcPftRvwFrq());
		fsi.setNextGrcRepayDate(fm.getNextGrcPftDate());

		fsi.setSystemDate(sysDate);
		fsi.setAppDate(appDate);
		fsi.setMakerAppDate(appDate);
		fsi.setMakerSysDate(sysDate);
		fsi.setMaker(999); // EOD

		// PftChg is the POST AMOUNT in Posting entries
		fsi.setPftChg(schdData.getPftChg());

		fsiList.add(fsi);
		return fsiList;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}
}