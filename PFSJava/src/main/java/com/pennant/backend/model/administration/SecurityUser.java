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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.security.UserType;

public class SecurityUser extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -8443234918260997954L;

	private long usrID = Long.MIN_VALUE;
	private String usrLogin;
	private String usrPwd;
	private String usrLName;
	private String usrMName;
	private String usrFName;
	private String usrMobile;
	private String usrEmail;
	private boolean usrEnabled = true;
	private Date usrCanSignonFrom;
	private Date usrCanSignonTo;
	private boolean usrCanOverrideLimits;
	private boolean usrAcExp;
	private boolean usrAcLocked;
	private String usrLanguage;
	private long usrDftAppId;
	private String usrBranchCode;
	private String usrDeptCode;
	private String usrToken;
	private boolean usrIsMultiBranch;
	private int usrInvldLoginTries;
	private LoggedInUser userDetails;
	private String userStaffID;
	private String lovDescUsrDftAppCodeName;
	private String lovDescUsrBranchCodeName;
	private String lovDescUsrDeptCodeName;
	private String lovDescUsrLanguage;
	private boolean newRecord;
	private String lovValue;
	private SecurityUser befImage;
	private Date usrAcExpDt;
	private Date pwdExpDt;
	private String usrDftAppCode;
	private String loginAppCode = App.CODE;
	private long loginAppId = App.ID;
	private Timestamp lastLoginOn;
	private Timestamp lastFailLoginOn;
	private String usrDesg;
	private String lovDescUsrDesg;
	private String authType;
	private String userType = UserType.USER.name();
	private List<SecurityUserDivBranch> securityUserDivBranchList;
	private List<SecurityUserOperations> securityUserOperationsList;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("loginAppId");
		excludeFields.add("lastLoginOn");
		excludeFields.add("lastFailLoginOn");
		excludeFields.add("userType");
		return excludeFields;
	}

	public boolean isNew() {
		return getUsrID() == Long.MIN_VALUE;
	}

	public SecurityUser() {
		super();
	}

	public SecurityUser(long usrID) {
		super();
		this.usrID = usrID;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public boolean isAccountNonLocked() {
		return !usrAcLocked;
	}

	public boolean isAccountNonExpired() {
		return !usrAcExp;
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
		return usrInvldLoginTries;
	}

	public void setUsrInvldLoginTries(int bigDecimal) {
		usrInvldLoginTries = bigDecimal;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public void setLoginAppCode(String loginAppCode) {
		this.loginAppCode = loginAppCode;
	}

	public String getLoginAppCode() {
		return loginAppCode;
	}

	public long getLoginAppId() {
		return loginAppId;
	}

	public void setLoginAppId(long loginAppId) {
		this.loginAppId = loginAppId;
	}

	public Date getUsrAcExpDt() {
		return usrAcExpDt;
	}

	public void setUsrAcExpDt(Date usrAcExpDt) {
		this.usrAcExpDt = usrAcExpDt;
	}

	public void setLovDescUsrLanguage(String lovDescUsrLanguage) {
		this.lovDescUsrLanguage = lovDescUsrLanguage;
	}

	public String getLovDescUsrLanguage() {
		return lovDescUsrLanguage;
	}

	public void setUsrDesg(String usrDesg) {
		this.usrDesg = usrDesg;
	}

	public String getUsrDesg() {
		return usrDesg;
	}

	public String getLovDescUsrDesg() {
		return lovDescUsrDesg;
	}

	public void setLovDescUsrDesg(String lovDescUsrDesg) {
		this.lovDescUsrDesg = lovDescUsrDesg;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public List<SecurityUserDivBranch> getSecurityUserDivBranchList() {
		return securityUserDivBranchList;
	}

	public void setSecurityUserDivBranchList(List<SecurityUserDivBranch> securityUserDivBranchList) {
		this.securityUserDivBranchList = securityUserDivBranchList;
	}

	public List<SecurityUserOperations> getSecurityUserOperationsList() {
		return securityUserOperationsList;
	}

	public void setSecurityUserOperationsList(List<SecurityUserOperations> securityUserOperationsList) {
		this.securityUserOperationsList = securityUserOperationsList;
	}

	public Timestamp getLastLoginOn() {
		return lastLoginOn;
	}

	public void setLastLoginOn(Timestamp lastLoginOn) {
		this.lastLoginOn = lastLoginOn;
	}

	public Timestamp getLastFailLoginOn() {
		return lastFailLoginOn;
	}

	public void setLastFailLoginOn(Timestamp lastFailLoginOn) {
		this.lastFailLoginOn = lastFailLoginOn;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Date getPwdExpDt() {
		return pwdExpDt;
	}

	public void setPwdExpDt(Date pwdExpDt) {
		this.pwdExpDt = pwdExpDt;
	}

}
