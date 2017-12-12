package com.pennant.webui.applicationmaster.blacklistcustomer;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
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

import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.applicationmaster.BlacklistCustomerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.blacklistcustomer.model.BlacklistCustomerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Dedup/BlacklistCustomers.zul file.
 */
public class BlacklistCustomerListCtrl extends GFCBaseListCtrl<BlackListCustomers> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BlacklistCustomerListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_blacklistCustomerList;
	protected Borderlayout borderLayout_blacklistCustomerList;
	protected Paging pagingBlacklistCustomerList;
	protected Listbox listboxBlacklistCustomer;

	protected Textbox customerCIF;
	protected Datebox custDOB;
	protected Textbox custFName;
	protected Textbox custLName;
	protected Textbox custEID;
	protected Textbox passport;
	protected Textbox mobileNum;
	//protected Textbox phoneCountryCode;
	//protected Textbox phoneAreaCode;
	protected Textbox nationality;

	protected Listbox sortOperator_customerCIF;
	protected Listbox sortOperator_custDOB;
	protected Listbox sortOperator_custFName;
	protected Listbox sortOperator_custLName;
	protected Listbox sortOperator_custEID;
	protected Listbox sortOperator_passport;
	protected Listbox sortOperator_mobileNum;
	protected Listbox sortOperator_nationality;

	// List headers
	protected Listheader listheader_CustomerCIF;
	protected Listheader listheader_CustDOB;
	protected Listheader listheader_CustFName;
	protected Listheader listheader_CustLName;
	protected Listheader listheader_CustEID;
	protected Listheader listheader_Passport;
	protected Listheader listheader_Mobile;
	protected Listheader listheader_Nationality;
	protected Listheader listheader_Employer;

	// checkRights
	protected Button button_BlacklistCustomerList_NewBlacklistCustomer;
	protected Button button_BlacklistCustomerList_SearchDialog;
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	
	private transient BlacklistCustomerService blacklistCustomerService;

	/**
	 * default constructor.<br>
	 */
	public BlacklistCustomerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BlackListCustomers";
		super.pageRightName = "BlacklistCustomerList";
		super.tableName = "BlacklistCustomer_View";
		super.queueTableName = "BlacklistCustomer_View";
	}

	@Override
	protected void doAddFilters() {

		super.doAddFilters();
		Filter filter = SearchFilterControl.getFilter("mobileNumber", this.mobileNum.getValue(),
				sortOperator_mobileNum);
		if(filter != null){
			searchObject.addFilter(filter);
		}
	}

	@Override
	protected void doReset() {
		super.doReset();
		SearchFilterControl.resetFilters(mobileNum, sortOperator_mobileNum);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_blacklistCustomerList(Event event) {

		// Set the page level components.
		setPageComponents(window_blacklistCustomerList, borderLayout_blacklistCustomerList, listboxBlacklistCustomer,
				pagingBlacklistCustomerList);
		setItemRender(new BlacklistCustomerListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BlacklistCustomerList_NewBlacklistCustomer,
				"button_blacklistCustomerList_NewBlacklistCustomer", true);
		registerButton(button_BlacklistCustomerList_SearchDialog);

		registerField("custCIF", listheader_CustomerCIF, SortOrder.ASC, customerCIF, sortOperator_customerCIF,
				Operators.STRING);
		registerField("custFName", listheader_CustFName, SortOrder.NONE, custFName, sortOperator_custFName,
				Operators.STRING);
		registerField("custLName", listheader_CustLName, SortOrder.NONE, custLName, sortOperator_custLName,
				Operators.STRING);
		registerField("custDOB", listheader_CustDOB, SortOrder.NONE, custDOB, sortOperator_custDOB, Operators.DATE);
		registerField("custCRCPR", listheader_CustEID, SortOrder.NONE, custEID, sortOperator_custEID, Operators.STRING);
		registerField("custPassportNo", listheader_Passport, SortOrder.NONE, passport, sortOperator_passport,
				Operators.STRING);
		registerField("mobileNumber", listheader_Mobile);
		registerField("custNationality", listheader_Nationality, SortOrder.NONE, nationality, sortOperator_nationality,
				Operators.STRING);
		registerField("employer", listheader_Employer, SortOrder.NONE);
		registerField("lovDescEmpName");

		SearchFilterControl.renderOperators(this.sortOperator_mobileNum, Operators.STRING);

		doSetFieldProperties();
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
	public void onClick$button_BlacklistCustomerList_SearchDialog(Event event) {
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
	public void onClick$button_BlacklistCustomerList_NewBlacklistCustomer(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		BlackListCustomers blackListCustomers = new BlackListCustomers();
		blackListCustomers.setNewRecord(true);
		blackListCustomers.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(blackListCustomers);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onBlacklistCustomerItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listboxBlacklistCustomer.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		BlackListCustomers blackListCustomers = blacklistCustomerService.getBlacklistCustomerById(id);

		if (blackListCustomers == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND custCIF='" + blackListCustomers.getCustCIF() + "' AND version="
				+ blackListCustomers.getVersion() + " ";

		if (doCheckAuthority(blackListCustomers, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && blackListCustomers.getWorkflowId() == 0) {
				blackListCustomers.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(blackListCustomers);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aBlacklistCustomer
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BlackListCustomers aBlacklistCustomer) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("blackListCustomer", aBlacklistCustomer);
		arg.put("blacklistCustomerListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Blacklist/BlacklistCustomerDialog.zul", null,
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

	private void doSetFieldProperties() {
		this.customerCIF.setMaxlength(12);
		this.mobileNum.setMaxlength(10);
		this.custFName.setMaxlength(50);
		this.custLName.setMaxlength(50);
		this.custEID.setMaxlength(20);
		this.passport.setMaxlength(50);
		this.nationality.setMaxlength(2);
		this.custDOB.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.recordStatus.setMaxlength(50);
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
		this.customerCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.customerCIF.setValue(customer.getCustCIF());
		} else {
			this.customerCIF.setValue("");
		}
		logger.debug("Leaving ");
	}


	public void setBlacklistCustomerService(BlacklistCustomerService blacklistCustomerService) {
		this.blacklistCustomerService = blacklistCustomerService;
	}

}
