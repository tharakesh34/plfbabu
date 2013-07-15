package com.pennant.backend.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class PennantApplicationUtil {

	
	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {

		if (amount == null) {
			return new BigDecimal(0);
		}
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}
	
	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = new BigDecimal(0);

		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}
		return bigDecimal;
	}

	public static String amountFormate(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = new BigDecimal(0);
		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}

		return formatAmount(bigDecimal, dec, false);
	}

	public static String formatAmount(BigDecimal value, int decPos, boolean debitCreditSymbol) {

		if (value != null && value.compareTo(new BigDecimal("0")) != 0) {
			DecimalFormat df = new DecimalFormat();
			StringBuffer sb = new StringBuffer("###,###,###,###");
			boolean negSign = false;

			if (decPos > 0) {
				sb.append('.');
				for (int i = 0; i < decPos; i++) {
					sb.append('0');
				}

				if (value.compareTo(new BigDecimal("0")) == -1) {
					negSign = true;
					value = value.multiply(new BigDecimal("-1"));
				}

				if (negSign) {
					value = value.multiply(new BigDecimal("-1"));
				}
			}

			if (debitCreditSymbol) {
				String s = sb.toString();
				sb.append(" 'Cr';").append(s).append(" 'Dr'");
			}

			df.applyPattern(sb.toString());
			return df.format(value).toString();
		} else {
			String string = "0.";
			for (int i = 0; i < decPos; i++) {
				string += "0";
			}
			return string;
		}
	}
	
	public static String getAmountFormate(int dec) {
		String formateString = PennantConstants.defaultAmountFormate;

		switch (dec) {
		case 0:
			formateString = PennantConstants.amountFormate0;
			break;
		case 1:
			formateString = PennantConstants.amountFormate1;
			break;
		case 2:
			formateString = PennantConstants.amountFormate2;
			break;
		case 3:
			formateString = PennantConstants.amountFormate3;
			break;
		case 4:
			formateString = PennantConstants.amountFormate4;
			break;
		}
		return formateString;
	}
	
	public static String getRateFormate(int dec) {
		String formateString = PennantConstants.rateFormate2;
		
		switch (dec) {
		case 2:
			formateString = PennantConstants.rateFormate2;
			break;
		case 3:
			formateString = PennantConstants.rateFormate3;
			break;
		case 4:
			formateString = PennantConstants.rateFormate4;
			break;
		case 5:
			formateString = PennantConstants.rateFormate5;
			break;
		case 6:
			formateString = PennantConstants.rateFormate6;
			break;
		case 7:
			formateString = PennantConstants.rateFormate7;
			break;
		case 8:
			formateString = PennantConstants.rateFormate8;
			break;
		case 9:
			formateString = PennantConstants.rateFormate9;
			break;
		case 10:
			formateString = PennantConstants.rateFormate10;
			break;
		}
		return formateString;
	}
	
	public static String formatRate(double value, int decPos) {
		StringBuffer sb = new StringBuffer("###,###,###,###");

		if (decPos > 0) {
			sb.append('.');
			for (int i = 0; i < decPos; i++) {
				sb.append('0');
			}
		}
		if (value != 0) {
			java.text.DecimalFormat df = new java.text.DecimalFormat();
			df.applyPattern(sb.toString());
			return df.format(value).toString();
		} else {
			String string = "0.";
			for (int i = 0; i < decPos; i++) {
				string += "0";
			}
			return string;

		}
	}

	public static String formateLong(long longValue) {
		StringBuffer sb = new StringBuffer("###,###,###,###");
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(longValue).toString();
	}

	public static String formateInt(int intValue) {

		StringBuffer sb = new StringBuffer("###,###,###,###");
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(intValue).toString();
	}

	public static String formateBoolean(int intValue) {

		if(intValue == 1){
			return String.valueOf(true);
		} else {
			return String.valueOf(false);
		}
	}
	
	public static String getCcyFormate(int minCCY) {
		String formateString = PennantConstants.defaultAmountFormate;
		formateString = getAmountFormate(getCcyDec(minCCY));
		return formateString;
	}

	public static int getCcyDec(int minCCY) {
		int decPos = PennantConstants.defaultCCYDecPos;
		switch (minCCY) {
		case 1:
			decPos = 0;
			break;
		case 10:
			decPos = 1;
			break;
		case 100:
			decPos = 2;
			break;
		case 1000:
			decPos = 3;
			break;
		case 10000:
			decPos = 4;
			break;
		}
		return decPos;
	}

	public static String formateDate(Date date, String dateFormate) {
		String formatedDate = null;
		if (StringUtils.trimToEmpty(dateFormate).equals("")) {
			dateFormate = PennantConstants.dateFormat;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormate);

		if (date != null) {
			formatedDate = formatter.format(date);
		}

		return formatedDate;

	}

	public static Date getDate(Timestamp timestamp) {
		Date date = null;
		if (timestamp != null) {
			date = new Date(timestamp.getTime());
		}
		return date;
	}

	public static Timestamp getTimestamp(Date date) {
		Timestamp timestamp = null;

		if (date != null) {
			timestamp = new Timestamp(date.getTime());
		}
		return timestamp;
	}

	public static Time getTime(Date date) {
		Time time = null;

		if (date != null) {
			time = new Time(date.getTime());
		}
		return time;
	}
}
