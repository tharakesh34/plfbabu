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
 * FileName    		:  CustomerPhoneNumberListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customerphonenumber;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.webui.customermasters.customerphonenumber.model.CustomerPhoneNumberListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerPhoneNumber /CustomerPhoneNumberList.zul
 * file.
 */
public class CustomerPhoneNumberListCtrl extends GFCBaseListCtrl<CustomerPhoneNumber> {
	private static final long serialVersionUID = 5073003999430539385L;
	private static final Logger logger = LogManager.getLogger(CustomerPhoneNumberListCtrl.class);

	protected Window window_CustomerPhoneNumberList;
	protected Borderlayout borderLayout_CustomerPhoneNumberList;
	protected Paging pagingCustomerPhoneNumberList;
	protected Listbox listBoxCustomerPhoneNumber;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_PhoneTypeCode;
	protected Listheader listheader_PhoneCountryCode;
	protected Listheader listheader_PhoneAreaCode;
	protected Listheader listheader_PhoneNumber;

	protected Textbox phoneCustCIF;
	protected Textbox phoneTypeCode;
	protected Textbox phoneCountryCode;
	protected Textbox phoneAreaCode;
	protected Textbox phoneNumber;

	protected Listbox sortOperator_phoneNumber;
	protected Listbox sortOperator_phoneCustCIF;
	protected Listbox sortOperator_phoneTypeCode;
	protected Listbox sortOperator_phoneCountryCode;
	protected Listbox sortOperator_phoneAreaCode;

	protected Button button_CustomerPhoneNumberList_NewCustomerPhoneNumber;
	protected Button button_CustomerPhoneNumberList_CustomerPhoneNumberSearchDialog;

	private transient CustomerPhoneNumberService customerPhoneNumberService;

	/**
	 * default constructor.<br>
	 */
	public CustomerPhoneNumberListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerPhoneNumber";
		super.pageRightName = "CustomerPhoneNumberList";
		super.tableName = "CustomerPhoneNumbers_AView";
		super.queueTableName = "CustomerPhoneNumbers_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerPhoneNumberList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerPhoneNumberList, borderLayout_CustomerPhoneNumberList,
				listBoxCustomerPhoneNumber, pagingCustomerPhoneNumberList);
		setItemRender(new CustomerPhoneNumberListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerPhoneNumberList_NewCustomerPhoneNumber,
				"button_CustomerPhoneNumberList_NewCustomerPhoneNumber", true);
		registerButton(button_CustomerPhoneNumberList_CustomerPhoneNumberSearchDialog);

		registerField("phonecustId", listheader_CustCIF, SortOrder.ASC, phoneCustCIF, sortOperator_phoneCustCIF,
				Operators.STRING);
		registerField("phoneTypeCode", listheader_PhoneTypeCode, SortOrder.NONE, phoneTypeCode,
				sortOperator_phoneTypeCode, Operators.STRING);
		registerField("lovDescPhoneTypeCodeName");
		registerField("phoneAreaCode", listheader_PhoneAreaCode, SortOrder.NONE, phoneAreaCode,
				sortOperator_phoneAreaCode, Operators.STRING);
		registerField("phoneNumber", listheader_PhoneNumber, SortOrder.NONE, phoneNumber, sortOperator_phoneNumber,
				Operators.STRING);

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
	public void onClick$button_CustomerPhoneNumberList_CustomerPhoneNumberSearchDialog(Event event) {
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
	public void onClick$button_CustomerPhoneNumberList_NewCustomerPhoneNumber(Event event) {
		logger.debug("Entering");
		// create a new CustomerPhoneNumber object, We GET it from the backEnd.
		final CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setNewRecord(true);
		customerPhoneNumber.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerPhoneNumber);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerPhoneNumberItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerPhoneNumber object
		final Listitem item = this.listBoxCustomerPhoneNumber.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long id = (long) item.getAttribute("id");
			String phoneTypeCode = (String) item.getAttribute("phoneTypeCode");

			CustomerPhoneNumber customerPhoneNumber = customerPhoneNumberService.getCustomerPhoneNumberById(id,
					phoneTypeCode);
			if (customerPhoneNumber == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " where phoneCustId= ?";

			if (doCheckAuthority(customerPhoneNumber, whereCond,
					new Object[] { customerPhoneNumber.getPhoneCustID() })) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerPhoneNumber.getWorkflowId() == 0) {
					customerPhoneNumber.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerPhoneNumber);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerPhoneNumber
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerPhoneNumber customerPhoneNumber) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerPhoneNumber", customerPhoneNumber);
		arg.put("customerPhoneNumberListCtrl", this);
		arg.put("newRecord", customerPhoneNumber.isNew());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/CustomerPhoneNumberDialog.zul", null, arg);
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

	public void setCustomerPhoneNumberService(CustomerPhoneNumberService customerPhoneNumberService) {
		this.customerPhoneNumberService = customerPhoneNumberService;
	}
}