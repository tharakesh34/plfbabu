package com.pennanttech.pff.commodity.webui;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.commodity.service.CommodityTypeService;
import com.pennanttech.pff.staticlist.AppStaticList;

public class CommodityTypeDialogueCtrl extends GFCBaseCtrl<CommodityType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CommodityTypeDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CommodityTypeDialogue;

	protected Uppercasebox type;
	protected Textbox description;
	protected Combobox unitType;
	protected Checkbox active;
	protected CommodityType commodityType;

	private transient CommodityTypeListCtrl commodityTypeListCtrl;
	private transient CommodityTypeService commodityTypeService;

	public CommodityTypeDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommodityTypeDialog";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.commodityType.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_CommodityTypeDialogue(Event event) throws AppException {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CommodityTypeDialogue);

		try {
			this.commodityType = (CommodityType) arguments.get("commodityType");
			this.commodityTypeListCtrl = (CommodityTypeListCtrl) arguments.get("commodityTypeListCtrl");

			if (this.commodityType == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			CommodityType commodityType = new CommodityType();
			BeanUtils.copyProperties(this.commodityType, commodityType);
			this.commodityType.setBefImage(commodityType);

			doLoadWorkFlow(this.commodityType.isWorkflow(), this.commodityType.getWorkflowId(),
					this.commodityType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CommodityTypeDialog");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.commodityType);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommodityTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommodityTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommodityTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommodityTypeDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.commodityType);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		commodityTypeListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.commodityType.getBefImage());
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
	public void doWriteBeanToComponents(CommodityType commodityType) {
		logger.debug(Literal.ENTERING);

		fillList(this.unitType, AppStaticList.getCommodityUnitTypes(), commodityType.getUnitType());
		this.type.setText(commodityType.getCode());
		this.description.setText(commodityType.getDescription());
		this.active.setChecked(commodityType.isActive());
		if (commodityType.isNew() || (commodityType.getRecordType() != null ? commodityType.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(commodityType.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(CommodityType acommodityType) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			acommodityType.setCode(this.type.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodityType.setDescription(this.description.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.unitType.getSelectedItem().getValue() != null
					&& !PennantConstants.List_Select.equals(this.unitType.getSelectedItem().getValue().toString())) {
				acommodityType.setUnitType(this.unitType.getSelectedItem().getValue());
			} else {
				acommodityType.setUnitType(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodityType.setActive(this.active.isChecked());
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
	 * @param covenantType
	 *            The entity that need to be render.
	 */
	public void doShowDialog(CommodityType commodityType) {
		logger.debug(Literal.ENTERING);

		if (commodityType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.type.setFocus(true);
		} else {
			this.description.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(commodityType.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(commodityType);

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

		if (!this.type.isReadonly()) {
			this.type.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommoditiesDialogue_CommodityType.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		if (!this.description.isReadonly()) {
			this.description.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommoditiesDialogue_CommodityDescription.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.type.isReadonly()) {
			if (this.type.isValid()) {
				if (StringUtils.isNotBlank(this.type.getValue())) {
					if (this.type.getValue().length() > 8) {
						throw new WrongValueException(this.type,
								Labels.getLabel("label_CommoditiesDialogue_CommodityTypeAlert.value"));
					}
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

		this.type.setConstraint("");
		this.description.setConstraint("");
		this.unitType.setConstraint("");

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

	/**
	 * Deletes a CovenantType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CommodityType acommodityType = new CommodityType();
		BeanUtils.copyProperties(this.commodityType, acommodityType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ acommodityType.getCode();
		if (MessageUtil.confirm(msg) != MessageUtil.YES) {
			return;
		}

		if (StringUtils.trimToEmpty(acommodityType.getRecordType()).equals("")) {
			acommodityType.setVersion(acommodityType.getVersion() + 1);
			acommodityType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			if (isWorkFlowEnabled()) {
				acommodityType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				acommodityType.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
				getWorkFlowDetails(userAction.getSelectedItem().getLabel(), acommodityType.getNextTaskId(),
						acommodityType);
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			if (doProcess(acommodityType, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.commodityType.isNewRecord()) {
			this.type.setDisabled(false);
		} else {
			this.type.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("CommodityType_Description"), this.description);
		readOnlyComponent(isReadOnly("CommodityType_UnitType"), this.active);
		readOnlyComponent(isReadOnly("CommodityType_Active"), this.unitType);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.commodityType.isNewRecord()) {
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

		readOnlyComponent(true, this.type);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.unitType);
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
		this.type.setValue("");
		this.description.setValue("");
		this.unitType.setSelectedIndex(0);
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final CommodityType commodityType = new CommodityType();
		BeanUtils.copyProperties(this.commodityType, commodityType);

		doSetValidation();
		doWriteComponentsToBean(commodityType);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(commodityType.getRecordType())) {
				commodityType.setVersion(commodityType.getVersion() + 1);
				if (commodityType.isNew()) {
					commodityType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					commodityType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					commodityType.setNewRecord(true);
				}
			}
		} else {
			commodityType.setVersion(commodityType.getVersion() + 1);
			if (commodityType.isNew()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(commodityType, tranType)) {
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CommodityType commodityType, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		commodityType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		commodityType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		commodityType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			commodityType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(commodityType.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, commodityType);
				}
				if (isNotesMandatory(taskId, commodityType)) {
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
			commodityType.setTaskId(taskId);
			commodityType.setNextTaskId(nextTaskId);
			commodityType.setRoleCode(getRole());
			commodityType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(commodityType, tranType);
			String operationRefs = getServiceOperations(taskId, commodityType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(commodityType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(commodityType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CommodityType aCommodityType = (CommodityType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = commodityTypeService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = commodityTypeService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = commodityTypeService.doApprove(auditHeader);

						if (aCommodityType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = commodityTypeService.doReject(auditHeader);
						if (aCommodityType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CommodityTypeDialogue, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_CommodityTypeDialogue, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.commodityType), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
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

	private AuditHeader getAuditHeader(CommodityType aStockCompany, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aStockCompany.getBefImage(), aStockCompany);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aStockCompany.getUserDetails(),
				getOverideMap());
	}

	public void setCommodityTypeService(CommodityTypeService commodityTypeService) {
		this.commodityTypeService = commodityTypeService;
	}

}
