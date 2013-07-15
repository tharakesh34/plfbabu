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
 * FileName    		:  WorkFlowDetails.java													*                           
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

import java.sql.Timestamp;

/**
 * Model class for the <b>WorkFlowDetails table</b>.<br>
 *
 */
public class WorkFlowDetails implements java.io.Serializable,Entity {
	
	private static final long serialVersionUID = 5638930814989470679L;
	
	private long workFlowId = Long.MIN_VALUE;
	private String workFlowType;
	private String workFlowSubType;
	private String workFlowDesc;
	private String workFlowXml;
	private String workFlowRoles;
	private String firstTaskOwner;
	private boolean workFlowActive;;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private WorkFlowDetails befImage;
	private LoginUserDetails userDetails;
	private String jsonDesign;

	public boolean isNew() {
		return isNewRecord();
	}

	public WorkFlowDetails(){
		
	}
	
	public WorkFlowDetails(long id) {
		this.setId(id);
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public long getId() {
		return workFlowId;
	}
	public void setId (long id) {
		this.workFlowId= id;
	}

	public long getWorkFlowId() {
		return workFlowId;
	}
	public void setWorkFlowId(long workFlowId) {
		this.workFlowId = workFlowId;
	}

	public String getWorkFlowType() {
		return workFlowType;
	}
	public void setWorkFlowType(String workFlowType) {
		this.workFlowType = workFlowType;
	}

	public String getWorkFlowSubType() {
		return workFlowSubType;
	}
	public void setWorkFlowSubType(String workFlowSubType) {
		this.workFlowSubType = workFlowSubType;
	}

	public String getWorkFlowDesc() {
		return workFlowDesc;
	}
	public void setWorkFlowDesc(String workFlowDesc) {
		this.workFlowDesc = workFlowDesc;
	}

	public String getWorkFlowXml() {
		return workFlowXml;
	}
	public void setWorkFlowXml(String workFlowXml) {
		this.workFlowXml = workFlowXml;
	}

	public String getWorkFlowRoles() {
		return workFlowRoles;
	}
	public void setWorkFlowRoles(String workFlowRoles) {
		this.workFlowRoles = workFlowRoles;
	}

	public String[] getFlowRoles() {
		if (workFlowRoles==null){
			return null;
		}
		return workFlowRoles.split(",");
	}

	public String getFirstTaskOwner() {
		return firstTaskOwner;
	}
	public void setFirstTaskOwner(String firstTaskOwner) {
		this.firstTaskOwner = firstTaskOwner;
	}
	
	public boolean isWorkFlowActive() {
		return workFlowActive;
	}
	public void setWorkFlowActive(boolean workFlowActive) {
		this.workFlowActive = workFlowActive;
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

	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public WorkFlowDetails getBefImage() {
		return befImage;
	}
	public void setBefImage(WorkFlowDetails befImage) {
		this.befImage = befImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}
	
	public String getJsonDesign() {
		return jsonDesign;
	}

	public void setJsonDesign(String jsonDesign) {
		this.jsonDesign = jsonDesign;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(WorkFlowDetails workFlowDetails) {
		return getId() == workFlowDetails.getId();
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof WorkFlowDetails) {
			WorkFlowDetails workFlowDetails= (WorkFlowDetails) obj;
			return equals(workFlowDetails);
		}
		return false;
	}
	
}
