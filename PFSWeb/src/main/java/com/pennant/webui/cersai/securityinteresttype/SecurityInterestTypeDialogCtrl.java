package com.pennant.webui.cersai.securityinteresttype;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.AssetCategory;
import com.pennant.backend.model.cersai.SecurityInterestType;
import com.pennant.backend.service.cersai.SecurityInterestTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/cersai/SecurityInterestType/securityInterestTypeDialog.zul file.
 * <br>
 */
public class SecurityInterestTypeDialogCtrl extends GFCBaseCtrl<SecurityInterestType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SecurityInterestTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SecurityInterestTypeDialog;
	protected ExtendedCombobox assetCategoryId;
	protected Space space_Id;
	protected Intbox id;
	protected Space space_Description;
	protected Textbox description;
	private SecurityInterestType securityInterestType; // overhanded per param

	private transient SecurityInterestTypeListCtrl securityInterestTypeListCtrl; // overhanded per param
	private transient SecurityInterestTypeService securityInterestTypeService;

	/**
	 * default constructor.<br>
	 */
	public SecurityInterestTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityInterestTypeDialog";
	}

	@Override
	protected String getReference() {
		return getSecurityInterestType().getAssetCategoryId() + PennantConstants.KEY_SEPERATOR
				+ getSecurityInterestType().getId() + PennantConstants.KEY_SEPERATOR
				+ getSecurityInterestType().getDescription();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_SecurityInterestTypeDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_SecurityInterestTypeDialog);

		try {
			// Get the required arguments.
			this.securityInterestType = (SecurityInterestType) arguments.get("securityInterestType");
			this.securityInterestTypeListCtrl = (SecurityInterestTypeListCtrl) arguments
					.get("securityInterestTypeListCtrl");

			if (this.securityInterestType == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			SecurityInterestType securityInterestType = new SecurityInterestType();
			BeanUtils.copyProperties(this.securityInterestType, securityInterestType);
			this.securityInterestType.setBefImage(securityInterestType);

			// Render the page and display the data.
			doLoadWorkFlow(this.securityInterestType.isWorkflow(), this.securityInterestType.getWorkflowId(),
					this.securityInterestType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.securityInterestType);
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

		this.assetCategoryId.setMandatoryStyle(true);
		this.assetCategoryId.setModuleName("AssetCategory");
		this.assetCategoryId.setValueColumn("Id");
		this.assetCategoryId.setDescColumn("Description");
		this.assetCategoryId.setValidateColumns(new String[] { "Id" });
		this.id.setMaxlength(3);
		this.description.setMaxlength(100);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityInterestTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityInterestTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityInterestTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityInterestTypeDialog_btnSave"));
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
		doShowNotes(this.securityInterestType);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		securityInterestTypeListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.securityInterestType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$assetCategoryId(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = assetCategoryId.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.assetCategoryId.setValue("");
			this.assetCategoryId.setDescription("");
			this.assetCategoryId.setAttribute("", null);
		} else {
			AssetCategory assetCategory = (AssetCategory) dataObject;
			if (assetCategory != null) {
				this.assetCategoryId.setValue(String.valueOf(assetCategory.getId()));
				this.assetCategoryId.setDescription(assetCategory.getDescription());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param securityInterestType
	 * 
	 */
	public void doWriteBeanToComponents(SecurityInterestType aSecurityInterestType) {
		logger.debug(Literal.ENTERING);

		this.assetCategoryId.setValue(aSecurityInterestType.getAssetCategoryId());
		this.id.setValue(aSecurityInterestType.getId());
		this.description.setValue(aSecurityInterestType.getDescription());

		if (aSecurityInterestType.isNewRecord()) {
			this.assetCategoryId.setDescription("");
		} else {
			this.assetCategoryId.setDescription(aSecurityInterestType.getAssetCategoryIdName());
		}

		this.recordStatus.setValue(aSecurityInterestType.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityInterestType
	 */
	public void doWriteComponentsToBean(SecurityInterestType aSecurityInterestType) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Asset Category Id
		try {
			aSecurityInterestType.setAssetCategoryId(this.assetCategoryId.getValue());
			aSecurityInterestType.setAssetCategoryIdName(this.assetCategoryId.getDescription());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// SI Type
		try {
			aSecurityInterestType.setId(this.id.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aSecurityInterestType.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param securityInterestType The entity that need to be render.
	 */
	public void doShowDialog(SecurityInterestType securityInterestType) {
		logger.debug(Literal.LEAVING);

		if (securityInterestType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.assetCategoryId.focus();
		} else {
			this.assetCategoryId.setReadonly(true);
			this.id.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(securityInterestType.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.description.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(securityInterestType);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.assetCategoryId.isReadonly()) {
			this.assetCategoryId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_SecurityInterestTypeDialog_AssetCategoryId.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.id.isReadonly()) {
			this.id.setConstraint(new PTNumberValidator(Labels.getLabel("label_SecurityInterestTypeDialog_Id.value"),
					true, false, 0));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(
					new PTStringValidator(Labels.getLabel("label_SecurityInterestTypeDialog_Description.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.assetCategoryId.setConstraint("");
		this.id.setConstraint("");
		this.description.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

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

	/**
	 * Deletes a SecurityInterestType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final SecurityInterestType aSecurityInterestType = new SecurityInterestType();
		BeanUtils.copyProperties(this.securityInterestType, aSecurityInterestType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aSecurityInterestType.getAssetCategoryId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aSecurityInterestType.getRecordType()).equals("")) {
				aSecurityInterestType.setVersion(aSecurityInterestType.getVersion() + 1);
				aSecurityInterestType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSecurityInterestType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aSecurityInterestType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aSecurityInterestType.getNextTaskId(),
							aSecurityInterestType);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSecurityInterestType, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.securityInterestType.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.assetCategoryId);
			readOnlyComponent(false, this.id);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.assetCategoryId);
			readOnlyComponent(true, this.id);

		}

		readOnlyComponent(isReadOnly("SecurityInterestTypeDialog_Description"), this.description);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.securityInterestType.isNewRecord()) {
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
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.assetCategoryId);
		readOnlyComponent(true, this.id);
		readOnlyComponent(true, this.description);

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
		logger.debug("Entering");
		this.assetCategoryId.setValue("");
		this.assetCategoryId.setDescription("");
		this.id.setText("");
		this.description.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final SecurityInterestType aSecurityInterestType = new SecurityInterestType();
		BeanUtils.copyProperties(this.securityInterestType, aSecurityInterestType);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aSecurityInterestType);

		isNew = aSecurityInterestType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSecurityInterestType.getRecordType())) {
				aSecurityInterestType.setVersion(aSecurityInterestType.getVersion() + 1);
				if (isNew) {
					aSecurityInterestType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSecurityInterestType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityInterestType.setNewRecord(true);
				}
			}
		} else {
			aSecurityInterestType.setVersion(aSecurityInterestType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aSecurityInterestType, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	protected boolean doProcess(SecurityInterestType aSecurityInterestType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSecurityInterestType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSecurityInterestType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityInterestType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSecurityInterestType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityInterestType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSecurityInterestType);
				}

				if (isNotesMandatory(taskId, aSecurityInterestType)) {
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

			aSecurityInterestType.setTaskId(taskId);
			aSecurityInterestType.setNextTaskId(nextTaskId);
			aSecurityInterestType.setRoleCode(getRole());
			aSecurityInterestType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSecurityInterestType, tranType);
			String operationRefs = getServiceOperations(taskId, aSecurityInterestType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSecurityInterestType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSecurityInterestType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		SecurityInterestType aSecurityInterestType = (SecurityInterestType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = securityInterestTypeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = securityInterestTypeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = securityInterestTypeService.doApprove(auditHeader);

					if (aSecurityInterestType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = securityInterestTypeService.doReject(auditHeader);
					if (aSecurityInterestType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_SecurityInterestTypeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_SecurityInterestTypeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.securityInterestType), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(SecurityInterestType aSecurityInterestType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityInterestType.getBefImage(),
				aSecurityInterestType);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aSecurityInterestType.getUserDetails(),
				getOverideMap());
	}

	public void setSecurityInterestTypeService(SecurityInterestTypeService securityInterestTypeService) {
		this.securityInterestTypeService = securityInterestTypeService;
	}

	public SecurityInterestType getSecurityInterestType() {
		return securityInterestType;
	}

	public void setSecurityInterestType(SecurityInterestType securityInterestType) {
		this.securityInterestType = securityInterestType;
	}

}
