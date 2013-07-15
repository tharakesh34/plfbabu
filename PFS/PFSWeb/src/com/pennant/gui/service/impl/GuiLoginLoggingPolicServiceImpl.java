
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

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import com.pennant.backend.model.SecLoginlog;
import com.pennant.backend.service.LoginLoggingService;
import com.pennant.gui.service.GuiLoginLoggingPolicService;

public class GuiLoginLoggingPolicServiceImpl implements GuiLoginLoggingPolicService {

	private final static Logger logger = Logger.getLogger(GuiLoginLoggingPolicServiceImpl.class);
	private transient LoginLoggingService loginLoggingService;


	public void logAuthFail(String userName, String clientAddress, String sessionId,String errorMessage) {
		logger.debug("Entering ");

		if (logger.isInfoEnabled()) {
			logger.info("Login failed for: " + userName + " Host:" + clientAddress + " SessionId: " + sessionId);
		}


		long loginLogID=0;
		Timestamp  loginTime = new Timestamp(System.currentTimeMillis());
		SecLoginlog logingLog = new SecLoginlog(loginLogID, userName, loginTime, clientAddress, getBrowserType(), 0, sessionId, null,errorMessage);
		this.loginLoggingService.saveLog(logingLog);
		logger.debug("Leaving ");
	}

	@Override
	public void logAuthPass(String userName, long userId, String clientAddress, String sessionId) {
		logger.debug("Entering ");

		if (logger.isDebugEnabled()) {
			logger.debug("Login ok for: " + userName + " -> UserID: " + userId + " Host:" + clientAddress + " SessionId: " + sessionId);
		}
		long loginLogID=0;
		Timestamp  loginTime = new Timestamp(System.currentTimeMillis());
		SecLoginlog logingLog = new SecLoginlog(loginLogID, userName, loginTime, clientAddress, getBrowserType(), 1, sessionId, null,null);
		this.loginLoggingService.saveLog(logingLog);
		logger.debug("Leaving ");
	}


	public LoginLoggingService getLoginLoggingService() {
		return this.loginLoggingService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}


	private String getBrowserType(){
		String browserType = "";
		Execution execution =  Executions.getCurrent();
		
			if (execution!= null) {
			 browserType = Executions.getCurrent().getUserAgent();
		}
			return browserType;
	}

}
