package com.pennant.backend.service.finance;

import com.pennant.backend.model.finance.FinanceMainExt;

public interface FinanceMainExtService {

	void saveFinanceMainExtDetails(FinanceMainExt financeMainExt);

	FinanceMainExt getNstlAccNumber(String finReference, boolean processFlag);
	
	FinanceMainExt getFinanceMainExtByRef(String finReference);

}
