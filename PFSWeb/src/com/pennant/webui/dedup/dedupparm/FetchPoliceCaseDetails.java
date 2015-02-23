package com.pennant.webui.dedup.dedupparm;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.util.PennantAppUtil;

public class FetchPoliceCaseDetails {
	
	private final static Logger logger = Logger.getLogger(FetchPoliceCaseDetails.class);
	
	private int userAction= -1;
	private static DedupParmService dedupParmService;
	private CustomerInterfaceService customerInterfaceService;
	private FinanceDetail financeDetail;
	private List<PoliceCase> policeCaseDedup;

	public FetchPoliceCaseDetails(){
		super();
	}

	public static FinanceDetail getPoliceCaseCustomer(FinanceDetail aFinanceDetail,Component parent){
		return new FetchPoliceCaseDetails(aFinanceDetail, parent).getFinanceDetail();
	}

	@SuppressWarnings("unchecked")
	private FetchPoliceCaseDetails (FinanceDetail aFinanceDetail,Component parent){
		super();

		setFinanceDetail(aFinanceDetail);
		Customer customer = null;
		if(aFinanceDetail.getCustomerDetails()== null || aFinanceDetail.getCustomerDetails().getCustomer() == null){
			customer = (Customer) PennantAppUtil.getCustomerObject(
					aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF(), null);
		}else{
			customer = aFinanceDetail.getCustomerDetails().getCustomer();
		}

		String userRole = aFinanceDetail.getFinScheduleData().getFinanceMain().getNextRoleCode();
		String finType = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType();
		long curUser = aFinanceDetail.getFinScheduleData().getFinanceMain().getLastMntBy();
		PoliceCase policeCaseData = dosetCustomertoPolicecCase(new PoliceCase(),customer,
				aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());

		setPoliceCaseDedup(getDedupParmService().fetchPoliceCaseCustomers(userRole, finType, policeCaseData));

		ShowDedupPoliceBox details = null;
		if(getPoliceCaseDedup()!=null && getPoliceCaseDedup().size() > 0) {

			Object dataObject = ShowDedupPoliceBox.show(parent, getPoliceCaseDedup(), 
					Labels.getLabel("label_FinPoliceCase_label"), policeCaseData,curUser);
			details = (ShowDedupPoliceBox) dataObject;

			if (details != null) {
				System.out.println("THE ACTIONED VALUE IS ::::"+details.getUserAction());	
				logger.debug("The User Action is "+details.getUserAction());
				userAction = details.getUserAction();
				setPoliceCaseDedup((List<PoliceCase>)details.getObject());
			}
		}else {
			userAction = -1;
		}

		if (userAction == -1) {
		} else {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setPoliceCaseFound(true);

			if (userAction == 1) {
				aFinanceDetail.getFinScheduleData().getFinanceMain().setPoliceCaseOverride(true);
				aFinanceDetail.setDedupPoliceCaseDetails(getPoliceCaseDedup());
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
	private PoliceCase dosetCustomertoPolicecCase(PoliceCase policeCase,
			Customer customer, String finReference) {
		policeCase.setCustCIF(customer.getCustCIF());	
		policeCase.setCustFName(customer.getCustFName());
		policeCase.setCustLName(customer.getCustLName());
		policeCase.setCustDOB(customer.getCustDOB());
		policeCase.setCustCRCPR(customer.getCustCRCPR());
		policeCase.setCustNationality(customer.getCustNationality());
		policeCase.setCustPassPort(customer.getCustPassportNo());
		policeCase.setCustMobileNumber(customer.getPhoneNumber());
		policeCase.setCustCtgType(customer.getLovDescCustCtgType());
		//policeCase.setCustProduct(customer.get);
		policeCase.setFinReference(finReference);
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

	public List<PoliceCase> getPoliceCaseDedup() {
		return policeCaseDedup;
	}
	public void setPoliceCaseDedup(List<PoliceCase> policeCaseDedup) {
		this.policeCaseDedup = policeCaseDedup;
	}

	public  DedupParmService getDedupParmService() {
		return dedupParmService;
	}
	public  void setDedupParmService(DedupParmService dedupParmService) {
		FetchPoliceCaseDetails.dedupParmService = dedupParmService;
	}
}
