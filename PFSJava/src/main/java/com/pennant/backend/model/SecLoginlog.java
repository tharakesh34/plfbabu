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
 * FileName : SecLoginlog.java *
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
package com.pennant.backend.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class SecLoginlog implements Serializable {
	private static final long serialVersionUID = -3312254328936965373L;

	private long loginLogID;
	private String loginUsrLogin;
	private Timestamp loginTime;
	private String loginIP;
	private String loginBrowserType;
	private int loginStsID;
	private String loginSessionID;
	private Timestamp logOutTime;
	private String loginError;

	public SecLoginlog() {
	    super();
	}

	public void setId(long id) {
		this.loginLogID = id;
	}

	public long getId() {
		return this.loginLogID;
	}

	public long getLoginLogID() {
		return loginLogID;
	}

	public void setLoginLogID(long loginLogID) {
		this.loginLogID = loginLogID;
	}

	public String getLoginUsrLogin() {
		return loginUsrLogin;
	}

	public void setLoginUsrLogin(String loginUsrLogin) {
		this.loginUsrLogin = loginUsrLogin;
	}

	public Timestamp getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}

	public String getLoginIP() {
		return loginIP;
	}

	public void setLoginIP(String loginIP) {
		this.loginIP = loginIP;
	}

	public String getLoginBrowserType() {
		return loginBrowserType;
	}

	public void setLoginBrowserType(String loginBrowserType) {
		this.loginBrowserType = loginBrowserType;
	}

	public int getLoginStsID() {
		return loginStsID;
	}

	public void setLoginStsID(int loginStsID) {
		this.loginStsID = loginStsID;
	}

	public String getLoginSessionID() {
		return loginSessionID;
	}

	public void setLoginSessionID(String loginSessionID) {
		this.loginSessionID = loginSessionID;
	}

	public Timestamp getLogOutTime() {
		return logOutTime;
	}

	public void setLogOutTime(Timestamp logOutTime) {
		this.logOutTime = logOutTime;
	}

	public String getLoginError() {
		return loginError;
	}

	public void setLoginError(String loginError) {
		this.loginError = loginError;
	}

}
