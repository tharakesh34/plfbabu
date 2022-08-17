package com.pennant.webui.systemmasters.transactionmapping;

/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : VehicleDealerListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-09-2011 * *
 * Modified Date : 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * 01-05-2018 Vinay 0.2 Module code added for individual module * * * * * * * *
 ********************************************************************************************
 */

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.service.transactionmapping.TransactionMappingService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.cd.model.TransactionMapping;
import com.pennanttech.pff.transactionmapping.webui.TransactionMappingListModelItemRenderer;

public class TransactionMappingListCtrl extends GFCBaseListCtrl<TransactionMapping> {
	private static final long serialVersionUID = 259921702952389829L;
	private static final Logger logger = LogManager.getLogger(TransactionMappingListCtrl.class);

	protected Window window_TransactionMappingList;
	protected Borderlayout borderLayout_TransactionMappingList;
	protected Paging pagingTransactionMappingList;
	protected Listbox listBoxTransactionMapping;

	protected Listheader listheader_POSId;
	protected Listheader listheader_DealerCode;
	protected Listheader listheader_DealerName;
	protected Listheader listheader_MID;
	protected Listheader listheader_TID;
	protected Listheader listheader_Active;

	protected Button button_TransactionMappingList_NewTransactionMapping;
	protected Button button_TransactionMappingList_TransactionMappingSearchDialog;

	protected Textbox posId;
	protected Textbox dealerCode;
	protected Textbox dealerName;
	protected Textbox mid;
	protected Textbox tid;
	protected Checkbox active;

	protected Listbox sortOperator_dealerCode;
	protected Listbox sortOperator_posId;
	protected Listbox sortOperator_dealerName;
	protected Listbox sortOperator_MID;
	protected Listbox sortOperator_TID;
	protected Listbox sortOperator_Active;

	private transient TransactionMappingService transactionMappingService;

	/**
	 * default constructor.<br>
	 */
	public TransactionMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "TransactionMapping";
		super.pageRightName = "TransactionMappingList";
		super.tableName = "TransactionMapping_VIEW";
		super.queueTableName = "TransactionMapping_VIEW";
		super.enquiryTableName = "TransactionMapping_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_TransactionMappingList(Event event) {
		// Set the page level components.
		setPageComponents(window_TransactionMappingList, borderLayout_TransactionMappingList, listBoxTransactionMapping,
				pagingTransactionMappingList);
		setItemRender(new TransactionMappingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_TransactionMappingList_NewTransactionMapping,
				"button_TransactionMappingList_NewTransactionMapping", true);
		registerButton(button_TransactionMappingList_TransactionMappingSearchDialog);
		registerField("posId", listheader_POSId, SortOrder.NONE, posId, sortOperator_posId, Operators.NUMERIC);
		registerField("dealerCode", listheader_DealerCode, SortOrder.NONE, dealerCode, sortOperator_dealerCode,
				Operators.NUMERIC);
		registerField("dealerName", listheader_DealerName, SortOrder.ASC, dealerName, sortOperator_dealerName,
				Operators.STRING);
		registerField("mid", listheader_MID, SortOrder.NONE, mid, sortOperator_MID, Operators.NUMERIC);
		registerField("tid", listheader_TID, SortOrder.NONE, tid, sortOperator_TID, Operators.NUMERIC);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);
		registerField("id");

		doSetFieldProperties();
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_TransactionMappingList_TransactionMappingSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_TransactionMappingList_NewTransactionMapping(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		TransactionMapping transactionMapping = new TransactionMapping();
		transactionMapping.setNewRecord(true);
		transactionMapping.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(transactionMapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onTransactionMappingItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxTransactionMapping.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		TransactionMapping transactionMapping = transactionMappingService.getTransactionMappingById(id);

		if (transactionMapping == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where POSId=?";
		if (doCheckAuthority(transactionMapping, whereCond, new Object[] { transactionMapping.getPosId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && transactionMapping.getWorkflowId() == 0) {
				transactionMapping.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(transactionMapping);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param transactionMapping The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TransactionMapping transactionMapping) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("transactionMapping", transactionMapping);
		arg.put("transactionMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/TransactionMapping/TransactionMappingDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.dealerName.setMaxlength(100);
		this.recordStatus.setMaxlength(50);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setTransactionMappingService(TransactionMappingService transactionMappingService) {
		this.transactionMappingService = transactionMappingService;
	}

}
