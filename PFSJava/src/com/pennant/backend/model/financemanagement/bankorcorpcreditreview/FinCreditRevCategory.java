package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;

public class FinCreditRevCategory implements java.io.Serializable {

	private static final long serialVersionUID = 3557119742009775415L;
	private long categoryId;
	private String  categoryDesc;	
	private String creditRevCode;
	private String remarks;
	private boolean brkdowndsply;
	private boolean changedsply;
	private int noOfyears ;	
	private int categorySeque;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditRevCategory befImage;
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

	public FinCreditRevCategory getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditRevCategory beforeImage) {
		this.befImage = beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	
	public String getCategoryDesc() {
    	return categoryDesc;
    }
	public void setCategoryDesc(String categoryDesc) {
    	this.categoryDesc = categoryDesc;
    }
	public String getCreditRevCode() {
    	return creditRevCode;
    }
	public void setCreditRevCode(String creditRevCode) {
    	this.creditRevCode = creditRevCode;
    }
	public int getNoOfyears() {
    	return noOfyears;
    }
	public void setNoOfyears(int noOfyears) {
    	this.noOfyears = noOfyears;
    }
	public int getCategorySeque() {
    	return categorySeque;
    }
	public void setCategorySeque(int categorySeque) {
    	this.categorySeque = categorySeque;
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
	public void setCategoryId(long categoryId) {
	    this.categoryId = categoryId;
    }
	public long getCategoryId() {
	    return categoryId;
    }
	public void setRemarks(String remarks) {
	    this.remarks = remarks;
    }
	public String getRemarks() {
	    return remarks;
    }
	
	public boolean isBrkdowndsply() {
    	return brkdowndsply;
    }
	public void setBrkdowndsply(boolean brkdowndsply) {
    	this.brkdowndsply = brkdowndsply;
    }
	public boolean isChangedsply() {
    	return changedsply;
    }
	public void setChangedsply(boolean changedsply) {
    	this.changedsply = changedsply;
    }
}
