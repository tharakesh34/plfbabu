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
 * FileName    		:  PasswordEncoderImpl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  30-07-2011	       Pennant	                 0.1                                            * 
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
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.pennant.app.util.SystemParameterDetails;

public class PasswordEncoderImpl implements PasswordEncoder, Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(PasswordEncoderImpl.class);

	public PasswordEncoderImpl() {
	}

	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object token) {
		logger.debug("Entering ");
	
		
		if (encPass == null) {	
			return false;
		}

		if (token == null) {
			return encPass.equals(rawPass);
		}

		if (rawPass.length() < Integer.parseInt(SystemParameterDetails.getSystemParameterValue("USR_PWD_MIN_LEN").toString())) {
			
			return false;
		}

		com.pennant.sec.util.PasswordEncoderImpl encoderImpl = new com.pennant.sec.util.PasswordEncoderImpl();
		logger.debug("Leaving ");
		return encoderImpl.isPasswordValid(encPass, rawPass, token);
	}

	@Override
	public String encodePassword(String rawPass, Object token) throws DataAccessException {
		logger.debug("Entering ");
		throw new RuntimeException();
	}
}
