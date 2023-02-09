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
 * * FileName : LegalNoteDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-06-2018 * * Modified
 * Date : 19-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legalnote;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.legal.LegalNote;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.legal.legaldetail.LegalDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Legal/LegalNote/legalNoteDialog.zul file. <br>
 */
public class LegalNoteDialogCtrl extends GFCBaseCtrl<LegalNote> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalNoteDialogCtrl.class);

	protected Window window_LegalNoteDialog;
	protected Combobox code;
	protected Textbox description;

	private boolean newRecord = false;
	private boolean newLegalNotes = false;
	private boolean enquiry = false;

	private LegalNote legalNote;
	private LegalDetailDialogCtrl legalDetailDialogCtrl;

	private List<LegalNote> legalNotesList;
	private List<ValueLabel> listPrioritys = PennantStaticListUtil.getCustomerEmailPriority();

	/**
	 * default constructor.<br>
	 */
	public LegalNoteDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalNoteDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.legalNote.getLegalNoteId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalNoteDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_LegalNoteDialog);
		try {
			this.legalNote = (LegalNote) arguments.get("legalNote");
			if (this.legalNote == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			this.setLegalDetailDialogCtrl((LegalDetailDialogCtrl) arguments.get("legalDetailDialogCtrl"));
			setNewLegalNotes(true);
			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}

			if (arguments.containsKey("enquiry")) {
				setEnquiry((boolean) arguments.get("enquiry"));
			}

			this.legalNote.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			// Store the before image.
			LegalNote legalNote = new LegalNote();
			BeanUtils.copyProperties(this.legalNote, legalNote);
			this.legalNote.setBefImage(legalNote);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalNote.isWorkflow(), this.legalNote.getWorkflowId(), this.legalNote.getNextTaskId());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.legalNote);
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
		fillComboBox(code, "", listPrioritys, "");
		this.description.setMaxlength(1000);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!isEnquiry()) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalNoteDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalNoteDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalNoteDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalNoteDialog_btnSave"));
		}
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.legalNote);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.legalNote.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param legalNote
	 * 
	 */
	public void doWriteBeanToComponents(LegalNote aLegalNote) {
		logger.debug(Literal.ENTERING);
		this.code.setValue(aLegalNote.getCode());
		this.description.setValue(aLegalNote.getDescription());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLegalNote
	 */
	public void doWriteComponentsToBean(LegalNote aLegalNote) {
		logger.debug(Literal.LEAVING);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// Code
		try {
			aLegalNote.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aLegalNote.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
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
	 * @param legalNote The entity that need to be render.
	 */
	public void doShowDialog(LegalNote legalNote) {
		logger.debug(Literal.LEAVING);

		if (legalNote.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.description.focus();
		} else {
			if (isNewLegalNotes()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			doWriteBeanToComponents(legalNote);
			if (isNewLegalNotes()) {
				this.groupboxWf.setVisible(false);
			}

			if (isEnquiry()) {
				this.btnCtrl.setBtnStatus_Enquiry();
				this.btnNotes.setVisible(false);
				doReadOnly();
			}
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(
					new StaticListValidator(listPrioritys, Labels.getLabel("label_LegalNoteDialog_Code.value")));
		}

		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LegalNoteDialog_Description.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		this.code.setConstraint("");
		this.description.setConstraint("");
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

	protected boolean doCustomDelete(final LegalNote aLegalNote, String tranType) {
		if (isNewLegalNotes()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = processDetails(aLegalNote, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_LegalNoteDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getLegalDetailDialogCtrl() != null) {
					getLegalDetailDialogCtrl().doFillLegalNotesDetails(this.legalNotesList);
				}
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final LegalNote aLegalNote = new LegalNote();
		BeanUtils.copyProperties(this.legalNote, aLegalNote);

		doDelete(String.valueOf(aLegalNote.getLegalNoteId()), aLegalNote);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		if (this.legalNote.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("LegalNoteDialog_Code"), this.code);
		readOnlyComponent(isReadOnly("LegalNoteDialog_Description"), this.description);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.legalNote.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewLegalNotes()) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isNewLegalNotes());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewLegalNotes()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.description);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.code.setValue("");
		this.description.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final LegalNote aLegalNote = new LegalNote();
		BeanUtils.copyProperties(this.legalNote, aLegalNote);
		boolean isNew = false;

		doRemoveValidation();

		doSetValidation();
		doWriteComponentsToBean(aLegalNote);

		isNew = aLegalNote.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLegalNote.getRecordType())) {
				aLegalNote.setVersion(aLegalNote.getVersion() + 1);
				if (isNew) {
					aLegalNote.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLegalNote.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLegalNote.setNewRecord(true);
				}
			}
		} else {
			if (isNewLegalNotes()) {
				if (isNewRecord()) {
					aLegalNote.setVersion(1);
					aLegalNote.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aLegalNote.getRecordType())) {
					aLegalNote.setVersion(aLegalNote.getVersion() + 1);
					aLegalNote.setRecordType(PennantConstants.RCD_UPD);
					aLegalNote.setNewRecord(true);
				}
				if (aLegalNote.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aLegalNote.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aLegalNote.setVersion(aLegalNote.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewLegalNotes()) {
				AuditHeader auditHeader = processDetails(aLegalNote, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_LegalNoteDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getLegalDetailDialogCtrl() != null) {
						getLegalDetailDialogCtrl().doFillLegalNotesDetails(this.legalNotesList);
					}
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_LegalNoteDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	private AuditHeader processDetails(LegalNote aLegalNote, String tranType) {

		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(aLegalNote, tranType);

		this.legalNotesList = new ArrayList<>();
		List<LegalNote> oldLegalNoteList = null;

		if (getLegalDetailDialogCtrl() != null) {
			oldLegalNoteList = getLegalDetailDialogCtrl().getLegalNotesList();
		}

		if (oldLegalNoteList != null && !oldLegalNoteList.isEmpty()) {
			for (LegalNote oldLegalNotes : oldLegalNoteList) {

				if (oldLegalNotes.getSeqNum() == aLegalNote.getSeqNum()) {
					duplicateRecord = true;
				}

				if (duplicateRecord) {
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aLegalNote.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aLegalNote.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.legalNotesList.add(aLegalNote);
						} else if (aLegalNote.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aLegalNote.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aLegalNote.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.legalNotesList.add(aLegalNote);
						} else if (aLegalNote.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.legalNotesList.add(oldLegalNotes);
						}
					}
				} else {
					this.legalNotesList.add(oldLegalNotes);
				}
				duplicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.legalNotesList.add(aLegalNote);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.legalNotesList.add(aLegalNote);
		}
		return auditHeader;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(LegalNote aLegalNote, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLegalNote.getBefImage(), aLegalNote);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aLegalNote.getUserDetails(),
				getOverideMap());
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewLegalNotes() {
		return newLegalNotes;
	}

	public void setNewLegalNotes(boolean newLegalNotes) {
		this.newLegalNotes = newLegalNotes;
	}

	public LegalNote getLegalNote() {
		return legalNote;
	}

	public void setLegalNote(LegalNote legalNote) {
		this.legalNote = legalNote;
	}

	public LegalDetailDialogCtrl getLegalDetailDialogCtrl() {
		return legalDetailDialogCtrl;
	}

	public void setLegalDetailDialogCtrl(LegalDetailDialogCtrl legalDetailDialogCtrl) {
		this.legalDetailDialogCtrl = legalDetailDialogCtrl;
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}
}
