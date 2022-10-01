package com.pennant.webui.applicationmaster.bouncecode;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.BounceCode;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.BounceCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class BounceCodeDialogCtrl extends GFCBaseCtrl<BounceCode> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(BounceCodeDialogCtrl.class);

	protected Window windowBounceCodeDialog;

	protected Textbox code;
	protected Textbox description;
	protected Checkbox createBounceOnDueDate;
	protected ExtendedCombobox bounceid;

	private BounceCode bounceCode;
	private transient BounceCodeListCtrl bounceCodeListCtrl;

	private transient boolean validationOn;

	private transient BounceCodeService bounceCodeService;

	public BounceCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BounceCodeDialog";
	}

	public void onCreate$windowBounceCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		setPageComponents(windowBounceCodeDialog);

		try {

			this.bounceCode = (BounceCode) arguments.get("bounceCode");
			this.bounceCodeListCtrl = (BounceCodeListCtrl) arguments.get("bounceCodeListCtrl");

			if (this.bounceCode == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			BounceCode bounceCode = new BounceCode();
			BeanUtils.copyProperties(this.bounceCode, bounceCode);
			this.bounceCode.setBefImage(bounceCode);

			doLoadWorkFlow(this.bounceCode.isWorkflow(), this.bounceCode.getWorkflowId(),
					this.bounceCode.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.bounceCode);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.code.setMaxlength(16);
		this.description.setMaxlength(50);
		this.bounceid.setMaxlength(8);

		this.bounceid.setModuleName("BounceReason");
		this.bounceid.setValueColumn("BounceCode");
		this.bounceid.setDescColumn("Lovdesccategory");
		this.bounceid.setValidateColumns(new String[] { "BounceCode" });

		if (this.createBounceOnDueDate.isChecked()) {
			this.bounceid.setMandatoryStyle(true);
		} else {
			this.bounceid.setMandatoryStyle(false);
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		if (enqiryModule) {
			if (south != null) {
				south.setVisible(false);
			}
		}
		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BounceCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BounceCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BounceCodeDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, windowBounceCodeDialog);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.bounceCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(true);
		logger.debug("Leaving");
	}

	public void doWriteBeanToComponents(BounceCode aBounceCode) {
		logger.debug("Entering");

		this.code.setValue(aBounceCode.getCode());
		this.description.setValue(aBounceCode.getDescription());
		this.createBounceOnDueDate.setChecked(aBounceCode.isCreateBounceOnDueDate());

		if (this.createBounceOnDueDate.isChecked()) {
			this.bounceid.setValue(String.valueOf(aBounceCode.getBounceCode()));
			this.bounceid.setAttribute("BounceCode", aBounceCode.getBounceId());
			// this.bounceid.setValue("");
		}

		this.recordStatus.setValue(aBounceCode.getRecordStatus());

		logger.debug("Leaving");
	}

	public void doWriteComponentsToBean(BounceCode aBounceCode) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			aBounceCode.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBounceCode.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBounceCode.setCreateBounceOnDueDate(this.createBounceOnDueDate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isNotBlank(this.bounceid.getValue())) {
				Object object = this.bounceid.getObject();
				if (object != null) {
					BounceReason bc = (BounceReason) object;
					aBounceCode.setBounceId(bc.getBounceID());
				} else {
					aBounceCode.setBounceCode(String.valueOf(this.bounceid.getValue()));
				}
			}
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

		aBounceCode.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");

	}

	public void doShowDialog(BounceCode aBounceCode) {
		if (aBounceCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.bounceid.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.bounceid.focus();
				this.bounceid.setValue(String.valueOf(aBounceCode.getBounceCode()));
				this.bounceid.setAttribute("BounceCode", aBounceCode.getBounceId());
				if (StringUtils.isNotBlank(aBounceCode.getRecordType())) {
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

		doWriteBeanToComponents(aBounceCode);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceCodeDialog_Code.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceCodeDialog_BounceCodeDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (this.createBounceOnDueDate.isChecked() && !this.bounceid.isReadonly()) {
			this.bounceid.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceCodeDialog_BounceId.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		logger.debug("Entering");

		setValidationOn(false);
		this.code.setConstraint("");
		this.description.setConstraint("");
		this.bounceid.setConstraint("");
		logger.debug("Leaving");
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
		this.bounceid.setErrorMessage("");

		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final BounceCode aBounceCode = new BounceCode();
		BeanUtils.copyProperties(getBounceCode(), aBounceCode);

		doDelete(Labels.getLabel("label_BounceCodeDialog_Code.value") + " : " + aBounceCode.getCode(), aBounceCode);

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug("Entering");

		this.code.setReadonly(true);
		this.description.setReadonly(true);
		this.createBounceOnDueDate.setDisabled(false);

		this.btnCancel.setVisible(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.bounceCode.isNewRecord()) {
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

	public void onCheck$createBounceOnDueDate(Event event) {
		logger.debug("Entering");

		if (this.createBounceOnDueDate.isChecked()) {
			this.bounceid.setMandatoryStyle(true);
			// this.bounceid.setValue(String.valueOf(bounceCode.getBounceCode()));
			this.bounceid.setValue("");
		} else {
			this.bounceid.setMandatoryStyle(false);
			this.bounceid.setValue("");
		}
	}

	public void doReadOnly() {
		logger.debug("Entering");

		this.code.setReadonly(true);
		this.description.setReadonly(true);
		this.createBounceOnDueDate.setDisabled(true);
		this.bounceid.setReadonly(true);

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

	public void doClear() {
		logger.debug("Entering");

		this.code.setValue("");
		this.description.setValue("");
		this.createBounceOnDueDate.setChecked(false);
		this.bounceid.setValue("");
		this.btnCancel.setVisible(true);
		logger.debug("Leaving");
	}

	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final BounceCode aBounceCode = new BounceCode();
		BeanUtils.copyProperties(getBounceCode(), aBounceCode);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aBounceCode);

		isNew = aBounceCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;

			if (StringUtils.isBlank(aBounceCode.getRecordType())) {
				aBounceCode.setVersion(aBounceCode.getVersion() + 1);

				if (isNew) {
					aBounceCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBounceCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBounceCode.setNewRecord(true);
				}
			}
		} else {
			aBounceCode.setVersion(aBounceCode.getVersion() + 1);

			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {

			if (doProcess(aBounceCode, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	protected boolean doProcess(BounceCode aBounceCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBounceCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBounceCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBounceCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBounceCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBounceCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBounceCode);
				}

				if (isNotesMandatory(taskId, aBounceCode)) {
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

			aBounceCode.setTaskId(taskId);
			aBounceCode.setNextTaskId(nextTaskId);
			aBounceCode.setRoleCode(getRole());
			aBounceCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBounceCode, tranType);
			String operationRefs = getServiceOperations(taskId, aBounceCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBounceCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBounceCode, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BounceCode aBounceCode = (BounceCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		String recordType = aBounceCode.getRecordType();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = bounceCodeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = bounceCodeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = bounceCodeService.doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = bounceCodeService.doReject(auditHeader);

					if (PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.windowBounceCodeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.windowBounceCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.bounceCode), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private AuditHeader getAuditHeader(BounceCode aBounceCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBounceCode.getBefImage(), aBounceCode);
		return new AuditHeader(String.valueOf(aBounceCode.getId()), null, null, null, auditDetail,
				aBounceCode.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.bounceCode);
	}

	protected void refreshList() {
		bounceCodeListCtrl.onClick$btnRefresh(null);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.bounceCode.getId());
	}

	public void setBounceCode(BounceCode bounceCode) {
		this.bounceCode = bounceCode;
	}

	public BounceCode getBounceCode() {
		return bounceCode;
	}

	public void setBounceCodeListCtrl(BounceCodeListCtrl bounceCodeListCtrl) {
		this.bounceCodeListCtrl = bounceCodeListCtrl;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public void setBounceCodeService(BounceCodeService bounceCodeService) {
		this.bounceCodeService = bounceCodeService;
	}
}