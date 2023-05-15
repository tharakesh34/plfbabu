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
 * * FileName : JVPostingEntryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * *
 * Modified Date : 21-06-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.others.jvposting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.HostAccountStatus;
import com.pennant.pff.accounting.TransactionType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Others/JVPostingEntry/jVPostingEntryDialog.zul file.
 */
public class JVPostingEntryDialogCtrl extends GFCBaseCtrl<JVPostingEntry> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(JVPostingEntryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_JVPostingEntryDialog;
	protected Label window_JVPostingEntryDialog_Title;

	protected Row row0;
	protected Label label_Batch;
	protected Hlayout hlayout_Batch;
	protected Space space_Batch;
	protected Textbox batch;

	protected Label label_BatchReference;
	protected Hlayout hlayout_BatchReference;
	protected Space space_BatchReference;
	protected Textbox batchReference;

	protected Row row1;
	protected Label label_BaseCcy;
	protected Hlayout hlayout_BaseCcy;
	protected ExtendedCombobox baseCCy;

	protected Label label_PostingBranch;
	protected Hlayout hlayout_postingBranch;
	protected ExtendedCombobox postingBranch;

	protected Row row2;
	protected Label label_TotDebitsByBatchCcy;
	protected Hlayout hlayout_TotDebitsByBatchCcy;
	protected Space space_TotDebitsByBatchCcy;
	protected Decimalbox totDebitsByBatchCcy;
	protected Row row13;
	protected Label label_TdsAdjustmentRequired;
	protected Hlayout hlayout_TdsAdjustmentRequired;
	protected Space space_TdsAdjustmentRequired;
	protected Checkbox tdsAdjustmentReq;

	protected Label label_TotCreditsByBatchCcy;
	protected Hlayout hlayout_TotCreditsByBatchCcy;
	protected Space space_TotCreditsByBatchCcy;
	protected Decimalbox totCreditsByBatchCcy;

	protected Row row3;
	protected Label label_DebitCount;
	protected Hlayout hlayout_DebitCount;
	protected Space space_DebitCount;
	protected Intbox debitCount;

	protected Label label_CreditsCount;
	protected Hlayout hlayout_CreditsCount;
	protected Space space_CreditsCount;
	protected Intbox creditsCount;

	protected Row row4;
	protected Label label_BatchPurpose;
	protected Hlayout hlayout_BatchPurpose;
	protected Space space_BatchPurpose;
	protected Textbox batchPurpose;

	protected Row row5;
	protected Label label_Account;
	protected Hlayout hlayout_Account;
	protected ExtendedCombobox account;

	protected Label label_AccountName;
	protected Hlayout hlayout_AccountName;
	protected Textbox accountName;
	protected Label label_AccountCurrency;

	protected Row row6;
	protected Label label_TxnCCy;
	protected Hlayout hlayout_TxnCCy;
	protected Space space_TxnCCy;
	protected ExtendedCombobox txnCCy;

	/*
	 * protected Label label_TxnType; protected Hlayout hlayout_TxnType; protected Space space_TxnType; protected
	 * Combobox txnType;
	 */

	protected Row row7;
	protected Label label_TxnAmount;
	protected Hlayout hlayout_TxnAmount;
	protected CurrencyBox txnAmount;
	protected Decimalbox exchange_Converted_txnAmount;
	protected Label label_ExTxnAmount_Ac;

	protected Label label_TxnCode;
	protected Hlayout hlayout_TxnCode;
	protected Space space_TxnCode;
	protected ExtendedCombobox txnCode;

	protected Row row8;
	protected Label label_TxnReference;
	protected Hlayout hlayout_TxnReference;
	protected Space space_TxnReference;
	protected Intbox txnReference;

	protected Label label_ExchangeRate;
	protected Hlayout hlayout_ExchangeRate;
	protected Space space_ExchangeRate;
	protected Decimalbox exchangeRate;

	protected Row row9;
	protected Label label_ValueDate;
	protected Hlayout hlayout_ValueDate;
	protected Space space_ValueDate;
	protected Datebox valueDate;

	protected Label label_PostingDate;
	protected Hlayout hlayout_PostingDate;
	protected Space space_PostingDate;
	protected Datebox postingDate;

	protected Row row10;
	protected Label label_NarrLine1;
	protected Hlayout hlayout_NarrLine1;
	protected Space space_NarrLine1;
	protected Textbox narrLine1;

	protected Label label_NarrLine2;
	protected Hlayout hlayout_NarrLine2;
	protected Space space_NarrLine2;
	protected Textbox narrLine2;

	protected Row row11;
	protected Label label_NarrLine3;
	protected Hlayout hlayout_NarrLine3;
	protected Space space_NarrLine3;
	protected Textbox narrLine3;

	protected Label label_NarrLine4;
	protected Hlayout hlayout_NarrLine4;
	protected Space space_NarrLine4;
	protected Textbox narrLine4;

	protected Row row12;
	protected Label label_DebitAccount;
	protected Hlayout hlayout_DebitAccount;
	protected ExtendedCombobox debitAccount;
	protected Label label_DebitAccountCurrency;

	protected Label label_DebitTxnCode;
	protected Hlayout hlayout_DebitTxnCode;
	protected Space space_DebitTxnCode;
	protected ExtendedCombobox debitTxnCode;

	protected Label recordType;
	protected Groupbox gb_statusDetails;
	private boolean enqModule = false;
	private boolean rePostingModule = false;

	// ServiceDAOs / Domain Classes
	private transient JVPostingService jVPostingService;
	private List<ValueLabel> listTxnType = PennantStaticListUtil.getTranType();
	private List<JVPostingEntry> deletedJVPostingEntryList = new ArrayList<JVPostingEntry>();
	private List<JVPostingEntry> jvPostingEntryList = new ArrayList<JVPostingEntry>();

	// ==========================JV Posting Entry
	private JVPostingDialogCtrl jVPostingDialogCtrl;
	// not auto wired vars
	private JVPosting jVPosting; // overhanded per param
	private JVPostingEntry jVPostingEntry; // overhanded per param
	private JVPostingEntry old_JVPostingEntry; // overhanded per param
	private boolean newRecord = false;

	/**
	 * default constructor.<br>
	 */
	public JVPostingEntryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "JVPostingEntryDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected JVPostingEntry object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_JVPostingEntryDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_JVPostingEntryDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else if (arguments.containsKey("rePostingModule")) {
				rePostingModule = (Boolean) arguments.get("rePostingModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("jVPostingDialogCtrl")) {
				setJVPostingDialogCtrl((JVPostingDialogCtrl) arguments.get("jVPostingDialogCtrl"));
				setjVPosting(getJVPostingDialogCtrl().getJVPosting());
				setDeletedJVPostingEntryList(getJVPostingDialogCtrl().getDeletedJVPostingEntryList());
			} else {
				setJVPostingDialogCtrl(null);
			}

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			// READ OVERHANDED params !
			if (arguments.containsKey("jVPostingEntry")) {
				this.jVPostingEntry = (JVPostingEntry) arguments.get("jVPostingEntry");
				JVPostingEntry befImage = new JVPostingEntry();
				BeanUtils.copyProperties(this.jVPostingEntry, befImage);
				this.jVPostingEntry.setBefImage(befImage);

				setJVPostingEntry(this.jVPostingEntry);
			} else {
				setJVPostingEntry(null);
			}

			if (arguments.containsKey("jVPosting")) {
				this.jVPosting = (JVPosting) arguments.get("jVPosting");
				JVPosting befImage = new JVPosting();
				BeanUtils.copyProperties(this.jVPosting, befImage);
				this.jVPosting.setBefImage(befImage);

				setjVPosting(this.jVPosting);
				this.jVPostingEntry.setWorkflowId(0);
			} else {
				setjVPosting(null);
			}
			doLoadWorkFlow(this.jVPostingEntry.isWorkflow(), this.jVPostingEntry.getWorkflowId(),
					this.jVPostingEntry.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			/* set components visible dependent of the users rights */
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			getUserWorkspace().allocateAuthorities("JVPostingEntryDialog", getRole());
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getJVPostingEntry());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_JVPostingEntryDialog.onClose();
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
		displayComponents(ScreenCTL.SCRN_GNEDT);
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
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doWriteBeanToComponents(this.jVPostingEntry.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
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
		MessageUtil.showHelpWindow(event, window_JVPostingEntryDialog);
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
		getJVPostingDialogCtrl().getWindow_JVPostingDialog().setVisible(true);
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(getNotes("JVPostingEntry", String.valueOf(getJVPostingEntry().getBatchReference()),
					getJVPostingEntry().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onFulfill$txnCCy(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Object dataObject = this.txnCCy.getObject();
		try {
			if (dataObject instanceof String) {
				this.txnCCy.setValue(dataObject.toString());
				this.txnCCy.setDescription("");
				doResetValues();
			} else {
				if (dataObject != null) {
					Currency aCurrency = (Currency) this.txnCCy.getObject();
					this.txnAmount.setFormat(PennantApplicationUtil.getAmountFormate(aCurrency.getCcyEditField()));
					this.txnAmount.setScale(CurrencyUtil.getFormat(getJVPostingEntry().getAccCCy()) == 0
							? CurrencyUtil.getFormat(getjVPosting().getCurrency())
							: CurrencyUtil.getFormat(getJVPostingEntry().getAccCCy()));
					this.txnAmount.setValue(BigDecimal.ZERO);
					if (this.txnAmount.getActualValue().compareTo(BigDecimal.ZERO) != 0) {
						this.txnAmount.setValue(PennantApplicationUtil.formateAmount(PennantApplicationUtil
								.unFormateAmount(this.txnAmount.getActualValue(), aCurrency.getCcyEditField()),
								aCurrency.getCcyEditField()));
					}
				} else {
					doResetValues();
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * public void onSelect$txnType(Event event) throws InterruptedException { logger.debug("Entering" +
	 * event.toString()); String selectedValue =this.txnType.getSelectedItem().getValue(); Filter[] filter = new
	 * Filter[1]; if(!selectedValue.equals(PennantConstants.List_Select)) { this.txnCode.setValue(""); filter[0] = new
	 * Filter("TranType", selectedValue, Filter.OP_EQUAL); this.txnCode.setFilters(filter); } logger.debug("Leaving" +
	 * event.toString()); }
	 */

	private void doResetValues() {
		logger.debug("Entering");
		this.exchange_Converted_txnAmount.setValue(BigDecimal.ZERO);
		this.exchangeRate.setValue(BigDecimal.ZERO);
		logger.debug("Leaving");
	}

	public void onChange$txnCode(Event event) {
	}

	public void onClick$btnSearchaccountName(Event event) {
		Object dataObject = ExtendedSearchListBox.show(this.window_JVPostingEntryDialog, "Accounts");
		if (dataObject instanceof String) {
			this.account.setValue(PennantApplicationUtil.formatAccountNumber(dataObject.toString()));
		} else {
			Accounts details = (Accounts) dataObject;
			if (details != null) {
				this.account.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
			}
		}
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aJVPostingEntry
	 */
	public void doShowDialog(JVPostingEntry aJVPostingEntry) {
		logger.debug("Entering");

		if (aJVPostingEntry.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aJVPostingEntry.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				if (PennantConstants.RCD_STATUS_SUBMITTED.equals(this.jVPosting.getRecordStatus())) {
					doReadOnly(true);
				} else {
					doReadOnly(false);
				}
				btnCancel.setVisible(false);
			}
		}
		if (enqModule) {
			this.btnSave.setVisible(false);
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnNotes.setVisible(false);
			doReadOnly(true);
		}
		this.batchReference.setReadonly(true);
		try {
			// fill the components with the data
			doWriteBeanToComponents(aJVPostingEntry);
			// set ReadOnly mode accordingly if the object is new or not.

			/*
			 * displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aJVPostingEntry.isNewRecord()));
			 */
			if (isNewRecord()) {
				this.btnCtrl.setInitNew();
				doEdit();
				// setFocus
				this.account.focus();
			} else {
				this.account.focus();
				doEdit();
			}

			doSetWindowTitle();
			// setDialog(DialogType.EMBEDDED);
			this.window_JVPostingEntryDialog.setHeight("80%");
			this.window_JVPostingEntryDialog.setWidth("90%");
			this.groupboxWf.setVisible(false);
			this.window_JVPostingEntryDialog.doModal();

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_JVPostingEntryDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doSetWindowTitle() {
		logger.debug("Entering");
		if (this.enqModule) {
			this.window_JVPostingEntryDialog_Title.setValue(Labels.getLabel("window_JVPostingEntryEnqDialog.title"));
		} else if (this.rePostingModule) {
			this.window_JVPostingEntryDialog_Title
					.setValue(Labels.getLabel("window_JVPostingEntryRePostingDialog.title"));
		} else {
			this.window_JVPostingEntryDialog_Title.setValue(Labels.getLabel("window_JVPostingEntryDialog.title"));
		}

		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.account, this.txnCCy));

		if (getJVPostingEntry().isNewRecord()) {
			setComponentAccessType("JVPostingEntryDialog_BatchReference", false, this.batchReference,
					this.space_BatchReference, this.label_BatchReference, this.hlayout_BatchReference, null);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getJVPostingEntry().isNewRecord()) {
			this.btnCancel.setVisible(false);
		}

		readOnlyComponent(true, this.batch);
		readOnlyComponent(true, this.batchReference);
		readOnlyComponent(true, this.baseCCy);
		readOnlyComponent(true, this.postingBranch);
		readOnlyComponent(true, this.totCreditsByBatchCcy);
		readOnlyComponent(true, this.totDebitsByBatchCcy);
		readOnlyComponent(true, this.debitCount);
		readOnlyComponent(true, this.creditsCount);
		readOnlyComponent(true, this.batchPurpose);
		readOnlyComponent(true, this.postingDate);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.jVPostingEntry.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (!enqModule) {
				this.btnCtrl.setBtnStatus_Edit();
			}
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");
		boolean tempReadOnly = readOnly;

		if (readOnly) {
			tempReadOnly = true;
		} else if (PennantConstants.RECORD_TYPE_DEL.equals(this.jVPosting.getRecordType()) || enqModule) {
			tempReadOnly = true;
		}
		if (rePostingModule) {
			tempReadOnly = false;
		}
		// Batch Header Details Default Read only for All Module Type
		// Batch Header Details
		readOnlyComponent(true, this.batch);
		readOnlyComponent(true, this.batchReference);
		readOnlyComponent(true, this.baseCCy);
		readOnlyComponent(true, this.postingBranch);
		readOnlyComponent(true, this.totCreditsByBatchCcy);
		readOnlyComponent(true, this.totDebitsByBatchCcy);
		readOnlyComponent(true, this.debitCount);
		readOnlyComponent(true, this.creditsCount);
		readOnlyComponent(true, this.batchPurpose);

		// Batch Account, Amount Detials
		readOnlyComponent(true, this.postingDate);
		setComponentAccessType("JVPostingEntryDialog_Account", tempReadOnly, this.account, null, this.label_Account,
				this.hlayout_Account, null);

		setComponentAccessType("JVPostingEntryDialog_AccountName", true, this.accountName, null, this.label_AccountName,
				this.hlayout_AccountName, null);

		setComponentAccessType("JVPostingEntryDialog_ExchangeRate", true, this.exchangeRate, null,
				this.label_ExchangeRate, this.hlayout_ExchangeRate, null);

		setExtAccess("JVPostingEntryDialog_TxnCCY", tempReadOnly, this.txnCCy, row6);

		setComponentAccessType("JVPostingEntryDialog_TxnCode", tempReadOnly, this.txnCode, this.space_TxnCode,
				this.label_TxnCode, this.hlayout_TxnCode, null);

		setComponentAccessType("JVPostingEntryDialog_TxnCode", tempReadOnly, this.debitTxnCode, this.space_TxnCode,
				this.label_TxnCode, this.hlayout_TxnCode, null);
		setComponentAccessType("JVPostingEntryDialog_TxnAmount", tempReadOnly, this.txnAmount, this.space_TxnCode,
				this.label_TxnCode, this.hlayout_TxnCode, null);

		setComponentAccessType("JVPostingEntryDialog_TxnAmount_Ac", tempReadOnly, this.exchange_Converted_txnAmount,
				null, this.label_TxnAmount, this.hlayout_TxnAmount, null);

		/*
		 * setComponentAccessType("JVPostingEntryDialog_TxnType", tempReadOnly, this.txnType, this.space_TxnType,
		 * this.label_TxnType, this.hlayout_TxnType, null);
		 */

		setComponentAccessType("JVPostingEntryDialog_TxnReference", true, this.txnReference, null,
				this.label_TxnReference, this.hlayout_TxnReference, null);

		/*
		 * setComponentAccessType("JVPostingEntryDialog_PostingDate", true, this.postingDate, this.space_PostingDate,
		 * this.label_PostingDate, this.hlayout_PostingDate, null);
		 */

		setComponentAccessType("JVPostingEntryDialog_ValueDate", true, this.valueDate, this.space_ValueDate,
				this.label_ValueDate, this.hlayout_ValueDate, null);

		setComponentAccessType("JVPostingEntryDialog_NarrLine1", tempReadOnly, this.narrLine1, null,
				this.label_NarrLine1, this.hlayout_NarrLine1, null);

		setComponentAccessType("JVPostingEntryDialog_NarrLine2", tempReadOnly, this.narrLine2, null,
				this.label_NarrLine2, this.hlayout_NarrLine2, null);

		setComponentAccessType("JVPostingEntryDialog_NarrLine3", tempReadOnly, this.narrLine3, null,
				this.label_NarrLine3, this.hlayout_NarrLine3, null);

		setComponentAccessType("JVPostingEntryDialog_NarrLine4", tempReadOnly, this.narrLine4, null,
				this.label_NarrLine4, this.hlayout_NarrLine4, null);

		setComponentAccessType("JVPostingEntryDialog_Account", tempReadOnly, this.debitAccount, null,
				this.label_DebitAccount, this.hlayout_DebitAccount, null);

		setComponentAccessType("JVPostingEntryDialog_TdsAdjustmentRequired", tempReadOnly, this.tdsAdjustmentReq, null,
				this.label_TdsAdjustmentRequired, this.hlayout_TdsAdjustmentRequired, null);

		logger.debug("Leaving");
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		// Assigning Rights based on Module Type
		if (!enqModule || rePostingModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_JVPostingEntryDialog_btnSave"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.exchangeRate.setMaxlength(13);
		this.exchangeRate.setFormat(PennantConstants.rateFormate9);
		this.exchangeRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.exchangeRate.setScale(9);

		this.batch.setMaxlength(50);
		this.baseCCy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.baseCCy.setMandatoryStyle(true);
		this.baseCCy.setTextBoxWidth(40);
		this.baseCCy.setModuleName("Currency");
		this.baseCCy.setValueColumn("CcyCode");
		this.baseCCy.setDescColumn("CcyDesc");
		this.baseCCy.setValidateColumns(new String[] { "CcyCode" });
		this.postingBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.postingBranch.setMandatoryStyle(true);
		this.postingBranch.setTextBoxWidth(40);
		this.postingBranch.setModuleName("Branch");
		this.postingBranch.setValueColumn("BranchCode");
		this.postingBranch.setDescColumn("BranchDesc");
		this.postingBranch.setValidateColumns(new String[] { "BranchCode" });
		this.txnCCy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.txnCCy.setMandatoryStyle(true);
		this.txnCCy.setTextBoxWidth(40);
		this.txnCCy.setModuleName("Currency");
		this.txnCCy.setValueColumn("CcyCode");
		this.txnCCy.setDescColumn("CcyDesc");
		this.txnCCy.setValidateColumns(new String[] { "CcyCode" });

		this.account.setModuleName("AccountMapping");
		this.account.setValueColumn("Account");
		this.account.setDescColumn("HostAccount");
		this.account.setValidateColumns(new String[] { "Account" });

		List<String> crEntries = new ArrayList<>();
		crEntries.add(TransactionType.CREDIT.code());
		crEntries.add(TransactionType.BOTH.code());

		Filter[] accFilter = new Filter[2];
		accFilter[0] = new Filter("allowedManualEntry", crEntries, Filter.OP_IN);
		accFilter[1] = new Filter("status", HostAccountStatus.CLOSE.code(), Filter.OP_NOT_EQUAL);

		this.account.setFilters(accFilter);

		this.debitAccount.setModuleName("AccountMapping");
		this.debitAccount.setValueColumn("Account");
		this.debitAccount.setDescColumn("HostAccount");
		this.debitAccount.setValidateColumns(new String[] { "Account" });

		List<String> drEntries = new ArrayList<>();
		drEntries.add(TransactionType.DEBIT.code());
		drEntries.add(TransactionType.BOTH.code());

		accFilter = new Filter[2];
		accFilter[0] = new Filter("allowedManualEntry", drEntries, Filter.OP_IN);
		accFilter[1] = new Filter("status", HostAccountStatus.CLOSE.code(), Filter.OP_NOT_EQUAL);

		this.debitAccount.setFilters(accFilter);

		this.txnCode.setMaxlength(3);
		this.txnCode.setMandatoryStyle(true);
		this.txnCode.setTextBoxWidth(40);
		this.txnCode.setModuleName("TransactionCode");
		this.txnCode.setValueColumn("TranCode");
		this.txnCode.setDescColumn("TranDesc");
		this.txnCode.setValidateColumns(new String[] { "TranCode" });

		Filter filter[] = new Filter[1];
		filter[0] = new Filter("TranType", AccountConstants.TRANTYPE_CREDIT, Filter.OP_EQUAL);
		this.txnCode.setFilters(filter);

		this.postingDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.txnReference.setMaxlength(50);

		int txnFormatter = CurrencyUtil.getFormat(getJVPostingEntry().getTxnCCy());
		int currFormatter = CurrencyUtil.getFormat(getjVPosting().getCurrency());

		this.txnAmount.setMandatory(true);
		this.txnAmount.setScale(txnFormatter == 0 ? currFormatter : txnFormatter);
		this.txnAmount
				.setFormat(PennantApplicationUtil.getAmountFormate(txnFormatter == 0 ? currFormatter : txnFormatter));

		this.totDebitsByBatchCcy.setFormat(PennantApplicationUtil.getAmountFormate(currFormatter));

		this.totCreditsByBatchCcy.setFormat(PennantApplicationUtil.getAmountFormate(currFormatter));

		this.exchange_Converted_txnAmount.setFormat(
				PennantApplicationUtil.getAmountFormate(CurrencyUtil.getFormat(getJVPostingEntry().getAccCCy())));

		this.narrLine1.setMaxlength(100);
		this.narrLine2.setMaxlength(100);
		this.narrLine3.setMaxlength(100);
		this.narrLine4.setMaxlength(100);

		this.account.setMandatoryStyle(true);

		this.debitTxnCode.setMaxlength(3);
		this.debitTxnCode.setMandatoryStyle(true);
		this.debitTxnCode.setTextBoxWidth(40);
		this.debitTxnCode.setModuleName("TransactionCode");
		this.debitTxnCode.setValueColumn("TranCode");
		this.debitTxnCode.setDescColumn("TranDesc");
		this.debitTxnCode.setValidateColumns(new String[] { "TranCode" });

		Filter filter1[] = new Filter[1];
		filter1[0] = new Filter("TranType", AccountConstants.TRANTYPE_DEBIT, Filter.OP_EQUAL);
		this.debitTxnCode.setFilters(filter1);

		this.debitAccount.setMandatoryStyle(true);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);

		if (ImplementationConstants.ALLOW_TDS_CERTIFICATE_ADJUSTMENT) {
			row13.setVisible(true);
		} else
			row13.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aJVPostingEntry JVPostingEntry
	 * @throws InterruptedException
	 * @throws UPPException
	 */
	public void doWriteBeanToComponents(JVPostingEntry aJVPostingEntry) {
		logger.debug("Entering");
		doFillBatchDetails();

		String hostAccount = AccountingConfigCache.getAccountMapping(aJVPostingEntry.getAccount());
		this.account.setValue(PennantApplicationUtil.formatAccountNumber(aJVPostingEntry.getAccount()));
		this.account.setDescription(hostAccount);

		String debitHostAccount = AccountingConfigCache.getAccountMapping(aJVPostingEntry.getDebitAccount());
		this.debitAccount.setValue(PennantApplicationUtil.formatAccountNumber(aJVPostingEntry.getDebitAccount()));
		this.debitAccount.setDescription(debitHostAccount);

		this.accountName.setValue(aJVPostingEntry.getAccountName());
		this.label_AccountCurrency.setValue(aJVPostingEntry.getAccCCy());
		/*
		 * fillComboBox(this.txnType, aJVPostingEntry.getTxnEntry(), listTxnType, "");
		 */

		if (aJVPostingEntry.isNewRecord()) {
			this.postingDate.setValue(SysParamUtil.getAppDate());
			this.valueDate.setValue(SysParamUtil.getAppDate());
		} else {
			this.postingDate.setValue(aJVPostingEntry.getPostingDate());
			this.valueDate.setValue(aJVPostingEntry.getValueDate());
		}
		if (aJVPostingEntry.getValueDate() != null) {
			this.valueDate.setValue(aJVPostingEntry.getValueDate());
		}
		this.txnReference.setValue((int) aJVPostingEntry.getTxnReference());
		this.narrLine1.setValue(aJVPostingEntry.getNarrLine1());
		this.narrLine2.setValue(aJVPostingEntry.getNarrLine2());
		this.narrLine3.setValue(aJVPostingEntry.getNarrLine3());
		this.narrLine4.setValue(aJVPostingEntry.getNarrLine4());

		CurrencyUtil.getFormat(getJVPostingEntry().getTxnCCy());

		this.txnAmount.setValue(PennantApplicationUtil.formateAmount(getJVPostingEntry().getTxnAmount(),
				CurrencyUtil.getFormat(getJVPostingEntry().getTxnCCy())));

		this.txnCode.setValue(aJVPostingEntry.getTxnCode());
		this.debitTxnCode.setValue(aJVPostingEntry.getDebitTxnCode());

		if (isNewRecord()) {
			this.txnCode.setDescription("");
			this.debitTxnCode.setDescription("");
		} else {
			this.txnCCy.setValue(aJVPostingEntry.getTxnCCy());
			/*
			 * this.txnType.setDisabled(true); this.txnType.setAutodrop(false);
			 */
			this.exchangeRate.setValue(aJVPostingEntry.getExchRate_Ac());
			this.txnCode.setDescription(aJVPostingEntry.getTxnDesc());
			this.debitTxnCode.setDescription(aJVPostingEntry.getDebitTxnDesc());
			this.label_ExTxnAmount_Ac.setValue(Labels.getLabel("label_JVPostingEntryDialog_ExTxnAmount.value",
					new String[] { aJVPostingEntry.getTxnCCy() }));
		}

		// calcJVPostings();
		this.recordStatus.setValue(aJVPostingEntry.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aJVPostingEntry.getRecordType()));
		this.tdsAdjustmentReq.setChecked(aJVPostingEntry.isTDSAdjReq());

		logger.debug("Leaving");
	}

	private void doFillBatchDetails() {
		logger.debug("Entering");

		this.batch.setValue(getjVPosting().getBatch());
		if (getjVPosting().isNewRecord()) {
			this.batchReference.setValue("");
		} else {
			this.batchReference.setValue(String.valueOf(getjVPosting().getBatchReference()));
		}
		this.baseCCy.setValue(getjVPosting().getCurrency());
		this.postingBranch.setValue(getjVPosting().getBranch());
		this.postingBranch.setDescription(getjVPosting().getBranchDesc());
		this.txnCCy.setValue(getjVPosting().getCurrency());
		int currFormatter = CurrencyUtil.getFormat(getjVPosting().getCurrency());
		this.totDebitsByBatchCcy
				.setValue(PennantApplicationUtil.formateAmount(getjVPosting().getTotDebitsByBatchCcy(), currFormatter));
		this.totCreditsByBatchCcy.setValue(
				PennantApplicationUtil.formateAmount(getjVPosting().getTotCreditsByBatchCcy(), currFormatter));
		this.debitCount.setValue(getjVPosting().getDebitCount());
		this.creditsCount.setValue(getjVPosting().getCreditsCount());
		this.batchPurpose.setValue(getjVPosting().getBatchPurpose());

		logger.debug("Entering");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJVPostingEntry
	 */
	public void doWriteComponentsToBean(JVPostingEntry aJVPostingEntry) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Batch Reference
		try {
			aJVPostingEntry.setBatchReference(getJVPostingDialogCtrl().getJVPosting().getBatchReference());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Name
		try {
			aJVPostingEntry.setAccount(PennantApplicationUtil.unFormatAccountNumber(this.account.getValue()));
			aJVPostingEntry.setAccCCy(this.txnCCy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Name
		try {
			aJVPostingEntry.setDebitAccount(PennantApplicationUtil.unFormatAccountNumber(this.debitAccount.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Txn C Cy
		try {
			aJVPostingEntry.setTxnCCy(this.txnCCy.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Txn C Cy
		try {
			aJVPostingEntry.setTxnDesc(this.txnCode.getDescription());
			aJVPostingEntry.setTxnCode(this.txnCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Debit Txn code
		try {
			aJVPostingEntry.setDebitTxnDesc(this.debitTxnCode.getDescription());
			aJVPostingEntry.setDebitTxnCode(this.debitTxnCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*
		 * // Txn Code try { String strTxnType = null; if (this.txnType.getSelectedItem() != null) { strTxnType =
		 * this.txnType.getSelectedItem().getValue() .toString(); } if (strTxnType != null &&
		 * !PennantConstants.List_Select.equals(strTxnType)) { aJVPostingEntry.setTxnEntry(strTxnType); } else {
		 * aJVPostingEntry.setTxnEntry(null); } } catch (WrongValueException we) { wve.add(we); }
		 */
		// Posting Date
		try {
			if (this.postingDate.getValue() != null) {
				aJVPostingEntry.setPostingDate(new Timestamp(this.postingDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Value Date
		try {
			if (this.valueDate.getValue() != null) {
				aJVPostingEntry.setValueDate(new Timestamp(this.valueDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Txn Amount
		try {
			aJVPostingEntry.setTxnAmount(CurrencyUtil.unFormat(this.txnAmount.getActualValue(),
					CurrencyUtil.getFormat(getJVPostingEntry().getTxnCCy())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// exchange_Converted_txnAmount
		try {
			aJVPostingEntry.setTxnAmount_Ac(CurrencyUtil.unFormat(this.exchange_Converted_txnAmount.getValue(),
					CurrencyUtil.getFormat(getJVPostingEntry().getAccCCy())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// exchangeRate
		try {
			aJVPostingEntry.setExchRate_Ac(this.exchangeRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Txn Reference
		try {
			aJVPostingEntry.setTxnReference(this.txnReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Narr Line1
		try {
			aJVPostingEntry.setNarrLine1(this.narrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Narr Line2
		try {
			aJVPostingEntry.setNarrLine2(this.narrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Narr Line3
		try {
			aJVPostingEntry.setNarrLine3(this.narrLine3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Narr Line4
		try {
			aJVPostingEntry.setNarrLine4(this.narrLine4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Modified Flag
		try {
			if (!aJVPostingEntry.isNewRecord() && isDataChanged()) {
				aJVPostingEntry.setModifiedFlag(PennantConstants.YES);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// TDSAdjustmentRequired
		try {
			aJVPostingEntry.setTDSAdjReq(this.tdsAdjustmentReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (isDataChanged()) {
			setOld_JVPostingEntry(getJVPostingEntry());
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Account
		if (!this.account.isReadonly()) {
			this.account.setConstraint(
					new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_Account.value"), null, true));
		}
		// Debit Account
		if (!this.debitAccount.isReadonly()) {
			this.debitAccount.setConstraint(new PTStringValidator(
					Labels.getLabel("label_JVPostingEntryDialog_DebitAccount.value"), null, true));
		}
		// Txn Code
		/*
		 * if (!this.txnType.isDisabled()) { this.txnType .setConstraint(new StaticListValidator( listTxnType,
		 * Labels.getLabel("label_JVPostingEntryDialog_TxnType.value"))); }
		 */
		// Posting Date
		if (!this.postingDate.isReadonly()) {
			this.postingDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_JVPostingEntryDialog_PostingDate.value"), true));
		}
		// Value Date
		if (!this.valueDate.isReadonly()) {
			this.valueDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_JVPostingEntryDialog_ValueDate.value"), true));
		}
		// TxnCode
		if (!this.txnCode.isReadonly()) {
			this.txnCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true, true));
		}

		// TxnCode
		if (!this.debitTxnCode.isReadonly()) {
			this.debitTxnCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_DebitTxnCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true, true));
		}
		// Txn Amount
		if (!this.txnAmount.isReadonly()) {
			if (this.label_AccountCurrency.getValue().equals(this.txnCCy.getValidatedValue())) {
				this.txnAmount.setConstraint(
						new PTDecimalValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnAmount.value"),
								CurrencyUtil.getFormat(getJVPostingEntry().getAccCCy()), true, false, 0));
			} else {
				this.txnAmount.setConstraint(
						new PTDecimalValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnAmount.value"),
								CurrencyUtil.getFormat(getJVPostingEntry().getTxnCCy()) == 0
										? CurrencyUtil.getFormat(getjVPosting().getCurrency())
										: CurrencyUtil.getFormat(getJVPostingEntry().getTxnCCy()),
								true, false, 0));
			}
		}
		// Txn Reference
		if (!this.txnReference.isReadonly()) {
			this.txnReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnReference.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.batchReference.setConstraint("");
		this.account.setConstraint("");
		// this.txnType.setConstraint("");
		this.postingDate.setConstraint("");
		this.valueDate.setConstraint("");
		this.txnAmount.setConstraint("");
		this.txnReference.setConstraint("");
		this.narrLine1.setConstraint("");
		this.narrLine2.setConstraint("");
		this.narrLine3.setConstraint("");
		this.narrLine4.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		// Txn C Cy
		if (txnCCy.isButtonVisible()) {
			this.txnCCy.setConstraint(new PTStringValidator(Labels.getLabel("label_JVPostingEntryDialog_TxnCCy.value"),
					null, true, true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.txnCCy.setConstraint("");
		this.txnCode.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.baseCCy.setErrorMessage("");
		this.postingBranch.setErrorMessage("");
		this.totDebitsByBatchCcy.setErrorMessage("");
		this.totCreditsByBatchCcy.setErrorMessage("");
		this.debitCount.setErrorMessage("");
		this.creditsCount.setErrorMessage("");
		this.batchPurpose.setErrorMessage("");
		this.account.setErrorMessage("");
		this.accountName.setErrorMessage("");
		this.txnCCy.setErrorMessage("");
		// this.txnType.setErrorMessage("");
		this.txnAmount.setErrorMessage("");
		this.txnCode.setErrorMessage("");
		this.txnReference.setErrorMessage("");
		this.exchangeRate.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.postingDate.setErrorMessage("");
		this.narrLine1.setErrorMessage("");
		this.narrLine2.setErrorMessage("");
		this.narrLine3.setErrorMessage("");
		this.narrLine4.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected boolean doCustomDelete(final JVPostingEntry aJVPostingEntry, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		aJVPostingEntry.setDeletedFlag(true);
		AuditHeader auditHeader = newEntryProcess(aJVPostingEntry, tranType, false);
		auditHeader = ErrorControl.showErrorDetails(this.window_JVPostingEntryDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			getJVPostingDialogCtrl().doFillJVPostingEntryDetails(this.jvPostingEntryList);
			getJVPostingDialogCtrl().setProceed(false);
			return true;
		}

		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final JVPostingEntry aJVPostingEntry = new JVPostingEntry();
		BeanUtils.copyProperties(getJVPostingEntry(), aJVPostingEntry);

		doDelete("", aJVPostingEntry);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.batch.setValue("");
		this.batchReference.setValue("");
		this.baseCCy.setValue("");
		this.baseCCy.setDescription("");
		this.postingBranch.setValue("");
		this.postingBranch.setDescription("");
		this.totDebitsByBatchCcy.setValue("");
		this.totCreditsByBatchCcy.setValue("");
		this.batchPurpose.setValue("");
		this.account.setValue("");
		this.accountName.setValue("");
		this.txnCCy.setValue("");
		// this.txnType.setSelectedIndex(0);
		this.txnAmount.setValue("");
		this.txnCode.setValue("");
		this.txnReference.setValue(0);
		this.exchangeRate.setValue("");
		this.narrLine1.setValue("");
		this.narrLine2.setValue("");
		this.narrLine3.setValue("");
		this.narrLine4.setValue("");
		this.tdsAdjustmentReq.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final JVPostingEntry aJVPostingEntry = new JVPostingEntry();
		BeanUtils.copyProperties(getJVPostingEntry(), aJVPostingEntry);
		aJVPostingEntry.setRePostingModule(this.rePostingModule);
		boolean isNew = false;

		doSetValidation();
		// fill the JVPostingEntry object with the components data
		doWriteComponentsToBean(aJVPostingEntry);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aJVPostingEntry.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aJVPostingEntry.getRecordType())) {
				aJVPostingEntry.setVersion(aJVPostingEntry.getVersion() + 1);
				if (isNew) {
					aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					if (this.rePostingModule) {
						aJVPostingEntry.setNewRecord(true);
					}
				}
			}
		} else {
			if (isNewRecord()) {
				aJVPostingEntry.setVersion(1);
				aJVPostingEntry.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aJVPostingEntry.getRecordType())) {
				aJVPostingEntry.setVersion(aJVPostingEntry.getVersion() + 1);
				aJVPostingEntry.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aJVPostingEntry.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			AuditHeader auditHeader = newEntryProcess(aJVPostingEntry, tranType, true);
			auditHeader = ErrorControl.showErrorDetails(this.window_JVPostingEntryDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				for (JVPostingEntry entry : jvPostingEntryList) {
					if (StringUtils.equals(entry.getTxnEntry(), AccountConstants.TRANTYPE_DEBIT)) {
						entry.setAccount(entry.getDebitAccount());
						entry.setAcType(entry.getDebitAcType());
						entry.setAccountName(entry.getDebitAcname());
					} else {
						entry.setAccount(entry.getAccount());
						entry.setAcType(entry.getAcType());
						entry.setAccountName(entry.getAccountName());
					}

				}

				getJVPostingDialogCtrl().doFillJVPostingEntryDetails(this.jvPostingEntryList);
				// send the data back to main screen
				getJVPostingDialogCtrl().setProceed(false);
				// Updating batch Details
				// doUpdateBatchDetails(aJVPostingEntry, true);
				// getJVPostingDialogCtrl().doUpdateBatchDetails(getjVPosting());
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newEntryProcess(JVPostingEntry aJVPostingEntry, String tranType, boolean saveOperation) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aJVPostingEntry, tranType);
		jvPostingEntryList = new ArrayList<JVPostingEntry>();

		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = String.valueOf(aJVPostingEntry.getAccount());
		valueParm[1] = PennantApplicationUtil.amountFormate(aJVPostingEntry.getTxnAmount(),
				CurrencyUtil.getFormat(getJVPostingEntry().getTxnCCy()));
		valueParm[2] = PennantApplicationUtil.getLabelDesc(aJVPostingEntry.getTxnEntry(), listTxnType);

		errParm[0] = PennantJavaUtil.getLabel("label_Account") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_TxnAmount") + ":" + valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_TxnType") + ":" + valueParm[2];
		List<JVPostingEntry> list = getJVPostingDialogCtrl().getJVPostingEntryList();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < getJVPostingDialogCtrl().getJVPostingEntryList().size(); i++) {
				JVPostingEntry jvPostingEntry = getJVPostingDialogCtrl().getJVPostingEntryList().get(i);

				if (jvPostingEntry.getDerivedTxnRef() != 0) {
					continue;
				}

				if ((jvPostingEntry.isExternalAccount())
						&& (jvPostingEntry.getTxnReference() == aJVPostingEntry.getTxnReference())) { // Both Current
																										// and Existing
																										// list entry is
																										// same

					/*
					 * if(isNewRecord()){ auditHeader.setErrorDetails(ErrorUtil.getErrorDetail( new
					 * ErrorDetails(PennantConstants.KEY_FIELD,"41008",errParm,valueParm),
					 * getUserWorkspace().getUserLanguage())); return auditHeader; }
					 */

					if (tranType == PennantConstants.TRAN_DEL) {
						if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							jvPostingEntryList.add(aJVPostingEntry);
							JVPostingEntry otherentry = doCheckAndPrepareOtherLeg(aJVPostingEntry, list);
							if (otherentry != null) {
								jvPostingEntryList.add(otherentry);
							}

						} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aJVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							jvPostingEntryList.add(aJVPostingEntry);
							JVPostingEntry otherentry = doCheckAndPrepareOtherLeg(aJVPostingEntry, list);
							if (otherentry != null) {
								jvPostingEntryList.add(otherentry);
							}
						} else if (aJVPostingEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getJVPostingDialogCtrl().getJVPosting().getJVPostingEntrysList()
									.size(); j++) {
								JVPostingEntry entry = getJVPostingDialogCtrl().getJVPosting().getJVPostingEntrysList()
										.get(j);
								if (entry.getBatchReference() == aJVPostingEntry.getBatchReference()
										&& entry.getAccount().equals(aJVPostingEntry.getAccount())) {
									jvPostingEntryList.add(entry);
									JVPostingEntry otherentry = doCheckAndPrepareOtherLeg(aJVPostingEntry, list);
									if (otherentry != null) {
										jvPostingEntryList.add(otherentry);
									}
								}
							}
						}
					} else {
						if (tranType.equals(PennantConstants.TRAN_UPD)) {
							recordAdded = true;
							jvPostingEntryList.add(aJVPostingEntry);
							JVPostingEntry otherentry = doCheckAndPrepareOtherLeg(aJVPostingEntry, list);
							if (otherentry != null) {
								jvPostingEntryList.add(otherentry);
							}

						}
					}
				} else {
					jvPostingEntryList.add(jvPostingEntry);
					JVPostingEntry otherentry = doCheckAndPrepareOtherLeg(jvPostingEntry, list);
					if (StringUtils.equals(otherentry.getTxnEntry(), AccountConstants.TRANTYPE_DEBIT)) {
						otherentry.setDebitAccount(otherentry.getAccount());
					} else {
						otherentry.setAccount(otherentry.getAccount());
					}
					if (otherentry != null) {
						jvPostingEntryList.add(otherentry);
					}
				}
			}
		}

		if (!recordAdded) {
			aJVPostingEntry.setTxnEntry(AccountConstants.TRANTYPE_CREDIT);
			aJVPostingEntry.setTDSAdjReq(this.tdsAdjustmentReq.isChecked());
			jvPostingEntryList.add(aJVPostingEntry);

			JVPostingEntry debitEntry = new JVPostingEntry();
			BeanUtils.copyProperties(aJVPostingEntry, debitEntry);
			debitEntry.setTxnReference(aJVPostingEntry.getTxnReference() + 1);
			debitEntry.setAccount(PennantApplicationUtil.unFormatAccountNumber(this.debitAccount.getValue()));
			debitEntry.setTxnEntry(AccountConstants.TRANTYPE_DEBIT);
			debitEntry.setTDSAdjReq(this.tdsAdjustmentReq.isChecked());
			debitEntry.setTxnCode(this.debitTxnCode.getValidatedValue());
			debitEntry.setTxnDesc(this.debitTxnCode.getDescription());
			debitEntry.setDerivedTxnRef(aJVPostingEntry.getTxnReference());
			jvPostingEntryList.add(debitEntry);

		}

		if (!saveOperation && !aJVPostingEntry.isNewRecord()) {
			for (int i = 0; i < jvPostingEntryList.size(); i++) {
				JVPostingEntry jvPostingEntry = jvPostingEntryList.get(i);
				if (jvPostingEntry.getTxnReference() == aJVPostingEntry.getTxnReference()) {
					jvPostingEntry.setDeletedFlag(true);
					jvPostingEntry.setRecordType(aJVPostingEntry.getRecordType());
				}
			}
		}
		return auditHeader;
	}

	public JVPostingEntry getEntryList(long derRef, List<JVPostingEntry> list) {

		for (JVPostingEntry jvPostingEntry : list) {
			if (derRef == jvPostingEntry.getDerivedTxnRef()) {
				return jvPostingEntry;
			}
		}
		return null;
	}

	public JVPostingEntry doCheckAndPrepareOtherLeg(JVPostingEntry aJVPostingEntry, List<JVPostingEntry> list) {
		JVPostingEntry otherentry = getEntryList(aJVPostingEntry.getTxnReference(), list);
		if (otherentry != null) {
			otherentry.setTxnReference(aJVPostingEntry.getTxnReference() + 1);
			otherentry.setTxnEntry(AccountConstants.TRANTYPE_DEBIT);
			otherentry.setTxnCode(this.debitTxnCode.getValidatedValue());
			otherentry.setDerivedTxnRef(aJVPostingEntry.getTxnReference());
		}
		return otherentry;
	}

	public void onFulfill$account(Event event) {
		logger.debug("Entering");

		if (StringUtils.isBlank(this.account.getValue())) {
			this.account.setValue("", "");
		} else {
			AccountMapping accountMapping = (AccountMapping) this.account.getObject();

			this.account.setValue(accountMapping.getAccount(), accountMapping.getHostAccount());
			getJVPostingEntry().setAcType(accountMapping.getAccountType());
			getJVPostingEntry().setAccountName(accountMapping.getAccountTypeDesc());
			this.accountName.setValue(accountMapping.getAccountTypeDesc());
		}
		logger.debug("Leaving");

	}

	public void onFulfill$debitAccount(Event event) {
		logger.debug("Entering");

		if (StringUtils.isBlank(this.debitAccount.getValue())) {
			this.debitAccount.setValue("", "");
		} else {
			AccountMapping accountMapping = (AccountMapping) this.debitAccount.getObject();
			this.debitAccount.setValue(accountMapping.getAccount(), accountMapping.getHostAccount());
			getJVPostingEntry().setDebitAcType(accountMapping.getAccountType());
			getJVPostingEntry().setDebitAcname(accountMapping.getAccountTypeDesc());
		}
		logger.debug("Leaving");

	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(JVPostingEntry aJVPostingEntry, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJVPostingEntry.getBefImage(), aJVPostingEntry);
		return new AuditHeader(null/* aJVPostingEntry.getBatchReference() */, null, null, null, auditDetail,
				aJVPostingEntry.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public JVPostingEntry getJVPostingEntry() {
		return this.jVPostingEntry;
	}

	public void setJVPostingEntry(JVPostingEntry jVPostingEntry) {
		this.jVPostingEntry = jVPostingEntry;
	}

	public void setJVPostingDialogCtrl(JVPostingDialogCtrl jVPostingDialogCtrl) {
		this.jVPostingDialogCtrl = jVPostingDialogCtrl;
	}

	public JVPostingDialogCtrl getJVPostingDialogCtrl() {
		return jVPostingDialogCtrl;
	}

	public JVPostingService getjVPostingService() {
		return jVPostingService;
	}

	public void setjVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

	public JVPosting getjVPosting() {
		return jVPosting;
	}

	public void setjVPosting(JVPosting jVPosting) {
		this.jVPosting = jVPosting;
	}

	public JVPostingEntry getOld_JVPostingEntry() {
		return old_JVPostingEntry;
	}

	public void setOld_JVPostingEntry(JVPostingEntry oldJVPostingEntry) {
		this.old_JVPostingEntry = oldJVPostingEntry;
	}

	public List<JVPostingEntry> getDeletedJVPostingEntryList() {
		return deletedJVPostingEntryList;
	}

	public void setDeletedJVPostingEntryList(List<JVPostingEntry> deletedJVPostingEntryList) {
		this.deletedJVPostingEntryList = deletedJVPostingEntryList;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
}