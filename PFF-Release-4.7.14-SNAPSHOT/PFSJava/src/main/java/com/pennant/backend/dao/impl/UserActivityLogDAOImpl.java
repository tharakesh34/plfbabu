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
 * FileName    		:  UserActivityLogDAOImpl.java											*                           
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

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.UserActivityLogDAO;
import com.pennant.backend.model.UserActivityLog;

public class UserActivityLogDAOImpl implements UserActivityLogDAO {

	private static Logger logger = Logger.getLogger(UserActivityLogDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public UserActivityLogDAOImpl() {
		super();
	}
	
	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
    public void save(UserActivityLog userActivityLog) {
		logger.debug("Entering");
		
		StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO Task_Log " );
		insertSql.append(" (Module, Reference, SerialNo, FromUser, RoleCode, Activity, ToUser, NextRoleCode, LogTime, ReassignedTime, Processed)");
		insertSql.append(" Values( :Module, :Reference, :SerialNo, :FromUser, :RoleCode, :Activity, :ToUser, :NextRoleCode, :LogTime, :ReassignedTime, :Processed)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userActivityLog);
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
    }

	@Override
	public void deleteByReference(String reference, String module) {
		logger.debug("Entering");
		UserActivityLog userAcitvity= new UserActivityLog();
		userAcitvity.setReference(reference);
		userAcitvity.setModule(module);
		StringBuilder   selectSql = new StringBuilder("Delete from Task_Log ");
		selectSql.append(" Where Reference = :Reference and Module = :Module");
		logger.debug("deleteSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userAcitvity);
		this.namedParameterJdbcTemplate.update(selectSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
    public void saveList(List<UserActivityLog> logList) {
		logger.debug("Entering");
		int serialNo = 0;
		for (UserActivityLog userActivityLog : logList) {
			if(serialNo==0){
				StringBuilder selectSql = new StringBuilder();
				selectSql.append(" SELECT COALESCE(MAX(SerialNo),0) FROM Task_Log WHERE Module=:Module AND Reference=:Reference");
				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userActivityLog);
				logger.debug("selectSql: " + selectSql.toString());
				try {
					serialNo = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
				}catch (EmptyResultDataAccessException e) {
					logger.warn("Exception: ", e);
				}
			}
			serialNo = serialNo+1;
			userActivityLog.setSerialNo(serialNo);
	        save(userActivityLog);
        }
		logger.debug("Leaving");
    }

	@Override
    public void updateFinStatus(String reference, String module) {
		logger.debug("Entering");
		UserActivityLog userAcitvity= new UserActivityLog();
		userAcitvity.setReference(reference);
		userAcitvity.setModule(module);
		userAcitvity.setProcessed(true);
		StringBuilder updateSql = new StringBuilder("Update Task_Log set Processed=:Processed");
		updateSql.append(" Where Reference = :Reference and Module = :Module");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userAcitvity);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

}
