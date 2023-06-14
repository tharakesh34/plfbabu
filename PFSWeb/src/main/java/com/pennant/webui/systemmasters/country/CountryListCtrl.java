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
 * * FileName : CountryListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified Date
 * : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.country;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.service.systemmasters.CountryService;
import com.pennant.webui.systemmasters.country.model.CountryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Country/CountryList.zul file.
 */
public class CountryListCtrl extends GFCBaseListCtrl<Country> {
	private static final long serialVersionUID = -2437455376763752382L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CountryList;
	protected Borderlayout borderLayout_CountryList;
	protected Paging pagingCountryList;
	protected Listbox listBoxCountry;

	protected Textbox countryCode;
	protected Textbox countryDesc;
	protected Decimalbox countryParentLimit;
	protected Decimalbox countryResidenceLimit;
	protected Decimalbox countryRiskLimit;
	protected Checkbox countryIsActive;

	protected Listbox sortOperator_countryDesc;
	protected Listbox sortOperator_countryCode;
	protected Listbox sortOperator_countryParentLimit;
	protected Listbox sortOperator_countryResidenceLimit;
	protected Listbox sortOperator_countryRiskLimit;
	protected Listbox sortOperator_countryIsActive;

	// List headers
	protected Listheader listheader_CountryCode;
	protected Listheader listheader_CountryDesc;
	protected Listheader listheader_CountryParentLimit;
	protected Listheader listheader_CountryResidenceLimit;
	protected Listheader listheader_CountryRiskLimit;
	protected Listheader listheader_CountryIsActive;

	// checkRights
	protected Button button_CountryList_NewCountry;
	protected Button button_CountryList_CountrySearchDialog;

	private transient CountryService countryService;

	/**
	 * default constructor.<br>
	 */
	public CountryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Country";
		super.pageRightName = "CountryList";
		super.tableName = "BMTCountries_AView";
		super.queueTableName = "BMTCountries_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CountryList(Event event) {
		// Set the page level components.
		setPageComponents(window_CountryList, borderLayout_CountryList, listBoxCountry, pagingCountryList);
		setItemRender(new CountryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CountryList_NewCountry, "button_CountryList_NewCountry", true);
		registerButton(button_CountryList_CountrySearchDialog);

		registerField("countryCode", listheader_CountryCode, SortOrder.ASC, countryCode, sortOperator_countryCode,
				Operators.STRING);
		registerField("countryDesc", listheader_CountryDesc, SortOrder.NONE, countryDesc, sortOperator_countryDesc,
				Operators.STRING);
		registerField("countryParentLimit", listheader_CountryParentLimit, SortOrder.NONE, countryParentLimit,
				sortOperator_countryParentLimit, Operators.NUMERIC);
		registerField("countryResidenceLimit", listheader_CountryResidenceLimit, SortOrder.NONE, countryResidenceLimit,
				sortOperator_countryResidenceLimit, Operators.NUMERIC);
		registerField("countryRiskLimit", listheader_CountryRiskLimit, SortOrder.NONE, countryRiskLimit,
				sortOperator_countryRiskLimit, Operators.NUMERIC);
		registerField("countryIsActive", listheader_CountryIsActive, SortOrder.NONE, countryIsActive,
				sortOperator_countryIsActive, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CountryList_CountrySearchDialog(Event event) {
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
	public void onClick$button_CountryList_NewCountry(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Country country = new Country();
		country.setNewRecord(true);
		country.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(country);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCountryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCountry.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Country country = countryService.getCountryById(id);

		if (country == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where CountryCode=?";
		if (doCheckAuthority(country, whereCond, new Object[] { country.getCountryCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && country.getWorkflowId() == 0) {
				country.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(country);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param country The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Country country) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("country", country);
		arg.put("countryListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Country/CountryDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}
}