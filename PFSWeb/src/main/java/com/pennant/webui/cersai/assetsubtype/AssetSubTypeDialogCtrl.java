package com.pennant.webui.cersai.assetsubtype;

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
import com.pennant.backend.model.cersai.AssetSubType;
import com.pennant.backend.service.cersai.AssetSubTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/cersai/AssetSubType/assetSubTypeDialog.zul file. <br>
 */
public class AssetSubTypeDialogCtrl extends GFCBaseCtrl<AssetSubType> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AssetSubTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AssetSubTypeDialog;
	protected Space space_AssetTypeId;
	protected ExtendedCombobox assetTypeId;
	protected Space space_Id;
	protected Intbox id;
	protected Space space_Description;
	protected Textbox description;
	private AssetSubType assetSubType; // overhanded per param

	public AssetSubType getAssetSubType() {
		return assetSubType;
	}

	public void setAssetSubType(AssetSubType assetSubType) {
		this.assetSubType = assetSubType;
	}

	private transient AssetSubTypeListCtrl assetSubTypeListCtrl; // overhanded per param
	private transient AssetSubTypeService assetSubTypeService;

	/**
	 * default constructor.<br>
	 */
	public AssetSubTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssetSubTypeDialog";
	}

	@Override
	protected String getReference() {
		return getAssetSubType().getAssetTypeId() + PennantConstants.KEY_SEPERATOR + getAssetSubType().getId()
				+ PennantConstants.KEY_SEPERATOR + getAssetSubType().getDescription();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_AssetSubTypeDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AssetSubTypeDialog);

		try {
			// Get the required arguments.
			this.assetSubType = (AssetSubType) arguments.get("assetSubType");
			this.assetSubTypeListCtrl = (AssetSubTypeListCtrl) arguments.get("assetSubTypeListCtrl");

			if (this.assetSubType == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AssetSubType assetSubType = new AssetSubType();
			BeanUtils.copyProperties(this.assetSubType, assetSubType);
			this.assetSubType.setBefImage(assetSubType);

			// Render the page and display the data.
			doLoadWorkFlow(this.assetSubType.isWorkflow(), this.assetSubType.getWorkflowId(),
					this.assetSubType.getNextTaskId());

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
			doShowDialog(this.assetSubType);
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

		this.assetTypeId.setMandatoryStyle(true);
		this.assetTypeId.setModuleName("AssetTyp");
		this.assetTypeId.setValueColumn("Id");
		this.assetTypeId.setDescColumn("Description");
		this.assetTypeId.setValidateColumns(new String[] { "Id" });
		this.assetTypeId.setValueType(DataType.LONG);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssetSubTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssetSubTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssetSubTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssetSubTypeDialog_btnSave"));
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
		doShowNotes(this.assetSubType);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		assetSubTypeListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.assetSubType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param assetSubType
	 * 
	 */
	public void doWriteBeanToComponents(AssetSubType aAssetSubType) {
		logger.debug(Literal.ENTERING);

		if (aAssetSubType.getAssetTypeId() != null) {
			this.assetTypeId.setValue(String.valueOf(aAssetSubType.getAssetTypeId()));
		}
		this.id.setValue(aAssetSubType.getId());
		this.description.setValue(aAssetSubType.getDescription());

		if (aAssetSubType.isNewRecord()) {
			this.assetTypeId.setDescription("");
		} else {
			this.assetTypeId.setDescription(String.valueOf(aAssetSubType.getAssetTypeId()));
		}
		if (aAssetSubType.isNewRecord()) {
			this.assetTypeId.setDescription("");
		} else {
			this.assetTypeId.setDescription(aAssetSubType.getAssetTypeIdName());
		}
		this.recordStatus.setValue(aAssetSubType.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAssetSubType
	 */
	public void doWriteComponentsToBean(AssetSubType aAssetSubType) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Asset Type Id
		try {
			this.assetTypeId.getValidatedValue();
			Long assetid = Long.valueOf(this.assetTypeId.getValue());
			aAssetSubType.setAssetTypeId((assetid));
			aAssetSubType.setAssetTypeIdName(this.assetTypeId.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Asset Sub Type ID
		try {
			aAssetSubType.setId(this.id.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aAssetSubType.setDescription(this.description.getValue());
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
	 * @param assetSubType The entity that need to be render.
	 */
	public void doShowDialog(AssetSubType assetSubType) {
		logger.debug(Literal.LEAVING);

		if (assetSubType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.assetTypeId.focus();
		} else {
			this.assetTypeId.setReadonly(true);
			this.id.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(assetSubType.getRecordType())) {
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

		doWriteBeanToComponents(assetSubType);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.assetTypeId.isReadonly()) {
			this.assetTypeId.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_AssetSubTypeDialog_AssetTypeId.value"), true, false, 0));
		}
		if (!this.id.isReadonly()) {
			this.id.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_AssetSubTypeDialog_Id.value"), true, false, 0));
		}
		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetSubTypeDialog_Description.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.assetTypeId.setConstraint("");
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
	 * Deletes a AssetSubType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final AssetSubType aAssetSubType = new AssetSubType();
		BeanUtils.copyProperties(this.assetSubType, aAssetSubType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aAssetSubType.getAssetTypeId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aAssetSubType.getRecordType()).equals("")) {
				aAssetSubType.setVersion(aAssetSubType.getVersion() + 1);
				aAssetSubType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAssetSubType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aAssetSubType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aAssetSubType.getNextTaskId(),
							aAssetSubType);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAssetSubType, tranType)) {
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

		if (this.assetSubType.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.assetTypeId);
			readOnlyComponent(false, this.id);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.assetTypeId);
			readOnlyComponent(true, this.id);

		}

		readOnlyComponent(isReadOnly("AssetSubTypeDialog_Description"), this.description);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.assetSubType.isNewRecord()) {
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

		readOnlyComponent(true, this.assetTypeId);
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
		this.assetTypeId.setValue("");
		this.assetTypeId.setDescription("");
		this.id.setText("");
		this.description.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final AssetSubType aAssetSubType = new AssetSubType();
		BeanUtils.copyProperties(this.assetSubType, aAssetSubType);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aAssetSubType);

		isNew = aAssetSubType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAssetSubType.getRecordType())) {
				aAssetSubType.setVersion(aAssetSubType.getVersion() + 1);
				if (isNew) {
					aAssetSubType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAssetSubType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAssetSubType.setNewRecord(true);
				}
			}
		} else {
			aAssetSubType.setVersion(aAssetSubType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aAssetSubType, tranType)) {
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
	protected boolean doProcess(AssetSubType aAssetSubType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAssetSubType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAssetSubType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAssetSubType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAssetSubType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAssetSubType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAssetSubType);
				}

				if (isNotesMandatory(taskId, aAssetSubType)) {
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

			aAssetSubType.setTaskId(taskId);
			aAssetSubType.setNextTaskId(nextTaskId);
			aAssetSubType.setRoleCode(getRole());
			aAssetSubType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAssetSubType, tranType);
			String operationRefs = getServiceOperations(taskId, aAssetSubType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAssetSubType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAssetSubType, tranType);
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
		AssetSubType aAssetSubType = (AssetSubType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = assetSubTypeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = assetSubTypeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = assetSubTypeService.doApprove(auditHeader);

					if (aAssetSubType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = assetSubTypeService.doReject(auditHeader);
					if (aAssetSubType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AssetSubTypeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AssetSubTypeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.assetSubType), true);
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

	private AuditHeader getAuditHeader(AssetSubType aAssetSubType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAssetSubType.getBefImage(), aAssetSubType);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAssetSubType.getUserDetails(),
				getOverideMap());
	}

	public void setAssetSubTypeService(AssetSubTypeService assetSubTypeService) {
		this.assetSubTypeService = assetSubTypeService;
	}

}
