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
 * * FileName : BuilderCompanyListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-05-2017 * *
 * Modified Date : 22-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.buildercompany;

import java.util.Map;

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

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.service.systemmasters.BuilderCompanyService;
import com.pennant.webui.systemmasters.buildercompany.model.BuilderCompanyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.masters/BuilderCompany/BuilderCompanyList.zul file.
 * 
 */
public class BuilderCompanyListCtrl extends GFCBaseListCtrl<BuilderCompany> {
	private static final long serialVersionUID = 1L;

	protected Window window_BuilderCompanyList;
	protected Borderlayout borderLayout_BuilderCompanyList;
	protected Paging pagingBuilderCompanyList;
	protected Listbox listBoxBuilderCompany;

	// List headers
	protected Listheader listheader_name;
	protected Listheader listheader_segmentation;
	protected Listheader listheader_groupId;
	// protected Listheader listheader_active;

	// checkRights
	protected Button button_BuilderCompanyList_NewBuilderCompany;
	protected Button button_BuilderCompanyList_BuilderCompanySearch;

	// Search Fields
	protected Textbox name; // autowired
	protected ExtendedCombobox segmentation; // autowired
	protected ExtendedCombobox groupId; // autowired

	protected Checkbox active; // autowired

	protected Listbox sortOperator_name;
	protected Listbox sortOperator_segmentation;
	protected Listbox sortOperator_groupId;
	protected Listbox sortOperator_Active;

	private transient BuilderCompanyService builderCompanyService;

	/**
	 * default constructor.<br>
	 */
	public BuilderCompanyListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BuilderCompany";
		super.pageRightName = "BuilderCompanyList";
		super.tableName = "BuilderCompany_AView";
		super.queueTableName = "BuilderCompany_View";
		super.enquiryTableName = "BuilderCompany_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilter(new Filter("FieldCode", "SEGMENT", Filter.OP_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BuilderCompanyList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_BuilderCompanyList, borderLayout_BuilderCompanyList, listBoxBuilderCompany,
				pagingBuilderCompanyList);
		setItemRender(new BuilderCompanyListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BuilderCompanyList_BuilderCompanySearch);
		registerButton(button_BuilderCompanyList_NewBuilderCompany, "button_BuilderCompanyList_NewBuilderCompany",
				true);

		registerField("id");
		registerField("name", listheader_name, SortOrder.NONE, name, sortOperator_name, Operators.STRING);
		registerField("segmentation", listheader_segmentation, SortOrder.NONE, segmentation, sortOperator_segmentation,
				Operators.STRING);
		registerField("groupId", listheader_groupId, SortOrder.NONE, groupId, sortOperator_groupId, Operators.NUMERIC);
		// registerField("active", listheader_active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);
		registerField("groupIdName");
		doSetFieldProperties();
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_BuilderCompanyList_BuilderCompanySearch(Event event) {
		search();
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

		this.groupId.setModuleName("BuilderGroup");
		this.groupId.setValueColumn("Id");
		this.groupId.setValueType(DataType.LONG);
		this.groupId.setDescColumn("Name");
		this.groupId.setValidateColumns(new String[] { "Id" });

		logger.debug("Leaving ");
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
	public void onClick$button_BuilderCompanyList_NewBuilderCompany(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		BuilderCompany buildercompany = new BuilderCompany();
		buildercompany.setNewRecord(true);
		buildercompany.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(buildercompany);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onBuilderCompanyItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBuilderCompany.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		BuilderCompany buildercompany = builderCompanyService.getBuilderCompany(id);

		if (buildercompany == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =? ");

		if (doCheckAuthority(buildercompany, whereCond.toString(), new Object[] { buildercompany.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && buildercompany.getWorkflowId() == 0) {
				buildercompany.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(buildercompany);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param buildercompany The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BuilderCompany buildercompany) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("buildercompany", buildercompany);
		arg.put("buildercompanyListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BuilderCompany/BuilderCompanyDialog.zul", null,
					arg);
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

	public void setBuilderCompanyService(BuilderCompanyService builderCompanyService) {
		this.builderCompanyService = builderCompanyService;
	}
}