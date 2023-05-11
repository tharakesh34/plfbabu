package com.pennant.pff.presentment.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.pff.presentment.ExcludeReasonCode;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennant.pff.presentment.service.PresentmentExcludeCodeService;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class PresentmentExcludeCodeDialogCtrl extends GFCBaseCtrl<PresentmentExcludeCode> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(PresentmentExcludeCodeDialogCtrl.class);

	protected Window windowPresentmentExcludeCodeDialog;

	protected Combobox code;
	protected Combobox instrumentType;
	protected Textbox description;
	protected ExtendedCombobox bounceId;

	private PresentmentExcludeCode excludeCode;
	private transient PresentmentExcludeCodeListCtrl presentmentExcludeCodeList;
	private transient PresentmentExcludeCodeService presentmentExcludeCodeService;

	private List<ValueLabel> mandateTypeList = MandateUtil.getInstrumentTypes();
	private List<ValueLabel> excludeCodeList = ExcludeReasonCode.getExcludeCodes();

	public PresentmentExcludeCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PresentmentExcludeCodeDialog";
	}

	public void onCreate$windowPresentmentExcludeCodeDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowPresentmentExcludeCodeDialog);

		try {

			this.excludeCode = (PresentmentExcludeCode) arguments.get("PresentmentExcludeCode");

			if (this.excludeCode == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			this.presentmentExcludeCodeList = (PresentmentExcludeCodeListCtrl) arguments
					.get("PresentmentExcludeCodeList");

			PresentmentExcludeCode exlcudeCode = new PresentmentExcludeCode();
			BeanUtils.copyProperties(this.excludeCode, exlcudeCode);

			this.excludeCode.setBefImage(exlcudeCode);

			doLoadWorkFlow(this.excludeCode.isWorkflow(), this.excludeCode.getWorkflowId(),
					this.excludeCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.excludeCode);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.bounceId.setMandatoryStyle(true);
		this.bounceId.setModuleName("BounceReason");
		this.bounceId.setValueColumn("BounceCode");
		this.bounceId.setValidateColumns(new String[] { "BounceCode" });

		this.groupboxWf.setVisible(isWorkFlowEnabled());

		logger.debug(Literal.LEAVING);
	}

	public void onChange$instrumentType(Event event) {
		logger.debug(Literal.ENTERING);

		onChangeInstrumentType();

		logger.debug(Literal.LEAVING);
	}

	private void onChangeInstrumentType() {
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("InstrumentType", this.instrumentType.getValue(), Filter.OP_EQUAL);
		this.bounceId.setFilters(filter);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		this.btnSave.setVisible(true);
		this.btnCancel.setVisible(false);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PresentmentExcludeCode_btnPresentmentExcludeCode"));
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PresentmentExcludeCode_btnDelete"));

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$bounceId(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Object dataObject = bounceId.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.bounceId.setValue("");
			this.bounceId.setDescription("");
			this.bounceId.setAttribute("BounceCode", null);
		} else {
			BounceReason details = (BounceReason) dataObject;
			this.bounceId.setAttribute("BounceCode", details.getBounceID());
			this.bounceId.setValue(details.getBounceCode(), details.getReturnCode());
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		PresentmentExcludeCode excludeCode = new PresentmentExcludeCode();
		BeanUtils.copyProperties(this.excludeCode, excludeCode);

		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(excludeCode);

		isNew = excludeCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;

			if (StringUtils.isBlank(excludeCode.getRecordType())) {
				excludeCode.setVersion(excludeCode.getVersion() + 1);

				if (isNew) {
					excludeCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					excludeCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					excludeCode.setNewRecord(true);
				}
			}
		} else {
			excludeCode.setVersion(excludeCode.getVersion() + 1);

			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(excludeCode, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doEdit();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		MessageUtil.showHelpWindow(event, windowPresentmentExcludeCodeDialog);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doWriteBeanToComponents(this.excludeCode.getBefImage());
		doReadOnly();

		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(true);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		doDelete();
	}

	public void doWriteBeanToComponents(PresentmentExcludeCode aBounceCode) {
		logger.debug(Literal.ENTERING);

		this.description.setValue(aBounceCode.getDescription());
		this.bounceId.setValue(StringUtils.trimToEmpty(aBounceCode.getBounceCode()),
				StringUtils.trimToEmpty(aBounceCode.getReturnCode()));
		this.bounceId.setAttribute("BounceCode", aBounceCode.getBounceId());

		this.recordStatus.setValue(aBounceCode.getRecordStatus());

		fillComboBox(this.code, aBounceCode.getCode(), excludeCodeList, "");
		fillComboBox(this.instrumentType, aBounceCode.getInstrumentType(), mandateTypeList, "");

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(PresentmentExcludeCode aBounceCode) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (this.code.getSelectedItem() != null
					&& !StringUtils.trimToEmpty(this.code.getSelectedItem().getValue().toString())
							.equals(PennantConstants.List_Select)) {
				aBounceCode.setCode(this.code.getSelectedItem().getValue().toString());
			} else {
				aBounceCode.setCode(PennantConstants.List_Select);
				throw new WrongValueException(this.code, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_BounceCodeDialog_Code.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBounceCode.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			aBounceCode.setBounceCode(this.bounceId.getValue());
			aBounceCode.setReturnCode(this.bounceId.getDescription());
			Object object = this.bounceId.getAttribute("BounceCode");
			if (object != null) {
				aBounceCode.setBounceId(Long.parseLong(object.toString()));
			} else {
				aBounceCode.setBounceId(null);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.instrumentType.getSelectedItem() != null
					&& !StringUtils.trimToEmpty(this.instrumentType.getSelectedItem().getValue().toString())
							.equals(PennantConstants.List_Select)) {
				aBounceCode.setInstrumentType(this.instrumentType.getSelectedItem().getValue().toString());
			} else {
				aBounceCode.setInstrumentType(PennantConstants.List_Select);
				throw new WrongValueException(this.instrumentType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_PresentmentExcludeDialog_InstrumentType.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aBounceCode.setRecordStatus(this.recordStatus.getValue());

		if (aBounceCode.isNewRecord()) {
			aBounceCode.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			aBounceCode.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(PresentmentExcludeCode aBounceCode) {
		if (aBounceCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.bounceId.setValue(String.valueOf(aBounceCode.getBounceCode()));
				this.bounceId.setAttribute("BounceCode", aBounceCode.getBounceId());
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
		onChangeInstrumentType();

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceCodeDialog_BounceCodeDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.bounceId.isReadonly()) {
			this.bounceId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BounceCodeDialog_BounceId.value"), null, true, true));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.code.setConstraint("");
		this.description.setConstraint("");
		this.bounceId.setConstraint("");
		this.instrumentType.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
		this.bounceId.setErrorMessage("");
		this.instrumentType.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final PresentmentExcludeCode aBounceCode = new PresentmentExcludeCode();
		BeanUtils.copyProperties(getExcludeCode(), aBounceCode);

		String keyReference = "Exlcude Code : " + aBounceCode.getCode() + " And Instrument Type: "
				+ aBounceCode.getInstrumentType();

		doDelete(keyReference, aBounceCode);

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (getExcludeCode().isNewRecord()) {
			this.code.setReadonly(false);
			this.instrumentType.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.code.setDisabled(true);
			this.instrumentType.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.bounceId.setReadonly(isReadOnly("PresentmentExcludeCodeDialog_BounceCode"));
		this.description.setReadonly(isReadOnly("PresentmentExcludeCodeDialog_Description"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.excludeCode.isNewRecord()) {
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

		this.code.setDisabled(true);
		this.description.setReadonly(true);
		this.bounceId.setReadonly(true);
		this.instrumentType.setReadonly(true);

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

		this.code.setValue("");
		this.description.setValue("");
		this.bounceId.setValue("");
		this.instrumentType.setValue("");
		this.btnCancel.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(PresentmentExcludeCode aBounceCode, String tranType) {
		logger.debug(Literal.ENTERING);

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

				if (isNotesMandatory(taskId, aBounceCode) && !notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		PresentmentExcludeCode aBounceCode = (PresentmentExcludeCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		String recordType = aBounceCode.getRecordType();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = presentmentExcludeCodeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = presentmentExcludeCodeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = presentmentExcludeCodeService.doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = presentmentExcludeCodeService.doReject(auditHeader);

					if (PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.windowPresentmentExcludeCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.excludeCode), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(PresentmentExcludeCode aBounceCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBounceCode.getBefImage(), aBounceCode);
		return new AuditHeader(String.valueOf(aBounceCode.getId()), null, null, null, auditDetail,
				aBounceCode.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doShowNotes(this.excludeCode);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	protected void refreshList() {
		presentmentExcludeCodeList.fillListData();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.excludeCode.getId());
	}

	public void setPresentmentExcludeCodeList(PresentmentExcludeCodeListCtrl presentmentExcludeCodeList) {
		this.presentmentExcludeCodeList = presentmentExcludeCodeList;
	}

	@Autowired
	public void setPresentmentExcludeCodeService(PresentmentExcludeCodeService presentmentExcludeCodeService) {
		this.presentmentExcludeCodeService = presentmentExcludeCodeService;
	}

	public PresentmentExcludeCode getExcludeCode() {
		return excludeCode;
	}

	public void setExcludeCode(PresentmentExcludeCode excludeCode) {
		this.excludeCode = excludeCode;
	}

}