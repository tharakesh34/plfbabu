package com.pennant.backend.dao.finance;


import com.pennant.backend.model.financemanagement.FinanceFlag;

public interface FinFlagsHeaderDAO {
	FinanceFlag getFinanceFlags();
	FinanceFlag getNewFinanceFlags();
	FinanceFlag getFinFlagsHeaderByRef(String finReference, String type);
	
	void update(FinanceFlag financeFlag, String type);
	void delete(FinanceFlag financeFlag, String type);
	void save(FinanceFlag financeFlag, String type);

}
