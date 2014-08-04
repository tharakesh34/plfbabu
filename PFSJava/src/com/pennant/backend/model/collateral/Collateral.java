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
 * * FileName : Collateral.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-12-2013 * * Modified Date :
 * 04-12-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 04-12-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Collateral table</b>.<br>
 * 
 */
public class Collateral implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private String cAFReference = null;
	private String reference;
	private String lastReview;
	private String currency;
	private String lovDescCurrencyName;
	private BigDecimal value;
	private BigDecimal bankvaluation;
	private BigDecimal bankmargin;
	private BigDecimal actualCoverage;
	private BigDecimal proposedCoverage;
	private String description;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private Collateral befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private int ccyFormat;

	private long CustID;

	public boolean isNew() {
		return isNewRecord();
	}

	public Collateral() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Collateral");
	}

	public Collateral(String id) {
		cAFReference = id;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("ccyFormat");

		return excludeFields;
	}

	//Getter and Setter methods

	public long getId() {
		return Long.parseLong(reference);
	}

	public void setId(long id) {
		this.reference = String.valueOf(id);
	}

	public String getCAFReference() {
		return cAFReference;
	}

	public void setCAFReference(String cAFReference) {
		this.cAFReference = cAFReference;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getLastReview() {
		return lastReview;
	}

	public void setLastReview(String lastReview) {
		this.lastReview = lastReview;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLovDescCurrencyName() {
		return this.lovDescCurrencyName;
	}

	public void setLovDescCurrencyName(String lovDescCurrencyName) {
		this.lovDescCurrencyName = lovDescCurrencyName;
	}

	public BigDecimal getValue() {
		if (value != null) {
	        return value;
        }
        return BigDecimal.ZERO;
		
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getBankvaluation() {
		if (bankvaluation != null) {
			return bankvaluation;
		}
		return BigDecimal.ZERO;
	}

	public void setBankvaluation(BigDecimal bankvaluation) {
		this.bankvaluation = bankvaluation;
	}

	public BigDecimal getBankmargin() {
		if (bankmargin != null) {
	        return bankmargin;
        }
        return BigDecimal.ZERO;
	}

	public void setBankmargin(BigDecimal bankmargin) {
		this.bankmargin = bankmargin;
	}

	public BigDecimal getActualCoverage() {
		if (actualCoverage != null) {
	        return actualCoverage;
        }
        return BigDecimal.ZERO;
	}

	public void setActualCoverage(BigDecimal actualCoverage) {
		this.actualCoverage = actualCoverage;
	}

	public BigDecimal getProposedCoverage() {
		if (proposedCoverage != null) {
	        return proposedCoverage;
        }
        return BigDecimal.ZERO;
	}

	public void setProposedCoverage(BigDecimal proposedCoverage) {
		this.proposedCoverage = proposedCoverage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public Collateral getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Collateral beforeImage) {
		this.befImage = beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

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
	public boolean equals(Collateral collateral) {
		return getId() == collateral.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Collateral) {
			Collateral collateral = (Collateral) obj;
			return equals(collateral);
		}
		return false;
	}

	public long getCustID() {
		return CustID;
	}

	public void setCustID(long custID) {
		CustID = custID;
	}

	public void setCcyFormat(int ccyFormat) {
		this.ccyFormat = ccyFormat;
	}

	public int getCcyFormat() {
		return ccyFormat;
	}

}
