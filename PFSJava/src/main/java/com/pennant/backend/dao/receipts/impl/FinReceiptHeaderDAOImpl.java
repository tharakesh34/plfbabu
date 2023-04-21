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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.applicationmaster.ClosureType;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.ReceiptAPIRequest;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

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
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Reference = ? and RcdMaintainSts = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		return this.jdbcOperations.query(sql.toString(), rowMapper, finReference, rcdMaintainSts);
	}

	@Override
	public long save(FinReceiptHeader rh, TableType tableType) {
		if (rh.getId() == 0 || rh.getId() == Long.MIN_VALUE) {
			rh.setId(getNextValue("SeqFinReceiptHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinReceiptHeader").append(tableType.getSuffix());
		sql.append("(ReceiptID, ReceiptDate, ReceiptType, RecAgainst, FinID, Reference, ReceiptPurpose");
		sql.append(", RcdMaintainSts, ReceiptMode, ExcessAdjustTo, AllocationType, ReceiptAmount, EffectSchdMethod");
		sql.append(", ReceiptModeStatus, RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate, Remarks");
		sql.append(", GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount, CashierBranch");
		sql.append(", InitiateDate, DepositProcess, DepositBranch, LppAmount, GstLpiAmount, GstLppAmount");
		sql.append(", ExtReference, Module, SubReceiptMode, ReceiptChannel, ReceivedFrom, PanNumber");
		sql.append(", CollectionAgentId, ActFinReceipt, FinDivision, PostBranch");
		sql.append(", ReasonCode, CancelRemarks, KnockOffType");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, RefWaiverAmt, Source, ValueDate, TransactionRef, DepositDate");
		sql.append(", PartnerBankId, PrvReceiptPurpose, ReceiptSource, RecAppDate, ReceivedDate");
		sql.append(", ClosureTypeId, SourceofFund, TdsAmount, EntityCode, BankCode");
		sql.append(", ToState, FromState, FinType, CustBankId, ModuleType");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, rh.getReceiptID());
			ps.setDate(index++, JdbcUtil.getDate(rh.getReceiptDate()));
			ps.setString(index++, rh.getReceiptType());
			ps.setString(index++, rh.getRecAgainst());
			ps.setObject(index++, JdbcUtil.getLong(rh.getFinID()));
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
			ps.setLong(index++, rh.getCollectionAgentId());
			ps.setBoolean(index++, rh.isActFinReceipt());
			ps.setString(index++, rh.getFinDivision());
			ps.setString(index++, rh.getPostBranch());
			ps.setObject(index++, rh.getReasonCode());
			ps.setString(index++, rh.getCancelRemarks());
			ps.setString(index++, rh.getKnockOffType());
			ps.setInt(index++, rh.getVersion());
			ps.setTimestamp(index++, rh.getLastMntOn());
			ps.setLong(index++, rh.getLastMntBy());
			ps.setString(index++, rh.getRecordStatus());
			ps.setString(index++, rh.getRoleCode());
			ps.setString(index++, rh.getNextRoleCode());
			ps.setString(index++, rh.getTaskId());
			ps.setString(index++, rh.getNextTaskId());
			ps.setString(index++, rh.getRecordType());
			ps.setLong(index++, rh.getWorkflowId());
			ps.setBigDecimal(index++, rh.getRefWaiverAmt());
			ps.setString(index++, rh.getSource());
			ps.setDate(index++, JdbcUtil.getDate(rh.getValueDate()));
			ps.setString(index++, rh.getTransactionRef());
			ps.setDate(index++, JdbcUtil.getDate(rh.getDepositDate()));
			ps.setObject(index++, rh.getPartnerBankId());
			ps.setString(index++, rh.getPrvReceiptPurpose());
			ps.setString(index++, rh.getReceiptSource());
			ps.setDate(index++, JdbcUtil.getDate(rh.getRecAppDate()));
			ps.setDate(index++, JdbcUtil.getDate(rh.getReceivedDate()));
			ps.setObject(index++, rh.getClosureTypeId());
			ps.setString(index++, rh.getSourceofFund());
			ps.setBigDecimal(index++, rh.getTdsAmount());
			ps.setString(index++, rh.getEntityCode());
			ps.setString(index++, rh.getBankCode());
			ps.setString(index++, rh.getToState());
			ps.setString(index++, rh.getFromState());
			ps.setString(index++, rh.getFinType());
			ps.setObject(index++, rh.getCustBankId());
			ps.setString(index, rh.getModuleType());
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
		sql.append(", SourceofFund = ?, TdsAmount = ?, EntityCode = ?, CustBankId = ?");
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
			ps.setObject(index++, rh.getReasonCode());
			ps.setString(index++, rh.getCancelRemarks());
			ps.setString(index++, rh.getKnockOffType());
			ps.setBigDecimal(index++, rh.getRefWaiverAmt());
			ps.setString(index++, rh.getSource());
			ps.setDate(index++, JdbcUtil.getDate(rh.getValueDate()));
			ps.setString(index++, rh.getTransactionRef());
			ps.setDate(index++, JdbcUtil.getDate(rh.getDepositDate()));
			ps.setObject(index++, JdbcUtil.getLong(rh.getPartnerBankId()));
			ps.setString(index++, rh.getPrvReceiptPurpose());
			ps.setString(index++, rh.getReceiptSource());
			ps.setDate(index++, JdbcUtil.getDate(rh.getRecAppDate()));
			ps.setDate(index++, JdbcUtil.getDate(rh.getReceivedDate()));
			ps.setString(index++, rh.getExtReference());
			ps.setObject(index++, rh.getClosureTypeId());
			ps.setString(index++, rh.getSourceofFund());
			ps.setBigDecimal(index++, rh.getTdsAmount());
			ps.setString(index++, rh.getEntityCode());
			ps.setObject(index++, rh.getCustBankId());

			ps.setLong(index, rh.getReceiptID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinReceiptHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), receiptID);
	}

	@Override
	public FinReceiptHeader getReceiptHeaderByID(long receiptID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, receiptID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int geFeeReceiptCount(String reference, String receiptPurpose, long receiptId) {
		StringBuilder sql = new StringBuilder("Select count(ReceiptID) From (");
		sql.append(" select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus From FinReceiptHeader union all ");
		sql.append(" select ReceiptID, Reference,ReceiptPurpose,ReceiptModeStatus From FinReceiptHeader_Temp) T");
		sql.append(" Where ReceiptID <> ? and Reference = ?");
		sql.append(" and ReceiptPurpose = ? and ReceiptModeStatus in(?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, receiptId, reference, receiptPurpose,
				"A", "F");
	}

	@Override
	public FinReceiptHeader getServicingFinanceHeader(long receiptID, String userRole, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID = ? and NextRoleCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, receiptID, userRole);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long generatedReceiptID(FinReceiptHeader rch) {
		if (rch.getId() == 0 || rch.getId() == Long.MIN_VALUE) {
			rch.setId(getNextValue("SeqFinReceiptHeader"));
		}
		return rch.getId();
	}

	@Override
	public void updateDepositProcessByReceiptID(long receiptID, boolean depositProcess, String type) {
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(type);
		sql.append(" Set DepositProcess = ?");
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setBoolean(1, depositProcess);
			ps.setLong(2, receiptID);

		});
	}

	@Override
	public void updateDepositBranchByReceiptID(long receiptID, String depositBranch, String type) {
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(type);
		sql.append(" Set DepositBranch = ?");
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, depositBranch);
			ps.setLong(2, receiptID);

		});
	}

	@Override
	public BigDecimal getTotalCashReceiptAmount(String depositBranch, String type) {
		StringBuilder sql = new StringBuilder("Select sum(Amount)");
		sql.append(" From FinReceiptDetail");
		sql.append(" Where PaymentType = ? and ReceiptId In (Select ReceiptId From FinReceiptHeader");
		sql.append(type);
		sql.append(" Where ReceiptModeStatus = ? and RecordType != ? and DepositBranch = ?)");

		logger.debug(Literal.SQL + sql.toString());

		Object[] parameters = new Object[] { ReceiptMode.CASH, RepayConstants.PAYSTATUS_CANCEL,
				PennantConstants.RECORD_TYPE_NEW, depositBranch };

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, parameters);
	}

	@Override
	public boolean isReceiptCancelProcess(String depositBranch, List<String> paymentTypes, String type,
			long receiptId) {

		StringBuilder sql = new StringBuilder("Select count(ReceiptId)");
		sql.append(" From FinReceiptDetail");
		sql.append(" Where PaymentType in (");
		sql.append(JdbcUtil.getInCondition(paymentTypes));
		sql.append(")");
		sql.append(" and ReceiptId In (Select ReceiptId From FinReceiptHeader").append(type);
		sql.append(" Where ReceiptModeStatus = ? and RecordType != ? and DepositBranch = ? and ReceiptId = ?)");

		logger.debug(Literal.SQL + sql.toString());

		Object[] parameters = new Object[paymentTypes.size() + 4];

		int i = 0;
		for (String paymentType : paymentTypes) {
			parameters[i++] = paymentType;
		}

		parameters[i++] = RepayConstants.PAYSTATUS_CANCEL;
		parameters[i++] = PennantConstants.RECORD_TYPE_NEW;
		parameters[i++] = depositBranch;
		parameters[i] = receiptId;

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, parameters) > 0;
	}

	@Override
	public List<FinReceiptHeader> getUpFrontReceiptHeaderByID(List<Long> receipts, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ReceiptID IN (");
		sql.append(JdbcUtil.getInCondition(receipts));
		sql.append(")  and Reference is null ");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (Long receiptId : receipts) {
				ps.setLong(index++, receiptId);
			}
		}, rowMapper);
	}

	@Override
	public void updateReference(String extReference, String finReference, String type) {
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(type);
		sql.append(" Set Reference = ?");
		sql.append(" Where ExtReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), finReference, extReference);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public List<FinReceiptHeader> getUpFrontReceiptHeaderByExtRef(String extRef, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ExtReference = ? and Reference is null ");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, extRef);
		}, rowMapper);
	}

	@Override
	public Date getMaxReceiptDateByRef(long finID) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select max(ReceiptDate) From FinReceiptHeader");
		sql.append(" Where FinID = ? and ReceiptModeStatus not in (?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Date.class, finID, "B", "C");
	}

	@Override
	public void cancelReceipts(String reference) {
		StringBuilder sql = new StringBuilder("Update FinReceiptHeader");
		sql.append(" Set ReceiptModeStatus = ?  Where Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), "C", reference);
	}

	@Override
	public List<Long> fetchReceiptIdList(String reference) {
		boolean upfrontFeeRevReq = SysParamUtil.isAllowed(SMTParameterConstants.UPFRONT_FEE_REVERSAL_REQ);
		boolean rpyPostingsRevReq = SysParamUtil
				.isAllowed(SMTParameterConstants.REPAY_POSTNGS_REVERSAL_REQ_IN_LOAN_CANCEL);

		StringBuilder sql = new StringBuilder("Select ReceiptID From FinreceiptHeader Where Reference = ?");

		int index = 1;
		if (!upfrontFeeRevReq) {
			sql.append(" and ReceiptPurpose != ?");
			index++;
		}

		if (!rpyPostingsRevReq) {
			sql.append(" and ReceiptPurpose != ?");
			index++;
		}

		Object[] parameters = new Object[index];

		int i = 0;

		parameters[i++] = reference;

		if (!upfrontFeeRevReq) {
			parameters[i++] = FinServiceEvent.FEEPAYMENT;
		}

		if (!rpyPostingsRevReq) {
			parameters[i] = FinServiceEvent.SCHDRPY;
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), Long.class, parameters);

	}

	@Override
	public boolean isExtRefAssigned(String extReference) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder(" Select COUNT(Reference)  From FinReceiptHeader frh");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = frh.Reference");
		sql.append(" Where ExtReference = ?  and Reference is not null");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, extReference) > 0;
	}

	// FIXME Move to PresentmentDetailDAO
	@Override
	public boolean checkInProcessPresentments(long finID) {
		StringBuilder sql = new StringBuilder("Select count(FinID)");
		sql.append(" From PresentmentDetails ");
		sql.append("Where Id in (Select PresentmentId From FinScheduleDetails Where FinID = ? and presentmentId != ?)");
		sql.append(" and Status in (?) and FinID = ? and Excludereason = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID, 0, "A", finID, 0) > 0;
	}

	// FIXME Move to PresentmentDetailDAO
	@Override
	public boolean checkPresentmentsInQueue(long finID) {
		StringBuilder sql = new StringBuilder("Select count(FinID)");
		sql.append(" From PresentmentDetails ");
		sql.append("Where Id in (Select PresentmentId From FinScheduleDetails Where FinID = ? and presentmentId != ?)");
		sql.append(" and Status in (?, ?) and FinID = ? and Excludereason = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID, 0, "A", "I", finID, 0) > 0;
	}

	@Override
	public List<FinReceiptHeader> getReceiptHeadersByRef(String reference, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where Reference = ?");
		sql.append(" order by ReceiptDate, ReceiptID");

		logger.debug(Literal.SQL + sql.toString());

		FinReceiptHeaderRowMaper rowMapper = new FinReceiptHeaderRowMaper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, reference);
		}, rowMapper);
	}

	@Override
	public boolean checkInProcessReceipts(long finID, long receiptId) {
		String sql = "Select count(ReceiptID) From FinReceiptHeader_Temp Where FinID = ? and ReceiptId <> ?";

		logger.debug(Literal.SQL + sql);

		int count = this.jdbcOperations.queryForObject(sql, Integer.class, finID, receiptId);

		if (count == 0) {
			sql = "Select count(ReceiptID) From FinReceiptHeader Where FinID= ? and ReceiptModeStatus in (?, ?)";

			logger.debug(Literal.SQL + sql);

			count = this.jdbcOperations.queryForObject(sql, Integer.class, finID, "I", "D");
		}
		return count > 0;
	}

	@Override
	public boolean isReceiptDetailsExits(String reference, String receiptMode, String chequeNo, String favourNumber,
			String type) {

		StringBuilder sql = new StringBuilder("Select count(rch.ReceiptID)");
		sql.append(" From FinReceiptHeader").append(type).append(" rch");
		sql.append(" Inner join FinReceiptDetail").append(type).append(" rcd");
		sql.append(" on rcd.ReceiptId = rch.ReceiptId");
		sql.append(" Where Reference = ? and ReceiptMode = ? and rcd.FavourNumber = ? and rcd.ChequeacNo = ?");
		sql.append(" and (rch.ReceiptModeStatus in (?) or rch.ReceiptModeStatus is null)");

		logger.debug(Literal.SQL + sql.toString());

		Object[] parameters = new Object[] { reference, receiptMode, favourNumber, chequeNo, "A" };

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, parameters) > 0;
	}

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
	public String getReceiptModeStatus(long receiptID) {
		String sql = "Select ReceiptModeStatus From FinReceiptHeader Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, receiptID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return "";
		}
	}

	@Override
	public List<Long> getInProcessReceiptId(String reference) {
		String sql = "Select ReceiptID From FinReceiptHeader Where Reference = ? and ReceiptModeStatus = ? and receiptPurpose = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForList(sql, Long.class, reference, "D", "SchdlRepayment");
	}

	@Override
	public void updateLoanInActive(long receiptId) {
		String sql = "Update FinReceiptHeader Set LoanActive= ? Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, 0, receiptId);
	}

	@Override
	public void saveMultiReceipt(FinReceiptHeader rch, FinReceiptDetail rcd, Map<String, String> valueMap) {
		StringBuilder sql = new StringBuilder("Insert into MultiReceiptApproval");
		sql.append(" (BatchId, ReceiptModeStatus, BounceDate, RealizationDate, Remarks, CancelReason, ReceiptID");
		sql.append(", DepositDate, ReceiptDate, FinReference, Stage, DepositNo, FundingAc, BounceId, UploadStatus");
		sql.append(", Reason, CancelRemarks)");
		sql.append(" Values(?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {

			int index = 1;

			ps.setLong(index++, rch.getBatchId());
			ps.setString(index++, rch.getReceiptModeStatus());
			ps.setDate(index++, JdbcUtil.getDate(rch.getBounceDate()));
			ps.setDate(index++, JdbcUtil.getDate(rch.getRealizationDate()));
			ps.setString(index++, rch.getRemarks());
			ps.setString(index++, rch.getCancelReason());
			ps.setLong(index++, rch.getReceiptID());
			ps.setDate(index++, JdbcUtil.getDate(rch.getDepositDate()));
			ps.setDate(index++, JdbcUtil.getDate(rch.getReceiptDate()));
			ps.setString(index++, rch.getReference());

			if (StringUtils.isNotBlank(rch.getRoleCode()) && rch.getRoleCode().contains("MAKER")) {
				ps.setString(index++, "M");
			} else {
				ps.setString(index++, "C");
			}
			ps.setString(index++, rcd.getDepositNo());
			ps.setLong(index++, rcd.getFundingAc());
			ps.setLong(index++, rch.getBounceReason());
			ps.setString(index++, valueMap.get("uploadStatus"));
			ps.setString(index++, valueMap.get("reason"));
			ps.setString(index, rch.getCancelRemarks());

		});
	}

	// FIXME Move to FinReceiptQueueLogDAO
	@Override
	public void saveMultiReceiptLog(List<FinReceiptQueueLog> rqlList) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into FinReceiptQueueLog (UploadId, ReceiptId, FinReference");
		sql.append(", TransactionDate, ThreadId, Progress, StartTime)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinReceiptQueueLog rql = rqlList.get(i);

				int index = 1;

				ps.setLong(index++, rql.getUploadId());
				ps.setLong(index++, rql.getReceiptId());
				ps.setString(index++, rql.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(rql.getTransactionDate()));
				ps.setLong(index++, rql.getThreadId());
				ps.setInt(index++, rql.getProgress());
				ps.setString(index, rql.getStartTime());

			}

			@Override
			public int getBatchSize() {
				return rqlList.size();
			}
		});
	}

	// FIXME Move to FinReceiptQueueLogDAO
	@Override
	public void updateMultiReceiptLog(FinReceiptQueueLog rql) {
		List<FinReceiptQueueLog> rqlList = new ArrayList<>();

		rqlList.add(rql);

		batchUpdateMultiReceiptLog(rqlList);
	}

	// FIXME Move to FinReceiptQueueLogDAO
	@Override
	public void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> rqlList) {
		String sql = "Update FinReceiptQueueLog Set EndTime = ?, ThreadId = ?, StartTime = ?, ErrorLog = ? , Progress = ? Where UploadId = ? and ReceiptId = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinReceiptQueueLog rql = rqlList.get(i);

				int index = 1;

				ps.setString(index++, rql.getEndTime());
				ps.setLong(index++, rql.getThreadId());
				ps.setString(index++, rql.getStartTime());
				ps.setString(index++, rql.getErrorLog());
				ps.setInt(index++, rql.getProgress());

				ps.setLong(index++, rql.getUploadId());
				ps.setLong(index, rql.getReceiptId());
			}

			@Override
			public int getBatchSize() {
				return rqlList.size();
			}
		});
	}

	// FIXME Move to FinReceiptQueueLogDAO
	@Override
	public List<Long> getInProcessMultiReceiptRecord() {
		String sql = "Select ReceiptId From FinReceiptQueueLog Where Progress = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> ps.setInt(1, 0), (rs, rowNum) -> {
			return JdbcUtil.getLong(rs.getObject(1));
		});
	}

	@Override
	public String getLoanReferenc(String reference, String receiptFileName) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" distinct Reference From ReceiptUploadDetails");
		sql.append(" Where UploadHeaderId in (Select UploadHeaderId From ReceiptUploadHeader_View");
		sql.append(" Where FileName not in (?) and UploadProgress in (?, ?))");
		sql.append(" and Reference = ? and uploadstatus in (?)");

		logger.debug(Literal.SQL + sql.toString());

		Object[] parameters = new Object[] { receiptFileName, ReceiptUploadConstants.RECEIPT_DEFAULT,
				ReceiptUploadConstants.RECEIPT_DOWNLOADED, reference, PennantConstants.UPLOAD_STATUS_SUCCESS };

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isReceiptsInProcess(String reference, String receiptPurpose, long receiptId, String type) {
		StringBuilder sql = new StringBuilder("Select count(ReceiptId) From FinReceiptHeader" + type);
		sql.append(" Where Reference = ? and ReceiptPurpose = ? and ReceiptID <> ?");

		logger.debug(Literal.SQL + sql.toString());

		Object[] parameters = new Object[] { reference, receiptPurpose, receiptId };

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, parameters) > 0;
	}

	@Override
	public FinReceiptHeader getFinTypeByReceiptID(long receiptID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" rch.Fintype, ft.FinTypeDesc, ft.FinCcy, cu.CcyDesc");
		sql.append(" From FinReceiptHeader rch ");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = rch.FinType");
		sql.append(" Inner Join RMTCurrencies cu on cu.CcyCode = ft.FinCcy");
		sql.append(" Where rch.ReceiptID = ? ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinReceiptHeader frh = new FinReceiptHeader();
				frh.setFinType(rs.getString("Fintype"));
				frh.setFinTypeDesc(rs.getString("FinTypeDesc"));
				frh.setFinCcy(rs.getString("FinCcy"));
				frh.setFinCcyDesc(rs.getString("CcyDesc"));
				return frh;
			}, receiptID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int geFeeReceiptCountByExtReference(String reference, String receiptPurpose, String extReference) {
		String sql = "Select count(ReceiptID) From FinReceiptHeader Where ReceiptPurpose = ? and ExtReference = ? and Reference !=  ?";

		logger.debug(Literal.SQL + sql);

		Object[] parameters = new Object[] { receiptPurpose, extReference, reference };

		return this.jdbcOperations.queryForObject(sql, Integer.class, parameters);
	}

	@Override
	public boolean isReceiptExists(String reference, String type) {
		StringBuilder sql = new StringBuilder("Select count(ReceiptID) From FinReceiptHeader");
		sql.append(StringUtils.trim(type));
		sql.append(" Where Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, reference) > 0;
	}

	public List<Long> isDedupReceiptExists(FinServiceInstruction fsi) {
		boolean isOnline = StringUtils.isNotBlank(fsi.getTransactionRef());
		boolean isChequeOrDD = StringUtils.isNotBlank(fsi.getFavourNumber());

		StringBuilder sql = new StringBuilder("Select ReceiptID From FinReceiptHeader");
		sql.append(" Where Reference = ? and ValueDate = ? and ReceiptModeStatus = ?");
		sql.append(" and ReceiptAmount = ?");

		if (isOnline || isChequeOrDD) {
			sql.append(" and TransactionRef = ?");
		}
		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, fsi.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(fsi.getValueDate()));
			ps.setString(index++, RepayConstants.PAYSTATUS_REALIZED);
			ps.setBigDecimal(index++, fsi.getAmount());
			if (isOnline) {
				ps.setString(index, fsi.getTransactionRef());
			} else if (isChequeOrDD) {
				ps.setString(index, fsi.getFavourNumber());
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

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
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
				frh.setLinkedTranId(JdbcUtil.getLong(rs.getObject("LinkedTranId")));
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
			}, receiptID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getCollectionAgencyId(String collectionAgency) {
		String sql = "Select ID From Collection_Agencies Where Code = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, collectionAgency);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public void updateCollectionMobAgencyLimit(ReceiptAPIRequest request) {
		StringBuilder sql = new StringBuilder("Update Collection_Mob_Aagency_Limits");
		sql.append(" set Status = ?, Response_Code = ?, ReTry_Count = ?, ReTry_On = ?");
		sql.append(" Where Receipt_Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setString(index++, request.getStatus());
				ps.setString(index++, request.getResponseCode());
				ps.setInt(index++, ((request.getRetryCount()) + 1));
				ps.setDate(index++, JdbcUtil.getDate(request.getRetryOn()));
				ps.setLong(index, request.getReceiptId());

			}
		});
	}

	@Override
	public long saveCollectionAPILog(ReceiptAPIRequest request) {
		StringBuilder sql = new StringBuilder("Insert Into Collection_Mob_Aagency_Limits");
		sql.append(" (Receipt_Id, Message_Id, Request_Time, Status, Response_Code, ReTry_Count)");
		sql.append(" Values(?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

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
				ps.setInt(index, request.getRetryCount());
				return ps;

			}
		}, keyHolder);

		return (long) (keyHolder.getKeys().get("id"));
	}

	@Override
	public List<ReceiptAPIRequest> getCollectionAPILog() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, Receipt_Id, Message_Id, Request_Time, Status, Response_Code, ");
		sql.append(" Retry_Count, Retry_On From Collection_Mob_Aagency_Limits ");
		sql.append(" Where Retry_Count < ? and Status = ?");

		logger.debug(Literal.SQL + sql.toString());

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

		return this.jdbcOperations.query(sql.toString(), rowMapper,
				new Object[] { reference, receiptPurpose, startDate, endDate });

	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptID, ReceiptDate, ReceiptType, RecAgainst");
		sql.append(", FinID, Reference, ReceiptPurpose, RcdMaintainSts");
		sql.append(", InstructionUID, ReceiptMode, ExcessAdjustTo, AllocationType, ReceiptAmount, EffectSchdMethod");
		sql.append(", ReceiptModeStatus, RealizationDate, CancelReason, WaviedAmt, TotFeeAmount, BounceDate");
		sql.append(", Remarks, GDRAvailable, ReleaseType, ThirdPartyName, ThirdPartyMobileNum, LpiAmount");
		sql.append(", CashierBranch, InitiateDate, DepositProcess, DepositBranch, LppAmount, GstLpiAmount");
		sql.append(", GstLppAmount, subReceiptMode, receiptChannel, receivedFrom, panNumber, collectionAgentId");
		sql.append(", ExtReference, Module, FinDivision, PostBranch, ActFinReceipt, ReasonCode, CancelRemarks");
		sql.append(", RefWaiverAmt, Source, ValueDate, TransactionRef, DepositDate, PartnerBankId");
		sql.append(", KnockOffType, PrvReceiptPurpose, ReceiptSource");
		sql.append(", RecAppDate, ReceivedDate, ClosureTypeId, SourceofFund, TdsAmount");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, CustBankId");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinType, FinCcy, FinBranch, CustCIF, CustShrtName, FinTypeDesc");
			sql.append(", FinCcyDesc, FinBranchDesc, CancelReasonDesc, FinIsActive, PromotionCode, ProductCategory");
			sql.append(", NextRepayRvwDate, CollectionAgentCode, CollectionAgentDesc, PostBranchDesc");
			sql.append(", CashierBranchDesc, FinDivisionDesc, EntityCode, ClosureTypeDesc");
			sql.append(", CustAcctNumber, CustAcctHolderName, ScheduleMethod, PftDaysBasis");

			if (StringUtils.trimToEmpty(type).contains("FView")) {
				sql.append(", ScheduleMethod, PftDaysBasis, CustID, CustomerCIF");
				sql.append(", CustomerName, CustBaseCcy, FinTDSApplicable");
			}

			if (StringUtils.trimToEmpty(type).contains("FEView") || StringUtils.trimToEmpty(type).contains("FCView")) {
				sql.append(", CustID, CustomerCIF, CustomerName");
			}
		}

		sql.append(" From FinReceiptHeader");
		sql.append(StringUtils.trim(type));
		return sql;
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
			rh.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
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
			rh.setReasonCode(JdbcUtil.getLong(rs.getObject("ReasonCode")));
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
			rh.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			rh.setPrvReceiptPurpose(rs.getString("PrvReceiptPurpose"));
			rh.setReceiptSource(rs.getString("ReceiptSource"));
			rh.setRecAppDate(rs.getDate("RecAppDate"));
			rh.setReceivedDate(rs.getDate("ReceivedDate"));
			rh.setSourceofFund(rs.getString("SourceofFund"));
			rh.setTdsAmount(rs.getBigDecimal("TdsAmount"));
			rh.setClosureTypeId(JdbcUtil.getLong(rs.getObject("ClosureTypeId")));
			rh.setCustBankId(JdbcUtil.getLong(rs.getObject("CustBankId")));

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
				rh.setCustAcctNumber(rs.getString("CustAcctNumber"));
				rh.setCustAcctHolderName(rs.getString("custAcctHolderName"));
				rh.setScheduleMethod(rs.getString("ScheduleMethod"));
				rh.setPftDaysBasis(rs.getString("PftDaysBasis"));

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
	public List<FinReceiptHeader> getInprocessReceipts(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptID, ValueDate, ReceiptPurpose, ReceiptModeStatus, AllocationType, ReceiptAmount");
		sql.append(" From FinReceiptHeader_Temp");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, rowNum) -> {
			FinReceiptHeader rh = new FinReceiptHeader();

			rh.setReceiptID(rs.getLong("ReceiptID"));
			rh.setValueDate(rs.getDate("ValueDate"));
			rh.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			rh.setReceiptModeStatus(rs.getString("ReceiptModeStatus"));
			rh.setAllocationType(rs.getString("AllocationType"));
			rh.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));

			return rh;
		});
	}

	@Override
	public List<FinReceiptHeader> getLastMntOn(long receiptID) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select  LastMntOn, 1 WorkflowId From FinReceiptHeader_Temp Where ReceiptID = ?");
		sql.append(" Union all");
		sql.append(" Select LastMntOn, 0 WorkflowId From FinReceiptHeader Where ReceiptID = ?");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, receiptID);
			ps.setLong(2, receiptID);
		}, (rs, rowNum) -> {
			FinReceiptHeader rh = new FinReceiptHeader();

			rh.setLastMntOn(rs.getTimestamp("LastMntOn"));
			rh.setWorkflowId(rs.getLong("WorkflowId"));

			return rh;
		});
	}

	@Override
	public ClosureType getClosureType(String closureType) {
		String sql = "Select Id, Code, Description From Closure_Types Where Code = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				ClosureType ct = new ClosureType();

				ct.setId(rs.getLong("Id"));
				ct.setCode(rs.getString("Code"));
				ct.setDescription(rs.getString("Description"));

				return ct;
			}, closureType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinReceiptHeader getReceiptById(long receiptId) {
		String sql = "Select Reference, ReceiptModeStatus From FinReceiptHeader Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinReceiptHeader rch = new FinReceiptHeader();

				rch.setReference(rs.getString("Reference"));
				rch.setReceiptModeStatus(rs.getString("ReceiptModeStatus"));
				return rch;
			}, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinReceiptHeader getInititatedReceipts(String reference, String type, String receiptPurpose) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Reference, ReceiptID, ReceiptDate");
		sql.append(" From FinReceiptHeader");
		sql.append(type);
		sql.append(" Where Reference = ? and Receiptpurpose = ? and ReceiptModeStatus not in (?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				FinReceiptHeader frh = new FinReceiptHeader();

				frh.setReference(rs.getString("Reference"));
				frh.setReceiptID(rs.getLong("ReceiptID"));
				frh.setReceiptDate(rs.getTimestamp("ReceiptDate"));

				return frh;
			}, reference, receiptPurpose, "B", "C");
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getCustCIF(String finRef) {
		StringBuilder sql = new StringBuilder("Select CustCIF From (");
		sql.append(" Select CustCIF From FinReceiptHeader_Temp frh");
		sql.append(" Left Join Customers c on CAST(c.CustID as Varchar(20)) = frh.Reference");
		sql.append(" Where ExtReference = ?");
		sql.append(" Union All");
		sql.append(" Select CustCIF From FinReceiptHeader frh");
		sql.append(" Left Join Customers c on CAST(C.CustID as Varchar(20)) = frh.Reference");
		sql.append(" Where ExtReference = ?");
		sql.append(" and Not Exists (Select 1 From FinReceiptHeader_Temp Where ReceiptID = frh.ReceiptID)");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, finRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isReceiptExistsByOnlineAndMob(String reference, long receiptId) {
		String sql = "Select count(ReceiptId) from FinReceiptHeader where Reference = ? and ReceiptId = ? and ReceiptMode != ? and RECEIPTCHANNEL = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, reference, receiptId, "ONLINE", "MOB") > 0;
	}

	public FinReceiptHeader getReceiptHeader(long receiptId) {
		String sql = "Select ReceiptAmount, ReceiptMode from FinReceiptHeader Where ReceiptId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinReceiptHeader frh = new FinReceiptHeader();

				frh.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
				frh.setReceiptMode(rs.getString("ReceiptMode"));

				return frh;
			}, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isReceiptExists(ReceiptUploadDetail rud, String type) {
		StringBuilder sql = new StringBuilder("Select rh.ReceiptID From FinReceiptHeader");
		sql.append(type);
		sql.append(" rh Inner Join FinReceiptDetail").append(type);
		sql.append(" rd on rd.ReceiptID = rh.ReceiptID");
		sql.append(" Where rh.Reference = ? and rh.ReceiptMode = ? and rd.ChequeAcNo = ? and rh.TransactionRef = ?");
		sql.append("and (rh.ReceiptModeStatus in ('A') or rh.ReceiptModeStatus is null)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, rud.getReference());
			ps.setString(index++, rud.getReceiptMode());
			ps.setString(index++, rud.getChequeNo());
			ps.setString(index, rud.getTransactionRef());

		}, (rs, roNum) -> {
			return rs.getLong(1);
		}).size() > 0;
	}

	@Override
	public boolean isChequeExists(ReceiptUploadDetail rud) {
		StringBuilder sql = new StringBuilder("Select rh.ReceiptID From FinReceiptHeader rh");
		sql.append(" Inner Join FinReceiptDetail rd on rd.ReceiptID = rh.ReceiptID");
		sql.append(" Where rh.Reference = ? and rd.PaymentType = ? and rh.TransactionRef = ?");
		sql.append(" and rd.BankCode = ? and rd.Status not in (?, ?) ");
		sql.append(" Union All");
		sql.append(" Select rh.ReceiptID From FinReceiptHeader_Temp rh");
		sql.append(" Inner Join FinReceiptDetail_Temp rd on rd.ReceiptID = rh.ReceiptID");
		sql.append(" Where rh.Reference = ? and rd.PaymentType = ? and rh.TransactionRef = ?");
		sql.append(" and rd.BankCode = ? and rd.Status not in (?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, rud.getReference());
			ps.setString(index++, rud.getReceiptMode());
			ps.setString(index++, rud.getTransactionRef());
			ps.setString(index++, rud.getBankCode());
			ps.setString(index++, "B");
			ps.setString(index++, "C");

			ps.setString(index++, rud.getReference());
			ps.setString(index++, rud.getReceiptMode());
			ps.setString(index++, rud.getTransactionRef());
			ps.setString(index++, rud.getBankCode());
			ps.setString(index++, "B");
			ps.setString(index, "C");
		}, (rs, rowNum) -> {
			return rs.getLong(1);
		}).size() > 0;
	}

	@Override
	public boolean isOnlineExists(ReceiptUploadDetail rud) {
		StringBuilder sql = new StringBuilder("Select rh.ReceiptID From FinReceiptHeader rh");
		sql.append(" Inner Join FinReceiptDetail rd on rd.ReceiptID = rh.ReceiptID");
		sql.append(" Where rh.Reference = ? and rd.PaymentType = ? and rh.TransactionRef = ?");
		sql.append(" and rd.Status not in (?, ?) ");
		sql.append(" Union All");
		sql.append(" Select rh.ReceiptID From FinReceiptHeader_Temp rh");
		sql.append(" Inner Join FinReceiptDetail_Temp rd on rd.ReceiptID = rh.ReceiptID");
		sql.append(" Where rh.Reference = ? and rd.PaymentType = ? and rh.TransactionRef = ?");
		sql.append(" and rd.Status not in (?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, rud.getReference());
			ps.setString(index++, rud.getReceiptMode());
			ps.setString(index++, rud.getTransactionRef());
			ps.setString(index++, "B");
			ps.setString(index++, "C");

			ps.setString(index++, rud.getReference());
			ps.setString(index++, rud.getReceiptMode());
			ps.setString(index++, rud.getTransactionRef());
			ps.setString(index++, "B");
			ps.setString(index, "C");
		}, (rs, rowNum) -> {
			return rs.getLong(1);
		}).size() > 0;
	}

	@Override
	public int isReceiptExists(String finreference, long receiptId) {
		String sql = "Select Count(ReceiptId) From FinReceiptHeader Where Reference = ? And ReceiptId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, finreference, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public int updateUTRNum(long receiptId, String utrNum, String receiptMode) {
		String sql = "Update FinReceiptDetail set TransactionRef = ? Where ReceiptId = ?";

		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {
			sql = "Update FinReceiptDetail set FavourNumber = ? Where ReceiptId = ?";
		}

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, utrNum);
			ps.setLong(2, receiptId);
		});
	}

	public void updateReceiptHeader(long receiptId, String utrNum) {
		String sql = "Update FinReceiptHeader set TransactionRef = ? Where ReceiptId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, utrNum);
			ps.setLong(2, receiptId);
		});

	}

	@Override
	public String getReceiptMode(long receiptId) {
		String sql = "Select ReceiptMode From FinReceiptHeader Where ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, receiptId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BigDecimal getClosureAmountByFinType(String finType) {
		String sql = "Select ClosureThresholdLimit From RMTFinanceTypes Where FinType = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public List<FinReceiptHeader> getSettlementReceipts(long finID, Date fromDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptId, ReceiptModeStatus From FinReceiptHeader");
		sql.append(" Where FinId = ? and receiptDate <= ? and excessAdjustTo = ? and ReceiptModeStatus  in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinReceiptHeader rch = new FinReceiptHeader();

			rch.setReceiptID(rs.getLong("ReceiptId"));
			rch.setReceiptModeStatus(rs.getString("ReceiptModeStatus"));

			return rch;
		}, finID, JdbcUtil.getDate(fromDate), "S", "R", "D");
	}

	@Override
	public BigDecimal getReceiptAmount(Date fromDate, Date toDate) {
		String sql = "Select sum(ReceiptAmount) From FinReceiptHeader Where ReceiptDate >=  ? and ReceiptDate <= ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, fromDate, toDate);
		} catch (DataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public void updateExcessAdjustTo(long receiptID, String excessAdjustTo) {
		String sql = "Update FinReceiptHeader Set ExcessAdjustTo= ? Where ReceiptID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, excessAdjustTo, receiptID);
	}

	@Override
	public String getReceiptModeStatuByExcessId(long excessID) {
		StringBuilder sql = new StringBuilder("Select ReceiptModeStatus From (");
		sql.append(" Select ReceiptModeStatus From FinReceiptHeader_Temp fh");
		sql.append(" Inner Join FinExcessAmount fa on fa.ReceiptID = fh.ReceiptID");
		sql.append(" Where fa.ExcessId = ?");
		sql.append(" union all");
		sql.append(" Select ReceiptModeStatus From FinReceiptHeader fh");
		sql.append(" Inner Join FinExcessAmount fa on fa.ReceiptID = fh.ReceiptID");
		sql.append(" Where fa.ExcessId = ? and Not Exists");
		sql.append(" (Select 1 From FinReceiptHeader_Temp WHERE ReceiptId = fh.ReceiptId)) T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, excessID, excessID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Long getReceiptIdByChequeSerialNo(String chequeSerialNo) {
		String sql = "Select ReceiptID from FinReceiptHeader where TransactioNRef = ? and ReceiptModeStatus = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, chequeSerialNo, "B");
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}