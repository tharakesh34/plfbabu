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
 * * FileName : HoldDisbursementListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-10-2018 * *
 * Modified Date : 09-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-10-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.holddisbursement;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennant.backend.service.finance.HoldDisbursementService;
import com.pennant.webui.finance.holddisbursement.model.HoldDisbursementListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/HoldDisbursement/HoldDisbursementList.zul file.
 * 
 */
public class HoldDisbursementListCtrl extends GFCBaseListCtrl<HoldDisbursement> {
	private static final long serialVersionUID = 1L;

	protected Window window_HoldDisbursementList;
	protected Borderlayout borderLayout_HoldDisbursementList;
	protected Paging pagingHoldDisbursementList;
	protected Listbox listBoxHoldDisbursement;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_Hold;
	protected Listheader listheader_TotalLoanAmt;
	protected Listheader listheader_DisbursedAmount;
	protected Listheader listheader_HoldLimitAmount;

	// checkRights
	protected Button button_HoldDisbursementList_NewHoldDisbursement;
	protected Button button_HoldDisbursementList_HoldDisbursementSearch;

	// Search Fields
	protected Textbox finReference; // autowired
	protected Checkbox hold; // autowired
	protected Decimalbox totalLoanAmt; // autowired
	protected Decimalbox disbursedAmount; // autowired
	protected Decimalbox holdLimitAmount; // autowired

	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_Hold;
	protected Listbox sortOperator_TotalLoanAmt;
	protected Listbox sortOperator_DisbursedAmount;
	protected Listbox sortOperator_HoldLimitAmount;

	private transient HoldDisbursementService holdDisbursementService;

	/**
	 * default constructor.<br>
	 */
	public HoldDisbursementListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "HoldDisbursement";
		super.pageRightName = "HoldDisbursementList";
		super.tableName = "HoldDisbursement_AView";
		super.queueTableName = "HoldDisbursement_View";
		super.enquiryTableName = "HoldDisbursement_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_HoldDisbursementList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_HoldDisbursementList, borderLayout_HoldDisbursementList, listBoxHoldDisbursement,
				pagingHoldDisbursementList);
		setItemRender(new HoldDisbursementListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_HoldDisbursementList_HoldDisbursementSearch);
		registerButton(button_HoldDisbursementList_NewHoldDisbursement,
				"button_HoldDisbursementList_NewHoldDisbursement", true);

		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("hold", listheader_Hold, SortOrder.NONE, hold, sortOperator_Hold, Operators.BOOLEAN);
		registerField("totalLoanAmt", listheader_TotalLoanAmt, SortOrder.NONE, totalLoanAmt, sortOperator_TotalLoanAmt,
				Operators.NUMERIC);
		registerField("disbursedAmount", listheader_DisbursedAmount, SortOrder.NONE, disbursedAmount,
				sortOperator_DisbursedAmount, Operators.NUMERIC);
		registerField("holdLimitAmount", listheader_HoldLimitAmount, SortOrder.NONE, holdLimitAmount,
				sortOperator_HoldLimitAmount, Operators.NUMERIC);
		registerField("remarks");

		// Render the page and display the data.
		doRenderPage();

		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_HoldDisbursementList_HoldDisbursementSearch(Event event) {
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
	public void onClick$button_HoldDisbursementList_NewHoldDisbursement(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		HoldDisbursement holdDisbursement = new HoldDisbursement();
		holdDisbursement.setNewRecord(true);
		holdDisbursement.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(holdDisbursement);
		// call the ZUL-file with the parameters packed in a map

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onHoldDisbursementItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxHoldDisbursement.getSelectedItem();
		long finID = (Long) selectedItem.getAttribute("finID");

		HoldDisbursement holddisbursement = holdDisbursementService.getHoldDisbursement(finID);

		if (holddisbursement == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		/*
		 * boolean financeDisbursementById = holdDisbursementService
		 * .getFinanceDisbursementById(holddisbursement.getFinReference());
		 * 
		 * if (financeDisbursementById) { MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("HD99019",
		 * null))); logger.debug("Leaving"); return; }
		 */

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  FinReference =?");

		if (doCheckAuthority(holddisbursement, whereCond.toString(),
				new Object[] { holddisbursement.getFinReference() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && holddisbursement.getWorkflowId() == 0) {
				holddisbursement.setWorkflowId(getWorkFlowId());
			}

			logUserAccess("menu_Item_HoldDisbursement", holddisbursement.getFinReference());

			doShowDialogPage(holddisbursement);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param holddisbursement The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(HoldDisbursement holddisbursement) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("holdDisbursement", holddisbursement);
		arg.put("holdDisbursementListCtrl", this);
		arg.put("moduleDefiner", "holdDisbursement");
		try {

			if (holddisbursement.isNewRecord()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/ManualAdvise/SelectManualAdviseFinReferenceDialog.zul", null,
						arg);
			} else {
				// arg.put("financeMain", financeMain);
				Executions.createComponents("/WEB-INF/pages/Finance/HoldDisbursement/HoldDisbursementDialog.zul", null,
						arg);
			}
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

	public void setHoldDisbursementService(HoldDisbursementService holdDisbursementService) {
		this.holdDisbursementService = holdDisbursementService;
	}
}