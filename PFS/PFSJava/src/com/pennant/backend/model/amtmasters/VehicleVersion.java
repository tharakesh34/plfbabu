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
 * FileName    		:  VehicleVersion.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.amtmasters;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>VehicleVersion table</b>.<br>
 *
 */
public class VehicleVersion implements java.io.Serializable, Entity {
	
	private static final long serialVersionUID = 103295340132704280L;
	
	private long vehicleVersionId = Long.MIN_VALUE;
	private long vehicleModelId;
	private String lovDescVehicleModelIdName;
	private long lovDescmanufacturerId;
	private String lovDescmanufacturerName;
	private String lovDescVehicleModelDesc;
	private String vehicleVersionCode;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private VehicleVersion befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long   workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public VehicleVersion() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("VehicleVersion");
	}

	public VehicleVersion(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return vehicleVersionId;
	}
	public void setId (long id) {
		this.vehicleVersionId = id;
	}
	
	public long getVehicleVersionId() {
		return vehicleVersionId;
	}
	public void setVehicleVersionId(long vehicleVersionId) {
		this.vehicleVersionId = vehicleVersionId;
	}
	
	public long getVehicleModelId() {
		return vehicleModelId;
	}
	public void setVehicleModelId(long vehicleModelId) {
		this.vehicleModelId = vehicleModelId;
	}

	public String getLovDescVehicleModelIdName() {
		return this.lovDescVehicleModelIdName;
	}
	public void setLovDescVehicleModelIdName (String lovDescVehicleModelIdName) {
		this.lovDescVehicleModelIdName = lovDescVehicleModelIdName;
	}
	
	public String getVehicleVersionCode() {
		return vehicleVersionCode;
	}
	public void setVehicleVersionCode(String vehicleVersionCode) {
		this.vehicleVersionCode = vehicleVersionCode;
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

	public VehicleVersion getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(VehicleVersion beforeImage){
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

	// Overridden Equals method to handle the comparison
	public boolean equals(VehicleVersion vehicleVersion) {
		return getId() == vehicleVersion.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof VehicleVersion) {
			VehicleVersion vehicleVersion = (VehicleVersion) obj;
			return equals(vehicleVersion);
		}
		return false;
	}

	public long getLovDescmanufacturerId() {
		return lovDescmanufacturerId;
	}
	public void setLovDescmanufacturerId(long lovDescmanufacturerId) {
		this.lovDescmanufacturerId = lovDescmanufacturerId;
	}

	public String getLovDescmanufacturerName() {
		return lovDescmanufacturerName;
	}
	public void setLovDescmanufacturerName(String lovDescmanufacturerName) {
		this.lovDescmanufacturerName = lovDescmanufacturerName;
	}

	public String getLovDescVehicleModelDesc() {
		return lovDescVehicleModelDesc;
	}
	public void setLovDescVehicleModelDesc(String lovDescVehicleModelDesc) {
		this.lovDescVehicleModelDesc = lovDescVehicleModelDesc;
	}
	
	

}
