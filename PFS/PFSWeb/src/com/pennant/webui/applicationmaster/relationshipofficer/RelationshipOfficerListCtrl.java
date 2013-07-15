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
 * FileName    		:  RelationshipOfficerListCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.relationshipofficer;

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
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.relationshipofficer.model.RelationshipOfficerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/RelationshipOfficer/RelationshipOfficerList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */

public class RelationshipOfficerListCtrl extends GFCBaseListCtrl<RelationshipOfficer> implements Serializable {

	private static final long serialVersionUID = 2977963103737338816L;
	private final static Logger logger = Logger.getLogger(RelationshipOfficerListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window 		window_RelationshipOfficerList; 		// autoWired
	protected Borderlayout 	borderLayout_RelationshipOfficerList; 	// autoWired
	protected Paging 		pagingRelationshipOfficerList; 			// autoWired
	protected Listbox 		listBoxRelationshipOfficer; 			// autoWired

	// List headers
	protected Listheader listheader_ROfficerCode; 		// autoWired
	protected Listheader listheader_ROfficerDesc; 		// autoWired
	protected Listheader listheader_ROfficerDeptCode; 	// autoWired
	protected Listheader listheader_ROfficerIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autoWired
	protected Button button_RelationshipOfficerList_NewRelationshipOfficer; 		 // autoWired
	protected Button button_RelationshipOfficerList_RelationshipOfficerSearchDialog; // autoWired
	protected Button button_RelationshipOfficerList_PrintList; 						 // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<RelationshipOfficer> searchObj;

	private transient RelationshipOfficerService relationshipOfficerService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public RelationshipOfficerListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected RelationshipOfficer
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RelationshipOfficerList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("RelationshipOfficer");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RelationshipOfficer");

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

		this.borderLayout_RelationshipOfficerList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingRelationshipOfficerList.setPageSize(getListRows());
		this.pagingRelationshipOfficerList.setDetailed(true);

		this.listheader_ROfficerCode.setSortAscending(new FieldComparator("rOfficerCode", true));
		this.listheader_ROfficerCode.setSortDescending(new FieldComparator("rOfficerCode", false));
		this.listheader_ROfficerDesc.setSortAscending(new FieldComparator("rOfficerDesc", true));
		this.listheader_ROfficerDesc.setSortDescending(new FieldComparator("rOfficerDesc", false));
		this.listheader_ROfficerDeptCode.setSortAscending(new FieldComparator("rOfficerDeptCode", true));
		this.listheader_ROfficerDeptCode.setSortDescending(new FieldComparator("rOfficerDeptCode", false));
		this.listheader_ROfficerIsActive.setSortAscending(new FieldComparator("rOfficerIsActive", true));
		this.listheader_ROfficerIsActive.setSortDescending(new FieldComparator("rOfficerIsActive", false));

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
		this.searchObj = new JdbcSearchObject<RelationshipOfficer>(RelationshipOfficer.class, getListRows());
		this.searchObj.addSort("ROfficerCode", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RelationshipOfficers_View");
			if (isFirstTask()) {
				button_RelationshipOfficerList_NewRelationshipOfficer.setVisible(true);
			} else {
				button_RelationshipOfficerList_NewRelationshipOfficer.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else{
			this.searchObj.addTabelName("RelationshipOfficers_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_RelationshipOfficerList_NewRelationshipOfficer.setVisible(false);
			this.button_RelationshipOfficerList_RelationshipOfficerSearchDialog.setVisible(false);
			this.button_RelationshipOfficerList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxRelationshipOfficer,this.pagingRelationshipOfficerList);
			// set the itemRenderer
			this.listBoxRelationshipOfficer.setItemRenderer(new RelationshipOfficerListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("RelationshipOfficerList");

		this.button_RelationshipOfficerList_NewRelationshipOfficer.setVisible(getUserWorkspace()
				.isAllowed("button_RelationshipOfficerList_NewRelationshipOfficer"));
		this.button_RelationshipOfficerList_RelationshipOfficerSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_RelationshipOfficerList_RelationshipOfficerFindDialog"));
		this.button_RelationshipOfficerList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_RelationshipOfficerList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.masters.relationshipofficer.model.
	 * RelationshipOfficerListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onRelationshipOfficerItemDoubleClicked(Event event)	throws Exception {
		logger.debug(event.toString());

		// get the selected RelationshipOfficer object
		final Listitem item = this.listBoxRelationshipOfficer.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final RelationshipOfficer aRelationshipOfficer = (RelationshipOfficer) item.getAttribute("data");
			final RelationshipOfficer relationshipOfficer = getRelationshipOfficerService()
			.getRelationshipOfficerById(aRelationshipOfficer.getId());

			if (relationshipOfficer == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aRelationshipOfficer.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_ROfficerCode") + ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND ROfficerCode='" + relationshipOfficer.getROfficerCode()
					+ "' AND version=" + relationshipOfficer.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"RelationshipOfficer", whereCond, relationshipOfficer.getTaskId(),relationshipOfficer.getNextTaskId());
					if (userAcces) {
						showDetailView(relationshipOfficer);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(relationshipOfficer);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the RelationshipOfficer dialog with a new empty entry. <br>
	 */
	public void onClick$button_RelationshipOfficerList_NewRelationshipOfficer(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new RelationshipOfficer object, We GET it from the backEnd.
		final RelationshipOfficer aRelationshipOfficer = getRelationshipOfficerService().getNewRelationshipOfficer();
		showDetailView(aRelationshipOfficer);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param RelationshipOfficer
	 *            (aRelationshipOfficer)
	 * @throws Exception
	 */
	private void showDetailView(RelationshipOfficer aRelationshipOfficer) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aRelationshipOfficer.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aRelationshipOfficer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("relationshipOfficer", aRelationshipOfficer);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the RelationshipOfficerListbox
		 * from the dialog when we do a delete, edit or insert a
		 * RelationshipOfficer.
		 */
		map.put("relationshipOfficerListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/RelationshipOfficer/RelationshipOfficerDialog.zul",	null, map);
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
		PTMessageUtils.showHelpWindow(event, window_RelationshipOfficerList);
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
		this.pagingRelationshipOfficerList.setActivePage(0);
		Events.postEvent("onCreate", this.window_RelationshipOfficerList, event);
		this.window_RelationshipOfficerList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the RelationshipOfficer dialog
	 */

	public void onClick$button_RelationshipOfficerList_RelationshipOfficerSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our RelationshipOfficerDialog ZUL-file with parameters.
		 * So we can call them with a object of the selected
		 * RelationshipOfficer. For handed over these parameter only a Map is
		 * accepted. So we put the RelationshipOfficer object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("relationshipOfficerCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
				"/WEB-INF/pages/ApplicationMaster/RelationshipOfficer/RelationshipOfficerSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the relationshipOfficer print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_RelationshipOfficerList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("RelationshipOfficer", getSearchObj(),this.pagingRelationshipOfficerList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setRelationshipOfficerService(
			RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}
	public RelationshipOfficerService getRelationshipOfficerService() {
		return this.relationshipOfficerService;
	}

	public JdbcSearchObject<RelationshipOfficer> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<RelationshipOfficer> searchObj) {
		this.searchObj = searchObj;
	}
}