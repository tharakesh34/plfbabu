package com.pennant.backend.service.cibil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.cibil.CIBILDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceEnquiry;

public class CIBILServiceImpl implements CIBILService {
	private CIBILDAO cibildao;

	public void setCibildao(CIBILDAO cibildao) {
		this.cibildao = cibildao;
	}

	@Override
	public Customer getCustomer(long customerId) {
		return cibildao.getCustomer(customerId);
	}

	@Override
	public List<CustomerDocument> getCustomerDocuments(long customerId) {
		return cibildao.getCustomerDocuments(customerId);
	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId) {
		return cibildao.getCustomerPhoneNumbers(customerId);
	}

	@Override
	public List<CustomerAddres> getCustomerAddres(long customerId) {
		return cibildao.getCustomerAddres(customerId);
	}

	@Override
	public List<FinanceEnquiry> getCustomerLoans(long customerId) {
		return cibildao.getCustomerLoans(customerId);
	}

	@Override
	public CustomerDetails getCustomerDetails(long customerId) {
		return cibildao.getCustomerDetails(customerId);
	}

	@Override
	public void logFileInfo(String fileName, String memberId, String memberName, String memberPwd) {
		cibildao.logFileInfo(fileName, memberId, memberName, memberPwd);
	}

	@Override
	public void deleteDetails() {
		cibildao.deleteDetails();
	}

	@Override
	public void extractCustomers() throws Exception {
		cibildao.extractCustomers();
		
	}

}
