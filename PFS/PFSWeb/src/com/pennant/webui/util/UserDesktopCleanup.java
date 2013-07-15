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
 * FileName    		:  UserDesktopCleanup.java										        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-09-2012															*
 *                                                                  
 * Modified Date    :  26-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  26-09-2012	    Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                  9                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.util;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;

import com.pennant.app.util.SessionUtil;
import com.pennant.webui.index.IndexCtrl;

public class UserDesktopCleanup  implements DesktopCleanup{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6585204252879222073L;
	private final static Logger logger = Logger.getLogger(IndexCtrl.class);
	@Override
	public void cleanup(Desktop arg0) throws Exception {
		logger.debug("Entering");
		if(arg0.getRequestPath().equals("/pages/index.zul")){
			Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
			WebAuthenticationDetails details = (WebAuthenticationDetails)currentUser.getDetails();
			//Set DeskTop Active is false because Session is Active but index.zul is not in open 
			SessionUtil.getActiveDeskTopsMap().put(currentUser.getName()+"-"+details.getSessionId(),false);
		}
		logger.debug("Leaving");
	}
}
