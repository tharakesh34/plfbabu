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
 *//*
package com.pennant.webui.finance.financemain;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

*//**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/CovenantTypeDialog.zul file. <br>
 * ************************************************************<br>
 *//*
public class CovenantTypeDialogCtrl extends GFCBaseCtrl<FinCovenantType> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = Logger.getLogger(CovenantTypeDialogCtrl.class);

	protected Window window_CovenantTypeDialog;
	protected ExtendedCombobox covenantType;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox mandRole;
	protected Textbox description;
	protected Checkbox alwWaiver;
	protected Checkbox alwPostpone;
	protected Row  row_Postpone;

	protected Label label_postponeDays;
	protected Hbox hbox_postponeDays;
	protected Label label_FinCovenantTypeDialog_MandRole;
	protected Datebox	receivableDate;
	protected Space space_receivableDate;
	
	protected  Label label_FinCovenantTypeDialog_RecvbleDate;
	
	private FinCovenantType finCovenantType;

	private transient CovenantTypeListCtrl covenantListCtrl;

	private transient boolean validationOn;
	
	private transient FinCovenantTypeService finCovenantTypeService;


	*//**
	 * default constructor.<br>
	 *//*
	public CovenantTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinCovenantTypeDialog";
	}

	*//**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 *//*
	public void onCreate$window_CovenantTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CovenantTypeDialog);

		try {
			// Get the required arguments.
			this.finCovenantType = (FinCovenantType) arguments.get("fincovenant");
			this.covenantListCtrl = (CovenantTypeListCtrl) arguments.get("covenantListCtrl");

			if (this.finCovenantType == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FinCovenantType finCovenantType = new FinCovenantType();
			BeanUtils.copyProperties(this.finCovenantType, finCovenantType);
			this.finCovenantType.setBefImage(finCovenantType);

			// Render the page and display the data.
			doLoadWorkFlow(this.finCovenantType.isWorkflow(), this.finCovenantType.getWorkflowId(), this.finCovenantType.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} 

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.finCovenantType);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	*//**
	 * Set the properties of the fields, like maxLength.<br>
	 *//*
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceManagement");
		this.finReference.setValueColumn("FinReference");

		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.covenantType.setMaxlength(50);
		this.covenantType.setTextBoxWidth(151);
		this.covenantType.setMandatoryStyle(true);
		this.covenantType.setModuleName("DocumentType");
		this.covenantType.setValueColumn("DocTypeCode");
		this.covenantType.setDescColumn("DocTypeDesc");
		this.covenantType.setValidateColumns(new String[] { "DocTypeCode" });
		
		this.mandRole.setMaxlength(100);
		this.mandRole.setTextBoxWidth(151);
		this.mandRole.setMandatoryStyle(true);
		this.mandRole.setModuleName("SecurityRoleEnq");
		this.mandRole.setValueColumn("RoleCd");
		this.mandRole.setDescColumn("RoleDesc");
		this.mandRole.setValidateColumns(new String[] { "RoleCd" });
		
		
		this.receivableDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		setStatusDetails();
		
		logger.debug("Leaving");
	}

	*//**
	 * Set Visible for components by checking if there's a right for it.
	 *//*
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeDialog_btnSave"));
		
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	*//**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 *//*
	public void onClick$btnSave(Event event) {
		doSave();
	}

	*//**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 *//*
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	*//**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 *//*
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	*//**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 *//*
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	*//**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 *//*
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	*//**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 *//*
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	*//**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 *//*
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.finCovenantType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	*//**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param finCovenantType
	 * 
	 *//*
	public void doWriteBeanToComponents(FinCovenantType finCovenantType) {
		logger.debug("Entering");
		this.finReference.setValue(finCovenantType.getFinReference());
		this.covenantType.setValue(finCovenantType.getCovenantType());
		this.covenantType.setDescription(finCovenantType.getCovenantTypeDesc());
		this.mandRole.setValue(finCovenantType.getMandRole());
		this.mandRole.setDescription(finCovenantType.getMandRoleDesc());
		this.description.setValue(finCovenantType.getDescription());
		this.alwWaiver.setChecked(finCovenantType.isAlwWaiver());
		this.alwPostpone.setChecked(finCovenantType.isAlwPostpone());
		if (finCovenantType.getReceivableDate() != null) {
			this.receivableDate.setValue(finCovenantType.getReceivableDate());
		}

		this.recordStatus.setValue(finCovenantType.getRecordStatus());

		doSetWaiverProp();
		
		doSetPostponeProp();

		logger.debug("Leaving");
	}
	
	private void doSetWaiverProp() {
		if (this.alwWaiver.isChecked()) {
			this.alwPostpone.setChecked(false);
			this.row_Postpone.setVisible(false);
			this.mandRole.setVisible(false);
			this.label_FinCovenantTypeDialog_MandRole.setVisible(false);
		} else{
			this.mandRole.setVisible(true);
			this.label_FinCovenantTypeDialog_MandRole.setVisible(true);
			this.row_Postpone.setVisible(true);
		}
	}

	public void onCheck$alwPostpone(Event event) throws Exception {
		doSetPostponeProp();
	}
	
	private void doSetPostponeProp() {
		if (this.alwPostpone.isChecked()) {
			this.mandRole.setVisible(false);
			this.label_FinCovenantTypeDialog_MandRole.setVisible(false);
			this.mandRole.setValue("", "");
			this.space_receivableDate.setSclass("mandatory");
			this.label_FinCovenantTypeDialog_RecvbleDate.setVisible(true);
			this.receivableDate.setVisible(true);
			
		} else {
			if(this.alwWaiver.isChecked()){
				this.mandRole.setVisible(false);
				this.label_FinCovenantTypeDialog_MandRole.setVisible(false);
			}else{				
				this.mandRole.setVisible(true);
				this.label_FinCovenantTypeDialog_MandRole.setVisible(true);
			}
			this.space_receivableDate.setSclass("");
			this.receivableDate.setErrorMessage("");
			this.receivableDate.setConstraint("");
			this.label_FinCovenantTypeDialog_RecvbleDate.setVisible(false);
			this.receivableDate.setVisible(false);
			this.space_receivableDate.setSclass("");
		}
	}

	*//**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinCovenantType
	 *//*
	public void doWriteComponentsToBean(FinCovenantType aFinCovenantType) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Finance Reference
		try {
			aFinCovenantType.setFinReference(this.finReference.getValue());
			this.finCovenantType.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinCovenantType.setCovenantTypeDesc(this.covenantType.getDescription());
			aFinCovenantType.setCovenantType(this.covenantType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.mandRole.isVisible()){
				aFinCovenantType.setMandRole(this.mandRole.getValidatedValue());
				aFinCovenantType.setMandRoleDesc(this.mandRole.getDescription());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinCovenantType.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinCovenantType.setAlwWaiver(this.alwWaiver.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinCovenantType.setAlwPostpone(this.alwPostpone.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinCovenantType.setReceivableDate(this.receivableDate.getValue());
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
		aFinCovenantType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");

		logger.debug("Leaving");
	}

	*//**
	 * Displays the dialog page.
	 * 
	 * @param finCovenantType
	 *            The entity that need to be render.
	 *//*
	public void doShowDialog(FinCovenantType finCovenantType) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (finCovenantType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.description.focus();
				if (StringUtils.isNotBlank(finCovenantType.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		
		// fill the components with the data
		doWriteBeanToComponents(finCovenantType);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	*//**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 *//*
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.finReference.isReadonly()){
			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCovenantTypeDialog_FinReference.value"),null,true, true));
		}
		
		if (!this.covenantType.isReadonly()) {
			this.covenantType.setConstraint(new PTStringValidator(Labels.getLabel("label_CovenantSearch_CovenantType.value"),null,true, true));
		}

		if (!this.mandRole.isReadonly() && this.mandRole.isVisible()) {
			this.mandRole.setConstraint(new PTStringValidator(Labels.getLabel("label_FinCovenantTypeDialog_MandRole.value"),null,true, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCovenantTypeDialog_Description.value"), null, false));
		}
		
		if (this.alwPostpone.isChecked() && !this.receivableDate.isDisabled()){
			this.receivableDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinCovenantTypeDialog_RecvbleDate.value"),true));
		}
		
		if(this.receivableDate.isVisible() &&  this.receivableDate.getValue()!=null){
		if (DateUtility.compare(this.receivableDate.getValue(), DateUtility.getAppDate()) == -1) {
			throw new WrongValueException(this.receivableDate,Labels.getLabel("DATE_PAST",
					new String[] {Labels.getLabel("label_FinCovenantTypeDialog_RecvbleDate.value") }));
		}
	}

		logger.debug("Leaving");
	}

	*//**
	 * Disables the Validation by setting empty constraints.
	 *//*
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.covenantType.setConstraint("");
		this.description.setConstraint("");
		this.mandRole.setConstraint("");
		this.receivableDate.setConstraint("");

		logger.debug("Leaving");
	}

	*//**
	 * Set Validations for LOV Fields
	 *//*
	private void doSetLOVValidation() {
	}

	*//**
	 * Remove Validations for LOV Fields
	 *//*
	private void doRemoveLOVValidation() {
	}

	*//**
	 * Clears validation error messages from all the fields of the dialog controller.
	 *//*
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.covenantType.setErrorMessage("");
		this.description.setErrorMessage("");
		this.mandRole.setErrorMessage("");
		this.receivableDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	
	*//**
	 * Deletes a FinCovenantType entity from database.<br>
	 * 
	 * @throws InterruptedException
	 *//*
	private void doDelete() {
		logger.debug("Entering");

		final FinCovenantType aFinCovenantType = new FinCovenantType();
		BeanUtils.copyProperties(getFinCovenantType(), aFinCovenantType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n"
				+ Labels.getLabel("FinCovenantType_CovenantType") + " : "
				+ aFinCovenantType.getCovenantType(); 
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinCovenantType.getRecordType())) {
				aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
				aFinCovenantType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinCovenantType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aFinCovenantType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinCovenantType.getNextTaskId(),
							aFinCovenantType);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aFinCovenantType, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	*//**
	 * Set the components for edit mode. <br>
	 *//*
	private void doEdit() {
		logger.debug("Entering");

		if (this.finCovenantType.isNewRecord()) {
			this.covenantType.setReadonly(isReadOnly("FinCovenantTypeDialog_covenantType"));
			this.space_receivableDate.setSclass("");
			this.label_FinCovenantTypeDialog_RecvbleDate.setVisible(false);
			this.receivableDate.setVisible(false);
			this.label_FinCovenantTypeDialog_MandRole.setVisible(false);
			this.mandRole.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.finReference.setReadonly(true);
			this.covenantType.setReadonly(true);
		}
		
		this.mandRole.setReadonly(isReadOnly("FinCovenantTypeDialog_mandRole"));
		this.description.setReadonly(isReadOnly("FinCovenantTypeDialog_description"));
		this.alwWaiver.setDisabled(isReadOnly("FinCovenantTypeDialog_alwWaiver"));
		this.alwPostpone.setDisabled(isReadOnly("FinCovenantTypeDialog_alwPostpone"));
		this.receivableDate.setDisabled(isReadOnly("FinCovenantTypeDialog_receivableDate-"));


		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finCovenantType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	*//**
	 * Set the components to ReadOnly. <br>
	 *//*
	public void doReadOnly() {
		logger.debug("Entering");
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

	*//**
	 * Clears the components values. <br>
	 *//*
	public void doClear() {
		logger.debug("Entering");

		this.covenantType.setValue("");
		this.description.setValue("");
		this.mandRole.setValue("");

		logger.debug("Leaving");
	}

	*//**
	 * Saves the components to table. <br>
	 * 
	 *//*
	public void doSave() {
		logger.debug("Entering");

		final FinCovenantType aFinCovenantType = new FinCovenantType();
		BeanUtils.copyProperties(this.finCovenantType, aFinCovenantType);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the FinCovenantType object with the components data
		doWriteComponentsToBean(aFinCovenantType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aFinCovenantType.isNew();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinCovenantType.getRecordType())) {
				aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
				if (isNew) {
					aFinCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinCovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinCovenantType.setNewRecord(true);
				}
			}
		} else {
			aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aFinCovenantType, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	*//**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinCovenantType
	 *            (FinCovenantType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 *//*
	private boolean doProcess(FinCovenantType aFinCovenantType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aFinCovenantType.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aFinCovenantType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinCovenantType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFinCovenantType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinCovenantType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinCovenantType);
				}

				if (isNotesMandatory(taskId, aFinCovenantType)) {
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

			aFinCovenantType.setTaskId(taskId);
			aFinCovenantType.setNextTaskId(nextTaskId);
			aFinCovenantType.setRoleCode(getRole());
			aFinCovenantType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinCovenantType, tranType);
			String operationRefs = getServiceOperations(taskId, aFinCovenantType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinCovenantType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinCovenantType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	*//**
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
	 *//*
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FinCovenantType aFinCovenantType = (FinCovenantType) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						aAuditHeader = finCovenantTypeService.delete(aAuditHeader);
						deleteNotes = true;
					} else {
						aAuditHeader = finCovenantTypeService.saveOrUpdate(aAuditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						aAuditHeader = finCovenantTypeService.doApprove(aAuditHeader);

						if (aFinCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						aAuditHeader = finCovenantTypeService.doReject(aAuditHeader);

						if (aFinCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						aAuditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CovenantTypeDialog, aAuditHeader);
						return processCompleted;
					}
				}

				aAuditHeader = ErrorControl.showErrorDetails(this.window_CovenantTypeDialog, aAuditHeader);
				retValue = aAuditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.finCovenantType), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					aAuditHeader.setOveride(true);
					aAuditHeader.setErrorMessage(null);
					aAuditHeader.setInfoMessage(null);
					aAuditHeader.setOverideMessage(null);
				}
			}

			setOverideMap(aAuditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	*//**
	 * Get Audit Header Details
	 * 
	 * @param aFinCovenantType
	 * @param tranType
	 * @return AuditHeader
	 *//*
	private AuditHeader getAuditHeader(FinCovenantType aFinCovenantType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinCovenantType.getBefImage(), aFinCovenantType);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinCovenantType.getUserDetails(),
				getOverideMap());
	}

	*//**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 *//*
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finCovenantType);
	}

	*//**
	 * Refresh the list page with the filters that are applied in list page.
	 *//*
	private void refreshList() {
		covenantListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finCovenantType.getFinReference());
	}


	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}
	
	public FinCovenantType getFinCovenantType() {
		return finCovenantType;
	}

	public void setFinCovenantType(FinCovenantType finCovenantType) {
		this.finCovenantType = finCovenantType;
	}
	
	public FinCovenantTypeService getFinCovenantTypeService() {
		return finCovenantTypeService;
	}

	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
	}
	

}
*/