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
 * FileName    		:  FeeTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.feetype.feetype;

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

import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.webui.feetype.feetype.model.FeeTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/FeeType/FeeType/FeeTypeList.zul file.
 */
public class FeeTypeListCtrl extends GFCBaseListCtrl<FeeType> {

	private static final long			serialVersionUID	= 1L;
	private static final Logger			logger				= Logger.getLogger(FeeTypeListCtrl.class);

	protected Window					window_FeeTypeList;
	protected Borderlayout				borderLayout_FeeTypeList;
	protected Paging					pagingFeeTypeList;
	protected Listbox					listBoxFeeType;

	protected Listheader				listheader_FeeTypeCode;
	protected Listheader				listheader_FeeTypeDesc;
	protected Listheader				listheader_Active;

	protected Button					button_FeeTypeList_NewFeeType;
	protected Button					button_FeeTypeList_FeeTypeSearch;

	protected Textbox					feeTypeCode;
	protected Textbox					feeTypeDesc;
	protected Checkbox					active;

	protected Listbox					sortOperator_FeeTypeCode;
	protected Listbox					sortOperator_FeeTypeDesc;
	protected Listbox					sortOperator_Active;

	private transient FeeTypeService	feeTypeService;

	/**
	 * default constructor.<br>
	 */
	public FeeTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FeeType";
		super.pageRightName = "FeeTypeList";
		super.tableName = "FeeTypes_AView";
		super.queueTableName = "FeeTypes_View";
		super.enquiryTableName = "FeeTypes_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FeeTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_FeeTypeList, borderLayout_FeeTypeList, listBoxFeeType, pagingFeeTypeList);
		setItemRender(new FeeTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FeeTypeList_NewFeeType, "button_FeeTypeList_NewFeeType", true);
		registerButton(button_FeeTypeList_FeeTypeSearch);

		registerField("feeTypeID");
		registerField("feeTypeCode", listheader_FeeTypeCode, SortOrder.ASC, feeTypeCode, sortOperator_FeeTypeCode,
				Operators.STRING);
		registerField("feeTypeDesc", listheader_FeeTypeDesc, SortOrder.NONE, feeTypeDesc, sortOperator_FeeTypeDesc,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

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
	public void onClick$button_FeeTypeList_FeeTypeSearch(Event event) {
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
	public void onClick$button_FeeTypeList_NewFeeType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FeeType feeType = new FeeType();
		feeType.setNewRecord(true);
		feeType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(feeType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFeeTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFeeType.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		FeeType feeType = feeTypeService.getFeeTypeById(id);

		if (feeType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FeeTypeID='" + feeType.getFeeTypeID() + "' AND version=" + feeType.getVersion() + " ";

		if (doCheckAuthority(feeType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && feeType.getWorkflowId() == 0) {
				feeType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(feeType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param feeType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FeeType feeType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("feeType", feeType);
		arg.put("feeTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FeeType/FeeType/FeeTypeDialog.zul", null, arg);
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

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

}