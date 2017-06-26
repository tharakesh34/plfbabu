package com.pennant.backend.service.cibil;

import com.pennant.backend.model.customermasters.CustomerDetails;

public interface CIBILService {
	CustomerDetails getCustomerDetails(String finreference, long customerId);

	long logFileInfo(String fileName, String memberId, String memberName, String memberPwd);

	void deleteDetails();

	long extractCustomers() throws Exception;
	
	void updateFileStatus(long headerid, String status);
}
