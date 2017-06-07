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
 * FileName    		:  DepartmentDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.department;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.service.systemmasters.DepartmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Department/departmentDialog.zul file.
 */
public class DepartmentDialogCtrl extends GFCBaseCtrl<Department> {
	private static final long serialVersionUID = 6735326538887365366L;
	private static final Logger logger = Logger.getLogger(DepartmentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_DepartmentDialog;	// autoWired

	protected Textbox 	deptCode; 					// autoWired
	protected Textbox 	deptDesc; 					// autoWired
	protected Checkbox 	deptIsActive; 				// autoWired

	// not autoWired variables
	private Department department; // over handed per parameter
	private transient DepartmentListCtrl departmentListCtrl; // over handed per
	// parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient DepartmentService departmentService;

	/**
	 * default constructor.<br>
	 */
	public DepartmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DepartmentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Department object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DepartmentDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DepartmentDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("department")) {
				this.department = (Department) arguments.get("department");
				Department befImage = new Department();
				BeanUtils.copyProperties(this.department, befImage);
				this.department.setBefImage(befImage);

				setDepartment(this.department);
			} else {
				setDepartment(null);
			}

			doLoadWorkFlow(this.department.isWorkflow(),
					this.department.getWorkflowId(),
					this.department.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"DepartmentDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the departmentListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete department here.
			if (arguments.containsKey("departmentListCtrl")) {
				setDepartmentListCtrl((DepartmentListCtrl) arguments
						.get("departmentListCtrl"));
			} else {
				setDepartmentListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDepartment());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_DepartmentDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.deptCode.setMaxlength(8);
		this.deptDesc.setMaxlength(50);

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
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DepartmentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DepartmentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DepartmentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DepartmentDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_DepartmentDialog);
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
		logger.debug("Entering");
		doWriteBeanToComponents(this.department.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDepartment
	 *            Department
	 */
	public void doWriteBeanToComponents(Department aDepartment) {
		logger.debug("Entering");
		this.deptCode.setValue(aDepartment.getDeptCode());
		this.deptDesc.setValue(aDepartment.getDeptDesc());
		this.deptIsActive.setChecked(aDepartment.isDeptIsActive());
		this.recordStatus.setValue(aDepartment.getRecordStatus());
		
		if(aDepartment.isNew() || (aDepartment.getRecordType() != null ? aDepartment.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.deptIsActive.setChecked(true);
			this.deptIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDepartment
	 */
	public void doWriteComponentsToBean(Department aDepartment) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aDepartment.setDeptCode(this.deptCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDepartment.setDeptDesc(this.deptDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDepartment.setDeptIsActive(this.deptIsActive.isChecked());
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

		aDepartment.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDepartment
	 * @throws Exception
	 */
	public void doShowDialog(Department aDepartment) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aDepartment.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.deptCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.deptDesc.focus();
				if (StringUtils.isNotBlank(aDepartment.getRecordType())) {
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
			doWriteBeanToComponents(aDepartment);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_DepartmentDialog.onClose();
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

		if (!this.deptCode.isReadonly()){
			this.deptCode.setConstraint(new PTStringValidator(Labels.getLabel("label_DepartmentDialog_DeptCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.deptDesc.isReadonly()){
			this.deptDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_DepartmentDialog_DeptDesc.value"), 
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
		this.deptCode.setConstraint("");
		this.deptDesc.setConstraint("");
		logger.debug("Leaving");
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
		logger.debug("Entering");
		this.deptCode.setErrorMessage("");
		this.deptDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a Department object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Department aDepartment = new Department();
		BeanUtils.copyProperties(getDepartment(), aDepartment);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_DepartmentDialog_DeptCode.value")+" : "+aDepartment.getDeptCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(aDepartment.getRecordType())) {
				aDepartment.setVersion(aDepartment.getVersion() + 1);
				aDepartment.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDepartment.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aDepartment, tranType)) {
					refreshList();
					closeDialog();
				}

			}catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getDepartment().isNewRecord()) {
			this.deptCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.deptCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.deptDesc.setReadonly(isReadOnly("DepartmentDialog_deptDesc"));
		this.deptIsActive.setDisabled(isReadOnly("DepartmentDialog_deptIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.department.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.deptCode.setReadonly(true);
		this.deptDesc.setReadonly(true);
		this.deptIsActive.setDisabled(true);

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
		this.deptCode.setValue("");
		this.deptDesc.setValue("");
		this.deptIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Department aDepartment = new Department();
		BeanUtils.copyProperties(getDepartment(), aDepartment);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Department object with the components data
		doWriteComponentsToBean(aDepartment);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aDepartment.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDepartment.getRecordType())) {
				aDepartment.setVersion(aDepartment.getVersion() + 1);
				if (isNew) {
					aDepartment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDepartment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDepartment.setNewRecord(true);
				}
			}
		} else {
			aDepartment.setVersion(aDepartment.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aDepartment, tranType)) {
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
	 * @param aDepartment
	 *            (Department)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Department aDepartment, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDepartment.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aDepartment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDepartment.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDepartment.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDepartment.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDepartment);
				}

				if (isNotesMandatory(taskId, aDepartment)) {
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

			aDepartment.setTaskId(taskId);
			aDepartment.setNextTaskId(nextTaskId);
			aDepartment.setRoleCode(getRole());
			aDepartment.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDepartment, tranType);

			String operationRefs = getServiceOperations(taskId, aDepartment);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDepartment, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDepartment, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Department aDepartment = (Department) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getDepartmentService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getDepartmentService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getDepartmentService().doApprove(auditHeader);

						if (aDepartment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getDepartmentService().doReject(auditHeader);

						if (aDepartment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DepartmentDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_DepartmentDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.department), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Department aDepartment, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDepartment.getBefImage(), aDepartment);
		return new AuditHeader(String.valueOf(aDepartment.getId()), null, null,
				null, auditDetail, aDepartment.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_DepartmentDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.department);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getDepartmentListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.department.getDeptCode());
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

	public Department getDepartment() {
		return this.department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	public DepartmentService getDepartmentService() {
		return this.departmentService;
	}

	public void setDepartmentListCtrl(DepartmentListCtrl departmentListCtrl) {
		this.departmentListCtrl = departmentListCtrl;
	}
	public DepartmentListCtrl getDepartmentListCtrl() {
		return this.departmentListCtrl;
	}

}
