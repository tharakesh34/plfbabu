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
 * FileName    		:  BankDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.bankdetail;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/BankDetail
 * /bankDetailDialog.zul file.
 */
public class BankDetailDialogCtrl extends GFCBaseCtrl<BankDetail> {
	private static final long serialVersionUID = -2489293301745014852L;
	private static final Logger logger = Logger.getLogger(BankDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_BankDetailDialog;	// autoWired

	protected Textbox 		bankCode; 						// autoWired
	protected Textbox 		bankName; 						// autoWired
	protected Checkbox 		active; 					// autoWired
	protected Intbox 		accNoLength;
	protected Textbox 		bankShortCode;
	// not autoWired variables
	private BankDetail bankDetail; // overHanded per parameter
	private transient BankDetailListCtrl bankDetailListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient BankDetailService bankDetailService;

	/**
	 * default constructor.<br>
	 */
	public BankDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BankDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected BankDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BankDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BankDetailDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("bankDetail")) {
				this.bankDetail = (BankDetail) arguments.get("bankDetail");
				BankDetail befImage = new BankDetail();
				BeanUtils.copyProperties(this.bankDetail, befImage);
				this.bankDetail.setBefImage(befImage);
				setBankDetail(this.bankDetail);
			} else {
				setBankDetail(null);
			}

			doLoadWorkFlow(this.bankDetail.isWorkflow(),
					this.bankDetail.getWorkflowId(),
					this.bankDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"BankDetailDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the bankDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete bankDetail here.
			if (arguments.containsKey("bankDetailListCtrl")) {
				setBankDetailListCtrl((BankDetailListCtrl) arguments
						.get("bankDetailListCtrl"));
			} else {
				setBankDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getBankDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BankDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.bankCode.setMaxlength(8);
		this.bankName.setMaxlength(50);
		this.accNoLength.setMaxlength(2);
		this.bankShortCode.setMaxlength(20);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BankDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BankDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BankDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BankDetailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_BankDetailDialog);
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
		doWriteBeanToComponents(this.bankDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBankDetail
	 *            BankDetail
	 */
	public void doWriteBeanToComponents(BankDetail aBankDetail) {
		logger.debug("Entering");
		this.bankCode.setValue(aBankDetail.getBankCode());
		this.bankName.setValue(aBankDetail.getBankName());
		this.active.setChecked(aBankDetail.isActive());
		this.accNoLength.setValue(aBankDetail.getAccNoLength());
		this.bankShortCode.setValue(aBankDetail.getBankShortCode());
		this.recordStatus.setValue(aBankDetail.getRecordStatus());
		
		if(aBankDetail.isNew() || (aBankDetail.getRecordType() != null ? aBankDetail.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBankDetail
	 */
	public void doWriteComponentsToBean(BankDetail aBankDetail) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aBankDetail.setBankCode(this.bankCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBankDetail.setBankName(this.bankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBankDetail.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.accNoLength.setConstraint("");
			this.accNoLength.setErrorMessage("");
			
			if (this.accNoLength.getValue() == null) {
				throw new WrongValueException(this.accNoLength, Labels.getLabel("NUMBER_RANGE_EQ", new String[] { Labels.getLabel("label_BankDetailDialog_AccNoLength.value"), "0",  String.valueOf(PennantConstants.accNo_maxValue)}));
			}
			
			int accNoLegthValue = this.accNoLength.getValue();
			
			if (!this.accNoLength.isReadonly()) {
				if (accNoLegthValue < 0 || accNoLegthValue > PennantConstants.accNo_maxValue) {
					throw new WrongValueException(this.accNoLength, Labels.getLabel("NUMBER_RANGE_EQ", new String[] { Labels.getLabel("label_BankDetailDialog_AccNoLength.value"), "0",  String.valueOf(PennantConstants.accNo_maxValue)}));
				}
			}
			
			aBankDetail.setAccNoLength(accNoLegthValue);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBankDetail.setBankShortCode(this.bankShortCode.getValue());
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

		aBankDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBankDetail
	 * @throws Exception
	 */
	public void doShowDialog(BankDetail aBankDetail) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aBankDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bankCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.bankName.focus();
				if (StringUtils.isNotBlank(aBankDetail.getRecordType())) {
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
			doWriteBeanToComponents(aBankDetail);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BankDetailDialog.onClose();
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

		if (!this.bankCode.isReadonly()){
			this.bankCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BankDetailDialog_BankCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	

		if (!this.bankName.isReadonly()){
			this.bankName.setConstraint(new PTStringValidator(Labels.getLabel("label_BankDetailDialog_BankName.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		
		if (!this.bankShortCode.isReadonly()){
			this.bankShortCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BankDetailDialog_BankShortCode.value"), 
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.bankCode.setConstraint("");
		this.bankName.setConstraint("");
		this.accNoLength.setConstraint("");
		this.bankShortCode.setConstraint("");
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
		logger.debug("Entering");
		
		this.bankCode.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.accNoLength.setErrorMessage("");
		this.bankShortCode.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a BankDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final BankDetail aBankDetail = new BankDetail();
		BeanUtils.copyProperties(getBankDetail(), aBankDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_BankDetailDialog_BankCode.value")+" : "+aBankDetail.getBankCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aBankDetail.getRecordType())) {
				aBankDetail.setVersion(aBankDetail.getVersion() + 1);
				aBankDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBankDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBankDetail, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
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

		if (getBankDetail().isNewRecord()) {
			this.bankCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.bankCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.bankName.setReadonly(isReadOnly("BankDetailDialog_bankName"));
		this.active.setDisabled(isReadOnly("BankDetailDialog_active"));
		this.accNoLength.setReadonly(isReadOnly("BankDetailDialog_accNoLength"));
		this.bankShortCode.setReadonly(isReadOnly("BankDetailDialog_bankShortCode"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.bankDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.bankCode.setReadonly(true);
		this.bankName.setReadonly(true);
		this.active.setDisabled(true);
		this.accNoLength.setReadonly(true);
		this.bankShortCode.setReadonly(true);
		
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
		this.bankCode.setValue("");
		this.bankName.setValue("");
		this.active.setChecked(false);
		this.accNoLength.setValue(0);
		this.bankShortCode.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final BankDetail aBankDetail = new BankDetail();
		BeanUtils.copyProperties(getBankDetail(), aBankDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the BankDetail object with the components data
		doWriteComponentsToBean(aBankDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBankDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBankDetail.getRecordType())) {
				aBankDetail.setVersion(aBankDetail.getVersion() + 1);
				if (isNew) {
					aBankDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBankDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBankDetail.setNewRecord(true);
				}
			}
		} else {
			aBankDetail.setVersion(aBankDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aBankDetail, tranType)) {
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
	 * @param aBankDetail
	 *            (BankDetail)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(BankDetail aBankDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBankDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBankDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBankDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBankDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBankDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBankDetail);
				}

				if (isNotesMandatory(taskId, aBankDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aBankDetail.setTaskId(taskId);
			aBankDetail.setNextTaskId(nextTaskId);
			aBankDetail.setRoleCode(getRole());
			aBankDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBankDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aBankDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBankDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBankDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BankDetail aBankDetail = (BankDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getBankDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getBankDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getBankDetailService().doApprove(auditHeader);

						if (aBankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBankDetailService().doReject(auditHeader);

						if (aBankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BankDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_BankDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.bankDetail), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aBankDetail
	 *            (BankDetail)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(BankDetail aBankDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBankDetail.getBefImage(), aBankDetail);
		return new AuditHeader(String.valueOf(aBankDetail.getId()),
				null, null, null, auditDetail, aBankDetail.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_BankDetailDialog, auditHeader);
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
		doShowNotes(this.bankDetail);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getBankDetailListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.bankDetail.getBankCode());
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

	public BankDetail getBankDetail() {
		return this.bankDetail;
	}
	public void setBankDetail(BankDetail bankDetail) {
		this.bankDetail = bankDetail;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}
	public BankDetailService getBankDetailService() {
		return this.bankDetailService;
	}

	public void setBankDetailListCtrl(BankDetailListCtrl bankDetailListCtrl) {
		this.bankDetailListCtrl = bankDetailListCtrl;
	}
	public BankDetailListCtrl getBankDetailListCtrl() {
		return this.bankDetailListCtrl;
	}

}
