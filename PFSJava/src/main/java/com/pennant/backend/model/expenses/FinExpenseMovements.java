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
 * * FileName : FinExpenseMovements.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * * Modified
 * Date : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.expenses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.finance.FinanceMain;

public class FinExpenseMovements implements Serializable {
	private static final long serialVersionUID = 1L;

	private long finExpenseMovemntId = Long.MIN_VALUE;
	private long finExpenseId = 0;
	private long finID;
	private String finReference = null;
	private String modeType = null;
	private long uploadId = 0;
	private BigDecimal transactionAmount = BigDecimal.ZERO;
	private String transactionType = null;
	private Timestamp lastMntOn;
	private Date transactionDate;
	private String fileName;
	private long lastMntBy;
	private FinanceMain financeMain;
	private String expenseTypeCode;
	private long expenseTypeID;
	private Long LinkedTranId;
	private Long RevLinkedTranId;

	public FinExpenseMovements() {
		super();
	}

	public Set<String> getExcludeFields() {

		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("fileName");
		excludeFields.add("lastMntBy");
		excludeFields.add("financeMain");
		excludeFields.add("expenseTypeCode");
		excludeFields.add("expenseTypeID");

		return excludeFields;
	}

	public long getFinExpenseMovemntId() {
		return finExpenseMovemntId;
	}

	public void setFinExpenseMovemntId(long finExpenseMovemntId) {
		this.finExpenseMovemntId = finExpenseMovemntId;
	}

	public long getFinExpenseId() {
		return finExpenseId;
	}

	public void setFinExpenseId(long finExpenseId) {
		this.finExpenseId = finExpenseId;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getModeType() {
		return modeType;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public String getExpenseTypeCode() {
		return expenseTypeCode;
	}

	public void setExpenseTypeCode(String expenseTypeCode) {
		this.expenseTypeCode = expenseTypeCode;
	}

	public long getExpenseTypeID() {
		return expenseTypeID;
	}

	public void setExpenseTypeID(long expenseTypeID) {
		this.expenseTypeID = expenseTypeID;
	}

	public Long getLinkedTranId() {
		return LinkedTranId;
	}

	public void setLinkedTranId(Long linkedTranId) {
		LinkedTranId = linkedTranId;
	}

	public Long getRevLinkedTranId() {
		return RevLinkedTranId;
	}

	public void setRevLinkedTranId(Long revLinkedTranId) {
		RevLinkedTranId = revLinkedTranId;
	}

}