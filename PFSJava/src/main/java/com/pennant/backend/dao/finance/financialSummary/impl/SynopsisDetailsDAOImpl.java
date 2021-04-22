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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.financialSummary.SynopsisDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.SynopsisDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>PSLDetail</code> with set of CRUD operations.
 */
public class SynopsisDetailsDAOImpl extends SequenceDao<SynopsisDetails> implements SynopsisDetailsDAO {
	private static Logger logger = LogManager.getLogger(SynopsisDetailsDAOImpl.class);

	public SynopsisDetailsDAOImpl() {
		super();
	}

	@Override
	public SynopsisDetails getSynopsisDetails(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.Id, T1.FinReference, T1.CustomerBackground, T1.DetailedBusinessProfile");
		sql.append(", T1.DetailsofGroupCompaniesIfAny, T1.PdDetails, T1.MajorProduct, T1.OtherRemarks");
		sql.append(", T1.CmtOnCollateralDtls, T1.EndUse, T1.Version, T1.LastMntBy, T1.LastMntOn");
		sql.append(", T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId");
		sql.append(", T1.NextTaskId, T1.RecordType, T1.WorkflowId");
		sql.append(" From SynopsisDetails_Temp T1");
		sql.append(" Left Join FinanceMain T2 on T2.FinReference = T1.FinReference");
		sql.append(" Where T1.FinReference = ?");
		sql.append(" Union All");
		sql.append(" Select T1.Id, T1.FinReference, T1.CustomerBackground, T1.DetailedBusinessProfile");
		sql.append(", T1.DetailsofGroupCompaniesIfAny, T1.PdDetails, T1.MajorProduct, T1.OtherRemarks");
		sql.append(", T1.CmtOnCollateralDtls, T1.EndUse, T1.Version, T1.LastMntBy, T1.LastMntOn");
		sql.append(", T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId");
		sql.append(", T1.NextTaskId, T1.RecordType, T1.WorkflowId");
		sql.append(" FROM  SynopsisDetails T1");
		sql.append(" Left Join FinanceMain T2 ON T2.finreference =  T1.finreference");
		sql.append(" Where not exists (Select 1 From SynopsisDetails_TEMP Where Id = T1.Id)");
		sql.append(" and T1.FinReference = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, finReference },
					(rs, i) -> {
				SynopsisDetails sd = new SynopsisDetails();

				sd.setId(JdbcUtil.getLong(rs.getLong("Id")));
				sd.setFinReference(rs.getString("FinReference"));
				sd.setCustomerBackGround(rs.getBytes("CustomerBackground"));
				sd.setDetailedBusinessProfile(rs.getBytes("DetailedBusinessProfile"));
				sd.setDetailsofGroupCompaniesIfAny(rs.getBytes("DetailsofGroupCompaniesIfAny"));
				sd.setPdDetails(rs.getBytes("PdDetails"));
				sd.setMajorProduct(rs.getBytes("MajorProduct"));
				sd.setOtherRemarks(rs.getBytes("OtherRemarks"));
				sd.setCmtOnCollateralDtls(rs.getBytes("CmtOnCollateralDtls"));
				sd.setEndUse(rs.getBytes("EndUse"));
				sd.setVersion(rs.getInt("Version"));
				sd.setLastMntBy(rs.getLong("LastMntBy"));
				sd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				sd.setRecordStatus(rs.getString("RecordStatus"));
				sd.setRoleCode(rs.getString("RoleCode"));
				sd.setNextRoleCode(rs.getString("NextRoleCode"));
				sd.setTaskId(rs.getString("TaskId"));
				sd.setNextTaskId(rs.getString("NextTaskId"));
				sd.setRecordType(rs.getString("RecordType"));
				sd.setWorkflowId(rs.getLong("WorkflowId"));

				return sd;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record is not found in SynopsisDetails and SynopsisDetails_Temp for the specified FinReference >> {}",
					finReference);
		}

		return null;
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
