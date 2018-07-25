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
 * FileName    		:  LegalPropertyDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.legal.impl;

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

import com.pennant.backend.dao.legal.LegalPropertyDetailDAO;
import com.pennant.backend.model.legal.LegalPropertyDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalPropertyDetail</code> with
 * set of CRUD operations.
 */
public class LegalPropertyDetailDAOImpl extends SequenceDao<LegalPropertyDetail> implements LegalPropertyDetailDAO {
	private static Logger logger = Logger.getLogger(LegalPropertyDetailDAOImpl.class);


	public LegalPropertyDetailDAOImpl() {
		super();
	}

	@Override
	public LegalPropertyDetail getLegalPropertyDetail(long legalPropertyId, long legalId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalId, legalPropertyId, scheduleType, propertySchedule, propertyType, northBy, ");
		sql.append(" southBy, eastBy, westBy, measurement, registrationOffice, registrationDistrict, ");
		sql.append(" propertyOwner, ");
		sql.append( " Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalPropertyDetails");
		sql.append(type);
		sql.append(" Where legalPropertyId = :legalPropertyId AND legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalPropertyDetail legalPropertyDetail = new LegalPropertyDetail();
		legalPropertyDetail.setLegalPropertyId(legalPropertyId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyDetail);
		RowMapper<LegalPropertyDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalPropertyDetail.class);
		try {
			legalPropertyDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalPropertyDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return legalPropertyDetail;
	}
	
	@Override
	public List<LegalPropertyDetail> getPropertyDetailsList(long legalId, String type) {
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalId, legalPropertyId, scheduleType, propertySchedule, propertyType, northBy, ");
		sql.append(" southBy, eastBy, westBy, measurement, registrationOffice, registrationDistrict, ");
		sql.append(" propertyOwner, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalPropertyDetails");
		sql.append(type);
		sql.append(" Where legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("legalId", legalId);

		RowMapper<LegalPropertyDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalPropertyDetail.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		} finally {
			source = null;
			sql = null;
		}
		return null;
	}

	@Override
	public String save(LegalPropertyDetail legalPropertyDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into LegalPropertyDetails");
		sql.append(tableType.getSuffix());
		sql.append("(legalPropertyId, legalId, scheduleType, propertySchedule, propertyType, northBy, ");
		sql.append("southBy, eastBy, westBy, measurement, registrationOffice, registrationDistrict, ");
		sql.append(" propertyOwner, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalPropertyId, :legalId, :scheduleType, :propertySchedule, :propertyType, :northBy, ");
		sql.append(" :southBy, :eastBy, :westBy, :measurement, :registrationOffice, :registrationDistrict, ");
		sql.append(" :propertyOwner, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalPropertyDetail.getLegalPropertyId() == Long.MIN_VALUE) {
			legalPropertyDetail.setLegalPropertyId(getNextId("SeqLegalPropertyDetails"));
			logger.debug("get NextID:" + legalPropertyDetail.getLegalPropertyId());
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyDetail);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(legalPropertyDetail.getLegalPropertyId());
	}

	@Override
	public void update(LegalPropertyDetail legalPropertyDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update LegalPropertyDetails");
		sql.append(tableType.getSuffix());
		sql.append("  set legalId = :legalId, scheduleType = :scheduleType, propertySchedule = :propertySchedule, ");
		sql.append(" propertyType = :propertyType, northBy = :northBy, southBy = :southBy, ");
		sql.append(" eastBy = :eastBy, westBy = :westBy, measurement = :measurement, ");
		sql.append(" registrationOffice = :registrationOffice, registrationDistrict = :registrationDistrict, propertyOwner = :propertyOwner, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalPropertyId = :legalPropertyId AND legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalPropertyDetail legalPropertyDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from LegalPropertyDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where legalPropertyId = :legalPropertyId AND legalId = :legalId");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyDetail);
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
	public void deleteList(LegalPropertyDetail propertyDetail, String tableType) {
		StringBuilder deleteSql = new StringBuilder("Delete From LegalPropertyDetails");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where legalId = :legalId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
	}
	
	
}
