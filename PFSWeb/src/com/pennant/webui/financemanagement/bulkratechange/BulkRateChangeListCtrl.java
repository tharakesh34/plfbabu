package com.pennant.webui.financemanagement.bulkratechange;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.service.finance.BulkRateChangeProcessService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.enquiry.model.BulkRateChangeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

public class BulkRateChangeListCtrl extends GFCBaseListCtrl<BulkProcessHeader> implements Serializable {

	private static final long serialVersionUID = 9086034736503097868L;
	private final static Logger logger = Logger.getLogger(BulkRateChangeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_BulkRateChangeList; // autowired
	protected Borderlayout borderLayout_BulkRateChangeList; // autowired
	protected Paging pagingBulkRateChangeList; // autowired
	protected Listbox listBoxBulkRateChange; // autowired

	// List headers
	protected Listheader listheader_fromDate; // autowired
	protected Listheader listheader_toDate; // autowired
	protected Listheader listheader_newProcessedRate; // autowired
	protected Listheader listheader_reCalculationType; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;
	
	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_BulkRateChangeList_NewBulkRateChange; // autowired
	protected Button button_BulkRateChangeList_BulkRateChangeSearchDialog; // autowired
	protected Button button_BulkRateChangeList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected Textbox fromDate; // autowired
	protected Listbox sortOperator_fromDate; // autowired
	protected Textbox toDate; // autowired
	protected Listbox sortOperator_toDate; // autowired
	protected Textbox newProcessedRate; // autowired
	protected Listbox sortOperator_newProcessedRate; // autowired
	protected Textbox reCalculationType; // autowired
	protected Listbox sortOperator_reCalculationType; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType; // autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	protected Textbox bulkProcessFor;   // autowired

	// For Bulk Deferment
	protected Listheader listheader_reCalFromDate; // autowired
	protected Listheader listheader_reCalToDate;  // autowired
	protected Listbox    sortOperator_reCalFromDate; // autowired
	protected Datebox    reCalFromDate;              // autowired
	protected Listbox    sortOperator_reCalToDate;   // autowired
	protected Datebox    reCalToDate;                // autowired

	private Tabbox	tabbox;
	
	protected Grid searchGrid; // autowired
	protected Textbox moduleType; // autowired
	protected Radio fromApproved;
	protected Radio fromWorkFlow;
	protected Row workFlowFrom;
	protected Row reCalTypeRow;
    protected Label label_BulkRateChangeSearch_newProcessedRate;
    protected Row row_AlwWorkflow;
	private transient boolean approvedList = false;

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<BulkProcessHeader> searchObj;
	private FinanceDetailService financeDetailService;
	private BulkRateChangeProcessService bulkRateChangeProcessService;
	private transient WorkFlowDetails workFlowDetails = null;
    private boolean isBulkDeferment=false;
	/**
	 * default constructor.<br>
	 */
	public BulkRateChangeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BulkRateChangeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("BulkProcessHeader");
		boolean wfAvailable = true;

		if (event.getTarget().getParent().getParent().getParent().getParent() != null) {
			tabbox = (Tabbox)event.getTarget().getParent().getParent().getParent().getParent();
			String menuItemName=tabbox.getSelectedTab().getId().trim().replace("tab_", "menu_Item_");
			String moduleName = menuItemName.trim().replace("tab_", "menu_Item_");
			if(moduleName.contentEquals("menu_Item_IjaraBulkRate")) {
				isBulkDeferment = false;
			} else if(moduleName.contentEquals("menu_Item_BulkDeferment")){
				isBulkDeferment = true;
			}
		}
		
		
		
		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BulkProcessHeader");
			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_fromDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_fromDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_toDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_toDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_reCalculationType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_reCalculationType.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if(isBulkDeferment){
			this.reCalTypeRow.setVisible(true);
			this.sortOperator_reCalFromDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_reCalFromDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_reCalToDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_reCalToDate.setItemRenderer(new SearchOperatorListModelItemRenderer());
		}
		
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.row_AlwWorkflow.setVisible(false);
			
		}

		 // set components visible dependent of the users rights 
		   doCheckRights(); 

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_BulkRateChangeList.setHeight(getBorderLayoutHeight());
		this.listBoxBulkRateChange.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingBulkRateChangeList.setPageSize(getListRows());
		this.pagingBulkRateChangeList.setDetailed(true);

		this.listheader_fromDate.setSortAscending(new FieldComparator("fromDate",	true));
		this.listheader_fromDate.setSortDescending(new FieldComparator("fromDate", false));
		this.listheader_toDate.setSortAscending(new FieldComparator("toDate", true));
		this.listheader_toDate.setSortDescending(new FieldComparator("toDate", false));
		
		if(!isBulkDeferment){
			this.sortOperator_newProcessedRate.setVisible(true);
			this.label_BulkRateChangeSearch_newProcessedRate.setVisible(true);
			this.newProcessedRate.setVisible(true);
			this.sortOperator_newProcessedRate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_newProcessedRate.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.listheader_newProcessedRate.setVisible(true);
			this.listheader_newProcessedRate.setSortAscending(new FieldComparator("newProcessedRate", true));
			this.listheader_newProcessedRate.setSortDescending(new FieldComparator("newProcessedRate", false));
		}
		
		this.listheader_reCalculationType.setSortAscending(new FieldComparator("reCalType", true));
		this.listheader_reCalculationType.setSortDescending(new FieldComparator("reCalType", false));

		if(isBulkDeferment){
			this.reCalTypeRow.setVisible(true);
			//this.listheader_reCalFromDate.setSortAscending(new FieldComparator("reCalFromdate", true));
			//this.listheader_reCalFromDate.setSortAscending(new FieldComparator("reCalFromdate", false));
			//this.listheader_reCalToDate.setSortAscending(new FieldComparator("reCalTodate", true));
			//this.listheader_reCalToDate.setSortAscending(new FieldComparator("reCalTodate", false));
		}
		
		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		// set the itemRenderer
		this.listBoxBulkRateChange.setItemRenderer(new BulkRateChangeListModelItemRenderer());
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_BulkRateChangeList_NewBulkRateChange.setVisible(true);
			} else {
				button_BulkRateChangeList_NewBulkRateChange.setVisible(false);
			}
		}

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_BulkRateChangeList_NewBulkRateChange.setVisible(false);
			this.button_BulkRateChangeList_BulkRateChangeSearchDialog.setVisible(false);
			this.button_BulkRateChangeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("IjarahaBulkRateChangeList");
		this.button_BulkRateChangeList_NewBulkRateChange.setVisible(getUserWorkspace()
				.isAllowed("button_IjarahaBulkRateChangeList_New"));
		this.button_BulkRateChangeList_BulkRateChangeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_IjarahaBulkRateChangeList_IjarahaBulkRateChangeDialog"));
		this.button_BulkRateChangeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_IjarahaBulkRateChangeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customer.model.
	 * CustomerListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onBulkRateChangeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Customer object
		final Listitem item = this.listBoxBulkRateChange.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			BulkProcessHeader aBulkProcessHeader = (BulkProcessHeader) item.getAttribute("data");
			 BulkProcessHeader bulkProcessHeader = null;
			 if(StringUtils.trimToEmpty(aBulkProcessHeader.getBulkProcessFor()).equals("R")){
				 bulkProcessHeader = getBulkRateChangeProcessService().getBulkProcessHeaderById(aBulkProcessHeader.getBulkProcessId(), "R");
			 } else if(StringUtils.trimToEmpty(aBulkProcessHeader.getBulkProcessFor()).equals("D")){
				 bulkProcessHeader = getBulkRateChangeProcessService().getBulkProcessHeaderById(aBulkProcessHeader.getBulkProcessId(), "D");
			 }
			
			if (bulkProcessHeader == null) {
				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = String.valueOf(aBulkProcessHeader.getFromDate());
				valueParm[1] = String.valueOf(aBulkProcessHeader.getToDate());

				errParm[0] = PennantJavaUtil.getLabel("label_FromDate") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_ToDate")+ ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());

			} else {
				String whereCond = " AND BulkProcessID='" + aBulkProcessHeader.getBulkProcessId()
						+ "' AND version=" + aBulkProcessHeader.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"BulkProcessHeader", whereCond, aBulkProcessHeader.getTaskId(),aBulkProcessHeader.getNextTaskId());
					if (userAcces) {
						showDetailView(bulkProcessHeader);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(bulkProcessHeader);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BulkRateChangeList_NewBulkRateChange(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("bulkRateChangeListCtrl", this);
		BulkProcessHeader bulkProcessHeader = new BulkProcessHeader();
		bulkProcessHeader.setNewRecord(true);
		if(this.bulkProcessFor.getValue().equals("R")) {
			bulkProcessHeader.setBulkProcessFor("R");
		} else {
			bulkProcessHeader.setBulkProcessFor("D");
		}
		
		map.put("bulkProcessHeader", bulkProcessHeader);
		Executions.createComponents("/WEB-INF/pages/FinanceManagement/BulkRateChange/BulkRateChangeDialog.zul", null, map);
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
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param BulkRateChangeDetails
	 *            (aBulkRateChangeDetails)
	 * @throws Exception
	 */
	private void showDetailView(BulkProcessHeader aBulkProcessHeader) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
			if (aBulkProcessHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
				aBulkProcessHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
			}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("bulkProcessHeader", aBulkProcessHeader);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the BulkRateChangeListbox from the
		 * dialog when we do a delete, edit or insert a BulkRateChange.
		 */
		map.put("bulkRateChangeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/BulkRateChange/BulkRateChangeDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_BulkRateChangeList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.sortOperator_fromDate.setSelectedIndex(0);
		this.fromDate.setValue("");
		this.sortOperator_toDate.setSelectedIndex(0);
		this.toDate.setValue("");
		if(this.sortOperator_newProcessedRate.isVisible()){
		   this.sortOperator_newProcessedRate.setSelectedIndex(0);
		   this.newProcessedRate.setValue("");
		}
		if(this.sortOperator_reCalculationType.isVisible()){
	     	this.sortOperator_reCalculationType.setSelectedIndex(0);
		    this.reCalculationType.setValue("");
		}
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the BulkRateChange dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BulkRateChangeList_BulkRateChangeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the BulkRateChange print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_BulkRateChangeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("BulkRateChange",
				getSearchObj(), this.pagingBulkRateChangeList.getTotalSize() + 1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering"); 
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<BulkProcessHeader>(BulkProcessHeader.class,	getListRows());
		this.searchObj.addSort("BulkProcessId", false);
		addFieldsToSearchObject();		
		this.searchObj.addTabelName("BulkProcessHeader_View"); 
		this.searchObj.addFilterNotEqual("RecordType", "");
		
		// Workflow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_BulkRateChangeList_NewBulkRateChange.setVisible(true);
			} else {
				button_BulkRateChangeList_NewBulkRateChange.setVisible(false);
			}
			
			if (!StringUtils.trimToEmpty(this.bulkProcessFor.getValue()).equals("")) {
				this.searchObj.addFilterEqual("BulkProcessFor", this.bulkProcessFor.getValue());
			  }
			
			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("BulkProcessHeader_View"); //
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("BulkProcessHeader_AView"); // _AView
		}
		

		
		
		// BulkRateChange CIF
		if (!StringUtils.trimToEmpty(this.fromDate.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_fromDate.getSelectedItem(),this.fromDate.getValue(), "fromDate");
		}
		// BulkRateChange Core Bank
		if (!StringUtils.trimToEmpty(this.toDate.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_toDate.getSelectedItem(),this.toDate.getValue(), "toDate");
		}
		// BulkRateChange Category Code
		if (!StringUtils.trimToEmpty(this.newProcessedRate.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_newProcessedRate.getSelectedItem(),this.newProcessedRate.getValue(), "newProcessedRate");
		}
		// BulkRateChange Default Branch
		if (!StringUtils.trimToEmpty(this.reCalculationType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_reCalculationType.getSelectedItem(),this.reCalculationType.getValue(), "reCalType");
		}
		
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxBulkRateChange,this.pagingBulkRateChangeList);

		logger.debug("Leaving");

	}
	public void addFieldsToSearchObject(){
		
		this.searchObj.addField("BulkProcessId");
		this.searchObj.addField("BulkProcessFor");
		this.searchObj.addField("FromDate");
	    this.searchObj.addField("ToDate");
		this.searchObj.addField("NewProcessedRate");
		this.searchObj.addField("ReCalType");
		this.searchObj.addField("ReCalFromDate");
		this.searchObj.addField("ReCalToDate");
		this.searchObj.addField("ExcludeDeferement");
		this.searchObj.addField("AddTermAfter");
		this.searchObj.addField("lovDescSqlQuery");
		
		this.searchObj.addField("Version");
		this.searchObj.addField("LastMntBy");
		this.searchObj.addField("LastMntOn");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType"); 
		
		this.searchObj.addField("RoleCode");
		this.searchObj.addField("NextRoleCode");
		this.searchObj.addField("TaskId");
		this.searchObj.addField("NextTaskId");
		this.searchObj.addField("workflowId");

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<BulkProcessHeader> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<BulkProcessHeader> searchObj) {
		this.searchObj = searchObj;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public BulkRateChangeProcessService getBulkRateChangeProcessService() {
		return bulkRateChangeProcessService;
	}

	public void setBulkRateChangeProcessService(BulkRateChangeProcessService bulkRateChangeProcessService) {
		this.bulkRateChangeProcessService = bulkRateChangeProcessService;
	}
}