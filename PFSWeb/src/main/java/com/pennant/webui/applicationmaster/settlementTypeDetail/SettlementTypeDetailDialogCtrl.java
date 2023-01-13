package com.pennant.webui.applicationmaster.settlementTypeDetail;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.SettlementTypeDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.SettlementTypeDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SettlementDetails/SettlementTypeDetailDialog.zul file.
 */
public class SettlementTypeDetailDialogCtrl extends GFCBaseCtrl<SettlementTypeDetail> {
	private static final long serialVersionUID = -4484270347916527133L;
	private static final Logger logger = LogManager.getLogger(SettlementTypeDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SettlementTypeDetailDialog;

	protected Textbox settlementCode;
	protected Textbox settlementDesc;
	protected Checkbox alwGracePeriod;
	protected Checkbox active;

	// not autoWired variables
	private SettlementTypeDetail settlementTypeDetail; // over handed per parameter
	private transient SettlementTypeDetailListCtrl settlementTypeDetailListCtrl; // over handed per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient SettlementTypeDetailService settlementTypeDetailService;

	/**
	 * default constructor.<br>
	 */
	public SettlementTypeDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SettlementTypeDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected SettlementTypeDetail object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SettlementTypeDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SettlementTypeDetailDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("settlementTypeDetail")) {
				this.settlementTypeDetail = (SettlementTypeDetail) arguments.get("settlementTypeDetail");
				SettlementTypeDetail befImage = new SettlementTypeDetail();
				BeanUtils.copyProperties(this.settlementTypeDetail, befImage);
				this.settlementTypeDetail.setBefImage(befImage);
				setSettlementTypeDetail(this.settlementTypeDetail);
			} else {
				setSettlementTypeDetail(null);
			}

			doLoadWorkFlow(this.settlementTypeDetail.isWorkflow(), this.settlementTypeDetail.getWorkflowId(),
					this.settlementTypeDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the SettlementTypeDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete settlementTypeDetail here.
			if (arguments.containsKey("settlementTypeDetailListCtrl")) {
				setSettlementTypeDetailListCtrl(
						(SettlementTypeDetailListCtrl) arguments.get("settlementTypeDetailListCtrl"));
			} else {
				setSettlementTypeDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(settlementTypeDetail);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SettlementTypeDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.settlementCode.setMaxlength(8);
		this.settlementDesc.setMaxlength(200);

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SettlementTypeDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SettlementTypeDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SettlementTypeDetailDialog_btnDelete"));
		this.btnSave.setVisible(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
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
		MessageUtil.showHelpWindow(event, window_SettlementTypeDetailDialog);
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
		logger.debug("Entering");
		doWriteBeanToComponents(this.settlementTypeDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSettlementTypeDetail SettlementTypeDetail
	 */
	public void doWriteBeanToComponents(SettlementTypeDetail settlementTypeDetail) {
		logger.debug("Entering");
		this.settlementCode.setValue(settlementTypeDetail.getSettlementCode());
		this.settlementDesc.setValue(settlementTypeDetail.getSettlementDesc());
		this.alwGracePeriod.setChecked(settlementTypeDetail.isAlwGracePeriod());
		this.active.setChecked(settlementTypeDetail.isActive());
		this.recordStatus.setValue(settlementTypeDetail.getRecordStatus());

		if (settlementTypeDetail.isNewRecord()
				|| (settlementTypeDetail.getRecordType() != null ? settlementTypeDetail.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSettlementTypeDetail
	 */
	public void doWriteComponentsToBean(SettlementTypeDetail settlementTypeDetail) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			settlementTypeDetail.setSettlementCode((this.settlementCode.getValue().toUpperCase()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			settlementTypeDetail.setSettlementDesc(this.settlementDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			settlementTypeDetail.setAlwGracePeriod(this.alwGracePeriod.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			settlementTypeDetail.setActive(this.active.isChecked());
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

		settlementTypeDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aSettlementTypeDetail
	 * @throws Exception
	 */
	public void doShowDialog(SettlementTypeDetail settlementTypeDetail) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (settlementTypeDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.settlementCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.settlementDesc.focus();
				if (StringUtils.isNotBlank(settlementTypeDetail.getRecordType())) {
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
			doWriteBeanToComponents(settlementTypeDetail);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SettlementTypeDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.settlementCode.isReadonly()) {
			this.settlementCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SettlementCode"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.settlementDesc.isReadonly()) {
			this.settlementDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SettlementDesc"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.settlementCode.setConstraint("");
		this.settlementDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.settlementCode.setErrorMessage("");
		this.settlementDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final SettlementTypeDetail settlementTypeDetail = new SettlementTypeDetail();
		BeanUtils.copyProperties(getSettlementTypeDetail(), settlementTypeDetail);

		String keyReference = Labels.getLabel("label_SettlementCode") + " : "
				+ settlementTypeDetail.getSettlementCode();

		doDelete(keyReference, settlementTypeDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getSettlementTypeDetail().isNewRecord()) {
			this.settlementCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.settlementCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.settlementDesc.setReadonly(false);
		this.alwGracePeriod.setDisabled(isReadOnly("SettlementTypeDetailDialog_AlwGracePeriod"));
		this.active.setDisabled(isReadOnly("SettlementTypeDetailDialog_Active"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.settlementTypeDetail.isNewRecord()) {
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
		logger.debug("Entering");

		this.settlementCode.setReadonly(true);
		this.settlementDesc.setReadonly(true);
		this.alwGracePeriod.setDisabled(true);
		this.active.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.settlementCode.setValue("");
		this.settlementDesc.setValue("");
		this.alwGracePeriod.setChecked(false);
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final SettlementTypeDetail aSettlementTypeDetail = new SettlementTypeDetail();
		BeanUtils.copyProperties(getSettlementTypeDetail(), aSettlementTypeDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SettlementTypeDetail object with the components data
		doWriteComponentsToBean(aSettlementTypeDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aSettlementTypeDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSettlementTypeDetail.getRecordType())) {
				aSettlementTypeDetail.setVersion(aSettlementTypeDetail.getVersion() + 1);
				if (isNew) {
					aSettlementTypeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSettlementTypeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSettlementTypeDetail.setNewRecord(true);
				}
			}
		} else {
			aSettlementTypeDetail.setVersion(aSettlementTypeDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSettlementTypeDetail, tranType)) {
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
	 * @param aSettlementTypeDetail (SettlementTypeDetail)
	 * 
	 * @param tranType              (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(SettlementTypeDetail aSettlementTypeDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSettlementTypeDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSettlementTypeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSettlementTypeDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSettlementTypeDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSettlementTypeDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSettlementTypeDetail);
				}

				if (isNotesMandatory(taskId, aSettlementTypeDetail)) {
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

			aSettlementTypeDetail.setTaskId(taskId);
			aSettlementTypeDetail.setNextTaskId(nextTaskId);
			aSettlementTypeDetail.setRoleCode(getRole());
			aSettlementTypeDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSettlementTypeDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aSettlementTypeDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSettlementTypeDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aSettlementTypeDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
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
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		SettlementTypeDetail aSettlementTypeDetail = (SettlementTypeDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = settlementTypeDetailService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = settlementTypeDetailService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = settlementTypeDetailService.doApprove(auditHeader);

						if (aSettlementTypeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = settlementTypeDetailService.doReject(auditHeader);

						if (aSettlementTypeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SettlementTypeDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SettlementTypeDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.settlementTypeDetail), true);
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
		} catch (AppException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aSettlementTypeDetail (SettlementTypeDetail)
	 * @param tranType              (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(SettlementTypeDetail aSettlementTypeDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSettlementTypeDetail.getBefImage(),
				aSettlementTypeDetail);
		return new AuditHeader(String.valueOf(aSettlementTypeDetail.getId()), null, null, null, auditDetail,
				aSettlementTypeDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_SettlementTypeDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.settlementTypeDetail);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getSettlementTypeDetailListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.settlementTypeDetail.getSettlementCode());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SettlementTypeDetailListCtrl getSettlementTypeDetailListCtrl() {
		return settlementTypeDetailListCtrl;
	}

	public SettlementTypeDetail getSettlementTypeDetail() {
		return settlementTypeDetail;
	}

	public void setSettlementTypeDetail(SettlementTypeDetail settlementTypeDetail) {
		this.settlementTypeDetail = settlementTypeDetail;
	}

	public void setSettlementTypeDetailListCtrl(SettlementTypeDetailListCtrl settlementTypeDetailListCtrl) {
		this.settlementTypeDetailListCtrl = settlementTypeDetailListCtrl;
	}

	public SettlementTypeDetailService getSettlementTypeDetailService() {
		return settlementTypeDetailService;
	}

	public void setSettlementTypeDetailService(SettlementTypeDetailService settlementTypeDetailService) {
		this.settlementTypeDetailService = settlementTypeDetailService;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

}
