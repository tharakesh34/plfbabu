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
 *																							*
 * FileName    		:  DashboardConfigurationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.dashboard.dashboardconfiguration;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.dashboard.dashboardconfiguration.model.DashboardConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/DashBoards/ DashboardConfiguration/dashboardConfigurationList.zul
 * file.
 */
public class DashboardConfigurationListCtrl extends GFCBaseListCtrl<DashboardConfiguration> {
	private static final long serialVersionUID = 3577100345663196590L;
	private static final Logger logger = Logger.getLogger(DashboardConfigurationListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DashboardConfigurationList;
	protected Borderlayout borderLayout_DashboardConfigurationList;
	protected Paging pagingDashboardConfigurationList;
	protected Listbox listBoxDashboardConfiguration;

	protected Textbox dashboardCode;
	protected Textbox dashboardDesc;
	protected Combobox dashboardType;
	protected Textbox caption;
	protected Textbox subCaption;
	protected Combobox dimension;

	protected Listbox sortOperator_dashboardCode;
	protected Listbox sortOperator_dashboardDesc;
	protected Listbox sortOperator_dashboardType;
	protected Listbox sortOperator_caption;
	protected Listbox sortOperator_subCaption;
	protected Listbox sortOperator_dimension;

	// List headers
	protected Listheader listheader_DashboardCode;
	protected Listheader listheader_DashboardDesc;
	protected Listheader listheader_DashboardType;

	// checkRights
	protected Button button_DashboardConfigurationList_NewDashboardConfiguration;
	protected Button button_DashboardConfigurationList_Search;

	private transient DashboardConfigurationService dashboardConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public DashboardConfigurationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DashboardConfiguration";
		super.pageRightName = "DashboardConfigurationList";
		super.tableName = "DashboardConfiguration_AView";
		super.queueTableName = "DashboardConfiguration_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DashboardConfigurationList(Event event) {
		// Set the page level components.
		setPageComponents(window_DashboardConfigurationList, borderLayout_DashboardConfigurationList,
				listBoxDashboardConfiguration, pagingDashboardConfigurationList);
		setItemRender(new DashboardConfigurationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DashboardConfigurationList_NewDashboardConfiguration,
				"button_DashboardConfigurationList_NewDashboardDetail", true);
		registerButton(button_DashboardConfigurationList_Search);

		registerField("dashboardCode", listheader_DashboardCode, SortOrder.ASC, dashboardCode,
				sortOperator_dashboardCode, Operators.STRING);
		registerField("dashboardDesc", listheader_DashboardDesc, SortOrder.NONE, dashboardDesc,
				sortOperator_dashboardDesc, Operators.STRING);
		registerField("dashboardType", listheader_DashboardType, SortOrder.NONE, dashboardType,
				sortOperator_dashboardType, Operators.STRING);
		registerField("dimension", dimension, SortOrder.NONE, sortOperator_dimension, Operators.STRING);
		registerField("subCaption", subCaption, SortOrder.NONE, sortOperator_subCaption, Operators.STRING);
		registerField("caption", caption, SortOrder.NONE, sortOperator_caption, Operators.STRING);

		fillComboBox(this.dashboardType, "", PennantStaticListUtil.getDashBoardType(), "");
		fillComboBox(this.dimension, "", PennantStaticListUtil.getChartDimensions(), "");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_DashboardConfigurationList_Search(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_DashboardConfigurationList_NewDashboardConfiguration(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		DashboardConfiguration dashboardConfiguration = new DashboardConfiguration();
		dashboardConfiguration.setNewRecord(true);
		dashboardConfiguration.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(dashboardConfiguration);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDashboardDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDashboardConfiguration.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		DashboardConfiguration dashboardConfiguration = dashboardConfigurationService.getDashboardDetailById(id);

		if (dashboardConfiguration == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND DashboardCode='" + dashboardConfiguration.getDashboardCode() + "' AND version="
				+ dashboardConfiguration.getVersion() + " ";

		if (doCheckAuthority(dashboardConfiguration, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && dashboardConfiguration.getWorkflowId() == 0) {
				dashboardConfiguration.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(dashboardConfiguration);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aDashboardConfiguration
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DashboardConfiguration aDashboardConfiguration) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("dashboardConfiguration", aDashboardConfiguration);
		arg.put("dashboardConfigurationListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/DashBoards/DashBoardConfiguration/DashboardConfigurationDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setDashboardConfigurationService(DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}

}