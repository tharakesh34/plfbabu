package com.pennant.Interface.service;

import java.math.BigDecimal;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.reports.AvailCustomerDetail;
import com.pennant.coreinterface.exception.CustomerNotFoundException;

public interface CustomerInterfaceService {
	
	Customer fetchCustomerDetails(Customer customer) throws CustomerNotFoundException;
	String generateNewCIF(String operation, Customer customer, String finReference)throws CustomerNotFoundException;
	AvailCustomerDetail fetchAvailCustDetails(AvailCustomerDetail detail, BigDecimal newExposure, String ccy) throws CustomerNotFoundException;
}
