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
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.service.LoginLoggingService;
import com.pennant.backend.service.UserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.policy.model.UserImpl;

/**
 * Workspace for the user. One workspace per userSession. <br>
 * <br>
 * Every logged in user have his own workspace. <br>
 * Here are stored several properties for the user. <br>
 * <br>
 * 1. Access the rights that the user have. <br>
 * 2. The office for that the user are logged in. <br>
 * 
 */
public class UserWorkspace implements Serializable, DisposableBean {

	private static final long serialVersionUID = -3936210543827830197L;
	private final static Logger logger = Logger.getLogger(UserWorkspace.class);

	private String userLanguage;
	private String browserType;

	private UserImpl userDetails;
	private transient UserService userService;
	private HashMap<String, String> hasMenuRights;

	private LoginLoggingService loginLoggingService;

	private Set<String> grantedAuthoritySet = null;
	private LoginUserDetails loginUserDetails;
	private Set<String> userRoleSet = null;
	private HashMap<String,Integer> accessType=null;

	private Set<String> roleRights=null;

	/**
	 * Get a logged-in users WorkSpace which holds all necessary vars. <br>
	 * 
	 * @return the users WorkSpace
	 */
	@Deprecated
	public static UserWorkspace getInstance() {
		return (UserWorkspace) SpringUtil.getBean("userWorkspace");
	}

	/**
	 * Default Constructor
	 */
	@SuppressWarnings("deprecation")
	public UserWorkspace() {
		if (logger.isDebugEnabled()) {
			logger.debug("create new Workspace [" + this + "]");
		}

		// speed up the ModalDialogs while disabling the animation
		Window.setDefaultActionOnShow("");
	}

	/**
	 * Logout with the spring-security logout action-URL.<br>
	 * Therefore we make a sendRedirect() to the logout uri we <br>
	 * have configured in the spring-config.br>
	 */
	public void doLogout() {
		//destroy();

		/* ++++++ Kills the Http session ++++++ */
		// HttpSession s = (HttpSession)
		// Sessions.getCurrent().getNativeSession();
		// s.invalidate();
		/* ++++++ Kills the zk session +++++ */
		// Sessions.getCurrent().invalidate();
		Executions.sendRedirect("/j_spring_logout");

	}

	/**
	 * Copied the grantedAuthorities to a Set of strings <br>
	 * for a faster searching in it.
	 * 
	 * @return String set of GrantedAuthorities (rightNames)
	 */
	@SuppressWarnings("unchecked")
	private Set<String> getGrantedAuthoritySet() {

		if (this.grantedAuthoritySet == null) {
			Authentication currentUser = SessionUserDetails.getAuthentication();
			this.userDetails = (UserImpl) currentUser.getPrincipal();

			loginUserDetails = SessionUserDetails.getUserDetails(userDetails);
			userLanguage = this.userDetails.getSecurityUser().getUsrLanguage();

			final Collection<GrantedAuthority> list = (Collection<GrantedAuthority>) currentUser.getAuthorities();
			this.grantedAuthoritySet = new HashSet<String>(list.size());

			for (final GrantedAuthority grantedAuthority : list) {
				this.grantedAuthoritySet.add(grantedAuthority.getAuthority());
			}

			final List<SecurityRole> secRoles=this.userDetails.getSecurityRole(); 

			userRoleSet = new HashSet<String>(secRoles.size());

			for (int i = 0; i < secRoles.size(); i++) {
				SecurityRole roles = secRoles.get(i); 
				userRoleSet.add(roles.getRoleCd());
			}

		}
		return this.grantedAuthoritySet;
	}

	/**
	 * Checks if a right is in the <b>granted rights</b> that the logged in user
	 * have. <br>
	 * 
	 * @param rightName
	 * @return true, if the right is in the granted user rights.<br>
	 *         false, if the right is not granted to the user.<br>
	 */
	public boolean isAllowed(String rightName) {
		return getGrantedAuthoritySet().contains(rightName);
	}

	public boolean isReadOnly(String rightName) {

		if (this.roleRights==null ||  !this.roleRights.contains(rightName)){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param rightName
	 * @return -1, if the user has NotVisability Right.<br>
	 *          0, if the user has ReadOnly Right Difficult value.<br>
	 *          1, if the user has Edit Right.<br>
	 */
	public int  getAccessType(String rightName) {

		if (rightName==null){
			return 0;
		}

		if(accessType!=null && accessType.containsKey(rightName)){
			return accessType.get(rightName);
		}

		return 0; 
	}

	public boolean isRoleContains(String roleName) {
		return getUserRoleSet().contains(roleName);
	}

	@Override
	public void destroy() {

		this.loginLoggingService.logLogOut(this.loginUserDetails.getLoginSessionID());
		if (logger.isDebugEnabled()) {
			logger.debug("destroy Workspace [" + this + "]");
		}
	}

	public void alocateAuthorities(String page) {

		SecurityRight secRight= new SecurityRight();
		secRight.setLoginAppCode(PennantConstants.applicationCode);
		secRight.setUsrID(userDetails.getSecurityUser().getUsrID());
		secRight.setRightName(page);

		final Collection<SecurityRight> rights = getUserService().getPageRights(secRight, null);	

		for (final SecurityRight right : rights) {
			if (!getGrantedAuthoritySet().contains(right.getRightName())){
				this.grantedAuthoritySet.add((right.getRightName()));	
			}
		}		
	}

	public void alocateAuthorities(String page,String roleCode) {

		SecurityRight secRight= new SecurityRight();
		secRight.setLoginAppCode(PennantConstants.applicationCode);
		secRight.setUsrID(userDetails.getSecurityUser().getUsrID());
		secRight.setRoleCd(roleCode);
		secRight.setRightName(page);

		final Collection<SecurityRight> rights = getUserService().getPageRights(secRight, null);	


		for (final SecurityRight right : rights) {
			if (!getGrantedAuthoritySet().contains(right.getRightName())){
				this.grantedAuthoritySet.add((right.getRightName()));	
			}
		}		
	}

	public void alocateAuthorities(String page,String roleCode, String menuRightName) {

		SecurityRight secRight= new SecurityRight();
		secRight.setLoginAppCode(PennantConstants.applicationCode);
		secRight.setUsrID(userDetails.getSecurityUser().getUsrID());
		secRight.setRoleCd(roleCode);
		secRight.setRightName(page);

		final Collection<SecurityRight> rights = getUserService().getPageRights(secRight, menuRightName);	


		for (final SecurityRight right : rights) {
			if (!getGrantedAuthoritySet().contains(right.getRightName())){
				this.grantedAuthoritySet.add((right.getRightName()));	
			}
		}		
	}

	public void deAlocateAuthorities(String page) {
		Set<String> tempAuthoritySet = new HashSet<String>();
		Object object[] =  grantedAuthoritySet.toArray();
		for (int i = 0; i <object.length; i++){
			if (!object[i].toString().contains(page) || object[i].toString().contains("menu")){
				tempAuthoritySet.add(object[i].toString());
			}
		}
		this.grantedAuthoritySet = tempAuthoritySet;
	}

	public void alocateRoleAuthorities(String roleCode,String page) {
		setRoleMenuAuthorities(roleCode, page, null);
	}
	
	public void alocateMenuRoleAuthorities(String roleCode,String page, String menuRightName) {
		setRoleMenuAuthorities(roleCode, page, menuRightName);
	}
	
	private void setRoleMenuAuthorities(String roleCode,String page, String menuRightName){
		SecurityRight secRight= new SecurityRight();
		secRight.setLoginAppCode(PennantConstants.applicationCode);
		secRight.setRightName(page);
		secRight.setRoleCd(roleCode);
		secRight.setUsrID(loginUserDetails.getLoginUsrID());
		final Collection<SecurityRight> rights = getUserService().getRoleRights(secRight, menuRightName);	

		if(this.roleRights==null){
			this.roleRights= new HashSet<String>(rights.size());
		}

		for (final SecurityRight right : rights) {
			if (!this.roleRights.contains(right.getRightName())){
				this.roleRights.add(right.getRightName());
			}
		}	
	}

	public void alocateRoleAuthorities(String page) {

		SecurityRight secRight= new SecurityRight();
		secRight.setLoginAppCode(PennantConstants.applicationCode);
		secRight.setRightName(page);
		secRight.setUsrID(loginUserDetails.getLoginUsrID());

		Object[] roles = getUserRoleSet().toArray();
		String[] userRoles =new String[roles.length];

		for (int i = 0; i < roles.length; i++) {
			userRoles[i] = (String) roles[i];
		}

		final Collection<SecurityRight> rights = getUserService().getRoleRights(secRight,userRoles);	

		if(this.roleRights==null){
			this.roleRights= new HashSet<String>(rights.size());
		}

		for (final SecurityRight right : rights) {
			if (!this.roleRights.contains(right.getRightName())){
				this.roleRights.add(right.getRightName());
			}
		}		
	}
	
	public void deAlocateRoleAuthorities(String page) {

		if (this.roleRights!=null){
			Set<String> tempAuthoritySet = new HashSet<String>();

			Object object[] =  this.roleRights.toArray();

			for (int i = 0; i <object.length; i++){
				if (!object[i].toString().contains(page)){
					tempAuthoritySet.add(object[i].toString());
				}
			}
			this.roleRights = tempAuthoritySet;
		}
	}	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public HashMap<String, String> getHasMenuRights() {
		return hasMenuRights;
	}
	public void setHasMenuRights(HashMap<String, String> hasMenuRights) {
		this.hasMenuRights = hasMenuRights;
	}
	public void setHasMenuRights(String menuId,String menuRight) {
		if (this.hasMenuRights==null){
			this.hasMenuRights= new HashMap<String, String>();
		}
		this.hasMenuRights.put(menuId, menuRight);
	}

	public LoginUserDetails getLoginUserDetails() {
		return loginUserDetails;
	}
	public ArrayList<String> getUserRoles() {
		ArrayList<String> arrayRoleCode=null;
		if (this.userRoleSet!=null){
			arrayRoleCode= new ArrayList<String>();
			Object[] object =  this.userRoleSet.toArray();

			for (int i = 0; i < object.length; i++) {
				arrayRoleCode.add(object[i].toString());
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

	public UserImpl getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(UserImpl userDetails) {
		this.userDetails = userDetails;
	}
	
	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}
	public String getBrowserType() {
		return browserType;
	}
	
	public UserService getUserService() {
		return this.userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}
	public String getUserLanguage() {
		return this.userLanguage;
	}
	
	public LoginLoggingService getLoginLoggingService() {
		return loginLoggingService;
	}
	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}

}
