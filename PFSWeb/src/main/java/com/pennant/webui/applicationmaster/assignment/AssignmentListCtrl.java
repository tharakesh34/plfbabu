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
 * * FileName : AssignmentListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * * Modified
 * Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.assignment;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.service.applicationmaster.AssignmentService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.systemmasters.assignment.model.AssignmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Assignment/AssignmentList.zul file.
 * 
 */
public class AssignmentListCtrl extends GFCBaseListCtrl<Assignment> {
	private static final long serialVersionUID = 1L;

	protected Window window_AssignmentList;
	protected Borderlayout borderLayout_AssignmentList;
	protected Paging pagingAssignmentList;
	protected Listbox listBoxAssignment;

	// List headers
	protected Listheader listheader_AssignmentId;
	protected Listheader listheader_DealCode;
	protected Listheader listheader_LoanType;
	protected Listheader listheader_DisbDate;
	protected Listheader listheader_OpexFeeType;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_AssignmentList_NewAssignment;
	protected Button button_AssignmentList_AssignmentSearch;

	// Search Fields
	protected Intbox assignmentId; // autowired
	protected Textbox dealCode; // autowired
	protected Textbox loanType; // autowired
	protected Datebox disbDate; // autowired
	protected Combobox opexFeeType; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_AssignmentId;
	protected Listbox sortOperator_DealCode;
	protected Listbox sortOperator_LoanType;
	protected Listbox sortOperator_DisbDate;
	protected Listbox sortOperator_OpexFeeType;
	protected Listbox sortOperator_Active;
	private List<ValueLabel> opexFeeTypeList = PennantStaticListUtil.getOpexFeeTypes();
	@Autowired
	private transient AssignmentService assignmentService;

	/**
	 * default constructor.<br>
	 */
	public AssignmentListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Assignment";
		super.pageRightName = "AssignmentList";
		super.tableName = "Assignment_AView";
		super.queueTableName = "Assignment_View";
		super.enquiryTableName = "Assignment_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssignmentList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		doSetFieldProperties();
		setPageComponents(window_AssignmentList, borderLayout_AssignmentList, listBoxAssignment, pagingAssignmentList);
		setItemRender(new AssignmentListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssignmentList_AssignmentSearch);
		registerButton(button_AssignmentList_NewAssignment, "button_AssignmentList_NewAssignment", true);

		registerField("id", listheader_AssignmentId, SortOrder.NONE, assignmentId, sortOperator_AssignmentId,
				Operators.STRING);
		registerField("dealCode", listheader_DealCode, SortOrder.NONE, dealCode, sortOperator_DealCode,
				Operators.STRING);
		registerField("loanType", listheader_LoanType, SortOrder.NONE, loanType, sortOperator_LoanType,
				Operators.STRING);
		registerField("disbDate", listheader_DisbDate, SortOrder.NONE, disbDate, sortOperator_DisbDate, Operators.DATE);
		registerField("sharingPercentage");
		registerField("gST");
		registerField("opexFeeType", listheader_OpexFeeType, SortOrder.NONE, opexFeeType, sortOperator_OpexFeeType,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		fillComboBox(opexFeeType, "", opexFeeTypeList, "");
		this.disbDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AssignmentList_AssignmentSearch(Event event) {
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
	public void onClick$button_AssignmentList_NewAssignment(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		Assignment assignment = new Assignment();
		assignment.setNewRecord(true);
		assignment.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(assignment);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssignmentItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssignment.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		Assignment assignment = assignmentService.getAssignment(id);

		if (assignment == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id = ?");

		if (doCheckAuthority(assignment, whereCond.toString(), new Object[] { assignment.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assignment.getWorkflowId() == 0) {
				assignment.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assignment);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param assignment The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Assignment assignment) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assignment", assignment);
		arg.put("assignmentListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Assignment/AssignmentDialog.zul", null, arg);
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

}