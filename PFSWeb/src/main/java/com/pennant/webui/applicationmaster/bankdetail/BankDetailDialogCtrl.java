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
 * * FileName : BankDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.bankdetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/BankDetail /bankDetailDialog.zul file.
 */
public class BankDetailDialogCtrl extends GFCBaseCtrl<BankDetail> {
	private static final long serialVersionUID = -2489293301745014852L;
	private static final Logger logger = LogManager.getLogger(BankDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BankDetailDialog; // autoWired

	protected Textbox bankCode; // autoWired
	protected Textbox bankName; // autoWired
	protected Checkbox active; // autoWired
	protected Intbox accNoLength;
	protected Intbox minAccNoLength;
	protected Textbox bankShortCode;
	protected Checkbox allowMultipleIFSC; // autoWired
	protected Checkbox cheque;
	protected Checkbox dd;
	protected Checkbox ecs;
	protected Checkbox nach;
	protected Checkbox dda;
	protected Checkbox eMandate;
	protected Checkbox updateBranches;
	protected Textbox allowedSources;
	protected Button btnMultiSource;
	protected Groupbox gb_instrumenttypes;
	// not autoWired variables
	private BankDetail bankDetail; // overHanded per parameter
	private transient BankDetailListCtrl bankDetailListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	private boolean isSkip = false;

	// ServiceDAOs / Domain Classes
	private transient BankDetailService bankDetailService;

	/**
	 * default constructor.<br>
	 */
	public BankDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BankDetailDialog";
	}

	public void onCreate$window_BankDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_BankDetailDialog);

		try {
			doCheckRights();

			if (arguments.containsKey("bankDetail")) {
				this.bankDetail = (BankDetail) arguments.get("bankDetail");
				BankDetail befImage = new BankDetail();
				BeanUtils.copyProperties(this.bankDetail, befImage);
				this.bankDetail.setBefImage(befImage);
				setBankDetail(this.bankDetail);
			} else {
				setBankDetail(null);
			}

			doLoadWorkFlow(this.bankDetail.isWorkflow(), this.bankDetail.getWorkflowId(),
					this.bankDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "BankDetailDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			if (arguments.containsKey("bankDetailListCtrl")) {
				setBankDetailListCtrl((BankDetailListCtrl) arguments.get("bankDetailListCtrl"));
			} else {
				setBankDetailListCtrl(null);
			}

			doSetFieldProperties();
			doShowDialog(getBankDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BankDetailDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.bankCode.setMaxlength(8);
		this.bankName.setMaxlength(50);
		this.accNoLength.setMaxlength(2);
		this.minAccNoLength.setMaxlength(2);
		this.bankShortCode.setMaxlength(20);
		// this.updateBranches.setChecked(true);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		if (this.eMandate.isChecked()) {
			this.allowedSources.setReadonly(false);
			this.btnMultiSource.setVisible(true);
		} else {
			this.allowedSources.setDisabled(true);
			this.btnMultiSource.setVisible(false);
			this.allowedSources.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BankDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BankDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BankDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BankDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, window_BankDetailDialog);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.bankDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(BankDetail aBankDetail) {
		logger.debug(Literal.ENTERING);
		this.bankCode.setValue(aBankDetail.getBankCode());
		this.bankName.setValue(aBankDetail.getBankName());
		this.active.setChecked(aBankDetail.isActive());
		this.accNoLength.setValue(aBankDetail.getAccNoLength());
		this.minAccNoLength.setValue(aBankDetail.getMinAccNoLength());
		this.bankShortCode.setValue(aBankDetail.getBankShortCode());
		this.recordStatus.setValue(aBankDetail.getRecordStatus());
		this.allowMultipleIFSC.setChecked(aBankDetail.isAllowMultipleIFSC());
		this.nach.setChecked(aBankDetail.isNach());
		this.ecs.setChecked(aBankDetail.isEcs());
		this.dd.setChecked(aBankDetail.isDd());
		this.cheque.setChecked(aBankDetail.isCheque());
		this.dda.setChecked(aBankDetail.isDda());
		this.eMandate.setChecked(aBankDetail.isEmandate());
		this.allowedSources.setValue(aBankDetail.getAllowedSources());
		if (aBankDetail.isNewRecord()) {
			this.updateBranches.setChecked(true);
		} else {
			this.updateBranches.setChecked(aBankDetail.isUpdateBranches());
		}

		if (aBankDetail.isNewRecord() || (aBankDetail.getRecordType() != null ? aBankDetail.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(BankDetail aBankDetail) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		List<WrongValueException> wve = new ArrayList<>();

		try {
			aBankDetail.setBankCode(this.bankCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBankDetail.setBankName(this.bankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBankDetail.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aBankDetail.setAllowMultipleIFSC(this.allowMultipleIFSC.isChecked());

		try {

			if (this.accNoLength.getValue() == null) {
				throw new WrongValueException(this.accNoLength, Labels.getLabel("FIELD_IS_GREATER", new String[] {
						Labels.getLabel("label_BankDetailDialog_AccNoLength.value"), "minAccNoLength" }));
			} else if (this.accNoLength.getValue() != 0
					&& this.accNoLength.getValue() < this.minAccNoLength.getValue()) {
				throw new WrongValueException(this.accNoLength,
						Labels.getLabel("FIELD_IS_GREATER",
								new String[] { Labels.getLabel("label_BankDetailDialog_AccNoLength.value"),
										Labels.getLabel("label_BankDetailDialog_MinimumAccNoLength.value") }));
			}

			aBankDetail.setAccNoLength(this.accNoLength.getValue());

			if (this.minAccNoLength.getValue() == null) {
				throw new WrongValueException(this.minAccNoLength,
						Labels.getLabel("FIELD_IS_LESSER", new String[] { Long.toString(this.minAccNoLength.getValue()),
								Long.toString(this.minAccNoLength.getValue()) }));
			} else if (aBankDetail.getMinAccNoLength() != 0
					&& aBankDetail.getAccNoLength() < aBankDetail.getMinAccNoLength()) {
				throw new WrongValueException(this.accNoLength,
						Labels.getLabel("FIELD_IS_LESSER",
								new String[] { Labels.getLabel("label_BankDetailDialog_AccNoLength.value"),
										Labels.getLabel("label_BankDetailDialog_MinimumAccNoLength.value") }));
			} else if (this.minAccNoLength.getValue() != 0
					&& this.minAccNoLength.getValue() > this.minAccNoLength.getValue()) {
				throw new WrongValueException(this.minAccNoLength,
						Labels.getLabel("FIELD_IS_LESSER", new String[] { Long.toString(this.minAccNoLength.getValue()),
								Long.toString(this.minAccNoLength.getValue()) }));
			}

			aBankDetail.setMinAccNoLength(this.minAccNoLength.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBankDetail.setBankShortCode(this.bankShortCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBankDetail.setNach(this.nach.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankDetail.setEcs(this.ecs.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankDetail.setDd(this.dd.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankDetail.setDda(this.dda.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankDetail.setCheque(this.cheque.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBankDetail.setEmandate(this.eMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.btnMultiSource.isVisible() && StringUtils.isBlank(this.allowedSources.getValue())) {
				throw new WrongValueException(this.btnMultiSource,
						Labels.getLabel("label_BankDetailDialog_AllowedSources.value") + " is Mandatory");
			}
			aBankDetail.setAllowedSources(this.allowedSources.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankDetail.setUpdateBranches(this.updateBranches.isChecked());
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

		aBankDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(BankDetail aBankDetail) {
		logger.debug(Literal.ENTERING);

		if (aBankDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.bankCode.focus();

			this.updateBranches.setChecked(true);

		} else {
			if (isWorkFlowEnabled()) {
				this.bankName.focus();
				if (StringUtils.isNotBlank(aBankDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
					// this.updateBranches.setChecked(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			doWriteBeanToComponents(aBankDetail);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BankDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(true);

		if (!this.bankCode.isReadonly()) {
			this.bankCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BankDetailDialog_BankCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.bankName.isReadonly()) {
			this.bankName.setConstraint(new PTStringValidator(Labels.getLabel("label_BankDetailDialog_BankName.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.bankShortCode.isReadonly()) {
			this.bankShortCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BankDetailDialog_BankShortCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.minAccNoLength.isReadonly()) {
			this.minAccNoLength.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_BankDetailDialog_MinimumAccNoLength.value"), true, false, 0));
		}

		if (!this.accNoLength.isReadonly()) {
			this.accNoLength.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_BankDetailDialog_AccNoLength.value"), true, false, 0));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		this.bankCode.setConstraint("");
		this.bankName.setConstraint("");
		this.accNoLength.setConstraint("");
		this.minAccNoLength.setConstraint("");
		this.bankShortCode.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected void doClearMessage() {

		this.bankCode.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.accNoLength.setErrorMessage("");
		this.minAccNoLength.setErrorMessage("");
		this.bankShortCode.setErrorMessage("");

	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final BankDetail aBankDetail = new BankDetail();
		BeanUtils.copyProperties(getBankDetail(), aBankDetail);

		doDelete(Labels.getLabel("label_BankDetailDialog_BankCode.value") + " : " + aBankDetail.getBankCode(),
				aBankDetail);

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (getBankDetail().isNewRecord()) {
			this.bankCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.bankCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.bankName.setReadonly(isReadOnly("BankDetailDialog_bankName"));
		this.active.setDisabled(isReadOnly("BankDetailDialog_active"));
		this.accNoLength.setReadonly(isReadOnly("BankDetailDialog_accNoLength"));
		this.minAccNoLength.setReadonly(isReadOnly("BankDetailDialog_accNoLength"));
		this.bankShortCode.setReadonly(isReadOnly("BankDetailDialog_bankShortCode"));
		this.allowMultipleIFSC.setDisabled(isReadOnly("BankDetailDialog_allowMultipleIFSC"));

		readOnlyComponent(isReadOnly("BankDetailDialog_ECS"), this.ecs);
		readOnlyComponent(isReadOnly("BankDetailDialog_DDA"), this.dda);
		readOnlyComponent(isReadOnly("BankDetailDialog_DD"), this.dd);
		readOnlyComponent(isReadOnly("BankDetailDialog_NACH"), this.nach);
		readOnlyComponent(isReadOnly("BankDetailDialog_Cheque"), this.cheque);
		readOnlyComponent(isReadOnly("BankDetailDialog_Emandate"), this.eMandate);
		readOnlyComponent(isReadOnly("BankDetailDialog_AllowedSources"), this.allowedSources);
		this.btnMultiSource.setDisabled(isReadOnly("button_BankDetailDialog_btnMultiSource"));
		readOnlyComponent(isReadOnly("BankDetailDialog_UpdateBranches"), this.updateBranches);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.bankDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug(Literal.LEAVING);
	}

	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.bankCode.setReadonly(true);
		this.bankName.setReadonly(true);
		this.active.setDisabled(true);
		this.accNoLength.setReadonly(true);
		this.minAccNoLength.setReadonly(true);
		this.bankShortCode.setReadonly(true);
		this.allowMultipleIFSC.setDisabled(true);
		this.dd.setDisabled(true);
		this.nach.setDisabled(true);
		this.dda.setDisabled(true);
		this.cheque.setDisabled(true);
		this.ecs.setDisabled(true);
		this.eMandate.setDisabled(true);
		this.allowedSources.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.bankCode.setValue("");
		this.bankName.setValue("");
		this.active.setChecked(false);
		this.accNoLength.setValue(0);
		this.minAccNoLength.setValue(0);
		this.bankShortCode.setValue("");
		this.allowMultipleIFSC.setChecked(false);
		this.nach.setChecked(false);
		this.ecs.setChecked(false);
		this.dd.setChecked(false);
		this.dda.setChecked(false);
		this.cheque.setChecked(false);
		this.active.setChecked(false);
		this.eMandate.setChecked(false);
		this.allowedSources.setValue("");
		logger.debug(Literal.LEAVING);
	}

	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final BankDetail aBankDetail = new BankDetail();
		BeanUtils.copyProperties(getBankDetail(), aBankDetail);
		boolean isNew = false;

		doSetValidation();

		isNew = aBankDetail.isNewRecord();
		if (this.userAction.getSelectedItem() != null) {
			if ((!isNew) && (PennantConstants.RCD_STATUS_SUBMITTED
					.equalsIgnoreCase(this.userAction.getSelectedItem().getValue())
					|| PennantConstants.RCD_STATUS_SAVED
							.equalsIgnoreCase(this.userAction.getSelectedItem().getValue()))) {
				if (this.updateBranches.isChecked()) {
					this.isSkip = false;
					MessageUtil.confirm("Are you sure, want to Update the Instrument Types in Bank Branches ?",
							event -> {
								if (Messagebox.ON_NO.equals(event.getName())) {
									this.isSkip = true;
								}
							});

					if (this.isSkip) {
						return;
					}
				}
			}
		}

		doWriteComponentsToBean(aBankDetail);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBankDetail.getRecordType())) {
				aBankDetail.setVersion(aBankDetail.getVersion() + 1);
				if (isNew) {
					aBankDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBankDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBankDetail.setNewRecord(true);
				}
			}
		} else {
			aBankDetail.setVersion(aBankDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {

			if (doProcess(aBankDetail, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(BankDetail aBankDetail, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBankDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBankDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBankDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBankDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBankDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBankDetail);
				}

				if (isNotesMandatory(taskId, aBankDetail)) {
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

			aBankDetail.setTaskId(taskId);
			aBankDetail.setNextTaskId(nextTaskId);
			aBankDetail.setRoleCode(getRole());
			aBankDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBankDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aBankDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBankDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBankDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BankDetail aBankDetail = (BankDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getBankDetailService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getBankDetailService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getBankDetailService().doApprove(auditHeader);

					if (aBankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getBankDetailService().doReject(auditHeader);

					if (aBankDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_BankDetailDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_BankDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.bankDetail), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		logger.debug(Literal.ENTERING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(BankDetail aBankDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBankDetail.getBefImage(), aBankDetail);
		return new AuditHeader(String.valueOf(aBankDetail.getId()), null, null, null, auditDetail,
				aBankDetail.getUserDetails(), getOverideMap());
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug(Literal.ENTERING);

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_BankDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug(Literal.ENTERING);
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.bankDetail);
	}

	protected void refreshList() {
		getBankDetailListCtrl().search();
	}

	public void onCheck$eMandate(Event event) {
		doShowEMandateSources(this.eMandate);
	}

	private void doShowEMandateSources(Checkbox event) {
		logger.debug(Literal.ENTERING);
		if (this.eMandate.isChecked()) {
			this.allowedSources.setReadonly(true);
			this.btnMultiSource.setVisible(true);
		} else {
			this.allowedSources.setReadonly(true);
			this.btnMultiSource.setVisible(false);
			this.allowedSources.setValue("");
		}
		logger.debug(Literal.LEAVING);

	}

	public void onClick$btnMultiSource(Event event) {
		logger.debug(Literal.ENTERING);
		Clients.clearWrongValue(this.btnMultiSource);

		Object dataObject = MultiSelectionSearchListBox.show(this.window, "Mandate_Sources",
				this.allowedSources.getValue(), null);
		if (dataObject instanceof String) {
			this.allowedSources.setValue(dataObject.toString());
		} else {
			String details = (String) dataObject;
			if (details != null) {
				this.allowedSources.setValue(details);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.bankDetail.getBankCode());
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public BankDetail getBankDetail() {
		return this.bankDetail;
	}

	public void setBankDetail(BankDetail bankDetail) {
		this.bankDetail = bankDetail;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public BankDetailService getBankDetailService() {
		return this.bankDetailService;
	}

	public void setBankDetailListCtrl(BankDetailListCtrl bankDetailListCtrl) {
		this.bankDetailListCtrl = bankDetailListCtrl;
	}

	public BankDetailListCtrl getBankDetailListCtrl() {
		return this.bankDetailListCtrl;
	}

}
