package com.pennant.backend.service.cibil;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.cibil.CIBILDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class CIBILServiceImpl implements CIBILService {

	@Autowired
	private CIBILDAO cibildao;

	@Override
	public CustomerDetails getCustomerDetails(String finReference, long customerId) {
		CustomerDetails customer = new CustomerDetails();

		try {
			customer.setCustomer(cibildao.getCustomer(customerId));
			customer.setAddressList(cibildao.getCustomerAddres(customerId));
			customer.setCustomerDocumentsList(cibildao.getCustomerDocuments(customerId));
			customer.setCustomerPhoneNumList(cibildao.getCustomerPhoneNumbers(customerId));
			customer.setCustomerFinance(cibildao.getFinanceSummary(finReference, customerId));
		} catch (Exception e) {
			customer = null;
		}

		return customer;
	}

	@Override
	public long logFileInfo(String fileName, String memberId, String memberName, String memberPwd, String reportPath) {
		return new Long(cibildao.logFileInfo(fileName, memberId, memberName, memberPwd, reportPath));
	}

	@Override
	public void deleteDetails() {
		cibildao.deleteDetails();
	}

	@Override
	public long extractCustomers() throws Exception {
		return new Long(cibildao.extractCustomers());
	}

	@Override
	public void updateFileStatus(long headerid, String status, long totalRecords,long processedRecords,long successCount,long failedCount, String remarks) {
		cibildao.updateFileStatus(headerid, status, totalRecords,processedRecords,successCount,failedCount,remarks);
	}

	@Override
	public void logFileInfoException(long id, String finReference, String reason) {
		cibildao.logFileInfoException(id, finReference, reason);
		
	}

	@Override
	public DataEngineStatus getLatestExecution() {
		return cibildao.getLatestExecution();
	}

}
