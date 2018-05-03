package com.pennant.Interface.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.reports.AvailCustomerDetail;
import com.pennant.coreinterface.model.EquationMasterMissedDetail;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CustomerInterfaceService {
	
	Customer fetchCustomerDetails(Customer customer) throws InterfaceException;
	String generateNewCIF(String operation, Customer customer, String finReference)throws InterfaceException;
	AvailCustomerDetail fetchAvailCustDetails(AvailCustomerDetail detail, BigDecimal newExposure, String ccy) throws InterfaceException;
	List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup customerDedup) throws InterfaceException;
	CustomerDetails getCustomerInfoByInterface(String cif, String string) throws InterfaceException;
	CustomerDetails processCustInformation(InterfaceCustomerDetail interfaceCustomerDetail)
            throws IllegalAccessException, InvocationTargetException;
	List<CustomerDetails> validateMasterFieldDetails(List<CustomerDetails> customerDetails,
            Date dateValueDate);
	List<EquationMasterMissedDetail> getMasterMissedDetails();
	
	String createNewCustomer(CustomerDetails customerDetail) throws InterfaceException;
	
	void updateCoreCustomer(CustomerDetails customerDetails) throws InterfaceException;
	String reserveCIF(Customer customer) throws InterfaceException;
	String releaseCIF(Customer customer, String reserveRefNum) throws InterfaceException;
}
