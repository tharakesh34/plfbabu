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
 * FileName    		:  ApplicationWorkspace.java											*                           
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
package com.pennant;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * Workspace for the application. One workspace server. <br>
 * <br>
 * Here are stored several properties for the application. <br>
 * <br>
 * 1. Language properties files. <br>
 * 2. Default values for creating new entries for: <br>
 * - users. <br>
 * 
 * <b>Not used at time!</b><br>
 * 
 */
public class ApplicationWorkspace implements Serializable {

	private static ApplicationWorkspace instance = new ApplicationWorkspace();

	private static final long serialVersionUID = -1397646202890802880L;
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(ApplicationWorkspace.class);

	/**
	 * Default Constructor, cannot invoked from outer this class. <br>
	 */
	private ApplicationWorkspace() {

	}

	public static ApplicationWorkspace getInstance() {
		return instance;
	}

}
