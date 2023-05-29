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
 * * FileName : CommitmentDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 * * Modified
 * Date : 25-03-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.commitment.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CommitmentMovement model</b> class.<br>
 * 
 */

public class CommitmentMovementDAOImpl extends BasicDao<CommitmentMovement> implements CommitmentMovementDAO {
	private static Logger logger = LogManager.getLogger(CommitmentMovementDAOImpl.class);

	public CommitmentMovementDAOImpl() {
		super();
	}

	@Override
	public CommitmentMovement getCommitmentMovement() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CommitmentMovement");
		CommitmentMovement commitmentMovement = new CommitmentMovement();
		if (workFlowDetails != null) {
			commitmentMovement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return commitmentMovement;
	}

	@Override
	public CommitmentMovement getNewCommitmentMovement() {
		CommitmentMovement commitmentMovement = getCommitmentMovement();
		commitmentMovement.setNewRecord(true);

		return commitmentMovement;
	}

	@Override
	public CommitmentMovement getCommitmentMovementById(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CmtReference, FinID, FinReference, FinBranch, FinType, MovementDate, MovementOrder, MovementType");
		sql.append(", MovementAmount, CmtAmount, CmtCharges, CmtUtilizedAmount, CmtAvailable, LinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From CommitmentMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CmtReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<CommitmentMovement> list = this.jdbcOperations.query(sql.toString(), (rs, i) -> {
			CommitmentMovement cm = new CommitmentMovement();

			cm.setCmtReference(rs.getString("CmtReference"));
			cm.setFinID(rs.getLong("FinID"));
			cm.setFinReference(rs.getString("FinReference"));
			cm.setFinBranch(rs.getString("FinBranch"));
			cm.setFinType(rs.getString("FinType"));
			cm.setMovementDate(rs.getTimestamp("MovementDate"));
			cm.setMovementOrder(rs.getLong("MovementOrder"));
			cm.setMovementType(rs.getString("MovementType"));
			cm.setMovementAmount(rs.getBigDecimal("MovementAmount"));
			cm.setCmtAmount(rs.getBigDecimal("CmtAmount"));
			cm.setCmtCharges(rs.getBigDecimal("CmtCharges"));
			cm.setCmtUtilizedAmount(rs.getBigDecimal("CmtUtilizedAmount"));
			cm.setCmtAvailable(rs.getBigDecimal("CmtAvailable"));
			cm.setLinkedTranId(rs.getLong("LinkedTranId"));
			cm.setVersion(rs.getInt("Version"));
			cm.setLastMntBy(rs.getLong("LastMntBy"));
			cm.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cm.setRecordStatus(rs.getString("RecordStatus"));
			cm.setRoleCode(rs.getString("RoleCode"));
			cm.setNextRoleCode(rs.getString("NextRoleCode"));
			cm.setTaskId(rs.getString("TaskId"));
			cm.setNextTaskId(rs.getString("NextTaskId"));
			cm.setRecordType(rs.getString("RecordType"));
			cm.setWorkflowId(rs.getLong("WorkflowId"));

			return cm;
		}, id);

		if (CollectionUtils.isNotEmpty(list)) {
			return list.stream().sorted((l1, l2) -> Long.compare(l2.getMovementOrder(), l1.getMovementOrder()))
					.collect(Collectors.toList()).get(0);
		}

		return null;
	}

	@Override
	public void delete(CommitmentMovement commitmentMovement, String type) {
		StringBuilder sql = new StringBuilder("Delete From CommitmentMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CmtReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(),
					ps -> ps.setString(1, commitmentMovement.getCmtReference()));

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void deleteByRef(String cmtReference, String type) {
		StringBuilder sql = new StringBuilder("Delete From CommitmentMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CmtReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setString(1, cmtReference));
	}

	@Override
	public String save(CommitmentMovement cm, String type) {
		StringBuilder sql = new StringBuilder("Insert Into CommitmentMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(CmtReference, FinID, FinReference, FinBranch, FinType, MovementDate, MovementOrder");
		sql.append(", MovementType, MovementAmount, CmtAmount, CmtCharges, CmtUtilizedAmount, CmtAvailable");
		sql.append(", LinkedTranId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, cm.getCmtReference());
			ps.setLong(index++, cm.getFinID());
			ps.setString(index++, cm.getFinReference());
			ps.setString(index++, cm.getFinBranch());
			ps.setString(index++, cm.getFinType());
			ps.setDate(index++, JdbcUtil.getDate(cm.getMovementDate()));
			ps.setLong(index++, cm.getMovementOrder());
			ps.setString(index++, cm.getMovementType());
			ps.setBigDecimal(index++, cm.getMovementAmount());
			ps.setBigDecimal(index++, cm.getCmtAmount());
			ps.setBigDecimal(index++, cm.getCmtCharges());
			ps.setBigDecimal(index++, cm.getCmtUtilizedAmount());
			ps.setBigDecimal(index++, cm.getCmtAvailable());
			ps.setLong(index++, cm.getLinkedTranId());
			ps.setInt(index++, cm.getVersion());
			ps.setLong(index++, cm.getLastMntBy());
			ps.setTimestamp(index++, cm.getLastMntOn());
			ps.setString(index++, cm.getRecordStatus());
			ps.setString(index++, cm.getRoleCode());
			ps.setString(index++, cm.getNextRoleCode());
			ps.setString(index++, cm.getTaskId());
			ps.setString(index++, cm.getNextTaskId());
			ps.setString(index++, cm.getRecordType());
			ps.setLong(index, cm.getWorkflowId());
		});

		return cm.getId();
	}

	@Override
	public void update(CommitmentMovement cm, String type) {
		StringBuilder sql = new StringBuilder("Update CommitmentMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set");
		sql.append(" FinID = ?, FinReference = ?, FinBranch = ?, FinType = ?, LinkedTranId = ?");
		sql.append(", MovementDate = ?, MovementOrder = ?, MovementType = ?, MovementAmount= ?");
		sql.append(", CmtAmount = ?, CmtCharges = ?, CmtUtilizedAmount = ?, CmtAvailable = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where CmtReference = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version= ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, cm.getFinID());
			ps.setString(index++, cm.getFinReference());
			ps.setString(index++, cm.getFinBranch());
			ps.setString(index++, cm.getFinType());
			ps.setLong(index++, cm.getLinkedTranId());
			ps.setDate(index++, JdbcUtil.getDate(cm.getMovementDate()));
			ps.setLong(index++, cm.getMovementOrder());
			ps.setString(index++, cm.getMovementType());
			ps.setBigDecimal(index++, cm.getMovementAmount());
			ps.setBigDecimal(index++, cm.getCmtAmount());
			ps.setBigDecimal(index++, cm.getCmtCharges());
			ps.setBigDecimal(index++, cm.getCmtUtilizedAmount());
			ps.setBigDecimal(index++, cm.getCmtAvailable());
			ps.setInt(index++, cm.getVersion());
			ps.setLong(index++, cm.getLastMntBy());
			ps.setTimestamp(index++, cm.getLastMntOn());
			ps.setString(index++, cm.getRecordStatus());
			ps.setString(index++, cm.getRoleCode());
			ps.setString(index++, cm.getNextRoleCode());
			ps.setString(index++, cm.getTaskId());
			ps.setString(index++, cm.getNextTaskId());
			ps.setString(index++, cm.getRecordType());
			ps.setLong(index++, cm.getWorkflowId());

			ps.setString(index++, cm.getCmtReference());
			if (!type.endsWith("_Temp")) {
				ps.setInt(index, cm.getVersion());
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int getMaxMovementOrderByRef(String cmtReference) {
		String sql = "Select coalesce(max(MovementOrder), 0) From CommitmentMovements Where CmtReference = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, cmtReference);
	}
}