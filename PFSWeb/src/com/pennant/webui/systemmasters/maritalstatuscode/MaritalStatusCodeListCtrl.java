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
 * FileName    		:  MaritalStatusCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.maritalstatuscode;

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
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.service.systemmasters.MaritalStatusCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.maritalstatuscode.model.MaritalStatusCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/MaritalStatusCode/MaritalStatusCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class MaritalStatusCodeListCtrl extends GFCBaseListCtrl<MaritalStatusCode> implements Serializable {

	private static final long serialVersionUID = -8496246844446191225L;
	private final static Logger logger = Logger.getLogger(MaritalStatusCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_MaritalStatusCodeList; 		 // autoWired
	protected Borderlayout 	borderLayout_MaritalStatusCodeList;  // autoWired
	protected Paging 		pagingMaritalStatusCodeList; 		 // autoWired
	protected Listbox 		listBoxMaritalStatusCode; 			 // autoWired

	// List headers
	protected Listheader listheader_MaritalStsCode; 			// autoWired
	protected Listheader listheader_MaritalStsDesc; 			// autoWired
	protected Listheader listheader_MaritalStsIsActive; 		// autoWired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_MaritalStatusCodeList_NewMaritalStatusCode; 		// autoWired
	protected Button button_MaritalStatusCodeList_MaritalStatusCodeSearchDialog;// autoWired
	protected Button button_MaritalStatusCodeList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<MaritalStatusCode> searchObj;

	private transient MaritalStatusCodeService maritalStatusCodeService;
	private transient WorkFlowDetails 		   workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public MaritalStatusCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSegmentCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_MaritalStatusCodeList(Event event)
	throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("MaritalStatusCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("MaritalStatusCode");

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
		 * currentDesktopHeight from a hidden Initialize box from the index.zul
		 * that are filled by onClientInfo() in the indexCtroller
		 */

		this.borderLayout_MaritalStatusCodeList
		.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingMaritalStatusCodeList.setPageSize(getListRows());
		this.pagingMaritalStatusCodeList.setDetailed(true);

		this.listheader_MaritalStsCode.setSortAscending(new FieldComparator("maritalStsCode", true));
		this.listheader_MaritalStsCode.setSortDescending(new FieldComparator("maritalStsCode", false));
		this.listheader_MaritalStsDesc.setSortAscending(new FieldComparator("maritalStsDesc", true));
		this.listheader_MaritalStsDesc.setSortDescending(new FieldComparator("maritalStsDesc", false));
		this.listheader_MaritalStsIsActive.setSortAscending(new FieldComparator("maritalStsIsActive", true));
		this.listheader_MaritalStsIsActive.setSortDescending(new FieldComparator("maritalStsIsActive", false));

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
		this.searchObj = new JdbcSearchObject<MaritalStatusCode>(MaritalStatusCode.class, getListRows());
		this.searchObj.addSort("MaritalStsCode", false);
		this.searchObj.addFilter(new Filter("MaritalStsCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTMaritalStatusCodes_View");
			if (isFirstTask()) {
				button_MaritalStatusCodeList_NewMaritalStatusCode.setVisible(true);
			} else {
				button_MaritalStatusCodeList_NewMaritalStatusCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTMaritalStatusCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_MaritalStatusCodeList_NewMaritalStatusCode.setVisible(false);
			this.button_MaritalStatusCodeList_MaritalStatusCodeSearchDialog.setVisible(false);
			this.button_MaritalStatusCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxMaritalStatusCode, this.pagingMaritalStatusCodeList);
			// set the itemRenderer
			this.listBoxMaritalStatusCode.setItemRenderer(new MaritalStatusCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("MaritalStatusCodeList");

		this.button_MaritalStatusCodeList_NewMaritalStatusCode.setVisible(getUserWorkspace()
				.isAllowed("button_MaritalStatusCodeList_NewMaritalStatusCode"));
		this.button_MaritalStatusCodeList_MaritalStatusCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_MaritalStatusCodeList_MaritalStatusCodeFindDialog"));
		this.button_MaritalStatusCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_MaritalStatusCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.maritalstatuscode.model.
	 * MaritalStatusCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onMaritalStatusCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected MaritalStatusCode object
		final Listitem item = this.listBoxMaritalStatusCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final MaritalStatusCode aMaritalStatusCode = (MaritalStatusCode) item.getAttribute("data");
			final MaritalStatusCode maritalStatusCode = getMaritalStatusCodeService()
			.getMaritalStatusCodeById(aMaritalStatusCode.getId());

			if (maritalStatusCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aMaritalStatusCode.getMaritalStsCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_MaritalStsCode")	+ ":" + aMaritalStatusCode.getMaritalStsCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND MaritalStsCode='"+ maritalStatusCode.getMaritalStsCode()
				+ "' AND version=" + maritalStatusCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"MaritalStatusCode", whereCond, maritalStatusCode.getTaskId(), maritalStatusCode.getNextTaskId());
					if (userAcces) {
						showDetailView(maritalStatusCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(maritalStatusCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the MaritalStatusCode dialog with a new empty entry. <br>
	 */
	public void onClick$button_MaritalStatusCodeList_NewMaritalStatusCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new MaritalStatusCode object, We GET it from the back end.
		final MaritalStatusCode aMaritalStatusCode = getMaritalStatusCodeService().getNewMaritalStatusCode();
		showDetailView(aMaritalStatusCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param MaritalStatusCode
	 *            (aMaritalStatusCode)
	 * @throws Exception
	 */
	private void showDetailView(MaritalStatusCode aMaritalStatusCode) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aMaritalStatusCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aMaritalStatusCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("maritalStatusCode", aMaritalStatusCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the MaritalStatusCodeListbox from
		 * the dialog when we do a delete, edit or insert a MaritalStatusCode.
		 */
		map.put("maritalStatusCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/MaritalStatusCode/MaritalStatusCodeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_MaritalStatusCodeList);
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
		this.pagingMaritalStatusCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_MaritalStatusCodeList, event);
		this.window_MaritalStatusCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the MaritalStatusCode dialog
	 */
	public void onClick$button_MaritalStatusCodeList_MaritalStatusCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our MaritalStatusCodeDialog ZUL-file with parameters. So
		 * we can call them with a object of the selected MaritalStatusCode. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * MaritalStatusCode object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("maritalStatusCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/MaritalStatusCode/MaritalStatusCodeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the maritalStatusCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_MaritalStatusCodeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("MaritalStatusCode", getSearchObj(),this.pagingMaritalStatusCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setMaritalStatusCodeService(MaritalStatusCodeService maritalStatusCodeService) {
		this.maritalStatusCodeService = maritalStatusCodeService;
	}
	public MaritalStatusCodeService getMaritalStatusCodeService() {
		return this.maritalStatusCodeService;
	}

	public JdbcSearchObject<MaritalStatusCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<MaritalStatusCode> searchObj) {
		this.searchObj = searchObj;
	}
}