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
 * FileName    		:  UploadHeaderDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2017       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.UploadHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.receiptupload.UploadReceipt;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class UploadHeaderDAOImpl extends SequenceDao<UploadHeader> implements UploadHeaderDAO {
	private static Logger logger = LogManager.getLogger(UploadHeaderDAOImpl.class);

	public UploadHeaderDAOImpl() {
		super();
	}

	@Override
	public UploadHeader getUploadHeader(long uploadId) {
		logger.debug("Entering");

		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setUploadId(uploadId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount, FailedCount, Module, FileDownload,");
		selectSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ApprovedDate, MakerId, ApproverId");
		selectSql.append(" From UploadHeader");
		selectSql.append(" WHERE  UploadId = :UploadId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		RowMapper<UploadHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(UploadHeader.class);

		try {
			uploadHeader = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			uploadHeader = null;
		}

		logger.debug("Leaving");

		return uploadHeader;
	}

	@Override
	public boolean isFileNameExist(String fileName) {
		logger.debug("Entering");

		long count = 0;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT UploadId From UploadHeader");
		selectSql.append(" WHERE  FileName = :FileName");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FileName", fileName);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}

		logger.debug("Leaving");

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method insert new Records into UploadHeader or UploadHeader_Temp.
	 * 
	 * save Promotion
	 * 
	 * @param Promotion
	 *            (promotion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(UploadHeader uploadHeader) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();

		if (uploadHeader.getUploadId() == Long.MIN_VALUE) {
			uploadHeader.setUploadId(getNextValue("SeqUploadHeader"));
			logger.debug("get NextID:" + uploadHeader.getUploadId());
		}

		sql.append(" Insert Into UploadHeader");
		sql.append(
				" (UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount, FailedCount, Module, FileDownload,");
		sql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(
				" Values (:UploadId, :FileLocation, :FileName, :TransactionDate, :TotalRecords, :SuccessCount, :FailedCount, :Module, :FileDownload,");
		sql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");

		return uploadHeader.getUploadId();
	}

	@Override
	public void updateRecordCounts(UploadHeader uploadHeader) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update UploadHeader");
		updateSql.append(
				" Set SuccessCount = (Select Count(UploadId) As SuccessCount from UploadFinExpenses where Status = 'SUCCESS' And UploadId = :UploadId)");
		updateSql.append(
				" , FailedCount = (Select Count(UploadId) As FailedCount from UploadFinExpenses where Status = 'FAILED' And UploadId = :UploadId)");
		updateSql.append(
				" , TotalRecords = (Select Count(UploadId) As SuccessCount from UploadFinExpenses where UploadId = :UploadId)");
		updateSql.append(" Where UploadId = :UploadId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateRecord(UploadHeader uploadHeader) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update UploadHeader");
		updateSql
				.append(" Set SuccessCount = :SuccessCount,  FailedCount = :FailedCount, TotalRecords = :TotalRecords");
		updateSql.append(" Where UploadId =:UploadId");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public UploadHeader getUploadHeaderById(long uploadId, String type) {
		logger.debug(Literal.ENTERING);

		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setUploadId(uploadId);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount");
		sql.append(
				", FailedCount, Module, entityCode, FileDownload,Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId , ApprovedDate, MakerId, ApproverId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", UserName");
		}
		sql.append(" From UploadHeader");
		sql.append(type);
		sql.append(" WHERE  UploadId = :UploadId ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		RowMapper<UploadHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(UploadHeader.class);

		try {
			uploadHeader = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			uploadHeader = null;
		}

		logger.debug(Literal.LEAVING);

		return uploadHeader;
	}

	@Override
	public void updateFileDownload(long uploadId, boolean fileDownload, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FileDownload", fileDownload);
		source.addValue("UploadId", uploadId);

		StringBuffer sql = new StringBuffer();
		sql.append("Update UploadHeader");
		sql.append(StringUtils.trim(type));
		sql.append(" Set FileDownload = :FileDownload");
		sql.append(" Where UploadId = :UploadId");

		logger.trace(Literal.SQL + sql.toString());
		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(UploadHeader uploadHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		if (uploadHeader.getUploadId() == Long.MIN_VALUE) {
			uploadHeader.setUploadId(getNextValue("SeqUploadHeader"));
			logger.debug("get NextID:" + uploadHeader.getUploadId());
		}

		sql.append(" Insert Into UploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" (UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount, FailedCount");
		sql.append(
				", Module, entityCode, FileDownload, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, ApprovedDate, MakerId, ApproverId)");
		sql.append(" Values (:UploadId, :FileLocation, :FileName, :TransactionDate, :TotalRecords, :SuccessCount");
		sql.append(
				", :FailedCount, :Module, :entityCode, :FileDownload, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode");
		sql.append(
				", :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ApprovedDate, :MakerId, :ApproverId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return uploadHeader.getUploadId();
	}

	@Override
	public void update(UploadHeader uploadHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update UploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" set FileLocation = :FileLocation, FileName = :FileName, TransactionDate = :TransactionDate,");
		sql.append(" TotalRecords = :TotalRecords, SuccessCount = :SuccessCount, FailedCount = :FailedCount,");
		sql.append(" Module = :Module,  entityCode =:entityCode, FileDownload = :FileDownload,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(
				" RecordType = :RecordType, WorkflowId = :WorkflowId ,ApprovedDate = :ApprovedDate, MakerId = :MakerId, ApproverId= :ApproverId");
		sql.append(" where UploadId = :UploadId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(uploadHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateRecordCounts(UploadHeader uploadHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder updateSql = new StringBuilder("Update UploadHeader");
		updateSql.append(" Set SuccessCount = (Select Count(UploadId) As SuccessCount from UploadFinExpenses where");
		updateSql.append(
				" Status = 'SUCCESS' And UploadId = :UploadId), FailedCount = (Select Count(UploadId) As FailedCount");
		updateSql.append(" from UploadFinExpenses where Status = 'FAILED' And UploadId = :UploadId)");
		updateSql.append(
				", TotalRecords = (Select Count(UploadId) As SuccessCount from UploadFinExpenses where UploadId = :UploadId)");
		updateSql.append(" Where UploadId = :UploadId");

		logger.debug(Literal.SQL + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(UploadHeader uploadHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from UploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" where UploadId = :UploadId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(uploadHeader);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			logger.warn(e.toString());
		}

		// Check for the concurrency failure.
		/*
		 * if (recordCount == 0) { throw new ConcurrencyException(); }
		 */

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(long uploadId, String fileName, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "FileName = :FileName And UploadId != :UploadId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("UploadHeader", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("UploadHeader_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "UploadHeader_Temp", "UploadHeader" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("UploadId", uploadId);
		paramSource.addValue("FileName", fileName);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceMain
	 * 
	 * @return FinanceMain
	 */
	@Override
	public UploadHeader getUploadHeader() {
		logger.debug("Entering");

		String moduleName = "UploadHeader";

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails(moduleName);
		UploadHeader uploadHeader = new UploadHeader();
		if (workFlowDetails != null) {
			uploadHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return uploadHeader;
	}

	@Override
	public boolean isFileDownload(long uploadID, String tableType) {
		logger.debug("Entering");

		boolean isFileDownload = false;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Filedownload From UploadHeader");
		selectSql.append(tableType);
		selectSql.append(" WHERE  uploadID = :UploadID");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("UploadID", uploadID);

		try {
			isFileDownload = jdbcTemplate.queryForObject(selectSql.toString(), source, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			isFileDownload = false;
		}

		logger.debug("Leaving");
		return isFileDownload;
	}

	@Override
	public List<UploadReceipt> getSuccesFailedReceiptCount(long uploadId) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("UploadId", uploadId);
		StringBuilder selectSql = new StringBuilder("Select Count(UploadId) Count, Status ");
		selectSql.append(" from UploadReceipt  ");
		selectSql.append(" Where UploadId = :UploadId Group By STATUS");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<UploadReceipt> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(UploadReceipt.class);
		logger.debug("Leaving");

		return jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public void updateFRRHeaderRecord(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		StringBuilder updateSql = new StringBuilder("Update UploadHeader");
		updateSql.append(
				" Set SuccessCount = :SuccessCount,  FailedCount = FailedCount + :FailedCount, TotalRecords = TotalRecords + :TotalRecords");
		updateSql.append(" Where UploadId =:UploadId");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}
}