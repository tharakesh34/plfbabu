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
 * * FileName : CustomerTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.customertype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/CustomerType/customerTypeDialog.zul file.
 */
public class CustomerTypeDialogCtrl extends GFCBaseCtrl<CustomerType> {
	private static final long serialVersionUID = 8514232721532245700L;
	private static final Logger logger = LogManager.getLogger(CustomerTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerTypeDialog; // autoWired

	protected Textbox custTypeCode; // autoWired
	protected Textbox custTypeDesc; // autoWired
	protected Combobox custTypeCtg; // autoWired
	protected Checkbox custTypeIsActive; // autoWired

	// not autoWired variables
	private CustomerType customerType; // over handed per parameter
	private transient CustomerTypeListCtrl customerTypeListCtrl; // over handed per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient CustomerTypeService customerTypeService;
	private List<ValueLabel> categoryTypeList = PennantAppUtil.getcustCtgCodeList();

	/**
	 * default constructor.<br>
	 */
	public CustomerTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerType object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CustomerTypeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerTypeDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("customerType")) {
			this.customerType = (CustomerType) arguments.get("customerType");
			CustomerType befImage = new CustomerType();
			BeanUtils.copyProperties(this.customerType, befImage);
			this.customerType.setBefImage(befImage);

			setCustomerType(this.customerType);
		} else {
			setCustomerType(null);
		}

		doLoadWorkFlow(this.customerType.isWorkflow(), this.customerType.getWorkflowId(),
				this.customerType.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerTypeDialog");
		} else {
			getUserWorkspace().allocateAuthorities(super.pageRightName);
		}

		// READ OVERHANDED parameters !
		// we get the customerTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerType here.
		if (arguments.containsKey("customerTypeListCtrl")) {
			setCustomerTypeListCtrl((CustomerTypeListCtrl) arguments.get("customerTypeListCtrl"));
		} else {
			setCustomerTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerType());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.custTypeCode.setMaxlength(8);
		this.custTypeDesc.setMaxlength(100);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerTypeDialog);
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
	 * @param event An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.customerType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerType CustomerType
	 */
	public void doWriteBeanToComponents(CustomerType aCustomerType) {
		logger.debug("Entering");
		this.custTypeCode.setValue(aCustomerType.getCustTypeCode());

		Comboitem comboitem;
		for (int i = 0; i < categoryTypeList.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(categoryTypeList.get(i).getValue());
			comboitem.setLabel(categoryTypeList.get(i).getLabel());
			custTypeCtg.appendChild(comboitem);
			if (StringUtils.trimToEmpty(aCustomerType.getCustTypeCtg()).equals(categoryTypeList.get(i).getValue())) {
				custTypeCtg.setSelectedItem(comboitem);
			}
		}
		this.custTypeDesc.setValue(aCustomerType.getCustTypeDesc());
		this.custTypeIsActive.setChecked(aCustomerType.isCustTypeIsActive());
		this.recordStatus.setValue(aCustomerType.getRecordStatus());

		if (aCustomerType.isNewRecord()
				|| StringUtils.equals(aCustomerType.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			this.custTypeIsActive.setChecked(true);
			this.custTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerType
	 */
	public void doWriteComponentsToBean(CustomerType aCustomerType) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerType.setCustTypeCode(this.custTypeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerType.setCustTypeCtg(this.custTypeCtg.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.custTypeCtg.isDisabled() && this.custTypeCtg.getSelectedIndex() < 0) {
				throw new WrongValueException(custTypeCtg, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerTypeDialog_CustTypeCtg.value") }));
			}
			aCustomerType.setCustTypeCtg(this.custTypeCtg.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerType.setCustTypeDesc(this.custTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerType.setCustTypeIsActive(this.custTypeIsActive.isChecked());
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

		aCustomerType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerType
	 */
	public void doShowDialog(CustomerType aCustomerType) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aCustomerType.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.custTypeDesc.focus();
				if (StringUtils.isNotBlank(aCustomerType.getRecordType())) {
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
			doWriteBeanToComponents(aCustomerType);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);
		if (!this.custTypeCode.isReadonly()) {
			this.custTypeCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerTypeDialog_CustTypeCode.value"),
							PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
		}
		if (!this.custTypeDesc.isReadonly()) {
			this.custTypeDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerTypeDialog_CustTypeDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.custTypeCtg.isReadonly()) {
			this.custTypeCtg.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerTypeDialog_CustTypeCtg.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custTypeCode.setConstraint("");
		this.custTypeDesc.setConstraint("");
		this.custTypeCtg.setConstraint("");
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
		logger.debug("Enterring");
		this.custTypeCode.setErrorMessage("");
		this.custTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCustomerTypeListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CustomerType aCustomerType = new CustomerType();
		BeanUtils.copyProperties(getCustomerType(), aCustomerType);

		doDelete(aCustomerType.getCustTypeCode(), aCustomerType);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCustomerType().isNewRecord()) {
			this.custTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.custTypeDesc.setReadonly(isReadOnly("CustomerTypeDialog_custTypeDesc"));
		this.custTypeCtg.setDisabled(isReadOnly("CustomerTypeDialog_custTypeCtg"));
		this.custTypeIsActive.setDisabled(isReadOnly("CustomerTypeDialog_custTypeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerType.isNewRecord()) {
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

		this.custTypeCode.setReadonly(true);
		this.custTypeCtg.setReadonly(true);
		this.custTypeDesc.setReadonly(true);
		this.custTypeIsActive.setDisabled(true);

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
		this.custTypeCode.setValue("");
		this.custTypeCtg.setValue("");
		this.custTypeDesc.setValue("");
		this.custTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerType aCustomerType = new CustomerType();
		BeanUtils.copyProperties(getCustomerType(), aCustomerType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerType object with the components data
		doWriteComponentsToBean(aCustomerType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCustomerType.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerType.getRecordType())) {
				aCustomerType.setVersion(aCustomerType.getVersion() + 1);
				if (isNew) {
					aCustomerType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerType.setNewRecord(true);
				}
			}
		} else {
			aCustomerType.setVersion(aCustomerType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCustomerType, tranType)) {
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
	 * @param aCustomerType (CustomerType)
	 * 
	 * @param tranType      (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(CustomerType aCustomerType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerType);
				}

				if (isNotesMandatory(taskId, aCustomerType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (!StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();

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

			aCustomerType.setTaskId(taskId);
			aCustomerType.setNextTaskId(nextTaskId);
			aCustomerType.setRoleCode(getRole());
			aCustomerType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerType, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerType aCustomerType = (CustomerType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getCustomerTypeService().delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = getCustomerTypeService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getCustomerTypeService().doApprove(auditHeader);

					if (aCustomerType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getCustomerTypeService().doReject(auditHeader);
					if (aCustomerType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CustomerTypeDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_CustomerTypeDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.customerType), true);
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

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerType (CustomerType)
	 * @param tranType      (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(CustomerType aCustomerType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerType.getBefImage(), aCustomerType);
		return new AuditHeader(String.valueOf(aCustomerType.getId()), null, null, null, auditDetail,
				aCustomerType.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.customerType);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.customerType.getCustTypeCode());
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

	public CustomerType getCustomerType() {
		return this.customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	public void setCustomerTypeService(CustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}

	public CustomerTypeService getCustomerTypeService() {
		return this.customerTypeService;
	}

	public void setCustomerTypeListCtrl(CustomerTypeListCtrl customerTypeListCtrl) {
		this.customerTypeListCtrl = customerTypeListCtrl;
	}

	public CustomerTypeListCtrl getCustomerTypeListCtrl() {
		return this.customerTypeListCtrl;
	}

}
