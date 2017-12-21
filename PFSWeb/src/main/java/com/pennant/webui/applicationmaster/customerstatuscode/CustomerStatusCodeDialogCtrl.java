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
 * FileName    		:  CustomerStatusCodeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.customerstatuscode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.CustomerStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CustomerStatusCode/CustomerStatusCodeDialog.zul file.
 */
public class CustomerStatusCodeDialogCtrl extends GFCBaseCtrl<CustomerStatusCode> {
	private static final long serialVersionUID = -7665708224082701621L;
	private static final Logger logger = Logger.getLogger(CustomerStatusCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_CustomerStatusCodeDialog;	// autoWired

	protected Textbox 	custStsCode; 					// autoWired
	protected Textbox 	custStsDescription; 			// autoWired
	protected Intbox 	dueDays; 						// autoWired
	protected Checkbox 	suspendProfit; 					// autoWired
	protected Checkbox 	custStsIsActive; 				// autoWired

	// not autoWired variables
	private CustomerStatusCode customerStatusCode; 					// overHanded per parameter
	private transient CustomerStatusCodeListCtrl customerStatusCodeListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient CustomerStatusCodeService customerStatusCodeService;

	/**
	 * default constructor.<br>
	 */
	public CustomerStatusCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerStatusCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerStatusCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerStatusCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerStatusCodeDialog);

		try{
		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("customerStatusCode")) {
			this.customerStatusCode = (CustomerStatusCode) arguments.get("customerStatusCode");
			CustomerStatusCode befImage = new CustomerStatusCode();
			BeanUtils.copyProperties(this.customerStatusCode, befImage);
			this.customerStatusCode.setBefImage(befImage);
			setCustomerStatusCode(this.customerStatusCode);
		} else {
			setCustomerStatusCode(null);
		}

		doLoadWorkFlow(this.customerStatusCode.isWorkflow(), this.customerStatusCode.getWorkflowId(), this.customerStatusCode.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerStatusCodeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the customerStatusCodeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerStatusCode here.
		if (arguments.containsKey("customerStatusCodeListCtrl")) {
			setCustomerStatusCodeListCtrl((CustomerStatusCodeListCtrl) arguments.get("customerStatusCodeListCtrl"));
		} else {
			setCustomerStatusCodeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerStatusCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerStatusCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Leaving");

		// Empty sent any required attributes
		this.custStsCode.setMaxlength(8);
		this.custStsDescription.setMaxlength(50);
		this.dueDays.setMaxlength(4);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerStatusCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerStatusCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerStatusCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerStatusCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerStatusCodeDialog);
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
		doWriteBeanToComponents(this.customerStatusCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerStatusCode
	 *            CustomerStatusCode
	 */
	public void doWriteBeanToComponents(CustomerStatusCode aCustomerStatusCode) {
		logger.debug("Entering");

		this.custStsCode.setValue(aCustomerStatusCode.getCustStsCode());
		this.custStsDescription.setValue(aCustomerStatusCode.getCustStsDescription());
		this.dueDays.setValue(aCustomerStatusCode.getDueDays());
		this.suspendProfit.setChecked(aCustomerStatusCode.isSuspendProfit());
		this.custStsIsActive.setChecked(aCustomerStatusCode.isCustStsIsActive());
		this.recordStatus.setValue(aCustomerStatusCode.getRecordStatus());
		
		if(aCustomerStatusCode.isNew() || (aCustomerStatusCode.getRecordType() != null ? aCustomerStatusCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.custStsIsActive.setChecked(true);
			this.custStsIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerStatusCode
	 */
	public void doWriteComponentsToBean(CustomerStatusCode aCustomerStatusCode) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerStatusCode.setCustStsCode(this.custStsCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerStatusCode.setCustStsDescription(this.custStsDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerStatusCode.setDueDays(this.dueDays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerStatusCode.setSuspendProfit(this.suspendProfit.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerStatusCode.setCustStsIsActive(this.custStsIsActive.isChecked());
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

		aCustomerStatusCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerStatusCode
	 * @throws Exception
	 */
	public void doShowDialog(CustomerStatusCode aCustomerStatusCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aCustomerStatusCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custStsCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.custStsDescription.focus();
				if (StringUtils.isNotBlank(aCustomerStatusCode.getRecordType())) {
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
			doWriteBeanToComponents(aCustomerStatusCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerStatusCodeDialog.onClose();
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

		if (!this.custStsCode.isReadonly()){
			this.custStsCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerStatusCodeDialog_CustStsCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.custStsDescription.isReadonly()){
			this.custStsDescription.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerStatusCodeDialog_CustStsDescription.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.dueDays.isReadonly()) {
			this.dueDays.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerStatusCodeDialog_DueDays.value"), true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custStsCode.setConstraint("");
		this.custStsDescription.setConstraint("");
		this.dueDays.setConstraint("");
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
		this.custStsCode.setErrorMessage("");
		this.custStsDescription.setErrorMessage("");
		this.dueDays.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a CustomerStatusCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerStatusCode aCustomerStatusCode = new CustomerStatusCode();
		BeanUtils.copyProperties(getCustomerStatusCode(), aCustomerStatusCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCustomerStatusCode.getCustStsCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerStatusCode.getRecordType())) {
				aCustomerStatusCode.setVersion(aCustomerStatusCode.getVersion() + 1);
				aCustomerStatusCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCustomerStatusCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCustomerStatusCode, tranType)) {
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
		if (getCustomerStatusCode().isNewRecord()) {
			this.custStsCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custStsCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.custStsDescription.setReadonly(isReadOnly("CustomerStatusCodeDialog_custStsDescription"));
		this.dueDays.setDisabled(isReadOnly("CustomerStatusCodeDialog_dueDays"));
		this.suspendProfit.setDisabled(isReadOnly("CustomerStatusCodeDialog_suspendProfit"));
		this.custStsIsActive.setDisabled(isReadOnly("CustomerStatusCodeDialog_custStsIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerStatusCode.isNewRecord()) {
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
		this.custStsCode.setReadonly(true);
		this.custStsDescription.setReadonly(true);
		this.dueDays.setReadonly(true);
		this.suspendProfit.setDisabled(true);
		this.custStsIsActive.setDisabled(true);

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
		this.custStsCode.setValue("");
		this.custStsDescription.setValue("");
		this.dueDays.setText("");
		this.suspendProfit.setChecked(false);
		this.custStsIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerStatusCode aCustomerStatusCode = new CustomerStatusCode();
		BeanUtils.copyProperties(getCustomerStatusCode(), aCustomerStatusCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		/*String statusCode = getCustomerStatusCodeService().getCurDueDaysStatus(this.dueDays.intValue(),"_View");
		if(!StringUtils.trimToEmpty(statusCode).equals("")) {	
			MessageUtil.showErrorMessage(Labels.getLabel("CUST_STS_CODE_WITH_DUEDAYS_ALREADY_EXISTS",
					new String[]{statusCode, String.valueOf(this.dueDays.getValue()) }));
			return;
		}*/
		
		// fill the CustomerStatusCode object with the components data
		doWriteComponentsToBean(aCustomerStatusCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCustomerStatusCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerStatusCode.getRecordType())) {
				aCustomerStatusCode.setVersion(aCustomerStatusCode.getVersion() + 1);
				if (isNew) {
					aCustomerStatusCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerStatusCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerStatusCode.setNewRecord(true);
				}
			}
		} else {
			aCustomerStatusCode.setVersion(aCustomerStatusCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCustomerStatusCode, tranType)) {
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
	 * @param aCustomerStatusCode
	 *            (CustomerStatusCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerStatusCode aCustomerStatusCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerStatusCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerStatusCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerStatusCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerStatusCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerStatusCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerStatusCode);
				}

				if (isNotesMandatory(taskId, aCustomerStatusCode)) {
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

			aCustomerStatusCode.setTaskId(taskId);
			aCustomerStatusCode.setNextTaskId(nextTaskId);
			aCustomerStatusCode.setRoleCode(getRole());
			aCustomerStatusCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerStatusCode, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerStatusCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerStatusCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerStatusCode, tranType);
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
		CustomerStatusCode aCustomerStatusCode = (CustomerStatusCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerStatusCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerStatusCodeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerStatusCodeService().doApprove(auditHeader);

						if (aCustomerStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerStatusCodeService().doReject(auditHeader);

						if (aCustomerStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerStatusCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerStatusCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.customerStatusCode), true);
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

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerStatusCode
	 *            (CustomerStatusCode)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(CustomerStatusCode aCustomerStatusCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,aCustomerStatusCode.getBefImage(), aCustomerStatusCode);
		return new AuditHeader(String.valueOf(aCustomerStatusCode.getId()), null,
				null, null, auditDetail, aCustomerStatusCode.getUserDetails(), getOverideMap());
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
		map.put("notes", getNotes(this.customerStatusCode));
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,	map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCustomerStatusCodeListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.customerStatusCode.getCustStsCode());
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

	public CustomerStatusCode getCustomerStatusCode() {
		return this.customerStatusCode;
	}
	public void setCustomerStatusCode(CustomerStatusCode customerStatusCode) {
		this.customerStatusCode = customerStatusCode;
	}
	/*public CustomerStatusCode getCustomerDueDays() {
		return this.dueDays;
	}
	public void setCustomerDueDays(CustomerStatusCode dueDays) {
		this.dueDays = dueDays;
	}*/

	public void setCustomerStatusCodeService(CustomerStatusCodeService customerStatusCodeService) {
		this.customerStatusCodeService = customerStatusCodeService;
	}
	public CustomerStatusCodeService getCustomerStatusCodeService() {
		return this.customerStatusCodeService;
	}

	public void setCustomerStatusCodeListCtrl(CustomerStatusCodeListCtrl customerStatusCodeListCtrl) {
		this.customerStatusCodeListCtrl = customerStatusCodeListCtrl;
	}
	public CustomerStatusCodeListCtrl getCustomerStatusCodeListCtrl() {
		return this.customerStatusCodeListCtrl;
	}

}
