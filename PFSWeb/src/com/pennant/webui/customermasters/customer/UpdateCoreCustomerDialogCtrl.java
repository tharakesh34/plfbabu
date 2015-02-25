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
 * FileName    		:  UpdateCoreCustomerDialogCtrl.java                                              * 	  
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.Interface.service.impl.PFFCustomerPreparation;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.EquationMasterMissedDetail;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class UpdateCoreCustomerDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 9031340167587772517L;
	private final static Logger logger = Logger.getLogger(UpdateCoreCustomerDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_UpdateCoreCustomer;        // autowired

	protected ExtendedCombobox   custCIF;               //autowired
	protected Button   btnUpdateCustCIF;                //autowired     
	protected Tab    newDetailsTab;                     //autowired
	protected Tab    exisitngDetailsTab;                //autowired
	protected Listbox    listBoxNewCustomer;            //autowired
	protected Listbox    listBoxUpdateCustomer;         //autowired

	private Customer customer; 

	private Listitem listitem = null ; 
	private Listcell listcell = null ; 
	private String custCIFTemp="";

	private CustomerDetailsService customerDetailsService;
	private DailyDownloadInterfaceService dailyDownloadInterfaceService;
	private PFFCustomerPreparation pffCustomerPreparation;
	private  transient PagedListService     pagedListService;

	private CustomerDetails coreCustomerDetails = null;
	private CustomerDetails pffCustomerDetails = null;
	private CustomerDetails saveCustomerChildDetails = new CustomerDetails();
	List<EquationMasterMissedDetail> masterValueMissedDetails = new ArrayList<EquationMasterMissedDetail>();

	private static final String listGroupCustomer = "CustomerDetails";
	private static final String listGroupAddress = "AddressDetails";
	private static final String listGroupPhoneNumber = "PhoneDetails";
	private static final String listGroupEmail = "EmailDetails";
	private static final String listGroupRating = "CustomerRatings";

	Date	dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());

	/**
	 * default constructor.<br>
	 */
	public UpdateCoreCustomerDialogCtrl() {
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
	public void onCreate$window_UpdateCoreCustomer(Event event) throws Exception {
		logger.debug("Entering");

		// set Field Properties
		doSetFieldProperties();

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custCIF.setMaxlength(10);
		this.custCIF.setMandatoryStyle(true);
		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });
		logger.debug("Leaving");
	}


	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer
	 *            Customer
	 * @throws CustomerNotFoundException 
	 * @throws WrongValueException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public void onFulfill$custCIF(Event event) throws WrongValueException, CustomerNotFoundException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		logger.debug("Entering");
		Object dataObject = custCIF.getObject();
		if (dataObject instanceof String) {
			this.listBoxUpdateCustomer.getItems().clear();
			this.listBoxNewCustomer.getItems().clear();
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", 
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		} else {
			customer = (Customer) dataObject;
			if (customer != null ) {
				Clients.clearWrongValue(custCIF);
				if(!custCIFTemp.equalsIgnoreCase(customer.getCustCIF())){
					this.listBoxUpdateCustomer.getItems().clear();
					this.listBoxNewCustomer.getItems().clear();
					doFillCustomerDetails();
				}
				custCIFTemp = customer.getCustCIF();
			}else{
				this.listBoxUpdateCustomer.getItems().clear();
				this.listBoxNewCustomer.getItems().clear();
				custCIFTemp = "";
			}
		}
		logger.debug("Leaving");
	}

	private void doFillCustomerDetails() throws CustomerNotFoundException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
		logger.debug("Entering");
		pffCustomerDetails = getCustomerDetailsService().getCustomerById(customer.getId());
		coreCustomerDetails = getPffCustomerPreparation().getCustomerByInterface(this.custCIF.getValue(), "");	
		if(pffCustomerDetails != null && coreCustomerDetails != null){
			processCustomerDetails(coreCustomerDetails,pffCustomerDetails);
			processAddressDetails(coreCustomerDetails, pffCustomerDetails);
			processPhoneNumberDetails(coreCustomerDetails, pffCustomerDetails);
			processEmailDetails(coreCustomerDetails, pffCustomerDetails);
			processRatingDetails(coreCustomerDetails, pffCustomerDetails);
		}
		if(this.listBoxUpdateCustomer.getItems() != null && this.listBoxUpdateCustomer.getItems().size() > 0){
			this.exisitngDetailsTab.setVisible(true);
			this.exisitngDetailsTab.setSelected(true);
			this.listBoxUpdateCustomer.setVisible(true);
		} 
		if(this.listBoxNewCustomer.getItems() != null && this.listBoxNewCustomer.getItems().size() > 0){
			this.newDetailsTab.setVisible(true);
			if(this.listBoxUpdateCustomer.getItems() == null || this.listBoxUpdateCustomer.getItems().size() <= 0){
				this.newDetailsTab.setSelected(true);
			}
		}else{
			this.newDetailsTab.setVisible(false);
		}
		if((this.listBoxUpdateCustomer.getItems() == null || this.listBoxUpdateCustomer.getItems().isEmpty()) &&
				(this.listBoxNewCustomer.getItems() == null ||this. listBoxNewCustomer.getItems().isEmpty())){
			Clients.showNotification(Labels.getLabel("UpdateCoreCustomer_EmptyDetails"),  "info", null, null, -1);
		}
		logger.debug("Leaving");
	}


	public void onClick$btnUpdateCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering");
		if(validate()){
			coreCustomerDetails.getCustomer().setUserDetails(getUserWorkspace().getLoginUserDetails());
			try{
				if(this.listBoxUpdateCustomer.getSelectedItems() != null && !this.listBoxUpdateCustomer.getSelectedItems().isEmpty()){
					updateCustomerDetails();
				}
				if(this.listBoxNewCustomer.getItems() != null && !this.listBoxNewCustomer.getItems().isEmpty()){
					getDailyDownloadInterfaceService().saveCustomerDetails(saveCustomerChildDetails);
				}
				if(!masterValueMissedDetails.isEmpty()){
					getDailyDownloadInterfaceService().saveMasterValueMissedDetails(masterValueMissedDetails);
				}
				this.custCIFTemp = "";
				Clients.showNotification("Record Updated Successfully",  "info", null, null, -1);
			}catch(Exception e){
				e.printStackTrace();
				this.custCIFTemp = "";
				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show("Record Updation Failed",Labels.getLabel("UpdateCoreCustomer_Message_Title"),
						MultiLineMessageBox.OK, Messagebox.ERROR, true);
			}
			this.listBoxUpdateCustomer.getItems().clear();
			this.listBoxNewCustomer.getItems().clear();
			this.custCIF.setValue("");
			this.custCIF.setDescription("");
		}

		logger.debug("Leaving");
	}

	private boolean validate() throws InterruptedException{
		logger.debug("Entering");
		MultiLineMessageBox.doSetTemplate();
		if(coreCustomerDetails == null || coreCustomerDetails.getCustomer() == null){
			MultiLineMessageBox.show(Labels.getLabel("UpdateCoreCustomer_EmptyDetails"),Labels.getLabel("UpdateCoreCustomer_Message_Title"),
					MultiLineMessageBox.OK, Messagebox.INFORMATION, true);
			return false;
		}
		if(listBoxNewCustomer.getItems() == null || listBoxNewCustomer.getItems().isEmpty()){
			if(listBoxUpdateCustomer.getItems() == null || listBoxUpdateCustomer.getItems().isEmpty()){
				MultiLineMessageBox.show(Labels.getLabel("UpdateCoreCustomer_EmptyDetails"),Labels.getLabel("UpdateCoreCustomer_Message_Title"),
						MultiLineMessageBox.OK, Messagebox.INFORMATION, true);
				logger.debug("Leaving");
				return false;
			}else if(listBoxUpdateCustomer.getSelectedItems() == null || listBoxUpdateCustomer.getSelectedItems().isEmpty()){
				MultiLineMessageBox.show(Labels.getLabel("UpdateCoreCustomer_NoFieldSelected"),Labels.getLabel("UpdateCoreCustomer_Message_Title"),
						MultiLineMessageBox.OK, Messagebox.INFORMATION, true);
				logger.debug("Leaving");
				return false;
			}
		}else if((listBoxUpdateCustomer.getItems() != null && !listBoxUpdateCustomer.getItems().isEmpty()) && 
				(listBoxUpdateCustomer.getSelectedItems() == null || listBoxUpdateCustomer.getSelectedItems().isEmpty())){
			int conf = (MultiLineMessageBox.show(Labels.getLabel("UpdateCoreCustomer_NoUpdateFieldSelected"),Labels.getLabel("UpdateCoreCustomer_Message_Title"),
					MultiLineMessageBox.YES|MultiLineMessageBox.NO, Messagebox.INFORMATION, true));
			logger.debug("Leaving");
			if (conf != MultiLineMessageBox.YES) {
				return false;
			}
		}
		if(!custFieldMasterCheckValidation()){
			return false;
		}
		logger.debug("Leaving");
		return true;
	}


	private boolean custFieldMasterCheckValidation() throws InterruptedException{
		logger.debug("Entering");
		
		List<CustomerDetails> customerDetailsListTemp = new ArrayList<CustomerDetails>();
		CustomerDetails tempCustomerDetails = new CustomerDetails();
		Cloner cloner = new Cloner();
		tempCustomerDetails = cloner.deepClone(this.coreCustomerDetails);
		customerDetailsListTemp.add(tempCustomerDetails);
		List<CustomerDetails> customerDetailsList = getPffCustomerPreparation().validateMasterFieldDetails(customerDetailsListTemp, null);
		this.coreCustomerDetails  = customerDetailsList.get(0);
		String errMsg = getMissedMasterErrorDetails(getPffCustomerPreparation().getMasterMissedDetails());
		tempCustomerDetails = null;
		if(!errMsg.equals("")){
			errMsg = errMsg + "  value does not exists in Master Table";
			MultiLineMessageBox.show(errMsg,Labels.getLabel("UpdateCoreCustomer_Message_Title"),
					MultiLineMessageBox.OK, Messagebox.ERROR, true);
			return false;
		}
		
		logger.debug("Leaving");	
		return true;
	}
	
	private String getMissedMasterErrorDetails(List<EquationMasterMissedDetail> masterValueMissedDetails){
		String errFields ="";
		if(this.listBoxUpdateCustomer.getSelectedItems() != null && !this.listBoxUpdateCustomer.getSelectedItems().isEmpty()){
			for (Listitem listitem : this.listBoxUpdateCustomer.getSelectedItems()) {
				if(listitem.isSelected() && !valueExistInMaster(listitem.getId(),masterValueMissedDetails)){
					if(errFields.equals("")){
						errFields = errFields  + getFieldLabel(listitem.getId());
					}else{
					    errFields = errFields  + "," + getFieldLabel(listitem.getId());
					}
				}
			}
		}
		if(this.listBoxNewCustomer.getItems() != null && !this.listBoxNewCustomer.getItems().isEmpty()){
			for (Listitem listitem : this.listBoxNewCustomer.getItems()) {
				if(!valueExistInMaster(listitem.getId(),masterValueMissedDetails)){
					if(errFields.equals("")){
						errFields = errFields  + getFieldLabel(listitem.getId());
					}else{
						errFields = errFields  + "," + getFieldLabel(listitem.getId());
					}
				}
			}
		}
		return errFields;
	}
	
	private boolean valueExistInMaster(String field,List<EquationMasterMissedDetail> masterValueMissedDetails){
		for (EquationMasterMissedDetail masterMissedDetail : masterValueMissedDetails) {
	        if(StringUtils.trimToEmpty(field).equalsIgnoreCase(masterMissedDetail.getFieldName())){
	        	return false;
	        }
        }
		return true;
	}
	
	
	public void processCustomerDetails(CustomerDetails core,CustomerDetails pff) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException{
		logger.debug("Entering");
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustCtgCode()).equals(core.getCustomer().getCustCtgCode())){
			addCustFieldToUpdateList(listGroupCustomer,"CustCtgCode",getFieldLabel("CustCtgCode"),
					core.getCustomer().getCustCtgCode(), pff.getCustomer().getCustCtgCode());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustShrtName()).equals(core.getCustomer().getCustShrtName())){
			addCustFieldToUpdateList(listGroupCustomer,"CustShrtName",getFieldLabel("CustShrtName"),
					core.getCustomer().getCustShrtName(), pff.getCustomer().getCustShrtName());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustCoreBank()).equals(core.getCustomer().getCustCoreBank())){
			addCustFieldToUpdateList(listGroupCustomer,"CustCoreBank",getFieldLabel("CustCoreBank"),
					core.getCustomer().getCustCoreBank(), pff.getCustomer().getCustCoreBank());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustFName()).equals(core.getCustomer().getCustFName())){
			addCustFieldToUpdateList(listGroupCustomer,"CustFName",getFieldLabel("CustFName"),core.getCustomer().getCustFName(), pff.getCustomer().getCustFName());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustFNameLclLng()).equals(core.getCustomer().getCustFNameLclLng())){
			addCustFieldToUpdateList(listGroupCustomer,"CustFNameLclLng",getFieldLabel("CustFNameLclLng"),core.getCustomer().getCustFNameLclLng(),
					pff.getCustomer().getCustFNameLclLng());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustShrtNameLclLng()).equals(core.getCustomer().getCustShrtNameLclLng())){
			addCustFieldToUpdateList(listGroupCustomer,"CustShrtNameLclLng",getFieldLabel("CustShrtNameLclLng"),
					core.getCustomer().getCustShrtNameLclLng(),pff.getCustomer().getCustShrtNameLclLng());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustTypeCode()).equals(core.getCustomer().getCustTypeCode())){
			addCustFieldToUpdateList(listGroupCustomer,"CustTypeCode",getFieldLabel("CustTypeCode"),
					core.getCustomer().getCustTypeCode(),pff.getCustomer().getCustTypeCode());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustDftBranch()).equals(core.getCustomer().getCustDftBranch())){
			addCustFieldToUpdateList(listGroupCustomer,"CustDftBranch",getFieldLabel("CustDftBranch"),
					core.getCustomer().getCustDftBranch(),pff.getCustomer().getCustDftBranch());
		}
		if(pff.getCustomer().getCustGroupID() != core.getCustomer().getCustGroupID()){
			addCustFieldToUpdateList(listGroupCustomer,"CustGroupID",getFieldLabel("CustGroupID"),
					core.getCustomer().getCustGroupID(),pff.getCustomer().getCustGroupID());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustParentCountry()).equals(core.getCustomer().getCustParentCountry())){
			addCustFieldToUpdateList(listGroupCustomer,"CustParentCountry",getFieldLabel("CustParentCountry"),
					core.getCustomer().getCustParentCountry(),pff.getCustomer().getCustParentCountry());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustRiskCountry()).equals(core.getCustomer().getCustRiskCountry())){
			addCustFieldToUpdateList(listGroupCustomer,"CustRiskCountry",getFieldLabel("CustRiskCountry"),
					core.getCustomer().getCustRiskCountry(),pff.getCustomer().getCustRiskCountry());
		}
		if(pff.getCustomer().getCustDOB() != null && core.getCustomer().getCustDOB() != null && 
				pff.getCustomer().getCustDOB().compareTo(core.getCustomer().getCustDOB()) != 0){
			addCustFieldToUpdateList(listGroupCustomer,"CustDOB",getFieldLabel("CustDOB"),
					DateUtility.formatUtilDate(core.getCustomer().getCustDOB(),PennantConstants.dateFormate),
					DateUtility.formatUtilDate(pff.getCustomer().getCustDOB(),PennantConstants.dateFormate));
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustSalutationCode()).equals(core.getCustomer().getCustSalutationCode())){
			addCustFieldToUpdateList(listGroupCustomer,"CustSalutationCode",getFieldLabel("CustSalutationCode"),
					core.getCustomer().getCustSalutationCode(),pff.getCustomer().getCustSalutationCode());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustGenderCode()).equals(core.getCustomer().getCustGenderCode())){
			addCustFieldToUpdateList(listGroupCustomer,"CustGenderCode",getFieldLabel("CustGenderCode"),
					core.getCustomer().getCustGenderCode(),pff.getCustomer().getCustGenderCode());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustRO1()).equals(core.getCustomer().getCustRO1())){
			addCustFieldToUpdateList(listGroupCustomer,"CustRO1",getFieldLabel("CustRO1"),
					core.getCustomer().getCustRO1(),pff.getCustomer().getCustRO1());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustCOB()).equals(core.getCustomer().getCustCOB())){
			addCustFieldToUpdateList(listGroupCustomer,"CustCOB",getFieldLabel("CustCOB"),
					core.getCustomer().getCustCOB(),pff.getCustomer().getCustCOB());
		}
		
		// Commented below CustSector and CustSubSector due to miss match of Subsector codes in Equation and PFF
		
		/*if(!StringUtils.trimToEmpty(pff.getCustomer().getCustSector()).equals(core.getCustomer().getCustSector())){
			addCustFieldToUpdateList(listGroupCustomer,"CustSector",getFieldLabel("CustSector"),
					core.getCustomer().getCustSector(),pff.getCustomer().getCustSector());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustSubSector()).equals(core.getCustomer().getCustSubSector())){
			addCustFieldToUpdateList(listGroupCustomer,"CustSubSector",getFieldLabel("CustSubSector"),
					core.getCustomer().getCustSubSector(),pff.getCustomer().getCustSubSector());
		}*/
		
		if(pff.getCustomer().isCustIsMinor() != core.getCustomer().isCustIsMinor()){
			addCustFieldToUpdateList(listGroupCustomer,"CustIsMinor",getFieldLabel("CustIsMinor"),
					String.valueOf(core.getCustomer().isCustIsMinor()),String.valueOf(pff.getCustomer().isCustIsMinor()));
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustPOB()).equals(core.getCustomer().getCustPOB())){
			addCustFieldToUpdateList(listGroupCustomer,"CustPOB",getFieldLabel("CustPOB"),
					core.getCustomer().getCustPOB(),pff.getCustomer().getCustPOB());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustPassportNo()).equals(core.getCustomer().getCustPassportNo())){
			addCustFieldToUpdateList(listGroupCustomer,"CustPassportNo",getFieldLabel("CustPassportNo"),
					core.getCustomer().getCustPassportNo(),pff.getCustomer().getCustPassportNo());
		}
		if(pff.getCustomer().getCustPassportExpiry() != null && core.getCustomer().getCustPassportExpiry() != null && 
				pff.getCustomer().getCustPassportExpiry().compareTo(core.getCustomer().getCustPassportExpiry()) != 0){
			addCustFieldToUpdateList(listGroupCustomer,"CustPassportExpiry",getFieldLabel("CustPassportExpiry"),
					DateUtility.formatUtilDate(core.getCustomer().getCustPassportExpiry(),PennantConstants.dateFormate),
					DateUtility.formatUtilDate(pff.getCustomer().getCustPassportExpiry(),PennantConstants.dateFormate));
		}
		if(pff.getCustomer().isCustIsTradeFinCust() != core.getCustomer().isCustIsTradeFinCust()){
			addCustFieldToUpdateList(listGroupCustomer,"CustIsTradeFinCust",getFieldLabel("CustIsTradeFinCust"),
					String.valueOf(core.getCustomer().isCustIsTradeFinCust()),String.valueOf(pff.getCustomer().isCustIsTradeFinCust()));
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustNationality()).equals(core.getCustomer().getCustNationality())){
			addCustFieldToUpdateList(listGroupCustomer,"CustNationality",getFieldLabel("CustNationality"),
					core.getCustomer().getCustNationality(),pff.getCustomer().getCustNationality());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustMaritalSts()).equals(core.getCustomer().getCustMaritalSts())){
			addCustFieldToUpdateList(listGroupCustomer,"CustMaritalSts",getFieldLabel("CustMaritalSts"),
					core.getCustomer().getCustMaritalSts(),pff.getCustomer().getCustMaritalSts());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustEmpSts()).equals(core.getCustomer().getCustEmpSts())){
			addCustFieldToUpdateList(listGroupCustomer,"CustEmpSts",getFieldLabel("CustEmpSts"),
					core.getCustomer().getCustEmpSts(),pff.getCustomer().getCustEmpSts());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustBaseCcy()).equals(core.getCustomer().getCustBaseCcy())){
			addCustFieldToUpdateList(listGroupCustomer,"CustBaseCcy",getFieldLabel("CustBaseCcy"),
					core.getCustomer().getCustBaseCcy(),pff.getCustomer().getCustBaseCcy());
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustResdCountry()).equals(core.getCustomer().getCustResdCountry())){
			addCustFieldToUpdateList(listGroupCustomer,"CustResdCountry",getFieldLabel("CustResdCountry"),
					core.getCustomer().getCustResdCountry(),pff.getCustomer().getCustResdCountry());
		}
		if(pff.getCustomer().getCustClosedOn() != null && core.getCustomer().getCustClosedOn() != null &&
				pff.getCustomer().getCustClosedOn().compareTo(core.getCustomer().getCustClosedOn()) != 0){
			addCustFieldToUpdateList(listGroupCustomer,"CustClosedOn",getFieldLabel("CustClosedOn"),
					DateUtility.formatUtilDate(core.getCustomer().getCustClosedOn(),PennantConstants.dateFormate),
					DateUtility.formatUtilDate(pff.getCustomer().getCustClosedOn(),PennantConstants.dateFormate));
		}
		if(pff.getCustomer().getCustFirstBusinessDate().compareTo(core.getCustomer().getCustFirstBusinessDate()) != 0){
			addCustFieldToUpdateList(listGroupCustomer,"CustFirstBusinessDate",getFieldLabel("CustFirstBusinessDate"),
					DateUtility.formatUtilDate(core.getCustomer().getCustFirstBusinessDate(),PennantConstants.dateFormate),
					DateUtility.formatUtilDate(pff.getCustomer().getCustFirstBusinessDate(),PennantConstants.dateFormate));
		}
		if(!StringUtils.trimToEmpty(pff.getCustomer().getCustRelation()).equals(core.getCustomer().getCustRelation())){
			addCustFieldToUpdateList(listGroupCustomer,"CustRelation",getFieldLabel("CustRelation"),
					core.getCustomer().getCustRelation(),pff.getCustomer().getCustRelation());
		}
		if(pff.getCustomer().isCustIsClosed() != core.getCustomer().isCustIsClosed()){
			addCustFieldToUpdateList(listGroupCustomer,"CustIsClosed",getFieldLabel("CustIsClosed"),
					String.valueOf(core.getCustomer().isCustIsClosed()),String.valueOf(pff.getCustomer().isCustIsClosed()));
		}
		if(pff.getCustomer().isCustIsBlocked() != core.getCustomer().isCustIsBlocked()){
			addCustFieldToUpdateList(listGroupCustomer,"CustIsBlocked",getFieldLabel("CustIsBlocked"),
					String.valueOf(core.getCustomer().isCustIsBlocked()),String.valueOf(pff.getCustomer().isCustIsBlocked()));
		}
		if(pff.getCustomer().isCustIsActive() != core.getCustomer().isCustIsActive()){
			addCustFieldToUpdateList(listGroupCustomer,"CustIsActive",getFieldLabel("CustIsActive"),
					String.valueOf(core.getCustomer().isCustIsActive()),String.valueOf(pff.getCustomer().isCustIsActive()));
		}
		if(pff.getCustomer().isCustIsDecease() != core.getCustomer().isCustIsDecease()){
			addCustFieldToUpdateList(listGroupCustomer,"CustIsDecease",getFieldLabel("CustIsDecease"),
					String.valueOf(core.getCustomer().isCustIsDecease()),String.valueOf(pff.getCustomer().isCustIsDecease()));
		}
		if(pff.getCustomer().getCustTotalIncome().compareTo(core.getCustomer().getCustTotalIncome()) != 0){
			Currency coreCurrency = PennantAppUtil.getCurrencyBycode(core.getCustomer().getCustBaseCcy());
			Currency pffCurrency = PennantAppUtil.getCurrencyBycode(pff.getCustomer().getCustBaseCcy());
			Listitem item = new Listitem();
			Listcell lc;
			item.setId("CustTotalIncome");
			lc = new Listcell(getFieldLabel("CustTotalIncome"));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formateAmount(core.getCustomer().getCustTotalIncome(),
					coreCurrency.getCcyEditField()).toString());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formateAmount(pff.getCustomer().getCustTotalIncome(),
					pffCurrency.getCcyEditField()).toString());
			lc.setParent(item);
			lc = new Listcell(PennantConstants.RCD_UPD);
			lc.setParent(item);
			this.listBoxUpdateCustomer.appendChild(item);
		}

		logger.debug("Leaving");
	}

	private void processAddressDetails(CustomerDetails core,CustomerDetails pff){
		logger.debug("Entering");

		List<CustomerAddres> saveCustAddressList = new ArrayList<CustomerAddres>();
		EquationMasterMissedDetail masterMissedDetail;

		List<AddressType> addressTypeMasterList = getAddressTypeMasterDetails();

		if(core.getAddressList() != null && !core.getAddressList().isEmpty()){
			for (int count=1;count<=core.getAddressList().size();count++) {
				CustomerAddres coreCustAddrs = core.getAddressList().get(count-1);
				if(valueExistInAddressTypeMaster(coreCustAddrs.getCustAddrType(),addressTypeMasterList)){
					CustomerAddres pffCustAddrs = customerAddressAlreadyExist(coreCustAddrs,pff.getAddressList());
					if(pffCustAddrs != null){

						Listgroup listgroup = new Listgroup(getGroupLabel(listGroupAddress)+" : "+
								Labels.getLabel("label_CustomerAddresDialog_CustAddrType.value")+" - "+coreCustAddrs.getCustAddrType());
						listgroup.setId(listGroupAddress+coreCustAddrs.getCustAddrType());
						listgroup.setCheckable(true);
						listgroup.setAttribute("data", coreCustAddrs);
						this.listBoxUpdateCustomer.appendChild(listgroup);

						boolean createAddressGroup = false;

						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrHNbr()).equals(pffCustAddrs.getCustAddrHNbr())){
							addChildFieldToUpdateList(listGroupAddress,"CustAddrHNbr",getFieldLabel("CustAddrHNbr"),
									coreCustAddrs.getCustAddrHNbr(),pffCustAddrs.getCustAddrHNbr());
							createAddressGroup = true;
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustFlatNbr()).equals(pffCustAddrs.getCustFlatNbr())){
							addChildFieldToUpdateList(listGroupAddress,"CustFlatNbr",getFieldLabel("CustFlatNbr"),
									coreCustAddrs.getCustFlatNbr(),pffCustAddrs.getCustFlatNbr());
							createAddressGroup = true;
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrStreet()).equals(pffCustAddrs.getCustAddrStreet())){
							addChildFieldToUpdateList(listGroupAddress,"CustAddrStreet",getFieldLabel("CustAddrStreet"),
									coreCustAddrs.getCustAddrStreet(),pffCustAddrs.getCustAddrStreet());
							createAddressGroup = true;
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrLine1()).equals(pffCustAddrs.getCustAddrLine1())){
							addChildFieldToUpdateList(listGroupAddress,"CustAddrLine1",getFieldLabel("CustAddrLine1"),
									coreCustAddrs.getCustAddrLine1(),pffCustAddrs.getCustAddrLine1());
							createAddressGroup = true;
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrLine2()).equals(pffCustAddrs.getCustAddrLine2())){
							addChildFieldToUpdateList(listGroupAddress,"CustAddrLine2",getFieldLabel("CustAddrLine2"),
									coreCustAddrs.getCustAddrLine2(),pffCustAddrs.getCustAddrLine2());
							createAddressGroup = true;
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrZIP()).equals(pffCustAddrs.getCustAddrZIP())){
							addChildFieldToUpdateList(listGroupAddress,"CustAddrZIP",getFieldLabel("CustAddrZIP"),
									coreCustAddrs.getCustAddrZIP(),pffCustAddrs.getCustAddrZIP());
							createAddressGroup = true;
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrPhone()).equals(pffCustAddrs.getCustAddrPhone())){
							addChildFieldToUpdateList(listGroupAddress,"CustAddrPhone",getFieldLabel("CustAddrPhone"),
									coreCustAddrs.getCustAddrPhone(),pffCustAddrs.getCustAddrPhone());
							createAddressGroup = true;
						}
						if(!createAddressGroup && this.listBoxUpdateCustomer.getFellowIfAny(
								listGroupAddress+coreCustAddrs.getCustAddrType()) != null){
							this.listBoxUpdateCustomer.getFellowIfAny(
									listGroupAddress+coreCustAddrs.getCustAddrType()).detach();
						}
					}else{
						coreCustAddrs.setVersion(1);
						coreCustAddrs.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						coreCustAddrs.setRecordType("");
						coreCustAddrs.setLastMntBy(1000);
						coreCustAddrs.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						coreCustAddrs.setRoleCode("");
						coreCustAddrs.setNextRoleCode("");
						coreCustAddrs.setTaskId("");
						coreCustAddrs.setNextTaskId("");
						coreCustAddrs.setWorkflowId(0);

						saveCustAddressList.add(coreCustAddrs);

						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrType()).equals("")){
							addFieldToNewList(listGroupAddress,getFieldLabel("CustAddrType"),
									coreCustAddrs.getCustAddrType(),count);
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrHNbr()).equals("")){
							addFieldToNewList(listGroupAddress,getFieldLabel("CustAddrHNbr"),
									coreCustAddrs.getCustAddrHNbr(),count);
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustFlatNbr()).equals("")){
							addFieldToNewList(listGroupAddress,getFieldLabel("CustFlatNbr"),
									coreCustAddrs.getCustFlatNbr(),count);
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrStreet()).equals("")){
							addFieldToNewList(listGroupAddress,getFieldLabel("CustAddrStreet"),
									coreCustAddrs.getCustAddrStreet(),count);
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrLine1()).equals("")){
							addFieldToNewList(listGroupAddress,getFieldLabel("CustAddrLine1"),
									coreCustAddrs.getCustAddrLine1(),count);
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrLine2()).equals("")){
							addFieldToNewList(listGroupAddress,getFieldLabel("CustAddrLine2"),
									coreCustAddrs.getCustAddrLine2(),count);
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrZIP()).equals("")){
							addFieldToNewList(listGroupAddress,getFieldLabel("CustAddrZIP"),
									coreCustAddrs.getCustAddrZIP(),count);
						}
						if(!StringUtils.trimToEmpty(coreCustAddrs.getCustAddrPhone()).equals("")){
							addFieldToNewList(listGroupAddress,getFieldLabel("CustAddrPhone"),
									coreCustAddrs.getCustAddrPhone(),count);
						}
					}
				}else{
					masterMissedDetail = new EquationMasterMissedDetail();
					masterMissedDetail.setModule("AddressDetails");
					masterMissedDetail.setLastMntOn(dateAppDate);
					masterMissedDetail.setFieldName("CustAddrType");
					masterMissedDetail.setDescription("Customer : "+core.getCustomer().getCustCIF()+" , '"+coreCustAddrs.getCustAddrType()+"' Value Does Not Exist In Master BMTAddressTypes Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
				}
			}
		}
		this.saveCustomerChildDetails.setAddressList(saveCustAddressList);
		logger.debug("Leaving");
	}


	private void processPhoneNumberDetails(CustomerDetails core,CustomerDetails pff){
		logger.debug("Entering");
		List<CustomerPhoneNumber> saveCustPhoneNumList = new ArrayList<CustomerPhoneNumber>();
		if(core.getCustomerPhoneNumList() != null && !core.getCustomerPhoneNumList().isEmpty()){
			for (int count=1;count<=core.getCustomerPhoneNumList().size();count++) {
				CustomerPhoneNumber coreCustPhoneNum = core.getCustomerPhoneNumList().get(count-1);

				CustomerPhoneNumber pffCustPhoneNum = customerPhoneNumberAlreadyExist(coreCustPhoneNum,pff.getCustomerPhoneNumList());

				if(pffCustPhoneNum != null){

					if(!StringUtils.trimToEmpty(coreCustPhoneNum.getPhoneNumber()).equals(pffCustPhoneNum.getPhoneNumber())){
						Listgroup listgroup = new Listgroup(getGroupLabel(listGroupPhoneNumber)+" : "+
								Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneTypeCode.value")+" - "+pffCustPhoneNum.getPhoneTypeCode());
						listgroup.setId(listGroupPhoneNumber+pffCustPhoneNum.getPhoneTypeCode());
						listgroup.setCheckable(true);
						listgroup.setAttribute("data", coreCustPhoneNum);
						this.listBoxUpdateCustomer.appendChild(listgroup);

						addChildFieldToUpdateList(listGroupPhoneNumber,"PhoneNumber",getFieldLabel("PhoneNumber"),
								coreCustPhoneNum.getPhoneNumber(),pffCustPhoneNum.getPhoneNumber());
					}
				}else{
					coreCustPhoneNum.setVersion(1);
					coreCustPhoneNum.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					coreCustPhoneNum.setRecordType("");
					coreCustPhoneNum.setLastMntBy(1000);
					coreCustPhoneNum.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					coreCustPhoneNum.setRoleCode("");
					coreCustPhoneNum.setNextRoleCode("");
					coreCustPhoneNum.setTaskId("");
					coreCustPhoneNum.setNextTaskId("");
					coreCustPhoneNum.setWorkflowId(0);

					saveCustPhoneNumList.add(coreCustPhoneNum);

					addFieldToNewList(listGroupPhoneNumber,getFieldLabel("PhoneTypeCode"),
							coreCustPhoneNum.getPhoneTypeCode(),count);
					addFieldToNewList(listGroupPhoneNumber,getFieldLabel("PhoneNumber"),
							coreCustPhoneNum.getPhoneNumber(),count);
				}
			}
		}
		this.saveCustomerChildDetails.setCustomerPhoneNumList(saveCustPhoneNumList);
		logger.debug("Leaving");
	}


	private void processEmailDetails(CustomerDetails core,CustomerDetails pff){
		logger.debug("Entering");
		List<CustomerEMail> saveCustEmailList = new ArrayList<CustomerEMail>();
		EquationMasterMissedDetail masterMissedDetail;

		List<EMailType> emailTypeMasterList = getEmailTypeMasterDetails();

		if(core.getCustomerEMailList() != null && !core.getCustomerEMailList().isEmpty()){

			for (int count=1;count<=core.getCustomerEMailList().size();count++) {
				CustomerEMail coreCustEmail = core.getCustomerEMailList().get(count-1);

				if(valueExistInEmailTypeMaster(coreCustEmail.getCustEMailTypeCode(),emailTypeMasterList)){

					CustomerEMail pffCustEmail = customerEmailAlreadyExist(coreCustEmail,pff.getCustomerEMailList());

					if(pffCustEmail != null){

						if(!StringUtils.trimToEmpty(coreCustEmail.getCustEMail()).equals(pffCustEmail.getCustEMail())){

							Listgroup listgroup = new Listgroup(getGroupLabel(listGroupEmail)+" : "+
									Labels.getLabel("label_CustomerEMailDialog_CustEMailTypeCode.value")+" - "+coreCustEmail.getCustEMailTypeCode());
							listgroup.setId(listGroupEmail+coreCustEmail.getCustEMailTypeCode());
							listgroup.setCheckable(true);
							listgroup.setAttribute("data", coreCustEmail);
							this.listBoxUpdateCustomer.appendChild(listgroup);

							addChildFieldToUpdateList(listGroupEmail,"CustEMail",getFieldLabel("CustEMail"),
									coreCustEmail.getCustEMail(),pffCustEmail.getCustEMail());
						}
					}else{
						coreCustEmail.setVersion(1);
						coreCustEmail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						coreCustEmail.setRecordType("");
						coreCustEmail.setLastMntBy(1000);
						coreCustEmail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						coreCustEmail.setRoleCode("");
						coreCustEmail.setNextRoleCode("");
						coreCustEmail.setTaskId("");
						coreCustEmail.setNextTaskId("");
						coreCustEmail.setWorkflowId(0);

						saveCustEmailList.add(coreCustEmail);

						addFieldToNewList(listGroupEmail,getFieldLabel("CustEMailTypeCode"),
								coreCustEmail.getCustEMailTypeCode(),count);
						addFieldToNewList(listGroupEmail,getFieldLabel("CustEMail"),
								coreCustEmail.getCustEMail(),count);	
					}
				}
				else{
					masterMissedDetail = new EquationMasterMissedDetail();
					masterMissedDetail.setModule("EmailDetails");
					masterMissedDetail.setLastMntOn(dateAppDate);
					masterMissedDetail.setFieldName("CustEMailTypeCode");
					masterMissedDetail.setDescription("Customer : "+core.getCustomer().getCustCIF()+" , '"+coreCustEmail.getCustEMailTypeCode()+"' Value Does Not Exist In Master BMTEMailTypes Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
				}
			}
		}
		this.saveCustomerChildDetails.setCustomerEMailList(saveCustEmailList);
		logger.debug("Leaving");
	}


	private void processRatingDetails(CustomerDetails core,CustomerDetails pff){
		logger.debug("Entering");

		List<CustomerRating> saveCustRatingList = new ArrayList<CustomerRating>();
		EquationMasterMissedDetail masterMissedDetail;

		List<RatingType> ratingTypeMasterList = getRatingTypeMasterDetails();

		if(core.getRatingsList() != null && !core.getRatingsList().isEmpty()){

			for (int count=1;count<=core.getRatingsList().size();count++) {
				CustomerRating coreCustRating = core.getRatingsList().get(count-1);

				if(valueExistInRatingMaster(coreCustRating,ratingTypeMasterList)){
					CustomerRating pffCustRating = customerRatingAlreadyExist(coreCustRating,pff.getRatingsList());

					if(pffCustRating != null){

						if(!StringUtils.trimToEmpty(coreCustRating.getCustRatingCode()).equals(pffCustRating.getCustRatingCode()) || 
								!StringUtils.trimToEmpty(coreCustRating.getCustRating()).equals(pffCustRating.getCustRating())){
							Listgroup listgroup = new Listgroup(getGroupLabel(listGroupRating)+" : "+
									Labels.getLabel("label_CustomerRatingDialog_CustRatingType.value")+" - "+coreCustRating.getCustRatingType());
							listgroup.setId(listGroupRating+coreCustRating.getCustRatingType());
							listgroup.setCheckable(true);
							listgroup.setAttribute("data", coreCustRating);
							this.listBoxUpdateCustomer.appendChild(listgroup);
						}

						if(!StringUtils.trimToEmpty(coreCustRating.getCustRatingCode()).equals(pffCustRating.getCustRatingCode())){
							addChildFieldToUpdateList(listGroupRating,"CustRatingCode",Labels.getLabel("label_CustomerRatingDialog_CustRatingCode.value"),
									coreCustRating.getCustRatingCode(),pffCustRating.getCustRatingCode());
						}
						if(!StringUtils.trimToEmpty(coreCustRating.getCustRating()).equals(pffCustRating.getCustRating())){
							addChildFieldToUpdateList(listGroupRating,"CustRating",Labels.getLabel("label_CustomerRatingDialog_CustRating.value"),
									coreCustRating.getCustRating(),pffCustRating.getCustRating());
						}
					}else{
						coreCustRating.setVersion(1);
						coreCustRating.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						coreCustRating.setRecordType("");
						coreCustRating.setLastMntBy(1000);
						coreCustRating.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						coreCustRating.setRoleCode("");
						coreCustRating.setNextRoleCode("");
						coreCustRating.setTaskId("");
						coreCustRating.setNextTaskId("");
						coreCustRating.setWorkflowId(0);

						saveCustRatingList.add(coreCustRating);

						addFieldToNewList(listGroupRating,Labels.getLabel("label_CustomerRatingDialog_CustRatingType.value"),
								coreCustRating.getCustRatingType(),count);
						addFieldToNewList(listGroupRating,Labels.getLabel("label_CustomerRatingDialog_CustRatingCode.value"),
								coreCustRating.getCustRatingCode(),count);
						addFieldToNewList(listGroupRating,Labels.getLabel("label_CustomerRatingDialog_CustRating.value"),
								coreCustRating.getCustRating(),count);
					}
				}else{
					masterMissedDetail = new EquationMasterMissedDetail();
					masterMissedDetail.setModule("RatingDetails");
					masterMissedDetail.setLastMntOn(dateAppDate);
					masterMissedDetail.setFieldName("CustRatingType");
					masterMissedDetail.setDescription("Customer : "+core.getCustomer().getCustCIF()+" , '"+coreCustRating.getCustRatingType()+"' Value Does Not Exist In Master BMTRatingTypes Table ");
					masterValueMissedDetails.add(masterMissedDetail);	
				}
			}
		}
		this.saveCustomerChildDetails.setRatingsList(saveCustRatingList);
		logger.debug("Leaving");
	}


	private void updateCustomerDetails(){
		logger.debug("Entering");
		if(this.listBoxUpdateCustomer.getGroups() != null && !this.listBoxUpdateCustomer.getGroups().isEmpty()){

			for (Listgroup listgroup : this.listBoxUpdateCustomer.getGroups()) {
				String moduleName = listgroup.getId();
				String updateQuery = ""; 

				for (Listitem listitem : listgroup.getItems()) {
					if(listitem.isSelected()){
						if(moduleName.equals(listGroupCustomer)){
							updateQuery = updateQuery + listitem.getId() + " = :"+listitem.getId()+", "  ;
						}else{
							Listcell listcell = (Listcell)listitem.getFirstChild();
							updateQuery = updateQuery + listcell.getLabel() + " = :"+listcell.getLabel()+", "  ;
						}
					}
				}
				if(!updateQuery.equals("")){
					updateQuery = updateQuery.substring(0, updateQuery.trim().length()-1);
					if(moduleName.contains(listGroupCustomer)){
						updateQuery = "Update  Customers  Set "+ updateQuery + " Where CustID = :CustID";
						getDailyDownloadInterfaceService().updateObjectDetails(updateQuery,coreCustomerDetails.getCustomer());
					}else if(moduleName.contains(listGroupAddress)){
						updateQuery = "Update  CustomerAddresses  Set "+ updateQuery  + " Where CustID = :CustID And CustAddrType = :CustAddrType";
						CustomerAddres customerAddres = (CustomerAddres)listgroup.getAttribute("data");
						getDailyDownloadInterfaceService().updateObjectDetails(updateQuery,customerAddres);
					}else if(moduleName.contains(listGroupPhoneNumber)){
						updateQuery = "Update  CustomerPhoneNumbers Set "+ updateQuery  + " Where PhoneCustID = :PhoneCustID And PhoneTypeCode = :PhoneTypeCode";
						CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber)listgroup.getAttribute("data");
						getDailyDownloadInterfaceService().updateObjectDetails(updateQuery,customerPhoneNumber);
					}else if(moduleName.contains(listGroupEmail)){
						updateQuery = "Update  CustomerEMails  Set "+ updateQuery  + " Where CustID = :CustID And CustEMailTypeCode = :CustEMailTypeCode";
						CustomerEMail customerEMail = (CustomerEMail)listgroup.getAttribute("data");
						getDailyDownloadInterfaceService().updateObjectDetails(updateQuery,customerEMail);
					}else if(moduleName.contains(listGroupRating)){
						updateQuery = "Update  CustomerRatings  Set "+ updateQuery  + " Where CustID = :CustID And CustRatingType = :CustRatingType";
						CustomerRating customerRating = (CustomerRating)listgroup.getAttribute("data");
						getDailyDownloadInterfaceService().updateObjectDetails(updateQuery,customerRating);
					}
				}
			}
		}
		logger.debug("Leaving");
	}



	private List<AddressType> getAddressTypeMasterDetails(){
		logger.debug("Entering");
		JdbcSearchObject<AddressType> jdbcSearchObject = new JdbcSearchObject<AddressType>(AddressType.class);
		jdbcSearchObject.addTabelName("BMTAddressTypes");
		logger.debug("Leaving");
		return  getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	private List<EMailType> getEmailTypeMasterDetails(){
		logger.debug("Entering");
		JdbcSearchObject<EMailType> jdbcSearchObject = new JdbcSearchObject<EMailType>(EMailType.class);
		jdbcSearchObject.addTabelName("BMTEMailTypes");
		logger.debug("Leaving");
		return  getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	private List<RatingType> getRatingTypeMasterDetails(){
		logger.debug("Entering");
		JdbcSearchObject<RatingType> jdbcSearchObject = new JdbcSearchObject<RatingType>(RatingType.class);
		jdbcSearchObject.addTabelName("BMTRatingTypes");
		logger.debug("Leaving");
		return  getPagedListService().getBySearchObject(jdbcSearchObject);
	}


	private boolean valueExistInAddressTypeMaster(String field,List<AddressType> addressTypes){
		for (AddressType addressType : addressTypes) {
			if(StringUtils.trimToEmpty(field).equalsIgnoreCase(addressType.getAddrTypeCode())){
				return true;
			}
		}
		return false;
	}

	private boolean valueExistInEmailTypeMaster(String field,List<EMailType> customerEMails){
		for (EMailType eMailType : customerEMails) {
			if(StringUtils.trimToEmpty(field).equalsIgnoreCase(eMailType.getEmailTypeCode())){
				return true;
			}
		}
		return false;
	}

	private boolean valueExistInRatingMaster(CustomerRating coreCustRating,List<RatingType> ratingTypes){
		for (RatingType ratingType : ratingTypes) {
			if(StringUtils.trimToEmpty(coreCustRating.getCustRatingType()).equalsIgnoreCase(ratingType.getRatingType())){
				return true;
			}
		}
		return false;
	}

	private CustomerAddres customerAddressAlreadyExist(CustomerAddres customerAddres,List<CustomerAddres> list){
		for (CustomerAddres cAddres : list) {
			if(StringUtils.trimToEmpty(customerAddres.getCustAddrType()).equalsIgnoreCase(cAddres.getCustAddrType())){
				return cAddres;
			}
		}
		return null;
	}

	private CustomerPhoneNumber customerPhoneNumberAlreadyExist(CustomerPhoneNumber customerPhoneNumber,List<CustomerPhoneNumber> cPhoneNumbers){
		for (CustomerPhoneNumber cPhoneNumber : cPhoneNumbers) {
			if(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneTypeCode()).equalsIgnoreCase(cPhoneNumber.getPhoneTypeCode())){
				return cPhoneNumber;
			}
		}
		return null;
	}

	private CustomerEMail customerEmailAlreadyExist(CustomerEMail customerEMail,List<CustomerEMail> list){
		for (CustomerEMail cEMail : list) {
			if(StringUtils.trimToEmpty(customerEMail.getCustEMailTypeCode()).equalsIgnoreCase(cEMail.getCustEMailTypeCode())){
				return cEMail;
			}
		}
		return null;
	}

	private CustomerRating customerRatingAlreadyExist(CustomerRating customerRating,List<CustomerRating> list){
		for (CustomerRating cRating : list) {
			if(StringUtils.trimToEmpty(customerRating.getCustRatingType()).equalsIgnoreCase(cRating.getCustRatingType())){
				return cRating;
			}
		}
		return null;
	}


	private String getFieldLabel(String field){
	
		if(field.equalsIgnoreCase("CustCtgCode")){
			return Labels.getLabel("label_CustomerDialog_CustCtgCode.value");
		}
		if(field.equalsIgnoreCase("CustShrtName")){
			return Labels.getLabel("label_CustomerDialog_CustShrtName.value");
		}
		if(field.equalsIgnoreCase("CustCoreBank")){
			return Labels.getLabel("label_CustomerDialog_CustCoreBank.value");
		}
		if(field.equalsIgnoreCase("CustFName")){
			return "Customer Full Name";
		}
		if(field.equalsIgnoreCase("CustFNameLclLng")){
			return "Customer Full Name Local Language";
		}
		if(field.equalsIgnoreCase("CustShrtNameLclLng")){
			return "Customer Short Name Local Language";
		}
		if(field.equalsIgnoreCase("CustTypeCode")){
			return Labels.getLabel("label_CustomerDialog_CustTypeCode.value");
		}
		if(field.equalsIgnoreCase("CustDftBranch")){
			return Labels.getLabel("label_CustomerDialog_CustDftBranch.value");
		}
		if(field.equalsIgnoreCase("CustGroupID")){
			return Labels.getLabel("label_CustomerDialog_CustGroupID.value");
		}
		if(field.equalsIgnoreCase("CustParentCountry")){
			return Labels.getLabel("label_CustomerDialog_CustParentCountry.value");
		}
		if(field.equalsIgnoreCase("CustRiskCountry")){
			return Labels.getLabel("label_CustomerDialog_CustRiskCountry.value");
		}
		if(field.equalsIgnoreCase("CustDOB")){
			return Labels.getLabel("label_CustomerDialog_CustDOB.value");
		}
		if(field.equalsIgnoreCase("CustSalutationCode")){
			return Labels.getLabel("label_CustomerDialog_CustSalutationCode.value");
		}
		if(field.equalsIgnoreCase("CustGenderCode")){
			return Labels.getLabel("label_CustomerDialog_CustGenderCode.value");
		}
		if(field.equalsIgnoreCase("CustRO1")){
			return Labels.getLabel("label_CustomerDialog_CustRO1.value");
		}
		if(field.equalsIgnoreCase("CustCOB")){
			return "Customer COB";
		}
		if(field.equalsIgnoreCase("CustSector")){
			return Labels.getLabel("label_CustomerDialog_CustSector.value");
		}
		if(field.equalsIgnoreCase("CustSubSector")){
			return Labels.getLabel("label_CustomerDialog_CustSubSector.value");
		}
		if(field.equalsIgnoreCase("CustIsMinor")){
			return "Customer Is Minor";
		}
		if(field.equalsIgnoreCase("CustPOB")){
			return "Customer POB";
		}
		if(field.equalsIgnoreCase("CustPassportNo")){
			return "Customer Passport Number";
		}
		if(field.equalsIgnoreCase("CustPassportExpiry")){
			return "Customer Passport Expiry";
		}
		if(field.equalsIgnoreCase("CustIsTradeFinCust")){
			return "Customer Is TradeFinCust";
		}
		if(field.equalsIgnoreCase("CustNationality")){
			return Labels.getLabel("label_CustomerDialog_CustNationality.value");
		}
		if(field.equalsIgnoreCase("CustMaritalSts")){
			return Labels.getLabel("label_CustomerDialog_CustMaritalSts.value");
		}
		if(field.equalsIgnoreCase("CustEmpSts")){
			return Labels.getLabel("label_CustomerDialog_CustEmpSts.value");
		}
		if(field.equalsIgnoreCase("CustBaseCcy")){
			return Labels.getLabel("label_CustomerDialog_CustBaseCcy.value");
		}
		if(field.equalsIgnoreCase("CustResdCountry")){
			return "Customer Residence Country";
		}
		if(field.equalsIgnoreCase("CustClosedOn")){
			return "Customer Closed On";
		}
		if(field.equalsIgnoreCase("CustFirstBusinessDate")){
			return "First Business Date";
		}
		if(field.equalsIgnoreCase("CustRelation")){
			return Labels.getLabel("label_CustomerDialog_CustRelation.value");
		}
		if(field.equalsIgnoreCase("CustIsClosed")){
			return "Customer Is Closed";
		}
		if(field.equalsIgnoreCase("CustIsBlocked")){
			return "Customer Blocked";
		}
		if(field.equalsIgnoreCase("CustIsActive")){
			return "Customer Is Active";
		}
		if(field.equalsIgnoreCase("CustIsDecease")){
			return "Customer Is Deceased";
		}
		if(field.equalsIgnoreCase("CustTotalIncome")){
			return "Customer Total Income";
		}
		
		//Address Details
		
		if(field.equalsIgnoreCase("CustAddrType")){
			return Labels.getLabel("label_CustomerAddresDialog_CustAddrType.value");
		}
		if(field.equalsIgnoreCase("CustAddrHNbr")){
			return Labels.getLabel("label_CustomerAddresDialog_CustAddrHNbr.value");
		}
		if(field.equalsIgnoreCase("CustFlatNbr")){
			return Labels.getLabel("label_CustomerAddresDialog_CustFlatNbr.value");
		}
		if(field.equalsIgnoreCase("CustAddrStreet")){
			return Labels.getLabel("label_CustomerAddresDialog_CustAddrStreet.value");
		}
		if(field.equalsIgnoreCase("CustAddrLine1")){
			return Labels.getLabel("label_CustomerAddresDialog_CustAddrLine1.value");
		}
		if(field.equalsIgnoreCase("CustAddrLine2")){
			return Labels.getLabel("label_CustomerAddresDialog_CustAddrLine2.value");
		}
		if(field.equalsIgnoreCase("CustAddrZIP")){
			return Labels.getLabel("label_CustomerAddresDialog_CustAddrZIP.value");
		}
		if(field.equalsIgnoreCase("CustAddrPhone")){
			return Labels.getLabel("label_CustomerAddresDialog_CustAddrPhone.value");
		}
		
		//Phone Details
		
		if(field.equalsIgnoreCase("PhoneTypeCode")){
			return Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneTypeCode.value");
		}
		if(field.equalsIgnoreCase("PhoneNumber")){
			return Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value");
		}
		//Email Details
		if(field.equalsIgnoreCase("CustEMailTypeCode")){
			return Labels.getLabel("label_CustomerEMailDialog_CustEMailTypeCode.value");
		}
		if(field.equalsIgnoreCase("CustEMail")){
			return Labels.getLabel("label_CustomerEMailDialog_CustEMail.value");
		}
		return "";
	}
	
	private void addFieldToNewList(String listGroup,String label,String coreValue,int count){
		logger.debug("Entering");
		if(this.listBoxNewCustomer.getGroups() != null && !this.listBoxNewCustomer.getGroups().toString().contains(listGroup+count)){
			Listgroup listgroup = new Listgroup(getGroupLabel(listGroup)+" : "+String.valueOf(count));
			listgroup.setId(listGroup+count);
			listgroup.setCheckable(true);
			this.listBoxNewCustomer.appendChild(listgroup);
		}
		listitem = new Listitem();
		listcell = new Listcell(label);
		listcell.setParent(listitem);
		listcell = new Listcell(coreValue);
		listcell.setParent(listitem);
		this.listBoxNewCustomer.appendChild(listitem);
		logger.debug("Leaving");
	}


	private void addCustFieldToUpdateList(String listGroup,String fieldID,String label,String coreValue,String pffValue){
		logger.debug("Entering");

		if(this.listBoxUpdateCustomer.getGroups() != null && !this.listBoxUpdateCustomer.getGroups().toString().contains(listGroup)){
			Listgroup listgroup = new Listgroup(getGroupLabel(listGroup));
			listgroup.setId(listGroup);
			listgroup.setCheckable(true);
			this.listBoxUpdateCustomer.appendChild(listgroup);
		}
		listitem = new Listitem();
		listitem.setId(fieldID);
		listcell = new Listcell(label);
		listcell.setParent(listitem);
		listcell = new Listcell(coreValue);
		listcell.setParent(listitem);
		listcell = new Listcell(pffValue);
		listcell.setParent(listitem);
		this.listBoxUpdateCustomer.appendChild(listitem);
		logger.debug("Leaving");
	}

	private String getGroupLabel(String listGroup){
		if(listGroup.equals(listGroupCustomer)){
			return Labels.getLabel("window_CustomerDialog.title");
		}else if(listGroup.equals(listGroupAddress)){
			return Labels.getLabel("window_CustomerAddresDialog.title");
		}else if(listGroup.equals(listGroupPhoneNumber)){
			return Labels.getLabel("window_CustomerPhoneNumberDialog.title");
		}else if(listGroup.equals(listGroupEmail)){
			return Labels.getLabel("window_CustomerEMailDialog.title");
		}else if(listGroup.equals(listGroupRating)){
			return Labels.getLabel("window_CustomerRatingDialog.title");
		}
		return "";
	}
	
	
	private void addCustFieldToUpdateList(String listGroup,String fieldID,String label,long coreValue,long pffValue){
		logger.debug("Entering");

		if(this.listBoxUpdateCustomer.getGroups() != null && !this.listBoxUpdateCustomer.getGroups().toString().contains(listGroup)){
			Listgroup listgroup = new Listgroup(getGroupLabel(listGroup));
			listgroup.setId(listGroup);
			listgroup.setCheckable(true);
			this.listBoxUpdateCustomer.appendChild(listgroup);
		}

		listitem = new Listitem();
		listitem.setId(fieldID);
		listcell = new Listcell(label);
		listcell.setParent(listitem);
		listcell = new Listcell(String.valueOf(coreValue));
		listcell.setParent(listitem);
		listcell = new Listcell(String.valueOf(pffValue));
		listcell.setParent(listitem);
		this.listBoxUpdateCustomer.appendChild(listitem);
		logger.debug("Leaving");
	}


	private void addChildFieldToUpdateList(String listGroup,String fieldID,String label,String coreValue,String pffValue){
		logger.debug("Entering");

		listitem = new Listitem();
		listcell = new Listcell(fieldID);
		listcell.setParent(listitem);
		listcell = new Listcell(coreValue);
		listcell.setParent(listitem);
		listcell = new Listcell(pffValue);
		listcell.setParent(listitem);
		this.listBoxUpdateCustomer.appendChild(listitem);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PFFCustomerPreparation getPffCustomerPreparation() {
		return pffCustomerPreparation;
	}
	public void setPffCustomerPreparation(
			PFFCustomerPreparation pffCustomerPreparation) {
		this.pffCustomerPreparation = pffCustomerPreparation;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public DailyDownloadInterfaceService getDailyDownloadInterfaceService() {
		return dailyDownloadInterfaceService;
	}
	public void setDailyDownloadInterfaceService(
			DailyDownloadInterfaceService dailyDownloadInterfaceService) {
		this.dailyDownloadInterfaceService = dailyDownloadInterfaceService;
	}
	
}