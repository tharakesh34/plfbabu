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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.FinanceCustomerListCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerChequeInfo/customerChequeInfoDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerChequeInfoDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7522534300621535097L;
	private final static Logger logger = Logger.getLogger(CustomerChequeInfoDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerChequeInfoDialog; 		// autoWired

	protected Longbox 	custID; 						// autoWired
	protected Textbox 	custCIF;						// autoWired
	protected Label   	custShrtName;					// autoWired
	protected Datebox 	monthYear; 					    // autoWired
	protected CurrencyBox 	totChequePayment; 		    // autoWired
	protected CurrencyBox 	salary; 		            // autoWired
	protected CurrencyBox 	returnChequeAmt; 		    // autoWired
	protected Intbox 	returnChequeCount; 		        // autoWired
	protected Intbox chequeSeq;          		        // autoWired

	protected Label recordStatus; 						// autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected South 	south;
	
	// not auto wired variables
	private CustomerChequeInfo customerChequeInfo; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initial.
	private transient long  		oldVar_custID;
	private transient Date  		oldVar_monthYear;
	protected transient BigDecimal 	oldVar_totChequePayment;
	protected transient BigDecimal 	oldVar_salary;
	protected transient BigDecimal 	oldVar_returnChequeAmt;
	protected transient int 		oldVar_returnChequeCount;
	private transient String 		oldVar_recordStatus;
	
	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerChequeInfoDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 			// autoWired
	protected Button btnEdit; 			// autoWired
	protected Button btnDelete; 		// autoWired
	protected Button btnSave; 			// autoWired
	protected Button btnCancel; 		// autoWired
	protected Button btnClose; 			// autoWired
	protected Button btnHelp; 			// autoWired
	protected Button btnNotes; 			// autoWired
	protected Button btnSearchPRCustid; // autoWired

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerChequeInfo> CustomerChequeInfoList;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String userRole="";
	private FinanceCustomerListCtrl financeCustomerListCtrl;
	private int finFormatter;

	/**
	 * default constructor.<br>
	 */
	public CustomerChequeInfoDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerChequeInfo object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerChequeInfoDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());


		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("customerChequeInfo")) {
			this.customerChequeInfo = (CustomerChequeInfo) args.get("customerChequeInfo");
			CustomerChequeInfo befImage = new CustomerChequeInfo();
			BeanUtils.copyProperties(this.customerChequeInfo, befImage);
			this.customerChequeInfo.setBefImage(befImage);
			setCustomerChequeInfo(this.customerChequeInfo);
		} else {
			setCustomerChequeInfo(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}
		
		if (args.containsKey("finFormatter")) {
			this.finFormatter = (Integer) args.get("finFormatter");
		}

		if(getCustomerChequeInfo().isNewRecord()){
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
			this.customerChequeInfo.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerChequeInfoDialog");
			}
		}
      if(args.containsKey("financeCustomerListCtrl")){
			
			setFinanceCustomerListCtrl((FinanceCustomerListCtrl) args.get("financeCustomerListCtrl"));
			setNewCustomer(true);
			
			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerChequeInfo.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerChequeInfoDialog");
			}
		}
		doLoadWorkFlow(this.customerChequeInfo.isWorkflow(),
				this.customerChequeInfo.getWorkflowId(), this.customerChequeInfo.getNextTaskId());
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"CustomerChequeInfoDialog");
		}


		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerChequeInfo());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		this.monthYear.setFormat(PennantConstants.dateFormat);
		
		this.totChequePayment.setMandatory(true);
		this.totChequePayment.setMaxlength(18);
		this.totChequePayment.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
		this.salary.setMandatory(true);
		this.salary.setMaxlength(18);
		this.salary.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
		this.returnChequeAmt.setMandatory(true);
		this.returnChequeAmt.setMaxlength(18);
		this.returnChequeAmt.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
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
		getUserWorkspace().alocateAuthorities("CustomerChequeInfoDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerChequeInfoDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerChequeInfoDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerChequeInfoDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerChequeInfoDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
	public void onClose$window_CustomerChequeInfoDialog(Event event)throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerChequeInfoDialog);
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
		} catch (final WrongValueException e) {
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
		boolean close= true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close= false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if(close){
			closeWindow();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewCustomer()){
			closePopUpWindow(this.window_CustomerChequeInfoDialog,"CustomerChequeInfoDialog");
		}else{
			closeDialog(this.window_CustomerChequeInfoDialog, "CustomerChequeInfoDialog");
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
		logger.debug("Entering");
		doResetInitValues();
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
		this.returnChequeAmt.setValue(PennantAppUtil.formateAmount(aCustomerChequeInfo.getReturnChequeAmt(),finFormatter));
		this.returnChequeCount.setValue(aCustomerChequeInfo.getReturnChequeCount());

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
			aCustomerChequeInfo.setTotChequePayment(PennantAppUtil.unFormateAmount(this.totChequePayment.getValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomerChequeInfo.setSalary(PennantAppUtil.unFormateAmount(this.salary.getValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerChequeInfo.setReturnChequeAmt(PennantAppUtil.unFormateAmount(this.returnChequeAmt.getValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerChequeInfo.setReturnChequeCount(this.returnChequeCount.getValue());
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerChequeInfo aCustomerChequeInfo) throws InterruptedException {
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

			// stores the initial data for comparing if they are changed during user action.
			doStoreInitValues();
            doCheckEnquiry();
			if(isNewCustomer()){
				this.window_CustomerChequeInfoDialog.setHeight("45%");
				this.window_CustomerChequeInfoDialog.setWidth("70%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerChequeInfoDialog.doModal() ;
			}else{
				this.window_CustomerChequeInfoDialog.setWidth("100%");
				this.window_CustomerChequeInfoDialog.setHeight("100%");
				setDialog(this.window_CustomerChequeInfoDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if("ENQ".equals(this.moduleType)){
			this.monthYear.setReadonly(true);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_monthYear = this.monthYear.getValue();
		this.oldVar_totChequePayment = this.totChequePayment.getValue();
		this.oldVar_salary = this.salary.getValue();
		this.oldVar_returnChequeAmt = this.returnChequeAmt.getValue();
		this.oldVar_returnChequeCount = this.returnChequeCount.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custID.setValue(this.oldVar_custID);
		this.monthYear.setValue(this.oldVar_monthYear);
		this.totChequePayment.setValue(this.oldVar_totChequePayment);
		this.salary.setValue(this.oldVar_salary);
		this.returnChequeAmt.setValue(this.oldVar_returnChequeAmt);
		this.returnChequeCount.setValue(this.oldVar_returnChequeCount);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
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

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		
		if (this.oldVar_monthYear != this.monthYear.getValue()) {
			return true;
		}

		if (this.oldVar_totChequePayment != this.totChequePayment.getValue()) {
			return true;
		}
		
		if (this.oldVar_salary != this.salary.getValue()) {
			return true;
		}
		
		if (this.oldVar_returnChequeAmt != this.returnChequeAmt.getValue()) {
			return true;
		}
		
		if (this.oldVar_returnChequeCount != this.returnChequeCount.getValue()) {
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
		if (!this.monthYear.isReadonly()) {
			this.monthYear.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerChequeInfoDialog_MonthYear.value"), null, true));
		}
		if (!this.totChequePayment.isDisabled()) {
			this.totChequePayment.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_CustomerChequeInfoDialog_TotChequePayment.value"), false));
		}
		if (!this.salary.isDisabled()) {
			this.salary.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_CustomerChequeInfoDialog_Salary.value"), false));
		}
		if (!this.returnChequeAmt.isDisabled()) {
			this.returnChequeAmt.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_CustomerChequeInfoDialog_ReturnChequeAmt.value"), false));
		}
		if (!this.returnChequeCount.isDisabled()) {
			this.returnChequeCount.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerChequeInfoDialog_ReturnChequeCount.value"), true, false));
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
		this.returnChequeAmt.setConstraint("");
		this.returnChequeCount.setConstraint("");
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
	private void doClearMessage() {
		logger.debug("Entering");
		this.monthYear.setErrorMessage("");
		this.totChequePayment.setErrorMessage("");
		this.salary.setErrorMessage("");
		this.returnChequeAmt.setErrorMessage("");
		this.returnChequeCount.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String msg = Labels
		.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aCustomerChequeInfo.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerChequeInfo.getRecordType()).equals("")) {
				aCustomerChequeInfo.setVersion(aCustomerChequeInfo.getVersion() + 1);
				aCustomerChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
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
					getFinanceCustomerListCtrl().doFillCustomerChequeInfoDetails(this.CustomerChequeInfoList);
					this.window_CustomerChequeInfoDialog.onClose();
				}
			} catch (DataAccessException e) {
				logger.error(e);
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
			this.monthYear.setReadonly(isReadOnly("CustomerChequeInfoDialog_monthYear"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.monthYear.setReadonly(true);
		}
		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.totChequePayment.setReadonly(isReadOnly("CustomerChequeInfoDialog_totChequePayment"));
		this.monthYear.setDisabled(isReadOnly("CustomerChequeInfoDialog_monthYear"));
		this.salary.setReadonly(isReadOnly("CustomerChequeInfoDialog_salary"));
		this.returnChequeAmt.setReadonly(isReadOnly("CustomerChequeInfoDialog_returnChequeAmt"));
		this.returnChequeCount.setReadonly(isReadOnly("CustomerChequeInfoDialog_returnChequeCount"));


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
				if("ENQ".equals(this.moduleType)){
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
		if(getFinanceCustomerListCtrl()!= null){
			isCustomerWorkflow = getFinanceCustomerListCtrl().getCustomerDetails().getCustomer().isWorkflow();
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
		this.monthYear.setReadonly(true);

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
		this.returnChequeAmt.setValue("");
		this.returnChequeCount.setValue(0);
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aCustomerChequeInfo.getRecordType()).equals("")){
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

				if(StringUtils.trimToEmpty(aCustomerChequeInfo.getRecordType()).equals("")){
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
				getFinanceCustomerListCtrl().doFillCustomerChequeInfoDetails(this.CustomerChequeInfoList);
				this.window_CustomerChequeInfoDialog.onClose();
			}
		
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
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
		
		if(getFinanceCustomerListCtrl().getCustomerChequeInfoDetailList()!=null && getFinanceCustomerListCtrl().getCustomerChequeInfoDetailList().size()>0){
			for (int i = 0; i < getFinanceCustomerListCtrl().getCustomerChequeInfoDetailList().size(); i++) {
				CustomerChequeInfo customerChequeInfo = getFinanceCustomerListCtrl().getCustomerChequeInfoDetailList().get(i);
				
				
				if(aCustomerChequeInfo.getChequeSeq()==customerChequeInfo.getChequeSeq()){ // Both Current and Existing list rating same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",
										errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if(tranType==PennantConstants.TRAN_DEL){
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
							for (int j = 0; j < getFinanceCustomerListCtrl().getCustomerDetails().getCustomerChequeInfoList().size(); j++) {
								CustomerChequeInfo email =  getFinanceCustomerListCtrl().getCustomerDetails().getCustomerChequeInfoList().get(j);
								if(email.getCustID() == aCustomerChequeInfo.getCustID() && 
										email.getChequeSeq() == aCustomerChequeInfo.getChequeSeq()){
									CustomerChequeInfoList.add(email);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							CustomerChequeInfoList.add(customerChequeInfo);
						}
					}
				}else{
					CustomerChequeInfoList.add(customerChequeInfo);
				}
			}
		}
		
		if(!recordAdded){
			CustomerChequeInfoList.add(aCustomerChequeInfo);
		}
		logger.debug("Leaving");
		return auditHeader;
	} 


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerChequeInfoDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("CustomerChequeInfo");
		notes.setReference(getReference());
		notes.setVersion(getCustomerChequeInfo().getVersion());
		return notes;
	}
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getCustomerChequeInfo().getCustID()+ PennantConstants.KEY_SEPERATOR
		+ getCustomerChequeInfo().getChequeSeq();
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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

	public FinanceCustomerListCtrl getFinanceCustomerListCtrl() {
		return financeCustomerListCtrl;
	}
	public void setFinanceCustomerListCtrl(
			FinanceCustomerListCtrl financeCustomerListCtrl) {
		this.financeCustomerListCtrl = financeCustomerListCtrl;
	}
	
}
