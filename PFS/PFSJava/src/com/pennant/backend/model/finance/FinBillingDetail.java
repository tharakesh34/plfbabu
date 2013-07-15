package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

public class FinBillingDetail  implements Serializable {
	
    private static final long serialVersionUID = -4721160971744893065L;
    
	private String finReference;
	private Date progClaimDate;
	private BigDecimal progClaimAmount = BigDecimal.ZERO;
	private boolean progClaimBilled = false;
	private int lovDescFinFormatter;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinBillingDetail befImage;
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

	public FinBillingDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinBillingDetail");
	}

	public FinBillingDetail(String id) {
		this.setId(id);
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

	public Date getProgClaimDate() {
    	return progClaimDate;
    }
	public void setProgClaimDate(Date progClaimDate) {
    	this.progClaimDate = progClaimDate;
    }

	public BigDecimal getProgClaimAmount() {
    	return progClaimAmount;
    }
	public void setProgClaimAmount(BigDecimal progClaimAmount) {
    	this.progClaimAmount = progClaimAmount;
    }

	public boolean isProgClaimBilled() {
    	return progClaimBilled;
    }
	public void setProgClaimBilled(boolean progClaimBilled) {
    	this.progClaimBilled = progClaimBilled;
    }

	public int getLovDescFinFormatter() {
    	return lovDescFinFormatter;
    }
	public void setLovDescFinFormatter(int lovDescFinFormatter) {
    	this.lovDescFinFormatter = lovDescFinFormatter;
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

	public FinBillingDetail getBefImage() {
    	return befImage;
    }
	public void setBefImage(FinBillingDetail befImage) {
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
	
	// Overridden Equals method to handle the comparison
	public boolean equals(FinBillingDetail detail) {
		return getId() == detail.getId();
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

		if (obj instanceof FinBillingDetail) {
			FinBillingDetail detail = (FinBillingDetail) obj;
			return equals(detail);
		}
		return false;
	}
	
	
}
