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
 * FileName    		:  FinExpenseMovementsDAOImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2017       Pennant	                 0.1                                            * 
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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinExpenseMovementsDAO;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class FinExpenseMovementsDAOImpl extends SequenceDao<FinExpenseMovements> implements FinExpenseMovementsDAO {
	private static Logger logger = Logger.getLogger(FinExpenseMovementsDAOImpl.class);

	public FinExpenseMovementsDAOImpl() {
		super();
	}

	@Override
	public long saveFinExpenseMovements(FinExpenseMovements finExpenseMovements) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();

		if (finExpenseMovements.getFinExpenseMovemntId() == Long.MIN_VALUE) {
			finExpenseMovements.setFinExpenseMovemntId(getNextValue("SeqFinExpenseMovements"));
			logger.debug("get NextID:" + finExpenseMovements.getFinExpenseMovemntId());
		}

		sql.append(" Insert Into FinExpenseMovements");
		sql.append(
				" (FinExpenseMovemntId, FinExpenseId, FinReference, ModeType, UploadId, TransactionAmount, TransactionType, LastMntOn, TransactionDate)");
		sql.append(
				" Values(:FinExpenseMovemntId, :FinExpenseId, :FinReference, :ModeType, :UploadId, :TransactionAmount, :TransactionType, :LastMntOn, :TransactionDate)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExpenseMovements);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");

		return finExpenseMovements.getFinExpenseMovemntId();
	}

	@Override
	public List<FinExpenseMovements> getFinExpenseMovementById(String financeRef, long finExpenseId) {
		logger.debug("Entering");

		FinExpenseMovements finExpenseMovements = new FinExpenseMovements();
		finExpenseMovements.setFinReference(financeRef);
		finExpenseMovements.setFinExpenseId(finExpenseId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT T1.modetype,T1.transactiontype,T1.transactiondate,T1.transactionamount,T2.fileName,T2.lastmntby");

		selectSql.append(" From finexpensemovements T1");
		selectSql.append(" Inner join uploadheader T2 on T2.uploadid=T1.uploadid");
		selectSql.append(" Where T1.FinReference = :FinReference And T1.FINEXPENSEID = :FinExpenseId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExpenseMovements);
		RowMapper<FinExpenseMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinExpenseMovements.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
}