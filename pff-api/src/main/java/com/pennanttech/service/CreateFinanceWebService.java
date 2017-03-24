package com.pennanttech.service;

import com.pennant.backend.model.finance.FinanceDetail;

public interface CreateFinanceWebService {

	public FinanceDetail doCreateFinance(FinanceDetail financeDetail);
	
	public FinanceDetail getFinanceDetails(String finreference);
	
	public FinanceDetail createFinanceWithWIF(FinanceDetail financeDetail);
}
