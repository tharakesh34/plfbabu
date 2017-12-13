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
 * FileName    		:  IndexCtl.java														*                           
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
package com.pennant.webui.index;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
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
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;

/**
 * This is the controller class for the /WEB-INF/pages/index.zul file.
 */
public class IndexCtrl<T> extends GFCBaseCtrl<T> {
	private static final long	serialVersionUID			= -3407055074703929527L;
	private static final Logger	logger						= Logger.getLogger(IndexCtrl.class);

	private static final int	CONTENT_AREA_HEIGHT_OFFSET	= 92;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Menubar			mainMenuBar;													// autowired
	protected Label				label_AppName;													// autowired
	protected Image				imgsmallLogo;
	protected Intbox			currentDesktopHeight;											// autowired
	protected Intbox			currentDesktopWidth;											// autowired

	private boolean				homePageDisplayed			= false;

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

	public void onCreate$outerIndexWindow(Event event) throws Exception {
		logger.debug("Entering");

		this.label_AppName.setValue(App.NAME);

		if (App.NAME.contains("Lending")) {
			this.imgsmallLogo.setSrc("/images/plf_logo.png");
		} else {
			this.imgsmallLogo.setSrc("/images/pff_logo.png");
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
	 * @throws Exception
	 */
	public void onClientInfo(ClientInfoEvent event) throws Exception {
		logger.debug("Entering");
		currentDesktopHeight.setValue(event.getDesktopHeight() - CONTENT_AREA_HEIGHT_OFFSET);
		currentDesktopWidth.setValue(event.getDesktopWidth());

		createMainTreeMenu();
		logger.debug("Leaving");
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

		Map<String, String> map = new HashMap<String, String>();
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
