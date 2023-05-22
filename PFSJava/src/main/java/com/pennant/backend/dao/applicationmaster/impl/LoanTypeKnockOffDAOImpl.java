package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.LoanTypeKnockOffDAO;
import com.pennant.backend.model.finance.FinTypeKnockOff;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class LoanTypeKnockOffDAOImpl extends SequenceDao<FinTypeKnockOff> implements LoanTypeKnockOffDAO {

	private static final Logger logger = LogManager.getLogger(LoanTypeKnockOffDAOImpl.class);

	public LoanTypeKnockOffDAOImpl() {
	    super();
	}

	@Override
	public FinTypeKnockOff getLoanKnockOffMappingByID(FinTypeKnockOff knockOffCodeMapping, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT Id, knockOffId, LoanType, knockOffOrder");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" FROM AUTO_KNOCKOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = :Id And LoanType = :LoanType");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(knockOffCodeMapping);
		RowMapper<FinTypeKnockOff> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeKnockOff.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("LoanType Knock Mapping details not found  with Id {} and LoanType {}",
					knockOffCodeMapping.getId(), knockOffCodeMapping.getLoanType());
		}
		logger.debug(Literal.LEAVING);

		return null;

	}

	@Override
	public List<FinTypeKnockOff> getLoanKnockOffMappingListByLoanType(String finType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT Id, knockOffId, LoanType, knockOffOrder");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" FROM AUTO_KNOCKOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LoanType = :LoanType ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("LoanType", finType);

		RowMapper<FinTypeKnockOff> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeKnockOff.class);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public void update(FinTypeKnockOff kCodeMapping, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update AUTO_KNOCKOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set  knockOffId = :knockOffId,  KnockOffOrder = :KnockOffOrder, Version = :Version");
		sql.append(", LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus");
		sql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId");
		sql.append(", NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where Id = :Id");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(kCodeMapping);

		if (this.jdbcTemplate.update(sql.toString(), beanParameters) <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(FinTypeKnockOff loanKnockOffCodeMapping, String type) {
		logger.debug(Literal.ENTERING);

		if (loanKnockOffCodeMapping.getId() == Long.MIN_VALUE) {
			loanKnockOffCodeMapping.setId(getNextValue("SEQAUTO_KNOCKOFF_LOAN_TYPES"));
		}

		StringBuilder sql = new StringBuilder("Insert Into AUTO_KNOCKOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, knockOffId, loanType, KnockOffOrder, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(")");
		sql.append(" Values(:Id, :knockOffId, :loanType, :knockOffOrder, :Version, :LastMntBy, :LastMntOn");
		sql.append(", :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(loanKnockOffCodeMapping);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return loanKnockOffCodeMapping.getId();
	}

	@Override
	public void delete(FinTypeKnockOff kCodeMapping, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From AUTO_KNOCKOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where KnockOffId = :KnockOffId And Id = :Id");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(kCodeMapping);

		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(long loanTypeId, String tableType) {
		logger.debug(Literal.ENTERING);

		FinTypeKnockOff codeMapping = new FinTypeKnockOff();

		codeMapping.setId(loanTypeId);
		StringBuilder sql = new StringBuilder("Delete From AUTO_KNOCKOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where LoanType =:LoanType");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(codeMapping);

		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(String loanTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "LoanType = :loanTypeCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("AUTO_KNOCKOFF_LOANTYPES", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("AUTO_KNOCKOFF_LOANTYPES_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "AUTO_KNOCKOFF_LOANTYPES_Temp", "AUTO_KNOCKOFF_LOANTYPES" },
					whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("loanTypeCode", loanTypeCode);

		boolean exists = false;
		if (jdbcTemplate.queryForObject(sql, paramSource, Integer.class) > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}

	@Override
	public boolean isExistKnockoffCode(long knockoffId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "KnockOffId = :KnockOffId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("AUTO_KNOCKOFF_LOANTYPES", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("AUTO_KNOCKOFF_LOANTYPES_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "AUTO_KNOCKOFF_LOANTYPES_Temp", "AUTO_KNOCKOFF_LOANTYPES" },
					whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("KnockOffId", knockoffId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
