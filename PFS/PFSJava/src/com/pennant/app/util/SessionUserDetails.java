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
 * FileName    		:  SessionUserDetails.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
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
package com.pennant.app.util;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.policy.model.UserImpl;

public class SessionUserDetails implements Serializable {
	
    private static final long serialVersionUID = 1116443283350618246L;

	public static Authentication getAuthentication(){
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	public static UserImpl getLogiedInUser(){
		Authentication currentUser = getAuthentication();
		if(currentUser==null){
			return null;
		}
		return (UserImpl) currentUser.getPrincipal();
	}
	
	public static  LoginUserDetails getUserDetails(UserImpl  userDetails){
		LoginUserDetails loginUserDetails = new LoginUserDetails();
		loginUserDetails.setLoginUsrID(userDetails.getUserId());
		loginUserDetails.setLoginBranchCode(userDetails.getSecurityUser().getUsrBranchCode());
		loginUserDetails.setLoginDeptCode(userDetails.getSecurityUser().getUsrDeptCode());
		loginUserDetails.setLoginIP(convertClientAddress(SecurityContextHolder.getContext().getAuthentication()));
		loginUserDetails.setLoginSessionID(convertClientSessionId(SecurityContextHolder.getContext().getAuthentication()));
		loginUserDetails.setUsrLanguage(userDetails.getSecurityUser().getUsrLanguage());
		loginUserDetails.setUsrCanOverrideLimits(userDetails.getSecurityUser().isUsrCanOverrideLimits());
		return loginUserDetails;
	}

	public static  String getUserLanguage(){
		
		UserImpl  userDetails = getLogiedInUser();
		if(userDetails==null){
			return (String) SystemParameterDetails.getSystemParameterValue("APP_LNG");
		}
		return userDetails.getSecurityUser().getUsrLanguage();
	}

	public static String convertClientAddress(Authentication authentication) {
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			return details.getRemoteAddress();
		} catch (ClassCastException e) {
			return "";
		}
	}

	public static  String convertClientSessionId(Authentication authentication) {
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			return details.getSessionId();
		} catch (ClassCastException e) {
			return "";
		}
	}
	
}
