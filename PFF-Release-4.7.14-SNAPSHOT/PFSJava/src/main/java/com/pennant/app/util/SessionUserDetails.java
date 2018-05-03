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

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.pennanttech.framework.security.core.User;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SessionUserDetails implements Serializable {
	private static final long serialVersionUID = 1116443283350618246L;
	private static final Logger logger = Logger.getLogger(SessionUserDetails.class);

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static User getLogiedInUser() {
		Authentication currentUser = getAuthentication();
		if (currentUser == null) {
			return null;
		}

		return (User) currentUser.getPrincipal();
	}

	public static LoggedInUser getUserDetails(User userDetails) {
		LoggedInUser user = new LoggedInUser();
		user.setLoginUsrID(userDetails.getUserId());
		user.setBranchCode(userDetails.getSecurityUser().getUsrBranchCode());
		user.setDepartmentCode(userDetails.getSecurityUser().getUsrDeptCode());
		user.setIpAddress(convertClientAddress(SecurityContextHolder.getContext().getAuthentication()));
		user.setSessionId(convertClientSessionId(SecurityContextHolder.getContext().getAuthentication()));
		user.setUsrLanguage(userDetails.getSecurityUser().getUsrLanguage());

		return user;
	}

	public static String getUserLanguage() {

		User userDetails = getLogiedInUser();
		if (userDetails == null) {
			return SysParamUtil.getValueAsString("APP_LNG");
		}
		return userDetails.getSecurityUser().getUsrLanguage();
	}

	private static String convertClientAddress(Authentication authentication) {
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			return details.getRemoteAddress();
		} catch (ClassCastException e) {
			logger.error("Exception: ", e);
			return "";
		}
	}

	private static String convertClientSessionId(Authentication authentication) {
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			return details.getSessionId();
		} catch (ClassCastException e) {
			logger.error("Exception: ", e);
			return "";
		}
	}
}
