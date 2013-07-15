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
 * FileName    		:  InterestRateTypeListCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.interestratetype;

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
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.service.applicationmaster.InterestRateTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.interestratetype.model.InterestRateTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/InterestRateType/InterestRateTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class InterestRateTypeListCtrl extends GFCBaseListCtrl<InterestRateType>	implements Serializable {

	private static final long serialVersionUID = 4676258087775088404L;
	private final static Logger logger = Logger.getLogger(InterestRateTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_InterestRateTypeList; 			// autoWired
	protected Borderlayout 	borderLayout_InterestRateTypeList; 		// autoWired
	protected Paging 		pagingInterestRateTypeList; 			// autoWired
	protected Listbox 		listBoxInterestRateType; 				// autoWired

	// List headers
	protected Listheader listheader_IntRateTypeCode; 				// autoWired
	protected Listheader listheader_IntRateTypeDesc; 				// autoWired
	protected Listheader listheader_IntRateTypeIsActive; 			// autoWired
	protected Listheader listheader_RecordStatus; 					// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 										  			// autoWired
	protected Button button_InterestRateTypeList_NewInterestRateType; 			// autoWired
	protected Button button_InterestRateTypeList_InterestRateTypeSearchDialog; 	// autoWired
	protected Button button_InterestRateTypeList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<InterestRateType> searchObj;

	private transient InterestRateTypeService interestRateTypeService;
	private transient WorkFlowDetails 		  workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public InterestRateTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected InterestRateType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InterestRateTypeList(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("InterestRateType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("InterestRateType");

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
		this.borderLayout_InterestRateTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingInterestRateTypeList.setPageSize(getListRows());
		this.pagingInterestRateTypeList.setDetailed(true);

		this.listheader_IntRateTypeCode.setSortAscending(new FieldComparator("intRateTypeCode", true));
		this.listheader_IntRateTypeCode.setSortDescending(new FieldComparator("intRateTypeCode", false));
		this.listheader_IntRateTypeDesc.setSortAscending(new FieldComparator("intRateTypeDesc", true));
		this.listheader_IntRateTypeDesc.setSortDescending(new FieldComparator("intRateTypeDesc", false));
		this.listheader_IntRateTypeIsActive.setSortAscending(new FieldComparator("intRateTypeIsActive",true));
		this.listheader_IntRateTypeIsActive.setSortDescending(new FieldComparator("intRateTypeIsActive",false));

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
		this.searchObj = new JdbcSearchObject<InterestRateType>(InterestRateType.class, getListRows());
		this.searchObj.addSort("IntRateTypeCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTInterestRateTypes_View");
			if (isFirstTask()) {
				button_InterestRateTypeList_NewInterestRateType.setVisible(true);
			} else {
				button_InterestRateTypeList_NewInterestRateType.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTInterestRateTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_InterestRateTypeList_NewInterestRateType.setVisible(false);
			this.button_InterestRateTypeList_InterestRateTypeSearchDialog.setVisible(false);
			this.button_InterestRateTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxInterestRateType,this.pagingInterestRateTypeList);
			// set the itemRenderer
			this.listBoxInterestRateType.setItemRenderer(new InterestRateTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("InterestRateTypeList");

		this.button_InterestRateTypeList_NewInterestRateType.setVisible(getUserWorkspace()
				.isAllowed("button_InterestRateTypeList_NewInterestRateType"));
		this.button_InterestRateTypeList_InterestRateTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_InterestRateTypeList_InterestRateTypeFindDialog"));
		this.button_InterestRateTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_InterestRateTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.interestratetype.model.
	 * InterestRateTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onInterestRateTypeItemDoubleClicked(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected InterestRateType object
		final Listitem item = this.listBoxInterestRateType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final InterestRateType aInterestRateType = (InterestRateType) item.getAttribute("data");
			final InterestRateType interestRateType = getInterestRateTypeService()
			.getInterestRateTypeById(aInterestRateType.getId());
			if (interestRateType == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aInterestRateType.getIntRateTypeCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_IntRateTypeCode")+ ":"+ aInterestRateType.getIntRateTypeCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND IntRateTypeCode='"+ interestRateType.getIntRateTypeCode()
				+ "' AND version=" + interestRateType.getVersion()+ " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"InterestRateType", whereCond,interestRateType.getTaskId(),interestRateType.getNextTaskId());
					if (userAcces) {
						showDetailView(interestRateType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(interestRateType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the InterestRateType dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_InterestRateTypeList_NewInterestRateType(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new InterestRateType object, We GET it from the back end.
		final InterestRateType aInterestRateType = getInterestRateTypeService().getNewInterestRateType();
		showDetailView(aInterestRateType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param InterestRateType
	 *            (aInterestRateType)
	 * @throws Exception
	 */
	private void showDetailView(InterestRateType aInterestRateType)throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aInterestRateType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aInterestRateType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("interestRateType", aInterestRateType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the InterestRateTypeListbox from
		 * the dialog when we do a delete, edit or insert a InterestRateType.
		 */
		map.put("interestRateTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/InterestRateType/InterestRateTypeDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_InterestRateTypeList);
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
		this.pagingInterestRateTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_InterestRateTypeList, event);
		this.window_InterestRateTypeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the InterestRateType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_InterestRateTypeList_InterestRateTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our InterestRateTypeDialog ZUL-file with parameters. So
		 * we can call them with a object of the selected InterestRateType. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * InterestRateType object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("interestRateTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/InterestRateType/InterestRateTypeSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the interestRateType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_InterestRateTypeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("InterestRateType", getSearchObj(),this.pagingInterestRateTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setInterestRateTypeService(InterestRateTypeService interestRateTypeService) {
		this.interestRateTypeService = interestRateTypeService;
	}
	public InterestRateTypeService getInterestRateTypeService() {
		return this.interestRateTypeService;
	}

	public JdbcSearchObject<InterestRateType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<InterestRateType> searchObj) {
		this.searchObj = searchObj;
	}
}