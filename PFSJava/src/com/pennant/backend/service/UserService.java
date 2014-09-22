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
 * FileName    		:  UserService.java														*                           
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
package com.pennant.backend.service;

import java.util.Collection;
import java.util.List;

import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;


public interface UserService {

	SecurityUser getNewUser();
	int getCountAllSecUser();
	List<SecurityUser> getAlleUser();
	SecurityUser getUserByLogin(final String userName);
	Collection<SecurityRight> getRightsByUser(SecurityUser user);
	List<SecurityUser> getUserLikeLoginname(String value);
	List<SecurityUser> getUserLikeLastname(String value);
	List<SecurityUser> getUserLikeEmail(String value);
	List<SecurityUser> getUserListByLogin(String userName);
	Collection<SecurityRight> getMenuRightsByUser(SecurityUser user);
	Collection<SecurityRight> getPageRights(SecurityRight secRight, String menuRightName);
	List<SecurityRole> getUserRolesByUserID(long userID);
	List<SecurityRight> getRoleRights(SecurityRight secRight, String menuRightName);
	List<SecurityRight> getRoleRights(SecurityRight secRight,String[] roles); 
}
