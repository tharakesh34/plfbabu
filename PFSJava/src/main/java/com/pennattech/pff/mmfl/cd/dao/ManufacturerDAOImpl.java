package com.pennattech.pff.mmfl.cd.dao;

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
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.mmfl.cd.model.Manufacturer;

public class ManufacturerDAOImpl extends SequenceDao<Manufacturer> implements ManufacturerDAO {
	private static Logger logger = LogManager.getLogger(ManufacturerDAOImpl.class);

	public ManufacturerDAOImpl() {
		super();
	}

	@Override
	public Manufacturer getManufacturer(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select ManufacturerId, Name, Description, Channel, Active ");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From CD_MANUFACTURERS");
		sql.append(type);
		sql.append(" Where ManufacturerId = :manufacturerId");

		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setManufacturerId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manufacturer);
		RowMapper<Manufacturer> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Manufacturer.class);

		try {
			manufacturer = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return manufacturer;
	}

	@Override
	public String save(Manufacturer manufacturer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into CD_MANUFACTURERS");
		sql.append(tableType.getSuffix());
		sql.append("(ManufacturerId, Name, Description, Channel, Active, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:manufacturerId, :name, :description, :channel, :active, :Version , :LastMntBy, :LastMntOn");
		sql.append(", :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (manufacturer.getManufacturerId() == Long.MIN_VALUE) {
			manufacturer.setManufacturerId(getNextValue("SEQCD_MANUFACTURERS"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manufacturer);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(manufacturer.getManufacturerId());
	}

	@Override
	public void update(Manufacturer manufacturer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CD_MANUFACTURERS");
		sql.append(tableType.getSuffix());
		sql.append(" set Name = :name, Description = :description, Channel = :channel, Active = :active");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where ManufacturerId = :manufacturerId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manufacturer);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(Manufacturer manufacturer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from CD_MANUFACTURERS");
		sql.append(tableType.getSuffix());
		sql.append(" where ManufacturerId = :manufacturerId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manufacturer);
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
	public boolean isDuplicateKey(Manufacturer manufacturer, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "ManufacturerId = :manufacturerId ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_MANUFACTURERS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_MANUFACTURERS_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_MANUFACTURERS_Temp", "CD_MANUFACTURERS" }, whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("manufacturerId", manufacturer.getManufacturerId());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
