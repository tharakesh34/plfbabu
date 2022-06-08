package com.pennanttech.pff.mmfl.cd.webui;

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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.mmfl.cd.model.SchemeProductGroup;
import com.pennanttech.pff.mmfl.cd.service.SchemeProductGroupService;

public class SchemeProductGroupDialogueCtrl extends GFCBaseCtrl<SchemeProductGroup> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SchemeProductGroupDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_schemeProductGroupDialogue;

	protected Textbox schemeId;
	protected Button btnSchemeId;
	protected Intbox productGroupCode;
	protected Uppercasebox posVendor;
	protected Checkbox active;
	protected SchemeProductGroup schemeProductGroup;

	private transient SchemeProductGroupListCtrl schemeProductGroupListCtrl;
	private transient SchemeProductGroupService schemeProductGroupService;

	public SchemeProductGroupDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CDSchemeProductGroupDialogue";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(
				String.valueOf(this.schemeProductGroup.getSchemeProductGroupId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_schemeProductGroupDialogue(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_schemeProductGroupDialogue);

		try {
			this.schemeProductGroup = (SchemeProductGroup) arguments.get("SchemeProductGroup");
			this.schemeProductGroupListCtrl = (SchemeProductGroupListCtrl) arguments.get("schemeProductGroupListCtrl");

			if (this.schemeProductGroup == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			SchemeProductGroup schemeProductGroup = new SchemeProductGroup();
			BeanUtils.copyProperties(this.schemeProductGroup, schemeProductGroup);
			this.schemeProductGroup.setBefImage(schemeProductGroup);

			doLoadWorkFlow(this.schemeProductGroup.isWorkflow(), this.schemeProductGroup.getWorkflowId(),
					this.schemeProductGroup.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CDSchemeProductGroupDialogue");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.schemeProductGroup);

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
		this.productGroupCode.setMaxlength(8);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CDSchemeProductGroupDialogue_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CDSchemeProductGroupDialogue_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CDSchemeProductGroupDialogue_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CDSchemeProductGroupDialogue_btnSave"));
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
		doShowNotes(this.schemeProductGroup);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		schemeProductGroupListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.schemeProductGroup.getBefImage());
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
	public void doWriteBeanToComponents(SchemeProductGroup schemeProductGroup) {
		logger.debug(Literal.ENTERING);

		this.schemeId.setText(schemeProductGroup.getPromotionId());
		this.productGroupCode.setValue(schemeProductGroup.getProductGroupCode());
		this.posVendor.setValue(schemeProductGroup.isPOSVendor() ? "1" : "0");
		this.active.setChecked(schemeProductGroup.isActive());
		if (schemeProductGroup.isNewRecord()
				|| (schemeProductGroup.getRecordType() != null ? schemeProductGroup.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(schemeProductGroup.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(SchemeProductGroup schemeProductGroup) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			schemeProductGroup.setPromotionId(this.schemeId.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			schemeProductGroup.setProductGroupCode(this.productGroupCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			schemeProductGroup.setPOSVendor(this.posVendor.getValue().equals("1"));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			schemeProductGroup.setActive(this.active.isChecked());
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
	public void doShowDialog(SchemeProductGroup schemeProductGroup) {
		logger.debug(Literal.ENTERING);

		if (schemeProductGroup.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.schemeId.setFocus(true);
		} else {
			this.productGroupCode.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(schemeProductGroup.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(schemeProductGroup);

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

		if (!this.btnSchemeId.isDisabled()) {
			this.schemeId.setConstraint(new PTStringValidator(Labels.getLabel("label_SchemeDealerGroup_SchemeId.value"),
					null, true, 1, 3800));
		}

		if (!this.productGroupCode.isReadonly()) {
			this.productGroupCode.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_SchemeProductGroupList_ProductGroupCode.value"), true, false));
		}

		if (!this.schemeId.getText().equals("")) {
			if (!this.posVendor.isReadonly()) {
				if (!this.posVendor.getText().equals("0") && !this.posVendor.getText().equals("1")) {
					throw new WrongValueException(this.posVendor,
							Labels.getLabel("label_SchemeProductDeatislDialoguePOSVendorAlert.value"));
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.schemeId.setConstraint("");
		this.productGroupCode.setConstraint("");
		this.posVendor.setConstraint("");

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

		final SchemeProductGroup schemeProductGroup = new SchemeProductGroup();
		BeanUtils.copyProperties(this.schemeProductGroup, schemeProductGroup);

		doDelete(String.valueOf(schemeProductGroup.getProductGroupCode()), schemeProductGroup);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.schemeProductGroup.isNewRecord()) {
			this.btnSchemeId.setDisabled(false);
			this.productGroupCode.setDisabled(false);
		} else {
			this.btnSchemeId.setDisabled(true);
			this.productGroupCode.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("CDSchemeProductGroupDialogue_ProductGroupCode"), this.productGroupCode);
		readOnlyComponent(isReadOnly("CDSchemeProductGroupDialogue_POSVendor"), this.posVendor);
		readOnlyComponent(isReadOnly("CDSchemeDealerGroupDialogue_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.schemeProductGroup.isNewRecord()) {
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

		readOnlyComponent(true, this.schemeId);
		readOnlyComponent(true, this.productGroupCode);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.posVendor);

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

		this.schemeId.setValue("");
		this.productGroupCode.setValue(0);
		this.active.setChecked(false);
		this.posVendor.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final SchemeProductGroup schemeProductGroup = new SchemeProductGroup();
		BeanUtils.copyProperties(this.schemeProductGroup, schemeProductGroup);

		doSetValidation();
		doWriteComponentsToBean(schemeProductGroup);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(schemeProductGroup.getRecordType())) {
				schemeProductGroup.setVersion(schemeProductGroup.getVersion() + 1);
				if (schemeProductGroup.isNewRecord()) {
					schemeProductGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					schemeProductGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					schemeProductGroup.setNewRecord(true);
				}
			}
		} else {
			schemeProductGroup.setVersion(schemeProductGroup.getVersion() + 1);
			if (schemeProductGroup.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(schemeProductGroup, tranType)) {
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
	protected boolean doProcess(SchemeProductGroup schemeProductGroup, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		schemeProductGroup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		schemeProductGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		schemeProductGroup.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			schemeProductGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(schemeProductGroup.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, schemeProductGroup);
				}
				if (isNotesMandatory(taskId, schemeProductGroup)) {
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
			schemeProductGroup.setTaskId(taskId);
			schemeProductGroup.setNextTaskId(nextTaskId);
			schemeProductGroup.setRoleCode(getRole());
			schemeProductGroup.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(schemeProductGroup, tranType);
			String operationRefs = getServiceOperations(taskId, schemeProductGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(schemeProductGroup, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(schemeProductGroup, tranType);
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
		SchemeProductGroup manufacturer = (SchemeProductGroup) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = schemeProductGroupService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = schemeProductGroupService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = schemeProductGroupService.doApprove(auditHeader);

					if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = schemeProductGroupService.doReject(auditHeader);
					if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_schemeProductGroupDialogue, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_schemeProductGroupDialogue, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.schemeProductGroup), true);
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

	private AuditHeader getAuditHeader(SchemeProductGroup manufacturer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, manufacturer.getBefImage(), manufacturer);
		return new AuditHeader(getReference(), null, null, null, auditDetail, manufacturer.getUserDetails(),
				getOverideMap());
	}

	public void onClick$btnSchemeId(Event event) {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_schemeProductGroupDialogue, "Promotion",
				String.valueOf(this.schemeId.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.schemeId.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void setSchemeProductGroupService(SchemeProductGroupService schemeProductGroupService) {
		this.schemeProductGroupService = schemeProductGroupService;
	}

	public void setSchemeProductGroupListCtrl(SchemeProductGroupListCtrl schemeProductGroupListCtrl) {
		this.schemeProductGroupListCtrl = schemeProductGroupListCtrl;
	}

}
