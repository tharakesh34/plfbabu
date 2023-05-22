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
 * FileName : SysParamUtil.java *
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.PennantConstants;

/**
 * A suite of utilities surrounding the use of the Currency that contain information about the environment for the
 * system.
 */
public class CurrencyUtil {
	private static final String AMOUNT_FORMAT = "###,###,###,###";
	private static CurrencyService currencyService;

	/**
	 * Method for get the Record Data of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static Currency getCurrencyObject(String ccy) {
		return getCurrency(ccy);
	}

	/**
	 * Method for get minimum currency unit's of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static int getFormat(String ccy) {

		if (!ImplementationConstants.ALLOW_MULTI_CCY) {
			return ImplementationConstants.BASE_CCY_EDT_FIELD;
		}

		if (StringUtils.equals(ImplementationConstants.BASE_CCY, ccy)) {
			return ImplementationConstants.BASE_CCY_EDT_FIELD;
		}

		if (StringUtils.isEmpty(ccy)) {
			ccy = SysParamUtil.getAppCurrency();
		}

		Currency currecny = getCurrency(ccy);
		if (currecny != null) {
			return currecny.getCcyEditField();
		}
		return 0;
	}

	/**
	 * Method for get Currency Object Description for Display
	 * 
	 * @param parmCode
	 * @return
	 */
	public static String getCcyDesc(String ccy) {

		if (StringUtils.isEmpty(ccy)) {
			ccy = SysParamUtil.getAppCurrency();
		}

		Currency currecny = getCurrency(ccy);
		if (currecny != null) {
			return currecny.getCcyDesc();
		}
		return "";
	}

	/**
	 * Method for get minimum currency unit's of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static String getCcyNumber(String ccy) {
		Currency currecny = getCurrency(ccy);
		if (currecny != null) {
			return currecny.getCcyNumber();
		}
		return "";
	}

	/**
	 * Method for get the Exchange rate of Currency
	 * 
	 * @param parmCode
	 * @return
	 */
	public static BigDecimal getExChangeRate(String ccy) {

		if (!ImplementationConstants.ALLOW_MULTI_CCY) {
			return BigDecimal.ONE;
		}

		if (StringUtils.equals(ImplementationConstants.BASE_CCY, ccy)) {
			return BigDecimal.ONE;
		}

		Currency currecny = getCurrency(ccy);
		if (currecny != null) {
			return currecny.getCcySpotRate();
		}
		return BigDecimal.ZERO;
	}

	public static Currency getCurrency(String ccy) {
		if (StringUtils.isEmpty(ccy)) {
			ccy = SysParamUtil.getAppCurrency();
		}
		return currencyService.getApprovedCurrencyById(ccy);
	}

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		CurrencyUtil.currencyService = currencyService;
	}

	/**
	 * Translates the {@link String} representation of a {@link BigDecimal} into a {@link BigDecimal}
	 * 
	 * @param value The {@link String} representation value which needs to be Translate into {@link BigDecimal}.
	 * @return A {@link BigDecimal} or <code>BigDecimal.ZEOR</code> if input value is <code>null</code>.
	 * 
	 * @throws NumberFormatException if value is not a valid representation of a {@link BigDecimal}
	 * 
	 */
	public static BigDecimal getBigDecimal(String value) {
		value = StringUtils.trimToNull(value);
		return value == null ? BigDecimal.ZERO : new BigDecimal(value);
	}

	/**
	 * Translates the {@link BigDecimal} representation of a {@link String} into a {@link String}
	 * 
	 * @param value The {@link BigDecimal} representation value which needs to be Translate to {@link String} .
	 * @return A corresponding {@link String} value or <code>0</code> if input value is <code>null</code>.
	 * 
	 */
	public static String getString(BigDecimal value) {
		return value == null ? BigDecimal.ZERO.toString() : value.toString();
	}

	/**
	 * Format the amount from major to minor currency.
	 * 
	 * <p>
	 * Examples
	 * <li>100000 to 1,000.00
	 * 
	 * <li>10000050 to 100,000.50
	 * 
	 * @param amount   The amount which needs to be format from major to minor
	 * @param decimals The number of decimal positions.
	 * @return The formated amount in {@link String} representation
	 */
	public static String format(BigDecimal amount) {
		return format(amount, PennantConstants.defaultCCYDecPos);
	}

	/**
	 * Format the amount from major to minor currency.
	 * 
	 * <p>
	 * Examples
	 * <li>100000 to 1,000.00
	 * 
	 * <li>10000050 to 100,000.50
	 * 
	 * @param amount   The amount which needs to be format from major to minor
	 * @param decimals The number of decimal positions.
	 * @return The formated amount in {@link String} representation
	 */
	public static String format(BigDecimal amount, int decimals) {
		return formatAmount(parse(amount, decimals), decimals);
	}

	/**
	 * Parse the amount from major to minor currency.
	 * 
	 * <p>
	 * Examples
	 * <li>100000 to 1000.00
	 * 
	 * <li>10000050 to 100000.50
	 * 
	 * @param amount   The amount which needs to be format from major to minor
	 * @param decimals The number of decimal positions.
	 * @return The formated amount in {@link BigDecimal} representation
	 */
	public static BigDecimal parse(BigDecimal amount, int decimals) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, decimals)));
		}
		return bigDecimal;
	}

	public static String format(String value, int decimals) {
		return format(getBigDecimal(value), decimals);
	}

	public static BigDecimal unFormat(BigDecimal amount, int dec) {
		if (amount == null) {
			return BigDecimal.ZERO;
		}

		BigInteger bigInteger = amount.multiply(BigDecimal.valueOf(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static BigDecimal unFormat(String amount, int dec) {
		if (StringUtils.isEmpty(amount) || StringUtils.isBlank(amount)) {
			return BigDecimal.ZERO;
		}
		BigInteger bigInteger = new BigDecimal(amount.replace(",", "")).multiply(BigDecimal.valueOf(Math.pow(10, dec)))
				.toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static String convertInWords(BigDecimal amount) {
		if (amount == null || amount == BigDecimal.ZERO) {
			return "";
		}

		try {
			return WordUtils.capitalize(NumberToEnglishWords.getAmountInText(amount, ""));
		} catch (Exception e) {
			//
		}
		return "";

	}

	public static String convertInWords(BigDecimal amount, int format) {
		if (amount == null || amount == BigDecimal.ZERO) {
			return "";
		}

		amount = parse(amount, format);

		try {
			return WordUtils.capitalize(NumberToEnglishWords.getAmountInText(amount, ""));
		} catch (Exception e) {
			//
		}
		return "";

	}

	public static String formatAmount(BigDecimal value, int decPos) {
		if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
			DecimalFormat df = new DecimalFormat();

			String format = "";

			if (ImplementationConstants.INDIAN_IMPLEMENTATION) {
				format = AMOUNT_FORMAT;
			} else {
				format = AMOUNT_FORMAT;
			}

			StringBuilder sb = new StringBuilder(format);
			boolean negSign = false;

			if (decPos > 0) {
				sb.append('.');
				for (int i = 0; i < decPos; i++) {
					sb.append('0');
				}

				if (value.compareTo(BigDecimal.ZERO) == -1) {
					negSign = true;
					value = value.multiply(new BigDecimal("-1"));
				}

				if (negSign) {
					value = value.multiply(new BigDecimal("-1"));
				}
			}

			df.applyPattern(sb.toString());
			String returnValue = df.format(value);
			if (returnValue.startsWith(".")) {
				returnValue = "0" + returnValue;
			}
			return returnValue;
		} else {
			String string = "0";
			if (decPos > 0) {
				string = ".";
				if (getAlwIntegralPartZero()) {
					string = "0.";
				}
				for (int i = 0; i < decPos; i++) {
					string = string.concat("0");
				}
			}
			return string;
		}
	}

	private static boolean getAlwIntegralPartZero() {
		return false;
	}
}
