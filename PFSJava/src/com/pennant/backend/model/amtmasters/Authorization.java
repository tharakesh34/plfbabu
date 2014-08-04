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
 * FileName    		:  Authorization.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2013    														*
 *                                                                  						*
 * Modified Date    :  20-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2013       Pennant	                 0.1                                            * 
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
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Authorization table</b>.<br>
 *
 */
public class Authorization implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private long authorizedId = Long.MIN_VALUE;
	private long authUserId;
	private String authUserIdName;
	private String authType;
	private String authTypeName;
	private String authName;
	private String authDept;
	private String authDeptName;
	private String authDesig;
	private String authDesigName;
	private String authSignature;
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
	private Authorization befImage;
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

	public Authorization() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Authorization");
	}

	public Authorization(long id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("authUserIdName");
			excludeFields.add("authTypeName");
			excludeFields.add("authDeptName");
			excludeFields.add("authDesigName");
	return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		// ++++++++++++++++++ getter / setter +++++++++++++++++++//
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public long getId() {
		return authorizedId;
	}
	
	public void setId (long id) {
		this.authorizedId = id;
	}
	
	public long getAuthorizedId() {
		return authorizedId;
	}
	public void setAuthorizedId(long authorizedId) {
		this.authorizedId = authorizedId;
	}
	
	
		
	
	public long getAuthUserId() {
		return authUserId;
	}
	public void setAuthUserId(long authUserId) {
		this.authUserId = authUserId;
	}
	
	public String getAuthUserIdName() {
		return this.authUserIdName;
	}

	public void setAuthUserIdName (String authUserIdName) {
		this.authUserIdName = authUserIdName;
	}
	
		
	
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	
	public String getAuthTypeName() {
		return this.authTypeName;
	}

	public void setAuthTypeName (String authTypeName) {
		this.authTypeName = authTypeName;
	}
	
		
	
	public String getAuthName() {
		return authName;
	}
	public void setAuthName(String authName) {
		this.authName = authName;
	}
	
	
		
	
	public String getAuthDept() {
		return authDept;
	}
	public void setAuthDept(String authDept) {
		this.authDept = authDept;
	}
	
	public String getAuthDeptName() {
		return this.authDeptName;
	}

	public void setAuthDeptName (String authDeptName) {
		this.authDeptName = authDeptName;
	}
	
		
	
	public String getAuthDesig() {
		return authDesig;
	}
	public void setAuthDesig(String authDesig) {
		this.authDesig = authDesig;
	}
	
	public String getAuthDesigName() {
		return this.authDesigName;
	}

	public void setAuthDesigName (String authDesigName) {
		this.authDesigName = authDesigName;
	}
	
		
	
	public String getAuthSignature() {
		return authSignature;
	}
	public void setAuthSignature(String authSignature) {
		this.authSignature = authSignature;
	}
	
	
		
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
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
	public Authorization getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(Authorization beforeImage){
		this.befImage=beforeImage;
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

	// Overidden Equals method to handle the comparision
	public boolean equals(Authorization authorization) {
		return getId() == authorization.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Authorization) {
			Authorization authorization = (Authorization) obj;
			return equals(authorization);
		}
		return false;
	}
}
