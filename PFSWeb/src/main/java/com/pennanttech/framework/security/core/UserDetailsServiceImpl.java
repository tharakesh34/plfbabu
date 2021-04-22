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
 * FileName    		:  UserDetailsServiceImpl.java													*                           
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
package com.pennanttech.framework.security.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This class implements the spring-security UserDetailService.<br>
 * It's been configured in the spring security xml contextfile.<br>
 * 
 */
public class UserDetailsServiceImpl implements UserDetailsService {
	private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

	private UserService userService;

	public UserDetailsServiceImpl() {
		super();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug(Literal.ENTERING);

		username = StringUtils.upperCase(username);
		SecurityUser user = userService.getUserByLogin(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found.");
		} else if (!user.isUsrEnabled()) {
			throw new UsernameNotFoundException("User account disabled.");
		}

		List<SecurityRole> securityRole = userService.getUserRolesByUserID(user.getId());
		Collection<GrantedAuthority> grantedAuthorities = getGrantedAuthority(user);

		final UserDetails userDetails = new User(user, grantedAuthorities, securityRole);

		logger.debug(Literal.LEAVING);
		return userDetails;

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
		logger.trace(Literal.ENTERING);

		// get the list of rights for a specified user.
		final Collection<SecurityRight> rights = userService.getMenuRightsByUser(user);

		final ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>(rights.size());

		rights.stream().forEach(right -> grantedAuthorities.add(new SimpleGrantedAuthority(right.getRightName())));

		logger.trace(Literal.LEAVING);
		return grantedAuthorities;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
