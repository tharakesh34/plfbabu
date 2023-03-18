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
 * * FileName : CustomerPhoneNumberDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.financialSummary.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import com.pennant.backend.dao.finance.financialSummary.DueDiligenceDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DueDiligenceDetailsDAOImpl extends SequenceDao<DueDiligenceDetails> implements DueDiligenceDetailsDAO {
	private static Logger logger = LogManager.getLogger(DueDiligenceDetailsDAOImpl.class);

	public DueDiligenceDetailsDAOImpl() {
		super();
	}

	@Override
	public List<DueDiligenceDetails> getDueDiligenceDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.Id, t2.FinID, t2.FinReference, t1.ParticularId,t3.Particulars,t1.Status,t1.Remarks");
		sql.append(", t1.Version, t1.LastMntBy, t1.LastMnton, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from  DUE_DILIGENCES_TEMP t1");
		sql.append(" left join FinanceMain_TEMP t2 on t2.FinID =  t1.FinID");
		sql.append(" left join Due_Diligence_Checklist t3 on t3.id =  t1.ParticularId");
		sql.append(" Where t2.FinID = ?");
		sql.append(" UNION ALL");
		sql.append(" Select  t1.Id, t2.FinID, t2.FinReference, t1.ParticularId,t3.Particulars,t1.Status,t1.Remarks");
		sql.append(", t1.Version, t1.LastMntBy, t1.LastMnton, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId ");
		sql.append(" from due_diligences t1");
		sql.append(" left join FinanceMain t2 on t2.FinID =  t1.FinID");
		sql.append(" left join Due_Diligence_Checklist t3 on t3.id =  t1.ParticularId");
		sql.append(" Where not exists ( Select 1 from due_diligence_checklist_temp Where id = t1.id)");
		sql.append(" and t2.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<DueDiligenceDetails> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			DueDiligenceDetails ddd = new DueDiligenceDetails();

			ddd.setId(rs.getLong("Id"));
			ddd.setFinID(rs.getLong("FinID"));
			ddd.setFinReference(rs.getString("FinReference"));
			ddd.setParticularId(rs.getLong("ParticularId"));
			ddd.setParticulars(rs.getString("Particulars"));
			ddd.setStatus(rs.getString("Status"));
			ddd.setRemarks(rs.getString("Remarks"));
			ddd.setVersion(rs.getInt("Version"));
			ddd.setLastMntBy(rs.getLong("LastMntBy"));
			ddd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ddd.setRecordStatus(rs.getString("RecordStatus"));
			ddd.setRoleCode(rs.getString("RoleCode"));
			ddd.setNextRoleCode(rs.getString("NextRoleCode"));
			ddd.setTaskId(rs.getString("TaskId"));
			ddd.setNextTaskId(rs.getString("NextTaskId"));
			ddd.setRecordType(rs.getString("RecordType"));
			ddd.setWorkflowId(rs.getLong("WorkflowId"));

			return ddd;
		});

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getParticularId(), l2.getParticularId()))
				.collect(Collectors.toList());
	}

	@Override
	public void delete(DueDiligenceDetails dueDiligenceDetails, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From Due_Diligences");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ? and FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, dueDiligenceDetails.getId());
				ps.setLong(2, dueDiligenceDetails.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(DueDiligenceDetails diligenceDtls, String type) {
		if (diligenceDtls.getId() == Long.MIN_VALUE) {
			diligenceDtls.setId(getNextValue("SeqDUE_DILIGENCES"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into Due_Diligences");
		sql.append(type);
		sql.append("( Id, FinID, FinReference, ParticularId, Status, Remarks, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, diligenceDtls.getId());
				ps.setLong(index++, diligenceDtls.getFinID());
				ps.setString(index++, diligenceDtls.getFinReference());
				ps.setLong(index++, diligenceDtls.getParticularId());
				ps.setString(index++, diligenceDtls.getStatus());
				ps.setString(index++, diligenceDtls.getRemarks());
				ps.setInt(index++, diligenceDtls.getVersion());
				ps.setLong(index++, diligenceDtls.getLastMntBy());
				ps.setTimestamp(index++, diligenceDtls.getLastMntOn());
				ps.setString(index++, diligenceDtls.getRecordStatus());
				ps.setString(index++, diligenceDtls.getRoleCode());
				ps.setString(index++, diligenceDtls.getNextRoleCode());
				ps.setString(index++, diligenceDtls.getTaskId());
				ps.setString(index++, diligenceDtls.getNextTaskId());
				ps.setString(index++, diligenceDtls.getRecordType());
				ps.setLong(index, diligenceDtls.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return diligenceDtls.getId();
	}

	@Override
	public void update(DueDiligenceDetails diligenceDtls, String type) {
		StringBuilder sql = new StringBuilder("Update Due_Diligences");
		sql.append(type);
		sql.append(" Set ParticularId = ?, Status = ?, Remarks = ?, Version = ?");
		sql.append(", LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ? and FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, diligenceDtls.getParticularId());
			ps.setString(index++, diligenceDtls.getStatus());
			ps.setString(index++, diligenceDtls.getRemarks());
			ps.setInt(index++, diligenceDtls.getVersion());
			ps.setLong(index++, diligenceDtls.getLastMntBy());
			ps.setTimestamp(index++, diligenceDtls.getLastMntOn());
			ps.setString(index++, diligenceDtls.getRecordStatus());
			ps.setString(index++, diligenceDtls.getRoleCode());
			ps.setString(index++, diligenceDtls.getNextRoleCode());
			ps.setString(index++, diligenceDtls.getTaskId());
			ps.setString(index++, diligenceDtls.getNextTaskId());
			ps.setString(index++, diligenceDtls.getRecordType());
			ps.setLong(index++, diligenceDtls.getWorkflowId());

			ps.setLong(index++, diligenceDtls.getId());
			ps.setLong(index++, diligenceDtls.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, diligenceDtls.getVersion());
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int getVersion(long id, long finID) {
		String sql = "Select Version FROM Due_Diligences Where ID = ? and FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, id, finID);
	}

	@Override
	public String getStatus(long id) {
		String sql = "Select status From Due_Diligence_Checklist Where id = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, String.class, id);
	}

}