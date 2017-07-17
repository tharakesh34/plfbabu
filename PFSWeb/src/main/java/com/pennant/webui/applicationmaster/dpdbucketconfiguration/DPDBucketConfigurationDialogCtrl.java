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
 * FileName    		:  DPDBucketConfigurationDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.dpdbucketconfiguration;

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
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.DPDBucketConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/applicationmaster/DPDBucketConfiguration/dPDBucketConfigurationDialog.zul file. <br>
 */
public class DPDBucketConfigurationDialogCtrl extends GFCBaseCtrl<DPDBucketConfiguration> {

	private static final long							serialVersionUID	= 1L;
	private static final Logger							logger				= Logger.getLogger(DPDBucketConfigurationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window									window_DPDBucketConfigurationDialog;
	protected ExtendedCombobox							productCode;
	protected ExtendedCombobox							bucketID;
	protected Intbox									dueDays;
	protected Checkbox									suspendProfit;
	private DPDBucketConfiguration						dPDBucketConfiguration;														// overhanded per param

	private transient DPDBucketConfigurationListCtrl	dPDBucketConfigurationListCtrl;												// overhanded per param
	private transient DPDBucketConfigurationService		dPDBucketConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public DPDBucketConfigurationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DPDBucketConfigurationDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.dPDBucketConfiguration.getConfigID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_DPDBucketConfigurationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DPDBucketConfigurationDialog);

		try {
			// Get the required arguments.
			this.dPDBucketConfiguration = (DPDBucketConfiguration) arguments.get("dpdbucketconfiguration");
			this.dPDBucketConfigurationListCtrl = (DPDBucketConfigurationListCtrl) arguments
					.get("dpdbucketconfigurationListCtrl");

			if (this.dPDBucketConfiguration == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			DPDBucketConfiguration dPDBucketConfiguration = new DPDBucketConfiguration();
			BeanUtils.copyProperties(this.dPDBucketConfiguration, dPDBucketConfiguration);
			this.dPDBucketConfiguration.setBefImage(dPDBucketConfiguration);

			// Render the page and display the data.
			doLoadWorkFlow(this.dPDBucketConfiguration.isWorkflow(), this.dPDBucketConfiguration.getWorkflowId(),
					this.dPDBucketConfiguration.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.dPDBucketConfiguration);
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

		this.bucketID.setModuleName("DPDBucket");
		this.bucketID.setMandatoryStyle(true);
		this.bucketID.setValueColumn("BucketCode");
		this.bucketID.setDescColumn("BucketDesc");
		this.bucketID.setValidateColumns(new String[] { "BucketCode" });
		
		this.dueDays.setMaxlength(5);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DPDBucketConfigurationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DPDBucketConfigurationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DPDBucketConfigurationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DPDBucketConfigurationDialog_btnSave"));
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
		doShowNotes(this.dPDBucketConfiguration);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		dPDBucketConfigurationListCtrl.search();
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.dPDBucketConfiguration.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param dPDBucketConfiguration
	 * 
	 */
	public void doWriteBeanToComponents(DPDBucketConfiguration aDPDBucketConfiguration) {
		logger.debug(Literal.ENTERING);

		this.productCode.setValue(aDPDBucketConfiguration.getProductCode());
		if (aDPDBucketConfiguration.getBucketID() != Long.MIN_VALUE && aDPDBucketConfiguration.getBucketID() != 0) {
			this.bucketID.setValue(String.valueOf(aDPDBucketConfiguration.getBucketID()));
		}
		this.dueDays.setValue(aDPDBucketConfiguration.getDueDays());
		this.suspendProfit.setChecked(aDPDBucketConfiguration.isSuspendProfit());
		
		this.bucketID.setObject(new DPDBucket(aDPDBucketConfiguration.getBucketID()));
		this.bucketID.setValue(aDPDBucketConfiguration.getBucketCode(),aDPDBucketConfiguration.getBucketIDName());
		
		if (aDPDBucketConfiguration.isNewRecord()) {
			this.productCode.setDescription("");
		} else {
			this.productCode.setDescription(aDPDBucketConfiguration.getProductCodeName());
		}
		this.recordStatus.setValue(aDPDBucketConfiguration.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDPDBucketConfiguration
	 */
	public void doWriteComponentsToBean(DPDBucketConfiguration aDPDBucketConfiguration) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Product Code
		try {
			aDPDBucketConfiguration.setProductCode(this.productCode.getValidatedValue());
			aDPDBucketConfiguration.setProductCodeName(this.productCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bucket ID
		try {
			this.bucketID.getValidatedValue();
			DPDBucket dPDBucket = (DPDBucket) this.bucketID.getObject();
			aDPDBucketConfiguration.setBucketID(dPDBucket.getBucketID());
			aDPDBucketConfiguration.setBucketCode(dPDBucket.getBucketCode());
			aDPDBucketConfiguration.setBucketIDName(dPDBucket.getBucketDesc());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Due Days
		try {
			aDPDBucketConfiguration.setDueDays(this.dueDays.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Suspend Profit
		try {
			aDPDBucketConfiguration.setSuspendProfit(this.suspendProfit.isChecked());
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
	 * @param dPDBucketConfiguration
	 *            The entity that need to be render.
	 */
	public void doShowDialog(DPDBucketConfiguration dPDBucketConfiguration) {
		logger.debug(Literal.LEAVING);

		if (dPDBucketConfiguration.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.productCode.focus();
		} else {
			this.bucketID.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(dPDBucketConfiguration.getRecordType())) {
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

		doWriteBeanToComponents(dPDBucketConfiguration);
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
					.getLabel("label_DPDBucketConfigurationDialog_ProductCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.bucketID.isReadonly()) {
			this.bucketID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_DPDBucketConfigurationDialog_BucketID.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.dueDays.isReadonly()) {
			this.dueDays.setConstraint(new PTNumberValidator(Labels
					.getLabel("label_DPDBucketConfigurationDialog_DueDays.value"), true, false, 0));
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
			DPDBucket details = (DPDBucket) dataObject;
			if (details != null) {
				this.bucketID.setAttribute("BucketID", details.getBucketID());
			}
		}
		logger.debug("Leaving" + event.toString());
	}*/
	
	/**
	 * Deletes a DPDBucketConfiguration object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug(Literal.LEAVING);

		final DPDBucketConfiguration aDPDBucketConfiguration = new DPDBucketConfiguration();
		BeanUtils.copyProperties(this.dPDBucketConfiguration, aDPDBucketConfiguration);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_DPDBucketConfigurationDialog_ProductCode.value") + " : "
				+ aDPDBucketConfiguration.getProductCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aDPDBucketConfiguration.getRecordType()).equals("")) {
				aDPDBucketConfiguration.setVersion(aDPDBucketConfiguration.getVersion() + 1);
				aDPDBucketConfiguration.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDPDBucketConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aDPDBucketConfiguration.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(),
							aDPDBucketConfiguration.getNextTaskId(), aDPDBucketConfiguration);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aDPDBucketConfiguration, tranType)) {
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

		if (this.dPDBucketConfiguration.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.productCode);
			readOnlyComponent(false, this.bucketID);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.productCode);
			readOnlyComponent(true, this.bucketID);

		}

		readOnlyComponent(isReadOnly("DPDBucketConfigurationDialog_DueDays"), this.dueDays);
		readOnlyComponent(isReadOnly("DPDBucketConfigurationDialog_SuspendProfit"), this.suspendProfit);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.dPDBucketConfiguration.isNewRecord()) {
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
		final DPDBucketConfiguration aDPDBucketConfiguration = new DPDBucketConfiguration();
		BeanUtils.copyProperties(this.dPDBucketConfiguration, aDPDBucketConfiguration);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aDPDBucketConfiguration);

		isNew = aDPDBucketConfiguration.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDPDBucketConfiguration.getRecordType())) {
				aDPDBucketConfiguration.setVersion(aDPDBucketConfiguration.getVersion() + 1);
				if (isNew) {
					aDPDBucketConfiguration.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDPDBucketConfiguration.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDPDBucketConfiguration.setNewRecord(true);
				}
			}
		} else {
			aDPDBucketConfiguration.setVersion(aDPDBucketConfiguration.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aDPDBucketConfiguration, tranType)) {
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
	private boolean doProcess(DPDBucketConfiguration aDPDBucketConfiguration, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDPDBucketConfiguration.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aDPDBucketConfiguration.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDPDBucketConfiguration.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDPDBucketConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDPDBucketConfiguration.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDPDBucketConfiguration);
				}

				if (isNotesMandatory(taskId, aDPDBucketConfiguration)) {
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

			aDPDBucketConfiguration.setTaskId(taskId);
			aDPDBucketConfiguration.setNextTaskId(nextTaskId);
			aDPDBucketConfiguration.setRoleCode(getRole());
			aDPDBucketConfiguration.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDPDBucketConfiguration, tranType);
			String operationRefs = getServiceOperations(taskId, aDPDBucketConfiguration);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDPDBucketConfiguration, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDPDBucketConfiguration, tranType);
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
		DPDBucketConfiguration aDPDBucketConfiguration = (DPDBucketConfiguration) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = dPDBucketConfigurationService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = dPDBucketConfigurationService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = dPDBucketConfigurationService.doApprove(auditHeader);

						if (aDPDBucketConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = dPDBucketConfigurationService.doReject(auditHeader);
						if (aDPDBucketConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DPDBucketConfigurationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_DPDBucketConfigurationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.dPDBucketConfiguration), true);
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

	private AuditHeader getAuditHeader(DPDBucketConfiguration aDPDBucketConfiguration, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDPDBucketConfiguration.getBefImage(),
				aDPDBucketConfiguration);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aDPDBucketConfiguration.getUserDetails(),
				getOverideMap());
	}

	public void setDPDBucketConfigurationService(DPDBucketConfigurationService dPDBucketConfigurationService) {
		this.dPDBucketConfigurationService = dPDBucketConfigurationService;
	}

}
