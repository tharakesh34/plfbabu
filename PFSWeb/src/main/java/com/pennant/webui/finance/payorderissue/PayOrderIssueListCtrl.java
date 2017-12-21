/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related PayOrderIssues. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * RepayOrderIssueion or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PayOrderIssueListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.payorderissue;

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

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.service.payorderissue.PayOrderIssueService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.finance.payorderissue.model.PayOrderIssueListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/PayOrderIssue/PayOrderIssueList.zul file.
 */
public class PayOrderIssueListCtrl extends GFCBaseListCtrl<PayOrderIssueHeader> {
	private static final long serialVersionUID = -6951358943287040101L;
	private static final Logger logger = Logger.getLogger(PayOrderIssueListCtrl.class);

	protected Window window_PayOrderIssueList;
	protected Borderlayout borderLayout_PayOrderIssueList;
	protected Paging pagingPayOrderIssueList;
	protected Listbox listBoxPayOrderIssue;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustShrtName;
	protected Listheader listheader_FinRef;
	protected Listheader listheader_FinType;
	protected Listheader listheader_TotalPOAmount;
	protected Listheader listheader_TotalPOCount;
	protected Listheader listheader_IssuedPOAmount;
	protected Listheader listheader_IssuedPOCount;
	protected Listheader listheader_PODueAmount;
	protected Listheader listheader_PODueCount;

	protected Textbox custCIF;
	protected Listbox sortOperator_custCIF;
	protected Textbox finReference;
	protected Listbox sortOperator_finReference;
	protected Textbox custName;
	protected Listbox sortOperator_custName;

	protected Button button_PayOrderIssueList_NewPayOrderIssue;
	protected Button button_PayOrderIssueList_PayOrderIssueSearchDialog;

	protected JdbcSearchObject<Customer> custCIFSearchObject;

	private transient PayOrderIssueService payOrderIssueService;

	/**
	 * default constructor.<br>
	 */
	public PayOrderIssueListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PayOrderIssueHeader";
		super.pageRightName = "PayOrderIssueList";
		super.tableName = "PayOrderIssueHeader_View";
		super.queueTableName = "PayOrderIssueHeader_View";
		super.enquiryTableName = "PayOrderIssueHeader_View";
	}
	
	@Override
	protected void doAddFilters() {

		super.doAddFilters();
		if (!enqiryModule) {
			searchObject.addFilterEqual("FinIsActive", 1);
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PayOrderIssueList(Event event) {
		
		// Set the page level components.
		setPageComponents(window_PayOrderIssueList, borderLayout_PayOrderIssueList, listBoxPayOrderIssue,
				pagingPayOrderIssueList);
		setItemRender(new PayOrderIssueListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PayOrderIssueList_NewPayOrderIssue, RIGHT_NOT_ACCESSIBLE, true);
		registerButton(button_PayOrderIssueList_PayOrderIssueSearchDialog);

		registerField("CustCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_custCIF, Operators.STRING);
		registerField("FinReference", listheader_FinRef, SortOrder.ASC, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("CustShrtName", listheader_CustShrtName, SortOrder.NONE, custName, sortOperator_custName,
				Operators.STRING);
		registerField("FinType", listheader_FinType, SortOrder.NONE);
		registerField("TotalPOAmount", listheader_TotalPOAmount, SortOrder.NONE);
		registerField("TotalPOCount", listheader_TotalPOCount, SortOrder.NONE);
		registerField("IssuedPOAmount", listheader_IssuedPOAmount, SortOrder.NONE);
		registerField("IssuedPOCount", listheader_IssuedPOCount, SortOrder.NONE);
		registerField("PODueAmount", listheader_PODueAmount, SortOrder.NONE);
		registerField("PODueCount", listheader_PODueCount, SortOrder.NONE);

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
	public void onClick$button_PayOrderIssueList_PayOrderIssueSearchDialog(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPayOrderIssueItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPayOrderIssue.getSelectedItem();

		// Get the selected entity.
		String finRef = (String) selectedItem.getAttribute("finRef");
		PayOrderIssueHeader aPayOrderIssueHeader = payOrderIssueService.getPayOrderIssueHeaderById(finRef);

		if (aPayOrderIssueHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinReference='" + aPayOrderIssueHeader.getFinReference() + "' AND version="
				+ aPayOrderIssueHeader.getVersion() + " ";

		if (doCheckAuthority(aPayOrderIssueHeader, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aPayOrderIssueHeader.getWorkflowId() == 0) {
				aPayOrderIssueHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aPayOrderIssueHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aPayOrderIssueHeader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PayOrderIssueHeader aPayOrderIssueHeader) {
		logger.debug("Entering");

		if (aPayOrderIssueHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aPayOrderIssueHeader.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> arg = getDefaultArguments();
		arg.put("payOrderIssueHeader", aPayOrderIssueHeader);
		arg.put("enqModule", enqiryModule);
		arg.put("moduleCode", super.moduleCode);
		arg.put("payOrderIssueListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/PayOrderIssue/PayOrderIssueDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
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

	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

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
	
	public void setPayOrderIssueService(PayOrderIssueService payOrderIssueService) {
		this.payOrderIssueService = payOrderIssueService;
	}

}