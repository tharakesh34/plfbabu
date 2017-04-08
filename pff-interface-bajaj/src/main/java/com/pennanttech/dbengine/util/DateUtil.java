package com.pennanttech.dbengine.util;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtil {

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
	 * Returns the last date of the month
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return int
	 */
	public static Date getMonthEndDate(java.util.Date date) {

		int[] daysInAMonth = { 29, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
				31 };
		int month = getMonth(date);
		int year = getYear(date);
		int day = daysInAMonth[month];
		if (isLeapYear(year) && month == 2) {
			day++;
		}

		return convert(new GregorianCalendar(year, month - 1, day));
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
	 * Returns a {@link java.util.Date} object that represents the server time at which it was allocated.
	 * 
	 * @return A {@link java.util.Date} that represents the server time.
	 */
	public static java.util.Date getSysDate() {
		return new java.util.Date();
	}
	
	/**
	 * Returns current sqldate
	 * 	  
	 * @return java.sql.Date
	 */
	public static Date getSqlDate() {
		java.util.Date utilDate = new java.util.Date();
		return new Date(utilDate.getTime());
	}
	/**
	 * Returns previous month date the Date using current month date
	 * 
	 * @return java.util.Date
	 */
	public static java.util.Date getPrevMonthDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

}
