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

package com.pennant.backend.dao.findedup.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.findedup.FinanceDedupeDAO;
import com.pennant.backend.model.finance.FinanceDedup;

/**
 * DAO methods implementation for the <b>FinanceDedup model</b> class.<br>
 * 
 */
public class FinanceDedupeDAOImpl implements FinanceDedupeDAO {

	private static Logger logger = Logger.getLogger(FinanceDedupeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
    public void saveList(List<FinanceDedup> dedups) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinDedupDetail");
		insertSql.append(" (FinReference , CustCIF , CustCRCPR , ChassisNumber , ");
		insertSql.append(" EngineNumber , StartDate , FinanceAmount ,FinanceType , ");
		insertSql.append("  ProfitAmount , Stage ,DedupeRule, OverrideUser)");
		insertSql.append(" Values(:FinReference , :CustCIF , :CustCRCPR , :ChassisNumber , ");
		insertSql.append(" :EngineNumber , :StartDate , :FinanceAmount ,:FinanceType ,  ");
		insertSql.append(" :ProfitAmount , :Stage , :DedupeRule, :OverrideUser)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(dedups.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
    }

	@Override
    public List<FinanceDedup> fetchOverrideDedupData(String finReference, String queryCode) {
		logger.debug("Entering");
		
		FinanceDedup dedup = new FinanceDedup();
		dedup.setFinReference(finReference);
		dedup.setDedupeRule(queryCode);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustCRCPR , ChassisNumber , ");
		selectSql.append(" EngineNumber , StartDate , FinanceAmount ,FinanceType , ");
		selectSql.append("  ProfitAmount , Stage ,DedupeRule, OverrideUser ");
		selectSql.append(" From FinDedupDetail");
		selectSql.append(" Where FinReference =:FinReference AND DedupeRule=:DedupeRule ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		RowMapper<FinanceDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDedup.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	


}