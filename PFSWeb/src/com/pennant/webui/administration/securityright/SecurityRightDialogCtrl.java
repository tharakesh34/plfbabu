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
 * FileName    		:  SecurityRightDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-07-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityright;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityRightService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/administration/SecurityRight/SecurityRightDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SecurityRightDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1944116473899101747L;
	private final static Logger logger = Logger.getLogger(SecurityRightDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_SecurityRightDialog;   // autoWired
	protected Combobox     rightType;                    // autoWired
	protected Textbox      rightName;                    // autoWired
	protected Label        recordStatus;                 // autoWired
	protected Radiogroup   userAction;                   // autoWired
	protected Groupbox     groupboxWf;                   // autoWired
	protected Row          statusRow;                    // autoWired
	protected Button       btnNew;                       // autoWired
	protected Button       btnEdit;                      // autoWired
	protected Button       btnDelete;                    // autoWired
	protected Button       btnSave;                      // autoWired
	protected Button       btnCancel;                    // autoWired
	protected Button       btnClose;                     // autoWired
	protected Button       btnHelp;                      // autoWired
	protected Button       btnNotes;                     // autoWired
	// not auto wired variables
	private   SecurityRight    securityRight;                      // overHanded per parameter
	private transient SecurityRightListCtrl securityRightListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.

	// private transient int oldVar_rightType;
	private transient String    oldVar_rightName;
	private transient String    oldVar_recordStatus;
	private transient String    oldVar_rightType;
	private transient boolean   validationOn;
	private boolean             notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String     btnCtroller_ClassPrefix = "button_SecurityRightDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	// ServiceDAOs / Domain Classes
	private transient SecurityRightService  securityRightService;
	private transient PagedListService pagedListService;
	private List<ValueLabel>           listRightType = PennantStaticListUtil.getRightType(); 

	/**
	 * default constructor.<br>
	 */
	public SecurityRightDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityRight object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityRightDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace()
				,this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("securityRight")) {
			this.securityRight = (SecurityRight) args.get("securityRight");
			SecurityRight befImage = new SecurityRight();
			BeanUtils.copyProperties(this.securityRight, befImage);
			this.securityRight.setBefImage(befImage);
			setSecurityRight(this.securityRight);
		} else {
			setSecurityRight(null);
		}

		doLoadWorkFlow(this.securityRight.isWorkflow(),this.securityRight.getWorkflowId(), this.securityRight.getNextTaskId());
		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"SecurityRightDialog");
		}

		// READ OVERHANDED parameters !
		// we get the securityRightListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete securityRight here.
		if (args.containsKey("securityRightListCtrl")) {
			setSecurityRightListCtrl((SecurityRightListCtrl) args.get("securityRightListCtrl"));
		} else {
			setSecurityRightListCtrl(null);
		}

		setListRightType();
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSecurityRight());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		// Empty sent any required attributes
		this.rightName.setMaxlength(100);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("SecurityRightDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityRightDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityRightDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityRightDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityRightDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
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
	public void onClose$window_SecurityRightDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering " + event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_SecurityRightDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering " + event.toString());
		doNew();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doDelete();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		}catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving " + event.toString());
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
		boolean close=true;

		if (isDataChanged()) {
			logger.debug("doClose isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,	MultiLineMessageBox.YES
					| MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("doClose isDataChanged : False");
		}

		if(close){
			closeDialog(this.window_SecurityRightDialog, "SecurityRight");
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setBtnStatus_Save();
		this.btnEdit.setVisible(true);
		this.btnDelete.setVisible(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSecurityRight
	 *            SecurityRight
	 */
	public void doWriteBeanToComponents(SecurityRight aSecurityRight) {
		logger.debug("Entering ");
		if(securityRight.isNew()){
			this.rightType.setSelectedIndex(0);

		}else{
			this.rightType.setValue(PennantAppUtil.getlabelDesc(
					String.valueOf(securityRight.getRightType()),PennantStaticListUtil.getRightType()));
		}
		this.rightName.setValue(aSecurityRight.getRightName());
		this.recordStatus.setValue(aSecurityRight.getRecordStatus());
		logger.debug("Leaving ");
	}
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityRight
	 */
	public void doWriteComponentsToBean(SecurityRight aSecurityRight) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			String strRightType = (String) this.rightType.getSelectedItem().getValue();
			if (StringUtils.trimToEmpty(strRightType).equalsIgnoreCase("")) {
				throw new WrongValueException(this.rightType,Labels.getLabel("STATIC_INVALID"
						,new String[] { Labels.getLabel("label_SecurityRightDialog_RightType.value") }));
			}
			aSecurityRight.setRightType(Integer.parseInt(strRightType));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSecurityRight.setRightName(this.rightName.getValue());

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

		aSecurityRight.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSecurityRight
	 * @throws InterruptedException
	 */
	public void doShowDialog(SecurityRight aSecurityRight) throws InterruptedException {
		logger.debug("Entering ");

		// if aSecurityRight == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSecurityRight == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aSecurityRight = getSecurityRightService().getNewSecurityRight();

			setSecurityRight(aSecurityRight);
		} else {
			setSecurityRight(aSecurityRight);
		}
		// set Read only mode accordingly if the object is new or not.
		if (aSecurityRight.isNew()) {
			this.btnCtrl.setInitNew();

			doEdit();
			// setFocus
			this.rightType.focus();
		} else {
			this.rightType.focus();
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
			doWriteBeanToComponents(aSecurityRight);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SecurityRightDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method sets all rightsTypes as ComboItems for ComboBox
	 */
	private void setListRightType() {
		logger.debug("Entering ");
		for (int i = 0; i < listRightType.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listRightType.get(i).getLabel());
			comboitem.setValue(listRightType.get(i).getValue());
			this.rightType.appendChild(comboitem);
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_rightType=	this.rightType.getValue();
		this.oldVar_rightName = this.rightName.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.rightType.setValue(this.oldVar_rightType);
		this.rightName.setValue(this.oldVar_rightName);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering ");
		boolean changed = false;
		doClearMessage();
		if(!this.oldVar_rightType.equals(this.rightType.getValue()))
		{
			changed = true;
		}
		if (!this.oldVar_rightName.equals(this.rightName.getValue())) {
			changed = true;
		}
		logger.debug("Leaving ");
		return changed;

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.rightType.isDisabled()) {
			this.rightType.setConstraint(new StaticListValidator(listRightType
					,Labels.getLabel("label_SecurityRightDialog_RightType.value")));
		}

		if (!this.rightName.isReadonly()){
			this.rightName.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityRightDialog_RightName.value"),PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.rightType.setConstraint("");
		this.rightName.setConstraint("");
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		setValidationOn(true);
		this.rightType.setErrorMessage("");
		this.rightName.setErrorMessage("");		
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a SecurityRight object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");

		final SecurityRight aSecurityRight = new SecurityRight();
		BeanUtils.copyProperties(getSecurityRight(), aSecurityRight);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> " + aSecurityRight.getRightName();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,MultiLineMessageBox.YES	| MultiLineMessageBox.NO,Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSecurityRight.getRecordType()).equals("")) {
				aSecurityRight.setVersion(aSecurityRight.getVersion() + 1);
				aSecurityRight.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSecurityRight.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aSecurityRight, tranType)) {
					refreshList();
					closeDialog(this.window_SecurityRightDialog, "SecurityRight");
				}
			} catch (DataAccessException e) {
				showMessage(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Create a new SecurityRight object. <br>
	 */
	private void doNew() {
		logger.debug("Entering ");

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new SecurityRight() in the front end.
		// we get it from the back end.
		final SecurityRight aSecurityRight = getSecurityRightService().getNewSecurityRight();
		setSecurityRight(aSecurityRight);
		doClear();                    // clear all components
		doEdit();                     // edit mode
		doStoreInitValues();          // remember the old variables
		this.rightName.focus();  	  // setFocus
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");

		if (getSecurityRight().isNewRecord()) {
			this.rightType.setDisabled(isReadOnly("SecurityRightDialog_rightType"));
			this.rightName.setReadonly(isReadOnly("SecurityRightDialog_rightName"));
			this.btnCancel.setVisible(false);
		} else {
			this.rightType.setDisabled(true);
			this.rightName.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.securityRight.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
				this.rightType.setDisabled(false);
				this.rightName.setReadonly(false);

			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			this.btnSave.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.rightType.setDisabled(true);
		this.rightName.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");

		// remove validation, if there are a save before
		this.rightType.setValue("");
		this.rightName.setValue("");
		logger.debug("Leaving ");

	}
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityRight aSecurityRight = new SecurityRight();
		BeanUtils.copyProperties(getSecurityRight(), aSecurityRight);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SecurityRight object with the components data
		doWriteComponentsToBean(aSecurityRight);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSecurityRight.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSecurityRight.getRecordType()).equals("")) {
				aSecurityRight.setVersion(aSecurityRight.getVersion() + 1);
				if (isNew) {
					aSecurityRight.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSecurityRight.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityRight.setNewRecord(true);
				}
			}
		} else {
			aSecurityRight.setVersion(aSecurityRight.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSecurityRight, tranType)) {
				refreshList();
				closeDialog(this.window_SecurityRightDialog, "SecurityRight");
			}
		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 *This Method used for setting all workFlow details from userWorkSpace and setting audit 
	 *details to auditHeader
	 * @param aSecurityRight
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(SecurityRight aSecurityRight, String tranType) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSecurityRight.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSecurityRight.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityRight.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aSecurityRight.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityRight.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSecurityRight);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId
						,aSecurityRight))) {
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

			aSecurityRight.setTaskId(taskId);
			aSecurityRight.setNextTaskId(nextTaskId);
			aSecurityRight.setRoleCode(getRole());
			aSecurityRight.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSecurityRight, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aSecurityRight);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSecurityRight,	PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aSecurityRight, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
		return processCompleted;

	}

	/**
	 *  This Method used for calling the all Database  operations from the service by passing the  
	 *  auditHeader and operationRefs(Method) as String
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		SecurityRight aSecurityRight=(SecurityRight)auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSecurityRightService().delete(auditHeader);
						deleteNotes=true;	
					} else {
						auditHeader = getSecurityRightService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getSecurityRightService().doApprove(auditHeader);

						if(aSecurityRight.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)) {
						auditHeader = getSecurityRightService().doReject(auditHeader);
						if(aSecurityRight.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;	
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999
								,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_SecurityRightDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SecurityRightDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if(deleteNotes){
						deleteNotes(getNotes(),true);
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
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * This method  creates and returns AuditHeader Object
	 * @param aSecurityRight
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityRight aSecurityRight, String tranType) {

		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityRight.getBefImage()
				,aSecurityRight);   
		return new AuditHeader(String.valueOf(aSecurityRight.getId()),null,null,null
				,auditDetail,aSecurityRight.getUserDetails(),getOverideMap());
	}

	/**
	 * This method displays Message box with error message
	 * @param e
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_SecurityRightDialog,auditHeader);
		} catch (Exception exp) {
			logger.error(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * When clicks On "notes" button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
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
		logger.debug("Leaving " + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering ");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method creates Notes Class Object , sets data and returns 
	 * @return notes (Notes)
	 */
	private Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("SecurityRight");
		notes.setReference(String.valueOf(getSecurityRight().getRightID()));
		notes.setVersion(getSecurityRight().getVersion());
		logger.debug("Leaving ");
		return notes;
	}

	/**
	 * Refreshes the list
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<SecurityRight> soAcademic = getSecurityRightListCtrl().getSearchObj();
		getSecurityRightListCtrl().pagingSecurityRightList.setActivePage(0);
		getSecurityRightListCtrl().getPagedListWrapper().setSearchObject(soAcademic);
		if(getSecurityRightListCtrl().listBoxSecurityRight!=null){
			getSecurityRightListCtrl().listBoxSecurityRight.getListModel();
		}
		logger.debug("Leaving");
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

	public SecurityRight getSecurityRight() {
		return this.securityRight;
	}
	public void setSecurityRight(SecurityRight securityRight) {
		this.securityRight = securityRight;
	}

	public void setSecurityRightService(SecurityRightService securityRightService) {
		this.securityRightService = securityRightService;
	}
	public SecurityRightService getSecurityRightService() {
		return this.securityRightService;
	}

	public void setSecurityRightListCtrl(SecurityRightListCtrl securityRightListCtrl) {
		this.securityRightListCtrl = securityRightListCtrl;
	}
	public SecurityRightListCtrl getSecurityRightListCtrl() {
		return this.securityRightListCtrl;
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
}
