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
 * * FileName : CustomerRating.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified Date
 * : 26-05-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.equation.process;

import java.sql.Timestamp;


/**
 * Model class for the <b>CustomerRating table</b>.<br>
 * 
 */
public class EquationCustomerRating implements java.io.Serializable {

	private static final long serialVersionUID = -5720554941556360647L;

	private long custID = Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private String custRatingType;
	private String lovDescCustRatingTypeName;
	private String custRatingCode;
	private String lovDesccustRatingCodeDesc;
	private String custRating;
	private String lovDescCustRatingName;
	private boolean valueType;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private EquationCustomerRating befImage;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;


	public long getId() {
		return custID;
	}

	public void setId(long id) {
		this.custID = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getCustRatingType() {
		return custRatingType;
	}

	public void setCustRatingType(String custRatingType) {
		this.custRatingType = custRatingType;
	}

	public String getLovDescCustRatingTypeName() {
		return this.lovDescCustRatingTypeName;
	}

	public void setLovDescCustRatingTypeName(String lovDescCustRatingTypeName) {
		this.lovDescCustRatingTypeName = lovDescCustRatingTypeName;
	}

	public String getCustRatingCode() {
		return custRatingCode;
	}

	public void setCustRatingCode(String custRatingCode) {
		this.custRatingCode = custRatingCode;
	}
	public String getCustRating() {
		return custRating;
	}

	public void setCustRating(String custRating) {
		this.custRating = custRating;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}

	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDesccustRatingCodeDesc() {
    	return lovDesccustRatingCodeDesc;
    }

	public void setLovDesccustRatingCodeDesc(String lovDesccustRatingCodeDesc) {
    	this.lovDesccustRatingCodeDesc = lovDesccustRatingCodeDesc;
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

	public EquationCustomerRating getBefImage() {
		return this.befImage;
	}

	public void setBefImage(EquationCustomerRating beforeImage) {
		this.befImage = beforeImage;
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

	public void setValueType(boolean valueType) {
		this.valueType = valueType;
	}

	public boolean isValueType() {
		return valueType;
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

	public void setLovDescCustRatingName(String lovDescCustRatingName) {
	    this.lovDescCustRatingName = lovDescCustRatingName;
    }

	public String getLovDescCustRatingName() {
	    return lovDescCustRatingName;
    }

}
