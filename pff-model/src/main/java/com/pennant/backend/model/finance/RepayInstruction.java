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
 * * FileName : RepayInstruction.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2011 * * Modified
 * Date : 02-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>RepayInstruction table</b>.<br>
 * 
 */
@XmlType(propOrder = { "repayDate", "repayAmount", "repaySchdMethod" })
@XmlAccessorType(XmlAccessType.NONE)
public class RepayInstruction extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long finID;
	private String finReference = null;
	@XmlElement
	private Date repayDate;
	@XmlElement
	private BigDecimal repayAmount = BigDecimal.ZERO;
	@XmlElement
	private String repaySchdMethod;

	private long logKey;
	private String lovValue;
	private RepayInstruction befImage;

	@XmlTransient
	private LoggedInUser userDetails;

	public RepayInstruction() {
		super();
	}

	public RepayInstruction copyEntity() {
		RepayInstruction entity = new RepayInstruction();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setRepayDate(this.repayDate);
		entity.setRepayAmount(this.repayAmount);
		entity.setRepaySchdMethod(this.repaySchdMethod);
		entity.setLogKey(this.logKey);
		entity.setNewRecord(super.isNewRecord());
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
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

	public RepayInstruction(String id) {
		super();
		this.setId(id);
	}

	// Getter and Setter methods

	public String getId() {
		return finReference;
	}

	public void setId(String id) {
		this.finReference = id;
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

	public Date getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(Date repayDate) {
		this.repayDate = repayDate;
	}

	public BigDecimal getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	public String getRepaySchdMethod() {
		return repaySchdMethod;
	}

	public void setRepaySchdMethod(String repaySchdMethod) {
		this.repaySchdMethod = repaySchdMethod;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public RepayInstruction getBefImage() {
		return this.befImage;
	}

	public void setBefImage(RepayInstruction beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLogKey(long logKey) {
		this.logKey = logKey;
	}

	public long getLogKey() {
		return logKey;
	}
}
