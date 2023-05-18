package com.pennanttech.external.app.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AmountUtil {

	public static String convertAmount(BigDecimal amount, int ccy) {
		String newAmount = parseString(amount, ccy);
		BigDecimal amt = new BigDecimal(newAmount);
		if (ccy == 0) {
			return String.valueOf(amt);
		}
		DecimalFormat f = new DecimalFormat("##0.00");
		return f.format(amt);
	}

	private static String parseString(BigDecimal amount, int decimals) {

		return (parse(amount, decimals)).toPlainString();
	}

	private static BigDecimal parse(BigDecimal amount, int decimals) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = amount.divide(BigDecimal.valueOf(Math.pow(10, decimals)));
		}
		return bigDecimal;
	}
}
