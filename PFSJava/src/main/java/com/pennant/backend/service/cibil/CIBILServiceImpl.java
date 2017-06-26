package com.pennant.backend.service.cibil;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.cibil.CIBILDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;

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
	public long logFileInfo(String fileName, String memberId, String memberName, String memberPwd) {
		return new Long(cibildao.logFileInfo(fileName, memberId, memberName, memberPwd));
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
	public void updateFileStatus(long headerid, String status) {
		cibildao.updateFileStatus(headerid, status);
	}

}
