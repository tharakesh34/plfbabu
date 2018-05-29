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
package com.pennant.webui.financemanagement.receipts;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
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

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.webui.financemanagement.receipts.model.ReceiptCancellationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/ReceiptCancellation/ReceiptCancellationList.zul file.
 */
public class ReceiptCancellationListCtrl extends GFCBaseListCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = Logger.getLogger(ReceiptCancellationListCtrl.class);

	protected Window window_ReceiptCancellationList;
	protected Borderlayout borderLayout_ReceiptCancellationList;
	protected Listbox listBoxReceiptCancellation;
	protected Paging pagingReceiptCancellationList;

	protected Listheader listheader_ReceiptCancellationReference;
	protected Listheader listheader_ReceiptCancellationPurpose;
	protected Listheader listheader_ReceiptCancellationMode;
	protected Listheader listheader_ReceiptCancellationAmount;
	protected Listheader listheader_ReceiptCancellationAllocattionType;
	protected Listheader listheader_ReceiptCancellationFinType;
	protected Listheader listheader_ReceiptCancellationFinBranch;
	protected Listheader listheader_ReceiptCancellationCusomer;
	protected Listheader listheader_ReceiptCancellationCustName;

	protected Button btnNew;
	protected Button btnSearch;

	protected Textbox receiptReference;
	protected Textbox customer;
	protected Combobox purpose;
	protected Combobox receiptMode;
	protected Combobox allocationType;
	protected Textbox finType;
	protected Textbox finBranch;

	protected Listbox sortOperator_ReceiptCancellationReference;
	protected Listbox sortOperator_ReceiptCancellationCustomer;
	protected Listbox sortOperator_ReceiptCancellationPurpose;
	protected Listbox sortOperator_ReceiptCancellationReceiptMode;
	protected Listbox sortOperator_ReceiptCancellationAllocationType;
	protected Listbox sortOperator_ReceiptCancellationFinType;
	protected Listbox sortOperator_ReceiptCancellationFinBranch;

	protected int   oldVar_sortOperator_custCIF; 
	protected int   oldVar_sortOperator_finType;
	protected int   oldVar_sortOperator_finBranch;

	private transient ReceiptCancellationService receiptCancellationService;
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	private String module;

	/**
	 * The default constructor.
	 */
	public ReceiptCancellationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		
		this.module = getArgument("module");
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			super.moduleCode = "ReceiptBounce";
			super.pageRightName = "ReceiptBounceList";
		}else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
			super.moduleCode = "ReceiptCancellation";
			super.pageRightName = "ReceiptCancellationList";
		}else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			super.moduleCode = "ReceiptCancellation";
			super.pageRightName = "ReceiptCancellationList";
		}
	
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			super.tableName = "FinReceiptHeader_FCView";
			super.queueTableName = "FinReceiptHeader_FCView";
			super.enquiryTableName = "FinReceiptHeader_FCView";
		}else{
			super.tableName = "FinReceiptHeader_View";
			super.queueTableName = "FinReceiptHeader_View";
			super.enquiryTableName = "FinReceiptHeader_View";
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReceiptCancellationList(Event event) {
		// Set the page level components.
		setPageComponents(window_ReceiptCancellationList, borderLayout_ReceiptCancellationList, listBoxReceiptCancellation, pagingReceiptCancellationList);
		setItemRender(new ReceiptCancellationListModelItemRenderer());
		registerButton(btnNew, "button_ReceiptCancellationList_NewReceiptCancellation", false);
		registerButton(btnSearch);

		registerField("receiptID");
		registerField("finCcy");
		registerField("reference", listheader_ReceiptCancellationReference, SortOrder.ASC, receiptReference,
				sortOperator_ReceiptCancellationReference, Operators.STRING);
		registerField("custCIF", listheader_ReceiptCancellationCusomer, SortOrder.NONE, customer,
				sortOperator_ReceiptCancellationCustomer, Operators.STRING);
		fillComboBox(this.purpose, "", PennantStaticListUtil.getReceiptPurpose(), "");
		registerField("receiptPurpose", listheader_ReceiptCancellationPurpose, SortOrder.NONE, purpose,
				sortOperator_ReceiptCancellationPurpose, Operators.STRING);
		fillComboBox(this.receiptMode, "", PennantStaticListUtil.getReceiptModes(), "");
		registerField("receiptMode", listheader_ReceiptCancellationMode, SortOrder.NONE, receiptMode, sortOperator_ReceiptCancellationReceiptMode,
				Operators.STRING);
		registerField("receiptAmount", listheader_ReceiptCancellationAmount);
		fillComboBox(this.allocationType, "", PennantStaticListUtil.getAllocationMethods(), "");
		registerField("allocationType", listheader_ReceiptCancellationAllocattionType, SortOrder.NONE, allocationType,
				sortOperator_ReceiptCancellationAllocationType, Operators.STRING);
		registerField("finType", listheader_ReceiptCancellationFinType, SortOrder.NONE, finType,
				sortOperator_ReceiptCancellationFinType, Operators.STRING);
		registerField("finBranch", listheader_ReceiptCancellationFinBranch, SortOrder.NONE, finBranch, sortOperator_ReceiptCancellationFinBranch,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}
	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			this.searchObject.addWhereClause(" FinIsActive = 1 AND ReceiptMode IN( '"+RepayConstants.RECEIPTMODE_CHEQUE+"','"+RepayConstants.RECEIPTMODE_DD+"') AND "
					+ " ReceiptPurpose = '"+FinanceConstants.FINSER_EVENT_SCHDRPY+"' AND (ReceiptModeStatus = '"+RepayConstants.PAYSTATUS_APPROVED+"' "
					+ " OR (ReceiptModeStatus = '"+RepayConstants.PAYSTATUS_REALIZED+"' AND (RecordType IS NULL OR RecordType='' ))"
					+ " OR ( ReceiptModeStatus = '"+RepayConstants.PAYSTATUS_BOUNCE+"' AND RecordType IS NOT NULL) ) ");
		}else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
			this.searchObject.addWhereClause("  FinIsActive = 1 AND ReceiptPurpose = '"+FinanceConstants.FINSER_EVENT_SCHDRPY+"' "
					+ " AND (ReceiptModeStatus = '"+RepayConstants.PAYSTATUS_APPROVED+"' OR (ReceiptModeStatus = '"+RepayConstants.PAYSTATUS_REALIZED+"' "
					+ " AND( RecordType IS NULL  OR RecordType='')) OR ( ReceiptModeStatus = '"+RepayConstants.PAYSTATUS_CANCEL+"' AND RecordType IS NOT NULL AND RecordType != '') ) ");
		}else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			this.searchObject.addWhereClause("  FinIsActive = 1 AND ReceiptPurpose = '"+FinanceConstants.FINSER_EVENT_FEEPAYMENT+"' AND "
					+ " ((ReceiptModeStatus = '"+RepayConstants.PAYSTATUS_FEES+"'  AND( RecordType IS NULL OR RecordType='')) "
					+ " OR ( ReceiptModeStatus = '"+RepayConstants.PAYSTATUS_CANCEL+"' AND RecordType IS NOT NULL) ) ");
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		this.customer.setValue("");
		this.sortOperator_ReceiptCancellationCustomer.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_ReceiptCancellationFinType.setSelectedIndex(0);
		this.finBranch.setValue("");
		this.sortOperator_ReceiptCancellationFinBranch.setSelectedIndex(0);
		this.listBoxReceiptCancellation.getItems().clear();
		this.oldVar_sortOperator_custCIF=0;
		this.oldVar_sortOperator_finType=0;
		this.oldVar_sortOperator_finBranch=0;
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ReceiptCancellationList_NewReceiptCancellation(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinReceiptHeader receiptHeader = new FinReceiptHeader();
		receiptHeader.setNewRecord(true);
		receiptHeader.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(receiptHeader);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onReceiptCancellationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReceiptCancellation.getSelectedItem();

		// Get the selected entity.
		FinReceiptHeader headerListItem = (FinReceiptHeader) selectedItem.getAttribute("data");
		boolean isFeePayment = false;
		if(StringUtils.equals(headerListItem.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_FEEPAYMENT)){
			isFeePayment = true;
		}
		
		FinReceiptHeader header = receiptCancellationService.getFinReceiptHeaderById(headerListItem.getReceiptID(),isFeePayment);

		if (header == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ReceiptID='" + header.getReceiptID() + "' AND version=" + header.getVersion() + " ";

		if (doCheckAuthority(header, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && header.getWorkflowId() == 0) {
				header.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(header);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param header
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinReceiptHeader header) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("receiptHeader", header);
		arg.put("module", this.module);
		arg.put("receiptCancellationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptCancellationDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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
		this.customer.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.customer.setValue(customer.getCustCIF());
		} else {
			this.customer.setValue("");
		}
		logger.debug("Leaving ");
	}
	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		if(this.oldVar_sortOperator_finType == Filter.OP_IN || this.oldVar_sortOperator_finType == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_ReceiptCancellationList, "FinanceType", this.finType.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finType.setValue(selectedValues);
			}

		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_ReceiptCancellationList, "FinanceType");
			if (dataObject instanceof String) {
				this.finType.setValue("");
			} else {
				FinanceType details = (FinanceType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFinType());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchBranchCode" button
	 * This method displays ExtendedSearchListBox with branch details
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event){
		logger.debug("Entering  "+event.toString());

		if(this.oldVar_sortOperator_finBranch == Filter.OP_IN || this.oldVar_sortOperator_finBranch == Filter.OP_NOT_IN){
			//Calling MultiSelection ListBox From DB
			String selectedValues= (String) MultiSelectionSearchListBox.show(
					this.window_ReceiptCancellationList, "Branch", this.finBranch.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finBranch.setValue(selectedValues);
			}

		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_ReceiptCancellationList,"Branch");
			if (dataObject instanceof String){
				this.finBranch.setValue("");
			}else{
				Branch details= (Branch) dataObject;
				if (details != null) {
					this.finBranch.setValue(details.getBranchCode());
				}
			}
		}
		logger.debug("Leaving"+event.toString());
	}

	// On Change Events for Multi-Selection Listbox's for Search operators

	public void onSelect$sortOperator_ReceiptCancellationCustomer(Event event) {
		this.oldVar_sortOperator_custCIF = doChangeStringOperator(sortOperator_ReceiptCancellationCustomer, oldVar_sortOperator_custCIF, this.customer);
	}

	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_ReceiptCancellationFinType, oldVar_sortOperator_finType, this.finType);
	}

	public void onSelect$sortOperator_finBranch(Event event) {
		this.oldVar_sortOperator_finBranch = doChangeStringOperator(sortOperator_ReceiptCancellationFinBranch, oldVar_sortOperator_finBranch, this.finBranch);
	}

	private int doChangeStringOperator(Listbox listbox,int oldOperator,Textbox textbox){

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = Integer.parseInt(((ValueLabel) item.getAttribute("data")).getValue());

		if(oldOperator == Filter.OP_IN || oldOperator == Filter.OP_NOT_IN){
			if(!(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN)){
				textbox.setValue("");
			}
		}else{
			if(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN){
				textbox.setValue("");
			}
		}
		return searchOpId;

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

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}
}