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
 * FileName    		:  ReportConfigurationListCtrl.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.reports.reportconfiguration;

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
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.reports.reportconfiguration.model.ReportConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Masters/ReportConfiguration/ReportConfigurationList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ReportConfigurationListCtrl extends GFCBaseListCtrl<ReportConfiguration> implements Serializable {

	private static final long serialVersionUID = -7603242416503761389L;
	private final static Logger logger = Logger.getLogger(ReportConfigurationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ReportConfigurationList; 				// autoWired
	protected Borderlayout 	borderLayout_ReportConfigurationList; 			// autoWired
	protected Paging 		pagingReportConfigurationList; 				// autoWired

	protected Listbox 		listBoxReportConfiguration; 					// autoWired
	protected Listbox 		sortOperator_ReportName; 				// autoWired
	protected Listbox 		sortOperator_ReportHeading; 			// autoWired
	protected Listbox 		sortOperator_PromptRequired; 				// autoWired
	protected Listbox 		sortOperator_ReportJasperName; 			// autoWired
	protected Listbox 		sortOperator_recordStatus; 			// autoWired
	protected Listbox 		sortOperator_recordType; 			// autoWired
	protected Listbox 		recordType; 						// autoWired

	// List headers
	protected Listheader 	listheader_ReportName; 				// autoWired
	protected Listheader 	listheader_ReportHeading; 				// autoWired
	protected Listheader 	listheader_PromptRequired; 				// autoWired
	protected Listheader 	listheader_ReportJasperName; 			// autoWired
	protected Listheader 	listheader_RecordStatus; 			// autoWired
	protected Listheader 	listheader_RecordType;				// autoWired

	protected Label label_ReportConfigurationSearch_RecordStatus; 			// autoWired
	protected Label label_ReportConfigurationSearch_RecordType; 			// autoWired

	protected Textbox		reportName;							// autoWired
	protected Textbox		reportHeading;							// autoWired
	protected Checkbox		promptRequired;							// autoWired
	protected Textbox		reportJasperName;						// autoWired
	protected Textbox		recordStatus;						// autoWired

	protected Textbox 		moduleType; 					  	// autoWired
	protected Radio			fromApproved;                     	// autoWired
	protected Radio			fromWorkFlow;					 	// autoWired
	protected Row			workFlowFrom;                    	// autoWired
	private   Grid 			searchGrid;						  	// autoWired

	// checkRights
	protected Button 		btnHelp; 							// autoWired
	protected Button		btnRefresh;							// autoWired
	protected Button 		button_ReportConfigurationList_NewReportConfiguration; 	// autoWired
	protected Button 		button_ReportConfigurationList_ReportConfigurationSearch; // autoWired
	protected Button 		button_ReportConfigurationList_PrintList; 		// autoWired
	int 	  listRows;

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ReportConfiguration> searchObj;

	private transient ReportConfigurationService reportConfigurationService;
	private transient WorkFlowDetails workFlowDetails=null;
	private transient boolean  approvedList=false;

	/**
	 * default constructor.<br>
	 */
	public ReportConfigurationListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ReportConfiguration object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onCreate$window_ReportConfigurationList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("ReportConfiguration");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ReportConfiguration");

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

		this.sortOperator_ReportName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_ReportName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ReportHeading.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_ReportHeading.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_PromptRequired.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
		this.sortOperator_PromptRequired.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ReportJasperName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_ReportJasperName.setItemRenderer(new SearchOperatorListModelItemRenderer());


		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(new SearchOperators().getBooleanOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_ReportConfigurationSearch_RecordStatus.setVisible(false);
			this.label_ReportConfigurationSearch_RecordType.setVisible(false);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_ReportConfigurationList.setHeight(getBorderLayoutHeight());
		this.listBoxReportConfiguration.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size()));

		// set the paging parameters
		this.pagingReportConfigurationList.setDetailed(true);
		this.pagingReportConfigurationList.setPageSize(getListRows());

		this.listheader_ReportName.setSortAscending(new FieldComparator("reportName", true));
		this.listheader_ReportName.setSortDescending(new FieldComparator("reportName", false));
		this.listheader_ReportHeading.setSortAscending(new FieldComparator("reportHeading", true));
		this.listheader_ReportHeading.setSortDescending(new FieldComparator("reportHeading", false));
		this.listheader_PromptRequired.setSortAscending(new FieldComparator("promptRequired", true));
		this.listheader_PromptRequired.setSortDescending(new FieldComparator("promptRequired", false));
		this.listheader_ReportJasperName.setSortAscending(new FieldComparator("promptRequired", true));
		this.listheader_ReportJasperName.setSortDescending(new FieldComparator("promptRequired", false));

		// set the itemRenderer
		this.listBoxReportConfiguration.setItemRenderer(new ReportConfigurationListModelItemRenderer());

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
			this.button_ReportConfigurationList_NewReportConfiguration.setVisible(false);
			this.button_ReportConfigurationList_ReportConfigurationSearch.setVisible(false);
			this.button_ReportConfigurationList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("ReportConfigurationList");
		if(moduleType==null){
			this.button_ReportConfigurationList_NewReportConfiguration.setVisible(
					getUserWorkspace().isAllowed("button_ReportConfigurationList_NewRptConfiguration"));
		}else{
			this.button_ReportConfigurationList_NewReportConfiguration.setVisible(false);
		}
		this.button_ReportConfigurationList_PrintList.setVisible(getUserWorkspace().isAllowed("button_ReportConfigurationList_PrintList"));

		//this.button_ReportConfigurationList_NewReportConfiguration.setVisible(true);
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.ReportConfiguration.model.
	 * ReportConfigurationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onReportConfigurationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected ReportConfiguration object
		final Listitem item = this.listBoxReportConfiguration.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ReportConfiguration aReportConfiguration = (ReportConfiguration) item.getAttribute("data");
			ReportConfiguration reportConfiguration = null;
			if(approvedList){
				reportConfiguration = getReportConfigurationService().getApprovedReportConfigurationById(aReportConfiguration.getId());
			}else{
				reportConfiguration = getReportConfigurationService().getReportConfigurationById(aReportConfiguration.getId());
			}
			if(reportConfiguration==null){

				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aReportConfiguration.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_ReportId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND ReportId='"+ reportConfiguration.getReportID()+"' AND version=" + reportConfiguration.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), 
							"ReportConfiguration", whereCond, reportConfiguration.getTaskId(), reportConfiguration.getNextTaskId());
					if (userAcces){
						showDetailView(reportConfiguration);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(reportConfiguration);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the ReportConfiguration dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ReportConfigurationList_NewReportConfiguration(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new ReportConfiguration object, We GET it from the backEnd.
		final ReportConfiguration aReportConfiguration = getReportConfigurationService().getNewReportConfiguration();
		showDetailView(aReportConfiguration);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param ReportConfiguration (aReportConfiguration)
	 * @throws Exception
	 */
	private void showDetailView(ReportConfiguration aReportConfiguration) throws Exception {
		logger.debug("Entering ");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aReportConfiguration.getWorkflowId()==0 && isWorkFlowEnabled()){
			aReportConfiguration.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("reportConfiguration", aReportConfiguration);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the ReportConfigurationListbox from the
		 * dialog when we do a delete, edit or insert a ReportConfiguration.
		 */
		map.put("reportConfigurationListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportConfiguration/ReportConfigurationDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_ReportConfigurationList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the ReportConfiguration dialog
	 * @param event
	 * @throws Exception
	 */

	public void onClick$button_ReportConfigurationList_ReportConfigurationSearch(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
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
	public void onClick$btnRefresh(Event event) {
		logger.debug(event.toString());

		this.sortOperator_ReportName.setSelectedIndex(0);
		this.reportName.setValue("");
		this.sortOperator_ReportHeading.setSelectedIndex(0);
		this.reportHeading.setValue("");
		this.sortOperator_PromptRequired.setSelectedIndex(0);
		this.promptRequired.setChecked(false);
		this.sortOperator_ReportJasperName.setSelectedIndex(0);
		this.reportJasperName.setValue("");

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * When the ReportConfiguration print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_ReportConfigurationList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("ReportConfiguration", getSearchObj(),this.pagingReportConfigurationList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each TextBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	public void doSearch() {
		logger.debug("Entering ");

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<ReportConfiguration>(ReportConfiguration.class, getListRows());

		// DEFAULT Sort on the table
		this.searchObj.addSort("REPORTID", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("ReportConfiguration_View");
			if (isFirstTask()&& this.moduleType==null) {
				button_ReportConfigurationList_NewReportConfiguration.setVisible(true);
			} else {
				button_ReportConfigurationList_NewReportConfiguration.setVisible(false);
			}

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
			this.searchObj.addTabelName("ReportConfiguration_View");
		}else{
			this.searchObj.addTabelName("ReportConfiguration_View");
		}

		if (StringUtils.isNotEmpty(this.reportName.getValue())) {

			// get the search operator
			final Listitem item_ReportName = this.sortOperator_ReportName.getSelectedItem();

			if (item_ReportName != null) {
				final int searchOpId = ((SearchOperators) item_ReportName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("reportName", "%"
							+ this.reportName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					this.searchObj.addFilter(new Filter("reportName", this.reportName.getValue(),searchOpId));
				}
			}
		}



		if (StringUtils.isNotEmpty(this.reportHeading.getValue())) {

			// get the search operator
			final Listitem item_ReportHeading = this.sortOperator_ReportHeading
			.getSelectedItem();

			if (item_ReportHeading != null) {
				final int searchOpId = ((SearchOperators) item_ReportHeading
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("reportHeading", "%"
							+ this.reportHeading.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					this.searchObj.addFilter(new Filter("reportHeading", this.reportHeading.getValue(),
							searchOpId));
				}
			}
		}

		// get the search operator
		final Listitem item_PromptRequired = this.sortOperator_PromptRequired.getSelectedItem();

		if (item_PromptRequired != null) {
			final int searchOpId = ((SearchOperators) item_PromptRequired.getAttribute("data")).getSearchOperatorId();

			if (searchOpId == -1) {
				// do nothing
			} else {

				if (this.promptRequired.isChecked()) {
					this.searchObj.addFilter(new Filter("promptRequired", 1, searchOpId));
				} else {
					this.searchObj.addFilter(new Filter("promptRequired", 0, searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.reportJasperName.getValue())) {

			// get the search operator
			final Listitem item_ReportJasperName = this.sortOperator_ReportJasperName.getSelectedItem();

			if (item_ReportJasperName != null) {
				final int searchOpId = ((SearchOperators) item_ReportJasperName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("reportJasperName", "%"
							+ this.reportJasperName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					this.searchObj.addFilter(new Filter("reportJasperName", this.reportJasperName.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus
						.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("recordStatus", "%"
							+ this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					this.searchObj.addFilter(new Filter("recordStatus", this.recordStatus
							.getValue(), searchOpId));
				}
			}
		}

		String selectedValue = "";
		if (this.recordType.getSelectedItem() != null) {
			selectedValue = this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType != null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("recordType", "%"+ selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					this.searchObj.addFilter(new Filter("recordType", selectedValue,searchOpId));
				}
			}
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		getPagedListWrapper().init(this.searchObj,this.listBoxReportConfiguration,this.pagingReportConfigurationList);
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setReportConfigurationService(ReportConfigurationService reportConfigurationService) {
		this.reportConfigurationService = reportConfigurationService;
	}
	public ReportConfigurationService getReportConfigurationService() {
		return this.reportConfigurationService;
	}

	public JdbcSearchObject<ReportConfiguration> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ReportConfiguration> searchObj) {
		this.searchObj = searchObj;
	}

}