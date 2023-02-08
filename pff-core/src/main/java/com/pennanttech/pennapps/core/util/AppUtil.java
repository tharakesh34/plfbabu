package com.pennanttech.pennapps.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.pennant.app.util.NumberToEnglishWords;

public class AppUtil {
	private static final String AMOUNT_FORMAT = "###,###,###,###";

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException If the constructor is used to create and initialize a new instance of the
	 *                                declaring class by suppressing Java language access checking.
	 */
	private AppUtil() {
		super();
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

	public static String formatAmount(String value, int decPos) {
		return formatAmount(getBigDecimal(value), decPos);
	}

	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}
		return bigDecimal;
	}

	public static String formatAmount(BigDecimal value, int decPos) {
		if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
			String string = "0";
			if (decPos > 0) {
				string = ".";
				// Integral part of a component default value requires zero or
				// not. EX: If requires, value will be like 0.00, if not, value
				// will be like .00
				if (getAlwIntegralPartZero()) {
					string = "0.";
				}
				for (int i = 0; i < decPos; i++) {
					string = string.concat("0");
				}
			}
			return string;
		}

		value = value.divide(new BigDecimal(Math.pow(10, decPos)));

		DecimalFormat df = new DecimalFormat();

		StringBuilder sb = new StringBuilder(AMOUNT_FORMAT);
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
	}

	private static boolean getAlwIntegralPartZero() {
		// TODO Auto-generated method stub
		return false;
	}

	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {
		if (amount == null) {
			return BigDecimal.ZERO;
		}

		BigInteger bigInteger = amount.multiply(BigDecimal.valueOf(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static BigDecimal unFormateAmount(String amount, int dec) {
		if (StringUtils.isEmpty(amount) || StringUtils.isBlank(amount)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(amount.replace(",", "")).multiply(BigDecimal.valueOf(Math.pow(10, dec)));
	}

	public static String getAmountInText(BigDecimal amount) {
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

	public static String getAmountInText(BigDecimal amount, int format) {
		if (amount == null || amount == BigDecimal.ZERO) {
			return "";
		}

		amount = formateAmount(amount, format);

		try {
			return WordUtils.capitalize(NumberToEnglishWords.getAmountInText(amount, ""));
		} catch (Exception e) {
			//
		}
		return "";

	}

}
