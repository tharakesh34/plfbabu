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
 * * FileName : FinanceRepaymentsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.ReceiptAPIRequest;
import com.pennant.backend.model.finance.ReceiptCancelDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class FinReceiptHeaderDAOImpl extends SequenceDao<FinReceiptHeader> implements FinReceiptHeaderDAO {
	private static Logger logger = LogManager.getLogger(FinReceiptHeaderDAOImpl.class);

	public FinReceiptHeaderDAOImpl() {
		super();
	}

	@Override
	public List<FinReceiptHeader> getReceiptHeaderByRef(String finReference, String rcdMaintainSts, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Reference = ? and RcdMaintainSts = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new Object[] { finReference, rcdMaintainSts }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public long save(FinReceiptHeader rh, TableType tableType) {
		if (rh.getId() == 0 || rh.getId() == Long.MIN_VALUE) {
			rh.setId(getNextValue("SeqFinReceiptHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinReceiptHeader").append(tableType.getSuffix());
		sql.append("(ReceiptID, ReceiptDate, ReceiptType, RecAgainst, Reference, ReceiptPurpose, RcdMaintainSts");
		sql.append(", ReceiptMode, ExcessAdjustTo, AllocationType, ReceiptAmount, EffectSchdMethod, ReceiptModeStatus");
		sql.append(", RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks, GDRAvailable");
		sql.append(", ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount, CashierBranch, InitiateDate");
		sql.append(", DepositProcess, DepositBranch, LppAmount, GstLpiAmount, GstLppAmount, ExtReference");
		sql.append(", Module, SubReceiptMode, ReceiptChannel, ReceivedFrom, PanNumber, CollectionAgentId");
		sql.append(", ActFinReceipt, FinDivision, PostBranch, ReasonCode, CancelRemarks, KnockOffType");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, RefWaiverAmt, Source, ValueDate, TransactionRef, DepositDate");
		sql.append(", PartnerBankId, PrvReceiptPurpose, ReceiptSource, RecAppDate, ReceivedDate");
		sql.append(", ClosureTypeId, SourceofFund, TdsAmount, EntityCode");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, JdbcUtil.setLong(rh.getReceiptID()));
			ps.setDate(index++, JdbcUtil.getDate(rh.getReceiptDate()));
			ps.setString(index++, rh.getReceiptType());
			ps.setString(index++, rh.getRecAgainst());
			ps.setString(index++, rh.getReference());
			ps.setString(index++, rh.getReceiptPurpose());
			ps.setString(index++, rh.getRcdMaintainSts());
			ps.setString(index++, rh.getReceiptMode());
			ps.setString(index++, rh.getExcessAdjustTo());
			ps.setString(index++, rh.getAllocationType());
			ps.setBigDecimal(index++, rh.getReceiptAmount());
			ps.setString(index++, rh.getEffectSchdMethod());
			ps.setString(index++, rh.getReceiptModeStatus());
			ps.setDate(index++, JdbcUtil.getDate(rh.getRealizationDate()));
			ps.setString(index++, rh.getCancelReason());
			ps.setBigDecimal(index++, rh.getWaviedAmt());
			ps.setBigDecimal(index++, rh.getTotFeeAmount());
			ps.setDate(index++, JdbcUtil.getDate(rh.getBounceDate()));
			ps.setString(index++, rh.getRemarks());
			ps.setBoolean(index++, rh.isGDRAvailable());
			ps.setString(index++, rh.getReleaseType());
			ps.setString(index++, rh.getThirdPartyName());
			ps.setString(index++, rh.getThirdPartyMobileNum());
			ps.setBigDecimal(index++, rh.getLpiAmount());
			ps.setString(index++, rh.getCashierBranch());
			ps.setDate(index++, JdbcUtil.getDate(rh.getInitiateDate()));
			ps.setBoolean(index++, rh.isDepositProcess());
			ps.setString(index++, rh.getDepositBranch());
			ps.setBigDecimal(index++, rh.getLppAmount());
			ps.setBigDecimal(index++, rh.getGstLpiAmount());
			ps.setBigDecimal(index++, rh.getGstLppAmount());
			ps.setString(index++, rh.getExtReference());
			ps.setString(index++, rh.getModule());
			ps.setString(index++, rh.getSubReceiptMode());
			ps.setString(index++, rh.getReceiptChannel());
			ps.setString(index++, rh.getReceivedFrom());
			ps.setString(index++, rh.getPanNumber());
			ps.setLong(index++, JdbcUtil.setLong(rh.getCollectionAgentId()));
			ps.setBoolean(index++, rh.isActFinReceipt());
			ps.setString(index++, rh.getFinDivision());
			ps.setString(index++, rh.getPostBranch());
			ps.setLong(index++, JdbcUtil.setLong(rh.getReasonCode()));
			ps.setString(index++, rh.getCancelRemarks());
			ps.setString(index++, rh.getKnockOffType());
			ps.setInt(index++, rh.getVersion());
			ps.setTimestamp(index++, rh.getLastMntOn());
			ps.setLong(index++, JdbcUtil.setLong(rh.getLastMntBy()));
			ps.setString(index++, rh.getRecordStatus());
			ps.setString(index++, rh.getRoleCode());
			ps.setString(index++, rh.getNextRoleCode());
			ps.setString(index++, rh.getTaskId());
			ps.setString(index++, rh.getNextTaskId());
			ps.setString(index++, rh.getRecordType());
			ps.setLong(index++, JdbcUtil.setLong(rh.getWorkflowId()));
			ps.setBigDecimal(index++, rh.getRefWaiverAmt());
			ps.setString(index++, rh.getSource());
			ps.setDate(index++, JdbcUtil.getDate(rh.getValueDate()));
			ps.setString(index++, rh.getTransactionRef());
			ps.setDate(index++, JdbcUtil.getDate(rh.getDepositDate()));
			ps.setLong(index++, JdbcUtil.setLong(rh.getPartnerBankId()));
			ps.setString(index++, rh.getPrvReceiptPurpose());
			ps.setString(index++, rh.getReceiptSource());
			ps.setDate(index++, JdbcUtil.getDate(rh.getRecAppDate()));
			ps.setDate(index++, JdbcUtil.getDate(rh.getReceivedDate()));
			ps.setObject(index++, JdbcUtil.getLong(rh.getClosureTypeId()));
			ps.setString(index++, rh.getSourceofFund());
			ps.setBigDecimal(index++, rh.getTdsAmount());
			ps.setString(index++, rh.getEntityCode());
		});

		return rh.getId();
	}

	@Override
	public void update(FinReceiptHeader rh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Set ");
		sql.append(" ReceiptDate = ?, ReceiptType = ?, RecAgainst = ?, Reference = ?");
		sql.append(", ReceiptPurpose = ?, ReceiptMode = ?, ExcessAdjustTo = ?, AllocationType = ?");
		sql.append(", ReceiptAmount = ?, EffectSchdMethod = ?, RcdMaintainSts = ?, InitiateDate = ?");
		sql.append(", ReceiptModeStatus = ?, RealizationDate = ?, CancelReason = ?, WaviedAmt = ?");
		sql.append(", TotFeeAmount = ?, BounceDate = ?, Remarks = ?, GDRAvailable = ?, ReleaseType = ?");
		sql.append(", ThirdPartyName = ?, ThirdPartyMobileNum = ?, LpiAmount = ?, CashierBranch = ?");
		sql.append(", DepositProcess = ?, DepositBranch = ?, LppAmount = ?, GstLpiAmount = ?, GstLppAmount = ?");
		sql.append(", Version = ?, LastMntOn = ?, LastMntBy = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", SubReceiptMode = ?, ReceiptChannel = ?, ReceivedFrom = ?, PanNumber = ?, CollectionAgentId = ?");
		sql.append(", ActFinReceipt = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?");
		sql.append(", WorkflowId = ?, FinDivision = ?, PostBranch = ?, ReasonCode = ?, CancelRemarks = ?");
		sql.append(", KnockOffType = ?, RefWaiverAmt = ?, Source = ?, ValueDate = ?, TransactionRef = ?");
		sql.append(", DepositDate = ?, PartnerBankId = ?, PrvReceiptPurpose = ?, ReceiptSource = ?");
		sql.append(", RecAppDate = ?, ReceivedDate = ?, ExtReference = ?, ClosureTypeId = ?");
		sql.append(", SourceofFund = ?, TdsAmount = ?, EntityCode = ?");
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setDate(index++, JdbcUtil.getDate(rh.getReceiptDate()));
			ps.setString(index++, rh.getReceiptType());
			ps.setString(index++, rh.getRecAgainst());
			ps.setString(index++, rh.getReference());
			ps.setString(index++, rh.getReceiptPurpose());
			ps.setString(index++, rh.getReceiptMode());
			ps.setString(index++, rh.getExcessAdjustTo());
			ps.setString(index++, rh.getAllocationType());
			ps.setBigDecimal(index++, rh.getReceiptAmount());
			ps.setString(index++, rh.getEffectSchdMethod());
			ps.setString(index++, rh.getRcdMaintainSts());
			ps.setDate(index++, JdbcUtil.getDate(rh.getInitiateDate()));
			ps.setString(index++, rh.getReceiptModeStatus());
			ps.setDate(index++, JdbcUtil.getDate(rh.getRealizationDate()));
			ps.setString(index++, rh.getCancelReason());
			ps.setBigDecimal(index++, rh.getWaviedAmt());
			ps.setBigDecimal(index++, rh.getTotFeeAmount());
			ps.setDate(index++, JdbcUtil.getDate(rh.getBounceDate()));
			ps.setString(index++, rh.getRemarks());
			ps.setBoolean(index++, rh.isGDRAvailable());
			ps.setString(index++, rh.getReleaseType());
			ps.setString(index++, rh.getThirdPartyName());
			ps.setString(index++, rh.getThirdPartyMobileNum());
			ps.setBigDecimal(index++, rh.getLpiAmount());
			ps.setString(index++, rh.getCashierBranch());
			ps.setBoolean(index++, rh.isDepositProcess());
			ps.setString(index++, rh.getDepositBranch());
			ps.setBigDecimal(index++, rh.getLppAmount());
			ps.setBigDecimal(index++, rh.getGstLpiAmount());
			ps.setBigDecimal(index++, rh.getGstLppAmount());
			ps.setInt(index++, rh.getVersion());
			ps.setTimestamp(index++, rh.getLastMntOn());
			ps.setLong(index++, rh.getLastMntBy());
			ps.setString(index++, rh.getRecordStatus());
			ps.setString(index++, rh.getRoleCode());
			ps.setString(index++, rh.getSubReceiptMode());
			ps.setString(index++, rh.getReceiptChannel());
			ps.setString(index++, rh.getReceivedFrom());
			ps.setString(index++, rh.getPanNumber());
			ps.setLong(index++, rh.getCollectionAgentId());
			ps.setBoolean(index++, rh.isActFinReceipt());
			ps.setString(index++, rh.getNextRoleCode());
			ps.setString(index++, rh.getTaskId());
			ps.setString(index++, rh.getNextTaskId());
			ps.setString(index++, rh.getRecordType());
			ps.setLong(index++, rh.getWorkflowId());
			ps.setString(index++, rh.getFinDivision());
			ps.setString(index++, rh.getPostBranch());
			ps.setLong(index++, rh.getReasonCode());
			ps.setString(index++, rh.getCancelRemarks());
			ps.setString(index++, rh.getKnockOffType());
			ps.setBigDecimal(index++, rh.getRefWaiverAmt());
			ps.setString(index++, rh.getSource());
			ps.setDate(index++, JdbcUtil.getDate(rh.getValueDate()));
			ps.setString(index++, rh.getTransactionRef());
			ps.setDate(index++, JdbcUtil.getDate(rh.getDepositDate()));
			ps.setLong(index++, rh.getPartnerBankId());
			ps.setString(index++, rh.getPrvReceiptPurpose());
			ps.setString(index++, rh.getReceiptSource());
			ps.setDate(index++, JdbcUtil.getDate(rh.getRecAppDate()));
			ps.setDate(index++, JdbcUtil.getDate(rh.getReceivedDate()));
			ps.setString(index++, rh.getExtReference());
			ps.setObject(index++, JdbcUtil.getLong(rh.getClosureTypeId()));
			ps.setString(index++, rh.getSourceofFund());
			ps.setBigDecimal(index++, rh.getTdsAmount());
			ps.setString(index++, rh.getEntityCode());

			ps.setLong(index, rh.getReceiptID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder sql = new StringBuilder(" DELETE From FinReceiptHeader");
		sql.append(tableType.getSuffix());
		sql.append(" where ReceiptID=:ReceiptID ");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetching Finance Receipt Header by using Receipt ID
	 */
	@Override
	public FinReceiptHeader getReceiptHeaderByID(long receiptID, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptID }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records not available for the Receipt ID {}", receiptID);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptID, ReceiptDate, ReceiptType, RecAgainst, Reference, ReceiptPurpose, RcdMaintainSts");
		sql.append(", InstructionUID, ReceiptMode, ExcessAdjustTo, AllocationType, ReceiptAmount, EffectSchdMethod");
		sql.append(", ReceiptModeStatus, RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate");
		sql.append(", Remarks, GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount");
		sql.append(", CashierBranch, InitiateDate, DepositProcess, DepositBranch, LppAmount, GstLpiAmount");
		sql.append(", GstLppAmount, subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId");
		sql.append(", ExtReference, Module, FinDivision, PostBranch, ActFinReceipt, ReasonCode, CancelRemarks");
		sql.append(", KnockOffType, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", RefWaiverAmt, Source, ValueDate, TransactionRef, DepositDate, PartnerBankId");
		sql.append(
				", PrvReceiptPurpose, ReceiptSource, RecAppDate, ReceivedDate, ClosureTypeId, SourceofFund, TdsAmount");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinType, FinCcy, FinBranch, CustCIF, CustShrtName, FinTypeDesc");
			sql.append(", FinCcyDesc, FinBranchDesc, CancelReasonDesc, FinIsActive, PromotionCode, ProductCategory");
			sql.append(", NextRepayRvwDate, CollectionAgentCode, CollectionAgentDesc, PostBranchDesc");
			sql.append(", CashierBranchDesc, FinDivisionDesc, EntityCode, ClosureTypeDesc");

			if (StringUtils.trimToEmpty(type).contains("FView")) {
				sql.append(", ScheduleMethod, PftDaysBasis, CustID, CustomerCIF");
				sql.append(", CustomerName, CustBaseCcy, FinTDSApplicable");
			}

			if (StringUtils.trimToEmpty(type).contains("FEView") || StringUtils.trimToEmpty(type).contains("FCView")) {
				sql.append(", CustID, CustomerCIF, CustomerName");
			}
		}

		sql.append(" from FinReceiptHeader");
		sql.append(StringUtils.trim(type));
		return sql;
	}

	@Override
	public int geFeeReceiptCount(String reference, String receiptPurpose, long receiptId) {
		int count = 0;

		StringBuilder sql = new StringBuilder("Select count(*)  from (");
		sql.append(" select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus from FinReceiptHeader union all ");
		sql.append(" select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus from FinReceiptHeader_Temp) T");
		sql.append(" Where ReceiptID <> :ReceiptID and Reference = :Reference");
		sql.append(" and ReceiptPurpose = :ReceiptPurpose and ReceiptModeStatus in('A','F')");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("ReceiptPurpose", receiptPurpose);
		source.addValue("ReceiptID", receiptId);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION);
		}

		return count;
	}

	@Override
	public FinReceiptHeader getServicingFinanceHeader(long receiptID, String userRole, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID = ? and NextRoleCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptID, userRole }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<ReceiptCancelDetail> getReceiptCancelDetailList(Date cancelReqDate, String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("ReceivedDate", cancelReqDate);

		StringBuilder sql = new StringBuilder(" Select RH.ReceiptID ReceiptId , RH.ReceivedDate ValueDate");
		sql.append(", (RH.ReceiptAmount + Rh.WaviedAmt ) Amount, Rh.WaviedAmt ");
		sql.append(" From FinReceiptHeader RH INNER JOIN FinReceiptDetail RD ON RH.ReceiptID = RD.ReceiptID");
		sql.append(" Where RH.Reference =:Reference AND  RD.ReceivedDate >= :ReceivedDate");
		sql.append("  AND RH.ReceiptModeStatus NOT IN('C','B') ORDER BY RH.ReceiptID ");

		logger.debug(Literal.SQL + sql.toString());
		RowMapper<ReceiptCancelDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(ReceiptCancelDetail.class);
		List<ReceiptCancelDetail> rcptCancelDetails = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		logger.debug(Literal.LEAVING);
		return rcptCancelDetails;
	}

	@Override
	public void updateReceiptStatus(long receiptID, String status) {
		String sql = "Update FinReceiptHeader Set ReceiptModeStatus = ? Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, status);
			ps.setLong(2, receiptID);
		});
	}

	@Override
	public long generatedReceiptID(FinReceiptHeader receiptHeader) {
		if (receiptHeader.getId() == 0 || receiptHeader.getId() == Long.MIN_VALUE) {
			receiptHeader.setId(getNextValue("SeqFinReceiptHeader"));
		}
		return receiptHeader.getId();
	}

	@Override
	public void updateDepositProcessByReceiptID(long receiptID, boolean depositProcess, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("DepositProcess", depositProcess);

		StringBuilder sql = new StringBuilder(" Update FinReceiptHeader");
		sql.append(type);
		sql.append(" Set DepositProcess = :DepositProcess ");
		sql.append(" Where ReceiptID = :ReceiptID");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateDepositBranchByReceiptID(long receiptID, String depositBranch, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);
		source.addValue("DepositBranch", depositBranch);

		StringBuilder sql = new StringBuilder(" Update FinReceiptHeader");
		sql.append(type);
		sql.append(" Set DepositBranch = :DepositBranch ");
		sql.append(" Where ReceiptID = :ReceiptID");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 */
	@Override
	public BigDecimal getTotalCashReceiptAmount(String depositBranch, String type) {
		logger.debug(Literal.ENTERING);

		BigDecimal amount = BigDecimal.ZERO;

		StringBuilder sql = new StringBuilder("Select Sum(Amount) from FinReceiptDetail");
		// selectSql.append(type); //check this case when we are submit the cancel request Details not effected to Temp
		// table
		sql.append(" Where PaymentType = :PaymentType And ReceiptId In (SELECT ReceiptId FROM FinReceiptHeader");
		sql.append(type);
		sql.append(" Where ReceiptModeStatus = :ReceiptModeStatus And RecordType != :RecordType");
		sql.append(" And DepositBranch = :DepositBranch)");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentType", RepayConstants.RECEIPTMODE_CASH);
		source.addValue("ReceiptModeStatus", RepayConstants.PAYSTATUS_CANCEL);
		source.addValue("RecordType", PennantConstants.RECORD_TYPE_NEW);
		source.addValue("DepositBranch", depositBranch);

		try {
			amount = this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (DataAccessException e) {
			logger.error(e);
			amount = BigDecimal.ZERO;
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return amount;
	}

	/**
	 * 
	 */
	@Override
	public boolean isReceiptCancelProcess(String depositBranch, List<String> paymentTypes, String type,
			long receiptId) {
		logger.debug(Literal.ENTERING);

		int count = 0;

		StringBuilder sql = new StringBuilder("Select Count(ReceiptId) from FinReceiptDetail");
		// selectSql.append(type); //check this case when we are submit the cancel request Details not effected to Temp
		// table
		sql.append(" Where PaymentType In (:PaymentType) And ReceiptId In (");
		sql.append(" SELECT ReceiptId FROM FinReceiptHeader");
		sql.append(type);
		sql.append(" Where ReceiptModeStatus = :ReceiptModeStatus And RecordType != :RecordType");
		sql.append(" And DepositBranch = :DepositBranch And ReceiptId = :ReceiptId)");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentType", paymentTypes);
		source.addValue("ReceiptModeStatus", RepayConstants.PAYSTATUS_CANCEL);
		source.addValue("RecordType", PennantConstants.RECORD_TYPE_NEW);
		source.addValue("DepositBranch", depositBranch);
		source.addValue("ReceiptId", receiptId);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		} finally {
			logger.debug(Literal.LEAVING);
		}

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<FinReceiptHeader> getUpFrontReceiptHeaderByID(List<Long> receipts, String type) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID IN (");
		int i = 0;
		while (i < receipts.size()) {
			sql.append(" ?,");
			i++;
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")  and Reference is null ");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					for (Long receiptId : receipts) {
						ps.setLong(index++, receiptId);
					}
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void updateReference(String extReference, String finReference, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(type);
		sql.append(" SET  Reference=:Reference  ");
		sql.append(" Where ExtReference=:ExtReference and Reference is null");

		logger.debug(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtReference", extReference);
		source.addValue("Reference", finReference);
		recordCount = this.jdbcTemplate.update(sql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public List<FinReceiptHeader> getUpFrontReceiptHeaderByExtRef(String extRef, String type) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ExtReference = ? and Reference is null ");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, extRef);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public Date getMaxReceiptDateByRef(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select max(receiptdate) from FinReceiptHeader");
		sql.append(" Where Reference =:Reference AND RECEIPTMODESTATUS not in ('B','C')");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), source, Date.class);
	}

	@Override
	public void cancelReceipts(String finReference) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(" Set ReceiptModeStatus='C'  Where Reference =:Reference");

		logger.debug("updateSql: " + sql.toString());
		source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> fetchReceiptIdList(String finreference) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" Select ReceiptID from FinreceiptHeader where Reference=:Reference");

		if (!SysParamUtil.isAllowed(SMTParameterConstants.UPFRONT_FEE_REVERSAL_REQ)) {
			sql.append(" and ReceiptPurpose != '" + FinanceConstants.FINSER_EVENT_FEEPAYMENT + "'");
		}

		if (!SysParamUtil.isAllowed(SMTParameterConstants.REPAY_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL)) {
			sql.append(" and ReceiptPurpose != '" + FinanceConstants.FINSER_EVENT_SCHDRPY + "'");
		}

		logger.debug(Literal.SQL + sql.toString());
		/*
		 * SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header); RowMapper<FinReceiptHeader>
		 * typeRowMapper = BeanPropertyRowMapper.newInstance(FinReceiptHeader.class);
		 */

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finreference);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForList(sql.toString(), source, Long.class);

	}

	@Override
	public boolean isExtRefAssigned(String extReference) {

		logger.debug(Literal.ENTERING);
		boolean isAssigned = false;
		int count = 0;
		StringBuilder sql = new StringBuilder(" Select COUNT(*)  From FinReceiptHeader");
		sql.append(" Where ExtReference =:ExtReference  AND Reference is not null");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ExtReference", extReference);

		logger.debug(Literal.LEAVING);
		count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		if (count > 0) {
			isAssigned = true;
		}
		return isAssigned;
	}

	@Override
	public boolean checkInProcessPresentments(String finReference) {
		StringBuilder sql = new StringBuilder("Select count(*) from PresentmentDetails");
		sql.append(" where Id In (select PresentmentId from finScheduleDetails");
		sql.append(" where FinReference = ? and presentmentId != ?)");
		sql.append(" and status in (?) and FinReference = ? and Excludereason = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { finReference, 0, "A", finReference, 0 }, Integer.class) > 0;
		} catch (DataAccessException e) {
			logger.warn("Presement not found for the Loan Reference {} with status A and Excludereason 0.",
					finReference);
		}

		return false;
	}

	@Override
	public boolean checkPresentmentsInQueue(String finReference) {
		StringBuilder sql = new StringBuilder("Select count(FinReference) from PresentmentDetails");
		sql.append(" where presentmentId In (select PresentmentId from finScheduleDetails");
		sql.append(" where FinReference = ? and presentmentId != ?)");
		sql.append(" and status in (?, ?) and FinReference = ? and Excludereason = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { finReference, 0, "A", "I", finReference, 0 }, Integer.class) > 0;
		} catch (DataAccessException e) {
			logger.warn("Presement not found for the Loan Reference {} with status I, A and Excludereason 0.",
					finReference);
		}
		return false;
	}

	@Override
	public List<FinReceiptHeader> getReceiptHeadersByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Reference = ?");
		sql.append(" order by ReceiptDate, ReceiptID");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.ENTERING, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public boolean checkInProcessReceipts(String reference, long receiptId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Count(*)");
		sql.append(" From FinReceiptHeader_Temp");
		sql.append(" Where Reference = ? and ReceiptId <> ? ");

		logger.debug(Literal.SQL + sql.toString());

		int count = 0;
		count = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { reference, receiptId },
				(rs, rowNum) -> rs.getInt(1));

		// Check for Schedule pay with Cheque mode.
		if (count == 0) {
			sql = new StringBuilder("Select");
			sql.append(" Count(*)");
			sql.append(" From FinReceiptHeader");
			sql.append(" Where Reference= ? and ReceiptModeStatus in (?, ?)");

			logger.debug(Literal.SQL + sql.toString());

			count = this.jdbcOperations.queryForObject(sql.toString(), new Object[] { reference, "I", "D" },
					(rs, rowNum) -> rs.getInt(1));
		}
		return count > 0;
	}

	/**
	 * check with below parameters in receipt details and return true if exits or false
	 * 
	 * @param reference
	 * @param receiptMode
	 * @param chequeNo
	 * @param favourNumber
	 */
	@Override
	public boolean isReceiptDetailsExits(String reference, String receiptMode, String chequeNo, String favourNumber,
			String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder sql = new StringBuilder("Select count(*) from finreceiptheader" + type);
		sql.append(" t inner join finreceiptdetail" + type);
		sql.append(" t1 on t1.receiptid=t.receiptid where REFERENCE= :REFERENCE and RECEIPTMODE= :RECEIPTMODE");
		sql.append(" and T1.FAVOURNUMBER= :FAVOURNUMBER and T1.CHEQUEACNO = :CHEQUEACNO ");
		sql.append(" and (T.RECEIPTMODESTATUS in ('A') or T.RECEIPTMODESTATUS is null)");
		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);
		source.addValue("RECEIPTMODE", receiptMode);
		source.addValue("FAVOURNUMBER", favourNumber);
		source.addValue("CHEQUEACNO", chequeNo);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		}

		logger.debug(Literal.LEAVING);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 29-10-2018, Ticket id:124998 update receipt mode status and realization date return boolean condition
	 */
	@Override
	public void updateReceiptStatusAndRealizationDate(long receiptID, String status, Date realizationDate) {
		String sql = "Update FinReceiptHeader Set ReceiptModeStatus = ?, RealizationDate = ? Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, status);
			ps.setDate(2, JdbcUtil.getDate(realizationDate));
			ps.setLong(3, receiptID);
		});
	}

	@Override
	public List<FinReceiptHeader> getInProcessReceipts(String Reference) {
		StringBuilder sql = new StringBuilder("");
		sql.append(" Select ReceiptID, AllocationType , ReceiptAmount ");
		sql.append(" From FinReceiptHeader_Temp");
		sql.append(" Where Reference =:Reference ");
		logger.debug(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", Reference);

		RowMapper<FinReceiptHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinReceiptHeader.class);
		List<FinReceiptHeader> rchList = null;

		try {
			rchList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {

		}

		return rchList;
	}

	@Override
	public String getReceiptModeStatus(long receiptID) {
		String sql = "Select ReceiptModeStatus from FinReceiptHeader Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, new Object[] { receiptID }, String.class);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return "";
	}

	@Override
	public List<Long> getInProcessReceiptId(String finReference) {
		StringBuilder sql = new StringBuilder("");
		sql.append(" Select ReceiptID");
		sql.append(" From FinReceiptHeader");
		sql.append(" Where Reference =:Reference and ReceiptModeStatus ='D' and receiptPurpose='SchdlRepayment'");
		logger.debug(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);

		List<Long> receiptList = null;

		try {
			receiptList = this.jdbcTemplate.queryForList(sql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {

		}

		return receiptList;
	}

	@Override
	public void updateLoanInActive(long receiptId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptId);
		source.addValue("LoanActive", 0);

		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(" Set LoanActive=:LoanActive");
		sql.append(" Where ReceiptID =:ReceiptID  ");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveMultiReceipt(FinReceiptHeader finReceiptHeader, FinReceiptDetail finReceiptDetail,
			Map<String, String> valueMap) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BatchId", finReceiptHeader.getBatchId());
		source.addValue("ReceiptID", finReceiptHeader.getReceiptID());
		source.addValue("ReceiptModeStatus", finReceiptHeader.getReceiptModeStatus());
		source.addValue("BounceDate", finReceiptHeader.getBounceDate());
		source.addValue("RealizationDate", finReceiptHeader.getRealizationDate());
		source.addValue("Remarks", finReceiptHeader.getRemarks());
		source.addValue("CancelReason", finReceiptHeader.getCancelReason());
		source.addValue("DepositDate", finReceiptDetail.getDepositDate());
		source.addValue("ReceiptDate", finReceiptHeader.getReceiptDate());
		source.addValue("FinReference", finReceiptHeader.getReference());
		if (StringUtils.isNotBlank(finReceiptHeader.getRoleCode())
				&& finReceiptHeader.getRoleCode().contains("MAKER")) {
			source.addValue("Stage", "M");
		} else {
			source.addValue("Stage", "C");
		}
		source.addValue("DepositNo", finReceiptDetail.getDepositNo());
		source.addValue("FundingAc", finReceiptDetail.getFundingAc());
		source.addValue("BounceId", finReceiptHeader.getBounceReason());
		source.addValue("UploadStatus", valueMap.get("uploadStatus"));
		source.addValue("Reason", valueMap.get("reason"));
		source.addValue("CancelRemarks", finReceiptHeader.getCancelRemarks());

		StringBuilder sql = new StringBuilder("Insert into MultiReceiptApproval");
		sql.append(" (BatchId, ReceiptModeStatus, BounceDate, RealizationDate, Remarks, CancelReason, ReceiptID");
		sql.append(", DepositDate, ReceiptDate, FinReference, Stage, DepositNo, FundingAc, BounceId, UploadStatus");
		sql.append(", Reason, CancelRemarks)");
		sql.append(" values(:BatchId, :ReceiptModeStatus, :BounceDate, :RealizationDate, :Remarks, :CancelReason");
		sql.append(", :ReceiptID, :DepositDate, :ReceiptDate, :FinReference, :Stage, :DepositNo, :FundingAc");
		sql.append(", :BounceId, :UploadStatus, :Reason, :CancelRemarks)");

		logger.debug(Literal.SQL + sql.toString());

		// SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptHeader);
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO FinReceiptQueueLog (UploadId, ReceiptId, FinReference");
		sql.append(", TransactionDate, ThreadId, Progress, StartTime)");
		sql.append(" Values( :UploadId, :ReceiptId, :FinReference, :TransactionDate");
		sql.append(", :ThreadId, :Progress, :StartTime)");

		logger.trace(Literal.SQL + sql.toString());

		jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(finReceiptQueueList.toArray()));
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateMultiReceiptLog(FinReceiptQueueLog finReceiptQueue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinReceiptQueueLog");
		sql.append(" Set EndTime =:EndTime, ThreadId =:ThreadId, StartTime =:StartTime, ErrorLog =:ErrorLog");
		sql.append(", Progress =:Progress Where UploadId =:UploadId And ReceiptId =:ReceiptId");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptQueue);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinReceiptQueueLog");
		sql.append(" Set EndTime =:StartTime, ThreadId =:ThreadId, StartTime =:StartTime, ErrorLog =:ErrorLog");
		sql.append(", Progress =:Progress Where UploadId =:UploadId And ReceiptId =:ReceiptId");

		logger.trace(Literal.SQL + sql.toString());

		jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(finReceiptQueueList.toArray()));
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> getInProcessMultiReceiptRecord() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", 0);

		StringBuilder sql = new StringBuilder(" Select ReceiptId From FinReceiptQueueLog");
		sql.append(" Where Progress =:Progress");

		logger.debug(Literal.SQL + sql.toString());

		List<Long> receiptList = this.jdbcTemplate.queryForList(sql.toString(), source, Long.class);
		logger.debug(Literal.LEAVING);
		return receiptList;
	}

	@Override
	public boolean checkEarlySettlementInitiation(String reference) {
		return checkReceiptInitiation(reference, "_view", FinanceConstants.FINSER_EVENT_EARLYSETTLE);
	}

	@Override
	public boolean checkPartialSettlementInitiation(String reference) {
		return checkReceiptInitiation(reference, "_Temp", FinanceConstants.FINSER_EVENT_EARLYRPY);
	}

	private boolean checkReceiptInitiation(String reference, String type, String purpose) {
		StringBuilder sql = new StringBuilder("Select count(*)  from FinReceiptHeader");
		sql.append(type);
		sql.append(" Where Reference = ? and Receiptpurpose = ? and ReceiptModeStatus not in (?,?)");

		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { reference, purpose, "B", "C" },
				(rs, rowNum) -> rs.getInt(1)) > 0;
	}

	@Override
	public boolean isChequeExists(String reference, String paytypeCheque, String bankCode, String favourNumber,
			String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder sql = new StringBuilder(" Select count(*) from finreceiptdetail");
		sql.append(type);
		sql.append(" where REFERENCE= :REFERENCE and PAYMENTTYPE= :RECEIPTMODE and FAVOURNUMBER= :FAVOURNUMBER");
		sql.append(" and BANKCODE = :BANKCODE AND STATUS NOT IN ('B','C')  ");
		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);
		source.addValue("RECEIPTMODE", paytypeCheque);
		source.addValue("FAVOURNUMBER", favourNumber);
		source.addValue("BANKCODE", bankCode);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			//
		}

		logger.debug(Literal.LEAVING);
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isOnlineExists(String reference, String subReceiptMode, String tranRef, String type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder sql = new StringBuilder("Select count(*) from finreceiptdetail");
		sql.append(type);
		sql.append(" where REFERENCE= :REFERENCE and PAYMENTTYPE= :RECEIPTMODE and TRANSACTIONREF= :TRANSACTIONREF");
		sql.append(" And STATUS NOT IN ('B','C')  ");
		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);
		source.addValue("RECEIPTMODE", subReceiptMode);
		source.addValue("TRANSACTIONREF", tranRef);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
			count = 0;
		}

		logger.debug(Literal.LEAVING);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Get Loan Reference if it is present in Temp table
	 */
	@Override
	public String getLoanReferenc(String finReference, String receiptFileName) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT DISTINCT REFERENCE FROM RECEIPTUPLOADDETAILS ");
		sql.append(" WHERE UPLOADHEADERID IN (SELECT UploadHeaderId FROM RECEIPTUPLOADHEADER_view ");
		sql.append(" where FileName not in ( :FileName) and uploadprogress in ("
				+ ReceiptUploadConstants.RECEIPT_DEFAULT + "," + ReceiptUploadConstants.RECEIPT_DOWNLOADED + ") )");
		sql.append(
				" AND REFERENCE = :Reference and uploadstatus in ('" + PennantConstants.UPLOAD_STATUS_SUCCESS + "')");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("FileName", receiptFileName);

		String reference = null;

		try {
			reference = this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			reference = null;
		}

		logger.debug(Literal.LEAVING);
		return reference;

	}

	private class FinReceiptHeaderRowMaper implements RowMapper<FinReceiptHeader> {
		private String type;

		FinReceiptHeaderRowMaper(String type) {
			this.type = type;
		}

		@Override
		public FinReceiptHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinReceiptHeader rh = new FinReceiptHeader();

			rh.setReceiptID(rs.getLong("ReceiptID"));
			rh.setReceiptDate(rs.getTimestamp("ReceiptDate"));
			rh.setReceiptType(rs.getString("ReceiptType"));
			rh.setRecAgainst(rs.getString("RecAgainst"));
			rh.setReference(rs.getString("Reference"));
			rh.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			rh.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
			// rh.setInstructionUID(rs.getLong("InstructionUID")); ( column is not available in bean)
			rh.setReceiptMode(rs.getString("ReceiptMode"));
			rh.setExcessAdjustTo(rs.getString("ExcessAdjustTo"));
			rh.setAllocationType(rs.getString("AllocationType"));
			rh.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			rh.setEffectSchdMethod(rs.getString("EffectSchdMethod"));
			rh.setReceiptModeStatus(rs.getString("ReceiptModeStatus"));
			rh.setRealizationDate(rs.getTimestamp("RealizationDate"));
			rh.setCancelReason(rs.getString("CancelReason"));
			rh.setWaviedAmt(rs.getBigDecimal("WaviedAmt"));
			rh.setTotFeeAmount(rs.getBigDecimal("TotFeeAmount"));
			rh.setBounceDate(rs.getTimestamp("BounceDate"));
			rh.setRemarks(rs.getString("Remarks"));
			rh.setGDRAvailable(rs.getBoolean("GDRAvailable"));
			rh.setReleaseType(rs.getString("ReleaseType"));
			rh.setThirdPartyName(rs.getString("ThirdPartyName"));
			rh.setThirdPartyMobileNum(rs.getString("ThirdPartyMobileNum"));
			rh.setLpiAmount(rs.getBigDecimal("LpiAmount"));
			rh.setCashierBranch(rs.getString("CashierBranch"));
			rh.setInitiateDate(rs.getTimestamp("InitiateDate"));
			rh.setDepositProcess(rs.getBoolean("DepositProcess"));
			rh.setDepositBranch(rs.getString("DepositBranch"));
			rh.setLppAmount(rs.getBigDecimal("LppAmount"));
			rh.setGstLpiAmount(rs.getBigDecimal("GstLpiAmount"));
			rh.setGstLppAmount(rs.getBigDecimal("GstLppAmount"));
			rh.setSubReceiptMode(rs.getString("subReceiptMode"));
			rh.setReceiptChannel(rs.getString("receiptChannel"));
			rh.setReceivedFrom(rs.getString("receivedFrom"));
			rh.setPanNumber(rs.getString("panNumber"));
			rh.setCollectionAgentId(rs.getLong("collectionAgentId"));
			rh.setExtReference(rs.getString("ExtReference"));
			rh.setModule(rs.getString("Module"));
			rh.setFinDivision(rs.getString("FinDivision"));
			rh.setPostBranch(rs.getString("PostBranch"));
			rh.setActFinReceipt(rs.getBoolean("ActFinReceipt"));
			rh.setReasonCode(rs.getLong("ReasonCode"));
			rh.setVersion(rs.getInt("Version"));
			rh.setLastMntOn(rs.getTimestamp("LastMntOn"));
			rh.setLastMntBy(rs.getLong("LastMntBy"));
			rh.setRecordStatus(rs.getString("RecordStatus"));
			rh.setRoleCode(rs.getString("RoleCode"));
			rh.setNextRoleCode(rs.getString("NextRoleCode"));
			rh.setTaskId(rs.getString("TaskId"));
			rh.setNextTaskId(rs.getString("NextTaskId"));
			rh.setRecordType(rs.getString("RecordType"));
			rh.setWorkflowId(rs.getLong("WorkflowId"));
			rh.setCancelRemarks(rs.getString("CancelRemarks"));
			rh.setKnockOffType(rs.getString("KnockOffType"));
			rh.setRefWaiverAmt(rs.getBigDecimal("RefWaiverAmt"));
			rh.setSource(rs.getString("Source"));
			rh.setValueDate(rs.getDate("ValueDate"));
			rh.setTransactionRef(rs.getString("TransactionRef"));
			rh.setDepositDate(rs.getDate("DepositDate"));
			rh.setPartnerBankId(JdbcUtil.setLong(rs.getLong("PartnerBankId")));
			rh.setPrvReceiptPurpose(rs.getString("PrvReceiptPurpose"));
			rh.setReceiptSource(rs.getString("ReceiptSource"));
			rh.setRecAppDate(rs.getDate("RecAppDate"));
			rh.setReceivedDate(rs.getDate("ReceivedDate"));
			rh.setSourceofFund(rs.getString("SourceofFund"));
			rh.setTdsAmount(rs.getBigDecimal("TdsAmount"));
			rh.setClosureTypeId(JdbcUtil.getLong(rs.getObject("ClosureTypeId")));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				rh.setFinType(rs.getString("FinType"));
				rh.setFinCcy(rs.getString("FinCcy"));
				rh.setFinBranch(rs.getString("FinBranch"));
				rh.setCustCIF(rs.getString("CustCIF"));
				rh.setCustShrtName(rs.getString("CustShrtName"));
				rh.setFinTypeDesc(rs.getString("FinTypeDesc"));
				rh.setFinCcyDesc(rs.getString("FinCcyDesc"));
				rh.setFinBranchDesc(rs.getString("FinBranchDesc"));
				rh.setCancelReasonDesc(rs.getString("CancelReasonDesc"));
				rh.setFinIsActive(rs.getBoolean("FinIsActive"));
				rh.setPromotionCode(rs.getString("PromotionCode"));
				rh.setProductCategory(rs.getString("ProductCategory"));
				rh.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
				rh.setCollectionAgentCode(rs.getString("CollectionAgentCode"));
				rh.setCollectionAgentDesc(rs.getString("CollectionAgentDesc"));
				rh.setPostBranchDesc(rs.getString("PostBranchDesc"));
				rh.setCashierBranchDesc(rs.getString("CashierBranchDesc"));
				rh.setFinDivisionDesc(rs.getString("FinDivisionDesc"));
				rh.setEntityCode(rs.getString("EntityCode"));
				rh.setClosureTypeDesc(rs.getString("ClosureTypeDesc"));

				if (StringUtils.trimToEmpty(type).contains("FView")) {
					rh.setScheduleMethod(rs.getString("ScheduleMethod"));
					rh.setPftDaysBasis(rs.getString("PftDaysBasis"));
					rh.setCustID(rs.getLong("CustID"));
					rh.setCustomerCIF(rs.getString("CustomerCIF"));
					rh.setCustomerName(rs.getString("CustomerName"));
					rh.setCustBaseCcy(rs.getString("CustBaseCcy"));
					rh.setFinTDSApplicable(rs.getBoolean("FinTDSApplicable"));
				}

				if (StringUtils.trimToEmpty(type).contains("FEView")
						|| StringUtils.trimToEmpty(type).contains("FCView")) {
					rh.setCustID(rs.getLong("CustID"));
					rh.setCustomerCIF(rs.getString("CustomerCIF"));
					rh.setCustomerName(rs.getString("CustomerName"));
				}
			}

			return rh;
		}

	}

	@Override
	public boolean isReceiptsInProcess(String reference, String receiptPurpose, long receiptId, String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) FROM FINRECEIPTHEADER" + type);
		selectSql.append(" WHERE REFERENCE = :REFERENCE AND RECEIPTPURPOSE = :RECEIPTPURPOSE ");
		selectSql.append(" AND ReceiptID <> :ReceiptID  ");
		logger.debug(Literal.SQL + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("REFERENCE", reference);
		source.addValue("RECEIPTPURPOSE", receiptPurpose);
		source.addValue("ReceiptID", receiptId);

		try {
			count = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			count = 0;
		}
		logger.debug(Literal.LEAVING);
		return count > 0 ? true : false;
	}

	@Override
	public FinReceiptHeader getFinTypeByReceiptID(long receiptID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RH.Fintype, FT.FinTypeDesc, FT.FinCcy, CU.CcyDesc From  FinReceiptHeader RH ");
		sql.append(" INNER JOIN RMTFinanceTypes FT ON FT.FinType=RH.FinType ");
		sql.append(" INNER JOIN RMTCurrencies CU on CU.CcyCode=FT.FinCcy ");
		sql.append(" Where RH.ReceiptID = ? ");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptID },
					new RowMapper<FinReceiptHeader>() {
						@Override
						public FinReceiptHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinReceiptHeader frh = new FinReceiptHeader();
							frh.setFinType(rs.getString("Fintype"));
							frh.setFinTypeDesc(rs.getString("FinTypeDesc"));
							frh.setFinCcy(rs.getString("FinCcy"));
							frh.setFinCcyDesc(rs.getString("CcyDesc"));
							return frh;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public int geFeeReceiptCountByExtReference(String reference, String receiptPurpose, String extReference) {
		int count = 0;
		StringBuilder sql = new StringBuilder("Select count(*)  from ");
		sql.append(" FinReceiptHeader where ReceiptPurpose = :ReceiptPurpose And ");
		sql.append(" EXTREFERENCE = :EXTREFERENCE and Reference !=  :Reference ");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptPurpose", receiptPurpose);
		source.addValue("Reference", reference);
		source.addValue("EXTREFERENCE", extReference);
		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION);
		}
		return count;
	}

	@Override
	public boolean isReceiptExists(String reference, String type) {

		logger.debug(Literal.ENTERING);
		boolean isExists = false;
		int count = 0;
		StringBuilder sql = new StringBuilder(" Select COUNT(*)  From FinReceiptHeader");
		sql.append(StringUtils.trim(type));
		sql.append(" Where Reference = :Reference");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		logger.debug(Literal.LEAVING);
		count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		if (count > 0) {
			isExists = true;
		}
		return isExists;
	}

	public List<Long> isDedupReceiptExists(FinServiceInstruction fsi) {
		logger.info("Checking for duplicate receipt...");

		boolean isOnline = StringUtils.isNotBlank(fsi.getTransactionRef());
		StringBuilder sql = new StringBuilder("Select ReceiptID From FinReceiptHeader");
		sql.append(" Where Reference = ? and ValueDate = ? and ReceiptModeStatus = ?");

		if (isOnline) {
			sql.append(" and TransactionRef = ?");
		}
		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, fsi.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(fsi.getValueDate()));
			ps.setString(index++, RepayConstants.PAYSTATUS_REALIZED);
			if (isOnline) {
				ps.setString(index++, fsi.getTransactionRef());
			}

		}, (rs, roNum) -> {
			return rs.getLong(1);
		});

	}

	@Override
	public FinReceiptHeader getNonLanReceiptHeader(long receiptID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptID, ReceiptDate, ReceiptType, RecAgainst, Reference, ReceiptPurpose");
		sql.append(", RcdMaintainSts, DepositDate, ReceiptMode, ExcessAdjustTo, AllocationType, ReceiptAmount");
		sql.append(", EffectSchdMethod, ReceiptModeStatus, RealizationDate, CancelReason, WaviedAmt");
		sql.append(", TotFeeAmount, BounceDate, Remarks, GDRAvailable, ReleaseType, ThirdPartyName");
		sql.append(", ThirdPartyMobileNum, LpiAmount,CashierBranch,InitiateDate,ReceiptSource");
		sql.append(", LinkedTranId, EntityCode, DepositProcess, DepositBranch, LppAmount, GstLpiAmount, GstLppAmount");
		sql.append(", subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId");
		sql.append(",  Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, ExtReference, Module, FinDivision, PostBranch");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CustID, CustCIF, CustShrtName, CancelReasonDesc, ReceiptSourceAcType, ReceiptSourceAcDesc");
			sql.append(", CancelRemarks, CollectionAgentCode, CollectionAgentDesc, PostBranchDesc");
			sql.append(", CashierBranchDesc, EntityDesc, TransactionRef");
		}

		sql.append(" From NonLanFinReceiptHeader");
		sql.append(StringUtils.trim(type));
		sql.append(" Where ReceiptID = ? ");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { receiptID }, (rs, i) -> {
				FinReceiptHeader frh = new FinReceiptHeader();

				frh.setReceiptID(rs.getLong("ReceiptID"));
				frh.setReceiptDate(rs.getTimestamp("ReceiptDate"));
				frh.setReceiptType(rs.getString("ReceiptType"));
				frh.setRecAgainst(rs.getString("RecAgainst"));
				frh.setReference(rs.getString("Reference"));
				frh.setReceiptPurpose(rs.getString("ReceiptPurpose"));
				frh.setRcdMaintainSts(rs.getString("RcdMaintainSts"));
				frh.setDepositDate(rs.getTimestamp("DepositDate"));
				frh.setReceiptMode(rs.getString("ReceiptMode"));
				frh.setExcessAdjustTo(rs.getString("ExcessAdjustTo"));
				frh.setAllocationType(rs.getString("AllocationType"));
				frh.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
				frh.setEffectSchdMethod(rs.getString("EffectSchdMethod"));
				frh.setReceiptModeStatus(rs.getString("ReceiptModeStatus"));
				frh.setRealizationDate(rs.getTimestamp("RealizationDate"));
				frh.setCancelReason(rs.getString("CancelReason"));
				frh.setWaviedAmt(rs.getBigDecimal("WaviedAmt"));
				frh.setTotFeeAmount(rs.getBigDecimal("TotFeeAmount"));
				frh.setBounceDate(rs.getTimestamp("BounceDate"));
				frh.setRemarks(rs.getString("Remarks"));
				frh.setGDRAvailable(rs.getBoolean("GDRAvailable"));
				frh.setReleaseType(rs.getString("ReleaseType"));
				frh.setThirdPartyName(rs.getString("ThirdPartyName"));
				frh.setThirdPartyMobileNum(rs.getString("ThirdPartyMobileNum"));
				frh.setLpiAmount(rs.getBigDecimal("LpiAmount"));
				frh.setCashierBranch(rs.getString("CashierBranch"));
				frh.setInitiateDate(rs.getTimestamp("InitiateDate"));
				frh.setReceiptSource(rs.getString("ReceiptSource"));
				frh.setLinkedTranId(rs.getLong("LinkedTranId"));
				frh.setEntityCode(rs.getString("EntityCode"));
				frh.setDepositProcess(rs.getBoolean("DepositProcess"));
				frh.setDepositBranch(rs.getString("DepositBranch"));
				frh.setLppAmount(rs.getBigDecimal("LppAmount"));
				frh.setGstLpiAmount(rs.getBigDecimal("GstLpiAmount"));
				frh.setGstLppAmount(rs.getBigDecimal("GstLppAmount"));
				frh.setSubReceiptMode(rs.getString("SubReceiptMode"));
				frh.setReceiptChannel(rs.getString("ReceiptChannel"));
				frh.setReceivedFrom(rs.getString("ReceivedFrom"));
				frh.setPanNumber(rs.getString("PanNumber"));
				frh.setCollectionAgentId(rs.getLong("CollectionAgentId"));
				frh.setVersion(rs.getInt("Version"));
				frh.setLastMntOn(rs.getTimestamp("LastMntOn"));
				frh.setLastMntBy(rs.getLong("LastMntBy"));
				frh.setRecordStatus(rs.getString("RecordStatus"));
				frh.setRoleCode(rs.getString("RoleCode"));
				frh.setNextRoleCode(rs.getString("NextRoleCode"));
				frh.setTaskId(rs.getString("TaskId"));
				frh.setNextTaskId(rs.getString("NextTaskId"));
				frh.setRecordType(rs.getString("RecordType"));
				frh.setWorkflowId(rs.getLong("WorkflowId"));
				frh.setExtReference(rs.getString("ExtReference"));
				frh.setModule(rs.getString("Module"));
				frh.setFinDivision(rs.getString("FinDivision"));
				frh.setPostBranch(rs.getString("PostBranch"));
				if (StringUtils.trimToEmpty(type).contains("View")) {
					frh.setCustID(rs.getLong("CustID"));
					frh.setCustCIF(rs.getString("CustCIF"));
					frh.setCustShrtName(rs.getString("CustShrtName"));
					frh.setCancelReasonDesc(rs.getString("CancelReasonDesc"));
					frh.setReceiptSourceAcType(rs.getString("ReceiptSourceAcType"));
					frh.setReceiptSourceAcDesc(rs.getString("ReceiptSourceAcDesc"));
					frh.setCancelRemarks(rs.getString("CancelRemarks"));
					frh.setCollectionAgentCode(rs.getString("CollectionAgentCode"));
					frh.setCollectionAgentDesc(rs.getString("CollectionAgentDesc"));
					frh.setPostBranchDesc(rs.getString("PostBranchDesc"));
					frh.setCashierBranchDesc(rs.getString("CashierBranchDesc"));
					frh.setEntityDesc(rs.getString("EntityDesc"));
					frh.setTransactionRef(rs.getString("TransactionRef"));
				}

				return frh;
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public long getCollectionAgencyId(String collectionAgency) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID");
		sql.append(" from COLLECTION_AGENCIES");
		sql.append(" where Code = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { collectionAgency }, (rs, i) -> {
				return rs.getLong("ID");
			});

		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in COLLECTION_AGENCIES table for the specified Code >> {}",
					collectionAgency);
		}

		return 0;
	}

	@Override
	public void updateCollectionMobAgencyLimit(ReceiptAPIRequest request) {
		StringBuilder sql = new StringBuilder("Update COLLECTION_MOB_AGENCY_LIMITS");
		sql.append(" set Status = ?, Response_Code = ?, ReTry_Count = ?, ReTry_On = ?");
		sql.append(" Where Receipt_Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setString(index++, request.getStatus());
					ps.setString(index++, request.getResponseCode());
					ps.setInt(index++, ((request.getRetryCount()) + 1));
					ps.setDate(index++, JdbcUtil.getDate(request.getRetryOn()));
					ps.setLong(index++, request.getReceiptId());

				}
			});
		} catch (Exception e) {
			logger.warn(Literal.ENTERING, e);
		}
	}

	@Override
	public long saveCollectionAPILog(ReceiptAPIRequest request) {
		StringBuilder sql = new StringBuilder("Insert Into COLLECTION_MOB_AGENCY_LIMITS");
		sql.append(" (Receipt_Id, Message_Id, Request_Time, Status, Response_Code, ReTry_Count)");
		sql.append(" Values(?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			this.jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					int index = 1;

					ps.setLong(index++, request.getReceiptId());
					ps.setLong(index++, request.getMessageId());
					ps.setDate(index++, JdbcUtil.getDate(request.getRequestTime()));
					ps.setString(index++, request.getStatus());
					ps.setString(index++, request.getResponseCode());
					ps.setInt(index++, request.getRetryCount());
					return ps;

				}
			}, keyHolder);

			return (long) (keyHolder.getKeys().get("id"));

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return 0;
	}

	@Override
	public List<ReceiptAPIRequest> getCollectionAPILog() {
		StringBuilder sql = new StringBuilder("Select ");
		sql.append(" ID, Receipt_Id, Message_Id, Request_Time, Status, Response_Code, ");
		sql.append(" Retry_Count, Retry_On from COLLECTION_MOB_AGENCY_LIMITS ");
		sql.append(" where Retry_Count < ? And Status = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setInt(1, 10);
			ps.setString(2, "F");

		}, (rs, i) -> {
			ReceiptAPIRequest request = new ReceiptAPIRequest();

			request.setID(rs.getLong("ID"));
			request.setReceiptId(rs.getLong("Receipt_Id"));
			request.setMessageId(rs.getLong("Message_Id"));
			request.setRequestTime(rs.getDate("Request_Time"));
			request.setStatus(rs.getString("Status"));
			request.setResponseCode(rs.getString("Response_Code"));
			request.setRetryCount(rs.getInt("Retry_Count"));
			request.setRetryOn(rs.getDate("Retry_On"));
			return request;
		});
	}

	@Override
	public List<FinReceiptHeader> getReceiptHeaderByID(String reference, String receiptPurpose, Date startDate,
			Date endDate, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Reference = ? and ReceiptPurpose = ? and ReceiptDate >= ? and ReceiptDate <= ?");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		return this.jdbcOperations.query(sql.toString(), new Object[] { reference, receiptPurpose, startDate, endDate },
				rowMapper);

	}
}