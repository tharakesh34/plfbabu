package com.pennant.webui.systemmasters.transactionmapping;

import java.sql.Timestamp;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.transactionmapping.TransactionMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.cd.model.TransactionMapping;

public class TransactionMappingDialogCtrl extends GFCBaseCtrl<TransactionMapping> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TransactionMappingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TransactionMappingDialog;

	protected ExtendedCombobox posId;
	protected ExtendedCombobox dealerCode;
	protected ExtendedCombobox mid;
	protected Textbox dealerName;
	protected Textbox tid;
	protected Checkbox active;
	protected Textbox mobileNumber1;
	protected Textbox mobileNumber2;
	protected Textbox mobileNumber3;

	private transient TransactionMappingListCtrl transactionMappingListCtrl;
	private transient TransactionMappingService transactionMappingService;
	private TransactionMapping transactionMapping;

	public TransactionMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TransactionMappingDialog";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.transactionMapping.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_TransactionMappingDialog(Event event) throws AppException {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_TransactionMappingDialog);

		try {
			this.transactionMapping = (TransactionMapping) arguments.get("transactionMapping");
			this.transactionMappingListCtrl = (TransactionMappingListCtrl) arguments.get("transactionMappingListCtrl");

			if (this.transactionMapping == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			TransactionMapping mapping = new TransactionMapping();
			BeanUtils.copyProperties(this.transactionMapping, mapping);
			this.transactionMapping.setBefImage(mapping);

			doLoadWorkFlow(this.transactionMapping.isWorkflow(), this.transactionMapping.getWorkflowId(),
					this.transactionMapping.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "TransactionMappingDialog");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.transactionMapping);

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

		this.posId.setModuleName("POSId");
		this.posId.setValueColumn("POSId");
		this.posId.setDescColumn("StoreName");
		this.posId.setValueType(DataType.LONG);
		this.posId.setMandatoryStyle(true);
		this.posId.setValidateColumns(new String[] { "POSId" });

		this.dealerCode.setModuleName("DealerCode");
		this.dealerCode.setValueColumn("DealerCode");
		this.dealerCode.setDescColumn("MerchantId");
		this.dealerCode.setValueType(DataType.LONG);
		this.dealerCode.setMandatoryStyle(true);
		this.dealerCode.setValidateColumns(new String[] { "DealerCode" });

		this.mid.setModuleName("Stores");
		this.mid.setValueColumn("StoreId");
		this.mid.setDescColumn("StoreName");
		this.mid.setValueType(DataType.LONG);
		this.mid.setMandatoryStyle(true);
		this.mid.setValidateColumns(new String[] { "StoreId" });

		this.tid.setMaxlength(20);

		this.mobileNumber1.setMaxlength(20);
		this.mobileNumber2.setMaxlength(20);
		this.mobileNumber3.setMaxlength(20);

		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TransactionMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TransactionMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TransactionMappingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TransactionMappingDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
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
		doShowNotes(this.transactionMapping);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		transactionMappingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.transactionMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param covenantType
	 * 
	 */
	public void doWriteBeanToComponents(TransactionMapping mapping) {
		logger.debug(Literal.ENTERING);

		if (mapping.getPosId() == 0) {
			this.posId.setDescription("");
			this.posId.setValue("");
		} else {
			this.posId.setDescription(mapping.getStoreName());
			this.posId.setValue(String.valueOf(mapping.getPosId()));
		}

		if (mapping.getDealerCode() == Long.MIN_VALUE) {
			this.dealerCode.setValue("");
			dealerCode.setDescription("");
		} else {
			this.dealerCode.setValue(String.valueOf(mapping.getDealerCode()));
			dealerCode.setDescription(mapping.getStoreName());
		}

		this.tid.setText(mapping.getTid());

		if (mapping.getMid() == Long.MIN_VALUE) {
			this.mid.setValue("");
			this.mid.setDescription("");
		} else {
			this.mid.setValue(String.valueOf(mapping.getMid()));
			this.mid.setDescription(mapping.getStoreName());
		}

		this.dealerName.setText(mapping.getDealerName());

		this.active.setChecked(mapping.isActive());

		if (mapping.isNewRecord() || (mapping.getRecordType() != null ? mapping.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}

		this.mobileNumber1.setText(mapping.getMobileNumber1());
		this.mobileNumber2.setText(mapping.getMobileNumber2());
		this.mobileNumber3.setText(mapping.getMobileNumber3());
		this.recordStatus.setValue(mapping.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(TransactionMapping aTransactionMapping) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			aTransactionMapping.setPosId(Integer.parseInt(this.posId.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionMapping.setDealerCode(Long.valueOf(this.dealerCode.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionMapping.setDealerName(this.dealerName.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionMapping.setMid(Long.valueOf(this.mid.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionMapping.setTid(this.tid.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionMapping.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionMapping.setMobileNumber1(this.mobileNumber1.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionMapping.setMobileNumber2(this.mobileNumber2.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTransactionMapping.setMobileNumber3(this.mobileNumber3.getText());
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param covenantType The entity that need to be render.
	 */
	public void doShowDialog(TransactionMapping mapping) {
		logger.debug(Literal.ENTERING);

		if (mapping.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.posId.setFocus(true);
		} else {
			// this.description.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(mapping.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(mapping);

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.posId.isReadonly()) {
			this.posId.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionMapping_POSId.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));
		}

		if (!this.dealerCode.isReadonly()) {
			this.dealerCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionMapping_DealerCode.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}

		if (!this.dealerName.isReadonly()) {
			this.dealerName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionMapping_DealerName.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_CHAR, true));
		}

		if (!this.mid.isReadonly()) {
			this.mid.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionMapping_MID.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));
		}

		if (!this.tid.isReadonly()) {
			this.tid.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionMapping_TID.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.mobileNumber1.isReadonly()) {
			this.mobileNumber1.setConstraint(
					new PTMobileNumberValidator(Labels.getLabel("label_TransactionMapping_MobileNumber1.value"), true));
		}

		if (!this.mobileNumber2.isReadonly()) {
			this.mobileNumber2.setConstraint(
					new PTMobileNumberValidator(Labels.getLabel("label_TransactionMapping_MobileNumber2.value"), true));
		}

		if (!this.mobileNumber3.isReadonly()) {
			this.mobileNumber3.setConstraint(
					new PTMobileNumberValidator(Labels.getLabel("label_TransactionMapping_MobileNumber3.value"), true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.posId.setConstraint("");
		this.dealerCode.setConstraint("");
		this.dealerName.setConstraint("");
		this.tid.setConstraint("");
		this.mid.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

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

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final TransactionMapping aTransactionMapping = new TransactionMapping();
		BeanUtils.copyProperties(this.transactionMapping, aTransactionMapping);

		doDelete(String.valueOf(aTransactionMapping.getPosId()), aTransactionMapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.transactionMapping.isNewRecord()) {
			this.posId.setReadonly(false);
		} else {
			this.posId.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("TransactionMappingDialog_POSId"), this.posId);
		readOnlyComponent(isReadOnly("TransactionMappingDialog_DealerCode"), this.dealerCode);
		readOnlyComponent(isReadOnly("TransactionMappingDialog_DealerName"), this.dealerName);
		readOnlyComponent(isReadOnly("TransactionMappingDialog_MID"), this.mid);
		readOnlyComponent(isReadOnly("TransactionMappingDialog_TID"), this.tid);
		readOnlyComponent(isReadOnly("TransactionMappingDialog_Active"), this.active);
		readOnlyComponent(isReadOnly("TransactionMappingDialog_MobileNumber1"), this.mobileNumber1);
		readOnlyComponent(isReadOnly("TransactionMappingDialog_MobileNumber2"), this.mobileNumber2);
		readOnlyComponent(isReadOnly("TransactionMappingDialog_MobileNumber3"), this.mobileNumber3);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.transactionMapping.isNewRecord()) {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.posId);
		readOnlyComponent(true, this.dealerCode);
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
		logger.debug(Literal.ENTERING);
		this.posId.setValue("");
		this.dealerCode.setValue("");
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final TransactionMapping mapping = new TransactionMapping();
		BeanUtils.copyProperties(this.transactionMapping, mapping);

		doSetValidation();
		doWriteComponentsToBean(mapping);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(mapping.getRecordType())) {
				mapping.setVersion(mapping.getVersion() + 1);
				if (mapping.isNewRecord()) {
					mapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					mapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					mapping.setNewRecord(true);
				}
			}
		} else {
			mapping.setVersion(mapping.getVersion() + 1);
			if (mapping.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(mapping, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(TransactionMapping mapping, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		mapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		mapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		mapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			mapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(mapping.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, mapping);
				}
				if (isNotesMandatory(taskId, mapping)) {
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
			mapping.setTaskId(taskId);
			mapping.setNextTaskId(nextTaskId);
			mapping.setRoleCode(getRole());
			mapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(mapping, tranType);
			String operationRefs = getServiceOperations(taskId, mapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(mapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(mapping, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		TransactionMapping aTransactionMapping = (TransactionMapping) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = transactionMappingService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = transactionMappingService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = transactionMappingService.doApprove(auditHeader);

					if (aTransactionMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = transactionMappingService.doReject(auditHeader);
					if (aTransactionMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_TransactionMappingDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_TransactionMappingDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.transactionMapping), true);
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(TransactionMapping aStockCompany, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aStockCompany.getBefImage(), aStockCompany);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aStockCompany.getUserDetails(),
				getOverideMap());
	}

	public void setTransactionMappingService(TransactionMappingService transactionMappingService) {
		this.transactionMappingService = transactionMappingService;
	}

	public TransactionMapping getTransactionMapping() {
		return transactionMapping;
	}

	public void setTransactionMapping(TransactionMapping transactionMapping) {
		this.transactionMapping = transactionMapping;
	}

}
