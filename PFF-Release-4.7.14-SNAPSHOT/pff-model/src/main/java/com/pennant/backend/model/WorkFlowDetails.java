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
 * FileName    		:  WorkFlowDetails.java													*                           
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  						*
 * Creation Date    :  26-04-2011															*
 *                                                                  						*
 * Modified Date    :  26-04-2011															*
 *                                                                  						*
 * Description 		:												 						*                                 
 *                                                                                          *
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


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>WorkFlowDetails table</b>.<br>
 *
 */
@XmlRootElement(name = "workFlow")
@XmlAccessorType(XmlAccessType.NONE)
public class WorkFlowDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 5638930814989470679L;

	@XmlElement
	private long workFlowDesignId = 0; // duplicate of workFlowId for API purpose.
	
	@XmlElement
	private String workFlowType;

	@XmlElement
	private String workFlowSubType;

	@XmlElement
	private String workFlowDesc;

	@XmlElement
	private String workFlowXml;

	@XmlElement
	private String workFlowRoles;

	@XmlElement
	private String firstTaskOwner;
	
	@XmlElement
	private boolean workFlowActive;

	private boolean newRecord;
	private String lovValue;
	private WorkFlowDetails befImage;
	private LoggedInUser userDetails;
	@XmlElement
	private WSReturnStatus returnStatus;

	@XmlElement
	private String jsonDesign;

	public boolean isNew() {
		return isNewRecord();
	}

	public WorkFlowDetails(){
		super();
	}
	
	public WorkFlowDetails(long id) {
		super();
		this.setId(id);
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public long getWorkFlowDesignId() {
		return workFlowDesignId;
	}

	public void setWorkFlowDesignId(long workFlowDesignId) {
		this.workFlowDesignId = workFlowDesignId;
	}

	public long getId() {
		return super.getWorkflowId();
	}
	public void setId (long id) {
		super.setWorkflowId(id);
	}

	public long getWorkFlowId() {
		return super.getWorkflowId();
	}
	public void setWorkFlowId(long workFlowId) {
		super.setWorkflowId(workFlowId);
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public String getJsonDesign() {
		return jsonDesign;
	}

	public void setJsonDesign(String jsonDesign) {
		this.jsonDesign = jsonDesign;
	}
	
	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
