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

	public SecurityUser getNewUser();

	public int getCountAllSecUser();

	List<SecurityUser> getAlleUser();
	
	
	public SecurityUser getUserByLogin(final String userName);

	public Collection<SecurityRight> getRightsByUser(SecurityUser user);

	public List<SecurityUser> getUserLikeLoginname(String value);

	public List<SecurityUser> getUserLikeLastname(String value);

	public List<SecurityUser> getUserLikeEmail(String value);
	
	public List<SecurityUser> getUserListByLogin(String userName);
	
	public Collection<SecurityRight> getMenuRightsByUser(SecurityUser user);
	
	public Collection<SecurityRight> getPageRights(SecurityRight secRight);

	public List<SecurityRole> getUserRolesByUserID(long userID);
	
	public List<SecurityRight> getRoleRights(SecurityRight secRight);
	public List<SecurityRight> getRoleRights(SecurityRight secRight,String[] roles); 
	
}
