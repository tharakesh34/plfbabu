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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>Promotion model</b> class.<br>
 * 
 */

public class PromotionDAOImpl extends SequenceDao<Promotion> implements PromotionDAO {
   private static Logger logger = Logger.getLogger(PromotionDAOImpl.class);

   public PromotionDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Promotion details by key field
	 * 
	 * @param promotionCode
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Promotion
	 */
	@Override
	public Promotion getPromotionById(final String promotionCode, String type) {
		logger.debug("Entering");

		Promotion promotion = new Promotion();
		promotion.setPromotionCode(promotionCode);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PromotionId, promotionCode,promotionDesc,finType,startDate,endDate,finIsDwPayRequired,");
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
			promotion = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
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
	@Override
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
			recordCount = this.jdbcTemplate.update(deletSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			logger.error("Exception", e);
			throw new DependencyFoundException(e);
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
		
		if (promotion.getPromotionId() == Long.MIN_VALUE) {
			promotion.setPromotionId(getNextId("SeqPromotions"));
			logger.debug("get NextID:" + promotion.getPromotionId());
		}
		
		sql.append("Insert Into Promotions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(PromotionId, promotionCode, promotionDesc, finType, startDate, endDate, finIsDwPayRequired,");
		sql.append(" downPayRule, actualInterestRate, finBaseRate, finSplRate, finMargin, applyRpyPricing,");
		sql.append(" rpyPricingMethod, finMinTerm, finMaxTerm, finMinAmount, finMaxAmount, finMinRate,");
		sql.append(" finMaxRate, active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" :PromotionId, :promotionCode, :promotionDesc, :finType, :startDate, :endDate, :finIsDwPayRequired,");
		sql.append(" :downPayRule, :actualInterestRate, :finBaseRate, :finSplRate, :finMargin, :applyRpyPricing,");
		sql.append(" :rpyPricingMethod, :finMinTerm, :finMaxTerm, :finMinAmount, :finMaxAmount, :finMinRate,");
		sql.append(" :finMaxRate, :active,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
		return promotion.getPromotionCode();
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
	@Override
	public void update(Promotion promotion, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		int recordCount = 0;

		updateSql.append("Update Promotions");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set PromotionId = :PromotionId, promotionCode=:promotionCode, promotionDesc=:promotionDesc, finType=:finType,");
		updateSql.append(" startDate=:startDate, endDate=:endDate, finIsDwPayRequired=:finIsDwPayRequired,");
		updateSql.append(" downPayRule=:downPayRule, actualInterestRate=:actualInterestRate, finBaseRate=:finBaseRate,");
		updateSql.append(" finSplRate=:finSplRate, finMargin=:finMargin, applyRpyPricing=:applyRpyPricing,");
		updateSql.append(" rpyPricingMethod=:rpyPricingMethod, finMinTerm=:finMinTerm, finMaxTerm=:finMaxTerm,");
		updateSql.append(" finMinAmount=:finMinAmount, finMaxAmount=:finMaxAmount, finMinRate=:finMinRate,");
		updateSql.append(" finMaxRate=:finMaxRate, active=:active,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where PromotionCode =:PromotionCode and PromotionId = :PromotionId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
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
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}


	/**
	 * Fetch record count of product
	 * 
	 * @param productCode
	 * @return Integer
	 */
	@Override
	public int getFinanceTypeCountById(String finType) {
		logger.debug("Entering");

		int financeTypeCount = 0;
		Promotion promotion = new Promotion();
		promotion.setFinType(finType);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(FinType) ");
		selectSql.append(" From Promotions Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);

		try {
			financeTypeCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			financeTypeCount = 0;
		}
		logger.debug("Leaving");
		return financeTypeCount;
	}

	/**
	 * Fetch the Promotions Based on the finType
	 * 
	 * @param productCode
	 */
	@Override
	public List<Promotion> getPromotionsByFinType(String finType, String type) {
		logger.debug("Entering");

		Promotion promotion = new Promotion();
		promotion.setFinType(finType);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT FinType, FinTypeDesc, PromotionCode, PromotionDesc");
		selectSql.append(" From Promotions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);
		RowMapper<Promotion> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Promotion.class);
		List<Promotion> PromotionList = new ArrayList<>();
		try {
			PromotionList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return PromotionList;
	}
	
	@Override
	public int getPromotionByRuleCode(long ruleId, String type) {
		logger.debug("Entering");
		Promotion promotion = new Promotion();
		promotion.setDownPayRule(ruleId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From Promotions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DownPayRule =:DownPayRule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(promotion);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}
}