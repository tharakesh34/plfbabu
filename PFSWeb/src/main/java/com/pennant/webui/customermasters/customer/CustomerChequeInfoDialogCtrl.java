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
 * FileName    		:  CustomerChequeInfoDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerChequeInfo/customerChequeInfoDialog.zul file.
 */
public class CustomerChequeInfoDialogCtrl extends GFCBaseCtrl<CustomerChequeInfo> {
	private static final long serialVersionUID = -7522534300621535097L;
	private static final Logger logger = Logger.getLogger(CustomerChequeInfoDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerChequeInfoDialog; 		

	protected Longbox 	custID; 						
	protected Textbox 	custCIF;						
	protected Label   	custShrtName;					
	protected Datebox 	monthYear; 					    
	protected CurrencyBox 	totChequePayment; 		    
	protected CurrencyBox 	salary; 		            
	protected CurrencyBox 	debits; 		            
	protected CurrencyBox 	returnChequeAmt; 		    
	protected Intbox 	returnChequeCount; 		        
	protected Intbox chequeSeq;          		        
	protected Textbox remarks;                          
	
	// not auto wired variables
	private CustomerChequeInfo customerChequeInfo; // overHanded per parameter

	private transient boolean validationOn;
	
	protected Button btnSearchPRCustid; 

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerChequeInfo> CustomerChequeInfoList;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerEnquiryDialogCtrlr customerEnquiryDialogCtrlr;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String userRole="";
	private int finFormatter;
	private boolean isFinanceProcess = false;

	/**
	 * default constructor.<br>
	 */
	public CustomerChequeInfoDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerChequeInfoDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerChequeInfo object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerChequeInfoDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerChequeInfoDialog);

		try {
			if (arguments.containsKey("customerChequeInfo")) {
				this.customerChequeInfo = (CustomerChequeInfo) arguments
						.get("customerChequeInfo");
				CustomerChequeInfo befImage = new CustomerChequeInfo();
				BeanUtils.copyProperties(this.customerChequeInfo, befImage);
				this.customerChequeInfo.setBefImage(befImage);
				setCustomerChequeInfo(this.customerChequeInfo);
			} else {
				setCustomerChequeInfo(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (arguments.containsKey("finFormatter")) {
				this.finFormatter = (Integer) arguments.get("finFormatter");
			}

			if (getCustomerChequeInfo().isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("customerDialogCtrl")) {
				setCustomerDialogCtrl((CustomerDialogCtrl) arguments
						.get("customerDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.customerChequeInfo.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole,
							"CustomerChequeInfoDialog");
				}
			}

			if (arguments.containsKey("customerEnquiryDialogCtrlr")) {
				setCustomerEnquiryDialogCtrlr((CustomerEnquiryDialogCtrlr) arguments.get("customerEnquiryDialogCtrlr"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.customerChequeInfo.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerChequeInfoDialog");
				}
			}

			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}
			doLoadWorkFlow(this.customerChequeInfo.isWorkflow(),
					this.customerChequeInfo.getWorkflowId(),
					this.customerChequeInfo.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CustomerChequeInfoDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCustomerChequeInfo());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerChequeInfoDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		this.monthYear.setFormat(PennantConstants.monthYearFormat);
		
		this.totChequePayment.setMandatory(true);
		this.totChequePayment.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.totChequePayment.setScale(finFormatter);
		
		this.salary.setMandatory(true);
		this.salary.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.salary.setScale(finFormatter);
		
		this.debits.setMandatory(true);
		this.debits.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.debits.setScale(finFormatter);
		
		this.returnChequeAmt.setMandatory(true);
		this.returnChequeAmt.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.returnChequeAmt.setScale(finFormatter);
		
		this.returnChequeCount.setMaxlength(4);
		this.remarks.setMaxlength(500);
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
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
		getUserWorkspace().allocateAuthorities("CustomerChequeInfoDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerChequeInfoDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerChequeInfoDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerChequeInfoDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerChequeInfoDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_CustomerChequeInfoDialog);
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.customerChequeInfo.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerChequeInfo
	 *            CustomerChequeInfo
	 */
	public void doWriteBeanToComponents(CustomerChequeInfo aCustomerChequeInfo) {
		logger.debug("Entering");

		if(aCustomerChequeInfo.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerChequeInfo.getCustID());
		}
		this.chequeSeq.setValue(aCustomerChequeInfo.getChequeSeq());
		this.monthYear.setValue(aCustomerChequeInfo.getMonthYear());
		this.totChequePayment.setValue(PennantAppUtil.formateAmount(aCustomerChequeInfo.getTotChequePayment(),finFormatter));
		this.salary.setValue(PennantAppUtil.formateAmount(aCustomerChequeInfo.getSalary(),finFormatter));
		this.debits.setValue(PennantAppUtil.formateAmount(aCustomerChequeInfo.getDebits(),finFormatter));
		this.returnChequeAmt.setValue(PennantAppUtil.formateAmount(aCustomerChequeInfo.getReturnChequeAmt(),finFormatter));
		this.returnChequeCount.setValue(aCustomerChequeInfo.getReturnChequeCount());
		this.remarks.setValue(aCustomerChequeInfo.getRemarks());

		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomerChequeInfo.getLovDescCustCIF()));
		this.custShrtName.setValue(StringUtils.trimToEmpty(aCustomerChequeInfo.getLovDescCustShrtName()));

		
		this.recordStatus.setValue(aCustomerChequeInfo.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerChequeInfo
	 */
	public void doWriteComponentsToBean(CustomerChequeInfo aCustomerChequeInfo) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerChequeInfo.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerChequeInfo.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
	
		try {
			aCustomerChequeInfo.setChequeSeq(this.chequeSeq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerChequeInfo.setMonthYear(this.monthYear.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.totChequePayment.getActualValue().compareTo(BigDecimal.ZERO)==0) {
				aCustomerChequeInfo.setTotChequePayment(BigDecimal.ZERO);
			}else{
				aCustomerChequeInfo.setTotChequePayment(PennantAppUtil.unFormateAmount(this.totChequePayment.getValidateValue(),finFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.salary.getActualValue().compareTo(BigDecimal.ZERO)==0) {
				aCustomerChequeInfo.setSalary(BigDecimal.ZERO);
			}else{
				aCustomerChequeInfo.setSalary(PennantAppUtil.unFormateAmount(this.salary.getValidateValue(),finFormatter));
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.debits.getActualValue().compareTo(BigDecimal.ZERO)==0) {
				aCustomerChequeInfo.setDebits(BigDecimal.ZERO);
			}else{
				aCustomerChequeInfo.setDebits(PennantAppUtil.unFormateAmount(this.debits.getValidateValue(),finFormatter));
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.returnChequeAmt.getActualValue().compareTo(BigDecimal.ZERO)==0) {
				aCustomerChequeInfo.setReturnChequeAmt(BigDecimal.ZERO);	
			}else{
				aCustomerChequeInfo.setReturnChequeAmt(PennantAppUtil.unFormateAmount(this.returnChequeAmt.getValidateValue(),finFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerChequeInfo.setReturnChequeCount(this.returnChequeCount.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerChequeInfo.setRemarks(this.remarks.getValue());
		}catch (WrongValueException we ) {
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

		aCustomerChequeInfo.setRecordStatus(this.recordStatus.getValue());
		setCustomerChequeInfo(aCustomerChequeInfo);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerChequeInfo
	 * @throws Exception
	 */
	public void doShowDialog(CustomerChequeInfo aCustomerChequeInfo) throws Exception {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.monthYear.focus();
			if (isNewCustomer()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aCustomerChequeInfo);

            doCheckEnquiry();
			if(isNewCustomer()){
				this.window_CustomerChequeInfoDialog.setHeight("50%");
				this.window_CustomerChequeInfoDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerChequeInfoDialog.doModal() ;
			}else{
				this.window_CustomerChequeInfoDialog.setWidth("100%");
				this.window_CustomerChequeInfoDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerChequeInfoDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if(PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)){
			this.totChequePayment.setReadonly(true);
			this.salary.setReadonly(true);
			this.debits.setReadonly(true);
			this.monthYear.setDisabled(true);
			this.returnChequeAmt.setReadonly(true);
			this.returnChequeCount.setReadonly(true);
			this.remarks.setReadonly(true);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
		if (!this.monthYear.isDisabled()) {
			this.monthYear.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerChequeInfoDialog_MonthYear.value"),true, startDate, true, true));
		}
		if (!this.totChequePayment.isDisabled()) {
			this.totChequePayment.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerChequeInfoDialog_TotChequePayment.value"), finFormatter, true, false));
		}
		if (!this.salary.isDisabled()) {
			this.salary.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerChequeInfoDialog_Salary.value"), finFormatter, true, false));
		}
		if (!this.debits.isDisabled()) {
			this.debits.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerChequeInfoDialog_Debits.value"), finFormatter, true, false));
		}
		if (!this.returnChequeAmt.isDisabled()) {
			this.returnChequeAmt.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerChequeInfoDialog_ReturnChequeAmt.value"), finFormatter, true, false));
		}
		if (!this.returnChequeCount.isDisabled()) {
			this.returnChequeCount.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerChequeInfoDialog_ReturnChequeCount.value"), true, false));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerChequeInfoDialog_Remarks.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.monthYear.setConstraint("");
		this.totChequePayment.setConstraint("");
		this.salary.setConstraint("");
		this.debits.setConstraint("");
		this.returnChequeAmt.setConstraint("");
		this.returnChequeCount.setConstraint("");
		this.remarks.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.monthYear.setErrorMessage("");
		this.totChequePayment.setErrorMessage("");
		this.salary.setErrorMessage("");
		this.debits.setErrorMessage("");
		this.returnChequeAmt.setErrorMessage("");
		this.returnChequeCount.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a CustomerChequeInfo object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerChequeInfo aCustomerChequeInfo = new CustomerChequeInfo();
		BeanUtils.copyProperties(getCustomerChequeInfo(), aCustomerChequeInfo);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_CustomerChequeInfoDialog_ChequeSeq.value")+" : "+aCustomerChequeInfo.getChequeSeq(); 
		
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerChequeInfo.getRecordType())) {
				aCustomerChequeInfo.setVersion(aCustomerChequeInfo.getVersion() + 1);
				aCustomerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(!isFinanceProcess && getCustomerDialogCtrl() != null &&  
						getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerChequeInfo.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()) {
					aCustomerChequeInfo.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerChequeInfo, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerChequeInfoDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerChequeInfoDetails(this.CustomerChequeInfoList);
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

		if (isNewRecord()){
			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
			this.monthYear.setDisabled(isReadOnly("CustomerChequeInfoDialog_monthYear"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.monthYear.setDisabled(true);
		}
		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.totChequePayment.setReadonly(isReadOnly("CustomerChequeInfoDialog_totChequePayment"));
		this.monthYear.setDisabled(isReadOnly("CustomerChequeInfoDialog_monthYear"));
		this.salary.setReadonly(isReadOnly("CustomerChequeInfoDialog_salary"));
		this.debits.setReadonly(isReadOnly("CustomerChequeInfoDialog_debits"));
		this.returnChequeAmt.setReadonly(isReadOnly("CustomerChequeInfoDialog_returnChequeAmt"));
		this.returnChequeCount.setReadonly(isReadOnly("CustomerChequeInfoDialog_returnChequeCount"));
		this.remarks.setReadonly(isReadOnly("CustomerChequeInfoDialog_remarks"));


		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerChequeInfo.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if(newCustomer){
				if(PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		boolean isCustomerWorkflow = false;
		if(getCustomerDialogCtrl() != null){
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.totChequePayment.setReadonly(true);
		this.monthYear.setDisabled(true);
		this.salary.setReadonly(true);
		this.debits.setReadonly(true);
		this.returnChequeAmt.setReadonly(true);
		this.returnChequeCount.setReadonly(true);
		this.remarks.setReadonly(true);

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
		this.monthYear.setText("");
		this.totChequePayment.setValue("");
		this.salary.setValue("");
		this.debits.setValue("");
		this.returnChequeAmt.setValue("");
		this.returnChequeCount.setValue(0);
		this.remarks.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerChequeInfo aCustomerChequeInfo = new CustomerChequeInfo();
		BeanUtils.copyProperties(getCustomerChequeInfo(), aCustomerChequeInfo);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerChequeInfo object with the components data
		doWriteComponentsToBean(aCustomerChequeInfo);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerChequeInfo.isNew();
		String tranType = "";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerChequeInfo.getRecordType())){
				aCustomerChequeInfo.setVersion(aCustomerChequeInfo.getVersion()+1);
				if(isNew){
					aCustomerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerChequeInfo.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerChequeInfo.setVersion(1);
					aCustomerChequeInfo.setRecordType(PennantConstants.RCD_ADD);					
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isBlank(aCustomerChequeInfo.getRecordType())){
					aCustomerChequeInfo.setVersion(aCustomerChequeInfo.getVersion()+1);
					aCustomerChequeInfo.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerChequeInfo.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerChequeInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerChequeInfo.setVersion(aCustomerChequeInfo.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerChequeInfo, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerChequeInfoDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getCustomerDialogCtrl().doFillCustomerChequeInfoDetails(this.CustomerChequeInfoList);
				closeDialog();
			}
		
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	
	private AuditHeader newFinanceCustomerProcess(CustomerChequeInfo aCustomerChequeInfo,String tranType){
		logger.debug("Entering");
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aCustomerChequeInfo, tranType);
		CustomerChequeInfoList = new ArrayList<CustomerChequeInfo>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = String.valueOf(aCustomerChequeInfo.getId());
		valueParm[1] = String.valueOf(aCustomerChequeInfo.getChequeSeq());
		
		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ChequeSeq")+ ":" + valueParm[1];
		
		if(getCustomerDialogCtrl().getCustomerChequeInfoDetailList()!=null && getCustomerDialogCtrl().getCustomerChequeInfoDetailList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerChequeInfoDetailList().size(); i++) {
				CustomerChequeInfo customerChequeInfo = getCustomerDialogCtrl().getCustomerChequeInfoDetailList().get(i);
				
				
				if(aCustomerChequeInfo.getChequeSeq()==customerChequeInfo.getChequeSeq()){ // Both Current and Existing list rating same
					
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",
										errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if(aCustomerChequeInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							CustomerChequeInfoList.add(aCustomerChequeInfo);
						}else if(aCustomerChequeInfo.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerChequeInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							CustomerChequeInfoList.add(aCustomerChequeInfo);
						}else if(aCustomerChequeInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerChequeInfoList().size(); j++) {
								CustomerChequeInfo email =  getCustomerDialogCtrl().getCustomerDetails().getCustomerChequeInfoList().get(j);
								if(email.getCustID() == aCustomerChequeInfo.getCustID() && 
										email.getChequeSeq() == aCustomerChequeInfo.getChequeSeq()){
									CustomerChequeInfoList.add(email);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							CustomerChequeInfoList.add(customerChequeInfo);
						}
					}
				} else {
					CustomerChequeInfoList.add(customerChequeInfo);
				}
			}
		}
		
		if (!recordAdded) {
			CustomerChequeInfoList.add(aCustomerChequeInfo);
		}
		logger.debug("Leaving");
		return auditHeader;
	} 


	// Search Button Component Events

	
	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerChequeInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerChequeInfo aCustomerChequeInfo, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,aCustomerChequeInfo.getBefImage(), aCustomerChequeInfo);
		return new AuditHeader(getReference(), String.valueOf(aCustomerChequeInfo.getCustID()), null, null, 
				auditDetail, aCustomerChequeInfo.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerChequeInfoDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.customerChequeInfo);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerChequeInfo().getCustID()+ PennantConstants.KEY_SEPERATOR
		+ getCustomerChequeInfo().getChequeSeq();
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

	public CustomerChequeInfo getCustomerChequeInfo() {
		return this.customerChequeInfo;
	}
	public void setCustomerChequeInfo(CustomerChequeInfo customerChequeInfo) {
		this.customerChequeInfo = customerChequeInfo;
	}

	public void setCustomerEmails(List<CustomerChequeInfo> customerEmails) {
		this.CustomerChequeInfoList = customerEmails;
	}
	public List<CustomerChequeInfo> getCustomerEmails() {
		return CustomerChequeInfoList;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}
	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public CustomerEnquiryDialogCtrlr getCustomerEnquiryDialogCtrlr() {
		return customerEnquiryDialogCtrlr;
	}

	public void setCustomerEnquiryDialogCtrlr(CustomerEnquiryDialogCtrlr customerEnquiryDialogCtrlr) {
		this.customerEnquiryDialogCtrlr = customerEnquiryDialogCtrlr;
	}
	
}
