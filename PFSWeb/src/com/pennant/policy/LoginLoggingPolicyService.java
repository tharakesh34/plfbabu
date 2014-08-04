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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.gui.service.GuiLoginLoggingPolicService;
import com.pennant.policy.model.PolicyManager;
import com.pennant.policy.model.UserImpl;

/**
 * This class is called from spring aop as an aspect and is for logging <br>
 * the Login of a user. It is configurated in the <br>
 * '/zkoss/src/main/resources/springSecurityContext.xml' <br>
 * Logs success and fails, sessionID, timestamp and remoteIP. <br>
 * 
 */
public class LoginLoggingPolicyService implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(LoginLoggingPolicyService.class);
	private PolicyManager policyManager;
	private UserImpl userDetails;
	private GuiLoginLoggingPolicService guiLoginLoggingPolicService;

	public LoginLoggingPolicyService() {
	}

	private void logAuthPass(Authentication authentication) {
		logger.debug("Entering ");
		final String user = authentication.getName();
		final long userId = ((User) authentication.getPrincipal()).getUserId();
		final String clientAddress = SessionUserDetails.convertClientAddress(authentication);
		final String sessionId = SessionUserDetails.convertClientSessionId(authentication);

		getGuiLoginLoggingPolicService().logAuthPass(user, userId, clientAddress, sessionId);
		logger.debug("Leaving ");

	}

	private void logAuthFail(Authentication authentication,String errorMessage) {
		logger.debug("Entering ");
		final String user = authentication.getName();
		final String clientAddress = SessionUserDetails.convertClientAddress(authentication);
		final String sessionId = SessionUserDetails.convertClientSessionId(authentication);

		getGuiLoginLoggingPolicService().logAuthFail(user, clientAddress, sessionId,errorMessage);
		logger.debug("Leaving ");
	}

	public Authentication loginLogging(ProceedingJoinPoint call) throws Throwable {
		logger.debug("Entering ");
		WebAuthenticationDetails details = null;
		Authentication authentication = (Authentication) call.getArgs()[0];
		Authentication result;
		try { // ###CR_10/07/12_47### Start
			/*
			 * Here we are checking for Authentication provider is LDAP's and is
			 * LDAP authentication flag is false then we are not allowing LDAP
			 * authentication .this check for when having Multiple
			 * Authentication providers
			 */
			if (call.getTarget() instanceof ActiveDirectoryLdapAuthenticationProvider && !PennantConstants.IS_LDAP_AUHRNTICATION) {
				throw new UsernameNotFoundException("LDAP Authentication  not allowed");
			}
			if (!(call.getTarget() instanceof ActiveDirectoryLdapAuthenticationProvider) && PennantConstants.IS_LDAP_AUHRNTICATION) {
				throw new UsernameNotFoundException("DAO  Authentication  not allowed");
			}
			result = (Authentication) call.proceed();
			if (PennantConstants.IS_LDAP_AUHRNTICATION) {
				userDetails = (UserImpl) getPolicyManager().loadUserByUsername(result.getName());
				details = (WebAuthenticationDetails) result.getDetails();
				AbstractAuthenticationToken overRidedResult = new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
				overRidedResult.setDetails(details);
				result = overRidedResult;
			}
		} catch (Exception e) {
			if (call.getTarget() instanceof ActiveDirectoryLdapAuthenticationProvider && PennantConstants.IS_LDAP_AUHRNTICATION) {
				logAuthFail(authentication, e.getMessage());
				logger.error("Login failed resason" + e.toString());
			}
			if (!(call.getTarget() instanceof ActiveDirectoryLdapAuthenticationProvider) && !PennantConstants.IS_LDAP_AUHRNTICATION) {
				logAuthFail(authentication, e.getMessage());
				logger.error("Login failed resason" + e.toString());
			}
			throw e;
		} // ###CR_10/07/12_47### End
		if (result != null) {
			logAuthPass(result);
		}
		logger.debug("Leaving ");
		return result;}

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

	public UserImpl getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserImpl userDetails) {
		this.userDetails = userDetails;
	}
}
