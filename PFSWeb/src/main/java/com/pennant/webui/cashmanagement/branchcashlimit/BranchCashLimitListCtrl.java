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
 * * FileName : BranchCashLimitListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-01-2018 * *
 * Modified Date : 29-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.cashmanagement.branchcashlimit;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.cashmanagement.BranchCashLimit;
import com.pennant.backend.service.cashmanagement.BranchCashLimitService;
import com.pennant.backend.util.CashManagementConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.cashmanagement.branchcashlimit.model.BranchCashLimitListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CashManagement/BranchCashLimit/BranchCashLimitList.zul file.
 * 
 */
public class BranchCashLimitListCtrl extends GFCBaseListCtrl<BranchCashLimit> {
	private static final long serialVersionUID = 1L;

	protected Window window_BranchCashLimitList;
	protected Borderlayout borderLayout_BranchCashLimitList;
	protected Paging pagingBranchCashLimitList;
	protected Listbox listBoxBranchCashLimit;

	// List headers
	protected Listheader listheader_BranchCode;
	protected Listheader listheader_BranchName;
	protected Listheader listheader_ReOrderLimit;
	protected Listheader listheader_CashLimit;
	protected Listheader listheader_CashPosition;
	protected Listheader listheader_CashInTransit;
	protected Listheader listheader_AdHocCashLimit;
	protected Listheader listheader_AdHocCashInTransit;

	// checkRights
	protected Button button_BranchCashLimitList_NewBranchCashLimit;
	protected Button button_BranchCashLimitList_BranchCashLimitSearch;

	// Search Fields
	protected ExtendedCombobox branchCode;
	protected Combobox cashStatus;

	protected Listbox sortOperator_BranchCode;
	protected Listbox sortOperator_CashStatus;

	private transient BranchCashLimitService branchCashLimitService;

	/**
	 * default constructor.<br>
	 */
	public BranchCashLimitListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BranchCashLimit";
		super.pageRightName = "BranchCashLimitList";
		super.tableName = "BranchCashLimit_AView";
		super.queueTableName = "BranchCashLimit_View";
		super.enquiryTableName = "BranchCashLimit_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BranchCashLimitList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_BranchCashLimitList, borderLayout_BranchCashLimitList, listBoxBranchCashLimit,
				pagingBranchCashLimitList);
		setItemRender(new BranchCashLimitListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BranchCashLimitList_BranchCashLimitSearch);
		registerButton(button_BranchCashLimitList_NewBranchCashLimit, "button_BranchCashLimitList_NewBranchCashLimit",
				true);

		doSetFieldProperties();
		registerField("branchCode", listheader_BranchCode, SortOrder.NONE, branchCode, sortOperator_BranchCode,
				Operators.STRING);
		registerField("branchCodeName", listheader_BranchName);
		registerField("reOrderLimit", listheader_ReOrderLimit);
		registerField("cashLimit", listheader_CashLimit);
		registerField("branchCash", listheader_CashPosition, SortOrder.NONE, sortOperator_CashStatus,
				Operators.DEFAULT);
		registerField("autoTransitAmount", listheader_CashInTransit);
		registerField("adHocCashLimit", listheader_AdHocCashLimit);
		registerField("adhocTransitAmount", listheader_AdHocCashInTransit);
		registerField("remarks");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	@Override
	public void doAddFilters() {
		super.doAddFilters();

		if (CashManagementConstants.Cash_Position_Low.equals(getComboboxValue(cashStatus))) {
			searchObject.addWhereClause(" BranchCash < reOrderLimit ");
		} else if (CashManagementConstants.Cash_Position_Excess.equals(getComboboxValue(cashStatus))) {
			searchObject.addWhereClause(" BranchCash > cashLimit ");
		} else if (CashManagementConstants.Cash_Position_Sufficient.equals(getComboboxValue(cashStatus))) {
			searchObject.addWhereClause(" (BranchCash >= reOrderLimit and BranchCash <= cashLimit) ");
		} else {
			searchObject.addWhereClause(null);
		}
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.branchCode.setModuleName("Branch");
		this.branchCode.setValueColumn("BranchCode");
		this.branchCode.setDescColumn("BranchDesc");
		this.branchCode.setValidateColumns(new String[] { "BranchCode" });
		fillComboBox(cashStatus, null, PennantStaticListUtil.getCashPositionStatusList(), "");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_BranchCashLimitList_BranchCashLimitSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		cashStatus.setSelectedIndex(0);
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_BranchCashLimitList_NewBranchCashLimit(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		BranchCashLimit branchcashlimit = new BranchCashLimit();
		branchcashlimit.setNewRecord(true);
		branchcashlimit.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(branchcashlimit, null);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onBranchCashLimitItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBranchCashLimit.getSelectedItem();
		final String branchCode = (String) selectedItem.getAttribute("branchCode");
		BranchCashLimit branchcashlimit = branchCashLimitService.getBranchCashLimit(branchCode);
		BranchCashLimit apporvedData = branchCashLimitService.getApprovedBranchCashLimit(branchCode);

		if (branchcashlimit == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where BranchCode = ?");

		if (doCheckAuthority(branchcashlimit, whereCond.toString(), new Object[] { branchcashlimit.getBranchCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && branchcashlimit.getWorkflowId() == 0) {
				branchcashlimit.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(branchcashlimit, apporvedData);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param branchcashlimit The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BranchCashLimit branchcashlimit, BranchCashLimit apporvedData) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("branchCashLimit", branchcashlimit);
		arg.put("branchCashLimitListCtrl", this);
		arg.put("apporvedData", apporvedData);

		try {
			Executions.createComponents("/WEB-INF/pages/CashManagement/BranchCashLimit/BranchCashLimitDialog.zul", null,
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

	public void setBranchCashLimitService(BranchCashLimitService branchCashLimitService) {
		this.branchCashLimitService = branchCashLimitService;
	}
}