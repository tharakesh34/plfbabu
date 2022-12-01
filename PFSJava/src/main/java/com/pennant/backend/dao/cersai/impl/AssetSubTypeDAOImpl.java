package com.pennant.backend.dao.cersai.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.cersai.AssetSubTypeDAO;
import com.pennant.backend.model.cersai.AssetSubType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>AssetSubType</code> with set of CRUD operations.
 */
public class AssetSubTypeDAOImpl extends BasicDao<AssetSubType> implements AssetSubTypeDAO {
	private static Logger logger = LogManager.getLogger(AssetSubTypeDAOImpl.class);

	public AssetSubTypeDAOImpl() {
		super();
	}

	@Override
	public AssetSubType getAssetSubType(Long assetTypeId, int id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" assetTypeId, id, description, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(", AssetTypeIdName");
		}
		sql.append(" From CERSAI_AssetSubType");
		sql.append(type);
		sql.append(" Where assetTypeId = :assetTypeId AND id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssetSubType assetSubType = new AssetSubType();
		assetSubType.setAssetTypeId(assetTypeId);
		assetSubType.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetSubType);
		RowMapper<AssetSubType> rowMapper = BeanPropertyRowMapper.newInstance(AssetSubType.class);

		try {
			assetSubType = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			assetSubType = null;
		}

		logger.debug(Literal.LEAVING);
		return assetSubType;
	}

	@Override
	public String save(AssetSubType assetSubType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into CERSAI_AssetSubType");
		sql.append(tableType.getSuffix());
		sql.append(" (assetTypeId, id, description, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :assetTypeId, :id, :description, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetSubType);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(assetSubType.getAssetTypeId());
	}

	@Override
	public void update(AssetSubType assetSubType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update CERSAI_AssetSubType");
		sql.append(tableType.getSuffix());
		sql.append("  set description = :description, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where assetTypeId = :assetTypeId AND id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetSubType);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(AssetSubType assetSubType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CERSAI_AssetSubType");
		sql.append(tableType.getSuffix());
		sql.append(" where assetTypeId = :assetTypeId AND id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetSubType);
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

	public boolean isDuplicateKey(int id, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = " id = :id ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CERSAI_AssetSubType", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CERSAI_AssetSubType_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CERSAI_AssetSubType_Temp", "CERSAI_AssetSubType" },
					whereClause);
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
