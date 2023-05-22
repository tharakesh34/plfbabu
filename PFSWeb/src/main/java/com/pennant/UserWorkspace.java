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
 * FileName : UserWorkspace.java *
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
package com.pennant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennanttech.framework.security.core.AuthenticationManager;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.lic.exception.LicenseException;

/**
 * Workspace for the user. One workspace per userSession. <br>
 * <br>
 * Every logged in user have his own workspace. <br>
 * Here are stored several properties for the user. <br>
 * <br>
 * 1. Access the rights that the user have. <br>
 * 2. The office for that the user are logged in. <br>
 */
public class UserWorkspace extends com.pennanttech.pennapps.web.session.UserWorkspace {
	private static final long serialVersionUID = -3936210543827830197L;
	private static final Logger logger = LogManager.getLogger(UserWorkspace.class);

	private User userDetails;
	private Map<String, String> hasMenuRights;
	private Set<String> grantedAuthoritySet = null;
	private LoggedInUser loggedInUser;
	private Set<String> userRoleSet = new HashSet<>();
	private Map<String, Integer> accessType = null;
	private Collection<GrantedAuthority> grantedAuthorities;
	private List<SecurityUserDivBranch> divisionBranches;
	private List<SecurityRole> securityRoles;
	private Map<String, Collection<SecurityRight>> rightsMap = new HashMap<>();

	@Autowired
	private transient UserService userService;

	/**
	 * Default Constructor
	 */
	@SuppressWarnings("deprecation")
	public UserWorkspace() {
		super(AuthenticationManager.getLoggedInUser().getUserId(),
				AuthenticationManager.getLoggedInUser().getLanguage());

		this.loggedInUser = AuthenticationManager.getLoggedInUser();
		this.grantedAuthorities = AuthenticationManager.getGrantedAuthorities();
		this. securityRoles = AuthenticationManager.getSecurityRoles();

		Sessions.getCurrent().setAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE,
				org.zkoss.util.Locales.getLocale(loggedInUser.getLanguage()));
		org.zkoss.util.Locales.setThreadLocal(org.zkoss.util.Locales.getLocale(loggedInUser.getLanguage()));

		// speed up the ModalDialogs while disabling the animation
		Window.setDefaultActionOnShow("");
	}

	/**
	 * Logout with the spring-security logout action-URL.<br>
	 * Therefore we make a sendRedirect() to the logout uri we have configured in the spring-config.
	 */
	@Override
	public void doLogout() {
		try {
			License.userLogout();
		} catch (LicenseException e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public void destroy() {
		userService.logLogOut(loggedInUser.getLoginLogId());
		logger.info("{} logged out at {}.", loggedInUser.getUserName(), DateUtil.getSysDate(DateFormat.LONG_DATE_TIME));
	}

	@Override
	public String getUserLanguage() {
		return getLoggedInUser().getLanguage();
	}

	/**
	 * Copied the grantedAuthorities to a Set of strings <br>
	 * for a faster searching in it.
	 * 
	 * @return String set of GrantedAuthorities (rightNames)
	 */
	public Set<String> getGrantedAuthoritySet() {
		if (this.grantedAuthoritySet == null) {
			Authentication currentUser = AuthenticationManager.getAuthentication();

			userDetails = (User) currentUser.getPrincipal();
			grantedAuthoritySet = new HashSet<>(grantedAuthorities.size());

			for (final GrantedAuthority grantedAuthority : grantedAuthorities) {
				grantedAuthoritySet.add(grantedAuthority.getAuthority());
			}

			for (SecurityRole role : securityRoles) {
				userRoleSet.add(role.getRoleCd());
			}
		}

		return grantedAuthoritySet;
	}

	/**
	 * Checks if a right is in the <b>granted rights</b> that the logged in user have. <br>
	 * 
	 * @param rightName
	 * @return false, if the right is in the granted user rights.<br>
	 *         true, if the right is not granted to the user.<br>
	 */
	public boolean isNotAllowed(String rightName) {
		return !isAllowed(rightName);
	}

	/**
	 * Checks if a right is in the <b>granted rights</b> that the logged in user have. <br>
	 * 
	 * @param rightName
	 * @return true, if the right is in the granted user rights.<br>
	 *         false, if the right is not granted to the user.<br>
	 */
	public boolean isAllowed(String rightName) {
		return getGrantedAuthoritySet().contains(rightName);
	}

	public boolean isReadOnly(String rightName) {
		return !isAllowed(rightName);

	}

	/**
	 * 
	 * @param rightName
	 * @return -1, if the user has NotVisability Right.<br>
	 *         0, if the user has ReadOnly Right Difficult value.<br>
	 *         1, if the user has Edit Right.<br>
	 */
	public int getAccessType(String rightName) {

		if (rightName == null) {
			return 0;
		}

		if (accessType != null && accessType.containsKey(rightName)) {
			return accessType.get(rightName);
		}

		return 0;
	}

	public boolean isRoleContains(String roleName) {
		return getUserRoleSet().contains(roleName);
	}

	public void allocateAuthorities(String page) {
		allocateAuthorities(page, null, null);
	}

	public void allocateAuthorities(String page, String roleCode) {
		allocateAuthorities(page, roleCode, null);
	}

	public void allocateAuthorities(String page, String roleCode, String menuRightName) {
		final Collection<SecurityRight> rights = getSecurityRights(page, roleCode, menuRightName);

		for (final SecurityRight right : rights) {
			String rightName = right.getRightName();
			if (!getGrantedAuthoritySet().contains(rightName)) {
				this.grantedAuthoritySet.add(rightName);
			}
		}
	}

	private Collection<SecurityRight> getSecurityRights(String page, String roleCode, String menuRightName) {
		StringBuilder rightKey = new StringBuilder();
		rightKey.append(page);

		if (StringUtils.isNotBlank(roleCode)) {
			rightKey.append('@');
			rightKey.append(roleCode);
		}

		if (StringUtils.isNotBlank(menuRightName)) {
			rightKey.append('@');
			rightKey.append(menuRightName);
		}

		if (!rightsMap.containsKey(rightKey.toString())) {
			SecurityRight secRight = new SecurityRight();
			secRight.setLoginAppId(App.ID);
			secRight.setLoginAppCode(App.CODE);
			secRight.setUsrID(loggedInUser.getUserId());
			secRight.setRoleCd(roleCode);
			secRight.setPage(page);
			secRight.setMenuRight(menuRightName);
			Collection<SecurityRight> rights = userService.getPageRights(secRight);
			if (!rights.isEmpty()) {
				rightsMap.put(rightKey.toString(), rights);
			}
			return rights;
		}

		return rightsMap.get(rightKey.toString());
	}

	public void deAllocateAuthorities(String page) {
		Set<String> tempAuthoritySet = new HashSet<>();
		Object[] object = grantedAuthoritySet.toArray();
		for (int i = 0; i < object.length; i++) {
			if (!object[i].toString().contains(page) || object[i].toString().contains("menu")) {
				tempAuthoritySet.add(object[i].toString());
			}
		}
		this.grantedAuthoritySet = tempAuthoritySet;
	}

	public void allocateRoleAuthorities(String roleCode, String page) {
		allocateAuthorities(page, roleCode, null);
	}

	public void allocateMenuRoleAuthorities(String roleCode, String page, String menuRightName) {
		allocateAuthorities(page, roleCode, menuRightName);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Map<String, String> getHasMenuRights() {
		return hasMenuRights;
	}

	public void setHasMenuRights(Map<String, String> hasMenuRights) {
		this.hasMenuRights = hasMenuRights;
	}

	public void setHasMenuRights(String menuId, String menuRight) {
		if (this.hasMenuRights == null) {
			this.hasMenuRights = new HashMap<>();
		}
		this.hasMenuRights.put(menuId, menuRight);
	}

	public LoggedInUser getLoggedInUser() {
		return loggedInUser;
	}

	public List<String> getUserRoles() {
		List<String> arrayRoleCode = new ArrayList<>();

		Object[] object = this.userRoleSet.toArray();

		for (int i = 0; i < object.length; i++) {
			arrayRoleCode.add(object[i].toString());
		}
		return arrayRoleCode;
	}

	public Set<String> getUserRoleSet() {
		return userRoleSet;
	}

	public void setUserRoleSet(Set<String> userRoleSet) {
		this.userRoleSet = userRoleSet;
	}

	public User getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(User userDetails) {
		this.userDetails = userDetails;
	}

	public List<SecurityUserDivBranch> getDivisionBranches() {
		return divisionBranches;
	}

	public void setDivisionBranches(List<SecurityUserDivBranch> divisionBranches) {
		this.divisionBranches = divisionBranches;
	}

	public List<SecurityRole> getSecurityRoles() {
		return securityRoles;
	}
}
