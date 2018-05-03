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
 * FileName    		:  ReportListDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2012    														*
 *                                                                  						*
 * Modified Date    :  23-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.reports.reportlist;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.reports.ReportListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Reports/ReportList/reportListDialog.zul file.
 */
public class ReportListDialogCtrl extends GFCBaseCtrl<ReportList> {
	private static final long serialVersionUID = 7403304686538288944L;
	private static final Logger logger = Logger.getLogger(ReportListDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_ReportListDialog; 	// autoWired
	protected Textbox 	code; 						// autoWired
	protected Combobox 	module; 					// autoWired
	protected Textbox 	fieldLabels; 				// autoWired
	protected Textbox 	fieldValues; 				// autoWired
	protected Textbox 	fieldType; 					// autoWired
	protected Textbox 	addfields; 					// autoWired
	protected Combobox 	reportFileName; 			// autoWired
	protected Textbox 	reportHeading; 				// autoWired
	protected Textbox 	moduleType; 				// autoWired

	

	// not auto wired variables
	private ReportList reportList; 									// overHanded per parameter
	private ReportList prvReportList; 								// overHanded per parameter
	private transient ReportListListCtrl 	reportListListCtrl; 	// overHanded per parameter
	private transient FieldsListSelectCtrl 	fieldsListSelectCtrl; 	// overHanded per parameter
	private transient PTListReportUtils 	ptListReportUtils;

	private transient boolean validationOn;
	
	protected Button btnConfigure; 	// autoWire

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ReportList> searchObj;
	
	// ServiceDAOs / Domain Classes
	private transient ReportListService reportListService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();

	private List<ValueLabel> listReportFileName=PennantStaticListUtil.getReportListName(); // autoWired
	private List<ValueLabel> moduleList = PennantAppUtil.getModuleList(false);

	/**
	 * default constructor.<br>
	 */
	public ReportListDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReportListDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ReportList object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReportListDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReportListDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("reportList")) {
			this.reportList = (ReportList) arguments.get("reportList");
			ReportList befImage =new ReportList();
			BeanUtils.copyProperties(this.reportList, befImage);
			this.reportList.setBefImage(befImage);

			setReportList(this.reportList);
		} else {
			setReportList(null);
		}

		doLoadWorkFlow(this.reportList.isWorkflow(),this.reportList.getWorkflowId(),this.reportList.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "ReportListDialog");
		}

		// READ OVERHANDED parameters !
		// we get the reportListListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete reportList here.
		if (arguments.containsKey("reportListListCtrl")) {
			setReportListListCtrl((ReportListListCtrl) arguments.get("reportListListCtrl"));
		} else {
			setReportListListCtrl(null);
		}

		if (arguments.containsKey("fieldListSelectCtrl")) {
			this.setFieldsListSelectCtrl((FieldsListSelectCtrl) arguments.get("fieldListSelectCtrl"));			
		} else {
			this.setFieldsListSelectCtrl(null);
		}
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getReportList());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.code.setMaxlength(50);
		this.reportHeading.setMaxlength(50);
		this.moduleType.setMaxlength(50);

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

		getUserWorkspace().allocateAuthorities("ReportListDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ReportListDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReportListDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReportListDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ReportListDialog_btnSave"));
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
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_ReportListDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		logger.debug("Leaving" +event.toString());
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
		doWriteBeanToComponents(this.reportList.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aReportList
	 *            ReportList
	 */
	public void doWriteBeanToComponents(ReportList aReportList) {
		logger.debug("Entering") ;
		this.code.setValue(aReportList.getCode());
		fillComboBox(module, aReportList.getModule(), moduleList, "");
		this.fieldLabels.setValue(aReportList.getFieldLabels());
		this.fieldValues.setValue(aReportList.getFieldValues());
		this.fieldType.setValue(aReportList.getFieldType());
		this.addfields.setValue(aReportList.getAddfields());
		this.reportHeading.setValue(aReportList.getReportHeading());
		fillComboBox(reportFileName, aReportList.getReportFileName(), PennantStaticListUtil.getReportListName(), "");
		this.moduleType.setValue(aReportList.getModuleType());

		this.recordStatus.setValue(aReportList.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReportList
	 */
	public void doWriteComponentsToBean(ReportList aReportList) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aReportList.setCode(this.code.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportList.setModule(this.module.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportList.setFieldLabels(aReportList.getFieldLabels());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportList.setFieldValues(aReportList.getFieldValues());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportList.setFieldType(aReportList.getFieldType());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportList.setAddfields(this.addfields.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportList.setReportHeading(this.reportHeading.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportList.setReportFileName(this.reportFileName.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportList.setModuleType(this.moduleType.getValue());
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

		aReportList.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aReportList
	 * @throws Exception
	 */
	public void doShowDialog(ReportList aReportList) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aReportList.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.code.focus();
		} else {
			if (isWorkFlowEnabled()){
				if (StringUtils.isNotBlank(aReportList.getRecordType())) {
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
			doWriteBeanToComponents(aReportList);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReportListDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		doClearMessage();
		if (!this.code.isReadonly()){
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_ReportListDialog_Code.value"), null, true));
		}
		if (!this.module.isDisabled()){
			this.module.setConstraint(new StaticListValidator(moduleList,Labels.getLabel("label_ReportListDialog_Module.value")));
		}
		if (!this.reportFileName.isDisabled()){
			this.reportFileName.setConstraint(new StaticListValidator(listReportFileName,Labels.getLabel("label_ReportListDialog_ReportFileName.value")));
		}
		if (!this.reportHeading.isReadonly()){
			this.reportHeading.setConstraint(new PTStringValidator(Labels.getLabel("label_ReportListDialog_ReportHeading.value"), null, true));
		}	
		if (!this.moduleType.isReadonly()){
			this.moduleType.setConstraint(new PTStringValidator(Labels.getLabel("label_ReportListDialog_ModuleType.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.code.setConstraint("");
		this.module.setConstraint("");
		this.fieldLabels.setConstraint("");
		this.fieldValues.setConstraint("");
		this.fieldType.setConstraint("");
		this.addfields.setConstraint("");
		this.reportFileName.setConstraint("");
		this.reportHeading.setConstraint("");
		this.moduleType.setConstraint("");
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
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.code.setErrorMessage("");
		this.module.setErrorMessage("");
		this.fieldLabels.setErrorMessage("");
		this.fieldValues.setErrorMessage("");
		this.fieldType.setErrorMessage("");
		this.addfields.setErrorMessage("");
		this.reportFileName.setErrorMessage("");
		this.reportHeading.setErrorMessage("");
		this.moduleType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a ReportList object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final ReportList aReportList = new ReportList();
		BeanUtils.copyProperties(getReportList(), aReportList);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_ReportListDialog_Code.value")+" : "+aReportList.getModule();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aReportList.getRecordType())){
				aReportList.setVersion(aReportList.getVersion()+1);
				aReportList.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aReportList.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aReportList,tranType)){
					refreshList();
					closeDialog(); 
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

		if (getReportList().isNewRecord()){
			this.code.setDisabled(false);
			this.module.setDisabled(false);
			this.btnCancel.setVisible(false);
		}else{
			this.code.setDisabled(true);
			this.module.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		
		if(getUserWorkspace().isAllowed("button_ReportListDialog_btnConfigure")){
			this.btnConfigure.setLabel(Labels.getLabel("label_ReportListDialog_btnConfigure.value"));
		} else {
			this.btnConfigure.setLabel(Labels.getLabel("label_ReportListDialog_btnConfiguration.value"));
			this.module.setDisabled(true);
		}
		
		this.fieldLabels.setReadonly(isReadOnly("ReportListDialog_fieldLabels"));
		this.fieldValues.setReadonly(isReadOnly("ReportListDialog_fieldValues"));
		this.fieldType.setReadonly(isReadOnly("ReportListDialog_fieldType"));
		this.addfields.setReadonly(isReadOnly("ReportListDialog_addfields"));
		this.reportFileName.setDisabled(isReadOnly("ReportListDialog_reportFileName"));
		this.btnConfigure.setDisabled(isReadOnly("ReportListDialog_reportFileName"));
		this.reportHeading.setReadonly(isReadOnly("ReportListDialog_reportHeading"));
		this.moduleType.setReadonly(isReadOnly("ReportListDialog_moduleType"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.reportList.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				this.reportFileName.focus();
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
		this.module.setDisabled(true);
		this.fieldLabels.setReadonly(true);
		this.fieldValues.setReadonly(true);
		this.fieldType.setReadonly(true);
		this.addfields.setReadonly(true);
		this.reportFileName.setDisabled(true);
		this.reportHeading.setReadonly(true);
		this.moduleType.setReadonly(true);

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

		this.code.setValue("");
		this.module.setValue("");
		this.fieldLabels.setValue("");
		this.fieldValues.setValue("");
		this.fieldType.setValue("");
		this.addfields.setValue("");
		this.reportFileName.setSelectedIndex(0);
		this.reportHeading.setValue("");
		this.moduleType.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final ReportList aReportList = new ReportList();
		BeanUtils.copyProperties(getReportList(), aReportList);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ReportList object with the components data
		doWriteComponentsToBean(aReportList);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aReportList.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReportList.getRecordType())){
				aReportList.setVersion(aReportList.getVersion()+1);
				if(isNew){
					aReportList.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aReportList.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReportList.setNewRecord(true);
				}
			}
		}else{
			aReportList.setVersion(aReportList.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aReportList,tranType)){
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aReportList
	 *            (ReportList)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ReportList aReportList,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aReportList.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aReportList.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReportList.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aReportList.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReportList.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReportList);
				}

				if (isNotesMandatory(taskId, aReportList)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aReportList.setTaskId(taskId);
			aReportList.setNextTaskId(nextTaskId);
			aReportList.setRoleCode(getRole());
			aReportList.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aReportList, tranType);

			String operationRefs = getServiceOperations(taskId, aReportList);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aReportList, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aReportList, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
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
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		ReportList aReportList = (ReportList) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getReportListService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getReportListService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getReportListService().doApprove(auditHeader);

						if(aReportList.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getReportListService().doReject(auditHeader);
						if(aReportList.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ReportListDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_ReportListDialog, auditHeader);
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
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Event to load the field values as a list. 
	 * 
	 * @throws InterruptedException,SuspendNotAllowedException
	 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$btnConfigure(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		doSetValidation();
		final HashMap map = new HashMap();
		map.put("reportListDialogCtrl", this);
		map.put("reportList", getReportList());
		map.put("newRecord", true);
		map.put("moduleName", this.module.getSelectedItem().getValue().toString());
		map.put("fileName", this.reportFileName.getSelectedItem().getValue().toString());
		map.put("btnConfigure", this.btnConfigure.getLabel());
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportList/FieldsListSelect.zul",null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aReportList
	 *            (ReportList)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(ReportList aReportList, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReportList.getBefImage(), aReportList);   
		return new AuditHeader(aReportList.getModule(),null,null,null,auditDetail,aReportList.getUserDetails(),getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ReportListDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.reportList);
	}

	// Get the notes entered for rejected reason
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("ReportList");
		notes.setReference(getReportList().getModule());
		notes.setVersion(getReportList().getVersion());
		return notes;
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getReportListListCtrl().search();
	} 
	
	@Override
	protected String getReference() {
		return String.valueOf(this.reportList);
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

	public ReportList getReportList() {
		return this.reportList;
	}
	public void setReportList(ReportList reportList) {
		this.reportList = reportList;
	}

	public void setReportListService(ReportListService reportListService) {
		this.reportListService = reportListService;
	}
	public ReportListService getReportListService() {
		return this.reportListService;
	}

	public void setReportListListCtrl(ReportListListCtrl reportListListCtrl) {
		this.reportListListCtrl = reportListListCtrl;
	}
	public ReportListListCtrl getReportListListCtrl() {
		return this.reportListListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public ReportList getPrvReportList() {
		return prvReportList;
	}

	public void setFieldsListSelectCtrl(FieldsListSelectCtrl fieldsListSelectCtrl) {
		this.fieldsListSelectCtrl = fieldsListSelectCtrl;
	}
	public FieldsListSelectCtrl getFieldsListSelectCtrl() {
		return fieldsListSelectCtrl;
	}

	public void setPtListReportUtils(PTListReportUtils ptListReportUtils) {
		this.ptListReportUtils = ptListReportUtils;
	}

	public PTListReportUtils getPtListReportUtils() {
		return ptListReportUtils;
	}

	public JdbcSearchObject<ReportList> getSearchObj() {
		return searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ReportList> searchObj) {
		this.searchObj = searchObj;
	}

}
