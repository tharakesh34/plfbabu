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
 * FileName    		:  FinanceWorkFlowDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.financeworkflow;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CAFFacilityType;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceWorkFlow/financeWorkFlowDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceWorkFlowDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4959034105708570551L;
	private final static Logger logger = Logger.getLogger(FinanceWorkFlowDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinanceWorkFlowDialog; 	// autoWired
	protected Textbox 		finType; 						// autoWired
	protected Combobox 		screenCode; 					// autoWired
	protected Textbox 		workFlowType; 					// autoWired
	protected Combobox 		moduleName; 					// autoWired
	protected Label 		recordStatus; 					// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired variables
	private FinanceWorkFlow financeWorkFlow; // overHanded per parameter
	private transient FinanceWorkFlowListCtrl financeWorkFlowListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_finType;
	private transient String  		oldVar_moduleName;
	private transient String  		oldVar_screenCode;
	private transient String  		oldVar_workFlowType;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinanceWorkFlowDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire

	protected Button btnSearchFinType; 		// autoWire
	protected Textbox lovDescFinTypeName;
	private transient String 		oldVar_lovDescFinTypeName;
	
	protected Button btnSearchWorkFlowType; // autoWire
	protected Textbox lovDescWorkFlowTypeName;
	private transient String 		oldVar_lovDescWorkFlowTypeName;

	// ServiceDAOs / Domain Classes
	private transient FinanceWorkFlowService financeWorkFlowService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public FinanceWorkFlowDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceWorkFlow object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceWorkFlowDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeWorkFlow")) {
			this.financeWorkFlow = (FinanceWorkFlow) args.get("financeWorkFlow");
			FinanceWorkFlow befImage =new FinanceWorkFlow();
			BeanUtils.copyProperties(this.financeWorkFlow, befImage);
			this.financeWorkFlow.setBefImage(befImage);

			setFinanceWorkFlow(this.financeWorkFlow);
		} else {
			setFinanceWorkFlow(null);
		}

		doLoadWorkFlow(this.financeWorkFlow.isWorkflow(),this.financeWorkFlow.getWorkflowId(),this.financeWorkFlow.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceWorkFlowDialog");
		}

		setListScreenCode();

		// READ OVERHANDED parameters !
		// we get the financeWorkFlowListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeWorkFlow here.
		if (args.containsKey("financeWorkFlowListCtrl")) {
			setFinanceWorkFlowListCtrl((FinanceWorkFlowListCtrl) args.get("financeWorkFlowListCtrl"));
		} else {
			setFinanceWorkFlowListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceWorkFlow());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.workFlowType.setMaxlength(50);
        this.screenCode.setSelectedIndex(0);
        readOnlyComponent(true, this.screenCode);
        
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

		getUserWorkspace().alocateAuthorities("FinanceWorkFlowDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceWorkFlowDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceWorkFlowDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceWorkFlowDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceWorkFlowDialog_btnSave"));
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
	public void onClose$window_FinanceWorkFlowDialog(Event event) throws Exception {
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
		// remember the old variables
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
		PTMessageUtils.showHelpWindow(event, window_FinanceWorkFlowDialog);
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
			closeDialog(this.window_FinanceWorkFlowDialog, "FinanceWorkFlowDialog");	
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
	 * @param aFinanceWorkFlow
	 *            FinanceWorkFlow
	 */
	public void doWriteBeanToComponents(FinanceWorkFlow aFinanceWorkFlow) {
		logger.debug("Entering") ;
		
		fillComboBox(this.moduleName, aFinanceWorkFlow.getModuleName(), PennantStaticListUtil.getWorkFlowModules(), "");
		this.finType.setValue(aFinanceWorkFlow.getFinType());
		this.screenCode.setValue(PennantAppUtil.getlabelDesc(aFinanceWorkFlow.getScreenCode(),PennantStaticListUtil.getScreenCodes()));
		this.workFlowType.setValue(aFinanceWorkFlow.getWorkFlowType());

		if (aFinanceWorkFlow.isNewRecord()){
			this.lovDescFinTypeName.setValue("");
			this.lovDescWorkFlowTypeName.setValue("");
		}else{
			if (aFinanceWorkFlow.getModuleName().equals(PennantConstants.WORFLOW_MODULE_FINANCE)) {
				this.lovDescFinTypeName.setValue(aFinanceWorkFlow.getFinType()+"-"+aFinanceWorkFlow.getLovDescFinTypeName());
			}else if (aFinanceWorkFlow.getModuleName().equals(PennantConstants.WORFLOW_MODULE_FACILITY)) {
				this.lovDescFinTypeName.setValue(aFinanceWorkFlow.getFinType()+"-"+aFinanceWorkFlow.getLovDescFacilityTypeName());
			}
			this.lovDescWorkFlowTypeName.setValue(aFinanceWorkFlow.getWorkFlowType()+"-"+aFinanceWorkFlow.getLovDescWorkFlowTypeName());
		}
		this.recordStatus.setValue(aFinanceWorkFlow.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceWorkFlow
	 */
	public void doWriteComponentsToBean(FinanceWorkFlow aFinanceWorkFlow) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.moduleName.getSelectedItem() == null || this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.moduleName,Labels.getLabel("FIELD_IS_MAND",new String[]{Labels.getLabel("label_FinanceWorkFlowDialog_moduleName.value")}));
			}
			aFinanceWorkFlow.setModuleName(this.moduleName.getSelectedItem().getValue().toString());
			
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceWorkFlow.setLovDescFinTypeName(this.lovDescFinTypeName.getValue());
			aFinanceWorkFlow.setFinType(this.finType.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceWorkFlow.setScreenCode(this.screenCode.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceWorkFlow.setLovDescWorkFlowTypeName(this.lovDescWorkFlowTypeName.getValue());
			aFinanceWorkFlow.setWorkFlowType(this.workFlowType.getValue());	
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

		aFinanceWorkFlow.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceWorkFlow
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceWorkFlow aFinanceWorkFlow) throws InterruptedException {
		logger.debug("Entering") ;

		// if aFinanceWorkFlow == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aFinanceWorkFlow == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aFinanceWorkFlow = getFinanceWorkFlowService().getNewFinanceWorkFlow();

			setFinanceWorkFlow(aFinanceWorkFlow);
		} else {
			setFinanceWorkFlow(aFinanceWorkFlow);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinanceWorkFlow.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.screenCode.focus();
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
			doWriteBeanToComponents(aFinanceWorkFlow);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_FinanceWorkFlowDialog);
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_moduleName = this.moduleName.getSelectedItem().getValue().toString();
		this.oldVar_lovDescFinTypeName = this.lovDescFinTypeName.getValue();
		this.oldVar_screenCode = this.screenCode.getValue();
		this.oldVar_workFlowType = this.workFlowType.getValue();
		this.oldVar_lovDescWorkFlowTypeName = this.lovDescWorkFlowTypeName.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finType.setValue(this.oldVar_finType);
		this.lovDescFinTypeName.setValue(this.oldVar_lovDescFinTypeName);
		this.screenCode.setValue(this.oldVar_screenCode);
		this.workFlowType.setValue(this.oldVar_workFlowType);
		this.lovDescWorkFlowTypeName.setValue(this.oldVar_lovDescWorkFlowTypeName);
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
		
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_moduleName != this.moduleName.getSelectedItem().getValue().toString()) {
			return true;
		}
		if (this.oldVar_screenCode != this.screenCode.getValue()) {
			return true;
		}
		if (this.oldVar_workFlowType != this.workFlowType.getValue()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.screenCode.isDisabled()){
			this.screenCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceWorkFlowDialog_ScreenCode.value"), null, true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.screenCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */	
	private void doSetLOVValidation() {
		this.lovDescFinTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceWorkFlowDialog_FinType.value"), null, true));
		this.lovDescWorkFlowTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceWorkFlowDialog_WorkFlowType.value"), null, true));
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		this.lovDescFinTypeName.setConstraint("");
		this.lovDescWorkFlowTypeName.setConstraint("");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.lovDescFinTypeName.setErrorMessage("");
		this.screenCode.setErrorMessage("");
		this.lovDescWorkFlowTypeName.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		final JdbcSearchObject<FinanceWorkFlow> soFinanceWorkFlow = getFinanceWorkFlowListCtrl().getSearchObj();
		getFinanceWorkFlowListCtrl().pagingFinanceWorkFlowList.setActivePage(0);
		getFinanceWorkFlowListCtrl().getPagedListWrapper().setSearchObject(soFinanceWorkFlow);
		if(getFinanceWorkFlowListCtrl().listBoxFinanceWorkFlow!=null){
			getFinanceWorkFlowListCtrl().listBoxFinanceWorkFlow.getListModel();
		}
	} 
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a FinanceWorkFlow object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinanceWorkFlow aFinanceWorkFlow = new FinanceWorkFlow();
		BeanUtils.copyProperties(getFinanceWorkFlow(), aFinanceWorkFlow);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceWorkFlow.getFinType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceWorkFlow.getRecordType()).equals("")){
				aFinanceWorkFlow.setVersion(aFinanceWorkFlow.getVersion()+1);
				aFinanceWorkFlow.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aFinanceWorkFlow.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFinanceWorkFlow,tranType)){
					refreshList();
					closeDialog(this.window_FinanceWorkFlowDialog, "FinanceWorkFlowDialog"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FinanceWorkFlow object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		
		final FinanceWorkFlow aFinanceWorkFlow = getFinanceWorkFlowService().getNewFinanceWorkFlow();
		aFinanceWorkFlow.setNewRecord(true);
		setFinanceWorkFlow(aFinanceWorkFlow);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.finType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFinanceWorkFlow().isNewRecord()){
			this.btnSearchFinType.setDisabled(false);
			this.moduleName.setDisabled(false);
			this.btnCancel.setVisible(false);
		}else{
			this.btnSearchFinType.setDisabled(true);
			this.moduleName.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.screenCode.setDisabled(isReadOnly("FinanceWorkFlowDialog_screenCode"));
		this.btnSearchWorkFlowType.setDisabled(isReadOnly("FinanceWorkFlowDialog_workFlowType"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeWorkFlow.isNewRecord()){
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
		this.btnSearchFinType.setDisabled(true);
		this.screenCode.setDisabled(true);
		this.btnSearchWorkFlowType.setDisabled(true);

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
		this.finType.setValue("");
		this.lovDescFinTypeName.setValue("");
		this.screenCode.setValue("");
		this.workFlowType.setValue("");
		this.lovDescWorkFlowTypeName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceWorkFlow aFinanceWorkFlow = new FinanceWorkFlow();
		BeanUtils.copyProperties(getFinanceWorkFlow(), aFinanceWorkFlow);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the FinanceWorkFlow object with the components data
		doWriteComponentsToBean(aFinanceWorkFlow);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinanceWorkFlow.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceWorkFlow.getRecordType()).equals("")){
				aFinanceWorkFlow.setVersion(aFinanceWorkFlow.getVersion()+1);
				if(isNew){
					aFinanceWorkFlow.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceWorkFlow.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceWorkFlow.setNewRecord(true);
				}
			}
		}else{
			aFinanceWorkFlow.setVersion(aFinanceWorkFlow.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aFinanceWorkFlow,tranType)){
				refreshList();
				closeDialog(this.window_FinanceWorkFlowDialog, "FinanceWorkFlowDialog");
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
	 * @param aFinanceWorkFlow (FinanceWorkFlow)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(FinanceWorkFlow aFinanceWorkFlow,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aFinanceWorkFlow.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceWorkFlow.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceWorkFlow.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String
			aFinanceWorkFlow.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceWorkFlow.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceWorkFlow);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aFinanceWorkFlow))) {
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

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
		
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

			aFinanceWorkFlow.setTaskId(taskId);
			aFinanceWorkFlow.setNextTaskId(nextTaskId);
			aFinanceWorkFlow.setRoleCode(getRole());
			aFinanceWorkFlow.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aFinanceWorkFlow, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aFinanceWorkFlow);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aFinanceWorkFlow, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aFinanceWorkFlow, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
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
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		FinanceWorkFlow aFinanceWorkFlow = (FinanceWorkFlow) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinanceWorkFlowService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getFinanceWorkFlowService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinanceWorkFlowService().doApprove(auditHeader);

						if(aFinanceWorkFlow.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinanceWorkFlowService().doReject(auditHeader);
						if(aFinanceWorkFlow.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceWorkFlowDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_FinanceWorkFlowDialog, auditHeader);
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
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void onClick$btnSearchFinType(Event event){
		logger.debug("Entering" + event.toString());
		
		if (this.moduleName.getSelectedItem() == null || this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
			throw new WrongValueException(this.moduleName,Labels.getLabel("FIELD_IS_MAND",new String[]{Labels.getLabel("label_FinanceWorkFlowDialog_moduleName.value")}));
		}
		Object dataObject=null;
		if (this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.WORFLOW_MODULE_FINANCE)) {
			dataObject = ExtendedSearchListBox.show(this.window_FinanceWorkFlowDialog,"FinanceType");
		}else if (this.moduleName.getSelectedItem().getValue().toString().equals(PennantConstants.WORFLOW_MODULE_FACILITY)) {
			dataObject = ExtendedSearchListBox.show(this.window_FinanceWorkFlowDialog,"CAFFacilityType");
		}
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		} else {
			if (dataObject instanceof FinanceType) {
				FinanceType details = (FinanceType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFinType());
					this.lovDescFinTypeName.setValue(details.getFinType() + "-" + details.getFinTypeDesc());
				}
			} else if (dataObject instanceof CAFFacilityType) {
				CAFFacilityType details = (CAFFacilityType) dataObject;
				if (details != null) {
					this.finType.setValue(details.getFacilityType());
					this.lovDescFinTypeName.setValue(details.getFacilityType() + "-" + details.getFacilityDesc());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnSearchWorkFlowType(Event event){
		logger.debug("Entering" + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceWorkFlowDialog,"WorkFlowDetails");
		if (dataObject instanceof String){
			this.workFlowType.setValue(dataObject.toString());
			this.lovDescWorkFlowTypeName.setValue("");
		}else{
			WorkFlowDetails details= (WorkFlowDetails) dataObject;
			if (details != null) {
				this.workFlowType.setValue(details.getWorkFlowType());
				this.lovDescWorkFlowTypeName.setValue(details.getWorkFlowType()+"-"+details.getWorkFlowDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**	
	 *Method to set values to Screen Code ComboBox 
	 */
	private void setListScreenCode(){
		logger.debug("Entering");
		
		List<ValueLabel> listScreenCode = PennantStaticListUtil.getScreenCodes();
		for (int i = 0; i < listScreenCode.size(); i++) {
			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listScreenCode.get(i).getLabel());
			comboitem.setValue(listScreenCode.get(i).getValue());
			this.screenCode.appendChild(comboitem);
		} 
		logger.debug("Leaving");
	}
	
	public void onChange$moduleName(Event event){
		logger.debug("Entering" + event.toString());
		this.finType.setValue("");
		this.lovDescFinTypeName.setValue("");
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
	private AuditHeader getAuditHeader(FinanceWorkFlow aFinanceWorkFlow, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceWorkFlow.getBefImage(), aFinanceWorkFlow);   
		return new AuditHeader(aFinanceWorkFlow.getFinType(),null,null,null,auditDetail,aFinanceWorkFlow.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinanceWorkFlowDialog, auditHeader);
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
		logger.debug("Entering");
		// logger.debug(event.toString());

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
		logger.debug("Leaving");
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("FinanceWorkFlow");
		notes.setReference(getFinanceWorkFlow().getFinType());
		notes.setVersion(getFinanceWorkFlow().getVersion());
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

	public FinanceWorkFlow getFinanceWorkFlow() {
		return this.financeWorkFlow;
	}
	public void setFinanceWorkFlow(FinanceWorkFlow financeWorkFlow) {
		this.financeWorkFlow = financeWorkFlow;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}
	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return this.financeWorkFlowService;
	}

	public void setFinanceWorkFlowListCtrl(FinanceWorkFlowListCtrl financeWorkFlowListCtrl) {
		this.financeWorkFlowListCtrl = financeWorkFlowListCtrl;
	}
	public FinanceWorkFlowListCtrl getFinanceWorkFlowListCtrl() {
		return this.financeWorkFlowListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
	
}
