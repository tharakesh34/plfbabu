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
 * FileName    		:  SecurityRoleGroups.java												*                           
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
package com.pennant.backend.model.administration;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SecurityRoleGroups extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;
	private long roleGrpID = Long.MIN_VALUE;
	private long grpID;
	private long roleID;
	private String lovDescGrpDesc;
	private String lovDescGrpCode;
	private String lovDescRoleCode;
	private LoggedInUser userDetails;
	private SecurityRoleGroups befImage;

	public SecurityRoleGroups() {
		super();
	}

	public SecurityRoleGroups(long roleGrpId) {
		super();
		this.roleGrpID = roleGrpId;
	}

	public long getRoleGrpID() {
		return roleGrpID;
	}

	public void setRoleGrpID(long roleGrpID) {
		this.roleGrpID = roleGrpID;
	}

	public long getGrpID() {
		return grpID;
	}

	public void setGrpID(long grpID) {
		this.grpID = grpID;
	}

	public long getRoleID() {
		return roleID;
	}

	public void setRoleID(long roleID) {
		this.roleID = roleID;
	}

	public String getLovDescGrpDesc() {
		return lovDescGrpDesc;
	}

	public void setLovDescGrpDesc(String lovDescGrpDesc) {
		this.lovDescGrpDesc = lovDescGrpDesc;
	}

	public String getLovDescGrpCode() {
		return lovDescGrpCode;
	}

	public void setLovDescGrpCode(String lovDescGrpCode) {
		this.lovDescGrpCode = lovDescGrpCode;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setBefImage(SecurityRoleGroups befImage) {
		this.befImage = befImage;
	}

	public SecurityRoleGroups getBefImage() {
		return befImage;
	}

	public boolean isNew() {

		return false;
	}

	public void setId(long roleGrpID) {
		this.roleGrpID = roleGrpID;

	}

	public long getId() {
		return roleGrpID;
	}

	public void setLovDescRoleCode(String lovDescRoleCode) {
		this.lovDescRoleCode = lovDescRoleCode;
	}

	public String getLovDescRoleCode() {
		return lovDescRoleCode;
	}

}
