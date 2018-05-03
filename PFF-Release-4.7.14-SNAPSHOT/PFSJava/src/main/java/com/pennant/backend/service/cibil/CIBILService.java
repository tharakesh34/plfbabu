package com.pennant.backend.service.cibil;

import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;

public interface CIBILService {
	CustomerDetails getCustomerDetails(String finreference, long customerId);

	long logFileInfo(String fileName, String memberId, String memberName, String memberPwd, String reportPath);

	void deleteDetails();

	long extractCustomers() throws Exception;
	
	void updateFileStatus(long headerid, String status,long totalRecords, long processedRecords, long successCount, long failedCount, String remarks);
	
	void logFileInfoException(long id, String finReference, String reason);
	
	DataEngineStatus getLatestExecution();

	EventProperties getEventProperties(String configName, String eventType);
}
