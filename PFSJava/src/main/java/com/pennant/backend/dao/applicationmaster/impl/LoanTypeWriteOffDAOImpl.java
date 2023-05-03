package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.applicationmaster.LoanTypeWriteOffDAO;
import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class LoanTypeWriteOffDAOImpl extends SequenceDao<FinTypeWriteOff> implements LoanTypeWriteOffDAO {

	public LoanTypeWriteOffDAOImpl() {
		super();
	}

	@Override
	public FinTypeWriteOff getLoanWriteOffMappingByID(FinTypeWriteOff writeOffMapping, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Id = ? and LoanType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinTypeWriteOffRM(), writeOffMapping.getId(),
					writeOffMapping.getLoanType());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinTypeWriteOff> getLoanWriteOffMappingListByLoanType(String finType, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where LoanType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), new FinTypeWriteOffRM(), finType);
	}

	@Override
	public void update(FinTypeWriteOff kCodeMapping, String type) {
		StringBuilder sql = new StringBuilder("Update AUTO_WRITE_OFF_LOAN_TYPE");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PslCode = ?, DpdDays = ?, Version = ?");
		sql.append(", ApprovedBy = ?, ApprovedOn = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, kCodeMapping.getPslCode());
			ps.setInt(++index, kCodeMapping.getDpdDays());
			ps.setInt(++index, kCodeMapping.getVersion());
			ps.setLong(++index, kCodeMapping.getApprovedBy());
			ps.setTimestamp(++index, kCodeMapping.getApprovedOn());
			ps.setLong(++index, kCodeMapping.getLastMntBy());
			ps.setTimestamp(++index, kCodeMapping.getLastMntOn());
			ps.setString(++index, kCodeMapping.getRecordStatus());
			ps.setString(++index, kCodeMapping.getRoleCode());
			ps.setString(++index, kCodeMapping.getNextRoleCode());
			ps.setString(++index, kCodeMapping.getTaskId());
			ps.setString(++index, kCodeMapping.getNextTaskId());
			ps.setString(++index, kCodeMapping.getRecordType());
			ps.setLong(++index, kCodeMapping.getWorkflowId());

			ps.setLong(++index, kCodeMapping.getId());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public long save(FinTypeWriteOff kCodeMapping, String type) {
		if (kCodeMapping.getId() == Long.MIN_VALUE) {
			kCodeMapping.setId(getNextValue("SeqAuto_Write_Off_Loan_Type"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Auto_Write_Off_Loan_Type");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, PslCode, LoanType, DpdDays, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(")");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, kCodeMapping.getId());
			ps.setString(++index, kCodeMapping.getPslCode());
			ps.setString(++index, kCodeMapping.getLoanType());
			ps.setInt(++index, kCodeMapping.getDpdDays());
			ps.setInt(++index, kCodeMapping.getVersion());
			ps.setLong(++index, kCodeMapping.getCreatedBy());
			ps.setTimestamp(++index, kCodeMapping.getCreatedOn());
			ps.setObject(++index, kCodeMapping.getApprovedBy());
			ps.setTimestamp(++index, kCodeMapping.getApprovedOn());
			ps.setLong(++index, kCodeMapping.getLastMntBy());
			ps.setTimestamp(++index, kCodeMapping.getLastMntOn());
			ps.setString(++index, kCodeMapping.getRecordStatus());
			ps.setString(++index, kCodeMapping.getRoleCode());
			ps.setString(++index, kCodeMapping.getNextRoleCode());
			ps.setString(++index, kCodeMapping.getTaskId());
			ps.setString(++index, kCodeMapping.getNextTaskId());
			ps.setString(++index, kCodeMapping.getRecordType());
			ps.setLong(++index, kCodeMapping.getWorkflowId());
		});

		return kCodeMapping.getId();
	}

	@Override
	public void delete(FinTypeWriteOff kCodeMapping, String type) {
		StringBuilder sql = new StringBuilder("Delete From Auto_Write_Off_Loan_Type");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PslCode = ? and Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), kCodeMapping.getPslCode(), kCodeMapping.getId());
	}

	@Override
	public void delete(long loanTypeId, String tableType) {
		StringBuilder sql = new StringBuilder("Delete From Auto_Write_Off_Loan_Type");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where LoanType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), loanTypeId);
	}

	@Override
	public boolean isDuplicateKey(String loanType, TableType tableType) {
		String sql;
		String whereClause = "LoanType = ?";

		Object[] obj = new Object[] { loanType };
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Auto_Write_Off_Loan_Type", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Auto_Write_Off_Loan_Type_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Auto_Write_Off_Loan_Type_Temp", "Auto_Write_Off_Loan_Type" },
					whereClause);
			obj = new Object[] { loanType, loanType };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public boolean isExistWriteoffCode(long writeoffId, TableType tableType) {
		String sql;
		String whereClause = "WriteOffId = ?";

		Object[] obj = new Object[] { writeoffId };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Auto_Write_Off_Loan_Type", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Auto_Write_Off_Loan_Type_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Auto_Write_Off_Loan_Type_Temp", "Auto_Write_Off_Loan_Type" },
					whereClause);
			obj = new Object[] { writeoffId, writeoffId };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, PSLCode, LoanType, DPDDays, Version");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Auto_Write_Off_Loan_Type");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinTypeWriteOffRM implements RowMapper<FinTypeWriteOff> {

		private FinTypeWriteOffRM() {
			super();
		}

		@Override
		public FinTypeWriteOff mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinTypeWriteOff ftwo = new FinTypeWriteOff();

			ftwo.setId(rs.getLong("Id"));
			ftwo.setPslCode(rs.getString("PSLCode"));
			ftwo.setLoanType(rs.getString("LoanType"));
			ftwo.setDpdDays(rs.getInt("DPDDays"));
			ftwo.setVersion(rs.getInt("Version"));
			ftwo.setCreatedBy(rs.getLong("CreatedBy"));
			ftwo.setCreatedOn(rs.getTimestamp("CreatedOn"));
			ftwo.setApprovedBy(rs.getLong("ApprovedBy"));
			ftwo.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			ftwo.setLastMntBy(rs.getLong("LastMntBy"));
			ftwo.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ftwo.setRecordStatus(rs.getString("RecordStatus"));
			ftwo.setRoleCode(rs.getString("RoleCode"));
			ftwo.setNextRoleCode(rs.getString("NextRoleCode"));
			ftwo.setTaskId(rs.getString("TaskId"));
			ftwo.setNextTaskId(rs.getString("NextTaskId"));
			ftwo.setRecordType(rs.getString("RecordType"));
			ftwo.setWorkflowId(rs.getLong("WorkflowId"));

			return ftwo;
		}
	}
}