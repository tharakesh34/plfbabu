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
 * * FileName : PaymentHeader.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified Date :
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennanttech.model.adapter.DateFormatterAdapter;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FeeRefundHeader table</b>.<br>
 * 
 */
public class FeeRefundHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String custCif;
	private String custShrtName;
	private long finID;
	private String paymentType;
	private String finReference;
	private String finType;
	private String branchDesc;
	private BigDecimal paymentAmount = BigDecimal.ZERO;
	private long createdBy;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Timestamp createdOn;
	private Long approvedBy;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Timestamp approvedOn;
	private String status;
	private String paymentInstrType;
	private long linkedTranId;
	private String finCcy;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private FeeRefundHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private int approvalStatus;
	private BigDecimal odAgainstCustomer = BigDecimal.ZERO;
	private BigDecimal odAgainstLoan = BigDecimal.ZERO;
	private long custId = Long.MIN_VALUE;
	private String custCoreBank;

	private List<FeeRefundDetail> feeRefundDetailList;
	private FeeRefundInstruction feeRefundInstruction;
	private Beneficiary defaultBeneficiary;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	public FeeRefundHeader() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("paymentDetail");
		excludeFields.add("paymentInstruction");
		excludeFields.add("paymentInstrType");
		excludeFields.add("defaultBeneficiary");
		excludeFields.add("finSource");
		excludeFields.add("finCcy");
		excludeFields.add("custShrtName");
		excludeFields.add("finReference");
		excludeFields.add("linkedTranId");
		excludeFields.add("feeRefundInstruction");
		excludeFields.add("odAgainstCustomer");
		excludeFields.add("odAgainstLoan");
		excludeFields.add("custId");
		excludeFields.add("custCoreBank");
		excludeFields.add("custCif");
		excludeFields.add("finType");
		excludeFields.add("branchDesc");

		return excludeFields;
	}

	public FeeRefundHeader(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
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

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
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

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentInstrType() {
		return paymentInstrType;
	}

	public void setPaymentInstrType(String paymentInstrType) {
		this.paymentInstrType = paymentInstrType;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FeeRefundHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FeeRefundHeader beforeImage) {
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

	public FeeRefundInstruction getFeeRefundInstruction() {
		return feeRefundInstruction;
	}

	public void setFeeRefundInstruction(FeeRefundInstruction feeRefundInstruction) {
		this.feeRefundInstruction = feeRefundInstruction;
	}

	public List<FeeRefundDetail> getFeeRefundDetailList() {
		return feeRefundDetailList;
	}

	public void setFeeRefundDetailList(List<FeeRefundDetail> feeRefundDetailList) {
		this.feeRefundDetailList = feeRefundDetailList;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public Beneficiary getDefaultBeneficiary() {
		return defaultBeneficiary;
	}

	public void setDefaultBeneficiary(Beneficiary defaultBeneficiary) {
		this.defaultBeneficiary = defaultBeneficiary;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(int approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public BigDecimal getOdAgainstCustomer() {
		return odAgainstCustomer;
	}

	public void setOdAgainstCustomer(BigDecimal odAgainstCustomer) {
		this.odAgainstCustomer = odAgainstCustomer;
	}

	public BigDecimal getOdAgainstLoan() {
		return odAgainstLoan;
	}

	public void setOdAgainstLoan(BigDecimal odAgainstLoan) {
		this.odAgainstLoan = odAgainstLoan;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

}
