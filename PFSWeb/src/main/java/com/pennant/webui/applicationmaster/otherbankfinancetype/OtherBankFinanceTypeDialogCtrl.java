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
 * FileName    		:  OtherBankFinanceTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2015    														*
 *                                                                  						*
 * Modified Date    :  03-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.otherbankfinancetype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.OtherBankFinanceTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/applicationmaster/OtherBankFinanceType
 * /otherBankFinanceTypeDialog.zul file.
 */
public class OtherBankFinanceTypeDialogCtrl extends
		GFCBaseCtrl<OtherBankFinanceType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(OtherBankFinanceTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_OtherBankFinanceTypeDialog; // autowired
	protected Textbox finType; // autowired
	protected Textbox finTypeDesc; // autowired
	protected Checkbox active; // autowired
	protected Row statusRow;

	// not auto wired vars
	private OtherBankFinanceType otherBankFinanceType; // overhanded per param
	private transient OtherBankFinanceTypeListCtrl otherBankFinanceTypeListCtrl; // overhanded
																					// per
																					// param

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient OtherBankFinanceTypeService otherBankFinanceTypeService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public OtherBankFinanceTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "OtherBankFinanceTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected OtherBankFinanceType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_OtherBankFinanceTypeDialog(Event event)
			throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_OtherBankFinanceTypeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			if (arguments.containsKey("otherBankFinanceType")) {
				this.otherBankFinanceType = (OtherBankFinanceType) arguments
						.get("otherBankFinanceType");
				OtherBankFinanceType befImage = new OtherBankFinanceType();
				BeanUtils.copyProperties(this.otherBankFinanceType, befImage);
				this.otherBankFinanceType.setBefImage(befImage);
				setOtherBankFinanceType(this.otherBankFinanceType);
			} else {
				setOtherBankFinanceType(null);
			}

			doLoadWorkFlow(this.otherBankFinanceType.isWorkflow(),
					this.otherBankFinanceType.getWorkflowId(),
					this.otherBankFinanceType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"OtherBankFinanceTypeDialog");
			}

			// READ OVERHANDED params !
			// we get the otherBankFinanceTypeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete otherBankFinanceType here.
			if (arguments.containsKey("otherBankFinanceTypeListCtrl")) {
				setOtherBankFinanceTypeListCtrl((OtherBankFinanceTypeListCtrl) arguments
						.get("otherBankFinanceTypeListCtrl"));
			} else {
				setOtherBankFinanceTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getOtherBankFinanceType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_OtherBankFinanceTypeDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finTypeDesc.setMaxlength(100);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_OtherBankFinanceTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_OtherBankFinanceTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_OtherBankFinanceTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_OtherBankFinanceTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_OtherBankFinanceTypeDialog);
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
		doWriteBeanToComponents(this.otherBankFinanceType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aOtherBankFinanceType
	 *            OtherBankFinanceType
	 */
	public void doWriteBeanToComponents(
			OtherBankFinanceType aOtherBankFinanceType) {
		logger.debug("Entering");
		this.finType.setValue(aOtherBankFinanceType.getFinType());
		this.finTypeDesc.setValue(aOtherBankFinanceType.getFinTypeDesc());
		this.active.setChecked(aOtherBankFinanceType.isActive());
		if (aOtherBankFinanceType.isNew()
				|| (aOtherBankFinanceType.getRecordType() != null ? aOtherBankFinanceType
						.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(aOtherBankFinanceType.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aOtherBankFinanceType
	 */
	public void doWriteComponentsToBean(
			OtherBankFinanceType aOtherBankFinanceType) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aOtherBankFinanceType.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOtherBankFinanceType.setFinTypeDesc(this.finTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOtherBankFinanceType.setActive(this.active.isChecked());
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

		aOtherBankFinanceType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aOtherBankFinanceType
	 * @throws Exception
	 */
	public void doShowDialog(OtherBankFinanceType aOtherBankFinanceType)
			throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aOtherBankFinanceType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.finTypeDesc.focus();
				if (StringUtils.isNotBlank(aOtherBankFinanceType.getRecordType())) {
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
			doWriteBeanToComponents(aOtherBankFinanceType);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_OtherBankFinanceTypeDialog.onClose();
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

		if (!this.finType.isReadonly()) {
			this.finType
					.setConstraint(new PTStringValidator(
							Labels.getLabel("label_OtherBankFinanceTypeDialog_FinType.value"),
							PennantRegularExpressions.REGEX_UPPERCASENAME, true));
		}
		if (!this.finTypeDesc.isReadonly()) {
			this.finTypeDesc
					.setConstraint(new PTStringValidator(
							Labels.getLabel("label_OtherBankFinanceTypeDialog_FinTypeDesc.value"),
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
		this.finType.setConstraint("");
		this.finTypeDesc.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a OtherBankFinanceType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final OtherBankFinanceType aOtherBankFinanceType = new OtherBankFinanceType();
		BeanUtils.copyProperties(getOtherBankFinanceType(),
				aOtherBankFinanceType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> "
				+ Labels.getLabel("label_OtherBankFinanceTypeDialog_FinType.value")
				+ " : " + aOtherBankFinanceType.getFinType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aOtherBankFinanceType.getRecordType())) {
				aOtherBankFinanceType.setVersion(aOtherBankFinanceType
						.getVersion() + 1);
				aOtherBankFinanceType
						.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aOtherBankFinanceType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aOtherBankFinanceType, tranType)) {
					refreshList();
					closeDialog();
				}

			}catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getOtherBankFinanceType().isNewRecord()) {
			this.finType.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("OtherBankFinanceTypeDialog_finTypeDesc"),
				this.finTypeDesc);
		readOnlyComponent(isReadOnly("OtherBankFinanceTypeDialog_active"),
				this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.otherBankFinanceType.isNewRecord()) {
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
		this.finType.setReadonly(true);
		this.finTypeDesc.setReadonly(true);
		this.active.setDisabled(true);

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

		this.finType.setValue("");
		this.finTypeDesc.setValue("");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final OtherBankFinanceType aOtherBankFinanceType = new OtherBankFinanceType();
		BeanUtils.copyProperties(getOtherBankFinanceType(),
				aOtherBankFinanceType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the OtherBankFinanceType object with the components data
		doWriteComponentsToBean(aOtherBankFinanceType);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aOtherBankFinanceType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aOtherBankFinanceType.getRecordType())) {
				aOtherBankFinanceType.setVersion(aOtherBankFinanceType
						.getVersion() + 1);
				if (isNew) {
					aOtherBankFinanceType
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aOtherBankFinanceType
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aOtherBankFinanceType.setNewRecord(true);
				}
			}
		} else {
			aOtherBankFinanceType
					.setVersion(aOtherBankFinanceType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aOtherBankFinanceType, tranType)) {
				doWriteBeanToComponents(aOtherBankFinanceType);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(OtherBankFinanceType aOtherBankFinanceType,
			String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aOtherBankFinanceType.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getLoginUsrID());
		aOtherBankFinanceType.setLastMntOn(new Timestamp(System
				.currentTimeMillis()));
		aOtherBankFinanceType.setUserDetails(getUserWorkspace()
				.getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aOtherBankFinanceType.setRecordStatus(String.valueOf(userAction
					.getSelectedItem().getValue()));

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aOtherBankFinanceType
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aOtherBankFinanceType);
				}

				if (isNotesMandatory(taskId, aOtherBankFinanceType)) {
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

			aOtherBankFinanceType.setTaskId(taskId);
			aOtherBankFinanceType.setNextTaskId(nextTaskId);
			aOtherBankFinanceType.setRoleCode(getRole());
			aOtherBankFinanceType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aOtherBankFinanceType, tranType);

			String operationRefs = getServiceOperations(taskId, aOtherBankFinanceType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aOtherBankFinanceType,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aOtherBankFinanceType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		OtherBankFinanceType aOtherBankFinanceType = (OtherBankFinanceType) auditHeader
				.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getOtherBankFinanceTypeService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getOtherBankFinanceTypeService()
								.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getOtherBankFinanceTypeService()
								.doApprove(auditHeader);

						if (aOtherBankFinanceType.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getOtherBankFinanceTypeService()
								.doReject(auditHeader);
						if (aOtherBankFinanceType.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_OtherBankFinanceTypeDialog,
								auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_OtherBankFinanceTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.otherBankFinanceType), true);
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
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
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

	public OtherBankFinanceType getOtherBankFinanceType() {
		return this.otherBankFinanceType;
	}

	public void setOtherBankFinanceType(
			OtherBankFinanceType otherBankFinanceType) {
		this.otherBankFinanceType = otherBankFinanceType;
	}

	public void setOtherBankFinanceTypeService(
			OtherBankFinanceTypeService otherBankFinanceTypeService) {
		this.otherBankFinanceTypeService = otherBankFinanceTypeService;
	}

	public OtherBankFinanceTypeService getOtherBankFinanceTypeService() {
		return this.otherBankFinanceTypeService;
	}

	public void setOtherBankFinanceTypeListCtrl(
			OtherBankFinanceTypeListCtrl otherBankFinanceTypeListCtrl) {
		this.otherBankFinanceTypeListCtrl = otherBankFinanceTypeListCtrl;
	}

	public OtherBankFinanceTypeListCtrl getOtherBankFinanceTypeListCtrl() {
		return this.otherBankFinanceTypeListCtrl;
	}

	private AuditHeader getAuditHeader(
			OtherBankFinanceType aOtherBankFinanceType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aOtherBankFinanceType.getBefImage(), aOtherBankFinanceType);
		return new AuditHeader(aOtherBankFinanceType.getFinType(), null, null,
				null, auditDetail, aOtherBankFinanceType.getUserDetails(),
				getOverideMap());
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(
					this.window_OtherBankFinanceTypeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.otherBankFinanceType);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finType.setErrorMessage("");
		this.finTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getOtherBankFinanceTypeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.otherBankFinanceType.getFinType());
	}

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
}
