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
 *
 * FileName    		:  AEAmounts.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;

public class AEAmounts implements Serializable {

    private static final long serialVersionUID = 4594615740716296558L;
	private Logger logger = Logger.getLogger(AEAmounts.class);

	private static AEAmountCodes aeAmountCodes;
	private BigDecimal zeroValue = BigDecimal.ZERO;
	private Date datePrvRepay = new Date();
	private Date dateCurRepay = new Date();

	private Date datePrvSchd = new Date();
	private Date dateCurSchd = new Date();
	private Date dateRefundCheck = new Date();
	private Date dateAccrueValue = new Date();
	private Date dateODFrom = new Date();
	private Date dateLastFullyPaid = new Date();
	private Date dateRepay = new Date();

	private boolean isOverDue = false;
	private boolean isFutureDeal = false;
	private boolean isEOD = false;

	// Profit Accrued till current Schedule End Date
	private BigDecimal accrueTillDate = BigDecimal.ZERO;
	private BigDecimal amzTillDate = BigDecimal.ZERO;
	private BigDecimal amzTillRepay = BigDecimal.ZERO;

	// -------------------------------------------------------------------------------------------------
	// Processing Schedule Details to fill AmountCode Details DATA
	// -------------------------------------------------------------------------------------------------
	public AEAmountCodes procAEAmounts(FinanceMain financeMain,
	        List<FinanceScheduleDetail> schdDetails, FinanceProfitDetail pftDetail, Date valueDate) {

		logger.debug("Entering");

		accrueTillDate = zeroValue;
		amzTillDate = zeroValue;
		amzTillRepay = zeroValue;

		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(
		        "PHASE").toString());
		if (!phase.equals("DAY")) {
			isEOD = true;
		}

		isOverDue = false;
		dateODFrom = financeMain.getFinStartDate();
		dateLastFullyPaid = dateODFrom;

		if (valueDate.before(financeMain.getFinStartDate())) {
			isFutureDeal = true;
		}

		if (financeMain.isAllowGrcPeriod()
		        && financeMain.getGrcPeriodEndDate().compareTo(valueDate) >= 0) {
			dateRepay = financeMain.getNextGrcPftDate();
		} else {
			dateRepay = financeMain.getNextRepayDate();
		}

		// Reset Finance Profit Details
		aeAmountCodes = new AEAmountCodes();
		aeAmountCodes.setFinReference(financeMain.getFinReference());
		aeAmountCodes.setLastRepayPftDate(financeMain.getLastRepayPftDate());
		aeAmountCodes.setNextRepayPftDate(financeMain.getNextRepayPftDate());
		aeAmountCodes.setLastRepayRvwDate(financeMain.getLastRepayRvwDate());
		aeAmountCodes.setNextRepayRvwDate(financeMain.getNextRepayRvwDate());
		aeAmountCodes.setODInst(0);
		aeAmountCodes.setPftInAdv(zeroValue);

		// FIND Schedule and Repayment Dates
		int sdSize = schdDetails.size();
		boolean isCurSchdSet = false;
		boolean isCurRepaySet = false;

		datePrvRepay = financeMain.getFinStartDate();
		datePrvSchd = datePrvRepay;
		dateRefundCheck = datePrvRepay;

		for (int i = 0; i < sdSize; i++) {
			// Set Repayment Dates
			if (schdDetails.get(i).isRepayOnSchDate()) {
				if (schdDetails.get(i).getSchDate().compareTo(valueDate) < 0) {
					datePrvRepay = schdDetails.get(i).getSchDate();
				} else {
					if (!isCurRepaySet) {
						dateCurRepay = schdDetails.get(i).getSchDate();
						isCurRepaySet = true;
					}
				}
			}

			// Set Schedule Dates
			if (schdDetails.get(i).getSchDate().compareTo(valueDate) < 0) {
				datePrvSchd = schdDetails.get(i).getSchDate();
			} else {
				if (!isCurSchdSet) {
					dateCurSchd = schdDetails.get(i).getSchDate();
					isCurSchdSet = true;
				}
			}

			if (isCurRepaySet && isCurSchdSet) {
				break;
			}
		}

		// Finance Asset Value
		aeAmountCodes.setAstValO(financeMain.getFinAssetValue());

		// Finance Current Asset Value
		aeAmountCodes.setAstValC(financeMain.getFinCurrAssetValue());

		// Days Calculation
		aeAmountCodes.setCPNoOfDays(DateUtility.getDaysBetween(dateCurRepay, datePrvRepay));

		if (isFutureDeal) {
			aeAmountCodes.setCpDaysTill(0);
		} else {
			aeAmountCodes.setCpDaysTill(DateUtility.getDaysBetween(valueDate, datePrvRepay));
		}

		// NORMAL Day or EOD/SOD
		if (isEOD) {
			dateAccrueValue = DateUtility.addDays(valueDate, 1);
		} else {
			dateAccrueValue = valueDate;
		}

		aeAmountCodes.setDaysDiff(DateUtility.getDaysBetween(valueDate, dateCurRepay));

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		FinanceScheduleDetail prvSchd = new FinanceScheduleDetail();

		// Calculate Amounts
		for (int i = 0; i < sdSize; i++) {
			curSchd = schdDetails.get(i);
			if (i != 0) {
				prvSchd = schdDetails.get(i - 1);
			}

			// Method calling for calucation of AmountCodes for all Schedules
			aeAmountCodes = calAEAmounts(curSchd, prvSchd, i, valueDate);
		}

		// Capitalize from next schedule date
		aeAmountCodes.setCpzNxt(aeAmountCodes.getCpzTot().subtract(aeAmountCodes.getCpzPrv())
		        .subtract(aeAmountCodes.getCpzCur()));

		// Actual Profit Balance till maturity
		aeAmountCodes.setPftAB(aeAmountCodes.getPft().subtract(aeAmountCodes.getPftAP()));

		// Scheduled Profit Balance till now
		aeAmountCodes.setPftSB(aeAmountCodes.getPftS().subtract(aeAmountCodes.getPftSP()));

		// Actual Principal payment balance till now
		aeAmountCodes.setPriAB(aeAmountCodes.getPri().subtract(aeAmountCodes.getPriAP()));

		// Scheduled Principal balance till now
		aeAmountCodes.setPriSB(aeAmountCodes.getPriS().subtract(aeAmountCodes.getPriSP()));

		// ----------------------------------------------------
		// ACCRUALS SETTING
		// ----------------------------------------------------
		// Accrued Profit Till Now
		aeAmountCodes.setAccrue(accrueTillDate.subtract(aeAmountCodes.getPftInAdv()));

		// Accrued Profit Suspended Till now
		aeAmountCodes.setAccrueS(pftDetail.getTdPftAccrueSusp());

		// Accrued Till Last posting
		aeAmountCodes.setlAccrue(pftDetail.getAcrTillLBD());

		// Accrue calculated From Last Posted to Next business date
		aeAmountCodes.setDAccrue(accrueTillDate.subtract(aeAmountCodes.getlAccrue()));

		// Accrued Till NextBusiness Date
		aeAmountCodes.setNAccrue(accrueTillDate.subtract(aeAmountCodes.getPftInAdv()));

		// ----------------------------------------------------
		// AMORTIZATION SETTING
		// ----------------------------------------------------
		// Amortized Profit Till Now
		aeAmountCodes.setAmz(amzTillDate.add(aeAmountCodes.getPftInAdv()));

		// Amortized Profit Suspended Till now
		aeAmountCodes.setAmzS(pftDetail.getTdPftAmortizedSusp());

		// Amortized Till Last posting
		aeAmountCodes.setlAmz(pftDetail.getAmzTillLBD());

		// Amortized calculated From Last Posted to Next business date
		aeAmountCodes.setdAmz(amzTillDate.subtract(aeAmountCodes.getlAmz()));

		// Amortized Till NextBusiness Date
		aeAmountCodes.setnAmz(amzTillDate.add(aeAmountCodes.getPftInAdv()));

		// Overdue Days
		if (isOverDue) {
			aeAmountCodes.setODDays(DateUtility.getDaysBetween(dateODFrom, valueDate));
		} else {
			aeAmountCodes.setODDays(0);
		}

		// Last Fully Paid Days
		aeAmountCodes.setDaysFromFullyPaid(DateUtility.getDaysBetween(dateLastFullyPaid, valueDate));

		logger.debug("Leaving");
		return aeAmountCodes;

	}

	/*
	 * Start Calculating the Amounts for accounting engine
	 */
	private AEAmountCodes calAEAmounts(FinanceScheduleDetail curSchd,
	        FinanceScheduleDetail prvSchd, int curSchdRecord, Date valueDate) {

		logger.debug("Entering");
		Date schdDate = curSchd.getSchDate();

		// -------------------------------------------------------------------------------------------------
		// Set Disbursement and down payment amounts
		// -------------------------------------------------------------------------------------------------
		if (schdDate.compareTo(valueDate) == 0) {
			aeAmountCodes.setDisburse(curSchd.getDisbAmount());
			aeAmountCodes.setDownpay(curSchd.getDownPaymentAmount());
		}

		if (curSchdRecord == 0) {
			return aeAmountCodes;
		}

		// Set Date for refund checking
		if (curSchd.isCpzOnSchDate() && schdDate.compareTo(dateRefundCheck) > 0
		        && schdDate.compareTo(dateCurRepay) < 0) {
			dateRefundCheck = schdDate;
		}

		// -------------------------------------------------------------------------------------------------
		// TOTALS FOR ALL PERIODS
		// -------------------------------------------------------------------------------------------------

		if (curSchd.isRepayOnSchDate()) {

			// Last Fully Paid Date
			if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) == 0
			        && curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0
			        && curSchd.getDefPrincipalSchd().compareTo(curSchd.getDefSchdPriPaid()) == 0
			        && curSchd.getDefProfitSchd().compareTo(curSchd.getDefSchdPftPaid()) == 0) {
				dateLastFullyPaid = curSchd.getSchDate();
			}

			//Over Due Calculations
			if (curSchd.getSchDate().before(valueDate) && isOverDue) {
				aeAmountCodes.setODInst(aeAmountCodes.getODInst() + 1);
			}

			if (curSchd.getSchDate().before(valueDate) && !isOverDue) {
				if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) != 0
				        || curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) != 0
				        || curSchd.getDefPrincipalSchd().compareTo(curSchd.getDefSchdPriPaid()) != 0
				        || curSchd.getDefProfitSchd().compareTo(curSchd.getDefSchdPftPaid()) != 0) {
					dateODFrom = curSchd.getSchDate();
					aeAmountCodes.setODInst(1);
					isOverDue = true;
				}
			}
		}

		// Total Scheduled Profit and Paid
		aeAmountCodes.setPft(aeAmountCodes.getPft().add(curSchd.getProfitSchd())
		        .add(curSchd.getDefProfitSchd()));
		aeAmountCodes.setPftAP(aeAmountCodes.getPftAP().add(curSchd.getSchdPftPaid())
		        .add(curSchd.getDefSchdPftPaid()));

		// Total principal and Paid
		aeAmountCodes.setPri(aeAmountCodes.getPri().add(curSchd.getPrincipalSchd())
		        .add(curSchd.getDefPrincipalSchd()));
		aeAmountCodes.setPriAP(aeAmountCodes.getPriAP().add(curSchd.getSchdPriPaid())
		        .add(curSchd.getDefSchdPriPaid()));

		// Total Capitalize
		aeAmountCodes.setCpzTot(aeAmountCodes.getCpzTot().add(curSchd.getCpzAmount()));

		// -------------------------------------------------------------------------------------------------
		// Amounts Calculation Till Previous Scheduled Date
		// -------------------------------------------------------------------------------------------------
		if (isEOD) {
			if (schdDate.compareTo(valueDate) == 0) {
				aeAmountCodes.setCpzPrv(aeAmountCodes.getCpzPrv().add(curSchd.getCpzAmount()));
			}
		}

		//TODO : Check whether this part will go to else condition
		if (schdDate.compareTo(datePrvSchd) <= 0) {
			aeAmountCodes.setCpzPrv(aeAmountCodes.getCpzPrv().add(curSchd.getCpzAmount()));
		}

		// Accrued and Amortized till amortization value date PAST
		if (schdDate.compareTo(dateAccrueValue) < 0) {
			// Accrue till previous scheduled date
			accrueTillDate = accrueTillDate.add(curSchd.getProfitCalc())
			        .subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDefSchdPftPaid())
			        .subtract(curSchd.getCpzAmount());

			// Amortization till previous scheduled date
			amzTillDate = amzTillDate.add(curSchd.getProfitCalc());
		}

		// VALUE DATE IS LAST DATE OF PREVIOUS SCHDULE RECORD
		if (prvSchd.getSchDate().compareTo(valueDate) == 0) {
			aeAmountCodes = calPartAccrual(curSchd, prvSchd.getSchDate());
		}

		if (schdDate.compareTo(dateAccrueValue) >= 0 && schdDate.compareTo(dateCurSchd) <= 0) {
			aeAmountCodes = calPartAccrual(curSchd, datePrvSchd);
		}

		// Payments till current repayment date
		if (schdDate.compareTo(dateCurRepay) <= 0) {

			// Schedule Profit and Paid
			aeAmountCodes.setPftS(aeAmountCodes.getPftS().add(curSchd.getProfitSchd())
			        .add(curSchd.getDefProfitSchd()));
			aeAmountCodes.setPftSP(aeAmountCodes.getPftSP().add(curSchd.getSchdPftPaid())
			        .add(curSchd.getDefSchdPftPaid()));

			// Schedule principal and Paid
			aeAmountCodes.setPriS(aeAmountCodes.getPriS().add(curSchd.getPrincipalSchd())
			        .add(curSchd.getDefPrincipalSchd()));
			aeAmountCodes.setPriSP(aeAmountCodes.getPriSP().add(curSchd.getSchdPriPaid())
			        .add(curSchd.getDefSchdPriPaid()));
		}

		// TODO to be verified the below code
		// Schedule Date same as value date
		if (schdDate.compareTo(datePrvSchd) > 0 && schdDate.compareTo(dateCurSchd) <= 0) {
			aeAmountCodes.setCpzCur(aeAmountCodes.getCpzCur().add(curSchd.getCpzAmount()));
		}

		// Amortization till current repayment date
		if (schdDate.compareTo(dateCurRepay) <= 0) {
			amzTillRepay = amzTillRepay.add(curSchd.getProfitCalc());
		}

		//Profit Paid in advance
		if (schdDate.compareTo(dateRepay) > 0) {
			aeAmountCodes.setPftInAdv(aeAmountCodes.getPftInAdv().add(curSchd.getSchdPftPaid())
			        .add(curSchd.getDefSchdPftPaid()));
		}
		logger.debug("Leaving");
		return aeAmountCodes;
	}

	private AEAmountCodes calPartAccrual(FinanceScheduleDetail curSchd, Date dateFrom) {

		logger.debug("Entering");
		int totalDays = 0;
		int daysIn = 0;
		BigDecimal pftCalAsOfNow = zeroValue;

		// Calculate Accruals if not in future
		if (!isFutureDeal) {
			// Total Days in current schedule record
			totalDays = DateUtility.getDaysBetween(dateFrom, curSchd.getSchDate());
			// Days in current schedule record
			daysIn = DateUtility.getDaysBetween(dateFrom, dateAccrueValue);

			if (totalDays > 0 && daysIn < totalDays) {
				pftCalAsOfNow = curSchd.getProfitCalc().divide(BigDecimal.valueOf(totalDays), 0,
				        RoundingMode.HALF_DOWN);
				pftCalAsOfNow = pftCalAsOfNow.setScale(0, RoundingMode.HALF_DOWN);
				pftCalAsOfNow = pftCalAsOfNow.multiply(new BigDecimal(daysIn));
			} else {
				pftCalAsOfNow = curSchd.getProfitCalc();
			}
		}

		// Accure till Value date
		if (curSchd.getSchDate().equals(dateAccrueValue)) {
			accrueTillDate = accrueTillDate.add(pftCalAsOfNow).subtract(curSchd.getSchdPftPaid())
			        .subtract(curSchd.getDefSchdPftPaid());
		} else {
			accrueTillDate = accrueTillDate.add(pftCalAsOfNow).subtract(curSchd.getSchdPftPaid())
			        .subtract(curSchd.getDefSchdPftPaid());
		}

		// Amortization till Value Date
		BigDecimal paidPftAmount = curSchd.getSchdPftPaid().add(curSchd.getDefSchdPftPaid());

		if (paidPftAmount.compareTo(pftCalAsOfNow) >= 0) {
			amzTillDate = amzTillDate.add(paidPftAmount);
		} else {
			amzTillDate = amzTillDate.add(pftCalAsOfNow);
		}

		logger.debug("Leaving");
		return aeAmountCodes;
	}

	/**
	 * Method for Preparation of DataSet object
	 * 
	 * @param financeMain
	 * @param eventCode
	 * @return
	 */
	public DataSet createDataSet(FinanceMain financeMain, String eventCode, Date dateValueDate,
	        Date dateSchdDate) {

		logger.debug("Entering");
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(financeMain.getFinReference());

		if (eventCode.equals("")) {
			if (financeMain.getFinStartDate().after(
			        (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"))) {
				dataSet.setFinEvent("ADDDBSF");
			} else {
				dataSet.setFinEvent("ADDDBSP");
			}
		} else {
			dataSet.setFinEvent(eventCode);
		}

		dataSet.setFinBranch(financeMain.getFinBranch());
		dataSet.setFinCcy(financeMain.getFinCcy());
		dataSet.setPostDate((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"));
		dataSet.setValueDate(dateValueDate);
		dataSet.setSchdDate(dateSchdDate);
		dataSet.setFinType(financeMain.getFinType());
		dataSet.setCustId(financeMain.getCustID());
		dataSet.setDisburseAccount(financeMain.getDisbAccountId());
		dataSet.setRepayAccount(financeMain.getRepayAccountId());
		dataSet.setFinAccount(financeMain.getFinAccount());
		dataSet.setFinCustPftAccount(financeMain.getFinCustPftAccount());
		dataSet.setDisburseAmount(financeMain.getFinAmount());
		
		//FIXME -- for disbursements which amt is currect
		dataSet.setFinAmount(financeMain.getCurDisbursementAmt()== null? financeMain.getFinAmount().subtract(
		        financeMain.getFinRepaymentAmount()) : financeMain.getCurDisbursementAmt());
		
		if (financeMain.isNewRecord()
		        || financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			dataSet.setNewRecord(true);
		} else {
			dataSet.setNewRecord(false);
		}

		dataSet.setDownPayment(financeMain.getDownPayment() == null ? BigDecimal.ZERO
		        : financeMain.getDownPayment());
		dataSet.setNoOfTerms(financeMain.getNumberOfTerms());
		logger.debug("Leaving");
		return dataSet;
	}

}
