package com.pennanttech.external.app.util;

import java.math.BigDecimal;
import java.util.Date;

public class ParseUtil {
	public static Date getDateItem(String[] lineDataStrings, int position) {
		try {
			if (lineDataStrings.length >= position) {
				return new Date(lineDataStrings[position - 1]);
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static BigDecimal getBigDecimalItem(String[] lineDataStrings, int position) {
		if (lineDataStrings.length >= position) {
			if (!"".equals(lineDataStrings[position - 1])) {
				return new BigDecimal(lineDataStrings[position - 1]);
			}
		}
		return new BigDecimal(0);
	}

	public static String getItem(String[] lineDataStrings, int position) {
		if (lineDataStrings.length >= position) {
			return lineDataStrings[(position - 1)];
		}
		return "";
	}

	public static long getLongItem(String[] lineDataStrings, int position) {
		if (lineDataStrings.length >= position) {
			if (!"".equals(lineDataStrings[position - 1])) {
				return Long.parseLong(lineDataStrings[position - 1]);
			}
		}
		return 0L;
	}
}
