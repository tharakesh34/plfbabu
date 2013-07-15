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
 * FileName    		:  FinODDetailsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-05-2012    														*
 *                                                                  						*
 * Modified Date    :  08-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-05-2012       Pennant	                 0.1                                            * 
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

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinODDetails;

/**
 * DAO methods implementation for the <b>FinODDetails model</b> class.<br>
 * 
 */
public class FinODDetailsDAOImpl extends BasisCodeDAO<FinODDetails> implements FinODDetailsDAO {

	private static Logger logger = Logger.getLogger(FinODDetailsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 *  Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public FinODDetails getFinODDetailsById(String finReference, Date schdDate, String overDueFor) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinODSchdDate(schdDate);
		finODDetails.setFinODFor(overDueFor);

		StringBuilder selectSql = new StringBuilder("Select FinReference, FinBranch, FinODFor,");
		selectSql.append(" FinType, CustID ,FinODSchdDate ,FinODTillDate,");
		selectSql.append(" FinCurODAmt, FinMaxODAmt ,FinCurODDays ,FinLMdfDate");
		selectSql.append(" From FinODDetails");
		selectSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate" );
		selectSql.append(" AND FinODFor =:FinODFor ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finODDetails = null;
		}
		logger.debug("Leaving");
		return finODDetails;
	}

	@Override
	public void update(FinODDetails finOdDetails) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql.append("Set FinReference= :FinReference ,FinBranch= :FinBranch,");
		updateSql.append(" FinType= :FinType, CustID = :CustID, FinODSchdDate = :FinODSchdDate,");
		updateSql.append(" FinODTillDate= :FinODTillDate, FinCurODAmt = :FinCurODAmt, FinMaxODAmt = :FinMaxODAmt,");
		updateSql.append(" FinCurODDays= :FinCurODDays, FinODFor = :FinODFor, FinLMdfDate = :FinLMdfDate");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate" );
		updateSql.append(" AND FinODFor =:FinODFor ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	public void save(FinODDetails finOdDetails) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinODDetails");
		insertSql.append(" (FinReference,FinBranch,"); 
		insertSql.append(" FinType, CustID ,FinODSchdDate ,FinODTillDate,");
		insertSql.append(" FinCurODAmt, FinMaxODAmt ,FinCurODDays ,FinODFor ,FinLMdfDate)");
		insertSql.append(" Values(:FinReference, :FinBranch,"); 
		insertSql.append(" :FinType, :CustID ,:FinODSchdDate ,:FinODTillDate,");
		insertSql.append(" :FinCurODAmt, :FinMaxODAmt ,:FinCurODDays ,:FinODFor ,:FinLMdfDate)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for getting Count of records in FinODDetails
	 * @param finReference
	 * @return
	 */
	public int getFinOverDueCntInPast(String finReference,boolean instCond){
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder(" Select count(FinODSchdDate)  " );
		selectQry.append(" from FinODDetails" );
		selectQry.append(" where FinReference = '" );
		selectQry.append(finReference+"'");
		if(instCond){
			selectQry.append(" AND FinCurODAmt <> 0 ");
		}
		logger.debug("selectSql: " + selectQry.toString());

		int recordCount =  this.namedParameterJdbcTemplate.getJdbcOperations().queryForInt(selectQry.toString());
		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Method for get the count of FinCurODDays
	 */
	public int getFinCurODDays(String finReference){
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder(" SELECT sum(FinCurODDays) " );
		selectQry.append(" from FinODDetails" );
		selectQry.append(" where FinReference = '" );
		selectQry.append(finReference+"'");
		logger.debug("selectSql: " + selectQry.toString());

		int recordCount =  this.namedParameterJdbcTemplate.getJdbcOperations().queryForInt(selectQry.toString());
		logger.debug("Leaving");
		return recordCount;
	}
	
	/**
	 * Method for hetting OverDue Details Object
	 * 
	 * @param finReference,type
	 */
	public int getPendingOverDuePayment(String finReference){
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder selectSql =new StringBuilder(" SELECT MAX(FinCurODDays) From FinODDetails");
		selectSql.append(" Where FinReference =:FinReference " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), beanParameters);
	}

}
