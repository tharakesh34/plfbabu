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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PaymentInstruction</code> with set of CRUD operations.
 */
public class PaymentInstructionDAOImpl extends SequenceDao<PaymentInstruction> implements PaymentInstructionDAO {
	private static Logger logger = LogManager.getLogger(PaymentInstructionDAOImpl.class);

	public PaymentInstructionDAOImpl() {
		super();
	}

	@Override
	public PaymentInstruction getPaymentInstruction(long paymentInstructionId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PaymentInstructionId = ?");

		logger.trace(Literal.SQL + sql.toString());

		PaymentInstructionRowMapper rowMapper = new PaymentInstructionRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { paymentInstructionId }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PaymentInstructionId, PaymentId, PaymentType, PaymentAmount, Remarks, PartnerBankId");
		sql.append(", IssuingBank, FavourName, FavourNumber, PayableLoc, PrintingLoc, ValueDate, PostDate");
		sql.append(", Status, RejectReason, TransactionRef, BankBranchId, AcctHolderName, AccountNo, PhoneCountryCode");
		sql.append(", PhoneNumber, ClearingDate, Active, PaymentCCy, RejectReason");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", PartnerBankCode, PartnerBankName, BankBranchIFSC, BankBranchCode");
			sql.append(", IssuingBankName, PCCityName, BranchDesc, BankName, BranchBankCode");
			sql.append(", PartnerBankAc, PartnerBankAcType");
		}

		sql.append(" from PaymentInstructions");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	@Override
	public PaymentInstruction getPaymentInstructionDetails(long paymentId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PaymentId = ?");

		logger.trace(Literal.SQL + sql.toString());

		PaymentInstructionRowMapper rowMapper = new PaymentInstructionRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { paymentId }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
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
				" :bankBranchId, :acctHolderName, :accountNo, :phoneCountryCode, :phoneNumber, :clearingDate, :status,");
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
		sql.append(" set paymentId = :paymentId, paymentType = :paymentType, paymentAmount = :paymentAmount");
		sql.append(", issuingBank = :issuingBank, remarks = :remarks, partnerBankId = :partnerBankId");
		sql.append(", favourName = :favourName, favourNumber = :favourNumber");
		sql.append(", payableLoc = :payableLoc, printingLoc = :printingLoc");
		sql.append(", valueDate = :valueDate, postDate = :postDate, bankBranchId = :bankBranchId");
		sql.append(", AcctHolderName = :acctHolderName, accountNo = :accountNo");
		sql.append(", phoneCountryCode = :phoneCountryCode, phoneNumber = :phoneNumber, clearingdate = :clearingDate");
		sql.append(", active = :active, paymentCCy = :paymentCCy, status = :status");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
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
	public int updatePaymentInstrucionStatus(PaymentInstruction paymentInstruction, TableType mainTab) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Update PAYMENTINSTRUCTIONS");
		sql.append(" Set STATUS = :STATUS, CLEARINGDATE = :CLEARINGDATE, TRANSACTIONREF = :TRANSACTIONREF,");
		sql.append(" REJECTREASON = :REJECTREASON");
		sql.append(" Where PAYMENTINSTRUCTIONID = :PAYMENTINSTRUCTIONID AND STATUS = :OLDSTATUS");

		paramMap.addValue("STATUS", paymentInstruction.getStatus());
		paramMap.addValue("CLEARINGDATE", paymentInstruction.getClearingDate());
		paramMap.addValue("TRANSACTIONREF", paymentInstruction.getTransactionRef());
		paramMap.addValue("REJECTREASON", paymentInstruction.getRejectReason());
		paramMap.addValue("PAYMENTINSTRUCTIONID", paymentInstruction.getPaymentInstructionId());
		paramMap.addValue("OLDSTATUS", DisbursementConstants.STATUS_AWAITCON);

		logger.debug(Literal.SQL + sql);
		return this.jdbcTemplate.update(sql.toString(), paramMap);
	}

	/**
	 * Method for Fetching Count for Assigned partnerBankId to Different Finances/Commitments
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

	@Override
	public void updateStatus(PaymentInstruction instruction, String tableType) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update PAYMENTINSTRUCTIONS");
		updateSql.append(StringUtils.trimToEmpty(tableType));
		updateSql.append("  Set Status = :Status");
		updateSql.append("  Where PaymentInstructionId = :PaymentInstructionId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(instruction);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	private class PaymentInstructionRowMapper implements RowMapper<PaymentInstruction> {
		private String type;

		PaymentInstructionRowMapper(String type) {
			this.type = type;
		}

		@Override
		public PaymentInstruction mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentInstruction fpd = new PaymentInstruction();

			fpd.setPaymentInstructionId(rs.getLong("PaymentInstructionId"));
			fpd.setPaymentId(rs.getLong("PaymentId"));
			fpd.setPaymentType(rs.getString("PaymentType"));
			fpd.setPaymentAmount(rs.getBigDecimal("PaymentAmount"));
			fpd.setRemarks(rs.getString("Remarks"));
			fpd.setPartnerBankId(rs.getLong("PartnerBankId"));
			fpd.setIssuingBank(rs.getString("IssuingBank"));
			fpd.setFavourName(rs.getString("FavourName"));
			fpd.setFavourNumber(rs.getString("FavourNumber"));
			fpd.setPayableLoc(rs.getString("PayableLoc"));
			fpd.setPrintingLoc(rs.getString("PrintingLoc"));
			fpd.setValueDate(rs.getTimestamp("ValueDate"));
			fpd.setPostDate(rs.getTimestamp("PostDate"));
			fpd.setStatus(rs.getString("Status"));
			fpd.setRejectReason(rs.getString("RejectReason"));
			fpd.setTransactionRef(rs.getString("TransactionRef"));
			fpd.setBankBranchId(rs.getLong("BankBranchId"));
			fpd.setAcctHolderName(rs.getString("AcctHolderName"));
			fpd.setAccountNo(rs.getString("AccountNo"));
			fpd.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			fpd.setPhoneNumber(rs.getString("PhoneNumber"));
			fpd.setClearingDate(rs.getTimestamp("ClearingDate"));
			fpd.setActive(rs.getBoolean("Active"));
			fpd.setPaymentCCy(rs.getString("PaymentCCy"));
			fpd.setVersion(rs.getInt("Version"));
			fpd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fpd.setLastMntBy(rs.getLong("LastMntBy"));
			fpd.setRecordStatus(rs.getString("RecordStatus"));
			fpd.setRoleCode(rs.getString("RoleCode"));
			fpd.setNextRoleCode(rs.getString("NextRoleCode"));
			fpd.setTaskId(rs.getString("TaskId"));
			fpd.setNextTaskId(rs.getString("NextTaskId"));
			fpd.setRecordType(rs.getString("RecordType"));
			fpd.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fpd.setPartnerBankCode(rs.getString("PartnerBankCode"));
				fpd.setPartnerBankName(rs.getString("PartnerBankName"));
				fpd.setBankBranchIFSC(rs.getString("BankBranchIFSC"));
				fpd.setBankBranchCode(rs.getString("BankBranchCode"));
				fpd.setIssuingBankName(rs.getString("IssuingBankName"));
				fpd.setpCCityName(rs.getString("PCCityName"));
				fpd.setBranchDesc(rs.getString("BranchDesc"));
				fpd.setBankName(rs.getString("BankName"));
				fpd.setPartnerBankAc(rs.getString("PartnerBankAc"));
				fpd.setPartnerBankAcType(rs.getString("PartnerBankAcType"));
				fpd.setBranchBankCode(rs.getString("BranchBankCode"));
			}

			return fpd;
		}

	}

	@Override
	public long getPymntsCustId(long paymentId) {
		StringBuilder sql = new StringBuilder("Select c.custid from customers c");
		sql.append(" inner join financemain fm on fm.custid = c.custid");
		sql.append(" inner join  paymentheader ph on ph.finreference = fm.finreference");
		sql.append(" where ph.paymentid = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { paymentId }, Long.class);
		} catch (Exception e) {
			logger.warn("Record is not found in PaymentHeader for the specified PaymentId >> {}", paymentId);
			throw new InterfaceException(Literal.EXCEPTION, e.getMessage());
		}
	}

	@Override
	public boolean isInstructionInProgress(String finReference) {
		logger.debug(Literal.ENTERING);
		boolean exists = false;
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select ");
		sql.append(" count(*) from PAYMENTINSTRUCTIONS_TEMP pi");
		sql.append(" left join PAYMENTHEADER_temp ph on ph.PAYMENTID = pi.PAYMENTID");
		sql.append(" left join PAYMENTDETAILS_temp pd on pd.PAYMENTID = ph.PAYMENTID");
		sql.append(" where ph.finReference =:finReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("finReference", finReference);
		Integer count = jdbcTemplate.queryForObject(sql.toString(), paramSource, Integer.class);
		if (count > 0) {
			exists = true;
		}
		logger.debug(Literal.LEAVING);
		return exists;

	}
}
