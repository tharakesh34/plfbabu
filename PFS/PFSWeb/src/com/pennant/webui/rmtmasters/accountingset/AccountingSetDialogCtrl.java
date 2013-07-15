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
 * FileName    		:  AccountingSetDialogCtrl.java                                                   * 	  
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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.rmtmasters.accountingset.model.TransactionEntryListModelItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/AccountingSet/accountingSetDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AccountingSetDialogCtrl extends GFCBaseListCtrl<TransactionEntry> implements Serializable {

	private static final long serialVersionUID = 8602015982512929710L;
	private final static Logger logger = Logger.getLogger(AccountingSetDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_AccountingSetDialog; // autowired

	protected Textbox 		eventCode; 					// autowired
	protected Textbox 		accountSetCode; 			// autowired
	protected Textbox 		accountSetCodeName; 		// autowired
	protected Checkbox 		entryByInvestment; 			// autowired
	protected Checkbox 		systemDefault;				// autowired
	protected Button 		btnCopyTo;					// autowired

	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Grid 			grid_Basicdetails;			// autoWired

	protected Listbox 		listBoxTransactionEntry;
	protected Paging 		pagingTransactionEntryList;

	// not auto wired vars
	private AccountingSet accountingSet; // overhanded per param
	private transient AccountingSetListCtrl accountingSetListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_eventCode;
	private transient String oldVar_accountSetCode;
	private transient String oldVar_accountSetCodeName;
	private transient boolean oldVar_entryByInvestment;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_AccountingSetDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	protected Button button_TransactionEntryList_NewTransactionEntry; // autowired
	protected Button btnSearchAccountSetCode; // autowire
	
	protected Button btnSearchEventCode; // autowire
	protected Textbox lovDescEventCodeName;
	private transient String oldVar_lovDescEventCodeName;

	// ServiceDAOs / Domain Classes
	private transient AccountingSetService accountingSetService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private List<TransactionEntry> transactionEntryList = new ArrayList<TransactionEntry>();
	private List<TransactionEntry> oldVar_transactionEntryList = new ArrayList<TransactionEntry>();
	private PagedListWrapper<TransactionEntry> TransactionEntryPagedListWrapper;
	int listRows;
	
	/**
	 * default constructor.<br>
	 */
	public AccountingSetDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountingSet object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountingSetDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		setTransactionEntryPagedListWrapper();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit,this.btnDelete, this.btnSave, this.btnCancel, 
				this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("accountingSet")) {
			this.accountingSet = (AccountingSet) args.get("accountingSet");
			AccountingSet befImage = new AccountingSet();
			BeanUtils.copyProperties(this.accountingSet, befImage);
			this.accountingSet.setBefImage(befImage);
			setAccountingSet(this.accountingSet);
		} else {
			setAccountingSet(null);
		}

		doLoadWorkFlow(this.accountingSet.isWorkflow(), 
				this.accountingSet.getWorkflowId(), this.accountingSet.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "AccountingSetDialog");
		}

		// READ OVERHANDED params !
		// we get the accountingSetListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete accountingSet here.
		if (args.containsKey("accountingSetListCtrl")) {
			setAccountingSetListCtrl((AccountingSetListCtrl) args.get("accountingSetListCtrl"));
		} else {
			setAccountingSetListCtrl(null);
		}

		getBorderLayoutHeight();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 + 170; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listBoxTransactionEntry.setHeight(listboxHeight+"px");
		listRows = Math.round(listboxHeight/ 24)-1;
		pagingTransactionEntryList.setPageSize(listRows);
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getAccountingSet());
		disableNewTransactionEntry();
		checkListboxcount();
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.eventCode.setMaxlength(8);
		this.accountSetCode.setMaxlength(8);
		this.accountSetCodeName.setMaxlength(50);

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
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("AccountingSetDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.button_TransactionEntryList_NewTransactionEntry.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnNewTransactionEntry"));
		this.btnCopyTo.setVisible(getUserWorkspace().isAllowed("button_AccountingSetDialog_btnCopyTo"));

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_AccountingSetDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
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
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_AccountingSetDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");

		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_AccountingSetDialog, "AccountingSet");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountingSet
	 *            (AccountingSet)
	 */
	public void doWriteBeanToComponents(AccountingSet aAccountingSet) {
		logger.debug("Entering");
		
		this.eventCode.setValue(aAccountingSet.getEventCode());
		this.accountSetCode.setValue(aAccountingSet.getAccountSetCode());
		this.accountSetCodeName.setValue(aAccountingSet.getAccountSetCodeName());
		this.entryByInvestment.setChecked(aAccountingSet.isEntryByInvestment());
		this.systemDefault.setChecked(aAccountingSet.isSystemDefault());
		
		doFilllistbox(aAccountingSet.getTransactionEntries());
		checkSystemDefault(aAccountingSet.getEventCode());
		if (aAccountingSet.getLovDescEventCodeName()!=null) {
			this.lovDescEventCodeName.setValue(aAccountingSet.getLovDescEventCodeName());
		}
		if(this.eventCode.getValue().equals("LATEPAY")){
			this.button_TransactionEntryList_NewTransactionEntry.setVisible(false);
		}
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
		aAccountingSet.setTransactionEntries(getTransactionEntryList());

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
	 * @throws InterruptedException
	 */
	public void doShowDialog(AccountingSet aAccountingSet) throws InterruptedException {
		logger.debug("Entering");

		// if aAccountingSet == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aAccountingSet == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aAccountingSet = getAccountingSetService().getNewAccountingSet();
			setAccountingSet(aAccountingSet);
		} else {
			setAccountingSet(aAccountingSet);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aAccountingSet.isNew()) {
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
			
			if (this.listBoxTransactionEntry.getItemCount()==0) {
	            this.btnCopyTo.setVisible(false);
            }
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_AccountingSetDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
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
			getTransactionEntryList().clear();
			setTransactionEntryList(transactionEntryList);
			this.pagingTransactionEntryList.setDetailed(true);
			getTransactionEntryPagedListWrapper().initList(transactionEntryList, 
					this.listBoxTransactionEntry, this.pagingTransactionEntryList);
			this.listBoxTransactionEntry.setItemRenderer(new TransactionEntryListModelItemRenderer());
		}
		checkListboxcount();
		logger.debug("Leaving");
	}

	private void disableNewTransactionEntry() {
		logger.debug("Entering");
		if (this.eventCode.getValue() == null || this.eventCode.getValue().equals("")) {
			this.button_TransactionEntryList_NewTransactionEntry.setDisabled(true);
		} else {
			this.button_TransactionEntryList_NewTransactionEntry.setDisabled(false);
		}
		logger.debug("Leaving");
	}

	private void checkListboxcount() {
		logger.debug("Entering");
		if (this.listBoxTransactionEntry.getItemCount() > 0) {
			this.btnSearchEventCode.setVisible(false);
			this.entryByInvestment.setDisabled(true);
		} else {
			this.btnSearchEventCode.setVisible(true);
			this.entryByInvestment.setDisabled(false);
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_eventCode = this.eventCode.getValue();
		this.oldVar_lovDescEventCodeName = this.lovDescEventCodeName.getValue();
		this.oldVar_accountSetCode = this.accountSetCode.getValue();
		this.oldVar_accountSetCodeName = this.accountSetCodeName.getValue();
		this.oldVar_entryByInvestment = this.entryByInvestment.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_transactionEntryList = getTransactionEntryList();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.eventCode.setValue(this.oldVar_eventCode);
		this.lovDescEventCodeName.setValue(this.oldVar_lovDescEventCodeName);
		this.accountSetCode.setValue(this.oldVar_accountSetCode);
		this.accountSetCodeName.setValue(this.oldVar_accountSetCodeName);
		this.entryByInvestment.setChecked(this.oldVar_entryByInvestment);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.transactionEntryList = this.oldVar_transactionEntryList;

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_eventCode != this.eventCode.getValue()) {
			return true;
		}
		if (this.oldVar_accountSetCode != this.accountSetCode.getValue()) {
			return true;
		}
		if (this.oldVar_accountSetCodeName != this.accountSetCodeName.getValue()) {
			return true;
		}
		if (this.oldVar_entryByInvestment != this.entryByInvestment.isChecked()) {
			return true;
		}
		if(this.oldVar_transactionEntryList != this.transactionEntryList){
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.accountSetCode.isReadonly()) {
			this.accountSetCode.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_AccountingSetDialog_AccountSetCode.value") }));
		}
		if (!this.accountSetCodeName.isReadonly()) {
			this.accountSetCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_AccountingSetDialog_AccountSetCodeName.value") }));
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
		this.eventCode.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_AccountingSetDialog_EventCode.value") }));
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints to the LOVFields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescEventCodeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Clear the Error Messages
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.eventCode.setErrorMessage("");
		this.lovDescEventCodeName.setErrorMessage("");
		this.accountSetCode.setErrorMessage("");
		this.accountSetCodeName.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for refreshing the list in ListCtrl
	 */
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<AccountingSet> soAccountingSet = getAccountingSetListCtrl().getSearchObj();
		getAccountingSetListCtrl().pagingAccountingSetList.setActivePage(0);
		getAccountingSetListCtrl().getPagedListWrapper().setSearchObject(soAccountingSet);
		if (getAccountingSetListCtrl().listBoxAccountingSet != null) {
			getAccountingSetListCtrl().listBoxAccountingSet.getListModel();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a AccountingSet object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final AccountingSet aAccountingSet = new AccountingSet();
		BeanUtils.copyProperties(getAccountingSet(), aAccountingSet);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +
		aAccountingSet.getEventCode()+":"+aAccountingSet.getAccountSetCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aAccountingSet.getRecordType()).equals("")) {
				aAccountingSet.setVersion(aAccountingSet.getVersion() + 1);
				aAccountingSet.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAccountingSet.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAccountingSet, tranType)) {
					refreshList();
					// do Close the dialog
					closeDialog(this.window_AccountingSetDialog, "AccountingSet");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new AccountingSet object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		
		final AccountingSet aAccountingSet = getAccountingSetService().getNewAccountingSet();
		setAccountingSet(aAccountingSet);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.accountSetCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getAccountingSet().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.btnSearchEventCode.setDisabled(false);
			this.btnCopyTo.setDisabled(true);
			this.btnCopyTo.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchEventCode.setDisabled(true);
		}
		
		// this.btnSearchEventCode.setDisabled(isReadOnly("AccountingSetDialog_eventCode"));
		if("LATEPAY".equals(getAccountingSet().getEventCode())){
			this.accountSetCode.setReadonly(true);
			this.button_TransactionEntryList_NewTransactionEntry.setVisible(false);
		}else{
			this.accountSetCode.setReadonly(isReadOnly("AccountingSetDialog_accountSetCode"));
		}
		
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
		this.btnSearchEventCode.setDisabled(true);
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the AccountingSet object with the components data
		doWriteComponentsToBean(aAccountingSet);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aAccountingSet.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aAccountingSet.getRecordType()).equals("")) {
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
				closeDialog(this.window_AccountingSetDialog, "AccountingSet");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAccountingSet
	 *            (AccountingSet)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(AccountingSet aAccountingSet, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAccountingSet.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aAccountingSet.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountingSet.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aAccountingSet.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountingSet.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aAccountingSet);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, 
						aAccountingSet))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aAccountingSet.setTaskId(taskId);
			aAccountingSet.setNextTaskId(nextTaskId);
			aAccountingSet.setRoleCode(getRole());
			aAccountingSet.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountingSet, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aAccountingSet);

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
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
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

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AccountingSetDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_AccountingSetDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchEventCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_AccountingSetDialog, "AccountEngineEvent");
		if (dataObject instanceof String) {
			this.eventCode.setValue(dataObject.toString());
			this.lovDescEventCodeName.setValue("");
		} else {
			AccountEngineEvent details = (AccountEngineEvent) dataObject;

			if (details != null) {
				checkSystemDefault(details.getAEEventCode());

				this.eventCode.setValue(details.getAEEventCode());
				this.lovDescEventCodeName.setValue(details.getAEEventCodeDesc());
			}
		}
		this.accountSetCode.setValue("");
		if(this.eventCode.getValue().equals("LATEPAY")){
			this.accountSetCode.setReadonly(true);
			this.btnSearchAccountSetCode.setVisible(true);
			this.button_TransactionEntryList_NewTransactionEntry.setVisible(false);
		}else{
			
			this.accountSetCodeName.setValue("");
			this.accountSetCode.setReadonly(false);
			this.btnSearchAccountSetCode.setVisible(false);
		}

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
				
				//OverDue transaction Entries preparation manually
				List<TransactionEntry> transactionEntries = getAccountingSetService().getODTransactionEntries();
				
				PFSParameter debitCode = SystemParameterDetails.getSystemParameterObject("ODC_DTCD");
				PFSParameter creditCode = SystemParameterDetails.getSystemParameterObject("ODC_CTCD");
				
				for (int i = 0; i < transactionEntries.size(); i++) {
					TransactionEntry entry = transactionEntries.get(i);
					entry.setRecordType(PennantConstants.RCD_ADD);
					if(entry.getAccountType().equals("PLA")){
						entry.setAccountType(details.getODCPLAccount());
						entry.setAccountSubHeadRule(details.getoDCPLSubHead());
					}else if(entry.getAccountType().equals("CA")){
						entry.setAccountType(details.getODCCharityAccount());
						entry.setAccountSubHeadRule(details.getoDCCharitySubHead());
					}
					
					if(entry.getDebitcredit().equals("D")){
						entry.setTranscationCode(debitCode.getSysParmValue());
						entry.setLovDescTranscationCodeName(debitCode.getSysParmValue()+"-"+debitCode.getSysParmDesc());
						entry.setRvsTransactionCode(creditCode.getSysParmValue());
						entry.setLovDescRvsTransactionCodeName(creditCode.getSysParmValue()+"-"+creditCode.getSysParmDesc());
					}else{
						entry.setTranscationCode(creditCode.getSysParmValue());
						entry.setLovDescTranscationCodeName(creditCode.getSysParmValue()+"-"+creditCode.getSysParmDesc());
						entry.setRvsTransactionCode(debitCode.getSysParmValue());
						entry.setLovDescRvsTransactionCodeName(debitCode.getSysParmValue()+"-"+debitCode.getSysParmDesc());
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
	public void onClick$button_TransactionEntryList_NewTransactionEntry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new TransactionEntry object, We GET it from the backEnd.
		final TransactionEntry aTransactionEntry = getAccountingSetService().getNewTransactionEntry();
		aTransactionEntry.setLovDescEventCodeName(this.eventCode.getValue());
		aTransactionEntry.setLovDescAccSetCodeName(this.accountSetCode.getValue());
		aTransactionEntry.setLovDescAccSetCodeDesc(this.accountSetCodeName.getValue());
		aTransactionEntry.setLovDescEventCodeDesc(this.lovDescEventCodeName.getValue());

		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("transactionEntry", aTransactionEntry);
		map.put("accountingSetDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RulesFactory/TransactionEntry/TransactionEntryDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("transactionEntry", itemdata);
		map.put("accountingSetDialogCtrl", this);
		map.put("role", getRole());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RulesFactory/TransactionEntry/TransactionEntryDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(AccountingSet aAccountingSet, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountingSet.getBefImage(), aAccountingSet);
		return new AuditHeader(String.valueOf(aAccountingSet.getAccountSetid()), 
				null, null, null, auditDetail, aAccountingSet.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_AccountingSetDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("AccountingSet");
		notes.setReference(String.valueOf(getAccountingSet().getAccountSetid()));
		notes.setVersion(getAccountingSet().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	private void checkSystemDefault(String eventCode) {
		if (eventCode != null && !eventCode.equals("")) {
			AccountingSet accountingSetmain = getAccountingSetService().getAccSetSysDflByEvent(eventCode, "_AView");
			if (accountingSetmain == null) {	
				AccountingSet accountingSettemp = getAccountingSetService().getAccSetSysDflByEvent(eventCode ,"_View");	
				checkSysHelper(accountingSettemp);
			}else{
				checkSysHelper(accountingSetmain);
			}
		} else {
			this.systemDefault.setDisabled(true);

		}
	}

	private void checkSysHelper(AccountingSet accountingSet){		
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
		
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
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
			this.TransactionEntryPagedListWrapper = (PagedListWrapper<TransactionEntry>) SpringUtil.getBean("pagedListWrapper");
		}
	}

	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		Events.postEvent("onClick$button_AccountingSetList_NewAccountingSet", 
				accountingSetListCtrl.window_AccountingSetList, getAccountingSet());
		logger.debug("Leaving" + event.toString());
	}
	
	
	
}
