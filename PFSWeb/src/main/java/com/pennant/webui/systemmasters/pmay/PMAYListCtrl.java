package com.pennant.webui.systemmasters.pmay;

import java.util.Map;

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

import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.systemmasters.PMAYService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.systemmasters.pmay.model.PMAYListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class PMAYListCtrl extends GFCBaseListCtrl<PMAY> {
	private static final long serialVersionUID = 485796535935527728L;

	protected Window window_PmayList;
	protected Borderlayout borderLayout_PmayList;
	protected Paging pagingPmayList;
	protected Listbox listBoxPmay;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_CIF;
	protected Listheader listheader_CustomerName;
	protected Listheader listheader_ApplicationID;

	// checkRights
	protected Button button_PmayList_NewPmay;
	protected Button button_PmayList_PmaySearchDialog;
	protected Button btnSearchCustCIF;

	// Search Fields
	protected Textbox finReference;
	protected Uppercasebox cif;
	protected Textbox customerName;
	protected Textbox applicationID;

	protected Listbox sortOperator_finReference;
	protected Listbox sortOperator_cif;
	protected Listbox sortOperator_customerName;
	protected Listbox sortOperator_applicationID;

	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private transient PMAYService pmayService;
	private FinanceDetailService financeDetailService;
	String moduleDefiner = "";
	private String enqModule;

	/**
	 * default constructor.<br>
	 */
	public PMAYListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PMAY";
		super.pageRightName = "PMAYList";
		super.tableName = "PMAY_AView";
		super.queueTableName = "PMAY_LView";
		super.enquiryTableName = "PMAY_View";

		this.enqModule = getArgument("module");
		if ("E".equals(this.enqModule)) {
			enqiryModule = true;
			super.enquiryTableName = "PMAY_View";
			super.tableName = "PMAY_View";
			super.queueTableName = "PMAY_View";
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PmayList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_PmayList, borderLayout_PmayList, listBoxPmay, pagingPmayList);
		setItemRender(new PMAYListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PmayList_PmaySearchDialog);
		registerButton(button_PmayList_NewPmay, "button_PmayList_NewPmay", true);

		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("custCif", listheader_CIF, SortOrder.ASC, cif, sortOperator_cif, Operators.SIMPLESTRING);
		registerField("custShrtName", listheader_CustomerName, SortOrder.NONE, customerName, sortOperator_customerName,
				Operators.STRING);

		// Render the page and display the data.
		doSetFieldProperties();
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PmayList_PmaySearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PmayList_NewPmay(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		PMAY pmay = new PMAY();
		pmay.setNewRecord(true);
		pmay.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(pmay);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onPMAYItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// Get the selected record.
		Listitem selectedItem = this.listBoxPmay.getSelectedItem();
		long finID = (Long) selectedItem.getAttribute("finID");
		PMAY pmay = pmayService.getPMAY(finID, "_View");

		if (pmay == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append(" finReference = ");
		whereCond.append(pmay.getFinReference());
		whereCond.append(" AND  version=");
		whereCond.append(pmay.getVersion());

		if (doCheckAuthority(pmay, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && pmay.getWorkflowId() == 0) {
				pmay.setWorkflowId(getWorkFlowId());
			}
			pmay.setNewRecord(false);
			doShowDialogPage(pmay);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private FinanceDetail getFinanceDetails(long finID) {
		FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailsForPmay(finID);
		return financeDetail;
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param pmay The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PMAY pmay) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("pmay", pmay);
		arg.put("pmayListCtrl", this);
		arg.put("eventCode", FinanceConstants.PMAY);
		arg.put("enqiryModule", enqiryModule);

		try {
			if (pmay.isNewRecord()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/SelectFinance/SelectFinReferenceDialog.zul", null, arg);
			} else {
				arg.put("financeDetail", getFinanceDetails(pmay.getFinID()));
				Executions.createComponents("/WEB-INF/pages/SystemMaster/PMAY/PMAYDialog.zul", null, arg);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
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

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSearchCustomerCIF();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);
		this.cif.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.cif.setValue(customer.getCustCIF());
		} else {
			this.cif.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	public PMAYService getPmayService() {
		return pmayService;
	}

	public void setPmayService(PMAYService pmayService) {
		this.pmayService = pmayService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}
