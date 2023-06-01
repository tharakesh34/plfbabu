package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class TaxHeaderDetailsDAOImpl extends SequenceDao<Taxes> implements TaxHeaderDetailsDAO {
	private static Logger logger = LogManager.getLogger(TaxHeaderDetailsDAOImpl.class);

	@Override
	public List<Taxes> getTaxDetailById(long headerId, String type) {
		logger.debug(Literal.ENTERING);
		// get the finances list
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceId", headerId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Id, ReferenceId, TaxType, TaxPerc, ActualTax, PaidTax, NetTax,");
		selectSql.append(" RemFeeTax, WaivedTax,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From TAX_DETAILS");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE ReferenceId = :ReferenceId");

		logger.debug("selectSql : " + selectSql.toString());
		RowMapper<Taxes> typeRowMapper = BeanPropertyRowMapper.newInstance(Taxes.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public TaxHeader getTaxHeaderDetailsById(long headerId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, InvoiceID, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from TAX_Header");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				TaxHeader th = new TaxHeader();

				th.setHeaderId(rs.getLong("HeaderId"));
				th.setInvoiceID(JdbcUtil.getLong(rs.getObject("InvoiceID")));
				th.setVersion(rs.getInt("Version"));
				th.setLastMntOn(rs.getTimestamp("LastMntOn"));
				th.setLastMntBy(rs.getLong("LastMntBy"));
				th.setRecordStatus(rs.getString("RecordStatus"));
				th.setRoleCode(rs.getString("RoleCode"));
				th.setNextRoleCode(rs.getString("NextRoleCode"));
				th.setTaskId(rs.getString("TaskId"));
				th.setNextTaskId(rs.getString("NextTaskId"));
				th.setRecordType(rs.getString("RecordType"));
				th.setWorkflowId(rs.getLong("WorkflowId"));

				return th;
			}, headerId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Tax Details not exists in TAX_DETAILS{} table/view for the specified HeaderId >> {}", type,
					headerId);
		}

		return null;
	}

	@Override
	public void update(Taxes taxes, String type) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL, ensure primary key will not be updated.
		if (taxes.getId() <= 0) {
			return;
		}
		StringBuilder sql = new StringBuilder(" Update TAX_DETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ReferenceId = :ReferenceId, TaxType = :TaxType, TaxPerc = :TaxPerc,");
		sql.append(" ActualTax = :ActualTax, PaidTax = :PaidTax, NetTax = :NetTax, RemFeeTax = :RemFeeTax,");
		sql.append(" WaivedTax = :WaivedTax,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where Id = :Id");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxes);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" Delete from TAX_DETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ReferenceId = :ReferenceId");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceId", headerId);

		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteById(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" Delete from TAX_DETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = :Id");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", id);

		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveTaxes(List<Taxes> taxes, String tableType) {
		String sql = getInsertSqlQuery(tableType);

		logger.trace(Literal.SQL + sql);

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Taxes td = taxes.get(i);
				setparameters(ps, td);
			}

			@Override
			public int getBatchSize() {
				return taxes.size();
			}
		});
	}

	private String getInsertSqlQuery(String tableType) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" TAX_DETAILS").append(StringUtils.trimToEmpty(tableType));
		sql.append(" (Id, ReferenceId, TaxType, TaxPerc, ActualTax, PaidTax, NetTax, RemFeeTax, WaivedTax");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");
		return sql.toString();
	}

	private void setparameters(PreparedStatement ps, Taxes td) throws SQLException {
		int index = 1;

		if (td.getId() == Long.MIN_VALUE) {
			td.setId(getNextValue("SeqTax_Details"));
		}

		ps.setLong(index++, td.getId());
		ps.setLong(index++, td.getReferenceId());
		ps.setString(index++, td.getTaxType());
		ps.setBigDecimal(index++, td.getTaxPerc());
		ps.setBigDecimal(index++, td.getActualTax());
		ps.setBigDecimal(index++, td.getPaidTax());
		ps.setBigDecimal(index++, td.getNetTax());
		ps.setBigDecimal(index++, td.getRemFeeTax());
		ps.setBigDecimal(index++, td.getWaivedTax());
		ps.setInt(index++, td.getVersion());
		ps.setLong(index++, td.getLastMntBy());
		ps.setTimestamp(index++, td.getLastMntOn());
		ps.setString(index++, td.getRecordStatus());
		ps.setString(index++, td.getRoleCode());
		ps.setString(index++, td.getNextRoleCode());
		ps.setString(index++, td.getTaskId());
		ps.setString(index++, td.getNextTaskId());
		ps.setString(index++, td.getRecordType());
		ps.setLong(index, td.getWorkflowId());
	}

	@Override
	public long save(Taxes taxes, String type) {
		String sql = getInsertSqlQuery(type);

		logger.trace(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql, ps -> setparameters(ps, taxes));
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return taxes.getId();
	}

	@Override
	public long save(TaxHeader th, String type) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" TAX_Header").append(StringUtils.trimToEmpty(type));
		sql.append(" (HeaderId, InvoiceID, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		if (th.getId() <= 0) {
			th.setId(getNextValue("SeqTAX_Header"));
		}

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, th.getHeaderId());

			if ((Object) th.getInvoiceID() instanceof Long) {
				ps.setLong(index++, th.getInvoiceID());
			} else {
				ps.setNull(index++, Types.NULL);
			}

			ps.setInt(index++, th.getVersion());
			ps.setLong(index++, th.getLastMntBy());
			ps.setTimestamp(index++, th.getLastMntOn());
			ps.setString(index++, th.getRecordStatus());
			ps.setString(index++, th.getRoleCode());
			ps.setString(index++, th.getNextRoleCode());
			ps.setString(index++, th.getTaskId());
			ps.setString(index++, th.getNextTaskId());
			ps.setString(index++, th.getRecordType());
			ps.setLong(index, th.getWorkflowId());

		});

		return th.getId();
	}

	@Override
	public void delete(TaxHeader taxes, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Delete From TAX_Header");
		sql.append(type);
		sql.append(" Where HeaderId = :HeaderId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxes);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> getHeaderIdsByReceiptId(long receiptId, String type) {
		StringBuilder sql = new StringBuilder("Select TaxHeaderID from ReceiptAllocationDetail");
		sql.append(type);
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> JdbcUtil.getLong(rs.getObject(1)), receiptId);
	}

	@Override
	public List<Long> getHeaderIdsFromMAM(long receiptId, String type) {
		StringBuilder sql = new StringBuilder("Select TaxHeaderID from ManualAdviseMovements");
		sql.append(type);
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> JdbcUtil.getLong(rs.getObject(1)), receiptId);
	}

	@Override
	public void update(TaxHeader taxHeader, String type) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL, ensure primary key will not be updated.

		if (taxHeader.getHeaderId() <= 0) {
			return;
		}

		StringBuilder sql = new StringBuilder(" Update TAX_Header");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set HeaderId = :HeaderId, InvoiceID=:InvoiceID, ");
		sql.append(
				" Version= :Version, LastMntBy= :LastMntBy, LastMntOn= :LastMntOn, RecordStatus= :RecordStatus, RoleCode= :RoleCode, NextRoleCode= :NextRoleCode, TaskId= :TaskId, NextTaskId= :NextTaskId");
		sql.append(" Where HeaderId = :HeaderId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}
}
