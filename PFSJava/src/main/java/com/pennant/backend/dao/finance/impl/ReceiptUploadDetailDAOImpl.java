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
 * FileName    		:  UploadHeaderDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2017       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.amazonaws.util.StringUtils;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

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
		StringBuilder sql = new StringBuilder();
		sql.append("Select UploadHeaderId, UploadDetailId, RootId, Reference, ReceiptPurpose, ReceiptAmount");
		sql.append(", AllocationType, UploadStatus, ReceivedDate, Reason, ReceiptId");
		sql.append(" From ReceiptUploadDetails");
		sql.append(" Where UploadHeaderId = ?");

		if (getsucessrcdOnly) {
			sql.append(" and UploadStatus = ?");
		}

		logger.trace(Literal.SQL, sql.toString());

		List<ReceiptUploadDetail> receiptUploadDetails = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, id);

			if (getsucessrcdOnly) {
				ps.setString(index++, PennantConstants.UPLOAD_STATUS_SUCCESS);
			}

		}, (rs, rowNum) -> {
			ReceiptUploadDetail rud = new ReceiptUploadDetail();
			rud.setUploadheaderId(rs.getLong("UploadHeaderid"));
			rud.setUploadDetailId(rs.getLong("UploadDetailId"));
			rud.setRootId(rs.getString("RootId"));
			rud.setReference(rs.getString("Reference"));
			rud.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			rud.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			rud.setAllocationType(rs.getString("AllocationType"));
			rud.setUploadStatus(rs.getString("UploadStatus"));
			rud.setReceivedDate(JdbcUtil.getDate(rs.getDate("ReceivedDate")));
			rud.setReason(rs.getString("Reason"));
			rud.setReceiptId(rs.getLong("ReceiptId") == 0 ? null : rs.getLong("ReceiptId"));

			return rud;
		});

		return receiptUploadDetails.stream()
				.sorted((dpd1, dpd2) -> Long.compare(dpd1.getUploadDetailId(), dpd1.getUploadDetailId()))
				.collect(Collectors.toList());
	}

	@Override
	public ReceiptUploadDetail getUploadReceiptDetail(long headerId, long detailId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UploadHeaderId, UploadDetailId, RootId, Reference, ReceiptPurpose, ReceiptAmount");
		sql.append(", AllocationType, UploadStatus, Reason, ExcessAdjustTo, EffectSchdMethod, Remarks");
		sql.append(", ValueDate, ReceivedDate, ReceiptMode, FundingAc, PaymentRef, FavourNumber, BankCode");
		sql.append(", ChequeAcNo ChequeNo, TransactionRef, Status, DepositDate, RealizationDate, InstrumentDate");
		sql.append(", ExtReference, SubReceiptMode, ReceiptChannel, ReceivedFrom, PanNumber, CollectionAgentId, TdsAmount");
		sql.append(" From ReceiptUploadDetails");
		sql.append(" Where UploadheaderId = ? And UploadDetailId = ? ");

		logger.trace(Literal.SQL, sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { headerId, detailId },
					(rs, rowNum) -> {
						ReceiptUploadDetail ca = new ReceiptUploadDetail();

						ca.setUploadheaderId(rs.getLong("UploadHeaderId"));
						ca.setUploadDetailId(rs.getLong("UploadDetailId"));
						ca.setRootId(rs.getString("RootId"));
						ca.setReference(rs.getString("Reference"));
						ca.setReceiptPurpose(rs.getString("ReceiptPurpose"));
						ca.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
						ca.setAllocationType(rs.getString("AllocationType"));
						ca.setUploadStatus(rs.getString("UploadStatus"));
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
						ca.setCollectionAgentId(rs.getLong("CollectionAgentId"));
						ca.setTdsAmount(rs.getBigDecimal("TdsAmount"));
						
						return ca;
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record does not exist in ReceiptUploadDetails table for the specified UploadheaderId >> {} and UploadDetailId >> {}",
					headerId, detailId);
		}
		return null;
	}

	@Override
	public long save(ReceiptUploadDetail rud) {

		if (rud.getUploadDetailId() == Long.MIN_VALUE) {
			rud.setUploadDetailId(getNextValue("SeqReceiptUploadDetail"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" ReceiptUploaddetails");
		sql.append(" (UploadHeaderId, UploadDetailId, Reference, ReceiptPurpose, Receiptamount, AllocationType");
		sql.append(", UploadStatus, Reason, RootId, ExcessAdjustTo, EffectSchdMethod, Remarks, ValueDate");
		sql.append(", ReceivedDate, ReceiptMode, FundingAc, PaymentRef, FavourNumber, BankCode, ChequeAcNo");
		sql.append(", TransactionRef, Status, DepositDate, RealizationDate, InstrumentDate, ExtReference");
		sql.append(", SubReceiptMode, ReceiptChannel, ReceivedFrom, PanNumber, CollectionAgentId, TdsAmount");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL, sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, JdbcUtil.setLong(rud.getUploadheaderId()));
			ps.setLong(index++, JdbcUtil.setLong(rud.getUploadDetailId()));
			ps.setString(index++, rud.getReference());
			ps.setString(index++, rud.getReceiptPurpose());
			ps.setBigDecimal(index++, rud.getReceiptAmount());
			ps.setString(index++, rud.getAllocationType());
			ps.setString(index++, rud.getUploadStatus());
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
			ps.setLong(index++, JdbcUtil.setLong(rud.getCollectionAgentId()));
			ps.setBigDecimal(index++, rud.getTdsAmount());

		});

		return rud.getUploadDetailId();

	}

	@Override
	public void delete(long uploadHeaderId) {

		StringBuilder sql = new StringBuilder("Delete From ReceiptUploaddetails");
		sql.append(" Where UploadHeaderId = ?");

		int recordCount = 0;
		logger.trace(Literal.SQL, sql.toString());
		recordCount = jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, uploadHeaderId));

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateStatus(ReceiptUploadDetail rud) {

		String reason = rud.getReason();
		reason = StringUtils.trim(reason);

		if (reason.length() > 1000) {
			reason.substring(0, 999);
		}
		rud.setReason(reason);

		StringBuilder sql = new StringBuilder("Update ReceiptUploadDetails");
		sql.append(" Set UploadStatus = ?, Reason = ?");
		sql.append(" Where UploadDetailId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setString(index++, rud.getUploadStatus());
				ps.setString(index++, rud.getReason());
				ps.setLong(index++, rud.getUploadDetailId());
			});
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	@Override
	public void updateReceiptId(long uploadDetailId, long receiptId) {
		StringBuilder sql = new StringBuilder("Update ReceiptUploaddetails");
		sql.append(" Set ReceiptId = ?");
		sql.append(" Where UploadDetailId = ?");

		logger.trace(Literal.SQL, sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index++, receiptId);
				ps.setLong(index++, uploadDetailId);
			});
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	@Override
	public void updateRejectStatusById(String id, String errorMsg) {
		StringBuilder sql = new StringBuilder("Update ReceiptUploaddetails");
		sql.append(" Set Reason = ?");
		sql.append(" Where UploadHeaderid = ?");

		logger.trace(Literal.SQL, sql.toString());
		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setString(index++, errorMsg);
				ps.setString(index++, id);

			});
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	@Override
	public String getLoanReferenc(String finReference, String receiptFileName) {
		StringBuilder sql = new StringBuilder("Select ");
		sql.append(" DISTINCT Reference");
		sql.append(" From ReceiptUploadDetails ");
		sql.append(" Where UploadHeaderId in ");
		sql.append("(Select UploadHeaderId From ReceiptUploadHeader_View");
		sql.append(" Where FileName not in (?) and uploadprogress in ( ?,? ) )");
		sql.append(" and Reference = ? and uploadstatus in (?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(),
					new Object[] { receiptFileName, ReceiptUploadConstants.RECEIPT_DEFAULT,
							ReceiptUploadConstants.RECEIPT_DOWNLOADED, finReference,
							PennantConstants.UPLOAD_STATUS_SUCCESS },
					(rs, rowNum) -> rs.getString(1));
		} catch (DataAccessException e) {
			logger.warn(
					"Record does not exist in ReceiptUploadDetails table for the specified finReference >> {} and receiptFileName >> {}",
					finReference, receiptFileName);
		}

		return null;

	}

	public List<Long> getListofReceiptUploadDetails(long uploadHeaderId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" UploadDetailId");
		sql.append(" From ReceiptUploadDetails");
		sql.append(" Where UploadHeaderId = ?");

		logger.trace(Literal.SQL, sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, uploadHeaderId), (rs, rowNum) -> {
			return (Long) rs.getLong("UploadDetailId");
		});
	}

	@Override
	public List<Long> getReceiptDetails(List<Long> list) {

		StringBuilder sql = new StringBuilder();
		sql.append("Select UploadDetailId from ReceiptUploadDetails where UploadHeaderId In(");

		Object[] parm = new Object[list.size() + 1];

		int index = 0;
		for (Long headerId : list) {
			if (index > 0) {
				sql.append(", ");
			}
			sql.append("?");

			parm[index++] = headerId;
		}

		parm[index] = PennantConstants.UPLOAD_STATUS_SUCCESS;

		sql.append(")");
		sql.append(" and Uploadstatus = ?");
		sql.append(" Order by Reference, ValueDate");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), parm, Long.class);
	}

	@Override
	public ReceiptUploadDetail getUploadReceiptDetail(long detailID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  UploadHeaderId, UploadDetailId, RootId, Reference, ReceiptPurpose, ReceiptAmount");
		sql.append(", AllocationType, UploadStatus, Reason, ExcessAdjustTo, EffectSchdMethod");
		sql.append(", Remarks, ValueDate, ReceivedDate, ReceiptMode, FundingAc, PaymentRef, FavourNumber");
		sql.append(", BankCode, ChequeAcNo ChequeNo, TransactionRef, Status, DepositDate, RealizationDate");
		sql.append(", InstrumentDate, ExtReference, SubReceiptMode, ReceiptChannel, ReceivedFrom");
		sql.append(", PanNumber, CollectionAgentId");
		sql.append(" From ReceiptUploadDetails");
		sql.append(" Where UploadDetailId = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.queryForObject(sql.toString(), new Object[] { detailID }, (rs, rowNum) -> {
			ReceiptUploadDetail rud = new ReceiptUploadDetail();

			rud.setUploadheaderId(rs.getLong("UploadHeaderId"));
			rud.setUploadDetailId(rs.getLong("UploadDetailId"));
			rud.setRootId(rs.getString("RootId"));
			rud.setReference(rs.getString("Reference"));
			rud.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			rud.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			rud.setAllocationType(rs.getString("AllocationType"));
			rud.setUploadStatus(rs.getString("UploadStatus"));
			rud.setReason(rs.getString("Reason"));
			rud.setExcessAdjustTo(rs.getString("ExcessAdjustTo"));
			rud.setEffectSchdMethod(rs.getString("EffectSchdMethod"));
			rud.setRemarks(rs.getString("Remarks"));
			rud.setValueDate(rs.getDate("ValueDate"));
			rud.setReceivedDate(rs.getDate("ReceivedDate"));
			rud.setReceiptMode(rs.getString("ReceiptMode"));
			rud.setFundingAc(rs.getString("FundingAc"));
			rud.setPaymentRef(rs.getString("PaymentRef"));
			rud.setFavourNumber(rs.getString("FavourNumber"));
			rud.setBankCode(rs.getString("BankCode"));
			rud.setChequeNo(rs.getString("ChequeNo"));
			rud.setTransactionRef(rs.getString("TransactionRef"));
			rud.setStatus(rs.getString("Status"));
			rud.setDepositDate(rs.getDate("DepositDate"));
			rud.setRealizationDate(rs.getDate("RealizationDate"));
			rud.setInstrumentDate(rs.getDate("InstrumentDate"));
			rud.setExtReference(rs.getString("ExtReference"));
			rud.setSubReceiptMode(rs.getString("SubReceiptMode"));
			rud.setReceiptChannel(rs.getString("ReceiptChannel"));
			rud.setReceivedFrom(rs.getString("ReceivedFrom"));
			rud.setPanNumber(rs.getString("PanNumber"));
			rud.setCollectionAgentId(rs.getLong("CollectionAgentId"));

			return rud;
		});
	}

}