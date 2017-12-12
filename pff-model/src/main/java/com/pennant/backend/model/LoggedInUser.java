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
 * FileName    		:  LoggedInUser.java												*                           
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
package com.pennant.backend.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class LoggedInUser implements Serializable {
	private static final long serialVersionUID = -4283625577333258727L;

	private long userId; 
	private String userName;
	private String staffId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String mobileNo;
	private String emailId;
	private String language; 
	private String branchCode;
	private String departmentCode;
	private Date logonFromTime;
	private Date logonToTime;
	private Date accountExpiredOn;
	private Date prevPassLogonTime;
	private Date prevFailLogonTime;
	private int failAttempts;
	private String sessionId;
	private String ipAddress;
	private String browserType;
	private Timestamp logonTime;
	private long loginLogId;
	private String authType;

	public LoggedInUser() {
		super();
	}

	public long getUserId() {
		return userId;
	}

	public void setLoginUsrID(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		StringBuilder fullName = new StringBuilder();
		fullName.append(firstName);

		if (StringUtils.isNotEmpty(middleName)) {
			fullName.append(' ');
			fullName.append(middleName);
		}

		if (StringUtils.isNotEmpty(lastName)) {
			fullName.append(' ');
			fullName.append(lastName);
		}

		return fullName.toString();
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getLanguage() {
		return language;
	}

	public void setUsrLanguage(String language) {
		this.language = language;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public Date getLogonFromTime() {
		return logonFromTime;
	}

	public void setLogonFromTime(Date logonFromTime) {
		this.logonFromTime = logonFromTime;
	}

	public Date getLogonToTime() {
		return logonToTime;
	}

	public void setLogonToTime(Date logonToTime) {
		this.logonToTime = logonToTime;
	}

	public Date getAccountExpiredOn() {
		return accountExpiredOn;
	}

	public void setAccountExpiredOn(Date accountExpiredOn) {
		this.accountExpiredOn = accountExpiredOn;
	}

	public Date getPrevPassLogonTime() {
		return prevPassLogonTime;
	}

	public void setPrevPassLogonTime(Date prevPassLogonTime) {
		this.prevPassLogonTime = prevPassLogonTime;
	}

	public Date getPrevFailLogonTime() {
		return prevFailLogonTime;
	}

	public void setPrevFailLogonTime(Date prevFailLogonTime) {
		this.prevFailLogonTime = prevFailLogonTime;
	}

	public int getFailAttempts() {
		return failAttempts;
	}

	public void setFailAttempts(int failAttempts) {
		this.failAttempts = failAttempts;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getBrowserType() {
		return browserType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}

	public Timestamp getLogonTime() {
		return logonTime;
	}

	public void setLogonTime(Timestamp logonTime) {
		this.logonTime = logonTime;
	}

	public long getLoginLogId() {
		return loginLogId;
	}

	public void setLoginLogId(long loginLogId) {
		this.loginLogId = loginLogId;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}
}
