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
 * FileName    		:  NationalityCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.nationalitycode;

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

import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.service.systemmasters.NationalityCodeService;
import com.pennant.webui.systemmasters.nationalitycode.model.NationalityCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/NationalityCode/NationalityCodeList.zul file.
 */
public class NationalityCodeListCtrl extends GFCBaseListCtrl<NationalityCode> {
	private static final long serialVersionUID = 1844331787045784573L;
	private static final Logger logger = Logger.getLogger(NationalityCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_NationalityCodeList;
	protected Borderlayout borderLayout_NationalityCodeList;
	protected Paging pagingNationalityCodeList;
	protected Listbox listBoxNationalityCode;

	protected Textbox nationalityCode;
	protected Textbox nationalityDesc;
	protected Checkbox nationalityIsActive;

	protected Listbox sortOperator_nationalityDesc;
	protected Listbox sortOperator_nationalityCode;
	protected Listbox sortOperator_nationalityIsActive;

	// List headers
	protected Listheader listheader_NationalityCode;
	protected Listheader listheader_NationalityDesc;
	protected Listheader listheader_NationalityIsActive;

	// checkRights
	protected Button button_NationalityCodeList_NewNationalityCode;
	protected Button button_NationalityCodeList_NationalityCodeSearchDialog;

	private transient NationalityCodeService nationalityCodeService;

	/**
	 * default constructor.<br>
	 */
	public NationalityCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "NationalityCode";
		super.pageRightName = "NationalityCodeList";
		super.tableName = "BMTNationalityCodes_AView";
		super.queueTableName = "BMTNationalityCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_NationalityCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_NationalityCodeList, borderLayout_NationalityCodeList, listBoxNationalityCode,
				pagingNationalityCodeList);
		setItemRender(new NationalityCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_NationalityCodeList_NewNationalityCode, "button_NationalityCodeList_NewNationalityCode",
				true);
		registerButton(button_NationalityCodeList_NationalityCodeSearchDialog);

		registerField("nationalityCode", listheader_NationalityCode, SortOrder.ASC, nationalityCode,
				sortOperator_nationalityCode, Operators.STRING);
		registerField("nationalityDesc", listheader_NationalityDesc, SortOrder.NONE, nationalityDesc,
				sortOperator_nationalityDesc, Operators.STRING);
		registerField("nationalityIsActive", listheader_NationalityIsActive, SortOrder.NONE, nationalityIsActive,
				sortOperator_nationalityIsActive, Operators.BOOLEAN);

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
	public void onClick$button_NationalityCodeList_NationalityCodeSearchDialog(Event event) {
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
	public void onClick$button_NationalityCodeList_NewNationalityCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		NationalityCode nationalityCode = new NationalityCode();
		nationalityCode.setNewRecord(true);
		nationalityCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(nationalityCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onNationalityCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxNationalityCode.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		NationalityCode nationalityCode = nationalityCodeService.getNationalityCodeById(id);

		if (nationalityCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND NationalityCode='" + nationalityCode.getNationalityCode() + "' AND version="
				+ nationalityCode.getVersion() + " ";
		if (doCheckAuthority(nationalityCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && nationalityCode.getWorkflowId() == 0) {
				nationalityCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(nationalityCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param nationalityCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(NationalityCode nationalityCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("nationalityCode", nationalityCode);
		arg.put("nationalityCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/NationalityCode/NationalityCodeDialog.zul", null,
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

	public void setNationalityCodeService(NationalityCodeService nationalityCodeService) {
		this.nationalityCodeService = nationalityCodeService;
	}
}