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
 * FileName    		:  CustomerDialogCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ProspectCustomerDialogCtrl extends GFCBaseCtrl implements Serializable {
	
	private static final long serialVersionUID = 9031340167587772517L;
	private final static Logger logger = Logger.getLogger(ProspectCustomerDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ProspectCustomerDialog; 			// autowired
	
	protected Textbox lovDescCustCIF;							//autowired
	protected Label lovDescCustShrtName;						//autowired
	protected Textbox custCoreBank; 							// autowired

	private transient String oldVar_lovDescCustCIF;
	private transient String oldVar_custCoreBank;
	
	private Customer customer; // overhanded per param
	
	private transient boolean validationOn;
	private CustomerDetailsService customerDetailsService;
	
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	
	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox gb_Action;

	/**
	 * default constructor.<br>
	 */
	public ProspectCustomerDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProspectCustomerDialog(Event event) throws Exception {
		logger.debug("Entering");
		
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		// Set the setter objects for PagedListwrapper classes to Initialize
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true,this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
		this.btnCtrl.setInitNew();
		this.btnClose.setVisible(false);

		// set Field Properties
		doSetFieldProperties();
		doStoreInitValues();
		
		// Hegiht Setting
		doShowDialog(this.customer);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.lovDescCustCIF.setMaxlength(10);
		this.custCoreBank.setMaxlength(6);
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
		getUserWorkspace().alocateAuthorities("CustomerDialog");
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnSave"));
		this.btnCancel.setVisible(false);
		// Customer related List Buttons
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public void onClick$viewCustInfo(Event event){
		try {
			if(customer != null){
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("custid", customer.getCustID());
				map.put("finFormatter", customer.getLovDescCcyFormatter());
				map.put("finReference", null);
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul", 
						window_ProspectCustomerDialog, map);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
		}
	}
	
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CustomerDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws CustomerNotFoundException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException, ParseException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		try {
			doSave();
		} catch (CustomerNotFoundException e) {
			logger.error("Customer Not Created...");
			PTMessageUtils.showErrorMessage("Customer Not Found...");
		}
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
		PTMessageUtils.showHelpWindow(event, window_ProspectCustomerDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, ParseException, 
		CustomerNotFoundException, IllegalAccessException, InvocationTargetException {
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
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 */
	private void doClose() throws InterruptedException, ParseException, CustomerNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
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
			closeDialog(this.window_ProspectCustomerDialog, "CustomerDialog");
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer
	 *            Customer
	 */
	public void onClick$btnSearchCustCIF(Event event) {

		Filter [] filters = new Filter[1];
		filters[0] = new Filter("custCoreBank","",Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_ProspectCustomerDialog, "Customer", filters);
		if (dataObject instanceof String) {
			this.lovDescCustCIF.setValue(dataObject.toString());
			this.lovDescCustShrtName.setValue("");
		} else {
			 this.customer = (Customer) dataObject;
			if(customer != null){
				this.lovDescCustCIF.setValue(customer.getCustCIF());
				this.lovDescCustShrtName.setValue(customer.getCustShrtName());
			}
			logger.debug("Leaving");
		}
	}
	
	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void onChange$lovDescCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.lovDescCustCIF.clearErrorMessage();
		
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Filter("custCoreBank","",Filter.OP_EQUAL));		
		customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), filters);

		if (customer != null) {
			this.lovDescCustCIF.setValue(String.valueOf(customer.getCustCIF()));
			this.lovDescCustShrtName.setValue(customer.getCustShrtName());
		} else {
			this.lovDescCustShrtName.setValue("");
			this.lovDescCustCIF.setValue("");
			throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID", 
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomer
	 * @throws ParseException
	 * @throws CustomerNotFoundException 
	 */
	public void doWriteComponentsToBean(Customer aCustomer) throws ParseException, CustomerNotFoundException {
		logger.debug("Entering");
		doSetValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCustomer.setCustCIF(this.lovDescCustCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.customer!=null){
				aCustomer.setCustCoreBank(StringUtils.trimToEmpty(this.custCoreBank.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve);
		
		aCustomer.setCustID(this.customer.getCustID());
		doRemoveValidation();
		logger.debug("Leaving");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomer
	 * @throws InterruptedException
	 */
	public void doShowDialog(Customer aCustomer) throws InterruptedException {
		logger.debug("Entering");
			doStoreInitValues();
 		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		// Basic Details Tab-->1.Key Details
		this.oldVar_lovDescCustCIF = this.lovDescCustCIF.getValue();
		this.oldVar_custCoreBank = this.custCoreBank.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		// Remove Error Messages for Fields
		doClearErrorMessage();
		if (this.oldVar_lovDescCustCIF != this.lovDescCustCIF.getValue()) {
			return true;
		}
		if (this.oldVar_custCoreBank != this.custCoreBank.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
 
		doClearErrorMessage();
		this.lovDescCustCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
		this.custCoreBank.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustCoreBank.value") }));

		
/* 		if (!this.lovDescCustCIF.isReadonly()) {
			this.lovDescCustCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CustID.value"), null, true));
		}
		if (!this.custCoreBank.isReadonly()) {
			this.custCoreBank.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCoreBank.value"), null, true));
		}*/
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.lovDescCustCIF.setConstraint("");
		this.custCoreBank.setConstraint("");
		doClearErrorMessage();
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearErrorMessage() {
		logger.debug("Enterring");
		this.lovDescCustCIF.setErrorMessage("");
		this.custCoreBank.setErrorMessage("");
		logger.debug("Leaving");
	}


	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
 		this.lovDescCustCIF.setReadonly(true);
		this.custCoreBank.setReadonly(true);
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
		this.lovDescCustCIF.setValue("");
		this.lovDescCustShrtName.setValue("");
		this.custCoreBank.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void doSave() throws InterruptedException, ParseException, CustomerNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		
		Customer aCustomer= new Customer();
		Cloner cloner = new Cloner();
		aCustomer = cloner.deepClone(this.customer);
		
		//BeanUtils.copyProperties(aCustomer, this.customer);
		doWriteComponentsToBean(aCustomer);
		
		Customer coreCustomer = new Customer();
		coreCustomer.setCustCIF(aCustomer.getCustCoreBank());
		
		Customer existCustomer = getCustomerDetailsService().getCheckCustomerByCIF(this.custCoreBank.getValue());
		if(existCustomer != null){
			//Show Confirmation Meesage
			Clients.showNotification("Core Bank Customer ID already used. Cannot be used furthur.",  "warning", null, null, -1);
			this.custCoreBank.setValue("");
			return;
		}
		
		
		coreCustomer = getCustomerDetailsService().fetchCoreCustomerDetails(coreCustomer);
		
		getCustomerDetailsService().updateProspectCustomer(aCustomer);
		
		//Show Confirmation Meesage
		Clients.showNotification("Updated SuccessFully",  "info", null, null, -1);
		
		doClear();
		logger.debug("Leaving");
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

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}