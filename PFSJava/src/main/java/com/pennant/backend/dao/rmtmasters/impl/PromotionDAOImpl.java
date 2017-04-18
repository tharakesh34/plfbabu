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
 * FileName    		:  PromotionDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>Promotion model</b> class.<br>
 * 
 */

public class PromotionDAOImpl extends BasisCodeDAO<Promotion> implements PromotionDAO {

	private static Logger logger = Logger.getLogger(PromotionDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PromotionDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Promotion details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Promotion
	 */
	@Override
	public Promotion getPromotionById(final String id, String type) {
		logger.debug("Entering");

		Promotion promotion = new Promotion();
		promotion.setId(id);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT promotionCode,promotionDesc,finType,startDate,endDate,finIsDwPayRequired,");
		sql.append(" downPayRule,actualInterestRate,finBaseRate,finSplRate,finMargin,applyRpyPricing,");
		sql.append(" rpyPricingMethod,finMinTerm,finMaxTerm,finMinAmount,finMaxAmount,finMinRate,");
		sql.append(" finMaxRate,active,");

		if (type.contains("View")) {
			sql.append(" finCcy, FinTypeDesc, DownPayRuleCode, DownPayRuleDesc, RpyPricingCode, RpyPricingDesc, ");
		}

		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From Promotions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PromotionCode =:PromotionCode");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		RowMapper<Promotion> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Promotion.class);

		try {
			promotion = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			promotion = null;
		}
	
		logger.debug("Leaving");
		
		return promotion;
	}

	/**
	 * This method Deletes the Record from the Promotions or Promotions_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Promotion by key PromotionCode
	 * 
	 * @param Promotion
	 *            (promotion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Promotion promotion, String type) {
		logger.debug("Entering");
		
		StringBuilder deletSql = new StringBuilder();
		int recordCount = 0;

		deletSql.append("Delete From Promotions");
		deletSql.append(StringUtils.trimToEmpty(type));
		deletSql.append(" Where PromotionCode =:PromotionCode");

		logger.debug("deletSql: " + deletSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deletSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", promotion.getId(), promotion.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) { };
			}
		} catch (DataAccessException e) {
			logger.error("Exception", e);
			ErrorDetails errorDetails = getError("41006", promotion.getId(), promotion.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into Promotions or Promotions_Temp.
	 * 
	 * save Promotion
	 * 
	 * @param Promotion
	 *            (promotion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Promotion promotion, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into Promotions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(promotionCode,promotionDesc,finType,startDate,endDate,finIsDwPayRequired,");
		sql.append("downPayRule,actualInterestRate,finBaseRate,finSplRate,finMargin,applyRpyPricing,");
		sql.append("rpyPricingMethod,finMinTerm,finMaxTerm,finMinAmount,finMaxAmount,finMinRate,");
		sql.append(" finMaxRate,active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" :promotionCode,:promotionDesc,:finType,:startDate,:endDate,:finIsDwPayRequired,");
		sql.append(" :downPayRule,:actualInterestRate,:finBaseRate,:finSplRate,:finMargin,:applyRpyPricing,");
		sql.append(" :rpyPricingMethod,:finMinTerm,:finMaxTerm,:finMinAmount,:finMaxAmount,:finMinRate,");
		sql.append(" :finMaxRate,:active,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
		return promotion.getId();
	}

	/**
	 * This method updates the Record Promotions or Promotions_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Promotion by key PromotionCode and Version
	 * 
	 * @param Promotion
	 *            (promotion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Promotion promotion, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		int recordCount = 0;

		updateSql.append("Update Promotions");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set promotionCode=:promotionCode,promotionDesc=:promotionDesc,finType=:finType,");
		updateSql.append(" startDate=:startDate,endDate=:endDate,finIsDwPayRequired=:finIsDwPayRequired,");
		updateSql.append(" downPayRule=:downPayRule,actualInterestRate=:actualInterestRate,finBaseRate=:finBaseRate,");
		updateSql.append(" finSplRate=:finSplRate,finMargin=:finMargin,applyRpyPricing=:applyRpyPricing,");
		updateSql.append(" rpyPricingMethod=:rpyPricingMethod,finMinTerm=:finMinTerm,finMaxTerm=:finMaxTerm,");
		updateSql.append(" finMinAmount=:finMinAmount,finMaxAmount=:finMaxAmount,finMinRate=:finMinRate,");
		updateSql.append(" finMaxRate=:finMaxRate,active=:active,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where PromotionCode =:PromotionCode");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", promotion.getId(), promotion.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}

		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String promotionCode, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = promotionCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_PromotionCode") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]), userLanguage);
	}

	@Override
	public int getPromtionCodeCount(String promotionCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(*) From Promotions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PromotionCode = :PromotionCode");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PromotionCode", promotionCode);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}