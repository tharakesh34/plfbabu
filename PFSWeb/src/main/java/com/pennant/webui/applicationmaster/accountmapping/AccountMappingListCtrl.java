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
 * * FileName : AccountMappingListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.accountmapping;

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
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.pff.extension.AccountingExtension;
import com.pennant.webui.applicationmaster.accountmapping.model.AccountMappingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.applicationmaster/AccountMapping/AccountMappingList.zul file.
 * 
 */
public class AccountMappingListCtrl extends GFCBaseListCtrl<AccountMapping> {
	private static final long serialVersionUID = 1L;

	protected Window window_AccountMappingList;
	protected Borderlayout borderLayout_AccountMappingList;
	protected Paging pagingAccountMappingList;
	protected Listbox listBoxAccountMapping;

	// List headers
	protected Listheader listheader_AccountType;
	protected Listheader listheader_Account;
	protected Listheader listheader_HostAccount;
	// checkRights
	protected Button button_AccountMappingList_NewAccountMapping;
	protected Button button_AccountMappingList_AccountMappingSearch;

	// Search Fields
	protected Textbox accountType;
	protected Textbox account;
	protected Textbox hostAccount;
	protected Radio loanTypeAc;
	protected Radio normalAc;

	protected Listbox sortOperator_accountType;
	protected Listbox sortOperator_account;
	protected Listbox sortOperator_hostAccount;

	private transient AccountMappingService accountMappingService;

	/**
	 * default constructor.<br>
	 */
	public AccountMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AccountMapping";
		super.pageRightName = "AccountMappingList";
		super.tableName = "AccountMapping_AView";
		super.queueTableName = "AccountMapping_View";
		super.enquiryTableName = "AccountMapping_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AccountMappingList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AccountMappingList, borderLayout_AccountMappingList, listBoxAccountMapping,
				pagingAccountMappingList);
		setItemRender(new AccountMappingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AccountMappingList_AccountMappingSearch);
		registerButton(button_AccountMappingList_NewAccountMapping, "button_AccountMappingList_NewAccountMapping",
				true);

		registerField("accountType", listheader_AccountType, SortOrder.ASC, accountType, sortOperator_accountType,
				Operators.STRING);
		registerField("account", listheader_Account, SortOrder.NONE, account, sortOperator_account, Operators.STRING);
		registerField("hostAccount", listheader_HostAccount, SortOrder.NONE, hostAccount, sortOperator_hostAccount,
				Operators.STRING);

		if (AccountingExtension.LOAN_TYPE_GL_MAPPING && AccountingExtension.NORMAL_GL_MAPPING) {
			loanTypeAc.setVisible(true);
			normalAc.setVisible(true);
		} else if (AccountingExtension.LOAN_TYPE_GL_MAPPING) {
			loanTypeAc.setVisible(false);
			loanTypeAc.setSelected(true);
			normalAc.setVisible(false);
		} else if (AccountingExtension.NORMAL_GL_MAPPING) {
			normalAc.setVisible(false);
			normalAc.setSelected(true);
			loanTypeAc.setVisible(false);
		}

		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AccountMappingList_AccountMappingSearch(Event event) {
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
	public void onClick$button_AccountMappingList_NewAccountMapping(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		AccountMapping accountmapping = new AccountMapping();
		accountmapping.setNewRecord(true);
		accountmapping.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(accountmapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAccountMappingItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAccountMapping.getSelectedItem();
		final String account = (String) selectedItem.getAttribute("account");
		AccountMapping accountmapping = accountMappingService.getAccountMapping(account);

		if (accountmapping == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Account =?");

		if (doCheckAuthority(accountmapping, whereCond.toString(), new Object[] { accountmapping.getAccount() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && accountmapping.getWorkflowId() == 0) {
				accountmapping.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(accountmapping);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param accountmapping The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AccountMapping accountmapping) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("accountmapping", accountmapping);
		arg.put("accountmappingListCtrl", this);
		if (!accountmapping.isNewRecord()) {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/AccountMapping/NormAccountMappingDialog.zul",
					null, arg);
		} else {
			try {
				if (loanTypeAc.isChecked()) {
					Executions.createComponents(
							"/WEB-INF/pages/ApplicationMaster/AccountMapping/AccountMappingDialog.zul", null, arg);
				} else if (normalAc.isChecked()) {
					Executions.createComponents(
							"/WEB-INF/pages/ApplicationMaster/AccountMapping/NormAccountMappingDialog.zul", null, arg);
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

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

	public void setAccountMappingService(AccountMappingService accountMappingService) {
		this.accountMappingService = accountMappingService;
	}
}