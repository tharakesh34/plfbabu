/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  SecurityGroupDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.administration.securitygroup;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityGroup/securityGroupDialog.zul file.
 */
public class SecurityGroupDialogCtrl extends GFCBaseCtrl<SecurityGroup> {
	private static final long serialVersionUID = 1709997819133952587L;
	private static final Logger logger = Logger
			.getLogger(SecurityGroupDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SecurityGroupDialog; // autoWired
	protected Textbox grpCode; // autoWired
	protected Textbox grpDesc; // autoWired

	/* not auto wired variables */
	private SecurityGroup securityGroup; // over handed per parameter
	private transient SecurityGroupListCtrl securityGroupListCtrl; // over
																	// handed
																	// per
																	// parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient SecurityGroupService securityGroupService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SecurityGroupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityGroupDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityGroup object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityGroupDialog(Event event)
			throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SecurityGroupDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("securityGroup")) {
				this.securityGroup = (SecurityGroup) arguments
						.get("securityGroup");
				SecurityGroup befImage = new SecurityGroup();
				BeanUtils.copyProperties(this.securityGroup, befImage);
				this.securityGroup.setBefImage(befImage);
				setSecurityGroup(this.securityGroup);
			} else {
				setSecurityGroup(null);
			}

			doLoadWorkFlow(this.securityGroup.isWorkflow(),
					this.securityGroup.getWorkflowId(),
					this.securityGroup.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"SecurityGroupDialog");
			}

			// READ OVERHANDED parameters !
			// we get the securityGroupListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete securityGroup here.
			if (arguments.containsKey("securityGroupListCtrl")) {
				setSecurityGroupListCtrl((SecurityGroupListCtrl) arguments
						.get("securityGroupListCtrl"));
			} else {
				setSecurityGroupListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSecurityGroup());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SecurityGroupDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		// Empty sent any required attributes
		logger.debug("Entering ");
		this.grpCode.setMaxlength(50);
		this.grpDesc.setMaxlength(100);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_SecurityGroupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_SecurityGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_SecurityGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_SecurityGroupDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering");
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_SecurityGroupDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doDelete();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
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
		logger.debug("Entering ");
		doWriteBeanToComponents(this.securityGroup.getBefImage());
		doReadOnly();
		this.btnCtrl.setBtnStatus_Save();
		this.btnEdit.setVisible(true);
		this.btnDelete.setVisible(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSecurityGroup
	 *            SecurityGroup
	 */
	public void doWriteBeanToComponents(SecurityGroup aSecurityGroup) {
		logger.debug("Entering ");
		this.grpCode.setValue(aSecurityGroup.getGrpCode());
		this.grpDesc.setValue(aSecurityGroup.getGrpDesc());
		this.recordStatus.setValue(aSecurityGroup.getRecordStatus());
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityGroup
	 */
	public void doWriteComponentsToBean(SecurityGroup aSecurityGroup) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSecurityGroup.setGrpCode(this.grpCode.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSecurityGroup.setGrpDesc(this.grpDesc.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aSecurityGroup.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSecurityGroup
	 * @throws Exception
	 */
	public void doShowDialog(SecurityGroup aSecurityGroup) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aSecurityGroup.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.grpCode.focus();
		} else {
			this.grpCode.focus();
			if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aSecurityGroup);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SecurityGroupDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.grpCode.isReadonly()) {
			this.grpCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SecurityGroupDialog_GrpCode.value"),
					PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE,
					true));
		}

		if (!this.grpDesc.isReadonly()) {
			this.grpDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SecurityGroupDialog_GrpDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.grpCode.setConstraint("");
		this.grpDesc.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {

	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {

	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering ");
		this.grpCode.setErrorMessage("");
		this.grpDesc.setErrorMessage("");
		logger.debug("Leaving ");
	}

	// CRUD operations

	/**
	 * Deletes a SecurityGroup object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityGroup aSecurityGroup = new SecurityGroup();
		BeanUtils.copyProperties(getSecurityGroup(), aSecurityGroup);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> "
				+ Labels.getLabel("label_SecurityGroupDialog_GrpCode.value")
				+ " : " + aSecurityGroup.getGrpCode();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSecurityGroup.getRecordType())) {
				aSecurityGroup.setVersion(aSecurityGroup.getVersion() + 1);
				aSecurityGroup.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSecurityGroup.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aSecurityGroup, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		if (getSecurityGroup().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.grpCode.setReadonly(true);
			this.btnCancel.setVisible(true);

		}
		this.grpDesc.setReadonly(isReadOnly("SecurityGroupDialog_grpDesc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.securityGroup.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.grpCode.setReadonly(true);
		this.grpDesc.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");

		// remove validation, if there are a save before
		this.grpCode.setValue("");
		this.grpDesc.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityGroup aSecurityGroup = new SecurityGroup();
		BeanUtils.copyProperties(getSecurityGroup(), aSecurityGroup);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SecurityGroup object with the components data
		doWriteComponentsToBean(aSecurityGroup);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSecurityGroup.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSecurityGroup.getRecordType())) {
				aSecurityGroup.setVersion(aSecurityGroup.getVersion() + 1);
				if (isNew) {
					aSecurityGroup
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSecurityGroup
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityGroup.setNewRecord(true);
				}
			}
		} else {
			aSecurityGroup.setVersion(aSecurityGroup.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aSecurityGroup, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This Method used for setting all workFlow details from userWorkSpace and
	 * setting audit details to auditHeader
	 * 
	 * @param aSecurityGroup
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(SecurityGroup aSecurityGroup, String tranType) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSecurityGroup.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getUserId());
		aSecurityGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityGroup.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSecurityGroup.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityGroup
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSecurityGroup);
				}

				if (isNotesMandatory(taskId, aSecurityGroup)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aSecurityGroup.setTaskId(taskId);
			aSecurityGroup.setNextTaskId(nextTaskId);
			aSecurityGroup.setRoleCode(getRole());
			aSecurityGroup.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSecurityGroup, tranType);

			String operationRefs = getServiceOperations(taskId, aSecurityGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSecurityGroup,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSecurityGroup, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * This Method used for calling the all Database Operations from the service
	 * By passing the auditHeader and operationRefs(Method) as String
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		SecurityGroup aSecurityGroup = (SecurityGroup) auditHeader
				.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getSecurityGroupService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSecurityGroupService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getSecurityGroupService().doApprove(
								auditHeader);

						if (aSecurityGroup.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSecurityGroupService().doReject(
								auditHeader);

						if (aSecurityGroup.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_SecurityGroupDialog, auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_SecurityGroupDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.securityGroup), true);
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * This method creates and returns Audit header Object
	 * 
	 * @param aSecurityGroup
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityGroup aSecurityGroup,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aSecurityGroup.getBefImage(), aSecurityGroup);
		return new AuditHeader(String.valueOf(aSecurityGroup.getId()), null,
				null, null, auditDetail, aSecurityGroup.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.securityGroup);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSecurityGroupListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(String.valueOf(getSecurityGroup().getGrpID()));
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

	public SecurityGroup getSecurityGroup() {
		return this.securityGroup;
	}

	public void setSecurityGroup(SecurityGroup securityGroup) {
		this.securityGroup = securityGroup;
	}

	public void setSecurityGroupService(
			SecurityGroupService securityGroupService) {
		this.securityGroupService = securityGroupService;
	}

	public SecurityGroupService getSecurityGroupService() {
		return this.securityGroupService;
	}

	public void setSecurityGroupListCtrl(
			SecurityGroupListCtrl securityGroupListCtrl) {
		this.securityGroupListCtrl = securityGroupListCtrl;
	}

	public SecurityGroupListCtrl getSecurityGroupListCtrl() {
		return this.securityGroupListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
