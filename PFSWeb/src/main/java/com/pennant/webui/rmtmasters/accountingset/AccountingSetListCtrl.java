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
 * * FileName : AccountingSetListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * *
 * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.accountingset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.rmtmasters.accountingset.model.AccountingSetListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * This is the controller class for the /WEB-INF/pages/RulesFactory/AccountingSet/AccountingSetList.zul file.
 */
public class AccountingSetListCtrl extends GFCBaseListCtrl<AccountingSet> {
	private static final long serialVersionUID = 4322539879503951300L;

	protected Window window_AccountingSetList;
	protected Borderlayout borderLayout_AccountingSetList;
	protected Paging pagingAccountingSetList;
	protected Listbox listBoxAccountingSet;

	protected Listheader listheader_EventCode;
	protected Listheader listheader_EventDesc;
	protected Listheader listheader_AccountSetCode;
	protected Listheader listheader_AccountSetCodeName;

	protected Button button_AccountingSetList_NewAccountingSet;
	protected Button button_AccountingSetList_AccountingSetSearchDialog;

	protected Textbox eventCode;
	protected Textbox accountSetCode;
	protected Textbox accountSetCodeName;
	protected Textbox accountEventDesc;

	protected Listbox sortOperator_accountEventDesc;
	protected Listbox sortOperator_eventCode;
	protected Listbox sortOperator_accountSetCode;
	protected Listbox sortOperator_accountSetCodeName;

	private transient AccountingSetService accountingSetService;

	/**
	 * default constructor.<br>
	 */
	public AccountingSetListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		this.searchObject.addFilterNotIn("EventCode", AccountingEvent.getExcludedAccEvents());
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AccountingSet";
		super.pageRightName = "AccountingSetList";
		super.tableName = "RMTAccountingSet_AView";
		super.queueTableName = "RMTAccountingSet_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AccountingSetList(Event event) {
		// Set the page level components.
		setPageComponents(window_AccountingSetList, borderLayout_AccountingSetList, listBoxAccountingSet,
				pagingAccountingSetList);
		setItemRender(new AccountingSetListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AccountingSetList_NewAccountingSet, "button_AccountingSetList_NewAccountingSet", true);
		registerButton(button_AccountingSetList_NewAccountingSet);

		registerField("accountSetid");
		registerField("eventCode", listheader_EventCode, SortOrder.ASC, eventCode, sortOperator_eventCode,
				Operators.STRING);
		registerField("accountSetCode", listheader_AccountSetCode, SortOrder.NONE, accountSetCode,
				sortOperator_accountSetCode, Operators.STRING);
		registerField("lovDescEventCodeName", listheader_EventDesc, SortOrder.NONE, accountEventDesc,
				sortOperator_accountEventDesc, Operators.STRING);
		registerField("accountSetCodeName", listheader_AccountSetCodeName, SortOrder.NONE, accountSetCodeName,
				sortOperator_accountSetCodeName, Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AccountingSetList_AccountingSetSearchDialog(Event event) {
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
	public void onClick$button_AccountingSetList_NewAccountingSet(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		AccountingSet aAccountingSet = new AccountingSet();
		aAccountingSet.setNewRecord(true);
		aAccountingSet.setWorkflowId(getWorkFlowId());

		if (event.getData() != null) {
			copyData(aAccountingSet, (AccountingSet) event.getData());
		}

		// Display the dialog page.
		doShowDialogPage(aAccountingSet);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onAccountingSetItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAccountingSet.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		AccountingSet aAccountingSet = accountingSetService.getAccountingSetById(id);

		if (aAccountingSet == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where AccountSetid=?";

		if (doCheckAuthority(aAccountingSet, whereCond, new Object[] { aAccountingSet.getAccountSetid() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aAccountingSet.getWorkflowId() == 0) {
				aAccountingSet.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aAccountingSet);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param accountingSet The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AccountingSet accountingSet) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("accountingSet", accountingSet);
		arg.put("accountingSetListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/AccountingSet/AccountingSetDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

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

	private AccountingSet copyData(AccountingSet newAcset, AccountingSet sourceSet) {
		newAcset.setEventCode(sourceSet.getEventCode());
		newAcset.setLovDescEventCodeName(sourceSet.getLovDescEventCodeName());
		List<TransactionEntry> transactionEntries = new ArrayList<TransactionEntry>();

		for (TransactionEntry sourTransEntry : sourceSet.getTransactionEntries()) {
			TransactionEntry trgTransEntry = accountingSetService.getNewTransactionEntry();

			trgTransEntry.setAccountSetid(sourTransEntry.getAccountSetid());
			trgTransEntry.setTransOrder(sourTransEntry.getTransOrder());
			trgTransEntry.setTransDesc(sourTransEntry.getTransDesc());
			trgTransEntry.setDebitcredit(sourTransEntry.getDebitcredit());
			trgTransEntry.setShadowPosting(sourTransEntry.isShadowPosting());
			trgTransEntry.setAccount(sourTransEntry.getAccount());
			trgTransEntry.setAccountType(sourTransEntry.getAccountType());
			trgTransEntry.setAccountBranch(sourTransEntry.getAccountBranch());
			trgTransEntry.setLovDescAccountTypeName(sourTransEntry.getLovDescAccountTypeName());
			trgTransEntry.setLovDescAccountBranchName(sourTransEntry.getLovDescAccountBranchName());
			trgTransEntry.setAccountSubHeadRule(sourTransEntry.getAccountSubHeadRule());
			trgTransEntry.setLovDescAccountSubHeadRuleName(sourTransEntry.getLovDescAccountSubHeadRuleName());
			trgTransEntry.setTranscationCode(sourTransEntry.getTranscationCode());
			trgTransEntry.setLovDescTranscationCodeName(sourTransEntry.getLovDescTranscationCodeName());
			trgTransEntry.setRvsTransactionCode(sourTransEntry.getRvsTransactionCode());
			trgTransEntry.setLovDescRvsTransactionCodeName(sourTransEntry.getLovDescRvsTransactionCodeName());
			trgTransEntry.setAmountRule(sourTransEntry.getAmountRule());
			trgTransEntry.setFeeCode(sourTransEntry.getFeeCode());
			// trgTransEntry.setRuleDecider(sourTransEntry.getRuleDecider());
			trgTransEntry.setLovDescFeeCodeName(sourTransEntry.getLovDescFeeCodeName());
			trgTransEntry.setLovDescEventCodeName(sourTransEntry.getLovDescEventCodeName());
			trgTransEntry.setLovDescEventCodeDesc(sourTransEntry.getLovDescEventCodeDesc());
			trgTransEntry.setLovDescAccSetCodeName(sourTransEntry.getLovDescAccSetCodeName());
			trgTransEntry.setLovDescAccSetCodeDesc(sourTransEntry.getLovDescAccSetCodeDesc());
			trgTransEntry.setLovDescSysInAcTypeName(sourTransEntry.getLovDescSysInAcTypeName());

			trgTransEntry.setAccountBranch(sourTransEntry.getAccountBranch());
			trgTransEntry.setVersion(1);
			trgTransEntry.setRecordType(PennantConstants.RCD_ADD);

			// Prepare List Of Entries
			transactionEntries.add(trgTransEntry);
		}
		newAcset.setTransactionEntries(transactionEntries);
		return newAcset;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}
}