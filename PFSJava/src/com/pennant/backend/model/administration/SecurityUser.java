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
 * FileName    		:  SecUser.java												*                           
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

import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.PennantConstants;

public class SecurityUser implements java.io.Serializable, Entity {
	
	private static final long serialVersionUID = -8443234918260997954L;
	private long      usrID = Long.MIN_VALUE;
	private int       version;
	private String    usrLogin;
	private String    usrPwd;
	private String    usrLName;
	private String    usrMName;
	private String    usrFName;
	private String    usrMobile;
	private String    usrEmail;
	private boolean   usrEnabled = true;
	private Date      usrCanSignonFrom;
	private Date      usrCanSignonTo;
	private boolean   usrCanOverrideLimits = false;
	private boolean   usrAcExp = false;
	private boolean   usrCredentialsExp = false;
	private boolean   usrAcLocked = false;
	private String    usrLanguage;
	private long      usrDftAppId;
	private String    usrBranchCode;
	private String    usrDeptCode;
	private String    usrToken;
	private boolean   usrIsMultiBranch = false;
	private int       UsrInvldLoginTries=0;
	private long      lastMntBy;
	private Timestamp lastMntOn;
	private long      workflowId = 0;
	private LoginUserDetails userDetails;
	private String    recordType;
	private String    roleCode="";
	private String    nextRoleCode= "";
	private String    taskId="";
	private String    nextTaskId= "";
	private String    userStaffID;
	private String    lovDescUsrDftAppCodeName;
	private String    lovDescUsrBranchCodeName;
	private String    lovDescUsrDeptCodeName;
	private String    lovDescUsrLanguage;
	private boolean   newRecord=false;
	private String    lovValue;
	private SecurityUser   befImage;
	private String    recordStatus;
	private String    userAction = "Save";
	private Date      UsrAcExpDt;
	private String    usrDftAppCode;
	private String    loginAppCode=PennantConstants.applicationCode;

	public boolean isNew() {
		return (getUsrID() == Long.MIN_VALUE);
	}
	
	public SecurityUser() {
	}

	public SecurityUser(long usrID) {
		this.usrID=usrID;
	}
	public SecurityUser(long usrID, String usrLogin, String usrPwd, boolean usrEnabled, Date usrCanSignonFrom,Date usrCanSignonTo,
			boolean usrCanOverrideLimits,boolean usrAcExp, boolean usrCredentialsExp, boolean usrAcLocked,String usrToken,
			boolean usrIsMultiBranch,int UsrInvldLoginTries) {
		this.setUsrID(usrID);
		this.usrLogin = usrLogin;
		this.usrPwd = usrPwd;
		this.usrEnabled = usrEnabled;
		this.usrCanSignonFrom=usrCanSignonFrom;
		this.usrCanSignonTo=usrCanSignonTo;
		this.usrCanOverrideLimits=usrCanOverrideLimits;
		this.usrAcExp = usrAcExp;
		this.usrCredentialsExp = usrCredentialsExp; 
		this.usrAcLocked = usrAcLocked;
		this.usrToken=usrToken;
		this.usrIsMultiBranch=usrIsMultiBranch;
		this.UsrInvldLoginTries=UsrInvldLoginTries;
	}
	
	public SecurityUser(long usrID, String usrLogin, String usrPwd, String usrLName, String usrMName, String usrFName,String usrMobile ,String usrEmail, boolean usrEnabled,  
			Date usrCanSignonFrom,Date usrCanSignonTo,boolean usrCanOverrideLimits,boolean usrAcExp,boolean usrCredentialsExp, 
			boolean usrAcLocked,String usrLanguage,long usrDftAppId,String usrBranchCode,String usrDeptCode,String usrToken,
			 boolean usrIsMultiBranch,int UsrInvldLoginTries,long lastMntBy,Timestamp lastMntOn) {
		this.setUsrID(usrID);
		this.usrLogin = usrLogin;
		this.usrPwd = usrPwd;
		this.usrLName = usrLName;
		this.usrMName = usrMName;
		this.usrFName = usrFName;
		this.usrMobile=usrMobile;
		this.usrEmail = usrEmail;
		this.usrEnabled = usrEnabled;
		this.usrCanSignonFrom=usrCanSignonFrom;
		this.usrCanSignonTo=usrCanSignonTo;
		this.usrCanOverrideLimits=usrCanOverrideLimits;
		this.usrAcExp = usrAcExp;
		this.usrCredentialsExp = usrCredentialsExp;
		this.usrAcLocked = usrAcLocked;
		this.usrLanguage=usrLanguage;
		this.usrDftAppId=usrDftAppId;
		this.usrBranchCode=usrBranchCode;
		this.usrDeptCode=usrDeptCode;
		this.usrToken=usrToken;
		this.usrIsMultiBranch=usrIsMultiBranch;
		this.UsrInvldLoginTries=UsrInvldLoginTries;
		this.lastMntBy=lastMntBy;
		this.lastMntOn=lastMntOn;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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

	public int getVersion() {
		return this.version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public String getUsrLogin() {
		return this.usrLogin;
	}
	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}

	public String getUsrPwd() {
		return this.usrPwd;
	}
	public void setUsrPwd(String usrPwd) {
		this.usrPwd = usrPwd;
	}

	public String getUsrLName() {
		return this.usrLName;
	}
	public void setUsrLName(String usrLName) {
		this.usrLName = usrLName;
	}

	public String getUsrMName() {
		return this.usrMName;
	}
	public void setUsrMName(String usrMName) {
		this.usrMName = usrMName;
	}

	public String getUsrFName() {
		return this.usrFName;
	}
	public void setUsrFName(String usrFName) {
		this.usrFName = usrFName;
	}

	public String getUsrMobile() {
		return usrMobile;
	}
	public void setUsrMobile(String usrMobile) {
		this.usrMobile = usrMobile;
	}

	public String getUsrEmail() {
		return this.usrEmail;
	}
	public void setUsrEmail(String usrEmail) {
		this.usrEmail = usrEmail;
	}

	public boolean isUsrEnabled() {
		return this.usrEnabled;
	}
	public void setUsrEnabled(boolean usrEnabled) {
		this.usrEnabled = usrEnabled;
	}

	public Date getUsrCanSignonFrom() {
		return usrCanSignonFrom;
	}
	public void setUsrCanSignonFrom(Date usrCanSignonFrom) {
		this.usrCanSignonFrom = usrCanSignonFrom;
	}

	public Date getUsrCanSignonTo() {
		return usrCanSignonTo;
	}
	public void setUsrCanSignonTo(Date usrCanSignonTo) {
		this.usrCanSignonTo = usrCanSignonTo;
	}

	public boolean isUsrCanOverrideLimits() {
		return usrCanOverrideLimits;
	}
	public void setUsrCanOverrideLimits(boolean usrCanOverrideLimits) {
		this.usrCanOverrideLimits = usrCanOverrideLimits;
	}

	public boolean isUsrAcExp() {
		return this.usrAcExp;
	}
	public void setUsrAcExp(boolean usrAcExp) {
		this.usrAcExp = usrAcExp;
	}

	public boolean isUsrCredentialsExp() {
		return this.usrCredentialsExp;
	}
	public void setUsrCredentialsExp(boolean usrCredentialsExp) {
		this.usrCredentialsExp = usrCredentialsExp;
	}

	public boolean isUsrAcLocked() {
		return this.usrAcLocked;
	}
	public void setUsrAcLocked(boolean usrAcLocked) {
		this.usrAcLocked = usrAcLocked;
	}

	public String getUsrToken() {
		return this.usrToken;
	}
	public void setUsrToken(String usrToken) {
		this.usrToken = usrToken;
	}
	
	public String getUsrBranchCode() {
		return usrBranchCode;
	}
	public void setUsrBranchCode(String usrBranchCode) {
		this.usrBranchCode = usrBranchCode;
	}

	public String getUsrDeptCode() {
		return usrDeptCode;
	}
	public void setUsrDeptCode(String usrDeptCode) {
		this.usrDeptCode = usrDeptCode;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(getUsrID()).hashCode();
	}
	
	public boolean isAccountNonLocked(){
		if (usrAcLocked){
			return false;
		}
		return true;	
	} 

	public boolean isAccountNonExpired(){
		if (usrAcExp){
			return false;
		}
		return true;	
	} 
	public boolean isCredentialsNonExpired(){
		if (usrCredentialsExp){
			return false;
		}
		return true;	
	}
	
	public String getUsrLanguage() {
		return usrLanguage;
	}
	public void setUsrLanguage(String usrLanguage) {
		this.usrLanguage = usrLanguage;
	}
	
	public long getUsrDftAppId() {
		return usrDftAppId;
	}
	public void setUsrDftAppId(long usrDftAppId) {
		this.usrDftAppId = usrDftAppId;
	}

	public String getUsrDftAppCode() {
		return usrDftAppCode;
	}
	public void setUsrDftAppCode(String usrDftAppCode) {
		this.usrDftAppCode = usrDftAppCode;
	}

	public boolean isUsrIsMultiBranch() {
		return usrIsMultiBranch;
	}
	public void setUsrIsMultiBranch(boolean usrIsMultiBranch) {
		this.usrIsMultiBranch = usrIsMultiBranch;
	}

	public int getUsrInvldLoginTries() {
		return UsrInvldLoginTries;
	}
	public void setUsrInvldLoginTries(int bigDecimal) {
		UsrInvldLoginTries = bigDecimal;
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

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}
	
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserStaffID() {
		return userStaffID;
	}
	public void setUserStaffID(String userStaffID) {
		this.userStaffID = userStaffID;
	}

	public String getLovDescUsrDftAppCodeName() {
		return lovDescUsrDftAppCodeName;
	}
	public void setLovDescUsrDftAppCodeName(String lovDescUsrDftAppCodeName) {
		this.lovDescUsrDftAppCodeName = lovDescUsrDftAppCodeName;
	}

	public String getLovDescUsrBranchCodeName() {
		return lovDescUsrBranchCodeName;
	}
	public void setLovDescUsrBranchCodeName(String lovDescUsrBranchCodeName) {
		this.lovDescUsrBranchCodeName = lovDescUsrBranchCodeName;
	}

	public String getLovDescUsrDeptCodeName() {
		return lovDescUsrDeptCodeName;
	}
	public void setLovDescUsrDeptCodeName(String lovDescUsrDeptCodeName) {
		this.lovDescUsrDeptCodeName = lovDescUsrDeptCodeName;
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

	public SecurityUser getBefImage() {
		return befImage;
	}
	public void setBefImage(SecurityUser befImage) {
		this.befImage = befImage;
	}

	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	
	public void setLoginAppCode(String loginAppCode) {
		this.loginAppCode = loginAppCode;
	}
	public String getLoginAppCode() {
		return loginAppCode;
	}

	public Date getUsrAcExpDt() {
		return UsrAcExpDt;
	}
	public void setUsrAcExpDt(Date usrAcExpDt) {
		UsrAcExpDt = usrAcExpDt;
	}

	public void setLovDescUsrLanguage(String lovDescUsrLanguage) {
		this.lovDescUsrLanguage = lovDescUsrLanguage;
	}
	public String getLovDescUsrLanguage() {
		return lovDescUsrLanguage;
	}	
	
	// Overridden Equals method to handle the comparison
	public boolean equals(SecurityUser secUser) {
		return getUsrID() == secUser.getUsrID();
	}
	
	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SecurityUser) {
			SecurityUser secUser = (SecurityUser) obj;
			return equals(secUser);
		}
		return false;
	}
}
