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
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.webui.util.WindowBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.lic.exception.LicenseException;

/**
 * This is the controller class for the /WEB-INF/loginDialog.zul file.
 */
public class LoginDialogCtrl extends WindowBaseCtrl {
	private static final long	serialVersionUID	= -71422545405325060L;
	private static final Logger	logger				= Logger.getLogger(LoginDialogCtrl.class);

	protected Window loginwin;
	protected Textbox txtbox_Username;
	protected Textbox txtbox_Password;
	protected Button btnReset;
	protected Textbox txtbox_randomKey;
	
	protected Row licenceMessageRow;
	protected Label licenceMessage;
	protected A licenceMessageIcon;
	protected Label copyRight;
	protected A copyRightInfo;
	

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
		setLicenceMessage();		
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
	
	/**
	 * when clicks on "copyrightInfo" hyper link
	 * 
	 * @param event
	 */
	public void onClick$copyRightInfo(Event event) {
		Executions.createComponents("~./pages/CopyRight.zul", this, null);
	}
	
	
	public void setLicenceMessage() {
		
		boolean licenseFound = false;
		try {
			License.validateLicense();
			licenseFound = true;
		} catch (LicenseException e) {
			licenceMessage.setValue(e.getErrorMessage());
			licenceMessageIcon.setIconSclass("z-icon-exclamation-triangle");
			licenceMessageRow.setVisible(true);
			copyRightInfo.setVisible(false);
		}

		if (licenseFound && License.getWarningMessage(false) != null) {
			licenceMessage.setValue(License.getWarningMessage(false));
			licenceMessageIcon.setIconSclass("z-icon-warning");
			licenceMessageRow.setVisible(true);
			copyRight.setValue(License.getCopyRight());
		}

		if (License.getCopyRight() != null) {
			copyRight.setValue(License.getCopyRight());
		} else {
			copyRight.setValue(App.getVersion());
		}

	}
	
	
}
