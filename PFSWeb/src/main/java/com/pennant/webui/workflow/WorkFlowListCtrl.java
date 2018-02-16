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
 * FileName    		:  WorkFlowListCtl.java													*                           
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
package com.pennant.webui.workflow;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.workflow.model.WorkFlowListModelItemRenderer;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /PFSWeb/WebContent/WEB-INF/pages/SolutionFactory/workFlow/workFlowList.zul file.
 */
public class WorkFlowListCtrl extends GFCBaseListCtrl<WorkFlowDetails> {
	private static final long serialVersionUID = -1635165456608902454L;
	private static final Logger logger = Logger.getLogger(WorkFlowListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_workFlowList;
	protected Borderlayout borderLayout_workFlowList;
	protected Paging pagingWorkFlowList;
	protected Listbox listBoxWorkFlow;

	protected Listheader listheader_workFlowType;
	protected Listheader listheader_workFlowSubType;
	protected Listheader listheader_workFlowDesc;
	protected Listheader listheader_workFlowStatus;

	protected Button button_workFlowList_NewworkFlow;
	protected Button button_workFlowList_workFlowFindDialog;

	protected Textbox workFlowType;
	protected Textbox workFlowSubType;
	protected Textbox workFLowDescription;

	protected Listbox sortOperator_workFlowType;
	protected Listbox sortOperator_workFlowSubType;
	protected Listbox sortOperator_description;

	private transient WorkFlowDetailsService workFlowDetailsService;

	/**
	 * default constructor.<br>
	 */
	public WorkFlowListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "WorkFlowDetails";
		super.pageRightName = "WorkFlowList";
		super.tableName = "WorkFlowDetails";
		super.queueTableName = "WorkFlowDetails";
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_workFlowList(Event event) {
		// Set the page level components.
		setPageComponents(window_workFlowList, borderLayout_workFlowList,
				listBoxWorkFlow, pagingWorkFlowList);
		setItemRender(new WorkFlowListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_workFlowList_NewworkFlow,
				"button_workFlowList_NewworkFlow", true);
		registerButton(button_workFlowList_workFlowFindDialog);

		registerField("workFlowId");
		registerField("workFlowType", listheader_workFlowType, SortOrder.ASC,
				workFlowType, sortOperator_workFlowType, Operators.STRING);
		registerField("workFlowSubType", listheader_workFlowSubType,
				SortOrder.NONE, workFlowSubType, sortOperator_workFlowSubType,
				Operators.STRING);
		registerField("workFlowDesc", listheader_workFlowDesc, SortOrder.NONE,
				workFLowDescription, sortOperator_description, Operators.STRING);
		registerField("workFlowActive", listheader_workFlowStatus);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilter(new Filter("WorkFlowActive",1,Filter.OP_EQUAL));
	}
	
	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_workFlowList_workFlowFindDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}
	
	public void doReset() {
		logger.debug("Entering");
		super.doReset();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the new button.
	 * Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_workFlowList_NewworkFlow(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		WorkFlowDetails workFlowDetails = new WorkFlowDetails();
		workFlowDetails.setNewRecord(true);
		workFlowDetails.setWorkFlowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(workFlowDetails);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view
	 * it's details. Show the dialog page with the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onWorkFlowItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxWorkFlow.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		WorkFlowDetails workFlowDetails = workFlowDetailsService
				.getWorkFlowDetailsByID(id);

		if (workFlowDetails == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND WorkFlowId='"
				+ workFlowDetails.getWorkFlowId() + "' AND version="
				+ workFlowDetails.getVersion() + " ";

		if (doCheckAuthority(workFlowDetails, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && workFlowDetails.getWorkFlowId() == 0) {
				workFlowDetails.setWorkFlowId(getWorkFlowId());
			}
			doShowDialogPage(workFlowDetails);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param workFlowDetails
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(WorkFlowDetails workFlowDetails) {
		logger.debug("Entering");
		
		Map<String, Object> arg = getDefaultArguments();
		arg.put("workFlowDetails", workFlowDetails);
		arg.put("workFlowListCtrl", this);

		try {
			Executions
			.createComponents(
					"/WEB-INF/pages/SolutionFactory/workFlow/WorkflowDesign.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	/**
	 * The framework calls this event handler when user clicks the print button
	 * to print the results.
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

	public void setWorkFlowDetailsService(
			WorkFlowDetailsService workFlowDetailsService) {
		this.workFlowDetailsService = workFlowDetailsService;
	}
}