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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinExpenseMovementsDAO;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

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
		sql.append(" (FinExpenseMovemntId, FinExpenseId, FinReference, ModeType, UploadId");
		sql.append(", TransactionAmount, TransactionType, LastMntOn");
		sql.append(", TransactionDate, LinkedTranId, RevLinkedTranID)");
		sql.append(" values");
		sql.append(" (:FinExpenseMovemntId, :FinExpenseId, :FinReference, :ModeType, :UploadId");
		sql.append(", :TransactionAmount, :TransactionType, :LastMntOn");
		sql.append(", :TransactionDate, :LinkedTranId, :RevLinkedTranId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExpenseMovements);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return finExpenseMovements.getFinExpenseMovemntId();
	}

	@Override
	public List<FinExpenseMovements> getFinExpenseMovementById(String financeRef, long finExpenseId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT m.ModeType, m.TransactionType, m.TransactionDate, m.TransactionAmount");
		sql.append(", h.FileName, h.LastMntBy");
		sql.append(" From FinExpenseMovements m");
		sql.append(" inner join UploadHeader h on h.uploadId = m.UploadId");
		sql.append(" Where m.FinReference = ? and m.FinExpenseID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new Object[] { financeRef, finExpenseId },
					new RowMapper<FinExpenseMovements>() {

						@Override
						public FinExpenseMovements mapRow(ResultSet rs, int arg1) throws SQLException {
							FinExpenseMovements movement = new FinExpenseMovements();
							movement.setModeType(rs.getString("ModeType"));
							movement.setTransactionType(rs.getString("TransactionType"));
							movement.setTransactionDate(JdbcUtil.getDate(rs.getDate("TransactionDate")));
							movement.setTransactionAmount(rs.getBigDecimal("TransactionAmount"));
							movement.setFileName(rs.getString("FileName"));
							movement.setLastMntBy(rs.getLong("LastMntBy"));
							return movement;
						}
					});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<FinExpenseMovements> getFinExpenseMovements(String financeRef, long expenseTypeID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select m.FinExpenseMovemntID, m.LinkedTranID");
		sql.append(" From FinExpenseDetails ed");
		sql.append(" inner join FinExpenseMovements m on m.FinExpenseId = ed.FinExpenseId");
		sql.append(" Where ed.FinReference = ? and ed.ExpenseTypeID = ? and RevLinkedTranID is NULL");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new Object[] { financeRef, expenseTypeID },
					new RowMapper<FinExpenseMovements>() {

						@Override
						public FinExpenseMovements mapRow(ResultSet rs, int arg1) throws SQLException {
							FinExpenseMovements movement = new FinExpenseMovements();
							movement.setFinExpenseMovemntId(rs.getLong("FinExpenseMovemntID"));
							movement.setLinkedTranId(rs.getLong("LinkedTranID"));
							return movement;
						}
					});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public void updateRevLinkedTranID(long id, long revLinkedTranID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" update FinExpenseMovements set RevLinkedTranID = ?");
		sql.append(" Where FinExpenseMovemntID = ?");

		this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, revLinkedTranID);
				ps.setLong(2, id);

			}
		});

		logger.trace(Literal.SQL + sql.toString());

	}
}