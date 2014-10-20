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
 * FileName    		:  DashboardConfigurationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.dashboard.dashboardconfiguration;

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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.dashboard.dashboardconfiguration.model.DashboardConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/DashBoards/
 * DashboardConfiguration/dashboardConfigurationList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DashboardConfigurationListCtrl extends GFCBaseListCtrl<DashboardConfiguration> implements Serializable {

	private static final long serialVersionUID = 3577100345663196590L;
	private final static Logger logger = Logger.getLogger(DashboardConfigurationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DashboardConfigurationList; 								// autoWired
	protected Panel 		panel_DashboardConfigurationList; 								// autoWired
	protected Borderlayout 	borderLayout_DashboardConfigurationList; 						// autoWired
	protected Paging 		pagingDashboardConfigurationList; 								// autoWired
	protected Listbox 		listBoxDashboardConfiguration; 									// autoWired

	protected Textbox  dashboardCode; 										// autoWired
	protected Listbox  sortOperator_dashboardCode; 							// autoWired
	protected Textbox  dashboardDesc; 										// autoWired
	protected Listbox  sortOperator_dashboardDesc; 							// autoWired
	protected Combobox dashboardType; 										// autoWired
	protected Listbox  sortOperator_dashboardType; 							// autoWired
	protected Textbox  caption; 										    // autoWired
	protected Listbox  sortOperator_caption; 							    // autoWired
	protected Textbox  subCaption; 										    // autoWired
	protected Listbox  sortOperator_subCaption; 							// autoWired
	protected Combobox  dimension; 										    // autoWired
	protected Listbox  sortOperator_dimension; 							    // autoWired
	protected Textbox recordStatus; 										// autoWired
	protected Listbox recordType;											// autoWired
	protected Listbox sortOperator_recordStatus; 							// autoWired
	protected Listbox sortOperator_recordType; 								// autoWired
	// List headers
	protected Listheader listheader_DashboardCode; 											// autoWired
	protected Listheader listheader_DashboardDesc; 											// autoWired
	protected Listheader listheader_DashboardType; 											// autoWired
	protected Listheader listheader_RecordStatus; 											// autoWired
	protected Listheader listheader_RecordType;												// autoWired

	// checkRights
	protected Button btnHelp; 																// autoWired
	protected Button button_DashboardConfigurationList_NewDashboardConfiguration; 			// autoWired
	protected Button button_DashboardConfigurationList_Search; 				                // autoWired
	protected Button button_DashboardConfigurationList_PrintList; 						    // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DashboardConfiguration> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;

	private transient DashboardConfigurationService dashboardConfigurationService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public DashboardConfigurationListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected DashboardConfiguration object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DashboardConfigurationList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try{ 
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("DashboardConfiguration");
			boolean wfAvailable=true;

			if (moduleMapping.getWorkflowType()!=null){
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DashboardConfiguration");

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

			this.sortOperator_caption.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_caption.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_dashboardCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_dashboardCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_dashboardDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_dashboardDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_dashboardType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getEqualOperators()));
			this.sortOperator_dashboardType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			fillComboBox(this.dashboardType, "", PennantStaticListUtil.getDashBoardType(), "");

			this.sortOperator_dimension.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_dimension.setItemRenderer(new SearchOperatorListModelItemRenderer());
			fillComboBox(this.dimension, "", PennantStaticListUtil.getChartDimensions(), "");
			
			if (isWorkFlowEnabled()){
				this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
				this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
				this.sortOperator_recordStatus.setSelectedIndex(0);
				this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
				this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
				this.recordType=setRecordType(this.recordType);
				this.sortOperator_recordType.setSelectedIndex(0);
				this.recordType.setSelectedIndex(0);
				this.sortOperator_recordType.setSelectedIndex(0);
				this.recordType.setSelectedIndex(0);
			}else{
				this.row_AlwWorkflow.setVisible(false);
			}


			// set components visible dependent of the users rights 
			doCheckRights();

			this.borderLayout_DashboardConfigurationList.setHeight(getBorderLayoutHeight());
			this.listBoxDashboardConfiguration.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

			// set the paging parameters
			this.pagingDashboardConfigurationList.setPageSize(getListRows());
			this.pagingDashboardConfigurationList.setDetailed(true);

			this.listheader_DashboardCode.setSortAscending(new FieldComparator("dashboardCode", true));
			this.listheader_DashboardCode.setSortDescending(new FieldComparator("dashboardCode", false));
			this.listheader_DashboardDesc.setSortAscending(new FieldComparator("dashboardDesc", true));
			this.listheader_DashboardDesc.setSortDescending(new FieldComparator("dashboardDesc", false));
			this.listheader_DashboardType.setSortAscending(new FieldComparator("dashboardType", true));
			this.listheader_DashboardType.setSortDescending(new FieldComparator("dashboardType", false));

			if (isWorkFlowEnabled()){
				this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
				this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
				this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
				this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
			}else{
				this.listheader_RecordStatus.setVisible(false);
				this.listheader_RecordType.setVisible(false);
			}

			// ++ create the searchObject and initialize sorting ++//
			this.searchObj = new JdbcSearchObject<DashboardConfiguration>(DashboardConfiguration.class,getListRows());
			this.searchObj.addSort("DashboardCode", false);
			this.searchObj.addField("dashboardCode");
			this.searchObj.addField("dashboardDesc");
			this.searchObj.addField("dashboardType");
			this.searchObj.addField("dimension");
			this.searchObj.addField("subCaption");
			this.searchObj.addField("caption");
			this.searchObj.addField("recordType");
			this.searchObj.addField("recordStatus");
			// WorkFlow
			if (isWorkFlowEnabled()) {
				this.searchObj.addTabelName("DashboardConfiguration_View");
				if (isFirstTask()) {
					button_DashboardConfigurationList_NewDashboardConfiguration.setVisible(true);
				} else {
					button_DashboardConfigurationList_NewDashboardConfiguration.setVisible(false);
				}

				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
			}else{
				this.searchObj.addTabelName("DashboardConfiguration_AView");
			}

			setSearchObj(this.searchObj);
			if (!isWorkFlowEnabled() && wfAvailable){
				this.button_DashboardConfigurationList_NewDashboardConfiguration.setVisible(false);
				this.button_DashboardConfigurationList_Search.setVisible(false);
				this.button_DashboardConfigurationList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
			}else{
				doSearch();
				// set the itemRenderer
				this.listBoxDashboardConfiguration.setItemRenderer(new DashboardConfigurationListModelItemRenderer());
			}
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			logger.error("Error in opening windiow"+e.toString());
			window_DashboardConfigurationList.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("DashboardConfigurationList");

		this.button_DashboardConfigurationList_NewDashboardConfiguration
		.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationList_NewDashboardDetail"));
		this.button_DashboardConfigurationList_Search
		.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationList_DashboardDetailFindDialog"));
		this.button_DashboardConfigurationList_PrintList
		.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.dashboard.dashboardConfiguration.model.DashboardDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onDashboardDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		// get the selected DashboardDetail object
		final Listitem item = this.listBoxDashboardConfiguration.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DashboardConfiguration aDashboardConfiguration = (DashboardConfiguration) item.getAttribute("data");
			final DashboardConfiguration dashboardConfiguration = getDashboardConfigurationService()
			.getDashboardDetailById(aDashboardConfiguration.getId());
			if(dashboardConfiguration == null){
				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aDashboardConfiguration.getId();
				errorParm[0] = PennantJavaUtil.getLabel("label_DashboardConfiguration") + ":" + aDashboardConfiguration.getId();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else {
				String whereCond =  " AND DashboardCode='"+ dashboardConfiguration.getDashboardCode()
				+"' AND version=" + dashboardConfiguration.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "DashboardConfiguration", whereCond, 
							dashboardConfiguration.getTaskId(), dashboardConfiguration.getNextTaskId());
					if (userAcces){
						showDetailView(dashboardConfiguration);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(dashboardConfiguration);
				}
			}
			logger.debug("Leaving" + event.toString());
		}
	}

	/**
	 * Call the DashboardDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_DashboardConfigurationList_NewDashboardConfiguration(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		// create a new DashboardDetail object, We GET it from the backEnd.
		final DashboardConfiguration aDashboardConfiguration = getDashboardConfigurationService().getNewDashboardDetail();
		showDetailView(aDashboardConfiguration);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param DashboardDetail (aDashboardConfiguration)
	 * @throws Exception
	 */
	private void showDetailView(DashboardConfiguration aDashboardConfiguration) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aDashboardConfiguration.getWorkflowId()==0 && isWorkFlowEnabled()){
			aDashboardConfiguration.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dashboardConfiguration", aDashboardConfiguration);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the DashboardDetailListbox from the
		 * dialog when we do a delete, edit or insert a DashboardDetail.
		 */
		map.put("dashboardConfigurationListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/DashBoards/DashBoardConfiguration" +
					"/DashboardConfigurationDialog.zul",null,map);
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
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_DashboardConfigurationList);
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
		logger.debug("Entering"+event.toString());
		this.sortOperator_dashboardType.setSelectedIndex(0);
		this.dashboardType.setSelectedIndex(0);
		this.sortOperator_caption.setSelectedIndex(0);
		this.caption.setValue("");
		this.sortOperator_dashboardCode.setSelectedIndex(0);
		this.dashboardCode.setValue("");
		this.sortOperator_dashboardDesc.setSelectedIndex(0);
		this.dashboardDesc.setValue("");
		this.sortOperator_dimension.setSelectedIndex(0);
		this.dimension.setSelectedIndex(0);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears the Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxDashboardConfiguration,this.pagingDashboardConfigurationList);

		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the DashboardDetail search dialog
	 */
	public void onClick$button_DashboardConfigurationList_Search(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the dashboardDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_DashboardConfigurationList_PrintList(Event event) 
	throws InterruptedException {
		logger.debug("Entering"+event.toString());
		new PTListReportUtils("DashboardDetail", getSearchObj(),this.pagingDashboardConfigurationList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * Method for Searching List based on Filters
	 */
	private void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();
		//Caption
		if (!StringUtils.trimToEmpty(this.caption.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_caption.getSelectedItem(),this.caption.getValue(), "Caption");
		}
		// Sub Caption
		if (!StringUtils.trimToEmpty(this.subCaption.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_subCaption.getSelectedItem(),this.subCaption.getValue(), "SubCaption");
		}
		//DashBoardCode
		if (!StringUtils.trimToEmpty(this.dashboardCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_dashboardCode.getSelectedItem(),this.dashboardCode.getValue(), "DashboardCode");
		}
		//DashBoardDescription
		if (!StringUtils.trimToEmpty(this.dashboardDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_dashboardDesc.getSelectedItem(),this.dashboardDesc.getValue(), "DashboardDesc");
		}
		//Dimension
		if (this.dimension.getValue()!= null && !PennantConstants.List_Select.equals(this.dimension.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_dimension.getSelectedItem(),this.dimension.getSelectedItem().getValue().toString(), "Dimension");
		}
		//DashBoardType
		if (this.dashboardType.getValue()!= null && !PennantConstants.List_Select.equals(this.dashboardType.getSelectedItem().getValue()) ) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_dashboardType.getSelectedItem(),this.dashboardType.getSelectedItem().getValue().toString(), "DashboardType");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !PennantConstants.List_Select.equals(this.recordType
				.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),this.recordType.getSelectedItem().getValue().toString(),"RecordType");
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

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxDashboardConfiguration,this.pagingDashboardConfigurationList);

		logger.debug("Leaving");
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<DashboardConfiguration> getSearchObj() {
		return searchObj;
	}
	public void setSearchObj(JdbcSearchObject<DashboardConfiguration> searchObj) {
		this.searchObj = searchObj;
	}

	public DashboardConfigurationService getDashboardConfigurationService() {
		return dashboardConfigurationService;
	}
	public void setDashboardConfigurationService(DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}

}