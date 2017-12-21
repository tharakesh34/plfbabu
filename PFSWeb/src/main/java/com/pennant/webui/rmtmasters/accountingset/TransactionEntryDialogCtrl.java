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
 * FileName    		:  TransactionEntryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.rmtmasters.accountingset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AmountCode;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the /WEB-INF/pages/RulesFactory/TransactionEntry/transactionEntryDialog.zul file.
 */
public class TransactionEntryDialogCtrl extends GFCBaseCtrl<TransactionEntry> {
	private static final long							serialVersionUID	= 4345607610334573882L;
	private static final Logger							logger				= Logger.getLogger(TransactionEntryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window									window_TransactionEntryDialog;												// autowired
	protected Intbox									transOrder;																// autowired
	protected Textbox									transDesc;																	// autowired
	protected Combobox									debitcredit;																// autowired
	protected Checkbox									shadowPosting;																// autowired
	protected Combobox									account;																	// autowired
	protected Textbox									accountType;																// autowired
	protected Textbox									accountBranch;																// autowired
	protected Textbox									accountSubHeadRule;														// autowired
	protected Textbox									transcationCode;															// autowired
	protected Textbox									rvsTransactionCode;														// autowired
	protected Codemirror								amountRule;																// autowired
	protected Textbox									eventCode;																	// autowired
	protected Textbox									accountSetCode;															// autowired
	protected Textbox									accountSetCodeName;														// autowired
	protected Textbox									lovDescEventCodeName;														// autowired
	protected Checkbox									entryByInvestment;															// autowired
	protected Label										label_TransactionEntryDialog_EntryByInvestment;
	protected Hbox										hbox_entryByInvestment;													// autowired
	protected Checkbox									openNewFinAc;																// autowired
	protected Row										row_OpenNewFinAc;															// autowired

	protected Label										label_TransactionEntryDialog_PostToCore;
	protected Hbox										hbox_PostToCore;

	protected Radio										postToCore;																// autowired
	protected Radio										postToERP;																	// autowired

	protected Row										row_Account;																// autowired

	// not auto wired vars
	private TransactionEntry							transactionEntry;															// overhanded per param

	private transient boolean							validationOn;

	protected Button									btnSearchAccountType;														// autowire
	protected Textbox									lovDescAccountTypeName;

	protected Button									btnSearchAccountBranch;													// autowire
	protected Textbox									lovDescAccountBranchName;

	protected Button									btnSearchAccountSubHeadRule;												// autowire
	protected Textbox									lovDescAccountSubHeadRuleName;

	protected Button									btnSearchTranscationCode;													// autowire
	protected Textbox									lovDescTranscationCodeName;

	protected Button									btnSearchRvsTransactionCode;												// autowire
	protected Textbox									lovDescRvsTransactionCodeName;

	// ServiceDAOs / Domain Classes
	private transient PagedListService					pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>>	overideMap			= new HashMap<String, ArrayList<ErrorDetails>>();

	private transient AccountingSetService				accountingSetService;
	private transient AccountingSetDialogCtrl			accountingSetDialogCtrl;
	private List<TransactionEntry>						transactionEntryList;

	protected Listbox									amountCodeListbox;															// auto wired
	protected Listbox									feeCodeListbox;															// auto wired
	protected Listbox									operator;																	// auto wired
	protected Button									btnCopyTo;
	protected Tab										tab_Fee;

	protected Grid										grid_Basicdetails;
	protected Column									column_CustomerData;
	protected Column									column_RULE;
	protected Column									column_Operators;
	protected Button									btnSimulate;
	protected Button									btnValidate;
	JSONArray											variables			= new JSONArray();
	HashSet<String>										amountcodes			= new HashSet<String>();
	protected Button									btnSearchSystemIntAccount;

	protected Space										spAccountType;
	protected Space										spSubHead;

	protected Radiogroup								chargeType;

	//protected Combobox ruleDecider;
	protected Groupbox									gb_RuleCode;
	private String										userRole			= "";
	private boolean                                     isGSTApplicable       = false;
	/**
	 * default constructor.<br>
	 */
	public TransactionEntryDialogCtrl() {
		super();

	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TransactionEntryDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected TransactionEntry object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TransactionEntryDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_TransactionEntryDialog);

		// READ OVERHANDED params !
		// we get the transactionEntryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete transactionEntry here.
		if (arguments.containsKey("accountingSetDialogCtrl")) {
			setAccountingSetDialogCtrl((AccountingSetDialogCtrl) arguments.get("accountingSetDialogCtrl"));
		} else {
			setAccountingSetDialogCtrl(null);
		}

		if (arguments.containsKey("transactionEntry")) {
			this.transactionEntry = (TransactionEntry) arguments.get("transactionEntry");
			TransactionEntry befImage = new TransactionEntry();
			BeanUtils.copyProperties(this.transactionEntry, befImage);
			this.transactionEntry.setBefImage(befImage);
			setTransactionEntry(this.transactionEntry);
		} else {
			setTransactionEntry(null);
		}

		this.transactionEntry.setWorkflowId(0);

		if (arguments.containsKey("role")) {
			userRole = (String) arguments.get("role");
			getUserWorkspace().allocateRoleAuthorities(userRole, super.pageRightName);
		}

		doLoadWorkFlow(this.transactionEntry.isWorkflow(), this.transactionEntry.getWorkflowId(),
				this.transactionEntry.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "TransactionEntryDialog");
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		getBorderLayoutHeight();
		int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 115;
		int listboxHeight = borderLayoutHeight - dialogHeight;
		this.amountCodeListbox.setHeight((listboxHeight + 15) + "px");
		this.feeCodeListbox.setHeight((listboxHeight + 15) + "px");
		this.amountRule.setHeight((listboxHeight + 40) + "px");
		this.operator.setHeight((listboxHeight + 15) + "px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getTransactionEntry());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.transOrder.setMaxlength(3);
		this.transDesc.setMaxlength(50);
		this.accountType.setMaxlength(15);
		this.accountBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.accountSubHeadRule.setMaxlength(8);
		this.transcationCode.setMaxlength(8);
		this.rvsTransactionCode.setMaxlength(8);

		if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_AHB)) {
			this.label_TransactionEntryDialog_PostToCore.setVisible(false);
			this.hbox_PostToCore.setVisible(false);
			this.row_Account.setVisible(false);
			this.postToCore.setChecked(true);
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(super.pageRightName, userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnValidateSave(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event)) {
			doSave();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_TransactionEntryDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if (doClose(this.btnSave.isVisible())) {
			getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(true);
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.transactionEntry.getBefImage());
		doReadOnly();

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aTransactionEntry
	 *            TransactionEntry
	 */
	public void doWriteBeanToComponents(TransactionEntry aTransactionEntry) {
		logger.debug("Entering");

		fillComboBox(debitcredit, aTransactionEntry.getDebitcredit(), PennantStaticListUtil.getTranType(), "");
		dofillAccount(this.account, aTransactionEntry.getAccount());
		doFillAccountType(aTransactionEntry.getAccount());
		doFillChargeType(this.chargeType, aTransactionEntry.getChargeType());
		this.eventCode.setValue(aTransactionEntry.getLovDescEventCodeName());
		this.lovDescEventCodeName.setValue(aTransactionEntry.getLovDescEventCodeDesc());
		this.accountSetCode.setValue(aTransactionEntry.getLovDescAccSetCodeName());
		this.accountSetCodeName.setValue(aTransactionEntry.getLovDescAccSetCodeDesc());
		this.transOrder.setValue(aTransactionEntry.getTransOrder());
		this.transDesc.setValue(aTransactionEntry.getTransDesc());
		this.shadowPosting.setChecked(aTransactionEntry.isShadowPosting());
		this.entryByInvestment.setChecked(aTransactionEntry.isEntryByInvestment());
		this.openNewFinAc.setChecked(aTransactionEntry.isOpenNewFinAc());
		this.accountType.setValue(aTransactionEntry.getAccountType());
		this.accountBranch.setValue(aTransactionEntry.getAccountBranch());
		this.accountSubHeadRule.setValue(aTransactionEntry.getAccountSubHeadRule());

		// Amount Rule Formula Set to CodeMirror
		this.amountRule.setValue(aTransactionEntry.getAmountRule());

		this.transcationCode.setValue(aTransactionEntry.getTranscationCode());
		this.rvsTransactionCode.setValue(aTransactionEntry.getRvsTransactionCode());

		if (aTransactionEntry.getLovDescTranscationCodeName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescTranscationCodeName())) {
			this.lovDescTranscationCodeName
					.setValue(aTransactionEntry.getLovDescTranscationCodeName().contains("-") ? aTransactionEntry
							.getLovDescTranscationCodeName() : aTransactionEntry.getTranscationCode() + "-"
							+ aTransactionEntry.getLovDescTranscationCodeName());
		} else {
			this.lovDescTranscationCodeName.setValue("");
		}
		if (aTransactionEntry.getLovDescRvsTransactionCodeName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescRvsTransactionCodeName())) {
			this.lovDescRvsTransactionCodeName.setValue(aTransactionEntry.getLovDescRvsTransactionCodeName().contains(
					"-") ? aTransactionEntry.getLovDescRvsTransactionCodeName() : aTransactionEntry
					.getRvsTransactionCode() + "-" + aTransactionEntry.getLovDescRvsTransactionCodeName());
		} else {
			this.lovDescRvsTransactionCodeName.setValue("");
		}

		if (aTransactionEntry.getLovDescAccountTypeName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescAccountTypeName())) {
			this.lovDescAccountTypeName
					.setValue(aTransactionEntry.getLovDescAccountTypeName().contains("-") ? aTransactionEntry
							.getLovDescAccountTypeName() : aTransactionEntry.getAccountType() + "-"
							+ aTransactionEntry.getLovDescAccountTypeName());
		} else if (aTransactionEntry.getLovDescSysInAcTypeName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescSysInAcTypeName())) {
			this.lovDescAccountTypeName
					.setValue(aTransactionEntry.getLovDescSysInAcTypeName().contains("-") ? aTransactionEntry
							.getLovDescSysInAcTypeName() : aTransactionEntry.getAccountType() + "-"
							+ aTransactionEntry.getLovDescSysInAcTypeName());
		} else {
			this.lovDescAccountTypeName.setValue("");
		}

		if (aTransactionEntry.getLovDescAccountBranchName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescAccountBranchName())) {
			this.lovDescAccountBranchName
					.setValue(aTransactionEntry.getLovDescAccountBranchName().contains("-") ? aTransactionEntry
							.getLovDescAccountBranchName() : aTransactionEntry.getAccountBranch() + "-"
							+ aTransactionEntry.getLovDescAccountBranchName());
		} else {
			this.lovDescAccountBranchName.setValue("");
		}
		if (aTransactionEntry.getLovDescAccountSubHeadRuleName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescAccountSubHeadRuleName())) {
			this.lovDescAccountSubHeadRuleName.setValue(aTransactionEntry.getLovDescAccountSubHeadRuleName().contains(
					"-") ? aTransactionEntry.getLovDescAccountSubHeadRuleName() : aTransactionEntry
					.getAccountSubHeadRule() + "-" + aTransactionEntry.getLovDescAccountSubHeadRuleName());
		} else {
			this.lovDescAccountSubHeadRuleName.setValue("");
		}

		String type = aTransactionEntry.getPostToSys();
		if (StringUtils.isBlank(aTransactionEntry.getPostToSys())) {
			type = AccountConstants.POSTTOSYS_CORE;
		}

		switch (type) {
		case AccountConstants.POSTTOSYS_CORE:
			this.postToCore.setChecked(true);
			break;
		case AccountConstants.POSTTOSYS_GLNPL:
			this.postToERP.setChecked(true);
			break;
		default:
			this.postToCore.setChecked(true);
			break;
		}

		// Fill AmountCode And Operators
		fillListbox(this.operator, PennantStaticListUtil.getMathBasicOperator(), true);
		doCheckRuleDecider();

		this.recordStatus.setValue(aTransactionEntry.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTransactionEntry
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(TransactionEntry aTransactionEntry) throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		aTransactionEntry.setLovDescEventCodeName(this.eventCode.getValue());
		aTransactionEntry.setLovDescAccSetCodeName(this.accountSetCode.getValue());
		aTransactionEntry.setLovDescAccSetCodeDesc(this.accountSetCodeName.getValue());

		try {
			aTransactionEntry.setTransOrder(this.transOrder.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setTransDesc(this.transDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setDebitcredit(this.debitcredit.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setShadowPosting(this.shadowPosting.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setEntryByInvestment(this.entryByInvestment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setOpenNewFinAc(this.openNewFinAc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setChargeType(this.chargeType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setAccount(this.account.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescAccountTypeName(this.lovDescAccountTypeName.getValue());
			aTransactionEntry.setAccountType(this.accountType.getValue());
			if (StringUtils.isEmpty(this.accountType.getValue())) {
				aTransactionEntry.setLovDescSysInAcTypeName("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescAccountBranchName(this.lovDescAccountBranchName.getValue());
			aTransactionEntry.setAccountBranch(this.accountBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescAccountSubHeadRuleName(this.lovDescAccountSubHeadRuleName.getValue());
			aTransactionEntry.setAccountSubHeadRule(this.accountSubHeadRule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescTranscationCodeName(this.lovDescTranscationCodeName.getValue());
			aTransactionEntry.setTranscationCode(this.transcationCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescRvsTransactionCodeName(this.lovDescRvsTransactionCodeName.getValue());
			aTransactionEntry.setRvsTransactionCode(this.rvsTransactionCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_AHB)) {
				aTransactionEntry.setPostToSys(this.postToCore.isChecked() ? AccountConstants.POSTTOSYS_CORE
						: (this.postToERP.isChecked() ? AccountConstants.POSTTOSYS_GLNPL : ""));
			} else {
				aTransactionEntry.setPostToSys(AccountConstants.POSTTOSYS_GLNPL);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isBlank(this.amountRule.getValue())) {
				throw new WrongValueException(this.amountRule, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_TransactionEntryDialog_AmountRule.value") }));
			}
			aTransactionEntry.setAmountRule(this.amountRule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aTransactionEntry.setFeeCode("");
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aTransactionEntry.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aTransactionEntry
	 * @throws InterruptedException
	 */
	public void doShowDialog(TransactionEntry aTransactionEntry) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aTransactionEntry.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.transDesc.focus();
		} else {
			this.transDesc.focus();
			doEdit();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
			} else {
				this.btnCtrl.setInitEdit();
				this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnSave"));
				this.btnEdit.setVisible(false);
			}
		}

		btnCancel.setVisible(false);

		try {
			// fill the components with the data
			doWriteBeanToComponents(aTransactionEntry);
			if (getTransactionEntry().isNewRecord()) {
				this.btnSearchTranscationCode.setDisabled(true);
				this.btnSearchRvsTransactionCode.setDisabled(true);
			}

			getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(false);
			getAccountingSetDialogCtrl().window_AccountingSetDialog.getParent().appendChild(
					window_TransactionEntryDialog);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.transOrder.isReadonly()) {
			this.transOrder.setConstraint(new PTNumberValidator(Labels
					.getLabel("label_TransactionEntryDialog_TransOrder.value"), true));
		}
		if (!this.transDesc.isReadonly()) {
			this.transDesc.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_TransactionEntryDialog_TransDesc.value") }));
		}
		if (!this.debitcredit.isDisabled()) {
			this.debitcredit.setConstraint(new StaticListValidator(PennantStaticListUtil.getTranType(), Labels
					.getLabel("label_TransactionEntryDialog_Debitcredit.value")));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.transOrder.setConstraint("");
		this.transDesc.setConstraint("");
		this.debitcredit.setConstraint("");
		this.account.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		if (this.row_Account.isVisible()) {
			if (this.account.getSelectedItem() != null) {
				if (this.account.getSelectedItem().getValue().toString().equals(AccountConstants.TRANACC_GLNPL)
						|| this.account.getSelectedItem().getValue().toString()
								.equals(AccountConstants.TRANACC_CUSTSYS)
						|| this.account.getSelectedItem().getValue().toString().equals(AccountConstants.TRANACC_BUILD)) {
					this.lovDescAccountTypeName
							.setConstraint("NO EMPTY:"
									+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels
											.getLabel("label_TransactionEntryDialog_AccountType.value") }));
					if (this.account.getSelectedItem().getValue().toString().equals(AccountConstants.TRANACC_GLNPL)
							|| this.account.getSelectedItem().getValue().toString()
									.equals(AccountConstants.TRANACC_BUILD)) {
						this.lovDescAccountSubHeadRuleName.setConstraint("NO EMPTY:"
								+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels
										.getLabel("label_TransactionEntryDialog_AccountSubHeadRule.value") }));
					}
				}
			}

			if (!this.account.isDisabled()) {
				this.account.setConstraint(new StaticListValidator(PennantStaticListUtil
						.getTransactionalAccount(ImplementationConstants.ALLOW_RIA), Labels
						.getLabel("label_TransactionEntryDialog_Account.value")));
			}
		}

		if (!this.btnSearchTranscationCode.isDisabled()) {
			this.lovDescTranscationCodeName.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_TransactionEntryDialog_TranscationCode.value") }));
		}

		if (!this.btnSearchAccountSubHeadRule.isDisabled()) {
			this.lovDescAccountSubHeadRuleName.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_TransactionEntryDialog_AccountSubHeadRule.value") }));
		}

		if (!this.btnSearchAccountType.isDisabled()) {
			this.lovDescAccountTypeName.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_TransactionEntryDialog_AccountType.value") }));
		}

		if (!this.btnSearchRvsTransactionCode.isDisabled()) {
			this.lovDescRvsTransactionCodeName.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_TransactionEntryDialog_RvsTransactionCode.value") }));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints to the LOVFields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescAccountTypeName.setConstraint("");
		this.lovDescAccountSubHeadRuleName.setConstraint("");
		this.lovDescTranscationCodeName.setConstraint("");
		this.lovDescRvsTransactionCodeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear the Error Messages
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.transOrder.setErrorMessage("");
		this.transDesc.setErrorMessage("");
		this.debitcredit.setErrorMessage("");
		this.account.setErrorMessage("");
		this.lovDescAccountTypeName.setErrorMessage("");
		this.lovDescAccountSubHeadRuleName.setErrorMessage("");
		this.lovDescTranscationCodeName.setErrorMessage("");
		this.lovDescRvsTransactionCodeName.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a TransactionEntry object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final TransactionEntry aTransactionEntry = new TransactionEntry();
		BeanUtils.copyProperties(getTransactionEntry(), aTransactionEntry);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aTransactionEntry.getTransOrder() + ":" + aTransactionEntry.getTransDesc();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aTransactionEntry.getRecordType())) {
				aTransactionEntry.setVersion(aTransactionEntry.getVersion() + 1);
				aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aTransactionEntry.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aTransactionEntry.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aTransactionEntry.setVersion(aTransactionEntry.getVersion() + 1);
				aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newTranEntryProcess(aTransactionEntry, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_TransactionEntryDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getAccountingSetDialogCtrl().doFilllistbox(this.transactionEntryList);
					window_TransactionEntryDialog.onClose();
					getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(true);
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getTransactionEntry().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.transOrder.setReadonly(false);
		} else {
			this.btnCancel.setVisible(true);
			this.transOrder.setReadonly(true);
		}

		this.transDesc.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_transDesc"));
		this.debitcredit.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_debitcredit"));
		this.shadowPosting.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_shadowPosting"));
		this.entryByInvestment.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_entryByInvestment"));
		this.openNewFinAc.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_openNewFinAc"));
		this.account.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_account"));
		this.btnSearchAccountBranch.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountBranch"));
		this.btnSearchAccountType.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
		this.btnSearchSystemIntAccount.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
		this.btnSearchAccountSubHeadRule.setDisabled(getUserWorkspace().isReadOnly(
				"TransactionEntryDialog_accountSubHeadEule"));
		this.btnSearchTranscationCode.setDisabled(getUserWorkspace().isReadOnly(
				"TransactionEntryDialog_transcationCode"));
		this.btnSearchRvsTransactionCode.setDisabled(getUserWorkspace().isReadOnly(
				"TransactionEntryDialog_rvsTransactionCode"));
		this.postToCore.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_coreAccount"));
		this.postToERP.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_eRPCategory"));

		if (!this.accountingSetDialogCtrl.entryByInvestment.isChecked()) {
			this.label_TransactionEntryDialog_EntryByInvestment.setVisible(false);
			this.hbox_entryByInvestment.setVisible(false);
			this.entryByInvestment.setChecked(false);
		}

		if (getUserWorkspace().isReadOnly("TransactionEntryDialog_amountRule")) {
			this.amountRule.setReadonly(true);
			this.column_CustomerData.setWidth("0%");
			this.amountCodeListbox.setVisible(false);
			this.feeCodeListbox.setVisible(false);
			this.column_RULE.setWidth("100%");
			this.column_Operators.setWidth("0%");
			this.operator.setVisible(false);
		} else {
			this.amountRule.setReadonly(false);
			this.column_CustomerData.setWidth("30%");
			this.amountCodeListbox.setVisible(true);
			this.feeCodeListbox.setVisible(true);
			this.column_RULE.setWidth("50%");
			this.column_Operators.setWidth("20%");
			this.operator.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.transactionEntry.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.transOrder.setReadonly(true);
		this.transDesc.setReadonly(true);
		this.debitcredit.setDisabled(true);
		this.shadowPosting.setDisabled(true);
		this.entryByInvestment.setDisabled(true);
		this.openNewFinAc.setDisabled(true);
		this.account.setDisabled(true);
		this.btnSearchAccountType.setDisabled(true);
		this.btnSearchAccountBranch.setDisabled(true);
		this.btnSearchAccountSubHeadRule.setDisabled(true);
		this.btnSearchTranscationCode.setDisabled(true);
		this.btnSearchRvsTransactionCode.setDisabled(true);
		this.amountRule.setReadonly(true);
		this.column_CustomerData.setWidth("0%");
		this.amountCodeListbox.setVisible(false);
		this.feeCodeListbox.setVisible(false);
		this.column_RULE.setWidth("100%");
		this.column_Operators.setWidth("0%");
		this.operator.setVisible(false);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.transOrder.setText("");
		this.transDesc.setValue("");
		this.debitcredit.setValue("");
		this.shadowPosting.setChecked(false);
		this.entryByInvestment.setChecked(false);
		this.openNewFinAc.setChecked(false);
		this.accountType.setValue("");
		this.accountBranch.setValue("");
		this.lovDescAccountTypeName.setValue("");
		this.lovDescAccountBranchName.setValue("");
		this.accountSubHeadRule.setValue("");
		this.lovDescAccountSubHeadRuleName.setValue("");
		this.transcationCode.setValue("");
		this.lovDescTranscationCodeName.setValue("");
		this.rvsTransactionCode.setValue("");
		this.lovDescRvsTransactionCodeName.setValue("");
		this.amountRule.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final TransactionEntry aTransactionEntry = new TransactionEntry();
		BeanUtils.copyProperties(getTransactionEntry(), aTransactionEntry);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();
		doSetLOVValidation();
		// fill the TransactionEntry object with the components data
		doWriteComponentsToBean(aTransactionEntry);

		// Write the additional validations as per below example
		// get the selected branch object from the lisBox
		// Do data level validations here

		isNew = aTransactionEntry.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aTransactionEntry.getRecordType())) {
				aTransactionEntry.setVersion(aTransactionEntry.getVersion() + 1);
				if (isNew) {
					aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTransactionEntry.setNewRecord(true);
				}
			}
		} else {

			if (isNew) {
				aTransactionEntry.setVersion(1);
				aTransactionEntry.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aTransactionEntry.getRecordType())) {
				aTransactionEntry.setVersion(aTransactionEntry.getVersion() + 1);
				aTransactionEntry.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aTransactionEntry.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aTransactionEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newTranEntryProcess(aTransactionEntry, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_TransactionEntryDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if(isGSTApplicable){
				MessageUtil.showMessage(Labels.getLabel("label_GstApplicable"));
				isGSTApplicable=false;
			}
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getAccountingSetDialogCtrl().doFilllistbox(this.transactionEntryList);
				window_TransactionEntryDialog.onClose();
				getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(true);
			}
			
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		
		logger.debug("Leaving");
	}

	private AuditHeader newTranEntryProcess(TransactionEntry aTransactionEntry, String tranType) {

		boolean recordAdded = false;
		boolean derivedEntryAdded = false;
		AuditHeader auditHeader = getAuditHeader(aTransactionEntry, tranType);
		transactionEntryList = new ArrayList<TransactionEntry>();

		String[] valueParm = new String[3];
		String[] errParm = new String[2];

		valueParm[0] = aTransactionEntry.getLovDescEventCodeName();
		valueParm[1] = aTransactionEntry.getLovDescAccSetCodeName();
		valueParm[2] = String.valueOf(aTransactionEntry.getTransOrder());

		errParm[0] = PennantJavaUtil.getLabel("label_FeeTranEvent") + ":" + valueParm[0] + " "
				+ PennantJavaUtil.getLabel("label_AccountingSetDialog_AccountSetCode.value") + ":" + valueParm[1];
		errParm[1] = PennantJavaUtil.getLabel("label_TransactionEntryDialog_TransOrder.value") + ":" + valueParm[2];
		//getAccountingSetDialogCtrl().getTransactionEntryList();
		if (getAccountingSetDialogCtrl().getTransactionEntryList() != null
				&& getAccountingSetDialogCtrl().getTransactionEntryList().size() > 0) {
			for (int i = 0; i < getAccountingSetDialogCtrl().getTransactionEntryList().size(); i++) {
				TransactionEntry transactionEntry = getAccountingSetDialogCtrl().getTransactionEntryList().get(i);

				if (transactionEntry.getLovDescEventCodeName().equals(aTransactionEntry.getLovDescEventCodeName())
						&& transactionEntry.getLovDescAccSetCodeName().equals(
								aTransactionEntry.getLovDescAccSetCodeName())
						&& transactionEntry.getTransOrder() == aTransactionEntry.getTransOrder()
						|| transactionEntry.getTransOrder() == (aTransactionEntry.getTransOrder() + 1)) {

					if ((transactionEntry.getTransOrder() == (aTransactionEntry.getTransOrder() + 1))
							&& aTransactionEntry.isNewRecord()
							&& StringUtils.equals(aTransactionEntry.getRecordType(), PennantConstants.RCD_ADD)
							&& StringUtils.equals(aTransactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_CORE)) {

						valueParm[2] = String.valueOf(aTransactionEntry.getTransOrder() + 1);
						errParm[1] = PennantJavaUtil.getLabel("label_TransactionEntryDialog_TransOrder.value") + ":"
								+ valueParm[2];
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "30549", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}

					// Both Current and Existing list Transaction Order is same
					if (aTransactionEntry.isNew()
							&& transactionEntry.getTransOrder() == (aTransactionEntry.getTransOrder())) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {

						if (transactionEntry.getTransOrder() == (aTransactionEntry.getTransOrder())) {
							if (PennantConstants.RECORD_TYPE_UPD.equals(aTransactionEntry.getRecordType())) {
								aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
								recordAdded = true;
								transactionEntryList.add(aTransactionEntry);
							} else if (PennantConstants.RCD_ADD.equals(aTransactionEntry.getRecordType())) {
								recordAdded = true;
							} else if (PennantConstants.RECORD_TYPE_NEW.equals(aTransactionEntry.getRecordType())) {
								aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_CAN);
								recordAdded = true;
								transactionEntryList.add(aTransactionEntry);
							} else if (PennantConstants.RECORD_TYPE_CAN.equals(aTransactionEntry.getRecordType())) {
								recordAdded = true;

								for (int j = 0; j < getAccountingSetDialogCtrl().getAccountingSet()
										.getTransactionEntries().size(); j++) {
									TransactionEntry fee = getAccountingSetDialogCtrl().getAccountingSet()
											.getTransactionEntries().get(j);
									if (fee.getTransOrder() == aTransactionEntry.getTransOrder()
											&& fee.getLovDescEventCodeName().equals(
													aTransactionEntry.getLovDescEventCodeName())) {
										transactionEntryList.add(fee);
									}
								}
							} else if (PennantConstants.RECORD_TYPE_DEL.equals(aTransactionEntry.getRecordType())) {
								aTransactionEntry.setNewRecord(true);
							}
						} else {
							TransactionEntry derivedEntry = verifyDerivedTranOrder(transactionEntry, aTransactionEntry,
									false);
							if (derivedEntry != null) {
								derivedEntryAdded = true;
								transactionEntryList.add(derivedEntry);
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							TransactionEntry derivedEntry = verifyDerivedTranOrder(transactionEntry, aTransactionEntry,
									false);
							if (derivedEntry != null) {
								transactionEntryList.add(derivedEntry);
							} else {
								transactionEntryList.add(transactionEntry);
							}
						} else {
							if (!(StringUtils.equals(aTransactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_CORE) && (transactionEntry != null && StringUtils
									.equals(transactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_GLNPL)))) {
								TransactionEntry derivedEntry = verifyDerivedTranOrder(transactionEntry,
										aTransactionEntry, false);
								if (derivedEntry != null) {
									transactionEntryList.add(derivedEntry);
									derivedEntryAdded = true;
								}
							}
						}
					}
				} else {
					TransactionEntry derivedEntry = null;

					if (!(StringUtils.equals(aTransactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_CORE) && (transactionEntry != null && StringUtils
							.equals(transactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_GLNPL)))) {
						derivedEntry = verifyDerivedTranOrder(transactionEntry, aTransactionEntry, false);
					}
					if (derivedEntry != null) {
						transactionEntryList.add(derivedEntry);
						derivedEntryAdded = true;
					} else {
						transactionEntryList.add(transactionEntry);
					}
				}
			}
		}
		if (!recordAdded) {
			if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_AHB)) {
				if (!derivedEntryAdded) {
					TransactionEntry derivedEntry = verifyDerivedTranOrder(null, aTransactionEntry, true);
					if (derivedEntry != null) {
						transactionEntryList.add(derivedEntry);
					}
				}
			}
			transactionEntryList.add(aTransactionEntry);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Maintaining Derived Transaction Entry Details for Modified entry
	 * 
	 * @return
	 */
	private TransactionEntry verifyDerivedTranOrder(TransactionEntry transactionEntry,
			TransactionEntry aTransactionEntry, boolean isNewRec) {
		logger.debug("Entering");

		TransactionEntry derivedEntry = new TransactionEntry();

		if (StringUtils.equals(aTransactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_GLNPL)) {

			// If System posting entry from T24 to ERp modification, then existing derived entry should be removed.
			if (transactionEntry != null
					&& StringUtils.equals(transactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_CORE)) {
				// Creating New Derived Entry
				BeanUtils.copyProperties(transactionEntry, derivedEntry);

				if (derivedEntry.getDerivedTranOrder() != 0
						&& derivedEntry.getDerivedTranOrder() == aTransactionEntry.getTransOrder()) {
					if (StringUtils.equals(transactionEntry.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
						derivedEntry.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					} else if (StringUtils.equals(transactionEntry.getRecordType(), PennantConstants.RCD_ADD)) {
						derivedEntry = null;
					} else {
						derivedEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						derivedEntry.setNewRecord(true);
					}
					return derivedEntry;
				}
			}

			return null;

		}

		// If System Type modified from ERP to T24, creation of Derived Entry.
		if (transactionEntry != null
				&& StringUtils.equals(transactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_GLNPL)) {
			if (StringUtils.equals(aTransactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_CORE)) {
				isNewRec = true;
			}
		}

		// Adding PFF Clearing Account Transaction Entry on Selection System Entry
		if (isNewRec) {

			// Creating New Derived Entry
			BeanUtils.copyProperties(aTransactionEntry, derivedEntry);
			derivedEntry.setTransDesc("PFF Clearing Account");
			derivedEntry.setAccount(AccountConstants.TRANACC_BUILD);
			derivedEntry.setAccountType(aTransactionEntry.getAccountType());
			derivedEntry.setAccountSubHeadRule(SysParamUtil.getValueAsString("SUBHEAD_PFFCLRAC"));
			derivedEntry.setTranscationCode(aTransactionEntry.getRvsTransactionCode());
			derivedEntry.setLovDescTranscationCodeName(aTransactionEntry.getLovDescRvsTransactionCodeName());
			derivedEntry.setRvsTransactionCode(aTransactionEntry.getTranscationCode());
			derivedEntry.setLovDescRvsTransactionCodeName(aTransactionEntry.getLovDescTranscationCodeName());
			derivedEntry.setDerivedTranOrder(aTransactionEntry.getTransOrder());
			derivedEntry.setTransOrder(aTransactionEntry.getTransOrder() + 1);
			derivedEntry
					.setDebitcredit(aTransactionEntry.getDebitcredit().equals(AccountConstants.TRANTYPE_CREDIT) ? AccountConstants.TRANTYPE_DEBIT
							: AccountConstants.TRANTYPE_CREDIT);
			derivedEntry.setRecordType(PennantConstants.RCD_ADD);
			derivedEntry.setRecordStatus("");
		} else {

			// Creating New Derived Entry
			BeanUtils.copyProperties(transactionEntry, derivedEntry);

			if (derivedEntry.getDerivedTranOrder() != 0
					&& derivedEntry.getDerivedTranOrder() == aTransactionEntry.getTransOrder()) {
				derivedEntry.setRecordType(aTransactionEntry.getRecordType());
				derivedEntry.setNewRecord(aTransactionEntry.isNewRecord());
			}

		}
		logger.debug("Leaving");
		return derivedEntry;
	}

	// Search Button Component Events

	public void onClick$btnSearchAccountType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = null;
		if (ImplementationConstants.CLIENT_NAME.equals(ImplementationConstants.CLIENT_AIB)) {
			Filter[] filter = new Filter[1];
			filter[0] = new Filter("CustSysAc", "1", Filter.OP_EQUAL);
			dataObject = ExtendedSearchListBox.show(this.window_TransactionEntryDialog, "AccountType", filter);
		} else {
			dataObject = ExtendedSearchListBox.show(this.window_TransactionEntryDialog, "AccountType");
		}

		if (dataObject instanceof String) {
			this.accountType.setValue(dataObject.toString());
			this.lovDescAccountTypeName.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				if(details.isTaxApplicable()){
					isGSTApplicable=true;
				}
				this.accountType.setValue(details.getAcType());
				this.lovDescAccountTypeName.setValue(details.getAcType() + "-" + details.getAcTypeDesc());

			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchSystemIntAccount(Event event) {
		logger.debug("Entering" + event.toString());

		String accountSource = "";
		accountSource = "AccountType";

		Object dataObject = ExtendedSearchListBox.show(this.window_TransactionEntryDialog, accountSource);
		if (dataObject instanceof String) {
			this.accountType.setValue(dataObject.toString());
			this.lovDescAccountTypeName.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.accountType.setValue(details.getAcType());
				this.lovDescAccountTypeName.setValue(details.getAcType() + "-" + details.getAcTypeDesc());

			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchAccountBranch(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_TransactionEntryDialog, "Branch");
		if (dataObject instanceof String) {
			this.accountBranch.setValue(dataObject.toString());
			this.lovDescAccountBranchName.setValue("");
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.accountBranch.setValue(details.getBranchCode());
				this.lovDescAccountBranchName.setValue(details.getBranchCode() + "-" + details.getBranchDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchAccountSubHeadRule(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RuleModule", RuleConstants.MODULE_SUBHEAD, Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_TransactionEntryDialog, "Rule", filter);
		if (dataObject instanceof String) {
			this.accountSubHeadRule.setValue(dataObject.toString());
			this.lovDescAccountSubHeadRuleName.setValue("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.accountSubHeadRule.setValue(details.getRuleCode());
				this.lovDescAccountSubHeadRuleName.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchTranscationCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_TransactionEntryDialog, "TransactionCode",
				getTranTypeFilter(this.debitcredit.getSelectedItem().getValue().toString(), false));

		// TranType
		if (dataObject instanceof String) {
			this.transcationCode.setValue(dataObject.toString());
			this.lovDescTranscationCodeName.setValue("");
		} else {
			TransactionCode details = (TransactionCode) dataObject;
			if (details != null) {
				this.transcationCode.setValue(details.getTranCode());
				this.lovDescTranscationCodeName.setValue(details.getTranCode() + "-" + details.getTranDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchRvsTransactionCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_TransactionEntryDialog, "TransactionCode",
				getTranTypeFilter(this.debitcredit.getSelectedItem().getValue().toString(), true));

		if (dataObject instanceof String) {
			this.rvsTransactionCode.setValue(dataObject.toString());
			this.lovDescRvsTransactionCodeName.setValue("");
		} else {
			TransactionCode details = (TransactionCode) dataObject;
			if (details != null) {
				this.rvsTransactionCode.setValue(details.getTranCode());
				this.lovDescRvsTransactionCodeName.setValue(details.getTranCode() + "-" + details.getTranDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private Filter[] getTranTypeFilter(String tranType, boolean isReversal) {
		logger.debug("Entering");
		Filter[] filter = new Filter[1];
		if (isReversal) {
			if (StringUtils.equals(AccountConstants.TRANTYPE_CREDIT, tranType)) {
				filter[0] = new Filter("TranType", new String[] { AccountConstants.TRANTYPE_DEBIT,
						AccountConstants.TRANTYPE_BOTH }, Filter.OP_IN);
			} else if (StringUtils.equals(AccountConstants.TRANTYPE_DEBIT, tranType)) {
				filter[0] = new Filter("TranType", new String[] { AccountConstants.TRANTYPE_CREDIT,
						AccountConstants.TRANTYPE_BOTH }, Filter.OP_IN);
			}
		} else {
			if (StringUtils.equals(AccountConstants.TRANTYPE_CREDIT, tranType)) {
				filter[0] = new Filter("TranType", new String[] { AccountConstants.TRANTYPE_CREDIT,
						AccountConstants.TRANTYPE_BOTH }, Filter.OP_IN);
			} else if (StringUtils.equals(AccountConstants.TRANTYPE_DEBIT, tranType)) {
				filter[0] = new Filter("TranType", new String[] { AccountConstants.TRANTYPE_DEBIT,
						AccountConstants.TRANTYPE_BOTH }, Filter.OP_IN);
			}
		}
		logger.debug("Leaving");
		return filter;
	}

	/**
	 * To get declared fields from the CustomerEligibilityCheck class
	 */
	private void getAmountCodes(String allowedevent) {
		logger.debug("Entering");
		this.amountCodeListbox.getItems().clear();
		List<AmountCode> amountCodesList = new ArrayList<AmountCode>();
		JdbcSearchObject<AmountCode> searchObj = new JdbcSearchObject<AmountCode>(AmountCode.class);
		searchObj.addTabelName("BMTAmountCodes");
		if (allowedevent != null) {
			searchObj.addFilter(new Filter("AllowedEvent", allowedevent, Filter.OP_EQUAL));
			if (!this.accountingSetDialogCtrl.entryByInvestment.isChecked()) {
				searchObj.addFilter(new Filter("AllowedRIA", 0, Filter.OP_EQUAL));
			} else {
				searchObj.addFilter(new Filter("AllowedRIA", 1, Filter.OP_EQUAL));
			}
		}
		amountCodesList = this.pagedListService.getBySearchObject(searchObj);

		for (int i = 0; i < amountCodesList.size(); i++) {
			Listitem item = new Listitem();
			Listcell lc = new Listcell(amountCodesList.get(i).getAmountCode());
			if (!amountcodes.contains(amountCodesList.get(i).getAmountCode())) {
				amountcodes.add(amountCodesList.get(i).getAmountCode());
			}
			lc.setParent(item);
			lc = new Listcell(amountCodesList.get(i).getAmountCodeDesc());
			lc.setParent(item);
			this.amountCodeListbox.appendChild(item);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for get the list of Rules existing on base of Event
	 * 
	 * @param allowedevent
	 */
	private void getFeeCodes(String allowedevent) {
		logger.debug("Entering");

		this.feeCodeListbox.getItems().clear();
		List<FeeType> feeRulesList = new ArrayList<FeeType>();
		JdbcSearchObject<FeeType> searchObj = new JdbcSearchObject<FeeType>(FeeType.class);
		searchObj.addTabelName("FeeTypes");
		searchObj.addFilter(new Filter("Active", 1, Filter.OP_EQUAL));
		/*
		 * //Rule Module & Rule Event Filter addition if(allowedevent.contains(AccountEventConstants.ACCEVENT_REPAY)){
		 * Object[] ruleModule= new String[]{RuleConstants.MODULE_FEES, RuleConstants.MODULE_REFUND}; Object[]
		 * ruleEvent= new String[]{allowedevent,RuleConstants.EVENT_REFUND};
		 * searchObj.addFilterIn("RuleModule",ruleModule); searchObj.addFilterIn("RuleEvent", ruleEvent); }else{
		 * searchObj.addFilterEqual("RuleModule", RuleConstants.MODULE_FEES); if
		 * (allowedevent.contains(AccountEventConstants.ACCEVENT_ADDDBS)) {
		 * searchObj.addFilterEqual("RuleEvent",AccountEventConstants.ACCEVENT_ADDDBS); }else
		 * if(allowedevent.equals(AccountEventConstants.ACCEVENT_STAGE)){ // All Fee Event will allow to Enter Details }
		 * }
		 */

		feeRulesList = this.pagedListService.getBySearchObject(searchObj);

		Listitem item = null;
		Listgroup group = null;
		Listcell lc = null;

		for (int i = 0; i < feeRulesList.size(); i++) {
			String feeTypeCode = feeRulesList.get(i).getFeeTypeCode();
			String feeTypeDesc = feeRulesList.get(i).getFeeTypeDesc();

			group = new Listgroup(feeTypeCode);
			group.setOpen(false);
			this.feeCodeListbox.appendChild(group);

			for (int j = 0; j < 5; j++) {
				String newRuleCode = feeTypeCode;
				String newRuleCodeDesc = feeTypeDesc;

				if (j == 0) {
					newRuleCode = newRuleCode + "_C";
					newRuleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_FeeCalc");
				} else if (j == 1) {
					newRuleCode = newRuleCode + "_W";
					newRuleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_FeeWaiver");
				} else if (j == 2) {
					if(StringUtils.equals(allowedevent, AccountEventConstants.ACCEVENT_REPAY) ||
							StringUtils.equals(allowedevent, AccountEventConstants.ACCEVENT_EARLYPAY) ||
							StringUtils.equals(allowedevent, AccountEventConstants.ACCEVENT_EARLYSTL)) {
						String ruleCode = "";
						String ruleCodeDesc = "";
						ruleCode = "PA_" + newRuleCode + "_P";
						ruleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_PayableAdvise");

						item = new Listitem();
						lc = new Listcell(ruleCode);

						if (!amountcodes.contains(ruleCode)) {
							amountcodes.add(ruleCode);
						}

						lc.setParent(item);
						lc = new Listcell(ruleCodeDesc);
						lc.setParent(item);
						this.feeCodeListbox.appendChild(item);

						ruleCode = "PB_" + newRuleCode + "_P";
						ruleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_PartnerBank");

						item = new Listitem();
						lc = new Listcell(ruleCode);

						if (!amountcodes.contains(ruleCode)) {
							amountcodes.add(ruleCode);
						}

						lc.setParent(item);
						lc = new Listcell(ruleCodeDesc);
						lc.setParent(item);
						this.feeCodeListbox.appendChild(item);

						ruleCode = "EA_" + newRuleCode + "_P";
						ruleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_EMIAdvance");

						item = new Listitem();
						lc = new Listcell(ruleCode);

						if (!amountcodes.contains(ruleCode)) {
							amountcodes.add(ruleCode);
						}

						lc.setParent(item);
						lc = new Listcell(ruleCodeDesc);
						lc.setParent(item);
						this.feeCodeListbox.appendChild(item);

						ruleCode = "EX_" + newRuleCode + "_P";
						ruleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_Excess");

						item = new Listitem();
						lc = new Listcell(ruleCode);

						if (!amountcodes.contains(ruleCode)) {
							amountcodes.add(ruleCode);
						}

						lc.setParent(item);
						lc = new Listcell(ruleCodeDesc);
						lc.setParent(item);
						this.feeCodeListbox.appendChild(item);

					}
					newRuleCode = newRuleCode + "_P";
					newRuleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_FeePaid");
				} else if (j == 3) {
					newRuleCode = newRuleCode + "_AF";
					newRuleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_FeeAF");
				} else if (j == 4) {
					newRuleCode = newRuleCode + "_SCH";
					newRuleCodeDesc = newRuleCodeDesc + Labels.getLabel("label_TransactionEntryDialog_FeeSF");
				}

				item = new Listitem();
				lc = new Listcell(newRuleCode);

				if (!amountcodes.contains(newRuleCode)) {
					amountcodes.add(newRuleCode);
				}

				lc.setParent(item);
				lc = new Listcell(newRuleCodeDesc);
				lc.setParent(item);
				this.feeCodeListbox.appendChild(item);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * To fill the list box with two list headers
	 * 
	 * @param listbox
	 * @param arrayList
	 * @param showFirstColumn
	 */
	private void fillListbox(Listbox listbox, ArrayList<ValueLabel> arrayList, boolean showFirstColumn) {
		logger.debug("Entering");
		for (int i = 0; i < arrayList.size(); i++) {
			Listitem item = new Listitem();
			Listcell lc = new Listcell(arrayList.get(i).getValue());
			if (showFirstColumn) {
				lc.setVisible(true);
			} else {
				lc.setVisible(false);
			}
			lc.setParent(item);
			lc = new Listcell(arrayList.get(i).getLabel());
			lc.setParent(item);
			listbox.appendChild(item);
		}
		logger.debug("Leaving");
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnSimulate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event)) {
			Clients.clearWrongValue(this.amountRule);
			// create a new window for input values
			createSimulationWindow(variables);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS AND CONFIRM EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnValidate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event)) {
			if (MessageUtil.confirm("No Errors Found! Proceed With Simulation?") == MessageUtil.YES) {
				// create a new window for input values
				createSimulationWindow(variables);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS
	 * 
	 * @param event
	 * @return
	 * @throws InterruptedException
	 */
	private boolean validate(ForwardEvent event) throws InterruptedException {
		boolean noerrors = false;
		// object containing errors and variables
		Object[] data = (Object[]) event.getOrigin().getData();
		// array of errors
		if (data != null && data.length != 0) {
			JSONArray errors = (JSONArray) data[0];
			// array of variables
			variables = (JSONArray) data[1];

			// if no errors
			if (variables != null && errors.size() == 0) {
				// check for new declared variables
				for (int i = 0; i < variables.size(); i++) {
					JSONObject variable = (JSONObject) variables.get(i);
					if (!"Result".equals(variable.get("name"))) {
						if (!amountcodes.contains(variable.get("name"))) {
							// if new variables found throw error message
							noerrors = false;
							MessageUtil.showError("Unknown Variable :" + variable.get("name"));
							return noerrors;
						} else {
							noerrors = true;
						}
					}
				}
				if (noerrors) {
					return validateResult();
				}

			} else {
				for (int i = 0; i < errors.size(); i++) {
					JSONObject error = (JSONObject) errors.get(i);
					if (error != null) {
						MessageUtil.showError("Error : At Line " + error.get("line") + ",Position "
								+ error.get("character") + "\n\n" + error.get("reason").toString());
					}
				}
			}
		} else {
			return true;
		}
		if (!noerrors) {
			MessageUtil.showError("Error found in Rule. Please correct the rule and try again");
		}
		return noerrors;
	}

	/**
	 * CALL THE RESULT ZUL FILE
	 * 
	 * @param jsonArray
	 * @throws InterruptedException
	 */
	public void createSimulationWindow(JSONArray jsonArray) throws InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("RuleVariables", jsonArray);
		map.put("transactionEntryDialogCtrl", this);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/TransactionEntry/TransactionEntryRuleResult.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean validateResult() throws InterruptedException {
		if (this.amountRule.getValue().contains("Result")) {
			if (this.amountRule.getValue().contains("{")) {
				MessageUtil.showError("Logical Operators Not Allowed");
				return false;
			}

		} else {
			MessageUtil.showError("Result not found");
			return false;
		}
		return true;
	}

	public void dofillAccount(Combobox combobox, String value) {
		logger.debug("Entering");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		List<ValueLabel> list = PennantStaticListUtil.getTransactionalAccount(ImplementationConstants.ALLOW_RIA);
		for (int i = 0; i < list.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(StringUtils.trim(list.get(i).getValue()));
			comboitem.setLabel(StringUtils.trim(list.get(i).getLabel()));
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(list.get(i).getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}

		logger.debug("Leaving");
	}

	public void doFillChargeType(Radiogroup radiogroup, String value) {
		logger.debug("Entering");
		radiogroup.getChildren().clear();
		ArrayList<ValueLabel> chargeTypes = PennantStaticListUtil.getChargeTypes();
		for (int i = 0; i < chargeTypes.size(); i++) {
			Radio radio = new Radio();
			radio.setValue(StringUtils.trim(chargeTypes.get(i).getValue()));
			radio.setLabel(StringUtils.trim(chargeTypes.get(i).getLabel()));
			radio.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_chargeType"));
			radiogroup.appendChild(radio);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(chargeTypes.get(i).getValue()))) {
				radiogroup.setSelectedItem(radio);
			}
		}
		if (radiogroup.getSelectedItem() == null) {
			radiogroup.setSelectedIndex(0);
		}

		logger.debug("Leaving");
	}

	public void onChange$debitcredit(Event event) {
		logger.debug("Entering");
		if (this.debitcredit.getSelectedItem() != null) {
			this.transcationCode.setValue("");
			this.lovDescTranscationCodeName.setValue("");
			this.rvsTransactionCode.setValue("");
			this.lovDescRvsTransactionCodeName.setValue("");
			if (!"#".equals(this.debitcredit.getSelectedItem().getValue())) {
				this.btnSearchTranscationCode.setDisabled(isReadOnly("TransactionEntryDialog_transcationCode"));
				this.btnSearchRvsTransactionCode.setDisabled(isReadOnly("TransactionEntryDialog_transcationCode"));
			} else {
				this.btnSearchTranscationCode.setDisabled(true);
				this.btnSearchRvsTransactionCode.setDisabled(true);
			}
		} else {
			this.btnSearchTranscationCode.setDisabled(true);
			this.btnSearchRvsTransactionCode.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(TransactionEntry aTransactionEntry, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTransactionEntry.getBefImage(), aTransactionEntry);
		return new AuditHeader(String.valueOf(aTransactionEntry.getAccountSetid()), null, null, null, auditDetail,
				aTransactionEntry.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_TransactionEntryDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.transactionEntry);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.transactionEntry.getAccountSetid());
	}

	public void onChange$account(Event event) {
		if (this.account.getSelectedItem() != null) {
			doFillAccountType(this.account.getSelectedItem().getValue().toString());
		}
	}

	private void doFillAccountType(String value) {
		logger.debug("Entering");

		this.openNewFinAc.setChecked(false);
		this.row_OpenNewFinAc.setVisible(false);
		//this.btnSearchAccountType.setDisabled(true);
		this.btnSearchSystemIntAccount.setDisabled(true);
		//this.btnSearchAccountSubHeadRule.setDisabled(true);
		this.lovDescAccountTypeName.setValue("");
		this.spAccountType.setSclass("");

		if (value != null && !"#".equals(value)) {
			if (value.equals(AccountConstants.TRANACC_DISB) || value.equals(AccountConstants.TRANACC_REPAY)
					|| value.equals(AccountConstants.TRANACC_CANFIN) || value.equals(AccountConstants.TRANACC_WRITEOFF)
					|| value.equals(AccountConstants.TRANACC_INVSTR) || value.equals(AccountConstants.TRANACC_DOWNPAY)
					|| value.equals(AccountConstants.TRANACC_FEEAC)
					|| value.equals(AccountConstants.TRANACC_WRITEOFFPAY)) {

				this.btnSearchAccountType.setDisabled(true);
				this.spSubHead.setSclass("");
				this.spAccountType.setSclass("");
				this.btnSearchAccountSubHeadRule.setDisabled(true);
				this.btnSearchSystemIntAccount.setDisabled(true);
				this.accountType.setValue(null);
				this.lovDescAccountTypeName.setValue("");
				this.btnSearchSystemIntAccount.setVisible(false);
				this.accountSubHeadRule.setValue("");
				this.lovDescAccountSubHeadRuleName.setValue("");
				this.openNewFinAc.setChecked(false);
			} else if (value.equals(AccountConstants.TRANACC_GLNPL) || value.equals(AccountConstants.TRANACC_BUILD)) {
				if (ImplementationConstants.CLIENT_NAME.equals(ImplementationConstants.CLIENT_AIB)) {
					this.btnSearchAccountType.setVisible(false);
					this.btnSearchSystemIntAccount.setVisible(true);
					this.btnSearchSystemIntAccount.setDisabled(getUserWorkspace().isReadOnly(
							"TransactionEntryDialog_accountType"));
					this.accountType.setValue(null);
					this.spSubHead.setSclass("mandatory");
				} else {
					this.btnSearchAccountType.setVisible(true);
					this.btnSearchSystemIntAccount.setVisible(false);
					this.btnSearchAccountType.setDisabled(getUserWorkspace().isReadOnly(
							"TransactionEntryDialog_accountType"));
					this.accountType.setValue(null);
				}
				this.spAccountType.setSclass("mandatory");
				this.btnSearchAccountSubHeadRule.setDisabled(getUserWorkspace().isReadOnly(
						"TransactionEntryDialog_accountSubHeadEule"));
				this.openNewFinAc.setChecked(false);
			} else if (value.equals(AccountConstants.TRANACC_CUSTSYS)) {
				this.btnSearchAccountType.setVisible(true);
				this.btnSearchAccountType.setDisabled(getUserWorkspace().isReadOnly(
						"TransactionEntryDialog_accountType"));
				this.spSubHead.setSclass("");
				this.spAccountType.setSclass("mandatory");
				this.btnSearchAccountSubHeadRule.setDisabled(true);
				this.btnSearchSystemIntAccount.setVisible(false);
				this.accountSubHeadRule.setValue("");
				this.lovDescAccountSubHeadRuleName.setValue("");
				this.row_OpenNewFinAc.setVisible(true);
			} else if (value.equals(AccountConstants.TRANACC_UNEARN)) {
				this.row_OpenNewFinAc.setVisible(true);
			} else if (value.equals(AccountConstants.TRANACC_SUSP)) {
				this.row_OpenNewFinAc.setVisible(true);
			} else if (value.equals(AccountConstants.TRANACC_PROVSN)) {
				this.row_OpenNewFinAc.setVisible(true);
			} else if (value.equals(AccountConstants.TRANACC_COMMIT)) {
				this.btnSearchAccountType.setDisabled(true);
				this.spSubHead.setSclass("");
				this.spAccountType.setSclass("");
				this.btnSearchAccountSubHeadRule.setDisabled(true);
				this.btnSearchSystemIntAccount.setDisabled(true);
				this.accountType.setValue(null);
				this.lovDescAccountTypeName.setValue("");
				this.btnSearchSystemIntAccount.setVisible(false);
				this.accountSubHeadRule.setValue("");
				this.lovDescAccountSubHeadRuleName.setValue("");
				this.openNewFinAc.setChecked(false);
			}
		}

		logger.debug("Leaving");
	}

	private void doCheckRuleDecider() {
		logger.debug("Entering");
		this.amountcodes.clear();
		getAmountCodes(getTransactionEntry().getLovDescEventCodeName());
		//if(StringUtils.isNotBlank(getTransactionEntry().getFeeCode())){
		getFeeCodes(getTransactionEntry().getLovDescEventCodeName());
		//}else{
		//this.tab_Fee.setVisible(false);
		//}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public TransactionEntry getTransactionEntry() {
		return this.transactionEntry;
	}

	public void setTransactionEntry(TransactionEntry transactionEntry) {
		this.transactionEntry = transactionEntry;
	}

	public AccountingSetService getAccountingSetService() {
		return accountingSetService;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setAccountingSetDialogCtrl(AccountingSetDialogCtrl accountingSetDialogCtrl) {
		this.accountingSetDialogCtrl = accountingSetDialogCtrl;
	}

	public AccountingSetDialogCtrl getAccountingSetDialogCtrl() {
		return accountingSetDialogCtrl;
	}

}
