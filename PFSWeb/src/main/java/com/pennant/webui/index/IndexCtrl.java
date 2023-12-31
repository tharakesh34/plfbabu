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
 * FileName : IndexCtl.java *
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
package com.pennant.webui.index;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.security.UserType;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.lic.constant.LicenseError;
import com.pennanttech.pennapps.lic.exception.LicenseException;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.security.core.otp.OTPStatus;

/**
 * This is the controller class for the /WEB-INF/pages/index.zul file.
 */
public class IndexCtrl<T> extends GFCBaseCtrl<T> {
	private static final long serialVersionUID = -3407055074703929527L;
	private static final Logger logger = LogManager.getLogger(IndexCtrl.class);

	private static final int CONTENT_AREA_HEIGHT_OFFSET = 92;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Menubar mainMenuBar;
	protected Label label_AppName;
	protected Image imgsmallLogo;
	protected Intbox currentDesktopHeight;
	protected Intbox currentDesktopWidth;

	private boolean homePageDisplayed = false;

	public IndexCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void doBeforeCompose(Window comp) {
		try {
			super.doBeforeComposeChildren(comp);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		Events.postEvent("onClientInfo", comp, Events.ON_CLIENT_INFO);
	}

	public void onCreate$outerIndexWindow(Event event) {
		logger.debug("Entering");

		this.label_AppName.setValue(App.NAME);

		if (App.NAME.contains("Lending")) {
			this.imgsmallLogo.setSrc("/images/plf_product_logo.png");
		} else {
			this.imgsmallLogo.setSrc("/images/pff_product_logo");
		}

		LoggedInUser user = getUserWorkspace().getLoggedInUser();
		logger.info("User Name: " + user.getUserName());

		if (PennantConstants.YES.equals(SysParamUtil.getValueAsString("LAST_LOGIN_INFO"))) {
			EventQueues.lookup("lastLoginEventQueue", EventQueues.DESKTOP, true)
					.publish(new Event("onChangeLastLogin", null, ""));
		}

		logger.debug("Leaving");
	}

	/**
	 * Gets the current desktop height and width and <br>
	 * stores it in the UserWorkspace properties. <br>
	 * We use these values for calculating the count of rows in the listboxes. <br>
	 * 
	 * @param event
	 */
	public void onClientInfo(ClientInfoEvent event) {
		currentDesktopHeight.setValue(event.getDesktopHeight() - CONTENT_AREA_HEIGHT_OFFSET);
		currentDesktopWidth.setValue(event.getDesktopWidth());

		LoggedInUser user = getUserWorkspace().getLoggedInUser();

		if ("Y".equalsIgnoreCase(App.getProperty("two.factor.authentication.required"))) {
			org.zkoss.zk.ui.Session session = Executions.getCurrent().getDesktop().getSession();

			if (session.getAttribute(OTPStatus.VERIFIED.name()) == null) {

				if (StringUtils.isEmpty(user.getMobileNo()) && StringUtils.isEmpty(user.getEmailId())) {
					MessageUtil
							.showError("Mobile Number/Email-ID not exists, please contact the system administrator.");
					Executions.sendRedirect("loginDialog.zul");
				}

				Map<String, Object> map = new HashedMap<>();
				map.put("user", user);

				Executions.createComponents("/pages/otp.zul", null, map);
			}
		}

		try {
			License.userLogin();
		} catch (LicenseException e) {
			LicenseError licenseError = LicenseError.valueOf(e.getErrorCode());
			if (LicenseError.LIC001 == licenseError || LicenseError.LIC003 == licenseError) {
				if (UserType.valueOf(user.getUserType()) == UserType.ADMIN
						&& getUserWorkspace().isAllowed("menuItem_License_LicenseUpload")) {
					Map<String, String> arg = new HashedMap<>();
					arg.put("origin", "LoginPage");
					Executions.createComponents("~./pages/lic/LicenseUpload.zul", null, arg);
				} else {
					Executions.sendRedirect("default-error.jsp");
				}
			} else {
				Executions.sendRedirect("default-error.jsp");
			}
		}

		createMainTreeMenu();
	}

	/**
	 * Returns the spring-security managed logged in user.<br>
	 */
	public String doGetLoggedInUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/**
	 * Creates the MainMenu as TreeMenu as default. <br>
	 */
	private void createMainTreeMenu() {

		// get an instance of the borderlayout defined in the index.zul-file
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");

		// get an instance of the searched west layout area
		final West west = bl.getWest();
		// clear the center child comps
		west.getChildren().clear();

		Map<String, String> map = new HashMap<>();
		if (homePageDisplayed) {
			map.put("HomePageDisplayed", "YES");
		} else {
			homePageDisplayed = true;
			map.put("HomePageDisplayed", "NO");
		}

		// create the components from the mainmenu.zul-file and put
		// it in the west layout area
		Executions.createComponents("/WEB-INF/pages/mainMenu.zul", west, map);
	}

	/**
	 * Shows the welcome page in the borderlayouts CENTER area.<br>
	 * 
	 * @throws InterruptedException
	 */
	public void showWelcomePage() throws InterruptedException {
		// get an instance of the borderlayout defined in the zul-file
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		// get an instance of the searched CENTER layout area
		final Center center = bl.getCenter();
		// clear the center child comps
		center.getChildren().clear();
		// call the zul-file and put it in the center layout area
		Executions.createComponents("/WEB-INF/pages/welcome.zul", center, null);
	}
}
