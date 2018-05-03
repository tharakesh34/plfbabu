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
 * FileName    		:  ChequePurposeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.chequepurpose;

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

import com.pennant.backend.model.applicationmaster.ChequePurpose;
import com.pennant.backend.service.applicationmaster.ChequePurposeService;
import com.pennant.webui.applicationmaster.chequepurpose.model.ChequePurposeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ChequePurpose/ChequePurposeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ChequePurposeListCtrl extends GFCBaseListCtrl<ChequePurpose> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ChequePurposeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting autowired by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ChequePurposeList;
	protected Borderlayout borderLayout_ChequePurposeList;
	protected Paging pagingChequePurposeList;
	protected Listbox listBoxChequePurpose;

	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_Active;

	protected Button button_ChequePurposeList_NewChequePurpose;
	protected Button button_ChequePurposeList_ChequePurposeSearch;

	protected Textbox code;
	protected Textbox description;

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;

	private transient ChequePurposeService chequePurposeService;

	/**
	 * default constructor.<br>
	 */
	public ChequePurposeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ChequePurpose";
		super.pageRightName = "ChequePurposeList";
		super.tableName = "ChequePurpose_AView";
		super.queueTableName = "ChequePurpose_View";
		super.enquiryTableName = "ChequePurpose_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ChequePurposeList(Event event) {
		// Set the page level components.
		setPageComponents(window_ChequePurposeList, borderLayout_ChequePurposeList, listBoxChequePurpose,
				pagingChequePurposeList);
		setItemRender(new ChequePurposeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ChequePurposeList_NewChequePurpose, "button_ChequePurposeList_NewChequePurpose", true);
		registerButton(button_ChequePurposeList_ChequePurposeSearch);

		registerField("code", listheader_Code, SortOrder.ASC, code, sortOperator_Code, Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE);

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
	public void onClick$button_ChequePurposeList_ChequePurposeSearch(Event event) {
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
	public void onClick$button_ChequePurposeList_NewChequePurpose(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ChequePurpose chequePurpose = new ChequePurpose();
		chequePurpose.setNewRecord(true);
		chequePurpose.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(chequePurpose);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onChequePurposeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxChequePurpose.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		ChequePurpose chequePurpose = chequePurposeService.getChequePurposeById(id);

		if (chequePurpose == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND code='" + chequePurpose.getCode() + "'  AND version=" + chequePurpose.getVersion()
				+ " ";

		if (doCheckAuthority(chequePurpose, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && chequePurpose.getWorkflowId() == 0) {
				chequePurpose.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(chequePurpose);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aChequePurpose
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ChequePurpose aChequePurpose) {
		logger.debug("Entering");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("chequePurpose", aChequePurpose);
		arg.put("chequePurposeListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/ChequePurpose/ChequePurposeDialog.zul", null,
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

	public void setChequePurposeService(ChequePurposeService chequePurposeService) {
		this.chequePurposeService = chequePurposeService;
	}
}