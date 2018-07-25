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
 * FileName    		:  PaymentHeaderDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2017    														*
 *                                                                  						*
 * Modified Date    :  27-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.payment.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PaymentHeader</code> with set of CRUD operations.
 */
public class PaymentHeaderDAOImpl extends SequenceDao<PaymentHeader> implements PaymentHeaderDAO {
	private static Logger logger = Logger.getLogger(PaymentHeaderDAOImpl.class);

	
	public PaymentHeaderDAOImpl() {
		super();
	}

	@Override
	public PaymentHeader getPaymentHeader(long paymentId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" paymentId, paymentType, paymentAmount, createdOn, approvedOn, status, finReference,");
		if (type.contains("View")) {
			sql.append("paymentType,status,");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PaymentHeader");
		sql.append(type);
		sql.append(" Where paymentId = :paymentId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PaymentHeader paymentHeader = new PaymentHeader();
		paymentHeader.setPaymentId(paymentId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentHeader);
		RowMapper<PaymentHeader> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PaymentHeader.class);

		try {
			paymentHeader = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			paymentHeader = null;
		}

		logger.debug(Literal.LEAVING);
		return paymentHeader;
	}

	@Override
	public String save(PaymentHeader paymentHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into PaymentHeader");
		sql.append(tableType.getSuffix());
		sql.append("(paymentId, paymentType, paymentAmount, createdOn, approvedOn, status, finReference, linkedTranId,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :paymentId, :paymentType, :paymentAmount, :createdOn, :approvedOn, :status, :finReference, :linkedTranId,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		// Get the sequence number.
		if (paymentHeader.getPaymentId() <= 0) {
			paymentHeader.setPaymentId(getNextId("SeqPaymentHeader"));
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentHeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(paymentHeader.getPaymentId());
	}

	@Override
	public void update(PaymentHeader paymentHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PaymentHeader");
		sql.append(tableType.getSuffix());
		sql.append(" set paymentType = :paymentType, paymentAmount = :paymentAmount, createdOn = :createdOn, ");
		sql.append(" approvedOn = :approvedOn, status = :status, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where paymentId = :paymentId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PaymentHeader paymentHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PaymentHeader");
		sql.append(tableType.getSuffix());
		sql.append(" where paymentId = :paymentId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentHeader);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}


	@Override
	public boolean isDuplicateKey(long paymentId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "PaymentId = :PaymentId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PaymentHeader", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PaymentHeader_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PaymentHeader_Temp", "PaymentHeader" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("PaymentId", paymentId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public FinanceMain getFinanceDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();
		sql.append("  SELECT FM.FinReference, FT.FinType, FT.FINTYPEDESC LovDescFinTypeName, FT.FinDivision FinPurpose, FM.CalRoundingMode, FM.RoundingTarget,");
		sql.append("  FM.FinBranch,FM.CustId, CU.CUSTCIF LovDescCustCif, CU.CUSTSHRTNAME LovDescCustShrtName, CURR.CCYCODE finCcy, ");
		sql.append("  FM.FINSTARTDATE, FM.MATURITYDATE,DIV.EntityCode LOVDESCENTITYCODE  FROM FINANCEMAIN FM");
		sql.append(" INNER JOIN CUSTOMERS CU ON CU.CUSTID = FM.CUSTID");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN RMTCURRENCIES CURR ON CURR.CCYCODE = FM.FINCCY");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL DIV ON DIV.DIVISIONCODE = FT.FINDIVISION");
		sql.append(" Where FinReference = :FinReference");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<FinExcessAmount> getfinExcessAmount(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" select excessID, finReference, amountType, amount, balanceAmt, reservedAmt");
		sql.append("  from finexcessamount Where FinReference = :FinReference");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		RowMapper<FinExcessAmount> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinExcessAmount.class);
		try {
			return jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<ManualAdvise> getManualAdvise(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" select MA.adviseID, MA.finReference, MA.balanceAmt, MA.adviseType, MA.adviseAmount, MA.reservedAmt, ");
		sql.append("ft.feetypecode,ft.FEETYPEDESC, FT.TaxApplicable, FT.TaxComponent from MANUALADVISE MA inner join FEETYPES ft on MA.FEETYPEID=ft.FEETYPEID ");
		sql.append("Where FinReference = :FinReference AND MA.AdviseType = :AdviseType order by MA.VALUEDATE  ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("AdviseType", 2);

		RowMapper<ManualAdvise> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);
		try {
			return jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}
}
