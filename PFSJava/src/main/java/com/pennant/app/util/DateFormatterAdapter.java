/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant CXF Webservices Application Framework. 
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
 * FileName    		:  DateFormatterAdapter.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  07-10-2016															*
 *                                                                  
 * Modified Date    :  07-10-2016															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-10-2016       Pennant	                 0.1                                            * 
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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.pennant.backend.util.PennantConstants;

public class DateFormatterAdapter extends XmlAdapter<String, Date> {
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(PennantConstants.XMLDateFormat);

	// this method is used to convert String to Date for CXFwebservices
	@Override
	public Date unmarshal(final String dateAsString) throws Exception {
		if (dateAsString != null && !dateAsString.trim().equals("")) {
			return dateFormat.parse(dateAsString);
		}
		return null;
	}

	// this method is used to convert Date to String for CXFWebServices
	@Override
	public String marshal(final Date date) throws Exception {
		if (date == null) {

		}
		return dateFormat.format(date);
	}
}