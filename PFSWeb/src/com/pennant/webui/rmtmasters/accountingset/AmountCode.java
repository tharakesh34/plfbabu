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
 * FileName    		:  AmountCode.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-09-2011    														*
 *                                                                  						*
 * Modified Date    :  15-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accountingset;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>AmountCode table</b>.<br>
 *
 */
public class AmountCode implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String allowedEvent;
	private String lovDescAllowedEventName;
	private boolean allowedRIA;
	private String amountCode = null;
	private String amountCodeDesc;
	private boolean slabIsActive;
	private boolean amountCodeIsActive;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private AmountCode befImage;
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

	public AmountCode() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("AmountCode");
	}

	public AmountCode(String id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	
	public String getAllowedEvent() {
		return allowedEvent;
	}
	public void setAllowedEvent(String allowedEvent) {
		this.allowedEvent = allowedEvent;
	}
	

	public String getLovDescAllowedEventName() {
		return this.lovDescAllowedEventName;
	}

	public void setLovDescAllowedEventName (String lovDescAllowedEventName) {
		this.lovDescAllowedEventName = lovDescAllowedEventName;
	}
	
		
	public String getId() {
		return amountCode;
	}
	
	public void setId (String id) {
		this.amountCode = id;
	}
	
	public void setAllowedRIA(boolean allowedRIA) {
		this.allowedRIA = allowedRIA;
	}

	public boolean isAllowedRIA() {
		return allowedRIA;
	}

	public String getAmountCode() {
		return amountCode;
	}
	public void setAmountCode(String amountCode) {
		this.amountCode = amountCode;
	}
	
	
		
	
	public String getAmountCodeDesc() {
		return amountCodeDesc;
	}
	public void setAmountCodeDesc(String amountCodeDesc) {
		this.amountCodeDesc = amountCodeDesc;
	}
	
	public boolean isSlabIsActive() {
		return slabIsActive;
	}
	public void setSlabIsActive(boolean slabIsActive) {
		this.slabIsActive = slabIsActive;
	}

	public boolean isAmountCodeIsActive() {
		return amountCodeIsActive;
	}
	public void setAmountCodeIsActive(boolean amountCodeIsActive) {
		this.amountCodeIsActive = amountCodeIsActive;
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

	public AmountCode getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(AmountCode beforeImage){
		this.befImage=beforeImage;
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

	// Overidden Equals method to handle the comparision
	public boolean equals(AmountCode amountCode) {
		return getId() == amountCode.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof AmountCode) {
			AmountCode amountCode = (AmountCode) obj;
			return equals(amountCode);
		}
		return false;
	}
}
