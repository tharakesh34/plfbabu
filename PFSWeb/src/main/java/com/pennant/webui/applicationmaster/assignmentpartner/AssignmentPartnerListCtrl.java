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
 * * FileName : AssignmentPartnerListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * *
 * Modified Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.assignmentpartner;

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

import com.pennant.backend.model.applicationmaster.AssignmentPartner;
import com.pennant.backend.service.applicationmaster.AssignmentPartnerService;
import com.pennant.webui.applicationmaster.assignmentpartner.model.AssignmentPartnerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/AssignmentPartner/AssignmentPartnerList.zul
 * file.
 * 
 */
public class AssignmentPartnerListCtrl extends GFCBaseListCtrl<AssignmentPartner> {
	private static final long serialVersionUID = 1L;

	protected Window window_AssignmentPartnerList;
	protected Borderlayout borderLayout_AssignmentPartnerList;
	protected Paging pagingAssignmentPartnerList;
	protected Listbox listBoxAssignmentPartner;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_EntityCode;
	protected Listheader listheader_GLCode;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_AssignmentPartnerList_NewAssignmentPartner;
	protected Button button_AssignmentPartnerList_AssignmentPartnerSearch;

	// Search Fields
	protected Textbox code; // autowired
	protected Textbox entityCode; // autowired
	protected Textbox gLCode; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_EntityCode;
	protected Listbox sortOperator_GLCode;
	protected Listbox sortOperator_Active;

	private transient AssignmentPartnerService assignmentPartnerService;

	/**
	 * default constructor.<br>
	 */
	public AssignmentPartnerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssignmentPartner";
		super.pageRightName = "AssignmentPartnerList";
		super.tableName = "AssignmentPartner_AView";
		super.queueTableName = "AssignmentPartner_View";
		super.enquiryTableName = "AssignmentPartner_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssignmentPartnerList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AssignmentPartnerList, borderLayout_AssignmentPartnerList, listBoxAssignmentPartner,
				pagingAssignmentPartnerList);
		setItemRender(new AssignmentPartnerListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssignmentPartnerList_AssignmentPartnerSearch);
		registerButton(button_AssignmentPartnerList_NewAssignmentPartner,
				"button_AssignmentPartnerList_NewAssignmentPartner", true);

		registerField("id");
		registerField("code", listheader_Code, SortOrder.NONE, code, sortOperator_Code, Operators.STRING);
		registerField("entityCodeName", listheader_EntityCode, SortOrder.NONE, entityCode, sortOperator_EntityCode,
				Operators.STRING);
		registerField("gLCode", listheader_GLCode, SortOrder.NONE, gLCode, sortOperator_GLCode, Operators.STRING);
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
	public void onClick$button_AssignmentPartnerList_AssignmentPartnerSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param entityCodeName An event sent to the event handler of the component.
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
	public void onClick$button_AssignmentPartnerList_NewAssignmentPartner(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		AssignmentPartner assignmentpartner = new AssignmentPartner();
		assignmentpartner.setNewRecord(true);
		assignmentpartner.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(assignmentpartner);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssignmentPartnerItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssignmentPartner.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		AssignmentPartner assignmentpartner = assignmentPartnerService.getAssignmentPartner(id);

		if (assignmentpartner == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id = ?");

		if (doCheckAuthority(assignmentpartner, whereCond.toString(), new Object[] { assignmentpartner.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assignmentpartner.getWorkflowId() == 0) {
				assignmentpartner.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assignmentpartner);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param assignmentpartner The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AssignmentPartner assignmentpartner) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assignmentPartner", assignmentpartner);
		arg.put("assignmentPartnerListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/AssignmentPartner/AssignmentPartnerDialog.zul", null, arg);
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

	public void setAssignmentPartnerService(AssignmentPartnerService assignmentPartnerService) {
		this.assignmentPartnerService = assignmentPartnerService;
	}
}