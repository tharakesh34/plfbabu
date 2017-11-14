package com.pennant.backend.model.rmtmasters;

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
 * FileName    		:  FinTypeAccounting.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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


import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Collateral table</b>.<br>
 *
 */
public class FinTypeAccounting extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String finType = null;
	private String event;
	private String eventDesc;
	private long accountSetID = Long.MIN_VALUE;
	private String lovDescAccountingName;
	private String lovDescEventAccountingName;
	private boolean mandatory=false;
	private boolean newRecord=false;
	private String lovValue;
	private FinTypeAccounting befImage;
	
	private int moduleId;
	
	@XmlTransient
	private LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinTypeAccounting() {
		super();
	}

	public FinTypeAccounting(String id) {
		super();
		this.setId(id);
	}
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("eventDesc");
		excludeFields.add("lovDescAccountingName");
		excludeFields.add("lovDescEventAccountingName");
		excludeFields.add("mandatory");
		return excludeFields;
	}
	//Getter and Setter methods
	
	public String getId() {
		return finType;
	}
	public void setId (String id) {
		this.finType = id;
	}
	
	public String getFinType() {
    	return finType;
    }
	public void setFinType(String finType) {
    	this.finType = finType;
    }

	public String getEvent() {
    	return event;
    }
	public void setEvent(String event) {
    	this.event = event;
    }

	public String getEventDesc() {
		return eventDesc;
	}
	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public long getAccountSetID() {
		return accountSetID;
	}
	public void setAccountSetID(long accountSetID) {
		this.accountSetID = accountSetID;
	}

	public String getLovDescAccountingName() {
		return lovDescAccountingName;
	}
	public void setLovDescAccountingName(String lovDescAccountingName) {
		this.lovDescAccountingName = lovDescAccountingName;
	}

	public String getLovDescEventAccountingName() {
		return lovDescEventAccountingName;
	}
	public void setLovDescEventAccountingName(String lovDescEventAccountingName) {
		this.lovDescEventAccountingName = lovDescEventAccountingName;
	}

	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
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

	public FinTypeAccounting getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinTypeAccounting beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

}

