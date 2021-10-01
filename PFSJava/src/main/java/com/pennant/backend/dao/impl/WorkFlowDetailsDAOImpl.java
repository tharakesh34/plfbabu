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
 * FileName : WorkFlowDetailsDAOImpl.java *
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.WorkFlowDetailsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class WorkFlowDetailsDAOImpl extends SequenceDao<WorkFlowDetails> implements WorkFlowDetailsDAO {
	private static Logger logger = LogManager.getLogger(WorkFlowDetailsDAOImpl.class);

	private LoadingCache<Long, WorkFlowDetails> workflowCache = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<Long, WorkFlowDetails>() {

				@Override
				public WorkFlowDetails load(Long workFlowID) throws Exception {
					logger.debug("parameter >> {}", workFlowID);
					// Make the expensive call
					return loadWorkFlowDetails(workFlowID);
				}

			});

	public WorkFlowDetailsDAOImpl() {
		super();
	}

	public WorkFlowDetails getWorkFlowDetailsByID(long workFlowID) {
		try {
			return workflowCache.get(workFlowID);
		} catch (Exception e) {
			//
		}

		return loadWorkFlowDetails(workFlowID);
	}

	private WorkFlowDetails loadWorkFlowDetails(long workFlowID) {
		StringBuilder sql = getSelectSqlQuery();
		sql.append(" Where WorkFlowId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new WorklowDetailsRM(), workFlowID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	public WorkFlowDetails getWorkFlowDetailsByFlowType(String workFlowType, boolean api) {
		StringBuilder sql = getSelectSqlQuery();
		sql.append(" Where WorkFlowType = ? and WorkFlowActive= ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new WorklowDetailsRM(), workFlowType, 1);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	public List<WorkFlowDetails> getActiveWorkFlowDetails() {
		StringBuilder sql = getSelectSqlQuery();
		sql.append(" Where WorkFlowActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setInt(1, 1), new WorklowDetailsRM());
	}

	public long save(WorkFlowDetails wfd) {
		wfd.setId(getNextValue("SeqWorkFlowDetails"));

		StringBuilder sql = new StringBuilder("Insert into WorkFlowDetails (");
		sql.append(" WorkFlowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc, WorkFlowXml");
		sql.append(", WorkFlowRoles, FirstTaskOwner, WorkFlowActive, JsonDesign");
		sql.append(", Version, LastMntBy, LastMntOn, RoleCode, NextRoleCode, TaskId, NextTaskId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, wfd.getWorkFlowId());
			ps.setString(index++, wfd.getWorkFlowType());
			ps.setString(index++, wfd.getWorkFlowSubType());
			ps.setString(index++, wfd.getWorkFlowDesc());
			ps.setString(index++, wfd.getWorkFlowXml());
			ps.setString(index++, wfd.getWorkFlowRoles());
			ps.setString(index++, wfd.getFirstTaskOwner());
			ps.setBoolean(index++, wfd.isWorkFlowActive());
			ps.setString(index++, wfd.getJsonDesign());
			ps.setInt(index++, wfd.getVersion());
			ps.setLong(index++, wfd.getLastMntBy());
			ps.setTimestamp(index++, wfd.getLastMntOn());
			ps.setString(index++, wfd.getRoleCode());
			ps.setString(index++, wfd.getNextRoleCode());
			ps.setString(index++, wfd.getTaskId());
			ps.setString(index++, wfd.getNextTaskId());
		});

		return wfd.getWorkFlowId();
	}

	public void update(WorkFlowDetails wfd) {
		String sql = "Update WorkFlowDetails set WorkFlowActive= ?, Version = ?, LastMntBy= ?, LastMntOn= ? Where WorkFlowId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBoolean(index++, wfd.isWorkFlowActive());
			ps.setInt(index++, wfd.getVersion());
			ps.setLong(index++, wfd.getLastMntBy());
			ps.setTimestamp(index++, wfd.getLastMntOn());

			ps.setLong(index++, wfd.getWorkFlowId());
		});

		clearWorkflowCache(wfd.getWorkFlowId());
	}

	public void clearWorkflowCache(long id) {
		try {
			workflowCache.invalidate(String.valueOf(id));
		} catch (Exception ex) {
			logger.warn("Exception: ", ex);
		}
	}

	@Override
	public long getWorkFlowDetailsCountByID(long workFlowId) {
		String sql = "Select count(WorkFlowId) From WorkFlowDetails Where WorkFlowId = ? and WorkFlowActive = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, workFlowId, 1);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return 0;
	}

	@Override
	public int getWorkFlowDetailsVersionByID(long workFlowId) {
		String sql = "Select Version From WorkFlowDetails Where WorkFlowId = ? and WorkFlowActive = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, workFlowId, 1);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return 0;
	}

	@Override
	public boolean isworkFlowTypeExist(String workFlowType) {
		String sql = "Select count(WorkFlowId) From WorkFlowDetails Where WorkFlowType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Long.class, workFlowType) > 0;
	}

	private StringBuilder getSelectSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" WorkflowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc, WorkFlowXml, WorkFlowRoles");
		sql.append(", FirstTaskOwner, WorkFlowActive, Version, LastMntBy, LastMntOn, JsonDesign");
		sql.append(" From WorkFlowDetails");
		return sql;
	}

	private class WorklowDetailsRM implements RowMapper<WorkFlowDetails> {

		@Override
		public WorkFlowDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
			WorkFlowDetails wfd = new WorkFlowDetails();

			wfd.setWorkflowId(rs.getLong("WorkflowId"));
			wfd.setWorkFlowType(rs.getString("WorkFlowType"));
			wfd.setWorkFlowSubType(rs.getString("WorkFlowSubType"));
			wfd.setWorkFlowDesc(rs.getString("WorkFlowDesc"));
			wfd.setWorkFlowXml(rs.getString("WorkFlowXml"));
			wfd.setWorkFlowRoles(rs.getString("WorkFlowRoles"));
			wfd.setFirstTaskOwner(rs.getString("FirstTaskOwner"));
			wfd.setWorkFlowActive(rs.getBoolean("WorkFlowActive"));
			wfd.setVersion(rs.getInt("Version"));
			wfd.setLastMntBy(rs.getLong("LastMntBy"));
			wfd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			wfd.setJsonDesign(rs.getString("JsonDesign"));

			return wfd;
		}

	}
}
