package com.pennanttech.pff.external;

import com.pennant.backend.model.customermasters.CustomerDetails;

public interface DedupService {
	public CustomerDetails invokeDedup(CustomerDetails details);
}
