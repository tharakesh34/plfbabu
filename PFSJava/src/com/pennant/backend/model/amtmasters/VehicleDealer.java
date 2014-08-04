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
 * FileName    		:  VehicleDealer.java                                                   * 	  
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
 * Model class for the <b>VehicleDealer table</b>.<br>
 *
 */
public class VehicleDealer implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private long dealerId = Long.MIN_VALUE;
	private String dealerType;
	private String dealerName;
	private String dealerTelephone;
	private String dealerFax;
	private String dealerAddress1;
	private String dealerAddress2;
	private String dealerAddress3;
	private String dealerAddress4;
	private String dealerCountry;
	private String dealerCity;
	private String dealerProvince;
	private String lovDescCountry;
	private String lovDescCity;
	private String lovDescProvince;
	
    private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private VehicleDealer befImage;
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

	public VehicleDealer() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("VehicleDealer");
	}

	public VehicleDealer(long id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	public long getId() {
		return dealerId;
	}
	
	public void setId (long id) {
		this.dealerId = id;
	}
	
	public long getDealerId() {
		return dealerId;
	}
	public void setDealerId(long dealerId) {
		this.dealerId = dealerId;
	}
	
	public String getDealerType() {
    	return dealerType;
    }

	public void setDealerType(String dealerType) {
    	this.dealerType = dealerType;
    }

	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	
	
		
	
	public String getDealerTelephone() {
    	return dealerTelephone;
    }

	public void setDealerTelephone(String dealerTelephone) {
    	this.dealerTelephone = dealerTelephone;
    }

	public String getDealerFax() {
    	return dealerFax;
    }

	public void setDealerFax(String dealerFax) {
    	this.dealerFax = dealerFax;
    }

	public String getDealerAddress1() {
    	return dealerAddress1;
    }

	public void setDealerAddress1(String dealerAddress1) {
    	this.dealerAddress1 = dealerAddress1;
    }

	public String getDealerAddress2() {
    	return dealerAddress2;
    }

	public void setDealerAddress2(String dealerAddress2) {
    	this.dealerAddress2 = dealerAddress2;
    }
	
	public String getDealerAddress3() {
    	return dealerAddress3;
    }

	public void setDealerAddress3(String dealerAddress3) {
    	this.dealerAddress3 = dealerAddress3;
    }

	public String getDealerAddress4() {
    	return dealerAddress4;
    }

	public void setDealerAddress4(String dealerAddress4) {
    	this.dealerAddress4 = dealerAddress4;
    }

	public String getDealerCountry() {
    	return dealerCountry;
    }

	public void setDealerCountry(String dealerCountry) {
    	this.dealerCountry = dealerCountry;
    }

	public String getDealerCity() {
    	return dealerCity;
    }

	public void setDealerCity(String dealerCity) {
    	this.dealerCity = dealerCity;
    }

	public String getDealerProvince() {
    	return dealerProvince;
    }

	public void setDealerProvince(String dealerProvince) {
    	this.dealerProvince = dealerProvince;
    }

	public String getLovDescCountry() {
    	return lovDescCountry;
    }

	public void setLovDescCountry(String lovDescCountry) {
    	this.lovDescCountry = lovDescCountry;
    }

	public String getLovDescCity() {
    	return lovDescCity;
    }

	public void setLovDescCity(String lovDescCity) {
    	this.lovDescCity = lovDescCity;
    }

	public String getLovDescProvince() {
    	return lovDescProvince;
    }

	public void setLovDescProvince(String lovDescProvince) {
    	this.lovDescProvince = lovDescProvince;
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

	public VehicleDealer getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(VehicleDealer beforeImage){
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
	public boolean equals(VehicleDealer vehicleDealer) {
		return getId() == vehicleDealer.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof VehicleDealer) {
			VehicleDealer vehicleDealer = (VehicleDealer) obj;
			return equals(vehicleDealer);
		}
		return false;
	}
	
}
