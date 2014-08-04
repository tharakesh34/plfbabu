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
 * FileName    		:  CustomerGroupListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customergroup;

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
import org.zkoss.zul.Checkbox;
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
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customergroup.model.CustomerGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerGroup/CustomerGroupList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerGroupListCtrl extends GFCBaseListCtrl<CustomerGroup> implements Serializable {

	private static final long serialVersionUID = 8090581617957622077L;
	private final static Logger logger = Logger.getLogger(CustomerGroupListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerGroupList; 			// autoWired
	protected Borderlayout 	borderLayout_CustomerGroupList; 	// autoWired
	protected Paging 		pagingCustomerGroupList; 			// autoWired
	protected Listbox 		listBoxCustomerGroup; 				// autoWired

	// List headers
	protected Listheader listheader_CustGrpCode; 		// autoWired
	protected Listheader listheader_CustGrpDesc; 		// autoWired
	protected Listheader listheader_CustGrpRO1; 		// autoWired
	protected Listheader listheader_CustGrpIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;
	
	//Search
	protected Textbox	custCIF; 							// autoWired
	protected Listbox 	sortOperator_custCIF; 				// autoWired
	protected Textbox	custGrpCode; 							// autoWired
	protected Listbox 	sortOperator_custGrpCode; 				// autoWired
	protected Textbox 	custGrpDesc;							// autoWired
	protected Listbox 	sortOperator_custGrpDesc;				// autoWired
	protected Textbox 	custGrpRO1;							// autoWired
	protected Listbox 	sortOperator_custGrpRO1;				// autoWired
	protected Checkbox 	custGrpIsActive; 						// autoWired
	protected Listbox 	sortOperator_custGrpIsActive; 			// autoWired
	protected Textbox 	recordStatus; 							// autoWired
	protected Listbox 	recordType;								// autoWired
	protected Listbox 	sortOperator_recordStatus; 				// autoWired
	protected Listbox 	sortOperator_recordType; 				// autoWired

	protected Label label_CustomerGroupSearch_RecordStatus; 	// autoWired
	protected Label label_CustomerGroupSearch_RecordType; 		// autoWired
	protected Label label_CustomerGroupSearchResult; 			// autoWired
	
	protected Grid	                       searchGrid;	 
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;

	private transient boolean	           approvedList	    = false;

	// checkRights
	protected Button btnHelp; 												// autoWired
	protected Button button_CustomerGroupList_NewCustomerGroup; 			// autoWired
	protected Button button_CustomerGroupList_CustomerGroupSearchDialog; 	// autoWired
	protected Button button_CustomerGroupList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerGroup> searchObj;
	private transient CustomerGroupService customerGroupService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CustomerGroupListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer Group object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerGroupList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerGroup");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerGroup");

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

		this.sortOperator_custGrpCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custGrpCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custGrpDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custGrpDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_custGrpRO1.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custGrpRO1.setItemRenderer(new SearchOperatorListModelItemRenderer());		
		
		this.sortOperator_custGrpIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custGrpIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_CustomerGroupSearch_RecordStatus.setVisible(false);
			this.label_CustomerGroupSearch_RecordType.setVisible(false);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerGroupList.setHeight(getBorderLayoutHeight());
		this.listBoxCustomerGroup.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCustomerGroupList.setPageSize(getListRows());
		this.pagingCustomerGroupList.setDetailed(true);

		this.listheader_CustGrpCode.setSortAscending(new FieldComparator("custGrpCode", true));
		this.listheader_CustGrpCode.setSortDescending(new FieldComparator("custGrpCode", false));
		this.listheader_CustGrpDesc.setSortAscending(new FieldComparator("custGrpDesc", true));
		this.listheader_CustGrpDesc.setSortDescending(new FieldComparator("custGrpDesc", false));
		this.listheader_CustGrpRO1.setSortAscending(new FieldComparator("custGrpRO1", true));
		this.listheader_CustGrpRO1.setSortDescending(new FieldComparator("custGrpRO1", false));
		this.listheader_CustGrpIsActive.setSortAscending(new FieldComparator("custGrpIsActive", true));
		this.listheader_CustGrpIsActive.setSortDescending(new FieldComparator("custGrpIsActive", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// set the itemRenderer
		this.listBoxCustomerGroup.setItemRenderer(new CustomerGroupListModelItemRenderer());
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerGroupList_NewCustomerGroup.setVisible(false);
			this.button_CustomerGroupList_CustomerGroupSearchDialog.setVisible(false);
			this.button_CustomerGroupList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
			
		}	
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerGroupList");

		this.button_CustomerGroupList_NewCustomerGroup.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerGroupList_NewCustomerGroup"));
		this.button_CustomerGroupList_CustomerGroupSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerGroupList_CustomerGroupFindDialog"));
		this.button_CustomerGroupList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerGroupList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.customergroup.model.CustomerGroupListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerGroupItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// get the selected CustomerGroup object
		final Listitem item = this.listBoxCustomerGroup.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerGroup aCustomerGroup = (CustomerGroup) item.getAttribute("data");
			final CustomerGroup customerGroup = getCustomerGroupService().getCustomerGroupById(aCustomerGroup.getId());

			if (customerGroup == null) {

				String[] valueParm = new String[1];
				String[] errParm = new String[1];

				valueParm[0] = String.valueOf(aCustomerGroup.getCustGrpID());
				errParm[0] = PennantJavaUtil.getLabel("label_CustGrpID")+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond =  " AND CustGrpID="+ customerGroup.getCustGrpID()+" AND version=" + customerGroup.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(), "CustomerGroup", whereCond, customerGroup.getTaskId(),
							customerGroup.getNextTaskId());
					if (userAcces){
						showDetailView(customerGroup);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerGroup);
				}
			}
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Call the CustomerGroup dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerGroupList_NewCustomerGroup(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new CustomerGroup object, We GET it from the back end.
		final CustomerGroup aCustomerGroup = getCustomerGroupService().getNewCustomerGroup();
		showDetailView(aCustomerGroup);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param CustomerGroup (aCustomerGroup)
	 * @throws Exception
	 */
	private void showDetailView(CustomerGroup aCustomerGroup) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aCustomerGroup.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerGroup.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerGroup", aCustomerGroup);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CustomerGroupListbox from the
		 * dialog when we do a delete, edit or insert a CustomerGroup.
		 */
		map.put("customerGroupListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerGroup/CustomerGroupDialog.zul",
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
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerGroupList);
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering" +event.toString());
		this.sortOperator_custGrpCode.setSelectedIndex(0);
		this.custGrpCode.setValue("");
		this.sortOperator_custGrpDesc.setSelectedIndex(0);
		this.custGrpDesc.setValue("");
		this.sortOperator_custGrpRO1.setSelectedIndex(0);
		this.custGrpRO1.setValue("");
		this.sortOperator_custGrpIsActive.setSelectedIndex(0);
		this.custGrpIsActive.setValue("");
		
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		/*this.pagingCustomerGroupList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerGroupList, event);
		this.window_CustomerGroupList.invalidate();*/
		logger.debug("Leaving" +event.toString());
	}

	/*
	 * call the CustomerGroup dialog
	 */
	public void onClick$button_CustomerGroupList_CustomerGroupSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doSearch();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * When the customerGroup print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerGroupList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerGroups", getSearchObj(),this.pagingCustomerGroupList.getTotalSize()+1);
		logger.debug("Leaving" +event.toString());
	}
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerGroup>(CustomerGroup.class, getListRows());
		this.searchObj.addSort("custGrpCode", false);
		this.searchObj.addTabelName("CustomerGroups_View");
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_CustomerGroupList_NewCustomerGroup.setVisible(true);
			} else {
				button_CustomerGroupList_NewCustomerGroup.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("CustomerGroups_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("CustomerGroups_AView");
		}
		// Customer GrpCode
		if (!StringUtils.trimToEmpty(this.custGrpCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custGrpCode.getSelectedItem(), this.custGrpCode.getValue(), "custGrpCode");
		}
		
		// Customer GrpDesc
		if (!StringUtils.trimToEmpty(this.custGrpDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custGrpDesc.getSelectedItem(), this.custGrpDesc.getValue(), "custGrpDesc");
		}
		// Customer GrpRO1
		if (!StringUtils.trimToEmpty(this.custGrpRO1.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custGrpRO1.getSelectedItem(), this.custGrpRO1.getValue(), "custGrpRO1");
		}

		// Customer GrpIsActive
		if (custGrpIsActive.isChecked()) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custGrpIsActive.getSelectedItem(), 1,"custGrpIsActive");
		} else {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custGrpIsActive.getSelectedItem(), 0,"custGrpIsActive");
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
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxCustomerGroup,this.pagingCustomerGroupList);
		logger.debug("Leaving");
	
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerGroupService(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}
	public CustomerGroupService getCustomerGroupService() {
		return this.customerGroupService;
	}

	public JdbcSearchObject<CustomerGroup> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerGroup> searchObj) {
		this.searchObj = searchObj;
	}

}