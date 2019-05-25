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
 * FileName    		:  ClusterDAOImpl.java                                                   * 	  
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
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.ClusterDAO;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>Cluster</code> with set of CRUD operations.
 */
public class ClusterDAOImpl extends SequenceDao<Cluster> implements ClusterDAO {
	private static Logger logger = Logger.getLogger(ClusterDAOImpl.class);

	public ClusterDAOImpl() {
		super();
	}

	@Override
	public Cluster getCluster(long Id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select Id, entity, code, clusterType, name, parent, parentType");

		if (type.contains("View")) {
			sql.append(", EntityDesc, ParentCode, ParentName");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Clusters");
		sql.append(type);
		sql.append(" Where Id = :Id");

		logger.trace(Literal.SQL + sql.toString());

		Cluster cluster = new Cluster();
		cluster.setId(Id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cluster);
		RowMapper<Cluster> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Cluster.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<Cluster> getClustersByEntity(String entity) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct clusterType from clusters where entity = :entity");
		sql.append(" order by clusterType Asc");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("entity", entity);

		RowMapper<Cluster> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Cluster.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public boolean isDuplicateKey(long Id, String entity, String code, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "entity = :entity AND code = :code AND Id = :Id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Clusters", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Clusters_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Clusters_Temp", "Clusters" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Id", Id);
		paramSource.addValue("entity", entity);
		paramSource.addValue("code", code);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(Cluster cluster, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into Clusters");
		sql.append(tableType.getSuffix());
		sql.append("(Id, entity, code, clusterType, name, parent, parentType");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :Id, :entity, :code, :clusterType, :name, :parent, :parentType");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (cluster.getId() == Long.MIN_VALUE) {
			cluster.setId(getNextValue("SeqClusters"));
			logger.debug("get NextID:" + cluster.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cluster);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(cluster.getId());
	}

	@Override
	public void update(Cluster cluster, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update Clusters");
		sql.append(tableType.getSuffix());
		sql.append(" set entity = :entity, code = :code, clusterType = :clusterType");
		sql.append(", name = :name, parent = :parent, parentType = :parentType");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :Id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cluster);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(Cluster cluster, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from Clusters");
		sql.append(tableType.getSuffix());
		sql.append(" where Id = :Id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cluster);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isChildsExists(Cluster cluster) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("select count(Id) from clusters");
		sql.append(" where parent = :Id and entity = :entity");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", cluster.getId());
		source.addValue("entity", cluster.getEntity());

		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION);
		}

		logger.debug(Literal.LEAVING);
		return false;
	}

	@Override
	public List<ClusterHierarchy> getClusterHierarcheyList(String entity) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from cluster_hierarchy");
		sql.append(" where entity =:entity order by seqorder desc");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("entity", entity);

		RowMapper<ClusterHierarchy> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ClusterHierarchy.class);

		try {
			return jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return new LinkedList<>();
	}
}
