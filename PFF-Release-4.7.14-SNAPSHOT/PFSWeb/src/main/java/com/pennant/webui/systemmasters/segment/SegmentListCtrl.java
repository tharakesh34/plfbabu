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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.service.systemmasters.SegmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.segment.model.SegmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/Segment/SegmentList.zul file.
 */
public class SegmentListCtrl extends GFCBaseListCtrl<Segment> {
	private static final long serialVersionUID = 1994302449627071841L;
	private static final Logger logger = Logger.getLogger(SegmentListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SegmentList;
	protected Borderlayout borderLayout_SegmentList;
	protected Paging pagingSegmentList;
	protected Listbox listBoxSegment;

	protected Textbox segmentCode;
	protected Textbox segmentDesc;
	protected Checkbox segmentIsActive;

	protected Listbox sortOperator_segmentDesc;
	protected Listbox sortOperator_segmentCode;
	protected Listbox sortOperator_segmentIsActive;

	// List headers
	protected Listheader listheader_SegmentCode;
	protected Listheader listheader_SegmentDesc;
	protected Listheader listheader_SegmentIsActive;

	// checkRights
	protected Button button_SegmentList_NewSegment;
	protected Button button_SegmentList_SegmentSearchDialog;

	private transient SegmentService segmentService;

	/**
	 * default constructor.<br>
	 */
	public SegmentListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Segment";
		super.pageRightName = "SegmentList";
		super.tableName = "BMTSegments_AView";
		super.queueTableName = "BMTSegments_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("segmentCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SegmentList(Event event) {
		// Set the page level components.
		setPageComponents(window_SegmentList, borderLayout_SegmentList, listBoxSegment, pagingSegmentList);
		setItemRender(new SegmentListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SegmentList_NewSegment, "button_SegmentList_NewSegment", true);
		registerButton(button_SegmentList_SegmentSearchDialog);

		registerField("segmentCode", listheader_SegmentCode, SortOrder.ASC, segmentCode, sortOperator_segmentCode,
				Operators.STRING);
		registerField("segmentDesc", listheader_SegmentDesc, SortOrder.NONE, segmentDesc, sortOperator_segmentDesc,
				Operators.STRING);
		registerField("segmentIsActive", listheader_SegmentIsActive, SortOrder.NONE, segmentIsActive,
				sortOperator_segmentIsActive, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_SegmentList_SegmentSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_SegmentList_NewSegment(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Segment segment = new Segment();
		segment.setNewRecord(true);
		segment.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(segment);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSegmentItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSegment.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Segment segment = segmentService.getSegmentById(id);

		if (segment == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SegmentCode='" + segment.getSegmentCode() + "' AND version=" + segment.getVersion()
				+ " ";

		if (doCheckAuthority(segment, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && segment.getWorkflowId() == 0) {
				segment.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(segment);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param segment
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Segment segment) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("segment", segment);
		arg.put("segmentListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Segment/SegmentDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) throws InterruptedException {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) throws InterruptedException {
		doShowHelp(event);
	}

	public void setSegmentService(SegmentService segmentService) {
		this.segmentService = segmentService;
	}
}