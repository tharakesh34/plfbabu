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
 * FileName    		:  MaritalStatusCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.maritalstatuscode;

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

import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.service.systemmasters.MaritalStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.maritalstatuscode.model.MaritalStatusCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/MaritalStatusCode/MaritalStatusCodeList.zul file.
 */
public class MaritalStatusCodeListCtrl extends GFCBaseListCtrl<MaritalStatusCode> {
	private static final long serialVersionUID = -8496246844446191225L;
	private static final Logger logger = Logger.getLogger(MaritalStatusCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_MaritalStatusCodeList;
	protected Borderlayout borderLayout_MaritalStatusCodeList;
	protected Paging pagingMaritalStatusCodeList;
	protected Listbox listBoxMaritalStatusCode;

	protected Textbox maritalStsCode;
	protected Textbox maritalStsDesc;
	protected Checkbox maritalStsIsActive;

	protected Listbox sortOperator_maritalStsDesc;
	protected Listbox sortOperator_maritalStsCode;
	protected Listbox sortOperator_maritalStsIsActive;

	// List headers
	protected Listheader listheader_MaritalStsCode;
	protected Listheader listheader_MaritalStsDesc;
	protected Listheader listheader_MaritalStsIsActive;

	// checkRights
	protected Button button_MaritalStatusCodeList_NewMaritalStatusCode;
	protected Button button_MaritalStatusCodeList_MaritalStatusCodeSearchDialog;

	private transient MaritalStatusCodeService maritalStatusCodeService;

	/**
	 * default constructor.<br>
	 */
	public MaritalStatusCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "MaritalStatusCode";
		super.pageRightName = "MaritalStatusCodeList";
		super.tableName = "BMTMaritalStatusCodes_AView";
		super.queueTableName = "BMTMaritalStatusCodes_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("maritalStsCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_MaritalStatusCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_MaritalStatusCodeList, borderLayout_MaritalStatusCodeList, listBoxMaritalStatusCode,
				pagingMaritalStatusCodeList);
		setItemRender(new MaritalStatusCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_MaritalStatusCodeList_NewMaritalStatusCode,
				"button_MaritalStatusCodeList_NewMaritalStatusCode", true);
		registerButton(button_MaritalStatusCodeList_MaritalStatusCodeSearchDialog);

		registerField("maritalStsCode", listheader_MaritalStsCode, SortOrder.ASC, maritalStsCode,
				sortOperator_maritalStsCode, Operators.STRING);
		registerField("maritalStsDesc", listheader_MaritalStsDesc, SortOrder.NONE, maritalStsDesc,
				sortOperator_maritalStsDesc, Operators.STRING);
		registerField("maritalStsIsActive", listheader_MaritalStsIsActive, SortOrder.NONE, maritalStsIsActive,
				sortOperator_maritalStsIsActive, Operators.BOOLEAN);

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
	public void onClick$button_MaritalStatusCodeList_MaritalStatusCodeSearchDialog(Event event) {
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
	public void onClick$button_MaritalStatusCodeList_NewMaritalStatusCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		MaritalStatusCode maritalStatusCode = new MaritalStatusCode();
		maritalStatusCode.setNewRecord(true);
		maritalStatusCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(maritalStatusCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onMaritalStatusCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxMaritalStatusCode.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		MaritalStatusCode maritalStatusCode = maritalStatusCodeService.getMaritalStatusCodeById(id);

		if (maritalStatusCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND MaritalStsCode='" + maritalStatusCode.getMaritalStsCode() + "' AND version="
				+ maritalStatusCode.getVersion() + " ";

		if (doCheckAuthority(maritalStatusCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && maritalStatusCode.getWorkflowId() == 0) {
				maritalStatusCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(maritalStatusCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param maritalStatusCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(MaritalStatusCode maritalStatusCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("maritalStatusCode", maritalStatusCode);
		arg.put("maritalStatusCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/MaritalStatusCode/MaritalStatusCodeDialog.zul",
					null, arg);
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

	public void setMaritalStatusCodeService(MaritalStatusCodeService maritalStatusCodeService) {
		this.maritalStatusCodeService = maritalStatusCodeService;
	}
}