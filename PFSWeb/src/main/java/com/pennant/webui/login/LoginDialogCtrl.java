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
 * FileName : LoginDialogCtl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 05-08-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.login;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.webui.util.WindowBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.security.user.AuthenticationError;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.lic.exception.LicenseException;
import com.pennapps.web.security.GoogleCaptcha;

/**
 * This is the controller class for the /WEB-INF/loginDialog.zul file.
 */
public class LoginDialogCtrl extends WindowBaseCtrl {
	private static final long serialVersionUID = -71422545405325060L;
	private static final Logger logger = LogManager.getLogger(LoginDialogCtrl.class);

	protected Window loginwin;
	protected Textbox txtbox_Username;
	protected Textbox txtbox_Password;
	protected Button btnReset;
	protected Textbox txtbox_randomKey;

	protected Div licenceMessageRow;
	protected Label licenceMessage;
	protected A licenceMessageIcon;
	protected Label copyRight;
	protected A copyRightInfo;

	protected Label unauthorizedMessage;
	protected Div unauthorizedMessageRow;

	protected Div recaptcha;

	protected Div loginError;
	protected Label loginErrorMsg;

	/**
	 * Default constructor.
	 */
	public LoginDialogCtrl() {
		super();
	}

	public void onCreate$loginwin(Event event) {
		logger.info(Literal.ENTERING);

		String disclaimerMessage = Labels.getLabel("label_LoginDialog_disclaimer", new String[] { "" });

		if (!StringUtils.isBlank(disclaimerMessage)) {
			this.unauthorizedMessageRow.setVisible(true);
			unauthorizedMessage.setValue(disclaimerMessage);
		}

		setLicenceMessage();

		if (GoogleCaptcha.isRequired()) {
			this.recaptcha.setVisible(true);
			recaptcha.setClientAttribute("data-sitekey", GoogleCaptcha.getSiteKey());
			recaptcha.setClientAttribute("data-callback", "onSuccess");
		}

		try {
			SessionUserDetails.getLogiedInUser();
			Sessions.getCurrent().invalidate();
		} catch (Exception e) {
			logger.trace("No session available.", e);
		}

		this.txtbox_Username.focus();

		String randomKey = "";
		try {
			randomKey = (String) Sessions.getCurrent().getAttribute("SATTR_RANDOM_KEY");
		} catch (Exception ex) {
			logger.warn("Unable to get session attribute 'SATTR_RANDOM_KEY':", ex);
		}

		txtbox_randomKey.setValue(randomKey);

		String errorMsg = Executions.getCurrent().getParameter("login_error");

		if (errorMsg == null) {
			loginError.setVisible(false);

			logger.info(Literal.LEAVING);

			return;
		}

		loginError.setVisible(true);

		if ("1".equals(errorMsg)) {
			loginErrorMsg.setValue(AuthenticationError.DEFAULT.message());
		} else {
			loginErrorMsg.setValue(errorMsg);
		}

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
		Map<String, String> arg = new HashedMap<>();

		if (App.NAME.contains("Lending")) {
			arg.put("productLogo", "images/plf_product_logo.png");
		} else {
			arg.put("productLogo", "images/pff_product_logo");
		}

		arg.put("productLogo", "images/plf_product_logo.png");
		Executions.createComponents("~./pages/lic/CopyRight.zul", this, arg);
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

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
