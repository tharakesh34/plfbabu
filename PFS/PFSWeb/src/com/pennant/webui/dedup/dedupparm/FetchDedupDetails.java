package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.dedup.DedupParmService;


public class FetchDedupDetails {
	
	private final static Logger logger = Logger.getLogger(FetchDedupDetails.class);	
	protected Window window_CustomerQDEDialog; 
	private static DedupParmService dedupParmService;
	private int userAction= -1;
	private CustomerDetails customerDetails;
	private FinanceDetail financeDetail;
	
	public FetchDedupDetails(){
		super();
	}
	
	public static CustomerDetails getCustomerDedup(String userRole, CustomerDetails aCustomerDetails,Component parent){
		return new FetchDedupDetails(userRole, aCustomerDetails, parent).getCustomerDetails();
	}
	
	public static FinanceDetail getLoanDedup(String userRole, FinanceDetail aFinanceDetail,Component parent){
		return new FetchDedupDetails(userRole, aFinanceDetail, parent).getFinanceDetail();
	}
	
	private  FetchDedupDetails(String userRole, CustomerDetails aCustomerDetails,Component parent){
		super();

		setCustomerDetails(aCustomerDetails);

		List<CustomerDedup> customerDedup = getDedupParmService().fetchCustomerDedupDetails(userRole, aCustomerDetails);
		ShowDedupListBox details = null;

		if(customerDedup.size() > 0) {

			Object dataObject = ShowDedupListBox.show(parent,customerDedup,aCustomerDetails.getCustDedup().getDedupFields());
			details 	= (ShowDedupListBox) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::"+details.getUserAction());		
				logger.debug("The User Action is "+details.getUserAction());
				userAction = details.getUserAction();
			}
		}else {
			userAction = -1;
		}

		if (userAction == -1) {
			aCustomerDetails.getCustomer().setDedupFound(false);
		} else {
			aCustomerDetails.getCustomer().setDedupFound(true);

			if (userAction == 1) {
				aCustomerDetails.getCustomer().setSkipDedup(true);
			} else {
				aCustomerDetails.getCustomer().setSkipDedup(false);
			}
		}

		setCustomerDetails(aCustomerDetails);
	}
	
	//TODO Edit for Finance Details DeDup
	private  FetchDedupDetails(String userRole, FinanceDetail aFinanceDetail,Component parent){
		super();

		setFinanceDetail(aFinanceDetail);

		/*List<?> loanDedup = getDedupParmService().fetchLoanDedupDetails(userRole, aFinanceDetail);
		ShowDedupListBox details = null;

		if(loanDedup.size() > 0) {

			Object dataObject = ShowDedupListBox.show(parent,loanDedup,);
			details 	= (ShowDedupListBox) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::"+details.getUserAction());		
				logger.debug("The User Action is "+details.getUserAction());
				userAction = details.getUserAction();
			}
		}else {
			userAction = -1;
		}

		if (userAction == -1) {
			aCustomerDetails.getCustomer().setDedupFound(false);
		} else {
			aCustomerDetails.getCustomer().setDedupFound(true);

			if (userAction == 1) {
				aCustomerDetails.getCustomer().setSkipDedup(true);
			} else {
				aCustomerDetails.getCustomer().setSkipDedup(false);
			}
		}
*/
		setFinanceDetail(aFinanceDetail);
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}
	public void setDedupParmService(DedupParmService dedupParmService) {
		FetchDedupDetails.dedupParmService = dedupParmService;
	}

	public int getUserAction() {
		return userAction;
	}
	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}
	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
	
	
}
