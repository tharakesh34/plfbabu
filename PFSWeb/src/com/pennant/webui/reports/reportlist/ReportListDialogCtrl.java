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
import org.zkoss.zk.ui.SuspendNotAllowedException;
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
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Reports/ReportList/reportListDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ReportListDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 7403304686538288944L;
	private final static Logger logger = Logger.getLogger(ReportListDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_ReportListDialog; 	// autoWired
	protected Combobox 	module; 					// autoWired
	protected Textbox 	fieldLabels; 				// autoWired
	protected Textbox 	fieldValues; 				// autoWired
	protected Textbox 	fieldType; 					// autoWired
	protected Textbox 	addfields; 					// autoWired
	protected Combobox 	reportFileName; 			// autoWired
	protected Textbox 	reportHeading; 				// autoWired
	protected Textbox 	reportFile; 				// autoWired
	protected Textbox 	moduleType; 				// autoWired

	protected Label 		recordStatus; 			// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	

	// not auto wired variables
	private ReportList reportList; 									// overHanded per parameter
	private ReportList prvReportList; 								// overHanded per parameter
	private transient ReportListListCtrl 	reportListListCtrl; 	// overHanded per parameter
	private transient FieldsListSelectCtrl 	fieldsListSelectCtrl; 	// overHanded per parameter
	private transient PTListReportUtils 	ptListReportUtils;

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_module;
	private transient String  		oldVar_fieldLabels;
	private transient String  		oldVar_fieldValues;
	private transient String  		oldVar_fieldType;
	private transient String  		oldVar_addfields;
	private transient String  		oldVar_reportFileName;
	private transient String  		oldVar_reportHeading;
	private transient String  		oldVar_moduleType;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ReportListDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire
	protected Button btnConfigure; 	// autoWire

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ReportList> searchObj;
	
	// ServiceDAOs / Domain Classes
	private transient ReportListService reportListService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	private List<ValueLabel> listReportFileName=PennantStaticListUtil.getReportListName(); // autoWired
	private List<ValueLabel> moduleList = PennantAppUtil.getModuleList();

	/**
	 * default constructor.<br>
	 */
	public ReportListDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ReportList object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReportListDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("reportList")) {
			this.reportList = (ReportList) args.get("reportList");
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
			getUserWorkspace().alocateRoleAuthorities(getRole(), "ReportListDialog");
		}

		setModuleNamesList();
		setListReportFileName();

		// READ OVERHANDED parameters !
		// we get the reportListListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete reportList here.
		if (args.containsKey("reportListListCtrl")) {
			setReportListListCtrl((ReportListListCtrl) args.get("reportListListCtrl"));
		} else {
			setReportListListCtrl(null);
		}

		if (args.containsKey("fieldListSelectCtrl")) {
			this.setFieldsListSelectCtrl((FieldsListSelectCtrl) args.get("fieldListSelectCtrl"));			
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

		getUserWorkspace().alocateAuthorities("ReportListDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ReportListDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReportListDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReportListDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ReportListDialog_btnSave"));
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
	public void onClose$window_ReportListDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
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
		PTMessageUtils.showHelpWindow(event, window_ReportListDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" +event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" +event.toString());
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
			closeDialog(this.window_ReportListDialog, "ReportList");	
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
	 * @param aReportList
	 *            ReportList
	 */
	public void doWriteBeanToComponents(ReportList aReportList) {
		logger.debug("Entering") ;
		this.module.setValue(aReportList.getModule());
		this.fieldLabels.setValue(aReportList.getFieldLabels());
		this.fieldValues.setValue(aReportList.getFieldValues());
		this.fieldType.setValue(aReportList.getFieldType());
		this.addfields.setValue(aReportList.getAddfields());
		this.reportHeading.setValue(aReportList.getReportHeading());
		this.reportFile.setValue(aReportList.getReportFileName());
		this.reportFileName.setValue(PennantAppUtil.getValueDesc(aReportList.getReportFileName(),
				PennantStaticListUtil.getReportListName()));
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
			aReportList.setModule(this.module.getValue().toString());
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
			aReportList.setReportFileName(this.reportFile.getValue().toString());
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(ReportList aReportList) throws InterruptedException {
		logger.debug("Entering") ;

		// if aReportList == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aReportList == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aReportList = getReportListService().getNewReportList();

			setReportList(aReportList);
		} else {
			setReportList(aReportList);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aReportList.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.module.focus();
		} else {
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aReportList.getRecordType()).equals("")) {
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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ReportListDialog);
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
		this.oldVar_module = this.module.getValue();
		this.oldVar_fieldLabels = this.fieldLabels.getValue();
		this.oldVar_fieldValues = this.fieldValues.getValue();
		this.oldVar_fieldType = this.fieldType.getValue();
		this.oldVar_addfields = this.addfields.getValue();
		this.oldVar_reportFileName = this.reportFileName.getValue();
		this.oldVar_reportHeading = this.reportHeading.getValue();
		this.oldVar_moduleType = this.moduleType.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.module.setValue(this.oldVar_module);
		this.fieldLabels.setValue(this.oldVar_fieldLabels);
		this.fieldValues.setValue(this.oldVar_fieldValues);
		this.fieldType.setValue(this.oldVar_fieldType);
		this.addfields.setValue(this.oldVar_addfields);
		this.reportFileName.setValue(this.oldVar_reportFileName);
		this.reportHeading.setValue(this.oldVar_reportHeading);
		this.moduleType.setValue(this.oldVar_moduleType);
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

		if (this.oldVar_module != this.module.getValue()) {
			return true;
		}
		if (this.oldVar_fieldLabels != this.fieldLabels.getValue()) {
			return true;
		}
		if (this.oldVar_fieldValues != this.fieldValues.getValue()) {
			return true;
		}
		if (this.oldVar_fieldType != this.fieldType.getValue()) {
			return true;
		}
		if (this.oldVar_addfields != this.addfields.getValue()) {
			return true;
		}
		if (this.oldVar_reportFileName != this.reportFileName.getValue()) {
			return true;
		}
		if (this.oldVar_reportHeading != this.reportHeading.getValue()) {
			return true;
		}
		if (this.oldVar_moduleType != this.moduleType.getValue()) {
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

		if (!this.module.isDisabled()){
			this.module.setConstraint(new StaticListValidator(moduleList,
					Labels.getLabel("label_ReportListDialog_Module.value")));
		}
		if (!this.reportFile.isDisabled()){
			this.reportFile.setConstraint(new StaticListValidator(
					listReportFileName,Labels.getLabel("label_ReportListDialog_ReportFileName.value")));
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
	private void doClearMessage() {
		logger.debug("Entering");
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aReportList.getModule();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aReportList.getRecordType()).equals("")){
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
					closeDialog(this.window_ReportListDialog, "ReportList"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new ReportList object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final ReportList aReportList = getReportListService().getNewReportList();
		aReportList.setNewRecord(true);
		setReportList(aReportList);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.module.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getReportList().isNewRecord()){
			this.module.setDisabled(false);
			this.btnCancel.setVisible(false);
			this.reportFile.setReadonly(true);
		}else{
			this.module.setDisabled(true);
			this.btnCancel.setVisible(true);
			this.reportFile.setReadonly(true);
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
		// remember the old variables
		doStoreInitValues();
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

		this.module.setValue("");
		this.fieldLabels.setValue("");
		this.fieldValues.setValue("");
		this.fieldType.setValue("");
		this.addfields.setValue("");
		this.reportFileName.setValue("");
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aReportList.getRecordType()).equals("")){
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
				doWriteBeanToComponents(aReportList);
				refreshList();
				closeDialog(this.window_ReportListDialog, "ReportList");
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

		aReportList.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aReportList.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReportList.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aReportList.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReportList.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aReportList);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aReportList))) {
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

			aReportList.setTaskId(taskId);
			aReportList.setNextTaskId(nextTaskId);
			aReportList.setRoleCode(getRole());
			aReportList.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aReportList, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aReportList);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for Preparing Report File Names List
	 */
	private void setListReportFileName(){
		for (int i = 0; i < listReportFileName.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listReportFileName.get(i).getValue());
			comboitem.setValue(listReportFileName.get(i).getValue());
			this.reportFileName.appendChild(comboitem);
		} 
	}

	/**
	 * Method to be called after selecting report file name comboBox
	 */
	public void onSelect$reportFileName(Event event){
		if(reportFileName.getSelectedItem().getValue().toString() != null){
			this.reportFile.setValue(listReportFileName.get(reportFileName.getSelectedIndex()).getLabel());
			if(reportFileName.getSelectedItem().getValue() == "Others"){
				this.reportFile.setReadonly(false);
				this.reportFile.focus();
			} else {
				this.reportFile.setReadonly(true);
			}
		}
	}

	/**
	 * Method for Preparing Report Module Names List
	 */
	private void setModuleNamesList() {
		logger.debug("Entering ");
		for (int i = 0; i < moduleList.size(); i++) {
			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(moduleList.get(i).getLabel());
			comboitem.setValue(moduleList.get(i).getValue());
			this.module.appendChild(comboitem);
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Event to load the field values as a list. 
	 * 
	 * @throws InterruptedException,SuspendNotAllowedException
	 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$btnConfigure(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap map = new HashMap();
		map.put("reportListDialogCtrl", this);
		map.put("reportList", getReportList());
		map.put("newRecord", true);
		map.put("moduleName", this.module.getValue());
		map.put("fileName", this.reportFile.getValue());
		map.put("btnConfigure", this.btnConfigure.getLabel());
		Executions.createComponents("/WEB-INF/pages/Reports/ReportList/FieldsListSelect.zul",null, map);
		logger.debug("Leaving");

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ReportListDialog, auditHeader);
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
		notes.setModuleName("ReportList");
		notes.setReference(getReportList().getModule());
		notes.setVersion(getReportList().getVersion());
		return notes;
	}

	// Method for refreshing the list after successful updation
	private void refreshList(){
		final JdbcSearchObject<ReportList> soReportList = getReportListListCtrl().getSearchObj();
		getReportListListCtrl().pagingReportListList.setActivePage(0);
		getReportListListCtrl().getPagedListWrapper().setSearchObject(soReportList);
		if(getReportListListCtrl().listBoxReportList!=null){
			getReportListListCtrl().listBoxReportList.getListModel();
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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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
