package com.pennant.Interface.service;

import java.util.List;
import java.util.Map;

import com.pennant.coreinterface.vo.CustomerLimit;
import com.pennant.coreinterface.exception.CustomerLimitProcessException;

public interface CustomerLimitIntefaceService {
	
	public List<CustomerLimit> fetchLimitDetails(CustomerLimit customerLimit) throws CustomerLimitProcessException;
	public List<CustomerLimit> fetchLimitEnquiryDetails(CustomerLimit customerLimit) throws CustomerLimitProcessException;
	public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws CustomerLimitProcessException;
	public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit customerLimit) throws CustomerLimitProcessException;
	
}
