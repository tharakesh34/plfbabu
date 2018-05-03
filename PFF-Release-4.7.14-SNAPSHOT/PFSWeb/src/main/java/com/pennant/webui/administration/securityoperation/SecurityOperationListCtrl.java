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
 * FileName    		:  SecurityOperationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityoperation;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.service.administration.SecurityOperationService;
import com.pennant.webui.administration.securityoperation.model.SecurityOperationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

public class SecurityOperationListCtrl extends GFCBaseListCtrl<SecurityOperation> {

	private static final long serialVersionUID = -418890474385890182L;
	private static final Logger logger = Logger.getLogger(SecurityOperationListCtrl.class);

	protected Window window_SecurityOperationList;
	protected Borderlayout borderLayout_SecurityOperationList;
	protected Paging pagingSecurityOperationList;
	protected Listbox listBoxSecurityOperation;

	protected Listheader listheader_OprCode;
	protected Listheader listheader_OprDesc;

	protected Button button_SecurityOperationList_NewSecurityOperation;
	protected Button button_SecurityOperationList_SecurityOperationSearchDialog;

	protected Textbox oprCode;
	protected Listbox sortOperator_oprCode;
	protected Textbox oprDesc;
	protected Listbox sortOperator_oprDesc;
	protected Label label_SecurityOperationSearch_RecordStatus;
	protected Label label_SecurityOperationSearch_RecordType;
	protected Label label_SecurityOperationSearchResult;

	private transient SecurityOperationService securityOperationService;

	/**
	 * default constructor.<br>
	 */
	public SecurityOperationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SecurityOperation";
		super.pageRightName = "SecurityOperationList";
		super.tableName = "SecOperations_View";
		super.queueTableName = "SecOperations_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SecurityOperationList(Event event){
		// Set the page level components.
		setPageComponents(window_SecurityOperationList, borderLayout_SecurityOperationList, listBoxSecurityOperation,
				pagingSecurityOperationList);
		setItemRender(new SecurityOperationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SecurityOperationList_NewSecurityOperation,
				"button_SecurityOperationList_NewSecurityOperation", true);
		registerButton(button_SecurityOperationList_SecurityOperationSearchDialog);
		registerField("oprID", SortOrder.ASC);
		registerField("oprCode", listheader_OprCode, SortOrder.NONE, oprCode, sortOperator_oprCode, Operators.STRING);
		registerField("oprDesc", listheader_OprDesc, SortOrder.NONE, oprDesc, sortOperator_oprDesc, Operators.STRING);

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
	public void onClick$button_SecurityOperationList_SecurityOperationSearchDialog(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSecurityOperationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSecurityOperation.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		SecurityOperation aSecurityOperation = new SecurityOperation();

		if (enqiryModule) {
			aSecurityOperation = securityOperationService.getApprovedSecurityOperationById(id);
		} else {
			aSecurityOperation = securityOperationService.getSecurityOperationById(id);
		}

		if (aSecurityOperation == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND OprId=" + aSecurityOperation.getOprID() + " AND version="
				+ aSecurityOperation.getVersion() + " ";

		if (doCheckAuthority(aSecurityOperation, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aSecurityOperation.getWorkflowId() == 0) {
				aSecurityOperation.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aSecurityOperation);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onClick$button_SecurityOperationList_NewSecurityOperation(Event event) {
		logger.debug(event.toString());

		// Create a new entity.
		SecurityOperation aSecurityOperation = new SecurityOperation();
		aSecurityOperation.setNewRecord(true);
		aSecurityOperation.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aSecurityOperation);

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSecurityOperation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SecurityOperation aSecurityOperation) {
		logger.debug("Entering");

		Map<String, Object> aruments = getDefaultArguments();
		aruments.put("SecurityOperation", aSecurityOperation);
		aruments.put("securityOperationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityOperations/SecurityOperationDialog.zul",
					null, aruments);
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

	public void setSecurityOperationService(SecurityOperationService securityOperationService) {
		this.securityOperationService = securityOperationService;
	}
}