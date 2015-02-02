/**
onc * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  CustomerListCtrl.java                                                   * 	  
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
import java.text.ParseException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.Interface.service.impl.PFFCustomerPreparation;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CoreCustomerSelectCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 9086034736503097868L;
	private final static Logger logger = Logger.getLogger(CoreCustomerSelectCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CoreCustomer; // autowired
	protected Textbox custCIF; // autowired
	protected Textbox custCPR; // autowired
	protected Textbox custCR1; 						// autowired
	protected Textbox custCR2; 						// autowired
	protected Hbox hboxCustCR;
	protected Label label_CustomerDialog_CustCRCPR;
	protected Borderlayout borderLayout_CoreCustomer; // autowired
	// checkRights
	protected Button btnSearchCustFetch; // autowired
	protected Button btnClose; // autowired
	protected CustomerListCtrl customerListCtrl; // autowired
	private CustomerDetailsService customerDetailsService;
	private CustomerService customerService;
	private CustomerIncomeService customerIncomeService;
	private CustomerInterfaceService customerInterfaceService;
	private PFFCustomerPreparation pffCustomerPreparation;
	private transient boolean validationOn;
	protected Radio exsiting;
	protected Radio prospect;
	protected Combobox custCtgType;
	protected Row row_custCtgType;
	protected Row row_custCRCPR;
	protected ExtendedCombobox custCountry; 					// autowired
	protected Row row_custCountry;
	protected Label label_CustomerDialog_CustCountry;
	private boolean isCountryBehrain = false;
	private String tempCountry = "";
	private boolean isCustCorp = false;
	/**
	 * default constructor.<br>
	 */
	public CoreCustomerSelectCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CoreCustomer(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED params !
		if (args.containsKey("customerListCtrl")) {
			this.customerListCtrl = (CustomerListCtrl) args.get("customerListCtrl");
		} else {
			this.customerListCtrl = null;
		}
		// set Field Properties
		doSetFieldProperties();
		
		fillComboBox(custCtgType, "", PennantStaticListUtil.getCategoryType(), "");
		this.custCIF.setFocus(true);
		this.custCPR.setMaxlength(9);
		this.custCR1.setMaxlength(5);
		this.custCR2.setMaxlength(2);
		this.window_CoreCustomer.doModal();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$custCtgType(Event event){
		logger.debug("Entering" + event.toString());
		doClearCRCPR();
		
		isCustCorp = this.custCtgType.getSelectedItem().getValue().toString().equals(PennantConstants.CUST_CAT_CORPORATE) || 
				     this.custCtgType.getSelectedItem().getValue().toString().equals(PennantConstants.CUST_CAT_BANK);
		 
		if(this.custCtgType.getSelectedItem().getValue().toString().equals("#")){
			this.label_CustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CustomerDialog_CustCRCPR.value"));
			setCPRNumberProperties(true);
		}else if(isCustCorp){
			setCPRNumberProperties(false);
		}else{
			setCPRNumberProperties(true);
		}
		logger.debug("Leaving" + event.toString());
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

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearErrorMessage();
		setValidationOn(true);
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearErrorMessage() {
		logger.debug("Enterring");
		this.custCIF.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSearchCustFetch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSetValidation();
		CustomerDetails customerDetails = null;
		// Get the data of Customer from Core Banking Customer
		try {
			this.custCIF.setConstraint("");
			this.custCIF.setErrorMessage("");
			this.custCIF.clearErrorMessage();
			boolean newRecord = false;
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());
			Customer customer = null;
			//If  customer exist is checked 
			if (this.exsiting.isChecked()){
				if (cif.equals("")) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_CustomerDialog_CoreCustID.value") }));
				}else{
					customer = getCustomerDetailsService().getCheckCustomerByCIF(cif);
				}
				if (customer == null) {
					newRecord = true;
					customerDetails = getPffCustomerPreparation().getCustomerByInterface(cif, "");
					if (customerDetails == null) {
						throw new CustomerNotFoundException();
					}
				}
			}//If  prospect customer  is checked 
			else  if (this.prospect.isChecked()) {
				newRecord = true;
				String ctgType=this.custCtgType.getSelectedItem().getValue().toString();
				
				if (StringUtils.trimToEmpty(ctgType).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.custCtgType, Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CustomerDialog_CustType.value") }));
				}
				if (StringUtils.trim(this.custCountry.getValue()).equals("")) {
					throw new WrongValueException(this.custCountry, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_CustomerDialog_CustCountry.value") }));
				}
				boolean isCustCatIndividual = ctgType.equals(PennantConstants.CUST_CAT_INDIVIDUAL);
				String custCPRCR="";
				if (isCountryBehrain){
					if(isCustCatIndividual){
						this.custCPR.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCPR.value"),
								PennantRegularExpressions.REGEX_NUMERIC_FL9, true));
					}else{
						this.custCR1.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCR.value"),PennantRegularExpressions.REGEX_CR1, true));
						this.custCR2.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCR.value"),PennantRegularExpressions.REGEX_CR2, true));
					}
				}else{
					if(isCustCatIndividual){
						this.custCPR.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCPR.value"),null,true));
					}else{
						this.custCPR.setConstraint(new PTStringValidator( Labels.getLabel("label_CustomerDialog_CustCR.value"),null,true));
					}
				}
				if (isCustCatIndividual || !isCountryBehrain) {
					custCPRCR = StringUtils.trimToEmpty(this.custCPR.getValue());
				}else{
					custCPRCR = StringUtils.trimToEmpty(this.custCR1.getValue().toString()+"-"+this.custCR2.getValue().toString());
				}
				
				String custCIF = getCustomerService().getCustomerByCRCPR(custCPRCR, "_View");
				if(custCIF != null){
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_CustomerDialog_ProspectExist", new String[]{custCIF}));
					return;
				}

				customerDetails = getCustomerDetailsService().getNewCustomer(true);
				customerDetails.getCustomer().setLovDescCustCtgType(ctgType);
				customerDetails.getCustomer().setCustCtgCode(setCustomerCategory(ctgType));
				customerDetails.getCustomer().setLovDescCustCtgCodeName(setCustomerCategory(ctgType));
				customerDetails.getCustomer().setCustCIF(getCustomerDetailsService().getNewProspectCustomerCIF());
				customerDetails.getCustomer().setCustCRCPR(custCPRCR);
				customerDetails.getCustomer().setCustNationality(this.custCountry.getValue());
				customerDetails.getCustomer().setLovDescCustNationalityName(this.custCountry.getDescription());


				//Reset Data from WIF Details if Exists
				WIFCustomer wifCustomer = getCustomerService().getWIFCustomerByID(0, custCPRCR);
				if(wifCustomer != null){
					BeanUtils.copyProperties(customerDetails.getCustomer(), wifCustomer);
					customerDetails.getCustomer().setCustID(Long.MIN_VALUE);
					customerDetails.setCustomerIncomeList(getCustomerIncomeService().getCustomerIncomes(wifCustomer.getCustID(), true));
					
					if(customerDetails.getCustomerIncomeList() != null && !customerDetails.getCustomerIncomeList().isEmpty()){
						for (CustomerIncome income : customerDetails.getCustomerIncomeList()) {
							income.setLovDescCustCIF(customerDetails.getCustomer().getCustCIF());
						}
					}
				}
				
				setCustomerStatus(customerDetails);
			
			}
			if (customer != null) {
				customerDetails = getCustomerDetailsService().getCustomerById(customer.getId());
			}
			if (customerDetails != null) {
				this.customerListCtrl.buildDialogWindow(customerDetails, newRecord);
			}
			this.window_CoreCustomer.onClose();
		} catch (CustomerNotFoundException e) {
			logger.error(e);
			MultiLineMessageBox.show(Labels.getLabel("Cust_NotFound"), Labels.getLabel("message.Error"), MultiLineMessageBox.ABORT, MultiLineMessageBox.ERROR);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$custCountry(Event event){
		logger.debug("Entering" + event.toString());
		doClearCRCPR();
		
		isCountryBehrain = StringUtils.trimToEmpty(this.custCountry.getValue()).equals(PennantConstants.COUNTRY_BEHRAIN);
		
		if(isCountryBehrain){
			if(isCustCorp) {
				setCPRNumberProperties(false);
			}else{
				setCPRNumberProperties(true);
			}
		}else{
			setCPRNumberProperties(true);
		}
		if(!this.tempCountry.equalsIgnoreCase(this.custCountry.getValue())){
			this.custCPR.setConstraint("");
			this.custCPR.setValue("");
			this.custCR1.setValue("");
			this.custCR2.setValue("");
		}
		this.tempCountry = this.custCountry.getValue();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onCheck$exsiting(Event event){
		this.custCIF.setDisabled(false);
		this.row_custCtgType.setVisible(false);
		this.row_custCountry.setVisible(false);
		this.row_custCRCPR.setVisible(false);
		this.custCPR.setValue("");
	}
	
	public void onCheck$prospect(Event event){
		this.custCIF.setValue("");
		this.custCIF.setDisabled(true);
		this.row_custCtgType.setVisible(true);
		this.row_custCountry.setVisible(true);
		this.row_custCRCPR.setVisible(true);
	}
	
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		this.window_CoreCustomer.onClose();
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custCountry.setMaxlength(2);
		this.custCountry.setMandatoryStyle(true);
		this.custCountry.setModuleName("Country");
		this.custCountry.setValueColumn("CountryCode");
		this.custCountry.setDescColumn("CountryDesc");
		this.custCountry.setValidateColumns(new String[] { "CountryCode" });
		logger.debug("Leaving");
	}
	
	public void setCPRNumberProperties(boolean isCPR){
		logger.debug("Entering");
		if(isCPR){
			this.custCPR.setVisible(true);
			this.hboxCustCR.setVisible(false);
			if(isCountryBehrain){
				this.custCPR.setMaxlength(9);	
			}else{
				this.custCPR.setMaxlength(15);
				this.custCPR.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCPR.value"),PennantRegularExpressions.REGEX_NUMERIC, true));
			}
		}else{
			if(isCountryBehrain){
				this.hboxCustCR.setVisible(true);
				this.custCPR.setVisible(false);
			}else{
				this.custCPR.setMaxlength(15);	
			}
		}

		if(isCustCorp){
			this.label_CustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CustomerDialog_CustCR.value"));
			this.label_CustomerDialog_CustCountry.setValue(Labels.getLabel("label_CustomerDialog_CustDomicile.value"));
		}else{
			this.label_CustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CustomerDialog_CustCPR.value"));
			this.label_CustomerDialog_CustCountry.setValue(Labels.getLabel("label_CustomerDialog_CustCountry.value"));
		}
		logger.debug("Leaving");
	}
	
	public void doClearCRCPR(){
		logger.debug("Entering");
		Clients.clearWrongValue(this.custCountry);
		Clients.clearWrongValue(this.custCPR);
		Clients.clearWrongValue(this.custCR1);
		Clients.clearWrongValue(this.custCR2);
		this.custCPR.setConstraint("");
		this.custCPR.setErrorMessage("");
		this.custCPR.setValue("");
		this.custCR1.setConstraint("");
		this.custCR1.setErrorMessage("");
		this.custCR2.setConstraint("");
		this.custCR2.setErrorMessage("");
		this.custCR1.setValue("");
		this.custCR2.setValue("");
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	public CustomerIncomeService getCustomerIncomeService() {
		return customerIncomeService;
	}
	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

	public void setPffCustomerPreparation(PFFCustomerPreparation pffCustomerPreparation) {
		this.pffCustomerPreparation = pffCustomerPreparation;
	}

	public PFFCustomerPreparation getPffCustomerPreparation() {
		return pffCustomerPreparation;
	}
	
	private void setCustomerStatus(CustomerDetails customerDetails) {
		try {
			if (StringUtils.trimToEmpty(customerDetails.getCustomer().getCustSts()).equals("")) {
				CustomerStatusCode customerStatusCode = getCustomerDetailsService().getCustStatusByMinDueDays();
				if (customerStatusCode != null) {
					customerDetails.getCustomer().setCustSts(customerStatusCode.getCustStsCode());
					customerDetails.getCustomer().setLovDescCustStsName(customerStatusCode.getCustStsDescription());
				}
			}
		} catch (Exception e) {
			logger.debug("Customer Status by Min Due Days " + e);
		}
	}
	
	private String setCustomerCategory(String custCtgType){
		custCtgType=StringUtils.trimToEmpty(custCtgType);
		if (custCtgType.equals(PennantConstants.CUST_CAT_INDIVIDUAL)) {
			return PennantConstants.PFF_CUSTCTG_INDIV;
		}
		if (custCtgType.equals(PennantConstants.CUST_CAT_CORPORATE)) {
			return PennantConstants.PFF_CUSTCTG_CORP;
		}
		if (custCtgType.equals(PennantConstants.CUST_CAT_BANK)) {
			return PennantConstants.PFF_CUSTCTG_BANK;
		}
		return "";
	}
	
}