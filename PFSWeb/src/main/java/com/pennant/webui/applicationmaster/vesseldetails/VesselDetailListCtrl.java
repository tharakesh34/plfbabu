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
 * FileName    		:  VesselDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-05-2015    														*
 *                                                                  						*
 * Modified Date    :  12-05-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-05-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.vesseldetails;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.applicationmaster.VesselDetail;
import com.pennant.backend.service.applicationmaster.VesselDetailService;
import com.pennant.webui.applicationmaster.vesseldetails.model.VesselDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/LMTMasters/VesselDetail/VesselDetailList.zul file.
 */
public class VesselDetailListCtrl extends GFCBaseListCtrl<VesselDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(VesselDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VesselDetailList;
	protected Borderlayout borderLayout_VesselDetailList;
	protected Paging pagingVesselDetailList;
	protected Listbox listBoxVesselDetail;

	protected Listheader listheader_VesselTypeID;
	protected Listheader listheader_VesselType;
	protected Listheader listheader_VesselSubType;
	protected Listheader listheader_IsActive;

	protected Button button_VesselDetailList_NewVesselDetail;
	protected Button button_VesselDetailList_VesselDetailSearch;

	protected Textbox vesselTypeID;
	protected Textbox vesselType;
	protected Textbox vesselSubType;
	protected Checkbox active;

	protected Listbox sortOperator_VesselTypeID;
	protected Listbox sortOperator_VesselType;
	protected Listbox sortOperator_VesselSubType;
	protected Listbox sortOperator_IsActive;

	private transient VesselDetailService vesselDetailService;

	/**
	 * default constructor.<br>
	 */
	public VesselDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VesselDetail";
		super.pageRightName = "VesselList";
		super.tableName = "VesselDetails_AView";
		super.queueTableName = "VesselDetails_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_VesselDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_VesselDetailList, borderLayout_VesselDetailList, listBoxVesselDetail,
				pagingVesselDetailList);
		setItemRender(new VesselDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_VesselDetailList_NewVesselDetail, "button_VesselList_NewVesselList", true);
		registerButton(button_VesselDetailList_VesselDetailSearch);

		registerField("vesselTypeID", listheader_VesselTypeID, SortOrder.ASC, vesselTypeID, sortOperator_VesselTypeID,
				Operators.STRING);
		registerField("vesselTypeName", listheader_VesselType, SortOrder.NONE, vesselType, sortOperator_VesselType,
				Operators.STRING);
		registerField("vesselSubType", listheader_VesselSubType, SortOrder.NONE, vesselSubType,
				sortOperator_VesselSubType, Operators.STRING);
		registerField("active", listheader_IsActive, SortOrder.NONE, active, sortOperator_IsActive, Operators.BOOLEAN);

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
	public void onClick$button_VesselDetailList_VesselDetailSearch(Event event) {
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
	public void onClick$button_VesselDetailList_NewVesselDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		VesselDetail vesselDetail = new VesselDetail();
		vesselDetail.setNewRecord(true);
		vesselDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(vesselDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onVesselDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxVesselDetail.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		VesselDetail vesselDetail = vesselDetailService.getVesselDetailById(id);

		if (vesselDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND VesselTypeID='" + vesselDetail.getVesselTypeID() + "'  AND version="
				+ vesselDetail.getVersion() + " ";

		if (doCheckAuthority(vesselDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vesselDetail.getWorkflowId() == 0) {
				vesselDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vesselDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aVesselDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VesselDetail aVesselDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vesselDetail", aVesselDetail);
		arg.put("vesselDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/VesselDetails/VesselDetailDialog.zul", null,
					arg);
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

	public void setVesselDetailService(VesselDetailService vesselDetailService) {
		this.vesselDetailService = vesselDetailService;
	}

	public VesselDetailService getVesselDetailService() {
		return this.vesselDetailService;
	}

}