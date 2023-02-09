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
 * * FileName : LegalPropertyTitleDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-06-2018 * *
 * Modified Date : 18-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legalpropertytitle;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.legal.LegalPropertyTitle;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.legal.legaldetail.LegalDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Legal/LegalPropertyTitle/legalPropertyTitleDialog.zul file. <br>
 */
public class LegalPropertyTitleDialogCtrl extends GFCBaseCtrl<LegalPropertyTitle> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalPropertyTitleDialogCtrl.class);

	protected Window window_LegalPropertyTitleDialog;
	protected Textbox title;

	private boolean newRecord = false;
	private boolean newPropertyTitles = false;
	private boolean enquiry = false;

	private LegalPropertyTitle legalPropertyTitle;
	private LegalDetailDialogCtrl legalDetailDialogCtrl;
	private List<LegalPropertyTitle> legalPropertyTitlesList;

	/**
	 * default constructor.<br>
	 */
	public LegalPropertyTitleDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalPropertyTitleDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.legalPropertyTitle.getLegalPropertyTitleId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalPropertyTitleDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_LegalPropertyTitleDialog);
		try {
			this.legalPropertyTitle = (LegalPropertyTitle) arguments.get("legalPropertyTitle");
			if (this.legalPropertyTitle == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			this.setLegalDetailDialogCtrl((LegalDetailDialogCtrl) arguments.get("legalDetailDialogCtrl"));
			setNewPropertyTitles(true);
			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}

			if (arguments.containsKey("enquiry")) {
				setEnquiry((boolean) arguments.get("enquiry"));
			}

			this.legalPropertyTitle.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			// Store the before image.
			LegalPropertyTitle legalPropertyTitle = new LegalPropertyTitle();
			BeanUtils.copyProperties(this.legalPropertyTitle, legalPropertyTitle);
			this.legalPropertyTitle.setBefImage(legalPropertyTitle);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalPropertyTitle.isWorkflow(), this.legalPropertyTitle.getWorkflowId(),
					this.legalPropertyTitle.getNextTaskId());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.legalPropertyTitle);
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
		this.title.setMaxlength(3000);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!isEnquiry()) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyTitleDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyTitleDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyTitleDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyTitleDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}

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
		doShowNotes(this.legalPropertyTitle);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.legalPropertyTitle.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param legalPropertyTitle
	 * 
	 */
	public void doWriteBeanToComponents(LegalPropertyTitle aLegalPropertyTitle) {
		logger.debug(Literal.ENTERING);
		this.title.setValue(aLegalPropertyTitle.getTitle());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLegalPropertyTitle
	 */
	public void doWriteComponentsToBean(LegalPropertyTitle aLegalPropertyTitle) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Property Details
		try {
			aLegalPropertyTitle.setTitle(this.title.getValue());
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
	 * @param legalPropertyTitle The entity that need to be render.
	 */
	public void doShowDialog(LegalPropertyTitle legalPropertyTitle) {
		logger.debug(Literal.LEAVING);

		if (legalPropertyTitle.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.title.focus();
		} else {
			if (isNewPropertyTitles()) {
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
			doWriteBeanToComponents(legalPropertyTitle);
			if (isNewPropertyTitles()) {
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
		if (!this.title.isReadonly()) {
			this.title
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LegalPropertyTitleDialog_Title.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		this.title.setConstraint("");
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

	protected boolean doCustomDelete(final LegalPropertyTitle aLegalPropertyTitle, String tranType) {
		if (isNewPropertyTitles()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = processDetails(aLegalPropertyTitle, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_LegalPropertyTitleDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getLegalDetailDialogCtrl() != null) {
					getLegalDetailDialogCtrl().doFillPropertyTitleDetails(this.legalPropertyTitlesList);
				}
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final LegalPropertyTitle aLegalPropertyTitle = new LegalPropertyTitle();
		BeanUtils.copyProperties(this.legalPropertyTitle, aLegalPropertyTitle);

		doDelete(aLegalPropertyTitle.getTitle(), aLegalPropertyTitle);

		logger.debug(Literal.LEAVING);
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
			ErrorControl.showErrorControl(this.window_LegalPropertyTitleDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.legalPropertyTitle.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("LegalPropertyTitleDialog_Title"), this.title);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.legalPropertyTitle.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewPropertyTitles()) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isNewPropertyTitles());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewPropertyTitles()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);
		readOnlyComponent(true, this.title);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.title.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final LegalPropertyTitle aLegalPropertyTitle = new LegalPropertyTitle();
		BeanUtils.copyProperties(this.legalPropertyTitle, aLegalPropertyTitle);
		boolean isNew = false;

		doRemoveValidation();

		doSetValidation();
		doWriteComponentsToBean(aLegalPropertyTitle);

		isNew = aLegalPropertyTitle.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLegalPropertyTitle.getRecordType())) {
				aLegalPropertyTitle.setVersion(aLegalPropertyTitle.getVersion() + 1);
				if (isNew) {
					aLegalPropertyTitle.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLegalPropertyTitle.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLegalPropertyTitle.setNewRecord(true);
				}
			}
		} else {
			if (isNewPropertyTitles()) {
				if (isNewRecord()) {
					aLegalPropertyTitle.setVersion(1);
					aLegalPropertyTitle.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aLegalPropertyTitle.getRecordType())) {
					aLegalPropertyTitle.setVersion(aLegalPropertyTitle.getVersion() + 1);
					aLegalPropertyTitle.setRecordType(PennantConstants.RCD_UPD);
					aLegalPropertyTitle.setNewRecord(true);
				}
				if (aLegalPropertyTitle.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aLegalPropertyTitle.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aLegalPropertyTitle.setVersion(aLegalPropertyTitle.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewPropertyTitles()) {
				AuditHeader auditHeader = processDetails(aLegalPropertyTitle, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_LegalPropertyTitleDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getLegalDetailDialogCtrl() != null) {
						getLegalDetailDialogCtrl().doFillPropertyTitleDetails(this.legalPropertyTitlesList);
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

	private AuditHeader processDetails(LegalPropertyTitle aLegalPropertyTitle, String tranType) {

		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(aLegalPropertyTitle, tranType);

		this.legalPropertyTitlesList = new ArrayList<>();
		List<LegalPropertyTitle> oldLegalPropertyTitleList = null;

		if (getLegalDetailDialogCtrl() != null) {
			oldLegalPropertyTitleList = getLegalDetailDialogCtrl().getPropertyTitleList();
		}

		if (oldLegalPropertyTitleList != null && !oldLegalPropertyTitleList.isEmpty()) {
			for (LegalPropertyTitle oldDetail : oldLegalPropertyTitleList) {

				if (oldDetail.getSeqNum() == aLegalPropertyTitle.getSeqNum()) {
					duplicateRecord = true;
				}

				if (duplicateRecord) {
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aLegalPropertyTitle.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aLegalPropertyTitle.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.legalPropertyTitlesList.add(aLegalPropertyTitle);
						} else if (aLegalPropertyTitle.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aLegalPropertyTitle.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aLegalPropertyTitle.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.legalPropertyTitlesList.add(aLegalPropertyTitle);
						} else if (aLegalPropertyTitle.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.legalPropertyTitlesList.add(oldDetail);
						}
					}
				} else {
					this.legalPropertyTitlesList.add(oldDetail);
				}
				duplicateRecord = false;
			}
		}

		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.legalPropertyTitlesList.add(aLegalPropertyTitle);
			recordAdded = true;
		}

		if (!recordAdded) {
			this.legalPropertyTitlesList.add(aLegalPropertyTitle);
		}
		return auditHeader;
	}

	private AuditHeader getAuditHeader(LegalPropertyTitle aLegalPropertyTitle, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLegalPropertyTitle.getBefImage(), aLegalPropertyTitle);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aLegalPropertyTitle.getUserDetails(),
				getOverideMap());
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LegalDetailDialogCtrl getLegalDetailDialogCtrl() {
		return legalDetailDialogCtrl;
	}

	public void setLegalDetailDialogCtrl(LegalDetailDialogCtrl legalDetailDialogCtrl) {
		this.legalDetailDialogCtrl = legalDetailDialogCtrl;
	}

	public boolean isNewPropertyTitles() {
		return newPropertyTitles;
	}

	public void setNewPropertyTitles(boolean newPropertyTitles) {
		this.newPropertyTitles = newPropertyTitles;
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}

}
