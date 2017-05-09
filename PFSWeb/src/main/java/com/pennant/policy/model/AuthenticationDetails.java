package com.pennant.policy.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.UserService;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.AuthenticationType;

import waffle.spring.WindowsAuthenticationToken;

public class AuthenticationDetails {
	private static final Logger logger = Logger.getLogger(AuthenticationDetails.class);

	private static UserService userService;

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static LoggedInUser getLogiedInUser() {
		Authentication currentUser = getAuthentication();
		LoggedInUser loggedInUser = null;

		if (currentUser == null) {
			return null;
		}

		if (currentUser.getClass().equals(WindowsAuthenticationToken.class)) {
			App.AUTH_TYPE  = AuthenticationType.SSO;
			String[] loginUser = currentUser.getName().split("\\\\");
			System.out.println("Login User " + loginUser[1]);
			SecurityUser user = getUserService().getUserByLogin(loginUser[1]);

			if (user == null) {
				user = new SecurityUser(0);
				user.setUsrLogin(loginUser[1]);
				loggedInUser = getUserDetails(user);
			} else {
				loggedInUser = getUserDetails(user);
			}

		} else {
			if (currentUser.getPrincipal().getClass().equals(String.class)) {
				return new LoggedInUser();
			}
			UserImpl userImpl = (UserImpl) currentUser.getPrincipal();
			loggedInUser = getUserDetails(userImpl.getSecurityUser());
			WebAuthenticationDetails details = (WebAuthenticationDetails) currentUser.getDetails();
			loggedInUser.setSessionId(details.getSessionId());
			loggedInUser.setLoginLogId(userImpl.getLoginId());
		}

		return loggedInUser;
	}

	public static Collection<GrantedAuthority> getGrantedAuthorities() {
		Collection<GrantedAuthority> grantedAuthorities = null;

		Authentication currentUser = getAuthentication();

		if (currentUser == null) {
			return null;
		}

		if (currentUser.getClass().equals(WindowsAuthenticationToken.class)) {
			App.AUTH_TYPE  = AuthenticationType.SSO;
			String[] loginUser = currentUser.getName().split("\\\\");
			SecurityUser user = getUserService().getUserByLogin(loginUser[1]);

			if (user == null) {
				grantedAuthorities = new ArrayList<GrantedAuthority>(0);
			} else {
				grantedAuthorities = getGrantedAuthority(user);
			}
		} else {
			if (currentUser.getPrincipal().getClass().equals(String.class)) {
				return new ArrayList<GrantedAuthority>();
			}
			UserImpl userImpl = (UserImpl) currentUser.getPrincipal();
			grantedAuthorities = userImpl.getAuthorities();
		}

		return grantedAuthorities;
	}

	public static List<SecurityRole> getSecurityRoles() {
		List<SecurityRole> roles = null;

		Authentication currentUser = getAuthentication();

		if (currentUser == null) {
			return null;
		}

		if (currentUser.getClass().equals(WindowsAuthenticationToken.class)) {
			App.AUTH_TYPE = AuthenticationType.SSO;
			String[] loginUser = currentUser.getName().split("\\\\");
			SecurityUser user = getUserService().getUserByLogin(loginUser[1]);

			if (user == null) {
				roles = new ArrayList<SecurityRole>();
			} else {
				roles = userService.getUserRolesByUserID(user.getUsrID());
			}
		} else {
			if (currentUser.getPrincipal().getClass().equals(String.class)) {
				return new ArrayList<SecurityRole>();
			}
			UserImpl userImpl = (UserImpl) currentUser.getPrincipal();
			roles = userImpl.getSecurityRole();
		}

		return roles;
	}

	private static Collection<GrantedAuthority> getGrantedAuthority(SecurityUser user) {
		logger.debug("Entering ");

		// get the list of rights for a specified user.
		final Collection<SecurityRight> rights = getUserService().getMenuRightsByUser(user);

		final ArrayList<GrantedAuthority> rechteGrantedAuthorities = new ArrayList<GrantedAuthority>(rights.size());

		// now create for all rights a GrantedAuthority
		// and fill the GrantedAuthority List with these authorities.
		for (final SecurityRight right : rights) {
			rechteGrantedAuthorities.add(new SimpleGrantedAuthority(right.getRightName()));
		}
		logger.debug("Leaving ");

		return rechteGrantedAuthorities;

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

		loggedInUser.setIpAddress(convertClientAddress());
		loggedInUser.setBrowserType(getBrowser());
		loggedInUser.setLogonTime(DateUtility.getTimestamp(new Date()));

		return loggedInUser;
	}

	public static String convertClientAddress() {
		try {
			Execution execution = Executions.getCurrent();
			return execution.getRemoteHost();
		} catch (ClassCastException e) {
			logger.warn("Exception: ", e);
			return "";
		}
	}

	public static String getBrowser() {
		Execution execution = Executions.getCurrent();
		String userAgent = execution.getHeader("User-Agent");
		String browserName = "unknown";
		String browserVer = "unknown";

		if (userAgent.contains("Chrome")) { // checking if Chrome
			String substring = userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0];
			browserName = substring.split("/")[0];
			browserVer = substring.split("/")[1];
		} else if (userAgent.contains("Firefox")) { // Checking if Firefox
			String substring = userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0];
			browserName = substring.split("/")[0];
			browserVer = substring.split("/")[1];
		} else if (userAgent.contains("MSIE")) { // Checking if Internet Explorer
			String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
			browserName = substring.split(" ")[0];
			browserVer = substring.split(" ")[1];
		}

		return browserName + "/" + browserVer;
	}

	private static UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		AuthenticationDetails.userService = userService;
	}
}
