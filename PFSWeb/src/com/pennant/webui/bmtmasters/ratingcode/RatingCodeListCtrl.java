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
 * FileName    		:  RatingCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.bmtmasters.ratingcode;

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
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.service.bmtmasters.RatingCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.bmtmasters.ratingcode.model.RatingCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/BMTMasters/RatingCode/RatingCodeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class RatingCodeListCtrl extends GFCBaseListCtrl<RatingCode> implements Serializable {

	private static final long serialVersionUID = 6738264839727215072L;
	private final static Logger logger = Logger.getLogger(RatingCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RatingCodeList; 		// autoWired
	protected Borderlayout 	borderLayout_RatingCodeList;// autoWired
	protected Paging 		pagingRatingCodeList; 		// autoWired
	protected Listbox 		listBoxRatingCode; 			// autoWired

	// List headers
	protected Listheader 	listheader_RatingType; 		// autoWired
	protected Listheader 	listheader_RatingCode; 		// autoWired
	protected Listheader 	listheader_RatingCodeDesc; 	// autoWired
	protected Listheader 	listheader_RatingIsActive; 	// autoWired
	protected Listheader 	listheader_RecordStatus; 	// autoWired
	protected Listheader 	listheader_RecordType;

	// checkRights
	protected Button 		btnHelp; 									  // autoWired
	protected Button 		button_RatingCodeList_NewRatingCode; 		  // autoWired
	protected Button 		button_RatingCodeList_RatingCodeSearchDialog; // autoWired
	protected Button 		button_RatingCodeList_PrintList; 			  // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<RatingCode> searchObj;

	private transient RatingCodeService ratingCodeService;
	private transient WorkFlowDetails 	workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public RatingCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected RatingCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RatingCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("RatingCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RatingCode");

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
		this.borderLayout_RatingCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingRatingCodeList.setPageSize(getListRows());
		this.pagingRatingCodeList.setDetailed(true);

		this.listheader_RatingType.setSortAscending(new FieldComparator("ratingType", true));
		this.listheader_RatingType.setSortDescending(new FieldComparator("ratingType", false));
		this.listheader_RatingCode.setSortAscending(new FieldComparator("ratingCode", true));
		this.listheader_RatingCode.setSortDescending(new FieldComparator("ratingCode", false));
		this.listheader_RatingCodeDesc.setSortAscending(new FieldComparator("ratingCodeDesc", true));
		this.listheader_RatingCodeDesc.setSortDescending(new FieldComparator("ratingCodeDesc", false));
		this.listheader_RatingIsActive.setSortAscending(new FieldComparator("ratingIsActive", true));
		this.listheader_RatingIsActive.setSortDescending(new FieldComparator("ratingIsActive", false));

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
		this.searchObj = new JdbcSearchObject<RatingCode>(RatingCode.class,getListRows());
		this.searchObj.addSort("RatingType", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTRatingCodes_View");
			if (isFirstTask()) {
				button_RatingCodeList_NewRatingCode.setVisible(true);
			} else {
				button_RatingCodeList_NewRatingCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}
		else{
			this.searchObj.addTabelName("BMTRatingCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_RatingCodeList_NewRatingCode.setVisible(false);
			this.button_RatingCodeList_RatingCodeSearchDialog.setVisible(false);
			this.button_RatingCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxRatingCode,this.pagingRatingCodeList);
			// set the itemRenderer
			this.listBoxRatingCode.setItemRenderer(new RatingCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("RatingCodeList");

		this.button_RatingCodeList_NewRatingCode.setVisible(getUserWorkspace()
				.isAllowed("button_RatingCodeList_NewRatingCode"));
		this.button_RatingCodeList_RatingCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_RatingCodeList_RatingCodeFindDialog"));
		this.button_RatingCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_RatingCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.ratingcode.model.
	 * RatingCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onRatingCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected RatingCode object
		final Listitem item = this.listBoxRatingCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final RatingCode aRatingCode = (RatingCode) item.getAttribute("data");
			final RatingCode ratingCode = getRatingCodeService()
			.getRatingCodeById(aRatingCode.getRatingType(),	aRatingCode.getRatingCode());

			if (ratingCode == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aRatingCode.getRatingType();
				valueParm[1] = aRatingCode.getRatingCode();

				errParm[0] = PennantJavaUtil.getLabel("label_RatingType") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_RatingCode") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND RatingType='" + ratingCode.getRatingType() 
				+ "' AND version=" + ratingCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"RatingCode", whereCond, ratingCode.getTaskId(),ratingCode.getNextTaskId());
					if (userAcces) {
						showDetailView(ratingCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(ratingCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the RatingCode dialog with a new empty entry. <br>
	 */
	public void onClick$button_RatingCodeList_NewRatingCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new RatingCode object, We GET it from the back end.
		final RatingCode aRatingCode = getRatingCodeService().getNewRatingCode();
		showDetailView(aRatingCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param RatingCode
	 *            (aRatingCode)
	 * @throws Exception
	 */
	private void showDetailView(RatingCode aRatingCode) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aRatingCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aRatingCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ratingCode", aRatingCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the RatingCodeListbox from the
		 * dialog when we do a delete, edit or insert a RatingCode.
		 */
		map.put("ratingCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/BMTMasters/RatingCode/RatingCodeDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_RatingCodeList);
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
		this.pagingRatingCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_RatingCodeList, event);
		this.window_RatingCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the RatingCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_RatingCodeList_RatingCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our RatingCodeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected RatingCode. For handed over
		 * these parameter only a Map is accepted. So we put the RatingCode
		 * object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ratingCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/BMTMasters/RatingCode/RatingCodeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the ratingCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_RatingCodeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("RatingCode", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setRatingCodeService(RatingCodeService ratingCodeService) {
		this.ratingCodeService = ratingCodeService;
	}
	public RatingCodeService getRatingCodeService() {
		return this.ratingCodeService;
	}

	public JdbcSearchObject<RatingCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<RatingCode> searchObj) {
		this.searchObj = searchObj;
	}
}