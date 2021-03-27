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
 * FileName    		:  LegalApplicantDetailDAOImpl.java                                                   * 	  
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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.legal.LegalApplicantDetailDAO;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalApplicantDetail</code> with set of CRUD operations.
 */
public class LegalApplicantDetailDAOImpl extends SequenceDao<LegalApplicantDetail> implements LegalApplicantDetailDAO {
	private static Logger logger = LogManager.getLogger(LegalApplicantDetailDAOImpl.class);

	public LegalApplicantDetailDAOImpl() {
		super();
	}

	@Override
	public LegalApplicantDetail getLegalApplicantDetail(long legalId, long legalApplicantId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalApplicantId, legalId, title, propertyOwnersName, age, relationshipType, ");
		sql.append(" iDType, iDNo, remarks, customerId, ");
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (type.contains("View")) {
			sql.append(" ,titleName,iDTypeName ");
		}
		sql.append(" From LEGALAPPLICANTDETAILS");
		sql.append(type);
		sql.append(" Where legalApplicantId = :legalApplicantId And legalId =:legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalApplicantDetail legalApplicantDetail = new LegalApplicantDetail();
		legalApplicantDetail.setLegalApplicantId(legalApplicantId);
		legalApplicantDetail.setLegalId(legalId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalApplicantDetail);
		RowMapper<LegalApplicantDetail> rowMapper = BeanPropertyRowMapper.newInstance(LegalApplicantDetail.class);

		try {
			legalApplicantDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalApplicantDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return legalApplicantDetail;
	}

	@Override
	public List<LegalApplicantDetail> getApplicantDetailsList(long legalId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LegalApplicantId, LegalId, Title, PropertyOwnersName, Age, RelationshipType, IDType");
		sql.append(", IDNo, Remarks, CustomerId, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", TitleName, IDTypeName");
		}

		sql.append(" from Legalapplicantdetails");
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
			}, new RowMapper<LegalApplicantDetail>() {
				@Override
				public LegalApplicantDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					LegalApplicantDetail ad = new LegalApplicantDetail();

					ad.setLegalApplicantId(rs.getLong("LegalApplicantId"));
					ad.setLegalId(rs.getLong("LegalId"));
					ad.setTitle(rs.getString("Title"));
					ad.setPropertyOwnersName(rs.getString("PropertyOwnersName"));
					ad.setAge(rs.getInt("Age"));
					ad.setRelationshipType(rs.getString("RelationshipType"));
					ad.setIDType(rs.getString("IDType"));
					ad.setIDNo(rs.getString("IDNo"));
					ad.setRemarks(rs.getString("Remarks"));
					ad.setCustomerId(rs.getLong("CustomerId"));
					ad.setVersion(rs.getInt("Version"));
					ad.setLastMntOn(rs.getTimestamp("LastMntOn"));
					ad.setLastMntBy(rs.getLong("LastMntBy"));
					ad.setRecordStatus(rs.getString("RecordStatus"));
					ad.setRoleCode(rs.getString("RoleCode"));
					ad.setNextRoleCode(rs.getString("NextRoleCode"));
					ad.setTaskId(rs.getString("TaskId"));
					ad.setNextTaskId(rs.getString("NextTaskId"));
					ad.setRecordType(rs.getString("RecordType"));
					ad.setWorkflowId(rs.getLong("WorkflowId"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						ad.setTitleName(rs.getString("TitleName"));
						ad.setIDTypeName(rs.getString("IDTypeName"));
					}

					return ad;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public String save(LegalApplicantDetail legalApplicantDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into LEGALAPPLICANTDETAILS");
		sql.append(tableType.getSuffix());
		sql.append("(legalApplicantId, legalId, title, propertyOwnersName, age, relationshipType, ");
		sql.append(" iDType, iDNo, remarks, customerId,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :legalApplicantId, :legalId, :title, :propertyOwnersName, :age, :relationshipType, ");
		sql.append(" :iDType, :iDNo, :remarks, :customerId,");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalApplicantDetail.getLegalApplicantId() == Long.MIN_VALUE) {
			legalApplicantDetail.setLegalApplicantId(getNextValue("SeqLEGALAPPLICANTDETAILS"));
			logger.debug("get NextValue:" + legalApplicantDetail.getLegalApplicantId());
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalApplicantDetail);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(legalApplicantDetail.getLegalApplicantId());
	}

	@Override
	public void update(LegalApplicantDetail legalApplicantDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update LEGALAPPLICANTDETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" set title = :title, propertyOwnersName = :propertyOwnersName, ");
		sql.append(" age = :age, relationshipType = :relationshipType, iDType = :iDType, ");
		sql.append(" iDNo = :iDNo, remarks = :remarks, customerId = :customerId,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalApplicantId = :legalApplicantId AND legalId = :legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalApplicantDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalApplicantDetail legalApplicantDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from LEGALAPPLICANTDETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" where legalApplicantId = :legalApplicantId AND legalId =:legalId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalApplicantDetail);
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
	public void deleteList(LegalApplicantDetail applicantDetail, String tableType) {

		StringBuilder deleteSql = new StringBuilder("Delete From LEGALAPPLICANTDETAILS");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where legalId = :legalId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(applicantDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
	}

}
