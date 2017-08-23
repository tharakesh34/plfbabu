package com.pennant.webui.financemanagement.bulkdeferment;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.service.finance.BulkDefermentChangeProcessService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.enquiry.model.BulkDefermentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

public class BulkDefermentChangeListCtrl extends GFCBaseListCtrl<BulkProcessHeader> {
	private static final long serialVersionUID = 9086034736503097868L;
	private static final Logger logger = Logger.getLogger(BulkDefermentChangeListCtrl.class);

	protected Window window_BulkRateChangeList; 
	protected Borderlayout borderLayout_BulkRateChangeList; 
	protected Paging pagingBulkRateChangeList; 
	protected Listbox listBoxBulkRateChange; 

	protected Listheader listheader_fromDate; 
	protected Listheader listheader_toDate; 
	protected Listheader listheader_newProcessedRate; 
	protected Listheader listheader_reCalculationType; 
	
	protected Button button_BulkRateChangeList_NewBulkRateChange; 
	protected Button button_BulkRateChangeList_BulkRateChangeSearchDialog; 

	protected Datebox fromDate; 
	protected Listbox sortOperator_fromDate; 
	protected Datebox toDate; 
	protected Listbox sortOperator_toDate; 
	protected Decimalbox newProcessedRate; 
	protected Listbox sortOperator_newProcessedRate; 
	protected Combobox reCalculationType; 
	protected Listbox sortOperator_reCalculationType; 
	protected Textbox bulkProcessFor;   

	protected Listheader listheader_reCalFromDate; 
	protected Listheader listheader_reCalToDate;  
	protected Listbox    sortOperator_reCalFromDate; 
	protected Datebox    reCalFromDate;              
	protected Listbox    sortOperator_reCalToDate;   
	protected Datebox    reCalToDate;                

	private Tabbox	tabbox;
	
	protected Textbox moduleType; 
	protected Row reCalTypeRow;
    protected Label label_BulkRateChangeSearch_newProcessedRate;

	// NEEDED for the ReUse in the SearchWindow
	private FinanceDetailService financeDetailService;
	private BulkDefermentChangeProcessService bulkDefermentChangeProcessService;
    private boolean isBulkDeferment=false;
   
    private List<ValueLabel> listRecalType =PennantStaticListUtil.getSchCalCodes();
	/**
	 * default constructor.<br>
	 */
	public BulkDefermentChangeListCtrl() {
		super();
	}

	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		searchObject.addFilterNotEqual("RecordType", "");
	}

	
	@Override
	protected void doSetProperties() {
		super.moduleCode = "BulkProcessHeader";
		super.pageRightName = "IjarahaBulkRateChangeList";
		super.tableName = "BulkProcessHeader_AView";
		super.queueTableName = "BulkProcessHeader_View";
		super.enquiryTableName = "BulkProcessHeader_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_BulkRateChangeList(Event event) throws Exception {
		logger.debug("Entering");

		if (event.getTarget().getParent().getParent().getParent().getParent() != null) {
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
			String menuItemName = tabbox.getSelectedTab().getId().trim().replace("tab_", "menu_Item_");
			String moduleName = menuItemName.trim().replace("tab_", "menu_Item_");
			if (moduleName.contentEquals("menu_Item_IjaraBulkRate")) {
				isBulkDeferment = false;
			} else if (moduleName.contentEquals("menu_Item_BulkDeferment")) {
				isBulkDeferment = true;
			}
		}

		// Set the page level components.
		setPageComponents(window_BulkRateChangeList, borderLayout_BulkRateChangeList, listBoxBulkRateChange,
				pagingBulkRateChangeList);
		setItemRender(new BulkDefermentListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BulkRateChangeList_NewBulkRateChange, "button_BulkRateChangeList_NewBulkRateChange", true);
		registerButton(button_BulkRateChangeList_BulkRateChangeSearchDialog);

		registerField("BulkProcessId", SortOrder.ASC);
		registerField("BulkProcessFor");
		registerField("NewProcessedRate");
		registerField("FromDate", listheader_fromDate, SortOrder.NONE, fromDate, sortOperator_fromDate,
				Operators.STRING);
		registerField("ToDate", listheader_toDate, SortOrder.NONE, toDate, sortOperator_toDate, Operators.STRING);
		registerField("ReCalFromDate", listheader_reCalFromDate, SortOrder.NONE, reCalFromDate,
				sortOperator_reCalFromDate, Operators.STRING);
		registerField("ReCalToDate", listheader_reCalToDate, SortOrder.NONE, reCalToDate, sortOperator_reCalToDate,
				Operators.STRING);
		fillComboBox(this.reCalculationType, "", listRecalType, ",STEPPOS,");
		registerField("ReCalType", listheader_reCalculationType, SortOrder.NONE, reCalculationType,
				sortOperator_reCalculationType, Operators.STRING);
		registerField("ExcludeDeferement");
		registerField("AddTermAfter");
		registerField("lovDescSqlQuery");

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
	public void onClick$button_BulkRateChangeList_BulkRateChangeSearchDialog(Event event) {
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
	public void onBulkDefermentChangeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBulkRateChange.getSelectedItem();

		long id = (long) selectedItem.getAttribute("id");
		String bulkProcessFor = (String) selectedItem.getAttribute("bulkProcessFor");

		BulkProcessHeader bulkProcessHeader = null;
		if ("R".equals(StringUtils.trimToEmpty(bulkProcessFor))) {
			bulkProcessHeader = bulkDefermentChangeProcessService.getBulkProcessHeaderById(
					id, "R");
		} else if ("D".equals(StringUtils.trimToEmpty(bulkProcessFor))) {
			bulkProcessHeader = bulkDefermentChangeProcessService.getBulkProcessHeaderById(
					id, "D");
		}

		if (bulkProcessHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BulkProcessID='" + bulkProcessHeader.getBulkProcessId() + "' AND version="
				+ bulkProcessHeader.getVersion() + " ";

		if (doCheckAuthority(bulkProcessHeader, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && bulkProcessHeader.getWorkflowId() == 0) {
				bulkProcessHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(bulkProcessHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_BulkRateChangeList_NewBulkRateChange(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		Map<String, Object> map = getDefaultArguments();
		map.put("bulkDefermentChangeListCtrl", this);
		BulkProcessHeader bulkProcessHeader = new BulkProcessHeader();
		bulkProcessHeader.setNewRecord(true);
		if("R".equals(this.bulkProcessFor.getValue())) {
			bulkProcessHeader.setBulkProcessFor("R");
		} else {
			bulkProcessHeader.setBulkProcessFor("D");
		}
		
		map.put("bulkProcessHeader", bulkProcessHeader);
		Executions.createComponents("/WEB-INF/pages/FinanceManagement/BulkDefermentChange/BulkDefermentChangeDialog.zul", null, map);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Build the Customer Dialog Window with Existing Core banking Data
	 * 
	 * @throws Exception
	 */
	public void buildDialogWindow(CustomerDetails customer,boolean newRecord) throws Exception{
		logger.debug("Entering");
		if (customer!=null) {
			if (newRecord) {
				// create a new Customer object, We GET it from the backEnd.
				//CustomerDetails aCustomerDetails = getCustomerDetailsService().getNewCustomer(false);
				//Customer customerlov = getCustomerDetailsService().fetchCustomerDetails(BulkRateChange.getBulkRateChange());
				//BulkRateChange.setBulkRateChange(BulkRateChangelov);
				//getBulkRateChangeDetailsService().setBulkRateChangeDetails(BulkRateChange);
			}
			//showDetailView(BulkRateChange);
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aBulkProcessHeader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BulkProcessHeader aBulkProcessHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("bulkProcessHeader", aBulkProcessHeader);
		arg.put("bulkDefermentChangeListCtrl", this);
		arg.put("enqiryModule", super.enqiryModule);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/BulkDefermentChange/BulkDefermentChangeDialog.zul", null, arg);
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

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public BulkDefermentChangeProcessService getBulkDefermentChangeProcessService() {
		return bulkDefermentChangeProcessService;
	}

	public void setBulkDefermentChangeProcessService(BulkDefermentChangeProcessService bulkDefermentChangeProcessService) {
		this.bulkDefermentChangeProcessService = bulkDefermentChangeProcessService;
	}
}