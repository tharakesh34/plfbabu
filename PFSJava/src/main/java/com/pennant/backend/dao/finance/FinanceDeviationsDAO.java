package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceDeviations;

public interface FinanceDeviationsDAO {

	long save(FinanceDeviations financeDeviations, String type);
	void update(FinanceDeviations financeDeviations, String type);
	List<FinanceDeviations> getFinanceDeviations(String finReference,String type);
	FinanceDeviations getFinanceDeviationsByID(String finReference, String module,String deviationCode, String type);
	void delete(FinanceDeviations financeDeviations, String type);
	void deleteCheckListRef(String finReference, String module, String devCode, String type);
	List<FinanceDeviations> getFinanceDeviations(String finReference, boolean deviProcessed, String type);
	void updateDeviProcessed(String finReference, String type);
	void deleteById(FinanceDeviations financeDeviations, String type);

	/**
	 * This method updates the Record financeDeviations.
	 * 
	 * @param deviationId
	 * @return void
	 * 
	 */
	//### 05-05-2018 story #361(tuleap server) Manual Deviations
	void updateMarkDeleted(long deviationId, String finReference);

}
