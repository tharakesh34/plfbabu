package com.pennant.pff.settlement.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.settlement.model.SettlementTypeDetail;
import com.pennant.pff.settlement.service.SettlementTypeDetailService;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Settlement/SettlementTypeDetailDialog.zul file.
 */
public class SettlementTypeDetailDialogCtrl extends GFCBaseCtrl<SettlementTypeDetail> {
	private static final long serialVersionUID = -4484270347916527133L;
	private static final Logger logger = LogManager.getLogger(SettlementTypeDetailDialogCtrl.class);

	protected Window windowSettlementTypeDetailDialog;
	protected Textbox code;
	protected Textbox description;
	protected Checkbox alwGracePeriod;
	protected Checkbox active;

	private SettlementTypeDetail settlementTypeDetail;
	private transient SettlementTypeDetailListCtrl settlementTypeDetailListCtrl;
	private transient SettlementTypeDetailService settlementTypeDetailService;

	public SettlementTypeDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SettlementTypeDetailDialog";
	}

	public void onCreate$windowSettlementTypeDetailDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowSettlementTypeDetailDialog);

		try {
			doCheckRights();

			if (arguments.containsKey("settlementTypeDetail")) {
				this.settlementTypeDetail = (SettlementTypeDetail) arguments.get("settlementTypeDetail");
				SettlementTypeDetail befImage = new SettlementTypeDetail();
				BeanUtils.copyProperties(this.settlementTypeDetail, befImage);
				this.settlementTypeDetail.setBefImage(befImage);
				setSettlementTypeDetail(this.settlementTypeDetail);
			} else {
				setSettlementTypeDetail(null);
			}

			doLoadWorkFlow(this.settlementTypeDetail.isWorkflow(), this.settlementTypeDetail.getWorkflowId(),
					this.settlementTypeDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			if (arguments.containsKey("settlementTypeDetailListCtrl")) {
				setSettlementTypeDetailListCtrl(
						(SettlementTypeDetailListCtrl) arguments.get("settlementTypeDetailListCtrl"));
			} else {
				setSettlementTypeDetailListCtrl(null);
			}

			doSetFieldProperties();
			doShowDialog(settlementTypeDetail);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.windowSettlementTypeDetailDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doSave();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doEdit();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		MessageUtil.showHelpWindow(event, windowSettlementTypeDetailDialog);
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnDelete(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doDelete();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doCancel();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doShowNotes(this.settlementTypeDetail);
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.code.setErrorMessage("");
		this.description.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void refreshList() {
		settlementTypeDetailListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.settlementTypeDetail.getSettlementCode());
	}

	@Override
	protected void doSave() {
		logger.debug(Literal.ENTERING);

		final SettlementTypeDetail aSettlementTypeDetail = new SettlementTypeDetail();
		BeanUtils.copyProperties(settlementTypeDetail, aSettlementTypeDetail);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aSettlementTypeDetail);

		isNew = aSettlementTypeDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSettlementTypeDetail.getRecordType())) {
				aSettlementTypeDetail.setVersion(aSettlementTypeDetail.getVersion() + 1);
				if (isNew) {
					aSettlementTypeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSettlementTypeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSettlementTypeDetail.setNewRecord(true);
				}
			}
		} else {
			aSettlementTypeDetail.setVersion(aSettlementTypeDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {

			if (doProcess(aSettlementTypeDetail, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected boolean doProcess(SettlementTypeDetail std, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		std.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		std.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		std.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			std.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(std.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, std);
				}

				if (isNotesMandatory(taskId, std)) {
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

			std.setTaskId(taskId);
			std.setNextTaskId(nextTaskId);
			std.setRoleCode(getRole());
			std.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(std, tranType);

			String operationRefs = getServiceOperations(taskId, std);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(std, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(std, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private void doWriteBeanToComponents(SettlementTypeDetail std) {
		logger.debug(Literal.ENTERING);

		this.code.setValue(std.getSettlementCode());
		this.description.setValue(std.getSettlementDesc());
		this.alwGracePeriod.setChecked(std.isAlwGracePeriod());
		this.active.setChecked(std.isActive());
		this.recordStatus.setValue(std.getRecordStatus());

		if (std.isNewRecord()
				|| (std.getRecordType() != null ? std.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)) {
		}

		logger.debug(Literal.LEAVING);
	}

	private void doWriteComponentsToBean(SettlementTypeDetail settlementTypeDetail) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			settlementTypeDetail.setSettlementCode((this.code.getValue().toUpperCase()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			settlementTypeDetail.setSettlementDesc(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			settlementTypeDetail.setAlwGracePeriod(this.alwGracePeriod.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			settlementTypeDetail.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		settlementTypeDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(SettlementTypeDetail settlementTypeDetail) {
		logger.debug(Literal.ENTERING);

		if (settlementTypeDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.code.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.description.focus();
				if (StringUtils.isNotBlank(settlementTypeDetail.getRecordType())) {
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
			doWriteBeanToComponents(settlementTypeDetail);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.windowSettlementTypeDetailDialog.onClose();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean doSaveProcess(AuditHeader ah, String method) {
		logger.debug(Literal.LEAVING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		SettlementTypeDetail aSettlementTypeDetail = (SettlementTypeDetail) ah.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (ah.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					ah = settlementTypeDetailService.delete(ah);
					deleteNotes = true;
				} else {
					ah = settlementTypeDetailService.saveOrUpdate(ah);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					ah = settlementTypeDetailService.doApprove(ah);

					if (aSettlementTypeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					ah = settlementTypeDetailService.doReject(ah);

					if (aSettlementTypeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					ah.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					ErrorControl.showErrorControl(this.windowSettlementTypeDetailDialog, ah);
					return processCompleted;
				}
			}

			ah = ErrorControl.showErrorDetails(this.windowSettlementTypeDetailDialog, ah);
			retValue = ah.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.settlementTypeDetail), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				ah.setOveride(true);
				ah.setErrorMessage(null);
				ah.setInfoMessage(null);
				ah.setOverideMessage(null);
			}
		}
		setOverideMap(ah.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.code.setMaxlength(8);
		this.description.setMaxlength(200);

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SettlementTypeDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SettlementTypeDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SettlementTypeDetailDialog_btnDelete"));
		this.btnSave.setVisible(true);
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.settlementTypeDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_SettlementCode"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(Labels.getLabel("label_SettlementDesc"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.code.setConstraint("");
		this.description.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final SettlementTypeDetail std = new SettlementTypeDetail();
		BeanUtils.copyProperties(settlementTypeDetail, std);

		String keyReference = Labels.getLabel("label_SettlementCode") + " : " + std.getSettlementCode();

		doDelete(keyReference, std);

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (settlementTypeDetail.isNewRecord()) {
			this.code.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.code.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.description.setReadonly(false);
		this.alwGracePeriod.setDisabled(isReadOnly("SettlementTypeDetailDialog_AlwGracePeriod"));
		this.active.setDisabled(isReadOnly("SettlementTypeDetailDialog_Active"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.settlementTypeDetail.isNewRecord()) {
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

	private void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.code.setReadonly(true);
		this.description.setReadonly(true);
		this.alwGracePeriod.setDisabled(true);
		this.active.setDisabled(true);

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

	private AuditHeader getAuditHeader(SettlementTypeDetail std, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, std.getBefImage(), std);
		return new AuditHeader(String.valueOf(std.getId()), null, null, null, ad, std.getUserDetails(),
				getOverideMap());
	}

	public void setSettlementTypeDetail(SettlementTypeDetail settlementTypeDetail) {
		this.settlementTypeDetail = settlementTypeDetail;
	}

	@Autowired
	public void setSettlementTypeDetailListCtrl(SettlementTypeDetailListCtrl settlementTypeDetailListCtrl) {
		this.settlementTypeDetailListCtrl = settlementTypeDetailListCtrl;
	}

	@Autowired
	public void setSettlementTypeDetailService(SettlementTypeDetailService settlementTypeDetailService) {
		this.settlementTypeDetailService = settlementTypeDetailService;
	}

}
