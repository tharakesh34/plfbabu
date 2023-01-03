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
 * * FileName : PaymentDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified Date :
 * 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.feerefund;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PaymentDetail table</b>.<br>
 * 
 */
public class FeeRefundDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long feeRefundDetailId = Long.MIN_VALUE;
	private long feeRefundId;
	private String receivableType;
	private BigDecimal totalAmount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal prevRefundAmount = BigDecimal.ZERO;
	private BigDecimal currRefundAmount = BigDecimal.ZERO;
	private BigDecimal availableAmount = BigDecimal.ZERO;
	private long receivableRefId;
	private long payableRefId;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private FeeRefundDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String feeTypeCode;
	private String feeTypeDesc;
	private String payableFeeTypeCode;
	private String payableFeeTypeDesc;
	private boolean taxApplicable = false;
	private String taxComponent;
	private Long taxHeaderId;
	private TaxHeader taxHeader;
	private BigDecimal adviseAmount = BigDecimal.ZERO;
	private BigDecimal prvGST = BigDecimal.ZERO;
	private ManualAdvise manualAdvise;

	public FeeRefundDetail() {
		super();
	}

	public FeeRefundDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("availableAmount");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("feeTypeCode");
		excludeFields.add("taxComponent");
		excludeFields.add("taxApplicable");
		excludeFields.add("finSource");
		excludeFields.add("taxHeader");
		excludeFields.add("adviseAmount");
		excludeFields.add("taxHeaderId");
		excludeFields.add("prvGST");
		excludeFields.add("manualAdvise");
		excludeFields.add("payableRefId");
		excludeFields.add("payableFeeTypeCode");
		excludeFields.add("payableFeeTypeDesc");
		return excludeFields;
	}

	public long getId() {
		return feeRefundDetailId;
	}

	public void setId(long id) {
		this.feeRefundDetailId = id;
	}

	public long getFeeRefundDetailId() {
		return feeRefundDetailId;
	}

	public void setFeeRefundDetailId(long feeRefundDetailId) {
		this.feeRefundDetailId = feeRefundDetailId;
	}

	public long getFeeRefundId() {
		return feeRefundId;
	}

	public void setFeeRefundId(long feeRefundId) {
		this.feeRefundId = feeRefundId;
	}

	public String getReceivableType() {
		return receivableType;
	}

	public void setReceivableType(String receivableType) {
		this.receivableType = receivableType;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getPrevRefundAmount() {
		return prevRefundAmount;
	}

	public void setPrevRefundAmount(BigDecimal prevRefundAmount) {
		this.prevRefundAmount = prevRefundAmount;
	}

	public BigDecimal getCurrRefundAmount() {
		return currRefundAmount;
	}

	public void setCurrRefundAmount(BigDecimal currRefundAmount) {
		this.currRefundAmount = currRefundAmount;
	}

	public long getReceivableRefId() {
		return receivableRefId;
	}

	public void setReceivableRefId(long receivableRefId) {
		this.receivableRefId = receivableRefId;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FeeRefundDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FeeRefundDetail beforeImage) {
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

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public String getTaxComponent() {
		return taxComponent;
	}

	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}

	public Long getTaxHeaderId() {
		return taxHeaderId;
	}

	public void setTaxHeaderId(Long taxHeaderId) {
		this.taxHeaderId = taxHeaderId;
	}

	public TaxHeader getTaxHeader() {
		return taxHeader;
	}

	public void setTaxHeader(TaxHeader taxHeader) {
		this.taxHeader = taxHeader;
	}

	public BigDecimal getAdviseAmount() {
		return adviseAmount;
	}

	public void setAdviseAmount(BigDecimal adviseAmount) {
		this.adviseAmount = adviseAmount;
	}

	public BigDecimal getPrvGST() {
		return prvGST;
	}

	public void setPrvGST(BigDecimal prvGST) {
		this.prvGST = prvGST;
	}

	public ManualAdvise getManualAdvise() {
		return manualAdvise;
	}

	public void setManualAdvise(ManualAdvise manualAdvise) {
		this.manualAdvise = manualAdvise;
	}

	public String getPayableFeeTypeCode() {
		return payableFeeTypeCode;
	}

	public void setPayableFeeTypeCode(String payableFeeTypeCode) {
		this.payableFeeTypeCode = payableFeeTypeCode;
	}

	public String getPayableFeeTypeDesc() {
		return payableFeeTypeDesc;
	}

	public void setPayableFeeTypeDesc(String payableFeeTypeDesc) {
		this.payableFeeTypeDesc = payableFeeTypeDesc;
	}

	public long getPayableRefId() {
		return payableRefId;
	}

	public void setPayableRefId(long payableRefId) {
		this.payableRefId = payableRefId;
	}

}
