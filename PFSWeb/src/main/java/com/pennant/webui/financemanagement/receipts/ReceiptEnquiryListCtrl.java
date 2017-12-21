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

import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.search.Filter;
import com.pennant.webui.financemanagement.receipts.model.ReceiptRealizationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/ReceiptRealization/ReceiptRealizationEnqListCtrl.zul file.
 */
public class ReceiptEnquiryListCtrl extends GFCBaseListCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = Logger.getLogger(ReceiptEnquiryListCtrl.class);

	protected Window window_ReceiptEnquiryList;
	protected Borderlayout borderLayout_ReceiptEnquiryList;
	protected Listbox listBoxReceipt;
	protected Paging pagingReceiptList;

	protected Listheader listheader_ReceiptReference;
	protected Listheader listheader_ReceiptPurpose;
	protected Listheader listheader_ReceiptMode;
	protected Listheader listheader_ReceiptAmount;
	protected Listheader listheader_ReceiptAllocattionType;
	protected Listheader listheader_ReceiptFinType;
	protected Listheader listheader_ReceiptFinBranch;
	protected Listheader listheader_ReceiptCusomer;
	protected Listheader listheader_ReceiptCustName;
	protected Listheader listheader_ReceiptStatus;

	protected Button btnNew;
	protected Button btnSearch;

	protected Textbox receiptReference;
	protected Textbox customer;
	protected Combobox purpose;
	protected Combobox receiptMode;
	protected Combobox allocationType;
	protected Textbox finType;
	protected Textbox finBranch;

	protected Listbox sortOperator_ReceiptReference;
	protected Listbox sortOperator_ReceiptCustomer;
	protected Listbox sortOperator_ReceiptPurpose;
	protected Listbox sortOperator_ReceiptReceiptMode;
	protected Listbox sortOperator_ReceiptAllocationType;
	protected Listbox sortOperator_ReceiptFinType;
	protected Listbox sortOperator_ReceiptFinBranch;

	protected int   oldVar_sortOperator_custCIF; 
	protected int   oldVar_sortOperator_finType;
	protected int   oldVar_sortOperator_finBranch;
	
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	private transient ReceiptService receiptService;
	private String module;

	/**
	 * The default constructor.
	 */
	public ReceiptEnquiryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {

		this.module = getArgument("module");
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			super.moduleCode = "FeeReceipt";
			super.tableName = "FinReceiptHeader_FEView";
			super.queueTableName = "FinReceiptHeader_FEView";
			super.enquiryTableName = "FinReceiptHeader_FEView";
		}else{
			super.moduleCode = "FinReceiptHeader";
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
	public void onCreate$window_ReceiptEnquiryList(Event event) {
		// Set the page level components.
		setPageComponents(window_ReceiptEnquiryList, borderLayout_ReceiptEnquiryList, listBoxReceipt, pagingReceiptList);
		setItemRender(new ReceiptRealizationListModelItemRenderer());
		registerButton(btnSearch);

		registerField("finCcy");
		registerField("receiptID");
		registerField("reference", listheader_ReceiptReference, SortOrder.ASC, receiptReference,
				sortOperator_ReceiptReference, Operators.STRING);
		registerField("custCIF", listheader_ReceiptCusomer, SortOrder.NONE, customer,
				sortOperator_ReceiptCustomer, Operators.STRING);
		fillComboBox(this.purpose, "", PennantStaticListUtil.getReceiptPurpose(), "");
		registerField("receiptPurpose", listheader_ReceiptPurpose, SortOrder.NONE, purpose,
				sortOperator_ReceiptPurpose, Operators.STRING);
		fillComboBox(this.receiptMode, "", PennantStaticListUtil.getReceiptModes(), "");
		registerField("receiptMode", listheader_ReceiptMode, SortOrder.NONE, receiptMode, sortOperator_ReceiptReceiptMode,
				Operators.STRING);
		registerField("receiptAmount", listheader_ReceiptAmount);
		fillComboBox(this.allocationType, "", PennantStaticListUtil.getAllocationMethods(), "");
		registerField("allocationType", listheader_ReceiptAllocattionType, SortOrder.NONE, allocationType,
				sortOperator_ReceiptAllocationType, Operators.STRING);
		registerField("finType", listheader_ReceiptFinType, SortOrder.NONE, finType,
				sortOperator_ReceiptFinType, Operators.STRING);
		registerField("finBranch", listheader_ReceiptFinBranch, SortOrder.NONE, finBranch, sortOperator_ReceiptFinBranch,
				Operators.STRING);
		registerField("ReceiptModeStatus", listheader_ReceiptStatus);

		// Render the page and display the data.
		doRenderPage();
		if(enqiryModule){
			this.workFlowFrom.setVisible(false);
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);

		}
		search();
	}
	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			this.searchObject.addWhereClause(" ReceiptPurpose = '"+FinanceConstants.FINSER_EVENT_FEEPAYMENT+"' AND RecordType IS NULL  ");
		}else{
			this.searchObject.addWhereClause(" ReceiptPurpose != '"+FinanceConstants.FINSER_EVENT_FEEPAYMENT+"' AND RecordType IS NULL ");
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
		this.sortOperator_ReceiptCustomer.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_ReceiptFinType.setSelectedIndex(0);
		this.finBranch.setValue("");
		this.sortOperator_ReceiptFinBranch.setSelectedIndex(0);
		this.listBoxReceipt.getItems().clear();
		this.oldVar_sortOperator_custCIF=0;
		this.oldVar_sortOperator_finType=0;
		this.oldVar_sortOperator_finBranch=0;
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
	public void onReceiptItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReceipt.getSelectedItem();

		// Get the selected entity.
		long receiptID = (long) selectedItem.getAttribute("id");
		
		boolean isFeePayment = false;
		String type = "_View";
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			isFeePayment = true;
			type = "_FEView";
		}
		FinReceiptHeader header = receiptService.getFinReceiptHeaderById(receiptID, isFeePayment, type);

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
		arg.put("module", module);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptEnquiryDialog.zul", null, arg);
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
					this.window_ReceiptEnquiryList, "FinanceType", this.finType.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finType.setValue(selectedValues);
			}

		}else{

			Object dataObject = ExtendedSearchListBox.show(this.window_ReceiptEnquiryList, "FinanceType");
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
					this.window_ReceiptEnquiryList, "Branch", this.finBranch.getValue(), new Filter[]{});
			if (selectedValues!= null) {
				this.finBranch.setValue(selectedValues);
			}

		}else{
			Object dataObject = ExtendedSearchListBox.show(this.window_ReceiptEnquiryList,"Branch");
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

	public void onSelect$sortOperator_ReceiptCustomer(Event event) {
		this.oldVar_sortOperator_custCIF = doChangeStringOperator(sortOperator_ReceiptCustomer, oldVar_sortOperator_custCIF, this.customer);
	}

	public void onSelect$sortOperator_finType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_ReceiptFinType, oldVar_sortOperator_finType, this.finType);
	}

	public void onSelect$sortOperator_finBranch(Event event) {
		this.oldVar_sortOperator_finBranch = doChangeStringOperator(sortOperator_ReceiptFinBranch, oldVar_sortOperator_finBranch, this.finBranch);
	}

	private int doChangeStringOperator(Listbox listbox,int oldOperator,Textbox textbox){

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

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

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}
}
