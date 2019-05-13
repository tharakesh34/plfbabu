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
 * FileName    		:  SecurityGroup.java                                                   * 	  
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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>SecurityGroup table</b>.<br>
 * 
 */
public class SecurityGroup extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long grpID = Long.MIN_VALUE;
	private String grpCode;
	private String grpDesc;
	private boolean newRecord;
	private String lovValue;
	private SecurityGroup befImage;
	private LoggedInUser userDetails;
	private List<SecurityGroupRights> lovDescAssignedRights = new ArrayList<SecurityGroupRights>();

	public SecurityGroup() {
		super();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public SecurityGroup(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return grpID;
	}

	public void setId(long id) {
		this.grpID = id;
	}

	public long getGrpID() {
		return grpID;
	}

	public void setGrpID(long grpID) {
		this.grpID = grpID;
	}

	public String getGrpCode() {
		return grpCode;
	}

	public void setGrpCode(String grpCode) {
		this.grpCode = grpCode;
	}

	public String getGrpDesc() {
		return grpDesc;
	}

	public void setGrpDesc(String grpDesc) {
		this.grpDesc = grpDesc;
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

	public SecurityGroup getBefImage() {
		return this.befImage;
	}

	public void setBefImage(SecurityGroup beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLovDescAssignedRights(List<SecurityGroupRights> lovDescAssignedRights) {
		this.lovDescAssignedRights = lovDescAssignedRights;
	}

	public List<SecurityGroupRights> getLovDescAssignedRights() {
		return lovDescAssignedRights;
	}
}
