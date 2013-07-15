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

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
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
import com.pennant.webui.smtmasters.weekendmaster.model.WeekendMasterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

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

	// List headers
	protected Listheader listheader_WeekendCode; // autowired
	protected Listheader listheader_WeekendDesc; // autowired
	protected Listheader listheader_Weekend; 	 // autowired

	protected Panel weekendMasterSeekPanel; // autowired
	protected Panel weekendMasterListPanel; // autowired

	// checkRights
	protected Button btnHelp; // autowired
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

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		this.borderLayout_WeekendMasterList.setHeight(getBorderLayoutHeight());
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

		/*
		 * if (isWorkFlowEnabled()){
		 * this.listheader_RecordStatus.setSortAscending(new
		 * FieldComparator("recordStatus", true));
		 * this.listheader_RecordStatus.setSortDescending(new
		 * FieldComparator("recordStatus", false));
		 * this.listheader_RecordType.setSortAscending(new
		 * FieldComparator("recordType", true));
		 * this.listheader_RecordType.setSortDescending(new
		 * FieldComparator("recordType", false)); }else{
		 * this.listheader_RecordStatus.setVisible(false);
		 * this.listheader_RecordType.setVisible(false); }
		 */
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<WeekendMaster>(
				WeekendMaster.class, getListRows());
		this.searchObj.addSort("WeekendCode", false);

		this.searchObj.addTabelName("SMTWeekendMaster_View");

		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_WeekendMasterList_NewWeekendMaster.setVisible(true);
			} else {
				button_WeekendMasterList_NewWeekendMaster.setVisible(false);
			}

			// this.searchObj.addFilterIn("nextRoleCode",
			// getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_WeekendMasterList_NewWeekendMaster.setVisible(false);
			this.button_WeekendMasterList_WeekendMasterSearchDialog
			.setVisible(false);
			this.button_WeekendMasterList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxWeekendMaster, this.pagingWeekendMasterList);
			// set the itemRenderer
			this.listBoxWeekendMaster
			.setItemRenderer(new WeekendMasterListModelItemRenderer());
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
				// String whereCond = " AND WeekendCode='"+
				// weekendMaster.getWeekendCode()+"' AND version=" +
				// weekendMaster.getVersion()+" ";
				showDetailView(weekendMaster);
				/*
				 * if(isWorkFlowEnabled()){ boolean userAcces =
				 * validateUserAccess
				 * (workFlowDetails.getId(),getUserWorkspace().
				 * getLoginUserDetails().getLoginUsrID(), "WeekendMaster",
				 * whereCond, weekendMaster.getTaskId(),
				 * weekendMaster.getNextTaskId()); if (userAcces){
				 * showDetailView(weekendMaster); }else{
				 * PTMessageUtils.showErrorMessage
				 * (Labels.getLabel("RECORD_NOTALLOWED")); } }else{
				 * showDetailView(weekendMaster); }
				 */
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

		/*
		 * if(aWeekendMaster.getWorkflowId()==0 && isWorkFlowEnabled()){
		 * aWeekendMaster.setWorkflowId(workFlowDetails.getWorkFlowId()); }
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
		this.pagingWeekendMasterList.setActivePage(0);
		Events.postEvent("onCreate", this.window_WeekendMasterList, event);
		this.window_WeekendMasterList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the WeekendMaster dialog
	 */
	public void onClick$button_WeekendMasterList_WeekendMasterSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our WeekendMasterDialog zul-file with parameters. So we
		 * can call them with a object of the selected WeekendMaster. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * WeekendMaster object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("weekendMasterCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions
			.createComponents(
					"/WEB-INF/pages/SolutionFactory/WeekendMaster/WeekendMasterSearchDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the weekendMaster print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_WeekendMasterList_PrintList(Event event)
	throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("WeekendMaster", getSearchObj());
		logger.debug("Leaving" + event.toString());
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