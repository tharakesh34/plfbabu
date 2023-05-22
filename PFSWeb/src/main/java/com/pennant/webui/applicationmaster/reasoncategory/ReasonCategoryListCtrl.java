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
 * * FileName : ReasonCategoryListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-12-2017 * *
 * Modified Date : 19-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-12-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.reasoncategory;

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

import com.pennant.backend.model.applicationmaster.ReasonCategory;
import com.pennant.backend.service.applicationmaster.ReasonCategoryService;
import com.pennant.webui.applicationmaster.reasoncategory.model.ReasonCategoryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ReasonCategory/ReasonCategoryList.zul file.
 * 
 */
public class ReasonCategoryListCtrl extends GFCBaseListCtrl<ReasonCategory> {
	private static final long serialVersionUID = 1L;

	protected Window window_ReasonCategoryList;
	protected Borderlayout borderLayout_ReasonCategoryList;
	protected Paging pagingReasonCategoryList;
	protected Listbox listBoxReasonCategory;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;

	// checkRights
	protected Button button_ReasonCategoryList_NewReasonCategory;
	protected Button button_ReasonCategoryList_ReasonCategorySearch;

	// Search Fields
	protected Textbox code; // autowired
	protected Textbox description; // autowired

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;

	private transient ReasonCategoryService reasonCategoryService;

	/**
	 * default constructor.<br>
	 */
	public ReasonCategoryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReasonCategory";
		super.pageRightName = "ReasonCategoryList";
		super.tableName = "ReasonCategory_AView";
		super.queueTableName = "ReasonCategory_View";
		super.enquiryTableName = "ReasonCategory_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReasonCategoryList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ReasonCategoryList, borderLayout_ReasonCategoryList, listBoxReasonCategory,
				pagingReasonCategoryList);
		setItemRender(new ReasonCategoryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ReasonCategoryList_ReasonCategorySearch);
		registerButton(button_ReasonCategoryList_NewReasonCategory, "button_ReasonCategoryList_NewReasonCategory",
				true);

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
	public void onClick$button_ReasonCategoryList_ReasonCategorySearch(Event event) {
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
	public void onClick$button_ReasonCategoryList_NewReasonCategory(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ReasonCategory reasoncategory = new ReasonCategory();
		reasoncategory.setNewRecord(true);
		reasoncategory.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(reasoncategory);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onReasonCategoryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReasonCategory.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		ReasonCategory reasoncategory = reasonCategoryService.getReasonCategory(id);

		if (reasoncategory == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(reasoncategory, whereCond.toString(), new Object[] { reasoncategory.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && reasoncategory.getWorkflowId() == 0) {
				reasoncategory.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(reasoncategory);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param reasoncategory The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ReasonCategory reasoncategory) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("reasonCategory", reasoncategory);
		arg.put("reasonCategoryListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/ReasonCategory/ReasonCategoryDialog.zul",
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

	public void setReasonCategoryService(ReasonCategoryService reasonCategoryService) {
		this.reasonCategoryService = reasonCategoryService;
	}
}