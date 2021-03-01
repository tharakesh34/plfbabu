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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.UserActivityLogDAO;
import com.pennant.backend.model.UserActivityLog;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class UserActivityLogDAOImpl extends BasicDao<UserActivityLog> implements UserActivityLogDAO {
	private static Logger logger = LogManager.getLogger(UserActivityLogDAOImpl.class);

	public UserActivityLogDAOImpl() {
		super();
	}

	@Override
	public void save(UserActivityLog userActivityLog) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" INSERT INTO Task_Log ");
		insertSql.append(
				" (Module, Reference, SerialNo, FromUser, RoleCode, Activity, ToUser, NextRoleCode, LogTime, ReassignedTime, Processed)");
		insertSql.append(
				" Values( :Module, :Reference, :SerialNo, :FromUser, :RoleCode, :Activity, :ToUser, :NextRoleCode, :LogTime, :ReassignedTime, :Processed)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userActivityLog);
		logger.debug("Leaving");
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	}

	@Override
	public void deleteByReference(String reference, String module) {
		logger.debug("Entering");
		UserActivityLog userAcitvity = new UserActivityLog();
		userAcitvity.setReference(reference);
		userAcitvity.setModule(module);
		StringBuilder selectSql = new StringBuilder("Delete from Task_Log ");
		selectSql.append(" Where Reference = :Reference and Module = :Module");
		logger.debug("deleteSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userAcitvity);
		this.jdbcTemplate.update(selectSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void saveList(List<UserActivityLog> logList) {
		logger.debug("Entering");
		int serialNo = 0;
		for (UserActivityLog userActivityLog : logList) {
			if (serialNo == 0) {
				StringBuilder selectSql = new StringBuilder();
				selectSql.append(
						" SELECT COALESCE(MAX(SerialNo),0) FROM Task_Log WHERE Module=:Module AND Reference=:Reference");
				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userActivityLog);
				logger.debug("selectSql: " + selectSql.toString());
				try {
					serialNo = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
				} catch (EmptyResultDataAccessException e) {
					logger.warn("Exception: ", e);
				}
			}
			serialNo = serialNo + 1;
			userActivityLog.setSerialNo(serialNo);
			save(userActivityLog);
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateFinStatus(String reference, String module) {
		logger.debug("Entering");
		UserActivityLog userAcitvity = new UserActivityLog();
		userAcitvity.setReference(reference);
		userAcitvity.setModule(module);
		userAcitvity.setProcessed(true);
		StringBuilder updateSql = new StringBuilder("Update Task_Log set Processed=:Processed");
		updateSql.append(" Where Reference = :Reference and Module = :Module");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userAcitvity);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public String getPreviousRole(String module, String reference, String role, String compareRole) {
		logger.debug(Literal.ENTERING);

		String result = "";

		StringBuilder sql = new StringBuilder("select rolecode, activity, nextrolecode");
		sql.append(" from task_log");
		sql.append(" where module = :module and reference = :reference");
		sql.append(" order by serialno desc");
		logger.debug("SQL: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("module", module);
		source.addValue("reference", reference);

		RowMapper<UserActivityLog> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(UserActivityLog.class);

		List<UserActivityLog> activities = jdbcTemplate.query(sql.toString(), source, typeRowMapper);

		// The previous role of compare role to be considered if one specified.	
		if (StringUtils.isNotEmpty(compareRole)) {
			role = compareRole;
		}

		for (UserActivityLog activity : activities) {
			if (StringUtils.equals(activity.getNextRoleCode(), role) && isForwardDirection(activity.getActivity())) {
				result = activity.getRoleCode();

				break;
			}
		}

		logger.debug(Literal.LEAVING);
		return result;
	}

	private boolean isForwardDirection(String activity) {
		if (StringUtils.contains(activity, "Saved") || StringUtils.contains(activity, "Reverted")
				|| StringUtils.contains(activity, "Resubmitted")) {
			return false;
		}

		return true;
	}
}
