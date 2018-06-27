package com.pennanttech.pff.dao.customer.income;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerIncome;

public interface IncomeDetailDAO {
	long save(CustomerIncome customerIncome, String type);

	void update(CustomerIncome customerIncome, String type);

	void delete(long id, String type);

	CustomerIncome getCustomerIncomeById(CustomerIncome customerIncome, String type, String source);

	BigDecimal getTotalIncomeByLinkId(long linkId);

	BigDecimal getTotalIncomeByFinReference(String keyReference);

	List<CustomerIncome> getIncomes(long linkId);

	List<CustomerIncome> getIncomesByCustomer(long custId, String type);

}
