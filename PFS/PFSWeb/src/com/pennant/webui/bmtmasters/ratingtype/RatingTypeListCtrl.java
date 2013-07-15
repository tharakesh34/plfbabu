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
 * FileName    		:  RatingTypeListCtrl.java                                                   * 	  
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

package com.pennant.webui.bmtmasters.ratingtype;

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
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.service.bmtmasters.RatingTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.bmtmasters.ratingtype.model.RatingTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/RatingType/RatingTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class RatingTypeListCtrl extends GFCBaseListCtrl<RatingType> implements Serializable {

	private static final long serialVersionUID = -342231205402716010L;
	private final static Logger logger = Logger.getLogger(RatingTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RatingTypeList; 		// autoWired
	protected Panel 		panel_RatingTypeList; 		// autoWired
	protected Borderlayout 	borderLayout_RatingTypeList;// autoWired
	protected Paging 		pagingRatingTypeList; 		// autoWired
	protected Listbox 		listBoxRatingType; 			// autoWired

	// List headers
	protected Listheader 	listheader_RatingType; 		// autoWired
	protected Listheader 	listheader_RatingTypeDesc; 	// autoWired
	protected Listheader 	listheader_ValueType; 		// autoWired
	protected Listheader 	listheader_ValueLen; 		// autoWired
	protected Listheader 	listheader_RatingIsActive; 	// autoWired
	protected Listheader 	listheader_RecordStatus; 	// autoWired
	protected Listheader 	listheader_RecordType;

	// checkRights
	protected Button 		btnHelp; 										// autoWired
	protected Button 		button_RatingTypeList_NewRatingType; 			// autoWired
	protected Button 		button_RatingTypeList_RatingTypeSearchDialog; 	// autoWired
	protected Button 		button_RatingTypeList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<RatingType> searchObj;

	private transient RatingTypeService ratingTypeService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public RatingTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected RatingTypeCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RatingTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("RatingType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RatingType");

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
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_RatingTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingRatingTypeList.setPageSize(getListRows());
		this.pagingRatingTypeList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_RatingType.setSortAscending(new FieldComparator("ratingType", true));
		this.listheader_RatingType.setSortDescending(new FieldComparator("ratingType", false));
		this.listheader_RatingTypeDesc.setSortAscending(new FieldComparator("ratingTypeDesc", true));
		this.listheader_RatingTypeDesc.setSortDescending(new FieldComparator("ratingTypeDesc", false));
		this.listheader_ValueType.setSortAscending(new FieldComparator("valueType", true));
		this.listheader_ValueType.setSortDescending(new FieldComparator("valueType", false));
		this.listheader_ValueLen.setSortAscending(new FieldComparator("valueLen", true));
		this.listheader_ValueLen.setSortDescending(new FieldComparator("valueLen", false));
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
		this.searchObj = new JdbcSearchObject<RatingType>(RatingType.class,getListRows());
		this.searchObj.addSort("RatingType", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTRatingTypes_View");
			if (isFirstTask()) {
				button_RatingTypeList_NewRatingType.setVisible(true);
			} else {
				button_RatingTypeList_NewRatingType.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTRatingTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_RatingTypeList_NewRatingType.setVisible(false);
			this.button_RatingTypeList_RatingTypeSearchDialog.setVisible(false);
			this.button_RatingTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxRatingType,this.pagingRatingTypeList);
			// set the itemRenderer
			this.listBoxRatingType.setItemRenderer(new RatingTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("RatingTypeList");

		this.button_RatingTypeList_NewRatingType.setVisible(getUserWorkspace()
				.isAllowed("button_RatingTypeList_NewRatingType"));
		this.button_RatingTypeList_RatingTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_RatingTypeList_RatingTypeFindDialog"));
		this.button_RatingTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_RatingTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.ratingtype.model.
	 * RatingTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onRatingTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected RatingType object
		final Listitem item = this.listBoxRatingType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final RatingType aRatingType = (RatingType) item.getAttribute("data");
			final RatingType ratingType = getRatingTypeService().getRatingTypeById(aRatingType.getId());

			if (ratingType == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aRatingType.getRatingType();
				errorParm[0] = PennantJavaUtil.getLabel("label_RatingType")+ ":" + aRatingType.getRatingType();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND RatingType='"+ ratingType.getRatingType()
				+ "' AND version="+ ratingType.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(
							workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"RatingType", whereCond, ratingType.getTaskId(),ratingType.getNextTaskId());
					if (userAcces) {
						showDetailView(ratingType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(ratingType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the RatingType dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_RatingTypeList_NewRatingType(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new RatingType object, We GET it from the backEnd.
		final RatingType aRatingType = getRatingTypeService().getNewRatingType();
		aRatingType.setValueLen(0); // initialize
		showDetailView(aRatingType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param RatingType
	 *            (aRatingType)
	 * @throws Exception
	 */
	private void showDetailView(RatingType aRatingType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aRatingType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aRatingType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ratingType", aRatingType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the RatingTypeListbox from the
		 * dialog when we do a delete, edit or insert a RatingType.
		 */
		map.put("ratingTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/BMTMasters/RatingType/RatingTypeDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_RatingTypeList);
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
		this.pagingRatingTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_RatingTypeList, event);
		this.window_RatingTypeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the RatingType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_RatingTypeList_RatingTypeSearchDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our RatingTypeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected RatingType. For handed over
		 * these parameter only a Map is accepted. So we put the RatingType
		 * object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ratingTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/BMTMasters/RatingType/RatingTypeSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the ratingType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_RatingTypeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("RatingType", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setRatingTypeService(RatingTypeService ratingTypeService) {
		this.ratingTypeService = ratingTypeService;
	}
	public RatingTypeService getRatingTypeService() {
		return this.ratingTypeService;
	}

	public JdbcSearchObject<RatingType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<RatingType> searchObj) {
		this.searchObj = searchObj;
	}

}