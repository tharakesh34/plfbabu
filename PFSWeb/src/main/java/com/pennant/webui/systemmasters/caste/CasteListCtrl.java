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
 * FileName    		:  CasteListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.caste;

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

import com.pennant.backend.model.systemmasters.Caste;
import com.pennant.backend.service.systemmasters.CasteService;
import com.pennant.webui.systemmasters.caste.model.CasteListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Caste/CasteList.zul file.
 */
public class CasteListCtrl extends GFCBaseListCtrl<Caste> {
	private static final long serialVersionUID = 1817958653208633892L;
	private static final Logger logger = Logger.getLogger(CasteListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CasteList;
	protected Borderlayout borderLayout_CasteList;
	protected Paging pagingCasteList;
	protected Listbox listBoxCaste;

	protected Textbox casteCode;
	protected Textbox casteDesc;
	protected Checkbox casteIsActive;

	protected Listbox sortOperator_casteCode;
	protected Listbox sortOperator_casteDesc;
	protected Listbox sortOperator_casteIsActive;

	// List headers
	protected Listheader listheader_CasteCode;
	protected Listheader listheader_CasteDesc;
	protected Listheader listheader_CasteIsActive;

	// checkRights
	protected Button button_CasteList_NewCaste;
	protected Button button_CasteList_CasteSearchDialog;

	private transient CasteService casteService;

	/**
	 * default constructor.<br>
	 */
	public CasteListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Caste";
		super.pageRightName = "CasteList";
		super.tableName = "Caste_AView";
		super.queueTableName = "Caste_View";
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CasteList(Event event) {
		// Set the page level components.
		setPageComponents(window_CasteList, borderLayout_CasteList, listBoxCaste, pagingCasteList);
		setItemRender(new CasteListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CasteList_NewCaste, "button_CasteList_NewCaste", true);
		registerButton(button_CasteList_CasteSearchDialog);

		registerField("casteCode", listheader_CasteCode, SortOrder.ASC, casteCode, sortOperator_casteCode,
				Operators.STRING);
		registerField("casteDesc", listheader_CasteDesc, SortOrder.NONE, casteDesc, sortOperator_casteDesc,
				Operators.STRING);
		registerField("casteIsActive", listheader_CasteIsActive, SortOrder.NONE, casteIsActive,
				sortOperator_casteIsActive, Operators.BOOLEAN);
		registerField("casteId");

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
	public void onClick$button_CasteList_CasteSearchDialog(Event event) {
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
	public void onClick$button_CasteList_NewCaste(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Caste caste = new Caste();
		caste.setNewRecord(true);
		caste.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(caste);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view
	 * it's details. Show the dialog page with the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCasteItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCaste.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		Caste caste = casteService.getCasteById(id);

		if (caste == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CasteCode='" + caste.getCasteCode() + "' AND version=" + caste.getVersion() + " ";

		if (doCheckAuthority(caste, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && caste.getWorkflowId() == 0) {
				caste.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(caste);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param caste
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Caste caste) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("caste", caste);
		arg.put("casteListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Caste/CasteDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public void setCasteService(CasteService casteService) {
		this.casteService = casteService;
	}
}