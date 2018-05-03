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
 * FileName    		:  LegalExpensesListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2016    														*
 *                                                                  						*
 * Modified Date    :  19-04-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2016       Pennant	                 0.1                                            * 
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
package com.pennant.webui.expenses.legalexpenses;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.service.expenses.LegalExpensesService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.expenses.legalexpenses.model.LegalExpensesListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Expenses/LegalExpenses/LegalExpensesList.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class LegalExpensesListCtrl extends GFCBaseListCtrl<LegalExpenses> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LegalExpensesListCtrl.class);

	protected Window window_LegalExpensesList;
	protected Borderlayout borderLayout_LegalExpensesList;
	protected Paging pagingLegalExpensesList;
	protected Listbox listBoxLegalExpenses;

	protected Listheader listheader_CustomerId;
	protected Listheader listheader_BookingDate;
	protected Listheader listheader_Amount;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_Expreference;
	protected Listheader listheader_TransactionType;

	protected Button button_LegalExpensesList_NewLegalExpenses;
	protected Button button_LegalExpensesList_LegalExpensesSearch;

	private transient LegalExpensesService legalExpensesService;

	protected Longbox customerId;
	protected Textbox finReference;
	protected Combobox transactionType;
	protected Textbox expReference;

	protected Listbox sortOperator_CustomerId;
	protected Listbox sortOperator_BookingDate;
	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_TransactionType;
	protected Listbox sortOperator_ExpReference;

	/**
	 * default constructor.<br>
	 */
	public LegalExpensesListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LegalExpenses";
		super.pageRightName = "LegalExpensesList";
		super.tableName = "FinLegalExpenses_AView";
		super.queueTableName = "FinLegalExpenses_View";
		super.enquiryTableName = "FinLegalExpenses_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalExpensesList(Event event) {
		// Set the page level components.
		setPageComponents(window_LegalExpensesList, borderLayout_LegalExpensesList, listBoxLegalExpenses,
				pagingLegalExpensesList);
		setItemRender(new LegalExpensesListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LegalExpensesList_NewLegalExpenses, "button_LegalExpensesList_NewLegalExpenses", true);
		registerButton(button_LegalExpensesList_LegalExpensesSearch);

		registerField("customerId", listheader_CustomerId, SortOrder.ASC, customerId, sortOperator_CustomerId,
				Operators.STRING);
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		fillComboBox(this.transactionType,"",PennantStaticListUtil.getTransactionTypes(),"");
		registerField("transactionType", listheader_TransactionType, SortOrder.NONE, transactionType,
				sortOperator_TransactionType, Operators.STRING);
		registerField("expReference", listheader_Expreference, SortOrder.NONE, expReference, sortOperator_ExpReference,
				Operators.STRING);
		registerField("amount", listheader_Amount, SortOrder.NONE);

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
	public void onClick$button_LegalExpensesList_LegalExpensesSearch(Event event) {
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
	public void onClick$button_LegalExpensesList_NewLegalExpenses(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		LegalExpenses legalExpenses = new LegalExpenses();
		legalExpenses.setNewRecord(true);
		legalExpenses.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(legalExpenses);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onLegalExpensesItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLegalExpenses.getSelectedItem();

		// Get the selected entity.
		String expReference = (String) selectedItem.getAttribute("expReference");
		LegalExpenses legalExpenses = legalExpensesService.getLegalExpensesById(expReference);

		if (legalExpenses == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND Finreference = '" + legalExpenses.getFinReference() + "' AND version = "
				+ legalExpenses.getVersion();

		if (doCheckAuthority(legalExpenses, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && legalExpenses.getWorkflowId() == 0) {
				legalExpenses.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(legalExpenses);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aLegalExpenses
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LegalExpenses aLegalExpenses) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("legalExpenses", aLegalExpenses);
		arg.put("legalExpensesListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/Expenses/LegalExpenses/LegalExpensesDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
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

	public void setLegalExpensesService(LegalExpensesService legalExpensesService) {
		this.legalExpensesService = legalExpensesService;
	}
}