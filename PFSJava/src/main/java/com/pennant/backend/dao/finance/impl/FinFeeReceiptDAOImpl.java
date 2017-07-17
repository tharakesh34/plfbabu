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
 * FileName    		:  FinFeeReceiptDAOImpl.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  1-06-2017    														*
 *                                                                  						*
 * Modified Date    :  1-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 1-06-2017       Pennant	                 0.1                                            * 
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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinFeeReceipt model</b> class.<br>
 * 
 */

public class FinFeeReceiptDAOImpl extends BasisNextidDaoImpl<FinFeeReceipt> implements FinFeeReceiptDAO {

	private static Logger logger = Logger.getLogger(FinFeeReceiptDAOImpl.class);

	public FinFeeReceiptDAOImpl() {
		super();
	}

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * Fetch the Record Goods Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinFeeReceipt
	 */
	@Override
	public FinFeeReceipt getFinFeeReceiptById(FinFeeReceipt finFeeReceipt, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql
				.append(" SELECT ID, FeeID, ReceiptID, PaidAmount,");
		selectSql
				.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",ReceiptAmount, FeeTypeCode, FeeType, FEETYPEID, ReceiptType, ReceiptReference, transactionRef, favourNumber");
		}

		selectSql.append(" From FinFeeReceipts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE  Id = :Id");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeReceipt);
		RowMapper<FinFeeReceipt> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeReceipt.class);

		try {
			finFeeReceipt = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finFeeReceipt = null;
		}
		
		logger.debug("Leaving");
		
		return finFeeReceipt;
	}

	/**
	 * Method for Checking Count of Receipt by using Receipt ID
	 */
	@Override
	public boolean isFinFeeReceiptAllocated(long receiptID, String type) {
		logger.debug("Entering");
		
		FinFeeReceipt receipt = new FinFeeReceipt();
		receipt.setReceiptID(receiptID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Count(*) From FinFeeReceipts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE  ReceiptID = :ReceiptID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receipt);
		int count = 0;
		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			count = 0;
		}
		
		logger.debug("Leaving");
		return count > 0 ? true : false;
	}
	
	@Override
	public List<FinFeeReceipt> getFinFeeReceiptByFinRef(final List<Long> feeIds, String type) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FeeID", feeIds);
		
		StringBuilder selectSql = new StringBuilder(" SELECT ID, FeeID, ReceiptID, PaidAmount,");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",ReceiptAmount, FeeTypeCode, FeeType, FEETYPEID, ReceiptType, transactionRef, favourNumber");
		}
		selectSql.append(" From FinFeeReceipts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append("  Where FeeID IN (:FeeID)" );
		
		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		RowMapper<FinFeeReceipt> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeReceipt.class);
		
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	/**
	 * This method Deletes the Record from the FinFeeReceipt or FinFeeReceipt_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Goods Details by key LoanRefNumber
	 * 
	 * @param Goods
	 *            Details (FinFeeReceipt)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinFeeReceipt finFeeDetail, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinFeeReceipts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Id = :Id and ReceiptID = :ReceiptID and FeeID = :FeeID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinFeeReceipt or FinFeeReceipt_Temp.
	 * 
	 * save Goods Details
	 * 
	 * @param Goods
	 *            Details (FinFeeReceipt)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinFeeReceipt finFeeReceipt, String type) {
		logger.debug("Entering");

		if (finFeeReceipt.getId() == Long.MIN_VALUE) {
			finFeeReceipt.setId(getNextidviewDAO().getNextId("SeqFinFeeReceipts"));
			logger.debug("get NextID:" + finFeeReceipt.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into FinFeeReceipts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Id, FeeID, ReceiptID, PaidAmount, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values( :Id, :FeeID, :ReceiptID, :PaidAmount,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeReceipt);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
		return finFeeReceipt.getId();
	}

	/**
	 * This method updates the Record FinFeeReceipt or FinFeeReceipt_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods
	 *            Details (FinFeeReceipt)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinFeeReceipt finFeeDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update FinFeeReceipts");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set FeeID = :FeeID, ReceiptID = :ReceiptID, PaidAmount = :PaidAmount, ");
		updateSql.append("  Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append("  RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append("  RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where Id = :Id ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug("Leaving");
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}