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
 * FileName    		:  ReinstateFinanceDialogCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.reinstatefinance;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.ReinstateFinanceService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.enquiry.FinanceEnquiryListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/ReinstateFinance/ReinstateFinanceDialog.zul file.
 */
public class ReinstateFinanceDialogCtrl extends GFCBaseCtrl<ReinstateFinance> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = Logger.getLogger(ReinstateFinanceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ReinstateFinanceDialog; 	
	protected ExtendedCombobox 	finReference; 			    
	protected Textbox           custCIF; 			        
	protected Label             custShortName; 			    
	protected ExtendedCombobox  finType; 			        
	protected ExtendedCombobox  finBranch; 			        
	protected ExtendedCombobox  finCcy; 			        
	protected CurrencyBox       finAmount; 			        
	protected CurrencyBox       totDownpayment; 			
	protected Datebox           finStartDate; 			    
	protected Datebox           maturityDate; 			    
	protected CurrencyBox       totProfit; 			        
	
	protected Textbox           rejectSts; 			        
	protected Textbox           rejectRemarks; 			    
	protected Textbox           rejectedBy; 			    
	protected Datebox           rejectedOn; 			    
	
	protected Groupbox          gb_RejectDetails; 		     
	protected Groupbox          gb_financeDetails; 		     

	// not auto wired variables
	private ReinstateFinance reinstateFinance; // overHanded per parameter
	private transient ReinstateFinanceListCtrl reinstateFinanceListCtrl; // overHanded per parameter
	private FinanceEnquiryListCtrl	     financeEnquiryListCtrl	= null;
	private transient boolean rejectedList;
	private Label label_ReinstateFinanceDialog;

	private transient boolean validationOn;
	
	private boolean	enqModule	= false;
	
	// ServiceDAOs / Domain Classes
	private transient ReinstateFinanceService reinstateFinanceService;
	private transient PagedListService pagedListService;
	int finFormatter = 2;
	private FinanceWorkFlowService financeWorkFlowService;
	private FinanceMain financeMain;
	private EventManager eventManager;

	/**
	 * default constructor.<br>
	 */
	public ReinstateFinanceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReinstateFinanceDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ReinstateFinance object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReinstateFinanceDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReinstateFinanceDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}
			if (arguments.containsKey("financeMain")) {
				financeMain = (FinanceMain) arguments.get("financeMain");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("reinstateFinance")) {
				this.reinstateFinance = (ReinstateFinance) arguments
						.get("reinstateFinance");
				ReinstateFinance befImage = new ReinstateFinance();
				BeanUtils.copyProperties(this.reinstateFinance, befImage);
				this.reinstateFinance.setBefImage(befImage);

				setReinstateFinance(this.reinstateFinance);
			} else {
				setReinstateFinance(null);
			}
			
			if(!getReinstateFinance().isNewRecord()){
				doLoadWorkFlow(this.reinstateFinance.isWorkflow(),
						this.reinstateFinance.getWorkflowId(),
						this.reinstateFinance.getNextTaskId());
			}

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ReinstateFinanceDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(true);
			}
			if (arguments.containsKey("rejectedList")) {
				this.rejectedList = (Boolean) arguments.get("rejectedList");
			}
			
			// READ OVERHANDED parameters !
			// we get the ReinstateFinanceListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete ReinstateFinance here.
			if (arguments.containsKey("reinstateFinanceListCtrl")) {
				setReinstateFinanceListCtrl((ReinstateFinanceListCtrl) arguments
						.get("reinstateFinanceListCtrl"));
			} else {
				setReinstateFinanceListCtrl(null);
			}
			
			if (arguments.containsKey("financeEnquiryListCtrl")) {
				this.setFinanceEnquiryListCtrl((FinanceEnquiryListCtrl) arguments
						.get("financeEnquiryListCtrl"));
			}

			/* set components visible dependent of the users rights */
			doCheckRights();
			// set Field Properties
			doSetFieldProperties();
			if(getReinstateFinance().isNewRecord()){
				doShowReinstateDialog(financeMain);
				
			}else{
				doShowDialog(getReinstateFinance());
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReinstateFinanceDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custCIF.setWidth("165px");
		
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finAmount.setScale(finFormatter);
		this.finAmount.setTextBoxWidth(164);
		this.totDownpayment.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totDownpayment.setScale(finFormatter);
		this.totDownpayment.setTextBoxWidth(164);
		this.totProfit.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totProfit.setScale(finFormatter);
		this.totProfit.setTextBoxWidth(164);
		
		this.finStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finStartDate.setWidth("164px");
		
		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate.setWidth("164px");
		
		this.rejectedOn.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.rejectedOn.setWidth("164px");
		
		this.rejectRemarks.setWidth("260px");
		
		if(!getReinstateFinance().isNewRecord()){
			if (isWorkFlowEnabled()){
				this.groupboxWf.setVisible(true);
			}else{
				this.groupboxWf.setVisible(false);
			}
		}
		logger.debug("Leaving");
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
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("ReinstateFinanceDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ReinstateFinanceDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReinstateFinanceDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReinstateFinanceDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ReinstateFinanceDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public void onClick$btnSave(Event event) throws InterruptedException, FileNotFoundException, XMLStreamException {
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
		MessageUtil.showHelpWindow(event, window_ReinstateFinanceDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 * @throws FactoryConfigurationError 
	 * @throws UnsupportedEncodingException 
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException, FileNotFoundException, XMLStreamException, UnsupportedEncodingException, FactoryConfigurationError {
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.reinstateFinance.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aReinstateFinance
	 *            ReinstateFinance
	 */
	public void doWriteBeanToComponents(ReinstateFinance aReinstateFinance) {
		logger.debug("Entering");
		doSetFinanceData(aReinstateFinance.getFinReference());
		this.recordStatus.setValue(aReinstateFinance.getRecordStatus());
		logger.debug("Leaving");
	}

	private void doSetFinanceData(String finReference){
		logger.debug("Entering");
		if(StringUtils.isNotBlank(finReference)){
			ReinstateFinance reinstateFinance =  getReinstateFinanceService().getFinanceDetailsById(finReference);
			if(reinstateFinance != null){
				finFormatter = CurrencyUtil.getFormat(reinstateFinance.getFinCcy());
				setCurrencyFieldProperties();
				this.finReference.setValue(reinstateFinance.getFinReference());
				this.custCIF.setValue(reinstateFinance.getCustCIF());
				this.custShortName.setValue(reinstateFinance.getCustShrtName());
				this.finType.setValue(reinstateFinance.getFinType());
				this.finType.setDescription(reinstateFinance.getLovDescFinTypeName());
				this.finBranch.setValue(reinstateFinance.getFinBranch());
				this.finBranch.setDescription(reinstateFinance.getLovDescFinBranchName());
				this.finCcy.setValue(reinstateFinance.getFinCcy());
				this.finCcy.setDescription(CurrencyUtil.getCcyDesc(reinstateFinance.getFinCcy()));
				this.finAmount.setValue(PennantAppUtil.formateAmount(reinstateFinance.getFinAmount(),finFormatter));
				this.totDownpayment.setValue(PennantAppUtil.formateAmount(reinstateFinance.getDownPayment(),finFormatter));
				this.finStartDate.setValue(reinstateFinance.getFinStartDate());
				this.maturityDate.setValue(reinstateFinance.getMaturityDate());
				this.totProfit.setValue(PennantAppUtil.formateAmount(reinstateFinance.getTotalProfit(),finFormatter));
				this.rejectSts.setValue(reinstateFinance.getRejectStatus());
				this.rejectRemarks.setValue(reinstateFinance.getRejectRemarks());
				this.rejectedBy.setValue(reinstateFinance.getRejectedBy());
				this.rejectedOn.setValue(reinstateFinance.getRejectedOn());
				
				this.gb_RejectDetails.setVisible(true);
				this.gb_financeDetails.setVisible(true);
			}else{
				doClear();
			}
		}
		logger.debug("Leaving");
	}
	
	private void setCurrencyFieldProperties(){
		logger.debug("Entering");
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finAmount.setScale(finFormatter);
		this.totDownpayment.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totDownpayment.setScale(finFormatter);
		this.totDownpayment.setTextBoxWidth(164);
		this.totProfit.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totProfit.setScale(finFormatter);
		logger.debug("Leaving");
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReinstateFinance
	 */
	public void doWriteComponentsToBean(ReinstateFinance aReinstateFinance) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aReinstateFinance.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			//Adding this field to bean to assign finance workflow based on finance type 
			aReinstateFinance.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aReinstateFinance.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aReinstateFinance
	 * @throws Exception
	 */
	public void doShowDialog(ReinstateFinance aReinstateFinance) throws Exception {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (aReinstateFinance.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.finReference.focus();
				if (StringUtils.isNotBlank(aReinstateFinance.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				if (rejectedList) {
					doEdit();
				}
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				if (rejectedList) {
				this.btnDelete.setVisible(false);
				this.label_ReinstateFinanceDialog.setValue(Labels.getLabel("label_RejectedFinanceDialog.value"));
				}
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aReinstateFinance);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReinstateFinanceDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
	
		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_ReinstateFinanceDialog_FinReference.value"), 
					null, true,true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
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
		this.finReference.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a ReinstateFinance object from database.<br>
	 * 
	 * @throws InterruptedException
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 * @throws FactoryConfigurationError 
	 * @throws UnsupportedEncodingException 
	 */
	private void doDelete() throws InterruptedException, FileNotFoundException, XMLStreamException, UnsupportedEncodingException, FactoryConfigurationError {
		logger.debug("Entering");
		final ReinstateFinance aReinstateFinance = new ReinstateFinance();
		BeanUtils.copyProperties(getReinstateFinance(), aReinstateFinance);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_ReinstateFinanceDialog_FinReference.value")+" : "+aReinstateFinance.getFinReference();
		
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aReinstateFinance.getRecordType())) {
				aReinstateFinance.setVersion(aReinstateFinance.getVersion() + 1);
				aReinstateFinance.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aReinstateFinance.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aReinstateFinance, tranType)) {
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
		if (getReinstateFinance().isNewRecord()) {
			//this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.custCIF.setReadonly(true);
		this.finType.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.finAmount.setReadonly(true);
		this.totDownpayment.setReadonly(true);
		this.finStartDate.setDisabled(true);
		this.maturityDate.setDisabled(true);
		this.totProfit.setReadonly(true);
		this.rejectSts.setReadonly(true);
		this.rejectRemarks.setReadonly(true);
		this.rejectedBy.setReadonly(true);
		this.rejectedOn.setDisabled(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.reinstateFinance.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
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
		this.custCIF.setValue("");
		this.custShortName.setValue("");
		this.finType.setValue("");
		this.finBranch.setValue("");
		this.finCcy.setValue("");
		this.finAmount.setValue(BigDecimal.ZERO);
		this.totDownpayment.setValue(BigDecimal.ZERO);
		this.finStartDate.setText("");
		this.maturityDate.setText("");
		this.totProfit.setValue(BigDecimal.ZERO);
		this.rejectSts.setValue("");
		this.rejectRemarks.setValue("");
		this.rejectedBy.setValue("");
		this.rejectedOn.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public void doSave() throws InterruptedException, FileNotFoundException, XMLStreamException {
		logger.debug("Entering");

		final ReinstateFinance aReinstateFinance = new ReinstateFinance();
		BeanUtils.copyProperties(getReinstateFinance(), aReinstateFinance);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ReinstateFinance object with the components data
		doWriteComponentsToBean(aReinstateFinance);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aReinstateFinance.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReinstateFinance.getRecordType())) {
				aReinstateFinance.setVersion(aReinstateFinance.getVersion() + 1);
				if (isNew) {
					aReinstateFinance.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReinstateFinance.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReinstateFinance.setNewRecord(true);
				}
			}
		} else {
			aReinstateFinance.setVersion(aReinstateFinance.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aReinstateFinance, tranType)) {
				refreshList();
				closeDialog();
				//Customer Notification for Role Identification
				if(StringUtils.isBlank(aReinstateFinance.getNextTaskId())){
					aReinstateFinance.setNextRoleCode("");
				}
				String msg = "" ;
				if(StringUtils.isBlank(aReinstateFinance.getNextRoleCode()) && !StringUtils.equals(aReinstateFinance.getRecordStatus(), PennantConstants.RCD_STATUS_CANCELLED)){
					if(StringUtils.isEmpty(aReinstateFinance.getFinPreApprovedRef())){
						msg = "Finance with Reference : " + aReinstateFinance.getFinReference()+" "+Labels.getLabel("label_ReinstateFinance_Success");
					}else{
						msg = "Finance with Reference : " + aReinstateFinance.getFinReference()+" "+Labels.getLabel("label_ReinstateFinance_PreApproval_Success");
					}
					Clients.showNotification(msg,  "info", null, null, -1);
				}
				else{
					msg = PennantApplicationUtil.getSavingStatus(aReinstateFinance.getRoleCode(),aReinstateFinance.getNextRoleCode(), 
							aReinstateFinance.getFinReference(), " Finance ", aReinstateFinance.getRecordStatus());
					Clients.showNotification(msg,  "info", null, null, -1);
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		
		
		
		// User Notifications Message/Alert
					try {
						if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
								&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
								&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

							String nextRoleCodes = aReinstateFinance.getNextRoleCode();
							if (StringUtils.isNotEmpty(nextRoleCodes)) {
								Notify notify = Notify.valueOf("ROLE");
								String[] to = nextRoleCodes.split(",");
								if (StringUtils.isNotEmpty(aReinstateFinance.getFinReference())) {
									String reference = aReinstateFinance.getFinReference();
									getEventManager().publish(
											Labels.getLabel("REC_PENDING_MESSAGE") + " with Reference" + ":" + reference,
											notify, to);
								} else {
									getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), notify, to);
								}
							}
						}
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aReinstateFinance
	 *            (ReinstateFinance)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 * @throws FactoryConfigurationError 
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	private boolean doProcess(ReinstateFinance aReinstateFinance, String tranType) throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, FactoryConfigurationError {
		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		aReinstateFinance.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aReinstateFinance.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReinstateFinance.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aReinstateFinance.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aReinstateFinance, finishedTasks);

			if (isNotesMandatory(taskId, aReinstateFinance)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aReinstateFinance, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_DDAMaintenance)){
					processCompleted = true;
				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if(StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)){
					List<String> finTypeList =getReinstateFinanceService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for(String fintypeList :finTypeList){
						if(StringUtils.isNotEmpty(FinanceConstants.FINSER_EVENT_REINSTATE) &&
								StringUtils.equals(FinanceConstants.FINSER_EVENT_REINSTATE,fintypeList)){
							isScheduleModify = true;
							break;
						}
					}
					if(isScheduleModify){
						aReinstateFinance.setScheduleChange(true);
					}else{
						aReinstateFinance.setScheduleChange(false);
					}
				}else {
					ReinstateFinance tReinstateFinance=  (ReinstateFinance) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, aReinstateFinance);
					auditHeader.getAuditDetail().setModelData(tReinstateFinance);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				ReinstateFinance tReinstateFinance=  (ReinstateFinance) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tReinstateFinance,finishedTasks);

			}

			ReinstateFinance tReinstateFinance=  (ReinstateFinance) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tReinstateFinance);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}
			
			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId)|| "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aReinstateFinance);
					auditHeader.getAuditDetail().setModelData(tReinstateFinance);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			auditHeader = getAuditHeader(aReinstateFinance, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	protected String getServiceTasks(String taskId, ReinstateFinance reinstateFinance,
			String finishedTasks) {
		logger.debug("Entering");
      // changes regarding parallel work flow 
		String nextRoleCode = StringUtils.trimToEmpty(reinstateFinance.getNextRoleCode());
		String nextRoleCodes[]=nextRoleCode.split(",");
		
		if (nextRoleCodes.length > 1 ) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, reinstateFinance);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}
	
	protected void setNextTaskDetails(String taskId, ReinstateFinance reinstateFinance) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(reinstateFinance.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			}else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, reinstateFinance);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole= StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		reinstateFinance.setTaskId(taskId);
		reinstateFinance.setNextTaskId(nextTaskId);
		reinstateFinance.setRoleCode(getRole());
		reinstateFinance.setNextRoleCode(nextRoleCode);
		
		logger.debug("Leaving");
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
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 * @throws FactoryConfigurationError 
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws FileNotFoundException,
			XMLStreamException, UnsupportedEncodingException, FactoryConfigurationError {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ReinstateFinance aReinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getReinstateFinanceService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getReinstateFinanceService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
                        String finEvent = "";
                        if(StringUtils.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL, aReinstateFinance.getFinPreApprovedRef())){
                        	finEvent = FinanceConstants.FINSER_EVENT_PREAPPROVAL;
                        }else{
                        	finEvent = FinanceConstants.FINSER_EVENT_ORG;
                        }
						FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(aReinstateFinance.getFinType(), 
								finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
						if(financeWorkFlow != null){
							WorkFlowDetails	workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
							if(workFlowDetails != null){
								WorkflowEngine workflow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
								String taskid = workflow.getUserTaskId(workflow.firstTaskOwner());
								aReinstateFinance.setLovDescWorkflowId(workFlowDetails.getWorkFlowId());
								aReinstateFinance.setLovDescRoleCode(workflow.firstTaskOwner());
								aReinstateFinance.setLovDescNextRoleCode(workflow.firstTaskOwner());
								aReinstateFinance.setLovDescTaskId(taskid);
								aReinstateFinance.setLovDescNextTaskId(taskid+";");
							}
						}

						auditHeader = getReinstateFinanceService().doApprove(auditHeader);

						if (aReinstateFinance.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getReinstateFinanceService().doReject(auditHeader);

						if (aReinstateFinance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ReinstateFinanceDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ReinstateFinanceDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.reinstateFinance), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}

			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aReinstateFinance
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ReinstateFinance aReinstateFinance, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReinstateFinance.getBefImage(), aReinstateFinance);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aReinstateFinance.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ReinstateFinanceDialog, auditHeader);
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
		doShowNotes(this.reinstateFinance);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getReinstateFinanceListCtrl().search();
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return String.valueOf(getReinstateFinance().getFinReference());
	}
	
	
	public void doShowReinstateDialog(FinanceMain details) throws Exception {
		logger.debug("Entering");
		
			if (details != null) {
				this.finReference.setValue(details.getFinReference());
				this.finReference.setDescription("");
				doSetFinanceData(details.getFinReference());
				getReinstateFinance().setFinPreApprovedRef(details.getFinPreApprovedRef());
				//Workflow Details
				setWorkflowDetails(details.getFinType());
				getReinstateFinance().setWorkflowId(getWorkFlowId());
				doLoadWorkFlow(this.reinstateFinance.isWorkflow(), this.reinstateFinance.getWorkflowId(), 
						this.reinstateFinance.getNextTaskId());
				if (isWorkFlowEnabled()) {
					this.userAction = setListRecordStatus(this.userAction);
					for (int i = 0; i < userAction.getItemCount(); i++) {
						userAction.getItemAtIndex(i).setDisabled(false);
					}
					if (getReinstateFinance().isNewRecord()) {
						this.btnCtrl.setBtnStatus_Edit();
						btnCancel.setVisible(false);
					} else {
						this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
					}
					this.groupboxWf.setVisible(true);
				}
				doEdit();
				this.finReference.setReadonly(true);
			}else{
				doClear();
			}
			setDialog(DialogType.EMBEDDED);
		
		logger.debug("Leaving");
	}
	
	private void setWorkflowDetails(String finType) {

		// Finance Maintenance Workflow Check & Assignment
		WorkFlowDetails workFlowDetails = null;
		if (StringUtils.isNotEmpty(FinanceConstants.FINSER_EVENT_REINSTATE)) {
			FinanceWorkFlow financeWorkflow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(finType,
					FinanceConstants.FINSER_EVENT_REINSTATE, PennantConstants.WORFLOW_MODULE_FINANCE);//TODO : Check Promotion case
			if (financeWorkflow != null && financeWorkflow.getWorkFlowType() != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkflow.getWorkFlowType());
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
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

	public ReinstateFinance getReinstateFinance() {
		return this.reinstateFinance;
	}
	public void setReinstateFinance(ReinstateFinance reinstateFinance) {
		this.reinstateFinance = reinstateFinance;
	}

	public void setReinstateFinanceService(ReinstateFinanceService reinstateFinanceService) {
		this.reinstateFinanceService = reinstateFinanceService;
	}
	public ReinstateFinanceService getReinstateFinanceService() {
		return this.reinstateFinanceService;
	}

	public void setReinstateFinanceListCtrl(ReinstateFinanceListCtrl reinstateFinanceListCtrl) {
		this.reinstateFinanceListCtrl = reinstateFinanceListCtrl;
	}
	public ReinstateFinanceListCtrl getReinstateFinanceListCtrl() {
		return this.reinstateFinanceListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}
	public void setFinanceWorkFlowService(
			FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setFinanceEnquiryListCtrl(FinanceEnquiryListCtrl financeEnquiryListCtrl) {
		this.financeEnquiryListCtrl = financeEnquiryListCtrl;
	}
	public FinanceEnquiryListCtrl getFinanceEnquiryListCtrl() {
		return financeEnquiryListCtrl;
	}

}
