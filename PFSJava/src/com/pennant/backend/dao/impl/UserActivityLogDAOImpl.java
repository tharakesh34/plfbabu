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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.UserActivityLogDAO;
import com.pennant.backend.model.UserActivityLog;

public class UserActivityLogDAOImpl implements UserActivityLogDAO {

	private static Logger logger = Logger.getLogger(UserActivityLogDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
    public void save(UserActivityLog queueAssignment) {
		logger.debug("Entering");
		
		StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO UserActivityLog " );
		insertSql.append(" (Module, Reference, FromUser, RoleCode, ToUser, NextRoleCode, LogTime, ReassignedTime)");
		insertSql.append(" Values( :Module, :Reference, :FromUser, :RoleCode, :ToUser, :NextRoleCode, :LogTime, :ReassignedTime)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignment);
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
    }

	@Override
	public void deleteByReference(String reference, String module) {
		logger.debug("Entering");
		UserActivityLog userAcitvity= new UserActivityLog();
		userAcitvity.setReference(reference);
		userAcitvity.setModule(module);
		StringBuilder   selectSql = new StringBuilder("Delete from UserActivityLog ");
		selectSql.append(" Where Reference = :Reference and Module = :Module");
		logger.debug("deleteSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userAcitvity);
		this.namedParameterJdbcTemplate.update(selectSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
    public long getNextUserId(String module, String reference, String roleCode, boolean multipleRoles) {
		logger.debug("Entering");
		long userId = 0;
		UserActivityLog log = new UserActivityLog();
		log.setModule(module);
		log.setReference(reference);
		if(multipleRoles){
			log.setNextRoleCode(roleCode);
		}else {
			log.setRoleCode(roleCode);
		}
		StringBuilder selectSql = new StringBuilder("");
		if(multipleRoles){
			selectSql.append(" SELECT  TOP 1 T2.ToUser FROM SecUsers AS T1 INNER JOIN");
			selectSql.append(" UserActivityLog AS T2 ON T1.UsrId =T2.FromUser AND ");
			selectSql.append(" T2.NextRoleCode=:NextRoleCode ");
		}else {
			selectSql.append(" SELECT  TOP 1 T2.FromUser FROM SecUsers AS T1 INNER JOIN");
			selectSql.append(" UserActivityLog AS T2 ON T1.UsrId =T2.FromUser AND ");
			selectSql.append(" T2.RoleCode=:RoleCode ");
		}
		selectSql.append(" AND T2.Module=:Module AND T2.Reference=:Reference WHERE T1.UsrEnabled = 1 ORDER BY LogTime desc");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(log);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			userId = this.namedParameterJdbcTemplate.queryForLong(selectSql.toString(), beanParameters);
		}catch (EmptyResultDataAccessException e) {
			return userId;
		}
		return userId;
    }

	@Override
    public void save(List<UserActivityLog> logList) {
		logger.debug("Entering");
		
		StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO UserActivityLog " );
		insertSql.append(" (Module, Reference, FromUser, RoleCode, ToUser, NextRoleCode, LogTime, ReassignedTime)");
		insertSql.append(" Values( :Module, :Reference, :FromUser, :RoleCode, :ToUser, :NextRoleCode, :LogTime, :ReassignedTime)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters =SqlParameterSourceUtils.createBatch(logList.toArray());
		
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
    }

}
