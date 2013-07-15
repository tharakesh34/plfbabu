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
 * FileName    		:  PRelationCodeListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.prelationcode;

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
import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennant.backend.service.systemmasters.PRelationCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.prelationcode.model.PRelationCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/PRelationCode/PRelationCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PRelationCodeListCtrl extends GFCBaseListCtrl<PRelationCode> implements Serializable {

	private static final long serialVersionUID = -6390654977697169073L;
	private final static Logger logger = Logger.getLogger(PRelationCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_PRelationCodeList; 		// autoWired
	protected Borderlayout 	borderLayout_PRelationCodeList; // autoWired
	protected Paging 		pagingPRelationCodeList; 		// autoWired
	protected Listbox 		listBoxPRelationCode; 			// autoWired

	// List headers
	protected Listheader 	listheader_PRelationCode; 		// autoWired
	protected Listheader 	listheader_PRelationDesc; 		// autoWired
	protected Listheader 	listheader_RelationCodeIsActive;// autoWired
	protected Listheader 	listheader_RecordStatus; 		// autoWired
	protected Listheader 	listheader_RecordType;

	// checkRights
	protected Button 		btnHelp; 											// autoWired
	protected Button 		button_PRelationCodeList_NewPRelationCode; 			// autoWired
	protected Button 		button_PRelationCodeList_PRelationCodeSearchDialog; // autoWired
	protected Button 		button_PRelationCodeList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<PRelationCode> searchObj;

	private transient PRelationCodeService 	pRelationCodeService;
	private transient WorkFlowDetails 		workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public PRelationCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected PRelationCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PRelationCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("PRelationCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PRelationCode");

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
		this.borderLayout_PRelationCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingPRelationCodeList.setPageSize(getListRows());
		this.pagingPRelationCodeList.setDetailed(true);

		this.listheader_PRelationCode.setSortAscending(new FieldComparator("pRelationCode", true));
		this.listheader_PRelationCode.setSortDescending(new FieldComparator("pRelationCode", false));
		this.listheader_PRelationDesc.setSortAscending(new FieldComparator("pRelationDesc", true));
		this.listheader_PRelationDesc.setSortDescending(new FieldComparator("pRelationDesc", false));
		this.listheader_RelationCodeIsActive.setSortAscending(new FieldComparator("relationCodeIsActive",true));
		this.listheader_RelationCodeIsActive.setSortDescending(new FieldComparator("relationCodeIsActive",false));

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
		this.searchObj = new JdbcSearchObject<PRelationCode>(PRelationCode.class, getListRows());
		this.searchObj.addSort("PRelationCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTPRelationCodes_View");
			if (isFirstTask()) {
				button_PRelationCodeList_NewPRelationCode.setVisible(true);
			} else {
				button_PRelationCodeList_NewPRelationCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTPRelationCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_PRelationCodeList_NewPRelationCode.setVisible(false);
			this.button_PRelationCodeList_PRelationCodeSearchDialog.setVisible(false);
			this.button_PRelationCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxPRelationCode, this.pagingPRelationCodeList);
			// set the itemRenderer
			this.listBoxPRelationCode.setItemRenderer(new PRelationCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("PRelationCodeList");

		this.button_PRelationCodeList_NewPRelationCode.setVisible(getUserWorkspace()
				.isAllowed("button_PRelationCodeList_NewPRelationCode"));
		this.button_PRelationCodeList_PRelationCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_PRelationCodeList_PRelationCodeFindDialog"));
		this.button_PRelationCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_PRelationCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.prelationcode.model.
	 * PRelationCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onPRelationCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected PRelationCode object
		final Listitem item = this.listBoxPRelationCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final PRelationCode aPRelationCode = (PRelationCode) item.getAttribute("data");
			final PRelationCode pRelationCode = getPRelationCodeService().getPRelationCodeById(aPRelationCode.getId());

			if (pRelationCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aPRelationCode.getPRelationCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_PRelationCode")+ ":" + aPRelationCode.getPRelationCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND PRelationCode='"+ pRelationCode.getPRelationCode() 
				+ "' AND version="+ pRelationCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"PRelationCode", whereCond,pRelationCode.getTaskId(),pRelationCode.getNextTaskId());
					if (userAcces) {
						showDetailView(pRelationCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(pRelationCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the PRelationCode dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PRelationCodeList_NewPRelationCode(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new PRelationCode object, We GET it from the back end.
		final PRelationCode aPRelationCode = getPRelationCodeService().getNewPRelationCode();
		showDetailView(aPRelationCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param PRelationCode
	 *            (aPRelationCode)
	 * @throws Exception
	 */
	private void showDetailView(PRelationCode aPRelationCode) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aPRelationCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aPRelationCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("pRelationCode", aPRelationCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the PRelationCodeListbox from the
		 * dialog when we do a delete, edit or insert a PRelationCode.
		 */
		map.put("pRelationCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/PRelationCode/PRelationCodeDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_PRelationCodeList);
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
		this.pagingPRelationCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_PRelationCodeList, event);
		this.window_PRelationCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the PRelationCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PRelationCodeList_PRelationCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our PRelationCodeDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected PRelationCode. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * PRelationCode object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("pRelationCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/PRelationCode/PRelationCodeSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the pRelationCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_PRelationCodeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("PRelationCode", getSearchObj(),this.pagingPRelationCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setPRelationCodeService(PRelationCodeService pRelationCodeService) {
		this.pRelationCodeService = pRelationCodeService;
	}
	public PRelationCodeService getPRelationCodeService() {
		return this.pRelationCodeService;
	}

	public JdbcSearchObject<PRelationCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<PRelationCode> searchObj) {
		this.searchObj = searchObj;
	}
}