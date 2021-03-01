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

import java.sql.PreparedStatement;
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
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
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
 * Data access layer implementation for <code>LegalPropertyDetail</code> with set of CRUD operations.
 */
public class LegalPropertyDetailDAOImpl extends SequenceDao<LegalPropertyDetail> implements LegalPropertyDetailDAO {
	private static Logger logger = LogManager.getLogger(LegalPropertyDetailDAOImpl.class);

	public LegalPropertyDetailDAOImpl() {
		super();
	}

	@Override
	public LegalPropertyDetail getLegalPropertyDetail(long legalId, long legalPropertyId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalId, legalPropertyId, scheduleType, propertySchedule, propertyType, northBy, ");
		sql.append(" southBy, eastBy, westBy, measurement, registrationOffice, registrationDistrict, propertyOwner, ");
		sql.append(" urbanLandCeiling, minorshareInvolved, propertyIsGramanatham, propertyReleased, ");
		sql.append(" propOriginalsAvailable, propertyIsAgricultural, nocObtainedFromLPA, anyMortgagePending, ");
		sql.append(" northSideEastByWest, southSideWestByEast, eastSideNorthBySouth, westSideSouthByNorth, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LegalPropertyDetails");
		sql.append(type);
		sql.append(" Where legalPropertyId = :legalPropertyId AND legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalPropertyDetail legalPropertyDetail = new LegalPropertyDetail();
		legalPropertyDetail.setLegalPropertyId(legalPropertyId);
		legalPropertyDetail.setLegalId(legalId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalPropertyDetail);
		RowMapper<LegalPropertyDetail> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LegalPropertyDetail.class);
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LegalId, LegalPropertyId, ScheduleType, PropertySchedule, PropertyType, NorthBy");
		sql.append(", SouthBy, EastBy, WestBy, Measurement, RegistrationOffice, RegistrationDistrict");
		sql.append(", PropertyOwner, UrbanLandCeiling, MinorshareInvolved, PropertyIsGramanatham, PropertyReleased");
		sql.append(", PropOriginalsAvailable, PropertyIsAgricultural, NocObtainedFromLPA, AnyMortgagePending");
		sql.append(", NorthSideEastByWest, SouthSideWestByEast, EastSideNorthBySouth, WestSideSouthByNorth");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" from LegalPropertyDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where legalId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, legalId);
				}
			}, new RowMapper<LegalPropertyDetail>() {
				@Override
				public LegalPropertyDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					LegalPropertyDetail lpd = new LegalPropertyDetail();

					lpd.setLegalId(rs.getLong("LegalId"));
					lpd.setLegalPropertyId(rs.getLong("LegalPropertyId"));
					lpd.setScheduleType(rs.getString("ScheduleType"));
					lpd.setPropertySchedule(rs.getString("PropertySchedule"));
					lpd.setPropertyType(rs.getString("PropertyType"));
					lpd.setNorthBy(rs.getString("NorthBy"));
					lpd.setSouthBy(rs.getString("SouthBy"));
					lpd.setEastBy(rs.getString("EastBy"));
					lpd.setWestBy(rs.getString("WestBy"));
					lpd.setMeasurement(rs.getBigDecimal("Measurement"));
					lpd.setRegistrationOffice(rs.getString("RegistrationOffice"));
					lpd.setRegistrationDistrict(rs.getString("RegistrationDistrict"));
					lpd.setPropertyOwner(rs.getString("PropertyOwner"));
					lpd.setUrbanLandCeiling(rs.getString("UrbanLandCeiling"));
					lpd.setMinorshareInvolved(rs.getString("MinorshareInvolved"));
					lpd.setPropertyIsGramanatham(rs.getString("PropertyIsGramanatham"));
					lpd.setPropertyReleased(rs.getString("PropertyReleased"));
					lpd.setPropOriginalsAvailable(rs.getString("PropOriginalsAvailable"));
					lpd.setPropertyIsAgricultural(rs.getString("PropertyIsAgricultural"));
					lpd.setNocObtainedFromLPA(rs.getString("NocObtainedFromLPA"));
					lpd.setAnyMortgagePending(rs.getString("AnyMortgagePending"));
					lpd.setNorthSideEastByWest(rs.getString("NorthSideEastByWest"));
					lpd.setSouthSideWestByEast(rs.getString("SouthSideWestByEast"));
					lpd.setEastSideNorthBySouth(rs.getString("EastSideNorthBySouth"));
					lpd.setWestSideSouthByNorth(rs.getString("WestSideSouthByNorth"));
					lpd.setVersion(rs.getInt("Version"));
					lpd.setLastMntOn(rs.getTimestamp("LastMntOn"));
					lpd.setLastMntBy(rs.getLong("LastMntBy"));
					lpd.setRecordStatus(rs.getString("RecordStatus"));
					lpd.setRoleCode(rs.getString("RoleCode"));
					lpd.setNextRoleCode(rs.getString("NextRoleCode"));
					lpd.setTaskId(rs.getString("TaskId"));
					lpd.setNextTaskId(rs.getString("NextTaskId"));
					lpd.setRecordType(rs.getString("RecordType"));
					lpd.setWorkflowId(rs.getLong("WorkflowId"));

					return lpd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
		sql.append(" urbanLandCeiling, minorshareInvolved, propertyIsGramanatham, propertyReleased, ");
		sql.append(" propOriginalsAvailable, propertyIsAgricultural, nocObtainedFromLPA, anyMortgagePending, ");
		sql.append(" northSideEastByWest, southSideWestByEast, eastSideNorthBySouth, westSideSouthByNorth, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalPropertyId, :legalId, :scheduleType, :propertySchedule, :propertyType, :northBy, ");
		sql.append(" :southBy, :eastBy, :westBy, :measurement, :registrationOffice, :registrationDistrict, ");
		sql.append(" :propertyOwner, ");
		sql.append(" :urbanLandCeiling, :minorshareInvolved, :propertyIsGramanatham, :propertyReleased, ");
		sql.append(" :propOriginalsAvailable, :propertyIsAgricultural, :nocObtainedFromLPA, :anyMortgagePending, ");
		sql.append(" :northSideEastByWest, :southSideWestByEast, :eastSideNorthBySouth, :westSideSouthByNorth, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalPropertyDetail.getLegalPropertyId() == Long.MIN_VALUE) {
			legalPropertyDetail.setLegalPropertyId(getNextValue("SeqLegalPropertyDetails"));
			logger.debug("get NextValue:" + legalPropertyDetail.getLegalPropertyId());
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
		sql.append(
				" registrationOffice = :registrationOffice, registrationDistrict = :registrationDistrict, propertyOwner = :propertyOwner, ");

		sql.append(" urbanLandCeiling = :urbanLandCeiling, minorshareInvolved = :minorshareInvolved, ");
		sql.append(" propertyIsGramanatham = :propertyIsGramanatham, propertyReleased = :propertyReleased, ");
		sql.append(
				" propOriginalsAvailable = :propOriginalsAvailable, propertyIsAgricultural = :propertyIsAgricultural, ");
		sql.append(" nocObtainedFromLPA = :nocObtainedFromLPA, anyMortgagePending = :anyMortgagePending, ");
		sql.append(" northSideEastByWest = :northSideEastByWest, southSideWestByEast = :southSideWestByEast, ");
		sql.append(" eastSideNorthBySouth = :eastSideNorthBySouth, westSideSouthByNorth = :westSideSouthByNorth, ");
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
