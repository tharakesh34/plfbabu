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
 * FileName    		:  DepartmentListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.department;

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

import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.service.systemmasters.DepartmentService;
import com.pennant.webui.systemmasters.department.model.DepartmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Department/DepartmentList.zul file.
 */
public class DepartmentListCtrl extends GFCBaseListCtrl<Department> {
	private static final long serialVersionUID = -3848678436698919359L;
	private static final Logger logger = Logger.getLogger(DepartmentListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DepartmentList;
	protected Borderlayout borderLayout_DepartmentList;
	protected Paging pagingDepartmentList;
	protected Listbox listBoxDepartment;

	protected Textbox deptCode;
	protected Textbox deptDesc;
	protected Checkbox deptIsActive;

	protected Listbox sortOperator_deptCode;
	protected Listbox sortOperator_deptDesc;
	protected Listbox sortOperator_deptIsActive;

	// List headers
	protected Listheader listheader_DeptCode;
	protected Listheader listheader_DeptDesc;
	protected Listheader listheader_DeptIsActive;

	// checkRights
	protected Button button_DepartmentList_NewDepartment;
	protected Button button_DepartmentList_DepartmentSearchDialog;

	private transient DepartmentService departmentService;

	/**
	 * default constructor.<br>
	 */
	public DepartmentListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Department";
		super.pageRightName = "DepartmentList";
		super.tableName = "BMTDepartments_AView";
		super.queueTableName = "BMTDepartments_View";

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DepartmentList(Event event) {
		// Set the page level components.
		setPageComponents(window_DepartmentList, borderLayout_DepartmentList, listBoxDepartment, pagingDepartmentList);
		setItemRender(new DepartmentListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DepartmentList_NewDepartment, "button_DepartmentList_NewDepartment", true);
		registerButton(button_DepartmentList_DepartmentSearchDialog);

		registerField("deptCode", listheader_DeptCode, SortOrder.ASC, deptCode, sortOperator_deptCode, Operators.STRING);
		registerField("deptDesc", listheader_DeptDesc, SortOrder.NONE, deptDesc, sortOperator_deptDesc,
				Operators.STRING);
		registerField("deptIsActive", listheader_DeptIsActive, SortOrder.NONE, deptIsActive, sortOperator_deptIsActive,
				Operators.BOOLEAN);

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
	public void onClick$button_DepartmentList_DepartmentSearchDialog(Event event) {
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
	public void onClick$button_DepartmentList_NewDepartment(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Department department = new Department();
		department.setNewRecord(true);
		department.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(department);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDepartmentItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDepartment.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Department department = departmentService.getDepartmentById(id);

		if (department == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND DeptCode='" + department.getDeptCode() + "' AND version=" + department.getVersion()
				+ " ";

		if (doCheckAuthority(department, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && department.getWorkflowId() == 0) {
				department.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(department);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param department
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Department department) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("department", department);
		arg.put("departmentListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Department/DepartmentDialog.zul", null, arg);
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
	public void onClick$help(Event event) throws InterruptedException {
		doShowHelp(event);
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
}