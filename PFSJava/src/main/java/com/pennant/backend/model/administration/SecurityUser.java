/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : SecUser.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.administration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.security.UserType;
import com.pennanttech.pff.core.RequestSource;

@XmlType(propOrder = { "usrID", "usrLogin" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "securityUser")

public class SecurityUser extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -8443234918260997954L;

	@XmlElement
	private long usrID = Long.MIN_VALUE;
	@XmlElement
	private String usrLogin;
	@XmlElement
	private String usrPwd;
	private String usrRawPwd;
	@XmlElement
	private String usrLName;
	@XmlElement
	private String usrMName;
	@XmlElement
	private String usrFName;
	@XmlElement
	private String usrMobile;
	@XmlElement
	private String usrEmail;
	@XmlElement
	private boolean usrEnabled = true;
	@XmlElement
	private Date usrCanSignonFrom;
	@XmlElement
	private Date usrCanSignonTo;
	@XmlElement
	private boolean usrCanOverrideLimits;
	@XmlElement
	private boolean usrAcExp;
	@XmlElement
	private boolean usrAcLocked;
	@XmlElement
	private String usrLanguage;
	@XmlElement
	private long usrDftAppId;
	@XmlElement
	private String usrBranchCode;
	@XmlElement
	private String usrDeptCode;
	@XmlElement
	private String usrToken;
	@XmlElement
	private boolean usrIsMultiBranch;
	private int usrInvldLoginTries;
	private LoggedInUser userDetails;
	@XmlElement
	private String userStaffID;
	private String lovDescUsrDftAppCodeName;
	private String lovDescUsrBranchCodeName;
	private String lovDescUsrDeptCodeName;
	private String lovDescUsrLanguage;
	private String lovValue;
	private SecurityUser befImage;
	@XmlElement
	private Date usrAcExpDt;
	private Date pwdExpDt;
	private String usrDftAppCode;
	private String loginAppCode = App.CODE;
	private long loginAppId = App.ID;
	private Timestamp lastLoginOn;
	private Timestamp lastFailLoginOn;
	@XmlElement
	private String usrDesg;
	private String lovDescUsrDesg;
	@XmlElement(name = "userType")
	private String authType;
	@XmlElement
	private Long businessVertical;
	private String businessVerticalCode;
	private String businessVerticalDesc;
	@XmlElement 
	private String ldapDomainName;
	private String userType = UserType.USER.name();
	@XmlElement
	private List<SecurityUserDivBranch> securityUserDivBranchList = new ArrayList<>();
	@XmlElement
	private List<SecurityUserOperations> securityUserOperationsList;
	private List<ReportingManager> reportingManagersList;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private String lovDescFirstName;

	private Date accountLockedOn;
	private Date accountUnLockedOn;

	private boolean accessToAllBranches;
	private Collection<SecurityRight> menuRights = new ArrayList<>();
	private List<SecurityRole> roles = new ArrayList<>();
	private boolean deleted;
	@XmlElement
	private String disableReason;
	@XmlElement
	private String employeeType;
	private Timestamp createdOn;
	private Long createdBy;
	private Timestamp approvedOn;
	private Long approvedBy;
	@XmlElement
	private WSReturnStatus returnStatus;
	private String sourceId;
	private String status;
	@XmlElement
	private String reason;
	@XmlElement(name = "usrConfrmPwd")
	private String confirmPassword;
	private RequestSource requestSource;
	@XmlElement
	private String baseLocation;
	@XmlElement
	private boolean notifyUser;
	@XmlElement
	private String mode;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("auditDetailMap");
		excludeFields.add("loginAppId");
		excludeFields.add("lastLoginOn");
		excludeFields.add("lastFailLoginOn");
		excludeFields.add("userType");
		excludeFields.add("businessVerticalCode");
		excludeFields.add("businessVerticalDesc");
		excludeFields.add("lovDescFirstName");
		excludeFields.add("menuRights");
		excludeFields.add("roles");
		excludeFields.add("returnStatus");
		excludeFields.add("sourceId");
		excludeFields.add("status");
		excludeFields.add("reason");
		excludeFields.add("confirmPassword");
		excludeFields.add("requestSource");
		excludeFields.add("notifyUser");
		excludeFields.add("mode");
		excludeFields.add("usrRawPwd");

		return excludeFields;
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

	public String getUsrRawPwd() {
		return usrRawPwd;
	}

	public void setUsrRawPwd(String usrRawPwd) {
		this.usrRawPwd = usrRawPwd;
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

	public List<ReportingManager> getReportingManagersList() {
		return reportingManagersList;
	}

	public void setReportingManagersList(List<ReportingManager> reportingManagersList) {
		this.reportingManagersList = reportingManagersList;
	}

	public Long getBusinessVertical() {
		return businessVertical;
	}

	public void setBusinessVertical(Long businessVertical) {
		this.businessVertical = businessVertical;
	}

	public String getBusinessVerticalCode() {
		return businessVerticalCode;
	}

	public void setBusinessVerticalCode(String businessVerticalCode) {
		this.businessVerticalCode = businessVerticalCode;
	}

	public String getBusinessVerticalDesc() {
		return businessVerticalDesc;
	}

	public void setBusinessVerticalDesc(String businessVerticalDesc) {
		this.businessVerticalDesc = businessVerticalDesc;
	}

	public String getldapDomainName() {
		return ldapDomainName;
	}

	public void setldapDomainName(String ldapDomainName) {
		this.ldapDomainName = ldapDomainName;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getLovDescFirstName() {
		return lovDescFirstName;
	}

	public void setLovDescFirstName(String lovDescFirstName) {
		this.lovDescFirstName = lovDescFirstName;
	}

	public boolean isAccessToAllBranches() {
		return accessToAllBranches;
	}

	public void setAccessToAllBranches(boolean accessToAllBranches) {
		this.accessToAllBranches = accessToAllBranches;
	}

	public Date getAccountLockedOn() {
		return accountLockedOn;
	}

	public void setAccountLockedOn(Date accountLockedOn) {
		this.accountLockedOn = accountLockedOn;
	}

	public Date getAccountUnLockedOn() {
		return accountUnLockedOn;
	}

	public void setAccountUnLockedOn(Date accountUnLockedOn) {
		this.accountUnLockedOn = accountUnLockedOn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public Collection<SecurityRight> getMenuRights() {
		return menuRights;
	}

	public void setMenuRights(Collection<SecurityRight> menuRights) {
		this.menuRights = menuRights;
	}

	public List<SecurityRole> getRoles() {
		return roles;
	}

	public void setRoles(List<SecurityRole> roles) {
		this.roles = roles;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getDisableReason() {
		return disableReason;
	}

	public void setDisableReason(String disableReason) {
		this.disableReason = disableReason;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public RequestSource getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(RequestSource requestSource) {
		this.requestSource = requestSource;
	}

	public String getLdapDomainName() {
		return ldapDomainName;
	}

	public void setLdapDomainName(String ldapDomainName) {
		this.ldapDomainName = ldapDomainName;
	}

	public String getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	public boolean isNotifyUser() {
		return notifyUser;
	}

	public void setNotifyUser(boolean notifyUser) {
		this.notifyUser = notifyUser;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
