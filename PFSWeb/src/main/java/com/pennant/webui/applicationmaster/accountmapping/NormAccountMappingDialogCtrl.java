package com.pennant.webui.applicationmaster.accountmapping;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.CostCenter;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class NormAccountMappingDialogCtrl extends GFCBaseCtrl<AccountMapping> {
	private static final long					serialVersionUID	= -6945930303723518608L;
	private static final Logger					logger				= Logger
			.getLogger(NormAccountMappingDialogCtrl.class);

	protected Window							window_NormAccountMappingDialog;
	protected Uppercasebox						account;
	protected Textbox							hostAccount;
	protected ExtendedCombobox					accountType;
	protected ExtendedCombobox					profitCenter;
	protected ExtendedCombobox					costCenter;

	private AccountMapping						accountMapping;
	private transient AccountMappingListCtrl	accountMappingListCtrl;

	private transient boolean					validationOn;

	private transient AccountMappingService		accountMappingService;

	/**
	 * default constructor.<br>
	 */
	public NormAccountMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AccountMappingDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_NormAccountMappingDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_NormAccountMappingDialog);

		try {
			// Get the required arguments.
			this.accountMapping = (AccountMapping) arguments.get("accountmapping");
			this.accountMappingListCtrl = (AccountMappingListCtrl) arguments.get("accountmappingListCtrl");

			if (this.accountMapping == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AccountMapping accountMapping = new AccountMapping();
			BeanUtils.copyProperties(this.accountMapping, accountMapping);
			this.accountMapping.setBefImage(accountMapping);

			// Render the page and display the data.
			doLoadWorkFlow(this.accountMapping.isWorkflow(), this.accountMapping.getWorkflowId(),
					this.accountMapping.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.accountMapping);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.account.setMaxlength(50);
		this.hostAccount.setMaxlength(50);

		this.profitCenter.setModuleName("ProfitCenter");
		this.profitCenter.setValueColumn("ProfitCenterCode");
		this.profitCenter.setDescColumn("ProfitCenterDesc");
		this.profitCenter.setDisplayStyle(2);
		this.profitCenter.setValidateColumns(new String[] { "ProfitCenterCode" });
		this.profitCenter.setMandatoryStyle(true);

		this.costCenter.setModuleName("CostCenter");
		this.costCenter.setValueColumn("CostCenterCode");
		this.costCenter.setDescColumn("CostCenterDesc");
		this.costCenter.setDisplayStyle(2);
		this.costCenter.setValidateColumns(new String[] { "CostCenterCode" });
		this.costCenter.setMandatoryStyle(true);

		this.accountType.setModuleName("AccountType");
		this.accountType.setValueColumn("AcType");
		this.accountType.setDescColumn("AcTypeDesc");
		this.accountType.setDisplayStyle(2);
		this.accountType.setValidateColumns(new String[] { "AcType" });
		this.accountType.setMandatoryStyle(true);
		setStatusDetails();

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.accountMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param accountMapping
	 * 
	 */
	public void doWriteBeanToComponents(AccountMapping accountMapping) {
		logger.debug("Entering");

		this.account.setValue(accountMapping.getAccount());
		this.hostAccount.setValue(accountMapping.getHostAccount());

		if (String.valueOf(accountMapping.getProfitCenterID()) != null) {
			this.profitCenter.setObject(new ProfitCenter(accountMapping.getProfitCenterID()));
			this.profitCenter.setValue(accountMapping.getProfitCenterCode(), accountMapping.getProfitCenterDesc());
		}

		if (String.valueOf(accountMapping.getCostCenterID()) != null) {
			this.costCenter.setObject(new CostCenter(accountMapping.getCostCenterID()));
			this.costCenter.setValue(accountMapping.getCostCenterCode(), accountMapping.getCostCenterDesc());
		}
		this.accountType.setValue(accountMapping.getAccountType());

		if (accountMapping.isNewRecord()) {
			this.accountType.setDescription("");
		} else {
			this.accountType.setDescription(accountMapping.getAccountTypeDesc());
		}

		this.recordStatus.setValue(accountMapping.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param accountMapping
	 */
	public void doWriteComponentsToBean(AccountMapping accountMapping) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			accountMapping.setAccount(this.account.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			accountMapping.setHostAccount(this.hostAccount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			accountMapping.setAccountType(this.accountType.getValue());
			accountMapping.setAccountTypeDesc(this.accountType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.profitCenter.getValidatedValue();
			ProfitCenter profitCenter = (ProfitCenter) this.profitCenter.getObject();
			accountMapping.setProfitCenterID(profitCenter.getProfitCenterID());
			accountMapping.setProfitCenterCode(profitCenter.getProfitCenterCode());
			accountMapping.setProfitCenterDesc(profitCenter.getProfitCenterDesc());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.costCenter.getValidatedValue();
			CostCenter costCenter = (CostCenter) this.costCenter.getObject();
			accountMapping.setCostCenterID(costCenter.getCostCenterID());
			accountMapping.setCostCenterCode(costCenter.getCostCenterCode());
			accountMapping.setCostCenterDesc(costCenter.getCostCenterDesc());
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

		accountMapping.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param accountMapping
	 *            The entity that need to be render.
	 */
	public void doShowDialog(AccountMapping accountMapping) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (accountMapping.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.account.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.hostAccount.focus();
				if (StringUtils.isNotBlank(accountMapping.getRecordType())) {
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

		// fill the components with the data
		doWriteBeanToComponents(accountMapping);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.account.isReadonly()) {
			this.account.setConstraint(
					new PTStringValidator(Labels.getLabel("label_NormAccountMappingDialog_Account.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.hostAccount.isReadonly()) {
			this.hostAccount.setConstraint(
					new PTStringValidator(Labels.getLabel("label_NormAccountMappingDialog_HostAccount.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.profitCenter.isReadonly()) {
			this.profitCenter.setConstraint(new PTStringValidator(
					Labels.getLabel("label_NormAccountMappingDialog_ProfitCenter.value"), null, true));
		}
		if (!this.costCenter.isReadonly()) {
			this.costCenter.setConstraint(new PTStringValidator(
					Labels.getLabel("label_NormAccountMappingDialog_CostCenter.value"), null, true));
		}
		if (!this.accountType.isReadonly()) {
			this.accountType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_NormAccountMappingDialog_AccountType.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		setValidationOn(false);
		this.account.setConstraint("");
		this.hostAccount.setConstraint("");
		this.profitCenter.setConstraint("");
		this.costCenter.setConstraint("");
		this.accountType.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.account.setErrorMessage("");
		this.hostAccount.setErrorMessage("");
		this.accountType.setErrorMessage("");
		this.profitCenter.setErrorMessage("");
		this.costCenter.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Deletes a accountMapping entity from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug("Entering");

		final AccountMapping entity = new AccountMapping();
		BeanUtils.copyProperties(this.accountMapping, entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_AccountMappingDialog_Account.value") + " : " + accountMapping.getAccount();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(accountMapping.getRecordType())) {
				accountMapping.setVersion(accountMapping.getVersion() + 1);
				accountMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					accountMapping.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(accountMapping, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.accountMapping.isNewRecord()) {
			this.account.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.account.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.hostAccount.setReadonly(isReadOnly("AccountMappingDialog_HostAccount"));
		this.profitCenter.setReadonly(isReadOnly("AccountMappingDialog_ProfitCenter"));
		this.costCenter.setReadonly(isReadOnly("AccountMappingDialog_CostCenter"));
		this.accountType.setReadonly(isReadOnly("AccountMappingDialog_AccountType"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.accountMapping.isNewRecord()) {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.account.setReadonly(true);
		this.hostAccount.setReadonly(true);
		this.profitCenter.setReadonly(true);
		this.costCenter.setReadonly(true);
		this.accountType.setReadonly(true);

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

		this.account.setValue("");
		this.hostAccount.setValue("");
		this.profitCenter.setValue("");
		this.costCenter.setValue("");
		this.accountType.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug("Entering");

		final AccountMapping aAccountMapping = new AccountMapping();
		BeanUtils.copyProperties(this.accountMapping, aAccountMapping);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the accountMapping object with the components data
		doWriteComponentsToBean(aAccountMapping);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aAccountMapping.isNew();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAccountMapping.getRecordType())) {
				aAccountMapping.setVersion(aAccountMapping.getVersion() + 1);
				if (isNew) {
					aAccountMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAccountMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAccountMapping.setNewRecord(true);
				}
			}
		} else {
			aAccountMapping.setVersion(aAccountMapping.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aAccountMapping, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAccountMapping
	 *            (AccountMapping)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(AccountMapping aAccountMapping, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aAccountMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAccountMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aAccountMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountMapping.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAccountMapping);
				}

				if (isNotesMandatory(taskId, aAccountMapping)) {
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

			aAccountMapping.setTaskId(taskId);
			aAccountMapping.setNextTaskId(nextTaskId);
			aAccountMapping.setRoleCode(getRole());
			aAccountMapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountMapping, tranType);
			String operationRefs = getServiceOperations(taskId, aAccountMapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAccountMapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAccountMapping, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		AccountMapping aAccountMapping = (AccountMapping) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						aAuditHeader = accountMappingService.delete(aAuditHeader);
						deleteNotes = true;
					} else {
						aAuditHeader = accountMappingService.saveOrUpdate(aAuditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						aAuditHeader = accountMappingService.doApprove(aAuditHeader);

						if (aAccountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						aAuditHeader = accountMappingService.doReject(aAuditHeader);

						if (aAccountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						aAuditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_NormAccountMappingDialog, aAuditHeader);
						return processCompleted;
					}
				}

				aAuditHeader = ErrorControl.showErrorDetails(this.window_NormAccountMappingDialog, aAuditHeader);
				retValue = aAuditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.accountMapping), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					aAuditHeader.setOveride(true);
					aAuditHeader.setErrorMessage(null);
					aAuditHeader.setInfoMessage(null);
					aAuditHeader.setOverideMessage(null);
				}
			}

			setOverideMap(aAuditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAccountMapping
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(AccountMapping aAccountMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountMapping.getBefImage(), aAccountMapping);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAccountMapping.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.accountMapping);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		accountMappingListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.accountMapping.getAccount());
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public void setAccountMappingService(AccountMappingService accountMappingService) {
		this.accountMappingService = accountMappingService;
	}

}
