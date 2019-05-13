
package com.pennanttech.pff.ucic.service;

import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;

public interface CustomerConsumerService {
	public List<CustomerDedup> getCustomer(Customer customer);
}
