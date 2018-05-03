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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.dashboard.DashBoard;
import com.pennant.backend.model.dashboarddetail.DashboardPosition;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.webui.dashboard.DashboardCreate;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * This is the controller class for the /WEB-INF/pages/Welcome.zul file.
 */
public class WelcomeCtrl extends GFCBaseCtrl<DashBoard> {
	private static final long serialVersionUID = 8242094102118374753L;
	
	private static final Logger logger = Logger.getLogger(WelcomeCtrl.class);
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
	public DashBoard dashBoard;

	private DashboardCreate dashboardCreate;
	// it stores current dash boards and is it maximized
	Map<String, Boolean> currentDashBords = new HashMap<String, Boolean>();
	// it stores dash board positions
	Map<String, DashboardPosition> dashboardpositionsMap = new HashMap<String, DashboardPosition>();

	public WelcomeCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
	
	public void onCreate$window_Welcome(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_Welcome);

		tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();
		Tab tab = (Tab) tabbox.getSelectedTab();
		tab.addForward("onSelect", window_Welcome, "onDashBoardTabSelected", null);
		int delayTime = 10 * 60 * 1000;
		this.refreshTimer.setDelay(delayTime);
		refreshTimer.stop();

		try {
			delayTime = SysParamUtil.getValueAsInt("DASHBOARD_REFRESH_MIN");
			delayTime = delayTime * 60 * 1000;
		} catch (Exception e) {
			logger.error("Error on parsing delay time", e);
		}
		
		this.refreshTimer.setDelay(delayTime);
		
		logger.debug("Leaving " + event.toString());
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		setDashBoardsLayOut(initilizeDashBords(true));
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */

	private int initilizeDashBords(boolean isRefresh) {
		logger.debug("Entering ");
		
		int column = 0 ;
		int dashboardColIndex = 0 ;
		if(isRefresh){
			// Initialize widgets collection list
			DashBoard dashBoard = getDashboardConfigurationService().getDashBoardData(getUserWorkspace().getLoggedInUser().getUserId(), "");
			setDashBoard(dashBoard);
			doFillDashboardList(getDashBoard());
			List<ValueLabel> dashboardlist = getDashboardslist();
			setDashboardslist(dashboardlist);
			dashBoardPosition = getDashBoard().getDashBoardPosition();

		}else{
			dashBoardPosition = getCurrentDashBordPositions();
		}
		
		

		this.firstPortalChildColumn.getChildren().clear();
		this.secondPortalChildColumn.getChildren().clear();
		this.thirdPortalChildColumn.getChildren().clear();
		

		for (String key : getDashBoard().getDashboardConfigMap().keySet()) {
			if (dashBoardPosition.containsKey(key)) {
				column = dashBoardPosition.get(key).getDashboardCol();
				dashboardColIndex = dashBoardPosition.get(key).getDashboardColIndex();
				
				Portalchildren pc = (Portalchildren) dashBoardsPortalLayout.getChildren().get(column);
				dashboardCreate.createPanel(getDashBoard().getDashboardConfigMap().get(key), pc);
			}
		}
		logger.debug("Leaving ");
		return dashboardColIndex;
	}

	/**
	 * Action of click the add Button(add button)
	 */
	public void onClick$addbtn(Event event) {
		logger.debug("Entering " + event.toString());
		int selectedColumnIdx = columnSelect.getSelectedIndex();
		if (cbDashBordsList.getChildren() != null && cbDashBordsList.getChildren().size() > 0) {
			if (dashBoardsPortalLayout.getFellowIfAny(cbDashBordsList.getSelectedItem().getValue().toString()) == null) {
				if (dashBoardsPortalLayout.getChildren().size() > selectedColumnIdx) {
					Portalchildren pc = (Portalchildren) dashBoardsPortalLayout.getChildren().get(selectedColumnIdx);
					dashboardCreate.createPanel(getDashBoard().getDashboardConfigMap().get(this.cbDashBordsList.getSelectedItem().getValue().toString()), pc);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on save button
	 * 
	 * @param event
	 */
	public void onClick$savebtn(Event event) {
		logger.debug("Entering " + event.toString());
		getDashboardConfigurationService().savePositions(new ArrayList<DashboardPosition>(getCurrentDashBordPositions().values()), getUserWorkspace().getLoggedInUser().getUserId());
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
				dashboardPosition.setUsrId(getUserWorkspace().getLoggedInUser().getUserId());
				dashboardPosition.setDashboardRef(p_children.getId());
				dashboardPosition.setDashboardCol(i);
				dashboardPosition.setDashboardRow(j);

				setDashboardColIndex(dashboardPosition);

				dashboardpositionsMap.put(p_children.getId(), dashboardPosition);
				currentDashBords.put(p_children.getId(), p_children.isMaximized());
			}
		}
		return dashboardpositionsMap;
	}

	private void setDashboardColIndex(DashboardPosition dashboardPosition) {
		if(singleColumnBtn != null && singleColumnBtn.isDisabled()) {
			dashboardPosition.setDashboardColIndex(1);
		} else if(twoColumnBtn != null && twoColumnBtn.isDisabled()) {
			dashboardPosition.setDashboardColIndex(2);
		} else if(threeColumnBtn != null && threeColumnBtn.isDisabled()) {
			dashboardPosition.setDashboardColIndex(3);
		}
		
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
		logger.debug("Entering");

		setDashBoardsLayOut(3);
		logger.debug("Leaving");
	}


	/**
	 * When click on Two column button
	 * 
	 * @param event
	 */
	public void onClick$twoColumnBtn(Event event) {
		logger.debug("Entering");
		setDashBoardsLayOut(2);
		logger.debug("Leaving");
	}
	
	private void setDashBoardsLayOut(int layOutType){
		
		Portalchildren firstChild = (Portalchildren)dashBoardsPortalLayout.getChildren().get(0);
		Portalchildren secondChild = (Portalchildren)dashBoardsPortalLayout.getChildren().get(1);
		Portalchildren thirdChild = (Portalchildren)dashBoardsPortalLayout.getChildren().get(2);
		
		switch(layOutType){
		case 1:
			
			firstChild.setWidth("100%");
			secondChild.setWidth("100%");
			thirdChild.setWidth("100%");
	
			singleColumnBtn.setDisabled(true);
			twoColumnBtn.setDisabled(false);
			threeColumnBtn.setDisabled(false);
			break ;
			
		case 2:
			firstChild.setWidth("50%");
			secondChild.setWidth("50%");
			thirdChild.setWidth("50%");
			singleColumnBtn.setDisabled(false);
			twoColumnBtn.setDisabled(true);
			threeColumnBtn.setDisabled(false);
			
			break ;

		case 3:	
			
			firstChild.setWidth("33%");
			secondChild.setWidth("33%");
			thirdChild.setWidth("33%");
	
			singleColumnBtn.setDisabled(false);
			twoColumnBtn.setDisabled(false);
			threeColumnBtn.setDisabled(true);
			break ;
			
		}
		initilizeDashBords(false);
		
	}

	/**
	 * this event will raise for every n seconds .
	 * 
	 * @param event
	 */
	public void onTimer$refreshTimer(Event event) {
		
		if(tabbox == null) {
			return;
		}
		
		if ("tab_Home".equals(tabbox.getSelectedTab().getId())) {
			this.window_Welcome.invalidate();
			this.cbDashBordsList.getItems().clear();
			initilizeDashBords(true);
		}
	}

	/**
	 * On Select DashBoard Tab
	 */
	public synchronized void onDashBoardTabSelected(Event event)
			throws Exception {
		logger.debug("Entering");
		initilizeDashBords(true);
		logger.debug("Leaving");
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
