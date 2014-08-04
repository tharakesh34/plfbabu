package com.pennant.backend.service.customermasters;

import java.util.List;

import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.reports.AvailPastDue;
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
	Customer fetchCoreCustomerDetails(Customer customer)throws CustomerNotFoundException;
	Customer getCustomerForPostings(long custId);
	Customer getCustomerByCIF(String id);
	Customer getCheckCustomerByCIF(String cif);
	String getNewProspectCustomerCIF();
	CustomerStatusCode getCustStatusByMinDueDays();
	CustomerCategory getCustomerCategoryById(String custCtgCode);
	void updateProspectCustomer(Customer customer);
	CustomerDetails getCustomerDetailsbyIdandPhoneType(long id, String phoneType);
	AvailPastDue getCustPastDueDetailByCustId(AvailPastDue pastDue, String limitCcy);
	CustomerDetails setCustomerDetails(CustomerDetails customer);
	DirectorDetail getNewDirectorDetail() ;
	List<CustomerRating> getCustomerRatingByCustId(long id, String type);
}
