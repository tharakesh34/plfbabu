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
 * FileName    		:  SplRateCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.splratecode;

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

import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.service.applicationmaster.SplRateCodeService;
import com.pennant.webui.applicationmaster.splratecode.model.SplRateCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/SplRateCode/SplRateCodeList.zul file.
 */
public class SplRateCodeListCtrl extends GFCBaseListCtrl<SplRateCode> {
	private static final long serialVersionUID = 7426008145901571944L;
	private static final Logger logger = Logger.getLogger(SplRateCodeListCtrl.class);

	protected Window window_SplRateCodeList;
	protected Borderlayout borderLayout_SplRateCodeList;
	protected Paging pagingSplRateCodeList;
	protected Listbox listBoxSplRateCode;

	// List headers
	protected Listheader listheader_SRType;
	protected Listheader listheader_SRTypeDesc;
	protected Listheader listheader_SRIsActive;

	// checkRights
	protected Button button_SplRateCodeList_NewSplRateCode;
	protected Button button_SplRateCodeList_SplRateCodeSearchDialog;

	protected Textbox sRType;
	protected Textbox sRTypeDesc;
	protected Checkbox sRIsActive;

	protected Listbox sortOperator_sRType;
	protected Listbox sortOperator_sRTypeDesc;
	protected Listbox sortOperator_sRIsActive;

	private transient SplRateCodeService splRateCodeService;

	/**
	 * The default constructor.
	 */
	public SplRateCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SplRateCode";
		super.pageRightName = "SplRateCodeList";
		super.tableName = "RMTSplRateCodes_AView";
		super.queueTableName = "RMTSplRateCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SplRateCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_SplRateCodeList, borderLayout_SplRateCodeList, listBoxSplRateCode,
				pagingSplRateCodeList);
		setItemRender(new SplRateCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SplRateCodeList_NewSplRateCode, "button_SplRateCodeList_NewSplRateCode", true);
		registerButton(button_SplRateCodeList_SplRateCodeSearchDialog);

		registerField("sRType", listheader_SRType, SortOrder.ASC, sRType, sortOperator_sRType, Operators.STRING);
		registerField("sRTypeDesc", listheader_SRTypeDesc, SortOrder.NONE, sRTypeDesc, sortOperator_sRTypeDesc,
				Operators.STRING);
		registerField("sRIsActive", listheader_SRIsActive, SortOrder.NONE, sRIsActive, sortOperator_sRIsActive,
				Operators.BOOLEAN);

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
	public void onClick$button_SplRateCodeList_SplRateCodeSearchDialog(Event event) {
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

	public void onClick$button_SplRateCodeList_NewSplRateCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SplRateCode aSplRateCode = new SplRateCode();
		aSplRateCode.setNewRecord(true);
		aSplRateCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aSplRateCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSplRateCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSplRateCode.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		SplRateCode aSplRateCode = splRateCodeService.getSplRateCodeById(id);

		if (aSplRateCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SRType='" + aSplRateCode.getSRType() + "' AND version=" + aSplRateCode.getVersion()
				+ " ";

		if (doCheckAuthority(aSplRateCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aSplRateCode.getWorkflowId() == 0) {
				aSplRateCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aSplRateCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SplRateCode aSplRateCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("splRateCode", aSplRateCode);
		arg.put("splRateCodeListCtrl", this);
		arg.put("newRecord", aSplRateCode.isNew());

		try {
			Executions
					.createComponents("/WEB-INF/pages/ApplicationMaster/SplRateCode/SplRateCodeDialog.zul", null, arg);
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

	public void setSplRateCodeService(SplRateCodeService splRateCodeService) {
		this.splRateCodeService = splRateCodeService;
	}
}