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
 * FileName    		:  CustomerListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customer;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.model.CustomerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerListCtrl extends GFCBaseListCtrl<Customer> implements Serializable {

	private static final long serialVersionUID = 9086034736503097868L;
	private final static Logger logger = Logger.getLogger(CustomerListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerList; // autowired
	protected Borderlayout borderLayout_CustomerList; // autowired
	protected Paging pagingCustomerList; // autowired
	protected Listbox listBoxCustomer; // autowired

	// List headers
	protected Listheader listheader_CustCIF; // autowired
	protected Listheader listheader_CustCoreBank; // autowired
	protected Listheader listheader_CustShrtName; // autowired
	protected Listheader listheader_CustDftBranch; // autowired
	protected Listheader listheader_CustCtgCode; // autowired
	protected Listheader listheader_CustTypeCode; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CustomerList_NewCustomer; // autowired
	protected Button button_CustomerList_CustomerSearchDialog; // autowired
	protected Button button_CustomerList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow

	protected Textbox custCIF; // autowired
	protected Listbox sortOperator_custCIF; // autowired
	protected Textbox custCoreBank; // autowired
	protected Listbox sortOperator_custCoreBank; // autowired
	protected Combobox custCtgCode; // autowired
	protected Listbox sortOperator_custCtgCode; // autowired
	protected Textbox custTypeCode; // autowired
	protected Listbox sortOperator_custTypeCode; // autowired
	protected Textbox custShrtName; // autowired
	protected Listbox sortOperator_custShrtName; // autowired
	protected Textbox custDftBranch; // autowired
	protected Listbox sortOperator_custDftBranch; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType; // autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired

	protected Grid searchGrid; // autowired
	protected Textbox moduleType; // autowired
	protected Radio fromApproved;
	protected Radio fromWorkFlow;
	protected Row workFlowFrom;
	protected Row row_AlwWorkflow;
	private transient boolean approvedList = false;

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerDetailsService customerDetailsService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerListCtrl() {
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
	public void onCreate$window_CustomerList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Customer");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Customer");

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

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCoreBank.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCoreBank.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custCtgCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCtgCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custShrtName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custShrtName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custDftBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custDftBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.row_AlwWorkflow.setVisible(false);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerList.setHeight(getBorderLayoutHeight());
		this.listBoxCustomer.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCustomerList.setPageSize(getListRows());
		this.pagingCustomerList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("custCIF",	true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("custCIF", false));
		this.listheader_CustCoreBank.setSortAscending(new FieldComparator("custCoreBank", true));
		this.listheader_CustCoreBank.setSortDescending(new FieldComparator("custCoreBank", false));
		this.listheader_CustShrtName.setSortAscending(new FieldComparator("custShrtName", true));
		this.listheader_CustShrtName.setSortDescending(new FieldComparator("custShrtName", false));
		this.listheader_CustDftBranch.setSortAscending(new FieldComparator("custDftBranch", true));
		this.listheader_CustDftBranch.setSortDescending(new FieldComparator("custDftBranch", false));
		this.listheader_CustCtgCode.setSortAscending(new FieldComparator("custCtgCode", true));
		this.listheader_CustCtgCode.setSortDescending(new FieldComparator("custCtgCode", false));
		this.listheader_CustTypeCode.setSortAscending(new FieldComparator("custTypeCode", true));
		this.listheader_CustTypeCode.setSortDescending(new FieldComparator("custTypeCode", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
      
		fillComboBox(this.custCtgCode, "", PennantStaticListUtil.getCustCtgType(), "");

		// set the itemRenderer
		this.listBoxCustomer.setItemRenderer(new CustomerListModelItemRenderer());
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CustomerList_NewCustomer.setVisible(true);
			} else {
				button_CustomerList_NewCustomer.setVisible(false);
			}
		}

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerList_NewCustomer.setVisible(false);
			this.button_CustomerList_CustomerSearchDialog.setVisible(false);
			this.button_CustomerList_PrintList.setVisible(false);
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
		getUserWorkspace().alocateAuthorities("CustomerList");
		this.button_CustomerList_NewCustomer.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerList_NewCustomer"));
		this.button_CustomerList_CustomerSearchDialog.setVisible(true);
		this.button_CustomerList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerList_PrintList"));
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
	public void onCustomerItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Customer object
		final Listitem item = this.listBoxCustomer.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			Customer aCustomer = (Customer) item.getAttribute("data");
			final CustomerDetails customerDetails = getCustomerDetailsService().getCustomerById(aCustomer.getId());

			if (customerDetails == null) {
				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aCustomer.getCustCIF();
				valueParm[1] = aCustomer.getCustCtgCode();

				errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CustCtgCode")+ ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());

			} else {
				String whereCond = " AND CustID='" + aCustomer.getCustID()
						+ "' AND version=" + aCustomer.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Customer", whereCond, aCustomer.getTaskId(),aCustomer.getNextTaskId());
					if (userAcces) {
						showDetailView(customerDetails);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(customerDetails);
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
	public void onClick$button_CustomerList_NewCustomer(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerListCtrl", this);

		Executions.createComponents(
						"/WEB-INF/pages/CustomerMasters/Customer/CoreCustomerSelect.zul",
						null, map);
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
				Customer customerlov = getCustomerDetailsService().fetchCustomerDetails(customer.getCustomer());
				customer.setCustomer(customerlov);
				getCustomerDetailsService().setCustomerDetails(customer);
			}
			showDetailView(customer);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param CustomerDetails
	 *            (aCustomerDetails)
	 * @throws Exception
	 */
	private void showDetailView(CustomerDetails aCustomerDetails) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		Customer aCustomer = aCustomerDetails.getCustomer();
		if (aCustomer.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aCustomerDetails.setCustomer(aCustomer);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerDetails", aCustomerDetails);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CustomerListbox from the
		 * dialog when we do a delete, edit or insert a Customer.
		 */
		map.put("customerListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog.zul",
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
		PTMessageUtils.showHelpWindow(event, window_CustomerList);
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
		this.sortOperator_custCIF.setSelectedIndex(0);
		this.custCIF.setValue("");
		this.sortOperator_custCoreBank.setSelectedIndex(0);
		this.custCoreBank.setValue("");
		this.sortOperator_custCtgCode.setSelectedIndex(0);
		this.custCtgCode.setSelectedIndex(0);
		this.sortOperator_custDftBranch.setSelectedIndex(0);
		this.custDftBranch.setValue("");
		this.sortOperator_custShrtName.setSelectedIndex(0);
		this.custShrtName.setValue("");
		this.sortOperator_custTypeCode.setSelectedIndex(0);
		this.custTypeCode.setValue("");

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
	 * Method for call the Customer dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerList_CustomerSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customer print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Customer",
				getSearchObj(), this.pagingCustomerList.getTotalSize() + 1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering"); 
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<Customer>(Customer.class,	getListRows());
		this.searchObj.addSort("CustID", false);
		addFieldsToSearchObject();		
		this.searchObj.addTabelName("Customers_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_CustomerList_NewCustomer.setVisible(true);
			} else {
				button_CustomerList_NewCustomer.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("Customers_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("Customers_AView");
		}

		// Customer CIF
		if (!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custCIF.getSelectedItem(),this.custCIF.getValue(), "custCIF");
		}
		// Customer Core Bank
		if (!StringUtils.trimToEmpty(this.custCoreBank.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custCoreBank.getSelectedItem(),this.custCoreBank.getValue(), "custCoreBank");
		}
		// Customer Category Code
		if (!this.custCtgCode.getSelectedItem().getValue().toString().equals("#")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custCtgCode.getSelectedItem(),this.custCtgCode.getSelectedItem().getValue().toString(), "custCtgCode");
		}
		// Customer Default Branch
		if (!StringUtils.trimToEmpty(this.custDftBranch.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custDftBranch.getSelectedItem(),this.custDftBranch.getValue(), "custDftBranch");
		}
		// Customer Short Name
		if (!StringUtils.trimToEmpty(this.custShrtName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custShrtName.getSelectedItem(),this.custShrtName.getValue(), "custShrtName");
		}
		// Customer Type Code
		if (!StringUtils.trimToEmpty(this.custTypeCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custTypeCode.getSelectedItem(),this.custTypeCode.getValue(), "custTypeCode");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxCustomer,this.pagingCustomerList);

		logger.debug("Leaving");

	}
	public void addFieldsToSearchObject(){
		
		this.searchObj.addField("CustID");
	    this.searchObj.addField("CustCIF");
		this.searchObj.addField("CustCoreBank");
		this.searchObj.addField("CustShrtName");
		this.searchObj.addField("CustCtgCode");
		this.searchObj.addField("CustDftBranch");
		this.searchObj.addField("CustTypeCode");
		
		// LOV values
		this.searchObj.addField("LovDescCustCtgCodeName");
		this.searchObj.addField("LovDescCustTypeCodeName");
		
		this.searchObj.addField("workflowId");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public JdbcSearchObject<Customer> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Customer> searchObj) {
		this.searchObj = searchObj;
	}
}