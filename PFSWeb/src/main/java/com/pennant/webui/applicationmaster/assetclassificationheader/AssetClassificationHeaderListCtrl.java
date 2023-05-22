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
 * * FileName : AssetClassificationHeaderListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020
 * * * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.assetclassificationheader;

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

import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.service.applicationmaster.AssetClassificationHeaderService;
import com.pennant.webui.applicationmaster.assetclassificationheader.model.AssetClassificationHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/AssetClassificationHeader/AssetClassificationHeaderList.zul file.
 * 
 */
public class AssetClassificationHeaderListCtrl extends GFCBaseListCtrl<AssetClassificationHeader> {
	private static final long serialVersionUID = 1L;

	protected Window window_AssetClassificationHeaderList;
	protected Borderlayout borderLayout_AssetClassificationHeaderList;
	protected Paging pagingAssetClassificationHeaderList;
	protected Listbox listBoxAssetClassificationHeader;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_StageOrder;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_AssetClassificationHeaderList_NewAssetClassificationHeader;
	protected Button button_AssetClassificationHeaderList_AssetClassificationHeaderSearch;

	// Search Fields
	protected Textbox code; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Active;

	private transient AssetClassificationHeaderService assetClassificationHeaderService;

	/**
	 * default constructor.<br>
	 */
	public AssetClassificationHeaderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssetClassificationHeader";
		super.pageRightName = "AssetClassificationHeaderList";
		super.tableName = "ASSET_CLSSFICATN_HEADER_AView";
		super.queueTableName = "ASSET_CLSSFICATN_HEADER_View";
		super.enquiryTableName = "ASSET_CLSSFICATN_HEADER_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssetClassificationHeaderList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AssetClassificationHeaderList, borderLayout_AssetClassificationHeaderList,
				listBoxAssetClassificationHeader, pagingAssetClassificationHeaderList);
		setItemRender(new AssetClassificationHeaderListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssetClassificationHeaderList_AssetClassificationHeaderSearch);
		registerButton(button_AssetClassificationHeaderList_NewAssetClassificationHeader,
				"button_AssetClassificationHeaderList_NewAssetClassificationHeader", true);

		registerField("id");
		registerField("code", listheader_Code, SortOrder.NONE, code, sortOperator_Code, Operators.STRING);
		registerField("description", listheader_Description);
		registerField("stageOrder", listheader_StageOrder);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AssetClassificationHeaderList_AssetClassificationHeaderSearch(Event event) {
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
	public void onClick$button_AssetClassificationHeaderList_NewAssetClassificationHeader(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		AssetClassificationHeader assetclassificationheader = new AssetClassificationHeader();
		assetclassificationheader.setNewRecord(true);
		assetclassificationheader.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(assetclassificationheader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssetClassificationHeaderItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssetClassificationHeader.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		AssetClassificationHeader assetclassificationheader = assetClassificationHeaderService
				.getAssetClassificationHeader(id);

		if (assetclassificationheader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = ");
		whereCond.append(assetclassificationheader.getId());
		whereCond.append(" AND  version=");
		whereCond.append(assetclassificationheader.getVersion());

		if (doCheckAuthority(assetclassificationheader, whereCond.toString(),
				new Object[] { assetclassificationheader.getId(), assetclassificationheader.getVersion() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assetclassificationheader.getWorkflowId() == 0) {
				assetclassificationheader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assetclassificationheader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param assetclassificationheader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AssetClassificationHeader assetclassificationheader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assetClassificationHeader", assetclassificationheader);
		arg.put("assetClassificationHeaderListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/AssetClassificationHeader/AssetClassificationHeaderDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
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

	public void setAssetClassificationHeaderService(AssetClassificationHeaderService assetClassificationHeaderService) {
		this.assetClassificationHeaderService = assetClassificationHeaderService;
	}
}