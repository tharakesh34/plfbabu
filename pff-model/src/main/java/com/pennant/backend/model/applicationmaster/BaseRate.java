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
 * * FileName : BaseRate.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified Date :
 * 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BaseRate table</b>.<br>
 * 
 */
public class BaseRate extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -8806339094908245173L;

	private String bRType;
	private String lovDescBRTypeName;
	private String currency;
	private Date bREffDate;
	private Date lastMdfDate;
	private BigDecimal bRRate;
	private boolean delExistingRates;
	private String lovValue;
	private BaseRate befImage;
	private LoggedInUser userDetails;
	private boolean bRTypeIsActive;
	private long createdBy;
	private Timestamp createdOn;
	private Long approvedBy;
	private Timestamp approvedOn;
	private String approvedUser;

	public BaseRate() {
		super();
	}

	public BaseRate copyEntity() {
		BaseRate entity = new BaseRate();
		entity.setBRType(this.bRType);
		entity.setLovDescBRTypeName(this.lovDescBRTypeName);
		entity.setCurrency(this.currency);
		entity.setBREffDate(this.bREffDate);
		entity.setLastMdfDate(this.lastMdfDate);
		entity.setBRRate(this.bRRate);
		entity.setDelExistingRates(this.delExistingRates);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setbRTypeIsActive(this.bRTypeIsActive);
		entity.setCreatedBy(this.createdBy);
		entity.setCreatedOn(this.createdOn);
		entity.setApprovedBy(this.approvedBy);
		entity.setApprovedOn(this.approvedOn);
		entity.setApprovedUser(this.approvedUser);
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

	public BaseRate(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("approvedUser");
		return excludeFields;
	}

	public String getId() {
		return bRType;
	}

	public void setId(String id) {
		this.bRType = id;
	}

	public String getBRType() {
		return bRType;
	}

	public void setBRType(String bRType) {
		this.bRType = bRType;
	}

	public String getLovDescBRTypeName() {
		return this.lovDescBRTypeName;
	}

	public void setLovDescBRTypeName(String lovDescBRTypeName) {
		this.lovDescBRTypeName = lovDescBRTypeName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getBREffDate() {
		return bREffDate;
	}

	public void setBREffDate(Date bREffDate) {
		this.bREffDate = bREffDate;
	}

	public BigDecimal getBRRate() {
		return bRRate;
	}

	public void setBRRate(BigDecimal bRRate) {
		this.bRRate = bRRate;
	}

	public boolean isDelExistingRates() {
		return delExistingRates;
	}

	public void setDelExistingRates(boolean delExistingRates) {
		this.delExistingRates = delExistingRates;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public BaseRate getBefImage() {
		return this.befImage;
	}

	public void setBefImage(BaseRate beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getLastMdfDate() {
		return lastMdfDate;
	}

	public void setLastMdfDate(Date lastMdfDate) {
		this.lastMdfDate = lastMdfDate;
	}

	public boolean isbRTypeIsActive() {
		return bRTypeIsActive;
	}

	public void setbRTypeIsActive(boolean bRTypeIsActive) {
		this.bRTypeIsActive = bRTypeIsActive;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
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

	public String getApprovedUser() {
		return approvedUser;
	}

	public void setApprovedUser(String approvedUser) {
		this.approvedUser = approvedUser;
	}

}
