package com.pennant.Interface.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.model.reports.AvailCustomerDetail;
import com.pennant.coreinterface.exception.CustomerNotFoundException;

public interface CustomerInterfaceService {
	
	Customer fetchCustomerDetails(Customer customer) throws CustomerNotFoundException;
	String generateNewCIF(String operation, Customer customer, String finReference)throws CustomerNotFoundException;
	AvailCustomerDetail fetchAvailCustDetails(AvailCustomerDetail detail, BigDecimal newExposure, String ccy) throws CustomerNotFoundException;
	List<BlackListCustomers> fetchBlackListedCustomers(BlackListCustomers customer, List<DedupParm> dedupParmList) throws IllegalAccessException, InvocationTargetException;
	List<PoliceCase> fetchPoliceCase(PoliceCase policeCase,List<DedupParm> dedupParmList)throws IllegalAccessException, InvocationTargetException;
}
