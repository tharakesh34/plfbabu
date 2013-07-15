package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;

public class FinCreditRevType implements java.io.Serializable {

	private static final long serialVersionUID = 3557119742009775415L;
	
	private String creditRevCode;
	private String creditRevDesc;
	private String creditCCY;
	private String entryCCY;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditRevType befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
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

	public FinCreditRevType getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditRevType beforeImage) {
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
	public void setCreditRevCode(String creditRevCode) {
	    this.creditRevCode = creditRevCode;
    }
	public String getCreditRevCode() {
	    return creditRevCode;
    }
	public void setCreditRevDesc(String creditRevDesc) {
	    this.creditRevDesc = creditRevDesc;
    }
	public String getCreditRevDesc() {
	    return creditRevDesc;
    }
	public void setCreditCCY(String creditCCY) {
	    this.creditCCY = creditCCY;
    }
	public String getCreditCCY() {
	    return creditCCY;
    }
	public void setEntryCCY(String entryCCY) {
	    this.entryCCY = entryCCY;
    }
	public String getEntryCCY() {
	    return entryCCY;
    }


}
