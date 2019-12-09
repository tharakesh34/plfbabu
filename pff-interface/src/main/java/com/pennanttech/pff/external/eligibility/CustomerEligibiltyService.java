package com.pennanttech.pff.external.eligibility;

import com.pennant.backend.model.finance.FinanceDetail;

public interface CustomerEligibiltyService {

	FinanceDetail checkCustomerEligility(FinanceDetail financeDetail);

}
