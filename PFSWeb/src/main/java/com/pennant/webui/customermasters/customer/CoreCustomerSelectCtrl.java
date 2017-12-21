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

import java.text.ParseException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
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
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul file.
 */
public class CoreCustomerSelectCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final long serialVersionUID = 9086034736503097868L;
	private static final Logger logger = Logger.getLogger(CoreCustomerSelectCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CoreCustomer; 
	protected Textbox custCIF; 
	protected Textbox custCPR; 
	protected Textbox custCR1; 						
	protected Textbox custCR2; 						
	protected Hbox hboxCustCR;
	protected Label label_CoreCustomerDialog_CustCRCPR;
	protected Borderlayout borderLayout_CoreCustomer; 
	// checkRights
	protected Button btnSearchCustFetch; 
	protected CustomerListCtrl customerListCtrl; 
	private transient boolean validationOn;
	protected Radio exsiting;
	protected Radio prospect;
	protected Combobox custCtgType;
	protected Row row_custCtgType;
	protected Row row_CustCIF;
	protected Row row_custCRCPR;
	protected ExtendedCombobox custNationality; 					
	protected Row row_custCountry;
	protected Label label_CoreCustomerDialog_CustNationality;
	protected Row row_EIDNumber;
	protected Uppercasebox eidNumber;
	protected Label label_CoreCustomerDialog_EIDNumber;
	
	private boolean isCountryBehrain = false;
	private String tempCountry = "";
	private boolean isRetailCustomer = false;
	
	private CustomerDetailsService customerDetailsService;
	private CustomerService customerService;
	private CustomerIncomeService customerIncomeService;
	private CustomerInterfaceService customerInterfaceService;
	private RelationshipOfficerService relationshipOfficerService;
	private BranchService branchService;
	private CustomerTypeService customerTypeService;
	
	/**
	 * default constructor.<br>
	 */
	public CoreCustomerSelectCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CoreCustomer(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CoreCustomer);

		if (arguments.containsKey("customerListCtrl")) {
			this.customerListCtrl = (CustomerListCtrl) arguments.get("customerListCtrl");
		} else {
			this.customerListCtrl = null;
		}
		// set Field Properties
		doSetFieldProperties();

		fillComboBox(custCtgType, "", PennantAppUtil.getcustCtgCodeList(), "");
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
		isRetailCustomer = this.custCtgType.getSelectedItem().getValue().toString().equals(PennantConstants.PFF_CUSTCTG_INDIV);
		this.eidNumber.setValue("");
		setCPRNumberProperties();
		logger.debug("Leaving" + event.toString());
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

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearMessage();
		setValidationOn(true);
		if (this.prospect.isChecked()) {
			this.eidNumber.clearErrorMessage();
			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				this.eidNumber.setConstraint(new PTStringValidator(Labels
						.getLabel("label_CoreCustomerDialog_EIDNumber.value"),
						PennantRegularExpressions.REGEX_EIDNUMBER, false));
			}else{
				this.eidNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CoreCustomerDialog_EIDNumber.value"),
						PennantRegularExpressions.REGEX_PANNUMBER,false));
			}
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
		this.custCtgType.setConstraint("");
		this.custNationality.setConstraint("");
		this.eidNumber.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		this.custCIF.setErrorMessage("");
		this.custCtgType.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.eidNumber.setErrorMessage("");
		Clients.clearWrongValue(this.eidNumber);
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

			boolean newRecord = false;
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());
			Customer customer = null;
			//If  customer exist is checked 
			if (this.exsiting.isChecked()){
				if (StringUtils.isEmpty(cif)) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_CoreCustomerDialog_CoreCustID.value") }));
				}else{
					customer = getCustomerDetailsService().getCheckCustomerByCIF(cif);
				}
				if (customer == null) {
					newRecord = true;
					customerDetails = getCustomerInterfaceService().getCustomerInfoByInterface(cif, "");
					if (customerDetails == null) {
						throw new InterfaceException("9999",Labels.getLabel("Cust_NotFound"));
					}
				}
				
			} else  if (this.prospect.isChecked()) {
				newRecord = true;
				String ctgType=this.custCtgType.getSelectedItem().getValue().toString();

				ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

				try {
					if (StringUtils.trimToEmpty(ctgType).equals(PennantConstants.List_Select)) {
						throw new WrongValueException(this.custCtgType, Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CoreCustomerDialog_CustType.value") }));
					}
				} catch (WrongValueException e) {
					wve.add(e);
				}

				try {
					if (StringUtils.isBlank(this.custNationality.getValue())) {
						if(isRetailCustomer){
							throw new WrongValueException(this.custNationality, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_CoreCustomerDialog_CustCountry.value") }));
						}else{
							throw new WrongValueException(this.custNationality, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_CoreCustomerDialog_CustNationality.value") }));
						}
					}
				} catch (WrongValueException e) {
					wve.add(e);
				}
                
				String eidNumbr = null;
				try{
					eidNumbr = this.eidNumber.getValue();
				}catch(WrongValueException e){
					wve.add(e);
				}
				
				boolean isCustCatIndividual = ctgType.equals(PennantConstants.PFF_CUSTCTG_INDIV);
				String custCPRCR="";

				try {
					if (isCustCatIndividual || !isCountryBehrain) {
						custCPRCR = StringUtils.trimToEmpty(this.custCPR.getValue());
					}else{
						custCPRCR = StringUtils.trimToEmpty(this.custCR1.getValue()+"-"+this.custCR2.getValue());
					}
				} catch (WrongValueException e) {
					wve.add(e);
				}

				doRemoveValidation();
				
				if(wve.size() > 0){
					WrongValueException[] wvea = new WrongValueException[wve.size()];
					for (int i = 0; i < wve.size(); i++) {
						wvea[i] = wve.get(i);
					}
					throw new WrongValuesException(wvea);
				}

				if(StringUtils.isNotBlank(custCPRCR)){
					String custCIF = getCustomerService().getCustomerByCRCPR(custCPRCR,"_View");
					if(custCIF != null){
						MessageUtil
								.showError(Labels.getLabel("label_CoreCustomerDialog_ProspectExist",
										new String[] {
												isCustCatIndividual
														? Labels.getLabel("label_CoreCustomerDialog_EIDNumber.value")
														: Labels.getLabel(
																"label_CoreCustomerDialog_TradeLicenseNumber.value"),
												custCIF }));
						return;
					}
				}
				String custCIF = null;
				if(StringUtils.isNotBlank(eidNumbr)){
					custCIF = getCustomerDetailsService().getEIDNumberById(eidNumbr, "_View");
				}
				if(StringUtils.isNotBlank(custCIF)){
					String msg = Labels.getLabel("label_CoreCustomerDialog_ProspectExist",new String[]{
							isCustCatIndividual ? Labels.getLabel("label_CustCRCPR") : Labels.getLabel("label_CustTradeLicenseNumber"),
									custCIF + ". \n"});
					if (MessageUtil.confirm(msg) != MessageUtil.YES) {
						return;
					}
					this.exsiting.setSelected(true);
					this.custCIF.setValue(custCIF);
					newRecord = false;
					customer = getCustomerDetailsService().getCheckCustomerByCIF(custCIF);
					if (customer == null) {
						newRecord = true;
						customerDetails = getCustomerInterfaceService().getCustomerInfoByInterface(custCIF, "");
						if (customerDetails == null) {
							throw new InterfaceException("9999",Labels.getLabel("Cust_NotFound"));
						}
					}
				}else{
					customerDetails = getCustomerDetailsService().getNewCustomer(true);
					customerDetails.getCustomer().setLovDescCustCtgType(ctgType);
					customerDetails.getCustomer().setCustCtgCode(ctgType);
					customerDetails.getCustomer().setLovDescCustCtgCodeName(ctgType);
					customerDetails.getCustomer().setCustCIF(getCustomerDetailsService().getNewProspectCustomerCIF());
					customerDetails.getCustomer().setCustCRCPR(eidNumbr);
					customerDetails.getCustomer().setCustNationality(this.custNationality.getValue());
					customerDetails.getCustomer().setLovDescCustNationalityName(this.custNationality.getDescription());

					//Setting Primary Relation Ship Officer
					RelationshipOfficer officer = getRelationshipOfficerService().getApprovedRelationshipOfficerById(getUserWorkspace().getUserDetails().getUsername());
					if(officer != null){
						customerDetails.getCustomer().setCustRO1(officer.getROfficerCode());
						customerDetails.getCustomer().setLovDescCustRO1Name(officer.getROfficerDesc());
					}

					//Setting User Branch to Customer Branch
					Branch branch = getBranchService().getApprovedBranchById(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());
					if(branch != null){
						customerDetails.getCustomer().setCustDftBranch(branch.getBranchCode());
						customerDetails.getCustomer().setLovDescCustDftBranchName(branch.getBranchDesc());
					}

					
					//Reset Data from WIF Details if Exists
					if(!(StringUtils.isEmpty(custCPRCR))){
						WIFCustomer wifCustomer = getCustomerService().getWIFCustomerByID(0,custCPRCR);
						if(wifCustomer != null){
							BeanUtils.copyProperties(wifCustomer, customerDetails.getCustomer());
							customerDetails.getCustomer().setCustID(Long.MIN_VALUE);
							customerDetails.setCustomerIncomeList(getCustomerIncomeService().getCustomerIncomes(wifCustomer.getCustID(), true));

							if(customerDetails.getCustomerIncomeList() != null && !customerDetails.getCustomerIncomeList().isEmpty()){
								for (CustomerIncome income : customerDetails.getCustomerIncomeList()) {
									income.setLovDescCustCIF(customerDetails.getCustomer().getCustCIF());
								}
							}
						}
					}
					setCustomerStatus(customerDetails);
				}
			}
			if (customer != null) {
				customerDetails = getCustomerDetailsService().getCustomerById(customer.getId());
			}
			
			if (StringUtils.isNotEmpty(customerDetails.getCustomer().getNextRoleCode())) {
				if(!getUserWorkspace().getUserRoles().contains(customerDetails.getCustomer().getNextRoleCode())){
					throw new AppException(Labels.getLabel("customer_maintainance_otherQueue"));
				}

			}
			if (customerDetails != null) {
				this.customerListCtrl.buildDialogWindow(customerDetails, newRecord);
			}
			this.window_CoreCustomer.onClose();
		} catch (WrongValueException wve) {
			throw wve;
		} catch (WrongValuesException wve) {
			throw wve;
		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$custNationality(Event event){
		logger.debug("Entering" + event.toString());
		doClearCRCPR();
		if(!this.tempCountry.equalsIgnoreCase(this.custNationality.getValue())){
			this.custCPR.setConstraint("");
			this.custCPR.setValue("");
			this.custCR1.setValue("");
			this.custCR2.setValue("");
		}
		this.tempCountry = this.custNationality.getValue();
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$exsiting(Event event){
		this.custCIF.setDisabled(false);
		this.row_custCtgType.setVisible(false);
		this.row_custCountry.setVisible(false);
		this.row_custCRCPR.setVisible(false);
		this.row_CustCIF.setVisible(true);
		this.custCPR.setValue("");
		this.row_EIDNumber.setVisible(false);
	}

	public void onCheck$prospect(Event event){
		this.custCIF.setValue("");
		this.custCIF.setDisabled(true);
		this.row_custCtgType.setVisible(true);
		this.row_custCountry.setVisible(true);
		this.row_custCRCPR.setVisible(false);
		this.row_CustCIF.setVisible(false);
		this.row_EIDNumber.setVisible(true);
		this.custNationality.setValue(SysParamUtil.getValueAsString("CURR_SYSTEM_COUNTRY"));
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
		this.custNationality.setMaxlength(2);
		this.custNationality.setMandatoryStyle(true);
		this.custNationality.setModuleName("NationalityCode");
		this.custNationality.setValueColumn("NationalityCode");
		this.custNationality.setDescColumn("NationalityDesc");
		this.custNationality.setValidateColumns(new String[] { "NationalityCode" });
		this.eidNumber.setMaxlength(LengthConstants.LEN_PAN);
		this.custCIF.setMaxlength(LengthConstants.LEN_CIF);
		logger.debug("Leaving");
	}

	public void setCPRNumberProperties(){
		logger.debug("Entering");
		if(isRetailCustomer){
			this.label_CoreCustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CoreCustomerDialog_CustCPR.value"));
			this.label_CoreCustomerDialog_CustNationality.setValue(Labels.getLabel("label_CoreCustomerDialog_CustCountry.value"));
			this.label_CoreCustomerDialog_EIDNumber.setValue(Labels.getLabel("label_CoreCustomerDialog_EIDNumber.value"));
			this.eidNumber.setMaxlength(LengthConstants.LEN_EID);
		}else{
			this.label_CoreCustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CoreCustomerDialog_CustCR.value"));
			this.label_CoreCustomerDialog_CustNationality.setValue(Labels.getLabel("label_CoreCustomerDialog_CustNationality.value"));
			this.label_CoreCustomerDialog_EIDNumber.setValue(Labels.getLabel("label_CoreCustomerDialog_TradeLicenseNumber.value"));
		}
		this.eidNumber.setMaxlength(LengthConstants.LEN_PAN);
		if("#".equals(getComboboxValue(this.custCtgType))){
			this.label_CoreCustomerDialog_EIDNumber.setValue(Labels.getLabel("label_CoreCustomerDialog_EIDNumber.value"));
		}
		logger.debug("Leaving");
	}

	public void doClearCRCPR(){
		logger.debug("Entering");
		Clients.clearWrongValue(this.custNationality);
		Clients.clearWrongValue(this.custCPR);
		Clients.clearWrongValue(this.custCR1);
		Clients.clearWrongValue(this.custCR2);
		Clients.clearWrongValue(this.eidNumber);
		this.custCPR.setConstraint("");
		this.custCPR.setErrorMessage("");
		this.custCPR.setValue("");
		this.custCR1.setConstraint("");
		this.custCR1.setErrorMessage("");
		this.custCR2.setConstraint("");
		this.custCR2.setErrorMessage("");
		this.eidNumber.setConstraint("");
		this.eidNumber.setErrorMessage("");
		this.custCR1.setValue("");
		this.custCR2.setValue("");
		logger.debug("Leaving");
	}
	
	private void setCustomerStatus(CustomerDetails customerDetails) {
		try {
			if (StringUtils.isBlank(customerDetails.getCustomer().getCustSts())) {
				CustomerStatusCode customerStatusCode = getCustomerDetailsService().getCustStatusByMinDueDays();
				if (customerStatusCode != null) {
					customerDetails.getCustomer().setCustSts(customerStatusCode.getCustStsCode());
					customerDetails.getCustomer().setLovDescCustStsName(customerStatusCode.getCustStsDescription());
				}
			}
		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}
	}

	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
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

	public RelationshipOfficerService getRelationshipOfficerService() {
		return relationshipOfficerService;
	}
	public void setRelationshipOfficerService(
			RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}

	public BranchService getBranchService() {
		return branchService;
	}
	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public CustomerTypeService getCustomerTypeService() {
		return customerTypeService;
	}
	public void setCustomerTypeService(CustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}

}