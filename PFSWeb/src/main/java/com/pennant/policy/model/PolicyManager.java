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
 * FileName    		:  PolicyManager.java													*                           
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
package com.pennant.policy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.UserService;


/**
 * This class implements the spring-security UserDetailService.<br>
 * It's been configured in the spring security xml contextfile.<br>
 * 
 */
@SuppressWarnings("deprecation")
public class PolicyManager implements UserDetailsService, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PolicyManager.class);

	private transient UserService userService;
	public UserDetails _userDetails;

	public PolicyManager() {
		
	}
	
	@Override
	public UserDetails loadUserByUsername(String userId) {
		logger.debug("Entering ");

		SecurityUser user = null;
		Collection<GrantedAuthority> grantedAuthorities = null;
		List<SecurityRole> securityRole=null;

		try {
			user = getUserByLogin(userId);
			if (user == null) {
				throw new UsernameNotFoundException("Invalid User");
			}

			/*if (!StringUtils.isEmpty(user.getUsrLanguage())) {
				Labels.register(new GeneralLabelLocator(user.getUsrLanguage()));
			} */ 
			
			securityRole= userService.getUserRolesByUserID(user.getId()); 	 
			grantedAuthorities = getGrantedAuthority(user);

		} catch (final NumberFormatException e) {
			throw new DataRetrievalFailureException("Cannot loadUserByUsername userId:" + userId + " Exception:" + e.getMessage(), e);
		}

		// Create the UserDetails object for a specified user with
		// their grantedAuthorities List.
		final UserDetails userDetails = new UserImpl(user, grantedAuthorities,securityRole);

		if (logger.isDebugEnabled()) {
			logger.debug("Rights for '" + user.getUsrLogin() + "' (ID: " + user.getId() + ") evaluated. [" + this + "]");
		}

		// neu wegen clustering ?
		this._userDetails = userDetails;
		logger.debug("Leaving ");
		return userDetails;

	}

	/**
	 * Gets the User object by his stored userName.<br>
	 * 
	 * @param userName
	 * @return
	 */
	public SecurityUser getUserByLogin(final String userName) {
		logger.debug("Entering ");
		return getUserService().getUserByLogin(userName);
	}

	/**
	 * Fills the GrantedAuthorities List for a specified user.<br>
	 * 1. Gets a unique list of rights that a user have.<br>
	 * 2. Creates GrantedAuthority objects from all rights. <br>
	 * 3. Creates a GrantedAuthorities list from all GrantedAuthority objects.<br>
	 * 
	 * @param user
	 * @return
	 */
	private Collection<GrantedAuthority> getGrantedAuthority(SecurityUser user) {
		logger.debug("Entering ");

		// get the list of rights for a specified user.
		final Collection<SecurityRight> rights = getUserService().getMenuRightsByUser(user);

		final ArrayList<GrantedAuthority> rechteGrantedAuthorities = new ArrayList<GrantedAuthority>(rights.size());

		// now create for all rights a GrantedAuthority
		// and fill the GrantedAuthority List with these authorities.
		for (final SecurityRight right : rights) {
			rechteGrantedAuthorities.add(new GrantedAuthorityImpl(right.getRightName()));
		}
		logger.debug("Leaving ");
		return rechteGrantedAuthorities;
	}

	public UserService getUserService() {
		return this.userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
