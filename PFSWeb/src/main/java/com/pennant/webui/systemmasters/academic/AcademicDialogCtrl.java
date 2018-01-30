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
package com.pennant.webui.systemmasters.academic;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.service.systemmasters.AcademicService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Academic/AcademicDialog.zul file. <br>
 * ************************************************************<br>
 */
public class AcademicDialogCtrl extends GFCBaseCtrl<Academic> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = Logger.getLogger(AcademicDialogCtrl.class);

	protected Window window_AcademicDialog;
	protected Uppercasebox academicLevel;
	protected Uppercasebox academicDecipline;
	protected Textbox academicDesc;

	private Academic academic;
	private transient AcademicListCtrl academicListCtrl;

	private transient boolean validationOn;
	
	private transient AcademicService academicService;

	/**
	 * default constructor.<br>
	 */
	public AcademicDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AcademicDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_AcademicDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AcademicDialog);

		try {
			// Get the required arguments.
			this.academic = (Academic) arguments.get("academic");
			this.academicListCtrl = (AcademicListCtrl) arguments.get("academicListCtrl");

			if (this.academic == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			Academic acadamic = new Academic();
			BeanUtils.copyProperties(this.academic, acadamic);
			this.academic.setBefImage(acadamic);

			// Render the page and display the data.
			doLoadWorkFlow(this.academic.isWorkflow(), this.academic.getWorkflowId(), this.academic.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} 

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.academic);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		this.academicLevel.setMaxlength(8);
		this.academicDecipline.setMaxlength(8);
		this.academicDesc.setMaxlength(50);

		setStatusDetails();
		
		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AcademicDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AcademicDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AcademicDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AcademicDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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

		doWriteBeanToComponents(this.academic.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param academic
	 * 
	 */
	public void doWriteBeanToComponents(Academic academic) {
		logger.debug("Entering");

		this.academicLevel.setValue(academic.getAcademicLevel());
		this.academicDecipline.setValue(academic.getAcademicDecipline());
		this.academicDesc.setValue(academic.getAcademicDesc());
		this.recordStatus.setValue(academic.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAcademic
	 */
	public void doWriteComponentsToBean(Academic academic) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			academic.setAcademicLevel(this.academicLevel.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			academic.setAcademicDecipline(this.academicDecipline.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			academic.setAcademicDesc(this.academicDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		academic.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic
	 *            The entity that need to be render.
	 */
	public void doShowDialog(Academic academic) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (academic.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.academicLevel.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.academicDesc.focus();
				if (StringUtils.isNotBlank(academic.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		
		// fill the components with the data
		doWriteBeanToComponents(academic);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.academicLevel.isReadonly()) {
			this.academicLevel.setConstraint(new PTStringValidator(Labels
					.getLabel("label_AcademicDialog_AcademicLevel.value"), PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM,
					true));
		}

		if (!this.academicDecipline.isReadonly()) {
			this.academicDecipline.setConstraint(new PTStringValidator(Labels
					.getLabel("label_AcademicDialog_AcademicDecipline.value"),
					PennantRegularExpressions.REGEX_UPPERCASENAME, true));
		}
		if (!this.academicDesc.isReadonly()) {
			this.academicDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_AcademicDialog_AcademicDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION,
					true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		setValidationOn(false);
		this.academicLevel.setConstraint("");
		this.academicDecipline.setConstraint("");
		this.academicDesc.setConstraint("");

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
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.academicLevel.setErrorMessage("");
		this.academicDecipline.setErrorMessage("");
		this.academicDesc.setErrorMessage("");

		logger.debug("Leaving");
	}

	
	/**
	 * Deletes a Academic entity from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug("Entering");

		final Academic entity = new Academic();
		BeanUtils.copyProperties(this.academic, entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_AcademicDialog_AcademicLevel.value") + " : " + academic.getAcademicLevel();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(academic.getRecordType())) {
				academic.setVersion(academic.getVersion() + 1);
				academic.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					academic.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(academic, tranType)) {
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

		if (this.academic.isNewRecord()) {
			this.academicLevel.setReadonly(false);
			this.academicDecipline.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.academicLevel.setReadonly(true);
			this.academicDecipline.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.academicDesc.setReadonly(isReadOnly("AcademicDialog_academicDesc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.academic.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.academicLevel.setReadonly(true);
		this.academicDecipline.setReadonly(true);
		this.academicDesc.setReadonly(true);

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

		this.academicLevel.setValue("");
		this.academicDecipline.setValue("");
		this.academicDesc.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug("Entering");

		final Academic aAcademic = new Academic();
		BeanUtils.copyProperties(this.academic, aAcademic);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the Academic object with the components data
		doWriteComponentsToBean(aAcademic);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aAcademic.isNew();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAcademic.getRecordType())) {
				aAcademic.setVersion(aAcademic.getVersion() + 1);
				if (isNew) {
					aAcademic.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAcademic.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAcademic.setNewRecord(true);
				}
			}
		} else {
			aAcademic.setVersion(aAcademic.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aAcademic, tranType)) {
				refreshList();
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
	 * @param aAcademic
	 *            (Academic)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Academic aAcademic, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aAcademic.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAcademic.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAcademic.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aAcademic.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAcademic.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAcademic);
				}

				if (isNotesMandatory(taskId, aAcademic)) {
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

			aAcademic.setTaskId(taskId);
			aAcademic.setNextTaskId(nextTaskId);
			aAcademic.setRoleCode(getRole());
			aAcademic.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAcademic, tranType);
			String operationRefs = getServiceOperations(taskId, aAcademic);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAcademic, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAcademic, tranType);
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
		AuditHeader aAuditHeader = auditHeader;
		Academic aAcademic = (Academic) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						aAuditHeader = academicService.delete(aAuditHeader);
						deleteNotes = true;
					} else {
						aAuditHeader = academicService.saveOrUpdate(aAuditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						aAuditHeader = academicService.doApprove(aAuditHeader);

						if (aAcademic.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						aAuditHeader = academicService.doReject(aAuditHeader);

						if (aAcademic.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						aAuditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AcademicDialog, aAuditHeader);
						return processCompleted;
					}
				}

				aAuditHeader = ErrorControl.showErrorDetails(this.window_AcademicDialog, aAuditHeader);
				retValue = aAuditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.academic), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					aAuditHeader.setOveride(true);
					aAuditHeader.setErrorMessage(null);
					aAuditHeader.setInfoMessage(null);
					aAuditHeader.setOverideMessage(null);
				}
			}

			setOverideMap(aAuditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Academic aAcademic, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAcademic.getBefImage(), aAcademic);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAcademic.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.academic);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		academicListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.academic.getAcademicID());
	}


	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}
	
	public void setAcademicService(AcademicService academicService) {
		this.academicService = academicService;
	}

}
