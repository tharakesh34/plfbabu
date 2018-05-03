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
 * FileName    		:  EmpStsCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.empstscode;

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

import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.service.systemmasters.EmpStsCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.empstscode.model.EmpStsCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/EmpStsCode/EmpStsCodeList.zul file.
 */
public class EmpStsCodeListCtrl extends GFCBaseListCtrl<EmpStsCode> {
	private static final long serialVersionUID = 183040264046520252L;
	private static final Logger logger = Logger.getLogger(EmpStsCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EmpStsCodeList;
	protected Borderlayout borderLayout_EmpStsCodeList;
	protected Paging pagingEmpStsCodeList;
	protected Listbox listBoxEmpStsCode;

	protected Textbox empStsCode;
	protected Textbox empStsDesc;
	protected Checkbox empStsIsActive;

	protected Listbox sortOperator_empStsCode;
	protected Listbox sortOperator_empStsDesc;
	protected Listbox sortOperator_empStsIsActive;

	// List headers
	protected Listheader listheader_EmpStsCode;
	protected Listheader listheader_EmpStsDesc;
	protected Listheader listheader_EmpStsIsActive;

	// checkRights
	protected Button button_EmpStsCodeList_NewEmpStsCode;
	protected Button button_EmpStsCodeList_EmpStsCodeSearchDialog;

	private transient EmpStsCodeService empStsCodeService;

	/**
	 * default constructor.<br>
	 */
	public EmpStsCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "EmpStsCode";
		super.pageRightName = "EmpStsCodeList";
		super.tableName = "BMTEmpStsCodes_AView";
		super.queueTableName = "BMTEmpStsCodes_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("EmpStsCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_EmpStsCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_EmpStsCodeList, borderLayout_EmpStsCodeList, listBoxEmpStsCode, pagingEmpStsCodeList);
		setItemRender(new EmpStsCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_EmpStsCodeList_NewEmpStsCode, "button_EmpStsCodeList_NewEmpStsCode", true);
		registerButton(button_EmpStsCodeList_EmpStsCodeSearchDialog);

		registerField("empStsCode", listheader_EmpStsCode, SortOrder.ASC, empStsCode, sortOperator_empStsCode,
				Operators.STRING);
		registerField("empStsDesc", listheader_EmpStsDesc, SortOrder.NONE, empStsDesc, sortOperator_empStsDesc,
				Operators.STRING);
		registerField("empStsIsActive", listheader_EmpStsIsActive, SortOrder.NONE, empStsIsActive,
				sortOperator_empStsIsActive, Operators.BOOLEAN);

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
	public void onClick$button_EmpStsCodeList_EmpStsCodeSearchDialog(Event event) {
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
	public void onClick$button_EmpStsCodeList_NewEmpStsCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		EmpStsCode empStsCode = new EmpStsCode();
		empStsCode.setNewRecord(true);
		empStsCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(empStsCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onEmpStsCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxEmpStsCode.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		EmpStsCode empStsCode = empStsCodeService.getEmpStsCodeById(id);

		if (empStsCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND EmpStsCode='" + empStsCode.getEmpStsCode() + "' AND version="
				+ empStsCode.getVersion() + " ";

		if (doCheckAuthority(empStsCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && empStsCode.getWorkflowId() == 0) {
				empStsCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(empStsCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param empStsCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(EmpStsCode empStsCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("empStsCode", empStsCode);
		arg.put("empStsCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/EmpStsCode/EmpStsCodeDialog.zul", null, arg);
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

	public void setEmpStsCodeService(EmpStsCodeService empStsCodeService) {
		this.empStsCodeService = empStsCodeService;
	}
}