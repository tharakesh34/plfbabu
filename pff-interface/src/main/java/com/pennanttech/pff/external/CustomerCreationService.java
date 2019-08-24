package com.pennanttech.pff.external;

import com.pennant.backend.model.customermasters.CustomerDetails;

public interface CustomerCreationService {
	public CustomerDetails invokeCutomerCreation(CustomerDetails detail) throws Exception;
}
