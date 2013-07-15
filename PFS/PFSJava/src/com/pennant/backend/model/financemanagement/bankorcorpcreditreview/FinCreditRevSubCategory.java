package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;

public class FinCreditRevSubCategory implements java.io.Serializable {

	private static final long serialVersionUID = 3557119742009775415L;
	private String subCategoryCode;
	private long  categoryId;
	private int subCategorySeque;
	private String subCategoryDesc;
	private String subCategoryItemType;
	private String itemsToCal;
	private String itemRule;	
	private boolean isCreditCCY;
	private boolean format;
	private boolean grand;
	private String mainSubCategoryCode;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditRevSubCategory befImage;
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

	public FinCreditRevSubCategory getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditRevSubCategory beforeImage) {
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
	
	public long getCategoryId() {
    	return categoryId;
    }
	public void setCategoryId(long categoryId) {
    	this.categoryId = categoryId;
    }
	public int getSubCategorySeque() {
    	return subCategorySeque;
    }
	public void setSubCategorySeque(int subCategorySeque) {
    	this.subCategorySeque = subCategorySeque;
    }
	public String getSubCategoryDesc() {
    	return subCategoryDesc;
    }
	public void setSubCategoryDesc(String subCategoryDesc) {
    	this.subCategoryDesc = subCategoryDesc;
    }
	public String getSubCategoryItemType() {
    	return subCategoryItemType;
    }
	public void setSubCategoryItemType(String subCategoryItemType) {
    	this.subCategoryItemType = subCategoryItemType;
    }
	public String getItemsToCal() {
    	return itemsToCal;
    }
	public void setItemsToCal(String itemsToCal) {
    	this.itemsToCal = itemsToCal;
    }
	public String getItemRule() {
    	return itemRule;
    }
	public void setItemRule(String itemRule) {
    	this.itemRule = itemRule;
    }
	public boolean isIsCreditCCY() {
    	return isCreditCCY;
    }
	public void setIsCreditCCY(boolean isCreditCCY) {
    	this.isCreditCCY = isCreditCCY;
    }
	public void setSubCategoryCode(String subCategoryCode) {
	    this.subCategoryCode = subCategoryCode;
    }
	public String getSubCategoryCode() {
	    return subCategoryCode;
    }
	public void setMainSubCategoryCode(String mainSubCategoryCode) {
	    this.mainSubCategoryCode = mainSubCategoryCode;
    }
	public String getMainSubCategoryCode() {
	    return mainSubCategoryCode;
    }
	public void setFormat(boolean format) {
	    this.format = format;
    }
	public boolean isFormat() {
	    return format;
    }
	public void setGrand(boolean grand) {
	    this.grand = grand;
    }
	public boolean isGrand() {
	    return grand;
    }	

}
