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
 * FileName    		:  FinancePremiumDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-02-2012    														*
 *                                                                  						*
 * Modified Date    :  09-02-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-02-2012       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.finance.FinancePremiumDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinancePremiumDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>FinancePremiumDetail model</b> class.<br>
 * 
 */
public class FinancePremiumDetailDAOImpl implements FinancePremiumDetailDAO {

	private static Logger logger = Logger.getLogger(FinancePremiumDetailDAOImpl.class);

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
	 *  Method for get the FinancePremiumDetail Object by Key finReference
	 */
	@Override
	public FinancePremiumDetail getFinPremiumDetailsById(String finReference, String type) {
		logger.debug("Entering");
		
		FinancePremiumDetail detail = new FinancePremiumDetail();
		detail.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("SELECT FinReference , IssueNumber , NoOfUnits, FaceValue , PremiumType , PremiumValue, " );
		selectSql.append(" PricePerUnit , YieldValue , LastCouponDate , AccruedProfit , PurchaseDate, FairValuePerUnit, FairValueAmount " );
		selectSql.append(" FROM FinancePremiumDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<FinancePremiumDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinancePremiumDetail.class);

		try {
			detail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			detail = null;
		}
		logger.debug("Leaving");
		return detail;
	}
	
	/**
	 *  Method for get the FinancePremiumDetail Object by Key finReference
	 */
	@Override
	public BigDecimal getFairValueAmount(String finReference, String type) {
		logger.debug("Entering");
		
		BigDecimal fairValueAmount = BigDecimal.ZERO;
		FinancePremiumDetail detail = new FinancePremiumDetail();
		detail.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("SELECT FairValueAmount " );
		selectSql.append(" FROM FinancePremiumDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try {
			fairValueAmount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			fairValueAmount = BigDecimal.ZERO;
		}
		logger.debug("Leaving");
		return fairValueAmount;
	}
	
	@SuppressWarnings("serial")
    @Override
	public void update(FinancePremiumDetail premiumDetail, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinancePremiumDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinReference=:FinReference , IssueNumber=:IssueNumber , NoOfUnits=:NoOfUnits, " );
		updateSql.append(" FaceValue=:FaceValue , PremiumType=:PremiumType , PremiumValue=:PremiumValue, " );
		updateSql.append(" PricePerUnit=:PricePerUnit , YieldValue=:YieldValue , LastCouponDate=:LastCouponDate , " );
		updateSql.append(" AccruedProfit=:AccruedProfit , PurchaseDate=:PurchaseDate, FairValuePerUnit=:FairValuePerUnit, FairValueAmount=:FairValueAmount " );
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(premiumDetail);
		int recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", premiumDetail.getFinReference(), PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) { };
		}

		logger.debug("Leaving");
	}

	@SuppressWarnings("serial")
    @Override
	public void updateAccruedAmount(FinancePremiumDetail premiumDetail) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinancePremiumDetail");
		updateSql.append(" Set AccruedProfit=:AccruedProfit " );
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(premiumDetail);
		int recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", premiumDetail.getFinReference(), PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) { };
		}

		logger.debug("Leaving");
	}

	@Override
	public String save(FinancePremiumDetail premiumDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinancePremiumDetail").append(type);
		insertSql.append(" (FinReference , IssueNumber , NoOfUnits, FaceValue , PremiumType , PremiumValue, " );
		insertSql.append(" PricePerUnit , YieldValue , LastCouponDate , AccruedProfit , PurchaseDate, FairValuePerUnit, FairValueAmount )" );
		insertSql.append(" VALUES (:FinReference , :IssueNumber , :NoOfUnits, :FaceValue , :PremiumType , :PremiumValue, " );
		insertSql.append(" :PricePerUnit , :YieldValue , :LastCouponDate , :AccruedProfit , :PurchaseDate, :FairValuePerUnit, :FairValueAmount )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(premiumDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return premiumDetail.getFinReference();
	}

    @Override
    public void delete(FinancePremiumDetail premiumDetail, String type) {
    	logger.debug("Entering");

    	StringBuilder deleteSql = new StringBuilder("Delete From FinancePremiumDetail");
    	deleteSql.append(StringUtils.trimToEmpty(type));
    	deleteSql.append(" Where FinReference =:FinReference");
    	logger.debug("deleteSql: " + deleteSql.toString());

    	SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(premiumDetail);
    	this.namedParameterJdbcTemplate.update(deleteSql.toString(),  beanParameters);
    }
	
	private ErrorDetails getError(String errorId, String finReference, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
		        parms[0], parms[1]), userLanguage);
	}
	
}
