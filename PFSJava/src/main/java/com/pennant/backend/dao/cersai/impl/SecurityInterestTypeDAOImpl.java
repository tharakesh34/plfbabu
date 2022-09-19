package com.pennant.backend.dao.cersai.impl;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.cersai.SecurityInterestTypeDAO;
import com.pennant.backend.model.cersai.SecurityInterestType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>SecurityInterestType</code> with set of CRUD operations.
 */
public class SecurityInterestTypeDAOImpl extends BasicDao<SecurityInterestType> implements SecurityInterestTypeDAO {
	private static Logger logger = LogManager.getLogger(SecurityInterestTypeDAOImpl.class);

	public SecurityInterestTypeDAOImpl() {
		super();
	}

	@Override
	public SecurityInterestType getSecurityInterestType(String assetCategoryId, int id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" assetCategoryId, id, description, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(", AssetCategoryIdName");
		}
		sql.append(" From CERSAI_SIType");
		sql.append(type);
		sql.append(" Where assetCategoryId = :assetCategoryId AND id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SecurityInterestType securityInterestType = new SecurityInterestType();
		securityInterestType.setAssetCategoryId(assetCategoryId);
		securityInterestType.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(securityInterestType);
		RowMapper<SecurityInterestType> rowMapper = BeanPropertyRowMapper.newInstance(SecurityInterestType.class);

		try {
			securityInterestType = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			securityInterestType = null;
		}

		logger.debug(Literal.LEAVING);
		return securityInterestType;
	}

	@Override
	public String save(SecurityInterestType securityInterestType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into CERSAI_SIType");
		sql.append(tableType.getSuffix());
		sql.append(" (assetCategoryId, id, description, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :assetCategoryId, :id, :description, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(securityInterestType);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(securityInterestType.getAssetCategoryId());
	}

	@Override
	public void update(SecurityInterestType securityInterestType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update CERSAI_SIType");
		sql.append(tableType.getSuffix());
		sql.append("  set description = :description, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where assetCategoryId = :assetCategoryId AND id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(securityInterestType);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SecurityInterestType securityInterestType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CERSAI_SIType");
		sql.append(tableType.getSuffix());
		sql.append(" where assetCategoryId = :assetCategoryId AND id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(securityInterestType);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(int id, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = " id = :id ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CERSAI_SIType", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CERSAI_SIType_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CERSAI_SIType_Temp", "CERSAI_SIType" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
