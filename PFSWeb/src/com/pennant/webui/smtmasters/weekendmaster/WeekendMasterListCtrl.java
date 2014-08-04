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
 * FileName    		:  WeekendMasterListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.smtmasters.weekendmaster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.service.smtmasters.WeekendMasterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.webui.smtmasters.weekendmaster.model.WeekendMasterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/WeekendMaster/WeekendMasterList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class WeekendMasterListCtrl extends GFCBaseListCtrl<WeekendMaster>
implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger
	.getLogger(WeekendMasterListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WeekendMasterList; 				// autowired
	protected Panel panel_WeekendMasterList; 				// autowired
	protected Borderlayout borderLayout_WeekendMasterList;  // autowired
	protected Paging pagingWeekendMasterList; 				// autowired
	protected Listbox listBoxWeekendMaster; 				// autowired
	protected Row weekendRow;
	// List headers
	protected Listheader listheader_WeekendCode; 	// autowired
	protected Listheader listheader_WeekendDesc; 	// autowired
	protected Listheader listheader_Weekend; 	 	// autowired

	protected Panel weekendMasterSeekPanel; 		// autowired
	protected Panel weekendMasterListPanel; 		// autowired

	// Searching Fields

	protected Uppercasebox weekendCode; 						// autowired
	protected Listbox sortOperator_weekendCode; 				// autowired
	protected Textbox weekendDesc; 								// autowired
	protected Listbox sortOperator_weekendDesc; 				// autowired
	protected Combobox weekend; 									// autowired
	protected Listbox sortOperator_weekend; 					// autowired
	protected Listbox sortOperator_recordStatus;				// autowired
	protected Listbox sortOperator_recordType; 					// autowired

	protected Label label_WeekendMasterSearch_RecordStatus; 	// autowired
	protected Label label_WeekendMasterSearch_RecordType; 		// autowired


	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false;
	// checkRights
	protected Button btnHelp; 											 // autowired
	protected Button button_WeekendMasterList_NewWeekendMaster; 		 // autowired
	protected Button button_WeekendMasterList_WeekendMasterSearchDialog; // autowired
	protected Button button_WeekendMasterList_PrintList; 				 // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<WeekendMaster> searchObj;

	// row count for listbox
	private int countRows;

	private transient WeekendMasterService weekendMasterService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public WeekendMasterListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected WeekendMaster object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_WeekendMasterList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil
		.getModuleMap("WeekendMaster");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("WeekendMaster");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}
		this.weekendRow.setVisible(false);
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_weekendCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_weekendCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_weekendDesc.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_weekendDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_weekend.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_weekend.setItemRenderer(new SearchOperatorListModelItemRenderer());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		this.borderLayout_WeekendMasterList.setHeight(getBorderLayoutHeight());
		this.listBoxWeekendMaster.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingWeekendMasterList.setPageSize(getListRows());
		this.pagingWeekendMasterList.setDetailed(true);

		this.listheader_WeekendCode.setSortAscending(new FieldComparator(
				"weekendCode", true));
		this.listheader_WeekendCode.setSortDescending(new FieldComparator(
				"weekendCode", false));
		this.listheader_WeekendDesc.setSortAscending(new FieldComparator(
				"weekendDesc", true));
		this.listheader_WeekendDesc.setSortDescending(new FieldComparator(
				"weekendDesc", false));
		this.listheader_Weekend.setSortAscending(new FieldComparator("weekend",
				true));
		this.listheader_Weekend.setSortDescending(new FieldComparator(
				"weekend", false));

		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_WeekendMasterList_NewWeekendMaster.setVisible(true);
			} else {
				button_WeekendMasterList_NewWeekendMaster.setVisible(false);
			}
		}

		// set the itemRenderer
		this.listBoxWeekendMaster.setItemRenderer(new WeekendMasterListModelItemRenderer());

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_WeekendMasterList_NewWeekendMaster.setVisible(false);
			this.button_WeekendMasterList_WeekendMasterSearchDialog
			.setVisible(false);
			this.button_WeekendMasterList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
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
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("WeekendMasterList");
		this.button_WeekendMasterList_NewWeekendMaster
		.setVisible(getUserWorkspace().isAllowed(
		"button_WeekendMasterList_NewWeekendMaster"));
		this.button_WeekendMasterList_WeekendMasterSearchDialog
		.setVisible(getUserWorkspace().isAllowed(
		"button_WeekendMasterList_WeekendMasterFindDialog"));
		this.button_WeekendMasterList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_WeekendMasterList_PrintList"));
		logger.debug("Leaving ");

	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.smtmasters.weekendmaster.model.
	 * WeekendMasterListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onWeekendMasterItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected WeekendMaster object
		final Listitem item = this.listBoxWeekendMaster.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final WeekendMaster aWeekendMaster = (WeekendMaster) item
			.getAttribute("data");
			final WeekendMaster weekendMaster = getWeekendMasterService()
			.getWeekendMasterById(aWeekendMaster.getId());
			if (weekendMaster == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aWeekendMaster.getWeekendCode();

				errParm[0] = PennantJavaUtil
				.getLabel("label_WeekendMasterDialog_WeekendCode")
				+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace()
								.getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				showDetailView(weekendMaster);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the WeekendMaster dialog with a new empty entry. <br>
	 */
	public void onClick$button_WeekendMasterList_NewWeekendMaster(Event event)
	throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new WeekendMaster object, We GET it from the backend.
		final WeekendMaster aWeekendMaster = getWeekendMasterService()
		.getNewWeekendMaster();
		showDetailView(aWeekendMaster);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param WeekendMaster
	 *            (aWeekendMaster)
	 * @throws Exception
	 */
	private void showDetailView(WeekendMaster aWeekendMaster) throws Exception {
		logger.debug("Entering ");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("weekendMaster", aWeekendMaster);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the WeekendMasterListbox from the
		 * dialog when we do a delete, edit or insert a WeekendMaster.
		 */
		map.put("weekendMasterListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions
			.createComponents(
					"/WEB-INF/pages/SolutionFactory/WeekendMaster/WeekendMasterDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_WeekendMasterList);
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
		this.sortOperator_weekendCode.setSelectedIndex(0);
		this.weekendCode.setValue("");
		this.sortOperator_weekendDesc.setSelectedIndex(0);
		this.weekendDesc.setValue("");
		this.sortOperator_weekend.setSelectedIndex(0);
		this.weekend.setValue("");
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the WeekendMaster dialog
	 */
	public void onClick$button_WeekendMasterList_WeekendMasterSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the weekendMaster print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_WeekendMasterList_PrintList(Event event)
	throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerRating", getSearchObj(),
				this.pagingWeekendMasterList.getTotalSize() + 1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<WeekendMaster>(WeekendMaster.class,getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("WeekendCode", false);

		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("SMTWeekendMaster_View");

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
			this.searchObj.addTabelName("SMTWeekendMaster_AView");
		}else{
			this.searchObj.addTabelName("SMTWeekendMaster_View");
		}

		//Weekend Code
		if (!StringUtils.trimToEmpty(this.weekendCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_weekendCode.getSelectedItem(),this.weekendCode.getValue(), "weekendCode");
		}
		//Weekend  Description
		if (!StringUtils.trimToEmpty(this.weekendDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_weekendDesc.getSelectedItem(),this.weekendDesc.getValue(), "weekendDesc");
		}

		//Weekend 
		if (!StringUtils.trimToEmpty(this.weekend.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_weekend.getSelectedItem(), this.weekend.getValue() , "weekend");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxWeekendMaster,this.pagingWeekendMasterList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<WeekendMaster> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<WeekendMaster> searchObj) {
		this.searchObj = searchObj;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public void setWeekendMasterService(
			WeekendMasterService weekendMasterService) {
		this.weekendMasterService = weekendMasterService;
	}

	public WeekendMasterService getWeekendMasterService() {
		return this.weekendMasterService;
	}
}