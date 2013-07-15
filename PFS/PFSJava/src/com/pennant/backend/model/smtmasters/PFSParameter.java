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
 * * FileName : PFSParameter.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-07-2011 * * Modified Date :
 * 12-07-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-07-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.smtmasters;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>PFSParameter table</b>.<br>
 * 
 */
public class PFSParameter implements java.io.Serializable {

	private static final long serialVersionUID = 2039677648479855637L;

	private String sysParmCode = null;
	private String sysParmDesc;
	private String sysParmType;
	private boolean sysParmMaint;
	private String sysParmValue;
	private int sysParmLength;
	private int sysParmDec;
	private String sysParmList;
	private String sysParmValdMod;
	private String sysParmDescription;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private PFSParameter befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public PFSParameter() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("PFSParameter");
	}

	public PFSParameter(String id) {
		this.setId(id);
	}

	public PFSParameter getBefImage() {
		return this.befImage;
	}
	public void setBefImage(PFSParameter beforeImage) {
		this.befImage = beforeImage;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getId() {
		return sysParmCode;
	}
	public void setId(String id) {
		this.sysParmCode = id;
	}

	public void setSysParmCode(String sysParmCode) {
		this.sysParmCode = sysParmCode;
	}
	public String getSysParmCode() {
		return sysParmCode;
	}

	public void setSysParmDesc(String sysParmDesc) {
		this.sysParmDesc = sysParmDesc;
	}
	public String getSysParmDesc() {
		return sysParmDesc;
	}
	
	public void setSysParmType(String sysParmType) {
		this.sysParmType = sysParmType;
	}
	public String getSysParmType() {
		return sysParmType;
	}
	
	public void setSysParmMaint(boolean sysParmMaint) {
		this.sysParmMaint = sysParmMaint;
	}
	public boolean isSysParmMaint() {
		return sysParmMaint;
	}
	
	public void setSysParmValue(String sysParmValue) {
		this.sysParmValue = sysParmValue;
	}
	public String getSysParmValue() {
		return sysParmValue;
	}
	
	public void setSysParmLength(int sysParmLength) {
		this.sysParmLength = sysParmLength;
	}
	public int getSysParmLength() {
		return sysParmLength;
	}
	
	public void setSysParmDec(int sysParmDec) {
		this.sysParmDec = sysParmDec;
	}
	public int getSysParmDec() {
		return sysParmDec;
	}

	public void setSysParmList(String sysParmList) {
		this.sysParmList = sysParmList;
	}
	public String getSysParmList() {
		return sysParmList;
	}

	public void setSysParmValdMod(String sysParmValdMod) {
		this.sysParmValdMod = sysParmValdMod;
	}
	public String getSysParmValdMod() {
		return sysParmValdMod;
	}

	public void setSysParmDescription(String sysParmDescription) {
		this.sysParmDescription = sysParmDescription;
	}
	public String getSysParmDescription() {
		return sysParmDescription;
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
	public void setLastMntOn(Timestamp lastMntOn) {
    	this.lastMntOn = lastMntOn;
    }

	public boolean isNewRecord() {
    	return newRecord;
    }
	public void setNewRecord(boolean newRecord) {
    	this.newRecord = newRecord;
    }
	
	public boolean isNew() {
		return isNewRecord();
	}

	public String getLovValue() {
    	return lovValue;
    }
	public void setLovValue(String lovValue) {
    	this.lovValue = lovValue;
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

	public long getWorkflowId() {
    	return workflowId;
    }
	public void setWorkflowId(long workflowId) {
    	this.workflowId = workflowId;
    }

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof PFSParameter) {
			PFSParameter pFSParameter = (PFSParameter) obj;
			return equals(pFSParameter);
		}
		return false;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(PFSParameter pFSParameter) {
		return getId() == pFSParameter.getId();
	}
	
}
