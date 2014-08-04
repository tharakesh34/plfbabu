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
 * FileName    		:  FinanceRepayPriorityListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-03-2012    														*
 *                                                                  						*
 * Modified Date    :  16-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-03-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financerepaypriority;


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
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.service.finance.FinanceRepayPriorityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financerepaypriority.model.FinanceRepayPriorityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceRepayPriority/FinanceRepayPriorityList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceRepayPriorityListCtrl extends GFCBaseListCtrl<FinanceRepayPriority> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceRepayPriorityListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceRepayPriorityList; // autowired
	protected Borderlayout borderLayout_FinanceRepayPriorityList; // autowired
	protected Paging pagingFinanceRepayPriorityList; // autowired
	protected Listbox listBoxFinanceRepayPriority; // autowired

	// List headers
	protected Listheader listheader_FinType; // autowired
	protected Listheader listheader_FinPriority; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;
	
	//Search
	protected Textbox finType; // autowired
	protected Listbox sortOperator_finType; // autowired
  	protected Intbox finPriority; // autowired
  	protected Listbox sortOperator_finPriority; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_FinanceRepayPrioritySearch_RecordStatus; // autowired
	protected Label label_FinanceRepayPrioritySearch_RecordType; // autowired
	protected Label label_FinanceRepayPrioritySearchResult; // autowired
	
	protected Grid	                       searchGrid;	
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;

	private transient boolean	           approvedList	    = false;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_FinanceRepayPriorityList_NewFinanceRepayPriority; // autowired
	protected Button button_FinanceRepayPriorityList_FinanceRepayPrioritySearchDialog; // autowired
	protected Button button_FinanceRepayPriorityList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceRepayPriority> searchObj;
	
	private transient FinanceRepayPriorityService financeRepayPriorityService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceRepayPriorityListCtrl() {
		super();
	}

	public void onCreate$window_FinanceRepayPriorityList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceRepayPriority");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceRepayPriority");
			
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
	
		this.sortOperator_finPriority.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finPriority.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
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
			this.label_FinanceRepayPrioritySearch_RecordStatus.setVisible(false);
			this.label_FinanceRepayPrioritySearch_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_FinanceRepayPriorityList.setHeight(getBorderLayoutHeight());
		this.listBoxFinanceRepayPriority.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingFinanceRepayPriorityList.setPageSize(getListRows());
		this.pagingFinanceRepayPriorityList.setDetailed(true);

		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		this.listheader_FinPriority.setSortAscending(new FieldComparator("finPriority", true));
		this.listheader_FinPriority.setSortDescending(new FieldComparator("finPriority", false));
		
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
		this.listBoxFinanceRepayPriority.setItemRenderer(new FinanceRepayPriorityListModelItemRenderer());
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_FinanceRepayPriorityList_NewFinanceRepayPriority.setVisible(false);
			this.button_FinanceRepayPriorityList_FinanceRepayPrioritySearchDialog.setVisible(false);
			this.button_FinanceRepayPriorityList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
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
		getUserWorkspace().alocateAuthorities("FinanceRepayPriorityList");
		
		this.button_FinanceRepayPriorityList_NewFinanceRepayPriority.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityList_NewFinanceRepayPriority"));
		this.button_FinanceRepayPriorityList_FinanceRepayPrioritySearchDialog.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityList_FinanceRepayPriorityFindDialog"));
		this.button_FinanceRepayPriorityList_PrintList.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.finance.financerepaypriority.model.FinanceRepayPriorityListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onFinanceRepayPriorityItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected FinanceRepayPriority object
		final Listitem item = this.listBoxFinanceRepayPriority.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceRepayPriority aFinanceRepayPriority = (FinanceRepayPriority) item.getAttribute("data");
			final FinanceRepayPriority financeRepayPriority = getFinanceRepayPriorityService().getFinanceRepayPriorityById(aFinanceRepayPriority.getId());
			
			if(financeRepayPriority==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceRepayPriority.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinType")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinType='"+ financeRepayPriority.getFinType()+"' AND version=" + financeRepayPriority.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinanceRepayPriority", whereCond, financeRepayPriority.getTaskId(), financeRepayPriority.getNextTaskId());
					if (userAcces){
						showDetailView(financeRepayPriority);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(financeRepayPriority);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the FinanceRepayPriority dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceRepayPriorityList_NewFinanceRepayPriority(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new FinanceRepayPriority object, We GET it from the backend.
		final FinanceRepayPriority aFinanceRepayPriority = getFinanceRepayPriorityService().getNewFinanceRepayPriority();
		aFinanceRepayPriority.setFinPriority(0);
		showDetailView(aFinanceRepayPriority);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param FinanceRepayPriority (aFinanceRepayPriority)
	 * @throws Exception
	 */
	private void showDetailView(FinanceRepayPriority aFinanceRepayPriority) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aFinanceRepayPriority.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceRepayPriority.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeRepayPriority", aFinanceRepayPriority);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the FinanceRepayPriorityListbox from the
		 * dialog when we do a delete, edit or insert a FinanceRepayPriority.
		 */
		map.put("financeRepayPriorityListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceRepayPriority/FinanceRepayPriorityDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_FinanceRepayPriorityList);
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
		this.sortOperator_finPriority.setSelectedIndex(0);
		this.finPriority.setText("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		/*this.pagingFinanceRepayPriorityList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FinanceRepayPriorityList, event);
		this.window_FinanceRepayPriorityList.invalidate();*/
		logger.debug("Leaving");
	}

	/*
	 * call the FinanceRepayPriority dialog
	 */
	
	public void onClick$button_FinanceRepayPriorityList_FinanceRepayPrioritySearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * When the financeRepayPriority print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_FinanceRepayPriorityList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		PTListReportUtils reportUtils = new PTListReportUtils("FinanceRepayPriority", getSearchObj(),this.pagingFinanceRepayPriorityList.getTotalSize()+1);
		logger.debug("Leaving");
	}
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceRepayPriority>(FinanceRepayPriority.class,getListRows());
		this.searchObj.addSort("FinType", false);
		this.searchObj.addTabelName("FinRpyPriority_View");
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_FinanceRepayPriorityList_NewFinanceRepayPriority.setVisible(true);
			} else {
				button_FinanceRepayPriorityList_NewFinanceRepayPriority.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("FinRpyPriority_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("FinRpyPriority_AView");
		}
		// System Internal A/c code
		if (!StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finType.getSelectedItem(), this.finType.getValue(), "finType");
		}
		
		// System Internal A/c Name
		if (this.finPriority.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finPriority.getSelectedItem(), this.finPriority.getValue(), "finPriority");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxFinanceRepayPriority, this.pagingFinanceRepayPriorityList);
		logger.debug("Leaving" );
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceRepayPriorityService(FinanceRepayPriorityService financeRepayPriorityService) {
		this.financeRepayPriorityService = financeRepayPriorityService;
	}

	public FinanceRepayPriorityService getFinanceRepayPriorityService() {
		return this.financeRepayPriorityService;
	}

	public JdbcSearchObject<FinanceRepayPriority> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceRepayPriority> searchObj) {
		this.searchObj = searchObj;
	}
}