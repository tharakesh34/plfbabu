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
 * * FileName : GuarantorDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * * Modified Date
 * : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>GuarantorDetail table</b>.<br>
 * 
 */
public class GuarantorDetail implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private long guarantorId = Long.MIN_VALUE;
	private String finReference;
	private boolean bankCustomer;
	private String guarantorCIF;
	private String guarantorCIFName;
	private String guarantorIDType;
	private String guarantorIDTypeName;
	private String guarantorIDNumber;
	private String name;
	private BigDecimal guranteePercentage;
	private String mobileNo;
	private String emailId;
	private byte[] guarantorProof;
	private String guarantorProofName;
	private String remarks;
	private String primaryExposure;
	private String secondaryExposure;
	private String guarantorExposure;
	private String worstStatus;
	private String status;
	
	private List<FinanceExposure> primaryList = null;
	private List<FinanceExposure> secoundaryList = null;
	private List<FinanceExposure> guarantorList = null;
	private FinanceExposure sumPrimaryDetails = null;
	private FinanceExposure sumSecondaryDetails = null;
	private FinanceExposure sumGurantorDetails = null;
	
	private int version;
	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	@XmlTransient
	private Timestamp lastMntOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private GuarantorDetail befImage;
	@XmlTransient
	private LoginUserDetails userDetails;
	@XmlTransient
	private String recordStatus;
	@XmlTransient
	private String roleCode = "";
	@XmlTransient
	private String nextRoleCode = "";
	@XmlTransient
	private String taskId = "";
	@XmlTransient
	private String nextTaskId = "";
	@XmlTransient
	private String recordType;
	@XmlTransient
	private String userAction = "Save";
	@XmlTransient
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public GuarantorDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("GuarantorDetail");
	}

	public GuarantorDetail(long id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("guarantorCIFName");
		excludeFields.add("guarantorIDTypeName");
		excludeFields.add("primaryExposure");
		excludeFields.add("secondaryExposure");
		excludeFields.add("guarantorExposure");
		excludeFields.add("worstStatus");
		excludeFields.add("status");
		excludeFields.add("primaryList");
		excludeFields.add("secoundaryList");
		excludeFields.add("guarantorList");
		excludeFields.add("sumPrimaryDetails");
		excludeFields.add("sumSecondaryDetails");
		excludeFields.add("sumGurantorDetails");
		excludeFields.add("name");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public long getId() {
		return guarantorId;
	}

	public void setId(long id) {
		this.guarantorId = id;
	}

	public long getGuarantorId() {
		return guarantorId;
	}

	public void setGuarantorId(long guarantorId) {
		this.guarantorId = guarantorId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public boolean isBankCustomer() {
		return bankCustomer;
	}

	public void setBankCustomer(boolean bankCustomer) {
		this.bankCustomer = bankCustomer;
	}

	public String getGuarantorCIF() {
		return guarantorCIF;
	}

	public void setGuarantorCIF(String guarantorCIF) {
		this.guarantorCIF = guarantorCIF;
	}

	public String getGuarantorCIFName() {
		return this.guarantorCIFName;
	}

	public void setGuarantorCIFName(String guarantorCIFName) {
		this.guarantorCIFName = guarantorCIFName;
	}

	public String getGuarantorIDType() {
		return guarantorIDType;
	}

	public void setGuarantorIDType(String guarantorIDType) {
		this.guarantorIDType = guarantorIDType;
	}

	public String getGuarantorIDTypeName() {
		return this.guarantorIDTypeName;
	}

	public void setGuarantorIDTypeName(String guarantorIDTypeName) {
		this.guarantorIDTypeName = guarantorIDTypeName;
	}

	public String getGuarantorIDNumber() {
		return guarantorIDNumber;
	}

	public void setGuarantorIDNumber(String guarantorIDNumber) {
		this.guarantorIDNumber = guarantorIDNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getGuranteePercentage() {
		return guranteePercentage;
	}

	public void setGuranteePercentage(BigDecimal guranteePercentage) {
		this.guranteePercentage = guranteePercentage;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getGuarantorProofName() {
		return guarantorProofName;
	}

	public void setGuarantorProofName(String guarantorProofName) {
		this.guarantorProofName = guarantorProofName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@XmlTransient
	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}

	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}

	@XmlTransient
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMaintainedOn(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			lastMntOn = DateUtility.ConvertFromXMLTime(xmlCalendar);
			lastMaintainedOn = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getLastMaintainedOn() throws DatatypeConfigurationException {

		if (lastMntOn == null) {
			return null;
		}
		return DateUtility.getXMLDate(lastMntOn);
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
	}

	@XmlTransient
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public GuarantorDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(GuarantorDetail beforeImage) {
		this.befImage = beforeImage;
	}

	@XmlTransient
	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	@XmlTransient
	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	@XmlTransient
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	@XmlTransient
	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	@XmlTransient
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@XmlTransient
	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	@XmlTransient
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	@XmlTransient
	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(GuarantorDetail guarantorDetail) {
		return getId() == guarantorDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof GuarantorDetail) {
			GuarantorDetail guarantorDetail = (GuarantorDetail) obj;
			return equals(guarantorDetail);
		}
		return false;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public byte[] getGuarantorProof() {
		return guarantorProof;
	}

	public void setGuarantorProof(byte[] guarantorProof) {
		this.guarantorProof = guarantorProof;
	}

	public String getPrimaryExposure() {
		return primaryExposure;
	}

	public void setPrimaryExposure(String primaryExposure) {
		this.primaryExposure = primaryExposure;
	}

	public String getSecondaryExposure() {
		return secondaryExposure;
	}

	public void setSecondaryExposure(String secondaryExposure) {
		this.secondaryExposure = secondaryExposure;
	}

	public String getGuarantorExposure() {
		return guarantorExposure;
	}

	public void setGuarantorExposure(String guarantorExposure) {
		this.guarantorExposure = guarantorExposure;
	}

	public String getWorstStatus() {
		return worstStatus;
	}

	public void setWorstStatus(String worstStatus) {
		this.worstStatus = worstStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<FinanceExposure> getPrimaryList() {
		return primaryList;
	}

	public void setPrimaryList(List<FinanceExposure> primaryList) {
		this.primaryList = primaryList;
	}

	public List<FinanceExposure> getSecoundaryList() {
		return secoundaryList;
	}

	public void setSecoundaryList(List<FinanceExposure> secoundaryList) {
		this.secoundaryList = secoundaryList;
	}

	public List<FinanceExposure> getGuarantorList() {
		return guarantorList;
	}

	public void setGuarantorList(List<FinanceExposure> guarantorList) {
		this.guarantorList = guarantorList;
	}

	public FinanceExposure getSumPrimaryDetails() {
    	return sumPrimaryDetails;
    }

	public void setSumPrimaryDetails(FinanceExposure sumPrimaryDetails) {
    	this.sumPrimaryDetails = sumPrimaryDetails;
    }

	public FinanceExposure getSumSecondaryDetails() {
    	return sumSecondaryDetails;
    }

	public void setSumSecondaryDetails(FinanceExposure sumSecondaryDetails) {
    	this.sumSecondaryDetails = sumSecondaryDetails;
    }

	public FinanceExposure getSumGurantorDetails() {
    	return sumGurantorDetails;
    }

	public void setSumGurantorDetails(FinanceExposure sumGurantorDetails) {
    	this.sumGurantorDetails = sumGurantorDetails;
    }

	
}
