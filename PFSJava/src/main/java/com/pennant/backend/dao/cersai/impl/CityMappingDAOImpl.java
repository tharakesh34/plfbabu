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

import com.pennant.backend.dao.cersai.CityMappingDAO;
import com.pennant.backend.model.cersai.CityMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>CityMapping</code> with set of CRUD operations.
 */
public class CityMappingDAOImpl extends BasicDao<CityMapping> implements CityMappingDAO {
	private static Logger logger = LogManager.getLogger(CityMappingDAOImpl.class);

	public CityMappingDAOImpl() {
		super();
	}

	@Override
	public CityMapping getCityMapping(int mappingType, String cityCode, String mappingValue, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" mappingType, cityCode, mappingValue, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(", CityCodeName");
		}
		sql.append(" From CityMapping");
		sql.append(type);
		sql.append(" Where mappingType = :mappingType AND cityCode = :cityCode ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		CityMapping cityMapping = new CityMapping();
		cityMapping.setMappingType(mappingType);
		cityMapping.setCityCode(cityCode);
		cityMapping.setMappingValue(mappingValue);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cityMapping);
		RowMapper<CityMapping> rowMapper = BeanPropertyRowMapper.newInstance(CityMapping.class);

		try {
			cityMapping = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			cityMapping = null;
		}

		logger.debug(Literal.LEAVING);
		return cityMapping;
	}

	@Override
	public String save(CityMapping cityMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into CityMapping");
		sql.append(tableType.getSuffix());
		sql.append(" (mappingType, cityCode, mappingValue, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :mappingType, :cityCode, :mappingValue, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cityMapping);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(cityMapping.getMappingType());
	}

	@Override
	public void update(CityMapping cityMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update CityMapping");
		sql.append(tableType.getSuffix());
		sql.append(
				" set  mappingValue = :mappingValue, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where mappingType = :mappingType  and cityCode = :cityCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cityMapping);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(CityMapping cityMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CityMapping");
		sql.append(tableType.getSuffix());
		sql.append(" where mappingType = :mappingType AND cityCode = :cityCode AND mappingValue = :mappingValue ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(cityMapping);
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

	public boolean isDuplicateKey(int mappingType, String cityCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = " mappingType = :mappingType AND cityCode = :cityCode ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CITYMAPPING", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CITYMAPPING_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CITYMAPPING_Temp", "CITYMAPPING" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("mappingType", mappingType);
		paramSource.addValue("cityCode", cityCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}
