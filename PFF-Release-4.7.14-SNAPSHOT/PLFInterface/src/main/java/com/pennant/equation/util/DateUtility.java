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
 * FileName    		:  DateUtility.java													*                           
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
package com.pennant.equation.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.pennant.mq.util.InterfaceMasterConfigUtil;

/**
 * Used to specify Date type selection in <b>DateUtility</b> class.
 * 
 */
public final class DateUtility {
	private static final Logger logger = Logger.getLogger(DateUtility.class);
	
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

		if (date == null) {
			return null;
		}

		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException pe) {
			logger.warn("Exception: ", pe);
		}
		return uDate;
	}

	/**
	 * Format date
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formateDate(java.util.Date date, String dateFormat) {
		if (date == null) {
			return "";
		}
		String formatedDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		formatedDate = formatter.format(date);
		return formatedDate;
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
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public static java.util.Date convertDateFromAS400(BigDecimal as400Date) {
		if (as400Date != null) {
			if (BigDecimal.ZERO.equals(as400Date)) {
				return getUtilDate("1900-01-01", InterfaceMasterConfigUtil.DBDateFormat);
			} else if (as400Date.equals(new BigDecimal(9999999))) {
				return getUtilDate("2049-12-31", InterfaceMasterConfigUtil.DBDateFormat);
			} else {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				try {
					return df.parse(new BigDecimal(19000000).add(as400Date).toString());
				} catch (ParseException pe) {
					logger.warn("Exception: ", pe);
				}
			}
		}

		return null;
	}

	/**
	 * Method for convert given MQ date into required date format
	 * 
	 * @param mqDate
	 * @param format
	 * @return
	 */
	public static java.util.Date convertDateFromMQ(String mqDate, String format) {

		if (mqDate == null) {
			return null;
		}
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			String formatDate = formatter.format(new SimpleDateFormat(InterfaceMasterConfigUtil.MQDATE).parse(mqDate));
			return formatter.parse(formatDate);
		} catch (ParseException e) {
			logger.warn("Exception: ", e);
		}

		return null;
	}

	public static String getTodayDateTime() {
		String dateFormat = "MM/dd/yyyy hh:mm:ss";
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(dateFormat);
		Calendar objCalendar = Calendar.getInstance();
		return df.format(objCalendar.getTime());
	}
}
