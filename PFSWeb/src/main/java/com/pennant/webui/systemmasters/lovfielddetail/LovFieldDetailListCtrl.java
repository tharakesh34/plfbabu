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
 * FileName    		:  LovFieldDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.lovfielddetail;

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

import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.webui.systemmasters.lovfielddetail.model.LovFieldDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/LovFieldDetail/LovFieldDetailList.zul file.
 */
public class LovFieldDetailListCtrl extends GFCBaseListCtrl<LovFieldDetail> {
	private static final long serialVersionUID = 3047814941939865707L;
	private static final Logger logger = Logger.getLogger(LovFieldDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LovFieldDetailList;
	protected Borderlayout borderLayout_LovFieldDetailList;
	protected Paging pagingLovFieldDetailList;
	protected Listbox listBoxLovFieldDetail;

	protected Textbox fieldCodeId;
	protected Textbox fieldCodeValue;
	protected Checkbox isActive;

	protected Listbox sortOperator_fieldCodeValue;
	protected Listbox sortOperator_fieldCodeId;
	protected Listbox sortOperator_isActive;

	// List headers
	protected Listheader listheader_FieldCode;
	protected Listheader listheader_FieldCodeValue;
	protected Listheader listheader_isActive;

	// checkRights
	protected Button button_LovFieldDetailList_NewLovFieldDetail;
	protected Button button_LovFieldDetailList_LovFieldDetailSearchDialog;

	private transient LovFieldDetailService lovFieldDetailService;

	/**
	 * default constructor.<br>
	 */
	public LovFieldDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LovFieldDetail";
		super.pageRightName = "LovFieldDetailList";
		super.tableName = "RMTLovFieldDetail_AView";
		super.queueTableName = "RMTLovFieldDetail_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LovFieldDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_LovFieldDetailList, borderLayout_LovFieldDetailList, listBoxLovFieldDetail,
				pagingLovFieldDetailList);
		setItemRender(new LovFieldDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LovFieldDetailList_NewLovFieldDetail, "button_LovFieldDetailList_NewLovFieldDetail", true);
		registerButton(button_LovFieldDetailList_LovFieldDetailSearchDialog);

		registerField("fieldCodeId");
		registerField("lovDescFieldCodeName");
		registerField("fieldCode", listheader_FieldCode, SortOrder.ASC, fieldCodeId, sortOperator_fieldCodeId,
				Operators.STRING);
		registerField("fieldCodeValue", listheader_FieldCodeValue, SortOrder.NONE, fieldCodeValue,
				sortOperator_fieldCodeValue, Operators.STRING);
		registerField("isActive", listheader_isActive, SortOrder.NONE, isActive, sortOperator_isActive,
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
	public void onClick$button_LovFieldDetailList_LovFieldDetailSearchDialog(Event event) {
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
	public void onClick$button_LovFieldDetailList_NewLovFieldDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		LovFieldDetail lovFieldDetail = new LovFieldDetail();
		lovFieldDetail.setNewRecord(true);
		lovFieldDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(lovFieldDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onLovFieldDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLovFieldDetail.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String fieldCode = ((String) selectedItem.getAttribute("fieldCode"));
		String fieldCodeValue = ((String) selectedItem.getAttribute("fieldCodeValue"));
		LovFieldDetail lovFieldDetail = lovFieldDetailService.getLovFieldDetailById(fieldCode, fieldCodeValue);

		if (lovFieldDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FieldCodeId=" + lovFieldDetail.getFieldCodeId() + " AND version="
				+ lovFieldDetail.getVersion() + " ";
		if (doCheckAuthority(lovFieldDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && lovFieldDetail.getWorkflowId() == 0) {
				lovFieldDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(lovFieldDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param lovFieldDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LovFieldDetail lovFieldDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("lovFieldDetail", lovFieldDetail);
		arg.put("lovFieldDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/LovFieldDetail/LovFieldDetailDialog.zul", null,
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

	public void setLovFieldDetailService(LovFieldDetailService lovFieldDetailService) {
		this.lovFieldDetailService = lovFieldDetailService;
	}
}