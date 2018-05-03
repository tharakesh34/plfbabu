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
 * FileName    		:  OtherBankFinanceTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2015    														*
 *                                                                  						*
 * Modified Date    :  03-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.otherbankfinancetype;

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

import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.service.applicationmaster.OtherBankFinanceTypeService;
import com.pennant.webui.applicationmaster.otherbankfinancetype.model.OtherBankFinanceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/OtherBankFinanceType
 * /OtherBankFinanceTypeList.zul file.
 */
public class OtherBankFinanceTypeListCtrl extends GFCBaseListCtrl<OtherBankFinanceType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(OtherBankFinanceTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_OtherBankFinanceTypeList;
	protected Borderlayout borderLayout_OtherBankFinanceTypeList;
	protected Paging pagingOtherBankFinanceTypeList;
	protected Listbox listBoxOtherBankFinanceType;

	protected Textbox finType;
	protected Textbox finDesc;
	protected Checkbox finIsActive;

	protected Listbox sortOperator_finType;
	protected Listbox sortOperator_finDesc;
	protected Listbox sortOperator_finIsActive;

	// List headers
	protected Listheader listheader_FinType;
	protected Listheader listheader_FinTypeDesc;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_OtherBankFinanceTypeList_NewOtherBankFinanceType;
	protected Button button_OtherBankFinanceTypeList_OtherBankFinanceTypeSearchDialog;

	private transient OtherBankFinanceTypeService otherBankFinanceTypeService;

	/**
	 * default constructor.<br>
	 */
	public OtherBankFinanceTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "OtherBankFinanceType";
		super.pageRightName = "OtherBankFinanceTypeList";
		super.tableName = "OtherBankFinanceType_AView";
		super.queueTableName = "OtherBankFinanceType_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_OtherBankFinanceTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_OtherBankFinanceTypeList, borderLayout_OtherBankFinanceTypeList,
				listBoxOtherBankFinanceType, pagingOtherBankFinanceTypeList);
		setItemRender(new OtherBankFinanceTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_OtherBankFinanceTypeList_NewOtherBankFinanceType,
				"button_OtherBankFinanceTypeList_NewOtherBankFinanceType", true);
		registerButton(button_OtherBankFinanceTypeList_OtherBankFinanceTypeSearchDialog);

		registerField("FinType", listheader_FinType, SortOrder.ASC, finType, sortOperator_finType, Operators.STRING);
		registerField("FinTypeDesc", listheader_FinTypeDesc, SortOrder.NONE, finDesc, sortOperator_finDesc,
				Operators.STRING);
		registerField("Active", listheader_Active, SortOrder.NONE, finIsActive, sortOperator_finIsActive,
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
	public void onClick$button_OtherBankFinanceTypeList_OtherBankFinanceTypeSearchDialog(Event event) {
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
	public void onClick$button_OtherBankFinanceTypeList_NewOtherBankFinanceType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		OtherBankFinanceType otherBankFinanceType = new OtherBankFinanceType();
		otherBankFinanceType.setNewRecord(true);
		otherBankFinanceType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(otherBankFinanceType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onOtherBankFinanceTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxOtherBankFinanceType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		OtherBankFinanceType otherBankFinanceType = otherBankFinanceTypeService.getOtherBankFinanceTypeById(id);

		if (otherBankFinanceType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinType='" + otherBankFinanceType.getFinType() + "' AND version="
				+ otherBankFinanceType.getVersion() + " ";

		if (doCheckAuthority(otherBankFinanceType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && otherBankFinanceType.getWorkflowId() == 0) {
				otherBankFinanceType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(otherBankFinanceType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aOtherBankFinanceType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(OtherBankFinanceType aOtherBankFinanceType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("otherBankFinanceType", aOtherBankFinanceType);
		arg.put("otherBankFinanceTypeListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/OtherBankFinanceType/OtherBankFinanceTypeDialog.zul", null, arg);
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

	public void setOtherBankFinanceTypeService(OtherBankFinanceTypeService otherBankFinanceTypeService) {
		this.otherBankFinanceTypeService = otherBankFinanceTypeService;
	}
}