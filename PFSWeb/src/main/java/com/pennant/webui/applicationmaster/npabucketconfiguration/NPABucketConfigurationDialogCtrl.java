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
 * FileName    		:  NPABucketConfigurationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.npabucketconfiguration;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.NPABucketConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/applicationmaster/NPABucketConfiguration/nPABucketConfigurationDialog.zul file. <br>
 */
public class NPABucketConfigurationDialogCtrl extends GFCBaseCtrl<NPABucketConfiguration> {

	private static final long							serialVersionUID	= 1L;
	private static final Logger							logger				= Logger.getLogger(NPABucketConfigurationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * 
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window									window_NPABucketConfigurationDialog;
	protected ExtendedCombobox							productCode;
	protected ExtendedCombobox							bucketID;
	protected Intbox									dueDays;
	protected Checkbox									suspendProfit;
	protected Row										row2;
	private NPABucketConfiguration						nPABucketConfiguration;														// overhanded per param

	private transient NPABucketConfigurationListCtrl	nPABucketConfigurationListCtrl;												// overhanded per param
	private transient NPABucketConfigurationService		nPABucketConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public NPABucketConfigurationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "NPABucketConfigurationDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.nPABucketConfiguration.getConfigID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_NPABucketConfigurationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_NPABucketConfigurationDialog);

		try {
			// Get the required arguments.
			this.nPABucketConfiguration = (NPABucketConfiguration) arguments.get("npabucketconfiguration");
			this.nPABucketConfigurationListCtrl = (NPABucketConfigurationListCtrl) arguments
					.get("npabucketconfigurationListCtrl");

			if (this.nPABucketConfiguration == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			NPABucketConfiguration nPABucketConfiguration = new NPABucketConfiguration();
			BeanUtils.copyProperties(this.nPABucketConfiguration, nPABucketConfiguration);
			this.nPABucketConfiguration.setBefImage(nPABucketConfiguration);

			// Render the page and display the data.
			doLoadWorkFlow(this.nPABucketConfiguration.isWorkflow(), this.nPABucketConfiguration.getWorkflowId(),
					this.nPABucketConfiguration.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.nPABucketConfiguration);
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

		this.productCode.setModuleName("Product");
		this.productCode.setMandatoryStyle(true);
		this.productCode.setValueColumn("ProductCode");
		this.productCode.setDescColumn("ProductDesc");
		this.productCode.setValidateColumns(new String[] { "ProductCode" });
				
		this.bucketID.setModuleName("NPABucket");
		this.bucketID.setMandatoryStyle(true);
		this.bucketID.setValueColumn("BucketCode");
		this.bucketID.setDescColumn("BucketDesc");
		this.bucketID.setValidateColumns(new String[] { "BucketCode" });

		this.dueDays.setMaxlength(5);
		this.row2.setVisible(false);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_NPABucketConfigurationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_NPABucketConfigurationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_NPABucketConfigurationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_NPABucketConfigurationDialog_btnSave"));
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
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.nPABucketConfiguration);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		nPABucketConfigurationListCtrl.search();
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.nPABucketConfiguration.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param nPABucketConfiguration
	 * 
	 */
	public void doWriteBeanToComponents(NPABucketConfiguration aNPABucketConfiguration) {
		logger.debug(Literal.ENTERING);

		this.productCode.setValue(aNPABucketConfiguration.getProductCode());
		this.dueDays.setValue(aNPABucketConfiguration.getDueDays());
		this.suspendProfit.setChecked(aNPABucketConfiguration.isSuspendProfit());
		
		this.bucketID.setObject(new NPABucket(aNPABucketConfiguration.getBucketID()));
		this.bucketID.setValue(aNPABucketConfiguration.getBucketCode(),aNPABucketConfiguration.getBucketIDName());
		
		if (aNPABucketConfiguration.isNewRecord()) {
			this.productCode.setDescription("");
		} else {
			this.productCode.setDescription(aNPABucketConfiguration.getProductCodeName());
		}
		this.recordStatus.setValue(aNPABucketConfiguration.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNPABucketConfiguration
	 */
	public void doWriteComponentsToBean(NPABucketConfiguration aNPABucketConfiguration) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Product Code
		try {
			aNPABucketConfiguration.setProductCode(this.productCode.getValidatedValue());
			aNPABucketConfiguration.setProductCodeName(this.productCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bucket ID
		try {
			this.bucketID.getValidatedValue();
			NPABucket nPABucket = (NPABucket) this.bucketID.getObject();
			aNPABucketConfiguration.setBucketID(nPABucket.getBucketID());
			aNPABucketConfiguration.setBucketCode(nPABucket.getBucketCode());
			aNPABucketConfiguration.setBucketIDName(nPABucket.getBucketDesc());
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Due Days
		try {
			aNPABucketConfiguration.setDueDays(this.dueDays.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Suspend Profit
		try {
			aNPABucketConfiguration.setSuspendProfit(this.suspendProfit.isChecked());
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
	 * @param nPABucketConfiguration
	 *            The entity that need to be render.
	 */
	public void doShowDialog(NPABucketConfiguration nPABucketConfiguration) {
		logger.debug(Literal.LEAVING);

		if (nPABucketConfiguration.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.productCode.focus();
		} else {
			this.bucketID.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(nPABucketConfiguration.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.dueDays.focus();
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

		doWriteBeanToComponents(nPABucketConfiguration);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.productCode.isReadonly()) {
			this.productCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_NPABucketConfigurationDialog_ProductCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.bucketID.isReadonly()) {
			this.bucketID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_NPABucketConfigurationDialog_BucketID.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.dueDays.isReadonly()) {
			this.dueDays.setConstraint(new PTNumberValidator(Labels
					.getLabel("label_NPABucketConfigurationDialog_DueDays.value"), true, false, 0));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.productCode.setConstraint("");
		this.bucketID.setConstraint("");
		this.dueDays.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		//Config ID
		//Product Code
		//Bucket ID
		//Due Days
		//Suspend Profit

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
	
	
	/*public void onFulfill$bucketID(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = bucketID.getObject();
		if (dataObject instanceof String) {
			this.bucketID.setValue(dataObject.toString());
			this.bucketID.setDescription("");
		} else {
			NPABucket details = (NPABucket) dataObject;
			if (details != null) {
				this.bucketID.setAttribute("BucketID", details.getBucketID());
			}
		}
		logger.debug("Leaving" + event.toString());
	}*/
	
	/**
	 * Deletes a NPABucketConfiguration object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug(Literal.LEAVING);

		final NPABucketConfiguration aNPABucketConfiguration = new NPABucketConfiguration();
		BeanUtils.copyProperties(this.nPABucketConfiguration, aNPABucketConfiguration);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_NPABucketConfigurationDialog_ProductCode.value") + " : "
				+ aNPABucketConfiguration.getProductCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aNPABucketConfiguration.getRecordType()).equals("")) {
				aNPABucketConfiguration.setVersion(aNPABucketConfiguration.getVersion() + 1);
				aNPABucketConfiguration.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aNPABucketConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aNPABucketConfiguration.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(),
							aNPABucketConfiguration.getNextTaskId(), aNPABucketConfiguration);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aNPABucketConfiguration, tranType)) {
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

		if (this.nPABucketConfiguration.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.productCode);
			readOnlyComponent(false, this.bucketID);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.productCode);
			readOnlyComponent(true, this.bucketID);

		}

		readOnlyComponent(isReadOnly("NPABucketConfigurationDialog_DueDays"), this.dueDays);
		readOnlyComponent(isReadOnly("NPABucketConfigurationDialog_SuspendProfit"), this.suspendProfit);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.nPABucketConfiguration.isNewRecord()) {
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

		readOnlyComponent(true, this.productCode);
		readOnlyComponent(true, this.bucketID);
		readOnlyComponent(true, this.dueDays);
		readOnlyComponent(true, this.suspendProfit);

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
		this.productCode.setValue("");
		this.productCode.setDescription("");
		this.bucketID.setValue("");
		this.bucketID.setDescription("");
		this.dueDays.setText("");
		this.suspendProfit.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final NPABucketConfiguration aNPABucketConfiguration = new NPABucketConfiguration();
		BeanUtils.copyProperties(this.nPABucketConfiguration, aNPABucketConfiguration);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aNPABucketConfiguration);

		isNew = aNPABucketConfiguration.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aNPABucketConfiguration.getRecordType())) {
				aNPABucketConfiguration.setVersion(aNPABucketConfiguration.getVersion() + 1);
				if (isNew) {
					aNPABucketConfiguration.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aNPABucketConfiguration.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aNPABucketConfiguration.setNewRecord(true);
				}
			}
		} else {
			aNPABucketConfiguration.setVersion(aNPABucketConfiguration.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aNPABucketConfiguration, tranType)) {
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
	private boolean doProcess(NPABucketConfiguration aNPABucketConfiguration, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aNPABucketConfiguration.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aNPABucketConfiguration.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aNPABucketConfiguration.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aNPABucketConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aNPABucketConfiguration.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aNPABucketConfiguration);
				}

				if (isNotesMandatory(taskId, aNPABucketConfiguration)) {
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

			aNPABucketConfiguration.setTaskId(taskId);
			aNPABucketConfiguration.setNextTaskId(nextTaskId);
			aNPABucketConfiguration.setRoleCode(getRole());
			aNPABucketConfiguration.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aNPABucketConfiguration, tranType);
			String operationRefs = getServiceOperations(taskId, aNPABucketConfiguration);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aNPABucketConfiguration, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aNPABucketConfiguration, tranType);
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
		NPABucketConfiguration aNPABucketConfiguration = (NPABucketConfiguration) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = nPABucketConfigurationService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = nPABucketConfigurationService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = nPABucketConfigurationService.doApprove(auditHeader);

						if (aNPABucketConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = nPABucketConfigurationService.doReject(auditHeader);
						if (aNPABucketConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_NPABucketConfigurationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_NPABucketConfigurationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.nPABucketConfiguration), true);
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

	private AuditHeader getAuditHeader(NPABucketConfiguration aNPABucketConfiguration, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aNPABucketConfiguration.getBefImage(),
				aNPABucketConfiguration);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aNPABucketConfiguration.getUserDetails(),
				getOverideMap());
	}

	public void setNPABucketConfigurationService(NPABucketConfigurationService nPABucketConfigurationService) {
		this.nPABucketConfigurationService = nPABucketConfigurationService;
	}

}
