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

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Intbox;
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
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.smtmasters.holidaymaster.model.HolidayMasterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

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

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		int panelHeight = 30;
		// put the logic for working with panel in the ApplicationWorkspace

	
		int height = ((Intbox) Path
				.getComponent("/outerIndexWindow/currentDesktopHeight"))
				.getValue().intValue();
		height = height + panelHeight;
		final int maxListBoxHeight = height - 103;
		setCountRows(Math.round(maxListBoxHeight / 24) - 1);
		this.borderLayout_HolidayMasterList.setHeight(String
				.valueOf(maxListBoxHeight) + "px");

		// set the paging parameters
		this.pagingHolidayMasterList.setPageSize(getCountRows());
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
		this.searchObj = new JdbcSearchObject<HolidayMaster>(
				HolidayMaster.class, getCountRows());
		this.searchObj.addSort("HolidayCode", false);

		this.searchObj.addTabelName("SMTHolidayMaster_View");

		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_HolidayMasterList_NewHolidayMaster.setVisible(true);
				button_HolidayMasterList_HolidayMasterSearchDialog
				.setVisible(true);
			} else {
				button_HolidayMasterList_NewHolidayMaster.setVisible(false);
			}

			// this.searchObj.addFilterIn("nextRoleCode",
			// getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_HolidayMasterList_NewHolidayMaster.setVisible(false);
			this.button_HolidayMasterList_HolidayMasterSearchDialog
					.setVisible(false);
			this.button_HolidayMasterList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxHolidayMaster, this.pagingHolidayMasterList);
			// set the itemRenderer
			this.listBoxHolidayMaster
					.setItemRenderer(new HolidayMasterListModelItemRenderer());
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
				// CAST AND STORE THE SELECTED OBJECT

				// String whereCond = " AND HolidayCode='"+
				// holidayMaster.getHolidayCode()+"' AND HolidayYear ='"+holidayMaster.getHolidayYear()+"' AND HolidayType ='"+holidayMaster.getHolidayType()+"' AND version="
				// + holidayMaster.getVersion()+" ";
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
		this.pagingHolidayMasterList.setActivePage(0);
		Events.postEvent("onCreate", this.window_HolidayMasterList, event);
		this.window_HolidayMasterList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * call the HolidayMaster dialog
	 */

	public void onClick$button_HolidayMasterList_HolidayMasterSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		/*
		 * we can call our HolidayMasterDialog zul-file with parameters. So we
		 * can call them with a object of the selected HolidayMaster. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * HolidayMaster object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("holidayMasterCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions
					.createComponents(
							"/WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterSearchDialog.zul",
							null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the holidayMaster print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_HolidayMasterList_PrintList(Event event)
			throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTReportUtils.getReport("HolidayMaster", getSearchObj());
		logger.debug("Leaving " + event.toString());
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