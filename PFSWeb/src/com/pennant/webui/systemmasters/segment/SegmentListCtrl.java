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
 * FileName    		:  SegmentListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.segment;

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
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.service.systemmasters.SegmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.segment.model.SegmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SystemMasters/Segment/SegmentList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SegmentListCtrl extends GFCBaseListCtrl<Segment> implements Serializable {

	private static final long serialVersionUID = 1994302449627071841L;
	private final static Logger logger = Logger.getLogger(SegmentListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SegmentList; 			// autoWired
	protected Borderlayout  borderLayout_SegmentList; 		// autoWired
	protected Paging 		pagingSegmentList; 				// autoWired
	protected Listbox 		listBoxSegment; 				// autoWired

	// List headers
	protected Listheader listheader_SegmentCode; 			// autoWired
	protected Listheader listheader_SegmentDesc; 			// autoWired
	protected Listheader listheader_SegmentIsActive; 		// autoWired
	protected Listheader listheader_RecordStatus; 		 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 								 // autoWired
	protected Button button_SegmentList_NewSegment; 		 // autoWired
	protected Button button_SegmentList_SegmentSearchDialog; // autoWired
	protected Button button_SegmentList_PrintList; 			 // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Segment> searchObj;

	private transient SegmentService segmentService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public SegmentListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SegmentCode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SegmentList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Segment");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Segment");

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
		this.borderLayout_SegmentList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSegmentList.setPageSize(getListRows());
		this.pagingSegmentList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_SegmentCode.setSortAscending(new FieldComparator("segmentCode", true));
		this.listheader_SegmentCode.setSortDescending(new FieldComparator("segmentCode", false));
		this.listheader_SegmentDesc.setSortAscending(new FieldComparator("segmentDesc", true));
		this.listheader_SegmentDesc.setSortDescending(new FieldComparator("segmentDesc", false));
		this.listheader_SegmentIsActive.setSortAscending(new FieldComparator("segmentIsActive", true));
		this.listheader_SegmentIsActive.setSortDescending(new FieldComparator("segmentIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Segment>(Segment.class, getListRows());
		this.searchObj.addSort("SegmentCode", false);
		this.searchObj.addFilter(new Filter("SegmentCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTSegments_View");
			if (isFirstTask()) {
				button_SegmentList_NewSegment.setVisible(true);
			} else {
				button_SegmentList_NewSegment.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTSegments_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_SegmentList_NewSegment.setVisible(false);
			this.button_SegmentList_SegmentSearchDialog.setVisible(false);
			this.button_SegmentList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxSegment, this.pagingSegmentList);
			// set the itemRenderer
			this.listBoxSegment.setItemRenderer(new SegmentListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SegmentList");

		this.button_SegmentList_NewSegment.setVisible(getUserWorkspace()
				.isAllowed("button_SegmentList_NewSegment"));
		this.button_SegmentList_SegmentSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SegmentList_SegmentFindDialog"));
		this.button_SegmentList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SegmentList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see:
	 * com.pennant.webui.bmtmasters.segment.model.SegmentListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSegmentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Segment object
		final Listitem item = this.listBoxSegment.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Segment aSegment = (Segment) item.getAttribute("data");
			final Segment segment = getSegmentService().getSegmentById(aSegment.getId());

			if (segment == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aSegment.getSegmentCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_Segment_Code") + ":" + aSegment.getSegmentCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND SegmentCode='" + segment.getSegmentCode() 
				+ "' AND version=" + segment.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Segment", whereCond, segment.getTaskId(), segment.getNextTaskId());
					if (userAcces) {
						showDetailView(segment);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(segment);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Segment dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SegmentList_NewSegment(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Segment object, We GET it from the backEnd.
		final Segment aSegment = getSegmentService().getNewSegment();
		showDetailView(aSegment);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param Segment
	 *            (aSegment)
	 * @throws Exception
	 */
	private void showDetailView(Segment aSegment) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aSegment.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aSegment.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("segment", aSegment);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SegmentListbox from the dialog
		 * when we do a delete, edit or insert a Segment.
		 */
		map.put("segmentListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Segment/SegmentDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_SegmentList);
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
		this.pagingSegmentList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SegmentList, event);
		this.window_SegmentList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Segment dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SegmentList_SegmentSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our SegmentDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Segment. For handed over
		 * these parameter only a Map is accepted. So we put the Segment object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("segmentCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Segment/SegmentSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the segment print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SegmentList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Segment", getSearchObj(),this.pagingSegmentList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public void setSegmentService(SegmentService segmentService) {
		this.segmentService = segmentService;
	}
	public SegmentService getSegmentService() {
		return this.segmentService;
	}

	public JdbcSearchObject<Segment> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Segment> searchObj) {
		this.searchObj = searchObj;
	}

}