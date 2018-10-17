package com.pennant.backend.service.finance;

import com.pennant.backend.model.finance.DemographicDetails;

public interface DemoGraphicDetailsService {

	DemographicDetails getDemoGraphicDetails(String pinCode);
}
