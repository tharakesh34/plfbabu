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
 * * FileName : BounceReasonListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified
 * Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.bouncereason;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.service.applicationmaster.BounceReasonService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.applicationmaster.bouncereason.model.BounceReasonListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/BounceReason/BounceReasonList.zul
 * file.
 * 
 */
public class BounceReasonListCtrl extends GFCBaseListCtrl<BounceReason> {
	private static final long serialVersionUID = 1L;

	protected Window window_BounceReasonList;
	protected Borderlayout borderLayout_BounceReasonList;
	protected Paging pagingBounceReasonList;
	protected Listbox listBoxBounceReason;

	// List headers
	protected Listheader listheader_BounceCode;
	protected Listheader listheader_ReasonType;
	protected Listheader listheader_Category;
	protected Listheader listheader_ReturnCode;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_BounceReasonList_NewBounceReason;
	protected Button button_BounceReasonList_BounceReasonSearch;

	// Search Fields
	protected Textbox bounceCode; // autowired
	protected Combobox reasonType; // autowired
	protected Combobox category; // autowired
	protected Textbox returnCode; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_BounceCode;
	protected Listbox sortOperator_ReasonType;
	protected Listbox sortOperator_Category;
	protected Listbox sortOperator_ReturnCode;
	protected Listbox sortOperator_Active;

	private transient BounceReasonService bounceReasonService;

	/**
	 * default constructor.<br>
	 */
	public BounceReasonListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BounceReason";
		super.pageRightName = "BounceReasonList";
		super.tableName = "BounceReasons_AView";
		super.queueTableName = "BounceReasons_View";
		super.enquiryTableName = "BounceReasons_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BounceReasonList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_BounceReasonList, borderLayout_BounceReasonList, listBoxBounceReason,
				pagingBounceReasonList);
		setItemRender(new BounceReasonListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BounceReasonList_BounceReasonSearch);
		registerButton(button_BounceReasonList_NewBounceReason, "button_BounceReasonList_NewBounceReason", true);

		fillList(reasonType, PennantStaticListUtil.getReasonType(), null);
		fillList(category, PennantStaticListUtil.getCategoryType(), null);

		registerField("bounceID");
		registerField("bounceCode", listheader_BounceCode, SortOrder.NONE, bounceCode, sortOperator_BounceCode,
				Operators.STRING);
		registerField("reasonType", listheader_ReasonType, SortOrder.NONE, reasonType, sortOperator_ReasonType,
				Operators.SIMPLE_NUMARIC);
		registerField("category", listheader_Category, SortOrder.NONE, category, sortOperator_Category,
				Operators.SIMPLE_NUMARIC);
		registerField("reason");
		registerField("action");
		registerField("ruleID");
		registerField("returnCode", listheader_ReturnCode, SortOrder.NONE, returnCode, sortOperator_ReturnCode,
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
	public void onClick$button_BounceReasonList_BounceReasonSearch(Event event) {
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
	public void onClick$button_BounceReasonList_NewBounceReason(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		BounceReason bouncereason = new BounceReason();
		bouncereason.setNewRecord(true);
		bouncereason.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(bouncereason);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onBounceReasonItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBounceReason.getSelectedItem();
		final long bounceID = (long) selectedItem.getAttribute("bounceID");
		BounceReason bouncereason = bounceReasonService.getBounceReason(bounceID);

		if (bouncereason == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  BounceID =?");

		if (doCheckAuthority(bouncereason, whereCond.toString(), new Object[] { bouncereason.getBounceID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && bouncereason.getWorkflowId() == 0) {
				bouncereason.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(bouncereason);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param bouncereason The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BounceReason bouncereason) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("bounceReason", bouncereason);
		arg.put("bounceReasonListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/BounceReason/BounceReasonDialog.zul", null,
					arg);
		} catch (Exception e) {
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

	public void setBounceReasonService(BounceReasonService bounceReasonService) {
		this.bounceReasonService = bounceReasonService;
	}
}