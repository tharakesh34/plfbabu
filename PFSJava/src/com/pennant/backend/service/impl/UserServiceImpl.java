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
 * FileName    		:  UserServiceImpl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  04-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.impl;

import java.util.Collection;
import java.util.List;

import com.pennant.backend.dao.LanguageDAO;
import com.pennant.backend.dao.UserDAO;
import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.UserService;

public class UserServiceImpl implements UserService {
	private UserDAO userDAO;
	private LanguageDAO languageDAO;

	private SecurityRightDAO securityRightDAO;

	public LanguageDAO getLanguageDAO() {
		return languageDAO;
	}

	public void setLanguageDAO(LanguageDAO languageDAO) {
		this.languageDAO = languageDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}



	public SecurityUser getNewUser() {
		return getUserDAO().getNewSecUser();
	}

	public List<SecurityUser> getAlleUser() {
		return getUserDAO().getAlleUser();
	}


	public SecurityUser getUserByLogin(final String userName) {
		return getUserDAO().getUserByLogin(userName);
	}

	@Override
	public Collection<SecurityRight> getRightsByUser(SecurityUser securityUser) {
		return getSecurityRightDAO().getRightsByUser(securityUser);
	}

	public List<SecurityUser> getUserLikeLastname(String value) {
		return getUserDAO().getUserLikeLastname(value);
	}

	public List<SecurityUser> getUserLikeLoginname(String value) {
		return getUserDAO().getUserLikeLogin(value);
	}

	public List<SecurityUser> getUserLikeEmail(String value) {
		return getUserDAO().getUserLikeEmail(value);
	}

	public List<SecurityUser> getUserListByLogin(String userName) {
		return getUserDAO().getUserListByLogin(userName);
	}

	public SecurityRightDAO getSecurityRightDAO() {
		return securityRightDAO;
	}

	public void setSecurityRightDAO(SecurityRightDAO securityRightDAO) {
		this.securityRightDAO = securityRightDAO;
	}

	public int getCountAllSecUser() {
		return getUserDAO().getCountAllSecUser();
	}

	public Collection<SecurityRight> getMenuRightsByUser(SecurityUser user) {
		return getSecurityRightDAO().getMenuRightsByUser(user);
		
	}

	public Collection<SecurityRight> getPageRights(SecurityRight secRight, String menuRightName) {
		return getSecurityRightDAO().getPageRights(secRight, menuRightName);
	}
	
	public List<SecurityRole> getUserRolesByUserID(long userID) {
		return getUserDAO().getUserRolesByUserID(userID);
	}

	public List<SecurityRight> getRoleRights(SecurityRight secRight, String menuRightName){
		return getSecurityRightDAO().getRoleRights(secRight, menuRightName); 
	}

	public List<SecurityRight> getRoleRights(SecurityRight secRight,String[] roles){
		return getSecurityRightDAO().getRoleRights(secRight,roles); 
	}


	
}
