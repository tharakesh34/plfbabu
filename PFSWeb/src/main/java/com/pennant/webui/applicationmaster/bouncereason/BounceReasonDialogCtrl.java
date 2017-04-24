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
 * FileName    		:  BounceReasonDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.bouncereason;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.service.applicationmaster.BounceReasonService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennanttech.pff.core.Literal;
	

/**
 * This is the controller class for the
 * /WEB-INF/pages/applicationmaster/BounceReason/bounceReasonDialog.zul file. <br>
 */
public class BounceReasonDialogCtrl extends GFCBaseCtrl<BounceReason>{

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(BounceReasonDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BounceReasonDialog; 
	protected Textbox 		bounceCode; 
 	protected Combobox 		reasonType; 
 	protected Combobox 		category; 
	protected Textbox 		reason; 
 	protected Combobox 		action; 
    protected ExtendedCombobox 		feeID; 
    protected Textbox 		returnCode; 
    protected Checkbox 		active; 
	private BounceReason bounceReason; // overhanded per param

	private transient BounceReasonListCtrl bounceReasonListCtrl; // overhanded per param
	private transient BounceReasonService bounceReasonService;
	
	private List<ValueLabel> listReasonType=PennantStaticListUtil.getReasonType();
	private List<ValueLabel> listCategory=PennantStaticListUtil.getCategoryType();
	private List<ValueLabel> listAction=PennantStaticListUtil.getAction();

	/**
	 * default constructor.<br>
	 */
	public BounceReasonDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BounceReasonDialog";
	}
	
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(String.valueOf(this.bounceReason.getBounceID()));
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
	public void onCreate$window_BounceReasonDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_BounceReasonDialog);

		
		try {
			// Get the required arguments.
			this.bounceReason = (BounceReason) arguments.get("bounceReason");
			this.bounceReasonListCtrl = (BounceReasonListCtrl) arguments.get("bounceReasonListCtrl");

			if (this.bounceReason == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			BounceReason bounceReason = new BounceReason();
			BeanUtils.copyProperties(this.bounceReason, bounceReason);
			this.bounceReason.setBefImage(bounceReason);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.bounceReason.isWorkflow(), this.bounceReason.getWorkflowId(),
					this.bounceReason.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName,getRole());
			}else{
				getUserWorkspace().allocateAuthorities(this.pageRightName,null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.bounceReason);
		} catch (Exception e) {
			logger.error("Exception:", e);
			closeDialog();
			MessageUtil.showError(e.toString());
		}
		
		logger.debug(Literal.LEAVING);
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		
			this.bounceCode.setMaxlength(8);
			this.reason.setMaxlength(50);
			this.feeID.setModuleName("FeeType");
			this.feeID.setMandatoryStyle(true);
			this.feeID.setValueColumn("FeeTypeCode");
			this.feeID.setDescColumn("FeeTypeDesc");
			this.feeID.setValidateColumns(new String[] {"FeeTypeCode"});
			
			this.active.setValue("true");
			
		
		setStatusDetails();
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BounceReasonDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BounceReasonDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BounceReasonDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BounceReasonDialog_btnSave"));
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
		doShowNotes(this.bounceReason);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		bounceReasonListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.bounceReason.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	
	/*public void onFulfill$feeID(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = feeID.getObject();
		if (dataObject instanceof String) {
			this.feeID.setValue(dataObject.toString());
			this.feeID.setDescription("");
		} else {
			FeeType details = (FeeType) dataObject;
			if (details != null) {
				this.feeID.setAttribute("FeeID", details.getFeeTypeID());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
*/
   	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param bounceReason
	 * 
	 */
	public void doWriteBeanToComponents(BounceReason aBounceReason) {
		logger.debug(Literal.ENTERING);
	
			this.bounceCode.setValue(aBounceReason.getBounceCode());
			fillComboBox(this.reasonType, String.valueOf(aBounceReason.getReasonType()), listReasonType,"");
			fillComboBox(this.category, String.valueOf(aBounceReason.getCategory()), listCategory,"");
			this.reason.setValue(aBounceReason.getReason());
			fillComboBox(this.action, String.valueOf(aBounceReason.getAction()), listAction,"");
			
			this.feeID.setObject(new FeeType(aBounceReason.getFeeID()));
			this.feeID.setValue(aBounceReason.getFeeTypeCode(),aBounceReason.getFeeIDName());
			
			this.returnCode.setValue(aBounceReason.getReturnCode());
			this.active.setChecked(aBounceReason.isActive());
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBounceReason
	 */
	public void doWriteComponentsToBean(BounceReason aBounceReason) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Bounce Code
		try {
		    aBounceReason.setBounceCode(this.bounceCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Reason Type
		try {
			String strReasonType =null; 
			if(this.reasonType.getSelectedItem()!=null){
				strReasonType = this.reasonType.getSelectedItem().getValue().toString();
			}
			if(strReasonType!= null && !PennantConstants.List_Select.equals(strReasonType)){
				aBounceReason.setReasonType(Integer.parseInt(strReasonType));
	
			}else{
				aBounceReason.setReasonType(0);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Category
		try {
			String strCategory =null; 
			if(this.category.getSelectedItem()!=null){
				strCategory = this.category.getSelectedItem().getValue().toString();
			}
			if(strCategory!= null && !PennantConstants.List_Select.equals(strCategory)){
				aBounceReason.setCategory(Integer.parseInt(strCategory));
	
			}else{
				aBounceReason.setCategory(0);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Reason
		try {
		    aBounceReason.setReason(this.reason.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Action
		try {
			String strAction =null; 
			if(this.action.getSelectedItem()!=null){
				strAction = this.action.getSelectedItem().getValue().toString();
			}
			if(strAction!= null && !PennantConstants.List_Select.equals(strAction)){
				aBounceReason.setAction(Integer.parseInt(strAction));
	
			}else{
				aBounceReason.setAction(0);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Fee ID
		try {
			this.feeID.getValidatedValue();
			FeeType feeType = (FeeType) this.feeID.getObject();
			aBounceReason.setFeeID(feeType.getFeeTypeID());
			aBounceReason.setFeeTypeCode(feeType.getFeeTypeCode());
			aBounceReason.setFeeIDName(feeType.getFeeTypeDesc());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Return Code
		try {
		    aBounceReason.setReturnCode(this.returnCode.getValue());
			}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Active
		try {
			aBounceReason.setActive(this.active.isChecked());
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
	 * @param bounceReason
	 *            The entity that need to be render.
	 */
	public void doShowDialog(BounceReason bounceReason) {
		logger.debug(Literal.LEAVING);

		if (bounceReason.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bounceCode.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(bounceReason.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.bounceCode.focus();
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

		doWriteBeanToComponents(bounceReason);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.bounceCode.isReadonly()){
			this.bounceCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceReasonDialog_BounceCode.value"),PennantRegularExpressions.REGEX_ALPHANUM,true));
		}
		if (!this.reasonType.isReadonly()){
			this.reasonType.setConstraint(new StaticListValidator(listReasonType,Labels.getLabel("label_BounceReasonDialog_ReasonType.value")));
		}
		if (!this.category.isReadonly()){
			this.category.setConstraint(new StaticListValidator(listCategory,Labels.getLabel("label_BounceReasonDialog_Category.value")));
		}
		if (!this.reason.isReadonly()){
			this.reason.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceReasonDialog_Reason.value"),PennantRegularExpressions.REGEX_DESCRIPTION,true));
		}
		if (!this.feeID.isReadonly()){
			this.feeID.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceReasonDialog_FeeID.value"),PennantRegularExpressions.REGEX_ALPHANUM,true));
		}
		if (!this.returnCode.isReadonly()){
			this.returnCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceReasonDialog_ReturnCode.value"),PennantRegularExpressions.REGEX_ALPHANUM,true));
		}
	
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		
		this.bounceCode.setConstraint("");
		this.reasonType.setConstraint("");
		this.category.setConstraint("");
		this.reason.setConstraint("");
		this.action.setConstraint("");
		this.feeID.setConstraint("");
		this.returnCode.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		//Bounce ID
		//Bounce Code
		//Reason Type
		//Category
		//Reason
		//Action
		//Fee ID
		//Return ID
		//Active
		
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
	 * Deletes a BounceReason object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final BounceReason aBounceReason = new BounceReason();
		BeanUtils.copyProperties(this.bounceReason, aBounceReason);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aBounceReason.getBounceID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aBounceReason.getRecordType()).equals("")){
				aBounceReason.setVersion(aBounceReason.getVersion()+1);
				aBounceReason.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aBounceReason.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aBounceReason.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aBounceReason.getNextTaskId(), aBounceReason);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aBounceReason,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				logger.error("Exception",  e);
				showErrorMessage(this.window_BounceReasonDialog,e);
			}
			
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		
		if (this.bounceReason.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.bounceCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.bounceCode);
			
		}
	
			readOnlyComponent(isReadOnly("BounceReasonDialog_ReasonType"), this.reasonType);
			readOnlyComponent(isReadOnly("BounceReasonDialog_Category"), this.category);
			readOnlyComponent(isReadOnly("BounceReasonDialog_Reason"), this.reason);
			readOnlyComponent(isReadOnly("BounceReasonDialog_Action"), this.action);
			readOnlyComponent(isReadOnly("BounceReasonDialog_FeeID"), this.feeID);
			readOnlyComponent(isReadOnly("BounceReasonDialog_ReturnCode"), this.returnCode);
			readOnlyComponent(isReadOnly("BounceReasonDialog_Active"), this.active);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.bounceReason.isNewRecord()) {
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
			
	
			readOnlyComponent(true, this.bounceCode);
			readOnlyComponent(true, this.reasonType);
			readOnlyComponent(true, this.category);
			readOnlyComponent(true, this.reason);
			readOnlyComponent(true, this.action);
			readOnlyComponent(true, this.feeID);
			readOnlyComponent(true, this.returnCode);
			readOnlyComponent(true, this.active);

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
				this.bounceCode.setValue("");
			 	this.reasonType.setSelectedIndex(0);
			 	this.category.setSelectedIndex(0);
				this.reason.setValue("");
			 	this.action.setSelectedIndex(0);
			  	this.feeID.setValue("");
			  	this.feeID.setDescription("");
			  	this.returnCode.setValue("");
				this.active.setChecked(false);

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final BounceReason aBounceReason = new BounceReason();
			BeanUtils.copyProperties(this.bounceReason, aBounceReason);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aBounceReason);

			isNew = aBounceReason.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aBounceReason.getRecordType())) {
					aBounceReason.setVersion(aBounceReason.getVersion() + 1);
					if (isNew) {
						aBounceReason.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aBounceReason.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aBounceReason.setNewRecord(true);
					}
				}
			} else {
				aBounceReason.setVersion(aBounceReason.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aBounceReason, tranType)) {
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
		private boolean doProcess(BounceReason aBounceReason, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aBounceReason.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aBounceReason.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aBounceReason.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aBounceReason.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aBounceReason.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aBounceReason);
					}

					if (isNotesMandatory(taskId, aBounceReason)) {
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

				aBounceReason.setTaskId(taskId);
				aBounceReason.setNextTaskId(nextTaskId);
				aBounceReason.setRoleCode(getRole());
				aBounceReason.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aBounceReason, tranType);
				String operationRefs = getServiceOperations(taskId, aBounceReason);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aBounceReason, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aBounceReason, tranType);
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
			BounceReason aBounceReason = (BounceReason) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = bounceReasonService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = bounceReasonService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = bounceReasonService.doApprove(auditHeader);

							if (aBounceReason.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = bounceReasonService.doReject(auditHeader);
							if (aBounceReason.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_BounceReasonDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_BounceReasonDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.bounceReason), true);
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

		private AuditHeader getAuditHeader(BounceReason aBounceReason, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aBounceReason.getBefImage(), aBounceReason);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aBounceReason.getUserDetails(),
					getOverideMap());
		}

		public void setBounceReasonService(BounceReasonService bounceReasonService) {
			this.bounceReasonService = bounceReasonService;
		}
			
}
