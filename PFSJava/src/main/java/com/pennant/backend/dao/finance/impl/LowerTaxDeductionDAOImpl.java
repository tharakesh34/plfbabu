package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.LowerTaxDeductionDAO;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class LowerTaxDeductionDAOImpl extends SequenceDao<LowerTaxDeduction> implements LowerTaxDeductionDAO {
	private static Logger logger = Logger.getLogger(LowerTaxDeductionDAOImpl.class);

	public LowerTaxDeductionDAOImpl() {
		super();
	}

	@Override
	public List<LowerTaxDeduction> getLowerTaxDeductionDetails(String finReference, String type) {

		LowerTaxDeduction lowerTaxDeduction = new LowerTaxDeduction();
		lowerTaxDeduction.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				"Select FinReference, SeqNo, FinMaintainId, StartDate, EndDate, Percentage, LimitAmt");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From LowerTaxDeduction");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lowerTaxDeduction);
		RowMapper<LowerTaxDeduction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LowerTaxDeduction.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public void save(LowerTaxDeduction lowerTaxDeduction, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" Insert into LowerTaxDeduction");
		sql.append(type);
		sql.append("(Id, SeqNo, FinReference, FinMaintainId, StartDate, Enddate, Percentage, LimitAmt, Version,");
		sql.append(" LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(
				" :Id, :seqno, :finReference, :FinMaintainId, :startDate, :endDate, :percentage, :limitAmt, :version,");
		sql.append(" :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(lowerTaxDeduction);

		// Get the identity sequence number.
		if (lowerTaxDeduction.getId() <= 0) {
			lowerTaxDeduction.setId(getNextValue("SeqLowerTaxDeduction"));
		}
		lowerTaxDeduction.setVersion(1);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(LowerTaxDeduction lowerTaxDeduction, String type) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update LowerTaxDeduction");
		sql.append(type);
		sql.append("  set seqNo = :seqno, finReference = :finReference, startDate = :startDate, ");
		sql.append(" enddate = :endDate, percentage = :percentage, limitAmt = :limitAmt, ");
		sql.append(" Version = :Version, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where finReference = :finReference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(lowerTaxDeduction);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LowerTaxDeduction lowerTaxDeduction, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Delete from Lowertaxdeduction");
		sql.append(type);
		sql.append(" where finReference = :finReference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(lowerTaxDeduction);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

	}

}
