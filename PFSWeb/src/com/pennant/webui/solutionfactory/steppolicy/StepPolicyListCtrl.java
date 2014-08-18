/**
Copyright 2011 - Pennant Technologies
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
 * FileName    		:  StepPolicyHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.solutionfactory.steppolicy;

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
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.solutionfactory.steppolicy.model.StepPolicyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/StepPolicyHeader/StepPolicyHeaderList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class StepPolicyListCtrl extends GFCBaseListCtrl<StepPolicyHeader> implements Serializable {

	private static final long serialVersionUID = -1491703348215991538L;
	private final static Logger logger = Logger.getLogger(StepPolicyListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the ZUL-file are getting autoWired by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_StepPolicyList; // autoWired
	protected Borderlayout borderLayout_StepPolicyList; // autoWired
	protected Paging pagingStepPolicyList; 				// autowired
	protected Listbox listBoxStepPolicy; // autoWired

	// List headers
	protected Listheader listheader_PolicyCode; // autoWired
	protected Listheader listheader_PolicyDesc; // autoWired
	protected Listheader listheader_Steps; // autoWired
	protected Listheader listheader_TenorSplit; // autoWired
	protected Listheader listheader_RateStepMargin; // autoWired
	protected Listheader listheader_EMISteps; // autoWired
	protected Listheader listheader_RecordStatus; // autoWired
	protected Listheader listheader_RecordType;

	// Filtering Fields
	protected  Listbox   sortOperator_policyCode;
	protected  Textbox   policyCode;
	protected  Listbox   sortOperator_policyDesc;
	protected  Textbox   policyDesc;
	protected  Listbox  sortOperator_recordStatus;
	protected  Textbox  recordStatus;
	protected  Listbox  sortOperator_recordType;
	protected  Listbox  recordType;
	
	protected Label label_StepPolicySearch_RecordStatus; 		// autowired

	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false; 
	// checkRights
	protected Button btnHelp; // autoWired
	protected Button button_StepPolicyList_NewStepPolicy; // autoWired
	protected Button button_StepPolicyList_StepPolicySearchDialog; // autoWired
	protected Button button_StepPolicyList_PrintList; // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<StepPolicyHeader> searchObj;
	private transient PagedListService pagedListService;
	private transient StepPolicyService stepPolicyService;
	private transient WorkFlowDetails workFlowDetails = null;


	/**
	 * default constructor.<br>
	 */
	public StepPolicyListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected StepPolicyHeader object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_StepPolicyList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("StepPolicyHeader");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("StepPolicyHeader");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_policyCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_policyCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_policyDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_policyDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
			this.label_StepPolicySearch_RecordStatus.setVisible(false);
		}


		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the currentDesktopHeight from a hidden IntBox
		 * from the index.zul that are filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_StepPolicyList.setHeight(getBorderLayoutHeight());
		// set the paging params
		this.pagingStepPolicyList.setPageSize(getListRows());
		this.pagingStepPolicyList.setDetailed(true);
		this.listBoxStepPolicy.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters

		this.listheader_PolicyCode.setSortAscending(new FieldComparator("policyCode", true));
		this.listheader_PolicyCode.setSortDescending(new FieldComparator("policyCode", false));
		this.listheader_PolicyDesc.setSortAscending(new FieldComparator("policyDesc", true));
		this.listheader_PolicyDesc.setSortDescending(new FieldComparator("policyDesc", false));
		this.listheader_Steps.setSortAscending(new FieldComparator("stepNumber", true));
		this.listheader_Steps.setSortDescending(new FieldComparator("stepNumber", false));
		this.listheader_TenorSplit.setSortAscending(new FieldComparator("tenorSplitPerc", true));
		this.listheader_TenorSplit.setSortDescending(new FieldComparator("tenorSplitPerc", false));
		this.listheader_RateStepMargin.setSortAscending(new FieldComparator("rateMargin", true));
		this.listheader_RateStepMargin.setSortDescending(new FieldComparator("rateMargin", false));
		this.listheader_EMISteps.setSortAscending(new FieldComparator("emiSplitPerc", true));
		this.listheader_EMISteps.setSortDescending(new FieldComparator("emiSplitPerc", false));

		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_StepPolicyList_NewStepPolicy.setVisible(true);
			} else {
				button_StepPolicyList_NewStepPolicy.setVisible(false);
			}
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// set the itemRenderer
		this.listBoxStepPolicy.setItemRenderer(new StepPolicyListModelItemRenderer());

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_StepPolicyList_NewStepPolicy.setVisible(false);
			this.button_StepPolicyList_StepPolicySearchDialog.setVisible(false);
			this.button_StepPolicyList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}


	
	/**
	 * Internal Method for Grouping List items
	 */
	public void findSearchObject(){
		logger.debug("Entering");
		getPagedListWrapper().init(this.searchObj, this.listBoxStepPolicy,this.pagingStepPolicyList);
		this.listBoxStepPolicy.setItemRenderer(new StepPolicyListModelItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("StepPolicyList");
		this.button_StepPolicyList_NewStepPolicy.setVisible(getUserWorkspace().isAllowed("button_StepPolicyList_NewStepPolicy"));
		this.button_StepPolicyList_StepPolicySearchDialog.setVisible(getUserWorkspace().isAllowed("button_StepPolicyList_StepPolicySearchDialog"));
		this.button_StepPolicyList_PrintList.setVisible(getUserWorkspace().isAllowed("button_StepPolicyList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the ListBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.financetype.model. StepPolicyHeaderListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onStepPolicyHeaderItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected StepPolicyHeader object
		final Listitem item = this.listBoxStepPolicy.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final StepPolicyHeader aStepPolicyHeader = (StepPolicyHeader) item.getAttribute("data");
			final StepPolicyHeader stepPolicyHeader = getStepPolicyService().getStepPolicyHeaderById(aStepPolicyHeader.getId());
			if (stepPolicyHeader == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];
				valueParm[0] = aStepPolicyHeader.getPolicyCode();

				errParm[0] = PennantJavaUtil.getLabel("label_PolicyCode") + ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace()
						.getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());

			} else {

				String whereCond = " AND PolicyCode='" + stepPolicyHeader.getPolicyCode() + "' AND version=" + stepPolicyHeader.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "StepPolicyHeader", whereCond,
							stepPolicyHeader.getTaskId(), stepPolicyHeader.getNextTaskId());
					if (userAcces) {
						showDetailView(stepPolicyHeader);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(stepPolicyHeader);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the StepPolicyHeader dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_StepPolicyList_NewStepPolicy(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final StepPolicyHeader aStepPolicyHeader = getStepPolicyService().getNewStepPolicyHeader();
		showDetailView(aStepPolicyHeader);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param StepPolicyHeader
	 *            (aStepPolicyHeader)
	 * @throws Exception
	 */
	private void showDetailView(StepPolicyHeader aStepPolicyHeader) throws Exception {
		logger.debug("Entering");
		
		if (aStepPolicyHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aStepPolicyHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("stepPolicyHeader", aStepPolicyHeader);
		map.put("stepPolicyListCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/StepPolicy/StepPolicyDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_StepPolicyList);
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
		this.sortOperator_policyCode.setSelectedIndex(0);
		this.policyCode.setValue("");
		this.sortOperator_policyDesc.setSelectedIndex(0);
		this.policyDesc.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the StepPolicyHeader dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_StepPolicyList_StepPolicySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the stepPolicyHeader print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_StepPolicyList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("StepPolicyHeader", getSearchObj(),-1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch(){

		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<StepPolicyHeader>(StepPolicyHeader.class,-1);

		// Default Sort on the table
		this.searchObj.addSort("PolicyCode", false);

		this.searchObj.addField("PolicyCode");
		this.searchObj.addField("PolicyDesc");
		this.searchObj.addField("StepNumber");
		this.searchObj.addField("TenorSplitPerc");
		this.searchObj.addField("RateMargin");
		this.searchObj.addField("EMISplitPerc");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");
		
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("StepPolicyHeaderDetails_View");

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
			this.searchObj.addTabelName("StepPolicyHeaderDetails_AView");
		}else{
			this.searchObj.addTabelName("StepPolicyHeaderDetails_View");
		}

		//Policy Code
		if (!StringUtils.trimToEmpty(this.policyCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_policyCode.getSelectedItem(), this.policyCode.getValue() , "policyCode");
		}
		//Policy Description
		if (!StringUtils.trimToEmpty(this.policyDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_policyDesc.getSelectedItem(), this.policyDesc.getValue() , "policyDesc");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
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
		
		findSearchObject();

		logger.debug("Leaving");

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//



	public JdbcSearchObject<StepPolicyHeader> getSearchObj() {
		return this.searchObj;
	}
	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}

	public void setSearchObj(JdbcSearchObject<StepPolicyHeader> searchObj) {
		this.searchObj = searchObj;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	public PagedListService getPagedListService() {
		return pagedListService;
	}

}