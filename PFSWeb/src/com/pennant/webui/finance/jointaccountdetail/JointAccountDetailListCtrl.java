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
 * FileName    		:  JountAccountDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.jointaccountdetail;

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
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.finance.jountaccountdetail.model.JountAccountDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/JountAccountDetail/JountAccountDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class JointAccountDetailListCtrl extends GFCBaseListCtrl<JointAccountDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(JointAccountDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_JountAccountDetailList; // autowired
	protected Borderlayout borderLayout_JountAccountDetailList; // autowired
	protected Paging pagingJountAccountDetailList; // autowired
	protected Listbox listBoxJountAccountDetail; // autowired

	// List headers
	protected Listheader listheader_CustCIF; // autowired
	protected Listheader listheader_RepayAccountId; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_JountAccountDetailList_NewJountAccountDetail; // autowired
	protected Button button_JountAccountDetailList_JountAccountDetailSearch; // autowired
	protected Button button_JountAccountDetailList_PrintList; // autowired
	protected Label  label_JountAccountDetailList_RecordStatus; 							// autoWired
	protected Label  label_JountAccountDetailList_RecordType; 							// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<JointAccountDetail> searchObj;
	
	private transient JointAccountDetailService jointAccountDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	
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
	public JointAccountDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_JountAccountDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("JountAccountDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("JountAccountDetail");
			
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
			this.label_JountAccountDetailList_RecordStatus.setVisible(false);
			this.label_JountAccountDetailList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_JountAccountDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxJountAccountDetail.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 
		
		// set the paging parameters
		this.pagingJountAccountDetailList.setPageSize(getListRows());
		this.pagingJountAccountDetailList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("custCIF", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("custCIF", false));
		this.listheader_RepayAccountId.setSortAscending(new FieldComparator("repayAccountId", true));
		this.listheader_RepayAccountId.setSortDescending(new FieldComparator("repayAccountId", false));
		// set the itemRenderer
		this.listBoxJountAccountDetail.setItemRenderer(new JountAccountDetailListModelItemRenderer());
		
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
			this.button_JountAccountDetailList_NewJountAccountDetail.setVisible(false);
			this.button_JountAccountDetailList_JountAccountDetailSearch.setVisible(false);
			this.button_JountAccountDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));

		}else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.finance.jountaccountdetail.model.JountAccountDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onJountAccountDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected JountAccountDetail object
		final Listitem item = this.listBoxJountAccountDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final JointAccountDetail aJountAccountDetail = (JointAccountDetail) item.getAttribute("data");
			JointAccountDetail jountAccountDetail = null;
			if(approvedList){
				jountAccountDetail = getJointAccountDetailService().getApprovedJountAccountDetailById(aJountAccountDetail.getId());
			}else{
				jountAccountDetail = getJointAccountDetailService().getJountAccountDetailById(aJountAccountDetail.getId());
			}
			
			if(jountAccountDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aJountAccountDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_JointAccountId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(jountAccountDetail.getWorkflowId()==0){
						jountAccountDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					WorkflowLoad flowLoad= new WorkflowLoad(jountAccountDetail.getWorkflowId(), jountAccountDetail.getNextTaskId(), getUserWorkspace().getUserRoleSet());
					
					boolean userAcces =  validateUserAccess("JountAccountDetail", new String[] {"JointAccountId"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), jountAccountDetail);
					if (userAcces){
						showDetailView(jountAccountDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(jountAccountDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the JountAccountDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_JountAccountDetailList_NewJountAccountDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new JountAccountDetail object, We GET it from the backend.
		final JointAccountDetail aJountAccountDetail = getJointAccountDetailService().getNewJountAccountDetail();
		showDetailView(aJountAccountDetail);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_JountAccountDetailList_JountAccountDetailSearch(Event event) throws Exception {
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
	/*		this.pagingJountAccountDetailList.setActivePage(0);
			Events.postEvent("onCreate", this.window_JountAccountDetailList, event);
			this.window_JountAccountDetailList.invalidate();
	*/		


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
		PTMessageUtils.showHelpWindow(event, window_JountAccountDetailList);
		logger.debug("Leaving");
	}

	/**
	 * When the jountAccountDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_JountAccountDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("JountAccountDetail", getSearchObj());
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
		getUserWorkspace().alocateAuthorities("JountAccountDetailList");
		
		if(moduleType==null){
			this.button_JountAccountDetailList_NewJountAccountDetail.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailList_NewJountAccountDetail"));
		}else{
			this.button_JountAccountDetailList_NewJountAccountDetail.setVisible(false);
		}	
		this.button_JountAccountDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailList_PrintList"));
	logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param JointAccountDetail (aJountAccountDetail)
	 * @throws Exception
	 */
	private void showDetailView(JointAccountDetail aJountAccountDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("jountAccountDetail", aJountAccountDetail);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the JountAccountDetailListbox from the
		 * dialog when we do a delete, edit or insert a JountAccountDetail.
		 */
		map.put("jountAccountDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/JountAccountDetail/JountAccountDetailDialog.zul",null,map);
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
		this.searchObj = new JdbcSearchObject<JointAccountDetail>(JointAccountDetail.class,getListRows());
		this.searchObj.addSort("JointAccountId", false);
		this.searchObj.addTabelName("FinJointAccountDetails_View");
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		this.searchObj.addField("custCIF");
		this.searchObj.addField("repayAccountId");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			
			if (isFirstTask() && this.moduleType==null) {
				button_JountAccountDetailList_NewJountAccountDetail.setVisible(true);
			} else {
				button_JountAccountDetailList_NewJountAccountDetail.setVisible(false);
			}
			
			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("FinJointAccountDetails_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("FinJointAccountDetails_AView");
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
		getPagedListWrapper().init(this.searchObj,this.listBoxJountAccountDetail,this.pagingJountAccountDetailList);

		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	

	public JdbcSearchObject<JointAccountDetail> getSearchObj() {
		return this.searchObj;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(
			JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public void setSearchObj(JdbcSearchObject<JointAccountDetail> searchObj) {
		this.searchObj = searchObj;
	}
}