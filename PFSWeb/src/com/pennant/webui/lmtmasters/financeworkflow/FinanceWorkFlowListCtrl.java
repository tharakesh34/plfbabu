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
 * FileName    		:  FinanceWorkFlowListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.financeworkflow;

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
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.lmtmasters.financeworkflow.model.FinanceWorkFlowListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceWorkFlow/FinanceWorkFlowList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceWorkFlowListCtrl extends GFCBaseListCtrl<FinanceWorkFlow> implements Serializable {

	private static final long serialVersionUID = 3938682418831790487L;

	private final static Logger logger = Logger.getLogger(FinanceWorkFlowListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinanceWorkFlowList; 		// autoWired
	protected Borderlayout 	borderLayout_FinanceWorkFlowList; 	// autoWired
	protected Paging 		pagingFinanceWorkFlowList; 			// autoWired
	protected Listbox 		listBoxFinanceWorkFlow; 			// autoWired

	// List headers
	protected Listheader listheader_FinType; 		// autoWired
	protected Listheader listheader_ScreenCode; 	// autoWired
	protected Listheader listheader_WorkFlowType; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// Filtering Fields
	protected Textbox finType; 								// autoWired
	protected Listbox sortOperator_finType; 				// autoWired
	protected Textbox screenCode; 							// autoWired
	protected Listbox sortOperator_screenCode; 				// autoWired
	protected Textbox workFlowType; 						// autoWired
	protected Listbox sortOperator_workFlowType; 			// autoWired
	protected Textbox recordStatus; 						// autoWired
	protected Listbox recordType;							// autoWired
	protected Listbox sortOperator_recordStatus; 			// autoWired
	protected Listbox sortOperator_recordType; 				// autoWired
	
	protected Row row_AlwWorkflow;
	
	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Textbox 		wfModule; 							// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false;
	private transient boolean  isPromotion = false;
	
	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_FinanceWorkFlowList_NewFinanceWorkFlow; 			// autoWired
	protected Button button_FinanceWorkFlowList_FinanceWorkFlowSearchDialog; 	// autoWired
	protected Button button_FinanceWorkFlowList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceWorkFlow> searchObj;
	
	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceWorkFlowListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceWorkFlow object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceWorkFlowList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(wfModule.getValue()+"WorkFlow");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails(wfModule.getValue()+"WorkFlow");
			
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
		
		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_screenCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_screenCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_workFlowType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_workFlowType.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.row_AlwWorkflow.setVisible(false);
		}
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_FinanceWorkFlowList.setHeight(getBorderLayoutHeight());
		this.listBoxFinanceWorkFlow.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount())); 
		
		// set the paging parameters
	    this.pagingFinanceWorkFlowList.setPageSize(getListRows());
		this.pagingFinanceWorkFlowList.setDetailed(true);

		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		this.listheader_ScreenCode.setSortAscending(new FieldComparator("screenCode", true));
		this.listheader_ScreenCode.setSortDescending(new FieldComparator("screenCode", false));
		this.listheader_WorkFlowType.setSortAscending(new FieldComparator("workFlowType", true));
		this.listheader_WorkFlowType.setSortDescending(new FieldComparator("workFlowType", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
			if (isFirstTask()) {
				button_FinanceWorkFlowList_NewFinanceWorkFlow.setVisible(true);
			   } else {
				button_FinanceWorkFlowList_NewFinanceWorkFlow.setVisible(false);
			}
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		

		// set the itemRenderer
		this.listBoxFinanceWorkFlow.setItemRenderer(new FinanceWorkFlowListModelItemRenderer());
		
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_FinanceWorkFlowList_NewFinanceWorkFlow.setVisible(false);
			this.button_FinanceWorkFlowList_FinanceWorkFlowSearchDialog.setVisible(false);
			this.button_FinanceWorkFlowList_PrintList.setVisible(false);
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
		
		String listName = "FinanceWorkFlowList";
		if(wfModule.getValue().equals("Promotion")){
			listName = "PromotionWorkFlowList";
			isPromotion = true;
		}
		
		getUserWorkspace().alocateAuthorities(listName);
		
		this.button_FinanceWorkFlowList_NewFinanceWorkFlow.setVisible(getUserWorkspace()
				.isAllowed("button_"+listName+"_NewFinanceWorkFlow"));
		this.button_FinanceWorkFlowList_FinanceWorkFlowSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_"+listName+"_FinanceWorkFlowFindDialog"));
		this.button_FinanceWorkFlowList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_"+listName+"_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.financeworkflow.model.FinanceWorkFlowListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFinanceWorkFlowItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// get the selected FinanceWorkFlow object
		final Listitem item = this.listBoxFinanceWorkFlow.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceWorkFlow aFinanceWorkFlow = (FinanceWorkFlow) item.getAttribute("data");
			final FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getFinanceWorkFlowById(aFinanceWorkFlow.getId());
			
			if(financeWorkFlow==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceWorkFlow.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinType")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinType='"+ financeWorkFlow.getFinType()+"' AND version=" + financeWorkFlow.getVersion()+" ";

					boolean userAcces =  validateUserAccess(
							workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinanceWorkFlow", whereCond, financeWorkFlow.getTaskId(), financeWorkFlow.getNextTaskId());
					if (userAcces){
						showDetailView(financeWorkFlow);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(financeWorkFlow);
				}
			}	
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Call the FinanceWorkFlow dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceWorkFlowList_NewFinanceWorkFlow(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new FinanceWorkFlow object, We GET it from the backEnd.
		final FinanceWorkFlow aFinanceWorkFlow = getFinanceWorkFlowService().getNewFinanceWorkFlow();
		showDetailView(aFinanceWorkFlow);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param FinanceWorkFlow (aFinanceWorkFlow)
	 * @throws Exception
	 */
	private void showDetailView(FinanceWorkFlow aFinanceWorkFlow) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aFinanceWorkFlow.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceWorkFlow.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeWorkFlow", aFinanceWorkFlow);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the FinanceWorkFlowListbox from the
		 * dialog when we do a delete, edit or insert a FinanceWorkFlow.
		 */
		map.put("financeWorkFlowListCtrl", this);
		map.put("isPromotion", isPromotion);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceWorkFlow/FinanceWorkFlowDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_FinanceWorkFlowList);
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
		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_screenCode.setSelectedIndex(0);
		this.screenCode.setValue("");
		this.sortOperator_workFlowType.setSelectedIndex(0);
		this.workFlowType.setValue("");
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
	 * call the FinanceWorkFlow dialog
	 */
	public void onClick$button_FinanceWorkFlowList_FinanceWorkFlowSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		   doSearch();
		logger.debug("Leaving");
	}

	/**
	 * When the financeWorkFlow print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_FinanceWorkFlowList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("FinanceWorkFlow", getSearchObj(),this.pagingFinanceWorkFlowList.getTotalSize()+1);
		logger.debug("Leaving" +event.toString());
	}
	
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceWorkFlow>(FinanceWorkFlow.class,getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("FinType", false);

		if(isPromotion){
			this.searchObj.addFilter(new Filter("lovDescProductName", "", Filter.OP_NOT_EQUAL));
		}else{
			this.searchObj.addFilter(new Filter("lovDescProductName", "", Filter.OP_EQUAL));
		}

		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("LMTFinanceWorkFlowDef_View");

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
			this.searchObj.addTabelName("LMTFinanceWorkFlowDef_AView");
		}else{
			this.searchObj.addTabelName("LMTFinanceWorkFlowDef_View");
		}

		//Finance Type
		if (!StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finType.getSelectedItem(),this.finType.getValue(), "FinType");
		}
		//Screen Code
		if (!StringUtils.trimToEmpty(this.screenCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_screenCode.getSelectedItem(),this.screenCode.getValue(), "ScreenCode");
		}
		//Screen Code
		if (!StringUtils.trimToEmpty(this.workFlowType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_workFlowType.getSelectedItem(),this.workFlowType.getValue(), "WorkFlowType");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxFinanceWorkFlow,this.pagingFinanceWorkFlowList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return this.financeWorkFlowService;
	}

	public JdbcSearchObject<FinanceWorkFlow> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceWorkFlow> searchObj) {
		this.searchObj = searchObj;
	}
}