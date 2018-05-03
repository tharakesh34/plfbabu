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
 * FileName    		:  LoanPurposeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.loanpurpose;

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

import com.pennant.backend.model.systemmasters.LoanPurpose;
import com.pennant.backend.service.systemmasters.LoanPurposeService;
import com.pennant.webui.systemmasters.loanpurpose.model.LoanPurposeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/LoanPurpose/LoanPurposeList.zul file.
 */
public class LoanPurposeListCtrl extends GFCBaseListCtrl<LoanPurpose> {
	private static final long				serialVersionUID	= 1817958653208633892L;
	private static final Logger				logger				= Logger.getLogger(LoanPurposeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_LoanPurposeList;
	protected Borderlayout					borderLayout_LoanPurposeList;
	protected Paging						pagingLoanPurposeList;
	protected Listbox						listBoxLoanPurpose;

	protected Textbox						loanPurposeCode;
	protected Textbox						loanPurposeDesc;
	protected Checkbox						loanPurposeIsActive;

	protected Listbox						sortOperator_loanPurposeCode;
	protected Listbox						sortOperator_loanPurposeDesc;
	protected Listbox						sortOperator_loanPurposeIsActive;

	// List headers
	protected Listheader					listheader_LoanPurposeCode;
	protected Listheader					listheader_LoanPurposeDesc;
	protected Listheader					listheader_LoanPurposeIsActive;

	// checkRights
	protected Button						button_LoanPurposeList_NewLoanPurpose;
	protected Button						button_LoanPurposeList_LoanPurposeSearchDialog;

	private transient LoanPurposeService	loanPurposeService;

	/**
	 * default constructor.<br>
	 */
	public LoanPurposeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LoanPurpose";
		super.pageRightName = "LoanPurposeList";
		super.tableName = "LoanPurposes_AView";
		super.queueTableName = "LoanPurposes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LoanPurposeList(Event event) {
		// Set the page level components.
		setPageComponents(window_LoanPurposeList, borderLayout_LoanPurposeList, listBoxLoanPurpose,
				pagingLoanPurposeList);
		setItemRender(new LoanPurposeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LoanPurposeList_NewLoanPurpose, "button_LoanPurposeList_NewLoanPurpose", true);
		registerButton(button_LoanPurposeList_LoanPurposeSearchDialog);

		registerField("loanPurposeCode", listheader_LoanPurposeCode, SortOrder.ASC, loanPurposeCode, sortOperator_loanPurposeCode,
				Operators.STRING);
		registerField("loanPurposeDesc", listheader_LoanPurposeDesc, SortOrder.NONE, loanPurposeDesc, sortOperator_loanPurposeDesc,
				Operators.STRING);
		registerField("loanPurposeIsActive", listheader_LoanPurposeIsActive, SortOrder.NONE, loanPurposeIsActive,
				sortOperator_loanPurposeIsActive, Operators.BOOLEAN);
		

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
	public void onClick$button_LoanPurposeList_LoanPurposeSearchDialog(Event event) {
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
	public void onClick$button_LoanPurposeList_NewLoanPurpose(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		LoanPurpose LoanPurpose = new LoanPurpose();
		LoanPurpose.setNewRecord(true);
		LoanPurpose.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(LoanPurpose);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onLoanPurposeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLoanPurpose.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		LoanPurpose LoanPurpose = loanPurposeService.getLoanPurposeById(id);

		if (LoanPurpose == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND loanPurposeCode='" + LoanPurpose.getLoanPurposeCode() + "' AND version="
				+ LoanPurpose.getVersion() + " ";

		if (doCheckAuthority(LoanPurpose, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && LoanPurpose.getWorkflowId() == 0) {
				LoanPurpose.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(LoanPurpose);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param LoanPurpose
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LoanPurpose LoanPurpose) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("LoanPurpose", LoanPurpose);
		arg.put("LoanPurposeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/LoanPurpose/LoanPurposeDialog.zul", null, arg);
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

	public void setLoanPurposeService(LoanPurposeService LoanPurposeService) {
		this.loanPurposeService = LoanPurposeService;
	}
}