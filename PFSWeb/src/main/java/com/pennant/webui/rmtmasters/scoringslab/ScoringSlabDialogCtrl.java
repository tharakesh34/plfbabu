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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.rmtmasters.scoringgroup.ScoringGroupDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/ScoringSlab/scoringSlabDialog.zul file.
 */
public class ScoringSlabDialogCtrl extends GFCBaseCtrl<ScoringSlab> {
	private static final long serialVersionUID = 3743543079732961048L;
	private static final Logger logger = Logger.getLogger(ScoringSlabDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL -file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  		window_ScoringSlabDialog;          // autoWired
	protected Longbox 		aScoringSlab;                      // autoWired
	protected Textbox 		creditWorthness;                   // autoWired
	

	// not auto wired variables 
	private ScoringSlab scoringSlab;       // over handed per parameters

	private transient boolean validationOn;
	
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();
	private transient ScoringGroup scoringGroup =null;
	private ScoringGroupDialogCtrl scoringGroupDialogCtrl;
	private List<ScoringSlab>   scoringSlabList;

	/**
	 * default constructor.<br>
	 */
	public ScoringSlabDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ScoringSlabDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringSlab object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScoringSlabDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScoringSlabDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("scroingSlab")) {
			this.scoringSlab = (ScoringSlab) arguments.get("scroingSlab");
			ScoringSlab befImage =new ScoringSlab();
			BeanUtils.copyProperties(this.scoringSlab, befImage);
			this.scoringSlab.setBefImage(befImage);
			setScoringSlab(this.scoringSlab);
		} else {
			setScoringSlab(null);
		}
		
		if (arguments.containsKey("scoringGroup")) {
			this.scoringGroup = (ScoringGroup) arguments.get("scoringGroup");
			setScoringGroup(this.scoringGroup);
		}
		
		if (arguments.containsKey("scoringGroupDialogCtrl")) {
			this.scoringGroupDialogCtrl =(ScoringGroupDialogCtrl)arguments.get("scoringGroupDialogCtrl");
			setScoringGroupDialogCtrl(this.scoringGroupDialogCtrl );
		} else {
			setScoringGroupDialogCtrl(null);
		}
		
		if(arguments.containsKey("roleCode")){
			getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "ScoringSlabDialog");
		}
		
		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "ScoringSlabDialog");
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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ScoringSlabDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ScoringSlabDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ScoringSlabDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ScoringSlabDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
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
		MessageUtil.showHelpWindow(event, window_ScoringSlabDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.scoringSlab.getBefImage());
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
		logger.debug("Entering");

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

			this.window_ScoringSlabDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.aScoringSlab.isReadonly()){
			this.aScoringSlab.setConstraint(new PTStringValidator(Labels.getLabel("label_ScoringSlabDialog_ScoringSlab.value"),null,true));
		}	
		if (!this.creditWorthness.isReadonly()){
			this.creditWorthness.setConstraint(new PTStringValidator(Labels.getLabel("label_ScoringSlabDialog_CreditWorthness.value"),null,true));
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
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.aScoringSlab.setErrorMessage("");
		this.creditWorthness.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

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
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aScoringSlab.getRecordType())){
				aScoringSlab.setVersion(aScoringSlab.getVersion()+1);
				aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()){
					aScoringSlab.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}else if (PennantConstants.RCD_UPD.equals(StringUtils.trimToEmpty(aScoringSlab.getRecordType()))) {
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
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
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
		this.aScoringSlab.setValue(Long.valueOf(0));
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

		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aScoringSlab.getRecordType())){
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

			if(StringUtils.isBlank(aScoringSlab.getRecordType())){
				tranType =PennantConstants.TRAN_UPD;
				aScoringSlab.setRecordType(PennantConstants.RCD_UPD);
			}
			if(PennantConstants.RCD_ADD.equals(aScoringSlab.getRecordType()) && isNew){
				tranType =PennantConstants.TRAN_ADD;
			} else if(PennantConstants.RECORD_TYPE_NEW.equals(aScoringSlab.getRecordType())){
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
			logger.error("Exception: ", e);
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
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(PennantConstants.TRAN_DEL.equals(tranType)){
						if(PennantConstants.RECORD_TYPE_UPD.equals(aScoringSlab.getRecordType())){
							aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							scoringSlabList.add(aScoringSlab);
						}else if(PennantConstants.RCD_ADD.equals(aScoringSlab.getRecordType())){
							recordAdded=true;
						}else if(PennantConstants.RECORD_TYPE_NEW.equals(aScoringSlab.getRecordType())){
							aScoringSlab.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							scoringSlabList.add(aScoringSlab);
						}else if(PennantConstants.RECORD_TYPE_CAN.equals(aScoringSlab.getRecordType())){
							recordAdded=true;
							for (int j = 0; j < getScoringGroupDialogCtrl().getScoringSlabList().size(); j++) {
								ScoringSlab scorslab = getScoringGroupDialogCtrl().getScoringSlabList().get(j);
								if( aScoringSlab.getScoringSlab()== scorslab.getScoringSlab()){
									scoringSlabList.add(scorslab);
								}
							}
						}else if(PennantConstants.RECORD_TYPE_DEL.equals(aScoringSlab.getRecordType())){
							aScoringSlab.setNewRecord(true);
						}
					}else{
						if(!PennantConstants.TRAN_UPD.equals(tranType)){
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
	
	// WorkFlow Components

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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ScoringSlabDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
       doShowNotes(this.scoringSlab);
		
	 }
	
	@Override
	protected String getReference() {
		return String.valueOf(this.scoringSlab.getScoreGroupId());
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
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
