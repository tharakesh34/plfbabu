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
 * FileName    		:  BlackListReasonCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.blacklistreasoncode;

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

import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.service.systemmasters.impl.BlackListReasonCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.blacklistreasoncode.model.BlackListReasonCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/BlackListReasonCode/BlackListReasonCodeList.zul
 * file.
 */
public class BlackListReasonCodeListCtrl extends GFCBaseListCtrl<BlackListReasonCode> {
	private static final long serialVersionUID = -4787094221203301336L;
	private static final Logger logger = Logger.getLogger(BlackListReasonCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BlackListReasonCodeList;
	protected Borderlayout borderLayout_BlackListReasonCodeList;
	protected Paging pagingBlackListReasonCodeList;
	protected Listbox listBoxBlackListReasonCode;

	// List headers
	protected Listheader listheader_BLRsnCode;
	protected Listheader listheader_BLRsnDesc;
	protected Listheader listheader_BLIsActive;

	// checkRights
	protected Button button_BlackListReasonCodeList_NewBlackListReasonCode;
	protected Button button_BlackListReasonCodeList_BlackListReasonCodeSearchDialog;

	protected Listbox sortOperator_bLRsnCode;
	protected Listbox sortOperator_bLRsnDesc;
	protected Listbox sortOperator_bLIsActive;

	protected Textbox bLRsnCode;
	protected Textbox bLRsnDesc;
	protected Checkbox bLIsActive;

	private transient BlackListReasonCodeService blackListReasonCodeService;

	/**
	 * default constructor.<br>
	 */
	public BlackListReasonCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BlackListReasonCode";
		super.pageRightName = "BlackListReasonCodeList";
		super.tableName = "BMTBlackListRsnCodes_AView";
		super.queueTableName = "BMTBlackListRsnCodes_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("bLRsnCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_BlackListReasonCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_BlackListReasonCodeList, borderLayout_BlackListReasonCodeList,
				listBoxBlackListReasonCode, pagingBlackListReasonCodeList);
		setItemRender(new BlackListReasonCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BlackListReasonCodeList_NewBlackListReasonCode,
				"button_BlackListReasonCodeList_NewBlackListReasonCode", true);
		registerButton(button_BlackListReasonCodeList_BlackListReasonCodeSearchDialog);

		registerField("bLRsnCode", listheader_BLRsnCode, SortOrder.ASC, bLRsnCode, sortOperator_bLRsnCode,
				Operators.STRING);
		registerField("bLRsnDesc", listheader_BLRsnDesc, SortOrder.NONE, bLRsnDesc, sortOperator_bLRsnDesc,
				Operators.STRING);
		registerField("bLIsActive", listheader_BLIsActive, SortOrder.NONE, bLIsActive, sortOperator_bLIsActive,
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
	public void onClick$button_BlackListReasonCodeList_BlackListReasonCodeSearchDialog(Event event) {
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
	public void onClick$button_BlackListReasonCodeList_NewBlackListReasonCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		BlackListReasonCode blackListReasonCode = new BlackListReasonCode();
		blackListReasonCode.setNewRecord(true);
		blackListReasonCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(blackListReasonCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onBlackListReasonCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBlackListReasonCode.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		BlackListReasonCode blackListReasonCode = blackListReasonCodeService.getBlackListReasonCodeById(id);
		
		if (blackListReasonCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BLRsnCode='" + blackListReasonCode.getBLRsnCode() + "' AND version="
				+ blackListReasonCode.getVersion() + " ";

		if (doCheckAuthority(blackListReasonCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && blackListReasonCode.getWorkflowId() == 0) {
				blackListReasonCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(blackListReasonCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param blackListReasonCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BlackListReasonCode blackListReasonCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("blackListReasonCode", blackListReasonCode);
		arg.put("blackListReasonCodeListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/BlackListReasonCode/BlackListReasonCodeDialog.zul", null, arg);
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

	public void setBlackListReasonCodeService(BlackListReasonCodeService blackListReasonCodeService) {
		this.blackListReasonCodeService = blackListReasonCodeService;
	}
}