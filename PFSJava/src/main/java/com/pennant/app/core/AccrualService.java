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
 * * FileName : AccrualService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 * * Modified Date
 * : 11-06-2015 * * Description : * *
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.IRRScheduleDetailDAO;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.IRRScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class AccrualService extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(AccrualService.class);

	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private IRRScheduleDetailDAO irrScheduleDetailDAO;

	private final BigDecimal HUNDERED = new BigDecimal(100);
	public String TDS_ROUNDING_MODE = null;
	public int TDS_ROUNDING_TARGET = 0;
	public BigDecimal TDS_PERCENTAGE = BigDecimal.ZERO;
	public BigDecimal TDS_MULTIPLIER = BigDecimal.ZERO;
	public int TDS_SCHD_INDEX = -2;

	public void processAccrual(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			// Ignore ACCRUAL calculation when loan is WriteOff
			if (finEODEvent.getFinanceMain().isWriteoffLoan()) {
				continue;
			}

			finEODEvent = calculateAccruals(finEODEvent, custEODEvent);
		}
	}

	public FinEODEvent calculateAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) {
		logger.info(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();

		long finID = fm.getFinID();

		fm.setRoundingTarget(finEODEvent.getFinType().getRoundingTarget());
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		EventProperties eventProperties = custEODEvent.getEventProperties();
		fm.setEventProperties(eventProperties);

		FinanceProfitDetail pfd = finEODEvent.getFinProfitDetail();
		if (pfd.getFinReference() == null) {
			pfd = financeProfitDetailDAO.getFinProfitDetailsById(finID);
		}

		pfd = calProfitDetails(fm, schedules, pfd, custEODEvent.getEodValueDate());

		// post Accruals on Application Extended Month End OR Application Month End OR Daily
		int amzPostingEvent = eventProperties.getAmzPostingEvent();
		boolean isAmzPostToday = false;
		if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_MTH_END) {
			if (DateUtil.compare(custEODEvent.getEodDate(), DateUtil.getMonthEnd(custEODEvent.getEodDate())) == 0) {
				isAmzPostToday = true;
			}
		} else if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_EXT_MTH_END) {
			if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
				if (DateUtil.compare(getEodConfig().getMnthExtTo(), custEODEvent.getEodDate()) == 0) {
					isAmzPostToday = true;
				}
			}

		} else {
			isAmzPostToday = true;
		}

		if (isAmzPostToday) {
			// If Subvention exists on Loan, calculate Subvention accrual amounts based on schedules
			if (fm.isAllowSubvention() && pfd.isSvnAcrCalReq()) {
				Date accrualDate = custEODEvent.getEodValueDate();
				BigDecimal svnPftAmount = calculateSvnPftAmount(fm, accrualDate);
				pfd.setSvnPftAmount(svnPftAmount);
				// Subvention calculation required on Next cycles?(In Future)
				if (DateUtil.compare(fm.getGrcPeriodEndDate(), accrualDate) < 0) {
					pfd.setSvnAcrCalReq(false);
				}
			}
			postAccruals(finEODEvent, custEODEvent);
		}

		logger.info(Literal.LEAVING);
		return finEODEvent;
	}

	public FinanceProfitDetail calProfitDetails(FinanceMain fm, List<FinanceScheduleDetail> schedules,
			FinanceProfitDetail pfd, Date valueDate) {
		EventProperties eventProperties = fm.getEventProperties();

		if (eventProperties.isParameterLoaded()) {
			TDS_ROUNDING_MODE = eventProperties.getTdsRoundMode();
			TDS_ROUNDING_TARGET = eventProperties.getTdsRoundingTarget();
			TDS_PERCENTAGE = eventProperties.getTdsPerc();
			TDS_MULTIPLIER = eventProperties.getTdsMultiplier();
			TDS_SCHD_INDEX = -2;
		} else {
			setSMTParms();
		}

		long finID = fm.getFinID();
		Date dateSusp = null;

		boolean isSuspReq = ImplementationConstants.SUSP_CHECK_REQ;

		if (isSuspReq) {
			if (!PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())) {
				dateSusp = financeSuspHeadDAO.getFinSuspDate(finID);
			}
		}

		if (dateSusp == null) {
			dateSusp = DateUtil.addDays(fm.getMaturityDate(), 1);
		}

		// Reset Totals
		resetCalculatedTotals(fm, pfd);

		// FIXME: How schdDetails will be empty? OD loans before disbursement?
		if (CollectionUtils.isEmpty(schedules)) {
		} else {
			calAccruals(fm, schedules, pfd, valueDate, dateSusp);
			if (ImplementationConstants.GAP_INTEREST_REQUIRED
					&& FinanceConstants.PRODUCT_CD.equals(fm.getProductCategory())) {
				calGapInterest(fm, pfd, valueDate);
			}

		}

		calculateTotals(fm, pfd, dateSusp, valueDate);
		return pfd;

	}

	private void resetCalculatedTotals(FinanceMain fm, FinanceProfitDetail pfd) {
		if (PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())) {
			pfd.setFinID(fm.getFinID());
			pfd.setFinReference(fm.getFinReference());
			pfd.setFinStartDate(fm.getFinStartDate());
			pfd.setCustId(fm.getCustID());
			pfd.setCustCIF(fm.getCustCIF());
			pfd.setFinBranch(fm.getFinBranch());
			pfd.setFinType(fm.getFinType());
			pfd.setFinCcy(fm.getFinCcy());
			pfd.setFinPurpose(fm.getFinPurpose());
			pfd.setFinContractDate(fm.getFinContractDate());
			pfd.setFinApprovedDate(fm.getFinApprovedDate());
			pfd.setFullPaidDate(fm.getFinStartDate());
			pfd.setFinAmount(fm.getFinAmount());
			pfd.setDownPayment(fm.getDownPayment());
			pfd.setFinCommitmentRef(fm.getFinCommitmentRef());
			pfd.setFinCategory(fm.getFinCategory());
			pfd.setProductCategory(fm.getProductCategory());
			pfd.setFirstODDate(pfd.getFinStartDate());
			pfd.setPrvODDate(pfd.getFinStartDate());
			pfd.setAdvanceEMI(fm.getAdvanceEMI());
			pfd.setSvAmount(fm.getSvAmount());
			pfd.setCbAmount(fm.getCbAmount());
			pfd.setCurReducingRate(fm.getRepayProfitRate());
		}

		EventProperties eventProperties = fm.getEventProperties();
		// Miscellaneous Fields
		if (eventProperties.isParameterLoaded()) {
			pfd.setLastMdfDate(eventProperties.getAppDate());
		} else {
			pfd.setLastMdfDate(SysParamUtil.getAppDate());
		}
		pfd.setMaturityDate(fm.getMaturityDate());
		pfd.setFinIsActive(fm.isFinIsActive());
		pfd.setClosingStatus(fm.getClosingStatus());
		pfd.setRepayFrq(fm.getRepayFrq());
		pfd.setFinStatus(fm.getFinStatus());
		pfd.setFinStsReason(fm.getFinStsReason());
		pfd.setFinWorstStatus(fm.getFinStatus());

		// Setting date for recal purpose
		pfd.setFirstRepayDate(pfd.getFinStartDate());
		pfd.setPrvRpySchDate(pfd.getFinStartDate());
		pfd.setNSchdDate(pfd.getMaturityDate());
		pfd.setFirstDisbDate(pfd.getMaturityDate());
		pfd.setLatestDisbDate(pfd.getMaturityDate());
		pfd.setLatestRpyDate(fm.getFinStartDate());

		pfd.setBaseRateCode(null);
		pfd.setSplRateCode(null);

		// Interest Calculaiton on Pastdue
		if (CalculationConstants.PDPFTCAL_NOTAPP.equals(fm.getPastduePftCalMthd())) {
			pfd.setCalPftOnPD(false);
		} else {
			pfd.setCalPftOnPD(true);
		}
		pfd.setPftOnPDMethod(fm.getPastduePftCalMthd());
		pfd.setPftOnPDMrg(fm.getPastduePftMargin());

		// profit
		pfd.setTotalPftSchd(BigDecimal.ZERO);
		pfd.setTotalPftCpz(BigDecimal.ZERO);
		pfd.setTotalPftPaid(BigDecimal.ZERO);
		pfd.setTotalPftBal(BigDecimal.ZERO);

		pfd.setTdSchdPft(BigDecimal.ZERO);
		pfd.setTdPftCpz(BigDecimal.ZERO);
		pfd.setTdSchdPftPaid(BigDecimal.ZERO);
		pfd.setTdSchdPftBal(BigDecimal.ZERO);

		pfd.setNSchdPft(BigDecimal.ZERO);
		pfd.setNSchdPftDue(BigDecimal.ZERO);
		pfd.setPrvRpySchPft(BigDecimal.ZERO);

		// principal
		pfd.setTotalpriSchd(BigDecimal.ZERO);
		pfd.setTotalPriPaid(BigDecimal.ZERO);
		pfd.setTotalPriBal(BigDecimal.ZERO);

		pfd.setTdSchdPri(BigDecimal.ZERO);
		pfd.setTdSchdPriPaid(BigDecimal.ZERO);
		pfd.setTdSchdPriBal(BigDecimal.ZERO);

		pfd.setNSchdPri(BigDecimal.ZERO);
		pfd.setNSchdPriDue(BigDecimal.ZERO);
		pfd.setPrvRpySchPri(BigDecimal.ZERO);

		// Accruals and amortizations
		pfd.setPftAccrued(BigDecimal.ZERO);
		pfd.setPftAccrueSusp(BigDecimal.ZERO);
		pfd.setPftAmz(BigDecimal.ZERO);
		pfd.setPftAmzNormal(BigDecimal.ZERO);
		pfd.setPftAmzPD(BigDecimal.ZERO);
		pfd.setPftAmzSusp(BigDecimal.ZERO);

		pfd.setTotalPriPaidInAdv(BigDecimal.ZERO);
		pfd.setTotalPftPaidInAdv(BigDecimal.ZERO);

		// Terms
		pfd.setNOInst(0);
		pfd.setNOPaidInst(0);
		pfd.setFutureInst(0);
		pfd.setRemainingTenor(0);
		pfd.setTotalTenor(0);

		// TDS
		pfd.setTdTdsAmount(BigDecimal.ZERO);
		pfd.setTdTdsPaid(BigDecimal.ZERO);
		pfd.setTdTdsBal(BigDecimal.ZERO);
		pfd.setTdsAccrued(BigDecimal.ZERO);
	}

	private void calAccruals(FinanceMain fm, List<FinanceScheduleDetail> schedules, FinanceProfitDetail pfd,
			Date valueDate, Date dateSusp) {
		String finState = CalculationConstants.FIN_STATE_NORMAL;
		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;
		Date accrualDate = null;
		BigDecimal maxRepayAmount = BigDecimal.ZERO;

		EventProperties eventProperties = fm.getEventProperties();

		if (eventProperties.isParameterLoaded()) {
			accrualDate = DateUtil.addDays(valueDate, eventProperties.getAccrualCalOn());
		} else {
			int valueToadd = SysParamUtil.getValueAsInt(SMTParameterConstants.ACCRUAL_CAL_ON);
			accrualDate = DateUtil.addDays(valueDate, valueToadd);
		}

		Date pdDate = pfd.getPrvODDate();

		for (int i = 0; i < schedules.size(); i++) {
			curSchd = schedules.get(i);
			curSchdDate = curSchd.getSchDate();

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schedules.get(i - 1);
			}

			prvSchdDate = prvSchd.getSchDate();

			// Next details: in few cases there might be schedules present even after the maturity date. ex: when
			// calculating the fees
			if (curSchdDate.compareTo(fm.getMaturityDate()) == 0 || i == schedules.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schedules.get(i + 1);
			}

			nextSchdDate = nextSchd.getSchDate();

			// -------------------------------------------------------------------------------------
			// Cumulative Totals
			// -------------------------------------------------------------------------------------
			calCumulativeTotals(pfd, curSchd, fm.isManualSchedule());

			// -------------------------------------------------------------------------------------
			// Till Date and Future Date Totals
			// -------------------------------------------------------------------------------------

			// Till date Calculation
			if (DateUtil.compare(curSchdDate, valueDate) <= 0) {
				calTillDateTotals(pfd, curSchd);
			} else {
				calNextDateTotals(pfd, curSchd);

				if (TDS_SCHD_INDEX < -1) {
					if (curSchd.isTDSApplicable()) {
						TDS_SCHD_INDEX = i;
					} else {
						TDS_SCHD_INDEX = -1;
					}
				}
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				// Marked as Paid OR
				// Schedule >0 & Balance Interest = 0 OR
				if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()
						|| (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) > 0
								&& curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0)) {
					pfd.setFullPaidDate(curSchd.getSchDate());
				}
			}

			// -------------------------------------------------------------------------------------
			// ACCRUAL CALCULATION
			// -------------------------------------------------------------------------------------
			BigDecimal pftAmz = BigDecimal.ZERO;
			BigDecimal pftAmzNormal = BigDecimal.ZERO;
			BigDecimal pftAmzPD = BigDecimal.ZERO;
			BigDecimal acrNormal = BigDecimal.ZERO;

			// Amortization
			if (DateUtil.compare(curSchdDate, accrualDate) < 0) {
				pftAmz = curSchd.getProfitCalc();
			} else if (DateUtil.compare(accrualDate, prvSchdDate) > 0
					&& DateUtil.compare(accrualDate, nextSchdDate) <= 0) {
				int days = getNoDays(prvSchdDate, accrualDate);
				int daysInCurPeriod = getNoDays(prvSchdDate, curSchdDate);

				BigDecimal amzForCal = curSchd.getProfitCalc()
						.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
				pftAmz = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
						RoundingMode.HALF_DOWN);
				pftAmz = pftAmz.add(prvSchd.getProfitFraction());
				pftAmz = CalculationUtil.roundAmount(pftAmz, fm.getCalRoundingMode(), fm.getRoundingTarget());
			} else {
				// Do Nothing
			}

			acrNormal = pftAmz.subtract(curSchd.getSchdPftPaid());

			if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate())
					&& DateUtil.compare(curSchdDate, valueDate) < 0) {
				if ((curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
						|| (curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0))) {
					finState = CalculationConstants.FIN_STATE_PD;
				}
			}

			if (DateUtil.compare(curSchd.getSchDate(), pdDate) <= 0) {
				pftAmzNormal = pftAmz;
			}

			if (finState.equals(CalculationConstants.FIN_STATE_PD)) {
				// PD Amortization
				if (DateUtil.compare(curSchdDate, dateSusp) < 0) {
					pftAmzPD = pftAmz;
				} else if (DateUtil.compare(dateSusp, curSchdDate) >= 0
						&& DateUtil.compare(dateSusp, nextSchdDate) < 0) {
					int days = getNoDays(prvSchdDate, dateSusp);
					int daysInCurPeriod = getNoDays(prvSchdDate, curSchdDate);

					BigDecimal amzForCal = curSchd.getProfitCalc()
							.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
					pftAmzPD = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
							RoundingMode.HALF_DOWN);
					pftAmzPD = pftAmzPD.add(prvSchd.getProfitFraction());
					pftAmzPD = CalculationUtil.roundAmount(pftAmzPD, fm.getCalRoundingMode(), fm.getRoundingTarget());
				} else {
					// Do Nothing
				}

				pftAmzPD = pftAmzPD.subtract(pftAmzNormal);
			}

			// This field will carry amortization till suspend date at this stage
			pfd.setPftAccrueSusp(pfd.getPftAccrueSusp().add(acrNormal));

			// Set Amortization for various periods
			pfd.setPftAmz(pfd.getPftAmz().add(pftAmz));
			pfd.setPftAmzNormal(pfd.getPftAmzNormal().add(pftAmzNormal));
			pfd.setPftAmzPD(pfd.getPftAmzPD().add(pftAmzPD));
			pfd.setPftAccrued(pfd.getPftAccrued().add(acrNormal));
			// Calculated the mandate Amounts
			BigDecimal repayAmount = curSchd.getRepayAmount().add(curSchd.getFeeSchd())
					.subtract(curSchd.getPartialPaidAmt());
			if (repayAmount.compareTo(maxRepayAmount) > 0) {
				maxRepayAmount = repayAmount;
			}
			pfd.setMaxRpyAmount(maxRepayAmount);
		}
	}

	private static void calCumulativeTotals(FinanceProfitDetail pfd, FinanceScheduleDetail schd, boolean isManualSchd) {

		// profit
		pfd.setTotalPftSchd(pfd.getTotalPftSchd().add(schd.getProfitSchd()));
		pfd.setTotalPftCpz(pfd.getTotalPftCpz().add(schd.getCpzAmount()));
		pfd.setTotalPftPaid(pfd.getTotalPftPaid().add(schd.getSchdPftPaid()));

		// principal
		pfd.setTotalpriSchd(pfd.getTotalpriSchd().add(schd.getPrincipalSchd()));
		pfd.setTotalPriPaid(pfd.getTotalPriPaid().add(schd.getSchdPriPaid()));

		// Schedule Information
		if ((schd.isRepayOnSchDate() || schd.isPftOnSchDate())) {
			if ((schd.isFrqDate() && !isHoliday(schd.getBpiOrHoliday()))
					|| (schd.getSchDate().compareTo(pfd.getMaturityDate()) == 0) || isManualSchd) {
				if (!StringUtils.equals(schd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {

					// Installments, Paid and OD
					pfd.setNOInst(pfd.getNOInst() + 1);

					if (schd.isSchPftPaid() && schd.isSchPriPaid()) {
						pfd.setNOPaidInst(pfd.getNOPaidInst() + 1);
					}

					// First Repayments Date and Amount
					if (DateUtil.compare(schd.getSchDate(), pfd.getFinStartDate()) > 0) {
						if (DateUtil.compare(pfd.getFirstRepayDate(), pfd.getFinStartDate()) == 0) {
							pfd.setFirstRepayDate(schd.getSchDate());
							pfd.setFirstRepayAmt(schd.getPrincipalSchd().add(schd.getProfitSchd()));
						}
					}
				}
			}
		}

		// Final Repayments Amount
		if (schd.getSchDate().compareTo(pfd.getMaturityDate()) == 0) {
			pfd.setFinalRepayAmt(schd.getPrincipalSchd().add(schd.getProfitSchd()));
		}

		if (schd.isDisbOnSchDate()) {
			if (pfd.getFirstDisbDate().compareTo(pfd.getMaturityDate()) == 0) {
				pfd.setFirstDisbDate(schd.getSchDate());
				pfd.setLatestDisbDate(schd.getSchDate());
			}

			pfd.setLatestDisbDate(schd.getSchDate());
		}
	}

	private void calTillDateTotals(FinanceProfitDetail pfd, FinanceScheduleDetail schd) {

		// profit
		pfd.setTdSchdPft(pfd.getTdSchdPft().add(schd.getProfitSchd()));
		pfd.setTdPftCpz(pfd.getTdPftCpz().add(schd.getCpzAmount()));
		pfd.setTdSchdPftPaid(pfd.getTdSchdPftPaid().add(schd.getSchdPftPaid()));

		// principal
		pfd.setTdSchdPri(pfd.getTdSchdPri().add(schd.getPrincipalSchd()));
		pfd.setTdSchdPriPaid(pfd.getTdSchdPriPaid().add(schd.getSchdPriPaid()));

		// Fully paid Date. Fully paid flags will be only used for setting the fully paid date. will not update back in
		// the schedule
		if (schd.getProfitSchd().compareTo(schd.getSchdPftPaid()) <= 0) {
			schd.setSchPftPaid(true);
		}

		if (schd.getPrincipalSchd().compareTo(schd.getSchdPriPaid()) <= 0) {
			schd.setSchPriPaid(true);
		}

		if (schd.isSchPftPaid() && schd.isSchPriPaid()) {
			pfd.setFullPaidDate(schd.getSchDate());
		}

		if (schd.isPftOnSchDate() || schd.isRepayOnSchDate()) {
			if (schd.isFrqDate() && !isHoliday(schd.getBpiOrHoliday())) {
				pfd.setPrvRpySchDate(schd.getSchDate());
				pfd.setPrvRpySchPft(schd.getProfitSchd());
				pfd.setPrvRpySchPri(schd.getPrincipalSchd());
			}
		}

		BigDecimal schdPftDue = BigDecimal.ZERO;
		BigDecimal schdTDSDue = BigDecimal.ZERO;

		// TDS Calculation
		if (schd.isTDSApplicable()) {

			BigDecimal schdBal = schd.getPrincipalSchd().add(schd.getProfitSchd()).subtract(schd.getSchdPriPaid())
					.subtract(schd.getSchdPftPaid());
			if (schdBal.compareTo(BigDecimal.ZERO) > 0) {
				schdPftDue = schd.getProfitSchd().divide(TDS_MULTIPLIER, 0, RoundingMode.HALF_DOWN);
				schdTDSDue = schd.getProfitSchd().subtract(schdPftDue);
				schdTDSDue = CalculationUtil.roundAmount(schdTDSDue, TDS_ROUNDING_MODE, TDS_ROUNDING_TARGET);

				pfd.setTdTdsAmount(pfd.getTdTdsAmount().add(schdTDSDue));
				pfd.setTdTdsPaid(pfd.getTdTdsPaid().add(schd.getTDSPaid()));

				schdTDSDue = schdTDSDue.subtract(schd.getTDSPaid());

				// This will happen only when there is profit schedule is 1/-rs 1 and TDS is 1/-rs
				// Common issue 7
				if (schdPftDue.compareTo(schdTDSDue) <= 0) {
					schdTDSDue = BigDecimal.ZERO;
				}

				pfd.setTdTdsBal(pfd.getTdTdsBal().add(schdTDSDue));
			} else {
				pfd.setTdTdsAmount(pfd.getTdTdsAmount().add(schd.getTDSAmount()));
				pfd.setTdTdsPaid(pfd.getTdTdsPaid().add(schd.getTDSPaid()));
			}
		}

		pfd.setCurReducingRate(schd.getCalculatedRate());
		pfd.setCurCpzBalance(schd.getCpzBalance());

		pfd.setBaseRateCode(schd.getBaseRate());
		pfd.setSplRateCode(schd.getSplRate());
		pfd.setMrgRate(schd.getMrgRate());

	}

	private static void calNextDateTotals(FinanceProfitDetail pfd, FinanceScheduleDetail schd) {

		// advance Profit and Principal
		pfd.setTotalPftPaidInAdv(pfd.getTotalPftPaidInAdv().add(schd.getSchdPftPaid()));
		pfd.setTotalPriPaidInAdv(pfd.getTotalPriPaidInAdv().add(schd.getSchdPriPaid()));

		// NEXT Schedule Details
		if ((schd.isRepayOnSchDate() || schd.isPftOnSchDate())) {
			if ((schd.isFrqDate() && !isHoliday(schd.getBpiOrHoliday()))
					|| DateUtil.compare(schd.getSchDate(), pfd.getMaturityDate()) == 0) {
				if (DateUtil.compare(pfd.getNSchdDate(), pfd.getMaturityDate()) == 0) {
					pfd.setNSchdDate(schd.getSchDate());
					pfd.setNSchdPri(schd.getPrincipalSchd());
					pfd.setNSchdPft(schd.getProfitSchd());
					pfd.setNSchdPriDue(schd.getPrincipalSchd().subtract(schd.getSchdPriPaid()));
					pfd.setNSchdPftDue(schd.getProfitSchd().subtract(schd.getSchdPftPaid()));
				}

				if (!StringUtils.equals(schd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
					pfd.setFutureInst(pfd.getFutureInst() + 1);
				}
			}
		}

		if (DateUtil.compare(schd.getSchDate(), pfd.getMaturityDate()) == 0
				&& DateUtil.compare(pfd.getNSchdDate(), pfd.getMaturityDate()) == 0) {
			pfd.setNSchdPri(schd.getPrincipalSchd());
			pfd.setNSchdPft(schd.getProfitSchd());
			pfd.setNSchdPriDue(schd.getPrincipalSchd().subtract(schd.getSchdPriPaid()));
			pfd.setNSchdPftDue(schd.getProfitSchd().subtract(schd.getSchdPftPaid()));
		}

	}

	private static boolean isHoliday(String bpiOrHoliday) {
		if (StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_HOLIDAY)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_POSTPONE)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_MORTEMIHOLIDAY)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_UNPLANNED)) {
			return true;
		} else {
			return false;
		}
	}

	private void calculateTotals(FinanceMain fm, FinanceProfitDetail pfd, Date dateSusp, Date valueDate) {

		pfd.setTotalPftBal(pfd.getTotalPftSchd().subtract(pfd.getTotalPftPaid()));
		pfd.setTotalPriBal(pfd.getTotalpriSchd().subtract(pfd.getTotalPriPaid()));
		pfd.setTdSchdPftBal(pfd.getTdSchdPft().subtract(pfd.getTdSchdPftPaid()));
		pfd.setTdSchdPriBal(pfd.getTdSchdPri().subtract(pfd.getTdSchdPriPaid()));

		// Current Flat Rate
		BigDecimal calPart1 = pfd.getTotalPftSchd().add(pfd.getTotalPftCpz());
		BigDecimal calPart2 = pfd.getTotalpriSchd().subtract(pfd.getTotalPftCpz());
		pfd.setFinStartDate(pfd.getFinStartDate() == null ? fm.getFinStartDate() : pfd.getFinStartDate());
		BigDecimal daysFactor = CalculationUtil.getInterestDays(pfd.getFinStartDate(), pfd.getMaturityDate(),
				fm.getProfitDaysBasis());
		if (calPart2.compareTo(BigDecimal.ZERO) > 0 && daysFactor.compareTo(BigDecimal.ZERO) > 0) {
			pfd.setCurFlatRate(calPart1.divide((calPart2.multiply(new BigDecimal(100)).multiply(daysFactor)), 9,
					RoundingMode.HALF_DOWN));
		} else {
			pfd.setCurFlatRate(BigDecimal.ZERO);
		}

		// Calculated at individual level
		// pftDetail.setPftAccrued(pftDetail.getPftAmz().subtract(pftDetail.getTotalPftPaid()));

		// Provision Amortization
		if (pfd.isProvision()) {
			setProvisionData(pfd);
		}

		// Suspense Amortization
		if (DateUtil.compare(dateSusp, pfd.getMaturityDate()) <= 0) {
			pfd.setPftAmzSusp(pfd.getPftAmz().subtract(pfd.getPftAmzNormal()).subtract(pfd.getPftAmzPD()));
			pfd.setPftInSusp(true);
			// Value Equivalent accrual after suspended date
			pfd.setPftAccrueSusp(pfd.getPftAccrued().subtract(pfd.getPftAccrueSusp()));
		} else {
			pfd.setPftInSusp(false);
			pfd.setPftAccrueSusp(BigDecimal.ZERO);
		}

		int tenor = DateUtil.getMonthsBetween(valueDate, pfd.getMaturityDate());
		pfd.setRemainingTenor(tenor);

		tenor = DateUtil.getMonthsBetween(pfd.getFinStartDate(), pfd.getMaturityDate());
		pfd.setTotalTenor(tenor);

		// TDS Calculation
		if (TDS_SCHD_INDEX >= 0) {
			BigDecimal accrueDue = pfd.getPftAccrued().subtract(pfd.getTdSchdPftBal());
			BigDecimal accrueTDS = accrueDue.divide(TDS_MULTIPLIER, 0, RoundingMode.HALF_DOWN);
			accrueTDS = CalculationUtil.roundAmount(accrueTDS, TDS_ROUNDING_MODE, TDS_ROUNDING_TARGET);
			pfd.setTdsAccrued(pfd.getTdTdsBal().add(accrueTDS));
		}

	}

	private void setProvisionData(FinanceProfitDetail pftDetail) {
		if (pftDetail.getCurODDays() > 0) {
			pftDetail.setPftAmzSusp(pftDetail.getPftAccrued().subtract(pftDetail.getODProfit()));
			pftDetail.setPftAccrueSusp(pftDetail.getPftAccrued());

			pftDetail.setPftAmz(BigDecimal.ZERO);
			pftDetail.setPftAccrued(BigDecimal.ZERO);
		} else {
			pftDetail.setPftAmzSusp(BigDecimal.ZERO);
			pftDetail.setPftAccrueSusp(BigDecimal.ZERO);
		}
		pftDetail.setPftInSusp(false);
	}

	/**
	 * @param financeMain
	 * @param resultSet
	 */
	public void postAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) {
		String eventCode = AccountingEvent.AMZ;
		FinanceProfitDetail pfd = finEODEvent.getFinProfitDetail();
		FinanceMain fm = finEODEvent.getFinanceMain();

		if (pfd.isPftInSusp()) {
			eventCode = AccountingEvent.AMZSUSP;
		}

		Long accountingID = getAccountingID(fm, eventCode);
		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return;
		}

		EventProperties eventProperties = custEODEvent.getEventProperties();
		fm.setEventProperties(eventProperties);

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, pfd, finEODEvent.getFinanceScheduleDetails(), eventCode,
				custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		// Y - Accrual Effective Date will be Value Date, N - Accrual Effective Date will be APP Date
		if (!eventProperties.getAcEffValDate()) {
			aeEvent.setValueDate(eventProperties.getPostDate());
		}

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();

		aeAmountCodes.setNpa(finEODEvent.isNpaStage());
		BigDecimal advInst = finExcessAmountDAO.getBalAdvIntAmt(fm.getFinReference());
		if (advInst.compareTo(aeAmountCodes.getdAmz()) > 0 && advInst.compareTo(BigDecimal.ZERO) > 0) {
			aeAmountCodes.setAdvInst(aeAmountCodes.getdAmz());
			aeAmountCodes.setIntAdv(true);
		} else {
			aeAmountCodes.setAdvInst(advInst.compareTo(BigDecimal.ZERO) > 0 ? advInst : BigDecimal.ZERO);
		}

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setAppDate(eventProperties.getAppDate());
		aeEvent.setAppValueDate(eventProperties.getAppDate());
		aeEvent.setEventProperties(eventProperties);

		// Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);

		if (aeEvent.isuAmzExists()) {
			finEODEvent.setAccruedAmount(aeEvent.getAeAmountCodes().getdAmz());
		}

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		finEODEvent.setUpdLBDPostings(true);

		// posting done update the accrual balance
		pfd.setAmzTillLBD(pfd.getPftAmz());
		pfd.setAmzTillLBDNormal(pfd.getPftAmzNormal());
		pfd.setAmzTillLBDPD(pfd.getPftAmzPD());
		pfd.setAmzTillLBDPIS(pfd.getPftAmzSusp());
		pfd.setAcrTillLBD(pfd.getPftAccrued());
		pfd.setAcrSuspTillLBD(pfd.getPftAccrueSusp());
		pfd.setSvnAcrTillLBD(pfd.getSvnPftAmount());

		pfd.setGapIntAmzLbd(pfd.getGapIntAmz());

		// Month End move all the balances to previous month also
		if (DateUtil.getDay(custEODEvent.getEodValueDate()) == 1) {
			pfd.setPrvMthAcr(pfd.getPftAccrued());
			pfd.setPrvMthAcrSusp(pfd.getPftAccrueSusp());
			pfd.setPrvMthAmz(pfd.getPftAmz());
			pfd.setPrvMthAmzNrm(pfd.getPftAmzNormal());
			pfd.setPrvMthAmzPD(pfd.getPftAmzPD());
			pfd.setPrvMthAmzSusp(pfd.getPftAmzSusp());
			pfd.setPrvMthGapIntAmz(pfd.getGapIntAmz());
			finEODEvent.setUpdMonthEndPostings(true);
		}
		// these fields should be update after the accrual posting only so these will not be considered in normal
		// update.
	}

	private static int getNoDays(Date date1, Date date2) {
		return DateUtil.getDaysBetween(date1, date2);
	}

	/**
	 * Method for returning AccrualMonthEnd list from current MonthEnd to MaturityDate
	 * 
	 * @param fm
	 * @param schedules
	 * @param appDate
	 * @return
	 */
	public static List<ProjectedAccrual> getAccrualsFromCurMonthEnd(FinanceMain fm,
			List<FinanceScheduleDetail> schedules, Date appDate, String fromFinStartDate) {

		List<ProjectedAccrual> list = new ArrayList<ProjectedAccrual>();

		// Calculate Month End Amortization From FinStartDate to Maturity
		List<ProjectedAccrual> accrualList = calAccrualsOnMonthEnd(fm, schedules, appDate, BigDecimal.ZERO, true);

		// ALM Extraction configuration from SMT parameter
		if ("Y".equals(fromFinStartDate)) {

			list = accrualList;
		} else {
			for (ProjectedAccrual projectedAccrual : accrualList) {
				if (DateUtil.compare(projectedAccrual.getAccruedOn(), appDate) >= 0) {
					list.add(projectedAccrual);
				}
			}
		}

		return list;
	}

	/**
	 * Month End Amortization Calculation
	 * 
	 * @param fm
	 * @param schedules
	 * @param appDate
	 * @return
	 */
	public static List<ProjectedAccrual> calAccrualsOnMonthEnd(FinanceMain fm, List<FinanceScheduleDetail> schedules,
			Date appDate, BigDecimal prvMthAmz, boolean calFromFinStartDate) {

		Map<Date, ProjectedAccrual> map = new HashMap<>(1);
		List<ProjectedAccrual> list = new ArrayList<>();
		Date appDateMonthStart = DateUtil.getMonthStart(appDate);
		Date appDateMonthEnd = DateUtil.getMonthEnd(appDate);
		BigDecimal totalProfit = BigDecimal.ZERO;
		BigDecimal cummAccAmt = BigDecimal.ZERO;

		if (DateUtil.compare(fm.getMaturityDate(), appDateMonthStart) < 0) {
			return list;
		}

		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;
		Date prvMonthEnd = null;

		List<Date> months = new ArrayList<>();
		List<Date> monthsCopy = new ArrayList<>();
		Date newMonth = null;

		Date monthEnd = DateUtil.getMonthEnd(fm.getFinStartDate());
		if (calFromFinStartDate || DateUtil.compare(monthEnd, appDateMonthEnd) == 0) {
			newMonth = monthEnd;
		} else {
			newMonth = appDateMonthEnd;
			prvMonthEnd = DateUtil.addDays(appDateMonthStart, -1);
			cummAccAmt = prvMthAmz;

			ProjectedAccrual prjAcc = new ProjectedAccrual();
			prjAcc.setFinID(fm.getFinID());
			prjAcc.setFinReference(fm.getFinReference());
			prjAcc.setAccruedOn(prvMonthEnd);
			prjAcc.setCumulativeAccrued(prvMthAmz);
			map.put(prvMonthEnd, prjAcc);
		}

		// Prepare Months list From FinStartDate to MaturityDate
		while (DateUtil.compare(DateUtil.getMonthEnd(fm.getMaturityDate()), newMonth) >= 0) {
			months.add((Date) newMonth.clone());
			newMonth = DateUtil.addMonths(newMonth, 1);
			newMonth = DateUtil.getMonthEnd(newMonth);
		}
		monthsCopy.addAll(months);

		for (int i = 0; i < schedules.size(); i++) {
			curSchd = schedules.get(i);
			curSchdDate = curSchd.getSchDate();
			totalProfit = totalProfit.add(curSchd.getProfitCalc());

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schedules.get(i - 1);
			}
			if (i == schedules.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schedules.get(i + 1);
			}

			prvSchdDate = prvSchd.getSchDate();
			nextSchdDate = nextSchd.getSchDate();

			if (!calFromFinStartDate && DateUtil.compare(curSchdDate, appDateMonthStart) < 0) {
				continue;
			}

			for (Date curMonthEnd : monthsCopy) {
				BigDecimal schdPftAmz = BigDecimal.ZERO;
				BigDecimal curPftAmz = BigDecimal.ZERO;
				BigDecimal prvPftAmz = BigDecimal.ZERO;
				BigDecimal pftAmz = BigDecimal.ZERO;
				boolean isSchdPftAmz = false;
				ProjectedAccrual prjAcc = null;

				if (map.containsKey(curMonthEnd)) {
					prjAcc = map.get(curMonthEnd);
				} else {
					prjAcc = new ProjectedAccrual();
					prjAcc.setFinID(fm.getFinID());
					prjAcc.setFinReference(fm.getFinReference());
					prjAcc.setAccruedOn(curMonthEnd);
				}

				// ACCRUAL calculation includes current date
				Date nextMonthStart = DateUtil.addDays(curMonthEnd, 1);
				Date curMonthStart = DateUtil.getMonthStart(curMonthEnd);

				// Schedules between Previous MonthEnd to CurMonthEnd
				if (DateUtil.compare(prvSchdDate, curMonthStart) >= 0
						&& DateUtil.compare(curSchdDate, curMonthEnd) <= 0) {

					schdPftAmz = curSchd.getProfitCalc();
					isSchdPftAmz = true;
				}

				if (DateUtil.compare(curMonthEnd, curSchdDate) < 0) {

					// Months Between schedules
					curPftAmz = calProfitAmz(curSchd, prvSchd, nextMonthStart, curMonthStart, fm);
				} else {

					// Profit Calculation From MonthEnd to CurSchdDate
					if (DateUtil.compare(curMonthEnd, prvSchdDate) >= 0
							&& DateUtil.compare(curMonthEnd, nextSchdDate) < 0) {
						curPftAmz = calProfitAmz(nextSchd, curSchd, nextMonthStart, curSchdDate, fm);
					}

					// Profit Calculation From CurSchdDate to Previous MonthEnd
					if (prvMonthEnd != null && !isSchdPftAmz) {
						prvPftAmz = calProfitAmz(curSchd, prvSchd, curSchdDate, curMonthStart, fm);
					}
				}

				pftAmz = schdPftAmz.add(prvPftAmz).add(curPftAmz);

				// Adjust remaining profit to maturity MonthEnd to avoid rounding issues
				if (DateUtil.compare(DateUtil.getMonthEnd(fm.getMaturityDate()), curMonthEnd) == 0) {
					prjAcc.setPftAmz(totalProfit.subtract(cummAccAmt));
				} else {
					prjAcc.setPftAmz(prjAcc.getPftAmz().add(pftAmz));
				}

				if (DateUtil.compare(curSchdDate, curMonthStart) >= 0
						&& DateUtil.compare(curSchdDate, curMonthEnd) <= 0) {
					if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
						prjAcc.setSchdDate(curSchdDate);
						prjAcc.setSchdPri(curSchd.getPrincipalSchd().subtract(curSchd.getCpzAmount()));
						prjAcc.setSchdPft(curSchd.getProfitSchd().add(curSchd.getCpzAmount()));
						prjAcc.setSchdTot(curSchd.getRepayAmount());
					}
				}

				// Current and Next Schedules are equal in Maturity
				if (DateUtil.compare(curMonthEnd, nextSchdDate) < 0
						|| DateUtil.compare(curSchdDate, nextSchdDate) == 0) {

					cummAccAmt = cummAccAmt.add(prjAcc.getPftAmz());
					prjAcc.setPftAccrued(prjAcc.getPftAmz());
					prjAcc.setCumulativeAccrued(cummAccAmt);

					prvMonthEnd = curMonthEnd;
					months.remove(curMonthEnd);
					monthsCopy = new ArrayList<Date>(months);
				}

				// Prepare Map and List
				if (!map.containsKey(curMonthEnd)) {
					map.put(curMonthEnd, prjAcc);
					list.add(map.get(curMonthEnd));
				}

				if (DateUtil.compare(curMonthEnd, curSchdDate) >= 0) {
					break;
				}
			}
		}

		return list;
	}

	/**
	 * Method for calculate part of the profit, Rounding calculation not required.
	 * 
	 * @param schdDetail
	 * @param monthEnd
	 * @return
	 * 
	 */
	private static BigDecimal calProfitAmz(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd, Date date1,
			Date date2, FinanceMain fm) {

		BigDecimal pftAmz = BigDecimal.ZERO;

		int days = DateUtil.getDaysBetween(date1, date2);
		int daysInCurPeriod = DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchd.getSchDate());

		BigDecimal amzForCal = curSchd.getProfitCalc()
				.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
		pftAmz = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
				RoundingMode.HALF_DOWN);
		pftAmz = pftAmz.add(prvSchd.getProfitFraction());
		pftAmz = CalculationUtil.roundAmount(pftAmz, fm.getCalRoundingMode(), fm.getRoundingTarget());
		return pftAmz;
	}

	public void setSMTParms() {

		if (!StringUtils.isBlank(TDS_ROUNDING_MODE)) {
			return;
		}

		TDS_ROUNDING_MODE = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
		TDS_ROUNDING_TARGET = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		TDS_PERCENTAGE = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		TDS_MULTIPLIER = HUNDERED.divide(HUNDERED.subtract(TDS_PERCENTAGE), 20, RoundingMode.HALF_DOWN);

		TDS_SCHD_INDEX = -2;
	}

	private void calGapInterest(FinanceMain fm, FinanceProfitDetail fpd, Date valueDate) {
		List<IRRScheduleDetail> irrSDList = irrScheduleDetailDAO.getIRRScheduleDetailList(fm.getFinID());

		IRRScheduleDetail prvSchd = null;
		IRRScheduleDetail curSchd = null;
		IRRScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;
		Date accrualDate = null;
		EventProperties eventProperties = fm.getEventProperties();

		if (eventProperties.isParameterLoaded()) {
			accrualDate = DateUtil.addDays(valueDate, eventProperties.getAccrualCalOn());
		} else {
			int valueToadd = SysParamUtil.getValueAsInt(SMTParameterConstants.ACCRUAL_CAL_ON);
			accrualDate = DateUtil.addDays(valueDate, valueToadd);
		}

		BigDecimal totIrrAmz = BigDecimal.ZERO;

		for (int i = 0; i < irrSDList.size(); i++) {
			curSchd = irrSDList.get(i);
			curSchdDate = curSchd.getSchDate();

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = irrSDList.get(i - 1);
			}

			prvSchdDate = prvSchd.getSchDate();

			// Next details: in few cases there might be schedules present even after the maturity date. ex: when
			// calculating the fees
			if (DateUtil.compare(curSchdDate, fm.getMaturityDate()) == 0 || i == (irrSDList.size() - 1)) {
				nextSchd = curSchd;
			} else {
				nextSchd = irrSDList.get(i + 1);
			}

			nextSchdDate = nextSchd.getSchDate();

			// -------------------------------------------------------------------------------------
			// IRR ACCRUAL CALCULATION
			// -------------------------------------------------------------------------------------
			BigDecimal irrAmz = BigDecimal.ZERO;

			// Amortization
			if (DateUtil.compare(curSchdDate, accrualDate) < 0) {
				irrAmz = curSchd.getProfitCalc();
			} else if (DateUtil.compare(accrualDate, prvSchdDate) > 0
					&& DateUtil.compare(accrualDate, nextSchdDate) <= 0) {
				int days = getNoDays(prvSchdDate, accrualDate);
				int daysInCurPeriod = getNoDays(prvSchdDate, curSchdDate);

				irrAmz = curSchd.getProfitCalc().multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod),
						9, RoundingMode.HALF_DOWN);
				irrAmz = CalculationUtil.roundAmount(irrAmz, fm.getCalRoundingMode(), fm.getRoundingTarget());
			} else {
				break;
			}

			totIrrAmz = totIrrAmz.add(irrAmz);
		}

		fpd.setGapIntAmz(totIrrAmz.subtract(fpd.getPftAmz()));
	}

	private BigDecimal calculateSvnPftAmount(FinanceMain fm, Date accrualDate) {
		long finID = fm.getFinID();
		List<SubventionScheduleDetail> list = subventionDetailDAO.getSubventionScheduleDetails(finID, 0, "");

		SubventionScheduleDetail svnPrvSchd = null;
		SubventionScheduleDetail svnCurSchd = null;
		BigDecimal svnPftAmount = BigDecimal.ZERO;

		for (int i = 0; i < list.size(); i++) {

			svnCurSchd = list.get(i);

			if (i == 0) {
				svnPrvSchd = svnCurSchd;
				svnPftAmount = svnPftAmount.add(svnCurSchd.getPresentValue());
				continue;
			} else {
				svnPrvSchd = list.get(i - 1);
			}

			if (svnCurSchd.getDisbSeqID() != svnPrvSchd.getDisbSeqID()) {
				svnPrvSchd = svnCurSchd;
				svnPftAmount = svnPftAmount.add(svnCurSchd.getPresentValue());
				continue;
			}

			if (DateUtil.compare(svnCurSchd.getSchDate(), accrualDate) > 0
					&& DateUtil.compare(fm.getGrcPeriodEndDate(), accrualDate) > 0) {

				if (DateUtil.compare(svnPrvSchd.getSchDate(), accrualDate) > 0) {
					continue;
				}
				int days = getNoDays(svnPrvSchd.getSchDate(), accrualDate);
				int daysInCurPeriod = getNoDays(svnPrvSchd.getSchDate(), svnCurSchd.getSchDate());

				BigDecimal amzForCal = svnCurSchd.getPresentValue();
				BigDecimal pftAmz = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
						RoundingMode.HALF_DOWN);
				pftAmz = CalculationUtil.roundAmount(pftAmz, RoundingMode.HALF_DOWN.name(), 0);

				svnPftAmount = svnPftAmount.add(pftAmz);
			} else {
				svnPftAmount = svnPftAmount.add(svnCurSchd.getPresentValue());
			}

		}
		return svnPftAmount;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setIrrScheduleDetailDAO(IRRScheduleDetailDAO irrScheduleDetailDAO) {
		this.irrScheduleDetailDAO = irrScheduleDetailDAO;
	}

}
