package com.pennant.backend.model.documentdetails;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
	private boolean				docIsCustDoc;
	
	private String 				custDocTitle;
	private String 				custDocSysName;
	private Timestamp 			custDocRcvdOn;
	private Date 				custDocExpDate;
	private Date 				custDocIssuedOn;
	private String				custDocIssuedCountry;
	private String 				lovDescCustDocIssuedCountry;
	private boolean 			custDocIsVerified;
	private long 				custDocVerifiedBy;
	private boolean 			custDocIsAcrive;
	private String				lovDescCustCIF;
	
	private String 				lovDescDocCategoryName;
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
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custDocTitle");
		excludeFields.add("custDocSysName");
		excludeFields.add("custDocRcvdOn");
		excludeFields.add("custDocExpDate");
		excludeFields.add("custDocIssuedOn");
		excludeFields.add("custDocIssuedCountry");
		excludeFields.add("custDocIsVerified");
		excludeFields.add("custDocVerifiedBy");
		excludeFields.add("custDocIsAcrive");
		excludeFields.add("docIsCustDoc");
		return excludeFields;
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

	public void setLovDescDocCategoryName(String lovDescDocCategoryName) {
	    this.lovDescDocCategoryName = lovDescDocCategoryName;
    }

	public String getLovDescDocCategoryName() {
	    return lovDescDocCategoryName;
    }

	public boolean isDocIsCustDoc() {
    	return docIsCustDoc;
    }

	public String getCustDocTitle() {
    	return custDocTitle;
    }

	public void setCustDocTitle(String custDocTitle) {
    	this.custDocTitle = custDocTitle;
    }

	public String getCustDocSysName() {
    	return custDocSysName;
    }

	public void setCustDocSysName(String custDocSysName) {
    	this.custDocSysName = custDocSysName;
    }

	public Timestamp getCustDocRcvdOn() {
    	return custDocRcvdOn;
    }

	public void setCustDocRcvdOn(Timestamp custDocRcvdOn) {
    	this.custDocRcvdOn = custDocRcvdOn;
    }

	public Date getCustDocExpDate() {
    	return custDocExpDate;
    }

	public void setCustDocExpDate(Date custDocExpDate) {
    	this.custDocExpDate = custDocExpDate;
    }

	public Date getCustDocIssuedOn() {
    	return custDocIssuedOn;
    }

	public void setCustDocIssuedOn(Date custDocIssuedOn) {
    	this.custDocIssuedOn = custDocIssuedOn;
    }

	public String getCustDocIssuedCountry() {
    	return custDocIssuedCountry;
    }

	public void setCustDocIssuedCountry(String custDocIssuedCountry) {
    	this.custDocIssuedCountry = custDocIssuedCountry;
    }

	public String getLovDescCustDocIssuedCountry() {
    	return lovDescCustDocIssuedCountry;
    }

	public void setLovDescCustDocIssuedCountry(String lovDescCustDocIssuedCountry) {
    	this.lovDescCustDocIssuedCountry = lovDescCustDocIssuedCountry;
    }

	public boolean isCustDocIsVerified() {
    	return custDocIsVerified;
    }

	public void setCustDocIsVerified(boolean custDocIsVerified) {
    	this.custDocIsVerified = custDocIsVerified;
    }

	public long getCustDocVerifiedBy() {
    	return custDocVerifiedBy;
    }

	public void setCustDocVerifiedBy(long custDocVerifiedBy) {
    	this.custDocVerifiedBy = custDocVerifiedBy;
    }

	public boolean isCustDocIsAcrive() {
    	return custDocIsAcrive;
    }

	public void setCustDocIsAcrive(boolean custDocIsAcrive) {
    	this.custDocIsAcrive = custDocIsAcrive;
    }

	public void setDocIsCustDoc(boolean docIsCustDoc) {
    	this.docIsCustDoc = docIsCustDoc;
    }

	public String getLovDescCustCIF() {
    	return lovDescCustCIF;
    }

	public void setLovDescCustCIF(String lovDescCustCIF) {
    	this.lovDescCustCIF = lovDescCustCIF;
    }

}
