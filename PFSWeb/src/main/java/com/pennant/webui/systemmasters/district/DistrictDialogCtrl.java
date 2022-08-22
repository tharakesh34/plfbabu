/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : DistrictDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.district;

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

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.District;
import com.pennant.backend.service.systemmasters.DistrictService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/District/DistrictDialog.zul file.
 */
public class DistrictDialogCtrl extends GFCBaseCtrl<District> {
	private static final long serialVersionUID = 223801324705386693L;
	private static final Logger logger = LogManager.getLogger(DistrictDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DistrictDialog; // autoWired

	protected Textbox districtCode; // autoWired
	protected Textbox districtName; // autoWired
	protected Textbox hostReferenceNo; // autoWired
	protected Checkbox districtIsActive; // autoWired

	// not autoWired variables
	private District district; // over handed per parameter
	private transient DistrictListCtrl districtListCtrl; // overHanded per
	// parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient DistrictService districtService;

	/**
	 * default constructor.<br>
	 */
	public DistrictDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DistrictDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Country object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_DistrictDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DistrictDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("district")) {
				this.district = (District) arguments.get("district");
				District befImage = new District();
				BeanUtils.copyProperties(this.district, befImage);
				this.district.setBefImage(befImage);

				setDistrict(this.district);
			} else {
				setDistrict(null);
			}
			doLoadWorkFlow(this.district.isWorkflow(), this.district.getWorkflowId(), this.district.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "DistrictDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}
			// READ OVERHANDED parameters !
			// we get the districtListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete district here.
			if (arguments.containsKey("districtListCtrl")) {
				setDistrictListCtrl((DistrictListCtrl) arguments.get("districtListCtrl"));
			} else {
				setDistrictListCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDistrict());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_DistrictDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.districtCode.setMaxlength(8);
		this.districtName.setMaxlength(50);
		this.hostReferenceNo.setMaxlength(20);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
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
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DistrictDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DistrictDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DistrictDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DistrictDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
		doEdit();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		MessageUtil.showHelpWindow(event, window_DistrictDialog);
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		doDelete();
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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
		doWriteBeanToComponents(this.district.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDistrict Country
	 */
	public void doWriteBeanToComponents(District aDistrict) {
		this.districtCode.setValue(aDistrict.getCode());
		this.districtName.setValue(aDistrict.getName());
		this.hostReferenceNo.setValue(aDistrict.getHostReferenceNo());
		this.districtIsActive.setChecked(aDistrict.isActive());
		this.recordStatus.setValue(aDistrict.getRecordStatus());

		if (aDistrict.isNew() || (aDistrict.getRecordType() != null ? aDistrict.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.districtIsActive.setChecked(true);
			this.districtIsActive.setDisabled(true);
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDistrict
	 */
	public void doWriteComponentsToBean(District aDistrict) {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aDistrict.setCode(this.districtCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDistrict.setName(this.districtName.getValue().trim());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDistrict.setHostReferenceNo(this.hostReferenceNo.getValue().trim());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDistrict.setActive(this.districtIsActive.isChecked());
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
		aDistrict.setRecordStatus(this.recordStatus.getValue());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aDistrict
	 */
	public void doShowDialog(District aDistrict) {
		logger.debug("Entering");
		if (aDistrict.isNew()) { // set Read only mode accordingly if the object is new or not.
			this.btnCtrl.setInitNew();
			doEdit();
			this.districtCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.districtName.focus();
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aDistrict);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_DistrictDialog.onClose();
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
		if (!this.districtCode.isReadonly()) {
			this.districtCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DistrictDialog_DistrictCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.districtName.isReadonly()) {
			this.districtName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DistrictDialog_DistrictName.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.districtCode.setConstraint("");
		this.districtName.setConstraint("");
		this.hostReferenceNo.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		this.districtCode.setErrorMessage("");
		this.districtName.setErrorMessage("");
		this.hostReferenceNo.setErrorMessage("");
	}

	// CRUD operations
	/**
	 * Deletes a Country object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final District aDistrict = new District();
		BeanUtils.copyProperties(getDistrict(), aDistrict);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_DistrictDialog_DistrictCode.value") + " : " + aDistrict.getCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDistrict.getRecordType())) {
				aDistrict.setVersion(aDistrict.getVersion() + 1);
				aDistrict.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDistrict.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aDistrict, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getDistrict().isNewRecord()) {
			this.districtCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.districtCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.districtName.setReadonly(isReadOnly("DistrictDialog_districtName"));
		this.hostReferenceNo.setReadonly(isReadOnly("DistrictDialog_hostReferenceNo"));
		this.districtIsActive.setDisabled(isReadOnly("DistrictDialog_districtIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.district.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		this.districtCode.setReadonly(true);
		this.districtName.setReadonly(true);
		this.hostReferenceNo.setReadonly(true);
		this.districtIsActive.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final District aDistrict = new District();
		BeanUtils.copyProperties(getDistrict(), aDistrict);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Country object with the components data
		doWriteComponentsToBean(aDistrict);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aDistrict.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDistrict.getRecordType())) {
				aDistrict.setVersion(aDistrict.getVersion() + 1);
				if (isNew) {
					aDistrict.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDistrict.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDistrict.setNewRecord(true);
				}
			}
		} else {
			aDistrict.setVersion(aDistrict.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aDistrict, tranType)) {
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
	 * @param aDistrict (Country)
	 * 
	 * @param tranType  (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(District aDistrict, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDistrict.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDistrict.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDistrict.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDistrict.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDistrict.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDistrict);
				}

				if (isNotesMandatory(taskId, aDistrict)) {
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

			aDistrict.setTaskId(taskId);
			aDistrict.setNextTaskId(nextTaskId);
			aDistrict.setRoleCode(getRole());
			aDistrict.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDistrict, tranType);

			String operationRefs = getServiceOperations(taskId, aDistrict);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDistrict, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDistrict, tranType);
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
		District aDistrict = (District) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getDistrictService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getDistrictService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getDistrictService().doApprove(auditHeader);

					if (aDistrict.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getDistrictService().doReject(auditHeader);

					if (aDistrict.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_DistrictDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_DistrictDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.district), true);
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

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aDistrict (Country)
	 * @param tranType  (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(District aDistrict, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDistrict.getBefImage(), aDistrict);
		return new AuditHeader(String.valueOf(aDistrict.getId()), null, null, null, auditDetail,
				aDistrict.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.district);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getDistrictListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.district.getCode());
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

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public DistrictListCtrl getDistrictListCtrl() {
		return districtListCtrl;
	}

	public void setDistrictListCtrl(DistrictListCtrl districtListCtrl) {
		this.districtListCtrl = districtListCtrl;
	}

	public DistrictService getDistrictService() {
		return districtService;
	}

	public void setDistrictService(DistrictService districtService) {
		this.districtService = districtService;
	}

}
