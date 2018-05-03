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
 * FileName    		:  SalesOfficerDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.salesofficer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.SalesOfficerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/SalesOfficer/salesOfficerDialog.zul file.
 */
public class SalesOfficerDialogCtrl extends GFCBaseCtrl<SalesOfficer> {
	private static final long serialVersionUID = 352659757425874223L;
	private static final Logger logger = Logger.getLogger(SalesOfficerDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_SalesOfficerDialog; 	// autoWired
	protected Textbox 	salesOffCode; 				// autoWired
	protected Textbox 	salesOffFName; 				// autoWired
	protected Textbox 	salesOffMName; 				// autoWired
	protected Textbox 	salesOffLName;			    // autoWired
	protected Textbox 	salesOffShrtName; 			// autoWired
	protected ExtendedCombobox 	salesOffDept; 				// autoWired
	protected Checkbox 	salesOffIsActive; 			// autoWired


	// not auto wired variables
	private SalesOfficer salesOfficer; 							 // overHanded per parameter
	private transient SalesOfficerListCtrl salesOfficerListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient SalesOfficerService salesOfficerService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();
	

	/**
	 * default constructor.<br>
	 */
	public SalesOfficerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SalesOfficerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SalesOfficer object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SalesOfficerDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SalesOfficerDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();
			
			if (arguments.containsKey("salesOfficer")) {
				this.salesOfficer = (SalesOfficer) arguments.get("salesOfficer");
				SalesOfficer befImage = new SalesOfficer();
				BeanUtils.copyProperties(this.salesOfficer, befImage);
				this.salesOfficer.setBefImage(befImage);

				setSalesOfficer(this.salesOfficer);
			} else {
				setSalesOfficer(null);
			}

			doLoadWorkFlow(this.salesOfficer.isWorkflow(),
					this.salesOfficer.getWorkflowId(),
					this.salesOfficer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"SalesOfficerDialog");
			}

			// READ OVERHANDED parameters !
			// we get the salesOfficerListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete salesOfficer here.
			if (arguments.containsKey("salesOfficerListCtrl")) {
				setSalesOfficerListCtrl((SalesOfficerListCtrl) arguments
						.get("salesOfficerListCtrl"));
			} else {
				setSalesOfficerListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSalesOfficer());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SalesOfficerDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.salesOffCode.setMaxlength(8);
		this.salesOffFName.setMaxlength(50);
		this.salesOffMName.setMaxlength(50);
		this.salesOffLName.setMaxlength(50);
		this.salesOffShrtName.setMaxlength(50);
		this.salesOffDept.setMaxlength(8);

		this.salesOffDept.setMandatoryStyle(true);
		this.salesOffDept.setModuleName("Department");
		this.salesOffDept.setValueColumn("DeptCode");
		this.salesOffDept.setDescColumn("DeptDesc");
		this.salesOffDept.setValidateColumns(new String[]{"DeptCode"});
		
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
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_SalesOfficerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_SalesOfficerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_SalesOfficerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_SalesOfficerDialog_btnSave"));
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
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
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
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_SalesOfficerDialog);
		logger.debug("Leaving");
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
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving");
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
		doWriteBeanToComponents(this.salesOfficer.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSalesOfficer
	 *            SalesOfficer
	 */
	public void doWriteBeanToComponents(SalesOfficer aSalesOfficer) {
		logger.debug("Entering");
		this.salesOffCode.setValue(aSalesOfficer.getSalesOffCode());
		this.salesOffFName.setValue(aSalesOfficer.getSalesOffFName());
		this.salesOffMName.setValue(aSalesOfficer.getSalesOffMName());
		this.salesOffLName.setValue(aSalesOfficer.getSalesOffLName());
		this.salesOffShrtName.setValue(aSalesOfficer.getSalesOffShrtName());
		this.salesOffDept.setValue(aSalesOfficer.getSalesOffDept());
		this.salesOffIsActive.setChecked(aSalesOfficer.isSalesOffIsActive());

		if (aSalesOfficer.isNewRecord()) {
			this.salesOffDept.setDescription("");
		} else {
			this.salesOffDept.setDescription(aSalesOfficer.getLovDescSalesOffDeptName());
		}
		this.recordStatus.setValue(aSalesOfficer.getRecordStatus());
		
		if(aSalesOfficer.isNew() || (aSalesOfficer.getRecordType() != null ? aSalesOfficer.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.salesOffIsActive.setChecked(true);
			this.salesOffIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSalesOfficer
	 */
	public void doWriteComponentsToBean(SalesOfficer aSalesOfficer) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSalesOfficer.setSalesOffCode(this.salesOffCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setSalesOffFName(this.salesOffFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setSalesOffMName(this.salesOffMName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setSalesOffLName(this.salesOffLName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setSalesOffShrtName(this.salesOffShrtName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer.setLovDescSalesOffDeptName(this.salesOffDept.getDescription());
			aSalesOfficer.setSalesOffDept(this.salesOffDept.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalesOfficer
					.setSalesOffIsActive(this.salesOffIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aSalesOfficer.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSalesOfficer
	 * @throws Exception
	 */
	public void doShowDialog(SalesOfficer aSalesOfficer)
			throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSalesOfficer.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.salesOffCode.focus();
		} else {
			this.salesOffFName.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aSalesOfficer.getRecordType())) {
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
			doWriteBeanToComponents(aSalesOfficer);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SalesOfficerDialog.onClose();
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

		if (!this.salesOffCode.isReadonly()){
			this.salesOffCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		
		if (!this.salesOffFName.isReadonly()){
			this.salesOffFName.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffFName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		
		if (!this.salesOffMName.isReadonly()){
			this.salesOffMName.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffMName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}

		if (!this.salesOffLName.isReadonly()){
			this.salesOffLName.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffLName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}

		if (!this.salesOffShrtName.isReadonly()){
			this.salesOffShrtName.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffShrtName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		
		if (!this.salesOffDept.isReadonly()) {
			this.salesOffDept.setConstraint(new PTStringValidator(Labels.getLabel("label_SalesOfficerDialog_SalesOffDept.value"), null, true,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.salesOffCode.setConstraint("");
		this.salesOffFName.setConstraint("");
		this.salesOffMName.setConstraint("");
		this.salesOffLName.setConstraint("");
		this.salesOffShrtName.setConstraint("");
		this.salesOffDept.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.salesOffCode.setErrorMessage("");
		this.salesOffFName.setErrorMessage("");
		this.salesOffMName.setErrorMessage("");
		this.salesOffLName.setErrorMessage("");
		this.salesOffShrtName.setErrorMessage("");
		this.salesOffDept.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a SalesOfficer object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SalesOfficer aSalesOfficer = new SalesOfficer();
		BeanUtils.copyProperties(getSalesOfficer(), aSalesOfficer);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_SalesOfficerDialog_SalesOffCode.value")+" : "+aSalesOfficer.getSalesOffCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSalesOfficer.getRecordType())) {
				aSalesOfficer.setVersion(aSalesOfficer.getVersion() + 1);
				aSalesOfficer.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSalesOfficer.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSalesOfficer, tranType)) {
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
		logger.debug("Enterring");

		if (getSalesOfficer().isNewRecord()) {
			this.salesOffCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.salesOffCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.salesOffFName
				.setReadonly(isReadOnly("SalesOfficerDialog_salesOffFName"));
		this.salesOffMName
				.setReadonly(isReadOnly("SalesOfficerDialog_salesOffMName"));
		this.salesOffLName
				.setReadonly(isReadOnly("SalesOfficerDialog_salesOffLName"));
		this.salesOffShrtName
				.setReadonly(isReadOnly("SalesOfficerDialog_salesOffShrtName"));
		this.salesOffDept.setReadonly(isReadOnly("SalesOfficerDialog_salesOffDept"));
		this.salesOffIsActive
				.setDisabled(isReadOnly("SalesOfficerDialog_salesOffIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.salesOfficer.isNewRecord()) {
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
		logger.debug("Enterring");
		this.salesOffCode.setReadonly(true);
		this.salesOffFName.setReadonly(true);
		this.salesOffMName.setReadonly(true);
		this.salesOffLName.setReadonly(true);
		this.salesOffShrtName.setReadonly(true);
		this.salesOffDept.setReadonly(true);
		this.salesOffIsActive.setDisabled(true);

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
		logger.debug("Enterring");
		// remove validation, if there are a save before

		this.salesOffCode.setValue("");
		this.salesOffFName.setValue("");
		this.salesOffMName.setValue("");
		this.salesOffLName.setValue("");
		this.salesOffShrtName.setValue("");
		this.salesOffDept.setValue("");
		this.salesOffDept.setDescription("");
		this.salesOffIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Enterring");
		final SalesOfficer aSalesOfficer = new SalesOfficer();
		BeanUtils.copyProperties(getSalesOfficer(), aSalesOfficer);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SalesOfficer object with the components data
		doWriteComponentsToBean(aSalesOfficer);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSalesOfficer.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSalesOfficer.getRecordType())) {
				aSalesOfficer.setVersion(aSalesOfficer.getVersion() + 1);
				if (isNew) {
					aSalesOfficer
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSalesOfficer
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSalesOfficer.setNewRecord(true);
				}
			}
		} else {
			aSalesOfficer.setVersion(aSalesOfficer.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSalesOfficer, tranType)) {
				doWriteBeanToComponents(aSalesOfficer);
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
	 * @param aSalesOfficer
	 *            (SalesOfficer)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(SalesOfficer aSalesOfficer, String tranType) {
		logger.debug("Enterring");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSalesOfficer.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getUserId());
		aSalesOfficer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSalesOfficer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSalesOfficer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSalesOfficer
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSalesOfficer);
				}

				if (isNotesMandatory(taskId, aSalesOfficer)) {
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

			aSalesOfficer.setTaskId(taskId);
			aSalesOfficer.setNextTaskId(nextTaskId);
			aSalesOfficer.setRoleCode(getRole());
			aSalesOfficer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSalesOfficer, tranType);

			String operationRefs = getServiceOperations(taskId, aSalesOfficer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSalesOfficer,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aSalesOfficer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;

		SalesOfficer aSalesOfficer = (SalesOfficer) auditHeader
				.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getSalesOfficerService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSalesOfficerService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getSalesOfficerService().doApprove(
								auditHeader);

						if (aSalesOfficer.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSalesOfficerService().doReject(
								auditHeader);
						if (aSalesOfficer.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_SalesOfficerDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_SalesOfficerDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.salesOfficer), true);
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

		logger.debug("return Value:" + processCompleted);
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
	private AuditHeader getAuditHeader(SalesOfficer aSalesOfficer,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aSalesOfficer.getBefImage(), aSalesOfficer);
		return new AuditHeader(aSalesOfficer.getSalesOffCode(), null, null,
				null, auditDetail, aSalesOfficer.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_SalesOfficerDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
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
		doShowNotes(this.salesOfficer);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSalesOfficerListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.salesOfficer.getSalesOffCode());
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

	public SalesOfficer getSalesOfficer() {
		return this.salesOfficer;
	}

	public void setSalesOfficer(SalesOfficer salesOfficer) {
		this.salesOfficer = salesOfficer;
	}

	public void setSalesOfficerService(SalesOfficerService salesOfficerService) {
		this.salesOfficerService = salesOfficerService;
	}

	public SalesOfficerService getSalesOfficerService() {
		return this.salesOfficerService;
	}

	public void setSalesOfficerListCtrl(
			SalesOfficerListCtrl salesOfficerListCtrl) {
		this.salesOfficerListCtrl = salesOfficerListCtrl;
	}

	public SalesOfficerListCtrl getSalesOfficerListCtrl() {
		return this.salesOfficerListCtrl;
	}

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

}
