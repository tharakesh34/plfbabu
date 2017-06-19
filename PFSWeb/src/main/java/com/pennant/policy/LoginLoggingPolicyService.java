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
 * FileName    		:  LoginLoggingPolicyService.java										*                           
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
package com.pennant.policy;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.gui.service.GuiLoginLoggingPolicService;
import com.pennant.policy.model.PolicyManager;
import com.pennant.policy.model.UserImpl;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.AuthenticationType;

/**
 * This class is called from spring aop as an aspect and is for logging <br>
 * the Login of a user. It is configurated in the <br>
 * '/zkoss/src/main/resources/springSecurityContext.xml' <br>
 * Logs success and fails, sessionID, timestamp and remoteIP. <br>
 * 
 */
public class LoginLoggingPolicyService implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LoginLoggingPolicyService.class);

	private PolicyManager policyManager;
	private UserImpl userDetails;
	private GuiLoginLoggingPolicService guiLoginLoggingPolicService;
	
	public LoginLoggingPolicyService() {
	}

	private void logAuthPass(Authentication authentication) {
		logger.debug("Entering");

		final String user = authentication.getName();
		long userId = 0;
		final String clientAddress = SessionUserDetails
				.convertClientAddress(authentication);
		final String sessionId = SessionUserDetails
				.convertClientSessionId(authentication);

		if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
			userDetails = (UserImpl) authentication.getPrincipal();
			userId = userDetails.getUserId();
		} else {
			userId = ((User) authentication.getPrincipal()).getUserId();
		}
		userDetails.setLoginId(getGuiLoginLoggingPolicService().logAuthPass(user, userId,
				clientAddress, sessionId));

		logger.debug("Leaving ");
	}

	private void logAuthFail(Authentication authentication,String errorMessage) {
		logger.debug("Entering ");

		final String user = authentication.getName();
		final String clientAddress = SessionUserDetails.convertClientAddress(authentication);
		final String sessionId = SessionUserDetails.convertClientSessionId(authentication);

		long logid = getGuiLoginLoggingPolicService().logAuthFail(user, clientAddress,sessionId,errorMessage);
		
		if (userDetails!=null) {
			userDetails.setLoginId(logid);
		}

		logger.debug("Leaving ");
	}


	public Authentication loginLogging(ProceedingJoinPoint call) throws Throwable {
		logger.debug("Entering ");
		WebAuthenticationDetails details = null;
		Authentication authentication = (Authentication) call.getArgs()[0];
		Authentication result;
		try {
			result = (Authentication) call.proceed();
			if (call.getTarget() instanceof DaoAuthenticationProvider) {
				App.AUTH_TYPE = AuthenticationType.DAO;
			} else if (call.getTarget() instanceof ActiveDirectoryLdapAuthenticationProvider) {
				userDetails = (UserImpl) getPolicyManager().loadUserByUsername(authentication.getName());
				App.AUTH_TYPE = AuthenticationType.LDAP;
				details = (WebAuthenticationDetails) result.getDetails();
				AbstractAuthenticationToken overRidedResult = new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
				overRidedResult.setDetails(details);
				result = overRidedResult;
			}else{
				userDetails = (UserImpl) getPolicyManager().loadUserByUsername(authentication.getName());
				App.AUTH_TYPE = AuthenticationType.SSO;
				//details = (WebAuthenticationDetails) result.getDetails();
				//AbstractAuthenticationToken overRidedResult = new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
				//overRidedResult.setDetails(details);
				//result = overRidedResult;				
			}

		} catch (Exception e) {
			logAuthFail(authentication, e.getMessage());
			logger.error("Exception: Login failed ", e);
			throw e;
		}

		if (result != null) {
			logAuthPass(result);
		}
		logger.debug("Leaving ");
		return result;
	}
	
	public GuiLoginLoggingPolicService getGuiLoginLoggingPolicService() {
		return guiLoginLoggingPolicService;
	}

	public void setGuiLoginLoggingPolicService(GuiLoginLoggingPolicService guiLoginLoggingPolicService) {
		this.guiLoginLoggingPolicService = guiLoginLoggingPolicService;
	}

	public PolicyManager getPolicyManager() {
		return policyManager;
	}

	public void setPolicyManager(PolicyManager policyManager) {
		this.policyManager = policyManager;
	}

}
