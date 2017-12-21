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
 * FileName    		:  IncomeTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.incometype;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.systemmasters.IncomeTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/IncomeType/incomeTypeDialog.zul file.
 */
public class IncomeTypeDialogCtrl extends GFCBaseCtrl<IncomeType> {
	private static final long serialVersionUID = -9144099736284703562L;
	private static final Logger logger = Logger.getLogger(IncomeTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_IncomeTypeDialog;

	protected Combobox incomeExpense;
	protected Combobox category;
	protected Decimalbox margin;
	
	protected Uppercasebox 	incomeTypeCode; 		
	protected Textbox 		incomeTypeDesc; 		
	protected Checkbox 		incomeTypeIsActive; 	

	// not autoWired variables
	private IncomeType incomeType; // over handed per parameter
	private transient IncomeTypeListCtrl incomeTypeListCtrl; // over handed per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient IncomeTypeService incomeTypeService;

	/**
	 * default constructor.<br>
	 */
	public IncomeTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "IncomeTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected IncomeType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_IncomeTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_IncomeTypeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("incomeType")) {
				this.incomeType = (IncomeType) arguments.get("incomeType");
				IncomeType befImage = new IncomeType();
				BeanUtils.copyProperties(this.incomeType, befImage);
				this.incomeType.setBefImage(befImage);

				setIncomeType(this.incomeType);
			} else {
				setIncomeType(null);
			}

			doLoadWorkFlow(this.incomeType.isWorkflow(),
					this.incomeType.getWorkflowId(),
					this.incomeType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"IncomeTypeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the incomeTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete incomeType here.
			if (arguments.containsKey("incomeTypeListCtrl")) {
				setIncomeTypeListCtrl((IncomeTypeListCtrl) arguments
						.get("incomeTypeListCtrl"));
			} else {
				setIncomeTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getIncomeType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_IncomeTypeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.incomeTypeCode.setMaxlength(8);
		this.incomeTypeDesc.setMaxlength(50);
		this.margin.setScale(2);
		this.margin.setFormat(PennantConstants.amountFormate2);
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IncomeTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IncomeTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IncomeTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IncomeTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_IncomeTypeDialog);
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
		doWriteBeanToComponents(this.incomeType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aIncomeType
	 *            IncomeType
	 */
	public void doWriteBeanToComponents(IncomeType aIncomeType) {
		logger.debug("Entering");
		fillComboBox(incomeExpense, aIncomeType.getIncomeExpense(), PennantStaticListUtil.getIncomeExpense(), "");
		fillComboBox(category, aIncomeType.getCategory(), PennantAppUtil.getIncomeExpenseCategory(), "");
		this.incomeTypeCode.setValue(aIncomeType.getIncomeTypeCode());
		this.incomeTypeDesc.setValue(aIncomeType.getIncomeTypeDesc());
		this.margin.setValue(PennantAppUtil.formateAmount(aIncomeType.getMargin(), 2));
		this.incomeTypeIsActive.setChecked(aIncomeType.isIncomeTypeIsActive());
		this.recordStatus.setValue(aIncomeType.getRecordStatus());
		
		if(aIncomeType.isNew() || (aIncomeType.getRecordType() != null ? aIncomeType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.incomeTypeIsActive.setChecked(true);
			this.incomeTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aIncomeType
	 */
	public void doWriteComponentsToBean(IncomeType aIncomeType) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.incomeExpense.getSelectedItem()==null || StringUtils.trimToEmpty(this.incomeExpense.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.incomeExpense,Labels.getLabel("STATIC_INVALID",new String[]{Labels.getLabel("label_IncomeTypeDialog_IncomeExpense.value")}));
			}
			
			aIncomeType.setIncomeExpense(this.incomeExpense.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.category.getSelectedItem()==null || StringUtils.trimToEmpty(this.category.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.category,Labels.getLabel("STATIC_INVALID",new String[]{Labels.getLabel("label_IncomeTypeDialog_Category.value")}));
			}
			aIncomeType.setCategory(this.category.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIncomeType.setIncomeTypeCode(this.incomeTypeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIncomeType.setIncomeTypeDesc(this.incomeTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIncomeType.setMargin(PennantAppUtil.unFormateAmount(this.margin.getValue(), 2));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIncomeType.setIncomeTypeIsActive(this.incomeTypeIsActive.isChecked());
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

		aIncomeType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aIncomeType
	 * @throws Exception
	 */
	public void doShowDialog(IncomeType aIncomeType)throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aIncomeType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.incomeExpense.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.incomeTypeDesc.focus();
				if (StringUtils.isNotBlank(aIncomeType.getRecordType())) {
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
			doWriteBeanToComponents(aIncomeType);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_IncomeTypeDialog.onClose();
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

		if (!this.incomeTypeCode.isReadonly()){
			this.incomeTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_IncomeTypeDialog_IncomeTypeCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.incomeTypeDesc.isReadonly()){
			this.incomeTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_IncomeTypeDialog_IncomeTypeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.margin.isReadonly()) {
			this.margin.setConstraint(new 	PTDecimalValidator(Labels.getLabel("label_IncomeTypeDialog_Margin.value"), 2,false,false,100));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.incomeTypeCode.setConstraint("");
		this.incomeTypeDesc.setConstraint("");
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
		logger.debug("Entering");
		this.incomeTypeCode.setErrorMessage("");
		this.incomeTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a IncomeType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final IncomeType aIncomeType = new IncomeType();
		BeanUtils.copyProperties(getIncomeType(), aIncomeType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_IncomeTypeDialog_IncomeExpense.value")+" : " +aIncomeType.getIncomeExpense() + "," +
				Labels.getLabel("label_IncomeTypeDialog_Category.value")+" : " +aIncomeType.getLovDescCategoryName() + "," +
				Labels.getLabel("label_IncomeTypeDialog_IncomeTypeCode.value")+" : " +aIncomeType.getIncomeTypeCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aIncomeType.getRecordType())) {
				aIncomeType.setVersion(aIncomeType.getVersion() + 1);
				aIncomeType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aIncomeType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aIncomeType, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
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

		if (getIncomeType().isNewRecord()) {
			this.incomeTypeCode.setReadonly(false);
			this.incomeExpense.setDisabled(false);
			this.category.setDisabled(false);
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.margin);
		} else {
			this.incomeTypeCode.setReadonly(true);
			this.incomeExpense.setDisabled(true);
			this.category.setDisabled(true);
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.margin);
		}
		
		/*readOnlyComponent(isReadOnly("IncomeTypeDialog_incomeExpense"), this.incomeExpense);
		readOnlyComponent(isReadOnly("IncomeTypeDialog_category"), this.category);
		readOnlyComponent(isReadOnly("IncomeTypeDialog_margin"), this.margin);*/
		
		this.incomeTypeDesc.setReadonly(isReadOnly("IncomeTypeDialog_incomeTypeDesc"));
		this.margin.setReadonly(isReadOnly("IncomeTypeDialog_incomeTypeDesc"));
		this.incomeTypeIsActive.setDisabled(isReadOnly("IncomeTypeDialog_incomeTypeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.incomeType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		readOnlyComponent(true, this.incomeExpense);
		readOnlyComponent(true, this.category);
		readOnlyComponent(true, this.margin);
		this.incomeExpense.setReadonly(true);
		this.incomeTypeCode.setReadonly(true);
		this.incomeTypeDesc.setReadonly(true);
		this.incomeTypeIsActive.setDisabled(true);

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
		this.incomeTypeCode.setValue("");
		this.incomeTypeDesc.setValue("");
		this.incomeTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final IncomeType aIncomeType = new IncomeType();
		BeanUtils.copyProperties(getIncomeType(), aIncomeType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the IncomeType object with the components data
		doWriteComponentsToBean(aIncomeType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aIncomeType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aIncomeType.getRecordType())) {
				aIncomeType.setVersion(aIncomeType.getVersion() + 1);
				if (isNew) {
					aIncomeType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aIncomeType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aIncomeType.setNewRecord(true);
				}
			}
		} else {
			aIncomeType.setVersion(aIncomeType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aIncomeType, tranType)) {
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
	 * @param aIncomeType
	 *            (IncomeType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(IncomeType aIncomeType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aIncomeType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aIncomeType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aIncomeType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aIncomeType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aIncomeType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aIncomeType);
				}
				if (isNotesMandatory(taskId, aIncomeType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isNotBlank(nextTaskId)) {
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
			aIncomeType.setTaskId(taskId);
			aIncomeType.setNextTaskId(nextTaskId);
			aIncomeType.setRoleCode(getRole());
			aIncomeType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aIncomeType, tranType);
			String operationRefs = getServiceOperations(taskId, aIncomeType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aIncomeType,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aIncomeType, tranType);
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
		IncomeType aIncomeType = (IncomeType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getIncomeTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getIncomeTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getIncomeTypeService().doApprove(auditHeader);

						if (aIncomeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getIncomeTypeService().doReject(auditHeader);

						if (aIncomeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_IncomeTypeDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_IncomeTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.incomeType), true);
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

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(IncomeType aIncomeType, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,aIncomeType.getBefImage(), aIncomeType);
		return new AuditHeader(String.valueOf(aIncomeType.getId()), null, null,
				null, auditDetail, aIncomeType.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_IncomeTypeDialog,auditHeader);
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
		doShowNotes(this.incomeType);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getIncomeTypeListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.incomeType.getIncomeTypeCode());
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

	public IncomeType getIncomeType() {
		return this.incomeType;
	}
	public void setIncomeType(IncomeType incomeType) {
		this.incomeType = incomeType;
	}

	public void setIncomeTypeService(IncomeTypeService incomeTypeService) {
		this.incomeTypeService = incomeTypeService;
	}
	public IncomeTypeService getIncomeTypeService() {
		return this.incomeTypeService;
	}

	public void setIncomeTypeListCtrl(IncomeTypeListCtrl incomeTypeListCtrl) {
		this.incomeTypeListCtrl = incomeTypeListCtrl;
	}
	public IncomeTypeListCtrl getIncomeTypeListCtrl() {
		return this.incomeTypeListCtrl;
	}
}
