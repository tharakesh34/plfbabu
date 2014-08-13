package com.pennant.coreinterface.service;

import java.util.List;
import java.util.Map;

import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.coreinterface.model.CustomerLimit;

public interface CustomerLimitProcess {

	Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize)
			throws CustomerLimitProcessException;

	List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit)
			throws CustomerLimitProcessException;

	List<CustomerLimit> fetchLimitEnqDetails(CustomerLimit custLimit)
			throws CustomerLimitProcessException;

	List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit custLimit)
			throws CustomerLimitProcessException;

}
