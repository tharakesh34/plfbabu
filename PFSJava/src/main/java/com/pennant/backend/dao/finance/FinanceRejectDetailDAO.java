package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.FinanceMain;

public interface FinanceRejectDetailDAO {

	void saveFinanceRejectedDetailsLog(FinanceMain financeMain);
}
