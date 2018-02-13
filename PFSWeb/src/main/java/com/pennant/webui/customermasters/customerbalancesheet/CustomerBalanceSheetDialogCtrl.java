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
 * FileName    		:  CustomerBalanceSheetDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-12-2011    														*
 *                                                                  						*
 * Modified Date    :  07-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.customerbalancesheet;

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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerBalanceSheetService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerBalanceSheet
 * /customerBalanceSheetDialog.zul file.
 */
public class CustomerBalanceSheetDialogCtrl extends GFCBaseCtrl<CustomerBalanceSheet> {
	private static final long serialVersionUID = 2360247056482972784L;
	private static final Logger logger = Logger.getLogger(CustomerBalanceSheetDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	 window_CustomerBalanceSheetDialog;	
	protected Longbox 	 custID; 						   	
	protected Combobox 	 financialYear; 					
	protected Decimalbox totalAssets; 						
	protected Decimalbox totalLiabilities; 					
	protected Decimalbox netProfit; 						
	protected Decimalbox netSales; 							
	protected Decimalbox netIncome; 						
	protected Decimalbox operatingProfit; 					
	protected Decimalbox cashFlow;	 						
	protected Decimalbox bookValue; 						
	protected Decimalbox marketValue; 						
	protected Textbox 	 custCIF;							
	protected Label 	 custShrtName;						

	// not auto wired vars
	private CustomerBalanceSheet customerBalanceSheet; 							// overhanded per param
	private transient CustomerBalanceSheetListCtrl customerBalanceSheetListCtrl;// overhanded per param

	private transient boolean validationOn;
	
	protected Button btnSearchPRCustid; 

	// ServiceDAOs / Domain Classes
	private transient CustomerBalanceSheetService customerBalanceSheetService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();
	private List<ValueLabel> listFinancialYear=PennantAppUtil.getFinancialYears(); 

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerBalanceSheet> balanceSheetDetails;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;

	/**
	 * default constructor.<br>
	 */
	public CustomerBalanceSheetDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerBalanceSheetDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerBalanceSheet object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerBalanceSheetDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerBalanceSheetDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();
		if (arguments.containsKey("customerBalanceSheet")) {
			this.customerBalanceSheet = (CustomerBalanceSheet) arguments.get("customerBalanceSheet");
			CustomerBalanceSheet befImage =new CustomerBalanceSheet();
			BeanUtils.copyProperties(this.customerBalanceSheet, befImage);
			this.customerBalanceSheet.setBefImage(befImage);
			setCustomerBalanceSheet(this.customerBalanceSheet);
		} else {
			setCustomerBalanceSheet(null);
		}

		if(getCustomerBalanceSheet().isNewRecord()){
			setNewRecord(true);
		}

		if(arguments.containsKey("customerDialogCtrl")){

			setCustomerDialogCtrl((CustomerDialogCtrl) arguments.get("customerDialogCtrl"));
			setNewCustomer(true);

			if(arguments.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerBalanceSheet.setWorkflowId(0);
			if(arguments.containsKey("roleCode")){
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"),
				"CustomerBalanceSheetDialog");
			}
		}

		doLoadWorkFlow(this.customerBalanceSheet.isWorkflow(),
				this.customerBalanceSheet.getWorkflowId(),this.customerBalanceSheet.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerBalanceSheetDialog");
		}

		setListFinancialYear();

		// READ OVERHANDED params !
		// we get the customerBalanceSheetListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerBalanceSheet here.
		if (arguments.containsKey("customerBalanceSheetListCtrl")) {
			setCustomerBalanceSheetListCtrl((CustomerBalanceSheetListCtrl) arguments.get(
			"customerBalanceSheetListCtrl"));
		} else {
			setCustomerBalanceSheetListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerBalanceSheet());

		//Calling SelectCtrl For proper selection of Customer
		if (isNewRecord() && !isNewCustomer()) {
			onload();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;

		//Empty sent any required attributes
		this.totalAssets.setMaxlength(18);
		this.totalAssets.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.totalAssets.setScale(0);
		this.totalLiabilities.setMaxlength(18);
		this.totalLiabilities.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.totalLiabilities.setScale(0);
		this.netProfit.setMaxlength(18);
		this.netProfit.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.netProfit.setScale(0);
		this.netSales.setMaxlength(18);
		this.netSales.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.netSales.setScale(0);
		this.netIncome.setMaxlength(18);
		this.netIncome.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.netIncome.setScale(0);
		this.operatingProfit.setMaxlength(18);
		this.operatingProfit.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.operatingProfit.setScale(0);
		this.cashFlow.setMaxlength(18);
		this.cashFlow.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.cashFlow.setScale(0);
		this.bookValue.setMaxlength(18);
		this.bookValue.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.bookValue.setScale(0);
		this.marketValue.setMaxlength(18);
		this.marketValue.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.marketValue.setScale(0);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerBalanceSheetDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerBalanceSheetDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerBalanceSheetDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerBalanceSheetDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerBalanceSheetDialog);
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

	@Override
	public void closeDialog() {
		if (isNewCustomer()) {
			closeWindow();
			return;
		}

		super.closeDialog();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.customerBalanceSheet.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerBalanceSheet
	 *            CustomerBalanceSheet
	 */
	public void doWriteBeanToComponents(CustomerBalanceSheet aCustomerBalanceSheet) {
		logger.debug("Entering") ;
		if(aCustomerBalanceSheet.getCustId()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerBalanceSheet.getCustId());
		}

		this.custCIF.setValue(aCustomerBalanceSheet.getLovDescCustCIF()==null?"":
			aCustomerBalanceSheet.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerBalanceSheet.getLovDescCustShrtName()==null?"":
			aCustomerBalanceSheet.getLovDescCustShrtName().trim());
		/*this.financialYear.setValue(PennantAppUtil.getlabelDesc(
				aCustomerBalanceSheet.getFinancialYear(),listFinancialYear));*/
		this.totalAssets.setValue(PennantAppUtil.formateAmount(aCustomerBalanceSheet.getTotalAssets(),0));
		this.totalLiabilities.setValue(PennantAppUtil.formateAmount(
				aCustomerBalanceSheet.getTotalLiabilities(),0));
		this.netProfit.setValue(PennantAppUtil.formateAmount(aCustomerBalanceSheet.getNetProfit(),0));
		this.netSales.setValue(PennantAppUtil.formateAmount(aCustomerBalanceSheet.getNetSales(),0));
		this.netIncome.setValue(PennantAppUtil.formateAmount(aCustomerBalanceSheet.getNetIncome(),0));
		this.operatingProfit.setValue(PennantAppUtil.formateAmount(
				aCustomerBalanceSheet.getOperatingProfit(),0));
		this.cashFlow.setValue(PennantAppUtil.formateAmount(aCustomerBalanceSheet.getCashFlow(),0));
		this.bookValue.setValue(PennantAppUtil.formateAmount(aCustomerBalanceSheet.getBookValue(),0));
		this.marketValue.setValue(PennantAppUtil.formateAmount(aCustomerBalanceSheet.getMarketValue(),0));

		this.recordStatus.setValue(aCustomerBalanceSheet.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerBalanceSheet
	 */
	public void doWriteComponentsToBean(CustomerBalanceSheet aCustomerBalanceSheet) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerBalanceSheet.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerBalanceSheet.setCustId(this.custID.longValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setFinancialYear(this.financialYear.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setTotalAssets(PennantAppUtil.unFormateAmount(
					this.totalAssets.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setTotalLiabilities(PennantAppUtil.unFormateAmount(
					this.totalLiabilities.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setNetProfit(PennantAppUtil.unFormateAmount(
					this.netProfit.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setNetSales(PennantAppUtil.unFormateAmount(
					this.netSales.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setNetIncome(PennantAppUtil.unFormateAmount(
					this.netIncome.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setOperatingProfit(PennantAppUtil.unFormateAmount(
					this.operatingProfit.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setCashFlow(PennantAppUtil.unFormateAmount(
					this.cashFlow.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setBookValue(PennantAppUtil.unFormateAmount(
					this.bookValue.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerBalanceSheet.setMarketValue(PennantAppUtil.unFormateAmount(
					this.marketValue.getValue(), 0));
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

		aCustomerBalanceSheet.setRecordStatus(this.recordStatus.getValue());
		setCustomerBalanceSheet(aCustomerBalanceSheet);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerBalanceSheet
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerBalanceSheet aCustomerBalanceSheet) throws InterruptedException {
		logger.debug("Entering") ;

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCustomerBalanceSheet.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.totalAssets.focus();
			if (isNewCustomer()){
				doEdit();
			}else if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aCustomerBalanceSheet);

			if(isNewCustomer()){
				this.window_CustomerBalanceSheetDialog.setHeight("400px");
				this.window_CustomerBalanceSheetDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_CustomerBalanceSheetDialog.doModal() ;
			}else{
				this.window_CustomerBalanceSheetDialog.setWidth("100%");
				this.window_CustomerBalanceSheetDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.custID.isReadonly()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerBalanceSheetDialog_CustId.value"),null,true));
		}
		if (!this.financialYear.isDisabled()){
			this.financialYear.setConstraint(new StaticListValidator(listFinancialYear,Labels.getLabel("label_CustomerBalanceSheetDialog_FinancialYear.value")));
		}	
		if (!this.totalAssets.isReadonly()){
			this.totalAssets.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_TotalAssets.value"), 0, true));
		}	
		if (!this.totalLiabilities.isReadonly()){
			this.totalLiabilities.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_TotalLiabilities.value"), 0, true));
		}	
		if (!this.netProfit.isReadonly()){
			this.netProfit.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_NetProfit.value"), 0, true));
		}	
		if (!this.netSales.isReadonly()){
			this.netSales.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_NetSales.value"), 0, true));
		}	
		if (!this.netIncome.isReadonly()){
			this.netIncome.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_NetIncome.value"), 0, true));
		}	
		if (!this.operatingProfit.isReadonly()){
			this.operatingProfit.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_OperatingProfit.value"), 0, true));
		}	
		if (!this.cashFlow.isReadonly()){
			this.cashFlow.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_CashFlow.value"), 0, true));
		}	
		if (!this.bookValue.isReadonly()){
			this.bookValue.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_BookValue.value"), 0, true));
		}	
		if (!this.marketValue.isReadonly()){
			this.marketValue.setConstraint(new PTDecimalValidator(Labels.getLabel(
			"label_CustomerBalanceSheetDialog_MarketValue.value"), 0, true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.financialYear.setConstraint("");
		this.totalAssets.setConstraint("");
		this.totalLiabilities.setConstraint("");
		this.netProfit.setConstraint("");
		this.netSales.setConstraint("");
		this.netIncome.setConstraint("");
		this.operatingProfit.setConstraint("");
		this.cashFlow.setConstraint("");
		this.bookValue.setConstraint("");
		this.marketValue.setConstraint("");
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
		this.custID.setErrorMessage("");
		this.financialYear.setErrorMessage("");
		this.totalAssets.setErrorMessage("");
		this.totalLiabilities.setErrorMessage("");
		this.netProfit.setErrorMessage("");
		this.netSales.setErrorMessage("");
		this.netIncome.setErrorMessage("");
		this.operatingProfit.setErrorMessage("");
		this.cashFlow.setErrorMessage("");
		this.bookValue.setErrorMessage("");
		this.marketValue.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updating
	private void refreshList() {
		getCustomerBalanceSheetListCtrl().search();
		}
	
	// CRUD operations

	/**
	 * Deletes a CustomerBalanceSheet object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CustomerBalanceSheet aCustomerBalanceSheet = new CustomerBalanceSheet();
		BeanUtils.copyProperties(getCustomerBalanceSheet(), aCustomerBalanceSheet);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> " + aCustomerBalanceSheet.getCustId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerBalanceSheet.getRecordType())){
				aCustomerBalanceSheet.setVersion(aCustomerBalanceSheet.getVersion()+1);
				aCustomerBalanceSheet.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCustomerBalanceSheet.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCusomerProcess(aCustomerBalanceSheet,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBalanceSheetDialog,
							auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || 
							retValue==PennantConstants.porcessOVERIDE){
					//	getCustomerDialogCtrl().doFillCustomerBalanceSheet(this.balanceSheetDetails);
						// send the data back to customer
						closeDialog();
					}	
				}else if(doProcess(aCustomerBalanceSheet,tranType)){
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

		if (isNewRecord()){

			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
		}
		this.custCIF.setReadonly(true);	
		this.custID.setReadonly(isReadOnly("CustomerBalanceSheetDialog_custID"));
		this.financialYear.setDisabled(isReadOnly("CustomerBalanceSheetDialog_financialYear"));
		this.totalAssets.setReadonly(isReadOnly("CustomerBalanceSheetDialog_totalAssets"));
		this.totalLiabilities.setReadonly(isReadOnly("CustomerBalanceSheetDialog_totalLiabilities"));
		this.netProfit.setReadonly(isReadOnly("CustomerBalanceSheetDialog_netProfit"));
		this.netSales.setReadonly(isReadOnly("CustomerBalanceSheetDialog_netSales"));
		this.netIncome.setReadonly(isReadOnly("CustomerBalanceSheetDialog_netIncome"));
		this.operatingProfit.setReadonly(isReadOnly("CustomerBalanceSheetDialog_operatingProfit"));
		this.cashFlow.setReadonly(isReadOnly("CustomerBalanceSheetDialog_cashFlow"));
		this.bookValue.setReadonly(isReadOnly("CustomerBalanceSheetDialog_bookValue"));
		this.marketValue.setReadonly(isReadOnly("CustomerBalanceSheetDialog_marketValue"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerBalanceSheet.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			if(newCustomer){
				if (isNewRecord()){
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.financialYear.setDisabled(true);
		this.totalAssets.setReadonly(true);
		this.totalLiabilities.setReadonly(true);
		this.netProfit.setReadonly(true);
		this.netSales.setReadonly(true);
		this.netIncome.setReadonly(true);
		this.operatingProfit.setReadonly(true);
		this.cashFlow.setReadonly(true);
		this.bookValue.setReadonly(true);
		this.marketValue.setReadonly(true);
		this.btnSearchPRCustid.setVisible(false);

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
		this.custID.setText("");
		this.financialYear.setValue("");
		this.totalAssets.setValue("");
		this.totalLiabilities.setValue("");
		this.netProfit.setValue("");
		this.netSales.setValue("");
		this.netIncome.setValue("");
		this.operatingProfit.setValue("");
		this.cashFlow.setValue("");
		this.bookValue.setValue("");
		this.marketValue.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CustomerBalanceSheet aCustomerBalanceSheet = new CustomerBalanceSheet();
		BeanUtils.copyProperties(getCustomerBalanceSheet(), aCustomerBalanceSheet);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerBalanceSheet object with the components data
		doWriteComponentsToBean(aCustomerBalanceSheet);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerBalanceSheet.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerBalanceSheet.getRecordType())){
				aCustomerBalanceSheet.setVersion(aCustomerBalanceSheet.getVersion()+1);
				if(isNew){
					aCustomerBalanceSheet.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerBalanceSheet.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerBalanceSheet.setNewRecord(true);
				}
			}
		}else{
			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerBalanceSheet.setVersion(1);
					aCustomerBalanceSheet.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isBlank(aCustomerBalanceSheet.getRecordType())){
					aCustomerBalanceSheet.setVersion(aCustomerBalanceSheet.getVersion()+1);
					aCustomerBalanceSheet.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerBalanceSheet.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerBalanceSheet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerBalanceSheet.setVersion(aCustomerBalanceSheet.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if(isNewCustomer()){
				AuditHeader auditHeader =  newCusomerProcess(aCustomerBalanceSheet,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBalanceSheetDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					//getCustomerDialogCtrl().doFillCustomerBalanceSheet(this.balanceSheetDetails);
					//true;
					// send the data back to customer
					closeDialog();

				}

			}else if(doProcess(aCustomerBalanceSheet,tranType)){
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCusomerProcess(CustomerBalanceSheet aCustomerBalanceSheet, String tranType) {
		return null;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerBalanceSheet (CustomerBalanceSheet)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerBalanceSheet aCustomerBalanceSheet,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCustomerBalanceSheet.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerBalanceSheet.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerBalanceSheet.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerBalanceSheet.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerBalanceSheet.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerBalanceSheet);
				}

				if (isNotesMandatory(taskId, aCustomerBalanceSheet)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();
			} else {
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

			aCustomerBalanceSheet.setTaskId(taskId);
			aCustomerBalanceSheet.setNextTaskId(nextTaskId);
			aCustomerBalanceSheet.setRoleCode(getRole());
			aCustomerBalanceSheet.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCustomerBalanceSheet, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerBalanceSheet);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerBalanceSheet, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerBalanceSheet, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader  (AuditHeader)
	 * @param method (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		CustomerBalanceSheet aCustomerBalanceSheet = (CustomerBalanceSheet) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCustomerBalanceSheetService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCustomerBalanceSheetService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCustomerBalanceSheetService().doApprove(auditHeader);

						if(aCustomerBalanceSheet.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCustomerBalanceSheetService().doReject(auditHeader);
						if(aCustomerBalanceSheet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerBalanceSheetDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CustomerBalanceSheetDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.customerBalanceSheet),true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	private void setListFinancialYear(){
		for (int i = 0; i < listFinancialYear.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listFinancialYear.get(i).getLabel());
			comboitem.setValue(listFinancialYear.get(i).getValue());
			this.financialYear.appendChild(comboitem);
		} 
	}

	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("custCtgType",PennantConstants.PFF_CUSTCTG_CORP);
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerBalanceSheet aCustomerBalanceSheet, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerBalanceSheet.getBefImage(), 
				aCustomerBalanceSheet);   
		return new AuditHeader(getReference(),String.valueOf(aCustomerBalanceSheet.getCustId()),null,null,auditDetail,
				aCustomerBalanceSheet.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * s
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerBalanceSheetDialog, auditHeader);
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
		doShowNotes(this.customerBalanceSheet);
	}
	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerBalanceSheet().getCustId()+PennantConstants.KEY_SEPERATOR
		   +getCustomerBalanceSheet().getFinancialYear()
		;
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

	public CustomerBalanceSheet getCustomerBalanceSheet() {
		return this.customerBalanceSheet;
	}
	public void setCustomerBalanceSheet(CustomerBalanceSheet customerBalanceSheet) {
		this.customerBalanceSheet = customerBalanceSheet;
	}

	public void setCustomerBalanceSheetService(CustomerBalanceSheetService customerBalanceSheetService) {
		this.customerBalanceSheetService = customerBalanceSheetService;
	}
	public CustomerBalanceSheetService getCustomerBalanceSheetService() {
		return this.customerBalanceSheetService;
	}

	public void setCustomerBalanceSheetListCtrl(CustomerBalanceSheetListCtrl customerBalanceSheetListCtrl) {
		this.customerBalanceSheetListCtrl = customerBalanceSheetListCtrl;
	}
	public CustomerBalanceSheetListCtrl getCustomerBalanceSheetListCtrl() {
		return this.customerBalanceSheetListCtrl;
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

	public List<CustomerBalanceSheet> getBalanceSheetDetails() {
		return balanceSheetDetails;
	}
	public void setBalanceSheetDetails(
			List<CustomerBalanceSheet> balanceSheetDetails) {
		this.balanceSheetDetails = balanceSheetDetails;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}
	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
}
