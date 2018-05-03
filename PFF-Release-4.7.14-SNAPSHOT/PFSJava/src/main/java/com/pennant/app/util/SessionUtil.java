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
 *																							*
 * FileName    		:  CollectionManagerDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-10-2012    														*
 *                                                                  						*
 * Modified Date    :  20-10-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  20-10-2012      Pennant	                 0.1                                            * 
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
package com.pennant.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.session.SessionRegistryImpl;
import org.zkoss.zkplus.spring.SpringUtil;

import com.pennanttech.framework.security.core.User;

public class SessionUtil {
	private static Map<String,Boolean> activeDeskTopsMap = new HashMap<String, Boolean>();
	private static Map<String, Object> currentLoginUsersMap = new HashMap<String, Object>();
	
	private SessionUtil(){
		super();
	}
	
	public static Map<String,Object> getCurrentLoginUsers(){
		currentLoginUsersMap.clear();
		
		User userImpl = null;
		for(int i=0; i<getSessionRegistry().getAllPrincipals().size(); i++){
			userImpl =(User) getSessionRegistry().getAllPrincipals().get(i);
			currentLoginUsersMap.put(String.valueOf(userImpl.getSecurityUser().getUsrLogin()) ,getSessionRegistry().getAllPrincipals().get(i));
		}
		
		return currentLoginUsersMap;
	}
	
	public static SessionRegistryImpl getSessionRegistry() {
		return (SessionRegistryImpl)SpringUtil.getBean("sessionRegistry");
	}

	public static Map<String, Boolean> getActiveDeskTopsMap() {
		return activeDeskTopsMap;
	}
	
	public static List<User> getLoggedInUsers() {
		List<User> loggedInUsers = new ArrayList<User>();
		Map<String, Object> loginUsersMap = getCurrentLoginUsers();
		
		for (String userID : loginUsersMap.keySet()) {
			if (SessionUtil.getActiveDeskTopsMap().containsKey(userID)) {
				if (!SessionUtil.getActiveDeskTopsMap().get(userID).booleanValue()) {
					loggedInUsers.add((User) loginUsersMap.get(userID));
				}
			} else {
				loggedInUsers.add((User) loginUsersMap.get(userID));
			}
		}

		return loggedInUsers;
	}
}
