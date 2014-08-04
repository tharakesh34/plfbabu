/**
\ * Copyright 2011 - Pennant Technologies
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
 * * FileName : JountAccountDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * * Modified
 * Date : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.finance;

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
 * Model class for the <b>JountAccountDetail table</b>.<br>
 * 
 */
public class JointAccountDetail implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private long jointAccountId = Long.MIN_VALUE;
	private String finReference;
	private String custCIF;
	private String lovDescCIFName;
	private boolean includeRepay;
	private String repayAccountId;
	private String primaryExposure;
	private String secondaryExposure;
	private String guarantorExposure;
	private String worstStatus;
	private String status;
	
	private List<FinanceExposure> primaryList = null;
	private List<FinanceExposure> secoundaryList = null;
	private List<FinanceExposure> guarantorList = null;
	
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
	private JointAccountDetail befImage;
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

	public JointAccountDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("JountAccountDetail");
	}

	public JointAccountDetail(long id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCIFName");
		excludeFields.add("primaryList");
		excludeFields.add("secoundaryList");
		excludeFields.add("guarantorList");
		excludeFields.add("primaryExposure");
		excludeFields.add("secondaryExposure");
		excludeFields.add("guarantorExposure");
		excludeFields.add("worstStatus");
		excludeFields.add("status");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public long getId() {
		return jointAccountId;
	}

	public void setId(long id) {
		this.jointAccountId = id;
	}

	public long getJointAccountId() {
		return jointAccountId;
	}

	public void setJointAccountId(long jointAccountId) {
		this.jointAccountId = jointAccountId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public boolean isIncludeRepay() {
		return includeRepay;
	}

	public void setIncludeRepay(boolean includeRepay) {
		this.includeRepay = includeRepay;
	}

	public String getRepayAccountId() {
		return repayAccountId;
	}

	public void setRepayAccountId(String repayAccountId) {
		this.repayAccountId = repayAccountId;
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
	public JointAccountDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(JointAccountDetail beforeImage) {
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
	public boolean equals(JointAccountDetail jountAccountDetail) {
		return getId() == jountAccountDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof JointAccountDetail) {
			JointAccountDetail jountAccountDetail = (JointAccountDetail) obj;
			return equals(jountAccountDetail);
		}
		return false;
	}

	public String getLovDescCIFName() {
		return lovDescCIFName;
	}

	public void setLovDescCIFName(String lovDescCIFName) {
		this.lovDescCIFName = lovDescCIFName;
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
}
