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
 *******************************************************************************************************
 * FILE HEADER *
 *******************************************************************************************************
 *
 * FileName : CDScheduleCalculator.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 22-08-2019 *
 * 
 * Modified Date : 22-08-2019 *
 * 
 * Description : Copied from CScheduleCalculator *
 * 
 ********************************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************************
 * 26-04-2011 Pennant 0.1 * * 10-05-2018 Satya 0.2 PSD - Ticket : 126189 * While doing Add Disbursement getting *
 * ArthemeticException in AccrualService due to * NoofDays is ZERO in newly added Schedule * 01-08-2018 Mangapathi 0.3
 * PSD - Ticket : 125445, 125588 * Mail Sub : Freezing Period, Dt : 30-May-2018 * To address Freezing period case when
 * schedule * term is in Presentment. * *
 * 
 * 05-12-2018 Pradeep Varma 0.4 Schedules sent for presentment should and * waiting for fate should be untouched for any
 * * schedule change * 05-12-2018 Pradeep Varma 0.5 Interest should not be left for future * adjustments based on loan
 * type flag * schedule change * 05-12-2018 Pradeep Varma 0.6 Adjut Terms while Rate Change * * *
 ********************************************************************************************************
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.FinServiceEvent;

public class CDScheduleCalculator {
	private static final Logger logger = LogManager.getLogger(CDScheduleCalculator.class);

	private FinScheduleData finScheduleData;

	// PROCESS METHODS IN SCHEDULE CALCULATOR
	public static final String PROC_GETCALSCHD = "procGetCalSchd";
	public static final String PROC_CHANGEREPAY = "procChangeRepay";
	public static final String PROC_RECALSCHD = "procReCalSchd";
	public static final String PROC_REBUILDSCHD = "reBuildSchd";

	public CDScheduleCalculator() {
		super();
	}

	/*
	 * #########################################################################
	 * 
	 * 
	 * #########################################################################
	 */

	public static FinScheduleData getCalSchd(FinScheduleData finScheduleData) {
		return new CDScheduleCalculator(PROC_GETCALSCHD, finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData changeRepay(FinScheduleData finScheduleData, BigDecimal amount) {
		return new CDScheduleCalculator(PROC_CHANGEREPAY, finScheduleData, amount).getFinScheduleData();
	}

	public static FinScheduleData reCalSchd(FinScheduleData finScheduleData) {
		return new CDScheduleCalculator(PROC_RECALSCHD, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	public static FinScheduleData reBuildSchd(FinScheduleData finScheduleData) {
		return new CDScheduleCalculator(PROC_REBUILDSCHD, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	public static FinScheduleData recalEarlyPaySchedule(FinScheduleData finScheduleData, Date earlyPayOnSchdl,
			Date earlyPayOnNextSchdl, BigDecimal earlyPayAmt) {
		return new CDScheduleCalculator(finScheduleData, earlyPayOnSchdl, earlyPayOnNextSchdl, earlyPayAmt)
				.getFinScheduleData();
	}

	// Constructors

	private CDScheduleCalculator(String method, FinScheduleData fsData) {
		logger.debug("Entering");

		if (StringUtils.equals(method, PROC_GETCALSCHD)) {
			setFinScheduleData(procGetCalSchd(fsData));
		}

		setFinanceTotals(fsData);

		logger.debug("Leaving");
	}

	private CDScheduleCalculator(String method, FinScheduleData fsData, BigDecimal amount) {
		logger.debug("Entering");

		if (StringUtils.equals(method, PROC_CHANGEREPAY)) {
			setFinScheduleData(procChangeRepay(fsData, amount));
		}

		if (StringUtils.equals(method, PROC_RECALSCHD)) {
			setFinScheduleData(procReCalSchd(fsData));
		}

		if (StringUtils.equals(method, PROC_REBUILDSCHD)) {
			setFinScheduleData(procRebuildSchd(fsData));
		}

		setFinanceTotals(fsData);
		logger.debug("Leaving");
	}

	private CDScheduleCalculator(FinScheduleData fsData, Date earlyPayOnSchdl, Date earlyPayOnNextSchdl,
			BigDecimal earlyPayAmt) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		fm.setProcMethod(FinServiceEvent.RECEIPT);
		fsData.getFinanceMain().setResetOrgBal(false);

		String receivedRecalMethod = fm.getRecalSchdMethod();
		String method = fm.getRecalSchdMethod();
		if (StringUtils.equals(CalculationConstants.EARLYPAY_ADJMUR, method)) {
			method = CalculationConstants.RPYCHG_ADJMDT;
		}

		fm.setEventFromDate(earlyPayOnSchdl);
		fm.setEventToDate(earlyPayOnSchdl);
		fm.setRecalType(method);

		fsData = resetRecalData(fsData, earlyPayOnSchdl, earlyPayAmt, fm.getReceiptPurpose());
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		if (fm.getIndexMisc() >= 0) {
			FinanceScheduleDetail curSchd = fsdList.get(fm.getIndexMisc());

			if (StringUtils.equals(curSchd.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
				earlyPayAmt = earlyPayAmt.add(curSchd.getPrincipalSchd()).add(curSchd.getProfitSchd());
			} else {
				earlyPayAmt = earlyPayAmt.add(curSchd.getPrincipalSchd());
			}
		}

		if (StringUtils.equals(CalculationConstants.RPYCHG_ADJMDT, method)
				|| StringUtils.equals(CalculationConstants.EARLYPAY_ADMPFI, method)) {

			fm.setRecalToDate(fm.getMaturityDate());

			fsData = changeRepay(fsData, earlyPayAmt);
			fsdList = fsData.getFinanceScheduleDetails();

			int fsdSize = fsdList.size();
			Date eventToDate = fm.getMaturityDate();

			for (int iFsd = fsdSize - 1; iFsd >= 0; iFsd--) {
				FinanceScheduleDetail curSchd = fsdList.get(iFsd);
				if ((curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0)) {
					fsdList.remove(iFsd);
				} else {
					eventToDate = curSchd.getSchDate();
					break;
				}
			}

			fm.setMaturityDate(eventToDate);

			if (StringUtils.equals(CalculationConstants.EARLYPAY_ADMPFI, method)) {
				fm.setEventToDate(eventToDate);
				fsdList = sortSchdDetails(fsdList);
				fsData.setFinanceScheduleDetails(fsdList);
			}

		} else if (StringUtils.equals(CalculationConstants.EARLYPAY_RECRPY, method)) {

			fm.setRecalToDate(fm.getMaturityDate());
			fsData = changeRepay(fsData, earlyPayAmt);

			fm.setEventFromDate(earlyPayOnNextSchdl);
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			fsData = reCalSchd(fsData);

		}

		setFinanceTotals(fsData);

		if (StringUtils.equals(CalculationConstants.EARLYPAY_ADJMUR, receivedRecalMethod)) {
			method = receivedRecalMethod;
		}

		setFinScheduleData(fsData);
		logger.debug("Leaving");
	}

	/*
	 * ######################################################################### MAIN METHODS
	 * #########################################################################
	 */

	/*
	 * ========================================================================= Method : procGetCalSchd Description :
	 * GET CALCULATED SCHEDULE Process: This method will be be called only at the time of initial schedule creation by
	 * BUILD SCHEDULE FUNCTION =========================================================================
	 */

	private FinScheduleData procGetCalSchd(FinScheduleData fsData) {
		logger.debug("Entering");
		FinanceMain fm = fsData.getFinanceMain();

		fm.setEventFromDate(fm.getFinStartDate());
		fm.setEventToDate(fm.getMaturityDate());
		fm.setRecalFromDate(fm.getFinStartDate());
		fm.setRecalToDate(fm.getMaturityDate());

		// PREPARE FIND SCHDULE DATA
		fsData = preapareFinSchdData(fsData);
		fsData = calSchdProcess(fsData, true);

		// Reset Schedule Event Start & End Dates
		fm.setEventFromDate(fm.getFinStartDate());
		fm.setEventToDate(fm.getMaturityDate());

		setFinScheduleData(fsData);

		logger.debug("Leaving");
		return fsData;
	}

	/*
	 * ========================================================================= Method : procReCalSchd Description : Re
	 * Calculate schedule from a given date to end date
	 * =========================================================================
	 */
	private FinScheduleData procReCalSchd(FinScheduleData fsData) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		Date evtFromDate = fm.getRecalFromDate();
		fm.setEventFromDate(evtFromDate);
		fm.setEventToDate(evtFromDate);
		fsData = setRecalAttributes(fsData, PROC_RECALSCHD, BigDecimal.ZERO, BigDecimal.ZERO);
		fsData = calSchdProcess(fsData, false);

		fm.setScheduleMaintained(true);

		logger.debug("Leaving");
		return fsData;
	}

	/*
	 * ========================================================================= This method on;y recalculates the
	 * schedule without any changes in schedule method and amounts
	 * =========================================================================
	 */
	private FinScheduleData procRebuildSchd(FinScheduleData fsData) {
		logger.debug("Entering");
		FinanceMain fm = fsData.getFinanceMain();
		Date evtFromDate = fm.getRecalFromDate();
		fm.setEventFromDate(evtFromDate);
		fm.setEventToDate(evtFromDate);
		fsData = calSchdProcess(fsData, false);
		logger.debug("Leaving");
		return fsData;
	}

	private FinScheduleData addSchdRcd(FinScheduleData schdData, Date newSchdDate, int idxPrv) {
		FinanceScheduleDetail prvSchd = schdData.getFinanceScheduleDetails().get(idxPrv);
		FinanceMain fm = schdData.getFinanceMain();

		FinanceScheduleDetail fsd = new FinanceScheduleDetail();
		fsd.setFinID(fm.getFinID());
		fsd.setFinReference(fm.getFinReference());
		fsd.setBpiOrHoliday("");
		fsd.setSchDate(newSchdDate);
		fsd.setDefSchdDate(newSchdDate);
		fsd.setSchSeq(1);

		fsd.setBaseRate(prvSchd.getBaseRate());
		fsd.setSplRate(prvSchd.getSplRate());
		fsd.setMrgRate(prvSchd.getMrgRate());
		fsd.setActRate(prvSchd.getActRate());
		fsd.setCalculatedRate(prvSchd.getCalculatedRate());
		fsd.setSchdMethod(prvSchd.getSchdMethod());
		fsd.setPftDaysBasis(prvSchd.getPftDaysBasis());
		fsd.setClosingBalance(prvSchd.getClosingBalance());
		fsd.setNoOfDays(DateUtil.getDaysBetween(newSchdDate, prvSchd.getSchDate()));
		fsd.setDayFactor(CalculationUtil.getInterestDays(prvSchd.getSchDate(), newSchdDate, fsd.getPftDaysBasis()));

		schdData.getFinanceScheduleDetails().add(fsd);
		schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));
		return schdData;
	}

	/*
	 * ========================================================================= Method : procChangeRepay Description :
	 * CHANGE REPAY AMOUNT =========================================================================
	 */
	private FinScheduleData procChangeRepay(FinScheduleData fsData, BigDecimal repayAmount) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		String reqSchdMethod = fm.getRecalSchdMethod();

		Date evtFromDate = fm.getEventFromDate();
		Date evtToDate = fm.getEventToDate();

		getSchdMethod(fsData);

		int fsdSize = fsData.getFinanceScheduleDetails().size();
		int idxPrv = 0;
		boolean isRepaymentFoundInSD = false;

		Date schdDate = new Date();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int iFsd = 0; iFsd < fsdSize; iFsd++) {
			curSchd = fsdList.get(iFsd);
			schdDate = curSchd.getSchDate();

			if (DateUtil.compare(schdDate, evtFromDate) > 0) {
				break;
			}

			if (DateUtil.compare(schdDate, evtFromDate) == 0) {
				curSchd.setPftOnSchDate(true);
				curSchd.setRepayOnSchDate(true);
				isRepaymentFoundInSD = true;
				break;
			}

			idxPrv = iFsd;
		}

		if (!isRepaymentFoundInSD) {
			fsData = addSchdRcd(fsData, evtFromDate, idxPrv);
			curSchd = fsdList.get(idxPrv + 1);
			curSchd.setPftOnSchDate(true);
			curSchd.setRepayOnSchDate(true);
			curSchd.setRepayAmount(repayAmount);
		}

		fsData = setRpyInstructDetails(fsData, evtFromDate, evtToDate, repayAmount, reqSchdMethod);
		fm.setRecalSchdMethod(fm.getScheduleMethod());
		fsData = setRecalAttributes(fsData, PROC_CHANGEREPAY, BigDecimal.ZERO, repayAmount);

		fsData = calSchdProcess(fsData, false);
		fm.setScheduleMaintained(true);

		logger.debug("Leaving");
		return fsData;
	}

	/*
	 * #########################################################################
	 * 
	 * SUB METHODS
	 * 
	 * #########################################################################
	 */

	/*
	 * ========================================================================= PREPARE FinSchedule
	 * =========================================================================
	 */
	private FinScheduleData preapareFinSchdData(FinScheduleData fsData) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		// Set Default scheduled date and schedule method first time
		for (FinanceScheduleDetail curSchd : fsdList) {
			curSchd.setDefSchdDate(curSchd.getSchDate());
		}

		if (fm.getLastRepayDate() == null) {
			fm.setLastRepayDate(fm.getFinStartDate());
		}

		if (fm.getLastRepayCpzDate() == null) {
			fm.setLastRepayCpzDate(fm.getGrcPeriodEndDate());
		}

		if (fm.getLastRepayPftDate() == null) {
			fm.setLastRepayPftDate(fm.getGrcPeriodEndDate());
		}

		if (fm.getLastRepayRvwDate() == null) {
			fm.setLastRepayRvwDate(fm.getFinStartDate());
		}

		if (!fm.isAllowRepayRvw()) {
			fm.setNextRepayRvwDate(fm.getMaturityDate());
		}

		if (!fm.isAllowRepayCpz()) {
			fm.setNextRepayCpzDate(fm.getMaturityDate());
		}

		fm.setNextGrcPftRvwDate(fm.getGrcPeriodEndDate());
		fm.setNextGrcCpzDate(fm.getGrcPeriodEndDate());
		fm.setRecalSchdMethod(fm.getScheduleMethod());
		fsData = setRpyInstructDetails(fsData, fm.getNextRepayPftDate(), fm.getMaturityDate(), BigDecimal.ZERO,
				fm.getScheduleMethod());

		logger.debug("Leaving");
		return fsData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setRpyInstructDetails Description : Set Repay Instruction Details Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setRpyInstructDetails(FinScheduleData schdData, Date fromDate, Date toDate,
			BigDecimal repayAmount, String schdMethod) {
		logger.debug("Entering");

		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();

		BigDecimal nextRIAmount = BigDecimal.ZERO;
		Date nextRIDate = null;
		String nextRISchdMethod = null;
		boolean isAddNewRI = true;
		int riIndex = -1;

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Find next date for instruction
		if (DateUtil.compare(toDate, fm.getMaturityDate()) >= 0) {
			nextRIDate = fm.getMaturityDate();
		} else {
			int fsdSize = fsdList.size();
			FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

			for (int iFsd = 0; iFsd < fsdSize; iFsd++) {
				curSchd = fsdList.get(iFsd);
				if (curSchd.getSchDate().after(toDate) && (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())) {
					nextRIDate = curSchd.getSchDate();
					nextRISchdMethod = curSchd.getSchdMethod();
					break;
				}
			}

			// Next instruction amount and schedule method
			sortRepayInstructions(schdData.getRepayInstructions());
			if (nextRIDate != null) {
				riIndex = fetchRpyInstruction(schdData, nextRIDate);
			}

			if (riIndex >= 0) {
				nextRIAmount = schdData.getRepayInstructions().get(riIndex).getRepayAmount();
				nextRISchdMethod = schdData.getRepayInstructions().get(riIndex).getRepaySchdMethod();
			}
		}

		List<RepayInstruction> riList = schdData.getRepayInstructions();
		RepayInstruction curRI = new RepayInstruction();

		// Remove any instructions between fromdate and todate
		for (int iRI = 0; iRI < riList.size(); iRI++) {
			curRI = riList.get(iRI);

			if ((DateUtil.compare(curRI.getRepayDate(), fromDate) >= 0
					&& DateUtil.compare(curRI.getRepayDate(), toDate) <= 0)) {
				riList.remove(iRI);
				iRI = iRI - 1;
			}

			if (DateUtil.compare(curRI.getRepayDate(), nextRIDate) == 0) {
				isAddNewRI = false;
			}
		}

		sortRepayInstructions(riList);

		// Add repay instructions on from date
		RepayInstruction newRI = new RepayInstruction();
		newRI.setRepayDate(fromDate);
		newRI.setRepayAmount(repayAmount);
		newRI.setRepaySchdMethod(schdMethod);
		newRI.setFinID(finID);
		newRI.setFinReference(finReference);

		schdData.getRepayInstructions().add(newRI);

		// Add (reset) repay instruction after todate
		if (DateUtil.compare(toDate, fm.getMaturityDate()) >= 0 || !isAddNewRI) {
			schdData.setRepayInstructions(sortRepayInstructions(schdData.getRepayInstructions()));
			return schdData;
		}

		if (DateUtil.compare(nextRIDate, fromDate) > 0) {
			newRI = new RepayInstruction();
			newRI.setFinID(finID);
			newRI.setFinReference(finReference);
			newRI.setRepayDate(nextRIDate);
			newRI.setRepayAmount(nextRIAmount);
			newRI.setRepaySchdMethod(nextRISchdMethod);
			schdData.getRepayInstructions().add(newRI);
		}

		sortRepayInstructions(riList);
		schdData.setRepayInstructions(riList);
		logger.debug("Leaving");
		return schdData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : getRpyInstructDetails Description : Get Repay Instruction Details Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData getRpyInstructDetails(FinScheduleData fsData) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		List<RepayInstruction> riList = fsData.getRepayInstructions();
		int riSize = fsData.getRepayInstructions().size();
		BigDecimal riAmount = BigDecimal.ZERO;

		Date fromDate = fm.getFinStartDate();
		Date toDate = fm.getMaturityDate();
		String fromSchdMethod = null;
		String toSchdMethod = null;

		fm.setIndexStart(0);

		for (int iRI = 0; iRI < riSize; iRI++) {
			RepayInstruction curRI = riList.get(iRI);

			if (iRI == 0) {
				fromDate = curRI.getRepayDate();
				riAmount = curRI.getRepayAmount();
				fromSchdMethod = curRI.getRepaySchdMethod();
				continue;
			}

			toDate = curRI.getRepayDate();
			toSchdMethod = curRI.getRepaySchdMethod();

			fsData = setRpyChanges(fsData, fromDate, toDate, riAmount, fromSchdMethod);
			fromDate = toDate;
			fromSchdMethod = toSchdMethod;

			riAmount = curRI.getRepayAmount();
		}

		if (DateUtil.compare(toDate, fm.getMaturityDate()) <= 0) {
			toDate = fm.getMaturityDate();
			setRpyChanges(fsData, fromDate, toDate, riAmount, fromSchdMethod);
		}

		logger.debug("Leaving");
		return fsData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : fetchRpyInstruction Description : Fetch Repay Instruction index by date Process :
	 * ________________________________________________________________________________________________________________
	 */

	private int fetchRpyInstruction(FinScheduleData fsData, Date riDate) {

		int riSize = fsData.getRepayInstructions().size();
		int idxRI = -1;

		for (int iRI = 0; iRI < riSize; iRI++) {
			RepayInstruction curInstruction = fsData.getRepayInstructions().get(iRI);

			if (curInstruction.getRepayDate().after(riDate)) {
				break;
			}

			idxRI = iRI;

		}

		return idxRI;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setRpyChanges Description : Set Repay Changes Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setRpyChanges(FinScheduleData fsData, Date fromDate, Date toDate, BigDecimal riAmount,
			String schdMethod) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		int sdSize = fsdList.size();
		int indexStart = fm.getIndexStart();

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int iFsd = indexStart; iFsd < sdSize; iFsd++) {
			curSchd = fsdList.get(iFsd);
			Date curSchdDate = curSchd.getSchDate();

			// Added for setting Schedule method in case of Different
			// frequencies for PFT,CPZ & RVW
			if (DateUtil.compare(curSchdDate, fromDate) < 0) {
				if (StringUtils.isEmpty(curSchd.getSchdMethod())) {
					curSchd.setSchdMethod(fm.getScheduleMethod());
				}
			}

			if (DateUtil.compare(curSchdDate, fromDate) >= 0) {
				curSchd.setSchdMethod(schdMethod);
				boolean isFreezeSchd = false;

				if (curSchd.getPresentmentId() != 0
						&& !StringUtils.equals(fm.getProcMethod(), FinServiceEvent.RECEIPT)) {
					isFreezeSchd = true;
				}

				if (curSchd.isRepayOnSchDate() && !isFreezeSchd) {
					if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
						curSchd.setRepayAmount(riAmount);
					} else if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)
							|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCPZ)) {
						curSchd.setRepayAmount(BigDecimal.ZERO);
					} else if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI_PFT)
							|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI)) {
						curSchd.setPrincipalSchd(riAmount);
					}
				}
			} else if (DateUtil.compare(curSchd.getSchDate(), toDate) >= 0) {
				indexStart = iFsd;
				break;
			}
		}

		fm.setIndexStart(indexStart);
		logger.debug("Leaving");
		return fsData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setFinanceTotals Description: Set Finance Totals after Grace and Repayment schedules calculation
	 * ________________________________________________________________________________________________________________
	 */
	private FinScheduleData setFinanceTotals(FinScheduleData fsData) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		// FIXME: PV: 13MAY17: It is kept on the assumption reqMaturity fields
		// in not used any where else
		if (fm.isNewRecord() || StringUtils.equals(fm.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			fm.setReqMaturity(fm.getCalMaturity());
		}

		// TODO: PV. Consumer Durabales.. To be evrified
		FeeScheduleCalculator.feeSchdBuild(fsData);

		boolean isFirstAdjSet = false;
		int sdSize = fsdList.size();
		fm.setTotalGraceCpz(BigDecimal.ZERO);
		fm.setTotalGracePft(BigDecimal.ZERO);
		fm.setTotalGrossGrcPft(BigDecimal.ZERO);
		fm.setTotalCpz(BigDecimal.ZERO);
		fm.setTotalProfit(BigDecimal.ZERO);
		fm.setTotalGrossPft(BigDecimal.ZERO);
		fm.setTotalRepayAmt(BigDecimal.ZERO);
		fm.setSchdIndex(0);
		fm.setAdjOrgBal(BigDecimal.ZERO);
		fm.setRemBalForAdj(BigDecimal.ZERO);
		fm.setDevFinCalReq(false);

		FinanceScheduleDetail curSchd = fsdList.get(0);
		curSchd.setSchdMethod(fsdList.get(1).getSchdMethod());
		Date schdDate = new Date();
		int instNumber = 0;

		for (int iFsd = 0; iFsd < sdSize; iFsd++) {
			curSchd = fsdList.get(iFsd);
			schdDate = curSchd.getSchDate();

			fm.setTotalCpz(fm.getTotalCpz().add(curSchd.getCpzAmount()));
			fm.setTotalProfit(fm.getTotalProfit().add(curSchd.getProfitSchd()));
			fm.setTotalGrossPft(fm.getTotalGrossPft().add(curSchd.getProfitSchd()));
			fm.setTotalRepayAmt(fm.getTotalRepayAmt().add(curSchd.getRepayAmount()));

			if (curSchd.isRepayOnSchDate()) {
				if (!isFirstAdjSet) {
					fm.setFirstRepay(curSchd.getRepayAmount());
					isFirstAdjSet = true;
				}

				fm.setLastRepay(curSchd.getRepayAmount());
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				if (curSchd.getRepayAmount().compareTo(curSchd.getPartialPaidAmt()) != 0 || curSchd.isFrqDate()) {
					instNumber = instNumber + 1;
					curSchd.setInstNumber(instNumber);
				} else {
					curSchd.setInstNumber(0);
				}
			} else {
				curSchd.setInstNumber(0);
			}

			if (DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) < 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
			} else if (DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) == 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
			} else if (DateUtil.compare(schdDate, fm.getMaturityDate()) < 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
			} else if (DateUtil.compare(schdDate, fm.getMaturityDate()) == 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
			}
		}

		fm.setTotalGrossPft(fm.getTotalProfit());
		IRRCalculator.calculateXIRRAndIRR(fsData, null, false);

		logger.debug("Leaving");
		return fsData;
	}

	/*
	 * ************************************************************************* Method : calSchdProcess Description :
	 * Calculate Schedule Process Process : *************************************************************************
	 */
	private FinScheduleData calSchdProcess(FinScheduleData fsData, boolean isFirstRun) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();

		// START PROCESS
		fsData = getRpyInstructDetails(fsData);
		fsData = graceSchdCal(fsData);

		if (isFirstRun) {
			fsData = prepareFirstSchdCal(fsData);
			fsData = getRpyInstructDetails(fsData);
		}

		fsData = repaySchdCal(fsData);

		if (fm.isEqualRepay() && fm.isCalculateRepay()
				&& !StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)) {
			fsData = calEqualPayment(fsData);
		}

		logger.debug("Leaving");
		return fsData;

	}

	/*
	 * ************************************************************************* Method : graceSchdCal
	 * *************************************************************************
	 */
	private FinScheduleData graceSchdCal(FinScheduleData fsData) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		fm.setPftForSelectedPeriod(BigDecimal.ZERO);

		if (fm.getRecalIdx() < 0) {
			fsData = setRecalIndex(fsData);
		}

		prepareFirstGraceRcd(fsData);
		fm.setSchdIndex(0);
		logger.debug("Leaving");
		return fsData;
	}

	private FinScheduleData setRecalIndex(FinScheduleData fsData) {
		FinanceMain finMain = fsData.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = fsData.getFinanceScheduleDetails();
		Date evtFromDate = finMain.getEventFromDate();

		if (evtFromDate == null) {
			evtFromDate = finMain.getFinStartDate();
		}

		if (evtFromDate.compareTo(finMain.getFinStartDate()) < 0) {
			evtFromDate = finMain.getFinStartDate();
		}

		boolean isPftCpzFromReset = false;
		if (StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFTCPZ)) {
			isPftCpzFromReset = true;
		}

		finMain.setPftCpzFromReset(BigDecimal.ZERO);
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = schdDetails.size();
		for (int i = 0; i < sdSize; i++) {
			curSchd = schdDetails.get(i);
			if (curSchd.getSchDate().compareTo(evtFromDate) < 0) {

				if (isPftCpzFromReset && curSchd.isCpzOnSchDate() && curSchd.isRepayOnSchDate()) {
					finMain.setPftCpzFromReset(BigDecimal.ZERO);
				} else {
					finMain.setPftCpzFromReset(finMain.getPftCpzFromReset().add(curSchd.getCpzAmount()));
				}

				continue;
			}

			finMain.setRecalIdx(i);
			break;
		}

		if (finMain.getRecalIdx() < 0) {
			finMain.setRecalIdx(sdSize - 1);
		}

		return fsData;
	}

	/*
	 * ************************************************************************* Method : repaySchdCal Description :
	 * Repay period schedule calculation for reducing rate
	 * *************************************************************************
	 */

	private FinScheduleData prepareFirstGraceRcd(FinScheduleData fsData) {
		logger.debug("Entering");
		Promotion promotion = fsData.getPromotion();
		FinanceMain fm = fsData.getFinanceMain();
		FinanceScheduleDetail curSchd = fsData.getFinanceScheduleDetails().get(0);

		if (fm.getReqRepayAmount().compareTo(BigDecimal.ZERO) != 0) {
			fm.setCalculateRepay(false);
			fm.setEqualRepay(false);
		}

		int remainingTerms = promotion.getTenor() - promotion.getAdvEMITerms();
		BigDecimal emi = fm.getReqRepayAmount();

		if (emi.compareTo(BigDecimal.ZERO) == 0) {
			if (promotion.isOpenBalOnPV()) {
				emi = fm.getFinAmount().divide(BigDecimal.valueOf(promotion.getTenor()));
			} else {
				emi = approxPMT(fm, promotion.getActualInterestRate(), promotion.getTenor(), fm.getFinAmount(),
						BigDecimal.ZERO, 0);
			}

			emi = CalculationUtil.roundAmount(emi, fm.getCalRoundingMode(), fm.getRoundingTarget());
		}

		BigDecimal totPayment = BigDecimal.ZERO;
		BigDecimal advanceEMI = emi.multiply(BigDecimal.valueOf(promotion.getAdvEMITerms()));
		BigDecimal totDuePayment = BigDecimal.ZERO;
		BigDecimal subvention = BigDecimal.ZERO;
		BigDecimal presentValue = BigDecimal.ZERO;

		/*
		 * fm.setAdvanceEMI(advanceEMI); fm.setAdvTerms(promotion.getAdvEMITerms());
		 * fm.setNumberOfTerms(promotion.getTenor());
		 * 
		 * if (fm.getAdvTerms()>0) { fm.setAdvType("AE"); fm.setAdvStage("FE"); }
		 */
		fm.setDownPayment(advanceEMI);
		fm.setDownPayBank(advanceEMI);

		if (fm.getDownPayment().compareTo(BigDecimal.ZERO) > 0) {
			curSchd.setDownPaymentAmount(advanceEMI);
			curSchd.setDownpaymentOnSchDate(true);
		}

		if (promotion.isOpenBalOnPV()) {
			totPayment = fm.getFinAmount();
			totDuePayment = totPayment.subtract(advanceEMI);
			presentValue = CalculationUtil.calLoanPV(promotion.getActualInterestRate(), remainingTerms, emi,
					fm.getRepayFrq(), fm.getCalRoundingMode(), fm.getRoundingTarget());

			if (advanceEMI.compareTo(BigDecimal.ZERO) == 0
					&& promotion.getSubventionRate().compareTo(BigDecimal.ZERO) == 0) {
				presentValue = totPayment;
			} else if (advanceEMI.compareTo(BigDecimal.ZERO) > 0 && totPayment.compareTo(BigDecimal.ZERO) > 0
					&& (promotion.getActualInterestRate().compareTo(promotion.getSubventionRate()) != 0)) {
				presentValue = totDuePayment;
			}

			subvention = totDuePayment.subtract(presentValue);
		} else {
			if (promotion.getActualInterestRate().compareTo(BigDecimal.ZERO) == 0) {
				totPayment = fm.getFinAmount();
			} else {
				totPayment = emi.multiply(BigDecimal.valueOf(promotion.getTenor()));
			}
			if (promotion.getActualInterestRate().compareTo(BigDecimal.ZERO) == 0) {
				subvention = BigDecimal.ZERO;
			} else {
				subvention = totPayment.subtract(fm.getFinAmount());
			}
			totDuePayment = totPayment.subtract(advanceEMI);
			presentValue = totDuePayment.subtract(subvention);
		}

		fm.setSvAmount(subvention);

		fm.setFinCurrAssetValue(fm.getFinAmount());

		if (!promotion.isOpenBalOnPV()) {
			fm.setFinCurrAssetValue(fm.getFinCurrAssetValue().add(subvention));
		}

		curSchd.setBalanceForPftCal(BigDecimal.ZERO);
		curSchd.setNoOfDays(0);
		curSchd.setDayFactor(BigDecimal.ZERO);
		curSchd.setProfitCalc(BigDecimal.ZERO);
		curSchd.setProfitSchd(BigDecimal.ZERO);
		curSchd.setPrincipalSchd(BigDecimal.ZERO);
		curSchd.setRepayAmount(BigDecimal.ZERO);
		curSchd.setProfitBalance(BigDecimal.ZERO);
		curSchd.setCpzAmount(BigDecimal.ZERO);
		curSchd.setProfitFraction(BigDecimal.ZERO);
		curSchd.setPrincipalSchd(BigDecimal.ZERO);
		curSchd.setClosingBalance(presentValue);
		curSchd.setRvwOnSchDate(true);
		curSchd.setPftDaysBasis(fm.getProfitDaysBasis());

		logger.debug("Leaving");
		return fsData;
	}

	/*
	 * ************************************************************************* Method : repaySchdCal Description :
	 * Repay period schedule calculation for reducing rate
	 * *************************************************************************
	 */
	private FinScheduleData repaySchdCal(FinScheduleData fsData) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		boolean isRepayComplete = false;

		fm.setCalTerms(0);
		Date derivedMDT = fm.getMaturityDate();

		// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);

		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		int sdSize = fsdList.size();
		fm.setNewMaturityIndex(sdSize - 1);

		// FIND LAST REPAYMENT SCHEDULE DATE
		int idxSchd = fm.getSchdIndex();

		fm.setNewMaturityIndex(fsdList.size() - 1);

		if (fm.getRecalIdx() < 0) {
			fsData = setRecalIndex(fsData);
		}

		BigDecimal prvClosingBalance = BigDecimal.ZERO;

		for (int iFsd = idxSchd + 1; iFsd < sdSize; iFsd++) {

			if (iFsd < fm.getRecalIdx()) {
				continue;
			}

			FinanceScheduleDetail curSchd = fsdList.get(iFsd);
			FinanceScheduleDetail prvSchd = fsdList.get(iFsd - 1);

			prvClosingBalance = prvSchd.getClosingBalance();

			Date curSchDate = curSchd.getSchDate();
			Date prvSchDate = prvSchd.getSchDate();

			curSchd.setBalanceForPftCal(prvClosingBalance);
			curSchd.setNoOfDays(DateUtil.getDaysBetween(curSchDate, prvSchDate));
			curSchd.setDayFactor(CalculationUtil.getInterestDays(prvSchDate, curSchDate, curSchd.getPftDaysBasis()));

			isRepayComplete = calculateInterest(fm, prvSchd, curSchd, roundAdjMth);

			if (isRepayComplete) {
				curSchd.setProfitSchd(BigDecimal.ZERO);
				curSchd.setPrincipalSchd(BigDecimal.ZERO);
				curSchd.setRepayAmount(BigDecimal.ZERO);
				curSchd.setClosingBalance(BigDecimal.ZERO);
				curSchd.setProfitBalance(BigDecimal.ZERO);
				curSchd.setCpzAmount(BigDecimal.ZERO);
			}

			// LAST REPAYMENT DATE
			if ((DateUtil.compare(curSchDate, derivedMDT) == 0)) {
				fsData = procMDTRecord(fsData, iFsd, isRepayComplete);
				isRepayComplete = true;
			}

			if (!isRepayComplete) {
				isRepayComplete = setSchdAmounts(fsData, iFsd, roundAdjMth);
			}

			// fm.setFinCurrAssetValue(fm.getFinCurrAssetValue().add(curSchd.getDisbAmount()).add(curSchd.getCpzAmount()).subtract(curSchd.getSchdPriPaid()));

		}

		logger.debug("Leaving");
		return fsData;
	}

	public boolean calculateInterest(FinanceMain fm, FinanceScheduleDetail prvSchd, FinanceScheduleDetail curSchd,
			String roundAdjMth) {

		BigDecimal calIntFraction = BigDecimal.ZERO;
		if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_NEXT_INST)
				|| (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)
						&& DateUtil.compare(curSchd.getSchDate(), fm.getMaturityDate()) == 0)) {
			calIntFraction = prvSchd.getProfitFraction();
		}

		if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) <= 0
				|| prvSchd.getCalculatedRate().compareTo(BigDecimal.ZERO) == 0
				|| prvSchd.getSchDate().compareTo(curSchd.getSchDate()) == 0) {

			curSchd.setProfitFraction(calIntFraction);
			curSchd.setProfitCalc(BigDecimal.ZERO);

			if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) <= 0) {
				return true;
			} else {
				return false;
			}
		}

		BigDecimal calIntRounded = BigDecimal.ZERO;
		BigDecimal calInt = CalculationUtil.calInterest(prvSchd.getSchDate(), curSchd.getSchDate(),
				curSchd.getBalanceForPftCal(), curSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

		calInt = calInt.add(calIntFraction);

		if (calInt.compareTo(BigDecimal.ZERO) > 0) {
			calIntRounded = CalculationUtil.roundAmount(calInt, fm.getCalRoundingMode(), fm.getRoundingTarget());
		}

		calIntFraction = calInt.subtract(calIntRounded);
		calInt = calIntRounded;

		if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)) {
			calIntFraction = calIntFraction.add(prvSchd.getProfitFraction());
		}

		curSchd.setRepayComplete(false);
		curSchd.setProfitCalc(calIntRounded);
		curSchd.setProfitFraction(calIntFraction);
		return false;
	}

	private boolean setSchdAmounts(FinScheduleData fsData, int idxCur, String roundAdjMth) {
		boolean isRepayComplete = false;
		FinanceMain fm = fsData.getFinanceMain();

		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = fsdList.get(idxCur);
		FinanceScheduleDetail prvSchd = fsdList.get(idxCur - 1);

		if (curSchd.isRepayOnSchDate()) {
			curSchd = calPftPriRpy(fsData, idxCur, (idxCur - 1), fm.getEventFromDate());
			fm.setNewMaturityIndex(idxCur);

			if (curSchd.getPrincipalSchd().compareTo(prvSchd.getClosingBalance().add(curSchd.getDisbAmount())) >= 0) {
				curSchd.setPrincipalSchd(prvSchd.getClosingBalance().add(curSchd.getDisbAmount()));

				BigDecimal calInt = curSchd.getProfitCalc();
				if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)) {
					calInt = calInt.add(prvSchd.getProfitFraction());
					if (calInt.compareTo(BigDecimal.ZERO) > 0) {
						calInt = CalculationUtil.roundAmount(calInt, fm.getCalRoundingMode(), fm.getRoundingTarget());
					}
				}

				curSchd.setProfitCalc(calInt);
				curSchd.setProfitSchd(
						prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount()).add(curSchd.getProfitCalc()));
				curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));

				// Rounding Last Installment
				String roundingMode = fsData.getFinanceMain().getCalRoundingMode();
				int roundingTarget = fsData.getFinanceMain().getRoundingTarget();

				int roundRequired = SysParamUtil.getValueAsInt(SMTParameterConstants.ROUND_LASTSCHD);

				if (roundRequired == 1) {
					curSchd.setRepayAmount(
							CalculationUtil.roundAmount(curSchd.getRepayAmount(), roundingMode, roundingTarget));
					curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));

					if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) < 0) {
						curSchd.setProfitSchd(BigDecimal.ZERO);
					}
				}

				isRepayComplete = true;
			}

			/* Count Repay schedules only */
			fm.setCalTerms(fm.getCalTerms() + 1);
			fm.setCalMaturity(curSchd.getSchDate());

		} else if (curSchd.isPftOnSchDate()) {
			curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));
			curSchd.setRepayAmount(curSchd.getProfitSchd());
			curSchd.setPrincipalSchd(BigDecimal.ZERO);
		}

		curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, fm.getScheduleMethod()));

		// Capitalize OR not
		if (curSchd.isCpzOnSchDate()) {
			if (!ImplementationConstants.DFT_CPZ_RESET_ON_RECAL_LOCK) {
				if (!curSchd.isRecalLock()) {
					curSchd.setCpzAmount(curSchd.getProfitBalance());
				}
			} else {
				curSchd.setCpzAmount(curSchd.getProfitBalance());
			}

		} else {
			curSchd.setCpzAmount(BigDecimal.ZERO);
		}

		curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd));

		if (DateUtil.compare(curSchd.getSchDate(), fm.getEventFromDate()) > 0
				&& DateUtil.compare(curSchd.getSchDate(), fm.getEventToDate()) <= 0) {
			fm.setPftForSelectedPeriod(fm.getPftForSelectedPeriod().add(curSchd.getProfitCalc()));
		}

		return isRepayComplete;
	}

	/*
	 * ************************************************************************* Method : calPftPriRpy Description :
	 * Calculate profit and principal for schedule payment
	 * *************************************************************************
	 */
	private FinanceScheduleDetail calPftPriRpy(FinScheduleData fsData, int iCur, int iPrv, Date evtFromDate) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = fsdList.get(iCur);
		FinanceScheduleDetail prvSchd = fsdList.get(iPrv);
		BigDecimal schdInterest = BigDecimal.ZERO;

		if (curSchd.getPresentmentId() > 0 && !StringUtils.equals(FinServiceEvent.RECEIPT, fm.getProcMethod())) {
			if ((curSchd.getProfitCalc().add(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())))
					.compareTo(curSchd.getProfitSchd()) > 0) {
				curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
				return curSchd;
			}
		}

		// If Schedule recalculation has Lock for the particular schedule term,
		// it should not recalculate.
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {
			if (curSchd.isRecalLock()) {
				curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
				return curSchd;
			}
		}

		// EQUAL PAYMENT: Applicable for REPAYMENT period
		if (CalculationConstants.SCHMTHD_EQUAL.equals(curSchd.getSchdMethod())) {
			BigDecimal pftToSchd = calProfitToSchd(curSchd, prvSchd);

			if (pftToSchd.compareTo(curSchd.getRepayAmount()) > 0 && !fsData.getFinanceType().isAllowPftBal()) {
				curSchd.setProfitSchd(pftToSchd);
				curSchd.setPrincipalSchd(BigDecimal.ZERO);
				curSchd.setRepayAmount(pftToSchd);
			} else {

				if (pftToSchd.compareTo(curSchd.getRepayAmount()) < 0) {
					curSchd.setProfitSchd(pftToSchd);
				} else {
					curSchd.setProfitSchd(curSchd.getRepayAmount());
				}

				curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
			}

			// PRINCIPAL ONLY: Applicable for REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_PRI.equals(curSchd.getSchdMethod())) {
			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
				curSchd.setProfitSchd(curSchd.getSchdPftPaid());
			} else {
				curSchd.setProfitSchd(BigDecimal.ZERO);
			}

			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			// CALCULATED PROFIT ONLY: Applicable for GRACE & REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_PFT.equals(curSchd.getSchdMethod())) {
			// IF Scheduled Profit cannot change (Effective Rate Calculation)
			// Then leave actual scheduled else calculate
			if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) < 0) {
				curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			}

			if (!fm.isProtectSchdPft()) {
				schdInterest = calProfitToSchd(curSchd, prvSchd);
				curSchd.setProfitSchd(schdInterest);
			}

			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

		} else if (CalculationConstants.SCHMTHD_PRI_PFT.equals(curSchd.getSchdMethod())) {
			if (curSchd.getPresentmentId() > 0) {

				curSchd.setProfitSchd(
						prvSchd.getProfitBalance().add(curSchd.getProfitCalc()).subtract(prvSchd.getCpzAmount()));

				if (StringUtils.isNotBlank(fm.getReceiptPurpose())
						&& (StringUtils.equals(fm.getReceiptPurpose(), FinServiceEvent.EARLYRPY)
								|| StringUtils.equals(fm.getReceiptPurpose(), FinServiceEvent.EARLYSETTLE))) {

					if (curSchd.getSchDate().compareTo(SysParamUtil.getAppDate()) <= 0) {
						curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
					} else {
						curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
					}
				} else {
					curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
				}

			} else if (!fm.isProtectSchdPft()) {
				schdInterest = calProfitToSchd(curSchd, prvSchd);
				curSchd.setProfitSchd(schdInterest);
				curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			} else {
				curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			}

		}

		curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

		// store first repay amount
		if (fm.getCalTerms() == 1) {
			fm.setFirstRepay(curSchd.getRepayAmount());
		}

		// store last repay amount
		fm.setLastRepay(curSchd.getRepayAmount());

		// logger.debug("Leaving");
		return curSchd;

	}

	/*
	 * ************************************************************************* Method : getProfitBalance Description :
	 * Get profit balance unscheduled till schedule date
	 * *************************************************************************
	 */
	private BigDecimal getProfitBalance(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd,
			String schdMethod) {
		if (!(schdMethod.equals(CalculationConstants.SCHMTHD_PRI))) {
			return prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount()).subtract(curSchd.getProfitSchd())
					.add(curSchd.getProfitCalc());
		} else {
			return prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount()).add(curSchd.getProfitCalc())
					.subtract(curSchd.getProfitSchd());
		}
	}

	/*
	 * ************************************************************************* Method : getClosingBalance Description
	 * : Schedule record Closing balance *************************************************************************
	 */
	private BigDecimal getClosingBalance(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd) {
		BigDecimal closingBal = prvSchd.getClosingBalance().add(curSchd.getDisbAmount()).add(curSchd.getFeeChargeAmt())
				.subtract(curSchd.getDownPaymentAmount()).subtract(curSchd.getPrincipalSchd())
				.add(curSchd.getCpzAmount());
		return closingBal;
	}

	/*
	 * ************************************************************************* Method : getProfitSchd Description :
	 * Get Profit to be scheduled *************************************************************************
	 */
	private BigDecimal calProfitToSchd(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd) {
		BigDecimal newProfit = BigDecimal.ZERO;

		// If profit already paid do not touch the schedule profit.
		if (curSchd.isSchPftPaid()) {
			newProfit = curSchd.getSchdPftPaid();
		} else if (curSchd.getPresentmentId() > 0) {
			newProfit = prvSchd.getProfitBalance().add(curSchd.getProfitCalc()).subtract(prvSchd.getCpzAmount());

			if (curSchd.getProfitSchd().compareTo(newProfit) > 0) {
				curSchd.setPrincipalSchd(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd().subtract(newProfit)));
			} else {
				newProfit = curSchd.getProfitSchd();
			}

		} else {
			return prvSchd.getProfitBalance().add(curSchd.getProfitCalc()).subtract(prvSchd.getCpzAmount());
		}

		return newProfit;
	}

	/*
	 * ************************************************************************* Method : round Description : To round
	 * the BigDecimal value to the basic rounding mode
	 * *************************************************************************
	 */
	private BigDecimal round(BigDecimal value) {
		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> >>
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SORTING METHODS >>>>>>>>>>>>>>>>
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sortSchdDetails Description: Sort Schedule Details
	 * ________________________________________________________________________________________________________________
	 */
	public static List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> fsdList) {

		if (fsdList != null && fsdList.size() > 0) {
			Collections.sort(fsdList, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return fsdList;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sortRepayInstructions Description: Sort Repay Instructions
	 * ________________________________________________________________________________________________________________
	 */
	private List<RepayInstruction> sortRepayInstructions(List<RepayInstruction> riList) {

		if (riList != null && riList.size() > 0) {
			Collections.sort(riList, new Comparator<RepayInstruction>() {
				@Override
				public int compare(RepayInstruction detail1, RepayInstruction detail2) {
					return DateUtil.compare(detail1.getRepayDate(), detail2.getRepayDate());
				}
			});
		}
		return riList;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : PMT
	 * ________________________________________________________________________________________________________________
	 */
	private BigDecimal approxPMT(FinanceMain fm, BigDecimal intRate, int terms, BigDecimal presentValue,
			BigDecimal futureValue, int type) {

		if (intRate.compareTo(BigDecimal.ZERO) == 0) {
			BigDecimal pmtValue = BigDecimal.ZERO;
			if (fm.isCalculateRepay()) {
				pmtValue = presentValue.divide(new BigDecimal(terms), 0, RoundingMode.HALF_DOWN);
			}
			return pmtValue;
		}

		String idb = fm.getProfitDaysBasis();
		String calFrq = StringUtils.mid(fm.getRepayFrq(), 0, 1);
		BigDecimal periods = BigDecimal.ZERO;
		BigDecimal days365 = new BigDecimal(365);
		BigDecimal days360 = new BigDecimal(360);

		// Interest Rate Per Day
		intRate = intRate.divide(BigDecimal.valueOf(36000), 13, RoundingMode.HALF_DOWN);

		if (!fm.getRepayPftFrq().equals(fm.getRepayFrq())) {
			if (fm.isFinRepayPftOnFrq()) {
				calFrq = StringUtils.mid(fm.getRepayPftFrq(), 0, 1);
			}
		}

		periods = BigDecimal.valueOf(CalculationUtil.getTermsPerYear(fm.getRepayPftFrq()));

		// Interest Rate Per Period
		// PMT calculation Changes 19-06-2019
		if (fm.isEqualRepay()
				&& (idb.equals(CalculationConstants.IDB_ACT_ISDA) || idb.equals(CalculationConstants.IDB_ACT_365FIXED)
						|| idb.equals(CalculationConstants.IDB_ACT_365LEAPS)
						|| idb.equals(CalculationConstants.IDB_ACT_365LEAP))) {
			intRate = intRate.multiply(days365.divide(periods, 13, RoundingMode.HALF_DOWN));
		} else {
			intRate = intRate.multiply(days360.divide(periods, 13, RoundingMode.HALF_DOWN));
		}

		presentValue = presentValue.subtract(futureValue);
		futureValue = BigDecimal.ZERO;

		double dPMT = 0;
		double dIntRate = intRate.doubleValue();
		double dPresentValue = presentValue.doubleValue();
		double dFutureValue = futureValue.doubleValue();
		BigDecimal pmt = BigDecimal.ZERO;

		dPMT = dIntRate / (Math.pow(1 + dIntRate, terms) - 1)
				* (dPresentValue * Math.pow(1 + dIntRate, terms) + dFutureValue);

		if (type == 1) {
			dPMT = dPMT / (1 + dIntRate);
		}

		pmt = round(BigDecimal.valueOf(dPMT));

		// pmt = pmt.setScale(0, RoundingMode.HALF_DOWN);
		pmt = CalculationUtil.roundAmount(pmt, fm.getCalRoundingMode(), fm.getRoundingTarget());

		return pmt;

	}

	public FinScheduleData procMDTRecord(FinScheduleData fsData, int i, boolean isRepayComplete) {
		logger.debug("Entering");

		FinanceScheduleDetail curSchd = fsData.getFinanceScheduleDetails().get(i);
		FinanceScheduleDetail prvSchd = fsData.getFinanceScheduleDetails().get(i - 1);

		// Different from Normal Loan
		curSchd.setPrincipalSchd(prvSchd.getClosingBalance());
		if (fsData.getPromotion().getActualInterestRate().compareTo(BigDecimal.ZERO) > 0) {
			curSchd.setRepayAmount(fsData.getFinanceMain().getReqRepayAmount());
			curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));
			curSchd.setProfitCalc(curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance()));
		} else {
			curSchd.setRepayAmount(prvSchd.getClosingBalance());
			curSchd.setProfitSchd(BigDecimal.ZERO);
		}

		BigDecimal endBal = getClosingBalance(curSchd, prvSchd);
		curSchd.setClosingBalance(endBal);

		curSchd.setSchdMethod(prvSchd.getSchdMethod());
		if (!isRepayComplete) {
			fsData.getFinanceMain().setCalTerms(fsData.getFinanceMain().getCalTerms() + 1);
			fsData.getFinanceMain().setCalMaturity(curSchd.getSchDate());
		}

		// Rounding Last Installment
		String roundingMode = fsData.getFinanceMain().getCalRoundingMode();
		int roundingTarget = fsData.getFinanceMain().getRoundingTarget();

		int roundRequired = SysParamUtil.getValueAsInt(SMTParameterConstants.ROUND_LASTSCHD);

		if (roundRequired == 1) {
			curSchd.setRepayAmount(CalculationUtil.roundAmount(curSchd.getRepayAmount(), roundingMode, roundingTarget));
			curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));

			if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) < 0) {
				curSchd.setProfitSchd(BigDecimal.ZERO);
			}
		}

		logger.debug("Leaving");
		return fsData;

	}

	public FinScheduleData prepareFirstSchdCal(FinScheduleData fsData) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		FinanceScheduleDetail openSchd = fsData.getFinanceScheduleDetails().get(0);
		BigDecimal presentValue = openSchd.getClosingBalance();
		BigDecimal instAmt = new BigDecimal(0);
		int terms = fm.getNumberOfTerms() - fm.getAdvTerms();
		String schdMethod = fm.getScheduleMethod();
		fm.setAdjTerms(terms);

		if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
			if (fm.getReqRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
				instAmt = fm.getReqRepayAmount();
			} else {
				BigDecimal intRate = openSchd.getCalculatedRate();
				instAmt = approxPMT(fm, intRate, terms, presentValue, BigDecimal.ZERO, 0);
			}

		} else if (schdMethod.equals(CalculationConstants.SCHMTHD_PRI)
				|| schdMethod.equals(CalculationConstants.SCHMTHD_PRI_PFT)) {

			if (fm.getReqRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
				instAmt = fm.getReqRepayAmount();
			} else {
				instAmt = presentValue.divide(BigDecimal.valueOf(terms), 0, RoundingMode.HALF_DOWN);
			}
		}

		fsData = setRpyInstructDetails(fsData, fm.getNextRepayPftDate(), fm.getMaturityDate(), instAmt, schdMethod);
		fm.setRecalFromDate(fm.getNextRepayPftDate());
		fm.setIndexMisc(fsData.getRepayInstructions().size() - 1);
		fm.setMiscAmount(instAmt);

		logger.debug("Leaving");
		return fsData;

	}

	private FinScheduleData calEqualPayment(FinScheduleData fsData) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		List<RepayInstruction> riList = fsData.getRepayInstructions();

		boolean isAdjustClosingBal = fm.isAdjustClosingBal();

		int sdSize = fsdList.size();

		if (!isAdjustClosingBal && AdvanceType.hasAdvEMI(fm.getAdvType())
				&& AdvanceStage.hasFrontEnd(fm.getAdvStage())) {
			sdSize = fsdList.size() - fm.getAdvTerms();
		}

		int riSize = riList.size();
		int iTerms = fm.getAdjTerms();
		int iRpyInst = 0;

		BigDecimal repayAmountLow = BigDecimal.ZERO;
		BigDecimal repayAmountHigh = BigDecimal.ZERO;

		// Setting Compare to Expected defaults to false for Further actions
		fm.setCompareToExpected(false);
		BigDecimal comparisionAmount = BigDecimal.ZERO;
		BigDecimal comparisionToAmount = BigDecimal.ZERO;
		String schdMethod = "";
		BigDecimal approxEMI = BigDecimal.ZERO;
		boolean isCompareMDTRecord = false;
		boolean isComapareWithEMI = false;

		// Comparison amount is Maturity Record or Instruction record
		if (StringUtils.equals(CalculationConstants.RPYCHG_CURPRD, fm.getRecalType())
				|| StringUtils.equals(CalculationConstants.RPYCHG_TILLDATE, fm.getRecalType())) {
			isCompareMDTRecord = true;
		}

		// Find Comparision with EMI or Principal
		schdMethod = riList.get(riSize - 1).getRepaySchdMethod();
		if (StringUtils.equals(CalculationConstants.SCHMTHD_EQUAL, schdMethod)) {
			isComapareWithEMI = true;
		}

		for (int i = 0; i < riSize; i++) {
			if (DateUtil.compare(riList.get(i).getRepayDate(), fm.getRecalFromDate()) >= 0) {
				iRpyInst = i;
				approxEMI = riList.get(i).getRepayAmount();
				break;
			}
		}

		// Calculate terms to be adjusted
		for (int i = 0; i < sdSize; i++) {
			if (DateUtil.compare(fsdList.get(i).getSchDate(), fm.getRecalFromDate()) >= 0
					&& DateUtil.compare(fsdList.get(i).getSchDate(), fm.getRecalToDate()) <= 0) {
				iTerms = iTerms + 1;
			}
		}

		// Set Recalculation Schedule Method
		schdMethod = fm.getRecalSchdMethod();

		// Find COMPARISION Amount
		if (isCompareMDTRecord) {
			comparisionAmount = fm.getCompareExpectedResult();
		} else {
			comparisionAmount = riList.get(iRpyInst).getRepayAmount();
		}

		// Find COMPARISION TO Amount
		if (isComapareWithEMI) {
			comparisionToAmount = fsdList.get(sdSize - 1).getRepayAmount();
		} else {
			comparisionToAmount = fsdList.get(sdSize - 1).getPrincipalSchd();
		}

		if (approxEMI.compareTo(comparisionToAmount) == 1) {
			repayAmountLow = comparisionToAmount;
			repayAmountHigh = approxEMI;
		} else {
			repayAmountLow = approxEMI;
			repayAmountHigh = comparisionToAmount;
		}

		BigDecimal lastTriedEMI = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);
		BigDecimal diff_Low_High = BigDecimal.ZERO;

		for (int i = 0; i < 50; i++) {
			int size = fsdList.size() - 1;
			if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())) {
				size = size - fm.getAdvTerms();
			}
			approxEMI = (repayAmountLow.add(repayAmountHigh)).divide(number2, 0, RoundingMode.HALF_DOWN);
			approxEMI = CalculationUtil.roundAmount(approxEMI, fm.getCalRoundingMode(), fm.getRoundingTarget());

			if (repayAmountLow.compareTo(approxEMI) == 0 || repayAmountHigh.compareTo(approxEMI) == 0) {
				break;
			}

			diff_Low_High = (repayAmountHigh.subtract(repayAmountLow)).abs();
			if (diff_Low_High.compareTo(BigDecimal.valueOf(fm.getRoundingTarget())) <= 0) {
				break;
			}

			lastTriedEMI = approxEMI;
			riList.get(iRpyInst).setRepayAmount(approxEMI);
			fm.setAdjustClosingBal(isAdjustClosingBal);
			fsData = getRpyInstructDetails(fsData);
			fsData = graceSchdCal(fsData);
			fsData = repaySchdCal(fsData);

			// Find COMPARISION Amount
			if (!isCompareMDTRecord) {
				comparisionAmount = riList.get(iRpyInst).getRepayAmount();
			}

			// Find COMPARISION TO Amount
			if (isComapareWithEMI) {
				comparisionToAmount = fsdList.get(size).getRepayAmount();
			} else {
				comparisionToAmount = fsdList.get(size).getPrincipalSchd();
			}

			if (comparisionToAmount.compareTo(comparisionAmount) == 0) {
				logger.debug("Leaving");
				return fsData;
			}

			diff_Low_High = (comparisionToAmount.subtract(comparisionAmount)).abs();
			if (diff_Low_High.compareTo(BigDecimal.valueOf(fm.getRoundingTarget())) <= 0) {
				logger.debug("Leaving");
				return fsData;
			}

			if (comparisionAmount.compareTo(comparisionToAmount) < 0) {
				repayAmountLow = approxEMI;
			} else {
				repayAmountHigh = approxEMI;
			}

		}

		// Find Nearest EMI
		BigDecimal minRepayDifference = BigDecimal.ZERO;
		BigDecimal maxRepayDifference = BigDecimal.ZERO;

		// Find COMPARISION Amount
		if (!isCompareMDTRecord) {
			comparisionAmount = riList.get(iRpyInst).getRepayAmount();
		}

		// Find COMPARISION TO Amount
		if (isComapareWithEMI) {
			comparisionToAmount = fsdList.get(sdSize - 1).getRepayAmount();
		} else {
			comparisionToAmount = fsdList.get(sdSize - 1).getPrincipalSchd();
		}

		if (repayAmountLow.compareTo(repayAmountHigh) != 0) {
			if (lastTriedEMI.compareTo(repayAmountLow) == 0) {
				minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				approxEMI = repayAmountHigh;
			} else {
				maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				approxEMI = repayAmountLow;
			}

			approxEMI = CalculationUtil.roundAmount(approxEMI, fm.getCalRoundingMode(), fm.getRoundingTarget());

			lastTriedEMI = approxEMI;
			riList.get(iRpyInst).setRepayAmount(approxEMI);
			fm.setAdjustClosingBal(isAdjustClosingBal);
			fsData = getRpyInstructDetails(fsData);
			fsData = graceSchdCal(fsData);
			fsData = repaySchdCal(fsData);

			// Find COMPARISION Amount
			if (!isCompareMDTRecord) {
				comparisionAmount = riList.get(iRpyInst).getRepayAmount();
			}

			// Find COMPARISION TO Amount
			if (isComapareWithEMI) {
				comparisionToAmount = fsdList.get(sdSize - 1).getRepayAmount();
			} else {
				comparisionToAmount = fsdList.get(sdSize - 1).getPrincipalSchd();
			}

			if (lastTriedEMI.compareTo(repayAmountLow) == 0) {
				minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
			} else {
				maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
			}

			if (maxRepayDifference.compareTo(minRepayDifference) < 0) {
				approxEMI = repayAmountHigh;
			} else {
				approxEMI = repayAmountLow;
			}
		}

		approxEMI = CalculationUtil.roundAmount(approxEMI, fm.getCalRoundingMode(), fm.getRoundingTarget());

		// SET EQUAL REPAYMENT AMOUNT AS EFFECTIVE REPAY AMOUNT AND CALL PROCESS
		riList.get(iRpyInst).setRepayAmount(approxEMI);
		fm.setAdjustClosingBal(isAdjustClosingBal);
		fsData = getRpyInstructDetails(fsData);
		fsData = graceSchdCal(fsData);
		fsData = repaySchdCal(fsData);

		logger.debug("Leaving");
		return fsData;
	}

	/**
	 * Method for Fetching Recal From Date
	 * 
	 * @param fsData
	 * @param eventFromDate
	 * @return
	 */
	private FinScheduleData resetRecalData(FinScheduleData fsData, Date eventFromDate, BigDecimal amount,
			String receiptPurpose) {

		// Resetting Next Recal From Date if not exists
		Date recalFromDate = eventFromDate;

		// TODO : If Early settle on or before Grace end , need to re-modify
		Date graceEndDate = fsData.getFinanceMain().getGrcPeriodEndDate();
		FinanceScheduleDetail openSchd = null;
		int prvIndex = -1;

		// Resetting Recal Schedule Method & Next Recal From Date if not exists
		String recalSchdMethod = null;
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		for (int i = 0; i < fsdList.size(); i++) {
			FinanceScheduleDetail curSchd = fsdList.get(i);

			if (DateUtil.compare(curSchd.getSchDate(), eventFromDate) == 0) {
				fsData.getFinanceMain().setIndexMisc(i);
				openSchd = curSchd;

				if (curSchd.isRepayOnSchDate()) {
					recalSchdMethod = curSchd.getSchdMethod();
					if (StringUtils.equals(recalSchdMethod, CalculationConstants.SCHMTHD_PFT)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					}

					if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
						if (StringUtils.equals(recalSchdMethod, CalculationConstants.SCHMTHD_PRI)) {
							recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
						}
					}
				} else if (curSchd.isPftOnSchDate()) {
					recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					recalSchdMethod = CalculationConstants.SCHMTHD_PRI;
					if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					}
				}

			} else if (DateUtil.compare(curSchd.getSchDate(), eventFromDate) > 0) {
				if (curSchd.getPresentmentId() != 0 || DateUtil.compare(curSchd.getSchDate(), graceEndDate) <= 0) {
					continue;
				}
				recalFromDate = curSchd.getSchDate();
				break;
			} else {
				prvIndex = prvIndex + 1;
			}

		}

		// If schedule Not found
		if (openSchd == null) {
			fsData = addSchdRcd(fsData, eventFromDate, prvIndex);
			openSchd = fsData.getFinanceScheduleDetails().get(prvIndex + 1);

			if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYRPY)) {
				recalSchdMethod = CalculationConstants.SCHMTHD_PRI;
			} else if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
				recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
			}

			openSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
		}

		if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
			openSchd.setPftOnSchDate(true);
		} else {
			openSchd.setPartialPaidAmt(openSchd.getPartialPaidAmt().add(amount));
		}
		openSchd.setRepayOnSchDate(true);
		openSchd.setTDSApplicable(fsData.getFinanceScheduleDetails().get(prvIndex + 2).isTDSApplicable());
		fsData.getFinanceMain().setRecalSchdMethod(recalSchdMethod);
		fsData.getFinanceMain().setRecalFromDate(recalFromDate);

		return fsData;
	}

	private FinScheduleData setRecalAttributes(FinScheduleData fsData, String recalPurpose, BigDecimal newDisbAmount,
			BigDecimal chgAmount) {
		logger.debug("Entering");

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		int sdSize = fsdList.size();

		fm.setCompareToExpected(false);
		fm.setCompareExpectedResult(BigDecimal.ZERO);
		fm.setCalculateRepay(true);
		fm.setEqualRepay(true);
		fm.setIndexMisc(0);
		fm.setMiscAmount(BigDecimal.ZERO);

		String recaltype = fm.getRecalType();
		boolean resetRI = true;

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT)) {
			fm.setCalculateRepay(false);
			fm.setEqualRepay(false);
			fm.setRecalFromDate(fm.getMaturityDate());
			fm.setRecalToDate(fm.getMaturityDate());
		} else if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLMDT)) {
			fm.setRecalToDate(fsdList.get(sdSize - 1).getSchDate());
		}

		// Set maturity Date schedule amount
		if (StringUtils.equals(CalculationConstants.SCHMTHD_EQUAL, fsdList.get(sdSize - 1).getSchdMethod())) {
			fm.setCompareExpectedResult(fsdList.get(sdSize - 1).getRepayAmount());
		} else {
			fm.setCompareExpectedResult(fsdList.get(sdSize - 1).getPrincipalSchd());
		}

		Date recalFromDate = fm.getRecalFromDate();
		Date recalToDate = fm.getRecalToDate();
		String schdMethod = fm.getRecalSchdMethod();

		// Set RecalSchdMethod
		fsData = getSchdMethod(fsData);
		schdMethod = fm.getRecalSchdMethod();

		// Set Repayment Instructions as 1 for recalFromDate to recalToDate.
		// Reason for not setting with 0 is to avoid deleting future 0
		// instructions
		if (!StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT) && resetRI) {
			fsData = setRpyInstructDetails(fsData, recalFromDate, recalToDate, BigDecimal.ONE, schdMethod);
		} else if (!resetRI) {
			fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			fm.setCalculateRepay(false);
		}

		logger.debug("Leaving");
		return fsData;
	}

	private FinScheduleData getSchdMethod(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		String schdMethod = fm.getRecalSchdMethod();

		if (StringUtils.equals(schdMethod, PennantConstants.List_Select)) {
			schdMethod = "";
		}

		if (!StringUtils.isBlank(schdMethod)) {
			return finScheduleData;
		}

		List<RepayInstruction> riList = finScheduleData.getRepayInstructions();
		int risize = riList.size();

		Date schdMethodDate = fm.getRecalFromDate();

		// Find Schedule Method used for existing instruction
		for (int iRI = 0; iRI < risize; iRI++) {
			schdMethod = riList.get(iRI).getRepaySchdMethod();

			if (DateUtil.compare(riList.get(iRI).getRepayDate(), schdMethodDate) >= 0) {
				break;
			}
		}

		if (StringUtils.isBlank(schdMethod)) {
			schdMethod = fm.getScheduleMethod();
		}

		fm.setRecalSchdMethod(schdMethod);

		return finScheduleData;

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}
}