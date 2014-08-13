package com.pennant.Interface.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.service.CustomerLimitProcess;

public class CustomerLimitIntefaceServiceCoreDBImpl implements CustomerLimitIntefaceService{
	private static Logger logger = Logger.getLogger(CustomerLimitIntefaceServiceCoreDBImpl.class);

	protected CustomerLimitProcess custLimitProcess;
	
	

	@Override
    public List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws CustomerLimitProcessException {
		logger.debug("Entering");
		List<CustomerLimit> customerLimits = null;
		try{
			customerLimits =  getCustLimitProcess().fetchLimitDetails(custLimit);
		}catch (CustomerLimitProcessException e) {
			throw e;
		}
		logger.debug("Leaving");
	    return customerLimits;
    }

	@Override
    public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit customerLimit)
            throws CustomerLimitProcessException {
		logger.debug("Entering");
		List<CustomerLimit> customerLimits = null;
		try{
			
			customerLimits =  getCustLimitProcess().fetchGroupLimitDetails(customerLimit);
			
		}catch (CustomerLimitProcessException e) {
			throw e;
		}
		logger.debug("Leaving");
	    return customerLimits;
    }

	public CustomerLimitProcess getCustLimitProcess() {
    	return custLimitProcess;
    }

	public void setCustLimitProcess(CustomerLimitProcess custLimitProcess) {
    	this.custLimitProcess = custLimitProcess;
    }

	@Override
    public List<CustomerLimit> fetchLimitEnquiryDetails(CustomerLimit customerLimit)
            throws CustomerLimitProcessException {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize)
            throws CustomerLimitProcessException {
	    // TODO Auto-generated method stub
	    return null;
    }

	
	
}
