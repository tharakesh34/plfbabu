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
 * FileName    		:  CustomerIncomeDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerincome;

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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
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
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerIncome/customerIncomeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerIncomeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 7152044545249791558L;
	private final static Logger logger = Logger.getLogger(CustomerIncomeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerIncomeDialog; 		// autoWired

	protected Longbox 	 custID; 						// autoWired
	protected ExtendedCombobox 	 custIncomeType; 		// autoWired
	protected Decimalbox custIncome; 					// autoWired
	protected Textbox 	 custCIF;						// autoWired
	protected Label   	 custShrtName;					// autoWired
	protected Checkbox   jointCust;

	protected Row   	 row_isJoint;					// autoWired

	protected Label 	 recordStatus; 					// autoWired
	protected Radiogroup userAction;
	protected Groupbox   groupboxWf;
	protected South		 south;

	// not auto wired variables
	private CustomerIncome customerIncome; // overHanded per parameter
	private transient CustomerIncomeListCtrl customerIncomeListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initial.
	private transient long  		oldVar_custID;
	private transient String  		oldVar_custIncomeType;
	private transient BigDecimal  	oldVar_custIncome;
	private transient boolean  	    oldVar_jointCust;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerIncomeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;    	// autoWire
	protected Button btnEdit;  		// autoWire
	protected Button btnDelete;		// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel;		// autoWire
	protected Button btnClose;		// autoWire
	protected Button btnHelp;		// autoWire
	protected Button btnNotes;		// autoWire
	protected Button btnSearchPRCustid; // autoWire

	private transient String 		oldVar_lovDescCustIncomeTypeName;


	// ServiceDAOs / Domain Classes
	private transient CustomerIncomeService customerIncomeService;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerIncome> customerIncomes;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private int ccyFormatter = 0;
	private String moduleType="";
	private boolean custIsJointCust = false;
	private String userRole="";

	/**
	 * default constructor.<br>
	 */
	public CustomerIncomeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerIncome object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerIncomeDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());


		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("customerIncome")) {
			this.customerIncome = (CustomerIncome) args.get("customerIncome");
			CustomerIncome befImage = new CustomerIncome();
			BeanUtils.copyProperties(this.customerIncome, befImage);
			this.customerIncome.setBefImage(befImage);
			setCustomerIncome(this.customerIncome);
		} else {
			setCustomerIncome(null);
		}

		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}
		if (args.containsKey("jointCust")) {
			this.custIsJointCust = (Boolean) args.get("jointCust");
		}
		
		if(getCustomerIncome().isNewRecord()){
			setNewRecord(true);
		}

		if(args.containsKey("customerDialogCtrl")){

			setCustomerDialogCtrl((CustomerDialogCtrl) args.get("customerDialogCtrl"));
			setNewCustomer(true);
			
			if(args.containsKey("ccyFormatter")){
				ccyFormatter =  (Integer) args.get("ccyFormatter");;
			}

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerIncome.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerIncomeDialog");
			}
		}else{
			ccyFormatter = customerIncome.getLovDescCcyEditField();
		}

		doLoadWorkFlow(this.customerIncome.isWorkflow(),
				this.customerIncome.getWorkflowId(), this.customerIncome.getNextTaskId());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"CustomerIncomeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the customerIncomeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerIncome here.
		if (args.containsKey("customerIncomeListCtrl")) {
			setCustomerIncomeListCtrl((CustomerIncomeListCtrl) args.get("customerIncomeListCtrl"));
		} else {
			setCustomerIncomeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerIncome());

		//Calling SelectCtrl For proper selection of Customer
		if (isNewRecord() & !isNewCustomer()) {
			onload();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custIncomeType.setMaxlength(8);
		this.custIncomeType.setMandatoryStyle(true);
		this.custIncomeType.getTextbox().setWidth("110px");
		this.custIncomeType.setModuleName("IncomeExpense");
		this.custIncomeType.setValueColumn("IncomeExpense");
		this.custIncomeType.setDescColumn("IncomeTypeDesc");
		this.custIncomeType.setValidateColumns(new String[] { "IncomeExpense" });
		
		this.custIncome.setMaxlength(18);
		this.custIncome.setFormat(PennantAppUtil.getAmountFormate(ccyFormatter));

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
		getUserWorkspace().alocateAuthorities("CustomerIncomeDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerIncomeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerIncomeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerIncomeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerIncomeDialog_btnSave"));
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
	public void onClose$window_CustomerIncomeDialog(Event event)throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerIncomeDialog);
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
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
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
			window_CustomerIncomeDialog.onClose();	
		}else{
			closeDialog(this.window_CustomerIncomeDialog, "CustomerIncome");
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
	 * @param aCustomerIncome
	 *            CustomerIncome
	 */
	public void doWriteBeanToComponents(CustomerIncome aCustomerIncome) {
		logger.debug("Entering");

		if(aCustomerIncome.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerIncome.getCustID());	
		}

		this.custIncomeType.setValue(aCustomerIncome.getCustIncomeType()==null?"":aCustomerIncome.getCustIncomeType());
		this.custIncome.setValue(PennantAppUtil.formateAmount(aCustomerIncome.getCustIncome(), ccyFormatter));
		this.custCIF.setValue(aCustomerIncome.getLovDescCustCIF()==null?"":aCustomerIncome.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerIncome.getLovDescCustShrtName()==null?"":aCustomerIncome.getLovDescCustShrtName().trim());
		this.jointCust.setChecked(aCustomerIncome.isJointCust());

		if (isNewRecord()) {
			this.custIncomeType.setDescription("");
		} else {
			this.custIncomeType.setDescription(aCustomerIncome.getLovDescCustIncomeTypeName());
		}
		this.recordStatus.setValue(aCustomerIncome.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerIncome
	 */
	public void doWriteComponentsToBean(CustomerIncome aCustomerIncome) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerIncome.setCustID(this.custID.getValue());	
			aCustomerIncome.setLovDescCustCIF(this.custCIF.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			this.custIncomeType.getDescription();
			aCustomerIncome.setCustIncomeType(this.custIncomeType.getValue().trim());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custIncome.getValue() != null) {
				aCustomerIncome.setCustIncome(PennantAppUtil.unFormateAmount(this.custIncome.getValue(), ccyFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aCustomerIncome.setJointCust(this.jointCust.isChecked());
//		try {
//			aCustomerIncome.setLovDescCustIncomeCountryName(this.lovDescCustIncomeCountryName.getValue());
//			aCustomerIncome.setCustIncomeCountry(this.custIncomeCountry.getValue());
//		} catch (WrongValueException we) {
//			wve.add(we);
//		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCustomerIncome.setRecordStatus(this.recordStatus.getValue());
		setCustomerIncome(aCustomerIncome);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerIncome
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerIncome aCustomerIncome)throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custIncome.focus();
			if (isNewCustomer()){
				doEdit();
			}else	if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerIncome);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			
			if(!custIsJointCust){
				this.row_isJoint.setVisible(false);
			}
			
			if(isNewCustomer()){
				this.window_CustomerIncomeDialog.setHeight("228px");
				this.window_CustomerIncomeDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_CustomerIncomeDialog.doModal() ;
			}else{
				this.window_CustomerIncomeDialog.setWidth("100%");
				this.window_CustomerIncomeDialog.setHeight("100%");
				setDialog(this.window_CustomerIncomeDialog);
			}			
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
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
		this.oldVar_custIncomeType = this.custIncomeType.getValue();
		this.oldVar_lovDescCustIncomeTypeName = this.custIncomeType.getDescription();
		this.oldVar_custIncome = this.custIncome.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_jointCust = this.jointCust.isChecked();
		
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custID.setValue(this.oldVar_custID);
		this.custIncomeType.setValue(this.oldVar_custIncomeType);
		this.custIncomeType.setDescription(this.oldVar_lovDescCustIncomeTypeName);
		this.custIncome.setValue(this.oldVar_custIncome);
		this.jointCust.setChecked(this.oldVar_jointCust);
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
		if (this.oldVar_custIncomeType != this.custIncomeType.getValue()) {
			return true;
		}
		if (this.oldVar_custIncome != this.custIncome.getValue()) {
			return true;
		}
		if (this.oldVar_jointCust != this.jointCust.isChecked()) {
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

		if (this.btnSearchPRCustid.isVisible()){
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_CustomerIncomeDialog_CustID.value")}));
		}

		if (!this.custIncome.isReadonly()) {
			this.custIncome.setConstraint(new AmountValidator(18, 0, 
					Labels.getLabel("label_CustomerIncomeDialog_CustIncome.value")));
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
		this.custIncome.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custIncomeType.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerIncomeDialog_CustIncomeType.value") }));

		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custIncomeType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custIncome.setErrorMessage("");
		this.custIncomeType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updating
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<CustomerIncome> soCustomerIncome = getCustomerIncomeListCtrl().getSearchObj();
		getCustomerIncomeListCtrl().pagingCustomerIncomeList.setActivePage(0);
		getCustomerIncomeListCtrl().getPagedListWrapper().setSearchObject(soCustomerIncome);
		if (getCustomerIncomeListCtrl().listBoxCustomerIncome != null) {
			getCustomerIncomeListCtrl().listBoxCustomerIncome.getListModel();
		}
		logger.debug("Leaving");
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerIncome object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerIncome aCustomerIncome = new CustomerIncome();
		BeanUtils.copyProperties(getCustomerIncome(), aCustomerIncome);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aCustomerIncome.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerIncome.getRecordType()).equals("")) {
				aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
				aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerIncome.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()) {
					aCustomerIncome.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCustomerProcess(aCustomerIncome,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerIncomeDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerIncome(this.customerIncomes);
						// send the data back to customer
						closeWindow();
					}	
				}else if (doProcess(aCustomerIncome, tranType)) {
					refreshList();
					closeWindow();
				}	
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerIncome object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerIncome() in the frontEnd.
		// we get it from the backEnd.
		final CustomerIncome aCustomerIncome = getCustomerIncomeService().getNewCustomerIncome();
		aCustomerIncome.setNewRecord(true);
		setCustomerIncome(aCustomerIncome);
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

		if(isNewRecord()){

			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
			this.custIncomeType.setReadonly(isReadOnly("CustomerIncomeDialog_custIncomeType"));
			this.jointCust.setDisabled(isReadOnly("CustomerIncomeDialog_custIncomeType"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custIncomeType.setReadonly(true);
			this.jointCust.setDisabled(true);
		}

		this.custCIF.setReadonly(true);
		this.custID.setReadonly(isReadOnly("CustomerIncomeDialog_custID"));
		this.custIncome.setReadonly(isReadOnly("CustomerIncomeDialog_custIncome"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerIncome.isNewRecord()) {
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
			}else {
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

		this.custCIF.setReadonly(true);
		this.custIncomeType.setReadonly(true);
		this.custIncome.setReadonly(true);
		this.jointCust.setDisabled(true);

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
		this.custShrtName.setValue("");
		this.custIncomeType.setValue("");
		this.custIncomeType.setDescription("");
		this.custIncome.setValue("");
		this.jointCust.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerIncome aCustomerIncome = new CustomerIncome();
		BeanUtils.copyProperties(getCustomerIncome(), aCustomerIncome);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerIncome object with the components data
		doWriteComponentsToBean(aCustomerIncome);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerIncome.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerIncome.getRecordType()).equals("")) {
				aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
				if (isNew) {
					aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerIncome.setNewRecord(true);
				}
			}
		} else {

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerIncome.setVersion(1);
					aCustomerIncome.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerIncome.getRecordType()).equals("")){
					aCustomerIncome.setVersion(aCustomerIncome.getVersion()+1);
					aCustomerIncome.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerIncome.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
			}else{
				aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}			
		}

		// save it to database
		try {
			if(isNewCustomer()){
				AuditHeader auditHeader =  newCustomerProcess(aCustomerIncome,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerIncomeDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerIncome(this.customerIncomes);
					// send the data back to customer
					closeWindow();
				}
			}else if (doProcess(aCustomerIncome, tranType)) {
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

	private AuditHeader newCustomerProcess(CustomerIncome aCustomerIncome,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aCustomerIncome, tranType);
		customerIncomes = new ArrayList<CustomerIncome>();

		String[] valueParm = new String[4];
		String[] errParm = new String[4];

		valueParm[0] = String.valueOf(aCustomerIncome.getId());
		valueParm[1] = aCustomerIncome.getCustIncomeType();
		valueParm[2] = String.valueOf(aCustomerIncome.isJointCust());
		valueParm[3] = aCustomerIncome.getCategory();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustIncomeType") + ":"+valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_JointCust") + ":"+valueParm[3];
		errParm[3] = PennantJavaUtil.getLabel("label_CustIncomeCountry") + ":"+valueParm[2];

		if(getCustomerDialogCtrl().getIncomeList()!=null && getCustomerDialogCtrl().getIncomeList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getIncomeList().size(); i++) {
				CustomerIncome customerIncome = getCustomerDialogCtrl().getIncomeList().get(i);

				if(aCustomerIncome.getCustIncomeType().equals(customerIncome.getCustIncomeType()) && 
						(aCustomerIncome.getCategory().equals(customerIncome.getCategory()))&& 
						(aCustomerIncome.isJointCust() == customerIncome.isJointCust())&& 
						(aCustomerIncome.getIncomeExpense().equals(customerIncome.getIncomeExpense()))){ // Both Current and Existing list rating same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41008",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerIncomes.add(aCustomerIncome);
						}else if(aCustomerIncome.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerIncomes.add(aCustomerIncome);
						}else if(aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerIncomeList().size(); j++) {
								CustomerIncome income =  getCustomerDialogCtrl().getCustomerDetails().getCustomerIncomeList().get(j);
								if(income.getCustID() == aCustomerIncome.getCustID() && income.getCustIncomeType().equals(aCustomerIncome.getCustIncomeType()) && income.getCategory().equals(aCustomerIncome.getCategory()) && income.getIncomeExpense().equals(aCustomerIncome.getIncomeExpense())){
									customerIncomes.add(income);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							customerIncomes.add(customerIncome);
						}
					}
				}else{
					customerIncomes.add(customerIncome);
				}
			}
		}

		if(!recordAdded){
			customerIncomes.add(aCustomerIncome);
		}
		return auditHeader;
	} 
	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerIncome
	 *            (CustomerIncome)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerIncome aCustomerIncome, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerIncome.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerIncome.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerIncome.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerIncome.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,aCustomerIncome);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCustomerIncome))) {
					try {
						if (!isNotes_Entered()) {
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
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aCustomerIncome.setTaskId(taskId);
			aCustomerIncome.setNextTaskId(nextTaskId);
			aCustomerIncome.setRoleCode(getRole());
			aCustomerIncome.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerIncome, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerIncome);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerIncome,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}						
				}
			}
		} else {

			auditHeader = getAuditHeader(aCustomerIncome, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
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
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerIncome aCustomerIncome = (CustomerIncome) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerIncomeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerIncomeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerIncomeService().doApprove(auditHeader);

						if (aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerIncomeService().doReject(auditHeader);
						if (aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerIncomeDialog, auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_CustomerIncomeDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
					}
				}
				setOverideMap(auditHeader.getOverideMap());
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
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
	
	public void onFulfill$custIncomeType(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = custIncomeType.getObject();
		if (dataObject instanceof String) {
			this.custIncomeType.setValue(dataObject.toString());
			this.custIncomeType.setDescription("");
		} else {
			IncomeType details = (IncomeType) dataObject;
			if (details != null) {
				this.custIncomeType.setValue(details.getIncomeTypeCode().trim());
				this.custIncomeType.setDescription(details.getIncomeTypeDesc());
				getCustomerIncome().setIncomeExpense(details.getIncomeExpense().trim());
				getCustomerIncome().setLovDescCustIncomeTypeName(details.getIncomeTypeDesc());
				getCustomerIncome().setCategory(details.getCategory().trim());
				getCustomerIncome().setLovDescCategoryName(details.getLovDescCategoryName());
				getCustomerIncome().setMargin(details.getMargin());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event)throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException,InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		List<Filter> filtersList=new ArrayList<Filter>();
		Filter filter=new Filter("lovDescCustCtgType", "I", Filter.OP_EQUAL);
		filtersList.add(filter);
		map.put("DialogCtrl", this);
		map.put("filtersList", filtersList);
		map.put("filtertype", "Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @param newSearchObject 
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
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
	 * @param aCustomerIncome
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerIncome aCustomerIncome,String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerIncome.getBefImage(), aCustomerIncome);

		return new AuditHeader(getReference(), String.valueOf(aCustomerIncome.getCustID()),
				null, null, auditDetail, aCustomerIncome.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerIncomeDialog,auditHeader);
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
		notes.setModuleName("CustomerIncome");
		notes.setReference(getReference());
		notes.setVersion(getCustomerIncome().getVersion());
		return notes;
	}
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getCustomerIncome().getCustID()
		+ PennantConstants.KEY_SEPERATOR + getCustomerIncome().getCustIncomeType()
		+ PennantConstants.KEY_SEPERATOR + getCustomerIncome().getCategory()
		+ PennantConstants.KEY_SEPERATOR + getCustomerIncome().getIncomeExpense();
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

	public CustomerIncome getCustomerIncome() {
		return this.customerIncome;
	}
	public void setCustomerIncome(CustomerIncome customerIncome) {
		this.customerIncome = customerIncome;
	}

	public void setCustomerIncomeService(
			CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}
	public CustomerIncomeService getCustomerIncomeService() {
		return this.customerIncomeService;
	}

	public void setCustomerIncomeListCtrl(
			CustomerIncomeListCtrl customerIncomeListCtrl) {
		this.customerIncomeListCtrl = customerIncomeListCtrl;
	}
	public CustomerIncomeListCtrl getCustomerIncomeListCtrl() {
		return this.customerIncomeListCtrl;
	}

	public void setCustomerIncomes(List<CustomerIncome> customerIncomes) {
		this.customerIncomes = customerIncomes;
	}
	public List<CustomerIncome> getCustomerIncomes() {
		return customerIncomes;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}
	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
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
}
