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
 * * FileName : FinanceCheckListReferenceDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-12-2011
 * * * Modified Date : 08-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.lmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinanceCheckListReference model</b> class.<br>
 * 
 */

public class FinanceCheckListReferenceDAOImpl extends BasicDao<FinanceCheckListReference>
		implements FinanceCheckListReferenceDAO {
	private static Logger logger = LogManager.getLogger(FinanceCheckListReferenceDAOImpl.class);

	public FinanceCheckListReferenceDAOImpl() {
		super();
	}

	@Override
	public FinanceCheckListReference getFinanceCheckListReferenceById(String reference, long questionId, long answer,
			String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinReference = ? and  QuestionId = ? and Answer = ?");

		FinanceCheckListReferenceRowMapper rowMapper = new FinanceCheckListReferenceRowMapper(type);

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, reference, questionId, answer);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceCheckListReference> getCheckListByFinRef(final long finID, String showStageCheckListIds,
			String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ?");

		if (StringUtils.isNotBlank(showStageCheckListIds)) {
			String[] sscl = showStageCheckListIds.split(",");
			sql.append(" and QuestionId IN(");

			int i = 0;

			while (i < sscl.length) {
				sql.append(" ?,");
				i++;
			}

			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		}
		FinanceCheckListReferenceRowMapper rowMapper = new FinanceCheckListReferenceRowMapper(type);

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);

			if (StringUtils.isNotBlank(showStageCheckListIds)) {
				String[] showStageCheckList = showStageCheckListIds.split(",");

				for (String showStage : showStageCheckList) {
					ps.setLong(index++, Long.valueOf(showStage));
				}
			}
		}, rowMapper);
	}

	@Override
	public List<FinanceCheckListReference> getCheckListByFinRef(String finReference, String showStageCheckListIds,
			String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinReference = ?");

		if (StringUtils.isNotBlank(showStageCheckListIds)) {
			String[] sscl = showStageCheckListIds.split(",");
			sql.append(" and QuestionId IN(");

			int i = 0;

			while (i < sscl.length) {
				sql.append(" ?,");
				i++;
			}

			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		}
		FinanceCheckListReferenceRowMapper rowMapper = new FinanceCheckListReferenceRowMapper(type);

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);

			if (StringUtils.isNotBlank(showStageCheckListIds)) {
				String[] showStageCheckList = showStageCheckListIds.split(",");

				for (String showStage : showStageCheckList) {
					ps.setLong(index++, Long.valueOf(showStage));
				}
			}
		}, rowMapper);
	}

	@Override
	public void delete(FinanceCheckListReference fclr, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinanceCheckListRef");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and QuestionId = ? and Answer = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fclr.getFinID());
				ps.setLong(index++, fclr.getQuestionId());
				ps.setLong(index, fclr.getAnswer());
			});
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	public void delete(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinanceCheckListRef");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, finID));
	}

	public void delete(String Reference, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinanceCheckListRef");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setString(1, Reference));
	}

	@Override
	public String save(FinanceCheckListReference fclr, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinanceCheckListRef");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, QuestionId, Answer, Remarks, InstructionUID");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fclr.getFinID());
			ps.setString(index++, fclr.getFinReference());
			ps.setLong(index++, fclr.getQuestionId());
			ps.setLong(index++, fclr.getAnswer());
			ps.setString(index++, fclr.getRemarks());
			ps.setLong(index++, fclr.getInstructionUID());
			ps.setInt(index++, fclr.getVersion());
			ps.setLong(index++, fclr.getLastMntBy());
			ps.setTimestamp(index++, fclr.getLastMntOn());
			ps.setString(index++, fclr.getRecordStatus());
			ps.setString(index++, fclr.getRoleCode());
			ps.setString(index++, fclr.getNextRoleCode());
			ps.setString(index++, fclr.getTaskId());
			ps.setString(index++, fclr.getNextTaskId());
			ps.setString(index++, fclr.getRecordType());
			ps.setLong(index, fclr.getWorkflowId());
		});
		return fclr.getId();
	}

	@Override
	public void update(FinanceCheckListReference fclr, String type) {
		StringBuilder sql = new StringBuilder("Update FinanceCheckListRef");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Remarks = ?, InstructionUID = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ? and QuestionId = ? and Answer = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version = ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fclr.getRemarks());
			ps.setLong(index++, fclr.getInstructionUID());
			ps.setInt(index++, fclr.getVersion());
			ps.setLong(index++, fclr.getLastMntBy());
			ps.setTimestamp(index++, fclr.getLastMntOn());
			ps.setString(index++, fclr.getRecordStatus());
			ps.setString(index++, fclr.getRoleCode());
			ps.setString(index++, fclr.getNextRoleCode());
			ps.setString(index++, fclr.getTaskId());
			ps.setString(index++, fclr.getNextTaskId());
			ps.setString(index++, fclr.getRecordType());
			ps.setLong(index++, fclr.getWorkflowId());
			ps.setLong(index++, fclr.getFinID());
			ps.setLong(index++, fclr.getQuestionId());
			ps.setLong(index++, fclr.getAnswer());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, fclr.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	private StringBuilder sqlSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, QuestionId, Answer, Remarks, InstructionUID");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(",TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescQuesDesc, LovDescAnswerDesc");
		}

		sql.append(" From FinanceCheckListRef");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinanceCheckListReferenceRowMapper implements RowMapper<FinanceCheckListReference> {
		private String type;

		private FinanceCheckListReferenceRowMapper(String type) {
			this.type = type;
		}

		@Override
		public FinanceCheckListReference mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceCheckListReference fcr = new FinanceCheckListReference();

			fcr.setFinID(rs.getLong("FinID"));
			fcr.setFinReference(rs.getString("FinReference"));
			fcr.setQuestionId(rs.getLong("QuestionId"));
			fcr.setAnswer(rs.getLong("Answer"));
			fcr.setRemarks(rs.getString("Remarks"));
			fcr.setInstructionUID(rs.getLong("InstructionUID"));
			fcr.setVersion(rs.getInt("Version"));
			fcr.setLastMntBy(rs.getLong("LastMntBy"));
			fcr.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fcr.setRecordStatus(rs.getString("RecordStatus"));
			fcr.setRoleCode(rs.getString("RoleCode"));
			fcr.setNextRoleCode(rs.getString("NextRoleCode"));
			fcr.setTaskId(rs.getString("TaskId"));
			fcr.setNextTaskId(rs.getString("NextTaskId"));
			fcr.setRecordType(rs.getString("RecordType"));
			fcr.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fcr.setLovDescQuesDesc(rs.getString("LovDescQuesDesc"));
				fcr.setLovDescAnswerDesc(rs.getString("LovDescAnswerDesc"));
			}

			return fcr;
		}
	}

}