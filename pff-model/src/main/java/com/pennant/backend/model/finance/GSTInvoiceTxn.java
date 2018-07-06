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
 * FileName    		:  GSTInvoiceTxn.java	                                                * 	  
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
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Entity;

public class GSTInvoiceTxn implements Entity {

	private long invoiceId = Long.MIN_VALUE;
	private long transactionID = 0;
	private String invoiceNo;
	private Date invoiceDate;
	private BigDecimal invoice_Amt = BigDecimal.ZERO;
	
	private String companyCode;
	private String companyName;
	private String company_GSTIN;
	private String company_Address1;
	private String company_Address2;
	private String company_Address3;
	private String company_PINCode;
	private String company_State_Code;
	private String company_State_Name;
	private String hsnNumber;
	private String natureService;
	private String panNumber;
	private String loanAccountNo;
	
	private String customerID;
	private String customerName;
	private String customerStateCode;
	private String customerStateName;
	private String customerGSTIN;
	private String customerAddress;
	
	private String invoice_Status;
	private String invoiceType = "D";
	
	private List<GSTInvoiceTxnDetails> gstInvoiceTxnDetailsList = new ArrayList<GSTInvoiceTxnDetails>();
	
	public GSTInvoiceTxn() {
		super();
	}

	public long getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(long transactionID) {
		this.transactionID = transactionID;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public BigDecimal getInvoice_Amt() {
		return invoice_Amt;
	}

	public void setInvoice_Amt(BigDecimal invoice_Amt) {
		this.invoice_Amt = invoice_Amt;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompany_GSTIN() {
		return company_GSTIN;
	}

	public void setCompany_GSTIN(String company_GSTIN) {
		this.company_GSTIN = company_GSTIN;
	}

	public String getCompany_Address1() {
		return company_Address1;
	}

	public void setCompany_Address1(String company_Address1) {
		this.company_Address1 = company_Address1;
	}

	public String getCompany_Address2() {
		return company_Address2;
	}

	public void setCompany_Address2(String company_Address2) {
		this.company_Address2 = company_Address2;
	}

	public String getCompany_Address3() {
		return company_Address3;
	}

	public void setCompany_Address3(String company_Address3) {
		this.company_Address3 = company_Address3;
	}

	public String getCompany_PINCode() {
		return company_PINCode;
	}

	public void setCompany_PINCode(String company_PINCode) {
		this.company_PINCode = company_PINCode;
	}

	public String getCompany_State_Code() {
		return company_State_Code;
	}

	public void setCompany_State_Code(String company_State_Code) {
		this.company_State_Code = company_State_Code;
	}

	public String getHsnNumber() {
		return hsnNumber;
	}

	public void setHsnNumber(String hsnNumber) {
		this.hsnNumber = hsnNumber;
	}

	public String getNatureService() {
		return natureService;
	}

	public void setNatureService(String natureService) {
		this.natureService = natureService;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getLoanAccountNo() {
		return loanAccountNo;
	}

	public void setLoanAccountNo(String loanAccountNo) {
		this.loanAccountNo = loanAccountNo;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerStateCode() {
		return customerStateCode;
	}

	public void setCustomerStateCode(String customerStateCode) {
		this.customerStateCode = customerStateCode;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getInvoice_Status() {
		return invoice_Status;
	}

	public void setInvoice_Status(String invoice_Status) {
		this.invoice_Status = invoice_Status;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCustomerGSTIN() {
		return customerGSTIN;
	}

	public void setCustomerGSTIN(String customerGSTIN) {
		this.customerGSTIN = customerGSTIN;
	}

	public String getCompany_State_Name() {
		return company_State_Name;
	}

	public void setCompany_State_Name(String company_State_Name) {
		this.company_State_Name = company_State_Name;
	}

	public List<GSTInvoiceTxnDetails> getGstInvoiceTxnDetailsList() {
		return gstInvoiceTxnDetailsList;
	}

	public void setGstInvoiceTxnDetailsList(List<GSTInvoiceTxnDetails> gstInvoiceTxnDetailsList) {
		this.gstInvoiceTxnDetailsList = gstInvoiceTxnDetailsList;
	}

	public long getId() {
		return invoiceId;
	}

	public void setId(long invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	public String getCustomerStateName() {
		return customerStateName;
	}

	public void setCustomerStateName(String customerStateName) {
		this.customerStateName = customerStateName;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}
}
