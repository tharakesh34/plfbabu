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
 * FileName    		:  SubSectorListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.subsector;

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

import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.systemmasters.SubSectorService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.subsector.model.SubSectorListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/SubSector/SubSectorList.zul file.
 */
public class SubSectorListCtrl extends GFCBaseListCtrl<SubSector> {
	private static final long serialVersionUID = -244988667564615833L;
	private static final Logger logger = Logger.getLogger(SubSectorListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SubSectorList;
	protected Borderlayout borderLayout_SubSectorList;
	protected Paging pagingSubSectorList;
	protected Listbox listBoxSubSector;

	// List headers
	protected Listheader listheader_SectorCode;
	protected Listheader listheader_SubSectorCode;
	protected Listheader listheader_SubSectorDesc;
	protected Listheader listheader_SubSectorIsActive;

	protected Textbox sectorCode;
	protected Textbox subSectorCode;
	protected Textbox subSectorDesc;
	protected Checkbox subSectorIsActive;

	protected Listbox sortOperator_sectorCode;
	protected Listbox sortOperator_subSectorCode;
	protected Listbox sortOperator_subSectorDesc;
	protected Listbox sortOperator_subSectorIsActive;

	// checkRights
	protected Button button_SubSectorList_NewSubSector;
	protected Button button_SubSectorList_SubSectorSearchDialog;

	private transient SubSectorService subSectorService;

	/**
	 * default constructor.<br>
	 */
	public SubSectorListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SubSector";
		super.pageRightName = "SubSectorList";
		super.tableName = "BMTSubSectors_AView";
		super.queueTableName = "BMTSubSectors_View";
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
	public void onCreate$window_SubSectorList(Event event) {
		// Set the page level components.
		setPageComponents(window_SubSectorList, borderLayout_SubSectorList, listBoxSubSector, pagingSubSectorList);
		setItemRender(new SubSectorListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SubSectorList_NewSubSector, "button_SubSectorList_NewSubSector", true);
		registerButton(button_SubSectorList_SubSectorSearchDialog);

		registerField("sectorCode", listheader_SectorCode, SortOrder.ASC, sectorCode, sortOperator_sectorCode,
				Operators.STRING);
		registerField("subSectorCode", listheader_SubSectorCode, SortOrder.NONE, subSectorCode,
				sortOperator_subSectorCode, Operators.STRING);
		registerField("subSectorDesc", listheader_SubSectorDesc, SortOrder.NONE, subSectorDesc,
				sortOperator_subSectorDesc, Operators.STRING);
		registerField("subSectorIsActive", listheader_SubSectorIsActive, SortOrder.NONE, subSectorIsActive,
				sortOperator_subSectorIsActive, Operators.BOOLEAN);

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
	public void onClick$button_SubSectorList_SubSectorSearchDialog(Event event) {
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
	public void onClick$button_SubSectorList_NewSubSector(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SubSector subSector = new SubSector();
		subSector.setNewRecord(true);
		subSector.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(subSector);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSubSectorItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSubSector.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String subSectorCode = (String) selectedItem.getAttribute("subSectorCode");

		SubSector subSector = subSectorService.getSubSectorById(id, subSectorCode);

		if (subSector == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SectorCode='" + subSector.getSectorCode() + "' AND version=" + subSector.getVersion()
				+ " ";

		if (doCheckAuthority(subSector, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && subSector.getWorkflowId() == 0) {
				subSector.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(subSector);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param subSector
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SubSector subSector) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("subSector", subSector);
		arg.put("subSectorListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/SubSector/SubSectorDialog.zul", null, arg);
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

	public void setSubSectorService(SubSectorService subSectorService) {
		this.subSectorService = subSectorService;
	}
}