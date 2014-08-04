package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

public class FinAgreementDetail implements Serializable {

    private static final long serialVersionUID = -4590527012741917367L;
    
	private String finReference;
	private long agrId = Long.MIN_VALUE;
	private String finType;
	private String agrName;
	private String lovDescAgrName;
	private byte[] agrContent;
	private boolean lovDescMandInput;

	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinAgreementDetail befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinAgreementDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinAgreementDetail");
	}

	public FinAgreementDetail(String id) {
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getId() {
		return finReference;
	}
	public void setId (String id) {
		this.finReference = id;
	}

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }

	public long getAgrId() {
    	return agrId;
    }
	public void setAgrId(long agrId) {
    	this.agrId = agrId;
    }

	public void setFinType(String finType) {
	    this.finType = finType;
    }
	public String getFinType() {
	    return finType;
    }
	
	public String getAgrName() {
    	return agrName;
    }
	public void setAgrName(String agrName) {
    	this.agrName = agrName;
    }

	public void setLovDescAgrName(String lovDescAgrName) {
	    this.lovDescAgrName = lovDescAgrName;
    }
	public String getLovDescAgrName() {
	    return lovDescAgrName;
    }

	public byte[] getAgrContent() {
    	return agrContent;
    }
	public void setAgrContent(byte[] agrContent) {
    	this.agrContent = agrContent;
    }

	public void setLovDescMandInput(boolean lovDescMandInput) {
	    this.lovDescMandInput = lovDescMandInput;
    }
	public boolean isLovDescMandInput() {
	    return lovDescMandInput;
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

	public String getLovValue() {
    	return lovValue;
    }
	public void setLovValue(String lovValue) {
    	this.lovValue = lovValue;
    }

	public FinAgreementDetail getBefImage() {
    	return befImage;
    }
	public void setBefImage(FinAgreementDetail befImage) {
    	this.befImage = befImage;
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
		if (this.workflowId==0){
			return false;
		}
		return true;
	}
	
	// Overidden Equals method to handle the comparision
	public boolean equals(FinAgreementDetail agreementDetail) {
		return getId() == agreementDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinAgreementDetail) {
			FinAgreementDetail agreementDetail = (FinAgreementDetail) obj;
			return equals(agreementDetail);
		}
		return false;
	}

	
}
