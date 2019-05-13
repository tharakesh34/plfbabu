package com.pennanttech.pff.external;

import com.pennanttech.model.DedupCustomerDetail;
import com.pennanttech.model.DedupCustomerResponse;

public interface CustomerDedupService {
	public DedupCustomerResponse invokeDedup(DedupCustomerDetail dedupCustomerDetail) throws Exception;
}
