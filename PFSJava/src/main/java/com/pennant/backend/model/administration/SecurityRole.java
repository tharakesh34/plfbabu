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
 * FileName    		:  SecurityRole.java                                                   	* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.administration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>SecurityRole table</b>.<br>
 *
 */

@XmlType(propOrder = {"roleCd","roleDesc"})
@XmlAccessorType(XmlAccessType.NONE)
public class SecurityRole extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	
	private long        roleID = Long.MIN_VALUE;
	private long        roleApp;
	
	@XmlElement
	private String       roleCd;
	@XmlElement
	private String       roleDesc;
	private String       roleCategory;
	private long         loginUsrId;
	private boolean      newRecord;
	private String       lovValue;
	private SecurityRole      befImage;
	private LoggedInUser userDetails;
	private String       loginAppCode;
	private String       lovDescRoleAppName;
	private List<SecurityGroup> lovDescAllGroups=new ArrayList<SecurityGroup>();
	private List<SecurityRoleGroups> lovDescAssignedGroups=new ArrayList<SecurityRoleGroups>();

	public SecurityRole() {
		super();
	}

	public SecurityRole(long roleId) {
		super();
		this.roleID=roleId;
	}

	public List<SecurityRoleGroups> getLovDescAssignedGroups() {
		return lovDescAssignedGroups;
	}

	public void setLovDescAssignedGroups(
			List<SecurityRoleGroups> lovDescAssignedGroups) {
		this.lovDescAssignedGroups = lovDescAssignedGroups;
	}

	public List<SecurityGroup> getLovDescAllGroups() {
		return lovDescAllGroups;
	}

	public void setLovDescAllGroups(List<SecurityGroup> lovDescAllGroups) {
		this.lovDescAllGroups = lovDescAllGroups;
	}

	public String getLovDescRoleAppName() {
		return lovDescRoleAppName;
	}

	public void setLovDescRoleAppName(String lovDescRoleAppName) {
		this.lovDescRoleAppName = lovDescRoleAppName;
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

	public SecurityRole getBefImage() {
		return befImage;
	}

	public void setBefImage(SecurityRole befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isNew() {
		//return getId() == Long.MIN_VALUE;
		return isNewRecord();
	}

	
	public void setId(long id) {
		this.roleID = id;
	}

	public long getId() {
		return roleID;
	}

	public long getRoleID() {
		return roleID;
	}

	public void setRoleID(long roleID) {
		this.roleID = roleID;
	}


	public String getRoleCd() {
		return roleCd;
	}

	public void setRoleCd(String roleCd) {
		this.roleCd = roleCd;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleCategory() {
		return roleCategory;
	}

	public void setRoleCategory(String roleCategory) {
		this.roleCategory = roleCategory;
	}

	public long getLoginUsrId() {
		return loginUsrId;
	}
		public long getLoginUsrID() {
		return loginUsrId;
	}

	public void setLoginUsrId(long loginUsrId ) {
		this.loginUsrId = loginUsrId;
	}
	
	public void setLoginUsrID(long loginUsrId) {
		this.loginUsrId = loginUsrId;
	}

	public String getLoginAppCode() {
		return loginAppCode;
	}

	public void setLoginAppCode(String appCode) {
		this.loginAppCode = appCode;
	}

	public long getRoleApp() {
		return roleApp;
	}

	public void setRoleApp(long roleApp) {
		this.roleApp = roleApp;
	}
}
