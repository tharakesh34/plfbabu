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
 * * FileName : GSTRateListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-05-2019 * * Modified Date
 * : 20-05-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-05-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.gstrate;

import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennant.backend.service.rmtmasters.GSTRateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.rmtmasters.gstrate.model.GSTRateListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/RMTMasters/GSTRate/GSTRateList.zul file.
 * 
 */
public class GSTRateListCtrl extends GFCBaseListCtrl<GSTRate> {
	private static final long serialVersionUID = 1L;

	protected Window window_GSTRateList;
	protected Borderlayout borderLayout_GSTRateList;
	protected Paging pagingGSTRateList;
	protected Listbox listBoxGSTRate;

	// List headers
	protected Listheader listheader_FromState;
	protected Listheader listheader_ToState;
	protected Listheader listheader_TaxType;
	protected Listheader listheader_Amount;
	protected Listheader listheader_Percentage;
	protected Listheader listheader_CalcOn;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_GSTRateList_NewGSTRate;
	protected Button button_GSTRateList_GSTRateSearch;

	// Search Fields
	protected Textbox fromState;
	protected Textbox toState;
	protected Combobox taxType;
	protected Combobox calcOn;
	protected Checkbox active;

	protected Listbox sortOperator_FromState;
	protected Listbox sortOperator_ToState;
	protected Listbox sortOperator_TaxType;
	protected Listbox sortOperator_CalcOn;
	protected Listbox sortOperator_Active;

	private transient GSTRateService gstRateService;

	private List<Property> listTaxType = PennantAppUtil.getTaxtTypeList();
	private List<ValueLabel> listCalcOn = PennantStaticListUtil.getCalcOnList();

	/**
	 * default constructor.<br>
	 */
	public GSTRateListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "GSTRate";
		super.pageRightName = "GSTRateList";
		super.tableName = "GST_RATES_AView";
		super.queueTableName = "GST_RATES_View";
		super.enquiryTableName = "GST_RATES_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_GSTRateList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_GSTRateList, borderLayout_GSTRateList, listBoxGSTRate, pagingGSTRateList);
		setItemRender(new GSTRateListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_GSTRateList_GSTRateSearch);
		registerButton(button_GSTRateList_NewGSTRate, "button_GSTRateList_NewGSTRate", true);

		registerField("id");
		registerField("fromState", listheader_FromState, SortOrder.NONE, fromState, sortOperator_FromState,
				Operators.STRING);
		registerField("fromStateName");
		registerField("toState", listheader_ToState, SortOrder.NONE, toState, sortOperator_ToState, Operators.STRING);
		registerField("toStateName");
		registerField("taxType", listheader_TaxType, SortOrder.NONE, taxType, sortOperator_TaxType, Operators.STRING);
		registerField("amount", listheader_Amount);
		registerField("percentage", listheader_Percentage);
		registerField("calcOn", listheader_CalcOn, SortOrder.NONE, calcOn, sortOperator_CalcOn, Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		fillList(this.taxType, listTaxType, PennantConstants.List_Select);
		fillComboBox(this.calcOn, PennantConstants.List_Select, listCalcOn, "");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_GSTRateList_GSTRateSearch(Event event) {
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
	public void onClick$button_GSTRateList_NewGSTRate(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		GSTRate gstrate = new GSTRate();
		gstrate.setNewRecord(true);
		gstrate.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(gstrate);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onGSTRateItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxGSTRate.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		GSTRate gstrate = gstRateService.getGSTRate(id);

		if (gstrate == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(gstrate, whereCond.toString(), new Object[] { gstrate.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && gstrate.getWorkflowId() == 0) {
				gstrate.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(gstrate);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param gstrate The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(GSTRate gstrate) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("gSTRate", gstrate);
		arg.put("gSTRateListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/RMTMasters/GSTRate/GSTRateDialog.zul", null, arg);
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

	public void setGstRateService(GSTRateService gSTRateService) {
		this.gstRateService = gSTRateService;
	}

	public GSTRateService getGstRateService() {
		return gstRateService;
	}
}