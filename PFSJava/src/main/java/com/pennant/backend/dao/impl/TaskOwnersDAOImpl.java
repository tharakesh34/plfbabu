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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.TaskOwnersDAO;
import com.pennant.backend.model.TaskOwners;
import com.pennanttech.pennapps.core.resource.Literal;

public class TaskOwnersDAOImpl implements TaskOwnersDAO {
	private static Logger logger = Logger.getLogger(TaskOwnersDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public TaskOwnersDAOImpl() {
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
    public void save(TaskOwners taskOwners) {
		logger.debug("Entering");
		
		StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO Task_Owners " );
		insertSql.append(" (Reference, RoleCode, ActualOwner, CurrentOwner, Processed)");
		insertSql.append(" Values( :Reference, :RoleCode, :ActualOwner, :CurrentOwner, :Processed)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwners);
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		}catch(DuplicateKeyException e){
			logger.debug("Exception: ", e);
		}
		logger.debug("Leaving");
    }

	@Override
    public void update(TaskOwners taskOwners) {
		logger.debug("Entering");
		StringBuilder updateSql = 	new StringBuilder(" UPDATE Task_Owners" );
		
		if(taskOwners.getCurrentOwner() != taskOwners.getActualOwner()){
			updateSql.append(" SET CurrentOwner=:CurrentOwner");
		}else{
			updateSql.append(" SET CurrentOwner=:CurrentOwner,ActualOwner=:ActualOwner");
		}
		updateSql.append(" WHERE Reference=:Reference AND RoleCode=:RoleCode");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwners);
		try {
			this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		}catch(DuplicateKeyException e){
			logger.debug("Exception: ", e);
		}
		logger.debug("Leaving");

	}

	@Override
	public void delete(TaskOwners taskOwners) {
		logger.debug("Entering ");
		StringBuilder deleteSql = new StringBuilder(" Delete From Task_Owners" );
		deleteSql.append(" Where Reference =:Reference ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwners);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			logger.debug("Exception: ", e);
		}
		logger.debug("Leaving ");
	}
	
	
	@Override
	public void saveOrUpdateList(List<TaskOwners> taskOwners) {
		logger.debug("Entering");
		for (TaskOwners taskOwner : taskOwners) {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwner);
			if(taskOwner.isNewRecord()){
/*				StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO Task_Owners " );
				insertSql.append(" (Reference, RoleCode, ActualOwner, CurrentOwner, Processed)");
				insertSql.append(" Values(  :Reference, :RoleCode, :ActualOwner, :CurrentOwner, :Processed)");
				logger.debug("insertSql: " + insertSql.toString());

				this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
*/				
				syncRecord(taskOwner);
				
			}else {
				logger.debug("Update");
				StringBuilder updateSql = 	new StringBuilder(" UPDATE Task_Owners SET Processed=:Processed,");
				updateSql.append(" CurrentOwner=:CurrentOwner,ActualOwner=:ActualOwner" );
				updateSql.append(" WHERE Reference=:Reference AND RoleCode=:RoleCode");
				logger.debug("updateSql: " + updateSql.toString());

				int recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
				if(recordCount <= 0){
					logger.debug("Unable to update");
				}
			}
		}
		logger.debug("Leaving");
	}
	
	private void syncRecord(TaskOwners taskOwner){
		logger.debug(Literal.ENTERING);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From Task_Owners");
		selectSql.append(" Where Reference=:Reference AND RoleCode=:RoleCode AND ActualOwner=:ActualOwner");
		logger.debug("SelectSQL: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwner);
		int count =0;
		try {
			count= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		}catch (EmptyResultDataAccessException e) {
		}
		
		if(count==0){
			logger.debug("Insert");
			StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO Task_Owners " );
			insertSql.append(" (Reference, RoleCode, ActualOwner, CurrentOwner, Processed)");
			insertSql.append(" Values(  :Reference, :RoleCode, :ActualOwner, :CurrentOwner, :Processed)");
			logger.debug("insertSql: " + insertSql.toString());
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		}else{
			logger.debug("Update");
			StringBuilder updateSql = 	new StringBuilder(" UPDATE Task_Owners SET Processed=:Processed,");
			updateSql.append(" CurrentOwner=:CurrentOwner " );
			updateSql.append(" WHERE Reference=:Reference AND RoleCode=:RoleCode AND ActualOwner=:ActualOwner");
			logger.debug("updateSql: " + updateSql.toString());
			this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		}
		logger.debug(Literal.LEAVING);
	}
	
	
	@Override
	public void updateList(List<TaskOwners> taskOwners) {
		logger.debug("Entering");
		StringBuilder updateSql = 	new StringBuilder(" UPDATE Task_Owners SET CurrentOwner=:CurrentOwner" );
		updateSql.append(" WHERE Reference=:Reference AND RoleCode=:RoleCode AND ActualOwner=:ActualOwner)");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParams = SqlParameterSourceUtils.createBatch(taskOwners.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParams);
		logger.debug("Leaving");

	}

	@Override
    public TaskOwners getTaskOwner(String finReference, String roleCode) {
		logger.debug("Entering");
		TaskOwners taskOwners = new TaskOwners();
		taskOwners.setReference(finReference);
		taskOwners.setRoleCode(roleCode);
		StringBuilder selectSql = new StringBuilder("SELECT Reference, RoleCode, ActualOwner, CurrentOwner, Processed From Task_Owners");
		selectSql.append(" Where Reference=:Reference AND RoleCode=:RoleCode");
		
		RowMapper<TaskOwners> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TaskOwners.class);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwners);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			taskOwners = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}
		return taskOwners;
    }

	@Override
    public boolean checkIfUserAlreadyAccessed(String finReferences, String selectedUser, String roleCode) {
		logger.debug("Entering");
		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("Reference", finReferences);
		parameterMap.put("ActualOwner", selectedUser);
		parameterMap.put("CurrentOwner", selectedUser);
		parameterMap.put("RoleCode", roleCode);
		
		StringBuilder selectSql = new StringBuilder("SELECT count(reference) From Task_Owners");
		selectSql.append(" Where Reference IN (:Reference) AND ActualOwner=:ActualOwner AND RoleCode=:RoleCode");
		logger.debug("selectSql: " + selectSql.toString());
		if(this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),parameterMap, Integer.class)>0){
			return true;
		}
		selectSql = new StringBuilder("SELECT count(reference) From Task_Owners");
		selectSql.append(" Where Reference IN (:Reference) AND CurrentOwner=:CurrentOwner");
		logger.debug("selectSql: " + selectSql.toString());
		if(this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),parameterMap, Integer.class)>0){
			return false;
		}
		return true;
    }

	@Override
    public String getUserRoleCodeByRefernce(long userId, String reference, List<String> userRoles) {
		logger.debug("Entering");
		String roleCode = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CurrentOwner", userId);
		source.addValue("Reference", reference);
		Map<String,List<String>> usrRoles =	new HashMap<String,List<String>>();
		usrRoles.put("UserRoles", userRoles);
		source.addValues(usrRoles);
		StringBuilder selectSql = new StringBuilder("SELECT RoleCode From Task_Owners");
		selectSql.append(" Where Reference=:Reference AND CurrentOwner=:CurrentOwner AND Processed=0");
		logger.debug("selectSql: " + selectSql.toString());
		try {
			roleCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			selectSql = new StringBuilder("SELECT RoleCode From (select RoleCode, row_number() over (order by CurrentOwner desc)");
			selectSql.append("row_num From Task_Owners where Reference=:Reference AND CurrentOwner=0 AND RoleCode in (:UserRoles)) Task Where row_num <= 1 ");
			logger.debug("selectSql: " + selectSql.toString());
			try {
				roleCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
			}catch (EmptyResultDataAccessException e1) {
				logger.warn("Exception: ", e1);
				return null;
			}
		}
		logger.debug("Leaving");
		return roleCode;
    }

	@Override
    public List<TaskOwners> getTaskOwnerList(String reference, String roleCode) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("RoleCode", Arrays.asList(StringUtils.trimToEmpty(roleCode).split(",")));
		StringBuilder selectSql = new StringBuilder("SELECT Reference, RoleCode, ActualOwner, CurrentOwner, Processed From Task_Owners");
		selectSql.append(" Where Reference=:Reference AND RoleCode IN (:RoleCode)");
		
		RowMapper<TaskOwners> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TaskOwners.class);
		logger.debug("selectSql: " + selectSql.toString());
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
    }

	@Override
	public void deviationReject(String finreference, String roleCode, String nextRoleCode) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finreference);
		source.addValue("RoleCode",roleCode);
		source.addValue("NextRoleCode",nextRoleCode);
		source.addValue("Processed",0);

		StringBuilder updateSql = new StringBuilder("Update Task_Owners set Processed=:Processed where Reference=:Reference AND RoleCode=:RoleCode ");

		logger.debug("selectSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		StringBuilder deleteSql = new StringBuilder("Delete from Task_Owners where Reference=:Reference AND RoleCode=:NextRoleCode ");

		logger.debug("selectSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
	}
}
