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
 * FileName    		:  CommodityDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.commodity.commoditydetail;

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
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.commodity.CommodityDetailService;
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
 * /WEB-INF/pages/Finance.Commodity/CommodityDetail/commodityDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CommodityDetailDialogCtrl extends GFCBaseCtrl implements Serializable {


	private static final long   serialVersionUID = 5409464429980669752L;
	private final static Logger logger = Logger.getLogger(CommodityDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_CommodityDetailDialog;      // autoWired
	protected Textbox commodityCode;                     // autoWired
	protected Textbox commodityName;                     // autoWired
	protected Textbox commodityUnitCode;                 // autoWired
	protected Textbox commodityUnitName;                 // autoWired

	protected Label   recordStatus;                      // autoWired
	protected Button  btnNew;                            // autoWired
	protected Button  btnEdit;                           // autoWired
	protected Button  btnDelete;                         // autoWired
	protected Button  btnSave;                           // autoWired
	protected Button  btnCancel;                         // autoWired
	protected Button  btnClose;                          // autoWired
	protected Button  btnHelp;                           // autoWired
	protected Button  btnNotes;                          // autoWired

	protected Radiogroup userAction;
	protected Groupbox   groupboxWf;

	// not auto wired variables
	private CommodityDetail commodityDetail;            // over handed per parameters
	private CommodityDetail prvCommodityDetail;         // over handed per parameters
	private transient CommodityDetailListCtrl commodityDetailListCtrl; 
	// over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_commodityCode;
	private transient String  		oldVar_commodityName;
	private transient String  		oldVar_commodityUnitCode;
	private transient String  		oldVar_commodityUnitName;
	private transient String        oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean           notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String     btnCtroller_ClassPrefix = "button_CommodityDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	// ServiceDAOs / Domain Classes
	private transient CommodityDetailService commodityDetailService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();


	/**
	 * default constructor.<br>
	 */
	public CommodityDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CommodityDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommodityDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("commodityDetail")) {
			this.commodityDetail = (CommodityDetail) args.get("commodityDetail");
			CommodityDetail befImage =new CommodityDetail();
			BeanUtils.copyProperties(this.commodityDetail, befImage);
			this.commodityDetail.setBefImage(befImage);

			setCommodityDetail(this.commodityDetail);
		} else {
			setCommodityDetail(null);
		}

		doLoadWorkFlow(this.commodityDetail.isWorkflow(),this.commodityDetail.getWorkflowId(),this.commodityDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CommodityDetailDialog");
		}


		// READ OVERHANDED parameters!
		// we get the commodityDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete commodityDetail here.
		if (args.containsKey("commodityDetailListCtrl")) {
			setCommodityDetailListCtrl((CommodityDetailListCtrl) args.get("commodityDetailListCtrl"));
		} else {
			setCommodityDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCommodityDetail());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CommodityDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CommodityDetailDialog);
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
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * when the "Notes" button is clicked. <br>
	 * @param event
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ GUI Process+++++++++++++++++++++++
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
			closeDialog(this.window_CommodityDetailDialog, "CommodityDetailDialog");	
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
	 * @param aCommodityDetail
	 *            CommodityDetail
	 */
	public void doWriteBeanToComponents(CommodityDetail aCommodityDetail) {
		logger.debug("Entering") ;
		this.commodityCode.setValue(aCommodityDetail.getCommodityCode());
		this.commodityName.setValue(aCommodityDetail.getCommodityName());
		this.commodityUnitCode.setValue(aCommodityDetail.getCommodityUnitCode());
		this.commodityUnitName.setValue(aCommodityDetail.getCommodityUnitName());

		this.recordStatus.setValue(aCommodityDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCommodityDetail
	 */
	public void doWriteComponentsToBean(CommodityDetail aCommodityDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCommodityDetail.setCommodityCode(this.commodityCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityDetail.setCommodityName(this.commodityName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityDetail.setCommodityUnitCode(this.commodityUnitCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityDetail.setCommodityUnitName(this.commodityUnitName.getValue());
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

		aCommodityDetail.setRecordStatus(this.recordStatus.getValue());
		setCommodityDetail(aCommodityDetail);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCommodityDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CommodityDetail aCommodityDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aCommodityDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCommodityDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aCommodityDetail = getCommodityDetailService().getNewCommodityDetail();

			setCommodityDetail(aCommodityDetail);
		} else {
			setCommodityDetail(aCommodityDetail);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aCommodityDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.commodityCode.focus();
		} else {
			this.commodityName.focus();
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
			doWriteBeanToComponents(aCommodityDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CommodityDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}
	/**
	 * Display Message in Error Box
	 * @param e
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CommodityDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.commodityCode.setMaxlength(8);
		this.commodityName.setMaxlength(100);
		this.commodityUnitCode.setMaxlength(8);
		this.commodityUnitName.setMaxlength(100);

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

		getUserWorkspace().alocateAuthorities("CommodityDetailDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_commodityCode = this.commodityCode.getValue();
		this.oldVar_commodityName = this.commodityName.getValue();
		this.oldVar_commodityUnitCode = this.commodityUnitCode.getValue();
		this.oldVar_commodityUnitName = this.commodityUnitName.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.commodityCode.setValue(this.oldVar_commodityCode);
		this.commodityName.setValue(this.oldVar_commodityName);
		this.commodityUnitCode.setValue(this.oldVar_commodityUnitCode);
		this.commodityUnitName.setValue(this.oldVar_commodityUnitName);
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

		if (this.oldVar_commodityCode != this.commodityCode.getValue()) {
			return true;
		}
		if (this.oldVar_commodityName != this.commodityName.getValue()) {
			return true;
		}
		if (this.oldVar_commodityUnitCode != this.commodityUnitCode.getValue()) {
			return true;
		}
		if (this.oldVar_commodityUnitName != this.commodityUnitName.getValue()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}
	/**
	 * 
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.commodityCode.setErrorMessage("");
		this.commodityName.setErrorMessage("");
		this.commodityUnitCode.setErrorMessage("");
		this.commodityUnitName.setErrorMessage("");
		logger.debug("Leaving");
	}


	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.commodityCode.isReadonly()){
			this.commodityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityDetailDialog_CommodityCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));

		}	
		if (!this.commodityName.isReadonly()){
			this.commodityName.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityDetailDialog_CommodityName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));

		}	
		if (!this.commodityUnitCode.isReadonly()){
			this.commodityUnitCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityDetailDialog_CommodityUnitCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));

		}	
		if (!this.commodityUnitName.isReadonly()){
			this.commodityUnitName.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityDetailDialog_CommodityUnitName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));

		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.commodityCode.setConstraint("");
		this.commodityName.setConstraint("");
		this.commodityUnitCode.setConstraint("");
		this.commodityUnitName.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.commodityCode.setReadonly(true);
		this.commodityName.setReadonly(true);
		this.commodityUnitCode.setReadonly(true);
		this.commodityUnitName.setReadonly(true);

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

		this.commodityCode.setValue("");
		this.commodityName.setValue("");
		this.commodityUnitCode.setValue("");
		this.commodityUnitName.setValue("");
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		logger.debug("Entering ");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving ");
	}	

	private Notes getNotes(){
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("CommodityDetail");
		notes.setReference(getReference());
		notes.setVersion(getCommodityDetail().getVersion());
		logger.debug("Leaving ");
		return notes;
	}

	/** 
	 * Get the Reference value
	 */
	private String getReference(){
		return getCommodityDetail().getCommodityCode()+PennantConstants.KEY_SEPERATOR+getCommodityDetail().getCommodityUnitCode();
	}

	/**
	 * Get Audit Header Details
	 * @param aCommodityDetail
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CommodityDetail aCommodityDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommodityDetail.getBefImage(), aCommodityDetail);   
		return new AuditHeader(getReference(),null,null,null,auditDetail
				,aCommodityDetail.getUserDetails(),getOverideMap());
	}
	
	/**
	 * This Method  refresh the list after successful update
	 */
	private void refreshList(){
		logger.debug("Entering ");
		final JdbcSearchObject<CommodityDetail> soCommodityDetail = getCommodityDetailListCtrl().getSearchObj();
		getCommodityDetailListCtrl().pagingCommodityDetailList.setActivePage(0);
		getCommodityDetailListCtrl().getPagedListWrapper().setSearchObject(soCommodityDetail);
		if(getCommodityDetailListCtrl().listBoxCommodityDetail!=null){
			getCommodityDetailListCtrl().listBoxCommodityDetail.getListModel();
		}
		logger.debug("Leaving ");
	} 
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CommodityDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CommodityDetail aCommodityDetail = new CommodityDetail();
		BeanUtils.copyProperties(getCommodityDetail(), aCommodityDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCommodityDetail.getCommodityCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCommodityDetail.getRecordType()).equals("")){
				aCommodityDetail.setVersion(aCommodityDetail.getVersion()+1);
				aCommodityDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCommodityDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCommodityDetail,tranType)){
					refreshList();
					closeDialog(this.window_CommodityDetailDialog, "CommodityDetailDialog"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CommodityDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		doStoreInitValues();    	// remember the old variables
		final CommodityDetail aCommodityDetail = getCommodityDetailService().getNewCommodityDetail();
		aCommodityDetail.setNewRecord(true);
		setCommodityDetail(aCommodityDetail);
		doClear();                  // clear all components
		doEdit();                   // edit mode
		this.btnCtrl.setBtnStatus_New();
		
		this.commodityCode.focus();	// setFocus
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCommodityDetail().isNewRecord()){
			this.commodityCode.setReadonly(false);
			this.btnCancel.setVisible(false);	
		}else{
			this.commodityCode.setReadonly(true);
			this.commodityUnitCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.commodityName.setReadonly(isReadOnly("CommodityDetailDialog_commodityName"));
		this.commodityUnitName.setReadonly(isReadOnly("CommodityDetailDialog_commodityUnitName"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.commodityDetail.isNewRecord()){
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
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CommodityDetail aCommodityDetail = new CommodityDetail();
		BeanUtils.copyProperties(getCommodityDetail(), aCommodityDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CommodityDetail object with the components data
		doWriteComponentsToBean(aCommodityDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCommodityDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCommodityDetail.getRecordType()).equals("")){
				aCommodityDetail.setVersion(aCommodityDetail.getVersion()+1);
				if(isNew){
					aCommodityDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCommodityDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCommodityDetail.setNewRecord(true);
				}
			}
		}else{
			aCommodityDetail.setVersion(aCommodityDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCommodityDetail,tranType)){
				refreshList();
				closeDialog(this.window_CommodityDetailDialog, "CommodityDetailDialog");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	/**
	 *  Set the workFlow Details List to Object
	 * @param aCommodityDetail
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(CommodityDetail aCommodityDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCommodityDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCommodityDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommodityDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";

			aCommodityDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCommodityDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCommodityDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCommodityDetail))) {
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

			aCommodityDetail.setTaskId(taskId);
			aCommodityDetail.setNextTaskId(nextTaskId);
			aCommodityDetail.setRoleCode(getRole());
			aCommodityDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCommodityDetail, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCommodityDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCommodityDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aCommodityDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		CommodityDetail aCommodityDetail = (CommodityDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCommodityDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCommodityDetailService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCommodityDetailService().doApprove(auditHeader);

						if(aCommodityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCommodityDetailService().doReject(auditHeader);
						if(aCommodityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999
								, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CommodityDetailDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CommodityDetailDialog, auditHeader);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CommodityDetail getCommodityDetail() {
		return this.commodityDetail;
	}

	public void setCommodityDetail(CommodityDetail commodityDetail) {
		this.commodityDetail = commodityDetail;
	}

	public void setCommodityDetailService(CommodityDetailService commodityDetailService) {
		this.commodityDetailService = commodityDetailService;
	}

	public CommodityDetailService getCommodityDetailService() {
		return this.commodityDetailService;
	}

	public void setCommodityDetailListCtrl(CommodityDetailListCtrl commodityDetailListCtrl) {
		this.commodityDetailListCtrl = commodityDetailListCtrl;
	}

	public CommodityDetailListCtrl getCommodityDetailListCtrl() {
		return this.commodityDetailListCtrl;
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

	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public CommodityDetail getPrvCommodityDetail() {
		return prvCommodityDetail;
	}
}
