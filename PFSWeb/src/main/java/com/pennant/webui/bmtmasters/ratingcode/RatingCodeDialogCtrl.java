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
 * FileName    		:  RatingCodeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.bmtmasters.ratingcode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.RatingCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/RatingCode/ratingCodeDialog.zul file.
 */
public class RatingCodeDialogCtrl extends GFCBaseCtrl<RatingCode> {
	private static final long serialVersionUID = -6289141323349585417L;
	private static final Logger logger = Logger.getLogger(RatingCodeDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_RatingCodeDialog; 	// autoWired

	protected Textbox 		ratingType; 				// autoWired
	protected Textbox 		ratingCode; 				// autoWired
	protected Textbox 		ratingCodeDesc; 			// autoWired
	protected Checkbox 		ratingIsActive; 			// autoWired

	// not autoWired Var's
	private RatingCode 		mratingCode; 				// over handed per parameter
	private transient 		RatingCodeListCtrl ratingCodeListCtrl; // over handed per parameter

	private transient boolean validationOn;
	
	protected Button btnSearchRatingType; 	// autoWired
	protected Textbox lovDescRatingTypeName;
	
	
	// ServiceDAOs / Domain Classes
	private transient RatingCodeService ratingCodeService;
	private transient PagedListService  pagedListService;
	

	/**
	 * default constructor.<br>
	 */
	public RatingCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RatingCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected RatingCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RatingCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RatingCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("ratingCode")) {
				this.mratingCode = (RatingCode) arguments.get("ratingCode");
				RatingCode befImage = new RatingCode();
				BeanUtils.copyProperties(this.mratingCode, befImage);
				this.mratingCode.setBefImage(befImage);

				setRatingCode(this.mratingCode);
			} else {
				setRatingCode(null);
			}

			doLoadWorkFlow(this.mratingCode.isWorkflow(),
					this.mratingCode.getWorkflowId(),
					this.mratingCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"RatingCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the ratingCodeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete ratingCode here.
			if (arguments.containsKey("ratingCodeListCtrl")) {
				setRatingCodeListCtrl((RatingCodeListCtrl) arguments
						.get("ratingCodeListCtrl"));
			} else {
				setRatingCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getRatingCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RatingCodeDialog.onClose();
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
		this.ratingCode.setMaxlength(8);
		this.ratingCodeDesc.setMaxlength(50);

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
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RatingCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RatingCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RatingCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RatingCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_RatingCodeDialog);
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
		doWriteBeanToComponents(this.mratingCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aRatingCode
	 *            RatingCode
	 */
	public void doWriteBeanToComponents(RatingCode aRatingCode) {
		logger.debug("Entering");

		this.ratingType.setValue(aRatingCode.getRatingType());
		this.ratingCode.setValue(aRatingCode.getRatingCode());
		this.ratingCodeDesc.setValue(aRatingCode.getRatingCodeDesc());
		this.ratingIsActive.setChecked(aRatingCode.isRatingIsActive());

		if (aRatingCode.isNewRecord()) {
			this.lovDescRatingTypeName.setValue("");
		} else {
			this.lovDescRatingTypeName.setValue(aRatingCode.getRatingType()	+ "-" + aRatingCode.getLovDescRatingTypeName());
		}
		this.recordStatus.setValue(aRatingCode.getRecordStatus());
		
		if(aRatingCode.isNew() || StringUtils.equals(aRatingCode.getRecordType(), PennantConstants.RECORD_TYPE_NEW)){
			this.ratingIsActive.setChecked(true);
			this.ratingIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRatingCode
	 */
	public void doWriteComponentsToBean(RatingCode aRatingCode) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aRatingCode.setLovDescRatingTypeName(this.lovDescRatingTypeName.getValue());
			aRatingCode.setRatingType(this.ratingType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingCode.setRatingCode(this.ratingCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingCode.setRatingCodeDesc(this.ratingCodeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingCode.setRatingIsActive(this.ratingIsActive.isChecked());
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

		aRatingCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aRatingCode
	 * @throws Exception
	 */
	public void doShowDialog(RatingCode aRatingCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aRatingCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.lovDescRatingTypeName.focus();
		} else {
			this.ratingCodeDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			this.ratingCode.setReadonly(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aRatingCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RatingCodeDialog.onClose();
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

		if (!this.ratingCode.isReadonly()){
			this.ratingCode.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingCodeDialog_RatingCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}		
		
		if (!this.ratingCodeDesc.isReadonly()){
			this.ratingCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingCodeDialog_RatingCodeDesc.value"), 
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
		this.ratingCode.setConstraint("");
		this.ratingCodeDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescRatingTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingCodeDialog_RatingType.value"),null,true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescRatingTypeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.ratingCode.setErrorMessage("");
		this.ratingCodeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a RatingCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final RatingCode aRatingCode = new RatingCode();
		BeanUtils.copyProperties(getRatingCode(), aRatingCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aRatingCode.getRatingType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aRatingCode.getRecordType())) {
				aRatingCode.setVersion(aRatingCode.getVersion() + 1);
				aRatingCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aRatingCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aRatingCode, tranType)) {
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

		if (getRatingCode().isNewRecord()) {
			this.ratingType.setDisabled(false);
			this.btnSearchRatingType.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.ratingType.setDisabled(true);
			this.btnSearchRatingType.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.ratingCode.setReadonly(isReadOnly("RatingCodeDialog_ratingCode"));
		this.ratingCodeDesc.setReadonly(isReadOnly("RatingCodeDialog_ratingCodeDesc"));
		this.ratingIsActive.setDisabled(isReadOnly("RatingCodeDialog_ratingIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.mratingCode.isNewRecord()) {
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

		this.btnSearchRatingType.setDisabled(true);
		this.ratingCode.setReadonly(true);
		this.ratingCodeDesc.setReadonly(true);
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
		this.lovDescRatingTypeName.setValue("");
		this.ratingCode.setValue("");
		this.ratingCodeDesc.setValue("");
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

		final RatingCode aRatingCode = new RatingCode();
		BeanUtils.copyProperties(getRatingCode(), aRatingCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the RatingCode object with the components data
		doWriteComponentsToBean(aRatingCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aRatingCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aRatingCode.getRecordType())) {
				aRatingCode.setVersion(aRatingCode.getVersion() + 1);
				if (isNew) {
					aRatingCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aRatingCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aRatingCode.setNewRecord(true);
				}
			}
		} else {
			aRatingCode.setVersion(aRatingCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aRatingCode, tranType)) {
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
	 * @param aRatingCode
	 *            (RatingCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(RatingCode aRatingCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aRatingCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aRatingCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRatingCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aRatingCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRatingCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aRatingCode);
				}

				if (isNotesMandatory(taskId, aRatingCode)) {
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

			aRatingCode.setTaskId(taskId);
			aRatingCode.setNextTaskId(nextTaskId);
			aRatingCode.setRoleCode(getRole());
			aRatingCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRatingCode, tranType);

			String operationRefs = getServiceOperations(taskId, aRatingCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aRatingCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aRatingCode, tranType);
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
		RatingCode aRatingCode = (RatingCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getRatingCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getRatingCodeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getRatingCodeService().doApprove(auditHeader);

						if (aRatingCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getRatingCodeService().doReject(auditHeader);

						if (aRatingCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RatingCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RatingCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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

	// Search Button Component Events

	public void onClick$btnSearchRatingType(Event event) {
		logger.debug("Entering" + event.toString());

		this.ratingType.getValue();

		Object dataObject = ExtendedSearchListBox.show(this.window_RatingCodeDialog, "RatingType");
		if (dataObject instanceof String) {
			this.ratingType.setValue(dataObject.toString());
			this.lovDescRatingTypeName.setValue("");
		} else {
			RatingType details = (RatingType) dataObject;
			if (details != null) {
				this.ratingType.setValue(details.getLovValue());
				this.lovDescRatingTypeName.setValue(details.getLovValue() + "-" + details.getRatingTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
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
	private AuditHeader getAuditHeader(RatingCode aRatingCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aRatingCode.getBefImage(), aRatingCode);
		return new AuditHeader(String.valueOf(aRatingCode.getId()), null, null,
				null, auditDetail, aRatingCode.getUserDetails(), getOverideMap());
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
			System.out.println(e);
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RatingCodeDialog,  auditHeader);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getRatingCodeListCtrl().search();
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("RatingCode");
		notes.setReference(getRatingCode().getRatingType() + PennantConstants.KEY_SEPERATOR + getRatingCode().getRatingCode());
		notes.setVersion(getRatingCode().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public RatingCode getRatingCode() {
		return this.mratingCode;
	}
	public void setRatingCode(RatingCode ratingCode) {
		this.mratingCode = ratingCode;
	}

	public void setRatingCodeService(RatingCodeService ratingCodeService) {
		this.ratingCodeService = ratingCodeService;
	}
	public RatingCodeService getRatingCodeService() {
		return this.ratingCodeService;
	}

	public void setRatingCodeListCtrl(RatingCodeListCtrl ratingCodeListCtrl) {
		this.ratingCodeListCtrl = ratingCodeListCtrl;
	}
	public RatingCodeListCtrl getRatingCodeListCtrl() {
		return this.ratingCodeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
