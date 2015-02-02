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
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  TasksCalendarDialogCtrl.java                                                   * 	  
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
*//*

package com.pennant.webui.masters.calendar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.masters.TasksCalendar;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.masters.TasksCalendarService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

*//**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/Country/countryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 *//*

public class TasksCalendarDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(TasksCalendarDialogCtrl.class);
	
	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	protected Window window_TaskCalendarDetailsDialog; // autowired

	protected Textbox taskType; // autowired
	protected Textbox taskStatus; // autowired
	protected Longbox custRef; // autowired
	protected Longbox leadReference; // autowired
	protected Textbox assignedUser; // autowired
	protected Textbox startDate; // autowired
	protected Textbox followupDate; // autowired
	protected Textbox closureDate; // autowired
	protected Textbox createdOn; // autowired

	protected Label recordStatus; // autowired
	protected Row row_TaskDetailsDialog_CustRef;
	protected Row row_TaskDetailsDialog_LeadReference;
	
	protected Radiogroup userAction;
	protected Groupbox gb;
	protected Row statusRow;

	// not auto wired vars
	private TasksCalendar taskCalendar; // overhanded per param
	//private transient TaskDetailsListCtrl taskDetailsListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_taskType;
	private transient String  		oldVar_taskStatus;
	private transient Long	    oldVar_taskLink;
	private transient Long 			oldVar_leadReference;
	private transient String  	    oldVar_assignedUser;
	private transient String  		oldVar_startDate;
	private transient String	 		oldVar_followupDate;
	private transient String	 		oldVar_closureDate;
	private transient String 			oldVar_createdOn;
	private transient String 	oldVar_recordStatus;
	

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_TaskDetailsDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	
	
	// ServiceDAOs / Domain Classes
 	private TasksCalendarService taskCalendarService;
	private transient PagedListService pagedListService;
	

	*//**
	 * default constructor.<br>
	 *//*
	public TasksCalendarDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	*//**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Country object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onCreate$window_TaskCalendarDetailsDialog(Event event) throws Exception {
		logger.debug(event.toString());

		 set components visible dependent of the users rights 
		doCheckRights();
		
		 create the Button Controller. Disable not used buttons during working 
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		
		// READ OVERHANDED params !
		if (args.containsKey("taskCalendar")) {
			this.taskCalendar = (TasksCalendar) args.get("taskCalendar");
			TasksCalendar befImage =new TasksCalendar();
			BeanUtils.copyProperties(this.taskCalendar, befImage);
			  
			setTaskCalendar(this.taskCalendar);
		} else {
			 
		}
	
		doLoadWorkFlow(this.taskCalendar.isWorkflow(),this.taskCalendar.getWorkflowId(),this.taskCalendar.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "TaskDetailsDialog");
		}

	
		// READ OVERHANDED params !
		// we get the countryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete country here.
		if (args.containsKey("taskDetailsListCtrl")) {
			setTaskDetailsListCtrl((TaskDetailsListCtrl) args.get("taskDetailsListCtrl"));
		} else {
			setTaskDetailsListCtrl(null);
		}

		// set Field Properties
		//doSetFieldProperties();
		doShowDialog(getTaskCalendar());
	}

	*//**
	 * Set the properties of the fields, like maxLength.<br>
	 *//*
	private void doSetFieldProperties() {
		//TODO Empty sent any required attributes
		
		
		if (isWorkFlowEnabled()){
			this.gb.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.gb.setVisible(false);
			this.statusRow.setVisible(false);
		}
	
	}

	*//**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 *//*
	private void doCheckRights() {
		
		getUserWorkspace().alocateAuthorities("TaskDetailsDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TaskDetailsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TaskDetailsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TaskDetailsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TaskDetailsDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		
		
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onClose$window_TaskCalendarDetailsDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
	}

	*//**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		
		doSave();
	}

	*//**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 *//*
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
	}

	*//**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_TaskCalendarDetailsDialog);
	}

	*//**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 *//*
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
	}

	*//**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
	}

	*//**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 *//*
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
	}

	*//**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final Exception e) {
			// close anyway
			window_TaskCalendarDetailsDialog.onClose();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	
	*//**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 *//*
	private void doClose() throws InterruptedException {

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		
		window_TaskCalendarDetailsDialog.onClose();
	}

	*//**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 *//*
	private void doCancel() {
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
	}

	*//**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCountry
	 *            Country
	 *//*
	public void doWriteBeanToComponents(TasksCalendar aTaskCalendar) {
		
		if(aTaskCalendar.getTaskLink() == 0 ){
				
		}
       if(aTaskCalendar.getTaskLink() == 1 ){
    	   this.row_TaskDetailsDialog_LeadReference.setVisible(true);
		}
       if(aTaskCalendar.getTaskLink() == 2 ){
			
			this.row_TaskDetailsDialog_CustRef.setVisible(true);
		}
		
		this.taskType.setValue(aTaskCalendar.getTaskType());
		this.taskStatus.setValue(aTaskCalendar.getStatus());
		this.custRef.setValue(aTaskCalendar.getCustReference());
		this.leadReference.setValue(aTaskCalendar.getLeadReference());
		if(aTaskCalendar.getStrAssignedUsr() != null)
		this.assignedUser.setValue(aTaskCalendar.getStrAssignedUsr());
		else
		this.assignedUser.setValue(" ");	
		this.startDate.setValue(aTaskCalendar.getStartDate().toString());
		this.followupDate.setValue(aTaskCalendar.getFollowupDate().toString());
		this.closureDate.setValue(aTaskCalendar.getClosureDate().toString());
		this.createdOn.setValue(aTaskCalendar.getCreatedOn().toString());
		 
	}

	*//**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCountry
	 *//*
	public void doWriteComponentsToBean(TasksCalendar aTaskCalendar) {
	
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
			
		try {
				if(this.taskType.getValue()!=null){
					aTaskCalendar.setTaskType(this.taskType.getValue());
				}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
				if(this.taskStatus.getValue()!=null){
					aTaskCalendar.setStatus(this.taskStatus.getValue());
				}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
				if(this.taskLink.getValue()!=null){
					aTaskCalendar.setTaskLink(this.taskLink.getValue());
				}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.leadReference.getValue()!=null){
				aTaskCalendar.setLeadReference(this.leadReference.getValue());
			}
		}catch (WrongValueException we ) {
		wve.add(we);
		}
		try {
			if(this.assignedUser.getValue()!=null){
				aTaskCalendar.setAssignedUser(this.assignedUser.getValue());
			}
		}catch (WrongValueException we ) {
		wve.add(we);
		}
		try {
			if(this.startDate.getValue()!=null){
				aTaskCalendar.setStartDate((Timestamp) this.startDate.getValue());
			}
		}catch (WrongValueException we ) {
		wve.add(we);
		}
		try {
			if(this.followupDate.getValue()!=null){
				aTaskCalendar.setFollowupDate((Timestamp) this.followupDate.getValue());
			}
		}catch (WrongValueException we ) {
		wve.add(we);
		}
		try {
			if(this.closureDate.getValue()!=null){
				aTaskCalendar.setClosureDate((Timestamp) this.closureDate.getValue());
			}
		}catch (WrongValueException we ) {
		wve.add(we);
		}
		try {
			if(this.createdOn.getValue()!=null){
				aTaskCalendar.setCreatedOn((Timestamp) this.createdOn.getValue());
			}
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
		
		aTaskCalendar.setRecordStatus(this.recordStatus.getValue());
		
	}

	*//**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCountry
	 * @throws InterruptedException
	 *//*
	public void doShowDialog(TasksCalendar aTaskCalendar) throws InterruptedException {

		// if aCountry == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aTaskCalendar == null) {
			*//** !!! DO NOT BREAK THE TIERS !!! *//*
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			//aCountry = getCountryService().getNewCountry();
			
			setTaskCalendar(aTaskCalendar);
		} else {
			setTaskCalendar(aTaskCalendar);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aTaskCalendar.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.taskType.focus();
		} else {
			if (isWorkFlowEnabled()){
				this.taskStatus.focus();
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			//}
		//}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aTaskCalendar);

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			window_TaskCalendarDetailsDialog.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * Stores the init values in mem vars. <br>
	 *//*
	private void doStoreInitValues() {
		this.oldVar_taskType = this.taskType.getValue();
		this.oldVar_taskStatus = this.taskStatus.getValue();
		this.oldVar_taskLink = this.custRef.getValue();
		this.oldVar_leadReference = this.leadReference.getValue();
		this.oldVar_assignedUser = this.assignedUser.getValue();
		this.oldVar_startDate = this.startDate.getValue().toString();
		this.oldVar_followupDate = this.followupDate.getValue().toString();
		this.oldVar_closureDate = this.closureDate.getValue().toString();
		this.oldVar_createdOn = this.createdOn.getValue().toString();
	//	this.oldVar_recordStatus = this.recordStatus.getValue();
	}

	*//**
	 * Resets the init values from mem vars. <br>
	 *//*
	private void doResetInitValues() {
		this.taskType.setValue(this.oldVar_taskType);
		this.taskStatus.setValue(this.oldVar_taskStatus);
		this.custRef.setValue(this.oldVar_taskLink);
		this.leadReference.setValue(this.oldVar_leadReference);
		this.assignedUser.setValue(this.oldVar_assignedUser);
		this.startDate.setValue(this.oldVar_startDate);
		this.followupDate.setValue(this.oldVar_followupDate);
		this.closureDate.setValue(this.oldVar_closureDate);
		this.createdOn.setValue(this.oldVar_createdOn);
 		
		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}

	}

	*//**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 *//*
	private boolean isDataChanged() {
		boolean changed = false;
		
		if (this.oldVar_taskType != this.taskType.getValue()) {
			changed = true;
		}
		if (this.oldVar_taskStatus != this.taskStatus.getValue()) {
			changed = true;
		}
		if (this.oldVar_taskLink != this.custRef.getValue()) {
			changed = true;
		}
		if (this.oldVar_leadReference != this.leadReference.getValue()) {
			changed = true;
		}
		if (this.oldVar_assignedUser != this.assignedUser.getValue()) {
			changed = true;
		}
		if (this.oldVar_startDate != this.startDate.getValue()) {
			changed = true;
		}
		if (this.oldVar_followupDate != this.followupDate.getValue()) {
			changed = true;
		}
		if (this.oldVar_closureDate != this.closureDate.getValue()) {
			changed = true;
		}
		if (this.oldVar_createdOn != this.createdOn.getValue()) {
			changed = true;
		}
		return changed;
	}

	*//**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 *//*
	private void doSetValidation() {
		setValidationOn(true);
		
		if (!this.countryCode.isReadonly()){
			this.countryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CountryDialog_CountryCode.value"),null,true));
		}	
		if (!this.countryDesc.isReadonly()){
			this.countryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CountryDialog_CountryDesc.value"),null,true));
		}	
		if (!this.countryParentLimit.isReadonly()){
			this.countryParentLimit.setConstraint(new AmountValidator(21,0,Labels.getLabel("label_CountryDialog_CountryParentLimit.value")));
		}	
		if (!this.countryResidenceLimit.isReadonly()){
			this.countryResidenceLimit.setConstraint(new AmountValidator(21,0,Labels.getLabel("label_CountryDialog_CountryResidenceLimit.value")));
		}	
		if (!this.countryRiskLimit.isReadonly()){
			this.countryRiskLimit.setConstraint(new AmountValidator(21,0,Labels.getLabel("label_CountryDialog_CountryRiskLimit.value")));
		}	
	}

	*//**
	 * Disables the Validation by setting empty constraints.
	 *//*
	private void doRemoveValidation() {
		setValidationOn(false);
		this.countryCode.setConstraint("");
		this.countryDesc.setConstraint("");
		this.countryParentLimit.setConstraint("");
		this.countryResidenceLimit.setConstraint("");
		this.countryRiskLimit.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * Deletes a Country object from database.<br>
	 * 
	 * @throws InterruptedException
	 *//*
	private void doDelete() throws InterruptedException {
		
		final TasksCalendar aTaskCalendar = new TasksCalendar();
		BeanUtils.copyProperties(getTaskCalendar(), aTaskCalendar);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aTaskCalendar.getTaskType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aTaskCalendar.getRecordType()).equals("")){
				aTaskCalendar.setVersion(aTaskCalendar.getVersion()+1);
				aTaskCalendar.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					//aTaskCalendar.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCountry,tranType)){

					final JdbcSearchObject<Country> soCountry = getCountryListCtrl().getSearchObj();
					// Set the ListModel
					getCountryListCtrl().getPagedListWrapper().setSearchObject(soCountry);

					// now synchronize the Country listBox
					final ListModelList lml = (ListModelList) getCountryListCtrl().listBoxCountry.getListModel();

					// Check if the Country object is new or updated -1
					// means that the obj is not in the list, so it's new ..
					if (lml.indexOf(aCountry) == -1) {
					} else {
						lml.remove(lml.indexOf(aCountry));
					}
					closeDialog(this.window_TaskCalendarDetailsDialog, "TasksCalendar"); 
				}

			}catch (DataAccessException e){
				showMessage(e);
			}
			
		}

	}

	*//**
	 * Create a new Country object. <br>
	 *//*
	private void doNew() {

		*//** !!! DO NOT BREAK THE TIERS !!! *//*
		// we don't create a new Country() in the frontend.
		// we get it from the backend.
		final Country aCountry = getCountryService().getNewCountry();
		aCountry.setNewRecord(true);
		setCountry(aCountry);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.taskType.focus();
	}

	*//**
	 * Set the components for edit mode. <br>
	 *//*
	private void doEdit() {

		if (getCountry().isNewRecord()){
			this.countryCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.countryCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		if (isWorkFlowEnabled()){
			this.countryDesc.setReadonly(isReadOnly("CountryDialog_countryDesc"));
			this.countryParentLimit.setReadonly(isReadOnly("CountryDialog_countryParentLimit"));
			this.countryResidenceLimit.setReadonly(isReadOnly("CountryDialog_countryResidenceLimit"));
			this.countryRiskLimit.setReadonly(isReadOnly("CountryDialog_countryRiskLimit"));
			this.countryIsActive.setDisabled(isReadOnly("CountryDialog_countryIsActive"));
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.country.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old vars
		doStoreInitValues();
	}

	*//**
	 * Set the components to ReadOnly. <br>
	 *//*
	public void doReadOnly() {
		 

		
		
		this.taskType.setReadonly(true);
		this.taskStatus.setReadonly(true);
		this.custRef.setReadonly(true);
		this.leadReference.setReadonly(true);
		this.assignedUser.setReadonly(true);
		this.startDate.setReadonly(true);
		this.assignedUser.setReadonly(true);
		this.followupDate.setReadonly(true);
		this.createdOn.setReadonly(true);
		this.closureDate.setReadonly(true);
		
		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		
		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
	}

	*//**
	 * Clears the components values. <br>
	 *//*
	public void doClear() {

		// remove validation, if there are a save before
		
		this.taskType.setValue(null);
		this.countryDesc.setValue("");
		this.countryParentLimit.setValue("");
		this.countryResidenceLimit.setValue("");
		this.countryRiskLimit.setValue("");
		this.countryIsActive.setChecked(false);
		
	}

	*//**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 *//*
	public void doSave() throws InterruptedException {

		final TasksCalendar aTaskCalendar = new TasksCalendar();
		BeanUtils.copyProperties(getTaskCalendar(), aTaskCalendar);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Country object with the components data
		doWriteComponentsToBean(aTaskCalendar);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aTaskCalendar.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			if (StringUtils.trimToEmpty(aTaskCalendar.getRecordType()).equals("")){
				aTaskCalendar.setVersion(aTaskCalendar.getVersion()+1);
				if(isNew){
					aTaskCalendar.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aTaskCalendar.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					//aTaskCalendar.setNewRecord(true);
				}
			}
		}else{
			aTaskCalendar.setVersion(aTaskCalendar.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aTaskCalendar,tranType)){
				doWriteBeanToComponents(aTaskCalendar);
				// ++ create the searchObject and init sorting ++ //
				final JdbcSearchObject<Country> soCountry = getCountryListCtrl().getSearchObj();

				// Set the ListModel
				getCountryListCtrl().pagingCountryList.setActivePage(0);
				getCountryListCtrl().getPagedListWrapper().setSearchObject(soCountry);

				// call from cusromerList then synchronize the Country listBox
				if (getCountryListCtrl().listBoxCountry != null) {
					// now synchronize the Country listBox
					final ListModelList lml = (ListModelList) getCountryListCtrl().listBoxCountry.getListModel();
				}

				doReadOnly();
				this.btnCtrl.setBtnStatus_Save();

				// Close the Existing Dialog
				closeDialog(this.window_TaskCalendarDetailsDialog, "TaskCalendar");
			}

		} catch (final DataAccessException e) {
			showMessage(e);
		}
	}

	private boolean doProcess(Country aCountry,String tranType){
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aCountry.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCountry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCountry.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aCountry.setRecordStatus(userAction.getSelectedItem().getValue());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCountry.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCountry);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCountry))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
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

			aCountry.setTaskId(taskId);
			aCountry.setNextTaskId(nextTaskId);
			aCountry.setRoleCode(getRole());
			aCountry.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aCountry, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCountry);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCountry, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aCountry, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		
		return processCompleted;
	}
	

	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						//auditHeader = getCountryService().delete(auditHeader);
					}else{
						//auditHeader = getCountryService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						//auditHeader = getCountryService().doApprove(auditHeader);
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						//auditHeader = getCountryService().doReject(auditHeader);
					}else{
						auditHeader.setErrorDetails(new ErrorDetails("9999", "E", Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_TaskCalendarDetailsDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(this.window_TaskCalendarDetailsDialog, auditHeader);
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
				}
				
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return processCompleted;
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

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(Country aCountry, String tranType){
		AuditHeader auditHeader = new AuditHeader(aCountry);
		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditCustNo(null);
		auditHeader.setAuditAccNo(null);
		auditHeader.setAuditLoanNo(null);
		auditHeader.setAuditReference(String.valueOf(aCountry.getId()));
		return auditHeader;
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("","E",e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_TaskCalendarDetailsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
	
	@SuppressWarnings("unchecked")
	public void onClick$btnNotes(Event event) throws Exception {
		// logger.debug(event.toString());
		
		final HashMap map = new HashMap();
		Notes notes = new Notes();
		notes.setModuleName("Country");
		//notes.setReference(getCountry().getCountryCode());
		//notes.setVersion(getCountry().getVersion());
		
		map.put("notes", notes);
		map.put("control", this);
		
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}
	
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}

	public TasksCalendar getTaskCalendar() {
		return taskCalendar;
	}

	 

	public void setTaskCalendar(TasksCalendar taskCalendar) {
		this.taskCalendar = taskCalendar;
	}

	public void setTaskCalendarService(TasksCalendarService taskCalendarService) {
		this.taskCalendarService = taskCalendarService;
	}

	public TasksCalendarService getTaskCalendarService() {
		return taskCalendarService;
	}
}
*/