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
 * FileName    		:  GeneralDesignationListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.generaldesignation;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.service.systemmasters.GeneralDesignationService;
import com.pennant.webui.systemmasters.generaldesignation.model.GeneralDesignationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/GeneralDesignation/GeneralDesignationList.zul file.
 */
public class GeneralDesignationListCtrl extends GFCBaseListCtrl<GeneralDesignation> {
	private static final long serialVersionUID = -1695611844309365191L;
	private static final Logger logger = Logger.getLogger(GeneralDesignationListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_GeneralDesignationList;
	protected Borderlayout borderLayout_GeneralDesignationList;
	protected Paging pagingGeneralDesignationList;
	protected Listbox listBoxGeneralDesignation;

	protected Textbox genDesignation;
	protected Textbox genDesgDesc;

	protected Listbox sortOperator_genDesignation;
	protected Listbox sortOperator_genDesgDesc;

	// List headers
	protected Listheader listheader_GenDesignation;
	protected Listheader listheader_GenDesgDesc;

	// checkRights
	protected Button button_GeneralDesignationList_NewGeneralDesignation;
	protected Button button_GeneralDesignationList_GeneralDesignationSearchDialog;

	private transient GeneralDesignationService generalDesignationService;

	/**
	 * default constructor.<br>
	 */
	public GeneralDesignationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "GeneralDesignation";
		super.pageRightName = "GeneralDesignationList";
		super.tableName = "RMTGenDesignations_AView";
		super.queueTableName = "RMTGenDesignations_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_GeneralDesignationList(Event event) {
		// Set the page level components.
		setPageComponents(window_GeneralDesignationList, borderLayout_GeneralDesignationList,
				listBoxGeneralDesignation, pagingGeneralDesignationList);
		setItemRender(new GeneralDesignationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_GeneralDesignationList_NewGeneralDesignation,
				"button_GeneralDesignationList_NewGeneralDesignation", true);
		registerButton(button_GeneralDesignationList_GeneralDesignationSearchDialog);

		registerField("genDesignation", listheader_GenDesignation, SortOrder.ASC, genDesignation,
				sortOperator_genDesignation, Operators.STRING);
		registerField("genDesgDesc", listheader_GenDesgDesc, SortOrder.NONE, genDesgDesc, sortOperator_genDesgDesc,
				Operators.STRING);

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
	public void onClick$button_GeneralDesignationList_GeneralDesignationSearchDialog(Event event) {
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
	public void onClick$button_GeneralDesignationList_NewGeneralDesignation(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		GeneralDesignation generalDesignation = new GeneralDesignation();
		generalDesignation.setNewRecord(true);
		generalDesignation.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(generalDesignation);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onGeneralDesignationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxGeneralDesignation.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		GeneralDesignation generalDesignation = generalDesignationService.getGeneralDesignationById(id);

		if (generalDesignation == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND GenDesignation='" + generalDesignation.getGenDesignation() + "' AND version="
				+ generalDesignation.getVersion() + " ";

		if (doCheckAuthority(generalDesignation, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && generalDesignation.getWorkflowId() == 0) {
				generalDesignation.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(generalDesignation);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param generalDesignation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(GeneralDesignation generalDesignation) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("generalDesignation", generalDesignation);
		arg.put("generalDesignationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/GeneralDesignation/GeneralDesignationDialog.zul",
					null, arg);
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

	public void setGeneralDesignationService(GeneralDesignationService generalDesignationService) {
		this.generalDesignationService = generalDesignationService;
	}
}