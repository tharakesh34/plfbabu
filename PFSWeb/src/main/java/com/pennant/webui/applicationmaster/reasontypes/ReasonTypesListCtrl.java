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
 * * FileName : ReasonTypesListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-12-2017 * * Modified
 * Date : 19-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-12-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.reasontypes;

import java.util.Map;

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

import com.pennant.backend.model.applicationmaster.ReasonTypes;
import com.pennant.backend.service.applicationmaster.ReasonTypesService;
import com.pennant.webui.applicationmaster.reasontypes.model.ReasonTypesListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ReasonTypes/ReasonTypesList.zul file.
 * 
 */
public class ReasonTypesListCtrl extends GFCBaseListCtrl<ReasonTypes> {
	private static final long serialVersionUID = 1L;

	protected Window window_ReasonTypesList;
	protected Borderlayout borderLayout_ReasonTypesList;
	protected Paging pagingReasonTypesList;
	protected Listbox listBoxReasonTypes;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;

	// checkRights
	protected Button button_ReasonTypesList_NewReasonTypes;
	protected Button button_ReasonTypesList_ReasonTypesSearch;

	// Search Fields
	protected Textbox code; // autowired
	protected Textbox description; // autowired

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;

	private transient ReasonTypesService reasonTypesService;

	/**
	 * default constructor.<br>
	 */
	public ReasonTypesListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReasonTypes";
		super.pageRightName = "ReasonTypesList";
		super.tableName = "ReasonTypes_AView";
		super.queueTableName = "ReasonTypes_View";
		super.enquiryTableName = "ReasonTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReasonTypesList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ReasonTypesList, borderLayout_ReasonTypesList, listBoxReasonTypes,
				pagingReasonTypesList);
		setItemRender(new ReasonTypesListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ReasonTypesList_ReasonTypesSearch);
		registerButton(button_ReasonTypesList_NewReasonTypes, "button_ReasonTypesList_NewReasonTypes", true);

		registerField("id");
		registerField("code", listheader_Code, SortOrder.NONE, code, sortOperator_Code, Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ReasonTypesList_ReasonTypesSearch(Event event) {
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
	public void onClick$button_ReasonTypesList_NewReasonTypes(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ReasonTypes reasontypes = new ReasonTypes();
		reasontypes.setNewRecord(true);
		reasontypes.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(reasontypes);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onReasonTypesItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReasonTypes.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		ReasonTypes reasontypes = reasonTypesService.getReasonTypes(id);

		if (reasontypes == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(reasontypes, whereCond.toString(), new Object[] { reasontypes.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && reasontypes.getWorkflowId() == 0) {
				reasontypes.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(reasontypes);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param reasontypes The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ReasonTypes reasontypes) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("reasonTypes", reasontypes);
		arg.put("reasonTypesListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/ReasonTypes/ReasonTypesDialog.zul", null,
					arg);
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

	public void setReasonTypesService(ReasonTypesService reasonTypesService) {
		this.reasonTypesService = reasonTypesService;
	}
}