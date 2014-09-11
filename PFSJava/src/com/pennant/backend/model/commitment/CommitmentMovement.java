package com.pennant.backend.model.commitment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;

public class CommitmentMovement implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2091525494536161888L;
	private String cmtReference;
	private String finReference;
	private String finBranch;
	private String finType;
	private Date movementDate;
	private long movementOrder;
	private String movementType;
	private BigDecimal cmtCharges;
	private BigDecimal movementAmount;
	private BigDecimal cmtAmount;
	private BigDecimal cmtUtilizedAmount;
	private BigDecimal cmtAvailable;
	private long linkedTranId;
	private int version;
	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	@XmlTransient
	private Timestamp lastMntOn;
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private CommitmentMovement befImage;
	@XmlTransient
	private LoginUserDetails userDetails;
	@XmlTransient
	private String recordStatus;
	@XmlTransient
	private String roleCode="";
	@XmlTransient
	private String nextRoleCode= "";
	@XmlTransient
	private String taskId="";
	@XmlTransient
	private String nextTaskId= "";
	@XmlTransient
	private String recordType;
	@XmlTransient
	private String userAction = "Save";
	@XmlTransient
	private long workflowId = 0;
	private HashMap<String, AuditDetail> lovDescAuditDetailMap = new HashMap<String, AuditDetail>();

	public boolean isNew() {
		return isNewRecord();
	}
	public CommitmentMovement() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CommitmentMovement");
	}


	public CommitmentMovement(String id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		return new HashSet<String>();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return finReference;
	}

	public void setId (String id) {
		this.finReference = id;
	}
	public String getCmtReference() {
    	return cmtReference;
    }
	public void setCmtReference(String cmtReference) {
    	this.cmtReference = cmtReference;
    }
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	public String getFinBranch() {
    	return finBranch;
    }
	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }
	public String getFinType() {
    	return finType;
    }
	public void setFinType(String finType) {
    	this.finType = finType;
    }
	public Date getMovementDate() {
    	return movementDate;
    }
	public void setMovementDate(Date movementDate) {
    	this.movementDate = movementDate;
    }
	public long getMovementOrder() {
    	return movementOrder;
    }
	public void setMovementOrder(long movementOrder) {
    	this.movementOrder = movementOrder;
    }
	public String getMovementType() {
    	return movementType;
    }
	public void setMovementType(String movementType) {
    	this.movementType = movementType;
    }
	public BigDecimal getMovementAmount() {
    	return movementAmount;
    }
	public void setMovementAmount(BigDecimal movementAmount) {
    	this.movementAmount = movementAmount;
    }
	public BigDecimal getCmtAmount() {
    	return cmtAmount;
    }
	public void setCmtAmount(BigDecimal cmtAmount) {
    	this.cmtAmount = cmtAmount;
    }
	public BigDecimal getCmtUtilizedAmount() {
    	return cmtUtilizedAmount;
    }
	public void setCmtUtilizedAmount(BigDecimal cmtUtilizedAmount) {
    	this.cmtUtilizedAmount = cmtUtilizedAmount;
    }
	public BigDecimal getCmtAvailable() {
    	return cmtAvailable;
    }
	public void setCmtAvailable(BigDecimal cmtAvailable) {
    	this.cmtAvailable = cmtAvailable;
    }
	public long getLinkedTranId() {
    	return linkedTranId;
    }
	public void setLinkedTranId(long linkedTranId) {
    	this.linkedTranId = linkedTranId;
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
	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}
	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}
	public XMLGregorianCalendar getLastMaintainedOn() {
		return lastMaintainedOn;
	}
	public void setLastMaintainedOn(XMLGregorianCalendar lastMaintainedOn) {
		this.lastMaintainedOn = lastMaintainedOn;
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
	
	public CommitmentMovement getBefImage() {
    	return befImage;
    }
	public void setBefImage(CommitmentMovement befImage) {
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

	public boolean isWorkflow() {
		if (this.workflowId==0){
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
	

	public HashMap<String, AuditDetail> getLovDescAuditDetailMap() {
    	return lovDescAuditDetailMap;
    }
	public void setLovDescAuditDetailMap(HashMap<String, AuditDetail> lovDescAuditDetailMap) {
    	this.lovDescAuditDetailMap = lovDescAuditDetailMap;
    }
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof CommitmentMovement) {
			CommitmentMovement commitmentMovements = (CommitmentMovement) obj;
			return equals(commitmentMovements);
		}
		return false;
	}
	public void setCmtCharges(BigDecimal cmtCharges) {
	    this.cmtCharges = cmtCharges;
    }
	public BigDecimal getCmtCharges() {
	    return cmtCharges;
    }
}
