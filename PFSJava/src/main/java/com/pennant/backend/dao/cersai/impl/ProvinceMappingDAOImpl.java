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

import com.pennant.backend.dao.cersai.ProvinceMappingDAO;
import com.pennant.backend.model.cersai.ProvinceMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ProvinceMapping</code> with set of CRUD operations.
 */
public class ProvinceMappingDAOImpl extends BasicDao<ProvinceMapping> implements ProvinceMappingDAO {
	private static Logger logger = LogManager.getLogger(ProvinceMappingDAOImpl.class);

	public ProvinceMappingDAOImpl() {
		super();
	}

	@Override
	public ProvinceMapping getProvinceMapping(int mappingType, String province, String mappingValue, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" mappingType, province, mappingValue, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(", ProvinceName ");
		}

		sql.append(" From ProvinceMapping");
		sql.append(type);
		sql.append(" Where mappingType = :mappingType AND province = :province ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ProvinceMapping provinceMapping = new ProvinceMapping();
		provinceMapping.setMappingType(mappingType);
		provinceMapping.setProvince(province);
		provinceMapping.setMappingValue(mappingValue);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(provinceMapping);
		RowMapper<ProvinceMapping> rowMapper = BeanPropertyRowMapper.newInstance(ProvinceMapping.class);

		try {
			provinceMapping = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			provinceMapping = null;
		}

		logger.debug(Literal.LEAVING);
		return provinceMapping;
	}

	@Override
	public String save(ProvinceMapping provinceMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into ProvinceMapping");
		sql.append(tableType.getSuffix());
		sql.append(" (mappingType, province, mappingValue, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :mappingType, :province, :mappingValue, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(provinceMapping);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(provinceMapping.getMappingType());
	}

	@Override
	public void update(ProvinceMapping provinceMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ProvinceMapping");
		sql.append(tableType.getSuffix());
		sql.append(
				" set  mappingValue = :mappingValue, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where mappingType = :mappingType and province = :province");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(provinceMapping);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ProvinceMapping provinceMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ProvinceMapping");
		sql.append(tableType.getSuffix());
		sql.append(" where mappingType = :mappingType AND province = :province AND mappingValue = :mappingValue ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(provinceMapping);
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

	public boolean isDuplicateKey(int mappingType, String province, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = " mappingType = :mappingType AND province = :province";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PROVINCEMAPPING", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PROVINCEMAPPING_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PROVINCEMAPPING_Temp", "PROVINCEMAPPING" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("mappingType", mappingType);
		paramSource.addValue("province", province);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
