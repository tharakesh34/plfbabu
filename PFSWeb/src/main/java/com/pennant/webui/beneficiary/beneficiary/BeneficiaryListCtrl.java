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
 * FileName    		:  BeneficiaryListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2016    														*
 *                                                                  						*
 * Modified Date    :  01-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.beneficiary.beneficiary;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.beneficiary.BeneficiaryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.beneficiary.beneficiary.model.BeneficiaryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.beneficiary/Beneficiary/BeneficiaryList.zul file.
 * 
 */
public class BeneficiaryListCtrl extends GFCBaseListCtrl<Beneficiary> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BeneficiaryListCtrl.class);

	protected Window window_BeneficiaryList;
	protected Borderlayout borderLayout_BeneficiaryList;
	protected Paging pagingBeneficiaryList;
	protected Listbox listBoxBeneficiary;

	// List headers
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_AccNumber;
	protected Listheader listheader_AccHolderName;

	// checkRights
	protected Button button_BeneficiaryList_NewBeneficiary;
	protected Button button_BeneficiaryList_BeneficiarySearch;

	protected Textbox custCIF;
	protected Textbox accNo;
	protected Textbox accHolderName;

	protected Listbox sortOperator_CustCIF;
	protected Listbox sortOperator_AccHolderName;
	protected Listbox sortOperator_AccNo;

	private transient BeneficiaryService beneficiaryService;
	protected JdbcSearchObject<Customer>	custCIFSearchObject;

	/**
	 * default constructor.<br>
	 */
	public BeneficiaryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Beneficiary";
		super.pageRightName = "BeneficiaryList";
		super.tableName = "Beneficiary_AView";
		super.queueTableName = "Beneficiary_View";
		super.enquiryTableName = "Beneficiary_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_BeneficiaryList(Event event) {
		// Set the page level components.
		setPageComponents(window_BeneficiaryList, borderLayout_BeneficiaryList, listBoxBeneficiary,
				pagingBeneficiaryList);
		setItemRender(new BeneficiaryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BeneficiaryList_BeneficiarySearch);
		registerButton(button_BeneficiaryList_NewBeneficiary, "button_BeneficiaryList_NewBeneficiary", true);

		registerField("beneficiaryId");
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_CustCIF, Operators.STRING);
		registerField("accNumber", listheader_AccNumber, SortOrder.NONE, accNo, sortOperator_AccNo, Operators.STRING);
		registerField("accHolderName", listheader_AccHolderName, SortOrder.NONE, accHolderName,
				sortOperator_AccHolderName, Operators.STRING);
		registerField("bankBranchID");

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
	public void onClick$button_BeneficiaryList_BeneficiarySearch(Event event) {
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
	public void onClick$button_BeneficiaryList_NewBeneficiary(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Beneficiary beneficiary = new Beneficiary();
		beneficiary.setNewRecord(true);
		beneficiary.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(beneficiary);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onBeneficiaryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBeneficiary.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		Beneficiary beneficiary = beneficiaryService.getBeneficiaryById(id);

		if (beneficiary == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BeneficiaryId='" + beneficiary.getBeneficiaryId() + "' AND version="
				+ beneficiary.getVersion() + " ";

		if (doCheckAuthority(beneficiary, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && beneficiary.getWorkflowId() == 0) {
				beneficiary.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(beneficiary);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aBeneficiary
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Beneficiary aBeneficiary) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("beneficiary", aBeneficiary);
		arg.put("beneficiaryListCtrl", this);
		arg.put("enqModule", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Beneficiary/BeneficiaryDialog.zul", null, arg);
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
		if (enqiryModule) {
			moduleCode = "BeneficiaryEnquiry";
		}
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
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}
	
	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	public void setBeneficiaryService(BeneficiaryService beneficiaryService) {
		this.beneficiaryService = beneficiaryService;
	}

}