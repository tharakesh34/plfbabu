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
 * FileName    		:  AccountTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.rmtmasters.accounttype;

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

import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.rmtmasters.AccountTypeService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.rmtmasters.accounttype.model.AccountTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/AccountType/AccountTypeList.zul file.
 */
public class AccountTypeListCtrl extends GFCBaseListCtrl<AccountType> {
	private static final long serialVersionUID = -1631313247095254648L;
	private static final Logger logger = Logger.getLogger(AccountTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AccountTypeList;
	protected Borderlayout borderLayout_AccountTypeList;
	protected Paging pagingAccountTypeList;
	protected Listbox listBoxAccountType;

	protected Listheader listheader_AcType;
	protected Listheader listheader_AcTypeDesc;
	protected Listheader listheader_AcPurpose;
	protected Listheader listheader_IsInternalAc;
	protected Listheader listheader_AcTypeIsActive;
	protected Listheader listheader_AcHead;
	protected Listheader listheader_IsCustSysAccount;

	protected Button button_AccountTypeList_NewAccountType;
	protected Button button_AccountTypeList_AccountTypeSearchDialog;

	protected Textbox acType;
	protected Textbox acTypeDesc;
	protected Combobox acPurpose;
	protected Checkbox internalAc;
	protected Checkbox acTypeIsActive;
	protected Checkbox isCustSysAccount;

	protected Listbox sortOperator_acType;
	protected Listbox sortOperator_acTypeDesc;
	protected Listbox sortOperator_acPurpose;
	protected Listbox sortOperator_internalAc;
	protected Listbox sortOperator_acTypeIsActive;
	protected Listbox sortOperator_isCustSysAccount;

	private transient AccountTypeService accountTypeService;

	/**
	 * default constructor.<br>
	 */
	public AccountTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AccountType";
		super.pageRightName = "AccountTypeList";
		super.tableName = "RMTAccountTypes_AView";
		super.queueTableName = "RMTAccountTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_AccountTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_AccountTypeList, borderLayout_AccountTypeList, listBoxAccountType,
				pagingAccountTypeList);
		setItemRender(new AccountTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AccountTypeList_NewAccountType, "button_AccountTypeList_NewAccountType", true);
		registerButton(button_AccountTypeList_AccountTypeSearchDialog);

		fillComboBox(this.acPurpose, "", PennantStaticListUtil.getAccountPurpose(), "");
		
		registerField("acType", listheader_AcType, SortOrder.ASC, acType, sortOperator_acType, Operators.STRING);
		registerField("acTypeDesc", listheader_AcTypeDesc, SortOrder.NONE, acTypeDesc, sortOperator_acTypeDesc,
				Operators.STRING);
		registerField("acPurpose", listheader_AcPurpose, SortOrder.NONE, acPurpose, sortOperator_acPurpose,
				Operators.STRING);
		registerField("internalAc", listheader_IsInternalAc, SortOrder.NONE, internalAc, sortOperator_internalAc,
				Operators.BOOLEAN);
		registerField("custSysAc", listheader_IsCustSysAccount, SortOrder.NONE, isCustSysAccount,
				sortOperator_isCustSysAccount, Operators.BOOLEAN);
		registerField("acTypeIsActive", listheader_AcTypeIsActive, SortOrder.NONE, acTypeIsActive,
				sortOperator_acTypeIsActive, Operators.BOOLEAN);
		
		doSetFieldProperties();
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
	public void onClick$button_AccountTypeList_AccountTypeSearchDialog(Event event) {
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
	public void onClick$button_AccountTypeList_NewAccountType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		AccountType accountType = new AccountType();
		accountType.setNewRecord(true);
		accountType.setWorkflowId(getWorkFlowId());

		if (event.getData() != null) {
			AccountType type = (AccountType) event.getData();
			setObjectData(accountType, type);
		}

		// Display the dialog page.
		doShowDialogPage(accountType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onAccountTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAccountType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		AccountType accountType = accountTypeService.getAccountTypeById(id);

		if (accountType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND AcType='" + accountType.getAcType() + "' AND version=" + accountType.getVersion()
				+ " ";

		if (doCheckAuthority(accountType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && accountType.getWorkflowId() == 0) {
				accountType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(accountType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	private AccountType setObjectData(AccountType aAccountType, AccountType type) {
		logger.debug("Entering");

		aAccountType.setAcPurpose(type.getAcPurpose());
		aAccountType.setInternalAc(type.isInternalAc());
		aAccountType.setCustSysAc(type.isCustSysAc());
		aAccountType.setAcTypeIsActive(type.isAcTypeIsActive());
		aAccountType.setAssertOrLiability(type.getAssertOrLiability());
		logger.debug("Leaving");

		return aAccountType;
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aAccountType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AccountType aAccountType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("accountType", aAccountType);
		arg.put("accountTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/AccountType/AccountTypeDialog.zul", null, arg);
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
	
	private void doSetFieldProperties() {
		this.acType.setMaxlength(14);
		
	}

	public void setAccountTypeService(AccountTypeService accountTypeService) {
		this.accountTypeService = accountTypeService;
	}
}