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
 * * FileName : TaxDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-06-2017 * * Modified
 * Date : 14-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.TaxDetailDAO;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>TaxDetail</code> with set of CRUD operations.
 */
public class TaxDetailDAOImpl extends SequenceDao<TaxDetail> implements TaxDetailDAO {
	private static Logger logger = LogManager.getLogger(TaxDetailDAOImpl.class);

	public TaxDetailDAOImpl() {
		super();
	}

	@Override
	public TaxDetail getTaxDetail(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Country, StateCode, EntityCode, TaxCode, AddressLine1, AddressLine2, AddressLine3");
		sql.append(", AddressLine4, PinCode, CityCode, HsnNumber, NatureService, PinCodeId");

		if (type.contains("View")) {
			sql.append(", CityName, CountryName, ProvinceName, EntityDesc");
			sql.append(", GstinAvailable, AreaName");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from TAXDETAIL");
		sql.append(type);
		sql.append(" Where id = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<TaxDetail>() {
				@Override
				public TaxDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					TaxDetail td = new TaxDetail();

					td.setId(rs.getLong("Id"));
					td.setCountry(rs.getString("Country"));
					td.setStateCode(rs.getString("StateCode"));
					td.setEntityCode(rs.getString("EntityCode"));
					td.setTaxCode(rs.getString("TaxCode"));
					td.setAddressLine1(rs.getString("AddressLine1"));
					td.setAddressLine2(rs.getString("AddressLine2"));
					td.setAddressLine3(rs.getString("AddressLine3"));
					td.setAddressLine4(rs.getString("AddressLine4"));
					td.setPinCode(rs.getString("PinCode"));
					td.setCityCode(rs.getString("CityCode"));
					td.setHsnNumber(rs.getString("HsnNumber"));
					td.setNatureService(rs.getString("NatureService"));
					td.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

					if (type.contains("View")) {
						td.setCityName(rs.getString("CityName"));
						td.setCountryName(rs.getString("CountryName"));
						td.setProvinceName(rs.getString("ProvinceName"));
						td.setEntityDesc(rs.getString("EntityDesc"));
						td.setGstinAvailable(rs.getBoolean("GstinAvailable"));
						td.setAreaName(rs.getString("AreaName"));
					}

					td.setVersion(rs.getInt("Version"));
					td.setLastMntOn(rs.getTimestamp("LastMntOn"));
					td.setLastMntBy(rs.getLong("LastMntBy"));
					td.setRecordStatus(rs.getString("RecordStatus"));
					td.setRoleCode(rs.getString("RoleCode"));
					td.setNextRoleCode(rs.getString("NextRoleCode"));
					td.setTaskId(rs.getString("TaskId"));
					td.setNextTaskId(rs.getString("NextTaskId"));
					td.setRecordType(rs.getString("RecordType"));
					td.setWorkflowId(rs.getLong("WorkflowId"));

					return td;
				}
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long id, String taxCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "taxCode = :taxCode AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("TAXDETAIL", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("TAXDETAIL_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "TAXDETAIL_Temp", "TAXDETAIL" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("taxCode", taxCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(TaxDetail taxDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into TAXDETAIL");
		sql.append(tableType.getSuffix());
		sql.append("(id, country, stateCode, entityCode, taxCode, addressLine1, ");
		sql.append(" addressLine2, addressLine3, addressLine4, pinCode, cityCode, hsnNumber, natureService, ");
		sql.append(" pinCodeId, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :Id, :Country, :StateCode, :EntityCode, :TaxCode, :AddressLine1, :AddressLine2,");
		sql.append(" :AddressLine3, :AddressLine4, :PinCode, :CityCode, :HsnNumber, :NatureService, :pinCodeId,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		sql.append(" :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (taxDetail.getId() <= 0) {
			taxDetail.setId(getNextValue("SeqTaxDetail"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(taxDetail.getId());
	}

	@Override
	public void update(TaxDetail taxDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update TAXDETAIL");
		sql.append(tableType.getSuffix());
		sql.append("  set country = :Country, stateCode = :StateCode, entityCode = :EntityCode, ");
		sql.append(" taxCode = :TaxCode, addressLine1 = :AddressLine1, addressLine2 = :AddressLine2, ");
		sql.append(" addressLine3 = :AddressLine3, addressLine4 = :AddressLine4, pinCode = :PinCode, ");
		sql.append(" cityCode = :CityCode, hsnNumber = :hsnNumber, natureService = :natureService,");
		sql.append(" pinCodeId = :PinCodeId, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :Id ");
		/* sql.append(QueryUtil.getConcurrencyCondition(tableType)); */

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(TaxDetail taxDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from TAXDETAIL");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :Id ");
		/* sql.append(QueryUtil.getConcurrencyCondition(tableType)); */

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxDetail);

		try {
			this.jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<TaxDetail> getTaxDetailbystateCode(String statecode, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Country, StateCode, EntityCode, TaxCode, AddressLine1, AddressLine2, AddressLine3");
		sql.append(", AddressLine4, PinCode, CityCode, HsnNumber, NatureService, PinCodeId, Version, LastMntOn");
		sql.append(", LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CityName, CountryName, ProvinceName, EntityDesc, GstinAvailable");
		}

		sql.append(" from TaxDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where StateCode= ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index, statecode);
			}
		}, new RowMapper<TaxDetail>() {
			@Override
			public TaxDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxDetail td = new TaxDetail();

				td.setId(rs.getLong("Id"));
				td.setCountry(rs.getString("Country"));
				td.setStateCode(rs.getString("StateCode"));
				td.setEntityCode(rs.getString("EntityCode"));
				td.setTaxCode(rs.getString("TaxCode"));
				td.setAddressLine1(rs.getString("AddressLine1"));
				td.setAddressLine2(rs.getString("AddressLine2"));
				td.setAddressLine3(rs.getString("AddressLine3"));
				td.setAddressLine4(rs.getString("AddressLine4"));
				td.setPinCode(rs.getString("PinCode"));
				td.setCityCode(rs.getString("CityCode"));
				td.setHsnNumber(rs.getString("HsnNumber"));
				td.setNatureService(rs.getString("NatureService"));
				td.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));
				td.setVersion(rs.getInt("Version"));
				td.setLastMntOn(rs.getTimestamp("LastMntOn"));
				td.setLastMntBy(rs.getLong("LastMntBy"));
				td.setRecordStatus(rs.getString("RecordStatus"));
				td.setRoleCode(rs.getString("RoleCode"));
				td.setNextRoleCode(rs.getString("NextRoleCode"));
				td.setTaskId(rs.getString("TaskId"));
				td.setNextTaskId(rs.getString("NextTaskId"));
				td.setRecordType(rs.getString("RecordType"));
				td.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					td.setCityName(rs.getString("CityName"));
					td.setCountryName(rs.getString("CountryName"));
					td.setProvinceName(rs.getString("ProvinceName"));
					td.setEntityDesc(rs.getString("EntityDesc"));
					td.setGstinAvailable(rs.getBoolean("GstinAvailable"));
				}

				return td;
			}
		});
	}

	@Override
	public int getGSTNumberCount(String entityCode, String taxCode, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select count(TaxCode) From TAXDETAIL");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ENTITYCODE <> :ENTITYCODE And TaxCode = :TaxCode");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ENTITYCODE", entityCode);
		source.addValue("TaxCode", taxCode);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public List<TaxDetail> getTaxDetailsbyEntityCode(String statecode, String type, String entityCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Country, StateCode, EntityCode, TaxCode, AddressLine1, AddressLine2, AddressLine3");
		sql.append(", AddressLine4, PinCode, CityCode, HsnNumber, NatureService");

		if (type.contains("View")) {
			sql.append(", CityName, CountryName, ProvinceName, EntityDesc, GstinAvailable");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from TAXDETAIL");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where StateCode = ? and EntityCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, statecode);
			ps.setString(index, entityCode);
		}, (rs, rowNum) -> {
			TaxDetail td = new TaxDetail();

			td.setId(rs.getLong("Id"));
			td.setCountry(rs.getString("Country"));
			td.setStateCode(rs.getString("StateCode"));
			td.setEntityCode(rs.getString("EntityCode"));
			td.setTaxCode(rs.getString("TaxCode"));
			td.setAddressLine1(rs.getString("AddressLine1"));
			td.setAddressLine2(rs.getString("AddressLine2"));
			td.setAddressLine3(rs.getString("AddressLine3"));
			td.setAddressLine4(rs.getString("AddressLine4"));
			td.setPinCode(rs.getString("PinCode"));
			td.setCityCode(rs.getString("CityCode"));
			td.setHsnNumber(rs.getString("HsnNumber"));
			td.setNatureService(rs.getString("NatureService"));

			if (type.contains("View")) {
				td.setCityName(rs.getString("CityName"));
				td.setCountryName(rs.getString("CountryName"));
				td.setProvinceName(rs.getString("ProvinceName"));
				td.setEntityDesc(rs.getString("EntityDesc"));
				td.setGstinAvailable(rs.getBoolean("GstinAvailable"));
			}

			td.setVersion(rs.getInt("Version"));
			td.setLastMntOn(rs.getTimestamp("LastMntOn"));
			td.setLastMntBy(rs.getLong("LastMntBy"));
			td.setRecordStatus(rs.getString("RecordStatus"));
			td.setRoleCode(rs.getString("RoleCode"));
			td.setNextRoleCode(rs.getString("NextRoleCode"));
			td.setTaskId(rs.getString("TaskId"));
			td.setNextTaskId(rs.getString("NextTaskId"));
			td.setRecordType(rs.getString("RecordType"));
			td.setWorkflowId(rs.getLong("WorkflowId"));

			return td;
		});
	}

	@Override
	public int getStateAndEntityCodeCount(String entityCode, String stateCode, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select count(*) From TAXDETAIL");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ENTITYCODE = :ENTITYCODE And StateCode = :StateCode");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ENTITYCODE", entityCode);
		source.addValue("StateCode", stateCode);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}
}
