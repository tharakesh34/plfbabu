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
 * FileName    		:  ReportConfigurationListCtrl.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.reports.reportconfiguration;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.webui.reports.reportconfiguration.model.ReportConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Masters/ReportConfiguration/ReportConfigurationList.zul
 * file.
 */
public class ReportConfigurationListCtrl extends GFCBaseListCtrl<ReportConfiguration> {
	private static final long serialVersionUID = -7603242416503761389L;
	private static final Logger logger = Logger.getLogger(ReportConfigurationListCtrl.class);

	protected Window 		window_ReportConfigurationList; 				
	protected Borderlayout 	borderLayout_ReportConfigurationList; 			
	protected Paging 		pagingReportConfigurationList; 				
	protected Listbox 		listBoxReportConfiguration;

	protected Listbox 		sortOperator_ReportName; 				
	protected Listbox 		sortOperator_ReportHeading; 			
	protected Listbox 		sortOperator_PromptRequired; 				
	protected Listbox 		sortOperator_ReportJasperName; 
	
	// List headers
	protected Listheader 	listheader_ReportName; 				
	protected Listheader 	listheader_ReportHeading; 				
	protected Listheader 	listheader_PromptRequired; 				
	protected Listheader 	listheader_ReportJasperName; 			
	protected Listheader 	listheader_menuItemCode;				


	protected Textbox		reportName;							
	protected Textbox		reportHeading;							
	protected Checkbox		promptRequired;							
	protected Textbox		reportJasperName;						
	protected Textbox		menuItemCode;						


	protected Button 		button_ReportConfigurationList_NewReportConfiguration; 	
	protected Button 		button_ReportConfigurationList_ReportConfigurationSearch; 
	
	int 	  listRows;

	private transient ReportConfigurationService reportConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public ReportConfigurationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReportConfiguration";
		super.pageRightName = "ReportConfigurationList";
		super.tableName = "ReportConfiguration_View";
		super.queueTableName = "ReportConfiguration_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReportConfigurationList(Event event) {
		// Set the page level components.
		setPageComponents(window_ReportConfigurationList, borderLayout_ReportConfigurationList, listBoxReportConfiguration, pagingReportConfigurationList);
		setItemRender(new ReportConfigurationListModelItemRenderer());

		// Register buttons and fields.
		//registerButton(button_ReportConfigurationList_NewReportConfiguration, "button_ReportConfigurationList_NewReportConfiguration", true);
		registerButton(button_ReportConfigurationList_ReportConfigurationSearch);

		registerField("REPORTID", SortOrder.ASC);
		registerField("reportName", listheader_ReportName, SortOrder.NONE, reportName,
				sortOperator_ReportName, Operators.STRING);
		registerField("reportHeading", listheader_ReportHeading, SortOrder.NONE, reportHeading,
				sortOperator_ReportHeading, Operators.STRING);
		registerField("promptRequired", listheader_PromptRequired, SortOrder.NONE, promptRequired, sortOperator_PromptRequired,
				Operators.BOOLEAN);
		registerField("reportJasperName", listheader_ReportJasperName, SortOrder.NONE, reportJasperName, sortOperator_ReportJasperName,
				Operators.STRING);

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
	public void onClick$button_ReportConfigurationList_ReportConfigurationSearch(Event event) {
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
	public void onClick$button_ReportConfigurationList_NewReportConfiguration(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ReportConfiguration aReportConfiguration = new ReportConfiguration();
		aReportConfiguration.setNewRecord(true);
		aReportConfiguration.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aReportConfiguration);

		logger.debug("Leaving");
	}
	
	
	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onReportConfigurationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReportConfiguration.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		ReportConfiguration aReportConfiguration = reportConfigurationService.getReportConfigurationById(id);

		if (aReportConfiguration == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ReportId='" + aReportConfiguration.getReportID() + "' AND version="
				+ aReportConfiguration.getVersion() + " ";

		if (doCheckAuthority(aReportConfiguration, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aReportConfiguration.getWorkflowId() == 0) {
				aReportConfiguration.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aReportConfiguration);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ReportConfiguration aReportConfiguration) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("reportConfiguration", aReportConfiguration);
		arg.put("reportConfigurationListCtrl", this);
		arg.put("moduleCode", super.moduleCode);

		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportConfiguration/ReportConfigurationDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void setReportConfigurationService(ReportConfigurationService reportConfigurationService) {
		this.reportConfigurationService = reportConfigurationService;
	}

}