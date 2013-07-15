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
 * FileName    		:  AccountTypeDialogCtrl.java                                                   * 	  
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
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.AccountTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/AccountType/accountTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AccountTypeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8382447556859137171L;
	private final static Logger logger = Logger
			.getLogger(AccountTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_AccountTypeDialog; // autoWired

	protected Textbox acType; // autoWired
	protected Textbox acTypeDesc; // autoWired
	protected Combobox acPurpose; // autoWired
	protected Textbox acHeadCode; // autoWired
	protected Checkbox internalAc; // autoWired
	protected Checkbox custSysAc; // autoWired
	protected Checkbox acTypeIsActive; // autoWired
	protected Space space_acHeadCode; // autoWired
	protected Label recordStatus; // autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	
	protected Row row_headcode;

	// not autoWired Var's
	private AccountType accountType; // overHanded per parameter
	private transient AccountTypeListCtrl accountTypeListCtrl; // overHanded per
																// parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String oldVar_acType;
	private transient String oldVar_acTypeDesc;
	private transient String oldVar_acPurpose;
	private transient String oldVar_acHeadCode;
	private transient boolean oldVar_internalAc;
	private transient boolean oldVar_custSysAc;
	private transient boolean oldVar_acTypeIsActive;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_AccountTypeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autoWired
	protected Button btnEdit; // autoWired
	protected Button btnDelete; // autoWired
	protected Button btnSave; // autoWired
	protected Button btnCancel; // autoWired
	protected Button btnClose; // autoWired
	protected Button btnHelp; // autoWired
	protected Button btnNotes; // autoWired
	protected Button btnCopyTo;
	private long custAccHeadMin;
	private long custAccHeadMax;
	private long custSysAccHeadMin;
	private long custSysAccHeadMax;
	private long internalAccHeadMin;
	private long internalAccHeadMax;

	// ServiceDAOs / Domain Classes
	private transient AccountTypeService accountTypeService;
	private transient PagedListService pagedListService;

	private List<ValueLabel> listAcPurpose = PennantAppUtil.getAccountPurpose(); // autoWired

	String CBI_Available = (String) SystemParameterDetails
			.getSystemParameterValue("CBI_AVAIL");

	/**
	 * default constructor.<br>
	 */
	public AccountTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("accountType")) {
			this.accountType = (AccountType) args.get("accountType");
			AccountType befImage = new AccountType();
			BeanUtils.copyProperties(this.accountType, befImage);
			this.accountType.setBefImage(befImage);

			setAccountType(this.accountType);
		} else {
			setAccountType(null);
		}

		doLoadWorkFlow(this.accountType.isWorkflow(),
				this.accountType.getWorkflowId(),
				this.accountType.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),
					"AccountTypeDialog");
		}

		setListAcPurpose();

		// READ OVERHANDED parameters !
		// we get the accountTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete accountType here.
		if (args.containsKey("accountTypeListCtrl")) {
			setAccountTypeListCtrl((AccountTypeListCtrl) args
					.get("accountTypeListCtrl"));
		} else {
			setAccountTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getAccountType());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		int acTypeLen = Integer.parseInt(SystemParameterDetails
				.getSystemParameterValue("ACCOUNT_TYPE_LEN").toString());
		this.acType.setMaxlength(acTypeLen);
		this.acTypeDesc.setMaxlength(50);

		this.acHeadCode.setMaxlength(4);

		if (CBI_Available.equals("Y")==true) {
			this.acHeadCode.setValue("0000");
			this.row_headcode.setVisible(false);
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
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("AccountTypeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_AccountTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_AccountTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_AccountTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_AccountTypeDialog_btnSave"));
		this.btnCancel.setVisible(false);

		this.btnCopyTo.setVisible(getUserWorkspace().isAllowed(
				"button_AccountTypeDialog_btnCopyTo"));
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
	public void onClose$window_AccountTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose(null);
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
		// remember the old Var's
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
		PTMessageUtils.showHelpWindow(event, window_AccountTypeDialog);
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
		logger.debug("Enetring" + event.toString());
		try {
			doClose(null);
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Creating Duplicate record
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		String message = "Change done to the existing definition. Do you want to save before copy?";
		doClose(message);
		Events.postEvent("onClick$button_AccountTypeList_NewAccountType",
				accountTypeListCtrl.window_AccountTypeList, getAccountType());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose(String msg) throws InterruptedException {
		logger.debug("Entering");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			if (msg == null) {
				msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			}
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}

		if (close) {
			closeDialog(this.window_AccountTypeDialog, "AccountType");
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
	 * @param aAccountType
	 *            AccountType
	 */
	public void doWriteBeanToComponents(AccountType aAccountType) {
		logger.debug("Entering");
		this.acType.setValue(aAccountType.getAcType());
		this.acTypeDesc.setValue(aAccountType.getAcTypeDesc());
		this.acPurpose.setValue(PennantAppUtil.getlabelDesc(
				aAccountType.getAcPurpose(), listAcPurpose));
		this.acHeadCode.setText(aAccountType.getAcHeadCode() == null ? ""
				: StringUtils.leftPad(
						String.valueOf(aAccountType.getAcHeadCode()), 4, '0'));

		this.internalAc.setChecked(aAccountType.isInternalAc());
		this.custSysAc.setChecked(aAccountType.isCustSysAc());
		this.acTypeIsActive.setChecked(aAccountType.isAcTypeIsActive());

		this.recordStatus.setValue(aAccountType.getRecordStatus());
		if (aAccountType.isNew()
				|| aAccountType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) {
			this.acTypeIsActive.setChecked(true);
			this.acTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccountType
	 */
	public void doWriteComponentsToBean(AccountType aAccountType) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAccountType.setAcType(this.acType.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAccountType.setAcTypeDesc(this.acTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAccountType.setAcPurpose(this.acPurpose.getSelectedItem()
					.getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAccountType.setAcHeadCode(StringUtils.leftPad(
					String.valueOf(this.acHeadCode.getText()), 4, '0'));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAccountType.setInternalAc(this.internalAc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAccountType.setCustSysAc(this.custSysAc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAccountType.setAcTypeIsActive(this.acTypeIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (CBI_Available.equals("Y")!=true) {
			try {
				if ((!this.custSysAc.isChecked() && !this.internalAc
						.isChecked())) {
					if ((Long.valueOf(this.acHeadCode.getValue()) < custAccHeadMin)
							|| (Long.valueOf(this.acHeadCode.getValue()) > custAccHeadMax)) {

						throw new WrongValueException(
								this.acHeadCode,
								Labels.getLabel(
										"FIELD_RANGE_FOR",
										new String[] {
												Labels.getLabel("label_AccountTypeDialog_AcHeadCode.value"),
												String.valueOf(custAccHeadMin),
												String.valueOf(custAccHeadMax),
												Labels.getLabel("label_AccountTypeDialog_CustAccount.value") }));
					}
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.custSysAc.isChecked() && !this.internalAc.isChecked()) {
					if ((Long.valueOf(this.acHeadCode.getValue()) < custSysAccHeadMin)
							|| (Long.valueOf(this.acHeadCode.getValue()) > custSysAccHeadMax)) {

						throw new WrongValueException(
								this.acHeadCode,
								Labels.getLabel(
										"FIELD_RANGE_FOR",
										new String[] {
												Labels.getLabel("label_AccountTypeDialog_AcHeadCode.value"),
												String.valueOf(custSysAccHeadMin),
												String.valueOf(custSysAccHeadMax),
												Labels.getLabel("label_AccountTypeDialog_CustSysAccount.value") }));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (!this.custSysAc.isChecked() && this.internalAc.isChecked()) {
					if ((Long.valueOf(this.acHeadCode.getValue()) < internalAccHeadMin)
							|| (Long.valueOf(this.acHeadCode.getValue()) > internalAccHeadMax)) {

						throw new WrongValueException(
								this.acHeadCode,
								Labels.getLabel(
										"FIELD_RANGE_FOR",
										new String[] {
												Labels.getLabel("label_AccountTypeDialog_AcHeadCode.value"),
												String.valueOf(internalAccHeadMin),
												String.valueOf(internalAccHeadMax),
												Labels.getLabel("label_AccountTypeDialog_IsInternalAc.value") }));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
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

		aAccountType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAccountType
	 * @throws InterruptedException
	 */
	public void doShowDialog(AccountType aAccountType)
			throws InterruptedException {
		logger.debug("Entering");
		// if aAccountType == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aAccountType == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aAccountType = getAccountTypeService().getNewAccountType();

			setAccountType(aAccountType);
		} else {
			setAccountType(aAccountType);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aAccountType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.acType.focus();
		} else {
			this.acTypeDesc.focus();
			if (isWorkFlowEnabled()) {
				if (!StringUtils.trimToEmpty(aAccountType.getRecordType())
						.equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aAccountType);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			doLoadAccountHead();
			setDialog(this.window_AccountTypeDialog);
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
	 * Stores the initialize values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_acType = this.acType.getValue();
		this.oldVar_acTypeDesc = this.acTypeDesc.getValue();
		this.oldVar_acPurpose = this.acPurpose.getValue();
		this.oldVar_acHeadCode = this.acHeadCode.getValue();
		this.oldVar_internalAc = this.internalAc.isChecked();
		this.oldVar_custSysAc = this.custSysAc.isChecked();
		this.oldVar_acTypeIsActive = this.acTypeIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.acType.setValue(this.oldVar_acType);
		this.acTypeDesc.setValue(this.oldVar_acTypeDesc);
		this.acPurpose.setValue(this.oldVar_acPurpose);
		this.acHeadCode.setValue(this.oldVar_acHeadCode);
		this.internalAc.setChecked(this.oldVar_internalAc);
		this.custSysAc.setChecked(this.oldVar_custSysAc);
		this.acTypeIsActive.setChecked(this.oldVar_acTypeIsActive);
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
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_acType != this.acType.getValue()) {
			return true;
		}
		if (this.oldVar_acTypeDesc != this.acTypeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_acPurpose != this.acPurpose.getValue()) {
			return true;
		}
		if (this.oldVar_acHeadCode != this.acHeadCode.getValue()) {
			return true;
		}
		if (this.oldVar_internalAc != this.internalAc.isChecked()) {
			return true;
		}
		if (this.oldVar_custSysAc != this.custSysAc.isChecked()) {
			return true;
		}
		if (this.oldVar_acTypeIsActive != this.acTypeIsActive.isChecked()) {
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
		doClearMessage();
		if (!this.acType.isReadonly()) {
			this.acType.setConstraint(new SimpleConstraint(
						PennantConstants.ALPHANUM_CAPS_REGEX,Labels.getLabel("FIELD_CHAR_CAPS",
									new String[] { Labels.getLabel("label_AccountTypeDialog_AcType.value") })));
		}
		if (!this.acTypeDesc.isReadonly()) {
			this.acTypeDesc.setConstraint(new SimpleConstraint(
							PennantConstants.DESC_REGEX,Labels.getLabel("MAND_FIELD_DESC",
									new String[] { Labels.getLabel("label_AccountTypeDialog_AcTypeDesc.value") })));
		}
		if (!this.acPurpose.isDisabled()) {
			this.acPurpose.setConstraint(new StaticListValidator(listAcPurpose,
							Labels.getLabel("label_AccountTypeDialog_AcPurpose.value")));
		}
		if (!this.acHeadCode.isReadonly() && CBI_Available.equals("Y") !=true) {
			this.acHeadCode.setConstraint(new SimpleConstraint(
							PennantConstants.NUM_REGEX,Labels.getLabel("MAND_FIELD_ALLOWED_CHARS",
									new String[] {Labels.getLabel("label_AccountTypeDialog_AcHeadCode.value"),
											"Numbers(0-9)", "'4'" })));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.acType.setConstraint("");
		this.acTypeDesc.setConstraint("");
		this.acPurpose.setConstraint("");
		this.acHeadCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.acType.setErrorMessage("");
		this.acTypeDesc.setErrorMessage("");
		this.acPurpose.setErrorMessage("");
		this.acHeadCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<AccountType> soObject = getAccountTypeListCtrl()
				.getSearchObj();
		getAccountTypeListCtrl().pagingAccountTypeList.setActivePage(0);
		getAccountTypeListCtrl().getPagedListWrapper()
				.setSearchObject(soObject);
		if (getAccountTypeListCtrl().listBoxAccountType != null) {
			getAccountTypeListCtrl().listBoxAccountType.getListModel();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a AccountType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final AccountType aAccountType = new AccountType();
		BeanUtils.copyProperties(getAccountType(), aAccountType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aAccountType.getAcType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aAccountType.getRecordType())
					.equals("")) {
				aAccountType.setVersion(aAccountType.getVersion() + 1);
				aAccountType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAccountType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAccountType, tranType)) {
					refreshList();
					closeDialog(this.window_AccountTypeDialog, "AccountType");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new AccountType object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new AccountType() in the front end.
		// we get it from the back end.
		final AccountType aAccountType = getAccountTypeService()
				.getNewAccountType();
		aAccountType.setNewRecord(true);
		setAccountType(aAccountType);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.acType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getAccountType().isNewRecord()) {
			this.acType.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.btnCopyTo.setVisible(false);
		} else {
			this.acType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.acTypeDesc.setReadonly(isReadOnly("AccountTypeDialog_acTypeDesc"));
		this.acPurpose.setDisabled(isReadOnly("AccountTypeDialog_acPurpose"));
		this.acHeadCode.setReadonly(isReadOnly("AccountTypeDialog_acHeadCode"));
		this.internalAc
				.setDisabled(isReadOnly("AccountTypeDialog_isInternalAc"));
		this.custSysAc.setDisabled(isReadOnly("AccountTypeDialog_isCustSysAc"));
		this.acTypeIsActive
				.setDisabled(isReadOnly("AccountTypeDialog_acTypeIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.accountType.isNewRecord()) {
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
		this.acType.setReadonly(true);
		this.acTypeDesc.setReadonly(true);
		this.acPurpose.setDisabled(true);
		this.acHeadCode.setReadonly(true);
		this.internalAc.setDisabled(true);
		this.custSysAc.setDisabled(true);
		this.acTypeIsActive.setDisabled(true);

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
		this.acType.setValue("");
		this.acTypeDesc.setValue("");
		this.acPurpose.setValue("");
		this.acHeadCode.setText("");
		this.internalAc.setChecked(false);
		this.custSysAc.setChecked(false);
		this.acTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final AccountType aAccountType = new AccountType();
		BeanUtils.copyProperties(getAccountType(), aAccountType);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the AccountType object with the components data
		doWriteComponentsToBean(aAccountType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aAccountType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aAccountType.getRecordType())
					.equals("")) {
				aAccountType.setVersion(aAccountType.getVersion() + 1);
				if (isNew) {
					aAccountType
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAccountType
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAccountType.setNewRecord(true);
				}
			}
		} else {
			aAccountType.setVersion(aAccountType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aAccountType, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_AccountTypeDialog, "AccountType");
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
	 * @param aAccountType
	 *            (AccountType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(AccountType aAccountType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAccountType.setLastMntBy(getUserWorkspace().getLoginUserDetails()
				.getLoginUsrID());
		aAccountType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountType.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aAccountType.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountType
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,
							aAccountType);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aAccountType))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
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

			aAccountType.setTaskId(taskId);
			aAccountType.setNextTaskId(nextTaskId);
			aAccountType.setRoleCode(getRole());
			aAccountType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountType, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,
					aAccountType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAccountType,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAccountType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
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
		AccountType aAccountType = (AccountType) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getAccountTypeService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getAccountTypeService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getAccountTypeService().doApprove(
								auditHeader);

						if (aAccountType.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getAccountTypeService().doReject(
								auditHeader);
						if (aAccountType.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_AccountTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_AccountTypeDialog, auditHeader);

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
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for Preparing AccountType Purpose List
	 */
	private void setListAcPurpose() {
		logger.debug("Entering");
		for (int i = 0; i < listAcPurpose.size(); i++) {
			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listAcPurpose.get(i).getLabel());
			comboitem.setValue(listAcPurpose.get(i).getValue());
			this.acPurpose.appendChild(comboitem);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to be called after checking customer system account checkBox
	 */
	public void onCheck$custSysAc(Event event) {
		logger.debug("Entering");
		customerSystemAcCheck();
		logger.debug("Leaving");
	}

	/**
	 * Check Whether Customer System Account is checked or not
	 * 
	 */
	public void customerSystemAcCheck() {
		logger.debug("Entering");
		if (this.custSysAc.isChecked()) {
			this.internalAc.setChecked(false);
			this.internalAc.setDisabled(true);

		} else {
			this.internalAc.setDisabled(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to be called after checking internal account checkBox
	 */
	public void onCheck$internalAc(Event event) {
		logger.debug("Entering");
		internalAcCheck();
		logger.debug("Leaving");
	}

	/**
	 * Check Whether Internal Account is checked or not
	 * 
	 */
	public void internalAcCheck() {
		logger.debug("Entering");
		if (this.internalAc.isChecked()) {
			this.custSysAc.setChecked(false);
			this.custSysAc.setDisabled(true);

		} else {
			this.custSysAc.setDisabled(false);
		}

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAccountType
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(AccountType aAccountType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aAccountType.getBefImage(), aAccountType);
		return new AuditHeader(String.valueOf(aAccountType.getId()), null,
				null, null, auditDetail, aAccountType.getUserDetails(),
				getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_AccountTypeDialog,
					auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("AccountType");
		notes.setReference(getAccountType().getAcType());
		notes.setVersion(getAccountType().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	/**
	 * 
	 */
	private void doLoadAccountHead() {
		logger.debug("Entering ");
		String accountHeads = (String) SystemParameterDetails
				.getSystemParameterValue("ACCOUNT_HEAD");
		String[] tempAccountHead = accountHeads.split(",");

		String[] custAccHeads = tempAccountHead[0].split("-");
		custAccHeadMin = Long.valueOf(custAccHeads[0]);
		custAccHeadMax = Long.valueOf(custAccHeads[1]);

		String[] custSysHeads = tempAccountHead[1].split("-");
		custSysAccHeadMin = Long.valueOf(custSysHeads[0]);
		custSysAccHeadMax = Long.valueOf(custSysHeads[1]);

		String[] internalAccHeads = tempAccountHead[2].split("-");
		internalAccHeadMin = Long.valueOf(internalAccHeads[0]);
		internalAccHeadMax = Long.valueOf(internalAccHeads[1]);
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

	public AccountType getAccountType() {
		return this.accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public void setAccountTypeService(AccountTypeService accountTypeService) {
		this.accountTypeService = accountTypeService;
	}

	public AccountTypeService getAccountTypeService() {
		return this.accountTypeService;
	}

	public void setAccountTypeListCtrl(AccountTypeListCtrl accountTypeListCtrl) {
		this.accountTypeListCtrl = accountTypeListCtrl;
	}

	public AccountTypeListCtrl getAccountTypeListCtrl() {
		return this.accountTypeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

}
