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
 * FileName    		:  DivisionDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-08-2013    														*
 *                                                                  						*
 * Modified Date    :  02-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.divisiondetail;

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

import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.systemmasters.DivisionDetailService;
import com.pennant.webui.systemmasters.divisiondetail.model.DivisionDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/DivisionDetail/DivisionDetailList.zul file.
 */
public class DivisionDetailListCtrl extends GFCBaseListCtrl<DivisionDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DivisionDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DivisionDetailList;
	protected Borderlayout borderLayout_DivisionDetailList;
	protected Paging pagingDivisionDetailList;
	protected Listbox listBoxDivisionDetail;

	protected Textbox divisionCode;
	protected Textbox divisionCodeDesc;
	protected Checkbox active;

	protected Listbox sortOperator_DivisionCode;
	protected Listbox sortOperator_DivisionCodeDesc;
	protected Listbox sortOperator_Active;

	// List headers
	protected Listheader listheader_DivisionCode;
	protected Listheader listheader_DivisionCodeDesc;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_DivisionDetailList_NewDivisionDetail;
	protected Button button_DivisionDetailList_DivisionDetailSearch;

	private transient DivisionDetailService divisionDetailService;

	/**
	 * default constructor.<br>
	 */
	public DivisionDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DivisionDetail";
		super.pageRightName = "DivisionDetailList";
		super.tableName = "SMTDivisionDetail_AView";
		super.queueTableName = "SMTDivisionDetail_View";
		super.enquiryTableName = "SMTDivisionDetail_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DivisionDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_DivisionDetailList, borderLayout_DivisionDetailList, listBoxDivisionDetail,
				pagingDivisionDetailList);
		setItemRender(new DivisionDetailListModelItemRenderer());

		// Register buttons and fields.
		//registerButton(button_DivisionDetailList_NewDivisionDetail, "button_DivisionDetailList_NewDivisionDetail", true);
		registerButton(button_DivisionDetailList_DivisionDetailSearch);

		registerField("divisionCode", listheader_DivisionCode, SortOrder.ASC, divisionCode, sortOperator_DivisionCode,
				Operators.STRING);
		registerField("divisionCodeDesc", listheader_DivisionCodeDesc, SortOrder.NONE, divisionCodeDesc,
				sortOperator_DivisionCodeDesc, Operators.STRING);
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
	public void onClick$button_DivisionDetailList_DivisionDetailSearch(Event event) {
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
	public void onClick$button_DivisionDetailList_NewDivisionDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		DivisionDetail divisionDetail = new DivisionDetail();
		divisionDetail.setNewRecord(true);
		divisionDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(divisionDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDivisionDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDivisionDetail.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		DivisionDetail divisionDetail = divisionDetailService.getDivisionDetailById(id);
		if (divisionDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND DivisionCode='" + divisionDetail.getDivisionCode() + "' AND version="
				+ divisionDetail.getVersion() + " ";

		if (doCheckAuthority(divisionDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && divisionDetail.getWorkflowId() == 0) {
				divisionDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(divisionDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param divisionDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DivisionDetail divisionDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("divisionDetail", divisionDetail);
		arg.put("divisionDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/DivisionDetail/DivisionDetailDialog.zul", null,
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

	public void setDivisionDetailService(DivisionDetailService divisionDetailService) {
		this.divisionDetailService = divisionDetailService;
	}
}