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
 * FileName    		:  ScoringSlabDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2011    														*
 *                                                                  						*
 * Modified Date    :  05-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.scoringslab;

import java.io.Serializable;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.rmtmasters.scoringgroup.ScoringGroupDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/ScoringSlab/scoringSlabDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ScoringSlabDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 3743543079732961048L;
	private final static Logger logger = Logger.getLogger(ScoringSlabDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL -file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  		window_ScoringSlabDialog;          // autoWired
	protected Longbox 		aScoringSlab;                      // autoWired
	protected Textbox 		creditWorthness;                   // autoWired
	
	protected Label   		recordStatus;                      // autoWired
	protected Radiogroup 	userAction;
	protected Groupbox   	groupboxWf;
	protected Row        	statusRow;

	// not auto wired variables 
	private ScoringSlab scoringSlab;       // over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient long  		oldVar_scoringSlab;
	private transient String  		oldVar_creditWorthness;
	private transient String       	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String       btnCtroller_ClassPrefix = "button_ScoringSlabDialog_";
	private transient ButtonStatusCtrl   btnCtrl;
	protected Button  btnNew;     // autoWired
	protected Button  btnEdit;    // autoWired
	protected Button  btnDelete;  // autoWired
	protected Button  btnSave;    // autoWired
	protected Button  btnCancel;  // autoWired
	protected Button  btnClose;   // autoWired
	protected Button  btnHelp;    // autoWired
	protected Button  btnNotes;   // autoWired

	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private transient ScoringGroup scoringGroup =null;
	private ScoringGroupDialogCtrl scoringGroupDialogCtrl;
	private List<ScoringSlab>   scoringSlabList;

	/**
	 * default constructor.<br>
	 */
	public ScoringSlabDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringSlab object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScoringSlabDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("scroingSlab")) {
			this.scoringSlab = (ScoringSlab) args.get("scroingSlab");
			ScoringSlab befImage =new ScoringSlab();
			BeanUtils.copyProperties(this.scoringSlab, befImage);
			this.scoringSlab.setBefImage(befImage);
			setScoringSlab(this.scoringSlab);
		} else {
			setScoringSlab(null);
		}
		
		if (args.containsKey("scoringGroup")) {
			this.scoringGroup = (ScoringGroup) args.get("scoringGroup");
			setScoringGroup(this.scoringGroup);
		}
		
		if (args.containsKey("scoringGroupDialogCtrl")) {
			this.scoringGroupDialogCtrl =(ScoringGroupDialogCtrl)args.get("scoringGroupDialogCtrl");
			setScoringGroupDialogCtrl(this.scoringGroupDialogCtrl );
		} else {
			setScoringGroupDialogCtrl(null);
		}
		
		if(args.containsKey("roleCode")){
			getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "ScoringSlabDialog");
		}
		
		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "ScoringSlabDialog");
		}

		// READ OVERHANDED parameters !
		// we get the scoringSlabListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete scoringSlab here.

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getScoringSlab());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.aScoringSlab.setMaxlength(4);
		this.creditWorthness.setMaxlength(50);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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

		getUserWorkspace().alocateAuthorities("ScoringSlabDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ScoringSlabDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ScoringSlabDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ScoringSlabDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ScoringSlabDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
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
	public void onClose$window_ScoringSlabDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_ScoringSlabDialog);
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
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //

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
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
					| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			this.window_ScoringSlabDialog.onClose();	
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
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aScoringSlab
	 *            ScoringSlab
	 */
	public void doWriteBeanToComponents(ScoringSlab aScoringSlab) {
		logger.debug("Entering") ;
		this.aScoringSlab.setValue(aScoringSlab.getScoringSlab());
		this.creditWorthness.setValue(aScoringSlab.getCreditWorthness());
		this.recordStatus.setValue(aScoringSlab.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aScoringSlab
	 */
	public void doWriteComponentsToBean(ScoringSlab aScoringSlab) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aScoringSlab.setScoringSlab(this.aScoringSlab.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aScoringSlab.setCreditWorthness(this.creditWorthness.getValue());
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

		aScoringSlab.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aScoringSlab
	 * @throws InterruptedException
	 */
	public void doShowDialog(ScoringSlab aScoringSlab) throws InterruptedException {
		logger.debug("Entering") ;

		// if aScoringSlab == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aScoringSlab == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aScoringSlab = new ScoringSlab();

			setScoringSlab(aScoringSlab);
		} else {
			setScoringSlab(aScoringSlab);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aScoringSlab.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.aScoringSlab.focus();
		} else {
			this.btnCtrl.setInitEdit();
			doReadOnly();
			btnCancel.setVisible(false);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aScoringSlab);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.window_ScoringSlabDialog.doModal();
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
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_scoringSlab = this.aScoringSlab.longValue();
		this.oldVar_creditWorthness = this.creditWorthness.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.aScoringSlab.setValue(this.oldVar_scoringSlab);
		this.creditWorthness.setValue(this.oldVar_creditWorthness);
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
		
		if (this.oldVar_scoringSlab != this.aScoringSlab.getValue()) {
			return true;
		}
		if (this.oldVar_creditWorthness != this.creditWorthness.getValue()) {
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

		if (!this.aScoringSlab.isReadonly()){
			this.aScoringSlab.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_ScoringSlabDialog_ScoringSlab.value")}));
		}	
		if (!this.creditWorthness.isReadonly()){
			this.creditWorthness.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_ScoringSlabDialog_CreditWorthness.value")}));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.aScoringSlab.setConstraint("");
		this.creditWorthness.setConstraint("");
		logger.debug("Leaving");
	}
	
	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}

	/**
	 * Method for Remove Error messages
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.aScoringSlab.setErrorMessage("");
		this.creditWorthness.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a ScoringSlab object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		
		final ScoringSlab aScoringSlab = new ScoringSlab();
		BeanUtils.copyProperties(getScoringSlab(), aScoringSlab);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
							+Labels.getLabel("label_ScoringSlabDialog_ScoringSlab.value")+":" + aScoringSlab.getScoringSlab();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aScoringSlab.getRecordType()).equals("")){
				aScoringSlab.setVersion(aScoringSlab.getVersion()+1);
				aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()){
					aScoringSlab.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}else if (StringUtils.trimToEmpty(aScoringSlab.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aScoringSlab.setVersion(aScoringSlab.getVersion() + 1);
				aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newScoringSlabDetailProcess(aScoringSlab,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ScoringSlabDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getScoringGroupDialogCtrl().doFillScoringSlab(this.scoringSlabList);
					this.window_ScoringSlabDialog.onClose();
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new ScoringSlab object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		final ScoringSlab aScoringSlab = new ScoringSlab();
		setScoringSlab(aScoringSlab);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.creditWorthness.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getScoringSlab().isNewRecord()){
			this.aScoringSlab.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.aScoringSlab.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.creditWorthness.setReadonly(isReadOnly("ScoringSlabDialog_creditWorthness"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.scoringSlab.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			if (this.scoringSlab.isNewRecord()){
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setBtnStatus_Edit();
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.aScoringSlab.setReadonly(true);
		this.creditWorthness.setReadonly(true);
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
		this.aScoringSlab.setValue((new Long(0)));
		this.creditWorthness.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final ScoringSlab aScoringSlab = new ScoringSlab();
		BeanUtils.copyProperties(getScoringSlab(), aScoringSlab);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the ScoringSlab object with the components data
		doWriteComponentsToBean(aScoringSlab);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aScoringSlab.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aScoringSlab.getRecordType()).equals("")){
				aScoringSlab.setVersion(aScoringSlab.getVersion()+1);
				if(isNew){
					aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aScoringSlab.setNewRecord(true);
				}
			}
		}else{
			/*set the tranType according to RecordType*/
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
				aScoringSlab.setVersion(1);
				aScoringSlab.setRecordType(PennantConstants.RCD_ADD);
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}

			if(StringUtils.trimToEmpty(aScoringSlab.getRecordType()).equals("")){
				tranType =PennantConstants.TRAN_UPD;
				aScoringSlab.setRecordType(PennantConstants.RCD_UPD);
			}
			if(aScoringSlab.getRecordType().equals(PennantConstants.RCD_ADD) && isNew){
				tranType =PennantConstants.TRAN_ADD;
			} else if(aScoringSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
				tranType =PennantConstants.TRAN_UPD;
			} 
		}
		try {
			AuditHeader auditHeader =  newScoringSlabDetailProcess(aScoringSlab,tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_ScoringSlabDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getScoringGroupDialogCtrl().doFillScoringSlab(this.scoringSlabList);
				this.window_ScoringSlabDialog.onClose();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method added the ScoringSlabDetail object into scoringSlabList
	 *  by setting RecordType according to tranType
	 *  <p>eg: 	if(tranType==PennantConstants.TRAN_DEL){
	 *  	aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
	 *  }</p>
	 * @param  aScoringSlab (EducationalExpense)
	 * @param  tranType (String)
	 * @return auditHeader (AuditHeader)
	 */
	private AuditHeader newScoringSlabDetailProcess(ScoringSlab aScoringSlab,String tranType){
		logger.debug("Entering ");
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aScoringSlab, tranType);
		scoringSlabList= new ArrayList<ScoringSlab>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aScoringSlab.getScoringSlab());
		errParm[0] = PennantJavaUtil.getLabel("label_ScoringSlabDialog_ScoringSlab.value") + ":"+valueParm[0];

		if(getScoringGroupDialogCtrl().getScoringSlabList()!=null && getScoringGroupDialogCtrl().getScoringSlabList().size()>0){
			for (int i = 0; i < getScoringGroupDialogCtrl().getScoringSlabList().size(); i++) {
				ScoringSlab scoringSlab = getScoringGroupDialogCtrl().getScoringSlabList().get(i);

				if( aScoringSlab.getScoringSlab()==scoringSlab.getScoringSlab()){ // Both Current and Existing list slab same
					/*if same ScoringSlab added twice set error detail*/
					if(getScoringSlab().isNew()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(aScoringSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							scoringSlabList.add(aScoringSlab);
						}
						else if(aScoringSlab.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aScoringSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							scoringSlabList.add(aScoringSlab);
						}else if(aScoringSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getScoringGroupDialogCtrl().getScoringSlabList().size(); j++) {
								ScoringSlab scorslab = getScoringGroupDialogCtrl().getScoringSlabList().get(j);
								if( aScoringSlab.getScoringSlab()== scorslab.getScoringSlab()){
									scoringSlabList.add(scorslab);
								}
							}
						}else if(aScoringSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							aScoringSlab.setNewRecord(true);
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD ){
							scoringSlabList.add(scoringSlab);
						}
					}
				}else{
					scoringSlabList.add(scoringSlab);
				}
			}
		}
		if(!recordAdded){
			scoringSlabList.add(aScoringSlab);
		}
		logger.debug("Leaving");
		return auditHeader;
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
	private AuditHeader getAuditHeader(ScoringSlab aScoringSlab, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aScoringSlab.getBefImage(), aScoringSlab);   
		return new AuditHeader(String.valueOf(aScoringSlab.getScoreGroupId()),null,null,null,
				auditDetail,aScoringSlab.getUserDetails(),getOverideMap());
	}

	/**
	 * This method  shows error message
	 * @param e
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ScoringSlabDialog, auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
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

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("ScoringSlab");
		notes.setReference(String.valueOf(getScoringSlab().getScoreGroupId()));
		notes.setVersion(getScoringSlab().getVersion());
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

	public ScoringSlab getScoringSlab() {
		return this.scoringSlab;
	}
	public void setScoringSlab(ScoringSlab scoringSlab) {
		this.scoringSlab = scoringSlab;
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

	public ScoringGroupDialogCtrl getScoringGroupDialogCtrl() {
		return scoringGroupDialogCtrl;
	}
	public void setScoringGroupDialogCtrl(
			ScoringGroupDialogCtrl scoringGroupDialogCtrl) {
		this.scoringGroupDialogCtrl = scoringGroupDialogCtrl;
	}

	public void setScoringGroup(ScoringGroup scoringGroup) {
		this.scoringGroup = scoringGroup;
	}
	public ScoringGroup getScoringGroup() {
		return scoringGroup;
	}
}
