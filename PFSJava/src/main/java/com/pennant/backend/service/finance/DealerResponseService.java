package com.pennant.backend.service.finance;

import com.pennant.backend.model.finance.DealerResponse;

public interface DealerResponseService {

	void save(DealerResponse dealerResponse);

	int getCountByProcessed(long finID, boolean processed);

}
