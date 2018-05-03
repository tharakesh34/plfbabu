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
 * FileName    		:  CustomerStatusCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.customerstatuscode;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.service.applicationmaster.CustomerStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.applicationmaster.customerstatuscode.model.CustomerStatusCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CustomerStatusCode /CustomerStatusCodeList.zul
 * file.
 */
public class CustomerStatusCodeListCtrl extends GFCBaseListCtrl<CustomerStatusCode> {
	private static final long serialVersionUID = -3727071843922740401L;
	private static final Logger logger = Logger.getLogger(CustomerStatusCodeListCtrl.class);

	protected Window window_CustomerStatusCodeList;
	protected Borderlayout borderLayout_CustomerStatusCodeList;
	protected Paging pagingCustomerStatusCodeList;
	protected Listbox listBoxCustomerStatusCode;

	protected Listheader listheader_CustStsCode;
	protected Listheader listheader_CustStsDescription;
	protected Listheader listheader_CustStsIsActive;

	protected Textbox custStsCode;
	protected Textbox custStsDescription;
	protected Checkbox custStsIsActive;

	protected Listbox sortOperator_custStsCode;
	protected Listbox sortOperator_custStsDescription;
	protected Listbox sortOperator_custStsIsActive;

	protected Button button_CustomerStatusCodeList_NewCustomerStatusCode;
	protected Button button_CustomerStatusCodeList_CustomerStatusCodeSearchDialog;

	private transient CustomerStatusCodeService customerStatusCodeService;

	/**
	 * default constructor.<br>
	 */
	public CustomerStatusCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerStatusCode";
		super.pageRightName = "CustomerStatusCodeList";
		super.tableName = "BMTCustStatusCodes_AView";
		super.queueTableName = "BMTCustStatusCodes_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("CustStsCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerStatusCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerStatusCodeList, borderLayout_CustomerStatusCodeList,
				listBoxCustomerStatusCode, pagingCustomerStatusCodeList);
		setItemRender(new CustomerStatusCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerStatusCodeList_NewCustomerStatusCode,
				"button_CustomerStatusCodeList_NewCustomerStatusCode", true);
		registerButton(button_CustomerStatusCodeList_CustomerStatusCodeSearchDialog);

		registerField("custStsCode", listheader_CustStsCode, SortOrder.ASC, custStsCode, sortOperator_custStsCode,
				Operators.STRING);
		registerField("custStsDescription", listheader_CustStsDescription, SortOrder.NONE, custStsDescription,
				sortOperator_custStsDescription, Operators.STRING);
		registerField("custStsIsActive", listheader_CustStsIsActive, SortOrder.NONE, custStsIsActive,
				sortOperator_custStsIsActive, Operators.BOOLEAN);

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
	public void onClick$button_CustomerStatusCodeList_CustomerStatusCodeSearchDialog(Event event) {
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
	public void onClick$button_CustomerStatusCodeList_NewCustomerStatusCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CustomerStatusCode customerStatusCode = new CustomerStatusCode();
		customerStatusCode.setNewRecord(true);
		customerStatusCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerStatusCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerStatusCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustomerStatusCode.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CustomerStatusCode customerStatusCode = customerStatusCodeService.getCustomerStatusCodeById(id);

		if (customerStatusCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CustStsCode='" + customerStatusCode.getCustStsCode() + "' AND version="
				+ customerStatusCode.getVersion() + " ";

		if (doCheckAuthority(customerStatusCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && customerStatusCode.getWorkflowId() == 0) {
				customerStatusCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(customerStatusCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerStatusCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerStatusCode customerStatusCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerStatusCode", customerStatusCode);
		arg.put("customerStatusCodeListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CustomerStatusCode/CustomerStatusCodeDialog.zul", null, arg);
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

	public void setCustomerStatusCodeService(CustomerStatusCodeService customerStatusCodeService) {
		this.customerStatusCodeService = customerStatusCodeService;
	}
}