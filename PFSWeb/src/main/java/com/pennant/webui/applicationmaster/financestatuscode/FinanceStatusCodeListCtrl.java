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
 * * FileName : FinanceStatusCodeListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-04-2017 * *
 * Modified Date : 18-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.financestatuscode;

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

import com.pennant.backend.model.applicationmaster.FinanceStatusCode;
import com.pennant.backend.service.applicationmaster.FinanceStatusCodeService;
import com.pennant.webui.applicationmaster.financestatuscode.model.FinanceStatusCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.applicationmaster/FinanceStatusCode/FinanceStatusCodeList.zul file.
 * 
 */
public class FinanceStatusCodeListCtrl extends GFCBaseListCtrl<FinanceStatusCode> {
	private static final long serialVersionUID = 1L;

	protected Window window_FinanceStatusCodeList;
	protected Borderlayout borderLayout_FinanceStatusCodeList;
	protected Paging pagingFinanceStatusCodeList;
	protected Listbox listBoxFinanceStatusCode;

	protected Listheader listheader_StatusCode;
	protected Listheader listheader_StatusDesc;

	protected Textbox statusCode;
	protected Textbox statusDesc;

	protected Listbox sortOperator_statusCode;
	protected Listbox sortOperator_statusDesc;

	// checkRights
	protected Button button_FinanceStatusCodeList_NewFinanceStatusCode;
	protected Button button_FinanceStatusCodeList_FinanceStatusCodeSearch;

	// Search Fields

	private transient FinanceStatusCodeService financeStatusCodeService;

	/**
	 * default constructor.<br>
	 */
	public FinanceStatusCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceStatusCode";
		super.pageRightName = "FinanceStatusCodeList";
		super.tableName = "FINANCESTATUSCODES_AView";
		super.queueTableName = "FINANCESTATUSCODES_View";
		super.enquiryTableName = "FINANCESTATUSCODES_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinanceStatusCodeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FinanceStatusCodeList, borderLayout_FinanceStatusCodeList, listBoxFinanceStatusCode,
				pagingFinanceStatusCodeList);
		setItemRender(new FinanceStatusCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinanceStatusCodeList_FinanceStatusCodeSearch);
		registerButton(button_FinanceStatusCodeList_NewFinanceStatusCode,
				"button_FinanceStatusCodeList_NewFinanceStatusCode", true);

		registerField("statusId");
		registerField("statusCode", listheader_StatusCode, SortOrder.ASC, statusCode, sortOperator_statusCode,
				Operators.STRING);
		registerField("statusDesc", listheader_StatusDesc, SortOrder.NONE, statusDesc, sortOperator_statusDesc,
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
	public void onClick$button_FinanceStatusCodeList_FinanceStatusCodeSearch(Event event) {
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
	public void onClick$button_FinanceStatusCodeList_NewFinanceStatusCode(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		FinanceStatusCode financestatuscode = new FinanceStatusCode();
		financestatuscode.setNewRecord(true);
		financestatuscode.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(financestatuscode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onFinanceStatusCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinanceStatusCode.getSelectedItem();
		final long statusId = (long) selectedItem.getAttribute("statusId");
		FinanceStatusCode financestatuscode = financeStatusCodeService.getFinanceStatusCode(statusId);

		if (financestatuscode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  StatusID =? ");

		if (doCheckAuthority(financestatuscode, whereCond.toString(),
				new Object[] { financestatuscode.getStatusId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && financestatuscode.getWorkflowId() == 0) {
				financestatuscode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(financestatuscode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param financestatuscode The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinanceStatusCode financestatuscode) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeStatusCode", financestatuscode);
		arg.put("financeStatusCodeListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/FinanceStatusCodes/FinanceStatusCodesDialog.zul", null, arg);
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

	public void setFinanceStatusCodeService(FinanceStatusCodeService financestatuscodeService) {
		this.financeStatusCodeService = financestatuscodeService;
	}
}