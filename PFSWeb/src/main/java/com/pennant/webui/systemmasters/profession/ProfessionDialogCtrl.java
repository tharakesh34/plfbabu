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
 * * FileName : ProfessionDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.profession;

import java.sql.Timestamp;
import java.util.ArrayList;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.service.systemmasters.ProfessionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/Profession/professionDialog.zul file.
 */
public class ProfessionDialogCtrl extends GFCBaseCtrl<Profession> {
	private static final long serialVersionUID = -5160841359166113408L;
	private static final Logger logger = LogManager.getLogger(ProfessionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProfessionDialog;

	protected Textbox professionCode;
	protected Textbox professionDesc;
	protected Checkbox professionIsActive;
	protected Checkbox professionSelfEmployee;

	// not autoWired variables
	private Profession profession; // over handed per parameter
	private transient ProfessionListCtrl professionListCtrl; // over handed per
	// parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient ProfessionService professionService;

	/**
	 * default constructor.<br>
	 */
	public ProfessionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProfessionDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Profession object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ProfessionDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProfessionDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("profession")) {
				this.profession = (Profession) arguments.get("profession");
				Profession befImage = new Profession();
				BeanUtils.copyProperties(this.profession, befImage);
				this.profession.setBefImage(befImage);

				setProfession(this.profession);
			} else {
				setProfession(null);
			}

			doLoadWorkFlow(this.profession.isWorkflow(), this.profession.getWorkflowId(),
					this.profession.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ProfessionDialog");
			}

			// READ OVERHANDED parameters !
			// we get the professionListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete profession here.
			if (arguments.containsKey("professionListCtrl")) {
				setProfessionListCtrl((ProfessionListCtrl) arguments.get("professionListCtrl"));
			} else {
				setProfessionListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getProfession());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ProfessionDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.professionCode.setMaxlength(8);
		this.professionDesc.setMaxlength(50);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProfessionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProfessionDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProfessionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProfessionDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_ProfessionDialog);
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
		doWriteBeanToComponents(this.profession.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProfession Profession
	 */
	public void doWriteBeanToComponents(Profession aProfession) {
		logger.debug("Entering");
		this.professionCode.setValue(aProfession.getProfessionCode());
		this.professionDesc.setValue(aProfession.getProfessionDesc());
		this.professionIsActive.setChecked(aProfession.isProfessionIsActive());
		this.professionSelfEmployee.setChecked(aProfession.isSelfEmployee());
		this.recordStatus.setValue(aProfession.getRecordStatus());

		if (aProfession.isNewRecord() || (aProfession.getRecordType() != null ? aProfession.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.professionIsActive.setChecked(true);
			this.professionIsActive.setDisabled(true);

		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProfession
	 */
	public void doWriteComponentsToBean(Profession aProfession) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aProfession.setProfessionCode(this.professionCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProfession.setProfessionDesc(this.professionDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProfession.setProfessionIsActive(this.professionIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProfession.setSelfEmployee(this.professionSelfEmployee.isChecked());
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

		aProfession.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aProfession
	 */
	public void doShowDialog(Profession aProfession) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aProfession.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.professionCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.professionDesc.focus();
				if (StringUtils.isNotBlank(aProfession.getRecordType())) {
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
			doWriteBeanToComponents(aProfession);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ProfessionDialog.onClose();
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
		if (!this.professionCode.isReadonly()) {
			this.professionCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProfessionDialog_ProfessionCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.professionDesc.isReadonly()) {
			this.professionDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProfessionDialog_ProfessionDesc.value"),
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
		this.professionCode.setConstraint("");
		this.professionDesc.setConstraint("");
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
		this.professionCode.setErrorMessage("");
		this.professionDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Profession aProfession = new Profession();
		BeanUtils.copyProperties(getProfession(), aProfession);

		String keyReference = Labels.getLabel("label_ProfessionDialog_ProfessionCode.value") + " : "
				+ aProfession.getProfessionCode();

		doDelete(keyReference, aProfession);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getProfession().isNewRecord()) {
			this.professionCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.professionCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.professionDesc.setReadonly(isReadOnly("ProfessionDialog_professionDesc"));
		this.professionIsActive.setDisabled(isReadOnly("ProfessionDialog_professionIsActive"));
		this.professionSelfEmployee.setDisabled(isReadOnly("ProfessionDialog_ProfessionSelfEmployee"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.profession.isNewRecord()) {
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

		this.professionCode.setReadonly(true);
		this.professionDesc.setReadonly(true);
		this.professionIsActive.setDisabled(true);
		this.professionSelfEmployee.setDisabled(true);

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
		this.professionCode.setValue("");
		this.professionDesc.setValue("");
		this.professionIsActive.setChecked(false);
		this.professionSelfEmployee.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Profession aProfession = new Profession();
		BeanUtils.copyProperties(getProfession(), aProfession);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Profession object with the components data
		doWriteComponentsToBean(aProfession);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aProfession.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProfession.getRecordType())) {
				aProfession.setVersion(aProfession.getVersion() + 1);
				if (isNew) {
					aProfession.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProfession.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProfession.setNewRecord(true);
				}
			}
		} else {
			aProfession.setVersion(aProfession.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aProfession, tranType)) {
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
	 * @param aProfession (Profession)
	 * 
	 * @param tranType    (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(Profession aProfession, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aProfession.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aProfession.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProfession.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aProfession.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProfession.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aProfession);
				}

				if (isNotesMandatory(taskId, aProfession)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
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

			aProfession.setTaskId(taskId);
			aProfession.setNextTaskId(nextTaskId);
			aProfession.setRoleCode(getRole());
			aProfession.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProfession, tranType);
			String operationRefs = getServiceOperations(taskId, aProfession);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProfession, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aProfession, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
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
		Profession aProfession = (Profession) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getProfessionService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getProfessionService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getProfessionService().doApprove(auditHeader);

					if (aProfession.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getProfessionService().doReject(auditHeader);

					if (aProfession.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ProfessionDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ProfessionDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.profession), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Details

	/**
	 * Get Audit Header Details
	 * 
	 * @param aProfession (Profession)
	 * @param tranType    (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Profession aProfession, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProfession.getBefImage(), aProfession);
		return new AuditHeader(String.valueOf(aProfession.getId()), null, null, null, auditDetail,
				aProfession.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.profession);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getProfessionListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.profession.getProfessionCode());
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

	public Profession getProfession() {
		return this.profession;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}

	public void setProfessionService(ProfessionService professionService) {
		this.professionService = professionService;
	}

	public ProfessionService getProfessionService() {
		return this.professionService;
	}

	public void setProfessionListCtrl(ProfessionListCtrl professionListCtrl) {
		this.professionListCtrl = professionListCtrl;
	}

	public ProfessionListCtrl getProfessionListCtrl() {
		return this.professionListCtrl;
	}

}
