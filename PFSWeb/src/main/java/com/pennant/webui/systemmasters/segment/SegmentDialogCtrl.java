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
 * * FileName : SegmentDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.segment;

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
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.service.systemmasters.SegmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/Segment/segmentDialog.zul file.
 */
public class SegmentDialogCtrl extends GFCBaseCtrl<Segment> {
	private static final long serialVersionUID = 555392639366041343L;
	private static final Logger logger = LogManager.getLogger(SegmentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SegmentDialog;
	protected Textbox segmentCode;
	protected Textbox segmentDesc;
	protected Checkbox segmentIsActive;

	// not auto wired variables
	private Segment segment; // overHanded per parameter
	private transient SegmentListCtrl segmentListCtrl; // overHanded per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient SegmentService segmentService;

	/**
	 * default constructor.<br>
	 */
	public SegmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SegmentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Segment object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_SegmentDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SegmentDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("segment")) {
				this.segment = (Segment) arguments.get("segment");
				Segment befImage = new Segment();
				BeanUtils.copyProperties(this.segment, befImage);
				this.segment.setBefImage(befImage);

				setSegment(this.segment);
			} else {
				setSegment(null);
			}

			doLoadWorkFlow(this.segment.isWorkflow(), this.segment.getWorkflowId(), this.segment.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "SegmentDialog");
			}

			// READ OVERHANDED parameters !
			// we get the segmentListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete segment here.
			if (arguments.containsKey("segmentListCtrl")) {
				setSegmentListCtrl((SegmentListCtrl) arguments.get("segmentListCtrl"));
			} else {
				setSegmentListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSegment());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SegmentDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.segmentCode.setMaxlength(8);
		this.segmentDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SegmentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SegmentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SegmentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SegmentDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_SegmentDialog);
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
		doWriteBeanToComponents(this.segment.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSegment (Segment)
	 * 
	 */
	public void doWriteBeanToComponents(Segment aSegment) {
		logger.debug("Entering");
		this.segmentCode.setValue(aSegment.getSegmentCode());
		this.segmentDesc.setValue(aSegment.getSegmentDesc());
		this.segmentIsActive.setChecked(aSegment.isSegmentIsActive());
		this.recordStatus.setValue(aSegment.getRecordStatus());

		if (aSegment.isNewRecord() || StringUtils.equals(aSegment.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			this.segmentIsActive.setChecked(true);
			this.segmentIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSegment
	 */
	public void doWriteComponentsToBean(Segment aSegment) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSegment.setSegmentCode(StringUtils.strip(this.segmentCode.getValue().toUpperCase()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSegment.setSegmentDesc(StringUtils.strip(this.segmentDesc.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSegment.setSegmentIsActive(this.segmentIsActive.isChecked());
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

		aSegment.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aSegment
	 */
	public void doShowDialog(Segment aSegment) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSegment.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.segmentCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.segmentDesc.focus();
				if (StringUtils.isNotBlank(aSegment.getRecordType())) {
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
			doWriteBeanToComponents(aSegment);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SegmentDialog.onClose();
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

		if (!this.segmentCode.isReadonly()) {
			this.segmentCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_SegmentDialog_SegmentCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.segmentDesc.isReadonly()) {
			this.segmentDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_SegmentDialog_SegmentDesc.value"),
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
		this.segmentCode.setConstraint("");
		this.segmentDesc.setConstraint("");
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
		this.segmentCode.setConstraint("");
		this.segmentCode.setErrorMessage("");
		this.segmentDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Segment aSegment = new Segment();
		BeanUtils.copyProperties(getSegment(), aSegment);

		doDelete(aSegment.getSegmentCode(), aSegment);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getSegment().isNewRecord()) {
			this.segmentCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.segmentCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.segmentDesc.setReadonly(isReadOnly("SegmentDialog_segmentDesc"));
		this.segmentIsActive.setDisabled(isReadOnly("SegmentDialog_segmentIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.segment.isNewRecord()) {
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
		this.segmentCode.setReadonly(true);
		this.segmentDesc.setReadonly(true);
		this.segmentIsActive.setDisabled(true);

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
		this.segmentCode.setValue("");
		this.segmentDesc.setValue("");
		this.segmentIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Segment aSegment = new Segment();
		BeanUtils.copyProperties(getSegment(), aSegment);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Segment object with the components data
		doWriteComponentsToBean(aSegment);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSegment.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSegment.getRecordType())) {
				aSegment.setVersion(aSegment.getVersion() + 1);
				if (isNew) {
					aSegment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSegment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSegment.setNewRecord(true);
				}
			}
		} else {
			aSegment.setVersion(aSegment.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSegment, tranType)) {
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
	 * @param aSegment (Segment)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(Segment aSegment, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSegment.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSegment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSegment.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSegment.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSegment.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSegment);
				}

				if (isNotesMandatory(taskId, aSegment)) {
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

			aSegment.setTaskId(taskId);
			aSegment.setNextTaskId(nextTaskId);
			aSegment.setRoleCode(getRole());
			aSegment.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSegment, tranType);

			String operationRefs = getServiceOperations(taskId, aSegment);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSegment, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSegment, tranType);
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
		Segment aSegment = (Segment) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getSegmentService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getSegmentService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getSegmentService().doApprove(auditHeader);

					if (aSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getSegmentService().doReject(auditHeader);

					if (aSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_SegmentDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_SegmentDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.segment), true);
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
	 * @param aSubSegment (SubSegment)
	 * @param tranType    (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Segment aSegment, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSegment.getBefImage(), aSegment);
		return new AuditHeader(String.valueOf(aSegment.getId()), null, null, null, auditDetail,
				aSegment.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_SegmentDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.segment);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getSegmentListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.segment.getSegmentCode());
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

	public Segment getSegment() {
		return this.segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public void setSegmentService(SegmentService segmentService) {
		this.segmentService = segmentService;
	}

	public SegmentService getSegmentService() {
		return this.segmentService;
	}

	public void setSegmentListCtrl(SegmentListCtrl segmentListCtrl) {
		this.segmentListCtrl = segmentListCtrl;
	}

	public SegmentListCtrl getSegmentListCtrl() {
		return this.segmentListCtrl;
	}

}
