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
 * FileName    		:  ManualAdviseDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-04-2017    														*
 *                                                                  						*
 * Modified Date    :  23-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.finance.manualadvise;

import java.math.BigDecimal;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennanttech.pff.core.Literal;
	

/**
 * This is the controller class for the
 * /WEB-INF/pages/finance/ManualAdvise/manualAdviseDialog.zul file. <br>
 */
public class ManualAdviseDialogCtrl extends GFCBaseCtrl<ManualAdvise>{

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(ManualAdviseDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ManualAdviseDialog; 
 	protected Combobox 		adviseType; 
    protected ExtendedCombobox 		finReference; 
    protected ExtendedCombobox 		feeTypeID; 
   	protected Intbox 		sequence; 
	protected Decimalbox	adviseAmount; 
	protected Decimalbox	paidAmount; 
	protected Decimalbox	waivedAmount; 
	protected Textbox 		remarks; 
	private ManualAdvise manualAdvise; // overhanded per param

	private transient ManualAdviseListCtrl manualAdviseListCtrl; // overhanded per param
	private transient ManualAdviseService manualAdviseService;
	
	private List<ValueLabel> listAdviseType=PennantStaticListUtil.getManualAdviseTypes();

	/**
	 * default constructor.<br>
	 */
	public ManualAdviseDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManualAdviseDialog";
	}
	
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(String.valueOf(this.manualAdvise.getAdviseID()));
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
	public void onCreate$window_ManualAdviseDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_ManualAdviseDialog);

		
		try {
			// Get the required arguments.
			this.manualAdvise = (ManualAdvise) arguments.get("manualAdvise");
			this.manualAdviseListCtrl = (ManualAdviseListCtrl) arguments.get("manualAdviseListCtrl");

			if (this.manualAdvise == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			ManualAdvise manualAdvise = new ManualAdvise();
			BeanUtils.copyProperties(this.manualAdvise, manualAdvise);
			this.manualAdvise.setBefImage(manualAdvise);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.manualAdvise.isWorkflow(), this.manualAdvise.getWorkflowId(),
					this.manualAdvise.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName,getRole());
			}else{
				getUserWorkspace().allocateAuthorities(this.pageRightName,null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.manualAdvise);
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
		
			this.finReference.setModuleName("FinanceMain");
			this.finReference.setValueColumn("finReference");
			this.finReference.setDescColumn("finReferenceName");
			this.finReference.setValidateColumns(new String[] {"finReference"});
			Filter[] filters0 = new Filter[1] ;
			this.finReference.setFilters(filters0);
			this.feeTypeID.setModuleName("FeeType");
			this.feeTypeID.setValueColumn("feeTypeID");
			this.feeTypeID.setDescColumn("feeTypeIDName");
			this.feeTypeID.setValidateColumns(new String[] {"feeTypeID"});
			Filter[] filters1 = new Filter[1] ;
			this.feeTypeID.setFilters(filters1);
			this.sequence.setMaxlength(10);
		  	this.adviseAmount.setMaxlength(18);
		  	this.adviseAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		  	this.adviseAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		  	this.adviseAmount.setScale(PennantConstants.defaultCCYDecPos);
		  	this.paidAmount.setMaxlength(18);
		  	this.paidAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		  	this.paidAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		  	this.paidAmount.setScale(PennantConstants.defaultCCYDecPos);
		  	this.waivedAmount.setMaxlength(18);
		  	this.waivedAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		  	this.waivedAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		  	this.waivedAmount.setScale(PennantConstants.defaultCCYDecPos);
			this.remarks.setMaxlength(100);
		
		setStatusDetails();
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnSave"));
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
		doShowNotes(this.manualAdvise);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		manualAdviseListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.manualAdvise.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	


      public void onFulfillFinReference(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.finReference.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	


      public void onFulfillFeeTypeID(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.feeTypeID.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param manualAdvise
	 * 
	 */
	public void doWriteBeanToComponents(ManualAdvise aManualAdvise) {
		logger.debug(Literal.ENTERING);
	
			fillComboBox(this.adviseType, String.valueOf(aManualAdvise.getAdviseType()), listAdviseType,"");
		   this.finReference.setValue(aManualAdvise.getFinReference());
		   this.feeTypeID.setValue(aManualAdvise.getFeeTypeCode());
			this.sequence.setValue(aManualAdvise.getSequence());
	  		this.adviseAmount.setValue(PennantApplicationUtil.formateAmount(aManualAdvise.getAdviseAmount(),PennantConstants.defaultCCYDecPos));
	  		this.paidAmount.setValue(PennantApplicationUtil.formateAmount(aManualAdvise.getPaidAmount(),PennantConstants.defaultCCYDecPos));
	  		this.waivedAmount.setValue(PennantApplicationUtil.formateAmount(aManualAdvise.getWaivedAmount(),PennantConstants.defaultCCYDecPos));
			this.remarks.setValue(aManualAdvise.getRemarks());
		
		if (aManualAdvise.isNewRecord()){
			   this.finReference.setDescription("");
			   this.feeTypeID.setDescription("");
		}else{
			   this.finReference.setDescription(aManualAdvise.getFinReferenceName());
			   this.feeTypeID.setDescription(aManualAdvise.getFeeTypeCode());
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aManualAdvise
	 */
	public void doWriteComponentsToBean(ManualAdvise aManualAdvise) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Advise Type
		try {
			String strAdviseType =null; 
			if(this.adviseType.getSelectedItem()!=null){
				strAdviseType = this.adviseType.getSelectedItem().getValue().toString();
			}
			if(strAdviseType!= null && !PennantConstants.List_Select.equals(strAdviseType)){
				aManualAdvise.setAdviseType(Integer.parseInt(strAdviseType));
	
			}else{
				aManualAdvise.setAdviseType(0);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Loan Reference
		try {
			this.finReference.getValidatedValue();
			Object obj = this.finReference.getAttribute("FinReference");
			if (obj != null) {
				aManualAdvise.setFinReference(this.finReference.getValue());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Fee Type ID
		try {
			this.feeTypeID.getValidatedValue();
			Object obj = this.feeTypeID.getAttribute("FeeTypeID");
			if (obj != null) {
				aManualAdvise.setFeeTypeID(Long.valueOf(String.valueOf(obj)));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Sequence
		try {
		    aManualAdvise.setSequence(this.sequence.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Advise Amount
		try {
			if(this.adviseAmount.getValue()!=null){
			 	aManualAdvise.setAdviseAmount(PennantApplicationUtil.unFormateAmount(this.adviseAmount.getValue(),PennantConstants.defaultCCYDecPos));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Paid Amount
		try {
			if(this.paidAmount.getValue()!=null){
			 	aManualAdvise.setPaidAmount(PennantApplicationUtil.unFormateAmount(this.paidAmount.getValue(),PennantConstants.defaultCCYDecPos));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Waived Amount
		try {
			if(this.waivedAmount.getValue()!=null){
			 	aManualAdvise.setWaivedAmount(PennantApplicationUtil.unFormateAmount(this.waivedAmount.getValue(),PennantConstants.defaultCCYDecPos));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Remarks
		try {
		    aManualAdvise.setRemarks(this.remarks.getValue());
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
	 * @param manualAdvise
	 *            The entity that need to be render.
	 */
	public void doShowDialog(ManualAdvise manualAdvise) {
		logger.debug(Literal.LEAVING);

		if (manualAdvise.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.adviseType.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(manualAdvise.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.adviseType.focus();
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

		doWriteBeanToComponents(manualAdvise);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.adviseType.isReadonly()){
			this.adviseType.setConstraint(new StaticListValidator(listAdviseType,Labels.getLabel("label_ManualAdviseDialog_AdviseType.value")));
		}
		if (!this.finReference.isReadonly()){
			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_ManualAdviseDialog_FinReference.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.feeTypeID.isReadonly()){
			this.feeTypeID.setConstraint(new PTStringValidator(Labels.getLabel("label_ManualAdviseDialog_FeeTypeID.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.sequence.isReadonly()){
			this.sequence.setConstraint(new PTNumberValidator(Labels.getLabel("label_ManualAdviseDialog_Sequence.value"),false,false,0));
		}
		if (!this.adviseAmount.isReadonly()){
			this.adviseAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ManualAdviseDialog_AdviseAmount.value"),PennantConstants.defaultCCYDecPos,false,false,0));
		}
		if (!this.paidAmount.isReadonly()){
			this.paidAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ManualAdviseDialog_PaidAmount.value"),PennantConstants.defaultCCYDecPos,false,false,0));
		}
		if (!this.waivedAmount.isReadonly()){
			this.waivedAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ManualAdviseDialog_WaivedAmount.value"),PennantConstants.defaultCCYDecPos,false,false,0));
		}
		if (!this.remarks.isReadonly()){
			this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_ManualAdviseDialog_Remarks.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
	
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		
		this.adviseType.setConstraint("");
		this.finReference.setConstraint("");
		this.feeTypeID.setConstraint("");
		this.sequence.setConstraint("");
		this.adviseAmount.setConstraint("");
		this.paidAmount.setConstraint("");
		this.waivedAmount.setConstraint("");
		this.remarks.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		//Advise ID
		//Advise Type
		//Loan Reference
		//Fee Type ID
		//Sequence
		//Advise Amount
		//Paid Amount
		//Waived Amount
		//Remarks
		
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
	 * Deletes a ManualAdvise object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final ManualAdvise aManualAdvise = new ManualAdvise();
		BeanUtils.copyProperties(this.manualAdvise, aManualAdvise);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aManualAdvise.getAdviseID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aManualAdvise.getRecordType()).equals("")){
				aManualAdvise.setVersion(aManualAdvise.getVersion()+1);
				aManualAdvise.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aManualAdvise.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aManualAdvise.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aManualAdvise.getNextTaskId(), aManualAdvise);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aManualAdvise,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				logger.error("Exception",  e);
				showErrorMessage(this.window_ManualAdviseDialog,e);
			}
			
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		
		if (this.manualAdvise.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			
		}
	
			readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseType"), this.adviseType);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_FinReference"), this.finReference);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_FeeTypeID"), this.feeTypeID);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_Sequence"), this.sequence);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseAmount"), this.adviseAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_PaidAmount"), this.paidAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_WaivedAmount"), this.waivedAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_Remarks"), this.remarks);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.manualAdvise.isNewRecord()) {
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
			readOnlyComponent(true, this.adviseType);
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.feeTypeID);
			readOnlyComponent(true, this.sequence);
			readOnlyComponent(true, this.adviseAmount);
			readOnlyComponent(true, this.paidAmount);
			readOnlyComponent(true, this.waivedAmount);
			readOnlyComponent(true, this.remarks);

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
			 	this.adviseType.setSelectedIndex(0);
			  	this.finReference.setValue("");
			  	this.finReference.setDescription("");
			  	this.feeTypeID.setValue("");
			  	this.feeTypeID.setDescription("");
				this.sequence.setText("");
				this.adviseAmount.setValue("");
				this.paidAmount.setValue("");
				this.waivedAmount.setValue("");
				this.remarks.setValue("");

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final ManualAdvise aManualAdvise = new ManualAdvise();
			BeanUtils.copyProperties(this.manualAdvise, aManualAdvise);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aManualAdvise);

			isNew = aManualAdvise.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aManualAdvise.getRecordType())) {
					aManualAdvise.setVersion(aManualAdvise.getVersion() + 1);
					if (isNew) {
						aManualAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aManualAdvise.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aManualAdvise.setNewRecord(true);
					}
				}
			} else {
				aManualAdvise.setVersion(aManualAdvise.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aManualAdvise, tranType)) {
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
		private boolean doProcess(ManualAdvise aManualAdvise, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aManualAdvise.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aManualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aManualAdvise.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aManualAdvise.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aManualAdvise.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aManualAdvise);
					}

					if (isNotesMandatory(taskId, aManualAdvise)) {
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

				aManualAdvise.setTaskId(taskId);
				aManualAdvise.setNextTaskId(nextTaskId);
				aManualAdvise.setRoleCode(getRole());
				aManualAdvise.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aManualAdvise, tranType);
				String operationRefs = getServiceOperations(taskId, aManualAdvise);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aManualAdvise, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aManualAdvise, tranType);
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
			ManualAdvise aManualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = manualAdviseService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = manualAdviseService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = manualAdviseService.doApprove(auditHeader);

							if (aManualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = manualAdviseService.doReject(auditHeader);
							if (aManualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_ManualAdviseDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_ManualAdviseDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.manualAdvise), true);
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

		private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aManualAdvise.getUserDetails(),
					getOverideMap());
		}

		public void setManualAdviseService(ManualAdviseService manualAdviseService) {
			this.manualAdviseService = manualAdviseService;
		}
			
}
