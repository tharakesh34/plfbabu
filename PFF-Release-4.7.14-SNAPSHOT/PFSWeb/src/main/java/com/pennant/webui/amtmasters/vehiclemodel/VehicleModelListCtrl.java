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
 * FileName    		:  VehicleModelListCtrl.java                                                   * 	  
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
package com.pennant.webui.amtmasters.vehiclemodel;

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

import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.service.amtmasters.VehicleModelService;
import com.pennant.webui.amtmasters.vehiclemodel.model.VehicleModelListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMaster/VehicleModel/VehicleModelList.zul file.
 */
public class VehicleModelListCtrl extends GFCBaseListCtrl<VehicleModel> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(VehicleModelListCtrl.class);

	protected Window window_VehicleModelList;
	protected Borderlayout borderLayout_VehicleModelList;
	protected Paging pagingVehicleModelList;
	protected Listbox listBoxVehicleModel;

	protected Listheader listheader_VehicleManufacterId;
	protected Listheader listheader_VehicleModelDesc;

	protected Button button_VehicleModelList_NewVehicleModel;
	protected Button button_VehicleModelList_VehicleModelSearchDialog;

	protected Textbox vehicleModelId;
	protected Textbox vehicleModelDesc;

	protected Listbox sortOperator_vehicleModelId;
	protected Listbox sortOperator_vehicleModelDesc;

	private transient VehicleModelService vehicleModelService;

	/**
	 * default constructor.<br>
	 */
	public VehicleModelListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VehicleModel";
		super.pageRightName = "VehicleModelList";
		super.tableName = "AMTVehicleModel_AView";
		super.queueTableName = "AMTVehicleModel_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_VehicleModelList(Event event) {
		// Set the page level components.
		setPageComponents(window_VehicleModelList, borderLayout_VehicleModelList, listBoxVehicleModel,
				pagingVehicleModelList);
		setItemRender(new VehicleModelListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_VehicleModelList_NewVehicleModel, "button_VehicleModelList_NewVehicleModel", true);
		registerButton(button_VehicleModelList_VehicleModelSearchDialog);

		registerField("vehicleModelId", vehicleModelId, SortOrder.NONE, sortOperator_vehicleModelId, Operators.STRING);
		registerField("VehicleManufacturerId", listheader_VehicleManufacterId, SortOrder.NONE);
		registerField("vehicleModelDesc", listheader_VehicleModelDesc, SortOrder.NONE, vehicleModelDesc,
				sortOperator_vehicleModelDesc, Operators.STRING);
		registerField("lovDescVehicleManufacturerName", SortOrder.ASC);

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
	public void onClick$button_VehicleModelList_VehicleModelSearchDialog(Event event) {
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
	public void onClick$button_VehicleModelList_NewVehicleModel(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		VehicleModel vehicleModel = new VehicleModel();
		vehicleModel.setNewRecord(true);
		vehicleModel.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(vehicleModel);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onVehicleModelItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxVehicleModel.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		long vehicleManufacturerId = (long) selectedItem.getAttribute("vehicleManufacturerId");
		VehicleModel vehicleModel = vehicleModelService.getVehicleModelById(id, vehicleManufacturerId);

		if (vehicleModel == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND VehicleModelId=" + vehicleModel.getVehicleModelId() + " AND version="
				+ vehicleModel.getVersion() + " ";

		if (doCheckAuthority(vehicleModel, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vehicleModel.getWorkflowId() == 0) {
				vehicleModel.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vehicleModel);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aVehicleModel
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VehicleModel aVehicleModel) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vehicleModel", aVehicleModel);
		arg.put("vehicleModelListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/VehicleModel/VehicleModelDialog.zul", null, arg);
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

	public void setVehicleModelService(VehicleModelService vehicleModelService) {
		this.vehicleModelService = vehicleModelService;
	}
}