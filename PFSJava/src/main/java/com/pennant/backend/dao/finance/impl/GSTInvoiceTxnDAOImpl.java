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

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennant.backend.model.finance.SeqGSTInvoice;
import com.pennanttech.pennapps.core.ConcurrencyException;
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
			gstInvoiceTxn.setInvoiceId(getNextId("Seq_Gst_Invoice_Txn"));
			logger.debug("get NextID:" + gstInvoiceTxn.getInvoiceId());
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert Into GST_Invoice_Txn");
		sql.append("(InvoiceId, TransactionID, InvoiceNo, InvoiceDate, Invoice_Amt, CompanyCode, CompanyName, Company_GSTIN, Company_Address1, Company_Address2, Company_Address3,");
		sql.append(" Company_PINCode, Company_State_Code, Company_State_Name, HsnNumber, NatureService, PanNumber, LoanAccountNo, CustomerID, CustomerName, CustomerGSTIN,");
		sql.append(" CustomerStateCode, CustomerStateName, CustomerAddress, Invoice_Status, InvoiceType)");
		sql.append(" Values (:InvoiceId, :TransactionID, :InvoiceNo, :InvoiceDate, :Invoice_Amt, :CompanyCode, :CompanyName, :Company_GSTIN, :Company_Address1, :Company_Address2, :Company_Address3,");
		sql.append(" :Company_PINCode, :Company_State_Code, :Company_State_Name, :HsnNumber, :NatureService, :PanNumber, :LoanAccountNo, :CustomerID, :CustomerName, :CustomerGSTIN,");
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
					invoiceDetail.setId(getNextId("Seq_Gst_Invoice_Txn_Details"));
					logger.debug("get NextID:" + invoiceDetail.getId());
				}
				invoiceDetail.setInvoiceId(gstInvoiceTxn.getInvoiceId());

				StringBuilder sqlQuery = new StringBuilder("insert into GST_Invoice_Txn_Details");
				sqlQuery.append("(Id, InvoiceId, FeeCode, FeeAmount, CGST_RATE, CGST_AMT, SGST_RATE, SGST_AMT, IGST_RATE, IGST_AMT, UGST_RATE, UGST_AMT)");
				sqlQuery.append("values (:Id, :InvoiceId, :FeeCode, :FeeAmount, :CGST_RATE, :CGST_AMT, :SGST_RATE, :SGST_AMT, :IGST_RATE, :IGST_AMT, :UGST_RATE, :UGST_AMT)");

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
		logger.debug("Entering");
		
		GSTInvoiceTxn gstInvoiceTxn = new GSTInvoiceTxn();
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select InvoiceId, TransactionID, InvoiceNo, InvoiceDate, Invoice_Amt, CompanyCode, CompanyName, Company_GSTIN, Company_Address1, Company_Address2, Company_Address3,");
		selectSql.append(" Company_PINCode, Company_State_Code, Company_State_Name, HsnNumber, NatureService, PanNumber, LoanAccountNo, CustomerID, CustomerName, CustomerGSTIN,");
		selectSql.append(" CustomerStateCode, CustomerStateName, CustomerAddress, Invoice_Status, InvoiceType");
		selectSql.append(" From GST_Invoice_Txn");
		selectSql.append(" Where InvoiceNo is null Order By InvoiceId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gstInvoiceTxn);
		RowMapper<GSTInvoiceTxn> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GSTInvoiceTxn.class);
		logger.debug("Leaving");
		
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	@Override
	public long saveSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Insert Into Seq_GST_Invoice");
		sql.append("(GstStateCode, TransactionType, SeqNo)");
		sql.append(" Values (:GstStateCode, :TransactionType, :SeqNo)");

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
		logger.debug("Entering");
		
		MapSqlParameterSource parameter = null;
		StringBuilder selectSql = new StringBuilder();
		long seqNo = 0;
		
		try {
			selectSql.append(" SELECT SeqNo From Seq_GST_Invoice");
			selectSql.append(" Where GstStateCode = :GstStateCode And TransactionType = :TransactionType");
			
			logger.debug("selectSql: " + selectSql.toString());
			
			parameter = new MapSqlParameterSource();
			parameter.addValue("GstStateCode", seqGSTInvoice.getGstStateCode());
			parameter.addValue("TransactionType", seqGSTInvoice.getTransactionType());
			
			seqNo = this.jdbcTemplate.queryForObject(selectSql.toString(), parameter, Long.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			selectSql = null;
			parameter = null;
			logger.debug("Leaving");
		}
		
		return seqNo;
	}
	
	@Override
	public SeqGSTInvoice getSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT GstStateCode, TransactionType, SeqNo");
		sql.append(" From Seq_GST_Invoice");
		sql.append(" Where GstStateCode = :GstStateCode And TransactionType = :TransactionType");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(seqGSTInvoice);
		RowMapper<SeqGSTInvoice> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SeqGSTInvoice.class);

		try {
			seqGSTInvoice = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			seqGSTInvoice = null;
		}

		logger.debug(Literal.LEAVING);
		return seqGSTInvoice;
	}	
}
