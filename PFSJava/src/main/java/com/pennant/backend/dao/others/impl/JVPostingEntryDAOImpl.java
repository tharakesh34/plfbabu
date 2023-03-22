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
 * * FileName : JVPostingEntryDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * *
 * Modified Date : 21-06-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.others.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.others.JVPostingEntryDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>JVPostingEntry model</b> class.<br>
 * 
 */
public class JVPostingEntryDAOImpl extends BasicDao<JVPostingEntry> implements JVPostingEntryDAO {
	private static Logger logger = LogManager.getLogger(JVPostingEntryDAOImpl.class);

	public JVPostingEntryDAOImpl() {
		super();
	}

	@Override
	public JVPostingEntry getNewJVPostingEntry() {
		WorkFlowDetails wfd = WorkFlowUtil.getWorkFlowDetails("JVPostingEntry");
		JVPostingEntry jvpe = new JVPostingEntry();

		if (wfd != null) {
			jvpe.setWorkflowId(wfd.getWorkFlowId());
		}

		jvpe.setNewRecord(true);

		return jvpe;
	}

	@Override
	public JVPostingEntry getJVPostingEntryById(final long id, long txnReference, long acEntryRef, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where BatchReference = ? and TxnReference = ? and AcEntryRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		JVPostingEntryRowMapper rowMapper = new JVPostingEntryRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, id, txnReference, acEntryRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<JVPostingEntry> getJVPostingEntryListById(final long id, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where BatchReference = ? and ExternalAccount = ?");

		logger.debug(Literal.SQL + sql.toString());

		JVPostingEntryRowMapper rowMapper = new JVPostingEntryRowMapper(type);

		List<JVPostingEntry> jvpList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, id);
			ps.setInt(index, 1);
		}, rowMapper);

		return jvpList.stream().sorted((j1, j2) -> Long.compare(j1.getTxnReference(), j2.getTxnReference()))
				.collect(Collectors.toList());
	}

	@Override
	public List<JVPostingEntry> getFailureJVPostingEntryListById(final long id, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where BatchReference = ? and PostingStatus ! = ?");

		logger.debug(Literal.SQL + sql.toString());

		JVPostingEntryRowMapper rowMapper = new JVPostingEntryRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, id);
			ps.setString(index, PennantConstants.POSTSTS_SUCCESS);
		}, rowMapper);
	}

	@Override
	public void delete(JVPostingEntry jve, String type) {
		StringBuilder sql = new StringBuilder("Delete From JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BatchReference = ? and TxnReference = ? and AcEntryRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, jve.getBatchReference());
				ps.setLong(index++, jve.getTxnReference());
				ps.setLong(index, jve.getAcEntryRef());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(JVPostingEntry jve, String type) {
		if (jve.getTxnReference() == 0) {
			JVPostingEntry jvpe = getNewJVPostingEntry();
			jvpe.setDaySeqDate(
					DateUtility.getDBDate(DateUtil.format(DateUtility.getSysDate(), PennantConstants.DBDateFormat)));
			jvpe.setDaySeqNo(0);
			jve.setTxnReference(getMaxSeqNumForCurrentDay(jvpe) + 1);

			jvpe.setDaySeqNo((int) jve.getTxnReference());
			upDateSeqNoForCurrentDayBatch(jvpe);

			if (!AccountConstants.TRANTYPE_CREDIT.equals(jve.getTxnEntry())) {
				jve.setDerivedTxnRef(jve.getTxnReference() - 1);
			}
		}

		StringBuilder sql = new StringBuilder("Insert Into JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FileName, BatchReference, AcEntryRef, HostSeqNo, Account, AcType, AccountName, TxnCCy");
		sql.append(", TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1");
		sql.append(", NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac");
		sql.append(", ModifiedFlag, DeletedFlag, ValidationStatus, PostingStatus, ExternalAccount, LinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, DerivedTxnRef, TdsAdjReq)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, jve.getFileName());
			ps.setLong(index++, jve.getBatchReference());
			ps.setLong(index++, jve.getAcEntryRef());
			ps.setString(index++, jve.getHostSeqNo());
			ps.setString(index++, jve.getAccount());
			ps.setString(index++, jve.getAcType());
			ps.setString(index++, jve.getAccountName());
			ps.setString(index++, jve.getTxnCCy());
			ps.setString(index++, jve.getTxnEntry());
			ps.setString(index++, jve.getAccCCy());
			ps.setString(index++, jve.getTxnCode());
			ps.setDate(index++, JdbcUtil.getDate(jve.getPostingDate()));
			ps.setDate(index++, JdbcUtil.getDate(jve.getValueDate()));
			ps.setBigDecimal(index++, jve.getTxnAmount());
			ps.setLong(index++, jve.getTxnReference());
			ps.setString(index++, jve.getNarrLine1());
			ps.setString(index++, jve.getNarrLine2());
			ps.setString(index++, jve.getNarrLine3());
			ps.setString(index++, jve.getNarrLine4());
			ps.setBigDecimal(index++, jve.getExchRate_Batch());
			ps.setBigDecimal(index++, jve.getExchRate_Ac());
			ps.setBigDecimal(index++, jve.getTxnAmount_Batch());
			ps.setBigDecimal(index++, jve.getTxnAmount_Ac());
			ps.setString(index++, jve.getModifiedFlag());
			ps.setBoolean(index++, jve.isDeletedFlag());
			ps.setString(index++, jve.getValidationStatus());
			ps.setString(index++, jve.getPostingStatus());
			ps.setBoolean(index++, jve.isExternalAccount());
			ps.setLong(index++, jve.getLinkedTranId());
			ps.setInt(index++, jve.getVersion());
			ps.setLong(index++, jve.getLastMntBy());
			ps.setTimestamp(index++, jve.getLastMntOn());
			ps.setString(index++, jve.getRecordStatus());
			ps.setString(index++, jve.getRoleCode());
			ps.setString(index++, jve.getNextRoleCode());
			ps.setString(index++, jve.getTaskId());
			ps.setString(index++, jve.getNextTaskId());
			ps.setString(index++, jve.getRecordType());
			ps.setLong(index++, jve.getWorkflowId());
			ps.setLong(index++, jve.getDerivedTxnRef());
			ps.setBoolean(index, jve.isTDSAdjReq());
		});

		return jve.getId();
	}

	@Override
	public void update(JVPostingEntry jve, String type) {
		StringBuilder sql = new StringBuilder("Update JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FileName = ?, HostSeqNo = ?, Account = ?, AcType = ?, AccountName = ?");
		sql.append(", TxnCCy = ?, AccCCy = ?, TxnCode = ?, TxnEntry = ?, PostingDate = ?, ValueDate = ?");
		sql.append(", TxnAmount = ?, NarrLine1 = ?, NarrLine2 = ?, NarrLine3 = ?, NarrLine4 = ?, ExchRate_Batch = ?");
		sql.append(", ExchRate_Ac = ?, TxnAmount_Batch = ?, TxnAmount_Ac = ?, ModifiedFlag = ?, DeletedFlag = ?");
		sql.append(", ValidationStatus = ?, PostingStatus = ?, ExternalAccount = ?, LinkedTranId = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?, TdsAdjReq = ?");
		sql.append(" Where BatchReference = ? and TxnReference = ? and AcEntryRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, jve.getFileName());
			ps.setString(index++, jve.getHostSeqNo());
			ps.setString(index++, jve.getAccount());
			ps.setString(index++, jve.getAcType());
			ps.setString(index++, jve.getAccountName());
			ps.setString(index++, jve.getTxnCCy());
			ps.setString(index++, jve.getAccCCy());
			ps.setString(index++, jve.getTxnCode());
			ps.setString(index++, jve.getTxnEntry());
			ps.setDate(index++, JdbcUtil.getDate(jve.getPostingDate()));
			ps.setDate(index++, JdbcUtil.getDate(jve.getValueDate()));
			ps.setBigDecimal(index++, jve.getTxnAmount());
			ps.setString(index++, jve.getNarrLine1());
			ps.setString(index++, jve.getNarrLine2());
			ps.setString(index++, jve.getNarrLine3());
			ps.setString(index++, jve.getNarrLine4());
			ps.setBigDecimal(index++, jve.getExchRate_Batch());
			ps.setBigDecimal(index++, jve.getExchRate_Ac());
			ps.setBigDecimal(index++, jve.getTxnAmount_Batch());
			ps.setBigDecimal(index++, jve.getTxnAmount_Ac());
			ps.setString(index++, jve.getModifiedFlag());
			ps.setBoolean(index++, jve.isDeletedFlag());
			ps.setString(index++, jve.getValidationStatus());
			ps.setString(index++, jve.getPostingStatus());
			ps.setBoolean(index++, jve.isExternalAccount());
			ps.setLong(index++, jve.getLinkedTranId());
			ps.setInt(index++, jve.getVersion());
			ps.setLong(index++, jve.getLastMntBy());
			ps.setTimestamp(index++, jve.getLastMntOn());
			ps.setString(index++, jve.getRecordStatus());
			ps.setString(index++, jve.getRoleCode());
			ps.setString(index++, jve.getNextRoleCode());
			ps.setString(index++, jve.getTaskId());
			ps.setString(index++, jve.getNextTaskId());
			ps.setString(index++, jve.getRecordType());
			ps.setLong(index++, jve.getWorkflowId());
			ps.setBoolean(index++, jve.isTDSAdjReq());

			ps.setLong(index++, jve.getBatchReference());
			ps.setLong(index++, jve.getTxnReference());
			ps.setLong(index, jve.getAcEntryRef());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateDeleteFlag(JVPostingEntry jve, String type) {
		StringBuilder sql = new StringBuilder("Update JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set DeletedFlag = ?");
		sql.append(" Where BatchReference = ? and TxnReference = ?  and AcEntryRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBoolean(index++, jve.isDeletedFlag());

			ps.setLong(index++, jve.getBatchReference());
			ps.setLong(index++, jve.getTxnReference());
			ps.setLong(index, jve.getAcEntryRef());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateDeletedDetails(JVPostingEntry jve, String type) {
		StringBuilder sql = new StringBuilder("Update JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FileName = ?, HostSeqNo = ?, Account = ?,  AcType = ?, AccountName = ?, TxnCCy = ?");
		sql.append(", AccCCy = ?, TxnCode = ?, TxnEntry = ?, PostingDate = ?, ValueDate = ?, TxnAmount = ?");
		sql.append(", NarrLine1 = ?, NarrLine2 = ?, NarrLine3 = ?, NarrLine4 = ?, ExchRate_Batch = ?, ExchRate_Ac = ?");
		sql.append(", TxnAmount_Batch = ?, TxnAmount_Ac = ?, ModifiedFlag = ?, DeletedFlag = ?, ValidationStatus = ?");
		sql.append(", PostingStatus = ?, ExternalAccount = ?, LinkedTranId = ?");
		sql.append(", LastMntBy = ?, LastMntOn = ?, WorkflowId = ?");
		sql.append(" Where BatchReference = ? and TxnReference = ? and AcEntryRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, jve.getFileName());
			ps.setString(index++, jve.getHostSeqNo());
			ps.setString(index++, jve.getAccount());
			ps.setString(index++, jve.getAcType());
			ps.setString(index++, jve.getAccountName());
			ps.setString(index++, jve.getTxnCCy());
			ps.setString(index++, jve.getAccCCy());
			ps.setString(index++, jve.getTxnCode());
			ps.setString(index++, jve.getTxnEntry());
			ps.setDate(index++, JdbcUtil.getDate(jve.getPostingDate()));
			ps.setDate(index++, JdbcUtil.getDate(jve.getValueDate()));
			ps.setBigDecimal(index++, jve.getTxnAmount());
			ps.setString(index++, jve.getNarrLine1());
			ps.setString(index++, jve.getNarrLine2());
			ps.setString(index++, jve.getNarrLine3());
			ps.setString(index++, jve.getNarrLine4());
			ps.setBigDecimal(index++, jve.getExchRate_Batch());
			ps.setBigDecimal(index++, jve.getExchRate_Ac());
			ps.setBigDecimal(index++, jve.getTxnAmount_Batch());
			ps.setBigDecimal(index++, jve.getTxnAmount_Ac());
			ps.setString(index++, jve.getModifiedFlag());
			ps.setBoolean(index++, jve.isDeletedFlag());
			ps.setString(index++, jve.getValidationStatus());
			ps.setString(index++, jve.getPostingStatus());
			ps.setBoolean(index++, jve.isExternalAccount());
			ps.setLong(index++, jve.getLinkedTranId());
			ps.setLong(index++, jve.getLastMntBy());
			ps.setTimestamp(index++, jve.getLastMntOn());
			ps.setLong(index++, jve.getWorkflowId());

			ps.setLong(index++, jve.getBatchReference());
			ps.setLong(index++, jve.getTxnReference());
			ps.setLong(index, jve.getAcEntryRef());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public void updateWorkFlowDetails(JVPostingEntry jve, String type) {
		StringBuilder sql = new StringBuilder("Update JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set LastMntBy = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, WorkflowId = ?, RecordStatus = ?");
		sql.append(" Where BatchReference = ? and Account = ? and DeletedFlag = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, jve.getLastMntBy());
			ps.setString(index++, jve.getRoleCode());
			ps.setString(index++, jve.getNextRoleCode());
			ps.setString(index++, jve.getTaskId());
			ps.setString(index++, jve.getNextTaskId());
			ps.setLong(index++, jve.getWorkflowId());
			ps.setString(index++, jve.getRecordStatus());

			ps.setLong(index++, jve.getBatchReference());
			ps.setString(index++, jve.getAccount());
			ps.setInt(index, 0);

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByID(JVPostingEntry jve, String tableType) {
		StringBuilder sql = new StringBuilder("Delete From JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where BatchReference = ? and TxnReference = ? and AcEntryRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, jve.getBatchReference());
				ps.setLong(index++, jve.getTxnReference());
				ps.setLong(index, jve.getAcEntryRef());
			});

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public JVPostingEntry getJVPostingEntryById(long batchRef, long txnReference, String account, String txnEntry,
			BigDecimal txnAmount, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FileName, BatchReference, AcEntryRef, HostSeqNo, Account,  AcType, AccountName, TxnCCy");
		sql.append(", TxnEntry, AccCCy, TxnCode, PostingDate, ValueDate, TxnAmount, TxnReference, NarrLine1");
		sql.append(", NarrLine2, NarrLine3, NarrLine4, ExchRate_Batch, ExchRate_Ac, TxnAmount_Batch, TxnAmount_Ac");
		sql.append(", ModifiedFlag, DeletedFlag, ValidationStatus, PostingStatus, ExternalAccount, LinkedTranId");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", TxnCCyName, TxnCCyEditField, TxnDesc, AccCCyName");
			sql.append(", AccCCyEditField, AcCcyNumber, TxnCcyNumber");
		}

		sql.append(" From JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BatchReference = ? and TxnReference = ? and TxnEntry = ? and Account = ? and TxnAmount = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				JVPostingEntry jve = new JVPostingEntry();

				jve.setFileName(rs.getString("FileName"));
				jve.setBatchReference(rs.getLong("BatchReference"));
				jve.setAcEntryRef(rs.getLong("AcEntryRef"));
				jve.setHostSeqNo(rs.getString("HostSeqNo"));
				jve.setAccount(rs.getString("Account"));
				jve.setAcType(rs.getString("AcType"));
				jve.setAccountName(rs.getString("AccountName"));
				jve.setTxnCCy(rs.getString("TxnCCy"));
				jve.setTxnEntry(rs.getString("TxnEntry"));
				jve.setAccCCy(rs.getString("AccCCy"));
				jve.setTxnCode(rs.getString("TxnCode"));
				jve.setPostingDate(rs.getTimestamp("PostingDate"));
				jve.setValueDate(rs.getTimestamp("ValueDate"));
				jve.setTxnAmount(rs.getBigDecimal("TxnAmount"));
				jve.setTxnReference(rs.getLong("TxnReference"));
				jve.setNarrLine1(rs.getString("NarrLine1"));
				jve.setNarrLine2(rs.getString("NarrLine2"));
				jve.setNarrLine3(rs.getString("NarrLine3"));
				jve.setNarrLine4(rs.getString("NarrLine4"));
				jve.setExchRate_Batch(rs.getBigDecimal("ExchRate_Batch"));
				jve.setExchRate_Ac(rs.getBigDecimal("ExchRate_Ac"));
				jve.setTxnAmount_Batch(rs.getBigDecimal("TxnAmount_Batch"));
				jve.setTxnAmount_Ac(rs.getBigDecimal("TxnAmount_Ac"));
				jve.setModifiedFlag(rs.getString("ModifiedFlag"));
				jve.setDeletedFlag(rs.getBoolean("DeletedFlag"));
				jve.setValidationStatus(rs.getString("ValidationStatus"));
				jve.setPostingStatus(rs.getString("PostingStatus"));
				jve.setExternalAccount(rs.getBoolean("ExternalAccount"));
				jve.setLinkedTranId(rs.getLong("LinkedTranId"));
				jve.setLastMntBy(rs.getLong("LastMntBy"));
				jve.setLastMntOn(rs.getTimestamp("LastMntOn"));
				jve.setRecordStatus(rs.getString("RecordStatus"));
				jve.setRoleCode(rs.getString("RoleCode"));
				jve.setNextRoleCode(rs.getString("NextRoleCode"));
				jve.setTaskId(rs.getString("TaskId"));
				jve.setNextTaskId(rs.getString("NextTaskId"));
				jve.setRecordType(rs.getString("RecordType"));
				jve.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					// jve.setTxnCCyName(rs.getString("TxnCCyName"));
					// jve.setTxnCCyEditField(rs.getString("TxnCCyEditField"));
					jve.setTxnDesc(rs.getString("TxnDesc"));
					// jve.setAccCCyName(rs.getString("AccCCyName"));
					// jve.setAccCCyEditField(rs.getString("AccCCyEditField"));
					// jve.setAcCcyNumber(rs.getString("AcCcyNumber"));
					// jve.setTxnCcyNumber(rs.getLong("TxnCcyNumber"));
				}

				return jve;

			}, batchRef, txnReference, txnEntry, account, txnAmount);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<JVPostingEntry> getDeletedJVPostingEntryListById(long batchRef, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where BatchReference = ? and DeletedFlag = ?");

		logger.debug(Literal.SQL + sql.toString());

		JVPostingEntryRowMapper rowMapper = new JVPostingEntryRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchRef);
			ps.setInt(index, 1);
		}, rowMapper);
	}

	@Override
	public void updateListPostingStatus(List<JVPostingEntry> aJVPostingEntryList, String type, boolean isTxnRefWise) {
		StringBuilder sql = new StringBuilder("Update JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set PostingStatus = ?");

		if (isTxnRefWise) {
			sql.append(" Where BatchReference = ? and TxnReference = ? and AcEntryRef = ?");
		} else {
			sql.append(" Where BatchReference = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				JVPostingEntry jve = aJVPostingEntryList.get(i);
				int index = 1;

				ps.setString(index++, jve.getPostingStatus());

				if (isTxnRefWise) {
					ps.setLong(index++, jve.getBatchReference());
					ps.setLong(index++, jve.getTxnReference());
					ps.setLong(index, jve.getAcEntryRef());
				} else {
					ps.setLong(index, jve.getBatchReference());
				}
			}

			@Override
			public int getBatchSize() {
				return aJVPostingEntryList.size();
			}
		});
	}

	@Override
	public int getMaxSeqNumForCurrentDay(JVPostingEntry jve) {
		int count = getJVPostingCount(jve);

		if (count != 0) {
			return count;
		}

		String sql = "Insert into SeqJVPostingEntry (SeqDate, SeqNo) Values (?, ?)";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(jve.getDaySeqDate()));
			ps.setInt(index, jve.getDaySeqNo());
		});

		return getJVPostingCount(jve);
	}

	private int getJVPostingCount(JVPostingEntry jve) {
		String sql = "Select Coalesce(max(SeqNo), 0) DaySeqNo From SeqJVPostingEntry Where SeqDate = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Integer.class, jve.getDaySeqDate());
	}

	@Override
	public void upDateSeqNoForCurrentDayBatch(JVPostingEntry jve) {
		String sql = "Update SeqJVPostingEntry Set SeqNo = ? Where SeqDate = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, jve.getDaySeqNo());
			ps.setDate(index, JdbcUtil.getDate(jve.getDaySeqDate()));
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteIAEntries(long batchReference) {
		String sql = "Delete From JVPostingEntry_Temp Where BatchReference = ? and ExternalAccount = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setLong(index++, batchReference);
				ps.setInt(index, 0);
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	private StringBuilder sqlSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" p.FileName, p.BatchReference, p.AcEntryRef, p.HostSeqNo, p.Account, p.AcType");
		sql.append(", p.AccountName, p.TxnCCy, p.TxnEntry, p.AccCCy, p.TxnCode, p.PostingDate");
		sql.append(", p.ValueDate, p.TxnAmount, p.TxnReference, p.NarrLine1, p.NarrLine2, p.NarrLine3");
		sql.append(", p.NarrLine4, TdsAdjReq, p.ExchRate_Batch, p.ExchRate_Ac, p.TxnAmount_Batch, p.TxnAmount_Ac");
		sql.append(", p.ModifiedFlag, p.DeletedFlag, p.ValidationStatus, p.PostingStatus, p.ExternalAccount");
		sql.append(", p.LinkedTranId, p.LastMntBy, p.LastMntOn, p.RecordStatus, p.RoleCode, p.NextRoleCode");
		sql.append(", p.TaskId, p.TDSAdjReq, p.NextTaskId, p.RecordType, p.WorkflowId, am.HostAccount GlCode");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", p.TxnDesc, p.DerivedTxnRef");
		}

		sql.append(" From JVPostingEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" p");
		sql.append(" Left join AccountMapping am on am.Account = p.Account");
		return sql;
	}

	private class JVPostingEntryRowMapper implements RowMapper<JVPostingEntry> {
		private String type;

		private JVPostingEntryRowMapper(String type) {
			this.type = type;
		}

		@Override
		public JVPostingEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
			JVPostingEntry jve = new JVPostingEntry();

			jve.setFileName(rs.getString("FileName"));
			jve.setBatchReference(rs.getLong("BatchReference"));
			jve.setAcEntryRef(rs.getLong("AcEntryRef"));
			jve.setHostSeqNo(rs.getString("HostSeqNo"));
			jve.setAccount(rs.getString("Account"));
			jve.setAcType(rs.getString("AcType"));
			jve.setAccountName(rs.getString("AccountName"));
			jve.setTxnCCy(rs.getString("TxnCCy"));
			jve.setTxnEntry(rs.getString("TxnEntry"));
			jve.setAccCCy(rs.getString("AccCCy"));
			jve.setTxnCode(rs.getString("TxnCode"));
			jve.setPostingDate(rs.getTimestamp("PostingDate"));
			jve.setValueDate(rs.getTimestamp("ValueDate"));
			jve.setTxnAmount(rs.getBigDecimal("TxnAmount"));
			jve.setTxnReference(rs.getLong("TxnReference"));
			jve.setNarrLine1(rs.getString("NarrLine1"));
			jve.setNarrLine2(rs.getString("NarrLine2"));
			jve.setNarrLine3(rs.getString("NarrLine3"));
			jve.setNarrLine4(rs.getString("NarrLine4"));
			jve.setTDSAdjReq(rs.getBoolean("TdsAdjReq"));
			jve.setExchRate_Batch(rs.getBigDecimal("ExchRate_Batch"));
			jve.setExchRate_Ac(rs.getBigDecimal("ExchRate_Ac"));
			jve.setTxnAmount_Batch(rs.getBigDecimal("TxnAmount_Batch"));
			jve.setTxnAmount_Ac(rs.getBigDecimal("TxnAmount_Ac"));
			jve.setModifiedFlag(rs.getString("ModifiedFlag"));
			jve.setDeletedFlag(rs.getBoolean("DeletedFlag"));
			jve.setValidationStatus(rs.getString("ValidationStatus"));
			jve.setPostingStatus(rs.getString("PostingStatus"));
			jve.setExternalAccount(rs.getBoolean("ExternalAccount"));
			jve.setLinkedTranId(rs.getLong("LinkedTranId"));
			jve.setLastMntBy(rs.getLong("LastMntBy"));
			jve.setLastMntOn(rs.getTimestamp("LastMntOn"));
			jve.setRecordStatus(rs.getString("RecordStatus"));
			jve.setRoleCode(rs.getString("RoleCode"));
			jve.setNextRoleCode(rs.getString("NextRoleCode"));
			jve.setTaskId(rs.getString("TaskId"));
			jve.setNextTaskId(rs.getString("NextTaskId"));
			jve.setRecordType(rs.getString("RecordType"));
			jve.setWorkflowId(rs.getLong("WorkflowId"));
			jve.setGlCode(rs.getString("GlCode"));
			jve.setTDSAdjReq(rs.getBoolean("TDSAdjReq"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				jve.setTxnDesc(rs.getString("TxnDesc"));
				jve.setDerivedTxnRef(rs.getLong("DerivedTxnRef"));
			}

			return jve;

		}
	}

}