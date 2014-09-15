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
 * FileName    		:  GeneralDepartmentDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.generaldepartment;

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
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.GeneralDepartmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/GeneralDepartment/generalDepartmentDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class GeneralDepartmentDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4896504189951469996L;
	private final static Logger logger = Logger.getLogger(GeneralDepartmentDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_GeneralDepartmentDialog; 	// autoWired

	protected Uppercasebox	 genDepartment; 					// autoWired
	protected Textbox 	 genDeptDesc; 						// autoWired
	protected Label 	 recordStatus; 					// autoWired
	protected Radiogroup userAction;
	protected Groupbox 	 groupboxWf;

	// not auto wired Var's
	private GeneralDepartment generalDepartment; // overHanded per param
	private transient GeneralDepartmentListCtrl generalDepartmentListCtrl; // overHanded per param

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  oldVar_genDepartment;
	private transient String  oldVar_genDeptDesc;
	private transient String  oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String 	   btnCtroller_ClassPrefix = "button_GeneralDepartmentDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autoWire
	protected Button btnEdit; 	// autoWire
	protected Button btnDelete; // autoWire
	protected Button btnSave; 	// autoWire
	protected Button btnCancel; // autoWire
	protected Button btnClose; 	// autoWire
	protected Button btnHelp; 	// autoWire
	protected Button btnNotes; 	// autoWire
	
	// ServiceDAOs / Domain Classes
	private transient GeneralDepartmentService generalDepartmentService;
	private transient PagedListService 		   pagedListService;

	/**
	 * default constructor.<br>
	 */
	public GeneralDepartmentDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected GeneralDepartment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GeneralDepartmentDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the params map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		
		// READ OVERHANDED params !
		if (args.containsKey("generalDepartment")) {
			this.generalDepartment = (GeneralDepartment) args.get("generalDepartment");
			GeneralDepartment befImage =new GeneralDepartment();
			BeanUtils.copyProperties(this.generalDepartment, befImage);
			this.generalDepartment.setBefImage(befImage);
			
			setGeneralDepartment(this.generalDepartment);
		} else {
			setGeneralDepartment(null);
		}
	
		doLoadWorkFlow(this.generalDepartment.isWorkflow(),
				this.generalDepartment.getWorkflowId(),
				this.generalDepartment.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "GeneralDepartmentDialog");
		}

	
		// READ OVERHANDED params !
		// we get the generalDepartmentListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete generalDepartment here.
		if (args.containsKey("generalDepartmentListCtrl")) {
			setGeneralDepartmentListCtrl((GeneralDepartmentListCtrl) args
					.get("generalDepartmentListCtrl"));
		} else {
			setGeneralDepartmentListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getGeneralDepartment());
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.genDepartment.setMaxlength(8);
		this.genDeptDesc.setMaxlength(50);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
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
		getUserWorkspace().alocateAuthorities("GeneralDepartmentDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDepartmentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDepartmentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDepartmentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDepartmentDialog_btnSave"));
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
	public void onClose$window_GeneralDepartmentDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doClose();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());		
		doSave();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering"+event.toString());
		doEdit();
		// remember the old Var's
		doStoreInitValues();	
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_GeneralDepartmentDialog);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering"+event.toString());
		doNew();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		doDelete();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering"+event.toString());
		doCancel();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving"+event.toString());
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
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		
		if(close){
			closeDialog(this.window_GeneralDepartmentDialog, "GeneralDepartment");
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
	 * @param aGeneralDepartment
	 *            GeneralDepartment
	 */
	public void doWriteBeanToComponents(GeneralDepartment aGeneralDepartment) {
		logger.debug("Entering");
		this.genDepartment.setValue(aGeneralDepartment.getGenDepartment());
		this.genDeptDesc.setValue(aGeneralDepartment.getGenDeptDesc());
		this.recordStatus.setValue(aGeneralDepartment.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aGeneralDepartment
	 */
	public void doWriteComponentsToBean(GeneralDepartment aGeneralDepartment) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			aGeneralDepartment.setGenDepartment(this.genDepartment.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aGeneralDepartment.setGenDeptDesc(this.genDeptDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		aGeneralDepartment.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aGeneralDepartment
	 * @throws InterruptedException
	 */
	public void doShowDialog(GeneralDepartment aGeneralDepartment) throws InterruptedException {
		logger.debug("Entering");
		// if aGeneralDepartment == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aGeneralDepartment == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aGeneralDepartment = getGeneralDepartmentService().getNewGeneralDepartment();
			
			setGeneralDepartment(aGeneralDepartment);
		} else {
			setGeneralDepartment(aGeneralDepartment);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aGeneralDepartment.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.genDepartment.focus();
		} else {
			if (isWorkFlowEnabled()){
				this.genDeptDesc.focus();
				if (!StringUtils.trimToEmpty(aGeneralDepartment.getRecordType()).equals("")){
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aGeneralDepartment);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_GeneralDepartmentDialog);
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
	 * Stores the init values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_genDepartment = this.genDepartment.getValue();
		this.oldVar_genDeptDesc = this.genDeptDesc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.genDepartment.setValue(this.oldVar_genDepartment);
		this.genDeptDesc.setValue(this.oldVar_genDeptDesc);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		
		if(isWorkFlowEnabled()){
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
		//To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_genDeptDesc != this.genDeptDesc.getValue()) {
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
		
		if (!this.genDepartment.isReadonly()){
			this.genDepartment.setConstraint(new PTStringValidator(Labels.getLabel("label_GeneralDepartmentDialog_GenDepartment.value"), 
					PennantRegularExpressions.REGEX_ALPHA, true));
		}	
		if (!this.genDeptDesc.isReadonly()){
			this.genDeptDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_GeneralDepartmentDialog_GenDeptDesc.value"), 
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
		this.genDepartment.setConstraint("");
		this.genDeptDesc.setConstraint("");
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
		logger.debug("Enterring");
		this.genDepartment.setErrorMessage("");
		this.genDeptDesc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<GeneralDepartment> soObject = getGeneralDepartmentListCtrl().getSearchObj();
		getGeneralDepartmentListCtrl().pagingGeneralDepartmentList.setActivePage(0);
		getGeneralDepartmentListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getGeneralDepartmentListCtrl().listBoxGeneralDepartment!=null){
			getGeneralDepartmentListCtrl().listBoxGeneralDepartment.getListModel();
		}
		logger.debug("Leaving");
	} 

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a GeneralDepartment object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final GeneralDepartment aGeneralDepartment = new GeneralDepartment();
		BeanUtils.copyProperties(getGeneralDepartment(), aGeneralDepartment);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aGeneralDepartment.getGenDepartment();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aGeneralDepartment.getRecordType()).equals("")){
				aGeneralDepartment.setVersion(aGeneralDepartment.getVersion()+1);
				aGeneralDepartment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aGeneralDepartment.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aGeneralDepartment,tranType)){
					refreshList();
					closeDialog(this.window_GeneralDepartmentDialog, "GeneralDepartment"); 
				}

			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new GeneralDepartment object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old Var's
		doStoreInitValues();		
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new GeneralDepartment() in the frontEnd.
		// we get it from the backEnd.
		final GeneralDepartment aGeneralDepartment = getGeneralDepartmentService()
				.getNewGeneralDepartment();
		aGeneralDepartment.setNewRecord(true);
		setGeneralDepartment(aGeneralDepartment);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.genDepartment.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getGeneralDepartment().isNewRecord()){
			this.genDepartment.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.genDepartment.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
			this.genDeptDesc.setReadonly(isReadOnly("GeneralDepartmentDialog_genDeptDesc"));
			if (isWorkFlowEnabled()){
				for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.generalDepartment.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
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
		this.genDepartment.setReadonly(true);
		this.genDeptDesc.setReadonly(true);
		
		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		
		if(isWorkFlowEnabled()){
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
		this.genDepartment.setValue("");
		this.genDeptDesc.setValue("");
		logger.debug("Leaving");
		
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		
		logger.debug("Entering");
		final GeneralDepartment aGeneralDepartment = new GeneralDepartment();
		BeanUtils.copyProperties(getGeneralDepartment(), aGeneralDepartment);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the GeneralDepartment object with the components data
		doWriteComponentsToBean(aGeneralDepartment);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aGeneralDepartment.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aGeneralDepartment.getRecordType()).equals("")){
				aGeneralDepartment.setVersion(aGeneralDepartment.getVersion()+1);
				if(isNew){
					aGeneralDepartment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aGeneralDepartment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aGeneralDepartment.setNewRecord(true);
				}
			}
		}else{
			aGeneralDepartment.setVersion(aGeneralDepartment.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			if(doProcess(aGeneralDepartment,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_GeneralDepartmentDialog, "GeneralDepartment");
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
	 * @param aGeneralDepartment (GeneralDepartment)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(GeneralDepartment aGeneralDepartment,String tranType){
		
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aGeneralDepartment.setLastMntBy(getUserWorkspace()
				.getLoginUserDetails().getLoginUsrID());
		aGeneralDepartment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aGeneralDepartment.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aGeneralDepartment.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aGeneralDepartment.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aGeneralDepartment);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aGeneralDepartment))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");
				
				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {
						
						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aGeneralDepartment.setTaskId(taskId);
			aGeneralDepartment.setNextTaskId(nextTaskId);
			aGeneralDepartment.setRoleCode(getRole());
			aGeneralDepartment.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aGeneralDepartment, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,
					aGeneralDepartment);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aGeneralDepartment,
							PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aGeneralDepartment, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**	
	 * Get the result after processing DataBase Operations 
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		GeneralDepartment aGeneralDepartment = (GeneralDepartment) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getGeneralDepartmentService().delete(auditHeader);

						deleteNotes=true;
					}else{
						auditHeader = getGeneralDepartmentService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getGeneralDepartmentService().doApprove(auditHeader);

						if(aGeneralDepartment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getGeneralDepartmentService().doReject(auditHeader);
						if(aGeneralDepartment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_GeneralDepartmentDialog,auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(
						this.window_GeneralDepartmentDialog, auditHeader);
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					
					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}
				
				if (retValue==PennantConstants.porcessOVERIDE){
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
	// ++++++++++++++++++ WorkFlow Components +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 * @param aGeneralDepartment (GeneralDepartment)
	 * @param tranType (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(GeneralDepartment aGeneralDepartment,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aGeneralDepartment.getBefImage(), aGeneralDepartment);
		return new AuditHeader(String.valueOf(aGeneralDepartment.getId()),
				null, null, null, auditDetail,
				aGeneralDepartment.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_GeneralDepartmentDialog,
					auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering ");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving ");
	}	

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("GeneralDepartment");
		notes.setReference(getGeneralDepartment().getGenDepartment());
		notes.setVersion(getGeneralDepartment().getVersion());
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

	public GeneralDepartment getGeneralDepartment() {
		return this.generalDepartment;
	}
	public void setGeneralDepartment(GeneralDepartment generalDepartment) {
		this.generalDepartment = generalDepartment;
	}

	public void setGeneralDepartmentService(
			GeneralDepartmentService generalDepartmentService) {
		this.generalDepartmentService = generalDepartmentService;
	}
	public GeneralDepartmentService getGeneralDepartmentService() {
		return this.generalDepartmentService;
	}

	public void setGeneralDepartmentListCtrl(
			GeneralDepartmentListCtrl generalDepartmentListCtrl) {
		this.generalDepartmentListCtrl = generalDepartmentListCtrl;
	}
	public GeneralDepartmentListCtrl getGeneralDepartmentListCtrl() {
		return this.generalDepartmentListCtrl;
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
