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
 * * FileName : TdsReceivablesTxn.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * * Modified
 * Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.tds.receivables;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennanttech.model.adapter.DateFormatterAdapter;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>TdsReceivablesTxn table</b>.<br>
 *
 */
public class TdsReceivablesTxn extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long tanID;
	private long txnID;
	private Date tranDate;
	private long receivableID;
	private long receiptID;
	private String referenceType;
	private BigDecimal tdsAdjusted = BigDecimal.ZERO;
	private BigDecimal adjustmentAmount = BigDecimal.ZERO;
	private BigDecimal balanceAmount = BigDecimal.ZERO;
	private boolean newRecord = false;
	private TdsReceivablesTxn befImage;
	private LoggedInUser userDetails;
	private Date receiptDate;
	private BigDecimal receiptAmount;
	private BigDecimal tdsReceivable;
	private String finReference;
	private String tanNumber;
	private String tanHolderName;
	private String finTranYear;
	private String status;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date dateOfReceipt;
	private String receiptPurpose;
	private String certificateNumber;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date certificateDate;
	private BigDecimal certificateAmount = BigDecimal.ZERO;
	private BigDecimal certificateBalance = BigDecimal.ZERO;
	private String module;

	public boolean isNew() {
		return isNewRecord();
	}

	public TdsReceivablesTxn() {
		super();
	}

	public TdsReceivablesTxn(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("referenceType");
		excludeFields.add("tanID");
		excludeFields.add("tdsAdjusted");
		excludeFields.add("balanceAmount");
		excludeFields.add("tdsReceivable");
		excludeFields.add("finReference");
		excludeFields.add("tanNumber");
		excludeFields.add("tanHolderName");
		excludeFields.add("receiptAmount");
		excludeFields.add("receiptDate");
		excludeFields.add("dateOfReceipt");
		excludeFields.add("receiptPurpose");
		excludeFields.add("certificateNumber");
		excludeFields.add("certificateDate");
		excludeFields.add("certificateAmount");
		excludeFields.add("certificateBalance");
		return excludeFields;
	}

	public String getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}

	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public void setAdjustmentAmount(BigDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public TdsReceivablesTxn getBefImage() {
		return this.befImage;
	}

	public void setBefImage(TdsReceivablesTxn beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getTranDate() {
		return tranDate;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	public long getReceivableID() {
		return receivableID;
	}

	public void setReceivableID(long receivableID) {
		this.receivableID = receivableID;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTanID() {
		return tanID;
	}

	public void setTanID(long tanID) {
		this.tanID = tanID;
	}

	public BigDecimal getTdsAdjusted() {
		return tdsAdjusted;
	}

	public void setTdsAdjusted(BigDecimal tdsAdjusted) {
		this.tdsAdjusted = tdsAdjusted;
	}

	public BigDecimal getTdsReceivable() {
		return tdsReceivable;
	}

	public void setTdsReceivable(BigDecimal tdsReceivable) {
		this.tdsReceivable = tdsReceivable;
	}

	public String getTanNumber() {
		return tanNumber;
	}

	public void setTanNumber(String tanNumber) {
		this.tanNumber = tanNumber;
	}

	public String getTanHolderName() {
		return tanHolderName;
	}

	public void setTanHolderName(String tanHolderName) {
		this.tanHolderName = tanHolderName;
	}

	public long getTxnID() {
		return txnID;
	}

	public void setTxnID(long txnID) {
		this.txnID = txnID;
	}

	public String getFinTranYear() {
		return finTranYear;
	}

	public void setFinTranYear(String finTranYear) {
		this.finTranYear = finTranYear;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateOfReceipt() {
		return dateOfReceipt;
	}

	public void setDateOfReceipt(Date dateOfReceipt) {
		this.dateOfReceipt = dateOfReceipt;
	}

	public String getReceiptPurpose() {
		return receiptPurpose;
	}

	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public String getCertificateNumber() {
		return certificateNumber;
	}

	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}

	public Date getCertificateDate() {
		return certificateDate;
	}

	public void setCertificateDate(Date certificateDate) {
		this.certificateDate = certificateDate;
	}

	public BigDecimal getCertificateAmount() {
		return certificateAmount;
	}

	public void setCertificateAmount(BigDecimal certificateAmount) {
		this.certificateAmount = certificateAmount;
	}

	public BigDecimal getCertificateBalance() {
		return certificateBalance;
	}

	public void setCertificateBalance(BigDecimal certificateBalance) {
		this.certificateBalance = certificateBalance;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

}