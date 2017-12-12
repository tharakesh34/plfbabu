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
 * FileName    		:  InterestRateTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.interestratetype;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.InterestRateTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/InterestRateType/interestRateTypeDialog.zul
 * file.
 */
public class InterestRateTypeDialogCtrl extends GFCBaseCtrl<InterestRateType> {
	private static final long serialVersionUID = 7844319112268758225L;
	private static final Logger logger = Logger.getLogger(InterestRateTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_InterestRateTypeDialog; 	// autoWired

	protected Combobox 		intRateTypeCode; 				// autoWired
	protected Textbox 		intRateTypeDesc; 				// autoWired
	protected Checkbox 		intRateTypeIsActive; 			// autoWired

	// not autoWired variables
	private InterestRateType interestRateType; // over handed per parameter
	private transient InterestRateTypeListCtrl interestRateTypeListCtrl; // over handed per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient InterestRateTypeService interestRateTypeService;

	/**
	 * default constructor.<br>
	 */
	public InterestRateTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InterestRateTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected InterestRateType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InterestRateTypeDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_InterestRateTypeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("interestRateType")) {
				this.interestRateType = (InterestRateType) arguments
						.get("interestRateType");
				InterestRateType befImage = new InterestRateType();
				BeanUtils.copyProperties(this.interestRateType, befImage);
				this.interestRateType.setBefImage(befImage);

				setInterestRateType(this.interestRateType);
			} else {
				setInterestRateType(null);
			}

			doLoadWorkFlow(this.interestRateType.isWorkflow(),
					this.interestRateType.getWorkflowId(),
					this.interestRateType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"InterestRateTypeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the interestRateTypeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete interestRateType here.
			if (arguments.containsKey("interestRateTypeListCtrl")) {
				setInterestRateTypeListCtrl((InterestRateTypeListCtrl) arguments
						.get("interestRateTypeListCtrl"));
			} else {
				setInterestRateTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getInterestRateType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_InterestRateTypeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.intRateTypeDesc.setMaxlength(50);

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
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InterestRateTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InterestRateTypeDialog_btnEdit"));
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_InterestRateTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InterestRateTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_InterestRateTypeDialog);
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
		doWriteBeanToComponents(this.interestRateType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		//Delete Operation not required for this module
		this.btnDelete.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aInterestRateType
	 *            InterestRateType
	 */
	public void doWriteBeanToComponents(InterestRateType aInterestRateType) {
		logger.debug("Entering");
		
		fillComboBox(this.intRateTypeCode,aInterestRateType.getIntRateTypeCode(),PennantStaticListUtil.getInterestRateType(false),"");
		this.intRateTypeDesc.setValue(aInterestRateType.getIntRateTypeDesc());
		this.intRateTypeIsActive.setChecked(aInterestRateType.isIntRateTypeIsActive());
		this.recordStatus.setValue(aInterestRateType.getRecordStatus());
		
		if(aInterestRateType.isNew() || (aInterestRateType.getRecordType() != null ? aInterestRateType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.intRateTypeIsActive.setChecked(true);
			this.intRateTypeIsActive.setDisabled(true);
		}
		
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aInterestRateType
	 */
	public void doWriteComponentsToBean(InterestRateType aInterestRateType) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		/*try {
			aInterestRateType.setIntRateTypeCode(this.intRateTypeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}*/
		try {
			if(!this.intRateTypeCode.isDisabled() && this.intRateTypeCode.getSelectedIndex()<0){
				throw new WrongValueException(intRateTypeCode, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_InterestRateTypeDialog_IntRateTypeCode.value")}));
			}
			aInterestRateType.setIntRateTypeCode(this.intRateTypeCode.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aInterestRateType.setIntRateTypeDesc(this.intRateTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aInterestRateType.setIntRateTypeIsActive(this.intRateTypeIsActive.isChecked());
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

		aInterestRateType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aInterestRateType
	 * @throws Exception
	 */
	public void doShowDialog(InterestRateType aInterestRateType)throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aInterestRateType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.intRateTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.intRateTypeDesc.focus();
				if (StringUtils.isNotBlank(aInterestRateType.getRecordType())) {
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
			doWriteBeanToComponents(aInterestRateType);

			this.btnDelete.setVisible(false);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_InterestRateTypeDialog.onClose();
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

		if (!this.intRateTypeCode.isDisabled()){
			this.intRateTypeCode.setConstraint(new StaticListValidator(PennantStaticListUtil.getInterestRateType(true),
					Labels.getLabel("label_InterestRateTypeDialog_IntRateTypeCode.value")));
		}
		
		/*if (!this.intRateTypeCode.isReadonly()){
			this.intRateTypeCode.setConstraint(new SimpleConstraint(
					PennantConstants.ALPHANUM_CAPS_REGEX, Labels.getLabel(
							"FIELD_ALNUM_CAPS",new String[]{Labels.getLabel(
							"label_InterestRateTypeDialog_IntRateTypeCode.value")})));
		}*/
		if (!this.intRateTypeDesc.isReadonly()){
			this.intRateTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_InterestRateTypeDialog_IntRateTypeDesc.value"), 
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
		this.intRateTypeCode.setConstraint("");
		this.intRateTypeDesc.setConstraint("");
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
		this.intRateTypeCode.setErrorMessage("");
		this.intRateTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a InterestRateType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final InterestRateType aInterestRateType = new InterestRateType();
		BeanUtils.copyProperties(getInterestRateType(), aInterestRateType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_InterestRateTypeDialog_IntRateTypeCode.value")+" : "+
				PennantStaticListUtil.getlabelDesc(StringUtils.trimToEmpty(aInterestRateType.getIntRateTypeCode()), PennantStaticListUtil.getInterestRateType(true));
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aInterestRateType.getRecordType())) {
				aInterestRateType.setVersion(aInterestRateType.getVersion() + 1);
				aInterestRateType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aInterestRateType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aInterestRateType, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
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

		if (getInterestRateType().isNewRecord()) {
			this.intRateTypeCode.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.intRateTypeCode.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		this.intRateTypeDesc.setReadonly(isReadOnly("InterestRateTypeDialog_intRateTypeDesc"));
		this.intRateTypeIsActive.setDisabled(isReadOnly("InterestRateTypeDialog_intRateTypeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.interestRateType.isNewRecord()) {
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

		this.intRateTypeCode.setDisabled(true);
		this.intRateTypeDesc.setReadonly(true);
		this.intRateTypeIsActive.setDisabled(true);

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
		this.intRateTypeCode.setValue("");
		this.intRateTypeDesc.setValue("");
		this.intRateTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final InterestRateType aInterestRateType = new InterestRateType();
		BeanUtils.copyProperties(getInterestRateType(), aInterestRateType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the InterestRateType object with the components data
		doWriteComponentsToBean(aInterestRateType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aInterestRateType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aInterestRateType.getRecordType())) {
				aInterestRateType.setVersion(aInterestRateType.getVersion() + 1);
				if (isNew) {
					aInterestRateType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aInterestRateType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aInterestRateType.setNewRecord(true);
				}
			}
		} else {
			aInterestRateType.setVersion(aInterestRateType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aInterestRateType, tranType)) {
				refreshList();
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
	 * @param aInterestRateType
	 *            (InterestRateType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(InterestRateType aInterestRateType,String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aInterestRateType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aInterestRateType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aInterestRateType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aInterestRateType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aInterestRateType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aInterestRateType);
				}

				if (isNotesMandatory(taskId, aInterestRateType)) {
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

			aInterestRateType.setTaskId(taskId);
			aInterestRateType.setNextTaskId(nextTaskId);
			aInterestRateType.setRoleCode(getRole());
			aInterestRateType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aInterestRateType, tranType);
			String operationRefs = getServiceOperations(taskId,aInterestRateType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aInterestRateType,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aInterestRateType, tranType);
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
		InterestRateType aInterestRateType = (InterestRateType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getInterestRateTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getInterestRateTypeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getInterestRateTypeService().doApprove(auditHeader);

						if (aInterestRateType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getInterestRateTypeService().doReject(auditHeader);

						if (aInterestRateType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_InterestRateTypeDialog,auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_InterestRateTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.interestRateType), true);
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
	private AuditHeader getAuditHeader(InterestRateType aInterestRateType,String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aInterestRateType.getBefImage(), aInterestRateType);
		return new AuditHeader(String.valueOf(aInterestRateType.getId()), null,
				null, null, auditDetail, aInterestRateType.getUserDetails(),
				getOverideMap());
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
			ErrorControl.showErrorControl(this.window_InterestRateTypeDialog,auditHeader);
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
		doShowNotes(this.interestRateType);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getInterestRateTypeListCtrl().search();
	}

	//
	

	@Override
	protected String getReference() {
		return String.valueOf(this.interestRateType.getIntRateTypeCode());
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

	public InterestRateType getInterestRateType() {
		return this.interestRateType;
	}
	public void setInterestRateType(InterestRateType interestRateType) {
		this.interestRateType = interestRateType;
	}

	public void setInterestRateTypeService(InterestRateTypeService interestRateTypeService) {
		this.interestRateTypeService = interestRateTypeService;
	}
	public InterestRateTypeService getInterestRateTypeService() {
		return this.interestRateTypeService;
	}

	public void setInterestRateTypeListCtrl(InterestRateTypeListCtrl interestRateTypeListCtrl) {
		this.interestRateTypeListCtrl = interestRateTypeListCtrl;
	}
	public InterestRateTypeListCtrl getInterestRateTypeListCtrl() {
		return this.interestRateTypeListCtrl;
	}

}
