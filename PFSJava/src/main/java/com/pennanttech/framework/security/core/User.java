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
 * FileName    		:  UserImpl.java														*                           
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
package com.pennanttech.framework.security.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;

public class User extends org.springframework.security.core.userdetails.User implements Serializable {
	private static final long	serialVersionUID	= 7682359879431168931L;

	final private String		usrToken;
	final private long			userId;
	private SecurityUser		securityUser;
	private List<SecurityRole>	securityRole;
	private long				loginId;

	public User(SecurityUser user, Collection<GrantedAuthority> authorities, List<SecurityRole> roles) {
		super(user.getUsrLogin(), user.getUsrPwd() == null ? "" : user.getUsrPwd(), user.isUsrEnabled(), user.isAccountNonExpired(), user
				.isCredentialsNonExpired(), user.isAccountNonLocked(), authorities);

		this.usrToken = user.getUsrToken();
		this.userId = user.getId();
		this.securityUser = user;
		this.securityRole = roles;
	}

	public String getToken() {
		return getUsrToken();
	}

	private String getUsrToken() {
		return this.usrToken;
	}

	public long getUserId() {
		return this.userId;
	}

	public SecurityUser getSecurityUser() {
		return this.securityUser;
	}

	public List<SecurityRole> getSecurityRole() {
		return securityRole;
	}

	public void setSecurityRole(List<SecurityRole> securityRole) {
		this.securityRole = securityRole;
	}

	public long getLoginId() {
		return loginId;
	}

	public void setLoginId(long loginId) {
		this.loginId = loginId;
	}
}
