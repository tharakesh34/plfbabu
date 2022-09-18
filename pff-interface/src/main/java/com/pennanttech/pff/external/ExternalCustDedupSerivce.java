package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerDedup;

public interface ExternalCustDedupSerivce {

	List<CustomerDedup> getExternarCustomerDedupDetails(CustomerDedup customerDedup);

	CustomerDedup generateNewUCIC(CustomerDedup custDedup, String ucicType);

	List<CustomerDedup> reTrigger(CustomerDedup custDedup);

}
