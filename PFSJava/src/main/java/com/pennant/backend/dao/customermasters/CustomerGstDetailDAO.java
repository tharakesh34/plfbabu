package com.pennant.backend.dao.customermasters;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;

public interface CustomerGstDetailDAO {
	// CustomerGST
	void delete(CustomerGST customerGST, String type);

	void deleteCustomerGSTByCustomer(long id, String type);

	long save(CustomerGST customerGST, String type);

	void update(CustomerGST customerGST, String type);

	List<CustomerGST> getCustomerGSTById(long id, String type);

	public CustomerGST getCustomerGstByCustId(CustomerGST customerGST, String type);

	CustomerGST getCustomerGSTByGstNumber(CustomerGST customerGST, String type);

	CustomerGST getCustomerGstByCustId(long id, String type);

	// CustomerGSTDetails
	void delete(CustomerGSTDetails customerGSTDetails, String type);

	void save(CustomerGSTDetails customerGSTDetails, String type);

	List<CustomerGSTDetails> getCustomerGSTDetailsByCustomer(long headerId, String type);

	List<CustomerGSTDetails> getCustomerGSTDetailsByCustomer(List<Long> headerIdList, String type);

	void update(CustomerGSTDetails customerGSTDetails, String type);

	int getVersion(long id);

	List<CustomerGSTDetails> getCustomerGSTDetailsById(long custId, String type);

	int getCustomerGstInfoByCustGstNumber(long id, long custId, String gstNumber, String string);

	void saveCustomerGSTDetailsBatch(List<CustomerGSTDetails> customerGSTDetailsList, String type);

	void delete(long id, String type);

}
