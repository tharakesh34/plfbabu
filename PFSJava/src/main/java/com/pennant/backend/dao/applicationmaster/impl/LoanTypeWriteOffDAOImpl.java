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

import com.pennant.backend.dao.applicationmaster.LoanTypeWriteOffDAO;
import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class LoanTypeWriteOffDAOImpl extends SequenceDao<FinTypeWriteOff> implements LoanTypeWriteOffDAO {

	private static final Logger logger = LogManager.getLogger(LoanTypeWriteOffDAOImpl.class);

	public LoanTypeWriteOffDAOImpl() {
	}

	@Override
	public FinTypeWriteOff getLoanWriteOffMappingByID(FinTypeWriteOff writeOffMapping, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT Id, PSLCode, LoanType, DPDDays");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" FROM AUTO_WRITEOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = :Id And LoanType = :LoanType");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(writeOffMapping);
		RowMapper<FinTypeWriteOff> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeWriteOff.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("LoanType Write Mapping details not found  with Id {} and LoanType {}", writeOffMapping.getId(),
					writeOffMapping.getLoanType());
		}
		logger.debug(Literal.LEAVING);

		return null;

	}

	@Override
	public List<FinTypeWriteOff> getLoanWriteOffMappingListByLoanType(String finType, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT Id, PSLCode, LoanType, DPDDays");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" FROM AUTO_WRITEOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LoanType = :LoanType ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("LoanType", finType);

		RowMapper<FinTypeWriteOff> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeWriteOff.class);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public void update(FinTypeWriteOff kCodeMapping, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update AUTO_WRITEOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set  PslCode = :PslCode,  DpdDays = :DpdDays, Version = :Version");
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
	public long save(FinTypeWriteOff loanWriteOffCodeMapping, String type) {
		logger.debug(Literal.ENTERING);

		if (loanWriteOffCodeMapping.getId() == Long.MIN_VALUE) {
			loanWriteOffCodeMapping.setId(getNextValue("SEQAUTO_WRITEOFF_LOAN_TYPES"));
		}

		StringBuilder sql = new StringBuilder("Insert Into AUTO_WRITEOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, PslCode, loanType, DpdDays, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(")");
		sql.append(" Values(:Id, :PslCode, :loanType, :DpdDays, :Version, :LastMntBy, :LastMntOn");
		sql.append(", :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(loanWriteOffCodeMapping);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return loanWriteOffCodeMapping.getId();
	}

	@Override
	public void delete(FinTypeWriteOff kCodeMapping, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From AUTO_WRITEOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PslCode = :PslCode And Id = :Id");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(kCodeMapping);

		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(long loanTypeId, String tableType) {
		logger.debug(Literal.ENTERING);

		FinTypeWriteOff codeMapping = new FinTypeWriteOff();

		codeMapping.setId(loanTypeId);
		StringBuilder sql = new StringBuilder("Delete From AUTO_WRITEOFF_LOANTYPES");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where LoanType =:LoanType");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(codeMapping);

		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(String loanType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "LoanType = :loanType";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("AUTO_WRITEOFF_LOANTYPES", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("AUTO_WRITEOFF_LOANTYPES_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "AUTO_WRITEOFF_LOANTYPES_Temp", "AUTO_WRITEOFF_LOANTYPES" },
					whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("loanType", loanType);

		boolean exists = false;
		if (jdbcTemplate.queryForObject(sql, paramSource, Integer.class) > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}

	@Override
	public boolean isExistWriteoffCode(long writeoffId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "WriteOffId = :WriteOffId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("AUTO_WRITEOFF_LOANTYPES", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("AUTO_WRITEOFF_LOANTYPES_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "AUTO_WRITEOFF_LOANTYPES_Temp", "AUTO_WRITEOFF_LOANTYPES" },
					whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("WriteOffId", writeoffId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
