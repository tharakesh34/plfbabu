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
 * FileName    		:  CustomerAddresListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customeraddres;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.customermasters.customeraddres.model.CustomerAddresListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresList.zul file.
 */
public class CustomerAddresListCtrl extends GFCBaseListCtrl<CustomerAddres> {
	private static final long serialVersionUID = -3065680573751828336L;
	private static final Logger logger = Logger.getLogger(CustomerAddresListCtrl.class);

	protected Window window_CustomerAddresList;
	protected Borderlayout borderLayout_CustomerAddresList;
	protected Paging pagingCustomerAddresList;
	protected Listbox listBoxCustomerAddres;

	protected Textbox custCIF;
	protected Textbox custAddrType;
	protected Textbox custAddrHNbr;
	protected Textbox custFlatNbr;
	protected Textbox custAddrStreet;

	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custAddrType;
	protected Listbox sortOperator_custAddrHNbr;
	protected Listbox sortOperator_custFlatNbr;
	protected Listbox sortOperator_custAddrStreet;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustAddrType;
	protected Listheader listheader_CustAddrHNbr;
	protected Listheader listheader_CustFlatNbr;
	protected Listheader listheader_CustAddrStreet;

	protected Button button_CustomerAddresList_NewCustomerAddres;
	protected Button button_CustomerAddresList_CustomerAddresSearchDialog;

	private transient CustomerAddresService customerAddresService;

	/**
	 * default constructor.<br>
	 */
	public CustomerAddresListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerAddres";
		super.pageRightName = "CustomerAddresList";
		super.tableName = "CustomerAddresses_View";
		super.queueTableName = "CustomerAddresses_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilterNotEqual("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerAddresList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerAddresList, borderLayout_CustomerAddresList, listBoxCustomerAddres,
				pagingCustomerAddresList);
		setItemRender(new CustomerAddresListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerAddresList_NewCustomerAddres, "button_CustomerAddresList_NewCustomerAddres", true);
		registerButton(button_CustomerAddresList_CustomerAddresSearchDialog);

		registerField("custId");
		registerField("lovDescCustCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("custAddrType", listheader_CustAddrType, SortOrder.NONE, custAddrType, sortOperator_custAddrType,
				Operators.STRING);
		registerField("custAddrHNbr", listheader_CustAddrHNbr, SortOrder.NONE, custAddrHNbr, sortOperator_custAddrHNbr,
				Operators.STRING);
		registerField("custFlatNbr", listheader_CustFlatNbr, SortOrder.NONE, custFlatNbr, sortOperator_custFlatNbr,
				Operators.STRING);
		registerField("custAddrStreet", listheader_CustAddrStreet, SortOrder.NONE, custAddrStreet,
				sortOperator_custAddrStreet, Operators.STRING);
		registerField("lovDescCustAddrTypeName");

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
	public void onClick$button_CustomerAddresList_CustomerAddresSearchDialog(Event event) {
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
	public void onClick$button_CustomerAddresList_NewCustomerAddres(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CustomerAddres aCustomerAddres = new CustomerAddres();
		aCustomerAddres.setNewRecord(true);
		aCustomerAddres.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aCustomerAddres);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerAddresItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustomerAddres.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		String addType = (String) selectedItem.getAttribute("type");

		CustomerAddres customerAddres = customerAddresService.getCustomerAddresById(id, addType);

		if (customerAddres == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CustId='" + customerAddres.getCustID() + "' AND version="
				+ customerAddres.getVersion() + " ";

		if (doCheckAuthority(customerAddres, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && customerAddres.getWorkflowId() == 0) {
				customerAddres.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(customerAddres);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerAddres
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerAddres customerAddres) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerAddres", customerAddres);
		arg.put("customerAddresListCtrl", this);
		arg.put("newRecord", customerAddres.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul", null,
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

	public void setCustomerAddresService(CustomerAddresService customerAddresService) {
		this.customerAddresService = customerAddresService;
	}

}