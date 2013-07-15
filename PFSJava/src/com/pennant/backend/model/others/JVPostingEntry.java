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
 * * FileName : JVPostingEntry.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * * Modified Date
 * : 21-06-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.others;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>JVPostingEntry table</b>.<br>
 * 
 */
public class JVPostingEntry implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String batchReference;
	private String account;
	private String accountName;
	private String txnCCy;
	private String txnCCyName;
	private int txnCCyEditField;
	private String accCCy;
	private String accCCyName;
	private int accCCyEditField;
	private String txnCode;
	private Timestamp postingDate;
	private Timestamp valueDate;
	private BigDecimal txnAmount;
	private String txnReference;
	private String narrLine1;
	private String narrLine2;
	private String narrLine3;
	private String narrLine4;
	private BigDecimal exchRate_Batch;//TODO  Rename To Base
	private BigDecimal exchRate_Ac;
	private BigDecimal txnAmount_Batch;//TODO Rename To base
	private BigDecimal txnAmount_Ac;
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
	private JVPostingEntry befImage;
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

	public JVPostingEntry() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("JVPostingEntry");
	}

	public JVPostingEntry(String id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("txnCCyName");
		excludeFields.add("txnCCyEditField");
		excludeFields.add("accCCyName");
		excludeFields.add("accCCyEditField");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return batchReference;
	}

	public void setId(String id) {
		this.batchReference = id;
	}

	public String getBatchReference() {
		return batchReference;
	}

	public void setBatchReference(String batchReference) {
		this.batchReference = batchReference;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getTxnCCy() {
		return txnCCy;
	}

	public void setTxnCCy(String txnCCy) {
		this.txnCCy = txnCCy;
	}

	public String getTxnCCyName() {
		return this.txnCCyName;
	}

	public void setTxnCCyName(String txnCCyName) {
		this.txnCCyName = txnCCyName;
	}

	public String getTxnCode() {
		return txnCode;
	}

	public void setTxnCode(String txnCode) {
		this.txnCode = txnCode;
	}

	public Timestamp getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Timestamp postingDate) {
		this.postingDate = postingDate;
	}

	public Timestamp getValueDate() {
		return valueDate;
	}

	public void setValueDate(Timestamp valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(BigDecimal txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getTxnReference() {
		return txnReference;
	}

	public void setTxnReference(String txnReference) {
		this.txnReference = txnReference;
	}

	public String getNarrLine1() {
		return narrLine1;
	}

	public void setNarrLine1(String narrLine1) {
		this.narrLine1 = narrLine1;
	}

	public String getNarrLine2() {
		return narrLine2;
	}

	public void setNarrLine2(String narrLine2) {
		this.narrLine2 = narrLine2;
	}

	public String getNarrLine3() {
		return narrLine3;
	}

	public void setNarrLine3(String narrLine3) {
		this.narrLine3 = narrLine3;
	}

	public String getNarrLine4() {
		return narrLine4;
	}

	public void setNarrLine4(String narrLine4) {
		this.narrLine4 = narrLine4;
	}

	public BigDecimal getExchRate_Batch() {
		return exchRate_Batch;
	}

	public void setExchRate_Batch(BigDecimal exchRate_Batch) {
		this.exchRate_Batch = exchRate_Batch;
	}

	public BigDecimal getExchRate_Ac() {
		return exchRate_Ac;
	}

	public void setExchRate_Ac(BigDecimal exchRate_Ac) {
		this.exchRate_Ac = exchRate_Ac;
	}

	public BigDecimal getTxnAmount_Batch() {
		return txnAmount_Batch;
	}

	public void setTxnAmount_Batch(BigDecimal txnAmount_Batch) {
		this.txnAmount_Batch = txnAmount_Batch;
	}

	public BigDecimal getTxnAmount_Ac() {
		return txnAmount_Ac;
	}

	public void setTxnAmount_Ac(BigDecimal txnAmount_Ac) {
		this.txnAmount_Ac = txnAmount_Ac;
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
	public JVPostingEntry getBefImage() {
		return this.befImage;
	}

	public void setBefImage(JVPostingEntry beforeImage) {
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
	public boolean equals(JVPostingEntry jVPostingEntry) {
		return getId() == jVPostingEntry.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof JVPostingEntry) {
			JVPostingEntry jVPostingEntry = (JVPostingEntry) obj;
			return equals(jVPostingEntry);
		}
		return false;
	}

	public void setTxnCCyEditField(int txnCCyEditField) {
		this.txnCCyEditField = txnCCyEditField;
	}

	public int getTxnCCyEditField() {
		return txnCCyEditField;
	}

	public void setAccCCy(String accCCy) {
	    this.accCCy = accCCy;
    }

	public String getAccCCy() {
	    return accCCy;
    }

	public void setAccCCyName(String accCCyName) {
	    this.accCCyName = accCCyName;
    }

	public String getAccCCyName() {
	    return accCCyName;
    }

	public void setAccCCyEditField(int accCCyEditField) {
	    this.accCCyEditField = accCCyEditField;
    }

	public int getAccCCyEditField() {
	    return accCCyEditField;
    }
}
