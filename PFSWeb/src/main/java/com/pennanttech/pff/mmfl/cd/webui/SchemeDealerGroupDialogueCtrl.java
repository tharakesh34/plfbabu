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
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.mmfl.cd.model.SchemeDealerGroup;
import com.pennanttech.pff.mmfl.cd.service.SchemeDealerGroupService;

public class SchemeDealerGroupDialogueCtrl extends GFCBaseCtrl<SchemeDealerGroup> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SchemeDealerGroupDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_schemeDealerGroupDialogue;

	protected Textbox schemeId;
	protected Button btnSchemeId;
	protected Intbox dealerGroupCode;
	protected Checkbox active;
	protected SchemeDealerGroup schemeDealerGroup;

	private transient SchemeDealerGroupListCtrl schemeDealerGroupListCtrl;
	private transient SchemeDealerGroupService schemeDealerGroupService;

	public SchemeDealerGroupDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CDSchemeDealerGroupDialogue";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(
				String.valueOf(this.schemeDealerGroup.getSchemeDealerGroupId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_schemeDealerGroupDialogue(Event event) throws AppException {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_schemeDealerGroupDialogue);

		try {
			this.schemeDealerGroup = (SchemeDealerGroup) arguments.get("SchemeDealerGroup");
			this.schemeDealerGroupListCtrl = (SchemeDealerGroupListCtrl) arguments.get("schemeDealerGroupListCtrl");

			if (this.schemeDealerGroup == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			SchemeDealerGroup schemeDealerGroup = new SchemeDealerGroup();
			BeanUtils.copyProperties(this.schemeDealerGroup, schemeDealerGroup);
			this.schemeDealerGroup.setBefImage(schemeDealerGroup);

			doLoadWorkFlow(this.schemeDealerGroup.isWorkflow(), this.schemeDealerGroup.getWorkflowId(),
					this.schemeDealerGroup.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CDSchemeDealerGroupDialogue");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.schemeDealerGroup);

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
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CDSchemeDealerGroupDialogue_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CDSchemeDealerGroupDialogue_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CDSchemeDealerGroupDialogue_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CDSchemeDealerGroupDialogue_btnSave"));
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
		doShowNotes(this.schemeDealerGroup);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		schemeDealerGroupListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.schemeDealerGroup.getBefImage());
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
	public void doWriteBeanToComponents(SchemeDealerGroup schemeDealerGroup) {
		logger.debug(Literal.ENTERING);

		this.schemeId.setText(schemeDealerGroup.getPromotionId());
		this.dealerGroupCode.setValue(schemeDealerGroup.getDealerGroupCode());
		this.active.setChecked(schemeDealerGroup.isActive());
		if (schemeDealerGroup.isNewRecord()
				|| (schemeDealerGroup.getRecordType() != null ? schemeDealerGroup.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(schemeDealerGroup.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(SchemeDealerGroup schemeDealerGroup) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			schemeDealerGroup.setPromotionId(this.schemeId.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			schemeDealerGroup.setDealerGroupCode(this.dealerGroupCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			schemeDealerGroup.setActive(this.active.isChecked());
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
	public void doShowDialog(SchemeDealerGroup schemeDealerGroup) {
		logger.debug(Literal.ENTERING);

		if (schemeDealerGroup.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.schemeId.setFocus(true);
		} else {
			this.dealerGroupCode.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(schemeDealerGroup.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(schemeDealerGroup);

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

		if (!this.dealerGroupCode.isReadonly()) {
			this.dealerGroupCode.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_SchemeDealerGroupList_DealerGroupCode.value"), true, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.schemeId.setConstraint("");
		this.dealerGroupCode.setConstraint("");

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

		final SchemeDealerGroup schemeDealerGroup = new SchemeDealerGroup();
		BeanUtils.copyProperties(this.schemeDealerGroup, schemeDealerGroup);

		doDelete(String.valueOf(schemeDealerGroup.getDealerGroupCode()), schemeDealerGroup);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.schemeDealerGroup.isNewRecord()) {
			this.btnSchemeId.setDisabled(false);
			this.dealerGroupCode.setDisabled(false);
		} else {
			this.btnSchemeId.setDisabled(true);
			this.dealerGroupCode.setDisabled(true);
		}

		/*
		 * readOnlyComponent(isReadOnly( "CDSchemeDealerGroupDialogue_DealerGroupCode"), this.dealerGroupCode);
		 */
		readOnlyComponent(isReadOnly("CDSchemeDealerGroupDialogue_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.schemeDealerGroup.isNewRecord()) {
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
		readOnlyComponent(true, this.dealerGroupCode);
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

		this.schemeId.setValue("");
		this.dealerGroupCode.setValue(0);
		this.active.setChecked(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final SchemeDealerGroup schemeDealerGroup = new SchemeDealerGroup();
		BeanUtils.copyProperties(this.schemeDealerGroup, schemeDealerGroup);

		doSetValidation();
		doWriteComponentsToBean(schemeDealerGroup);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(schemeDealerGroup.getRecordType())) {
				schemeDealerGroup.setVersion(schemeDealerGroup.getVersion() + 1);
				if (schemeDealerGroup.isNewRecord()) {
					schemeDealerGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					schemeDealerGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					schemeDealerGroup.setNewRecord(true);
				}
			}
		} else {
			schemeDealerGroup.setVersion(schemeDealerGroup.getVersion() + 1);
			if (schemeDealerGroup.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(schemeDealerGroup, tranType)) {
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
	protected boolean doProcess(SchemeDealerGroup schemeDealerGroup, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		schemeDealerGroup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		schemeDealerGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		schemeDealerGroup.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			schemeDealerGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(schemeDealerGroup.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, schemeDealerGroup);
				}
				if (isNotesMandatory(taskId, schemeDealerGroup)) {
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
			schemeDealerGroup.setTaskId(taskId);
			schemeDealerGroup.setNextTaskId(nextTaskId);
			schemeDealerGroup.setRoleCode(getRole());
			schemeDealerGroup.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(schemeDealerGroup, tranType);
			String operationRefs = getServiceOperations(taskId, schemeDealerGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(schemeDealerGroup, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(schemeDealerGroup, tranType);
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
		SchemeDealerGroup manufacturer = (SchemeDealerGroup) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = schemeDealerGroupService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = schemeDealerGroupService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = schemeDealerGroupService.doApprove(auditHeader);

						if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = schemeDealerGroupService.doReject(auditHeader);
						if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_schemeDealerGroupDialogue, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_schemeDealerGroupDialogue, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.schemeDealerGroup), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (AppException e) {
			logger.error("Exception: ", e);
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

	private AuditHeader getAuditHeader(SchemeDealerGroup manufacturer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, manufacturer.getBefImage(), manufacturer);
		return new AuditHeader(getReference(), null, null, null, auditDetail, manufacturer.getUserDetails(),
				getOverideMap());
	}

	public void onClick$btnSchemeId(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_schemeDealerGroupDialogue, "Promotion",
				String.valueOf(this.schemeId.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.schemeId.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void setSchemeDealerGroupListCtrl(SchemeDealerGroupListCtrl schemeDealerGroupListCtrl) {
		this.schemeDealerGroupListCtrl = schemeDealerGroupListCtrl;
	}

	public void setSchemeDealerGroupService(SchemeDealerGroupService schemeDealerGroupService) {
		this.schemeDealerGroupService = schemeDealerGroupService;
	}

}
