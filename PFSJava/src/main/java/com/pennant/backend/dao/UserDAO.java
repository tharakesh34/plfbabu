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

	 SecurityUser getNewSecUser();
	 int getCountAllSecUser();
	 List<SecurityUser> getAlleUser();
	 SecurityUser getUserByID(Long usrID);
	 SecurityUser getUserByFiluserNr(String usrNum);
	 SecurityUser getUserByNameAndPassword(String userName, String passWord);
	 SecurityUser getUserByLogin(final String userName);
	 List<SecurityUser> getUserLikeLastname(String value);
	 List<SecurityUser> getUserLikeLogin(String value);
	 List<SecurityUser> getUserLikeEmail(String email);
	 List<SecurityUser> getUserListByLogin(String login);
	 void updateLoginStatus(String userName, int authPass);
	 List<SecurityRole> getUserRolesByUserID(long userID);
	 void update(SecurityUser user);//for changing password

}
