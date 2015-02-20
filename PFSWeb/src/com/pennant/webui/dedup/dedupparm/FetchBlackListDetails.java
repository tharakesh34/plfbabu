package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Window;

import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.util.PennantAppUtil;

public class FetchBlackListDetails {
	
	private final static Logger logger = Logger.getLogger(FetchBlackListDetails.class);
	private static DedupParmService dedupParmService;
	private List<BlackListCustomers> blackListCustomers;
	private FinanceDetail financeDetail;
	private int userAction= -1;

	public FetchBlackListDetails() {
		super();
	}

	public static FinanceDetail getBlackListCustomers(FinanceDetail tFinanceDetail, Window parent) {
		return new FetchBlackListDetails(tFinanceDetail, parent).getFinanceDetail();
	}
	
	/**
	 * 
	 * @param role
	 * @param custCIF
	 * @param parent
	 */
	private FetchBlackListDetails(FinanceDetail tFinanceDetail, Window parent) {
		super();
        logger.debug("Entering");
        
       // Customer customer = tFinanceDetail.getCustomerDetails().getCustomer();
        //if(customer == null){
        	Customer customer = (Customer) PennantAppUtil.getCustomerObject(
        			tFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF(), null);
        //}
        
        String userRole = tFinanceDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
        String finType = tFinanceDetail.getFinScheduleData().getFinanceMain().getFinType();
        
        BlackListCustomers blackListCustData = doSetCustDataToBlackList(new BlackListCustomers(), customer);
        setBlackListCustomers(getDedupParmService().fetchBlackListCustomers(userRole, finType, blackListCustData));
		
        ShowBlackListDetailBox details = null;
		if(getBlackListCustomers().size() > 0) {
			
			Object dataObject = ShowBlackListDetailBox.show(parent, getBlackListCustomers(), 
					Labels.getLabel("label_BlackListCustomerFields_label"), blackListCustData);
			details = (ShowBlackListDetailBox) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::"+details.getUserAction());	
				logger.debug("The User Action is "+details.getUserAction());
				userAction = details.getUserAction();
			}
		}else {
			userAction = -1;
		}

		if (userAction == -1) {
			//Nothing To DO
		} else {
			tFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(true);

			if (userAction == 1) {
				tFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(true);
			} else {
				tFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklistOverride(false);
			}
		}
		setFinanceDetail(tFinanceDetail);
		logger.debug("Leaving");
	}
	
	/**
	 * Prepare Black List Customer Object Data
	 * @param blackListCustomer
	 * @param customer
	 * @return
	 */
	private BlackListCustomers doSetCustDataToBlackList(BlackListCustomers blackListCustomer, Customer customer) {
		blackListCustomer.setCustCIF(customer.getCustCIF());
		blackListCustomer.setCustShrtName(customer.getCustShrtName());
		blackListCustomer.setCustFName(customer.getCustFName());
		blackListCustomer.setCustLName(customer.getCustLName());
		blackListCustomer.setCustCRCPR(customer.getCustCRCPR());
		blackListCustomer.setCustPassportNo(customer.getCustPassportNo());
		blackListCustomer.setPhoneNumber(customer.getPhoneNumber());
		blackListCustomer.setCustNationality(customer.getCustNationality());
		blackListCustomer.setCustDOB(customer.getCustDOB());
		blackListCustomer.setCustCtgType(customer.getLovDescCustCtgType());
	    return blackListCustomer;
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
	
}
