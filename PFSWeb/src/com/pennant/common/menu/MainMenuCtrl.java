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

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.UserWorkspace;
import com.pennant.backend.model.MenuDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.MenuDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.common.menu.dropdown.DropDownMenuFactory;
import com.pennant.common.menu.tree.TreeMenuFactory;
import com.pennant.policy.model.UserImpl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.WindowBaseCtrl;

/**
 * 
 * Main menu controller. <br>
 * <br>
 * Added the buttons for expanding/closing the menu tree. Calls the menu
 * factory.
 */
public class MainMenuCtrl extends WindowBaseCtrl implements Serializable {

	private static final long serialVersionUID = -909795057747345551L;
	private static final Logger logger = Logger.getLogger(MainMenuCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	private Window 		mainMenuWindow; 	// autowire
	protected Button 	btnGo; 				// autowire
	protected Textbox 	menuName; 			// autowired
	
	private static String bgColor = "#FF6600";
	private static String bgColorInner = "white";
	
	private transient MenuDetailsService menuDetailsService;
	private HashMap<String, MenuDetails> hasMenuDetails;
	private String homePageDisplayed = "NO";

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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
		logger.debug("Entering ");

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
				onClick$btnMainMenuExpandAll(event);
			}
		});
		image = new Image("/images/icons/close4.png");
		hbox.appendChild(image);
		image.setId("btnMainMenuCollapseAll");
		image.setTooltiptext(Labels.getLabel("btnFolderCollapse.tooltiptext"));
		image.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClick$btnMainMenuCollapseAll(event);
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
				onClick$btnGo(event);
			}
		});

		Separator separator = createSeparator(false);
		separator.setWidth("98%");
		separator.setStyle("background-color:"+bgColor+";background-image:none; height:1px;margin:10px 0px; margin:4px");
		separator.setBar(true);
		separator.setParent(gb);

		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

		// the menuTree
		final Tree tree = new Tree();
		tree.setSizedByContent(true);
		//tree.setStyle("overflow:auto;");
		tree.setParent(gb);

		// tree.setZclass("z-dottree");
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
		if ("NO".equals(homePageDisplayed)) {
			Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
			UserImpl userDetails = (UserImpl) currentUser.getPrincipal();		

			SecurityUser secUser = userDetails.getSecurityUser();

			if(secUser.getUsrAcExpDt()==null){
				showPage("/WEB-INF/pages/welcome.zul", "menu_Item_Home");
			}
			if(secUser.getUsrAcExpDt()!=null){
				if(secUser.getUsrAcExpDt().before(new Date(System.currentTimeMillis()))){
					Window win = (Window)Executions.createComponents( "/WEB-INF/pages/PasswordReset/changePwd.zul", null , null ) ;
					win.setTitle(Labels.getLabel("label_ChangePassword"));
					win.setWidth("98%");
					win.setHeight("98%");
					win.doModal();
				}
				else{
					showPage("/WEB-INF/pages/welcome.zul", "menu_Item_Home");
				}}
		}
		logger.debug("Leaving ");

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
			final Tabbox tabbox = ((Tabbox)center.getFellow("divCenter").getFellow("tabBoxIndexCenter"));
			final Tabs tabs = (Tabs) tabbox.getFellow("tabsIndexCenter");
			Tab selectedTab = tabbox.getSelectedTab();

			/**
			 * Check if the tab is already opened than select them and<br>
			 * go out of here. If not than create them.<br>
			 */

			Tab checkTab = null;
			try {
				// checkTab = (Tab) tabs.getFellow(tabName);
				checkTab = (Tab) tabs.getFellow(tabName.trim().replace("menu_Item_", "tab_"));
				if(selectedTab != null && selectedTab.getId().equals(checkTab.getId())){
					checkTab.setSelected(true);
				}
			} catch (final ComponentNotFoundException ex) {
				// Ignore if can not get tab.
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
						workspace.deAlocateAuthorities(pageName);
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
		
			if (logger.isDebugEnabled()) {
				logger.debug("--> calling zul-file: " + zulFilePathName);
			}
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
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

	public void onClick$btnMainMenuExpandAll(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}
		doCollapseExpandAll(getMainMenuWindow(), true);
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnMainMenuCollapseAll(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}
		doCollapseExpandAll(getMainMenuWindow(), false);
		logger.debug("Leaving " + event.toString());
	}

	@SuppressWarnings("deprecation")
	public void onClick$btnMainMenuChange(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// correct the desktop height
		final Checkbox cb = (Checkbox) Path.getComponent("/outerIndexWindow/CBtreeMenu");
		cb.setChecked(false);

		// get an instance of the borderlayout defined in the index.zul-file
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		// get an instance of the searched west layout area
		final West west = bl.getWest();
		west.setVisible(false);

		final North north = bl.getNorth();
		north.setFlex(true); 

		final Div div = (Div) north.getFellow("divDropDownMenu");

		final Menubar menuBar = (Menubar) div.getFellow("mainMenuBar");
		menuBar.setVisible(true);

		// generate the menu from the menuXMLFile
		DropDownMenuFactory.addDropDownMenu(menuBar);

		final Menuitem changeToTreeMenu = new Menuitem();
		changeToTreeMenu.setLabel(Labels.getLabel("menu_Item_backToTree"));
		changeToTreeMenu.setImage("/images/icons/refresh2_yellow_16x16.gif");
		changeToTreeMenu.setParent(menuBar);
		changeToTreeMenu.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// get an instance of the borderlayout defined in the
				// index.zul-file
				final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				// get an instance of the searched west layout area
				final West west = bl.getWest();
				west.setVisible(true);

				final North north = bl.getNorth();

				final Div div = (Div) north.getFellow("divDropDownMenu");

				final Menubar menuBar = (Menubar) div.getFellow("mainMenuBar");
				menuBar.getChildren().clear();
				menuBar.setVisible(false);
				north.setFlex(false); // that's important !!!!

				// correct the desktop height
				final Checkbox cb = (Checkbox) Path.getComponent("/outerIndexWindow/CBtreeMenu");
				cb.setChecked(true);


				// Refresh the whole page for setting correct sizes of the
				// components
				final Window win = (Window) Path.getComponent("/outerIndexWindow");
				win.invalidate();
				logger.debug("Leaving " + event.toString());

			}
		});

		// Guestbook
		final Menuitem guestBookMenu = new Menuitem();
		guestBookMenu.setLabel("ZK Guestbook");
		guestBookMenu.addEventListener("onClick", new GuestBookListener());
		guestBookMenu.setParent(menuBar);

		// Refresh the whole page for setting correct sizes of the
		// components
		final Window win = (Window) Path.getComponent("/outerIndexWindow");
		win.invalidate();
		logger.debug("Leaving ");
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
	public void onClick$btnGo(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		try{
			if (StringUtils.trimToEmpty(menuName.getValue()).equals("")){
				PTMessageUtils.showErrorMessage(Labels.getLabel("Invalid_Menu_Blank"));
			}else{

				MenuDetails menuDetails = getMenuDetails(StringUtils.trimToEmpty(menuName.getValue().toUpperCase()));

				if (menuDetails==null){
					PTMessageUtils.showErrorMessage(Labels.getLabel("Invalid_Menu_Code"));
				}else{

					if (isAllowed(menuDetails.getMenuRef())){
						showPage(menuDetails.getMenuZulPath(), menuDetails.getMenuRef());
						menuName.setValue("");
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("Not_Authorised"));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("Leaving " + event.toString());
	}

	private boolean isAllowed(String menuId){
		logger.debug("Entering ");
		@SuppressWarnings("deprecation")
		UserWorkspace workspace = UserWorkspace.getInstance();
		if (workspace.getHasMenuRights().get(menuId)==null){
			return false;
		}else if (workspace.getHasMenuRights().get(menuId).equals("")){
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
		
		List<MenuDetails> menuList =  getMenuDetailsService().getMenuDetailsByApp(PennantConstants.applicationCode);
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
	
}
