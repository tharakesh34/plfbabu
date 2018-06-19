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
 * FileName    		:  RelationshipOfficerDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.relationshipofficer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/RelationshipOfficer
 * /relationshipOfficerDialog.zul file.
 */
public class RelationshipOfficerDialogCtrl extends GFCBaseCtrl<RelationshipOfficer> {
	private static final long serialVersionUID = -7675063106991743780L;
	private static final Logger logger = Logger.getLogger(RelationshipOfficerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RelationshipOfficerDialog; 	// autoWired
	protected Textbox rOfficerCode; 					// autoWired
	protected Textbox rOfficerDesc; 					// autoWired
	protected ExtendedCombobox rOfficerDeptCode; 				// autoWired
	protected Checkbox rOfficerIsActive; 				// autoWired
	protected Textbox grade; // autoWired
	protected Textbox mobileNO; // autoWired
	protected ExtendedCombobox genDesignation;
	protected Datebox dateOfJoin;
	// not auto wired variables
	private RelationshipOfficer relationshipOfficer; 						   // overHanded per parameter
	private transient RelationshipOfficerListCtrl relationshipOfficerListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient RelationshipOfficerService relationshipOfficerService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();

	/**
	 * default constructor.<br>
	 */
	public RelationshipOfficerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RelationshipOfficerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected RelationshipOfficer
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RelationshipOfficerDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RelationshipOfficerDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("relationshipOfficer")) {
				this.relationshipOfficer = (RelationshipOfficer) arguments
						.get("relationshipOfficer");
				RelationshipOfficer befImage = new RelationshipOfficer();
				BeanUtils.copyProperties(this.relationshipOfficer, befImage);
				this.relationshipOfficer.setBefImage(befImage);

				setRelationshipOfficer(this.relationshipOfficer);
			} else {
				setRelationshipOfficer(null);
			}
			
			doLoadWorkFlow(this.relationshipOfficer.isWorkflow(),
					this.relationshipOfficer.getWorkflowId(),
					this.relationshipOfficer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"RelationshipOfficerDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the relationshipOfficerListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete relationshipOfficer here.
			if (arguments.containsKey("relationshipOfficerListCtrl")) {
				setRelationshipOfficerListCtrl((RelationshipOfficerListCtrl) arguments
						.get("relationshipOfficerListCtrl"));
			} else {
				setRelationshipOfficerListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getRelationshipOfficer());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RelationshipOfficerDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.rOfficerCode.setMaxlength(8);
		this.rOfficerDesc.setMaxlength(50);
		this.rOfficerDeptCode.setMaxlength(8);
		this.genDesignation.setMaxlength(8);
		this.grade.setMaxlength(35);
		this.mobileNO.setMaxlength(20);
		this.rOfficerDeptCode.setMandatoryStyle(true);
		this.rOfficerDeptCode.setModuleName("GeneralDepartment");
		this.rOfficerDeptCode.setValueColumn("GenDepartment");
		this.rOfficerDeptCode.setDescColumn("GenDeptDesc");
		this.rOfficerDeptCode.setValidateColumns(new String[]{"GenDepartment"});
		this.genDesignation.setModuleName("GeneralDesignation");
		this.genDesignation.setValueColumn("GenDesignation");
		this.genDesignation.setDescColumn("GenDesgDesc");
		this.genDesignation.setValidateColumns(new String[] { "GenDesignation" });
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RelationshipOfficerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RelationshipOfficerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RelationshipOfficerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RelationshipOfficerDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_RelationshipOfficerDialog);
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
		doWriteBeanToComponents(this.relationshipOfficer.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aRelationshipOfficer
	 *            RelationshipOfficer
	 */
	public void doWriteBeanToComponents(RelationshipOfficer aRelationshipOfficer) {
		logger.debug("Entering");
		this.rOfficerCode.setValue(aRelationshipOfficer.getROfficerCode());
		this.rOfficerDesc.setValue(aRelationshipOfficer.getROfficerDesc());
		this.rOfficerDeptCode.setValue(aRelationshipOfficer.getROfficerDeptCode());
		this.rOfficerIsActive.setChecked(aRelationshipOfficer.isROfficerIsActive());
		this.dateOfJoin.setValue(aRelationshipOfficer.getDateOfJoin());
		this.grade.setValue(aRelationshipOfficer.getGrade());
		this.mobileNO.setValue(aRelationshipOfficer.getMobileNO());
		this.genDesignation.setValue(aRelationshipOfficer.getGenDesignation());


		if (aRelationshipOfficer.isNewRecord()) {
			this.rOfficerDeptCode.setDescription("");
		} else {
			this.rOfficerDeptCode.setDescription(aRelationshipOfficer.getLovDescROfficerDeptCodeName());
			this.genDesignation.setDescription(aRelationshipOfficer.getGendesgdesc());

		}
		this.recordStatus.setValue(aRelationshipOfficer.getRecordStatus());
		
		if(aRelationshipOfficer.isNew() || (aRelationshipOfficer.getRecordType() != null ? aRelationshipOfficer.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.rOfficerIsActive.setChecked(true);
			this.rOfficerIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRelationshipOfficer
	 */
	public void doWriteComponentsToBean(RelationshipOfficer aRelationshipOfficer) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aRelationshipOfficer.setROfficerCode(this.rOfficerCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRelationshipOfficer.setROfficerDesc(this.rOfficerDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRelationshipOfficer.setLovDescROfficerDeptCodeName(this.rOfficerDeptCode.getDescription());
			aRelationshipOfficer.setROfficerDeptCode(this.rOfficerDeptCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRelationshipOfficer.setROfficerIsActive(this.rOfficerIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRelationshipOfficer.setGrade(this.grade.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRelationshipOfficer.setMobileNO(this.mobileNO.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRelationshipOfficer.setGenDesignation(this.genDesignation.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRelationshipOfficer.setDateOfJoin(this.dateOfJoin.getValue());
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

		aRelationshipOfficer.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aRelationshipOfficer
	 * @throws Exception
	 */
	public void doShowDialog(RelationshipOfficer aRelationshipOfficer) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aRelationshipOfficer.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.rOfficerCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.rOfficerDesc.focus();
				if (StringUtils.isNotBlank(aRelationshipOfficer.getRecordType())) {
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
			doWriteBeanToComponents(aRelationshipOfficer);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_RelationshipOfficerDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Enterring");
		setValidationOn(true);

		if (!this.rOfficerCode.isReadonly()){
			this.rOfficerCode.setConstraint(new PTStringValidator(Labels.getLabel("label_RelationshipOfficerDialog_ROfficerCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.rOfficerDesc.isReadonly()){
			this.rOfficerDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_RelationshipOfficerDialog_ROfficerDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.rOfficerDeptCode.isReadonly()) {
			this.rOfficerDeptCode.setConstraint(new PTStringValidator(Labels.getLabel("label_RelationshipOfficerDialog_ROfficerDeptCode.value"), null, true,true));
		}
		if (!this.grade.isReadonly()) {
			this.grade.setConstraint(new PTStringValidator(
					Labels.getLabel("label_RelationshipOfficerDialog_Grade.value"), PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
		}
		if (!this.mobileNO.isReadonly()) {
			this.mobileNO.setConstraint(new PTStringValidator(
					Labels.getLabel("label_RelationshipOfficerDialog_MobileNO.value"), PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.dateOfJoin.isReadonly()) {
			this.dateOfJoin.setConstraint(
					new PTDateValidator(Labels.getLabel("label_RelationshipOfficerDialog_DateofJoin.value"), false));
		}
		if (!this.genDesignation.isReadonly()) {
			this.genDesignation.setConstraint(new PTStringValidator(
					Labels.getLabel("label_RelationshipOfficerDialog_GenDesignation.value"), null, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.rOfficerCode.setConstraint("");
		this.rOfficerDesc.setConstraint("");
		this.rOfficerDeptCode.setConstraint("");
		this.rOfficerDeptCode.setConstraint("");
		this.grade.setConstraint("");
		this.dateOfJoin.setConstraint("");
		this.mobileNO.setConstraint("");
		this.genDesignation.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.rOfficerCode.setErrorMessage("");
		this.rOfficerDesc.setErrorMessage("");
		this.rOfficerDeptCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a RelationshipOfficer object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final RelationshipOfficer aRelationshipOfficer = new RelationshipOfficer();
		BeanUtils.copyProperties(getRelationshipOfficer(), aRelationshipOfficer);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_RelationshipOfficerDialog_ROfficerCode.value")+" : "+aRelationshipOfficer.getROfficerCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aRelationshipOfficer.getRecordType())) {
				aRelationshipOfficer.setVersion(aRelationshipOfficer.getVersion() + 1);
				aRelationshipOfficer.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aRelationshipOfficer.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aRelationshipOfficer, tranType)) {
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

		if (getRelationshipOfficer().isNewRecord()) {
			this.rOfficerCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.rOfficerCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.rOfficerDesc.setReadonly(isReadOnly("RelationshipOfficerDialog_rOfficerDesc"));
		this.rOfficerDeptCode.setReadonly(isReadOnly("RelationshipOfficerDialog_rOfficerDeptCode"));
		this.rOfficerIsActive.setDisabled(isReadOnly("RelationshipOfficerDialog_rOfficerIsActive"));
		this.dateOfJoin.setReadonly(isReadOnly("RelationshipOfficerDialog_dateOfJoin"));
		this.grade.setReadonly(isReadOnly("RelationshipOfficerDialog_grade"));
		this.mobileNO.setReadonly(isReadOnly("RelationshipOfficerDialog_mobileNO"));
		this.genDesignation.setReadonly(isReadOnly("RelationshipOfficerDialog_genDesignation"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.relationshipOfficer.isNewRecord()) {
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
		this.rOfficerCode.setReadonly(true);
		this.rOfficerDesc.setReadonly(true);
		this.rOfficerDeptCode.setReadonly(true);
		this.rOfficerIsActive.setDisabled(true);
		this.dateOfJoin.setReadonly(true);
		this.grade.setReadonly(true);
		this.mobileNO.setReadonly(true);
		this.genDesignation.setReadonly(true);

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

		this.rOfficerCode.setValue("");
		this.rOfficerDesc.setValue("");
		this.rOfficerDeptCode.setValue("");
		this.rOfficerDeptCode.setDescription("");
		this.rOfficerIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final RelationshipOfficer aRelationshipOfficer = new RelationshipOfficer();
		BeanUtils.copyProperties(getRelationshipOfficer(), aRelationshipOfficer);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the RelationshipOfficer object with the components data
		doWriteComponentsToBean(aRelationshipOfficer);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aRelationshipOfficer.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aRelationshipOfficer.getRecordType())) {
				aRelationshipOfficer.setVersion(aRelationshipOfficer.getVersion() + 1);
				if (isNew) {
					aRelationshipOfficer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aRelationshipOfficer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aRelationshipOfficer.setNewRecord(true);
				}
			}
		} else {
			aRelationshipOfficer.setVersion(aRelationshipOfficer.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aRelationshipOfficer, tranType)) {
				doWriteBeanToComponents(aRelationshipOfficer);
				refreshList();
				closeDialog();
			}

		}  catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aRelationshipOfficer
	 *            (RelationshipOfficer)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(RelationshipOfficer aRelationshipOfficer,
			String tranType) {
		logger.debug("Enterring");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aRelationshipOfficer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aRelationshipOfficer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRelationshipOfficer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aRelationshipOfficer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRelationshipOfficer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aRelationshipOfficer);
				}

				if (isNotesMandatory(taskId, aRelationshipOfficer)) {
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

			aRelationshipOfficer.setTaskId(taskId);
			aRelationshipOfficer.setNextTaskId(nextTaskId);
			aRelationshipOfficer.setRoleCode(getRole());
			aRelationshipOfficer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRelationshipOfficer, tranType);
			String operationRefs = getServiceOperations(taskId, aRelationshipOfficer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aRelationshipOfficer, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aRelationshipOfficer, tranType);
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
		logger.debug("Enterring");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		RelationshipOfficer aRelationshipOfficer = (RelationshipOfficer) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getRelationshipOfficerService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getRelationshipOfficerService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getRelationshipOfficerService().doApprove(auditHeader);

						if (aRelationshipOfficer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getRelationshipOfficerService().doReject(auditHeader);
						if (aRelationshipOfficer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RelationshipOfficerDialog,	auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RelationshipOfficerDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.relationshipOfficer), true);
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
	private AuditHeader getAuditHeader(RelationshipOfficer aRelationshipOfficer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aRelationshipOfficer.getBefImage(), aRelationshipOfficer);
		return new AuditHeader(aRelationshipOfficer.getROfficerCode(), null,
				null, null, auditDetail, aRelationshipOfficer.getUserDetails(),	getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RelationshipOfficerDialog, auditHeader);
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
     doShowNotes(this.relationshipOfficer);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getRelationshipOfficerListCtrl().search();
	}


	@Override
	protected String getReference() {
		return String.valueOf(this.relationshipOfficer.getROfficerCode());
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

	public RelationshipOfficer getRelationshipOfficer() {
		return this.relationshipOfficer;
	}
	public void setRelationshipOfficer(RelationshipOfficer relationshipOfficer) {
		this.relationshipOfficer = relationshipOfficer;
	}

	public void setRelationshipOfficerService(
			RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}
	public RelationshipOfficerService getRelationshipOfficerService() {
		return this.relationshipOfficerService;
	}

	public void setRelationshipOfficerListCtrl(RelationshipOfficerListCtrl relationshipOfficerListCtrl) {
		this.relationshipOfficerListCtrl = relationshipOfficerListCtrl;
	}
	public RelationshipOfficerListCtrl getRelationshipOfficerListCtrl() {
		return this.relationshipOfficerListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

}
