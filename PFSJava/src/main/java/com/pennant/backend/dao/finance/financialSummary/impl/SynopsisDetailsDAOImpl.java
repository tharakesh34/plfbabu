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
package com.pennant.backend.dao.finance.financialSummary.impl;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.financialSummary.SynopsisDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.SynopsisDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>PSLDetail</code> with set of CRUD operations.
 */
public class SynopsisDetailsDAOImpl extends SequenceDao<SynopsisDetails> implements SynopsisDetailsDAO {
	private static Logger logger = Logger.getLogger(SynopsisDetailsDAOImpl.class);

	public SynopsisDetailsDAOImpl() {
		super();
	}

	@Override
	public SynopsisDetails getSynopsisDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT  T1.id, T1.finReference, T1.CustomerBackground,T1.DetailedBusinessProfile,T1.detailsofGroupCompaniesIfAny,T1.PdDetails,T1.MajorProduct,T1.OtherRemarks");
		selectSql.append(" ,T1.CmtOnCollateralDtls,T1.EndUse");
		selectSql.append(", T1.Version, T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode");
		selectSql.append(", T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId ");
		selectSql.append(" FROM  SynopsisDetails_TEMP T1");
		selectSql.append(" LEFT JOIN FinanceMain T2 ON T2.finreference =  T1.finreference");
		selectSql.append(" where T1.finReference = :finReference");
		selectSql.append(" UNION ALL");
		selectSql.append(
				" SELECT  T1.id, T1.finReference, T1.CustomerBackground,T1.DetailedBusinessProfile,T1.detailsofGroupCompaniesIfAny,T1.PdDetails,T1.MajorProduct,T1.OtherRemarks");
		selectSql.append(" ,T1.CmtOnCollateralDtls,T1.EndUse");
		selectSql.append(", T1.Version, T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode");
		selectSql.append(", T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId ");
		selectSql.append(" FROM  SynopsisDetails T1");
		selectSql.append(" LEFT JOIN FinanceMain T2 ON T2.finreference =  T1.finreference");
		selectSql.append(" WHERE NOT EXISTS (SELECT 1 FROM SynopsisDetails_TEMP WHERE id = T1.id)");
		selectSql.append(" AND T1.finReference = :finReference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql.toString());

		SynopsisDetails synopsisDetails = new SynopsisDetails();
		synopsisDetails.setFinReference(finReference);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(synopsisDetails);
		RowMapper<SynopsisDetails> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SynopsisDetails.class);

		try {
			synopsisDetails = jdbcTemplate.queryForObject(selectSql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			synopsisDetails = null;
		}

		logger.debug(Literal.LEAVING);
		return synopsisDetails;
	}

	@Override
	public String save(SynopsisDetails synopsisDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (synopsisDetails.getId() == Long.MIN_VALUE) {
			synopsisDetails.setId(getNextValue("SeqSynopsisDetails"));
			logger.debug("get NextID:" + synopsisDetails.getId());
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into SynopsisDetails");
		sql.append(tableType.getSuffix());
		sql.append(
				"(id, finReference, customerBackGround, detailedBusinessProfile, detailsofGroupCompaniesIfAny, pdDetails, majorProduct, otherRemarks");
		sql.append(" ,CmtOnCollateralDtls,EndUse");
		sql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(
				" :id, :finReference, :customerBackGround, :detailedBusinessProfile, :detailsofGroupCompaniesIfAny, :pdDetails, :majorProduct, :otherRemarks");
		sql.append(" ,:CmtOnCollateralDtls,:EndUse");
		sql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(synopsisDetails);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(synopsisDetails.getFinReference());
	}

	@Override
	public void update(SynopsisDetails synopsisDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update SynopsisDetails");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set customerBackGround = :customerBackGround, detailedBusinessProfile = :detailedBusinessProfile, detailsofGroupCompaniesIfAny = :detailsofGroupCompaniesIfAny");
		sql.append(", pdDetails = :pdDetails, majorProduct = :majorProduct, otherRemarks = :otherRemarks");
		sql.append(" ,CmtOnCollateralDtls=:CmtOnCollateralDtls,EndUse=:EndUse");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(",RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where finReference = :finReference ");
		//	sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(synopsisDetails);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SynopsisDetails synopsisDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from SynopsisDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where finReference = :finReference ");
		//sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(synopsisDetails);
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

}
