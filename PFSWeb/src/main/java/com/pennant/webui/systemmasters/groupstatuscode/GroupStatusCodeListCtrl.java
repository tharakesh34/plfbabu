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
 * FileName    		:  GroupStatusCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.groupstatuscode;

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

import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.service.systemmasters.GroupStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.groupstatuscode.model.GroupStatusCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/GroupStatusCode/GroupStatusCodeList.zul file.
 */
public class GroupStatusCodeListCtrl extends GFCBaseListCtrl<GroupStatusCode> {
	private static final long serialVersionUID = -8159846804242209891L;
	private static final Logger logger = Logger.getLogger(GroupStatusCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_GroupStatusCodeList;
	protected Borderlayout borderLayout_GroupStatusCodeList;
	protected Paging pagingGroupStatusCodeList;
	protected Listbox listBoxGroupStatusCode;

	protected Textbox grpStsCode;
	protected Textbox grpStsDescription;
	protected Checkbox grpStsIsActive;

	protected Listbox sortOperator_grpStsDescription;
	protected Listbox sortOperator_grpStsCode;
	protected Listbox sortOperator_grpStsIsActive;

	// List headers
	protected Listheader listheader_GrpStsCode;
	protected Listheader listheader_GrpStsDescription;
	protected Listheader listheader_GrpStsIsActive;

	// checkRights
	protected Button button_GroupStatusCodeList_NewGroupStatusCode;
	protected Button button_GroupStatusCodeList_GroupStatusCodeSearchDialog;

	private transient GroupStatusCodeService groupStatusCodeService;

	/**
	 * default constructor.<br>
	 */
	public GroupStatusCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "GroupStatusCode";
		super.pageRightName = "GroupStatusCodeList";
		super.tableName = "BMTGrpStatusCodes_AView";
		super.queueTableName = "BMTGrpStatusCodes_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("GrpStsCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_GroupStatusCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_GroupStatusCodeList, borderLayout_GroupStatusCodeList, listBoxGroupStatusCode,
				pagingGroupStatusCodeList);
		setItemRender(new GroupStatusCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_GroupStatusCodeList_NewGroupStatusCode, "button_GroupStatusCodeList_NewGroupStatusCode",
				true);
		registerButton(button_GroupStatusCodeList_GroupStatusCodeSearchDialog);

		registerField("grpStsCode", listheader_GrpStsCode, SortOrder.ASC, grpStsCode, sortOperator_grpStsCode,
				Operators.STRING);
		registerField("grpStsDescription", listheader_GrpStsDescription, SortOrder.NONE, grpStsDescription,
				sortOperator_grpStsDescription, Operators.STRING);
		registerField("grpStsIsActive", listheader_GrpStsIsActive, SortOrder.NONE, grpStsIsActive,
				sortOperator_grpStsIsActive, Operators.BOOLEAN);

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
	public void onClick$button_GroupStatusCodeList_GroupStatusCodeSearchDialog(Event event) {
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
	public void onClick$button_GroupStatusCodeList_NewGroupStatusCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		GroupStatusCode groupStatusCode = new GroupStatusCode();
		groupStatusCode.setNewRecord(true);
		groupStatusCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(groupStatusCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onGroupStatusCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxGroupStatusCode.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		GroupStatusCode groupStatusCode = groupStatusCodeService.getGroupStatusCodeById(id);

		if (groupStatusCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND GrpStsCode='" + groupStatusCode.getGrpStsCode() + "' AND version="
				+ groupStatusCode.getVersion() + " ";

		if (doCheckAuthority(groupStatusCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && groupStatusCode.getWorkflowId() == 0) {
				groupStatusCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(groupStatusCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param groupStatusCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(GroupStatusCode groupStatusCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("groupStatusCode", groupStatusCode);
		arg.put("groupStatusCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/GroupStatusCode/GroupStatusCodeDialog.zul", null,
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

	public void setGroupStatusCodeService(GroupStatusCodeService groupStatusCodeService) {
		this.groupStatusCodeService = groupStatusCodeService;
	}
}