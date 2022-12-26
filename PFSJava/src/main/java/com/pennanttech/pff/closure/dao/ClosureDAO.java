package com.pennanttech.pff.closure.dao;

import com.pennant.backend.model.finance.FinReceiptData;

public interface ClosureDAO {
	void saveClosureAmount(FinReceiptData frd);
}
