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
 * FileName : MainMenuCtrl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 04-08-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-08-2011 Pennant 0.1 * 03-05-2018 Sai Krishna 0.2 While opening a menu group, close * the siblings. Also fixed the
 * * position of search menu in ZUL * * * * * *
 ********************************************************************************************
 */
package com.pennant.common.menu;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.pennant.UserWorkspace;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.webui.util.WindowBaseCtrl;
import com.pennanttech.extension.Services;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.menu.MainMenu;
import com.pennanttech.pennapps.web.menu.Menu;
import com.pennanttech.pennapps.web.menu.MenuItem;
import com.pennanttech.pennapps.web.menu.TreeMenuBuilder;
import com.pennanttech.pennapps.web.util.ComponentUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.GlemCollateralProcess;
import com.pennapps.core.access.log.UserAccess;
import com.pennapps.core.access.log.UserAccessDAO;

/**
 * Controller for the main menu.
 */
public class MainMenuCtrl extends WindowBaseCtrl {
	private static final long serialVersionUID = -909795057747345551L;
	private static final Logger logger = LogManager.getLogger(MainMenuCtrl.class);

	protected Window mainMenuWindow;
	protected Image expandAll;
	protected Image collapseAll;
	protected Textbox menuSearch;
	protected Button search;
	protected Tree mainMenu;

	private TreeMenuBuilder menuBuilder;
	private transient UserWorkspace userWorkspace;
	private UserAccessDAO userAccessDAO;
	@Autowired(required = false)
	private GlemCollateralProcess glemsCollateralProcess;

	/**
	 * Creates a new main menu controller.
	 */
	public MainMenuCtrl() {
		super();
	}

	/**
	 * Event listener that will be notified when the window created.
	 * 
	 * @param event The event being received.
	 * @throws URISyntaxException If the default navigate URL is not a valid URI.
	 */
	public void onCreate$mainMenuWindow(ForwardEvent event) throws URISyntaxException {
		logger.debug(Literal.ENTERING);

		// Get the event arguments.
		Map<?, ?> args = ((CreateEvent) event.getOrigin()).getArg();
		boolean navigateToDefaultPage = false;

		if (args.containsKey("HomePageDisplayed")) {
			navigateToDefaultPage = "NO".equals((String) args.get("HomePageDisplayed")) ? true : false;
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
	 * @param navigateToDefaultPage <code>true</code> to navigate to the default page.
	 * @throws URISyntaxException If the default navigate URL is not a valid URI.
	 */
	private void createMenu(boolean navigateToDefaultPage) throws URISyntaxException {
		logger.trace(Literal.ENTERING);

		// Generate the tree menu.
		Treechildren treechildren = new Treechildren();
		mainMenu.appendChild(treechildren);

		filterMenus(MainMenu.getMenuItems());
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
		Date pwdExpDate = userWorkspace.getLoggedInUser().getPasswordExpiredOn();
		Date expiredDate = userWorkspace.getLoggedInUser().getAccountExpiredOn();

		if (!AuthenticationType.DAO.name().equals(authType)) {
			ComponentUtil.openMenuItem("menu_Item_Home", "/WEB-INF/pages/welcome.zul", false,
					new MenuItemOnCloseListener());
		} else if ((expiredDate != null && expiredDate.before(DateUtil.getSysDate()))) {
			Executions.sendRedirect("/csrfLogout.zul");
		} else if ((pwdExpDate != null && pwdExpDate.before(DateUtil.getSysDate()))) {
			Executions.createComponents("/WEB-INF/pages/PasswordReset/changePwd.zul", null, null);
			ComponentUtil.openMenuItem("menu_Item_Home", "/WEB-INF/pages/welcome.zul", false,
					new MenuItemOnCloseListener());
		} else {
			ComponentUtil.openMenuItem("menu_Item_Home", "/WEB-INF/pages/welcome.zul", false,
					new MenuItemOnCloseListener());
		}

		logger.trace(Literal.LEAVING);
	}

	private void filterMenus(List<MenuItem> list) {
		for (MenuItem menuItem : list) {
			if (menuItem instanceof Menu) {
				filterMenus(((Menu) menuItem).getItems());
			} else {
				if (!isAllowedMenu(menuItem))
					menuItem.setRightName("#Pennant#");
			}
		}
	}

	private boolean isAllowedMenu(MenuItem menuItem) {
		String menuId = menuItem.getId();

		if (Services.isExclude(menuId)) {
			return false;
		}

		switch (menuId) {
		case "menu_Item_LoanTypeKnockOff":
		case "menu_Item_AutoKnockoff":
		case "menu_Category_Auto_KnockOff":
		case "menu_Item_AutoKnockoffDetails":
			return ImplementationConstants.ALLOW_AUTO_KNOCK_OFF;
		case "menu_Category_IncomeAmortization":
		case "menu_Item_AmortizationMethodRule":
		case "menu_Item_AMZProcess":
		case "menu_Item_FeeAmzReferenceReport":
		case "menu_Item_FeeAmzLoanTypeReport":
		case "menu_Item_ExpenseLoanTypeReport":
		case "menu_Item_ExpenseLoanLevelReport":
		case "menu_Item_CalAvgPOS":
		case "menu_Item_IncomeAmortization":
		case "menu_Item_ExpenseUpload":
			return ImplementationConstants.ALLOW_IND_AS;
		case "menu_Item_OverDraftFinanceType":
			return ImplementationConstants.ALLOW_OD_LOANS;
		case "menu_Item_CDFinanceType":
		case "menu_Item_CDSchemes":
		case "menu_Item_NewCDFinanceMain":
			return ImplementationConstants.ALLOW_CD_LOANS;
		case "menu_Item_Sampling":
			return ImplementationConstants.ALLOW_SAMPLING;
		case "menu_Item_School":
		case "menu_Item_School_IncomeExpense":
			return ImplementationConstants.ALLOW_SCHOOL_ORG;
		case "menu_Item_AssetClassCodes":
		case "menu_Item_AssetSubClassCodes":
		case "menu_Item_AssetClassSetup":
		case "menu_Item_NPA_Report":
			return ImplementationConstants.ALLOW_NPA;
		case "menu_Item_ManualProvisioning":
		case "menu_Item_Provision_Report":
			return ImplementationConstants.ALLOW_PROVISION;
		case "menu_Item_LoanDownSizing":
			return ImplementationConstants.ALLOW_LOAN_DOWNSIZING;
		case "menu_Item_PMAY":
		case "menu_Item_PmayEnquiry":
		case "menu_Item_PMAYDetails":
			return ImplementationConstants.ALLOW_PMAY;
		case "menu_Item_OCR":
			return ImplementationConstants.ALLOW_OCR;
		case "menu_Category_NonLanReceipts":
			return ImplementationConstants.ALLOW_NON_LAN_RECEIPTS;
		case "menu_Item_Restructure":
			return ImplementationConstants.ALLOW_RESTRUCTURING;
		case "menu_Item_subventionDeale":
		case "menu_Item_subventionManufacturer":
		case "menuItem_Subvention_knockoff_Upload":
		case "menuItem_Subvention_Process_Upload":
		case "menu_Item_SubventionUploadStsRpt":
		case "menu_Item_SubventionMISReport":
		case "menu_Item_SubventionAmortReport":
			return ImplementationConstants.ALLOW_SUBVENTION;
		case "menu_Item_CersaiDownload":
		case "menu_Item_CersaiUpload":
		case "menu_Item_CersaiReport":
		case "menu_Item_CersaiModificationReport":
		case "menu_Item_CersaiSatisfactionReport":
			return ImplementationConstants.ALLOW_CERSAI;
		case "menu_Item_CollateralDownload":
			return this.glemsCollateralProcess != null;
		case "menu_Item_FinTypePartnerbankMapping":
			return PartnerBankExtension.BRANCH_WISE_MAPPING;
		default:
			break;
		}
		return true;
	}

	/**
	 * Event listener that will be notified when the menu clicked.
	 * 
	 * @param event The event being received.
	 */
	public void onMenuClick(ForwardEvent event) {
		logger.trace(Literal.ENTERING);

		Treeitem treeitem = (Treeitem) event.getOrigin().getTarget();
		treeitem.setOpen(!treeitem.isOpen());

		// While opening a menu group, close the siblings.
		if (treeitem.isOpen()) {
			// Close previous siblings.
			Treeitem sibling = treeitem;

			while ((sibling = sibling.getPreviousSibling() instanceof Treeitem ? (Treeitem) sibling.getPreviousSibling()
					: null) != null) {
				sibling.setOpen(false);
			}

			// Close next siblings.
			sibling = treeitem;

			while ((sibling = sibling.getNextSibling() instanceof Treeitem ? (Treeitem) sibling.getNextSibling()
					: null) != null) {
				sibling.setOpen(false);
			}
		}

		logger.trace(Literal.LEAVING);
	}

	/**
	 * Event listener that will be notified when the menu item clicked.
	 * 
	 * @param event The event being received.
	 * @throws URISyntaxException If the navigate URL is not a valid URI.
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

		if (App.getBooleanProperty("user.access.log.req")) {
			UserAccess menuAccess = new UserAccess();
			menuAccess.setMenuItem(Labels.getLabel(menuItem.getId()));
			menuAccess.setUsrLoginId(user.getLoginLogId());
			menuAccess.setAccessedBy(user.getUserId());

			userAccessDAO.logUserAccess(menuAccess);
		}

		try {
			ComponentUtil.openMenuItem(menuItem.getId(), menuItem.getNavigateUrl(), true,
					new MenuItemOnCloseListener());
		} catch (AppException e) {
			MessageUtil.showError(e);
		}
		logger.trace(Literal.LEAVING);
	}

	public class MenuItemOnCloseListener implements EventListener<Event> {
		@Override
		public void onEvent(Event event) {
			String page = event.getTarget().getId().replace("tab_", "");

			userWorkspace.deAllocateAuthorities(page);
		}
	}

	public void setUserWorkspace(UserWorkspace userWorkspace) {
		this.userWorkspace = userWorkspace;
	}

	@Autowired
	public void setMenuAccessDao(UserAccessDAO userAccessDAO) {
		this.userAccessDAO = userAccessDAO;
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
