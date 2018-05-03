package com.pennant.datamigration.service;

import java.util.List;

import com.pennant.backend.model.finance.FinScheduleData;


public interface DMTransactionService {
	
	public FinScheduleData getFinanceDetails(String finReference, String type);

	public List<String> getFinanceReferenceList();

	public int updateFinanceDetails(FinScheduleData financeDetails, String type);
	
}
