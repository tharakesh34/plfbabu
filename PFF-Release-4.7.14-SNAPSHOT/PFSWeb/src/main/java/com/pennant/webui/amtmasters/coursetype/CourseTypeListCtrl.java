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
 * FileName    		:  CourseTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.amtmasters.coursetype;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.amtmasters.CourseType;
import com.pennant.backend.service.amtmasters.CourseTypeService;
import com.pennant.webui.amtmasters.coursetype.model.CourseTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMasters/CourseType/CourseTypeList.zul file.
 */
public class CourseTypeListCtrl extends GFCBaseListCtrl<CourseType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CourseTypeListCtrl.class);

	protected Window window_CourseTypeList;
	protected Borderlayout borderLayout_CourseTypeList;
	protected Paging pagingCourseTypeList;
	protected Listbox listBoxCourseType;

	protected Listheader listheader_CourseTypeCode;
	protected Listheader listheader_CourseTypeDesc;

	protected Button button_CourseTypeList_NewCourseType;
	protected Button button_CourseTypeList_CourseTypeSearchDialog;

	protected Textbox courseTypeCode;
	protected Textbox courseTypeDesc;

	protected Listbox sortOperator_courseTypeCode;
	protected Listbox sortOperator_courseTypeDesc;

	private transient CourseTypeService courseTypeService;

	/**
	 * default constructor.<br>
	 */
	public CourseTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CourseType";
		super.pageRightName = "CourseTypeList";
		super.tableName = "AMTCourseType_AView";
		super.queueTableName = "AMTCourseType_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CourseTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_CourseTypeList, borderLayout_CourseTypeList, listBoxCourseType, pagingCourseTypeList);
		setItemRender(new CourseTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CourseTypeList_NewCourseType, "button_CourseTypeList_NewCourseType", true);
		registerButton(button_CourseTypeList_CourseTypeSearchDialog);

		registerField("courseTypeCode", listheader_CourseTypeCode, SortOrder.ASC, courseTypeCode,
				sortOperator_courseTypeCode, Operators.STRING);
		registerField("courseTypeDesc", listheader_CourseTypeDesc, SortOrder.NONE, courseTypeDesc,
				sortOperator_courseTypeDesc, Operators.STRING);

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
	public void onClick$button_CourseTypeList_CourseTypeSearchDialog(Event event) {
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
	public void onClick$button_CourseTypeList_NewCourseType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CourseType courseType = new CourseType();
		courseType.setNewRecord(true);
		courseType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(courseType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCourseTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCourseType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CourseType courseType = courseTypeService.getCourseTypeById(id);

		if (courseType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CourseTypeCode='" + courseType.getCourseTypeCode() + "' AND version="
				+ courseType.getVersion() + " ";

		if (doCheckAuthority(courseType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && courseType.getWorkflowId() == 0) {
				courseType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(courseType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param courseType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CourseType courseType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("courseType", courseType);
		arg.put("courseTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/CourseType/CourseTypeDialog.zul", null, arg);
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

	public void setCourseTypeService(CourseTypeService courseTypeService) {
		this.courseTypeService = courseTypeService;
	}

}