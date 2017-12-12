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
 * FileName    		:  CustomerRatingListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customerrating;

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

import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.service.customermasters.CustomerRatingService;
import com.pennant.webui.customermasters.customerrating.model.CustomerRatingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingList.zul file.
 */
public class CustomerRatingListCtrl extends GFCBaseListCtrl<CustomerRating> {
	private static final long serialVersionUID = -6628823752111176539L;
	private static final Logger logger = Logger.getLogger(CustomerRatingListCtrl.class);

	protected Window window_CustomerRatingList;
	protected Borderlayout borderLayout_CustomerRatingList;
	protected Paging pagingCustomerRatingList;
	protected Listbox listBoxCustomerRating;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustRatingType;
	protected Listheader listheader_CustRatingCode;
	protected Listheader listheader_CustRating;

	protected Textbox custCIF;
	protected Textbox custRatingType;
	protected Textbox custRatingCode;
	protected Textbox custRating;
	
	protected Listbox sortOperator_custRating;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custRatingType;
	protected Listbox sortOperator_custRatingCode;

	protected Button button_CustomerRatingList_NewCustomerRating;
	protected Button button_CustomerRatingList_CustomerRatingSearchDialog;

	private transient CustomerRatingService customerRatingService;

	/**
	 * default constructor.<br>
	 */
	public CustomerRatingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerRating";
		super.pageRightName = "CustomerRatingList";
		super.tableName = "CustomerRatings_AView";
		super.queueTableName = "CustomerRatings_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerRatingList(Event event) throws Exception {
		// Set the page level components.
		setPageComponents(window_CustomerRatingList, borderLayout_CustomerRatingList, listBoxCustomerRating,
				pagingCustomerRatingList);
		setItemRender(new CustomerRatingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerRatingList_NewCustomerRating, "button_CustomerRatingList_NewCustomerRating", true);
		registerButton(button_CustomerRatingList_CustomerRatingSearchDialog);

		registerField("custID");
		registerField("lovDescCustCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("custRatingType", listheader_CustRatingType, SortOrder.NONE, custRatingType,
				sortOperator_custRatingType, Operators.STRING);
		registerField("custRatingCode", listheader_CustRatingCode, SortOrder.NONE, custRatingCode,
				sortOperator_custRatingCode, Operators.STRING);
		registerField("custRating", listheader_CustRating, SortOrder.NONE, custRating, sortOperator_custRating,
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
	public void onClick$button_CustomerRatingList_CustomerRatingSearchDialog(Event event) throws Exception {
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
	public void onClick$button_CustomerRatingList_NewCustomerRating(Event event) throws Exception {
		logger.debug("Entering");
		// create a new CustomerRating object, We GET it from the backEnd.
		final CustomerRating customerRating = new CustomerRating();
		customerRating.setNewRecord(true);
		customerRating.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerRating);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerRatingItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerRating object
		final Listitem item = this.listBoxCustomerRating.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long id = (long) item.getAttribute("id");
			String custRatingType = (String) item.getAttribute("custRatingType");
			final CustomerRating customerRating = customerRatingService.getCustomerRatingById(id, custRatingType);

			if (customerRating == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND custId='" + customerRating.getCustID() + "' AND version="
					+ customerRating.getVersion() + " ";

			if (doCheckAuthority(customerRating, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerRating.getWorkflowId() == 0) {
					customerRating.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerRating);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerRating
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerRating customerRating) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerRating", customerRating);
		arg.put("customerRatingListCtrl", this);
		arg.put("newRecord", customerRating.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingDialog.zul", null,
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

	public void setCustomerRatingService(CustomerRatingService customerRatingService) {
		this.customerRatingService = customerRatingService;
	}
}