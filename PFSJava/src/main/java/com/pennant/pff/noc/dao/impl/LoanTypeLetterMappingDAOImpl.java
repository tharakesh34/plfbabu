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
 * * FileName : FinanceTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified
 * Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.pff.noc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.pff.noc.dao.LoanTypeLetterMappingDAO;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class LoanTypeLetterMappingDAOImpl extends SequenceDao<LoanTypeLetterMapping>
		implements LoanTypeLetterMappingDAO {

	public LoanTypeLetterMappingDAOImpl() {
		super();
	}

	@Override
	public LoanTypeLetterMapping getLoanTypeLetterMappingByID(LoanTypeLetterMapping letterMapping, String type) {
		StringBuilder sql = new StringBuilder("Select Id, FinType, Lettertype");
		sql.append(", AutoGeneration, LetterMode, EmailTemplateId, AgreementCodeId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From Loantype_Letter_Mapping");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ? And FinType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				LoanTypeLetterMapping ltlm = new LoanTypeLetterMapping();

				ltlm.setId(rs.getLong("Id"));
				ltlm.setFinType(rs.getString("FinType"));
				ltlm.setLetterType(rs.getString("Lettertype"));
				ltlm.setAutoGeneration(rs.getBoolean("AutoGeneration"));
				ltlm.setLetterMode(rs.getString("LetterMode"));
				ltlm.setEmailTemplateId(rs.getLong("EmailTemplateId"));
				ltlm.setAgreementCodeId(rs.getLong("AgreementCodeId"));
				ltlm.setVersion(rs.getInt("Version"));
				ltlm.setLastMntBy(rs.getLong("LastMntBy"));
				ltlm.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ltlm.setRecordStatus(rs.getString("RecordStatus"));
				ltlm.setRoleCode(rs.getString("RoleCode"));
				ltlm.setNextRoleCode(rs.getString("NextRoleCode"));
				ltlm.setTaskId(rs.getString("TaskId"));
				ltlm.setNextTaskId(rs.getString("NextTaskId"));
				ltlm.setRecordType(rs.getString("RecordType"));
				ltlm.setWorkflowId(rs.getLong("WorkflowId"));

				return ltlm;

			}, letterMapping.getId(), letterMapping.getFinType());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<LoanTypeLetterMapping> getLoanTypeLettterMappingListByLoanType(String finType) {
		StringBuilder sql = new StringBuilder("Select Id, FinType, LetterType");
		sql.append(", AutoGeneration, LetterMode, EmailTemplateId, AgreementCodeId");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From (");
		sql.append(getSqlQuery(TableType.TEMP_TAB));
		sql.append(" Union All ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where not exists (Select 1 From Loantype_Letter_Mapping_Temp Where FinType = ltlm.FinType)) ltlm");
		sql.append(" Where FinType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), new LoanTypeLetterMappingRM(), finType);
	}

	@Override
	public long save(LoanTypeLetterMapping ltlm, String type) {
		if (ltlm.getId() == 0 || ltlm.getId() == Long.MIN_VALUE) {
			ltlm.setId(getNextValue("SEQ_LOANTYPE_LETTER_MAPPING"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Loantype_Letter_Mapping");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, FinType, Lettertype, AutoGeneration, LetterMode");
		sql.append(", EmailTemplateId, AgreementCodeId");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, ltlm.getId());
				ps.setString(++index, ltlm.getFinType());
				ps.setString(++index, ltlm.getLetterType());
				ps.setBoolean(++index, ltlm.isAutoGeneration());
				ps.setString(++index, ltlm.getLetterMode());
				ps.setLong(++index, ltlm.getEmailTemplateId());
				ps.setLong(++index, ltlm.getAgreementCodeId());
				ps.setInt(++index, ltlm.getVersion());
				ps.setLong(++index, ltlm.getCreatedBy());
				ps.setTimestamp(++index, ltlm.getCreatedOn());
				ps.setObject(++index, ltlm.getApprovedBy());
				ps.setTimestamp(++index, ltlm.getApprovedOn());
				ps.setLong(++index, ltlm.getLastMntBy());
				ps.setTimestamp(++index, ltlm.getLastMntOn());
				ps.setString(++index, ltlm.getRecordStatus());
				ps.setString(++index, ltlm.getRoleCode());
				ps.setString(++index, ltlm.getNextRoleCode());
				ps.setString(++index, ltlm.getTaskId());
				ps.setString(++index, ltlm.getNextTaskId());
				ps.setString(++index, ltlm.getRecordType());
				ps.setLong(++index, ltlm.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return ltlm.getId();
	}

	@Override
	public void update(LoanTypeLetterMapping ltlm, String type) {
		StringBuilder sql = new StringBuilder("Update Loantype_Letter_Mapping");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Lettertype = ?, AutoGeneration = ?");
		sql.append(", LetterMode = ?, EmailTemplateId = ?, AgreementCodeId = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setString(++index, ltlm.getLetterType());
				ps.setBoolean(++index, ltlm.isAutoGeneration());
				ps.setString(++index, ltlm.getLetterMode());
				ps.setLong(++index, ltlm.getEmailTemplateId());
				ps.setLong(++index, ltlm.getAgreementCodeId());
				ps.setInt(++index, ltlm.getVersion());
				ps.setLong(++index, ltlm.getLastMntBy());
				ps.setTimestamp(++index, ltlm.getLastMntOn());
				ps.setString(++index, ltlm.getRecordStatus());
				ps.setString(++index, ltlm.getRoleCode());
				ps.setString(++index, ltlm.getNextRoleCode());
				ps.setString(++index, ltlm.getTaskId());
				ps.setString(++index, ltlm.getNextTaskId());
				ps.setString(++index, ltlm.getRecordType());
				ps.setLong(++index, ltlm.getWorkflowId());

				ps.setLong(++index, ltlm.getId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void delete(LoanTypeLetterMapping ltlm, String type) {
		StringBuilder sql = new StringBuilder("Delete From Loantype_Letter_Mapping");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ? and LetterType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (this.jdbcOperations.update(sql.toString(), ltlm.getId(), ltlm.getLetterType()) == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void delete(String finType, String type) {
		StringBuilder sql = new StringBuilder("Delete From Loantype_Letter_Mapping");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), finType);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isDuplicateKey(String finType, TableType tableType) {
		Object[] parameters = new Object[] { finType };

		String sql;
		String whereClause = "Fintype = ?";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Loantype_Letter_Mapping", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Loantype_Letter_Mapping_Temp", whereClause);
			break;
		default:
			String[] tables = new String[] { "Loantype_Letter_Mapping_Temp", "Loantype_Letter_Mapping" };
			sql = QueryUtil.getCountQuery(tables, whereClause);
			parameters = new Object[] { finType, finType };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, parameters) > 0;
	}

	@Override
	public boolean isExistLetterType(String letterType, TableType tableType) {
		Object[] parameters = new Object[] { letterType };

		String sql;
		String whereClause = "LetterType = ?";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Loantype_Letter_Mapping", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Loantype_Letter_Mapping_Temp", whereClause);
			break;
		default:
			String[] tables = new String[] { "Loantype_Letter_Mapping_Temp", "Loantype_Letter_Mapping" };
			sql = QueryUtil.getCountQuery(tables, whereClause);
			parameters = new Object[] { letterType, letterType };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, parameters) > 0;
	}

	@Override
	public List<LoanTypeLetterMapping> getLoanTypeLetterMapping(List<String> roleCodes) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinType, LetterType, AutoGeneration, LetterMode");
		sql.append(", EmailTemplateId, AgreementCodeId, Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From (");
		sql.append(getSqlQuery(TableType.TEMP_TAB));
		sql.append(" Union All ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where not exists (Select 1 From Loantype_Letter_Mapping_Temp Where FinType = ltlm.FinType)) ltlm");
		sql.append(" Where NextRoleCode is null or NextRoleCode = ? or NextRoleCode in (");
		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<LoanTypeLetterMapping> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "");
			for (String roleCode : roleCodes) {
				ps.setString(index++, roleCode);
			}

		}, new LoanTypeLetterMappingRM());

		return list.stream().sorted((l1, l2) -> l1.getFinType().compareTo(l2.getFinType())).toList();
	}

	private StringBuilder getSqlQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ltlm.Id, ltlm.FinType, ltlm.Lettertype, ltlm.AutoGeneration, ltlm.LetterMode");
		sql.append(", ltlm.EmailTemplateId, ltlm.AgreementCodeId, ltlm.Version");
		sql.append(", ltlm.CreatedBy, ltlm.CreatedOn, ltlm.ApprovedBy, ltlm.ApprovedOn");
		sql.append(", ltlm.LastMntBy, ltlm.LastMntOn, ltlm.RecordStatus, ltlm.RoleCode");
		sql.append(", ltlm.NextRoleCode, ltlm.TaskId, ltlm.NextTaskId, ltlm.RecordType, ltlm.WorkflowId");
		sql.append(" From Loantype_Letter_Mapping").append(tableType.getSuffix()).append(" ltlm");

		return sql;
	}

	private class LoanTypeLetterMappingRM implements RowMapper<LoanTypeLetterMapping> {
		private LoanTypeLetterMappingRM() {
			super();
		}

		@Override
		public LoanTypeLetterMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
			LoanTypeLetterMapping ltlm = new LoanTypeLetterMapping();

			ltlm.setId(rs.getLong("Id"));
			ltlm.setFinType(rs.getString("FinType"));
			ltlm.setLetterType(rs.getString("LetterType"));
			ltlm.setAutoGeneration(rs.getBoolean("AutoGeneration"));
			ltlm.setLetterMode(rs.getString("LetterMode"));
			ltlm.setEmailTemplateId(rs.getLong("EmailTemplateId"));
			ltlm.setAgreementCodeId(rs.getLong("AgreementCodeId"));
			ltlm.setVersion(rs.getInt("Version"));
			ltlm.setCreatedBy(rs.getLong("CreatedBy"));
			ltlm.setCreatedOn(rs.getTimestamp("CreatedOn"));
			ltlm.setApprovedBy(rs.getLong("ApprovedBy"));
			ltlm.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			ltlm.setLastMntBy(rs.getLong("LastMntBy"));
			ltlm.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ltlm.setRecordStatus(rs.getString("RecordStatus"));
			ltlm.setRoleCode(rs.getString("RoleCode"));
			ltlm.setNextRoleCode(rs.getString("NextRoleCode"));
			ltlm.setTaskId(rs.getString("TaskId"));
			ltlm.setNextTaskId(rs.getString("NextTaskId"));
			ltlm.setRecordType(rs.getString("RecordType"));
			ltlm.setWorkflowId(rs.getLong("WorkflowId"));

			return ltlm;
		}
	}
}