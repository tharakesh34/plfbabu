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
package com.pennanttech.pff.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link java.util.Calendar} and {@link java.util.Date} object.
 * </p>
 */
public class DateUtil {
	protected DateUtil() {
		super();
	}

	/**
	 * Enumerates the date format patterns.
	 */
	public enum DateFormat {
		SHORT_DATE("dd/MM/yyyy"),
		LONG_DATE("dd-MMM-yyyy"),
		SHORT_TIME("HH:mm"),
		LONG_TIME("HH:mm:ss"),
		SHORT_DATE_TIME("dd/MM/yyyy HH:mm"),
		LONG_DATE_TIME("dd-MMM-yyyy HH:mm:ss");
		
		private final String	pattern;

		private DateFormat(String pattern) {
			this.pattern = pattern;
		}

		/**
		 * @return The pattern of the Format.
		 */
		public String getPattern() {
			return pattern;
		}
	}

	/**
	 * Returns a {@link java.util.Date} object that represents the server time at which it was allocated.
	 * 
	 * @return A {@link java.util.Date} that represents the server time.
	 */
	public static Date getSysDate() {
		return new Date();
	}

	/**
	 * Returns the string representation with the specified date format pattern of the server time.
	 * 
	 * @param dateFormat
	 *            The format describing the date and time pattern.
	 * @return The formatted date string of the server time.
	 * @throws IllegalArgumentException
	 *             - If the given format is <code>null</code>.
	 */
	public static String getSysDate(DateFormat dateFormat) {
		if (dateFormat == null) {
			throw new IllegalArgumentException();
		}

		return format(getSysDate(), dateFormat);
	}
	
	/**
	 * Returns the string representation with the specified date format pattern of the server time.
	 * 
	 * @param dateFormat
	 *            The format describing the date and time pattern.
	 * @return The formatted date string of the server time.
	 * @throws IllegalArgumentException
	 *             - If the given format is <code>null</code>.
	 */
	public static String getSysDate(String dateFormat) {
		if (dateFormat == null) {
			throw new IllegalArgumentException();
		}

		return format(getSysDate(), dateFormat);
	}

	/**
	 * <p>
	 * Formats a {@link java.util.Date} object into string with the specified pattern.
	 * </p>
	 * 
	 * @param date
	 *            The date object to be formatted.
	 * @param pattern
	 *            The pattern describing the date and time format.
	 * @return The formatted date string, or an empty String if input date is <code>null</code>.
	 * @throws IllegalArgumentException
	 *             If the given pattern is <code>null</code> or invalid.
	 */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return "";
		}

		if (pattern == null) {
			throw new IllegalArgumentException();
		}

		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);

		return formatter.format(date);
	}

	/**
	 * <p>
	 * Formats a {@link java.util.Date} object into string with the specified date format pattern.
	 * </p>
	 * 
	 * <p>
	 * <code>null</code>s are handled without exceptions.
	 * </p>
	 * 
	 * @param date
	 *            The date object to be formatted.
	 * @param dateFormat
	 *            The format describing the date and time pattern.
	 * @return The formatted date string, or an empty String if input date is <code>null</code>.
	 */
	public static String format(Date date, DateFormat dateFormat) {
		return format(date, dateFormat.getPattern());
	}

	/**
	 * <p>
	 * Formats a {@link java.util.Date} object into string with the standard short date format.
	 * </p>
	 * 
	 * <p>
	 * <code>null</code>s are handled without exceptions.
	 * </p>
	 * 
	 * @param date
	 *            The date object to be formatted.
	 * @return The formatted date string, or an empty String if <code>null</code> input.
	 */
	public static String formatToShortDate(Date date) {
		return format(date, DateFormat.SHORT_DATE.getPattern());
	}

	/**
	 * <p>
	 * Formats a {@link java.util.Date} object into string with the standard long date format.
	 * </p>
	 * 
	 * <p>
	 * <code>null</code>s are handled without exceptions.
	 * </p>
	 * 
	 * @param date
	 *            The date object to be formatted.
	 * @return The formatted date string, or an empty String if <code>null</code> input.
	 */
	public static String formatToLongDate(Date date) {
		return format(date, DateFormat.LONG_DATE.getPattern());
	}

	/**
	 * <p>
	 * Converts the string representation of a specified pattern to its {@link java.util.Date} equivalent.
	 * </p>
	 * 
	 * @param text
	 *            The string that should be parsed.
	 * @param pattern
	 *            The pattern describing the date and time format.
	 * @return A {@link java.util.Date} parsed from the string. <code>null</code> if <code>null</code> input or empty
	 *         input.
	 * @throws IllegalArgumentException
	 *             If the given pattern is <code>null</code> or invalid.
	 * @throws ParseException
	 *             - If the given string cannot be parsed.
	 */
	public static Date parse(String text, String pattern) throws ParseException {
		if (StringUtils.isEmpty(text)) {
			return null;
		}

		if (pattern == null) {
			throw new IllegalArgumentException();
		}

		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);

		return formatter.parse(text);
	}

	/**
	 * <p>
	 * Converts the string representation of a specified date format pattern to its {@link java.util.Date} equivalent.
	 * </p>
	 * 
	 * @param text
	 *            The string that should be parsed.
	 * @param dateFormat
	 *            The format describing the date and time pattern.
	 * @return A {@link java.util.Date} parsed from the string. <code>null</code> if <code>null</code> input or empty
	 *         input.
	 * @throws ParseException
	 *             - If the given string cannot be parsed.
	 */
	public static Date parse(String text, DateFormat dateFormat) throws ParseException {
		return parse(text, dateFormat.getPattern());
	}

	/**
	 * <p>
	 * Converts the string representation of the standard short date format to its {@link java.util.Date} equivalent.
	 * </p>
	 * 
	 * @param text
	 *            The string that should be parsed.
	 * @return A {@link java.util.Date} parsed from the string. <code>null</code> if <code>null</code> input or empty
	 *         input.
	 * @throws ParseException
	 *             - If the given string cannot be parsed.
	 */
	public static Date parseShortDate(String text) throws ParseException {
		return parse(text, DateFormat.SHORT_DATE.getPattern());
	}

	/**
	 * <p>
	 * Compares two dates for equality, returning <code>true</code> if they are equal. The date objects are equal if and
	 * only if the <code>getTime</code> method returns the same <code>long</code> value for both.
	 * </p>
	 * 
	 * <p>
	 * <code>null</code>s are handled without exceptions. Two <code>null</code> references are considered to be equal.
	 * </p>
	 * 
	 * @param date1
	 *            The first date to compare.
	 * @param date2
	 *            The second date to compare.
	 * @return <code>true</code> if the date objects are the same; <code>false</code> otherwise.
	 */
	public static boolean matches(Date date1, Date date2) {
		if (date1 == null) {
			return date2 == null;
		}

		return date1.equals(date2);
	}

	/**
	 * Returns a {@link Date} object with the specified YEAR, MONTH, and DAY_OF_MONTH.
	 * 
	 * @param year
	 *            The value used to set the YEAR field.
	 * @param month
	 *            The value used to set the MONTH field. Month value is 0-based. e.g., 0 for January.
	 * @param date
	 *            The value used to set the DAY_OF_MONTH field.
	 * @return A {@link Date} representing the the specified YEAR, MONTH, and DAY_OF_MONTH.
	 */
	public static Date getDate(int year, int month, int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(year, month, date);

		return calendar.getTime();
	}

	/**
	 * Returns month start {@link Date} for the specified {@link Date}.
	 * 
	 * @param date
	 *            The date object which is used to get the month start date.
	 * @return A {@link Date} representing the month start date
	 */
	public static Date getMonthEnd(Date date) {
		int[] daysInAMonth = { 29, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int month = getMonth(date);
		int year = getYear(date);
		int day = daysInAMonth[month];

		if (isLeapYear(year) && month == 2) {
			day++;
		}

		return convert(new GregorianCalendar(year, month - 1, day));
	}

	/**
	 * Returns month end {@link Date} for the specified {@link Date}.
	 * 
	 * @param date
	 *            The date object which is used to get the month end date.
	 * @return A {@link Date} representing the month end date
	 */
	public static Date getMonthStart(Date date) {
		int month = getMonth(date) - 1;
		int year = getYear(date);
		int day = 01;

		return convert(new GregorianCalendar(year, month, day));
	}

	// FIXME  ADD Comments
	public static Date addMonths(Date date, int months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}

	// FIXME  ADD Comments
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	// FIXME  ADD Comments
	public static java.sql.Date getSqlDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException();
		}

		return new java.sql.Date(date.getTime());
	}

	// FIXME  ADD Comments
	public static int getMonth(Date date) {
		return date == null ? -1 : convert(date).get(Calendar.MONTH) + 1;
	}

	// FIXME  ADD Comments
	public static int getYear(java.util.Date date) {
		return date == null ? -1 : convert(date).get(Calendar.YEAR);
	}

	private static GregorianCalendar convert(Date date) {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return gc;
	}

	private static Date convert(GregorianCalendar date) {
		return date == null ? null : new Date(date.getTime().getTime());
	}

	@SuppressWarnings("unused")
	private static int getDay(Date date) {
		return date == null ? -1 : convert(date).get(Calendar.DATE);
	}

	private static boolean isLeapYear(int year) {
		return new GregorianCalendar().isLeapYear(year);
	}
}
