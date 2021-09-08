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
package com.pennant.webui.finance.financemain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/QueueAssignment/ReleaseLockList.zul file.
 */
public class ReleaseLockListCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = LogManager.getLogger(ReleaseLockListCtrl.class);

	protected Window window_ReleaseLockList;
	protected Button button_ReleaseLockList_Search;
	protected Borderlayout borderLayout_ReleaseLockList;
	protected Paging pagingReleaseLockList;

	protected Listbox sortOperator_finReference;
	protected Textbox finReference;

	protected Listbox listBoxReleaseLock;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_LoginId;
	protected Listheader listheader_PhoneNo;

	private transient FinanceDetailService financeDetailService;

	/**
	 * The default constructor.
	 */
	public ReleaseLockListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReleaseLock";
		super.pageRightName = "ReleaseLock";
		super.tableName = "LockedFinances_View";
		super.queueTableName = "LockedFinances_View";
		super.enquiryTableName = "LockedFinances_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReleaseLockList(Event event) {
		// Set the page level components.
		setPageComponents(window_ReleaseLockList, borderLayout_ReleaseLockList, listBoxReleaseLock,
				pagingReleaseLockList);
		setItemRender(new ReleaseLockListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ReleaseLockList_Search);

		registerField("FinReference", listheader_FinReference, SortOrder.ASC, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("NextUsrName", listheader_LoginId, SortOrder.NONE);
		registerField("LovValue", listheader_PhoneNo, SortOrder.NONE);

		// Render the page and display the data.
		doRenderPage();

		// rendering the list page data required or not.
		if (renderListOnLoad) {
			search();
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ReleaseLockList_Search(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onReleaseLockItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = listBoxReleaseLock.getSelectedItem();

		// Get the selected entity.
		long finID = (Long) selectedItem.getAttribute("finID");
		String id = (String) selectedItem.getAttribute("id");

		if (MessageUtil.NO == MessageUtil
				.confirm(Labels.getLabel("message.Question.unlock_record", new String[] { id }))) {
			return;
		}

		// Unlock the record after confirmation.
		financeDetailService.updateNextUserId(finID, null);
		MessageUtil.showMessage(Labels.getLabel("message.Information.unlock_record", new String[] { id }));

		// Refresh the list.
		search();

		logger.debug("Leaving");
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

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}
