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
 * FileName    		:  SecurityOperation.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014       Pennant	                 0.1                                            * 
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

import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>SecurityOperation table</b>.<br>
 *
 */
public class SecurityOperation extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	private long oprID = Long.MIN_VALUE;
	private String oprCode;
	private String oprDesc;
	private boolean newRecord=false;
	private String lovValue;
	private SecurityOperation befImage;
	private LoggedInUser userDetails;
	private List<SecurityOperationRoles> securityOperationRolesList;
	
	public SecurityOperation() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("SecurityOperation"));
	}

	public SecurityOperation(long id) {
		super();
		this.setId(id);
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public boolean isNew() {
		return isNewRecord();
	}
	
	public long getId() {
		return oprID;
	}
	
	public void setId (long id) {
		this.oprID = id;
	}
	
	public long getOprID() {
		return oprID;
	}

	public void setOprID(long oprID) {
		this.oprID = oprID;
	}
	
	public String getOprCode() {
		return oprCode;
	}

	public void setOprCode(String oprCode) {
		this.oprCode = oprCode;
	}

	public String getOprDesc() {
		return oprDesc;
	}

	public void setOprDesc(String oprDesc) {
		this.oprDesc = oprDesc;
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

	public SecurityOperation getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(SecurityOperation beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<SecurityOperationRoles> getSecurityOperationRolesList() {
		return securityOperationRolesList;
	}

	public void setSecurityOperationRolesList(
			List<SecurityOperationRoles> securityOperationRolesList) {
		this.securityOperationRolesList = securityOperationRolesList;
	}
}
