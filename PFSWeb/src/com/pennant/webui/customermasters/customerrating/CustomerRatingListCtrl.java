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
 * FileName    		:  CustomerRatingListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerrating;

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
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.service.customermasters.CustomerRatingService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customerrating.model.CustomerRatingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerRatingListCtrl extends GFCBaseListCtrl<CustomerRating> implements Serializable {

	private static final long serialVersionUID = -6628823752111176539L;
	private final static Logger logger = Logger.getLogger(CustomerRatingListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerRatingList; // autowired
	protected Borderlayout borderLayout_CustomerRatingList; // autowired
	protected Paging pagingCustomerRatingList; // autowired
	protected Listbox listBoxCustomerRating; // autowired
	protected Grid searchGrid;

	// List headers
	protected Listheader listheader_CustCIF; // autowired
	protected Listheader listheader_CustRatingType; // autowired
	protected Listheader listheader_CustRatingCode; // autowired
	protected Listheader listheader_CustRating; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType; // autowired

	// Searching Fields
	protected Textbox custCIF; // autowired
	protected Listbox sortOperator_custCIF; // autowired
	protected Textbox custRatingType; // autowired
	protected Listbox sortOperator_custRatingType; // autowired
	protected Textbox custRatingCode; // autowired
	protected Listbox sortOperator_custRatingCode; // autowired
	protected Textbox custRating; // autowired
	protected Listbox sortOperator_custRating; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType; // autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired

	protected Label label_CustomerRatingSearch_RecordStatus; // autowired
	protected Label label_CustomerRatingSearch_RecordType; // autowired
	protected Label label_CustomerRatingSearchResult; // autowired

	protected Textbox moduleType; // autowired
	protected Radio fromApproved;
	protected Radio fromWorkFlow;
	protected Row workFlowFrom;

	private transient boolean approvedList = false;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CustomerRatingList_NewCustomerRating; // autowired
	protected Button button_CustomerRatingList_CustomerRatingSearchDialog; // autowired
	protected Button button_CustomerRatingList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerRating> searchObj;
	private transient CustomerRatingService customerRatingService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerRatingListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerRating object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerRatingList(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerRating");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerRating");

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

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custRatingType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custRatingType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custRatingCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custRatingCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custRating.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custRating.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_CustomerRatingSearch_RecordStatus.setVisible(false);
			this.label_CustomerRatingSearch_RecordType.setVisible(false);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerRatingList.setHeight(getBorderLayoutHeight());
		this.listBoxCustomerRating.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCustomerRatingList.setPageSize(getListRows());
		this.pagingCustomerRatingList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		this.listheader_CustRatingType.setSortAscending(new FieldComparator("custRatingType", true));
		this.listheader_CustRatingType.setSortDescending(new FieldComparator("custRatingType", false));
		this.listheader_CustRatingCode.setSortAscending(new FieldComparator("custRatingCode", true));
		this.listheader_CustRatingCode.setSortDescending(new FieldComparator("custRatingCode", false));
		this.listheader_CustRating.setSortAscending(new FieldComparator("custRating", true));
		this.listheader_CustRating.setSortDescending(new FieldComparator("custRating", false));

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
		this.listBoxCustomerRating.setItemRenderer(new CustomerRatingListModelItemRenderer());

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerRatingList_NewCustomerRating.setVisible(false);
			this.button_CustomerRatingList_CustomerRatingSearchDialog.setVisible(false);
			this.button_CustomerRatingList_PrintList.setVisible(false);
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
		getUserWorkspace().alocateAuthorities("CustomerRatingList");

		this.button_CustomerRatingList_NewCustomerRating.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerRatingList_NewCustomerRating"));
		this.button_CustomerRatingList_CustomerRatingSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerRatingList_CustomerRatingFindDialog"));
		this.button_CustomerRatingList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerRatingList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customerrating.model.
	 * CustomerRatingListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerRatingItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerRating object
		final Listitem item = this.listBoxCustomerRating.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerRating aCustomerRating = (CustomerRating) item.getAttribute("data");
			final CustomerRating customerRating = getCustomerRatingService().getCustomerRatingById(aCustomerRating.getId(),
							aCustomerRating.getCustRatingType());

			if (customerRating == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = String.valueOf(aCustomerRating.getCustID());
				valueParm[1] = aCustomerRating.getCustRatingType();

				errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CustRatingType")+ ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND CustID='" + customerRating.getCustID()
						+ "' AND custRatingType='"+ customerRating.getCustRatingType() + "' AND version="
						+ customerRating.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"CustomerRating", whereCond,customerRating.getTaskId(),customerRating.getNextTaskId());
					if (userAcces) {
						showDetailView(customerRating);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(customerRating);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerRating dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerRatingList_NewCustomerRating(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerRating object, We GET it from the backEnd.
		final CustomerRating aCustomerRating = getCustomerRatingService().getNewCustomerRating();
		showDetailView(aCustomerRating);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CustomerRating
	 *            (aCustomerRating)
	 * @throws Exception
	 */
	private void showDetailView(CustomerRating aCustomerRating)
			throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aCustomerRating.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomerRating.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerRating", aCustomerRating);
		map.put("customerRatingListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_CustomerRatingList);
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
		this.sortOperator_custRating.setSelectedIndex(0);
		this.custRating.setValue("");
		this.sortOperator_custRatingCode.setSelectedIndex(0);
		this.custRatingCode.setValue("");
		this.sortOperator_custRatingType.setSelectedIndex(0);
		this.custRatingType.setValue("");

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
	 * call the CustomerRating dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerRatingList_CustomerRatingSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerRating print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerRatingList_PrintList(Event event)
			throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerRating", getSearchObj(),
				this.pagingCustomerRatingList.getTotalSize() + 1);

		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerRating>(CustomerRating.class, getListRows());
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addSort("lovDescCustCIF", false);
		this.searchObj.addTabelName("CustomerRatings_View");

		// Workflow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_CustomerRatingList_NewCustomerRating.setVisible(true);
			} else {
				button_CustomerRatingList_NewCustomerRating.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("CustomerRatings_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("CustomerRatings_AView");
		}

		// Customer CIF
		if (!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custCIF.getSelectedItem(),this.custCIF.getValue(), "lovDescCustCIF");
		}
		// Customer Rating Type
		if (!StringUtils.trimToEmpty(this.custRatingType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custRatingType.getSelectedItem(),this.custRatingType.getValue(), "custRatingType");
		}
		// Customer Rating Code
		if (!StringUtils.trimToEmpty(this.custRatingCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custRatingCode.getSelectedItem(),this.custRatingCode.getValue(), "custRatingCode");
		}
		// Customer Rating
		if (!StringUtils.trimToEmpty(this.custRating.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custRating.getSelectedItem(),this.custRating.getValue(), "custRating");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxCustomerRating,this.pagingCustomerRatingList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerRatingService(
			CustomerRatingService customerRatingService) {
		this.customerRatingService = customerRatingService;
	}
	public CustomerRatingService getCustomerRatingService() {
		return this.customerRatingService;
	}

	public JdbcSearchObject<CustomerRating> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerRating> searchObj) {
		this.searchObj = searchObj;
	}

}