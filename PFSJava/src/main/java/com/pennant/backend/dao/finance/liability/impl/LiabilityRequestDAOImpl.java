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
 * * FileName : LiabilityRequestDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-12-2015 * *
 * Modified Date : 31-12-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-12-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.liability.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.liability.LiabilityRequestDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>LiabilityRequest model</b> class.<br>
 * 
 */

public class LiabilityRequestDAOImpl extends SequenceDao<LiabilityRequest> implements LiabilityRequestDAO {
	private static Logger logger = LogManager.getLogger(LiabilityRequestDAOImpl.class);

	public LiabilityRequestDAOImpl() {
		super();
	}

	@Override
	public LiabilityRequest getLiabilityRequest() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LiabilityRequest");
		LiabilityRequest liabilityRequest = new LiabilityRequest();

		if (workFlowDetails != null) {
			liabilityRequest.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return liabilityRequest;
	}

	@Override
	public LiabilityRequest getNewLiabilityRequest() {
		LiabilityRequest liabilityRequest = getLiabilityRequest();
		liabilityRequest.setNewRecord(true);

		return liabilityRequest;
	}

	@Override
	public LiabilityRequest getLiabilityRequestById(long id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		LiabilityRequestRM rowMapper = new LiabilityRequestRM(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public LiabilityRequest getLiabilityRequestByFinReference(long finID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		LiabilityRequestRM rowMapper = new LiabilityRequestRM(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(LiabilityRequest lr, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinLiabilityReq");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			if (this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, lr.getId())) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(LiabilityRequest lr, String type) {
		if (lr.getId() <= 0) {
			lr.setId(getNextValue("SeqFinLiabilityReq"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinLiabilityReq");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, FinID, FinReference, FinEvent, InsPaidStatus, InsClaimAmount");
		sql.append(", InsClaimReason, InitiatedBy, Version , LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, NocDate)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, lr.getId());
			ps.setLong(index++, lr.getFinID());
			ps.setString(index++, lr.getFinReference());
			ps.setString(index++, lr.getFinEvent());
			ps.setString(index++, lr.getInsPaidStatus());
			ps.setBigDecimal(index++, lr.getInsClaimAmount());
			ps.setString(index++, lr.getInsClaimReason());
			ps.setLong(index++, lr.getInitiatedBy());
			ps.setInt(index++, lr.getVersion());
			ps.setLong(index++, lr.getLastMntBy());
			ps.setTimestamp(index++, lr.getLastMntOn());
			ps.setString(index++, lr.getRecordStatus());
			ps.setString(index++, lr.getRoleCode());
			ps.setString(index++, lr.getNextRoleCode());
			ps.setString(index++, lr.getTaskId());
			ps.setString(index++, lr.getNextTaskId());
			ps.setString(index++, lr.getRecordType());
			ps.setLong(index++, lr.getWorkflowId());
			ps.setDate(index++, JdbcUtil.getDate(lr.getNocDate()));
		});

		return String.valueOf(lr.getId());
	}

	@Override
	public void update(LiabilityRequest lr, String type) {
		StringBuilder sql = new StringBuilder("Update FinLiabilityReq");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set InsPaidStatus = ?, InsClaimAmount = ?, InsClaimReason = ?, InitiatedBy = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, lr.getInsPaidStatus());
			ps.setBigDecimal(index++, lr.getInsClaimAmount());
			ps.setString(index++, lr.getInsClaimReason());
			ps.setLong(index++, lr.getInitiatedBy());
			ps.setInt(index++, lr.getVersion());
			ps.setLong(index++, lr.getLastMntBy());
			ps.setTimestamp(index++, lr.getLastMntOn());
			ps.setString(index++, lr.getRecordStatus());
			ps.setString(index++, lr.getRoleCode());
			ps.setString(index++, lr.getNextRoleCode());
			ps.setString(index++, lr.getTaskId());
			ps.setString(index++, lr.getNextTaskId());
			ps.setString(index++, lr.getRecordType());
			ps.setLong(index++, lr.getWorkflowId());
			ps.setLong(index++, lr.getId());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public String getProceedingWorkflow(String finType, String finEvent) {
		String sql = "Select NextFinEvent From ProceedWorkflowType Where FinType = ? and FinEvent = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finType, finEvent);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getFinareferenceCount(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select count(ID)");
		sql.append(" From FinLiabilityReq");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID);
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, InitiatedBy, FinEvent, InsPaidStatus, InsClaimAmount, InsClaimReason");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinType, CustCIF, FinBranch, FinStartDate, NumberOfTerms");
			sql.append(", MaturityDate, FinCcy, FinAmount, CustShrtName, BranchDesc");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinLiabilityReq");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class LiabilityRequestRM implements RowMapper<LiabilityRequest> {
		private String type;

		public LiabilityRequestRM(String type) {
			this.type = type;
		}

		@Override
		public LiabilityRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
			LiabilityRequest lr = new LiabilityRequest();
			lr.setId(rs.getLong("Id"));
			lr.setFinID(rs.getLong("FinID"));
			lr.setFinReference(rs.getString("FinReference"));
			lr.setInitiatedBy(rs.getLong("InitiatedBy"));
			lr.setFinEvent(rs.getString("FinEvent"));
			lr.setInsPaidStatus(rs.getString("InsPaidStatus"));
			lr.setInsClaimAmount(rs.getBigDecimal("InsClaimAmount"));
			lr.setInsClaimReason(rs.getString("InsClaimReason"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				lr.setFinType(rs.getString("FinType"));
				lr.setCustCIF(rs.getString("CustCIF"));
				lr.setFinBranch(rs.getString("FinBranch"));
				lr.setFinStartDate(rs.getDate("FinStartDate"));
				lr.setNumberOfTerms(rs.getInt("NumberOfTerms"));
				lr.setMaturityDate(rs.getDate("MaturityDate"));
				lr.setFinCcy(rs.getString("FinCcy"));
				lr.setFinAmount(rs.getBigDecimal("FinAmount"));
				// lr.setCustShrtName(rs.getString("CustShrtName"));
				// lr.setBranchDesc(rs.getString("BranchDesc"));
			}

			lr.setVersion(rs.getInt("Version"));
			lr.setLastMntBy(rs.getLong("LastMntBy"));
			lr.setLastMntOn(rs.getTimestamp("LastMntOn"));
			lr.setRecordStatus(rs.getString("RecordStatus"));
			lr.setRoleCode(rs.getString("RoleCode"));
			lr.setNextRoleCode(rs.getString("NextRoleCode"));
			lr.setTaskId(rs.getString("TaskId"));
			lr.setNextTaskId(rs.getString("NextTaskId"));
			lr.setRecordType(rs.getString("RecordType"));
			lr.setWorkflowId(rs.getLong("WorkflowId"));

			return lr;
		}
	}
}