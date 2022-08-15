/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : UploadFinTypeExpenseDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * *
 * Modified Date : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.UploadFinTypeExpenseDAO;
import com.pennant.backend.model.expenses.UploadFinTypeExpense;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

/**
 * DAO methods implementation for the <b>UploadFinTypeExpenseDAOImpl model</b> class.<br>
 * 
 */
public class UploadFinTypeExpenseDAOImpl extends BasicDao<UploadFinTypeExpense> implements UploadFinTypeExpenseDAO {
	private static Logger logger = LogManager.getLogger(UploadFinTypeExpenseDAOImpl.class);

	public UploadFinTypeExpenseDAOImpl() {
		super();
	}

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void saveUploadDetails(List<UploadFinTypeExpense> uploadDetailsList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO UploadFinTypeExpenses");
		insertSql.append(" (UploadId, FinType, ExpenseTypeCode, Percentage, AmountValue, Status, Reason) ");
		insertSql.append(" VALUES(:UploadId, :FinType, :ExpenseTypeCode, :Percentage, :AmountValue, :Status, :Reason)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(uploadDetailsList.toArray());

		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public List<UploadFinTypeExpense> getSuccesFailedCount(long uploadId) {

		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("UploadId", uploadId);
		StringBuilder selectSql = new StringBuilder("Select Count(UploadId) Count, Status ");
		selectSql.append(" from UploadFinTypeExpenses  ");
		selectSql.append(" Where UploadId = :UploadId Group By STATUS");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<UploadFinTypeExpense> typeRowMapper = BeanPropertyRowMapper.newInstance(UploadFinTypeExpense.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}

}