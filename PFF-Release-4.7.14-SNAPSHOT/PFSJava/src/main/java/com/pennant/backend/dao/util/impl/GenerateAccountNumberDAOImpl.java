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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.model.SeqAccountNumber;
import com.pennant.backend.dao.util.GenerateAccountNumberDAO;

/**
 * DAO methods implementation for the <b>SeqAccountNumber model</b> class.<br>
 * 
 */
public class GenerateAccountNumberDAOImpl implements GenerateAccountNumberDAO {

	private static Logger logger = Logger.getLogger(GenerateAccountNumberDAOImpl.class);
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GenerateAccountNumberDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record SeqAccountNumber Details details by key field
	 * 
	 * @param id's (String, string, String, boolean)
	 *
	 * @return SeqAccountNumber
	 */
	@Override
	public SeqAccountNumber getSeqAccountNumber(SeqAccountNumber seqAccountNumber,boolean isReadOnly) {
		
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder("Select AccountBranch,AccountHeadCode,AccountCcyCode,AccountSeqNo from SeqAccountNumber" );
		selectQry.append(" Where AccountBranch =:AccountBranch AND AccountHeadCode = :AccountHeadCode");
		selectQry.append("  AND AccountCcyCode= :AccountCcyCode");
		
		logger.debug("selectSql: " + selectQry.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(seqAccountNumber);
		RowMapper<SeqAccountNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SeqAccountNumber.class);

		try {
			seqAccountNumber= this.namedParameterJdbcTemplate.queryForObject(selectQry.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			seqAccountNumber=null;
			logger.error("Exception: ", e);
		}
				
		logger.debug("Leaving");
		return seqAccountNumber;
	}


	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the SeqAccountNumber .
	 * if Record not deleted then throws DataAccessException
	 * with error 41003. delete SeqAccountNumber Details by keys
	 *  AccountBranch,AccountHeadCode, AccountCcyCode
	 * 
	 * @param SeqAccountNumber
	 *            Details (seqAccountNumber)
	 *
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	/**
	 * This method insert new Records into SeqAccountNumber .
	 * 
	 * save SeqAccountNumber Details
	 * 
	 * @param SeqAccountNumber
	 *            Details (seqAccountNumber)
	 * 
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(SeqAccountNumber seqAccountNumber) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into SeqAccountNumber");
		insertSql.append(" Values(:AccountBranch, :AccountHeadCode, :AccountCcyCode, :AccountSeqNo )");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(seqAccountNumber);
		int count = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(count+" for insert");
		logger.debug("Leaving");
		
	}

	/**
	 * This method updates the Record SeqAccountNumber. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update SeqAccountNumber Details by keys AccountBranch, AccountHeadCode, AccountCcyCode
	 * 
	 * @param SeqAccountNumber
	 *            Details (seqAccountNumber)
	 * 
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SeqAccountNumber seqAccountNumber) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update SeqAccountNumber");
		updateSql.append(" Set AccountSeqNo = :AccountSeqNo " );
		updateSql.append(" Where AccountBranch =:AccountBranch AND AccountHeadCode =:AccountHeadCode" );
		updateSql.append(" AND AccountCcyCode =:AccountCcyCode ") ;
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(seqAccountNumber);
		int count =  this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug(count+" for update");
		logger.debug("Leaving");
	}

}