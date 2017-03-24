package com.pennant.interfaces.dao;

import java.util.List;

import com.pennant.interfaces.model.FetchCustomerFinancesResponse;
import com.pennant.interfaces.model.FetchFinanceScheduleResponse;
import com.pennant.interfaces.model.FetchFinanceDetailsResponse;

public interface FinanceMainDAO {

	List<FetchCustomerFinancesResponse> getCustomerFinanceList(String customerNo);
	FetchFinanceDetailsResponse getFinanceDetails(String financeRef);
	List<FetchFinanceScheduleResponse> getFinanceScheduleDetails(String financeRef);
}
