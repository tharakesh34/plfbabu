package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.applicationmaster.AutoKnockOffDAO;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class AutoKnockOffDAOImpl extends SequenceDao<AutoKnockOff> implements AutoKnockOffDAO {
	private static Logger logger = LogManager.getLogger(AutoKnockOffDAOImpl.class);

	public AutoKnockOffDAOImpl() {
		super();
	}

	@Override
	public String save(AutoKnockOff knockOff, TableType tableType) {
		if (knockOff.getId() == Long.MIN_VALUE) {
			knockOff.setId(getNextValue("SEQAUTO_KNOCKOFF"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" AUTO_KNOCKOFF");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, Code, Description, ExecutionDays, Active,  Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, JdbcUtil.setLong(knockOff.getId()));
				ps.setString(index++, knockOff.getCode());
				ps.setString(index++, knockOff.getDescription());
				ps.setString(index++, knockOff.getExecutionDays());
				ps.setBoolean(index++, knockOff.isActive());
				ps.setInt(index++, knockOff.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(knockOff.getLastMntBy()));
				ps.setTimestamp(index++, knockOff.getLastMntOn());
				ps.setString(index++, knockOff.getRecordStatus());
				ps.setString(index++, knockOff.getRoleCode());
				ps.setString(index++, knockOff.getNextRoleCode());
				ps.setString(index++, knockOff.getTaskId());
				ps.setString(index++, knockOff.getNextTaskId());
				ps.setString(index++, knockOff.getRecordType());
				ps.setLong(index, JdbcUtil.setLong(knockOff.getWorkflowId()));
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(knockOff.getId());
	}

	@Override
	public void update(AutoKnockOff knockOff, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" AUTO_KNOCKOFF");
		sql.append(tableType.getSuffix());
		sql.append(" set Code = ?, Description = ?, Active = ?, ExecutionDays = ?, Version = ?, LastMntBy = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int count = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, knockOff.getCode());
				ps.setString(index++, knockOff.getDescription());
				ps.setBoolean(index++, knockOff.isActive());
				ps.setString(index++, knockOff.getExecutionDays());
				ps.setInt(index++, knockOff.getVersion());
				ps.setLong(index++, knockOff.getLastMntBy());
				ps.setTimestamp(index++, knockOff.getLastMntOn());
				ps.setString(index++, knockOff.getRecordStatus());
				ps.setString(index++, knockOff.getRoleCode());
				ps.setString(index++, knockOff.getNextRoleCode());
				ps.setString(index++, knockOff.getTaskId());
				ps.setString(index++, knockOff.getNextTaskId());
				ps.setString(index++, knockOff.getRecordType());
				ps.setLong(index++, knockOff.getWorkflowId());

				ps.setLong(index, knockOff.getId());
			});

			if (count == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			//
		}

	}

	@Override
	public void delete(AutoKnockOff entity, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from");
		sql.append(" AUTO_KNOCKOFF");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), entity.getId());
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public AutoKnockOff getAutoKnockOffCode(long id, TableType type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		AutoKnockOffRowMapper rowMapper = new AutoKnockOffRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, id);

		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public AutoKnockOff getAutoKnockOffCode(String code, TableType type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Code = ?");

		logger.debug(Literal.SQL + sql.toString());

		AutoKnockOffRowMapper rowMapper = new AutoKnockOffRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, code);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public boolean isDuplicateKey(long id, String code, TableType tableType) {
		String sql;
		String whereClause = "Code = ? and Id != ?";

		Object[] object = new Object[] {};

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("AUTO_KNOCKOFF", whereClause);
			object = new Object[] { code, id };
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("AUTO_KNOCKOFF_Temp", whereClause);
			object = new Object[] { code, id };
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "AUTO_KNOCKOFF_Temp", "AUTO_KNOCKOFF" }, whereClause);
			object = new Object[] { code, id, code, id };
			break;
		}

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, Integer.class, object) > 0;
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return false;
	}

	public List<AutoKnockOff> getKnockOffDetails(long finID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ak.Id, fm.FinID, fm.Finreference, ft.FinType, ft.FinTypeDesc, ak.Code, ak.Description");
		sql.append(", ak.ExecutionDays, fe.FeeTypeCode, fe.FeeTypedesc, akl.KnockOffOrder, akf.FeeOrder");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.Fintype = fm.Fintype");
		sql.append(" Inner Join Auto_KnockOff_LoanTypes akl on akl.Loantype = ft.Fintype");
		sql.append(" Inner Join Auto_knockOff ak on ak.Id = akl.Knockoffid and ak.Active = ?");
		sql.append(" Inner Join AUTO_KNOCKOFF_FEE_TYPES akf on akf.KnockOffId = ak.Id");
		sql.append(" Inner Join FeeTypes fe on fe.FeeTypeId = akf.FeeTypeId ");
		sql.append(" Where fm.FinID = ? and Fm.FinIsActive=1");
		sql.append(" Order By akl.KnockOffOrder, akf.FeeOrder");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setInt(index++, 1);
					ps.setLong(index, finID);
				}
			}, new RowMapper<AutoKnockOff>() {
				@Override
				public AutoKnockOff mapRow(ResultSet rs, int rowNum) throws SQLException {
					AutoKnockOff knockOff = new AutoKnockOff();

					knockOff.setId(rs.getLong("Id"));
					knockOff.setFinID(rs.getLong("FinID"));
					knockOff.setFinreference(rs.getString("Finreference"));
					knockOff.setFinType(rs.getString("FinType"));
					knockOff.setFinTypeDesc(rs.getString("FinTypeDesc"));
					knockOff.setCode(rs.getString("Code"));
					knockOff.setDescription(rs.getString("Description"));
					knockOff.setExecutionDays(rs.getString("ExecutionDays"));
					knockOff.setFeeTypeCode(rs.getString("FeeTypeCode"));
					// knockOff.setFeeTypeDesc(rs.getString("FeeTypedesc"));
					knockOff.setKnockOffOrder(rs.getString("KnockOffOrder"));
					knockOff.setFeeOrder(rs.getInt("FeeOrder"));

					return knockOff;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	private StringBuilder getSqlQuery(TableType type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Code, Description, ExecutionDays, Active, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From AUTO_KNOCKOFF");
		sql.append(type.getSuffix());

		return sql;
	}

	public class AutoKnockOffRowMapper implements RowMapper<AutoKnockOff> {
		@Override
		public AutoKnockOff mapRow(ResultSet rs, int rowNum) throws SQLException {
			AutoKnockOff knockOff = new AutoKnockOff();

			knockOff.setId(rs.getLong("Id"));
			knockOff.setCode(rs.getString("Code"));
			knockOff.setDescription(rs.getString("Description"));
			knockOff.setExecutionDays(rs.getString("ExecutionDays"));
			knockOff.setActive(rs.getBoolean("Active"));
			knockOff.setVersion(rs.getInt("Version"));
			knockOff.setLastMntOn(rs.getTimestamp("LastMntOn"));
			knockOff.setLastMntBy(rs.getLong("LastMntBy"));
			knockOff.setRecordStatus(rs.getString("RecordStatus"));
			knockOff.setRoleCode(rs.getString("RoleCode"));
			knockOff.setNextRoleCode(rs.getString("NextRoleCode"));
			knockOff.setTaskId(rs.getString("TaskId"));
			knockOff.setNextTaskId(rs.getString("NextTaskId"));
			knockOff.setRecordType(rs.getString("RecordType"));
			knockOff.setWorkflowId(rs.getLong("WorkflowId"));

			return knockOff;
		}
	}

	@Override
	public void logExcessForKnockOff(Date valueDate, String day, String thresholdValue) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" AUTO_kNOCKOFF_EXCESS");
		sql.append(" (FinID, FinReference, AmountType, BalanceAmount, PayableID");
		sql.append(", ValueDate, ExecutionDay, ThresholdValue)");
		sql.append(" Select FinID, FinReference, AmountType, BalanceAmount, PayableID");
		sql.append(", ? ValueDate, ? ExecutionDay, ? ThresholdValue");
		sql.append(" From");
		sql.append(" (Select fm.FinID, fm.FinReference, AmountType, sum(BALANCEAMT) BalanceAmount");
		sql.append(", ExcessId PayableId");
		sql.append(" From FinExcessAmount ea");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ea.FinID");
		sql.append(" Where  AmountType = ? and BALANCEAMT > ? and fm.FinIsActive = ? and");
		sql.append(" fm.WriteoffLoan = ? Group by fm.FinID, fm.FinReference, AmountType, ExcessId");
		sql.append(" Union All");
		sql.append(" Select FinID, FinReference, AmountType, sum(BALANCEAMT) BalanceAmount");
		sql.append(", PayableId from ");
		sql.append(" (Select fm.FinID, fm.FinReference, ? AmountType, BALANCEAMT, AdviseId PayableId");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ma.FinID");
		sql.append(" Where  ma.AdviseType = ? and BALANCEAMT > ? and fm.FinIsActive = ? and");
		sql.append(" fm.WriteoffLoan = ?) it Group by it.FinID, it.FinReference, it.AmountType, it.PayableId) T");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(valueDate));
				ps.setString(index++, day);
				ps.setString(index++, thresholdValue);
				ps.setString(index++, "E");
				ps.setInt(index++, 0);
				ps.setInt(index++, 1);
				ps.setInt(index++, 0);
				ps.setString(index++, "P");
				ps.setInt(index++, 2);
				ps.setInt(index++, 0);
				ps.setInt(index++, 1);
				ps.setInt(index, 0);
			});
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	@Override
	public void deleteKnockOffExcessLog(Date valueDate) {
		StringBuilder sql = new StringBuilder("Delete");
		sql.append(" From AUTO_KNOCKOFF_EXCESS_DETAILS");
		sql.append(" Where ExcessId in (");
		sql.append(" Select ID from AUTO_KNOCKOFF_EXCESS");
		sql.append(" Where ValueDate = ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), JdbcUtil.getDate(valueDate));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		sql = new StringBuilder("Delete");
		sql.append(" From AUTO_KNOCKOFF_EXCESS");
		sql.append(" Where ValueDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), JdbcUtil.getDate(valueDate));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long logKnockOffDetails(Date valueDate, String day) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" AUTO_KNOCKOFF_EXCESS_DETAILS");
		sql.append(" (KnockOffId, ExcessID, FinType, FinTypeDesc");
		sql.append(", Code, Description, ExecutionDays, FinCcy, FeeTypeCode");
		sql.append(", FeeTypeDesc, KnockOffOrder, FeeOrder)");
		sql.append(" Select");
		sql.append(" ak.Id KnockOffId, ake.Id ExcessId, ft.FinType");
		sql.append(", ft.FinTypeDesc, ak.Code, ak.Description, ak.ExecutionDays, fm.FinCcy");
		sql.append(", fe.FeeTypeCode, fe.FeeTypedesc, akl.KnockOffOrder, akf.FeeOrder");
		sql.append(" From AUTO_KNOCKOFF_EXCESS ake");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ake.FinID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.Fintype = fm.Fintype");
		sql.append(" Inner Join Auto_KnockOff_LoanTypes akl on akl.Loantype = ft.Fintype");
		sql.append(" Inner Join Auto_knockOff ak on ak.Id = akl.Knockoffid and ak.Active = ?");
		sql.append(" Inner Join AUTO_KNOCKOFF_FEE_TYPES akf on akf.KnockOffId = ak.Id");
		sql.append(" Inner Join FeeTypes fe on fe.FeeTypeId = akf.FeeTypeId");
		sql.append(" Where ake.valuedate = ? and ake.ProcessingFlag = ?");
		sql.append(" and ak.ExecutionDays like ?");
		sql.append(" Order by ExcessId, akl.KnockOffOrder, akf.FeeOrder");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setInt(index++, 1);
				ps.setDate(index++, JdbcUtil.getDate(valueDate));
				ps.setInt(index++, 0);
				ps.setString(index, "%" + day + "%");
			});
		} catch (Exception e) {
			//
		}

		return 0;
	}
}
