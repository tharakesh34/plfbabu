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
 * * FileName : ReasonCodeListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-12-2017 * * Modified
 * Date : 19-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-12-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.reasoncode;

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

import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.service.applicationmaster.ReasonCodeService;
import com.pennant.webui.applicationmaster.reasoncode.model.ReasonCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ReasonCode/ReasonCodeList.zul file.
 * 
 */
public class ReasonCodeListCtrl extends GFCBaseListCtrl<ReasonCode> {
	private static final long serialVersionUID = 1L;

	protected Window window_ReasonCodeList;
	protected Borderlayout borderLayout_ReasonCodeList;
	protected Paging pagingReasonCodeList;
	protected Listbox listBoxReasonCode;

	// List headers
	protected Listheader listheader_ReasonTypeID;
	protected Listheader listheader_ReasonCategoryID;
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_ReasonCodeList_NewReasonCode;
	protected Button button_ReasonCodeList_ReasonCodeSearch;

	// Search Fields
	protected Textbox reasonTypeID; // autowired
	protected Textbox reasonCategoryID; // autowired
	protected Textbox code; // autowired
	protected Textbox description; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_ReasonTypeID;
	protected Listbox sortOperator_ReasonCategoryID;
	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Active;

	private transient ReasonCodeService reasionCodeService;

	/**
	 * default constructor.<br>
	 */
	public ReasonCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReasonCode";
		super.pageRightName = "ReasonCodeList";
		super.tableName = "Reasons_AView";
		super.queueTableName = "Reasons_View";
		super.enquiryTableName = "Reasons_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReasonCodeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ReasonCodeList, borderLayout_ReasonCodeList, listBoxReasonCode, pagingReasonCodeList);
		setItemRender(new ReasonCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ReasonCodeList_ReasonCodeSearch);
		registerButton(button_ReasonCodeList_NewReasonCode, "button_ReasonCodeList_NewReasonCode", true);

		registerField("id");
		registerField("ReasonTypeCode", listheader_ReasonTypeID, SortOrder.NONE, reasonTypeID,
				sortOperator_ReasonTypeID, Operators.STRING);
		registerField("ReasonCategoryCode", listheader_ReasonCategoryID, SortOrder.NONE, reasonCategoryID,
				sortOperator_ReasonCategoryID, Operators.STRING);
		registerField("code", listheader_Code, SortOrder.NONE, code, sortOperator_Code, Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
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
	public void onClick$button_ReasonCodeList_ReasonCodeSearch(Event event) {
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
	public void onClick$button_ReasonCodeList_NewReasonCode(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ReasonCode reasoncode = new ReasonCode();
		reasoncode.setNewRecord(true);
		reasoncode.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(reasoncode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onReasonCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReasonCode.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		ReasonCode reasoncode = reasionCodeService.getReasonCode(id);

		if (reasoncode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =?");

		if (doCheckAuthority(reasoncode, whereCond.toString(), new Object[] { reasoncode.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && reasoncode.getWorkflowId() == 0) {
				reasoncode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(reasoncode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param reasoncode The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ReasonCode reasoncode) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("reasonCode", reasoncode);
		arg.put("reasonCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/ReasonCode/ReasonCodeDialog.zul", null, arg);
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

	public void setReasonCodeService(ReasonCodeService reasionCodeService) {
		this.reasionCodeService = reasionCodeService;
	}
}
