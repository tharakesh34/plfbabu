package com.pennant.backend.service.limitservice;

import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitDetails;

public interface LimitService {
	List<LimitDetails> getCustomerLimitdetails(FinanceMain financeMain);
}
