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
 *																							*
 * FileName    		:  QueryListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-07-2013    														*
 *                                                                  						*
 * Modified Date    :  04-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-07-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.query;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.service.applicationmaster.QueryService;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.applicationmaster.query.model.QueryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Query/QueryList.zul file.
 */
public class QueryListCtrl extends GFCBaseListCtrl<Query> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(QueryListCtrl.class);

	protected Window window_QueryList;
	protected Borderlayout borderLayout_QueryList;
	protected Paging pagingQueryList;
	protected Listbox listBoxQuery;

	protected Listheader listheader_QueryCode;
	protected Listheader listheader_QueryModule;
	protected Listheader listheader_QueryDesc;
	protected Listheader listheader_SubQuery;
	protected Listheader listheader_Active;

	protected Button button_QueryList_NewQuery;
	protected Button button_QueryList_QuerySearch;

	protected Textbox queryCode;
	protected Combobox queryModule;
	protected Textbox queryDesc;
	protected Checkbox subQuery;
	protected Checkbox active;

	protected Listbox sortOperator_QueryCode;
	protected Listbox sortOperator_QueryModule;
	protected Listbox sortOperator_QueryDesc;
	protected Listbox sortOperator_SubQuery;
	protected Listbox sortOperator_Active;

	protected Comboitem comboitem;
	protected Space space;

	private transient QueryService queryService;

	/**
	 * default constructor.<br>
	 */
	public QueryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Query";
		super.pageRightName = "QueryList";
		super.tableName = "Queries_AView";
		super.queueTableName = "Queries_View";
		super.enquiryTableName = "Queries_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_QueryList(Event event) {
		// Set the page level components.
		setPageComponents(window_QueryList, borderLayout_QueryList, listBoxQuery, pagingQueryList);
		setItemRender(new QueryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_QueryList_NewQuery, "QueryList", true);
		registerButton(button_QueryList_QuerySearch);

		fillComboBox(this.queryModule, "", PennantAppUtil.getQueryModuleByValueLabel(), "");

		registerField("queryCode", listheader_QueryCode, SortOrder.ASC, queryCode, sortOperator_QueryCode,
				Operators.STRING);
		registerField("queryModule", listheader_QueryModule, SortOrder.NONE, queryModule, sortOperator_QueryModule,
				Operators.NUMERIC);
		registerField("queryDesc", listheader_QueryDesc, SortOrder.NONE, queryDesc, sortOperator_QueryDesc,
				Operators.STRING);
		registerField("subQuery", listheader_SubQuery, SortOrder.NONE, subQuery, sortOperator_SubQuery,
				Operators.BOOLEAN);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

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
	public void onClick$button_QueryList_QuerySearch(Event event) {
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
	public void onClick$button_QueryList_NewQuery(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Query query = new Query();
		query.setNewRecord(true);
		query.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(query);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onQueryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxQuery.getSelectedItem();

		// Get the selected entity.
		String queryCode = (String) selectedItem.getAttribute("queryCode");
		String queryModule = (String) selectedItem.getAttribute("queryModule");

		Query query = queryService.getQueryById(queryCode, queryModule);

		if (query == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND QueryCode='" + query.getQueryCode() + "'";

		if (doCheckAuthority(query, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && query.getWorkflowId() == 0) {
				query.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(query);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param query
	 *            ,isSubQuery The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Query query) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("query", query);
		arg.put("queryListCtrl", this);
		arg.put("enqModule", enqiryModule);
		arg.put("subquery", query.isSubQuery());

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Query/QueryDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
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

	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}
}