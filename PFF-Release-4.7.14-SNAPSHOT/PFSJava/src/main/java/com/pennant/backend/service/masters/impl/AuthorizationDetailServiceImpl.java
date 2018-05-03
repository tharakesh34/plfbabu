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
 *																							*
 * FileName    		:  AuthorizationDetailServiceImpl.java                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2012    														*
 *                                                                  						*
 * Modified Date    :  21-06-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.masters.impl;

import java.util.List;

import com.pennant.backend.dao.masters.AuthorizationDetailDAO;
import com.pennant.backend.model.masters.AuthorizationDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.masters.AuthorizationDetailService;

/**
 * Service implementation for methods that depends on
 * <b>AuthorizationDetail</b>.<br>
 * 
 */
public class AuthorizationDetailServiceImpl extends
		GenericService<AuthorizationDetail> implements
		AuthorizationDetailService {
	private AuthorizationDetailDAO authorizationDetailDAO;

	/**
	 * @return the authorizationDetailDAO
	 */
	public AuthorizationDetailDAO getAuthorizationDetailDAO() {
		return authorizationDetailDAO;
	}

	/**
	 * @param authorizationDetailDAO
	 *            the authorizationDetailDAO to set
	 */
	public void setAuthorizationDetailDAO(
			AuthorizationDetailDAO authorizationDetailDAO) {
		this.authorizationDetailDAO = authorizationDetailDAO;
	}

	

	@Override
	public List<AuthorizationDetail> getAuthorizationDetails(String channelCode, String channelIP) {
		return getAuthorizationDetailDAO().getAuthorizationDetails(channelCode,channelIP);
	}
}