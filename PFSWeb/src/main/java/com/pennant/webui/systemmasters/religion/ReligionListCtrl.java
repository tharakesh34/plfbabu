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
 * * FileName : ReligionListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-01-2018 * * Modified
 * Date : 24-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.religion;

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

import com.pennant.backend.model.systemmasters.Religion;
import com.pennant.backend.service.systemmasters.ReligionService;
import com.pennant.webui.systemmasters.religion.model.ReligionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Religion/ReligionList.zul file.
 * 
 */
public class ReligionListCtrl extends GFCBaseListCtrl<Religion> {
	private static final long serialVersionUID = 1L;

	protected Window window_ReligionList;
	protected Borderlayout borderLayout_ReligionList;
	protected Paging pagingReligionList;
	protected Listbox listBoxReligion;

	// List headers
	protected Listheader listheader_ReligionCode;
	protected Listheader listheader_ReligionDesc;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_ReligionList_NewReligion;
	protected Button button_ReligionList_ReligionSearch;

	// Search Fields
	protected Textbox religionCode; // autowired
	protected Textbox religionDesc; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_ReligionCode;
	protected Listbox sortOperator_ReligionDesc;
	protected Listbox sortOperator_Active;

	private transient ReligionService religionService;

	/**
	 * default constructor.<br>
	 */
	public ReligionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Religion";
		super.pageRightName = "ReligionList";
		super.tableName = "Religion_AView";
		super.queueTableName = "Religion_View";
		super.enquiryTableName = "Religion_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReligionList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ReligionList, borderLayout_ReligionList, listBoxReligion, pagingReligionList);
		setItemRender(new ReligionListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ReligionList_ReligionSearch);
		registerButton(button_ReligionList_NewReligion, "button_ReligionList_NewReligion", true);

		registerField("religionId");
		registerField("religionCode", listheader_ReligionCode, SortOrder.NONE, religionCode, sortOperator_ReligionCode,
				Operators.STRING);
		registerField("religionDesc", listheader_ReligionDesc, SortOrder.NONE, religionDesc, sortOperator_ReligionDesc,
				Operators.STRING);
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
	public void onClick$button_ReligionList_ReligionSearch(Event event) {
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
	public void onClick$button_ReligionList_NewReligion(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		Religion religion = new Religion();
		religion.setNewRecord(true);
		religion.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(religion);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onReligionItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReligion.getSelectedItem();
		final long religionId = (long) selectedItem.getAttribute("religionId");
		Religion religion = religionService.getReligion(religionId);

		if (religion == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  ReligionId =?");

		if (doCheckAuthority(religion, whereCond.toString(), new Object[] { religion.getReligionId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && religion.getWorkflowId() == 0) {
				religion.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(religion);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param religion The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Religion religion) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("religion", religion);
		arg.put("religionListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Religion/ReligionDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
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

	public void setReligionService(ReligionService religionService) {
		this.religionService = religionService;
	}
}