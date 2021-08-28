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
 * * FileName : FinFeeReceiptDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 1-06-2017 * * Modified
 * Date : 1-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 1-06-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.OverdueTaxMovement;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinFeeReceipt model</b> class.<br>
 * 
 */
public class FinODAmzTaxDetailDAOImpl extends SequenceDao<FinODAmzTaxDetail> implements FinODAmzTaxDetailDAO {
	private static Logger logger = LogManager.getLogger(FinODAmzTaxDetailDAOImpl.class);

	public FinODAmzTaxDetailDAOImpl() {
		super();
	}

	@Override
	public long save(FinODAmzTaxDetail oatd) {
		if (oatd.getTaxSeqId() == Long.MIN_VALUE || oatd.getTaxSeqId() == 0) {
			oatd.setTaxSeqId(getNextValue("SeqFinODAmzTaxDetail"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinODAmzTaxDetail(");
		sql.append("TaxSeqId, FinID, FinReference, ValueDate, PostDate, TaxFor, Amount, TaxType");
		sql.append(", CGST, SGST, UGST, IGST, TotalGST, PaidAmount, WaivedAmount, InvoiceID)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, oatd.getTaxSeqId());
			ps.setLong(index++, oatd.getFinID());
			ps.setString(index++, oatd.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(oatd.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(oatd.getPostDate()));
			ps.setString(index++, oatd.getTaxFor());
			ps.setBigDecimal(index++, oatd.getAmount());
			ps.setString(index++, oatd.getTaxType());
			ps.setBigDecimal(index++, oatd.getCGST());
			ps.setBigDecimal(index++, oatd.getSGST());
			ps.setBigDecimal(index++, oatd.getUGST());
			ps.setBigDecimal(index++, oatd.getIGST());
			ps.setBigDecimal(index++, oatd.getTotalGST());
			ps.setBigDecimal(index++, oatd.getPaidAmount());
			ps.setBigDecimal(index++, oatd.getWaivedAmount());
			ps.setLong(index++, JdbcUtil.getLong(oatd.getInvoiceID()));
		});

		return oatd.getTaxSeqId();
	}

	@Override
	public void saveTaxReceivable(FinTaxReceivable tr) {
		StringBuilder sql = new StringBuilder("Insert Into FinTaxReceivable");
		sql.append(" (FinID, FinReference, TaxFor, ReceivableAmount, CGST, IGST, UGST, SGST, CESS)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, tr.getFinID());
			ps.setString(index++, tr.getFinReference());
			ps.setString(index++, tr.getTaxFor());
			ps.setBigDecimal(index++, tr.getReceivableAmount());
			ps.setBigDecimal(index++, tr.getCGST());
			ps.setBigDecimal(index++, tr.getIGST());
			ps.setBigDecimal(index++, tr.getUGST());
			ps.setBigDecimal(index++, tr.getSGST());
			ps.setBigDecimal(index++, tr.getCESS());

		});
	}

	@Override
	public FinTaxReceivable getFinTaxReceivable(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, TaxFor, ReceivableAmount, CGST, IGST, UGST, SGST, CESS");
		sql.append(" From FinTaxReceivable");
		sql.append(" Where FinID = ? and TaxFor = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinTaxReceivable tr = new FinTaxReceivable();

				tr.setFinID(rs.getLong("FinID"));
				tr.setFinReference(rs.getString("FinReference"));
				tr.setTaxFor(rs.getString("TaxFor"));
				tr.setReceivableAmount(rs.getBigDecimal("ReceivableAmount"));
				tr.setCGST(rs.getBigDecimal("CGST"));
				tr.setIGST(rs.getBigDecimal("IGST"));
				tr.setUGST(rs.getBigDecimal("UGST"));
				tr.setSGST(rs.getBigDecimal("SGST"));
				tr.setCESS(rs.getBigDecimal("CESS"));

				return tr;
			}, finID, type);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public void updateTaxReceivable(FinTaxReceivable tr) {
		StringBuilder sql = new StringBuilder("Update FinTaxReceivable");
		sql.append(" Set ReceivableAmount = ?, CGST = ?, IGST = ?, UGST = ?, SGST = ?, CESS = ?");
		sql.append(" Where FinID = ? and TaxFor= ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, tr.getReceivableAmount());
			ps.setBigDecimal(index++, tr.getCGST());
			ps.setBigDecimal(index++, tr.getIGST());
			ps.setBigDecimal(index++, tr.getUGST());
			ps.setBigDecimal(index++, tr.getSGST());
			ps.setBigDecimal(index++, tr.getCESS());
			ps.setLong(index++, tr.getFinID());
			ps.setString(index, tr.getTaxFor());

		});
	}

	@Override
	public void saveTaxIncome(FinTaxIncomeDetail tid) {
		StringBuilder sql = new StringBuilder("Insert Into FinTaxIncomeDetail");
		sql.append(" (RepayID, TaxFor, ReceivedAmount, CGST, IGST, UGST, SGST, CESS)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, tid.getRepayID());
			ps.setString(index, tid.getTaxFor());
			ps.setBigDecimal(index++, tid.getReceivedAmount());
			ps.setBigDecimal(index++, tid.getCGST());
			ps.setBigDecimal(index++, tid.getIGST());
			ps.setBigDecimal(index++, tid.getUGST());
			ps.setBigDecimal(index++, tid.getSGST());
			ps.setBigDecimal(index++, tid.getCESS());
		});
	}

	@Override
	public FinTaxIncomeDetail getFinTaxIncomeDetail(long repayID, String type) {
		String sql = "Select RepayID, TaxFor, ReceivedAmount, CGST, IGST, UGST, SGST, CESS From FinTaxIncomeDetail Where RepayID = ? and TaxFor = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, (rs, num) -> {
				FinTaxIncomeDetail tid = new FinTaxIncomeDetail();

				tid.setRepayID(rs.getLong("RepayID"));
				tid.setTaxFor(rs.getString("TaxFor"));
				tid.setReceivedAmount(rs.getBigDecimal("ReceivedAmount"));
				tid.setCGST(rs.getBigDecimal("CGST"));
				tid.setIGST(rs.getBigDecimal("IGST"));
				tid.setUGST(rs.getBigDecimal("UGST"));
				tid.setSGST(rs.getBigDecimal("SGST"));
				tid.setCESS(rs.getBigDecimal("CESS"));

				return tid;
			}, repayID, type);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public boolean isDueCreatedForDate(long finID, Date valueDate, String taxFor) {
		String sql = "Select Count(FinID) From FinODAmzTaxDetail Where FinID = ? and ValueDate= ? and TaxFor = ?";

		logger.debug(Literal.SQL + sql);

		try {
			Object[] obj = new Object[] { finID, JdbcUtil.getDate(valueDate), taxFor };
			return this.jdbcOperations.queryForObject(sql, Integer.class, obj) > 0 ? true : false;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public List<FinODAmzTaxDetail> getFinODAmzTaxDetail(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TaxSeqId, FinID, FinReference, ValueDate, TaxFor, Amount, TaxType, TotalGST");
		sql.append(", PaidAmount, WaivedAmount, InvoiceID");
		sql.append(" From FinODAmzTaxDetail Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
		}, (rs, num) -> {
			FinODAmzTaxDetail oda = new FinODAmzTaxDetail();

			oda.setTaxSeqId(rs.getLong("TaxSeqId"));
			oda.setFinID(rs.getLong("FinID"));
			oda.setFinReference(rs.getString("FinReference"));
			oda.setValueDate(rs.getDate("ValueDate"));
			oda.setTaxFor(rs.getString("TaxFor"));
			oda.setAmount(rs.getBigDecimal("Amount"));
			oda.setTaxType(rs.getString("TaxType"));
			oda.setTotalGST(rs.getBigDecimal("TotalGST"));
			oda.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			oda.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			oda.setInvoiceID(rs.getLong("InvoiceID"));

			return oda;
		});

	}

	@Override
	public List<FinODAmzTaxDetail> getODTaxList(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TaxSeqId, FinID, FinReference, ValueDate, TaxFor, Amount, TaxType, TotalGST");
		sql.append(", PaidAmount, WaivedAmount, InvoiceID");
		sql.append(" From FinODAmzTaxDetail Where FinID = ? and (Amount - PaidAmount - WaivedAmount) > ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setInt(index++, 0);
		}, (rs, num) -> {
			FinODAmzTaxDetail oda = new FinODAmzTaxDetail();

			oda.setTaxSeqId(rs.getLong("TaxSeqId"));
			oda.setFinID(rs.getLong("FinID"));
			oda.setFinReference(rs.getString("FinReference"));
			oda.setValueDate(rs.getDate("ValueDate"));
			oda.setTaxFor(rs.getString("TaxFor"));
			oda.setAmount(rs.getBigDecimal("Amount"));
			oda.setTaxType(rs.getString("TaxType"));
			oda.setTotalGST(rs.getBigDecimal("TotalGST"));
			oda.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			oda.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			oda.setInvoiceID(rs.getLong("InvoiceID"));

			return oda;
		});
	}

	@Override
	public List<FinTaxIncomeDetail> getFinTaxIncomeList(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T.RepayID, H.ValueDate PostDate, D.ReceivedDate ValueDate, T.TaxFor, T.ReceivedAmount");
		sql.append(", T.CGST, T.IGST, T.UGST, T.SGST, T.CESS  From FinTaxIncomeDetail T ");
		sql.append(" Inner Join FInRepayHeader H on T.RepayID = H.RepayID ");
		sql.append(" Inner Join FinReceiptDetail D on H.ReceiptSeqID = D.ReceiptSeqID and D.Status NOT IN (?, ?)");
		sql.append(" Where H.FinID = ? and T.TaxFor = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "B");
			ps.setString(index++, "C");
			ps.setLong(index++, finID);
			ps.setString(index++, type);

		}, (rs, num) -> {
			FinTaxIncomeDetail tid = new FinTaxIncomeDetail();

			tid.setRepayID(rs.getLong("RepayID"));
			tid.setPostDate(rs.getDate("PostDate"));
			tid.setValueDate(rs.getDate("ValueDate"));
			tid.setTaxFor(rs.getString("TaxFor"));
			tid.setReceivedAmount(rs.getBigDecimal("ReceivedAmount"));
			tid.setCGST(rs.getBigDecimal("CGST"));
			tid.setIGST(rs.getBigDecimal("IGST"));
			tid.setUGST(rs.getBigDecimal("UGST"));
			tid.setSGST(rs.getBigDecimal("SGST"));
			tid.setCESS(rs.getBigDecimal("CESS"));

			return tid;
		});
	}

	@Override
	public void updateODTaxDueList(List<FinODAmzTaxDetail> updateDueList) {
		String sql = "Update FinODAmzTaxDetail Set PaidAmount = ?, WaivedAmount = ? Where FinID = ? and valueDate = ? and TaxFor = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinODAmzTaxDetail oda = updateDueList.get(i);
				int index = 1;

				ps.setBigDecimal(index++, oda.getPaidAmount());
				ps.setBigDecimal(index++, oda.getWaivedAmount());
				ps.setLong(index++, oda.getFinID());
				ps.setDate(index++, JdbcUtil.getDate(oda.getValueDate()));
				ps.setString(index++, oda.getTaxFor());
			}

			@Override
			public int getBatchSize() {
				return updateDueList.size();
			}
		});
	}

	@Override
	public void saveTaxList(List<OverdueTaxMovement> taxMovements) {
		StringBuilder sql = new StringBuilder("Insert Into OverdueTaxMovements");
		sql.append(" (InvoiceID, ValueDate, SchDate, TaxFor, FinID, FinReference, PaidAmount");
		sql.append(", WaivedAmount, TaxHeaderId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					OverdueTaxMovement oda = taxMovements.get(i);
					int index = 1;

					ps.setLong(index++, oda.getInvoiceID());
					ps.setDate(index++, JdbcUtil.getDate(oda.getValueDate()));
					ps.setDate(index++, JdbcUtil.getDate(oda.getSchDate()));
					ps.setString(index++, oda.getTaxFor());
					ps.setLong(index++, oda.getFinID());
					ps.setString(index++, oda.getFinReference());
					ps.setBigDecimal(index++, oda.getPaidAmount());
					ps.setBigDecimal(index++, oda.getWaivedAmount());
					ps.setLong(index++, oda.getTaxHeaderId());
				}

				@Override
				public int getBatchSize() {
					return taxMovements.size();
				}
			});
		} catch (Exception e) {
			throw e;
		}
	}

}