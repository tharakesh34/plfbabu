package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

public class FinBillingHeader implements Serializable {
	
    private static final long serialVersionUID = -4721160971744893065L;
    
	private String finReference;
	private BigDecimal preContrOrDeffCost;
	private BigDecimal contrBillRetain;
	private boolean autoAcClaimDate;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinBillingHeader befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	private List<FinBillingDetail> billingDetailList = new ArrayList<FinBillingDetail>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinBillingHeader() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinBillingHeader");
	}

	public FinBillingHeader(String id) {
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

	public BigDecimal getPreContrOrDeffCost() {
    	return preContrOrDeffCost;
    }
	public void setPreContrOrDeffCost(BigDecimal preContrOrDeffCost) {
    	this.preContrOrDeffCost = preContrOrDeffCost;
    }

	public BigDecimal getContrBillRetain() {
    	return contrBillRetain;
    }
	public void setContrBillRetain(BigDecimal contrBillRetain) {
    	this.contrBillRetain = contrBillRetain;
    }

	public boolean getAutoAcClaimDate() {
    	return autoAcClaimDate;
    }
	public void setAutoAcClaimDate(boolean autoAcClaimDate) {
    	this.autoAcClaimDate = autoAcClaimDate;
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

	public FinBillingHeader getBefImage() {
    	return befImage;
    }
	public void setBefImage(FinBillingHeader befImage) {
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
	
	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}
	
	public void setWorkflowId(long workflowId) {
    	this.workflowId = workflowId;
    }
	
	public List<FinBillingDetail> getBillingDetailList() {
    	return billingDetailList;
    }
	public void setBillingDetailList(List<FinBillingDetail> billingDetailList) {
    	this.billingDetailList = billingDetailList;
    }

	// Overridden Equals method to handle the comparison
	public boolean equals(FinBillingHeader detail) {
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

		if (obj instanceof FinBillingHeader) {
			FinBillingHeader detail = (FinBillingHeader) obj;
			return equals(detail);
		}
		return false;
	}
}
