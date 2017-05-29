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
 *																							*
 * FileName    		:  StoredProcedureUtil.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-09-2012    														*
 *                                                                  						*
 * Modified Date    :  21-09-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-09-2012       Pennant	                 0.1                                            * 
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;


public class StoredProcedureUtil extends StoredProcedure {
	Map<String,Object> outParameterMap= null;
	
	public StoredProcedureUtil(DataSource dataSource, String sprocName, Map<String,Object> inParameterMap, Map<String,Object> outParameterMap) {
		super(dataSource, sprocName);
		
		this.outParameterMap = outParameterMap;
		/* Declare the input Parameters */
			for (Entry<String, Object> entry : inParameterMap.entrySet()) {
				declareParameter(new SqlParameter(entry.getKey(), (Integer) entry.getValue()));
			}
		/* Declare the output Parameters */
			for (Entry<String, Object> entry : outParameterMap.entrySet()) {
				declareParameter(new SqlOutParameter(entry.getKey(),(Integer) entry.getValue()));
			} 
		compile();
	}
	
	public Map<String, Object> execute() {
		
		 Map<String, Object> inputs = new HashMap<String, Object>();
		 for (Entry<String, Object> entry : outParameterMap.entrySet()) {
			 inputs.put(entry.getKey(), new Object());
		 }
		return super.execute(inputs);
	}

	public Map<String, Object> execute(String usrLogin) {
		return super.execute(usrLogin);
	}

}
