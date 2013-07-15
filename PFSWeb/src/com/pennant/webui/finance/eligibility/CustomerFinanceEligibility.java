/*package com.pennant.webui.finance.eligibility;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.lmtmasters.LoanEligibility;
import com.pennant.backend.service.customermasters.CustomerService;

public class CustomerFinanceEligibility {
	
	private static final long serialVersionUID = 8904534465232544668L;
	private final static Logger logger = Logger.getLogger(CustomerFinanceEligibility.class);
	
	private CustomerService customerService;
	private EligibilityScheduleGenerator eligibilityScheduleGenerator;
	
	private static boolean finTypeExists = false;
	List<LoanEligibility> loanEligibilities = new ArrayList<LoanEligibility>();
	
	*//**
	 * Method for Eligibility of Customer for Particular FinanceType of Loan .
	 * @param loanEligibility
	 * @return 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterruptedException 
	 *//*	
	public List<LoanEligibility> getCustomerEligibilityResult(LoanEligibility loanEligibility)
				throws IllegalAccessException, InvocationTargetException, InterruptedException{
		logger.debug("Entering");
		
		Customer customer = null;
		
		//For Customer Data
		if(loanEligibility.getCustId() != Long.MIN_VALUE){
			customer = getCustomerService().getApprovedCustomerById(loanEligibility.getCustId());
			BeanUtils.copyProperties(loanEligibility.getCustomerEligibilityCheck(), customer);
		}
		
		if(!(loanEligibility.getCustomerEligibilityCheck().getFinType() == null || 
	 			loanEligibility.getCustomerEligibilityCheck().getFinType().equals(""))){
			finTypeExists = true;
	 	}
		
	 	if(customer == null){
	 		final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("customer", customer);
			map.put("loanEligibility",loanEligibility);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/Eligibility/CustomerFinanceEligibility.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			}
	 	}else{
	 		loanEligibilities = getEligibilityScheduleGenerator().generateEligibilityList(
	 				finTypeExists,loanEligibility);
	 	}
		
		logger.debug("Leaving");
		return loanEligibilities;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public CustomerService getCustomerService() {
		return customerService;
	}
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setEligibilityScheduleGenerator(
			EligibilityScheduleGenerator eligibilityScheduleGenerator) {
		this.eligibilityScheduleGenerator = eligibilityScheduleGenerator;
	}
	public EligibilityScheduleGenerator getEligibilityScheduleGenerator() {
		return eligibilityScheduleGenerator;
	}
	
}
*/