/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerDocument.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.interfaces.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Model class for the <b>CustomerDocument table</b>.<br>
 *
 */
public class CustomerDocument implements java.io.Serializable {

	private static final long serialVersionUID = 6420966711989511378L;
	
	private long custID = Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private String custDocType;
	private String custDocName;
	private String custDocCategory;
	private String lovDescCustDocCategory;
	private String custDocTitle;
	private String custDocSysName;
	private Timestamp custDocRcvdOn;
	private Date custDocExpDate;
	private Date custDocIssuedOn;
	private String custDocIssuedCountry;
	private String lovDescCustDocIssuedCountry;
	private boolean custDocIsVerified;
	private long custDocVerifiedBy;
	private boolean custDocIsAcrive;
	private byte[] custDocImage;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CustomerDocument befImage;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustDocVerifiedBy;
	private boolean lovDescdocExpDateIsMand=false;
	private boolean docIssueDateMand=false;
	private boolean docIdNumMand=false;
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

	public CustomerDocument() {
		
	}

	public CustomerDocument(long id) {
		this.setId(id);
	}

	
	public CustomerDocument(String docCategory, String doctype, String docName, byte[] docImage) {
		this.custDocCategory = docCategory;
		this.custDocType = doctype;
		this.custDocTitle = docName;
		this.custDocImage = docImage;
		this.newRecord  = true;
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("docIssueDateMand");
		excludeFields.add("docIdNumMand");
		
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return custID;
	}
	public void setId (long id) {
		this.custID = id;
	}
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}
	
	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getCustDocType() {
		return custDocType;
	}
	public void setCustDocType(String custDocType) {
		this.custDocType = custDocType;
	}
 
	public String getCustDocCategory() {
    	return custDocCategory;
    }

	public void setCustDocCategory(String custDocCategory) {
    	this.custDocCategory = custDocCategory;
    }


	public String getLovDescCustDocCategory() {
    	return lovDescCustDocCategory;
    }

	public void setLovDescCustDocCategory(String lovDescCustDocCategory) {
    	this.lovDescCustDocCategory = lovDescCustDocCategory;
    }

	public String getCustDocTitle() {
		return custDocTitle;
	}
	public void setCustDocTitle(String custDocTitle) {
		this.custDocTitle = custDocTitle;
	}
	
	public String getCustDocName() {
    	return custDocName;
    }

	public void setCustDocName(String custDocName) {
    	this.custDocName = custDocName;
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
		return this.lovDescCustDocIssuedCountry;
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
	
	public byte[] getCustDocImage() {
    	return custDocImage;
    }

	public void setCustDocImage(byte[] custDocImage) {
    	this.custDocImage = custDocImage;
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

	public CustomerDocument getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerDocument beforeImage){
		this.befImage=beforeImage;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}
	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
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
	
	public String getLovDescCustDocVerifiedBy() {
		return lovDescCustDocVerifiedBy;
	}

	public void setLovDescCustDocVerifiedBy(String lovDescCustDocVerifiedBy) {
		this.lovDescCustDocVerifiedBy = lovDescCustDocVerifiedBy;
	}

	public boolean isLovDescdocExpDateIsMand() {
	    return lovDescdocExpDateIsMand;
    }
	public void setLovDescdocExpDateIsMand(boolean lovDescdocExpDateIsMand) {
	    this.lovDescdocExpDateIsMand = lovDescdocExpDateIsMand;
    }

	public boolean isDocIssueDateMand() {
	    return docIssueDateMand;
    }
	public void setDocIssueDateMand(boolean docIssueDateMand) {
	    this.docIssueDateMand = docIssueDateMand;
    }

	public boolean isDocIdNumMand() {
	    return docIdNumMand;
    }
	public void setDocIdNumMand(boolean docIdNumMand) {
	    this.docIdNumMand = docIdNumMand;
    }

	
}
