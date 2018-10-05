package com.pennant.datamigration.service;

import java.util.List;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;


public interface DMTransactionService {
	
	public MigrationData getFinanceDetails(MigrationData srcMD, ReferenceID rid, String type) throws Exception;

	public List<String> getFinanceReferenceList(String type);

	public int updateFinanceDetails(FinScheduleData financeDetails, String type);
	
	public void saveFinanceDetails(MigrationData dMD);
	
}
