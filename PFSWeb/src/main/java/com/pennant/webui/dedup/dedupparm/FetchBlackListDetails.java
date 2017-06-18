package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zul.Window;

import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;

public class FetchBlackListDetails {
	
	private static final Logger logger = Logger.getLogger(FetchBlackListDetails.class);
	private static DedupParmService dedupParmService;
	private List<BlackListCustomers> blackListCustomers;
	private List<FinBlacklistCustomer> finBlacklistCustomer;
	private FinanceDetail financeDetail;
	private int userAction= -1;
	
	String BLACKLIST_FIELDS = "custCIF,custDOB,custFName,custLName,custCRCPR,"
			+ "custPassportNo,mobileNumber,custNationality,employer,watchListRule,override,overridenby";

	public FetchBlackListDetails() {
		super();
	}

	public static FinanceDetail getBlackListCustomers(String userRole,FinanceDetail tFinanceDetail, Window parent, String curLoginUser) {
		return new FetchBlackListDetails(userRole,tFinanceDetail, parent, curLoginUser).getFinanceDetail();
	}
	
	/**
	 * 
	 * @param role
	 * @param custCIF
	 * @param parent
	 */
	@SuppressWarnings("unchecked")
	private FetchBlackListDetails(String userRole,FinanceDetail aFinanceDetail, Window parent, String curLoginUser) {
		super();
        logger.debug("Entering");
        
        Customer customer = null;
        String mobileNumber = "";
        if(aFinanceDetail.getCustomerDetails().getCustomer() != null) {
        	customer = aFinanceDetail.getCustomerDetails().getCustomer();
        	if(aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList() != null){
        		for(CustomerPhoneNumber custPhone: aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList()) {
        			if(custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)){
        				mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(), 
        						custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
        				break;
        			}
        		}
        	}
		}

        String finType = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType();
        String finReference = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference();
        
        BlackListCustomers blackListCustData = doSetCustDataToBlackList(customer, finReference , mobileNumber);
        setBlackListCustomers(getDedupParmService().fetchBlackListCustomers(userRole, finType, blackListCustData, curLoginUser));
		
        ShowBlackListDetailBox details = null;
		if(getBlackListCustomers()!= null && getBlackListCustomers().size() > 0) {
			
			Object dataObject = ShowBlackListDetailBox.show(parent, getBlackListCustomers(), 
					BLACKLIST_FIELDS, blackListCustData, curLoginUser);
			details = (ShowBlackListDetailBox) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::"+details.getUserAction());	
				logger.debug("The User Action is "+details.getUserAction());
				userAction = details.getUserAction();
				setFinBlacklistCustomer((List<FinBlacklistCustomer>)details.getObject());
			}
		} else {
			userAction = -1;
		}

		aFinanceDetail.setFinBlacklistCustomer(null);

		/**
         * userAction represents Clean or Blacklisted actions
         * if user click on Clean button userAction = 1
         * if user click on Blacklisted button userAction = 0
         * if no customer found as a blacklist customer then userAction = -1
         */
		if (userAction == -1) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(false);
			aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(false);
		} else {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(true);

			if (userAction == 1) {
				aFinanceDetail.setFinBlacklistCustomer(getFinBlacklistCustomer());
				aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(true);
			} else {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(false);
			}
		}
		setFinanceDetail(aFinanceDetail);
		logger.debug("Leaving");
	}
	
	/**
	 * Prepare Black List Customer Object Data
	 * @param blackListCustomer
	 * @param customer
	 * @return
	 */
	private BlackListCustomers doSetCustDataToBlackList(Customer customer, String finReference, String mobileNumber) {
		logger.debug("Entering");

		if (customer != null) {
			BlackListCustomers blackListCustomer = new BlackListCustomers();
			
			blackListCustomer.setCustCIF(customer.getCustCIF());
			blackListCustomer.setCustShrtName(customer.getCustShrtName());
			blackListCustomer.setCustFName(customer.getCustFName());
			blackListCustomer.setCustLName(customer.getCustLName());
			blackListCustomer.setCustCRCPR(customer.getCustCRCPR());
			blackListCustomer.setCustPassportNo(customer.getCustPassportNo());
			blackListCustomer.setMobileNumber(mobileNumber);
			blackListCustomer.setCustNationality(customer.getCustNationality());
			blackListCustomer.setCustDOB(customer.getCustDOB());
			blackListCustomer.setCustCtgCode(customer.getCustCtgCode());
			blackListCustomer.setFinReference(finReference);
			
			blackListCustomer.setLikeCustFName(blackListCustomer.getCustFName()!=null?"%"+blackListCustomer.getCustFName()+"%":"");
			blackListCustomer.setLikeCustLName(blackListCustomer.getCustLName()!=null?"%"+blackListCustomer.getCustLName()+"%":"");

			logger.debug("Leaving");

			return blackListCustomer;
		} else {
			return null;
		}
    }

	public  DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public  void setDedupParmService(DedupParmService dedupParmService) {
		FetchBlackListDetails.dedupParmService = dedupParmService;
	}

	public List<BlackListCustomers> getBlackListCustomers() {
		return blackListCustomers;
	}

	public void setBlackListCustomers(List<BlackListCustomers> blackListCustomers) {
		this.blackListCustomers = blackListCustomers;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
	
	public List<FinBlacklistCustomer> getFinBlacklistCustomer() {
		return finBlacklistCustomer;
	}

	public void setFinBlacklistCustomer(List<FinBlacklistCustomer> finBlacklistCustomer) {
		this.finBlacklistCustomer = finBlacklistCustomer;
	}
	
}
