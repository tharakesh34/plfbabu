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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
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
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.MenuDetails;
import com.pennant.backend.service.MenuDetailsService;
import com.pennant.common.menu.tree.TreeMenuFactory;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.WindowBaseCtrl;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.AuthenticationType;
import com.pennanttech.pff.core.util.DateUtil;

/**
 * 
 * Main menu controller. <br>
 * <br>
 * Added the buttons for expanding/closing the menu tree. Calls the menu
 * factory.
 */
public class MainMenuCtrl extends WindowBaseCtrl {
	private static final long serialVersionUID = -909795057747345551L;
	private static final Logger logger = Logger.getLogger(MainMenuCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 */
	private Window 		mainMenuWindow; 	// autowire
	protected Textbox 	menuName; 			// autowired
	
	private static String bgColor = "#FF6600";
	private static String bgColorInner = "white";
	
	private transient MenuDetailsService menuDetailsService;
	private HashMap<String, MenuDetails> hasMenuDetails;
	private LoggedInUser loggedInUser; 
	private transient UserWorkspace userWorkspace;
	private String homePageDisplayed = "NO";
	private transient UserService userService;

	public MainMenuCtrl() {
		super();
	}
	
	// Component Events

	/**
	 * Before binding the data and calling the Menu window we check, if the
	 * ZUL-file is called with a parameter for a selected User object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$mainMenuWindow(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		loggedInUser = getUserWorkspace().getLoggedInUser();
		CreateEvent ce = (CreateEvent) ((ForwardEvent) event).getOrigin();
		@SuppressWarnings("unchecked")
		final Map<String, Object> args = (Map<String, Object>) ce.getArg();

		if (args.containsKey("HomePageDisplayed")) {
			homePageDisplayed = (String) args.get("HomePageDisplayed");
		}
		//doOnCreateCommon(getMainMenuWindow(), event); // wire vars
		createMenu();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Creates the mainMenu. <br>
	 * 
	 * @throws InterruptedException
	 */
	private void createMenu() throws InterruptedException {
		logger.trace(Literal.ENTERING);

		Image image;
		final Groupbox gb = (Groupbox) getMainMenuWindow().getFellowIfAny("groupbox_menu");

		// Hbox for the expand/collapse buttons
		final Hbox hbox = new Hbox();
		hbox.setStyle("backgound-color: " + bgColorInner+";" +"margin:4px;");
		hbox.setParent(gb);

		image = new Image("/images/icons/open4.png");
		hbox.appendChild(image);
		Space space = new Space();
		space.setWidth("0px");
		hbox.appendChild(space);
		image.setId("btnMainMenuExpandAll");
		image.setTooltiptext(Labels.getLabel("btnFolderExpand.tooltiptext"));
		image.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				expandMenus(event);
			}
		});
		image = new Image("/images/icons/close4.png");
		hbox.appendChild(image);
		image.setId("btnMainMenuCollapseAll");
		image.setTooltiptext(Labels.getLabel("btnFolderCollapse.tooltiptext"));
		image.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				collapseMenus(event);
			}
		});

		menuName = new Textbox();
		menuName.setId("menuName1");
		menuName.setTooltiptext("Please Enter Menu Code");
		menuName.setTabindex(1);
		menuName.setWidth("100px");
		hbox.appendChild(menuName);


		Button btnGo = new Button();
		btnGo.setId("btnGo");
		btnGo.setLabel("Go");
		btnGo.setTooltiptext("navigate to menu");
		btnGo.setTabindex(2);

		hbox.appendChild(btnGo);

		btnGo.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				openMenu(event);
			}
		});

		Separator separator = createSeparator(false);
		separator.setWidth("98%");
		separator.setStyle("background-color:"+bgColor+";background-image:none; height:1px;margin:10px 0px; margin:4px");
		separator.setBar(true);
		separator.setParent(gb);

		// the menuTree
		final Tree tree = new Tree();
		tree.setSizedByContent(true);
		tree.setParent(gb);
		tree.setStyle("border: none");

		final Treechildren treechildren = new Treechildren();
		tree.appendChild(treechildren);

		// generate the treeMenu from the menuXMLFile
		TreeMenuFactory.addMainMenu(treechildren);

		final Separator sep1 = new Separator();
		sep1.setWidth("97%");
		sep1.setBar(false);
		sep1.setParent(gb);
		doCollapseExpandAll(getMainMenuWindow(), false);
		
		/* as standard, call the welcome page */
		if (!"NO".equals(homePageDisplayed)) {
			return;
		}
		
		String authType = StringUtils.trimToEmpty(userWorkspace.getLoggedInUser().getAuthType());
		Date expiredDate = loggedInUser.getAccountExpiredOn();

		if (AuthenticationType.SSO.name().equals(authType)) {
			if (getUserService().getUserByLogin(loggedInUser.getUserName()) == null) {
				Window win = (Window) Executions.createComponents("/logout.zul", null, null);
				win.setWidth("100%");
				win.setHeight("100%");
				win.doModal();
				return;
			}
		}

		if (!AuthenticationType.DAO.name().equals(authType) || expiredDate == null) {
			showPage("/WEB-INF/pages/welcome.zul", "menu_Item_Home");
		} else if (expiredDate.before(DateUtil.getSysDate())) {
			Window win = (Window) Executions.createComponents("/WEB-INF/pages/PasswordReset/changePwd.zul", null, null);
			win.setWidth("98%");
			win.setHeight("98%");
			win.doModal();
		} else {
			showPage("/WEB-INF/pages/welcome.zul", "menu_Item_Home");
		}
	
		logger.trace(Literal.LEAVING);
	}

	/**
	 * Creates a seperator. <br>
	 * 
	 * @param withBar
	 * <br>
	 *            true=with Bar <br>
	 *            false = without Bar <br>
	 * @return
	 */
	private static Separator createSeparator(boolean withBar) {
		logger.debug("Entering ");

		final Separator sep = new Separator();
		sep.setBar(withBar);
		logger.debug("Leaving ");
		return sep;
	}

	public final class GuestBookListener implements EventListener<Event> {
	
		public GuestBookListener() {
			
		}
		
		@Override
		public void onEvent(Event event) throws Exception {
			logger.debug("Entering ");

			showPage("/WEB-INF/pages/guestbook/guestBookList.zul", "Guestbook");
			logger.debug("Leaving ");
		}
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
		logger.debug("Entering ");

		try {

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
				// checkTab = (Tab) tabs.getFellow(tabName);
				if (tabs.getFellowIfAny(tabName.trim().replace("menu_Item_", "tab_")) != null) {
					checkTab = (Tab) tabs.getFellow(tabName.trim().replace("menu_Item_", "tab_"));
					checkTab.setSelected(true);
				}
			} catch (final ComponentNotFoundException ex) {
				logger.warn("Exception: ", ex);
			}

			if (checkTab == null) {

				final Tab tab = new Tab();
				tab.setId(tabName.trim().replace("menu_Item_", "tab_"));
				tab.setLabel(Labels.getLabel(tabName));
				tab.setClosable(true);
				if("tab_Home".equals(tab.getId())){
					tab.setClosable(false);
				}

				tab.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {
					public void onEvent(Event event) throws UiException {
						String pageName = event.getTarget().getId().replace("tab_", "");
						@SuppressWarnings("deprecation")
						UserWorkspace workspace= UserWorkspace.getInstance();
						workspace.deAllocateAuthorities(pageName);
					}
				});


				tab.setParent(tabs);

				final Tabpanels tabpanels = (Tabpanels) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter").getFellow("tabpanelsBoxIndexCenter");
				final Tabpanel tabpanel = new Tabpanel();
				tabpanel.setHeight("100%");
				tabpanel.setStyle("padding: 0px;");
				tabpanel.setParent(tabpanels);

				/*
				 * create the page and put it in the tabs area
				 */
				Executions.createComponents(zulFilePathName, tabpanel, null);
				tab.setSelected(true);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	public Window getMainMenuWindow() {
		logger.debug("Entering ");
		return this.mainMenuWindow;
	}

	public void setMainMenuWindow(Window mainMenuWindow) {
		this.mainMenuWindow = mainMenuWindow;
	}

	public void expandMenus(Event event) throws Exception {
		logger.debug("Entering");

		doCollapseExpandAll(getMainMenuWindow(), true);
		
		logger.debug("Leaving");
	}

	public void collapseMenus(Event event) throws Exception {
		logger.debug("Entering");

		doCollapseExpandAll(getMainMenuWindow(), false);
		
		logger.debug("Leaving");
	}

	private void doCollapseExpandAll(Component component, boolean aufklappen) {

		if (component instanceof Treeitem) {
			final Treeitem treeitem = (Treeitem) component;
			treeitem.setOpen(aufklappen);
		}
		final Collection<Component> com = component.getChildren();
		if (com != null) {
			for (final Iterator<?> iterator = com.iterator(); iterator.hasNext();) {
				doCollapseExpandAll((Component) iterator.next(), aufklappen);

			}
		}
	
	}
	public void openMenu(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try{
			if (StringUtils.isBlank(menuName.getValue())){
				MessageUtil.showError(Labels.getLabel("Invalid_Menu_Blank"));
			}else{

				MenuDetails menuDetails = getMenuDetails(StringUtils.trimToEmpty(menuName.getValue().toUpperCase()));

				if (menuDetails==null){
					MessageUtil.showError(Labels.getLabel("Invalid_Menu_Code"));
				}else{

					if (isAllowed(menuDetails.getMenuRef())){
						showPage(menuDetails.getMenuZulPath(), menuDetails.getMenuRef());
						menuName.setValue("");
					}else{
						MessageUtil.showError(Labels.getLabel("Not_Authorised"));
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean isAllowed(String menuId){
		logger.debug("Entering ");
		@SuppressWarnings("deprecation")
		UserWorkspace workspace = UserWorkspace.getInstance();
		if (workspace.getHasMenuRights().get(menuId)==null){
			return false;
		}else if (StringUtils.isEmpty(workspace.getHasMenuRights().get(menuId))){
			return true;
		}
		logger.debug("Leaving ");
		return workspace.isAllowed(workspace.getHasMenuRights().get(menuId));
	}
	/**
	 * @return the menuDetailsService
	 */
	public MenuDetailsService getMenuDetailsService() {
		return menuDetailsService;
	}

	/**
	 * @param menuDetailsService the menuDetailsService to set
	 */
	public void setMenuDetailsService(MenuDetailsService menuDetailsService) {
		this.menuDetailsService = menuDetailsService;
	}

	public MenuDetails getMenuDetails(String menuCode){
		logger.debug("Entering ");
		MenuDetails menuDetails=null;
		if (this.hasMenuDetails==null){
			menuDetaiils();
		}

		if (hasMenuDetails!=null){
			menuDetails = this.hasMenuDetails.get(menuCode.toUpperCase());
		}
		logger.debug("Leaving ");
		return menuDetails;
	} 


	private void menuDetaiils(){
		logger.debug("Entering ");
		
		List<MenuDetails> menuList =  getMenuDetailsService().getMenuDetailsByApp(App.CODE);
		if (menuList.size()>0){
			this.hasMenuDetails = new HashMap<String, MenuDetails>();
		}
		for (int i = 0; i < menuList.size(); i++) {
			MenuDetails menuDetails = menuList.get(i);
			this.hasMenuDetails.put(menuDetails.getMenuCode().toUpperCase(), menuDetails);
		}
		logger.debug("Leaving ");
	} 
	
	public void onMenuItemSelected(ForwardEvent event){
		logger.debug("entering");
		Treeitem treeitem = (Treeitem) event.getOrigin().getTarget();
		treeitem.setOpen(!treeitem.isOpen());
		logger.debug("leaving");
	}
	
	final protected UserWorkspace getUserWorkspace() {
		return userWorkspace;
	}

	public void setUserWorkspace(UserWorkspace userWorkspace) {
		this.userWorkspace = userWorkspace;
	}
	
	public UserService getUserService() {
		return this.userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
}
