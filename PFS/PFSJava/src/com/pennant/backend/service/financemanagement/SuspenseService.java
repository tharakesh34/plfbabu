package com.pennant.backend.service.financemanagement;

import java.util.List;

import com.pennant.backend.model.finance.FinanceSuspHead;

public interface SuspenseService {
	
	FinanceSuspHead getFinanceSuspHead();
	FinanceSuspHead getNewFinanceSuspHead();
	void updateSuspense(FinanceSuspHead suspHead);
	FinanceSuspHead refresh(FinanceSuspHead suspHead);
	FinanceSuspHead getFinanceSuspHeadById(String finRef, boolean isEnquiry);
	List<String> getSuspFinanceList();
	
}
