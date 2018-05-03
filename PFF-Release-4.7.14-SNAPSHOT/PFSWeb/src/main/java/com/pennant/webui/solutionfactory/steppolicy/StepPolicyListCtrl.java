/**
Copyright 2011 - Pennant Technologies
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
 * FileName    		:  StepPolicyHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.solutionfactory.steppolicy;

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

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.webui.solutionfactory.steppolicy.model.StepPolicyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/StepPolicyHeader/StepPolicyHeaderList.zul file.
 */
public class StepPolicyListCtrl extends GFCBaseListCtrl<StepPolicyHeader> {
	private static final long serialVersionUID = -1491703348215991538L;
	private static final Logger logger = Logger.getLogger(StepPolicyListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_StepPolicyList;
	protected Borderlayout borderLayout_StepPolicyList;
	protected Paging pagingStepPolicyList;
	protected Listbox listBoxStepPolicy;

	protected Listheader listheader_PolicyCode;
	protected Listheader listheader_PolicyDesc;
	protected Listheader listheader_Steps;
	protected Listheader listheader_TenorSplit;
	protected Listheader listheader_RateStepMargin;
	protected Listheader listheader_EMISteps;

	protected Button button_StepPolicyList_NewStepPolicy;
	protected Button button_StepPolicyList_StepPolicySearchDialog;

	protected Textbox policyCode;
	protected Textbox policyDesc;

	protected Listbox sortOperator_policyCode;
	protected Listbox sortOperator_policyDesc;

	private transient StepPolicyService stepPolicyService;

	/**
	 * default constructor.<br>
	 */
	public StepPolicyListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "StepPolicyHeader";
		super.pageRightName = "StepPolicyList";
		super.tableName = "StepPolicyHeaderDetails_AView";
		super.queueTableName = "StepPolicyHeaderDetails_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_StepPolicyList(Event event) {
		// Set the page level components.
		setPageComponents(window_StepPolicyList, borderLayout_StepPolicyList, listBoxStepPolicy, pagingStepPolicyList);
		setItemRender(new StepPolicyListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_StepPolicyList_NewStepPolicy, "button_StepPolicyList_NewStepPolicy", true);
		registerButton(button_StepPolicyList_StepPolicySearchDialog);

		registerField("PolicyCode", listheader_PolicyCode, SortOrder.ASC, policyCode, sortOperator_policyCode,
				Operators.STRING);
		registerField("policyDesc", listheader_PolicyDesc, SortOrder.NONE, policyDesc, sortOperator_policyDesc,
				Operators.STRING);
		registerField("stepNumber", listheader_Steps, SortOrder.NONE);
		registerField("tenorSplitPerc", listheader_TenorSplit, SortOrder.NONE);
		registerField("rateMargin", listheader_RateStepMargin, SortOrder.NONE);
		registerField("emiSplitPerc", listheader_EMISteps, SortOrder.NONE);

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
	public void onClick$button_StepPolicyList_StepPolicySearchDialog(Event event) {
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
	public void onClick$button_StepPolicyList_NewStepPolicy(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		StepPolicyHeader stepPolicyHeader = new StepPolicyHeader();
		stepPolicyHeader.setNewRecord(true);
		stepPolicyHeader.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(stepPolicyHeader);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onStepPolicyHeaderItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxStepPolicy.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		StepPolicyHeader stepPolicyHeader = stepPolicyService.getStepPolicyHeaderById(id);

		if (stepPolicyHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND PolicyCode='" + stepPolicyHeader.getPolicyCode() + "' AND version="
				+ stepPolicyHeader.getVersion() + " ";

		if (doCheckAuthority(stepPolicyHeader, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && stepPolicyHeader.getWorkflowId() == 0) {
				stepPolicyHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(stepPolicyHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aStepPolicyHeader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(StepPolicyHeader aStepPolicyHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("stepPolicyHeader", aStepPolicyHeader);
		arg.put("stepPolicyListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/StepPolicy/StepPolicyDialog.zul", null, arg);
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

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
}