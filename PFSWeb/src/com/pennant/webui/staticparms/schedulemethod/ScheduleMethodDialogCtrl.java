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
 * FileName    		:  ScheduleMethodDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.staticparms.schedulemethod;

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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.staticparms.ScheduleMethodService;
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
 * /WEB-INF/pages/StaticParms/ScheduleMethod/scheduleMethodDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ScheduleMethodDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8452766351018963227L;
	private final static Logger logger = Logger.getLogger(ScheduleMethodDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_ScheduleMethodDialog; 	// autoWired
	protected Textbox 	schdMethod; 					// autoWired
	protected Textbox 	schdMethodDesc; 				// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	

	// not auto wired variables
	private ScheduleMethod scheduleMethod; 								// overHanded per parameter
	private ScheduleMethod prvScheduleMethod; 							// overHanded per parameter
	private transient ScheduleMethodListCtrl scheduleMethodListCtrl; 	// overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String oldVar_schdMethod;
	private transient String oldVar_schdMethodDesc;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ScheduleMethodDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire

	// ServiceDAOs / Domain Classes
	private transient ScheduleMethodService scheduleMethodService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public ScheduleMethodDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ScheduleMethod object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScheduleMethodDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("scheduleMethod")) {
			this.scheduleMethod = (ScheduleMethod) args.get("scheduleMethod");
			ScheduleMethod befImage = new ScheduleMethod();
			BeanUtils.copyProperties(this.scheduleMethod, befImage);
			this.scheduleMethod.setBefImage(befImage);

			setScheduleMethod(this.scheduleMethod);
		} else {
			setScheduleMethod(null);
		}

		doLoadWorkFlow(this.scheduleMethod.isWorkflow(),
				this.scheduleMethod.getWorkflowId(),
				this.scheduleMethod.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"ScheduleMethodDialog");
		}

		// READ OVERHANDED parameters !
		// we get the scheduleMethodListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete scheduleMethod here.
		if (args.containsKey("scheduleMethodListCtrl")) {
			setScheduleMethodListCtrl((ScheduleMethodListCtrl) args.get("scheduleMethodListCtrl"));
		} else {
			setScheduleMethodListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getScheduleMethod());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.schdMethod.setMaxlength(8);
		this.schdMethodDesc.setMaxlength(50);

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

		getUserWorkspace().alocateAuthorities("ScheduleMethodDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ScheduleMethodDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ScheduleMethodDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ScheduleMethodDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ScheduleMethodDialog_btnSave"));
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
	public void onClose$window_ScheduleMethodDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
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
		PTMessageUtils.showHelpWindow(event, window_ScheduleMethodDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving");
	}

	// GUI Process

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
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_ScheduleMethodDialog, "ScheduleMethod");
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
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aScheduleMethod
	 *            ScheduleMethod
	 */
	public void doWriteBeanToComponents(ScheduleMethod aScheduleMethod) {
		logger.debug("Entering");
		this.schdMethod.setValue(aScheduleMethod.getSchdMethod());
		this.schdMethodDesc.setValue(aScheduleMethod.getSchdMethodDesc());
		this.recordStatus.setValue(aScheduleMethod.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aScheduleMethod
	 */
	public void doWriteComponentsToBean(ScheduleMethod aScheduleMethod) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aScheduleMethod.setSchdMethod(this.schdMethod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aScheduleMethod.setSchdMethodDesc(this.schdMethodDesc.getValue());
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

		aScheduleMethod.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aScheduleMethod
	 * @throws InterruptedException
	 */
	public void doShowDialog(ScheduleMethod aScheduleMethod)throws InterruptedException {
		logger.debug("Entering");

		// if aScheduleMethod == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aScheduleMethod == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aScheduleMethod = getScheduleMethodService().getNewScheduleMethod();

			setScheduleMethod(aScheduleMethod);
		} else {
			setScheduleMethod(aScheduleMethod);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aScheduleMethod.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.schdMethod.focus();
		} else {
			this.schdMethodDesc.focus();
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
			doWriteBeanToComponents(aScheduleMethod);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ScheduleMethodDialog);
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
	 * Stores the initialize values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_schdMethod = this.schdMethod.getValue();
		this.oldVar_schdMethodDesc = this.schdMethodDesc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.schdMethod.setValue(this.oldVar_schdMethod);
		this.schdMethodDesc.setValue(this.oldVar_schdMethodDesc);
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
		// To clear the Error Messages
		doClearMessage();

		if(this.oldVar_schdMethod != this.schdMethod.getValue()){
			return true;
		}
		if (this.oldVar_schdMethodDesc != this.schdMethodDesc.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.schdMethod.isReadonly()){
			this.schdMethod.setConstraint(new PTStringValidator(Labels.getLabel("label_ScheduleMethodDialog_SchdMethod.value"),PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}	
		if (!this.schdMethodDesc.isReadonly()){
			this.schdMethodDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_ScheduleMethodDialog_SchdMethodDesc.value"), 
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
		this.schdMethod.setConstraint("");
		this.schdMethodDesc.setConstraint("");
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
	private void doClearMessage() {
		logger.debug("Entering");
		this.schdMethod.setErrorMessage("");
		this.schdMethodDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a ScheduleMethod object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final ScheduleMethod aScheduleMethod = new ScheduleMethod();
		BeanUtils.copyProperties(getScheduleMethod(), aScheduleMethod);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + aScheduleMethod.getSchdMethod();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aScheduleMethod.getRecordType()).equals("")) {
				aScheduleMethod.setVersion(aScheduleMethod.getVersion() + 1);
				aScheduleMethod.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aScheduleMethod.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aScheduleMethod, tranType)) {
					refreshList();
					closeDialog(this.window_ScheduleMethodDialog,
					"ScheduleMethod");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new ScheduleMethod object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		final ScheduleMethod aScheduleMethod = getScheduleMethodService().getNewScheduleMethod();
		aScheduleMethod.setNewRecord(true);
		setScheduleMethod(aScheduleMethod);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.schdMethod.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getScheduleMethod().isNewRecord()) {
			this.schdMethod.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.schdMethod.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.schdMethodDesc.setReadonly(isReadOnly("ScheduleMethodDialog_schdMethodDesc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.scheduleMethod.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
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

		this.schdMethod.setReadonly(true);
		this.schdMethodDesc.setReadonly(true);

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
		this.schdMethod.setValue("");
		this.schdMethodDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final ScheduleMethod aScheduleMethod = new ScheduleMethod();
		BeanUtils.copyProperties(getScheduleMethod(), aScheduleMethod);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the ScheduleMethod object with the components data
		doWriteComponentsToBean(aScheduleMethod);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aScheduleMethod.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aScheduleMethod.getRecordType())
					.equals("")) {
				aScheduleMethod.setVersion(aScheduleMethod.getVersion() + 1);
				if (isNew) {
					aScheduleMethod
					.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aScheduleMethod
					.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aScheduleMethod.setNewRecord(true);
				}
			}
		} else {
			aScheduleMethod.setVersion(aScheduleMethod.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aScheduleMethod, tranType)) {
				doWriteBeanToComponents(aScheduleMethod);
				refreshList();
				closeDialog(this.window_ScheduleMethodDialog, "ScheduleMethod");
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
	 * @param aScheduleMethod
	 *            (ScheduleMethod)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ScheduleMethod aScheduleMethod, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aScheduleMethod.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aScheduleMethod.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aScheduleMethod.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aScheduleMethod.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aScheduleMethod.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,aScheduleMethod);
				}
				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aScheduleMethod))) {
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

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
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

			aScheduleMethod.setTaskId(taskId);
			aScheduleMethod.setNextTaskId(nextTaskId);
			aScheduleMethod.setRoleCode(getRole());
			aScheduleMethod.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aScheduleMethod, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aScheduleMethod);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aScheduleMethod,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aScheduleMethod, tranType);
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

		ScheduleMethod aScheduleMethod = (ScheduleMethod) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getScheduleMethodService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getScheduleMethodService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getScheduleMethodService().doApprove(auditHeader);
						if (aScheduleMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getScheduleMethodService().doReject(auditHeader);
						if (aScheduleMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_ScheduleMethodDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ScheduleMethodDialog, auditHeader);
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
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aScheduleMethod
	 *            (ScheduleMethod)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(ScheduleMethod aScheduleMethod,String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aScheduleMethod.getBefImage(), aScheduleMethod);
		return new AuditHeader(aScheduleMethod.getSchdMethod(), null, null,
				null, auditDetail, aScheduleMethod.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_ScheduleMethodDialog,auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<ScheduleMethod> soScheduleMethod = getScheduleMethodListCtrl().getSearchObj();
		getScheduleMethodListCtrl().pagingScheduleMethodList.setActivePage(0);
		getScheduleMethodListCtrl().getPagedListWrapper().setSearchObject(soScheduleMethod);
		if (getScheduleMethodListCtrl().listBoxScheduleMethod != null) {
			getScheduleMethodListCtrl().listBoxScheduleMethod.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the entered Notes for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("ScheduleMethod");
		notes.setReference(getScheduleMethod().getSchdMethod());
		notes.setVersion(getScheduleMethod().getVersion());
		logger.debug("Leaving");
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

	public ScheduleMethod getScheduleMethod() {
		return this.scheduleMethod;
	}
	public void setScheduleMethod(ScheduleMethod scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public void setScheduleMethodService(ScheduleMethodService scheduleMethodService) {
		this.scheduleMethodService = scheduleMethodService;
	}
	public ScheduleMethodService getScheduleMethodService() {
		return this.scheduleMethodService;
	}

	public void setScheduleMethodListCtrl(ScheduleMethodListCtrl scheduleMethodListCtrl) {
		this.scheduleMethodListCtrl = scheduleMethodListCtrl;
	}
	public ScheduleMethodListCtrl getScheduleMethodListCtrl() {
		return this.scheduleMethodListCtrl;
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
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public ScheduleMethod getPrvScheduleMethod() {
		return prvScheduleMethod;
	}
}
