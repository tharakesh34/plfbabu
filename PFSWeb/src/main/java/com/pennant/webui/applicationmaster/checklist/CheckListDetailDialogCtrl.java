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
 * * FileName : CheckListDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * *
 * Modified Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.checklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/RMTMasters/CheckListDetail/checkListDetailDialog.zul file.
 */
public class CheckListDetailDialogCtrl extends GFCBaseCtrl<CheckListDetail> {
	private static final long serialVersionUID = 2164774289694537365L;
	private static final Logger logger = LogManager.getLogger(CheckListDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CheckListDetailDialog; // autoWired
	protected Longbox ansSeqNo; // autoWired
	protected Textbox ansDesc; // autoWired
	protected Textbox ansCond; // autoWired
	protected Checkbox remarkAllow; // autoWired
	protected Checkbox docRequired; // autoWired
	protected ExtendedCombobox docType; // autoWired
	protected Row row_DocType; // autoWired
	protected Checkbox remarkMand; // autoWired

	// not auto wired variables
	private CheckListDetail checkListDetail; // overHanded per parameter

	private transient boolean validationOn;

	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private CheckListDialogCtrl checkListDialogCtrl = null;
	private CheckList checkList;
	private boolean isNewRecord = false;
	private List<CheckListDetail> chkListDetailList;

	/**
	 * default constructor.<br>
	 */
	public CheckListDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CheckListDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CheckListDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CheckListDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CheckListDetailDialog);

		try {

			if (arguments.containsKey("checkListDetail")) {
				this.checkListDetail = (CheckListDetail) arguments.get("checkListDetail");
				CheckListDetail befImage = new CheckListDetail();
				BeanUtils.copyProperties(this.checkListDetail, befImage);
				this.checkListDetail.setBefImage(befImage);

				setCheckListDetail(this.checkListDetail);
			} else {
				setCheckListDetail(null);
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("checkList")) {
				this.checkList = (CheckList) arguments.get("checkList");
				setCheckList(this.checkList);
			}

			if (arguments.containsKey("checkListDialogCtrl")) {
				this.checkListDialogCtrl = (CheckListDialogCtrl) arguments.get("checkListDialogCtrl");
				setCheckListDialogCtrl(this.checkListDialogCtrl);
				this.checkListDetail.setWorkflowId(0);
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			doLoadWorkFlow(this.checkListDetail.isWorkflow(), this.checkListDetail.getWorkflowId(),
					this.checkListDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
			}
			// READ OVERHANDED parameters !
			// we get the checkListDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete checkListDetail here.

			// set Field Properties

			if (isWorkFlowEnabled()) {
				getUserWorkspace().allocateAuthorities("CheckListDetailDialog", getRole());
			} else {
				getUserWorkspace().allocateAuthorities("CheckListDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			doSetFieldProperties();
			doShowDialog(getCheckListDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CheckListDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.ansDesc.setMaxlength(100);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		this.docType.setTextBoxWidth(150);
		this.docType.setMandatoryStyle(true);
		this.docType.setModuleName("DocumentType");
		this.docType.setValueColumn("DocTypeCode");
		this.docType.setDescColumn("DocTypeDesc");
		this.docType.setValidateColumns(new String[] { "DocTypeCode" });

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
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CheckListDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CheckListDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CheckListDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CheckListDetailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CheckListDetailDialog);
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
	 * when the "checks" Remarks checkBox. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$remarkAllow(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		if (!this.remarkAllow.isChecked()) {
			this.remarkMand.setChecked(false);
			this.remarkMand.setDisabled(true);
		} else {
			this.remarkMand.setDisabled(false);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "checks" Doc Required checkBox. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$docRequired(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		onCheckDocRequire();
		logger.debug("Leaving " + event.toString());
	}

	private void onCheckDocRequire() {
		if (this.docRequired.isChecked()) {
			this.row_DocType.setVisible(true);
		} else {
			this.row_DocType.setVisible(false);
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.checkListDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCheckListDetail CheckListDetail
	 */
	public void doWriteBeanToComponents(CheckListDetail aCheckListDetail) {
		logger.debug("Entering");
		this.ansSeqNo.setValue(aCheckListDetail.getAnsSeqNo());
		this.ansDesc.setValue(aCheckListDetail.getAnsDesc());
		this.ansCond.setValue(aCheckListDetail.getAnsCond());
		this.remarkAllow.setChecked(aCheckListDetail.isRemarksAllow());
		this.docRequired.setDisabled(true);
		this.docRequired.setChecked(aCheckListDetail.isDocRequired());
		this.docType.setValue(aCheckListDetail.getDocType());
		this.docType.setDescription(StringUtils.trimToEmpty(aCheckListDetail.getLovDescDocType()));
		onCheckDocRequire();
		this.remarkMand.setChecked(aCheckListDetail.isRemarksMand());
		this.recordStatus.setValue(aCheckListDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCheckListDetail
	 */
	public void doWriteComponentsToBean(CheckListDetail aCheckListDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCheckListDetail.setAnsSeqNo(this.ansSeqNo.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setAnsDesc(this.ansDesc.getValue());
			aCheckListDetail.setLovDescCheckListDesc(this.checkList.getCheckListDesc());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setAnsCond("Condition");
			// aCheckListDetail.setAnsCond(this.ansCond.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setRemarksAllow(this.remarkAllow.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setDocRequired(this.docRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setLovDescDocType(this.docType.getDescription());
			aCheckListDetail.setDocType(this.docType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setRemarksMand(this.remarkMand.isChecked());
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

		aCheckListDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCheckListDetail
	 */
	public void doShowDialog(CheckListDetail aCheckListDetail) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCheckListDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ansDesc.focus();
		} else {
			this.ansDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnEdit.setVisible(false);
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCheckListDetail);

			this.window_CheckListDetailDialog.doModal();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CheckListDetailDialog.onClose();
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

		if (!this.ansDesc.isReadonly()) {
			this.ansDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CheckListDetailDialog_AnsDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.ansCond.isReadonly()) {
			this.ansCond.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CheckListDetailDialog_AnsRemarks.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.ansSeqNo.setConstraint("");
		this.ansDesc.setConstraint("");
		this.ansCond.setConstraint("");
		this.docType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		if (!this.docType.isReadonly() && this.docRequired.isChecked()) {
			this.docType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CheckListDetailDialog_DocType.value"), null, true, true));
		}

		logger.debug("Leaving");
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
		this.ansSeqNo.setErrorMessage("");
		this.ansDesc.setErrorMessage("");
		this.ansCond.setErrorMessage("");
		this.docType.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected boolean doCustomDelete(final CheckListDetail checkListDetail, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		AuditHeader auditHeader = newChkListDetailProcess(checkListDetail, tranType);
		auditHeader = ErrorControl.showErrorDetails(this.window_CheckListDetailDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			getCheckListDialogCtrl().doFillCheckListDetailsList(this.chkListDetailList);

			this.window_CheckListDetailDialog.onClose();
		}

		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CheckListDetail checkListDetail = new CheckListDetail();
		BeanUtils.copyProperties(getCheckListDetail(), checkListDetail);

		final String keyReference = checkListDetail.getAnsDesc();

		doDelete(keyReference, checkListDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCheckListDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.remarkMand.setDisabled(true);
			this.ansDesc.setReadonly(isReadOnly("CheckListDetailDialog_ansDesc"));
		} else {
			this.btnCancel.setVisible(true);
			this.ansDesc.setReadonly(true);
		}

		this.ansSeqNo.setReadonly(isReadOnly("CheckListDetailDialog_ansSeqNo"));
		this.ansCond.setReadonly(isReadOnly("CheckListDetailDialog_ansCond"));
		this.remarkAllow.setDisabled(isReadOnly("CheckListDetailDialog_remarksAllow"));
		if (getCheckListDetail() != null && getCheckListDetail().isDocRequired()) {
			this.docRequired.setDisabled(true);
			this.docRequired.setChecked(true);
		} else {
			this.docRequired.setChecked(isReadOnly("CheckListDetailDialog_docRequired"));
		}
		this.docType.setReadonly(isReadOnly("CheckListDetailDialog_docType"));
		if (this.remarkAllow.isChecked() && !isReadOnly("CheckListDetailDialog_remarksMand")
				&& !this.remarkAllow.isDisabled()) {
			this.remarkMand.setDisabled(false);
		} else {
			this.remarkMand.setDisabled(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.checkListDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (getCheckListDetail().isNewRecord()) {
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setBtnStatus_Edit();
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.ansSeqNo.setReadonly(true);
		this.ansDesc.setReadonly(true);
		this.ansCond.setReadonly(true);
		this.remarkAllow.setDisabled(true);
		this.docRequired.setDisabled(true);
		this.docType.setReadonly(true);
		this.remarkMand.setDisabled(true);

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

		this.ansSeqNo.setText("");
		this.ansDesc.setValue("");
		this.ansCond.setValue("");
		this.remarkAllow.setChecked(false);
		this.docRequired.setChecked(false);
		this.remarkMand.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CheckListDetail aCheckListDetail = new CheckListDetail();
		BeanUtils.copyProperties(getCheckListDetail(), aCheckListDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CheckListDetail object with the components data
		doWriteComponentsToBean(aCheckListDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCheckListDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCheckListDetail.getRecordType())) {
				aCheckListDetail.setVersion(aCheckListDetail.getVersion() + 1);
				if (isNew) {
					aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCheckListDetail.setNewRecord(true);
				}
			}
		} else {
			/* set the tranType according to RecordType */
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				aCheckListDetail.setVersion(1);
				aCheckListDetail.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aCheckListDetail.getRecordType())) {
				tranType = PennantConstants.TRAN_UPD;
				aCheckListDetail.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aCheckListDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			AuditHeader auditHeader = newChkListDetailProcess(aCheckListDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CheckListDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getCheckListDialogCtrl().doFillCheckListDetailsList(this.chkListDetailList);

				this.window_CheckListDetailDialog.onClose();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	private AuditHeader newChkListDetailProcess(CheckListDetail aCheckListDetail, String tranType) {
		logger.debug("Entering ");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCheckListDetail, tranType);
		chkListDetailList = new ArrayList<CheckListDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCheckListDetail.getAnsDesc());
		errParm[0] = PennantJavaUtil.getLabel("label_AnsDesc") + ":" + valueParm[0];

		if (getCheckListDetail().isNewRecord()) {
			long newSeqNo = 0;
			for (CheckListDetail aCheckList : getCheckListDialogCtrl().getChekListDetailsList()) {
				if (newSeqNo <= aCheckList.getAnsSeqNo()) {
					newSeqNo = aCheckList.getAnsSeqNo();
				}
				if (aCheckListDetail.getAnsDesc().equals(aCheckList.getAnsDesc())) {
					auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
							getUserWorkspace().getUserLanguage()));
					return auditHeader;
				}

			}
			newSeqNo = newSeqNo + 1;
			aCheckListDetail.setAnsSeqNo(newSeqNo);
		}

		if (getCheckListDialogCtrl().getChekListDetailsList() != null
				&& !getCheckListDialogCtrl().getChekListDetailsList().isEmpty()) {
			for (int i = 0; i < getCheckListDialogCtrl().getChekListDetailsList().size(); i++) {
				CheckListDetail checkListDetail = getCheckListDialogCtrl().getChekListDetailsList().get(i);
				if (aCheckListDetail.getAnsSeqNo() == (checkListDetail.getAnsSeqNo())) {
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							chkListDetailList.add(aCheckListDetail);
						} else if (aCheckListDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							chkListDetailList.add(aCheckListDetail);
						} else if (aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCheckListDialogCtrl().getChekListDetailsList().size(); j++) {
								CheckListDetail chkDetail = getCheckListDialogCtrl().getChekListDetailsList().get(j);
								if (aCheckListDetail.getAnsDesc().trim()
										.equalsIgnoreCase(checkListDetail.getAnsDesc().trim())) {
									chkListDetailList.add(chkDetail);
								}
							}
						} else if (aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							if (this.checkList != null && this.checkList.isWorkflow()) {
								aCheckListDetail.setNewRecord(true);
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							chkListDetailList.add(checkListDetail);
						}
					}
				} else {
					chkListDetailList.add(checkListDetail);
				}
			}
		}
		if (!recordAdded) {
			chkListDetailList.add(aCheckListDetail);
		}
		return auditHeader;
	}

	// WorkFlow Components

	/**
	 * @param aCheckListDetail
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CheckListDetail aCheckListDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCheckListDetail.getBefImage(), aCheckListDetail);
		return new AuditHeader(String.valueOf(aCheckListDetail.getCheckListId()), null, null, null, auditDetail,
				aCheckListDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.checkList);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.checkList.getCheckListId());
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

	public CheckListDetail getCheckListDetail() {
		return this.checkListDetail;
	}

	public void setCheckListDetail(CheckListDetail checkListDetail) {
		this.checkListDetail = checkListDetail;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public CheckListDialogCtrl getCheckListDialogCtrl() {
		return checkListDialogCtrl;
	}

	public void setCheckListDialogCtrl(CheckListDialogCtrl checkListDialogCtrl) {
		this.checkListDialogCtrl = checkListDialogCtrl;
	}

	public CheckList getCheckList() {
		return checkList;
	}

	public void setCheckList(CheckList checkList) {
		this.checkList = checkList;
	}

	public boolean isNewRecord() {
		return isNewRecord;
	}

	public void setNewRecord(boolean isNewRecord) {
		this.isNewRecord = isNewRecord;
	}

	public void setChkListDetailList(List<CheckListDetail> chkListDetailList) {
		this.chkListDetailList = chkListDetailList;
	}

	public List<CheckListDetail> getChkListDetailList() {
		return chkListDetailList;
	}
}
