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
 * FileName    		:  AuthorizationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2013    														*
 *                                                                  						*
 * Modified Date    :  20-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.authorization;

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
import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.service.amtmasters.AuthorizationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.amtmasters.authorization.model.AuthorizationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMasters/Authorization/AuthorizationList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AuthorizationListCtrl extends GFCBaseListCtrl<Authorization> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(AuthorizationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_AuthorizationList; // autowired
	protected Borderlayout borderLayout_AuthorizationList; // autowired
	protected Paging pagingAuthorizationList; // autowired
	protected Listbox listBoxAuthorization; // autowired

	// List headers
	protected Listheader listheader_AuthType; // autowired
	protected Listheader listheader_AuthName; // autowired
	protected Listheader listheader_AuthDept; // autowired
	protected Listheader listheader_AuthDesig; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_AuthorizationList_NewAuthorization; // autowired
	protected Button button_AuthorizationList_AuthorizationSearch; // autowired
	protected Button button_AuthorizationList_PrintList; // autowired
	protected Label  label_AuthorizationList_RecordStatus; 							// autoWired
	protected Label  label_AuthorizationList_RecordType; 							// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Authorization> searchObj;
	
	private transient AuthorizationService authorizationService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	
    protected Combobox authType; // autowired
	protected Listbox sortOperator_AuthType; // autowired

	protected Textbox authName; // autowired
	protected Listbox sortOperator_AuthName; // autowired

	protected Textbox authDept; // autowired
	protected Listbox sortOperator_AuthDept; // autowired

	protected Textbox authDesig; // autowired
	protected Listbox sortOperator_AuthDesig; // autowired

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
	public AuthorizationListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_AuthorizationList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Authorization");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Authorization");
			
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
	
      	this.sortOperator_AuthType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
      	fillComboBox(this.authType,"",PennantStaticListUtil.getAuthTypes(),"");
		this.sortOperator_AuthType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_AuthName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_AuthName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_AuthDept.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_AuthDept.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_AuthDesig.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_AuthDesig.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_AuthorizationList_RecordStatus.setVisible(false);
			this.label_AuthorizationList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_AuthorizationList.setHeight(getBorderLayoutHeight());
		this.listBoxAuthorization.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 
		
		// set the paging parameters
		this.pagingAuthorizationList.setPageSize(getListRows());
		this.pagingAuthorizationList.setDetailed(true);

		this.listheader_AuthType.setSortAscending(new FieldComparator("authType", true));
		this.listheader_AuthType.setSortDescending(new FieldComparator("authType", false));
		this.listheader_AuthName.setSortAscending(new FieldComparator("authName", true));
		this.listheader_AuthName.setSortDescending(new FieldComparator("authName", false));
		this.listheader_AuthDept.setSortAscending(new FieldComparator("authDept", true));
		this.listheader_AuthDept.setSortDescending(new FieldComparator("authDept", false));
		this.listheader_AuthDesig.setSortAscending(new FieldComparator("authDesig", true));
		this.listheader_AuthDesig.setSortDescending(new FieldComparator("authDesig", false));
		// set the itemRenderer
		this.listBoxAuthorization.setItemRenderer(new AuthorizationListModelItemRenderer());
		
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
			this.button_AuthorizationList_NewAuthorization.setVisible(false);
			this.button_AuthorizationList_AuthorizationSearch.setVisible(false);
			this.button_AuthorizationList_PrintList.setVisible(false);
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
	 * see: com.pennant.webui.amtmasters.authorization.model.AuthorizationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onAuthorizationItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected Authorization object
		final Listitem item = this.listBoxAuthorization.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Authorization aAuthorization = (Authorization) item.getAttribute("data");
			Authorization authorization = null;
			if(approvedList){
				authorization = getAuthorizationService().getApprovedAuthorizationById(aAuthorization.getId());
			}else{
				authorization = getAuthorizationService().getAuthorizationById(aAuthorization.getId());
			}
			
			if(authorization==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aAuthorization.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_AuthorizedId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(authorization.getWorkflowId()==0){
						authorization.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					WorkflowLoad flowLoad= new WorkflowLoad(authorization.getWorkflowId(), authorization.getNextTaskId(), getUserWorkspace().getUserRoleSet());
					
					boolean userAcces =  validateUserAccess("Authorization", new String[] {"AuthorizedId"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), authorization);
					if (userAcces){
						showDetailView(authorization);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(authorization);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the Authorization dialog with a new empty entry. <br>
	 */
	public void onClick$button_AuthorizationList_NewAuthorization(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new Authorization object, We GET it from the backend.
		final Authorization aAuthorization = getAuthorizationService().getNewAuthorization();
		showDetailView(aAuthorization);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_AuthorizationList_AuthorizationSearch(Event event) throws Exception {
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
	/*		this.pagingAuthorizationList.setActivePage(0);
			Events.postEvent("onCreate", this.window_AuthorizationList, event);
			this.window_AuthorizationList.invalidate();
	*/		

  		this.sortOperator_AuthType.setSelectedIndex(0);
		this.authType.setSelectedIndex(0);
  		this.sortOperator_AuthName.setSelectedIndex(0);
	  	this.authName.setValue("");
  		this.sortOperator_AuthDept.setSelectedIndex(0);
	  	this.authDept.setValue("");
  		this.sortOperator_AuthDesig.setSelectedIndex(0);
	  	this.authDesig.setValue("");

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
		PTMessageUtils.showHelpWindow(event, window_AuthorizationList);
		logger.debug("Leaving");
	}

	/**
	 * When the authorization print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_AuthorizationList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("Authorization", getSearchObj());
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
		getUserWorkspace().alocateAuthorities("AuthorizationList");
		
		if(moduleType==null){
			this.button_AuthorizationList_NewAuthorization.setVisible(getUserWorkspace().isAllowed("button_AuthorizationList_NewAuthorization"));
		}else{
			this.button_AuthorizationList_NewAuthorization.setVisible(false);
		}	
		this.button_AuthorizationList_PrintList.setVisible(getUserWorkspace().isAllowed("button_AuthorizationList_PrintList"));
	logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Authorization (aAuthorization)
	 * @throws Exception
	 */
	private void showDetailView(Authorization aAuthorization) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("authorization", aAuthorization);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the AuthorizationListbox from the
		 * dialog when we do a delete, edit or insert a Authorization.
		 */
		map.put("authorizationListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/Authorization/AuthorizationDialog.zul",null,map);
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
		this.searchObj = new JdbcSearchObject<Authorization>(Authorization.class,getListRows());
		this.searchObj.addSort("AuthorizedId", false);
		this.searchObj.addTabelName("AMTAuthorization_View");
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		this.searchObj.addField("authorizedId");
		this.searchObj.addField("authUserId");
		this.searchObj.addField("authType");
		this.searchObj.addField("authName");
		this.searchObj.addField("authDept");
		this.searchObj.addField("authDesig");
		this.searchObj.addField("RecordType");
		this.searchObj.addField("RecordStatus");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			
			if (isFirstTask() && this.moduleType==null) {
				button_AuthorizationList_NewAuthorization.setVisible(true);
			} else {
				button_AuthorizationList_NewAuthorization.setVisible(false);
			}
			
			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("AMTAuthorization_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("AMTAuthorization_AView");
		}
		
	// Auth Type
		if (this.authType.getSelectedItem()!=null && !PennantConstants.List_Select.equals(this.authType.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_AuthType.getSelectedItem(), this.authType.getSelectedItem().getValue().toString(), "AuthType");
		}
	// Auth Name
		if (!StringUtils.trimToEmpty(this.authName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_AuthName.getSelectedItem(), this.authName.getValue(), "AuthName");
		}
	// Auth Dept
		if (!StringUtils.trimToEmpty(this.authDept.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_AuthDept.getSelectedItem(), this.authDept.getValue(), "AuthDept");
		}
	// Auth Desig
		if (!StringUtils.trimToEmpty(this.authDesig.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_AuthDesig.getSelectedItem(), this.authDesig.getValue(), "AuthDesig");
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
		getPagedListWrapper().init(this.searchObj,this.listBoxAuthorization,this.pagingAuthorizationList);

		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setAuthorizationService(AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public AuthorizationService getAuthorizationService() {
		return this.authorizationService;
	}

	public JdbcSearchObject<Authorization> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Authorization> searchObj) {
		this.searchObj = searchObj;
	}
}