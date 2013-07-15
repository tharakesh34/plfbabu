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
 * FileName    		:  VehicleModel.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>VehicleModel table</b>.<br>
 *
 */
public class VehicleModel implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private long vehicleModelId = Long.MIN_VALUE;
	private long VehicleManufacturerId;
	private String lovDescVehicleManufacturerName;
	private String vehicleModelDesc;
	private String manufacturerName;
	private String lovDescManufacter;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private VehicleModel befImage;
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

	public VehicleModel() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("VehicleModel");
	}

	public VehicleModel(long id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	public long getId() {
		return vehicleModelId;
	}
	
	public void setId (long id) {
		this.vehicleModelId = id;
	}
	
	public long getVehicleModelId() {
		return vehicleModelId;
	}
	public void setVehicleModelId(long vehicleModelId) {
		this.vehicleModelId = vehicleModelId;
	}
	
	
		
	
	public String getVehicleModelDesc() {
		return vehicleModelDesc;
	}
	public void setVehicleModelDesc(String vehicleModelDesc) {
		this.vehicleModelDesc = vehicleModelDesc;
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

	public VehicleModel getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(VehicleModel beforeImage){
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
	public boolean equals(VehicleModel vehicleModel) {
		return getId() == vehicleModel.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof VehicleModel) {
			VehicleModel vehicleModel = (VehicleModel) obj;
			return equals(vehicleModel);
		}
		return false;
	}

	public long getVehicleManufacturerId() {
		return VehicleManufacturerId;
	}

	public void setVehicleManufacturerId(long vehicleManufacturerId) {
		VehicleManufacturerId = vehicleManufacturerId;
	}

	public String getLovDescVehicleManufacturerName() {
		return lovDescVehicleManufacturerName;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getLovDescManufacter() {
		return lovDescManufacter;
	}

	public void setLovDescManufacter(String lovDescManufacter) {
		this.lovDescManufacter = lovDescManufacter;
	}

	public void setLovDescVehicleManufacturerName(
			String lovDescVehicleManufacturerName) {
		this.lovDescVehicleManufacturerName = lovDescVehicleManufacturerName;
	}
	
	
}
