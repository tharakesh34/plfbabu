package com.pennanttech.framework.security.core;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class OTPEngine {
	private static String value;
	private static int characters;
	public static int otpValidity;

	public static String generateOTP() {
		StringBuilder builder = new StringBuilder();
		String otp = null;
		int count = characters;
		try {
			while (count-- != 0) {
				int character = (int) (Math.random() * value.length());
				builder.append(value.charAt(character));
			}
			otp = builder.toString();

		} finally {
			builder = null;
		}

		return otp;
	}

	public static String generateCID() {
		StringBuilder builder = new StringBuilder();
		String cid = null;
		int count = 15;
		String value = "0123456789";
		try {
			while (count-- != 0) {
				int character = (int) (Math.random() * value.length());
				builder.append(value.charAt(character));
			}
			cid = builder.toString();
		} finally {
			builder = null;
		}

		return cid;
	}

	public static boolean isOTPExpired(Date oldDate, Date newDate) {
		long diff = newDate.getTime() - oldDate.getTime();

		Calendar newCalendar = null;
		Calendar oldCalendar = null;
		try {

			newCalendar = new GregorianCalendar();
			oldCalendar = new GregorianCalendar();
			newCalendar.setTime(newDate);
			oldCalendar.setTime(oldDate);

			int year = oldCalendar.get(Calendar.YEAR) - newCalendar.get(Calendar.YEAR);
			int month = year * 12 + oldCalendar.get(Calendar.MONTH) - newCalendar.get(Calendar.MONTH);

			// Month
			if (month > 0) {
				return true;
			}

			// Days
			int days = (int) (diff) / (1000 * 60 * 60 * 24);
			if (days > 0) {
				return true;
			}
			// Hours
			long hour = diff / (60 * 60 * 1000);
			if (hour > 0) {
				return true;
			}

			// Minutes
			long minute = diff / (60 * 1000) % 60;
			long seconds = diff / 1000 % 60;
			if (minute >= otpValidity && seconds > 0) {
				return true;
			}
		} finally {
			newCalendar = null;
			oldCalendar = null;
		}

		return false;
	}

	public static void setValue(String value) {
		OTPEngine.value = value;
	}

	public String getValue() {
		return OTPEngine.value;
	}

	public static void setCharacters(int characters) {
		OTPEngine.characters = characters;
	}

	public int getCharacters() {
		return OTPEngine.characters;
	}

	public static int getOtpValidity() {
		return otpValidity;
	}

	public static void setOtpValidity(int otpValidity) {
		OTPEngine.otpValidity = otpValidity;
	}

}
