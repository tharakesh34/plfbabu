package com.pennant.backend.dao.cibil;

import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;

public interface CIBILDAO {

	CustomerDetails getCustomerDetails(long customerId);

	Customer getCustomer(long customerId);

	List<CustomerDocument> getCustomerDocuments(long customerId);

	List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId);

	List<CustomerAddres> getCustomerAddres(long customerId);

	FinanceEnquiry getFinanceSummary(String financeReference, long customerId);

	long logFileInfo(String fileName, String memberId, String memberName, String memberPwd, String reportPath);

	void deleteDetails();

	int extractCustomers() throws Exception;

	void updateFileStatus(long headerid, String status, long totalRecords, long processedRecords, long successCount,
			long failedCount, String remarks);

	void logFileInfoException(long id, String finReference, String reason);

	DataEngineStatus getLatestExecution();

	EventProperties getEventProperties(String configName, String eventType);

	List<CustomerEMail> getCustomerEmails(long customerId);

}
