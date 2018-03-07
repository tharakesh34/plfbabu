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


import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.UploadHeaderDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.expenses.UploadHeader;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class UploadHeaderDAOImpl extends BasisNextidDaoImpl<UploadHeader> implements UploadHeaderDAO {

	private static Logger logger = Logger.getLogger(UploadHeaderDAOImpl.class);
	
	public UploadHeaderDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public UploadHeader getUploadHeader(long uploadId) {
		logger.debug("Entering");
		
		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setUploadId(uploadId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount, FailedCount, Module," );
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From UploadHeader");
		selectSql.append(" WHERE  UploadId = :UploadId ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		RowMapper<UploadHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(UploadHeader.class);
		
		try {
			uploadHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
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
			uploadHeader.setUploadId(getNextidviewDAO().getNextId("SeqUploadHeader"));
			logger.debug("get NextID:" + uploadHeader.getUploadId());
		}
		
		sql.append(" Insert Into UploadHeader");
		sql.append(" (UploadId, FileLocation, FileName, TransactionDate, TotalRecords, SuccessCount, FailedCount, Module,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" Values (:UploadId, :FileLocation, :FileName, :TransactionDate, :TotalRecords, :SuccessCount, :FailedCount, :Module,");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
		return uploadHeader.getUploadId();
	}
	
	@Override
	public void updateRecordCounts(UploadHeader uploadHeader) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update UploadHeader");
		updateSql.append(" Set SuccessCount = (Select Count(UploadId) As SuccessCount from UploadFinExpenses where Status = 'SUCCESS' And UploadId = :UploadId)");
		updateSql.append(" , FailedCount = (Select Count(UploadId) As FailedCount from UploadFinExpenses where Status = 'FAILED' And UploadId = :UploadId)");
		updateSql.append(" , TotalRecords = (Select Count(UploadId) As SuccessCount from UploadFinExpenses where UploadId = :UploadId)");
		updateSql.append(" Where UploadId = :UploadId");

		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
	
	@Override
	public void updateRecord(UploadHeader uploadHeader) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update UploadHeader");
		updateSql.append(" Set SuccessCount = :SuccessCount,  FailedCount = :FailedCount, TotalRecords = :TotalRecords");
		updateSql.append(" Where UploadId =:UploadId");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadHeader);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
}