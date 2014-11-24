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

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.West;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.policy.model.UserImpl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTDateFormat;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/index.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class IndexCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3407055074703929527L;
	private final static Logger logger = Logger.getLogger(IndexCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Menubar mainMenuBar; // autowired
	protected Label label_AppName; // autowired
	protected Image imgsmallLogo;
	
	protected Intbox currentDesktopHeight; // autowired
	protected Intbox currentDesktopWidth; // autowired
	protected Checkbox CBtreeMenu; // autowired

	private final int centerAreaHeightOffset = 50;
	private boolean homePageDisplayed = false;

	private final String appName = Labels.getLabel(PennantConstants.applicationCode);

	public IndexCtrl() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public void doBeforeCompose(Component comp){
		try {
			super.doBeforeComposeChildren(comp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Events.postEvent("onClientInfo", comp, Events.ON_CLIENT_INFO);
		
	}
	
	public void onCreate$outerIndexWindow(Event event) throws Exception {
		//this.mainMenuBar.setVisible(false);

		//createMainTreeMenu();
		this.label_AppName.setValue(this.appName);
		
		if (StringUtils.trimToEmpty(appName).contains("Loan")) {
			this.imgsmallLogo.setSrc("/images/Pennant/PLFConvSmallLogo.png");
		}

		/**
		 * public Day(int day, int month, int year)Constructs a new one day time
		 * period.
		 * 
		 * Parameters:day - the day-of-the-month.month - the month (1 to 12).
		 * year - the year (1900 <= year <= 9999).
		 */
		final Date date = DateUtility.getUtilDate("08/11/2010", PennantConstants.dateFormat);

		final String zkVersion = doGetZkVersion();
		final String appVersion = this.appName + " v5.0.409 / " + PTDateFormat.getDateFormater().format(date);

		final String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		final String loginDate = DateUtility.formatDate(DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString()),
				PennantConstants.dateFormat).toString();
		final String loginTime = PennantAppUtil.getTime(new Timestamp(System.currentTimeMillis())).toString();
		final String branchCode = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecurityUser().getUsrBranchCode();
		final String departmentCode = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecurityUser().getUsrDeptCode();
		
		final String version = zkVersion + " | " + appVersion;
		final String tableSchemaName = "public";
		
		EventQueues.lookup("loginTimeEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeLoginTime", null, loginTime));
		EventQueues.lookup("loginDateEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeLoginDate", null, loginDate));
		EventQueues.lookup("userNameEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeUser", null, userName));
		EventQueues.lookup("branchCodeEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeBranch", null, branchCode));
		EventQueues.lookup("departmentCodeEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangedepartment", null, departmentCode));
		EventQueues.lookup("appVersionEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeAppVersion", null, version));
		EventQueues.lookup("tableSchemaEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeTableSchema", null, tableSchemaName));
		
		if(PennantConstants.YES.equals(SystemParameterDetails.getSystemParameterValue("LAST_LOGIN_INFO"))) {
			EventQueues.lookup("lastLoginEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeLastLogin", null, ""));
		}

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
		setCurrentDesktopHeight(event.getDesktopHeight() - this.centerAreaHeightOffset);
		setCurrentDesktopWidth(event.getDesktopWidth());
		createMainTreeMenu();
	}

	/**
	 * Returns the used ZK framework version and build number.<br>
	 * 
	 * @return
	 */
	private String doGetZkVersion() {

		final String version = Executions.getCurrent().getDesktop().getWebApp().getVersion();
		final String build = Executions.getCurrent().getDesktop().getWebApp().getBuild();
		return "ZK " + version + " EE" + " / build : " + build;
	}

	/**
	 * Returns the spring-security managed logged in user.<br>
	 */
	public String doGetLoggedInUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/**
	 * When the 'Logout' button is clicked.<br>
	 * 
	 * @throws IOException
	 */
	public void onClick$btnLogout() throws IOException {
		// logger.debug(event.toString());

		getUserWorkspace().doLogout(); // logout.
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
		Executions.createComponents("/WEB-INF/pages/mainTreeMenu.zul", west, map);
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

	/**
	 * When the 'My Settings' toolbarButton is clicked.<br>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void onClick$btnIndexMySettings() throws IOException, InterruptedException {
		showPage("/WEB-INF/pages/sec_user/userSettings.zul", "UserSettings");
	}

	/**
	 * When the 'Configuration' toolbarButton is clicked.<br>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void onClick$btnIndexUserAppConfiguration() throws IOException, InterruptedException {
		showPage("/WEB-INF/pages/sec_user/userAppConfiguration.zul", "Configuration");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the
	 * borderlayout. Checks if the tab is opened before. If yes than it selects
	 * this tab.
	 * 
	 * @param zulFilePathName
	 *            The ZulFile Name with path.
	 * @param tabName
	 *            The tab name.
	 * @throws InterruptedException
	 */
	private void showPage(String zulFilePathName, String tabName) throws InterruptedException {

		try {
			// get the parameter for working with tabs from the application params

			/* get an instance of the borderlayout defined in the zul-file */
			final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
			/* get an instance of the searched CENTER layout area */
			final Center center = bl.getCenter();
			// get the tabs component
			final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter");

			/**
			 * Check if the tab is already opened than select them and<br>
			 * go out of here. If not than create them.<br>
			 */

			Tab checkTab = null;
			try {
				checkTab = (Tab) tabs.getFellow("tab_" + tabName.trim());
				checkTab.setSelected(true);
			} catch (final ComponentNotFoundException ex) {
				// Ignore if can not get tab.
			}

			if (checkTab == null) {

				final Tab tab = new Tab();
				tab.setId("tab_" + tabName.trim());
				tab.setLabel(tabName.trim());
				tab.setClosable(true);

				tab.setParent(tabs);

				final Tabpanels tabpanels = (Tabpanels) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter").getFellow("tabpanelsBoxIndexCenter");
				final Tabpanel tabpanel = new Tabpanel();
				tabpanel.setHeight("100%");
				tabpanel.setStyle("padding: 0px;");
				tabpanel.setParent(tabpanels);

				/**
				 * Create the page and put it in the tabs area. If zul-file
				 * is not found, detach the created tab
				 */
				try {
					Executions.createComponents(zulFilePathName, tabpanel, null);
					tab.setSelected(true);
				} catch (final Exception e) {
					tab.detach();
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("--> calling zul-file: " + zulFilePathName);
			}
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	public void setCurrentDesktopHeight(int desktopHeight) {
		if (isTreeMenu() == true) {
			this.currentDesktopHeight.setValue(Integer.valueOf(desktopHeight));
		} else {
			this.currentDesktopHeight.setValue(Integer.valueOf(desktopHeight - 30));
		}
	}

	public int getCurrentDesktopHeight() {
		return this.currentDesktopHeight.getValue().intValue();
	}

	public void setCurrentDesktopWidth(int currentDesktopWidth) {
		this.currentDesktopWidth.setValue(Integer.valueOf(currentDesktopWidth));
	}

	public int getCurrentDesktopWidth() {
		return this.currentDesktopWidth.getValue().intValue();
	}

	public void setTreeMenu(boolean treeMenu) {
		this.CBtreeMenu.setChecked(treeMenu);
	}

	public boolean isTreeMenu() {
		return this.CBtreeMenu.isChecked();
	}

}
