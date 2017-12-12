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
 * FileName    		:  RejectDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.rejectdetail;

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

import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.service.applicationmaster.RejectDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.rejectdetail.model.RejectDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/RejectDetail/RejectDetailList.zul file.
 */
public class RejectDetailListCtrl extends GFCBaseListCtrl<RejectDetail> {
	private static final long serialVersionUID = 858161292428279969L;
	private static final Logger logger = Logger.getLogger(RejectDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RejectDetailList;
	protected Borderlayout borderLayout_RejectDetailList;
	protected Paging pagingRejectDetailList;
	protected Listbox listBoxRejectDetail;

	protected Textbox rejectCode;
	protected Textbox rejectDesc;
	protected Checkbox rejectIsActive;

	protected Listbox sortOperator_rejectCode;
	protected Listbox sortOperator_rejectDesc;
	protected Listbox sortOperator_rejectIsActive;

	// List headers
	protected Listheader listheader_RejectCode;
	protected Listheader listheader_RejectDesc;
	protected Listheader listheader_RejectIsActive;

	// checkRights
	protected Button button_RejectDetailList_NewRejectDetail;
	protected Button button_RejectDetailList_RejectDetailSearchDialog;

	private transient RejectDetailService rejectDetailService;

	/**
	 * default constructor.<br>
	 */
	public RejectDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "RejectDetail";
		super.pageRightName = "RejectDetailList";
		super.tableName = "BMTRejectCodes_AView";
		super.queueTableName = "BMTRejectCodes_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("rejectCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_RejectDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_RejectDetailList, borderLayout_RejectDetailList, listBoxRejectDetail,
				pagingRejectDetailList);
		setItemRender(new RejectDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_RejectDetailList_NewRejectDetail, "button_RejectDetailList_NewRejectDetail", true);
		registerButton(button_RejectDetailList_RejectDetailSearchDialog);

		registerField("rejectCode", listheader_RejectCode, SortOrder.ASC, rejectCode, sortOperator_rejectCode,
				Operators.STRING);
		registerField("rejectDesc", listheader_RejectDesc, SortOrder.NONE, rejectDesc, sortOperator_rejectDesc,
				Operators.STRING);
		registerField("rejectIsActive", listheader_RejectIsActive, SortOrder.NONE, rejectIsActive,
				sortOperator_rejectIsActive, Operators.BOOLEAN);

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
	public void onClick$button_RejectDetailList_RejectDetailSearchDialog(Event event) {
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
	public void onClick$button_RejectDetailList_NewRejectDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		RejectDetail rejectDetail = new RejectDetail();
		rejectDetail.setNewRecord(true);
		rejectDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(rejectDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onRejectDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxRejectDetail.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		RejectDetail rejectDetail = rejectDetailService.getRejectDetailById(id);

		if (rejectDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND RejectCode='" + rejectDetail.getRejectCode() + "'" + " AND version="
				+ rejectDetail.getVersion() + " ";

		if (doCheckAuthority(rejectDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && rejectDetail.getWorkflowId() == 0) {
				rejectDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(rejectDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aRejectDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(RejectDetail aRejectDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("rejectDetail", aRejectDetail);
		arg.put("rejectDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/RejectDetail/RejectDetailDialog.zul", null,
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

	public void setRejectDetailService(RejectDetailService rejectDetailService) {
		this.rejectDetailService = rejectDetailService;
	}
}