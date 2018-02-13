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

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Authorization table</b>.<br>
 *
 */
public class Authorization extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	
	private long authUserId = Long.MIN_VALUE;
	private String authUserIdName;
	private String authType;
	private String authTypeName;
	private String authName;
	private String authDept;
	private String authDeptName;
	private String authDesig;
	private String authDesigName;
	private String authSignature;
	private boolean newRecord;
	private String lovValue;
	private Authorization befImage;
	private LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public Authorization() {
		super();
	}

	public Authorization(long id) {
		super();
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return authUserId;
	}
	
	public void setId (long id) {
		this.authUserId = id;
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

	public Authorization getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(Authorization beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
