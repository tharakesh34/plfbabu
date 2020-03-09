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
 * FileName    		:  CityListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.city;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.service.systemmasters.CityService;
import com.pennant.webui.systemmasters.city.model.CityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/City/CityList.zul file.
 */
public class CityListCtrl extends GFCBaseListCtrl<City> {
	private static final long serialVersionUID = 485796535935527728L;
	private static final Logger logger = Logger.getLogger(CityListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CityList;
	protected Borderlayout borderLayout_CityList;
	protected Paging pagingCityList;
	protected Listbox listBoxCity;

	protected Listbox sortOperator_pCCountry;
	protected Listbox sortOperator_pCProvince;
	protected Listbox sortOperator_pCCity;
	protected Listbox sortOperator_pCCityName;

	protected Textbox pCCountry;
	protected Textbox pCProvince;
	protected Textbox pCCity;
	protected Textbox pCCityName;

	// List headers
	protected Listheader listheader_PCCountry;
	protected Listheader listheader_PCProvince;
	protected Listheader listheader_PCCity;
	protected Listheader listheader_PCCityName;

	// checkRights
	protected Button button_CityList_NewCity;
	protected Button button_CityList_CitySearchDialog;

	private transient CityService cityService;

	/**
	 * default constructor.<br>
	 */
	public CityListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "City";
		super.pageRightName = "CityList";
		super.tableName = "RMTProvinceVsCity_AView";
		super.queueTableName = "RMTProvinceVsCity_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CityList(Event event) {
		// Set the page level components.
		setPageComponents(window_CityList, borderLayout_CityList, listBoxCity, pagingCityList);
		setItemRender(new CityListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CityList_NewCity, "button_CityList_NewCity", true);
		registerButton(button_CityList_CitySearchDialog);

		registerField("lovDescPCCountryName");
		registerField("lovDescPCProvinceName");
		registerField("pCCountry", listheader_PCCountry, SortOrder.ASC, pCCountry, sortOperator_pCCountry,
				Operators.STRING);
		registerField("pCProvince", listheader_PCProvince, SortOrder.NONE, pCProvince, sortOperator_pCProvince,
				Operators.STRING);
		registerField("pCCity", listheader_PCCity, SortOrder.NONE, pCCity, sortOperator_pCCity, Operators.STRING);
		registerField("pCCityName", listheader_PCCityName, SortOrder.NONE, pCCityName, sortOperator_pCCityName,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		
		// rendering the list page data required or not.
		if (renderListOnLoad) {
			search();
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CityList_CitySearchDialog(Event event) {
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
	public void onClick$button_CityList_NewCity(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		City city = new City();
		city.setNewRecord(true);
		city.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(city);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCityItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCity.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		final String pcCountry = (String) selectedItem.getAttribute("pcCountry");
		final String pcProvince = (String) selectedItem.getAttribute("pcProvince");
		final String pcCity = (String) selectedItem.getAttribute("pcCity");

		City city = cityService.getCityById(pcCountry, pcProvince, pcCity);

		if (city == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where PCCountry=? AND PCProvince=? AND PCCity=?";

		if (doCheckAuthority(city, whereCond,
				new Object[] { city.getPCCountry(), city.getPCProvince(), city.getPCCity() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && city.getWorkflowId() == 0) {
				city.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(city);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param city
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(City city) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("city", city);
		arg.put("cityListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/City/CityDialog.zul", null, arg);
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

	public void setCityService(CityService cityService) {
		this.cityService = cityService;
	}
}