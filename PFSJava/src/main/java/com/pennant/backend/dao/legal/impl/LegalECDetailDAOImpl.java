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
 * FileName    		:  LegalECDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-06-2018    														*
 *                                                                  						*
 * Modified Date    :  19-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-06-2018       PENNANT	                 0.1                                            * 
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
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.legal.LegalECDetailDAO;
import com.pennant.backend.model.legal.LegalECDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>LegalECDetail</code> with set of CRUD operations.
 */
public class LegalECDetailDAOImpl extends SequenceDao<LegalECDetail> implements LegalECDetailDAO {
	private static Logger	logger	= Logger.getLogger(LegalECDetailDAOImpl.class);



	public LegalECDetailDAOImpl() {
		super();
	}
	
	@Override
	public LegalECDetail getLegalECDetail(long legalECId,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalECId, legalId, ecDate, document, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From LegalECDetails");
		sql.append(type);
		sql.append(" Where legalECId = :legalECId");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		LegalECDetail legalECDetail = new LegalECDetail();
		legalECDetail.setLegalECId(legalECId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalECDetail);
		RowMapper<LegalECDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalECDetail.class);

		try {
			legalECDetail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			legalECDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return legalECDetail;
	}	
	
	@Override
	public List<LegalECDetail> getLegalECDetailList(long legalId,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" legalECId, legalId, ecDate, document, ");
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From LegalECDetails");
		sql.append(type);
		sql.append(" Where legalId = :legalId");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		LegalECDetail legalECDetail = new LegalECDetail();
		legalECDetail.setLegalId(legalId);
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalECDetail);
		RowMapper<LegalECDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalECDetail.class);
		
		try {
			return this.jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
		} finally {
			sql = null;
		}
		return null;
	}		
	
	@Override
	public String save(LegalECDetail legalECDetail,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into LegalECDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (legalECId, legalId, ecDate, document, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :legalECId, :legalId, :ecDate, :document, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (legalECDetail.getId()==Long.MIN_VALUE){
			legalECDetail.setId(getNextId("SeqLegalECDetails"));
			logger.debug("get NextID:"+legalECDetail.getId());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalECDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(legalECDetail.getLegalECId());
	}	

	@Override
	public void update(LegalECDetail legalECDetail,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update LegalECDetails" );
		sql.append(tableType.getSuffix());
		sql.append("  set legalId = :legalId, ecDate = :ecDate, document = :document, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where legalECId = :legalECId ");
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalECDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalECDetail legalECDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from LegalECDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where legalECId = :legalECId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalECDetail);
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
	public void deleteList(LegalECDetail legalECDetail, String tableType) {
	
		StringBuilder deleteSql = new StringBuilder("Delete From LegalECDetails");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where legalId = :legalId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(legalECDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
	}
	
	
}	
