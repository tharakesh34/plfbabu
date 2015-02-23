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

import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.StoredProcedureUtil;
import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.util.PennantConstants;

public class QueueAssignmentDAOImpl implements QueueAssignmentDAO {

	private static Logger logger = Logger.getLogger(QueueAssignmentDAOImpl.class);
	private DataSource dataSource;
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	@Override
    public void save(QueueAssignment queueAssignment) {
		logger.debug("Entering");
		StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO QueueAssignment (Module, UserId, RoleCode, " );
		insertSql.append(" AssignedRcdCount, LastAssignedOn, ProcessedRcdCount, LastProcessedOn, UserActive)");
		insertSql.append(" Values( :Module, :UserId, :RoleCode, :AssignedRcdCount, :LastAssignedOn, :ProcessedRcdCount, :LastProcessedOn, :UserActive)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignment);
		logger.debug("Leaving");
		try{
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		}catch(Exception e){
			logger.debug("Leaving"+e.getMessage());
		}
    }
	
	@Override
	public void update(List<QueueAssignment> queueAssignmentList, QueueAssignment queueAssignment) {
		logger.debug("Entering");
		StringBuilder  updateSql = 	new StringBuilder(" UPDATE QueueAssignment SET AssignedRcdCount=AssignedRcdCount-1" );
		updateSql.append(" Where Module =:Module AND UserId=:UserId AND RoleCode=:RoleCode ");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParams = SqlParameterSourceUtils.createBatch(queueAssignmentList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParams);
		
		if(queueAssignment != null) {
			updateSql = 	new StringBuilder(" UPDATE QueueAssignment SET AssignedRcdCount=AssignedRcdCount+1, ProcessedRcdCount=ProcessedRcdCount-1" );
			updateSql.append(" Where Module =:Module AND UserId=:UserId AND RoleCode=:RoleCode ");
			logger.debug("updateSql: " + updateSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignment);
			try{
				this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
			}catch(Exception e){
				logger.debug("Leaving"+e.getMessage());
			}
		}
		logger.debug("Leaving");
	}
	
	@Override
	public long getNewUserId(String module, String nextRoleCode, long userId) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder("SELECT TOP 1 UsrId as UserId, ISNULL(UserId,0) as lovDescQAUserId FROM (");
		selectSql.append(" SELECT ISNULL(AssignedRcdCount,0) RcdCount, T1.UsrId, T4.UserId");
		selectSql.append(" FROM SecUsers AS T1 INNER JOIN");
		selectSql.append(" SecUserRoles AS T2 ON T1.UsrID=T2.UsrID INNER JOIN");
		selectSql.append(" SecRoles AS T3 ON T2.RoleID = T3.RoleID LEFT OUTER JOIN");
		selectSql.append(" QueueAssignment AS T4 ON T1.UsrId =T4.UserId  AND T4.RoleCode='"+nextRoleCode+"' AND T4.Module='"+module+"'");
		selectSql.append(" WHERE T3.RoleCd='"+nextRoleCode+"' AND T1.UsrId!="+userId+" AND T1.UsrEnabled = 1");
		selectSql.append(" GROUP BY  T4.AssignedRcdCount, T1.UsrId, T4.UserId) T ORDER BY RcdCount,UsrId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new QueueAssignment());
		RowMapper<QueueAssignment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(QueueAssignment.class);
		logger.debug("selectSql: " + selectSql.toString());
		QueueAssignment queueAssignment =this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		if(queueAssignment.getLovDescQAUserId() == 0){
			queueAssignment.setModule(module);
			queueAssignment.setRoleCode(nextRoleCode);
			queueAssignment.setAssignedRcdCount(0);
			save(queueAssignment);
		}
		logger.debug("Leaving");
		return queueAssignment.getUserId();
	}

	@Override
    public List<QueueAssignment> getRecordCountByRoleCode() {
		logger.debug("Entering ");
		StringBuilder   selectSql = new StringBuilder("select sum(AssignedRcdCount) as AssignedRcdCount, RoleCode from QueueAssignment group by RoleCode" );
		logger.debug("selectSql: " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new QueueAssignment());
		RowMapper< QueueAssignment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				QueueAssignment.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	@Override
    public void saveorUpdateNextUserId(QueueAssignment queueAssignment) {
		logger.debug("Entering");
		StringBuilder  updateSql = 	new StringBuilder(" UPDATE QueueAssignment SET AssignedRcdCount=AssignedRcdCount-1" );
		updateSql.append(" Where Module =:Module AND UserId=:UserId AND RoleCode=:RoleCode ");
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(queueAssignment);
		int count = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParams);
		if(count ==0){
			save(queueAssignment);
		}
		logger.debug("Leaving");
    }

	@Override
	public void updateUserCounts(String module, String increaseRoleCode, long increaseUserId, String decreaseRoleCode, long decreaseUserId) {
		logger.debug("Entering");
		//
		QueueAssignment queue = new QueueAssignment();
		queue.setModule(module);
		queue.setUserId(increaseUserId);
		queue.setRoleCode(increaseRoleCode);
		
		StringBuilder  updateincreaseSql = 	new StringBuilder(" UPDATE QueueAssignment SET AssignedRcdCount=AssignedRcdCount+1, LastAssignedOn=:LastAssignedOn" );
		updateincreaseSql.append(" Where Module =:Module AND UserId=:UserId AND RoleCode=:RoleCode ");
		logger.debug("updateSql: " + updateincreaseSql.toString());

		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(queue);
		this.namedParameterJdbcTemplate.update(updateincreaseSql.toString(), beanParams);
		
		//
		
		queue = new QueueAssignment();
		queue.setModule(module);
		queue.setUserId(decreaseUserId);
		queue.setRoleCode(decreaseRoleCode);
		queue.setLastProcessedOn(new Timestamp(System.currentTimeMillis()));
		StringBuilder  updateDecreaseSql = 	new StringBuilder(" UPDATE QueueAssignment SET AssignedRcdCount=AssignedRcdCount-1,");
		updateDecreaseSql.append(" LastProcessedOn=:LastProcessedOn, ProcessedRcdCount=ProcessedRcdCount+1" );
		updateDecreaseSql.append(" Where Module =:Module AND UserId=:UserId AND RoleCode=:RoleCode AND AssignedRcdCount != 0");
		logger.debug("updateSql: " + updateDecreaseSql.toString());

		SqlParameterSource beanParams1 = new BeanPropertySqlParameterSource(queue);
		this.namedParameterJdbcTemplate.update(updateDecreaseSql.toString(), beanParams1);
		
		logger.debug("Leaving");
	}
	
	@Override
	public void updateUserCounts(String module, String roleCode, long userId) {
		logger.debug("Entering");
		QueueAssignment queue = new QueueAssignment();
		queue.setModule(module);
		queue.setUserId(userId);
		queue.setRoleCode(roleCode);
		queue.setLastProcessedOn(new Timestamp(System.currentTimeMillis()));
		StringBuilder  updateDecreaseSql = 	new StringBuilder(" UPDATE QueueAssignment SET AssignedRcdCount=AssignedRcdCount-1,");
		updateDecreaseSql.append(" LastProcessedOn=:LastProcessedOn, ProcessedRcdCount=ProcessedRcdCount+1" );
		updateDecreaseSql.append(" Where Module =:Module AND UserId=:UserId AND RoleCode=:RoleCode AND AssignedRcdCount != 0");
		logger.debug("updateSql: " + updateDecreaseSql.toString());

		SqlParameterSource beanParams1 = new BeanPropertySqlParameterSource(queue);
		this.namedParameterJdbcTemplate.update(updateDecreaseSql.toString(), beanParams1);
		
		logger.debug("Leaving");
	}

	@Override
	public void executeStoredProcedure(long userId) {
		logger.debug("Entering");
		try {
			Map<String, Object> inputParamMap = new LinkedHashMap<String, Object>();
			Map<String, Object> outputParamMap = new LinkedHashMap<String, Object>();
			inputParamMap.put("@USERID", Types.BIGINT);
			inputParamMap.put("@MODULE", Types.VARCHAR);

			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put("@USERID", userId);
			inputs.put("@MODULE", PennantConstants.WORFLOW_MODULE_FINANCE);
			new StoredProcedureUtil(this.dataSource, "SP_ReassignFinRecords", inputParamMap, outputParamMap).execute(inputs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Queue Assignment Updation failed");
		} finally {

		}
		logger.debug("Leaving");
	}
}
