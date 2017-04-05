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
 * FileName    		:  LegalExpensesDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2016    														*
 *                                                                  						*
 * Modified Date    :  19-04-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.expenses.impl;


import java.math.BigDecimal;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.expenses.LegalExpensesDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>LegalExpenses model</b> class.<br>
 * 
 */

public class LegalExpensesDAOImpl  extends BasisNextidDaoImpl<LegalExpenses> implements LegalExpensesDAO {

	private static Logger logger = Logger.getLogger(LegalExpensesDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	

	/**
	 * Fetch the Record  Legal Expenses details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LegalExpenses
	 */
	@Override
	public LegalExpenses getLegalExpensesById( String reference, String type) {
		logger.debug("Entering");
		LegalExpenses legalExpenses = new LegalExpenses();
		
		legalExpenses.setExpReference(reference);
		
		StringBuilder selectSql = new StringBuilder("Select expReference,CustomerId, BookingDate, Amount, FinReference, TransactionType, Remarks, RecoveredAmount, Amountdue, IsRecoverdFromMOPA, TotalCharges");
		selectSql.append(", Version ,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinLegalExpenses");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ExpReference =:ExpReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(legalExpenses);
		RowMapper<LegalExpenses> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LegalExpenses.class);
		
		try{
			legalExpenses = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			legalExpenses = null;
		}
		logger.debug("Leaving");
		return legalExpenses;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinLegalExpenses or FinLegalExpenses_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Legal Expenses by key CustomerID
	 * 
	 * @param Legal Expenses (legalExpenses)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(LegalExpenses legalExpenses,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinLegalExpenses");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where expReference =:expReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(legalExpenses);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",legalExpenses.getFinReference() ,legalExpenses.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",legalExpenses.getFinReference() ,legalExpenses.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinLegalExpenses or FinLegalExpenses_Temp.
	 *
	 * save Legal Expenses 
	 * 
	 * @param Legal Expenses (legalExpenses)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(LegalExpenses legalExpenses, String type) {
		logger.debug("Entering");

		if (legalExpenses.getExpReference() == null) {

			if (legalExpenses.getId() == 0
					|| legalExpenses.getId() == Long.MIN_VALUE) {
				legalExpenses.setId(getNextidviewDAO().getNextId("SeqFinLegalExpenses"));
			}
			if (legalExpenses.getTransactionType().equals(
					PennantConstants.LEGEL_FEES)) {
				legalExpenses.setExpReference(legalExpenses.getFinReference()
						+ "L" + legalExpenses.getId());
			} else if (legalExpenses.getTransactionType().equals(
					PennantConstants.FINES)) {
				legalExpenses.setExpReference(legalExpenses.getFinReference()
						+ "F" + legalExpenses.getId());
			} else if (legalExpenses.getTransactionType().equals(
					PennantConstants.OTHERS)) {
				legalExpenses.setExpReference(legalExpenses.getFinReference()
						+ "O" + legalExpenses.getId());
			}

		}
		/*
		 * if(legalExpenses.getExpReference() ==0 ||
		 * legalExpenses.getExpReference() == Long.MIN_VALUE){
		 * legalExpenses.setExpReference
		 * (getNextidviewDAO().getNextId("Seq"+PennantJavaUtil
		 * .getTabelMap("LegalExpenses"))); }
		 */

		StringBuilder insertSql = new StringBuilder(
				"Insert Into FinLegalExpenses");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
				.append(" (ExpReference,CustomerId, BookingDate, Amount, FinReference, TransactionType, Remarks, RecoveredAmount, Amountdue, IsRecoverdFromMOPA, TotalCharges");
		insertSql
				.append(", Version ,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
				.append(" Values(:ExpReference,:CustomerId, :BookingDate, :Amount, :FinReference, :TransactionType, :Remarks, :RecoveredAmount, :Amountdue, :IsRecoverdFromMOPA, :TotalCharges");
		insertSql
				.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				legalExpenses);
		this.namedParameterJdbcTemplate.update(insertSql.toString(),
				beanParameters);
		logger.debug("Leaving");
		return legalExpenses.getId();
	}
	
	/**
	 * This method updates the Record FinLegalExpenses or FinLegalExpenses_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Legal Expenses by key CustomerID and Version
	 * 
	 * @param Legal Expenses (legalExpenses)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(LegalExpenses legalExpenses,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinLegalExpenses");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set CustomerId = :CustomerId, BookingDate = :BookingDate, Amount = :Amount, FinReference = :FinReference, TransactionType = :TransactionType, Remarks = :Remarks, RecoveredAmount = :RecoveredAmount, Amountdue = :Amountdue, IsRecoverdFromMOPA = :IsRecoverdFromMOPA, TotalCharges = :TotalCharges");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ExpReference =:ExpReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(legalExpenses);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",legalExpenses.getFinReference() ,legalExpenses.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String customerID, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = customerID;
		parms[0][0] = PennantJavaUtil.getLabel("label_ExpReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


	@Override
	public BigDecimal getTotalCharges(String finReference) {

		logger.debug("Entering");
 
		LegalExpenses legalExpenses= new LegalExpenses();
		legalExpenses.setFinReference(finReference);

		// Get Profit calculated - Paid Profits
		StringBuilder selectSql = new StringBuilder(" SELECT ");
		selectSql.append(" SUM(Amount) ");
		selectSql.append(" FROM FinLegalExpenses_Aview where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(legalExpenses);

		BigDecimal totalCharges = BigDecimal.ZERO;
		try {
			totalCharges = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			totalCharges = BigDecimal.ZERO;
		}

		
		logger.debug("Leaving");
		return totalCharges;
	
	}

	
}