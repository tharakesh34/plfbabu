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
 * FileName    		:  CustomerBankInfoDialogCtrl.java                                                   * 	  
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
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
 * /WEB-INF/pages/CustomerMasters/CustomerBankInfo/customerBankInfoDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerBankInfoDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7522534300621535097L;
	private final static Logger logger = Logger.getLogger(CustomerBankInfoDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerBankInfoDialog; 		// autoWired

	protected Longbox 	custID; 						// autoWired
	protected Textbox 	custCIF;						// autoWired
	protected Label   	custShrtName;					// autoWired
	protected ExtendedCombobox 	bankName; 				// autoWired
	protected Textbox 	accountNumber; 					// autoWired
	protected ExtendedCombobox 	accountType; 			// autoWired

	protected Label recordStatus; 						// autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected South 	south;
	
	// not auto wired variables
	private CustomerBankInfo customerBankInfo; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initial.
	private transient long  		oldVar_custID;
	private transient String  		oldVar_bankName;
	private transient String  		oldVar_accountNumber;
	private transient String  		oldVar_accountType;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerBankInfoDialog_";
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

	private transient String oldVar_lovDescBankName;
	private transient String oldVar_lovDescAccountType;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerBankInfo> CustomerBankInfoList;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String userRole="";
	private FinanceCustomerListCtrl financeCustomerListCtrl;

	/**
	 * default constructor.<br>
	 */
	public CustomerBankInfoDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerBankInfo object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerBankInfoDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());


		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("customerBankInfo")) {
			this.customerBankInfo = (CustomerBankInfo) args.get("customerBankInfo");
			CustomerBankInfo befImage = new CustomerBankInfo();
			BeanUtils.copyProperties(this.customerBankInfo, befImage);
			this.customerBankInfo.setBefImage(befImage);
			setCustomerBankInfo(this.customerBankInfo);
		} else {
			setCustomerBankInfo(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}

		if(getCustomerBankInfo().isNewRecord()){
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
			this.customerBankInfo.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerBankInfoDialog");
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
			this.customerBankInfo.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerBankInfoDialog");
			}
		}
		doLoadWorkFlow(this.customerBankInfo.isWorkflow(),
				this.customerBankInfo.getWorkflowId(), this.customerBankInfo.getNextTaskId());
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"CustomerBankInfoDialog");
		}


		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerBankInfo());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.bankName.setMaxlength(8);
		this.bankName.setMandatoryStyle(true);
		this.bankName.setTextBoxWidth(117);
		this.bankName.setModuleName("BankDetail");
		this.bankName.setValueColumn("BankCode");
		this.bankName.setDescColumn("BankName");
		this.bankName.setValidateColumns(new String[] { "BankCode" });
		
		this.accountType.setMaxlength(8);
		this.accountType.setMandatoryStyle(true);
		this.accountType.setTextBoxWidth(110);
		this.accountType.setModuleName("AccountType");
		this.accountType.setValueColumn("AcType");
		this.accountType.setDescColumn("AcTypeDesc");
		this.accountType.setValidateColumns(new String[] { "AcType" });
		
		this.accountNumber.setMaxlength(50);
		this.accountType.setMaxlength(8);

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
		getUserWorkspace().alocateAuthorities("CustomerBankInfoDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnSave"));
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
	public void onClose$window_CustomerBankInfoDialog(Event event)throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerBankInfoDialog);
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
			closePopUpWindow(this.window_CustomerBankInfoDialog,"CustomerBankInfoDialog");
		}else{
			closeDialog(this.window_CustomerBankInfoDialog, "CustomerBankInfoDialog");
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
	 * @param aCustomerBankInfo
	 *            CustomerBankInfo
	 */
	public void doWriteBeanToComponents(CustomerBankInfo aCustomerBankInfo) {
		logger.debug("Entering");

		if(aCustomerBankInfo.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerBankInfo.getCustID());
		}
		this.bankName.setValue(aCustomerBankInfo.getBankName());
		this.bankName.setDescription(StringUtils.trimToEmpty(aCustomerBankInfo.getLovDescBankName()));
		this.accountNumber.setValue(aCustomerBankInfo.getAccountNumber());
		this.accountType.setValue(aCustomerBankInfo.getAccountType());
		this.accountType.setDescription(StringUtils.trimToEmpty(aCustomerBankInfo.getLovDescAccountType()));
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomerBankInfo.getLovDescCustCIF()));
		this.custShrtName.setValue(StringUtils.trimToEmpty(aCustomerBankInfo.getLovDescCustShrtName()));

		
		this.recordStatus.setValue(aCustomerBankInfo.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerBankInfo
	 */
	public void doWriteComponentsToBean(CustomerBankInfo aCustomerBankInfo) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerBankInfo.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerBankInfo.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setLovDescBankName(this.bankName.getDescription());
			aCustomerBankInfo.setBankName(this.bankName.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerBankInfo.setAccountNumber(this.accountNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomerBankInfo.setLovDescAccountType(this.accountType.getDescription());
			aCustomerBankInfo.setAccountType(this.accountType.getValidatedValue());
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

		aCustomerBankInfo.setRecordStatus(this.recordStatus.getValue());
		setCustomerBankInfo(aCustomerBankInfo);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerBankInfo
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerBankInfo aCustomerBankInfo) throws InterruptedException {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.bankName.focus();
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
			doWriteBeanToComponents(aCustomerBankInfo);

			// stores the initial data for comparing if they are changed during user action.
			doStoreInitValues();
            doCheckEnquiry();
            if(isNewCustomer()){
				this.window_CustomerBankInfoDialog.setHeight("35%");
				this.window_CustomerBankInfoDialog.setWidth("70%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerBankInfoDialog.doModal() ;
			}else{
				this.window_CustomerBankInfoDialog.setWidth("100%");
				this.window_CustomerBankInfoDialog.setHeight("100%");
				setDialog(this.window_CustomerBankInfoDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if("ENQ".equals(this.moduleType)){
			this.bankName.setReadonly(true);
			this.accountType.setReadonly(true);
			this.accountNumber.setReadonly(true);
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
		this.oldVar_bankName = this.bankName.getValue();
		this.oldVar_lovDescBankName = this.bankName.getDescription();
		this.oldVar_accountNumber = this.accountNumber.getValue();
		this.oldVar_accountType = this.accountType.getValue();
		this.oldVar_lovDescAccountType = this.accountType.getDescription();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custID.setValue(this.oldVar_custID);
		this.bankName.setValue(this.oldVar_bankName);
		this.bankName.setDescription(this.oldVar_lovDescBankName);
		this.accountNumber.setValue(this.oldVar_accountNumber);
		this.accountType.setValue(this.oldVar_accountType);
		this.accountType.setDescription(this.oldVar_lovDescAccountType);
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
		if (this.oldVar_bankName != this.bankName.getValue()) {
			return true;
		}
		if (this.oldVar_accountNumber != this.accountNumber.getValue()) {
			return true;
		}
		if (this.oldVar_accountType != this.accountType.getValue()) {
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
		if (!this.accountNumber.isReadonly()) {
			this.accountNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerBankInfoDialog_AccountNumber.value"), null, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.accountNumber.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.bankName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerBankInfoDialog_BankName.value"),null,true,true));
		this.accountType.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerBankInfoDialog_AccountType.value"),null,true,true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.bankName.setConstraint("");
		this.accountType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.accountNumber.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.accountType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerBankInfo object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerBankInfo aCustomerBankInfo = new CustomerBankInfo();
		BeanUtils.copyProperties(getCustomerBankInfo(), aCustomerBankInfo);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
		.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aCustomerBankInfo.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerBankInfo.getRecordType()).equals("")) {
				aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion() + 1);
				aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerBankInfo.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()) {
					aCustomerBankInfo.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerBankInfo, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBankInfoDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinanceCustomerListCtrl().doFillCustomerBankInfoDetails(this.CustomerBankInfoList);
					this.window_CustomerBankInfoDialog.onClose();
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
			this.bankName.setReadonly(isReadOnly("CustomerBankInfoDialog_BankName"));
			this.bankName.setMandatoryStyle(!isReadOnly("CustomerBankInfoDialog_BankName"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.bankName.setReadonly(true);
		}
		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.accountType.setReadonly(isReadOnly("CustomerBankInfoDialog_AccountType"));
		this.accountNumber.setReadonly(isReadOnly("CustomerBankInfoDialog_AccountNumber"));


		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerBankInfo.isNewRecord()) {
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
		this.bankName.setReadonly(true);
		this.accountType.setReadonly(true);
		this.accountNumber.setReadonly(true);

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
		this.bankName.setValue("");
		this.bankName.setDescription("");
		this.accountType.setValue("");
		this.accountType.setDescription("");
		this.accountNumber.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerBankInfo aCustomerBankInfo = new CustomerBankInfo();
		BeanUtils.copyProperties(getCustomerBankInfo(), aCustomerBankInfo);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerBankInfo object with the components data
		doWriteComponentsToBean(aCustomerBankInfo);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerBankInfo.isNew();
		String tranType = "";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerBankInfo.getRecordType()).equals("")){
				aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion()+1);
				if(isNew){
					aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerBankInfo.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerBankInfo.setVersion(1);
					aCustomerBankInfo.setRecordType(PennantConstants.RCD_ADD);					
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerBankInfo.getRecordType()).equals("")){
					aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion()+1);
					aCustomerBankInfo.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerBankInfo, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBankInfoDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getFinanceCustomerListCtrl().doFillCustomerBankInfoDetails(this.CustomerBankInfoList);
				this.window_CustomerBankInfoDialog.onClose();
			}
		
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	
	private AuditHeader newFinanceCustomerProcess(CustomerBankInfo aCustomerBankInfo,String tranType){
		logger.debug("Entering");
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aCustomerBankInfo, tranType);
		CustomerBankInfoList = new ArrayList<CustomerBankInfo>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = String.valueOf(aCustomerBankInfo.getId());
		valueParm[1] = aCustomerBankInfo.getBankName();
		
		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_BankName")+ ":" + valueParm[1];
		
		if(getFinanceCustomerListCtrl().getCustomerBankInfoDetailList()!=null && getFinanceCustomerListCtrl().getCustomerBankInfoDetailList().size()>0){
			for (int i = 0; i < getFinanceCustomerListCtrl().getCustomerBankInfoDetailList().size(); i++) {
				CustomerBankInfo customerBankInfo = getFinanceCustomerListCtrl().getCustomerBankInfoDetailList().get(i);
				
				
				if(aCustomerBankInfo.getBankName().equals(customerBankInfo.getBankName())){ // Both Current and Existing list rating same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",
										errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							CustomerBankInfoList.add(aCustomerBankInfo);
						}else if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							CustomerBankInfoList.add(aCustomerBankInfo);
						}else if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getFinanceCustomerListCtrl().getCustomerDetails().getCustomerBankInfoList().size(); j++) {
								CustomerBankInfo email =  getFinanceCustomerListCtrl().getCustomerDetails().getCustomerBankInfoList().get(j);
								if(email.getCustID() == aCustomerBankInfo.getCustID() && 
										email.getBankName().equals(aCustomerBankInfo.getBankName())){
									CustomerBankInfoList.add(email);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							CustomerBankInfoList.add(customerBankInfo);
						}
					}
				}else{
					CustomerBankInfoList.add(customerBankInfo);
				}
			}
		}
		
		if(!recordAdded){
			CustomerBankInfoList.add(aCustomerBankInfo);
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
	 * @param aCustomerBankInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerBankInfo aCustomerBankInfo, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,aCustomerBankInfo.getBefImage(), aCustomerBankInfo);
		return new AuditHeader(getReference(), String.valueOf(aCustomerBankInfo.getCustID()), null, null, 
				auditDetail, aCustomerBankInfo.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerBankInfoDialog, auditHeader);
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
		notes.setModuleName("CustomerBankInfo");
		notes.setReference(getReference());
		notes.setVersion(getCustomerBankInfo().getVersion());
		return notes;
	}
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getCustomerBankInfo().getCustID()+ PennantConstants.KEY_SEPERATOR
		+ getCustomerBankInfo().getBankName();
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

	public CustomerBankInfo getCustomerBankInfo() {
		return this.customerBankInfo;
	}
	public void setCustomerBankInfo(CustomerBankInfo customerBankInfo) {
		this.customerBankInfo = customerBankInfo;
	}

	public void setCustomerEmails(List<CustomerBankInfo> customerEmails) {
		this.CustomerBankInfoList = customerEmails;
	}
	public List<CustomerBankInfo> getCustomerEmails() {
		return CustomerBankInfoList;
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
