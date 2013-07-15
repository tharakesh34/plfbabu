package com.pennant.backend.model.documentdetails;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;

public class DocumentDetails implements java.io.Serializable, Entity {

	private static final long	serialVersionUID	= -5569765259024813213L;
	private long				docId				= Long.MIN_VALUE;
	private String				docModule;
	private String				referenceId="";

	private String				docCategory;
	private String				doctype;
	private String				docName;
	private byte[]				docImage;
	
	private DocumentDetails 	befImage;
	private LoginUserDetails	userDetails;
	private boolean 			newRecord = false;
	private int					version;
	private long				lastMntBy;
	private Timestamp			lastMntOn;
	private String				recordStatus;
	private String				roleCode			= "";
	private String				nextRoleCode		= "";
	private String				taskId				= "";
	private String				nextTaskId			= "";
	private String				recordType;
	private String				userAction			= "Save";
	private long				workflowId			= 0;

	public DocumentDetails() {

	}

	public DocumentDetails(String docModule, String docCategory, String doctype, String docName, byte[] docImage) {
		this.docModule = docModule;
		this.docCategory = docCategory;
		this.doctype = doctype;
		this.docName = docName;
		this.docImage = docImage;
		this.newRecord  = true;
	}

	@Override
	public boolean isNew() {
		return newRecord;
	}

	@Override
	public long getId() {
		return docId;
	}

	@Override
	public void setId(long id) {
		this.docId = id;

	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getDocModule() {
		return docModule;
	}

	public void setDocModule(String docModule) {
		this.docModule = docModule;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
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

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}
	
	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}
	
	// Overidden Equals method to handle the comparision
	public boolean equals(DocumentDetails details) {
		return getId() == details.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof DocumentDetails) {
			DocumentDetails details = (DocumentDetails) obj;
			return equals(details);
		}
		return false;
	}

	public void setBefImage(DocumentDetails befImage) {
		this.befImage = befImage;
	}

	public DocumentDetails getBefImage() {
		return befImage;
	}

	public void setReferenceId(String referenceId) {
	    this.referenceId = referenceId;
    }
	
	public void setRefId(long referenceId) {
	    this.referenceId = String.valueOf(referenceId);
    }

	public String getReferenceId() {
	    return referenceId;
    }

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

}
