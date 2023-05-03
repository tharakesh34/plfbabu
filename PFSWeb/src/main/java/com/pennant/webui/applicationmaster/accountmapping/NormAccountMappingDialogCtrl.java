package com.pennant.webui.applicationmaster.accountmapping;

import java.sql.Timestamp;
import java.util.ArrayList;

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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.CostCenter;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.pff.accounting.AccountingUtil;
import com.pennant.pff.accounting.HostAccountStatus;
import com.pennant.pff.accounting.TransactionType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class NormAccountMappingDialogCtrl extends GFCBaseCtrl<AccountMapping> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(NormAccountMappingDialogCtrl.class);

	protected Window window_NormAccountMappingDialog;
	protected Uppercasebox account;
	protected Textbox hostAccount;
	protected ExtendedCombobox accountType;
	protected ExtendedCombobox profitCenter;
	protected ExtendedCombobox costCenter;
	protected ExtendedCombobox finType;
	protected Datebox openedDate;
	protected Datebox closedDate;
	protected Combobox status;
	protected Combobox allowedManualEntry;
	protected Space spaceClosedDate;
	protected Textbox gLDescription;
	protected Uppercasebox accountTypeGroup;

	private AccountMapping accountMapping;
	private transient AccountMappingListCtrl accountMappingListCtrl;

	private transient boolean validationOn;

	private transient AccountMappingService accountMappingService;

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
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_NormAccountMappingDialog(Event event) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * AccountMappingDialog_ClosedDate Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.account.setMaxlength(50);
		this.account.setDisabled(true);
		this.hostAccount.setMaxlength(50);

		this.profitCenter.setModuleName("ProfitCenter");
		this.profitCenter.setValueColumn("ProfitCenterCode");
		this.profitCenter.setDescColumn("ProfitCenterDesc");
		this.profitCenter.setDisplayStyle(2);
		this.profitCenter.setValidateColumns(new String[] { "ProfitCenterCode" });

		this.costCenter.setModuleName("CostCenter");
		this.costCenter.setValueColumn("CostCenterCode");
		this.costCenter.setDescColumn("CostCenterDesc");
		this.costCenter.setDisplayStyle(2);
		this.costCenter.setValidateColumns(new String[] { "CostCenterCode" });

		this.accountType.setModuleName("AccountType");
		this.accountType.setValueColumn("AcType");
		this.accountType.setDescColumn("AcTypeDesc");
		this.accountType.setDisplayStyle(2);
		this.accountType.setValidateColumns(new String[] { "AcType" });
		this.accountType.setMandatoryStyle(true);
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("controlac", 0, Filter.OP_EQUAL);
		this.accountType.setFilters(filters);

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setDisplayStyle(2);
		this.finType.setValidateColumns(new String[] { "FinType" });

		this.openedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.closedDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.gLDescription.setMaxlength(50);

		this.accountTypeGroup.setMaxlength(50);
		this.accountTypeGroup.setDisabled(true);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.accountMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param am
	 * 
	 */
	public void doWriteBeanToComponents(AccountMapping am) {
		logger.debug(Literal.ENTERING);

		this.account.setValue(am.getAccount());
		this.hostAccount.setValue(am.getHostAccount());

		if (am.getProfitCenterID() != null) {
			this.profitCenter.setObject(new ProfitCenter(am.getProfitCenterID()));
			this.profitCenter.setValue(am.getProfitCenterCode(), am.getProfitCenterDesc());
		}

		if (am.getCostCenterID() != null) {
			this.costCenter.setObject(new CostCenter(am.getCostCenterID()));
			this.costCenter.setValue(am.getCostCenterCode(), am.getCostCenterDesc());
		}

		this.accountType.setValue(am.getAccountType());

		if (am.isNewRecord()) {
			this.accountType.setDescription("");
		} else {
			this.accountType.setDescription(am.getAccountTypeDesc());
		}

		this.finType.setValue(am.getFinType());

		if (am.isNewRecord()) {
			this.finType.setDescription("");
		} else {
			this.finType.setDescription(am.getFinTypeDesc());
		}

		if (am.getOpenedDate() == null) {
			this.openedDate.setValue(SysParamUtil.getAppDate());
		} else {
			this.openedDate.setValue(am.getOpenedDate());
		}

		if (HostAccountStatus.isClose(am.getStatus())) {
			this.spaceClosedDate.setSclass(PennantConstants.mandateSclass);
			this.closedDate.setValue(am.getClosedDate());
			this.closedDate.setDisabled(true);
		}

		if (StringUtils.isEmpty(am.getAllowedManualEntry())) {
			fillComboBox(this.allowedManualEntry, TransactionType.NONE.code(), AccountingUtil.getManualEntries(), "");
		} else {
			fillComboBox(this.allowedManualEntry, am.getAllowedManualEntry(), AccountingUtil.getManualEntries(), "");
		}

		if (StringUtils.isEmpty(am.getStatus())) {
			fillComboBox(this.status, HostAccountStatus.OPEN.code(), AccountingUtil.getGLAccountStatus(), "");
		} else {
			fillComboBox(this.status, am.getStatus(), AccountingUtil.getGLAccountStatus(), "");
		}

		this.recordStatus.setValue(am.getRecordStatus());
		this.gLDescription.setValue(am.getGLDescription());
		this.accountTypeGroup.setValue(am.getAccountTypeGroup());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param accountMapping
	 */
	public void doWriteComponentsToBean(AccountMapping accountMapping) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			accountMapping.setAccount(this.account.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			String hostAccount = this.hostAccount.getValue();
			boolean isExistingHostAccount = accountMappingService.isExistingHostAccount(hostAccount);

			if (isExistingHostAccount && accountMapping.isNewRecord()) {
				throw new WrongValueException(this.hostAccount,
						Labels.getLabel("DATA_ALREADY_EXISTS", new String[] { Labels.getLabel("label_HostAccount") }));
			}

			accountMapping.setHostAccount(hostAccount);
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
			if (this.profitCenter.getObject() != null) {
				ProfitCenter profitCenter = (ProfitCenter) this.profitCenter.getObject();
				accountMapping.setProfitCenterID(profitCenter.getProfitCenterID());
				accountMapping.setProfitCenterCode(profitCenter.getProfitCenterCode());
				accountMapping.setProfitCenterDesc(profitCenter.getProfitCenterDesc());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.costCenter.getValidatedValue();
			if (this.costCenter.getObject() != null) {
				CostCenter costCenter = (CostCenter) this.costCenter.getObject();
				accountMapping.setCostCenterID(costCenter.getCostCenterID());
				accountMapping.setCostCenterCode(costCenter.getCostCenterCode());
				accountMapping.setCostCenterDesc(costCenter.getCostCenterDesc());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			accountMapping.setFinType(this.finType.getValue());
			accountMapping.setFinTypeDesc(this.finType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		accountMapping.setOpenedDate(this.openedDate.getValue());
		accountMapping.setClosedDate(this.closedDate.getValue());
		accountMapping.setAllowedManualEntry(this.allowedManualEntry.getSelectedItem().getValue());
		accountMapping.setStatus(this.status.getSelectedItem().getValue());

		try {
			accountMapping.setGLDescription(this.gLDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			accountMapping.setAccountTypeGroup(this.accountTypeGroup.getValue());
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param accountMapping The entity that need to be render.
	 */
	public void doShowDialog(AccountMapping accountMapping) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (accountMapping.isNewRecord()) {
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

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

		if (!this.accountType.isReadonly()) {
			this.accountType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_NormAccountMappingDialog_AccountType.value"), null, true));
		}

		if (!this.allowedManualEntry.isDisabled()) {
			this.allowedManualEntry.setConstraint(new StaticListValidator(AccountingUtil.getManualEntries(),
					Labels.getLabel("label_NormAccountMappingDialog_AlwManualEntry.value")));
		}

		if (!this.openedDate.isDisabled()) {
			this.openedDate.setConstraint(new PTDateValidator(Labels.getLabel("DATE_NO_FUTURE"), true, null,
					SysParamUtil.getAppDate(), true));
		}

		if (!this.status.isDisabled()) {
			this.status.setConstraint(new StaticListValidator(AccountingUtil.getGLAccountStatus(),
					Labels.getLabel("label_NormAccountMappingDialog_Status.value")));
		}

		if (!this.gLDescription.isReadonly()) {
			this.gLDescription.setConstraint(
					new PTStringValidator(Labels.getLabel("label_NormAccountMappingDialog_GLDescription.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION));
		}

		if (!this.accountTypeGroup.isReadonly()) {
			this.accountTypeGroup.setConstraint(new PTStringValidator(
					Labels.getLabel("label_NormAccountMappingDialog_AccountTypeGroup.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.account.setConstraint("");
		this.hostAccount.setConstraint("");
		this.accountType.setConstraint("");
		this.finType.setConstraint("");
		this.openedDate.setConstraint("");
		this.closedDate.setConstraint("");
		this.allowedManualEntry.setConstraint("");
		this.status.setConstraint("");
		this.accountTypeGroup.setConstraint("");

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		this.account.setErrorMessage("");
		this.hostAccount.setErrorMessage("");
		this.accountType.setErrorMessage("");
		this.profitCenter.setErrorMessage("");
		this.costCenter.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.openedDate.setErrorMessage("");
		this.closedDate.setErrorMessage("");
		this.allowedManualEntry.setErrorMessage("");
		this.status.setErrorMessage("");
		this.gLDescription.setErrorMessage("");
		this.accountTypeGroup.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a accountMapping entity from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final AccountMapping entity = new AccountMapping();
		BeanUtils.copyProperties(this.accountMapping, entity);

		doDelete(Labels.getLabel("label_AccountMappingDialog_Account.value") + " : " + accountMapping.getAccount(),
				entity);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.accountMapping.isNewRecord()) {
			this.account.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.status.setDisabled(true);
			this.closedDate.setDisabled(true);
			this.accountTypeGroup.setReadonly(false);
		} else {
			this.account.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.accountTypeGroup.setReadonly(true);

			this.closedDate.setDisabled(isReadOnly("AccountMappingDialog_ClosedDate"));
			this.status.setDisabled(isReadOnly("AccountMappingDialog_Status"));
		}

		this.hostAccount.setReadonly(isReadOnly("AccountMappingDialog_HostAccount"));
		this.profitCenter.setReadonly(isReadOnly("AccountMappingDialog_ProfitCenter"));
		this.costCenter.setReadonly(isReadOnly("AccountMappingDialog_CostCenter"));
		this.accountType.setReadonly(isReadOnly("AccountMappingDialog_AccountType"));
		this.finType.setReadonly(isReadOnly("AccountMappingDialog_FinType"));
		this.openedDate.setDisabled(isReadOnly("AccountMappingDialog_OpenedDate"));
		this.allowedManualEntry.setDisabled(isReadOnly("AccountMappingDialog_AllowedManualEntry"));
		this.gLDescription.setReadonly(isReadOnly("AccountMappingDialog_GLDescription"));

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
		logger.debug(Literal.ENTERING);
		this.account.setReadonly(true);
		this.hostAccount.setReadonly(true);
		this.profitCenter.setReadonly(true);
		this.costCenter.setReadonly(true);
		this.accountType.setReadonly(true);
		this.finType.setReadonly(true);
		this.openedDate.setReadonly(true);
		this.closedDate.setReadonly(true);
		this.allowedManualEntry.setReadonly(true);
		this.status.setReadonly(true);
		this.gLDescription.setReadonly(true);
		this.accountTypeGroup.setReadonly(true);

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

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.account.setValue("");
		this.hostAccount.setValue("");
		this.profitCenter.setValue("");
		this.costCenter.setValue("");
		this.accountType.setValue("");
		this.finType.setValue("");
		this.openedDate.setValue(null);
		this.closedDate.setValue(null);
		this.allowedManualEntry.setValue("");
		this.status.setValue("");
		this.gLDescription.setValue("");
		this.accountTypeGroup.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

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

		isNew = aAccountMapping.isNewRecord();
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAccountMapping (AccountMapping)
	 * 
	 * @param tranType        (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(AccountMapping aAccountMapping, String tranType) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		AccountMapping aAccountMapping = (AccountMapping) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

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
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public void onSelect$status(Event event) {
		String status = (String) this.status.getSelectedItem().getValue();

		if (HostAccountStatus.isClose(status)) {
			this.spaceClosedDate.setSclass(PennantConstants.mandateSclass);
			this.closedDate.setDisabled(true);
			this.closedDate.setValue(SysParamUtil.getAppDate());
		} else {
			this.spaceClosedDate.setSclass("");
			this.closedDate.setDisabled(false);
		}
	}

	public void onFulfill$finType(Event event) {
		this.account.setValue(getAccount());
	}

	public void onFulfill$accountType(Event event) {
		this.account.setValue(getAccount());

		Object dataObject = accountType.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.accountTypeGroup.setValue(null);
		} else {
			AccountType details = (AccountType) dataObject;
			this.accountTypeGroup.setValue(details.getGroupCode());
		}
	}

	private String getAccount() {
		return StringUtils.trimToEmpty(this.finType.getValue())
				.concat(StringUtils.trimToEmpty(this.accountType.getValue()));
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
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.accountMapping);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
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