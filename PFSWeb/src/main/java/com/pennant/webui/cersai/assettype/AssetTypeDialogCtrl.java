package com.pennant.webui.cersai.assettype;

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
import com.pennant.backend.model.cersai.AssetTyp;
import com.pennant.backend.service.cersai.AssetTypeService;
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
 * This is the controller class for the /WEB-INF/pages/cersai/AssetType/assetTypeDialog.zul file. <br>
 */
public class AssetTypeDialogCtrl extends GFCBaseCtrl<AssetTyp> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AssetTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AssetTypeDialog;
	protected ExtendedCombobox assetCategoryId;
	protected Space space_Id;
	protected Intbox id;
	protected Space space_Description;
	protected Textbox description;
	private AssetTyp assetTyp; // overhanded per param

	private transient AssetTypeListCtrl assetTypListCtrl; // overhanded per param
	private transient AssetTypeService assetTypService;

	/**
	 * default constructor.<br>
	 */
	public AssetTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssetTypeDialog";
	}

	@Override
	protected String getReference() {
		return getAssetTyp().getAssetCategoryId() + PennantConstants.KEY_SEPERATOR + getAssetTyp().getId()
				+ PennantConstants.KEY_SEPERATOR + getAssetTyp().getDescription();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_AssetTypeDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AssetTypeDialog);

		try {
			// Get the required arguments.
			this.assetTyp = (AssetTyp) arguments.get("assetTyp");
			this.assetTypListCtrl = (AssetTypeListCtrl) arguments.get("assetTypListCtrl");

			if (this.assetTyp == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AssetTyp assetType = new AssetTyp();
			BeanUtils.copyProperties(this.assetTyp, assetType);
			this.assetTyp.setBefImage(assetType);

			// Render the page and display the data.
			doLoadWorkFlow(this.assetTyp.isWorkflow(), this.assetTyp.getWorkflowId(), this.assetTyp.getNextTaskId());

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
			doShowDialog(this.assetTyp);
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
		this.assetCategoryId.setValueType(DataType.LONG);
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssetTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssetTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssetTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssetTypeDialog_btnSave"));
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
		doShowNotes(this.assetTyp);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		assetTypListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.assetTyp.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$assetCategoryId(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = assetCategoryId.getObject();
		if (dataObject instanceof String) {
			this.assetCategoryId.setValue(dataObject.toString());
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
	 * @param assetType
	 * 
	 */
	public void doWriteBeanToComponents(AssetTyp aAssetTyp) {
		logger.debug(Literal.ENTERING);
		if (aAssetTyp.getAssetCategoryId() != null) {
			this.assetCategoryId.setValue(String.valueOf(aAssetTyp.getAssetCategoryId()));
		}
		this.id.setValue(aAssetTyp.getId());
		this.description.setValue(aAssetTyp.getDescription());

		if (aAssetTyp.isNewRecord()) {
			this.assetCategoryId.setDescription("");
		} else {
			this.assetCategoryId.setDescription(aAssetTyp.getAssetCategoryIdName());
		}

		this.recordStatus.setValue(aAssetTyp.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAssetType
	 */
	public void doWriteComponentsToBean(AssetTyp aAssetTyp) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Asset Category Id
		try {
			aAssetTyp.setAssetCategoryId(Long.valueOf(this.assetCategoryId.getValidatedValue()));
			aAssetTyp.setAssetCategoryIdName(this.assetCategoryId.getDescription());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Asset Type Id
		try {
			aAssetTyp.setId(this.id.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aAssetTyp.setDescription(this.description.getValue());
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
	 * @param assetType The entity that need to be render.
	 */
	public void doShowDialog(AssetTyp assetTyp) {
		logger.debug(Literal.LEAVING);

		if (assetTyp.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.assetCategoryId.focus();
		} else {
			this.assetCategoryId.setReadonly(true);
			this.id.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(assetTyp.getRecordType())) {
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

		doWriteBeanToComponents(assetTyp);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.assetCategoryId.isReadonly()) {
			this.assetCategoryId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetTypeDialog_AssetCategoryId.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.id.isReadonly()) {
			this.id.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_AssetTypeDialog_Id.value"), true, false, 0));
		}
		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetTypeDialog_Description.value"),
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
	 * Deletes a AssetType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final AssetTyp aAssetTyp = new AssetTyp();
		BeanUtils.copyProperties(this.assetTyp, aAssetTyp);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aAssetTyp.getAssetCategoryId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aAssetTyp.getRecordType()).equals("")) {
				aAssetTyp.setVersion(aAssetTyp.getVersion() + 1);
				aAssetTyp.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAssetTyp.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aAssetTyp.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aAssetTyp.getNextTaskId(), aAssetTyp);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAssetTyp, tranType)) {
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

		if (this.assetTyp.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.assetCategoryId);
			readOnlyComponent(false, this.id);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.assetCategoryId);
			readOnlyComponent(true, this.id);

		}

		readOnlyComponent(isReadOnly("AssetTypeDialog_Description"), this.description);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.assetTyp.isNewRecord()) {
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
		final AssetTyp aAssetTyp = new AssetTyp();
		BeanUtils.copyProperties(this.assetTyp, aAssetTyp);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aAssetTyp);

		isNew = aAssetTyp.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAssetTyp.getRecordType())) {
				aAssetTyp.setVersion(aAssetTyp.getVersion() + 1);
				if (isNew) {
					aAssetTyp.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAssetTyp.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAssetTyp.setNewRecord(true);
				}
			}
		} else {
			aAssetTyp.setVersion(aAssetTyp.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aAssetTyp, tranType)) {
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
	protected boolean doProcess(AssetTyp aAssetTyp, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAssetTyp.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAssetTyp.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAssetTyp.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAssetTyp.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAssetTyp.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAssetTyp);
				}

				if (isNotesMandatory(taskId, aAssetTyp)) {
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

			aAssetTyp.setTaskId(taskId);
			aAssetTyp.setNextTaskId(nextTaskId);
			aAssetTyp.setRoleCode(getRole());
			aAssetTyp.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAssetTyp, tranType);
			String operationRefs = getServiceOperations(taskId, aAssetTyp);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAssetTyp, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAssetTyp, tranType);
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
		AssetTyp aAssetTyp = (AssetTyp) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = assetTypService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = assetTypService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = assetTypService.doApprove(auditHeader);

					if (aAssetTyp.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = assetTypService.doReject(auditHeader);
					if (aAssetTyp.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AssetTypeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AssetTypeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.assetTyp), true);
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

	private AuditHeader getAuditHeader(AssetTyp aAssetTyp, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAssetTyp.getBefImage(), aAssetTyp);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAssetTyp.getUserDetails(),
				getOverideMap());
	}

	public void setAssetTypService(AssetTypeService assetTypService) {
		this.assetTypService = assetTypService;
	}

	public AssetTyp getAssetTyp() {
		return assetTyp;
	}

	public void setAssetTyp(AssetTyp assetTyp) {
		this.assetTyp = assetTyp;
	}

}
