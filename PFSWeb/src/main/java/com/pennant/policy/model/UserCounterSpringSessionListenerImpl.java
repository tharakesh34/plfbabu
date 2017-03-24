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
 * FileName    		:  UserCounterSpringSessionListenerImpl.java 							*                           
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
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.aspectj.lang.reflect.Pointcut;
public class UserCounterSpringSessionListenerImpl implements Serializable {

	private static final long serialVersionUID = 8979460663616009375L;
	private static final Logger logger = Logger.getLogger(UserCounterSpringSessionListenerImpl.class);
	private UserCounterImpl userCounter;

	public UserCounterSpringSessionListenerImpl() {
		
	}
	
	public void startSession() {
		logger.warn("start");
	}

	public void disposeSession() {
		logger.warn("ende");
	}

	public void registerNewSession(Pointcut pointcut) {
		// public void registerNewSession(Object returnValue, Method method,
		// Object[] args, Object target) throws Throwable {
		// final String session = pointcut. user1.toString();
		// final String user = session1;
		// System.err.println("###############################################");
		// System.err.println(session + " -> " + user);

		// return call.proceed();
	}

	public void removeSessionInformation(Object returnValue, Method method, Object[] args, Object target)
			throws Throwable {
		final String user = (String) args[0];
		logger.debug(user);
	}

	public UserCounterImpl getUserCounter() {
		return this.userCounter;
	}

	public void setUserCounter(UserCounterImpl userCounter) {
		this.userCounter = userCounter;
	}

}
