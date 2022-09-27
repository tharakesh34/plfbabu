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
 * * FileName : CustomerListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.model.CustomerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul file.
 */
public class CustomerListCtrl extends GFCBaseListCtrl<Customer> {
	private static final long serialVersionUID = 9086034736503097868L;
	private static final Logger logger = LogManager.getLogger(CustomerListCtrl.class);

	protected Window window_CustomerList;
	protected Borderlayout borderLayout_CustomerList;
	protected Paging pagingCustomerList;
	protected Listbox listBoxCustomer;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustCoreBank;
	protected Listheader listheader_CustShrtName;
	protected Listheader listheader_CustDftBranch;
	protected Listheader listheader_CustCtgCode;
	protected Listheader listheader_CustTypeCode;
	protected Listheader listheader_RequestStage;

	protected Button button_CustomerList_NewCustomer;
	protected Button button_CustomerList_CustomerSearchDialog;

	protected Textbox custCIF;
	protected Textbox custCoreBank;
	protected Combobox custCtgCode;
	protected Textbox custTypeCode;
	protected Textbox custShrtName;
	protected Textbox custDftBranch;

	protected Listbox sortOperator_custDftBranch;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custCoreBank;
	protected Listbox sortOperator_custCtgCode;
	protected Listbox sortOperator_custTypeCode;
	protected Listbox sortOperator_custShrtName;
	protected Listbox sortOperator_custEID;
	protected Listbox sortOperator_custMobile;
	protected Textbox custEid;
	protected Textbox custMobile;

	private transient CustomerDetailsService customerDetailsService;
	private final List<ValueLabel> custCtgCodeList = PennantAppUtil.getcustCtgCodeList();
	private String module = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Customer";
		super.pageRightName = "CustomerList";
		super.tableName = "Customers_AView";
		super.queueTableName = "Customers_LView";
		super.enquiryTableName = "Customers_TView";

		this.module = getArgument("enqiryModule");
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerList(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerList, borderLayout_CustomerList, listBoxCustomer, pagingCustomerList);
		setItemRender(new CustomerListModelItemRenderer());

		// Register buttons and fields.
		if (enqiryModule || StringUtils.equals("360", module)) {
			button_CustomerList_NewCustomer.setVisible(false);
		} else {
			registerButton(button_CustomerList_NewCustomer, "button_CustomerList_NewCustomer", true);
		}
		registerButton(button_CustomerList_CustomerSearchDialog);

		registerField("custId");
		registerField("custCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.SIMPLESTRING);
		registerField("custCoreBank", listheader_CustCoreBank, SortOrder.NONE, custCoreBank, sortOperator_custCoreBank,
				Operators.STRING);
		registerField("custShrtName", listheader_CustShrtName, SortOrder.NONE, custShrtName, sortOperator_custShrtName,
				Operators.SIMPLESTRING);
		registerField("custDftBranch", listheader_CustDftBranch, SortOrder.NONE, custDftBranch,
				sortOperator_custDftBranch, Operators.STRING);

		registerField("custCRCPR", custEid, SortOrder.NONE, sortOperator_custEID, Operators.STRING);
		this.custMobile.setMaxlength(LengthConstants.LEN_MOBILE);
		registerField("phoneNumber", custMobile, SortOrder.NONE, sortOperator_custMobile, Operators.STRING);

		fillComboBox(this.custCtgCode, "", custCtgCodeList, "");
		registerField("custCtgCode", listheader_CustCtgCode, SortOrder.NONE, custCtgCode, sortOperator_custCtgCode,
				Operators.STRING);

		registerField("lovDescCustCtgCodeName");
		registerField("lovDescCustTypeCodeName", listheader_CustTypeCode, SortOrder.NONE, custTypeCode,
				sortOperator_custTypeCode, Operators.STRING);
		registerField("LovDescRequestStage", listheader_RequestStage);

		// Render the page and display no data when the page loaded for the first time.
		doRenderPage();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CustomerList_CustomerSearchDialog(Event event) {
		if (!isFilterApplied()) {
			MessageUtil.showError("Please enter at least one search criteria.");
			return;
		}

		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		applyDefaultFilter = false;
		doReset();

		pagedListWrapper.clear();
		// search();
	}

	public void refreshList() {
		applyDefaultFilter = false;
		search();
	}

	protected void applySearch() {
		if (isFilterApplied()) {
			pagedListWrapper.init(this.searchObject, this.listbox, this.paging);
		}
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CustomerList_NewCustomer(Event event) {
		logger.debug("Entering");

		Map<String, Object> map = getDefaultArguments();
		map.put("customerListCtrl", this);

		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CoreCustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCustomerItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// get the selected Customer object
		final Listitem item = this.listBoxCustomer.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long id = (long) item.getAttribute("id");
			final CustomerDetails customerDetails = customerDetailsService.getCustomerById(id);

			if (customerDetails == null || customerDetails.getCustomer() == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			customerDetailsService.setFinForCoApplicantAndGuarantor(customerDetails);
			// Check whether the user has authority to change/view the record.
			String wherCondition = " where custId = ?";
			long custID = customerDetails.getCustomer().getCustID();

			if (doCheckAuthority(customerDetails.getCustomer(), wherCondition, new Object[] { custID })) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerDetails.getCustomer().getWorkflowId() == 0) {
					customerDetails.getCustomer().setWorkflowId(getWorkFlowId());
				}

				logUserAccess("menu_Item_NewCustomer", String.valueOf(custID));
				doShowDialogPage(customerDetails);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerDetails The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerDetails customerDetails) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerDetails", customerDetails);
		arg.put("customerListCtrl", this);
		arg.put("newRecord", customerDetails.getCustomer().isNewRecord());
		arg.put("module", module);
		arg.put("isFromCustomer", true);

		try {
			StringBuilder pageName = new StringBuilder("/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog");
			pageName.append(".zul");

			if (enqiryModule) {
				Executions.createComponents(pageName.toString(), null, arg);
			} else if (StringUtils.equals("360", module)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/customerView.zul", null, arg);
			} else {
				Executions.createComponents(pageName.toString(), null, arg);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Build the Customer Dialog Window with Existing Core banking Data
	 */
	public void buildDialogWindow(CustomerDetails customerDetails, boolean newRecord) {
		logger.debug("Entering");
		if (customerDetails != null) {
			if (isWorkFlowEnabled() && customerDetails.getCustomer().getWorkflowId() == 0) {
				customerDetails.getCustomer().setWorkflowId(getWorkFlowId());
			}
			if (newRecord) {
				// create a new Customer object, We GET it from the backEnd.
				// CustomerDetails aCustomerDetails =
				// getCustomerDetailsService().getNewCustomer(false);
				Customer customerlov = customerDetailsService.fetchCustomerDetails(customerDetails.getCustomer());
				customerDetails.setCustomer(customerlov);
				customerDetailsService.setCustomerDetails(customerDetails);
			}
			doShowDialogPage(customerDetails);
		}
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		if (enqiryModule) {
			moduleCode = "CustomerEnquiry";
		}
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

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

}