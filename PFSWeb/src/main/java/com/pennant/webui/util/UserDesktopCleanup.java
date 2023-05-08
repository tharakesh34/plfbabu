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
 * FileName : UserDesktopCleanup.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-09-2012 *
 * 
 * Modified Date : 26-09-2012 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-09-2012 Pennant 0.1 * * * * 9 * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;

import com.pennant.app.util.SessionUtil;

public class UserDesktopCleanup implements DesktopCleanup {
	private static final Logger logger = LogManager.getLogger(UserDesktopCleanup.class);

	public UserDesktopCleanup() {
	    super();
	}

	@Override
	public void cleanup(Desktop arg0) throws Exception {
		logger.debug("Entering");
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ("/pages/index.zul".equals(arg0.getRequestPath())) {
			// WebAuthenticationDetails details = (WebAuthenticationDetails)currentUser.getDetails();
			// Set DeskTop Active is false because Session is Active but index.zul is not in open
			// SessionUtil.getActiveDeskTopsMap().put(currentUser.getName()+"-"+details.getSessionId(), false);
			SessionUtil.getActiveDeskTopsMap().put(currentUser.getName(), false);
		}

		logger.debug("Leaving");
	}
}
