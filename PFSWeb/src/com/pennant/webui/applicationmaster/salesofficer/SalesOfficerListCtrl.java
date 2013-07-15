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
 * FileName    		:  SalesOfficerListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.salesofficer;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
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
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.service.applicationmaster.SalesOfficerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.salesofficer.model.SalesOfficerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/SalesOfficer/SalesOfficerList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SalesOfficerListCtrl extends GFCBaseListCtrl<SalesOfficer> implements Serializable {

	private static final long serialVersionUID = 2821884142332965776L;
	private final static Logger logger = Logger.getLogger(SalesOfficerListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_SalesOfficerList; 					// autoWired
	protected Borderlayout borderLayout_SalesOfficerList; 		// autoWired
	protected Paging pagingSalesOfficerList; 					// autoWired
	protected Listbox listBoxSalesOfficer; 						// autoWired

	// List headers
	protected Listheader listheader_SalesOffCode; 				// autoWired
	protected Listheader listheader_SalesOffFName; 				// autoWired
	protected Listheader listheader_SalesOffDept; 				// autoWired
	protected Listheader listheader_SalesOffIsActive; 			// autoWired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// autoWired
	protected Button button_SalesOfficerList_NewSalesOfficer; 			// autoWired
	protected Button button_SalesOfficerList_SalesOfficerSearchDialog; 	// autoWired
	protected Button button_SalesOfficerList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SalesOfficer> searchObj;

	private transient SalesOfficerService salesOfficerService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public SalesOfficerListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SalesOfficer object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SalesOfficerList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SalesOfficer");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SalesOfficer");

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

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_SalesOfficerList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSalesOfficerList.setPageSize(getListRows());
		this.pagingSalesOfficerList.setDetailed(true);

		this.listheader_SalesOffCode.setSortAscending(new FieldComparator("salesOffCode", true));
		this.listheader_SalesOffCode.setSortDescending(new FieldComparator("salesOffCode", false));
		this.listheader_SalesOffFName.setSortAscending(new FieldComparator("salesOffFName", true));
		this.listheader_SalesOffFName.setSortDescending(new FieldComparator("salesOffFName", false));
		this.listheader_SalesOffDept.setSortAscending(new FieldComparator("salesOffDept", true));
		this.listheader_SalesOffDept.setSortDescending(new FieldComparator("salesOffDept", false));
		this.listheader_SalesOffIsActive.setSortAscending(new FieldComparator("salesOffIsActive", true));
		this.listheader_SalesOffIsActive.setSortDescending(new FieldComparator("salesOffIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<SalesOfficer>(SalesOfficer.class,getListRows());
		this.searchObj.addSort("SalesOffCode", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("SalesOfficers_View");
			if (isFirstTask()) {
				button_SalesOfficerList_NewSalesOfficer.setVisible(true);
			} else {
				button_SalesOfficerList_NewSalesOfficer.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}
		else{
			this.searchObj.addTabelName("SalesOfficers_AView");
		}
		
		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_SalesOfficerList_NewSalesOfficer.setVisible(false);
			this.button_SalesOfficerList_SalesOfficerSearchDialog.setVisible(false);
			this.button_SalesOfficerList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxSalesOfficer, this.pagingSalesOfficerList);
			// set the itemRenderer
			this.listBoxSalesOfficer.setItemRenderer(new SalesOfficerListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SalesOfficerList");

		this.button_SalesOfficerList_NewSalesOfficer
				.setVisible(getUserWorkspace().isAllowed(
						"button_SalesOfficerList_NewSalesOfficer"));
		this.button_SalesOfficerList_SalesOfficerSearchDialog
				.setVisible(getUserWorkspace().isAllowed(
						"button_SalesOfficerList_SalesOfficerFindDialog"));
		this.button_SalesOfficerList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SalesOfficerList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.masters.salesofficer.model.
	 * SalesOfficerListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onSalesOfficerItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected SalesOfficer object
		final Listitem item = this.listBoxSalesOfficer.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SalesOfficer aSalesOfficer = (SalesOfficer) item.getAttribute("data");
			final SalesOfficer salesOfficer = getSalesOfficerService()
					.getSalesOfficerById(aSalesOfficer.getId());

			if (salesOfficer == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aSalesOfficer.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_SalesOffCode")
						+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace()
								.getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND SalesOffCode='"
							+ salesOfficer.getSalesOffCode() + "' AND version="
							+ salesOfficer.getVersion() + " ";

					boolean userAcces = validateUserAccess(
							workFlowDetails.getId(), getUserWorkspace()
									.getLoginUserDetails().getLoginUsrID(),
							"SalesOfficer", whereCond,
							salesOfficer.getTaskId(),
							salesOfficer.getNextTaskId());
					if (userAcces) {
						showDetailView(salesOfficer);
					} else {
						PTMessageUtils.showErrorMessage(Labels
								.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(salesOfficer);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the SalesOfficer dialog with a new empty entry. <br>
	 */
	public void onClick$button_SalesOfficerList_NewSalesOfficer(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new SalesOfficer object, We GET it from the backEnd.
		final SalesOfficer aSalesOfficer = getSalesOfficerService().getNewSalesOfficer();
		showDetailView(aSalesOfficer);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param SalesOfficer
	 *            (aSalesOfficer)
	 * @throws Exception
	 */
	private void showDetailView(SalesOfficer aSalesOfficer) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aSalesOfficer.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aSalesOfficer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("salesOfficer", aSalesOfficer);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SalesOfficerListbox from the
		 * dialog when we do a delete, edit or insert a SalesOfficer.
		 */
		map.put("salesOfficerListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
				"/WEB-INF/pages/ApplicationMaster/SalesOfficer/SalesOfficerDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_SalesOfficerList);
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
		this.pagingSalesOfficerList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SalesOfficerList, event);
		this.window_SalesOfficerList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the SalesOfficer dialog
	 */

	public void onClick$button_SalesOfficerList_SalesOfficerSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our SalesOfficerDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected SalesOfficer. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * SalesOfficer object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("salesOfficerCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
				"/WEB-INF/pages/ApplicationMaster/SalesOfficer/SalesOfficerSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the salesOfficer print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SalesOfficerList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("SalesOfficer", getSearchObj(),this.pagingSalesOfficerList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSalesOfficerService(SalesOfficerService salesOfficerService) {
		this.salesOfficerService = salesOfficerService;
	}
	public SalesOfficerService getSalesOfficerService() {
		return this.salesOfficerService;
	}

	public JdbcSearchObject<SalesOfficer> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SalesOfficer> searchObj) {
		this.searchObj = searchObj;
	}
}