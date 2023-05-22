/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : BuilderGroupListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-05-2017 * * Modified
 * Date : 17-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.buildergroup;

import java.util.Map;

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

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.systemmasters.BuilderGroup;
import com.pennant.backend.service.systemmasters.BuilderGroupService;
import com.pennant.webui.systemmasters.buildergroup.model.BuilderGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.masters/BuilderGroup/BuilderGroupList.zul file.
 * 
 */
public class BuilderGroupListCtrl extends GFCBaseListCtrl<BuilderGroup> {
	private static final long serialVersionUID = 1L;

	protected Window window_BuilderGroupList;
	protected Borderlayout borderLayout_BuilderGroupList;
	protected Paging pagingBuilderGroupList;
	protected Listbox listBoxBuilderGroup;

	// List headers
	protected Listheader listheader_name;
	protected Listheader listheader_segmentation;

	// checkRights
	protected Button button_BuilderGroupList_NewBuilderGroup;
	protected Button button_BuilderGroupList_BuilderGroupSearch;

	// Search Fields
	protected Textbox name; // autowired
	protected ExtendedCombobox segmentation; // autowired

	protected Listbox sortOperator_name;
	protected Listbox sortOperator_segmentation;

	private transient BuilderGroupService builderGroupService;

	/**
	 * default constructor.<br>
	 */
	public BuilderGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BuilderGroup";
		super.pageRightName = "BuilderGroupList";
		super.tableName = "BuilderGroup_AView";
		super.queueTableName = "BuilderGroup_View";
		super.enquiryTableName = "BuilderGroup_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BuilderGroupList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_BuilderGroupList, borderLayout_BuilderGroupList, listBoxBuilderGroup,
				pagingBuilderGroupList);
		setItemRender(new BuilderGroupListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BuilderGroupList_BuilderGroupSearch);
		registerButton(button_BuilderGroupList_NewBuilderGroup, "button_BuilderGroupList_NewBuilderGroup", true);

		registerField("id");
		registerField("name", listheader_name, SortOrder.NONE, name, sortOperator_name, Operators.STRING);
		registerField("segmentation", listheader_segmentation, SortOrder.NONE, segmentation, sortOperator_segmentation,
				Operators.STRING);

		// Render the page and display the data.
		doSetFieldProperties();
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_BuilderGroupList_BuilderGroupSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_BuilderGroupList_NewBuilderGroup(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		BuilderGroup buildergroup = new BuilderGroup();
		buildergroup.setNewRecord(true);
		buildergroup.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(buildergroup);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		this.segmentation.setModuleName("LovFieldDetail");
		this.segmentation.setValueColumn("FieldCodeValue");
		this.segmentation.setDescColumn("ValueDesc");
		this.segmentation.setValidateColumns(new String[] { "FieldCodeValue" });
		Filter segmentFilter[] = new Filter[1];
		segmentFilter[0] = new Filter("FieldCode", "SEGMENT", Filter.OP_EQUAL);
		this.segmentation.setFilters(segmentFilter);
		logger.debug("Leaving ");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onBuilderGroupItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBuilderGroup.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		BuilderGroup buildergroup = builderGroupService.getBuilderGroup(id);

		if (buildergroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append(" where id =?");

		if (doCheckAuthority(buildergroup, whereCond.toString(), new Object[] { buildergroup.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && buildergroup.getWorkflowId() == 0) {
				buildergroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(buildergroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param buildergroup The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BuilderGroup buildergroup) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("builderGroup", buildergroup);
		arg.put("builderGroupListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BuilderGroup/BuilderGroupDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setBuilderGroupService(BuilderGroupService builderGroupService) {
		this.builderGroupService = builderGroupService;
	}
}