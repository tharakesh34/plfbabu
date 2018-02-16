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
 * FileName    		:  SuspenseListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.suspense;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.financemanagement.suspense.model.CustSuspenseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/Suspense/SuspenseList.zul file.
 */
public class CustSuspenseListCtrl extends GFCBaseListCtrl<Customer> {
	private static final long serialVersionUID = 4481377123949925578L;
	private static final Logger logger = Logger.getLogger(CustSuspenseListCtrl.class);

	protected Window window_CustSuspenseList;
	protected Borderlayout borderLayout_CustSuspenseList;
	protected Paging pagingCustSuspenseList;
	protected Listbox listBoxCustSuspense;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustShrtName;
	protected Listheader listheader_CustBranch;
	protected Listheader listheader_CustSts;
	protected Listheader listheader_CustStsChngdDate;
	protected Listheader listheader_CustSuspendDate;

	protected Longbox custID;
	protected Textbox custCIF;
	protected Listbox sortOperator_custID;
	protected Uppercasebox custSts;
	protected Listbox sortOperator_custSts;
	protected Uppercasebox custBranch;
	protected Listbox sortOperator_custBranch;
	protected Datebox custSuspenseDate;
	protected Listbox sortOperator_custSuspenseDate;

	protected Label label_SuspenseSearchResult;

	protected Textbox moduleType;

	protected Button button_CustSuspenseList_SuspenseSearchDialog;
	protected Button button_CustSuspenseList_NewSuspense;

	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private transient CustomerService customerService;
	protected Textbox moduleName;
	private String menuItemRightName = null;
	private String module = "";

	/**
	 * default constructor.<br>
	 */
	public CustSuspenseListCtrl() {
		super();
	}

	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		searchObject.addFilter(new Filter("custSuspTrigger", "M", Filter.OP_EQUAL));
	}
	
	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerSuspense";
		super.pageRightName = "SuspenseList";
		super.tableName = "Customers_AView";
		super.queueTableName = "Customers_View";
		this.menuItemRightName = (String)arguments.get("rightName");

		if ("CUSTSUSPHEAD".equals(moduleName.getValue())) {
			this.module = "CustSuspense";
		} else if ("SUSPENQ".equals(moduleName.getValue())) {
			this.module = "SuspenseEnquiry";
			moduleCode = null;
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustSuspenseList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// Getting Menu Item Right Name

		if (getUserWorkspace().getHasMenuRights().containsKey(menuItemRightName)) {
			menuItemRightName = getUserWorkspace().getHasMenuRights().get(menuItemRightName);
		}

		// Set the page level components.
		setPageComponents(window_CustSuspenseList, borderLayout_CustSuspenseList, listBoxCustSuspense,
				pagingCustSuspenseList);
		setItemRender(new CustSuspenseListModelItemRenderer());

		// Register buttons and fields.

		if ("CustSuspense".equals(this.module)) {
			registerButton(button_CustSuspenseList_NewSuspense, "button_FinanceFlagsList_NewFinanceFlags", true);
		} else {
			registerButton(button_CustSuspenseList_NewSuspense, RIGHT_NOT_ACCESSIBLE, true);
		}
		registerButton(button_CustSuspenseList_SuspenseSearchDialog);

		registerField("CustID", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custID, Operators.STRING);
		registerField("custSts", listheader_CustSts, SortOrder.NONE, custSts, sortOperator_custSts,
				Operators.MULTISELECT);
		registerField("custDftBranch", listheader_CustBranch, SortOrder.NONE, custBranch, sortOperator_custBranch,
				Operators.MULTISELECT);
		registerField("custSuspDate", listheader_CustSuspendDate, SortOrder.NONE, custSuspenseDate,
				sortOperator_custSuspenseDate, Operators.DATE);
		registerField("CustShrtName");
		registerField("custStsChgDate");
		registerField("LovDescCustStsName");
		registerField("LovDescCustCtgCodeName");
		registerField("LovDescCustTypeCodeName");

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
	public void onClick$button_CustSuspenseList_SuspenseSearchDialog(Event event) {
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
	public void onClick$button_CustSuspenseList_NewSuspense(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Customer aCustomer = new Customer();
		aCustomer.setNewRecord(true);
		aCustomer.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aCustomer);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustSuspenseItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustSuspense.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		Customer customer = customerService.getCustomerById(id);

		if (customer == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CustID='" + customer.getCustID() + "' AND Version=" + customer.getVersion() + " ";

		if (doCheckAuthority(customer, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && customer.getWorkflowId() == 0) {
				customer.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(customer);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCustomer
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Customer aCustomer) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("custSuspense", aCustomer);
		arg.put("custSuspenseListCtrl", this);
		arg.put("menuItemRightName", menuItemRightName);
		arg.put("eventCode", FinanceConstants.FINSER_EVENT_FINFLAGS);

		try {
			if ("CustSuspense".equals(this.module)) {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Suspense/CustSuspenseDialog.zul", null,
						arg);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Entering");
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

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustSts(Event event) {
		logger.debug("Entering " + event.toString());

		setSearchValue(sortOperator_custSts, this.custSts, "CustomerStatusCode");

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustBranch(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_custBranch, this.custBranch, "Branch");

		logger.debug("Leaving" + event.toString());
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

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
}