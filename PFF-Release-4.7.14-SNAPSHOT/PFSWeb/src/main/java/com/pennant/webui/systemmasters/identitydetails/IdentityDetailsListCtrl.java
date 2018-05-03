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
 * FileName    		:  IdentityDetailsListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.identitydetails;

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

import com.pennant.backend.model.systemmasters.IdentityDetails;
import com.pennant.backend.service.systemmasters.IdentityDetailsService;
import com.pennant.webui.systemmasters.identitydetails.model.IdentityDetailsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/IdentityDetails/IdentityDetailsList.zul file.
 */
public class IdentityDetailsListCtrl extends GFCBaseListCtrl<IdentityDetails> {
	private static final long serialVersionUID = 7858815454023737745L;
	private static final Logger logger = Logger.getLogger(IdentityDetailsListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_IdentityDetailsList;
	protected Borderlayout borderLayout_IdentityDetailsList;
	protected Paging pagingIdentityDetailsList;
	protected Listbox listBoxIdentityDetails;

	protected Textbox identityType;
	protected Textbox identityDesc;

	protected Listbox sortOperator_identityDesc;
	protected Listbox sortOperator_identityType;

	// List headers
	protected Listheader listheader_IdentityType;
	protected Listheader listheader_IdentityDesc;

	// checkRights
	protected Button button_IdentityDetailsList_NewIdentityDetails;
	protected Button button_IdentityDetailsList_IdentityDetailsSearchDialog;

	private transient IdentityDetailsService identityDetailsService;

	/**
	 * default constructor.<br>
	 */
	public IdentityDetailsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "IdentityDetails";
		super.pageRightName = "IdentityDetailsList";
		super.tableName = "BMTIdentityType_AView";
		super.queueTableName = "BMTIdentityType_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_IdentityDetailsList(Event event) {
		// Set the page level components.
		setPageComponents(window_IdentityDetailsList, borderLayout_IdentityDetailsList, listBoxIdentityDetails,
				pagingIdentityDetailsList);
		setItemRender(new IdentityDetailsListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_IdentityDetailsList_NewIdentityDetails, "button_IdentityDetailsList_NewIdentityDetails",
				true);
		registerButton(button_IdentityDetailsList_IdentityDetailsSearchDialog);

		registerField("identityType", listheader_IdentityType, SortOrder.ASC, identityType, sortOperator_identityType,
				Operators.STRING);
		registerField("identityDesc", listheader_IdentityDesc, SortOrder.NONE, identityDesc, sortOperator_identityDesc,
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
	public void onClick$button_IdentityDetailsList_IdentityDetailsSearchDialog(Event event) {
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
	public void onClick$button_IdentityDetailsList_NewIdentityDetails(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		IdentityDetails identityDetails = new IdentityDetails();
		identityDetails.setNewRecord(true);
		identityDetails.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(identityDetails);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onIdentityDetailsItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxIdentityDetails.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		IdentityDetails identityDetails = identityDetailsService.getIdentityDetailsById(id);

		if (identityDetails == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND IdentityType='" + identityDetails.getIdentityType() + "' AND version="
				+ identityDetails.getVersion() + " ";

		if (doCheckAuthority(identityDetails, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && identityDetails.getWorkflowId() == 0) {
				identityDetails.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(identityDetails);
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
	private void doShowDialogPage(IdentityDetails aIdentityDetails) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("identityDetails", aIdentityDetails);
		arg.put("identityDetailsListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/IdentityDetails/IdentityDetailsDialog.zul", null,
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

	public void setIdentityDetailsService(IdentityDetailsService identityDetailsService) {
		this.identityDetailsService = identityDetailsService;
	}
}