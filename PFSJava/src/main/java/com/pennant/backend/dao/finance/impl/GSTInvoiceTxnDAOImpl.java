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
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  GSTInvoiceTxnDAOImpl.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-04-2018    														*
 *                                                                  						*
 * Modified Date    :  18-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-04-2018       Pennant	                 0.1                                            * 
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennant.backend.model.finance.SeqGSTInvoice;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class GSTInvoiceTxnDAOImpl extends SequenceDao<GSTInvoiceTxn> implements GSTInvoiceTxnDAO {
	private static Logger logger = Logger.getLogger(GSTInvoiceTxnDAOImpl.class);

	public GSTInvoiceTxnDAOImpl() {
		super();
	}

	@Override
	public long save(GSTInvoiceTxn gstInvoiceTxn) {
		logger.debug(Literal.ENTERING);

		if (gstInvoiceTxn.getInvoiceId() <= 0) {
			gstInvoiceTxn.setInvoiceId(getNextValue("Seq_Gst_Invoice_Txn"));
			logger.debug("get NextID:" + gstInvoiceTxn.getInvoiceId());
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert Into GST_Invoice_Txn");
		sql.append(
				"(InvoiceId, TransactionID, InvoiceNo, InvoiceDate, Invoice_Amt, CompanyCode, CompanyName, Company_GSTIN, Company_Address1, Company_Address2, Company_Address3,");
		sql.append(
				" Company_PINCode, Company_State_Code, Company_State_Name, HsnNumber, NatureService, PanNumber, LoanAccountNo, CustomerID, CustomerName, CustomerGSTIN,");
		sql.append(" CustomerStateCode, CustomerStateName, CustomerAddress, Invoice_Status, InvoiceType)");
		sql.append(
				" Values (:InvoiceId, :TransactionID, :InvoiceNo, :InvoiceDate, :Invoice_Amt, :CompanyCode, :CompanyName, :Company_GSTIN, :Company_Address1, :Company_Address2, :Company_Address3,");
		sql.append(
				" :Company_PINCode, :Company_State_Code, :Company_State_Name, :HsnNumber, :NatureService, :PanNumber, :LoanAccountNo, :CustomerID, :CustomerName, :CustomerGSTIN,");
		sql.append(" :CustomerStateCode, :CustomerStateName, :CustomerAddress, :Invoice_Status, :InvoiceType)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(gstInvoiceTxn);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		if (CollectionUtils.isNotEmpty(gstInvoiceTxn.getGstInvoiceTxnDetailsList())) {
			for (GSTInvoiceTxnDetails invoiceDetail : gstInvoiceTxn.getGstInvoiceTxnDetailsList()) {
				if (invoiceDetail.getId() <= 0) {
					invoiceDetail.setId(getNextValue("Seq_Gst_Invoice_Txn_Details"));
					logger.debug("get NextID:" + invoiceDetail.getId());
				}
				invoiceDetail.setInvoiceId(gstInvoiceTxn.getInvoiceId());

				StringBuilder sqlQuery = new StringBuilder("insert into GST_Invoice_Txn_Details");
				sqlQuery.append(
						"(Id, InvoiceId, FeeCode, FeeAmount, CGST_RATE, CGST_AMT, SGST_RATE, SGST_AMT, IGST_RATE, IGST_AMT, UGST_RATE, UGST_AMT)");
				sqlQuery.append(
						"values (:Id, :InvoiceId, :FeeCode, :FeeAmount, :CGST_RATE, :CGST_AMT, :SGST_RATE, :SGST_AMT, :IGST_RATE, :IGST_AMT, :UGST_RATE, :UGST_AMT)");

				// Execute the SQL, binding the arguments
				logger.trace(Literal.SQL + sqlQuery.toString());
				paramSource = new BeanPropertySqlParameterSource(invoiceDetail);

				try {
					jdbcTemplate.update(sqlQuery.toString(), paramSource);
				} catch (DuplicateKeyException e) {
					throw new ConcurrencyException(e);
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return gstInvoiceTxn.getInvoiceId();
	}

	@Override
	public void updateGSTInvoiceNo(GSTInvoiceTxn gstInvoiceTxn) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("Update GST_Invoice_Txn");
		sql.append(" Set InvoiceNo = :InvoiceNo");
		sql.append(" Where InvoiceId = :InvoiceId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(gstInvoiceTxn);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<GSTInvoiceTxn> getGSTInvoiceTxnList() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" InvoiceId, TransactionID, InvoiceNo, InvoiceDate, Invoice_Amt, CompanyCode, CompanyName");
		sql.append(", Company_GSTIN, Company_Address1, Company_Address2, Company_Address3, Company_PINCode");
		sql.append(", Company_State_Code, Company_State_Name, HsnNumber, NatureService, PanNumber");
		sql.append(", LoanAccountNo, CustomerID, CustomerName, CustomerGSTIN, CustomerStateCode, CustomerStateName");
		sql.append(", CustomerAddress, Invoice_Status, InvoiceType");
		sql.append(" from GST_Invoice_Txn");
		sql.append(" Where InvoiceNo is null");
		sql.append(" Order By InvoiceId");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					// FIXME
				}
			}, new RowMapper<GSTInvoiceTxn>() {
				@Override
				public GSTInvoiceTxn mapRow(ResultSet rs, int rowNum) throws SQLException {
					GSTInvoiceTxn git = new GSTInvoiceTxn();

					git.setInvoiceId(rs.getLong("InvoiceId"));
					git.setTransactionID(rs.getLong("TransactionID"));
					git.setInvoiceNo(rs.getString("InvoiceNo"));
					git.setInvoiceDate(rs.getTimestamp("InvoiceDate"));
					git.setInvoice_Amt(rs.getBigDecimal("Invoice_Amt"));
					git.setCompanyCode(rs.getString("CompanyCode"));
					git.setCompanyName(rs.getString("CompanyName"));
					git.setCompany_GSTIN(rs.getString("Company_GSTIN"));
					git.setCompany_Address1(rs.getString("Company_Address1"));
					git.setCompany_Address2(rs.getString("Company_Address2"));
					git.setCompany_Address3(rs.getString("Company_Address3"));
					git.setCompany_PINCode(rs.getString("Company_PINCode"));
					git.setCompany_State_Code(rs.getString("Company_State_Code"));
					git.setCompany_State_Name(rs.getString("Company_State_Name"));
					git.setHsnNumber(rs.getString("HsnNumber"));
					git.setNatureService(rs.getString("NatureService"));
					git.setPanNumber(rs.getString("PanNumber"));
					git.setLoanAccountNo(rs.getString("LoanAccountNo"));
					git.setCustomerID(rs.getString("CustomerID"));
					git.setCustomerName(rs.getString("CustomerName"));
					git.setCustomerGSTIN(rs.getString("CustomerGSTIN"));
					git.setCustomerStateCode(rs.getString("CustomerStateCode"));
					git.setCustomerStateName(rs.getString("CustomerStateName"));
					git.setCustomerAddress(rs.getString("CustomerAddress"));
					git.setInvoice_Status(rs.getString("Invoice_Status"));
					git.setInvoiceType(rs.getString("InvoiceType"));

					return git;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public long saveSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert Into Seq_GST_Invoice");
		sql.append("(GstStateCode, TransactionType, SeqNo)");
		sql.append(" Values (:gstStateCode, :transactionType, :seqNo)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(seqGSTInvoice);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

		return seqGSTInvoice.getSeqNo();
	}

	@Override
	public void updateSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("Update Seq_GST_Invoice");
		sql.append(" Set SeqNo = :SeqNo");
		sql.append(" Where GstStateCode = :GstStateCode And TransactionType = :TransactionType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(seqGSTInvoice);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long getSeqNoFromSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" SeqNo");
		sql.append(" From Seq_GST_Invoice");
		sql.append(" Where GstStateCode = ? And TransactionType = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { seqGSTInvoice.getGstStateCode(), seqGSTInvoice.getTransactionType() }, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return 0;
	}

	@Override
	public SeqGSTInvoice getSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" GstStateCode, TransactionType, SeqNo");
		sql.append(" from Seq_GST_Invoice");
		sql.append(" Where GstStateCode = ? And TransactionType = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { seqGSTInvoice.getGstStateCode(), seqGSTInvoice.getTransactionType() },
					new RowMapper<SeqGSTInvoice>() {
						@Override
						public SeqGSTInvoice mapRow(ResultSet rs, int rowNum) throws SQLException {
							SeqGSTInvoice sgi = new SeqGSTInvoice();

							sgi.setGstStateCode(rs.getString("GstStateCode"));
							sgi.setTransactionType(rs.getString("TransactionType"));
							sgi.setSeqNo(rs.getLong("SeqNo"));

							return sgi;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public boolean isGstInvoiceExist(String custCif, String finReference, String invoiceType, Date fromDate,
			Date toDate) {
		logger.debug(Literal.ENTERING);

		List<GSTInvoiceTxn> list = new ArrayList<GSTInvoiceTxn>();

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" InvoiceId, InvoiceNo");
		sql.append(" from GST_Invoice_Txn");
		sql.append(" Where CustomerID = ? And InvoiceType = ?");

		if (StringUtils.isNotBlank(finReference)) {
			sql.append(" And LoanAccountNo = ?");
		}

		if (fromDate != null && toDate != null) {
			sql.append(" And InvoiceDate >= ? And InvoiceDate <= ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		try {
			list = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, custCif);
					ps.setString(index++, invoiceType);

					if (StringUtils.isNotBlank(finReference)) {
						ps.setString(index++, finReference);
					}

					if (fromDate != null && toDate != null) {
						ps.setDate(index++, JdbcUtil.getDate(fromDate));
						ps.setDate(index++, JdbcUtil.getDate(toDate));

					}
				}
			}, new RowMapper<GSTInvoiceTxn>() {
				@Override
				public GSTInvoiceTxn mapRow(ResultSet rs, int rowNum) throws SQLException {
					GSTInvoiceTxn git = new GSTInvoiceTxn();

					git.setInvoiceId(rs.getLong("InvoiceId"));
					git.setInvoiceNo(rs.getString("InvoiceNo"));

					return git;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		if (CollectionUtils.isNotEmpty(list)) {
			for (GSTInvoiceTxn invoiceTxn : list) {
				if (StringUtils.isNotBlank(invoiceTxn.getInvoiceNo())) {
					return true;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return false;
	}

	@Override
	public void updateSeqNo() {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("Update Seq_GST_Invoice");
		sql.append(" Set SeqNo = :SeqNo");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("SeqNo", 0);
		int recordCount = jdbcTemplate.update(sql.toString(), source);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from Seq_GST_Invoice ");
		sql.append(" where GstStateCode = :gstStateCode And TransactionType = :transactionType ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(seqGSTInvoice);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

}
