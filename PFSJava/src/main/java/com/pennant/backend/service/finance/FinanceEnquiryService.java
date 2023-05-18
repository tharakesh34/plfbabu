package com.pennant.backend.service.finance;

import com.pennant.backend.model.finance.FinanceDetail;

public interface FinanceEnquiryService {
	public FinanceDetail getLoanDetails(long finID);
}
