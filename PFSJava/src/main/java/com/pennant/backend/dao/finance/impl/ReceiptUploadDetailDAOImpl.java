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
 * * FileName : UploadHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * * Modified
 * Date : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ThreadAllocation;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class ReceiptUploadDetailDAOImpl extends SequenceDao<ReceiptUploadDetail> implements ReceiptUploadDetailDAO {

	private static Logger logger = LogManager.getLogger(ReceiptUploadDetailDAOImpl.class);

	public ReceiptUploadDetailDAOImpl() {
		super();
	}

	@Override
	public List<ReceiptUploadDetail> getUploadReceiptDetails(long id, boolean getsucessrcdOnly) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UploadHeaderId, UploadDetailId, RootId, Reference, ReceiptPurpose, ReceiptAmount");
		sql.append(", AllocationType, ProcessingStatus, ReceivedDate, Reason, ReceiptId");
		sql.append(" From ReceiptUploadDetails");
		sql.append(" Where UploadHeaderId = ?");

		if (getsucessrcdOnly) {
			sql.append(" and ProcessingStatus = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		List<ReceiptUploadDetail> uploads = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, id);

			if (getsucessrcdOnly) {
				ps.setInt(index, ReceiptDetailStatus.SUCCESS.getValue());
			}

		}, (rs, rowNum) -> {
			ReceiptUploadDetail rud = new ReceiptUploadDetail();

			rud.setUploadheaderId(rs.getLong("UploadHeaderId"));
			rud.setUploadDetailId(rs.getLong("UploadDetailId"));
			rud.setRootId(rs.getString("RootId"));
			rud.setReference(rs.getString("Reference"));
			rud.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			rud.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			rud.setAllocationType(rs.getString("AllocationType"));
			rud.setProcessingStatus(rs.getInt("ProcessingStatus"));
			rud.setReceivedDate(JdbcUtil.getDate(rs.getDate("ReceivedDate")));
			rud.setReason(rs.getString("Reason"));
			rud.setReceiptId(JdbcUtil.getLong(rs.getObject("ReceiptId")));

			return rud;
		});

		return uploads.stream().sorted((dpd1, dpd2) -> Long.compare(dpd1.getUploadDetailId(), dpd1.getUploadDetailId()))
				.collect(Collectors.toList());
	}

	@Override
	public ReceiptUploadDetail getUploadReceiptDetail(long headerId, long detailId) {
		StringBuilder sql = sqlSelectQuery();
		sql.append(" Where UploadheaderId = ? and UploadDetailId = ? ");
		logger.debug(Literal.SQL + sql.toString());

		ReceiptUploadDetailRowMapper rowMapper = new ReceiptUploadDetailRowMapper();

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, headerId, detailId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long save(ReceiptUploadDetail rud) {
		if (rud.getUploadDetailId() == Long.MIN_VALUE) {
			rud.setUploadDetailId(getNextValue("SeqReceiptUploadDetail"));
		}

		StringBuilder sql = new StringBuilder("Insert Into ReceiptUploaddetails");
		sql.append(" (UploadHeaderId, UploadDetailId, Reference, ReceiptPurpose, Receiptamount, AllocationType");
		sql.append(", ProcessingStatus, Reason, RootId, ExcessAdjustTo, EffectSchdMethod, Remarks, ValueDate");
		sql.append(", ReceivedDate, ReceiptMode, FundingAc, PaymentRef, FavourNumber, BankCode, ChequeAcNo");
		sql.append(", TransactionRef, Status, DepositDate, RealizationDate, InstrumentDate, ExtReference");
		sql.append(", SubReceiptMode, ReceiptChannel, ReceivedFrom, PanNumber, CollectionAgentId, TdsAmount");
		sql.append(", BckdtdWthOldDues, BounceReason, CancelReason, BounceDate, ReceiptId");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, rud.getUploadheaderId());
			ps.setLong(index++, rud.getUploadDetailId());
			ps.setString(index++, rud.getReference());
			ps.setString(index++, rud.getReceiptPurpose());
			ps.setBigDecimal(index++, rud.getReceiptAmount());
			ps.setString(index++, rud.getAllocationType());
			ps.setInt(index++, rud.getProcessingStatus());
			ps.setString(index++, rud.getReason());
			ps.setString(index++, rud.getRootId());
			ps.setString(index++, rud.getExcessAdjustTo());
			ps.setString(index++, rud.getEffectSchdMethod());
			ps.setString(index++, rud.getRemarks());
			ps.setDate(index++, JdbcUtil.getDate(rud.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(rud.getReceivedDate()));
			ps.setString(index++, rud.getReceiptMode());
			ps.setString(index++, rud.getFundingAc());
			ps.setString(index++, rud.getPaymentRef());
			ps.setString(index++, rud.getFavourNumber());
			ps.setString(index++, rud.getBankCode());
			ps.setString(index++, rud.getChequeNo());
			ps.setString(index++, rud.getTransactionRef());
			ps.setString(index++, rud.getStatus());
			ps.setDate(index++, JdbcUtil.getDate(rud.getDepositDate()));
			ps.setDate(index++, JdbcUtil.getDate(rud.getRealizationDate()));
			ps.setDate(index++, JdbcUtil.getDate(rud.getInstrumentDate()));
			ps.setString(index++, rud.getExtReference());
			ps.setString(index++, rud.getSubReceiptMode());
			ps.setString(index++, rud.getReceiptChannel());
			ps.setString(index++, rud.getReceivedFrom());
			ps.setString(index++, rud.getPanNumber());
			ps.setObject(index++, rud.getCollectionAgentId());
			ps.setBigDecimal(index++, rud.getTdsAmount());
			ps.setBoolean(index++, rud.isBckdtdWthOldDues());
			ps.setString(index++, rud.getBounceReason());
			ps.setString(index++, rud.getCancelReason());
			ps.setDate(index++, JdbcUtil.getDate(rud.getBounceDate()));
			ps.setObject(index, rud.getReceiptId());
		});

		return rud.getUploadDetailId();
	}

	@Override
	public void delete(long uploadHeaderId) {
		String sql = "Delete From ReceiptUploaddetails Where UploadHeaderId = ?";
		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index, uploadHeaderId);
		});
	}

	@Override
	public void updateStatus(ReceiptUploadDetail rud) {
		String reason = StringUtils.trim(rud.getReason());

		rud.setReason(reason);

		if (reason.length() > 1000) {
			rud.setReason(reason.substring(0, 999));
		}

		String sql = "Update ReceiptUploadDetails Set ProcessingStatus = ?, Reason = ? Where UploadDetailId = ?";
		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, rud.getProcessingStatus());
			ps.setString(index++, rud.getReason());
			ps.setLong(index, rud.getUploadDetailId());
		});
	}

	@Override
	public void updateReceiptId(long uploadDetailId, long receiptId) {
		String sql = "Update ReceiptUploaddetails Set ReceiptId = ? Where UploadDetailId = ?";
		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, receiptId);
			ps.setLong(index, uploadDetailId);
		});
	}

	@Override
	public void updateRejectStatusById(String id, String errorMsg) {
		String sql = "Update ReceiptUploaddetails Set Reason = ? Where UploadHeaderId = ?";
		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, errorMsg);
			ps.setString(index, id);

		});
	}

	@Override
	public String getLoanReferenc(String finReference, String fileName) {
		StringBuilder sql = new StringBuilder("Select ");
		sql.append(" Distinct Reference");
		sql.append(" From ReceiptUploadDetails ");
		sql.append(" Where UploadHeaderId in (");
		sql.append(" Select UploadHeaderId From ReceiptUploadHeader_Temp");
		sql.append(" Where FileName not in (?) and uploadprogress in ( ?,? ) ");
		sql.append(" Union All");
		sql.append(" Select UploadHeaderId From ReceiptUploadHeader");
		sql.append(" Where FileName not in (?) and uploadprogress in ( ?,? ) ");
		sql.append(") and Reference = ? and ProcessingStatus in (?)");

		logger.debug(Literal.SQL + sql.toString());

		int deflt = ReceiptUploadConstants.RECEIPT_DEFAULT;
		int download = ReceiptUploadConstants.RECEIPT_DOWNLOADED;
		int status = ReceiptDetailStatus.SUCCESS.getValue();

		Object[] args = new Object[] { fileName, deflt, download, fileName, deflt, download, finReference, status };
		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getString(1), args);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public List<Long> getListofReceiptUploadDetails(long uploadHeaderId) {
		String sql = "Select UploadDetailId From ReceiptUploadDetails Where UploadHeaderId = ?";
		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> ps.setLong(1, uploadHeaderId),
				(rs, rowNum) -> JdbcUtil.getLong(rs.getObject("UploadDetailId")));
	}

	@Override
	public List<Long> getReceiptDetails(List<Long> list) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select UploadDetailId From ReceiptUploadDetails Where UploadHeaderId In(");
		sql.append(commaJoin(list));
		sql.append(") and ProcessingStatus = ?");
		sql.append(" order by Reference, ValueDate");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long l1 : list) {
				ps.setObject(index++, l1);
			}

			ps.setInt(index, ReceiptDetailStatus.SUCCESS.getValue());
		}, (rs, rowNum) -> JdbcUtil.getLong(rs.getObject(1)));

	}

	@Override
	public ReceiptUploadDetail getUploadReceiptDetail(long detailID) {
		StringBuilder sql = sqlSelectQuery();
		sql.append(" Where UploadDetailId = ?");
		logger.debug(Literal.SQL + sql.toString());

		ReceiptUploadDetailRowMapper rowMapper = new ReceiptUploadDetailRowMapper();

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), rowMapper, detailID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ThreadAllocation> getFinRefWithCount(List<Long> uploadHeaderIdList) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Reference, count(Reference) FinCount from receiptuploaddetails");
		sql.append(" Where UploadHeaderId in (");
		sql.append(commaJoin(uploadHeaderIdList));
		sql.append(" )");
		sql.append(" and ProcessingStatus = ? Group By Reference");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			for (Long uploadHeader : uploadHeaderIdList) {
				ps.setLong(++index, uploadHeader);
			}

			ps.setInt(++index, ReceiptDetailStatus.INPROGRESS.getValue());
		}, (rs, rowNum) -> {
			ThreadAllocation thread = new ThreadAllocation();
			thread.setReference(rs.getString("Reference"));
			thread.setCount(rs.getInt("FinCount"));

			return thread;
		});

	}

	private String commaJoin(List<Long> list) {
		return list.stream().map(e -> "?").collect(Collectors.joining(","));
	}

	@Override
	public List<ReceiptUploadDetail> getUploadReceiptDetailsByThreadId(List<Long> uploadHeaderIdList,
			Integer threadId) {
		StringBuilder sql = sqlSelectQuery();
		sql.append(" Where UploadHeaderId in (");
		sql.append(commaJoin(uploadHeaderIdList));
		sql.append(")");
		sql.append(" and ThreadId = ? and ProcessingStatus = ?");

		logger.debug(Literal.SQL + sql.toString());

		ReceiptUploadDetailRowMapper rowMapper = new ReceiptUploadDetailRowMapper();

		List<ReceiptUploadDetail> rudList = jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long uploadHeader : uploadHeaderIdList) {
				ps.setLong(index++, uploadHeader);
			}

			ps.setInt(index++, threadId);
			ps.setInt(index, ReceiptDetailStatus.INPROGRESS.getValue());
		}, rowMapper);

		return rudList.stream().sorted((f1, f2) -> DateUtil.compare(f1.getValueDate(), f2.getValueDate()))
				.collect(Collectors.toList());

	}

	@Override
	public int updateThreadAllocationByFinRef(List<ThreadAllocation> batchAllocations, List<Long> uploadHeaderIdList) {
		StringBuilder sql = new StringBuilder("Update ReceiptUploadDetails");
		sql.append(" Set ThreadId = ?");
		sql.append(" Where Reference = ? And UploadHeaderId In (");
		sql.append(commaJoin(uploadHeaderIdList));
		sql.append(") and ProcessingStatus = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ThreadAllocation fd = batchAllocations.get(i);
				int index = 1;

				ps.setInt(index++, fd.getThreadId());
				ps.setString(index++, fd.getReference());

				for (Long uploadHeader : uploadHeaderIdList) {
					ps.setLong(index++, uploadHeader);
				}

				ps.setInt(index, ReceiptDetailStatus.INPROGRESS.getValue());
			}

			@Override
			public int getBatchSize() {
				return batchAllocations.size();
			}
		}).length;
	}

	@Override
	public long updateStatus(List<Long> uploadHeaderIdList) {
		StringBuilder sql = new StringBuilder("Update ReceiptUploaddetails");
		sql.append(" Set ProcessingStatus = ?");
		sql.append(" Where UploadHeaderid in ( ");
		sql.append(commaJoin(uploadHeaderIdList));
		sql.append(") and ProcessingStatus = ? and ((ReceiptId = 0 or ReceiptId is null) or (status in ('B','C')))");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, ReceiptDetailStatus.INPROGRESS.getValue());

			for (Long uploadHeader : uploadHeaderIdList) {
				ps.setLong(index++, uploadHeader);
			}

			ps.setInt(index, ReceiptDetailStatus.SUCCESS.getValue());
		});

	}

	@Override
	public List<String> isDuplicateExists(ReceiptUploadDetail rud) {
		boolean isOnline = StringUtils.isNotBlank(rud.getTransactionRef());
		StringBuilder sql = new StringBuilder("Select FileName From ReceiptUploadHeader_Temp");
		sql.append(" Where UploadHeaderId IN (");
		sql.append(" Select UploadHeaderId From ReceiptUploadDetails");
		sql.append(" Where Reference = ?  and ValueDate = ? and ReceiptAmount = ?");
		sql.append(" and UploadHeaderId <> ? and ProcessingStatus = ?");

		if (isOnline) {
			sql.append(" and TransactionRef = ?");
		}
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, rud.getReference());
			ps.setDate(index++, JdbcUtil.getDate(rud.getValueDate()));
			ps.setBigDecimal(index++, rud.getReceiptAmount());
			ps.setLong(index++, rud.getUploadheaderId());
			ps.setInt(index++, ReceiptDetailStatus.SUCCESS.getValue());

			if (isOnline) {
				ps.setString(index, rud.getTransactionRef());
			}

		}, (rs, roNum) -> rs.getString(1));
	}

	@Override
	public boolean isReceiptsQueue(String finReference) {
		String sql = "Select count(Reference) From ReceiptUploadDetails Where Reference = ? and ProcessingStatus = ? and (ReceiptId = ? OR ReceiptId is Null)";

		logger.debug(Literal.SQL + sql);

		Object[] object = new Object[] { finReference, ReceiptDetailStatus.SUCCESS.getValue(), 0 };
		return this.jdbcOperations.queryForObject(sql, Integer.class, object) > 0;
	}

	private StringBuilder sqlSelectQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UploadHeaderId, UploadDetailId, RootId, Reference, ReceiptPurpose, ReceiptAmount");
		sql.append(", AllocationType, ProcessingStatus, Reason, ExcessAdjustTo, EffectSchdMethod, Remarks");
		sql.append(", ValueDate, ReceivedDate, ReceiptMode, FundingAc, PaymentRef, FavourNumber, BankCode");
		sql.append(", ChequeAcNo ChequeNo, TransactionRef, Status, DepositDate, RealizationDate, InstrumentDate");
		sql.append(", ExtReference, SubReceiptMode, ReceiptChannel, ReceivedFrom");
		sql.append(", PanNumber, CollectionAgentId, TdsAmount");
		sql.append(", BckdtdWthOldDues, ReceiptId, BounceDate, BounceReason, CancelReason");
		sql.append(" From ReceiptUploadDetails");
		return sql;
	}

	private class ReceiptUploadDetailRowMapper implements RowMapper<ReceiptUploadDetail> {
		private ReceiptUploadDetailRowMapper() {
			super();
		}

		@Override
		public ReceiptUploadDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReceiptUploadDetail ca = new ReceiptUploadDetail();

			ca.setUploadheaderId(rs.getLong("UploadHeaderId"));
			ca.setUploadDetailId(rs.getLong("UploadDetailId"));
			ca.setRootId(rs.getString("RootId"));
			ca.setReference(rs.getString("Reference"));
			ca.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			ca.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			ca.setAllocationType(rs.getString("AllocationType"));
			ca.setProcessingStatus(rs.getInt("ProcessingStatus"));
			ca.setReason(rs.getString("Reason"));
			ca.setExcessAdjustTo(rs.getString("ExcessAdjustTo"));
			ca.setEffectSchdMethod(rs.getString("EffectSchdMethod"));
			ca.setRemarks(rs.getString("Remarks"));
			ca.setValueDate(rs.getTimestamp("ValueDate"));
			ca.setReceivedDate(rs.getTimestamp("ReceivedDate"));
			ca.setReceiptMode(rs.getString("ReceiptMode"));
			ca.setFundingAc(rs.getString("FundingAc"));
			ca.setPaymentRef(rs.getString("PaymentRef"));
			ca.setFavourNumber(rs.getString("FavourNumber"));
			ca.setBankCode(rs.getString("BankCode"));
			// ca.setChequeAcNo(rs.getLong("ChequeAcNo"));
			ca.setChequeNo(rs.getString("ChequeNo"));
			ca.setTransactionRef(rs.getString("TransactionRef"));
			ca.setStatus(rs.getString("Status"));
			ca.setDepositDate(rs.getTimestamp("DepositDate"));
			ca.setRealizationDate(rs.getTimestamp("RealizationDate"));
			ca.setInstrumentDate(rs.getTimestamp("InstrumentDate"));
			ca.setExtReference(rs.getString("ExtReference"));
			ca.setSubReceiptMode(rs.getString("SubReceiptMode"));
			ca.setReceiptChannel(rs.getString("ReceiptChannel"));
			ca.setReceivedFrom(rs.getString("ReceivedFrom"));
			ca.setPanNumber(rs.getString("PanNumber"));
			ca.setCollectionAgentId(JdbcUtil.getLong(rs.getObject("CollectionAgentId")));
			ca.setTdsAmount(rs.getBigDecimal("TdsAmount"));
			ca.setBckdtdWthOldDues(rs.getBoolean("BckdtdWthOldDues"));
			ca.setReceiptId(rs.getLong("ReceiptId"));
			ca.setBounceDate(rs.getDate("BounceDate"));
			ca.setBounceReason(rs.getString("BounceReason"));
			ca.setCancelReason(rs.getString("CancelReason"));

			return ca;

		}
	}

}