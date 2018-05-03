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
 * FileName    		:  EmploymentTypeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.employmenttype;

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

import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.service.systemmasters.EmploymentTypeService;
import com.pennant.webui.systemmasters.employmenttype.model.EmploymentTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/EmploymentType/EmploymentTypeList.zul file.
 */
public class EmploymentTypeListCtrl extends GFCBaseListCtrl<EmploymentType> {
	private static final long serialVersionUID = -7932825649812138524L;
	private static final Logger logger = Logger.getLogger(EmploymentTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EmploymentTypeList;
	protected Borderlayout borderLayout_EmploymentTypeList;
	protected Paging pagingEmploymentTypeList;
	protected Listbox listBoxEmploymentType;

	protected Textbox empType;
	protected Textbox empTypeDesc;

	protected Listbox sortOperator_empType;
	protected Listbox sortOperator_empTypeDesc;

	// List headers
	protected Listheader listheader_EmpType;
	protected Listheader listheader_EmpTypeDesc;

	// checkRights
	protected Button button_EmploymentTypeList_NewEmploymentType;
	protected Button button_EmploymentTypeList_EmploymentTypeSearchDialog;

	private transient EmploymentTypeService employmentTypeService;

	/**
	 * default constructor.<br>
	 */
	public EmploymentTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "EmploymentType";
		super.pageRightName = "EmploymentTypeList";
		super.tableName = "RMTEmpTypes_AView";
		super.queueTableName = "RMTEmpTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_EmploymentTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_EmploymentTypeList, borderLayout_EmploymentTypeList, listBoxEmploymentType,
				pagingEmploymentTypeList);
		setItemRender(new EmploymentTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_EmploymentTypeList_NewEmploymentType, "button_EmploymentTypeList_NewEmploymentType", true);
		registerButton(button_EmploymentTypeList_EmploymentTypeSearchDialog);

		registerField("empType", listheader_EmpType, SortOrder.ASC, empType, sortOperator_empType, Operators.STRING);
		registerField("empTypeDesc", listheader_EmpTypeDesc, SortOrder.NONE, empTypeDesc, sortOperator_empTypeDesc,
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
	public void onClick$button_EmploymentTypeList_EmploymentTypeSearchDialog(Event event) {
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
	public void onClick$button_EmploymentTypeList_NewEmploymentType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		EmploymentType employmentType = new EmploymentType();
		employmentType.setNewRecord(true);
		employmentType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(employmentType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onEmploymentTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxEmploymentType.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		EmploymentType employmentType = employmentTypeService.getEmploymentTypeById(id);

		if (employmentType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND EmpType='" + employmentType.getEmpType() + "' AND version="
				+ employmentType.getVersion() + " ";

		if (doCheckAuthority(employmentType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && employmentType.getWorkflowId() == 0) {
				employmentType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(employmentType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param employmentType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(EmploymentType employmentType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("employmentType", employmentType);
		arg.put("employmentTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/EmploymentType/EmploymentTypeDialog.zul", null,
					arg);
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

	public void setEmploymentTypeService(EmploymentTypeService employmentTypeService) {
		this.employmentTypeService = employmentTypeService;
	}
}