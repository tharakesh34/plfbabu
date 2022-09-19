package com.pennanttech.pff.overdraft.web;

import java.sql.Timestamp;
import java.util.ArrayList;

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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.overdraft.OverdraftConstants;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;

public class ManualBlockingDialogCtrl extends GFCBaseCtrl<OverdraftLimit> {
	private static final long serialVersionUID = -4204920631293929379L;
	private static final Logger logger = LogManager.getLogger(ManualBlockingDialogCtrl.class);

	protected Window window_ManualBlockingDialog;
	protected Borderlayout borderlayout_ManualBlockingDialog;

	protected Button btnSave;
	protected Button btnClose;

	protected ExtendedCombobox custID;
	protected ExtendedCombobox finReference;
	protected Checkbox blockUnBlockLimit;

	private OverdraftLimit odl;
	private ManualBlockingListCtrl manualBlockingListCtrl;
	private transient boolean validationOn;

	private OverdrafLoanService OverdrafLoanService;

	public ManualBlockingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManualBlockingDialog";
	}

	public void onCreate$window_ManualBlockingDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_ManualBlockingDialog);

		try {

			this.odl = (OverdraftLimit) arguments.get("OverdraftLoanLimits");
			this.manualBlockingListCtrl = (ManualBlockingListCtrl) arguments.get("manualBlockingListCtrl");

			doLoadWorkFlow(this.odl.isWorkflow(), this.odl.getWorkflowId(), this.odl.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
				if (StringUtils.isNotBlank(this.odl.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			doSetFieldProperties();
			doShowDialog(this.odl);

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ManualBlockingDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	public void doSetFieldProperties() {

		this.custID.setDisplayStyle(2);
		this.custID.setTextBoxWidth(130);
		this.custID.setMandatoryStyle(true);

		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setDisplayStyle(2);
		this.finReference.setTextBoxWidth(130);
		this.finReference.setMandatoryStyle(true);

		defaultFilters();

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");

	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void doShowDialog(OverdraftLimit odlh) {

		if (odlh.isNewRecord()) {
			this.custID.setVisible(true);
			this.finReference.setVisible(true);
			this.blockUnBlockLimit.setChecked(false);
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			this.blockUnBlockLimit.setDisabled(isReadOnly("ManualBlockingDialog_blockingstatus"));

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(odlh.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(odlh);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ManualBlockingDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	public void doWriteBeanToComponents(OverdraftLimit odlh) {

		this.custID.setValue(odlh.getCustCIF());
		this.finReference.setValue(odlh.getFinReference());
		this.blockUnBlockLimit.setChecked(odlh.isBlockLimit());

		this.recordStatus.setValue(odlh.getRecordStatus());
	}

	public void doReadOnly() {
		logger.debug("Entering ");
		this.custID.setReadonly(true);
		this.finReference.setReadonly(true);
		this.blockUnBlockLimit.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final OverdraftLimit odlh = new OverdraftLimit();
		BeanUtils.copyProperties(this.odl, odlh);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(odlh.getRecordType())) {
				odlh.setVersion(odlh.getVersion() + 1);
				odlh.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					odlh.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(odlh, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.odl);
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);
		final OverdraftLimit odlh = new OverdraftLimit();
		BeanUtils.copyProperties(odl, odlh);
		boolean isNew = false;
		doSetValidation();
		doWriteComponentsToBean(odlh);
		isNew = odlh.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(odlh.getRecordType())) {
				odlh.setVersion(odlh.getVersion() + 1);
				if (isNew) {
					odlh.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					odlh.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					odlh.setNewRecord(true);
				}
			}
		} else {
			odlh.setVersion(odlh.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(odlh, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	protected void refreshList() {
		manualBlockingListCtrl.search();
	}

	protected boolean doProcess(OverdraftLimit odlh, String tranType) {
		logger.debug("Entering ");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		odlh.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		odlh.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		odlh.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			odlh.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(odlh.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, odlh);
				}

				if (isNotesMandatory(taskId, odlh)) {
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

			odlh.setTaskId(taskId);
			odlh.setNextTaskId(nextTaskId);
			odlh.setRoleCode(getRole());
			odlh.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(odlh, tranType);

			String operationRefs = getServiceOperations(taskId, odlh);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(odlh, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(odlh, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	public void doWriteComponentsToBean(OverdraftLimit odlh) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			odlh.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			odlh.setBlockLimit(this.blockUnBlockLimit.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			odlh.setBlockType(OverdraftConstants.MANUAL_BLOCK_STATUS);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		odl.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving ");

	}

	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.custID.isReadonly()) {
			this.custID.setConstraint(new PTStringValidator(Labels.getLabel("label_ManualBlockingDialog_CustId.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true, true));
		}

		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManualBlockingDialog_FinReference.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true, true));
		}

		logger.debug("Leaving ");
	}

	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.custID.setConstraint("");
		logger.debug("Leaving ");
	}

	public void onFulfill$finReference(Event event) {
		validateFinReference(event, false);
	}

	public void validateFinReference(Event event, boolean isShowSearchList) {
		logger.debug("Entering " + event.toString());

		this.finReference.setConstraint("");
		this.finReference.clearErrorMessage();
		Clients.clearWrongValue(finReference);
		Object dataObject = null;

		if (isShowSearchList) {
			dataObject = ExtendedSearchListBox.show(this.window_ManualBlockingDialog, "OverdraftLimit");
		} else {
			dataObject = this.finReference.getObject();
		}

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
		} else {
			OverdraftLimit odlh = (OverdraftLimit) dataObject;
			if (odlh != null) {
				this.finReference.setValue(odlh.getFinReference());
				this.custID.setValue(String.valueOf(odlh.getCustCIF()));
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	public void onFulfill$custID(Event event) {
		Object dataObject = custID.getObject();
		Customer details = (Customer) dataObject;

		if (dataObject instanceof String) {
			this.custID.setValue(dataObject.toString());
		} else {
			if (details != null) {
				this.custID.setAttribute("custID", details.getCustID());
				getFinReferences(details.getCustCIF());
			} else {
				defaultFilters();
			}
		}
	}

	private void getFinReferences(String custCif) {
		// Build Where Clause For FinRef

		if (StringUtils.isNotBlank(this.custID.getValue())) {

			Filter[] filter = new Filter[2];
			filter[0] = new Filter("CustCif", custCif, Filter.OP_EQUAL);
			filter[1] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);

			this.finReference.setFilters(filter);
		} else {
			defaultFilters();
		}
	}

	private void defaultFilters() {
		logger.debug(Literal.ENTERING);

		this.custID.setModuleName("Customer");
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setValidateColumns(new String[] { "CustCIF" });

		this.finReference.setModuleName("OverdraftLimit");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug("Entering ");

		if (this.odl.isNewRecord()) {
			this.custID.setReadonly(false);
			this.finReference.setReadonly(false);
			this.blockUnBlockLimit.setChecked(false);
		} else {
			this.custID.setReadonly(true);
			this.finReference.setReadonly(true);
			this.blockUnBlockLimit.setDisabled(isReadOnly("ManualBlockingDialog_blockingstatus"));
		}

		if (isWorkFlowEnabled()) {

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.odl.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}

		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving");
	}

	private AuditHeader getAuditHeader(OverdraftLimit odl, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, odl.getBefImage(), odl);
		return new AuditHeader(getReference(), null, null, null, auditDetail, odl.getUserDetails(), getOverideMap());
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		OverdraftLimit odlh = (OverdraftLimit) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = OverdrafLoanService.delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = OverdrafLoanService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = OverdrafLoanService.doApprove(auditHeader);

					if (odlh.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = OverdrafLoanService.doReject(auditHeader);
					if (odlh.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ManualBlockingDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_ManualBlockingDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.odl), true);
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

		logger.debug("Leaving ");
		return processCompleted;
	}

	@Override
	protected String getReference() {
		return this.odl.getCustCIF() + PennantConstants.KEY_SEPERATOR + this.odl.getFinReference();
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		OverdrafLoanService = overdrafLoanService;
	}

}
