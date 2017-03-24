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
 * FileName    		:  UserCounterImpl.java													*                           
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
package com.pennant.policy.model;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.pennant.policy.UserCounter;

public class UserCounterImpl implements UserCounter, Serializable {

	private static final long serialVersionUID = -2617461135135795178L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(UserCounterImpl.class);

	public UserCounterImpl() {
		
	}
	
	public static class SessionListener {

		private UserCounterImpl userCounter;
		private final Log logger = LogFactory.getLog(getClass());

		public void startSession() {
			this.logger.warn("start");
		}

		public void disposeSession() {
			this.logger.warn("ende");
		}

		public UserCounterImpl getUserCounter() {
			return this.userCounter;
		}

		public void setUserCounter(UserCounterImpl userCounter) {
			this.userCounter = userCounter;
		}

	}

}
