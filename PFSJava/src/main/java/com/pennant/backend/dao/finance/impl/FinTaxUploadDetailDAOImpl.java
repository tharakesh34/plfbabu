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
 * FileName    		:  ManualAdviseDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinTaxUploadDetailDAO;
import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * Data access layer implementation for <code>ManualAdvise</code> with set of
 * CRUD operations.
 */
public class FinTaxUploadDetailDAOImpl extends SequenceDao<FinTaxUploadHeader> implements FinTaxUploadDetailDAO {
	private static Logger logger = Logger.getLogger(FinTaxUploadDetailDAOImpl.class);

	public FinTaxUploadDetailDAOImpl() {
		super();
	}

	@Override
	public List<FinTaxUploadDetail> getFinTaxDetailUploadById(String reference, String type, String status) {

		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select BatchReference, TaxCode, AggrementNo, ApplicableFor,Applicant,TaxExempted,AddrLine1,AddrLine2,AddrLine3,AddrLine4,"
						+ "Country,City,PinCode,Province,SeqNo,");
		sql.append(
				"  LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinTaxUploadDetail");
		sql.append(type);
		sql.append(" Where BatchReference = :BatchReference and RecordStatus<>" + status);

		source.addValue("BatchReference", reference);
		RowMapper<FinTaxUploadDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinTaxUploadDetail.class);

		logger.debug("selectSql: " + sql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);

	}

	@Override
	public void update(FinTaxUploadHeader finTaxUploadHeader, String type) {

		logger.debug(Literal.ENTERING);
		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update finTaxUploadHeader");
		sql.append(type);
		sql.append(" set BatchReference = :BatchReference,");
		sql.append(
				" FileName = :FileName, NumberofRecords = :NumberofRecords ,BatchCreatedDate =:BatchCreatedDate,BatchApprovedDate =:BatchApprovedDate,Status= :Status,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		sql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where BatchReference =:BatchReference ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTaxUploadHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void save(FinTaxUploadHeader finTaxUploadHeader, String type) {

		logger.debug("Entering");
		if (finTaxUploadHeader.getId() <= 0) {
			finTaxUploadHeader.setId(getNextValue("SeqFeePostings"));
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into finTaxUploadHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BatchReference, FileName, NumberofRecords, BatchCreatedDate,BatchApprovedDate,Status,");
		insertSql.append("  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append("	TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				"  Values(:BatchReference, :FileName, :NumberofRecords, :BatchCreatedDate, :BatchApprovedDate,:Status,");
		insertSql.append(
				"  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxUploadHeader);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void saveFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type) {

		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into finTaxUploadDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (BatchReference, TaxCode, AggrementNo, ApplicableFor,Applicant,TaxExempted,AddrLine1,AddrLine2,AddrLine3,AddrLine4,Country,");
		insertSql.append(
				" Province,City,PinCode,SeqNo,Version ,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append("	TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				"  Values(:BatchReference, :TaxCode, :AggrementNo, :ApplicableFor,:Applicant,:TaxExempted,:AddrLine1,:AddrLine2,:AddrLine3,:AddrLine4,:Country,");
		insertSql.append(
				":Province, :City, :PinCode, :SeqNo,  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxUploadDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void delete(FinTaxUploadHeader finTaxUploadHeader, String type) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from finTaxUploadHeader");
		sql.append(type);
		sql.append(" where BatchReference = :BatchReference ");
		/* sql.append(QueryUtil.getConcurrencyCondition(tableType)); */

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finTaxUploadHeader);
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
	public void updateFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update FinTaxUploadDetail");
		sql.append(type);
		sql.append(
				" set BatchReference=:BatchReference , TaxCode=:TaxCode , AggrementNo=:AggrementNo, ApplicableFor=:ApplicableFor , Applicant=:Applicant ,SeqNo=:SeqNo,");
		sql.append(
				" TaxExempted=:TaxExempted ,AddrLine1=:AddrLine1, AddrLine2=:AddrLine2, AddrLine3=:AddrLine3, AddrLine4=:AddrLine4, Country=:Country, Province=:Province,City=:City,PinCode=:PinCode,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		sql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where BatchReference =:BatchReference and SeqNo=:SeqNo");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxUploadDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void deleteFintaxDetail(FinTaxUploadDetail taxUploadDetail, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from finTaxUploadDetail");
		sql.append(type);
		sql.append(" where BatchReference = :BatchReference and SeqNo=:SeqNo ");
		/* sql.append(QueryUtil.getConcurrencyCondition(tableType)); */

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(taxUploadDetail);
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
	public FinTaxUploadHeader getFinTaxUploadHeaderByRef(long ref, String type) {

		logger.debug("Entering");
		FinTaxUploadHeader finTaxUploadHeader = new FinTaxUploadHeader();
		finTaxUploadHeader.setId(ref);
		StringBuilder selectSql = new StringBuilder();

		selectSql
				.append("SELECT BatchReference, FileName, NumberofRecords, BatchCreatedDate,BatchApprovedDate,Status,");
		selectSql.append(
				"LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  fintaxuploadheader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BatchReference =:BatchReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxUploadHeader);
		RowMapper<FinTaxUploadHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinTaxUploadHeader.class);

		try {
			finTaxUploadHeader = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finTaxUploadHeader = null;
		}
		logger.debug("Leaving");
		return finTaxUploadHeader;

	}

}
