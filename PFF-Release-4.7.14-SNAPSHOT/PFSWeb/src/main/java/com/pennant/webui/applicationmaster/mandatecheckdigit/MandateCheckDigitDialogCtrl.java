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
 * FileName    		:  MandateCheckDigitDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-12-2017    														*
 *                                                                  						*
 * Modified Date    :  11-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-12-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.mandatecheckdigit;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.MandateCheckDigitService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/MandateCheckDigit/mandateCheckDigitDialog.zul
 * file. <br>
 */
public class MandateCheckDigitDialogCtrl extends GFCBaseCtrl<MandateCheckDigit> {

	private static final long					serialVersionUID	= 1L;
	private static final Logger					logger				= Logger
			.getLogger(MandateCheckDigitDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_MandateCheckDigitDialog;
	protected Intbox							checkDigitValue;
	protected Uppercasebox						lookUpValue;
	protected Checkbox							active;
	private MandateCheckDigit					mandateCheckDigit;				// overhanded per param

	private transient MandateCheckDigitListCtrl	mandateCheckDigitListCtrl;		// overhanded
	// per
	// param
	private transient MandateCheckDigitService	mandateCheckDigitService;

	/**
	 * default constructor.<br>
	 */
	public MandateCheckDigitDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MandateCheckDigitDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.mandateCheckDigit.getCheckDigitValue());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_MandateCheckDigitDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_MandateCheckDigitDialog);

		try {
			// Get the required arguments.
			this.mandateCheckDigit = (MandateCheckDigit) arguments.get("mandateCheckDigit");
			this.mandateCheckDigitListCtrl = (MandateCheckDigitListCtrl) arguments.get("mandateCheckDigitListCtrl");

			if (this.mandateCheckDigit == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			MandateCheckDigit mandateCheckDigit = new MandateCheckDigit();
			BeanUtils.copyProperties(this.mandateCheckDigit, mandateCheckDigit);
			this.mandateCheckDigit.setBefImage(mandateCheckDigit);

			// Render the page and display the data.
			doLoadWorkFlow(this.mandateCheckDigit.isWorkflow(), this.mandateCheckDigit.getWorkflowId(),
					this.mandateCheckDigit.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.mandateCheckDigit);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.checkDigitValue.setMaxlength(10);
		this.lookUpValue.setMaxlength(1);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MandateCheckDigitDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MandateCheckDigitDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MandateCheckDigitDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.mandateCheckDigit);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		mandateCheckDigitListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.mandateCheckDigit.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param mandateCheckDigit
	 * 
	 */
	public void doWriteBeanToComponents(MandateCheckDigit aMandateCheckDigit) {
		logger.debug(Literal.ENTERING);

		this.checkDigitValue.setValue(aMandateCheckDigit.getCheckDigitValue());
		this.lookUpValue.setValue(aMandateCheckDigit.getLookUpValue());
		this.recordStatus.setValue(aMandateCheckDigit.getRecordStatus());
		if(aMandateCheckDigit.isNewRecord()){
			this.active.setChecked(true);
		}else{
		this.active.setChecked(aMandateCheckDigit.isActive());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aMandateCheckDigit
	 */
	public void doWriteComponentsToBean(MandateCheckDigit aMandateCheckDigit) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Check Digit Value
		try {
			int checkDigit = this.checkDigitValue.getValue();
			if (!this.checkDigitValue.isReadonly()) {
				if (checkDigit < 0) {
					throw new WrongValueException(this.checkDigitValue,
							Labels.getLabel("NUMBER_MINVALUE_EQ",
									new String[] {
											Labels.getLabel("label_MandateCheckDigitDialog_CheckDigitValue.value"),
											String.valueOf(0) }));
				} else if (checkDigit >= 43){
					throw new WrongValueException(this.checkDigitValue,
							Labels.getLabel("NUMBER_MAXVALUE_EQ",
									new String[] {
											Labels.getLabel("label_MandateCheckDigitDialog_CheckDigitValue.value"),
											String.valueOf(42) }));
				}

			}
			aMandateCheckDigit.setCheckDigitValue(this.checkDigitValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Look Up Value
		try {
			aMandateCheckDigit.setLookUpValue(this.lookUpValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aMandateCheckDigit.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param mandateCheckDigit
	 *            The entity that need to be render.
	 */
	public void doShowDialog(MandateCheckDigit mandateCheckDigit) {
		logger.debug(Literal.LEAVING);

		if (mandateCheckDigit.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.checkDigitValue.focus();
		} else {
			this.checkDigitValue.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(mandateCheckDigit.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.lookUpValue.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(mandateCheckDigit);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		/*if (!this.checkDigitValue.isReadonly()) {
			this.checkDigitValue.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_MandateCheckDigitDialog_CheckDigitValue.value"),true, true));
		}*/
		if (!this.lookUpValue.isReadonly()) {
			this.lookUpValue.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateCheckDigitDialog_LookUpValue.value"),
							PennantRegularExpressions.REGEX_LOOK_UP_VALUE, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.checkDigitValue.setConstraint("");
		this.lookUpValue.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a MandateCheckDigit object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final MandateCheckDigit aMandateCheckDigit = new MandateCheckDigit();
		BeanUtils.copyProperties(this.mandateCheckDigit, aMandateCheckDigit);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_CheckDigitValue") + " : " + aMandateCheckDigit.getCheckDigitValue();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aMandateCheckDigit.getRecordType()).equals("")) {
				aMandateCheckDigit.setVersion(aMandateCheckDigit.getVersion() + 1);
				aMandateCheckDigit.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aMandateCheckDigit.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aMandateCheckDigit.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aMandateCheckDigit.getNextTaskId(),
							aMandateCheckDigit);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aMandateCheckDigit, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.mandateCheckDigit.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.checkDigitValue);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.checkDigitValue);

		}

		readOnlyComponent(isReadOnly("MandateCheckDigitDialog_LookUpValue"), this.lookUpValue);
		readOnlyComponent(isReadOnly("MandateCheckDigitDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.mandateCheckDigit.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		this.btnDelete.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.checkDigitValue);
		readOnlyComponent(true, this.lookUpValue);
		readOnlyComponent(true, this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.checkDigitValue.setText("");
		this.lookUpValue.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final MandateCheckDigit aMandateCheckDigit = new MandateCheckDigit();
		BeanUtils.copyProperties(this.mandateCheckDigit, aMandateCheckDigit);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aMandateCheckDigit);

		isNew = aMandateCheckDigit.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aMandateCheckDigit.getRecordType())) {
				aMandateCheckDigit.setVersion(aMandateCheckDigit.getVersion() + 1);
				if (isNew) {
					aMandateCheckDigit.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aMandateCheckDigit.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMandateCheckDigit.setNewRecord(true);
				}
			}
		} else {
			aMandateCheckDigit.setVersion(aMandateCheckDigit.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aMandateCheckDigit, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(MandateCheckDigit aMandateCheckDigit, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aMandateCheckDigit.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aMandateCheckDigit.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMandateCheckDigit.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aMandateCheckDigit.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aMandateCheckDigit.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aMandateCheckDigit);
				}

				if (isNotesMandatory(taskId, aMandateCheckDigit)) {
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

			aMandateCheckDigit.setTaskId(taskId);
			aMandateCheckDigit.setNextTaskId(nextTaskId);
			aMandateCheckDigit.setRoleCode(getRole());
			aMandateCheckDigit.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aMandateCheckDigit, tranType);
			String operationRefs = getServiceOperations(taskId, aMandateCheckDigit);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aMandateCheckDigit, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aMandateCheckDigit, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		MandateCheckDigit aMandateCheckDigit = (MandateCheckDigit) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = mandateCheckDigitService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = mandateCheckDigitService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = mandateCheckDigitService.doApprove(auditHeader);

						if (aMandateCheckDigit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = mandateCheckDigitService.doReject(auditHeader);
						if (aMandateCheckDigit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_MandateCheckDigitDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_MandateCheckDigitDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.mandateCheckDigit), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(MandateCheckDigit aMandateCheckDigit, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandateCheckDigit.getBefImage(), aMandateCheckDigit);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aMandateCheckDigit.getUserDetails(),
				getOverideMap());
	}

	public void setMandateCheckDigitService(MandateCheckDigitService mandateCheckDigitService) {
		this.mandateCheckDigitService = mandateCheckDigitService;
	}

}
