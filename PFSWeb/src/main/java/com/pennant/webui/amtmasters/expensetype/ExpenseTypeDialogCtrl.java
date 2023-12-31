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
 * * FileName : ExpenseTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-09-2011 * *
 * Modified Date : 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.amtmasters.expensetype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.amtmasters.ExpenseTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMaster/ExpenseType/expenseTypeDialog.zul file.
 */
public class ExpenseTypeDialogCtrl extends GFCBaseCtrl<ExpenseType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ExpenseTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExpenseTypeDialog; // autowired
	protected Uppercasebox expenseTypeCode; // autowired
	protected Textbox expenseTypeDesc; // autowired
	protected Checkbox amortReq; // autowired
	protected Checkbox taxApplicable; // autowired
	protected Checkbox active; // autowired
	protected Row row_AmzTax;

	// not auto wired vars
	private ExpenseType expenseType; // overhanded per param
	private transient ExpenseTypeListCtrl expenseTypeListCtrl; // overhanded per

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient ExpenseTypeService expenseTypeService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();

	/**
	 * default constructor.<br>
	 */
	public ExpenseTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExpenseTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected ExpenseType object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ExpenseTypeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ExpenseTypeDialog);

		try {
			/* set components visible dependent of the users rights */

			// READ OVERHANDED params !
			if (arguments.containsKey("expenseType")) {
				this.expenseType = (ExpenseType) arguments.get("expenseType");
				ExpenseType befImage = new ExpenseType();
				BeanUtils.copyProperties(this.expenseType, befImage);
				this.expenseType.setBefImage(befImage);

				setExpenseType(this.expenseType);
			} else {
				setExpenseType(null);
			}

			doLoadWorkFlow(this.expenseType.isWorkflow(), this.expenseType.getWorkflowId(),
					this.expenseType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ExpenseTypeDialog");
			}

			// READ OVERHANDED params !
			// we get the expenseTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete expenseType here.
			if (arguments.containsKey("expenseTypeListCtrl")) {
				setExpenseTypeListCtrl((ExpenseTypeListCtrl) arguments.get("expenseTypeListCtrl"));
			} else {
				setExpenseTypeListCtrl(null);
			}
			doCheckRights();
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getExpenseType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ExpenseTypeDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.expenseTypeCode.setMaxlength(8);
		this.expenseTypeDesc.setMaxlength(100);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExpenseTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExpenseTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ExpenseTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExpenseTypeDialog_btnSave"));
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
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_ExpenseTypeDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
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
		doWriteBeanToComponents(this.expenseType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aExpenseType ExpenseType
	 */
	public void doWriteBeanToComponents(ExpenseType aExpenseType) {
		logger.debug("Entering");

		if (aExpenseType.isNewRecord()) {
			this.active.setChecked(true);
			this.amortReq.setChecked(true);
		} else {
			this.active.setChecked(aExpenseType.isActive());
			this.amortReq.setChecked(aExpenseType.isAmortReq());
		}
		this.expenseTypeCode.setValue(aExpenseType.getExpenseTypeCode());
		this.expenseTypeDesc.setValue(aExpenseType.getExpenseTypeDesc());
		this.taxApplicable.setChecked(aExpenseType.isTaxApplicable());
		this.recordStatus.setValue(aExpenseType.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aExpenseType
	 */
	public void doWriteComponentsToBean(ExpenseType aExpenseType) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Expense Code
		try {
			aExpenseType.setExpenseTypeCode(this.expenseTypeCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Expense Description
		try {
			aExpenseType.setExpenseTypeDesc(this.expenseTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Amortization Flag
		try {
			aExpenseType.setAmortReq(this.amortReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// TaxApplicable Flag
		try {
			aExpenseType.setTaxApplicable(this.taxApplicable.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Expense Is Active
		try {
			aExpenseType.setActive(this.active.isChecked());
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

		aExpenseType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aExpenseType
	 */
	public void doShowDialog(ExpenseType aExpenseType) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aExpenseType.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.expenseTypeCode.focus();
		} else {
			this.expenseTypeDesc.focus();
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
			doWriteBeanToComponents(aExpenseType);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ExpenseTypeDialog.onClose();
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

		if (!this.expenseTypeCode.isReadonly()) {
			this.expenseTypeCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ExpenseTypeDialog_ExpenseTypeCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.expenseTypeDesc.isReadonly()) {
			this.expenseTypeDesc.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ExpenseTypeDialog_ExpenseTypeDesc.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.expenseTypeCode.setConstraint("");
		this.expenseTypeDesc.setConstraint("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ExpenseType aExpenseType = new ExpenseType();
		BeanUtils.copyProperties(getExpenseType(), aExpenseType);

		doDelete(Labels.getLabel("label_ExpenceTypeCode") + " : " + aExpenseType.getExpenseTypeCode(), aExpenseType);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getExpenseType().isNewRecord()) {
			this.expenseTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.expenseTypeCode.setReadonly(true);// isReadOnly("ExpenseTypeDialog_expenceTypeName")
		}

		this.expenseTypeDesc.setReadonly(isReadOnly("ExpenseTypeDialog_Description"));
		this.amortReq.setDisabled(isReadOnly("ExpenseTypeDialog_AmortizationReq"));
		this.taxApplicable.setDisabled(isReadOnly("ExpenseTypeDialog_TaxApplicable"));
		this.active.setDisabled(isReadOnly("ExpenseTypeDialog_Active"));

		this.row_AmzTax.setVisible(!isReadOnly("ExpenseTypeDialog_RowAmortTaxApplicable"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.expenseType.isNewRecord()) {
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
		this.expenseTypeCode.setReadonly(true);
		this.expenseTypeDesc.setReadonly(true);
		this.active.setDisabled(true);
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

		this.expenseTypeCode.setValue("");
		this.expenseTypeDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final ExpenseType aExpenseType = new ExpenseType();
		BeanUtils.copyProperties(getExpenseType(), aExpenseType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ExpenseType object with the components data
		doWriteComponentsToBean(aExpenseType);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aExpenseType.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aExpenseType.getRecordType())) {
				aExpenseType.setVersion(aExpenseType.getVersion() + 1);
				if (isNew) {
					aExpenseType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExpenseType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aExpenseType.setNewRecord(true);
				}
			}
		} else {
			aExpenseType.setVersion(aExpenseType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aExpenseType, tranType)) {
				doWriteBeanToComponents(aExpenseType);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	protected boolean doProcess(ExpenseType aExpenseType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aExpenseType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aExpenseType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aExpenseType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aExpenseType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aExpenseType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aExpenseType);
				}

				if (isNotesMandatory(taskId, aExpenseType)) {
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

			aExpenseType.setTaskId(taskId);
			aExpenseType.setNextTaskId(nextTaskId);
			aExpenseType.setRoleCode(getRole());
			aExpenseType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aExpenseType, tranType);

			String operationRefs = getServiceOperations(taskId, aExpenseType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aExpenseType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aExpenseType, tranType);
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

		ExpenseType aExpenseType = (ExpenseType) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getExpenseTypeService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getExpenseTypeService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getExpenseTypeService().doApprove(auditHeader);

					if (aExpenseType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getExpenseTypeService().doReject(auditHeader);
					if (aExpenseType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {

					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ExpenseTypeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ExpenseTypeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.expenseType), true);
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

	public ExpenseType getExpenseType() {
		return this.expenseType;
	}

	public void setExpenseType(ExpenseType expenseType) {
		this.expenseType = expenseType;
	}

	public void setExpenseTypeService(ExpenseTypeService expenseTypeService) {
		this.expenseTypeService = expenseTypeService;
	}

	public ExpenseTypeService getExpenseTypeService() {
		return this.expenseTypeService;
	}

	public void setExpenseTypeListCtrl(ExpenseTypeListCtrl expenseTypeListCtrl) {
		this.expenseTypeListCtrl = expenseTypeListCtrl;
	}

	public ExpenseTypeListCtrl getExpenseTypeListCtrl() {
		return this.expenseTypeListCtrl;
	}

	private AuditHeader getAuditHeader(ExpenseType aExpenseType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aExpenseType.getBefImage(), aExpenseType);
		return new AuditHeader(String.valueOf(aExpenseType.getExpenseTypeId()), null, null, null, auditDetail,
				aExpenseType.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.expenseType);

	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.expenseTypeCode.setErrorMessage("");
		this.expenseTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getExpenseTypeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.expenseType.getExpenseTypeId());
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}
}
