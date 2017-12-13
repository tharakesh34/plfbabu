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
 * FileName    		:  UserWorkspace.java													*                           
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
package com.pennant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

import com.pennant.app.util.FinanceWorkflowRoleUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennanttech.framework.security.core.AuthenticationManager;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.App;

/**
 * Workspace for the user. One workspace per userSession. <br>
 * <br>
 * Every logged in user have his own workspace. <br>
 * Here are stored several properties for the user. <br>
 * <br>
 * 1. Access the rights that the user have. <br>
 * 2. The office for that the user are logged in. <br>
 */
public class UserWorkspace implements Serializable, DisposableBean {
	private static final long serialVersionUID = -3936210543827830197L;
	private static final Logger logger = Logger.getLogger(UserWorkspace.class);

	private User userDetails;
	private Map<String, String> hasMenuRights;
	private Set<String> grantedAuthoritySet = null;
	private LoggedInUser loggedInUser;
	private Set<String> userRoleSet = new HashSet<String>();
	private HashMap<String, Integer> accessType = null;
	private Collection<GrantedAuthority> grantedAuthorities;
	private List<SecurityUserDivBranch> divisionBranches;
	private List<SecurityRole> securityRoles;
	private HashMap<String, Collection<SecurityRight>> rightsMap = new HashMap<>();
	
	@Autowired
	private transient UserService userService;
	
	/**
	 * Default Constructor
	 */
	@SuppressWarnings("deprecation")
	public UserWorkspace() {
		loggedInUser = AuthenticationManager.getLoggedInUser();
		grantedAuthorities = AuthenticationManager.getGrantedAuthorities();
		securityRoles = AuthenticationManager.getSecurityRoles();

		Sessions.getCurrent().setAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE,
				org.zkoss.util.Locales.getLocale(loggedInUser.getUsrLanguage()));
		org.zkoss.util.Locales.setThreadLocal(org.zkoss.util.Locales.getLocale(loggedInUser.getUsrLanguage()));

		// speed up the ModalDialogs while disabling the animation
		Window.setDefaultActionOnShow("");
	}

	/**
	 * Logout with the spring-security logout action-URL.<br>
	 * Therefore we make a sendRedirect() to the logout uri we have configured in the spring-config.
	 */
	public void doLogout() {
		Executions.sendRedirect("/csrfLogout.zul");
	}

	/**
	 * Copied the grantedAuthorities to a Set of strings <br>
	 * for a faster searching in it.
	 * 
	 * @return String set of GrantedAuthorities (rightNames)
	 */
	public Set<String> getGrantedAuthoritySet() {
		if (this.grantedAuthoritySet == null) {
			Authentication currentUser = SessionUserDetails.getAuthentication();

			userDetails = (User) currentUser.getPrincipal();
			grantedAuthoritySet = new HashSet<String>(grantedAuthorities.size());

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

	@Override
	public void destroy() {
		userService.logLogOut(loggedInUser.getLoginLogId());
		logger.debug(loggedInUser.getUserName() + " logged out.");
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
			if (!getGrantedAuthoritySet().contains(right.getRightName())) {
				this.grantedAuthoritySet.add(right.getRightName());
			}
		}
	}

	private Collection<SecurityRight> getSecurityRights(String page, String roleCode, String menuRightName){
		StringBuffer rightKey= new StringBuffer();
		rightKey.append(page);
		
		if (StringUtils.isNotBlank(roleCode)) {
			rightKey.append("@");
			rightKey.append(roleCode);
		}
		
		if (StringUtils.isNotBlank(menuRightName)) {
			rightKey.append("@");
			rightKey.append(menuRightName);
		}
		
		if(!rightsMap.containsKey(rightKey.toString())){
			SecurityRight secRight = new SecurityRight();
			secRight.setLoginAppId(App.ID);
			secRight.setLoginAppCode(App.CODE);
			secRight.setUsrID(loggedInUser.getLoginUsrID());
			secRight.setRoleCd(roleCode);
			secRight.setPage(page);
			secRight.setMenuRight(menuRightName);
			Collection<SecurityRight> rights  = userService.getPageRights(secRight);
			rightsMap.put(rightKey.toString(), rights);
			return rights;
		}
		
		return rightsMap.get(rightKey.toString());
	}
	
	
	public void deAllocateAuthorities(String page) {
		Set<String> tempAuthoritySet = new HashSet<String>();
		Object[] object = grantedAuthoritySet.toArray();
		for (int i = 0; i < object.length; i++) {
			if (!object[i].toString().contains(page) || object[i].toString().contains("menu")) {
				tempAuthoritySet.add(object[i].toString());
			}
		}
		this.grantedAuthoritySet = tempAuthoritySet;
	}

	public void allocateRoleAuthorities(String roleCode, String page) {
		allocateAuthorities(page, roleCode,null);
	}

	public void allocateMenuRoleAuthorities(String roleCode, String page, String menuRightName) {
		allocateAuthorities(page, roleCode,menuRightName);
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
			this.hasMenuRights = new HashMap<String, String>();
		}
		this.hasMenuRights.put(menuId, menuRight);
	}

	public LoggedInUser getLoggedInUser() {
		return loggedInUser;
	}

	public ArrayList<String> getUserRoles() {
		ArrayList<String> arrayRoleCode = null;
			arrayRoleCode = new ArrayList<String>();
			Object[] object = this.userRoleSet.toArray();

			for (int i = 0; i < object.length; i++) {
				arrayRoleCode.add(object[i].toString());
			}
		return arrayRoleCode;
	}

	public ArrayList<String> getUserFinanceRoles(String[] moduleNames,String finEvent) {
		Set<String> finRoleSet = FinanceWorkflowRoleUtil.getFinanceRoles(moduleNames,finEvent);
		ArrayList<String> arrayRoleCode = new ArrayList<String>();;
		Object[] roles= this.userRoleSet.toArray();
		for (Object role : roles) {
			if(finRoleSet.contains(role.toString())){
				arrayRoleCode.add(role.toString());
			}
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

	public String getUserLanguage() {
		return getLoggedInUser().getUsrLanguage();
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
