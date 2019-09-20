package com.pennanttech.pff.external;

import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;

public interface DomainCheckService {
	void validateDomain(CustomerDetails customerDetails);

	void validateDomain(FinanceDetail detail);
}
