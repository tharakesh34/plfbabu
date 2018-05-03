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
 * FileName    		:  FinanceRateReviewDAOImpl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceRateReviewDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinanceRateReview;

/**
 * DAO methods implementation for the <b>RepayInstruction model</b> class.<br>
 * 
 */

public class FinanceRateReviewDAOImpl extends BasisCodeDAO<FinanceRateReview> implements FinanceRateReviewDAO {

	private static Logger				logger	= Logger.getLogger(FinanceRateReviewDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinanceRateReviewDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Repay Instruction Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepayInstruction
	 */
	@Override
	public List<FinanceRateReview> getFinanceRateReviewById(final String id,Date date) {
		logger.debug("Entering");

		FinanceRateReview financeRateReview = new FinanceRateReview();
		financeRateReview.setFinReference(id);

		StringBuilder selectSql = new StringBuilder("Select FinReference, RateType, Currency,ValueDate, EffectiveDate,");
		selectSql.append(" EventFromDate, EventToDate, RecalFromdate, RecalToDate, EMIAmount");
		selectSql.append(" From FinanceRateReview");
		selectSql.append(" Where FinReference =:FinReference and ValueDate = :ValueDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRateReview);
		RowMapper<FinanceRateReview> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceRateReview.class);

		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeRateReview = null;
		}
		logger.debug("Leaving");
		return Collections.emptyList();
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void save(FinanceRateReview financeRateReview) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FinanceRateReview");
		insertSql.append(" ( FinReference, RateType, Currency,ValueDate, EffectiveDate, EventFromDate, EventToDate, ");
		insertSql.append("  RecalFromdate, RecalToDate, EMIAmount ) ");
		insertSql.append(" Values ( :FinReference, :RateType, :Currency, :ValueDate , :EffectiveDate, :EventFromDate, ");
		insertSql.append(" :EventToDate, :RecalFromdate, :RecalToDate, :EMIAmount)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRateReview);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}