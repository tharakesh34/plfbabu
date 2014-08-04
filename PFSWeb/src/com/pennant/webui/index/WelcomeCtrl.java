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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.dashboard.DashBoard;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboarddetail.DashboardPosition;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.service.dashboard.DetailStatisticsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.common.menu.domain.MenuPendingDetail;
import com.pennant.webui.dashboard.DashboardCreate;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Welcome.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class WelcomeCtrl extends GFCBaseListCtrl<DashBoard> implements Serializable {

	private static final long serialVersionUID = 8242094102118374753L;
	
	private final static Logger logger = Logger.getLogger(WelcomeCtrl.class);
	protected Window window_Welcome;
	protected Button addbtn;
	protected Radiogroup columnSelect;
	protected Combobox cbDashBordsList;
	protected Portallayout dashBoardsPortalLayout;
	protected Portalchildren firstPortalChildColumn;
	protected Portalchildren secondPortalChildColumn;
	protected Portalchildren thirdPortalChildColumn;
	protected Button singleColumnBtn;
	protected Button twoColumnBtn;
	protected Button threeColumnBtn;
	protected Timer refreshTimer;
	protected Tabbox tabbox;
	private DashboardConfigurationService dashboardConfigurationService;
	public Map<String, DashboardPosition> dashBoardPosition;
	public List<ValueLabel> dashboardslist;

	public Groupbox first;
	public Groupbox second;
	public DetailStatisticsService DetailStatisticsService;
	public DashBoard dashBoard;
	public Listbox listbox;

	private DashboardCreate dashboardCreate;
	// it stores current dash boards and is it maximized
	Map<String, Boolean> currentDashBords = new HashMap<String, Boolean>();
	// it stores dash board positions
	Map<String, DashboardPosition> dashboardpositionsMap = new HashMap<String, DashboardPosition>();

	public void onCreate$window_Welcome(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();
		Tab tab = (Tab) tabbox.getSelectedTab();
		tab.addForward("onSelect", window_Welcome, "onDashBoardTabSelected", null);
		int delayTime = 10 * 60 * 1000;
		this.refreshTimer.setDelay(delayTime);
		initilizeDashBords(false);// Refresh is false
		setDashBoardsLayOut(2);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */

	private void initilizeDashBords(boolean isRefresh) {
		logger.debug("Entering ");
		// Initialize widgets collection list
		DashBoard dashBoard = getDashboardConfigurationService().getDashBoardData(
				getUserWorkspace().getUserDetails().getSecurityUser().getUsrID(), "");
		setDashBoard(dashBoard);
		doFillDashboardList(dashBoard);
		List<ValueLabel> dashboardlist = getDashboardslist();
		setDashboardslist(dashboardlist);
		
		if(!this.refreshTimer.isRunning()){
			this.refreshTimer.start();
		}

		if (dashBoardPosition != null) {
			dashBoardPosition.clear();
		}
		if (isRefresh) {// If refresh by timer take current dash boards on
						// Desktop and refresh those
			dashBoardPosition = getCurrentDashBordPositions();
		} else { // If refresh by page refresh or initial loading ,load dash
					// boards which are saved only
			dashBoardPosition = getDashBoard().getDashBoardPosition();
		}

		// Clearing all Columns first for Refresh
		this.firstPortalChildColumn.getChildren().clear();
		this.secondPortalChildColumn.getChildren().clear();
		this.thirdPortalChildColumn.getChildren().clear();

		for (String key : getDashBoard().getDashboardConfigMap().keySet()) {
			if (dashBoardPosition.containsKey(key)) {
				Portalchildren pc = (Portalchildren) dashBoardsPortalLayout
						.getChildren().get(
								dashBoardPosition.get(key).getDashboardCol());
				if(key.equals("PENDING_RCD_ROLE")){
					pc.appendChild(createPanelListBox(
							getDashBoard().getDashboardConfigMap().get(key), pc));
					pc.setWidth("80%");
					pc.setHeight("90%");
				}else{
				Panel dashBoradPanel = dashboardCreate.createPanel(
						getDashBoard().getDashboardConfigMap().get(key), pc);
				pc.appendChild(dashBoradPanel);
				}
				/*
				 * to be implemented for maximized panel should be in maximized
				 * after refresh . if(currentDashBords.containsKey(key)){
				 * dashBoradPanel.setMaximized(true);
				 * dashboardCreate.onPanelMaximizedHelper(dashBoradPanel); }
				 */

			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Action of click the add Button(add button)
	 */
	public void onClick$addbtn(Event event) {
		logger.debug("Entering " + event.toString());
		int selectedColumnIdx = columnSelect.getSelectedIndex();
		if (cbDashBordsList.getChildren() != null
				&& cbDashBordsList.getChildren().size() > 0) {
			if (dashBoardsPortalLayout.getFellowIfAny(cbDashBordsList
					.getSelectedItem().getValue().toString()) == null) {
				if (dashBoardsPortalLayout.getChildren().size() > selectedColumnIdx) {
					Portalchildren pc = (Portalchildren) dashBoardsPortalLayout
							.getChildren().get(selectedColumnIdx);
					if(cbDashBordsList.getSelectedItem().getValue().toString().equals("PENDING_RCD_ROLE")){
						pc.appendChild(createPanelListBox(
								getDashBoard().getDashboardConfigMap().get(
										this.cbDashBordsList.getSelectedItem()
												.getValue().toString()), pc));
						pc.setWidth("80%");
						pc.setHeight("90%");
					}else{
					pc.appendChild(dashboardCreate.createPanel(
							getDashBoard().getDashboardConfigMap().get(
									this.cbDashBordsList.getSelectedItem()
											.getValue().toString()), pc));
					}
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public  Panel  createPanelListBox(DashboardConfiguration info,Portalchildren portalchildren) {
		logger.debug("Entering ");
		Panel dashboardPanel = new Panel();
 		dashboardPanel.setBorder("normal");
		dashboardPanel.setCollapsible(true);
		dashboardPanel.setClosable(true);  
		dashboardPanel.setMaximizable(true);
		dashboardPanel.appendChild(new Panelchildren());
		dashboardPanel.setId(info.getDashboardCode());
		dashboardPanel.setTitle(info.getDashboardDesc());
		Panelchildren panelchildren = dashboardPanel.getPanelchildren();
		Listhead listhead = new Listhead();
		listbox = new Listbox();
		listbox.setHeight("100%");
		listbox.setRows(6);
		listbox.appendChild(listhead);
		Listheader listheaderFinRef = new Listheader("Finance Reference");
		listhead.appendChild(listheaderFinRef);
		Listheader listheaderCIF = new Listheader("Customer CIF");
		listhead.appendChild(listheaderCIF);
		panelchildren.appendChild(listbox);
		doFillDetailListBox(listbox);
		dashboardPanel.appendChild(panelchildren);
		logger.debug("Leaving");
		return dashboardPanel;
	}
	
	public void doFillDetailListBox(Listbox listbox) {
		logger.debug("Entering");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<MenuPendingDetail> jdbcSearchObject = new JdbcSearchObject<MenuPendingDetail>(MenuPendingDetail.class);
		jdbcSearchObject.addTabelName("FinancePendingDetailsByRole_View");
		//jdbcSearchObject.addFilterEqual("NextRoleCode", getRole());
		List<MenuPendingDetail> menuPendingDetails = pagedListService.getBySearchObject(jdbcSearchObject);
		listbox.getItems().clear();
		Listgroup group = null;
		Listitem item = null;
		Listcell cell = null;
		String menuCode = "";
		if(menuPendingDetails != null){
			for (MenuPendingDetail menuPendingDetail : menuPendingDetails){
				if(!menuCode.equalsIgnoreCase(menuPendingDetail.getMenuCode())){
					group = new Listgroup();
					cell = new Listcell(menuPendingDetail.getMenuCode());
					cell.setParent(group);
					this.listbox.appendChild(group);
				}
				menuCode = menuPendingDetail.getMenuCode();
				item = new Listitem();
				item.setId(menuPendingDetail.getMenuCode());
				Listcell lc;
				lc = new Listcell(menuPendingDetail.getFinReference());
				lc.setParent(item);
				lc = new Listcell(menuPendingDetail.getCustCIF());
				lc.setParent(item);
				item.setAttribute("data", menuPendingDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onDetailListBoxDoubleClicked");
				listbox.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}
	
	public void onDetailListBoxDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		final Listitem item = this.listbox.getSelectedItem();
		final MenuPendingDetail menuPendingDetail = (MenuPendingDetail) item.getAttribute("data");
		if(menuPendingDetail != null){
		openZulpage(menuPendingDetail);
		}
		logger.debug("Leaving");
	}

	public void openZulpage(MenuPendingDetail menuPendingDetail) throws InterruptedException{
		try {
			
				/* get an instance of the borderlayout defined in the zul-file */
				final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				/* get an instance of the searched CENTER layout area */
				final Center center = bl.getCenter();

				final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter")
						.getFellow("tabsIndexCenter");

				// Check if the tab is already open, if not than create them
				Tab checkTab = null;
				try {
					checkTab = (Tab) tabs.getFellow("tab_" + menuPendingDetail.getMenuCode());
					checkTab.setSelected(true);
				} catch (final ComponentNotFoundException ex) {
					// Ignore if can not get tab.
				}

				if (checkTab == null) {
					final Tab tab = new Tab();
					tab.setId("tab_" +  menuPendingDetail.getMenuCode());
					tab.setLabel( menuPendingDetail.getMenuCode());
					tab.setClosable(true);

					tab.setParent(tabs);

					final Tabpanels tabpanels = (Tabpanels) center.getFellow("divCenter")
							.getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter")
							.getFellow("tabpanelsBoxIndexCenter");
					final Tabpanel tabpanel = new Tabpanel();
					tabpanel.setHeight("100%");
					tabpanel.setStyle("padding: 0px;");
					tabpanel.setParent(tabpanels);

					Executions.createComponents(menuPendingDetail.getMenuZulPath(), tabpanel, null);
					tab.setSelected(true);
				}
		
			if (logger.isDebugEnabled()) {
				logger.debug("--> calling zul-file: " + menuPendingDetail.getMenuZulPath());
			}
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}

	}
	
	
	/**
	 * When user clicks on save button
	 * 
	 * @param event
	 */
	public void onClick$savebtn(Event event) {
		logger.debug("Entering " + event.toString());
		getDashboardConfigurationService().savePositions(
				(new ArrayList<DashboardPosition>(getCurrentDashBordPositions()
						.values())),
				getUserWorkspace().getUserDetails().getSecurityUser()
						.getUsrID());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This Method Returns Current DashBords on the DeskTop .Follows Observer
	 * Design Pattern
	 * 
	 * @return Map<String,DashboardPosition>
	 */
	private Map<String, DashboardPosition> getCurrentDashBordPositions() {

		DashboardPosition dashboardPosition;
		Panel p_children;
		Portalchildren ptc;
		dashboardpositionsMap.clear();
		currentDashBords.clear();
		for (int i = 0; i < dashBoardsPortalLayout.getChildren().size(); i++) {

			ptc = (Portalchildren) dashBoardsPortalLayout.getChildren().get(i);
			for (int j = 0; j < ptc.getChildren().size(); j++) {
				p_children = (Panel) ptc.getChildren().get(j);
				dashboardPosition = new DashboardPosition();
				dashboardPosition.setUsrId(getUserWorkspace().getUserDetails().getSecurityUser().getUsrID());
				dashboardPosition.setDashboardRef(p_children.getId());
				dashboardPosition.setDashboardCol(i);
				dashboardPosition.setDashboardRow(j);
				dashboardpositionsMap.put(p_children.getId(), dashboardPosition);
				currentDashBords.put(p_children.getId(), p_children.isMaximized());
			}
		}
		return dashboardpositionsMap;
	}

	/**
	 * Set list of dash boards into selection comboBox
	 * 
	 * @param dashBoard
	 */
	public void doFillDashboardList(DashBoard dashBoard) {
		logger.debug("Entering ");
		this.cbDashBordsList.getItems().clear();
		for (String key : dashBoard.getDashboardConfigMap().keySet()) {
			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setValue(dashBoard.getDashboardConfigMap().get(key).getDashboardCode());
			comboitem.setLabel(dashBoard.getDashboardConfigMap().get(key).getDashboardDesc());
			this.cbDashBordsList.appendChild(comboitem);

		}
		if (dashBoard.getDashboardConfigMap().size() > 0) {
			this.cbDashBordsList.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * When click on single column button
	 * 
	 * @param event
	 */
	public void onClick$singleColumnBtn(Event event) {
		logger.debug("Entering " + event.toString());
		setDashBoardsLayOut(1);
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * When click on Three column button
	 * 
	 * @param event
	 */
	public void onClick$threeColumnBtn(Event event) {
		logger.debug("Entering " + event.toString());
		setDashBoardsLayOut(3);
		logger.debug("Leaving " + event.toString());
	}


	/**
	 * When click on Two column button
	 * 
	 * @param event
	 */
	public void onClick$twoColumnBtn(Event event) {
		logger.debug("Entering " + event.toString());
		setDashBoardsLayOut(2);
		logger.debug("Leaving " + event.toString());
	}
	
	private void setDashBoardsLayOut(int layOutType){
		switch(layOutType){
		case 1:
			
			firstPortalChildColumn.setWidth("100%");
			/*
			 * This code is from ZK Example Code ptc2.setVisible(false);
			 * ptc3.setVisible(false);
			 */
			// bussinessPortal.setHeight("");
			singleColumnBtn.setDisabled(true);
			twoColumnBtn.setDisabled(false);
			threeColumnBtn.setDisabled(false);
			break ;
			
		case 2:
			firstPortalChildColumn.setWidth("50%");
			secondPortalChildColumn.setWidth("50%");
			/*
			 * ptc2.setVisible(true); ptc3.setVisible(false);
			 */
			// bussinessPortal.setHeight("");
			singleColumnBtn.setDisabled(false);
			twoColumnBtn.setDisabled(true);
			threeColumnBtn.setDisabled(false);
			
			break ;

		case 3:	
			
			firstPortalChildColumn.setWidth("33%");
			secondPortalChildColumn.setWidth("33%");
			thirdPortalChildColumn.setWidth("34%");
			/*
			 * ptc2.setVisible(true); ptc3.setVisible(true);
			 * bussinessPortal.setHeight("");
			 */
			singleColumnBtn.setDisabled(false);
			twoColumnBtn.setDisabled(false);
			threeColumnBtn.setDisabled(true);
			break ;
		}		
	}

	/**
	 * this event will raise for every n seconds .
	 * 
	 * @param event
	 */
	public void onTimer$refreshTimer(Event event) {
		logger.debug("Entering " + event.toString());
		/*Refresh Only if DashBoards Tab is selected*/
		if (tabbox.getSelectedTab().getId().equals("tab_Home")) {
			this.window_Welcome.invalidate();
			this.cbDashBordsList.getItems().clear();
			initilizeDashBords(true);
		}else{
			this.refreshTimer.stop();
		}
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * On Select DashBoard Tab
	 */
	public synchronized void onDashBoardTabSelected(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		initilizeDashBords(true);
		logger.debug("Leaving " + event.toString());
	}

	// GETTERS AND SETTERS

	public List<ValueLabel> getDashboardslist() {
		return dashboardslist;
	}

	public void setDashboardslist(List<ValueLabel> dashboardslist) {
		this.dashboardslist = dashboardslist;
	}

	public DashboardCreate getDashboardCreate() {
		return dashboardCreate;
	}

	public void setDashboardCreate(DashboardCreate dashboardCreate) {
		this.dashboardCreate = dashboardCreate;
	}

	public DashBoard getDashBoard() {
		return dashBoard;
	}

	public void setDashBoard(DashBoard dashBoard) {
		this.dashBoard = dashBoard;
	}

	public DashboardConfigurationService getDashboardConfigurationService() {
		return dashboardConfigurationService;
	}

	public void setDashboardConfigurationService(
			DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}
}
