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
 * 
 * FileName : AEAmounts.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
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

import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;

public class AEAmounts implements Serializable {

	private static final long serialVersionUID = 4594615740716296558L;
	private static Logger logger = Logger.getLogger(AEAmounts.class);

	private AEAmountCodes aeAmountCodes;
	private DataSet dataSet;

	private BigDecimal zeroValue = BigDecimal.ZERO;
	private Date datePrvRepay = new Date();
	private Date dateCurRepay = new Date();

	private Date datePrvSchd = new Date();
	private Date dateCurSchd = new Date();
	private Date datePrvSchdSusp = new Date();
	private Date dateCurSchdSusp = new Date();

	private Date dateRefundCheck = new Date();
	private Date dateAccrueValue = new Date();
	private Date dateODFrom = new Date();
	private Date dateCRBODFrom = new Date();
	private Date dateLastFullyPaid = new Date();
	private Date dateSuspPftTfr = new Date();

	private Date datePrvRecord = new Date();
	private Date dateCurRecord = new Date();

	private boolean isOverDue = false;
	private boolean isCRBOverDue = false;
	private boolean isFutureDeal = false;
	private boolean isEOD = false;
	private boolean isSusp = false;
	private boolean isCurSchdSet = false;
	private boolean isCurRepaySet = false;
	private boolean isCurSchdSuspSet = false;
	private boolean isNextSchdSet = false;

	// Profit Accrued till current Schedule End Date
	private BigDecimal accrueTillDate = BigDecimal.ZERO;
	private BigDecimal accumulatedPriTillDate = BigDecimal.ZERO;
	private BigDecimal depreciatePri = BigDecimal.ZERO; //Yearly Depreciation Principal
	//private BigDecimal accrueTillTsfdDate = BigDecimal.ZERO;
	private BigDecimal amzTillDate = BigDecimal.ZERO;
	private BigDecimal amzTillRepay = BigDecimal.ZERO;
	private BigDecimal accrueTillSusp = BigDecimal.ZERO;
	
	private static FinanceSuspHeadDAO suspHeadDAO;
	private static FinODDetailsDAO finODDetailsDAO;
	private static CustomerDAO customerDAO;

	private int crbGraceODDays = 0;

	public AEAmounts() {
		super();
	}

	// -------------------------------------------------------------------------------------------------
	// Processing Schedule Details to fill AmountCode Details DATA
	// -------------------------------------------------------------------------------------------------

	public static AEAmountCodes procAEAmounts(FinanceMain financeMain,
	        List<FinanceScheduleDetail> schdDetails, FinanceProfitDetail pftDetail, Date valueDate) {
		return new AEAmounts(financeMain, schdDetails, pftDetail, valueDate, false).getAeAmountCodes();
	}
	
	public static AEAmountCodes procAccrualAmounts(FinanceMain financeMain,
			List<FinanceScheduleDetail> schdDetails, FinanceProfitDetail pftDetail, Date valueDate) {
		return new AEAmounts(financeMain, schdDetails, pftDetail, valueDate, true).getAeAmountCodes();
	}

	private AEAmounts(FinanceMain financeMain, List<FinanceScheduleDetail> schdDetails,
	        FinanceProfitDetail pftDetail, Date valueDate, boolean isAccrualProcess) {
		logger.debug("Entering");

		accrueTillDate = zeroValue;
		accumulatedPriTillDate = zeroValue;
		depreciatePri = zeroValue;
		amzTillDate = zeroValue;
		amzTillRepay = zeroValue;
		accrueTillSusp = zeroValue;

		//Fetch Customer Object Details
		Customer customer = getCustomerDAO().getCustomerByID(financeMain.getCustID());
		
		isSusp = false;
		aeAmountCodes = new AEAmountCodes();
		if (customer != null && customer.getCustStsChgDate() != null) {
			dateSuspPftTfr = customer.getCustStsChgDate();
			aeAmountCodes.setPftInSusp(true);
			isSusp = true;

			//TODO Calculate datePftTranfered
			dateSuspPftTfr = DateUtility.getMonthStartDate(dateSuspPftTfr);
		}
		
		String parmCode = null;
		if (customer != null && PennantConstants.PFF_CUSTCTG_INDIV.equals(customer.getCustCtgCode())) {
			parmCode = "CRBGRACE_INDV_ODDAYS";
		} else {
			parmCode = "CRBGRACE_CORP_ODDAYS";
		}
		
		crbGraceODDays = Integer.parseInt(SystemParameterDetails.getSystemParameterValue(parmCode).toString());

		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_PHASE).toString());
		if (!phase.equals(PennantConstants.APP_PHASE_DAY)) {
			isEOD = true;
		}

		isOverDue = false;
		isCRBOverDue = false;
		dateODFrom = financeMain.getFinStartDate();
		dateCRBODFrom = financeMain.getFinStartDate();
		dateLastFullyPaid = financeMain.getFinStartDate();

		if (valueDate.before(financeMain.getFinStartDate())) {
			isFutureDeal = true;
		}

		if (financeMain.getGrcPeriodEndDate() == null) {
			financeMain.setGrcPeriodEndDate(financeMain.getFinStartDate());
		}

		// Reset Finance Profit Details
		aeAmountCodes.setFinReference(financeMain.getFinReference());
		aeAmountCodes.setNextRepayPftDate(financeMain.getNextRepayPftDate());
		aeAmountCodes.setNextRepayRvwDate(financeMain.getNextRepayRvwDate());
		aeAmountCodes.setODInst(0);
		aeAmountCodes.setCRBODInst(0);
		aeAmountCodes.setPftInAdv(zeroValue);

		// FIND Schedule and Repayment Dates
		int sdSize = schdDetails.size();

		datePrvRepay = financeMain.getFinStartDate();
		datePrvSchd = datePrvRepay;
		dateRefundCheck = datePrvRepay;

		// NORMAL Day or EOD/SOD
		if (isEOD) {
			dateAccrueValue = DateUtility.addDays(valueDate, 1);
		} else {
			dateAccrueValue = valueDate;
		}

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		FinanceScheduleDetail prvSchd = new FinanceScheduleDetail();

		boolean isFirstRepaySet = false;
		
		// Calculate Amounts
		for (int i = 0; i < sdSize; i++) {
			curSchd = schdDetails.get(i);
			dateCurRecord = curSchd.getSchDate();

			if (!isFirstRepaySet && (curSchd.isRepayOnSchDate() || curSchd.isDeferedPay() ||
					(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
				aeAmountCodes.setFirstRepayAmt(curSchd.getPrincipalSchd()
				        .add(curSchd.getProfitSchd()).add(curSchd.getDefPrincipalSchd())
				        .add(curSchd.getDefProfitSchd()));
				aeAmountCodes.setFirstRepayDate(curSchd.getSchDate());
				isFirstRepaySet = true;
            }
			
			if (i != 0) {
				prvSchd = schdDetails.get(i - 1);
				datePrvRecord = prvSchd.getSchDate();
			} else {
				datePrvRecord = dateCurRecord;
			}

			// Method calling for calculation of AmountCodes for all Schedules
			aeAmountCodes = calAEAmounts(curSchd, prvSchd, i, valueDate,
			        financeMain.getProfitDaysBasis(), isAccrualProcess);
			
			if ((i != 0) && (valueDate.compareTo(curSchd.getSchDate()) <= 0 || (i == sdSize-1)) && 
					aeAmountCodes.getNextRpySchDate() == null){
				aeAmountCodes.setNextRpySchDate(curSchd.getSchDate());
	 				
					if(i != 0){
						aeAmountCodes.setLastRpySchDate(prvSchd.getSchDate());
						aeAmountCodes.setLastRpySchPft(prvSchd.getPrincipalSchd());
						aeAmountCodes.setLastRpySchPri(prvSchd.getProfitSchd());
					}
					
					aeAmountCodes.setNextSchdPri(curSchd.getPrincipalSchd().add(
					        curSchd.getDefPrincipalSchd()));
					aeAmountCodes.setNextSchdPft(curSchd.getProfitSchd().add(
					        curSchd.getDefProfitSchd()));
					aeAmountCodes.setNextSchdPriBal(aeAmountCodes.getNextSchdPri()
					        .subtract(curSchd.getSchdPriPaid())
					        .subtract(curSchd.getDefSchdPriPaid()));
					aeAmountCodes.setNextSchdPftBal(aeAmountCodes.getNextSchdPft()
					        .subtract(curSchd.getSchdPftPaid())
					        .subtract(curSchd.getDefSchdPftPaid()));
 			}
		}
				
		// Depreciation Calculation depends on Total Finance Amount with Days basis
		if (isAccrualProcess) {

			BigDecimal daysBetweenFinStartMaturityDate  = new BigDecimal(DateUtility.getDaysBetween(
					financeMain.getMaturityDate(),financeMain.getFinStartDate()));
			BigDecimal daysBetweenFinStartValueDate     = new BigDecimal(DateUtility.getDaysBetween(
					valueDate, financeMain.getFinStartDate()));
			BigDecimal daysBetweenStartYearValueDate = new BigDecimal(DateUtility.getDaysBetween(
					valueDate, DateUtility.getYearStartDate(valueDate)));
			BigDecimal finAmount = financeMain.getFinAmount().subtract(financeMain.getDownPayment()).
					add(financeMain.getFeeChargeAmt());
			
			
			accumulatedPriTillDate = (finAmount.multiply(daysBetweenFinStartValueDate))
					.divide(daysBetweenFinStartMaturityDate, 0, RoundingMode.HALF_DOWN);

			if(daysBetweenStartYearValueDate.compareTo(daysBetweenFinStartValueDate) < 0){
				depreciatePri = (finAmount.multiply(daysBetweenStartYearValueDate)).
						divide(daysBetweenFinStartMaturityDate, 0, RoundingMode.HALF_DOWN);
			}else{
				depreciatePri = (finAmount.multiply(daysBetweenFinStartValueDate)).
						divide(daysBetweenFinStartMaturityDate, 0, RoundingMode.HALF_DOWN);
			}

		}
		
		//Last Repay Amount Value
		Date lastSchDate = null;
		if((sdSize -1) > 0){
			FinanceScheduleDetail lastSchd = schdDetails.get(sdSize -1);
			lastSchDate = lastSchd.getSchDate();
			aeAmountCodes.setLastRepayAmt(lastSchd.getPrincipalSchd()
					.add(lastSchd.getProfitSchd()).add(lastSchd.getDefPrincipalSchd())
					.add(lastSchd.getDefProfitSchd()));
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

		aeAmountCodes.setDaysDiff(DateUtility.getDaysBetween(valueDate, dateCurRepay));

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
		
		//Principal Depreciation Value Till now
		aeAmountCodes.setAccumulatedDepPri(accumulatedPriTillDate);
		
		if(lastSchDate != null && (valueDate.compareTo(lastSchDate) > 0) && (DateUtility.getYear(valueDate) !=  DateUtility.getYear(lastSchDate))){
			depreciatePri = BigDecimal.ZERO;
		}
		aeAmountCodes.setDepreciatePri(depreciatePri);

		// ----------------------------------------------------
		// ACCRUALS SETTING
		// ----------------------------------------------------
		// Accrued Profit Till Now
		if (financeMain.isFinIsActive()) {
        	aeAmountCodes.setAccrue(accrueTillDate.subtract(aeAmountCodes.getPftInAdv()));

			// Accrued Profit Suspended Till now
			if (isSusp) {
				aeAmountCodes.setAccrueS(aeAmountCodes.getAccrue().subtract(accrueTillSusp));
			} else {
				aeAmountCodes.setAccrueS(BigDecimal.ZERO);
			}

			// Accrued Till Last posting
			aeAmountCodes.setlAccrue(pftDetail.getAcrTillLBD());

			// Accrue calculated From Last Posted to Next business date
			aeAmountCodes.setDAccrue(accrueTillDate.subtract(aeAmountCodes.getlAccrue()));

			// Accrued Till NextBusiness Date
			aeAmountCodes.setNAccrue(accrueTillDate.subtract(aeAmountCodes.getPftInAdv()));

        } else {
			aeAmountCodes.setAccrue(zeroValue);
			aeAmountCodes.setAccrueS(zeroValue);
			aeAmountCodes.setlAccrue(pftDetail.getAcrTillLBD());
			aeAmountCodes.setDAccrue(accrueTillDate.subtract(aeAmountCodes.getlAccrue()));
			aeAmountCodes.setNAccrue(zeroValue);
		}
		
		// ----------------------------------------------------
		// AMORTIZATION SETTING
		// ----------------------------------------------------
		if (financeMain.isFinIsActive()) {
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
			
		} else {
			aeAmountCodes.setAmz(aeAmountCodes.getPftAP());
			aeAmountCodes.setAmzS(zeroValue);
			aeAmountCodes.setlAmz(pftDetail.getAmzTillLBD());
			aeAmountCodes.setdAmz(amzTillDate.subtract(aeAmountCodes.getlAmz()));
			aeAmountCodes.setnAmz(aeAmountCodes.getPftAP());
		}

		// Overdue Days
		if (isOverDue) {
			
			// NORMAL Day or EOD/SOD
			if (isEOD) {
				aeAmountCodes.setODDays(DateUtility.getDaysBetween(dateODFrom, dateAccrueValue));
			} else {
				aeAmountCodes.setODDays(DateUtility.getDaysBetween(dateODFrom, valueDate));
			}
			
			if(pftDetail.getFirstODDate() == null){
				aeAmountCodes.setFirstODDate(dateODFrom);
			}
			
			if (isCRBOverDue) {
				
				// CRB Detail : Day or EOD/SOD
				if (isEOD) {
					aeAmountCodes.setCRBODDays(DateUtility.getDaysBetween(dateCRBODFrom, dateAccrueValue));
				} else {
					aeAmountCodes.setCRBODDays(DateUtility.getDaysBetween(dateCRBODFrom, valueDate));
				}

				//First Overdue Date after CRB Reported Grace days
				if(pftDetail.getCRBFirstODDate() == null){
					aeAmountCodes.setCRBFirstODDate(dateCRBODFrom);
				}
			}
			
			//Fetch Finance Overdue Details
			FinODDetails finODDetails = getFinODDetailsDAO().getFinODSummary(
			        financeMain.getFinReference(), crbGraceODDays , true, "");

			if (finODDetails != null) {
				aeAmountCodes.setPriOD(finODDetails.getFinCurODPri());
				aeAmountCodes.setPftOD(finODDetails.getFinCurODPft());
				aeAmountCodes.setCRBPriOD(finODDetails.getCRBFinCurODPri());
				aeAmountCodes.setCRBPftOD(finODDetails.getCRBFinCurODPft());
				aeAmountCodes.setPenaltyPaid(finODDetails.getTotPenaltyPaid());
				aeAmountCodes.setPenaltyDue(finODDetails.getTotPenaltyBal());
				aeAmountCodes.setPenaltyWaived(finODDetails.getTotWaived());
			}

		} else {
			aeAmountCodes.setODDays(0);
			aeAmountCodes.setCRBODDays(0);
		}

		// Last Fully Paid Days
		aeAmountCodes
		        .setDaysFromFullyPaid(DateUtility.getDaysBetween(dateLastFullyPaid, valueDate));
		
		BigDecimal dayFactor = BigDecimal.ONE;
		if(financeMain.getMaturityDate() != null){
			dayFactor = CalculationUtil.getInterestDays(financeMain.getFinStartDate(),
					financeMain.getMaturityDate(), financeMain.getProfitDaysBasis());
		}
		dayFactor = dayFactor.setScale(9, RoundingMode.HALF_DOWN);

		/*
		 * Rate = (Profit including Capitalized * 100) / (Principal Paid excluding Capitalized * Day Factor)
		 * 
		 */
		if (aeAmountCodes.getPri().compareTo(BigDecimal.ZERO) != 0) {
			aeAmountCodes.setCurFlatRate(aeAmountCodes
			        .getPft()
			        .add(aeAmountCodes.getCpzTot())
			        .multiply(new BigDecimal(100))
			        .divide((aeAmountCodes.getPri().subtract(aeAmountCodes.getCpzTot()))
			                .multiply(dayFactor), 9, RoundingMode.HALF_DOWN));
		}

		aeAmountCodes.setFullyPaidDate(dateLastFullyPaid);

		setAEAmountCodes(aeAmountCodes);
		logger.debug("Leaving");
	}

	/*
	 * Start Calculating the Amounts for accounting engine
	 */
	private AEAmountCodes calAEAmounts(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd, 
			int curSchdRecord, Date valueDate, String profitDayBasis, boolean isAccrualProcess) {

		// -------------------------------------------------------------------------------------------------
		// Set Disbursement and down payment amounts
		// -------------------------------------------------------------------------------------------------
		if (dateCurRecord.compareTo(valueDate) == 0) {
			aeAmountCodes.setDisburse(curSchd.getDisbAmount());
			aeAmountCodes.setDownpay(curSchd.getDownPaymentAmount());
		}

		// Set Date for refund checking
		//TODO
		//Pradeep-04 Jan 14: Why isCpzOnSchDate condition included here???
		//How this dateRefundCheck is in used in the refund calculation???
		if (curSchd.isCpzOnSchDate() && dateCurRecord.compareTo(dateRefundCheck) > 0
		        && dateCurRecord.compareTo(dateCurRepay) < 0) {
			dateRefundCheck = dateCurRecord;
		}

		// -------------------------------------------------------------------------------------------------
		// TOTALS FOR ALL PERIODS
		// -------------------------------------------------------------------------------------------------
		
		//Boolean is only used to reduce validations related to schedule dates
		if (!isCurSchdSet) {
			//Previous Record Date less than value date then set previous details
			if (dateCurRecord.compareTo(valueDate) < 0) {
				datePrvSchd = dateCurRecord;
			} 

			//Value Date > Previous Record Date AND <= Current Record Date then set current schedule date 
			if (valueDate.compareTo(datePrvRecord) > 0 && valueDate.compareTo(dateCurRecord) <= 0) {
				dateCurSchd = dateCurRecord;
				isCurSchdSet = true;
				aeAmountCodes.setCpzCur(aeAmountCodes.getCpzCur().add(curSchd.getCpzAmount()));
			}
        }

		//Boolean is only used to reduce validations related t
		if (!isCurSchdSuspSet) {
			//Previous Record Date less than vaue date then set previous details
			if (dateCurRecord.compareTo(dateSuspPftTfr) < 0) {
				datePrvSchdSusp = dateCurRecord;
			} 

			//Value Date > Previous Record Date AND <= Current Record Date then set current schedule date 
			if (valueDate.compareTo(datePrvRecord) > 0 && dateSuspPftTfr.compareTo(dateCurRecord) <= 0) {
				dateCurSchdSusp = dateCurRecord;
				isCurSchdSuspSet = true;
            }
        }

		//If the schedule date less than current schedule date set reducing rate.
		if (dateCurRecord.compareTo(valueDate) <= 0) {
			aeAmountCodes.setCurReducingRate(curSchd.getCalculatedRate());
		}

		
		//REPAYMENT Records
		if (curSchd.isRepayOnSchDate() || curSchd.isDeferedPay() ||
				(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
			
			//Total Terms Calculation
			aeAmountCodes.setTtlTerms(aeAmountCodes.getTtlTerms() + 1);

			// Last Fully Paid Date
			if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) == 0
			        && curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0
			        && curSchd.getDefPrincipalSchd().compareTo(curSchd.getDefSchdPriPaid()) == 0
			        && curSchd.getDefProfitSchd().compareTo(curSchd.getDefSchdPftPaid()) == 0){

				if( (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0 
						|| curSchd.getDefRepaySchd().compareTo(BigDecimal.ZERO) > 0)) {
					dateLastFullyPaid = dateCurRecord;
				}

				//Total Paid Terms Calculation
				aeAmountCodes.setPaidInst(aeAmountCodes.getPaidInst() + 1);
			}

			//Elapsed Terms Calculation
			if (dateCurRecord.compareTo(valueDate) <= 0) {
				aeAmountCodes.setElpTerms(aeAmountCodes.getElpTerms() + 1);
			}
			
			boolean isOverdueSchd = false;
			if(isAccrualProcess){
				isOverdueSchd = (dateCurRecord.compareTo(valueDate) <= 0) ;
			}else{
				isOverdueSchd = dateCurRecord.before(valueDate);
			}

			//Over Due Calculations
			if (isOverdueSchd && isOverDue) {
				aeAmountCodes.setODInst(aeAmountCodes.getODInst() + 1);
				aeAmountCodes.setLastODDate(dateCurRecord);
			}

			if (isOverdueSchd && !isOverDue) {
				if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) != 0
				        || curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) != 0
				        || curSchd.getDefPrincipalSchd().compareTo(curSchd.getDefSchdPriPaid()) != 0
				        || curSchd.getDefProfitSchd().compareTo(curSchd.getDefSchdPftPaid()) != 0) {
					dateODFrom = curSchd.getSchDate();
					aeAmountCodes.setODInst(1);
					aeAmountCodes.setLastODDate(dateODFrom);
					isOverDue = true;
				}
			}
			
			//Over Due Calculations
			if (isOverdueSchd && isCRBOverDue) {
				int days = DateUtility.getDaysBetween(dateCurRecord, dateAccrueValue);
				if(days > crbGraceODDays){
					aeAmountCodes.setCRBODInst(aeAmountCodes.getCRBODInst() + 1);
					aeAmountCodes.setCRBLastODDate(dateCurRecord);
				}
			}
			
			if (isOverdueSchd && !isCRBOverDue) {
				if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) != 0
						|| curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) != 0
						|| curSchd.getDefPrincipalSchd().compareTo(curSchd.getDefSchdPriPaid()) != 0
						|| curSchd.getDefProfitSchd().compareTo(curSchd.getDefSchdPftPaid()) != 0) {
					
					int days = DateUtility.getDaysBetween(dateCurRecord, dateAccrueValue);
					if(days > crbGraceODDays){
						dateCRBODFrom = curSchd.getSchDate();
						aeAmountCodes.setCRBODInst(1);
						aeAmountCodes.setCRBLastODDate(dateCRBODFrom);
						isCRBOverDue = true;
					}
				}
			}

			//Boolean is only used to reduce validations
			if (!isCurRepaySet) {
				//Previous Record Date less than vaue date then set previous details
				if (dateCurRecord.compareTo(valueDate) < 0) {
					datePrvRepay = dateCurRecord;
				} 

				//Value Date > Previous Record Date AND <= Current Record Date then set current schedule date 
				if (valueDate.compareTo(datePrvRecord) > 0 && valueDate.compareTo(dateCurRecord) <= 0) {
					dateCurRepay = dateCurRecord;
					isCurRepaySet = true;
	            }
				
				// Payments till current repayment date
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

				// Amortization till current repayment date
				amzTillRepay = amzTillRepay.add(curSchd.getProfitCalc());
    
            }  
		}

		/*//Latest Repay Schedule Profit (Either Partially or Fully paid -  include Deferment Schedule Amount)
		if(curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || curSchd.getDefSchdPftPaid().compareTo(BigDecimal.ZERO) > 0){
			aeAmountCodes.setLastRpySchPft(curSchd.getSchdPftPaid().add(curSchd.getDefSchdPftPaid()));
			aeAmountCodes.setLastRpySchDate(curSchd.getSchDate());
		}

		//Latest Repay Schedule principal (Either Partially or Fully paid -  include Deferment Schedule Amount)
		if(curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0 || curSchd.getDefSchdPriPaid().compareTo(BigDecimal.ZERO) > 0){
			aeAmountCodes.setLastRpySchPri(curSchd.getSchdPriPaid().add(curSchd.getDefSchdPriPaid()));
			aeAmountCodes.setLastRpySchDate(curSchd.getSchDate());
		}
		
		//Next Schedule Repayments Date
		if (aeAmountCodes.getNextRpySchDate() == null && (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) != 0
		        || curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) != 0
		        || curSchd.getDefPrincipalSchd().compareTo(curSchd.getDefSchdPriPaid()) != 0
		        || curSchd.getDefProfitSchd().compareTo(curSchd.getDefSchdPftPaid()) != 0)) {
			aeAmountCodes.setNextRpySchDate(curSchd.getSchDate());
		}*/
		
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
			if (dateCurRecord.compareTo(valueDate) == 0) {
				aeAmountCodes.setCpzPrv(aeAmountCodes.getCpzPrv().add(curSchd.getCpzAmount()));
			}
		}

		if (dateCurRecord.compareTo(datePrvSchd) <= 0) {
			aeAmountCodes.setCpzPrv(aeAmountCodes.getCpzPrv().add(curSchd.getCpzAmount()));
		}

		// Accrued and Amortized till amortization value date PAST
		if (dateCurRecord.compareTo(dateAccrueValue) < 0) {
			// Accrue till previous scheduled date
			accrueTillDate = accrueTillDate.add(curSchd.getProfitCalc())
			        .subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDefSchdPftPaid())
			        .subtract(curSchd.getCpzAmount());

			// Amortization till previous scheduled date
			amzTillDate = amzTillDate.add(curSchd.getProfitCalc());

		}
		
		/*// Principal Accumulated till previous scheduled date
		if (isAccrualProcess && dateCurRecord.compareTo(valueDate) < 0) {
			accumulatedPriTillDate = accumulatedPriTillDate.add(curSchd.getPrincipalSchd()).add(curSchd.getDefPrincipalSchd());
			
			//Yearly Depreciation value calculation
			if(DateUtility.getYear(prvSchd.getSchDate()) != DateUtility.getYear(curSchd.getSchDate())  && curSchdRecord != 0){
				depreciatePri = BigDecimal.ZERO;
				
				BigDecimal priAmt = curSchd.getPrincipalSchd().add(curSchd.getDefPrincipalSchd());
				int actualDays = DateUtility.getDaysBetween(curSchd.getSchDate(), DateUtility.getMonthEndDate(prvSchd.getSchDate()));
				int totalDays = DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchd.getSchDate());
				depreciatePri = (priAmt.multiply(new BigDecimal(actualDays))).divide(new BigDecimal(totalDays), 0, RoundingMode.HALF_DOWN);
				
			}else{
				depreciatePri = depreciatePri.add(curSchd.getPrincipalSchd()).add(curSchd.getDefPrincipalSchd());
			}
		}*/

		/*// Transfered Accrued Amount till PAST month End Date
		if (dateCurRecord.compareTo(dateAccrueTsfdValue) <= 0) {
			// Accrue till previous Month End date
			accrueTillTsfdDate = accrueTillTsfdDate.add(curSchd.getProfitCalc())
			        .subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDefSchdPftPaid())
			        .subtract(curSchd.getCpzAmount());

			prvAccrueTsfdDate = dateCurRecord;
		}*/

		//###NEW Accrued till suspense date month start PAST
		if (isSusp) {
			if (dateCurRecord.compareTo(dateSuspPftTfr) < 0) {
				// Accrue till previous scheduled date
				accrueTillSusp = accrueTillSusp.add(curSchd.getProfitCalc())
				        .subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDefSchdPftPaid())
				        .subtract(curSchd.getCpzAmount());
			}
		}

		// VALUE DATE IS LAST DATE OF PREVIOUS SCHDULE RECORD
		if (curSchdRecord != 0) {
			if (datePrvRecord.compareTo(valueDate) == 0) {
				aeAmountCodes = calPartAccrual(curSchd, datePrvRecord,
				        prvSchd.getCalculatedRate(), profitDayBasis);
				
				/*//Principal Accrual Calculation
				if(isAccrualProcess){
					calPartPriAccrual(curSchd, datePrvRecord,valueDate);
				}*/
			}

			//###NEW Suspense From DATE IS LAST DATE OF PREVIOUS SCHDULE RECORD
			if (isSusp) {
				if (prvSchd.getSchDate().compareTo(dateSuspPftTfr) == 0) {
					aeAmountCodes = calPartAccrualSusp(curSchd, datePrvRecord,
					        prvSchd.getCalculatedRate(), profitDayBasis);
				}
			}

			if (isCurSchdSet) {
				if (dateCurRecord.compareTo(dateAccrueValue) >= 0 && dateCurRecord.compareTo(dateCurSchd) <= 0) {
					aeAmountCodes = calPartAccrual(curSchd, datePrvSchd, prvSchd.getCalculatedRate(),
					        profitDayBasis);
				}    
				
				/*//Principal Accrual Calculation
				if(isAccrualProcess){
					if(datePrvRecord.compareTo(valueDate) < 0 && dateCurRecord.compareTo(valueDate) > 0){
						calPartPriAccrual(curSchd, datePrvRecord,valueDate);
					}
				}*/
            }
			

			//###NEW Suspense From DATE IS BETWEEN the schedule dates
			if (isSusp && isCurSchdSuspSet) {
				if (dateCurRecord.compareTo(dateSuspPftTfr) >= 0
				        && dateCurRecord.compareTo(dateCurSchdSusp) <= 0) {
					aeAmountCodes = calPartAccrualSusp(curSchd, datePrvSchdSusp,
					        prvSchd.getCalculatedRate(), profitDayBasis);
				}
			}
			
			/*//Partial Transfered Accrue Amount Till Last Month End
			if (dateCurRecord.compareTo(dateAccrueTsfdValue) > 0
			        && valueDate.compareTo(dateAccrueTsfdValue) == 0) {
				calPartAccrualTsfd(curSchd, prvAccrueTsfdDate, prvSchd.getCalculatedRate(),
				        profitDayBasis);
			}*/
		}

		//Profit Paid in advance
		if (isNextSchdSet) {
			aeAmountCodes.setPftInAdv(aeAmountCodes.getPftInAdv().add(curSchd.getSchdPftPaid())
			        .add(curSchd.getDefSchdPftPaid()));
		}
		return aeAmountCodes;
	}

	private AEAmountCodes calPartAccrual(FinanceScheduleDetail curSchd, Date dateFrom,
	        BigDecimal pftRate, String profitDayBasis) {

		//	int daysIn = 0;
		BigDecimal pftCalAsOfNow = zeroValue;

		// Calculate Accruals if not in future
		if (!isFutureDeal) {
			// Days in current schedule record
			//	daysIn = DateUtility.getDaysBetween(dateFrom, dateAccrueValue);

			if (curSchd.getSchDate().compareTo(dateAccrueValue) != 0) {
				/*pftCalAsOfNow = curSchd.getBalanceForPftCal().multiply(new BigDecimal(daysIn)).multiply(pftRate);
				pftCalAsOfNow = pftCalAsOfNow.setScale(0, RoundingMode.HALF_DOWN);*/

				BigDecimal dayFactor = CalculationUtil.getInterestDays(dateFrom, dateAccrueValue,
				        profitDayBasis);
				dayFactor = dayFactor.setScale(9, RoundingMode.HALF_DOWN);

				pftCalAsOfNow = ((curSchd.getBalanceForPftCal().multiply(pftRate))
				        .multiply(dayFactor))
				        .divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);

			} else {
				pftCalAsOfNow = curSchd.getProfitCalc();
			}
		}

		// Accure till Value date
		if (curSchd.getSchDate().compareTo(dateAccrueValue) == 0) {
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

		return aeAmountCodes;
	}
	
	/**
	 * Method for Calculating Partial Principal Accrual Amount
	 * @param curSchd
	 * @param dateFrom
	 * @param valueDate
	 */
	/*private void calPartPriAccrual(FinanceScheduleDetail curSchd, Date dateFrom, Date valueDate) {

		BigDecimal priCalAsOfNow = zeroValue;

		// Calculate Accruals if not in future
		if (!isFutureDeal) {
			
			// Days in current schedule record
			int daysIn = DateUtility.getDaysBetween(dateFrom, valueDate);
			int totaldaysIn = DateUtility.getDaysBetween(dateFrom, curSchd.getSchDate());
			priCalAsOfNow = curSchd.getPrincipalSchd().add(curSchd.getDefPrincipalSchd());

			if (curSchd.getSchDate().compareTo(valueDate) != 0) {
				priCalAsOfNow = (priCalAsOfNow.multiply(new BigDecimal(daysIn))).divide(new BigDecimal(totaldaysIn), 0, RoundingMode.HALF_DOWN);
			}
		}

		//Principal Accrue till Value date
		accumulatedPriTillDate = accumulatedPriTillDate.add(priCalAsOfNow);
		depreciatePri = depreciatePri.add(priCalAsOfNow);

	}*/

	/*private void calPartAccrualTsfd(FinanceScheduleDetail curSchd, Date dateFrom,
	        BigDecimal pftRate, String profitDayBasis) {

		BigDecimal accrueAsOfLastMnthEnd = zeroValue;

		// Calculate Accruals if not in future
		if (!isFutureDeal) {

			if (curSchd.getSchDate().compareTo(dateAccrueTsfdValue) != 0) {
				
				Date fromDate = DateUtility.addDays(dateFrom, 1);

				BigDecimal dayFactor = CalculationUtil.getInterestDays(fromDate,
						dateAccrueTsfdValue, profitDayBasis);
				dayFactor = dayFactor.setScale(9, RoundingMode.HALF_DOWN);

				accrueAsOfLastMnthEnd = ((curSchd.getBalanceForPftCal().multiply(pftRate))
				        .multiply(dayFactor))
				        .divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);

				accrueTillTsfdDate = accrueTillTsfdDate.add(accrueAsOfLastMnthEnd)
				        .subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDefSchdPftPaid());

			}
		}
	}*/

	private AEAmountCodes calPartAccrualSusp(FinanceScheduleDetail curSchd, Date dateFrom,
	        BigDecimal pftRate, String profitDayBasis) {

		BigDecimal pftCalAsOfNow = zeroValue;

		if (curSchd.getSchDate().compareTo(dateSuspPftTfr) != 0) {
			BigDecimal dayFactor = CalculationUtil.getInterestDays(dateFrom, dateSuspPftTfr,
			        profitDayBasis);
			dayFactor = dayFactor.setScale(9, RoundingMode.HALF_DOWN);

			pftCalAsOfNow = ((curSchd.getBalanceForPftCal().multiply(pftRate)).multiply(dayFactor))
			        .divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
		} else {
			pftCalAsOfNow = curSchd.getProfitCalc();
		}

		accrueTillSusp = accrueTillSusp.add(pftCalAsOfNow).subtract(curSchd.getSchdPftPaid())
		        .subtract(curSchd.getDefSchdPftPaid());
		return aeAmountCodes;
	}

	/**
	 * Method for Preparation of DataSet object
	 * 
	 * @param financeMain
	 * @param eventCode
	 * @return
	 */
	public static DataSet createDataSet(FinanceMain financeMain, String eventCode,
	        Date dateValueDate, Date dateSchdDate) {
		return new AEAmounts(financeMain, eventCode, dateValueDate, dateSchdDate).getDataSet();
	}

	private AEAmounts(FinanceMain financeMain, String eventCode, Date dateValueDate,
	        Date dateSchdDate) {
		logger.debug("Entering");
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(financeMain.getFinReference());

		if (eventCode.equals("")) {
			if (financeMain.getFinStartDate().after(
			        (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR))) {
				dataSet.setFinEvent("ADDDBSF");
			} else {
				dataSet.setFinEvent("ADDDBSP");
			}
		} else {
			dataSet.setFinEvent(eventCode);
		}

		dataSet.setFinBranch(financeMain.getFinBranch());
		dataSet.setFinCcy(financeMain.getFinCcy());
		dataSet.setPostDate((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR));
		dataSet.setValueDate(dateValueDate);
		dataSet.setSchdDate(dateSchdDate);
		dataSet.setFinType(financeMain.getFinType());
		dataSet.setCustId(financeMain.getCustID());
		dataSet.setDisburseAccount(financeMain.getDisbAccountId());
		dataSet.setRepayAccount(financeMain.getRepayAccountId());
		dataSet.setFinAccount(financeMain.getFinAccount());
		dataSet.setFinCustPftAccount(financeMain.getFinCustPftAccount());
		dataSet.setDisburseAmount(financeMain.getCurDisbursementAmt());
		dataSet.setFinPurpose(financeMain.getFinPurpose());
		dataSet.setCmtReference(financeMain.getFinCommitmentRef());
		dataSet.setFinAmount(financeMain.getFinAmount());

		if (financeMain.isNewRecord()
		        || PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			dataSet.setNewRecord(true);
		} else {
			dataSet.setNewRecord(false);
		}

		dataSet.setDownPayment(financeMain.getDownPayment() == null ? BigDecimal.ZERO : financeMain.getDownPayment());
		dataSet.setDownPayBank(financeMain.getDownPayBank() == null ? BigDecimal.ZERO : financeMain.getDownPayBank());
		dataSet.setDownPaySupl(financeMain.getDownPaySupl() == null ? BigDecimal.ZERO : financeMain.getDownPaySupl());
		dataSet.setDownPayAccount(financeMain.getDownPayAccount());
		dataSet.setSecurityDeposit(financeMain.getSecurityDeposit() == null ? BigDecimal.ZERO : financeMain.getSecurityDeposit());
		dataSet.setGrcPftChg(financeMain.getTotalGrossGrcPft() == null ? BigDecimal.ZERO : financeMain.getTotalGrossGrcPft());
		dataSet.setNoOfTerms(financeMain.getNumberOfTerms()+ financeMain.getGraceTerms());

		//Tenure Calculation
		int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate(),true);
		dataSet.setTenure(months);
		
		setDataSet(dataSet);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AEAmountCodes getAeAmountCodes() {
		return aeAmountCodes;
	}
	public void setAEAmountCodes(AEAmountCodes aeAmountCodes) {
		this.aeAmountCodes = aeAmountCodes;
	}

	public DataSet getDataSet() {
		return dataSet;
	}
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public static FinanceSuspHeadDAO getSuspHeadDAO() {
		return suspHeadDAO;
	}
	public void setSuspHeadDAO(FinanceSuspHeadDAO suspHeadDAO) {
		AEAmounts.suspHeadDAO = suspHeadDAO;
	}

	public static FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		AEAmounts.finODDetailsDAO = finODDetailsDAO;
	}

	public static CustomerDAO getCustomerDAO() {
		return customerDAO;
	}
	public void setCustomerDAO(CustomerDAO customerDAO) {
		AEAmounts.customerDAO = customerDAO;
	}
}
