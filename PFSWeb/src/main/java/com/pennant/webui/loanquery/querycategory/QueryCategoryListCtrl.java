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
 * * FileName : QueryCategoryListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-05-2018 * *
 * Modified Date : 08-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-05-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.loanquery.querycategory;

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

import com.pennant.backend.model.loanquery.QueryCategory;
import com.pennant.backend.service.loanquery.QueryCategoryService;
import com.pennant.webui.loanquery.querycategory.model.QueryCategoryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/LoanQuery/QueryCategory/QueryCategoryList.zul file.
 * 
 */
public class QueryCategoryListCtrl extends GFCBaseListCtrl<QueryCategory> {
	private static final long serialVersionUID = 1L;

	protected Window window_QueryCategoryList;
	protected Borderlayout borderLayout_QueryCategoryList;
	protected Paging pagingQueryCategoryList;
	protected Listbox listBoxQueryCategory;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_QueryCategoryList_NewQueryCategory;
	protected Button button_QueryCategoryList_QueryCategorySearch;

	// Search Fields
	protected Textbox code; // autowired
	protected Textbox description; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Active;

	private transient QueryCategoryService queryCategoryService;

	/**
	 * default constructor.<br>
	 */
	public QueryCategoryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "QueryCategory";
		super.pageRightName = "QueryCategoryList";
		super.tableName = "BMTQueryCategories_AView";
		super.queueTableName = "BMTQueryCategories_View";
		super.enquiryTableName = "BMTQueryCategories_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_QueryCategoryList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_QueryCategoryList, borderLayout_QueryCategoryList, listBoxQueryCategory,
				pagingQueryCategoryList);
		setItemRender(new QueryCategoryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_QueryCategoryList_QueryCategorySearch);
		registerButton(button_QueryCategoryList_NewQueryCategory, "button_QueryCategoryList_NewQueryCategory", true);

		registerField("id");
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
	public void onClick$button_QueryCategoryList_QueryCategorySearch(Event event) {
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
	public void onClick$button_QueryCategoryList_NewQueryCategory(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		QueryCategory querycategory = new QueryCategory();
		querycategory.setNewRecord(true);
		querycategory.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(querycategory);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onQueryCategoryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxQueryCategory.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		QueryCategory querycategory = queryCategoryService.getQueryCategory(id);

		if (querycategory == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =?");

		if (doCheckAuthority(querycategory, whereCond.toString(), new Object[] { querycategory.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && querycategory.getWorkflowId() == 0) {
				querycategory.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(querycategory);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param querycategory The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(QueryCategory querycategory) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("queryCategory", querycategory);
		arg.put("queryCategoryListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryCategory/QueryCategoryDialog.zul", null, arg);
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

	public void setQueryCategoryService(QueryCategoryService queryCategoryService) {
		this.queryCategoryService = queryCategoryService;
	}
}