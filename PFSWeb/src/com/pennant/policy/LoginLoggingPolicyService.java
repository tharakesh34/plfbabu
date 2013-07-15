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
import org.springframework.security.core.Authentication;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.gui.service.GuiLoginLoggingPolicService;

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
		final Authentication authentication = (Authentication) call.getArgs()[0];

		@SuppressWarnings("unused")
		Object userObject=  authentication.getPrincipal();

		final Authentication result;
		try {
			result = (Authentication) call.proceed();
		} catch (Exception e) {
			logAuthFail(authentication,e.getMessage());
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

}
