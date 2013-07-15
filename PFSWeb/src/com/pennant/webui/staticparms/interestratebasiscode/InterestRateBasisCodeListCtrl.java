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
 * FileName    		:  InterestRateBasisCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.interestratebasiscode;

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
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.service.staticparms.InterestRateBasisCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.staticparms.interestratebasiscode.model.InterestRateBasisCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/InterestRateBasisCode/InterestRateBasisCodeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class InterestRateBasisCodeListCtrl extends GFCBaseListCtrl<InterestRateBasisCode> implements Serializable {

	private static final long serialVersionUID = -2097643737104268398L;
	private final static Logger logger = Logger.getLogger(InterestRateBasisCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_InterestRateBasisCodeList; 		// autoWired
	protected Borderlayout 	borderLayout_InterestRateBasisCodeList; // autoWired
	protected Paging 		pagingInterestRateBasisCodeList; 		// autoWired
	protected Listbox 		listBoxInterestRateBasisCode; 			// autoWired

	// List headers
	protected Listheader listheader_IntRateBasisCode; 				// autoWired
	protected Listheader listheader_IntRateBasisDesc; 				// autoWired
	protected Listheader listheader_IntRateBasisIsActive; 			// autoWired
	protected Listheader listheader_RecordStatus; 					// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 															// autoWired
	protected Button button_InterestRateBasisCodeList_NewInterestRateBasisCode; 		// autoWired
	protected Button button_InterestRateBasisCodeList_InterestRateBasisCodeSearchDialog;// autoWired
	protected Button button_InterestRateBasisCodeList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<InterestRateBasisCode> searchObj;

	private transient InterestRateBasisCodeService interestRateBasisCodeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public InterestRateBasisCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected InterestRateBasisCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InterestRateBasisCodeList(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("InterestRateBasisCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("InterestRateBasisCode");

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

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_InterestRateBasisCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingInterestRateBasisCodeList.setPageSize(getListRows());
		this.pagingInterestRateBasisCodeList.setDetailed(true);

		this.listheader_IntRateBasisCode.setSortAscending(new FieldComparator("intRateBasisCode", true));
		this.listheader_IntRateBasisCode.setSortDescending(new FieldComparator("intRateBasisCode", false));
		this.listheader_IntRateBasisDesc.setSortAscending(new FieldComparator("intRateBasisDesc", true));
		this.listheader_IntRateBasisDesc.setSortDescending(new FieldComparator("intRateBasisDesc", false));
		this.listheader_IntRateBasisIsActive.setSortAscending(new FieldComparator("intRateBasisIsActive",true));
		this.listheader_IntRateBasisIsActive.setSortDescending(new FieldComparator("intRateBasisIsActive",false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<InterestRateBasisCode>(InterestRateBasisCode.class, getListRows());
		this.searchObj.addSort("IntRateBasisCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTIntRateBasisCodes_View");
			if (isFirstTask()) {
				button_InterestRateBasisCodeList_NewInterestRateBasisCode.setVisible(true);
			} else {
				button_InterestRateBasisCodeList_NewInterestRateBasisCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTIntRateBasisCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_InterestRateBasisCodeList_NewInterestRateBasisCode.setVisible(false);
			this.button_InterestRateBasisCodeList_InterestRateBasisCodeSearchDialog.setVisible(false);
			this.button_InterestRateBasisCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxInterestRateBasisCode,this.pagingInterestRateBasisCodeList);
			// set the itemRenderer
			this.listBoxInterestRateBasisCode.setItemRenderer(new InterestRateBasisCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("InterestRateBasisCodeList");
		this.button_InterestRateBasisCodeList_NewInterestRateBasisCode.setVisible(getUserWorkspace()
				.isAllowed("button_InterestRateBasisCodeList_NewInterestRateBasisCode"));
		this.button_InterestRateBasisCodeList_InterestRateBasisCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_InterestRateBasisCodeList_InterestRateBasisCodeFindDialog"));
		this.button_InterestRateBasisCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_InterestRateBasisCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.StaticParms.interestratebasiscode.model.
	 * InterestRateBasisCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onInterestRateBasisCodeItemDoubleClicked(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected InterestRateBasisCode object
		final Listitem item = this.listBoxInterestRateBasisCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final InterestRateBasisCode aInterestRateBasisCode = (InterestRateBasisCode) item.getAttribute("data");
			final InterestRateBasisCode interestRateBasisCode = getInterestRateBasisCodeService()
			.getInterestRateBasisCodeById(aInterestRateBasisCode.getId());

			if (interestRateBasisCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aInterestRateBasisCode.getIntRateBasisCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_IntRateBasisCode")+ ":"+ aInterestRateBasisCode.getIntRateBasisCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND IntRateBasisCode='"
					+ interestRateBasisCode.getIntRateBasisCode()
					+ "' AND version=" + interestRateBasisCode.getVersion()+ " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"InterestRateBasisCode", whereCond,interestRateBasisCode.getTaskId(),interestRateBasisCode.getNextTaskId());
					if (userAcces) {
						showDetailView(interestRateBasisCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(interestRateBasisCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the InterestRateBasisCode dialog with a new empty entry. <br>
	 */
	public void onClick$button_InterestRateBasisCodeList_NewInterestRateBasisCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new InterestRateBasisCode object, We GET it from the
		// backEnd.
		final InterestRateBasisCode aInterestRateBasisCode = getInterestRateBasisCodeService().getNewInterestRateBasisCode();
		showDetailView(aInterestRateBasisCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param InterestRateBasisCode
	 *            (aInterestRateBasisCode)
	 * @throws Exception
	 */
	private void showDetailView(InterestRateBasisCode aInterestRateBasisCode)throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aInterestRateBasisCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aInterestRateBasisCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("interestRateBasisCode", aInterestRateBasisCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the InterestRateBasisCodeListbox
		 * from the dialog when we do a delete, edit or insert a
		 * InterestRateBasisCode.
		 */
		map.put("interestRateBasisCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/InterestRateBasisCode/InterestRateBasisCodeDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_InterestRateBasisCodeList);
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
		this.pagingInterestRateBasisCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_InterestRateBasisCodeList,event);
		this.window_InterestRateBasisCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the InterestRateBasisCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_InterestRateBasisCodeList_InterestRateBasisCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our InterestRateBasisCodeDialog ZUL-file with parameters.
		 * So we can call them with a object of the selected
		 * InterestRateBasisCode. For handed over these parameter only a Map is
		 * accepted. So we put the InterestRateBasisCode object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("interestRateBasisCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/InterestRateBasisCode/InterestRateBasisCodeSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the interestRateBasisCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_InterestRateBasisCodeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("InterestRateBasisCode", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setInterestRateBasisCodeService(
			InterestRateBasisCodeService interestRateBasisCodeService) {
		this.interestRateBasisCodeService = interestRateBasisCodeService;
	}
	public InterestRateBasisCodeService getInterestRateBasisCodeService() {
		return this.interestRateBasisCodeService;
	}

	public JdbcSearchObject<InterestRateBasisCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<InterestRateBasisCode> searchObj) {
		this.searchObj = searchObj;
	}
}