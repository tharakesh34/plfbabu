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
 * FileName    		:  RatingTypeDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.bmtmasters.ratingtype;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.RatingTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/RatingType/ratingTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class RatingTypeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8645816104609479355L;
	private final static Logger logger = Logger.getLogger(RatingTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RatingTypeDialog; 		// autoWired
	protected Textbox 		ratingType; 					// autoWired
	protected Textbox 		ratingTypeDesc; 				// autoWired
	protected Checkbox 		valueType; 						// autoWired
	protected Intbox 		valueLen; 						// autoWired
	protected Checkbox 		ratingIsActive; 				// autoWired

	protected Label 		recordStatus; 					// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired variables
	private RatingType 						mRatingTytpe; 		// overHanded per parameter
	private transient RatingTypeListCtrl    ratingTypeListCtrl; // overHanded per
	// parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 	oldVar_ratingType;
	private transient String 	oldVar_ratingTypeDesc;
	private transient boolean 	oldVar_valueType;
	private transient int 		oldVar_valueLen;
	private transient boolean 	oldVar_ratingIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean 	validationOn;
	private boolean 			notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String 		btnCtroller_ClassPrefix = "button_RatingTypeDialog_";
	private transient ButtonStatusCtrl 	btnCtrl;
	protected Button 	btnNew; 	// autoWire
	protected Button 	btnEdit; 	// autoWire
	protected Button	btnDelete; 	// autoWire
	protected Button 	btnSave; 	// autoWire
	protected Button 	btnCancel; 	// autoWire
	protected Button 	btnClose; 	// autoWire
	protected Button 	btnHelp; 	// autoWire
	protected Button 	btnNotes; 	// autoWire

	// ServiceDAOs / Domain Classes
	private transient RatingTypeService ratingTypeService;
	private transient PagedListService  pagedListService;

	/**
	 * default constructor.<br>
	 */
	public RatingTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected RatingType object in a
	 * Map.
	 * 
	 * @parameter event
	 * @throws Exception
	 */
	public void onCreate$window_RatingTypeDialog(Event event) throws Exception {
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
		if (args.containsKey("ratingType")) {
			this.mRatingTytpe = (RatingType) args.get("ratingType");
			RatingType befImage = new RatingType();
			BeanUtils.copyProperties(this.mRatingTytpe, befImage);
			this.mRatingTytpe.setBefImage(befImage);
			setMRatingType(this.mRatingTytpe);
		} else {
			setMRatingType(null);
		}

		doLoadWorkFlow(this.mRatingTytpe.isWorkflow(),this.mRatingTytpe.getWorkflowId(),this.mRatingTytpe.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"RatingTypeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the ratingTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete ratingType here.
		if (args.containsKey("ratingTypeListCtrl")) {
			setRatingTypeListCtrl((RatingTypeListCtrl) args.get("ratingTypeListCtrl"));
		} else {
			setRatingTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getMRatingType());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.ratingType.setMaxlength(8);
		this.ratingTypeDesc.setMaxlength(50);
		this.valueLen.setMaxlength(3);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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

		getUserWorkspace().alocateAuthorities("RatingTypeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RatingTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RatingTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RatingTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RatingTypeDialog_btnSave"));
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
	public void onClose$window_RatingTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		// remember the old variables
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_RatingTypeDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
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
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeDialog(this.window_RatingTypeDialog, "RatingType");
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
	 * @param aRatingType
	 *            RatingType
	 */
	public void doWriteBeanToComponents(RatingType aRatingType) {
		logger.debug("Entering");
		this.ratingType.setValue(aRatingType.getRatingType());
		this.ratingTypeDesc.setValue(aRatingType.getRatingTypeDesc());
		this.valueType.setChecked(aRatingType.isValueType());
		this.valueLen.setValue(aRatingType.getValueLen());
		this.ratingIsActive.setChecked(aRatingType.isRatingIsActive());
		this.recordStatus.setValue(aRatingType.getRecordStatus());
		
		if(aRatingType.isNew() || aRatingType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.ratingIsActive.setChecked(true);
			this.ratingIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRatingType
	 */
	public void doWriteComponentsToBean(RatingType aRatingType) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aRatingType.setRatingType(this.ratingType.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingType.setRatingTypeDesc(this.ratingTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingType.setValueType(this.valueType.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingType.setValueLen(this.valueLen.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRatingType.setRatingIsActive(this.ratingIsActive.isChecked());
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

		aRatingType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aRatingType
	 * @throws InterruptedException
	 */
	public void doShowDialog(RatingType aRatingType)throws InterruptedException {
		logger.debug("Entering");

		// if aRatingType == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aRatingType == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aRatingType = getRatingTypeService().getNewRatingType();
			setMRatingType(aRatingType);
		} else {
			setMRatingType(aRatingType);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aRatingType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ratingType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.ratingTypeDesc.focus();
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
			doWriteBeanToComponents(aRatingType);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_RatingTypeDialog);
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_ratingType = this.ratingType.getValue();
		this.oldVar_ratingTypeDesc = this.ratingTypeDesc.getValue();
		this.oldVar_valueType = this.valueType.isChecked();
		this.oldVar_valueLen = this.valueLen.intValue();
		this.oldVar_ratingIsActive = this.ratingIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.ratingType.setValue(this.oldVar_ratingType);
		this.ratingTypeDesc.setValue(this.oldVar_ratingTypeDesc);
		this.valueType.setChecked(this.oldVar_valueType);
		this.valueLen.setValue(this.oldVar_valueLen);
		this.ratingIsActive.setChecked(this.oldVar_ratingIsActive);
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

		if(this.oldVar_ratingType != this.ratingType.getValue()){
			return true;
		}
		if (this.oldVar_ratingTypeDesc != this.ratingTypeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_valueType != this.valueType.isChecked()) {
			return true;
		}
		if (this.oldVar_valueLen != this.valueLen.intValue()) {
			return true;
		}
		if (this.oldVar_ratingIsActive != this.ratingIsActive.isChecked()) {
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

		if (!this.ratingType.isReadonly()){
			this.ratingType.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingTypeDialog_RatingType.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}			
		if (!this.ratingTypeDesc.isReadonly()){
			this.ratingTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_RatingTypeDialog_RatingTypeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.valueLen.isReadonly()) {
			this.valueLen.setConstraint(new IntValidator(3, 
					Labels.getLabel("label_RatingTypeDialog_ValueLen.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.ratingType.setConstraint("");
		this.ratingTypeDesc.setConstraint("");
		this.valueLen.setConstraint("");
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
		this.ratingType.setErrorMessage("");
		this.ratingTypeDesc.setErrorMessage("");
		this.valueLen.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a RatingType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final RatingType aRatingType = new RatingType();
		BeanUtils.copyProperties(getMRatingType(), aRatingType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + aRatingType.getRatingType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aRatingType.getRecordType()).equals("")) {
				aRatingType.setVersion(aRatingType.getVersion() + 1);
				aRatingType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aRatingType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aRatingType, tranType)) {
					refreshList();
					closeDialog(this.window_RatingTypeDialog, "RatingType");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new RatingType object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new RatingType() in the frontEnd.
		// we get it from the backEnd.
		final RatingType aRatingType = getRatingTypeService()
		.getNewRatingType();
		aRatingType.setValueLen(0); // initialize
		aRatingType.setNewRecord(true);
		setMRatingType(aRatingType);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.ratingType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getMRatingType().isNewRecord()) {
			this.ratingType.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.ratingType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.ratingTypeDesc.setReadonly(isReadOnly("RatingTypeDialog_ratingTypeDesc"));
		this.valueType.setDisabled(isReadOnly("RatingTypeDialog_valueType"));
		this.valueLen.setReadonly(isReadOnly("RatingTypeDialog_valueLen"));
		this.ratingIsActive.setDisabled(isReadOnly("RatingTypeDialog_ratingIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.mRatingTytpe.isNewRecord()) {
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
		this.ratingType.setReadonly(true);
		this.ratingTypeDesc.setReadonly(true);
		this.valueType.setDisabled(true);
		this.valueLen.setReadonly(true);
		this.ratingIsActive.setDisabled(true);

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
		this.ratingType.setValue("");
		this.ratingTypeDesc.setValue("");
		this.valueType.setChecked(false);
		this.valueLen.setText("");
		this.ratingIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final RatingType aRatingType = new RatingType();
		BeanUtils.copyProperties(getMRatingType(), aRatingType);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the RatingType object with the components data
		doWriteComponentsToBean(aRatingType);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aRatingType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aRatingType.getRecordType()).equals("")) {
				aRatingType.setVersion(aRatingType.getVersion() + 1);
				if (isNew) {
					aRatingType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aRatingType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aRatingType.setNewRecord(true);
				}
			}
		} else {
			aRatingType.setVersion(aRatingType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aRatingType, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_RatingTypeDialog, "RatingType");
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
	 * @param aRatingType
	 *            (RatingType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(RatingType aRatingType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aRatingType.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aRatingType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRatingType.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aRatingType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRatingType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,aRatingType);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aRatingType))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
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

			aRatingType.setTaskId(taskId);
			aRatingType.setNextTaskId(nextTaskId);
			aRatingType.setRoleCode(getRole());
			aRatingType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRatingType, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aRatingType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aRatingType,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aRatingType, tranType);
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
		RatingType aRatingType = (RatingType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getRatingTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getRatingTypeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getRatingTypeService().doApprove(auditHeader);

						if (aRatingType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getRatingTypeService().doReject(auditHeader);

						if (aRatingType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_RatingTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RatingTypeDialog, auditHeader);
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
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ WorkFlow Details +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * Get Audit Header Details
	 * 
	 * @param aRatingType
	 *            (RatingType)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(RatingType aRatingType, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aRatingType.getBefImage(), aRatingType);
		return new AuditHeader(String.valueOf(aRatingType.getId()), null, null,
				null, auditDetail, aRatingType.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RatingTypeDialog,auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		final JdbcSearchObject<RatingType> soRatingType = getRatingTypeListCtrl().getSearchObj();
		getRatingTypeListCtrl().pagingRatingTypeList.setActivePage(0);
		getRatingTypeListCtrl().getPagedListWrapper().setSearchObject(soRatingType);
		if (getRatingTypeListCtrl().listBoxRatingType != null) {
			getRatingTypeListCtrl().listBoxRatingType.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("RatingType");
		notes.setReference(getMRatingType().getRatingType());
		notes.setVersion(getMRatingType().getVersion());
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

	public RatingType getMRatingType() {
		return this.mRatingTytpe;
	}
	public void setMRatingType(RatingType rating) {
		this.mRatingTytpe = rating;
	}

	public void setRatingTypeService(RatingTypeService ratingTypeService) {
		this.ratingTypeService = ratingTypeService;
	}
	public RatingTypeService getRatingTypeService() {
		return this.ratingTypeService;
	}

	public void setRatingTypeListCtrl(RatingTypeListCtrl ratingTypeListCtrl) {
		this.ratingTypeListCtrl = ratingTypeListCtrl;
	}
	public RatingTypeListCtrl getRatingTypeListCtrl() {
		return this.ratingTypeListCtrl;
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

}
