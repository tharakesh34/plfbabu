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
 * FileName    		:  LoginLoggingServiceImpl.java											*                           
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
package com.pennant.backend.service.impl;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.SecLoginlogDAO;
import com.pennant.backend.dao.UserDAO;
import com.pennant.backend.model.SecLoginlog;
import com.pennant.backend.service.LoginLoggingService;

public class LoginLoggingServiceImpl implements LoginLoggingService {
	private final static Logger logger = Logger.getLogger(LoginLoggingServiceImpl.class);

	private SecLoginlogDAO secLoginlogDAO;
	private UserDAO userDAO;

	/**
	 * default Constructor
	 */
	public LoginLoggingServiceImpl() {
		super();
	}

	@Override
	public long saveLog(SecLoginlog logingLog) {
		logger.debug("Entering");

		long id = secLoginlogDAO.saveLog(logingLog);
		userDAO.updateLoginStatus(logingLog.getLoginUsrLogin(), logingLog.getLoginStsID());

		logger.debug("Leaving");
		return id;
	}

	@Override
	public void logLogOut(long loginId) {
		secLoginlogDAO.logLogOut(loginId);
	}

	public void setSecLoginlogDAO(SecLoginlogDAO secLoginlogDAO) {
		this.secLoginlogDAO = secLoginlogDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}
