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
 * FileName    		:  DivisionDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-08-2013    														*
 *                                                                  						*
 * Modified Date    :  02-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.divisiondetail;

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
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.systemmasters.DivisionDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.WorkflowLoad;
import com.pennant.webui.systemmasters.divisiondetail.model.DivisionDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SystemMaster/DivisionDetail/DivisionDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DivisionDetailListCtrl extends GFCBaseListCtrl<DivisionDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(DivisionDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_DivisionDetailList; // autowired
	protected Borderlayout borderLayout_DivisionDetailList; // autowired
	protected Paging pagingDivisionDetailList; // autowired
	protected Listbox listBoxDivisionDetail; // autowired

	// List headers
	protected Listheader listheader_DivisionCode; // autowired
	protected Listheader listheader_DivisionCodeDesc; // autowired
	protected Listheader listheader_Active; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_DivisionDetailList_NewDivisionDetail; // autowired
	protected Button button_DivisionDetailList_DivisionDetailSearch; // autowired
	protected Button button_DivisionDetailList_PrintList; // autowired
	protected Label  label_DivisionDetailList_RecordStatus; 							// autoWired
	protected Label  label_DivisionDetailList_RecordType; 							// autoWired
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DivisionDetail> searchObj;
	
	private transient DivisionDetailService divisionDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	
	protected Textbox divisionCode; // autowired
	protected Listbox sortOperator_DivisionCode; // autowired

	protected Textbox divisionCodeDesc; // autowired
	protected Listbox sortOperator_DivisionCodeDesc; // autowired

	protected Checkbox active; // autowired
	protected Listbox sortOperator_Active; // autowired

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
	public DivisionDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_DivisionDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("DivisionDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DivisionDetail");
			
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
	
		this.sortOperator_DivisionCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_DivisionCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_DivisionCodeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_DivisionCodeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_Active.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_Active.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);

			this.sortOperator_RecordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);

		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_DivisionDetailList_RecordStatus.setVisible(false);
			this.label_DivisionDetailList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_DivisionDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxDivisionDetail.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 
		
		// set the paging parameters
		this.pagingDivisionDetailList.setPageSize(getListRows());
		this.pagingDivisionDetailList.setDetailed(true);

		this.listheader_DivisionCode.setSortAscending(new FieldComparator("divisionCode", true));
		this.listheader_DivisionCode.setSortDescending(new FieldComparator("divisionCode", false));
		this.listheader_DivisionCodeDesc.setSortAscending(new FieldComparator("divisionCodeDesc", true));
		this.listheader_DivisionCodeDesc.setSortDescending(new FieldComparator("divisionCodeDesc", false));
		this.listheader_Active.setSortAscending(new FieldComparator("active", true));
		this.listheader_Active.setSortDescending(new FieldComparator("active", false));
		// set the itemRenderer
		this.listBoxDivisionDetail.setItemRenderer(new DivisionDetailListModelItemRenderer());
		
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
			this.button_DivisionDetailList_NewDivisionDetail.setVisible(false);
			this.button_DivisionDetailList_DivisionDetailSearch.setVisible(false);
			this.button_DivisionDetailList_PrintList.setVisible(false);
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
	 * see: com.pennant.webui.systemmaster.divisiondetail.model.DivisionDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onDivisionDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected DivisionDetail object
		final Listitem item = this.listBoxDivisionDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DivisionDetail aDivisionDetail = (DivisionDetail) item.getAttribute("data");
			DivisionDetail divisionDetail = null;
			if(approvedList){
				divisionDetail = getDivisionDetailService().getApprovedDivisionDetailById(aDivisionDetail.getId());
			}else{
				divisionDetail = getDivisionDetailService().getDivisionDetailById(aDivisionDetail.getId());
			}
			
			if(divisionDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aDivisionDetail.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_DivisionCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled() && moduleType==null){

					if(divisionDetail.getWorkflowId()==0){
						divisionDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
					}

					WorkflowLoad flowLoad= new WorkflowLoad(divisionDetail.getWorkflowId(), divisionDetail.getNextTaskId(), getUserWorkspace().getUserRoleSet());
					
					boolean userAcces =  validateUserAccess("DivisionDetail", new String[] {"DivisionCode"}, flowLoad.getRole(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), divisionDetail);
					if (userAcces){
						showDetailView(divisionDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(divisionDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the DivisionDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_DivisionDetailList_NewDivisionDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new DivisionDetail object, We GET it from the backend.
		final DivisionDetail aDivisionDetail = getDivisionDetailService().getNewDivisionDetail();
		showDetailView(aDivisionDetail);
		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */
	
	public void onClick$button_DivisionDetailList_DivisionDetailSearch(Event event) throws Exception {
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
	/*		this.pagingDivisionDetailList.setActivePage(0);
			Events.postEvent("onCreate", this.window_DivisionDetailList, event);
			this.window_DivisionDetailList.invalidate();
	*/		

  		this.sortOperator_DivisionCode.setSelectedIndex(0);
	  	this.divisionCode.setValue("");
  		this.sortOperator_DivisionCodeDesc.setSelectedIndex(0);
	  	this.divisionCodeDesc.setValue("");
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
		PTMessageUtils.showHelpWindow(event, window_DivisionDetailList);
		logger.debug("Leaving");
	}

	/**
	 * When the divisionDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_DivisionDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("DivisionDetail", getSearchObj(),this.pagingDivisionDetailList.getTotalSize()+1);
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
		getUserWorkspace().alocateAuthorities("DivisionDetailList");
		
		if(moduleType==null){
			this.button_DivisionDetailList_NewDivisionDetail.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailList_NewDivisionDetail"));
		}else{
			this.button_DivisionDetailList_NewDivisionDetail.setVisible(false);
		}	
		this.button_DivisionDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailList_PrintList"));
	logger.debug("Leaving");
	}


	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param DivisionDetail (aDivisionDetail)
	 * @throws Exception
	 */
	private void showDetailView(DivisionDetail aDivisionDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("divisionDetail", aDivisionDetail);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the DivisionDetailListbox from the
		 * dialog when we do a delete, edit or insert a DivisionDetail.
		 */
		map.put("divisionDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/DivisionDetail/DivisionDetailDialog.zul",null,map);
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
		this.searchObj = new JdbcSearchObject<DivisionDetail>(DivisionDetail.class,getListRows());
		this.searchObj.addSort("DivisionCode", false);
		this.searchObj.addTabelName("SMTDivisionDetail_View");
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	/*	this.searchObj.addField("divisionCode");
		this.searchObj.addField("divisionCodeDesc");
		this.searchObj.addField("active");*/
		
		// Workflow
		if (isWorkFlowEnabled()) {
			
			if (isFirstTask() && this.moduleType==null) {
				button_DivisionDetailList_NewDivisionDetail.setVisible(true);
			} else {
				button_DivisionDetailList_NewDivisionDetail.setVisible(false);
			}
			
			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					this.searchObj.addTabelName("SMTDivisionDetail_TView");
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("SMTDivisionDetail_AView");
		}
		
	// Division Code
		if (!StringUtils.trimToEmpty(this.divisionCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_DivisionCode.getSelectedItem(), this.divisionCode.getValue(), "DivisionCode");
		}
	// Division Code Desc
		if (!StringUtils.trimToEmpty(this.divisionCodeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_DivisionCodeDesc.getSelectedItem(), this.divisionCodeDesc.getValue(), "DivisionCodeDesc");
		}
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
		getPagedListWrapper().init(this.searchObj,this.listBoxDivisionDetail,this.pagingDivisionDetailList);

		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDivisionDetailService(DivisionDetailService divisionDetailService) {
		this.divisionDetailService = divisionDetailService;
	}

	public DivisionDetailService getDivisionDetailService() {
		return this.divisionDetailService;
	}

	public JdbcSearchObject<DivisionDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<DivisionDetail> searchObj) {
		this.searchObj = searchObj;
	}
}