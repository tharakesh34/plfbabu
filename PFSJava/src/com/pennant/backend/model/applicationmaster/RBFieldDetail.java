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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  Country.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  08-03-2011    
 *                                                                  
 * Modified Date    :  08-03-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-03-2011       PENNANT TECHONOLOGIES	                 0.1                                         * 
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

package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.systemmasters.City;


/**
 * Model class for the <b>Country table</b>.<br>
 *
 */

public class RBFieldDetail implements java.io.Serializable {

	private static final long serialVersionUID = 1073404971137750236L;
	private String entityCode;
	private String rbModule;
	private String rbFldName;
	private String rbFldDesc;
	private String rbFldType;
	private int	   rbFldLen;
	private String rbFldTableName;
	private String rbStFlds;
	private String moduleCode;

	private int version;
	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	@XmlTransient
	private Timestamp lastMntOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private LoginUserDetails userDetails;
	@XmlTransient
	private String recordStatus;
	@XmlTransient
	private String roleCode="";
	@XmlTransient
	private String nextRoleCode= "";
	@XmlTransient
	private String taskId="";
	@XmlTransient
	private String nextTaskId= "";
	@XmlTransient
	private String recordType;
	@XmlTransient
	private String userAction = "Save";
	@XmlTransient
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}


	public RBFieldDetail(String entityCode) {
		super();
		this.entityCode= entityCode;
	}

	public RBFieldDetail() {
		super();
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("countryCodeName");
		excludeFields.add("provinceCodeName");
		excludeFields.add("lastMaintainedOn");
		excludeFields.add("lastMaintainedUser");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return rbFldName;
	}

	public void setId (String id) {
		this.rbFldName = id;
	}
 
	public String getRbModule() {
		return rbModule;
	}


	public void setRbModule(String rbModule) {
		this.rbModule = rbModule;
	}


	public String getRbFldName() {
		return rbFldName;
	}


	public void setRbFldName(String rbFldName) {
		this.rbFldName = rbFldName;
	}


	public String getRbFldDesc() {
		return rbFldDesc;
	}


	public void setRbFldDesc(String rbFldDesc) {
		this.rbFldDesc = rbFldDesc;
	}


	public String getRbFldType() {
		return rbFldType;
	}


	public void setRbFldType(String rbFldType) {
		this.rbFldType = rbFldType;
	}


	public int getRbFldLen() {
		return rbFldLen;
	}


	public void setRbFldLen(int rbFldLen) {
		this.rbFldLen = rbFldLen;
	}


	public String getRbFldTableName() {
		return rbFldTableName;
	}


	public void setRbFldTableName(String rbFldTableName) {
		this.rbFldTableName = rbFldTableName;
	}


	public String getRbStFlds() {
		return rbStFlds;
	}


	public void setRbStFlds(String rbStFlds) {
		this.rbStFlds = rbStFlds;
	}


	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}


	@XmlTransient
	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}

	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}

	@XmlTransient
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMaintainedOn(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			lastMntOn = DateUtility.ConvertFromXMLTime(xmlCalendar);
			lastMaintainedOn = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getLastMaintainedOn()
	throws DatatypeConfigurationException {

		if (lastMntOn == null) {
			return null;
		}
		return DateUtility.getXMLDate(lastMntOn);
	}


	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
	}

	@XmlTransient
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	@XmlTransient
	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	@XmlTransient
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	@XmlTransient
	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	@XmlTransient
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@XmlTransient
	public String getNextTaskId() {
		return nextTaskId;
	}


	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	@XmlTransient
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	@XmlTransient
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
	public String getModuleCode() {
		return moduleCode;
	}


	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	// Overidden Equals method to handle the comparision
	public boolean equals(City city) {
		return getId() == city.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof City) {
			City city = (City) obj;
			return equals(city);
		}
		return false;
	}
}


