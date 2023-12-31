package com.pennanttech.pff.dao.customer.income;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerIncome;

public interface IncomeDetailDAO {
	long save(CustomerIncome customerIncome, String type);

	void update(CustomerIncome customerIncome, String type);

	void delete(long id, String type);

	List<CustomerIncome> getTotalIncomeByLinkId(long linkId);

	List<CustomerIncome> getTotalIncomeByFinReference(String keyReference);

	List<CustomerIncome> getIncomes(long linkId);

	List<CustomerIncome> getIncomesByCustomer(long custId, String type);

	void deletebyLinkId(long linkId, String type);

	void deletebyIncomeType(long linkId, String incomeType);

	List<CustomerIncome> getTotalIncomeBySamplingId(long id);
}
