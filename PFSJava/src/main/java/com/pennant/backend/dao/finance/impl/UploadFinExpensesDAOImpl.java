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
 * FileName    		:  UploadFinExpensesDAOImpl.java                                        * 	  
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
 * 14-08-2013       Pennant	                 0.1                                            * 
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

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.UploadFinExpensesDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.expenses.UploadFinExpenses;

/**
 * DAO methods implementation for the <b>UploadFinExpenses model</b> class.<br>
 * 
 */
public class UploadFinExpensesDAOImpl extends BasisCodeDAO<UploadFinExpenses> implements UploadFinExpensesDAO {

	private static Logger logger = Logger.getLogger(UploadFinExpensesDAOImpl.class);
	
	public UploadFinExpensesDAOImpl() {
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

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void saveUploadFinExpenses(List<UploadFinExpenses> uploadFinExpensesList) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO UploadFinExpenses");
		insertSql.append(" (UploadId, FinType, FinReference, FinApprovalStartDate, FinApprovalEndDate, ExpenseTypeCode, Percentage, AmountValue, Type, Status, Reason) ");
		insertSql.append(" VALUES (:UploadId, :FinType, :FinReference, :FinApprovalStartDate, :FinApprovalEndDate, :ExpenseTypeCode, :Percentage, :AmountValue, :Type, :Status, :Reason)");

		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(uploadFinExpensesList.toArray());
		
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
	
}