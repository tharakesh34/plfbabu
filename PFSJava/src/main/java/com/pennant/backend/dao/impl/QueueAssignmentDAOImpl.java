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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.StoredProcedureUtil;
import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.QueueAssignmentHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

public class QueueAssignmentDAOImpl implements QueueAssignmentDAO {
	private static Logger				logger	= Logger.getLogger(QueueAssignmentDAOImpl.class);

	private DataSource					dataSource;
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public QueueAssignmentDAOImpl() {
		super();
	}

	@Override
	public void save(QueueAssignment assignment) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("insert into Task_Assignments");
		sql.append(" (Module, UserId, UserRoleCode, AssignedCount, LastAssignedOn, ProcessedCount,");
		sql.append(" LastProcessedOn, UserActive, ManualAssign)");
		sql.append(" values (:Module, :UserId, :UserRoleCode, :AssignedCount, :LastAssignedOn, :ProcessedCount,");
		sql.append(" :LastProcessedOn, :UserActive, :ManualAssign)");
		logger.debug("SQL: " + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignment);

		this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		logger.debug("Leaving");
	}

	@Override
	public void update(QueueAssignment assignment) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("update Task_Assignments");
		sql.append(" set");
		if (assignment.isRecordProcessed()) {
			sql.append(" AssignedCount = (case when AssignedCount = 0 then 0");
			sql.append("   when AssignedCount > 0 then AssignedCount - :AssignedCount end),");
			sql.append(" ProcessedCount = ProcessedCount + :ProcessedCount");
		} else {
			sql.append(" AssignedCount = AssignedCount + :AssignedCount");
		}
		sql.append(" where Module = :Module and UserId = :UserId and UserRoleCode = :UserRoleCode");
		logger.debug("SQL: " + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignment);

		this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		logger.debug("Leaving");
	}

	@Override
	public boolean exists(QueueAssignment assignment) {
		logger.debug("Entering ");

		StringBuilder sql = new StringBuilder("select count(*) from Task_Assignments");
		sql.append(" where Module = :Module and UserId = :UserId and UserRoleCode = :UserRoleCode");
		logger.debug("SQL: " + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignment);

		long count = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, Long.class);

		logger.debug("Leaving");
		return count > 0 ? true : false;
	}

	@Override
	public void saveOrUpdate(List<QueueAssignment> assignments) {
		logger.debug("Entering");

		for (QueueAssignment assignment : assignments) {
			if (exists(assignment)) {
				update(assignment);
			} else {
				save(assignment);
			}
		}

		logger.debug("Leaving");
	}

	@Override
	public QueueAssignment getNewUserId(String module, String nextRoleCode, String userId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		QueueAssignment queueAssignment = new QueueAssignment();
		source.addValue("RoleCode", nextRoleCode);
		source.addValue("Module", module);
		source.addValue("UserId", Arrays.asList(userId.split(",")));

		StringBuilder selectSql = new StringBuilder(" SELECT UserId , lovDescQAUserId FROM ");
		selectSql.append(" (SELECT UsrId UserId, COALESCE(UserId, 0) lovDescQAUserId, ");
		selectSql
				.append("row_number() over (ORDER BY RcdCount, UsrId) row_num  FROM (SELECT COALESCE(AssignedCount, 0) RcdCount, T1.UsrId, T6.UserId ");
		selectSql.append("FROM SecUsers T1  INNER JOIN SecUserOperations T2 ON T1.UsrID=T2.UsrID INNER JOIN ");
		selectSql
				.append("SecOperations T3 ON T3.OprID = T2.OprID INNER JOIN SecOperationRoles T4 ON T4.OprID =T3.OprID INNER JOIN ");
		selectSql
				.append("SecRoles T5 ON T4.RoleID = T5.RoleID  LEFT OUTER JOIN  Task_Assignments T6 ON T1.UsrId =T6.UserId ");
		selectSql
				.append("AND T6.UserRoleCode=:RoleCode AND T6.Module=:Module  WHERE T5.RoleCd=:RoleCode AND T1.UsrId NOT IN (:UserId)");
		selectSql
				.append("AND T1.UsrEnabled = 1  GROUP BY  T6.AssignedCount, T1.UsrId, T6.UserId )T1)T where row_num <= 1");

		RowMapper<QueueAssignment> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(QueueAssignment.class);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			queueAssignment = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			e = null;
			throw new EmptyResultDataAccessException(PennantJavaUtil.getLabel("label_NoUsers_ToAssign"), 1);
		}
		if (queueAssignment.getLovDescQAUserId() == 0) {
			queueAssignment.setModule(module);
			queueAssignment.setUserRoleCode(nextRoleCode);
			queueAssignment.setAssignedCount(0);
			save(queueAssignment);
		}
		logger.debug("Leaving");
		return queueAssignment;
	}

	@Override
	public void updateUserCounts(String module, String increaseRoleCode, long increaseUserId, String decreaseRoleCode,
			long decreaseUserId, boolean resubmit, boolean updateUser) {
		logger.debug("Entering");
		//
		QueueAssignment queue = new QueueAssignment();
		queue.setModule(module);
		queue.setUserId(increaseUserId);
		queue.setUserRoleCode(increaseRoleCode);

		StringBuilder updateincreaseSql = new StringBuilder(
				" UPDATE Task_Assignments SET AssignedCount=AssignedCount+1, LastAssignedOn=:LastAssignedOn");
		if (resubmit) {
			updateincreaseSql.append(" , ProcessedCount=(CASE WHEN ProcessedCount > 0 THEN ProcessedCount-1 END)");
		}
		updateincreaseSql.append(" Where Module =:Module AND UserId=:UserId AND UserRoleCode=:UserRoleCode ");
		logger.debug("updateSql: " + updateincreaseSql.toString());

		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(queue);
		int increasedCount = this.namedParameterJdbcTemplate.update(updateincreaseSql.toString(), beanParams);
		logger.debug("Increased User Count " + increasedCount);

		//
		if (updateUser) {
			queue = new QueueAssignment();
			queue.setModule(module);
			queue.setUserId(decreaseUserId);
			queue.setUserRoleCode(decreaseRoleCode);
			queue.setLastProcessedOn(new Timestamp(System.currentTimeMillis()));
			StringBuilder updateDecreaseSql = new StringBuilder(" UPDATE Task_Assignments SET ");
			updateDecreaseSql.append(" LastProcessedOn=:LastProcessedOn");
			if (!resubmit) {
				updateDecreaseSql.append(" , ProcessedCount=ProcessedCount+1");
			}
			updateDecreaseSql.append(" ,AssignedCount= (CASE WHEN AssignedCount = 0 THEN 0");
			updateDecreaseSql.append(" WHEN AssignedCount > 0 THEN AssignedCount-1 END)");
			updateDecreaseSql.append(" Where Module =:Module AND UserId=:UserId AND UserRoleCode=:UserRoleCode");
			logger.debug("updateSql: " + updateDecreaseSql.toString());

			SqlParameterSource beanParams1 = new BeanPropertySqlParameterSource(queue);
			int decreasedCount = this.namedParameterJdbcTemplate.update(updateDecreaseSql.toString(), beanParams1);
			logger.debug("Decreased User Count " + decreasedCount);
		}

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
			new StoredProcedureUtil(this.dataSource, "SP_ReassignFinRecords", inputParamMap, outputParamMap)
					.execute(inputs);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			logger.info("Queue Assignment Updation failed");
		}
		logger.debug("Leaving");
	}

	@Override
	public List<QueueAssignment> getFinances(String nextUserId, String nextRoleCode, boolean isManual) {
		logger.debug("Entering ");
		nextUserId = StringUtils.isBlank(nextUserId) ? " " : nextUserId;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CurrentOwner", nextUserId);
		source.addValue("RoleCode", nextRoleCode);

		StringBuilder selectSql = new StringBuilder(
				"SELECT Module, COALESCE(UserId, 0) UserId, COALESCE(FromUserId,0) FromUserId, UserRoleCode, T1.FinReference Reference,");
		selectSql
				.append(" T1.FinType lovDescFinType, T5.FinTypeDesc lovDescFinTypeDesc, T1.CustId lovDescCustCIF, T1.FinAmount lovDescFinAmount, T3.CcyEditField lovDescEditField,");
		selectSql
				.append(" COALESCE(T2.Version,0) Version, T2.LastMntOn, COALESCE(T2.LastMntBy,0) LastMntBy,T2.RecordStatus, T2.RoleCode, T2.NextRoleCode, ");
		selectSql
				.append(" T2.TaskId, T2.NextTaskId, T2.RecordType, COALESCE(T2.WorkflowId,0) WorkflowId, T4.UsrFName LovDescUserName,T6.ActualOwner lovDescActualOwner");
		selectSql
				.append(" FROM FinanceMain_Temp T1 INNER JOIN Task_Owners T6 ON T1.FinReference = T6.Reference AND T6.Processed=0 INNER JOIN ");
		selectSql
				.append(" RMTCurrencies T3 ON T1.FinCcy= T3.CcyCode INNER JOIN RMTFinanceTypes T5 ON T1.FinType = T5.FinType LEFT OUTER JOIN");
		selectSql
				.append(" Task_Assignments_Temp T2 ON T1.FinReference = T2.Reference AND T2.UserRoleCode=T6.RoleCode LEFT OUTER JOIN SecUsers T4 ON T2.UserId = T4.UsrId ");
		selectSql.append(" Where T6.RoleCode=:RoleCode AND ");
		if (StringUtils.isBlank(nextUserId) || isManual) {
			selectSql.append(" T6.CurrentOwner=0");
		} else {
			selectSql.append(" T6.CurrentOwner=:CurrentOwner");
		}
		selectSql.append(" ORDER BY Module desc");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<QueueAssignment> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(QueueAssignment.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

	}

	@Override
	public void save(QueueAssignment queueAssignment, String tableType) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder(" INSERT INTO Task_Assignments_Temp ");
		insertSql.append(" (Module, UserId, FromUserId, UserRoleCode, Reference, ManualAssign,");
		insertSql
				.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values( :Module, :UserId, :FromUserId, :UserRoleCode, :Reference, :ManualAssign,");
		insertSql
				.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId) ");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignment);
		logger.debug("Leaving");
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}
	}

	/**
	 * This method updates the Record Task_Assignments_Temp. if Record not updated then throws DataAccessException with
	 * error 41004. update Task_Assignments Details by key Reference and Version
	 * 
	 * @param Task_Assignments_Temp
	 *            Details (queueAssignment)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(QueueAssignment queueAssignment, String tableType) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update Task_Assignments");
		updateSql.append(StringUtils.trimToEmpty(tableType));
		updateSql
				.append(" SET Module=:Module, UserId=:UserId, FromUserId=:FromUserId, UserRoleCode=:UserRoleCode,Reference=:Reference, ManualAssign=:ManualAssign,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql
				.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where Reference =:Reference ");
		if (!tableType.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignment);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void delete(QueueAssignment queueDetail, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From Task_Assignments");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql
				.append(" Where Module=:Module AND Reference=:Reference AND UserRoleCode=:UserRoleCode AND ManualAssign=:ManualAssign");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueDetail);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public QueueAssignmentHeader isNewRequest(String module, String userId, String userRoleCode, boolean isManual) {
		logger.debug("Entering");
		QueueAssignmentHeader queueAssignmentHeader = new QueueAssignmentHeader();
		queueAssignmentHeader.setModule(module);
		queueAssignmentHeader.setUserRoleCode(userRoleCode);
		queueAssignmentHeader.setManualAssign(isManual);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select * FROM  Task_Assignments_Header_Temp");
		selectSql.append(" Where Module =:Module AND UserRoleCode=:UserRoleCode AND ManualAssign=:ManualAssign");
		if (!isManual) {
			queueAssignmentHeader.setUserId(userId);
			selectSql.append(" AND UserId=:UserId");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignmentHeader);
		RowMapper<QueueAssignmentHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(QueueAssignmentHeader.class);

		try {
			queueAssignmentHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			return null;
		}
		logger.debug("Leaving");
		return queueAssignmentHeader;
	}

	@Override
	public void saveHeader(QueueAssignmentHeader queueAssignmentHeader, String tableType) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder(" INSERT INTO Task_Assignments_Header");
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql
				.append("(Module, UserId, UserRoleCode, AssignedCount, LastAssignedOn, ProcessedCount, LastProcessedOn, UserActive, ManualAssign,");
		insertSql
				.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
				.append(" Values( :Module, :UserId, :UserRoleCode, :AssignedCount, :LastAssignedOn, :ProcessedCount, :LastProcessedOn, :UserActive, :ManualAssign,");
		insertSql
				.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId) ");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignmentHeader);
		logger.debug("Leaving");
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}
	}

	@Override
	public void updateHeader(QueueAssignmentHeader queueAssignmentHeader, String tableType) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update Task_Assignments_Header");
		updateSql.append(StringUtils.trimToEmpty(tableType));
		updateSql.append(" SET UserId=:UserId,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql
				.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Module =:Module AND UserRoleCode=:UserRoleCode");
		if (!queueAssignmentHeader.isManualAssign()) {
			updateSql.append(" AND UserId=:UserId ");
		}
		if (!tableType.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignmentHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void deleteHeader(QueueAssignmentHeader queueAssignmentHeader) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("DELETE FROM  Task_Assignments_Header_Temp");
		updateSql.append("  Where Module =:Module AND UserRoleCode=:UserRoleCode AND ManualAssign=:ManualAssign");
		if (!queueAssignmentHeader.isManualAssign()) {
			updateSql.append(" AND UserId=:UserId");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(queueAssignmentHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<QueueAssignment> getQueueAssignmentList(String userId, String module, String userRoleCode) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("UserRoleCode", Arrays.asList(StringUtils.trimToEmpty(userRoleCode).split(",")));
		source.addValue("Module", module);
		source.addValue("UserId", Arrays.asList(userId.split(",")));
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select Module,UserId, UserRoleCode,");
		selectSql.append(" AssignedCount, LastAssignedOn, ProcessedCount, LastProcessedOn, UserActive");
		selectSql.append(" FROM  Task_Assignments");
		selectSql.append(" Where UserId IN (:UserId) AND Module =:Module AND UserRoleCode IN(:UserRoleCode)");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<QueueAssignment> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(QueueAssignment.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
}
