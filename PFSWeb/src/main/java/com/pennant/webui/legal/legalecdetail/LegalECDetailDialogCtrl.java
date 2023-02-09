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
 * * FileName : LegalECDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-06-2018 * *
 * Modified Date : 19-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legalecdetail;

import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ibm.icu.text.SimpleDateFormat;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.legal.LegalECDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.legal.legaldetail.LegalDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LegalECDetailDialogCtrl extends GFCBaseCtrl<LegalECDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalECDetailDialogCtrl.class);

	protected Window window_LegalECDetailDialog;
	protected Datebox ecDate;
	protected Textbox document;

	private boolean enquiry = false;
	private boolean newRecord = false;
	private boolean newLegalECDetails = false;

	private LegalECDetail legalECDetail;
	private LegalDetailDialogCtrl legalDetailDialogCtrl;
	private List<LegalECDetail> legalEcdDetailsList;

	protected Groupbox gb_ecAdditinalDetails;
	protected Textbox ecNumber;
	protected Datebox ecFrom;
	protected Datebox ecTo;
	protected Combobox ecType;
	private List<ValueLabel> listEcTypes = PennantStaticListUtil.getEcTypes();

	/**
	 * default constructor.<br>
	 */
	public LegalECDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalECDetailDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.legalECDetail.getLegalECId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalECDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LegalECDetailDialog);

		try {
			// Get the required arguments.
			this.legalECDetail = (LegalECDetail) arguments.get("legalECDetail");
			if (this.legalECDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			this.setLegalDetailDialogCtrl((LegalDetailDialogCtrl) arguments.get("legalDetailDialogCtrl"));
			setNewLegalECDetails(true);
			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			if (arguments.containsKey("enquiry")) {
				setEnquiry((boolean) arguments.get("enquiry"));
			}

			this.legalECDetail.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}
			// Store the before image.
			LegalECDetail legalECDetail = new LegalECDetail();
			BeanUtils.copyProperties(this.legalECDetail, legalECDetail);
			this.legalECDetail.setBefImage(legalECDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalECDetail.isWorkflow(), this.legalECDetail.getWorkflowId(),
					this.legalECDetail.getNextTaskId());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.legalECDetail);
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
		this.ecDate.setFormat(PennantConstants.dateFormat);
		this.document.setMaxlength(1000);

		this.ecFrom.setFormat(PennantConstants.dateFormat);
		this.ecTo.setFormat(PennantConstants.dateFormat);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!isEnquiry()) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalECDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalECDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalECDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalECDetailDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}
		this.btnSave.setVisible(true);
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
		doShowNotes(this.legalECDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.legalECDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param legalECDetail
	 * 
	 */
	public void doWriteBeanToComponents(LegalECDetail aLegalECDetail) {
		logger.debug(Literal.ENTERING);

		this.ecDate.setValue(aLegalECDetail.getEcDate());
		this.document.setValue(aLegalECDetail.getDocument());
		this.ecNumber.setValue(aLegalECDetail.getEcNumber());
		this.ecFrom.setValue(aLegalECDetail.getEcFrom());
		this.ecTo.setValue(aLegalECDetail.getEcTo());
		this.ecType.setValue(aLegalECDetail.getEcType());
		fillComboBox(this.ecType, aLegalECDetail.getEcType(), listEcTypes, "");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLegalECDetail
	 */
	public void doWriteComponentsToBean(LegalECDetail aLegalECDetail) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Ec Date
		try {
			aLegalECDetail.setEcDate(this.ecDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Document
		try {
			aLegalECDetail.setDocument(this.document.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalECDetail.setEcNumber(this.ecNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalECDetail.setEcFrom(this.ecFrom.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalECDetail.setEcTo(this.ecTo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.gb_ecAdditinalDetails.isVisible() && this.ecTo.getValue().before(this.ecFrom.getValue())) {
				throw new WrongValueException(this.ecTo, "EC To date should not before EC Start Date.");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalECDetail.setEcType(this.ecType.getSelectedItem().getValue().toString());
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
	 * @param legalECDetail The entity that need to be render.
	 */
	public void doShowDialog(LegalECDetail legalECDetail) {
		logger.debug(Literal.LEAVING);

		if (legalECDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.ecDate.focus();
		} else {
			if (isNewLegalECDetails()) {
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
			doWriteBeanToComponents(legalECDetail);
			if (isNewLegalECDetails()) {
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
		logger.debug(Literal.ENTERING);
		if (!this.ecDate.isReadonly()) {
			this.ecDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_LegalECDetailDialog_EcDate.value"), false));
		}
		if (!this.document.isReadonly()) {
			this.document
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LegalECDetailDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		if (!this.ecNumber.isReadonly() && this.gb_ecAdditinalDetails.isVisible()) {
			this.ecNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalECDetailDialog_EcNumber.value"), null, true));
		}

		if (!this.ecFrom.isDisabled() && this.gb_ecAdditinalDetails.isVisible()) {
			this.ecFrom.setConstraint(
					new PTDateValidator(Labels.getLabel("label_LegalECDetailDialog_EcPeriodFrom.value"), true));
		}

		if (!this.ecTo.isDisabled() && this.gb_ecAdditinalDetails.isVisible()) {
			this.ecTo.setConstraint(
					new PTDateValidator(Labels.getLabel("label_LegalECDetailDialog_EcPeriodTo.value"), true));
		}

		if (!this.ecType.isDisabled() && this.gb_ecAdditinalDetails.isVisible()) {
			this.ecType.setConstraint(
					new PTListValidator(Labels.getLabel("label_LegalECDetailDialog_EcType.value"), listEcTypes, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.ecDate.setConstraint("");
		this.document.setConstraint("");
		this.ecNumber.setConstraint("");
		this.ecFrom.setConstraint("");
		this.ecTo.setConstraint("");
		this.ecType.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		this.ecFrom.setErrorMessage("");
		this.ecTo.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	protected boolean doCustomDelete(final LegalECDetail aLegalECDetail, String tranType) {
		if (isNewLegalECDetails()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = processDetails(aLegalECDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_LegalECDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getLegalDetailDialogCtrl() != null) {
					getLegalDetailDialogCtrl().doFillECDDetails(this.legalEcdDetailsList);
				}
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final LegalECDetail aLegalECDetail = new LegalECDetail();
		BeanUtils.copyProperties(this.legalECDetail, aLegalECDetail);

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = aLegalECDetail.getEcDate();
		String ecDate = df.format(date);

		doDelete(ecDate, aLegalECDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.legalECDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("LegalECDetailDialog_EcDate"), this.ecDate);
		readOnlyComponent(isReadOnly("LegalECDetailDialog_Remarks"), this.document);

		this.gb_ecAdditinalDetails
				.setVisible(getUserWorkspace().isAllowed("LegalECDetailDialog_gb_ecAdditinalDetails"));
		readOnlyComponent(isReadOnly("LegalECDetailDialog_EcNumber"), this.ecNumber);
		readOnlyComponent(isReadOnly("LegalECDetailDialog_EcPeriodFrom"), this.ecFrom);
		readOnlyComponent(isReadOnly("LegalECDetailDialog_EcPeriodTo"), this.ecTo);
		readOnlyComponent(isReadOnly("LegalECDetailDialog_EcType"), this.ecType);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.legalECDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewLegalECDetails()) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isNewLegalECDetails());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewLegalECDetails()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);
		readOnlyComponent(true, this.ecDate);
		readOnlyComponent(true, this.document);
		readOnlyComponent(true, this.ecNumber);
		readOnlyComponent(true, this.ecFrom);
		readOnlyComponent(true, this.ecTo);
		readOnlyComponent(true, this.ecType);

		if (PennantConstants.YES.equals(SysParamUtil.getValueAsString("LEGAL_DETAIL_ADDITIONAL_FIELDS_ENQUIRY"))) {
			this.gb_ecAdditinalDetails.setVisible(true);
		} else {
			this.gb_ecAdditinalDetails.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.ecDate.setText("");
		this.document.setValue("");
		this.ecNumber.setValue("");
		this.ecFrom.setText("");
		this.ecTo.setText("");
		this.ecType.setSelectedIndex(0);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final LegalECDetail aLegalECDetail = new LegalECDetail();
		BeanUtils.copyProperties(this.legalECDetail, aLegalECDetail);
		boolean isNew = false;

		doRemoveValidation();
		doSetValidation();
		doWriteComponentsToBean(aLegalECDetail);

		isNew = aLegalECDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLegalECDetail.getRecordType())) {
				aLegalECDetail.setVersion(aLegalECDetail.getVersion() + 1);
				if (isNew) {
					aLegalECDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLegalECDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLegalECDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewLegalECDetails()) {
				if (isNewRecord()) {
					aLegalECDetail.setVersion(1);
					aLegalECDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aLegalECDetail.getRecordType())) {
					aLegalECDetail.setVersion(aLegalECDetail.getVersion() + 1);
					aLegalECDetail.setRecordType(PennantConstants.RCD_UPD);
					aLegalECDetail.setNewRecord(true);
				}
				if (aLegalECDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aLegalECDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aLegalECDetail.setVersion(aLegalECDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewLegalECDetails()) {
				AuditHeader auditHeader = processDetails(aLegalECDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_LegalECDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getLegalDetailDialogCtrl() != null) {
						getLegalDetailDialogCtrl().doFillECDDetails(this.legalEcdDetailsList);
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
			ErrorControl.showErrorControl(this.window_LegalECDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	private AuditHeader processDetails(LegalECDetail aLegalECDetail, String tranType) {
		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(aLegalECDetail, tranType);

		this.legalEcdDetailsList = new ArrayList<>();
		List<LegalECDetail> oldLegalECDetailsList = null;

		if (getLegalDetailDialogCtrl() != null) {
			oldLegalECDetailsList = getLegalDetailDialogCtrl().getEcdDetailList();
		}

		if (oldLegalECDetailsList != null && !oldLegalECDetailsList.isEmpty()) {
			for (LegalECDetail oldLegalDetail : oldLegalECDetailsList) {
				if (oldLegalDetail.getSeqNum() == aLegalECDetail.getSeqNum()) {
					duplicateRecord = true;
				}

				if (duplicateRecord) {
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aLegalECDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aLegalECDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.legalEcdDetailsList.add(aLegalECDetail);
						} else if (aLegalECDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aLegalECDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aLegalECDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.legalEcdDetailsList.add(aLegalECDetail);
						} else if (aLegalECDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.legalEcdDetailsList.add(oldLegalDetail);
						}
					}
				} else {
					this.legalEcdDetailsList.add(oldLegalDetail);
				}
				duplicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.legalEcdDetailsList.add(aLegalECDetail);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.legalEcdDetailsList.add(aLegalECDetail);
		}
		return auditHeader;
	}

	private AuditHeader getAuditHeader(LegalECDetail aLegalECDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLegalECDetail.getBefImage(), aLegalECDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aLegalECDetail.getUserDetails(),
				getOverideMap());
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewLegalECDetails() {
		return newLegalECDetails;
	}

	public void setNewLegalECDetails(boolean newLegalECDetails) {
		this.newLegalECDetails = newLegalECDetails;
	}

	public LegalECDetail getLegalECDetail() {
		return legalECDetail;
	}

	public void setLegalECDetail(LegalECDetail legalECDetail) {
		this.legalECDetail = legalECDetail;
	}

	public LegalDetailDialogCtrl getLegalDetailDialogCtrl() {
		return legalDetailDialogCtrl;
	}

	public void setLegalDetailDialogCtrl(LegalDetailDialogCtrl legalDetailDialogCtrl) {
		this.legalDetailDialogCtrl = legalDetailDialogCtrl;
	}

}
