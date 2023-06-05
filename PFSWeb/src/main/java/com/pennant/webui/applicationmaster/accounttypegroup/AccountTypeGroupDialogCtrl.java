package com.pennant.webui.applicationmaster.accounttypegroup;

import java.sql.Timestamp;
import java.util.ArrayList;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.AccountTypeGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class AccountTypeGroupDialogCtrl extends GFCBaseCtrl<AccountTypeGroup> {
	private static final long serialVersionUID = -210929672381582779L;
	private static final Logger logger = LogManager.getLogger(AccountTypeGroupDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AccounTypeGroupDialog;

	protected Intbox acctTypeLevel;
	protected Textbox groupCode;
	protected Textbox groupDescription;
	protected ExtendedCombobox parentGroupId;

	// not autoWired Var's
	private AccountTypeGroup accountTypeGroup; // overHanded per parameter
	private transient AccountTypeGroupListCtrl accountTypeGroupListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	protected Checkbox groupIsActive; // autoWired
	// ServiceDAOs / Domain Classes
	private transient AccountTypeGroupService accountTypeGroupService;

	/**
	 * default constructor.<br>
	 */
	public AccountTypeGroupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AccountTypeGroupDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected AccountTypeGroup object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_AccounTypeGroupDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AccounTypeGroupDialog);

		try {
			/* set components visible dependent of the users rights */

			if (arguments.containsKey("accountTypeGroup")) {
				this.accountTypeGroup = (AccountTypeGroup) arguments.get("accountTypeGroup");
				AccountTypeGroup befImage = new AccountTypeGroup();
				BeanUtils.copyProperties(this.accountTypeGroup, befImage);
				this.accountTypeGroup.setBefImage(befImage);

				setAccountTypeGroup(this.accountTypeGroup);
			} else {
				setAccountTypeGroup(null);
			}

			doLoadWorkFlow(this.accountTypeGroup.isWorkflow(), this.accountTypeGroup.getWorkflowId(),
					this.accountTypeGroup.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the accountTypeGroupListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete accountTypeGroup here.
			if (arguments.containsKey("accountTypeGroupListCtrl")) {
				setAccountTypeGroupListCtrl((AccountTypeGroupListCtrl) arguments.get("accountTypeGroupListCtrl"));
			} else {
				setAccountTypeGroupListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getAccountTypeGroup());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AccounTypeGroupDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		this.acctTypeLevel.setMaxlength(3);
		this.groupCode.setMaxlength(15);
		this.groupDescription.setMaxlength(50);

		this.parentGroupId.setWidth("200px");
		this.parentGroupId.setModuleName("AccountTypeGroup");
		this.parentGroupId.setMandatoryStyle(false);
		this.parentGroupId.setValueColumn("GroupCode");
		this.parentGroupId.setDescColumn("GroupDescription");
		this.parentGroupId.setDisplayStyle(2);
		this.parentGroupId.setValidateColumns(new String[] { "GroupCode" });

		if (this.accountTypeGroup.getAcctTypeLevel() != 0) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("AcctTypeLevel", this.accountTypeGroup.getAcctTypeLevel() - 1, Filter.OP_EQUAL);
			this.parentGroupId.setFilters(filters);
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountTypeGroupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountTypeGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AccountTypeGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountTypeGroupDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
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
		MessageUtil.showHelpWindow(event, window_AccounTypeGroupDialog);
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.accountTypeGroup.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountTypeGroup
	 * 
	 */
	public void doWriteBeanToComponents(AccountTypeGroup aAccountTypeGroup) {
		logger.debug("Entering ");
		this.acctTypeLevel.setValue(aAccountTypeGroup.getAcctTypeLevel());
		this.groupCode.setValue(aAccountTypeGroup.getGroupCode());
		this.groupDescription.setValue(aAccountTypeGroup.getGroupDescription());
		this.groupIsActive.setChecked(aAccountTypeGroup.isGroupIsActive());
		if (aAccountTypeGroup.getParentGroupId() != Long.MIN_VALUE && aAccountTypeGroup.getParentGroupId() != 0) {
			this.parentGroupId.setAttribute("ParentGroupId", aAccountTypeGroup.getParentGroupId());
			this.parentGroupId.setValue(aAccountTypeGroup.getParentGroup(), aAccountTypeGroup.getParentGroupDesc());
		}
		if (this.acctTypeLevel.getValue() > 1) {
			this.parentGroupId.setMandatoryStyle(true);
		} else {
			this.parentGroupId.setSclass("");
		}
		if (aAccountTypeGroup.isNewRecord()
				|| (aAccountTypeGroup.getRecordType() != null ? aAccountTypeGroup.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.groupIsActive.setChecked(true);
			this.groupIsActive.setDisabled(true);
		}
		this.recordStatus.setValue(aAccountTypeGroup.getRecordStatus());
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccountTypeGroup
	 */
	public void doWriteComponentsToBean(AccountTypeGroup aAccountTypeGroup) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAccountTypeGroup.setGroupCode(this.groupCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAccountTypeGroup.setAcctTypeLevel(this.acctTypeLevel.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAccountTypeGroup.setGroupDescription(this.groupDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.parentGroupId.getValidatedValue();
			Long obj = (Long) this.parentGroupId.getAttribute("ParentGroupId");
			if (obj != null) {
				aAccountTypeGroup.setParentGroupId((obj));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountTypeGroup.setGroupIsActive(this.groupIsActive.isChecked());
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

		aAccountTypeGroup.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aAccountTypeGroup
	 */
	public void doShowDialog(AccountTypeGroup aAccountTypeGroup) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aAccountTypeGroup.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.acctTypeLevel.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aAccountTypeGroup.getRecordType())) {
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
			// fill the components with the data
			doWriteBeanToComponents(aAccountTypeGroup);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AccounTypeGroupDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.acctTypeLevel.isReadonly()) {
			this.acctTypeLevel.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AccounTypeGroupDialog_AcctTypeLevel.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.groupCode.isReadonly()) {
			this.groupCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AccounTypeGroupDialog_GroupCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.groupDescription.isReadonly()) {
			this.groupDescription.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AccounTypeGroupDialog_GroupDescription.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.parentGroupId.isReadonly()) {
			if (this.acctTypeLevel.getValue() > 1) {
				this.parentGroupId.setConstraint(new PTStringValidator(
						Labels.getLabel("label_AccounTypeGroupDialog_ParentGroup.value"), null, true));
			} else {
				this.parentGroupId.setConstraint(new PTStringValidator(
						Labels.getLabel("label_AccounTypeGroupDialog_ParentGroup.value"), null, false));
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.acctTypeLevel.setConstraint("");
		this.groupCode.setConstraint("");
		this.groupDescription.setConstraint("");
		this.parentGroupId.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.acctTypeLevel.setErrorMessage("");
		this.groupCode.setErrorMessage("");
		this.groupDescription.setErrorMessage("");
		this.parentGroupId.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getAccountTypeGroupListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AccountTypeGroup aAccountTypeGroup = new AccountTypeGroup();
		BeanUtils.copyProperties(getAccountTypeGroup(), aAccountTypeGroup);

		doDelete(Labels.getLabel("label_AccountTypeGroupSearch_GroupCode.value") + " : "
				+ aAccountTypeGroup.getGroupCode(), aAccountTypeGroup);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");

		if (getAccountTypeGroup().isNewRecord()) {
			this.groupCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.groupCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.acctTypeLevel.setReadonly(isReadOnly("AccountTypeGroupDialog_AcctTypeLevel"));
		this.groupDescription.setReadonly(isReadOnly("AccountTypeGroupDialog_GroupDescription"));
		this.parentGroupId.setReadonly(isReadOnly("AccountTypeGroupDialog_ParentGroup"));
		this.groupIsActive.setDisabled(isReadOnly("AccounTypeGroupDialog_GroupIsActive"));

		if (isWorkFlowEnabled()) {

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.accountTypeGroup.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}

		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.acctTypeLevel.setReadonly(true);
		this.groupCode.setReadonly(true);
		this.groupDescription.setReadonly(true);
		this.parentGroupId.setReadonly(true);
		this.groupIsActive.setDisabled(true);

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

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before
		this.acctTypeLevel.setValue(0);
		this.groupCode.setValue("");
		this.groupDescription.setValue("");
		this.parentGroupId.setValue("");
		this.groupIsActive.setChecked(false);
		logger.debug("Leaving ");
	}

	public void onChange$acctTypeLevel(Event event) {
		this.parentGroupId.setDescription("");
		this.parentGroupId.setValue("");
		if (this.acctTypeLevel.getValue() != null) {
			if (this.acctTypeLevel.getValue() != 0) {
				Filter[] filters = new Filter[1];
				filters[0] = new Filter("AcctTypeLevel", this.acctTypeLevel.getValue() - 1, Filter.OP_EQUAL);
				this.parentGroupId.setFilters(filters);
			}
			if (this.acctTypeLevel.getValue() > 1) {
				this.parentGroupId.setMandatoryStyle(true);
			} else {
				this.parentGroupId.setMandatoryStyle(false);
			}
		} else {
			this.acctTypeLevel.setValue(0);
		}
	}

	public void onFulfill$parentGroupId(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = parentGroupId.getObject();
		if (dataObject instanceof String) {
			this.parentGroupId.setValue(dataObject.toString());
			this.parentGroupId.setDescription("");
		} else {
			AccountTypeGroup details = (AccountTypeGroup) dataObject;
			if (details != null) {
				this.parentGroupId.setAttribute("ParentGroupId", details.getGroupId());
				if (this.acctTypeLevel.getValue() == 0 && this.parentGroupId.getValue() != null) {
					this.acctTypeLevel.setValue((int) (details.getAcctTypeLevel() + 1));
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final AccountTypeGroup aAccountTypeGroup = new AccountTypeGroup();
		BeanUtils.copyProperties(getAccountTypeGroup(), aAccountTypeGroup);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the AccountTypeGroup object with the components data
		doWriteComponentsToBean(aAccountTypeGroup);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aAccountTypeGroup.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAccountTypeGroup.getRecordType())) {
				aAccountTypeGroup.setVersion(aAccountTypeGroup.getVersion() + 1);
				if (isNew) {
					aAccountTypeGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAccountTypeGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAccountTypeGroup.setNewRecord(true);
				}
			}
		} else {
			aAccountTypeGroup.setVersion(aAccountTypeGroup.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aAccountTypeGroup, tranType)) {
				refreshList();
				// Close the Existing Dialog
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
	 * @param aAccountTypeGroup (AccountTypeGroup)
	 * 
	 * @param tranType          (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(AccountTypeGroup aAccountTypeGroup, String tranType) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAccountTypeGroup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAccountTypeGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountTypeGroup.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAccountTypeGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountTypeGroup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAccountTypeGroup);
				}

				if (isNotesMandatory(taskId, aAccountTypeGroup)) {
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

			aAccountTypeGroup.setTaskId(taskId);
			aAccountTypeGroup.setNextTaskId(nextTaskId);
			aAccountTypeGroup.setRoleCode(getRole());
			aAccountTypeGroup.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountTypeGroup, tranType);

			String operationRefs = getServiceOperations(taskId, aAccountTypeGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAccountTypeGroup, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aAccountTypeGroup, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AccountTypeGroup aAccountTypeGroup = (AccountTypeGroup) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getAccountTypeGroupService().delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = getAccountTypeGroupService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getAccountTypeGroupService().doApprove(auditHeader);

					if (aAccountTypeGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getAccountTypeGroupService().doReject(auditHeader);
					if (aAccountTypeGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AccounTypeGroupDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_AccounTypeGroupDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.accountTypeGroup), true);
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

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAccountTypeGroup
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(AccountTypeGroup aAccountTypeGroup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountTypeGroup.getBefImage(), aAccountTypeGroup);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAccountTypeGroup.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.accountTypeGroup);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return String.valueOf(getAccountTypeGroup().getGroupId());
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

	public AccountTypeGroup getAccountTypeGroup() {
		return this.accountTypeGroup;
	}

	public void setAccountTypeGroup(AccountTypeGroup accountTypeGroup) {
		this.accountTypeGroup = accountTypeGroup;
	}

	public void setAccountTypeGroupService(AccountTypeGroupService accountTypeGroupService) {
		this.accountTypeGroupService = accountTypeGroupService;
	}

	public AccountTypeGroupService getAccountTypeGroupService() {
		return this.accountTypeGroupService;
	}

	public void setAccountTypeGroupListCtrl(AccountTypeGroupListCtrl accountTypeGroupListCtrl) {
		this.accountTypeGroupListCtrl = accountTypeGroupListCtrl;
	}

	public AccountTypeGroupListCtrl getAccountTypeGroupListCtrl() {
		return this.accountTypeGroupListCtrl;
	}

}
