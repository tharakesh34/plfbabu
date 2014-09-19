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
 * FileName    		:  QueryListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-07-2013    														*
 *                                                                  						*
 * Modified Date    :  04-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-07-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.query;

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
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.service.applicationmaster.QueryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.applicationmaster.query.model.QueryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Query/QueryList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class QueryListCtrl extends GFCBaseListCtrl<Query> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(QueryListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_QueryList; // autowired
	protected Borderlayout borderLayout_QueryList; // autowired
	protected Paging pagingQueryList; // autowired
	protected Listbox listBoxQuery; // autowired

	// List headers
	protected Listheader listheader_QueryCode; // autowired
	protected Listheader listheader_QueryModule; // autowired
	protected Listheader listheader_QueryDesc; // autowired
	protected Listheader listheader_SubQuery; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;
	protected Listheader listheader_Active;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_QueryList_NewQuery; // autowired
	protected Button button_QueryList_QuerySearch; // autowired
	protected Button button_QueryList_PrintList; // autowired
	protected Label  label_QueryList_RecordStatus; 							// autoWired
	protected Label  label_QueryList_RecordType; 							// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Query> searchObj;

	private transient QueryService queryService;
	private transient WorkFlowDetails workFlowDetails=null;


	protected Textbox queryCode; // autowired
	protected Listbox sortOperator_QueryCode; // autowired

	protected Combobox queryModule; // autowired
	protected Listbox sortOperator_QueryModule; // autowired

	protected Textbox queryDesc; // autowired
	protected Listbox sortOperator_QueryDesc; // autowired

	protected Checkbox subQuery; // autowired
	protected Listbox sortOperator_SubQuery; // autowired
	
	protected Checkbox active; 
	protected Listbox sortOperator_Active;

	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_RecordStatus; // autowired
	protected Listbox sortOperator_RecordType; // autowired
	protected Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;
	protected Comboitem comboitem;
	protected Space space;

	private transient boolean  approvedList=false;


	/**
	 * default constructor.<br>
	 */
	public QueryListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_QueryList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Query");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Query");
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

		this.sortOperator_QueryCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_QueryCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_QueryModule.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		fillComboBox(this.queryModule,"",PennantAppUtil.getQueryModuleByValueLabel(),"");

		this.sortOperator_QueryModule.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_QueryDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_QueryDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_SubQuery.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_SubQuery.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_Active.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_Active.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_QueryList_RecordStatus.setVisible(false);
			this.label_QueryList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_QueryList.setHeight(getBorderLayoutHeight());
		this.listBoxQuery.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 

		// set the paging parameters
		this.pagingQueryList.setPageSize(getListRows());
		this.pagingQueryList.setDetailed(true);

		this.listheader_QueryCode.setSortAscending(new FieldComparator("queryCode", true));
		this.listheader_QueryCode.setSortDescending(new FieldComparator("queryCode", false));
		this.listheader_QueryModule.setSortAscending(new FieldComparator("queryModule", true));
		this.listheader_QueryModule.setSortDescending(new FieldComparator("queryModule", false));
		this.listheader_QueryDesc.setSortAscending(new FieldComparator("queryDesc", true));
		this.listheader_QueryDesc.setSortDescending(new FieldComparator("queryDesc", false));
		this.listheader_SubQuery.setSortAscending(new FieldComparator("subQuery", true));
		this.listheader_SubQuery.setSortDescending(new FieldComparator("subQuery", false));
		this.listheader_Active.setSortAscending(new FieldComparator("active", true));
		this.listheader_Active.setSortDescending(new FieldComparator("active", false));
		// set the itemRenderer
		this.listBoxQuery.setItemRenderer(new QueryListModelItemRenderer());

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
			this.button_QueryList_NewQuery.setVisible(false);
			this.button_QueryList_QuerySearch.setVisible(false);
			this.button_QueryList_PrintList.setVisible(false);
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
	 * see: com.pennant.webui.applicationmaster.query.model.QueryListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onQueryItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected Query object
		final Listitem item = this.listBoxQuery.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Query aQuery = (Query) item.getAttribute("data");
			Query query = null;
			
			if(approvedList){
				query = getQueryService().getApprovedQueryById(aQuery);				
			}else{
				query = getQueryService().getQueryById(aQuery);
			}

			if(query==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aQuery.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_QueryCode")+":"+valueParm[0];
				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(query.getWorkflowId()==0){
						query.setWorkflowId(workFlowDetails.getWorkFlowId());
					}
					String whereCond = " AND QueryCode='" + query.getQueryCode() 
							+ "'";
					//WorkflowLoad flowLoad= new WorkflowLoad(query.getWorkflowId(), query.getNextTaskId(), getUserWorkspace().getUserRoleSet());
					//boolean userAcces =  validateUserAccess("Query", new String[] {"QueryCode"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails(), query);
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Query", whereCond, query.getTaskId(), query.getNextTaskId());
					if (userAcces){
						showDetailView(query,aQuery.isSubQuery());
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(query,aQuery.isSubQuery());
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the Query dialog with a new empty entry. <br>
	 */
	public void onClick$button_QueryList_NewQuery(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new Query object, We GET it from the backend.
		final Query aQuery = getQueryService().getNewQuery();
		
		showDetailView(aQuery,aQuery.isSubQuery());
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */

	public void onClick$button_QueryList_QuerySearch(Event event) throws Exception {
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
		this.pagingQueryList.setActivePage(0);
		Events.postEvent("onCreate", this.window_QueryList, event);
		this.window_QueryList.invalidate();


		this.sortOperator_QueryCode.setSelectedIndex(0);
		this.queryCode.setValue("");
		this.sortOperator_QueryModule.setSelectedIndex(0);
		this.queryModule.setSelectedIndex(0);
		this.sortOperator_QueryDesc.setSelectedIndex(0);
		this.queryDesc.setValue("");
		this.sortOperator_SubQuery.setSelectedIndex(0);
		this.subQuery.setChecked(false);
		this.sortOperator_Active.setSelectedIndex(0);
		this.active.setChecked(false);

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
		PTMessageUtils.showHelpWindow(event, window_QueryList);
		logger.debug("Leaving");
	}

	/**
	 * When the query print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_QueryList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("Query", getSearchObj());
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
		getUserWorkspace().alocateAuthorities("QueryList");

		if(moduleType==null){
			this.button_QueryList_NewQuery.setVisible(getUserWorkspace().isAllowed("button_QueryList_NewQuery"));
		}else{
			this.button_QueryList_NewQuery.setVisible(false);
		}	
		this.button_QueryList_PrintList.setVisible(getUserWorkspace().isAllowed("button_QueryList_PrintList"));
		logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Query (aQuery)
	 * @throws Exception
	 */
	private void showDetailView(Query aQuery,boolean isSubQuery) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("query", aQuery);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the QueryListbox from the
		 * dialog when we do a delete, edit or insert a Query.
		 */
		map.put("queryListCtrl", this);
		map.put("subquery", isSubQuery);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Query/QueryDialog.zul",null,map);
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
		this.searchObj = new JdbcSearchObject<Query>(Query.class,getListRows());
		this.searchObj.addSort("QueryCode", false);
		this.searchObj.addTabelName("Queries_View");

		this.searchObj.addField("queryCode");
		this.searchObj.addField("queryModule");
		this.searchObj.addField("queryDesc");
		this.searchObj.addField("subQuery");
		this.searchObj.addField("active");
		if (isWorkFlowEnabled()) {
			this.searchObj.addField("recordStatus");
			this.searchObj.addField("recordType");
		}
		// Workflow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType==null) {
				button_QueryList_NewQuery.setVisible(true);
			} else {
				button_QueryList_NewQuery.setVisible(false);
			}

			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("Queries_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("Queries_AView");
		}

		// Query Code
		if (!StringUtils.trimToEmpty(this.queryCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_QueryCode.getSelectedItem(), this.queryCode.getValue(), "QueryCode");
		}
		// Query Module
		if (this.queryModule.getSelectedItem()!=null && !PennantConstants.List_Select.equals(this.queryModule.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_QueryModule.getSelectedItem(), this.queryModule.getSelectedItem().getLabel().toString(), "QueryModule");
		}
		// Query Desc
		if (!StringUtils.trimToEmpty(this.queryDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_QueryDesc.getSelectedItem(), this.queryDesc.getValue(), "QueryDesc");
		}
		// Sub Query
		int intSubQuery=0;
		if(this.subQuery.isChecked()){
			intSubQuery=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_SubQuery.getSelectedItem(),intSubQuery, "SubQuery");
		// Active
		int intActive=0;
		if(this.active.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_Active.getSelectedItem(),intActive, "Active");
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem()!=null && !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
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
		getPagedListWrapper().init(this.searchObj,this.listBoxQuery,this.pagingQueryList);

		logger.debug("Leaving");
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

	public QueryService getQueryService() {
		return this.queryService;
	}

	public JdbcSearchObject<Query> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Query> searchObj) {
		this.searchObj = searchObj;
	}
}