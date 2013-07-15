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
 * FileName    		:  DispatchModeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-08-2011    														*
 *                                                                  						*
 * Modified Date    :  18-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.dispatchmode;

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
import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.service.systemmasters.DispatchModeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.dispatchmode.model.DispatchModeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/DispatchMode/DispatchModeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DispatchModeListCtrl extends GFCBaseListCtrl<DispatchMode> implements Serializable {

	private static final long serialVersionUID = 3085856113492519328L;
	private final static Logger logger = Logger.getLogger(DispatchModeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DispatchModeList; 		// autoWired
	protected Borderlayout 	borderLayout_DispatchModeList; 	// autoWired
	protected Paging 		pagingDispatchModeList; 		// autoWired
	protected Listbox 		listBoxDispatchMode; 			// autoWired

	// List headers
	protected Listheader listheader_DispatchModeCode; 		// autoWired
	protected Listheader listheader_DispatchModeDesc; 		// autoWired
	protected Listheader listheader_DispatchModeIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// autoWired
	protected Button button_DispatchModeList_NewDispatchMode; 			// autoWired
	protected Button button_DispatchModeList_DispatchModeSearchDialog; 	// autoWired
	protected Button button_DispatchModeList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DispatchMode> searchObj;

	private transient DispatchModeService dispatchModeService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public DispatchModeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected DispatchMode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DispatchModeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("DispatchMode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DispatchMode");

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

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_DispatchModeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingDispatchModeList.setPageSize(getListRows());
		this.pagingDispatchModeList.setDetailed(true);

		this.listheader_DispatchModeCode.setSortAscending(new FieldComparator("dispatchModeCode", true));
		this.listheader_DispatchModeCode.setSortDescending(new FieldComparator("dispatchModeCode", false));
		this.listheader_DispatchModeDesc.setSortAscending(new FieldComparator("dispatchModeDesc", true));
		this.listheader_DispatchModeDesc.setSortDescending(new FieldComparator("dispatchModeDesc", false));
		this.listheader_DispatchModeIsActive.setSortAscending(new FieldComparator("dispatchModeIsActive", true));
		this.listheader_DispatchModeIsActive.setSortDescending(new FieldComparator("dispatchModeIsActive", false));

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
		this.searchObj = new JdbcSearchObject<DispatchMode>(DispatchMode.class, getListRows());
		this.searchObj.addSort("DispatchModeCode", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTDispatchModes_View");
			if (isFirstTask()) {
				button_DispatchModeList_NewDispatchMode.setVisible(true);
			} else {
				button_DispatchModeList_NewDispatchMode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTDispatchModes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_DispatchModeList_NewDispatchMode.setVisible(false);
			this.button_DispatchModeList_DispatchModeSearchDialog.setVisible(false);
			this.button_DispatchModeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxDispatchMode, this.pagingDispatchModeList);
			// set the itemRenderer
			this.listBoxDispatchMode.setItemRenderer(new DispatchModeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("DispatchModeList");

		this.button_DispatchModeList_NewDispatchMode.setVisible(getUserWorkspace()
				.isAllowed("button_DispatchModeList_NewDispatchMode"));
		this.button_DispatchModeList_DispatchModeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_DispatchModeList_DispatchModeFindDialog"));
		this.button_DispatchModeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_DispatchModeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.dispatchmode.model.
	 * DispatchModeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDispatchModeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected DispatchMode object
		final Listitem item = this.listBoxDispatchMode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DispatchMode aDispatchMode = (DispatchMode) item.getAttribute("data");
			final DispatchMode dispatchMode = getDispatchModeService().getDispatchModeById(aDispatchMode.getId());

			if (dispatchMode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aDispatchMode.getDispatchModeCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_DispatchModeCode") + ":"	+ aDispatchMode.getDispatchModeCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND DispatchModeCode='" + dispatchMode.getDispatchModeCode() 
				+ "' AND version=" + dispatchMode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"DispatchMode", whereCond,dispatchMode.getTaskId(), dispatchMode.getNextTaskId());
					if (userAcces) {
						showDetailView(dispatchMode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(dispatchMode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the DispatchMode dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DispatchModeList_NewDispatchMode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new DispatchMode object, We GET it from the backEnd.
		final DispatchMode aDispatchMode = getDispatchModeService().getNewDispatchMode();
		showDetailView(aDispatchMode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param DispatchMode
	 *            (aDispatchMode)
	 * @throws Exception
	 */
	private void showDetailView(DispatchMode aDispatchMode) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aDispatchMode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aDispatchMode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dispatchMode", aDispatchMode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the DispatchModeListbox from the
		 * dialog when we do a delete, edit or insert a DispatchMode.
		 */
		map.put("dispatchModeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/DispatchMode/DispatchModeDialog.zul", null, map);
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
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_DispatchModeList);
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
		this.pagingDispatchModeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_DispatchModeList, event);
		this.window_DispatchModeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the DispatchMode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DispatchModeList_DispatchModeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our DispatchModeDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected DispatchMode. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * DispatchMode object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dispatchModeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/DispatchMode/DispatchModeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the dispatchMode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_DispatchModeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("DispatchMode", getSearchObj(),this.pagingDispatchModeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDispatchModeService(DispatchModeService dispatchModeService) {
		this.dispatchModeService = dispatchModeService;
	}
	public DispatchModeService getDispatchModeService() {
		return this.dispatchModeService;
	}

	public JdbcSearchObject<DispatchMode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<DispatchMode> searchObj) {
		this.searchObj = searchObj;
	}
}