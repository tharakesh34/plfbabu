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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.Literal;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinExcessAmountDAOImpl extends BasisNextidDaoImpl<FinExcessAmount> implements FinExcessAmountDAO {
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

	/**
	 * Method for Fetching List of Excess amounts exist against Finance Reference
	 */
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

	/**
	 * Method for Saving Excess Movements after Excess Utilization
	 */
	@Override
	public void saveExcess(FinExcessAmount excess) {
		logger.debug("Entering");
		
		if (excess.getId() == 0 || excess.getId() == Long.MIN_VALUE) {
			excess.setId(getNextidviewDAO().getNextId("SeqFinExcessAmount"));
			logger.debug("get NextID:" + excess.getId());
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinExcessAmount");
		insertSql.append(" (ExcessID, FinReference, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt)");
		insertSql.append(" Values(:ExcessID, :FinReference, :AmountType, :Amount, :UtilisedAmt, :ReservedAmt, :BalanceAmt)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(excess);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Update utilization amount after amounts Approval
	 */
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
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Update Excess Balance amount after amounts Approval
	 */
	@SuppressWarnings("serial")
	@Override
	public void updateExcessBal(long excessID, BigDecimal amount) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExcessID", excessID);
		source.addValue("PaidNow", amount);

		StringBuilder updateSql = new StringBuilder("Update FinExcessAmount");
		updateSql.append(" Set Amount = Amount + :PaidNow, BalanceAmt = BalanceAmt + :PaidNow ");
		updateSql.append(" Where ExcessID =:ExcessID");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", excessID, PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Method for Update Excess Balance amount after amounts Approval
	 */
	@Override
	public int updateExcessBalByRef(String reference, String amountType, BigDecimal amount) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", reference);
		source.addValue("AmountType", amountType);
		source.addValue("PaidNow", amount);

		StringBuilder updateSql = new StringBuilder("Update FinExcessAmount");
		updateSql.append(" Set Amount = Amount + :PaidNow, BalanceAmt = BalanceAmt + :PaidNow ");
		updateSql.append(" Where FinReference =:FinReference AND AmountType=:AmountType ");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Method for Saving Excess Movements after Excess Utilization
	 */
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
	 * Method for updating Reserved amount against Excess ID
	 */
	@SuppressWarnings("serial")
	@Override
	public void updateExcessReserve(long payAgainstID, BigDecimal reserveAmt) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExcessID", payAgainstID);
		source.addValue("PaidNow", reserveAmt);

		StringBuilder updateSql = new StringBuilder("Update FinExcessAmount ");
		updateSql.append(" Set ReservedAmt = ReservedAmt + :PaidNow, BalanceAmt = BalanceAmt - :PaidNow ");
		updateSql.append(" Where ExcessID =:ExcessID");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", payAgainstID, PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Fetch the Reserved Excess Amounts Log details
	 */
	@Override
	public FinExcessAmountReserve getExcessReserve(long receiptID, long payAgainstID) {
		logger.debug("Entering");

		FinExcessAmountReserve reserve = new FinExcessAmountReserve();
		reserve.setReceiptID(receiptID);
		reserve.setExcessID(payAgainstID);

		StringBuilder selectSql = new StringBuilder(" Select ReceiptID, ExcessID , ReservedAmt ");
		selectSql.append(" From FinExcessAmountReserve ");
		selectSql.append(" Where ReceiptID =:ReceiptID AND ExcessID=:ExcessID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reserve);
		RowMapper<FinExcessAmountReserve> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinExcessAmountReserve.class);

		try {
			reserve = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			reserve = null;
		}

		logger.debug("Leaving");
		return reserve;
	}

	/**
	 * Method for Save Reserved amount against Excess ID
	 */
	@Override
	public void saveExcessReserveLog(long receiptID, long payAgainstID, BigDecimal reserveAmt) {
		logger.debug("Entering");
		
		FinExcessAmountReserve reserve = new FinExcessAmountReserve();
		reserve.setReceiptID(receiptID);
		reserve.setExcessID(payAgainstID);
		reserve.setReservedAmt(reserveAmt);
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinExcessAmountReserve ");
		insertSql.append(" (ExcessID, ReceiptID, ReservedAmt )");
		insertSql.append(" Values(:ExcessID, :ReceiptID, :ReservedAmt)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reserve);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for updating Reserved excess Amount after modifications
	 */
	@SuppressWarnings("serial")
	@Override
	public void updateExcessReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("ExcessID", payAgainstID);
		source.addValue("PaidNow", diffInReserve);

		StringBuilder updateSql = new StringBuilder("Update FinExcessAmountReserve ");
		updateSql.append(" Set ReservedAmt = ReservedAmt + :PaidNow ");
		updateSql.append(" Where ReceiptID =:ReceiptID AND ExcessID =:ExcessID ");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", payAgainstID, PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Deleting Reserved Amounts against Excess ID Processed for Utilization
	 */
	@Override
	public void deleteExcessReserve(long receiptID, long payAgainstID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("ExcessID", payAgainstID);

		StringBuilder updateSql = new StringBuilder("Delete From FinExcessAmountReserve ");
		updateSql.append(" Where ReceiptID =:ReceiptID AND ExcessID =:ExcessID ");

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

@SuppressWarnings("serial")
@Override
	public void updateExcessAmount(long excessID, String amountType, BigDecimal amount) {
		logger.debug("Entering");

		int recordCount = 0;
		FinExcessAmount finExcessAmount = new FinExcessAmount();
		finExcessAmount.setExcessID(excessID);
		finExcessAmount.setAmount(amount);

		StringBuilder updateSql = new StringBuilder("Update FinExcessAmount");
		if ("R".equals(amountType)) {
			updateSql.append(" Set ReservedAmt = ReservedAmt + :amount, BalanceAmt = BalanceAmt - :amount ");
		} else if ("U".equals(amountType)) {
			updateSql.append(" Set ReservedAmt = UtilisedAmt + :amount, BalanceAmt = BalanceAmt - :amount ");
		}
		updateSql.append(" Where ExcessID =:ExcessID");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExcessAmount);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", finExcessAmount.getExcessID(),
					PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
	}
		@Override
	public FinExcessAmount getExcessAmountsByRefAndType(String finReference, String amountType) {
		logger.debug("Entering");
 		FinExcessAmount finExcessAmount = new FinExcessAmount();
		finExcessAmount.setFinReference(finReference);
		finExcessAmount.setAmountType(amountType);
		
		StringBuilder selectSql = new StringBuilder("");
		selectSql.append(" Select ExcessID, AmountType, Amount, UtilisedAmt, ReservedAmt, BalanceAmt From FinExcessAmount");
		selectSql.append(" Where FinReference =:FinReference and AmountType = :AmountType");
		
		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finExcessAmount);
		RowMapper<FinExcessAmount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinExcessAmount.class);

		try {
			finExcessAmount = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finExcessAmount = null;
		}

		logger.debug(Literal.LEAVING);
		return finExcessAmount;
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
