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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.service.finance.commodity.CommodityDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance.Commodity/CommodityDetail/commodityDetailDialog.zul file.
 */
public class CommodityDetailDialogCtrl extends GFCBaseCtrl<CommodityDetail> {
	private static final long   serialVersionUID = 5409464429980669752L;
	private static final Logger logger = Logger.getLogger(CommodityDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  window_CommodityDetailDialog;      // autoWired
	protected Textbox commodityCode;                     // autoWired
	protected Textbox commodityName;                     // autoWired
	protected Textbox commodityUnitCode;                 // autoWired
	protected Textbox commodityUnitName;                 // autoWired

	// not auto wired variables
	private CommodityDetail commodityDetail;            // over handed per parameters
	private transient CommodityDetailListCtrl commodityDetailListCtrl; 
	// over handed per parameters

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient CommodityDetailService commodityDetailService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();

	/**
	 * default constructor.<br>
	 */
	public CommodityDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommodityDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CommodityDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommodityDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CommodityDetailDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();
			// READ OVERHANDED parameters !
			if (arguments.containsKey("commodityDetail")) {
				this.commodityDetail = (CommodityDetail) arguments
						.get("commodityDetail");
				CommodityDetail befImage = new CommodityDetail();
				BeanUtils.copyProperties(this.commodityDetail, befImage);
				this.commodityDetail.setBefImage(befImage);

				setCommodityDetail(this.commodityDetail);
			} else {
				setCommodityDetail(null);
			}

			doLoadWorkFlow(this.commodityDetail.isWorkflow(),
					this.commodityDetail.getWorkflowId(),
					this.commodityDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CommodityDetailDialog");
			}

			// READ OVERHANDED parameters!
			// we get the commodityDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete commodityDetail here.
			if (arguments.containsKey("commodityDetailListCtrl")) {
				setCommodityDetailListCtrl((CommodityDetailListCtrl) arguments
						.get("commodityDetailListCtrl"));
			} else {
				setCommodityDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCommodityDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CommodityDetailDialog.onClose();
		}
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
		MessageUtil.showHelpWindow(event, window_CommodityDetailDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}
	
	
	/**
	 * when the "Notes" button is clicked. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.commodityDetail);
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.commodityDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
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
	 * @throws Exception
	 */
	public void doShowDialog(CommodityDetail aCommodityDetail) throws Exception {
		logger.debug("Entering");

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

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_CommodityDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}
	/**
	 * Display Message in Error Box
	 * @param e
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CommodityDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	// Helpers
	
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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	/**
	 * 
	 */
	@Override
	protected void doClearMessage() {
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
					PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));

		}	
		if (!this.commodityName.isReadonly()){
			this.commodityName.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityDetailDialog_CommodityName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));

		}	
		if (!this.commodityUnitCode.isReadonly()){
			this.commodityUnitCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityDetailDialog_CommodityUnitCode.value"),
					PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));

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

	/** 
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCommodityDetail().getCommodityCode()+PennantConstants.KEY_SEPERATOR+getCommodityDetail().getCommodityUnitCode();
	}

	/**
	 * Get Audit Header Details
	 * @param aCommodityDetail
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CommodityDetail aCommodityDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommodityDetail.getBefImage(), aCommodityDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aCommodityDetail.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCommodityDetailListCtrl().search();
	}

	// CRUD operations

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_CommodityDetailDialog_CommodityCode.value")+" : "+aCommodityDetail.getCommodityCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCommodityDetail.getRecordType())){
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
					closeDialog(); 
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
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

		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aCommodityDetail.getRecordType())){
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
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
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

		aCommodityDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCommodityDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommodityDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";

			aCommodityDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCommodityDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCommodityDetail);
				}

				if (isNotesMandatory(taskId, aCommodityDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}


			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();
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

			aCommodityDetail.setTaskId(taskId);
			aCommodityDetail.setNextTaskId(nextTaskId);
			aCommodityDetail.setRoleCode(getRole());
			aCommodityDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCommodityDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aCommodityDetail);

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

				if (StringUtils.isBlank(method)){
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999
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
						deleteNotes(getNotes(this.commodityDetail),true);
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

}
