/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : UserActivityLogDAOImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.TaskOwnersDAO;
import com.pennant.backend.model.TaskOwners;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class TaskOwnersDAOImpl extends BasicDao<TaskOwners> implements TaskOwnersDAO {
	private static Logger logger = LogManager.getLogger(TaskOwnersDAOImpl.class);

	public TaskOwnersDAOImpl() {
		super();
	}

	@Override
	public void save(TaskOwners taskOwners) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" INSERT INTO Task_Owners ");
		insertSql.append(" (Reference, RoleCode, ActualOwner, CurrentOwner, Processed)");
		insertSql.append(" Values( :Reference, :RoleCode, :ActualOwner, :CurrentOwner, :Processed)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwners);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void update(TaskOwners taskOwners) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder(" UPDATE Task_Owners");

		if (taskOwners.getCurrentOwner() != taskOwners.getActualOwner()) {
			updateSql.append(" SET CurrentOwner=:CurrentOwner");
		} else {
			updateSql.append(" SET CurrentOwner=:CurrentOwner,ActualOwner=:ActualOwner");
		}
		updateSql.append(" WHERE Reference=:Reference AND RoleCode=:RoleCode");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwners);

		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void delete(TaskOwners taskOwners) {
		logger.debug("Entering ");
		StringBuilder deleteSql = new StringBuilder(" Delete From Task_Owners");
		deleteSql.append(" Where Reference =:Reference ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwners);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving ");
	}

	@Override
	public void saveOrUpdateList(List<TaskOwners> taskOwners) {
		logger.debug(Literal.ENTERING);

		for (TaskOwners taskOwner : taskOwners) {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwner);
			if (taskOwner.isNewRecord()) {
				syncRecord(taskOwner);
			} else {
				StringBuilder updateSql = new StringBuilder(" UPDATE Task_Owners SET Processed=:Processed,");
				updateSql.append(" CurrentOwner=:CurrentOwner,ActualOwner=:ActualOwner");
				updateSql.append(" WHERE Reference=:Reference AND RoleCode=:RoleCode");
				logger.debug("updateSql: " + updateSql.toString());

				int recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
				if (recordCount <= 0) {
					logger.debug("Unable to update");
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void syncRecord(TaskOwners taskOwner) {
		logger.debug(Literal.ENTERING);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From Task_Owners");
		selectSql.append(" Where Reference=:Reference AND RoleCode=:RoleCode AND ActualOwner=:ActualOwner");
		logger.debug("SelectSQL: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwner);
		int count = 0;
		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e.getMessage());
		}

		if (count == 0) {
			logger.debug("Insert");
			StringBuilder insertSql = new StringBuilder(" INSERT INTO Task_Owners ");
			insertSql.append(" (Reference, RoleCode, ActualOwner, CurrentOwner, Processed)");
			insertSql.append(" Values(  :Reference, :RoleCode, :ActualOwner, :CurrentOwner, :Processed)");
			logger.debug("insertSql: " + insertSql.toString());
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} else {
			logger.debug("Update");
			StringBuilder updateSql = new StringBuilder(" UPDATE Task_Owners SET Processed=:Processed,");
			updateSql.append(" CurrentOwner=:CurrentOwner ");
			updateSql.append(" WHERE Reference=:Reference AND RoleCode=:RoleCode AND ActualOwner=:ActualOwner");
			logger.debug("updateSql: " + updateSql.toString());
			this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateList(List<TaskOwners> taskOwners) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder(" UPDATE Task_Owners SET CurrentOwner=:CurrentOwner");
		updateSql.append(" WHERE Reference=:Reference AND RoleCode=:RoleCode AND ActualOwner=:ActualOwner)");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParams = SqlParameterSourceUtils.createBatch(taskOwners.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParams);
		logger.debug("Leaving");

	}

	@Override
	public TaskOwners getTaskOwner(String finReference, String roleCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select Reference, RoleCode, ActualOwner, CurrentOwner, Processed");
		sql.append(" from Task_Owners");
		sql.append(" where Reference = ? and RoleCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<TaskOwners>() {
				@Override
				public TaskOwners mapRow(ResultSet rs, int rowNum) throws SQLException {
					TaskOwners to = new TaskOwners();

					to.setReference(rs.getString("Reference"));
					to.setRoleCode(rs.getString("RoleCode"));
					to.setActualOwner(rs.getLong("ActualOwner"));
					to.setCurrentOwner(rs.getLong("CurrentOwner"));
					to.setProcessed(rs.getBoolean("Processed"));

					return to;
				}
			}, finReference, roleCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		logger.debug(Literal.LEAVING);
		return null;
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
		if (this.jdbcTemplate.queryForObject(selectSql.toString(), parameterMap, Integer.class) > 0) {
			return true;
		}
		selectSql = new StringBuilder("SELECT count(reference) From Task_Owners");
		selectSql.append(" Where Reference IN (:Reference) AND CurrentOwner=:CurrentOwner");
		logger.debug("selectSql: " + selectSql.toString());
		if (this.jdbcTemplate.queryForObject(selectSql.toString(), parameterMap, Integer.class) > 0) {
			return false;
		}
		return true;
	}

	@Override
	public String getUserRoleCodeByRefernce(long userId, String reference, List<String> userRoles) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RoleCode");
		sql.append(" from Task_Owners");
		sql.append(" Where Reference = ? and CurrentOwner = ? and Processed = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, reference, userId, 0);
		} catch (EmptyResultDataAccessException e) {
			Map<String, List<String>> usrRoles = new HashMap<String, List<String>>();
			usrRoles.put("UserRoles", userRoles);

			MapSqlParameterSource source = new MapSqlParameterSource();
			source.addValue("Reference", reference);
			source.addValues(usrRoles);

			sql = new StringBuilder("Select");
			sql.append(" RoleCode");
			sql.append(" from (Select RoleCode, row_number() over (");
			sql.append(" order by CurrentOwner desc)");
			sql.append(" row_num from Task_Owners");
			sql.append(" where Reference = :Reference and CurrentOwner = 0");
			sql.append(" and roleCode in (:UserRoles)) Task Where row_num <= 1");

			logger.trace(Literal.SQL + sql.toString());

			try {
				return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
			} catch (EmptyResultDataAccessException e1) {
				return null;
			}
		} catch (IncorrectResultSizeDataAccessException e) {
			List<String> list = null;
			try {
				list = this.jdbcOperations.queryForList(sql.toString(), String.class, reference, userId, 0);

				if (list != null && list.size() > 0) {
					return list.get(0);
				} else {
					return null;
				}

			} catch (EmptyResultDataAccessException e1) {
				return null;
			}
		}
	}

	@Override
	public List<TaskOwners> getTaskOwnerList(String reference, String roleCode) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("RoleCode", Arrays.asList(StringUtils.trimToEmpty(roleCode).split(",")));
		StringBuilder selectSql = new StringBuilder(
				"SELECT Reference, RoleCode, ActualOwner, CurrentOwner, Processed From Task_Owners");
		selectSql.append(" Where Reference=:Reference AND RoleCode IN (:RoleCode)");

		RowMapper<TaskOwners> typeRowMapper = BeanPropertyRowMapper.newInstance(TaskOwners.class);
		logger.debug("selectSql: " + selectSql.toString());
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public void deviationReject(String finreference, String roleCode, String nextRoleCode) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finreference);
		source.addValue("RoleCode", roleCode);
		source.addValue("NextRoleCode", nextRoleCode);
		source.addValue("Processed", 0);

		StringBuilder updateSql = new StringBuilder(
				"Update Task_Owners set Processed=:Processed where Reference=:Reference AND RoleCode=:RoleCode ");

		logger.debug("selectSql: " + updateSql.toString());
		this.jdbcTemplate.update(updateSql.toString(), source);

		StringBuilder deleteSql = new StringBuilder(
				"Delete from Task_Owners where Reference=:Reference AND RoleCode=:NextRoleCode ");

		logger.debug("selectSql: " + deleteSql.toString());
		this.jdbcTemplate.update(deleteSql.toString(), source);
	}

	// Reinstate Loan
	@Override
	public void updateTaskOwner(TaskOwners taskOwners, boolean baseRole) {
		StringBuilder sql = new StringBuilder("UPDATE Task_Owners");
		sql.append(" set Processed = :Processed");
		if (!baseRole) {
			sql.append(", ActualOwner = :ActualOwner");
			sql.append(", CurrentOwner = :CurrentOwner ");
		}
		sql.append(" where Reference = :Reference and RoleCode = :RoleCode");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taskOwners);

		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}
}
