package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.exception.PFFInterfaceException;

public class FetchFinCustomerDedupDetails {

	private final static Logger logger = Logger.getLogger(FetchFinCustomerDedupDetails.class);	
	
	
	private static final String CUSTOMERDEDUP_LABELS =  "custCIF,custDOB,custFName,custLName,custCRCPR,"
			+ "custPassportNo,mobileNumber,custNationality,dedupRule,override,overridenby";

	private static DedupParmService dedupParmService;
	
	public FetchFinCustomerDedupDetails(){
		super();
	}
	
	
	@SuppressWarnings("unchecked")
	public static FinanceDetail getFinCustomerDedup(String userRole, FinanceDetail aFinanceDetail, Window parentWindow, String curLoginUser) throws PFFInterfaceException {
		List<CustomerDedup> customerDedupList=null;
		int userAction= -1;
		Customer customer = null;
		String mobileNumber = "";
		if (aFinanceDetail.getCustomerDetails().getCustomer() != null) {
			customer = aFinanceDetail.getCustomerDetails().getCustomer();
			if (aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList() != null) {
				for (CustomerPhoneNumber custPhone : aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList()) {
					if (custPhone.getPhoneTypeCode().equals(
							PennantConstants.PHONETYPE_MOBILE)) {
						mobileNumber = PennantApplicationUtil.formatPhoneNumber(
								custPhone.getPhoneCountryCode(),
								custPhone.getPhoneAreaCode(),
								custPhone.getPhoneNumber());
						break;
					}
				}
			}

			FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

			CustomerDedup customerDedup = doSetCustomerDedup(customer, aFinanceMain.getFinReference(), mobileNumber);

			List<CustomerDedup> custDedupData = getDedupParmService().fetchCustomerDedupDetails(userRole,customerDedup, curLoginUser, aFinanceMain.getFinType());

			if(custDedupData !=null && !custDedupData.isEmpty()) {

				Object dataObject = ShowCustomerDedupListBox.show(parentWindow,custDedupData,CUSTOMERDEDUP_LABELS, customerDedup, curLoginUser);

				if (dataObject != null) {
					ShowCustomerDedupListBox details=(ShowCustomerDedupListBox) dataObject;

					System.out.println("THE ACTIONED VALUE IS ::::"+details.getUserAction());		
					logger.debug("The User Action is "+details.getUserAction());
					userAction = details.getUserAction();
					customerDedupList=(List<CustomerDedup>)details.getObject();
				}
			} else {
				userAction = -1;
			}

			aFinanceDetail.setCustomerDedupList(null);

			/**
			 * userAction represents NotDuplicate or DuplicateFound actions
			 * if user click on NotDuplicate button userAction = 1
			 * if user click on DuplicateFound button userAction = 0
			 * if no customer found as a duplicate customer then userAction = -1
			 */
			if (userAction == -1) {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(true);
			} else if (userAction == 1) {
				aFinanceDetail.setCustomerDedupList(customerDedupList);
				aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(true);
			} else {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setDedupFound(true);
			}

			logger.debug("Leaving");
			
		}
		return aFinanceDetail;
		

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
