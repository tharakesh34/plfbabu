package com.pennant.webui.dedup.dedupparm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.InterfaceException;

public class FetchCustomerDedupDetails {

	private static final Logger		logger					= Logger.getLogger(FetchCustomerDedupDetails.class);

	private static final String		CUSTOMERDEDUP_LABELS	= "custCIF,custDOB,custFName,custLName,custCRCPR,custEMail,mobileNumber,aadharNumber,"
			+ "custNationality,dedupRule,override";

	private static DedupParmService	dedupParmService;
	private static CustomerDedupDAO	customerDedupDAO;
	private static MasterDefDAO masterDefDAO;

	public FetchCustomerDedupDetails() {
		super();
	}

	@SuppressWarnings("unchecked")
	public static CustomerDetails getCustomerDedup(String userRole, CustomerDetails customerDetails, Window parentWindow, String curLoginUser) throws InterfaceException {
		List<CustomerDedup> customerDedupList = null;


		if (customerDetails != null && customerDetails.getCustomer() != null) {

			CustomerDedup customerDedup = doSetCustomerDedup(customerDetails);

			Customer customer = customerDetails.getCustomer();

			List<CustomerDedup> custDedupData = fetchCustomerDedupDetails(userRole, customerDedup,curLoginUser);

			if (custDedupData != null && !custDedupData.isEmpty()) {
				customer.setDedupFound(true);
				Object dataObject = ShowCustomerDedupListBox.show(parentWindow, custDedupData, CUSTOMERDEDUP_LABELS, customerDedup, curLoginUser);

				if (dataObject != null) {
					ShowCustomerDedupListBox details = (ShowCustomerDedupListBox) dataObject;

					System.out.println("THE ACTIONED VALUE IS ::::" + details.getUserAction());
					logger.debug("The User Action is " + details.getUserAction());
					int userAction  = details.getUserAction();
					
					customerDedupList = (List<CustomerDedup>) details.getObject();
					
					if (userAction == 1) {
						customerDetails.setCustomerDedupList(customerDedupList);
						customer.setSkipDedup(true);
					}else if(userAction==0){
						customer.setSkipDedup(false);
					}
				}
				
			}else{
				customer.setDedupFound(false);
				customerDetails.setCustomerDedupList(null);
			} 
			logger.debug("Leaving");

		}
		return customerDetails;

	}

	public static List<CustomerDedup> fetchCustomerDedupDetails(String userRole, CustomerDedup customerDedup, String curLoginUser) throws InterfaceException {

		List<CustomerDedup> overridedCustDedupList = new ArrayList<CustomerDedup>();
		List<CustomerDedup> customerDedupList = new ArrayList<CustomerDedup>();
		List<DedupParm> list = getDedupParmService().getDedupParmByModule(FinanceConstants.DEDUP_CUSTOMER, customerDedup.getCustCtgCode(), "");
		for (DedupParm dedupParm : list) {
			// to get previously overridden data
			List<CustomerDedup> custDedupList = getCustomerDedupDAO().fetchOverrideCustDedupData(customerDedup.getCustCIF(), dedupParm.getQueryCode(),FinanceConstants.DEDUP_CUSTOMER);
			for (CustomerDedup custDedup : custDedupList) {
				custDedup.setOverridenby(custDedup.getOverrideUser());
				overridedCustDedupList.add(custDedup);
			}
			//to get the de dup details based on the de dup parameters i.e query's list from both application and core banking
			customerDedupList.addAll(getDedupParmService().getCustomerDedup(customerDedup, list));

		}
		customerDedupList=doSetCustomerDeDupGrouping(customerDedupList);
		
		
		boolean newUser = false;
		// Checking for duplicate records in overrideBlacklistCustomers and currentBlacklistCustomers
		try {
			if (!overridedCustDedupList.isEmpty() && !customerDedupList.isEmpty()) {

				for (CustomerDedup previousDedup: overridedCustDedupList) {
					for (CustomerDedup currentDedup: customerDedupList) {
						if (previousDedup.getCustCIF().equals(currentDedup.getCustCIF())) {
							currentDedup.setOverridenby(previousDedup.getOverrideUser());
							if(previousDedup.getOverrideUser().contains(curLoginUser)) {
								currentDedup.setOverrideUser(previousDedup.getOverrideUser());
								newUser = false;
							} else {
								currentDedup.setOverrideUser(previousDedup.getOverrideUser() + PennantConstants.DELIMITER_COMMA + curLoginUser);
								newUser = true;
							}
							//Checking for New Rule
							if (isRuleChanged(previousDedup.getDedupRule(),currentDedup.getDedupRule())) {
								currentDedup.setNewRule(true);
								if(previousDedup.getCustCIF().equals(currentDedup.getCustCIF())) {
									currentDedup.setNewCustDedupRecord(false);
								} else {
									currentDedup.setNewCustDedupRecord(true);
									currentDedup.setOverride(false);
								}
							} else {
								currentDedup.setNewCustDedupRecord(false);
							}

							if(newUser) {
								currentDedup.setOverride(previousDedup.isOverride());
							}
						}
					}
				}
			} else if (!overridedCustDedupList.isEmpty() && customerDedupList.isEmpty()) {
				customerDedupList.addAll(overridedCustDedupList);
			}
		
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		
		
		return customerDedupList;
	}

    /**
     * Checking for Rule weather it is added or removed
     * @param overrideListRule
     * @param newListRule
     * @return
     */
    private static boolean isRuleChanged(String overrideListRule, String newListRule) {
		String[] exeRuleList = overrideListRule.split(",");
		String[] newRuleList = newListRule.split(",");
		if (exeRuleList.length != newRuleList.length) {
			return true;
		} else {
			for (String newRule : newRuleList) {
				if (!Arrays.toString(exeRuleList).contains(newRule)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static List<CustomerDedup> doSetCustomerDeDupGrouping(List<CustomerDedup> customerDedupList) {
		logger.debug("Entering");
		List<CustomerDedup> groupedList=new ArrayList<CustomerDedup>();
		try {
			
			for (CustomerDedup customerDedup : customerDedupList) {
				customerDedup.setModule(FinanceConstants.DEDUP_CUSTOMER);
				customerDedup.setOverride(true);
				if (groupedList.isEmpty()) {
					groupedList.add(customerDedup);
				}else{
					CustomerDedup custDedup=checkRecordinList(customerDedup,groupedList);
					if (custDedup!=null) {
						groupedList.add(custDedup);
					}
				}
				
			}
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		   logger.debug("Leaving");
		   return groupedList;
    }
	
	
	private static CustomerDedup checkRecordinList(CustomerDedup customerDedupcheck, List<CustomerDedup> groupedList) {
		for (CustomerDedup customerDedup : groupedList) {
			if (customerDedup.getCustCIF().equals(customerDedupcheck.getCustCIF())) {
				customerDedup.setQueryField(customerDedup.getQueryField() + PennantConstants.DELIMITER_COMMA+ customerDedupcheck.getQueryField());
				if (!customerDedup.getDedupRule().contains(customerDedupcheck.getDedupRule())) {
					customerDedup.setDedupRule(customerDedup.getDedupRule() + PennantConstants.DELIMITER_COMMA + customerDedupcheck.getDedupRule());
				}
				return null;
			}
		}
		return customerDedupcheck;
	}

	private static CustomerDedup doSetCustomerDedup(CustomerDetails customerDetails) {
		logger.debug("Entering");
		String mobileNumber = "";
		String emailid = "";
		String aadharId = "";
		String aadhar = masterDefDAO.getMasterCode("DOC_TYPE", "AADHAAR");
		String passPort = masterDefDAO.getMasterCode("DOC_TYPE", "PASSPORT");
		Customer customer = customerDetails.getCustomer();
		if (customerDetails.getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhone : customerDetails.getCustomerPhoneNumList()) {
				if (String.valueOf(custPhone.getPhoneTypePriority()).equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
							custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					break;
				}
			}
		}
		if (customerDetails.getCustomerEMailList() != null) {
			for (CustomerEMail email : customerDetails.getCustomerEMailList()) {
				if (String.valueOf(email.getCustEMailPriority()).equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					emailid = email.getCustEMail();
					break;
				}
			}
		}
		//Aadhar 
		if (customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (document.getCustDocCategory().equals(aadhar)) {
					aadharId = document.getCustDocTitle();
					break;
				}
			}
		}
		//Passport 
		if (customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (document.getCustDocCategory().equals(passPort)) {
					passPort = document.getCustDocTitle();
					break;
				}
			}
		}

		CustomerDedup customerDedup = new CustomerDedup();
		customerDedup.setFinReference(customer.getCustCIF());
		customerDedup.setCustId(customer.getCustID());
		customerDedup.setCustCIF(customer.getCustCIF());
		customerDedup.setCustFName(customer.getCustFName());
		customerDedup.setCustLName(customer.getCustLName());
		customerDedup.setCustShrtName(customer.getCustShrtName());
		customerDedup.setCustDOB(customer.getCustDOB());
		customerDedup.setCustCRCPR(customer.getCustCRCPR());
		customerDedup.setAadharNumber(aadharId);
		customerDedup.setCustCtgCode(customer.getCustCtgCode());
		customerDedup.setCustDftBranch(customer.getCustDftBranch());
		customerDedup.setCustSector(customer.getCustSector());
		customerDedup.setCustSubSector(customer.getCustSubSector());
		customerDedup.setCustNationality(customer.getCustNationality());
		customerDedup.setCustPassportNo(passPort);
		customerDedup.setCustTradeLicenceNum(customer.getCustTradeLicenceNum());
		customerDedup.setCustVisaNum(customer.getCustVisaNum());
		customerDedup.setMobileNumber(mobileNumber);
		customerDedup.setCustPOB(customer.getCustPOB());
		customerDedup.setCustResdCountry(customer.getCustResdCountry());
		customerDedup.setCustEMail(emailid);

		logger.debug("Leaving");
		return customerDedup;

	}

	public static DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		FetchCustomerDedupDetails.dedupParmService = dedupParmService;
	}

	public static CustomerDedupDAO getCustomerDedupDAO() {
		return customerDedupDAO;
	}

	public void setCustomerDedupDAO(CustomerDedupDAO customerDedupDAO) {
		FetchCustomerDedupDetails.customerDedupDAO = customerDedupDAO;
	}

	public static MasterDefDAO getMasterDefDAO() {
		return masterDefDAO;
	}

	public static void setMasterDefDAO(MasterDefDAO masterDefDAO) {
		FetchCustomerDedupDetails.masterDefDAO = masterDefDAO;
	}

}
