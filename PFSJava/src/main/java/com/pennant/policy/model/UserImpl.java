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
package com.pennant.policy.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;

public class UserImpl extends User implements Serializable, com.pennant.policy.User {
	private static final long serialVersionUID = 7682359879431168931L;

	final private String usrToken;
	final private long userId;
	private SecurityUser securityUser;
	private List<SecurityRole> securityRole;
	private long loginId;

	public UserImpl(SecurityUser user, Collection<GrantedAuthority> grantedAuthorities, List<SecurityRole> securityRole){
		super(user.getUsrLogin(), user.getUsrPwd(), user.isUsrEnabled(), user.isAccountNonExpired(), user
				.isCredentialsNonExpired(), user.isAccountNonLocked(), grantedAuthorities);

		this.usrToken = user.getUsrToken();
		this.userId = user.getId();
		this.securityUser = user;
		this.securityRole = securityRole;
	}

	/*
	 * public Md5Token getToken() { if (StringUtils.isBlank(getUsrToken())) { return null; } return new
	 * Md5Token(getUsrToken()); }
	 */

	public String getToken() {
		return getUsrToken();
	}

	private String getUsrToken() {
		return this.usrToken;
	}

	@Override
	public long getUserId() {
		return this.userId;
	}

	@Override
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
