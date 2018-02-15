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
 *
 * FileName    		:  QueueAssignment.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class QueueAssignment extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	
	private String module;
	private long fromUserId = Long.MIN_VALUE;
	private long userId = Long.MIN_VALUE;
	private String userRoleCode;
	private String lovDescActualOwner;
	private String reference;
	private String lovDescUserName;
	private String lovDescFinType;
	private String lovDescFinTypeDesc;
	private long lovDescQAUserId;
	private List<TaskOwners> taskOwnersList = new ArrayList<TaskOwners>();
	private List<String> finReferenceList = new ArrayList<String>();
	private String lovDescUserAction;
	private BigDecimal lovDescFinAmount;
	private int lovDescEditField;
	private String lovDescCustCIF;
	
	private int assignedCount=0;
	private Timestamp lastAssignedOn = new Timestamp(System.currentTimeMillis());
	private int processedCount = 0;
	private Timestamp lastProcessedOn = null;
	private boolean userActive = true;
	private boolean newRecord=false;
	private String lovValue;
	private QueueAssignment befImage;
	private LoggedInUser userDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private boolean recordProcessed;
	private boolean manualAssign;
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("assignedCount");
		excludeFields.add("lastAssignedOn");
		excludeFields.add("processedCount");
		excludeFields.add("lastProcessedOn");
		excludeFields.add("userActive");
		excludeFields.add("newRecord");
		excludeFields.add("lovValue");
		excludeFields.add("befImage");
		excludeFields.add("userDetails");
		excludeFields.add("userAction");
		excludeFields.add("auditDetailMap");
		excludeFields.add("recordProcessed");
		return excludeFields;
	}
	
	public boolean isNew() {
		return isNewRecord();
	}
	
	public QueueAssignment() {
		super();
	}
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public Timestamp getLastAssignedOn() {
		return lastAssignedOn;
	}
	public void setLastAssignedOn(Timestamp lastAssignedOn) {
		this.lastAssignedOn = lastAssignedOn;
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
	
	public int getAssignedCount() {
		return assignedCount;
	}
	public void setAssignedCount(int assignedCount) {
		this.assignedCount = assignedCount;
	}
	
	public int getProcessedCount() {
		return processedCount;
	}
	public void setProcessedCount(int processedCount) {
		this.processedCount = processedCount;
	}
	
	public List<TaskOwners> getTaskOwnersList() {
	    return taskOwnersList;
    }
	public void setTaskOwnersList(List<TaskOwners> taskOwnersList) {
	    this.taskOwnersList = taskOwnersList;
    }
	
	public List<String> getFinReferenceList() {
	    return finReferenceList;
    }
	public void setFinReferenceList(List<String> finReferenceList) {
	    this.finReferenceList = finReferenceList;
    }
	public String getLovDescUserAction() {
	    return lovDescUserAction;
    }
	public void setLovDescUserAction(String lovDescUserAction) {
	    this.lovDescUserAction = lovDescUserAction;
    }
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
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
	public QueueAssignment getBefImage() {
		return befImage;
	}
	public void setBefImage(QueueAssignment befImage) {
		this.befImage = befImage;
	}
	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public BigDecimal getLovDescFinAmount() {
		return lovDescFinAmount;
	}
	public void setLovDescFinAmount(BigDecimal lovDescFinAmount) {
		this.lovDescFinAmount = lovDescFinAmount;
	}
	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}
	public int getLovDescEditField() {
	    return lovDescEditField;
    }
	public void setLovDescEditField(int lovDescEditField) {
	    this.lovDescEditField = lovDescEditField;
    }

	public long getFromUserId() {
	    return fromUserId;
    }
	public void setFromUserId(long fromUserId) {
	    this.fromUserId = fromUserId;
    }

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
	    return auditDetailMap;
    }

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
	    this.auditDetailMap = auditDetailMap;
    }
	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;
	}

	public String getLovDescUserName() {
	    return lovDescUserName;
    }
	public void setLovDescUserName(String lovDescUserName) {
	    this.lovDescUserName = lovDescUserName;
    }

	public String getLovDescFinType() {
	    return lovDescFinType;
    }

	public void setLovDescFinType(String lovDescFinType) {
	    this.lovDescFinType = lovDescFinType;
    }

	public String getLovDescFinTypeDesc() {
	    return lovDescFinTypeDesc;
    }

	public void setLovDescFinTypeDesc(String lovDescFinTypeDesc) {
	    this.lovDescFinTypeDesc = lovDescFinTypeDesc;
    }

	public String getLovDescActualOwner() {
	    return lovDescActualOwner;
    }

	public void setLovDescActualOwner(String lovDescActualOwner) {
	    this.lovDescActualOwner = lovDescActualOwner;
    }

	public boolean isRecordProcessed() {
	    return recordProcessed;
    }

	public void setRecordProcessed(boolean recordProcessed) {
	    this.recordProcessed = recordProcessed;
    }

	public boolean isManualAssign() {
	    return manualAssign;
    }

	public void setManualAssign(boolean manualAssign) {
	    this.manualAssign = manualAssign;
    }
}
