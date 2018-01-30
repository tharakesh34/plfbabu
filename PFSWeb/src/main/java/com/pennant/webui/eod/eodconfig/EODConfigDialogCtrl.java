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
 * FileName    		:  EODConfigDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-05-2017    														*
 *                                                                  						*
 * Modified Date    :  24-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.eod.eodconfig;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.service.eod.EODConfigService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/eod/EODConfig/eODConfigDialog.zul file. <br>
 */
public class EODConfigDialogCtrl extends GFCBaseCtrl<EODConfig> {

	private static final long			serialVersionUID	= 1L;
	private static final Logger			logger				= Logger.getLogger(EODConfigDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_EODConfigDialog;
	protected Checkbox					extMnthRequired;
	protected Datebox					mnthExtTo;
	protected Checkbox					active;
	private EODConfig					eODConfig;															// overhanded per param
	private EODConfig					appRovedeodConfig;
	private transient EODConfigListCtrl	eODConfigListCtrl;													// overhanded per param
	private transient EODConfigService	eODConfigService;

	/**
	 * default constructor.<br>
	 */
	public EODConfigDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "EODConfigDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.eODConfig.getEodConfigId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_EODConfigDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_EODConfigDialog);

		try {
			// Get the required arguments.
			this.eODConfig = (EODConfig) arguments.get("eodconfig");
			this.eODConfigListCtrl = (EODConfigListCtrl) arguments.get("eodconfigListCtrl");

			if (this.eODConfig == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			EODConfig eODConfig = new EODConfig();
			BeanUtils.copyProperties(this.eODConfig, eODConfig);
			this.eODConfig.setBefImage(eODConfig);

			// Render the page and display the data.
			doLoadWorkFlow(this.eODConfig.isWorkflow(), this.eODConfig.getWorkflowId(), this.eODConfig.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}
			appRovedeodConfig = eODConfigService.getApprovedEODConfig(eODConfig.getId());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.eODConfig);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.mnthExtTo.setFormat(PennantConstants.dateFormat);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EODConfigDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EODConfigDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EODConfigDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.eODConfig);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		eODConfigListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.eODConfig.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param eODConfig
	 * 
	 */
	public void doWriteBeanToComponents(EODConfig aEODConfig) {
		logger.debug(Literal.ENTERING);

		this.extMnthRequired.setChecked(aEODConfig.isExtMnthRequired());
		this.mnthExtTo.setValue(aEODConfig.getMnthExtTo());
		this.active.setChecked(aEODConfig.isActive());
		doCheckMonthEnd();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEODConfig
	 */
	public void doWriteComponentsToBean(EODConfig aEODConfig) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Extended month required
		try {
			aEODConfig.setExtMnthRequired(this.extMnthRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Month Extended To
		try {
			aEODConfig.setMnthExtTo(this.mnthExtTo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Active
		try {
			aEODConfig.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param eODConfig
	 *            The entity that need to be render.
	 */
	public void doShowDialog(EODConfig eODConfig) {
		logger.debug(Literal.LEAVING);

		if (eODConfig.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.extMnthRequired.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(eODConfig.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.extMnthRequired.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				btnDelete.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(eODConfig);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.mnthExtTo.isReadonly()) {

			String lable = Labels.getLabel("label_EODConfigDialog_MnthExtTo.value");
			if (appRovedeodConfig != null && appRovedeodConfig.isInExtMnth()) {
				//greater than today and less than current month
				this.mnthExtTo.setConstraint(new PTDateValidator(lable, true, DateUtility.getAppDate(),
						DateUtility.getMonthEnd(DateUtility.getAppDate()), true));
			} else {
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(DateUtility.getAppDate());
				calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
				//greater than current month end and less than next month end;
				this.mnthExtTo.setConstraint(new PTDateValidator(lable, true,
						DateUtility.getMonthEnd(DateUtility.getAppDate()),DateUtility.getMonthEnd(calendar.getTime()), false));
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.mnthExtTo.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		//Config Id
		//Extended month required
		//Month Extended To
		//Active

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a EODConfig object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final EODConfig aEODConfig = new EODConfig();
		BeanUtils.copyProperties(this.eODConfig, aEODConfig);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aEODConfig.getEodConfigId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aEODConfig.getRecordType()).equals("")) {
				aEODConfig.setVersion(aEODConfig.getVersion() + 1);
				aEODConfig.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aEODConfig.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aEODConfig.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aEODConfig.getNextTaskId(), aEODConfig);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aEODConfig, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.eODConfig.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);

		}

		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.extMnthRequired);
		readOnlyComponent(isReadOnly("EODConfigDialog_MnthExtTo"), this.mnthExtTo);
		readOnlyComponent(isReadOnly("EODConfigDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.eODConfig.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				this.btnDelete.setVisible(false);
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.extMnthRequired);
		readOnlyComponent(true, this.mnthExtTo);
		readOnlyComponent(true, this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.extMnthRequired.setChecked(false);
		this.mnthExtTo.setText("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final EODConfig aEODConfig = new EODConfig();
		BeanUtils.copyProperties(this.eODConfig, aEODConfig);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aEODConfig);

		isNew = aEODConfig.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aEODConfig.getRecordType())) {
				aEODConfig.setVersion(aEODConfig.getVersion() + 1);
				if (isNew) {
					aEODConfig.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aEODConfig.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEODConfig.setNewRecord(true);
				}
			}
		} else {
			aEODConfig.setVersion(aEODConfig.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aEODConfig, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(EODConfig aEODConfig, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aEODConfig.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aEODConfig.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEODConfig.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aEODConfig.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aEODConfig.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aEODConfig);
				}

				if (isNotesMandatory(taskId, aEODConfig)) {
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

			aEODConfig.setTaskId(taskId);
			aEODConfig.setNextTaskId(nextTaskId);
			aEODConfig.setRoleCode(getRole());
			aEODConfig.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aEODConfig, tranType);
			String operationRefs = getServiceOperations(taskId, aEODConfig);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aEODConfig, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aEODConfig, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		EODConfig aEODConfig = (EODConfig) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = eODConfigService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = eODConfigService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = eODConfigService.doApprove(auditHeader);

						if (aEODConfig.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = eODConfigService.doReject(auditHeader);
						if (aEODConfig.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_EODConfigDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_EODConfigDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.eODConfig), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	private void doCheckMonthEnd() {

		if (appRovedeodConfig != null && appRovedeodConfig.isInExtMnth()) {
			readOnlyComponent(true, this.extMnthRequired);
		}
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(EODConfig aEODConfig, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aEODConfig.getBefImage(), aEODConfig);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aEODConfig.getUserDetails(),
				getOverideMap());
	}

	public void setEODConfigService(EODConfigService eODConfigService) {
		this.eODConfigService = eODConfigService;
	}

}
