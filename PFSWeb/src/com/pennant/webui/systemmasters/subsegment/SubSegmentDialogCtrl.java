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
 * FileName    		:  SubSegmentDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.subsegment;

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

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.SubSegmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/SubSegment/subSegmentDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SubSegmentDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3976608317795122426L;
	private final static Logger logger = Logger.getLogger(SubSegmentDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_SubSegmentDialog;// autoWired
	protected Textbox 	segmentCode; 			// autoWired
	protected Textbox 	subSegmentCode; 		// autoWired
	protected Textbox 	subSegmentDesc; 		// autoWired
	protected Checkbox 	subSegmentIsActive; 	// autoWired

	protected Label 		recordStatus; 		// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not autoWired variables
	private SubSegment subSegment; // overHanded per parameter
	private transient SubSegmentListCtrl subSegmentListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_segmentCode;
	private transient String  		oldVar_subSegmentCode;
	private transient String  		oldVar_subSegmentDesc;
	private transient boolean  		oldVar_subSegmentIsActive;
	private transient String 		oldVar_recordStatus;

	private transient boolean 	validationOn;
	private boolean 			notes_Entered=false;


	private transient final String 		btnCtroller_ClassPrefix = "button_SubSegmentDialog_";
	private transient ButtonStatusCtrl 	btnCtrl;
	// Button controller for the CRUD buttons
	protected Button btnNew; 			// autoWire
	protected Button btnEdit; 			// autoWire
	protected Button btnDelete; 		// autoWire
	protected Button btnSave; 			// autoWire
	protected Button btnCancel; 		// autoWire
	protected Button btnClose; 			// autoWire
	protected Button btnHelp; 			// autoWire
	protected Button btnNotes; 			// autoWire

	protected Button btnSearchSegmentCode; 	// autoWire
	protected Textbox lovDescSegmentCodeName;
	private transient String oldVar_lovDescSegmentCodeName;

	// ServiceDAOs / Domain Classes
	private transient SubSegmentService subSegmentService;
	private transient PagedListService 	pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SubSegmentDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSegment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SubSegmentDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are OVERHANDED by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("subSegment")) {
			this.subSegment = (SubSegment) args.get("subSegment");
			SubSegment befImage = new SubSegment();
			BeanUtils.copyProperties(this.subSegment, befImage);
			this.subSegment.setBefImage(befImage);

			setSubSegment(this.subSegment);
		} else {
			setSubSegment(null);
		}

		doLoadWorkFlow(this.subSegment.isWorkflow(), this.subSegment.getWorkflowId(), this.subSegment.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SubSegmentDialog");
		}

		// READ OVERHANDED parameters !
		// we get the subSegmentListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete subSegment here.
		if (args.containsKey("subSegmentListCtrl")) {
			setSubSegmentListCtrl((SubSegmentListCtrl) args.get("subSegmentListCtrl"));
		} else {
			setSubSegmentListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSubSegment());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.segmentCode.setMaxlength(8);
		this.subSegmentCode.setMaxlength(8);
		this.subSegmentDesc.setMaxlength(50);

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
		getUserWorkspace().alocateAuthorities("SubSegmentDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SubSegmentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SubSegmentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SubSegmentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SubSegmentDialog_btnSave"));
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
	public void onClose$window_SubSegmentDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_SubSegmentDialog);
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
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_SubSegmentDialog, "SubSegment");
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
	 * @param aSubSegment
	 *            (SubSegment)
	 * 
	 */
	public void doWriteBeanToComponents(SubSegment aSubSegment) {
		logger.debug("Entering");
		this.segmentCode.setValue(aSubSegment.getSegmentCode());
		this.subSegmentCode.setValue(aSubSegment.getSubSegmentCode());
		this.subSegmentDesc.setValue(aSubSegment.getSubSegmentDesc());
		this.subSegmentIsActive.setChecked(aSubSegment.isSubSegmentIsActive());
		
		if (aSubSegment.isNewRecord()) {
			this.lovDescSegmentCodeName.setValue("");
		} else {
			this.lovDescSegmentCodeName.setValue(aSubSegment.getSegmentCode() + "-" + aSubSegment.getLovDescSegmentCodeName());
		}
		
		this.recordStatus.setValue(aSubSegment.getRecordStatus());
		
		if(aSubSegment.isNew() || aSubSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.subSegmentIsActive.setChecked(true);
			this.subSegmentIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSubSegment
	 */
	public void doWriteComponentsToBean(SubSegment aSubSegment) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSubSegment.setLovDescSegmentCodeName(this.lovDescSegmentCodeName.getValue());
			aSubSegment.setSegmentCode(StringUtils.strip(this.segmentCode.getValue().toUpperCase()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSegment.setSubSegmentCode(StringUtils.strip(this.subSegmentCode.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSegment.setSubSegmentDesc(StringUtils.strip(this.subSegmentDesc.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSegment.setSubSegmentIsActive(this.subSegmentIsActive.isChecked());
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
		aSubSegment.setRecordStatus(this.recordStatus.getValue());
		setSubSegment(aSubSegment);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSubSegment
	 * @throws InterruptedException
	 */
	public void doShowDialog(SubSegment aSubSegment) throws InterruptedException {
		logger.debug("Entering");
		// if aSubSegment == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSubSegment == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aSubSegment = getSubSegmentService().getNewSubSegment();
			setSubSegment(aSubSegment);
		} else {
			setSubSegment(aSubSegment);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSubSegment.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.lovDescSegmentCodeName.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.subSegmentDesc.focus();
				if (!StringUtils.trimToEmpty(aSubSegment.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			this.subSegmentCode.setReadonly(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSubSegment);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SubSegmentDialog);
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
		this.oldVar_segmentCode = this.segmentCode.getValue();
		this.oldVar_lovDescSegmentCodeName = this.lovDescSegmentCodeName.getValue();
		this.oldVar_subSegmentCode = this.subSegmentCode.getValue();
		this.oldVar_subSegmentDesc = this.subSegmentDesc.getValue();
		this.oldVar_subSegmentIsActive = this.subSegmentIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.segmentCode.setValue(this.oldVar_segmentCode);
		this.lovDescSegmentCodeName.setValue(this.oldVar_lovDescSegmentCodeName);
		this.subSegmentCode.setValue(this.oldVar_subSegmentCode);
		this.subSegmentDesc.setValue(this.oldVar_subSegmentDesc);
		this.subSegmentIsActive.setChecked(this.oldVar_subSegmentIsActive);
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

		if (this.oldVar_segmentCode != this.segmentCode.getValue()) {
			return true;
		}
		if (this.oldVar_subSegmentCode != this.subSegmentCode.getValue()) {
			return true;
		}
		if (this.oldVar_subSegmentDesc != this.subSegmentDesc.getValue()) {
			return true;
		}
		if (this.oldVar_subSegmentIsActive != this.subSegmentIsActive.isChecked()) {
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
		if (!this.subSegmentCode.isReadonly()){
			this.subSegmentCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSegmentDialog_SubSegmentCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	

		if (!this.subSegmentDesc.isReadonly()){
			this.subSegmentDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSegmentDialog_SubSegmentDesc.value"), 
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
		this.segmentCode.setConstraint("");
		this.subSegmentCode.setConstraint("");
		this.subSegmentDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescSegmentCodeName.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSegmentDialog_SegmentCode.value"), null, true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescSegmentCodeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.segmentCode.setErrorMessage("");
		this.subSegmentCode.setErrorMessage("");
		this.subSegmentDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Deletes a SubSegment object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SubSegment aSubSegment = new SubSegment();
		BeanUtils.copyProperties(getSubSegment(), aSubSegment);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aSubSegment.getSegmentCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSubSegment.getRecordType()).equals("")) {
				aSubSegment.setVersion(aSubSegment.getVersion() + 1);
				aSubSegment.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSubSegment.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSubSegment, tranType)) {
					refreshList();
					closeDialog(this.window_SubSegmentDialog, "SubSegment");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new SubSegment object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new SubSegment() in the frontEnd.
		// we get it from the backEnd.
		final SubSegment aSubSegment = getSubSegmentService().getNewSubSegment();
		aSubSegment.setNewRecord(true);
		setSubSegment(aSubSegment);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.segmentCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {

		logger.debug("Entering");
		if (getSubSegment().isNewRecord()) {
			this.segmentCode.setReadonly(true);
			this.subSegmentCode.setReadonly(false);
			this.btnSearchSegmentCode.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.segmentCode.setReadonly(true);
			this.subSegmentCode.setReadonly(true);
			this.btnSearchSegmentCode.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.subSegmentDesc.setReadonly(isReadOnly("SubSegmentDialog_subSegmentDesc"));
		this.subSegmentIsActive.setDisabled(isReadOnly("SubSegmentDialog_subSegmentIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.subSegment.isNewRecord()) {
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
		this.btnSearchSegmentCode.setDisabled(true);
		this.subSegmentCode.setReadonly(true);
		this.subSegmentDesc.setReadonly(true);
		this.subSegmentIsActive.setDisabled(true);

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
		this.segmentCode.setValue("");
		this.lovDescSegmentCodeName.setValue("");
		this.subSegmentCode.setValue("");
		this.subSegmentDesc.setValue("");
		this.subSegmentIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {

		logger.debug("Entering");
		final SubSegment aSubSegment = new SubSegment();
		BeanUtils.copyProperties(getSubSegment(), aSubSegment);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SubSegment object with the components data
		doWriteComponentsToBean(aSubSegment);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSubSegment.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSubSegment.getRecordType()).equals("")) {
				aSubSegment.setVersion(aSubSegment.getVersion() + 1);
				if (isNew) {
					aSubSegment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSubSegment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSubSegment.setNewRecord(true);
				}
			}
		} else {
			aSubSegment.setVersion(aSubSegment.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSubSegment, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_SubSegmentDialog, "SubSegment");
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
	 * @param aSubSegment
	 *            (SubSegment)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(SubSegment aSubSegment, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSubSegment.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSubSegment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSubSegment.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aSubSegment.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSubSegment.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSubSegment);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aSubSegment))) {
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
			
			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
				
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

			aSubSegment.setTaskId(taskId);
			aSubSegment.setNextTaskId(nextTaskId);
			aSubSegment.setRoleCode(getRole());
			aSubSegment.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSubSegment, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aSubSegment);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSubSegment, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSubSegment, tranType);
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
		SubSegment aSubSegment = (SubSegment) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSubSegmentService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSubSegmentService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSubSegmentService().doApprove(auditHeader);

						if (aSubSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSubSegmentService().doReject(auditHeader);

						if (aSubSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SubSegmentDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SubSegmentDialog, auditHeader);
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
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchSegmentCode(Event event) {

		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_SubSegmentDialog, "Segment");
		if (dataObject instanceof String) {
			this.segmentCode.setValue(dataObject.toString());
			this.lovDescSegmentCodeName.setValue("");
		} else {
			Segment details = (Segment) dataObject;
			if (details != null) {
				this.segmentCode.setValue(details.getLovValue());
				this.lovDescSegmentCodeName.setValue(details.getLovValue() + "-" + details.getSegmentDesc());
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
	 * @param aSubSegment
	 *            (SubSegment)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(SubSegment aSubSegment, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSubSegment.getBefImage(), aSubSegment);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aSubSegment.getUserDetails(), getOverideMap());

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
			ErrorControl.showErrorControl(this.window_SubSegmentDialog, auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,	map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
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
		final JdbcSearchObject<SubSegment> soSubSegment = getSubSegmentListCtrl().getSearchObj();
		getSubSegmentListCtrl().pagingSubSegmentList.setActivePage(0);
		getSubSegmentListCtrl().getPagedListWrapper().setSearchObject(soSubSegment);
		if (getSubSegmentListCtrl().listBoxSubSegment != null) {
			getSubSegmentListCtrl().listBoxSubSegment.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("SubSegment");
		notes.setReference(getReference());
		notes.setVersion(getSubSegment().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getSubSegment().getSubSegmentCode()+PennantConstants.KEY_SEPERATOR +
					getSubSegment().getSegmentCode();
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

	public SubSegment getSubSegment() {
		return this.subSegment;
	}
	public void setSubSegment(SubSegment subSegment) {
		this.subSegment = subSegment;
	}

	public void setSubSegmentService(SubSegmentService subSegmentService) {
		this.subSegmentService = subSegmentService;
	}
	public SubSegmentService getSubSegmentService() {
		return this.subSegmentService;
	}

	public void setSubSegmentListCtrl(SubSegmentListCtrl subSegmentListCtrl) {
		this.subSegmentListCtrl = subSegmentListCtrl;
	}
	public SubSegmentListCtrl getSubSegmentListCtrl() {
		return this.subSegmentListCtrl;
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
