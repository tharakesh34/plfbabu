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
package com.pennant.webui.systemmasters.district;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.model.systemmasters.District;
import com.pennant.backend.service.systemmasters.DistrictService;
import com.pennant.webui.systemmasters.district.model.DistrictListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/District/DistrictList.zul file.
 */
public class DistrictListCtrl extends GFCBaseListCtrl<District> {
	private static final long serialVersionUID = -2437455376763752382L;
	private static final Logger logger = LogManager.getLogger(DistrictListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DistrictList;
	protected Borderlayout borderLayout_DistrictList;
	protected Paging pagingDistrictList;
	protected Listbox listBoxDistrict;

	protected Textbox districtCode;
	protected Textbox districtName;

	protected Listbox sortOperator_districtCode;
	protected Listbox sortOperator_districtName;
	protected Listbox sortOperator_hostReferenceNo;
	protected Listbox sortOperator_districtIsActive;

	// List headers
	protected Listheader listheader_DistrictCode;
	protected Listheader listheader_DistrictName;
	protected Listheader listheader_HostReferenceNo;
	protected Listheader listheader_DistrictIsActive;

	// checkRights
	protected Button button_DistrictList_NewDistrict;
	protected Button button_DistrictList_DistrictSearchDialog;

	private transient DistrictService districtService;

	/**
	 * default constructor.<br>
	 */
	public DistrictListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "District";
		super.pageRightName = "DistrictList";
		super.tableName = "RMTDistricts_AView";
		super.queueTableName = "RMTDistricts_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DistrictList(Event event) {
		// Set the page level components.
		setPageComponents(window_DistrictList, borderLayout_DistrictList, listBoxDistrict, pagingDistrictList);
		setItemRender(new DistrictListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DistrictList_NewDistrict, "button_DistrictList_NewDistrict", true);
		registerButton(button_DistrictList_DistrictSearchDialog);

		registerField("id");
		registerField("code", listheader_DistrictCode, SortOrder.ASC, districtCode, sortOperator_districtCode,
				Operators.STRING);
		registerField("name", listheader_DistrictName, SortOrder.NONE, districtName, sortOperator_districtName,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_DistrictList_DistrictSearchDialog(Event event) {
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
	public void onClick$button_DistrictList_NewDistrict(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		District district = new District();
		district.setNewRecord(true);
		district.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(district);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onDistrictItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDistrict.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected district.
		long id = ((long) selectedItem.getAttribute("id"));
		District district = districtService.getDistrictById(id, "_View");

		if (district == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where Code = ?";

		if (doCheckAuthority(district, whereCond, new Object[] { district.getCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && district.getWorkflowId() == 0) {
				district.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(district);
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
	private void doShowDialogPage(District district) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("district", district);
		arg.put("districtListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/District/DistrictDialog.zul", null, arg);
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

	public void setDistrictService(DistrictService districtService) {
		this.districtService = districtService;
	}
}