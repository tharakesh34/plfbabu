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

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>VehicleModel table</b>.<br>
 *
 */
public class VehicleModel extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long vehicleModelId = Long.MIN_VALUE;
	private long VehicleManufacturerId;
	private String lovDescVehicleManufacturerName;
	private String vehicleModelDesc;
	private boolean newRecord;
	private String lovValue;
	private VehicleModel befImage;
	private LoggedInUser userDetails;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public VehicleModel() {
		super();
	}

	public VehicleModel(long id) {
		super();
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public void setLovDescVehicleManufacturerName(
			String lovDescVehicleManufacturerName) {
		this.lovDescVehicleManufacturerName = lovDescVehicleManufacturerName;
	}
}
