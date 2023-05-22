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
 * * FileName : AccountingSetDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * *
 * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.accountingset;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.rmtmasters.accountingset.model.TransactionEntryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * This is the controller class for the /WEB-INF/pages/RulesFactory/AccountingSet/accountingSetDialog.zul file.
 */
public class AccountingSetDialogCtrl extends GFCBaseCtrl<AccountingSet> {
	private static final long serialVersionUID = 8602015982512929710L;
	private static final Logger logger = LogManager.getLogger(AccountingSetDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AccountingSetDialog;

	protected ExtendedCombobox eventCode;
	protected Textbox accountSetCode;
	protected Textbox accountSetCodeName;
	protected Checkbox entryByInvestment;
	protected Checkbox systemDefault;
	protected Button btnCopyTo;

	protected Grid grid_Basicdetails;

	protected Hbox hbox_EntryByInvestment;

	protected Listbox listBoxTransactionEntry;
	protected Listheader listheader_Account;
	protected Listheader listheader_PostToSystem;
	protected Listheader listheader_ByInvestor;
	protected Paging pagingTransactionEntryList;

	// not auto wired vars
	private AccountingSet accountingSet; // overhanded per param
	private transient AccountingSetListCtrl accountingSetListCtrl; // overhanded per param

	private transient boolean validationOn;

	protected Button button_TransactionEntryList_NewTransactionEntry;
	protected Button btnSearchAccountSetCode; // autowire
	protected Textbox lovDescEventCodeName;

	// ServiceDAOs / Domain Classes
	private transient AccountingSetService accountingSetService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private List<TransactionEntry> transactionEntryList = new ArrayList<TransactionEntry>();
	private PagedListWrapper<TransactionEntry> TransactionEntryPagedListWrapper;
	int listRows;
	private boolean saveRecord = false;

	protected Listheader listheader_AccType;
	protected Listheader listheader_AmountRule;

	/**
	 * default constructor.<br>
	 */
	public AccountingSetDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AccountingSetDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected AccountingSet object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_AccountingSetDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AccountingSetDialog);

		try {
			setTransactionEntryPagedListWrapper();

			if (arguments.containsKey("accountingSet")) {
				this.accountingSet = (AccountingSet) arguments.get("accountingSet");
				AccountingSet befImage = new AccountingSet();
				BeanUtils.copyProperties(this.accountingSet, befImage);
				this.accountingSet.setBefImage(befImage);
				setAccountingSet(this.accountingSet);
			} else {
				setAccountingSet(null);
			}

			// READ OVERHANDED params !
			// we get the accountingSetListWindow controller. So we have access to it and can synchronize the shown data
			// when we do insert, editor delete accountingSet here.
			if (arguments.containsKey("accountingSetListCtrl")) {
				setAccountingSetListCtrl((AccountingSetListCtrl) arguments.get("accountingSetListCtrl"));
			} else {
				setAccountingSetListCtrl(null);
			}

			doLoadWorkFlow(this.accountingSet.isWorkflow(), this.accountingSet.getWorkflowId(),
					this.accountingSet.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "AccountingSetDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			getBorderLayoutHeight();
			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 170;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listBoxTransactionEntry.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 24) - 1;
			pagingTransactionEntryList.setPageSize(listRows);

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getAccountingSet());
			disableNewTransactionEntry();
			checkListboxcount();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AccountingSetDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.eventCode.setMaxlength(10);
		this.accountSetCode.setMaxlength(10);
		this.accountSetCodeName.setMaxlength(50);
		this.eventCode.setMandatoryStyle(true);
		this.eventCode.setModuleName("AccountEngineEvent");/*
															 * this.eventCode.setValueColumn("AEEventCode");
															 * this.eventCode.setDescColumn("AEEventCodeDesc");
															 */
		this.eventCode.setValidateColumns(new String[] { "AEEventCode" });

		List<String> eventCodes = AccountingEvent.getExcludedAccEvents();

		int i = eventCodes.size();
		Filter[] filter = new Filter[i];
		for (i = 0; i < eventCodes.size(); i++) {
			filter[i] = new Filter("AEEventCode", eventCodes.get(i), Filter.OP_NOT_EQUAL);
		}

		this.eventCode.setFilters(filter);

		this.listheader_Account.setVisible(false);
		this.listheader_PostToSystem.setVisible(false);

		this.hbox_EntryByInvestment.setVisible(ImplementationConstants.ALLOW_RIA);
		this.listheader_ByInvestor.setVisible(ImplementationConstants.ALLOW_RIA);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.button_TransactionEntryList_NewTransactionEntry
				.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnNewTransactionEntry"));
		this.btnCopyTo.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnCopyTo"));

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		saveRecord = true;
		doSave();
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
		MessageUtil.showHelpWindow(event, window_AccountingSetDialog);
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

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.accountingSet.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountingSet (AccountingSet)
	 */
	public void doWriteBeanToComponents(AccountingSet aAccountingSet) {
		logger.debug("Entering");

		this.eventCode.setValue(aAccountingSet.getEventCode());
		this.accountSetCode.setValue(aAccountingSet.getAccountSetCode());
		this.accountSetCodeName.setValue(aAccountingSet.getAccountSetCodeName());
		this.entryByInvestment.setChecked(aAccountingSet.isEntryByInvestment());
		this.systemDefault.setChecked(aAccountingSet.isSystemDefault());

		doFilllistbox(aAccountingSet.getTransactionEntries());
		checkSystemDefault(aAccountingSet.getEventCode(), aAccountingSet.getAccountSetCode());
		if (aAccountingSet.getLovDescEventCodeName() != null) {
			this.lovDescEventCodeName.setValue(aAccountingSet.getLovDescEventCodeName());
		}
		/*
		 * if(this.eventCode.getValue().equals(AccountEventConstants.ACCEVENT_LATEPAY)){
		 * this.button_TransactionEntryList_NewTransactionEntry.setVisible(false); }
		 */
		this.recordStatus.setValue(aAccountingSet.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccountingSet
	 */
	public void doWriteComponentsToBean(AccountingSet aAccountingSet) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAccountingSet.setLovDescEventCodeName(this.lovDescEventCodeName.getValue());
			aAccountingSet.setEventCode(this.eventCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountingSet.setAccountSetCode(this.accountSetCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountingSet.setAccountSetCodeName(this.accountSetCodeName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountingSet.setEntryByInvestment(this.entryByInvestment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aAccountingSet.setSystemDefault(this.systemDefault.isChecked());

		try {
			if (saveRecord) {
				saveRecord = false;
				if (this.listBoxTransactionEntry.getItemCount() <= 0) {
					throw new WrongValueException(this.listBoxTransactionEntry,
							"Please add atleast one transaction entry!!!");
				}
			}
			aAccountingSet.setTransactionEntries(getTransactionEntryList());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aAccountingSet.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aAccountingSet
	 */
	public void doShowDialog(AccountingSet aAccountingSet) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aAccountingSet.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.accountSetCode.focus();
		} else {
			this.accountSetCode.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aAccountingSet);

			if (this.listBoxTransactionEntry.getItemCount() == 0) {
				this.btnCopyTo.setVisible(false);
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AccountingSetDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param transactionEntryList
	 */
	public void doFilllistbox(List<TransactionEntry> transactionEntryList) {
		logger.debug("Entering");

		if (transactionEntryList != null) {

			Collections.sort(transactionEntryList, new Comparator<TransactionEntry>() {
				@Override
				public int compare(TransactionEntry entry1, TransactionEntry entry2) {
					if (entry1.getTransOrder() > entry2.getTransOrder()) {
						return 1;
					} else if (entry1.getTransOrder() < entry2.getTransOrder()) {
						return -1;
					}
					return 0;
				}
			});
			getTransactionEntryList().clear();
			setTransactionEntryList(transactionEntryList);
			this.pagingTransactionEntryList.setDetailed(true);
			getTransactionEntryPagedListWrapper().initList(transactionEntryList, this.listBoxTransactionEntry,
					this.pagingTransactionEntryList);
			this.listBoxTransactionEntry.setItemRenderer(new TransactionEntryListModelItemRenderer());
		}
		checkListboxcount();
		// Set the first page as the active page.
		if (pagingTransactionEntryList != null) {
			this.pagingTransactionEntryList.setActivePage(0);
		}

		logger.debug("Leaving");
	}

	private void disableNewTransactionEntry() {
		logger.debug("Entering");
		if (this.eventCode.getValue() == null || StringUtils.isEmpty(this.eventCode.getValue())) {
			this.button_TransactionEntryList_NewTransactionEntry.setDisabled(true);
		} else {
			this.button_TransactionEntryList_NewTransactionEntry.setDisabled(false);
		}
		logger.debug("Leaving");
	}

	private void checkListboxcount() {
		logger.debug("Entering");

		if (this.listBoxTransactionEntry.getItemCount() > 0) {
			this.entryByInvestment.setDisabled(true);
		} else {
			this.entryByInvestment.setDisabled(isReadOnly("AccountingSetDialog_entryByInvestment"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.accountSetCode.isReadonly()) {
			this.accountSetCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AccountingSetDialog_AccountSetCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.accountSetCodeName.isReadonly()) {
			this.accountSetCodeName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AccountingSetDialog_AccountSetCodeName.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		setValidationOn(false);
		this.accountSetCode.setConstraint("");
		this.accountSetCodeName.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		this.eventCode.setConstraint(
				new PTStringValidator(Labels.getLabel("label_AccountingSetDialog_EventCode.value"), null, true, true));

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints to the LOVFields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescEventCodeName.setConstraint("");
		this.eventCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Clear the Error Messages
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.eventCode.setErrorMessage("");
		this.lovDescEventCodeName.setErrorMessage("");
		this.accountSetCode.setErrorMessage("");
		this.accountSetCodeName.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getAccountingSetListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a AccountingSet object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AccountingSet aAccountingSet = new AccountingSet();
		BeanUtils.copyProperties(getAccountingSet(), aAccountingSet);

		String keyReference = Labels.getLabel("label_AccountingSetDialog_EventCode.value") + ":"
				+ aAccountingSet.getEventCode() + ", "
				+ Labels.getLabel("label_AccountingSetDialog_AccountSetCode.value") + ":"
				+ aAccountingSet.getAccountSetCode();

		doDelete(keyReference, aAccountingSet);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getAccountingSet().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.btnCopyTo.setDisabled(true);
			this.btnCopyTo.setVisible(false);
			this.eventCode.setReadonly(false);
			this.accountSetCode.setReadonly(isReadOnly("AccountingSetDialog_accountSetCode"));
		} else {
			this.eventCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.accountSetCode.setReadonly(true);
		}

		// this.btnSearchEventCode.setDisabled(isReadOnly("AccountingSetDialog_eventCode"));
		/*
		 * if(AccountEventConstants.ACCEVENT_LATEPAY.equals(getAccountingSet().getEventCode())){
		 * this.accountSetCode.setReadonly(true);
		 * this.button_TransactionEntryList_NewTransactionEntry.setVisible(false); }else{
		 */

		// }

		this.systemDefault.setDisabled(isReadOnly("AccountingSetDialog_systemDefault"));
		this.entryByInvestment.setDisabled(isReadOnly("AccountingSetDialog_entryByInvestment"));
		this.accountSetCodeName.setReadonly(isReadOnly("AccountingSetDialog_accountSetCodeName"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.accountingSet.isNewRecord()) {
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
		this.eventCode.setReadonly(true);
		this.accountSetCode.setReadonly(true);
		this.accountSetCodeName.setReadonly(true);
		this.entryByInvestment.setDisabled(true);

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
		this.eventCode.setValue("");
		this.lovDescEventCodeName.setValue("");
		this.accountSetCode.setValue("");
		this.accountSetCodeName.setValue("");
		this.entryByInvestment.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final AccountingSet aAccountingSet = new AccountingSet();
		BeanUtils.copyProperties(getAccountingSet(), aAccountingSet);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the AccountingSet object with the components data
		doWriteComponentsToBean(aAccountingSet);

		if (this.userAction.getSelectedItem() != null
				&& !this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_RESUBMITTED)
				&& !this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_FINALIZED)
				&& !this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_APPROVED)
				&& !this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_CANCELLED)
				&& !this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_REJECTED)) {
			if (!validateFees(aAccountingSet)) {
				return;
			}
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aAccountingSet.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAccountingSet.getRecordType())) {
				aAccountingSet.setVersion(aAccountingSet.getVersion() + 1);
				if (isNew) {
					aAccountingSet.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAccountingSet.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAccountingSet.setNewRecord(true);
				}
			}
		} else {
			aAccountingSet.setVersion(aAccountingSet.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aAccountingSet, tranType)) {
				refreshList();
				// do Close the Dialog window
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAccountingSet (AccountingSet)
	 * 
	 * @param tranType       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(AccountingSet aAccountingSet, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAccountingSet.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAccountingSet.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountingSet.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAccountingSet.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountingSet.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAccountingSet);
				}

				if (isNotesMandatory(taskId, aAccountingSet)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aAccountingSet.setTaskId(taskId);
			aAccountingSet.setNextTaskId(nextTaskId);
			aAccountingSet.setRoleCode(getRole());
			aAccountingSet.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountingSet, tranType);

			String operationRefs = getServiceOperations(taskId, aAccountingSet);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAccountingSet, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAccountingSet, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		AccountingSet aAccountingSet = (AccountingSet) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getAccountingSetService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getAccountingSetService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getAccountingSetService().doApprove(auditHeader);

					if (aAccountingSet.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getAccountingSetService().doReject(auditHeader);
					if (aAccountingSet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AccountingSetDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AccountingSetDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.accountingSet), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// Search Button Component Events

	public void onFulfill$eventCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = eventCode.getObject();
		if (dataObject instanceof String) {
			this.eventCode.setValue(dataObject.toString());
			this.lovDescEventCodeName.setValue("");
		} else {
			AccountEngineEvent details = (AccountEngineEvent) dataObject;

			if (details != null) {
				checkSystemDefault(details.getAEEventCode(), "");

				this.eventCode.setValue(details.getAEEventCode());
				this.lovDescEventCodeName.setValue(details.getAEEventCodeDesc());
			}
		}
		this.accountSetCode.setValue("");
		/*
		 * if(this.eventCode.getValue().equals(AccountEventConstants.ACCEVENT_LATEPAY)){
		 * this.accountSetCode.setReadonly(true); this.btnSearchAccountSetCode.setVisible(true);
		 * this.button_TransactionEntryList_NewTransactionEntry.setVisible(false); }else{
		 */

		this.accountSetCodeName.setValue("");
		this.accountSetCode.setReadonly(false);
		this.btnSearchAccountSetCode.setVisible(false);
		// }

		disableNewTransactionEntry();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchAccountSetCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_AccountingSetDialog, "OverdueCharge");
		if (dataObject instanceof String) {
			this.accountSetCode.setValue(dataObject.toString());
			this.accountSetCodeName.setValue("");
		} else {
			OverdueCharge details = (OverdueCharge) dataObject;

			if (details != null) {
				this.accountSetCode.setValue(details.getODCRuleCode());
				this.accountSetCodeName.setValue(details.getoDCRuleDescription());

				// OverDue transaction Entries preparation manually
				List<TransactionEntry> transactionEntries = getAccountingSetService().getODTransactionEntries();

				PFSParameter debitCode = SysParamUtil.getSystemParameterObject("ODC_DTCD");
				PFSParameter creditCode = SysParamUtil.getSystemParameterObject("ODC_CTCD");

				for (int i = 0; i < transactionEntries.size(); i++) {
					TransactionEntry entry = transactionEntries.get(i);
					entry.setRecordType(PennantConstants.RCD_ADD);
					if ("PLA".equals(entry.getAccountType())) {
						entry.setAccountType(details.getODCPLAccount());
						entry.setAccountSubHeadRule(details.getoDCPLSubHead());
					} else if ("CA".equals(entry.getAccountType())) {
						entry.setAccountType(details.getODCCharityAccount());
						entry.setAccountSubHeadRule(details.getoDCCharitySubHead());
					}

					if (entry.getDebitcredit().equals(AccountConstants.TRANTYPE_DEBIT)) {
						entry.setTranscationCode(debitCode.getSysParmValue());
						entry.setLovDescTranscationCodeName(
								debitCode.getSysParmValue() + "-" + debitCode.getSysParmDesc());
						entry.setRvsTransactionCode(creditCode.getSysParmValue());
						entry.setLovDescRvsTransactionCodeName(
								creditCode.getSysParmValue() + "-" + creditCode.getSysParmDesc());
					} else {
						entry.setTranscationCode(creditCode.getSysParmValue());
						entry.setLovDescTranscationCodeName(
								creditCode.getSysParmValue() + "-" + creditCode.getSysParmDesc());
						entry.setRvsTransactionCode(debitCode.getSysParmValue());
						entry.setLovDescRvsTransactionCodeName(
								debitCode.getSysParmValue() + "-" + debitCode.getSysParmDesc());
					}
				}
				doFilllistbox(transactionEntries);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the TransactionEntry dialog with a new empty entry. <br>
	 */
	public void onClick$button_TransactionEntryList_NewTransactionEntry(Event event) {
		logger.debug("Entering" + event.toString());

		String recordStatus = userAction.getSelectedItem().getValue();

		if (!StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_CANCELLED)
				&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_REJECTED)
				&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_RESUBMITTED)) {
			doSetValidation();
		}

		doWriteComponentsToBean(getAccountingSet());

		// create a new TransactionEntry object, We GET it from the backEnd.
		final TransactionEntry aTransactionEntry = getAccountingSetService().getNewTransactionEntry();
		aTransactionEntry.setLovDescEventCodeName(this.eventCode.getValue());
		aTransactionEntry.setLovDescAccSetCodeName(this.accountSetCode.getValue());
		aTransactionEntry.setLovDescAccSetCodeDesc(this.accountSetCodeName.getValue());
		aTransactionEntry.setLovDescEventCodeDesc(this.lovDescEventCodeName.getValue());
		if (ImplementationConstants.CLIENT_NAME.equals(ImplementationConstants.CLIENT_BFL)) {
			aTransactionEntry.setAccount(AccountConstants.TRANACC_BUILD);
		}

		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("transactionEntry", aTransactionEntry);
		map.put("accountingSetDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/TransactionEntry/TransactionEntryDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Double Click the Transaction Entry Item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onTransactionEntryItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Listitem item = (Listitem) event.getOrigin().getTarget();
		TransactionEntry itemdata = (TransactionEntry) item.getAttribute("data");
		itemdata.setLovDescEventCodeName(this.eventCode.getValue());
		itemdata.setLovDescAccSetCodeName(this.accountSetCode.getValue());
		itemdata.setLovDescAccSetCodeDesc(this.accountSetCodeName.getValue());
		itemdata.setLovDescEventCodeDesc(this.lovDescEventCodeName.getValue());
		itemdata.setNewRecord(false);
		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("transactionEntry", itemdata);
		map.put("accountingSetDialogCtrl", this);
		map.put("role", getRole());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/TransactionEntry/TransactionEntryDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(AccountingSet aAccountingSet, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountingSet.getBefImage(), aAccountingSet);
		return new AuditHeader(String.valueOf(aAccountingSet.getAccountSetid()), null, null, null, auditDetail,
				aAccountingSet.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.accountingSet);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.accountingSet.getAccountSetid());
	}

	private void checkSystemDefault(String eventCode, String setCode) {
		if (eventCode != null && StringUtils.isNotEmpty(eventCode)) {
			AccountingSet accountingSetmain = getAccountingSetService().getAccSetSysDflByEvent(eventCode, setCode,
					"_AView");
			if (accountingSetmain == null) {
				AccountingSet accountingSettemp = getAccountingSetService().getAccSetSysDflByEvent(eventCode, setCode,
						"_View");
				checkSysHelper(accountingSettemp);
			} else {
				checkSysHelper(accountingSetmain);
			}
		} else {
			this.systemDefault.setDisabled(true);

		}
	}

	private void checkSysHelper(AccountingSet accountingSet) {
		if (accountingSet == null) {
			this.systemDefault.setDisabled(isReadOnly("AccountingSetDialog_systemDefault"));
		} else {
			if (accountingSet.getAccountSetid() != getAccountingSet().getAccountSetid()) {
				this.systemDefault.setChecked(false);
				this.systemDefault.setDisabled(true);
			} else {
				this.systemDefault.setDisabled(isReadOnly("AccountingSetDialog_systemDefault"));
			}
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

	public AccountingSet getAccountingSet() {
		return this.accountingSet;
	}

	public void setAccountingSet(AccountingSet accountingSet) {
		this.accountingSet = accountingSet;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}

	public AccountingSetService getAccountingSetService() {
		return this.accountingSetService;
	}

	public void setAccountingSetListCtrl(AccountingSetListCtrl accountingSetListCtrl) {
		this.accountingSetListCtrl = accountingSetListCtrl;
	}

	public AccountingSetListCtrl getAccountingSetListCtrl() {
		return this.accountingSetListCtrl;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setTransactionEntryList(List<TransactionEntry> transactionEntryList) {
		this.transactionEntryList = transactionEntryList;
	}

	public List<TransactionEntry> getTransactionEntryList() {
		return transactionEntryList;
	}

	public PagedListWrapper<TransactionEntry> getTransactionEntryPagedListWrapper() {
		return TransactionEntryPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setTransactionEntryPagedListWrapper() {
		if (this.TransactionEntryPagedListWrapper == null) {
			this.TransactionEntryPagedListWrapper = (PagedListWrapper<TransactionEntry>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doClose(this.btnSave.isVisible());

		Events.postEvent("onClick$button_AccountingSetList_NewAccountingSet",
				accountingSetListCtrl.window_AccountingSetList, getAccountingSet());
		logger.debug("Leaving" + event.toString());
	}

	public boolean validateFees(AccountingSet aAccountingSet) {
		List<String> feeCodeTransactionList = new ArrayList<String>();
		if (aAccountingSet.getTransactionEntries() != null) {
			for (TransactionEntry transactionEntry : aAccountingSet.getTransactionEntries()) {
				String feeCode = getTransactionFeeCode(transactionEntry.getAmountRule());
				if (StringUtils.isNotEmpty(feeCode) && !feeCodeTransactionList.contains(feeCode)) {
					feeCodeTransactionList.add(feeCode);
				}
			}
		}
		if (!feeCodeTransactionList.isEmpty()) {
			Map<String, List<FinTypeFees>> finTypeFeeMap = getAccountingSetService().fetchFinTypeFees(aAccountingSet);
			if (finTypeFeeMap != null && !finTypeFeeMap.isEmpty()) {
				if (validateFintypeFees(feeCodeTransactionList, finTypeFeeMap)) {
					if (!validateAccountingFees(feeCodeTransactionList, finTypeFeeMap)) {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private boolean validateFintypeFees(List<String> feeCodeTransactionList,
			Map<String, List<FinTypeFees>> finTypeFeeMap) {
		List<ErrorDetail> feeErrorDetails = new ArrayList<ErrorDetail>();
		for (String feeCode : feeCodeTransactionList) {
			String finTypes = "";
			for (String finType : finTypeFeeMap.keySet()) {
				boolean feeConfigured = false;
				if (finTypeFeeMap.get(finType) == null) {
					continue;
				}
				for (FinTypeFees finTypeFee : finTypeFeeMap.get(finType)) {
					if (StringUtils.equals(feeCode, finTypeFee.getFeeTypeCode())) {
						feeConfigured = true;
						break;
					}
				}
				if (!feeConfigured) {
					if (StringUtils.isEmpty(finTypes)) {
						finTypes = finType;
					} else {
						finTypes = finTypes + "," + finType;
					}
				}
			}
			if (StringUtils.isNotEmpty(finTypes)) {
				String[] errParm = new String[2];
				String[] valueParm = new String[2];
				valueParm[0] = feeCode;
				errParm[0] = PennantJavaUtil.getLabel("label_FeeCode") + ":" + valueParm[0];
				valueParm[1] = finTypes;
				errParm[1] = PennantJavaUtil.getLabel("FinanceType_label") + ":" + valueParm[1];
				feeErrorDetails.add(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "WFEE04", errParm, valueParm),
						getUserWorkspace().getLoggedInUser().getLanguage()));
			}
		}
		if (feeErrorDetails != null && !feeErrorDetails.isEmpty()) {
			String errorMsg = "";
			String warningMsg = "";
			int errorCount = 0;
			int warningCount = 0;
			for (ErrorDetail errorDetail : feeErrorDetails) {
				if (errorDetail.getSeverity().equalsIgnoreCase(PennantConstants.ERR_SEV_ERROR)) {
					errorCount++;
					if (StringUtils.isEmpty(errorMsg)) {
						errorMsg = errorCount + ")" + errorDetail.getError();
					} else {
						errorMsg = errorMsg + " \n  \n " + errorCount + ")" + errorDetail.getError();
					}
				} else {
					warningCount++;
					if (StringUtils.isEmpty(warningMsg)) {
						warningMsg = warningCount + ")" + errorDetail.getError();
					} else {
						warningMsg = warningMsg + " \n \n " + warningCount + ")" + errorDetail.getError();
					}
				}
			}
			if (StringUtils.isNotEmpty(errorMsg)) {
				MessageUtil.showError(errorMsg);
				return false;
			} else if (StringUtils.isNotEmpty(warningMsg)) {
				warningMsg = warningMsg + " \n \n " + "Do you want to proceed?";
				if (MessageUtil.confirm(warningMsg) != MessageUtil.YES) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean validateAccountingFees(List<String> feeCodeTransactionList,
			Map<String, List<FinTypeFees>> finTypeFeeMap) {
		List<ErrorDetail> feeErrorDetails = new ArrayList<ErrorDetail>();

		Map<String, String> finTypeFeeCodeMap = new HashMap<String, String>();

		for (String finType : finTypeFeeMap.keySet()) {
			if (finTypeFeeMap.get(finType) == null) {
				continue;
			}
			for (FinTypeFees finTypeFee : finTypeFeeMap.get(finType)) {
				if (finTypeFeeCodeMap.containsKey(finTypeFee.getFeeTypeCode())) {
					String finTypes = finTypeFeeCodeMap.get(finTypeFee.getFeeTypeCode());
					finTypes = finTypes + "," + finTypeFee.getFinType();
					finTypeFeeCodeMap.put(finTypeFee.getFeeTypeCode(), finTypes);
				} else {
					finTypeFeeCodeMap.put(finTypeFee.getFeeTypeCode(), finTypeFee.getFinType());
				}
			}
		}
		if (!finTypeFeeCodeMap.isEmpty()) {
			for (String finTypeFeeCode : finTypeFeeCodeMap.keySet()) {
				boolean feeConfigured = false;
				for (String feeCode : feeCodeTransactionList) {
					if (StringUtils.equals(feeCode, finTypeFeeCode)) {
						feeConfigured = true;
						break;
					}
				}
				if (!feeConfigured) {
					String[] errParm = new String[2];
					String[] valueParm = new String[2];
					valueParm[0] = finTypeFeeCode;
					errParm[0] = PennantJavaUtil.getLabel("label_FeeCode") + ":" + valueParm[0];
					valueParm[1] = finTypeFeeCodeMap.get(finTypeFeeCode);
					errParm[1] = PennantJavaUtil.getLabel("FinanceType_label") + ":" + valueParm[1];
					feeErrorDetails.add(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "WFEE05", errParm, valueParm),
							getUserWorkspace().getLoggedInUser().getLanguage()));

				}
			}
		}
		if (feeErrorDetails != null && !feeErrorDetails.isEmpty()) {
			String errorMsg = "";
			String warningMsg = "";
			int errorCount = 0;
			int warningCount = 0;
			for (ErrorDetail errorDetail : feeErrorDetails) {
				if (errorDetail.getSeverity().equalsIgnoreCase(PennantConstants.ERR_SEV_ERROR)) {
					errorCount++;
					if (StringUtils.isEmpty(errorMsg)) {
						errorMsg = errorCount + ")" + errorDetail.getError();
					} else {
						errorMsg = errorMsg + " \n  \n " + errorCount + ")" + errorDetail.getError();
					}
				} else {
					warningCount++;
					if (StringUtils.isEmpty(warningMsg)) {
						warningMsg = warningCount + ")" + errorDetail.getError();
					} else {
						warningMsg = warningMsg + " \n \n " + warningCount + ")" + errorDetail.getError();
					}
				}
			}
			if (StringUtils.isNotEmpty(errorMsg)) {
				MessageUtil.showError(errorMsg);
				return false;
			} else if (StringUtils.isNotEmpty(warningMsg)) {
				warningMsg = warningMsg + "\n\n" + "Do you want to proceed?";
				if (MessageUtil.confirm(warningMsg) != MessageUtil.YES) {
					return false;
				}
			}
		}
		return true;
	}

	private String getTransactionFeeCode(String amountRule) {
		String[] feeCodeList = amountRule.split("[^a-zA-Z0-9_]+");
		String feeCode = "";
		for (int k = 0; k < feeCodeList.length; k++) {
			if ((StringUtils.isNotBlank(feeCodeList[k]) || "Result".equalsIgnoreCase(feeCodeList[k]))
					&& (feeCodeList[k].trim().endsWith("_W") || feeCodeList[k].trim().endsWith("_C")
							|| feeCodeList[k].trim().endsWith("_P") || feeCodeList[k].trim().endsWith("_AF"))) {
				if (!feeCode.contains(feeCodeList[k].trim().substring(0, feeCodeList[k].trim().indexOf('_')) + ",")) {
					feeCode = feeCode + feeCodeList[k].trim().substring(0, feeCodeList[k].trim().indexOf('_')) + ",";
				}
			}
		}
		if (feeCode.endsWith(",")) {
			feeCode = feeCode.substring(0, feeCode.length() - 1);
		}
		return feeCode;
	}

}
