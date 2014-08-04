package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantConstants;


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
	
	/**
	 * Method of Dedup Check for Customer Details
	 * @param userRole
	 * @param aCustomerDetails
	 * @param parent
	 */
	private  FetchDedupDetails(String userRole, CustomerDetails aCustomerDetails,Component parent){
		super();

		setCustomerDetails(aCustomerDetails);

		List<CustomerDedup> customerDedup = getDedupParmService().fetchCustomerDedupDetails(userRole, aCustomerDetails);
		ShowDedupListBox details = null;

		if(customerDedup.size() > 0) {
			String compareFileds[]=new String[2];
			compareFileds[0]=PennantConstants.CUST_DEDUP_LISTFILED2;
			compareFileds[1]=PennantConstants.CUST_DEDUP_LISTFILED3;
			Object dataObject = ShowDedupListBox.show(parent,customerDedup,PennantConstants.CUST_DEDUP_LIST_FIELDS,aCustomerDetails.getCustomer(),aCustomerDetails.getCustomerDocumentsList(),compareFileds);
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
	
	/**
	 * Method of Dedup Check for Finance Details
	 * @param userRole
	 * @param aFinanceDetail
	 * @param parent
	 */
	private  FetchDedupDetails(String userRole, FinanceDetail aFinanceDetail,Component parent){
		super();

		setFinanceDetail(aFinanceDetail);
		FinanceDedup financeDedup = getDedupParmService().getCustomerById(aFinanceDetail.getFinScheduleData().getFinanceMain().getCustID());
		if(financeDedup != null){
			financeDedup.setFinReference(aFinanceDetail.getFinScheduleData().getFinReference());
			financeDedup.setLikeCustFName(financeDedup.getCustFName()!=null?"%"+financeDedup.getCustFName()+"%":"");
			financeDedup.setLikeCustMName(financeDedup.getCustMName()!=null?"%"+financeDedup.getCustMName()+"%":"");
			financeDedup.setLikeCustLName(financeDedup.getCustLName()!=null?"%"+financeDedup.getCustLName()+"%":"");
		}
		
		List<?> loanDedup = getDedupParmService().fetchFinDedupDetails(userRole, financeDedup);
		ShowDedupListBox details = null;

		if(loanDedup.size() > 0) {

			Object dataObject = ShowDedupListBox.show(parent,loanDedup,Labels.getLabel("label_FinDedupFields_label"));
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
			aFinanceDetail.getFinScheduleData().getFinanceMain().setDedupFound(false);
		} else {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setDedupFound(true);

			if (userAction == 1) {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(true);
			} else {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(false);
			}
		}

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
