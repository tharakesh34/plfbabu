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
 * FileName    		:  CustomerIdentityListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customeridentity;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.service.customermasters.CustomerIdentityService;
import com.pennant.webui.customermasters.customeridentity.model.CustomerIdentityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerIdentity/CustomerIdentityList.zul file.
 */
public class CustomerIdentityListCtrl extends GFCBaseListCtrl<CustomerIdentity> {
	private static final long serialVersionUID = -3970688148092697445L;
	private static final Logger logger = Logger.getLogger(CustomerIdentityListCtrl.class);

	protected Window window_CustomerIdentityList;
	protected Borderlayout borderLayout_CustomerIdentityList;
	protected Paging pagingCustomerIdentityList;
	protected Listbox listBoxCustomerIdentity;

	protected Listheader listheader_CustIdCIF;
	protected Listheader listheader_IdType;
	protected Listheader listheader_IdIssuedBy;
	protected Listheader listheader_IdRef;
	protected Listheader listheader_IdIssueCountry;

	protected Textbox idCustCIF;
	protected Textbox idType;
	protected Textbox idIssuedBy;
	protected Textbox idRef;
	protected Textbox idIssueCountry;
	protected Datebox idIssuedOn;
	protected Datebox idExpiresOn;
	protected Textbox idLocation;

	protected Listbox sortOperator_idLocation;
	protected Listbox sortOperator_idCustCIF;
	protected Listbox sortOperator_idType;
	protected Listbox sortOperator_idIssuedBy;
	protected Listbox sortOperator_idRef;
	protected Listbox sortOperator_idIssueCountry;
	protected Listbox sortOperator_idIssuedOn;
	protected Listbox sortOperator_idExpiresOn;

	protected Button button_CustomerIdentityList_NewCustomerIdentity;
	protected Button button_CustomerIdentityList_CustomerIdentitySearchDialog;

	private transient CustomerIdentityService customerIdentityService;

	/**
	 * default constructor.<br>
	 */
	public CustomerIdentityListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerIdentity";
		super.pageRightName = "CustomerIdentityList";
		super.tableName = "CustIdentities_AView";
		super.queueTableName = "CustIdentities_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerIdentityList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerIdentityList, borderLayout_CustomerIdentityList, listBoxCustomerIdentity,
				pagingCustomerIdentityList);
		setItemRender(new CustomerIdentityListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerIdentityList_NewCustomerIdentity,
				"button_CustomerIdentityList_NewCustomerIdentity", true);
		registerButton(button_CustomerIdentityList_CustomerIdentitySearchDialog);

		registerField("idcustid");
		registerField("lovdescCustCif", listheader_CustIdCIF, SortOrder.ASC, idCustCIF, sortOperator_idCustCIF,
				Operators.STRING);
		registerField("idType", listheader_IdType, SortOrder.NONE, idType, sortOperator_idType, Operators.STRING);
		registerField("lovDescIdTypeName");
		registerField("idIssuedBy", listheader_IdIssuedBy, SortOrder.NONE, idIssuedBy, sortOperator_idIssuedBy,
				Operators.STRING);
		registerField("idRef", listheader_IdRef, SortOrder.NONE, idRef, sortOperator_idRef, Operators.STRING);
		registerField("idIssueCountry", listheader_IdIssuedBy, SortOrder.NONE, idIssuedBy, sortOperator_idIssuedBy,
				Operators.STRING);
		registerField("lovDescIdIssueCountryName");

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
	public void onClick$button_CustomerIdentityList_CustomerIdentitySearchDialog(Event event) {
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
	public void onClick$button_CustomerIdentityList_NewCustomerIdentity(Event event) {
		logger.debug("Entering");
		// create a new CustomerIdentity object, We GET it from the backEnd.
		final CustomerIdentity aCustomerIdentity = new CustomerIdentity();
		aCustomerIdentity.setNewRecord(true);
		aCustomerIdentity.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aCustomerIdentity);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerIdentityItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected CustomerIdentity object
		final Listitem item = this.listBoxCustomerIdentity.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long id = (long) item.getAttribute("id");
			String idType = (String) item.getAttribute("idType");
			final CustomerIdentity customerIdentity = customerIdentityService.getCustomerIdentityById(id, idType);
			if (customerIdentity == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND IdCustID='" + customerIdentity.getIdCustID() + "' AND version="
					+ customerIdentity.getVersion() + " ";

			if (doCheckAuthority(customerIdentity, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerIdentity.getWorkflowId() == 0) {
					customerIdentity.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerIdentity);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerIdentity
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerIdentity customerIdentity) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerIdentity", customerIdentity);
		arg.put("customerIdentityListCtrl", this);
		arg.put("newRecord", customerIdentity.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIdentity/CustomerIdentityDialog.zul",
					null, arg);
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

	public void setCustomerIdentityService(CustomerIdentityService customerIdentityService) {
		this.customerIdentityService = customerIdentityService;
	}

}