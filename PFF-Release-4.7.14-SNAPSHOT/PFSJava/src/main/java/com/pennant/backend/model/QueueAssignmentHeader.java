package com.pennant.backend.model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class QueueAssignmentHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	
	private String module;
	private String userId;
	private String lovDescUserName;
	private long fromUserId = Long.MIN_VALUE;
	private long lovDescQAUserId;
	private String userRoleCode;
	private String roleDesc;
	private int assignedCount=0;
	private String reference;
	private Timestamp lastAssignedOn = new Timestamp(System.currentTimeMillis());
	private int processedCount = 0;
	private Timestamp lastProcessedOn = null;
	private boolean userActive = true;
	private boolean singleUser = false;
	private boolean lovDescNewRecord = false;
	private QueueAssignmentHeader befImage;
	private boolean newRecord=false;
	private LoggedInUser userDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private List<QueueAssignment> queueAssignmentsList;
	private boolean manualAssign = false;
	
	public QueueAssignmentHeader() {
		super();
	}
	
	public boolean isNew() {
		return isNewRecord();
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLovDescUserName() {
		return lovDescUserName;
	}
	public void setLovDescUserName(String lovDescUserName) {
		this.lovDescUserName = lovDescUserName;
	}
	public long getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}
	public long getLovDescQAUserId() {
		return lovDescQAUserId;
	}
	public void setLovDescQAUserId(long lovDescQAUserId) {
		this.lovDescQAUserId = lovDescQAUserId;
	}
	public String getUserRoleCode() {
		return userRoleCode;
	}
	public void setUserRoleCode(String userRoleCode) {
		this.userRoleCode = userRoleCode;
	}

	public List<QueueAssignment> getQueueAssignmentsList() {
	    return queueAssignmentsList;
    }
	public void setQueueAssignmentsList(List<QueueAssignment> queueAssignmentsList) {
	    this.queueAssignmentsList = queueAssignmentsList;
    }
	public int getAssignedCount() {
	    return assignedCount;
    }
	public void setAssignedCount(int assignedCount) {
	    this.assignedCount = assignedCount;
    }
	public LoggedInUser getUserDetails() {
	    return userDetails;
    }
	public void setUserDetails(LoggedInUser userDetails) {
	    this.userDetails = userDetails;
    }
	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
	    return auditDetailMap;
    }
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
	    this.auditDetailMap = auditDetailMap;
    }
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public String getModule() {
	    return module;
    }
	public void setModule(String module) {
	    this.module = module;
    }

	public QueueAssignmentHeader getBefImage() {
	    return befImage;
    }

	public void setBefImage(QueueAssignmentHeader befImage) {
	    this.befImage = befImage;
    }

	public boolean isLovDescNewRecord() {
	    return lovDescNewRecord;
    }

	public void setLovDescNewRecord(boolean lovDescNewRecord) {
	    this.lovDescNewRecord = lovDescNewRecord;
    }

	public String getReference() {
	    return reference;
    }

	public void setReference(String reference) {
	    this.reference = reference;
    }

	public Timestamp getLastAssignedOn() {
	    return lastAssignedOn;
    }

	public void setLastAssignedOn(Timestamp lastAssignedOn) {
	    this.lastAssignedOn = lastAssignedOn;
    }
	public int getProcessedCount() {
		return processedCount;
	}
	public void setProcessedCount(int processedCount) {
		this.processedCount = processedCount;
	}

	public Timestamp getLastProcessedOn() {
	    return lastProcessedOn;
    }

	public void setLastProcessedOn(Timestamp lastProcessedOn) {
	    this.lastProcessedOn = lastProcessedOn;
    }

	public boolean isUserActive() {
	    return userActive;
    }
	public void setUserActive(boolean userActive) {
	    this.userActive = userActive;
    }

	public boolean isSingleUser() {
	    return singleUser;
    }

	public void setSingleUser(boolean singleUser) {
	    this.singleUser = singleUser;
    }

	public String getRoleDesc() {
	    return roleDesc;
    }

	public void setRoleDesc(String roleDesc) {
	    this.roleDesc = roleDesc;
    }

	public boolean isManualAssign() {
	    return manualAssign;
    }

	public void setManualAssign(boolean manualAssign) {
	    this.manualAssign = manualAssign;
    }
}
