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
 * FileName    		:  LoginLoggingPolicyService.java										*                           
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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.SecLoginlog;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;

import eu.bitwalker.useragentutils.UserAgent;

/**
 * This class is called from spring AOP as an aspect and is for logging.
 */
public class AuthenticationManager implements AuthenticationProvider {
	private final static Logger		logger	= Logger.getLogger(Authentication.class);

	@Autowired
	private UserDetailsService		userDetailsService;
	@Autowired
	private UserService				userService;
	@Autowired
	private DaoAuthenticationProvider	daoAuthenticationProvider;
	@Autowired
	private AuthenticationProvider	ldapAuthenticationProvider;

	@Value("${authentication.default}")
	private String					defaultAuthType;
	@Value("${authentication.ldap}")
	private boolean					ldapAuthentication;
	@Value("${authentication.dao}")
	private boolean					daoAuthentication;

	public AuthenticationManager() {
		super();
	}

	/**
	 * Authenticates the user. If multiple types of authentication allowed, the application will try each type until
	 * succeeded.
	 * 
	 * @param call
	 * @return The authentication token. <code>null</code>, if the authentication fails.
	 * @throws Throwable
	 *             - If the authentication fails.
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Authentication result = null;

		try {

			if (ldapAuthentication && daoAuthentication) {
				boolean defaultIsActiveDirectory = false;

				try {
					if ("LDAP".equals(defaultAuthType)) {
						defaultIsActiveDirectory = true;
						result = authenticate(authentication, true);
					} else {
						result = authenticate(authentication, false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (result == null) {
					if (defaultIsActiveDirectory) {
						result = authenticate(authentication, false);
					} else {
						result = authenticate(authentication, true);
					}
				}

			} else if (ldapAuthentication) {
				result = authenticate(authentication, true);
			} else if (daoAuthentication) {
				result = authenticate(authentication, false);
			}
		} catch (Exception e) {
			logAttempt(authentication, e.getMessage());
		}

		if (result != null) {
			logAttempt(result, null);
		}

		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

	private Authentication authenticate(Authentication authentication, boolean ldap) {
		Authentication result = null;

		if (ldap) {
			User user = (User) userDetailsService.loadUserByUsername(authentication.getName());

			result = ldapAuthenticationProvider.authenticate(authentication);

			AbstractAuthenticationToken token = null;
			token = new UsernamePasswordAuthenticationToken(user, result.getCredentials(), user.getAuthorities());
			token.setDetails(result.getDetails());
			result = token;
		} else {
			result = daoAuthenticationProvider.authenticate(authentication);
		}
		
		SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(result);

		return result;
	}

	/**
	 * Save the login attempt details to the database.
	 * 
	 * @param authentication
	 * @param error
	 */
	private void logAttempt(Authentication authentication, String error) {
		logger.trace(Literal.ENTERING);

		long logId = userService.logLoginAttempt(getLoginLog(authentication, error));

		if (error == null) {
			((User) authentication.getPrincipal()).setLoginId(logId);
		}

		logger.trace(Literal.LEAVING);
	}

	/**
	 * Gets the currently authenticated principal, or an authentication request token.
	 * 
	 * @return The Authentication or <code>null</code> if no authentication information is available.
	 */
	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static LoggedInUser getLoggedInUser() {
		Authentication authentication = getAuthentication();

		if (authentication == null) {
			return null;
		}

		User userImpl = (User) authentication.getPrincipal();
		LoggedInUser loggedInUser = getUserDetails(userImpl.getSecurityUser());

		loggedInUser.setSessionId(getSessionId(authentication));
		loggedInUser.setLoginLogId(userImpl.getLoginId());

		return loggedInUser;
	}

	public static Collection<GrantedAuthority> getGrantedAuthorities() {
		Collection<GrantedAuthority> grantedAuthorities = null;

		Authentication currentUser = getAuthentication();

		if (currentUser == null) {
			return null;
		}

		User userImpl = (User) currentUser.getPrincipal();
		grantedAuthorities = userImpl.getAuthorities();

		return grantedAuthorities;
	}

	public static List<SecurityRole> getSecurityRoles() {
		List<SecurityRole> roles = null;

		Authentication currentUser = getAuthentication();

		if (currentUser == null) {
			return null;
		}

		User userImpl = (User) currentUser.getPrincipal();
		roles = userImpl.getSecurityRole();

		return roles;
	}

	private static LoggedInUser getUserDetails(SecurityUser user) {
		LoggedInUser loggedInUser = new LoggedInUser();

		loggedInUser.setLoginUsrID(user.getUsrID());
		loggedInUser.setUserName(user.getUsrLogin());
		loggedInUser.setStaffId(user.getUserStaffID());
		loggedInUser.setFirstName(user.getUsrFName());
		loggedInUser.setMiddleName(user.getUsrMName());
		loggedInUser.setLastName(user.getUsrLName());
		loggedInUser.setMobileNo(user.getUsrMobile());
		loggedInUser.setEmailId(user.getUsrEmail());
		loggedInUser.setUsrLanguage(user.getUsrLanguage());
		loggedInUser.setBranchCode(user.getUsrBranchCode());
		loggedInUser.setDepartmentCode(user.getUsrDeptCode());
		loggedInUser.setLogonFromTime(user.getUsrCanSignonFrom());
		loggedInUser.setLogonToTime(user.getUsrCanSignonTo());
		loggedInUser.setAccountExpiredOn(user.getUsrAcExpDt());
		loggedInUser.setPrevPassLogonTime(user.getLastLoginOn());
		loggedInUser.setPrevFailLogonTime(user.getLastFailLoginOn());
		loggedInUser.setFailAttempts(user.getUsrInvldLoginTries());
		loggedInUser.setIpAddress(getRemoteAddress());
		loggedInUser.setBrowserType(getBrowser());
		loggedInUser.setLogonTime(DateUtility.getTimestamp(new Date()));
		loggedInUser.setAuthType(user.getAuthType());
		loggedInUser.setUserType(user.getUserType());
		loggedInUser.setCredentialsExpired(user.isUsrCredentialsExp());

		return loggedInUser;
	}

	public static String getRemoteAddress() {
		try {

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletRequest httpRequest = (HttpServletRequest) attr.getRequest();

			// Get the remote host.
			String host;
			if (httpRequest.getHeader("X-FORWARDED-FOR") != null) {
				host = httpRequest.getHeader("X-FORWARDED-FOR");
			} else {
				host = httpRequest.getRemoteHost();
			}

			return host;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return "";
		}
	}

	public static String getSessionId(Authentication authentication) {
		WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
		return details.getSessionId();
	}

	public static String getBrowser() {
		UserAgent userAgent = getUserAgent();

		return userAgent.getBrowser().getName() + " Version " + userAgent.getBrowserVersion();
	}
	
	private static UserAgent getUserAgent() {
		return UserAgent.parseUserAgentString(getRequestAttribute().getRequest().getHeader("User-Agent"));
	}
	
	private static ServletRequestAttributes getRequestAttribute() {
		return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	}

	private SecLoginlog getLoginLog(Authentication authentication, String loginError) {
		SecLoginlog secLoginlog = new SecLoginlog();

		secLoginlog.setLoginLogID(Long.MIN_VALUE);
		secLoginlog.setLoginUsrLogin(authentication.getName());
		secLoginlog.setLoginTime(new Timestamp(System.currentTimeMillis()));
		secLoginlog.setLoginIP(getRemoteAddress());
		secLoginlog.setLoginBrowserType(getBrowser());
		secLoginlog.setLoginSessionID(getSessionId(authentication));

		if (StringUtils.length(loginError) <= 500) {
			secLoginlog.setLoginError(loginError);
		} else {
			loginError = StringUtils.substring(loginError, 0, 500);
		}

		secLoginlog.setLoginError(loginError);

		if (loginError == null) {
			secLoginlog.setLoginStsID(1);
		} else {
			secLoginlog.setLoginStsID(0);
		}

		return secLoginlog;
	}
}
