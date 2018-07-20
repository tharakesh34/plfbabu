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
 * FileName    		:  PaymentInstructionDAOImpl.java                                                   * 	  
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PaymentInstruction</code> with set
 * of CRUD operations.
 */
public class PaymentInstructionDAOImpl extends SequenceDao<PaymentInstruction> implements PaymentInstructionDAO {
	private static Logger logger = Logger.getLogger(PaymentInstructionDAOImpl.class);

	public PaymentInstructionDAOImpl() {
		super();
	}

	@Override
	public PaymentInstruction getPaymentInstruction(long paymentInstructionId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(
				" paymentInstructionId, paymentId, paymentType, paymentAmount, remarks, partnerBankId, issuingBank, ");
		sql.append(" favourName, favourNumber, payableLoc, printingLoc, valueDate, postDate, status, transactionRef,");
		sql.append(
				" bankBranchId, acctHolderName, accountNo, phoneCountryCode, phoneNumber, clearingdate, active, paymentCCy, ");
		if (type.contains("View")) {
			sql.append(
					" partnerBankCode, partnerBankName, bankBranchIFSC, bankBranchCode, issuingBankName, pCCityName, branchDesc, bankName, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PaymentInstructions");
		sql.append(type);
		sql.append(" Where paymentInstructionId = :paymentInstructionId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PaymentInstruction paymentInstruction = new PaymentInstruction();
		paymentInstruction.setPaymentInstructionId(paymentInstructionId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentInstruction);
		RowMapper<PaymentInstruction> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PaymentInstruction.class);
		try {
			paymentInstruction = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			paymentInstruction = null;
		}
		logger.debug(Literal.LEAVING);
		return paymentInstruction;
	}

	@Override
	public PaymentInstruction getPaymentInstructionDetails(long paymentId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append(
				" SELECT  paymentInstructionId, paymentId, paymentType, paymentAmount, remarks, partnerBankId, issuingBank,");
		sql.append(" favourName, favourNumber, payableLoc, printingLoc, valueDate, postDate, ");
		sql.append(
				" bankBranchId, acctHolderName, accountNo, phoneCountryCode, phoneNumber, clearingdate, status, transactionRef, ");
		sql.append(" active, paymentCCy, ");
		if (type.contains("View")) {
			sql.append(
					" partnerBankCode, partnerBankName, bankBranchIFSC, bankBranchCode, issuingBankName, pCCityName, ");
			sql.append(" branchDesc, bankName, partnerBankAc, partnerBankAcType, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PaymentInstructions");
		sql.append(type);
		sql.append(" Where paymentId = :paymentId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PaymentInstruction paymentInstruction = new PaymentInstruction();
		paymentInstruction.setPaymentId(paymentId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentInstruction);
		RowMapper<PaymentInstruction> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PaymentInstruction.class);
		try {
			paymentInstruction = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			paymentInstruction = null;
		}
		logger.debug(Literal.LEAVING);
		return paymentInstruction;
	}

	@Override
	public String save(PaymentInstruction paymentInstruction, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into PaymentInstructions");
		sql.append(tableType.getSuffix());
		sql.append(
				" (paymentInstructionId, paymentId, paymentType, paymentAmount, remarks, partnerBankId, issuingBank,");
		sql.append(" favourName, favourNumber, payableLoc, printingLoc, valueDate, postDate, ");
		sql.append(" bankBranchId, acctHolderName, accountNo, phoneCountryCode, phoneNumber, clearingdate, status,");
		sql.append(" active, paymentCCy, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(
				" :paymentInstructionId, :paymentId, :paymentType, :paymentAmount, :remarks, :partnerBankId, :issuingBank,");
		sql.append(" :favourName, :favourNumber, :payableLoc, :printingLoc, :valueDate, :postDate, ");
		sql.append(
				" :bankBranchId, :acctHolderName, :accountNo, :phoneCountryCode, :phoneNumber, :clearingdate, :status,");
		sql.append(" :active, :paymentCCy, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the sequence number.
		if (paymentInstruction.getPaymentInstructionId() <= 0) {
			paymentInstruction.setPaymentInstructionId(getNextValue("SeqAdvpayment"));
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentInstruction);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(paymentInstruction.getPaymentInstructionId());
	}

	@Override
	public void update(PaymentInstruction paymentInstruction, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PaymentInstructions");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set paymentId = :paymentId, paymentType = :paymentType, paymentAmount = :paymentAmount, issuingBank = :issuingBank,");
		sql.append(" remarks = :remarks, partnerBankId = :partnerBankId, favourName = :favourName, ");
		sql.append(" favourNumber = :favourNumber, payableLoc = :payableLoc, printingLoc = :printingLoc, ");
		sql.append(" valueDate = :valueDate, postDate = :postDate, bankBranchId = :bankBranchId, ");
		sql.append(" acctHolderName = :acctHolderName, accountNo = :accountNo, phoneCountryCode = :phoneCountryCode, ");
		sql.append(" phoneNumber = :phoneNumber, clearingdate = :clearingdate, active = :active, ");
		sql.append(" paymentCCy = :paymentCCy, status = :status,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where paymentInstructionId = :paymentInstructionId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentInstruction);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PaymentInstruction paymentInstruction, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PaymentInstructions");
		sql.append(tableType.getSuffix());
		sql.append(" where paymentId = :paymentId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentInstruction);
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
	public boolean isDuplicateKey(long paymentInstructionId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "PaymentInstructionId = :PaymentInstructionId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PaymentInstructions", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PaymentInstructions_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PaymentInstructions_Temp", "PaymentInstructions" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("PaymentInstructionId", paymentInstructionId);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);
		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public void updatePaymentInstrucionStatus(PaymentInstruction paymentInstruction, TableType mainTab) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Update PAYMENTINSTRUCTIONS");
		sql.append(" Set STATUS = :STATUS, CLEARINGDATE = :CLEARINGDATE, TRANSACTIONREF = :TRANSACTIONREF,");
		sql.append("  REJECTREASON = :REJECTREASON Where PAYMENTINSTRUCTIONID = :PAYMENTINSTRUCTIONID");

		paramMap.addValue("STATUS", paymentInstruction.getStatus());
		paramMap.addValue("CLEARINGDATE", paymentInstruction.getClearingDate());
		paramMap.addValue("TRANSACTIONREF", paymentInstruction.getTransactionRef());
		paramMap.addValue("REJECTREASON", paymentInstruction.getRejectReason());
		paramMap.addValue("PAYMENTINSTRUCTIONID", paymentInstruction.getPaymentInstructionId());

		logger.debug(Literal.SQL + sql);
		this.jdbcTemplate.update(sql.toString(), paramMap);
	}

	/**
	 * Method for Fetching Count for Assigned partnerBankId to Different
	 * Finances/Commitments
	 */
	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, String type) {
		logger.debug("Entering");

		int assignedCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PartnerBankId", partnerBankId);

		StringBuilder selectSql = new StringBuilder(" Select Count(1) ");
		selectSql.append(" From PaymentInstructions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PartnerBankId = :PartnerBankId ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			assignedCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			assignedCount = 0;
		}
		logger.debug("Leaving");
		return assignedCount;
	}
}
