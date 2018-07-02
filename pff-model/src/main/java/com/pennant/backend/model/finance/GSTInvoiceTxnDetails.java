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

public class GSTInvoiceTxnDetails {

	private long id = Long.MIN_VALUE;
	private long invoiceId = 0;
	private String invoiceNo;
	
	private String feeCode;
	private String feeDescription;
	private BigDecimal feeAmount = BigDecimal.ZERO;
	private BigDecimal CGST_RATE = BigDecimal.ZERO;
	private BigDecimal CGST_AMT = BigDecimal.ZERO;
	private BigDecimal IGST_RATE = BigDecimal.ZERO;
	private BigDecimal IGST_AMT = BigDecimal.ZERO;	
	private BigDecimal UGST_RATE = BigDecimal.ZERO;
	private BigDecimal UGST_AMT = BigDecimal.ZERO;	
	private BigDecimal SGST_RATE = BigDecimal.ZERO;
	private BigDecimal SGST_AMT = BigDecimal.ZERO;
	
	public GSTInvoiceTxnDetails() {
		super();
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}


	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	public String getFeeDescription() {
		return feeDescription;
	}

	public void setFeeDescription(String feeDescription) {
		this.feeDescription = feeDescription;
	}

	public BigDecimal getCGST_RATE() {
		return CGST_RATE;
	}

	public void setCGST_RATE(BigDecimal cGST_RATE) {
		CGST_RATE = cGST_RATE;
	}

	public BigDecimal getCGST_AMT() {
		return CGST_AMT;
	}

	public void setCGST_AMT(BigDecimal cGST_AMT) {
		CGST_AMT = cGST_AMT;
	}

	public BigDecimal getIGST_RATE() {
		return IGST_RATE;
	}

	public void setIGST_RATE(BigDecimal iGST_RATE) {
		IGST_RATE = iGST_RATE;
	}

	public BigDecimal getIGST_AMT() {
		return IGST_AMT;
	}

	public void setIGST_AMT(BigDecimal iGST_AMT) {
		IGST_AMT = iGST_AMT;
	}

	public BigDecimal getUGST_RATE() {
		return UGST_RATE;
	}

	public void setUGST_RATE(BigDecimal uGST_RATE) {
		UGST_RATE = uGST_RATE;
	}

	public BigDecimal getUGST_AMT() {
		return UGST_AMT;
	}

	public void setUGST_AMT(BigDecimal uGST_AMT) {
		UGST_AMT = uGST_AMT;
	}

	public BigDecimal getSGST_RATE() {
		return SGST_RATE;
	}

	public void setSGST_RATE(BigDecimal sGST_RATE) {
		SGST_RATE = sGST_RATE;
	}

	public BigDecimal getSGST_AMT() {
		return SGST_AMT;
	}

	public void setSGST_AMT(BigDecimal sGST_AMT) {
		SGST_AMT = sGST_AMT;
	}

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}

}
