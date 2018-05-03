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
 * FileName    		:  RatingTypeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.bmtmasters.ratingtype;

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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.RatingTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/RatingType/ratingTypeDialog.zul file.
 */
public class RatingTypeDialogCtrl extends GFCBaseCtrl<RatingType> {
	private static final long serialVersionUID = 8645816104609479355L;
	private static final Logger logger = Logger.getLogger(RatingTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_RatingTypeDialog; 		// autoWired
	protected Textbox 		ratingType; 					// autoWired
	protected Textbox 		ratingTypeDesc; 				// autoWired
	protected Checkbox 		valueType; 						// autoWired
	protected Intbox 		valueLen; 						// autoWired
	protected Checkbox 		ratingIsActive; 				// autoWired

	// not auto wired variables
	private RatingType 						mRatingTytpe; 		// overHanded per parameter
	private transient RatingTypeListCtrl    ratingTypeListCtrl; // overHanded per
	// parameter

	private transient boolean 	validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient RatingTypeService ratingTypeService;
	private transient PagedListService  pagedListService;

	/**
	 * default constructor.<br>
	 */
	public RatingTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RatingTypeDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected RatingType object in a
	 * Map.
	 * 
	 * @parameter event
	 * @throws Exception
	 */
	public void onCreate$window_RatingTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RatingTypeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("ratingType")) {
				this.mRatingTytpe = (RatingType) arguments.get("ratingType");
				RatingType befImage = new RatingType();
				BeanUtils.copyProperties(this.mRatingTytpe, befImage);
				this.mRatingTytpe.setBefImage(befImage);
				setMRatingType(this.mRatingTytpe);
			} else {
				setMRatingType(null);
			}

			doLoadWorkFlow(this.mRatingTytpe.isWorkflow(),
					this.mRatingTytpe.getWorkflowId(),
					this.mRatingTytpe.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"RatingTypeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the ratingTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete ratingType here.
			if (arguments.containsKey("ratingTypeListCtrl")) {
				setRatingTypeListCtrl((RatingTypeListCtrl) arguments
						.get("ratingTypeListCtrl"));
			} else {
				setRatingTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMRatingType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RatingTypeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.ratingType.setMaxlength(8);
		this.ratingTypeDesc.setMaxlength(50);
		this.valueLen.setMaxlength(3);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RatingTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RatingTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RatingTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RatingTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_RatingTypeDialog);
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
		doWriteBeanToComponents(this.mRatingTytpe.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aRatingType
	 *            RatingType
	 */
	public void doWriteBeanToComponents(RatingType aRatingType) {
		logger.debug("Entering");
		this.ratingType.setValue(aRatingType.getRatingType());
		this.ratingTypeDesc.setValue(aRatingType.getRatingTypeDesc());
		this.valueType.setChecked(aRatingType.isValueType());
		this.valueLen.setValue(aRatingType.getValueLen());
		this.ratingIsActive.setChecked(aRatingType.isRatingIsActive());
		this.recordStatus.setValue(aRatingType.getRecordStatus());
		
		if(aRatingType.isNew() || aRatingType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.ratingIsActive.setChecked(true);
			this.ratingIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRatingType
	 */
	public void doWriteComponentsToBean(RatingType aRatingType) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aRatingType.setRatingType(this.ratingType.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingType.setRatingTypeDesc(this.ratingTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingType.setValueType(this.valueType.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingType.setValueLen(this.valueLen.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingType.setRatingIsActive(this.ratingIsActive.isChecked());
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

		aRatingType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aRatingType
	 * @throws Exception
	 */
	public void doShowDialog(RatingType aRatingType)throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aRatingType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ratingType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.ratingTypeDesc.focus();
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
			doWriteBeanToComponents(aRatingType);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RatingTypeDialog.onClose();
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

		if (!this.ratingType.isReadonly()){
			this.ratingType.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingTypeDialog_RatingType.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}			
		if (!this.ratingTypeDesc.isReadonly()){
			this.ratingTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingTypeDialog_RatingTypeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.valueLen.isReadonly()) {
			this.valueLen.setConstraint(new PTNumberValidator(Labels.getLabel("label_RatingTypeDialog_ValueLen.value"), true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.ratingType.setConstraint("");
		this.ratingTypeDesc.setConstraint("");
		this.valueLen.setConstraint("");
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
		this.ratingType.setErrorMessage("");
		this.ratingTypeDesc.setErrorMessage("");
		this.valueLen.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a RatingType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final RatingType aRatingType = new RatingType();
		BeanUtils.copyProperties(getMRatingType(), aRatingType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + aRatingType.getRatingType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aRatingType.getRecordType())) {
				aRatingType.setVersion(aRatingType.getVersion() + 1);
				aRatingType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aRatingType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aRatingType, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getMRatingType().isNewRecord()) {
			this.ratingType.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.ratingType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.ratingTypeDesc.setReadonly(isReadOnly("RatingTypeDialog_ratingTypeDesc"));
		this.valueType.setDisabled(isReadOnly("RatingTypeDialog_valueType"));
		this.valueLen.setReadonly(isReadOnly("RatingTypeDialog_valueLen"));
		this.ratingIsActive.setDisabled(isReadOnly("RatingTypeDialog_ratingIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.mRatingTytpe.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.ratingType.setReadonly(true);
		this.ratingTypeDesc.setReadonly(true);
		this.valueType.setDisabled(true);
		this.valueLen.setReadonly(true);
		this.ratingIsActive.setDisabled(true);

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
		this.ratingType.setValue("");
		this.ratingTypeDesc.setValue("");
		this.valueType.setChecked(false);
		this.valueLen.setText("");
		this.ratingIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final RatingType aRatingType = new RatingType();
		BeanUtils.copyProperties(getMRatingType(), aRatingType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the RatingType object with the components data
		doWriteComponentsToBean(aRatingType);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aRatingType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aRatingType.getRecordType())) {
				aRatingType.setVersion(aRatingType.getVersion() + 1);
				if (isNew) {
					aRatingType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aRatingType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aRatingType.setNewRecord(true);
				}
			}
		} else {
			aRatingType.setVersion(aRatingType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aRatingType, tranType)) {
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
	 * @param aRatingType
	 *            (RatingType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(RatingType aRatingType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aRatingType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aRatingType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRatingType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aRatingType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRatingType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aRatingType);
				}

				if (isNotesMandatory(taskId, aRatingType)) {
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

			aRatingType.setTaskId(taskId);
			aRatingType.setNextTaskId(nextTaskId);
			aRatingType.setRoleCode(getRole());
			aRatingType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRatingType, tranType);
			String operationRefs = getServiceOperations(taskId, aRatingType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aRatingType,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aRatingType, tranType);
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
		RatingType aRatingType = (RatingType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getRatingTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getRatingTypeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getRatingTypeService().doApprove(auditHeader);

						if (aRatingType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getRatingTypeService().doReject(auditHeader);

						if (aRatingType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_RatingTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RatingTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.mRatingTytpe), true);
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
	 * @param aRatingType
	 *            (RatingType)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(RatingType aRatingType, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aRatingType.getBefImage(), aRatingType);
		return new AuditHeader(String.valueOf(aRatingType.getId()), null, null,
				null, auditDetail, aRatingType.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RatingTypeDialog,auditHeader);
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
		doShowNotes(this.mRatingTytpe);
		
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getRatingTypeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.mRatingTytpe.getRatingType());
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

	public RatingType getMRatingType() {
		return this.mRatingTytpe;
	}
	public void setMRatingType(RatingType rating) {
		this.mRatingTytpe = rating;
	}

	public void setRatingTypeService(RatingTypeService ratingTypeService) {
		this.ratingTypeService = ratingTypeService;
	}
	public RatingTypeService getRatingTypeService() {
		return this.ratingTypeService;
	}

	public void setRatingTypeListCtrl(RatingTypeListCtrl ratingTypeListCtrl) {
		this.ratingTypeListCtrl = ratingTypeListCtrl;
	}
	public RatingTypeListCtrl getRatingTypeListCtrl() {
		return this.ratingTypeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
