package com.pennant.backend.service.cibil;

import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceEnquiry;

public interface CIBILService {
	CustomerDetails getCustomerDetails(long customerId);

	Customer getCustomer(long customerId);

	List<CustomerDocument> getCustomerDocuments(long customerId);

	List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId);

	List<CustomerAddres> getCustomerAddres(long customerId);

	List<FinanceEnquiry> getCustomerLoans(long customerId);
	
	void logFileInfo(String fileName, String memberId, String memberName, String memberPwd);
	
	void deleteDetails();
	
	 void extractCustomers() throws Exception;



}
