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
 * FileName    		:  BlackListReasonCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.blacklistreasoncode;

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
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.service.systemmasters.impl.BlackListReasonCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.blacklistreasoncode.model.BlackListReasonCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/BlackListReasonCode/BlackListReasonCodeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class BlackListReasonCodeListCtrl extends
GFCBaseListCtrl<BlackListReasonCode> implements Serializable {

	private static final long serialVersionUID = -4787094221203301336L;
	private final static Logger logger = Logger .getLogger(BlackListReasonCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BlackListReasonCodeList; 		// autoWired
	protected Borderlayout 	borderLayout_BlackListReasonCodeList; 	// autoWired
	protected Paging 		pagingBlackListReasonCodeList; 		// autoWired
	protected Listbox 		listBoxBlackListReasonCode; 			// autoWired

	// List headers
	protected Listheader listheader_BLRsnCode; 		// autoWired
	protected Listheader listheader_BLRsnDesc; 		// autoWired
	protected Listheader listheader_BLIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;		// autoWired

	// checkRights
	protected Button btnHelp; 															// autoWired
	protected Button button_BlackListReasonCodeList_NewBlackListReasonCode; 			// autoWired
	protected Button button_BlackListReasonCodeList_BlackListReasonCodeSearchDialog; 	// autoWired
	protected Button button_BlackListReasonCodeList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<BlackListReasonCode> searchObj;

	private transient BlackListReasonCodeService blackListReasonCodeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public BlackListReasonCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected BlackListReasonCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BlackListReasonCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("BlackListReasonCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BlackListReasonCode");

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
		this.borderLayout_BlackListReasonCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingBlackListReasonCodeList.setPageSize(getListRows());
		this.pagingBlackListReasonCodeList.setDetailed(true);

		this.listheader_BLRsnCode.setSortAscending(new FieldComparator("bLRsnCode", true));
		this.listheader_BLRsnCode.setSortDescending(new FieldComparator("bLRsnCode", false));
		this.listheader_BLRsnDesc.setSortAscending(new FieldComparator("bLRsnDesc", true));
		this.listheader_BLRsnDesc.setSortDescending(new FieldComparator("bLRsnDesc", false));
		this.listheader_BLIsActive.setSortAscending(new FieldComparator("bLIsActive", true));
		this.listheader_BLIsActive.setSortDescending(new FieldComparator("bLIsActive", false));

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
		this.searchObj = new JdbcSearchObject<BlackListReasonCode>(
				BlackListReasonCode.class, getListRows());
		this.searchObj.addSort("BLRsnCode", false);
		this.searchObj.addFilter(new Filter("BLRsnCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTBlackListRsnCodes_View");
			if (isFirstTask()) {
				button_BlackListReasonCodeList_NewBlackListReasonCode.setVisible(true);
			} else {
				button_BlackListReasonCodeList_NewBlackListReasonCode.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTBlackListRsnCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_BlackListReasonCodeList_NewBlackListReasonCode.setVisible(false);
			this.button_BlackListReasonCodeList_BlackListReasonCodeSearchDialog.setVisible(false);
			this.button_BlackListReasonCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxBlackListReasonCode, this.pagingBlackListReasonCodeList);
			// set the itemRenderer
			this.listBoxBlackListReasonCode.setItemRenderer(new BlackListReasonCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("BlackListReasonCodeList");
		this.button_BlackListReasonCodeList_NewBlackListReasonCode.setVisible(getUserWorkspace()
				.isAllowed("button_BlackListReasonCodeList_NewBlackListReasonCode"));
		this.button_BlackListReasonCodeList_BlackListReasonCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_BlackListReasonCodeList_BlackListReasonCodeFindDialog"));
		this.button_BlackListReasonCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_BlackListReasonCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.blacklistreasoncode.model.
	 * BlackListReasonCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onBlackListReasonCodeItemDoubleClicked(Event event)
	throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected BlackListReasonCode object
		final Listitem item = this.listBoxBlackListReasonCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final BlackListReasonCode aBlackListReasonCode = (BlackListReasonCode) item.getAttribute("data");
			final BlackListReasonCode blackListReasonCode = getBlackListReasonCodeService()
			.getBlackListReasonCodeById(aBlackListReasonCode.getId());

			if (blackListReasonCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aBlackListReasonCode.getBLRsnCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_BLRsnCode") + ":" + aBlackListReasonCode.getBLRsnCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND BLRsnCode='" + blackListReasonCode.getBLRsnCode()
				+ "' AND version=" + blackListReasonCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"BlackListReasonCode", whereCond, blackListReasonCode.getTaskId(),blackListReasonCode.getNextTaskId());
					if (userAcces) {
						showDetailView(blackListReasonCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(blackListReasonCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the BlackListReasonCode dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BlackListReasonCodeList_NewBlackListReasonCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new BlackListReasonCode object, We GET it from the backEnd.
		final BlackListReasonCode aBlackListReasonCode = getBlackListReasonCodeService().getNewBlackListReasonCode();
		showDetailView(aBlackListReasonCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param BlackListReasonCode
	 *            (aBlackListReasonCode)
	 * @throws Exception
	 */
	private void showDetailView(BlackListReasonCode aBlackListReasonCode)
	throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aBlackListReasonCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aBlackListReasonCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("blackListReasonCode", aBlackListReasonCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the BlackListReasonCodeListbox
		 * from the dialog when we do a delete, edit or insert a
		 * BlackListReasonCode.
		 */
		map.put("blackListReasonCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/BlackListReasonCode/BlackListReasonCodeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_BlackListReasonCodeList);
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
		this.pagingBlackListReasonCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_BlackListReasonCodeList, event);
		this.window_BlackListReasonCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the BlackListReasonCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BlackListReasonCodeList_BlackListReasonCodeSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our BlackListReasonCodeDialog ZUL-file with parameters.
		 * So we can call them with a object of the selected
		 * BlackListReasonCode. For handed over these parameter only a Map is
		 * accepted. So we put the BlackListReasonCode object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("blackListReasonCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/BlackListReasonCode/BlackListReasonCodeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the blackListReasonCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_BlackListReasonCodeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("BlackListReasonCode", getSearchObj(),this.pagingBlackListReasonCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setBlackListReasonCodeService(BlackListReasonCodeService blackListReasonCodeService) {
		this.blackListReasonCodeService = blackListReasonCodeService;
	}
	public BlackListReasonCodeService getBlackListReasonCodeService() {
		return this.blackListReasonCodeService;
	}

	public JdbcSearchObject<BlackListReasonCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<BlackListReasonCode> searchObj) {
		this.searchObj = searchObj;
	}
}