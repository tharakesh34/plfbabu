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
 * FileName    		:  CustomerPhoneNumberDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerphonenumber;

import java.io.Serializable;
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
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.customermasters.customer.FinanceCustomerListCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/customerPhoneNumberDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerPhoneNumberDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3093280086658721485L;
	private final static Logger logger = Logger.getLogger(CustomerPhoneNumberDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerPhoneNumberDialog; 	// autowired

	protected Longbox 		phoneCustID; 						// autowired
	protected ExtendedCombobox 		phoneTypeCode; 						// autowired
	protected ExtendedCombobox 		phoneCountryCode; 					// autowired
	protected Textbox 		phoneAreaCode; 						// autowired
	protected Textbox 		phoneNumber; 						// autowired
	protected Textbox 		custCIF;							// autowired
	protected Label 		custShrtName;						// autowired

	protected Label 		recordStatus; 						// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected South			south;

	// not auto wired vars
	private CustomerPhoneNumber customerPhoneNumber; // overhanded per param
	private transient CustomerPhoneNumberListCtrl customerPhoneNumberListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient long  	oldVar_phoneCustID;
	private transient String  	oldVar_phoneTypeCode;
	private transient String  	oldVar_phoneCountryCode;
	private transient String  	oldVar_phoneAreaCode;
	private transient String  	oldVar_phoneNumber;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerPhoneNumberDialog_";
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

	private transient String 		oldVar_lovDescPhoneTypeCodeName;
	private transient String oldVar_lovDescPhoneCountryName;

	// ServiceDAOs / Domain Classes
	private transient CustomerPhoneNumberService customerPhoneNumberService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerPhoneNumber> customerPhoneNumbers;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String userRole="";
	private FinanceCustomerListCtrl financeCustomerListCtrl;
	private boolean isFinanceCustomer = false;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerPhoneNumberDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerPhoneNumber object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerPhoneNumberDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());


		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);


		// READ OVERHANDED params !
		if (args.containsKey("customerPhoneNumber")) {
			this.customerPhoneNumber = (CustomerPhoneNumber) args.get("customerPhoneNumber");
			CustomerPhoneNumber befImage =new CustomerPhoneNumber();
			BeanUtils.copyProperties(this.customerPhoneNumber, befImage);
			this.customerPhoneNumber.setBefImage(befImage);
			setCustomerPhoneNumber(this.customerPhoneNumber);
		} else {
			setCustomerPhoneNumber(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}

		if(getCustomerPhoneNumber().isNewRecord()){
			setNewRecord(true);
		}

		if(args.containsKey("customerDialogCtrl")){
			isFinanceCustomer = false; 
			setCustomerDialogCtrl((CustomerDialogCtrl) args.get("customerDialogCtrl"));
			setNewCustomer(true);

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerPhoneNumber.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole,"CustomerPhoneNumberDialog");
			}

		}
        if(args.containsKey("financeCustomerListCtrl")){
			
			isFinanceCustomer = true ;
			setFinanceCustomerListCtrl((FinanceCustomerListCtrl) args.get("financeCustomerListCtrl"));
			setNewCustomer(true);
			
			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerPhoneNumber.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerPhoneNumberDialog");
			}
		}
		doLoadWorkFlow(this.customerPhoneNumber.isWorkflow(),this.customerPhoneNumber.getWorkflowId(),
				this.customerPhoneNumber.getNextTaskId());
		/* set components visible dependent of the users rights */
		doCheckRights();


		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerPhoneNumberDialog");
		}


		// READ OVERHANDED params !
		// we get the customerPhoneNumberListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerPhoneNumber here.
		if (args.containsKey("customerPhoneNumberListCtrl")) {
			setCustomerPhoneNumberListCtrl((CustomerPhoneNumberListCtrl) args.get("customerPhoneNumberListCtrl"));
		} else {
			setCustomerPhoneNumberListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerPhoneNumber());

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
		logger.debug("Entering");
		//Empty sent any required attributes
		this.phoneTypeCode.setMaxlength(8);
		this.phoneTypeCode.setMandatoryStyle(true);
		this.phoneTypeCode.setTextBoxWidth(121);
		this.phoneTypeCode.setModuleName("PhoneType");
		this.phoneTypeCode.setValueColumn("PhoneTypeCode");
		this.phoneTypeCode.setDescColumn("PhoneTypeDesc");
		this.phoneTypeCode.setValidateColumns(new String[] { "PhoneTypeCode" });
		
		this.phoneCountryCode.setMaxlength(2);
		this.phoneCountryCode.setMandatoryStyle(true);
		this.phoneCountryCode.getTextbox().setWidth("110px");
		this.phoneCountryCode.setModuleName("Country");
		this.phoneCountryCode.setValueColumn("CountryCode");
		this.phoneCountryCode.setDescColumn("CountryDesc");
		this.phoneCountryCode.setValidateColumns(new String[] { "CountryCode" });
		
		this.phoneAreaCode.setMaxlength(8);
		this.phoneNumber.setMaxlength(11);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
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
		getUserWorkspace().alocateAuthorities("CustomerPhoneNumberDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnSave"));
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
	public void onClose$window_CustomerPhoneNumberDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerPhoneNumberDialog);
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
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		if (close) {
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
			closePopUpWindow(this.window_CustomerPhoneNumberDialog,"CustomerPhoneNumberDialog");
		}else{
			closeDialog(this.window_CustomerPhoneNumberDialog, "CustomerPhoneNumberDialog");
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
	 * @param aCustomerPhoneNumber
	 *            CustomerPhoneNumber
	 */
	public void doWriteBeanToComponents(CustomerPhoneNumber aCustomerPhoneNumber) {
		logger.debug("Entering");

		if(aCustomerPhoneNumber.getPhoneCustID()!=Long.MIN_VALUE){
			this.phoneCustID.setValue(aCustomerPhoneNumber.getPhoneCustID());
		}

		this.phoneTypeCode.setValue(aCustomerPhoneNumber.getPhoneTypeCode());
		this.phoneCountryCode.setValue(aCustomerPhoneNumber.getPhoneCountryCode());
		this.phoneAreaCode.setValue(aCustomerPhoneNumber.getPhoneAreaCode());
		this.phoneNumber.setValue(aCustomerPhoneNumber.getPhoneNumber());
		this.custCIF.setValue(aCustomerPhoneNumber.getLovDescCustCIF()==null?"":aCustomerPhoneNumber.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerPhoneNumber.getLovDescCustShrtName()==null?"":aCustomerPhoneNumber.getLovDescCustShrtName().trim());

		if (isNewRecord()){
			this.phoneTypeCode.setDescription("");
			this.phoneCountryCode.setDescription("");
		}else{
			this.phoneTypeCode.setDescription(aCustomerPhoneNumber.getLovDescPhoneTypeCodeName());
			this.phoneCountryCode.setDescription(aCustomerPhoneNumber.getLovDescPhoneCountryName());
		}

		this.recordStatus.setValue(aCustomerPhoneNumber.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerPhoneNumber
	 */
	public void doWriteComponentsToBean(CustomerPhoneNumber aCustomerPhoneNumber) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerPhoneNumber.setPhoneCustID(this.phoneCustID.longValue());
			aCustomerPhoneNumber.setLovDescCustCIF(this.custCIF.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerPhoneNumber.setLovDescPhoneTypeCodeName(this.phoneTypeCode.getDescription());
			aCustomerPhoneNumber.setPhoneTypeCode(this.phoneTypeCode.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
//		try {
//			aCustomerPhoneNumber.setLovDescPhoneCountryName(this.lovDescPhoneCountryName.getValue());
//			aCustomerPhoneNumber.setLovDescPhoneCountryName(this.phoneCountryDesc.getValue());
//			aCustomerPhoneNumber.setPhoneCountryCode(this.phoneCountryCode.getValue());
//		}catch (WrongValueException we ) {
//			wve.add(we);
//		}
//		try {
//			aCustomerPhoneNumber.setPhoneAreaCode(this.phoneAreaCode.getValue());
//		}catch (WrongValueException we ) {
//			wve.add(we);
//		}
		try {
			aCustomerPhoneNumber.setPhoneNumber(this.phoneNumber.getValue());
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

		aCustomerPhoneNumber.setRecordStatus(this.recordStatus.getValue());
		setCustomerPhoneNumber(aCustomerPhoneNumber);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerPhoneNumber
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerPhoneNumber aCustomerPhoneNumber) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.phoneTypeCode.focus();
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
			doWriteBeanToComponents(aCustomerPhoneNumber);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
            doCheckEnquiry();
			if(isNewCustomer()){
				this.window_CustomerPhoneNumberDialog.setHeight("30%");
				this.window_CustomerPhoneNumberDialog.setWidth("70%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerPhoneNumberDialog.doModal() ;
			}else{
				this.window_CustomerPhoneNumberDialog.setWidth("100%");
				this.window_CustomerPhoneNumberDialog.setHeight("100%");
				setDialog(this.window_CustomerPhoneNumberDialog);
			}

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if("ENQ".equals(this.moduleType)){
			this.phoneNumber.setReadonly(true);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_phoneCustID = this.phoneCustID.longValue();
		this.oldVar_phoneTypeCode = this.phoneTypeCode.getValue();
		this.oldVar_lovDescPhoneTypeCodeName = this.phoneTypeCode.getDescription();
		this.oldVar_phoneCountryCode = this.phoneCountryCode.getValue();
		this.oldVar_lovDescPhoneCountryName =  this.phoneCountryCode.getDescription();
		this.oldVar_phoneAreaCode = this.phoneAreaCode.getValue();
		this.oldVar_phoneNumber = this.phoneNumber.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.phoneCustID.setValue(this.oldVar_phoneCustID);
		this.phoneTypeCode.setValue(this.oldVar_phoneTypeCode);
		this.phoneTypeCode.setDescription(this.oldVar_lovDescPhoneTypeCodeName);
		this.phoneCountryCode.setDescription(this.oldVar_lovDescPhoneCountryName);
		this.phoneCountryCode.setValue(this.oldVar_phoneCountryCode);
		this.phoneAreaCode.setValue(this.oldVar_phoneAreaCode);
		this.phoneNumber.setValue(this.oldVar_phoneNumber);
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
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_phoneCustID != this.phoneCustID.longValue()) {
			return true;
		}
		if (this.oldVar_phoneTypeCode != this.phoneTypeCode.getValue()) {
			return true;
		}
		if (this.oldVar_phoneCountryCode != this.phoneCountryCode.getValue()) {
			return true;
		}
		if (this.oldVar_phoneAreaCode != this.phoneAreaCode.getValue()) {
			return true;
		}
		if (this.oldVar_phoneNumber != this.phoneNumber.getValue()) {
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

		if (!this.phoneCustID.isReadonly()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneCustID.value"),null,true));
		}	
		if (!this.phoneAreaCode.isReadonly()){
			this.phoneAreaCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneAreaCode.value"),null,true));
		}	
		if (!this.phoneNumber.isReadonly()){
			/*this.phoneNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value"),null,true));*/
			this.phoneNumber.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value"),true));
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
		this.phoneCountryCode.setConstraint("");
		this.phoneAreaCode.setConstraint("");
		this.phoneNumber.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.phoneTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneTypeCode.value"),null,true,true));

		this.phoneCountryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneCountryCode.value"),null,true,true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.phoneTypeCode.setConstraint("");
		this.phoneCountryCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.phoneCountryCode.setErrorMessage("");
		this.phoneAreaCode.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.phoneTypeCode.setErrorMessage("");
		this.phoneCountryCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updating
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<CustomerPhoneNumber> soAcademic = getCustomerPhoneNumberListCtrl().getSearchObj();
		getCustomerPhoneNumberListCtrl().pagingCustomerPhoneNumberList.setActivePage(0);
		getCustomerPhoneNumberListCtrl().getPagedListWrapper().setSearchObject(soAcademic);
		if (getCustomerPhoneNumberListCtrl().listBoxCustomerPhoneNumber != null) {
			getCustomerPhoneNumberListCtrl().listBoxCustomerPhoneNumber.getListModel();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerPhoneNumber object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerPhoneNumber aCustomerPhoneNumber = new CustomerPhoneNumber();
		BeanUtils.copyProperties(getCustomerPhoneNumber(), aCustomerPhoneNumber);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> " + aCustomerPhoneNumber.getPhoneCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerPhoneNumber.getRecordType()).equals("")){
				aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion()+1);
				aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerPhoneNumber.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()){
					aCustomerPhoneNumber.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isFinanceCustomer){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerPhoneNumber, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPhoneNumberDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getFinanceCustomerListCtrl().doFillCustomerPhoneNumberDetails(this.customerPhoneNumbers);
						this.window_CustomerPhoneNumberDialog.onClose();
					}
				}else{
					if(isNewCustomer()){
						tranType=PennantConstants.TRAN_DEL;
						AuditHeader auditHeader =  newCusomerProcess(aCustomerPhoneNumber,tranType);
						auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPhoneNumberDialog,
								auditHeader);
						int retValue = auditHeader.getProcessStatus();
						if (retValue==PennantConstants.porcessCONTINUE || 
								retValue==PennantConstants.porcessOVERIDE){
							getCustomerDialogCtrl().doFillCustomerPhoneNumbers(this.customerPhoneNumbers);
							//true;
							// send the data back to customer
							closeWindow();
						}	

					}else if(doProcess(aCustomerPhoneNumber,tranType)){
						refreshList();
						closeWindow();
					}
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerPhoneNumber object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old vars
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerPhoneNumber() in the frontEnd.
		// we get it from the backEnd.
		final CustomerPhoneNumber aCustomerPhoneNumber = getCustomerPhoneNumberService().getNewCustomerPhoneNumber();
		aCustomerPhoneNumber.setNewRecord(true);
		setCustomerPhoneNumber(aCustomerPhoneNumber);
		doClear(); // clear all components
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
			this.phoneTypeCode.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneTypeCode"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.phoneTypeCode.setReadonly(true);
		}
		this.phoneCustID.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneCustID"));
		this.custCIF.setReadonly(true);
		this.phoneCountryCode.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneCountryCode"));
		this.phoneCountryCode.setMandatoryStyle(!isReadOnly("CustomerPhoneNumberDialog_phoneCountryCode"));
		this.phoneAreaCode.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneAreaCode"));
		this.phoneNumber.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneNumber"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerPhoneNumber.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
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
		this.phoneCustID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.phoneTypeCode.setReadonly(true);
		this.phoneCountryCode.setReadonly(true);
		this.phoneAreaCode.setReadonly(true);
		this.phoneNumber.setReadonly(true);

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
		this.custCIF.setValue("");
		this.custShrtName.setValue("");
		this.phoneTypeCode.setValue("");
		this.phoneTypeCode.setDescription("");
		this.phoneCountryCode.setDescription("");
		this.phoneCountryCode.setValue("");
		this.phoneAreaCode.setValue("");
		this.phoneNumber.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CustomerPhoneNumber aCustomerPhoneNumber = new CustomerPhoneNumber();
		BeanUtils.copyProperties(getCustomerPhoneNumber(), aCustomerPhoneNumber);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerPhoneNumber object with the components data
		doWriteComponentsToBean(aCustomerPhoneNumber);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerPhoneNumber.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerPhoneNumber.getRecordType()).equals("")){
				aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion()+1);
				if(isNew){
					aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerPhoneNumber.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerPhoneNumber.setVersion(1);
					aCustomerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerPhoneNumber.getRecordType()).equals("")){
					aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion()+1);
					aCustomerPhoneNumber.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if(isFinanceCustomer){
				AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerPhoneNumber, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPhoneNumberDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinanceCustomerListCtrl().doFillCustomerPhoneNumberDetails(this.customerPhoneNumbers);
					this.window_CustomerPhoneNumberDialog.onClose();
				}
			}else{
				if(isNewCustomer()){
					AuditHeader auditHeader =  newCusomerProcess(aCustomerPhoneNumber,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPhoneNumberDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerPhoneNumbers(this.customerPhoneNumbers);
						// send the data back to customer
						closeWindow();
					}
				}else if(doProcess(aCustomerPhoneNumber,tranType)){
					refreshList();
					// Close the Existing Dialog
					closeWindow();
				}
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Creating list of Details
	 */
	private AuditHeader newCusomerProcess(CustomerPhoneNumber aCustomerPhoneNumber,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aCustomerPhoneNumber, tranType);
		customerPhoneNumbers = new ArrayList<CustomerPhoneNumber>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerPhoneNumber.getPhoneCustID());
		valueParm[1] = aCustomerPhoneNumber.getPhoneTypeCode();

		errParm[0] = PennantJavaUtil.getLabel("label_PhoneCustID")+ ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_PhoneTypeCode")+ ":"+ valueParm[1];

		if(getCustomerDialogCtrl().getPhoneNumberList()!=null && getCustomerDialogCtrl().getPhoneNumberList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getPhoneNumberList().size(); i++) {
				CustomerPhoneNumber customerPhoneNumber = getCustomerDialogCtrl().getPhoneNumberList().get(i);

				if(customerPhoneNumber.getPhoneTypeCode().equals(aCustomerPhoneNumber.getPhoneTypeCode())){ // Both Current and Existing list PhoneNumber same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerPhoneNumbers.add(aCustomerPhoneNumber);
						}else if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerPhoneNumbers.add(aCustomerPhoneNumber);
						}else if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerPhoneNumList().size(); j++) {
								CustomerPhoneNumber phoneNumber =  getCustomerDialogCtrl().getCustomerDetails().getCustomerPhoneNumList().get(j);
								if(phoneNumber.getPhoneCustID() == aCustomerPhoneNumber.getPhoneCustID() && phoneNumber.getPhoneTypeCode().equals(aCustomerPhoneNumber.getPhoneTypeCode())){
									customerPhoneNumbers.add(phoneNumber);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							customerPhoneNumbers.add(customerPhoneNumber);
						}
					}
				}else{
					customerPhoneNumbers.add(customerPhoneNumber);
				}
			}
		}

		if(!recordAdded){
			customerPhoneNumbers.add(aCustomerPhoneNumber);
		}
		return auditHeader;
	} 
	/**
	 * Method for Creating list of Details
	 */
	private AuditHeader newFinanceCustomerProcess(CustomerPhoneNumber aCustomerPhoneNumber,String tranType){
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aCustomerPhoneNumber, tranType);
		customerPhoneNumbers = new ArrayList<CustomerPhoneNumber>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = String.valueOf(aCustomerPhoneNumber.getPhoneCustID());
		valueParm[1] = aCustomerPhoneNumber.getPhoneTypeCode();
		
		errParm[0] = PennantJavaUtil.getLabel("label_PhoneCustID")+ ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_PhoneTypeCode")+ ":"+ valueParm[1];
		
		if(getFinanceCustomerListCtrl().getCustomerPhoneNumberDetailList()!=null && getFinanceCustomerListCtrl().getCustomerPhoneNumberDetailList().size()>0){
			for (int i = 0; i < getFinanceCustomerListCtrl().getCustomerPhoneNumberDetailList().size(); i++) {
				CustomerPhoneNumber customerPhoneNumber = getFinanceCustomerListCtrl().getCustomerPhoneNumberDetailList().get(i);
				
				if(customerPhoneNumber.getPhoneTypeCode().equals(aCustomerPhoneNumber.getPhoneTypeCode())){ // Both Current and Existing list PhoneNumber same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerPhoneNumbers.add(aCustomerPhoneNumber);
						}else if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerPhoneNumbers.add(aCustomerPhoneNumber);
						}else if(aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getFinanceCustomerListCtrl().getCustomerDetails().getCustomerPhoneNumList().size(); j++) {
								CustomerPhoneNumber phoneNumber =  getFinanceCustomerListCtrl().getCustomerDetails().getCustomerPhoneNumList().get(j);
								if(phoneNumber.getPhoneCustID() == aCustomerPhoneNumber.getPhoneCustID() && phoneNumber.getPhoneTypeCode().equals(aCustomerPhoneNumber.getPhoneTypeCode())){
									customerPhoneNumbers.add(phoneNumber);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							customerPhoneNumbers.add(customerPhoneNumber);
						}
					}
				}else{
					customerPhoneNumbers.add(customerPhoneNumber);
				}
			}
		}
		
		if(!recordAdded){
			customerPhoneNumbers.add(aCustomerPhoneNumber);
		}
		return auditHeader;
	} 

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerPhoneNumber
	 *            (CustomerPhoneNumber)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerPhoneNumber aCustomerPhoneNumber,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCustomerPhoneNumber.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerPhoneNumber.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerPhoneNumber.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerPhoneNumber.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerPhoneNumber.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerPhoneNumber);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCustomerPhoneNumber))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
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

			aCustomerPhoneNumber.setTaskId(taskId);
			aCustomerPhoneNumber.setNextTaskId(nextTaskId);
			aCustomerPhoneNumber.setRoleCode(getRole());
			aCustomerPhoneNumber.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCustomerPhoneNumber, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerPhoneNumber);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerPhoneNumber, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerPhoneNumber, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving");
		return processCompleted;
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
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		CustomerPhoneNumber aCustomerPhoneNumber = (CustomerPhoneNumber) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCustomerPhoneNumberService().delete(auditHeader);
						deleteNotes = true;
					}else{
						auditHeader = getCustomerPhoneNumberService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCustomerPhoneNumberService().doApprove(auditHeader);
						if (aCustomerPhoneNumber.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCustomerPhoneNumberService().doReject(auditHeader);
						if (aCustomerPhoneNumber.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_CustomerPhoneNumberDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPhoneNumberDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onFulfill$phoneTypeCode(Event event){
		logger.debug("Entering" + event.toString());

		Object dataObject = phoneTypeCode.getObject();
		if (dataObject instanceof String){
			this.phoneTypeCode.setValue(dataObject.toString());
			this.phoneTypeCode.setDescription("");
		}else{
			PhoneType details= (PhoneType) dataObject;
			if (details != null) {
				this.phoneTypeCode.setValue(details.getPhoneTypeCode());
				this.phoneTypeCode.setDescription(details.getPhoneTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$phoneCountryCode(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = phoneCountryCode.getObject();
		if (dataObject instanceof String) {
			this.phoneCountryCode.setValue(dataObject.toString());
			this.phoneCountryCode.setDescription("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.phoneCountryCode.setValue(details.getCountryCode());
				this.phoneCountryCode.setDescription(details.getCountryDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
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

	// To load the customer filter dialog
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",	null, map);
		logger.debug("Leaving");
	}

	//To set the customer id from Customer filter
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering");
		final Customer aCustomer = (Customer)nCustomer; 
		this.phoneCustID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
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
	 * @param aCustomerIdentity
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerPhoneNumber aCustomerPhoneNumber, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerPhoneNumber.getBefImage(), aCustomerPhoneNumber);

		return new AuditHeader(getReference(),String.valueOf(aCustomerPhoneNumber.getPhoneCustID()), null,
				null, auditDetail, aCustomerPhoneNumber.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerPhoneNumberDialog, auditHeader);
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

		final HashMap<String, Object> map = new HashMap<String, Object>();

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
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerPhoneNumber");
		notes.setReference(getReference());
		notes.setVersion(getCustomerPhoneNumber().getVersion());
		logger.debug("Leaving");
		return notes;
	}
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getCustomerPhoneNumber().getPhoneCustID()+PennantConstants.KEY_SEPERATOR+
		getCustomerPhoneNumber().getPhoneTypeCode();
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

	public CustomerPhoneNumber getCustomerPhoneNumber() {
		return this.customerPhoneNumber;
	}
	public void setCustomerPhoneNumber(CustomerPhoneNumber customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}

	public void setCustomerPhoneNumberService(CustomerPhoneNumberService customerPhoneNumberService) {
		this.customerPhoneNumberService = customerPhoneNumberService;
	}
	public CustomerPhoneNumberService getCustomerPhoneNumberService() {
		return this.customerPhoneNumberService;
	}

	public void setCustomerPhoneNumberListCtrl(CustomerPhoneNumberListCtrl customerPhoneNumberListCtrl) {
		this.customerPhoneNumberListCtrl = customerPhoneNumberListCtrl;
	}
	public CustomerPhoneNumberListCtrl getCustomerPhoneNumberListCtrl() {
		return this.customerPhoneNumberListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}
	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}
	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setCustomerPhoneNumbers(List<CustomerPhoneNumber> customerPhoneNumbers) {
		this.customerPhoneNumbers = customerPhoneNumbers;
	}
	public List<CustomerPhoneNumber> getCustomerPhoneNumbers() {
		return customerPhoneNumbers;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}
	
	private String getLovDescription(String value) {
		value = StringUtils.trimToEmpty(value);

		try {
			value = StringUtils.split(value, "-", 2)[1];
		} catch (Exception e) {
			//
		}

		return value;
	}

	public FinanceCustomerListCtrl getFinanceCustomerListCtrl() {
		return financeCustomerListCtrl;
	}
	public void setFinanceCustomerListCtrl(
			FinanceCustomerListCtrl financeCustomerListCtrl) {
		this.financeCustomerListCtrl = financeCustomerListCtrl;
	}

}
