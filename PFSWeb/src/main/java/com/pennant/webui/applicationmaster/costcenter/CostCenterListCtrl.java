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
 * * FileName : CostCenterListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified
 * Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.costcenter;

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

import com.pennant.backend.model.applicationmaster.CostCenter;
import com.pennant.backend.service.applicationmaster.CostCenterService;
import com.pennant.webui.applicationmaster.costcenter.model.CostCenterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/CostCenter/CostCenterList.zul file.
 * 
 */
public class CostCenterListCtrl extends GFCBaseListCtrl<CostCenter> {
	private static final long serialVersionUID = 1L;

	protected Window window_CostCenterList;
	protected Borderlayout borderLayout_CostCenterList;
	protected Paging pagingCostCenterList;
	protected Listbox listBoxCostCenter;

	// List headers
	protected Listheader listheader_CostCenterCode;
	protected Listheader listheader_CostCenterDesc;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_CostCenterList_NewCostCenter;
	protected Button button_CostCenterList_CostCenterSearch;

	// Search Fields
	protected Textbox costCenterCode; // autowired
	protected Textbox costCenterDesc; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_CostCenterCode;
	protected Listbox sortOperator_CostCenterDesc;
	protected Listbox sortOperator_Active;

	private transient CostCenterService costCenterService;

	/**
	 * default constructor.<br>
	 */
	public CostCenterListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CostCenter";
		super.pageRightName = "CostCenterList";
		super.tableName = "CostCenters_AView";
		super.queueTableName = "CostCenters_View";
		super.enquiryTableName = "CostCenters_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CostCenterList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_CostCenterList, borderLayout_CostCenterList, listBoxCostCenter, pagingCostCenterList);
		setItemRender(new CostCenterListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CostCenterList_CostCenterSearch);
		registerButton(button_CostCenterList_NewCostCenter, "button_CostCenterList_NewCostCenter", true);

		registerField("costCenterID");
		registerField("costCenterCode", listheader_CostCenterCode, SortOrder.NONE, costCenterCode,
				sortOperator_CostCenterCode, Operators.STRING);
		registerField("costCenterDesc", listheader_CostCenterDesc, SortOrder.NONE, costCenterDesc,
				sortOperator_CostCenterDesc, Operators.STRING);
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
	public void onClick$button_CostCenterList_CostCenterSearch(Event event) {
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
	public void onClick$button_CostCenterList_NewCostCenter(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		CostCenter costcenter = new CostCenter();
		costcenter.setNewRecord(true);
		costcenter.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(costcenter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onCostCenterItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCostCenter.getSelectedItem();
		final long costCenterID = (long) selectedItem.getAttribute("costCenterID");
		CostCenter costcenter = costCenterService.getCostCenter(costCenterID);

		if (costcenter == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  CostCenterID =?");

		if (doCheckAuthority(costcenter, whereCond.toString(), new Object[] { costcenter.getCostCenterID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && costcenter.getWorkflowId() == 0) {
				costcenter.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(costcenter);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param costcenter The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CostCenter costcenter) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("costCenter", costcenter);
		arg.put("costCenterListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CostCenter/CostCenterDialog.zul", null, arg);
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

	public void setCostCenterService(CostCenterService costCenterService) {
		this.costCenterService = costCenterService;
	}
}