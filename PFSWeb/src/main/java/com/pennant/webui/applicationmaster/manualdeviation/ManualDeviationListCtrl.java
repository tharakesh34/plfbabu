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
 * * FileName : ManualDeviationListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-04-2018 * *
 * Modified Date : 03-04-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-04-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.manualdeviation;

import java.util.Map;

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

import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.service.applicationmaster.ManualDeviationService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/ManualDeviation/ManualDeviationList.zul file.
 * 
 */
public class ManualDeviationListCtrl extends GFCBaseListCtrl<ManualDeviation> {
	private static final long serialVersionUID = 1L;

	protected Window window_ManualDeviationList;
	protected Borderlayout borderLayout_ManualDeviationList;
	protected Paging pagingManualDeviationList;
	protected Listbox listBoxManualDeviation;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_Module;
	protected Listheader listheader_Categorization;
	protected Listheader listheader_Severity;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_ManualDeviationList_NewManualDeviation;
	protected Button button_ManualDeviationList_ManualDeviationSearch;

	// Search Fields
	protected Textbox code; // autowired
	protected Textbox description; // autowired
	protected Textbox module; // autowired
	protected Textbox categorization; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Module;
	protected Listbox sortOperator_Categorization;
	protected Listbox sortOperator_Severity;
	protected Listbox sortOperator_Active;

	private transient ManualDeviationService manualDeviationService;

	/**
	 * default constructor.<br>
	 */
	public ManualDeviationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ManualDeviation";
		super.pageRightName = "ManualDeviationList";
		super.tableName = "ManualDeviations_AView";
		super.queueTableName = "ManualDeviations_View";
		super.enquiryTableName = "ManualDeviations_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ManualDeviationList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ManualDeviationList, borderLayout_ManualDeviationList, listBoxManualDeviation,
				pagingManualDeviationList);
		setItemRender(new ManualDeviationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ManualDeviationList_ManualDeviationSearch);
		registerButton(button_ManualDeviationList_NewManualDeviation, "button_ManualDeviationList_NewManualDeviation",
				true);

		registerField("deviationID");
		registerField("code", listheader_Code, SortOrder.NONE, code, sortOperator_Code, Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
		registerField("module", listheader_Module, SortOrder.NONE, module, sortOperator_Module, Operators.STRING);
		registerField("categorizationName", listheader_Categorization, SortOrder.NONE, categorization,
				sortOperator_Categorization, Operators.STRING);
		registerField("severity", listheader_Severity, SortOrder.NONE);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ManualDeviationList_ManualDeviationSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ManualDeviationList_NewManualDeviation(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ManualDeviation manualdeviation = new ManualDeviation();
		manualdeviation.setNewRecord(true);
		manualdeviation.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(manualdeviation);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onManualDeviationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxManualDeviation.getSelectedItem();
		final long deviationID = (long) selectedItem.getAttribute("deviationID");
		ManualDeviation manualdeviation = manualDeviationService.getManualDeviation(deviationID);

		if (manualdeviation == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  DeviationID =? ");

		if (doCheckAuthority(manualdeviation, whereCond.toString(),
				new Object[] { manualdeviation.getDeviationID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && manualdeviation.getWorkflowId() == 0) {
				manualdeviation.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(manualdeviation);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param manualdeviation The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ManualDeviation manualdeviation) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("manualDeviation", manualdeviation);
		arg.put("manualDeviationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/ManualDeviation/ManualDeviationDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setManualDeviationService(ManualDeviationService manualDeviationService) {
		this.manualDeviationService = manualDeviationService;
	}
}