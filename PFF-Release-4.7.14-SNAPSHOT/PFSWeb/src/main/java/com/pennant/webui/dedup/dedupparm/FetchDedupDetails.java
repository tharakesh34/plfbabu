package com.pennant.webui.dedup.dedupparm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;


public class FetchDedupDetails {

	private static final Logger logger = Logger.getLogger(FetchDedupDetails.class);	
	protected Window window_CustomerQDEDialog; 
	private static DedupParmService dedupParmService;
	private int userAction= -1;

	private String FINDEDUPLABELSWBG = "CustCIF,FinLimitRef,CustCRCPR,ChassisNumber,EngineNumber,DupReference,StartDate,"
			+ "FinanceAmount,FinanceType,ProfitAmount,StageDesc,DedupeRule,OverrideUser";

	private String FINDEDUPLABELSPBG = "CustCIF,CustCRCPR,ChassisNumber,EngineNumber,DupReference,StartDate,"
			+ "FinanceAmount,FinanceType,ProfitAmount,StageDesc,DedupeRule,OverrideUser";
	
	

	private CustomerDetails customerDetails;
	private FinanceDetail financeDetail;
	private List<FinanceDedup> financeDedupList;

	public FetchDedupDetails(){
		super();
	}

	public static CustomerDetails getCustomerDedup(String userRole, CustomerDetails aCustomerDetails,Component parent){
		return new FetchDedupDetails(userRole, aCustomerDetails, parent).getCustomerDetails();
	}

	public static FinanceDetail getLoanDedup(String userRole, FinanceDetail aFinanceDetail,Component parent ,String curLoginUser){
		return new FetchDedupDetails(userRole, aFinanceDetail, parent,curLoginUser).getFinanceDetail();
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
			Object dataObject = ShowDedupListBox.show(parent,customerDedup,PennantConstants.CUST_DEDUP_LIST_FIELDS,
					aCustomerDetails.getCustomer(),aCustomerDetails.getCustomerDocumentsList(),compareFileds, null);
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
			aCustomerDetails.getCustomer().setSkipDedup(false);
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
	@SuppressWarnings("unchecked")
	private  FetchDedupDetails(String userRole, FinanceDetail aFinanceDetail,Component parent,String curLoginUser){
		super();

		//Data Preparation for Rule Executions
		Customer customer = aFinanceDetail.getCustomerDetails().getCustomer();
		FinanceDedup financeDedup = new FinanceDedup();
		financeDedup.setCustId(customer.getCustID());
		financeDedup.setCustCRCPR(customer.getCustCRCPR());
		financeDedup.setCustCIF(customer.getCustCIF());
		financeDedup.setCustFName(customer.getCustFName());
		financeDedup.setCustMName(customer.getCustMName());
		financeDedup.setCustLName(customer.getCustLName());
		financeDedup.setCustShrtName(customer.getCustShrtName());
		financeDedup.setCustMotherMaiden(customer.getCustMotherMaiden());
		financeDedup.setCustNationality(customer.getCustNationality());
		financeDedup.setCustParentCountry(customer.getCustParentCountry());
		financeDedup.setCustDOB(customer.getCustDOB());
		financeDedup.setMobileNumber(getCustMobileNum(aFinanceDetail));
		financeDedup.setTradeLicenceNo(customer.getCustTradeLicenceNum());
		
		//Check Customer is Existing or New Customer Object
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
				
		//finance data to set in to finance dedup 
		financeDedup.setFinanceAmount(aFinanceMain.getFinAmount());
		financeDedup.setProfitAmount(aFinanceMain.getTotalGrossPft());
		financeDedup.setFinanceType(aFinanceMain.getFinType());
		financeDedup.setStartDate(aFinanceMain.getFinStartDate());
		financeDedup.setFinLimitRef(aFinanceMain.getFinLimitRef());
		
		financeDedup.setFinReference(aFinanceMain.getFinReference());
		financeDedup.setLikeCustFName(financeDedup.getCustFName()!=null?"%"+financeDedup.getCustFName()+"%":"");
		financeDedup.setLikeCustMName(financeDedup.getCustMName()!=null?"%"+financeDedup.getCustMName()+"%":"");
		financeDedup.setLikeCustLName(financeDedup.getCustLName()!=null?"%"+financeDedup.getCustLName()+"%":"");
		
		
		//For Existing Customer/ New Customer
		List<FinanceDedup> loanDedup = new ArrayList<FinanceDedup>();
		List<FinanceDedup> dedupeRuleData = getDedupParmService().fetchFinDedupDetails(userRole,financeDedup, 
				curLoginUser, aFinanceMain.getFinType());
		loanDedup.addAll(dedupeRuleData);

		ShowDedupListBox details = null;
		Object dataObject;
		if(!loanDedup.isEmpty()) {
			if(FinanceConstants.FIN_DIVISION_CORPORATE.equals(aFinanceDetail.getFinScheduleData().getFinanceType().getFinDivision())){
				
				dataObject = ShowDedupListBox.show(parent,loanDedup,FINDEDUPLABELSWBG,
						financeDedup, curLoginUser);
			}else{
				dataObject = ShowDedupListBox.show(parent,loanDedup,FINDEDUPLABELSPBG,
						financeDedup, curLoginUser);
			}
			details 	= (ShowDedupListBox) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::"+details.getUserAction());		
				logger.debug("The User Action is "+details.getUserAction());
				userAction = details.getUserAction();
				setFinanceDedupList((List<FinanceDedup>)details.getObject());
			}
		}else {
			userAction = -1;
		}

		aFinanceDetail.setFinDedupDetails(null);
		if (userAction == -1) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setDedupFound(false);
			aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(false);
		} else if (userAction == 1) {
			aFinanceDetail.setFinDedupDetails(getFinanceDedupList());
			aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(true);
		} else {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setDedupFound(true);
			aFinanceDetail.getFinScheduleData().getFinanceMain().setSkipDedup(false);
		}


		setFinanceDetail(aFinanceDetail);
	}

	private String getCustMobileNum(FinanceDetail aFinanceDetail) {
		String custMobileNumber = "";
		if(aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList() != null){
			for(CustomerPhoneNumber custPhone: aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList()) {
				if(custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)){
					custMobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(), 
							custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					break;
				}
			}
		}
		return custMobileNumber;
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public List<FinanceDedup> getFinanceDedupList() {
		return financeDedupList;
	}

	public void setFinanceDedupList(List<FinanceDedup> financeDedupList) {
		this.financeDedupList = financeDedupList;
	}

}
