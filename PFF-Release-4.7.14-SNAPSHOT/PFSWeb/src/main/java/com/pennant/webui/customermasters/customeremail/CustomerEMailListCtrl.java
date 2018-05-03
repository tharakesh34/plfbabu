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
 * FileName    		:  CustomerEMailListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customeremail;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.customermasters.customeremail.model.CustomerEMailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailList.zul file.
 */
public class CustomerEMailListCtrl extends GFCBaseListCtrl<CustomerEMail> {
	private static final long serialVersionUID = -5818545488371155444L;
	private static final Logger logger = Logger.getLogger(CustomerEMailListCtrl.class);

	protected Window window_CustomerEMailList;
	protected Borderlayout borderLayout_CustomerEMailList;
	protected Paging pagingCustomerEMailList;
	protected Listbox listBoxCustomerEMail;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustEMailTypeCode;
	protected Listheader listheader_CustEMailPriority;
	protected Listheader listheader_CustEMail;

	protected Textbox custCIF;
	protected Textbox custEMailTypeCode;
	protected Intbox custEMailPriority;
	protected Textbox custEMailid;

	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custEMailTypeCode;
	protected Listbox sortOperator_custEMailPriority;
	protected Listbox sortOperator_custEMailid;

	protected Label label_CustomerEMailSearch_RecordStatus;
	protected Label label_CustomerEMailSearch_RecordType;
	protected Label label_CustomerEMailSearchResult;

	protected Button button_CustomerEMailList_NewCustomerEMail;
	protected Button button_CustomerEMailList_CustomerEMailSearchDialog;

	private transient CustomerEMailService customerEMailService;

	/**
	 * default constructor.<br>
	 */
	public CustomerEMailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerEMail";
		super.pageRightName = "CustomerEMailList";
		super.tableName = "CustomerEMails_AView";
		super.queueTableName = "CustomerEMails_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onCreate$window_CustomerEMailList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerEMailList, borderLayout_CustomerEMailList, listBoxCustomerEMail,
				pagingCustomerEMailList);
		setItemRender(new CustomerEMailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerEMailList_NewCustomerEMail, "button_CustomerEMailList_NewCustomerEMail", true);
		registerButton(button_CustomerEMailList_CustomerEMailSearchDialog);

		registerField("custId");
		registerField("lovDescCustCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("custEMailTypeCode", listheader_CustEMailTypeCode, SortOrder.NONE, custEMailTypeCode,
				sortOperator_custEMailTypeCode, Operators.STRING);
		registerField("custEMailPriority", listheader_CustEMailPriority, SortOrder.NONE, custEMailPriority,
				sortOperator_custEMailPriority, Operators.STRING);
		registerField("custEMail", listheader_CustEMail, SortOrder.NONE, custEMailid, sortOperator_custEMailid,
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
	public void onClick$button_CustomerEMailList_CustomerEMailSearchDialog(Event event) {
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
	public void onClick$button_CustomerEMailList_NewCustomerEMail(Event event) {
		logger.debug("Entering");
		// create a new CustomerEMail object, We GET it from the back end.
		final CustomerEMail aCustomerEMail = new CustomerEMail();
		aCustomerEMail.setNewRecord(true);
		aCustomerEMail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aCustomerEMail);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerEMailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// get the selected CustomerEMail object
		final Listitem item = this.listBoxCustomerEMail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long id = (long) item.getAttribute("id");
			String typeCode = (String) item.getAttribute("typeCode");
			final CustomerEMail customerEMail = customerEMailService.getCustomerEMailById(id, typeCode);

			if (customerEMail == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND custId='" + customerEMail.getCustID() + "' AND version="
					+ customerEMail.getVersion() + " ";

			if (doCheckAuthority(customerEMail, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerEMail.getWorkflowId() == 0) {
					customerEMail.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerEMail);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerEMail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerEMail customerEMail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerEMail", customerEMail);
		arg.put("customerEMailListCtrl", this);
		arg.put("newRecord", customerEMail.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailDialog.zul", null,
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

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilterNotEqual("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW);

	}

	public void setCustomerEMailService(CustomerEMailService customerEMailService) {
		this.customerEMailService = customerEMailService;
	}
}