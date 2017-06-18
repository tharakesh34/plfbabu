
/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  GuiLoginLoggingPolicServiceImpl.java									*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  05-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.gui.service.impl;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import com.pennant.backend.model.SecLoginlog;
import com.pennant.backend.service.LoginLoggingService;
import com.pennant.gui.service.GuiLoginLoggingPolicService;

public class GuiLoginLoggingPolicServiceImpl implements GuiLoginLoggingPolicService {

	private static final Logger logger = Logger.getLogger(GuiLoginLoggingPolicServiceImpl.class);
	private transient LoginLoggingService loginLoggingService;

	public GuiLoginLoggingPolicServiceImpl() {
		
	}
	
	public long logAuthFail(String userName, String clientAddress, String sessionId,String errorMessage) {
		logger.debug("Entering ");

		if (logger.isInfoEnabled()) {
			logger.info("Login failed for: " + userName + " Host:" + clientAddress + " SessionId: " + sessionId);
		}


		Timestamp  loginTime = new Timestamp(System.currentTimeMillis());
		long loginLogID = this.loginLoggingService.saveLog(getLogingLog(userName, loginTime, clientAddress, getBrowserType(), 0, sessionId, null,errorMessage));
		
		logger.debug("Leaving ");
		return loginLogID;
	}

	@Override
	public long logAuthPass(String userName, long userId, String clientAddress, String sessionId) {
		logger.debug("Entering");
		 
		Timestamp loginTime = new Timestamp(System.currentTimeMillis());
		long loginLogID = this.loginLoggingService.saveLog(getLogingLog(userName, loginTime, clientAddress, getBrowserType(), 1,
				sessionId, null, null));
		logger.debug("Leaving");
		return loginLogID;
	}


	public LoginLoggingService getLoginLoggingService() {
		return this.loginLoggingService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}


	private String getBrowserType() {
		String browserType = "";
		Execution execution = Executions.getCurrent();

		if (execution != null) {
			browserType = Executions.getCurrent().getUserAgent();
		}

		return browserType;
	}
	
	private SecLoginlog getLogingLog(String loginUsrLogin, Timestamp loginTime, String loginIP, String loginBrowserType,
			int loginStsID, String loginSessionID, Timestamp logOutTime, String loginError) {
		SecLoginlog secLoginlog = new SecLoginlog();

		secLoginlog.setLoginLogID(Long.MIN_VALUE);
		secLoginlog.setLoginUsrLogin(loginUsrLogin);
		secLoginlog.setLoginTime(loginTime);
		secLoginlog.setLoginIP(loginIP);
		secLoginlog.setLoginBrowserType(loginBrowserType);
		secLoginlog.setLoginStsID(loginStsID);
		secLoginlog.setLoginSessionID(loginSessionID);
		secLoginlog.setLogOutTime(logOutTime);
		if (StringUtils.length(loginError) <= 500) {
			secLoginlog.setLoginError(loginError);
		} else {
			loginError = StringUtils.substring(loginError, 0, 500);
		}

		return secLoginlog;
	}
}
