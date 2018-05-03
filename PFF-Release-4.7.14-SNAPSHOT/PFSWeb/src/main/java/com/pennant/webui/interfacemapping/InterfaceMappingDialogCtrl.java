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
 * FileName    		:  InterfaceMappingDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.interfacemapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.interfacemapping.InterfaceFields;
import com.pennant.backend.model.interfacemapping.InterfaceMapping;
import com.pennant.backend.model.interfacemapping.MasterMapping;
import com.pennant.backend.service.interfacemapping.InterfaceMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.bajaj.process.collections.model.CollectionConstants;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/InterfaceMapping/InterfaceMappingDialog.zul file.
 */
public class InterfaceMappingDialogCtrl extends GFCBaseCtrl<InterfaceMapping> {
	private static final long					serialVersionUID	= 3184249234920071313L;
	private static final Logger					logger				= Logger.getLogger(InterfaceMappingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_InterfaceMappingDialog;
	protected ExtendedCombobox					interfaceName;
	protected ExtendedCombobox					interfaceField;
	protected ExtendedCombobox					mappingTable;
	protected Combobox							mappingColumn;
	protected ExtendedCombobox					mappingValue;
	protected Checkbox							active;

	protected Row								row1;
	protected Row								row2;
	protected Listbox							listBoxInterfaceMapping;

	// not autoWired Var's
	private InterfaceMapping					interfaceMapping;
	private transient InterfaceMappingListCtrl	interfaceMappingListCtrl;
	private List<MasterMapping>					masterMappingList	= new ArrayList<MasterMapping>();
	private String								mappingType			= null;
	private Long								id;

	// parameters
	private transient boolean					validationOn;

	// ServiceDAOs / Domain Classes
	private transient InterfaceMappingService	interfaceMappingService;


	/**
	 * default constructor.<br>
	 */
	public InterfaceMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InterfaceMappingDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected InterfaceMapping object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InterfaceMappingDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_InterfaceMappingDialog);
		
		/* set components visible dependent of the users rights */
		try {
			
			doCheckRights();
			// Get the required arguments.
			this.interfaceMapping = (InterfaceMapping) arguments.get("interfaceMapping");

			InterfaceMapping befImage = new InterfaceMapping();
			BeanUtils.copyProperties(this.interfaceMapping, befImage);
			this.interfaceMapping.setBefImage(befImage);

			setInterfaceMapping(this.interfaceMapping);
			setInterfaceMappingListCtrl((InterfaceMappingListCtrl) arguments.get("interfaceMappingListCtrl"));

			doLoadWorkFlow(this.interfaceMapping.isWorkflow(), this.interfaceMapping.getWorkflowId(), this.interfaceMapping.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "InterfaceMappingDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.interfaceMapping);
			
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
			this.window_InterfaceMappingDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.interfaceName.setModuleName("InterfaceFields");
		this.interfaceName.setValueColumn("InterfaceName");
		this.interfaceName.setValidateColumns(new String[] { "InterfaceName" });
		this.interfaceName.setMandatoryStyle(true);

		this.interfaceField.setModuleName("InterfaceFields");
		this.interfaceField.setValueColumn("Field");
		this.interfaceField.setValidateColumns(new String[] { "Field" });
		this.interfaceField.setMandatoryStyle(true);

		this.mappingTable.setMandatoryStyle(true);
		this.mappingValue.setMandatoryStyle(true);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InterfaceMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InterfaceMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InterfaceMappingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InterfaceMappingDialog_btnSave"));
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
		
		MessageUtil.showHelpWindow(event, window_InterfaceMappingDialog);
		
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
		
		doWriteBeanToComponents(this.interfaceMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param ainterfaceMapping
	 *            InterfaceMapping
	 */
	public void doWriteBeanToComponents(InterfaceMapping ainterfaceMapping) {
		logger.debug("Entering");
	
		if (!ainterfaceMapping.isNew()) {
			
			this.interfaceName.setValue(ainterfaceMapping.getInterfaceName());
			this.interfaceField.setValue(ainterfaceMapping.getInterfaceField());
			this.active.setChecked(ainterfaceMapping.isActive());
			
			doFillMappingType(ainterfaceMapping);
		
		} else {
			
			this.interfaceName.setValue("");
			this.interfaceField.setValue("");
			this.mappingValue.setValue("");
			this.mappingColumn.setValue("");
			this.mappingTable.setValue("");
			this.active.setChecked(true);
			this.recordStatus.setValue(ainterfaceMapping.getRecordStatus());
		}
	
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param ainterfaceMapping
	 */
	public void doWriteComponentsToBean(InterfaceMapping ainterfaceMapping,boolean condition) {
		logger.debug("Entering");
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			ainterfaceMapping.setInterfaceName(this.interfaceName.getValue());
			ainterfaceMapping.setInterfaceId(getId());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			ainterfaceMapping.setInterfaceField(this.interfaceField.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//if mapping type is column,validation will check
		if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_COLUMN)) {
			
			try {
				ainterfaceMapping.setMappingTable(this.mappingTable.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if ("#".equals(getComboboxValue(this.mappingColumn))) {
					throw new WrongValueException(this.mappingColumn, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_InterfaceMappingDialog_MappingColumn.value") }));
				}
					ainterfaceMapping.setMappingColumn(this.mappingColumn.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		//if mapping type is Value,validation will check
		if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_VALUE)) {
			try {
				ainterfaceMapping.setMappingValue(this.mappingValue.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		try {
			ainterfaceMapping.setActive(this.active.isChecked());
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
		} else {	
			if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_MASTER) && !condition) {
				validateMappingListItems(getMasterMappingList());
			} else {
				if (interfaceMapping.getMasterMappingList() != null
						&& !interfaceMapping.getMasterMappingList().isEmpty()) {

					for (int i = 0; i < interfaceMapping.getMasterMappingList().size(); i++) {
						MasterMapping masterMapping = interfaceMapping.getMasterMappingList().get(i);

						if (StringUtils.isBlank(masterMapping.getInterfaceValue())) {
							interfaceMapping.getMasterMappingList().remove(i);
							i = 0;
						}
					}
				}

			}
		}

		ainterfaceMapping.setRecordStatus(this.recordStatus.getValue());
		ainterfaceMapping.setMasterMappingList(this.masterMappingList);
		
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param ainterfaceMapping
	 * @throws Exception
	 */
	public void doShowDialog(InterfaceMapping ainterfaceMapping) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (ainterfaceMapping.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.interfaceName.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.interfaceField.focus();
				if (StringUtils.isNotBlank(ainterfaceMapping.getRecordType())) {
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
			doWriteBeanToComponents(ainterfaceMapping);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_InterfaceMappingDialog.onClose();
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

		if (!this.interfaceName.isReadonly()) {
			this.interfaceName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_InterfaceMappingDialog_InterfaceName.value"), null, true));
		}

		if (!this.interfaceField.isReadonly()) {
			this.interfaceField.setConstraint(new PTStringValidator(
					Labels.getLabel("label_InterfaceMappingDialog_InterfaceField.value"), null, true));
		}

		if (!this.mappingTable.isReadonly()) {
			this.mappingTable.setConstraint(new PTStringValidator(
					Labels.getLabel("label_InterfaceMappingDialog_MappingTable.value"), null, true));
		}

		if (!this.mappingValue.isReadonly()) {
			this.mappingValue.setConstraint(new PTStringValidator(
					Labels.getLabel("label_InterfaceMappingDialog_MappingValue.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
	
		setValidationOn(false);
		this.interfaceName.setConstraint("");
		this.interfaceField.setConstraint("");
		this.mappingTable.setConstraint("");
		this.mappingColumn.setConstraint("");
		this.mappingValue.setConstraint("");
		
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
		
		this.interfaceName.setErrorMessage("");
		this.interfaceField.setErrorMessage("");
		this.mappingTable.setErrorMessage("");
		this.mappingColumn.setErrorMessage("");
		this.mappingValue.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a interfaceMapping object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final InterfaceMapping entity = new InterfaceMapping();
		BeanUtils.copyProperties(getInterfaceMapping(), entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_InterfaceMappingDialog_InterfaceName.value") + " : "
				+ interfaceMapping.getInterfaceName();
		
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			
			if (StringUtils.isBlank(interfaceMapping.getRecordType())) {
				interfaceMapping.setVersion(interfaceMapping.getVersion() + 1);
				interfaceMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					interfaceMapping.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					
					if (interfaceMapping.getMasterMappingList() != null && !interfaceMapping.getMasterMappingList().isEmpty()) {
						
						for (int i = 0; i< interfaceMapping.getMasterMappingList().size();  i++) {
							MasterMapping masterMapping = interfaceMapping.getMasterMappingList().get(i);
							
							if (StringUtils.isBlank(masterMapping.getInterfaceValue())) {
								interfaceMapping.getMasterMappingList().remove(i);
								i = 0;
							}
						}
					}
					
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
		
			try {
				if (doProcess(interfaceMapping, tranType)) {
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

		if (getInterfaceMapping().isNewRecord()) {
			this.interfaceName.setReadonly(isReadOnly("InterfaceMappingDialog_InterfaceName")); 
			this.mappingColumn.setDisabled(isReadOnly("InterfaceMappingDialog_MappingColumn"));
			this.mappingValue.setReadonly(isReadOnly("InterfaceMappingDialog_MappingValue"));
			this.btnCancel.setVisible(false);
			this.active.setDisabled(true);
		} else {
			this.interfaceName.setReadonly(true);
			this.mappingColumn.setDisabled(true);
			this.mappingValue.setReadonly(true);
			this.active.setDisabled(isReadOnly("InterfaceMappingDialog_Active"));
			this.btnCancel.setVisible(true);
		}
		
		this.interfaceField.setReadonly(true);
		this.mappingTable.setReadonly(true);
	
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.interfaceMapping.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.interfaceName.setReadonly(true);
		this.interfaceField.setReadonly(true);
		this.mappingTable.setReadonly(true);
		this.mappingColumn.setReadonly(true);
		this.mappingValue.setReadonly(true);
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
		this.interfaceName.setValue("");
		this.interfaceField.setValue("");
		this.mappingTable.setValue("");
		this.mappingColumn.setValue("");
		this.mappingValue.setValue("");
		this.active.setChecked(false);
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final InterfaceMapping ainterfaceMapping = new InterfaceMapping();
		BeanUtils.copyProperties(getInterfaceMapping(), ainterfaceMapping);
		boolean isNew = false;
		boolean condition=false;
		
		if (this.userAction.getSelectedItem() != null) {
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {
				condition = true;
			}
		}
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		
		// fill the InterfaceMapping object with the components data
		doWriteComponentsToBean(ainterfaceMapping,condition);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = ainterfaceMapping.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			
			if (StringUtils.isBlank(ainterfaceMapping.getRecordType())) {
				ainterfaceMapping.setVersion(ainterfaceMapping.getVersion() + 1);
				if (isNew) {
					ainterfaceMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					ainterfaceMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					ainterfaceMapping.setNewRecord(true);
				}
			}
			
		} else {
			ainterfaceMapping.setVersion(ainterfaceMapping.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(ainterfaceMapping, tranType)) {
				refreshList();
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
	 * @param ainterfaceMapping
	 *            (InterfaceMapping)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(InterfaceMapping ainterfaceMapping, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		ainterfaceMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		ainterfaceMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ainterfaceMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			ainterfaceMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(ainterfaceMapping.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, ainterfaceMapping);
				}

				if (isNotesMandatory(taskId, ainterfaceMapping)) {
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

			ainterfaceMapping.setTaskId(taskId);
			ainterfaceMapping.setNextTaskId(nextTaskId);
			ainterfaceMapping.setRoleCode(getRole());
			ainterfaceMapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(ainterfaceMapping, tranType);

			String operationRefs = getServiceOperations(taskId, ainterfaceMapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(ainterfaceMapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(ainterfaceMapping, tranType);
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
		InterfaceMapping ainterfaceMapping = (InterfaceMapping) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getInterfaceMappingService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getInterfaceMappingService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getInterfaceMappingService().doApprove(auditHeader);

						if (ainterfaceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getInterfaceMappingService().doReject(auditHeader);

						if (ainterfaceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_InterfaceMappingDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_InterfaceMappingDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.interfaceMapping), true);
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
	
	/**
	 * change interface field based on interfaceName
	 * 
	 * @param event
	 */
	public void onFulfill$interfaceName(Event event) {
		logger.debug("Entering");
	
		Object dataObject = interfaceName.getObject();
		this.interfaceField.setValue("");
		
		if (dataObject instanceof String) {
			this.row1.setVisible(false);
			this.row2.setVisible(false);
			this.listBoxInterfaceMapping.setVisible(false);
			this.mappingValue.setValue("");
			this.mappingColumn.setValue("");
			this.mappingTable.setValue("");
		} else if (!(dataObject instanceof String)) {
			
			InterfaceFields interfaceField = (InterfaceFields) dataObject;
			
			if (interfaceField != null) {
				this.interfaceField.setValue(interfaceField.getField());
				processMappingData(interfaceField);
			} else {
				
				this.row1.setVisible(false);
				this.row2.setVisible(false);
				this.listBoxInterfaceMapping.setVisible(false);
				this.interfaceField.setValue("");
			}
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Based on interfaceField and module type from event,
	 * screen will display 
	 * 
	 * @param event
	 */

	public void onFulfill$interfaceField(Event event) {
		logger.debug("Entering");
		
		Object dataObject = interfaceField.getObject();
		
		if (dataObject instanceof String) {
			this.row1.setVisible(false);
			this.row2.setVisible(false);
			this.listBoxInterfaceMapping.setVisible(false);
			this.mappingValue.setValue("");
			this.mappingColumn.setValue("");
			this.mappingTable.setValue("");
		} else if (!(dataObject instanceof String)) {
			
			InterfaceFields interfaceField = (InterfaceFields) dataObject;
			
			if (interfaceField != null) {
				processMappingData(interfaceField);
			} else {
				this.row1.setVisible(false);
				this.row2.setVisible(false);
				this.listBoxInterfaceMapping.setVisible(false);
			}
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Based on interfaceField and module type from event,
	 * screen will display 
	 * 
	 * @param interfaceField
	 */
	public void processMappingData(InterfaceFields interfaceField) {
		logger.debug("Entering");

		mappingType = interfaceField.getMappingType();
		setId(interfaceField.getInterfaceId());

		if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_VALUE)) {
			this.row1.setVisible(false);
			this.row2.setVisible(true);
			this.listBoxInterfaceMapping.setVisible(false);
			this.mappingValue.setMandatoryStyle(true);
			String[] vale = ModuleUtil.getLovFields(interfaceField.getModule());
			
			this.mappingValue.setModuleName(interfaceField.getModule());
			this.mappingValue.setValueColumn(vale[0]);
			this.mappingValue.setDescColumn(vale[1]);
			this.mappingValue.setValidateColumns(new String[] { vale[0] });

		} else if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_COLUMN)) {
			this.row1.setVisible(true);
			this.row2.setVisible(false);
			this.listBoxInterfaceMapping.setVisible(false);
			this.mappingTable.setMandatoryStyle(true);
			this.mappingTable.setValue(interfaceField.getTableName());
			this.mappingTable.setButtonDisabled(true);

			//List of column Name will retrieve based on interfaceField table name
			List<String> tableColumnsList = getInterfaceMappingService().getTableNameColumnsList(interfaceField.getTableName());
			
			List<ValueLabel> columnsList = new ArrayList<>();
			if (tableColumnsList != null && !tableColumnsList.isEmpty()) {
				for (String column : tableColumnsList) {
					columnsList.add(new ValueLabel(column, column));
				}
			}
			
			fillComboBox(this.mappingColumn,null, columnsList, "");

		} else if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_MASTER)) {
			this.row1.setVisible(false);
			this.row2.setVisible(false);
			this.listBoxInterfaceMapping.setVisible(true);
			getMasterMappingList().clear();
			doFillMasterList(interfaceField);
		}
		
		logger.debug("Leaving");
	}
	/**
	 * Based on interface Mapping Type,interface Mapping bean data setting
	 * 
	 * @param interfaceMapping
	 */

	private void doFillMappingType(InterfaceMapping interfaceMapping) {
		logger.debug("Entering");

		mappingType = interfaceMapping.getMappingType();
        id = interfaceMapping.getInterfaceId();
		if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_VALUE)) {
			this.row1.setVisible(false);
			this.row2.setVisible(true);
			this.listBoxInterfaceMapping.setVisible(false);

			String[] vale = ModuleUtil.getLovFields(interfaceMapping.getModule());
			
			this.mappingValue.setModuleName(interfaceMapping.getModule());
			this.mappingValue.setValueColumn(vale[0]);
			this.mappingValue.setDescColumn(vale[1]);
			this.mappingValue.setValidateColumns(new String[] { vale[0] });
			this.mappingValue.setValue(interfaceMapping.getMappingValue());

		} else if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_COLUMN)) {
			this.row1.setVisible(true);
			this.row2.setVisible(false);
			this.listBoxInterfaceMapping.setVisible(false);
			
			this.mappingTable.setValue(interfaceMapping.getMappingTable());
			
			List<String> tableColumnsList = getInterfaceMappingService()
					.getTableNameColumnsList(interfaceMapping.getMappingTable());

			List<ValueLabel> columnsList = new ArrayList<>();
			if (tableColumnsList != null && !tableColumnsList.isEmpty()) {
				for (String column : tableColumnsList) {
					columnsList.add(new ValueLabel(column, column));
				}
			}
			fillComboBox(this.mappingColumn,interfaceMapping.getMappingColumn(), columnsList, "");

		} else if (StringUtils.equals(mappingType, CollectionConstants.INTERFACEMAPPING_MASTER)) {
			
			this.row1.setVisible(false);
			this.row2.setVisible(false);
			this.listBoxInterfaceMapping.setVisible(true);

			//Get the Latest data from the Master Mapping Table
			//boolean condition = isReadOnly("InterfaceMappingDialog_List");
			
			if (!PennantConstants.RECORD_TYPE_DEL.equals(interfaceMapping.getRecordType())) {
				prepareMasterMappingList(interfaceMapping.getMasterMappingList(), interfaceMapping.getModule());
			}
			
			doFulfillMastermapping(interfaceMapping.getMasterMappingList());
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method to set List item based on mappingList
	 * 
	 * @param mappingList
	 */
	private void doFulfillMastermapping(List<MasterMapping> mappingList) {
		logger.debug("Entering");

		this.listBoxInterfaceMapping.getItems().clear();

		Hbox hbox = null;
		Space space = null;
		Textbox interfaceValue = null;
		boolean condition = isReadOnly("InterfaceMappingDialog_List");
		
		setMasterMappingList(mappingList);
		
		if (mappingList != null && !mappingList.isEmpty()) {

			for (MasterMapping masterMapping : mappingList) {
				
				if (!masterMapping.isNewRecord()) {
					MasterMapping befImage = new MasterMapping();
					BeanUtils.copyProperties(masterMapping, befImage);
					masterMapping.setBefImage(befImage);
				}
				
				Listitem item = new Listitem();
				Listcell lc;

				//PLF Value
				lc = new Listcell(masterMapping.getPlfValue());
				lc.setParent(item);
				
				lc = new Listcell();
				hbox = new Hbox();
				hbox.setParent(lc);

				space = new Space();
				space.setSpacing("2px");
				space.setSclass(PennantConstants.mandateSclass);
				space.setParent(hbox);
				
				space = new Space();
				space.setSpacing("2px");
				space.setParent(hbox);

				// Interface Value
				interfaceValue = new Textbox();
				interfaceValue.setValue(masterMapping.getInterfaceValue());
				interfaceValue.setReadonly(condition);
				hbox.appendChild(interfaceValue);
				
				List<Object> interfaceList = new ArrayList<Object>(11);
				interfaceList.add(interfaceValue);
				interfaceList.add(masterMapping);
				
				interfaceValue.addForward("onChange", window_InterfaceMappingDialog, "onChangeInterfaceValue", interfaceList);
				
				item.appendChild(lc);

				this.listBoxInterfaceMapping.appendChild(item);
			}
		}

		logger.debug("Leaving");

	}
   
	public void onChangeInterfaceValue(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) event.getData();

		Textbox interfaceValueBox = (Textbox) list.get(0);
		MasterMapping masterMapping = (MasterMapping) list.get(1);
		
		interfaceValueBox.setErrorMessage("");
		
		if (StringUtils.isBlank(masterMapping.getRecordType())) {
			if (isWorkFlowEnabled()) {
				masterMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				masterMapping.setNewRecord(true);
			}
		}

		masterMapping.setInterfaceValue(interfaceValueBox.getValue());
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method doListModelItemRender to extact data based on data given and added to mastermappinglist
	 * 
	 * @param masterMappingList
	 * @return
	 */
	private void validateMappingListItems(List<MasterMapping> masterMappingList) {
		logger.debug("Entering");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		if (masterMappingList != null && !masterMappingList.isEmpty()) {
			List<Listitem> listItems = this.listBoxInterfaceMapping.getItems();
			
			for (Listitem item : listItems) {
				Listcell lc = (Listcell) item.getChildren().get(0);
				Textbox interfaceValueBox = (Textbox) item.getChildren().get(1).getFirstChild().getChildren().get(2);
				interfaceValueBox.setErrorMessage("");
				
				if (StringUtils.isBlank(interfaceValueBox.getValue())) {
					wve.add(new WrongValueException(interfaceValueBox, "Please Enter " + lc.getLabel() + " Value"));
				}
			}
			
			if (!wve.isEmpty()) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
		} 

		logger.debug("Leaving");
	}
    /**
     * Based on interface Module Type,Retrive List of data of first column from Module type table.
     * @param interfaceField
     */
	public void doFillMasterList(InterfaceFields interfaceField) {

		logger.debug("Entering");

		if (interfaceField.getModule() != null) {
			prepareMasterMappingList(getMasterMappingList(), interfaceField.getModule());
			doFulfillMastermapping(masterMappingList);
		}

		logger.debug("Leaving");
	}

	/**
	 * method compareBothData ,compare both main table and module table and remove unwanted list
	 * 
	 * @param masterList
	 * @param module
	 * @return list of MasterMapping
	 */
	
	public void prepareMasterMappingList(List<MasterMapping> masterList, String module) {
		logger.debug("Entering");

		//get table name based on module type
		String tableName = ModuleUtil.getLovTableName(module);
		String[] vale = ModuleUtil.getLovFields(module);

		List<String> mappedColumnsList = getInterfaceMappingService().getMappings(tableName, vale[0]);
		
		if (mappedColumnsList != null && !mappedColumnsList.isEmpty()) {
			
			MasterMapping masterMapping = null;
			
			for (String columnValue : mappedColumnsList) {
				boolean recordFound = false;
				
				for(MasterMapping mapping : masterList) {
					if (StringUtils.equals(columnValue, mapping.getPlfValue())) {
						recordFound = true;
						break;
					}
				}
				
				if (!recordFound) {
					masterMapping = new MasterMapping();
					masterMapping.setPlfValue(columnValue);
					masterMapping.setNewRecord(true);
					masterMapping.setRecordType(PennantConstants.RCD_ADD);
					masterList.add(masterMapping);
				}
			}
		}
		
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * @param ainterfaceMapping
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(InterfaceMapping ainterfaceMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, ainterfaceMapping.getBefImage(), ainterfaceMapping);
		return new AuditHeader(String.valueOf(ainterfaceMapping.getInterfaceName()), null, null, null, auditDetail,
				ainterfaceMapping.getUserDetails(), getOverideMap());

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
		doShowNotes(this.interfaceMapping);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getInterfaceMappingListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.interfaceMapping.getInterfaceName());
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

	public InterfaceMapping getInterfaceMapping() {
		return this.interfaceMapping;
	}

	public void setInterfaceMapping(InterfaceMapping interfaceMapping) {
		this.interfaceMapping = interfaceMapping;
	}

	public void setInterfaceMapping(InterfaceMappingService addressTypeService) {
		this.interfaceMappingService = addressTypeService;
	}

	public void setInterfaceMappingListCtrl(InterfaceMappingListCtrl interfaceMappingListCtrl) {
		this.interfaceMappingListCtrl = interfaceMappingListCtrl;
	}

	public InterfaceMappingListCtrl getInterfaceMappingListCtrl() {
		return this.interfaceMappingListCtrl;
	}

	public List<MasterMapping> getMasterMappingList() {
		return masterMappingList;
	}

	public void setMasterMappingList(List<MasterMapping> masterMappingList) {
		this.masterMappingList = masterMappingList;
	}
	
	public InterfaceMappingService getInterfaceMappingService() {
		return interfaceMappingService;
	}

	public void setInterfaceMappingService(InterfaceMappingService interfaceMappingService) {
		this.interfaceMappingService = interfaceMappingService;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
