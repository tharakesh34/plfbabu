/**
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
 * * FileName : GSTInvoiceTxnDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-04-2018 * * Modified
 * Date : 18-04-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-04-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennant.backend.model.finance.SeqGSTInvoice;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class GSTInvoiceTxnDAOImpl extends SequenceDao<GSTInvoiceTxn> implements GSTInvoiceTxnDAO {
	private static Logger logger = LogManager.getLogger(GSTInvoiceTxnDAOImpl.class);

	public GSTInvoiceTxnDAOImpl() {
		super();
	}

	@Override
	public long save(GSTInvoiceTxn gsti) {
		if (gsti.getInvoiceId() <= 0) {
			gsti.setInvoiceId(getNextValue("Seq_Gst_Invoice_Txn"));
		}

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" GST_Invoice_Txn");
		sql.append("(InvoiceId, TransactionID, InvoiceNo, InvoiceDate, Invoice_Amt, CompanyCode, CompanyName");
		sql.append(", Company_GSTIN, Company_Address1, Company_Address2, Company_Address3, Company_PINCode");
		sql.append(", Company_State_Code, Company_State_Name, HsnNumber, NatureService, PanNumber, LoanAccountNo");
		sql.append(", CustomerID, CustomerName, CustomerGSTIN, CustomerStateCode, CustomerStateName, CustomerAddress");
		sql.append(", Invoice_Status, InvoiceType, DueInvoiceId, InvoiceFor");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, gsti.getInvoiceId());
				ps.setLong(index++, gsti.getTransactionID());
				ps.setString(index++, gsti.getInvoiceNo());
				ps.setDate(index++, JdbcUtil.getDate(gsti.getInvoiceDate()));
				ps.setBigDecimal(index++, gsti.getInvoice_Amt());
				ps.setString(index++, gsti.getCompanyCode());
				ps.setString(index++, gsti.getCompanyName());
				ps.setString(index++, gsti.getCompany_GSTIN());
				ps.setString(index++, gsti.getCompany_Address1());
				ps.setString(index++, gsti.getCompany_Address2());
				ps.setString(index++, gsti.getCompany_Address3());
				ps.setString(index++, gsti.getCompany_PINCode());
				ps.setString(index++, gsti.getCompany_State_Code());
				ps.setString(index++, gsti.getCompany_State_Name());
				ps.setString(index++, gsti.getHsnNumber());
				ps.setString(index++, gsti.getNatureService());
				ps.setString(index++, gsti.getPanNumber());
				ps.setString(index++, gsti.getLoanAccountNo());
				ps.setString(index++, gsti.getCustomerID());
				ps.setString(index++, gsti.getCustomerName());
				ps.setString(index++, gsti.getCustomerGSTIN());
				ps.setString(index++, gsti.getCustomerStateCode());
				ps.setString(index++, gsti.getCustomerStateName());
				ps.setString(index++, gsti.getCustomerAddress());
				ps.setString(index++, gsti.getInvoice_Status());
				ps.setString(index++, gsti.getInvoiceType());
				ps.setObject(index++, gsti.getDueInvoiceId());
				ps.setString(index, gsti.getInvoiceFor());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		if (CollectionUtils.isEmpty(gsti.getGstInvoiceTxnDetailsList())) {
			return gsti.getInvoiceId();
		}

		for (GSTInvoiceTxnDetails gstid : gsti.getGstInvoiceTxnDetailsList()) {

			if (gstid.getId() <= 0) {
				gstid.setId(getNextValue("Seq_Gst_Invoice_Txn_Details"));
			}

			gstid.setInvoiceId(gsti.getInvoiceId());

			sql = new StringBuilder("Insert into");
			sql.append(" GST_Invoice_Txn_Details");
			sql.append(" (Id, InvoiceId, FeeCode, FeeAmount, CGST_RATE, CGST_AMT, SGST_RATE, SGST_AMT, IGST_RATE");
			sql.append(", IGST_AMT, UGST_RATE, UGST_AMT, CESS_RATE, CESS_AMT");
			sql.append(") values(");
			sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			sql.append(")");

			logger.debug(Literal.SQL + sql.toString());

			try {
				jdbcOperations.update(sql.toString(), ps -> {
					int index = 1;

					ps.setLong(index++, gstid.getId());
					ps.setLong(index++, gstid.getInvoiceId());
					ps.setString(index++, gstid.getFeeCode());
					ps.setBigDecimal(index++, gstid.getFeeAmount());
					ps.setBigDecimal(index++, gstid.getCGST_RATE());
					ps.setBigDecimal(index++, gstid.getCGST_AMT());
					ps.setBigDecimal(index++, gstid.getSGST_RATE());
					ps.setBigDecimal(index++, gstid.getSGST_AMT());
					ps.setBigDecimal(index++, gstid.getIGST_RATE());
					ps.setBigDecimal(index++, gstid.getIGST_AMT());
					ps.setBigDecimal(index++, gstid.getUGST_RATE());
					ps.setBigDecimal(index++, gstid.getUGST_AMT());
					ps.setBigDecimal(index++, gstid.getCESS_RATE());
					ps.setBigDecimal(index, gstid.getCESS_AMT());
				});

			} catch (DuplicateKeyException e) {
				throw new ConcurrencyException(e);
			}
		}

		return gsti.getInvoiceId();
	}

	@Override
	public void updateGSTInvoiceNo(GSTInvoiceTxn invoice) {
		String sql = "Update GST_Invoice_Txn Set InvoiceNo = ? Where InvoiceId = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setString(index++, invoice.getInvoiceNo());
			ps.setLong(index, invoice.getInvoiceId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public List<GSTInvoiceTxn> getGSTInvoiceTxnList() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" InvoiceId, TransactionID, InvoiceNo, InvoiceDate, Invoice_Amt, CompanyCode, CompanyName");
		sql.append(", Company_GSTIN, Company_Address1, Company_Address2, Company_Address3, Company_PINCode");
		sql.append(", Company_State_Code, Company_State_Name, HsnNumber, NatureService, PanNumber");
		sql.append(", LoanAccountNo, CustomerID, CustomerName, CustomerGSTIN, CustomerStateCode, CustomerStateName");
		sql.append(", CustomerAddress, Invoice_Status, InvoiceType, DueInvoiceId, InvoiceFor");
		sql.append(" From GST_Invoice_Txn");
		sql.append(" Where InvoiceNo is null");

		logger.debug(Literal.SQL + sql.toString());

		List<GSTInvoiceTxn> list = this.jdbcOperations.query(sql.toString(), ps -> {
		}, (rs, i) -> {
			GSTInvoiceTxn gstIT = new GSTInvoiceTxn();

			gstIT.setInvoiceId(rs.getLong("InvoiceId"));
			gstIT.setTransactionID(rs.getLong("TransactionID"));
			gstIT.setInvoiceNo(rs.getString("InvoiceNo"));
			gstIT.setInvoiceDate(rs.getTimestamp("InvoiceDate"));
			gstIT.setInvoice_Amt(rs.getBigDecimal("Invoice_Amt"));
			gstIT.setCompanyCode(rs.getString("CompanyCode"));
			gstIT.setCompanyName(rs.getString("CompanyName"));
			gstIT.setCompany_GSTIN(rs.getString("Company_GSTIN"));
			gstIT.setCompany_Address1(rs.getString("Company_Address1"));
			gstIT.setCompany_Address2(rs.getString("Company_Address2"));
			gstIT.setCompany_Address3(rs.getString("Company_Address3"));
			gstIT.setCompany_PINCode(rs.getString("Company_PINCode"));
			gstIT.setCompany_State_Code(rs.getString("Company_State_Code"));
			gstIT.setCompany_State_Name(rs.getString("Company_State_Name"));
			gstIT.setHsnNumber(rs.getString("HsnNumber"));
			gstIT.setNatureService(rs.getString("NatureService"));
			gstIT.setPanNumber(rs.getString("PanNumber"));
			gstIT.setLoanAccountNo(rs.getString("LoanAccountNo"));
			gstIT.setCustomerID(rs.getString("CustomerID"));
			gstIT.setCustomerName(rs.getString("CustomerName"));
			gstIT.setCustomerGSTIN(rs.getString("CustomerGSTIN"));
			gstIT.setCustomerStateCode(rs.getString("CustomerStateCode"));
			gstIT.setCustomerStateName(rs.getString("CustomerStateName"));
			gstIT.setCustomerAddress(rs.getString("CustomerAddress"));
			gstIT.setInvoice_Status(rs.getString("Invoice_Status"));
			gstIT.setInvoiceType(rs.getString("InvoiceType"));
			gstIT.setDueInvoiceId(JdbcUtil.getLong(rs.getObject("DueInvoiceId")));
			gstIT.setInvoiceFor(rs.getString("InvoiceFor"));

			return gstIT;
		});

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getInvoiceId(), l2.getInvoiceId()))
				.collect(Collectors.toList());
	}

	@Override
	public long saveSeqGSTInvoice(SeqGSTInvoice invoice) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" GST_INVOICE_SEQUENCES");
		sql.append(" (EntityCode, StateCode, TransactionType, MonthYear");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setString(index++, invoice.getEntityCode());
					ps.setString(index++, invoice.getStateCode());
					ps.setString(index++, invoice.getTransactionType());
					ps.setString(index, invoice.getMonthYear());
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return invoice.getSeqNo();
	}

	@Override
	public void updateSeqGSTInvoice(SeqGSTInvoice invoice) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" GST_INVOICE_SEQUENCES");
		sql.append(" Set SeqNo = ?");

		if (invoice.getMonthYear() != null) {
			sql.append(", MonthYear = ?");
		}

		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, invoice.getSeqNo());

			if (invoice.getMonthYear() != null) {
				ps.setString(index++, invoice.getMonthYear());
			}

			ps.setLong(index, invoice.getID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public SeqGSTInvoice getSeqNoFromSeqGSTInvoice(SeqGSTInvoice invoice) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, EntityCode, StateCode, TransactionType, MonthYear, SeqNo");
		sql.append(" from GST_INVOICE_SEQUENCES");
		sql.append(" Where EntityCode = ? and StateCode = ? and TransactionType = ?");

		logger.debug(Literal.SQL + sql.toString());

		Object[] args = new Object[] { invoice.getEntityCode(), invoice.getStateCode(), invoice.getTransactionType() };

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				SeqGSTInvoice gsi = new SeqGSTInvoice();

				gsi.setID(rs.getLong("ID"));
				gsi.setEntityCode(rs.getString("EntityCode"));
				gsi.setStateCode(rs.getString("StateCode"));
				gsi.setTransactionType(rs.getString("TransactionType"));
				gsi.setMonthYear(rs.getString("MonthYear"));
				gsi.setSeqNo(rs.getLong("SeqNo"));

				return gsi;
			}, args);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public SeqGSTInvoice getSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, EntityCode, StateCode, TransactionType, MonthYear, SeqNo");
		sql.append(" from GST_INVOICE_SEQUENCES");
		sql.append(" Where EntityCode = ? and StateCode = ? and TransactionType = ?");

		logger.debug(Literal.SQL + sql.toString());

		Object[] args = new Object[] { seqGSTInvoice.getEntityCode(), seqGSTInvoice.getStateCode(),
				seqGSTInvoice.getTransactionType() };
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				SeqGSTInvoice sgi = new SeqGSTInvoice();

				sgi.setID(rs.getLong("ID"));
				sgi.setEntityCode(rs.getString("EntityCode"));
				sgi.setStateCode(rs.getString("StateCode"));
				sgi.setTransactionType(rs.getString("TransactionType"));
				sgi.setMonthYear(rs.getString("MonthYear"));
				sgi.setSeqNo(rs.getLong("SeqNo"));

				return sgi;
			}, args);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isGstInvoiceExist(String custCif, String finReference, String invoiceType, Date fromDate,
			Date toDate) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" InvoiceId, InvoiceNo");
		sql.append(" From GST_Invoice_Txn");
		sql.append(" Where InvoiceType = ?");

		if (StringUtils.isNotBlank(custCif)) {
			sql.append(" and CustomerID = ?");
		}

		if (StringUtils.isNotBlank(finReference)) {
			sql.append(" and LoanAccountNo = ?");
		}

		if (fromDate != null && toDate != null) {
			sql.append(" and InvoiceDate >= ? and InvoiceDate <= ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		List<GSTInvoiceTxn> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, invoiceType);

			if (StringUtils.isNotBlank(custCif)) {
				ps.setString(index++, custCif);
			}

			if (StringUtils.isNotBlank(finReference)) {
				ps.setString(index++, finReference);
			}

			if (fromDate != null && toDate != null) {
				ps.setDate(index++, JdbcUtil.getDate(fromDate));
				ps.setDate(index, JdbcUtil.getDate(toDate));

			}
		}, (rs, rowNum) -> {
			GSTInvoiceTxn git = new GSTInvoiceTxn();

			git.setInvoiceId(rs.getLong("InvoiceId"));
			git.setInvoiceNo(rs.getString("InvoiceNo"));

			return git;
		});

		if (CollectionUtils.isNotEmpty(list)) {
			for (GSTInvoiceTxn invoiceTxn : list) {
				if (StringUtils.isNotBlank(invoiceTxn.getInvoiceNo())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void updateSeqNo() {
		String sql = "Update Seq_GST_Invoice Set SeqNo = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> ps.setLong(1, 0));
	}

	@Override
	public void deleteSeqGSTInvoice(SeqGSTInvoice invoice) {
		String sql = "Delete From GST_INVOICE_SEQUENCES Where EntityCode = ? and StateCode = ? and TransactionType = ?";

		logger.debug(Literal.SQL + sql);

		Object[] args = new Object[] { invoice.getEntityCode(), invoice.getStateCode(), invoice.getTransactionType() };
		try {
			jdbcOperations.update(sql, args);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public Long getInvoiceIdByTranId(Long tranId) {
		String sql = "Select InvoiceId From gst_invoice_txn Where TransactionId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> JdbcUtil.getLong(rs.getLong("InvoiceId")),
					tranId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<GSTInvoiceTxnDetails> getTxnListByInvoiceId(Long invoiceId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, InvoiceId, FeeCode, FeeAmount, CGST_RATE, CGST_AMT, SGST_RATE, SGST_AMT, IGST_RATE");
		sql.append(", IGST_AMT, UGST_RATE, UGST_AMT, CESS_RATE, CESS_AMT");
		sql.append(" from gst_invoice_txn_details");
		sql.append(" Where InvoiceId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, invoiceId);
		}, (rs, rowNum) -> {
			GSTInvoiceTxnDetails txndetails = new GSTInvoiceTxnDetails();

			txndetails.setId(rs.getLong("Id"));
			txndetails.setInvoiceId(rs.getLong("InvoiceId"));
			txndetails.setFeeCode(rs.getString("FeeCode"));
			txndetails.setFeeAmount(rs.getBigDecimal("FeeAmount"));
			txndetails.setCGST_RATE(rs.getBigDecimal("CGST_RATE"));
			txndetails.setCGST_AMT(rs.getBigDecimal("CGST_AMT"));
			txndetails.setSGST_RATE(rs.getBigDecimal("SGST_RATE"));
			txndetails.setSGST_AMT(rs.getBigDecimal("SGST_AMT"));
			txndetails.setIGST_RATE(rs.getBigDecimal("IGST_RATE"));
			txndetails.setIGST_AMT(rs.getBigDecimal("IGST_AMT"));
			txndetails.setUGST_RATE(rs.getBigDecimal("UGST_RATE"));
			txndetails.setUGST_AMT(rs.getBigDecimal("UGST_AMT"));
			txndetails.setCESS_RATE(rs.getBigDecimal("CESS_RATE"));
			txndetails.setCESS_AMT(rs.getBigDecimal("CESS_AMT"));

			return txndetails;
		});
	}
}
