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
 * * FileName : FinFeeDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified
 * Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * DAO methods implementation for the <b>FinFeeDetail model</b> class.<br>
 * 
 */

public class FinFeeDetailDAOImpl extends SequenceDao<FinFeeDetail> implements FinFeeDetailDAO {
	private static Logger logger = LogManager.getLogger(FinFeeDetailDAOImpl.class);

	public FinFeeDetailDAOImpl() {
		super();
	}

	@Override
	public FinFeeDetail getFinFeeDetailById(FinFeeDetail fd, boolean isWIF, String type) {
		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FeeID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		long feeID = fd.getFeeID();

		if (feeID == Long.MIN_VALUE) {
			logger.warn("Record not found with the below parameters.FeeID: {}", feeID);
			return null;
		}

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, feeID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(long finID, boolean isWIF, String type) {
		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, rowMapper);
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(String reference, boolean isWIF, String type) {
		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, reference);
		}, rowMapper);
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(final long finID, boolean isWIF, String type, String finEvent) {
		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinID = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index, finEvent);
		}, rowMapper);
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByReferenceId(long referenceId, String finEvent, String type) {
		StringBuilder sql = getSelectQuery(false, type);
		sql.append(" Where ReferenceId = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, false);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, referenceId);
			ps.setString(index, finEvent);
		}, rowMapper);
	}

	@Override
	public List<FinFeeDetail> getFinScheduleFees(final long finID, boolean isWIF, String type) {
		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where FinID = ? and FeeScheduleMethod IN (?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index++, "STFI");
			ps.setString(index++, "STNI");
			ps.setString(index++, "STET");
			ps.setString(index, "POSP");
		}, rowMapper);
	}

	@Override
	public List<FinFeeDetail> getPaidFinFeeDetails(String reference, String type) {
		StringBuilder sql = getSelectQuery(false, type);
		sql.append(" Where FinReference = ? and ActualAmount > ?");
		sql.append(" and FeeScheduleMethod in (?, ?) and OriginationFee = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, false);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, reference);
			ps.setInt(index++, 0);
			ps.setString(index++, "DISB");
			ps.setString(index++, "PBCU");
			ps.setInt(index, 1);
		}, rowMapper);

	}

	@Override
	public void refresh(FinFeeDetail finFeeDetail) {

	}

	@Override
	public void delete(FinFeeDetail fd, boolean isWIF, String type) {
		StringBuilder sql = new StringBuilder();

		if (isWIF) {
			sql.append("Delete From WIFFinFeeDetail");
		} else {
			sql.append("Delete From FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and OriginationFee = ? and FinEvent = ? and FeeTypeID = ? and FeeID = ?");

		if (StringUtils.isNotBlank(fd.getVasReference())) {
			sql.append(" and VasReference = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fd.getFinID());
				ps.setBoolean(index++, fd.isOriginationFee());
				ps.setString(index++, fd.getFinEvent());
				ps.setLong(index++, fd.getFeeTypeID());
				ps.setLong(index++, fd.getFeeID());

				if (StringUtils.isNotBlank(fd.getVasReference())) {
					ps.setString(index, fd.getVasReference());
				}
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public long save(FinFeeDetail fe, boolean isWIF, String type) {
		if (fe.getFeeID() == Long.MIN_VALUE) {
			fe.setFeeID(getNextValue("SeqFinFeeDetail"));
		}

		fe.setPostDate(SysParamUtil.getAppDate());

		StringBuilder sql = new StringBuilder("Insert into");

		if (isWIF) {
			sql.append(" WIFFinFeeDetail");
		} else {
			sql.append(" FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FeeID, FinID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder");
		sql.append(", CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount, FeeScheduleMethod, Terms");
		sql.append(", RemainingFee, PaymentRef, CalculationType, VasReference, Status, RuleCode, FixedAmount");
		sql.append(", Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd");
		sql.append(", PostDate, Refundable, PaidAmountOriginal, PaidAmountGST, NetAmountOriginal, NetAmountGST");
		sql.append(", NetAmount, RemainingFeeOriginal, RemainingFeeGST, TaxApplicable, TaxComponent");
		sql.append(", ActualAmountOriginal, ActualAmountGST, TransactionId, InstructionUID, NetTDS, PaidTDS, RemTDS");

		if (!isWIF) {
			sql.append(", ActPercentage, TaxPercent");
		}

		sql.append(", WaivedGST, ReferenceId, TaxHeaderId, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");

		if (!isWIF) {
			sql.append(", ?, ?");
		}

		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fe.getFeeID());
			ps.setLong(index++, fe.getFinID());
			ps.setString(index++, fe.getFinReference());
			ps.setBoolean(index++, fe.isOriginationFee());
			ps.setString(index++, fe.getFinEvent());
			ps.setLong(index++, fe.getFeeTypeID());
			ps.setInt(index++, fe.getFeeSeq());
			ps.setInt(index++, fe.getFeeOrder());
			ps.setBigDecimal(index++, fe.getCalculatedAmount());
			ps.setBigDecimal(index++, fe.getActualAmount());
			ps.setBigDecimal(index++, fe.getWaivedAmount());
			ps.setBigDecimal(index++, fe.getPaidAmount());
			ps.setString(index++, fe.getFeeScheduleMethod());
			ps.setInt(index++, fe.getTerms());
			ps.setBigDecimal(index++, fe.getRemainingFee());
			ps.setString(index++, fe.getPaymentRef());
			ps.setString(index++, fe.getCalculationType());
			ps.setString(index++, fe.getVasReference());
			ps.setString(index++, fe.getStatus());
			ps.setString(index++, fe.getRuleCode());
			ps.setBigDecimal(index++, fe.getFixedAmount());
			ps.setBigDecimal(index++, fe.getPercentage());
			ps.setString(index++, fe.getCalculateOn());
			ps.setBoolean(index++, fe.isAlwDeviation());
			ps.setBigDecimal(index++, fe.getMaxWaiverPerc());
			ps.setBoolean(index++, fe.isAlwModifyFee());
			ps.setBoolean(index++, fe.isAlwModifyFeeSchdMthd());
			ps.setDate(index++, JdbcUtil.getDate(fe.getPostDate()));
			ps.setBoolean(index++, fe.isRefundable());
			ps.setBigDecimal(index++, fe.getPaidAmountOriginal());
			ps.setBigDecimal(index++, fe.getPaidAmountGST());
			ps.setBigDecimal(index++, fe.getNetAmountOriginal());
			ps.setBigDecimal(index++, fe.getNetAmountGST());
			ps.setBigDecimal(index++, fe.getNetAmount());
			ps.setBigDecimal(index++, fe.getRemainingFeeOriginal());
			ps.setBigDecimal(index++, fe.getRemainingFeeGST());
			ps.setBoolean(index++, fe.isTaxApplicable());
			ps.setString(index++, fe.getTaxComponent());
			ps.setBigDecimal(index++, fe.getActualAmountOriginal());
			ps.setBigDecimal(index++, fe.getActualAmountGST());
			ps.setString(index++, fe.getTransactionId());
			ps.setObject(index++, fe.getInstructionUID());
			ps.setBigDecimal(index++, fe.getNetTDS());
			ps.setBigDecimal(index++, fe.getPaidTDS());
			ps.setBigDecimal(index++, fe.getRemTDS());

			if (!isWIF) {
				ps.setBigDecimal(index++, fe.getActPercentage());
				ps.setBigDecimal(index++, fe.getTaxPercent());
			}

			ps.setBigDecimal(index++, fe.getWaivedGST());
			ps.setLong(index++, fe.getReferenceId());
			ps.setObject(index++, fe.getTaxHeaderId());
			ps.setInt(index++, fe.getVersion());
			ps.setLong(index++, fe.getLastMntBy());
			ps.setTimestamp(index++, fe.getLastMntOn());
			ps.setString(index++, fe.getRecordStatus());
			ps.setString(index++, fe.getRoleCode());
			ps.setString(index++, fe.getNextRoleCode());
			ps.setString(index++, fe.getTaskId());
			ps.setString(index++, fe.getNextTaskId());
			ps.setString(index++, fe.getRecordType());
			ps.setLong(index, fe.getWorkflowId());
		});

		return fe.getFeeID();
	}

	@Override
	public void update(FinFeeDetail fe, boolean isWIF, String type) {
		StringBuilder sql = new StringBuilder();

		if (isWIF) {
			sql.append("Update WIFFinFeeDetail");
		} else {
			sql.append("Update FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinID =  ?, FinReference = ?, FeeSeq = ?, FeeOrder = ?, CalculatedAmount = ?");
		sql.append(", ActualAmount = ?, WaivedAmount = ?, PaidAmount = ?, FeeScheduleMethod = ?");
		sql.append(", Terms = ?, RemainingFee = ?, PaymentRef = ?, CalculationType = ?, VasReference = ?");
		sql.append(", RuleCode = ?, Status = ?, FixedAmount = ?, Percentage = ?, CalculateOn = ?, AlwDeviation = ?");
		sql.append(", MaxWaiverPerc = ?, AlwModifyFee = ?, AlwModifyFeeSchdMthd = ?, Refundable = ?");
		sql.append(", PaidAmountOriginal = ?, PaidAmountGST = ?, NetAmountOriginal = ?, NetAmountGST = ?");
		sql.append(", NetAmount = ?, RemainingFeeOriginal = ?, RemainingFeeGST = ?, TaxApplicable = ?");
		sql.append(", TaxComponent = ?, ActualAmountOriginal = ?, ActualAmountGST = ?,  NetTDS = ?");
		sql.append(", PaidTDS = ?, RemTDS = ?, InstructionUID = ?");
		if (!isWIF) {
			sql.append(", ActPercentage = ?, TaxPercent = ?");
		}
		sql.append(", WaivedGST = ?, ReferenceId = ?, TaxHeaderId = ?, Version = ?, LastMntBy = ?");
		sql.append(", LastMntOn = ?, RecordStatus= ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append("  Where FeeID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fe.getFinID());
			ps.setString(index++, fe.getFinReference());
			ps.setInt(index++, fe.getFeeSeq());
			ps.setInt(index++, fe.getFeeOrder());
			ps.setBigDecimal(index++, fe.getCalculatedAmount());
			ps.setBigDecimal(index++, fe.getActualAmount());
			ps.setBigDecimal(index++, fe.getWaivedAmount());
			ps.setBigDecimal(index++, fe.getPaidAmount());
			ps.setString(index++, fe.getFeeScheduleMethod());
			ps.setInt(index++, fe.getTerms());
			ps.setBigDecimal(index++, fe.getRemainingFee());
			ps.setString(index++, fe.getPaymentRef());
			ps.setString(index++, fe.getCalculationType());
			ps.setString(index++, fe.getVasReference());
			ps.setString(index++, fe.getRuleCode());
			ps.setString(index++, fe.getStatus());
			ps.setBigDecimal(index++, fe.getFixedAmount());
			ps.setBigDecimal(index++, fe.getPercentage());
			ps.setString(index++, fe.getCalculateOn());
			ps.setBoolean(index++, fe.isAlwDeviation());
			ps.setBigDecimal(index++, fe.getMaxWaiverPerc());
			ps.setBoolean(index++, fe.isAlwModifyFee());
			ps.setBoolean(index++, fe.isAlwModifyFeeSchdMthd());
			ps.setBoolean(index++, fe.isRefundable());
			ps.setBigDecimal(index++, fe.getPaidAmountOriginal());
			ps.setBigDecimal(index++, fe.getPaidAmountGST());
			ps.setBigDecimal(index++, fe.getNetAmountOriginal());
			ps.setBigDecimal(index++, fe.getNetAmountGST());
			ps.setBigDecimal(index++, fe.getNetAmount());
			ps.setBigDecimal(index++, fe.getRemainingFeeOriginal());
			ps.setBigDecimal(index++, fe.getRemainingFeeGST());
			ps.setBoolean(index++, fe.isTaxApplicable());
			ps.setString(index++, fe.getTaxComponent());
			ps.setBigDecimal(index++, fe.getActualAmountOriginal());
			ps.setBigDecimal(index++, fe.getActualAmountGST());
			ps.setBigDecimal(index++, fe.getNetTDS());
			ps.setBigDecimal(index++, fe.getPaidTDS());
			ps.setBigDecimal(index++, fe.getRemTDS());
			ps.setObject(index++, fe.getInstructionUID());

			if (!isWIF) {
				ps.setBigDecimal(index++, fe.getActPercentage());
				ps.setBigDecimal(index++, fe.getTaxPercent());
			}

			ps.setBigDecimal(index++, fe.getWaivedGST());
			ps.setLong(index++, fe.getReferenceId());
			ps.setObject(index++, fe.getTaxHeaderId());
			ps.setInt(index++, fe.getVersion());
			ps.setLong(index++, fe.getLastMntBy());
			ps.setTimestamp(index++, fe.getLastMntOn());
			ps.setString(index++, fe.getRecordStatus());
			ps.setString(index++, fe.getRoleCode());
			ps.setString(index++, fe.getNextRoleCode());
			ps.setString(index++, fe.getTaskId());
			ps.setString(index++, fe.getNextTaskId());
			ps.setString(index++, fe.getRecordType());
			ps.setLong(index++, fe.getWorkflowId());
			ps.setLong(index, fe.getFeeID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void statusUpdate(long feeID, String status, boolean isWIF, String type) {
		StringBuilder sql = new StringBuilder();

		if (isWIF) {
			sql.append("Update WIFFinFeeDetail");
		} else {
			sql.append("Update FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Status = ? Where FeeID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, status);
			ps.setLong(index, feeID);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public int getFeeSeq(FinFeeDetail fd, boolean isWIF, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Coalesce(Max(FeeSeq), 0)");

		if (isWIF) {
			sql.append(" From WIFFinFeeDetail");
		} else {
			sql.append(" From FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
			return rs.getInt(1);
		}, fd.getFinReference(), fd.getFinEvent());
	}

	@Override
	public FinFeeDetail getVasFeeDetailById(String vasReference, boolean isWIF, String type) {
		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where VasReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, vasReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateTaxPercent(UploadTaxPercent tp) {
		String sql = "Update FinFeeDetail Set TaxPercent = ? Where FinID = ? and FeeTypeId = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, tp.getTaxPercent());
			ps.setLong(index++, tp.getFinID());
			ps.setLong(index, tp.getFeeTypeId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public long getFinFeeTypeIdByFeeType(String feeTypeCode, long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeTypeID From FinFeeDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FeeTypeCode = ? and FinID = ? and FinEvent != ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Long.class, feeTypeCode, finID,
					AccountingEvent.VAS_FEE);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return Long.MIN_VALUE;
		}
	}

	@Override
	public List<FinFeeDetail> getFeeDetailByExtReference(String extReference, long feeTypeId, String tableType) {
		StringBuilder sql = getSelectQuery(false, tableType);
		sql.append(" Where TransactionId = ? and FeeTypeID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, extReference);
			ps.setLong(index, feeTypeId);
		}, new FinFeeDetailsRowMapper(tableType, false));
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailsByTran(String reference, boolean isWIF, String type) {
		StringBuilder sql = getSelectQuery(isWIF, type);
		sql.append(" Where TransactionId = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper(type, isWIF);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index, reference);
		}, rowMapper);
	}

	@Override
	public List<FinFeeDetail> getDMFinFeeDetailByFinRef(long finID, String type) {
		return getFinFeeDetailByFinRef(finID, false, type);
	}

	@Override
	public boolean isFinTypeFeeExists(long feeTypeId, String finType, int moduleId, boolean originationFee) {
		StringBuilder sql = new StringBuilder("Select count(FeeTypeId) From (");
		sql.append(" Select FeeTypeId From FinTypeFees Where FeeTypeId  = ? and FinType = ?");
		sql.append(" and ModuleId = ? and OriginationFee = ?");
		sql.append(" Union all");
		sql.append(" Select FeeTypeId From FinTypeFees_temp Where FeeTypeId  = ? and FinType = ?");
		sql.append(" and ModuleId = ? and OriginationFee = ?");
		sql.append(" ) T");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, feeTypeId, finType, moduleId,
					originationFee, feeTypeId, finType, moduleId, originationFee) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public List<FinFeeDetail> getPreviousAdvPayments(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ft.FeeTypeId, FeeTypeCode, sum(CalculatedAmount) CalculatedAmount");
		sql.append(" From FinFeeDetail fd");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = fd.FeeTypeID");
		sql.append(" Where FinID = ? and FinEvent in (?, ?) and FeeTypeCode in (?, ?)");
		sql.append(" group by ft.FeeTypeId, FeeTypeCode");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setString(index++, AccountingEvent.ADDDBSP);
			ps.setString(index++, AccountingEvent.ADDDBSN);
			ps.setString(index++, AdvanceRuleCode.ADVINT.name());
			ps.setString(index, AdvanceRuleCode.ADVEMI.name());

		}, (rs, num) -> {
			FinFeeDetail fd = new FinFeeDetail();

			fd.setFeeTypeID(rs.getLong("FeeTypeId"));
			fd.setFeeTypeCode(rs.getString("FeeTypeCode"));
			fd.setCalculatedAmount(rs.getBigDecimal("CalculatedAmount"));

			return fd;
		});
	}

	@Override
	public List<FinFeeDetail> getFeeDetails(long finID, String feetypeCode, List<String> finEvents) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from FinFeeDetail");
		sql.append(" where FinID = :FinID and FinEvent in (:FinEvent) ");
		sql.append(" and FeeTypeID = (select FeeTypeID from feetypes where FeetypeCode = :FeeTypeCode)");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinID", finID);
		source.addValue("FinEvent", finEvents);
		source.addValue("FeeTypeCode", feetypeCode);

		RowMapper<FinFeeDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinFeeDetail.class);

		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public void updateFeesFromUpfront(FinFeeDetail fd, String type) {
		StringBuilder sql = new StringBuilder("Update FinFeeDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PaidAmount = ?, PaidAmountOriginal = ?, PaidAmountGST = ?, RemainingFee = ?");
		sql.append(", RemainingFeeOriginal = ?, RemainingFeeGST = ?, PaidTDS = ?, RemTDS = ?, LastMntOn = ?");
		sql.append(" Where FeeID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, fd.getPaidAmount());
			ps.setBigDecimal(index++, fd.getPaidAmountOriginal());
			ps.setBigDecimal(index++, fd.getPaidAmountGST());
			ps.setBigDecimal(index++, fd.getRemainingFee());
			ps.setBigDecimal(index++, fd.getRemainingFeeOriginal());
			ps.setBigDecimal(index++, fd.getRemainingFeeGST());
			ps.setBigDecimal(index++, fd.getPaidTDS());
			ps.setBigDecimal(index++, fd.getRemTDS());
			ps.setTimestamp(index++, fd.getLastMntOn());

			ps.setLong(index, fd.getFeeID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByTransactionId(String transactionId, boolean isWIF, String tableType) {
		StringBuilder sql = new StringBuilder();

		if (isWIF) {
			sql.append("Delete From WIFFinFeeDetail");
		} else {
			sql.append("Delete From FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where TransactionId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index, transactionId);
		});
	}

	@Override
	public boolean isFinFeeDetailExists(FinFeeDetail ffd, String type) {
		StringBuilder sql = new StringBuilder("Select count(FinID) From FinFeeDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinEvent  = ? and OriginationFee = ? and FinID = ? and VasReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		Object[] args = new Object[] { ffd.getFinEvent(), ffd.isOriginationFee(), ffd.getFinID(),
				ffd.getVasReference() };

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, args) > 0;
	}

	private StringBuilder getSelectQuery(boolean isWIF, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeID, FinID, FinReference, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder");
		sql.append(", CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount, FeeScheduleMethod, Terms");
		sql.append(", RemainingFee, PaymentRef, CalculationType, VasReference, Status");
		sql.append(", RuleCode, FixedAmount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc");
		sql.append(", AlwModifyFee, AlwModifyFeeSchdMthd,Refundable");
		sql.append(", PaidAmountOriginal, PaidAmountGST, NetAmountOriginal, NetAmountGST");
		sql.append(", NetAmount, RemainingFeeOriginal, RemainingFeeGST");
		sql.append(", TaxApplicable, TaxComponent, ActualAmountOriginal, ActualAmountGST, InstructionUID");
		sql.append(", WaivedGST, ReferenceId, TaxHeaderId, NetTDS, PaidTDS, RemTDS");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (!isWIF) {
			sql.append(", ActPercentage");
		}

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TdsReq");

			if (!isWIF) {
				sql.append(", VasProductCode");
			}
		}

		if (isWIF) {
			sql.append(" From WIFFinFeeDetail");
		} else {
			sql.append(" From FinFeeDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinFeeDetailsRowMapper implements RowMapper<FinFeeDetail> {
		private String type;
		private boolean wIf;

		private FinFeeDetailsRowMapper(String type, boolean wIf) {
			this.type = type;
			this.wIf = wIf;
		}

		@Override
		public FinFeeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinFeeDetail fd = new FinFeeDetail();

			fd.setFeeID(rs.getLong("FeeID"));
			fd.setFinID(rs.getLong("FinID"));
			fd.setFinReference(rs.getString("FinReference"));
			fd.setOriginationFee(rs.getBoolean("OriginationFee"));
			fd.setFinEvent(rs.getString("FinEvent"));
			fd.setFeeTypeID(rs.getLong("FeeTypeID"));
			fd.setFeeSeq(rs.getInt("FeeSeq"));
			fd.setFeeOrder(rs.getInt("FeeOrder"));
			fd.setCalculatedAmount(rs.getBigDecimal("CalculatedAmount"));
			fd.setActualAmount(rs.getBigDecimal("ActualAmount"));
			fd.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			fd.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fd.setFeeScheduleMethod(rs.getString("FeeScheduleMethod"));
			fd.setTerms(rs.getInt("Terms"));
			fd.setRemainingFee(rs.getBigDecimal("RemainingFee"));
			fd.setPaymentRef(rs.getString("PaymentRef"));
			fd.setCalculationType(rs.getString("CalculationType"));
			fd.setVasReference(rs.getString("VasReference"));
			fd.setStatus(rs.getString("Status"));
			fd.setRuleCode(rs.getString("RuleCode"));
			fd.setFixedAmount(rs.getBigDecimal("FixedAmount"));
			fd.setPercentage(rs.getBigDecimal("Percentage"));
			fd.setCalculateOn(rs.getString("CalculateOn"));
			fd.setAlwDeviation(rs.getBoolean("AlwDeviation"));
			fd.setMaxWaiverPerc(rs.getBigDecimal("MaxWaiverPerc"));
			fd.setAlwModifyFee(rs.getBoolean("AlwModifyFee"));
			fd.setAlwModifyFeeSchdMthd(rs.getBoolean("AlwModifyFeeSchdMthd"));
			fd.setRefundable(rs.getBoolean("Refundable"));
			fd.setPaidAmountOriginal(rs.getBigDecimal("PaidAmountOriginal"));
			fd.setPaidAmountGST(rs.getBigDecimal("PaidAmountGST"));
			fd.setNetAmountOriginal(rs.getBigDecimal("NetAmountOriginal"));
			fd.setNetAmountGST(rs.getBigDecimal("NetAmountGST"));
			fd.setNetAmount(rs.getBigDecimal("NetAmount"));
			fd.setRemainingFeeOriginal(rs.getBigDecimal("RemainingFeeOriginal"));
			fd.setRemainingFeeGST(rs.getBigDecimal("RemainingFeeGST"));
			fd.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			fd.setTaxComponent(rs.getString("TaxComponent"));
			fd.setActualAmountOriginal(rs.getBigDecimal("ActualAmountOriginal"));
			fd.setActualAmountGST(rs.getBigDecimal("ActualAmountGST"));
			fd.setInstructionUID(JdbcUtil.getLong(rs.getObject("InstructionUID")));
			fd.setWaivedGST(rs.getBigDecimal("WaivedGST"));
			fd.setReferenceId(rs.getLong("ReferenceId"));
			fd.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
			fd.setNetTDS(rs.getBigDecimal("NetTDS"));
			fd.setPaidTDS(rs.getBigDecimal("PaidTDS"));
			fd.setRemTDS(rs.getBigDecimal("RemTDS"));
			fd.setVersion(rs.getInt("Version"));
			fd.setLastMntBy(rs.getLong("LastMntBy"));
			fd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fd.setRecordStatus(rs.getString("RecordStatus"));
			fd.setRoleCode(rs.getString("RoleCode"));
			fd.setNextRoleCode(rs.getString("NextRoleCode"));
			fd.setTaskId(rs.getString("TaskId"));
			fd.setNextTaskId(rs.getString("NextTaskId"));
			fd.setRecordType(rs.getString("RecordType"));
			fd.setWorkflowId(rs.getLong("WorkflowId"));

			if (!wIf) {
				fd.setActPercentage(rs.getBigDecimal("ActPercentage"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					fd.setVasProductCode(rs.getString("VasProductCode"));
				}
			}

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fd.setFeeTypeCode(rs.getString("FeeTypeCode"));
				fd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				fd.setTdsReq(rs.getBoolean("TdsReq"));

				if (!wIf) {
					fd.setVasProductCode(rs.getString("VasProductCode"));
				}
			}

			return fd;
		}
	}

	@Override
	public List<FinFeeDetail> getTotalPaidFees(String reference, String type) {
		StringBuilder sql = new StringBuilder("Select PaidAmount, FeeID, FeeTypeCode");
		sql.append(" From FinFeeDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where TransactionId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> ps.setString(1, reference), (rs, rowNum) -> {
			FinFeeDetail fee = new FinFeeDetail();

			fee.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fee.setFeeID(rs.getLong("FeeID"));
			fee.setFeeTypeCode(rs.getString("FeeTypeCode"));
			fee.setTransactionId(reference);

			return fee;
		});
	}

	@Override
	public FinFeeDetail getFinFeeDetail(long feeID) {
		StringBuilder sql = getSelectQuery(false, "");
		sql.append(" Where FeeID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper("", false);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, feeID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}