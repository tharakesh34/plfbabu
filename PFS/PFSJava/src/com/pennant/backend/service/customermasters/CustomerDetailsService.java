package com.pennant.backend.service.customermasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.coreinterface.exception.CustomerNotFoundException;

public interface CustomerDetailsService {
	
	CustomerDetails getCustomerById(long id);
	CustomerDetails getApprovedCustomerById(long id);
	CustomerDetails getCustomer(boolean createNew);
	CustomerDetails getNewCustomer(boolean createNew);
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader) throws CustomerNotFoundException;
	Customer fetchCustomerDetails(Customer customer);
	Customer getCustomerForPostings(long custId);
}
