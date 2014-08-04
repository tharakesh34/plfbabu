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
 * FileName    		:  CourseTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.coursetype;

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
import com.pennant.backend.model.amtmasters.CourseType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.amtmasters.CourseTypeService;
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
 * /WEB-INF/pages/AMTMaster/CourseType/courseTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CourseTypeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1226164441717601993L;
	private final static Logger logger = Logger.getLogger(CourseTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window      window_CourseTypeDialog;       // autowired
	protected Textbox     courseTypeCode;                // autowired
	protected Textbox     courseTypeDesc;                // autowired
	protected Label       recordStatus;                  // autowired
	protected Radiogroup  userAction;                    // autowired
	protected Groupbox    groupboxWf;                    // autowired
	protected Button      btnNew;                        // autowired
	protected Button      btnEdit;                       // autowired
	protected Button      btnDelete;                     // autowired
	protected Button      btnSave;                       // autowired
	protected Button      btnCancel;                     // autowired
	protected Button      btnClose;                      // autowired
	protected Button      btnHelp;                       // autowired
	protected Button      btnNotes;                      // autowired

	// not auto wired vars
	private CourseType courseType;                           // overhanded per param
	private transient CourseTypeListCtrl courseTypeListCtrl; // overhanded per param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_courseTypeCode;
	private transient String  		oldVar_courseTypeDesc;
	private transient String        oldVar_recordStatus;
	private transient boolean       validationOn;
	private boolean                 notes_Entered=false;
	// Button controller for the CRUD buttons
	private transient final String                   btnCtroller_ClassPrefix = "button_CourseTypeDialog_";
	private transient ButtonStatusCtrl               btnCtrl;
	// ServiceDAOs / Domain Classes
	private transient CourseTypeService              courseTypeService;
	private transient PagedListService               pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public CourseTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CourseType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CourseTypeDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("courseType")) {
			this.courseType = (CourseType) args.get("courseType");
			CourseType befImage =new CourseType();
			BeanUtils.copyProperties(this.courseType, befImage);
			this.courseType.setBefImage(befImage);

			setCourseType(this.courseType);
		} else {
			setCourseType(null);
		}

		doLoadWorkFlow(this.courseType.isWorkflow(),this.courseType.getWorkflowId(),this.courseType.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CourseTypeDialog");
		}

		// READ OVERHANDED params !
		// we get the courseTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete courseType here.
		if (args.containsKey("courseTypeListCtrl")) {
			setCourseTypeListCtrl((CourseTypeListCtrl) args.get("courseTypeListCtrl"));
		} else {
			setCourseTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCourseType());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.courseTypeCode.setMaxlength(20);
		this.courseTypeDesc.setMaxlength(50);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;

		getUserWorkspace().alocateAuthorities("CourseTypeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CourseTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CourseTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CourseTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CourseTypeDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CourseTypeDialog(Event event) throws Exception {
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
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_CourseTypeDialog);
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
			throw e;
		}
		logger.debug("Leaving");
	}
	/**
	 * When user clicks on "Notes" button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ GUI Process ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_CourseTypeDialog, "CourseType");	
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
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCourseType
	 *            CourseType
	 */
	public void doWriteBeanToComponents(CourseType aCourseType) {
		logger.debug("Entering") ;
		this.courseTypeCode.setValue(aCourseType.getCourseTypeCode());
		this.courseTypeDesc.setValue(aCourseType.getCourseTypeDesc());

		this.recordStatus.setValue(aCourseType.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCourseType
	 */
	public void doWriteComponentsToBean(CourseType aCourseType) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCourseType.setCourseTypeCode(this.courseTypeCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCourseType.setCourseTypeDesc(this.courseTypeDesc.getValue());
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

		aCourseType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCourseType
	 * @throws InterruptedException
	 */
	public void doShowDialog(CourseType aCourseType) throws InterruptedException {
		logger.debug("Entering") ;

		// if aCourseType == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCourseType == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCourseType = getCourseTypeService().getNewCourseType();

			setCourseType(aCourseType);
		} else {
			setCourseType(aCourseType);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aCourseType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.courseTypeCode.focus();
		} else {
			this.courseTypeDesc.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCourseType);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CourseTypeDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_courseTypeCode = this.courseTypeCode.getValue();
		this.oldVar_courseTypeDesc = this.courseTypeDesc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.courseTypeCode.setValue(this.oldVar_courseTypeCode);
		this.courseTypeDesc.setValue(this.oldVar_courseTypeDesc);
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
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.courseTypeCode.isReadonly()){
			this.courseTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CourseTypeDialog_CourseTypeCode.value"), PennantRegularExpressions.REGEX_ALPHA_CODE, true));
		}	
		if (!this.courseTypeDesc.isReadonly()){
			this.courseTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CourseTypeDialog_CourseTypeDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.courseTypeCode.setConstraint("");
		this.courseTypeDesc.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.courseTypeCode.setErrorMessage("");
		this.courseTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CourseType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CourseType aCourseType = new CourseType();
		BeanUtils.copyProperties(getCourseType(), aCourseType);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCourseType.getCourseTypeCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCourseType.getRecordType()).equals("")){
				aCourseType.setVersion(aCourseType.getVersion()+1);
				aCourseType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCourseType.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCourseType,tranType)){
					refreshList();
					closeDialog(this.window_CourseTypeDialog, "CourseType"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CourseType object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final CourseType aCourseType = getCourseTypeService().getNewCourseType();
		aCourseType.setNewRecord(true);
		setCourseType(aCourseType);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.courseTypeCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCourseType().isNewRecord()){
			this.courseTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.courseTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.courseTypeDesc.setReadonly(isReadOnly("CourseTypeDialog_courseTypeDesc"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.courseType.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
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
		this.courseTypeCode.setReadonly(true);
		this.courseTypeDesc.setReadonly(true);

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

		this.courseTypeCode.setValue("");
		this.courseTypeDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CourseType aCourseType = new CourseType();
		BeanUtils.copyProperties(getCourseType(), aCourseType);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CourseType object with the components data
		doWriteComponentsToBean(aCourseType);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aCourseType.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCourseType.getRecordType()).equals("")){
				aCourseType.setVersion(aCourseType.getVersion()+1);
				if(isNew){
					aCourseType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCourseType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCourseType.setNewRecord(true);
				}
			}
		}else{
			aCourseType.setVersion(aCourseType.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCourseType,tranType)){
				doWriteBeanToComponents(aCourseType);
				refreshList();
				closeDialog(this.window_CourseTypeDialog, "CourseType");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(CourseType aCourseType,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCourseType.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCourseType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCourseType.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCourseType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCourseType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCourseType);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCourseType))) {
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

			aCourseType.setTaskId(taskId);
			aCourseType.setNextTaskId(nextTaskId);
			aCourseType.setRoleCode(getRole());
			aCourseType.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCourseType, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCourseType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCourseType, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aCourseType, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		CourseType aCourseType = (CourseType) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCourseTypeService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCourseTypeService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCourseTypeService().doApprove(auditHeader);

						if(aCourseType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCourseTypeService().doReject(auditHeader);
						if(aCourseType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CourseTypeDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CourseTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	/**
	 * Display Message in Error Box
	 * @param e
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CourseTypeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	/**
	 * 	// Method for refreshing the list after successful updation
	 */
	private void refreshList(){
		final JdbcSearchObject<CourseType> soCourseType = getCourseTypeListCtrl().getSearchObj();
		getCourseTypeListCtrl().pagingCourseTypeList.setActivePage(0);
		getCourseTypeListCtrl().getPagedListWrapper().setSearchObject(soCourseType);
		if(getCourseTypeListCtrl().listBoxCourseType!=null){
			getCourseTypeListCtrl().listBoxCourseType.getListModel();
		}
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

	public CourseType getCourseType() {
		return this.courseType;
	}

	public void setCourseType(CourseType courseType) {
		this.courseType = courseType;
	}

	public void setCourseTypeService(CourseTypeService courseTypeService) {
		this.courseTypeService = courseTypeService;
	}

	public CourseTypeService getCourseTypeService() {
		return this.courseTypeService;
	}

	public void setCourseTypeListCtrl(CourseTypeListCtrl courseTypeListCtrl) {
		this.courseTypeListCtrl = courseTypeListCtrl;
	}

	public CourseTypeListCtrl getCourseTypeListCtrl() {
		return this.courseTypeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}


	private AuditHeader getAuditHeader(CourseType aCourseType, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCourseType.getBefImage(), aCourseType);   
		return new AuditHeader(aCourseType.getCourseTypeCode(),null,null,null,auditDetail,aCourseType.getUserDetails(),getOverideMap());
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
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

	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("CourseType");
		notes.setReference(getCourseType().getCourseTypeCode());
		notes.setVersion(getCourseType().getVersion());
		return notes;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
}
