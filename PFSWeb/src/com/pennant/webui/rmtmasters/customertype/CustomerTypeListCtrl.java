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
 * FileName    		:  CustomerTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.customertype;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
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
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.rmtmasters.customertype.model.CustomerTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/CustomerType/CustomerTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerTypeListCtrl extends GFCBaseListCtrl<CustomerType>
		implements Serializable {

	private static final long serialVersionUID = 5954194788863085861L;
	private final static Logger logger = Logger.getLogger(CustomerTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerTypeList; 		// auto wired
	protected Borderlayout 	borderLayout_CustomerTypeList; 	// auto wired
	protected Paging 		pagingCustomerTypeList; 		// auto wired
	protected Listbox 		listBoxCustomerType; 			// auto wired

	// List headers
	protected Listheader listheader_CustTypeCode; 		// auto wired
	protected Listheader listheader_CustTypeDesc; 		// auto wired
	protected Listheader listheader_CustTypeCtg; 		// auto wired	
	protected Listheader listheader_CustTypeIsActive; 	// auto wired
	protected Listheader listheader_RecordStatus; 		// auto wired
	protected Listheader listheader_RecordType;
	
	//Search
	protected Textbox 	custTypeCode; 					// auto wired
	protected Listbox 	sortOperator_custTypeCode; 		// auto wired
	protected Textbox 	custTypeDesc; 					// auto wired
	protected Listbox 	sortOperator_custTypeDesc; 		// auto wired
	protected Combobox 	custTypeCtg; 					// auto wired
	protected Listbox 	sortOperator_custTypeCtg; 		// auto wired
	protected Checkbox 	custTypeIsActive; 				// auto wired
	protected Listbox 	sortOperator_custTypeIsActive;	// auto wired
	protected Textbox 	recordStatus; 					// auto wired
	protected Listbox 	recordType;						// auto wired
	protected Listbox 	sortOperator_recordStatus; 		// auto wired
	protected Listbox 	sortOperator_recordType; 		// auto wired
	
	protected Label label_CustomerTypeSearch_RecordStatus; 	// auto wired
	protected Label label_CustomerTypeSearch_RecordType; 	// auto wired
	protected Label label_CustomerTypeSearchResult; 		// auto wired
	
	protected Grid	                       searchGrid;	
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;
	private transient boolean	           approvedList	    = false;
	private List<ValueLabel>           listCustTypeCtg = PennantStaticListUtil.getCategoryType();
	// checkRights
	protected Button btnHelp; 											// auto wired
	protected Button button_CustomerTypeList_NewCustomerType; 			// auto wired
	protected Button button_CustomerTypeList_CustomerTypeSearchDialog; 	// auto wired
	protected Button button_CustomerTypeList_PrintList; 				// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerType> searchObj;
	
	private transient CustomerTypeService customerTypeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerTypeList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerType");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerType");
			
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

		this.sortOperator_custTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_custTypeCtg.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custTypeCtg.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custTypeIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_custTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_CustomerTypeSearch_RecordStatus.setVisible(false);
			this.label_CustomerTypeSearch_RecordType.setVisible(false);
		}
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerTypeList.setHeight(getBorderLayoutHeight());
		this.listBoxCustomerType.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCustomerTypeList.setPageSize(getListRows());
		this.pagingCustomerTypeList.setDetailed(true);

		this.listheader_CustTypeCode.setSortAscending(new FieldComparator("custTypeCode", true));
		this.listheader_CustTypeCode.setSortDescending(new FieldComparator("custTypeCode", false));
		this.listheader_CustTypeDesc.setSortAscending(new FieldComparator("custTypeDesc", true));
		this.listheader_CustTypeDesc.setSortDescending(new FieldComparator("custTypeDesc", false));
		this.listheader_CustTypeCtg.setSortAscending(new FieldComparator("custTypeCtg", true));
		this.listheader_CustTypeCtg.setSortDescending(new FieldComparator("custTypeCtg", false));		
		this.listheader_CustTypeIsActive.setSortAscending(new FieldComparator("custTypeIsActive", true));
		this.listheader_CustTypeIsActive.setSortDescending(new FieldComparator("custTypeIsActive", false));
		
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
		this.listBoxCustomerType.setItemRenderer(new CustomerTypeListModelItemRenderer());
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CustomerTypeList_NewCustomerType.setVisible(false);
			this.button_CustomerTypeList_CustomerTypeSearchDialog.setVisible(false);
			this.button_CustomerTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(
					PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}	
		setListCustTypeCtg();
		logger.debug("Leaving" +event.toString());
	}
	/**
	 * This method sets all rightsTypes as ComboItems for ComboBox
	 */
	private void setListCustTypeCtg() {
		logger.debug("Entering ");
		Comboitem comboitem ;
		for (int i = 0; i < listCustTypeCtg.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setLabel(listCustTypeCtg.get(i).getLabel());
			comboitem.setValue(listCustTypeCtg.get(i).getValue());
			this.custTypeCtg.appendChild(comboitem);
		}
		logger.debug("Leaving ");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().alocateAuthorities("CustomerTypeList");
		this.button_CustomerTypeList_NewCustomerType.setVisible(
				getUserWorkspace().isAllowed("button_CustomerTypeList_NewCustomerType"));
		this.button_CustomerTypeList_CustomerTypeSearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_CustomerTypeList_CustomerTypeFindDialog"));
		this.button_CustomerTypeList_PrintList.setVisible(
				getUserWorkspace().isAllowed("button_CustomerTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.customertype.model.
	 * CustomerTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// get the selected CustomerType object
		final Listitem item = this.listBoxCustomerType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerType aCustomerType = (CustomerType) item.getAttribute("data");
			final CustomerType customerType = getCustomerTypeService()
					.getCustomerTypeById(aCustomerType.getId());
			if(customerType==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aCustomerType.getCustTypeCode();

				errParm[0] = PennantJavaUtil.getLabel("label_CustTypeCode") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND CustTypeCode='"+ customerType.getCustTypeCode()+
				"' AND version=" + customerType.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CustomerType",
							whereCond, customerType.getTaskId(), customerType.getNextTaskId());
					if (userAcces){
						showDetailView(customerType);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(customerType);
				}
			}
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Call the CustomerType dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerTypeList_NewCustomerType(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new CustomerType object, We GET it from the back end.
		final CustomerType aCustomerType = getCustomerTypeService().getNewCustomerType();
		showDetailView(aCustomerType);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CustomerType (aCustomerType)
	 * @throws Exception
	 */
	private void showDetailView(CustomerType aCustomerType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCustomerType.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCustomerType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerType", aCustomerType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CustomerTypeListbox from the
		 * dialog when we do a delete, edit or insert a CustomerType.
		 */
		map.put("customerTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/CustomerType/CustomerTypeDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CustomerTypeList);
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
		this.sortOperator_custTypeCode.setSelectedIndex(0);
		this.custTypeCode.setValue("");
		this.sortOperator_custTypeDesc.setSelectedIndex(0);
		this.custTypeDesc.setValue("");
		this.sortOperator_custTypeCtg.setSelectedIndex(0);
		this.custTypeCtg.setValue("");
		this.sortOperator_custTypeIsActive.setSelectedIndex(0);
		this.custTypeIsActive.setValue("");
		
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		doSearch();
		/*this.pagingCustomerTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerTypeList, event);
		this.window_CustomerTypeList.invalidate();*/
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * call the CustomerType dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerTypeList_CustomerTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doSearch();
		logger.debug("Leaving"  +event.toString());
	}

	/**
	 * When the customerType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerType", getSearchObj(),this.pagingCustomerTypeList.getTotalSize()+1);
		logger.debug("Leaving" +event.toString());
	}
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerType>(CustomerType.class,getListRows());
		this.searchObj.addSort("CustTypeCode", false);
		this.searchObj.addTabelName("RMTCustTypes_View");
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_CustomerTypeList_NewCustomerType.setVisible(true);
			} else {
				button_CustomerTypeList_NewCustomerType.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("RMTCustTypes_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("RMTCustTypes_AView");
		}
		// Customer Type Code
		if (!StringUtils.trimToEmpty(this.custTypeCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custTypeCode.getSelectedItem(), this.custTypeCode.getValue(), "custTypeCode");
		}
		
		// Customer Type Desc
		if (!StringUtils.trimToEmpty(this.custTypeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_custTypeDesc.getSelectedItem(), this.custTypeDesc.getValue(), "custTypeDesc");
		}
		// Customer Type Ctg
		if (null !=this.custTypeCtg.getSelectedItem() && !StringUtils.trimToEmpty(this.custTypeCtg.getSelectedItem().getValue().toString()).equals("")){
			searchObj = getSearchFilter(searchObj, this.sortOperator_custTypeCtg.getSelectedItem(), this.custTypeCtg.getSelectedItem().getValue().toString(), "custTypeCtg");
		}
		//Customer Type IsActive
		if (custTypeIsActive.isChecked()) { 
			 searchObj = getSearchFilter(searchObj,this.sortOperator_custTypeIsActive.getSelectedItem(), 1,"custTypeIsActive"); 
			 } else { 
				 searchObj = getSearchFilter(searchObj,this.sortOperator_custTypeIsActive.getSelectedItem(), 0,"custTypeIsActive"); 
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
		getPagedListWrapper().init(this.searchObj, this.listBoxCustomerType, this.pagingCustomerTypeList);
		logger.debug("Leaving" );
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerTypeService(CustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}
	public CustomerTypeService getCustomerTypeService() {
		return this.customerTypeService;
	}

	public JdbcSearchObject<CustomerType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerType> searchObj) {
		this.searchObj = searchObj;
	}
}