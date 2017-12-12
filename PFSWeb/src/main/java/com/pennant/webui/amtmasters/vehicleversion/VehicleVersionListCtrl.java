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
 * FileName    		:  VehicleVersionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.amtmasters.vehicleversion;

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

import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.service.amtmasters.VehicleVersionService;
import com.pennant.webui.amtmasters.vehicleversion.model.VehicleVersionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMasters/VehicleVersion/VehicleVersionList.zul file.
 */
public class VehicleVersionListCtrl extends GFCBaseListCtrl<VehicleVersion> {
	private static final long serialVersionUID = 8007121399442577547L;
	private static final Logger logger = Logger.getLogger(VehicleVersionListCtrl.class);

	protected Window window_VehicleVersionList;
	protected Borderlayout borderLayout_VehicleVersionList;
	protected Paging pagingVehicleVersionList;
	protected Listbox listBoxVehicleVersion;

	protected Listheader listheader_VehicleModelId;
	protected Listheader listheader_VehicleVersionCode;

	protected Button button_VehicleVersionList_NewVehicleVersion;
	protected Button button_VehicleVersionList_VehicleVersionSearchDialog;

	protected Textbox vehicleModel;
	protected Textbox vehicleVersionCode;

	protected Listbox sortOperator_vehicleModel;
	protected Listbox sortOperator_vehicleVersionCode;

	private transient VehicleVersionService vehicleVersionService;

	/**
	 * default constructor.<br>
	 */
	public VehicleVersionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VehicleVersion";
		super.pageRightName = "VehicleVersionList";
		super.tableName = "AMTVehicleVersion_AView";
		super.queueTableName = "AMTVehicleVersion_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_VehicleVersionList(Event event) {
		// Set the page level components.
		setPageComponents(window_VehicleVersionList, borderLayout_VehicleVersionList, listBoxVehicleVersion,
				pagingVehicleVersionList);
		setItemRender(new VehicleVersionListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_VehicleVersionList_NewVehicleVersion, "button_VehicleVersionList_NewVehicleVersion", true);
		registerButton(button_VehicleVersionList_VehicleVersionSearchDialog);

		registerField("vehicleVersionId");
		registerField("vehicleModelId");
		registerField("lovDescVehicleModelDesc", listheader_VehicleModelId, SortOrder.ASC, vehicleModel,
				sortOperator_vehicleModel, Operators.STRING);
		registerField("vehicleVersionCode", listheader_VehicleVersionCode, SortOrder.NONE, vehicleVersionCode,
				sortOperator_vehicleVersionCode, Operators.STRING);

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
	public void onClick$button_VehicleVersionList_VehicleVersionSearchDialog(Event event) {
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
	public void onClick$button_VehicleVersionList_NewVehicleVersion(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		VehicleVersion vehicleVersion = new VehicleVersion();
		vehicleVersion.setNewRecord(true);
		vehicleVersion.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(vehicleVersion);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onVehicleVersionItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxVehicleVersion.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		VehicleVersion vehicleVersion = vehicleVersionService.getVehicleVersionById(id);

		if (vehicleVersion == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND VehicleVersionId=" + vehicleVersion.getVehicleVersionId() + " AND version="
				+ vehicleVersion.getVersion() + " ";

		if (doCheckAuthority(vehicleVersion, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vehicleVersion.getWorkflowId() == 0) {
				vehicleVersion.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vehicleVersion);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aVehicleVersion
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VehicleVersion aVehicleVersion) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vehicleVersion", aVehicleVersion);
		arg.put("vehicleVersionListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/VehicleVersion/VehicleVersionDialog.zul", null, arg);
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

	public void setVehicleVersionService(VehicleVersionService vehicleVersionService) {
		this.vehicleVersionService = vehicleVersionService;
	}

}