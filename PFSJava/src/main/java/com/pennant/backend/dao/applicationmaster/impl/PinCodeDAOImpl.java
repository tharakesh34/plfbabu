/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : PinCodeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-06-2017 * * Modified Date
 * : 01-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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

import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PinCode</code> with set of CRUD operations.
 */
public class PinCodeDAOImpl extends SequenceDao<PinCode> implements PinCodeDAO {
	private static Logger logger = LogManager.getLogger(PinCodeDAOImpl.class);

	public PinCodeDAOImpl() {
		super();
	}

	@Override
	public PinCode getPinCode(long pinCodeId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" pinCodeId, pinCode, city, areaName, active, groupId,serviceable,");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(",pCCityName");
		}
		sql.append(" From PinCodes");
		sql.append(type);
		sql.append(" Where pinCodeId = :pinCodeId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PinCode pinCode = new PinCode();
		pinCode.setPinCodeId(pinCodeId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		RowMapper<PinCode> rowMapper = BeanPropertyRowMapper.newInstance(PinCode.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long pinCodeId, String city, String areaName, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "City = :City and AreaName = :AreaName AND pinCodeId != :pinCodeId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PinCodes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PinCodes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PinCodes_Temp", "PinCodes" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("pinCodeId", pinCodeId);
		paramSource.addValue("City", city);
		paramSource.addValue("AreaName", areaName);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(PinCode pinCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into PinCodes");
		sql.append(tableType.getSuffix());
		sql.append(" (pinCodeId, pinCode, city, areaName, active,groupId,serviceable, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :pinCodeId, :pinCode, :city, :areaName, :active, :groupId,:serviceable,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (pinCode.getPinCodeId() <= 0) {
			pinCode.setPinCodeId(getNextValue("SeqPinCodes"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(pinCode.getPinCodeId());
	}

	@Override
	public void update(PinCode pinCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PinCodes");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set pinCode = :pinCode, city = :city, areaName= :areaName, active = :active,groupId=:groupId,serviceable=:serviceable, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where pinCodeId = :pinCodeId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PinCode pinCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PinCodes");
		sql.append(tableType.getSuffix());
		sql.append(" where pinCodeId = :pinCodeId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
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
	public boolean isCityCodeExists(String pcCity) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("City", pcCity);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(City)");
		selectSql.append(" From PinCodes_View ");
		selectSql.append(" Where City=:City");

		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}

	@Override
	public PinCode getPinCode(String code, String type) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" pinCodeId, pinCode, city, areaName, active,groupId, serviceable,");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" pCCountry, pCProvince, LovDescPCProvinceName, pCCityName,");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PinCodes");
		sql.append(type);

		sql.append(" Where pinCode = :pinCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PinCode pinCode = new PinCode();
		pinCode.setPinCode(code);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		RowMapper<PinCode> rowMapper = BeanPropertyRowMapper.newInstance(PinCode.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getPinCodeCount(String pinCode, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(pinCode)");
		sql.append(" From PinCodes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PinCode = :PinCode");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PinCode", pinCode);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	@Override
	public PinCode getPinCodeById(long pinCodeId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PinCodeId, PinCode, City, AreaName");
		sql.append(", Active, GroupId, Serviceable");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", PCCountry, PCProvince");
		}

		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PinCodes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PinCodeId = :PinCodeId");

		logger.trace(Literal.SQL + sql.toString());

		PinCode pinCode = new PinCode();
		pinCode.setPinCodeId(pinCodeId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pinCode);
		RowMapper<PinCode> rowMapper = BeanPropertyRowMapper.newInstance(PinCode.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<PinCode> getResult(ISearch search, List<String> roleCodes) {
		List<Object> value = new ArrayList<>();
		String whereCondition = QueryUtil.buildWhereClause(search, value);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PinCodeId, PinCode, PCCityName, AreaName, Active, Version, Lastmntby, Lastmnton");
		sql.append(", Recordstatus, Rolecode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From (Select PinCodeId, PinCode, PCCityName, AreaName, Active, t1.Version, t1.Lastmntby");
		sql.append(", t1.Lastmnton, t1.Recordstatus, t1.Rolecode, t1.NextRoleCode, t1.TaskId, t1.NextTaskId");
		sql.append(", t1.RecordType, t1.WorkFlowId");
		sql.append(" From PinCodes_Temp t1");
		sql.append(" Inner Join RMTProvincevsCity t2 on t1.City = t2.PCCity");
		sql.append(" Union All");
		sql.append(" Select PinCodeId, PinCode, PCCityName, AreaName, Active, t1.Version, t1.Lastmntby, t1.Lastmnton");
		sql.append(", t1.Recordstatus, t1.Rolecode, t1.NextRoleCode, t1.TaskId, t1.NextTaskId, t1.RecordType");
		sql.append(", t1.WorkFlowId");
		sql.append(" From PinCodes t1");
		sql.append(" Inner Join RMTProvincevsCity t2 on t1.City = t2.PCCity");
		sql.append(" Where Not Exists (Select 1 from PinCodes_Temp Where PinCode = t1.PinCode)) t");

		if (!StringUtils.isEmpty(whereCondition)) {
			sql.append(whereCondition);
			sql.append(" and (NextRoleCode is null or NextRoleCode = '' or NextRoleCode in (");
		} else {
			sql.append(" Where (NextRoleCode is null or NextRoleCode = '' or NextRoleCode in (");
		}

		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append("))");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			for (Object object : value) {
				ps.setObject(++index, object);
			}

			for (String roleCode : roleCodes) {
				ps.setString(++index, roleCode);
			}
		}, new PinCodesRM());
	}

	@Override
	public PinCode getPinCodeById(long pinCodeId) {
		StringBuilder sql = getPinCodeQuery();
		sql.append(" Where PinCodeId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), new PinCodeQueryRM(), pinCodeId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public PinCode getPinCode(String code) {
		StringBuilder sql = getPinCodeQuery();
		sql.append(" Where PinCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), new PinCodeQueryRM(), code);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getPinCodeQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PinCodeId, PinCode, City, AreaName");
		sql.append(", Active, GroupId, Serviceable, rm.PCCountry, rm.PCProvince");
		sql.append(", p.Version, p.LastMntOn, p.LastMntBy,p.RecordStatus, p.RoleCode");
		sql.append(", p.NextRoleCode, p.TaskId, p.NextTaskId, p.RecordType, p.WorkflowId");
		sql.append(" From PinCodes p");
		sql.append(" Inner Join RMTProvincevsCity rm ON p.city = rm.pccity");
		return sql;
	}

	private class PinCodeQueryRM implements RowMapper<PinCode> {
		private PinCodeQueryRM() {
			super();
		}

		@Override
		public PinCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			PinCode pinCode = new PinCode();

			pinCode.setPinCodeId(rs.getLong("PinCodeId"));
			pinCode.setPinCode(rs.getString("PinCode"));
			pinCode.setCity(rs.getString("City"));
			pinCode.setAreaName(rs.getString("AreaName"));
			pinCode.setActive(rs.getBoolean("Active"));
			pinCode.setGroupId(JdbcUtil.getLong(rs.getObject("GroupId")));
			pinCode.setServiceable(rs.getBoolean("Serviceable"));
			pinCode.setpCCountry(rs.getString("PCCountry"));
			pinCode.setPCProvince(rs.getString("PCProvince"));
			pinCode.setpCProvince(rs.getString("PCProvince"));
			pinCode.setVersion(rs.getInt("Version"));
			pinCode.setLastMntBy(rs.getLong("LastMntBy"));
			pinCode.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pinCode.setRecordStatus(rs.getString("RecordStatus"));
			pinCode.setRoleCode(rs.getString("RoleCode"));
			pinCode.setNextRoleCode(rs.getString("NextRoleCode"));
			pinCode.setTaskId(rs.getString("TaskId"));
			pinCode.setNextTaskId(rs.getString("NextTaskId"));
			pinCode.setRecordType(rs.getString("RecordType"));
			pinCode.setWorkflowId(rs.getLong("WorkflowId"));

			return pinCode;
		}
	}

	private class PinCodesRM implements RowMapper<PinCode> {

		private PinCodesRM() {
			super();
		}

		@Override
		public PinCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			PinCode pc = new PinCode();

			pc.setPinCodeId(rs.getLong("PinCodeId"));
			pc.setPinCode(rs.getString("PinCode"));
			pc.setPCCityName(rs.getString("PCCityName"));
			pc.setAreaName(rs.getString("AreaName"));
			pc.setActive(rs.getBoolean("Active"));
			pc.setVersion(rs.getInt("Version"));
			pc.setLastMntBy(rs.getLong("LastMntBy"));
			pc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pc.setRecordStatus(rs.getString("RecordStatus"));
			pc.setRoleCode(rs.getString("RoleCode"));
			pc.setNextRoleCode(rs.getString("NextRoleCode"));
			pc.setTaskId(rs.getString("TaskId"));
			pc.setNextTaskId(rs.getString("NextTaskId"));
			pc.setRecordType(rs.getString("RecordType"));
			pc.setWorkflowId(rs.getLong("WorkflowId"));

			return pc;
		}
	}

}
