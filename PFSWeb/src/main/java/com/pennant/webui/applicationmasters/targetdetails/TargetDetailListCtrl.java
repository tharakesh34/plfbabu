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
 * FileName    		:  GenderListCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmasters.targetdetails;


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

import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.service.applicationmaster.TargetDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmasters.targetdetails.model.TargetdetailListModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMasters/TargetDetails/TargetDetailsList.zul file.
 */
public class TargetDetailListCtrl extends GFCBaseListCtrl<TargetDetail> {
	private static final long serialVersionUID = 3226455931949186314L;
	private static final Logger logger = Logger.getLogger(TargetDetailListCtrl.class);

	protected Window window_TargetDetailList;
	protected Borderlayout borderLayout_TargetDetailList;
	protected Paging pagingTargetDetailList;
	protected Listbox listBoxTargetDetailList;

	protected Listheader listheader_TargetCode;
	protected Listheader listheader_TargetDesc;
	protected Listheader listheader_TargetIsActive;

	protected Button button_TargetDetailList_NewTargetDetailList;
	protected Button button_TargetDetailList_TargetDetailsSearchDialog;

	protected Textbox targetCode;
	protected Textbox targetDesc;
	protected Checkbox targetIsActive;

	protected Listbox sortOperator_targetCode;
	protected Listbox sortOperator_targetDesc;
	protected Listbox sortOperator_targetIsActive;

	private transient TargetDetailService targetDetailService;

	/**
	 * default constructor.<br>
	 */
	public TargetDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "TargetDetail";
		super.pageRightName = "TargetDetailList";
		super.tableName = "TargetDetails_AView";
		super.queueTableName = "TargetDetails_View";
	}
	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("targetCode",PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_TargetDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_TargetDetailList, borderLayout_TargetDetailList, listBoxTargetDetailList,
				pagingTargetDetailList);
		setItemRender(new TargetdetailListModelItemRender());

		// Register buttons and fields.
		registerButton(button_TargetDetailList_NewTargetDetailList, "button_TargetDetailList_NewTargetDetailList", true);
		registerButton(button_TargetDetailList_TargetDetailsSearchDialog);

		registerField("targetCode", listheader_TargetCode, SortOrder.ASC, targetCode, sortOperator_targetCode,
				Operators.STRING);
		registerField("targetDesc", listheader_TargetDesc, SortOrder.ASC, targetDesc, sortOperator_targetDesc,
				Operators.STRING);
		registerField("active", listheader_TargetIsActive, SortOrder.NONE, targetIsActive, sortOperator_targetIsActive,
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
	public void onClick$button_TargetDetailList_TargetDetailsSearchDialog(Event event) {
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
	public void onClick$button_TargetDetailList_NewTargetDetailList(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		TargetDetail targetDetail = new TargetDetail();
		targetDetail.setNewRecord(true);
		targetDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(targetDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onTargetDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxTargetDetailList.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		TargetDetail targetDetail = targetDetailService.getTargetDetailById(id);

		if (targetDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND TargetCode='" + targetDetail.getTargetCode() + "'" + " AND version="
				+ targetDetail.getVersion() + " ";

		if (doCheckAuthority(targetDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && targetDetail.getWorkflowId() == 0) {
				targetDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(targetDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aTargetDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TargetDetail aTargetDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("targetDetail", aTargetDetail);
		arg.put("targetDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/TargetDetails/TargetDetailDialog.zul", null,
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

	public void setTargetDetailService(TargetDetailService targetDetailService) {
		this.targetDetailService = targetDetailService;
	}

}
