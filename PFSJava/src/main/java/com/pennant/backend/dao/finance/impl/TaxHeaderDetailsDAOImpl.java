package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class TaxHeaderDetailsDAOImpl extends SequenceDao<Taxes> implements TaxHeaderDetailsDAO {
	private static Logger logger = Logger.getLogger(TaxHeaderDetailsDAOImpl.class);

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
		RowMapper<Taxes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Taxes.class);
		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION);
			logger.debug(Literal.LEAVING);
			return new ArrayList<Taxes>();
		}

	}

	@Override
	public TaxHeader getTaxHeaderDetailsById(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from TAX_Header");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { headerId },
					new RowMapper<TaxHeader>() {
						@Override
						public TaxHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
							TaxHeader th = new TaxHeader();

							th.setHeaderId(rs.getLong("HeaderId"));
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
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void update(Taxes taxes, String type) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL, ensure primary key will not be updated.
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

		logger.debug(Literal.ENTERING);
		for (Taxes tax : taxes) {
			if (tax.getId() == Long.MIN_VALUE) {
				tax.setId(getNextValue("SeqTax_Details"));
				logger.debug("get NextID:" + tax.getId());
			}
		}
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert Into TAX_DETAILS");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" (Id, ReferenceId, TaxType, TaxPerc, ActualTax, PaidTax, NetTax, RemFeeTax, WaivedTax,");
		sql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(
				" Values(:Id, :ReferenceId, :TaxType, :TaxPerc, :ActualTax, :PaidTax, :NetTax, :RemFeeTax, :WaivedTax,");
		sql.append(" :Version,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(taxes.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(Taxes taxes, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert Into TAX_DETAILS");
		sql.append(type);
		sql.append(" (Id, ReferenceId, TaxType, TaxPerc, ActualTax, PaidTax, NetTax, RemFeeTax, WaivedTax,");
		sql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(
				" Values(:Id, :ReferenceId, :TaxType, :TaxPerc, :ActualTax, :PaidTax, :NetTax, :RemFeeTax, :WaivedTax,");
		sql.append(" :Version,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (taxes.getId() == Long.MIN_VALUE) {
			taxes.setId(getNextValue("SeqTax_Details"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxes);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return taxes.getId();
	}

	@Override
	public long save(TaxHeader taxHeader, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into TAX_Header");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (HeaderId,");
		sql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" Values(:HeaderId,");
		sql.append(" :Version,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		if (taxHeader.getId() <= 0) {
			taxHeader.setId(getNextValue("SeqTAX_Header"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxHeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return taxHeader.getId();
	}

	@Override
	public void delete(TaxHeader taxes, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Delete From TAX_Header");
		sql.append(type);
		sql.append(" Where HeaderId = :HeaderId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxes);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> getHeaderIdsByReceiptId(long receiptId, String type) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("ReceiptID", receiptId);

		StringBuilder selectSql = new StringBuilder("Select  TaxHeaderID from ReceiptAllocationDetail");
		selectSql.append(type);
		selectSql.append(" Where ReceiptID = :ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, Long.class);
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
		sql.append(" Set HeaderId = :HeaderId,");
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
