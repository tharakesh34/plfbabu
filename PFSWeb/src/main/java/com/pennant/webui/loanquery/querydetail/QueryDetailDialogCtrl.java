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
 * FileName    		:  QueryDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-05-2018    														*
 *                                                                  						*
 * Modified Date    :  09-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-05-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.loanquery.querydetail;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/LoanQuery/QueryDetail/queryDetailDialog.zul file. <br>
 */
public class QueryDetailDialogCtrl extends GFCBaseCtrl<QueryDetail>{

	private static final long serialVersionUID = 1L;
	private static final  Logger logger = Logger.getLogger(QueryDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_QueryDetailDialog; 
    protected ExtendedCombobox 		finReference; 
    protected ExtendedCombobox 		categoryId; 
	protected Space			space_QryNotes;
	protected Textbox 		qryNotes; 
	protected Space			space_AssignedRole;
	protected Textbox 		assignedRole; 
	protected Space			space_NotifyTo;
	protected Textbox 		notifyTo; 
	protected Space			space_Status;
	protected Textbox 		status; 
	protected Space			space_RaisedBy;
   	protected Longbox 		raisedBy; 
	protected Space			space_RaisedOn;
  	protected Datebox 		raisedOn; 
	protected Textbox 		responsNotes; 
   	protected Longbox 		responseBy; 
  	protected Datebox 		responseOn; 
	protected Textbox 		closerNotes; 
   	protected Longbox 		closerBy;
   	protected Longbox 		id; 
  	protected Datebox 		closerOn; 
  	
  	//protected Textbox 		code; 
   	//protected Textbox 		description; 
  	
	private QueryDetail queryDetail; // overhanded per param

	private transient QueryDetailListCtrl queryDetailListCtrl; // overhanded per param
	private transient QueryDetailService queryDetailService;
	

	/**
	 * default constructor.<br>
	 */
	public QueryDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "QueryDetailDialog";
	}
	
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(String.valueOf(this.queryDetail.getId()));
		return referenceBuffer.toString();
	}

	
	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_QueryDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_QueryDetailDialog);

		
		try {
			// Get the required arguments.
			this.queryDetail = (QueryDetail) arguments.get("queryDetail");
			this.queryDetailListCtrl = (QueryDetailListCtrl) arguments.get("queryDetailListCtrl");

			if (this.queryDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			QueryDetail queryDetail = new QueryDetail();
			BeanUtils.copyProperties(this.queryDetail, queryDetail);
			this.queryDetail.setBefImage(queryDetail);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.queryDetail.isWorkflow(), this.queryDetail.getWorkflowId(),
					this.queryDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if(!enqiryModule){
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName,getRole());
			}else{
				getUserWorkspace().allocateAuthorities(this.pageRightName,null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.queryDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		
		logger.debug(Literal.LEAVING);
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		doReadOnly();
		this.finReference.setMandatoryStyle(false);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("finReference");
		this.finReference.setDescColumn("finReferenceName");
		this.finReference.setValidateColumns(new String[] {"finReference"});
		this.categoryId.setMandatoryStyle(false);
		this.categoryId.setModuleName("QueryCategory");
		this.categoryId.setValueColumn("id");
		this.categoryId.setDescColumn("description");
		this.categoryId.setValidateColumns(new String[] {"id"});
		this.categoryId.setMaxlength(10);
		this.qryNotes.setMaxlength(2000);
		this.assignedRole.setMaxlength(100);
		this.notifyTo.setMaxlength(1000);
		this.status.setMaxlength(8);
	 	this.raisedBy.setMaxlength(19);
	 	this.raisedOn.setFormat(PennantConstants.dateTimeFormat);
		this.responsNotes.setMaxlength(50);
	 	this.responseBy.setMaxlength(19);
	 	this.responseOn.setFormat(PennantConstants.dateTimeFormat);
		this.closerNotes.setMaxlength(10);
	 	this.closerBy.setMaxlength(19);
	 	this.closerOn.setFormat(PennantConstants.dateTimeFormat);
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_QueryDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_QueryDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_QueryDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_QueryDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
		
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event)  throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.queryDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		queryDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.queryDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	

      public void onFul$fillfinReference(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	
    	logger.debug(Literal.LEAVING);
	}	


      public void onFul$fillcategoryId(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	
    	logger.debug(Literal.LEAVING);
	}	
















	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param queryDetail
	 * 
	 */
	public void doWriteBeanToComponents(QueryDetail aQueryDetail) {
		logger.debug(Literal.ENTERING);
	
		   this.finReference.setValue(aQueryDetail.getFinReference());
		   this.categoryId.setValue(aQueryDetail.getCategoryCode());
			this.qryNotes.setValue(aQueryDetail.getQryNotes());
			this.assignedRole.setValue(aQueryDetail.getAssignedRole());
			this.notifyTo.setValue(aQueryDetail.getNotifyTo());
			this.status.setValue(aQueryDetail.getStatus());
			this.raisedBy.setValue(aQueryDetail.getRaisedBy());
			this.raisedOn.setValue(aQueryDetail.getRaisedOn());
			this.responsNotes.setValue(aQueryDetail.getResponsNotes());
			this.responseBy.setValue(aQueryDetail.getResponseBy());
			this.responseOn.setValue(aQueryDetail.getResponseOn());
			this.closerNotes.setValue(aQueryDetail.getCloserNotes());
			this.closerBy.setValue(aQueryDetail.getCloserBy());
			this.closerOn.setValue(aQueryDetail.getCloserOn());
			//this.id.setValue(aQueryDetail.getId());
			//this.code.setValue(aQueryDetail.getCategoryCode());
			//this.description.setValue(aQueryDetail.getCategoryCode());
		if (aQueryDetail.isNewRecord()){
			   this.finReference.setDescription("");
			   this.categoryId.setDescription("");
		}else{
			   this.finReference.setDescription(aQueryDetail.getFinReference());
			   this.categoryId.setDescription(aQueryDetail.getCategoryIdName());
		}
		
		this.recordStatus.setValue(aQueryDetail.getRecordStatus());
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aQueryDetail
	 */
	public void doWriteComponentsToBean(QueryDetail aQueryDetail) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Finance Reference
		try {
			this.finReference.getValidatedValue();
			Object obj = this.finReference.getAttribute("FinReference");
			if (obj != null) {
				aQueryDetail.setFinReference((String) obj);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Category Id
		try {
			this.categoryId.getValidatedValue();
			Object obj = this.categoryId.getAttribute("CategoryId");
			if (obj != null) {
				aQueryDetail.setCategoryId(Long.valueOf(String.valueOf(obj)));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Notes
		try {
		    aQueryDetail.setQryNotes(this.qryNotes.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Assigned Role
		try {
		    aQueryDetail.setAssignedRole(this.assignedRole.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Notify To
		try {
		    aQueryDetail.setNotifyTo(this.notifyTo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Status
		try {
		    aQueryDetail.setStatus(this.status.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Raised By
		try {
		    aQueryDetail.setRaisedBy(this.raisedBy.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Raised On
		try {
	 		if(this.raisedOn.getValue()!=null){
	 			aQueryDetail.setRaisedOn(new Timestamp(this.raisedOn.getValue().getTime()));
	 		}	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Respons Notes
		try {
		    aQueryDetail.setResponsNotes(this.responsNotes.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Response By
		try {
		    aQueryDetail.setResponseBy(this.responseBy.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Response On
		try {
	 		if(this.responseOn.getValue()!=null){
	 			aQueryDetail.setResponseOn(new Timestamp(this.responseOn.getValue().getTime()));
	 		}	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Closer Notes
		try {
		    aQueryDetail.setCloserNotes(this.closerNotes.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Closer By
		try {
		    aQueryDetail.setCloserBy(this.closerBy.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Closer On
		try {
	 		if(this.closerOn.getValue()!=null){
	 			aQueryDetail.setCloserOn(new Timestamp(this.closerOn.getValue().getTime()));
	 		}	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param queryDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(QueryDetail queryDetail) {
		logger.debug(Literal.LEAVING);

		if (queryDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.qryNotes.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(queryDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.qryNotes.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(queryDetail);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.finReference.isReadonly()){
			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_FinReference.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		if (!this.categoryId.isReadonly()){
			this.categoryId.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_CategoryId.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		if (!this.qryNotes.isReadonly()){
			this.qryNotes.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_QryNotes.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		if (!this.assignedRole.isReadonly()){
			this.assignedRole.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_AssignedRole.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		if (!this.notifyTo.isReadonly()){
			this.notifyTo.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_NotifyTo.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		if (!this.status.isReadonly()){
			this.status.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_Status.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		if (!this.raisedBy.isReadonly()){
			this.raisedBy.setConstraint(new PTNumberValidator(Labels.getLabel("label_QueryDetailDialog_RaisedBy.value"),true,false,0));
		}
		if (!this.raisedOn.isReadonly()){
			this.raisedOn.setConstraint(new PTDateValidator(Labels.getLabel("label_QueryDetailDialog_RaisedOn.value"),false));
		}
		if (!this.responsNotes.isReadonly()){
			this.responsNotes.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_ResponsNotes.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.responseBy.isReadonly()){
			this.responseBy.setConstraint(new PTNumberValidator(Labels.getLabel("label_QueryDetailDialog_ResponseBy.value"),false,false,0));
		}
		if (!this.responseOn.isReadonly()){
			this.responseOn.setConstraint(new PTDateValidator(Labels.getLabel("label_QueryDetailDialog_ResponseOn.value"),false));
		}
		if (!this.closerNotes.isReadonly()){
			this.closerNotes.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_CloserNotes.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.closerBy.isReadonly()){
			this.closerBy.setConstraint(new PTNumberValidator(Labels.getLabel("label_QueryDetailDialog_CloserBy.value"),false,false,0));
		}
		if (!this.closerOn.isReadonly()){
			this.closerOn.setConstraint(new PTDateValidator(Labels.getLabel("label_QueryDetailDialog_CloserOn.value"),false));
		}
	
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		
		this.finReference.setConstraint("");
		this.categoryId.setConstraint("");
		this.qryNotes.setConstraint("");
		this.assignedRole.setConstraint("");
		this.notifyTo.setConstraint("");
		this.status.setConstraint("");
		this.raisedBy.setConstraint("");
		this.raisedOn.setConstraint("");
		this.responsNotes.setConstraint("");
		this.responseBy.setConstraint("");
		this.responseOn.setConstraint("");
		this.closerNotes.setConstraint("");
		this.closerBy.setConstraint("");
		this.closerOn.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		
	
	logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a QueryDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final QueryDetail aQueryDetail = new QueryDetail();
		BeanUtils.copyProperties(this.queryDetail, aQueryDetail);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aQueryDetail.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aQueryDetail.getRecordType()).equals("")){
				aQueryDetail.setVersion(aQueryDetail.getVersion()+1);
				aQueryDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aQueryDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aQueryDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aQueryDetail.getNextTaskId(), aQueryDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aQueryDetail,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				MessageUtil.showError(e);
			}
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		
		if (this.queryDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			
		}
	
			readOnlyComponent(isReadOnly("QueryDetailDialog_FinReference"), this.finReference);
			readOnlyComponent(isReadOnly("QueryDetailDialog_CategoryId"), this.categoryId);
			readOnlyComponent(isReadOnly("QueryDetailDialog_QryNotes"), this.qryNotes);
			readOnlyComponent(isReadOnly("QueryDetailDialog_AssignedRole"), this.assignedRole);
			readOnlyComponent(isReadOnly("QueryDetailDialog_NotifyTo"), this.notifyTo);
			readOnlyComponent(isReadOnly("QueryDetailDialog_Status"), this.status);
			readOnlyComponent(isReadOnly("QueryDetailDialog_RaisedBy"), this.raisedBy);
			readOnlyComponent(isReadOnly("QueryDetailDialog_RaisedOn"), this.raisedOn);
			readOnlyComponent(isReadOnly("QueryDetailDialog_ResponsNotes"), this.responsNotes);
			readOnlyComponent(isReadOnly("QueryDetailDialog_ResponseBy"), this.responseBy);
			readOnlyComponent(isReadOnly("QueryDetailDialog_ResponseOn"), this.responseOn);
			readOnlyComponent(isReadOnly("QueryDetailDialog_CloserNotes"), this.closerNotes);
			readOnlyComponent(isReadOnly("QueryDetailDialog_CloserBy"), this.closerBy);
			readOnlyComponent(isReadOnly("QueryDetailDialog_CloserOn"), this.closerOn);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.queryDetail.isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
			}

			
		logger.debug(Literal.LEAVING);
	}	
			
		/**
		 * Set the components to ReadOnly. <br>
		 */
		public void doReadOnly() {
			logger.debug(Literal.LEAVING);
			
	
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.categoryId);
			readOnlyComponent(true, this.qryNotes);
			readOnlyComponent(true, this.assignedRole);
			readOnlyComponent(true, this.notifyTo);
			readOnlyComponent(true, this.status);
			readOnlyComponent(true, this.raisedBy);
			readOnlyComponent(true, this.raisedOn);
			readOnlyComponent(true, this.responsNotes);
			readOnlyComponent(true, this.responseBy);
			readOnlyComponent(true, this.responseOn);
			readOnlyComponent(true, this.closerNotes);
			readOnlyComponent(true, this.closerBy);
			readOnlyComponent(true, this.closerOn);

			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(true);
				}
				this.recordStatus.setValue("");
				this.userAction.setSelectedIndex(0);
	
			}

			logger.debug(Literal.LEAVING);
		}

		
		/**
		 * Clears the components values. <br>
		 */
		public void doClear() {
			logger.debug("Entering");
			  	this.finReference.setValue("");
			  	this.finReference.setDescription("");
			  	this.categoryId.setValue("");
			  	this.categoryId.setDescription("");
				this.qryNotes.setValue("");
				this.assignedRole.setValue("");
				this.notifyTo.setValue("");
				this.status.setValue("");
				this.raisedBy.setText("");
				this.raisedOn.setText("");
				this.responsNotes.setValue("");
				this.responseBy.setText("");
				this.responseOn.setText("");
				this.closerNotes.setValue("");
				this.closerBy.setText("");
				this.closerOn.setText("");

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final QueryDetail aQueryDetail = new QueryDetail();
			BeanUtils.copyProperties(this.queryDetail, aQueryDetail);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aQueryDetail);

			isNew = aQueryDetail.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aQueryDetail.getRecordType())) {
					aQueryDetail.setVersion(aQueryDetail.getVersion() + 1);
					if (isNew) {
						aQueryDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aQueryDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aQueryDetail.setNewRecord(true);
					}
				}
			} else {
				aQueryDetail.setVersion(aQueryDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aQueryDetail, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (final DataAccessException e) {
				logger.error(e);
				MessageUtil.showError(e);
			}
			logger.debug("Leaving");
		}

		/**
		 * Set the workFlow Details List to Object
		 * 
		 * @param aAuthorizedSignatoryRepository
		 *            (AuthorizedSignatoryRepository)
		 * 
		 * @param tranType
		 *            (String)
		 * 
		 * @return boolean
		 * 
		 */
		private boolean doProcess(QueryDetail aQueryDetail, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aQueryDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
			aQueryDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aQueryDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aQueryDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aQueryDetail.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aQueryDetail);
					}

					if (isNotesMandatory(taskId, aQueryDetail)) {
						if (!notesEntered) {
							MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}

					}
				}
				if (!StringUtils.isBlank(nextTaskId)) {
					String[] nextTasks = nextTaskId.split(";");

					if (nextTasks != null && nextTasks.length > 0) {
						for (int i = 0; i < nextTasks.length; i++) {

							if (nextRoleCode.length() > 1) {
								nextRoleCode = nextRoleCode.concat(",");
							}
							nextRoleCode = getTaskOwner(nextTasks[i]);
						}
					} else {
						nextRoleCode = getTaskOwner(nextTaskId);
					}
				}

				aQueryDetail.setTaskId(taskId);
				aQueryDetail.setNextTaskId(nextTaskId);
				aQueryDetail.setRoleCode(getRole());
				aQueryDetail.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aQueryDetail, tranType);
				String operationRefs = getServiceOperations(taskId, aQueryDetail);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aQueryDetail, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aQueryDetail, tranType);
				processCompleted = doSaveProcess(auditHeader, null);
			}

			logger.debug("Leaving");
			return processCompleted;
		}

		/**
		 * Get the result after processing DataBase Operations
		 * 
		 * @param AuditHeader
		 *            auditHeader
		 * @param method
		 *            (String)
		 * @return boolean
		 * 
		 */

		private boolean doSaveProcess(AuditHeader auditHeader, String method) {
			logger.debug("Entering");
			boolean processCompleted = false;
			int retValue = PennantConstants.porcessOVERIDE;
			QueryDetail aQueryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = queryDetailService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = queryDetailService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = queryDetailService.doApprove(auditHeader);

							if (aQueryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = queryDetailService.doReject(auditHeader);
							if (aQueryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_QueryDetailDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_QueryDetailDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.queryDetail), true);
						}
					}

					if (retValue == PennantConstants.porcessOVERIDE) {
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

			logger.debug("Leaving");
			return processCompleted;
		}

		/**
		 * @param aAuthorizedSignatoryRepository
		 * @param tranType
		 * @return
		 */

		private AuditHeader getAuditHeader(QueryDetail aQueryDetail, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aQueryDetail.getBefImage(), aQueryDetail);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aQueryDetail.getUserDetails(),
					getOverideMap());
		}

		public void setQueryDetailService(QueryDetailService queryDetailService) {
			this.queryDetailService = queryDetailService;
		}
			
}
