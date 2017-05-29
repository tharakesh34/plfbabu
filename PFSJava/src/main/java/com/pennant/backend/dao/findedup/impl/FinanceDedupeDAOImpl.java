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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

	public FinanceDedupeDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
    public void saveList(List<FinanceDedup> dedups,String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinDedupDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference ,DupReference, CustCIF , CustCRCPR , ChassisNumber , ");
		insertSql.append(" EngineNumber , StartDate , FinanceAmount ,FinanceType , ");
		insertSql.append("  ProfitAmount , Stage ,DedupeRule, OverrideUser,FinLimitRef)");
		insertSql.append(" Values(:FinReference ,:DupReference, :CustCIF , :CustCRCPR , :ChassisNumber , ");
		insertSql.append(" :EngineNumber , :StartDate , :FinanceAmount ,:FinanceType ,  ");
		insertSql.append(" :ProfitAmount , :Stage , :DedupeRule, :OverrideUser,:FinLimitRef)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(dedups.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
    }
	/*
	 * This  updated the Finance dedup list 
	 */
	@Override
    public void updateList(List<FinanceDedup> dedups) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinDedupDetail");
		updateSql.append(" Set CustCIF = :CustCIF , CustCRCPR = :CustCRCPR , ChassisNumber = :ChassisNumber, ");
		updateSql.append(" EngineNumber = :EngineNumber, StartDate = :StartDate , FinanceAmount = :FinanceAmount ,FinanceType = :FinanceType, ");
		updateSql.append("  ProfitAmount= :ProfitAmount , Stage = :Stage ,DedupeRule = :DedupeRule,  ");
		updateSql.append(" OverrideUser = :OverrideUser,FinLimitRef = :FinLimitRef");
		updateSql.append(" Where FinReference =:FinReference And DupReference=:DupReference ");
		
		logger.debug("insertSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(dedups.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	    
    }

	@Override
    public List<FinanceDedup> fetchOverrideDedupData(String finReference, String queryCode) {
		logger.debug("Entering");
		
		FinanceDedup dedup = new FinanceDedup();
		dedup.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select D.FinReference ,D.DupReference, D.CustCIF , D.CustCRCPR , D.ChassisNumber , ");
		selectSql.append(" D.EngineNumber , D.StartDate , D.FinanceAmount ,D.FinanceType , ");
		selectSql.append(" D.ProfitAmount , D.Stage ,D.DedupeRule, D.OverrideUser, S.RoleDesc StageDesc,D.FinLimitRef");
		selectSql.append(" From FinDedupDetail D LEFT OUTER JOIN SecRoles S ON S.RoleCd = D.Stage ");
		selectSql.append(" Where D.FinReference = :FinReference AND D.DedupeRule LIKE  '%,");
		selectSql.append(queryCode.trim());
		selectSql.append(",%' ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		RowMapper<FinanceDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDedup.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	@Override
    public void deleteList(String finReference) {
		logger.debug("Entering");
		FinanceDedup dedup = new FinanceDedup();
		dedup.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinDedupDetail");
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	@Override
    public void moveData(String finReference, String suffix) {
		logger.debug(" Entering ");
		try {
	        if (StringUtils.isBlank(suffix)) {
	            return;
	        }
	        
	        MapSqlParameterSource map=new MapSqlParameterSource();
	        map.addValue("FinReference", finReference);
	        
	        StringBuilder selectSql = new StringBuilder();
	        selectSql.append(" SELECT * FROM FinDedupDetail");
	        selectSql.append(" WHERE FinReference = :FinReference ");
	        
	        RowMapper<FinanceDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDedup.class);
	        List<FinanceDedup> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), map,typeRowMapper);
	        
	        if (list!=null && !list.isEmpty()) {
	        	saveList(list,suffix);
            }
	        
        } catch (DataAccessException e) {
	     logger.debug(e);
        }
	    logger.debug(" Leaving ");
    }
	
}