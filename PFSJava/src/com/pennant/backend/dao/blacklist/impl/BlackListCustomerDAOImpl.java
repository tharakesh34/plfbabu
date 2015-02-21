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
 * FileName    		:  DedupParmDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.blacklist.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.blacklist.BlackListCustomerDAO;
import com.pennant.backend.model.blacklist.BlackListCustomers;

/**
 * DAO methods implementation for the <b>DedupParm model</b> class.<br>
 * 
 */
public class BlackListCustomerDAOImpl implements BlackListCustomerDAO {

	private static Logger logger = Logger.getLogger(BlackListCustomerDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
    public void saveList(List<BlackListCustomers> blackListCustomers) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinBlackListDetail");
		insertSql.append(" (FinReference , CustCIF , CustFName , CustLName , ");
		insertSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , PhoneNumber , CustNationality , ");
		insertSql.append(" Employer , WatchListRule , Override , OverrideUser )");
		insertSql.append(" Values(:FinReference , :CustCIF , :CustFName , :CustLName , ");
		insertSql.append(" :CustShrtName , :CustDOB , :CustCRCPR ,:CustPassportNo , :PhoneNumber , :CustNationality , ");
		insertSql.append(" :Employer , :WatchListRule , :Override , :OverrideUser)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(blackListCustomers.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	@Override
    public List<BlackListCustomers> fetchOverrideBlackListData(String finReference, String queryCode) {
		logger.debug("Entering");
		
		BlackListCustomers blackListCustomer = new BlackListCustomers();
		blackListCustomer.setFinReference(finReference);
		blackListCustomer.setWatchListRule(queryCode);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , PhoneNumber , CustNationality , ");
		selectSql.append(" Employer , WatchListRule , Override , OverrideUser ");
		selectSql.append(" From FinBlackListDetail");
		selectSql.append(" Where FinReference =:FinReference AND WatchListRule=:WatchListRule ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(blackListCustomer);
		RowMapper<BlackListCustomers> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BlackListCustomers.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	


}