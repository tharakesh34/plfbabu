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
 * FileName    		:  LoginUserDetails.java												*                           
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

public class LoginUserDetails {

	public LoginUserDetails() {
		
	}
	private long loginUsrID;
	private String loginBranchCode;
	private String loginDeptCode;
	private String loginIP;
	private String loginSessionID;
	private String usrLanguage;
	private boolean   usrCanOverrideLimits = false;
	
	public long getLoginUsrID() {
		return loginUsrID;
	}
	
	public void setLoginUsrID(long loginUsrID) {
		this.loginUsrID = loginUsrID;
	}
	
	public String getLoginBranchCode() {
		return loginBranchCode;
	}
	
	public void setLoginBranchCode(String loginBranchCode) {
		this.loginBranchCode = loginBranchCode;
	}
	
	public String getLoginDeptCode() {
		return loginDeptCode;
	}
	
	public void setLoginDeptCode(String loginDeptCode) {
		this.loginDeptCode = loginDeptCode;
	}
	
	public String getLoginIP() {
		return loginIP;
	}
	
	public void setLoginIP(String loginIP) {
		this.loginIP = loginIP;
	}
	
	public String getLoginSessionID() {
		return loginSessionID;
	}
	
	public void setLoginSessionID(String loginSessionID) {
		this.loginSessionID = loginSessionID;
	}
	
	public String getUsrLanguage() {
		return usrLanguage;
	}

	public void setUsrLanguage(String usrLanguage) {
		this.usrLanguage = usrLanguage;
	}

	public boolean isUsrCanOverrideLimits() {
    	return usrCanOverrideLimits;
    }

	public void setUsrCanOverrideLimits(boolean usrCanOverrideLimits) {
    	this.usrCanOverrideLimits = usrCanOverrideLimits;
    }

}
