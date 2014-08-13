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
 * FileName    		:  FinTypeAccountDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.rmtmasters.financetype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinTypeAccountDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinTypeAccountDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinTypeAccountDialog; // autowired

	protected ExtendedCombobox finCcy; // autowired
	protected Combobox finEvent; // autowired
	protected Checkbox alwManualEntry; // autowired
	protected Checkbox alwCustAccount; // autowired
	protected Textbox accountReceivable; // autowired
	protected Textbox custAccountTypes; // autowired
	protected Button btnSearchAccountTypes; // autowired
	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	// not auto wired vars
	private FinTypeAccount finTypeAccount; // overhanded per param
	private transient FinTypeAccountDialogCtrl finTypeAccountDialogCtrl; // overhanded per
	// param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.

	private transient String oldVar_finCcy;
	private transient String oldVar_event;
	private transient boolean oldVar_alwManualEntry;
	private transient boolean oldVar_alwCustAccount;
	private transient String oldVar_accountReceivable;
	private transient String oldVar_custAccountTypes;


	private transient String oldVar_recordStatus;
	private transient boolean validationOn;
	private boolean notes_Entered = false;
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinTypeAccountDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private transient AccountInterfaceService accountInterfaceService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	protected List<ValueLabel> eventList = PennantStaticListUtil.getAccountEventsList();
	private String userRole="";
	private FinanceTypeDialogCtrl financeTypeDialogCtrl;
	private List<FinTypeAccount> finTypeAccountList;
	protected Map<String, Object> accounTypesDataMap = new HashMap<String, Object>();
	protected Listbox accountReceivableList;
	protected Button addAccountReceivable;
	private List<CoreBankAccountDetail> accounts=new ArrayList<CoreBankAccountDetail>();
	private int ccyFormat=0;

	/**
	 * default constructor.<br>
	 */
	public FinTypeAccountDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected finTypeAccount object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinTypeAccountDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {
			/* set components visible dependent of the users rights */

			/*
			 * create the Button Controller. Disable not used buttons during
			 * working
			 */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("finTypeAccount")) {
				this.finTypeAccount = (FinTypeAccount) args.get("finTypeAccount");
				FinTypeAccount befImage = new FinTypeAccount();
				BeanUtils.copyProperties(this.finTypeAccount, befImage);
				this.finTypeAccount.setBefImage(befImage);
				setFinTypeAccount(this.finTypeAccount);
			} else {
				setFinTypeAccount(null);
			}
			this.finTypeAccount.setWorkflowId(0);
			doLoadWorkFlow(this.finTypeAccount.isWorkflow(), this.finTypeAccount.getWorkflowId(), this.finTypeAccount.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "FinTypeAccountDialog");
			}
			if (args.containsKey("role")) {
				userRole=args.get("role").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "FinTypeAccountDialog");
			}
			doCheckRights();
			// READ OVERHANDED params !
			// we get the finTypeAccountlListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete FinTypeAccount here.
			if (args.containsKey("financeTypeDialogCtrl")) {
				setFinanceTypeDialogCtrl((FinanceTypeDialogCtrl) args.get("financeTypeDialogCtrl"));
			} else {
				setFinanceTypeDialogCtrl(null);
			}
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
					.getValue().intValue()- PennantConstants.borderlayoutMainNorth;

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinTypeAccount());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_FinTypeAccountDialog.onClose();
		}
		logger.debug("Leaving");
	}

	public FinTypeAccount getFinTypeAccount() {
		return finTypeAccount;
	}

	public void setFinTypeAccount(FinTypeAccount finTypeAccount) {
		this.finTypeAccount = finTypeAccount;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.finCcy.setMaxlength(3);
		this.finCcy.setMandatoryStyle(true);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });

		this.accountReceivable.setMaxlength(13);
		this.custAccountTypes.setMaxlength(300);
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
		getUserWorkspace().alocateAuthorities("FinTypeAccountDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
	public void onClose$window_FinTypeAccountDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinTypeAccountDialog);
		logger.debug("Leaving");
	}


	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}


	// GUI Process
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
			closePopUpWindow(this.window_FinTypeAccountDialog,"FinTypeAccountDialog");
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
	 * @param aFinTypeAccount
	 *            FinTypeAccount
	 */
	public void doWriteBeanToComponents(FinTypeAccount aFinTypeAccount) {
		logger.debug("Entering");
		this.finCcy.setValue(aFinTypeAccount.getFinCcy()); 
		fillComboBox(this.finEvent, aFinTypeAccount.getEvent(), eventList, "");
		this.alwManualEntry.setChecked(aFinTypeAccount.isAlwManualEntry()); 
		this.alwCustAccount.setChecked(aFinTypeAccount.isAlwCustomerAccount()); 
		this.custAccountTypes.setValue(aFinTypeAccount.getCustAccountTypes()); 
		if (aFinTypeAccount.isNewRecord()) {
			this.finCcy.setDescription("");
		} else {
			this.finCcy.setDescription(aFinTypeAccount.getFinCcyName());
		}
		this.ccyFormat = aFinTypeAccount.getFinFormatter();
		setAccountNumberDetails(aFinTypeAccount.getAccountReceivable());
		setAccountTypesDetails(aFinTypeAccount.getCustAccountTypes());
		alwManualEntryIsChecked();
		this.recordStatus.setValue(aFinTypeAccount.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinTypeAccount
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(FinTypeAccount aFinTypeAccount) throws InterruptedException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aFinTypeAccount.setFinCcyName(this.finCcy.getDescription());
			aFinTypeAccount.setFinCcy(this.finCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (getComboboxValue(this.finEvent).equals("#")) {
				throw new WrongValueException(this.finEvent, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinTypeAccountDialog_Event.value") }));
			}
			aFinTypeAccount.setEvent(getComboboxValue(this.finEvent));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinTypeAccount.setAlwManualEntry(this.alwManualEntry.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeAccount.setAlwCustomerAccount(this.alwCustAccount.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeAccount.setAccountReceivable(getAccountNumberDetails(getAccounts()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeAccount.setCustAccountTypes(this.custAccountTypes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aFinTypeAccount.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinTypeAccount
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinTypeAccount aFinTypeAccount) throws InterruptedException {
		logger.debug("Entering");
		// if aFinTypeAccount == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aFinTypeAccount == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinTypeAccount = null;// getFinanceTypeService().getNewFinTypeAccount();
			setFinTypeAccount(aFinTypeAccount);
		} else {
			setFinTypeAccount(aFinTypeAccount);
		}
		// set Readonly mode accordingly if the object is new or not.
		if (aFinTypeAccount.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finCcy.focus();
		} else {
			this.alwManualEntry.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinTypeAccount);
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.window_FinTypeAccountDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_event = this.finEvent.getValue();
		this.oldVar_alwManualEntry = this.alwManualEntry.isChecked();
		this.oldVar_alwCustAccount = this.alwCustAccount.isChecked();
		this.oldVar_accountReceivable = this.accountReceivable.getValue();
		this.oldVar_custAccountTypes = this.custAccountTypes.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finCcy.setValue(this.oldVar_finCcy);
		this.finEvent.setValue(this.oldVar_event);
		this.alwManualEntry.setValue(this.oldVar_alwManualEntry);
		this.alwCustAccount.setValue(this.oldVar_alwCustAccount);
		this.accountReceivable.setValue(this.oldVar_accountReceivable);
		this.custAccountTypes.setValue(this.oldVar_custAccountTypes);
		this.recordStatus.setValue(this.oldVar_recordStatus);
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
		logger.debug("Entering");
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		if (this.oldVar_event != this.finEvent.getValue()) {
			return true;
		}
		if (this.oldVar_alwManualEntry != this.alwManualEntry.isChecked()) {
			return true;
		}
		if (this.oldVar_alwCustAccount != this.alwCustAccount.isChecked()) {
			return true;
		}
		if (this.oldVar_accountReceivable != this.accountReceivable.getValue()) {
			return true;
		}
		if (this.oldVar_custAccountTypes != this.custAccountTypes.getValue()) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.finCcy.isReadonly()) {
			this.finCcy.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinTypeAccountDialog_FinCcy.value") }));
		}
		if (!this.finEvent.isReadonly()) {
			this.finEvent.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinTypeAccountDialog_Event.value") }));
		}
	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finCcy.setConstraint("");
		this.finEvent.setConstraint("");
		this.accountReceivable.setConstraint("");
		this.custAccountTypes.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Deletes a FinTypeAccount object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinTypeAccount aFinTypeAccount = new FinTypeAccount();
		BeanUtils.copyProperties(getFinTypeAccount(), aFinTypeAccount);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinTypeAccount.getFinCcy();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aFinTypeAccount.getRecordType()).equals("")) {
				aFinTypeAccount.setVersion(aFinTypeAccount.getVersion() + 1);
				aFinTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aFinTypeAccount.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aFinTypeAccount.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aFinTypeAccount.setVersion(aFinTypeAccount.getVersion() + 1);
				aFinTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newCustAccTypesProcess(aFinTypeAccount, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinTypeAccountDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinanceTypeDialogCtrl().doFillCustAccountTypes(this.finTypeAccountList);
					closePopUpWindow(this.window_FinTypeAccountDialog,"FinTypeAccountDialog");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
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
		if (getFinTypeAccount().isNewRecord()) {
			this.finCcy.setReadonly(false);
			this.finEvent.setDisabled(false);
		} else {
			this.finCcy.setReadonly(true);
			this.finEvent.setDisabled(true);
		}
		readOnlyComponent(isReadOnly("FinTypeAccountDialog_alwManualEntry"), this.alwManualEntry);
		readOnlyComponent(isReadOnly("FinTypeAccountDialog_alwCustAccount"), this.alwCustAccount);
		readOnlyComponent(isReadOnly("FinTypeAccountDialog_accountReceivable"), this.accountReceivable);
		this.addAccountReceivable.setVisible(!isReadOnly("FinTypeAccountDialog_accountReceivable"));
		this.btnSearchAccountTypes.setDisabled(isReadOnly("FinTypeAccountDialog_custAccountTypes"));		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finTypeAccount.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}
	public boolean isReadOnly(String componentName){
		return getUserWorkspace().isReadOnly(componentName);
	}
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finCcy.setReadonly(true);
		this.finEvent.setDisabled(true);
		this.alwManualEntry.setDisabled(true);
		this.alwCustAccount.setDisabled(true);
		this.accountReceivable.setReadonly(true);
		this.addAccountReceivable.setDisabled(true);
		this.btnSearchAccountTypes.setDisabled(true);

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
		this.finCcy.setValue("");
		this.finCcy.setDescription("");
		this.finEvent.setValue("");
		this.alwManualEntry.setChecked(false);
		this.alwCustAccount.setChecked(false);
		this.accountReceivable.setValue("");
		this.custAccountTypes.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinTypeAccount aFinTypeAccount = new FinTypeAccount();
		BeanUtils.copyProperties(getFinTypeAccount(), aFinTypeAccount);
		boolean isNew = false;
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the FinTypeAccount object with the components data
		doWriteComponentsToBean(aFinTypeAccount);
		if(!this.alwManualEntry.isChecked() && getAccountNumberDetails(getAccounts()).equals("") && this.custAccountTypes.getValue().equals("")){
        	PTMessageUtils.showErrorMessage("Please Enter Either Of The Fields   :  "+Labels.getLabel("label_FinTypeAccountDialog_AlwManualEntry.value")+" , "
        			+Labels.getLabel("label_FinTypeAccountDialog_CustAccountTypes.value")+" , "+Labels.getLabel("label_FinTypeAccountDialog_AccountReceivable.value"));
        	return;
        }
		// Write the additional validations as per below example
		// get the selected FinTypeAccount object from the listbox
		// Do data level validations here
		isNew = aFinTypeAccount.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinTypeAccount.getRecordType()).equals("")) {
				aFinTypeAccount.setVersion(aFinTypeAccount.getVersion() + 1);
				if (isNew) {
					aFinTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinTypeAccount.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aFinTypeAccount.setVersion(1);
				aFinTypeAccount.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.trimToEmpty(aFinTypeAccount.getRecordType()).equals("")) {
				aFinTypeAccount.setVersion(aFinTypeAccount.getVersion() + 1);
				aFinTypeAccount.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aFinTypeAccount.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aFinTypeAccount.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			AuditHeader auditHeader = newCustAccTypesProcess(aFinTypeAccount, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FinTypeAccountDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFinanceTypeDialogCtrl().doFillCustAccountTypes(finTypeAccountList);
				closePopUpWindow(this.window_FinTypeAccountDialog,"FinTypeAccountDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method validates  FinTypeAccount details <br>
	 * and will return AuditHeader
	 *
	 */
	private AuditHeader newCustAccTypesProcess(FinTypeAccount aFinTypeAccount, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aFinTypeAccount, tranType);
		finTypeAccountList = new ArrayList<FinTypeAccount>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aFinTypeAccount.getFinCcy();
		valueParm[1] = aFinTypeAccount.getEvent();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeAccountDialog_FinCcy.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FinTypeAccountDialog_Event.value") + ":" + valueParm[1];
		List<FinTypeAccount> list = getFinanceTypeDialogCtrl().getFinTypeAccountList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				FinTypeAccount finTypeAccount = list.get(i);
				if (finTypeAccount.getFinCcy().equals(aFinTypeAccount.getFinCcy()) && finTypeAccount.getEvent().equals(aFinTypeAccount.getEvent())) {
					// Both Current and Existing list rating same
					if (aFinTypeAccount.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aFinTypeAccount.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aFinTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finTypeAccountList.add(aFinTypeAccount);
						} else if (aFinTypeAccount.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aFinTypeAccount.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aFinTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finTypeAccountList.add(aFinTypeAccount);
						} else if (aFinTypeAccount.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							List<FinTypeAccount> savedList = getFinanceTypeDialogCtrl().getFinanceType().getFinTypeAccounts();
							for (int j = 0; j < savedList.size(); j++) {
								FinTypeAccount accType = savedList.get(j);
								if (accType.getFinType().equals(aFinTypeAccount.getFinType())) {
									finTypeAccountList.add(accType);
								}
							}
						} else if (aFinTypeAccount.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aFinTypeAccount.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							finTypeAccountList.add(finTypeAccount);
						}
					}
				} else {
					finTypeAccountList.add(finTypeAccount);
				}
			}
		}
		if (!recordAdded) {
			finTypeAccountList.add(aFinTypeAccount);
		}
		logger.debug("Leaving");
		return auditHeader;
	}


	/**
	 * When user clicks on "btnSearchAccountTypes" button
	 * This method displays ExtendedMultipleSearchListBox with AccountType details
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnSearchAccountTypes(Event event){
		logger.debug("Entering " + event.toString());
		Object dataObject = ExtendedMultipleSearchListBox.show(this.window_FinTypeAccountDialog, "AccountType", this.accounTypesDataMap);
		if (dataObject instanceof String){
			this.custAccountTypes.setValue(dataObject.toString());
		}else{
			HashMap<String,Object> details= (HashMap<String,Object>) dataObject;
			if (details != null) {
				String multivalues=details.keySet().toString();
				this.custAccountTypes.setValue(multivalues.replace("[","").replace("]","").replace(" ", ""));
			}
			String toolTipDesc="";
			for (String  key : details.keySet()) {
				Object obj = (Object)details.get(key);
				if(obj instanceof String){
					//	
				}else{
					AccountType accountType = (AccountType)obj;
					if(finTypeAccount != null){
						toolTipDesc = toolTipDesc.concat(accountType.getAcTypeDesc()+" , ");
					}
				}
			}
			if(!StringUtils.trimToEmpty(toolTipDesc).equals("") && toolTipDesc.endsWith(", ")){
				toolTipDesc = toolTipDesc.substring(0,toolTipDesc.length()-2);
			}
			this.custAccountTypes.setTooltiptext(toolTipDesc);
		}
		logger.debug("Leaving " + event.toString());
	}
	
	public void onCheck$alwManualEntry(Event event){
		logger.debug(event.toString());
		alwManualEntryIsChecked();
		logger.debug(event.toString());
	}
	
	private void alwManualEntryIsChecked(){
		logger.debug("Entering ");
		if(alwManualEntry.isChecked()){
			alwCustAccount.setDisabled(isReadOnly("FinTypeAccountDialog_alwCustAccount"));
		}else{
			alwCustAccount.setDisabled(true);
			alwCustAccount.setChecked(false);
		}
		logger.debug("Leaving ");
	}
	public void onFulfill$finCcy(Event event){
		Object obj=this.finCcy.getObject();
		if (obj !=null && obj instanceof Currency) {
			Currency currency=(Currency) obj;
			this.ccyFormat=currency.getCcyEditField();
		}
		
	}

	/**
	 * This method will add  account number to <br>
	 * accountReceivableList
	 *
	 */
	public void onClick$addAccountReceivable(Event event) throws WrongValueException, InterruptedException {
		logger.debug(event.toString());
		boolean valid = true;
		String ccy=this.finCcy.getValidatedValue();
		if (StringUtils.trimToEmpty(ccy).equals("")) {
			valid = false;
			throw new WrongValueException(this.finCcy, Labels.getLabel("FIELD_IS_MAND",
					new String[] {Labels.getLabel("label_FinTypeAccountDialog_FinCcy.value") }));
		}
		String accno = this.accountReceivable.getValue();
		if (StringUtils.trimToEmpty(accno).equals("")) {
			valid = false;
			throw new WrongValueException(this.accountReceivable, Labels.getLabel("FIELD_IS_MAND",
					new String[] {Labels.getLabel("label_FinTypeAccountDialog_AccReceivableAccNumber.value") }));
		}
		try {
			Pattern pattern = Pattern.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ACCOUNT));
			Matcher matcher = pattern.matcher(accno);
			valid = matcher.matches();
		} catch (Exception e) {
			logger.debug(e);
		}
		if (!valid) {
			throw new WrongValueException(this.accountReceivable, Labels.getLabel(PennantRegularExpressions.REGEX_ACCOUNT,
					new String[] {Labels.getLabel("label_FinTypeAccountDialog_AccReceivableAccNumber.value") }));
		} else {
			CoreBankAccountDetail accountDetail = validateAccountInEquation(accno);
			if (accountDetail == null) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("ACCOUNT_INVALID",new String[]{accno}));
			} else {
				if (!accountDetail.getAcCcy().equals(ccy)) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("ACCOUNT_CCY_MISMATCH",new String[]{accountDetail.getAcCcy(),Labels.getLabel("label_FinTypeAccountDialog_FinCcy.value"),ccy }));
					return;
				}
				if (!isAccAlreadyInList(accno)) {
					accounts.add(accountDetail);
				}else{
					PTMessageUtils.showErrorMessage(Labels.getLabel("ACCOUNT_EXISTS",new String[]{accno}));
				}
				doFillAccountReceivables(accounts);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * this method validates account number balance <br>
	 * by passing argument to checkAccountID method in 
	 * AccountInterfaceService
	 */
	private CoreBankAccountDetail validateAccountInEquation(String accountId) throws InterruptedException {
		try {
			List<CoreBankAccountDetail> coreAcctList=new ArrayList<CoreBankAccountDetail>();
			CoreBankAccountDetail accountDetail=new CoreBankAccountDetail();
			accountDetail.setAccountNumber(accountId);
			coreAcctList.add(accountDetail);
			List<CoreBankAccountDetail> accountDetails = getAccountInterfaceService().checkAccountID(coreAcctList);
			if (accountDetails!=null && !accountDetails.isEmpty()) {
				return accountDetails.get(0);
			}
			
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}

	private void doFillAccountReceivables(List<CoreBankAccountDetail> accounts)   {
		logger.debug("Entering ");
		this.accountReceivableList.getItems().clear();
		for (CoreBankAccountDetail accountDetail : accounts) {
			Listitem listitem = new Listitem();
			Listcell listcell ;
			listcell = new Listcell(PennantApplicationUtil.formatAccountNumber(accountDetail.getAccountNumber()));
			listcell.setParent(listitem);
			listcell = new Listcell(StringUtils.trimToEmpty(accountDetail.getAcType()));
			listcell.setParent(listitem);
			listcell = new Listcell(PennantAppUtil.amountFormate(accountDetail.getAcBal(), ccyFormat));
			listcell.setParent(listitem);
			listcell = new Listcell();
			Button button=new Button(Labels.getLabel("label_FinTypeAccountDialog_AccReceivableList_btnDelete.value"));
			button.setVisible(!isReadOnly("FinTypeAccountDialog_accountReceivable"));
			Object object[]=new Object[2];
			object[0]=listitem;
			object[1]=accountDetail.getAccountNumber();
			button.addForward("onClick", this.window_FinTypeAccountDialog, "onClickDeleteAccount",object);
			button.setParent(listcell);
			listcell.setParent(listitem);
			accountReceivableList.appendChild(listitem);
			logger.debug("Leaving ");
		}
		 doCheckRecords();
	}

	/**
	 * This method is to delete account added to the list . <br>
	 *  accountReceivableList
	 * 
	 */
	public void onClickDeleteAccount(ForwardEvent event){
		logger.debug(event.toString());
		Object object[]=(Object[]) event.getData();
		Listitem listitem=(Listitem) object[0];
		String accNo=(String) object[1];
		removeAccount(accounts,accNo);
		this.accountReceivableList.removeChild(listitem);
		 doCheckRecords();
		logger.debug("Leaving ");
	}

	private void removeAccount(List<CoreBankAccountDetail> accounts, String accNo) {
		for (Iterator<CoreBankAccountDetail> iterator = accounts.iterator(); iterator.hasNext();) {
			if (iterator.next().getAccountNumber().equals(accNo)) {
				iterator.remove();
			}
		}
	}

	private boolean isAccAlreadyInList(String accno) {
		for (CoreBankAccountDetail detail : accounts) {
			if (detail.getAccountNumber().equals(accno)) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * This method is to save account number details . <br>
	 * 
	 */
	public String getAccountNumberDetails(List<CoreBankAccountDetail> accounts){
		logger.debug("Entering ");
		StringBuilder totalAccounts=new StringBuilder("");
		for (CoreBankAccountDetail accountDetail : accounts) {
			if (totalAccounts.toString().equals("")) {
				totalAccounts.append(accountDetail.getAccountNumber());
			}else{
				totalAccounts.append(",");
				totalAccounts.append(accountDetail.getAccountNumber());
			}
		}
		logger.debug("Leaving ");
		return totalAccounts.toString();
	}

	/**
	 * This method is to set account number details . <br>
	 *  
	 */
	public void setAccountNumberDetails(String  custAccounts){
		logger.debug("Entering ");
		try {
			List<CoreBankAccountDetail> accountDetails=new ArrayList<CoreBankAccountDetail>();
			if (!StringUtils.trimToEmpty(custAccounts).equals("")) {
				String[] accounts = custAccounts.split(",");
				for (String accountType : accounts) {
					CoreBankAccountDetail accountDetail = new CoreBankAccountDetail();
					accountDetail.setAccountNumber(accountType);
					accountDetails.add(accountDetail);
				}
				setAccounts(validateAccountListInEquation(accountDetails));
				doFillAccountReceivables(getAccounts());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving ");
	}

	private List<CoreBankAccountDetail> validateAccountListInEquation(List<CoreBankAccountDetail> accountDetails) {
		try {
			return getAccountInterfaceService().checkAccountID(accountDetails);
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}

	/**
	 * This method is to set Account Types  details . <br>
	 *  
	 * 
	 */
	public void setAccountTypesDetails(String  custAccountTypes){
		logger.debug("Entering ");
		if(!StringUtils.trimToEmpty(custAccountTypes).equals("")){
			String[] accountTypes = custAccountTypes.split(",");
			for(String accountType : accountTypes){
				this.accounTypesDataMap.put(accountType, new AccountType());
			}
		}
		logger.debug("Leaving ");
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

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(FinTypeAccount aFinTypeAccount, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinTypeAccount.getBefImage(), aFinTypeAccount);
		return new AuditHeader(aFinTypeAccount.getFinType(), null, null, null, auditDetail, aFinTypeAccount.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinTypeAccountDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("FinTypeAccount");
		notes.setReference(getFinTypeAccount().getFinCcy());
		notes.setVersion(getFinTypeAccount().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.finCcy.setErrorMessage("");
		this.finEvent.setErrorMessage("");
		this.accountReceivable.setErrorMessage("");
		this.custAccountTypes.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doCheckRecords(){
		if (getAccounts().size()>0) {
			this.finCcy.setReadonly(true); 
		}else{
			if (getFinTypeAccount().isNewRecord()) {
				this.finCcy.setReadonly(false); 
			}
		}
	}
	
	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public FinTypeAccountDialogCtrl getFinTypeAccountDialogCtrl() {
		return finTypeAccountDialogCtrl;
	}

	public void setFinTypeAccountDialogCtrl(
			FinTypeAccountDialogCtrl finTypeAccountDialogCtrl) {
		this.finTypeAccountDialogCtrl = finTypeAccountDialogCtrl;
	}

	public FinanceTypeDialogCtrl getFinanceTypeDialogCtrl() {
		return financeTypeDialogCtrl;
	}

	public void setFinanceTypeDialogCtrl(FinanceTypeDialogCtrl financeTypeDialogCtrl) {
		this.financeTypeDialogCtrl = financeTypeDialogCtrl;
	}

	public List<CoreBankAccountDetail> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<CoreBankAccountDetail> accounts) {
		this.accounts = accounts;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}
}
