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
 * * FileName : BuilderProjcetListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-05-2017 * *
 * Modified Date : 22-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.builderprojcet;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.service.systemmasters.BuilderProjcetService;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.systemmasters.builderprojcet.model.BuilderProjcetListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.masters/BuilderProjcet/BuilderProjcetList.zul file.
 * 
 */
public class BuilderProjcetListCtrl extends GFCBaseListCtrl<BuilderProjcet> {
	private static final long serialVersionUID = 1L;

	protected Window window_BuilderProjcetList;
	protected Borderlayout borderLayout_BuilderProjcetList;
	protected Paging pagingBuilderProjcetList;
	protected Listbox listBoxBuilderProjcet;

	// List headers
	protected Listheader listheader_id;
	protected Listheader listheader_name;
	protected Listheader listheader_builderId;
	protected Listheader listheader_apfNo;

	// checkRights
	protected Button button_BuilderProjcetList_NewBuilderProjcet;
	protected Button button_BuilderProjcetList_BuilderProjcetSearch;

	// Search Fields
	protected Longbox id; // autowired
	protected Uppercasebox name; // autowired
	protected ExtendedCombobox builderId; // autowired
	protected Textbox apfNo; // autowired

	protected Listbox sortOperator_id;
	protected Listbox sortOperator_name;
	protected Listbox sortOperator_builderId;
	protected Listbox sortOperator_apfNo;

	private transient BuilderProjcetService builderProjcetService;

	/**
	 * default constructor.<br>
	 */
	public BuilderProjcetListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BuilderProjcet";
		super.pageRightName = "BuilderProjcetList";
		super.tableName = "BuilderProjcet_AView";
		super.queueTableName = "BuilderProjcet_View";
		super.enquiryTableName = "BuilderProjcet_View";
	}

	/*
	 * @Override protected void doAddFilters() { super.doAddFilters(); this.searchObject.addFilter(new
	 * Filter("FieldCode", "SEGMENT", Filter.OP_EQUAL)); }
	 */

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BuilderProjcetList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_BuilderProjcetList, borderLayout_BuilderProjcetList, listBoxBuilderProjcet,
				pagingBuilderProjcetList);
		setItemRender(new BuilderProjcetListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BuilderProjcetList_BuilderProjcetSearch);
		registerButton(button_BuilderProjcetList_NewBuilderProjcet, "button_BuilderProjcetList_NewBuilderProjcet",
				true);

		registerField("id", listheader_id, SortOrder.NONE, id, sortOperator_id, Operators.NUMERIC);
		registerField("name", listheader_name, SortOrder.NONE, name, sortOperator_name, Operators.STRING);
		registerField("builderId", listheader_builderId, SortOrder.NONE, builderId, sortOperator_builderId,
				Operators.NUMERIC);
		// registerField("builderIdName");
		registerField("apfNo", listheader_apfNo, SortOrder.NONE, apfNo, sortOperator_apfNo, Operators.STRING);
		registerField("builderIdName");
		doSetFieldProperties();
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.name.setMaxlength(50);
		this.builderId.setModuleName("BuilderCompany");
		this.builderId.setValueColumn("Id");
		builderId.setValueType(DataType.LONG);
		this.builderId.setDescColumn("Name");
		this.builderId.setValidateColumns(new String[] { "Id" });
		this.apfNo.setMaxlength(20);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_BuilderProjcetList_BuilderProjcetSearch(Event event) {
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
	public void onClick$button_BuilderProjcetList_NewBuilderProjcet(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		BuilderProjcet builderprojcet = new BuilderProjcet();
		builderprojcet.setNewRecord(true);
		builderprojcet.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(builderprojcet);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onBuilderProjcetItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBuilderProjcet.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		BuilderProjcet builderprojcet = builderProjcetService.getBuilderProjcet(id);

		if (builderprojcet == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =?");

		if (doCheckAuthority(builderprojcet, whereCond.toString(), new Object[] { builderprojcet.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && builderprojcet.getWorkflowId() == 0) {
				builderprojcet.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(builderprojcet);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param builderprojcet The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BuilderProjcet builderprojcet) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("builderprojcet", builderprojcet);
		arg.put("builderprojcetListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BuilderProjcet/BuilderProjcetDialog.zul", null,
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

	public void setBuilderProjcetService(BuilderProjcetService builderProjcetService) {
		this.builderProjcetService = builderProjcetService;
	}
}