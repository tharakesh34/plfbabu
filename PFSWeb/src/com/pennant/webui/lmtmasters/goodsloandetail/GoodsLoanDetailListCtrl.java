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
 * FileName    		:  GoodsLoanDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.lmtmasters.goodsloandetail;

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
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.service.lmtmasters.GoodsLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.lmtmasters.goodsloandetail.model.GoodsLoanDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/LMTMasters/GoodsLoanDetail/GoodsLoanDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class GoodsLoanDetailListCtrl extends GFCBaseListCtrl<GoodsLoanDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(GoodsLoanDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_GoodsLoanDetailList; // autowired
	protected Borderlayout borderLayout_GoodsLoanDetailList; // autowired
	protected Paging pagingGoodsLoanDetailList; // autowired
	protected Listbox listBoxGoodsLoanDetail; // autowired

	// List headers
	protected Listheader listheader_LoanRefNumber; // autowired
	protected Listheader listheader_ItemNumber; // autowired
	protected Listheader listheader_UnitPrice; // autowired
	protected Listheader listheader_Quantity; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_GoodsLoanDetailList_NewGoodsLoanDetail; // autowired
	protected Button button_GoodsLoanDetailList_GoodsLoanDetailSearch; // autowired
	protected Button button_GoodsLoanDetailList_PrintList; // autowired
	protected Label  label_GoodsLoanDetailList_RecordStatus; 							// autoWired
	protected Label  label_GoodsLoanDetailList_RecordType; 							// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<GoodsLoanDetail> searchObj;
	
	private transient GoodsLoanDetailService goodsLoanDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	
	protected Textbox loanRefNumber; // autowired
	protected Listbox sortOperator_LoanRefNumber; // autowired

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
	public GoodsLoanDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_GoodsLoanDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("GoodsLoanDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("GoodsLoanDetail");
			
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
			this.label_GoodsLoanDetailList_RecordStatus.setVisible(false);
			this.label_GoodsLoanDetailList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_GoodsLoanDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxGoodsLoanDetail.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 
		
		// set the paging parameters
		this.pagingGoodsLoanDetailList.setPageSize(getListRows());
		this.pagingGoodsLoanDetailList.setDetailed(true);

		this.listheader_LoanRefNumber.setSortAscending(new FieldComparator("loanRefNumber", true));
		this.listheader_LoanRefNumber.setSortDescending(new FieldComparator("loanRefNumber", false));
		this.listheader_ItemNumber.setSortAscending(new FieldComparator("itemNumber", true));
		this.listheader_ItemNumber.setSortDescending(new FieldComparator("itemNumber", false));
		this.listheader_UnitPrice.setSortAscending(new FieldComparator("unitPrice", true));
		this.listheader_UnitPrice.setSortDescending(new FieldComparator("unitPrice", false));
		this.listheader_Quantity.setSortAscending(new FieldComparator("quantity", true));
		this.listheader_Quantity.setSortDescending(new FieldComparator("quantity", false));
		// set the itemRenderer
		this.listBoxGoodsLoanDetail.setItemRenderer(new GoodsLoanDetailListModelItemRenderer());
		
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
			this.button_GoodsLoanDetailList_NewGoodsLoanDetail.setVisible(false);
			this.button_GoodsLoanDetailList_GoodsLoanDetailSearch.setVisible(false);
			this.button_GoodsLoanDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));

		}else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		this.button_GoodsLoanDetailList_NewGoodsLoanDetail.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.goodsloandetail.model.GoodsLoanDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onGoodsLoanDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected GoodsLoanDetail object
		final Listitem item = this.listBoxGoodsLoanDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final GoodsLoanDetail aGoodsLoanDetail = (GoodsLoanDetail) item.getAttribute("data");
			GoodsLoanDetail goodsLoanDetail = null;
			if(approvedList){
				goodsLoanDetail = getGoodsLoanDetailService().getApprovedGoodsLoanDetailById(aGoodsLoanDetail.getId(),aGoodsLoanDetail.getItemNumber());
			}else{
				goodsLoanDetail = getGoodsLoanDetailService().getGoodsLoanDetailById(aGoodsLoanDetail.getId(),aGoodsLoanDetail.getItemNumber());
			}
			
			if(goodsLoanDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aGoodsLoanDetail.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(goodsLoanDetail.getWorkflowId()==0){
						goodsLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					WorkflowLoad flowLoad= new WorkflowLoad(goodsLoanDetail.getWorkflowId(), goodsLoanDetail.getNextTaskId(), getUserWorkspace().getUserRoleSet());
					
					boolean userAcces =  validateUserAccess("GoodsLoanDetail", new String[] {"LoanRefNumber"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), goodsLoanDetail);
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
	 * Call the GoodsLoanDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_GoodsLoanDetailList_NewGoodsLoanDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new GoodsLoanDetail object, We GET it from the backend.
		final GoodsLoanDetail aGoodsLoanDetail = getGoodsLoanDetailService().getNewGoodsLoanDetail();
		showDetailView(aGoodsLoanDetail);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_GoodsLoanDetailList_GoodsLoanDetailSearch(Event event) throws Exception {
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
	/*		this.pagingGoodsLoanDetailList.setActivePage(0);
			Events.postEvent("onCreate", this.window_GoodsLoanDetailList, event);
			this.window_GoodsLoanDetailList.invalidate();
	*/		

  		this.sortOperator_LoanRefNumber.setSelectedIndex(0);
	  	this.loanRefNumber.setValue("");
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
		PTMessageUtils.showHelpWindow(event, window_GoodsLoanDetailList);
		logger.debug("Leaving");
	}

	/**
	 * When the goodsLoanDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_GoodsLoanDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("GoodsLoanDetail", getSearchObj());
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
		getUserWorkspace().alocateAuthorities("GoodsLoanDetailList");
		
		if(moduleType==null){
			this.button_GoodsLoanDetailList_NewGoodsLoanDetail.setVisible(false);//getUserWorkspace().isAllowed("button_GoodsLoanDetailList_NewGoodsLoanDetail")
		}else{
			this.button_GoodsLoanDetailList_NewGoodsLoanDetail.setVisible(false);
		}	
		this.button_GoodsLoanDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_GoodsLoanDetailList_PrintList"));
	logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param GoodsLoanDetail (aGoodsLoanDetail)
	 * @throws Exception
	 */
	private void showDetailView(GoodsLoanDetail aGoodsLoanDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("goodsLoanDetail", aGoodsLoanDetail);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the GoodsLoanDetailListbox from the
		 * dialog when we do a delete, edit or insert a GoodsLoanDetail.
		 */
		map.put("goodsLoanDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/LMTMasters/GoodsLoanDetail/GoodsLoanDetailDialog.zul",null,map);
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
		this.searchObj = new JdbcSearchObject<GoodsLoanDetail>(GoodsLoanDetail.class,getListRows());
		this.searchObj.addFilter(new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addSort("LoanRefNumber", false);
		this.searchObj.addTabelName("LMTGoodsLoanDetail_View");
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		this.searchObj.addField("loanRefNumber");
		this.searchObj.addField("itemNumber");
		this.searchObj.addField("unitPrice");
		this.searchObj.addField("quantity");
		this.searchObj.addField("addtional4");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		// Workflow
		if (isWorkFlowEnabled()) {
			
			if (isFirstTask() && this.moduleType==null) {
				button_GoodsLoanDetailList_NewGoodsLoanDetail.setVisible(false);
			} else {
				button_GoodsLoanDetailList_NewGoodsLoanDetail.setVisible(false);
			}
			
			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("LMTGoodsLoanDetail_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("LMTGoodsLoanDetail_AView");
		}
		
	// Loan Ref Number
		if (!StringUtils.trimToEmpty(this.loanRefNumber.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_LoanRefNumber.getSelectedItem(), this.loanRefNumber.getValue(), "LoanRefNumber");
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
		getPagedListWrapper().init(this.searchObj,this.listBoxGoodsLoanDetail,this.pagingGoodsLoanDetailList);

		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setGoodsLoanDetailService(GoodsLoanDetailService goodsLoanDetailService) {
		this.goodsLoanDetailService = goodsLoanDetailService;
	}

	public GoodsLoanDetailService getGoodsLoanDetailService() {
		return this.goodsLoanDetailService;
	}

	public JdbcSearchObject<GoodsLoanDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<GoodsLoanDetail> searchObj) {
		this.searchObj = searchObj;
	}
}