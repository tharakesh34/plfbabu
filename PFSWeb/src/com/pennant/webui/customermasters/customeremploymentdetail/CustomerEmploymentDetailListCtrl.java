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
 * FileName    		:  CustomerEmploymentDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeremploymentdetail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customeremploymentdetail.model.CustomerEmploymentDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail
 * /CustomerEmploymentDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerEmploymentDetailListCtrl extends GFCBaseListCtrl<CustomerEmploymentDetail> implements Serializable {

	private static final long serialVersionUID = 5652445153118844873L;
	private final static Logger logger = Logger.getLogger(CustomerEmploymentDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are gettingautoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerEmploymentDetailList; 		//autoWired
	protected Borderlayout 	borderLayout_CustomerEmploymentDetailList; 	//autoWired
	protected Paging 		pagingCustomerEmploymentDetailList; 		//autoWired
	protected Listbox 		listBoxCustomerEmploymentDetail; 			//autoWired

	// List headers
	protected Listheader listheader_CustEmpCIF; 	//autoWired
	protected Listheader listheader_CustEmpName; 	//autoWired
	protected Listheader listheader_CustEmpDesg; 	//autoWired
	protected Listheader listheader_CustEmpDept; 	//autoWired
	protected Listheader listheader_CustEmpID; 		//autoWired
	protected Listheader listheader_RecordStatus; 	//autoWired
	protected Listheader listheader_RecordType;
	
	//search
	protected Textbox custCIF; 									// autowired
	protected Listbox sortOperator_custCIF; 					// autowired
	protected Textbox custEmpName; 								// autowired
	protected Listbox sortOperator_custEmpName; 				// autowired
	protected Datebox custEmpFrom; 								// autowired
	protected Listbox sortOperator_custEmpFrom; 				// autowired
	protected Textbox custEmpDesg; 								// autowired
	protected Listbox sortOperator_custEmpDesg; 				// autowired
	protected Textbox custEmpDept; 								// autowired
	protected Listbox sortOperator_custEmpDept; 				// autowired
	protected Textbox custEmpID; 								// autowired
	protected Listbox sortOperator_custEmpID; 					// autowired
	protected Textbox custEmpType; 								// autowired
	protected Listbox sortOperator_custEmpType; 				// autowired
	protected Textbox custEmpHNbr; 								// autowired
	protected Listbox sortOperator_custEmpHNbr; 				// autowired
	protected Textbox custEMpFlatNbr; 							// autowired
	protected Listbox sortOperator_custEMpFlatNbr; 				// autowired
	protected Textbox custEmpAddrStreet; 						// autowired
	protected Listbox sortOperator_custEmpAddrStreet; 			// autowired
	protected Textbox custEmpPOBox; 							// autowired
	protected Listbox sortOperator_custEmpPOBox; 				// autowired
	protected Textbox custEmpAddrCity; 							// autowired
	protected Listbox sortOperator_custEmpAddrCity; 			// autowired
	protected Textbox custEmpAddrProvince; 						// autowired
	protected Listbox sortOperator_custEmpAddrProvince; 		// autowired
	protected Textbox custEmpAddrCountry; 						// autowired
	protected Listbox sortOperator_custEmpAddrCountry; 			// autowired
	protected Textbox custEmpAddrZIP; 							// autowired
	protected Listbox sortOperator_custEmpAddrZIP; 				// autowired
	protected Textbox custEmpAddrPhone; 						// autowired
	protected Listbox sortOperator_custEmpAddrPhone; 			// autowired
	protected Textbox recordStatus; 							// autowired
	protected Listbox recordType;								// autowired
	protected Listbox sortOperator_recordStatus; 				// autowired
	protected Listbox sortOperator_recordType; 					// autowired
	
	protected Label label_CustomerEmploymentDetailSearch_RecordStatus; 	// autowired
	protected Label label_CustomerEmploymentDetailSearch_RecordType; 	// autowired
	protected Label label_CustomerEmploymentDetailSearchResult; 		// autowired
	
	protected Grid	                       searchGrid;	                                                  // autowired
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;
	
	private transient boolean	           approvedList	    = false;

	// checkRights
	protected Button btnHelp; 																	//autoWired
	protected Button button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail; 			//autoWired
	protected Button button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog; 	//autoWired
	protected Button button_CustomerEmploymentDetailList_PrintList; //autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerEmploymentDetail> searchObj;
	private transient CustomerEmploymentDetailService customerEmploymentDetailService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CustomerEmploymentDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerEmploymentDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerEmploymentDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerEmploymentDetail");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerEmploymentDetail");

			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		
		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpDesg.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpDesg.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpDept.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpDept.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custEmpID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custEmpID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_CustomerEmploymentDetailSearch_RecordStatus.setVisible(false);
			this.label_CustomerEmploymentDetailSearch_RecordType.setVisible(false);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerEmploymentDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxCustomerEmploymentDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCustomerEmploymentDetailList.setPageSize(getListRows());
		this.pagingCustomerEmploymentDetailList.setDetailed(true);

		this.listheader_CustEmpCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustEmpCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		this.listheader_CustEmpName.setSortAscending(new FieldComparator("custEmpName", true));
		this.listheader_CustEmpName.setSortDescending(new FieldComparator("custEmpName", false));
		this.listheader_CustEmpDesg.setSortAscending(new FieldComparator("custEmpDesg", true));
		this.listheader_CustEmpDesg.setSortDescending(new FieldComparator("custEmpDesg", false));
		this.listheader_CustEmpDept.setSortAscending(new FieldComparator("custEmpDept", true));
		this.listheader_CustEmpDept.setSortDescending(new FieldComparator("custEmpDept", false));
		this.listheader_CustEmpID.setSortAscending(new FieldComparator("custEmpID", true));
		this.listheader_CustEmpID.setSortDescending(new FieldComparator("custEmpID", false));
		this.listBoxCustomerEmploymentDetail.setItemRenderer(new CustomerEmploymentDetailListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail.setVisible(false);
			this.button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog.setVisible(false);
			this.button_CustomerEmploymentDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
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
		
		getUserWorkspace().alocateAuthorities("CustomerEmploymentDetailList");

		this.button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail"));
		this.button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEmploymentDetailList_CustomerEmploymentDetailFindDialog"));
		this.button_CustomerEmploymentDetailList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEmploymentDetailList_PrintList"));

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customeremploymentdetail.model.CustomerEmploymentDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerEmploymentDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerEmploymentDetail object
		final Listitem item = this.listBoxCustomerEmploymentDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerEmploymentDetail aCustomerEmploymentDetail = (CustomerEmploymentDetail) 
								item.getAttribute("data");
			final CustomerEmploymentDetail customerEmploymentDetail = getCustomerEmploymentDetailService().getCustomerEmploymentDetailById(aCustomerEmploymentDetail.getId(),aCustomerEmploymentDetail.getCustEmpName());

			if (customerEmploymentDetail == null) {

				String[] valueParm = new String[1];
				String[] errParm = new String[1];

				valueParm[0] = String.valueOf(aCustomerEmploymentDetail.getCustID());
				errParm[0] = PennantJavaUtil.getLabel("label_CustID")+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond =  " AND CustID='"+ customerEmploymentDetail.getCustID()+
									"' AND version=" + customerEmploymentDetail.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),"CustomerEmploymentDetail",
							whereCond, customerEmploymentDetail.getTaskId(),customerEmploymentDetail.getNextTaskId());
					if (userAcces){
						showDetailView(customerEmploymentDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerEmploymentDetail);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerEmploymentDetail dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerEmploymentDetail object, We GET it from the back end.
		final CustomerEmploymentDetail aCustomerEmploymentDetail = getCustomerEmploymentDetailService().getNewCustomerEmploymentDetail();
		showDetailView(aCustomerEmploymentDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CustomerEmploymentDetail (aCustomerEmploymentDetail)
	 * @throws Exception
	 */
	private void showDetailView(CustomerEmploymentDetail aCustomerEmploymentDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aCustomerEmploymentDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerEmploymentDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerEmploymentDetail", aCustomerEmploymentDetail);
		map.put("customerEmploymentDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailDialog.zul",
							null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CustomerEmploymentDetailList);
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
		this.sortOperator_custEmpDept.setSelectedIndex(0);
		this.custEmpDept.setValue("");
		this.sortOperator_custEmpDesg.setSelectedIndex(0);
		this.custEmpDesg.setValue("");
		this.sortOperator_custEmpID.setSelectedIndex(0);
		this.custEmpID.setValue("");
		this.sortOperator_custEmpName.setSelectedIndex(0);
		this.custEmpName.setValue("");
		this.sortOperator_recordStatus.setSelectedIndex(0);
		this.recordStatus.setValue("");
		this.pagingCustomerEmploymentDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerEmploymentDetailList, event);
		this.window_CustomerEmploymentDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the CustomerEmploymentDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerEmploymentDetailList_CustomerEmploymentDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerEmploymentDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerEmploymentDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerEmploymentDetail", getSearchObj(),this.pagingCustomerEmploymentDetailList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	
	public void doSearch() {
		logger.debug("Entering");
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerEmploymentDetail>(CustomerEmploymentDetail.class,getListRows());
		this.searchObj.addSort("CustID", false);
		//this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addTabelName("CustomerEmpDetails_View");

		// Work flow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail.setVisible(true);
			} else {
				button_CustomerEmploymentDetailList_NewCustomerEmploymentDetail.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("CustomerEmpDetails_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("CustomerEmpDetails_AView");
		}
		
		
		// Customer CIF
		if (!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custCIF.getSelectedItem(), this.custCIF.getValue(), "lovDescCustCIF");
		}
		
		// Customer EMPName
		if (!StringUtils.trimToEmpty(this.custEmpName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custEmpName.getSelectedItem(), this.custEmpName.getValue(), "custEmpName");
		}
		
		// Customer EmpDesg
		if (!StringUtils.trimToEmpty(this.custEmpDesg.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custEmpDesg.getSelectedItem(), this.custEmpDesg.getValue(), "custEmpDesg");
		}
		
		// Customer EmpDept
		if (!StringUtils.trimToEmpty(this.custEmpDept.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custEmpDept.getSelectedItem(), this.custEmpDept.getValue(), "custEmpDept");
		}
		
		// Customer EMPID
		if (!StringUtils.trimToEmpty(this.custEmpID.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custEmpID.getSelectedItem(), this.custEmpID.getValue(), "custEmpID");
		}
		
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		}
		
		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		
		getPagedListWrapper().init(this.searchObj,this.listBoxCustomerEmploymentDetail,	this.pagingCustomerEmploymentDetailList);
		logger.debug("Leaving");
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerEmploymentDetailService(CustomerEmploymentDetailService customerEmploymentDetailService) {
		this.customerEmploymentDetailService = customerEmploymentDetailService;
	}
	public CustomerEmploymentDetailService getCustomerEmploymentDetailService() {
		return this.customerEmploymentDetailService;
	}

	public JdbcSearchObject<CustomerEmploymentDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerEmploymentDetail> searchObj) {
		this.searchObj = searchObj;
	}

}