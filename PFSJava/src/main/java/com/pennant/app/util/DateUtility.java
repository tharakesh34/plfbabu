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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link java.util.Calendar} and {@link java.util.Date} object.
 * </p>
 */
public final class DateUtility extends DateUtil {
	private static final Logger logger = LogManager.getLogger(DateUtility.class);

	/**
	 * Take String Date and return UTIL Date
	 * 
	 * @param date (Date)
	 * 
	 * @return Date
	 */
	public static java.util.Date getDate(String date) {
		return parseShortDate(date);
	}

	public static java.util.Date getDate(String date, String format) {
		return parse(date, format);
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
	 * @param date1 (Date)
	 * 
	 * @param date2 (Date)
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
	 * @param date (GregorianCalendar)
	 * 
	 * @return sql date
	 */
	public static Date convert(GregorianCalendar date) {
		return date == null ? null : new Date(date.getTime().getTime());
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
	 * @param date   (Date)
	 * 
	 * @param months (int)
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
	 * @param date  (Date)
	 * 
	 * @param years (int)
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
	 * @param date1 (Date)
	 * 
	 * @param date1 (Date)
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
		if (includeDate2 && getMonthEnd(date1)
				.compareTo(parse(format(date1, PennantConstants.DBDateFormat), PennantConstants.DBDateFormat)) == 0) {

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
	 * @param date1 (Date)
	 * 
	 * @param date1 (Date)
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
	 * @param date (Date)
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
	 * This method will return a value with the below format Ex:01-01-2020 so it will convert to 20001 (YY+DAYOFYEAR
	 * with left pad 000)
	 * 
	 * @return
	 */
	public static long getDateYYJDay() {
		Calendar curCalendar = Calendar.getInstance();
		curCalendar.setTime(SysParamUtil.getAppDate());

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
		return new SimpleDateFormat("HH:mm:ss")
				.format(convert(new GregorianCalendar(getYearsBetween(endTime, startTime),
						getMonthsBetween(endTime, startTime) % 12, getDay(endTime) - getDay(startTime),
						convert(endTime).get(Calendar.HOUR) - convert(startTime).get(Calendar.HOUR),
						convert(endTime).get(Calendar.MINUTE) - convert(startTime).get(Calendar.MINUTE),
						convert(endTime).get(Calendar.SECOND) - convert(startTime).get(Calendar.SECOND))));

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
				return parse("1900-01-01", PennantConstants.DBDateFormat);
			} else if (as400Date.equals(new BigDecimal(9999999))) {
				return parse("2049-12-31", PennantConstants.DBDateFormat);
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
		if (endTime == null || startTime == null) {
			return "";
		}

		long diff = endTime.getTime() - startTime.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;

		int seconds = 0;
		if (diffSeconds > 0) {
			seconds = Integer.parseInt(String.valueOf(diffSeconds));
		}

		int minutes = 0;
		if (diffMinutes > 0) {
			minutes = Integer.parseInt(String.valueOf(diffMinutes));
		}

		int hours = 0;
		if (diffHours > 0) {
			hours = Integer.parseInt(String.valueOf(diffHours));
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, seconds);

		return new SimpleDateFormat(format).format(cal.getTime());
	}

	/**
	 * Returns the date of the month
	 * 
	 * @param month (integer)
	 * @return Date
	 */
	public static Date getDate(int month) {

		month = month - 1;
		int year = getYear(getSysDate());
		int day = 01;

		return convert(new GregorianCalendar(year, month, day));
	}

	/**
	 * Returns the Previous year date
	 * 
	 * @param date (Date)
	 * @return Date
	 */
	public static Date getPreviousYearDate(java.util.Date date) {

		int month = getMonth(date) - 1;
		int year = getYear(date);
		int day = getDay(date);

		return convert(new GregorianCalendar(year - 1, month, day));

	}

	public static java.util.Date getDerivedAppDate() {
		java.util.Date appDate = SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_DATE.getCode());

		java.util.Date sysDate = null;
		String prodEnv = SysParamUtil.getValueAsString("IS_PROD_ENV");
		if (StringUtils.equals(prodEnv, PennantConstants.YES)) {
			sysDate = getSysDate();
		} else {
			sysDate = SysParamUtil.getValueAsDate("SYS_DATE");
		}

		if (compare(getMonthEnd(appDate), appDate) == 0 && DateUtility.compare(sysDate, appDate) > 0) {
			appDate = sysDate;

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(appDate);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			appDate = calendar.getTime();
		}

		return appDate;
	}

}
