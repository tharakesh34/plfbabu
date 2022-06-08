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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.mmfl.cd.model.Manufacturer;
import com.pennanttech.pff.mmfl.cd.service.ManufacturerService;

public class ManufacturerDialogueCtrl extends GFCBaseCtrl<Manufacturer> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ManufacturerDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_manufacturerDialogue;

	protected Textbox name;
	protected Textbox description;
	protected Button btnchannels;
	protected Textbox txtchannel;
	protected Checkbox active;
	protected Manufacturer manufacturer;

	private transient ManufacturerListCtrl manufacturerListCtrl;
	private transient ManufacturerService manufacturerService;

	public ManufacturerDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CDManufacturersDialogue";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.manufacturer.getManufacturerId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_manufacturerDialogue(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_manufacturerDialogue);

		try {
			this.manufacturer = (Manufacturer) arguments.get("Manufacturer");
			this.manufacturerListCtrl = (ManufacturerListCtrl) arguments.get("manufacturerListCtrl");

			if (this.manufacturer == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			Manufacturer manufacturerdata = new Manufacturer();
			BeanUtils.copyProperties(this.manufacturer, manufacturerdata);
			this.manufacturer.setBefImage(manufacturerdata);

			doLoadWorkFlow(this.manufacturer.isWorkflow(), this.manufacturer.getWorkflowId(),
					this.manufacturer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CDManufacturersDialogue");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.manufacturer);

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
		this.txtchannel.setMaxlength(20);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CDManufacturersDialogue_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CDManufacturersDialogue_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CDManufacturersDialogue_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CDManufacturersDialogue_btnSave"));
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
		doShowNotes(this.manufacturer);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		manufacturerListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.manufacturer.getBefImage());
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
	public void doWriteBeanToComponents(Manufacturer manufacturer) {
		logger.debug(Literal.ENTERING);

		this.name.setText(manufacturer.getName());
		this.description.setText(manufacturer.getDescription());
		this.active.setChecked(manufacturer.isActive());
		if (manufacturer.isNewRecord() || (manufacturer.getRecordType() != null ? manufacturer.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.txtchannel.setText(manufacturer.getChannel());
		this.recordStatus.setValue(manufacturer.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(Manufacturer manufacturer) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			manufacturer.setName(this.name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setDescription(this.description.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			manufacturer.setChannel(this.txtchannel.getValue());
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
	public void doShowDialog(Manufacturer manufacturer) {
		logger.debug(Literal.ENTERING);

		if (manufacturer.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.name.setFocus(true);
		} else {
			this.description.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(manufacturer.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(manufacturer);

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

		if (!this.name.isReadonly()) {
			this.name.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManufacturerList_ManufacturerName.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ManufacturerList_Description.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.txtchannel.getText().equals("") && !this.btnchannels.isDisabled()) {
			if (this.txtchannel.getText().length() > 20) {
				throw new WrongValueException(this.txtchannel,
						Labels.getLabel("label_ConsumerProductDialogue_channelLength.value"));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.name.setConstraint("");
		this.description.setConstraint("");
		this.txtchannel.setConstraint("");

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

		final Manufacturer manufacturer = new Manufacturer();
		BeanUtils.copyProperties(this.manufacturer, manufacturer);

		doDelete(manufacturer.getName(), manufacturer);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.manufacturer.isNewRecord()) {
			this.name.setDisabled(false);
		} else {
			this.name.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("CDManufacturersDialogue_Description"), this.description);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_Channel"), this.active);
		readOnlyComponent(isReadOnly("CDManufacturersDialogue_Active"), this.btnchannels);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.manufacturer.isNewRecord()) {
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

		readOnlyComponent(true, this.name);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.txtchannel);
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
		this.name.setValue("");
		this.description.setValue("");
		this.txtchannel.setValue("");
		;
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final Manufacturer manufacturer = new Manufacturer();
		BeanUtils.copyProperties(this.manufacturer, manufacturer);

		doSetValidation();
		doWriteComponentsToBean(manufacturer);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(manufacturer.getRecordType())) {
				manufacturer.setVersion(manufacturer.getVersion() + 1);
				if (manufacturer.isNewRecord()) {
					manufacturer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					manufacturer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					manufacturer.setNewRecord(true);
				}
			}
		} else {
			manufacturer.setVersion(manufacturer.getVersion() + 1);
			if (manufacturer.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(manufacturer, tranType)) {
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
	protected boolean doProcess(Manufacturer manufacturer, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		manufacturer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		manufacturer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manufacturer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			manufacturer.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(manufacturer.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, manufacturer);
				}
				if (isNotesMandatory(taskId, manufacturer)) {
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
			manufacturer.setTaskId(taskId);
			manufacturer.setNextTaskId(nextTaskId);
			manufacturer.setRoleCode(getRole());
			manufacturer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(manufacturer, tranType);
			String operationRefs = getServiceOperations(taskId, manufacturer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(manufacturer, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(manufacturer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public void setManufacturerListCtrl(ManufacturerListCtrl manufacturerListCtrl) {
		this.manufacturerListCtrl = manufacturerListCtrl;
	}

	public void setManufacturerService(ManufacturerService manufacturerService) {
		this.manufacturerService = manufacturerService;
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
		Manufacturer manufacturer = (Manufacturer) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = manufacturerService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = manufacturerService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = manufacturerService.doApprove(auditHeader);

					if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = manufacturerService.doReject(auditHeader);
					if (manufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_manufacturerDialogue, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_manufacturerDialogue, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.manufacturer), true);
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

	private AuditHeader getAuditHeader(Manufacturer manufacturer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, manufacturer.getBefImage(), manufacturer);
		return new AuditHeader(getReference(), null, null, null, auditDetail, manufacturer.getUserDetails(),
				getOverideMap());
	}

	public void onClick$btnchannels(Event event) {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_manufacturerDialogue, "ChannelTypes",
				String.valueOf(this.txtchannel.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.txtchannel.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

}
