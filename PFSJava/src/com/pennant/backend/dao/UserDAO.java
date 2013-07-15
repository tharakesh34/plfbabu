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
 * FileName    		:  UserDAO.java															*                           
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
package com.pennant.backend.dao;

import java.util.List;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;

public interface UserDAO {

	public SecurityUser getNewSecUser();

	public int getCountAllSecUser();

	public List<SecurityUser> getAlleUser();

	public SecurityUser getUserByID(Long usr_id);

	public SecurityUser getUserByFiluserNr(String usr_nr);

	public SecurityUser getUserByNameAndPassword(String userName, String passWord);

	public SecurityUser getUserByLogin(final String userName);

	public List<SecurityUser> getUserLikeLastname(String value);

	public List<SecurityUser> getUserLikeLogin(String value);

	public List<SecurityUser> getUserLikeEmail(String email);

	public List<SecurityUser> getUserListByLogin(String login);

	public void updateLoginStatus(String userName, int authPass);

	public List<SecurityRole> getUserRolesByUserID(long userID);

	public void update(SecurityUser user);//for changing password

}
