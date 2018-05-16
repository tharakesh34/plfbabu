/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CollateralAssignment.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-05-2016    														*
 *                                                                  						*
 * Modified Date    :  07-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-05-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 16-05-2018       Srinivasa Varma          0.2          Development Item 82               * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.util.HashSet;
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
@XmlType(propOrder ={
		"collateralRef", "assignPerc"})
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
	
	private boolean newRecord;
	private String lovValue;
	private CollateralAssignment befImage;

	//### 16-05-2018 Start Development Item 82
	private BigDecimal			specialLTV = BigDecimal.ZERO;
	private BigDecimal			bankLTV = BigDecimal.ZERO;
	//### 16-05-2018 End Development Item 82
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		excludeFields.add("collateralCcy");
		excludeFields.add("bankValuation");
		excludeFields.add("collateralValue");
		excludeFields.add("assignedValue");
		excludeFields.add("availableAssignPerc");
		excludeFields.add("availableAssignValue");
		excludeFields.add("totAssignedPerc");
		//### 16-05-2018 Start Development Item 82
		excludeFields.add("specialLTV");
		excludeFields.add("bankLTV");
		//### 16-05-2018 End Development Item 82
		
		return excludeFields;
	}
	
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CollateralAssignment() {
		super();
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

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
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

	//### 16-05-2018 Start Development Item 82
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
	
	//### 16-05-2018 End Development Item 82

}
