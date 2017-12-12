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
 * FileName    		:  LovFieldCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.staticparms.lovfieldcode;

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

import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.service.staticparms.LovFieldCodeService;
import com.pennant.webui.staticparms.lovfieldcode.model.LovFieldCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/StaticParms/LovFieldCode/LovFieldCodeList.zul file.
 */
public class LovFieldCodeListCtrl extends GFCBaseListCtrl<LovFieldCode> {
	private static final long serialVersionUID = 8396609468989226478L;
	private static final Logger logger = Logger.getLogger(LovFieldCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LovFieldCodeList;
	protected Borderlayout borderLayout_LovFieldCodeList;
	protected Paging pagingLovFieldCodeList;
	protected Listbox listBoxLovFieldCode;

	protected Listheader listheader_FieldCode;
	protected Listheader listheader_FieldCodeDesc;
	protected Listheader listheader_FieldCodeType;
	protected Listheader listheader_FieldEdit;
	protected Listheader listheader_isActive;

	protected Button button_LovFieldCodeList_NewLovFieldCode;
	protected Button button_LovFieldCodeList_LovFieldCodeSearchDialog;

	protected Textbox fieldCode;
	protected Textbox fieldCodeDesc;
	protected Textbox fieldCodeType;
	protected Checkbox isActive;

	protected Listbox sortOperator_fieldCode;
	protected Listbox sortOperator_fieldCodeDesc;
	protected Listbox sortOperator_fieldCodeType;
	protected Listbox sortOperator_isActive;

	private transient LovFieldCodeService lovFieldCodeService;

	/**
	 * default constructor.<br>
	 */
	public LovFieldCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LovFieldCode";
		super.pageRightName = "LovFieldCodeList";
		super.tableName = "BMTLovFieldCode_AView";
		super.queueTableName = "BMTLovFieldCode_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LovFieldCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_LovFieldCodeList, borderLayout_LovFieldCodeList, listBoxLovFieldCode,
				pagingLovFieldCodeList);
		setItemRender(new LovFieldCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LovFieldCodeList_NewLovFieldCode, RIGHT_NOT_ACCESSIBLE, true);
		registerButton(button_LovFieldCodeList_LovFieldCodeSearchDialog);

		registerField("fieldCode", listheader_FieldCode, SortOrder.ASC, fieldCode, sortOperator_fieldCode,
				Operators.STRING);
		registerField("fieldCodeDesc", listheader_FieldCodeDesc, SortOrder.NONE, fieldCodeDesc,
				sortOperator_fieldCodeDesc, Operators.STRING);
		registerField("fieldCodeType", listheader_FieldCodeType, SortOrder.NONE, fieldCodeType,
				sortOperator_fieldCodeType, Operators.STRING);
		registerField("isActive", listheader_isActive, SortOrder.NONE, isActive, sortOperator_isActive,
				Operators.BOOLEAN);
		registerField("fieldEdit", listheader_FieldEdit, SortOrder.NONE);

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
	public void onClick$button_LovFieldCodeList_LovFieldCodeSearchDialog(Event event) {
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
	public void onClick$button_LovFieldCodeList_NewLovFieldCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		LovFieldCode LovFieldCode = new LovFieldCode();
		LovFieldCode.setNewRecord(true);
		LovFieldCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(LovFieldCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onLovFieldCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLovFieldCode.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		LovFieldCode lovFieldCode = lovFieldCodeService.getLovFieldCodeById(id);

		if (lovFieldCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FieldCode='" + lovFieldCode.getFieldCode() + "' AND version="
				+ lovFieldCode.getVersion() + " ";

		if (doCheckAuthority(lovFieldCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && lovFieldCode.getWorkflowId() == 0) {
				lovFieldCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(lovFieldCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aLovFieldCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LovFieldCode aLovFieldCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("lovFieldCode", aLovFieldCode);
		arg.put("lovFieldCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/StaticParms/LovFieldCode/LovFieldCodeDialog.zul", null, arg);
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

	public void setLovFieldCodeService(LovFieldCodeService lovFieldCodeService) {
		this.lovFieldCodeService = lovFieldCodeService;
	}
}