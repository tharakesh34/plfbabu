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
 * FileName    		:  SecurityGroupRights.java														*                           
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

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class SecurityGroupRights extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	private long grpRightID = Long.MIN_VALUE;
	private long grpID;
	private long rightID;
	private LoggedInUser userDetails;
	private SecurityGroupRights befImage;
	private String lovDescGrpCode;
	private String lovDescGrpDesc;
	private int lovDescRightType;
	private String lovDescRightName;

	public SecurityGroupRights() {
		super();
	}

	public SecurityGroupRights(long grpRightId) {
		super();
		this.grpRightID = grpRightId;
	}

	public String getLovDescGrpCode() {
		return lovDescGrpCode;
	}

	public void setLovDescGrpCode(String lovDescGrpCode) {
		this.lovDescGrpCode = lovDescGrpCode;
	}

	public String getLovDescGrpDesc() {
		return lovDescGrpDesc;
	}

	public void setLovDescGrpDesc(String lovDescGrpDesc) {
		this.lovDescGrpDesc = lovDescGrpDesc;
	}

	public int getLovDescRightType() {
		return lovDescRightType;
	}

	public void setLovDescRightType(int lovDescRightType) {
		this.lovDescRightType = lovDescRightType;
	}

	public String getLovDescRightName() {
		return lovDescRightName;
	}

	public void setLovDescRightName(String lovDescRightName) {
		this.lovDescRightName = lovDescRightName;
	}

	public void setId(long grpRightID) {
		this.grpRightID = grpRightID;

	}

	public long getId() {
		return grpRightID;
	}

	public long getGrpRightID() {
		return grpRightID;
	}

	public void setGrpRightID(long grpRightID) {
		this.grpRightID = grpRightID;
	}

	public long getGrpID() {
		return grpID;
	}

	public void setGrpID(long grpID) {
		this.grpID = grpID;
	}

	public long getRightID() {
		return rightID;
	}

	public void setRightID(long rightID) {
		this.rightID = rightID;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setBefImage(SecurityGroupRights befImage) {
		this.befImage = befImage;
	}

	public SecurityGroupRights getBefImage() {
		return befImage;
	}

	@Override
	public boolean isNew() {
		return false;
	}

}
