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
 * FileName    		:  SubSectorDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.subsector;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.systemmasters.SubSectorService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/SubSector/subSectorDialog.zul file.
 */
public class SubSectorDialogCtrl extends GFCBaseCtrl<SubSector> {
	private static final long serialVersionUID = 6126940774535492694L;
	private static final Logger logger = Logger.getLogger(SubSectorDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_SubSectorDialog; 	
	protected ExtendedCombobox 	sectorCode; 				
	protected Textbox 	subSectorCode; 				
	protected Textbox 	subSectorDesc; 				
	protected Checkbox 	subSectorIsActive; 			

	// not auto wired variables
	private SubSector subSector; 							// overHanded per parameter
	private transient SubSectorListCtrl subSectorListCtrl;  // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient SubSectorService subSectorService;

	/**
	 * default constructor.<br>
	 */
	public SubSectorDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SubSectorDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSector object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SubSectorDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SubSectorDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("subSector")) {
				this.subSector = (SubSector) arguments.get("subSector");
				SubSector befImage = new SubSector();
				BeanUtils.copyProperties(this.subSector, befImage);
				this.subSector.setBefImage(befImage);

				setSubSector(this.subSector);
			} else {
				setSubSector(null);
			}

			doLoadWorkFlow(this.subSector.isWorkflow(),
					this.subSector.getWorkflowId(),
					this.subSector.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"SubSectorDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the subSectorListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete subSector here.
			if (arguments.containsKey("subSectorListCtrl")) {
				setSubSectorListCtrl((SubSectorListCtrl) arguments
						.get("subSectorListCtrl"));
			} else {
				setSubSectorListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSubSector());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SubSectorDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.sectorCode.setMaxlength(8);
		this.subSectorCode.setMaxlength(8);
		this.subSectorDesc.setMaxlength(50);

		this.sectorCode.setModuleName("Sector");
		this.sectorCode.setValueColumn("SectorCode");
		this.sectorCode.setDescColumn("SectorDesc");
		this.sectorCode.setValidateColumns(new String[]{"SectorCode"});
		
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SubSectorDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SubSectorDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SubSectorDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SubSectorDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_SubSectorDialog);
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
		doWriteBeanToComponents(this.subSector.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSubSector
	 *            (SubSector)
	 * 
	 */
	public void doWriteBeanToComponents(SubSector aSubSector) {
		logger.debug("Entering");
		this.sectorCode.setValue(aSubSector.getSectorCode());
		this.subSectorCode.setValue(aSubSector.getSubSectorCode());
		this.subSectorDesc.setValue(aSubSector.getSubSectorDesc());
		this.subSectorIsActive.setChecked(aSubSector.isSubSectorIsActive());
		this.sectorCode.setMandatoryStyle(true);

		if (aSubSector.isNewRecord()) {
			this.sectorCode.setDescription("");
		} else {
			this.sectorCode.setDescription(aSubSector.getLovDescSectorCodeName());
		}
		this.recordStatus.setValue(aSubSector.getRecordStatus());
		
		if(aSubSector.isNew() || (aSubSector.getRecordType() != null ? aSubSector.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.subSectorIsActive.setChecked(true);
			this.subSectorIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSubSector
	 *            (SubSector)
	 */
	public void doWriteComponentsToBean(SubSector aSubSector) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSubSector.setLovDescSectorCodeName(this.sectorCode.getDescription());
			aSubSector.setSectorCode(this.sectorCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSector.setSubSectorCode(this.subSectorCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSector.setSubSectorDesc(this.subSectorDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSector.setSubSectorIsActive(this.subSectorIsActive.isChecked());
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

		aSubSector.setRecordStatus(this.recordStatus.getValue());
		setSubSector(aSubSector);
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSubSector
	 * @throws Exception
	 */
	public void doShowDialog(SubSector aSubSector) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSubSector.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.sectorCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.subSectorDesc.focus();
				if (StringUtils.isNotBlank(aSubSector.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			this.subSectorCode.setReadonly(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSubSector);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SubSectorDialog.onClose();
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

		if (!this.sectorCode.isReadonly()) {
			this.sectorCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSectorDialog_SectorCode.value"), null, true,true));
		}
		
		if (!this.subSectorCode.isReadonly()){
			this.subSectorCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSectorDialog_SubSectorCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	

		if (!this.subSectorDesc.isReadonly()){
			this.subSectorDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSectorDialog_SubSectorDesc.value"), 
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
		this.subSectorCode.setConstraint("");
		this.subSectorDesc.setConstraint("");
		this.sectorCode.setConstraint("");
		logger.debug("Leaving");
	}

	

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.subSectorCode.setErrorMessage("");
		this.subSectorDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a SubSector object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SubSector aSubSector = new SubSector();
		BeanUtils.copyProperties(getSubSector(), aSubSector);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_SubSectorDialog_SectorCode.value")+" : "+aSubSector.getSectorCode() +","+
				Labels.getLabel("label_SubSectorDialog_SubSectorCode.value")+" : "+aSubSector.getSubSectorCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSubSector.getRecordType())) {
				aSubSector.setVersion(aSubSector.getVersion() + 1);
				aSubSector.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSubSector.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSubSector, tranType)) {
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
		if (getSubSector().isNewRecord()) {
			this.sectorCode.setReadonly(true);
			this.subSectorCode.setReadonly(false);
			this.sectorCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.sectorCode.setReadonly(true);
			this.subSectorCode.setReadonly(true);
			this.sectorCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		//this.subSectorCode.setReadonly(isReadOnly("SubSectorDialog_subSectorCode"));
		this.subSectorDesc.setReadonly(isReadOnly("SubSectorDialog_subSectorDesc"));
		this.subSectorIsActive.setDisabled(isReadOnly("SubSectorDialog_subSectorIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.subSector.isNewRecord()) {
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
		this.sectorCode.setReadonly(true);
		this.subSectorCode.setReadonly(true);
		this.subSectorDesc.setReadonly(true);
		this.subSectorIsActive.setDisabled(true);

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
		this.sectorCode.setValue("");
		this.sectorCode.setDescription("");
		this.subSectorCode.setValue("");
		this.subSectorDesc.setValue("");
		this.subSectorIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SubSector aSubSector = new SubSector();
		BeanUtils.copyProperties(getSubSector(), aSubSector);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SubSector object with the components data
		doWriteComponentsToBean(aSubSector);

		// Write the additional validations as per below example
		// get the selected branch object from the listBbox
		// Do data level validations here

		isNew = aSubSector.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSubSector.getRecordType())) {
				aSubSector.setVersion(aSubSector.getVersion() + 1);
				if (isNew) {
					aSubSector.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSubSector.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSubSector.setNewRecord(true);
				}
			}
		} else {
			aSubSector.setVersion(aSubSector.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSubSector, tranType)) {
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
	 * @param aSubSector
	 *            (SubSector)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(SubSector aSubSector, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSubSector.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aSubSector.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSubSector.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSubSector.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSubSector.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSubSector);
				}

				if (isNotesMandatory(taskId, aSubSector)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (!StringUtils.isBlank(nextTaskId)) {
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

			aSubSector.setTaskId(taskId);
			aSubSector.setNextTaskId(nextTaskId);
			aSubSector.setRoleCode(getRole());
			aSubSector.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSubSector, tranType);

			String operationRefs = getServiceOperations(taskId, aSubSector);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSubSector, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSubSector, tranType);
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
		SubSector aSubSector = (SubSector) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSubSectorService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSubSectorService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSubSectorService().doApprove(auditHeader);

						if (aSubSector.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSubSectorService().doReject(auditHeader);

						if (aSubSector.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SubSectorDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SubSectorDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.subSector), true);
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

	// WorkFlow Details
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aSubSegment
	 *            (SubSegment)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(SubSector aSubSector, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSubSector.getBefImage(), aSubSector);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aSubSector.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_SubSectorDialog, auditHeader);
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
		doShowNotes(this.subSector);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSubSectorListCtrl().search();
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getSubSector().getSubSectorCode()+PennantConstants.KEY_SEPERATOR +
					getSubSector().getSectorCode();
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

	public SubSector getSubSector() {
		return this.subSector;
	}
	public void setSubSector(SubSector subSector) {
		this.subSector = subSector;
	}

	public void setSubSectorService(SubSectorService subSectorService) {
		this.subSectorService = subSectorService;
	}
	public SubSectorService getSubSectorService() {
		return this.subSectorService;
	}

	public void setSubSectorListCtrl(SubSectorListCtrl subSectorListCtrl) {
		this.subSectorListCtrl = subSectorListCtrl;
	}
	public SubSectorListCtrl getSubSectorListCtrl() {
		return this.subSectorListCtrl;
	}

}
