/**
 * 
 * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  ClusterHierarcheyDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-11-2018    														*
 *                                                                  						*
 * Modified Date    :  21-11-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-11-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.applicationmaster.ClusterHierarchyDAO;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ClusterHierarchey</code> with set of CRUD operations.
 */
public class ClusterHierarchyDAOImpl extends BasicDao<ClusterHierarchy> implements ClusterHierarchyDAO {
	private static Logger logger = LogManager.getLogger(ClusterHierarchyDAOImpl.class);

	public ClusterHierarchyDAOImpl() {
		super();
	}

	@Override
	public ClusterHierarchy getClusterHierarcheybyId(String entity, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL. 
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" entity,");

		sql.append(
				" Version, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from cluster_hierarchy");
		sql.append(type);
		sql.append(" Where entity = :entity");

		// Execute the SQL, binding the arguments. 
		logger.trace(Literal.SQL + sql.toString());

		ClusterHierarchy clusterHierarchey = new ClusterHierarchy();
		clusterHierarchey.setEntity(entity);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(clusterHierarchey);
		RowMapper<ClusterHierarchy> rowMapper = BeanPropertyRowMapper.newInstance(ClusterHierarchy.class);

		try {
			clusterHierarchey = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			clusterHierarchey = null;
		}

		logger.debug(Literal.LEAVING);

		return clusterHierarchey;
	}

	public ClusterHierarchy getClusterHierarchey(String entity, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("");

		sql.append("select DISTINCT entity from cluster_hierarchy");
		sql.append(type);
		sql.append(" Where entity = :entity");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("entity", entity);

		RowMapper<ClusterHierarchy> rowMapper = BeanPropertyRowMapper.newInstance(ClusterHierarchy.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	//This method will get all the records of given entity from database
	@Override
	public List<ClusterHierarchy> getClusterHierarcheyList(String entity, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" entity, clustertype, seqorder,");

		sql.append(
				" Version, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from cluster_hierarchy");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where entity = :entity");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ClusterHierarchy cHierarchey = new ClusterHierarchy();
		cHierarchey.setEntity(entity);
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cHierarchey);
		RowMapper<ClusterHierarchy> rowMapper = BeanPropertyRowMapper.newInstance(ClusterHierarchy.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public String save(ClusterHierarchy clusterHierarchey, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into cluster_hierarchy");
		sql.append(tableType.getSuffix());
		sql.append(" (entity, clusterType, seqOrder, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :entity, :clusterType, :seqOrder, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.batchUpdate(sql.toString(),
					SqlParameterSourceUtils.createBatch(clusterHierarchey.getClusterTypes().toArray()));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(clusterHierarchey.getEntity());
	}

	@Override
	public void update(ClusterHierarchy clusterHierarchey, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update cluster_hierarchy");
		sql.append(tableType.getSuffix());
		sql.append("  set seqOrder = :seqOrder, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where entity = :entity AND clusterType = :clusterType ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		//SqlParameterSource paramSource = new BeanPropertySqlParameterSource(clusterHierarchey);
		int[] recordCount = jdbcTemplate.batchUpdate(sql.toString(),
				SqlParameterSourceUtils.createBatch(clusterHierarchey.getClusterTypes().toArray()));
		//int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount.length == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ClusterHierarchy clusterHierarchey, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from cluster_hierarchy");
		sql.append(tableType.getSuffix());
		sql.append(" where entity = :entity");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(clusterHierarchey);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(String entity, String clusterType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "entity = :entity and clusterType=:clusterType ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("cluster_hierarchy", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("cluster_hierarchy_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "cluster_hierarchy_Temp", "cluster_hierarchy" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		paramSource.addValue("entity", entity);
		paramSource.addValue("clusterType", clusterType);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}

	@Override
	public boolean isDuplicateKey(String entity, int seqOrder, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "entity = :entity and seqOrder=:seqOrder ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("cluster_hierarchy", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("cluster_hierarchy_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "cluster_hierarchy_Temp", "cluster_hierarchy" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		paramSource.addValue("entity", entity);
		paramSource.addValue("seqOrder", seqOrder);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}
}
