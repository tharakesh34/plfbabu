package com.pennant.Interface.service;

import java.util.List;
import java.util.Map;

import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.model.CustomerLimit;

public interface CustomerLimitIntefaceService {
	
	List<CustomerLimit> fetchLimitDetails(CustomerLimit customerLimit) throws CustomerLimitProcessException;
	List<CustomerLimit> fetchLimitEnquiryDetails(CustomerLimit customerLimit) throws CustomerLimitProcessException;
	Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws CustomerLimitProcessException;
	List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit availLimit) throws CustomerLimitProcessException;
}
