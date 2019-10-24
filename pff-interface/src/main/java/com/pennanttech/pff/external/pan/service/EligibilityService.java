package com.pennanttech.pff.external.pan.service;

import com.pennant.backend.model.finance.FinanceDetail;

public interface EligibilityService {
	public FinanceDetail getEligibilityDetails(FinanceDetail details);

	public FinanceDetail getEligibilityStatus(FinanceDetail details);

}
