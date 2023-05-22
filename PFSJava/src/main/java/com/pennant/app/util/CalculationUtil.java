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
 * FileName : CalculationUtil.java *
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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CalculationUtil implements Serializable {
	private static final long serialVersionUID = -7140560124513312794L;

	private static CurrencyDAO currencyDAO;

	public static BigDecimal getInterestDays(Date startDate, Date endDate, String strDaysBasis) {

		strDaysBasis = StringUtils.trimToEmpty(strDaysBasis);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);

		if (startCalendar.compareTo(endCalendar) == 0) {
			return BigDecimal.ZERO;
		}

		if (startCalendar.after(endCalendar)) {
			Calendar tempCalendar = startCalendar;
			startCalendar = (Calendar) endCalendar.clone();
			endCalendar = tempCalendar;
		}

		switch (strDaysBasis) {

		case CalculationConstants.IDB_30U360:
			return getIDB_30U360(startCalendar, endCalendar);
		case CalculationConstants.IDB_30E360:
			return getIDB_30E360(startCalendar, endCalendar);
		case CalculationConstants.IDB_30E360I:
			return getIDB_30E360I(startCalendar, endCalendar);
		case CalculationConstants.IDB_30EP360:
			return getIDB_30EP360(startCalendar, endCalendar);
		case CalculationConstants.IDB_ACT_ISDA:
			return getIDB_ACT_ISDA(startCalendar, endCalendar);
		case CalculationConstants.IDB_ACT_365FIXED:
			return getIDB_ACT_365FIXED(startCalendar, endCalendar);
		case CalculationConstants.IDB_ACT_360:
			return getIDB_ACT_360(startCalendar, endCalendar);
		case CalculationConstants.IDB_ACT_365LEAP:
			return getIDB_ACT_365LEAP(startCalendar, endCalendar);
		case CalculationConstants.IDB_ACT_365LEAPS:
			return getIDB_ACT_365LEAPStart(startCalendar, endCalendar);
		case CalculationConstants.IDB_BY_PERIOD:
			return getIDB_BY_PERIOD(startCalendar, endCalendar);
		case CalculationConstants.IDB_30E360IH:
			return getIDB_30E360IH(startCalendar, endCalendar);
		case CalculationConstants.IDB_30E360IA:
			return getIDB_30E360IA(startCalendar, endCalendar);
		case CalculationConstants.IDB_15E360IA:
			return getIDB_15E360IA(startCalendar, endCalendar);
		default:
			return BigDecimal.ONE;
		}

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

		return BigDecimal.valueOf(
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

		return BigDecimal.valueOf(
				(360 * (yearOfEnd - yearOfStart) + 30 * (monthOfEnd - monthOfStart) + (dayOfEnd - dayOfStart)) / 360d);
	}

	private static BigDecimal getIDB_30E360I(Calendar startCalendar, Calendar endCalendar) {

		int dayOfStart = startCalendar.get(Calendar.DAY_OF_MONTH);
		int dayOfEnd = endCalendar.get(Calendar.DAY_OF_MONTH);
		int monthOfStart = startCalendar.get(Calendar.MONTH);
		int monthOfEnd = endCalendar.get(Calendar.MONTH);
		int yearOfStart = startCalendar.get(Calendar.YEAR);
		int yearOfEnd = endCalendar.get(Calendar.YEAR);

		boolean isFebSetReq = true;
		if (yearOfStart == yearOfEnd && (monthOfStart == Calendar.FEBRUARY || monthOfEnd == Calendar.FEBRUARY)) {
			if (DateUtil.isLeapYear(yearOfStart)) {
				if (dayOfStart == 29 && dayOfEnd == 29) {
					isFebSetReq = false;
				}
			} else {
				if (dayOfStart == 28 && dayOfEnd == 28) {
					isFebSetReq = false;
				}
			}
		}

		if (isFebSetReq && monthOfStart == Calendar.FEBRUARY) {
			if (DateUtil.isLeapYear(yearOfStart)) {
				if (dayOfStart > 28) {
					dayOfStart = 30;
				}
			} else {
				if (dayOfStart > 27) {
					dayOfStart = 30;
				}
			}
		}

		if (isFebSetReq && monthOfEnd == Calendar.FEBRUARY) {
			if (DateUtil.isLeapYear(yearOfEnd)) {
				if (dayOfEnd > 28) {
					dayOfEnd = 30;
				}
			} else {
				if (dayOfEnd > 27) {
					dayOfEnd = 30;
				}
			}
		}

		if (dayOfEnd == 31) {
			dayOfEnd = 30;
		}

		if (dayOfStart == 31) {
			dayOfStart = 30;
		}

		return BigDecimal.valueOf(
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

		return BigDecimal.valueOf(
				(360 * (yearOfEnd - yearOfStart) + 30 * (monthOfEnd - monthOfStart) + (dayOfEnd - dayOfStart)) / 360d);
	}

	private static BigDecimal getIDB_ACT_ISDA(Calendar startCalendar, Calendar endCalendar) {
		double fraction = 0.00;

		double daysStartIn = 0;
		double daysLeft = 0;
		double daysEndIn = 0;
		double daysEnd = 0;

		if (startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)) {
			double daysbetween = endCalendar.get(Calendar.DAY_OF_YEAR) - startCalendar.get(Calendar.DAY_OF_YEAR);
			fraction = daysbetween / startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		} else {
			daysStartIn = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			daysLeft = daysStartIn - startCalendar.get(Calendar.DAY_OF_YEAR);

			daysEndIn = endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			daysEnd = endCalendar.get(Calendar.DAY_OF_YEAR);

			// if (!(startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) ==
			// startCalendar
			// .get(Calendar.DAY_OF_YEAR))) {

			daysLeft = daysLeft + 1;
			daysEnd = daysEnd - 1;

			// }

			fraction = ((daysLeft) / daysStartIn) + (daysEnd / daysEndIn);
		}

		return BigDecimal.valueOf(fraction);
	}

	private static BigDecimal getIDB_ACT_365FIXED(Calendar startCalendar, Calendar endCalendar) {
		return BigDecimal.valueOf(getDaysBetween(startCalendar, endCalendar) / 365d);
	}

	private static BigDecimal getIDB_ACT_360(Calendar startCalendar, Calendar endCalendar) {
		return BigDecimal.valueOf(getDaysBetween(startCalendar, endCalendar) / 360d);
	}

	private static BigDecimal getIDB_ACT_365LEAP(Calendar startCalendar, Calendar endCalendar) {
		double daysInYear = 365d;

		if (endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 366) {
			daysInYear = 366d;
		}

		return BigDecimal.valueOf(getDaysBetween(startCalendar, endCalendar) / daysInYear);
	}

	private static BigDecimal getIDB_ACT_365LEAPStart(Calendar startCalendar, Calendar endCalendar) {
		double daysInYear = 365d;

		if (startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 366) {
			daysInYear = 366d;
		}

		return BigDecimal.valueOf(getDaysBetween(startCalendar, endCalendar) / daysInYear);
	}

	private static BigDecimal getIDB_BY_PERIOD(Calendar startCalendar, Calendar endCalendar) {
		int numberOfDays = calNoOfDays(startCalendar.getTime(), endCalendar.getTime(),
				CalculationConstants.IDB_BY_PERIOD);
		double daysInYear = 360d;
		BigDecimal dayFactor = BigDecimal.valueOf(numberOfDays / daysInYear);
		return dayFactor;
	}

	private static BigDecimal getIDB_30E360IH(Calendar startCalendar, Calendar endCalendar) {
		int numberOfDays = calNoOfDays(startCalendar.getTime(), endCalendar.getTime(),
				CalculationConstants.IDB_30E360I);
		double days30 = 30d;
		double daysInYear = 360d;

		double remainderDays = numberOfDays % days30;
		BigDecimal dayFactor = BigDecimal.ZERO;

		if (remainderDays == 0) {
			dayFactor = BigDecimal.valueOf(numberOfDays / daysInYear);
		} else {
			dayFactor = getIDB_ACT_ISDA(startCalendar, endCalendar);
		}

		return dayFactor;
	}

	private static BigDecimal getIDB_30E360IA(Calendar startCalendar, Calendar endCalendar) {
		int numberOfDays = calNoOfDays(startCalendar.getTime(), endCalendar.getTime(),
				CalculationConstants.IDB_30E360I);
		double days30 = 30d;
		double daysInYear = 360d;

		double remainderDays = numberOfDays % days30;
		BigDecimal dayFactor = BigDecimal.ZERO;

		if (remainderDays == 0) {
			dayFactor = BigDecimal.valueOf(numberOfDays / daysInYear);
		} else {
			dayFactor = getIDB_ACT_365FIXED(startCalendar, endCalendar);
		}

		return dayFactor;
	}

	private static BigDecimal getIDB_15E360IA(Calendar startCalendar, Calendar endCalendar) {
		int numberOfDays = calNoOfDays(startCalendar.getTime(), endCalendar.getTime(),
				CalculationConstants.IDB_30E360I);
		double days15 = 15d;
		double daysInYear = 360d;

		double remainderDays = numberOfDays % days15;

		BigDecimal dayFactor = BigDecimal.ZERO;
		if (remainderDays == 0) {
			dayFactor = BigDecimal.valueOf(numberOfDays / daysInYear);
		} else {
			dayFactor = getIDB_ACT_365FIXED(startCalendar, endCalendar);
		}

		return dayFactor;
	}

	public static BigDecimal calInterest(Date dtStart, Date dtEnd, BigDecimal principalAmount, String strDaysBasis,
			BigDecimal rate) {
		/*
		 * interest= (Principal * Days Factor * Rate)/100
		 */
		MathContext mathContext = new MathContext(0);
		BigDecimal daysFactor = getInterestDays(dtStart, dtEnd, strDaysBasis);
		BigDecimal interest = ((principalAmount.multiply(daysFactor, mathContext)).multiply(rate, mathContext))
				.divide(BigDecimal.valueOf(100));
		return interest;
	}

	public static BigDecimal calInterestWithDaysFactor(BigDecimal daysFactor, BigDecimal principalAmount,
			BigDecimal rate) {
		/*
		 * interest= (Principal * Days Factor * Rate)/100
		 */
		MathContext mathContext = new MathContext(0);
		BigDecimal interest = ((principalAmount.multiply(daysFactor, mathContext)).multiply(rate, mathContext))
				.divide(BigDecimal.valueOf(100));
		return interest;
	}

	public static int calNoOfDays(Date dtStart, Date dtEnd, String strDaysBasis) {
		strDaysBasis = StringUtils.trimToEmpty(strDaysBasis);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(dtStart);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(dtEnd);

		if (startCalendar.compareTo(endCalendar) == 0) {
			return 0;
		}

		if (startCalendar.after(endCalendar)) {
			Calendar tempCalendar = startCalendar;
			startCalendar = (Calendar) endCalendar.clone();
			endCalendar = tempCalendar;
		}

		int dayOfStart = startCalendar.get(Calendar.DAY_OF_MONTH);
		int dayOfEnd = endCalendar.get(Calendar.DAY_OF_MONTH);
		int monthOfStart = startCalendar.get(Calendar.MONTH);
		int monthOfEnd = endCalendar.get(Calendar.MONTH);
		int yearOfStart = startCalendar.get(Calendar.YEAR);
		int yearOfEnd = endCalendar.get(Calendar.YEAR);

		if (strDaysBasis.equals(CalculationConstants.IDB_30U360)) {

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

			return 360 * (yearOfEnd - yearOfStart) + (30 * (monthOfEnd - monthOfStart)) + (dayOfEnd - dayOfStart);

		} else if (strDaysBasis.equals(CalculationConstants.IDB_30E360)) {
			if (dayOfEnd == 31) {
				dayOfEnd = 30;
			}

			if (dayOfStart == 31) {
				dayOfStart = 30;
			}

			return 360 * (yearOfEnd - yearOfStart) + (30 * (monthOfEnd - monthOfStart)) + (dayOfEnd - dayOfStart);

		} else if (strDaysBasis.equals(CalculationConstants.IDB_30E360I)) {
			return calNumberOfDays30E360ISDA(dayOfStart, dayOfEnd, monthOfStart, monthOfEnd, yearOfStart, yearOfEnd);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_30EP360)) {
			if (dayOfStart == 31) {
				dayOfStart = 30;
			}

			if (dayOfEnd == 31) {
				monthOfEnd = monthOfEnd + 1;
				dayOfEnd = 1;
			}

			return 360 * (yearOfEnd - yearOfStart) + (30 * (monthOfEnd - monthOfStart)) + (dayOfEnd - dayOfStart);

		} else if (strDaysBasis.equals(CalculationConstants.IDB_ACT_ISDA)) {
			// return getIDB_ACT_ISDA(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_ACT_365FIXED)
				|| strDaysBasis.equals(CalculationConstants.IDB_ACT_360)
				|| strDaysBasis.equals(CalculationConstants.IDB_ACT_365LEAP)
				|| strDaysBasis.equals(CalculationConstants.IDB_ACT_365LEAPS)) {
			return (int) getDaysBetween(startCalendar, endCalendar);
		} else if (strDaysBasis.equals(CalculationConstants.IDB_BY_PERIOD)) {
			double daysInMonth = 30d;

			int days = calNumberOfDays30E360ISDA(dayOfStart, dayOfEnd, monthOfStart, monthOfEnd, yearOfStart,
					yearOfEnd);
			double noOfmonths = days / daysInMonth;

			noOfmonths = Math.ceil(noOfmonths);

			/*
			 * if (startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) &&
			 * startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)) { noOfmonths = noOfmonths + 1; }
			 */

			int numberOfDays = (int) (noOfmonths * 30);

			return numberOfDays;
		}

		return 0;
	}

	private static int calNumberOfDays30E360ISDA(int dayOfStart, int dayOfEnd, int monthOfStart, int monthOfEnd,
			int yearOfStart, int yearOfEnd) {
		boolean isFebSetReq = true;
		if (yearOfStart == yearOfEnd && (monthOfStart == Calendar.FEBRUARY || monthOfEnd == Calendar.FEBRUARY)) {
			if (DateUtil.isLeapYear(yearOfStart)) {
				if (dayOfStart == 29 && dayOfEnd == 29) {
					isFebSetReq = false;
				}
			} else {
				if ((dayOfStart == 28 || dayOfStart == 29) && dayOfEnd == 28) {
					isFebSetReq = false;
				}
			}
		}

		if (isFebSetReq && monthOfStart == Calendar.FEBRUARY) {
			if (DateUtil.isLeapYear(yearOfStart)) {
				if (dayOfStart > 28) {
					dayOfStart = 30;
				}
			} else {
				if (dayOfStart > 27) {
					dayOfStart = 30;
				}
			}
		}

		if (isFebSetReq && monthOfEnd == Calendar.FEBRUARY) {

			if (DateUtil.isLeapYear(yearOfEnd)) {
				if (dayOfEnd > 28) {
					dayOfEnd = 30;
				}
			} else {
				if (dayOfEnd > 27) {
					dayOfEnd = 30;
				}
			}
		}

		if (dayOfEnd == 31) {
			dayOfEnd = 30;
		}

		if (dayOfStart == 31) {
			dayOfStart = 30;
		}
		return 360 * (yearOfEnd - yearOfStart) + (30 * (monthOfEnd - monthOfStart)) + (dayOfEnd - dayOfStart);
	}

	public static BigDecimal calInstallment(BigDecimal principle, BigDecimal rate, String paymentFrequency,
			int noOfTerms) {
		/*
		 * M = P(1+r)n r / [(1+r)n-1] r=rate/100*frequency
		 */
		int frqequency = getTermsPerYear(paymentFrequency);

		if (rate.compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal r = rate.divide(new BigDecimal(100).multiply(new BigDecimal(frqequency)), 10,
					RoundingMode.HALF_DOWN);
			BigDecimal nTimesOfr = (r.add(BigDecimal.ONE)).pow(noOfTerms);
			BigDecimal numerator = principle.multiply(nTimesOfr).multiply(r);
			BigDecimal denominator = nTimesOfr.subtract(BigDecimal.ONE);
			return numerator.divide(denominator, 10, RoundingMode.HALF_DOWN);
		} else {
			return principle.divide(BigDecimal.valueOf(noOfTerms), 10, RoundingMode.HALF_DOWN);
		}

	}

	public static BigDecimal getConvertedAmount(Currency fromCurrency, Currency toCurrency, BigDecimal actualAmount) {

		BigDecimal buyRate = BigDecimal.ZERO;
		BigDecimal sellRate = BigDecimal.ZERO;

		if (fromCurrency.isCcyIsReceprocal()) {
			buyRate = BigDecimal.ONE.divide(fromCurrency.getCcySpotRate(), 9, RoundingMode.HALF_DOWN);
		} else {
			buyRate = fromCurrency.getCcySpotRate();
		}

		if (toCurrency.isCcyIsReceprocal()) {
			sellRate = BigDecimal.ONE.divide(toCurrency.getCcySpotRate(), 9, RoundingMode.HALF_DOWN);
		} else {
			sellRate = toCurrency.getCcySpotRate();
		}

		actualAmount = (actualAmount.multiply(sellRate).multiply(toCurrency.getCcyMinorCcyUnits()))
				.divide(buyRate.multiply(fromCurrency.getCcyMinorCcyUnits()), 0, RoundingMode.HALF_DOWN);
		actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);

		return actualAmount;

	}

	/**
	 * Convert Amount based on the exchange rates
	 * 
	 * @param entityCode
	 * @param fromCcy
	 * @param toCcy
	 * @param amount
	 * @return
	 */
	public static BigDecimal getConvertedAmount(final String fromCcy, final String toCcy, BigDecimal actualAmount) {

		if (actualAmount.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		String fromCcyCode = fromCcy;
		String toCcyCode = toCcy;

		String localCcy = "";
		if (ImplementationConstants.ALLOW_MULTI_CCY) {
			localCcy = SysParamUtil.getAppCurrency();
		} else {
			localCcy = ImplementationConstants.BASE_CCY;
		}

		if (fromCcyCode == null) {
			fromCcyCode = localCcy;
		}
		if (toCcyCode == null) {
			toCcyCode = localCcy;
		}

		if (fromCcyCode.equals(toCcyCode)) {
			return actualAmount;
		}

		Currency fromCurrency = null;
		Currency toCurrency = null;
		List<Currency> currencyList = currencyDAO.getCurrencyList(Arrays.asList(fromCcyCode, toCcyCode));
		for (Currency currency : currencyList) {
			if (currency.getCcyCode().equals(fromCcyCode)) {
				fromCurrency = currency;
			} else {
				toCurrency = currency;
			}
		}

		return getConvertedAmount(fromCurrency, toCurrency, actualAmount);

	}

	public static String getConvertedAmountASString(final String fromCcyCode, final String toCcyCode,
			BigDecimal actualAmount) {

		if (actualAmount.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO.toString();
		}

		String fromCcy = fromCcyCode;
		String toCcy = toCcyCode;

		// Base Currency
		String localCcy = SysParamUtil.getAppCurrency();
		if (fromCcy == null) {
			fromCcy = localCcy;
		}
		if (toCcy == null) {
			toCcy = localCcy;
		}

		Currency fromCurrency = new Currency();
		Currency toCurrency = new Currency();
		List<Currency> currencyList = currencyDAO.getCurrencyList(Arrays.asList(fromCcy, toCcy));
		for (Currency currency : currencyList) {
			if (currency.getCcyCode().equals(fromCcy)) {
				fromCurrency = currency;
			} else {
				toCurrency = currency;
			}
		}

		if (fromCcy.equals(toCcy)) {
			return PennantApplicationUtil.amountFormate(actualAmount, fromCurrency.getCcyEditField());
		}

		BigDecimal bigDecimal = getConvertedAmount(fromCurrency, toCurrency, actualAmount);
		return PennantApplicationUtil.amountFormate(bigDecimal, toCurrency.getCcyEditField());

	}

	public static String convertedUnFormatAmount(final String fromCcyCode, final String toCcyCode,
			BigDecimal actualAmount) {

		if (actualAmount.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO.toString();
		}

		String fromCcy = fromCcyCode;
		String toCcy = toCcyCode;

		// Base Currency
		String localCcy = SysParamUtil.getAppCurrency();

		if (fromCcy == null || toCcy == null) {
			localCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		}

		if (fromCcy == null) {
			fromCcy = localCcy;
		}
		if (toCcy == null) {
			toCcy = localCcy;
		}

		if (fromCcy.equals(toCcy)) {
			return actualAmount.toString();
		}

		Currency fromCurrency = null;
		Currency toCurrency = null;
		List<Currency> currencyList = currencyDAO.getCurrencyList(Arrays.asList(fromCcy, toCcy));
		for (Currency currency : currencyList) {
			if (currency.getCcyCode().equals(fromCcy)) {
				fromCurrency = currency;
			} else {
				toCurrency = currency;
			}
		}
		BigDecimal bigDecimal = getConvertedAmount(fromCurrency, toCurrency, actualAmount);
		return bigDecimal.toString();

	}

	/**
	 * calculate average profit rate [avgProfitRate = (profitAmt * 100)/(Days Factor * principalAmt )]
	 * 
	 * @param (Date)       startDate
	 * @param (Date)       maturityDate
	 * @param (String)     profitDaysBasis
	 * @param (BigDecimal) principalAmt
	 * @param (BigDecimal) maturityAmount
	 * @return(BigDecimal) avgProfitRate
	 */
	public static BigDecimal calcAvgProfitRate(Date startDate, Date maturityDate, String profitDaysBasis,
			final BigDecimal reqPrincipalAmt, BigDecimal maturityAmount) {

		BigDecimal avgProfitRate = BigDecimal.ZERO;
		BigDecimal profitAmt = BigDecimal.ZERO;
		BigDecimal principalAmt = reqPrincipalAmt;

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

	public static BigDecimal getExchangeRate(final String fromCcyCode, final String toCcyCode) {

		BigDecimal buyRate = BigDecimal.ZERO;
		BigDecimal sellRate = BigDecimal.ZERO;

		String fromCcy = fromCcyCode;
		String toCcy = toCcyCode;

		// Base Currency
		String localCcy = SysParamUtil.getAppCurrency();
		if (fromCcy == null) {
			fromCcy = localCcy;
		}
		if (toCcy == null) {
			toCcy = localCcy;
		}

		if (fromCcy.equals(toCcy)) {
			return BigDecimal.ONE;
		}

		Currency toCurrency = null;
		Currency fromCurrency = null;
		List<Currency> currencyList = currencyDAO.getCurrencyList(Arrays.asList(fromCcy, toCcy));
		for (Currency currency : currencyList) {
			if (currency.getCcyCode().equals(fromCcy)) {
				fromCurrency = currency;
			} else {
				toCurrency = currency;
			}
		}

		if (fromCurrency.isCcyIsReceprocal()) {
			buyRate = BigDecimal.ONE.divide(fromCurrency.getCcySpotRate(), 9, RoundingMode.HALF_DOWN);
		} else {
			buyRate = fromCurrency.getCcySpotRate();
		}

		if (toCurrency.isCcyIsReceprocal()) {
			sellRate = BigDecimal.ONE.divide(toCurrency.getCcySpotRate(), 9, RoundingMode.HALF_DOWN);
		} else {
			sellRate = toCurrency.getCcySpotRate();
		}

		return (sellRate).divide(buyRate, 9, RoundingMode.HALF_DOWN);
	}

	/**
	 * This method returns the anualizedPercRate using the below values:
	 * 
	 * @param finAmount     The Finance Amount
	 * @param downPayment   The Down Payment Amount
	 * @param repayPftFrq   The Repay Profit Frequency
	 * @param numberOfTerms No of Terms
	 * @param totalProfit   Total Profit Amount
	 * @return The anualizedPercRate value for the above parameters
	 */
	public static BigDecimal calulateAunalizedPercRate(BigDecimal finAmount, BigDecimal downPayment, String repayPftFrq,
			int numberOfTerms, BigDecimal totalProfit) {

		BigDecimal anualizedPercRate = BigDecimal.ZERO;
		if (finAmount.compareTo(BigDecimal.ZERO) == 0) {
			return anualizedPercRate;
		}
		anualizedPercRate = new BigDecimal(getTermsPerYear(repayPftFrq))
				.multiply((new BigDecimal("95").multiply(new BigDecimal(numberOfTerms)).add(new BigDecimal("9")))
						.multiply(totalProfit).multiply(new BigDecimal(100)))
				.divide((new BigDecimal("12").multiply(new BigDecimal(numberOfTerms))
						.multiply(new BigDecimal(numberOfTerms).add(new BigDecimal("1"))))
								.multiply(new BigDecimal("4").multiply(finAmount.subtract(downPayment) // downPayment is
																										// subtracted
																										// (###
																										// 28-11-2016 -
																										// PSD Ticket ID
																										// 124367)
								).add(totalProfit)), 2, RoundingMode.HALF_DOWN);
		return anualizedPercRate;
	}

	/**
	 * This method takes the paymentFrequency as "M0031".. and returns the number of terms in one year
	 * 
	 * @param Paymentfrequency
	 * @return the number of terms in one year as int value
	 */

	public static int getTermsPerYear(String paymentFrequency) {

		int frqequency = 0;
		switch (FrequencyUtil.getFrequencyCode(paymentFrequency).charAt(0)) {
		case 'D':
			frqequency = CalculationConstants.FRQ_DAILY;
			break;
		case 'W':
			frqequency = CalculationConstants.FRQ_WEEKLY;
			break;
		case 'F':
			frqequency = CalculationConstants.FRQ_FORTNIGHTLY;
			break;
		case 'M':
			frqequency = CalculationConstants.FRQ_MONTHLY;
			break;
		case 'Q':
			frqequency = CalculationConstants.FRQ_QUARTERLY;
			break;
		case 'H':
			frqequency = CalculationConstants.FRQ_HALF_YEARLY;
			break;
		case 'Y':
			frqequency = CalculationConstants.FRQ_YEARLY;
			break;
		default:
			break;
		}
		return frqequency;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		CalculationUtil.currencyDAO = currencyDAO;
	}

	public static BigDecimal roundAmount(BigDecimal amount, String roundingMode, int roundingTarget) {
		// Since we are getting more than 9 decimals,As per the product we are considering the 9 decimals only.
		amount = amount.setScale(9, RoundingMode.HALF_UP);

		if (StringUtils.isBlank(roundingMode)) {
			roundingMode = RoundingMode.HALF_DOWN.name();
		}

		if (roundingTarget == 0) {
			amount = amount.setScale(0, RoundingMode.HALF_DOWN);
			return amount;
		}

		BigDecimal bdRoundTarget = BigDecimal.valueOf(roundingTarget);

		amount = amount.divide(bdRoundTarget);
		roundingMode = StringUtils.trimToEmpty(roundingMode);

		if (StringUtils.equals(roundingMode, RoundingMode.HALF_DOWN.name())) {
			amount = amount.setScale(0, RoundingMode.HALF_DOWN);
		} else if (StringUtils.equals(roundingMode, RoundingMode.HALF_EVEN.name())) {
			amount = amount.setScale(0, RoundingMode.HALF_EVEN);
		} else if (StringUtils.equals(roundingMode, RoundingMode.HALF_UP.name())) {
			amount = amount.setScale(0, RoundingMode.HALF_UP);
		} else if (StringUtils.equals(roundingMode, RoundingMode.CEILING.name())) {
			amount = amount.setScale(0, RoundingMode.CEILING);
		} else if (StringUtils.equals(roundingMode, RoundingMode.DOWN.name())) {
			amount = amount.setScale(0, RoundingMode.DOWN);
		} else if (StringUtils.equals(roundingMode, RoundingMode.FLOOR.name())) {
			amount = amount.setScale(0, RoundingMode.FLOOR);
		} else {
			amount = amount.setScale(0, RoundingMode.UP);
		}

		amount = amount.multiply(BigDecimal.valueOf(roundingTarget));

		return amount;
	}

	// This formula applicable only for 30/360 with Fixed periods only
	public static BigDecimal calDepositPV(BigDecimal fv, BigDecimal intRate, int terms, String frq, String roundingMode,
			int roundingTarget) {
		BigDecimal pv = BigDecimal.ZERO;

		// Future Value is ZERO
		if (fv.compareTo(BigDecimal.ZERO) == 0) {
			return pv;
		}

		// Interest Rate = 0
		if (intRate.compareTo(BigDecimal.ZERO) == 0) {
			pv = roundAmount(fv, roundingMode, roundingTarget);
			return pv;
		}

		BigDecimal termsPerYear = BigDecimal.valueOf(getTermsPerYear(frq));
		intRate = intRate.divide(BigDecimal.valueOf(100));
		intRate = intRate.divide(termsPerYear, 13, RoundingMode.HALF_DOWN);

		double dIntRate = intRate.doubleValue();
		double dFV = fv.doubleValue();
		double dPV = dFV / (Math.pow((1 + dIntRate), terms));
		pv = BigDecimal.valueOf(dPV);
		pv = roundAmount(pv, roundingMode, roundingTarget);
		return pv;
	}

	// This formula applicable only for 30/360 with Fixed periods only
	public static BigDecimal calLoanPVAdvance(BigDecimal intRate, int terms, BigDecimal pmt, BigDecimal fv, int type,
			String frq, String roundingMode, int roundingTarget) {
		BigDecimal pv = BigDecimal.ZERO;

		// Type 0: Arrears and 1: Advance
		if (type != 0) {
			type = 1;
		}

		// Interest Rate = 0
		if (intRate.compareTo(BigDecimal.ZERO) == 0) {
			pv = pmt.multiply(BigDecimal.valueOf(terms));
			pv = pv.subtract(fv);
			pv = roundAmount(pv, roundingMode, roundingTarget);
			return pv;
		}

		BigDecimal termsPerYear = BigDecimal.valueOf(getTermsPerYear(frq));
		intRate = intRate.divide(BigDecimal.valueOf(100));
		intRate = intRate.divide(termsPerYear, 13, RoundingMode.HALF_DOWN);

		double dIntRate = intRate.doubleValue();
		double dFV = fv.doubleValue();
		double dPmt = pmt.doubleValue();
		double dPV = (((1 - Math.pow(1 + dIntRate, terms)) / dIntRate) * dPmt * (1 + dIntRate * type) - dFV)
				/ Math.pow(1 + dIntRate, terms);

		dPV = dPV * (-1);
		pv = BigDecimal.valueOf(dPV);
		pv = roundAmount(pv, roundingMode, roundingTarget);
		return pv;
	}

	public static BigDecimal calLoanPV(BigDecimal intRate, int terms, BigDecimal pmt, String frq, String roundingMode,
			int roundingTarget) {
		BigDecimal pv = BigDecimal.ZERO;

		// Interest Rate = 0
		if (intRate.compareTo(BigDecimal.ZERO) == 0) {
			pv = pmt.multiply(BigDecimal.valueOf(terms));
			pv = roundAmount(pv, roundingMode, roundingTarget);
			return pv;
		}

		BigDecimal termsPerYear = BigDecimal.valueOf(getTermsPerYear(frq));
		intRate = intRate.divide(BigDecimal.valueOf(100));
		intRate = intRate.divide(termsPerYear, 13, RoundingMode.HALF_DOWN);

		double dIntRate = intRate.doubleValue();
		double dPmt = pmt.doubleValue();
		double dPV = dPmt / dIntRate * (1 - Math.pow(1 + dIntRate, -terms));

		pv = BigDecimal.valueOf(dPV);
		pv = roundAmount(pv, roundingMode, roundingTarget);
		return pv;
	}

	public static BigDecimal getTotalGST(FinTaxIncomeDetail taxIncome) {
		BigDecimal totoGSTAmount = BigDecimal.ZERO;
		totoGSTAmount = totoGSTAmount.add(taxIncome.getCGST());
		totoGSTAmount = totoGSTAmount.add(taxIncome.getSGST());
		totoGSTAmount = totoGSTAmount.add(taxIncome.getUGST());
		totoGSTAmount = totoGSTAmount.add(taxIncome.getIGST());
		totoGSTAmount = totoGSTAmount.add(taxIncome.getCESS());

		return totoGSTAmount;
	}

	public static BigDecimal getTotalPaidGST(ManualAdvise manualAdvise) {
		BigDecimal totPaidGSTAmount = BigDecimal.ZERO;
		totPaidGSTAmount = totPaidGSTAmount.add(manualAdvise.getPaidCGST());
		totPaidGSTAmount = totPaidGSTAmount.add(manualAdvise.getPaidSGST());
		totPaidGSTAmount = totPaidGSTAmount.add(manualAdvise.getPaidUGST());
		totPaidGSTAmount = totPaidGSTAmount.add(manualAdvise.getPaidIGST());
		totPaidGSTAmount = totPaidGSTAmount.add(manualAdvise.getPaidCESS());

		return totPaidGSTAmount;
	}

	public static BigDecimal getTotalWaivedGST(ManualAdvise manualAdvise) {
		BigDecimal totWaivedGSTAmount = BigDecimal.ZERO;
		totWaivedGSTAmount = totWaivedGSTAmount.add(manualAdvise.getWaivedCGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(manualAdvise.getWaivedSGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(manualAdvise.getWaivedUGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(manualAdvise.getWaivedIGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(manualAdvise.getWaivedCESS());

		return totWaivedGSTAmount;
	}

	public static BigDecimal getTotalPaidGST(ReceiptAllocationDetail rad) {
		BigDecimal totPaidGSTAmount = BigDecimal.ZERO;
		totPaidGSTAmount = totPaidGSTAmount.add(rad.getPaidCGST());
		totPaidGSTAmount = totPaidGSTAmount.add(rad.getPaidSGST());
		totPaidGSTAmount = totPaidGSTAmount.add(rad.getPaidUGST());
		totPaidGSTAmount = totPaidGSTAmount.add(rad.getPaidIGST());
		totPaidGSTAmount = totPaidGSTAmount.add(rad.getPaidCESS());

		return totPaidGSTAmount;
	}

	public static BigDecimal getTotalWaivedGST(ReceiptAllocationDetail rad) {
		BigDecimal totWaivedGSTAmount = BigDecimal.ZERO;
		totWaivedGSTAmount = totWaivedGSTAmount.add(rad.getWaivedCGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(rad.getWaivedSGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(rad.getWaivedUGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(rad.getWaivedIGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(rad.getWaivedCESS());

		return totWaivedGSTAmount;
	}

	public static BigDecimal getTotalGSTPerc(ReceiptAllocationDetail rad) {
		BigDecimal totalPerc = BigDecimal.ZERO;
		totalPerc = totalPerc.add(rad.getPercCGST());
		totalPerc = totalPerc.add(rad.getPercSGST());
		totalPerc = totalPerc.add(rad.getPercUGST());
		totalPerc = totalPerc.add(rad.getPercIGST());
		totalPerc = totalPerc.add(rad.getPercCESS());

		return totalPerc;
	}

	public static BigDecimal getTotalPaidGST(ManualAdviseMovements mam) {
		BigDecimal totPaidGSTAmount = BigDecimal.ZERO;
		totPaidGSTAmount = totPaidGSTAmount.add(mam.getPaidCGST());
		totPaidGSTAmount = totPaidGSTAmount.add(mam.getPaidSGST());
		totPaidGSTAmount = totPaidGSTAmount.add(mam.getPaidUGST());
		totPaidGSTAmount = totPaidGSTAmount.add(mam.getPaidIGST());
		totPaidGSTAmount = totPaidGSTAmount.add(mam.getPaidCESS());

		return totPaidGSTAmount;
	}

	public static BigDecimal getTotalWaivedGST(ManualAdviseMovements mam) {
		BigDecimal totWaivedGSTAmount = BigDecimal.ZERO;
		totWaivedGSTAmount = totWaivedGSTAmount.add(mam.getWaivedCGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(mam.getWaivedSGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(mam.getWaivedUGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(mam.getWaivedIGST());
		totWaivedGSTAmount = totWaivedGSTAmount.add(mam.getWaivedCESS());

		return totWaivedGSTAmount;
	}

	public static BigDecimal getTotalGST(AdviseDueTaxDetail detail) {
		BigDecimal totPaidGSTAmount = BigDecimal.ZERO;
		totPaidGSTAmount = totPaidGSTAmount.add(detail.getCGST());
		totPaidGSTAmount = totPaidGSTAmount.add(detail.getSGST());
		totPaidGSTAmount = totPaidGSTAmount.add(detail.getUGST());
		totPaidGSTAmount = totPaidGSTAmount.add(detail.getIGST());
		totPaidGSTAmount = totPaidGSTAmount.add(detail.getCESS());

		return totPaidGSTAmount;
	}

	public static BigDecimal getPenaltyBalance(FinODDetails od) {
		BigDecimal penaltyBalance = BigDecimal.ZERO;
		BigDecimal lpiBalance = BigDecimal.ZERO;

		BigDecimal peanltyAmt = od.getTotPenaltyAmt();
		BigDecimal peanltyReceived = BigDecimal.ZERO;
		peanltyReceived = peanltyReceived.add(od.getTotPenaltyPaid());
		peanltyReceived = peanltyReceived.add(od.getTotWaived());

		BigDecimal lpiAmt = od.getLPIAmt();
		BigDecimal lpiReceived = BigDecimal.ZERO;
		lpiReceived = lpiReceived.add(od.getLPIPaid());
		lpiReceived = lpiReceived.add(od.getLPIWaived());

		penaltyBalance = penaltyBalance.add(peanltyAmt);
		penaltyBalance = penaltyBalance.subtract(peanltyReceived);

		lpiBalance = lpiBalance.add(lpiAmt);
		lpiBalance = lpiBalance.subtract(lpiReceived);

		return penaltyBalance.add(lpiBalance);

	}

	private static long getDaysBetween(Calendar startCalendar, Calendar endCalendar) {
		return Math
				.round((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / (1000d * 60d * 60d * 24d));
	}

	public static void setODTotals(FinODDetails fod) {
		BigDecimal totPenaltyBal = fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid())
				.subtract(fod.getTotWaived());
		if (totPenaltyBal.compareTo(BigDecimal.ZERO) >= 0) {
			fod.setTotPenaltyBal(totPenaltyBal);
			fod.setPayableAmount(BigDecimal.ZERO);
		} else {
			fod.setTotPenaltyBal(BigDecimal.ZERO);
			fod.setPayableAmount(fod.getTotPenaltyPaid().subtract(fod.getTotWaived()).subtract(fod.getTotPenaltyAmt()));
		}
	}
}
