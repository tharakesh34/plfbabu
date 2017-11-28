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
 * FileName    		:  MainMenuCtrl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  04-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.common.menu;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.pennant.UserWorkspace;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.WindowBaseCtrl;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.menu.MainMenu;
import com.pennanttech.pennapps.web.menu.MenuItem;
import com.pennanttech.pennapps.web.menu.TreeMenuBuilder;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * Controller for the main menu.
 */
public class MainMenuCtrl extends WindowBaseCtrl {
	private static final long serialVersionUID = -909795057747345551L;
	private static final Logger logger = Logger.getLogger(MainMenuCtrl.class);

	protected Window mainMenuWindow;
	protected Image expandAll;
	protected Image collapseAll;
	protected Textbox menuSearch;
	protected Button search;
	protected Tree mainMenu;

	private TreeMenuBuilder menuBuilder;
	private transient UserWorkspace userWorkspace;
	private transient UserService userService;

	/**
	 * Creates a new main menu controller.
	 */
	public MainMenuCtrl() {
		super();
	}

	/**
	 * Event listener that will be notified when the window created.
	 * 
	 * @param event
	 *            The event being received.
	 * @throws URISyntaxException
	 *             If the default navigate URL is not a valid URI.
	 */
	public void onCreate$mainMenuWindow(ForwardEvent event) throws URISyntaxException {
		logger.debug(Literal.ENTERING);

		// Get the event arguments.
		Map<?, ?> args = ((CreateEvent) event.getOrigin()).getArg();
		boolean navigateToDefaultPage = false;

		if (args.containsKey("HomePageDisplayed")) {
			navigateToDefaultPage = (String) args.get("HomePageDisplayed") == "NO" ? true : false;
		}

		// Create the menu.
		createMenu(navigateToDefaultPage);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Event listener that will be notified when the expand all clicked.
	 */
	public void onClick$expandAll() {
		menuBuilder.toggleAll(true);
		Clients.scrollIntoView(menuSearch);
	}

	/**
	 * Event listener that will be notified when the collapse all clicked.
	 */
	public void onClick$collapseAll() {
		menuBuilder.toggleAll(false);
	}

	/**
	 * Event listener that will be notified when the search button clicked.
	 */
	public void onClick$search() {
		menuSearch.setText(StringUtils.trimToEmpty(menuSearch.getValue()));
		menuBuilder.filter(menuSearch.getValue());
	}

	/**
	 * Creates the main menu and navigate to the default page.
	 * 
	 * @param navigateToDefaultPage
	 *            <code>true</code> to navigate to the default page.
	 * @throws URISyntaxException
	 *             If the default navigate URL is not a valid URI.
	 */
	private void createMenu(boolean navigateToDefaultPage) throws URISyntaxException {
		logger.trace(Literal.ENTERING);

		// Generate the tree menu.
		Treechildren treechildren = new Treechildren();
		mainMenu.appendChild(treechildren);

		menuBuilder = new TreeMenuBuilder(treechildren, MainMenu.getMenuItems(),
				userWorkspace.getGrantedAuthoritySet());
		menuBuilder.render();

		// Collapse all the menu items.
		menuBuilder.toggleAll(false);

		// Store the menu rights in the user workspace.
		userWorkspace.setHasMenuRights(menuBuilder.getMenuRights());

		// Navigate to the default page.
		if (!navigateToDefaultPage) {
			return;
		}

		String authType = StringUtils.trimToEmpty(userWorkspace.getLoggedInUser().getAuthType());
		Date expiredDate = userWorkspace.getLoggedInUser().getAccountExpiredOn();


		if (!AuthenticationType.DAO.name().equals(authType) || expiredDate == null) {
			openPage("menu_Item_Home", "/WEB-INF/pages/welcome.zul", false);
		} else if (expiredDate.before(DateUtil.getSysDate())) {
			Window win = (Window) Executions.createComponents("/WEB-INF/pages/PasswordReset/changePwd.zul", null, null);
			win.setWidth("98%");
			win.setHeight("98%");
			win.doModal();
		} else {
			openPage("menu_Item_Home", "/WEB-INF/pages/welcome.zul", false);
		}

		logger.trace(Literal.LEAVING);
	}

	/**
	 * Event listener that will be notified when the menu clicked.
	 * 
	 * @param event
	 *            The event being received.
	 */
	public void onMenuClick(ForwardEvent event) {
		logger.trace(Literal.ENTERING);

		Treeitem treeitem = (Treeitem) event.getOrigin().getTarget();
		treeitem.setOpen(!treeitem.isOpen());

		logger.trace(Literal.LEAVING);
	}

	/**
	 * Event listener that will be notified when the menu item clicked.
	 * 
	 * @param event
	 *            The event being received.
	 * @throws URISyntaxException
	 *             If the navigate URL is not a valid URI.
	 */
	public void onMenuItemClick(ForwardEvent event) throws URISyntaxException {
		logger.trace(Literal.ENTERING);

		// Get the menu item.
		Treeitem treeitem = (Treeitem) event.getOrigin().getTarget();
		MenuItem menuItem = (MenuItem) treeitem.getAttribute("data");

		// Check whether user can access now.
		if (!"Y".equalsIgnoreCase(SysParamUtil.getValueAsString(PennantConstants.ALLOW_ACCESS_TO_APP))) {
			MessageUtil.showInfo("ALLOW_ACCESS_RESTRICTION");
			return;
		}

		LoggedInUser user = userWorkspace.getLoggedInUser();

		if (user.getLogonFromTime() != null && DateUtility.compareTime(new Date(System.currentTimeMillis()),
				user.getLogonFromTime(), false) == -1) {
			MessageUtil.showInfo("OPS_NOT_ALLOWED_BEFORE",
					DateUtil.format(user.getLogonFromTime(), DateFormat.SHORT_TIME));
			return;
		}

		if (user.getLogonToTime() != null
				&& DateUtility.compareTime(new Date(System.currentTimeMillis()), user.getLogonToTime(), false) == 1) {
			MessageUtil.showInfo("OPS_NOT_ALLOWED_AFTER",
					DateUtil.format(user.getLogonToTime(), DateFormat.SHORT_TIME));
			return;
		}

		openPage(menuItem.getId(), menuItem.getNavigateUrl(), true);

		logger.trace(Literal.LEAVING);
	}

	/**
	 * Opens the menu item.
	 * 
	 * @param menuId
	 *            The ID of the menu.
	 * @param navigateUrl
	 *            The URL of the page.
	 * @param closable
	 *            <code>true</code> if the page is closable.
	 * @throws URISyntaxException
	 *             If the navigate URL is not a valid URI.
	 */
	private void openPage(String menuId, String navigateUrl, boolean closable) throws URISyntaxException {
		logger.info("Openening page " + navigateUrl);

		// Get the tab id.
		String tabId = menuId.replace("menu_Item_", "tab_");

		// Get the container components for the page.
		Tabs tabs = (Tabs) Path.getComponent("/outerIndexWindow/tabsIndexCenter");
		Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");

		// Open the tab if one already exists.
		if (tabs.hasFellow(tabId)) {
			((Tab) tabs.getFellow(tabId)).setSelected(true);
			return;
		}

		// Create the tab and panel.
		Tab tab = new Tab();
		tab.setParent(tabs);
		tab.setId(tabId);
		tab.setLabel(StringUtils.trimToEmpty(Labels.getLabel(menuId)));
		tab.setClosable(closable);
		tab.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				String page = event.getTarget().getId().replace("tab_", "");

				userWorkspace.deAllocateAuthorities(page);
			}
		});

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setParent(tabpanels);
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");

		// Prepare the URI and parameters of the page.
		URIBuilder uriBuilder = new URIBuilder(navigateUrl);
		String uri = uriBuilder.getPath();

		Map<String, String> parametrs = new HashMap<>();
		for (NameValuePair param : uriBuilder.getQueryParams()) {
			parametrs.put(param.getName(), param.getValue());
		}

		// Create components from a page file in the respective tab panel and select the tab.
		Executions.createComponents(uri, tabpanel, parametrs);
		tab.setSelected(true);
	}

	public void setUserWorkspace(UserWorkspace userWorkspace) {
		this.userWorkspace = userWorkspace;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
