package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;

public class FetchPoliceCaseDetails {
	private static final Logger logger = Logger.getLogger(FetchPoliceCaseDetails.class);
	
	private int userAction= -1;
	private static DedupParmService dedupParmService;
	private CustomerInterfaceService customerInterfaceService;
	private FinanceDetail financeDetail;
	private List<PoliceCaseDetail> policeCaseDedup;
	private List<PoliceCase> PoliceCaseDedupCheck;

	String PoliceCase_List = "custCIF,custDOB,custFName,custLName,custCRCPR,custPassportNo,mobileNumber,"
			+ "custNationality,custProduct,policeCaseRule,override,overridenby";
	public FetchPoliceCaseDetails(){
		super();
	}

	public static FinanceDetail getPoliceCaseCustomer(String userRole,FinanceDetail aFinanceDetail,Component parent,String loginUser){
		return new FetchPoliceCaseDetails(userRole,aFinanceDetail, parent,loginUser).getFinanceDetail();
	}

	@SuppressWarnings("unchecked")
	private FetchPoliceCaseDetails (String userRole,FinanceDetail aFinanceDetail,Component parent,String loginUser){
		super();

		setFinanceDetail(aFinanceDetail);
		Customer customer = null;
		String custMobileNumber = "";
		
		if(aFinanceDetail.getCustomerDetails().getCustomer()!=null){
			customer = aFinanceDetail.getCustomerDetails().getCustomer();
			if(aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList() != null){
        		for(CustomerPhoneNumber custPhone: aFinanceDetail.getCustomerDetails().getCustomerPhoneNumList()) {
        			if(custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)){
        				custMobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(), 
        						custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
        			}
        		}
        	}
		}

		String finType = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType();
		String curUser = loginUser;
		 
		PoliceCaseDetail policeCaseData = dosetCustomertoPoliceCase(new PoliceCaseDetail(),customer,
				aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference(),custMobileNumber);

		setPoliceCaseDedup(getDedupParmService().fetchPoliceCaseCustomers(userRole, finType, policeCaseData,curUser));

		ShowDedupPoliceBox details = null;
		if(getPoliceCaseDedup()!=null && getPoliceCaseDedup().size() > 0) {

			Object dataObject = ShowDedupPoliceBox.show(parent, getPoliceCaseDedup(), 
					PoliceCase_List, policeCaseData,curUser);
			details = (ShowDedupPoliceBox) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::"+details.getUserAction());	
				logger.debug("The User Action is "+details.getUserAction());
				userAction = details.getUserAction();
				setPoliceCaseDedupCheck((List<PoliceCase>)details.getObject());
			}
		}else {
			userAction = -1;
		}
		aFinanceDetail.setDedupPoliceCaseDetails(null);
		
		if (userAction == -1) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setPoliceCaseFound(false);
			aFinanceDetail.getFinScheduleData().getFinanceMain().setPoliceCaseOverride(false);
		} else {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setPoliceCaseFound(true);
			if (userAction == 1) {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setPoliceCaseOverride(true);
				aFinanceDetail.setDedupPoliceCaseDetails(getPoliceCaseDedupCheck());
			} else {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setPoliceCaseOverride(false);
			}
		}
		setFinanceDetail(aFinanceDetail);
		logger.debug("Leaving");
	}
	
	/**
	 * Preparing Police Case Object Data using Customer for Rule Executions
	 * @param policeCase
	 * @param customer
	 * @param finReference
	 * @return
	 */
	private PoliceCaseDetail dosetCustomertoPoliceCase(PoliceCaseDetail policeCase,
			Customer customer, String finReference,String custMobileNumber) {
		policeCase.setCustCIF(customer.getCustCIF());	
		policeCase.setCustFName(customer.getCustFName());
		policeCase.setCustLName(customer.getCustLName());
		policeCase.setCustDOB(customer.getCustDOB());
		policeCase.setCustCRCPR(customer.getCustCRCPR());
		policeCase.setCustNationality(customer.getCustNationality());
		policeCase.setCustPassportNo(customer.getCustPassportNo());
		policeCase.setMobileNumber(custMobileNumber);
		policeCase.setCustCtgCode(customer.getCustCtgCode());
		//policeCase.setCustProduct(customer.get);
		policeCase.setFinReference(finReference);
		
		policeCase.setLikeCustFName(policeCase.getCustFName()!=null?"%"+policeCase.getCustFName()+"%":"");
		policeCase.setLikeCustLName(policeCase.getCustLName()!=null?"%"+policeCase.getCustLName()+"%":"");

		return policeCase;
	}

	public int getUserAction() {
		return userAction;
	}
	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public List<PoliceCaseDetail> getPoliceCaseDedup() {
		return policeCaseDedup;
	}
	public void setPoliceCaseDedup(List<PoliceCaseDetail> policeCaseDedup) {
		this.policeCaseDedup = policeCaseDedup;
	}

	public  DedupParmService getDedupParmService() {
		return dedupParmService;
	}
	public  void setDedupParmService(DedupParmService dedupParmService) {
		FetchPoliceCaseDetails.dedupParmService = dedupParmService;
	}

	public List<PoliceCase> getPoliceCaseDedupCheck() {
		return PoliceCaseDedupCheck;
	}

	public void setPoliceCaseDedupCheck(List<PoliceCase> policeCaseDedupCheck) {
		PoliceCaseDedupCheck = policeCaseDedupCheck;
	}
}
