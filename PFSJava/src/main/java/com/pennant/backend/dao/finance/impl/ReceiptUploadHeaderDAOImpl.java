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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class ReceiptUploadHeaderDAOImpl extends SequenceDao<ReceiptUploadHeader> implements ReceiptUploadHeaderDAO {

	private static Logger logger = LogManager.getLogger(ReceiptUploadHeaderDAOImpl.class);

	public ReceiptUploadHeaderDAOImpl() {
		super();
	}

	@Override
	public UploadHeader getUploadHeader(long uploadId) {
		logger.debug("Entering");

		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setUploadId(uploadId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount, FailedCount, Module,EntityCode,UPLOADPROGRESS,");
		selectSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
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
		selectSql.append(" SELECT UploadHeaderId From receiptUploadheader_view");
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

	@Override
	public long save(ReceiptUploadHeader receiptUploadHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into ReceiptUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" (UploadHeaderId, FileName, Transactiondate, TotalRecords,SuccessCount,FailedCount, Version,");
		sql.append(" LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId,EntityCode,UPLOADPROGRESS)");
		sql.append(
				" values (:UploadHeaderId, :FileName, :transactionDate, :TotalRecords, :SuccessCount, :FailedCount ,:Version,");
		sql.append(" :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId,:entityCode,:uploadProgress)");

		// Get the identity sequence number.
		if (receiptUploadHeader.getUploadHeaderId() <= 0) {
			receiptUploadHeader.setUploadHeaderId(getNextValue("SeqReceiptUploadHeader"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(receiptUploadHeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return receiptUploadHeader.getUploadHeaderId();
	}

	@Override
	public void update(ReceiptUploadHeader receiptUploadHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update ReceiptUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" set FileName = :FileName, SuccessCount = :SuccessCount, FailedCount = :FailedCount,");
		sql.append(" TotalRecords = :TotalRecords, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(
				" RecordType = :RecordType, WorkflowId = :WorkflowId,entityCode=:entityCode,uploadProgress=:uploadProgress");
		sql.append(" where UploadHeaderId = :UploadHeaderId");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(receiptUploadHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ReceiptUploadHeader receiptUploadHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ReceiptUploadHeader");
		sql.append(tableType.getSuffix());
		sql.append(" where UploadHeaderId = :UploadHeaderId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(receiptUploadHeader);

		try {
			this.jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			logger.error(e);
			;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public ReceiptUploadHeader getReceiptHeaderById(long receiptId, String type) {
		logger.debug(Literal.ENTERING);

		ReceiptUploadHeader receiptUploadHeader = new ReceiptUploadHeader();
		receiptUploadHeader.setUploadHeaderId(receiptId);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				" Select UploadHeaderId, FileName, Transactiondate, TotalRecords,SuccessCount,FailedCount,entityCode,UPLOADPROGRESS,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  RECEIPTUPLOADHEADER");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where UploadHeaderId =:UploadHeaderId");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptUploadHeader);
		RowMapper<ReceiptUploadHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReceiptUploadHeader.class);

		try {
			receiptUploadHeader = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			receiptUploadHeader = null;
		}

		logger.debug(Literal.LEAVING);
		return receiptUploadHeader;
	}

	/**
	 * update success count and failed count for perticular receiptid
	 * 
	 * @param uploadHeaderId
	 * @param sucessCount
	 * @param failedCount
	 */
	@Override
	public void uploadHeaderStatusCnt(long uploadHeaderId, int sucessCount, int failedCount) {

		logger.debug("Entering");
		MapSqlParameterSource spMapSqlParameterSource = new MapSqlParameterSource();
		spMapSqlParameterSource.addValue("SuccessCount", sucessCount);
		spMapSqlParameterSource.addValue("FailedCount", failedCount);
		spMapSqlParameterSource.addValue("TotalRecords", failedCount + sucessCount);
		spMapSqlParameterSource.addValue("UploadHeaderId", uploadHeaderId);

		StringBuilder updateSql = new StringBuilder("Update RECEIPTUPLOADHEADER");
		updateSql
				.append(" Set SuccessCount = :SuccessCount,  FailedCount = :FailedCount, TotalRecords = :TotalRecords");
		updateSql.append(" Where UploadHeaderId =:UploadHeaderId");

		logger.debug("updateSql: " + updateSql.toString());

		this.jdbcTemplate.update(updateSql.toString(), spMapSqlParameterSource);

		logger.debug("Leaving");
	}

	/**
	 * update download status in table
	 * 
	 * @param uploadHeaderId
	 * @param receiptDownloaded
	 */
	@Override
	public void updateUploadProgress(long uploadHeaderId, int receiptDownloaded) {

		logger.debug("Entering");
		MapSqlParameterSource spMapSqlParameterSource = new MapSqlParameterSource();
		spMapSqlParameterSource.addValue("UploadProgress", receiptDownloaded);
		spMapSqlParameterSource.addValue("UploadHeaderId", uploadHeaderId);

		StringBuilder updateSql = new StringBuilder("Update RECEIPTUPLOADHEADER_temp");
		updateSql.append(" Set UploadProgress = :UploadProgress ");
		updateSql.append(" Where UploadHeaderId =:UploadHeaderId");

		logger.debug("updateSql: " + updateSql.toString());

		try {
			this.jdbcTemplate.update(updateSql.toString(), spMapSqlParameterSource);
		} catch (DataAccessException e) {
			logger.error("Exception:" + e);
		}

		logger.debug("Leaving");
	}

	/**
	 * check whether particular record download with status 1 in uploadprocess
	 * 
	 * @param uploadHeaderId
	 * @param receiptDownloaded
	 */
	@Override
	public boolean isFileDownlaoded(long uploadHeaderId, int receiptDownloaded) {
		logger.debug("Entering");

		int receiptCount = 0;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT count(*) From receiptUploadheader_view");
		selectSql.append(" WHERE  UploadHeaderId = :UploadHeaderId and UPLOADPROGRESS= :UPLOADPROGRESS ");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("UploadHeaderId", uploadHeaderId);
		source.addValue("UPLOADPROGRESS", receiptDownloaded);

		try {
			receiptCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			receiptCount = 0;
		}

		logger.debug("Leaving");

		if (receiptCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * update success count and failed count for perticular receiptid
	 * 
	 * @param uploadHeaderId
	 * @param sucessCount
	 * @param failedCount
	 */
	@Override
	public List<Long> getHeaderStatusCnt(long uploadHeaderId) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("uploadHeaderId", uploadHeaderId);

		String selectSql = "Select receiptid FROM  RECEIPTUPLOADDETAILS Where UploadHeaderId =:uploadHeaderId";
		logger.debug("selectSql: " + selectSql.toString());

		List<Long> countList = this.jdbcTemplate.queryForList(selectSql.toString(), source, Long.class);

		logger.debug("Leaving");
		return countList;

	}

	@Override
	public long generateSeqId() {
		return getNextValue("SeqReceiptUploadHeader");
	}

}