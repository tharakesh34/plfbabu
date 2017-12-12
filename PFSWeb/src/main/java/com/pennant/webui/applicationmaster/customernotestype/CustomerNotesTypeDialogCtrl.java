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
 * FileName    		:  CustomerNotesTypeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.customernotestype;

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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.service.applicationmaster.CustomerNotesTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CustomerNotesType /customerNotesTypeDialog.zul
 * file.
 */
public class CustomerNotesTypeDialogCtrl extends GFCBaseCtrl<CustomerNotesType> {
	private static final long serialVersionUID = -7057719949392212414L;
	private static final Logger logger = Logger.getLogger(CustomerNotesTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerNotesTypeDialog; // autoWired

	protected Textbox custNotesTypeCode; // autoWired
	protected Textbox custNotesTypeDesc; // autoWired
	protected Checkbox custNotesTypeIsPerminent; // autoWired
	protected Checkbox custNotesTypeIsActive; // autoWired
	protected Textbox custNotesTypeArchiveFrq; // autoWired

	// not autoWired variables
	private CustomerNotesType customerNotesType; // overHanded per parameters
	private transient CustomerNotesTypeListCtrl customerNotesTypeListCtrl; // overHanded per parameters

	private transient boolean validationOn;
	
	protected Button btnSearchCustNotesTypeArchiveFrq; // autoWired
	protected Textbox lovDescCustNotesTypeArcFrqName;
	

	// ServiceDAOs / Domain Classes
	private transient CustomerNotesTypeService customerNotesTypeService;

	/**
	 * default constructor.<br>
	 */
	public CustomerNotesTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerNotesTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected CustomerNotesType object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerNotesTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerNotesTypeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("customerNotesType")) {
				this.customerNotesType = (CustomerNotesType) arguments.get("customerNotesType");
				CustomerNotesType befImage = new CustomerNotesType();
				BeanUtils.copyProperties(this.customerNotesType, befImage);
				this.customerNotesType.setBefImage(befImage);
				setCustomerNotesType(this.customerNotesType);
			} else {
				setCustomerNotesType(null);
			}

			doLoadWorkFlow(this.customerNotesType.isWorkflow(), this.customerNotesType.getWorkflowId(),
					this.customerNotesType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerNotesTypeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the customerNotesTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete customerNotesType here.
			if (arguments.containsKey("customerNotesTypeListCtrl")) {
				setCustomerNotesTypeListCtrl((CustomerNotesTypeListCtrl) arguments.get("customerNotesTypeListCtrl"));
			} else {
				setCustomerNotesTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCustomerNotesType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerNotesTypeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.custNotesTypeCode.setMaxlength(8);
		this.custNotesTypeDesc.setMaxlength(50);
		this.custNotesTypeArchiveFrq.setMaxlength(8);

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerNotesTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerNotesTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerNotesTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerNotesTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerNotesTypeDialog);
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
		doWriteBeanToComponents(this.customerNotesType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerNotesType
	 *            CustomerNotesType
	 */
	public void doWriteBeanToComponents(CustomerNotesType aCustomerNotesType) {
		logger.debug("Entering");
		this.custNotesTypeCode.setValue(aCustomerNotesType.getCustNotesTypeCode());
		this.custNotesTypeDesc.setValue(aCustomerNotesType.getCustNotesTypeDesc());
		this.custNotesTypeIsPerminent.setChecked(aCustomerNotesType.isCustNotesTypeIsPerminent());
		this.custNotesTypeIsActive.setChecked(aCustomerNotesType.isCustNotesTypeIsActive());
		this.custNotesTypeArchiveFrq.setValue(aCustomerNotesType.getCustNotesTypeArchiveFrq());

		if (aCustomerNotesType.isNewRecord()) {
			this.lovDescCustNotesTypeArcFrqName.setValue("");
		} else {
			this.lovDescCustNotesTypeArcFrqName.setValue(aCustomerNotesType.getCustNotesTypeArchiveFrq() + "-"
					+ aCustomerNotesType.getLovDescCustNotesTypeArcFrqName());
		}
		this.recordStatus.setValue(aCustomerNotesType.getRecordStatus());

		if (aCustomerNotesType.isNew()
				|| (aCustomerNotesType.getRecordType() != null ? aCustomerNotesType.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.custNotesTypeIsActive.setChecked(true);
			this.custNotesTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerNotesType
	 */
	public void doWriteComponentsToBean(CustomerNotesType aCustomerNotesType) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerNotesType.setCustNotesTypeCode(this.custNotesTypeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerNotesType.setCustNotesTypeDesc(this.custNotesTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerNotesType.setCustNotesTypeIsPerminent(this.custNotesTypeIsPerminent.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerNotesType.setCustNotesTypeIsActive(this.custNotesTypeIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerNotesType.setLovDescCustNotesTypeArcFrqName(this.lovDescCustNotesTypeArcFrqName.getValue());
			aCustomerNotesType.setCustNotesTypeArchiveFrq(this.custNotesTypeArchiveFrq.getValue());
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

		aCustomerNotesType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerNotesType
	 * @throws Exception
	 */
	public void doShowDialog(CustomerNotesType aCustomerNotesType) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aCustomerNotesType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custNotesTypeCode.focus();
		} else {
			this.custNotesTypeDesc.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aCustomerNotesType.getRecordType())) {
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
			doWriteBeanToComponents(aCustomerNotesType);
			
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerNotesTypeDialog.onClose();
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

		if (!this.custNotesTypeCode.isReadonly()) {
			this.custNotesTypeCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_CustomerNotesTypeDialog_CustNotesTypeCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.custNotesTypeDesc.isReadonly()) {
			this.custNotesTypeDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_CustomerNotesTypeDialog_CustNotesTypeDesc.value"),
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
		this.custNotesTypeCode.setConstraint("");
		this.custNotesTypeDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		this.lovDescCustNotesTypeArcFrqName.setConstraint(new PTStringValidator(Labels
				.getLabel("label_CustomerNotesTypeDialog_CustNotesTypeArchiveFrq.value"), null, true));
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		this.lovDescCustNotesTypeArcFrqName.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custNotesTypeCode.setErrorMessage("");
		this.custNotesTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a CustomerNotesType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerNotesType aCustomerNotesType = new CustomerNotesType();
		BeanUtils.copyProperties(getCustomerNotesType(), aCustomerNotesType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aCustomerNotesType.getCustNotesTypeCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerNotesType.getRecordType())) {
				aCustomerNotesType.setVersion(aCustomerNotesType.getVersion() + 1);
				aCustomerNotesType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCustomerNotesType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCustomerNotesType, tranType)) {
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

		if (getCustomerNotesType().isNewRecord()) {
			this.custNotesTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custNotesTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.custNotesTypeDesc.setReadonly(isReadOnly("CustomerNotesTypeDialog_custNotesTypeDesc"));
		this.custNotesTypeIsPerminent.setDisabled(isReadOnly("CustomerNotesTypeDialog_custNotesTypeIsPerminent"));
		this.custNotesTypeIsActive.setDisabled(isReadOnly("CustomerNotesTypeDialog_custNotesTypeIsActive"));
		this.btnSearchCustNotesTypeArchiveFrq
				.setDisabled(isReadOnly("CustomerNotesTypeDialog_custNotesTypeArchiveFrq"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerNotesType.isNewRecord()) {
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

		this.custNotesTypeCode.setReadonly(true);
		this.custNotesTypeDesc.setReadonly(true);
		this.custNotesTypeIsPerminent.setDisabled(true);
		this.custNotesTypeIsActive.setDisabled(true);
		this.btnSearchCustNotesTypeArchiveFrq.setDisabled(true);

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
		this.custNotesTypeCode.setValue("");
		this.custNotesTypeDesc.setValue("");
		this.custNotesTypeIsPerminent.setChecked(false);
		this.custNotesTypeIsActive.setChecked(false);
		this.custNotesTypeArchiveFrq.setValue("");
		this.lovDescCustNotesTypeArcFrqName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerNotesType aCustomerNotesType = new CustomerNotesType();
		BeanUtils.copyProperties(getCustomerNotesType(), aCustomerNotesType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerNotesType object with the components data
		doWriteComponentsToBean(aCustomerNotesType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCustomerNotesType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerNotesType.getRecordType())) {
				aCustomerNotesType.setVersion(aCustomerNotesType.getVersion() + 1);
				if (isNew) {
					aCustomerNotesType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerNotesType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerNotesType.setNewRecord(true);
				}
			}
		} else {
			aCustomerNotesType.setVersion(aCustomerNotesType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aCustomerNotesType, tranType)) {
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
	 * @param aCustomerNotesType
	 *            (CustomerNotesType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerNotesType aCustomerNotesType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerNotesType.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aCustomerNotesType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerNotesType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerNotesType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerNotesType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerNotesType);
				}

				if (isNotesMandatory(taskId, aCustomerNotesType)) {
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

			aCustomerNotesType.setTaskId(taskId);
			aCustomerNotesType.setNextTaskId(nextTaskId);
			aCustomerNotesType.setRoleCode(getRole());
			aCustomerNotesType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerNotesType, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerNotesType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerNotesType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerNotesType, tranType);
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
		CustomerNotesType aCustomerNotesType = (CustomerNotesType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerNotesTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerNotesTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerNotesTypeService().doApprove(auditHeader);

						if (aCustomerNotesType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerNotesTypeService().doReject(auditHeader);

						if (aCustomerNotesType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerNotesTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerNotesTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.customerNotesType), true);
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

	public void onClick$btnSearchCustNotesTypeArchiveFrq(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerNotesTypeDialog, "Frequency");
		if (dataObject instanceof String) {
			this.custNotesTypeArchiveFrq.setValue(dataObject.toString());
			this.lovDescCustNotesTypeArcFrqName.setValue("");
		} else {
			Frequency details = (Frequency) dataObject;
			if (details != null) {
				this.custNotesTypeArchiveFrq.setValue(details.getLovValue());
				this.lovDescCustNotesTypeArcFrqName.setValue(details.getLovValue() + "-" + details.getFrqDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerNotesType aCustomerNotesType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerNotesType.getBefImage(), aCustomerNotesType);
		return new AuditHeader(String.valueOf(aCustomerNotesType.getId()), null, null, null, auditDetail,
				aCustomerNotesType.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerNotesTypeDialog, auditHeader);
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
		doShowNotes(this.customerNotesType);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCustomerNotesTypeListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.customerNotesType.getCustNotesTypeCode());
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

	public CustomerNotesType getCustomerNotesType() {
		return this.customerNotesType;
	}

	public void setCustomerNotesType(CustomerNotesType customerNotesType) {
		this.customerNotesType = customerNotesType;
	}

	public void setCustomerNotesTypeService(CustomerNotesTypeService customerNotesTypeService) {
		this.customerNotesTypeService = customerNotesTypeService;
	}

	public CustomerNotesTypeService getCustomerNotesTypeService() {
		return this.customerNotesTypeService;
	}

	public void setCustomerNotesTypeListCtrl(CustomerNotesTypeListCtrl customerNotesTypeListCtrl) {
		this.customerNotesTypeListCtrl = customerNotesTypeListCtrl;
	}

	public CustomerNotesTypeListCtrl getCustomerNotesTypeListCtrl() {
		return this.customerNotesTypeListCtrl;
	}

}
