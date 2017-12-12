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
 * FileName    		:  DesignationListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.designation;

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

import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.service.systemmasters.DesignationService;
import com.pennant.webui.systemmasters.designation.model.DesignationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Designation/DesignationList.zul file.
 */
public class DesignationListCtrl extends GFCBaseListCtrl<Designation> {
	private static final long serialVersionUID = -4747695952669967067L;
	private static final Logger logger = Logger.getLogger(DesignationListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DesignationList;
	protected Borderlayout borderLayout_DesignationList;
	protected Paging pagingDesignationList;
	protected Listbox listBoxDesignation;

	// List headers
	protected Listheader listheader_DesgCode;
	protected Listheader listheader_DesgDesc;
	protected Listheader listheader_DesgIsActive;

	protected Textbox desgCode;
	protected Textbox desgDesc;
	protected Checkbox desgIsActive;

	protected Listbox sortOperator_desgDesc;
	protected Listbox sortOperator_desgCode;
	protected Listbox sortOperator_desgIsActive;

	// checkRights
	protected Button button_DesignationList_NewDesignation;
	protected Button button_DesignationList_DesignationSearchDialog;

	private transient DesignationService designationService;

	/**
	 * default constructor.<br>
	 */
	public DesignationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Designation";
		super.pageRightName = "DesignationList";
		super.tableName = "BMTDesignations_AView";
		super.queueTableName = "BMTDesignations_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DesignationList(Event event) {
		// Set the page level components.
		setPageComponents(window_DesignationList, borderLayout_DesignationList, listBoxDesignation,
				pagingDesignationList);
		setItemRender(new DesignationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DesignationList_NewDesignation, "button_DesignationList_NewDesignation", true);
		registerButton(button_DesignationList_DesignationSearchDialog);

		registerField("desgCode", listheader_DesgCode, SortOrder.ASC, desgCode, sortOperator_desgCode, Operators.STRING);
		registerField("desgDesc", listheader_DesgDesc, SortOrder.NONE, desgDesc, sortOperator_desgDesc,
				Operators.STRING);
		registerField("desgIsActive", listheader_DesgIsActive, SortOrder.NONE, desgIsActive, sortOperator_desgIsActive,
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
	public void onClick$button_DesignationList_DesignationSearchDialog(Event event) {
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
	public void onClick$button_DesignationList_NewDesignation(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Designation designation = new Designation();
		designation.setNewRecord(true);
		designation.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(designation);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDesignationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDesignation.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Designation designation = designationService.getDesignationById(id);

		if (designation == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND DesgCode='" + designation.getDesgCode() + "' AND version=" + designation.getVersion()
				+ " ";

		if (doCheckAuthority(designation, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && designation.getWorkflowId() == 0) {
				designation.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(designation);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param designation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Designation designation) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("designation", designation);
		arg.put("designationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Designation/DesignationDialog.zul", null, arg);
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

	public void setDesignationService(DesignationService designationService) {
		this.designationService = designationService;
	}
}