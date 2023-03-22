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
package com.pennant.pff.payment.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PaymentDetail table</b>.<br>
 * 
 */
@XmlType(propOrder = { "paymentDetailId", "paymentId", "amountType", "amount", "referenceId", "finType" })
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long paymentDetailId = Long.MIN_VALUE;
	private long paymentId;
	@XmlElement
	private String amountType;
	private String amountTypeName;
	@XmlElement
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal availableAmount = BigDecimal.ZERO;
	private long referenceId;
	private String referenceIdName;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private PaymentDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String feeTypeCode;
	private String feeTypeDesc;
	private boolean taxApplicable = false;
	private String taxComponent;
	private Long taxHeaderId;
	private TaxHeader taxHeader;
	private boolean apiRequest = false;
	private String finSource;
	private BigDecimal adviseAmount = BigDecimal.ZERO;
	private BigDecimal prvGST = BigDecimal.ZERO;
	private ManualAdvise manualAdvise;
	private Long receiptID;
	private Date valueDate;
	private boolean expand;
	private boolean collapse;
	private Long autoRefundID;
	private String sourceId;
	@XmlElement
	private String paymentType;

	public PaymentDetail() {
		super();
	}

	public PaymentDetail(long id) {

		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("amountTypeName");
		excludeFields.add("referenceIdName");
		excludeFields.add("availableAmount");
		excludeFields.add("reserveAmount");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("feeTypeCode");
		excludeFields.add("apiRequest");
		excludeFields.add("paymentTaxDetail");
		excludeFields.add("taxComponent");
		excludeFields.add("taxApplicable");
		excludeFields.add("finSource");
		excludeFields.add("taxHeader");
		excludeFields.add("adviseAmount");
		excludeFields.add("prvGST");
		excludeFields.add("manualAdvise");
		excludeFields.add("receiptID");
		excludeFields.add("valueDate");
		excludeFields.add("expand");
		excludeFields.add("collapse");
		excludeFields.add("autoRefundID");
		excludeFields.add("sourceId");
		excludeFields.add("paymentType");

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return paymentDetailId;
	}

	public void setId(long id) {
		this.paymentDetailId = id;
	}

	public long getPaymentDetailId() {
		return paymentDetailId;
	}

	public void setPaymentDetailId(long paymentDetailId) {
		this.paymentDetailId = paymentDetailId;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public String getAmountTypeName() {
		return this.amountTypeName;
	}

	public void setAmountTypeName(String amountTypeName) {
		this.amountTypeName = amountTypeName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public String getReferenceIdName() {
		return this.referenceIdName;
	}

	public void setReferenceIdName(String referenceIdName) {
		this.referenceIdName = referenceIdName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public PaymentDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PaymentDetail beforeImage) {
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

	public boolean isApiRequest() {
		return apiRequest;
	}

	public void setApiRequest(boolean apiRequest) {
		this.apiRequest = apiRequest;
	}

	public String getFinSource() {
		return finSource;
	}

	public void setFinSource(String finSource) {
		this.finSource = finSource;
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

	public Long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(Long receiptID) {
		this.receiptID = receiptID;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
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

	public Long getAutoRefundID() {
		return autoRefundID;
	}

	public void setAutoRefundID(Long autoRefundID) {
		this.autoRefundID = autoRefundID;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

}
