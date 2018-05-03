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
 * FileName    		:  RelationshipOfficerListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.relationshipofficer;

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

import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.webui.applicationmaster.relationshipofficer.model.RelationshipOfficerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/RelationshipOfficer
 * /RelationshipOfficerList.zul file.
 */
public class RelationshipOfficerListCtrl extends GFCBaseListCtrl<RelationshipOfficer> {
	private static final long serialVersionUID = 2977963103737338816L;
	private static final Logger logger = Logger.getLogger(RelationshipOfficerListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RelationshipOfficerList;
	protected Borderlayout borderLayout_RelationshipOfficerList;
	protected Paging pagingRelationshipOfficerList;
	protected Listbox listBoxRelationshipOfficer;

	protected Listheader listheader_ROfficerCode;
	protected Listheader listheader_ROfficerDesc;
	protected Listheader listheader_ROfficerDeptCode;
	protected Listheader listheader_ROfficerIsActive;

	protected Button button_RelationshipOfficerList_NewRelationshipOfficer;
	protected Button button_RelationshipOfficerList_RelationshipOfficerSearchDialog;

	protected Textbox rOfficerCode;
	protected Textbox rOfficerDesc;
	protected Textbox rOfficerDeptCode;
	protected Checkbox rOfficerIsActive;

	protected Listbox sortOperator_rOfficerCode;
	protected Listbox sortOperator_rOfficerDesc;
	protected Listbox sortOperator_rOfficerDeptCode;
	protected Listbox sortOperator_rOfficerIsActive;

	private transient RelationshipOfficerService relationshipOfficerService;

	/**
	 * default constructor.<br>
	 */
	public RelationshipOfficerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "RelationshipOfficer";
		super.pageRightName = "RelationshipOfficerList";
		super.tableName = "RelationshipOfficers_AView";
		super.queueTableName = "RelationshipOfficers_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_RelationshipOfficerList(Event event) {
		// Set the page level components.
		setPageComponents(window_RelationshipOfficerList, borderLayout_RelationshipOfficerList,
				listBoxRelationshipOfficer, pagingRelationshipOfficerList);
		setItemRender(new RelationshipOfficerListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_RelationshipOfficerList_NewRelationshipOfficer,
				"button_RelationshipOfficerList_NewRelationshipOfficer", true);
		registerButton(button_RelationshipOfficerList_RelationshipOfficerSearchDialog);

		registerField("rOfficerCode", listheader_ROfficerCode, SortOrder.ASC, rOfficerCode, sortOperator_rOfficerCode,
				Operators.STRING);
		registerField("rOfficerDesc", listheader_ROfficerDesc, SortOrder.NONE, rOfficerDesc, sortOperator_rOfficerDesc,
				Operators.STRING);
		registerField("rOfficerDeptCode", listheader_ROfficerDeptCode, SortOrder.NONE, rOfficerDeptCode,
				sortOperator_rOfficerDeptCode, Operators.STRING);
		registerField("lovDescROfficerDeptCodeName");
		registerField("rOfficerIsActive", listheader_ROfficerIsActive, SortOrder.NONE, rOfficerIsActive,
				sortOperator_rOfficerIsActive, Operators.BOOLEAN);

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
	public void onClick$button_RelationshipOfficerList_RelationshipOfficerSearchDialog(Event event) {
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
	public void onClick$button_RelationshipOfficerList_NewRelationshipOfficer(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		RelationshipOfficer relationshipOfficer = new RelationshipOfficer();
		relationshipOfficer.setNewRecord(true);
		relationshipOfficer.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(relationshipOfficer);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onRelationshipOfficerItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxRelationshipOfficer.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		RelationshipOfficer relationshipOfficer = relationshipOfficerService.getRelationshipOfficerById(id);

		if (relationshipOfficer == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ROfficerCode='" + relationshipOfficer.getROfficerCode() + "' AND version="
				+ relationshipOfficer.getVersion() + " ";

		if (doCheckAuthority(relationshipOfficer, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && relationshipOfficer.getWorkflowId() == 0) {
				relationshipOfficer.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(relationshipOfficer);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aRelationshipOfficer
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(RelationshipOfficer aRelationshipOfficer) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("relationshipOfficer", aRelationshipOfficer);
		arg.put("relationshipOfficerListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/RelationshipOfficer/RelationshipOfficerDialog.zul", null, arg);
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

	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}
}