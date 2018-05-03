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

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>VehicleVersion table</b>.<br>
 *
 */
public class VehicleVersion extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 103295340132704280L;
	
	private long vehicleVersionId = Long.MIN_VALUE;
	private long vehicleModelId;
	private String lovDescVehicleModelIdName;
	private long lovDescmanufacturerId;
	private String lovDescmanufacturerName;
	private String lovDescVehicleModelDesc;
	private String vehicleVersionCode;
	private String vehicleCategory;
	private int vehicleCc;
	private int vehicleDoors;
	private boolean newRecord;
	private String lovValue;
	private VehicleVersion befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public VehicleVersion() {
		super();
	}

	public VehicleVersion(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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
	
	public String getVehicleCategory() {
		return vehicleCategory;
	}
	public void setVehicleCategory(String vehicleCategory) {
		this.vehicleCategory = vehicleCategory;
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public int getVehicleCc() {
		return vehicleCc;
	}
	public void setVehicleCc(int vehicleCc) {
		this.vehicleCc = vehicleCc;
	}

	public int getVehicleDoors() {
		return vehicleDoors;
	}
	public void setVehicleDoors(int vehicleDoors) {
		this.vehicleDoors = vehicleDoors;
	}
}
