package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.applicationmaster.AutoKnockOffDAO;
import com.pennant.backend.model.autoknockoff.AutoKnockOffExcessDetails;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class AutoKnockOffDAOImpl extends SequenceDao<AutoKnockOff> implements AutoKnockOffDAO {
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

				ps.setLong(index++, knockOff.getId());
				ps.setString(index++, knockOff.getCode());
				ps.setString(index++, knockOff.getDescription());
				ps.setString(index++, knockOff.getExecutionDays());
				ps.setBoolean(index++, knockOff.isActive());
				ps.setInt(index++, knockOff.getVersion());
				ps.setLong(index++, knockOff.getLastMntBy());
				ps.setTimestamp(index++, knockOff.getLastMntOn());
				ps.setString(index++, knockOff.getRecordStatus());
				ps.setString(index++, knockOff.getRoleCode());
				ps.setString(index++, knockOff.getNextRoleCode());
				ps.setString(index++, knockOff.getTaskId());
				ps.setString(index++, knockOff.getNextTaskId());
				ps.setString(index++, knockOff.getRecordType());
				ps.setLong(index, knockOff.getWorkflowId());
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
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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

		return jdbcOperations.queryForObject(sql, Integer.class, object) > 0;
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
		sql.append(" AUTO_KNOCKOFF_EXCESS_STAGE");
		sql.append(" (FinID, FinReference, AmountType, BalanceAmount, PayableID");
		sql.append(", ValueDate, ExecutionDay, ThresholdValue)");
		sql.append(" Select FinID, FinReference, AmountType, BalanceAmount, PayableID");
		sql.append(", ? ValueDate, ? ExecutionDay, ? ThresholdValue");
		sql.append(" From");
		sql.append(" (Select fm.FinID, fm.FinReference, AmountType, sum(BALANCEAMT) BalanceAmount");
		sql.append(", ExcessId PayableId");
		sql.append(" From FinExcessAmount ea");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ea.FinID");
		sql.append(" Where  AmountType = ? and BALANCEAMT > ? and");
		sql.append(" fm.WriteoffLoan = ? Group by fm.FinID, fm.FinReference, AmountType, ExcessId");
		sql.append(" Union All");
		sql.append(" Select FinID, FinReference, AmountType, sum(BALANCEAMT) BalanceAmount");
		sql.append(", PayableId from ");
		sql.append(" (Select fm.FinID, fm.FinReference, ? AmountType, BALANCEAMT, AdviseId PayableId");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ma.FinID");
		sql.append(" Where  ma.AdviseType = ? and BALANCEAMT > ? and");
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
				ps.setInt(index++, 0);
				ps.setString(index++, "P");
				ps.setInt(index++, 2);
				ps.setInt(index++, 0);
				ps.setInt(index, 0);
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void truncateData() {
		String sql = "DELETE FROM AUTO_KNOCKOFF_EXCESS_DTL_STAGE";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);

		sql = "DELETE FROM AUTO_KNOCKOFF_EXCESS_STAGE";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql);
	}

	@Override
	public long logKnockOffDetails(Date valueDate, String day) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" AUTO_KNOCKOFF_EXCESS_DTL_STAGE");
		sql.append(" (KnockOffId, ExcessID, FinType, FinTypeDesc");
		sql.append(", Code, Description, ExecutionDays, FinCcy, FeeTypeCode");
		sql.append(", FeeTypeDesc, KnockOffOrder, FeeOrder)");
		sql.append(" Select");
		sql.append(" ak.Id KnockOffId, ake.Id ExcessId, ft.FinType");
		sql.append(", ft.FinTypeDesc, ak.Code, ak.Description, ak.ExecutionDays, fm.FinCcy");
		sql.append(", fe.FeeTypeCode, fe.FeeTypedesc, akl.KnockOffOrder, akf.FeeOrder");
		sql.append(" From AUTO_KNOCKOFF_EXCESS_STAGE ake");
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
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public List<AutoKnockOffExcess> getKnockOffExcess(long custID, Date valueDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ake.ID, ake.FinID, ake.FinReference, ake.AmountType, ake.ValueDate");
		sql.append(", ake.BalanceAmount, ake.ExecutionDay, ake.ThresholdValue, ake.PayableId");
		sql.append(" From AUTO_KNOCKOFF_EXCESS_STAGE ake");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = ake.FinReference and fm.FinIsActive = ?");
		sql.append(" Where fm.CustId = ? and ValueDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setLong(index++, custID);
			ps.setDate(index, JdbcUtil.getDate(valueDate));
		}, (rs, rowNum) -> {
			AutoKnockOffExcess knockOff = new AutoKnockOffExcess();

			knockOff.setID(rs.getLong("ID"));
			knockOff.setFinID(rs.getLong("FinID"));
			knockOff.setFinReference(rs.getString("FinReference"));
			knockOff.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
			knockOff.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
			knockOff.setAmountType(rs.getString("AmountType"));
			knockOff.setPayableID(rs.getLong("PayableId"));
			knockOff.setExecutionDay(rs.getString("ExecutionDay"));
			knockOff.setThresholdValue(rs.getString("ThresholdValue"));

			return knockOff;
		});
	}

	@Override
	public List<AutoKnockOffExcessDetails> getKnockOffExcessDetails(long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" aked.ID, aked.ExcessID, aked.KnockOffID, aked.Code, aked.Description, aked.ExecutionDays");
		sql.append(", aked.FinType, aked.FeeTypeCode, aked.KnockOffOrder, aked.FeeOrder");
		sql.append(", aked.FinCcy");
		sql.append(", coalesce(frhCount, 0) FrhCount");
		sql.append(" From AUTO_KNOCKOFF_EXCESS_DTL_STAGE aked");
		sql.append(" Inner Join AUTO_KNOCKOFF_EXCESS_STAGE ake on ake.ID = aked.ExcessID");
		sql.append(" Left join (select count(*) frhcount, Reference from FinReceiptHeader_Temp rh");
		sql.append(" Inner join FinReceiptDetail_Temp rd on rd.ReceiptId = rh.ReceiptId");
		sql.append(" Group by Reference) rh on rh.Reference = ake.FinReference");
		sql.append(" Where aked.ExcessID = ?");
		sql.append(" order by aked.ID, aked.KnockOffOrder, aked.FeeOrder");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, id), (rs, rowNum) -> {
			AutoKnockOffExcessDetails excessDetails = new AutoKnockOffExcessDetails();
			excessDetails.setID(rs.getLong("ID"));
			excessDetails.setExcessID(rs.getLong("ExcessID"));
			excessDetails.setKnockOffID(rs.getLong("KnockOffID"));
			excessDetails.setCode(rs.getString("Code"));
			excessDetails.setDescription(rs.getString("Description"));
			excessDetails.setExecutionDays(rs.getString("ExecutionDays"));
			excessDetails.setFinType(rs.getString("FinType"));
			excessDetails.setFeeTypeCode(rs.getString("FeeTypeCode"));
			excessDetails.setKnockOffOrder(rs.getString("KnockOffOrder"));
			excessDetails.setFeeOrder(rs.getInt("FeeOrder"));
			excessDetails.setFinCcy(rs.getString("FinCcy"));
			excessDetails.setFrhCount(rs.getInt("FrhCount"));

			return excessDetails;
		});
	}

	public void updateExcessData(AutoKnockOffExcess knockOff) {
		String sql = "Update AUTO_KNOCKOFF_EXCESS_STAGE set ProcessingFlag = ?, UtilizedAmount = ?  Where ID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setInt(index++, 1);
				ps.setBigDecimal(index++, knockOff.getTotalUtilizedAmnt());

				ps.setLong(index, knockOff.getID());
			});
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	public void updateExcessDetails(List<AutoKnockOffExcessDetails> knockOffExcess) {
		String sql = "Update AUTO_KNOCKOFF_EXCESS_DTL_STAGE Set ReceiptId = ?, Reason = ?, Status = ?, UtilizedAmnt = ? Where ID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					AutoKnockOffExcessDetails excessDetails = knockOffExcess.get(i);
					int index = 1;

					ps.setLong(index++, excessDetails.getReceiptID());
					ps.setString(index++, excessDetails.getReason());
					ps.setString(index++, excessDetails.getStatus());
					ps.setBigDecimal(index++, excessDetails.getUtilizedAmnt());
					ps.setLong(index, excessDetails.getID());
				}

				@Override
				public int getBatchSize() {
					return knockOffExcess.size();
				}
			});
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	@Override
	public void backupExecutionData() {
		String sql = "INSERT INTO AUTO_KNOCKOFF_EXCESS (ID, FINID, FINREFERENCE, PAYABLEID, AMOUNTTYPE, BALANCEAMOUNT, EXECUTIONDAY, THRESHOLDVALUE, UTILIZEDAMOUNT, VALUEDATE, PROCESSINGFLAG) SELECT ID, FINID, FINREFERENCE, PAYABLEID, AMOUNTTYPE, BALANCEAMOUNT, EXECUTIONDAY, THRESHOLDVALUE, UTILIZEDAMOUNT, VALUEDATE, PROCESSINGFLAG FROM AUTO_KNOCKOFF_EXCESS_STAGE";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql);

		sql = "INSERT INTO AUTO_KNOCKOFF_EXCESS_DETAILS (ID, EXCESSID, KNOCKOFFID, FINTYPE, FINTYPEDESC, CODE, DESCRIPTION, EXECUTIONDAYS, FEETYPECODE, FEETYPEDESC, KNOCKOFFORDER, FEEORDER, UTILIZEDAMNT, RECEIPTID, REASON, FINCCY, STATUS) SELECT ID, EXCESSID, KNOCKOFFID, FINTYPE, FINTYPEDESC, CODE, DESCRIPTION, EXECUTIONDAYS, FEETYPECODE, FEETYPEDESC, KNOCKOFFORDER, FEEORDER, UTILIZEDAMNT, RECEIPTID, REASON, FINCCY, STATUS FROM AUTO_KNOCKOFF_EXCESS_DTL_STAGE";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql);
	}
}
