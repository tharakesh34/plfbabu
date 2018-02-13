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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountDialog.zul file.
 */
public class FinTypeAccountDialogCtrl extends GFCBaseCtrl<FinTypeAccount> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FinTypeAccountDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinTypeAccountDialog; // autowired

	protected ExtendedCombobox finCcy; // autowired
	protected Combobox finEvent; // autowired
	protected Checkbox alwManualEntry; // autowired
	protected Checkbox alwCustAccount; // autowired
	protected Textbox accountReceivable; // autowired
	protected Textbox custAccountTypes; // autowired
	protected Button btnSearchAccountTypes; // autowired
	// not auto wired vars
	private FinTypeAccount finTypeAccount; // overhanded per param
	private transient FinTypeAccountDialogCtrl finTypeAccountDialogCtrl; // overhanded per

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient AccountInterfaceService accountInterfaceService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();

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

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypeAccountDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected finTypeAccount object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinTypeAccountDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypeAccountDialog);

		try {
			if (arguments.containsKey("finTypeAccount")) {
				this.finTypeAccount = (FinTypeAccount) arguments.get("finTypeAccount");
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
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FinTypeAccountDialog");
			}
			if (arguments.containsKey("role")) {
				userRole=arguments.get("role").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "FinTypeAccountDialog");
			}
			doCheckRights();
			// READ OVERHANDED params !
			// we get the finTypeAccountlListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete FinTypeAccount here.
			if (arguments.containsKey("financeTypeDialogCtrl")) {
				setFinanceTypeDialogCtrl((FinanceTypeDialogCtrl) arguments.get("financeTypeDialogCtrl"));
			} else {
				setFinanceTypeDialogCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinTypeAccount());
		} catch (Exception e) {
			MessageUtil.showError(e);
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

		this.finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
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
		getUserWorkspace().allocateAuthorities("FinTypeAccountDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypeAccountDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
		MessageUtil.showHelpWindow(event, window_FinTypeAccountDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.finTypeAccount.getBefImage());
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
		
		this.ccyFormat = CurrencyUtil.getFormat(aFinTypeAccount.getFinCcy());
		setAccountNumberDetails(aFinTypeAccount.getAccountReceivable(), aFinTypeAccount.getDefaultAccNum());
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
			aFinTypeAccount.setFinCcy(this.finCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.finEvent))) {
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
		try {
			if(StringUtils.isNotEmpty(this.accountReceivable.getValue())){
				throw new WrongValueException(this.accountReceivable, Labels.getLabel("const_EMPTY",
						new String[] { Labels.getLabel("label_FinTypeAccountDialog_AccountReceivable.value") }));
			}
			aFinTypeAccount.setCustAccountTypes(this.custAccountTypes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			boolean defaultAccNum = false;
			String defaultAccount = null;
			for (int i = 0; i < accountReceivableList.getItems().size(); i++) {
				Listitem listitem = accountReceivableList.getItems().get(i);
				List<Component> componentList = ((Listcell) listitem.getLastChild().getPreviousSibling()).getChildren();
				if (componentList != null && componentList.size() > 0) {
					Component component = componentList.get(0);
					if (component instanceof Checkbox) {
						if (((Checkbox) component).isChecked()) {
							if(!defaultAccNum) {
								Listcell listCell =(Listcell) listitem.getChildren().get(0);
								defaultAccount = String.valueOf(listCell.getLabel());
								defaultAccNum = true;
							} else {
								throw new WrongValueException(this.accountReceivableList,Labels.getLabel("DEFAULT_ACCOUNT"));
							}
						} else {
							aFinTypeAccount.setDefaultAccNum("");
						}
					}
					aFinTypeAccount.setDefaultAccNum(defaultAccount);
				}
			}
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
			
			this.window_FinTypeAccountDialog.doModal();
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
		if (!this.finCcy.isReadonly()) {
			this.finCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_FinTypeAccountDialog_FinCcy.value"),null,true,true));
		}
		if (!this.finEvent.isReadonly()) {
			this.finEvent.setConstraint(new PTStringValidator(Labels.getLabel("label_FinTypeAccountDialog_Event.value"),null,true));
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

	// CRUD operations
	
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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_FinTypeAccountDialog_FinCcy.value")+" : "+aFinTypeAccount.getFinCcy()+","+
				Labels.getLabel("label_FinTypeAccountDialog_Event.value")+" : "+ 
				PennantAppUtil.getlabelDesc(aFinTypeAccount.getEvent(), eventList);
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinTypeAccount.getRecordType())) {
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
					closeDialog();
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
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinTypeAccount object with the components data
		doWriteComponentsToBean(aFinTypeAccount);
		if(!this.alwManualEntry.isChecked() && StringUtils.isEmpty(getAccountNumberDetails(getAccounts())) && StringUtils.isEmpty(this.custAccountTypes.getValue())){
			MessageUtil.showError("Please Enter Either Of The Fields   :  "
					+ Labels.getLabel("label_FinTypeAccountDialog_AlwManualEntry.value") + " , "
					+ Labels.getLabel("label_FinTypeAccountDialog_CustAccountTypes.value") + " , "
					+ Labels.getLabel("label_FinTypeAccountDialog_AccountReceivable.value"));
        	return;
        }
		// Write the additional validations as per below example
		// get the selected FinTypeAccount object from the listbox
		// Do data level validations here
		isNew = aFinTypeAccount.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinTypeAccount.getRecordType())) {
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
			if (StringUtils.isBlank(aFinTypeAccount.getRecordType())) {
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
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
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
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aFinTypeAccount.getRecordType())) {
							aFinTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finTypeAccountList.add(aFinTypeAccount);
						} else if (PennantConstants.RCD_ADD.equals(aFinTypeAccount.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aFinTypeAccount.getRecordType())) {
							aFinTypeAccount.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finTypeAccountList.add(aFinTypeAccount);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aFinTypeAccount.getRecordType())) {
							recordAdded = true;
							List<FinTypeAccount> savedList = getFinanceTypeDialogCtrl().getFinanceType().getFinTypeAccounts();
							for (int j = 0; j < savedList.size(); j++) {
								FinTypeAccount accType = savedList.get(j);
								if (accType.getFinType().equals(aFinTypeAccount.getFinType())) {
									finTypeAccountList.add(accType);
								}
							}
						} else if (PennantConstants.RECORD_TYPE_DEL.equals(aFinTypeAccount.getRecordType())) {
							aFinTypeAccount.setNewRecord(true);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
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
			if(StringUtils.isNotBlank(toolTipDesc) && toolTipDesc.endsWith(", ")){
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
		/*logger.debug("Entering ");
		if(alwManualEntry.isChecked()){
			alwCustAccount.setDisabled(isReadOnly("FinTypeAccountDialog_alwCustAccount"));
		}else{
			alwCustAccount.setDisabled(true);
			alwCustAccount.setChecked(false);
		}
		logger.debug("Leaving ");*/
	}
	public void onFulfill$finCcy(Event event){
		Object obj=this.finCcy.getObject();
		if (obj instanceof Currency) {
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
		if (StringUtils.isBlank(ccy)) {
			valid = false;
			throw new WrongValueException(this.finCcy, Labels.getLabel("FIELD_IS_MAND",
					new String[] {Labels.getLabel("label_FinTypeAccountDialog_FinCcy.value") }));
		}
		String accno = this.accountReceivable.getValue();
		if (StringUtils.isBlank(accno)) {
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
				MessageUtil.showError(Labels.getLabel("ACCOUNT_INVALID", new String[] { accno }));
			} else {
				if (!accountDetail.getAcCcy().equals(ccy)) {
					MessageUtil
							.showError(Labels.getLabel("ACCOUNT_CCY_MISMATCH", new String[] { accountDetail.getAcCcy(),
									Labels.getLabel("label_FinTypeAccountDialog_FinCcy.value"), ccy }));
					return;
				}
				if (!isAccAlreadyInList(accno)) {
					accounts.add(accountDetail);
				}else{
					MessageUtil.showError(Labels.getLabel("ACCOUNT_EXISTS", new String[] { accno }));
				}
				doFillAccountReceivables(accounts, getFinTypeAccount().getDefaultAccNum());
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

	private void doFillAccountReceivables(List<CoreBankAccountDetail> accounts, String defaultAccNum)   {
		logger.debug("Entering ");
		this.accountReceivable.setValue("");
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
			Checkbox defaultCheckbox = new Checkbox();
			defaultCheckbox.setDisabled(isReadOnly("FinTypeAccountDialog_accountReceivable"));
			defaultCheckbox.setParent(listcell);
			if(StringUtils.equals(defaultAccNum, accountDetail.getAccountNumber())) {
				defaultCheckbox.setChecked(true);
			}
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
		if(!StringUtils.isBlank(getFinTypeAccount().getDefaultAccNum())) {
			getFinTypeAccount().setDefaultAccNum("");
		}
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
		StringBuilder totalAccounts=new StringBuilder();
		if(accounts != null){
			for (CoreBankAccountDetail accountDetail : accounts) {
				if (StringUtils.isEmpty(totalAccounts.toString())) {
					totalAccounts.append(accountDetail.getAccountNumber());
				}else{
					totalAccounts.append(",");
					totalAccounts.append(accountDetail.getAccountNumber());
				}
			}
		}
		logger.debug("Leaving ");
		return totalAccounts.toString();
	}

	/**
	 * This method is to set account number details . <br>
	 *  
	 */
	public void setAccountNumberDetails(String custAccounts, String defaultAccNum){
		logger.debug("Entering ");
		try {
			List<CoreBankAccountDetail> accountDetails=new ArrayList<CoreBankAccountDetail>();
			if (StringUtils.isNotBlank(custAccounts)) {
				String[] accounts = custAccounts.split(",");
				for (String accountType : accounts) {
					CoreBankAccountDetail accountDetail = new CoreBankAccountDetail();
					accountDetail.setAccountNumber(accountType);
					accountDetails.add(accountDetail);
				}
				setAccounts(validateAccountListInEquation(accountDetails));
				doFillAccountReceivables(getAccounts(), defaultAccNum);
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
		if(StringUtils.isNotBlank(custAccountTypes)){
			String[] accountTypes = custAccountTypes.split(",");
			for(String accountType : accountTypes){
				this.accounTypesDataMap.put(accountType, new AccountType());
			}
		}
		logger.debug("Leaving ");
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

	private AuditHeader getAuditHeader(FinTypeAccount aFinTypeAccount, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinTypeAccount.getBefImage(), aFinTypeAccount);
		return new AuditHeader(aFinTypeAccount.getFinType(), null, null, null, auditDetail, aFinTypeAccount.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinTypeAccountDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.finTypeAccount);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.finTypeAccount.getFinCcy());
	}

	@Override
	protected void doClearMessage() {
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
	
	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
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
