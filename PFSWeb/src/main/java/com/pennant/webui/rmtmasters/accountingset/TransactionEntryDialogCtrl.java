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
 * * FileName : TransactionEntryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * *
 * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.accountingset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.finance.FeeType;
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
import com.pennant.pff.accounting.SingleFee;
import com.pennant.pff.extension.FeeExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * This is the controller class for the /WEB-INF/pages/RulesFactory/TransactionEntry/transactionEntryDialog.zul file.
 */
public class TransactionEntryDialogCtrl extends GFCBaseCtrl<TransactionEntry> {
	private static final long serialVersionUID = 4345607610334573882L;
	private static final Logger logger = LogManager.getLogger(TransactionEntryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TransactionEntryDialog;
	protected Intbox transOrder;
	protected Textbox transDesc;
	protected Combobox debitcredit;
	protected Checkbox shadowPosting;
	protected Combobox account;
	protected Textbox accountType;
	protected Textbox accountBranch;
	protected Textbox accountSubHeadRule;
	protected Textbox transcationCode;
	protected Textbox rvsTransactionCode;
	protected Codemirror amountRule;
	protected Textbox eventCode;
	protected Textbox accountSetCode;
	protected Textbox accountSetCodeName;
	protected Textbox lovDescEventCodeName;
	protected Checkbox entryByInvestment;
	protected Label label_TransactionEntryDialog_EntryByInvestment;
	protected Hbox hbox_entryByInvestment;
	protected Checkbox openNewFinAc;
	protected Row row_OpenNewFinAc;
	protected Row row_ShadowPosting;
	protected Label label_TransactionEntryDialog_PostToCore;
	protected Hbox hbox_PostToCore;

	protected Radio postToCore;
	protected Radio postToERP;

	protected Row row_Account;

	// not auto wired vars
	private TransactionEntry transactionEntry;

	private transient boolean validationOn;

	protected Button btnSearchAccountType;
	protected Textbox lovDescAccountTypeName;

	protected Button btnSearchAccountBranch;
	protected Textbox lovDescAccountBranchName;

	protected Button btnSearchAccountSubHeadRule;
	protected Textbox lovDescAccountSubHeadRuleName;

	protected Button btnSearchTranscationCode;
	protected Textbox lovDescTranscationCodeName;

	protected Button btnSearchRvsTransactionCode;
	protected Textbox lovDescRvsTransactionCodeName;

	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();

	private transient AccountingSetService accountingSetService;
	private transient AccountingSetDialogCtrl accountingSetDialogCtrl;
	private List<TransactionEntry> transactionEntryList;

	protected Listbox amountCodeListbox;
	protected Listbox feeCodeListbox;
	protected Listbox vasCodeListbox;
	protected Listbox expenseCodeListbox;
	protected Listbox operator;
	protected Button btnCopyTo;
	protected Tab tab_Fee;
	protected Tab tab_Vas;
	protected Tab tab_expense;
	protected Tab tab_amount;

	protected Grid grid_Basicdetails;
	protected Column column_CustomerData;
	protected Column column_RULE;
	protected Column column_Operators;
	protected Button btnSimulate;
	protected Button btnValidate;
	private JSONArray variables = new JSONArray();
	private Set<String> amountcodes = new HashSet<>();
	protected Button btnSearchSystemIntAccount;

	protected Space spAccountType;
	protected Space spSubHead;

	protected Radiogroup chargeType;

	// protected Combobox ruleDecider;
	protected Groupbox gb_RuleCode;
	private String userRole = "";
	private boolean isGSTApplicable = false;

	// ### START SFA_20210405 -->
	protected Row row_FeeRepeat;
	protected Label label_TransactionEntryDialog_FeeRepeat;
	protected Checkbox feeRepeat;
	protected Hbox hbox_feeRepeat;

	protected Label label_TransactionEntryDialog_RecOrPay;
	protected Combobox receivableOrPayable;
	protected Hbox hbox_receivableOrPayable;

	protected Row row_AssignEntry;
	protected Label label_TransactionEntryDialog_AssignmentEntry;
	protected Checkbox assignmentEntry;
	protected Hbox hbox_assignmentEntry;

	protected Label label_TransactionEntryDialog_Bulking;
	protected Checkbox bulking;
	protected Hbox hbox_bulking;

	private List<ValueLabel> listRecOrPay = PennantStaticListUtil.getReceivableOrPayable();
	public static final int DEFAULT_TYPE = PennantConstants.RECEIVABLE;

	// ### END SFA_20210405 <--

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
	 */
	public void onCreate$window_TransactionEntryDialog(Event event) {
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
		this.vasCodeListbox.setHeight((listboxHeight + 15) + "px");
		this.expenseCodeListbox.setHeight((listboxHeight + 15) + "px");
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

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		// ### START SFA_20210405 -->
		if (!FeeExtension.ALLOW_SINGLE_FEE_CONFIG) {
			this.row_FeeRepeat.setVisible(false);
			this.row_AssignEntry.setVisible(false);
			this.feeRepeat.setChecked(false);
			this.receivableOrPayable.setVisible(false);
			this.assignmentEntry.setChecked(false);
			this.bulking.setChecked(false);
		}
		// ### END SFA_20210405 <--

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
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	protected void doPostClose() {
		getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(true);
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
	 * @param aTransactionEntry TransactionEntry
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
			this.lovDescTranscationCodeName.setValue(aTransactionEntry.getLovDescTranscationCodeName().contains("-")
					? aTransactionEntry.getLovDescTranscationCodeName()
					: aTransactionEntry.getTranscationCode() + "-" + aTransactionEntry.getLovDescTranscationCodeName());
		} else {
			this.lovDescTranscationCodeName.setValue("");
		}
		if (aTransactionEntry.getLovDescRvsTransactionCodeName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescRvsTransactionCodeName())) {
			this.lovDescRvsTransactionCodeName
					.setValue(aTransactionEntry.getLovDescRvsTransactionCodeName().contains("-")
							? aTransactionEntry.getLovDescRvsTransactionCodeName()
							: aTransactionEntry.getRvsTransactionCode() + "-"
									+ aTransactionEntry.getLovDescRvsTransactionCodeName());
		} else {
			this.lovDescRvsTransactionCodeName.setValue("");
		}

		if (aTransactionEntry.getLovDescAccountTypeName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescAccountTypeName())) {
			this.lovDescAccountTypeName.setValue(aTransactionEntry.getLovDescAccountTypeName().contains("-")
					? aTransactionEntry.getLovDescAccountTypeName()
					: aTransactionEntry.getAccountType() + "-" + aTransactionEntry.getLovDescAccountTypeName());
		} else if (aTransactionEntry.getLovDescSysInAcTypeName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescSysInAcTypeName())) {
			this.lovDescAccountTypeName.setValue(aTransactionEntry.getLovDescSysInAcTypeName().contains("-")
					? aTransactionEntry.getLovDescSysInAcTypeName()
					: aTransactionEntry.getAccountType() + "-" + aTransactionEntry.getLovDescSysInAcTypeName());
		} else {
			this.lovDescAccountTypeName.setValue("");
		}

		if (aTransactionEntry.getLovDescAccountBranchName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescAccountBranchName())) {
			this.lovDescAccountBranchName.setValue(aTransactionEntry.getLovDescAccountBranchName().contains("-")
					? aTransactionEntry.getLovDescAccountBranchName()
					: aTransactionEntry.getAccountBranch() + "-" + aTransactionEntry.getLovDescAccountBranchName());
		} else {
			this.lovDescAccountBranchName.setValue("");
		}
		if (aTransactionEntry.getLovDescAccountSubHeadRuleName() != null
				&& StringUtils.isNotEmpty(aTransactionEntry.getLovDescAccountSubHeadRuleName())) {
			this.lovDescAccountSubHeadRuleName
					.setValue(aTransactionEntry.getLovDescAccountSubHeadRuleName().contains("-")
							? aTransactionEntry.getLovDescAccountSubHeadRuleName()
							: aTransactionEntry.getAccountSubHeadRule() + "-"
									+ aTransactionEntry.getLovDescAccountSubHeadRuleName());
		} else {
			this.lovDescAccountSubHeadRuleName.setValue("");
		}

		String type = aTransactionEntry.getPostToSys();
		if (StringUtils.isBlank(aTransactionEntry.getPostToSys())) {
			type = AccountConstants.POSTTOSYS_GLNPL;
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

		// ### START SFA_20210405 -->
		this.feeRepeat.setChecked(aTransactionEntry.isFeeRepeat());
		fillComboBox(this.receivableOrPayable, String.valueOf(aTransactionEntry.getReceivableOrPayable()), listRecOrPay,
				"");

		if (StringUtils.trimToEmpty(aTransactionEntry.getAccountType()).startsWith(SingleFee.FEE)
				|| this.amountRule.getValue().contains("FEE_")) {
			this.label_TransactionEntryDialog_Bulking.setVisible(true);
			this.bulking.setVisible(true);

			this.row_FeeRepeat.setVisible(true);
			// this.label_TransactionEntryDialog_AssignmentEntry.setVisible(true);
			// this.assignmentEntry.setVisible(true);
		} else {
			this.receivableOrPayable.setDisabled(true);
		}

		this.assignmentEntry.setChecked(aTransactionEntry.isAssignmentEntry());
		this.bulking.setChecked(aTransactionEntry.isBulking());
		// ### END SFA_20210405 <--

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
			aTransactionEntry.setPostToSys(this.postToCore.isChecked() ? AccountConstants.POSTTOSYS_CORE
					: (this.postToERP.isChecked() ? AccountConstants.POSTTOSYS_GLNPL : ""));
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

		// ### START SFA_20210405 -->
		try {
			aTransactionEntry.setFeeRepeat(this.feeRepeat.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strRecOrPay = null;
			if (this.receivableOrPayable.getSelectedItem() != null) {
				strRecOrPay = this.receivableOrPayable.getSelectedItem().getValue().toString();
			}
			if (strRecOrPay != null && !PennantConstants.List_Select.equals(strRecOrPay)) {
				aTransactionEntry.setReceivableOrPayable(Integer.parseInt(strRecOrPay));

			} else {
				aTransactionEntry.setReceivableOrPayable(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionEntry.setAssignmentEntry(this.assignmentEntry.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionEntry.setBulking(this.bulking.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ### END SFA_20210405 <--

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
	 */
	public void doShowDialog(TransactionEntry aTransactionEntry) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aTransactionEntry.isNewRecord()) {
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

		String lovDescEventCodeName2 = aTransactionEntry.getLovDescEventCodeName();
		tab_expense.setVisible(
				ImplementationConstants.ALLOW_IND_AS && AccountingEvent.EXPENSE.equals(lovDescEventCodeName2)
						|| AccountingEvent.INDAS.equals(lovDescEventCodeName2));

		if (ImplementationConstants.ALLOW_IND_AS && AccountingEvent.INDAS.equals(lovDescEventCodeName2)) {
			this.tab_Vas.setVisible(false);
			this.tab_expense.setVisible(false);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aTransactionEntry);
			if (getTransactionEntry().isNewRecord()) {
				this.btnSearchTranscationCode.setDisabled(true);
				this.btnSearchRvsTransactionCode.setDisabled(true);
			}

			getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(false);
			getAccountingSetDialogCtrl().window_AccountingSetDialog.getParent()
					.appendChild(window_TransactionEntryDialog);
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
			this.transOrder.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_TransactionEntryDialog_TransOrder.value"), true));
		}
		if (!this.transDesc.isReadonly()) {
			this.transDesc.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_TransactionEntryDialog_TransDesc.value") }));
		}
		if (!this.debitcredit.isDisabled()) {
			this.debitcredit.setConstraint(new StaticListValidator(PennantStaticListUtil.getTranType(),
					Labels.getLabel("label_TransactionEntryDialog_Debitcredit.value")));
		}
		if (!this.receivableOrPayable.isDisabled()) {
			this.receivableOrPayable
					.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceivableOrPayable(),
							Labels.getLabel("label_TransactionEntryDialog_RecOrPay.value")));
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
		this.receivableOrPayable.setConstraint("");
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
						|| this.account.getSelectedItem().getValue().toString().equals(AccountConstants.TRANACC_CUSTSYS)
						|| this.account.getSelectedItem().getValue().toString()
								.equals(AccountConstants.TRANACC_BUILD)) {
					this.lovDescAccountTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_TransactionEntryDialog_AccountType.value") }));
					if (this.account.getSelectedItem().getValue().toString().equals(AccountConstants.TRANACC_GLNPL)
							|| this.account.getSelectedItem().getValue().toString()
									.equals(AccountConstants.TRANACC_BUILD)) {
						this.lovDescAccountSubHeadRuleName
								.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] {
										Labels.getLabel("label_TransactionEntryDialog_AccountSubHeadRule.value") }));
					}
				}
			}

			if (!this.account.isDisabled()) {
				this.account.setConstraint(new StaticListValidator(
						PennantStaticListUtil.getTransactionalAccount(ImplementationConstants.ALLOW_RIA),
						Labels.getLabel("label_TransactionEntryDialog_Account.value")));
			}
		}

		if (!this.btnSearchTranscationCode.isDisabled()) {
			this.lovDescTranscationCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_TransactionEntryDialog_TranscationCode.value") }));
		}

		if (!this.btnSearchAccountSubHeadRule.isDisabled()) {
			this.lovDescAccountSubHeadRuleName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_TransactionEntryDialog_AccountSubHeadRule.value") }));
		}

		if (!this.btnSearchAccountType.isDisabled()) {
			this.lovDescAccountTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_TransactionEntryDialog_AccountType.value") }));
		}

		if (!this.btnSearchRvsTransactionCode.isDisabled()) {
			this.lovDescRvsTransactionCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
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

	protected boolean doCustomDelete(final TransactionEntry aTransactionEntry, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		AuditHeader auditHeader = newTranEntryProcess(aTransactionEntry, tranType);
		auditHeader = ErrorControl.showErrorDetails(this.window_TransactionEntryDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			getAccountingSetDialogCtrl().doFilllistbox(this.transactionEntryList);
			// window_TransactionEntryDialog.onClose();
			getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(true);
			return true;

		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final TransactionEntry aTransactionEntry = new TransactionEntry();
		BeanUtils.copyProperties(getTransactionEntry(), aTransactionEntry);

		final String keyReference = aTransactionEntry.getTransOrder() + ":" + aTransactionEntry.getTransDesc();

		doDelete(keyReference, aTransactionEntry);

		logger.debug(Literal.LEAVING);
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
		this.account.setDisabled(true);
		this.btnSearchAccountBranch.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountBranch"));
		this.btnSearchAccountType.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
		this.btnSearchSystemIntAccount.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
		this.btnSearchAccountSubHeadRule
				.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountSubHeadEule"));
		this.btnSearchTranscationCode
				.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_transcationCode"));
		this.btnSearchRvsTransactionCode
				.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_rvsTransactionCode"));
		this.postToCore.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_coreAccount"));
		this.postToERP.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_eRPCategory"));

		if (!this.accountingSetDialogCtrl.entryByInvestment.isChecked()) {
			this.label_TransactionEntryDialog_EntryByInvestment.setVisible(false);
			this.hbox_entryByInvestment.setVisible(false);
			this.entryByInvestment.setChecked(false);
		}

		// ### START SFA_20210405 -->
		// Made same as account type edit rights
		this.bulking.setDisabled(this.btnSearchAccountType.isDisabled());
		// ### END SFA_20210405 <--

		if (getUserWorkspace().isReadOnly("TransactionEntryDialog_amountRule")) {
			this.amountRule.setReadonly(true);
			this.column_CustomerData.setWidth("0%");
			this.amountCodeListbox.setVisible(false);
			this.feeCodeListbox.setVisible(false);
			this.vasCodeListbox.setVisible(false);
			this.expenseCodeListbox.setVisible(false);
			this.column_RULE.setWidth("100%");
			this.column_Operators.setWidth("0%");
			this.operator.setVisible(false);
		} else {
			this.amountRule.setReadonly(false);
			this.column_CustomerData.setWidth("30%");
			this.amountCodeListbox.setVisible(true);
			this.feeCodeListbox.setVisible(true);
			this.vasCodeListbox.setVisible(true);
			this.expenseCodeListbox.setVisible(true);
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
		this.expenseCodeListbox.setVisible(false);
		this.column_RULE.setWidth("100%");
		this.column_Operators.setWidth("0%");
		this.operator.setVisible(false);

		// ### START SFA_20210405 -->
		this.assignmentEntry.setDisabled(this.btnSearchAccountType.isDisabled());
		// ### END SFA_20210405 <--

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

		// ### START SFA_20210405 -->
		this.feeRepeat.setChecked(false);
		this.receivableOrPayable.setValue("");
		this.assignmentEntry.setChecked(false);
		this.bulking.setChecked(false);
		// ### END SFA_20210405 <--

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

		isNew = aTransactionEntry.isNewRecord();
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
			if (isGSTApplicable) {
				MessageUtil.showMessage(Labels.getLabel("label_GstApplicable"));
				isGSTApplicable = false;
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

		boolean derivedEntryReq = false;

		errParm[0] = PennantJavaUtil.getLabel("label_FeeTranEvent") + ":" + valueParm[0] + " "
				+ PennantJavaUtil.getLabel("label_AccountingSetDialog_AccountSetCode.value") + ":" + valueParm[1];
		errParm[1] = PennantJavaUtil.getLabel("label_TransactionEntryDialog_TransOrder.value") + ":" + valueParm[2];
		// getAccountingSetDialogCtrl().getTransactionEntryList();
		if (getAccountingSetDialogCtrl().getTransactionEntryList() != null
				&& getAccountingSetDialogCtrl().getTransactionEntryList().size() > 0) {
			for (int i = 0; i < getAccountingSetDialogCtrl().getTransactionEntryList().size(); i++) {
				TransactionEntry transactionEntry = getAccountingSetDialogCtrl().getTransactionEntryList().get(i);

				if (transactionEntry.getLovDescEventCodeName().equals(aTransactionEntry.getLovDescEventCodeName())
						&& transactionEntry.getLovDescAccSetCodeName()
								.equals(aTransactionEntry.getLovDescAccSetCodeName())
						&& transactionEntry.getTransOrder() == aTransactionEntry.getTransOrder()
						|| (transactionEntry.getTransOrder() == (aTransactionEntry.getTransOrder() + 1)
								&& derivedEntryReq)) {

					if (derivedEntryReq && (transactionEntry.getTransOrder() == (aTransactionEntry.getTransOrder() + 1))
							&& aTransactionEntry.isNewRecord()
							&& StringUtils.equals(aTransactionEntry.getRecordType(), PennantConstants.RCD_ADD)
							&& StringUtils.equals(aTransactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_CORE)) {

						valueParm[2] = String.valueOf(aTransactionEntry.getTransOrder() + 1);
						errParm[1] = PennantJavaUtil.getLabel("label_TransactionEntryDialog_TransOrder.value") + ":"
								+ valueParm[2];
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "30549", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					// Both Current and Existing list Transaction Order is same
					if (aTransactionEntry.isNewRecord()
							&& transactionEntry.getTransOrder() == (aTransactionEntry.getTransOrder())) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
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
											&& fee.getLovDescEventCodeName()
													.equals(aTransactionEntry.getLovDescEventCodeName())) {
										transactionEntryList.add(fee);
									}
								}
							} else if (PennantConstants.RECORD_TYPE_DEL.equals(aTransactionEntry.getRecordType())) {
								aTransactionEntry.setNewRecord(true);
							}
						} else {
							if (derivedEntryReq) {
								TransactionEntry derivedEntry = verifyDerivedTranOrder(transactionEntry,
										aTransactionEntry, false);
								if (derivedEntry != null) {
									derivedEntryAdded = true;
									transactionEntryList.add(derivedEntry);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							if (derivedEntryReq) {
								TransactionEntry derivedEntry = verifyDerivedTranOrder(transactionEntry,
										aTransactionEntry, false);
								if (derivedEntry != null) {
									transactionEntryList.add(derivedEntry);
								} else {
									transactionEntryList.add(transactionEntry);
								}
							} else {
								transactionEntryList.add(transactionEntry);
							}
						} else {
							if (!(StringUtils.equals(aTransactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_CORE)
									&& (transactionEntry != null && StringUtils.equals(transactionEntry.getPostToSys(),
											AccountConstants.POSTTOSYS_GLNPL)))) {
								if (derivedEntryReq) {
									TransactionEntry derivedEntry = verifyDerivedTranOrder(transactionEntry,
											aTransactionEntry, false);
									if (derivedEntry != null) {
										transactionEntryList.add(derivedEntry);
										derivedEntryAdded = true;
									}
								}
							}
						}
					}
				} else {

					if (derivedEntryReq) {
						TransactionEntry derivedEntry = null;

						if (!(StringUtils.equals(aTransactionEntry.getPostToSys(), AccountConstants.POSTTOSYS_CORE)
								&& (transactionEntry != null && StringUtils.equals(transactionEntry.getPostToSys(),
										AccountConstants.POSTTOSYS_GLNPL)))) {
							derivedEntry = verifyDerivedTranOrder(transactionEntry, aTransactionEntry, false);
						}
						if (derivedEntry != null) {
							transactionEntryList.add(derivedEntry);
							derivedEntryAdded = true;
						} else {
							transactionEntryList.add(transactionEntry);
						}
					} else {
						transactionEntryList.add(transactionEntry);
					}
				}
			}
		}
		if (!recordAdded) {
			if (derivedEntryReq) {
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

			// If System posting entry from T24 to ERp modification, then
			// existing derived entry should be removed.
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

		// Adding PFF Clearing Account Transaction Entry on Selection System
		// Entry
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
			derivedEntry.setDebitcredit(aTransactionEntry.getDebitcredit().equals(AccountConstants.TRANTYPE_CREDIT)
					? AccountConstants.TRANTYPE_DEBIT
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
		dataObject = ExtendedSearchListBox.show(this.window_TransactionEntryDialog, "AccountType");

		if (dataObject instanceof String) {
			this.accountType.setValue(dataObject.toString());
			this.lovDescAccountTypeName.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				if (details.isTaxApplicable()) {
					isGSTApplicable = true;
				}
				this.accountType.setValue(details.getAcType());
				this.lovDescAccountTypeName.setValue(details.getAcType() + "-" + details.getAcTypeDesc());

				// ### START SFA_20210405 -->
				this.feeRepeat.setChecked(false);
				this.receivableOrPayable.setValue(Labels.getLabel("Combo.Select"));
				this.receivableOrPayable.setDisabled(true);
				this.label_TransactionEntryDialog_Bulking.setVisible(false);
				this.assignmentEntry.setDisabled(true);
				this.bulking.setVisible(false);
				this.row_FeeRepeat.setVisible(false);

				if (details.getAcType().startsWith(SingleFee.FEE)) {
					this.feeRepeat.setChecked(true);
					this.receivableOrPayable.setDisabled(false);
					this.receivableOrPayable.setValue(Labels.getLabel("Combo.Select"));
					this.label_TransactionEntryDialog_Bulking.setVisible(true);
					this.assignmentEntry.setDisabled(false);
					this.bulking.setVisible(true);
					this.row_FeeRepeat.setVisible(true);
					getSingleFeeCodes();

				} else {
					getSingleFeeCodes();
					getAllFeeCodes();
				}
				// ### END SFA_20210405 <--
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	private void getSingleFeeCodes() {
		this.feeCodeListbox.getItems().clear();
		List<FeeType> feeRulesList = new ArrayList<FeeType>();

		feeRulesList.add(new FeeType());
		feeRulesList.get(feeRulesList.size() - 1).setFeeTypeCode("FEE");
		feeRulesList.get(feeRulesList.size() - 1).setFeeTypeDesc("Fee Type");

		prepareFeeAmountsList(feeRulesList);
	}

	private void prepareFeeAmountsList(List<FeeType> feeRulesList) {
		Listitem item = null;
		Listgroup group = null;
		Listcell lc = null;

		Map<String, String> feeMap = fillAccountingDetails();
		List<String> feeMapKeys = new ArrayList<>(feeMap.keySet());

		for (FeeType feeType : feeRulesList) {
			String feeTypeCode = feeType.getFeeTypeCode();
			String feeTypeDesc = feeType.getFeeTypeDesc();

			group = new Listgroup(feeTypeCode);
			this.feeCodeListbox.appendChild(group);
			group.setOpen(false);

			for (String feeCode : feeMapKeys) {
				String ruleCode = feeTypeCode + feeCode;
				String ruleCodeDesc = feeTypeDesc + feeCode;

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
		}
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
				filter[0] = new Filter("TranType",
						new String[] { AccountConstants.TRANTYPE_DEBIT, AccountConstants.TRANTYPE_BOTH }, Filter.OP_IN);
			} else if (StringUtils.equals(AccountConstants.TRANTYPE_DEBIT, tranType)) {
				filter[0] = new Filter("TranType",
						new String[] { AccountConstants.TRANTYPE_CREDIT, AccountConstants.TRANTYPE_BOTH },
						Filter.OP_IN);
			}
		} else {
			if (StringUtils.equals(AccountConstants.TRANTYPE_CREDIT, tranType)) {
				filter[0] = new Filter("TranType",
						new String[] { AccountConstants.TRANTYPE_CREDIT, AccountConstants.TRANTYPE_BOTH },
						Filter.OP_IN);
			} else if (StringUtils.equals(AccountConstants.TRANTYPE_DEBIT, tranType)) {
				filter[0] = new Filter("TranType",
						new String[] { AccountConstants.TRANTYPE_DEBIT, AccountConstants.TRANTYPE_BOTH }, Filter.OP_IN);
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

		if (allowedevent.endsWith("_S")) {
			allowedevent = allowedevent.replace("_S", "");
		} else if (allowedevent.endsWith("_N")) {
			allowedevent = allowedevent.replace("_N", "");
		} else if (allowedevent.endsWith("_W")) {
			allowedevent = allowedevent.replace("_W", "");
		}

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

	// ### START SFA_20210405 -->
	// Earlier code moved to getAllFeeCodes
	private void getFeeCodes() {
		if (SingleFee.FEE.equals(this.accountType.getValue())) {
			getSingleFeeCodes();
		} else {
			getSingleFeeCodes();
			getAllFeeCodes();
		}
	}

	/**
	 * Method for get the list of Rules existing on base of Event
	 * 
	 * @param allowedevent
	 */
	private void getAllFeeCodes() {
		logger.debug(Literal.ENTERING);

		JdbcSearchObject<FeeType> searchObj = new JdbcSearchObject<FeeType>(FeeType.class);
		searchObj.addTabelName("FeeTypes");
		searchObj.addFilter(new Filter("Active", 1, Filter.OP_EQUAL));

		String event = this.eventCode.getValue();
		if (ImplementationConstants.ALLOW_IND_AS && AccountingEvent.INDAS.equals(event)) {
			searchObj.addFilter(new Filter("AmortzReq", 1, Filter.OP_EQUAL));
		}

		ArrayList<String> list = new ArrayList<>();

		if (!ImplementationConstants.ALLOW_TDS_ON_FEE) {
			list.add(Allocation.BOUNCE);
			list.add(Allocation.ODC);
			list.add(Allocation.LPFT);
		}

		list.add("KOFF_EMI");
		list.add("KOFF_LPI");

		if (!ImplementationConstants.ALLOW_DSF_CASHCLT) {
			list.add("CASHCLT");
			list.add("DSF");
		}

		searchObj.addFilter(Filter.notIn("FeeTypeCode", list));

		List<FeeType> feeRulesList = this.pagedListService.getBySearchObject(searchObj);

		Listitem item = null;
		Listgroup group = null;
		Listcell lc = null;

		Map<String, String> feeMap = fillAccountingDetails();
		List<String> feeMapKeys = new ArrayList<>(feeMap.keySet());

		for (FeeType feeType : feeRulesList) {
			String feeTypeCode = feeType.getFeeTypeCode();
			String feeTypeDesc = feeType.getFeeTypeDesc();

			group = new Listgroup(feeTypeCode);
			this.feeCodeListbox.appendChild(group);
			group.setOpen(false);

			for (String feeCode : feeMapKeys) {
				String ruleCode = feeTypeCode + feeCode;
				String ruleCodeDesc = feeTypeDesc + feeCode;

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
		}

		logger.debug(Literal.LEAVING);
	}

	private void getExpenseCodes() {
		logger.debug(Literal.ENTERING);

		this.expenseCodeListbox.getItems().clear();

		JdbcSearchObject<ExpenseType> searchObj = new JdbcSearchObject<ExpenseType>(ExpenseType.class);
		searchObj.addTabelName("ExpenseTypes");
		searchObj.addFilter(new Filter("Active", 1, Filter.OP_EQUAL));
		searchObj.addFilter(new Filter("AmortReq", 1, Filter.OP_EQUAL));

		List<ExpenseType> feeRulesList = this.pagedListService.getBySearchObject(searchObj);

		Listitem item = null;
		Listgroup group = null;
		Listcell lc = null;

		for (ExpenseType expenseType : feeRulesList) {
			String code = expenseType.getExpenseTypeCode();
			String decription = expenseType.getExpenseTypeDesc();

			group = new Listgroup(code);
			this.expenseCodeListbox.appendChild(group);
			group.setOpen(false);

			if (AccountingEvent.EXPENSE.equals(this.eventCode.getValue())) {
				String amountCode = code + "_AMZ_N";
				item = new Listitem();
				lc = new Listcell(amountCode);

				if (!amountcodes.contains(amountCode)) {
					amountcodes.add(amountCode);
				}

				lc.setParent(item);
				lc = new Listcell(decription + " " + Labels.getLabel("label_TransactionEntryDialog_Expense_AMZ_N"));
				lc.setParent(item);
				this.expenseCodeListbox.appendChild(item);
			}

			if (AccountingEvent.INDAS.equals(this.eventCode.getValue())) {
				String amountCode = code + "_AMZ";
				item = new Listitem();
				lc = new Listcell(amountCode);

				if (!amountcodes.contains(amountCode)) {
					amountcodes.add(amountCode);
				}

				lc.setParent(item);
				lc = new Listcell(decription + " " + Labels.getLabel("label_TransactionEntryDialog_Expense_AMZ"));
				lc.setParent(item);
				this.expenseCodeListbox.appendChild(item);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	private void getVasCodes(String allowedevent) {
		logger.debug("Entering");

		this.vasCodeListbox.getItems().clear();
		List<VASConfiguration> vasRulesList = new ArrayList<>();
		JdbcSearchObject<VASConfiguration> searchObj = new JdbcSearchObject<>(VASConfiguration.class);
		searchObj.addTabelName("VasStructure");
		searchObj.addFilter(new Filter("Active", 1, Filter.OP_EQUAL));

		vasRulesList = this.pagedListService.getBySearchObject(searchObj);
		Listitem item = null;
		Listgroup group = null;
		Listcell lc = null;

		Map<String, String> vasMap = fillVasDetails();
		List<String> vasMapKeys = new ArrayList<>(vasMap.keySet());

		if (vasRulesList.isEmpty()) {
			this.tab_Vas.setVisible(false);
		} else {
			String ruleCode = "";
			String ruleCodeDesc = "";
			for (String vasMapKey : vasMapKeys) {
				ruleCode = "VAS" + vasMapKey;
				ruleCodeDesc = "Total " + vasMap.get(vasMapKey);

				item = new Listitem();
				lc = new Listcell(ruleCode);

				if (!amountcodes.contains(ruleCode)) {
					amountcodes.add(ruleCode);
				}

				lc.setParent(item);
				lc = new Listcell(ruleCodeDesc);
				lc.setParent(item);
				this.vasCodeListbox.appendChild(item);
			}
		}

		for (VASConfiguration vasRule : vasRulesList) {
			String vasTypeCode = vasRule.getProductCode();
			String vasTypeDesc = vasRule.getProductDesc();

			group = new Listgroup(vasTypeCode);
			this.vasCodeListbox.appendChild(group);
			group.setOpen(false);

			String ruleCode = "";
			String ruleCodeDesc = "";

			for (String vasMapKey : vasMapKeys) {

				ruleCode = "VAS_" + vasTypeCode + vasMapKey;
				ruleCodeDesc = vasMap.get(vasMapKey) + "(" + vasTypeDesc + ")";

				item = new Listitem();
				lc = new Listcell(ruleCode);

				if (!amountcodes.contains(ruleCode)) {
					amountcodes.add(ruleCode);
				}

				lc.setParent(item);
				lc = new Listcell(ruleCodeDesc);
				lc.setParent(item);
				this.vasCodeListbox.appendChild(item);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Adding the Fee Extensions and labels
	 * 
	 * @return
	 */
	private Map<String, String> fillAccountingDetails() {
		Map<String, String> feeMap = new HashMap<>();

		if (ImplementationConstants.ALLOW_IND_AS && AccountingEvent.INDAS.equals(this.eventCode.getValue())) {
			feeMap.put("_AMZ", Labels.getLabel("label_TransactionEntryDialog_Fee_AMZ"));
			feeMap.put("_AMZ_N", Labels.getLabel("label_TransactionEntryDialog_Fee_AMZ_N"));
			feeMap.put("_AMZ_BAL", Labels.getLabel("label_TransactionEntryDialog_Fee_AMZ_BAL"));
			return feeMap;
		}

		feeMap.put("_N", Labels.getLabel("label_TransactionEntryDialog_N"));
		feeMap.put("_W", Labels.getLabel("label_TransactionEntryDialog_FeeWaiver"));
		feeMap.put("_P", Labels.getLabel("label_TransactionEntryDialog_FeePaid"));
		feeMap.put("_AF", Labels.getLabel("label_TransactionEntryDialog_FeeAF"));
		feeMap.put("_SCH", Labels.getLabel("label_TransactionEntryDialog_FeeSF"));
		feeMap.put("_R", Labels.getLabel("label_TransactionEntryDialog_FeeReFunded"));

		// GST Fields
		feeMap.put("_CGST_C", Labels.getLabel("label_TransactionEntryDialog_CGST"));
		feeMap.put("_CGST_N", Labels.getLabel("label_TransactionEntryDialog_CGST"));
		feeMap.put("_CGST_P", Labels.getLabel("label_TransactionEntryDialog_CGST"));
		feeMap.put("_CGST_SCH", Labels.getLabel("label_TransactionEntryDialog_CGST"));
		feeMap.put("_CGST_AF", Labels.getLabel("label_TransactionEntryDialog_CGST"));
		feeMap.put("_CGST_R", Labels.getLabel("label_TransactionEntryDialog_R_CGST"));

		feeMap.put("_SGST_C", Labels.getLabel("label_TransactionEntryDialog_SGST"));
		feeMap.put("_SGST_N", Labels.getLabel("label_TransactionEntryDialog_SGST"));
		feeMap.put("_SGST_P", Labels.getLabel("label_TransactionEntryDialog_SGST"));
		feeMap.put("_SGST_SCH", Labels.getLabel("label_TransactionEntryDialog_SGST"));
		feeMap.put("_SGST_AF", Labels.getLabel("label_TransactionEntryDialog_SGST"));
		feeMap.put("_SGST_R", Labels.getLabel("label_TransactionEntryDialog_R_SGST"));

		feeMap.put("_UGST_C", Labels.getLabel("label_TransactionEntryDialog_UGST"));
		feeMap.put("_UGST_N", Labels.getLabel("label_TransactionEntryDialog_UGST"));
		feeMap.put("_UGST_P", Labels.getLabel("label_TransactionEntryDialog_UGST"));
		feeMap.put("_UGST_SCH", Labels.getLabel("label_TransactionEntryDialog_UGST"));
		feeMap.put("_UGST_AF", Labels.getLabel("label_TransactionEntryDialog_UGST"));
		feeMap.put("_UGST_R", Labels.getLabel("label_TransactionEntryDialog_R_UGST"));

		feeMap.put("_IGST_C", Labels.getLabel("label_TransactionEntryDialog_IGST"));
		feeMap.put("_IGST_N", Labels.getLabel("label_TransactionEntryDialog_IGST"));
		feeMap.put("_IGST_P", Labels.getLabel("label_TransactionEntryDialog_IGST"));
		feeMap.put("_IGST_SCH", Labels.getLabel("label_TransactionEntryDialog_IGST"));
		feeMap.put("_IGST_AF", Labels.getLabel("label_TransactionEntryDialog_IGST"));
		feeMap.put("_IGST_R", Labels.getLabel("label_TransactionEntryDialog_R_IGST"));

		feeMap.put("_CESS_C", Labels.getLabel("label_TransactionEntryDialog_CESS"));
		feeMap.put("_CESS_N", Labels.getLabel("label_TransactionEntryDialog_CESS"));
		feeMap.put("_CESS_P", Labels.getLabel("label_TransactionEntryDialog_CESS"));
		feeMap.put("_CESS_SCH", Labels.getLabel("label_TransactionEntryDialog_CESS"));
		feeMap.put("_CESS_AF", Labels.getLabel("label_TransactionEntryDialog_CESS"));
		feeMap.put("_CESS_R", Labels.getLabel("label_TransactionEntryDialog_R_CESS"));

		// Waivers
		feeMap.put("_CGST_W", Labels.getLabel("label_TransactionEntryDialog_W_CGST"));
		feeMap.put("_SGST_W", Labels.getLabel("label_TransactionEntryDialog_W_SGST"));
		feeMap.put("_UGST_W", Labels.getLabel("label_TransactionEntryDialog_W_UGST"));
		feeMap.put("_IGST_W", Labels.getLabel("label_TransactionEntryDialog_W_IGST"));
		feeMap.put("_CESS_W", Labels.getLabel("label_TransactionEntryDialog_W_IGST"));

		// TDS
		if (ImplementationConstants.ALLOW_TDS_ON_FEE) {
			feeMap.put("_TDS_N", Labels.getLabel("label_TransactionEntryDialog_N_TDS"));
			feeMap.put("_TDS_P", Labels.getLabel("label_TransactionEntryDialog_P_TDS"));
			feeMap.put("_TDS_R", Labels.getLabel("label_TransactionEntryDialog_R_TDS"));
		}

		// REFUND
		feeMap.put("_R", Labels.getLabel("label_TransactionEntryDialog_FeeReFunded"));
		feeMap.put("_CGST_R", Labels.getLabel("label_TransactionEntryDialog_R_CGST"));
		feeMap.put("_SGST_R", Labels.getLabel("label_TransactionEntryDialog_R_SGST"));
		feeMap.put("_UGST_R", Labels.getLabel("label_TransactionEntryDialog_R_UGST"));
		feeMap.put("_IGST_R", Labels.getLabel("label_TransactionEntryDialog_R_IGST"));

		return feeMap;
	}

	private Map<String, String> fillVasDetails() {

		Map<String, String> vasMap = new HashMap<>();

		vasMap.put("_DD", Labels.getLabel("label_TransactionEntryDialog_VAS_DD"));
		vasMap.put("_AF", Labels.getLabel("label_TransactionEntryDialog_VAS_AF"));
		vasMap.put("_P", Labels.getLabel("label_TransactionEntryDialog_VAS_P"));
		vasMap.put("_W", Labels.getLabel("label_TransactionEntryDialog_VAS_W"));

		return vasMap;

	}

	/**
	 * To fill the list box with two list headers
	 * 
	 * @param listbox
	 * @param arrayList
	 * @param showFirstColumn
	 */
	private void fillListbox(Listbox listbox, List<ValueLabel> arrayList, boolean showFirstColumn) {
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
		logger.debug(Literal.ENTERING + event.toString());
		if (validate(event)) {
			MessageUtil.confirm("No Errors Found! Proceed With Simulation?", evnt -> {
				if (Messagebox.ON_YES.equals(evnt.getName())) {
					createSimulationWindow(variables);
				}
			});
		}
		logger.debug(Literal.LEAVING + event.toString());
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
		boolean containsFeeCode = false;
		boolean amountCode = false;
		Set<String> hSet = new HashSet<String>();
		boolean excludeFlag = false;
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

						// ### START SFA_20210405 -->
						hSet = getExcludedAmountCodes();
						if (this.accountType.getValue().startsWith(SingleFee.FEE)
								&& !variable.get("name").toString().startsWith("FEE_")
								&& !hSet.contains(variable.get("name").toString())) {
							noerrors = false;
							MessageUtil.showError(
									"Rule shouldn't contain this amount code when Account type is - FEE_IE/FEE_IE_WR/FEE_IE_CGST/FEE_IE_SGST/FEE_IE_UGST \n"
											+ "/FEE_IE_IGST/FEE_IE_CESS");
							return noerrors;
						}
						if (variable.get("name").toString().startsWith("FEE_")) {
							containsFeeCode = true;
						}
						if (!variable.get("name").toString().startsWith("FEE_")) {
							amountCode = true;

						}
						if (hSet.contains(variable.get("name").toString())) {
							excludeFlag = true;
						}

						if (containsFeeCode && amountCode) {
							if (!excludeFlag) {
								noerrors = false;
								MessageUtil.showError("Can't club amount code with fee code");
								return noerrors;
							}
						}
						// ### END SFA_20210405 <--

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
				if (noerrors && !excludeFlag) {
					// ### START SFA_20210405 -->
					boolean validationRslt = false;
					validationRslt = validateResult();
					if (validationRslt && containsFeeCode) {
						this.feeRepeat.setChecked(true);
						this.receivableOrPayable.setDisabled(false);
						this.receivableOrPayable.setValue(this.receivableOrPayable.getValue());
						this.label_TransactionEntryDialog_Bulking.setVisible(true);
						this.assignmentEntry.setDisabled(false);
						this.bulking.setVisible(true);
						this.row_FeeRepeat.setVisible(true);
					} else {
						this.feeRepeat.setChecked(false);
						this.receivableOrPayable.setValue(Labels.getLabel("Combo.Select"));
						this.receivableOrPayable.setDisabled(true);
						this.label_TransactionEntryDialog_Bulking.setVisible(false);
						this.assignmentEntry.setDisabled(true);
						this.bulking.setVisible(false);
						this.row_FeeRepeat.setVisible(false);
					}
					// ### END SFA_20210405 <--
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

	public Set<String> getExcludedAmountCodes() {
		Set<String> excludedFields = new HashSet<String>();
		excludedFields.add("negativeFee");
		excludedFields.add("ae_assignmentPerc");
		return excludedFields;
	}

	/**
	 * CALL THE RESULT ZUL FILE
	 * 
	 * @param jsonArray
	 * @throws InterruptedException
	 */
	public void createSimulationWindow(JSONArray jsonArray) throws InterruptedException {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
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
		List<ValueLabel> chargeTypes = PennantStaticListUtil.getChargeTypes();
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
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_TransactionEntryDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
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
		this.row_ShadowPosting.setVisible(ImplementationConstants.ALLOW_SHADOW_POSTINGS);
		// this.btnSearchAccountType.setDisabled(true);
		this.btnSearchSystemIntAccount.setDisabled(true);
		// this.btnSearchAccountSubHeadRule.setDisabled(true);
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
				this.btnSearchAccountType.setVisible(true);
				this.btnSearchSystemIntAccount.setVisible(false);
				this.btnSearchAccountType
						.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
				this.accountType.setValue(null);
				this.spAccountType.setSclass("mandatory");
				this.btnSearchAccountSubHeadRule
						.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountSubHeadEule"));
				this.openNewFinAc.setChecked(false);
			} else if (value.equals(AccountConstants.TRANACC_CUSTSYS)) {
				this.btnSearchAccountType.setVisible(true);
				this.btnSearchAccountType
						.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
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
		this.amountcodes.clear();
		getAmountCodes(getTransactionEntry().getLovDescEventCodeName());
		getFeeCodes();
		getVasCodes(getTransactionEntry().getLovDescEventCodeName());
		if (tab_expense.isVisible()) {
			getExpenseCodes();
		}

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

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setAccountingSetDialogCtrl(AccountingSetDialogCtrl accountingSetDialogCtrl) {
		this.accountingSetDialogCtrl = accountingSetDialogCtrl;
	}

	public AccountingSetDialogCtrl getAccountingSetDialogCtrl() {
		return accountingSetDialogCtrl;
	}

}
