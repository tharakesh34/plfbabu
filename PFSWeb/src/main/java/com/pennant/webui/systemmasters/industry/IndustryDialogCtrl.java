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
 * * FileName : IndustryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.industry;

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

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.service.systemmasters.IndustryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Industry/industryDialog.zul file.
 */
public class IndustryDialogCtrl extends GFCBaseCtrl<Industry> {
	private static final long serialVersionUID = -2259811281710327276L;
	private static final Logger logger = LogManager.getLogger(IndustryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by component with the same 'id' in the ZUL-file are getting auto wired by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_IndustryDialog;

	protected Textbox industryCode;
	protected ExtendedCombobox subSectorCode;
	protected Textbox industryDesc;
	protected Checkbox industryIsActive;

	// not autoWired Var's
	private Industry industry; // overHanded per parameter
	private transient IndustryListCtrl industryListCtrl; // overHanded per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient IndustryService industryService;

	/**
	 * default constructor.<br>
	 */
	public IndustryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "IndustryDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Industry object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_IndustryDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_IndustryDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("industry")) {
				this.industry = (Industry) arguments.get("industry");
				Industry befImage = new Industry();
				BeanUtils.copyProperties(this.industry, befImage);
				this.industry.setBefImage(befImage);
				setIndustry(this.industry);
			} else {
				setIndustry(null);
			}

			doLoadWorkFlow(this.industry.isWorkflow(), this.industry.getWorkflowId(), this.industry.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "IndustryDialog");
			}

			// READ OVERHANDED parameters !
			// we get the industryListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete industry here.
			if (arguments.containsKey("industryListCtrl")) {
				setIndustryListCtrl((IndustryListCtrl) arguments.get("industryListCtrl"));
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getIndustry());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_IndustryDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.industryCode.setMaxlength(8);
		this.subSectorCode.setMaxlength(8);
		this.industryDesc.setMaxlength(50);

		this.subSectorCode.setMandatoryStyle(true);
		this.subSectorCode.setModuleName("SubSector");
		this.subSectorCode.setValueColumn("SubSectorCode");
		this.subSectorCode.setDescColumn("SubSectorDesc");
		this.subSectorCode.setValidateColumns(new String[] { "SubSectorCode" });

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IndustryDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IndustryDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IndustryDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IndustryDialog_btnSave"));

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
		MessageUtil.showHelpWindow(event, window_IndustryDialog);
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
		doWriteBeanToComponents(this.industry.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aIndustry Industry
	 */
	public void doWriteBeanToComponents(Industry aIndustry) {
		logger.debug("Entering");
		this.industryCode.setValue(aIndustry.getIndustryCode());
		this.subSectorCode.setValue(aIndustry.getSubSectorCode());
		this.industryDesc.setValue(aIndustry.getIndustryDesc());
		this.industryIsActive.setChecked(aIndustry.isIndustryIsActive());

		if (aIndustry.isNewRecord()) {
			this.subSectorCode.setDescription("");
		} else {
			this.subSectorCode.setDescription(aIndustry.getLovDescSubSectorCodeName());
		}
		this.recordStatus.setValue(aIndustry.getRecordStatus());

		if (aIndustry.isNewRecord() || (aIndustry.getRecordType() != null ? aIndustry.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.industryIsActive.setChecked(true);
			this.industryIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aIndustry
	 */
	public void doWriteComponentsToBean(Industry aIndustry) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aIndustry.setIndustryCode(this.industryCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndustry.setLovDescSubSectorCodeName(this.subSectorCode.getDescription());
			aIndustry.setSubSectorCode(this.subSectorCode.getValidatedValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndustry.setIndustryDesc(this.industryDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndustry.setIndustryIsActive(this.industryIsActive.isChecked());
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

		aIndustry.setRecordStatus(this.recordStatus.getValue());
		setIndustry(aIndustry);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aIndustry
	 */
	public void doShowDialog(Industry aIndustry) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aIndustry.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.industryCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.industryDesc.focus();
				if (StringUtils.isNotBlank(aIndustry.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				this.subSectorCode.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aIndustry);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_IndustryDialog.onClose();
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

		if (!this.industryCode.isReadonly()) {
			this.industryCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IndustryDialog_IndustryCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.subSectorCode.isReadonly()) {
			this.subSectorCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_IndustryDialog_SubSectorCode.value"), null, false, true));
		}

		if (!this.industryDesc.isReadonly()) {
			this.industryDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IndustryDialog_IndustryDesc.value"),
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
		this.industryCode.setConstraint("");
		this.subSectorCode.setConstraint("");
		this.industryDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.industryCode.setErrorMessage("");
		this.subSectorCode.setErrorMessage("");
		this.industryDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Industry aIndustry = new Industry();
		BeanUtils.copyProperties(getIndustry(), aIndustry);

		doDelete(Labels.getLabel("label_IndustryDialog_IndustryCode.value") + " : " + aIndustry.getIndustryCode(),
				aIndustry);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getIndustry().isNewRecord()) {
			this.industryCode.setReadonly(false);
			this.subSectorCode.setReadonly(false);
			this.subSectorCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.industryCode.setReadonly(true);
			this.subSectorCode.setReadonly(true);
			this.subSectorCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.industryDesc.setReadonly(isReadOnly("IndustryDialog_industryDesc"));
		this.industryIsActive.setDisabled(isReadOnly("IndustryDialog_industryIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.industry.isNewRecord()) {
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

		this.industryCode.setReadonly(true);
		this.subSectorCode.setReadonly(true);
		this.industryDesc.setReadonly(true);
		this.industryIsActive.setDisabled(true);

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
		this.industryCode.setValue("");
		this.subSectorCode.setValue("");
		this.subSectorCode.setDescription("");
		this.industryDesc.setValue("");
		this.industryIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Industry aIndustry = new Industry();
		BeanUtils.copyProperties(getIndustry(), aIndustry);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Industry object with the components data
		doWriteComponentsToBean(aIndustry);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aIndustry.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aIndustry.getRecordType())) {
				aIndustry.setVersion(aIndustry.getVersion() + 1);
				if (isNew) {
					aIndustry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aIndustry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aIndustry.setNewRecord(true);
				}
			}
		} else {
			aIndustry.setVersion(aIndustry.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aIndustry, tranType)) {
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
	 * @param aIndustry (Industry)
	 * 
	 * @param tranType  (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(Industry aIndustry, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aIndustry.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aIndustry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aIndustry.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aIndustry.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aIndustry.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aIndustry);
				}

				if (isNotesMandatory(taskId, aIndustry)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
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

			aIndustry.setTaskId(taskId);
			aIndustry.setNextTaskId(nextTaskId);
			aIndustry.setRoleCode(getRole());
			aIndustry.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aIndustry, tranType);
			String operationRefs = getServiceOperations(taskId, aIndustry);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aIndustry, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aIndustry, tranType);
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
		Industry aIndustry = (Industry) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getIndustryService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getIndustryService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getIndustryService().doApprove(auditHeader);

					if (aIndustry.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getIndustryService().doReject(auditHeader);

					if (aIndustry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_IndustryDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_IndustryDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.industry), true);
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
	 * @param aIndustry (Industry)
	 * @param tranType  (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Industry aIndustry, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aIndustry.getBefImage(), aIndustry);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aIndustry.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.industry);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getIndustryListCtrl().search();
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getIndustry().getIndustryCode() + PennantConstants.KEY_SEPERATOR + getIndustry().getSubSectorCode();
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

	public Industry getIndustry() {
		return this.industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	public void setIndustryService(IndustryService industryService) {
		this.industryService = industryService;
	}

	public IndustryService getIndustryService() {
		return this.industryService;
	}

	public void setIndustryListCtrl(IndustryListCtrl industryListCtrl) {
		this.industryListCtrl = industryListCtrl;
	}

	public IndustryListCtrl getIndustryListCtrl() {
		return this.industryListCtrl;
	}

}
