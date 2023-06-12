/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : LoginLoggingPolicyService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.framework.security.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.model.SecLoginlog;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.pff.constant.LookUpCode;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.security.ldap.ActiveDirectoryLdapAuthenticationProviderAdapter;
import com.pennanttech.pennapps.core.security.user.AuthenticationError;
import com.pennanttech.pennapps.core.security.user.ExternalAuthenticationProvider;
import com.pennanttech.pennapps.core.security.user.UserAuthenticationException;
import com.pennanttech.pennapps.core.util.DateUtil;

import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This class is called from spring AOP as an aspect and is for logging.
 * 
 * Supports DAO, LDAP, and External authentication.
 */
public class AuthenticationManager implements AuthenticationProvider {
	private static final Logger logger = LogManager.getLogger(Authentication.class);

	private UserDetailsService userDetailsService;
	private UserService userService;
	private ActiveDirectoryLdapAuthenticationProviderAdapter defaultLdapAuthenticationProviderAdapter;
	private DaoAuthenticationProvider daoAuthenticationProvider;
	private ExternalAuthenticationProvider externalAuthenticationProvider;
	private SecurityUserDAO securityUsersDAO;
	private SAMLAuthenticationProvider samlAuthenticationProvider;

	@Value("${authentication.default}")
	private String defaultAuthType;
	@Value("${authentication.ldap}")
	private boolean ldapAuthentication;
	@Value("${authentication.dao}")
	private boolean daoAuthentication;

	public AuthenticationManager() {
		super();
	}

	/**
	 * Authenticates the user. If multiple types of authentication allowed, the application will try each type until
	 * succeeded.
	 * 
	 * @param call
	 * @return The authentication token. <code>null</code>, if the authentication fails.
	 * @throws Throwable - If the authentication fails.
	 */
	@Override
	public Authentication authenticate(Authentication authentication) {
		logger.info("Auhenticating the user {}", authentication.getName());
		Authentication result = null;

		SecurityUser securityUser = null;
		try {
			if (authentication instanceof SAMLAuthenticationToken) {
				result = samlAuthenticationProvider.authenticate(authentication);
				SAMLCredential credential = (SAMLCredential) result.getCredentials();
				logger.info("Auhenticating the user {}", credential.getNameID().getValue());

				User user = (User) result.getDetails();

				securityUser = user.getSecurityUser();

			} else {
				User user = (User) userDetailsService.loadUserByUsername(authentication.getName());
				securityUser = user.getSecurityUser();

				String authType = securityUser.getAuthType();
				String domainName = securityUser.getldapDomainName();
				if ("DAO".equals(StringUtils.trim(authType))) {
					// DAO.
					result = daoAuthenticationProvider.authenticate(authentication);
				} else {
					if (externalAuthenticationProvider != null) {
						// External.
						String username = authentication.getPrincipal().toString();
						Object credentials = authentication.getCredentials();
						String password = credentials == null ? null : credentials.toString();

						externalAuthenticationProvider.authenticate(username, password);
						result = authentication;
					} else {
						// LDAP.
						result = defaultLdapAuthenticationProviderAdapter.getAuthenticationProvider(domainName)
								.authenticate(authentication);
					}

					if (result != null) {
						// Get the user details.
						AbstractAuthenticationToken token = null;
						token = new UsernamePasswordAuthenticationToken(user, result.getCredentials(),
								user.getAuthorities());
						token.setDetails(result.getDetails());

						result = token;
					}
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logAttempt(authentication, e.getMessage());

			if (e instanceof BadCredentialsException) {
				throw new UserAuthenticationException(AuthenticationError.INVALID_PASSWORD);
			}

			throw e;
		}

		if (result != null) {
			logAttempt(result, securityUser.getUsrID());
		}

		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(result);

		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

	private void logAttempt(Authentication authentication, String error) {
		userService.logLoginAttempt(getLoginLog(authentication, error));

		List<String> reasons = securityUsersDAO.getLovFieldCodeValues(LookUpCode.SU_DISABLE_REASON);

		String disableReason = LookUpCode.SU_DR_INCORRECTPASSWORD;

		if (!reasons.contains(disableReason)) {
			disableReason = null;
		}

		userService.updateInvalidTries(authentication.getName(), disableReason);
	}

	private void logAttempt(Authentication authentication, long userId) {
		SecLoginlog secLoginlog = getLoginLog(authentication);

		long logId = userService.logLoginAttempt(secLoginlog);

		userService.updateLoginStatus(userId);

		((User) authentication.getPrincipal()).setLoginId(logId);
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

		loggedInUser.setSessionId(getSessionId());
		loggedInUser.setLoginLogId(userImpl.getLoginId());

		return loggedInUser;
	}

	public static Collection<GrantedAuthority> getGrantedAuthorities() {
		Collection<GrantedAuthority> grantedAuthorities = null;

		Authentication currentUser = getAuthentication();

		if (currentUser == null) {
			return new ArrayList<>();
		}

		User userImpl = (User) currentUser.getPrincipal();
		grantedAuthorities = userImpl.getAuthorities();

		return grantedAuthorities;
	}

	public static List<SecurityRole> getSecurityRoles() {
		List<SecurityRole> roles = null;

		Authentication currentUser = getAuthentication();

		if (currentUser == null) {
			return new ArrayList<>();
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
		loggedInUser.setLogonTime(DateUtil.getTimestamp(new Date()));
		loggedInUser.setAuthType(user.getAuthType());
		loggedInUser.setUserType(user.getUserType());
		loggedInUser.setPasswordExpiredOn(user.getPwdExpDt());

		return loggedInUser;
	}

	public static String getRemoteAddress() {
		try {

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletRequest httpRequest = attr.getRequest();

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

	public static String getSessionId() {
		return getRequestAttribute().getSessionId();
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
		SecLoginlog secLoginlog = getLoginLog(authentication);

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

	private SecLoginlog getLoginLog(Authentication authentication) {
		SecLoginlog secLoginlog = new SecLoginlog();
		secLoginlog.setLoginLogID(Long.MIN_VALUE);
		secLoginlog.setLoginUsrLogin(authentication.getName());
		secLoginlog.setLoginTime(new Timestamp(System.currentTimeMillis()));
		secLoginlog.setLoginIP(getRemoteAddress());
		secLoginlog.setLoginBrowserType(getBrowser());
		secLoginlog.setLoginSessionID(getSessionId());

		return secLoginlog;
	}

	@Autowired
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setActiveDirectoryLdapAuthenticationProviderAdapter(
			ActiveDirectoryLdapAuthenticationProviderAdapter defaultLdapAuthenticationProviderAdapter) {
		this.defaultLdapAuthenticationProviderAdapter = defaultLdapAuthenticationProviderAdapter;
	}

	@Autowired
	public void setDaoAuthenticationProvider(DaoAuthenticationProvider daoAuthenticationProvider) {
		this.daoAuthenticationProvider = daoAuthenticationProvider;
	}

	@Qualifier(value = "externalAuthenticationProvider")
	@Autowired(required = false)
	public void setExternalAuthenticationProvider(ExternalAuthenticationProvider externalAuthenticationProvider) {
		this.externalAuthenticationProvider = externalAuthenticationProvider;
	}

	@Autowired
	public void setSecurityUsersDAO(SecurityUserDAO securityUsersDAO) {
		this.securityUsersDAO = securityUsersDAO;
	}

	@Autowired(required = false)
	public void setSamlAuthenticationProvider(SAMLAuthenticationProvider samlAuthenticationProvider) {
		this.samlAuthenticationProvider = samlAuthenticationProvider;
	}
}
