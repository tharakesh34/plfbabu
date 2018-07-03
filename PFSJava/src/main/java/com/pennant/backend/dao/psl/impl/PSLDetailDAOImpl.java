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
 * FileName    		:  PSLDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-06-2018    														*
 *                                                                  						*
 * Modified Date    :  20-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.psl.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.psl.PSLDetailDAO;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PSLDetail</code> with set of CRUD
 * operations.
 */
public class PSLDetailDAOImpl extends BasisNextidDaoImpl<PSLDetail> implements PSLDetailDAO {
	private static Logger logger = Logger.getLogger(PSLDetailDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PSLDetailDAOImpl() {
		super();
	}

	@Override
	public PSLDetail getPSLDetail(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" finReference, categoryCode, weakerSection, landHolding, landArea, sector, ");
		sql.append(" amount, subCategory, purpose, endUse, LoanPurpose, EligibleAmount,");
		if (type.contains("View")) {
			sql.append("WeakerSectionName,PurposeName, EnduseName, ");
		}

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PSLDetail");
		sql.append(type);
		sql.append(" Where finReference = :finReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PSLDetail pSLDetail = new PSLDetail();
		pSLDetail.setFinReference(finReference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pSLDetail);
		RowMapper<PSLDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PSLDetail.class);

		try {
			pSLDetail = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			pSLDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return pSLDetail;
	}

	@Override
	public String save(PSLDetail pSLDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append("(finReference, categoryCode, weakerSection, landHolding, landArea, sector, ");
		sql.append(" amount, subCategory, purpose, endUse,");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, LoanPurpose, EligibleAmount)");
		sql.append(" values(");
		sql.append(" :finReference, :categoryCode, :weakerSection, :landHolding, :landArea, :sector, ");
		sql.append(" :amount, :subCategory, :purpose, :endUse, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :LoanPurpose, :EligibleAmount)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pSLDetail);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(pSLDetail.getFinReference());
	}

	@Override
	public void update(PSLDetail pSLDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append("  set categoryCode = :categoryCode, weakerSection = :weakerSection, landHolding = :landHolding, ");
		sql.append(" landArea = :landArea, sector = :sector, amount = :amount, ");
		sql.append(" subCategory = :subCategory, purpose = :purpose, endUse = :endUse, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, LoanPurpose = :LoanPurpose, EligibleAmount = :EligibleAmount");
		sql.append(" where finReference = :finReference ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pSLDetail);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PSLDetail pSLDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(pSLDetail);
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
