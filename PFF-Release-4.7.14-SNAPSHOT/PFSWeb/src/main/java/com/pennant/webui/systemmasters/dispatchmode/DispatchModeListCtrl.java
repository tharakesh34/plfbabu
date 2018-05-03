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
 * FileName    		:  DispatchModeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-08-2011    														*
 *                                                                  						*
 * Modified Date    :  18-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.dispatchmode;

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

import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.service.systemmasters.DispatchModeService;
import com.pennant.webui.systemmasters.dispatchmode.model.DispatchModeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/DispatchMode/DispatchModeList.zul file.
 */
public class DispatchModeListCtrl extends GFCBaseListCtrl<DispatchMode> {
	private static final long serialVersionUID = 3085856113492519328L;
	private static final Logger logger = Logger.getLogger(DispatchModeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DispatchModeList;
	protected Borderlayout borderLayout_DispatchModeList;
	protected Paging pagingDispatchModeList;
	protected Listbox listBoxDispatchMode;

	protected Textbox dispatchModeCode;
	protected Textbox dispatchModeDesc;
	protected Checkbox dispatchModeIsActive;

	protected Listbox sortOperator_dispatchModeCode;
	protected Listbox sortOperator_dispatchModeDesc;
	protected Listbox sortOperator_dispatchModeIsActive;

	// List headers
	protected Listheader listheader_DispatchModeCode;
	protected Listheader listheader_DispatchModeDesc;
	protected Listheader listheader_DispatchModeIsActive;

	// checkRights
	protected Button button_DispatchModeList_NewDispatchMode;
	protected Button button_DispatchModeList_DispatchModeSearchDialog;

	private transient DispatchModeService dispatchModeService;

	/**
	 * default constructor.<br>
	 */
	public DispatchModeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DispatchMode";
		super.pageRightName = "DispatchModeList";
		super.tableName = "BMTDispatchModes_AView";
		super.queueTableName = "BMTDispatchModes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DispatchModeList(Event event) {
		// Set the page level components.
		setPageComponents(window_DispatchModeList, borderLayout_DispatchModeList, listBoxDispatchMode,
				pagingDispatchModeList);
		setItemRender(new DispatchModeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DispatchModeList_NewDispatchMode, "button_DispatchModeList_NewDispatchMode", true);
		registerButton(button_DispatchModeList_DispatchModeSearchDialog);

		registerField("dispatchModeCode", listheader_DispatchModeCode, SortOrder.ASC, dispatchModeCode,
				sortOperator_dispatchModeCode, Operators.STRING);
		registerField("dispatchModeDesc", listheader_DispatchModeDesc, SortOrder.NONE, dispatchModeDesc,
				sortOperator_dispatchModeDesc, Operators.STRING);
		registerField("dispatchModeIsActive", listheader_DispatchModeIsActive, SortOrder.NONE, dispatchModeIsActive,
				sortOperator_dispatchModeIsActive, Operators.BOOLEAN);

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
	public void onClick$button_DispatchModeList_DispatchModeSearchDialog(Event event) {
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
	public void onClick$button_DispatchModeList_NewDispatchMode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		DispatchMode dispatchMode = new DispatchMode();
		dispatchMode.setNewRecord(true);
		dispatchMode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(dispatchMode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDispatchModeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDispatchMode.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		DispatchMode dispatchMode = dispatchModeService.getDispatchModeById(id);

		if (dispatchMode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND DispatchModeCode='" + dispatchMode.getDispatchModeCode() + "' AND version="
				+ dispatchMode.getVersion() + " ";

		if (doCheckAuthority(dispatchMode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && dispatchMode.getWorkflowId() == 0) {
				dispatchMode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(dispatchMode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param dispatchMode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DispatchMode dispatchMode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("dispatchMode", dispatchMode);
		arg.put("dispatchModeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/DispatchMode/DispatchModeDialog.zul", null, arg);
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

	public void setDispatchModeService(DispatchModeService dispatchModeService) {
		this.dispatchModeService = dispatchModeService;
	}
}