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
 * FileName    		:  TimestampFormatterAdapter.java													*                           
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


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.pennant.backend.util.PennantConstants;


	 public class TimestampFormatterAdapter extends XmlAdapter<String, Timestamp> {
	        private final SimpleDateFormat dateFormat = new SimpleDateFormat(PennantConstants.dateTimeFormat);
	      
	        //this method is used to converting Timestamp to String for CXFWebServices
	        @Override
	        public Timestamp unmarshal(final String dateAsString) throws Exception {
	        	 Date date = dateFormat.parse(dateAsString);
	        	 Timestamp sq = new java.sql.Timestamp(date.getTime());
	            return sq;
	        }
	        //this method is used to converting String to Timestamp for CXFWebServices
	        @Override
	        public String marshal(final Timestamp date) throws Exception {
	            return dateFormat.format(date);
	        }
	    
}
