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
 * FileName    		:  TransactionCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.transactioncode;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.applicationmaster.transactioncode.model.TransactionCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/TransactionCode/TransactionCodeList.zul file.
 */
public class TransactionCodeListCtrl extends GFCBaseListCtrl<TransactionCode> {
	private static final long serialVersionUID = 8484399111058985206L;
	private static final Logger logger = Logger.getLogger(TransactionCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TransactionCodeList;
	protected Borderlayout borderLayout_TransactionCodeList;
	protected Paging pagingTransactionCodeList;
	protected Listbox listBoxTransactionCode;

	protected Textbox tranCode;
	protected Textbox tranDesc;
	protected Combobox tranType;
	protected Checkbox tranIsActive;

	protected Listbox sortOperator_tranCode;
	protected Listbox sortOperator_tranDesc;
	protected Listbox sortOperator_tranType;
	protected Listbox sortOperator_tranIsActive;

	// List headers
	protected Listheader listheader_TranCode;
	protected Listheader listheader_TranDesc;
	protected Listheader listheader_TranType;
	protected Listheader listheader_TranIsActive;

	// checkRights
	protected Button button_TransactionCodeList_NewTransactionCode;
	protected Button button_TransactionCodeList_TransactionCodeSearchDialog;

	private transient TransactionCodeService transactionCodeService;

	/**
	 * default constructor.<br>
	 */
	public TransactionCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "TransactionCode";
		super.pageRightName = "TransactionCodeList";
		super.tableName = "BMTTransactionCode_AView";
		super.queueTableName = "BMTTransactionCode_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_TransactionCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_TransactionCodeList, borderLayout_TransactionCodeList, listBoxTransactionCode,
				pagingTransactionCodeList);
		setItemRender(new TransactionCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_TransactionCodeList_NewTransactionCode, "button_TransactionCodeList_NewTransactionCode",
				true);
		registerButton(button_TransactionCodeList_TransactionCodeSearchDialog);

		fillComboBox(this.tranType, "", PennantStaticListUtil.getTranTypeBoth(), "");

		registerField("tranCode", listheader_TranCode, SortOrder.ASC, tranCode, sortOperator_tranCode, Operators.STRING);
		registerField("tranDesc", listheader_TranDesc, SortOrder.NONE, tranDesc, sortOperator_tranDesc,
				Operators.STRING);
		registerField("tranType", listheader_TranType, SortOrder.NONE, tranType, sortOperator_tranType,
				Operators.STRING);
		registerField("tranIsActive", listheader_TranIsActive, SortOrder.NONE, tranIsActive, sortOperator_tranIsActive,
				Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_TransactionCodeList_TransactionCodeSearchDialog(Event event) {
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
	public void onClick$button_TransactionCodeList_NewTransactionCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		TransactionCode transactionCode = new TransactionCode();
		transactionCode.setNewRecord(true);
		transactionCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(transactionCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onTransactionCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxTransactionCode.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		TransactionCode transactionCode = transactionCodeService.getTransactionCodeById(id);

		if (transactionCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		// Check whether the user has authority to change/view the record.
		String whereCond = " AND TranCode='" + transactionCode.getTranCode() + "' AND version="
				+ transactionCode.getVersion() + " ";

		if (doCheckAuthority(transactionCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && transactionCode.getWorkflowId() == 0) {
				transactionCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(transactionCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aTransactionCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TransactionCode aTransactionCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("transactionCode", aTransactionCode);
		arg.put("transactionCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/TransactionCode/TransactionCodeDialog.zul",
					null, arg);
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

	public void setTransactionCodeService(TransactionCodeService transactionCodeService) {
		this.transactionCodeService = transactionCodeService;
	}
}