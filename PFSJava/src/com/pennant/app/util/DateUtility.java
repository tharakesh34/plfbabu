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
 * FileName : DateUtility.java *
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
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.util.PennantConstants;

/**
 * Used to specify Date type selection in <b>DateUtility</b> class.
 */
public final class DateUtility {

	/**
	 * Parsing Util Date in required Format
	 * 
	 * @param date
	 *            (String)
	 * 
	 * @param format
	 *            (String)
	 * 
	 * @return Date
	 */
	private static Date parseDate(String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return new Date(uDate.getTime());
	}

	/**
	 * Parsing Util Date in required Format
	 * 
	 * @param date
	 *            (String)
	 * 
	 * @param format
	 *            (String)
	 * 
	 * @return java.Util.Date
	 */
	public static java.util.Date getUtilDate(String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return uDate;
	}

	/**
	 * Format Util Date in required Format
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @param format
	 *            (String)
	 * 
	 * @return String
	 */
	public static String formatDate(java.util.Date date, String format) {
		return date == null ? "" : new SimpleDateFormat(format).format(date) + "";
	}

	/**
	 * Parse String Date into Util Date Format
	 * 
	 * @param date
	 *            (String)
	 * 
	 * @return Date
	 */
	private static Date parseDate(String date) {
		SimpleDateFormat df = new SimpleDateFormat(PennantConstants.dateFormat);
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return new Date(uDate.getTime());
	}

	/**
	 * Count Number of days between Util Dates
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @param date2
	 *            (Date)
	 * 
	 * @return int
	 */
	public static int getDaysBetween(java.util.Date date1, java.util.Date date2) {

		if (date1 == null || date2 == null){
			return -1;
		}
		GregorianCalendar gc1 = convert(date1);
		GregorianCalendar gc2 = convert(date2);
		if (gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR)) {
			return Math.abs(gc1.get(Calendar.DAY_OF_YEAR) - gc2.get(Calendar.DAY_OF_YEAR));
		}
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long days = (time1 - time2) / (1000 * 60 * 60 * 24);

		return Math.abs((int) days);
	}

	/**
	 * Count Number of days between String Formated Dates
	 * 
	 * @param date1
	 *            (String)
	 * 
	 * @param date2
	 *            (String)
	 * 
	 * @return int
	 */
	public static int getDaysBetween(String argDate1, String argDate2) {

		java.util.Date date1 = parseDate(argDate1);
		java.util.Date date2 = parseDate(argDate2);

		if (date1 == null || date2 == null){
			return -1;
		}
		GregorianCalendar gc1 = convert(date1);
		GregorianCalendar gc2 = convert(date2);
		if (gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR)) {
			return Math.abs(gc1.get(Calendar.DAY_OF_YEAR) - gc2.get(Calendar.DAY_OF_YEAR));
		}
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long days = (time1 - time2) / (1000 * 60 * 60 * 24);

		return Math.abs((int) days);
	}

	/**
	 * Count Number of days between String Formated Dates
	 * 
	 * @param date
	 *            (GregorianCalendar)
	 * 
	 * @return sql date
	 */
	public static java.sql.Date convert(GregorianCalendar date) {
		return date == null ? null : new java.sql.Date(date.getTime().getTime());
	}

	/**
	 * Compares the given dates and returns
	 * 
	 * <pre>
	 * 		 0 if both dates are equal
	 * 		<0 if date1 < date2
	 * 		>0 if date1 > date2
	 * </pre>
	 */
	public static int compare(java.util.Date date1, java.util.Date date2) {
		GregorianCalendar gc1, gc2;
		gc1 = new GregorianCalendar(getYear(date1), getMonth(date1) - 1, getDay(date1));
		gc2 = new GregorianCalendar(getYear(date2), getMonth(date2) - 1, getDay(date2));
		if (gc1.after(gc2)){
			return 1;
		} else if (gc1.before(gc2)){
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * Returns the day part of the Date. <br>
	 * This method is provided because <code> getDate() <code> method in Date is deprecated
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return int
	 */
	public static int getDay(java.util.Date date) {
		return date == null ? -1 : convert(date).get(Calendar.DATE);
	}

	/**
	 * Returns the month part of the Date. This method is provided because
	 * <code> getMonth() <code> method in Date is deprecated
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return int
	 */
	public static int getMonth(java.util.Date date) {
		return date == null ? -1 : convert(date).get(Calendar.MONTH) + 1;
	}

	/**
	 * Returns the last date of the month
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return int
	 */
	public static java.sql.Date getMonthEndDate(java.util.Date date) {

		int[] daysInAMonth = { 29, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int month = getMonth(date);
		int year = getYear(date);
		int day = daysInAMonth[month];
		if (isLeapYear(year) && month == 2){
			day++;
		}

		return convert(new GregorianCalendar(year, month - 1, day));
	}

	/**
	 * Returns the last date of the month
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return int
	 */
	public static java.sql.Date getMonthStartDate(java.util.Date date) {

		int month = getMonth(date) - 1;
		int year = getYear(date);
		int day = 01;

		return convert(new GregorianCalendar(year, month, day));
	}
	
	/**
	 * Returns the year part of the Date. This method is provided because
	 * <code> getYear() <code> method in Date is deprecated
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return int
	 */
	public static int getYear(java.util.Date date) {
		return date == null ? -1 : convert(date).get(Calendar.YEAR);
	}

	/**
	 * Returns the current date
	 * 
	 * @return Date
	 */
	public static java.sql.Date today() {
		return new java.sql.Date(System.currentTimeMillis());
	}

	/**
	 * Returns whether it is a leap year or not
	 * 
	 * @param year
	 *            (int)
	 * 
	 * @return boolean
	 */
	public static boolean isLeapYear(int year) {
		return new GregorianCalendar().isLeapYear(year);
	}

	/**
	 * Returns whether the values represent a valid date
	 * 
	 * @param day
	 *            (int)
	 * 
	 * @param month
	 *            (int)
	 * 
	 * @param year
	 *            (int)
	 * 
	 * @return boolean
	 */
	public static boolean isValidDate(int day, int month, int year) {
		if (day < 1 || day > 31) {
			return false;
		}
		if (month < 1 || month > 12) {
			return false;
		}
		if (year < 1000 || year > 9999) {
			return false;
		}
		int[] daysInAMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if (isLeapYear(year)) {
			daysInAMonth[1]++;
		}
		if (day > daysInAMonth[month - 1]) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the corresponding Date object. Formats valid are <font color = red> DDMMYYYY, MMDDYYYY, YYYYMMDD,
	 * YYYYDDMM, MMYYYY, MMMYYYY, DD</font> Throws InvalidFormatException and InvalidDateException
	 */
	public static Date getSqlDate(String date, String format) {
		return getSqlDate(date, format, '/');
	}

	/**
	 * Returns the corresponding Date object. Formats valid are <font color = red> DDMMYYYY, MMDDYYYY, YYYYMMDD,
	 * YYYYDDMM, MMYYYY, MMMYYYY, DD, MMDDYY, DDMMYY, YYMMDD</font> Throws InvalidFormatException and
	 * InvalidDateException
	 */
	public static Date getSqlDate(String date, String format, char sep) {
		if (format.trim().equalsIgnoreCase("DDMMYYYY")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "ddMMyyyy");
			} else {
				return parseDate(date, "dd" + sep + "MM" + sep + "yyyy");
			}
		} else if (format.trim().equalsIgnoreCase("MMDDYYYY")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "MMddyyyy");
			} else {
				return parseDate(date, "MM" + sep + "dd" + sep + "yyyy");
			}
		} else if (format.trim().equalsIgnoreCase("YYYYDDMM")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "yyyyddMM");
			} else {
				return parseDate(date, "yyyy" + sep + "dd" + sep + "MM");
			}
		} else if (format.trim().equalsIgnoreCase("YYYYMMDD")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "yyyyMMdd");
			} else {
				return parseDate(date, "yyyy" + sep + "MM" + sep + "dd");
			}
		} else if (format.trim().equalsIgnoreCase("MMYYYY")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "MMyyyy");
			} else {
				return parseDate(date, "MM" + sep + "yyyy");
			}
		} else if (format.trim().equalsIgnoreCase("DD")) {
			return parseDate(date, "dd");
		} else if (format.trim().equalsIgnoreCase("MMMYYYY")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "MMMyyyy");
			} else {
				return parseDate(date, "MMM" + sep + "yyyy");
			}
		}
		if (format.trim().equalsIgnoreCase("DDMMYY")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "ddMMyy");
			} else {
				return parseDate(date, "dd" + sep + "MM" + sep + "yy");
			}
		} else if (format.trim().equalsIgnoreCase("MMDDYY")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "MMddyy");
			} else {
				return parseDate(date, "MM" + sep + "dd" + sep + "yy");
			}
		} else if (format.trim().equalsIgnoreCase("YYMMDD")) {
			if (sep == ' ' || sep == '\u0000') {
				return parseDate(date, "yyMMdd");
			} else {
				return parseDate(date, "yy" + sep + "MM" + sep + "dd");
			}
		} else {
			return null;
		}
	}

	/**
	 * Returns the string representation in the corresponding format. Formats valid are <font color = red> DDMMYYYY,
	 * MMDDYYYY, YYYYMMDD, YYYYDDMM, MMYYYY, MMMYYYY, DD, MMDDYY, YYMMDD, DDMMYY</font> Throws InvalidFormatException
	 */
	public static String format(Date date, String format, char dateSeparator) {
		if (date == null){
			return null;
		}
		String allowed = " -./";
		String dateSeparatorString = String.valueOf('/');
		if (allowed.indexOf(dateSeparator) >= 0) {
			dateSeparatorString = String.valueOf(dateSeparator);
		} else if (dateSeparator == '\u0000') {
			dateSeparatorString = "";
		}
		if (format.trim().equalsIgnoreCase("DDMMYYYY")) {
			return formatDate(date, "dd" + dateSeparatorString + "MM" + dateSeparatorString
			        + "yyyy");
		} else if (format.trim().equalsIgnoreCase("MMDDYYYY")) {
			return formatDate(date, "MM" + dateSeparatorString + "dd" + dateSeparatorString
			        + "yyyy");
		} else if (format.trim().equalsIgnoreCase("YYYYDDMM")) {
			return formatDate(date, "yyyy" + dateSeparatorString + "dd" + dateSeparatorString
			        + "MM");
		} else if (format.trim().equalsIgnoreCase("YYYYMMDD")) {
			return formatDate(date, "yyyy" + dateSeparatorString + "MM" + dateSeparatorString
			        + "dd");
		} else if (format.trim().equalsIgnoreCase("MMYYYY")) {
			return formatDate(date, "MM" + dateSeparatorString + "yyyy");
		} else if (format.trim().equalsIgnoreCase("DD")) {
			return formatDate(date, "dd");
		} else if (format.trim().equalsIgnoreCase("MMMYYYY")) {
			return formatDate(date, "MMM" + dateSeparatorString + "yyyy");
		} else if (format.trim().equalsIgnoreCase("DDMMYY")) {
			return formatDate(date, "dd" + dateSeparatorString + "MM" + dateSeparatorString + "yy");
		} else if (format.trim().equalsIgnoreCase("MMDDYY")) {
			return formatDate(date, "MM" + dateSeparatorString + "dd" + dateSeparatorString + "yy");
		} else if (format.trim().equalsIgnoreCase("YYMMDD")) {
			return formatDate(date, "yy" + dateSeparatorString + "MM" + dateSeparatorString + "dd");
		} else {
			return null;
		}
	}

	/**
	 * String Formated Date intoUtil Date by '/'
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @param format
	 *            (String)
	 * 
	 * @return String
	 */
	public static String format(Date date, String format) {
		return format(date, format, '/');
	}

	public static String formatUtilDate(java.util.Date date, String format) {
		return date == null ? "" : formatDate(date, format);
	}

	/**
	 * String Formated Date intoUtil Date by reuired Seperator or not
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @param format
	 *            (String)
	 * 
	 * @param seperatorRequired
	 *            (boolean)
	 * 
	 * @return String
	 */
	public static String format(Date date, String format, boolean seperatorRequired) {
		if (seperatorRequired) {
			return format(date, format);
		} else {
			return format(date, format, '\u0000');
		}
	}

	/**
	 * Return whether the date is before today
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return boolean
	 */
	public static boolean beforeToday(java.util.Date date) {
		return date == null ? false : date.before(new java.util.Date());
	}

	/**
	 * Return whether the date is after today
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return boolean
	 */
	public static boolean afterToday(java.util.Date date) {
		return date == null ? false : date.after(new java.util.Date());
	}

	/**
	 * Adds the required number of days to the date
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @param days
	 *            (int)
	 * 
	 * @return Date
	 */
	public static java.sql.Date addDays(java.util.Date date, int days) {
		if (date == null){
			return null;
		}
		GregorianCalendar gc = convert(date);
		gc.add(Calendar.DATE, days);
		return convert(gc);
	}

	/**
	 * Adds the required number of weeks to the date
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @param weeks
	 *            (int)
	 * 
	 * @return Date
	 */
	public static java.sql.Date addWeeks(java.util.Date date, int weeks) {
		return addDays(date, weeks * 7);
	}

	/**
	 * Adds the required number of months to the date
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @param months
	 *            (int)
	 * 
	 * @return Date
	 */
	public static java.sql.Date addMonths(java.util.Date date, int months) {
		if (date == null){
			return null;
		}
		GregorianCalendar gc = convert(date);
		gc.add(Calendar.MONTH, months);
		return convert(gc);
	}

	/**
	 * Adds the required number of years to the date
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @param years
	 *            (int)
	 * 
	 * @return Date
	 */
	public static java.sql.Date addYears(java.util.Date date, int years) {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = convert(date);
		gc.add(Calendar.YEAR, years);
		return convert(gc);
	}

	/**
	 * Adds the required frequency to the date. Valid frequencies are<br>
	 * <code>
		'D' = Daily         <br>
		'W' = Weekly        <br>
		'F' = Fortnightly   <br>
		'M' = Monthly       <br>
		'Q' = Quarterly     <br>
		'H' = Half Yearly   <br>
		'Y' = Annually      <br>
	 */
	public static java.sql.Date addFrequency(java.util.Date date, char frequency) {

		if (date == null){
			return null;
		}
		GregorianCalendar gc = convert(date);
		switch (frequency) {
		case 'D':
			gc.add(Calendar.DATE, 1);
			break;
		case 'W':
			gc.add(Calendar.DATE, 7);
			break;
		case 'F':
			gc.add(Calendar.DATE, 14);
			break;
		case 'M':
			gc.add(Calendar.MONTH, 1);
			break;
		case 'Q':
			gc.add(Calendar.MONTH, 3);
			break;
		case 'H':
			gc.add(Calendar.MONTH, 6);
			break;
		case 'Y':
			gc.add(Calendar.YEAR, 1);
			break;
		}

		return convert(gc);
	}

	/**
	 * Returns the total number of weeks between the two dates
	 */
	public static int getWeeksBetween(java.util.Date date1, java.util.Date date2) {
		return Math.abs((int) (getDaysBetween(date1, date2) / 7));
	}

	/**
	 * Returns the total number of months between the two dates
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @return int
	 */
	public static int getMonthsBetween(java.util.Date date1, java.util.Date date2) {

		if (date1 == null || date2 == null) {
			return -1;
		}
		if (date1.before(date2)) {
			java.util.Date temp = date2;
			date2 = date1;
			date1 = temp;
		}
		int years = convert(date1).get(Calendar.YEAR) - convert(date2).get(Calendar.YEAR);
		int months = convert(date1).get(Calendar.MONTH) - convert(date2).get(Calendar.MONTH);
		months += years * 12;
		if (convert(date1).get(Calendar.DATE) < convert(date2).get(Calendar.DATE)) {
			months--;
		}

		return months;
	}

	public static int getMonthsBetween(java.util.Date date1, java.util.Date date2, boolean includeDate2) {

		if (date1 == null || date2 == null){
			return -1;
		}
		if (date1.before(date2)) {
			java.util.Date temp = date2;
			date2 = date1;
			date1 = temp;
		}
		
		int years = convert(date1).get(Calendar.YEAR) - convert(date2).get(Calendar.YEAR);
		int months = 0;
		if (includeDate2 && getMonthEndDate(date1).compareTo(date1) == 0) {
			months = convert(addDays(date1, 1)).get(Calendar.MONTH) - convert(date2).get(Calendar.MONTH);
		}else{
			months = convert(date1).get(Calendar.MONTH) - convert(date2).get(Calendar.MONTH);
		}
		
		months += years * 12;
		java.util.Date date3 = addMonths(date2, months);

		int days = 0;
		if (includeDate2) {
			days = convert(addDays(date1, 1)).get(Calendar.DATE) - convert(date3).get(Calendar.DATE);
		}else{
			days = convert(date1).get(Calendar.DATE) - convert(date3).get(Calendar.DATE);
		}
		
		if(days > 0){
			months++;
		}

		return months;
	}

	/**
	 * Returns the total number of years between the two dates
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @return int
	 */
	public static int getYearsBetween(java.util.Date date1, java.util.Date date2) {

		if (date1 == null || date2 == null){
			return -1;
		}
		if (date1.before(date2)) {
			java.util.Date temp = date2;
			date2 = date1;
			date1 = temp;
		}
		int years = convert(date1).get(Calendar.YEAR) - convert(date2).get(Calendar.YEAR);
		if (convert(date1).get(Calendar.MONTH) < convert(date2).get(Calendar.MONTH)) {
			years--;
		} else if (convert(date1).get(Calendar.MONTH) == convert(date2).get(Calendar.MONTH)) {
			if (convert(date1).get(Calendar.DATE) < convert(date2).get(Calendar.DATE)) {
				years--;
			}
		}

		return years;
	}

	/**
	 * Returns the duration between the two dates in <font color=red>'1 year 2 months and 3 days'</font> format
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @return String
	 */
	public static String getDurationBetween(java.util.Date date1, java.util.Date date2) {

		if (date1 == null || date2 == null){
			return null;
		}
		int years = getYearsBetween(date1, date2);
		int months = getMonthsBetween(date1, date2) % 12;
		int days = getDay(date1) - getDay(date2);
		if (days < 0) {
			java.util.Date date = addYears(date2, years);
			date = addMonths(date, months);
			days = getDaysBetween(date1, date);
		}

		return years + (years == 1 ? " year " : " years ") + months
		        + (months == 1 ? " month and " : " months and ") + days
		        + (days == 1 ? " day " : " days");
	}

	/**
	 * Converts the Date to GregorianCalendar
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return GregorianCalendar
	 */
	public static GregorianCalendar convert(java.util.Date date) {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return gc;
	}

	/**
	 * pass the startMonth of the Financial Year as well as a date and you will get the number of days in the particular
	 * half-year. If the date is not end of financial year, it will return -1; startMonth 1=Jan, 2=Feb....., 12=Dec
	 */
	public static int getDaysInYear(int startMonth, java.util.Date date) {
		if (Math.abs(startMonth - getMonth(date) - 1) % 12 != 0){
			return -1;
		}
		return DateUtility.getDaysBetween(
		        DateUtility.getMonthEndDate(DateUtility.addMonths(date, -12)),
		        DateUtility.getMonthEndDate(date));

	}

	/**
	 * Take String Date and return UTIL Date
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return Date
	 */
	public static Date getDate(String date) {
		return parseDate(date);
	}
	public static Date getDate(String date, String format) {
		return parseDate(date, format);
	}

	/**
	 * Take String Date and return UTIL Date in DB Format
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return Date
	 */
	public static Date getDBDate(String date) {
		if(date == null){
			return null;
		}
		return parseDate(date, PennantConstants.DBDateFormat);
	}

	public static long getDateYYYYJDay() {
		Calendar curCalendar = Calendar.getInstance();
		return (curCalendar.get(Calendar.YEAR) * 1000)
		          + curCalendar.get(Calendar.DAY_OF_YEAR);
	}

	public static int getDateJulionDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
	}

	public static long getDaysBetween(Calendar startCalendar, Calendar endCalendar) {
		return (long) Math.round((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis())
		        / (1000d * 60d * 60d * 24d));
	}

	/**
	 * This method compares two times by comparing hours ,minutes and seconds
	 * 
	 * @param firstTime
	 * @param secondTime
	 * @param isSeconds
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static int compareTime(java.util.Date firstTime, java.util.Date secondTime,
	        boolean isSeconds) {

		GregorianCalendar gc1 = convert(firstTime);
		GregorianCalendar gc2 = convert(secondTime);

		if (isSeconds) {
			if (gc1.get(gc1.HOUR_OF_DAY) == gc2.get(gc2.HOUR_OF_DAY)
			        && gc1.get(gc1.MINUTE) == gc2.get(gc2.MINUTE)
			        && gc1.get(gc1.SECOND) == gc2.get(gc2.SECOND)) {
				return 0;
			}
		}
		if (gc1.get(gc1.HOUR_OF_DAY) == gc2.get(gc2.HOUR_OF_DAY)
		        && gc1.get(gc1.MINUTE) == gc2.get(gc2.MINUTE)) {
			return 0;
		}
		if (gc1.get(gc1.HOUR_OF_DAY) > gc2.get(gc2.HOUR_OF_DAY)) {
			return 1;
		}
		if (gc1.get(gc1.HOUR_OF_DAY) == gc2.get(gc2.HOUR_OF_DAY)
		        && gc1.get(gc1.MINUTE) > gc2.get(gc2.MINUTE)) {
			return 1;
		}
		if (isSeconds) {
			if (gc1.get(gc1.HOUR_OF_DAY) == gc2.get(gc2.HOUR_OF_DAY)
			        && gc1.get(gc1.MINUTE) == gc2.get(gc2.MINUTE)
			        && gc1.get(gc1.SECOND) > gc2.get(gc2.SECOND)) {
				return 1;
			}
		}
		return -1;
	}

	public static java.util.Date getUtilDate() {
		return new java.util.Date(System.currentTimeMillis());
	}

	public static java.util.Date getSystemDate() {
		return parseDate(formatUtilDate(getUtilDate(), PennantConstants.dateFormat));
	}

	/**
	 * This method returns the year End Date for a give Date
	 * 
	 * @param date
	 * @return Date
	 */
	public static java.util.Date getYearEndDate(java.util.Date date) {
		int month = 11;
		int year = getYear(date);
		int day = 31;
		return convert(new GregorianCalendar(year, month, day));
	}

	public static String timeBetween(java.util.Date endTime, java.util.Date startTime) {
		if (endTime == null || startTime == null) {
			return "";
		}
		return new SimpleDateFormat("HH:mm:ss").format(convert(new GregorianCalendar(
		        getYearsBetween(endTime, startTime), 
		        getMonthsBetween(endTime, startTime) % 12,
		        getDay(endTime) - getDay(startTime), 
		        convert(endTime).get(Calendar.HOUR)- convert(startTime).get(Calendar.HOUR), 
		        convert(endTime).get(Calendar.MINUTE)- convert(startTime).get(Calendar.MINUTE), 
		        convert(endTime).get(Calendar.SECOND)- convert(startTime).get(Calendar.SECOND))));

	}

	public static Timestamp ConvertFromXMLTime(XMLGregorianCalendar xmlCalendar) {
		return new Timestamp(xmlCalendar.toGregorianCalendar().getTimeInMillis());
	}

	public static XMLGregorianCalendar getXMLDate(Timestamp timestamp)
	        throws DatatypeConfigurationException {

		GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
		gcal.setTimeInMillis(timestamp.getTime());
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	/**
	 * Format date
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formateDate(java.util.Date date, String dateFormat) {
		String formatedDate = null;
		if (StringUtils.trimToEmpty(dateFormat).equals("")) {
			dateFormat = PennantConstants.dateFormat;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		if (date != null) {
			formatedDate = formatter.format(date);
		}
		return formatedDate;

	}
//	public static Date getformatAS400Date(String date) {
//		String temp=date.substring(1);		
//		SimpleDateFormat df = new SimpleDateFormat(PennantConstants.AS400DateFormat);
//		java.util.Date uDate = null;
//		try {
//			uDate = df.parse(temp);
//		} catch (ParseException pe) {
//			pe.printStackTrace();
//		}
//		return new Date(uDate.getTime());
//	}
	
	public static java.util.Date convertDateFromAS400(BigDecimal as400Date){
		if (as400Date != null){
			if  (BigDecimal.ZERO.equals(as400Date)) {
				return getUtilDate("1900-01-01","yyyy-MM-dd");
			}else if (as400Date.equals(new BigDecimal(9999999))){
				return getUtilDate("2049-12-31","yyyy-MM-dd"); 
			}else{			
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				try {
					return df.parse(new BigDecimal(19000000).add(as400Date).toString());
				} catch (ParseException pe) {
					pe.printStackTrace();
				}
			}
		}
		
		return null;
	}

	public static Date getformatCDate(String date) {
		String temp=date.substring(1);		
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		java.util.Date uDate = null;
		try {
			uDate = df.parse(temp);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return new Date(uDate.getTime());
	}
	
	/**
	 * Format date
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static Date getFormattedDate(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException pe) {
			return null;
		}
		return getDBDate(formatDate(new Date(uDate.getTime()),
		        PennantConstants.DBDateFormat));
	}
	
}
