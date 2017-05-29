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
 * FileName    		:  BusinessCalendar.java													*                           
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.smtmasters.HolidayDetail;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.util.PennantConstants;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.common.WorkingWeek;
import net.objectlab.kit.datecalc.jdk.CalendarKitCalculatorsFactory;

/**
 * A Business Calendar defines a set of holiday dates.
 */
public class BusinessCalendar implements Serializable {
	private static final long serialVersionUID = -4728201973665323130L;
	private static final Logger logger = Logger.getLogger(BusinessCalendar.class);

	private static WeekendMasterDAO weekendMasterDAO;
	private static HolidayMasterDAO holidayMasterDAO;

	private static WorkingWeek workingWeek = null;
	public static String WEEKEND_DESC = "Weekend";

	private static LoadingCache<String, HolidayCalendar<Calendar>> holidayCache = CacheBuilder.newBuilder()
			.maximumSize(10).expireAfterAccess(30, TimeUnit.MINUTES)
			.build(new CacheLoader<String, HolidayCalendar<Calendar>>() {
				@Override
				public HolidayCalendar<Calendar> load(String parameters) throws Exception {
					String[] paramValues = parameters.split(":");

					return getHolidayCalendar(paramValues[0]);
				}
			});
	private static LoadingCache<String, WeekendMaster> weekendCache = CacheBuilder.newBuilder().maximumSize(10)
			.expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<String, WeekendMaster>() {
				@Override
				public WeekendMaster load(String holidayCode) throws Exception {
					return getWeekendMaster(holidayCode);
				}
			});

	/**
	 * Initialization of <b>BusinessCalendar</b> class.
	 */
	public static void Init() {
		logger.debug("Entering");
		getWorkingWeek();
		logger.debug("Leaving");
	}

	/**
	 * Get Weekend Master details
	 * 
	 * @param weekendCode
	 *            (String)
	 * 
	 * @return WeekendMaster
	 */
	private static WeekendMaster getWeekendMaster(String weekendCode) {
		return weekendMasterDAO.getWeekendMasterByCode(weekendCode);
	}

	/**
	 * Get List of holidays from <b>WeekendMaster</b> class for specific weekends
	 * 
	 * @param holidayCode
	 *            (String)
	 * 
	 * @param holidayYear
	 *            (int)
	 * 
	 * @return list
	 * 
	 */
	public static List<HolidayDetail> getWeekendList(String holidayCode, int holidayYear) {
		logger.debug("Entering");
		List<HolidayDetail> holidayDetails = new ArrayList<HolidayDetail>();
		WeekendMaster weekendMaster = getWeekendMaster(holidayCode);
		Calendar calendar = getCurrentYear(holidayYear);

		for (int i = 0; i < HolidayUtil.getLateBoundary(holidayYear).get(Calendar.DAY_OF_YEAR); i++) {

			if (calendar.get(Calendar.YEAR) != holidayYear) {
				break;
			}

			if (StringUtils.contains(weekendMaster.getWeekend(), String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)))) {

				HolidayDetail holidayDetail = new HolidayDetail(holidayCode, weekendMaster.getWeekendDesc(),
						new BigDecimal(holidayYear), "N", calendar.get(Calendar.DAY_OF_YEAR), WEEKEND_DESC);
				holidayDetails.add(holidayDetail);

			}
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		logger.debug("Leaving");
		return holidayDetails;
	}

	/**
	 * Get Date From Holiday Calendar
	 * 
	 * @param holidayCode
	 *            (String)
	 * 
	 * @return calendar
	 * 
	 */
	private static HolidayCalendar<Calendar> getHolidayCalendar(String holidayCode) {
		logger.debug("Entering");

		HolidayCalendar<Calendar> holidayCalendar = fetchHolidays(holidayCode);

		logger.debug("Leaving");
		return holidayCalendar;
	}

	private static HolidayCalendar<Calendar> fetchHolidays(String holidayCode) {
		logger.debug("Entering");

		List<HolidayMaster> normalHolidayMasters = null;
		HolidayMaster permanentHolidayMaster = null;
		int startingYear = 0;
		int endingYear = 0;
		List<HolidayMaster> holidayList = holidayMasterDAO.getHolidayMasterCode(holidayCode);

		if (!holidayList.isEmpty()) {
			normalHolidayMasters = new ArrayList<HolidayMaster>();
			for (int i = 0; i < holidayList.size(); i++) {
				if (StringUtils.trimToEmpty(holidayList.get(i).getHolidayType()).equalsIgnoreCase(
						HolidayHandlerTypes.HOLIDAYTYPE_PERMINENT)) {
					permanentHolidayMaster = holidayList.get(i);
				} else {
					normalHolidayMasters.add(holidayList.get(i));
					int holidayYear = holidayList.get(i).getHolidayYear().intValue();

					if (i == 0) {
						startingYear = holidayYear;
						endingYear = holidayYear;
					} else {
						if (holidayYear < startingYear) {
							startingYear = holidayYear;
						}
						if (holidayYear > endingYear) {
							endingYear = holidayYear;
						}
					}
				}
			}
		}

		Set<Calendar> holidays = HolidayUtil.getHolidays(normalHolidayMasters, permanentHolidayMaster);
		HolidayCalendar<Calendar> holidayCalendar = null;
		if (!holidays.isEmpty()) {
			holidayCalendar = new DefaultHolidayCalendar<Calendar>(holidays, getCurrentYear(startingYear),
					HolidayUtil.getLateBoundary(endingYear));
		}

		logger.debug("Leaving");
		return holidayCalendar;
	}

	/**
	 * Get collection of holidays Map
	 * 
	 * @param holidayList
	 *            (List)
	 * 
	 * @param pHolidayMaster
	 *            (HolidayMaster)
	 * 
	 * @return calendar
	 * 
	 */
	private static Map<String, Boolean> getHolidayMap(List<HolidayMaster> holidayList) {
		logger.debug("Entering");
		Map<String, Boolean> holidayMap = null;
		if (holidayList != null) {
			holidayMap = new HashMap<String, Boolean>();
			for (int i = 0; i < holidayList.size(); i++) {
				BigDecimal holidayYear = holidayList.get(i).getHolidayYear();
				List<HolidayDetail> holidayDetails = holidayList.get(i).getHolidayList(holidayYear);
				if (holidayDetails == null) {
					continue;
				}
				for (int j = 0; j < holidayDetails.size(); j++) {
					String curDate = DateUtility.formatDate(holidayDetails.get(j).getHoliday().getTime(),
							PennantConstants.DBDateFormat);
					if (!holidayMap.containsKey(curDate)) {
						holidayMap.put(curDate, true);
					}
				}
			}
		}
		logger.debug("Leaving");
		return holidayMap;
	}

	/**
	 * Get Date from Calender
	 * 
	 * @param holidayCode
	 *            (String)
	 * 
	 * @param date
	 *            (Calendar)
	 * 
	 * @return calendar
	 * 
	 */
	private static DateCalculator<Calendar> getDateCalendar(String holidayCode, Calendar date, String handlerType) {
		logger.debug("Entering");
		getWorkingWeek();
		String holidayHandlerType = null;
		if (StringUtils.isBlank(handlerType)) {
			holidayHandlerType = HolidayHandlerType.FORWARD;
		} else {
			holidayHandlerType = handlerType;
		}
		HolidayCalendar<Calendar> holidayCalendar = null;
		try {
			holidayCalendar = holidayCache.get(holidayCode + ":" + handlerType);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from cache: ", e);
			holidayCalendar = getHolidayCalendar(holidayCode);

		} catch (InvalidCacheLoadException e) {
			logger.warn("Unable to load data from cache: ", e);
			holidayCalendar = getHolidayCalendar(holidayCode);

		}
		WorkingWeek week = null;

		if (holidayCalendar == null || !isInHolidayRange(date, holidayCalendar)) {
			Set<Calendar> holidays = new HashSet<Calendar>();
			holidayCalendar = new DefaultHolidayCalendar<Calendar>(holidays, HolidayUtil.getEarlyBoundary(date),
					HolidayUtil.getLateBoundary(date));
		}
		WeekendMaster master = null;
		try {
			master = weekendCache.get(holidayCode);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from cache: ", e);
			master = getWeekendMaster(holidayCode);
		}
		if (master != null && StringUtils.isNotBlank(master.getWeekend())) {
			String[] weekArray = master.getWeekend().split(",");
			week = workingWeek;
			for (int i = 0; i < weekArray.length; i++) {
				week = week.withWorkingDayFromCalendar(false, Integer.parseInt(weekArray[i]));
			}
		} else {
			week = workingWeek;
		}

		CalendarKitCalculatorsFactory.getDefaultInstance().registerHolidays(holidayCode, holidayCalendar);
		DateCalculator<Calendar> dateCalculator = CalendarKitCalculatorsFactory.getDefaultInstance().getDateCalculator(
				holidayCode, holidayHandlerType);

		if (week != null) {
			dateCalculator.setWorkingWeek(week);
		}
		logger.debug("Leaving");
		return dateCalculator;
	}

	/**
	 * Check the Date is in Range of Holidays
	 * 
	 * @param date
	 *            (Calendar)
	 * 
	 * @param holidayCalendar
	 *            (HolidayCalendar)
	 * 
	 * @return boolean
	 * 
	 */
	private static boolean isInHolidayRange(Calendar date, HolidayCalendar<Calendar> holidayCalendar) {

		if (date.before(holidayCalendar.getEarlyBoundary())) {
			return false;
		}

		if (date.after(holidayCalendar.getLateBoundary())) {
			return false;
		}

		return true;
	}

	/**
	 * Get the Calendar at Starting of the Year
	 */
	private static Calendar getCurrentYear(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, Calendar.JANUARY, 01);

		return calendar;
	}

	/**
	 * Get the Working week Details
	 */
	private static void getWorkingWeek() {
		if (workingWeek == null) {
			workingWeek = new WorkingWeek();
			workingWeek = workingWeek.withWorkingDayFromCalendar(true, Calendar.SATURDAY);
			workingWeek = workingWeek.withWorkingDayFromCalendar(true, Calendar.SUNDAY);
		}
	}

	public static Calendar getBusinessDate(String holidayCode, String nBDAction, Date date) {
		if (HolidayHandlerTypes.MOVE_NONE.equals(nBDAction)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(date.getTime());
			
			return calendar;
		}
		
		if (StringUtils.isBlank(holidayCode)) {
			holidayCode = HolidayHandlerTypes.getHolidayCode("");
		}

		String handlerType = getHandlerType(nBDAction);
		Calendar tempDate = Calendar.getInstance();
		tempDate.setTimeInMillis(date.getTime());

		DateCalculator<Calendar> dateCalculator = getDateCalendar(holidayCode, tempDate, handlerType);
		dateCalculator.setStartDate(tempDate);

		return dateCalculator.getCurrentBusinessDate();
	}

	private static String getHandlerType(String nbdAction) {
		if (nbdAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_NEXT)) {
			return HolidayHandlerType.FORWARD;
		} else if (nbdAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_NEXT_PREVIOUS)) {
			return HolidayHandlerType.MODIFIED_FOLLOWING;
		} else if (nbdAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_PREVIOUS)) {
			return HolidayHandlerType.BACKWARD;
		} else if (nbdAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_PREVIOUS_NEXT)) {
			return HolidayHandlerType.MODIFIED_PRECEDING;
		} else if (nbdAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_NEXT_NONE)) {
			return HolidayHandlerType.FORWARD_NOT_NEXT_MONTH;
		} else if (nbdAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_PREVIOUS_NONE)) {
			return HolidayHandlerType.BACKWARD_NOT_PREVIOUS_MONTH;
		} else if (nbdAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_NONE)) {
			return HolidayHandlerType.ACTUAL_DATE;
		} else {
			return HolidayHandlerType.FORWARD;
		}
	}

	/**
	 * Method for Getting Working Business Date either Previous or Next Date depends on action Performed
	 * 
	 * @param holidayCode
	 * @param handlerType
	 * @param date
	 * @return
	 */
	public static Calendar getWorkingBussinessDate(String holidayCode, String handlerType, Date date) {

		if (StringUtils.isBlank(holidayCode)) {
			holidayCode = HolidayHandlerTypes.getHolidayCode("");
		}

		List<HolidayMaster> holidayList = holidayMasterDAO.getHolidayMasterCode(holidayCode);

		boolean workingBussDateFound = false;
		Calendar tempDate = Calendar.getInstance();
		tempDate.setTime(date);

		// Save Details of Calendar Dates into Map(GregorianCalendar's are not compared correctly)
		Map<String, Boolean> holidayListMap = getHolidayMap(holidayList);
		if (holidayListMap == null) {
			tempDate.add(Calendar.DATE, 1);
			return tempDate;
		}

		while (!workingBussDateFound) {

			if (handlerType.equals(HolidayHandlerTypes.MOVE_NEXT)) {
				tempDate.add(Calendar.DATE, 1);
			} else if (handlerType.equals(HolidayHandlerTypes.MOVE_PREVIOUS)) {
				tempDate.add(Calendar.DATE, -1);
			}
			if (!holidayListMap.containsKey(DateUtility.formatDate(tempDate.getTime(), PennantConstants.DBDateFormat))) {
				workingBussDateFound = true;
			}
		}
		return tempDate;

	}

	// Clear holidayCache data.
	public static void clearHolidayCache(String holidayCode, String holidayType) {
		try {
			holidayCache.invalidate(holidayCode + ":" + holidayType);
		} catch (Exception ex) {
			logger.warn("Error clearing data from holidayCache cache: ", ex);
		}
	}

	// Clear weekendCache data.
	public static void clearWeekendCache(String holidayCode) {
		try {
			weekendCache.invalidate(holidayCode);
		} catch (Exception ex) {
			logger.warn("Error clearing data from weekendCache cache: ", ex);
		}
	}

	public void setWeekendMasterDAO(WeekendMasterDAO weekendMasterDAO) {
		BusinessCalendar.weekendMasterDAO = weekendMasterDAO;
	}

	public void setHolidayMasterDAO(HolidayMasterDAO holidayMasterDAO) {
		BusinessCalendar.holidayMasterDAO = holidayMasterDAO;
	}
}
