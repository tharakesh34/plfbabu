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

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerBalanceSheetService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerBalanceSheet/customerBalanceSheetDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerBalanceSheetDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 2360247056482972784L;
	private final static Logger logger = Logger.getLogger(CustomerBalanceSheetDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	 window_CustomerBalanceSheetDialog;	// autowired
	protected Longbox 	 custID; 						   	// autowired
	protected Combobox 	 financialYear; 					// autowired
	protected Decimalbox totalAssets; 						// autowired
	protected Decimalbox totalLiabilities; 					// autowired
	protected Decimalbox netProfit; 						// autowired
	protected Decimalbox netSales; 							// autowired
	protected Decimalbox netIncome; 						// autowired
	protected Decimalbox operatingProfit; 					// autowired
	protected Decimalbox cashFlow;	 						// autowired
	protected Decimalbox bookValue; 						// autowired
	protected Decimalbox marketValue; 						// autowired
	protected Textbox 	 custCIF;							// autowired
	protected Label 	 custShrtName;						// autowired

	protected Label 		recordStatus; 					// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired vars
	private CustomerBalanceSheet customerBalanceSheet; 							// overhanded per param
	private transient CustomerBalanceSheetListCtrl customerBalanceSheetListCtrl;// overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient long  		oldVar_custID;
	private transient String  		oldVar_financialYear;
	private transient BigDecimal  	oldVar_totalAssets;
	private transient BigDecimal  	oldVar_totalLiabilities;
	private transient BigDecimal  	oldVar_netProfit;
	private transient BigDecimal  	oldVar_netSales;
	private transient BigDecimal  	oldVar_netIncome;
	private transient BigDecimal  	oldVar_operatingProfit;
	private transient BigDecimal  	oldVar_cashFlow;
	private transient BigDecimal  	oldVar_bookValue;
	private transient BigDecimal  	oldVar_marketValue;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerBalanceSheetDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire
	protected Button btnSearchPRCustid; // autowire

	// ServiceDAOs / Domain Classes
	private transient CustomerBalanceSheetService customerBalanceSheetService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private List<ValueLabel> listFinancialYear= null; // autowired

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

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerBalanceSheet object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerBalanceSheetDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix,
				true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("customerBalanceSheet")) {
			this.customerBalanceSheet = (CustomerBalanceSheet) args.get("customerBalanceSheet");
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

		if(args.containsKey("customerDialogCtrl")){

			setCustomerDialogCtrl((CustomerDialogCtrl) args.get("customerDialogCtrl"));
			setNewCustomer(true);

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerBalanceSheet.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"),
				"CustomerBalanceSheetDialog");
			}
		}

		doLoadWorkFlow(this.customerBalanceSheet.isWorkflow(),
				this.customerBalanceSheet.getWorkflowId(),this.customerBalanceSheet.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerBalanceSheetDialog");
		}

		setListFinancialYear();

		// READ OVERHANDED params !
		// we get the customerBalanceSheetListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerBalanceSheet here.
		if (args.containsKey("customerBalanceSheetListCtrl")) {
			setCustomerBalanceSheetListCtrl((CustomerBalanceSheetListCtrl) args.get(
			"customerBalanceSheetListCtrl"));
		} else {
			setCustomerBalanceSheetListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerBalanceSheet());

		//Calling SelectCtrl For proper selection of Customer
		if(isNewRecord() & !isNewCustomer()){
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
		this.totalAssets.setFormat(PennantAppUtil.getAmountFormate(0));
		this.totalAssets.setScale(0);
		this.totalLiabilities.setMaxlength(18);
		this.totalLiabilities.setFormat(PennantAppUtil.getAmountFormate(0));
		this.totalLiabilities.setScale(0);
		this.netProfit.setMaxlength(18);
		this.netProfit.setFormat(PennantAppUtil.getAmountFormate(0));
		this.netProfit.setScale(0);
		this.netSales.setMaxlength(18);
		this.netSales.setFormat(PennantAppUtil.getAmountFormate(0));
		this.netSales.setScale(0);
		this.netIncome.setMaxlength(18);
		this.netIncome.setFormat(PennantAppUtil.getAmountFormate(0));
		this.netIncome.setScale(0);
		this.operatingProfit.setMaxlength(18);
		this.operatingProfit.setFormat(PennantAppUtil.getAmountFormate(0));
		this.operatingProfit.setScale(0);
		this.cashFlow.setMaxlength(18);
		this.cashFlow.setFormat(PennantAppUtil.getAmountFormate(0));
		this.cashFlow.setScale(0);
		this.bookValue.setMaxlength(18);
		this.bookValue.setFormat(PennantAppUtil.getAmountFormate(0));
		this.bookValue.setScale(0);
		this.marketValue.setMaxlength(18);
		this.marketValue.setFormat(PennantAppUtil.getAmountFormate(0));
		this.marketValue.setScale(0);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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

		getUserWorkspace().alocateAuthorities("CustomerBalanceSheetDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerBalanceSheetDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerBalanceSheetDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerBalanceSheetDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerBalanceSheetDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++//
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CustomerBalanceSheetDialog(Event event) throws Exception {
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
		// remember the old vars
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
		PTMessageUtils.showHelpWindow(event, window_CustomerBalanceSheetDialog);
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
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
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
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			closeWindow();

		}

		logger.debug("Leaving") ;
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewCustomer()){
			window_CustomerBalanceSheetDialog.onClose();	
		}else{
			closeDialog(this.window_CustomerBalanceSheetDialog, "CustomerBalanceSheet");	
		}
		logger.debug("Leaving");
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
		this.financialYear.setValue(PennantAppUtil.getlabelDesc(
				aCustomerBalanceSheet.getFinancialYear(),listFinancialYear));
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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			if(isNewCustomer()){
				this.window_CustomerBalanceSheetDialog.setHeight("400px");
				this.window_CustomerBalanceSheetDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_CustomerBalanceSheetDialog.doModal() ;
			}else{
				this.window_CustomerBalanceSheetDialog.setWidth("100%");
				this.window_CustomerBalanceSheetDialog.setHeight("100%");
				setDialog(this.window_CustomerBalanceSheetDialog);
			}
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
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_financialYear = this.financialYear.getValue();
		this.oldVar_totalAssets = this.totalAssets.getValue();
		this.oldVar_totalLiabilities = this.totalLiabilities.getValue();
		this.oldVar_netProfit = this.netProfit.getValue();
		this.oldVar_netSales = this.netSales.getValue();
		this.oldVar_netIncome = this.netIncome.getValue();
		this.oldVar_operatingProfit = this.operatingProfit.getValue();
		this.oldVar_cashFlow = this.cashFlow.getValue();
		this.oldVar_bookValue = this.bookValue.getValue();
		this.oldVar_marketValue = this.marketValue.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custID.setValue(this.oldVar_custID);
		this.financialYear.setValue(this.oldVar_financialYear);
		this.totalAssets.setValue(this.oldVar_totalAssets);
		this.totalLiabilities.setValue(this.oldVar_totalLiabilities);
		this.netProfit.setValue(this.oldVar_netProfit);
		this.netSales.setValue(this.oldVar_netSales);
		this.netIncome.setValue(this.oldVar_netIncome);
		this.operatingProfit.setValue(this.oldVar_operatingProfit);
		this.cashFlow.setValue(this.oldVar_cashFlow);
		this.bookValue.setValue(this.oldVar_bookValue);
		this.marketValue.setValue(this.oldVar_marketValue);
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

		//To clear the Error Messages
		doClearMessage();

		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (this.oldVar_totalAssets != this.totalAssets.getValue()) {
			return true;
		}
		if (this.oldVar_totalLiabilities != this.totalLiabilities.getValue()) {
			return true;
		}
		if (this.oldVar_netProfit != this.netProfit.getValue()) {
			return true;
		}
		if (this.oldVar_netSales != this.netSales.getValue()) {
			return true;
		}
		if (this.oldVar_netIncome != this.netIncome.getValue()) {
			return true;
		}
		if (this.oldVar_operatingProfit != this.operatingProfit.getValue()) {
			return true;
		}
		if (this.oldVar_cashFlow != this.cashFlow.getValue()) {
			return true;
		}
		if (this.oldVar_bookValue != this.bookValue.getValue()) {
			return true;
		}
		if (this.oldVar_marketValue != this.marketValue.getValue()) {
			return true;
		}

		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.custID.isReadonly()){
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_CustomerBalanceSheetDialog_CustId.value")}));
		}
		if (!this.financialYear.isDisabled()){
			this.financialYear.setConstraint(new StaticListValidator(listFinancialYear,Labels.getLabel("label_CustomerBalanceSheetDialog_FinancialYear.value")));
		}	
		if (!this.totalAssets.isReadonly()){
			this.totalAssets.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_TotalAssets.value")));
		}	
		if (!this.totalLiabilities.isReadonly()){
			this.totalLiabilities.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_TotalLiabilities.value")));
		}	
		if (!this.netProfit.isReadonly()){
			this.netProfit.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_NetProfit.value")));
		}	
		if (!this.netSales.isReadonly()){
			this.netSales.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_NetSales.value")));
		}	
		if (!this.netIncome.isReadonly()){
			this.netIncome.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_NetIncome.value")));
		}	
		if (!this.operatingProfit.isReadonly()){
			this.operatingProfit.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_OperatingProfit.value")));
		}	
		if (!this.cashFlow.isReadonly()){
			this.cashFlow.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_CashFlow.value")));
		}	
		if (!this.bookValue.isReadonly()){
			this.bookValue.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_BookValue.value")));
		}	
		if (!this.marketValue.isReadonly()){
			this.marketValue.setConstraint(new AmountValidator(18,0,Labels.getLabel(
			"label_CustomerBalanceSheetDialog_MarketValue.value")));
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
	private void doClearMessage() {
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
		getCustomerBalanceSheetListCtrl().findSearchObject();
		if (getCustomerBalanceSheetListCtrl().listBoxCustomerBalanceSheet != null) {
			getCustomerBalanceSheetListCtrl().listBoxCustomerBalanceSheet.getListModel();
		}
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerBalanceSheet.getRecordType()).equals("")){
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
						closeWindow();
					}	
				}else if(doProcess(aCustomerBalanceSheet,tranType)){
					refreshList();
					closeWindow();
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerBalanceSheet object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		final CustomerBalanceSheet aCustomerBalanceSheet = getCustomerBalanceSheetService().
		getNewCustomerBalanceSheet();
		aCustomerBalanceSheet.setNewRecord(true);
		setCustomerBalanceSheet(aCustomerBalanceSheet);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.custCIF.focus();
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aCustomerBalanceSheet.getRecordType()).equals("")){
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

				if(StringUtils.trimToEmpty(aCustomerBalanceSheet.getRecordType()).equals("")){
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
					closeWindow();

				}

			}else if(doProcess(aCustomerBalanceSheet,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeWindow();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCusomerProcess(CustomerBalanceSheet aCustomerBalanceSheet,String tranType){
		return null;} 

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

		aCustomerBalanceSheet.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerBalanceSheet.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerBalanceSheet.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerBalanceSheet.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerBalanceSheet.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerBalanceSheet);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCustomerBalanceSheet))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
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

			aCustomerBalanceSheet.setTaskId(taskId);
			aCustomerBalanceSheet.setNextTaskId(nextTaskId);
			aCustomerBalanceSheet.setRoleCode(getRole());
			aCustomerBalanceSheet.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCustomerBalanceSheet, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerBalanceSheet);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerBalanceSheetDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CustomerBalanceSheetDialog, auditHeader);
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
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
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
		map.put("custCtgType","C");
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerBalanceSheetDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
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

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("CustomerBalanceSheet");
		notes.setReference(getReference());
		notes.setVersion(getCustomerBalanceSheet().getVersion());
		return notes;
	}
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getCustomerBalanceSheet().getCustId()+PennantConstants.KEY_SEPERATOR
		   +getCustomerBalanceSheet().getFinancialYear()
		;
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
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
