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

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Used to specify Date type selection in <b>DateUtility</b> class.
 *
 */
public final class DateUtility {


	/**
	 * Parsing Util Date in required Format
	 * 
	 * @param date (String)
	 *  
	 * @param format (String)
	 * 
	 * @return java.Util.Date
	 */
	public static java.util.Date getUtilDate(String date, String format){  
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate =null;
		try{
			uDate = df.parse(date);
		}
		catch(ParseException pe){
			pe.printStackTrace();
		}
		return uDate;
	}  

	/**
	 * Format Util Date in required Format
	 * 
	 * @param date (Date)
	 *  
	 * @param format (String)
	 * 
	 * @return String
	 */
	public static String formatDate(java.util.Date date, String format){
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date) + "";
	}  

}
