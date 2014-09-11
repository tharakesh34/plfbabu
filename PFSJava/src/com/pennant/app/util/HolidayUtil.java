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

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.common.WorkingWeek;
import net.objectlab.kit.datecalc.jdk.CalendarKitCalculatorsFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.smtmasters.HolidayDetail;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.util.PennantConstants;

/**
 * Used to specify Date type selection in <b>HolidayUtil</b> class.
 */
public class HolidayUtil implements Serializable {
	
    private static final long serialVersionUID = -4728201973665323130L;
	private final static Logger logger = Logger.getLogger(HolidayUtil.class);
	
	private static WeekendMasterDAO weekendMasterDAO;
	private static HolidayMasterDAO holidayMasterDAO;

	private static HashMap<String, WeekendMaster> weekendMap=new HashMap<String, WeekendMaster>();
	private static HashMap<String, HolidayMaster> holidayMap=new HashMap<String, HolidayMaster>();
	private static HashMap<String, HolidayCalendar<Calendar>> holidayCalendarMap=new HashMap<String, HolidayCalendar<Calendar>>();
	private static WorkingWeek workingWeek=null;

	/**
	 * Initialization of <b>HolidayUtil</b> class.
	 *
	 */
	public static void Init(){
		logger.debug("Entering");
		getWorkingWeek();
		weekendMap=new HashMap<String, WeekendMaster>();
		holidayMap= new HashMap<String, HolidayMaster>();
		holidayCalendarMap=new HashMap<String, HolidayCalendar<Calendar>>();
		logger.debug("Leaving");
	}
	
	/**
	 * Get Weekend Master details 
	 * 
	 * @param weekendCode (String)
	 * 
	 * @return WeekendMaster
	 */
	private static WeekendMaster getWeekendMaster(String weekendCode){
		logger.debug("Entering");
		WeekendMaster weekendMaster=null;
		if(weekendMap.containsKey(weekendCode)){
			weekendMaster =weekendMap.get(weekendCode);
		}else{
			weekendMaster =  getWeekendMasterDAO().getWeekendMasterByCode(weekendCode);
			weekendMap.put(weekendCode, weekendMaster);
		}
		logger.debug("Leaving");
		return weekendMaster;
	} 

	/**
	 * Get List of Holidays from <b>HolidayMaster</b> class
	 * 
	 * @param holidayCode (String)
	 * 
	 * @param  holidayYear (int)
	 * 
	 * @return map
	 *
	 */
	public static HashMap<Date, HolidayDetail> getHolidayList(String holidayCode,int holidayYear){
		logger.debug("Entering");
		List<HolidayMaster> holidayList =   getHolidayMasterDAO().getHolidayMasterCodeYear(holidayCode, new BigDecimal(holidayYear), "");
		HashMap<Date, HolidayDetail> holidayMap = new HashMap<Date, HolidayDetail>();
		
		HolidayMaster pHolidayMaster=null;
		HolidayMaster nHolidayMaster=null;
		
		for (int i = 0; i < holidayList.size(); i++) {
			if(holidayList.get(i).getHolidayType().equalsIgnoreCase("P")){
				pHolidayMaster=holidayList.get(i);
			}else{
				nHolidayMaster=holidayList.get(i);
			}
		}
		
		List<HolidayDetail>  list= null;
		if(nHolidayMaster!=null){
			list= nHolidayMaster.getHolidayList(new BigDecimal(holidayYear));
		}else{
			list =getWeekendList(holidayCode,holidayYear);
		}

		for (int i = 0; i < list.size(); i++) {
			holidayMap.put(list.get(i).getHoliDayDate(), list.get(i));
		}

		if(pHolidayMaster!=null){
			List<HolidayDetail>  pList = pHolidayMaster.getHolidayList(new BigDecimal(holidayYear));
			for (int i = 0; i < pList.size(); i++) {
				if(!holidayMap.containsKey(pList.get(i).getHoliDayDate())){
					Calendar cal=pList.get(i).getHoliday();
					if (holidayYear % 4 == 0  ){
						int month = cal.get(Calendar.MONTH);
						if(month>1){
							cal.add(Calendar.DATE, 1);
						}
					}
					holidayMap.put(cal.getTime(), pList.get(i));	
				}
			}
		}
		logger.debug("Leaving");
		return holidayMap;
	}

	/**
	 * Get List of holidays from <b>WeekendMaster</b> class for specific weekends
	 * 
	 * @param holidayCode (String)
	 * 
	 * @param  holidayYear (int)
	 * 
	 * @return list
	 *
	 */
	public static List<HolidayDetail> getWeekendList(String holidayCode,int holidayYear){
		logger.debug("Entering");
		List<HolidayDetail> holidayDetails = new ArrayList<HolidayDetail>();
		WeekendMaster weekendMaster = getWeekendMaster(holidayCode);
		Calendar calendar = getCurrentYear(holidayYear); 
		
		for (int i = 0; i < getYearEnd(holidayYear).get(Calendar.DAY_OF_YEAR); i++) {
			if(StringUtils.contains(weekendMaster.getWeekend(),String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)))){
				HolidayDetail holidayDetail = new HolidayDetail(holidayCode,weekendMaster.getWeekendDesc(),new BigDecimal(holidayYear),"N",
																calendar.get(Calendar.DAY_OF_YEAR),PennantConstants.WEEKEND_DESC) ;
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
	 * @param holidayCode (String)
	 * 
	 * @return calendar
	 *
	 */
	private static HolidayCalendar<Calendar> getHolidayCalendar(String holidayCode,String handlerType){
		logger.debug("Entering");
		if(!holidayCalendarMap.containsKey(holidayCode)){
			fetchHolidays(holidayCode,handlerType);
		}
		logger.debug("Leaving");
		return holidayCalendarMap.get(holidayCode);
	}  

	/**
	 * Get holiday details From Holiday Master  
	 * 
	 * @param holidayCode (String)
	 *
	 */
	private static void fetchHolidays(String holidayCode,String handlerType){
		logger.debug("Entering");
		List<HolidayMaster> normalList =   null;
		HolidayMaster pHolidayMaster=null;
		int startingYear =0;
		int endingYear =0;
		List<HolidayMaster> holidayList =   getHolidayMasterDAO().getHolidayMasterCode(holidayCode);

		if(holidayList!=null && holidayList.size()!=0){
			normalList =   new ArrayList<HolidayMaster>() ;
			for (int i = 0; i < holidayList.size(); i++) {
				if(holidayList.get(i).getHolidayType().equalsIgnoreCase("P")){
					pHolidayMaster=holidayList.get(i);
				}else{
					normalList.add(holidayList.get(i));
					int holidayYear = holidayList.get(i).getHolidayYear().intValue();
					
					if(i==0){
						startingYear =holidayYear;
						endingYear =holidayYear;
					}else{
						if(holidayYear<startingYear){
							startingYear=holidayYear;
						}
						if(holidayYear>endingYear){
							endingYear=holidayYear;
						}
					}
				}
			}
		}
		
		holidayMap.put(holidayCode, pHolidayMaster);
		Set<Calendar> holidays = getHolidays(normalList,pHolidayMaster);
		if(holidays!=null){
			final HolidayCalendar <Calendar> holidayCalendar = new DefaultHolidayCalendar<Calendar> (holidays, getCurrentYear(startingYear), getYearEnd(endingYear));
			holidayCalendarMap.put(holidayCode, holidayCalendar);
		}else{
			holidayCalendarMap.put(holidayCode, null);
		}
		logger.debug("Leaving");
	} 

	
	/**
	 * Get collection of holidays list
	 * 
	 * @param holidayList (List)
	 * 
	 * @param pHolidayMaster (HolidayMaster)
	 * 
	 * @return calendar
	 *
	 */
	private static Set<Calendar> getHolidays(List<HolidayMaster> holidayList,HolidayMaster pHolidayMaster){
		logger.debug("Entering");
		Set<Calendar> holidays=null;
		if(holidayList!=null){
			holidays = new HashSet<Calendar>();
			for (int i = 0; i < holidayList.size(); i++) {
				BigDecimal holidayYear =  holidayList.get(i).getHolidayYear();
				List<HolidayDetail> nHolidayDetails = holidayList.get(i).getHolidayList(holidayYear);
				for (int j = 0; j < nHolidayDetails.size(); j++) {
					if(!holidays.contains(nHolidayDetails.get(j).getHoliday())){
						holidays.add(nHolidayDetails.get(j).getHoliday());
					}
				}
				
				if(pHolidayMaster != null){
					List<HolidayDetail> pHolidayDetails =  pHolidayMaster.getHolidayList(holidayYear);

					for (int j = 0; j < pHolidayDetails.size(); j++) {
						if(!holidays.contains(pHolidayDetails.get(j).getHoliday())){
							holidays.add(pHolidayDetails.get(j).getHoliday());
						}
					}
				}
			}
		}
		logger.debug("Leaving");
		return holidays;
	} 
	
	/**
	 * Get collection of holidays Map
	 * 
	 * @param holidayList (List)
	 * 
	 * @param pHolidayMaster (HolidayMaster)
	 * 
	 * @return calendar
	 *
	 */
	private static Map<String,Boolean> getHolidayMap(List<HolidayMaster> holidayList){
		logger.debug("Entering");
		 Map<String,Boolean> holidayMap=null;
		if(holidayList!=null){
			holidayMap = new HashMap<String, Boolean>();
			for (int i = 0; i < holidayList.size(); i++) {
				BigDecimal holidayYear =  holidayList.get(i).getHolidayYear();
				List<HolidayDetail> holidayDetails = holidayList.get(i).getHolidayList(holidayYear);
				if(holidayDetails == null){
					continue;
				}
				for (int j = 0; j < holidayDetails.size(); j++) {
					String curDate = DateUtility.formatDate(holidayDetails.get(j).getHoliday().getTime(), PennantConstants.DBDateFormat);
					if(!holidayMap.containsKey(curDate)){
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
	 * @param holidayCode (String)
	 * 
	 * @param date (Calendar)
	 * 
	 * @return calendar
	 *
	 */
	public static DateCalculator<Calendar>  getDateCalendar(String holidayCode,Calendar date,String handlerType){
		logger.debug("Entering");
		
		getWorkingWeek();
		String holidayHandlerType=null;
		if(StringUtils.trimToEmpty(handlerType).equals("")){
			holidayHandlerType = HolidayHandlerType.FORWARD;
		}else{
			holidayHandlerType =handlerType;
		}
			
		HolidayCalendar <Calendar> holidayCalendar = getHolidayCalendar(holidayCode,handlerType);
		WorkingWeek week = null;
		
		if(holidayCalendar==null || !isInHolidayRange(date, holidayCalendar)){
			Set<Calendar> holidays  = new HashSet<Calendar>();
			if(holidayMap.containsKey(holidayCode)){
				HolidayMaster holidayMaster = holidayMap.get(holidayCode);
				if(holidayMaster!=null){
					holidays =perminentHolidays(holidayMaster.getHolidayList(new BigDecimal(date.get(Calendar.YEAR))));
				}
			}
				
			holidayCalendar =new DefaultHolidayCalendar<Calendar> (holidays, getYearStart(date.get(Calendar.YEAR)), getYearEnd(date.get(Calendar.YEAR)));
			
			WeekendMaster master =  getWeekendMaster(holidayCode);
			if(master!=null && !StringUtils.trimToEmpty(master.getWeekend()).equalsIgnoreCase("")){
				String[] weekArray = master.getWeekend().split(",");
				week = workingWeek;
				for (int i = 0; i < weekArray.length; i++) {
					week = week.withWorkingDayFromCalendar(true, Integer.parseInt(weekArray[i]));
				}
			}
		}

		CalendarKitCalculatorsFactory.getDefaultInstance().registerHolidays(holidayCode, holidayCalendar);
		DateCalculator<Calendar> dateCalculator = CalendarKitCalculatorsFactory.getDefaultInstance() .getDateCalculator(holidayCode, holidayHandlerType);
		
		if(week!=null){
			dateCalculator.setWorkingWeek(week);	
		}
		logger.debug("Leaving");
		return dateCalculator;
	}
	
	/**
	 * Check the Date has Holiday or not
	 * 
	 * @param holidayCode (String)
	 * 
	 * @param date (Date)
	 * 
	 * @return boolean
	 *
	 */
	public static boolean  isHoliday(String holidayCode,Date date){
		logger.debug("Entering");
		String handlerType = HolidayHandlerType.FORWARD;
		Calendar tempDate = Calendar.getInstance();
		tempDate.setTime(date);
		logger.debug("Leaving");
		return getDateCalendar(holidayCode, tempDate,handlerType).isNonWorkingDay(tempDate);
	}
	
	/**
	 * Check the Date is in Range of Holidays
	 * 
	 * @param date (Calendar)
	 * 
	 * @param holidayCalendar (HolidayCalendar)
	 * 
	 * @return boolean
	 *
	 */
	private static boolean isInHolidayRange(Calendar date,HolidayCalendar<Calendar> holidayCalendar){
		
		if(date.before(holidayCalendar.getEarlyBoundary())){
			return false;	
		}

		if(date.after(holidayCalendar.getLateBoundary())){
			return false;	
		}

		return true;
	}
	
	/**
	 * Get the Calendar at Starting of the Before Year
	 */
	private static Calendar getYearStart(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.set(year-1, Calendar.JANUARY, 01);
		return calendar ;
	}

	/**
	 * Get the Calendar at Starting of the Year
	 */
	private static Calendar getCurrentYear(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, Calendar.JANUARY, 01);
		return calendar ;
	}

	/**
	 * Get the Calendar at Ending of the Year
	 */
	private static Calendar getYearEnd(int year){
		Calendar  calendar= Calendar.getInstance();
		calendar.set(year+1, Calendar.DECEMBER, 31);
		return calendar;
	}

	/**
	 * Get the Working week Details
	 */
	private static void getWorkingWeek(){
		if(workingWeek==null){
			workingWeek = new WorkingWeek();
			workingWeek= workingWeek.withWorkingDayFromCalendar(true, 1);
			workingWeek= workingWeek.withWorkingDayFromCalendar(true, 7);
		}
	}	 
	
	public static Calendar  getBusinessDate(String holidayCode,String NBDAction,Date date){
		if(holidayCode.equals("") || holidayCode == null) {	
			holidayCode = "GEN";
		}
		String handlerType = getHandlerType(NBDAction);
		Calendar tempDate = Calendar.getInstance();
		tempDate.setTime(date);
		DateCalculator<Calendar> dateCalculator = getDateCalendar(holidayCode,tempDate,handlerType);
		dateCalculator.setStartDate(tempDate);
		return dateCalculator.getCurrentBusinessDate();
	}
	
	private static String getHandlerType(String NBDAction)
	{
		if (NBDAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_NEXT)) {
			return HolidayHandlerType.FORWARD;
		} else if (NBDAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_NEXT_PREVIOUS)) {
			return HolidayHandlerType.MODIFIED_FOLLOWING;
		} else if (NBDAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_PREVIOUS)) {
			return HolidayHandlerType.BACKWARD;
		} else if (NBDAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_PREVIOUS_NEXT)) {
			return HolidayHandlerType.MODIFIED_PRECEDING;
		} else if (NBDAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_NEXT_NONE)) {
			return HolidayHandlerType.FORWARD_NOT_NEXT_MONTH;
		} else if (NBDAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_PREVIOUS_NONE)) {
			return HolidayHandlerType.BACKWARD_NOT_PREVIOUS_MONTH;
		} else if (NBDAction.equalsIgnoreCase(HolidayHandlerTypes.MOVE_NONE)) {
			return HolidayHandlerType.ACTUAL_DATE;
		} else {
			return HolidayHandlerType.FORWARD;
		}
	}
	
	public static Calendar getNextBusinessDate(String holidayCode,String Action,Date date) {
		if(holidayCode.equals("") || holidayCode == null) {	
			holidayCode = "GEN";
		}
		String handlerType = getHandlerType(Action);
		int moveBy = (Action == HolidayHandlerTypes.MOVE_PREVIOUS ? -1:1);
		Calendar tempDate = Calendar.getInstance();
		tempDate.setTime(date);
		DateCalculator<Calendar> dateCalculator = getDateCalendar(holidayCode,tempDate,handlerType);
		dateCalculator.setStartDate(tempDate);
		if(dateCalculator.isNonWorkingDay(tempDate)) {
			return dateCalculator.getCurrentBusinessDate();
		} else {
			return dateCalculator.moveByBusinessDays(moveBy).getCurrentBusinessDate();
		}
	}
	
	/**
	 * Method for Getting Working Business Date either Previous or Next Date depends on action Performed
	 * @param holidayCode
	 * @param handlerType
	 * @param date
	 * @return
	 */
	public static Calendar getWorkingBussinessDate(String holidayCode,String handlerType,Date date) {
		
		List<HolidayMaster> holidayList =   getHolidayMasterDAO().getHolidayMasterCode(holidayCode);
		
		boolean workingBussDateFound = false;
		Calendar tempDate = Calendar.getInstance();
		tempDate.setTime(date);
		
		//Save Details of Calendar Dates into Map(GregorianCalendar's are not compared correctly)
		Map<String,Boolean> holidayListMap = getHolidayMap(holidayList);
		if(holidayListMap == null){
			tempDate.add(Calendar.DATE, 1);
			return tempDate;
		}
		
		while(!workingBussDateFound){

			if(handlerType.equals(HolidayHandlerTypes.MOVE_NEXT)){
				tempDate.add(Calendar.DATE, 1);
			}else if(handlerType.equals(HolidayHandlerTypes.MOVE_PREVIOUS)){
				tempDate.add(Calendar.DATE, -1);
			}
			if (!holidayListMap.containsKey(DateUtility.formatDate(tempDate.getTime(), PennantConstants.DBDateFormat))){
				workingBussDateFound = true;
			}
		}
		return tempDate;
		
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	private static Set<Calendar> perminentHolidays(List<HolidayDetail> holidayDetails){
		return new HashSet<Calendar>(); 
	}
	
	public static WeekendMasterDAO getWeekendMasterDAO() {
		return weekendMasterDAO;
	}
	public void setWeekendMasterDAO(WeekendMasterDAO weekendMasterDAO) {
		HolidayUtil.weekendMasterDAO = weekendMasterDAO;
	}
	
	public static HolidayMasterDAO getHolidayMasterDAO() {
		return holidayMasterDAO;
	}
	public void setHolidayMasterDAO(HolidayMasterDAO holidayMasterDAO) {
		HolidayUtil.holidayMasterDAO = holidayMasterDAO;
	}
	
}
