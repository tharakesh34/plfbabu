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
 * FileName    		:  NextIDViewMySALDaoImpl.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
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
package com.pennant.backend.dao.impl;

import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.backend.dao.NextidviewDAO;

public class NextIdViewMySQLDaoImpl implements NextidviewDAO {
	
	private static Logger logger = Logger.getLogger(NextIdViewMySQLDaoImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public NextIdViewMySQLDaoImpl() {
		super();
	}
	
	public long getNextId(String seqName) {
		String selectCountSql = 	"select * from "+seqName ;
		return this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(selectCountSql, Long.class);		
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * gets sequence number from 
	 */
	public long getSeqNumber(String seqName) {
		logger.debug("Entering");

		long count =0; 

		try {
			String selectCountSql = "select seqNo from "+seqName ;
			count = this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(selectCountSql, Long.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);

		}
		logger.debug("Leaving"+count);
		return count;
	}
	/**
	 * updates table
	 */
	public void setSeqNumber(String seqName,long seqNumber) {
		logger.debug("Entering");

		try {
			Map<String, Long> namedParamters=Collections.singletonMap("seqNumber", seqNumber);
			
			String updateSql = 	"update "+seqName+" set seqNo = :seqNumber" ;			
			this.namedParameterJdbcTemplate.update(updateSql,namedParamters);
		} catch (Exception e) {
			logger.error("Exception: ", e);

		}
		logger.debug("Leaving");
	}

	@Override
    public long getNextExtId(String seqName) {
		String selectCountSql = 	"select * from "+seqName ;
		return this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(selectCountSql, Long.class);		
	}	
}
