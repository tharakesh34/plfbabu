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
 * * FileName : ExtInterfaceConfigurationDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 10-08-2019 * * Modified Date : 10-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-08-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.externalinterface.InterfaceConfiguration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennant.backend.service.externalinterface.ExtInterfaceConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ExtInterface/InterfaceConfiguration/ExtInterfaceConfigurationDialog.zul file. <br>
 */
public class ExtInterfaceConfigurationDialogCtrl extends GFCBaseCtrl<InterfaceConfiguration> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ExtInterfaceConfigurationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExtInterfaceConfigurationDialog;
	protected Space space_Code;
	protected Textbox code;
	protected Space space_Description;
	protected Textbox description;
	protected Space space_Type;
	protected Combobox type;
	protected Space space_NotificationType;
	protected Combobox notificationType;
	protected Checkbox active;
	private InterfaceConfiguration interfaceConfiguration; // overhanded
	private transient ExtInterfaceConfigurationListCtrl ExtInterfaceConfigurationListCtrl; // overhanded
	protected Textbox errorCodes;
	private Textbox emails;
	// param
	private transient ExtInterfaceConfigurationService ExtInterfaceConfigurationService;
	private Row emailRow;
	private List<ValueLabel> listNotificationType = PennantStaticListUtil.getNotificationTypeList();
	private List<ValueLabel> listInterfaceType = PennantStaticListUtil.getInterfaceTypeList();
	private static Pattern pattern;
	private Matcher matcher;

	/**
	 * default constructor.<br>
	 */
	public ExtInterfaceConfigurationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtInterfaceConfigurationDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.interfaceConfiguration.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExtInterfaceConfigurationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ExtInterfaceConfigurationDialog);

		try {
			// Get the required arguments.
			this.interfaceConfiguration = (InterfaceConfiguration) arguments.get("InterfaceConfiguration");
			this.ExtInterfaceConfigurationListCtrl = (ExtInterfaceConfigurationListCtrl) arguments
					.get("ExtInterfaceConfigurationListCtrl");

			if (this.interfaceConfiguration == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			InterfaceConfiguration InterfaceConfiguration = new InterfaceConfiguration();
			BeanUtils.copyProperties(this.interfaceConfiguration, InterfaceConfiguration);
			this.interfaceConfiguration.setBefImage(InterfaceConfiguration);

			// Render the page and display the data.
			doLoadWorkFlow(this.interfaceConfiguration.isWorkflow(), this.interfaceConfiguration.getWorkflowId(),
					this.interfaceConfiguration.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.interfaceConfiguration);
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

		this.code.setMaxlength(10);
		this.description.setMaxlength(20);
		this.errorCodes.setMaxlength(20);
		this.emails.setMaxlength(1000);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtInterfaceConfigurationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtInterfaceConfigurationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ExtInterfaceConfigurationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtInterfaceConfigurationDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.interfaceConfiguration);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		ExtInterfaceConfigurationListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.interfaceConfiguration.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param InterfaceConfiguration
	 * 
	 */
	public void doWriteBeanToComponents(InterfaceConfiguration aExtInterfaceConfiguration) {
		logger.debug(Literal.ENTERING);

		this.code.setValue(aExtInterfaceConfiguration.getCode());
		this.description.setValue(aExtInterfaceConfiguration.getDescription());
		fillComboBox(this.type, aExtInterfaceConfiguration.getType(), listInterfaceType, "");
		fillComboBox(this.notificationType, String.valueOf(aExtInterfaceConfiguration.getNotificationType()),
				listNotificationType, "");

		this.active.setChecked(aExtInterfaceConfiguration.isActive());
		this.recordStatus.setValue(aExtInterfaceConfiguration.getRecordStatus());
		this.errorCodes.setValue(aExtInterfaceConfiguration.getErrorCodes());
		if (2 == aExtInterfaceConfiguration.getNotificationType()) {
			emailRow.setVisible(true);
			this.emails.setValue(aExtInterfaceConfiguration.getContactsDetail());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aExtInterfaceConfiguration
	 */
	public void doWriteComponentsToBean(InterfaceConfiguration aExtInterfaceConfiguration) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Service Code
		try {
			aExtInterfaceConfiguration.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Service Name
		try {
			aExtInterfaceConfiguration.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtInterfaceConfiguration.setContactsDetail(this.emails.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Type
		try {
			String strType = null;
			if (this.type.getSelectedItem() != null) {
				strType = this.type.getSelectedItem().getValue().toString();
			}
			if (strType != null && !PennantConstants.List_Select.equals(strType)) {
				aExtInterfaceConfiguration.setType(strType);

			} else {
				aExtInterfaceConfiguration.setType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Notification Type
		try {
			String strNotificationType = null;
			if (this.notificationType.getSelectedItem() != null) {
				strNotificationType = this.notificationType.getSelectedItem().getValue().toString();
			}
			if (strNotificationType != null && !PennantConstants.List_Select.equals(strNotificationType)) {
				aExtInterfaceConfiguration.setNotificationType(Integer.parseInt(strNotificationType));

			} else {
				aExtInterfaceConfiguration.setNotificationType(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// error Codes
		try {
			aExtInterfaceConfiguration.setErrorCodes(this.errorCodes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// active
		try {
			aExtInterfaceConfiguration.setActive(this.active.isChecked());
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
	 * @param InterfaceConfiguration The entity that need to be render.
	 */
	public void doShowDialog(InterfaceConfiguration interfaceConfiguration) {
		logger.debug(Literal.LEAVING);

		if (interfaceConfiguration.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.code.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(interfaceConfiguration.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.description.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(interfaceConfiguration);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		boolean wrongMail = false;
		if (!this.code.isReadonly()) {
			this.code.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ExtInterfaceConfigurationDialog_Code.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ExtInterfaceConfigurationDialog_Description.value"), null, true));
		}
		if (!this.type.isReadonly()) {
			this.type.setConstraint(new StaticListValidator(listInterfaceType,
					Labels.getLabel("label_ExtInterfaceConfigurationDialog_Type.value")));
		}
		if (!this.notificationType.isReadonly()) {
			this.notificationType.setConstraint(new StaticListValidator(listNotificationType,
					Labels.getLabel("label_ExtInterfaceConfigurationDialog_NotificationType.value")));
		}
		/*
		 * if (!this.errorCodes.isReadonly()) { this.errorCodes.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_ExtInterfaceConfigurationDialog_errorCodes.value"),
		 * PennantRegularExpressions.REGEX_NAME, true)); }
		 */
		if (!this.emails.isReadonly()) {
			if (emails != null && StringUtils.isNotBlank(emails.getValue())) {
				String[] emailIds = emails.getValue().split(",");
				if (emailIds != null) {
					for (String mail : emailIds) {
						if (!validateEmail(mail)) {
							wrongMail = true;
							break;
						}
					}

				}
			}
			if (wrongMail) {
				this.emails.setConstraint(
						new PTEmailValidator(Labels.getLabel("label_CustomerEMailDialog_CustEMail.value"), true));
			}

		}

		logger.debug(Literal.LEAVING);
	}

	public boolean validateEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.code.setConstraint("");
		this.description.setConstraint("");
		this.type.setConstraint("");
		this.notificationType.setConstraint("");
		this.errorCodes.setConstraint("");
		this.emails.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

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

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final InterfaceConfiguration aExtInterfaceConfiguration = new InterfaceConfiguration();
		BeanUtils.copyProperties(this.interfaceConfiguration, aExtInterfaceConfiguration);

		doDelete(String.valueOf(aExtInterfaceConfiguration.getId()), aExtInterfaceConfiguration);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.interfaceConfiguration.isNewRecord()) {
			this.btnCancel.setVisible(false);

		} else {
			this.btnCancel.setVisible(true);
			this.code.setReadonly(true);
			this.description.setReadonly(true);
		}

		this.type.setReadonly(isReadOnly("ExtInterfaceConfigurationDialog_Type"));
		this.notificationType.setReadonly(isReadOnly("ExtInterfaceConfigurationDialog_NotificationType"));
		this.active.setDisabled(isReadOnly("ExtInterfaceConfigurationDialog_active"));
		/*
		 * this.errorCodes.(isReadOnly("ExternalInterfaceConfigurationDialog_errorCodes"));
		 */
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.interfaceConfiguration.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
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

		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.type);
		readOnlyComponent(true, this.notificationType);
		readOnlyComponent(true, this.errorCodes);
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
		this.code.setValue("");
		this.description.setValue("");
		this.type.setSelectedIndex(0);
		this.errorCodes.setValue("");
		this.notificationType.setSelectedIndex(0);
		this.emails.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final InterfaceConfiguration aExtInterfaceConfiguration = new InterfaceConfiguration();
		BeanUtils.copyProperties(getInterfaceConfiguration(), aExtInterfaceConfiguration);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aExtInterfaceConfiguration);

		isNew = aExtInterfaceConfiguration.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aExtInterfaceConfiguration.getRecordType())) {
				aExtInterfaceConfiguration.setVersion(aExtInterfaceConfiguration.getVersion() + 1);
				if (isNew) {
					aExtInterfaceConfiguration.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExtInterfaceConfiguration.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aExtInterfaceConfiguration.setNewRecord(true);
				}
			}
		} else {
			aExtInterfaceConfiguration.setVersion(aExtInterfaceConfiguration.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aExtInterfaceConfiguration, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(InterfaceConfiguration configuration, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		configuration.setEodDate(SysParamUtil.getAppDate());
		configuration.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		configuration.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		configuration.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			configuration.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(configuration.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, configuration);
				}

				if (isNotesMandatory(taskId, configuration)) {
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

			configuration.setTaskId(taskId);
			configuration.setNextTaskId(nextTaskId);
			configuration.setRoleCode(getRole());
			configuration.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(configuration, tranType);
			String operationRefs = getServiceOperations(taskId, configuration);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(configuration, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(configuration, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		InterfaceConfiguration aExtInterfaceConfiguration = (InterfaceConfiguration) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = ExtInterfaceConfigurationService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = ExtInterfaceConfigurationService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = ExtInterfaceConfigurationService.doApprove(auditHeader);

					if (aExtInterfaceConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = ExtInterfaceConfigurationService.doReject(auditHeader);
					if (aExtInterfaceConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ExtInterfaceConfigurationDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ExtInterfaceConfigurationDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.interfaceConfiguration), true);
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

	public void onSelect$notificationType(Event event) {
		logger.debug(Literal.ENTERING);
		String emailCode = notificationType.getSelectedItem().getValue();
		if (StringUtils.equalsIgnoreCase("2", emailCode)) {
			emailRow.setVisible(true);
		} else {
			emailRow.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(InterfaceConfiguration aExtInterfaceConfiguration, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aExtInterfaceConfiguration.getBefImage(),
				aExtInterfaceConfiguration);
		return new AuditHeader(getReference(), null, null, null, auditDetail,
				aExtInterfaceConfiguration.getUserDetails(), getOverideMap());
	}

	public InterfaceConfiguration getInterfaceConfiguration() {
		return interfaceConfiguration;
	}

	public void setInterfaceConfiguration(InterfaceConfiguration interfaceConfiguration) {
		this.interfaceConfiguration = interfaceConfiguration;
	}

	public void setExtInterfaceConfigurationService(ExtInterfaceConfigurationService ExtInterfaceConfigurationService) {
		this.ExtInterfaceConfigurationService = ExtInterfaceConfigurationService;
	}

}
