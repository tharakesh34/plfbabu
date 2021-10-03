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
 * * FileName : FeeType.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-01-2017 * * Modified Date :
 * 03-01-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-01-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FeeType table</b>.<br>
 *
 */
public class FeeType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	// FIXME GDP: Copied from java. We need to remove this bean from java.

	private long feeTypeID = Long.MIN_VALUE;
	private String feeTypeCode;
	private String feeTypeDesc;
	private boolean manualAdvice;
	private int adviseType;
	private Long accountSetId;
	private String accountSetCode;
	private String accountSetCodeName;
	private boolean active;
	private String lovValue;
	private FeeType befImage;
	private LoggedInUser userDetails;
	private String hostFeeTypeCode;
	// GST fields
	private boolean taxApplicable;
	private String taxComponent;

	private boolean amortzReq;
	private boolean refundable;

	private boolean dueAccReq;
	private Long dueAccSet;
	private String dueAcctSetCode;
	private String dueAcctSetCodeName;
	private boolean tdsReq;

	// ### START SFA_20210405 -->
	private String acType;
	private String acTypeDesc;
	private String feeIncomeOrExpense;
	// ### END SFA_20210405 <--

	public FeeType() {
		super();
	}

	public FeeType(long id) {
		super();
		this.setId(id);
	}

	public FeeType copyEntity() {
		FeeType entity = new FeeType();
		entity.setFeeTypeID(this.feeTypeID);
		entity.setFeeTypeCode(this.feeTypeCode);
		entity.setFeeTypeDesc(this.feeTypeDesc);
		entity.setManualAdvice(this.manualAdvice);
		entity.setAdviseType(this.adviseType);
		entity.setAccountSetId(this.accountSetId);
		entity.setAccountSetCode(this.accountSetCode);
		entity.setAccountSetCodeName(this.accountSetCodeName);
		entity.setActive(this.active);
		entity.setNewRecord(super.isNewRecord());
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setHostFeeTypeCode(this.hostFeeTypeCode);
		entity.setTaxApplicable(this.taxApplicable);
		entity.setTaxComponent(this.taxComponent);
		entity.setAmortzReq(this.amortzReq);
		entity.setrefundable(this.refundable);
		entity.setDueAccReq(this.dueAccReq);
		entity.setDueAccSet(this.dueAccSet);
		entity.setDueAcctSetCode(this.dueAcctSetCode);
		entity.setDueAcctSetCodeName(this.dueAcctSetCodeName);
		entity.setTdsReq(this.tdsReq);
		entity.setAcType(this.acType);
		entity.setAcTypeDesc(this.acTypeDesc);
		entity.setFeeIncomeOrExpense(this.feeIncomeOrExpense);
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

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("accountSetCode");
		excludeFields.add("accountSetCodeName");
		excludeFields.add("dueAcctSetCode");
		excludeFields.add("dueAcctSetCodeName");
		excludeFields.add("acType");
		excludeFields.add("acTypeDesc");
		return excludeFields;
	}

	public long getId() {
		return feeTypeID;
	}

	public void setId(long id) {
		this.feeTypeID = id;
	}

	public long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(long feeTypeID) {
		this.feeTypeID = feeTypeID;
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

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FeeType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FeeType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public Long getAccountSetId() {
		return accountSetId;
	}

	public void setAccountSetId(Long accountSetId) {
		this.accountSetId = accountSetId;
	}

	public String getAccountSetCode() {
		return accountSetCode;
	}

	public void setAccountSetCode(String accountSetCode) {
		this.accountSetCode = accountSetCode;
	}

	public String getAccountSetCodeName() {
		return accountSetCodeName;
	}

	public void setAccountSetCodeName(String accountSetCodeName) {
		this.accountSetCodeName = accountSetCodeName;
	}

	public boolean isManualAdvice() {
		return manualAdvice;
	}

	public void setManualAdvice(boolean manualAdvice) {
		this.manualAdvice = manualAdvice;
	}

	public int getAdviseType() {
		return adviseType;
	}

	public void setAdviseType(int adviseType) {
		this.adviseType = adviseType;
	}

	public String getHostFeeTypeCode() {
		return hostFeeTypeCode;
	}

	public void setHostFeeTypeCode(String hostFeeTypeCode) {
		this.hostFeeTypeCode = hostFeeTypeCode;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public boolean isAmortzReq() {
		return amortzReq;
	}

	public void setAmortzReq(boolean amortzReq) {
		this.amortzReq = amortzReq;
	}

	public String getTaxComponent() {
		return taxComponent;
	}

	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}

	public boolean isrefundable() {
		return refundable;
	}

	public void setrefundable(boolean refundable) {
		this.refundable = refundable;
	}

	public boolean isDueAccReq() {
		return dueAccReq;
	}

	public void setDueAccReq(boolean dueAccReq) {
		this.dueAccReq = dueAccReq;
	}

	public Long getDueAccSet() {
		return dueAccSet;
	}

	public void setDueAccSet(Long dueAccSet) {
		this.dueAccSet = dueAccSet;
	}

	public String getDueAcctSetCode() {
		return dueAcctSetCode;
	}

	public void setDueAcctSetCode(String dueAcctSetCode) {
		this.dueAcctSetCode = dueAcctSetCode;
	}

	public String getDueAcctSetCodeName() {
		return dueAcctSetCodeName;
	}

	public void setDueAcctSetCodeName(String dueAcctSetCodeName) {
		this.dueAcctSetCodeName = dueAcctSetCodeName;
	}

	public boolean isTdsReq() {
		return tdsReq;
	}

	public void setTdsReq(boolean tdsReq) {
		this.tdsReq = tdsReq;
	}
	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getAcTypeDesc() {
		return acTypeDesc;
	}

	public void setAcTypeDesc(String acTypeDesc) {
		this.acTypeDesc = acTypeDesc;
	}

	public String getFeeIncomeOrExpense() {
		return feeIncomeOrExpense;
	}

	public void setFeeIncomeOrExpense(String feeIncomeOrExpense) {
		this.feeIncomeOrExpense = feeIncomeOrExpense;
	}

}
