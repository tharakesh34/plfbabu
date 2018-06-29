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

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.legal.LegalApplicantDetailDAO;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalApplicantDetail</code> with set of CRUD operations.
 */
public class LegalApplicantDetailDAOImpl extends BasisNextidDaoImpl<LegalApplicantDetail> implements LegalApplicantDetailDAO {
	private static Logger				logger	= Logger.getLogger(LegalApplicantDetailDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public LegalApplicantDetailDAOImpl() {
		super();
	}
	
	@Override
	public LegalApplicantDetail getLegalApplicantDetail(long legalApplicantId, long legalId, String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalApplicantId, legalId, title, propertyOwnersName, age, relationshipType, ");
		sql.append(" iDType, iDNo, remarks, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
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

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalApplicantDetail);
		RowMapper<LegalApplicantDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalApplicantDetail.class);

		try {
			legalApplicantDetail = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalApplicantDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return legalApplicantDetail;
	}		
	
	@Override
	public List<LegalApplicantDetail> getApplicantDetailsList(long legalId,  String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT legalApplicantId, legalId, title, propertyOwnersName, age, relationshipType, ");
		sql.append(" iDType, iDNo, remarks, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		if (type.contains("View")) {
			sql.append(" ,titleName,iDTypeName ");
		}
		sql.append(" From LEGALAPPLICANTDETAILS");
		sql.append(type);
		sql.append(" Where legalId =:legalId");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("legalId", legalId);

		RowMapper<LegalApplicantDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalApplicantDetail.class);
		try {
			return this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
		} finally {
			source = null;
			sql = null;
		}
		return null;
	}
	
	@Override
	public String save(LegalApplicantDetail legalApplicantDetail,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into LEGALAPPLICANTDETAILS");
		sql.append(tableType.getSuffix());
		sql.append("(legalApplicantId, legalId, title, propertyOwnersName, age, relationshipType, ");
		sql.append(" iDType, iDNo, remarks, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :legalApplicantId, :legalId, :title, :propertyOwnersName, :age, :relationshipType, ");
		sql.append(" :iDType, :iDNo, :remarks, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalApplicantDetail.getLegalApplicantId() == Long.MIN_VALUE) {
			legalApplicantDetail.setLegalApplicantId(getNextidviewDAO().getNextId("SeqLEGALAPPLICANTDETAILS"));
			logger.debug("get NextID:" + legalApplicantDetail.getLegalApplicantId());
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalApplicantDetail);
		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(legalApplicantDetail.getLegalApplicantId());
	}	

	@Override
	public void update(LegalApplicantDetail legalApplicantDetail,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update LEGALAPPLICANTDETAILS" );
		sql.append(tableType.getSuffix());
		sql.append(" set title = :title, propertyOwnersName = :propertyOwnersName, ");
		sql.append(" age = :age, relationshipType = :relationshipType, iDType = :iDType, ");
		sql.append(" iDNo = :iDNo, remarks = :remarks, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalApplicantId = :legalApplicantId AND legalId = :legalId");
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalApplicantDetail);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
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
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
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
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
	}
	
	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
}	
