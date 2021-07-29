package com.pennant.backend.model.finance;

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
 * * FileName : FinFeeReceipt.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 1-06-2017 * * Modified Date :
 * 1-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 1-06-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinFeeReceipt extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long feeID;
	private long receiptID;
	private long feeTypeId;
	private String receiptReference;
	private String transactionRef;
	private String favourNumber;
	private String feeTypeCode;
	private String feeTypeDesc;
	private String receiptType;
	private BigDecimal receiptAmount = BigDecimal.ZERO;
	private BigDecimal availableAmount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal remainingFee = BigDecimal.ZERO;
	private BigDecimal paidTds = BigDecimal.ZERO;
	private boolean exist = true;
	private FinFeeReceipt befImage;
	private LoggedInUser userDetails;

	private String vasReference;

	public FinFeeReceipt() {
		super();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("receiptReference");
		excludeFields.add("transactionRef");
		excludeFields.add("favourNumber");
		excludeFields.add("feeTypeCode");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("receiptType");
		excludeFields.add("feeTypeId");
		excludeFields.add("receiptAmount");
		excludeFields.add("availableAmount");
		excludeFields.add("remainingFee");
		excludeFields.add("exist");
		excludeFields.add("vasReference");
		excludeFields.add("paidTds");
		return excludeFields;
	}

	public FinFeeReceipt copyEntity() {
		FinFeeReceipt entity = new FinFeeReceipt();
		entity.setId(this.id);
		entity.setFeeID(this.feeID);
		entity.setReceiptID(this.receiptID);
		entity.setFeeTypeId(this.feeTypeId);
		entity.setReceiptReference(this.receiptReference);
		entity.setTransactionRef(this.transactionRef);
		entity.setFavourNumber(this.favourNumber);
		entity.setFeeTypeCode(this.feeTypeCode);
		entity.setFeeTypeDesc(this.feeTypeDesc);
		entity.setReceiptType(this.receiptType);
		entity.setReceiptAmount(this.receiptAmount);
		entity.setAvailableAmount(this.availableAmount);
		entity.setPaidAmount(this.paidAmount);
		entity.setRemainingFee(this.remainingFee);
		entity.setPaidTds(this.paidTds);
		entity.setNewRecord(super.isNewRecord());
		entity.setExist(this.exist);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setVasReference(this.vasReference);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getRemainingFee() {
		return remainingFee;
	}

	public void setRemainingFee(BigDecimal remainingFee) {
		this.remainingFee = remainingFee;
	}

	public FinFeeReceipt getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinFeeReceipt beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getFeeID() {
		return feeID;
	}

	public void setFeeID(long feeID) {
		this.feeID = feeID;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public long getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(long feeTypeId) {
		this.feeTypeId = feeTypeId;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getReceiptReference() {
		return this.receiptReference;
	}

	public void setReceiptReference(String receiptReference) {
		this.receiptReference = receiptReference;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getFavourNumber() {
		return favourNumber;
	}

	public void setFavourNumber(String favourNumber) {
		this.favourNumber = favourNumber;
	}

	public String getVasReference() {
		return vasReference;
	}

	public void setVasReference(String vasReference) {
		this.vasReference = vasReference;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

	public BigDecimal getPaidTds() {
		return paidTds;
	}

	public void setPaidTds(BigDecimal paidTds) {
		this.paidTds = paidTds;
	}

}
