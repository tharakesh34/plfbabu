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
 * FileName    		:  ProvinceListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.province;

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

import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.webui.systemmasters.province.model.ProvinceListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Province/ProvinceList.zul file.
 */
public class ProvinceListCtrl extends GFCBaseListCtrl<Province> {
	private static final long serialVersionUID = -3109779707000635809L;
	private static final Logger logger = Logger.getLogger(ProvinceListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProvinceList;
	protected Borderlayout borderLayout_ProvinceList;
	protected Paging pagingProvinceList;
	protected Listbox listBoxProvince;

	protected Textbox cPCountry;
	protected Textbox cPCountryDescription;
	protected Textbox cPProvince;
	protected Textbox cPProvinceName;

	protected Listbox sortOperator_cPCountryDescription;
	protected Listbox sortOperator_cPCountry;
	protected Listbox sortOperator_cPProvince;
	protected Listbox sortOperator_cPProvinceName;

	// List headers
	protected Listheader listheader_CPCountry;
	protected Listheader listheader_CPCountryDescription;
	protected Listheader listheader_CPProvince;
	protected Listheader listheader_CPProvinceName;

	// checkRights
	protected Button button_ProvinceList_NewProvince;
	protected Button button_ProvinceList_ProvinceSearchDialog;

	private transient ProvinceService provinceService;

	/**
	 * default constructor.<br>
	 */
	public ProvinceListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Province";
		super.pageRightName = "ProvinceList";
		super.tableName = "RMTCountryVsProvince_AView";
		super.queueTableName = "RMTCountryVsProvince_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ProvinceList(Event event) {
		// Set the page level components.
		setPageComponents(window_ProvinceList, borderLayout_ProvinceList, listBoxProvince, pagingProvinceList);
		setItemRender(new ProvinceListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ProvinceList_NewProvince, "button_ProvinceList_NewProvince", true);
		registerButton(button_ProvinceList_ProvinceSearchDialog);

		registerField("cPCountry", listheader_CPCountry, SortOrder.ASC, cPCountry, sortOperator_cPCountry,
				Operators.STRING);
		registerField("lovDescCPCountryName", listheader_CPCountryDescription, SortOrder.NONE, cPCountryDescription,
				sortOperator_cPCountryDescription, Operators.STRING);
		registerField("cPProvince", listheader_CPProvince, SortOrder.NONE, cPProvince, sortOperator_cPProvince,
				Operators.STRING);
		registerField("cPProvinceName", listheader_CPProvinceName, SortOrder.NONE, cPProvinceName,
				sortOperator_cPProvinceName, Operators.STRING);

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
	public void onClick$button_ProvinceList_ProvinceSearchDialog(Event event) {
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
	public void onClick$button_ProvinceList_NewProvince(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Province province = new Province();
		province.setNewRecord(true);
		province.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(province);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onProvinceItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxProvince.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String cpCountry = ((String) selectedItem.getAttribute("cpCountry"));
		String cpProvince = ((String) selectedItem.getAttribute("cpProvince"));
		Province province = provinceService.getProvinceById(cpCountry, cpProvince);

		if (province == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CPCountry='" + province.getCPCountry() + "'" + "AND CPProvince='"
				+ province.getCPProvince() + "'AND version=" + province.getVersion();

		if (doCheckAuthority(province, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && province.getWorkflowId() == 0) {
				province.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(province);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param province
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Province province) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("province", province);
		arg.put("provinceListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Province/ProvinceDialog.zul", null, arg);
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
	public void onClick$print(Event event) throws InterruptedException {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) throws InterruptedException {
		doShowHelp(event);
	}

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}
}