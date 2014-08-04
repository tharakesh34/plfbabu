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
 * FileName    		:  HolidayMasterListCtrl.java                                                   * 	  
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

package com.pennant.webui.smtmasters.holidaymaster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.smtmasters.holidaymaster.model.HolidayMasterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class HolidayMasterListCtrl extends GFCBaseListCtrl<HolidayMaster>
		implements Serializable {

	private static final long serialVersionUID = 5550212164288969546L;

	private final static Logger logger = Logger
			.getLogger(HolidayMasterListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_HolidayMasterList; 				// autowired
	protected Borderlayout borderLayout_HolidayMasterList;  // autowired
	protected Paging pagingHolidayMasterList; 				// autowired
	protected Listbox listBoxHolidayMaster; 				// autowired

	// List headers
	protected Listheader listheader_HolidayCode; 	// autowired
	protected Listheader listheader_HolidayYear; 	// autowired
	protected Listheader listheader_HolidayType;	//autowired
	// protected Listheader listheader_RecordStatus; // autowired
	// protected Listheader listheader_RecordType;

	protected Panel holidayMasterSeekPanel; // autowired
	protected Panel holidayMasterListPanel; // autowired

	// Filtering Fields
	
	protected Textbox holidayCode; 				  // autowired
	protected Listbox sortOperator_holidayCode;   // autowired
	protected Intbox holidayYear; 				  // autowired
	protected Listbox sortOperator_holidayYear;   // autowired
	protected Textbox holidayType; 				  // autowired
	protected Listbox sortOperator_holidayType;   // autowired
	
	protected Label label_HolidayMasterSearchResult; 		    // autowired
	
	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false;
	
	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_HolidayMasterList_NewHolidayMaster; 		 // autowired
	protected Button button_HolidayMasterList_HolidayMasterSearchDialog; // autowired
	protected Button button_HolidayMasterList_PrintList; 				 // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<HolidayMaster> searchObj;

	// row count for listbox
	private int countRows;

	private transient HolidayMasterService holidayMasterService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public HolidayMasterListCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a list of HolidayMaster object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_HolidayMasterList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil
				.getModuleMap("HolidayMaster");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("HolidayMaster");

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

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		
		this.sortOperator_holidayCode.setModel(new ListModelList<SearchOperators>(
				new SearchOperators().getStringOperators()));
		this.sortOperator_holidayCode.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_holidayYear.setModel(
				new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_holidayYear.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_holidayType.setModel(
				new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_holidayType.setItemRenderer(
				new SearchOperatorListModelItemRenderer());
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_HolidayMasterList.setHeight(getBorderLayoutHeight());
		this.listBoxHolidayMaster.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingHolidayMasterList.setPageSize(getListRows());
		this.pagingHolidayMasterList.setDetailed(true);

		this.listheader_HolidayCode.setSortAscending(new FieldComparator(
				"holidayCode", true));
		this.listheader_HolidayCode.setSortDescending(new FieldComparator(
				"holidayCode", false));
		this.listheader_HolidayYear.setSortAscending(new FieldComparator(
				"holidayYear", true));
		this.listheader_HolidayYear.setSortDescending(new FieldComparator(
				"holidayYear", false));
		this.listheader_HolidayType.setSortAscending(new FieldComparator(
				"holidayType", true));
		this.listheader_HolidayType.setSortDescending(new FieldComparator(
				"holidayType", false));
	
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_HolidayMasterList_NewHolidayMaster.setVisible(true);
				button_HolidayMasterList_HolidayMasterSearchDialog
				.setVisible(true);
			} else {
				button_HolidayMasterList_NewHolidayMaster.setVisible(false);
			}
		}

		// set the itemRenderer
		this.listBoxHolidayMaster.setItemRenderer(new HolidayMasterListModelItemRenderer());
		
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_HolidayMasterList_NewHolidayMaster.setVisible(false);
			this.button_HolidayMasterList_HolidayMasterSearchDialog
					.setVisible(false);
			this.button_HolidayMasterList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("HolidayMasterList");
		this.button_HolidayMasterList_NewHolidayMaster
				.setVisible(getUserWorkspace().isAllowed(
						"button_HolidayMasterList_NewHolidayMaster"));
		this.button_HolidayMasterList_HolidayMasterSearchDialog
				.setVisible(getUserWorkspace().isAllowed(
						"button_HolidayMasterList_HolidayMasterFindDialog"));
		this.button_HolidayMasterList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_HolidayMasterList_PrintList"));
		this.button_HolidayMasterList_NewHolidayMaster.setVisible(true);
		this.button_HolidayMasterList_HolidayMasterSearchDialog
		.setVisible(true);
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.smtmasters.holidaymaster.model.
	 * HolidayMasterListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onHolidayMasterItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected HolidayMaster object
		final Listitem item = this.listBoxHolidayMaster.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final HolidayMaster aHolidayMaster = (HolidayMaster) item
					.getAttribute("data");
			final HolidayMaster holidayMaster = getHolidayMasterService()
					.getHolidayMasterById(aHolidayMaster.getId(),
							aHolidayMaster.getHolidayYear(),
							aHolidayMaster.getHolidayType());

			if (holidayMaster == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aHolidayMaster.getHolidayCode();
				valueParm[1] = aHolidayMaster.getHolidayYear().toString();
				valueParm[2] = aHolidayMaster.getHolidayType();

				errParm[0] = PennantJavaUtil
						.getLabel("label_HolidayMasterDialog_HolidayCode")
						+ ":" + valueParm[0];
				errParm[1] = PennantJavaUtil
						.getLabel("label_HolidayMasterDialog_HolidayYear")
						+ ":" + valueParm[1];
				errParm[2] = PennantJavaUtil
						.getLabel("label_HolidayMasterDialog_HolidayType" + ":"
								+ valueParm[2]);

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace()
								.getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				showDetailView(holidayMaster);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the HolidayMaster dialog with a new empty entry. <br>
	 */
	public void onClick$button_HolidayMasterList_NewHolidayMaster(Event event)
			throws Exception {
		logger.debug("Entering " + event.toString());
		HolidayMaster aHolidayMaster = getHolidayMasterService()
				.getNewHolidayMaster();
		showDetailView(aHolidayMaster);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param HolidayMaster
	 *            (aHolidayMaster)
	 * @throws Exception
	 */
	private void showDetailView(HolidayMaster aHolidayMaster) throws Exception {
		logger.debug("Entering ");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("holidayMaster", aHolidayMaster);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the HolidayMasterListbox from the
		 * dialog when we do a delete, edit or insert a HolidayMaster.
		 */
		map.put("holidayMasterListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions
					.createComponents(
							"/WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterDialog.zul",
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
	 * 
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_HolidayMasterList);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * 
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		   this.sortOperator_holidayCode.setSelectedIndex(0);
		   this.holidayCode.setValue("");
		   this.sortOperator_holidayYear.setSelectedIndex(0);
		   this.holidayYear.setValue(null);
		   this.sortOperator_holidayType.setSelectedIndex(0);
		   this.holidayType.setValue("");
		   doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * call the HolidayMaster dialog
	 */

	public void onClick$button_HolidayMasterList_HolidayMasterSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		    doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the holidayMaster print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_HolidayMasterList_PrintList(Event event)
			throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("HolidayMaster", getSearchObj(),
				this.pagingHolidayMasterList.getTotalSize() + 1);
		logger.debug("Leaving " + event.toString());
	}
	
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<HolidayMaster>(HolidayMaster.class,getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("HolidayCode", false);
		
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("SMTHolidayMaster_View");

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
			this.searchObj.addTabelName("SMTHolidayMaster_AView");
		}else{
			this.searchObj.addTabelName("SMTHolidayMaster_View");
		}
		
		//Holiday Code
		if (!StringUtils.trimToEmpty(this.holidayCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_holidayCode.getSelectedItem(),this.holidayCode.getValue(), "holidayCode");
		}
		//Holiday Year
		if (this.holidayYear.intValue()!=0) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_holidayYear.getSelectedItem(),this.holidayYear.getValue(), "holidayYear");
		}
		//Holiday Type
		if (!StringUtils.trimToEmpty(this.holidayType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_holidayType.getSelectedItem(),this.holidayType.getValue(), "holidayType");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxHolidayMaster,this.pagingHolidayMasterList);
		logger.debug("Leaving");
	}

	public void setHolidayMasterService(
			HolidayMasterService holidayMasterService) {
		this.holidayMasterService = holidayMasterService;
	}

	public HolidayMasterService getHolidayMasterService() {
		return this.holidayMasterService;
	}

	public JdbcSearchObject<HolidayMaster> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<HolidayMaster> searchObj) {
		this.searchObj = searchObj;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}
}