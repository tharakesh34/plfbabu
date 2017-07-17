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
 * FileName    		:  LoginDialogCtl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    : 05-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.login;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.webui.util.WindowBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/loginDialog.zul file.
 */
public class LoginDialogCtrl extends WindowBaseCtrl {
	private static final long	serialVersionUID	= -71422545405325060L;
	private static final Logger	logger				= Logger.getLogger(LoginDialogCtrl.class);

	protected Window			loginwin;
	protected Textbox			txtbox_Username;
	protected Textbox			txtbox_Password;
	protected Button			btnReset;
	protected Textbox			txtbox_randomKey;

	/**
	 * Default constructor.
	 */
	public LoginDialogCtrl() {
		super();
	}

	public void onCreate$loginwin(Event event) throws Exception {
		logger.info(Literal.ENTERING);

		// Invalidate the session if one exists.
		try {
			SessionUserDetails.getLogiedInUser();
			Sessions.getCurrent().invalidate();
		} catch (Exception e) {
			// No session available.
		}

		this.txtbox_Username.focus();

		String randomKey = "";
		try {
			randomKey = (String) Sessions.getCurrent().getAttribute("SATTR_RANDOM_KEY");
		} catch (Exception ex) {
			logger.warn("Unable to get session attribute 'SATTR_RANDOM_KEY':", ex);
		}

		txtbox_randomKey.setValue(randomKey);
		logger.info(Literal.LEAVING);
	}

	/**
	 * when clicks on "reset" button
	 * 
	 * @param event
	 */
	public void onClick$btnReset(Event event) {
		Executions.sendRedirect("loginDialog.zul");
	}
}
