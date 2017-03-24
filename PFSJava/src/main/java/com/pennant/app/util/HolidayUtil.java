/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  HolidayUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.smtmasters.HolidayDetail;
import com.pennant.backend.model.smtmasters.HolidayMaster;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link com.pennant.backend.model.smtmasters.HolidayMaster
 * HolidayMaster} and {@link com.pennant.backend.model.smtmasters.WeekendMaster WeekendMaster}.
 * </p>
 */
public final class HolidayUtil {
	/**
	 * Private constructor to hide the implicit public one.
	 */
	private HolidayUtil() {
		super();
	}

	/**
	 * Gets the set of holidays as per the specified normal and permanent holiday masters. The permanent holidays will
	 * be considered for all the years to which normal holiday masters provided.
	 * 
	 * @param normalHolidayMasters
	 *            The list of holiday masters for specific years.
	 * @param permanentHolidayMaster
	 *            The permanent holiday master that need to be considered for all the years to which normal holiday
	 *            masters provided.
	 * @return The set of holidays as specified by the normal and permanent holiday masters, or an empty Set if the list
	 *         of holiday masters is <code>null</code>.
	 */
	public static Set<Calendar> getHolidays(List<HolidayMaster> normalHolidayMasters,
			HolidayMaster permanentHolidayMaster) {
		Set<Calendar> holidays = new HashSet<Calendar>();

		if (normalHolidayMasters == null) {
			return holidays;
		}

		for (HolidayMaster holidayMaster : normalHolidayMasters) {
			holidays = HolidayUtil.addHolidays(holidays, holidayMaster);
			holidays = HolidayUtil.addHolidays(holidays, permanentHolidayMaster, holidayMaster.getHolidayYear());
		}

		return holidays;
	}

	/**
	 * Adds the holidays of the holiday master to the specified holidays set if it is not already present.
	 * 
	 * @param holidays
	 *            The set of holidays to which the the holidays of the holiday master to be added.
	 * @param holidayMaster
	 *            The holiday master whose holidays to be added to the specified holidays set.
	 * @return The set of holidays including the holidays of the holiday master.
	 */
	public static Set<Calendar> addHolidays(Set<Calendar> holidays, HolidayMaster holidayMaster) {
		return addHolidays(holidays, holidayMaster, null);
	}

	/**
	 * Adds the holidays of the holiday master to the specified holidays set if it is not already present.
	 * 
	 * @param holidays
	 *            The set of holidays to which the the holidays of the holiday master to be added.
	 * @param holidayMaster
	 *            The holiday master whose holidays to be added to the specified holidays set.
	 * @param year
	 *            The year for which the holiday master to be considered in case of permanent holidays. The holiday
	 *            master will be considered for normal holidays if <code>null</code>.
	 * @return The set of holidays including the holidays of the holiday master.
	 */
	public static Set<Calendar> addHolidays(Set<Calendar> holidays, HolidayMaster holidayMaster, BigDecimal year) {
		if (holidays == null) {
			holidays = new HashSet<Calendar>();
		}

		if (holidayMaster == null) {
			return holidays;
		}

		if (year == null) {
			year = holidayMaster.getHolidayYear();
		}

		for (HolidayDetail detail : holidayMaster.getHolidayList(year)) {
			holidays.add(detail.getHoliday());
		}

		return holidays;
	}

	/**
	 * Gets the early boundary for holiday calendar based on the specified date i.e., first day of the previous year to
	 * the given date.
	 * 
	 * @param date
	 *            The date for which the boundary required.
	 * @return The early boundary for holiday calendar based on the specified date.
	 */
	public static Calendar getEarlyBoundary(Calendar date) {
		return getEarlyBoundary(date.get(Calendar.YEAR));
	}

	/**
	 * Gets the early boundary for holiday calendar based on the specified year i.e., first day of the previous year to
	 * the given year.
	 * 
	 * @param year
	 *            The year for which the boundary required.
	 * @return The early boundary for holiday calendar based on the specified year.
	 */
	public static Calendar getEarlyBoundary(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year - 1, Calendar.JANUARY, 1, 0, 0, 0);

		return calendar;
	}

	/**
	 * Gets the late boundary for holiday calendar based on the specified date i.e., last day of the next year to the
	 * given date.
	 * 
	 * @param date
	 *            The date for which the boundary required.
	 * @return The late boundary for holiday calendar based on the specified date.
	 */
	public static Calendar getLateBoundary(Calendar date) {
		return getLateBoundary(date.get(Calendar.YEAR));
	}

	/**
	 * Gets the late boundary for holiday calendar based on the specified year i.e., last day of the next year to the
	 * given year.
	 * 
	 * @param year
	 *            The year for which the boundary required.
	 * @return The late boundary for holiday calendar based on the specified year.
	 */
	public static Calendar getLateBoundary(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year + 1, Calendar.DECEMBER, 31, 23, 59, 59);

		return calendar;
	}
}
