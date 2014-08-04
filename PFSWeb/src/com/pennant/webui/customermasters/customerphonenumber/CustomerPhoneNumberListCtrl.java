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
 * FileName    		:  CustomerPhoneNumberListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerphonenumber;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customerphonenumber.model.CustomerPhoneNumberListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerPhoneNumber
 * /CustomerPhoneNumberList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerPhoneNumberListCtrl extends GFCBaseListCtrl<CustomerPhoneNumber> implements Serializable {

	private static final long serialVersionUID = 5073003999430539385L;
	private final static Logger logger = Logger.getLogger(CustomerPhoneNumberListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerPhoneNumberList; // autowired
	protected Borderlayout borderLayout_CustomerPhoneNumberList; // autowired
	protected Paging pagingCustomerPhoneNumberList; // autowired
	protected Listbox listBoxCustomerPhoneNumber; // autowired

	// List headers
	protected Listheader listheader_CustCIF; // autowired
	protected Listheader listheader_PhoneTypeCode; // autowired
	protected Listheader listheader_PhoneCountryCode; // autowired
	protected Listheader listheader_PhoneAreaCode; // autowired
	protected Listheader listheader_PhoneNumber; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// Searching Fields
	protected Textbox phoneCustCIF; // autowired
	protected Listbox sortOperator_phoneCustCIF; // autowired
	protected Textbox phoneTypeCode; // autowired
	protected Listbox sortOperator_phoneTypeCode; // autowired
	protected Textbox phoneCountryCode; // autowired
	protected Listbox sortOperator_phoneCountryCode; // autowired
	protected Textbox phoneAreaCode; // autowired
	protected Listbox sortOperator_phoneAreaCode; // autowired
	protected Textbox phoneNumber; // autowired
	protected Listbox sortOperator_phoneNumber; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType; // autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired

	protected Grid searchGrid; // autowired
	protected Textbox moduleType; // autowired
	protected Radio fromApproved;
	protected Radio fromWorkFlow;
	protected Row workFlowFrom;

	private transient boolean approvedList = false;

	protected Label label_CustomerPhoneNumberSearch_RecordStatus; // autowired
	protected Label label_CustomerPhoneNumberSearch_RecordType; // autowired
	protected Label label_CustomerPhoneNumberSearchResult; // autowired

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CustomerPhoneNumberList_NewCustomerPhoneNumber; // autowired
	protected Button button_CustomerPhoneNumberList_CustomerPhoneNumberSearchDialog; // autowired
	protected Button button_CustomerPhoneNumberList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerPhoneNumber> searchObj;
	private transient CustomerPhoneNumberService customerPhoneNumberService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerPhoneNumberListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerPhoneNumber
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerPhoneNumberList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerPhoneNumber");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerPhoneNumber");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_phoneCustCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_phoneCustCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_phoneTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_phoneTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_phoneCountryCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_phoneCountryCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_phoneAreaCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_phoneAreaCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_phoneNumber.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_phoneNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerPhoneNumberSearch_RecordStatus.setVisible(false);
			this.label_CustomerPhoneNumberSearch_RecordType.setVisible(false);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerPhoneNumberList.setHeight(getBorderLayoutHeight());
		this.listBoxCustomerPhoneNumber.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCustomerPhoneNumberList.setPageSize(getListRows());
		this.pagingCustomerPhoneNumberList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		this.listheader_PhoneTypeCode.setSortAscending(new FieldComparator("phoneTypeCode", true));
		this.listheader_PhoneTypeCode.setSortDescending(new FieldComparator("phoneTypeCode", false));
		this.listheader_PhoneCountryCode.setSortAscending(new FieldComparator("phoneCountryCode", true));
		this.listheader_PhoneCountryCode.setSortDescending(new FieldComparator("phoneCountryCode", false));
		this.listheader_PhoneAreaCode.setSortAscending(new FieldComparator("phoneAreaCode", true));
		this.listheader_PhoneAreaCode.setSortDescending(new FieldComparator("phoneAreaCode", false));
		this.listheader_PhoneNumber.setSortAscending(new FieldComparator("phoneNumber", true));
		this.listheader_PhoneNumber.setSortDescending(new FieldComparator("phoneNumber", false));

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
		this.listBoxCustomerPhoneNumber.setItemRenderer(new CustomerPhoneNumberListModelItemRenderer());

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerPhoneNumberList_NewCustomerPhoneNumber.setVisible(false);
			this.button_CustomerPhoneNumberList_CustomerPhoneNumberSearchDialog.setVisible(false);
			this.button_CustomerPhoneNumberList_PrintList.setVisible(false);
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
		getUserWorkspace().alocateAuthorities("CustomerPhoneNumberList");

		this.button_CustomerPhoneNumberList_NewCustomerPhoneNumber.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerPhoneNumberList_NewCustomerPhoneNumber"));
		this.button_CustomerPhoneNumberList_CustomerPhoneNumberSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerPhoneNumberList_CustomerPhoneNumberFindDialog"));
		this.button_CustomerPhoneNumberList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerPhoneNumberList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customerphonenumber.model.
	 * CustomerPhoneNumberListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerPhoneNumberItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerPhoneNumber object
		final Listitem item = this.listBoxCustomerPhoneNumber.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerPhoneNumber aCustomerPhoneNumber = (CustomerPhoneNumber) item.getAttribute("data");
			final CustomerPhoneNumber customerPhoneNumber = getCustomerPhoneNumberService().getCustomerPhoneNumberById(aCustomerPhoneNumber.getId(),
							aCustomerPhoneNumber.getPhoneTypeCode());
			if (customerPhoneNumber == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = String.valueOf(aCustomerPhoneNumber.getPhoneCustID());
				valueParm[1] = aCustomerPhoneNumber.getPhoneTypeCode();

				errParm[0] = PennantJavaUtil.getLabel("label_PhoneCustID")+ ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_PhoneTypeCode")+ ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				customerPhoneNumber.setLovDescPhoneTypeCodeName(aCustomerPhoneNumber.getLovDescPhoneTypeCodeName());
				String whereCond = " AND PhoneCustID='" + customerPhoneNumber.getPhoneCustID() + "' AND PhoneTypeCode='"
						+ customerPhoneNumber.getPhoneTypeCode() + "' AND version=" + customerPhoneNumber.getVersion()+ " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"CustomerPhoneNumber", whereCond,customerPhoneNumber.getTaskId(),customerPhoneNumber.getNextTaskId());
					if (userAcces) {
						showDetailView(customerPhoneNumber);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(customerPhoneNumber);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerPhoneNumber dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerPhoneNumberList_NewCustomerPhoneNumber(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerPhoneNumber object, We GET it from the backEnd.
		final CustomerPhoneNumber aCustomerPhoneNumber = getCustomerPhoneNumberService().getNewCustomerPhoneNumber();
		showDetailView(aCustomerPhoneNumber);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CustomerPhoneNumber
	 *            (aCustomerPhoneNumber)
	 * @throws Exception
	 */
	private void showDetailView(CustomerPhoneNumber aCustomerPhoneNumber)
			throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aCustomerPhoneNumber.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomerPhoneNumber.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerPhoneNumber", aCustomerPhoneNumber);
		map.put("customerPhoneNumberListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/CustomerPhoneNumberDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_CustomerPhoneNumberList);
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

		this.sortOperator_phoneCustCIF.setSelectedIndex(0);
		this.phoneCustCIF.setValue("");
		this.sortOperator_phoneAreaCode.setSelectedIndex(0);
		this.phoneAreaCode.setValue("");
		this.sortOperator_phoneCountryCode.setSelectedIndex(0);
		this.phoneCountryCode.setValue("");
		this.sortOperator_phoneTypeCode.setSelectedIndex(0);
		this.phoneTypeCode.setValue("");
		this.sortOperator_phoneNumber.setSelectedIndex(0);
		this.phoneNumber.setValue("");

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();

		logger.debug("Leaving");

	}

	/**
	 * call the CustomerPhoneNumber dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerPhoneNumberList_CustomerPhoneNumberSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerPhoneNumber print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerPhoneNumberList_PrintList(Event event)
			throws InterruptedException {
		logger.debug("Entering" + event.toString());
		// PTReportUtils.getReport("CustomerPhoneNumber", getSearchObj());
		PTListReportUtils reportUtils = new PTListReportUtils(
				"CustomerPhoneNumber", getSearchObj(),
				this.pagingCustomerPhoneNumberList.getTotalSize() + 1);

		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {

		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerPhoneNumber>(
				CustomerPhoneNumber.class, getListRows());
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addSort("lovDescCustCIF", false);
		this.searchObj.addTabelName("CustomerPhoneNumbers_View");

		// Workflow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_CustomerPhoneNumberList_NewCustomerPhoneNumber.setVisible(true);
			} else {
				button_CustomerPhoneNumberList_NewCustomerPhoneNumber.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("CustomerPhoneNumbers_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("CustomerPhoneNumbers_AView");
		}

		// Customer CIF
		if (!StringUtils.trimToEmpty(this.phoneCustCIF.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_phoneCustCIF.getSelectedItem(),this.phoneCustCIF.getValue(), "lovDescCustCIF");
		}
		// Customer Phone Type
		if (!StringUtils.trimToEmpty(this.phoneTypeCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_phoneTypeCode.getSelectedItem(),this.phoneTypeCode.getValue(), "phoneTypeCode");
		}
		// Customer Phone Area Code
		if (!StringUtils.trimToEmpty(this.phoneAreaCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_phoneAreaCode.getSelectedItem(),this.phoneAreaCode.getValue(), "phoneAreaCode");
		}
		// Customer Phone Country Code
		if (!StringUtils.trimToEmpty(this.phoneCountryCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_phoneCountryCode.getSelectedItem(),this.phoneCountryCode.getValue(), "phoneCountryCode");
		}
		// Customer Phone Number
		if (!StringUtils.trimToEmpty(this.phoneNumber.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_phoneNumber.getSelectedItem(),this.phoneNumber.getValue(), "phoneNumber");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxCustomerPhoneNumber,this.pagingCustomerPhoneNumberList);

		logger.debug("Leaving");

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerPhoneNumberService(
			CustomerPhoneNumberService customerPhoneNumberService) {
		this.customerPhoneNumberService = customerPhoneNumberService;
	}
	public CustomerPhoneNumberService getCustomerPhoneNumberService() {
		return this.customerPhoneNumberService;
	}

	public JdbcSearchObject<CustomerPhoneNumber> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerPhoneNumber> searchObj) {
		this.searchObj = searchObj;
	}

}