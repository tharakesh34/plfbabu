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
 * * FileName : DepositMovementsListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-07-2018 * *
 * Modified Date : 17-07-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-07-2018 PENNANT 0.1 * * * * * * * * *
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.service.finance.DepositDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.financemanagement.receipts.model.DepositMovementsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/Receipts/DepositMovementsList.zul file.
 * 
 */
public class DepositMovementsListCtrl extends GFCBaseListCtrl<DepositMovements> {
	private static final long serialVersionUID = 1L;

	protected Window window_DepositMovementsList;
	protected Borderlayout borderLayout_DepositMovementsList;
	protected Paging pagingDepositMovementsList;
	protected Listbox listBoxDepositMovements;

	// List headers
	protected Listheader listheader_DepositType;
	protected Listheader listheader_DepositSlipNumber;
	protected Listheader listheader_TransactionDate;
	protected Listheader listheader_BranchCode;
	protected Listheader listheader_PartnerBankId;
	protected Listheader listheader_DepositAmount;

	// checkRights
	protected Button button_DepositMovementsList_NewDepositMovements;
	protected Button button_DepositMovementsList_DepositMovementsSearch;

	// Search Fields
	protected Textbox depositSlipNumber;
	protected Datebox transactionDate;
	protected Combobox depositType;

	protected Listbox sortOperator_DepositSlipNumber;
	protected Listbox sortOperator_TransactionDate;
	protected Listbox sortOperator_DepositType;

	private transient DepositDetailsService depositDetailsService;

	/**
	 * default constructor.<br>
	 */
	public DepositMovementsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DepositMovements";
		super.pageRightName = "DepositMovementsList";
		super.tableName = "DepositMovements_AView";
		super.queueTableName = "DepositMovements_AView";
		super.enquiryTableName = "DepositMovements_View";

		this.transactionDate.setFormat(PennantConstants.dateFormat);
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("ReceiptId", 0, Filter.OP_EQUAL);
		filters[1] = new Filter("BranchCode", getUserWorkspace().getLoggedInUser().getBranchCode(), Filter.OP_EQUAL);
		this.searchObject.addFilters(filters);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DepositMovementsList(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DepositMovementsList, borderLayout_DepositMovementsList, listBoxDepositMovements,
				pagingDepositMovementsList);
		setItemRender(new DepositMovementsListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DepositMovementsList_DepositMovementsSearch);
		registerButton(button_DepositMovementsList_NewDepositMovements,
				"button_DepositMovementsList_NewDepositMovements", true);

		registerField("DepositType", listheader_DepositType, SortOrder.NONE, depositType, sortOperator_DepositType,
				Operators.STRING);
		registerField("depositSlipNumber", listheader_DepositSlipNumber, SortOrder.NONE, depositSlipNumber,
				sortOperator_DepositSlipNumber, Operators.STRING);
		registerField("TransactionDate", listheader_TransactionDate, SortOrder.NONE, transactionDate,
				sortOperator_TransactionDate, Operators.DATE);
		registerField("ReservedAmount", listheader_DepositAmount, SortOrder.NONE);
		registerField("MovementId");
		registerField("BranchCode");
		registerField("BranchDesc");
		registerField("PartnerBankName");

		fillComboBox(depositType, "", PennantStaticListUtil.getDepositTypesListList(), "");

		// Render the page and display the data.
		doRenderPage();

		search();

		this.button_DepositMovementsList_NewDepositMovements.setVisible(false);
		print.setVisible(false);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_DepositMovementsList_DepositMovementsSearch(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onDepositMovementsItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDepositMovements.getSelectedItem();
		final long movementId = (long) selectedItem.getAttribute("movementId");
		DepositMovements depositMovements = depositDetailsService.getApprovedDepositMovementsById(movementId);

		if (depositMovements == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  MovementId =? ");

		if (doCheckAuthority(depositMovements, whereCond.toString(),
				new Object[] { depositMovements.getMovementId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && depositMovements.getWorkflowId() == 0) {
				depositMovements.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(depositMovements);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param depositMovements The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DepositMovements depositMovements) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("depositMovements", depositMovements);
		arg.put("depositMovementsListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/DepositMovementsDialog.zul", null,
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

	public DepositDetailsService getDepositDetailsService() {
		return depositDetailsService;
	}

	public void setDepositDetailsService(DepositDetailsService depositDetailsService) {
		this.depositDetailsService = depositDetailsService;
	}
}