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
	private boolean taxApplicable;
	private String taxComponent;
	private boolean amortzReq;
	private boolean refundable;
	private boolean dueAccReq;
	private Long dueAccSet;
	private String dueAcctSetCode;
	private String dueAcctSetCodeName;
	private boolean tdsReq;
	private String acType;
	private String acTypeDesc;
	private String incomeOrExpenseAcType;
	private String incomeOrExpenseAcTypeDesc;
	private String payableLinkTo;
	private Long recvFeeTypeId;
	private String recvFeeTypeCode;
	private String recvFeeTypeDesc;
	private String allocationtype;
	private String waiverOrRefundAcType;
	private String waiverOrRefundAcTypeDesc;
	private boolean allowAutoRefund;

	private String cgstAcType;
	private String cgstAcTypeDesc;
	private String sgstAcType;
	private String sgstAcTypeDesc;
	private String igstAcType;
	private String igstAcTypeDesc;
	private String ugstAcType;
	private String ugstAcTypeDesc;
	private String cessAcType;
	private String cessAcTypeDesc;
	private String tdsAcType;
	private String tdsAcTypeDesc;

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
		entity.setRefundable(this.refundable);
		entity.setDueAccReq(this.dueAccReq);
		entity.setDueAccSet(this.dueAccSet);
		entity.setDueAcctSetCode(this.dueAcctSetCode);
		entity.setDueAcctSetCodeName(this.dueAcctSetCodeName);
		entity.setTdsReq(this.tdsReq);
		entity.setAcType(this.acType);
		entity.setAcTypeDesc(this.acTypeDesc);
		entity.setIncomeOrExpenseAcType(this.incomeOrExpenseAcType);
		entity.setIncomeOrExpenseAcTypeDesc(this.incomeOrExpenseAcTypeDesc);
		entity.setPayableLinkTo(this.payableLinkTo);
		entity.setRecvFeeTypeId(this.recvFeeTypeId);
		entity.setRecvFeeTypeCode(this.recvFeeTypeCode);
		entity.setRecvFeeTypeDesc(this.recvFeeTypeDesc);
		entity.setAllocationtype(this.allocationtype);
		entity.setWaiverOrRefundAcType(this.waiverOrRefundAcType);
		entity.setWaiverOrRefundAcTypeDesc(this.waiverOrRefundAcTypeDesc);
		entity.setAllowAutoRefund(this.allowAutoRefund);
		entity.setCgstAcType(this.cgstAcType);
		entity.setCgstAcTypeDesc(this.cgstAcTypeDesc);
		entity.setSgstAcType(this.sgstAcType);
		entity.setSgstAcTypeDesc(this.sgstAcTypeDesc);
		entity.setIgstAcType(this.igstAcType);
		entity.setIgstAcTypeDesc(this.igstAcTypeDesc);
		entity.setUgstAcType(this.ugstAcType);
		entity.setUgstAcTypeDesc(this.ugstAcTypeDesc);
		entity.setCessAcType(this.cessAcType);
		entity.setCessAcTypeDesc(this.cessAcTypeDesc);
		entity.setTdsAcType(this.tdsAcType);
		entity.setTdsAcTypeDesc(this.tdsAcTypeDesc);

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
		excludeFields.add("recvFeeTypeCode");
		excludeFields.add("recvFeeTypeDesc");
		excludeFields.add("allocationtype");
		excludeFields.add("incomeOrExpenseAcTypeDesc");
		excludeFields.add("waiverOrRefundAcTypeDesc");
		excludeFields.add("cgstAcType");
		excludeFields.add("cgstAcTypeDesc");
		excludeFields.add("sgstAcType");
		excludeFields.add("sgstAcTypeDesc");
		excludeFields.add("igstAcType");
		excludeFields.add("igstAcTypeDesc");
		excludeFields.add("ugstAcType");
		excludeFields.add("ugstAcTypeDesc");
		excludeFields.add("cessAcType");
		excludeFields.add("cessAcTypeDesc");
		excludeFields.add("tdsAcType");
		excludeFields.add("tdsAcTypeDesc");

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

	public boolean isRefundable() {
		return refundable;
	}

	public void setRefundable(boolean refundable) {
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

	public String getIncomeOrExpenseAcType() {
		return incomeOrExpenseAcType;
	}

	public void setIncomeOrExpenseAcType(String incomeOrExpenseAcType) {
		this.incomeOrExpenseAcType = incomeOrExpenseAcType;
	}

	public String getIncomeOrExpenseAcTypeDesc() {
		return incomeOrExpenseAcTypeDesc;
	}

	public void setIncomeOrExpenseAcTypeDesc(String incomeOrExpenseAcTypeDesc) {
		this.incomeOrExpenseAcTypeDesc = incomeOrExpenseAcTypeDesc;
	}

	public String getPayableLinkTo() {
		return payableLinkTo;
	}

	public void setPayableLinkTo(String payableLinkTo) {
		this.payableLinkTo = payableLinkTo;
	}

	public Long getRecvFeeTypeId() {
		return recvFeeTypeId;
	}

	public void setRecvFeeTypeId(Long recvFeeTypeId) {
		this.recvFeeTypeId = recvFeeTypeId;
	}

	public String getRecvFeeTypeCode() {
		return recvFeeTypeCode;
	}

	public void setRecvFeeTypeCode(String recvFeeTypeCode) {
		this.recvFeeTypeCode = recvFeeTypeCode;
	}

	public String getRecvFeeTypeDesc() {
		return recvFeeTypeDesc;
	}

	public void setRecvFeeTypeDesc(String recvFeeTypeDesc) {
		this.recvFeeTypeDesc = recvFeeTypeDesc;
	}

	public String getAllocationtype() {
		return allocationtype;
	}

	public void setAllocationtype(String allocationtype) {
		this.allocationtype = allocationtype;
	}

	public String getWaiverOrRefundAcType() {
		return waiverOrRefundAcType;
	}

	public void setWaiverOrRefundAcType(String waiverOrRefundAcType) {
		this.waiverOrRefundAcType = waiverOrRefundAcType;
	}

	public String getWaiverOrRefundAcTypeDesc() {
		return waiverOrRefundAcTypeDesc;
	}

	public void setWaiverOrRefundAcTypeDesc(String waiverOrRefundAcTypeDesc) {
		this.waiverOrRefundAcTypeDesc = waiverOrRefundAcTypeDesc;
	}

	public boolean isAllowAutoRefund() {
		return allowAutoRefund;
	}

	public void setAllowAutoRefund(boolean allowAutoRefund) {
		this.allowAutoRefund = allowAutoRefund;
	}

	public String getCgstAcType() {
		return cgstAcType;
	}

	public void setCgstAcType(String cgstAcType) {
		this.cgstAcType = cgstAcType;
	}

	public String getCgstAcTypeDesc() {
		return cgstAcTypeDesc;
	}

	public void setCgstAcTypeDesc(String cgstAcTypeDesc) {
		this.cgstAcTypeDesc = cgstAcTypeDesc;
	}

	public String getSgstAcType() {
		return sgstAcType;
	}

	public void setSgstAcType(String sgstAcType) {
		this.sgstAcType = sgstAcType;
	}

	public String getSgstAcTypeDesc() {
		return sgstAcTypeDesc;
	}

	public void setSgstAcTypeDesc(String sgstAcTypeDesc) {
		this.sgstAcTypeDesc = sgstAcTypeDesc;
	}

	public String getIgstAcType() {
		return igstAcType;
	}

	public void setIgstAcType(String igstAcType) {
		this.igstAcType = igstAcType;
	}

	public String getIgstAcTypeDesc() {
		return igstAcTypeDesc;
	}

	public void setIgstAcTypeDesc(String igstAcTypeDesc) {
		this.igstAcTypeDesc = igstAcTypeDesc;
	}

	public String getUgstAcType() {
		return ugstAcType;
	}

	public void setUgstAcType(String ugstAcType) {
		this.ugstAcType = ugstAcType;
	}

	public String getUgstAcTypeDesc() {
		return ugstAcTypeDesc;
	}

	public void setUgstAcTypeDesc(String ugstAcTypeDesc) {
		this.ugstAcTypeDesc = ugstAcTypeDesc;
	}

	public String getCessAcType() {
		return cessAcType;
	}

	public void setCessAcType(String cessAcType) {
		this.cessAcType = cessAcType;
	}

	public String getCessAcTypeDesc() {
		return cessAcTypeDesc;
	}

	public void setCessAcTypeDesc(String cessAcTypeDesc) {
		this.cessAcTypeDesc = cessAcTypeDesc;
	}

	public String getTdsAcType() {
		return tdsAcType;
	}

	public void setTdsAcType(String tdsAcType) {
		this.tdsAcType = tdsAcType;
	}

	public String getTdsAcTypeDesc() {
		return tdsAcTypeDesc;
	}

	public void setTdsAcTypeDesc(String tdsAcTypeDesc) {
		this.tdsAcTypeDesc = tdsAcTypeDesc;
	}

}
