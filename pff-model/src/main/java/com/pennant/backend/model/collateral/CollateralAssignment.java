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
 * * FileName : CollateralAssignment.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 07-05-2016 * * Modified
 * Date : 07-05-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 07-05-2016 Pennant 0.1 * * 16-05-2018 Srinivasa Varma 0.2 Development Item 82 * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CollateralAssignment table</b>.<br>
 * 
 */
@XmlType(propOrder = { "collateralRef", "assignPerc", "assignmentReference" })
@XmlAccessorType(XmlAccessType.NONE)
public class CollateralAssignment extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String reference;
	private String module;
	@XmlElement
	private String collateralRef;
	private String collateralCcy;
	private BigDecimal bankValuation = BigDecimal.ZERO;
	private BigDecimal collateralValue = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal assignPerc = BigDecimal.ZERO;
	private BigDecimal availableAssignPerc = BigDecimal.ZERO;
	private BigDecimal assignedValue = BigDecimal.ZERO;
	private BigDecimal availableAssignValue = BigDecimal.ZERO;
	private BigDecimal totAssignedPerc = BigDecimal.ZERO;
	private boolean active = true;
	private String lovValue;
	private CollateralAssignment befImage;
	private String hostReference;
	private BigDecimal assignPercent = BigDecimal.ZERO;

	// ### 16-05-2018 Start Development Item 82
	private BigDecimal specialLTV = BigDecimal.ZERO;
	private BigDecimal bankLTV = BigDecimal.ZERO;
	// ### 16-05-2018 End Development Item 82
	@XmlElement
	private String assignmentReference;
	@XmlElement(name = "seq")
	private long assignmentSeq;
	private String depositorCIF;
	private String collateralType;
	private Long assetid = Long.MIN_VALUE;
	private Long siid = Long.MIN_VALUE;
	private List<CostComponentDetail> costComponentDetailList = new ArrayList<>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("collateralCcy");
		excludeFields.add("bankValuation");
		excludeFields.add("collateralValue");
		excludeFields.add("assignedValue");
		excludeFields.add("availableAssignPerc");
		excludeFields.add("availableAssignValue");
		excludeFields.add("totAssignedPerc");
		// ### 16-05-2018 Start Development Item 82
		excludeFields.add("specialLTV");
		excludeFields.add("bankLTV");
		// ### 16-05-2018 End Development Item 82
		excludeFields.add("assignmentReference");
		excludeFields.add("assignmentSeq");
		excludeFields.add("depositorCIF");
		excludeFields.add("collateralType");
		excludeFields.add("assignPercent");
		excludeFields.add("siid");
		excludeFields.add("assetid");
		excludeFields.add("costComponentDetailList");

		return excludeFields;
	}

	@XmlTransient
	private LoggedInUser userDetails;

	public CollateralAssignment() {
		super();
	}

	public CollateralAssignment copyEntity() {
		CollateralAssignment entity = new CollateralAssignment();
		entity.setReference(this.reference);
		entity.setModule(this.module);
		entity.setCollateralRef(this.collateralRef);
		entity.setCollateralCcy(this.collateralCcy);
		entity.setBankValuation(this.bankValuation);
		entity.setCollateralValue(this.collateralValue);
		entity.setAssignPerc(this.assignPerc);
		entity.setAvailableAssignPerc(this.availableAssignPerc);
		entity.setAssignedValue(this.assignedValue);
		entity.setAvailableAssignValue(this.availableAssignValue);
		entity.setTotAssignedPerc(this.totAssignedPerc);
		entity.setActive(this.active);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setHostReference(this.hostReference);
		entity.setAssignPercent(this.assignPercent);
		entity.setSpecialLTV(this.specialLTV);
		entity.setBankLTV(this.bankLTV);
		entity.setAssignmentReference(this.assignmentReference);
		entity.setAssignmentSeq(this.assignmentSeq);
		entity.setDepositorCIF(this.depositorCIF);
		entity.setCollateralType(this.collateralType);
		entity.setUserDetails(this.userDetails);
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

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public String getCollateralCcy() {
		return collateralCcy;
	}

	public void setCollateralCcy(String collateralCcy) {
		this.collateralCcy = collateralCcy;
	}

	public BigDecimal getBankValuation() {
		return bankValuation;
	}

	public void setBankValuation(BigDecimal bankValuation) {
		this.bankValuation = bankValuation;
	}

	public BigDecimal getAssignPerc() {
		return assignPerc;
	}

	public void setAssignPerc(BigDecimal assignPerc) {
		this.assignPerc = assignPerc;
	}

	public BigDecimal getAvailableAssignPerc() {
		return availableAssignPerc;
	}

	public void setAvailableAssignPerc(BigDecimal availableAssignPerc) {
		this.availableAssignPerc = availableAssignPerc;
	}

	public BigDecimal getAssignedValue() {
		return assignedValue;
	}

	public void setAssignedValue(BigDecimal assignedValue) {
		this.assignedValue = assignedValue;
	}

	public BigDecimal getAvailableAssignValue() {
		return availableAssignValue;
	}

	public void setAvailableAssignValue(BigDecimal availableAssignValue) {
		this.availableAssignValue = availableAssignValue;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CollateralAssignment getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CollateralAssignment beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(BigDecimal collateralValue) {
		this.collateralValue = collateralValue;
	}

	public BigDecimal getTotAssignedPerc() {
		return totAssignedPerc;
	}

	public void setTotAssignedPerc(BigDecimal totAssignedPerc) {
		this.totAssignedPerc = totAssignedPerc;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	// ### 16-05-2018 Start Development Item 82
	public BigDecimal getSpecialLTV() {
		return specialLTV;
	}

	public void setSpecialLTV(BigDecimal specialLTV) {
		this.specialLTV = specialLTV;
	}

	public BigDecimal getBankLTV() {
		return bankLTV;
	}

	public void setBankLTV(BigDecimal bankLTV) {
		this.bankLTV = bankLTV;
	}

	public String getHostReference() {
		return hostReference;
	}

	public void setHostReference(String hostReference) {
		this.hostReference = hostReference;
	}

	// ### 16-05-2018 End Development Item 82
	public String getAssignmentReference() {
		return assignmentReference;
	}

	public void setAssignmentReference(String assignmentReference) {
		this.assignmentReference = assignmentReference;
	}

	public long getAssignmentSeq() {
		return assignmentSeq;
	}

	public void setAssignmentSeq(long assignmentSeq) {
		this.assignmentSeq = assignmentSeq;
	}

	public String getDepositorCIF() {
		return depositorCIF;
	}

	public void setDepositorCIF(String depositorCIF) {
		this.depositorCIF = depositorCIF;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public BigDecimal getAssignPercent() {
		return assignPercent;
	}

	public void setAssignPercent(BigDecimal assignPercent) {
		this.assignPercent = assignPercent;
	}

	public Long getAssetid() {
		return assetid;
	}

	public void setAssetid(Long assetid) {
		this.assetid = assetid;
	}

	public Long getSiid() {
		return siid;
	}

	public void setSiid(Long siid) {
		this.siid = siid;
	}

	public List<CostComponentDetail> getCostComponentDetailList() {
		return costComponentDetailList;
	}

	public void setCostComponentDetailList(List<CostComponentDetail> costComponentDetailList) {
		this.costComponentDetailList = costComponentDetailList;
	}

}
