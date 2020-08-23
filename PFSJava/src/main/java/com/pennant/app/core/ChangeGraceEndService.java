/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ChangeGraceEndService.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.rits.cloning.Cloner;

public class ChangeGraceEndService extends ServiceHelper {

	private static final long serialVersionUID = -6254138886117514225L;
	private static Logger logger = Logger.getLogger(ChangeGraceEndService.class);

	private AccrualService accrualService;

	/**
	 * Auto Increment Grace End in EOD Process
	 * 
	 * @param custEODEvent
	 * @throws Exception
	 */
	public void processChangeGraceEnd(CustEODEvent custEODEvent) {

		Date eodValueDate = custEODEvent.getEodValueDate();
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			// Full Disbursement, Other than Grace End Date
			FinanceMain financeMain = finEODEvent.getFinanceMain();
			/*
			 * if (!(financeMain.isAllowGrcPeriod() && financeMain.getGrcPeriodEndDate().compareTo(eodValueDate) == 0 &&
			 * financeMain.getFinAssetValue().compareTo(financeMain.getFinCurrAssetValue()) != 0)) {
			 * 
			 * continue; }
			 */
			//Above condition was splitted as separate conditions for better understanding
			//If Grace is not allowed
			if (!financeMain.isAllowGrcPeriod()) {
				continue;
			}

			//If it is Fully Disbursed
			if (!(financeMain.getFinAssetValue().compareTo(financeMain.getFinCurrAssetValue()) != 0)) {
				continue;
			}

			//Threshold Gestation Period less than future grace emi count
			int thrldtoMaintainGrcPrd = finEODEvent.getFinType().getThrldtoMaintainGrcPrd();
			if (thrldtoMaintainGrcPrd > 0) {
				int pendingGraceEMICount = 0;
				boolean duefound = false;
				boolean thrldfound = false;

				for (FinanceScheduleDetail scheduleDetail : finEODEvent.getFinanceScheduleDetails()) {
					//Checking weather the schedule date has due on EOD date.
					if (scheduleDetail.getSchDate().compareTo(eodValueDate) == 0) {
						duefound = true;
						pendingGraceEMICount += 1;
					}
					//if schedule due found on eod value date(5-march-2020(Schedule due ) == 5-march-2020(EOD))	
					if (duefound) {
						//checking schedule date is greater than eod date and less than grace end date for pending grace emi count
						if (scheduleDetail.getSchDate().compareTo(eodValueDate) > 0
								&& scheduleDetail.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
							pendingGraceEMICount += 1;
						}
						//if no due found and schedule is greater than grace end date	
					} else if (!duefound
							&& scheduleDetail.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) > 0) {
						break;
					}
					//we need to break the loop if future  emi's are greater than Threshold Gestation Period
					if (pendingGraceEMICount >= thrldtoMaintainGrcPrd) {
						thrldfound = true;
						break;
					}
				}
				//Pending emi count is greater than threshold count
				if (thrldfound) {
					continue;
				}
				//if there is no pending emi's
				if (pendingGraceEMICount == 0) {
					continue;
				}
			} else {
				//If EOD value date is other than grace end date
				if (!(financeMain.getGrcPeriodEndDate().compareTo(eodValueDate) == 0)) {
					continue;
				}
			}

			// Allow AutoIncGrcEndDate
			FinanceProfitDetail finProfitDetail = finEODEvent.getFinProfitDetail();
			FinanceType finType = getFinanceType(finEODEvent.getFinanceMain().getFinType());
			finEODEvent.setFinType(finType);

			if (!(financeMain.isAutoIncGrcEndDate()
					&& finProfitDetail.getNOAutoIncGrcEnd() < finType.getMaxAutoIncrAllowed())) {

				continue;
			}

			AEEvent aeEvent = null;
			boolean isChangeGrcEndSuccess = true;
			FinScheduleData finScheduleData = prepareFinScheduleData(finEODEvent);

			try {

				// Schedule Re Calculation Based on New Grace End
				finScheduleData = changeGraceEnd(finScheduleData, false);

				// Posting Entries Related Profit Change
				if (finScheduleData.getErrorDetails().isEmpty()) {
					aeEvent = getChangeGrcEndPostings(finScheduleData);
				} else {
					isChangeGrcEndSuccess = false;
				}

			} catch (Exception e) {
				isChangeGrcEndSuccess = false;
			}

			if (isChangeGrcEndSuccess) {

				finScheduleData.getFinanceMain().setScheduleChange(true);
				finProfitDetail.setNOAutoIncGrcEnd(finProfitDetail.getNOAutoIncGrcEnd() + 1);

				// Data Saving Parameters
				finEODEvent.setUpdFinMain(true);
				finEODEvent.setUpdRepayInstruct(true);
				finEODEvent.setUpdFinSchdForChangeGrcEnd(true);
				finEODEvent.setFinanceMain(finScheduleData.getFinanceMain());
				finEODEvent.setRepayInstructions(finScheduleData.getRepayInstructions());
				finEODEvent.setFinanceScheduleDetails(finScheduleData.getFinanceScheduleDetails());

				// Prepare FinServiceInstruction
				List<FinServiceInstruction> finServInstList = getFinServiceInstruction(finScheduleData);
				finEODEvent.setFinServiceInstructions(finServInstList);

				if (aeEvent != null) {
					finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
				}
			}
		}
	}

	/**
	 * Method for preparing data to end grace period after full disbursement
	 * 
	 * @param finScheduleData
	 */
	@SuppressWarnings("unused")
	public FinScheduleData changeGraceEnd(FinScheduleData finScheduleData, boolean isFullDisb) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		// Previous GrcPeriodEndDate
		Date newGrcEndDate = financeMain.getEventFromDate();
		Date prvGraceEnd = financeMain.getGrcPeriodEndDate();

		// Setting Details ---> 1. Grace Details

		int fddLockPeriod = financeType.getFddLockPeriod();
		if (financeMain.isAllowGrcPeriod() && !ImplementationConstants.APPLY_FDDLOCKPERIOD_AFTERGRACE) {
			fddLockPeriod = 0;
		}

		financeMain.setEventFromDate(formatDate(prvGraceEnd));

		// NextGrcPftDate
		Date nextGrcPftDate = FrequencyUtil.getNextDate(financeMain.getGrcPftFrq(), 1, financeMain.getFinStartDate(),
				HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate();

		nextGrcPftDate = formatDate(nextGrcPftDate);
		financeMain.setNextGrcPftDate(nextGrcPftDate);

		boolean includeStartDate = false;
		if (financeMain.getFinStartDate().compareTo(nextGrcPftDate) < 0) {
			includeStartDate = true;
		}

		// GraceEndDate AND GraceTerms
		if (isFullDisb) {

			financeMain.setGrcPeriodEndDate(formatDate(newGrcEndDate));

			financeMain.setGraceTerms(FrequencyUtil
					.getTerms(financeMain.getGrcPftFrq(), nextGrcPftDate, newGrcEndDate, includeStartDate, false)
					.getTerms());
		} else {

			int graceTerms = financeMain.getGraceTerms() + financeType.getGrcAutoIncrMonths();
			financeMain.setGraceTerms(graceTerms);

			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(financeMain.getGrcPftFrq(), graceTerms,
					nextGrcPftDate, HolidayHandlerTypes.MOVE_NONE, true, 0).getScheduleList();

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);

				newGrcEndDate = formatDate(calendar.getTime());
				financeMain.setGrcPeriodEndDate(newGrcEndDate);
			}
			scheduleDateList = null;
		}

		// NextGrcPftRvwDate
		if (financeMain.isAllowGrcPftRvw()) {

			RateDetail rateDetail = RateUtil.rates(financeMain.getGraceBaseRate(), financeMain.getFinCcy(),
					financeMain.getGraceSpecialRate(), financeMain.getGrcMargin(), financeType.getFInGrcMinRate(),
					financeType.getFinGrcMaxRate());

			// Date baseDate =
			// DateUtility.addDays(financeMain.getFinStartDate(),
			// rateDetail.getLockingPeriod());

			financeMain.setNextGrcPftRvwDate(FrequencyUtil
					.getNextDate(financeMain.getGrcPftRvwFrq(), 1, financeMain.getFinStartDate(),
							HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
					.getNextFrequencyDate());

			financeMain.setNextGrcPftRvwDate(formatDate(financeMain.getNextGrcPftRvwDate()));

			if (financeMain.getNextGrcPftRvwDate().after(financeMain.getGrcPeriodEndDate())) {
				financeMain.setNextGrcPftRvwDate(financeMain.getGrcPeriodEndDate());
			}
		}

		// NextGrcCpzDate
		if (financeMain.isAllowGrcCpz()) {

			financeMain.setAllowGrcCpz(true);

			financeMain.setNextGrcCpzDate(FrequencyUtil
					.getNextDate(financeMain.getGrcCpzFrq(), 1, financeMain.getFinStartDate(),
							HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
					.getNextFrequencyDate());

			financeMain.setNextGrcCpzDate(formatDate(financeMain.getNextGrcCpzDate()));

			if (financeMain.getNextGrcCpzDate().after(financeMain.getGrcPeriodEndDate())) {
				financeMain.setNextGrcCpzDate(financeMain.getGrcPeriodEndDate());
			}
		}

		// Setting Details ---> 2. Repay Details

		// NextRepayDate AND NextRepayPftDate
		if (financeMain.getRepayFrq() != null) {

			Date nextRepayDate = FrequencyUtil.getNextDate(financeMain.getRepayFrq(), 1,
					financeMain.getGrcPeriodEndDate(), HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
					.getNextFrequencyDate();

			financeMain.setNextRepayDate(formatDate(nextRepayDate));
		}

		if (financeMain.getRepayPftFrq() != null) {
			financeMain.setNextRepayPftDate(formatDate(financeMain.getNextRepayDate()));
		}

		// RepayRvwFrq AND NextRepayRvwDate
		if (financeMain.isAllowRepayRvw()) {

			RateDetail rateDetail = RateUtil.rates(financeMain.getRepayBaseRate(), financeMain.getFinCcy(),
					financeMain.getRepaySpecialRate(), financeMain.getRepayMargin(), financeType.getFInMinRate(),
					financeType.getFinMaxRate());

			// Date baseDate =
			// DateUtility.addDays(financeMain.getGrcPeriodEndDate(),
			// rateDetail.getLockingPeriod());

			financeMain
					.setNextRepayRvwDate(
							FrequencyUtil
									.getNextDate(financeMain.getRepayRvwFrq(), 1, financeMain.getGrcPeriodEndDate(),
											HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
									.getNextFrequencyDate());

			financeMain.setNextRepayRvwDate(formatDate(financeMain.getNextRepayRvwDate()));

		} else {
			financeMain.setRepayRvwFrq("");
		}

		// NextRepayCpzDate
		if (financeMain.isAllowRepayCpz()) {

			financeMain
					.setNextRepayCpzDate(
							FrequencyUtil
									.getNextDate(financeMain.getRepayCpzFrq(), 1, financeMain.getGrcPeriodEndDate(),
											HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
									.getNextFrequencyDate());

			financeMain.setNextRepayCpzDate(formatDate(financeMain.getNextRepayCpzDate()));

		} else {
			financeMain.setRepayCpzFrq("");
		}

		// MaturityDate AND NumberOfTerms
		if (financeMain.getRepayFrq() != null && financeMain.getNextRepayDate() != null) {

			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(financeMain.getRepayFrq(), financeMain.getNumberOfTerms(),
							financeMain.getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, true, 0)
					.getScheduleList();

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);

				financeMain.setMaturityDate(calendar.getTime());
				financeMain.setMaturityDate(formatDate(financeMain.getMaturityDate()));
			}
			scheduleDateList = null;

			financeMain.setNumberOfTerms(FrequencyUtil.getTerms(financeMain.getRepayFrq(),
					financeMain.getNextRepayDate(), financeMain.getMaturityDate(), true, true).getTerms());
		}

		financeMain.setDevFinCalReq(false);
		// financeMain.setChgDropLineSchd(true);

		// Schedule Re Calculation by Changing Grace End
		finScheduleData = ScheduleCalculator.changeGraceEnd(finScheduleData);

		if (finScheduleData.getErrorDetails() == null || finScheduleData.getErrorDetails().isEmpty()) {

			// Plan EMI Holidays Resetting after Change Grace Period End Date
			if (financeMain.isPlanEMIHAlw()) {

				financeMain.setEventFromDate(financeMain.getRecalFromDate());
				financeMain.setEventToDate(financeMain.getMaturityDate());
				financeMain.setRecalFromDate(financeMain.getRecalFromDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());

				financeMain.setEqualRepay(true);
				financeMain.setCalculateRepay(true);

				if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					finScheduleData = ScheduleCalculator.getFrqEMIHoliday(finScheduleData);
				} else {
					finScheduleData = ScheduleCalculator.getAdhocEMIHoliday(finScheduleData);
				}
			}

			financeMain.setScheduleRegenerated(true);
			finScheduleData.setSchduleGenerated(true);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Prepare Profit Change Posting Entries
	 * 
	 * 
	 * @param finSchdData
	 * @throws Exception
	 */
	public AEEvent getChangeGrcEndPostings(FinScheduleData finSchdData) throws Exception {

		Date valueDate = SysParamUtil.getAppDate();
		FinanceMain finMain = finSchdData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finSchdData.getFinanceScheduleDetails();

		// Get Profit Detail
		FinanceProfitDetail profitDetail = getFinanceProfitDetailDAO()
				.getFinProfitDetailsById(finMain.getFinReference());

		BigDecimal totalPftSchdOld = profitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzOld = profitDetail.getTotalPftCpz();

		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		newProfitDetail = accrualService.calProfitDetails(finMain, finSchdDetails, profitDetail, valueDate);

		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		// Amount Codes Details Preparation
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(finMain, profitDetail, finSchdDetails,
				AccountEventConstants.ACCEVENT_SCDCHG, valueDate, valueDate);

		Map<String, Object> dataMap = aeEvent.getDataMap();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		Map<String, Object> map = getFinanceMainDAO().getGLSubHeadCodes(finMain.getFinReference());

		amountCodes.setBusinessvertical((String) map.get("Businessvertical"));
		amountCodes.setAlwflexi(finMain.isAlwFlexi());
		amountCodes.setFinbranch(finMain.getFinBranch());
		amountCodes.setEntitycode(finMain.getEntityCode());

		if (amountCodes.getPftChg().compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}

		aeEvent.setPostDate(valueDate);
		aeEvent.setCustAppDate(valueDate);
		aeEvent.setFinType(finMain.getFinType());

		// TODO : Need to validate
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		long accountingID = getAccountingID(finMain, AccountEventConstants.ACCEVENT_SCDCHG);
		if (accountingID == Long.MIN_VALUE) {
			return null;
		} else {
			aeEvent.getAcSetIDList().add(accountingID);
		}

		//Postings Process and save all postings related to finance for one time accounts update
		aeEvent = postAccountingEOD(aeEvent);

		finSchdData.setPftChg(amountCodes.getPftChg());
		aeEvent.getReturnDataSet();

		return aeEvent;
	}

	/**
	 * Method for fetching Finance Schedule Data based on FinReference
	 * 
	 * @param finRef
	 * @param type
	 * @return
	 */
	public FinScheduleData prepareFinScheduleData(FinEODEvent finEODEvent) {

		FinScheduleData finSchData = new FinScheduleData();

		Cloner finEODCloner = new Cloner();
		FinEODEvent finEODEvt = finEODCloner.deepClone(finEODEvent);

		String finRef = finEODEvt.getFinanceMain().getFinReference();
		finSchData.setFinReference(finRef);

		finSchData.setFinanceMain(finEODEvt.getFinanceMain());
		finSchData.setFinanceType(finEODEvt.getFinType());
		finSchData.setFinanceScheduleDetails(finEODEvt.getFinanceScheduleDetails());
		//finSchData.setSubventionDetail(finEODEvt.getSubventionDetail());

		List<RepayInstruction> repayInstructions = getRepayInstructionDAO()
				.getRepayInstrEOD(finSchData.getFinReference());

		Cloner rpyInstCloner = new Cloner();
		finEODEvent.setOrgRepayInsts(rpyInstCloner.deepClone(repayInstructions));
		finSchData.setRepayInstructions(repayInstructions);

		// Finance Disbursement Details
		List<FinanceDisbursement> finDisbDetails = getFinanceDisbursementDAO()
				.getFinanceDisbursementDetails(finSchData.getFinReference(), "", false);

		finSchData.setDisbursementDetails(finDisbDetails);
		finEODEvent.setFinanceDisbursements(finDisbDetails);

		finEODCloner = null;
		rpyInstCloner = null;

		return finSchData;
	}

	/**
	 * 
	 * @param financeMain
	 * @return
	 */
	private List<FinServiceInstruction> getFinServiceInstruction(FinScheduleData finScheduleData) {

		FinServiceInstruction finServInst = new FinServiceInstruction();
		List<FinServiceInstruction> finServInstList = new ArrayList<FinServiceInstruction>();

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		finServInst.setFinReference(financeMain.getFinReference());
		finServInst.setFinEvent(FinanceConstants.FINSER_EVENT_CHGGRCEND);
		finServInst.setGrcPeriodEndDate(financeMain.getGrcPeriodEndDate());
		finServInst.setGrcTerms(financeMain.getGraceTerms());
		finServInst.setGrcPftFrq(financeMain.getGrcPftFrq());
		finServInst.setGrcCpzFrq(financeMain.getGrcCpzFrq());
		finServInst.setGrcRvwFrq(financeMain.getGrcPftRvwFrq());
		finServInst.setNextGrcRepayDate(financeMain.getNextGrcPftDate());
		finServInst.setSystemDate(DateUtility.getSysDate());
		finServInst.setAppDate(SysParamUtil.getAppDate());
		finServInst.setMakerAppDate(SysParamUtil.getAppDate());
		finServInst.setMakerSysDate(DateUtility.getSysDate());
		finServInst.setMaker(999); // EOD

		// PftChg is the POST AMOUNT in Posting entries 
		finServInst.setPftChg(finScheduleData.getPftChg());

		finServInstList.add(finServInst);
		return finServInstList;
	}

	// setters / getters

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}
}