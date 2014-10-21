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
 * FileName    		:  SeqAccountNumberDAOImpl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  				   														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.util.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.util.FinanceAccountNumberDAO;
import com.pennant.backend.model.rulefactory.FinanceAccountNumber;

/**
 * DAO methods implementation for the <b>SeqAccountNumber model</b> class.<br>
 * 
 */
public class FinanceAccountNumberDAOImpl implements FinanceAccountNumberDAO {

	private static Logger logger = Logger.getLogger(FinanceAccountNumberDAOImpl.class);
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	
	/**
	 * Fetch the Record FinanceAccountNumber Details details by key field
	 * 
	 * @param financeAccountNumber (FinanceAccountNumber)
	 *
	 * @return FinanceAccountNumber
	 */
	@Override
	public FinanceAccountNumber getFinanceAccountNumber(FinanceAccountNumber financeAccountNumber) {
		
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder("Select FinBranch,FinType,FinReference,Account," );
		selectQry.append(" AccountType,AccountCcy,AccountNumber from FinanceAccountNumber" );
		selectQry.append(" Where FinBranch =:FinBranch AND FinType = :FinType");
		selectQry.append("  AND FinReference= :FinReference AND Account= :Account AND AccountType= :AccountType ");
		
		logger.debug("selectSql: " + selectQry.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeAccountNumber);
		RowMapper<FinanceAccountNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceAccountNumber.class);

		try {
			financeAccountNumber= this.namedParameterJdbcTemplate.queryForObject(selectQry.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeAccountNumber=null;
			logger.error(e);
		}
				
		logger.debug("Leaving");
		return financeAccountNumber;
	}


	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method insert new Records into FinanceAccountNumber .
	 * 
	 * save FinanceAccountNumber Details
	 * 
	 * @param financeAccountNumber (FinanceAccountNumber)
	 * 
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinanceAccountNumber financeAccountNumber) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into FinanceAccountNumber");
		insertSql.append(" Values( :FinBranch, :FinType, :FinReference, :Account, :AccountType, :AccountCcy, :AccountNumber)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeAccountNumber);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
}