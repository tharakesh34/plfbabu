package com.pennant.Interface.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.vo.CustomerLimit;
import com.pennant.equation.process.CustomerLimitProcess;

public class CustomerLimitIntefaceServiceEquationImpl implements CustomerLimitIntefaceService{
	private static Logger logger = Logger.getLogger(CustomerLimitIntefaceServiceEquationImpl.class);

	protected CustomerLimitProcess custLimitProcess;
	
	@Override
    public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize)
            throws CustomerLimitProcessException {
		logger.debug("Entering");
		Map<String, Object> customerLimitMap = null;
		try{
			customerLimitMap =  getCustLimitProcess().fetchCustLimitEnqList(pageNo, pageSize);
			
			if(customerLimitMap.containsKey("CustLimitList")){
				@SuppressWarnings("unchecked")
                List<CustomerLimit> list = (List<CustomerLimit>) customerLimitMap.get("CustLimitList");
				List<com.pennant.backend.model.customermasters.CustomerLimit> custLimitList = new ArrayList<com.pennant.backend.model.customermasters.CustomerLimit>();
				com.pennant.backend.model.customermasters.CustomerLimit limit = null;
				for (CustomerLimit customerLimit : list) {
	                
					 limit = new com.pennant.backend.model.customermasters.CustomerLimit();
					 limit.setCustCIF(customerLimit.getCustMnemonic());
					 limit.setCustLocation(customerLimit.getCustLocation());
					 limit.setCustShortName(customerLimit.getCustName());
					 limit.setLimitCategory(customerLimit.getLimitCategory());
					 limit.setCurrency(customerLimit.getLimitCurrency());
					 limit.setEarliestExpiryDate(customerLimit.getLimitExpiry());
					 limit.setBranch(customerLimit.getLimitBranch());
					 limit.setRepeatThousands(customerLimit.getRepeatThousands().equals("Y") ? true : false);
					 limit.setCheckLimit(customerLimit.getCheckLimit().equals("Y") ? true : false);
					 limit.setSeqNum(customerLimit.getSeqNum());
					 
					 custLimitList.add(limit);
                }
				
				customerLimitMap.put("CustLimitList",custLimitList);
			}else{
				customerLimitMap.put("CustLimitList",null);
			}
		}catch (CustomerLimitProcessException e) {
			throw e;
		}
		logger.debug("Leaving");
	    return customerLimitMap;
    }
	

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
    public List<CustomerLimit> fetchLimitEnquiryDetails(CustomerLimit customerLimit)
            throws CustomerLimitProcessException {
		logger.debug("Entering");
		List<CustomerLimit> customerLimits = null;
		try{
			customerLimits =  getCustLimitProcess().fetchLimitEnqDetails(customerLimit);
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


}
