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
 * FileName    		:  CustomerIncomeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.customerincome;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.webui.customermasters.customerincome.model.CustomerIncomeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeList.zul file.
 */
public class CustomerIncomeListCtrl extends GFCBaseListCtrl<CustomerIncome> {
	private static final long serialVersionUID = -5018975982654527543L;
	private static final Logger logger = Logger.getLogger(CustomerIncomeListCtrl.class);

	protected Window window_CustomerIncomeList;
	protected Borderlayout borderLayout_CustomerIncomeList;
	protected Paging pagingCustomerIncomeList;
	protected Listbox listBoxCustomerIncome;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustIncomeType;
	protected Listheader listheader_JointCust;
	protected Listheader listheader_CustIncome;
	protected Listheader listheader_CustIncomeCountry;

	protected Textbox custCIF;
	protected Textbox custIncomeType;
	protected Checkbox jointCust;
	protected Decimalbox custIncome;
	protected Textbox custIncomeCountry;
	
	protected Listbox sortOperator_custIncomeCountry;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custIncomeType;
	protected Listbox sortOperator_jointCust;
	protected Listbox sortOperator_custIncome;

	protected Button button_CustomerIncomeList_NewCustomerIncome;
	protected Button button_CustomerIncomeList_CustomerIncomeSearchDialog;

	private transient CustomerIncomeService customerIncomeService;

	/**
	 * default constructor.<br>
	 */
	public CustomerIncomeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerIncome";
		super.pageRightName = "CustomerIncomeList";
		super.tableName = "CustomerIncomes_AView";
		super.queueTableName = "CustomerIncomes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerIncomeList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerIncomeList, borderLayout_CustomerIncomeList, listBoxCustomerIncome,
				pagingCustomerIncomeList);
		setItemRender(new CustomerIncomeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerIncomeList_NewCustomerIncome, "button_CustomerIncomeList_NewCustomerIncome", true);
		registerButton(button_CustomerIncomeList_NewCustomerIncome);

		registerField("custid");
		registerField("lovDescCustCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("custIncomeType", listheader_CustIncomeType, SortOrder.NONE, custIncomeType,
				sortOperator_custIncomeType, Operators.STRING);
		registerField("lovDescCustIncomeTypeName");
		registerField("jointCust", listheader_JointCust, SortOrder.NONE, jointCust, sortOperator_jointCust,
				Operators.BOOLEAN);
		registerField("custIncome", listheader_CustIncome, SortOrder.NONE, custIncome, sortOperator_custIncome,
				Operators.SIMPLE_NUMARIC);
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
	public void onClick$button_CustomerIncomeList_CustomerIncomeSearchDialog(Event event) {
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
	public void onClick$button_CustomerIncomeList_NewCustomerIncome(Event event) {
		logger.debug("Entering");
		// create a new CustomerIncome object, We GET it from the backEnd.
		final CustomerIncome customerIncome = new CustomerIncome();
		customerIncome.setNewRecord(true);
		customerIncome.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerIncome);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerIncomeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// get the selected CustomerIncome object
		final Listitem item = this.listBoxCustomerIncome.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerIncome aCustomerIncome = (CustomerIncome) item.getAttribute("data");
			final CustomerIncome customerIncome = customerIncomeService.getCustomerIncomeById(aCustomerIncome);

			if (customerIncome == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND AcademicID='" + customerIncome.getCustId() + "' AND version="
					+ customerIncome.getVersion() + " ";

			if (doCheckAuthority(customerIncome, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerIncome.getWorkflowId() == 0) {
					customerIncome.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerIncome);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerIncome
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerIncome customerIncome) {

		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerIncome", customerIncome);
		arg.put("customerIncomeListCtrl", this);
		arg.put("newRecord", customerIncome.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}
}