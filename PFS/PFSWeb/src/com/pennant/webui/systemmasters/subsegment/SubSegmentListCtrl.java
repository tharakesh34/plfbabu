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
 * FileName    		:  SubSegmentListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.subsegment;

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
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.systemmasters.SubSegmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.subsegment.model.SubSegmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/SubSegment/SubSegmentList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SubSegmentListCtrl extends GFCBaseListCtrl<SubSegment> implements Serializable {

	private static final long serialVersionUID = -4802249076746862609L;
	private final static Logger logger = Logger.getLogger(SubSegmentListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl'extends GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SubSegmentList; 			// autoWired
	protected Borderlayout 	borderLayout_SubSegmentList; 	// autoWired
	protected Paging 		pagingSubSegmentList; 			// autoWired
	protected Listbox 		listBoxSubSegment; 				// autoWired

	// List headers
	protected Listheader listheader_SegmentCode; 			// autoWired
	protected Listheader listheader_SubSegmentCode; 		// autoWired
	protected Listheader listheader_SubSegmentDesc; 		// autoWired
	protected Listheader listheader_SubSegmentIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType; 			// autoWired

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_SubSegmentList_NewSubSegment; 			// autoWired
	protected Button button_SubSegmentList_SubSegmentSearchDialog; 	// autoWired
	protected Button button_SubSegmentList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SubSegment> searchObj;

	private transient SubSegmentService subSegmentService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public SubSegmentListCtrl() {
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
	public void onCreate$window_SubSegmentList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SubSegment");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SubSegment");

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
		this.borderLayout_SubSegmentList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSubSegmentList.setPageSize(getListRows());
		this.pagingSubSegmentList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_SegmentCode.setSortAscending(new FieldComparator("segmentCode", true));
		this.listheader_SegmentCode.setSortDescending(new FieldComparator("segmentCode", false));
		this.listheader_SubSegmentCode.setSortAscending(new FieldComparator("subSegmentCode", true));
		this.listheader_SubSegmentCode.setSortDescending(new FieldComparator("subSegmentCode", false));
		this.listheader_SubSegmentDesc.setSortAscending(new FieldComparator("subSegmentDesc", true));
		this.listheader_SubSegmentDesc.setSortDescending(new FieldComparator("subSegmentDesc", false));
		this.listheader_SubSegmentIsActive.setSortAscending(new FieldComparator("subSegmentIsActive", true));
		this.listheader_SubSegmentIsActive.setSortDescending(new FieldComparator("subSegmentIsActive", false));

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
		this.searchObj = new JdbcSearchObject<SubSegment>(SubSegment.class, getListRows());
		this.searchObj.addSort("SegmentCode", false);
		this.searchObj.addFilter(new Filter("SegmentCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTSubSegments_View");
			if (isFirstTask()) {
				button_SubSegmentList_NewSubSegment.setVisible(true);
			} else {
				button_SubSegmentList_NewSubSegment.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTSubSegments_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_SubSegmentList_NewSubSegment.setVisible(false);
			this.button_SubSegmentList_SubSegmentSearchDialog.setVisible(false);
			this.button_SubSegmentList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxSubSegment, this.pagingSubSegmentList);
			// set the itemRenderer
			this.listBoxSubSegment.setItemRenderer(new SubSegmentListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SubSegmentList");

		this.button_SubSegmentList_NewSubSegment.setVisible(getUserWorkspace()
				.isAllowed("button_SubSegmentList_NewSubSegment"));
		this.button_SubSegmentList_SubSegmentSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SubSegmentList_SubSegmentFindDialog"));
		this.button_SubSegmentList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SubSegmentList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the ListBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.subsegment.model.
	 * SubSegmentListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSubSegmentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected SubSegment object
		final Listitem item = this.listBoxSubSegment.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SubSegment aSubSegment = (SubSegment) item.getAttribute("data");
			final SubSegment subSegment = getSubSegmentService().getSubSegmentById(aSubSegment.getId(), aSubSegment.getSubSegmentCode());

			if (subSegment == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aSubSegment.getSegmentCode();
				valueParm[1] = aSubSegment.getSubSegmentCode();

				errParm[0] = PennantJavaUtil.getLabel("label_SegmentCode") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_SubSegmentCode") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND SegmentCode='" + subSegment.getSegmentCode()
				+ "' AND SubSegmentCode='" + aSubSegment.getSubSegmentCode() 
				+ "' AND version=" + subSegment.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"SubSegment", whereCond, subSegment.getTaskId(), subSegment.getNextTaskId());
					if (userAcces) {
						showDetailView(subSegment);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(subSegment);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the SubSegment dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SubSegmentList_NewSubSegment(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new SubSegment object, We GET it from the backEnd.
		final SubSegment aSubSegment = getSubSegmentService().getNewSubSegment();
		showDetailView(aSubSegment);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param SubSegment
	 *            (aSubSegment)
	 * @throws Exception
	 */
	private void showDetailView(SubSegment aSubSegment) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aSubSegment.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aSubSegment.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("subSegment", aSubSegment);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the ListBox ListModel. This is
		 * fine for synchronizing the data in the SubSegmentListbox from the
		 * dialog when we do a delete, edit or insert a SubSegment.
		 */
		map.put("subSegmentListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/SubSegment/SubSegmentDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_SubSegmentList);
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
		this.pagingSubSegmentList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SubSegmentList, event);
		this.window_SubSegmentList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the SubSegment dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SubSegmentList_SubSegmentSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our SubSegmentDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected SubSegment. For handed over
		 * these parameter only a Map is accepted. So we put the SubSegment
		 * object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("subSegmentCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/SubSegment/SubSegmentSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the subSegment print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SubSegmentList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("SubSegment", getSearchObj(),this.pagingSubSegmentList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSubSegmentService(SubSegmentService subSegmentService) {
		this.subSegmentService = subSegmentService;
	}
	public SubSegmentService getSubSegmentService() {
		return this.subSegmentService;
	}

	public JdbcSearchObject<SubSegment> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SubSegment> searchObj) {
		this.searchObj = searchObj;
	}

}