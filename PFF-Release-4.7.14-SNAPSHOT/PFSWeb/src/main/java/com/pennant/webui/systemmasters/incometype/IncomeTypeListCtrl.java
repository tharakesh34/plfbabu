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
 * FileName    		:  IncomeTypeListCtrl.java                                              * 	  
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
package com.pennant.webui.systemmasters.incometype;

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

import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.systemmasters.IncomeTypeService;
import com.pennant.webui.systemmasters.incometype.model.IncomeTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/BMTMasters/IncomeType/IncomeTypeList.zul file.
 */
public class IncomeTypeListCtrl extends GFCBaseListCtrl<IncomeType> {
	private static final long serialVersionUID = -3522599343656178315L;
	private static final Logger logger = Logger.getLogger(IncomeTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_IncomeTypeList;
	protected Borderlayout borderLayout_IncomeTypeList;
	protected Paging pagingIncomeTypeList;
	protected Listbox listBoxIncomeType;

	protected Textbox incomeTypeCode;
	protected Textbox incomeTypeDesc;
	protected Checkbox incomeTypeIsActive;

	protected Listbox sortOperator_incomeTypeDesc;
	protected Listbox sortOperator_incomeTypeCode;
	protected Listbox sortOperator_incomeTypeIsActive;

	// List headers
	protected Listheader listheader_IncomeExpense;
	protected Listheader listheader_IncomeTypeCategory;
	protected Listheader listheader_IncomeTypeCode;
	protected Listheader listheader_IncomeTypeDesc;
	protected Listheader listheader_IncomeTypeIsActive;

	// checkRights
	protected Button button_IncomeTypeList_NewIncomeType;
	protected Button button_IncomeTypeList_IncomeTypeSearchDialog;

	private transient IncomeTypeService incomeTypeService;

	/**
	 * default constructor.<br>
	 */
	public IncomeTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "IncomeType";
		super.pageRightName = "IncomeTypeList";
		super.tableName = "BMTIncomeTypes_AView";
		super.queueTableName = "BMTIncomeTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_IncomeTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_IncomeTypeList, borderLayout_IncomeTypeList, listBoxIncomeType, pagingIncomeTypeList);
		setItemRender(new IncomeTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_IncomeTypeList_NewIncomeType, "button_IncomeTypeList_NewIncomeType", true);
		registerButton(button_IncomeTypeList_IncomeTypeSearchDialog);

		registerField("IncomeExpense", listheader_IncomeExpense, SortOrder.ASC);
		registerField("category", listheader_IncomeTypeCategory, SortOrder.NONE);
		registerField("incomeTypeCode", listheader_IncomeTypeCode, SortOrder.NONE, incomeTypeCode,
				sortOperator_incomeTypeCode, Operators.STRING);
		registerField("incomeTypeDesc", listheader_IncomeTypeDesc, SortOrder.NONE, incomeTypeDesc,
				sortOperator_incomeTypeDesc, Operators.STRING);
		registerField("incomeTypeIsActive", listheader_IncomeTypeIsActive, SortOrder.NONE, incomeTypeIsActive,
				sortOperator_incomeTypeIsActive, Operators.BOOLEAN);

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
	public void onClick$button_IncomeTypeList_IncomeTypeSearchDialog(Event event) {
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
	public void onClick$button_IncomeTypeList_NewIncomeType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		IncomeType incomeType = new IncomeType();
		incomeType.setNewRecord(true);
		incomeType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(incomeType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onIncomeTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxIncomeType.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		String incomeExpense = ((String) selectedItem.getAttribute("incomeExpense"));
		String category = ((String) selectedItem.getAttribute("category"));
		IncomeType incomeType = incomeTypeService.getIncomeTypeById(id, incomeExpense, category);

		if (incomeType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND IncomeTypeCode='" + incomeType.getIncomeTypeCode() + "' AND version="
				+ incomeType.getVersion() + " ";

		if (doCheckAuthority(incomeType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && incomeType.getWorkflowId() == 0) {
				incomeType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(incomeType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param incomeType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(IncomeType incomeType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("incomeType", incomeType);
		arg.put("incomeTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/IncomeType/IncomeTypeDialog.zul", null, arg);
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

	public void setIncomeTypeService(IncomeTypeService incomeTypeService) {
		this.incomeTypeService = incomeTypeService;
	}
}