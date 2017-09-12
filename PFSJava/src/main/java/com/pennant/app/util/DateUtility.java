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
import org.apache.log4j.Logger;

import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.util.DateUtil;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link java.util.Calendar} and {@link java.util.Date} object.
 * </p>
 */
public final class DateUtility extends DateUtil {
	private static final Logger logger = Logger.getLogger(DateUtility.class);

	
	private DateUtility(){
		super();
	}
	
	/**
	 * Returns the string representation with the specified pattern of the server time.
	 * 
	 * @param pattern
	 *            The pattern describing the date and time format.
	 * @return The formatted date string of the server time.
	 * @throws IllegalArgumentException
	 *             - If the given pattern is <code>null</code> or invalid.
	 */
	public static String getSysDate(String pattern) {
		if (StringUtils.isBlank(pattern)) {
			throw new IllegalArgumentException();
		}

		return format(getSysDate(), pattern);
	}

	/**
	 * Returns a {@link java.util.Date} object that represents the application date.
	 * 
	 * @return A {@link java.util.Date} that represents the application date.
	 */
	public static java.util.Date getAppDate() {
		return SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_DATE.getCode());
	}

	/**
	 * Returns the string representation with the specified date format pattern of the application date.
	 * 
	 * @param dateFormat
	 *            The format describing the date and time pattern.
	 * @return The formatted date string of the application date.
	 */
	public static String getAppDate(DateFormat dateFormat) {
		return format(getAppDate(), dateFormat);
	}
	
	/**
	 * Returns the string representation with the specified date format pattern of the application date.
	 * 
	 * @param dateFormat
	 *            The format describing the date and time pattern.
	 * @return The formatted date string of the application date.
	 */
	public static String getAppDate(String dateFormat) {
		return format(getAppDate(), dateFormat);
	}

	/**
	 * Returns a {@link java.util.Date} object that represents the value date.
	 * 
	 * @return A {@link java.util.Date} that represents the value date.
	 */
	public static java.util.Date getAppValueDate() {
		return SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_VALUEDATE.getCode());
	}

	public static String getAppValueDate(DateFormat dateFormat) {
		return format(SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_VALUEDATE.getCode()), dateFormat);
	}

	/**
	 * Returns a {@link java.util.Date} object that represents the Next business date.
	 * 
	 * @return A {@link java.util.Date} that represents the Next business date.
	 */
	public static java.util.Date getNextBusinessdate() {
		return SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);
	}

	/**
	 * Returns the string representation with the specified pattern of the value date.
	 * 
	 * @param pattern
	 *            The pattern describing the date and time format.
	 * @return The formatted date string of the value date.
	 */
	public static String getValueDate(String pattern) {
		return format(SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_VALUEDATE.getCode()), pattern);
	}

	public static String formatDate(java.util.Date date, String pattern) {
		return date == null ? "" : new SimpleDateFormat(pattern).format(date);
	}

	public static String formateDate(java.util.Date date, String pattern) {
		String formatedDate = null;
		if (StringUtils.isBlank(pattern)) {
			pattern = DateFormat.SHORT_DATE.getPattern();
		}

		SimpleDateFormat formatter = new SimpleDateFormat(pattern);

		if (date != null) {
			formatedDate = formatter.format(date);
		}
		return formatedDate;

	}

	public static String formatUtilDate(java.util.Date date, String pattern) {
		return date == null ? "" : formatDate(date, pattern);
	}

	private static Date parseDate(String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException e) {
			logger.error("Exception: ", e);
		}
		if(uDate == null){
			uDate = DateUtility.getAppDate();
		}
		return new Date(uDate.getTime());
	}

	private static Date parseDate(String date) {
		SimpleDateFormat df = new SimpleDateFormat(DateFormat.SHORT_DATE.getPattern());
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException e) {
			logger.error("Exception: ", e);
		}
		if(uDate == null){
			uDate = DateUtility.getAppDate();
		}
		return new Date(uDate.getTime());
	}

	public static java.util.Date getUtilDate(String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException e) {
			logger.error("Exception: ", e);
		}
		return uDate;
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
		if (date == null) {
			return null;
		}
		return parseDate(date, PennantConstants.DBDateFormat);
	}

	public static Date getformatCDate(String date) {
		String temp = date.substring(1);
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		java.util.Date uDate = null;
		try {
			uDate = df.parse(temp);
		} catch (ParseException e) {
			logger.error("Exception: ", e);
		}
		if(uDate == null){
			uDate = DateUtility.getAppDate();
		}
		return new Date(uDate.getTime());
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
		if (gc1.after(gc2)) {
			return 1;
		} else if (gc1.before(gc2)) {
			return -1;
		} else {
			return 0;
		}
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
	public static int compareTime(java.util.Date firstTime, java.util.Date secondTime, boolean isSeconds) {

		GregorianCalendar gc1 = convert(firstTime);
		GregorianCalendar gc2 = convert(secondTime);

		if (isSeconds) {
			if (gc1.get(gc1.HOUR_OF_DAY) == gc2.get(gc2.HOUR_OF_DAY) && gc1.get(gc1.MINUTE) == gc2.get(gc2.MINUTE)
					&& gc1.get(gc1.SECOND) == gc2.get(gc2.SECOND)) {
				return 0;
			}
		}
		if (gc1.get(gc1.HOUR_OF_DAY) == gc2.get(gc2.HOUR_OF_DAY) && gc1.get(gc1.MINUTE) == gc2.get(gc2.MINUTE)) {
			return 0;
		}
		if (gc1.get(gc1.HOUR_OF_DAY) > gc2.get(gc2.HOUR_OF_DAY)) {
			return 1;
		}
		if (gc1.get(gc1.HOUR_OF_DAY) == gc2.get(gc2.HOUR_OF_DAY) && gc1.get(gc1.MINUTE) > gc2.get(gc2.MINUTE)) {
			return 1;
		}
		if (isSeconds) {
			if (gc1.get(gc1.HOUR_OF_DAY) == gc2.get(gc2.HOUR_OF_DAY) && gc1.get(gc1.MINUTE) == gc2.get(gc2.MINUTE)
					&& gc1.get(gc1.SECOND) > gc2.get(gc2.SECOND)) {
				return 1;
			}
		}
		return -1;
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

		if (date1 == null || date2 == null) {
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
	public static Date convert(GregorianCalendar date) {
		return date == null ? null : new Date(date.getTime().getTime());
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
	public static Date getMonthEndDate(java.util.Date date) {

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
	 * Returns the last date of the month
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return int
	 */
	public static Date getMonthStartDate(java.util.Date date) {

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
	 * Adds the required number of days to the date
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(java.util.Date date, int days) {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = convert(date);
		gc.add(Calendar.DATE, days);
		return convert(gc);
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
	public static Date addMonths(java.util.Date date, int months) {
		if (date == null) {
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
	public static Date addYears(java.util.Date date, int years) {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = convert(date);
		gc.add(Calendar.YEAR, years);
		return convert(gc);
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

		if (date1 == null || date2 == null) {
			return -1;
		}
		if (date1.before(date2)) {
			java.util.Date temp = date2;
			date2 = date1;
			date1 = temp;
		}

		int years = convert(date1).get(Calendar.YEAR) - convert(date2).get(Calendar.YEAR);
		int months = 0;
		if (includeDate2
				&& getMonthEndDate(date1)
						.compareTo(
								getUtilDate(formatUtilDate(date1, PennantConstants.DBDateFormat),
										PennantConstants.DBDateFormat)) == 0) {

			if (convert(addDays(date1, 1)).get(Calendar.YEAR) != convert(date1).get(Calendar.YEAR)) {
				years++;
			}
			months = convert(addDays(date1, 1)).get(Calendar.MONTH) - convert(date2).get(Calendar.MONTH);
		} else {
			months = convert(date1).get(Calendar.MONTH) - convert(date2).get(Calendar.MONTH);
		}

		months += years * 12;
		java.util.Date date3 = addMonths(date2, months);

		int days = 0;
		if (includeDate2) {
			days = convert(addDays(date1, 1)).get(Calendar.DATE) - convert(date3).get(Calendar.DATE);
		} else {
			days = convert(date1).get(Calendar.DATE) - convert(date3).get(Calendar.DATE);
		}

		if (days > 0) {
			months++;
		}

		return Math.abs(months);
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

		if (date1 == null || date2 == null) {
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

	public static long getDateYYJDay() {
		Calendar curCalendar = Calendar.getInstance();
		curCalendar.setTime(getAppDate());

		return Long.parseLong(String.valueOf(curCalendar.get(Calendar.YEAR)).substring(2)
				.concat(StringUtils.leftPad(String.valueOf(curCalendar.get(Calendar.DAY_OF_YEAR)), 3, "0")));
	}

	public static long getDaysBetween(Calendar startCalendar, Calendar endCalendar) {
		return Math
				.round((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / (1000d * 60d * 60d * 24d));
	}

	/**
	 * This method returns the year End Date for a give Date
	 * 
	 * @param date
	 * @return Date
	 */
	public static java.util.Date getYearStartDate(java.util.Date date) {
		int month = 0;
		int year = getYear(date);
		int day = 1;
		return convert(new GregorianCalendar(year, month, day));
	}

	public static String timeBetween(java.util.Date endTime, java.util.Date startTime) {
		if (endTime == null || startTime == null) {
			return "";
		}
		return new SimpleDateFormat("HH:mm:ss").format(convert(new GregorianCalendar(
				getYearsBetween(endTime, startTime), getMonthsBetween(endTime, startTime) % 12, getDay(endTime)
						- getDay(startTime), convert(endTime).get(Calendar.HOUR)
						- convert(startTime).get(Calendar.HOUR), convert(endTime).get(Calendar.MINUTE)
						- convert(startTime).get(Calendar.MINUTE), convert(endTime).get(Calendar.SECOND)
						- convert(startTime).get(Calendar.SECOND))));

	}

	public static Timestamp ConvertFromXMLTime(XMLGregorianCalendar xmlCalendar) {
		return new Timestamp(xmlCalendar.toGregorianCalendar().getTimeInMillis());
	}

	public static XMLGregorianCalendar getXMLDate(Timestamp timestamp) throws DatatypeConfigurationException {

		GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
		gcal.setTimeInMillis(timestamp.getTime());
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
	}

	public static java.util.Date convertDateFromAS400(BigDecimal as400Date) {
		if (as400Date != null) {
			if (BigDecimal.ZERO.equals(as400Date)) {
				return getUtilDate("1900-01-01", PennantConstants.DBDateFormat);
			} else if (as400Date.equals(new BigDecimal(9999999))) {
				return getUtilDate("2049-12-31", PennantConstants.DBDateFormat);
			} else {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				try {
					return df.parse(new BigDecimal(19000000).add(as400Date).toString());
				} catch (ParseException e) {
					logger.error("Exception: ", e);
				}
			}
		}

		return null;
	}

	public static Timestamp getTimestamp(java.util.Date date) {
		Timestamp timestamp = null;

		if (date != null) {
			timestamp = new Timestamp(date.getTime());
		}
		return timestamp;
	}

	public static java.util.Date convertFromXMLTime(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar == null) {
			return null;
		}

		return new java.util.Date(xmlCalendar.toGregorianCalendar().getTimeInMillis());
	}
	
	public static String timeBetween(java.util.Date endTime, java.util.Date startTime, String format) {
		if(endTime == null || startTime == null ){
			return "";
		}
		
		long diff = endTime.getTime() - startTime.getTime();
		
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		
		int seconds = 0;
		if(diffSeconds > 0) {
			seconds = Integer.parseInt(String.valueOf(diffSeconds));
		} 
		
		int minutes = 0;
		if(diffMinutes > 0) {
			minutes = Integer.parseInt(String.valueOf(diffMinutes));
		} 
		
		int hours = 0;
		if(diffHours > 0) {
			hours = Integer.parseInt(String.valueOf(diffHours));
		} 
		
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, seconds);
		
		
		return  new SimpleDateFormat(format).format(cal.getTime());
	}

	public static java.util.Date getPostDate() {
		String setPostingDateTo = SysParamUtil.getValueAsString(PennantConstants.SET_POSTDATE_TO);
		java.util.Date postingDate = getAppDate();
		
		if (!StringUtils.equals(setPostingDateTo, SysParamUtil.Param.APP_DATE.getCode())) {
			postingDate = getAppDate();
		} else {
			postingDate = getAppValueDate();
		}
		
		return postingDate;
	}
	
	/**
	 * Returns the date of the month
	 * 
	 * @param month
	 *            (integer)
	 * @return Date
	 */
	public static Date getDate(int month) {

		month = month - 1;
		int year = getYear(getSysDate());
		int day = 01;

		return convert(new GregorianCalendar(year, month, day));
	}

}
