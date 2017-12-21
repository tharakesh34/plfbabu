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
 * FileName    		:  SectorListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.sector;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.service.systemmasters.SectorService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.sector.model.SectorListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/Sector/SectorList.zul file.
 */
public class SectorListCtrl extends GFCBaseListCtrl<Sector> {
	private static final long serialVersionUID = -4561944744750744817L;
	private static final Logger logger = Logger.getLogger(SectorListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SectorList;
	protected Borderlayout borderLayout_SectorList;
	protected Paging pagingSectorList;
	protected Listbox listBoxSector;

	protected Textbox sectorCode;
	protected Textbox sectorDesc;
	protected Decimalbox sectorLimit;
	protected Checkbox sectorIsActive;

	protected Listbox sortOperator_sectorCode;
	protected Listbox sortOperator_sectorDesc;
	protected Listbox sortOperator_sectorLimit;
	protected Listbox sortOperator_sectorIsActive;

	// List headers
	protected Listheader listheader_SectorCode;
	protected Listheader listheader_SectorDesc;
	protected Listheader listheader_SectorLimit;
	protected Listheader listheader_SectorIsActive;

	// checkRights
	protected Button button_SectorList_NewSector;
	protected Button button_SectorList_SectorSearchDialog;

	private transient SectorService sectorService;

	/**
	 * default constructor.<br>
	 */
	public SectorListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Sector";
		super.pageRightName = "SectorList";
		super.tableName = "BMTSectors_AView";
		super.queueTableName = "BMTSectors_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("sectorCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SectorList(Event event) {
		// Set the page level components.
		setPageComponents(window_SectorList, borderLayout_SectorList, listBoxSector, pagingSectorList);
		setItemRender(new SectorListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SectorList_NewSector, "button_SectorList_NewSector", true);
		registerButton(button_SectorList_SectorSearchDialog);

		registerField("sectorCode", listheader_SectorCode, SortOrder.ASC, sectorCode, sortOperator_sectorCode,
				Operators.STRING);
		registerField("sectorDesc", listheader_SectorDesc, SortOrder.NONE, sectorDesc, sortOperator_sectorDesc,
				Operators.STRING);
		registerField("sectorLimit", listheader_SectorLimit, SortOrder.NONE, sectorLimit, sortOperator_sectorLimit,
				Operators.NUMERIC);
		registerField("sectorIsActive", listheader_SectorIsActive, SortOrder.NONE, sectorIsActive,
				sortOperator_sectorIsActive, Operators.BOOLEAN);

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
	public void onClick$button_SectorList_SectorSearchDialog(Event event) {
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
	public void onClick$button_SectorList_NewSector(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Sector sector = new Sector();
		sector.setNewRecord(true);
		sector.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(sector);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSectorItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSector.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Sector sector = sectorService.getSectorById(id);

		if (sector == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SectorCode='" + sector.getSectorCode() + "' AND version=" + sector.getVersion() + " ";
		if (doCheckAuthority(sector, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && sector.getWorkflowId() == 0) {
				sector.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(sector);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param sector
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Sector sector) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("sector", sector);
		arg.put("sectorListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Sector/SectorDialog.zul", null, arg);
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

	public void setSectorService(SectorService sectorService) {
		this.sectorService = sectorService;
	}
}