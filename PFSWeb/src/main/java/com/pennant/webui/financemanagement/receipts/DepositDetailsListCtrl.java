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
 * * FileName : DepositDetailsListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-03-2018 * *
 * Modified Date : 06-03-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-03-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.receipts;

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
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.finance.DepositDetailsService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.financemanagement.receipts.model.DepositDetailsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/Receipts/DepositDetailsList.zul file.
 * 
 */
public class DepositDetailsListCtrl extends GFCBaseListCtrl<DepositDetails> {
	private static final long serialVersionUID = 1L;

	protected Window window_DepositDetailsList;
	protected Borderlayout borderLayout_DepositDetailsList;
	protected Paging pagingDepositDetailsList;
	protected Listbox listBoxDepositDetails;

	// List headers
	protected Listheader listheader_DepositType;
	protected Listheader listheader_BranchCode;
	protected Listheader listheader_AvailableAmount;

	// checkRights
	protected Button button_DepositDetailsList_NewDepositDetails;
	protected Button button_DepositDetailsList_DepositDetailsSearch;

	// Search Fields
	protected Combobox depositType;
	protected ExtendedCombobox branchCode;

	protected Listbox sortOperator_DepositType;
	protected Listbox sortOperator_BranchCode;

	private transient DepositDetailsService depositDetailsService;
	private transient BranchService branchService;

	/**
	 * default constructor.<br>
	 */
	public DepositDetailsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DepositDetails";
		super.pageRightName = "DepositDetailsList";
		super.tableName = "DepositDetails_AView";
		super.queueTableName = "DepositDetails_View";
		super.enquiryTableName = "DepositDetails_View";

		this.branchCode.setModuleName("Branch");
		this.branchCode.setValueColumn("BranchCode");
		this.branchCode.setDescColumn("BranchDesc");
		this.branchCode.setValidateColumns(new String[] { "BranchCode" });
		readOnlyComponent(true, this.branchCode);
	}

	@Override
	protected void doAddFilters() {

		super.doAddFilters();

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("BranchCode", getUserWorkspace().getLoggedInUser().getBranchCode(), Filter.OP_EQUAL);
		this.branchCode.setFilters(filters);
		this.branchCode.setVisible(false);
		// this.branchCode.setValue(getUserWorkspace().getLoggedInUser().getBranchCode());
		this.searchObject.addFilters(filters);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DepositDetailsList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_DepositDetailsList, borderLayout_DepositDetailsList, listBoxDepositDetails,
				pagingDepositDetailsList);
		setItemRender(new DepositDetailsListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DepositDetailsList_DepositDetailsSearch);
		registerButton(button_DepositDetailsList_NewDepositDetails, "button_DepositDetailsList_NewDepositDetails",
				true);

		registerField("depositType", listheader_DepositType, SortOrder.NONE, depositType, sortOperator_DepositType,
				Operators.STRING);
		registerField("BranchCode", listheader_BranchCode, SortOrder.NONE, branchCode, sortOperator_BranchCode,
				Operators.STRING);
		registerField("ActualAmount", listheader_AvailableAmount, SortOrder.NONE);
		registerField("branchDesc");
		registerField("DepositId");
		registerField("ReservedAmount");

		fillComboBox(depositType, "", PennantStaticListUtil.getDepositTypesListList(), "");

		// Render the page and display the data.
		doRenderPage();

		search();

		button_DepositDetailsList_NewDepositDetails.setVisible(false);
		print.setVisible(false);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_DepositDetailsList_DepositDetailsSearch(Event event) {
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
	public void onClick$button_DepositDetailsList_NewDepositDetails(Event event) {
		logger.debug(Literal.ENTERING);

		String branchCode = getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode();

		Branch branch = this.branchService.getApprovedBranchById(branchCode);

		if (branch == null) {
			MessageUtil.showError("Requested branch is not available.");
			return;
		} else {
			// Create a new entity.
			DepositDetails depositDetails = new DepositDetails();
			depositDetails.setNewRecord(true);
			depositDetails.setWorkflowId(getWorkFlowId());
			depositDetails.setBranchCode(branch.getBranchCode());
			depositDetails.setBranchDesc(branch.getBranchDesc());

			// Display the dialog page.
			doShowDialogPage(depositDetails);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onDepositDetailsItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDepositDetails.getSelectedItem();
		final long depositId = (long) selectedItem.getAttribute("depositId");
		DepositDetails depositDetails = depositDetailsService.getDepositDetailsById(depositId);

		if (depositDetails == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		} else {
			StringBuilder whereCond = new StringBuilder();
			whereCond.append("  where  DepositId =? ");

			if (doCheckAuthority(depositDetails, whereCond.toString(),
					new Object[] { depositDetails.getDepositId() })) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && depositDetails.getWorkflowId() == 0) {
					depositDetails.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(depositDetails);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param depositDetails The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DepositDetails depositDetails) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("depositDetails", depositDetails);
		arg.put("depositDetailsListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/DepositDetailsDialog.zul", null,
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

	public BranchService getBranchService() {
		return branchService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public DepositDetailsService getDepositDetailsService() {
		return depositDetailsService;
	}

	public void setDepositDetailsService(DepositDetailsService depositDetailsService) {
		this.depositDetailsService = depositDetailsService;
	}
}