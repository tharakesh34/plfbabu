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
 * FileName    		:  FinanceRepaymentsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinExcessAmountDAOImpl implements FinExcessAmountDAO {
	private static Logger	           logger	= Logger.getLogger(FinExcessAmountDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public FinExcessAmountDAOImpl() {
		super();
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<FinExcessAmount> getExcessAmountsByRef(String finReference) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("");
		selectSql.append(" Select ExcessID, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt From FinExcessAmount");
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinExcessAmount> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinExcessAmount.class);

		List<FinExcessAmount> excessList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return excessList;
	}

	@SuppressWarnings("serial")
	@Override
	public void updateUtilise(long excessID, BigDecimal amount) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExcessID", excessID);
		source.addValue("PaidNow", amount);

		StringBuilder updateSql = new StringBuilder("Update FinExcessAmount");
		updateSql.append(" Set UtilisedAmt = UtilisedAmt + :PaidNow, ReservedAmt = ReservedAmt - :PaidNow ");
		updateSql.append(" Where ExcessID =:ExcessID");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", excessID, PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) { };
		}
	}

	@Override
	public void saveExcessMovement(FinExcessMovement movement) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinExcessMovement");
		insertSql.append(" (ExcessID, ReceiptID, MovementType, TranType, Amount)");
		insertSql.append(" Values(:ExcessID, :ReceiptID, :MovementType, :TranType, :Amount)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(movement);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Populating Error Message Preparation
	 * @param errorId
	 * @param finReference
	 * @param userLanguage
	 * @return
	 */
	private ErrorDetails getError(String errorId, long excessID, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = String.valueOf(excessID);
		parms[0][0] = PennantJavaUtil.getLabel("label_ExcessID") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
				parms[0], parms[1]), userLanguage);
	}

}
