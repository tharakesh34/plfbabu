package com.pennanttech.pff.external;

import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;

public interface DominValidationService {
	void validateDomain(CustomerDetails customerDetails, String loginId);

	void validateDomain(FinanceDetail detail, String loginId);
}
