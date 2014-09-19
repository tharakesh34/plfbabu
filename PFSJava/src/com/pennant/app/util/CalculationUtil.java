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
 * FileName    		:  CalculationUtil.java													*                           
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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;

public class CalculationUtil implements Serializable {

    private static final long serialVersionUID = -7140560124513312794L;
    
    private static CurrencyDAO currencyDAO;
    
	public static BigDecimal getInterestDays(Date startDate, Date endDate, String strDaysBasis) {

		strDaysBasis = StringUtils.trimToEmpty(strDaysBasis);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);

		if (startCalendar.equals(endCalendar)) {
			return BigDecimal.ZERO;
		}

		if (startCalendar.after(endCalendar)) {
			Calendar tempCalendar = startCalendar;
			startCalendar = (Calendar) endCalendar.clone();
			endCalendar = tempCalendar;
		}

		if (strDaysBasis.equals(CalculationConstants.IDB_30U360)) {
			return getIDB_30U360(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_30E360)) {
			return getIDB_30E360(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_30E360I)) {
			return getIDB_30E360I(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_30EP360)) {
			return getIDB_30EP360(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_ACT_ICMS)) {
			return getIDB_ACT_ICMS(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_ACT_ISDA)) {
			return getIDB_ACT_ISDA(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_ACT_AFB)) {
			return getIDB_ACT_AFB(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_ACT_365FIXED)) {
			return getIDB_ACT_365FIXED(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_ACT_360)) {
			return getIDB_ACT_360(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_ACT_365LEAP)) {
			return getIDB_ACT_365LEAP(startCalendar, endCalendar);

		}

		return BigDecimal.ONE;
	}

	private static BigDecimal getIDB_30U360(Calendar startCalendar, Calendar endCalendar) {

		int dayOfStart = startCalendar.get(Calendar.DAY_OF_MONTH);
		int dayOfEnd = endCalendar.get(Calendar.DAY_OF_MONTH);
		int monthOfStart = startCalendar.get(Calendar.MONTH);
		int monthOfEnd = endCalendar.get(Calendar.MONTH);
		int yearOfStart = startCalendar.get(Calendar.YEAR);
		int yearOfEnd = endCalendar.get(Calendar.YEAR);

		boolean isLastDayOfFebStart = dayOfStart == startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
		&& monthOfStart == Calendar.FEBRUARY;
		boolean isLastDayOfFebEnd = dayOfEnd == startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
		&& monthOfEnd == Calendar.FEBRUARY;

		if (isLastDayOfFebStart && isLastDayOfFebEnd) {
			dayOfEnd = 30;
		}

		if (isLastDayOfFebStart) {
			dayOfStart = 30;
		}

		if (dayOfEnd == 31 && dayOfStart >= 30) {
			dayOfEnd = 30;
		}

		if (dayOfStart == 31) {
			dayOfStart = 30;
		}

		return new BigDecimal(
				(360 * (yearOfEnd - yearOfStart) + 30 * (monthOfEnd - monthOfStart) + (dayOfEnd - dayOfStart)) / 360d);
	}

	private static BigDecimal getIDB_30E360(Calendar startCalendar, Calendar endCalendar) {

		int dayOfStart = startCalendar.get(Calendar.DAY_OF_MONTH);
		int dayOfEnd = endCalendar.get(Calendar.DAY_OF_MONTH);
		int monthOfStart = startCalendar.get(Calendar.MONTH);
		int monthOfEnd = endCalendar.get(Calendar.MONTH);
		int yearOfStart = startCalendar.get(Calendar.YEAR);
		int yearOfEnd = endCalendar.get(Calendar.YEAR);

		if (dayOfEnd == 31) {
			dayOfEnd = 30;
		}

		if (dayOfStart == 31) {
			dayOfStart = 30;
		}

		return new BigDecimal(
				(360 * (yearOfEnd - yearOfStart) + 30 * (monthOfEnd - monthOfStart) + (dayOfEnd - dayOfStart)) / 360d);
	}

	private static BigDecimal getIDB_30E360I(Calendar startCalendar, Calendar endCalendar) {

		int dayOfStart = startCalendar.get(Calendar.DAY_OF_MONTH);
		int dayOfEnd = endCalendar.get(Calendar.DAY_OF_MONTH);
		int monthOfStart = startCalendar.get(Calendar.MONTH);
		int monthOfEnd = endCalendar.get(Calendar.MONTH);
		int yearOfStart = startCalendar.get(Calendar.YEAR);
		int yearOfEnd = endCalendar.get(Calendar.YEAR);

		if (monthOfStart == 2 && dayOfStart > 27) {
			dayOfStart = 30;
		}

		if (monthOfEnd == 2 && dayOfEnd > 27) {
			dayOfStart = 30;
		}

		if (dayOfEnd == 31) {
			dayOfEnd = 30;
		}

		if (dayOfStart == 31) {
			dayOfStart = 30;
		}

		return new BigDecimal(
				(360 * (yearOfEnd - yearOfStart) + 30 * (monthOfEnd - monthOfStart) + (dayOfEnd - dayOfStart)) / 360d);
	}

	private static BigDecimal getIDB_30EP360(Calendar startCalendar, Calendar endCalendar) {

		int dayOfStart = startCalendar.get(Calendar.DAY_OF_MONTH);
		int dayOfEnd = endCalendar.get(Calendar.DAY_OF_MONTH);
		int monthOfStart = startCalendar.get(Calendar.MONTH);
		int monthOfEnd = endCalendar.get(Calendar.MONTH);
		int yearOfStart = startCalendar.get(Calendar.YEAR);
		int yearOfEnd = endCalendar.get(Calendar.YEAR);

		if (dayOfStart == 31) {
			dayOfStart = 30;
		}

		if (dayOfEnd == 31) {
			monthOfEnd = monthOfEnd + 1;
			dayOfEnd = 1;
		}

		return new BigDecimal(
				(360 * (yearOfEnd - yearOfStart) + 30 * (monthOfEnd - monthOfStart) + (dayOfEnd - dayOfStart)) / 360d);
	}

	private static BigDecimal getIDB_ACT_ICMS(Calendar startCalendar, Calendar endCalendar) {
		return BigDecimal.valueOf(1);
	}

	private static BigDecimal getIDB_ACT_ISDA(Calendar startCalendar, Calendar endCalendar) {

		double daysStartIn = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		double daysLeft = daysStartIn - startCalendar.get(Calendar.DAY_OF_YEAR);

		double daysEndIn = endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		double daysEnd = endCalendar.get(Calendar.DAY_OF_YEAR);

		double fraction = ((daysLeft) / daysStartIn) + (daysEnd / daysEndIn);
		return BigDecimal.valueOf(fraction);

	}

	private static BigDecimal getIDB_ACT_AFB(Calendar startCalendar, Calendar endCalendar) {
		return BigDecimal.ONE;
	}

	private static BigDecimal getIDB_ACT_365FIXED(Calendar startCalendar, Calendar endCalendar) {
		return BigDecimal.valueOf(DateUtility.getDaysBetween(startCalendar, endCalendar) / 365d);
	}

	private static BigDecimal getIDB_ACT_360(Calendar startCalendar, Calendar endCalendar) {
		return BigDecimal.valueOf(DateUtility.getDaysBetween(startCalendar, endCalendar) / 360d);
	}

	private static BigDecimal getIDB_ACT_365LEAP(Calendar startCalendar, Calendar endCalendar) {
		double daysInYear = 365d;

		if (endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 366) {
			Calendar tempCalendar = (Calendar) endCalendar.clone();
			tempCalendar.set(Calendar.MONTH, 2);
			tempCalendar.set(Calendar.DATE, 29);

			if (tempCalendar.compareTo(endCalendar) >= 0 && tempCalendar.after(startCalendar)) {
				daysInYear = 366d;
			}

		}

		return BigDecimal.valueOf(DateUtility.getDaysBetween(startCalendar, endCalendar) / daysInYear);
	}

	public static BigDecimal calInterest(Date dtStart, Date dtEnd, BigDecimal principalAmount, String strDaysBasis,
			BigDecimal rate) {
		/*
		 * interest= (Principal * Days Factor * Rate)/100
		 */
		MathContext mathContext = new MathContext(BigDecimal.ROUND_UP);
		BigDecimal daysFactor = getInterestDays(dtStart, dtEnd, strDaysBasis);
		BigDecimal interest = ((principalAmount.multiply(daysFactor, mathContext)).multiply(rate, mathContext))
		.divide(BigDecimal.valueOf(100));
		return interest;
	}

	public static BigDecimal calInstallment(BigDecimal principle, BigDecimal rate, String paymentFrequency, int noOfTerms) {
		/*
		 * M = P(1+r)n r / [(1+r)n-1] 
		 * r=rate/100*frequency
		 */
		int frqequency = 0;

		switch (FrequencyUtil.getFrequencyCode(paymentFrequency).charAt(0)) {

		case 'D' :
			frqequency = CalculationConstants.FRQ_DAILY;
			break;

		case 'W' :
			frqequency = CalculationConstants.FRQ_WEEKLY;
			break;
		case 'F' :
			frqequency = CalculationConstants.FRQ_FORTNIGHTLY;
			break;
		case 'M' :
			frqequency = CalculationConstants.FRQ_MONTHLY;
			break;
		case 'Q' :
			frqequency = CalculationConstants.FRQ_QUARTERLY;
			break;
		case 'H' :
			frqequency = CalculationConstants.FRQ_HALF_YEARLY;
			break;
		case 'Y' :
			frqequency = CalculationConstants.FRQ_YEARLY;
			break;
		default :

			break;

		}
		if(rate.compareTo(BigDecimal.ZERO)!=0){
		BigDecimal r = rate.divide(new BigDecimal(100).multiply(new BigDecimal(frqequency)), 10,
				BigDecimal.ROUND_HALF_DOWN);
		BigDecimal nTimesOfr = (r.add(BigDecimal.ONE)).pow(noOfTerms);
		BigDecimal numerator = principle.multiply(nTimesOfr).multiply(r);
		BigDecimal denominator = nTimesOfr.subtract(BigDecimal.ONE);	
		return numerator.divide(denominator, 10, BigDecimal.ROUND_HALF_DOWN);
		}else{
			return principle.divide(BigDecimal.valueOf(noOfTerms), 10, BigDecimal.ROUND_HALF_DOWN);
		}
		
	}
	
	public static BigDecimal getConvertedAmount(Currency fromCurrency, Currency toCurrency,
	        BigDecimal actualAmount) {

		BigDecimal buyRate = BigDecimal.ZERO;
		BigDecimal sellRate = BigDecimal.ZERO;

		if (fromCurrency.isCcyIsReceprocal()) {
			buyRate = BigDecimal.ONE.divide(fromCurrency.getCcySpotRate(), 9,
					RoundingMode.HALF_DOWN);
		} else {
			buyRate = fromCurrency.getCcySpotRate();
		}

		if (toCurrency.isCcyIsReceprocal()) {
			sellRate = BigDecimal.ONE
			.divide(toCurrency.getCcySpotRate(), 9, RoundingMode.HALF_DOWN);
		} else {
			sellRate = toCurrency.getCcySpotRate();
		}

		actualAmount = (actualAmount.multiply(sellRate)
		        .multiply(toCurrency.getCcyMinorCcyUnits()))
		        .divide(buyRate.multiply(fromCurrency.getCcyMinorCcyUnits()), 0, RoundingMode.HALF_DOWN);
		actualAmount = actualAmount.setScale(0, BigDecimal.ROUND_HALF_DOWN);

		return actualAmount;

	}
	
	/**
	 * Convert Amount based on the exchange rates
	 * @param entityCode
	 * @param fromCcy
	 * @param toCcy
	 * @param amount
	 * @return
	 */
	public static BigDecimal getConvertedAmount(String fromCcy, String toCcy,
	        BigDecimal actualAmount) {

		if(actualAmount.compareTo(BigDecimal.ZERO) == 0){
			return BigDecimal.ZERO;
		}
		
 		//Base Currency 
 		String localCcy = SystemParameterDetails.getSystemParameterValue(PennantConstants.LOCAL_CCY).toString();
 		if(fromCcy == null ){
 			fromCcy = localCcy;
 		}
 		if(toCcy == null ){
 			toCcy = localCcy;
 		}
		
 		if(fromCcy.equals(toCcy) ){
			return actualAmount;
		}

		Currency fromCurrency = null;
		Currency toCurrency = null;
		List<Currency> currencyList =getCurrencyDAO().getCurrencyList(Arrays.asList(fromCcy,toCcy));
		for (Currency currency : currencyList) {
			if (currency.getCcyCode().equals(fromCcy)) {
				fromCurrency = currency;
			} else {
				toCurrency = currency;
			}
		}

		return getConvertedAmount(fromCurrency, toCurrency, actualAmount);

	}
	
	public static String getConvertedAmountASString(String fromCcy, String toCcy, 
			BigDecimal actualAmount) {
			
		if(actualAmount.compareTo(BigDecimal.ZERO) == 0){
			return String.valueOf(BigDecimal.ZERO);
		}
		
		//Base Currency 
		String localCcy = SystemParameterDetails.getSystemParameterValue(PennantConstants.LOCAL_CCY).toString();
		//int format = Integer.parseInt(SystemParameterDetails.getSystemParameterValue(PennantConstants.LOCAL_CCY_FORMAT).toString());
		if(fromCcy == null ){
			fromCcy = localCcy;
		}
		if(toCcy == null ){
			toCcy = localCcy;
		}
		
/*		if(fromCcy.equals(toCcy)){
			return PennantApplicationUtil.amountFormate(actualAmount, format);
		} 
		*/
		Currency fromCurrency =  null;
		Currency toCurrency =  null;
		List<Currency> currencyList = getCurrencyDAO().getCurrencyList(Arrays.asList(fromCcy,toCcy));
		for (Currency currency : currencyList) {
			if(currency.getCcyCode().equals(fromCcy)){
				fromCurrency = currency;
			}else{
				toCurrency = currency;
			}
		}
		
		if(fromCcy.equals(toCcy)){
			return PennantApplicationUtil.amountFormate(actualAmount, fromCurrency.getCcyEditField());
		} 	
		
		BigDecimal bigDecimal= getConvertedAmount(fromCurrency, toCurrency, actualAmount);
		return PennantApplicationUtil.amountFormate(bigDecimal,toCurrency.getCcyEditField() );
		
	}
	
	public static String convertedUnFormatAmount(String fromCcy, String toCcy, 
			BigDecimal actualAmount) {
		
		if(actualAmount.compareTo(BigDecimal.ZERO) == 0){
			return String.valueOf(BigDecimal.ZERO);
		}
		
		//Base Currency 
		String localCcy = SystemParameterDetails.getSystemParameterValue(PennantConstants.LOCAL_CCY).toString();
		if(fromCcy == null ){
			fromCcy = localCcy;
		}
		if(toCcy == null ){
			toCcy = localCcy;
		}
		
		if(fromCcy.equals(toCcy)){
			return String.valueOf(actualAmount);
		} 		
		
		Currency fromCurrency =  null;
		Currency toCurrency =  null;
		List<Currency> currencyList = getCurrencyDAO().getCurrencyList(Arrays.asList(fromCcy,toCcy));
		for (Currency currency : currencyList) {
			if(currency.getCcyCode().equals(fromCcy)){
				fromCurrency = currency;
			}else{
				toCurrency = currency;
			}
		}
		BigDecimal bigDecimal= getConvertedAmount(fromCurrency, toCurrency, actualAmount);
		return String.valueOf(bigDecimal);
		
	}
	
	
	/**
	 * calculate average profit rate [avgProfitRate = (profitAmt * 100)/(Days Factor * principalAmt )]
	 * 
	 * @param (Date) startDate
	 * @param (Date) maturityDate
	 * @param (String) profitDaysBasis
	 * @param (BigDecimal) principalAmt
	 * @param (BigDecimal) maturityAmount
	 * @return(BigDecimal) avgProfitRate
	 */
	public static BigDecimal calcAvgProfitRate(Date startDate, Date maturityDate,  String profitDaysBasis, BigDecimal principalAmt, BigDecimal maturityAmount) {
		BigDecimal avgProfitRate = BigDecimal.ZERO;
		BigDecimal profitAmt = BigDecimal.ZERO;
		
		/*
		 * avgProfitRate = (profitAmt * 100)/(Days Factor * principalAmt)
		 */

		if (principalAmt.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal dayFactor;
		dayFactor = CalculationUtil.getInterestDays(startDate, maturityDate, profitDaysBasis);

		profitAmt = maturityAmount.subtract(principalAmt);
		profitAmt = profitAmt.multiply(new BigDecimal(100));
		
		principalAmt = principalAmt.multiply(dayFactor);

		avgProfitRate = profitAmt.divide(principalAmt, 9, RoundingMode.HALF_UP);

		return avgProfitRate;
	}
	
	
	/**
	 * getCalculatedCommodity <br>
	 * calculate Commodity(quantity, Unit Sell Price, Sell Amount) Based on
	 * (Profit Amount, Finance Amount, Unit Buy Price, Buy Amount)
	 * 
	 * @param (CommidityLoanDetail) commidityLoanDetail
	 * @return(CommidityLoanDetail) commidityLoanDetail
	 */
	public static CommidityLoanDetail getCalculatedCommodity(CommidityLoanDetail commidityLoanDetail) {
		long quantity = 0;
		BigDecimal buyPrice 		= BigDecimal.ZERO;
		BigDecimal buyAmount 		= BigDecimal.ZERO;
		BigDecimal sellAmount 		= BigDecimal.ZERO;
		BigDecimal unitSellPrice 	= BigDecimal.ZERO;
		BigDecimal profit		 	= BigDecimal.ZERO;

		if(commidityLoanDetail.getUnitBuyPrice() != null) {
			buyPrice = commidityLoanDetail.getUnitBuyPrice();
		}

		if(commidityLoanDetail.getBuyAmount() != null) {
			buyAmount = commidityLoanDetail.getBuyAmount();
		}


		if (buyAmount.compareTo(BigDecimal.ZERO) > 0 && buyPrice.compareTo(BigDecimal.ZERO) > 0) {
			quantity = buyAmount.divide(buyPrice, BigDecimal.ROUND_HALF_DOWN).longValue();

			if(commidityLoanDetail.getBuyAmount() != null) {
				profit = commidityLoanDetail.getLovDescFinProfitAmt();
			}

			BigDecimal finAmount = commidityLoanDetail.getLovDescFinAmount();

			if (profit.compareTo(BigDecimal.ZERO) > 0) {
				sellAmount = (buyAmount.multiply(profit)).divide(finAmount, RoundingMode.HALF_DOWN);
				sellAmount = sellAmount.add(buyAmount);
				unitSellPrice = unitSellPrice.add(sellAmount).divide(new BigDecimal(quantity), 9,  RoundingMode.HALF_UP);
			}
		}

		commidityLoanDetail.setQuantity(quantity);
		commidityLoanDetail.setSellAmount(sellAmount);
		commidityLoanDetail.setUnitSellPrice(unitSellPrice);

		return commidityLoanDetail;

	}
	
	public static BigDecimal getExchangeRate(String fromCcy, String toCcy) {
		BigDecimal buyRate = BigDecimal.ZERO;
		BigDecimal sellRate = BigDecimal.ZERO;
		//Base Currency 
		String localCcy = SystemParameterDetails.getSystemParameterValue(PennantConstants.LOCAL_CCY).toString();
		if(fromCcy == null ){
			fromCcy = localCcy;
		}
		if(toCcy == null ){
			toCcy = localCcy;
		}

		if(fromCcy.equals(toCcy) ){
			return BigDecimal.ONE;
		}
		
		Currency toCurrency = null;
		Currency fromCurrency = null;
		List<Currency> currencyList =getCurrencyDAO().getCurrencyList(Arrays.asList(fromCcy,toCcy));
		for (Currency currency : currencyList) {
			if (currency.getCcyCode().equals(fromCcy)) {
				fromCurrency = currency;
			}else {
				toCurrency = currency;
			}
		}

		if (fromCurrency.isCcyIsReceprocal()) {
			buyRate = BigDecimal.ONE.divide(fromCurrency.getCcySpotRate(), 9,
					RoundingMode.HALF_DOWN);
		} else {
			buyRate = fromCurrency.getCcySpotRate();
		}

		if (toCurrency.isCcyIsReceprocal()) {
			sellRate = BigDecimal.ONE
			.divide(toCurrency.getCcySpotRate(), 9, RoundingMode.HALF_DOWN);
		} else {
			sellRate = toCurrency.getCcySpotRate();
		}

		return (sellRate)
		        .divide(buyRate, 9, RoundingMode.HALF_DOWN);
	}

	public static CurrencyDAO getCurrencyDAO() {
	    return currencyDAO;
    }
	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
	    CalculationUtil.currencyDAO = currencyDAO;
    }

	
}
