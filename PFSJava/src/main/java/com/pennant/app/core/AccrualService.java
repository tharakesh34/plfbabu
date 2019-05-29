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
 * FileName    		:  AccrualService.java                                                  * 	  
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;

public class AccrualService extends ServiceHelper {
	private static final long serialVersionUID = 6161809223570900644L;
	private static Logger logger = Logger.getLogger(AccrualService.class);

	private FinanceSuspHeadDAO suspHeadDAO;

	public BigDecimal big100 = new BigDecimal(100);
	public String tdsRoundMode = null;
	public int tdsRoundingTarget = 0;
	public BigDecimal tdsPerc = BigDecimal.ZERO;
	public BigDecimal tdsMultiplier = BigDecimal.ZERO;
	public int tdsSchdIdx = -2;

	public CustEODEvent processAccrual(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			finEODEvent = calculateAccruals(finEODEvent, custEODEvent);
		}
		logger.debug(" Leaving ");

		return custEODEvent;

	}

	public FinEODEvent calculateAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");

		FinanceMain finMain = finEODEvent.getFinanceMain();
		finMain.setRoundingTarget(finEODEvent.getFinType().getRoundingTarget());
		List<FinanceScheduleDetail> scheduleDetailList = finEODEvent.getFinanceScheduleDetails();

		// Finance Profit Details
		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();
		if (profitDetail.getFinReference() == null) {
			profitDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(finMain.getFinReference());
		}

		profitDetail = calProfitDetails(finMain, scheduleDetailList, profitDetail, custEODEvent.getEodValueDate());

		//FIXME: PV 15MAY17: To confirm it is being updated in latePayMarkingService.processDPDBuketing OR latePayMarkingService.processCustomerStatus
		//String worstSts = getCustomerStatusCodeDAO().getFinanceStatus(finReference, false);
		//profitDetail.setFinWorstStatus(worstSts);

		//post Accruals on Application Extended Month End OR Application Month End OR Daily
		int amzPostingEvent = SysParamUtil.getValueAsInt(AccountConstants.AMZ_POSTING_EVENT);
		boolean isAmzPostToday = false;
		if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_MTH_END) {
			if (custEODEvent.getEodDate().compareTo(DateUtility.getMonthEnd(custEODEvent.getEodDate())) == 0) {
				isAmzPostToday = true;
			}
		} else if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_EXT_MTH_END) {
			if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
				if (getEodConfig().getMnthExtTo().compareTo(custEODEvent.getEodDate()) == 0) {
					isAmzPostToday = true;
				}
			}

		} else {
			isAmzPostToday = true;
		}

		if (isAmzPostToday) {
			postAccruals(finEODEvent, custEODEvent);
		}

		logger.debug(" Leaving ");
		return finEODEvent;
	}

	public FinanceProfitDetail calProfitDetails(FinanceMain finMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, Date valueDate) {
		logger.debug("Entering");

		setSMTParms();

		String finRef = finMain.getFinReference();
		Date dateSusp = null;

		int suspReq = SysParamUtil.getValueAsInt(SMTParameterConstants.SUSP_CHECK_REQ);

		if (suspReq == 1) {
			if (!StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				dateSusp = suspHeadDAO.getFinSuspDate(finRef);
			}
		}

		if (dateSusp == null) {
			dateSusp = DateUtility.addDays(finMain.getMaturityDate(), 1);
		}

		//Reset Totals
		resetCalculatedTotals(finMain, pftDetail);

		//Calculate Accruals
		//FIXME: How schdDetails will be empty? OD loans before disbursement?
		if (schdDetails == null || schdDetails.isEmpty()) {
		} else {
			calAccruals(finMain, schdDetails, pftDetail, valueDate, dateSusp);
		}

		//Gross Totals
		calculateTotals(finMain, pftDetail, dateSusp, valueDate);

		return pftDetail;

	}

	private void resetCalculatedTotals(FinanceMain finMain, FinanceProfitDetail pftDetail) {

		if (StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			pftDetail.setFinReference(finMain.getFinReference());
			pftDetail.setFinStartDate(finMain.getFinStartDate());
			pftDetail.setCustId(finMain.getCustID());
			pftDetail.setCustCIF(finMain.getCustCIF());
			pftDetail.setFinBranch(finMain.getFinBranch());
			pftDetail.setFinType(finMain.getFinType());
			pftDetail.setFinCcy(finMain.getFinCcy());
			pftDetail.setFinPurpose(finMain.getFinPurpose());
			pftDetail.setFinContractDate(finMain.getFinContractDate());
			pftDetail.setFinApprovedDate(finMain.getFinApprovedDate());
			pftDetail.setFullPaidDate(finMain.getFinStartDate());
			pftDetail.setFinAmount(finMain.getFinAmount());
			pftDetail.setDownPayment(finMain.getDownPayment());
			pftDetail.setFinCommitmentRef(finMain.getFinCommitmentRef());
			pftDetail.setFinCategory(finMain.getFinCategory());
			pftDetail.setProductCategory(finMain.getProductCategory());
			pftDetail.setFirstODDate(pftDetail.getFinStartDate());
			pftDetail.setPrvODDate(pftDetail.getFinStartDate());
			pftDetail.setAdvanceEMI(finMain.getAdvanceEMI());

		}

		//Miscellaneous Fields
		pftDetail.setLastMdfDate(DateUtility.getAppDate());
		pftDetail.setMaturityDate(finMain.getMaturityDate());
		pftDetail.setFinIsActive(finMain.isFinIsActive());
		pftDetail.setClosingStatus(finMain.getClosingStatus());
		pftDetail.setRepayFrq(finMain.getRepayFrq());
		pftDetail.setFinStatus(finMain.getFinStatus());
		pftDetail.setFinStsReason(finMain.getFinStsReason());
		pftDetail.setFinWorstStatus(finMain.getFinStatus());

		//Setting date for recal purpose
		pftDetail.setFirstRepayDate(pftDetail.getFinStartDate());
		pftDetail.setPrvRpySchDate(pftDetail.getFinStartDate());
		pftDetail.setNSchdDate(pftDetail.getMaturityDate());
		pftDetail.setFirstDisbDate(pftDetail.getMaturityDate());
		pftDetail.setLatestDisbDate(pftDetail.getMaturityDate());
		pftDetail.setLatestRpyDate(finMain.getFinStartDate());

		pftDetail.setBaseRateCode(null);
		pftDetail.setSplRateCode(null);

		//Interest Calculaiton on Pastdue
		if (StringUtils.equals(finMain.getPastduePftCalMthd(), CalculationConstants.PDPFTCAL_NOTAPP)) {
			pftDetail.setCalPftOnPD(false);
		} else {
			pftDetail.setCalPftOnPD(true);
		}
		pftDetail.setPftOnPDMethod(finMain.getPastduePftCalMthd());
		pftDetail.setPftOnPDMrg(finMain.getPastduePftMargin());

		// profit
		pftDetail.setTotalPftSchd(BigDecimal.ZERO);
		pftDetail.setTotalPftCpz(BigDecimal.ZERO);
		pftDetail.setTotalPftPaid(BigDecimal.ZERO);
		pftDetail.setTotalPftBal(BigDecimal.ZERO);

		pftDetail.setTdSchdPft(BigDecimal.ZERO);
		pftDetail.setTdPftCpz(BigDecimal.ZERO);
		pftDetail.setTdSchdPftPaid(BigDecimal.ZERO);
		pftDetail.setTdSchdPftBal(BigDecimal.ZERO);

		pftDetail.setNSchdPft(BigDecimal.ZERO);
		pftDetail.setNSchdPftDue(BigDecimal.ZERO);
		pftDetail.setPrvRpySchPft(BigDecimal.ZERO);

		// principal
		pftDetail.setTotalpriSchd(BigDecimal.ZERO);
		pftDetail.setTotalPriPaid(BigDecimal.ZERO);
		pftDetail.setTotalPriBal(BigDecimal.ZERO);

		pftDetail.setTdSchdPri(BigDecimal.ZERO);
		pftDetail.setTdSchdPriPaid(BigDecimal.ZERO);
		pftDetail.setTdSchdPriBal(BigDecimal.ZERO);

		pftDetail.setNSchdPri(BigDecimal.ZERO);
		pftDetail.setNSchdPriDue(BigDecimal.ZERO);
		pftDetail.setPrvRpySchPri(BigDecimal.ZERO);

		// advised Profit & Rebate
		pftDetail.setTotalAdvPftSchd(BigDecimal.ZERO);
		pftDetail.setTotalRbtSchd(BigDecimal.ZERO);
		pftDetail.setTdSchdAdvPft(BigDecimal.ZERO);
		pftDetail.setTdSchdRbt(BigDecimal.ZERO);

		//Accruals and amortizations
		pftDetail.setPftAccrued(BigDecimal.ZERO);
		pftDetail.setPftAccrueSusp(BigDecimal.ZERO);
		pftDetail.setPftAmz(BigDecimal.ZERO);
		pftDetail.setPftAmzNormal(BigDecimal.ZERO);
		pftDetail.setPftAmzPD(BigDecimal.ZERO);
		pftDetail.setPftAmzSusp(BigDecimal.ZERO);

		pftDetail.setTotalPriPaidInAdv(BigDecimal.ZERO);
		pftDetail.setTotalPftPaidInAdv(BigDecimal.ZERO);

		//Terms
		pftDetail.setNOInst(0);
		pftDetail.setNOPaidInst(0);
		pftDetail.setFutureInst(0);
		pftDetail.setRemainingTenor(0);
		pftDetail.setTotalTenor(0);

		//TDS
		pftDetail.setTdTdsAmount(BigDecimal.ZERO);
		pftDetail.setTdTdsPaid(BigDecimal.ZERO);
		pftDetail.setTdTdsBal(BigDecimal.ZERO);
		pftDetail.setTdsAccrued(BigDecimal.ZERO);
	}

	private void calAccruals(FinanceMain finMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, Date valueDate, Date dateSusp) {
		String finState = CalculationConstants.FIN_STATE_NORMAL;
		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;
		BigDecimal maxRepayAmount = BigDecimal.ZERO;
		int valueToadd = SysParamUtil.getValueAsInt(SMTParameterConstants.ACCRUAL_CAL_ON);
		Date accrualDate = DateUtility.addDays(valueDate, valueToadd);
		Date pdDate = pftDetail.getPrvODDate();

		for (int i = 0; i < schdDetails.size(); i++) {
			curSchd = schdDetails.get(i);
			curSchdDate = curSchd.getSchDate();

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schdDetails.get(i - 1);
			}

			prvSchdDate = prvSchd.getSchDate();

			// Next details: in few cases  there might be schedules present even after the maturity date. ex: when calculating the fees
			if (curSchdDate.compareTo(finMain.getMaturityDate()) == 0 || i == schdDetails.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schdDetails.get(i + 1);
			}

			nextSchdDate = nextSchd.getSchDate();

			//-------------------------------------------------------------------------------------
			//Cumulative Totals
			//-------------------------------------------------------------------------------------
			calCumulativeTotals(pftDetail, curSchd);

			//-------------------------------------------------------------------------------------
			//Till Date and Future Date Totals
			//-------------------------------------------------------------------------------------

			// Till date Calculation
			if (curSchdDate.compareTo(valueDate) <= 0) {
				calTillDateTotals(pftDetail, curSchd);
			} else {
				calNextDateTotals(pftDetail, curSchd);

				if (tdsSchdIdx < -1) {
					if (curSchd.isTDSApplicable()) {
						tdsSchdIdx = i;
					} else {
						tdsSchdIdx = -1;
					}
				}
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				// Marked as Paid OR
				// Schedule >0 & Balance Interest = 0 OR
				if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()
						|| (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) > 0
								&& curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0)) {
					pftDetail.setFullPaidDate(curSchd.getSchDate());
				}
			}

			//-------------------------------------------------------------------------------------
			//ACCRUAL CALCULATION
			//-------------------------------------------------------------------------------------
			BigDecimal pftAmz = BigDecimal.ZERO;
			BigDecimal pftAmzNormal = BigDecimal.ZERO;
			BigDecimal pftAmzPD = BigDecimal.ZERO;
			BigDecimal acrNormal = BigDecimal.ZERO;

			// Amortization
			if (curSchdDate.compareTo(accrualDate) < 0) {
				pftAmz = curSchd.getProfitCalc();
			} else if (accrualDate.compareTo(prvSchdDate) > 0 && accrualDate.compareTo(nextSchdDate) <= 0) {
				int days = getNoDays(prvSchdDate, accrualDate);
				int daysInCurPeriod = getNoDays(prvSchdDate, curSchdDate);

				BigDecimal amzForCal = curSchd.getProfitCalc()
						.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
				pftAmz = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
						RoundingMode.HALF_DOWN);
				pftAmz = pftAmz.add(prvSchd.getProfitFraction());
				pftAmz = CalculationUtil.roundAmount(pftAmz, finMain.getCalRoundingMode(), finMain.getRoundingTarget());
			} else {
				//Do Nothing
			}

			acrNormal = pftAmz.subtract(curSchd.getSchdPftPaid());

			if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) && curSchdDate.compareTo(valueDate) < 0) {
				if ((curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
						|| (curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0))) {
					finState = CalculationConstants.FIN_STATE_PD;
				}
			}

			if (DateUtility.compare(curSchd.getSchDate(), pdDate) <= 0) {
				pftAmzNormal = pftAmz;
			}

			if (finState.equals(CalculationConstants.FIN_STATE_PD)) {
				// PD Amortization
				if (curSchdDate.compareTo(dateSusp) < 0) {
					pftAmzPD = pftAmz;
				} else if (dateSusp.compareTo(curSchdDate) >= 0 && dateSusp.compareTo(nextSchdDate) < 0) {
					int days = getNoDays(prvSchdDate, dateSusp);
					int daysInCurPeriod = getNoDays(prvSchdDate, curSchdDate);

					BigDecimal amzForCal = curSchd.getProfitCalc()
							.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
					pftAmzPD = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
							RoundingMode.HALF_DOWN);
					pftAmzPD = pftAmzPD.add(prvSchd.getProfitFraction());
					pftAmzPD = CalculationUtil.roundAmount(pftAmzPD, finMain.getCalRoundingMode(),
							finMain.getRoundingTarget());
				} else {
					//Do Nothing
				}

				pftAmzPD = pftAmzPD.subtract(pftAmzNormal);
			}

			//This field will carry amortization till suspend date at this stage
			pftDetail.setPftAccrueSusp(pftDetail.getPftAccrueSusp().add(acrNormal));

			//Set Amortization for various periods
			pftDetail.setPftAmz(pftDetail.getPftAmz().add(pftAmz));
			pftDetail.setPftAmzNormal(pftDetail.getPftAmzNormal().add(pftAmzNormal));
			pftDetail.setPftAmzPD(pftDetail.getPftAmzPD().add(pftAmzPD));
			pftDetail.setPftAccrued(pftDetail.getPftAccrued().add(acrNormal));
			//Calculated the mandate Amounts
			BigDecimal repayAmount = curSchd.getRepayAmount().add(curSchd.getFeeSchd())
					.subtract(curSchd.getPartialPaidAmt());
			if (repayAmount.compareTo(maxRepayAmount) > 0) {
				maxRepayAmount = repayAmount;
			}
			pftDetail.setMaxRpyAmount(maxRepayAmount);
		}

	}

	private static void calCumulativeTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// profit
		pftDetail.setTotalPftSchd(pftDetail.getTotalPftSchd().add(curSchd.getProfitSchd()));
		pftDetail.setTotalPftCpz(pftDetail.getTotalPftCpz().add(curSchd.getCpzAmount()));
		pftDetail.setTotalPftPaid(pftDetail.getTotalPftPaid().add(curSchd.getSchdPftPaid()));

		// principal
		pftDetail.setTotalpriSchd(pftDetail.getTotalpriSchd().add(curSchd.getPrincipalSchd()));
		pftDetail.setTotalPriPaid(pftDetail.getTotalPriPaid().add(curSchd.getSchdPriPaid()));

		// advised Profit
		pftDetail.setTotalAdvPftSchd(pftDetail.getTotalAdvPftSchd().add(curSchd.getAdvProfit()));
		pftDetail.setTotalRbtSchd(pftDetail.getTotalRbtSchd().add(curSchd.getRebate()));

		//Schedule Information
		if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate())) {
			if ((curSchd.isFrqDate() && !isHoliday(curSchd.getBpiOrHoliday()))
					|| curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0) {
				//Installments, Paid and OD
				pftDetail.setNOInst(pftDetail.getNOInst() + 1);

				if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
					pftDetail.setNOPaidInst(pftDetail.getNOPaidInst() + 1);
				}

				//First Repayments Date and Amount
				if (curSchd.getSchDate().compareTo(pftDetail.getFinStartDate()) > 0) {
					if (pftDetail.getFirstRepayDate().compareTo(pftDetail.getFinStartDate()) == 0) {
						pftDetail.setFirstRepayDate(curSchd.getSchDate());
						pftDetail.setFirstRepayAmt(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
					}
				}
			}
		}

		//Final Repayments Amount
		if (curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0) {
			pftDetail.setFinalRepayAmt(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
		}

		if (curSchd.isDisbOnSchDate()) {
			if (pftDetail.getFirstDisbDate().compareTo(pftDetail.getMaturityDate()) == 0) {
				pftDetail.setFirstDisbDate(curSchd.getSchDate());
				pftDetail.setLatestDisbDate(curSchd.getSchDate());
			}

			pftDetail.setLatestDisbDate(curSchd.getSchDate());
		}

		logger.debug("Leaving");
	}

	private void calTillDateTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// profit
		pftDetail.setTdSchdPft(pftDetail.getTdSchdPft().add(curSchd.getProfitSchd()));
		pftDetail.setTdPftCpz(pftDetail.getTdPftCpz().add(curSchd.getCpzAmount()));
		pftDetail.setTdSchdPftPaid(pftDetail.getTdSchdPftPaid().add(curSchd.getSchdPftPaid()));

		// principal
		pftDetail.setTdSchdPri(pftDetail.getTdSchdPri().add(curSchd.getPrincipalSchd()));
		pftDetail.setTdSchdPriPaid(pftDetail.getTdSchdPriPaid().add(curSchd.getSchdPriPaid()));

		// advised Profit
		pftDetail.setTdSchdAdvPft(pftDetail.getTdSchdAdvPft().add(curSchd.getAdvProfit()));
		pftDetail.setTdSchdRbt(pftDetail.getTdSchdRbt().add(curSchd.getRebate()));

		//Fully paid Date. Fully paid flags will be only used for setting the fully paid date. will not update back in the schedule
		if (curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) <= 0) {
			curSchd.setSchPftPaid(true);
		}

		if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) <= 0) {
			curSchd.setSchPriPaid(true);
		}

		if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
			pftDetail.setFullPaidDate(curSchd.getSchDate());
		}

		if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
			if (curSchd.isFrqDate() && !isHoliday(curSchd.getBpiOrHoliday())) {
				pftDetail.setPrvRpySchDate(curSchd.getSchDate());
				pftDetail.setPrvRpySchPft(curSchd.getProfitSchd());
				pftDetail.setPrvRpySchPri(curSchd.getPrincipalSchd());
			}
		}

		BigDecimal schdPftDue = BigDecimal.ZERO;
		BigDecimal schdTDSDue = BigDecimal.ZERO;

		// TDS Calculation
		if (curSchd.isTDSApplicable()) {
			if (!curSchd.isSchPftPaid() || !curSchd.isSchPriPaid()) {
				schdPftDue = curSchd.getProfitSchd().divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
				schdTDSDue = curSchd.getProfitSchd().subtract(schdPftDue);
				schdTDSDue = CalculationUtil.roundAmount(schdTDSDue, tdsRoundMode, tdsRoundingTarget);

				pftDetail.setTdTdsAmount(pftDetail.getTdTdsAmount().add(schdTDSDue));
				pftDetail.setTdTdsPaid(pftDetail.getTdTdsPaid().add(curSchd.getTDSPaid()));

				schdTDSDue = schdTDSDue.subtract(curSchd.getTDSPaid());
				pftDetail.setTdTdsBal(pftDetail.getTdTdsBal().add(schdTDSDue));
			} else {
				pftDetail.setTdTdsAmount(pftDetail.getTdTdsAmount().add(curSchd.getTDSAmount()));
				pftDetail.setTdTdsPaid(pftDetail.getTdTdsPaid().add(curSchd.getTDSPaid()));
			}
		}

		pftDetail.setCurReducingRate(curSchd.getCalculatedRate());
		pftDetail.setCurCpzBalance(curSchd.getCpzBalance());

		pftDetail.setBaseRateCode(curSchd.getBaseRate());
		pftDetail.setSplRateCode(curSchd.getSplRate());
		pftDetail.setMrgRate(curSchd.getMrgRate());

		logger.debug("Leaving");
	}

	private static void calNextDateTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// advance Profit and Principal
		pftDetail.setTotalPftPaidInAdv(pftDetail.getTotalPftPaidInAdv().add(curSchd.getSchdPftPaid()));
		pftDetail.setTotalPriPaidInAdv(pftDetail.getTotalPriPaidInAdv().add(curSchd.getSchdPriPaid()));

		//NEXT Schedule Details
		if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate())) {
			if ((curSchd.isFrqDate() && !isHoliday(curSchd.getBpiOrHoliday()))
					|| curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0) {
				if (pftDetail.getNSchdDate().compareTo(pftDetail.getMaturityDate()) == 0) {
					pftDetail.setNSchdDate(curSchd.getSchDate());
					pftDetail.setNSchdPri(curSchd.getPrincipalSchd());
					pftDetail.setNSchdPft(curSchd.getProfitSchd());
					pftDetail.setNSchdPriDue(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
					pftDetail.setNSchdPftDue(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
				}
				pftDetail.setFutureInst(pftDetail.getFutureInst() + 1);
			}
		}

		if (curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0
				&& pftDetail.getNSchdDate().compareTo(pftDetail.getMaturityDate()) == 0) {
			pftDetail.setNSchdPri(curSchd.getPrincipalSchd());
			pftDetail.setNSchdPft(curSchd.getProfitSchd());
			pftDetail.setNSchdPriDue(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
			pftDetail.setNSchdPftDue(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		}

		logger.debug("Leaving");
	}

	private static boolean isHoliday(String bpiOrHoliday) {
		if (StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_HOLIDAY)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_POSTPONE)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_UNPLANNED)) {
			return true;
		} else {
			return false;
		}
	}

	private void calculateTotals(FinanceMain finMain, FinanceProfitDetail pftDetail, Date dateSusp, Date valueDate) {
		logger.debug("Entering");

		pftDetail.setTotalPftBal(pftDetail.getTotalPftSchd().subtract(pftDetail.getTotalPftPaid()));
		pftDetail.setTotalPriBal(pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPriPaid()));
		pftDetail.setTdSchdPftBal(pftDetail.getTdSchdPft().subtract(pftDetail.getTdSchdPftPaid()));
		pftDetail.setTdSchdPriBal(pftDetail.getTdSchdPri().subtract(pftDetail.getTdSchdPriPaid()));

		// Current Flat Rate
		BigDecimal calPart1 = pftDetail.getTotalPftSchd().add(pftDetail.getTotalPftCpz());
		BigDecimal calPart2 = pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPftCpz());
		BigDecimal daysFactor = CalculationUtil.getInterestDays(pftDetail.getFinStartDate(),
				pftDetail.getMaturityDate(), finMain.getProfitDaysBasis());
		if (calPart2.compareTo(BigDecimal.ZERO) > 0) {
			pftDetail.setCurFlatRate(calPart1.divide((calPart2.multiply(new BigDecimal(100)).multiply(daysFactor)), 9,
					RoundingMode.HALF_DOWN));
		} else {
			pftDetail.setCurFlatRate(BigDecimal.ZERO);
		}

		//Calculated at individual level
		//pftDetail.setPftAccrued(pftDetail.getPftAmz().subtract(pftDetail.getTotalPftPaid()));

		// Suspense Amortization
		if (dateSusp.compareTo(pftDetail.getMaturityDate()) <= 0) {
			pftDetail.setPftAmzSusp(
					pftDetail.getPftAmz().subtract(pftDetail.getPftAmzNormal()).subtract(pftDetail.getPftAmzPD()));
			pftDetail.setPftInSusp(true);
			//Value Equivalent accrual after suspended date
			pftDetail.setPftAccrueSusp(pftDetail.getPftAccrued().subtract(pftDetail.getPftAccrueSusp()));
		} else {
			pftDetail.setPftInSusp(false);
			pftDetail.setPftAccrueSusp(BigDecimal.ZERO);
		}

		//FIXME: Delete if late pay marking is handling this. New method required to recal OD's after repayment
		// OD Details
		/*
		 * if (!StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) { FinODDetails
		 * finODDetails = getFinODDetailsDAO().getFinODSummary(pftDetail.getFinReference()); if (finODDetails != null) {
		 * pftDetail.setODPrincipal(finODDetails.getFinCurODPri());
		 * pftDetail.setODProfit(finODDetails.getFinCurODPft());
		 * pftDetail.setPenaltyPaid(finODDetails.getTotPenaltyPaid());
		 * pftDetail.setPenaltyDue(finODDetails.getTotPenaltyBal());
		 * pftDetail.setPenaltyWaived(finODDetails.getTotWaived());
		 * pftDetail.setFirstODDate(finODDetails.getFinODSchdDate());
		 * pftDetail.setPrvODDate(finODDetails.getFinODTillDate()); pftDetail.setCurODDays(getNoDays(valueDate,
		 * finODDetails.getFinODTillDate()));
		 * 
		 * //Workaround solution to avoid another fields in the FinODDetails
		 * pftDetail.setMaxODDays(finODDetails.getFinCurODDays()); } }
		 */
		int tenor = DateUtility.getMonthsBetween(valueDate, pftDetail.getMaturityDate(), true);
		pftDetail.setRemainingTenor(tenor);

		tenor = DateUtility.getMonthsBetween(pftDetail.getFinStartDate(), pftDetail.getMaturityDate(), true);
		pftDetail.setTotalTenor(tenor);

		/*
		 * //Special Rates pftDetail.setSplRate(BigDecimal.ZERO); try { if
		 * (!StringUtils.isBlank(pftDetail.getSplRateCode())) { pftDetail.setSplRate(new
		 * BigDecimal(pftDetail.getSplRateCode())); } } catch (Exception e) { // TODO: handle exception }
		 * 
		 * //Base Rate if (!StringUtils.isBlank(pftDetail.getBaseRateCode())) { pftDetail.setBaseRate(
		 * pftDetail.getCurReducingRate().subtract(pftDetail.getSplRate()).subtract(pftDetail.getMrgRate())); }
		 */

		// TDS Calculation
		if (tdsSchdIdx >= 0) {
			BigDecimal accrueDue = pftDetail.getPftAccrued().subtract(pftDetail.getTdSchdPftBal());
			BigDecimal accrueTDS = accrueDue.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
			accrueTDS = CalculationUtil.roundAmount(accrueTDS, tdsRoundMode, tdsRoundingTarget);
			pftDetail.setTdsAccrued(pftDetail.getTdTdsBal().add(accrueTDS));
		}

		logger.debug("Leaving");
	}

	/**
	 * @param financeMain
	 * @param resultSet
	 * @throws Exception
	 */
	public void postAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");

		String eventCode = AccountEventConstants.ACCEVENT_AMZ;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		if (finPftDetail.isPftInSusp()) {
			eventCode = AccountEventConstants.ACCEVENT_AMZSUSP;
		}

		long accountingID = getAccountingID(main, eventCode);
		if (accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(finPftDetail, finEODEvent.getFinanceScheduleDetails(), eventCode,
				custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		// Y - Accrual Effective Date will be Value Date, N - Accrual Effective Date will be APP Date
		String acc_eff_valDate = SysParamUtil.getValueAsString(SMTParameterConstants.ACC_EFF_VALDATE);

		if (StringUtils.equals(acc_eff_valDate, "N")) {
			aeEvent.setValueDate(DateUtility.getPostDate());
		}

		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());

		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		finEODEvent.setUpdLBDPostings(true);

		//posting done update the accrual balance
		finPftDetail.setAmzTillLBD(finPftDetail.getPftAmz());
		finPftDetail.setAmzTillLBDNormal(finPftDetail.getPftAmzNormal());
		finPftDetail.setAmzTillLBDPD(finPftDetail.getPftAmzPD());
		finPftDetail.setAmzTillLBDPIS(finPftDetail.getPftAmzSusp());
		finPftDetail.setAcrTillLBD(finPftDetail.getPftAccrued());
		finPftDetail.setAcrSuspTillLBD(finPftDetail.getPftAccrueSusp());
		finPftDetail.setSvnAcrTillLBD(finPftDetail.getSvnPftAmount());

		//Month End move all the balances to previous month also
		if (DateUtility.getDay(custEODEvent.getEodValueDate()) == 1) {
			finPftDetail.setPrvMthAcr(finPftDetail.getPftAccrued());
			finPftDetail.setPrvMthAcrSusp(finPftDetail.getPftAccrueSusp());
			finPftDetail.setPrvMthAmz(finPftDetail.getPftAmz());
			finPftDetail.setPrvMthAmzNrm(finPftDetail.getPftAmzNormal());
			finPftDetail.setPrvMthAmzPD(finPftDetail.getPftAmzPD());
			finPftDetail.setPrvMthAmzSusp(finPftDetail.getPftAmzSusp());
			finEODEvent.setUpdMonthEndPostings(true);
		}
		// these fields should be update after the accrual posting only so these will not be considered in normal update.
		logger.debug(" Leaving ");
	}

	private static int getNoDays(Date date1, Date date2) {
		return DateUtility.getDaysBetween(date1, date2);
	}

	/**
	 * Method for returning AccrualMonthEnd list from current MonthEnd to MaturityDate
	 * 
	 * @param finMain
	 * @param schdDetails
	 * @param appDate
	 * @return
	 */
	public static List<ProjectedAccrual> getAccrualsFromCurMonthEnd(FinanceMain finMain,
			List<FinanceScheduleDetail> schdDetails, Date appDate, String fromFinStartDate) {
		logger.debug(" Entering ");

		List<ProjectedAccrual> list = new ArrayList<ProjectedAccrual>();

		// Calculate Month End Amortization From FinStartDate to Maturity
		List<ProjectedAccrual> accrualList = calAccrualsOnMonthEnd(finMain, schdDetails, appDate, BigDecimal.ZERO,
				true);

		// ALM Extraction configuration from SMT parameter
		if ("Y".equals(fromFinStartDate)) {

			list = accrualList;
		} else {
			for (ProjectedAccrual projectedAccrual : accrualList) {
				if (projectedAccrual.getAccruedOn().compareTo(appDate) >= 0) {
					list.add(projectedAccrual);
				}
			}
		}

		logger.debug(" Leaving ");
		return list;
	}

	/**
	 * Month End Amortization Calculation
	 * 
	 * @param finMain
	 * @param schdDetails
	 * @param appDate
	 * @return
	 */
	public static List<ProjectedAccrual> calAccrualsOnMonthEnd(FinanceMain finMain,
			List<FinanceScheduleDetail> schdDetails, Date appDate, BigDecimal prvMthAmz, boolean calFromFinStartDate) {
		logger.debug(" Entering ");

		HashMap<Date, ProjectedAccrual> map = new HashMap<Date, ProjectedAccrual>(1);
		List<ProjectedAccrual> list = new ArrayList<ProjectedAccrual>();
		Date appDateMonthStart = DateUtility.getMonthStart(appDate);
		Date appDateMonthEnd = DateUtility.getMonthEnd(appDate);
		BigDecimal totalProfit = BigDecimal.ZERO;
		BigDecimal cummAccAmt = BigDecimal.ZERO;

		if (getFormatDate(finMain.getMaturityDate()).compareTo(appDateMonthStart) < 0) {
			return list;
		}

		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;
		Date prvMonthEnd = null;

		List<Date> months = new ArrayList<Date>();
		List<Date> monthsCopy = new ArrayList<Date>();
		Date newMonth = null;

		if (calFromFinStartDate
				|| DateUtility.getMonthEnd(getFormatDate(finMain.getFinStartDate())).compareTo(appDateMonthEnd) == 0) {

			newMonth = new Date(DateUtility.getMonthEnd(finMain.getFinStartDate()).getTime());
		} else {

			newMonth = new Date(appDateMonthEnd.getTime());
			prvMonthEnd = DateUtility.addDays(appDateMonthStart, -1);
			cummAccAmt = prvMthAmz;

			ProjectedAccrual prjAcc = new ProjectedAccrual();
			prjAcc.setFinReference(finMain.getFinReference());
			prjAcc.setAccruedOn(prvMonthEnd);
			prjAcc.setCumulativeAccrued(prvMthAmz);
			map.put(prvMonthEnd, prjAcc);
		}

		// Prepare Months list From FinStartDate to MaturityDate
		while (DateUtility.getMonthEnd(finMain.getMaturityDate()).compareTo(newMonth) >= 0) {
			months.add(getFormatDate((Date) newMonth.clone()));
			newMonth = DateUtility.addMonths(newMonth, 1);
			newMonth = DateUtility.getMonthEnd(newMonth);
		}
		monthsCopy.addAll(months);

		for (int i = 0; i < schdDetails.size(); i++) {

			curSchd = schdDetails.get(i);
			curSchdDate = curSchd.getSchDate();
			totalProfit = totalProfit.add(curSchd.getProfitCalc());

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schdDetails.get(i - 1);
			}
			if (i == schdDetails.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schdDetails.get(i + 1);
			}

			prvSchdDate = prvSchd.getSchDate();
			nextSchdDate = nextSchd.getSchDate();

			if (!calFromFinStartDate && curSchdDate.compareTo(appDateMonthStart) < 0) {
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
					prjAcc.setFinReference(finMain.getFinReference());
					prjAcc.setAccruedOn(curMonthEnd);
				}

				// ACCRUAL calculation includes current date
				Date nextMonthStart = DateUtility.addDays(curMonthEnd, 1);
				Date curMonthStart = DateUtility.getMonthStart(curMonthEnd);

				// Schedules between Previous MonthEnd to CurMonthEnd
				if (prvSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0) {

					schdPftAmz = curSchd.getProfitCalc();
					isSchdPftAmz = true;
				}

				if (curMonthEnd.compareTo(curSchdDate) < 0) {

					// Months Between schedules
					curPftAmz = calProfitAmz(curSchd, prvSchd, nextMonthStart, curMonthStart, finMain);
				} else {

					// Profit Calculation From MonthEnd to CurSchdDate 
					if (curMonthEnd.compareTo(prvSchdDate) >= 0 && curMonthEnd.compareTo(nextSchdDate) < 0) {
						curPftAmz = calProfitAmz(nextSchd, curSchd, nextMonthStart, curSchdDate, finMain);
					}

					// Profit Calculation From CurSchdDate to Previous MonthEnd
					if (prvMonthEnd != null && !isSchdPftAmz) {
						prvPftAmz = calProfitAmz(curSchd, prvSchd, curSchdDate, curMonthStart, finMain);
					}
				}

				pftAmz = schdPftAmz.add(prvPftAmz).add(curPftAmz);

				// Adjust remaining profit to maturity MonthEnd to avoid rounding issues
				if (DateUtility.getMonthEnd(getFormatDate(finMain.getMaturityDate())).compareTo(curMonthEnd) == 0) {
					prjAcc.setPftAmz(totalProfit.subtract(cummAccAmt));
				} else {
					prjAcc.setPftAmz(prjAcc.getPftAmz().add(pftAmz));
				}

				if (curSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0) {
					if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
						prjAcc.setSchdDate(curSchdDate);
						prjAcc.setSchdPri(curSchd.getPrincipalSchd().subtract(curSchd.getCpzAmount()));
						prjAcc.setSchdPft(curSchd.getProfitSchd().add(curSchd.getCpzAmount()));
						prjAcc.setSchdTot(curSchd.getRepayAmount());
					}
				}

				// Current and Next Schedules are equal in Maturity
				if (curMonthEnd.compareTo(nextSchdDate) < 0 || curSchdDate.compareTo(nextSchdDate) == 0) {

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

				if (curMonthEnd.compareTo(curSchdDate) >= 0) {
					break;
				}
			}
		}

		logger.debug(" Leaving ");
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
			Date date2, FinanceMain finMain) {

		BigDecimal pftAmz = BigDecimal.ZERO;

		int days = DateUtility.getDaysBetween(date1, date2);
		int daysInCurPeriod = DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchd.getSchDate());

		BigDecimal amzForCal = curSchd.getProfitCalc()
				.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
		pftAmz = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
				RoundingMode.HALF_DOWN);
		pftAmz = pftAmz.add(prvSchd.getProfitFraction());
		pftAmz = CalculationUtil.roundAmount(pftAmz, finMain.getCalRoundingMode(), finMain.getRoundingTarget());
		return pftAmz;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	private static Date getFormatDate(Date date) {
		return DateUtility.getDBDate(DateUtility.format(date, PennantConstants.DBDateFormat));
	}

	public void setSMTParms() {
		tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		tdsMultiplier = big100.divide(big100.subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);

		tdsSchdIdx = -2;
	}

	public void setSuspHeadDAO(FinanceSuspHeadDAO suspHeadDAO) {
		this.suspHeadDAO = suspHeadDAO;
	}

}
