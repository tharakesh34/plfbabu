package com.pennanttech.pff.commodity.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class CommoditiesDAOImpl extends SequenceDao<Commodity> implements CommoditiesDAO {
	private static Logger logger = LogManager.getLogger(CommoditiesDAOImpl.class);

	public CommoditiesDAOImpl() {
		super();
	}

	@Override
	public Commodity getCommodities(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, CommodityType, Code, Description, CurrentValue, HSNCode, Active,");
		sql.append(" AlertsRequired, AlertType, AlertToRoles, UserTemplate, CustomerTemplate");

		if (type.contains("View")) {
			sql.append(
					", CommodityTypeCode, UserTemplateName, CustomerTemplateName, userTemplateCode, customerTemplateCode");
		}

		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From Commodities");
		sql.append(type);
		sql.append(" Where Id = :id");

		Commodity commodity = new Commodity();
		commodity.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commodity);
		RowMapper<Commodity> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Commodity.class);

		try {
			commodity = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);

		return commodity;
	}

	@Override
	public String save(Commodity commodity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into Commodities");
		sql.append(tableType.getSuffix());
		sql.append("(Id, CommodityType, Code, Description, CurrentValue, HSNCode, Active,");
		sql.append(" AlertsRequired, AlertType, AlertToRoles, UserTemplate, CustomerTemplate");
		sql.append(", Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:Id, :CommodityType, :Code, :Description, :CurrentValue, :HSNCode, :Active,");
		sql.append(" :AlertsRequired, :AlertType, :AlertToRoles, :UserTemplate, :CustomerTemplate");
		sql.append(", :Version");
		sql.append(", :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId");
		sql.append(", :RecordType, :WorkflowId)");

		if (commodity.getId() == Long.MIN_VALUE) {
			commodity.setId(getNextValue("SEQCOMMODITIES"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commodity);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(commodity.getId());
	}

	@Override
	public void update(Commodity commodity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update Commodities");
		sql.append(tableType.getSuffix());
		sql.append(" set Description = :Description, CurrentValue = :CurrentValue, HSNCode = :HSNCode,");
		sql.append(
				" AlertsRequired= :AlertsRequired, AlertType= :AlertType, AlertToRoles= :AlertToRoles, UserTemplate= :UserTemplate, CustomerTemplate= :CustomerTemplate");
		sql.append(", Active = :Active, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :Id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commodity);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(Commodity commodity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from Commodities");
		sql.append(tableType.getSuffix());
		sql.append(" where Id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commodity);
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
	public boolean isDuplicateKey(Commodity commodity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "HSNCode = :HSNCode And Code = :Code ";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Commodities", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Commodities_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Commodities_Temp", "Commodities" }, whereClause);
			break;
		}
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("HSNCode", commodity.getHSNCode());
		paramSource.addValue("Code", commodity.getCode());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	public void saveCommoditiesLog(MapSqlParameterSource mapData) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert into Commodities_Log");
		sql.append("(CommodityId, AuditImage, CurrentValue, BatchId, ModifiedBy, ModifiedOn)");
		sql.append(" values");
		sql.append("(:CommodityId, :AuditImage, :CurrentValue, :BatchId, :ModifiedBy, :ModifiedOn)");
		logger.trace(Literal.SQL + sql.toString());
		try {
			jdbcTemplate.update(sql.toString(), mapData);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public Commodity getCommodity(Commodity commodity) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Code", commodity.getCode());
		paramSource.addValue("HSNCode", commodity.getHSNCode());

		StringBuilder sql = new StringBuilder();
		sql.append("select * from COMMODITIES Where");
		sql.append(" Code = :Code");
		sql.append(" and HSNCode = :HSNCode");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<Commodity> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Commodity.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {

		}

		return null;
	}

	@Override
	public String getCommodityHSNCode(String code) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Code", code);

		StringBuilder sql = new StringBuilder();
		sql.append("select HSNCode from COMMODITIES_View Where");
		sql.append(" Code = :Code");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, String.class);
		} catch (EmptyResultDataAccessException e) {

		}

		return null;
	}

	@Override
	public String getCommodityCode(String hsnCode) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("HSNCode", hsnCode);

		StringBuilder sql = new StringBuilder();
		sql.append("select Code from COMMODITIES_View Where");
		sql.append(" HSNCode = :HSNCode");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, String.class);
		} catch (EmptyResultDataAccessException e) {

		}

		return null;
	}

	@Override
	public boolean isDuplicateCode(Commodity commodity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "Code = :Code";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Commodities", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Commodities_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Commodities_Temp", "Commodities" }, whereClause);
			break;
		}
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Code", commodity.getCode());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public boolean isDuplicateHSNCode(Commodity commodity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "HsnCode = :HSNCode";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Commodities", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Commodities_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Commodities_Temp", "Commodities" }, whereClause);
			break;
		}
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("HSNCode", commodity.getHSNCode());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public void updateCommodity(Commodity commodity) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update Commodities");
		sql.append(" set CurrentValue = :CurrentValue, Version = :Version ");
		sql.append(" where HSNCode = :HSNCode ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSources = new BeanPropertySqlParameterSource(commodity);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSources);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
}
