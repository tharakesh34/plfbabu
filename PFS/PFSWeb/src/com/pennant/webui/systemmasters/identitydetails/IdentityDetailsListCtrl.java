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
 * FileName    		:  IdentityDetailsListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.identitydetails;

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
import com.pennant.backend.model.systemmasters.IdentityDetails;
import com.pennant.backend.service.systemmasters.IdentityDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.identitydetails.model.IdentityDetailsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/IdentityDetails/IdentityDetailsList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class IdentityDetailsListCtrl extends GFCBaseListCtrl<IdentityDetails> implements Serializable {

	private static final long serialVersionUID = 7858815454023737745L;
	private final static Logger logger = Logger.getLogger(IdentityDetailsListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_IdentityDetailsList; 		// autoWired
	protected Borderlayout 	borderLayout_IdentityDetailsList; 	// autoWired
	protected Paging 		pagingIdentityDetailsList; 			// autoWired
	protected Listbox 		listBoxIdentityDetails; 			// autoWired

	// List headers
	protected Listheader listheader_IdentityType;				// autoWired
	protected Listheader listheader_IdentityDesc;				// autoWired
	protected Listheader listheader_RecordStatus;				// autoWired
	protected Listheader listheader_RecordType;	 				// autoWired

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_IdentityDetailsList_NewIdentityDetails; 			// autoWired
	protected Button button_IdentityDetailsList_IdentityDetailsSearchDialog;	// autoWired
	protected Button button_IdentityDetailsList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<IdentityDetails> searchObj;

	private transient IdentityDetailsService identityDetailsService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public IdentityDetailsListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected IdentityDetails object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCreate$window_IdentityDetailsList(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("IdentityDetails");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("IdentityDetails");

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
		this.borderLayout_IdentityDetailsList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingIdentityDetailsList.setPageSize(getListRows());
		this.pagingIdentityDetailsList.setDetailed(true);

		this.listheader_IdentityType.setSortAscending(new FieldComparator("identityType", true));
		this.listheader_IdentityType.setSortDescending(new FieldComparator("identityType", false));
		this.listheader_IdentityDesc.setSortAscending(new FieldComparator("identityDesc", true));
		this.listheader_IdentityDesc.setSortDescending(new FieldComparator("identityDesc", false));

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
		this.searchObj = new JdbcSearchObject<IdentityDetails>(IdentityDetails.class, getListRows());
		this.searchObj.addSort("IdentityType", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTIdentityType_View");
			if (isFirstTask()) {
				button_IdentityDetailsList_NewIdentityDetails.setVisible(true);
			} else {
				button_IdentityDetailsList_NewIdentityDetails.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTIdentityType_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_IdentityDetailsList_NewIdentityDetails.setVisible(false);
			this.button_IdentityDetailsList_IdentityDetailsSearchDialog.setVisible(false);
			this.button_IdentityDetailsList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxIdentityDetails,this.pagingIdentityDetailsList);
			// set the itemRenderer
			this.listBoxIdentityDetails.setItemRenderer(new IdentityDetailsListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("IdentityDetailsList");

		this.button_IdentityDetailsList_NewIdentityDetails.setVisible(getUserWorkspace()
				.isAllowed("button_IdentityDetailsList_NewIdentityDetails"));
		this.button_IdentityDetailsList_IdentityDetailsSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_IdentityDetailsList_IdentityDetailsFindDialog"));
		this.button_IdentityDetailsList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_IdentityDetailsList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.identitydetails.model.
	 * IdentityDetailsListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onIdentityDetailsItemDoubleClicked(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected IdentityDetails object
		final Listitem item = this.listBoxIdentityDetails.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final IdentityDetails aIdentityDetails = (IdentityDetails) item.getAttribute("data");
			final IdentityDetails identityDetails = getIdentityDetailsService().getIdentityDetailsById(aIdentityDetails.getId());

			if (identityDetails == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aIdentityDetails.getIdentityType();
				errorParm[0] = PennantJavaUtil.getLabel("label_IdentityType")+ ":" + aIdentityDetails.getIdentityType();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND IdentityType='"+ identityDetails.getIdentityType() 
				+ "' AND version="+ identityDetails.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"IdentityDetails", whereCond,identityDetails.getTaskId(),identityDetails.getNextTaskId());
					if (userAcces) {
						showDetailView(identityDetails);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(identityDetails);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the IdentityDetails dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_IdentityDetailsList_NewIdentityDetails(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new IdentityDetails object, We GET it from the back end.
		final IdentityDetails aIdentityDetails = getIdentityDetailsService().getNewIdentityDetails();
		showDetailView(aIdentityDetails);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param IdentityDetails
	 *            (aIdentityDetails)
	 * @throws Exception
	 */
	private void showDetailView(IdentityDetails aIdentityDetails)throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aIdentityDetails.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aIdentityDetails.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("identityDetails", aIdentityDetails);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the IdentityDetailsListbox from
		 * the dialog when we do a delete, edit or insert a IdentityDetails.
		 */
		map.put("identityDetailsListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/IdentityDetails/IdentityDetailsDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_IdentityDetailsList);
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
		this.pagingIdentityDetailsList.setActivePage(0);
		Events.postEvent("onCreate", this.window_IdentityDetailsList, event);
		this.window_IdentityDetailsList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the IdentityDetails dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_IdentityDetailsList_IdentityDetailsSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our IdentityDetailsDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected IdentityDetails. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * IdentityDetails object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("identityDetailsCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/IdentityDetails/IdentityDetailsSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the identityDetails print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_IdentityDetailsList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("IdentityDetails", getSearchObj(),this.pagingIdentityDetailsList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setIdentityDetailsService(IdentityDetailsService identityDetailsService) {
		this.identityDetailsService = identityDetailsService;
	}
	public IdentityDetailsService getIdentityDetailsService() {
		return this.identityDetailsService;
	}

	public JdbcSearchObject<IdentityDetails> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<IdentityDetails> searchObj) {
		this.searchObj = searchObj;
	}
}