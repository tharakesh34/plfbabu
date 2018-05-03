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

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class GSTInvoiceTxnDAOImpl extends BasisNextidDaoImpl<GSTInvoiceTxn> implements GSTInvoiceTxnDAO {
	private static Logger logger = Logger.getLogger(GSTInvoiceTxnDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GSTInvoiceTxnDAOImpl() {
		super();
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public String save(GSTInvoiceTxn gstInvoiceTxn) {
		logger.debug(Literal.ENTERING);
		
		if (gstInvoiceTxn.getId() <= Long.MIN_VALUE) {
			gstInvoiceTxn.setId(getNextidviewDAO().getNextId("SeqGst_invoice_txn"));
			logger.debug("get NextID:" + gstInvoiceTxn.getId());
		}	
		
		String invoiceNo = gstInvoiceTxn.getCompanyCode() + "INV" + gstInvoiceTxn.getId();
		gstInvoiceTxn.setInvoiceNo(invoiceNo);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into GST_Invoice_Txn");
		sql.append("(TransactionID, InvoiceNo, InvoiceDate, Invoice_Amt, CompanyCode, CompanyName, Company_GSTIN, Company_Address1, Company_Address2, Company_Address3, ");
		sql.append("Company_PINCode, Company_State_Code, Company_State_Name, HsnNumber, NatureService, PanNumber, LoanAccountNo, CustomerID, CustomerName, CustomerGSTIN, CustomerStateCode, CustomerStateName, CustomerAddress, Invoice_Status)");
		sql.append("values (:TransactionID, :InvoiceNo, :InvoiceDate, :Invoice_Amt, :CompanyCode, :CompanyName, :Company_GSTIN, :Company_Address1, :Company_Address2, :Company_Address3,");
		sql.append(":Company_PINCode, :Company_State_Code, :Company_State_Name, :HsnNumber, :NatureService, :PanNumber, :LoanAccountNo, :CustomerID, :CustomerName, :CustomerGSTIN, :CustomerStateCode, :CustomerStateName, :CustomerAddress, :Invoice_Status)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(gstInvoiceTxn);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		
		if (CollectionUtils.isNotEmpty(gstInvoiceTxn.getGstInvoiceTxnDetailsList())) {
			for (GSTInvoiceTxnDetails details : gstInvoiceTxn.getGstInvoiceTxnDetailsList()) {
				details.setInvoiceNo(invoiceNo);
			}
			
			saveGSTInvoiceTxnDetails(gstInvoiceTxn.getGstInvoiceTxnDetailsList());
		}

		logger.debug(Literal.LEAVING);
		
		return gstInvoiceTxn.getInvoiceNo();
	}
	
	private void saveGSTInvoiceTxnDetails(List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into GST_Invoice_Txn_Details");
		sql.append("(InvoiceNo, FeeCode, FeeDescription, CGST_RATE, CGST_AMT, SGST_RATE, SGST_AMT, IGST_RATE, IGST_AMT, UGST_RATE, UGST_AMT)");
		sql.append("values (:InvoiceNo, :FeeCode, :FeeDescription, :CGST_RATE, :CGST_AMT, :SGST_RATE, :SGST_AMT, :IGST_RATE, :IGST_AMT, :UGST_RATE, :UGST_AMT)");

		// Execute the SQL, binding the arguments
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(gstInvoiceTxnDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

}
