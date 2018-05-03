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
 * FileName    		:  GuiLoginLoggingPolicService.java										*                           
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

package com.pennant.gui.service;

/**
 * 
 */
public interface GuiLoginLoggingPolicService {

	/**
	 * Saves the login data for a succesfully login.<br>
	 * 
	 * @param userName
	 *            Username, who is succesfully logged in.
	 * @param userId
	 *            ID des Users.
	 * @param sessionId
	 *            ZK Session Id.
	 * @param clientAddress
	 *            Remote Ip from the client.
	 */
	long logAuthPass(String userName,long userId, String clientAddress, String sessionId);

	/**
	 * Saves the login data for a failed login.<br>
	 * 
	 * @param userName
	 *            Username, who is failed to login.
	 * @param sessionId
	 *            ZK Session Id.
	 * @param clientAddress
	 *            Remote Ip from the client.
	 */
	long logAuthFail(String userName, String clientAddress, String sessionId,String errorMessage);
	

}
