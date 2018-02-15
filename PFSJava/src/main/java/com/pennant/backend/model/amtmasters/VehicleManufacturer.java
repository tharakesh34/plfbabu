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
 * FileName    		:  VehicleManufacturer.java                                                   * 	  
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

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>VehicleManufacturer table</b>.<br>
 *
 */
public class VehicleManufacturer extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	private long manufacturerId = Long.MIN_VALUE;
	private String manufacturerName;
	private boolean newRecord;
	private String lovValue;
	private VehicleManufacturer befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public VehicleManufacturer() {
		super();
	}

	public VehicleManufacturer(long id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods
	
	public long getId() {
		return manufacturerId;
	}
	
	public void setId (long id) {
		this.manufacturerId = id;
	}
	
	public long getManufacturerId() {
		return manufacturerId;
	}
	public void setManufacturerId(long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}
	
	
		
	
	public String getManufacturerName() {
		return manufacturerName;
	}
	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
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

	public VehicleManufacturer getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(VehicleManufacturer beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
