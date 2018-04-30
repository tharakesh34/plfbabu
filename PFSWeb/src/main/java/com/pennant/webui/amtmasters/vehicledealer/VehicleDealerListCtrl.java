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
 * FileName    		:  VehicleDealerListCtrl.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.amtmasters.vehicledealer;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.amtmasters.vehicledealer.model.VehicleDealerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/AMTMaster/VehicleDealer/VehicleDealerList.zul file.
 */
public class VehicleDealerListCtrl extends GFCBaseListCtrl<VehicleDealer> {
	private static final long serialVersionUID = 259921702952389829L;
	private static final Logger logger = Logger.getLogger(VehicleDealerListCtrl.class);

	protected Window window_VehicleDealerList;
	protected Borderlayout borderLayout_VehicleDealerList;
	protected Paging pagingVehicleDealerList;
	protected Listbox listBoxVehicleDealer;

	protected Listheader listheader_DealerType;
	protected Listheader listheader_DealerName;
	protected Listheader listheader_DealerTelephone;
	protected Listheader listheader_DealerFax;
	protected Listheader listheader_Email;
	protected Listheader listheader_DealerProvince;
	protected Listheader listheader_DealerCity;
	protected Listheader listheader_Active;

	protected Button button_VehicleDealerList_NewVehicleDealer;
	protected Button button_VehicleDealerList_VehicleDealerSearchDialog;

	protected Textbox dealerId;
	protected Textbox dealerName;
	protected Combobox dealerType;
	protected Textbox dealerTelephone;
	protected Textbox email;
	protected Textbox dealerProvince;
	protected Textbox dealerCity;
	protected Checkbox active;

	protected Listbox sortOperator_dealerId;
	protected Listbox sortOperator_dealerName;
	protected Listbox sortOperator_dealerType;
	protected Listbox sortOperator_dealerTelephone;
	protected Listbox sortOperator_email;
	protected Listbox sortOperator_dealerProvince;
	protected Listbox sortOperator_dealerCity;
	protected Listbox sortOperator_active;

	private transient VehicleDealerService vehicleDealerService;
	private String module;

	/**
	 * default constructor.<br>
	 */
	public VehicleDealerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VehicleDealer";
		super.pageRightName = "VehicleDealerList";
		super.tableName = "AMTVehicleDealer_AView";
		super.queueTableName = "AMTVehicleDealer_View";
		this.module = getArgument("module");
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("DealerType", this.module, Filter.OP_EQUAL);
		searchObject.addFilterAnd(filters);
	}

 
	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_VehicleDealerList(Event event) {
		// Set the page level components.
		setPageComponents(window_VehicleDealerList, borderLayout_VehicleDealerList, listBoxVehicleDealer,
				pagingVehicleDealerList);
		setItemRender(new VehicleDealerListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_VehicleDealerList_NewVehicleDealer, "button_VehicleDealerList_NewVehicleDealer", true);
		registerButton(button_VehicleDealerList_VehicleDealerSearchDialog);

		fillComboBox(this.dealerType, "", PennantStaticListUtil.getDealerType(), "");

		registerField("dealerId", dealerId, SortOrder.NONE, sortOperator_dealerId, Operators.STRING);
		registerField("dealerType", listheader_DealerType, SortOrder.ASC, dealerType, sortOperator_dealerType,
				Operators.STRING);
		registerField("dealerName", listheader_DealerName, SortOrder.NONE, dealerName, sortOperator_dealerName,
				Operators.STRING);
		registerField("dealerTelephone", listheader_DealerTelephone, SortOrder.NONE, dealerTelephone, sortOperator_dealerTelephone, Operators.STRING);
		registerField("email", listheader_Email, SortOrder.NONE, email, sortOperator_email, Operators.STRING);
		registerField("dealerTelephone", listheader_DealerTelephone, SortOrder.NONE);
		registerField("dealerFax", listheader_DealerFax, SortOrder.NONE);
		registerField("dealerProvince", listheader_DealerProvince, SortOrder.NONE, dealerProvince,
				sortOperator_dealerProvince, Operators.STRING);
		registerField("dealerCity", listheader_DealerCity, SortOrder.NONE, dealerCity, sortOperator_dealerCity,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_active, Operators.BOOLEAN);

		SearchFilterControl.renderOperators(this.sortOperator_dealerTelephone, Operators.STRING);

		doSetFieldProperties();
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_VehicleDealerList_VehicleDealerSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button.
	 * Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_VehicleDealerList_NewVehicleDealer(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		VehicleDealer vehicleDealer = new VehicleDealer();
		vehicleDealer.setNewRecord(true);
		vehicleDealer.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(vehicleDealer);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view
	 * it's details. Show the dialog page with the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onVehicleDealerItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		

		// Get the selected record.
		Listitem selectedItem = this.listBoxVehicleDealer.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		VehicleDealer vehicleDealer = vehicleDealerService.getVehicleDealerById(id);

		if (vehicleDealer == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND DealerId=" + vehicleDealer.getDealerId() + " AND version=" + vehicleDealer.getVersion()
				+ " ";
		if (doCheckAuthority(vehicleDealer, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vehicleDealer.getWorkflowId() == 0) {
				vehicleDealer.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vehicleDealer);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param vehicleDealer
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VehicleDealer vehicleDealer) {
		logger.debug(Literal.ENTERING);

		this.module = getArgument("module");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vehicleDealer", vehicleDealer);
		arg.put("vehicleDealerListCtrl", this);
		arg.put("module", this.module);

		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/VehicleDealer/VehicleDealerDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		
		this.dealerId.setMaxlength(19);
		this.dealerName.setMaxlength(50);
		this.dealerTelephone.setMaxlength(10);
		this.recordStatus.setMaxlength(50);
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button
	 * to print the results.
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

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

}