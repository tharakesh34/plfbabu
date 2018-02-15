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
 * FileName    		:  SecurityUserDivBranch.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>SecurityUserDivBranch table</b>.<br>
 *
 */
public class SecurityUserDivBranch extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -306657295035931426L;

	private long  usrID = Long.MIN_VALUE;
	private String    userDivision;
	private String    userBranch;
	private String    userBranchDesc;
	private boolean newRecord=false;
	private String lovValue;
	private String lovDescPriKey;
	private SecurityUserDivBranch befImage;
	private String branchSwiftBrnCde;
	private LoggedInUser userDetails;

	public SecurityUserDivBranch() {
		super();
	}
	
	public boolean isNew() {
		return getUsrID() == Long.MIN_VALUE;	
	}

	public SecurityUserDivBranch(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("userBranchDesc");
		excludeFields.add("branchSwiftBrnCde");
		return excludeFields;
	}

	public void setId(long id) {
		this.usrID = id;
	}
	public long getId() {
		return usrID;
	}

	public void setUsrID(long usrID) {
		this.usrID = usrID;
	}
	public long getUsrID() {
		return usrID;
	}
	public String getUserDivision() {
		return userDivision;
	}
	public void setUserDivision(String userDivision) {
		this.userDivision = userDivision;
	}
	public String getUserBranch() {
		return userBranch;
	}
	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}
	public void setUserBranchDesc(String userBranchDesc) {
	    this.userBranchDesc = userBranchDesc;
    }

	public String getUserBranchDesc() {
	    return userBranchDesc;
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

	public SecurityUserDivBranch getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(SecurityUserDivBranch beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescPriKey() {
	    return lovDescPriKey;
    }

	public void setLovDescPriKey(String lovDescPriKey) {
	    this.lovDescPriKey = lovDescPriKey;
    }

	public String getBranchSwiftBrnCde() {
		return branchSwiftBrnCde;
	}

	public void setBranchSwiftBrnCde(String branchSwiftBrnCde) {
		this.branchSwiftBrnCde = branchSwiftBrnCde;
	}
}
