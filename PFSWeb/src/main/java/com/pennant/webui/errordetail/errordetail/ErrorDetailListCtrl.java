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
 * FileName    		:  ErrorDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2016    														*
 *                                                                  						*
 * Modified Date    :  05-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.errordetail.errordetail;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.errordetail.errordetail.model.ErrorDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/ErrorDetail/ErrorDetail/ErrorDetailList.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class ErrorDetailListCtrl extends GFCBaseListCtrl<ErrorDetails> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ErrorDetailListCtrl.class);

	protected Window window_ErrorDetailList;
	protected Borderlayout borderLayout_ErrorDetailList;
	protected Paging pagingErrorDetailList;
	protected Listbox listBoxErrorDetail;

	protected Listheader listheader_ErrorCode;
	protected Listheader listheader_ErrorLanguage;
	protected Listheader listheader_ErrorSeverity;
	protected Listheader listheader_ErrorMessage;
	protected Listheader listheader_ErrorExtendedMessage;

	protected Textbox errorCode;
	protected Textbox errorLanguage;
	protected Combobox errorSeverity;
	protected Textbox errorMessage;
	protected Textbox errorExtendedMessage;

	protected Listbox sortOperator_ErrorCode;
	protected Listbox sortOperator_ErrorLanguage;
	protected Listbox sortOperator_ErrorSeverity;
	protected Listbox sortOperator_ErrorMessage;
	protected Listbox sortOperator_ErrorExtendedMessage;

	protected Button button_ErrorDetailList_NewErrorDetail;
	protected Button button_ErrorDetailList_ErrorDetailSearch;

	private transient ErrorDetailService errorDetailService;

	/**
	 * default constructor.<br>
	 */
	public ErrorDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ErrorDetails";
		super.pageRightName = "ErrorDetailList";
		super.tableName = "ErrorDetails_AView";
		super.queueTableName = "ErrorDetails_View";
		super.enquiryTableName = "ErrorDetails_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ErrorDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_ErrorDetailList, borderLayout_ErrorDetailList, listBoxErrorDetail,
				pagingErrorDetailList);
		setItemRender(new ErrorDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ErrorDetailList_NewErrorDetail, "button_ErrorDetailList_NewErrorDetail", true);
		registerButton(button_ErrorDetailList_ErrorDetailSearch);

		fillComboBox(this.errorSeverity, "", PennantStaticListUtil.getSysParamType(), "");

		registerField("errorCode", listheader_ErrorCode, SortOrder.ASC, errorCode, sortOperator_ErrorCode,
				Operators.STRING);
		registerField("errorLanguage", listheader_ErrorLanguage, SortOrder.NONE, errorLanguage,
				sortOperator_ErrorLanguage, Operators.STRING);
		registerField("errorSeverity", listheader_ErrorSeverity, SortOrder.NONE, errorSeverity,
				sortOperator_ErrorSeverity, Operators.SIMPLE_NUMARIC);
		registerField("errorMessage", listheader_ErrorMessage, SortOrder.NONE, errorMessage, sortOperator_ErrorMessage,
				Operators.STRING);
		registerField("errorExtendedMessage", listheader_ErrorExtendedMessage, SortOrder.NONE, errorExtendedMessage,
				sortOperator_ErrorExtendedMessage, Operators.STRING);

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
	public void onClick$button_ErrorDetailList_ErrorDetailSearch(Event event) {
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
	public void onClick$button_ErrorDetailList_NewErrorDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ErrorDetails errorDetail = new ErrorDetails();
		errorDetail.setNewRecord(true);
		errorDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(errorDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onErrorDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxErrorDetail.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		ErrorDetails errorDetail = errorDetailService.getErrorDetailById(id);

		if (errorDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ErrorCode='" + errorDetail.getId() + "' AND version=" + errorDetail.getVersion() + " ";

		if (doCheckAuthority(errorDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && errorDetail.getWorkflowId() == 0) {
				errorDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(errorDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param errorDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ErrorDetails errorDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("errorDetail", errorDetail);
		arg.put("errorDetailListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/ErrorDetail/ErrorDetailDialog.zul", null, arg);
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

	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		this.errorDetailService = errorDetailService;
	}
}