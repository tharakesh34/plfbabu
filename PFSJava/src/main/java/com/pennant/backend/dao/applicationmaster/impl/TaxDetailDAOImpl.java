/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  TaxDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2017    														*
 *                                                                  						*
 * Modified Date    :  14-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.TaxDetailDAO;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>TaxDetail</code> with set of CRUD
 * operations.
 */
public class TaxDetailDAOImpl extends SequenceDao<TaxDetail> implements TaxDetailDAO {
	private static Logger logger = Logger.getLogger(TaxDetailDAOImpl.class);

	public TaxDetailDAOImpl() {
		super();
	}

	@Override
	public TaxDetail getTaxDetail(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, country, stateCode, entityCode, taxCode, addressLine1, ");
		sql.append(" addressLine2, addressLine3, addressLine4, pinCode, cityCode, hsnNumber, natureService, ");

		if (type.contains("View")) {
			sql.append(
					"addressLine2, addressLine3, addressLine4, pinCode, cityName, countryName,provinceName,entityDesc, GstInAvailable,");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From TAXDETAIL");
		sql.append(type);
		sql.append(" Where id = :Id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		TaxDetail taxDetail = new TaxDetail();
		taxDetail.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxDetail);
		RowMapper<TaxDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TaxDetail.class);

		try {
			taxDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			taxDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return taxDetail;
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
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :Id, :Country, :StateCode, :EntityCode, :TaxCode, :AddressLine1, ");
		sql.append(" :AddressLine2, :AddressLine3, :AddressLine4, :PinCode, :CityCode, :HsnNumber, :NatureService, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

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
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("select id, country, stateCode, entityCode, taxCode, addressLine1, ");
		sql.append(" addressLine2, addressLine3, addressLine4, pinCode, cityCode, hsnNumber, natureService, ");

		if (type.contains("View")) {
			sql.append(
					"addressLine2, addressLine3, addressLine4, pinCode, cityName, countryName,provinceName,entityDesc, GstInAvailable,");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From TAXDETAIL");
		sql.append(type);
		sql.append(" Where StateCode = :StateCode");

		source.addValue("StateCode", statecode);

		RowMapper<TaxDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TaxDetail.class);

		logger.debug("selectSql: " + sql.toString());
		logger.debug("Leaving");

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public int getGSTNumberCount(String entityCode, String taxCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select count(TaxCode) From TAXDETAIL");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ENTITYCODE <> :ENTITYCODE And TaxCode = :TaxCode");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("ENTITYCODE", entityCode);
		source.addValue("TaxCode", taxCode);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}
}
