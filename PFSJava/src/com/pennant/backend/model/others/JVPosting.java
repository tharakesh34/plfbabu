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
 * * FileName : JVPosting.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * * Modified Date :
 * 21-06-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.others;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>JVPosting table</b>.<br>
 * 
 */
public class JVPosting implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private String batch;
	private String filename;
	private long batchReference;
	private String currency;
	private int currencyEditField;
	private String currencyDesc;
	private String exchangeRateType;
	private String RateTypeDescription;
	private BigDecimal totDebitsByBatchCcy;
	private int debitCCyEditField;
	private BigDecimal totCreditsByBatchCcy;
	private int creditCCyEditField;
	private int debitCount;
	private int creditsCount;
	private String batchPurpose;
	private int version;
	private String validationStatus = "";
	private String batchPostingStatus = "";
	private String txnId  = "";
	private Date postingDate;
	private String finType  = "";
	private String branch  = "";
	private String branchDesc  = "";
	private boolean rePostingModule = false;
	private List<JVPostingEntry> JVPostingEntrysList = new ArrayList<JVPostingEntry>();
	private List<JVPostingEntry> postingEntryList = new ArrayList<JVPostingEntry>();
	private List<JVPostingEntry> deletedJVPostingEntryList = new ArrayList<JVPostingEntry>();
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private String ccyNumber;

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
	private JVPosting befImage;
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

	public JVPosting() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("JVPosting");
	}
	
	public JVPosting(String id) {
		
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("debitCCyEditField");
		excludeFields.add("creditCCyEditField");
		excludeFields.add("currencyEditField");
		excludeFields.add("currencyDesc");
		excludeFields.add("branchDesc");
		excludeFields.add("RateTypeDescription");
		excludeFields.add("JVPostingEntrysList");
		excludeFields.add("rePostingModule");	
		excludeFields.add("txnId");
		excludeFields.add("finType");
		excludeFields.add("ccyNumber");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public long getId() {
		return batchReference;
	}

	@Override
	public void setId(long id) {
		this.batchReference = id;
	}

	public long getBatchReference() {
		return batchReference;
	}

	public void setBatchReference(long batchReference) {
		this.batchReference = batchReference;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public int getDebitCount() {
		return debitCount;
	}

	public void setDebitCount(int debitCount) {
		this.debitCount = debitCount;
	}

	public int getCreditsCount() {
		return creditsCount;
	}

	public void setCreditsCount(int creditsCount) {
		this.creditsCount = creditsCount;
	}

	public BigDecimal getTotDebitsByBatchCcy() {
		return totDebitsByBatchCcy;
	}

	public void setTotDebitsByBatchCcy(BigDecimal totDebitsByBatchCcy) {
		this.totDebitsByBatchCcy = totDebitsByBatchCcy;
	}

	public BigDecimal getTotCreditsByBatchCcy() {
		return totCreditsByBatchCcy;
	}

	public void setTotCreditsByBatchCcy(BigDecimal totCreditsByBatchCcy) {
		this.totCreditsByBatchCcy = totCreditsByBatchCcy;
	}

	public String getBatchPurpose() {
		return batchPurpose;
	}

	public void setBatchPurpose(String batchPurpose) {
		this.batchPurpose = batchPurpose;
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
	public JVPosting getBefImage() {
		return this.befImage;
	}

	public void setBefImage(JVPosting beforeImage) {
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
	public boolean equals(JVPosting jVPosting) {
		return getId() == jVPosting.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof JVPosting) {
			JVPosting jVPosting = (JVPosting) obj;
			return equals(jVPosting);
		}
		return false;
	}

	public void setJVPostingEntrysList(List<JVPostingEntry> jVPostingEntrys) {
		JVPostingEntrysList = jVPostingEntrys;
	}

	public List<JVPostingEntry> getJVPostingEntrysList() {
		return JVPostingEntrysList;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public String getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}

	public int getDebitCCyEditField() {
		return debitCCyEditField;
	}

	public void setDebitCCyEditField(int debitCCyEditField) {
		this.debitCCyEditField = debitCCyEditField;
	}

	public int getCreditCCyEditField() {
		return creditCCyEditField;
	}

	public void setCreditCCyEditField(int creditCCyEditField) {
		this.creditCCyEditField = creditCCyEditField;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getCurrencyEditField() {
		return currencyEditField;
	}

	public void setCurrencyEditField(int currencyEditField) {
		this.currencyEditField = currencyEditField;
	}

	public String getCurrencyDesc() {
		return currencyDesc;
	}

	public void setCurrencyDesc(String currencyDesc) {
		this.currencyDesc = currencyDesc;
	}

	public String getExchangeRateType() {
		return exchangeRateType;
	}

	public void setExchangeRateType(String exchangeRateType) {
		this.exchangeRateType = exchangeRateType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getRateTypeDescription() {
		return RateTypeDescription;
	}

	public void setRateTypeDescription(String rateTypeDescription) {
		RateTypeDescription = rateTypeDescription;
	}

	public List<JVPostingEntry> getDeletedJVPostingEntryList() {
		return deletedJVPostingEntryList;
	}

	public void setDeletedJVPostingEntryList(List<JVPostingEntry> deletedJVPostingEntryList) {
		this.deletedJVPostingEntryList = deletedJVPostingEntryList;
	}

	public String getBatchPostingStatus() {
		return batchPostingStatus;
	}

	public void setBatchPostingStatus(String batchPostingStatus) {
		this.batchPostingStatus = batchPostingStatus;
	}

	public boolean isRePostingModule() {
	    return rePostingModule;
    }

	public void setRePostingModule(boolean rePostingModule) {
	    this.rePostingModule = rePostingModule;
    }

	public String getTxnId() {
	    return txnId;
    }

	public void setTxnId(String txnId) {
	    this.txnId = txnId;
    }

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public String getFinType() {
	    return finType;
    }

	public void setFinType(String finType) {
	    this.finType = finType;
    }

	public String getBranch() {
	    return branch;
    }

	public void setBranch(String branch) {
	    this.branch = branch;
    }

	public String getCcyNumber() {
	    return ccyNumber;
    }

	public void setCcyNumber(String ccyNumber) {
	    this.ccyNumber = ccyNumber;
    }

	public List<JVPostingEntry> getPostingEntryList() {
	    return postingEntryList;
    }

	public void setPostingEntryList(List<JVPostingEntry> postingEntryList) {
	    this.postingEntryList = postingEntryList;
    }

	public String getBranchDesc() {
	    return branchDesc;
    }

	public void setBranchDesc(String branchDesc) {
	    this.branchDesc = branchDesc;
    }
}
