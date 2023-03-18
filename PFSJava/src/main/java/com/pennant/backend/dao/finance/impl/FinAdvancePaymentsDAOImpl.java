/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinAdvancePaymentsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinAdvancePayments model</b> class.<br>
 * 
 */

public class FinAdvancePaymentsDAOImpl extends SequenceDao<FinAdvancePayments> implements FinAdvancePaymentsDAO {
	private static Logger logger = LogManager.getLogger(FinAdvancePaymentsDAOImpl.class);

	public FinAdvancePaymentsDAOImpl() {
		super();
	}

	@Override
	public FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments fap, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where PaymentId = ?");

		FinAdvancePaymentsRowMapper rowMapper = new FinAdvancePaymentsRowMapper(type);

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, fap.getPaymentId());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinAdvancePayments getFinAdvancePaymentsById(long paymentId) {
		StringBuilder sql = getSqlQuery("");
		sql.append(" Where PaymentId = ?");

		FinAdvancePaymentsRowMapper rowMapper = new FinAdvancePaymentsRowMapper("");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, paymentId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(final long finID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ?");

		FinAdvancePaymentsRowMapper rowMapper = new FinAdvancePaymentsRowMapper(type);

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);

		}, rowMapper);
	}

	@Override
	public void delete(FinAdvancePayments fad, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PaymentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, fad.getPaymentId()));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(FinAdvancePayments fap, String type) {
		if (fap.getPaymentId() == Long.MIN_VALUE) {
			fap.setPaymentId(getNextValue("SeqAdvpayment"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (PaymentId, FinID, FinReference, PaymentSeq, DisbSeq, PaymentDetail, AmtToBeReleased");
		sql.append(", LiabilityHoldName, BeneficiaryName, BeneficiaryAccNo, ReEnterBeneficiaryAccNo, Description");
		sql.append(", PaymentType, LlReferenceNo, LlDate, CustContribution, SellerContribution, Remarks");
		sql.append(", BankCode, PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode, PhoneAreaCode");
		sql.append(", PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy, POIssued, PartnerBankID");
		sql.append(", LinkedTranId, TransactionRef, RealizationDate, VasReference, HoldDisbursement, LEI");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fap.getPaymentId());
			ps.setLong(index++, fap.getFinID());
			ps.setString(index++, fap.getFinReference());
			ps.setInt(index++, fap.getPaymentSeq());
			ps.setInt(index++, fap.getDisbSeq());
			ps.setString(index++, fap.getPaymentDetail());
			ps.setBigDecimal(index++, fap.getAmtToBeReleased());
			ps.setString(index++, fap.getLiabilityHoldName());
			ps.setString(index++, fap.getBeneficiaryName());
			ps.setString(index++, fap.getBeneficiaryAccNo());
			ps.setString(index++, fap.getReEnterBeneficiaryAccNo());
			ps.setString(index++, fap.getDescription());
			ps.setString(index++, fap.getPaymentType());
			ps.setString(index++, fap.getLlReferenceNo());
			ps.setDate(index++, JdbcUtil.getDate(fap.getLlDate()));
			ps.setBigDecimal(index++, fap.getCustContribution());
			ps.setBigDecimal(index++, fap.getSellerContribution());
			ps.setString(index++, fap.getRemarks());
			ps.setString(index++, fap.getBankCode());
			ps.setString(index++, fap.getPayableLoc());
			ps.setString(index++, fap.getPrintingLoc());
			ps.setDate(index++, JdbcUtil.getDate(fap.getValueDate()));
			ps.setLong(index++, fap.getBankBranchID());
			ps.setString(index++, fap.getPhoneCountryCode());
			ps.setString(index++, fap.getPhoneAreaCode());
			ps.setString(index++, fap.getPhoneNumber());
			ps.setDate(index++, JdbcUtil.getDate(fap.getClearingDate()));
			ps.setString(index++, fap.getStatus());
			ps.setBoolean(index++, fap.isActive());
			ps.setDate(index++, JdbcUtil.getDate(fap.getInputDate()));
			ps.setString(index++, fap.getDisbCCy());
			ps.setBoolean(index++, fap.ispOIssued());
			ps.setLong(index++, fap.getPartnerBankID());
			ps.setLong(index++, fap.getLinkedTranId());
			ps.setString(index++, fap.getTransactionRef());
			ps.setDate(index++, JdbcUtil.getDate(fap.getRealizationDate()));
			ps.setString(index++, fap.getVasReference());
			ps.setBoolean(index++, fap.isHoldDisbursement());
			ps.setString(index++, fap.getLei());
			ps.setInt(index++, fap.getVersion());
			ps.setLong(index++, fap.getLastMntBy());
			ps.setTimestamp(index++, fap.getLastMntOn());
			ps.setString(index++, fap.getRecordStatus());
			ps.setString(index++, fap.getRoleCode());
			ps.setString(index++, fap.getNextRoleCode());
			ps.setString(index++, fap.getTaskId());
			ps.setString(index++, fap.getNextTaskId());
			ps.setString(index++, fap.getRecordType());
			ps.setLong(index, fap.getWorkflowId());

		});
		return fap.getFinReference();
	}

	@Override
	public void update(FinAdvancePayments fap, String type) {
		StringBuilder sql = new StringBuilder("Update FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PaymentDetail = ?, AmtToBeReleased = ?, LiabilityHoldName = ?");
		sql.append(", BeneficiaryName = ?, DisbSeq = ?, BeneficiaryAccNo = ?, reEnterBeneficiaryAccNo = ?");
		sql.append(", Description = ?, PaymentType = ?, LlReferenceNo = ?, LlDate = ?, CustContribution = ?");
		sql.append(", SellerContribution = ?, Remarks = ?, BankCode = ?, PayableLoc = ?, PrintingLoc = ?");
		sql.append(", ValueDate = ?, BankBranchID = ?, PhoneCountryCode = ?, PhoneAreaCode = ?");
		sql.append(", PhoneNumber = ?, ClearingDate = ?, Status = ?, Active = ?, InputDate = ?");
		sql.append(", DisbCCy = ?, POIssued = ?, PartnerBankID = ?, TransactionRef = ?, RealizationDate = ?");
		sql.append(", VasReference = ?, Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(", HoldDisbursement = ?, LinkedTranId = ?, LEI = ?");
		sql.append("  Where PaymentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fap.getPaymentDetail());
			ps.setBigDecimal(index++, fap.getAmtToBeReleased());
			ps.setString(index++, fap.getLiabilityHoldName());
			ps.setString(index++, fap.getBeneficiaryName());
			ps.setInt(index++, fap.getDisbSeq());
			ps.setString(index++, fap.getBeneficiaryAccNo());
			ps.setString(index++, fap.getReEnterBeneficiaryAccNo());
			ps.setString(index++, fap.getDescription());
			ps.setString(index++, fap.getPaymentType());
			ps.setString(index++, fap.getLlReferenceNo());
			ps.setDate(index++, JdbcUtil.getDate(fap.getLlDate()));
			ps.setBigDecimal(index++, fap.getCustContribution());
			ps.setBigDecimal(index++, fap.getSellerContribution());
			ps.setString(index++, fap.getRemarks());
			ps.setString(index++, fap.getBankCode());
			ps.setString(index++, fap.getPayableLoc());
			ps.setString(index++, fap.getPrintingLoc());
			ps.setDate(index++, JdbcUtil.getDate(fap.getValueDate()));
			ps.setLong(index++, fap.getBankBranchID());
			ps.setString(index++, fap.getPhoneCountryCode());
			ps.setString(index++, fap.getPhoneAreaCode());
			ps.setString(index++, fap.getPhoneNumber());
			ps.setDate(index++, JdbcUtil.getDate(fap.getClearingDate()));
			ps.setString(index++, fap.getStatus());
			ps.setBoolean(index++, fap.isActive());
			ps.setDate(index++, JdbcUtil.getDate(fap.getInputDate()));
			ps.setString(index++, fap.getDisbCCy());
			ps.setBoolean(index++, fap.ispOIssued());
			ps.setLong(index++, fap.getPartnerBankID());
			ps.setString(index++, fap.getTransactionRef());
			ps.setDate(index++, JdbcUtil.getDate(fap.getRealizationDate()));
			ps.setString(index++, fap.getVasReference());
			ps.setInt(index++, fap.getVersion());
			ps.setLong(index++, fap.getLastMntBy());
			ps.setTimestamp(index++, fap.getLastMntOn());
			ps.setString(index++, fap.getRecordStatus());
			ps.setString(index++, fap.getRoleCode());
			ps.setString(index++, fap.getNextRoleCode());
			ps.setString(index++, fap.getTaskId());
			ps.setString(index++, fap.getNextTaskId());
			ps.setString(index++, fap.getRecordType());
			ps.setLong(index++, fap.getWorkflowId());
			ps.setBoolean(index++, fap.isHoldDisbursement());
			ps.setLong(index++, fap.getLinkedTranId());
			ps.setString(index++, fap.getLei());
			ps.setLong(index, fap.getPaymentId());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateStatus(FinAdvancePayments fap, String type) {
		StringBuilder sql = new StringBuilder("Update FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Status = ?");
		sql.append(" Where FinID = ? and DisbSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fap.getStatus());
			ps.setLong(index++, fap.getFinID());
			ps.setLong(index, fap.getDisbSeq());
		});
	}

	@Override
	public void updatePaymentStatus(FinAdvancePayments fap, String type) {
		StringBuilder sql = new StringBuilder("Update FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Status = ?");
		sql.append(" Where FinID = ? and PaymentId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fap.getStatus());
			ps.setLong(index++, fap.getFinID());
			ps.setLong(index, fap.getPaymentId());
		});
	}

	@Override
	public void deleteByFinRef(long finID, String tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, finID));
	}

	@Override
	public int getBranch(long bankBranchID, String type) {
		StringBuilder sql = new StringBuilder("Select Count(PaymentId)");
		sql.append(" From FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankBranchID = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, bankBranchID);
	}

	@Override
	public int getAdvancePaymentsCountByPartnerBank(long partnerBankID, String type) {
		StringBuilder sql = new StringBuilder("Select Count(PaymentId)");
		sql.append(" From FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PartnerBankID = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, partnerBankID);
	}

	@Override
	public void update(long paymentId, long linkedTranId) {
		String sql = "Update FinAdvancePayments Set LinkedTranId = ? Where PaymentId = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, linkedTranId);
			ps.setLong(index, paymentId);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int updateDisbursmentStatus(FinAdvancePayments fap) {
		StringBuilder sql = new StringBuilder("Update FinAdvancePayments");
		sql.append(" Set Status = ?, ClearingDate = ?, TransactionRef = ?, RealizationDate = ?, RejectReason = ?");

		String paymentType = fap.getPaymentType();
		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
			sql.append(", LlReferenceNo = ?, LlDate = ?");
		}

		if (fap.getLastMntOn() != null) {
			sql.append(", LastMntOn = ?");
		}

		sql.append(" Where PaymentId = ? and Status = ?");

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fap.getStatus());
			ps.setDate(index++, JdbcUtil.getDate(fap.getClearingDate()));
			ps.setString(index++, fap.getTransactionRef());
			ps.setDate(index++, JdbcUtil.getDate(fap.getRealizationDate()));
			ps.setString(index++, fap.getRejectReason());

			if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
				ps.setString(index++, fap.getLlReferenceNo());
				ps.setDate(index++, JdbcUtil.getDate(fap.getLlDate()));
			}

			if (fap.getLastMntOn() != null) {
				ps.setDate(index++, JdbcUtil.getDate(fap.getLastMntOn()));
			}

			ps.setLong(index++, fap.getPaymentId());
			ps.setString(index, DisbursementConstants.STATUS_AWAITCON);
		});
	}

	@Override
	public int getBankCode(String bankCode, String type) {
		StringBuilder sql = new StringBuilder("Select Count(PaymentId)");
		sql.append(" From FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankCode = ?");

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, bankCode);
	}

	@Override
	public int getMaxPaymentSeq(long finID) {
		String sql = "Select Max(PaymentSeq) From FinAdvancePayments Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Integer.class, finID);
	}

	@Override
	public int getFinAdvCountByRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select Count(PaymentId)");
		sql.append(" From FinAdvancePayments");
		sql.append(type);
		sql.append(" Where FinID = ?");

		Object[] objects = null;

		if (ImplementationConstants.ALW_QDP_CUSTOMIZATION) {
			sql.append(" and Status  in(?, ?, ?, ?)");

			objects = new Object[] { finID, DisbursementConstants.STATUS_APPROVED,
					DisbursementConstants.STATUS_AWAITCON, DisbursementConstants.STATUS_PAID,
					DisbursementConstants.STATUS_REALIZED };
		} else {
			objects = new Object[] { finID };
		}

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, objects);
	}

	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, String type) {
		StringBuilder sql = new StringBuilder(" Select Count(PaymentId)");
		sql.append(" From FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PartnerBankId = ?");

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, partnerBankId);
	}

	@Override
	public int getCountByFinReference(long finID) {
		String sql = "Select Max(PaymentSeq) From FinAdvancePayments Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID);
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentByFinRef(long finID, Date toDate, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ? and LlDate = ?");

		FinAdvancePaymentsRowMapper rowMapper = new FinAdvancePaymentsRowMapper(type);

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setDate(index, JdbcUtil.getDate(toDate));
		}, rowMapper);
	}

	@Override
	public void updateLinkedTranId(FinAdvancePayments fap) {
		String sql = "Update FinAdvancePayments Set LinkedTranId = ? Where PaymentId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, fap.getLinkedTranId());
			ps.setLong(index, fap.getPaymentId());
		});
	}

	@Override
	public int getCountByPaymentId(long finID, long paymentId) {
		String sql = "Select Count(PaymentId) From FinAdvancePayments Where FinID = ? and PaymentID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, paymentId);
	}

	@Override
	public int getFinAdvanceByVasRef(long finID, String vasReference, String type) {
		StringBuilder sql = new StringBuilder("Select Count(1)");
		sql.append(" From FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and VasReference = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID, vasReference);
	}

	@Override
	public void updateLLDate(FinAdvancePayments fap, String type) {
		StringBuilder sql = new StringBuilder("Update FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set LLDate = ?, Status = ?, LinkedTranId = ?");
		sql.append(" Where FinID = ? and PaymentSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(fap.getLlDate()));
			ps.setString(index++, fap.getStatus());
			ps.setLong(index++, fap.getLinkedTranId());
			ps.setLong(index++, fap.getFinID());
			ps.setLong(index, fap.getPaymentSeq());

		});
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PaymentId, FinID, FinReference, PaymentSeq, DisbSeq, PaymentDetail, AmtToBeReleased");
		sql.append(", LiabilityHoldName, BeneficiaryName, BeneficiaryAccNo, ReEnterBeneficiaryAccNo");
		sql.append(", Description, PaymentType, LlReferenceNo, LlDate, CustContribution, SellerContribution");
		sql.append(", Remarks, BankCode, PayableLoc, PrintingLoc, ValueDate, BankBranchID, PhoneCountryCode");
		sql.append(", PhoneAreaCode, PhoneNumber, ClearingDate, Status, Active, InputDate, DisbCCy");
		sql.append(", VasReference");
		sql.append(", POIssued, PartnerBankID, TransactionRef, RealizationDate, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", LinkedTranId, RecordType, WorkflowId, HoldDisbursement, LEI");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", BranchCode, BranchBankCode, BranchBankName, BranchDesc");
			sql.append(", BankName, City, IFSC, PartnerbankCode, PartnerBankName, PartnerBankAcType");
			sql.append(", PartnerBankAc, PrintingLocDesc, RejectReason");
		}

		sql.append(" From FinAdvancePayments");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinAdvancePaymentsRowMapper implements RowMapper<FinAdvancePayments> {
		private String type;

		private FinAdvancePaymentsRowMapper(String type) {
			this.type = type;
		}

		@Override
		public FinAdvancePayments mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinAdvancePayments fad = new FinAdvancePayments();

			fad.setPaymentId(rs.getLong("PaymentId"));
			fad.setFinID(rs.getLong("FinID"));
			fad.setFinReference(rs.getString("FinReference"));
			fad.setPaymentSeq(rs.getInt("PaymentSeq"));
			fad.setDisbSeq(rs.getInt("DisbSeq"));
			fad.setPaymentDetail(rs.getString("PaymentDetail"));
			fad.setAmtToBeReleased(rs.getBigDecimal("AmtToBeReleased"));
			fad.setLiabilityHoldName(rs.getString("LiabilityHoldName"));
			fad.setBeneficiaryName(rs.getString("BeneficiaryName"));
			fad.setBeneficiaryAccNo(rs.getString("BeneficiaryAccNo"));
			fad.setReEnterBeneficiaryAccNo(rs.getString("ReEnterBeneficiaryAccNo"));
			fad.setDescription(rs.getString("Description"));
			fad.setPaymentType(rs.getString("PaymentType"));
			fad.setLLReferenceNo(rs.getString("LlReferenceNo"));
			fad.setLLDate(rs.getTimestamp("LlDate"));
			fad.setCustContribution(rs.getBigDecimal("CustContribution"));
			fad.setSellerContribution(rs.getBigDecimal("SellerContribution"));
			fad.setRemarks(rs.getString("Remarks"));
			fad.setBankCode(rs.getString("BankCode"));
			fad.setPayableLoc(rs.getString("PayableLoc"));
			fad.setPrintingLoc(rs.getString("PrintingLoc"));
			fad.setValueDate(rs.getTimestamp("ValueDate"));
			fad.setBankBranchID(rs.getLong("BankBranchID"));
			fad.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			fad.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			fad.setPhoneNumber(rs.getString("PhoneNumber"));
			fad.setClearingDate(rs.getTimestamp("ClearingDate"));
			fad.setStatus(rs.getString("Status"));
			fad.setActive(rs.getBoolean("Active"));
			fad.setInputDate(rs.getTimestamp("InputDate"));
			fad.setDisbCCy(rs.getString("DisbCCy"));
			fad.setVasReference(rs.getString("VasReference"));
			fad.setpOIssued(rs.getBoolean("POIssued"));
			fad.setPartnerBankID(rs.getLong("PartnerBankID"));
			fad.setTransactionRef(rs.getString("TransactionRef"));
			fad.setRealizationDate(rs.getTimestamp("RealizationDate"));
			fad.setVersion(rs.getInt("Version"));
			fad.setLastMntBy(rs.getLong("LastMntBy"));
			fad.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fad.setRecordStatus(rs.getString("RecordStatus"));
			fad.setRoleCode(rs.getString("RoleCode"));
			fad.setNextRoleCode(rs.getString("NextRoleCode"));
			fad.setTaskId(rs.getString("TaskId"));
			fad.setNextTaskId(rs.getString("NextTaskId"));
			fad.setLinkedTranId(rs.getLong("LinkedTranId"));
			fad.setRecordType(rs.getString("RecordType"));
			fad.setWorkflowId(rs.getLong("WorkflowId"));
			fad.setHoldDisbursement(rs.getBoolean("HoldDisbursement"));
			fad.setLei(rs.getString("LEI"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fad.setBranchCode(rs.getString("BranchCode"));
				fad.setBranchBankCode(rs.getString("BranchBankCode"));
				fad.setBranchBankName(rs.getString("BranchBankName"));
				fad.setBranchDesc(rs.getString("BranchDesc"));
				fad.setBankName(rs.getString("BankName"));
				fad.setCity(rs.getString("City"));
				fad.setiFSC(rs.getString("IFSC"));
				fad.setPartnerbankCode(rs.getString("PartnerbankCode"));
				fad.setPartnerBankName(rs.getString("PartnerBankName"));
				fad.setPartnerBankAcType(rs.getString("PartnerBankAcType"));
				fad.setPartnerBankAc(rs.getString("PartnerBankAc"));
				fad.setPrintingLocDesc(rs.getString("PrintingLocDesc"));
				fad.setRejectReason(rs.getString("RejectReason"));
			}

			return fad;

		}
	}

	@Override
	public int getStatusCountByFinRefrence(long finID) {
		String sql = "Select Count(FinReference) From FinAdvancePayments_view Where FinID = ? And Status = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, finID, "AC");
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public PaymentInstruction getBeneficiary(long finId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fap.PaymentID, fap.PartnerBankId, fap.PaymentType, pb.PartnerBankCode, pb.PartnerBankName");
		sql.append(", bb.BankBranchId, bb.BankCode, bb.BranchDesc, bd.BankName, bb.IFSC");
		sql.append(", pc.PCCityName, fap.BeneficiaryAccNo, fap.BeneficiaryName");
		sql.append(", fap.PhoneNumber, fap.BeneficiaryAccno, pb.AcType");
		sql.append(", pb.AccountNo, fm.FinType, fm.FinBranch");
		sql.append(" From FinAdvancePayments fap");
		sql.append(" Inner join FinanceMain fm on fm.FInID = fap.FInID");
		sql.append(" Inner join PartnerBanks pb on pb.PartnerBankId = fap.PartnerBankId");
		sql.append(" Inner join BankBranches bb on bb.BankBranchId = fap.BankBranchId");
		sql.append(" Inner join BmtBankDetail bd ON bd.BankCode = bb.BankCode");
		sql.append(" Left join RmtProvinceVsCity pc ON pc.PcCity = bb.City");
		sql.append(" Where fap.Finid = ? and fap.PaymentType IN (?, ?, ?, ?) and fap.PaymentDetail = ?");
		sql.append(" and fap.Status in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<PaymentInstruction> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, finId);
			ps.setString(++index, DisbursementConstants.PAYMENT_TYPE_NEFT);
			ps.setString(++index, DisbursementConstants.PAYMENT_TYPE_RTGS);
			ps.setString(++index, DisbursementConstants.PAYMENT_TYPE_IMPS);
			ps.setString(++index, DisbursementConstants.PAYMENT_TYPE_IFT);
			ps.setString(++index, DisbursementConstants.PAYMENT_DETAIL_CUSTOMER);
			ps.setString(++index, DisbursementConstants.STATUS_PAID);
			ps.setString(++index, DisbursementConstants.STATUS_REALIZED);
		}, (rs, rowNum) -> {
			PaymentInstruction pi = new PaymentInstruction();

			pi.setPaymentId(rs.getLong("PaymentID"));
			pi.setPartnerBankId(rs.getLong("PartnerBankId"));
			pi.setPartnerBankAcType(rs.getString("PaymentType"));
			pi.setPartnerBankCode(rs.getString("PartnerBankCode"));
			pi.setPartnerBankName(rs.getString("PartnerBankName"));
			pi.setBankBranchId(rs.getLong("BankBranchId"));
			pi.setBankBranchCode(rs.getString("BankCode"));
			pi.setBranchDesc(rs.getString("BranchDesc"));
			pi.setBankName(rs.getString("BankName"));
			pi.setBankBranchIFSC(rs.getString("IFSC"));
			pi.setpCCityName(rs.getString("PCCityName"));
			pi.setAccountNo(rs.getString("BeneficiaryAccNo"));
			pi.setAcctHolderName(rs.getString("BeneficiaryName"));
			pi.setPhoneNumber(rs.getString("PhoneNumber"));
			pi.setPartnerBankAc(rs.getString("BeneficiaryAccno"));
			pi.setPartnerBankAcType(rs.getString("AcType"));
			pi.setPartnerBankAc(rs.getString("AccountNo"));
			pi.setFinType(rs.getString("FinType"));
			pi.setFinBranch(rs.getString("FinBranch"));

			return pi;
		});

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		list = list.stream().sorted((l1, l2) -> Long.compare(l2.getPaymentId(), l1.getPaymentId()))
				.collect(Collectors.toList());
		return list.get(0);
	}

	@Override
	public PaymentInstruction getBeneficiaryByPrintLoc(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" bb.BankBranchId, bb.BranchCode, bb.BranchDesc, bb.BankCode, bd.BankName, bb.IFSC, bb.City");
		sql.append(", fm.FinType, fm.FinBranch, c.CustShrtName");
		sql.append(" From RMTBranches b");
		sql.append(" Inner Join BankBranches bb on bb.BranchCode = b.DefChequeDDPrintLoc");
		sql.append(" Inner Join FinanceMain fm on fm.FinBranch = b.BranchCode");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join BmtBankDetail bd ON bd.BankCode = bb.BankCode");
		sql.append(" Where fm.Finid = ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PaymentInstruction pi = new PaymentInstruction();

				pi.setBankBranchId(rs.getLong("BankBranchId"));
				pi.setBankBranchCode(rs.getString("BranchCode"));
				pi.setBranchDesc(rs.getString("BranchDesc"));
				pi.setBankName(rs.getString("BankName"));
				pi.setBankBranchIFSC(rs.getString("IFSC"));
				pi.setpCCityName(rs.getString("City"));
				pi.setPrintingLoc(rs.getString("BankCode"));
				pi.setPrintingLocDesc(rs.getString("BranchDesc"));
				pi.setFinType(rs.getString("FinType"));
				pi.setFinBranch(rs.getString("FinBranch"));
				pi.setFavourName(rs.getString("CustShrtName"));

				return pi;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}
}