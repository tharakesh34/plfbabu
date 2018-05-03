package com.pennant.backend.dao.custdedup;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerDedup;

public interface CustomerDedupDAO {

	void saveList(List<CustomerDedup> insertList,String type);

	void updateList(List<CustomerDedup> updateList);

	List<CustomerDedup> fetchOverrideCustDedupData(String finReference, String queryCode,String module);

	List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup customerDedup, String sqlQuery);

	void moveData(String finReference, String type);
}
