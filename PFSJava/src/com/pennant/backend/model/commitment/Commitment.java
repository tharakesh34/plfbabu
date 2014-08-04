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
 * * FileName : Commitment.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 * * Modified Date :
 * 25-03-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.commitment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Commitment table</b>.<br>
 * 
 */
public class Commitment implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String cmtReference;
	private long custID;
	private String cmtBranch;
	private boolean openAccount;
	private String cmtAccount;
	private String cmtCcy;
	private BigDecimal cmtPftRateMin;
	private BigDecimal cmtPftRateMax;
	private BigDecimal cmtAmount;
	private BigDecimal cmtUtilizedAmount;
	private BigDecimal cmtAvailable;
	private Date cmtPromisedDate;
	private Date cmtStartDate;
	private Date cmtExpDate;
	private boolean activeStatus;
	private boolean nonperformingStatus;
	private String cmtTitle;
	private String cmtNotes;
	private boolean revolving;
	private boolean sharedCmt;
	private boolean multiBranch;
	private int version;

	private BigDecimal cmtCharges;
	private String chargesAccount;
	private boolean cmtActive;
	private boolean cmtStopRateRange;
	private boolean nonPerforming;
	
	private int ccyEditField;
	private String custShrtName;
	private String custCIF;
	private String branchDesc;
	private String ccyDesc;
	private String chargesAccountName;
	private String cmtAccountName;
	private String custCtgCode;
	private Date custDOB;
	private String facilityRef;
	private String facilityRefDesc;

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
	private Commitment befImage;
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
	public CommitmentMovement commitmentMovement;

	public boolean isNew() {
		return isNewRecord();
	}

	public Commitment() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Commitment");
		this.commitmentMovement = new CommitmentMovement();
	}

	public Commitment(String id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("commitmentMovement");
		excludeFields.add("custShrtName");
		excludeFields.add("branchDesc");
		excludeFields.add("ccyDesc");
		excludeFields.add("chargesAccountName");
		excludeFields.add("cmtAccountName");
		excludeFields.add("activeStatus");
		excludeFields.add("ccyEditField");
		excludeFields.add("nonperformingStatus");
		excludeFields.add("custCtgCode");
		excludeFields.add("custDOB");
		excludeFields.add("custCIF");
		excludeFields.add("facilityRefDesc");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return cmtReference;
	}

	public CommitmentMovement getCommitmentMovement() {
		return commitmentMovement;
	}

	public void setCommitmentMovement(CommitmentMovement commitmentMovement) {
		this.commitmentMovement = commitmentMovement;
	}

	public void setId(String id) {
		this.cmtReference = id;
	}

	public String getCmtReference() {
		return cmtReference;
	}

	public void setCmtReference(String cmtReference) {
		this.cmtReference = cmtReference;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCmtBranch() {
		return cmtBranch;
	}

	public void setCmtBranch(String cmtBranch) {
		this.cmtBranch = cmtBranch;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getCcyDesc() {
		return ccyDesc;
	}

	public void setCcyDesc(String ccyDesc) {
		this.ccyDesc = ccyDesc;
	}

	public boolean isOpenAccount() {
		return openAccount;
	}

	public void setOpenAccount(boolean openAccount) {
		this.openAccount = openAccount;
	}

	public String getCmtAccount() {
		return cmtAccount;
	}

	public void setCmtAccount(String cmtAccount) {
		this.cmtAccount = cmtAccount;
	}

	public String getCmtAccountName() {
		return cmtAccountName;
	}

	public void setCmtAccountName(String cmtAccountName) {
		this.cmtAccountName = cmtAccountName;
	}

	public String getCmtCcy() {
		return cmtCcy;
	}

	public void setCmtCcy(String cmtCcy) {
		this.cmtCcy = cmtCcy;
	}

	public BigDecimal getCmtPftRateMin() {
		return cmtPftRateMin;
	}

	public void setCmtPftRateMin(BigDecimal cmtPftRateMin) {
		this.cmtPftRateMin = cmtPftRateMin;
	}

	public BigDecimal getCmtPftRateMax() {
		return cmtPftRateMax;
	}

	public void setCmtPftRateMax(BigDecimal cmtPftRateMax) {
		this.cmtPftRateMax = cmtPftRateMax;
	}

	public BigDecimal getCmtAmount() {
		return cmtAmount;
	}

	public void setCmtAmount(BigDecimal cmtAmount) {
		this.cmtAmount = cmtAmount;
	}

	public BigDecimal getCmtUtilizedAmount() {
		return cmtUtilizedAmount;
	}

	public void setCmtUtilizedAmount(BigDecimal cmtUtilizedAmount) {
		this.cmtUtilizedAmount = cmtUtilizedAmount;
	}

	public BigDecimal getCmtAvailable() {
		return cmtAvailable;
	}

	public void setCmtAvailable(BigDecimal cmtAvailable) {
		this.cmtAvailable = cmtAvailable;
	}

	public Date getCmtPromisedDate() {
		return cmtPromisedDate;
	}

	public void setCmtPromisedDate(Date cmtPromisedDate) {
		this.cmtPromisedDate = cmtPromisedDate;
	}

	public Date getCmtStartDate() {
		return cmtStartDate;
	}

	public void setCmtStartDate(Date cmtStartDate) {
		this.cmtStartDate = cmtStartDate;
	}

	public Date getCmtExpDate() {
		return cmtExpDate;
	}

	public void setCmtExpDate(Date cmtExpDate) {
		this.cmtExpDate = cmtExpDate;
	}

	public String getCmtTitle() {
		return cmtTitle;
	}

	public BigDecimal getCmtCharges() {
		return cmtCharges;
	}

	public void setCmtCharges(BigDecimal cmtCharges) {
		this.cmtCharges = cmtCharges;
	}

	public String getChargesAccount() {
		return chargesAccount;
	}

	public void setChargesAccount(String chargesAccount) {
		this.chargesAccount = chargesAccount;
	}

	public String getChargesAccountName() {
		return chargesAccountName;
	}

	public void setChargesAccountName(String chargesAccountName) {
		this.chargesAccountName = chargesAccountName;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public boolean isNonperformingStatus() {
		return nonperformingStatus;
	}

	public void setNonperformingStatus(boolean nonperformingStatus) {
		this.nonperformingStatus = nonperformingStatus;
	}

	public void setCmtTitle(String cmtTitle) {
		this.cmtTitle = cmtTitle;
	}

	public String getCmtNotes() {
		return cmtNotes;
	}

	public void setCmtNotes(String cmtNotes) {
		this.cmtNotes = cmtNotes;
	}

	public boolean isRevolving() {
		return revolving;
	}

	public void setRevolving(boolean revolving) {
		this.revolving = revolving;
	}

	public boolean isSharedCmt() {
		return sharedCmt;
	}

	public void setSharedCmt(boolean sharedCmt) {
		this.sharedCmt = sharedCmt;
	}

	public boolean isMultiBranch() {
		return multiBranch;
	}

	public void setMultiBranch(boolean multiBranch) {
		this.multiBranch = multiBranch;
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
	public Commitment getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Commitment beforeImage) {
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
	public boolean equals(Commitment commitment) {
		return getId() == commitment.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Commitment) {
			Commitment commitment = (Commitment) obj;
			return equals(commitment);
		}
		return false;
	}

	public void setCmtActive(boolean cmtActive) {
		this.cmtActive = cmtActive;
	}

	public boolean isCmtActive() {
		return cmtActive;
	}

	public void setCmtStopRateRange(boolean cmtStopRateRange) {
		this.cmtStopRateRange = cmtStopRateRange;
	}

	public boolean isCmtStopRateRange() {
		return cmtStopRateRange;
	}

	public void setNonPerforming(boolean nonPerforming) {
		this.nonPerforming = nonPerforming;
	}

	public boolean isNonPerforming() {
		return nonPerforming;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCustCtgCode(String custCtgCode) {
	    this.custCtgCode = custCtgCode;
    }

	public String getCustCtgCode() {
	    return custCtgCode;
    }

	public void setCustDOB(Date custDOB) {
	    this.custDOB = custDOB;
    }

	public Date getCustDOB() {
	    return custDOB;
    }

	public void setCustCIF(String custCIF) {
	    this.custCIF = custCIF;
    }

	public String getCustCIF() {
	    return custCIF;
    }

	public void setFacilityRef(String facilityRef) {
	    this.facilityRef = facilityRef;
    }

	public String getFacilityRef() {
	    return facilityRef;
    }

	public void setFacilityRefDesc(String facilityRefDesc) {
	    this.facilityRefDesc = facilityRefDesc;
    }

	public String getFacilityRefDesc() {
	    return facilityRefDesc;
    }
}
