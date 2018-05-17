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
 * 27-04-2018       Sai & Manoj              0.2          story #360 Externalize customer   * 
 *                                                        primary identity (Re-factoring)   * 
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
import java.util.Map;

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
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.CustomerInterfaceService;
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
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul file.
 */
public class CoreCustomerSelectCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final long serialVersionUID = 9086034736503097868L;
	private static final Logger logger = Logger.getLogger(CoreCustomerSelectCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUl-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CoreCustomer;
	protected Textbox custCIF;
	protected Borderlayout borderLayout_CoreCustomer;
	protected Button btnSearchCustFetch;
	protected CustomerListCtrl customerListCtrl;
	private transient boolean validationOn;
	protected Radio exsiting;
	protected Radio prospect;
	protected Combobox custCtgType;
	protected Row row_custCtgType;
	protected Row row_CustCIF;
	protected ExtendedCombobox custNationality;
	protected Row row_custCountry;
	protected Label label_CoreCustomerDialog_CustNationality;
	protected Row row_PrimaryID;
	protected Label label_CoreCustomerDialog_PrimaryID;
	protected Space space_PrimaryID;
	protected Uppercasebox primaryID;

	private boolean isRetailCustomer = false;

	private CustomerDetailsService customerDetailsService;
	private CustomerService customerService;
	private CustomerIncomeService customerIncomeService;
	private CustomerInterfaceService customerInterfaceService;
	private RelationshipOfficerService relationshipOfficerService;
	private BranchService branchService;
	private CustomerTypeService customerTypeService;

	// Properties related to primary identity.
	private String primaryIdLabel;
	private String primaryIdRegex;
	private boolean primaryIdMandatory;

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
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CoreCustomer(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CoreCustomer);

		if (arguments.containsKey("customerListCtrl")) {
			customerListCtrl = (CustomerListCtrl) arguments.get("customerListCtrl");
		} else {
			customerListCtrl = null;
		}

		// Set Field Properties
		doSetFieldProperties();

		fillComboBox(custCtgType, "", PennantAppUtil.getcustCtgCodeList(), "");

		custCIF.setFocus(true);
		window_CoreCustomer.doModal();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Sets the primary identity field attributes like:<br/>
	 * <code>
	 * - Label<br/>
	 * - Mandatory<br/>
	 * - Max length<br/> 
	 * - Constraint<br/>
	 * </code>
	 */
	private void doSetPrimaryIdAttributes() {
		// ### 01-05-2018 - Start - TuleApp ID : #360

		Map<String, String> attributes = PennantApplicationUtil.getPrimaryIdAttributes(getComboboxValue(custCtgType));
		
		primaryIdLabel = attributes.get("LABEL");
		primaryIdMandatory = Boolean.valueOf(attributes.get("MANDATORY"));
		primaryIdRegex = attributes.get("REGEX");
		int maxLength = Integer.valueOf(attributes.get("LENGTH")); 

		label_CoreCustomerDialog_PrimaryID.setValue(Labels.getLabel(primaryIdLabel));
		space_PrimaryID.setSclass(primaryIdMandatory ? PennantConstants.mandateSclass : "");
		primaryID.setSclass(PennantConstants.mandateSclass);
		primaryID.setValue("");
		primaryID.setMaxlength(maxLength);
		// ### 01-05-2018 - End
	}

	public void onChange$custCtgType(Event event) {
		logger.debug(Literal.ENTERING);

		doClearMessage();

		isRetailCustomer = custCtgType.getSelectedItem().getValue().toString()
				.equals(PennantConstants.PFF_CUSTCTG_INDIV);

		if (isRetailCustomer) {
			label_CoreCustomerDialog_CustNationality
					.setValue(Labels.getLabel("label_FinanceCustomerList_CustNationality.value"));
		} else {
			label_CoreCustomerDialog_CustNationality
					.setValue(Labels.getLabel("label_CoreCustomerDialog_CustNationality.value"));
		}

		doSetPrimaryIdAttributes();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		setValidationOn(true);

		if (prospect.isChecked()) {
			primaryID.clearErrorMessage();
			primaryID.setConstraint(
					new PTStringValidator(Labels.getLabel(primaryIdLabel), primaryIdRegex, primaryIdMandatory));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		custCIF.setConstraint("");
		custCtgType.setConstraint("");
		custNationality.setConstraint("");
		primaryID.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		custCIF.setErrorMessage("");
		custCtgType.setErrorMessage("");
		custNationality.setErrorMessage("");
		Clients.clearWrongValue(custNationality);
		primaryID.setErrorMessage("");
		Clients.clearWrongValue(primaryID);
	}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSearchCustFetch(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		CustomerDetails customerDetails = null;

		// Get the data of Customer from Core Banking Customer
		try {
			boolean newRecord = false;
			String cif = StringUtils.trimToEmpty(custCIF.getValue());
			Customer customer = null;

			// If customer exist is checked 
			if (exsiting.isChecked()) {
				if (StringUtils.isEmpty(cif)) {
					throw new WrongValueException(custCIF, Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_CoreCustomerDialog_CoreCustID.value") }));
				} else {
					customer = getCustomerDetailsService().getCheckCustomerByCIF(cif);
				}

				if (customer == null) {
					newRecord = true;
					customerDetails = getCustomerInterfaceService().getCustomerInfoByInterface(cif, "");
					if (customerDetails == null) {
						throw new InterfaceException("9999", Labels.getLabel("Cust_NotFound"));
					}
				}
			} else if (prospect.isChecked()) {
				newRecord = true;
				String ctgType = custCtgType.getSelectedItem().getValue().toString();

				ArrayList<WrongValueException> wve = new ArrayList<>();

				try {
					if (StringUtils.trimToEmpty(ctgType).equals(PennantConstants.List_Select)) {
						throw new WrongValueException(custCtgType, Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CoreCustomerDialog_CustType.value") }));
					}
				} catch (WrongValueException e) {
					wve.add(e);
				}

				try {
					if (StringUtils.isBlank(custNationality.getValue())) {
						if (isRetailCustomer) {
							throw new WrongValueException(custNationality, Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { Labels.getLabel("label_CoreCustomerDialog_CustCountry.value") }));
						} else {
							throw new WrongValueException(custNationality,
									Labels.getLabel("FIELD_NO_EMPTY", new String[] {
											Labels.getLabel("label_CoreCustomerDialog_CustNationality.value") }));
						}
					}
				} catch (WrongValueException e) {
					wve.add(e);
				}

				// Get the primary identity.
				String primaryIdNumber = null;

				try {
					primaryIdNumber = primaryID.getValue();
				} catch (WrongValueException e) {
					wve.add(e);
				}

				doRemoveValidation();

				// Throw if any exceptions exist.
				if (!wve.isEmpty()) {
					WrongValueException[] wvea = new WrongValueException[wve.size()];
					for (int i = 0; i < wve.size(); i++) {
						wvea[i] = wve.get(i);
					}
					throw new WrongValuesException(wvea);
				}

				// Check whether any other customer exists with the same primary identity.
				if (StringUtils.isNotBlank(primaryIdNumber)) {
					cif = getCustomerDetailsService().getEIDNumberById(primaryIdNumber, "_View");
				}

				if (StringUtils.isNotBlank(cif)) {
					String msg = Labels.getLabel("label_CoreCustomerDialog_ProspectExist",
							new String[] { Labels.getLabel(primaryIdLabel), cif + ". \n" });

					if (MessageUtil.confirm(msg) != MessageUtil.YES) {
						return;
					}

					exsiting.setSelected(true);
					custCIF.setValue(cif);
					newRecord = false;
					customer = getCustomerDetailsService().getCheckCustomerByCIF(cif);

					if (customer == null) {
						newRecord = true;
						customerDetails = getCustomerInterfaceService().getCustomerInfoByInterface(cif, "");

						if (customerDetails == null) {
							throw new InterfaceException("9999", Labels.getLabel("Cust_NotFound"));
						}
					}
				} else {
					customerDetails = getCustomerDetailsService().getNewCustomer(true);
					customerDetails.getCustomer().setLovDescCustCtgType(ctgType);
					customerDetails.getCustomer().setCustCtgCode(ctgType);
					customerDetails.getCustomer().setLovDescCustCtgCodeName(ctgType);
					customerDetails.getCustomer().setCustCIF(getCustomerDetailsService().getNewProspectCustomerCIF());
					customerDetails.getCustomer().setCustCRCPR(primaryIdNumber);
					customerDetails.getCustomer().setCustNationality(custNationality.getValue());
					customerDetails.getCustomer().setLovDescCustNationalityName(custNationality.getDescription());

					//Setting Primary Relation Ship Officer
					RelationshipOfficer officer = getRelationshipOfficerService()
							.getApprovedRelationshipOfficerById(getUserWorkspace().getUserDetails().getUsername());
					if (officer != null) {
						customerDetails.getCustomer().setCustRO1(Long.parseLong(officer.getROfficerCode()));
						customerDetails.getCustomer().setLovDescCustRO1Name(officer.getROfficerDesc());
					}

					//Setting User Branch to Customer Branch
					Branch branch = getBranchService().getApprovedBranchById(
							getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());
					if (branch != null) {
						customerDetails.getCustomer().setCustDftBranch(branch.getBranchCode());
						customerDetails.getCustomer().setLovDescCustDftBranchName(branch.getBranchDesc());
					}

					//Reset Data from WIF Details if Exists
					String custCPRCR = "";

					if (!(StringUtils.isEmpty(custCPRCR))) {
						WIFCustomer wifCustomer = getCustomerService().getWIFCustomerByID(0, custCPRCR);
						if (wifCustomer != null) {
							BeanUtils.copyProperties(wifCustomer, customerDetails.getCustomer());
							customerDetails.getCustomer().setCustID(Long.MIN_VALUE);
							customerDetails.setCustomerIncomeList(
									getCustomerIncomeService().getCustomerIncomes(wifCustomer.getCustID(), true));

							if (customerDetails.getCustomerIncomeList() != null
									&& !customerDetails.getCustomerIncomeList().isEmpty()) {
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
				if (!getUserWorkspace().getUserRoles().contains(customerDetails.getCustomer().getNextRoleCode())) {
					throw new AppException(Labels.getLabel("customer_maintainance_otherQueue"));
				}

			}
			if (customerDetails != null) {
				customerListCtrl.buildDialogWindow(customerDetails, newRecord);
			}
			window_CoreCustomer.onClose();
		} catch (WrongValueException | WrongValuesException wve) {
			throw wve;
		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$exsiting(Event event) {
		custCIF.setDisabled(false);
		row_custCtgType.setVisible(false);
		row_custCountry.setVisible(false);
		row_CustCIF.setVisible(true);
		row_PrimaryID.setVisible(false);
	}

	public void onCheck$prospect(Event event) {
		custCIF.setValue("");
		custCIF.setDisabled(true);
		row_custCtgType.setVisible(true);
		row_custCountry.setVisible(true);
		row_CustCIF.setVisible(false);
		row_PrimaryID.setVisible(true);

		custNationality.setValue(SysParamUtil.getValueAsString("CURR_SYSTEM_COUNTRY"));

		doSetPrimaryIdAttributes();
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
		window_CoreCustomer.onClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		custNationality.setMaxlength(2);
		custNationality.setMandatoryStyle(true);
		custNationality.setModuleName("NationalityCode");
		custNationality.setValueColumn("NationalityCode");
		custNationality.setDescColumn("NationalityDesc");
		custNationality.setValidateColumns(new String[] { "NationalityCode" });
		custCIF.setMaxlength(LengthConstants.LEN_CIF);
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

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

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

	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
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
