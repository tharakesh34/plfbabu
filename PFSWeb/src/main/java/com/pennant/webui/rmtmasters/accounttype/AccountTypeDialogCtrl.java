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

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.model.applicationmaster.CostCenter;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.rmtmasters.AccountTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/AccountType/accountTypeDialog.zul file.
 */
public class AccountTypeDialogCtrl extends GFCBaseCtrl<AccountType> {
	private static final long serialVersionUID = 8382447556859137171L;
	private final static Logger logger = Logger
			.getLogger(AccountTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AccountTypeDialog; // autoWired

	protected Textbox acType; // autoWired
	protected Textbox acTypeDesc; // autoWired
	protected Textbox acLmtCategory; // autoWired
	protected Combobox acPurpose; // autoWired
	protected Textbox acHeadCode; // autoWired
	protected Checkbox internalAc; // autoWired
	protected Checkbox custSysAc; // autoWired
	protected Combobox assertOrLiability; // autoWired
	protected Checkbox onBalanceSheet; // autoWired
	protected Checkbox allowOverDraw; // autoWired
	protected Checkbox acTypeIsActive; // autoWired
	protected Space space_acHeadCode; // autoWired
	protected ExtendedCombobox	acTypeGrpId;
	protected ExtendedCombobox	profitCenter;
	protected ExtendedCombobox	costCenter;
	
	protected Row row_headcode;

	// not autoWired Var's
	private AccountType accountType; // overHanded per parameter
	private transient AccountTypeListCtrl accountTypeListCtrl; // overHanded per
																// parameter
	private transient boolean validationOn;
	
	protected Button btnCopyTo;
	private long custAccHeadMin;
	private long custAccHeadMax;
	private long custSysAccHeadMin;
	private long custSysAccHeadMax;
	private long internalAccHeadMin;
	private long internalAccHeadMax;

	// ServiceDAOs / Domain Classes
	private transient AccountTypeService accountTypeService;


	String CBI_Available = SysParamUtil.getValueAsString("CBI_AVAIL");

	/**
	 * default constructor.<br>
	 */
	public AccountTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AccountTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AccountTypeDialog);

		try {
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("accountType")) {
				this.accountType = (AccountType) arguments.get("accountType");
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
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"AccountTypeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the accountTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete accountType here.
			if (arguments.containsKey("accountTypeListCtrl")) {
				setAccountTypeListCtrl((AccountTypeListCtrl) arguments
						.get("accountTypeListCtrl"));
			} else {
				setAccountTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getAccountType());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_AccountTypeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.acType.setMaxlength(14);
		this.acTypeDesc.setMaxlength(50);
		this.acLmtCategory.setMaxlength(100);

		this.acHeadCode.setMaxlength(4);
		
		this.acTypeGrpId.setWidth("200px");
		this.acTypeGrpId.setModuleName("AccountTypeGroup");
		this.acTypeGrpId.setMandatoryStyle(true);
		this.acTypeGrpId.setValueColumn("GroupCode");
		this.acTypeGrpId.setDescColumn("GroupDescription");
		this.acTypeGrpId.setDisplayStyle(2);
		this.acTypeGrpId.setValidateColumns(new String[] {"GroupCode" });
		
		this.profitCenter.setModuleName("ProfitCenter");
		this.profitCenter.setMandatoryStyle(true);
		this.profitCenter.setValueColumn("ProfitCenterCode");
		this.profitCenter.setDescColumn("ProfitCenterDesc");
		this.profitCenter.setDisplayStyle(2);
		this.profitCenter.setValidateColumns(new String[] {"ProfitCenterCode" });
		
		this.costCenter.setModuleName("CostCenter");
		this.costCenter.setMandatoryStyle(false);
		this.costCenter.setValueColumn("CostCenterCode");
		this.costCenter.setDescColumn("CostCenterDesc");
		this.costCenter.setDisplayStyle(2);
		this.costCenter.setValidateColumns(new String[] {"CostCenterCode" });
		this.costCenter.setMandatoryStyle(true);
		
		if ("Y".equals(CBI_Available)) {
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
		MessageUtil.showHelpWindow(event, window_AccountTypeDialog);
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
		
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Method for Creating Duplicate record
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug("Entering");
		if (doClose(this.btnSave.isVisible())) {
			Events.postEvent("onClick$button_AccountTypeList_NewAccountType",
					accountTypeListCtrl.window_AccountTypeList, getAccountType());
		}
		logger.debug("Leaving");
	
	}

	// GUI operations

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
			closeDialog();
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
		doWriteBeanToComponents(this.accountType.getBefImage());
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
		this.acLmtCategory.setValue(aAccountType.getAcLmtCategory());
		fillComboBox(this.acPurpose, aAccountType.getAcPurpose(), PennantStaticListUtil.getAccountPurpose(), "");
		this.acHeadCode.setText(aAccountType.getAcHeadCode() == null ? ""
				: StringUtils.leftPad(
						String.valueOf(aAccountType.getAcHeadCode()), 4, '0'));

		this.internalAc.setChecked(aAccountType.isInternalAc());
		this.custSysAc.setChecked(aAccountType.isCustSysAc());
		this.acTypeIsActive.setChecked(aAccountType.isAcTypeIsActive());
		fillComboBox(this.assertOrLiability, aAccountType.getAssertOrLiability(), PennantStaticListUtil.getAssetOrLiability(), "");
		this.onBalanceSheet.setChecked(aAccountType.isOnBalanceSheet());
		this.allowOverDraw.setChecked(aAccountType.isAllowOverDraw());

		this.recordStatus.setValue(aAccountType.getRecordStatus());
		if (aAccountType.isNew()
				|| PennantConstants.RECORD_TYPE_NEW.equals(aAccountType
						.getRecordType())) {
			this.acTypeIsActive.setChecked(true);
			this.acTypeIsActive.setDisabled(true);
		}
			this.acTypeGrpId.setObject(new AccountTypeGroup(aAccountType.getAcTypeGrpId()));
			this.acTypeGrpId.setValue(aAccountType.getGroupCode(),aAccountType.getGroupDescription());
		
			this.profitCenter.setObject(new ProfitCenter(aAccountType.getProfitCenterID()));
			this.profitCenter.setValue(aAccountType.getProfitCenterCode(),aAccountType.getProfitCenterDesc());
			
			if(aAccountType.getCostCenterID() != null ){
				this.costCenter.setObject(new CostCenter(aAccountType.getCostCenterID()));
				this.costCenter.setValue(aAccountType.getCostCenterCode(),aAccountType.getCostCenterDesc());
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
			aAccountType.setAcLmtCategory(this.acLmtCategory.getValue());
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
		
		/*try {
			aAccountType.setAssertOrLiability(this.assertOrLiability.getSelectedItem()
					.getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}*/
		
		try {
			aAccountType.setOnBalanceSheet(this.onBalanceSheet.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aAccountType.setAllowOverDraw(this.allowOverDraw.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			this.acTypeGrpId.getValidatedValue();
			AccountTypeGroup accountTypeGroup = (AccountTypeGroup) this.acTypeGrpId.getObject();
			aAccountType.setAcTypeGrpId(accountTypeGroup.getGroupId());
			aAccountType.setGroupCode(accountTypeGroup.getGroupCode());
			aAccountType.setGroupDescription(accountTypeGroup.getGroupDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.profitCenter.getValidatedValue();
			ProfitCenter profitCenter = (ProfitCenter) this.profitCenter.getObject();
			aAccountType.setProfitCenterID(profitCenter.getProfitCenterID());
			aAccountType.setProfitCenterCode(profitCenter.getProfitCenterCode());
			aAccountType.setProfitCenterDesc(profitCenter.getProfitCenterDesc());
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			this.costCenter.getValidatedValue();
			CostCenter costCenter = (CostCenter) this.costCenter.getObject();
			if(costCenter != null){
				aAccountType.setCostCenterID(costCenter.getCostCenterID());
				aAccountType.setCostCenterCode(costCenter.getCostCenterCode());
				aAccountType.setCostCenterDesc(costCenter.getCostCenterDesc());
			}else{
				aAccountType.setCostCenterID(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		
		if (!"Y".equals(CBI_Available)) {
			try {
				if (!this.custSysAc.isChecked() && !this.internalAc
						.isChecked()) {
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
	 * @throws Exception
	 */
	public void doShowDialog(AccountType aAccountType)
			throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aAccountType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.acType.focus();
		} else {
			this.acTypeDesc.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aAccountType.getRecordType())) {
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

			doLoadAccountHead();
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AccountTypeDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		doClearMessage();
		if (!this.acType.isReadonly()) {
			this.acType.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountTypeDialog_AcType.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.acTypeDesc.isReadonly()) {
			this.acTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountTypeDialog_AcTypeDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.acPurpose.isDisabled()) {
			this.acPurpose.setConstraint(new StaticListValidator(PennantStaticListUtil.getAccountPurpose(),
							Labels.getLabel("label_AccountTypeDialog_AcPurpose.value")));
		}
		if (!this.acHeadCode.isReadonly() && !"Y".equals(CBI_Available)) {
			this.acHeadCode.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountTypeDialog_AcHeadCode.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true,4,4));
		}
		if (!this.assertOrLiability.isDisabled()) {
			this.assertOrLiability.setConstraint(new StaticListValidator(PennantStaticListUtil.getAssetOrLiability(),
							Labels.getLabel("label_AccountTypeDialog_AssertOrLiability.value")));
		}
		if (!this.acTypeGrpId.isReadonly()) {
			this.acTypeGrpId.setConstraint(new PTStringValidator(Labels
					.getLabel("label_AccountTypeDialog_AcTypeGrpId.value"), null,
					true));
		}
		if (!this.profitCenter.isReadonly()) {
			this.profitCenter.setConstraint(new PTStringValidator(Labels
					.getLabel("label_AccountTypeDialog_ProfitCenter.value"), null,
					true));
		}
		if (!this.costCenter.isReadonly()) {
			this.costCenter.setConstraint(new PTStringValidator(Labels
					.getLabel("label_AccountTypeDialog_CostCenter.value"), null,
					true, true));
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
		this.acLmtCategory.setConstraint("");
		this.acPurpose.setConstraint("");
		this.acHeadCode.setConstraint("");
		this.assertOrLiability.setConstraint("");
		this.acTypeGrpId.setConstraint("");
		this.profitCenter.setConstraint("");
		this.costCenter.setConstraint("");
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
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.acType.setErrorMessage("");
		this.acTypeDesc.setErrorMessage("");
		this.acLmtCategory.setErrorMessage("");
		this.acPurpose.setErrorMessage("");
		this.acHeadCode.setErrorMessage("");
		this.assertOrLiability.setErrorMessage("");
		this.acTypeGrpId.setErrorMessage("");
		this.profitCenter.setErrorMessage("");
		this.costCenter.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getAccountTypeListCtrl().search();
	}

	// CRUD operations

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_AccountTypeDialog_AcType.value")+" : "+aAccountType.getAcType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(aAccountType.getRecordType())) {
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
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
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
		this.acLmtCategory.setReadonly(isReadOnly("AccountTypeDialog_acLmtCategory"));
		this.acPurpose.setDisabled(isReadOnly("AccountTypeDialog_acPurpose"));
		this.acHeadCode.setReadonly(isReadOnly("AccountTypeDialog_acHeadCode"));
		this.internalAc
				.setDisabled(isReadOnly("AccountTypeDialog_isInternalAc"));
		this.custSysAc.setDisabled(isReadOnly("AccountTypeDialog_isCustSysAc"));
		this.acTypeIsActive.setDisabled(isReadOnly("AccountTypeDialog_acTypeIsActive"));
		this.assertOrLiability.setDisabled(isReadOnly("AccountTypeDialog_AssertOrLiability"));
		this.onBalanceSheet.setDisabled(isReadOnly("AccountTypeDialog_OnBalanceSheet"));
		this.allowOverDraw.setDisabled(isReadOnly("AccountTypeDialog_AllowOverDraw"));
		this.acTypeGrpId.setReadonly(isReadOnly("AccountTypeDialog_acTypeGrpId"));
		this.profitCenter.setReadonly(isReadOnly("AccountTypeDialog_profitCenter"));
		this.costCenter.setReadonly(isReadOnly("AccountTypeDialog_costCenter"));

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
		this.acLmtCategory.setReadonly(true);
		this.acPurpose.setDisabled(true);
		this.acHeadCode.setReadonly(true);
		this.internalAc.setDisabled(true);
		this.custSysAc.setDisabled(true);
		this.acTypeIsActive.setDisabled(true);
		this.assertOrLiability.setDisabled(true);
		this.onBalanceSheet.setDisabled(true);
		this.allowOverDraw.setDisabled(true);
		this.acTypeGrpId.setReadonly(true);
		this.profitCenter.setReadonly(true);
		this.costCenter.setReadonly(true);

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
		this.acLmtCategory.setValue("");
		this.acPurpose.setValue("");
		this.acHeadCode.setText("");
		this.internalAc.setChecked(false);
		this.custSysAc.setChecked(false);
		this.acTypeIsActive.setChecked(false);
		this.assertOrLiability.setValue("");
		this.onBalanceSheet.setChecked(false);
		this.allowOverDraw.setChecked(false);
		this.acTypeGrpId.setValue("");;
		this.profitCenter.setValue("");;
		this.costCenter.setValue("");;
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

		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aAccountType.getRecordType())) {
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

		aAccountType.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getLoginUsrID());
		aAccountType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAccountType.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountType
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAccountType);
				}

				if (isNotesMandatory(taskId, aAccountType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (!StringUtils.isBlank(nextTaskId)) {
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

			aAccountType.setTaskId(taskId);
			aAccountType.setNextTaskId(nextTaskId);
			aAccountType.setRoleCode(getRole());
			aAccountType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountType, tranType);

			String operationRefs = getServiceOperations(taskId, aAccountType);

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

				if (StringUtils.isBlank(method)) {
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
						deleteNotes(getNotes(this.accountType), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
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

	// WorkFlow Components

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
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_AccountTypeDialog,
					auditHeader);
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
		doShowNotes(this.accountType);
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.accountType.getAcType());
	}

	/**
	 * 
	 */
	private void doLoadAccountHead() {
		logger.debug("Entering ");
		String accountHeads = SysParamUtil.getValueAsString("ACCOUNT_HEAD");
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
	
/*	public void onFulfill$acTypeGrpId(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = acTypeGrpId.getObject();
		if (dataObject instanceof String) {
			this.acTypeGrpId.setValue(dataObject.toString());
			this.acTypeGrpId.setDescription("");
		} else {
			AccountTypeGroup details = (AccountTypeGroup) dataObject;
			if (details != null) {
				this.acTypeGrpId.setAttribute("AcTypeGrpId", details.getGroupId());
			}
		}
		logger.debug("Leaving" + event.toString());
	}*/
/*	public void onFulfill$profitCenter(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = profitCenter.getObject();
		if (dataObject instanceof String) {
			this.profitCenter.setValue(dataObject.toString());
			this.profitCenter.setDescription("");
		} else {
			ProfitCenter details = (ProfitCenter) dataObject;
			if (details != null) {
				this.profitCenter.setAttribute("ProfitCenter", details.getProfitCenterID());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	public void onFulfill$costCenter(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = costCenter.getObject();
		if (dataObject instanceof String) {
			this.costCenter.setValue(dataObject.toString());
			this.costCenter.setDescription("");
		} else {
			CostCenter details = (CostCenter) dataObject;
			if (details != null) {
				this.costCenter.setAttribute("CostCenter", details.getCostCenterID());
			}
		}
		logger.debug("Leaving" + event.toString());
	}*/


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

}
