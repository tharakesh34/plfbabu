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
import com.pennant.exception.PFFInterfaceException;

public interface CustomerInterfaceService {
	
	Customer fetchCustomerDetails(Customer customer) throws PFFInterfaceException;
	String generateNewCIF(String operation, Customer customer, String finReference)throws PFFInterfaceException;
	AvailCustomerDetail fetchAvailCustDetails(AvailCustomerDetail detail, BigDecimal newExposure, String ccy) throws PFFInterfaceException;
	List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup customerDedup) throws PFFInterfaceException;
	CustomerDetails getCustomerInfoByInterface(String cif, String string) throws PFFInterfaceException;
	CustomerDetails processCustInformation(InterfaceCustomerDetail interfaceCustomerDetail)
            throws IllegalAccessException, InvocationTargetException;
	List<CustomerDetails> validateMasterFieldDetails(List<CustomerDetails> customerDetails,
            Date dateValueDate);
	List<EquationMasterMissedDetail> getMasterMissedDetails();
	
	String createNewCustomer(CustomerDetails customerDetail) throws PFFInterfaceException;
	
	void updateCoreCustomer(CustomerDetails customerDetails) throws PFFInterfaceException;
	String reserveCIF(Customer customer) throws PFFInterfaceException;
	String releaseCIF(Customer customer, String reserveRefNum) throws PFFInterfaceException;
}
