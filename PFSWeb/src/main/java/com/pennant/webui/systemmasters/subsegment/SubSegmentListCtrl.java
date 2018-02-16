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

import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.systemmasters.SubSegmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.subsegment.model.SubSegmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/SubSegment/SubSegmentList.zul file.
 */
public class SubSegmentListCtrl extends GFCBaseListCtrl<SubSegment> {
	private static final long serialVersionUID = -4802249076746862609L;
	private static final Logger logger = Logger.getLogger(SubSegmentListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl'extends GenericForwardComposer.
	 */
	protected Window window_SubSegmentList;
	protected Borderlayout borderLayout_SubSegmentList;
	protected Paging pagingSubSegmentList;
	protected Listbox listBoxSubSegment;

	// List headers
	protected Listheader listheader_SegmentCode;
	protected Listheader listheader_SubSegmentCode;
	protected Listheader listheader_SubSegmentDesc;
	protected Listheader listheader_SubSegmentIsActive;

	protected Textbox segmentCode;
	protected Textbox subSegmentCode;
	protected Textbox subSegmentDesc;
	protected Checkbox subSegmentIsActive;

	protected Listbox sortOperator_segmentCode;
	protected Listbox sortOperator_subSegmentCode;
	protected Listbox sortOperator_subSegmentDesc;
	protected Listbox sortOperator_subSegmentIsActive;

	// checkRights
	protected Button button_SubSegmentList_NewSubSegment;
	protected Button button_SubSegmentList_SubSegmentSearchDialog;

	private transient SubSegmentService subSegmentService;

	/**
	 * default constructor.<br>
	 */
	public SubSegmentListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SubSegment";
		super.pageRightName = "SubSegmentList";
		super.tableName = "BMTSubSegments_AView";
		super.queueTableName = "BMTSubSegments_View";
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
	public void onCreate$window_SubSegmentList(Event event) {
		// Set the page level components.
		setPageComponents(window_SubSegmentList, borderLayout_SubSegmentList, listBoxSubSegment, pagingSubSegmentList);
		setItemRender(new SubSegmentListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SubSegmentList_NewSubSegment, "button_SubSegmentList_NewSubSegment", true);
		registerButton(button_SubSegmentList_SubSegmentSearchDialog);

		registerField("segmentCode", listheader_SegmentCode, SortOrder.ASC, segmentCode, sortOperator_segmentCode,
				Operators.STRING);
		registerField("subSegmentCode", listheader_SubSegmentCode, SortOrder.NONE, subSegmentCode,
				sortOperator_subSegmentCode, Operators.STRING);
		registerField("subSegmentDesc", listheader_SubSegmentDesc, SortOrder.NONE, subSegmentDesc,
				sortOperator_subSegmentDesc, Operators.STRING);
		registerField("subSegmentIsActive", listheader_SubSegmentIsActive, SortOrder.NONE, subSegmentIsActive,
				sortOperator_subSegmentIsActive, Operators.BOOLEAN);

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
	public void onClick$button_SubSegmentList_SubSegmentSearchDialog(Event event) {
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
	public void onClick$button_SubSegmentList_NewSubSegment(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SubSegment subSegment = new SubSegment();
		subSegment.setNewRecord(true);
		subSegment.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(subSegment);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSubSegmentItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSubSegment.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String subSegmentCode = (String) selectedItem.getAttribute("subSegmentCode");
		SubSegment subSegment = subSegmentService.getSubSegmentById(id, subSegmentCode);

		if (subSegment == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SegmentCode='" + subSegment.getSegmentCode() + "' AND SubSegmentCode='"
				+ subSegment.getSubSegmentCode() + "' AND version=" + subSegment.getVersion() + " ";

		if (doCheckAuthority(subSegment, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && subSegment.getWorkflowId() == 0) {
				subSegment.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(subSegment);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param subSegment
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SubSegment subSegment) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("subSegment", subSegment);
		arg.put("subSegmentListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/SubSegment/SubSegmentDialog.zul", null, arg);
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
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setSubSegmentService(SubSegmentService subSegmentService) {
		this.subSegmentService = subSegmentService;
	}
}