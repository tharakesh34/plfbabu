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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PaymentDetail table</b>.<br>
 * 
 */
public class FeeRefundDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long headerID;
	private String receivableType;
	private Long receivableFeeTypeID;
	private Long payableFeeTypeID;
	private Long receivableID;
	private Long payableID;
	private BigDecimal refundAmount = BigDecimal.ZERO;
	private Long taxHeaderID;
	private long createdBy;
	private Timestamp createdOn;
	private Timestamp approvedOn;
	private Long approvedBy;

	private FeeRefundDetail befImage;
	private LoggedInUser userDetails;

	private BigDecimal availableAmount = BigDecimal.ZERO;
	private BigDecimal adviseAmount = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal prevRefundAmount = BigDecimal.ZERO;

	private String receivableFeeTypeCode;
	private String receivableFeeTypeDesc;
	private String payableFeeTypeCode;
	private String payableFeeTypeDesc;

	private boolean expand;
	private boolean collapse;
	private Long finID;

	public FeeRefundDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("availableAmount");
		excludeFields.add("adviseAmount");
		excludeFields.add("paidAmount");
		excludeFields.add("prevRefundAmount");
		excludeFields.add("receivableFeeTypeCode");
		excludeFields.add("receivableFeeTypeDesc");
		excludeFields.add("payableFeeTypeCode");
		excludeFields.add("payableFeeTypeDesc");
		excludeFields.add("expand");
		excludeFields.add("collapse");
		excludeFields.add("finID");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

	public String getReceivableType() {
		return receivableType;
	}

	public void setReceivableType(String receivableType) {
		this.receivableType = receivableType;
	}

	public Long getReceivableFeeTypeID() {
		return receivableFeeTypeID;
	}

	public void setReceivableFeeTypeID(Long receivableFeeTypeID) {
		this.receivableFeeTypeID = receivableFeeTypeID;
	}

	public Long getPayableFeeTypeID() {
		return payableFeeTypeID;
	}

	public void setPayableFeeTypeID(Long payableFeeTypeID) {
		this.payableFeeTypeID = payableFeeTypeID;
	}

	public Long getReceivableID() {
		return receivableID;
	}

	public void setReceivableID(Long receivableID) {
		this.receivableID = receivableID;
	}

	public Long getPayableID() {
		return payableID;
	}

	public void setPayableID(Long payableID) {
		this.payableID = payableID;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public Long getTaxHeaderID() {
		return taxHeaderID;
	}

	public void setTaxHeaderID(Long taxHeaderID) {
		this.taxHeaderID = taxHeaderID;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public FeeRefundDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(FeeRefundDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public BigDecimal getAdviseAmount() {
		return adviseAmount;
	}

	public void setAdviseAmount(BigDecimal adviseAmount) {
		this.adviseAmount = adviseAmount;
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

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public boolean isCollapse() {
		return collapse;
	}

	public void setCollapse(boolean collapse) {
		this.collapse = collapse;
	}

	public BigDecimal getTotalAmount() {
		return null;
	}

	public String getReceivableFeeTypeCode() {
		return receivableFeeTypeCode;
	}

	public void setReceivableFeeTypeCode(String receivableFeeTypeCode) {
		this.receivableFeeTypeCode = receivableFeeTypeCode;
	}

	public String getReceivableFeeTypeDesc() {
		return receivableFeeTypeDesc;
	}

	public void setReceivableFeeTypeDesc(String receivableFeeTypeDesc) {
		this.receivableFeeTypeDesc = receivableFeeTypeDesc;
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

	public Long getFinID() {
		return finID;
	}

	public void setFinID(Long finID) {
		this.finID = finID;
	}

}
