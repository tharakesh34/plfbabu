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
 * FileName    		:  NationalityCodeDialogCtrl.java                                      * 	  
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

package com.pennant.webui.systemmasters.nationalitycode;

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
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.NationalityCodeService;
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
 * /WEB-INF/pages/SystemMaster/NationalityCode/nationalityCodeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class NationalityCodeDialogCtrl extends GFCBaseCtrl implements Serializable {


	private static final long serialVersionUID = 2856434917203365995L;
	private final static Logger logger = Logger.getLogger(NationalityCodeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_NationalityCodeDialog; 	// autoWired

	protected Textbox 		txtNationalityCode; 			// autoWired
	protected Textbox 		nationalityDesc; 				// autoWired
	protected Checkbox 		nationalityIsActive; 			// autoWired

	protected Label 		recordStatus; 					// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired variables
	private NationalityCode nationalityCode; 						   // over handed per parameter
	private transient NationalityCodeListCtrl nationalityCodeListCtrl; // over handed per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String 	oldVar_nationalityCode;
	private transient String 	oldVar_nationalityDesc;
	private transient boolean 	oldVar_nationalityIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_NationalityCodeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autoWired
	protected Button btnEdit; 	// autoWired
	protected Button btnDelete; // autoWired
	protected Button btnSave; 	// autoWired
	protected Button btnCancel; // autoWired
	protected Button btnClose; 	// autoWired
	protected Button btnHelp; 	// autoWired
	protected Button btnNotes; 	// autoWired

	// ServiceDAOs / Domain Classes
	private transient NationalityCodeService nationalityCodeService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public NationalityCodeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected NationalityCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_NationalityCodeDialog(Event event)	throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("nationalityCode")) {
			this.nationalityCode = (NationalityCode) args.get("nationalityCode");
			NationalityCode befImage = new NationalityCode();
			BeanUtils.copyProperties(this.nationalityCode, befImage);
			this.nationalityCode.setBefImage(befImage);

			setNationalityCode(this.nationalityCode);
		} else {
			setNationalityCode(null);
		}

		doLoadWorkFlow(this.nationalityCode.isWorkflow(), this.nationalityCode.getWorkflowId(),this.nationalityCode.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "NationalityCodeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the nationalityCodeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete nationalityCode here.
		if (args.containsKey("nationalityCodeListCtrl")) {
			setNationalityCodeListCtrl((NationalityCodeListCtrl) args.get("nationalityCodeListCtrl"));
		} else {
			setNationalityCodeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getNationalityCode());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.txtNationalityCode.setMaxlength(2);
		this.nationalityDesc.setMaxlength(50);

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

		getUserWorkspace().alocateAuthorities("NationalityCodeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_NationalityCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_NationalityCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_NationalityCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_NationalityCodeDialog_btnSave"));
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
	public void onClose$window_NationalityCodeDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_NationalityCodeDialog);
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
			logger.debug("doClose isDataChanged(): true");

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
			logger.debug("doClose isDataChanged(): false");
		}

		if (close) {
			closeDialog(this.window_NationalityCodeDialog, "NationalityCode");
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
	 * @param aNationalityCode
	 *            NationalityCode
	 */
	public void doWriteBeanToComponents(NationalityCode aNationalityCode) {
		logger.debug("Entering");
		this.txtNationalityCode.setValue(aNationalityCode.getNationalityCode());
		this.nationalityDesc.setValue(aNationalityCode.getNationalityDesc());
		this.nationalityIsActive.setChecked(aNationalityCode.isNationalityIsActive());
		this.recordStatus.setValue(aNationalityCode.getRecordStatus());
		
		if(aNationalityCode.isNew() || (aNationalityCode.getRecordType() != null ? aNationalityCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.nationalityIsActive.setChecked(true);
			this.nationalityIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNationalityCode
	 */
	public void doWriteComponentsToBean(NationalityCode aNationalityCode) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aNationalityCode.setNationalityCode(this.txtNationalityCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aNationalityCode.setNationalityDesc(this.nationalityDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aNationalityCode.setNationalityIsActive(this.nationalityIsActive.isChecked());
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

		aNationalityCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aNationalityCode
	 * @throws InterruptedException
	 */
	public void doShowDialog(NationalityCode aNationalityCode) throws InterruptedException {
		logger.debug("Entering");

		// if aNationalityCode == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aNationalityCode == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aNationalityCode = getNationalityCodeService().getNewNationalityCode();

			setNationalityCode(aNationalityCode);
		} else {
			setNationalityCode(aNationalityCode);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aNationalityCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.txtNationalityCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.nationalityDesc.focus();
				if (!StringUtils.trimToEmpty(aNationalityCode.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aNationalityCode);

			// stores the initialize data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_NationalityCodeDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
			logger.debug("Leaving");
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
		this.oldVar_nationalityCode = this.txtNationalityCode.getValue();
		this.oldVar_nationalityDesc = this.nationalityDesc.getValue();
		this.oldVar_nationalityIsActive = this.nationalityIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		this.txtNationalityCode.setValue(this.oldVar_nationalityCode);
		this.nationalityDesc.setValue(this.oldVar_nationalityDesc);
		this.nationalityIsActive.setChecked(this.oldVar_nationalityIsActive);
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

		if (this.oldVar_nationalityCode != this.txtNationalityCode.getValue()) {
			return true;
		}
		if (this.oldVar_nationalityDesc != this.nationalityDesc.getValue()) {
			return true;
		}
		if (this.oldVar_nationalityIsActive != this.nationalityIsActive.isChecked()) {
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

		if (!this.txtNationalityCode.isReadonly()){
			this.txtNationalityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_NationalityCodeDialog_NationalityCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.nationalityDesc.isReadonly()){
			this.nationalityDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_NationalityCodeDialog_NationalityDesc.value"), 
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
		this.txtNationalityCode.setConstraint("");
		this.nationalityDesc.setConstraint("");
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
		this.txtNationalityCode.setErrorMessage("");
		this.nationalityDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a NationalityCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final NationalityCode aNationalityCode = new NationalityCode();
		BeanUtils.copyProperties(getNationalityCode(), aNationalityCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aNationalityCode.getNationalityCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aNationalityCode.getRecordType()).equals("")) {
				aNationalityCode.setVersion(aNationalityCode.getVersion() + 1);
				aNationalityCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aNationalityCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aNationalityCode, tranType)) {
					refreshList();
					closeDialog(this.window_NationalityCodeDialog,	"NationalityCode");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new NationalityCode object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new NationalityCode() in the front end.
		// we get it from the back end.
		final NationalityCode aNationalityCode = getNationalityCodeService().getNewNationalityCode();
		aNationalityCode.setNewRecord(true);
		setNationalityCode(aNationalityCode);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.txtNationalityCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getNationalityCode().isNewRecord()) {
			this.txtNationalityCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.txtNationalityCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.nationalityDesc.setReadonly(isReadOnly("NationalityCodeDialog_nationalityDesc"));
		this.nationalityIsActive.setDisabled(isReadOnly("NationalityCodeDialog_nationalityIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.nationalityCode.isNewRecord()) {
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

		this.txtNationalityCode.setReadonly(true);
		this.nationalityDesc.setReadonly(true);
		this.nationalityIsActive.setDisabled(true);

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
		this.txtNationalityCode.setValue("");
		this.nationalityDesc.setValue("");
		this.nationalityIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final NationalityCode aNationalityCode = new NationalityCode();
		BeanUtils.copyProperties(getNationalityCode(), aNationalityCode);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the NationalityCode object with the components data
		doWriteComponentsToBean(aNationalityCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aNationalityCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aNationalityCode.getRecordType()).equals("")) {
				aNationalityCode.setVersion(aNationalityCode.getVersion() + 1);
				if (isNew) {
					aNationalityCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aNationalityCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aNationalityCode.setNewRecord(true);
				}
			}
		} else {
			aNationalityCode.setVersion(aNationalityCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aNationalityCode, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_NationalityCodeDialog,	"NationalityCode");
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
	 * @param aNationalityCode
	 *            (NationalityCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(NationalityCode aNationalityCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aNationalityCode.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aNationalityCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aNationalityCode.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aNationalityCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aNationalityCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aNationalityCode);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aNationalityCode))) {
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

			aNationalityCode.setTaskId(taskId);
			aNationalityCode.setNextTaskId(nextTaskId);
			aNationalityCode.setRoleCode(getRole());
			aNationalityCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aNationalityCode, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aNationalityCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aNationalityCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aNationalityCode, tranType);
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
		NationalityCode aNationalityCode = (NationalityCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getNationalityCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getNationalityCodeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getNationalityCodeService().doApprove(auditHeader);

						if (aNationalityCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getNationalityCodeService().doReject(auditHeader);

						if (aNationalityCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_NationalityCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_NationalityCodeDialog, auditHeader);
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
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(NationalityCode aNationalityCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aNationalityCode.getBefImage(), aNationalityCode);
		return new AuditHeader(String.valueOf(aNationalityCode.getId()), null,
				null, null, auditDetail, aNationalityCode.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_NationalityCodeDialog, auditHeader);
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
		final JdbcSearchObject<NationalityCode> soNationalityCode = getNationalityCodeListCtrl().getSearchObj();
		getNationalityCodeListCtrl().pagingNationalityCodeList.setActivePage(0);
		getNationalityCodeListCtrl().getPagedListWrapper().setSearchObject(soNationalityCode);
		if (getNationalityCodeListCtrl().listBoxNationalityCode != null) {
			getNationalityCodeListCtrl().listBoxNationalityCode.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("NationalityCode");
		notes.setReference(getNationalityCode().getNationalityCode());
		notes.setVersion(getNationalityCode().getVersion());
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

	public NationalityCode getNationalityCode() {
		return this.nationalityCode;
	}
	public void setNationalityCode(NationalityCode nationalityCode) {
		this.nationalityCode = nationalityCode;
	}

	public void setNationalityCodeService(NationalityCodeService nationalityCodeService) {
		this.nationalityCodeService = nationalityCodeService;
	}
	public NationalityCodeService getNationalityCodeService() {
		return this.nationalityCodeService;
	}

	public void setNationalityCodeListCtrl(NationalityCodeListCtrl nationalityCodeListCtrl) {
		this.nationalityCodeListCtrl = nationalityCodeListCtrl;
	}
	public NationalityCodeListCtrl getNationalityCodeListCtrl() {
		return this.nationalityCodeListCtrl;
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
