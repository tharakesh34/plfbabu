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
 * FileName    		:  SecurityUserAccessDAOImpl.java    		                            * 	  
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

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.administration.SecurityUserAccessDAO;
import com.pennant.backend.model.administration.SecurityUserAccess;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class SecurityUserAccessDAOImpl extends SequenceDao<SecurityUserAccess> implements SecurityUserAccessDAO {

	private static Logger logger = Logger.getLogger(SecurityUserAccessDAOImpl.class);

	public SecurityUserAccessDAOImpl() {
		super();
	}

	/**
	 * This method is to Save SecurityUser Division Branch Details
	 */
	public void saveDivisionBranches(List<SecurityUserAccess> list) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into secUserAccess");
		sql.append(" (UsrId, Division, Branch, AccessType, Entity, ClusterId, ClusterType");
		sql.append(", ParentCluster, ParentClusterType");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append("values(:UsrId, :Division, :Branch, :AccessType, :Entity, :ClusterId, :ClusterType");
		sql.append(", :ParentCluster, :ParentClusterType");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(list.toArray()));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteDivisionBranchesByUser(long userId, String category) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		if ("UserAccess".equals(category)) {
			sql.append("delete from secUserAccess where UsrId = :UsrId");
		} else {
			sql.append("delete from SecurityUserDivBranch where UsrId = :UsrId");
		}

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		parameterSource.addValue("UsrId", userId);

		this.jdbcTemplate.update(sql.toString(), parameterSource);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Branch> getBranches() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select branchcode, entity, clusterId from rmtbranches");

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<Branch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Branch.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<Cluster> getClusters(String entity, String clusterType, Long clusterId) {
		logger.debug(Literal.ENTERING);

		int size = getChilds(entity, clusterType);

		StringBuilder sql = new StringBuilder();
		for (int i = 0; i <= size; i++) {
			if (size == 0) {
				sql.append("select * from clusters where clusterType =:clusterType and entity =:entity and id =:id");
				break;
			}
			if (i == 0) {
				sql.append("select * from clusters where parent in (");
			} else if (i == size) {
				sql.append("select Id from clusters c");
				sql.append(" where clusterType = :clusterType and entity =:entity and id =:id");
				for (int j = 0; j < size; j++) {
					sql.append(")");
				}
			} else {
				sql.append(" select Id from clusters where parent in (");
			}
		}

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("clusterType", clusterType);
		parameterSource.addValue("entity", entity);
		parameterSource.addValue("id", clusterId);

		RowMapper<Cluster> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Cluster.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	private int getChilds(String entity, String clusterType) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(clusterType) from cluster_hierarchy where seqorder >");
		sql.append("(select seqorder from cluster_hierarchy");
		sql.append(" where clusterType = :clusterType and entity=:entity)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("clusterType", clusterType);
		parameterSource.addValue("entity", entity);

		return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, Integer.class);
	}

	private Long getParentId(Long clusterId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select parent from clusters");
		sql.append(" where id = :id");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("id", clusterId);

		return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, Long.class);
	}

	@Override
	public void saveDivBranches(List<SecurityUserDivBranch> list) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into SecurityUserDivBranch");
		sql.append("(UsrID, UserDivision, UserBranch");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");

		sql.append("values(:UsrID, :UserDivision, :UserBranch");
		sql.append(", :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(list.toArray()));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteDivBranchDetails(SecurityUserDivBranch securityUserDivBranch) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From secUserAccess");
		sql.append(" Where UsrID = :UsrID And Division = :Division");

		StringBuilder sql1 = new StringBuilder("Delete From securityUserDivBranch");
		sql1.append(" Where UsrID = :UsrID And UserDivision = :Division");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("UsrID", securityUserDivBranch.getUsrID());
		parameterSource.addValue("Division", securityUserDivBranch.getUserDivision());

		logger.debug(Literal.SQL + sql.toString());
		logger.debug(Literal.SQL + sql1.toString());

		try {
			this.jdbcTemplate.update(sql.toString(), parameterSource);
			this.jdbcTemplate.update(sql1.toString(), parameterSource);

		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<SecurityUserAccess> getSecUserAccessByClusterId(Long clusterId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from secUserAccess ");
		sql.append(" where ClusterId = :ClusterId ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("ClusterId", clusterId);
		RowMapper<SecurityUserAccess> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SecurityUserAccess.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void deleteDivisionBranchesByBranchCodeAndUserId(String branchCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append("delete from SecurityUserDivBranch ");
		sql.append("where UserBranch = :UserBranch ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		parameterSource.addValue("UserBranch", branchCode);

		this.jdbcTemplate.update(sql.toString(), parameterSource);
		logger.debug(Literal.LEAVING);

	}

}
