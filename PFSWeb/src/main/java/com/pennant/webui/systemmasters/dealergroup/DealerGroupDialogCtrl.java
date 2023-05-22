package com.pennant.webui.systemmasters.dealergroup;

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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DealerGroup;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.systemmasters.DealerGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DealerGroupDialogCtrl extends GFCBaseCtrl<DealerGroup> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DealerGroupDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DealerGroupDialog;
	protected Button btndealerCode;
	protected Textbox dealerCode;
	protected ExtendedCombobox dealerCategory;
	protected Button btnchannels;
	protected Textbox txtchannel;
	protected Checkbox active;

	private DealerGroup dealerGroup;
	private transient DealerGroupListCtrl dealerGroupListCtrl;
	private transient DealerGroupService dealerGroupService;

	/**
	 * default constructor.<br>
	 */
	public DealerGroupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DealerGroupDialog";
	}

	public void onCreate$window_DealerGroupDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DealerGroupDialog);

		try {
			// Get the required arguments.
			this.dealerGroup = (DealerGroup) arguments.get("dealerGroup");
			this.dealerGroupListCtrl = (DealerGroupListCtrl) arguments.get("dealerGroupListCtrl");

			if (this.dealerGroup == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			DealerGroup dealerGroup = new DealerGroup();
			BeanUtils.copyProperties(this.dealerGroup, dealerGroup);
			this.dealerGroup.setBefImage(dealerGroup);

			// Render the page and display the data.
			doLoadWorkFlow(this.dealerGroup.isWorkflow(), this.dealerGroup.getWorkflowId(),
					this.dealerGroup.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "DealerGroupDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.dealerGroup);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param builderCompany The entity that need to be render.
	 */
	public void doShowDialog(DealerGroup dealerGroup) {
		logger.debug(Literal.LEAVING);

		if (dealerGroup.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.dealerCode.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(dealerGroup.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.dealerCode.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(dealerGroup);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DealerGroupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DealerGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DealerGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DealerGroupDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

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

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.txtchannel.setMaxlength(20);
		this.dealerCode.setMaxlength(8);
		this.dealerCategory.setModuleName("Category");
		this.dealerCategory.setMandatoryStyle(true);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$dealerCategory(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onfullfillDealerCategory();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onfullfillDealerCategory() {
		Object dataObject = dealerCategory.getObject();
		if (dataObject instanceof String) {
			this.dealerCategory.setObject(null);
			this.dealerCategory.setValue("", "");
		} else {
			if (dataObject instanceof LovFieldDetail) {
				LovFieldDetail lovFieldDetail = (LovFieldDetail) dataObject;
				this.dealerCategory.setObject(lovFieldDetail);
				this.dealerCategory.setValue(String.valueOf(lovFieldDetail.getFieldCodeId()),
						lovFieldDetail.getValueDesc());
			}
		}
	}

	public void onClick$btnchannels(Event event) {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_DealerGroupDialog, "ChannelTypes",
				String.valueOf(this.txtchannel.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.txtchannel.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void onClick$btndealerCode(Event event) {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_DealerGroupDialog, "DealerMapping",
				String.valueOf(this.dealerCode.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.dealerCode.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

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
	 * Writes the bean data to the components.<br>
	 * 
	 * @param builderGroup
	 * 
	 */
	public void doWriteBeanToComponents(DealerGroup aDealerGroup) {
		logger.debug(Literal.ENTERING);

		this.dealerCode.setValue(aDealerGroup.getDealerCode());
		this.txtchannel.setValue(aDealerGroup.getChannel());
		this.active.setChecked(aDealerGroup.isActive());
		if (dealerGroup.isNewRecord() || (dealerGroup.getRecordType() != null ? dealerGroup.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		if (aDealerGroup.getDealerCategoryId() != 0) {
			LovFieldDetail lovFieldDetail = new LovFieldDetail();
			lovFieldDetail.setFieldCodeId(Long.valueOf(aDealerGroup.getDealerCategoryId()));
			this.dealerCategory.setObject(lovFieldDetail);
			this.dealerCategory.setValue(String.valueOf(lovFieldDetail.getFieldCodeId()),
					lovFieldDetail.getValueDesc());
		}
		this.recordStatus.setValue(aDealerGroup.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBuilderGroup
	 */
	public void doWriteComponentsToBean(DealerGroup aDealerGroup) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			if (!this.dealerCode.getValue().equals("")) {
				aDealerGroup.setDealerCode(this.dealerCode.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Chaneel
		try {
			aDealerGroup.setChannel(this.txtchannel.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// dealer category
		try {
			this.dealerCategory.getValidatedValue();
			aDealerGroup.setDealerCategoryId(Integer.parseInt(this.dealerCategory.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// cative
		try {
			aDealerGroup.setActive(this.active.isChecked());
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
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.btndealerCode.isDisabled()) {
			this.dealerCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DealerGroupDialog_dealerCode.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.dealerCategory.isReadonly()) {
			this.dealerCategory.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DealerGroupDialog_dealerCategory.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.dealerCode.setConstraint("");

		this.dealerCategory.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// id
		// Name
		// Segmentation

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
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final DealerGroup aDealerGroup = new DealerGroup();
		BeanUtils.copyProperties(this.dealerGroup, aDealerGroup);

		doDelete(aDealerGroup.getDealerCode(), aDealerGroup);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		dealerGroupListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.dealerGroup.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.btndealerCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.btndealerCode);
		}

		readOnlyComponent(isReadOnly("DealerGroupDialog_DealerCategoryId"), this.dealerCategory);
		readOnlyComponent(isReadOnly("DealerGroupDialog_Channel"), this.btnchannels);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.dealerGroup.isNewRecord()) {
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

		readOnlyComponent(true, this.dealerCode);
		readOnlyComponent(true, this.txtchannel);

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
		this.dealerCode.setValue("");
		this.dealerCategory.setValue("");
		this.txtchannel.setValue("");

		this.active.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final DealerGroup aDealerGroup = new DealerGroup();
		BeanUtils.copyProperties(this.dealerGroup, aDealerGroup);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aDealerGroup);

		isNew = aDealerGroup.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDealerGroup.getRecordType())) {
				aDealerGroup.setVersion(aDealerGroup.getVersion() + 1);
				if (isNew) {
					aDealerGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDealerGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDealerGroup.setNewRecord(true);
				}
			}
		} else {
			aDealerGroup.setVersion(aDealerGroup.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aDealerGroup, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
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
	protected boolean doProcess(DealerGroup aDealerGroup, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDealerGroup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDealerGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDealerGroup.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDealerGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDealerGroup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDealerGroup);
				}

				if (isNotesMandatory(taskId, aDealerGroup)) {
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

			aDealerGroup.setTaskId(taskId);
			aDealerGroup.setNextTaskId(nextTaskId);
			aDealerGroup.setRoleCode(getRole());
			aDealerGroup.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDealerGroup, tranType);
			String operationRefs = getServiceOperations(taskId, aDealerGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDealerGroup, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDealerGroup, tranType);
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
		DealerGroup aDealerGroup = (DealerGroup) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = dealerGroupService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = dealerGroupService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = dealerGroupService.doApprove(auditHeader);

					if (aDealerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = dealerGroupService.doReject(auditHeader);
					if (aDealerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_DealerGroupDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_DealerGroupDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.dealerGroup), true);
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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.dealerGroup);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(DealerGroup aDealerGroup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDealerGroup.getBefImage(), aDealerGroup);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aDealerGroup.getUserDetails(),
				getOverideMap());
	}

	public void setBuilderGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public DealerGroupService getDealerGroupService() {
		return dealerGroupService;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.dealerGroup.getDealerGroupId()));
		return referenceBuffer.toString();
	}

}
