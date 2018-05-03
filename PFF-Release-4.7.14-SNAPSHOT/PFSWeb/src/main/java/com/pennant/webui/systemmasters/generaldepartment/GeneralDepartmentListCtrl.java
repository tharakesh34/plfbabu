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
 * FileName    		:  GeneralDepartmentListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.generaldepartment;

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

import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.service.systemmasters.GeneralDepartmentService;
import com.pennant.webui.systemmasters.generaldepartment.model.GeneralDepartmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/GeneralDepartment/GeneralDepartmentList.zul file.
 */
public class GeneralDepartmentListCtrl extends GFCBaseListCtrl<GeneralDepartment> {
	private static final long serialVersionUID = -8782007567428187225L;
	private static final Logger logger = Logger.getLogger(GeneralDepartmentListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_GeneralDepartmentList;
	protected Borderlayout borderLayout_GeneralDepartmentList;
	protected Paging pagingGeneralDepartmentList;
	protected Listbox listBoxGeneralDepartment;

	protected Textbox genDepartment;
	protected Textbox genDeptDesc;

	protected Listbox sortOperator_genDepartment;
	protected Listbox sortOperator_genDeptDesc;

	// List headers
	protected Listheader listheader_GenDepartment;
	protected Listheader listheader_GenDeptDesc;

	// checkRights
	protected Button button_GeneralDepartmentList_NewGeneralDepartment;
	protected Button button_GeneralDepartmentList_GeneralDepartmentSearchDialog;

	private transient GeneralDepartmentService generalDepartmentService;

	/**
	 * default constructor.<br>
	 */
	public GeneralDepartmentListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "GeneralDepartment";
		super.pageRightName = "GeneralDepartmentList";
		super.tableName = "RMTGenDepartments_AView";
		super.queueTableName = "RMTGenDepartments_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_GeneralDepartmentList(Event event) {
		// Set the page level components.
		setPageComponents(window_GeneralDepartmentList, borderLayout_GeneralDepartmentList, listBoxGeneralDepartment,
				pagingGeneralDepartmentList);
		setItemRender(new GeneralDepartmentListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_GeneralDepartmentList_NewGeneralDepartment,
				"button_GeneralDepartmentList_NewGeneralDepartment", true);
		registerButton(button_GeneralDepartmentList_GeneralDepartmentSearchDialog);

		registerField("genDepartment", listheader_GenDepartment, SortOrder.ASC, genDepartment,
				sortOperator_genDepartment, Operators.STRING);
		registerField("genDeptDesc", listheader_GenDeptDesc, SortOrder.NONE, genDeptDesc, sortOperator_genDeptDesc,
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
	public void onClick$button_GeneralDepartmentList_GeneralDepartmentSearchDialog(Event event) {
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
	public void onClick$button_GeneralDepartmentList_NewGeneralDepartment(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		GeneralDepartment generalDepartment = new GeneralDepartment();
		generalDepartment.setNewRecord(true);
		generalDepartment.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(generalDepartment);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onGeneralDepartmentItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxGeneralDepartment.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		GeneralDepartment generalDepartment = generalDepartmentService.getGeneralDepartmentById(id);

		if (generalDepartment == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND GenDepartment='" + generalDepartment.getGenDepartment() + "' AND version="
				+ generalDepartment.getVersion() + " ";

		if (doCheckAuthority(generalDepartment, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && generalDepartment.getWorkflowId() == 0) {
				generalDepartment.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(generalDepartment);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param generalDepartment
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(GeneralDepartment generalDepartment) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("generalDepartment", generalDepartment);
		arg.put("generalDepartmentListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/GeneralDepartment/GeneralDepartmentDialog.zul",
					null, arg);
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

	public void setGeneralDepartmentService(GeneralDepartmentService generalDepartmentService) {
		this.generalDepartmentService = generalDepartmentService;
	}
}