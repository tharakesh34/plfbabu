package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.InterfaceException;

public class FetchFinCustomerDedupDetails {

	private static final Logger logger = Logger.getLogger(FetchFinCustomerDedupDetails.class);	
	
	
	private static  String CUSTOMERDEDUP_LABELS =  "custCIF,custDOB,custFName,custLName,custCRCPR,"
			+ "custPassportNo,mobileNumber,custNationality,dedupRule,override,overridenby";

	private static DedupParmService dedupParmService;
	
	public FetchFinCustomerDedupDetails(){
		super();
	}
	
	
	@SuppressWarnings("unchecked")
	public static CustomerDetails getFinCustomerDedup(String userRole, String finType, String ref,
			CustomerDetails custdetails, Window parentWindow, String curLoginUser) throws Exception {
		logger.debug("Entering");
		List<CustomerDedup> customerDedupList=null;
		int userAction= -1;
		Customer customer = null;
		String mobileNumber = "";
		if (custdetails.getCustomer() != null) {
			customer = custdetails.getCustomer();
			if (custdetails.getCustomerPhoneNumList() != null) {
				for (CustomerPhoneNumber custPhone : custdetails.getCustomerPhoneNumList()) {
					if (custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)) {
						mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
								custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					}
				}
			}

			CustomerDedup customerDedup = doSetCustomerDedup(customer, ref, mobileNumber);
			List<CustomerDedup> custDedupData = null;

			if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				// get customer dedup details from interface
				custDedupData = dedupParmService.getDedupCustomerDetails(custdetails,finType,ref);
				CUSTOMERDEDUP_LABELS = "custCIF,custDOB,custShrtName,custCRCPR,phoneNumber,custCoreBank,address,override";
			} 

			if (custDedupData != null && !custDedupData.isEmpty()) {

				Object dataObject = ShowCustomerDedupListBox.show(parentWindow, custDedupData, CUSTOMERDEDUP_LABELS,
						customerDedup, curLoginUser);

				if (dataObject != null) {
					ShowCustomerDedupListBox details = (ShowCustomerDedupListBox) dataObject;

					System.out.println("THE ACTIONED VALUE IS ::::" + details.getUserAction());
					logger.debug("The User Action is " + details.getUserAction());
					userAction = details.getUserAction();
					customerDedupList = (List<CustomerDedup>) details.getObject();
				}
			} 

			custdetails.setCustomerDedupList(null);

			/**
			 * userAction represents NotDuplicate or DuplicateFound actions if user click on NotDuplicate button
			 * userAction = 1 if user click on DuplicateFound button userAction = 0 if no customer found as a duplicate
			 * customer then userAction = -1
			 */
			if (userAction == 0) {
				custdetails.getCustomer().setSkipDedup(false);
			} else {
				if (userAction == 1) {
					custdetails.setCustomerDedupList(customerDedupList);
					custdetails.getCustomer().setSkipDedup(true);
					custdetails.getCustomer().setDedupFound(true);
				} else if (userAction == 2) {
					custdetails.getCustomer().setDedupFound(true);
					custdetails.getCustomer().setSkipDedup(false);
					throw new InterfaceException("41002",
							Labels.getLabel("label_Message_CustomerMultiOverrideAlert_Baj"));
				} else {
					custdetails.getCustomer().setDedupFound(false);
				}

			}
		
			logger.debug("Leaving");

		}
		return custdetails;
		

	}


	private static CustomerDedup doSetCustomerDedup(Customer customer, String finReference, String mobileNumber) {
		logger.debug("Entering");
		
		if(customer != null) {
			CustomerDedup customerDedup = new CustomerDedup();

			customerDedup.setFinReference(finReference);
			customerDedup.setCustId(customer.getCustID());
			customerDedup.setCustCIF(customer.getCustCIF());
			customerDedup.setCustFName(customer.getCustFName());
			customerDedup.setCustLName(customer.getCustLName());
			customerDedup.setCustMotherMaiden(customer.getCustMotherMaiden());
			customerDedup.setCustShrtName(customer.getCustShrtName());
			customerDedup.setCustDOB(customer.getCustDOB());
			customerDedup.setCustCRCPR(customer.getCustCRCPR());
			customerDedup.setCustCtgCode(customer.getCustCtgCode());
			customerDedup.setCustDftBranch(customer.getCustDftBranch());
			customerDedup.setCustSector(customer.getCustSector());
			customerDedup.setCustSubSector(customer.getCustSubSector());
			customerDedup.setCustNationality(customer.getCustNationality());
			customerDedup.setCustPassportNo(customer.getCustPassportNo());
			customerDedup.setCustTradeLicenceNum(customer.getCustTradeLicenceNum());
			customerDedup.setCustVisaNum(customer.getCustVisaNum());
			customerDedup.setMobileNumber(mobileNumber);
			customerDedup.setCustPOB(customer.getCustPOB());
			customerDedup.setCustResdCountry(customer.getCustResdCountry());
			customerDedup.setCustEMail(customer.getEmailID());
			
			logger.debug("Leaving");
			return customerDedup;
		} else {
			return null;
		}
		
	}

	public static DedupParmService getDedupParmService() {
		return dedupParmService;
	}


	public void setDedupParmService(DedupParmService dedupParmService) {
		FetchFinCustomerDedupDetails.dedupParmService = dedupParmService;
	}
	
	
}
