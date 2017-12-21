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
 * FileName    		:  PRelationCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.prelationcode;

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

import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennant.backend.service.systemmasters.PRelationCodeService;
import com.pennant.webui.systemmasters.prelationcode.model.PRelationCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/PRelationCode/PRelationCodeList.zul file.
 */
public class PRelationCodeListCtrl extends GFCBaseListCtrl<PRelationCode> {
	private static final long serialVersionUID = -6390654977697169073L;
	private static final Logger logger = Logger.getLogger(PRelationCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PRelationCodeList;
	protected Borderlayout borderLayout_PRelationCodeList;
	protected Paging pagingPRelationCodeList;
	protected Listbox listBoxPRelationCode;

	protected Textbox pRelationCode;
	protected Textbox pRelationDesc;
	protected Checkbox relationCodeIsActive;

	protected Listbox sortOperator_pRelationDesc;
	protected Listbox sortOperator_pRelationCode;
	protected Listbox sortOperator_relationCodeIsActive;

	// List headers
	protected Listheader listheader_PRelationCode;
	protected Listheader listheader_PRelationDesc;
	protected Listheader listheader_RelationCodeIsActive;

	// checkRights
	protected Button button_PRelationCodeList_NewPRelationCode;
	protected Button button_PRelationCodeList_PRelationCodeSearchDialog;

	private transient PRelationCodeService pRelationCodeService;

	/**
	 * default constructor.<br>
	 */
	public PRelationCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PRelationCode";
		super.pageRightName = "PRelationCodeList";
		super.tableName = "BMTPRelationCodes_AView";
		super.queueTableName = "BMTPRelationCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PRelationCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_PRelationCodeList, borderLayout_PRelationCodeList, listBoxPRelationCode,
				pagingPRelationCodeList);
		setItemRender(new PRelationCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PRelationCodeList_NewPRelationCode, "button_PRelationCodeList_NewPRelationCode", true);
		registerButton(button_PRelationCodeList_PRelationCodeSearchDialog);

		registerField("pRelationCode", listheader_PRelationCode, SortOrder.ASC, pRelationCode,
				sortOperator_pRelationCode, Operators.STRING);
		registerField("pRelationDesc", listheader_PRelationDesc, SortOrder.NONE, pRelationDesc,
				sortOperator_pRelationDesc, Operators.STRING);
		registerField("relationCodeIsActive", listheader_RelationCodeIsActive, SortOrder.NONE, relationCodeIsActive,
				sortOperator_relationCodeIsActive, Operators.BOOLEAN);

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
	public void onClick$button_PRelationCodeList_PRelationCodeSearchDialog(Event event) {
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
	public void onClick$button_PRelationCodeList_NewPRelationCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		PRelationCode pRelationCode = new PRelationCode();
		pRelationCode.setNewRecord(true);
		pRelationCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(pRelationCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPRelationCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPRelationCode.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		PRelationCode pRelationCode = pRelationCodeService.getPRelationCodeById(id);

		if (pRelationCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND PRelationCode='" + pRelationCode.getPRelationCode() + "' AND version="
				+ pRelationCode.getVersion() + " ";
		if (doCheckAuthority(pRelationCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && pRelationCode.getWorkflowId() == 0) {
				pRelationCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(pRelationCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param pRelationCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PRelationCode pRelationCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("pRelationCode", pRelationCode);
		arg.put("pRelationCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/PRelationCode/PRelationCodeDialog.zul", null, arg);
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

	public void setPRelationCodeService(PRelationCodeService pRelationCodeService) {
		this.pRelationCodeService = pRelationCodeService;
	}
}