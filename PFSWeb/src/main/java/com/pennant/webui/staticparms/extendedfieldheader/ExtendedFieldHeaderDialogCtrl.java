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
 * FileName    		:  ExtendedFieldHeaderDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.staticparms.extendedfieldheader;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.service.staticparms.ExtendedFieldHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/ExtendedFieldHeader/extendedFieldHeaderDialog.zul
 * file.
 */
public class ExtendedFieldHeaderDialogCtrl extends GFCBaseCtrl<ExtendedFieldHeader> {
	private static final long serialVersionUID = -4892656164017054696L;
	private static final Logger logger = Logger.getLogger(ExtendedFieldHeaderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */

	protected Window 		window_ExtendedFieldHeaderDialog;	// autowired

	protected Combobox 		moduleName; 						// autowired
	protected Combobox 		subModuleName; 						// autowired
	protected Textbox 		tabHeading; 						// autowired
	protected Radiogroup 	numberOfColumns; 					// autowired
	protected Radio 		radio_column1;
	protected Radio 		radio_column2;


	// not auto wired vars
	private ExtendedFieldHeader extendedFieldHeader; // overhanded per param
	private transient ExtendedFieldHeaderListCtrl extendedFieldHeaderListCtrl; // overhanded per param
	private List<ValueLabel> modulesList = null;
	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient ExtendedFieldHeaderService extendedFieldHeaderService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtendedFieldHeaderDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ExtendedFieldHeader
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ExtendedFieldHeaderDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ExtendedFieldHeaderDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			if (arguments.containsKey("extendedFieldHeader")) {
				this.extendedFieldHeader = (ExtendedFieldHeader) arguments.get("extendedFieldHeader");
				ExtendedFieldHeader befImage = new ExtendedFieldHeader();
				BeanUtils.copyProperties(this.extendedFieldHeader, befImage);
				this.extendedFieldHeader.setBefImage(befImage);

				setExtendedFieldHeader(this.extendedFieldHeader);
			} else {
				setExtendedFieldHeader(null);
			}

			doLoadWorkFlow(this.extendedFieldHeader.isWorkflow(), this.extendedFieldHeader.getWorkflowId(),
					this.extendedFieldHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ExtendedFieldHeaderDialog");
			}

			// READ OVERHANDED params !
			// we get the extendedFieldHeaderListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete extendedFieldHeader here.
			if (arguments.containsKey("extendedFieldHeaderListCtrl")) {
				setExtendedFieldHeaderListCtrl((ExtendedFieldHeaderListCtrl) arguments.get("extendedFieldHeaderListCtrl"));
			} else {
				setExtendedFieldHeaderListCtrl(null);
			}
			
			if (arguments.containsKey("modulesList")) {
				modulesList = (List<ValueLabel>) arguments.get("modulesList");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getExtendedFieldHeader());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ExtendedFieldHeaderDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Empty sent any required attributes
		this.moduleName.setMaxlength(50);
		this.subModuleName.setMaxlength(50);
		this.tabHeading.setMaxlength(20);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldHeaderDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_ExtendedFieldHeaderDialog);
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
		doWriteBeanToComponents(this.extendedFieldHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aExtendedFieldHeader
	 *            ExtendedFieldHeader
	 */
	public void doWriteBeanToComponents(ExtendedFieldHeader aExtendedFieldHeader) {
		logger.debug("Entering");

		fillComboBox(this.moduleName, aExtendedFieldHeader.getModuleName(), modulesList, "");
		fillsubModule(this.subModuleName, aExtendedFieldHeader.getModuleName(),
				aExtendedFieldHeader.getSubModuleName());
		this.numberOfColumns.setSelectedIndex(0);
		
		// fillCombobox(this.subModuleName,PennantAppUtil.getSubModuleName(aExtendedFieldHeader.getModuleName()),aExtendedFieldHeader.getSubModuleName());
		this.tabHeading.setValue(aExtendedFieldHeader.getTabHeading());

		for (int i = 0; i < numberOfColumns.getItemCount(); i++) {
			if (this.numberOfColumns.getItemAtIndex(i).getValue().equals(aExtendedFieldHeader.getNumberOfColumns()==null?"":aExtendedFieldHeader.getNumberOfColumns().trim())) {
				this.numberOfColumns.setSelectedIndex(i);
			}
		}

		this.recordStatus.setValue(aExtendedFieldHeader.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aExtendedFieldHeader
	 */
	public void doWriteComponentsToBean(ExtendedFieldHeader aExtendedFieldHeader) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if(!this.moduleName.isDisabled() && this.moduleName.getSelectedIndex()<1){
				throw new WrongValueException(moduleName, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_ExtendedFieldHeaderDialog_ModuleName.value")}));
			}
			aExtendedFieldHeader.setModuleName(this.moduleName.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(!this.subModuleName.isDisabled() && this.subModuleName.getSelectedIndex()<1){
				throw new WrongValueException(subModuleName, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_ExtendedFieldHeaderDialog_SubModuleName.value")}));
			}
			aExtendedFieldHeader.setSubModuleName(this.subModuleName.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aExtendedFieldHeader.setSubModuleName(this.subModuleName.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtendedFieldHeader.setTabHeading(this.tabHeading.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtendedFieldHeader.setNumberOfColumns(this.numberOfColumns.getSelectedItem().getValue().toString());
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

		aExtendedFieldHeader.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aExtendedFieldHeader
	 * @throws Exception
	 */
	public void doShowDialog(ExtendedFieldHeader aExtendedFieldHeader) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aExtendedFieldHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.moduleName.focus();
		} else {
			this.tabHeading.focus();
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
			doWriteBeanToComponents(aExtendedFieldHeader);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ExtendedFieldHeaderDialog.onClose();
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

		if (!this.tabHeading.isReadonly()) {
			this.tabHeading.setConstraint(new PTStringValidator(Labels.getLabel("label_ExtendedFieldHeaderDialog_TabHeading.value"), PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.moduleName.setConstraint("");
		this.subModuleName.setConstraint("");
		this.tabHeading.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a ExtendedFieldHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final ExtendedFieldHeader aExtendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties(getExtendedFieldHeader(), aExtendedFieldHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
		+ Labels.getLabel(aExtendedFieldHeader.getModuleName());
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aExtendedFieldHeader.getRecordType())) {
				aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
				aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aExtendedFieldHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aExtendedFieldHeader, tranType)) {
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

		if (getExtendedFieldHeader().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.moduleName.setDisabled(false);
			this.subModuleName.setDisabled(false);
		} else {
			this.btnCancel.setVisible(true);
			this.moduleName.setDisabled(true);
			this.subModuleName.setDisabled(true);
		}

		this.tabHeading.setReadonly(isReadOnly("ExtendedFieldHeaderDialog_tabHeading"));
		this.radio_column1.setDisabled(isReadOnly("ExtendedFieldHeaderDialog_numberOfColumns"));
		this.radio_column2.setDisabled(isReadOnly("ExtendedFieldHeaderDialog_numberOfColumns"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.extendedFieldHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.moduleName.setDisabled(true);
		this.subModuleName.setDisabled(true);
		this.tabHeading.setReadonly(true);
		this.radio_column1.setDisabled(true);
		this.radio_column2.setDisabled(true);


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

		this.moduleName.setValue("");
		this.subModuleName.setValue("");
		this.tabHeading.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final ExtendedFieldHeader aExtendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties(getExtendedFieldHeader(), aExtendedFieldHeader);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ExtendedFieldHeader object with the components data
		doWriteComponentsToBean(aExtendedFieldHeader);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aExtendedFieldHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aExtendedFieldHeader.getRecordType())) {
				aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
				if (isNew) {
					aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aExtendedFieldHeader.setNewRecord(true);
				}
			}
		} else {
			aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aExtendedFieldHeader, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(ExtendedFieldHeader aExtendedFieldHeader, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aExtendedFieldHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aExtendedFieldHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aExtendedFieldHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aExtendedFieldHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aExtendedFieldHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aExtendedFieldHeader);
				}

				if (isNotesMandatory(taskId, aExtendedFieldHeader)) {
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

			aExtendedFieldHeader.setTaskId(taskId);
			aExtendedFieldHeader.setNextTaskId(nextTaskId);
			aExtendedFieldHeader.setRoleCode(getRole());
			aExtendedFieldHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aExtendedFieldHeader, tranType);

			String operationRefs = getServiceOperations(taskId, aExtendedFieldHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aExtendedFieldHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			aExtendedFieldHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			auditHeader = getAuditHeader(aExtendedFieldHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		ExtendedFieldHeader aExtendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getExtendedFieldHeaderService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getExtendedFieldHeaderService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getExtendedFieldHeaderService().doApprove(auditHeader);

						if (aExtendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getExtendedFieldHeaderService().doReject(auditHeader);
						if (aExtendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ExtendedFieldHeaderDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldHeaderDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.extendedFieldHeader), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
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

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return this.extendedFieldHeader;
	}
	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public void setExtendedFieldHeaderService(ExtendedFieldHeaderService extendedFieldHeaderService) {
		this.extendedFieldHeaderService = extendedFieldHeaderService;
	}
	public ExtendedFieldHeaderService getExtendedFieldHeaderService() {
		return this.extendedFieldHeaderService;
	}

	public void setExtendedFieldHeaderListCtrl(ExtendedFieldHeaderListCtrl extendedFieldHeaderListCtrl) {
		this.extendedFieldHeaderListCtrl = extendedFieldHeaderListCtrl;
	}
	public ExtendedFieldHeaderListCtrl getExtendedFieldHeaderListCtrl() {
		return this.extendedFieldHeaderListCtrl;
	}
	
	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}
	
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.moduleName.setErrorMessage("");
		this.subModuleName.setErrorMessage("");
		this.tabHeading.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void fillsubModule(Combobox combobox, String moduleName, String value) {
		if (this.moduleName.getSelectedItem() != null) {
			HashMap<String, String> hashMap = PennantStaticListUtil.getModuleName().get(moduleName) == null ? new HashMap<String, String>()
					: PennantStaticListUtil.getModuleName().get(moduleName);
			ArrayList<String> arrayList = new ArrayList<String>(hashMap.keySet());
			subModuleName.getItems().clear();
			Comboitem comboitem = new Comboitem();
			comboitem.setLabel("----Select-----");
			comboitem.setValue("");
			subModuleName.appendChild(comboitem);
			subModuleName.setSelectedItem(comboitem);
			if (arrayList != null) {
				for (int i = 0; i < arrayList.size(); i++) {
					comboitem = new Comboitem();
					comboitem.setLabel(Labels.getLabel("label_ExtendedField_"+arrayList.get(i)));
					comboitem.setValue(arrayList.get(i));
					subModuleName.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(arrayList.get(i))) {
						subModuleName.setSelectedItem(comboitem);
					}
				}
			}
		} else {
			subModuleName.getItems().clear();
		}
	}

	public void onChange$moduleName(Event event) {
		if (this.moduleName.getSelectedItem() != null) {
			String module = this.moduleName.getSelectedItem().getValue().toString();
			fillsubModule(this.subModuleName, module, "");
		}
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
	
	// Audit Changes
	
	private AuditHeader getAuditHeader(ExtendedFieldHeader aExtendedFieldHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aExtendedFieldHeader.getBefImage(), aExtendedFieldHeader);
		return new AuditHeader(String.valueOf(aExtendedFieldHeader.getModuleId()), null, null, null, auditDetail,
				aExtendedFieldHeader.getUserDetails(), getOverideMap());
	}
	
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ExtendedFieldHeaderDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.extendedFieldHeader);
	}
	

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getExtendedFieldHeaderListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.extendedFieldHeader.getModuleId());
	}
}
