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
 * FileName    		:  CheckListListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.checklist;


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
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.service.applicationmaster.CheckListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.applicationmaster.checklist.model.CheckListListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CheckList/CheckListList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CheckListListCtrl extends GFCBaseListCtrl<CheckList> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CheckListListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CheckListList;                         // autoWired
	protected Borderlayout borderLayout_CheckListList;             // autoWired
	protected Paging pagingCheckListList;                          // autoWired
	protected Listbox listBoxCheckList;                            // autoWired

	// List headers
	protected Listheader listheader_CheckListDesc;                 // autoWired
	protected Listheader listheader_Active;                        // autoWired
	protected Listheader listheader_RecordStatus;                  // autoWired
	protected Listheader listheader_RecordType;                    // autoWired
	protected Listheader listheader_CheckListMaxCount;             // autoWired
	protected Listheader listheader_CheckListMinCount;             // autoWired

	
	// Filtering Fields
	
	protected Textbox checkListDesc; 					// autowired
	protected Listbox sortOperator_checkListDesc; 		// autowired
  	protected Intbox checkMinCount; 					// autowired
  	protected Listbox sortOperator_checkMinCount; 		// autowired
  	protected Intbox checkMaxCount; 					// autowired
  	protected Listbox sortOperator_checkMaxCount; 		// autowired
	protected Checkbox active; 							// autowired
	protected Listbox sortOperator_active; 				// autowired
	protected Textbox recordStatus; 					// autowired
	protected Listbox recordType;						// autowired
	protected Listbox sortOperator_recordStatus; 		// autowired
	protected Listbox sortOperator_recordType; 			// autowired
	
	protected Label label_CheckListSearch_RecordStatus; // autowired
	protected Label label_CheckListSearch_RecordType; 	// autowired
	protected Label label_CheckListSearchResult; 		// autowired

	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false;
	
	// checkRights
	protected Button btnHelp;                                       // autoWired
	protected Button button_CheckListList_NewCheckList;             // autoWired
	protected Button button_CheckListList_CheckListSearchDialog;    // autoWired
	protected Button button_CheckListList_PrintList;                // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CheckList> searchObj;
	
	private transient CheckListService checkListService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CheckListListCtrl() {
		super();
	}

	public void onCreate$window_CheckListList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CheckList");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CheckList");
			
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
		this.sortOperator_checkListDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_checkListDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_checkMinCount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_checkMinCount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_checkMaxCount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_checkMaxCount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_active.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_active.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_CheckListSearch_RecordStatus.setVisible(false);
			this.label_CheckListSearch_RecordType.setVisible(false);
		}
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_CheckListList.setHeight(getBorderLayoutHeight());
		this.listBoxCheckList.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 
		
		// set the paging parameters
		this.pagingCheckListList.setPageSize(getListRows());
		this.pagingCheckListList.setDetailed(true);

		this.listheader_CheckListDesc.setSortAscending(new FieldComparator("checkListDesc", true));
		this.listheader_CheckListDesc.setSortDescending(new FieldComparator("checkListDesc", false));
		this.listheader_Active.setSortAscending(new FieldComparator("active", true));
		this.listheader_Active.setSortDescending(new FieldComparator("active", false));
		this.listheader_CheckListMinCount.setSortAscending(new FieldComparator("checkMinCount", true));
		this.listheader_CheckListMinCount.setSortDescending(new FieldComparator("checkMinCount", false));
		this.listheader_CheckListMaxCount.setSortAscending(new FieldComparator("checkMaxCount", true));
		this.listheader_CheckListMaxCount.setSortDescending(new FieldComparator("checkMaxCount", false));
		
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
		this.listBoxCheckList.setItemRenderer(new CheckListListModelItemRenderer());
		
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CheckListList_NewCheckList.setVisible(false);
			this.button_CheckListList_CheckListSearchDialog.setVisible(false);
			this.button_CheckListList_PrintList.setVisible(false);
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
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CheckListList");
		this.button_CheckListList_NewCheckList.setVisible(getUserWorkspace().isAllowed("button_CheckListList_NewCheckList"));
		this.button_CheckListList_CheckListSearchDialog.setVisible(getUserWorkspace().isAllowed("button_CheckListList_CheckListFindDialog"));
		this.button_CheckListList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CheckListList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.checklist.model.CheckListListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCheckListItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected CheckList object
		final Listitem item = this.listBoxCheckList.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CheckList aCheckList = (CheckList) item.getAttribute("data");
			final CheckList checkList = getCheckListService().getCheckListById(aCheckList.getId());
			
			if(checkList==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aCheckList.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_CheckListId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND CheckListId="+ checkList.getCheckListId()+" AND version=" + checkList.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CheckList", whereCond, checkList.getTaskId(), checkList.getNextTaskId());
					if (userAcces){
						showDetailView(checkList);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(checkList);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the CheckList dialog with a new empty entry. <br>
	 */
	public void onClick$button_CheckListList_NewCheckList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CheckList object, We GET it from the backEnd.
		final CheckList aCheckList = getCheckListService().getNewCheckList();
		showDetailView(aCheckList);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch(){
     logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<CheckList>(CheckList.class,getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("checkListDesc", false);
		
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTCheckList_View");

			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
				
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		
		if(approvedList){
			this.searchObj.addTabelName("BMTCheckList_AView");
		}else{
			this.searchObj.addTabelName("BMTCheckList_View");
		}
		
		//check List Description
		if (!StringUtils.trimToEmpty(this.checkListDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_checkListDesc.getSelectedItem(),this.checkListDesc.getValue(), "checkListDesc");
		}
		//check List Minimum Count
		if (this.checkMinCount.intValue()!=0) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_checkMinCount.getSelectedItem(),this.checkMinCount.getValue(), "checkMinCount");
		}
		
		//check List Maximum Count
		if (this.checkMaxCount.intValue()!=0) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_checkMaxCount.getSelectedItem(),this.checkMaxCount.getValue(), "checkMaxCount");
		}
		
		// Check List is Active or Inactive
		if (active.isChecked()) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_active.getSelectedItem(), 1,"active");
		} else {
			searchObj = getSearchFilter(searchObj,this.sortOperator_active.getSelectedItem(), 0,"active");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxCheckList,this.pagingCheckListList);

		logger.debug("Leaving");
	}
	
	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param CheckList (aCheckList)
	 * @throws Exception
	 */
	private void showDetailView(CheckList aCheckList) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCheckList.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCheckList.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("checkList", aCheckList);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CheckListListbox from the
		 * dialog when we do a delete, edit or insert a CheckList.
		 */
		map.put("checkListListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CheckList/CheckListDialog.zul",null,map);
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
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_CheckListList);
		logger.debug("Leaving");
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
		
		this.sortOperator_checkListDesc.setSelectedIndex(0);
		this.checkListDesc.setValue("");
		this.sortOperator_checkMaxCount.setSelectedIndex(0);
		this.checkMaxCount.setValue(null);
		this.sortOperator_checkMinCount.setSelectedIndex(0);
		this.checkMinCount.setValue(null);
        this.sortOperator_active.setSelectedIndex(0);
        this.active.setChecked(false);
         
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		logger.debug("Leaving");
	}

	/*
	 * call the CheckList dialog
	 */
	
	public void onClick$button_CheckListList_CheckListSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		   doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the checkList print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CheckListList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		PTListReportUtils reportUtils = new PTListReportUtils("CheckList", getSearchObj(),this.pagingCheckListList.getTotalSize() + 1);
		logger.debug("Leaving");
	}

	public void setCheckListService(CheckListService checkListService) {
		this.checkListService = checkListService;
	}

	public CheckListService getCheckListService() {
		return this.checkListService;
	}

	public JdbcSearchObject<CheckList> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CheckList> searchObj) {
		this.searchObj = searchObj;
	}
}