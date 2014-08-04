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
 * FileName    		:  CommidityLoanDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.commidityloandetail;

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
import org.zkoss.zul.Intbox;
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
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.service.lmtmasters.CommidityLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.lmtmasters.commidityloandetail.model.CommidityLoanDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/LMTMasters/CommidityLoanDetail/CommidityLoanDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CommidityLoanDetailListCtrl extends GFCBaseListCtrl<CommidityLoanDetail> implements Serializable {

	private static final long serialVersionUID = 6016221553026703014L;
	private final static Logger logger = Logger.getLogger(CommidityLoanDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CommidityLoanDetailList; // autowired
	protected Borderlayout borderLayout_CommidityLoanDetailList; // autowired
	protected Paging pagingCommidityLoanDetailList; // autowired
	protected Listbox listBoxCommidityLoanDetail; // autowired

	// List headers
	protected Listheader listheader_LoanRefNumber; // autowired
	protected Listheader listheader_ItemType; // autowired
	protected Listheader listheader_Quantity; // autowired
	protected Listheader listheader_UnitBuyPrice; // autowired
	protected Listheader listheader_BuyAmount; // autowired
	protected Listheader listheader_UnitSellPrice; // autowired
	protected Listheader listheader_SellAmount; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CommidityLoanDetailList_NewCommidityLoanDetail; // autowired
	protected Button button_CommidityLoanDetailList_CommidityLoanDetailSearch; // autowired
	protected Button button_CommidityLoanDetailList_PrintList; // autowired
	protected Label  label_CommidityLoanDetailList_RecordStatus; 							// autoWired
	protected Label  label_CommidityLoanDetailList_RecordType; 							// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CommidityLoanDetail> searchObj;

	private transient CommidityLoanDetailService commidityLoanDetailService;
	private transient WorkFlowDetails workFlowDetails=null;


	protected Textbox loanRefNumber; // autowired
	protected Listbox sortOperator_LoanRefNumber; // autowired

	protected Textbox itemType; // autowired
	protected Listbox sortOperator_ItemType; // autowired

	protected Textbox itemNumber; // autowired
	protected Listbox sortOperator_ItemNumber; // autowired

	protected Intbox addtional4; // autowired
	protected Listbox sortOperator_Addtional4; // autowired

	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_RecordStatus; // autowired
	protected Listbox sortOperator_RecordType; // autowired
	protected Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false;

	/**
	 * default constructor.<br>
	 */
	public CommidityLoanDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_CommidityLoanDetailList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CommidityLoanDetail");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CommidityLoanDetail");

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

		this.sortOperator_LoanRefNumber.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_LoanRefNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ItemType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ItemType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ItemNumber.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ItemNumber.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_Addtional4.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_Addtional4.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);

			this.sortOperator_RecordType.setSelectedIndex(1);
			this.recordType.setSelectedIndex(0);

		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_CommidityLoanDetailList_RecordStatus.setVisible(false);
			this.label_CommidityLoanDetailList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_CommidityLoanDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxCommidityLoanDetail.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 

		// set the paging parameters
		this.pagingCommidityLoanDetailList.setPageSize(getListRows());
		this.pagingCommidityLoanDetailList.setDetailed(true);

		this.listheader_LoanRefNumber.setSortAscending(new FieldComparator("loanRefNumber", true));
		this.listheader_LoanRefNumber.setSortDescending(new FieldComparator("loanRefNumber", false));
		this.listheader_ItemType.setSortAscending(new FieldComparator("itemType", true));
		this.listheader_ItemType.setSortDescending(new FieldComparator("itemType", false));
		this.listheader_UnitBuyPrice.setSortAscending(new FieldComparator("unitBuyPrice", true));
		this.listheader_UnitBuyPrice.setSortDescending(new FieldComparator("unitvPrice", false));
		this.listheader_BuyAmount.setSortAscending(new FieldComparator("buyAmount", true));
		this.listheader_BuyAmount.setSortDescending(new FieldComparator("buyAmount", false));
		this.listheader_UnitSellPrice.setSortAscending(new FieldComparator("unitSellPrice", true));
		this.listheader_UnitSellPrice.setSortDescending(new FieldComparator("unitSellPrice", false));
		this.listheader_SellAmount.setSortAscending(new FieldComparator("sellAmount", true));
		this.listheader_SellAmount.setSortDescending(new FieldComparator("sellAmount", false));
		this.listheader_Quantity.setSortAscending(new FieldComparator("quantity", true));
		this.listheader_Quantity.setSortDescending(new FieldComparator("quantity", false));

		// set the itemRenderer
		this.listBoxCommidityLoanDetail.setItemRenderer(new CommidityLoanDetailListModelItemRenderer());

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
			this.button_CommidityLoanDetailList_NewCommidityLoanDetail.setVisible(false);
			this.button_CommidityLoanDetailList_CommidityLoanDetailSearch.setVisible(false);
			this.button_CommidityLoanDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));

		}else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		this.button_CommidityLoanDetailList_NewCommidityLoanDetail.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.goodsloandetail.model.CommidityLoanDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCommidityLoanDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected CommidityLoanDetail object
		final Listitem item = this.listBoxCommidityLoanDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CommidityLoanDetail aCommidityLoanDetail = (CommidityLoanDetail) item.getAttribute("data");
			CommidityLoanDetail goodsLoanDetail = null;
			if(approvedList){
				goodsLoanDetail = getCommidityLoanDetailService().getApprovedCommidityLoanDetailById(aCommidityLoanDetail.getId(),aCommidityLoanDetail.getItemType());
			}else{
				goodsLoanDetail = getCommidityLoanDetailService().getCommidityLoanDetailById(aCommidityLoanDetail.getId(),aCommidityLoanDetail.getItemType());
			}

			if(goodsLoanDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aCommidityLoanDetail.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(goodsLoanDetail.getWorkflowId()==0){
						goodsLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					WorkflowLoad flowLoad= new WorkflowLoad(goodsLoanDetail.getWorkflowId(), goodsLoanDetail.getNextTaskId(), getUserWorkspace().getUserRoleSet());

					boolean userAcces =  validateUserAccess("CommidityLoanDetail", new String[] {"LoanRefNumber"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), goodsLoanDetail);
					if (userAcces){
						showDetailView(goodsLoanDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(goodsLoanDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the CommidityLoanDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_CommidityLoanDetailList_NewCommidityLoanDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new CommidityLoanDetail object, We GET it from the backend.
		final CommidityLoanDetail aCommidityLoanDetail = getCommidityLoanDetailService().getNewCommidityLoanDetail();
		showDetailView(aCommidityLoanDetail);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */

	public void onClick$button_CommidityLoanDetailList_CommidityLoanDetailSearch(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doSearch();
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
		logger.debug(event.toString());
		/*		this.pagingCommidityLoanDetailList.setActivePage(0);
			Events.postEvent("onCreate", this.window_CommidityLoanDetailList, event);
			this.window_CommidityLoanDetailList.invalidate();
		 */		

		this.sortOperator_LoanRefNumber.setSelectedIndex(0);
		this.loanRefNumber.setValue("");
		this.sortOperator_ItemType.setSelectedIndex(0);
		this.itemType.setValue("");
		this.sortOperator_ItemNumber.setSelectedIndex(0);
		this.itemNumber.setValue("");
		this.sortOperator_Addtional4.setSelectedIndex(0);
		this.addtional4.setText("");

		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();

		logger.debug("Leaving");
	}

	/*
	 * Invoke Search 
	 */


	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_CommidityLoanDetailList);
		logger.debug("Leaving");
	}

	/**
	 * When the goodsLoanDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CommidityLoanDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("CommidityLoanDetail", getSearchObj());
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "fromApproved"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromApproved(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromApproved"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CommidityLoanDetailList");

		if(moduleType==null){
			this.button_CommidityLoanDetailList_NewCommidityLoanDetail.setVisible(getUserWorkspace().isAllowed("button_CommidityLoanDetailList_NewCommidityLoanDetail"));
		}else{
			this.button_CommidityLoanDetailList_NewCommidityLoanDetail.setVisible(false);
		}	
		this.button_CommidityLoanDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CommidityLoanDetailList_PrintList"));
		logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CommidityLoanDetail (aCommidityLoanDetail)
	 * @throws Exception
	 */
	private void showDetailView(CommidityLoanDetail aCommidityLoanDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("commidityLoanDetail", aCommidityLoanDetail);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the CommidityLoanDetailListbox from the
		 * dialog when we do a delete, edit or insert a CommidityLoanDetail.
		 */
		map.put("commidityLoanDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/LMTMasters/CommidityLoanDetail/CommidityLoanDetailDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 

	public void doSearch() {
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<CommidityLoanDetail>(CommidityLoanDetail.class,getListRows());
		this.searchObj.addSort("LoanRefNumber", false);
		this.searchObj.addTabelName("LMTCommidityLoanDetail_View");

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		this.searchObj.addField("loanRefNumber");
		this.searchObj.addField("itemType");
		this.searchObj.addField("itemNumber");
		this.searchObj.addField("unitPrice");
		this.searchObj.addField("quantity");
		this.searchObj.addField("addtional4");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		// Workflow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType==null) {
				button_CommidityLoanDetailList_NewCommidityLoanDetail.setVisible(true);
			} else {
				button_CommidityLoanDetailList_NewCommidityLoanDetail.setVisible(false);
			}

			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("LMTCommidityLoanDetail_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("LMTCommidityLoanDetail_AView");
		}

		// Loan Ref Number
		if (!StringUtils.trimToEmpty(this.loanRefNumber.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_LoanRefNumber.getSelectedItem(), this.loanRefNumber.getValue(), "LoanRefNumber");
		}
		// Item Type
		if (!StringUtils.trimToEmpty(this.itemType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_ItemType.getSelectedItem(), this.itemType.getValue(), "ItemType");
		}
		// Item Number
		if (!StringUtils.trimToEmpty(this.itemNumber.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_ItemNumber.getSelectedItem(), this.itemNumber.getValue(), "ItemNumber");
		}
		// Addtional4
		if (this.addtional4.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_Addtional4.getSelectedItem(), this.addtional4.getValue(), "Addtional4");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem()!=null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
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
		getPagedListWrapper().init(this.searchObj,this.listBoxCommidityLoanDetail,this.pagingCommidityLoanDetailList);

		logger.debug("Leaving");
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCommidityLoanDetailService(CommidityLoanDetailService goodsLoanDetailService) {
		this.commidityLoanDetailService = goodsLoanDetailService;
	}

	public CommidityLoanDetailService getCommidityLoanDetailService() {
		return this.commidityLoanDetailService;
	}

	public JdbcSearchObject<CommidityLoanDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CommidityLoanDetail> searchObj) {
		this.searchObj = searchObj;
	}
}