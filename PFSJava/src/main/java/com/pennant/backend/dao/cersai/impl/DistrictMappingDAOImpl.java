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

import com.pennant.backend.dao.cersai.DistrictMappingDAO;
import com.pennant.backend.model.cersai.DistrictMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>DistrictMapping</code> with set of CRUD operations.
 */
public class DistrictMappingDAOImpl extends BasicDao<DistrictMapping> implements DistrictMappingDAO {
	private static Logger logger = LogManager.getLogger(DistrictMappingDAOImpl.class);

	public DistrictMappingDAOImpl() {
		super();
	}

	@Override
	public DistrictMapping getDistrictMapping(int mappingType, String district, String mappingValue, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" mappingType, district, mappingValue, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(", DistrictName ");
		}

		sql.append(" From DistrictMapping");
		sql.append(type);
		sql.append(" Where mappingType = :mappingType AND district = :district");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		DistrictMapping districtMapping = new DistrictMapping();
		districtMapping.setMappingType(mappingType);
		districtMapping.setDistrict(district);
		districtMapping.setMappingValue(mappingValue);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(districtMapping);
		RowMapper<DistrictMapping> rowMapper = BeanPropertyRowMapper.newInstance(DistrictMapping.class);

		try {
			districtMapping = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			districtMapping = null;
		}

		logger.debug(Literal.LEAVING);
		return districtMapping;
	}

	@Override
	public String save(DistrictMapping districtMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into DistrictMapping");
		sql.append(tableType.getSuffix());
		sql.append(" (mappingType, district, mappingValue, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :mappingType, :district, :mappingValue, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(districtMapping);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(districtMapping.getMappingType());
	}

	@Override
	public void update(DistrictMapping districtMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update DistrictMapping");
		sql.append(tableType.getSuffix());
		sql.append(
				" set mappingValue = :mappingValue, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where mappingType = :mappingType  and district = :district");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(districtMapping);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(DistrictMapping districtMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from DistrictMapping");
		sql.append(tableType.getSuffix());
		sql.append(" where mappingType = :mappingType AND district = :district AND mappingValue = :mappingValue ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(districtMapping);
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
	public boolean isDuplicateKey(int mappingType, String district, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = " mappingType = :mappingType AND district = :district ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("DistrictMapping", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("DistrictMapping_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "DistrictMapping_Temp", "DistrictMapping" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("mappingType", mappingType);
		paramSource.addValue("district", district);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
