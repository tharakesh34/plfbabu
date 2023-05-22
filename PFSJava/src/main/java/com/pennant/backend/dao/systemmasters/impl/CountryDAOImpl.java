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
 * * FileName : CountryDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified Date
 * : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.systemmasters.impl;

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

import com.pennant.backend.dao.smtmasters.CountryDAO;
import com.pennant.backend.model.systemmasters.Country;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Country model</b> class.<br>
 * 
 */
public class CountryDAOImpl extends BasicDao<Country> implements CountryDAO {
	private static Logger logger = LogManager.getLogger(CountryDAOImpl.class);

	public CountryDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Country details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Country
	 */
	@Override
	public Country getCountryById(final String id, String type) {
		logger.debug("Entering");

		Country country = new Country();
		country.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				"SELECT CountryCode, CountryDesc, CountryParentLimit, CountryResidenceLimit,CountryRiskLimit, CountryIsActive,SystemDefault,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  BMTCountries");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CountryCode =:CountryCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);
		RowMapper<Country> typeRowMapper = BeanPropertyRowMapper.newInstance(Country.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(String countryCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "CountryCode = :countryCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTCountries", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTCountries_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTCountries_Temp", "BMTCountries" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("countryCode", countryCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(Country country, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into BMTCountries");
		sql.append(tableType.getSuffix());
		sql.append(" (CountryCode, CountryDesc, CountryParentLimit, CountryResidenceLimit,");
		sql.append(" CountryRiskLimit, CountryIsActive,SystemDefault,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values(:CountryCode, :CountryDesc, :CountryParentLimit, :CountryResidenceLimit,");
		sql.append(" :CountryRiskLimit, :CountryIsActive,:SystemDefault,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(country);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return country.getId();
	}

	@Override
	public void update(Country country, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update BMTCountries");
		sql.append(tableType.getSuffix());
		sql.append(" set CountryDesc = :CountryDesc, CountryParentLimit = :CountryParentLimit,");
		sql.append(" CountryResidenceLimit = :CountryResidenceLimit, CountryRiskLimit = :CountryRiskLimit,");
		sql.append(" CountryIsActive = :CountryIsActive, SystemDefault = :SystemDefault,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where CountryCode =:CountryCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(country);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(Country country, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BMTCountries");
		sql.append(tableType.getSuffix());
		sql.append(" where CountryCode =:countryCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(country);
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

	/**
	 * Fetch the count of system default values by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return Gender
	 */
	@Override
	public String getSystemDefaultCount(String countryCode) {
		logger.debug("Entering");
		Country country = new Country();
		country.setCountryCode(countryCode);
		country.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT CountryCode FROM  BMTCountries_View ");
		selectSql.append(" Where CountryCode != :CountryCode and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return "";
		}
	}

	@Override
	public boolean isExistCountryCode(String code) {
		String sql = "Select Count(CPCountry) From RMTCountryVsProvince  Where CPCountry = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, code) > 0;
	}

	@Override
	public boolean isActiveCountry(String code) {
		String sql = "Select Count(CountryCode) From bmtCountries Where CountryCode = ? and CountryIsActive = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, code, 1) > 0;
	}
}