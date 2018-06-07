package com.pennant.backend.service.customermasters;

import java.util.List;

import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.reports.AvailPastDue;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CustomerDetailsService {
	CustomerDetails getCustomerById(long id);

	CustomerDetails getApprovedCustomerById(long id);

	CustomerDetails getNewCustomer(boolean createNew, CustomerDetails customerDetails);

	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws InterfaceException; 

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	Customer fetchCustomerDetails(Customer customer);

	Customer fetchCoreCustomerDetails(Customer customer) throws InterfaceException;

	Customer getCustomerForPostings(long custId);

	Customer getCustomerByCIF(String id);

	WIFCustomer getWIFCustomerByCIF(long id);

	Customer getCheckCustomerByCIF(String cif);
	
	String getNewProspectCustomerCIF();

	CustomerStatusCode getCustStatusByMinDueDays();

	CustomerCategory getCustomerCategoryById(String custCtgCode);

	void updateProspectCustomer(Customer customer);

	CustomerDetails getCustomerDetailsbyIdandPhoneType(long id, String phoneType);

	AvailPastDue getCustPastDueDetailByCustId(AvailPastDue pastDue, String limitCcy);

	CustomerDetails setCustomerDetails(CustomerDetails customer);

	DirectorDetail getNewDirectorDetail();

	List<CustomerRating> getCustomerRatingByCustId(long id, String type);

	//Finance Customer Details
	List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType) throws InterfaceException;

	CustomerDetails getCustomerDetailsById(long id, boolean reqChildDetails, String type);

	List<AuditDetail> validate(CustomerDetails customerDetails, long workflowId, String method,
	        String usrLanguage);

	String getEIDNumberById(String eidNumber, String type);

	long getEIDNumberByCustId(String eidNumber, String type);

	WIFCustomer getWIFByEIDNumber(String eidNumber, String type);

	boolean isDuplicateCrcpr(long custId, String custCRCPR);

	void updateProspectCustCIF(String oldCustCIF, String newCustCIF);

	String getCustCoreBankIdByCIF(String custCIF);

	String getNewCoreCustomerCIF();

	void updateCorebankCustCIF(String newCoreCustCIF);

	AuditDetail doCustomerValidations(AuditHeader auditHeader);
	
	int getCustomerCountByCIF(String custCIF, String type);
	
	boolean getCustomerByCoreBankId(String custCoreBank);

	Customer checkCustomerByCIF(String cif, String type);

	int updateCustCRCPR(String custDocTitle,long custID);
	
	Customer getCustomerShrtName(long id);
	
	CustomerDetails getCustomerAndCustomerDocsById(long id, String type);

}
