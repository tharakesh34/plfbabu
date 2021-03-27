package com.pennanttech.pff.commodity.dao;

import java.util.HashMap;
import java.util.Map;

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
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class CommodityTypeDAOImpl extends SequenceDao<CommodityType> implements CommodityTypeDAO {
	private static Logger logger = LogManager.getLogger(CommodityTypeDAOImpl.class);

	public CommodityTypeDAOImpl() {
		super();
	}

	@Override
	public CommodityType getCommodityType(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, Code, Description, UnitType, Active");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From Commodity_Types");
		sql.append(type);
		sql.append(" Where Id = :id");

		CommodityType commodityTYpe = new CommodityType();
		commodityTYpe.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commodityTYpe);
		RowMapper<CommodityType> rowMapper = BeanPropertyRowMapper.newInstance(CommodityType.class);

		try {
			commodityTYpe = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return commodityTYpe;
	}

	@Override
	public String save(CommodityType commodityType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into Commodity_Types");
		sql.append(tableType.getSuffix());
		sql.append("(Id, Code, Description, UnitType, Active, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:id, :code, :Description, :UnitType, :Active, :Version , :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (commodityType.getId() == Long.MIN_VALUE) {
			commodityType.setId(getNextValue("SEQCOMMODITY_TYPES"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commodityType);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(commodityType.getId());
	}

	@Override
	public void update(CommodityType commodityType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update Commodity_Types");
		sql.append(tableType.getSuffix());

		sql.append(" set Code = :code, Description = :Description, UnitType = :UnitType, Active = :Active");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commodityType);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(CommodityType commodityType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from Commodity_Types");
		sql.append(tableType.getSuffix());
		sql.append(" where Id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commodityType);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(CommodityType commodityType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "Code = :code ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Commodity_Types", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Commodity_Types_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Commodity_Types_Temp", "Commodity_Types" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("code", commodityType.getCode());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	public Map<String, Long> getCommodityTypeData() {
		logger.debug(Literal.ENTERING);
		Map<String, Long> map = new HashMap<String, Long>();

		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, Code ");
		sql.append(" From Commodity_Types");

		SqlRowSet rowSet = this.jdbcTemplate.getJdbcOperations().queryForRowSet(sql.toString());
		while (rowSet.next()) {
			map.put(rowSet.getString(2), rowSet.getLong(1));
		}
		logger.debug(Literal.LEAVING);
		return map;

	}
}
