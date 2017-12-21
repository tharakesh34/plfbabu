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
 * FileName    		:  FinanceApplicationCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.financeapplicationcode;

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

import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.service.applicationmaster.FinanceApplicationCodeService;
import com.pennant.webui.applicationmaster.financeapplicationcode.model.FinanceApplicationCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/FinanceApplicationCode/FinanceApplicationCodeList.zul file.
 */
public class FinanceApplicationCodeListCtrl extends GFCBaseListCtrl<FinanceApplicationCode> {
	private static final long serialVersionUID = -3571720185247491921L;
	private static final Logger logger = Logger.getLogger(FinanceApplicationCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceApplicationCodeList;
	protected Borderlayout borderLayout_FinanceApplicationCodeList;
	protected Paging pagingFinanceApplicationCodeList;
	protected Listbox listBoxFinanceApplicationCode;

	protected Textbox finAppType;
	protected Textbox finAppDesc;
	protected Checkbox finAppIsActive;

	protected Listbox sortOperator_finAppType;
	protected Listbox sortOperator_finAppDesc;
	protected Listbox sortOperator_finAppIsActive;

	// List headers
	protected Listheader listheader_FinAppType;
	protected Listheader listheader_FinAppDesc;
	protected Listheader listheader_FinAppIsActive;

	// checkRights
	protected Button button_FinanceApplicationCodeList_NewFinanceApplicationCode;
	protected Button button_FinanceApplicationCodeList_FinanceApplicationCodeSearchDialog;

	private transient FinanceApplicationCodeService financeApplicationCodeService;

	/**
	 * default constructor.<br>
	 */
	public FinanceApplicationCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceApplicationCode";
		super.pageRightName = "FinanceApplicationCodeList";
		super.tableName = "BMTFinAppCodes_AView";
		super.queueTableName = "BMTFinAppCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinanceApplicationCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_FinanceApplicationCodeList, borderLayout_FinanceApplicationCodeList,
				listBoxFinanceApplicationCode, pagingFinanceApplicationCodeList);
		setItemRender(new FinanceApplicationCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinanceApplicationCodeList_NewFinanceApplicationCode,
				"button_FinanceApplicationCodeList_NewFinanceApplicationCode", true);
		registerButton(button_FinanceApplicationCodeList_FinanceApplicationCodeSearchDialog);

		registerField("finAppType", listheader_FinAppType, SortOrder.ASC, finAppType, sortOperator_finAppType,
				Operators.STRING);
		registerField("finAppDesc", listheader_FinAppDesc, SortOrder.NONE, finAppDesc, sortOperator_finAppDesc,
				Operators.STRING);
		registerField("finAppIsActive", listheader_FinAppIsActive, SortOrder.NONE, finAppIsActive,
				sortOperator_finAppIsActive, Operators.BOOLEAN);

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
	public void onClick$button_FinanceApplicationCodeList_FinanceApplicationCodeSearchDialog(Event event) {
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
	public void onClick$button_FinanceApplicationCodeList_NewFinanceApplicationCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinanceApplicationCode financeApplicationCode = new FinanceApplicationCode();
		financeApplicationCode.setNewRecord(true);
		financeApplicationCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(financeApplicationCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFinanceApplicationCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinanceApplicationCode.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		FinanceApplicationCode financeApplicationCode = financeApplicationCodeService.getFinanceApplicationCodeById(id);

		if (financeApplicationCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinAppType='" + financeApplicationCode.getFinAppType() + "' AND version="
				+ financeApplicationCode.getVersion() + " ";

		if (doCheckAuthority(financeApplicationCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && financeApplicationCode.getWorkflowId() == 0) {
				financeApplicationCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(financeApplicationCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param financeApplicationCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinanceApplicationCode financeApplicationCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeApplicationCode", financeApplicationCode);
		arg.put("financeApplicationCodeListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/FinanceApplicationCode/FinanceApplicationCodeDialog.zul", null,
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

	public void setFinanceApplicationCodeService(FinanceApplicationCodeService financeApplicationCodeService) {
		this.financeApplicationCodeService = financeApplicationCodeService;
	}
}