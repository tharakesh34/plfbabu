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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  FinFeeCharges.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  10-06-2014    
 *                                                                  
 * Modified Date    :  10-06-2014    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-06-2014       PENNANT TECHONOLOGIES	                 0.1                            * 
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

package com.pennant.backend.dao.rulefactory.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.RuleConstants;

public class FinFeeChargesDAOImpl extends BasisCodeDAO<FeeRule> implements FinFeeChargesDAO {

private static Logger logger = Logger.getLogger(FinFeeChargesDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public FinFeeChargesDAOImpl() {
		super();
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * Method for Fetching Fee charge Details list based upon Reference
	 */
	@Override
	public FeeRule getFeeChargesByFinRefAndFee(String finReference, String feeCode, String tableType) {
		logger.debug("Entering");
		
		FeeRule feeRule = new FeeRule();
		feeRule.setFinReference(finReference);
		feeRule.setFeeCode(feeCode);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference , SchDate , FinEvent, FeeCode , SeqNo, FeeCodeDesc , FeeOrder , FeeToFinance, " );
		selectSql.append(" AllowWaiver, WaiverPerc,CalFeeAmount, FeeAmount, WaiverAmount, PaidAmount, ExcludeFromRpt, CalFeeModify, FeeMethod, ScheduleTerms " );
		selectSql.append(" FROM FinFeeCharges");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" WHERE  FinReference=:FinReference AND FeeCode=:FeeCode ORDER BY SchDate, FeeOrder");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeRule);
		RowMapper<FeeRule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeRule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**
	 * Method for Fetching Fee charge Details list based upon Reference
	 */
	@Override
	public boolean updateFeeChargesByFinRefAndFee(FeeRule feeRule, String tableType) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" UPDATE FinFeeCharges");
		updateSql.append(StringUtils.trimToEmpty(tableType));
		updateSql.append(" Set ExcludeFromRpt = :ExcludeFromRpt ");
		updateSql.append(" WHERE FinReference=:FinReference AND FeeCode=:FeeCode");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeRule);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
		if (recordCount <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Method for saving Fee charge Details list
	 */
	@Override
	public void saveChargesBatch(List<FeeRule> chargeList,boolean isWIF,  String tableType) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		if(isWIF){
			insertSql.append(" INSERT INTO WIFFinFeeCharges");
		}else{
			insertSql.append(" INSERT INTO FinFeeCharges");
		}
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql.append(" (FinReference , SchDate , FinEvent , FeeCode , SeqNo, FeeCodeDesc , FeeOrder ,FeeToFinance, AllowWaiver, WaiverPerc,CalFeeAmount, FeeAmount, WaiverAmount, PaidAmount, CalFeeModify, FeeMethod, ScheduleTerms) ");
		insertSql.append(" VALUES (:FinReference , :SchDate , :FinEvent, :FeeCode , :SeqNo, :FeeCodeDesc , :FeeOrder ,:FeeToFinance, :AllowWaiver, :WaiverPerc,:CalFeeAmount, :FeeAmount, :WaiverAmount, :PaidAmount, :CalFeeModify, :FeeMethod, :ScheduleTerms) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(chargeList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for saving Fee charge Details list
	 */
	@Override
	public void deleteChargesBatch(String finReference,String finEvent, boolean isWIF, String tableType) {
		logger.debug("Entering");
		
		FeeRule feeRule = new FeeRule();
		feeRule.setFinReference(finReference);
		feeRule.setFinEvent(finEvent);
		
		StringBuilder deleteSql = new StringBuilder();
		if(isWIF){
			deleteSql.append(" DELETE FROM WIFFinFeeCharges");	
		}else{
			deleteSql.append(" DELETE FROM FinFeeCharges");
		}
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" WHERE FinReference =:FinReference AND FinEvent=:FinEvent ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeRule);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching Fee charge Details list based upon Reference
	 */
	@Override
    public List<FeeRule> getFeeChargesByFinRef(String finReference, String finEvent, boolean isWIF, String tableType) {
		logger.debug("Entering");
		
		FeeRule feeRule = new FeeRule();
		feeRule.setFinReference(finReference);
		feeRule.setFinEvent(finEvent);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference , SchDate , FinEvent, FeeCode , SeqNo, FeeCodeDesc , FeeOrder , FeeToFinance, " );
		selectSql.append(" AllowWaiver, WaiverPerc, CalFeeAmount, FeeAmount, WaiverAmount, PaidAmount, CalFeeModify,FeeMethod, ScheduleTerms " );
		if(isWIF){
			selectSql.append(" FROM WIFFinFeeCharges");
		}else {
			
			if(!"_VIEW".equalsIgnoreCase(tableType) && !"_AVIEW".equalsIgnoreCase(tableType)){
				selectSql.append("	, ExcludeFromRpt ");  
			}
			selectSql.append(" FROM FinFeeCharges");
		}
		
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" WHERE  FinReference=:FinReference ");
		if(StringUtils.isNotEmpty(finEvent)){
			selectSql.append(" AND FinEvent=:FinEvent ");
		}
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeRule);
		RowMapper<FeeRule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeRule.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }

	@Override
	public FeeRule getInsFee(String finReference, String type) {
		logger.debug("Entering");
		
		FeeRule feeRule = new FeeRule();
		feeRule.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SUM(FeeAmount) FeeAmount, SUM(WaiverAmount) WaiverAmount, SUM(PaidAmount) PaidAmount " );
		selectSql.append(" FROM FinFeeCharges");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE FinReference=:FinReference AND FeeCode IN ('" );
		selectSql.append(RuleConstants.TAKAFUL_FEE);
		selectSql.append("', '");
		selectSql.append(RuleConstants.AUTOINS_FEE);
		selectSql.append("' )");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeRule);
		RowMapper<FeeRule> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeRule.class);
		
		try {
			feeRule = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			feeRule = null;
		}
		
		logger.debug("Leaving");
		return feeRule;
	}
}
