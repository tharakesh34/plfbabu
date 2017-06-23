package com.pennant.backend.service.cibil;

import com.pennant.backend.model.customermasters.CustomerDetails;

public interface CIBILService {
	CustomerDetails getCustomerDetails(String finreference, long customerId);

	void logFileInfo(String fileName, String memberId, String memberName, String memberPwd);

	void deleteDetails();

	void extractCustomers() throws Exception;
}
