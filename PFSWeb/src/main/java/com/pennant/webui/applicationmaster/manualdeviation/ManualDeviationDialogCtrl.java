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
 * FileName    		:  ManualDeviationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2018    														*
 *                                                                  						*
 * Modified Date    :  03-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.manualdeviation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.applicationmaster.ManualDeviationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/ManualDeviation/manualDeviationDialog.zul file.
 * <br>
 */
public class ManualDeviationDialogCtrl extends GFCBaseCtrl<ManualDeviation> {

	private static final long					serialVersionUID	= 1L;
	private static final Logger					logger				= Logger.getLogger(ManualDeviationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_ManualDeviationDialog;
	protected Space								space_Code;
	protected Textbox							code;
	protected Space								space_Description;
	protected Textbox							description;
	protected Space								space_Module;
	protected Combobox							module;
	protected Space								space_Categorization;
	protected ExtendedCombobox					categorization;
	protected Space								space_Severity;
	protected ExtendedCombobox					severity;
	protected Space								space_Active;
	protected Checkbox							active;
	private ManualDeviation						manualDeviation;														// overhanded per param

	private transient ManualDeviationListCtrl	manualDeviationListCtrl;												// overhanded per param
	private transient ManualDeviationService	manualDeviationService;

	private List<ValueLabel>					moduleList			= PennantStaticListUtil.getWorkFlowModules();

	/**
	 * default constructor.<br>
	 */
	public ManualDeviationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManualDeviationDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.manualDeviation.getDeviationID()));
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
	public void onCreate$window_ManualDeviationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ManualDeviationDialog);

		try {
			// Get the required arguments.
			this.manualDeviation = (ManualDeviation) arguments.get("manualDeviation");
			this.manualDeviationListCtrl = (ManualDeviationListCtrl) arguments.get("manualDeviationListCtrl");

			if (this.manualDeviation == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			ManualDeviation manualDeviation = new ManualDeviation();
			BeanUtils.copyProperties(this.manualDeviation, manualDeviation);
			this.manualDeviation.setBefImage(manualDeviation);

			// Render the page and display the data.
			doLoadWorkFlow(this.manualDeviation.isWorkflow(), this.manualDeviation.getWorkflowId(),
					this.manualDeviation.getNextTaskId());

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
			doShowDialog(this.manualDeviation);
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

		this.categorization.setMandatoryStyle(true);
		this.categorization.setModuleName("MDEV_CAT");
		this.categorization.setValueColumn("FieldCodeValue");
		this.categorization.setDescColumn("ValueDesc");
		this.categorization.setValidateColumns(new String[] { "FieldCodeValue" });

		this.severity.setMandatoryStyle(true);
		this.severity.setModuleName("MDEV_SEV");
		this.severity.setValueColumn("FieldCodeValue");
		this.severity.setDescColumn("ValueDesc");
		this.severity.setValidateColumns(new String[] { "FieldCodeValue" });

		this.code.setMaxlength(20);
		this.description.setMaxlength(100);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ManualDeviationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ManualDeviationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ManualDeviationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ManualDeviationDialog_btnSave"));
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
		doShowNotes(this.manualDeviation);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$categorization(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = categorization.getObject();

		if (dataObject instanceof String) {
			this.categorization.setValue(dataObject.toString());
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.categorization.setAttribute("categorization", details.getFieldCodeId());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$severity(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = severity.getObject();

		if (dataObject instanceof String) {
			this.severity.setValue(dataObject.toString());
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.severity.setAttribute("severity", details.getFieldCodeId());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		manualDeviationListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.manualDeviation.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param manualDeviation
	 * 
	 */
	public void doWriteBeanToComponents(ManualDeviation aManualDeviation) {
		logger.debug(Literal.ENTERING);

		this.code.setValue(aManualDeviation.getCode());
		this.description.setValue(aManualDeviation.getDescription());
		String moduleName = PennantConstants.WORFLOW_MODULE_FINANCE;
		fillComboBox(this.module, moduleName, moduleList, "");
		if (aManualDeviation.getCategorization() != Long.MIN_VALUE && aManualDeviation.getCategorization() != 0) {
			this.categorization.setAttribute("categorization", aManualDeviation.getCategorization());
			this.categorization.setValue(aManualDeviation.getCategorizationCode(),
					aManualDeviation.getCategorizationName());
		}
		if (aManualDeviation.getSeverity() != Long.MIN_VALUE && aManualDeviation.getSeverity() != 0) {
			this.severity.setAttribute("severity", aManualDeviation.getSeverity());
			this.severity.setValue(aManualDeviation.getSeverityCode(), aManualDeviation.getSeverityName());
		}
		this.active.setChecked(aManualDeviation.isActive());
		this.recordStatus.setValue(aManualDeviation.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aManualDeviation
	 */
	public void doWriteComponentsToBean(ManualDeviation aManualDeviation) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Code
		try {
			aManualDeviation.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Description
		try {
			aManualDeviation.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Module
		try {
			String strModule = null;
			if (this.module.getSelectedItem() != null) {
				strModule = this.module.getSelectedItem().getValue().toString();
			}
			if (strModule != null && !PennantConstants.List_Select.equals(strModule)) {
				aManualDeviation.setModule(strModule);

			} else {
				aManualDeviation.setModule(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Categorization
		try {
			this.categorization.getValidatedValue();
			Object obj = this.categorization.getAttribute("categorization");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aManualDeviation.setCategorization(Long.valueOf((obj.toString())));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Severity
		try {
			this.severity.getValidatedValue();
			Object obj = this.severity.getAttribute("severity");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aManualDeviation.setSeverity(Long.valueOf((obj.toString())));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Active
		try {
			aManualDeviation.setActive(this.active.isChecked());
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
	 * @param manualDeviation
	 *            The entity that need to be render.
	 */
	public void doShowDialog(ManualDeviation manualDeviation) {
		logger.debug(Literal.LEAVING);

		if (manualDeviation.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.code.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(manualDeviation.getRecordType())) {
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

		doWriteBeanToComponents(manualDeviation);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_ManualDeviationDialog_Code.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManualDeviationDialog_Description.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.module.isReadonly()) {
			this.module.setConstraint(
					new StaticListValidator(moduleList, Labels.getLabel("label_ManualDeviationDialog_Module.value")));
		}
		if (!this.categorization.isReadonly()) {
			this.categorization.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ManualDeviationDialog_Categorization.value"), null, true, true));
		}
		if (!this.severity.isReadonly()) {
			this.severity.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ManualDeviationDialog_Severity.value"), null, true, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.code.setConstraint("");
		this.description.setConstraint("");
		this.module.setConstraint("");
		this.categorization.setConstraint("");
		this.severity.setConstraint("");

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

	/**
	 * Deletes a ManualDeviation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final ManualDeviation aManualDeviation = new ManualDeviation();
		BeanUtils.copyProperties(this.manualDeviation, aManualDeviation);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aManualDeviation.getCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aManualDeviation.getRecordType()).equals("")) {
				aManualDeviation.setVersion(aManualDeviation.getVersion() + 1);
				aManualDeviation.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aManualDeviation.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aManualDeviation.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aManualDeviation.getNextTaskId(),
							aManualDeviation);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aManualDeviation, tranType)) {
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

		if (this.manualDeviation.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.code);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.code);

		}
		readOnlyComponent(true, this.module);
		readOnlyComponent(isReadOnly("ManualDeviationDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("ManualDeviationDialog_Categorization"), this.categorization);
		readOnlyComponent(isReadOnly("ManualDeviationDialog_Severity"), this.severity);
		readOnlyComponent(isReadOnly("ManualDeviationDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.manualDeviation.isNewRecord()) {
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
		readOnlyComponent(true, this.module);
		readOnlyComponent(true, this.categorization);
		readOnlyComponent(true, this.severity);
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
		this.module.setSelectedIndex(0);
		this.categorization.setValue("");
		this.severity.setValue("");
		;
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final ManualDeviation aManualDeviation = new ManualDeviation();
		BeanUtils.copyProperties(this.manualDeviation, aManualDeviation);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aManualDeviation);

		isNew = aManualDeviation.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aManualDeviation.getRecordType())) {
				aManualDeviation.setVersion(aManualDeviation.getVersion() + 1);
				if (isNew) {
					aManualDeviation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aManualDeviation.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aManualDeviation.setNewRecord(true);
				}
			}
		} else {
			aManualDeviation.setVersion(aManualDeviation.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aManualDeviation, tranType)) {
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ManualDeviation aManualDeviation, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aManualDeviation.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aManualDeviation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aManualDeviation.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aManualDeviation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aManualDeviation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aManualDeviation);
				}

				if (isNotesMandatory(taskId, aManualDeviation)) {
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

			aManualDeviation.setTaskId(taskId);
			aManualDeviation.setNextTaskId(nextTaskId);
			aManualDeviation.setRoleCode(getRole());
			aManualDeviation.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aManualDeviation, tranType);
			String operationRefs = getServiceOperations(taskId, aManualDeviation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aManualDeviation, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aManualDeviation, tranType);
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
		ManualDeviation aManualDeviation = (ManualDeviation) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = manualDeviationService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = manualDeviationService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = manualDeviationService.doApprove(auditHeader);

						if (aManualDeviation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = manualDeviationService.doReject(auditHeader);
						if (aManualDeviation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ManualDeviationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ManualDeviationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.manualDeviation), true);
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

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ManualDeviation aManualDeviation, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualDeviation.getBefImage(), aManualDeviation);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aManualDeviation.getUserDetails(),
				getOverideMap());
	}

	public void setManualDeviationService(ManualDeviationService manualDeviationService) {
		this.manualDeviationService = manualDeviationService;
	}

}
