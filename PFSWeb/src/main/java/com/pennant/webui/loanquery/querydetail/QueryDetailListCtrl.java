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
 * * FileName : QueryDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-05-2018 * * Modified
 * Date : 09-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-05-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.loanquery.querydetail;

import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.loanquery.querydetail.model.QueryDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/LoanQuery/QueryDetail/QueryDetailList.zul file.
 * 
 */
public class QueryDetailListCtrl extends GFCBaseListCtrl<QueryDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_QueryDetailList;
	protected Borderlayout borderLayout_QueryDetailList;
	protected Paging pagingQueryDetailList;
	protected Listbox listBoxQueryDetail;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_Status;
	protected Listheader listheader_RaisedBy;
	protected Listheader listheader_RaisedOn;
	protected Listheader listheader_QryCtg;
	protected Listheader listheader_UsrLogin;
	// checkRights
	protected Button button_QueryDetailList_NewQueryDetail;
	protected Button button_QueryDetailList_QueryDetailSearch;

	// Search Fields
	protected Textbox id; // autowired
	protected Textbox finReference; // autowired
	protected Textbox categoryId; // autowired
	protected Combobox status; // autowired
	protected Textbox qryNotes; // autowired
	protected Textbox usrLogin; // autowired
	protected Longbox raisedBy; // autowired
	protected Datebox raisedOn; // autowired
	protected Textbox queryCtg; // autowired
	protected Textbox module; // autowired

	protected Listbox sortOperator_Id;
	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_Status;
	protected Listbox sortOperator_UsrLogin;
	protected Listbox sortOperator_RaisedBy;
	protected Listbox sortOperator_RaisedOn;
	protected Listbox sortOperator_QueryCtg;
	protected Listbox sortOperator_Module;

	private transient QueryDetailService queryDetailService;
	private final List<ValueLabel> queryModuleStatusList = PennantStaticListUtil.getQueryModuleStatusList();

	/**
	 * default constructor.<br>
	 */
	public QueryDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "QueryDetail";
		super.pageRightName = "QueryDetailList";
		super.tableName = "QUERYDETAIL_View";
		super.queueTableName = "QUERYDETAIL_View";
		super.enquiryTableName = "QUERYDETAIL_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_QueryDetailList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_QueryDetailList, borderLayout_QueryDetailList, listBoxQueryDetail,
				pagingQueryDetailList);
		setItemRender(new QueryDetailListModelItemRenderer("", 0));

		// Register buttons and fields.
		registerButton(button_QueryDetailList_QueryDetailSearch);
		// registerButton(button_QueryDetailList_NewQueryDetail, "button_QueryDetailList_NewQueryDetail", true);
		this.button_QueryDetailList_NewQueryDetail.setVisible(false);

		registerField("id");
		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("categoryId");
		registerField("qryNotes");
		registerField("raisedBy", listheader_RaisedBy, SortOrder.NONE, raisedBy, sortOperator_RaisedBy,
				Operators.SIMPLE_NUMARIC);
		registerField("status", listheader_Status, SortOrder.NONE, status, sortOperator_Status, Operators.STRING);
		registerField("raisedOn", listheader_RaisedOn, SortOrder.NONE, raisedOn, sortOperator_RaisedOn, Operators.DATE);
		registerField("categoryCode", listheader_QryCtg, SortOrder.NONE, queryCtg, sortOperator_QueryCtg,
				Operators.STRING);
		registerField("categoryDescription");
		registerField("usrLogin");
		registerField("Module", module, SortOrder.NONE, sortOperator_Module, Operators.BOOLEAN);
		fillComboBox(this.status, "", queryModuleStatusList, "");

		// Render the page and display the data.
		doRenderPage();

		search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_QueryDetailList_QueryDetailSearch(Event event) {
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
	public void onClick$button_QueryDetailList_NewQueryDetail(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		QueryDetail querydetail = new QueryDetail();
		querydetail.setNewRecord(true);
		querydetail.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowNewDialogPage(querydetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onQueryDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxQueryDetail.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		QueryDetail querydetail = queryDetailService.getQueryDetail(id);

		if (querydetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =?");

		if (doCheckAuthority(querydetail, whereCond.toString(), new Object[] { querydetail.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && querydetail.getWorkflowId() == 0) {
				querydetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(querydetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param querydetail The entity that need to be passed to the dialog.
	 */
	private void doShowNewDialogPage(QueryDetail querydetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("queryDetail", querydetail);
		arg.put("queryDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/QueryDetailNewDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(QueryDetail querydetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("queryDetail", querydetail);
		arg.put("queryDetailListCtrl", this);
		arg.put("legalModuleName", querydetail.getModule());

		try {
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/QueryDetailDialog.zul", null, arg);
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

	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}

}