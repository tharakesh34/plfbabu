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
 * FileName    		:  AgreementDefinitionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-11-2011    														*
 *                                                                  						*
 * Modified Date    :  23-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.agreementdefinition;

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

import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.webui.applicationmaster.agreementdefinition.model.AgreementDefinitionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/AgreementDefinition
 * /AgreementDefinitionList.zul file.
 */
public class AgreementDefinitionListCtrl extends GFCBaseListCtrl<AgreementDefinition> {
	private static final long serialVersionUID = 1225118639931508378L;
	private static final Logger logger = Logger.getLogger(AgreementDefinitionListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AgreementDefinitionList;
	protected Borderlayout borderLayout_AgreementDefinitionList;
	protected Paging pagingAgreementDefinitionList;
	protected Listbox listBoxAgreementDefinition;

	protected Textbox aggCode;
	protected Textbox aggName;
	protected Textbox aggDesc;
	protected Checkbox aggIsActive;

	protected Listbox sortOperator_aggCode;
	protected Listbox sortOperator_aggName;
	protected Listbox sortOperator_aggDesc;
	protected Listbox sortOperator_aggIsActive;

	// List headers
	protected Listheader listheader_AggCode;
	protected Listheader listheader_AggName;
	protected Listheader listheader_AggDesc;
	protected Listheader listheader_AggIsActive;

	// checkRights
	protected Button button_AgreementDefinitionList_NewAgreementDefinition;
	protected Button button_AgreementDefinitionList_AgreementDefinitionSearchDialog;

	private transient AgreementDefinitionService agreementDefinitionService;

	/**
	 * default constructor.<br>
	 */
	public AgreementDefinitionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AgreementDefinition";
		super.pageRightName = "AgreementDefinitionList";
		super.tableName = "BMTAggrementDef_AView";
		super.queueTableName = "BMTAggrementDef_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_AgreementDefinitionList(Event event) {
		// Set the page level components.
		setPageComponents(window_AgreementDefinitionList, borderLayout_AgreementDefinitionList,
				listBoxAgreementDefinition, pagingAgreementDefinitionList);
		setItemRender(new AgreementDefinitionListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AgreementDefinitionList_NewAgreementDefinition,
				"button_AgreementDefinitionList_NewAgreementDefinition", true);
		registerButton(button_AgreementDefinitionList_AgreementDefinitionSearchDialog);

		registerField("aggId");
		registerField("aggCode", listheader_AggCode, SortOrder.ASC, aggCode, sortOperator_aggCode, Operators.STRING);
		registerField("aggName", listheader_AggName, SortOrder.NONE, aggName, sortOperator_aggName, Operators.STRING);
		registerField("aggDesc", listheader_AggDesc, SortOrder.NONE, aggDesc, sortOperator_aggDesc, Operators.STRING);
		registerField("aggIsActive", listheader_AggIsActive, SortOrder.NONE, aggIsActive, sortOperator_aggIsActive,
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
	public void onClick$button_AgreementDefinitionList_AgreementDefinitionSearchDialog(Event event) {
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
	public void onClick$button_AgreementDefinitionList_NewAgreementDefinition(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		AgreementDefinition agreementDefinition = new AgreementDefinition();
		agreementDefinition.setNewRecord(true);
		agreementDefinition.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(agreementDefinition);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onAgreementDefinitionItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAgreementDefinition.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		long id = ((long) selectedItem.getAttribute("id"));
		AgreementDefinition agreementDefinition = agreementDefinitionService.getAgreementDefinitionById(id);

		if (agreementDefinition == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND AggCode='" + agreementDefinition.getAggCode() + "' AND version="
				+ agreementDefinition.getVersion() + " ";

		if (doCheckAuthority(agreementDefinition, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && agreementDefinition.getWorkflowId() == 0) {
				agreementDefinition.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(agreementDefinition);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param agreementDefinition
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AgreementDefinition agreementDefinition) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("agreementDefinition", agreementDefinition);
		arg.put("agreementDefinitionListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/AgreementDefinition/AgreementDefinitionDialog.zul", null, arg);
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

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}
}