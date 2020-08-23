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
 * FileName    		:  FinFeeConfigDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :    														*
 *                                                                  						*
 * Modified Date    :  														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *                                   * 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.FinFeeConfigDAO;
import com.pennant.backend.model.finance.FinFeeConfig;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>FinFeeConfigDAO </b> class.<br>
 * 
 */

public class FinFeeConfigDAOImpl extends SequenceDao<FinFeeDetail> implements FinFeeConfigDAO {
	private static Logger logger = Logger.getLogger(FinFeeConfigDAOImpl.class);

	public FinFeeConfigDAOImpl() {
		super();
	}

	@Override
	public String save(FinFeeConfig finFeeDetailConfig, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();

		saveQuery(tableType.getSuffix(), sql);

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finFeeDetailConfig);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return finFeeDetailConfig.getFinReference();
	}

	@Override
	public void saveList(List<FinFeeConfig> finFeeDetailConfig, String type) {
		StringBuilder insertSql = new StringBuilder();
		saveQuery(type, insertSql);

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finFeeDetailConfig.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

	}

	private void saveQuery(String type, StringBuilder insertSql) {
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" Insert Into FinFeeConfig ");
		insertSql.append(" (finReference,originationFee, finEvent, feeTypeID, feeOrder, ");
		insertSql.append(" feeScheduleMethod,ruleCode, calculationType, amount ,");
		insertSql.append(" percentage, calculateOn, alwDeviation, alwModifyFeeSchdMthd, alwModifyFee,");
		insertSql.append(" maxWaiverPerc, moduleId, ReferenceId, finTypeFeeId, alwPreIncomization,");
		insertSql.append(" percType ,percRule , percRuleId )");
		insertSql.append(" Values( :finReference,:originationFee, :finEvent, :feeTypeID, :feeOrder,");
		insertSql.append(" :feeScheduleMethod, :ruleCode, :calculationType, :amount,");
		insertSql.append(" :percentage, :calculateOn, :alwDeviation, :alwModifyFeeSchdMthd, :alwModifyFee,");
		insertSql.append(" :maxWaiverPerc, :moduleId, :ReferenceId, :finTypeFeeId, :alwPreIncomization,");
		insertSql.append(" :percType , :percRule , :PercRuleId )");
	}

	@Override
	public List<FinFeeConfig> getFinFeeConfigList(String finReference, String eventCode, boolean origination,
			String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinReference", finReference);
		mapSqlParameterSource.addValue("FinEvent", eventCode);
		mapSqlParameterSource.addValue("Origination", origination);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinTypeFeeId, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeOrder,");
		selectSql.append(
				" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd,  AlwPreIncomization,");
		selectSql.append(" PercType, PercRule, ReferenceId, PercRuleId,");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc, TaxApplicable, TaxComponent");
		}
		selectSql.append(" FROM Finfeeconfig");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference AND FinEvent = :FinEvent ");
		selectSql.append(" AND OriginationFee = :Origination ");
		List<FinFeeConfig> finFeeConfigList = new ArrayList<>();

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinFeeConfig> typeRowMapper = BeanPropertyRowMapper.newInstance(FinFeeConfig.class);
		try {
			finFeeConfigList = this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			finFeeConfigList = new ArrayList<>();
		}
		logger.debug(Literal.LEAVING);
		return finFeeConfigList;
	}

	@Override
	public void update(FinFeeConfig entity, TableType tableType) {

	}

	@Override
	public void delete(FinFeeConfig entity, TableType tableType) {

	}

	@Override
	public int getFinFeeConfigCountByRuleCode(String ruleCode, String type) {
		logger.debug("Entering");
		FinFeeConfig finFeeConfig = new FinFeeConfig();
		finFeeConfig.setPercRule(ruleCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinFeeConfig");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PercRule =:PercRule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeConfig);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

}