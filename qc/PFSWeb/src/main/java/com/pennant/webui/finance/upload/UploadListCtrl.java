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
 * FileName    		:  UploadListCtrl.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2018    														*
 *                                                                  						*
 * Modified Date    :  04-10-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2018       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.upload;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.webui.finance.upload.model.UploadListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/Uploads/UploadList.zul file.
 */
public class UploadListCtrl extends GFCBaseListCtrl<UploadHeader> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = Logger.getLogger(UploadListCtrl.class);

	protected Window window_UploadList;
	protected Borderlayout borderLayout_UploadList;
	protected Listbox listBoxUpload;
	protected Paging pagingUploadList;

	protected Listheader listheader_UploadList_FileName;
	protected Listheader listheader_UploadList_TransactionDate;
	protected Listheader listheader_UploadList_RecordCount;

	protected Button button_UploadList_New;
	protected Button button_UploadList_SearchDialog;

	protected Textbox fileName;
	protected Datebox transactionDate;

	protected Listbox sortOperator_FileName;
	protected Listbox sortOperator_TransactionDate;

	private transient UploadHeaderService uploadHeaderService;

	private String module = "";

	/**
	 * The default constructor.
	 */
	public UploadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "UploadHeader";
		super.pageRightName = "UploadHeaderList";
		super.tableName = "UploadHeader_AView";
		super.queueTableName = "UploadHeader_TView";
		super.enquiryTableName = "UploadHeader_View";

		this.module = getArgument("module");
		this.transactionDate.setFormat(PennantConstants.dateFormat);
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("Module", this.module, Filter.OP_EQUAL);
		this.searchObject.addFilters(filters);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_UploadList(Event event) {
		// Set the page level components.
		setPageComponents(window_UploadList, borderLayout_UploadList, listBoxUpload, pagingUploadList);
		setItemRender(new UploadListModelItemRenderer());

		String newButtonRight = "";
		if (UploadConstants.UPLOAD_MODULE_REFUND.equals(this.module)) {
			newButtonRight = "button_UploadList_NewRefundUpload";
		} else if (UploadConstants.UPLOAD_MODULE_ASSIGNMENT.equals(this.module)) {
			newButtonRight = "button_UploadList_NewAssignmentUpload";
		}

		// Register buttons and fields.
		registerButton(button_UploadList_New, newButtonRight, true);
		registerButton(button_UploadList_SearchDialog);

		registerField("UploadId");
		registerField("FileName", listheader_UploadList_FileName, SortOrder.ASC, fileName, sortOperator_FileName,
				Operators.STRING);
		registerField("TransactionDate", listheader_UploadList_TransactionDate, SortOrder.ASC, transactionDate,
				sortOperator_TransactionDate, Operators.DATE);
		registerField("TotalRecords", listheader_UploadList_RecordCount);
		registerField("Module");

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
	public void onClick$button_UploadList_SearchDialog(Event event) {
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
	public void onClick$button_UploadList_New(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setNewRecord(true);
		uploadHeader.setWorkflowId(getWorkFlowId());
		uploadHeader.setModule(this.module);
		uploadHeader.setTransactionDate(DateUtility.getSysDate());

		// Display the dialog page.
		doShowDialogPage(uploadHeader);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onUploadItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxUpload.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		UploadHeader uploadHeader = uploadHeaderService.getUploadHeaderById(id);

		if (uploadHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " UploadId = '" + uploadHeader.getUploadId() + "' AND version=" + uploadHeader.getVersion()
				+ " ";

		if (doCheckAuthority(uploadHeader, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && uploadHeader.getWorkflowId() == 0) {
				uploadHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(uploadHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param uploadHeader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(UploadHeader uploadHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("uploadHeader", uploadHeader);
		arg.put("uploadListCtrl", this);

		String zulPath = "";

		if (UploadConstants.UPLOAD_MODULE_REFUND.equals(this.module)) {
			zulPath = "/WEB-INF/pages/Finance/Uploads/RefundUploadDialog.zul";
		} else if (UploadConstants.UPLOAD_MODULE_ASSIGNMENT.equals(this.module)) {
			zulPath = "/WEB-INF/pages/Finance/Uploads/AssignmentUploadDialog.zul";
		}

		try {
			Executions.createComponents(zulPath, null, arg);
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
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}
}