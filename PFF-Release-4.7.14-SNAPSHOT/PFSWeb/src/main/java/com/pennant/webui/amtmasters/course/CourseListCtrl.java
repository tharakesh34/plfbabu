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
 * FileName    		:  CourseListCtrl.java                                                   * 	  
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
package com.pennant.webui.amtmasters.course;

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

import com.pennant.backend.model.amtmasters.Course;
import com.pennant.backend.service.amtmasters.CourseService;
import com.pennant.webui.amtmasters.course.model.CourseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMasters/Course/CourseList.zul file.
 */
public class CourseListCtrl extends GFCBaseListCtrl<Course> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CourseListCtrl.class);

	protected Window window_CourseList;
	protected Borderlayout borderLayout_CourseList;
	protected Paging pagingCourseList;
	protected Listbox listBoxCourse;

	protected Listheader listheader_CourseName;
	protected Listheader listheader_CourseDesc;

	protected Button button_CourseList_NewCourse;
	protected Button button_CourseList_CourseSearchDialog;

	protected Textbox courseName;
	protected Textbox courseDesc;

	protected Listbox sortOperator_courseName;
	protected Listbox sortOperator_courseDesc;

	private transient CourseService courseService;

	/**
	 * default constructor.<br>
	 */
	public CourseListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Course";
		super.pageRightName = "CourseList";
		super.tableName = "AMTCourse_AView";
		super.queueTableName = "AMTCourse_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CourseList(Event event) {
		// Set the page level components.
		setPageComponents(window_CourseList, borderLayout_CourseList, listBoxCourse, pagingCourseList);
		setItemRender(new CourseListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CourseList_NewCourse, "button_CourseList_NewCourse", true);
		registerButton(button_CourseList_CourseSearchDialog);

		registerField("courseName", listheader_CourseName, SortOrder.ASC, courseName, sortOperator_courseName,
				Operators.STRING);
		registerField("courseDesc", listheader_CourseDesc, SortOrder.NONE, courseDesc, sortOperator_courseDesc,
				Operators.STRING);

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
	public void onClick$button_CourseList_CourseSearchDialog(Event event) {
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
	public void onClick$button_CourseList_NewCourse(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Course course = new Course();
		course.setNewRecord(true);
		course.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(course);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCourseItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCourse.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Course course = courseService.getCourseById(id);

		if (course == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CourseName='" + course.getCourseName() + "' AND version=" + course.getVersion() + " ";

		if (doCheckAuthority(course, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && course.getWorkflowId() == 0) {
				course.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(course);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param course
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Course course) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("course", course);
		arg.put("courseListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/Course/CourseDialog.zul", null, arg);
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

	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}

}