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
 * FileName    		:  SecurityUserHierarchyDAOImpl.java    	                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  30-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  30-07-2011      Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.administration.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.administration.SecurityUserHierarchyDAO;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityUserHierarchy;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class SecurityUserHierarchyDAOImpl extends SequenceDao<SecurityUserHierarchy>
		implements SecurityUserHierarchyDAO {

	private static Logger logger = LogManager.getLogger(SecurityUserHierarchyDAOImpl.class);

	public SecurityUserHierarchyDAOImpl() {
		super();
	}

	@Override
	public void deleteUserHierarchy(long userId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("delete from user_hierarchy where UserId = :UserId");

		StringBuilder sql1 = new StringBuilder();
		sql1.append("delete from user_hierarchy where Reporting_To = :UserId");

		logger.trace(Literal.SQL + sql.toString());
		logger.trace(Literal.SQL + sql1.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UserId", userId);

		this.jdbcTemplate.update(sql.toString(), parameterSource);
		this.jdbcTemplate.update(sql1.toString(), parameterSource);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void deleteUserHierarchy(SecurityUserHierarchy securityUserHierarchy) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update cluster_hierarchy");
		sql.append("delete from user_hierarchy where user =: UserId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		int[] recordCount = jdbcTemplate.batchUpdate(sql.toString(),
				SqlParameterSourceUtils.createBatch(securityUserHierarchy.getUserHierarchys().toArray()));

		// Check for the concurrency failure.
		if (recordCount.length == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void saveUserHierarchy(List<SecurityUserHierarchy> userHeirarchys) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into user_Hierarchy");
		sql.append(" (UserId, Business_Vertical, Product, Fin_Type, Branch, Reporting_To, Depth");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append("values(:UserId, :BusinessVertical, :Product, :FinType, :Branch, :ReportingTo, :Depth");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId");
		sql.append(", :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(userHeirarchys.toArray()));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<SecurityUserHierarchy> getDownLevelUsers(SecurityUserHierarchy userHierarchy) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UserId", userHierarchy.getUserId());
		parameterSource.addValue("Depth", 0);

		StringBuilder sql = new StringBuilder();
		sql.append(" select * from (");
		sql.append("select UserId, Business_Vertical, Product, Fin_Type, Branch, Reporting_To, Depth");
		sql.append(" from user_hierarchy where");
		sql.append(" reporting_to = :UserId");

		if (userHierarchy.getBusinessVertical() != null) {
			sql.append(" and Business_Vertical = :Business_Vertical");
			parameterSource.addValue("Business_Vertical", userHierarchy.getBusinessVertical());
		}

		if (userHierarchy.getProduct() != null) {
			sql.append(" and Product = :Product");
			parameterSource.addValue("Product", userHierarchy.getProduct());
		}

		if (userHierarchy.getFinType() != null) {
			sql.append(" and Fin_Type = :Fin_Type");
			parameterSource.addValue("Fin_Type", userHierarchy.getFinType());
		}

		if (userHierarchy.getBranch() != null) {
			sql.append(" and Branch= :Branch");
			parameterSource.addValue("Branch", userHierarchy.getBranch());
		}

		sql.append(") t where Depth <> :Depth");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<SecurityUserHierarchy> rowMapper = BeanPropertyRowMapper.newInstance(SecurityUserHierarchy.class);

		try {
			return jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<SecurityUserHierarchy> getUpLevelUsers(SecurityUserHierarchy userHierarchy) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		//parameterSource.addValue("ReportingTo", userHierarchy.getReportingTo());
		parameterSource.addValue("UserId", userHierarchy.getUserId());
		parameterSource.addValue("Depth", 1);

		StringBuilder sql = new StringBuilder();

		//		sql.append(" select reporting_to,depth from user_Hierarchy where userid =");
		//		sql.append("(select userId from user_hierarchy where userId = :ReportingTo and depth = :Depth)");
		//		sql.append(" and depth <> 0");

		sql.append(" select reporting_to from user_hierarchy where userId = :UserId  and depth = :Depth");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<SecurityUserHierarchy> rowMapper = BeanPropertyRowMapper.newInstance(SecurityUserHierarchy.class);

		try {
			return jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void getUpLevelUser(SecurityUserHierarchy userHierarchy) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UserId", userHierarchy.getUserId());
		parameterSource.addValue("Depth", 1);

		StringBuilder sql = new StringBuilder();
		sql.append(" select reporting_to from user_hierarchy where userid = :UserId  and depth = :Depth");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<SecurityUserHierarchy> rowMapper = BeanPropertyRowMapper.newInstance(SecurityUserHierarchy.class);

		try {
			jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

	}

	@Override
	public void updateUserHierarchy(SecurityUserHierarchy userHierarchy) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update user_Hierarchy");
		sql.append(" set depth = ");
		sql.append(" (select (max(depth)-1) as depth from user_hierarchy where reporting_to =");
		sql.append(
				" (select reporting_to from user_hierarchy where userId = :UserId and reporting_to <> :UserId)) where userId in");
		sql.append(" (select userId from user_Hierarchy where reporting_to =");
		sql.append(
				" (select reporting_to from user_hierarchy where userId = :UserId and reporting_to <> :UserId and depth <> 0)");
		sql.append(" and userId <> :UserId");
		sql.append(" and depth <> 0)");
		sql.append(" and userId <> :UserId");
		sql.append(" and reporting_to <> :UserId");
		sql.append(" and depth <> 0");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userHierarchy);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateUserHierarchy(SecurityUserHierarchy userHierarchy, ReportingManager reportingManager) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UserId", userHierarchy.getUserId());
		parameterSource.addValue("reportingTo", reportingManager.getReportingTo());

		StringBuilder sql = new StringBuilder("update user_Hierarchy");
		sql.append(" set depth = depth -1");
		sql.append(" where UserId = :UserId and reporting_to = :reportingTo");

		logger.trace(Literal.SQL + sql.toString());
		//SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userHierarchy);

		//this.jdbcTemplate.update(sql.toString(), beanParameters);
		this.jdbcTemplate.update(sql.toString(), parameterSource);
	}

}
