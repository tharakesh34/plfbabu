package com.pennant.Interface.service;

import java.util.List;
import java.util.Map;

import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.model.CustomerLimit;

public interface CustomerLimitIntefaceService {
	
	public List<CustomerLimit> fetchLimitDetails(CustomerLimit customerLimit) throws CustomerLimitProcessException;
	public List<CustomerLimit> fetchLimitEnquiryDetails(CustomerLimit customerLimit) throws CustomerLimitProcessException;
	public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws CustomerLimitProcessException;
	public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit availLimit) throws CustomerLimitProcessException;
	
}
