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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/RelationshipOfficer/relationshipOfficerDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class RelationshipOfficerDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7675063106991743780L;
	private final static Logger logger = Logger.getLogger(RelationshipOfficerDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_RelationshipOfficerDialog; 	// autoWired
	protected Textbox rOfficerCode; 					// autoWired
	protected Textbox rOfficerDesc; 					// autoWired
	protected ExtendedCombobox rOfficerDeptCode; 				// autoWired
	protected Checkbox rOfficerIsActive; 				// autoWired

	protected Label recordStatus; 						// autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;


	// not auto wired variables
	private RelationshipOfficer relationshipOfficer; 						   // overHanded per parameter
	private RelationshipOfficer prvRelationshipOfficer; 					   // overHanded per parameter
	private transient RelationshipOfficerListCtrl relationshipOfficerListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String oldVar_rOfficerCode;
	private transient String oldVar_rOfficerDesc;
	private transient String oldVar_rOfficerDeptCode;
	private transient boolean oldVar_rOfficerIsActive;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_RelationshipOfficerDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 					// autoWire
	protected Button btnEdit; 					// autoWire
	protected Button btnDelete; 				// autoWire
	protected Button btnSave; 					// autoWire
	protected Button btnCancel; 				// autoWire
	protected Button btnClose;					// autoWire
	protected Button btnHelp; 					// autoWire
	protected Button btnNotes; 					// autoWire

	private transient String oldVar_lovDescROfficerDeptCodeName;

	// ServiceDAOs / Domain Classes
	private transient RelationshipOfficerService relationshipOfficerService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public RelationshipOfficerDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected RelationshipOfficer
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RelationshipOfficerDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("relationshipOfficer")) {
			this.relationshipOfficer = (RelationshipOfficer) args.get("relationshipOfficer");
			RelationshipOfficer befImage = new RelationshipOfficer();
			BeanUtils.copyProperties(this.relationshipOfficer, befImage);
			this.relationshipOfficer.setBefImage(befImage);

			setRelationshipOfficer(this.relationshipOfficer);
		} else {
			setRelationshipOfficer(null);
		}

		doLoadWorkFlow(this.relationshipOfficer.isWorkflow(), 
				this.relationshipOfficer.getWorkflowId(), this.relationshipOfficer.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "RelationshipOfficerDialog");
		}

		// READ OVERHANDED parameters !
		// we get the relationshipOfficerListWindow controller. So we have
		// access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete relationshipOfficer here.
		if (args.containsKey("relationshipOfficerListCtrl")) {
			setRelationshipOfficerListCtrl((RelationshipOfficerListCtrl) args.get("relationshipOfficerListCtrl"));
		} else {
			setRelationshipOfficerListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getRelationshipOfficer());
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
		
		this.rOfficerDeptCode.setMandatoryStyle(true);
		this.rOfficerDeptCode.setModuleName("Department");
		this.rOfficerDeptCode.setValueColumn("DeptCode");
		this.rOfficerDeptCode.setDescColumn("DeptDesc");
		this.rOfficerDeptCode.setValidateColumns(new String[]{"DeptCode"});

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

		getUserWorkspace().alocateAuthorities("RelationshipOfficerDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RelationshipOfficerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RelationshipOfficerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RelationshipOfficerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RelationshipOfficerDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_RelationshipOfficerDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
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
		PTMessageUtils.showHelpWindow(event, window_RelationshipOfficerDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_RelationshipOfficerDialog, "RelationshipOfficer");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
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

		if (aRelationshipOfficer.isNewRecord()) {
			this.rOfficerDeptCode.setDescription("");
		} else {
			this.rOfficerDeptCode.setDescription(aRelationshipOfficer.getLovDescROfficerDeptCodeName());
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
		doSetLOVValidation();

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

		doRemoveValidation();
		doRemoveLOVValidation();

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
	 * @throws InterruptedException
	 */
	public void doShowDialog(RelationshipOfficer aRelationshipOfficer) throws InterruptedException {
		logger.debug("Entering");

		// if aRelationshipOfficer == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aRelationshipOfficer == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aRelationshipOfficer = getRelationshipOfficerService().getNewRelationshipOfficer();

			setRelationshipOfficer(aRelationshipOfficer);
		} else {
			setRelationshipOfficer(aRelationshipOfficer);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aRelationshipOfficer.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.rOfficerCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.rOfficerDesc.focus();
				if (!StringUtils.trimToEmpty(aRelationshipOfficer.getRecordType()).equals("")) {
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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_RelationshipOfficerDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initialized values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_rOfficerCode = this.rOfficerCode.getValue();
		this.oldVar_rOfficerDesc = this.rOfficerDesc.getValue();
		this.oldVar_rOfficerDeptCode = this.rOfficerDeptCode.getValue();
		this.oldVar_lovDescROfficerDeptCodeName = this.rOfficerDeptCode.getDescription();
		this.oldVar_rOfficerIsActive = this.rOfficerIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialized values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.rOfficerCode.setValue(this.oldVar_rOfficerCode);
		this.rOfficerDesc.setValue(this.oldVar_rOfficerDesc);
		this.rOfficerDeptCode.setValue(this.oldVar_rOfficerDeptCode);
		this.rOfficerDeptCode.setDescription(this.oldVar_lovDescROfficerDeptCodeName);
		this.rOfficerIsActive.setChecked(this.oldVar_rOfficerIsActive);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_rOfficerDesc != this.rOfficerDesc.getValue()) {
			return true;
		}
		if (this.oldVar_rOfficerDeptCode != this.rOfficerDeptCode.getValue()) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Enterring");
		setValidationOn(true);

		if (!this.rOfficerCode.isReadonly()){
			this.rOfficerCode.setConstraint(new PTStringValidator(Labels.getLabel("label_RelationshipOfficerDialog_ROfficerCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.rOfficerDesc.isReadonly()){
			this.rOfficerDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_RelationshipOfficerDialog_ROfficerDesc.value"),
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
		this.rOfficerCode.setConstraint("");
		this.rOfficerDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		this.rOfficerDeptCode.setConstraint(new PTStringValidator(Labels.getLabel("label_RelationshipOfficerDialog_ROfficerDeptCode.value"), null, true));
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		this.rOfficerDeptCode.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.rOfficerCode.setErrorMessage("");
		this.rOfficerDesc.setErrorMessage("");
		this.rOfficerDeptCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aRelationshipOfficer.getROfficerCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aRelationshipOfficer.getRecordType()).equals("")) {
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
					closeDialog(this.window_RelationshipOfficerDialog, "RelationshipOfficer");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new RelationshipOfficer object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final RelationshipOfficer aRelationshipOfficer = getRelationshipOfficerService().getNewRelationshipOfficer();
		aRelationshipOfficer.setNewRecord(true);
		setRelationshipOfficer(aRelationshipOfficer);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.rOfficerCode.focus();
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
		// remember the old variables
		doStoreInitValues();
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aRelationshipOfficer.getRecordType()).equals("")) {
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
				closeDialog(this.window_RelationshipOfficerDialog, "RelationshipOfficer");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
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

		aRelationshipOfficer.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aRelationshipOfficer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRelationshipOfficer.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aRelationshipOfficer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRelationshipOfficer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aRelationshipOfficer);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aRelationshipOfficer))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aRelationshipOfficer.setTaskId(taskId);
			aRelationshipOfficer.setNextTaskId(nextTaskId);
			aRelationshipOfficer.setRoleCode(getRole());
			aRelationshipOfficer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRelationshipOfficer, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aRelationshipOfficer);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
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
						auditHeader.setErrorDetails(new ErrorDetails(
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
						deleteNotes(getNotes(), true);
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
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events ++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onFullfill$rOfficerDeptCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = rOfficerDeptCode.getObject(); 
		if (dataObject instanceof String) {
			this.rOfficerDeptCode.setValue(dataObject.toString());
			this.rOfficerDeptCode.setDescription("");
		} else {
			Department details = (Department) dataObject;
			if (details != null) {
				this.rOfficerDeptCode.setValue(details.getDeptCode());
				this.rOfficerDeptCode.setDescription(details.getDeptDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RelationshipOfficerDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,	map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		final JdbcSearchObject<RelationshipOfficer> soRelationshipOfficer = getRelationshipOfficerListCtrl().getSearchObj();
		getRelationshipOfficerListCtrl().pagingRelationshipOfficerList.setActivePage(0);
		getRelationshipOfficerListCtrl().getPagedListWrapper().setSearchObject(soRelationshipOfficer);
		if (getRelationshipOfficerListCtrl().listBoxRelationshipOfficer != null) {
			getRelationshipOfficerListCtrl().listBoxRelationshipOfficer.getListModel();
		}
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("RelationshipOfficer");
		notes.setReference(getRelationshipOfficer().getROfficerCode());
		notes.setVersion(getRelationshipOfficer().getVersion());
		return notes;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public RelationshipOfficer getPrvRelationshipOfficer() {
		return prvRelationshipOfficer;
	}

}
