package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.FinanceMainExt;

public interface FinanceMainExtDAO {

	FinanceMainExt getFinanceMainExtByRef(String finReference);

	void save(FinanceMainExt financeMainExt);

	void update(FinanceMainExt financeMainExt);
	
	String getRepayIBAN(String finReference);

	FinanceMainExt getNstlAccNumber(String finReference, boolean processFlag);

}
