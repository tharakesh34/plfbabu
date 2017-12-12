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
 * FileName    		:  SalutationListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.salutation;

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

import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.systemmasters.SalutationService;
import com.pennant.webui.systemmasters.salutation.model.SalutationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/Salutation/SalutationList.zul file.
 */
public class SalutationListCtrl extends GFCBaseListCtrl<Salutation> {
	private static final long serialVersionUID = 1690558052025431845L;
	private static final Logger logger = Logger.getLogger(SalutationListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SalutationList;
	protected Borderlayout borderLayout_SalutationList;
	protected Paging pagingSalutationList;
	protected Listbox listBoxSalutation;

	// List headers
	protected Listheader listheader_SalutationCode;
	protected Listheader listheader_SaluationDesc;
	protected Listheader listheader_SalutationIsActive;

	protected Textbox salutationCode;
	protected Textbox saluationDesc;
	protected Checkbox salutationIsActive;

	protected Listbox sortOperator_salutationCode;
	protected Listbox sortOperator_saluationDesc;
	protected Listbox sortOperator_salutationIsActive;

	// checkRights
	protected Button button_SalutationList_NewSalutation;
	protected Button button_SalutationList_SalutationSearchDialog;

	private transient SalutationService salutationService;

	/**
	 * default constructor.<br>
	 */
	public SalutationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Salutation";
		super.pageRightName = "SalutationList";
		super.tableName = "BMTSalutations_AView";
		super.queueTableName = "BMTSalutations_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SalutationList(Event event) {
		// Set the page level components.
		setPageComponents(window_SalutationList, borderLayout_SalutationList, listBoxSalutation, pagingSalutationList);
		setItemRender(new SalutationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SalutationList_NewSalutation, "button_SalutationList_NewSalutation", true);
		registerButton(button_SalutationList_SalutationSearchDialog);

		registerField("salutationCode", listheader_SalutationCode, SortOrder.ASC, salutationCode,
				sortOperator_salutationCode, Operators.STRING);
		registerField("saluationDesc", listheader_SaluationDesc, SortOrder.NONE, saluationDesc,
				sortOperator_saluationDesc, Operators.STRING);
		registerField("salutationIsActive", listheader_SalutationIsActive, SortOrder.NONE, salutationIsActive,
				sortOperator_salutationIsActive, Operators.BOOLEAN);

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
	public void onClick$button_SalutationList_SalutationSearchDialog(Event event) {
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
	public void onClick$button_SalutationList_NewSalutation(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Salutation salutation = new Salutation();
		salutation.setNewRecord(true);
		salutation.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(salutation);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSalutationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSalutation.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Salutation salutation = salutationService.getSalutationById(id);

		if (salutation == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SalutationCode='" + salutation.getSalutationCode() + "' AND version="
				+ salutation.getVersion() + " ";
		if (doCheckAuthority(salutation, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && salutation.getWorkflowId() == 0) {
				salutation.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(salutation);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param salutation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Salutation salutation) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("salutation", salutation);
		arg.put("salutationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Salutation/SalutationDialog.zul", null, arg);
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

	public void setSalutationService(SalutationService salutationService) {
		this.salutationService = salutationService;
	}
}