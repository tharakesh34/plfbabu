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
 * FileName    		:  BaseRateCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.baseratecode;

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

import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.service.applicationmaster.BaseRateCodeService;
import com.pennant.webui.applicationmaster.baseratecode.model.BaseRateCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/BaseRateCode/BaseRateCodeList.zul file.
 */
public class BaseRateCodeListCtrl extends GFCBaseListCtrl<BaseRateCode> {
	private static final long serialVersionUID = 7711473870956306562L;
	private static final Logger logger = Logger.getLogger(BaseRateCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BaseRateCodeList;
	protected Borderlayout borderLayout_BaseRateCodeList;
	protected Paging pagingBaseRateCodeList;
	protected Listbox listBoxBaseRateCode;

	protected Textbox bRType;
	protected Textbox bRTypeDesc;

	protected Listbox sortOperator_bRType;
	protected Listbox sortOperator_bRTypeDesc;

	// List headers
	protected Listheader listheader_BRType;
	protected Listheader listheader_BRTypeDesc;

	// checkRights
	protected Button button_BaseRateCodeList_NewBaseRateCode;
	protected Button button_BaseRateCodeList_BaseRateCodeSearchDialog;

	private transient BaseRateCodeService baseRateCodeService;

	/**
	 * default constructor.<br>
	 */
	public BaseRateCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BaseRateCode";
		super.pageRightName = "BaseRateCodeList";
		super.tableName = "RMTBaseRateCodes_AView";
		super.queueTableName = "RMTBaseRateCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_BaseRateCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_BaseRateCodeList, borderLayout_BaseRateCodeList, listBoxBaseRateCode,
				pagingBaseRateCodeList);
		setItemRender(new BaseRateCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BaseRateCodeList_NewBaseRateCode, "button_BaseRateCodeList_NewBaseRateCode", true);
		registerButton(button_BaseRateCodeList_BaseRateCodeSearchDialog);

		registerField("bRType", listheader_BRType, SortOrder.ASC, bRType, sortOperator_bRType, Operators.STRING);
		registerField("bRTypeDesc", listheader_BRTypeDesc, SortOrder.NONE, bRTypeDesc, sortOperator_bRTypeDesc,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_BaseRateCodeList_BaseRateCodeSearchDialog(Event event) {
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
	public void onClick$button_BaseRateCodeList_NewBaseRateCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		BaseRateCode baseRateCode = new BaseRateCode();
		baseRateCode.setNewRecord(true);
		baseRateCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(baseRateCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onBaseRateCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBaseRateCode.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(id);

		if (baseRateCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BRType='" + baseRateCode.getBRType() + "' AND version=" + baseRateCode.getVersion()
				+ " ";

		if (doCheckAuthority(baseRateCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && baseRateCode.getWorkflowId() == 0) {
				baseRateCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(baseRateCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aBaseRateCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BaseRateCode aBaseRateCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("baseRateCode", aBaseRateCode);
		arg.put("baseRateCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/BaseRateCode/BaseRateCodeDialog.zul", null,
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

	public void setBaseRateCodeService(BaseRateCodeService baseRateCodeService) {
		this.baseRateCodeService = baseRateCodeService;
	}
}