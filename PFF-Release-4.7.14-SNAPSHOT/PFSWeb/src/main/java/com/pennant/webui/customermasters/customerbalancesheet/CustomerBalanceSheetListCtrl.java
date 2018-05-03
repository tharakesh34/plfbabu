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
 * FileName    		:  CustomerBalanceSheetListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-12-2011    														*
 *                                                                  						*
 * Modified Date    :  07-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.customerbalancesheet;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.service.customermasters.CustomerBalanceSheetService;
import com.pennant.webui.customermasters.customerbalancesheet.model.CustomerBalanceSheetComparator;
import com.pennant.webui.customermasters.customerbalancesheet.model.CustomerBalanceSheetListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerBalanceSheet
 * /CustomerBalanceSheetList.zul file.
 */
public class CustomerBalanceSheetListCtrl extends GFCBaseListCtrl<CustomerBalanceSheet> {
	private static final long serialVersionUID = 7572807238518910341L;
	private static final Logger logger = Logger.getLogger(CustomerBalanceSheetListCtrl.class);

	protected Window window_CustomerBalanceSheetList;
	protected Borderlayout borderLayout_CustomerBalanceSheetList;
	protected Paging pagingCustomerBalanceSheetList;
	protected Listbox listBoxCustomerBalanceSheet;

	protected Listbox sortOperator_financialYear;
	protected Listbox sortOperator_totalAssets;
	protected Listbox sortOperator_totalLiabilities;
	protected Listbox sortOperator_netProfit;

	protected Textbox financialYear;
	protected Decimalbox totalAssets;
	protected Decimalbox totalLiabilities;
	protected Decimalbox netProfit;

	protected Listheader listheader_FinancialYear;
	protected Listheader listheader_TotalAssets;
	protected Listheader listheader_TotalLiabilities;
	protected Listheader listheader_NetProfit;

	protected Button button_CustomerBalanceSheetList_NewCustomerBalanceSheet;
	protected Button button_CustomerBalanceSheetList_CustomerBalanceSheetSearchDialog;

	private transient CustomerBalanceSheetService customerBalanceSheetService;

	/**
	 * default constructor.<br>
	 */
	public CustomerBalanceSheetListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerBalanceSheet";
		super.pageRightName = "CustomerBalanceSheetList";
		super.tableName = "CustomerBalanceSheet_AView";
		super.queueTableName = "CustomerBalanceSheet_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerBalanceSheetList(Event event) {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_CustomerBalanceSheetList, borderLayout_CustomerBalanceSheetList,
				listBoxCustomerBalanceSheet, null);
		setItemRender(new CustomerBalanceSheetListModelItemRenderer());
		setComparator(new CustomerBalanceSheetComparator());

		// Register buttons and fields.
		registerButton(button_CustomerBalanceSheetList_NewCustomerBalanceSheet,
				"button_CustomerBalanceSheetList_NewCustomerBalanceSheet", true);
		registerButton(button_CustomerBalanceSheetList_CustomerBalanceSheetSearchDialog);

		registerField("custId", SortOrder.ASC);
		registerField("financialYear", listheader_FinancialYear, SortOrder.ASC, financialYear,
				sortOperator_financialYear, Operators.STRING);
		registerField("totalAssets", listheader_TotalAssets, SortOrder.NONE, totalAssets, sortOperator_totalAssets,
				Operators.NUMERIC);
		registerField("totalLiabilities", listheader_TotalLiabilities, SortOrder.NONE, totalLiabilities,
				sortOperator_totalLiabilities, Operators.NUMERIC);
		registerField("netProfit", listheader_NetProfit, SortOrder.NONE, netProfit, sortOperator_netProfit,
				Operators.NUMERIC);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CustomerBalanceSheetList_CustomerBalanceSheetSearchDialog(Event event) {
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
	public void onClick$button_CustomerBalanceSheetList_NewCustomerBalanceSheet(Event event) {
		logger.debug("Entering");
		// create a new CustomerBalanceSheet object, We GET it from the backEnd.
		final CustomerBalanceSheet customerBalanceSheet = new CustomerBalanceSheet();
		customerBalanceSheet.setNewRecord(true);
		customerBalanceSheet.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerBalanceSheet);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerBalanceSheetItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// get the selected CustomerBalanceSheet object
		final Listitem item = this.listBoxCustomerBalanceSheet.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			String id = (String) item.getAttribute("id");
			long custId = (long) item.getAttribute("custId");
			final CustomerBalanceSheet customerBalanceSheet = customerBalanceSheetService.getCustomerBalanceSheetById(
					id, custId);

			if (customerBalanceSheet == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND custid='" + customerBalanceSheet.getCustId() + "' AND version="
					+ customerBalanceSheet.getVersion() + " ";

			if (doCheckAuthority(customerBalanceSheet, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerBalanceSheet.getWorkflowId() == 0) {
					customerBalanceSheet.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerBalanceSheet);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerBalanceSheet
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerBalanceSheet customerBalanceSheet) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerBalanceSheet", customerBalanceSheet);
		arg.put("customerBalanceSheetListCtrl", this);
		arg.put("newRecord", customerBalanceSheet.isNew());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerBalanceSheet/CustomerBalanceSheetDialog.zul", null, arg);
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

	public void setCustomerBalanceSheetService(CustomerBalanceSheetService customerBalanceSheetService) {
		this.customerBalanceSheetService = customerBalanceSheetService;
	}
}