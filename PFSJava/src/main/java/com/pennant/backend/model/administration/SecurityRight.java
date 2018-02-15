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
 * FileName    		:  SEcRight.java														*                           
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

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SecurityRight extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = -1574628715506591010L;

	private long           	rightID = Long.MIN_VALUE;
	private Integer        	rightType;
	private String         	rightName;
	private String         	page;
	private int            	accessType;
	private long 			loginAppId;
	private String         	loginAppCode;
	private long           	loginUsrId;
	private String         	loginGrpCode;
	private String         	loginRoleCd;
	private boolean        	newRecord = false;
	private String         	lovValue;
	private SecurityRight  	befImage;
	private LoggedInUser 	userDetails;
	private String         	menuRight;
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("loginAppId");
		excludeFields.add("menuRight");
		return excludeFields;
	}


	public boolean isNew() {
		return getId() == Long.MIN_VALUE;
	}

	public SecurityRight() {
		super();
	}

	public SecurityRight(long rightId){
		super();
		this.rightID=rightId;
	}
	
	public SecurityRight(long rightID, String rightName) {
		super();
		this.setId(rightID);
		this.rightName = rightName;
	}

	public SecurityRight(long rightID, Integer rightType, String rightName) {
		super();
		this.setRightID(rightID);
		this.rightType = rightType;
		this.rightName = rightName;
	}

	public void setId(long id) {
		this.rightID = id;
	}

	public long getId() {
		return rightID;
	}

	public long getRightID() {
		return rightID;
	}

	public void setRightID(long rightID) {
		this.rightID = rightID;
	}

	public Integer getRightType() {
		return rightType;
	}

	public void setRightType(Integer rightType) {
		this.rightType = rightType;
	}

	public String getRightName() {
		return rightName;
	}

	public void setRightName(String rightName) {
		this.rightName = rightName;
	}

	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}

	public long getLoginAppId() {
		return loginAppId;
	}

	public void setLoginAppId(long loginAppId) {
		this.loginAppId = loginAppId;
	}

	public String getLoginAppCode() {
		return loginAppCode;
	}

	public void setLoginAppCode(String loginAppCode) {
		this.loginAppCode = loginAppCode;
	}

	public long getLoginUsrId() {
		return loginUsrId;
	}

	public long getUsrID() {
		return loginUsrId;
	}

	public void setLoginUsrId(long loginUsrId) {
		this.loginUsrId = loginUsrId;
	}

	public void setUsrID(long usrID) {
		this.loginUsrId = usrID;
	}

	public String getLoginGrpCode() {
		return loginGrpCode;
	}

	public String getGrpCode() {
		return loginGrpCode;
	}

	public void setLoginGrpCode(String loginGrpCode) {
		this.loginGrpCode = loginGrpCode;
	}

	public void setGrpCode(String grpCode) {
		this.loginGrpCode = grpCode;
	}

	public String getLoginRoleCd() {
		return loginRoleCd;
	}

	public String getRoleCd() {
		return loginRoleCd;
	}

	public void setLoginRoleCd(String loginRoleCd) {
		this.loginRoleCd = loginRoleCd;
	}

	public void setRoleCd(String roleCd) {
		this.loginRoleCd = roleCd;
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

	public SecurityRight getBefImage() {
		return befImage;
	}

	public void setBefImage(SecurityRight befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}

	public int getAccessType() {
		return accessType;
	}


	public String getMenuRight() {
		return menuRight;
	}


	public void setMenuRight(String menuRight) {
		this.menuRight = menuRight;
	}

}
